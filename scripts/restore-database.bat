@echo off
REM Script para restaurar backup do banco de dados PostgreSQL
REM Uso: restore-database.bat <arquivo_backup.sql>

SET CONTAINER_NAME=clube-quinze-postgres
SET DB_NAME=clube_quinze
SET DB_USER=postgres

if "%~1"=="" (
    echo [ERRO] Especifique o arquivo de backup!
    echo Uso: restore-database.bat ^<arquivo_backup.sql^>
    echo.
    echo Backups disponiveis:
    dir /b backups\*.sql 2>nul
    exit /b 1
)

SET BACKUP_FILE=%~1

if not exist "%BACKUP_FILE%" (
    echo [ERRO] Arquivo nao encontrado: %BACKUP_FILE%
    exit /b 1
)

echo === Restaurando backup do banco de dados ===
echo Container: %CONTAINER_NAME%
echo Database: %DB_NAME%
echo Arquivo: %BACKUP_FILE%
echo.
echo [AVISO] Isso ira SUBSTITUIR todos os dados atuais do banco!
set /p CONFIRM=Deseja continuar? (S/N):

if /i not "%CONFIRM%"=="S" (
    echo Operacao cancelada.
    exit /b 0
)

REM Verificar se container está rodando
docker ps | findstr %CONTAINER_NAME% >nul
if errorlevel 1 (
    echo [ERRO] Container %CONTAINER_NAME% nao esta rodando!
    exit /b 1
)

echo.
echo [INFO] Parando aplicacao...
docker compose stop api

echo [INFO] Dropando banco existente...
docker exec %CONTAINER_NAME% psql -U %DB_USER% -c "DROP DATABASE IF EXISTS %DB_NAME%;"

echo [INFO] Criando banco limpo...
docker exec %CONTAINER_NAME% psql -U %DB_USER% -c "CREATE DATABASE %DB_NAME%;"

echo [INFO] Restaurando backup...
type "%BACKUP_FILE%" | docker exec -i %CONTAINER_NAME% psql -U %DB_USER% -d %DB_NAME%

if errorlevel 1 (
    echo [ERRO] Falha ao restaurar backup!
    exit /b 1
)

echo [OK] Backup restaurado com sucesso!
echo.
echo [INFO] Reiniciando aplicacao...
docker compose start api

echo.
echo [OK] Processo concluido!

