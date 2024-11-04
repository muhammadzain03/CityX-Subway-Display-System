# Train Simulator

A Java program that models the movement of 12 trains on 3 subway lines. Here's an outline of what the program will do:

1. Parse the CSV file and store the station information in data structures that make it easy to access.
2. Initialize the positions of the 12 trains on the subway lines, making sure to keep 4 stations distance between them.
3. Simulate the movement of the trains every 15 seconds by updating their positions based on their current direction and speed.
4. Print out the current positions of the trains on the subway lines to the console and output folder. 

## How to run?

    1) Go to SubwayScreen folder
    2) run command
        java -jar .\exe\SubwaySimulator.jar --in "..\data\subway.csv" --out "..\out"

# ===============================================================================================================================

    1) To compile your Java files and have the class files placed in the bin directory, you can use the 'javac' command with the appropriate source and destination paths. Here's how you can do it:
        i) cd "C:\Zain\University of Calgary\Summer 2024\ENSF-380 - Object Oriented Principles for Software Development\Final Project\SubwayScreen"
        ii) javac -d bin src\ca\ucalgary\edu\ensf380\*.java

    2) After running the javac command, check the bin directory to ensure that the class files have been created in the correct subdirectories. You should find them under:
        -> C:\Zain\University of Calgary\Summer 2024\ENSF-380 - Object Oriented Principles for Software Development\Final Project\SubwayScreen\bin\ca\ucalgary\edu\ensf380

    3) to run: java -cp bin ca.ucalgary.edu.ensf380.MyApp1







