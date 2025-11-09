# Como Resolver o Problema do Database na VPS

## Problema
O erro `FATAL: database "clube_quinze" does not exist` ocorre quando o volume do PostgreSQL já existe mas o banco de dados não foi criado.

## Solução Rápida (VPS)

### Opção 1: Criar o banco manualmente no container existente
```bash
# Conectar ao container do postgres
docker exec -it clube-quinze-postgres psql -U postgres

# Dentro do psql, criar o banco:
CREATE DATABASE clube_quinze;

# Sair com: \q
```

### Opção 2: Recriar o volume (CUIDADO: apaga dados)
```bash
# Parar os containers
docker compose down

# Remover o volume (ISSO APAGARÁ TODOS OS DADOS)
docker volume rm clubequinzeapi_postgres_data

# Subir novamente (irá criar tudo do zero)
docker compose up -d
```

### Opção 3: Executar o script de inicialização manualmente
```bash
# Executar o script dentro do container
docker exec -it clube-quinze-postgres bash -c "psql -U postgres -d postgres -c \"CREATE DATABASE clube_quinze;\""
```

## Verificar se o banco existe
```bash
docker exec -it clube-quinze-postgres psql -U postgres -l
```

## Verificar os logs
```bash
docker compose logs -f
```

## Para o futuro
O arquivo `scripts/init-db.sh` agora é montado no container e garante que o banco existe, mas ele só roda na primeira inicialização do PostgreSQL. Se o volume já existe, ele não roda automaticamente.

