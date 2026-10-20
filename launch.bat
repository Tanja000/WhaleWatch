@echo off
cd /d "%~dp0"
title WhaleWatch Launcher

set DEVICE=%1
if "%DEVICE%"=="" set DEVICE=android

:: Check if server is already running
%SystemRoot%\System32\curl.exe -s -m 2 http://127.0.0.1:8765/api/health >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Starting WhaleWatch Server in background...
    start "" powershell.exe -ExecutionPolicy Bypass -WindowStyle Hidden -File "%~dp0server.ps1"
    ping 127.0.0.1 -n 3 >nul
)

:: Launch Chrome in standalone device simulation window
set CHROME="C:\Program Files\Google\Chrome\Application\chrome.exe"
if exist %CHROME% (
    if /I "%DEVICE%"=="ios" (
        start "" %CHROME% --app="http://127.0.0.1:8765/?device=ios" --window-size=430,932 --user-data-dir="%LOCALAPPDATA%\WhaleWatchIosProfile"
    ) else (
        start "" %CHROME% --app="http://127.0.0.1:8765/?device=android" --window-size=430,930 --user-data-dir="%LOCALAPPDATA%\WhaleWatchAndroidProfile"
    )
) else (
    start http://127.0.0.1:8765/?device=%DEVICE%
)
