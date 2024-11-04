
# 🚇 CityX Subway Display System

Welcome to the **CityX Subway Display System** project! This software was developed as a final project for ENSF 380 and provides a dynamic and real-time subway display system. It shows advertisements, news updates, weather information, and train positions on a subway map, helping commuters stay informed on the go.

## 🌟 Features

- **Real-Time Train Tracking**: Displays current train positions on a subway map.
- **Weather Updates**: Fetches live weather information based on the city.
- **News Feeds**: Shows top news headlines based on the selected country.
- **Audio Announcements**: Plays station arrival audio announcements.
- **Advertisement Display**: Rotates advertisements with a set timer.

## 🛠 Prerequisites

- **Java Development Kit (JDK) 21**
- **Database Connection**: Ensure a connected and configured database with required tables.
- **JLayer Library**: For audio playback (MP3 files).

## 📂 Project Structure

```
CityXSubwayDisplay/
├── src/
│   ├── ca/ucalgary/edu/ensf380/
│   │   ├── view/                # UI components like MapPanel, NewsPanel, WeatherPanel
│   │   ├── controller/          # Controls advertisements, weather, and train info
│   │   ├── model/               # Models for SubwayLine, Train, Station
│   ├── SubwayScreenApp.java     # Main application runner
├── audio/                       # Audio files for station announcements
├── out/                         # Folder for simulation output files
├── README.md
└── ...
```

## 🚀 Getting Started

Follow these steps to clone the repository and set up the project correctly.

### 1. Clone the Repository

```bash
git clone <repository_url>
cd CityXSubwayDisplay
```

### 2. Check Required Files

Ensure the following files and folders are present:
- **`out/`** folder for storing simulator output files (create this folder if it’s missing).
- Ensure the **audio** folder contains MP3 files for station announcements.

### 3. Configure the Build Path

If needed, go to the **Java Build Path** in your IDE and fix any missing libraries or paths.

### 4. Connect to the Database

Ensure your database is set up and connected. Populate the required tables for advertisements, stations, and other necessary data.

## ▶️ Running the Program

Use the following command to run the program:

```bash
java SubwayScreenApp <train_number> <city> <country_code>
```

**Example**:
```bash
java SubwayScreenApp 1 Calgary CA
```

### To Stop the Program

Simply close the main application window to exit the program.

## 📝 Example Scenarios

1. **Display Information for Train 1 in Calgary, Canada**:
   ```bash
   java SubwayScreenApp 1 Calgary CA
   ```

2. **Display for a Train in a Different City**:
   Modify `<train_number>`, `<city>`, and `<country_code>` as needed.

## 📋 Notes

- Ensure you have a reliable internet connection for live weather and news feeds.
- Audio announcements require MP3 files named by station codes in the **audio** folder.

## 📜 License

This project is licensed under the MIT License. See `LICENSE` for more information.

## 📧 Contact

For questions or collaboration, please contact [Your Email or LinkedIn Profile].
