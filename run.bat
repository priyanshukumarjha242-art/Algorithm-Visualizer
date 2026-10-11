@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"
set "OUT=out"
set "JAVAC_CMD=C:\Program Files\Java\jdk-26.0.2\bin\javac.exe"
set "JAVA_CMD=C:\Program Files\Java\jdk-26.0.2\bin\java.exe"

where javac >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVAC_CMD=javac"
    set "JAVA_CMD=java"
)

if not exist "%OUT%" mkdir "%OUT%"

echo [BUILD] Compiling Java source files...
powershell -NoProfile -ExecutionPolicy Bypass -Command "& { & '%JAVAC_CMD%' -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { '\"' + $_.FullName + '\"' }) }"

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    pause
    exit /b %errorlevel%
)

echo [SUCCESS] Launching Algorithm Visualizer...
start "" "%JAVA_CMD%" -cp out com.visualizer.Main
