@echo off
echo ========================================
echo    Subway Screen Application - Run
echo ========================================

REM Change to the project directory
cd /d "%~dp0"

REM Create out directory if it doesn't exist
if not exist "out" (
    echo Creating out directory...
    mkdir out
)

REM Check if bin directory exists
if not exist "bin" (
    echo.
    echo ========================================
    echo    Error: bin directory not found!
    echo ========================================
    echo Please run build.bat first to compile the application.
    echo.
    pause
    exit /b 1
)

REM Check if the main class exists
if not exist "bin\ca\ucalgary\edu\ensf380\view\SubwayScreenApp.class" (
    echo.
    echo ========================================
    echo    Error: SubwayScreenApp.class not found!
    echo ========================================
    echo Please run build.bat first to compile the application.
    echo.
    pause
    exit /b 1
)

echo Starting Subway Screen Application...
echo (Using default configuration: Train 1, Calgary, CA)
echo.

REM Run the application - arguments are now OPTIONAL!
REM Default: Train 1, Calgary, CA
REM Custom usage: java -cp "bin;lib/*" ca.ucalgary.edu.ensf380.view.SubwayScreenApp [train] [city] [country]
java -cp "bin;lib/*" ca.ucalgary.edu.ensf380.view.SubwayScreenApp

REM Check if the application ran successfully
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    Application exited successfully!
    echo ========================================
    echo.
) else (
    echo.
    echo ========================================
    echo    Application exited with errors!
    echo ========================================
    echo.
)

pause 