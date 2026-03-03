up:
	docker compose up -d

up-all:
	docker compose up -d

down:
	docker compose down -v

down-all:
	docker compose down -v --rmi all

logs:
	docker compose logs -f

logs-all:
	docker compose logs -f

build:
	docker compose build --no-cache

build-all:
	docker compose build --no-cache

test:
	docker compose ps

clean:
	docker compose down -v --rmi all

restart:
	docker compose restart

up-auth:
	docker compose up -d wishly-auth-service

down-auth:
	docker compose stop wishly-auth-service

logs-auth:
	docker compose logs -f wishly-auth-service

build-auth:
	docker compose build --no-cache wishly-auth-service

restart-auth:
	docker compose restart wishly-auth-service