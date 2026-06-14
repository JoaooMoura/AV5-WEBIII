@echo off
cd /d "%~dp0"

where docker >nul 2>&1
if errorlevel 1 (
	echo Docker nao encontrado. Instale e abra o Docker Desktop.
	exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
	echo Docker Desktop nao esta iniciado.
	exit /b 1
)

docker compose -f docker-compose.yml up --build -d --wait
if errorlevel 1 exit /b 1

docker compose -f docker-compose.yml ps
echo.
echo Sistema iniciado em http://localhost:8080
echo Use stop.bat para encerrar.
