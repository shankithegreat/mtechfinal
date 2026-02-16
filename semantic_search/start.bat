@echo off
REM Quick start script - builds index and starts server

echo ===================================
echo Semantic Search - Quick Start
echo ===================================

cd backend

echo Activating environment...
call .venv_semanticsearch\Scripts\activate.bat

echo.
echo [1/2] Building semantic index...
echo This may take a few minutes on first run...
python semantic_indexer.py

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Index build failed
    pause
    exit /b 1
)

echo.
echo [2/2] Starting API server...
echo API will run on http://localhost:5000
echo Press Ctrl+C to stop the server
echo.
echo Open frontend\index.html in your browser to use the search interface
echo.

python search_api.py
