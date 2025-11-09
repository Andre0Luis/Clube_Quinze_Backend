# Setup e Troubleshooting - Clube Quinze API

## 🚀 Setup Rápido

### Pré-requisitos
- Docker e Docker Compose
- Java 21 (para desenvolvimento local)
- Maven (para desenvolvimento local)

### Executar com Docker (Recomendado)

```bash
# Subir a aplicação e o banco de dados
docker compose up -d

# Ver logs
docker compose logs -f

# Ver logs apenas da aplicação
docker compose logs -f api

# Ver logs apenas do banco
docker compose logs -f postgres

# Parar
docker compose down

# Parar e remover volumes (CUIDADO: apaga dados)
docker compose down -v
```

A aplicação estará disponível em: http://localhost:8080  
Swagger UI: http://localhost:8080/swagger-ui.html

### Executar localmente (sem Docker)

1. Certifique-se de ter um PostgreSQL rodando
2. Configure as variáveis de ambiente ou edite `application.properties`
3. Execute:

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

## 🔧 Troubleshooting

### ❌ Erro: "database clube_quinze does not exist"

Este erro ocorre quando o volume do PostgreSQL já existe mas o banco de dados não foi criado.

#### Solução Rápida (Linux/VPS)
```bash
chmod +x scripts/fix-database.sh
./scripts/fix-database.sh
```

#### Solução Rápida (Windows)
```cmd
scripts\fix-database.bat
```

#### Solução Manual
```bash
# Criar o banco manualmente
docker exec -it clube-quinze-postgres psql -U postgres -c "CREATE DATABASE clube_quinze;"

# Reiniciar a aplicação
docker compose restart api
```

#### Solução Definitiva (remove todos os dados)
```bash
# Parar containers
docker compose down

# Remover volume do banco (ISSO APAGARÁ TODOS OS DADOS!)
docker volume rm clubequinzeapi_postgres_data

# Subir novamente (criará tudo do zero)
docker compose up -d
```

### ❌ Erro: "Illegal base64 character" no JWT

Este erro ocorre quando o `JWT_SECRET` no arquivo `.env` não está no formato correto.

#### Solução
Certifique-se de que o `JWT_SECRET` seja uma string com pelo menos 32 caracteres ou uma string base64 válida.

Exemplo de `.env`:
```properties
JWT_SECRET=minha-chave-super-secreta-com-pelo-menos-32-caracteres
JWT_ACCESS_TOKEN_TTL=PT15M
JWT_REFRESH_TOKEN_TTL=PT7D
```

### ❌ Aplicação não conecta no banco

Verifique se:
1. O container do PostgreSQL está rodando: `docker ps`
2. O banco existe: `docker exec -it clube-quinze-postgres psql -U postgres -l`
3. As credenciais estão corretas no `.env` ou `application.properties`

### 🔍 Verificar logs de erro

```bash
# Ver últimas 100 linhas dos logs
docker compose logs --tail=100 api

# Seguir logs em tempo real
docker compose logs -f api

# Ver logs do banco
docker compose logs -f postgres
```

### 🧹 Limpar tudo e começar do zero

```bash
# Parar e remover containers, networks e volumes
docker compose down -v

# Remover imagens antigas (opcional)
docker rmi clube-quinze-api

# Rebuildar e subir
docker compose up --build -d
```

## 📦 Deploy na VPS

### Primeira vez

1. Clone o repositório
2. Crie o arquivo `.env` com as variáveis de ambiente
3. Dê permissão aos scripts:
```bash
chmod +x scripts/*.sh
```
4. Suba os containers:
```bash
docker compose up -d
```

### Atualizar aplicação

```bash
# Baixar mudanças
git pull

# Rebuild e restart
docker compose up --build -d

# Verificar logs
docker compose logs -f api
```

### Backup do banco de dados

```bash
# Criar backup
docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurar backup
docker exec -i clube-quinze-postgres psql -U postgres clube_quinze < backup_20250109_120000.sql
```

## 🔐 Variáveis de Ambiente Requeridas

Crie um arquivo `.env` na raiz do projeto:

```properties
# Database
DATABASE_URL=jdbc:postgresql://postgres:5432/clube_quinze
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
DATABASE_HOST=postgres
DATABASE_PORT=5432

# JWT
JWT_SECRET=sua-chave-secreta-com-pelo-menos-32-caracteres
JWT_ACCESS_TOKEN_TTL=PT15M
JWT_REFRESH_TOKEN_TTL=PT7D

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

## 📝 Comandos Úteis

```bash
# Entrar no container da aplicação
docker exec -it clube-quinze-api sh

# Entrar no container do PostgreSQL
docker exec -it clube-quinze-postgres psql -U postgres -d clube_quinze

# Ver recursos usados pelos containers
docker stats

# Reiniciar apenas um serviço
docker compose restart api

# Ver portas expostas
docker compose ps
```

## 🆘 Ainda com problemas?

1. Verifique se todas as portas necessárias estão livres (5432, 8080)
2. Verifique se o Docker está rodando: `docker --version`
3. Verifique se o Docker Compose está instalado: `docker compose version`
4. Leia os logs completos: `docker compose logs`
5. Consulte o [TROUBLESHOOTING.md](TROUBLESHOOTING.md) para mais detalhes

