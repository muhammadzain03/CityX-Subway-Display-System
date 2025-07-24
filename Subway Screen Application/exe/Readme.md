# Train Simulator

A Java program that models the movement of 12 trains on 3 subway lines. Here's an outline of what the program will do:

1. Parse the CSV file and store the station information in data structures that make it easy to access.
2. Initialize the positions of the 12 trains on the subway lines, making sure to keep 4 stations distance between them.
3. Simulate the movement of the trains every 15 seconds by updating their positions based on their current direction and speed.
4. Print out the current positions of the trains on the subway lines to the console and output folder. 

## How to run?

    1) Open CMD
    2) Go to Subway Screen Application folder
    3) run command
        java -jar .\exe\SubwaySimulator.jar --in ".\data\subway.csv" --out ".\out"

# ===============================================================================================================================

    1) Open new CMD
    2) To compile your Java files and have the class files placed in the bin directory, you can use the 'javac' command with the appropriate source and destination paths. Here's how you can do it:
        i) cd "C:\Zain\Software Projects (Linked with Github)\Subway Screen Application\Subway Screen Application"
        ii) javac -d bin -cp "lib/*" src\ca\ucalgary\edu\ensf380\*.java src\ca\ucalgary\edu\ensf380\controller\*.java src\ca\ucalgary\edu\ensf380\view\*.java src\ca\ucalgary\edu\ensf380\model\*.java src\ca\ucalgary\edu\ensf380\util\*.java

    3) After running the javac command, check the bin directory to ensure that the class files have been created in the correct subdirectories. You should find them under:
        -> C:\Zain\Software Projects (Linked with Github)\Subway Screen Application\Subway Screen Application\bin\ca\ucalgary\edu\ensf380

    4) to run: java -cp "bin;lib/*" ca.ucalgary.edu.ensf380.view.SubwayScreenApp 1 Calgary CA

