#!/bin/bash

# Script para fazer backup do banco de dados PostgreSQL
# Uso: ./backup-database.sh [nome_arquivo]

BACKUP_DIR="backups"
CONTAINER_NAME="clube-quinze-postgres"
DB_NAME="clube_quinze"
DB_USER="postgres"

# Criar diretório de backups se não existir
mkdir -p "$BACKUP_DIR"

# Gerar nome do arquivo com timestamp se não fornecido
if [ -z "$1" ]; then
    BACKUP_FILE="$BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).sql"
else
    BACKUP_FILE="$BACKUP_DIR/$1"
fi

echo "=== Fazendo backup do banco de dados ==="
echo "Container: $CONTAINER_NAME"
echo "Database: $DB_NAME"
echo "Arquivo: $BACKUP_FILE"
echo ""

# Verificar se container está rodando
if ! docker ps | grep -q "$CONTAINER_NAME"; then
    echo "[ERRO] Container $CONTAINER_NAME não está rodando!"
    exit 1
fi

# Fazer backup
echo "[INFO] Iniciando backup..."
docker exec "$CONTAINER_NAME" pg_dump -U "$DB_USER" "$DB_NAME" > "$BACKUP_FILE"

if [ $? -ne 0 ]; then
    echo "[ERRO] Falha ao criar backup!"
    exit 1
fi

echo "[OK] Backup criado com sucesso: $BACKUP_FILE"
echo ""

# Mostrar tamanho do arquivo
ls -lh "$BACKUP_FILE"

echo ""
echo "=== Para restaurar este backup: ==="
echo "./restore-database.sh \"$BACKUP_FILE\""

# Criar também um backup compactado
gzip -c "$BACKUP_FILE" > "$BACKUP_FILE.gz"
echo ""
echo "[OK] Backup compactado criado: $BACKUP_FILE.gz"
ls -lh "$BACKUP_FILE.gz"

