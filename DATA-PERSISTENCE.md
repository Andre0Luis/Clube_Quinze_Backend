# Persistência de Dados - Clube Quinze API

## 🗄️ Como os Dados São Persistidos

O projeto usa **volumes do Docker** para garantir que os dados do PostgreSQL persistam mesmo quando o container é parado ou reiniciado.

### Configuração Atual

No `compose.yaml`:
```yaml
services:
  postgres:
    volumes:
      - postgres_data:/var/lib/postgresql/data  # ← Volume nomeado

volumes:
  postgres_data:  # ← Volume persistente gerenciado pelo Docker
```

## ✅ Quando os Dados São MANTIDOS

Os dados **permanecem intactos** nas seguintes situações:

1. **Container é parado:**
   ```bash
   docker compose stop
   docker compose stop postgres
   ```

2. **Container é reiniciado:**
   ```bash
   docker compose restart
   docker compose restart postgres
   ```

3. **Container cai por erro:**
   - O Docker automaticamente mantém os dados no volume

4. **Servidor/VPS é reiniciado:**
   - Os volumes persistem no sistema de arquivos do host

5. **Containers são removidos (sem flag -v):**
   ```bash
   docker compose down  # ✅ Dados mantidos
   ```

6. **Rebuild da aplicação:**
   ```bash
   docker compose up --build  # ✅ Dados mantidos
   ```

## ❌ Quando os Dados São PERDIDOS

Os dados **são removidos** apenas quando:

1. **Volume é explicitamente removido:**
   ```bash
   docker compose down -v  # ⚠️ Flag -v remove volumes!
   docker volume rm clubequinzeapi_postgres_data
   ```

2. **Volume é purgado:**
   ```bash
   docker system prune -a --volumes  # ⚠️ Remove tudo!
   ```

## 🔍 Verificar Volumes

### Windows:
```cmd
scripts\check-volumes.bat
```

### Linux/VPS:
```bash
chmod +x scripts/check-volumes.sh
./scripts/check-volumes.sh
```

### Comandos Manuais:

```bash
# Listar todos os volumes
docker volume ls

# Inspecionar volume específico
docker volume inspect clubequinzeapi_postgres_data

# Ver onde o volume está fisicamente no host
docker volume inspect clubequinzeapi_postgres_data --format '{{ .Mountpoint }}'

# Ver espaço usado
docker system df -v
```

## 💾 Backup e Restore

### Fazer Backup

**Windows:**
```cmd
scripts\backup-database.bat
```

**Linux/VPS:**
```bash
chmod +x scripts/backup-database.sh
./scripts/backup-database.sh
```

**Manual:**
```bash
# Criar backup
docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze > backup.sql

# Criar backup com timestamp
docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze > backup_$(date +%Y%m%d_%H%M%S).sql

# Criar backup compactado
docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze | gzip > backup.sql.gz
```

### Restaurar Backup

**Windows:**
```cmd
scripts\restore-database.bat backups\backup.sql
```

**Linux/VPS:**
```bash
chmod +x scripts/restore-database.sh
./restore-database.sh backups/backup.sql
```

**Manual:**
```bash
# Restaurar backup
docker exec -i clube-quinze-postgres psql -U postgres clube_quinze < backup.sql

# Restaurar backup compactado
gunzip -c backup.sql.gz | docker exec -i clube-quinze-postgres psql -U postgres clube_quinze
```

## 📊 Localização dos Volumes

### Docker Desktop (Windows/Mac)
Os volumes ficam dentro do sistema de arquivos gerenciado pelo Docker Desktop:
- Windows: `\\wsl$\docker-desktop-data\data\docker\volumes\`
- Mac: `~/Library/Containers/com.docker.docker/Data/`

### Linux/VPS
Os volumes ficam em:
```
/var/lib/docker/volumes/clubequinzeapi_postgres_data/_data
```

⚠️ **Não edite esses arquivos diretamente!** Use sempre os comandos do Docker ou scripts fornecidos.

## 🔄 Estratégias de Backup Recomendadas

### Desenvolvimento Local
- Fazer backup antes de mudanças importantes no schema
- Usar `git` para versionar migrations

### Produção/VPS
1. **Backup Automático Diário:**
   ```bash
   # Adicionar ao crontab
   0 2 * * * /caminho/para/scripts/backup-database.sh
   ```

2. **Manter múltiplas cópias:**
   ```bash
   # Manter últimos 7 dias
   find backups/ -name "backup_*.sql.gz" -mtime +7 -delete
   ```

3. **Backup antes de deploy:**
   ```bash
   ./scripts/backup-database.sh
   git pull
   docker compose up --build -d
   ```

4. **Backup remoto (cloud):**
   ```bash
   # Enviar para S3, Google Cloud Storage, etc.
   aws s3 cp backup.sql.gz s3://meu-bucket/backups/
   ```

## 🚨 Migrar Dados Entre Ambientes

### Do Local para VPS:
```bash
# 1. No local, criar backup
./scripts/backup-database.sh

# 2. Enviar para VPS
scp backups/backup_*.sql.gz usuario@vps:/caminho/projeto/backups/

# 3. Na VPS, restaurar
./scripts/restore-database.sh backups/backup_*.sql.gz
```

### Da VPS para Local:
```bash
# 1. Na VPS, criar backup
./scripts/backup-database.sh

# 2. No local, baixar
scp usuario@vps:/caminho/projeto/backups/backup_*.sql.gz backups/

# 3. No local, restaurar
./scripts/restore-database.sh backups/backup_*.sql.gz
```

## 🛡️ Segurança dos Dados

### Boas Práticas:

1. **Nunca commite backups no git:**
   ```bash
   # Já está no .gitignore
   backups/
   *.sql
   *.sql.gz
   ```

2. **Proteja backups em produção:**
   ```bash
   chmod 600 backups/*.sql
   ```

3. **Criptografe backups sensíveis:**
   ```bash
   # Criar backup criptografado
   docker exec clube-quinze-postgres pg_dump -U postgres clube_quinze | \
     gpg --symmetric --cipher-algo AES256 > backup.sql.gpg
   
   # Restaurar backup criptografado
   gpg --decrypt backup.sql.gpg | \
     docker exec -i clube-quinze-postgres psql -U postgres clube_quinze
   ```

4. **Use senhas fortes em produção:**
   - Altere `POSTGRES_PASSWORD` no `.env`
   - Nunca use credenciais padrão

## 📝 Checklist de Segurança de Dados

- [ ] Backups automáticos configurados
- [ ] Backups testados (fazer restore de teste)
- [ ] Backups armazenados fora do servidor
- [ ] Volumes do Docker não serão removidos acidentalmente
- [ ] Senhas de produção são fortes e únicas
- [ ] Backups antigos são rotacionados/removidos
- [ ] Equipe sabe como restaurar backup em emergência

## 🆘 Recuperação de Desastres

Se você perdeu os dados acidentalmente:

1. **Parar tudo imediatamente:**
   ```bash
   docker compose down
   ```

2. **NÃO reiniciar containers** (pode sobrescrever dados)

3. **Verificar se o volume ainda existe:**
   ```bash
   docker volume ls | grep postgres
   ```

4. **Se o volume existe, os dados podem estar intactos:**
   ```bash
   # Criar container temporário para inspecionar
   docker run --rm -v clubequinzeapi_postgres_data:/data alpine ls -la /data
   ```

5. **Restaurar do último backup:**
   ```bash
   ./scripts/restore-database.sh backups/backup_MAIS_RECENTE.sql
   ```

6. **Se não tem backup e volume sumiu:**
   - Infelizmente, dados foram perdidos 😢
   - Implementar backups automáticos imediatamente!

## 📚 Recursos Adicionais

- [Docker Volumes Documentation](https://docs.docker.com/storage/volumes/)
- [PostgreSQL Backup/Restore](https://www.postgresql.org/docs/current/backup.html)
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - Problemas comuns
- [SETUP.md](SETUP.md) - Configuração inicial

