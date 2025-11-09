@echo off
REM Script para fazer backup do banco de dados PostgreSQL
REM Uso: backup-database.bat [nome_arquivo]

SET BACKUP_DIR=backups
SET CONTAINER_NAME=clube-quinze-postgres
SET DB_NAME=clube_quinze
SET DB_USER=postgres

REM Criar diretório de backups se não existir
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM Gerar nome do arquivo com timestamp se não fornecido
if "%~1"=="" (
    for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
    for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
    set BACKUP_FILE=%BACKUP_DIR%\backup_%mydate%_%mytime%.sql
) else (
    set BACKUP_FILE=%BACKUP_DIR%\%~1
)

echo === Fazendo backup do banco de dados ===
echo Container: %CONTAINER_NAME%
echo Database: %DB_NAME%
echo Arquivo: %BACKUP_FILE%
echo.

REM Verificar se container está rodando
docker ps | findstr %CONTAINER_NAME% >nul
if errorlevel 1 (
    echo [ERRO] Container %CONTAINER_NAME% nao esta rodando!
    exit /b 1
)

REM Fazer backup
echo [INFO] Iniciando backup...
docker exec %CONTAINER_NAME% pg_dump -U %DB_USER% %DB_NAME% > "%BACKUP_FILE%"

if errorlevel 1 (
    echo [ERRO] Falha ao criar backup!
    exit /b 1
)

echo [OK] Backup criado com sucesso: %BACKUP_FILE%
echo.

REM Mostrar tamanho do arquivo
for %%A in ("%BACKUP_FILE%") do echo Tamanho: %%~zA bytes

echo.
echo === Para restaurar este backup: ===
echo restore-database.bat "%BACKUP_FILE%"

