# 📦 Persistência de Dados - RESUMO EXECUTIVO

## ✅ SIM! Seus dados estão SEGUROS e PROTEGIDOS

O projeto usa **volumes do Docker** para persistência de dados. Isso significa que:

### 🛡️ DADOS SÃO MANTIDOS quando:

✅ Container é parado (`docker compose stop`)  
✅ Container é reiniciado (`docker compose restart`)  
✅ Container cai por erro  
✅ Servidor/VPS é reiniciado  
✅ Rebuild da aplicação (`docker compose up --build`)  
✅ Remoção de containers SEM flag `-v` (`docker compose down`)  

### ⚠️ DADOS SÃO PERDIDOS apenas quando:

❌ Você usa `docker compose down -v` (flag `-v` remove volumes)  
❌ Você remove o volume: `docker volume rm clubequinzeapi_postgres_data`  
❌ Você usa `docker system prune -a --volumes`  

---

## 📊 Volume Configurado

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data  # ← Dados persistentes aqui

volumes:
  postgres_data:  # ← Volume gerenciado pelo Docker
```

**Localização física:**
- Windows/Mac: Gerenciado pelo Docker Desktop
- Linux/VPS: `/var/lib/docker/volumes/clubequinzeapi_postgres_data/_data`

---

## 🔧 Scripts Criados

### Windows (.bat)
- `scripts\backup-database.bat` - Fazer backup
- `scripts\restore-database.bat` - Restaurar backup
- `scripts\fix-database.bat` - Corrigir banco inexistente
- `scripts\check-volumes.bat` - Verificar volumes

### Linux/Mac/VPS (.sh)
- `scripts/backup-database.sh` - Fazer backup
- `scripts/restore-database.sh` - Restaurar backup
- `scripts/fix-database.sh` - Corrigir banco inexistente
- `scripts/check-volumes.sh` - Verificar volumes

### Uso (Linux/VPS - dar permissão primeiro):
```bash
chmod +x scripts/*.sh
./scripts/backup-database.sh
```

---

## 📚 Documentação Completa

Criamos documentação detalhada sobre tudo:

1. **DATA-PERSISTENCE.md** - Guia completo sobre persistência de dados
2. **SCRIPTS.md** - Documentação de todos os scripts
3. **SETUP.md** - Como configurar o projeto
4. **TROUBLESHOOTING.md** - Problemas comuns e soluções
5. **backups/README.md** - Como usar backups

---

## 🚀 Uso Rápido

### Fazer Backup
```bash
# Windows
scripts\backup-database.bat

# Linux/VPS
./scripts/backup-database.sh
```

### Restaurar Backup
```bash
# Windows
scripts\restore-database.bat backups\backup.sql

# Linux/VPS
./scripts/restore-database.sh backups/backup.sql
```

### Verificar se Dados Estão Seguros
```bash
# Windows
scripts\check-volumes.bat

# Linux/VPS
./scripts/check-volumes.sh
```

### Corrigir "database does not exist"
```bash
# Windows
scripts\fix-database.bat

# Linux/VPS
./scripts/fix-database.sh
```

---

## 💡 Boas Práticas

### ✅ FAÇA:
- ✓ Fazer backup antes de mudanças importantes
- ✓ Testar backups periodicamente
- ✓ Configurar backups automáticos em produção
- ✓ Armazenar backups fora do servidor (cloud)
- ✓ Usar senhas fortes em produção

### ❌ NÃO FAÇA:
- ✗ Usar `docker compose down -v` sem saber o que faz
- ✗ Editar arquivos do volume diretamente
- ✗ Commitar backups no git (já está no .gitignore)
- ✗ Usar credenciais padrão em produção

---

## 🆘 Em Caso de Emergência

Se algo der errado:

1. **Pare tudo:**
   ```bash
   docker compose down
   ```

2. **Verifique se volume existe:**
   ```bash
   docker volume ls | grep postgres
   ```

3. **Se volume existe, dados estão salvos!**

4. **Restaure do backup:**
   ```bash
   ./scripts/restore-database.sh backups/backup_MAIS_RECENTE.sql
   ```

5. **Leia a documentação:**
   - [DATA-PERSISTENCE.md](DATA-PERSISTENCE.md)
   - [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

## 📞 Arquivos Criados

```
projeto/
├── backups/
│   └── README.md                    ← Como usar backups
├── scripts/
│   ├── backup-database.bat          ← Backup (Windows)
│   ├── backup-database.sh           ← Backup (Linux)
│   ├── restore-database.bat         ← Restore (Windows)
│   ├── restore-database.sh          ← Restore (Linux)
│   ├── fix-database.bat             ← Fix DB (Windows)
│   ├── fix-database.sh              ← Fix DB (Linux)
│   ├── check-volumes.bat            ← Check (Windows)
│   ├── check-volumes.sh             ← Check (Linux)
│   ├── init-db.sh                   ← Auto-init DB
│   └── wait-for-postgres.sh         ← Wait DB ready
├── DATA-PERSISTENCE.md              ← Guia completo persistência
├── SCRIPTS.md                       ← Docs dos scripts
├── SETUP.md                         ← Setup do projeto
├── TROUBLESHOOTING.md               ← Problemas e soluções
├── QUICK-START.md                   ← Este arquivo
├── .gitignore                       ← Atualizado (ignora backups)
└── compose.yaml                     ← Atualizado (volumes)
```

---

## 🎯 Conclusão

**Seus dados estão seguros!** O Docker cuida da persistência automaticamente através de volumes. Você só precisa:

1. ✅ Não usar `-v` ao remover containers
2. ✅ Fazer backups regulares
3. ✅ Testar os backups periodicamente

**Para mais detalhes, consulte:**
- 📖 [DATA-PERSISTENCE.md](DATA-PERSISTENCE.md) - Tudo sobre persistência
- 🔧 [SCRIPTS.md](SCRIPTS.md) - Como usar os scripts
- 🚀 [SETUP.md](SETUP.md) - Setup completo

---

**Tudo configurado e documentado! 🎉**

