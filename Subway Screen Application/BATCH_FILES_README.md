# 🚆 Subway Screen Application - Batch Files Guide

This guide explains how to use the automated batch files for building and running your Subway Screen Application.

## 📁 Files Created

- `build.bat` - Compiles all Java source files
- `run.bat` - Runs the SubwayScreenApp with required arguments

## 🚀 How to Use

### Step 1: Build the Application
```cmd
build.bat
```

**What this does:**
- Changes to the project directory
- Creates `out` folder if it doesn't exist
- Creates `bin` folder if it doesn't exist
- **Compiles all Java files** with proper classpath
- Shows success/failure message

### Step 2: Run the Application
```cmd
run.bat
```

**What this does:**
- Changes to the project directory
- Creates `out` folder if it doesn't exist
- Checks if `bin` directory exists (runs build.bat first if needed)
- Checks if the main class exists
- **Starts the simulator in the background** (via SimulatorManager)
- **Runs the GUI application** with default configuration (Train 1, Calgary, CA)
- **Displays professional content immediately** - no loading delays!
- **Continuously updates** train positions every 13 seconds
- **Updates news in background** while keeping content visible
- Shows success/failure message

## 🔧 Technical Details

### Directory Structure
```
Subway Screen Application/
├── build.bat          # Build script
├── run.bat            # Run script
├── src/               # Source files
├── bin/               # Compiled classes (created by build.bat)
├── lib/               # External JARs
├── out/               # Output directory (created automatically)
└── data/              # Data files
```

### Classpath Configuration
- **Build:** `javac -cp "lib/*" -d bin src/...`
- **Run:** `java -cp "bin;lib/*" ca.ucalgary.edu.ensf380.view.SubwayScreenApp`

### Arguments Passed to Application
- `1` - Train identifier (train number)
- `Calgary` - City name
- `CA` - Country code

## ⚠️ Important Notes

1. **Always run `build.bat` first** before `run.bat`
2. **Make sure Java JDK 21 is installed** and in your PATH
3. **All work happens in the project directory** - the batch files automatically change to the correct location
4. **The `out` folder is created automatically** if it doesn't exist
5. **Error checking is included** - the scripts will tell you if something goes wrong

## 🐛 Troubleshooting

### "bin directory not found"
- Run `build.bat` first to compile the application

### "SubwayScreenApp.class not found"
- Run `build.bat` first to compile the application

### Compilation errors
- Check that all required JARs are in the `lib` folder
- Verify Java JDK 21 is installed and in PATH
- Check for syntax errors in your Java source files

### Runtime errors
- Ensure the simulator has generated output files in the `out` directory
- Check that all required data files exist
- Verify database connectivity if using MySQL features

## 🎯 Quick Start

1. **Download and extract** the ZIP file from GitHub
2. **Open Command Prompt** in the project directory
3. **Run:** `build.bat` (this runs the simulator AND compiles the code)
4. **Run:** `run.bat` (this launches the GUI application)
5. **Enjoy your Subway Screen Application!** 🚆

## 📋 Complete Workflow

The `build.bat` file handles the **compilation process**:

1. **Compiles all Java source files** with proper classpath
2. **Creates necessary directories** (`bin` and `out`)

The `run.bat` file handles the **complete runtime process**:

1. **Starts the simulator in the background** (via MyApp3)
2. **Launches the GUI application** with continuous updates
3. **Manages the simulator process** automatically

This means anyone who downloads your project can simply:
- Double-click `build.bat` → Code is compiled
- Double-click `run.bat` → Simulator starts + Application launches 