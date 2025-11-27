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
echo.
echo ========================================
echo    Application Configuration
echo ========================================
echo.

REM Prompt for Train Number
set /p TRAIN_NUM="Enter Train Number (1-12) [Default: 1]: "
if "%TRAIN_NUM%"=="" set TRAIN_NUM=1

REM Prompt for City
set /p CITY="Enter City Name [Default: Calgary]: "
if "%CITY%"=="" set CITY=Calgary

REM Prompt for Country Code
set /p COUNTRY="Enter Country Code (e.g., CA, US, UK) [Default: CA]: "
if "%COUNTRY%"=="" set COUNTRY=CA

echo.
echo ========================================
echo Configuration Summary:
echo   Train Number: %TRAIN_NUM%
echo   City: %CITY%
echo   Country: %COUNTRY%
echo ========================================
echo.
echo Starting application...
echo.

REM Run the application with user inputs
java -cp "bin;lib/*" ca.ucalgary.edu.ensf380.view.SubwayScreenApp %TRAIN_NUM% %CITY% %COUNTRY%

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