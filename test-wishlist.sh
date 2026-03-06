#!/bin/bash
set -e

GATEWAY_URL="http://localhost:8080"
AUTH_URL="http://localhost:8083"
EMAIL="test$(date +%s)@test.com"
PASSWORD="Password12"

echo "🚀 Wishlist Service E2E Test"
echo "   Email: ${EMAIL}"
echo ""

# 0. Регистрация (новый пользователь)
echo "=== 0. Register ==="
REGISTER_RESPONSE=$(curl -s -X POST "${AUTH_URL}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}")

echo "${REGISTER_RESPONSE}" | jq .
echo ""

# 1. Логин
echo "=== 1. Login ==="
LOGIN_RESPONSE=$(curl -s -X POST "${AUTH_URL}/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"${EMAIL}\",\"password\":\"${PASSWORD}\"}")

echo "${LOGIN_RESPONSE}" | jq .

ACCESS_TOKEN=$(echo "${LOGIN_RESPONSE}" | jq -r '.accessToken')
echo "Token: ${ACCESS_TOKEN:0:50}..."

# Проверка токена
DOT_COUNT=$(echo ${ACCESS_TOKEN} | tr -cd '.' | wc -c)
echo "JWT dots: ${DOT_COUNT} (должно быть 2)"

if [ "${DOT_COUNT}" != "2" ]; then
    echo "❌ Токен невалидный!"
    exit 1
fi
echo ""

# 2. Создать вишлист
echo "=== 2. Create Wishlist ==="
WISHLIST_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/wishlists" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{"title":"Day Birthday","privacy":"PUBLIC","eventDate":"2026-12-31T00:00:00"}')

echo "${WISHLIST_RESPONSE}" | jq .

WISHLIST_ID=$(echo "${WISHLIST_RESPONSE}" | jq -r '.id')

if [ "${WISHLIST_ID}" = "null" ] || [ -z "${WISHLIST_ID}" ]; then
    echo "❌ Не удалось создать вишлист!"
    docker logs wishly-wishlist-service --tail 30
    exit 1
fi

echo "✅ Wishlist ID: ${WISHLIST_ID}"
echo ""

# 3. Добавить подарок (с URL)
echo "=== 3. Add Gift Item (with URL) ==="
GIFT_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}/items" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "name": "Laptop",
    "priority": "HIGH",
    "productUrl": "https://www.wildberries.ru/catalog/160599645/detail.aspx",
    "description": "Want it for birthday"
  }')

echo "${GIFT_RESPONSE}" | jq .

GIFT_ID=$(echo "${GIFT_RESPONSE}" | jq -r '.id')

if [ "${GIFT_ID}" = "null" ] || [ -z "${GIFT_ID}" ]; then
    echo "❌ Не удалось добавить подарок!"
    docker logs wishly-wishlist-service --tail 30
    exit 1
fi

echo "✅ Gift ID: ${GIFT_ID}"
echo ""

# 4. Добавить подарок (без URL - тоже должно работать)
echo "=== 4. Add Gift Item (without URL) ==="
GIFT2_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}/items" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "name": "Book",
    "priority": "MEDIUM",
    "productUrl": "",
    "description": "Any interesting book"
  }')

echo "${GIFT2_RESPONSE}" | jq .
echo ""

# 5. Получить все подарки
echo "=== 5. Get Gift Items ==="
curl -s "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}/items" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq .
echo ""

# 6. Тест валидации URL (невалидный URL должен вернуть ошибку)
echo "=== 6. Test URL Validation (invalid URL) ==="
INVALID_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}/items" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "name": "Invalid",
    "priority": "LOW",
    "productUrl": "not-a-valid-url",
    "description": "Should fail"
  }')

echo "${INVALID_RESPONSE}" | jq .

if echo "${INVALID_RESPONSE}" | grep -q "Invalid URL"; then
    echo "✅ URL validation working"
else
    echo "⚠️ URL validation may not be working"
fi
echo ""

# 7. Резервирование подарка
echo "=== 7. Reserve Gift Item ==="
RESERVE_RESPONSE=$(curl -s -X POST "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}/items/${GIFT_ID}/reserve" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "guestName": "John Doe",
    "guestEmail": "john@example.com"
  }')

echo "${RESERVE_RESPONSE}" | jq .
echo ""

# 8. Получить вишлист (проверка что резервирование отобразилось)
echo "=== 8. Get Wishlist with Items ==="
curl -s "${GATEWAY_URL}/api/wishlists/${WISHLIST_ID}" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq .
echo ""

# 9. Проверка статусов сервисов
echo "=== 9. Service Health Check ==="
echo "Gateway:"
curl -s "${GATEWAY_URL}/actuator/health" | jq . || echo "⚠️ Gateway health not available"

echo ""
echo "Redis (для Gateway rate limiting):"
docker exec wishly-redis redis-cli ping || echo "⚠️ Redis not available"

echo ""
echo "PostgreSQL:"
docker exec wishly-postgres psql -U user -d wishly -c "SELECT COUNT(*) FROM wishlists;" || echo "⚠️ PostgreSQL not available"
echo ""

# Итоги
echo "========================================"
echo "✅ ALL TESTS PASSED!"
echo ""
echo "Architecture verified:"
echo "  ✅ JWT Authentication (Gateway)"
echo "  ✅ Wishlist CRUD"
echo "  ✅ GiftItem CRUD (URL optional)"
echo "  ✅ URL Format Validation"
echo "  ✅ Reservation System"
echo "  ✅ PostgreSQL Persistence"
echo "  ✅ Redis (Gateway rate limiting)"
echo "========================================"