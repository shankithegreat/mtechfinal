@echo off
REM Setup script for Semantic Repository Index System (Windows)

echo ===================================
echo Semantic Search Setup
echo ===================================

REM Check Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Python is not installed or not in PATH
    pause
    exit /b 1
)

echo [1/4] Setting up virtual environment...
cd backend
if exist .venv (
    echo Virtual environment already exists
) else (
    python -m venv .venv
)

echo [2/4] Activating virtual environment...
call .venv\Scripts\activate.bat

echo [3/4] Installing dependencies...
pip install --upgrade pip
pip install -r requirements.txt

echo [4/4] Setup complete!
echo.
echo Next steps:
echo 1. Build the index: python semantic_indexer.py
echo 2. Start the API: python search_api.py
echo 3. Open frontend: ..\frontend\index.html
echo.

pause
