================================================================================
                    PRIORITY-DRIVEN TASK SCHEDULER (v1.0.0)                     
================================================================================

[DESCRIPTION]
A robust command-line interface (CLI) application engineered in Java for 
efficient task management, chronological tracking, and urgency prioritization. 
Built entirely from scratch to demonstrate low-level implementation of core 
data structures and custom algorithms without relying on framework abstractions.

[SYSTEM FEATURES]
* OPTION_1 : ADD TASK -----------------> Inserts task with Name, Date, & Priority
* OPTION_2 : SHOW CLOSEST FOR TODAY ---> Evaluates absolute calendar day delta
* OPTION_3 : SHOW ALL (PRIORITY) ------> Pulls elements ranked by level (1 to 3)
* OPTION_4 : DELETE TASK --------------> Extracts node & balances memory bounds
* OPTION_5 : SHOW ALL (DEADLINE) ------> Displays pure timeline chronological order
* OPTION_6 : VIEW TRASH LOG -----------> Renders deleted session history (LIFO)
* OPTION_7 : EXIT ---------------------> Gracefully terminates application pipeline

[DATA STRUCTURES REGISTERED]
01/ CUSTOM MIN-HEAP : Backs the Priority Queue structure. Handles insertion 
                      and automated tree balancing in O(log n) time.
02/ CUSTOM STACK    : Backs the Trash History Log. Preserves task objects in a
                      pure Last-In, First-Out (LIFO) session cache registry.
03/ ARRAYLIST       : Provides O(1) index accessibility for data mutation, 
                      CLI iteration arrays, and sequential memory streaming.

[ALGORITHMIC PARADIGMS]
* HeapifyUp / HeapifyDown : Binary tree restructuring routines for the Min-Heap.
* Absolute Day Delta     : Direct pointer evaluation using linear O(n) scan.
* Chronological Sort     : Iterative Bubble Sort ordering dates chronologically.

================================================================================
                    QUICK START / INSTALLATION INSTRUCTIONS                     
================================================================================

[STEP 1: DOWNLOAD THE FILE]
* Grab the "TaskSchedulerApp.java" file and save it into a folder on your computer.

[STEP 2: OPEN YOUR TERMINAL / COMMAND PROMPT]
* Windows: Search for "cmd" in the Start menu and open it.
* Mac/Linux: Open the "Terminal" application.
* Inside the terminal, navigate to your folder using the "cd" command:
  > cd path/to/your/folder

[STEP 3: COMPILE THE CODE]
* Make sure you have Java installed, then type this command and press Enter:
  > javac TaskSchedulerApp.java

[STEP 4: RUN THE APPLICATION]
* Start the application by typing this command and pressing Enter:
  > java TaskSchedulerApp

================================================================================
NOTE: The program automatically creates a "tasks.txt" file in the same folder 
      to save your data. Keep that file there so you don't lose your tasks!
================================================================================
