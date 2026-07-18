import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

// --- 1. TASK MODEL ---
class Task {
    String name;
    String date; // Format: YYYY-MM-DD
    String time; 
    int priority; // 1 (Most Urgent) to 3 (Least Urgent)

    public Task(String name, String date, String time, int priority) {
        this.name = name;
        this.date = date;
        this.time = time.trim().isEmpty() ? "N/A" : time;
        this.priority = priority;
    }

    public LocalDate getLocalDate() {
        try {
            return LocalDate.parse(this.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return null; 
        }
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%d", name, date, time, priority);
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            return new Task(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("[Priority %d] %s (Due: %s, Time: %s)", priority, name, date, time);
    }
}

// --- 2. CUSTOM MIN-HEAP (PRIORITY QUEUE) ---
// Satisfies structural requirement 1 (Trees/Queues)
class TaskPriorityQueue {
    private List<Task> heap;

    public TaskPriorityQueue() {
        this.heap = new ArrayList<>();
    }

    public void insert(Task task) {
        heap.add(task);
        heapifyUp(heap.size() - 1);
    }

    public Task peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    public Task poll() {
        if (heap.isEmpty()) return null;
        Task root = heap.get(0);
        Task lastNode = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, lastNode);
            heapifyDown(0);
        }
        return root;
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Retrieves heap tasks sorted by Priority
    public List<Task> getSortedTasks() {
        List<Task> tempHeap = new ArrayList<>(this.heap);
        List<Task> sortedList = new ArrayList<>();
        TaskPriorityQueue tempQueue = new TaskPriorityQueue();
        tempQueue.heap = tempHeap;
        
        while (!tempQueue.isEmpty()) {
            sortedList.add(tempQueue.poll());
        }
        return sortedList;
    }

    // Sorts tasks chronologically using custom Bubble Sort
    public List<Task> getTasksByDeadlineOrder() {
        List<Task> list = new ArrayList<>(this.heap);
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                LocalDate date1 = list.get(j).getLocalDate();
                LocalDate date2 = list.get(j + 1).getLocalDate();
                
                if (date1 != null && date2 != null && date1.isAfter(date2)) {
                    Task temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        return list;
    }

    public void removeTask(Task task) {
        int index = heap.indexOf(task);
        if (index == -1) return;
        
        int lastIndex = heap.size() - 1;
        if (index == lastIndex) {
            heap.remove(lastIndex);
        } else {
            heap.set(index, heap.get(lastIndex));
            heap.remove(lastIndex);
            heapifyDown(index);
            heapifyUp(index);
        }
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).priority < heap.get(parentIndex).priority) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size && heap.get(leftChild).priority < heap.get(smallest).priority) {
                smallest = leftChild;
            }
            if (rightChild < size && heap.get(rightChild).priority < heap.get(smallest).priority) {
                smallest = rightChild;
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Task temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}

// --- 3. CUSTOM STACK (DELETED HISTORY / TRASH BIN) ---
// Satisfies structural requirement 2 (Stacks)
class TaskStack {
    private List<Task> stack = new ArrayList<>();

    public void push(Task task) {
        stack.add(task);
    }

    public Task pop() {
        if (stack.isEmpty()) return null;
        return stack.remove(stack.size() - 1);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Returns copy of stack in LIFO order for display
    public List<Task> getLIFOList() {
        List<Task> reversed = new ArrayList<>();
        for (int i = stack.size() - 1; i >= 0; i--) {
            reversed.add(stack.get(i));
        }
        return reversed;
    }
}

// --- 4. MAIN CONTROLLER & CLI ---
public class TaskSchedulerApp {
    private static TaskPriorityQueue taskQueue = new TaskPriorityQueue();
    private static TaskStack trashBin = new TaskStack();
    private static Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "tasks.txt";

    public static void main(String[] args) {
        loadFromFile();

        while (true) {
            System.out.println("\n=================================");
            System.out.println("     TASK SCHEDULER SYSTEM       ");
            System.out.println("=================================");
            System.out.println("1. Add Task");
            System.out.println("2. Show Closest Task for Today");
            System.out.println("3. Show All Tasks (Priority Order)");
            System.out.println("4. Delete a Task");
            System.out.println("5. Show All Tasks (Deadline Order)");
            System.out.println("6. View Deleted Tasks History (Trash Log)");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            String choiceStr = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    showClosestTask();
                    break;
                case 3:
                    showAllTasksPriority();
                    break;
                case 4:
                    deleteTask();
                    break;
                case 5:
                    showAllTasksDeadline();
                    break;
                case 6:
                    viewDeletedHistory();
                    break;
                case 7:
                    System.out.println("Exiting Application. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please choose an option from the menu.");
            }
        }
    }

    private static void addTask() {
        System.out.println("\n--- Add New Task ---");
        System.out.print("Enter Task Name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Task name cannot be empty. Enter Task Name: ");
            name = scanner.nextLine().trim();
        }

        String date = "";
        while (true) {
            System.out.print("Enter Due Date (format: YYYY-MM-DD): ");
            date = scanner.nextLine().trim();
            try {
                LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use YYYY-MM-DD (e.g. 2026-07-18).");
            }
        }

        System.out.print("Enter Time (Optional, press Enter to skip): ");
        String time = scanner.nextLine().trim();

        int priority = 3;
        while (true) {
            System.out.print("Enter Priority (1 = Most Urgent, 2 = Medium, 3 = Least Urgent): ");
            String priorityInput = scanner.nextLine().trim();
            try {
                priority = Integer.parseInt(priorityInput);
                if (priority >= 1 && priority <= 3) {
                    break;
                }
                System.out.println("Priority must be 1, 2, or 3.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1-3).");
            }
        }

        Task newTask = new Task(name, date, time, priority);
        taskQueue.insert(newTask);
        saveToFile(); 
        System.out.println("\nTask added successfully!");
    }

    private static void showClosestTask() {
        System.out.println("\n--- Closest Task for Today ---");
        if (taskQueue.isEmpty()) {
            System.out.println("No tasks scheduled!");
            return;
        }

        LocalDate today = LocalDate.now(); 
        List<Task> allTasks = taskQueue.getSortedTasks();
        
        Task closestTask = null;
        long minDiff = Long.MAX_VALUE;

        for (Task task : allTasks) {
            LocalDate taskDate = task.getLocalDate();
            if (taskDate != null) {
                long daysDiff = Math.abs(ChronoUnit.DAYS.between(today, taskDate));
                if (daysDiff < minDiff) {
                    minDiff = daysDiff;
                    closestTask = task;
                }
            }
        }

        if (closestTask != null) {
            System.out.printf("Today's Date: %s\n", today);
            System.out.println("The closest task is:");
            System.out.println(closestTask);
            System.out.printf("(Difference: %d day(s) away)\n", minDiff);
        } else {
            System.out.println("Error reading dates.");
        }
    }

    private static void showAllTasksPriority() {
        System.out.println("\n--- All Scheduled Tasks (Priority Order) ---");
        if (taskQueue.isEmpty()) {
            System.out.println("No tasks in the scheduler.");
            return;
        }

        List<Task> sorted = taskQueue.getSortedTasks();
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), sorted.get(i));
        }
    }

    private static void deleteTask() {
        System.out.println("\n--- Delete a Task ---");
        if (taskQueue.isEmpty()) {
            System.out.println("No tasks to delete.");
            return;
        }

        List<Task> sorted = taskQueue.getSortedTasks();
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), sorted.get(i));
        }

        int index = -1;
        while (true) {
            System.out.print("Enter the number of the task to delete: ");
            String indexInput = scanner.nextLine().trim();
            try {
                index = Integer.parseInt(indexInput);
                if (index >= 1 && index <= sorted.size()) {
                    break;
                }
                System.out.println("Invalid number. Please select an index from the list.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid index number.");
            }
        }

        Task taskToDelete = sorted.get(index - 1);
        taskQueue.removeTask(taskToDelete);
        
        // Push deleted task onto the Stack (LIFO History Log)
        trashBin.push(taskToDelete);
        
        saveToFile(); 
        System.out.printf("Successfully deleted and archived: \"%s\"\n", taskToDelete.name);
    }

    private static void showAllTasksDeadline() {
        System.out.println("\n--- All Scheduled Tasks (Deadline Chronological Order) ---");
        if (taskQueue.isEmpty()) {
            System.out.println("No tasks in the scheduler.");
            return;
        }

        List<Task> deadlineOrdered = taskQueue.getTasksByDeadlineOrder();
        for (int i = 0; i < deadlineOrdered.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), deadlineOrdered.get(i));
        }
    }

    // Prints history using the LIFO Stack
    private static void viewDeletedHistory() {
        System.out.println("\n--- Deleted Tasks History (Trash Log - LIFO) ---");
        if (trashBin.isEmpty()) {
            System.out.println("Trash log is clean. No deleted tasks found.");
            return;
        }

        List<Task> history = trashBin.getLIFOList();
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("[Archived %d] %s\n", (i + 1), history.get(i));
        }
    }

    // --- FILE PERSISTENCE ---
    private static void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            List<Task> sortedTasks = taskQueue.getSortedTasks();
            for (Task task : sortedTasks) {
                writer.println(task.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not save tasks to storage file.");
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; 

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Task loadedTask = Task.fromFileString(line);
                if (loadedTask != null) {
                    taskQueue.insert(loadedTask);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Error loading tasks from file.");
        }
    }
}