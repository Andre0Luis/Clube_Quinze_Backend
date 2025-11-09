# 🔧 Solução para "database clube_quinze does not exist" na VPS

## 🎯 Solução Rápida (Copie e Cole)

Execute este comando na sua VPS:

```bash
docker exec -it clube-quinze-postgres psql -U postgres -c "CREATE DATABASE clube_quinze;" && docker compose restart api
```

**OU use o script automatizado:**

```bash
chmod +x scripts/fix-database.sh
./scripts/fix-database.sh
```

---

## 🔍 Por que isso aconteceu?

O PostgreSQL detecta dados existentes no volume e **pula a inicialização automática**. Isso significa que:

1. ✅ O container do PostgreSQL está rodando
2. ✅ O volume com dados anteriores existe
3. ❌ Mas o banco `clube_quinze` não foi criado nesse volume

Isso geralmente acontece quando:
- Você migra volumes entre ambientes
- Você cria um volume manualmente
- O script de inicialização não roda (só roda na primeira vez)

---

## ✅ Soluções (escolha uma)

### Opção 1: Script Automatizado (RECOMENDADO)

```bash
# Dar permissão
chmod +x scripts/fix-database.sh

# Executar
./scripts/fix-database.sh
```

O script vai:
- ✓ Verificar se o container está rodando
- ✓ Verificar se o banco existe
- ✓ Criar o banco se não existir
- ✓ Testar a conexão
- ✓ Sugerir reiniciar a aplicação

### Opção 2: Comando Manual Rápido

```bash
# Criar o banco
docker exec -it clube-quinze-postgres psql -U postgres -c "CREATE DATABASE clube_quinze;"

# Reiniciar aplicação
docker compose restart api
```

### Opção 3: Via psql Interativo

```bash
# Entrar no psql
docker exec -it clube-quinze-postgres psql -U postgres

# Dentro do psql, executar:
CREATE DATABASE clube_quinze;

# Sair
\q

# Reiniciar aplicação
docker compose restart api
```

### Opção 4: Recriar Tudo (APAGA DADOS!)

⚠️ **ATENÇÃO: Isso apagará todos os dados!**

```bash
# Parar containers
docker compose down

# Remover volume (APAGA DADOS!)
docker volume rm clubequinzeapi_postgres_data

# Subir novamente (cria tudo do zero)
docker compose up -d
```

---

## 🔍 Verificar se Funcionou

### 1. Listar bancos de dados:
```bash
docker exec clube-quinze-postgres psql -U postgres -l
```

Você deve ver `clube_quinze` na lista.

### 2. Conectar ao banco:
```bash
docker exec -it clube-quinze-postgres psql -U postgres -d clube_quinze
```

Se conectar sem erro, está funcionando!

### 3. Ver logs da aplicação:
```bash
docker compose logs -f api
```

Não deve mais aparecer erro de "database does not exist".

---

## 📝 Para Prevenir no Futuro

### 1. Arquivos Atualizados

Os seguintes arquivos foram atualizados para prevenir isso:

**compose.yaml e docker-compose.yml:**
```yaml
volumes:
  - ./scripts/init-db.sh:/docker-entrypoint-initdb.d/init-db.sh
```

**scripts/init-db.sh:**
Script que sempre verifica e cria o banco se não existir.

### 2. Use os Scripts

Os scripts agora lidam com isso automaticamente:
- `fix-database.sh` - Corrige banco inexistente
- `check-volumes.sh` - Verifica status dos volumes
- `backup-database.sh` - Backup antes de mudanças

### 3. Backup Antes de Mudanças

```bash
# Sempre antes de mudanças importantes
./scripts/backup-database.sh
```

---

## 🚀 Comandos Úteis para VPS

### Verificar Status:
```bash
# Status dos containers
docker compose ps

# Logs da aplicação
docker compose logs -f api

# Logs do banco
docker compose logs -f postgres

# Ver últimas 50 linhas
docker compose logs --tail=50 api
```

### Gerenciar Containers:
```bash
# Reiniciar tudo
docker compose restart

# Reiniciar apenas aplicação
docker compose restart api

# Parar tudo
docker compose stop

# Iniciar tudo
docker compose start

# Rebuild e restart
docker compose up --build -d
```

### Verificar Banco:
```bash
# Listar bancos
docker exec clube-quinze-postgres psql -U postgres -l

# Conectar ao banco
docker exec -it clube-quinze-postgres psql -U postgres -d clube_quinze

# Dentro do psql:
\dt              # Listar tabelas
\d usuarios      # Ver estrutura da tabela
SELECT COUNT(*) FROM usuarios;  # Contar registros
\q               # Sair
```

---

## 🎓 Entendendo o Problema

### O que são volumes Docker?

Volumes são espaços de armazenamento persistentes gerenciados pelo Docker:

```
Container PostgreSQL
    ↓
Volume (postgres_data)
    ↓
Disco da VPS (/var/lib/docker/volumes/)
```

### Por que o banco não foi criado?

1. PostgreSQL inicia
2. Verifica `/var/lib/postgresql/data` (montado do volume)
3. Encontra dados existentes
4. **Pula inicialização automática** (acha que já está configurado)
5. Mas o banco `clube_quinze` não existe nesse volume

### A solução:

Criar o banco manualmente já que o PostgreSQL pulou a inicialização.

---

## 📚 Documentação Relacionada

- **[DATA-PERSISTENCE.md](DATA-PERSISTENCE.md)** - Como os dados são persistidos
- **[SCRIPTS.md](SCRIPTS.md)** - Documentação de todos os scripts
- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - Outros problemas comuns
- **[QUICK-START.md](QUICK-START.md)** - Resumo rápido

---

## ✅ Checklist de Resolução

Execute na ordem:

- [ ] 1. Verificar containers rodando: `docker compose ps`
- [ ] 2. Executar script: `./scripts/fix-database.sh`
- [ ] 3. Verificar banco criado: `docker exec clube-quinze-postgres psql -U postgres -l`
- [ ] 4. Reiniciar aplicação: `docker compose restart api`
- [ ] 5. Verificar logs: `docker compose logs -f api`
- [ ] 6. Testar conexão: curl ou navegador
- [ ] 7. Fazer backup: `./scripts/backup-database.sh`

---

## 🆘 Ainda com Problemas?

Se após seguir os passos acima ainda tiver problemas:

1. **Verifique os logs completos:**
   ```bash
   docker compose logs --tail=200 > logs.txt
   cat logs.txt
   ```

2. **Verifique se há outros erros:**
   ```bash
   docker compose ps
   docker volume ls
   ```

3. **Consulte a documentação:**
   - [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
   - [DATA-PERSISTENCE.md](DATA-PERSISTENCE.md)

4. **Tente recriar (última opção, apaga dados):**
   ```bash
   docker compose down -v
   docker compose up -d
   ```

---

**Problema resolvido! 🎉**

Após criar o banco, seus dados estarão seguros e persistirão mesmo com reinicializações do container.

