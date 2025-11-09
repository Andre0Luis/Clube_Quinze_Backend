# Scripts Disponíveis - Clube Quinze API

Este documento lista todos os scripts utilitários disponíveis no projeto.

## 📁 Estrutura

```
scripts/
├── backup-database.bat          # Backup do banco (Windows)
├── backup-database.sh           # Backup do banco (Linux/Mac/VPS)
├── check-volumes.bat            # Verificar volumes Docker (Windows)
├── check-volumes.sh             # Verificar volumes Docker (Linux/Mac/VPS)
├── fix-database.bat             # Corrigir banco inexistente (Windows)
├── fix-database.sh              # Corrigir banco inexistente (Linux/Mac/VPS)
├── restore-database.bat         # Restaurar backup (Windows)
├── restore-database.sh          # Restaurar backup (Linux/Mac/VPS)
├── init-db.sh                   # Inicialização do banco (usado pelo Docker)
└── wait-for-postgres.sh         # Aguardar banco estar pronto (usado pelo Docker)
```

## 🔧 Scripts de Manutenção

### Backup do Banco de Dados

Cria um backup completo do banco de dados PostgreSQL.

**Windows:**
```cmd
scripts\backup-database.bat [nome_arquivo_opcional]
```

**Linux/VPS:**
```bash
chmod +x scripts/backup-database.sh
./scripts/backup-database.sh [nome_arquivo_opcional]
```

**O que faz:**
- Verifica se o container do PostgreSQL está rodando
- Cria um arquivo `.sql` com todos os dados
- Salva em `backups/` com timestamp
- Na versão Linux, também cria versão compactada `.sql.gz`

**Exemplo:**
```bash
# Criar backup com nome automático (timestamp)
./scripts/backup-database.sh

# Criar backup com nome específico
./scripts/backup-database.sh meu-backup.sql
```

---

### Restaurar Backup

Restaura um backup do banco de dados.

**Windows:**
```cmd
scripts\restore-database.bat backups\backup.sql
```

**Linux/VPS:**
```bash
chmod +x scripts/restore-database.sh
./scripts/restore-database.sh backups/backup.sql
```

**O que faz:**
- Para a aplicação
- Remove o banco atual
- Cria um banco limpo
- Restaura os dados do backup
- Reinicia a aplicação

**⚠️ Atenção:** Este script **substitui todos os dados atuais**! Confirma antes de executar.

---

### Verificar Volumes

Mostra informações sobre os volumes Docker onde os dados estão armazenados.

**Windows:**
```cmd
scripts\check-volumes.bat
```

**Linux/VPS:**
```bash
chmod +x scripts/check-volumes.sh
./scripts/check-volumes.sh
```

**O que mostra:**
- Lista de volumes do projeto
- Detalhes do volume do PostgreSQL
- Espaço usado
- Localização física no sistema
- Comandos para backup/restore

---

### Corrigir Banco de Dados Inexistente

Soluciona o erro "database clube_quinze does not exist".

**Windows:**
```cmd
scripts\fix-database.bat
```

**Linux/VPS:**
```bash
chmod +x scripts/fix-database.sh
./scripts/fix-database.sh
```

**O que faz:**
- Verifica se o container do PostgreSQL está rodando
- Verifica se o banco `clube_quinze` existe
- Se não existir, cria o banco
- Testa a conexão
- Sugere reiniciar a aplicação

**Quando usar:**
- Após reiniciar containers e ver erro de banco inexistente
- Quando o volume do PostgreSQL já existe mas o banco não foi criado
- Após migrar volumes entre ambientes

---

## 🚀 Scripts de Deploy/Setup

### Inicialização do Banco (usado pelo Docker)

**Arquivo:** `scripts/init-db.sh`

Este script é executado automaticamente pelo Docker Compose na primeira inicialização do PostgreSQL.

**O que faz:**
- Verifica se o banco `clube_quinze` existe
- Se não existir, cria automaticamente
- Garante que a aplicação sempre encontre o banco

**Nota:** Não precisa executar manualmente! É montado automaticamente no container.

---

### Aguardar PostgreSQL (usado pelo Docker)

**Arquivo:** `scripts/wait-for-postgres.sh`

Script que aguarda o PostgreSQL estar pronto antes de iniciar a aplicação.

**O que faz:**
- Tenta conectar ao PostgreSQL
- Aguarda até estar disponível
- Timeout após tentativas
- Usado em pipelines CI/CD

---

## 📚 Documentação Disponível

### README.md
Documentação principal da API com todos os endpoints.

### SETUP.md
Guia completo de setup e configuração do projeto.
```bash
# Ver conteúdo
cat SETUP.md
```

### TROUBLESHOOTING.md
Soluções para problemas comuns.
```bash
# Ver conteúdo
cat TROUBLESHOOTING.md
```

### DATA-PERSISTENCE.md
Explicação detalhada sobre como os dados são persistidos e protegidos.
```bash
# Ver conteúdo
cat DATA-PERSISTENCE.md
```

### SCRIPTS.md (este arquivo)
Lista de todos os scripts disponíveis.

---

## 🔐 Dar Permissão de Execução (Linux/Mac/VPS)

Antes de usar os scripts em sistemas Unix, dê permissão de execução:

```bash
# Dar permissão a todos os scripts de uma vez
chmod +x scripts/*.sh

# Ou individualmente
chmod +x scripts/backup-database.sh
chmod +x scripts/restore-database.sh
chmod +x scripts/fix-database.sh
chmod +x scripts/check-volumes.sh
```

---

## 💡 Dicas de Uso

### 1. Backup Antes de Mudanças Importantes
```bash
# Antes de atualizar o código
./scripts/backup-database.sh
git pull
docker compose up --build -d
```

### 2. Backup Automático Diário (VPS)
```bash
# Adicionar ao crontab
crontab -e

# Adicionar linha:
0 2 * * * cd /caminho/projeto && ./scripts/backup-database.sh
```

### 3. Limpar Backups Antigos
```bash
# Manter apenas últimos 7 dias
find backups/ -name "backup_*.sql*" -mtime +7 -delete
```

### 4. Copiar Dados Entre Ambientes
```bash
# Do local para VPS
./scripts/backup-database.sh
scp backups/backup_*.sql.gz user@vps:/projeto/backups/

# Na VPS
./scripts/restore-database.sh backups/backup_*.sql.gz
```

### 5. Verificar Integridade dos Dados
```bash
# Conectar ao banco e fazer queries
docker exec -it clube-quinze-postgres psql -U postgres -d clube_quinze

# Dentro do psql
\dt              # Listar tabelas
\d usuarios      # Ver estrutura da tabela usuarios
SELECT COUNT(*) FROM usuarios;  # Contar registros
\q               # Sair
```

---

## 🆘 Emergência: Recuperar Dados

Se algo der errado:

1. **Não entre em pânico!**
2. **Pare tudo:**
   ```bash
   docker compose down
   ```
3. **Verifique se o volume existe:**
   ```bash
   docker volume ls | grep postgres
   ```
4. **Se existe, os dados provavelmente estão salvos**
5. **Restaure do último backup:**
   ```bash
   ./scripts/restore-database.sh backups/backup_MAIS_RECENTE.sql
   ```

---

## 📞 Suporte

Se encontrar problemas:

1. Verifique os logs: `docker compose logs -f`
2. Leia [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
3. Consulte [DATA-PERSISTENCE.md](DATA-PERSISTENCE.md)
4. Verifique os volumes: `./scripts/check-volumes.sh`

---

## ✅ Checklist de Operações

### Setup Inicial
- [ ] Criar arquivo `.env` baseado em `.env.example`
- [ ] Dar permissão aos scripts (Linux/Mac)
- [ ] Subir containers: `docker compose up -d`
- [ ] Verificar logs: `docker compose logs -f`
- [ ] Fazer primeiro backup de teste

### Manutenção Regular
- [ ] Fazer backup antes de mudanças importantes
- [ ] Verificar espaço em disco: `docker system df`
- [ ] Limpar backups antigos periodicamente
- [ ] Testar restore em ambiente de dev

### Deploy/Atualização
- [ ] Fazer backup: `./scripts/backup-database.sh`
- [ ] Baixar mudanças: `git pull`
- [ ] Rebuild: `docker compose up --build -d`
- [ ] Verificar logs: `docker compose logs -f api`
- [ ] Testar aplicação

### Troubleshooting
- [ ] Verificar containers: `docker compose ps`
- [ ] Ver logs: `docker compose logs --tail=100`
- [ ] Verificar volumes: `./scripts/check-volumes.sh`
- [ ] Testar conexão com banco
- [ ] Se necessário, executar `./scripts/fix-database.sh`

