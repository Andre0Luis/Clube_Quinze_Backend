#!/bin/bash

# Script para restaurar backup do banco de dados PostgreSQL
# Uso: ./restore-database.sh <arquivo_backup.sql>

CONTAINER_NAME="clube-quinze-postgres"
DB_NAME="clube_quinze"
DB_USER="postgres"

if [ -z "$1" ]; then
    echo "[ERRO] Especifique o arquivo de backup!"
    echo "Uso: ./restore-database.sh <arquivo_backup.sql>"
    echo ""
    echo "Backups disponíveis:"
    ls -lh backups/*.sql 2>/dev/null || echo "Nenhum backup encontrado"
    exit 1
fi

BACKUP_FILE="$1"

# Se o arquivo termina com .gz, descompactar primeiro
if [[ "$BACKUP_FILE" == *.gz ]]; then
    echo "[INFO] Descompactando backup..."
    gunzip -c "$BACKUP_FILE" > /tmp/restore_temp.sql
    BACKUP_FILE="/tmp/restore_temp.sql"
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "[ERRO] Arquivo não encontrado: $BACKUP_FILE"
    exit 1
fi

echo "=== Restaurando backup do banco de dados ==="
echo "Container: $CONTAINER_NAME"
echo "Database: $DB_NAME"
echo "Arquivo: $BACKUP_FILE"
echo ""
echo "[AVISO] Isso irá SUBSTITUIR todos os dados atuais do banco!"
read -p "Deseja continuar? (S/N): " CONFIRM

if [[ ! "$CONFIRM" =~ ^[Ss]$ ]]; then
    echo "Operação cancelada."
    exit 0
fi

# Verificar se container está rodando
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo "[ERRO] Container $CONTAINER_NAME não está rodando!"
    exit 1
fi

echo ""
echo "[INFO] Parando aplicação..."
docker compose stop api

echo "[INFO] Dropando banco existente..."
docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -c "DROP DATABASE IF EXISTS $DB_NAME;"

echo "[INFO] Criando banco limpo..."
docker exec "$CONTAINER_NAME" psql -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;"

echo "[INFO] Restaurando backup..."
docker exec -i "$CONTAINER_NAME" psql -U "$DB_USER" -d "$DB_NAME" < "$BACKUP_FILE"

if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao restaurar backup!"
    exit 1
fi

echo "[OK] Backup restaurado com sucesso!"
echo ""
echo "[INFO] Reiniciando aplicação..."
docker compose start api

echo ""
echo "[OK] Processo concluído!"

# Limpar arquivo temporário se foi descompactado
if [ -f "/tmp/restore_temp.sql" ]; then
    rm /tmp/restore_temp.sql
fi

