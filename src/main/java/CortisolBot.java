import java.util.Scanner;

/**
 * Talks to user and keeps todo list.
 */
public class CortisolBot {

    /**
     * Prints a horizontal separator line.
     */
    public static void printLine() {
        System.out.println("-".repeat(55));
    }

    /**
     * Lists all tasks currently in the task list.
     *
     * @param tasks array containing the tasks
     * @param taskCount number of tasks currently in the list
     */
    public static void listTasks(Task[] tasks, int taskCount) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.printf("%d.%s\n", i + 1, tasks[i]);
        }
        printLine();
    }

    /**
     * Adds a todo task to the task list.
     *
     * @param tasks array containing the tasks
     * @param taskCount index at which the new task should be added
     * @param userInput user's todo command
     */
    public static void addTodo(Task[] tasks, int taskCount, String userInput) {
        String description = userInput.substring(5);
        tasks[taskCount] = new ToDo(description);

        System.out.printf(
                "Got It. I've added this task:\n\t[T][ ] %s\n"
                        + " Now you have %d tasks in the list.\n",
                description, taskCount + 1);
        printLine();
    }

    /**
     * Adds a deadline task to the task list.
     *
     * @param tasks array containing the tasks
     * @param taskCount index at which the new task should be added
     * @param userInput user's deadline command
     */
    public static void addDeadline(Task[] tasks, int taskCount, String userInput) {
        String[] words = userInput.split("/");
        String description = words[0].substring(9);
        String deadline = words[1].substring(3);

        tasks[taskCount] = new Deadline(description, deadline);

        System.out.printf(
                "Got It. I've added this task:\n\t[D][ ] %s (by: %s)\n"
                        + " Now you have %d tasks in the list.\n",
                description, deadline, taskCount + 1);
        printLine();
    }

    /**
     * Adds an event task to the task list.
     *
     * @param tasks array containing the tasks
     * @param taskCount index at which the new task should be added
     * @param userInput user's event command
     */
    public static void addEvent(Task[] tasks, int taskCount, String userInput) {
        String[] words = userInput.split("/");
        String description = words[0].substring(6);
        String startTime = words[1].substring(6);
        String endTime = words[2].substring(4);

        tasks[taskCount] = new Event(description, startTime, endTime);

        System.out.printf(
                "Got It. I've added this task:\n\t[E][ ] %s (from: %s to: %s)\n"
                        + " Now you have %d tasks in the list.\n",
                description, startTime, endTime, taskCount + 1);
        printLine();
    }

    /**
     * Marks a task as done.
     *
     * @param tasks array containing the tasks
     * @param userInput user's mark command
     */
    public static void markTask(Task[] tasks, String userInput) {
        String[] words = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(words[1]) - 1;

        tasks[taskIndex].markAsDone();

        System.out.println("Nice! I've marked this task as done:");
        System.out.printf("\t%s\n", tasks[taskIndex]);
        printLine();
    }

    /**
     * Marks a task as not done.
     *
     * @param tasks array containing the tasks
     * @param userInput user's unmark command
     */
    public static void unmarkTask(Task[] tasks, String userInput) {
        String[] words = userInput.split("\\s+");
        int taskIndex = Integer.parseInt(words[1]) - 1;

        tasks[taskIndex].markAsNotDone();

        System.out.println("Ok, I've marked this task as not done yet:");
        System.out.printf("\t%s\n", tasks[taskIndex]);
        printLine();
    }

    public static void main(String[] args) {
        String banner = "  ____           _   _           _ ____        _   \n"
                + " / ___|___  _ __| |_(_)___  ___ | | __ )  ___ | |_ \n"
                + "| |   / _ \\| '__| __| / __|/ _ \\| |  _ \\ / _ \\| __|\n"
                + "| |__| (_) | |  | |_| \\__ \\ (_) | | |_) | (_) | |_ \n"
                + " \\____\\___/|_|   \\__|_|___/\\___/|_|____/ \\___/ \\__|";

        String name = "CortisolBot";
        System.out.println(banner);
        printLine();
        System.out.printf("Greetings sir/madam, %s humbly at your service.\n", name);
        System.out.println("How may I serve you at this evening?");
        printLine();

        Task[] tasks = new Task[100];
        int taskCount = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String userInput = scanner.nextLine();
            printLine();

            if (userInput.equals("list")) {
                listTasks(tasks, taskCount);
            } else if (userInput.startsWith("todo ")) {
                addTodo(tasks, taskCount, userInput);
                taskCount++;
            } else if (userInput.startsWith("deadline ")) {
                addDeadline(tasks, taskCount, userInput);
                taskCount++;
            } else if (userInput.startsWith("event ")) {
                addEvent(tasks, taskCount, userInput);
                taskCount++;
            } else if (userInput.equals("bye")) {
                break;
            } else if (userInput.startsWith("mark ")) {
                markTask(tasks, userInput);
            } else if (userInput.startsWith("unmark ")) {
                unmarkTask(tasks, userInput);
            } else {
                tasks[taskCount] = new Task(userInput);
                System.out.printf("added: %s\n", userInput);
                printLine();
                taskCount++;
            }
        }

        System.out.println("Tonight has been an honour. I shall bid thee farewell!");
        printLine();
    }
}