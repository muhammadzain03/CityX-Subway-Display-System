@echo off
echo ========================================
echo    Subway Screen Application - Build
echo ========================================

REM Change to the project directory
cd /d "%~dp0"

REM Clean old compiled files for a fresh build
echo Cleaning old compiled files...
if exist "bin" (
    echo Removing old class files from bin...
    rmdir /s /q bin 2>nul
)

REM Create fresh bin directory
echo Creating bin directory...
mkdir bin

REM Create out directory if it doesn't exist
if not exist "out" (
    echo Creating out directory...
    mkdir out
)

echo Compiling Java source files...
echo.

REM Compile all Java files together with proper classpath
REM This ensures all interdependencies are resolved
javac -cp "lib/*" -d bin src/ca/ucalgary/edu/ensf380/*.java src/ca/ucalgary/edu/ensf380/controller/*.java src/ca/ucalgary/edu/ensf380/model/*.java src/ca/ucalgary/edu/ensf380/util/*.java src/ca/ucalgary/edu/ensf380/view/*.java

REM Check if compilation was successful
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    Build completed successfully!
    echo ========================================
    echo Compiled classes are in the 'bin' folder
    echo.
    echo Note: The simulator will be started automatically when you run the application.
    echo.
) else (
    echo.
    echo ========================================
    echo    Build failed! Check for errors above.
    echo ========================================
    echo.
)

pause 