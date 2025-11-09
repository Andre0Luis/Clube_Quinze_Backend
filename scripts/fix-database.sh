#!/bin/bash

# Script para criar o banco de dados clube_quinze em um container existente
# Uso: ./fix-database.sh

echo "=== Verificando se o container do PostgreSQL está rodando ==="
if ! docker ps | grep -q clube-quinze-postgres; then
    echo "❌ Container clube-quinze-postgres não está rodando!"
    echo "Execute: docker compose up -d postgres"
    exit 1
fi

echo "✅ Container encontrado!"
echo ""

echo "=== Verificando se o banco clube_quinze existe ==="
DB_EXISTS=$(docker exec clube-quinze-postgres psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='clube_quinze'")

if [ "$DB_EXISTS" = "1" ]; then
    echo "✅ O banco de dados clube_quinze já existe!"
    echo ""
    echo "=== Listando todos os bancos ==="
    docker exec clube-quinze-postgres psql -U postgres -c "\l"
else
    echo "⚠️  Banco de dados clube_quinze não existe. Criando..."
    docker exec clube-quinze-postgres psql -U postgres -c "CREATE DATABASE clube_quinze;"

    if [ $? -eq 0 ]; then
        echo "✅ Banco de dados clube_quinze criado com sucesso!"
        echo ""
        echo "=== Listando todos os bancos ==="
        docker exec clube-quinze-postgres psql -U postgres -c "\l"
    else
        echo "❌ Erro ao criar o banco de dados!"
        exit 1
    fi
fi

echo ""
echo "=== Testando conexão com o banco clube_quinze ==="
docker exec clube-quinze-postgres psql -U postgres -d clube_quinze -c "SELECT version();"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Tudo pronto! O banco clube_quinze está funcionando."
    echo "Agora você pode reiniciar a aplicação:"
    echo "  docker compose restart api"
else
    echo ""
    echo "❌ Não foi possível conectar ao banco clube_quinze"
    exit 1
fi

