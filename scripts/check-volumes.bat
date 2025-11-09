@echo off
echo === Verificando volumes do Docker ===
echo.

echo [INFO] Listando todos os volumes:
docker volume ls | findstr clube

echo.
echo [INFO] Detalhes do volume do PostgreSQL:
docker volume inspect clubequinzeapi_postgres_data

echo.
echo [INFO] Espaco usado pelo volume:
docker system df -v | findstr postgres

echo.
echo === Para fazer backup do banco ===
echo docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze ^> backup.sql
echo.
echo === Para restaurar backup ===
echo docker exec -i clube-quinze-postgres psql -U postgres clube_quinze ^< backup.sql

