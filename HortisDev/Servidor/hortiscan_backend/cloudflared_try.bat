@echo off

:: Levantar el servicio para el puerto 8080
echo Ejecutando en puerto 8080
start cmd /k "echo Ejecutando en puerto 8080 & cloudflared tunnel --url http://localhost:8080"

:: Levantar el servicio para el puerto 80
echo Ejecutando en puerto 80
start cmd /k "echo Ejecutando en puerto 80 & cloudflared tunnel --url http://localhost:80"

:: Levantar el servicio para el puerto 4200
echo Ejecutando en puerto 4200
start cmd /k "echo Ejecutando en puerto 4200 & cloudflared tunnel --url http://localhost:4200"