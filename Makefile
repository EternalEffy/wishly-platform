up:
	docker compose up -d

up-auth:
	docker compose up -d auth-service

down:
	docker compose down -v

logs:
	docker compose logs -f

build:
	docker compose build --no-cache

build-auth:
	docker compose build --no-cache auth-service

test:
	docker compose ps

clean:
	docker compose down -v --rmi all