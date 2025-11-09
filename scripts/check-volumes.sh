#!/bin/bash

echo "=== Verificando volumes do Docker ==="
echo ""

echo "[INFO] Listando todos os volumes:"
docker volume ls | grep clube

echo ""
echo "[INFO] Detalhes do volume do PostgreSQL:"
docker volume inspect clubequinzeapi_postgres_data 2>/dev/null || \
docker volume inspect clube-quinze-api_postgres_data 2>/dev/null || \
echo "Volume não encontrado. Lista de volumes disponíveis:" && docker volume ls

echo ""
echo "[INFO] Espaço usado pelo volume:"
docker system df -v | grep postgres

echo ""
echo "=== Para fazer backup do banco ==="
echo "docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze > backup_\$(date +%Y%m%d_%H%M%S).sql"
echo ""
echo "=== Para restaurar backup ==="
echo "docker exec -i clube-quinze-postgres psql -U postgres clube_quinze < backup.sql"
echo ""
echo "=== Localização física do volume ==="
docker volume inspect clubequinzeapi_postgres_data --format '{{ .Mountpoint }}' 2>/dev/null || \
docker volume inspect clube-quinze-api_postgres_data --format '{{ .Mountpoint }}' 2>/dev/null

