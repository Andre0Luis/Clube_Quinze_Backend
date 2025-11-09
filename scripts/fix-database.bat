@echo off
REM Script para criar o banco de dados clube_quinze em um container existente
REM Uso: fix-database.bat

echo === Verificando se o container do PostgreSQL esta rodando ===
docker ps | findstr clube-quinze-postgres >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Container clube-quinze-postgres nao esta rodando!
    echo Execute: docker compose up -d postgres
    exit /b 1
)

echo [OK] Container encontrado!
echo.

echo === Verificando se o banco clube_quinze existe ===
for /f %%i in ('docker exec clube-quinze-postgres psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='clube_quinze'"') do set DB_EXISTS=%%i

if "%DB_EXISTS%"=="1" (
    echo [OK] O banco de dados clube_quinze ja existe!
    echo.
    echo === Listando todos os bancos ===
    docker exec clube-quinze-postgres psql -U postgres -c "\l"
) else (
    echo [AVISO] Banco de dados clube_quinze nao existe. Criando...
    docker exec clube-quinze-postgres psql -U postgres -c "CREATE DATABASE clube_quinze;"

    if errorlevel 1 (
        echo [ERRO] Erro ao criar o banco de dados!
        exit /b 1
    )

    echo [OK] Banco de dados clube_quinze criado com sucesso!
    echo.
    echo === Listando todos os bancos ===
    docker exec clube-quinze-postgres psql -U postgres -c "\l"
)

echo.
echo === Testando conexao com o banco clube_quinze ===
docker exec clube-quinze-postgres psql -U postgres -d clube_quinze -c "SELECT version();"

if errorlevel 1 (
    echo.
    echo [ERRO] Nao foi possivel conectar ao banco clube_quinze
    exit /b 1
)

echo.
echo [OK] Tudo pronto! O banco clube_quinze esta funcionando.
echo Agora voce pode reiniciar a aplicacao:
echo   docker compose restart api

