#!/bin/bash
set -e

# Script para garantir que o banco de dados existe
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    -- Criar o banco de dados se não existir
    SELECT 'CREATE DATABASE clube_quinze'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'clube_quinze')\gexec
EOSQL

echo "Database clube_quinze está pronto!"

