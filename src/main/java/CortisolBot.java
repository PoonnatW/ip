import java.util.ArrayList;
import java.util.Scanner;

/**
 * Talks to user and keeps todo list.
 */
public class CortisolBot {

    /** Where the task list is kept between sessions. */
    private static final String DATA_FILE_PATH = "data/cortisolbot.txt";

    /**
     * Prints a horizontal separator line.
     */
    public static void printLine() {
        System.out.println("-".repeat(55));
    }

    /**
     * Lists all tasks currently in the task list.
     *
     * @param tasks list containing the tasks
     */
    public static void listTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Your list is presently empty, sir/madam. A rare luxury.");
            printLine();
            return;
        }

        System.out.println("Your tasks, sir/madam, as they presently stand:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("\t%d.%s\n", i + 1, tasks.get(i));
        }
        printLine();
    }

    /**
     * Announces a newly added task and the resulting size of the list.
     *
     * @param task the task that was just added
     * @param taskCount number of tasks in the list after the addition
     */
    public static void printAddedTask(Task task, int taskCount) {
        System.out.printf(
                "Very good, sir/madam. I have added the following:\n\t%s\n"
                        + " That makes %d in your keeping.\n",
                task, taskCount);
        printLine();
    }

    /**
     * Adds a todo task to the task list.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "todo" command word
     * @throws CortisolException if no description was given
     */
    public static void addTodo(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        if (arguments.isEmpty()) {
            throw new CortisolException("A todo without a description is rather like tea "
                    + "without leaves, sir/madam.\n Do try: todo <description>");
        }

        tasks.add(new ToDo(arguments));
        printAddedTask(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Adds a deadline task to the task list.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "deadline" command word
     * @throws CortisolException if the description or due date is missing
     */
    public static void addDeadline(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        // Limit of 2 keeps any later "/by" as part of the due date itself.
        String[] parts = arguments.split("/by", 2);
        String description = parts[0].trim();
        String deadline = parts.length > 1 ? parts[1].trim() : "";

        if (description.isEmpty()) {
            throw new CortisolException("You have not told me what is due, sir/madam.\n"
                    + " Do try: deadline <description> /by <when>");
        }
        if (deadline.isEmpty()) {
            throw new CortisolException("A deadline is of little use without a date, sir/madam.\n"
                    + " Do try: deadline <description> /by <when>");
        }

        tasks.add(new Deadline(description, deadline));
        printAddedTask(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Adds an event task to the task list.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "event" command word
     * @throws CortisolException if the description, start or end is missing
     */
    public static void addEvent(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        String[] fromParts = arguments.split("/from", 2);
        String description = fromParts[0].trim();
        String[] toParts = fromParts.length > 1
                ? fromParts[1].split("/to", 2)
                : new String[0];
        String startTime = toParts.length > 0 ? toParts[0].trim() : "";
        String endTime = toParts.length > 1 ? toParts[1].trim() : "";

        if (description.isEmpty()) {
            throw new CortisolException("You have not told me what the occasion is, sir/madam.\n"
                    + " Do try: event <description> /from <start> /to <end>");
        }
        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new CortisolException("An event requires both a start and an end, sir/madam.\n"
                    + " Do try: event <description> /from <start> /to <end>");
        }

        tasks.add(new Event(description, startTime, endTime));
        printAddedTask(tasks.get(tasks.size() - 1), tasks.size());
    }

    /**
     * Converts the argument of a command that refers to a task by number into a
     * valid list index.
     *
     * @param arguments text following the command word
     * @param taskCount number of tasks currently in the list
     * @param commandWord command the user typed, used to phrase the error messages
     * @return zero-based index of the requested task
     * @throws CortisolException if no number was given, the text is not a number,
     *                           or the number does not refer to an existing task
     */
    public static int parseTaskIndex(String arguments, int taskCount, String commandWord)
            throws CortisolException {
        if (arguments.isEmpty()) {
            throw new CortisolException("Which task shall I " + commandWord + ", sir/madam?\n"
                    + " Do try: " + commandWord + " <task number>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new CortisolException("'" + arguments + "' is not a number I recognise, "
                    + "sir/madam.\n Do try: " + commandWord + " <task number>");
        }

        if (taskCount == 0) {
            throw new CortisolException("Your list is presently empty, sir/madam. "
                    + "There is nothing to " + commandWord + " just yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CortisolException("I keep no task numbered " + taskNumber + ", sir/madam.\n"
                    + " Your list runs from 1 to " + taskCount + ".");
        }

        return taskNumber - 1;
    }

    /**
     * Marks a task as done.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "mark" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    public static void markTask(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        int taskIndex = parseTaskIndex(arguments, tasks.size(), "mark");
        Task task = tasks.get(taskIndex);
        task.markAsDone();

        System.out.println("Consider it done, sir/madam:");
        System.out.printf("\t%s\n", task);
        printLine();
    }

    /**
     * Marks a task as not done.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "unmark" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    public static void unmarkTask(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        int taskIndex = parseTaskIndex(arguments, tasks.size(), "unmark");
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();

        System.out.println("Very well, sir/madam. I have returned it to the undone:");
        System.out.printf("\t%s\n", task);
        printLine();
    }

    /**
     * Removes a task from the task list.
     *
     * @param tasks list containing the tasks
     * @param arguments text following the "delete" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    public static void deleteTask(ArrayList<Task> tasks, String arguments)
            throws CortisolException {
        int taskIndex = parseTaskIndex(arguments, tasks.size(), "delete");
        // remove() returns the task it removed, so we can still report it
        // after it has left the list.
        Task removedTask = tasks.remove(taskIndex);

        System.out.println("Consider it forgotten, sir/madam:");
        System.out.printf("\t%s\n", removedTask);
        System.out.printf(" That leaves %d in your keeping.\n", tasks.size());
        printLine();
    }

    /**
     * Reports what was found in the data file when the bot started.
     * <p>
     * Says nothing at all when there was nothing to report, so that a first
     * run opens with the greeting alone.
     *
     * @param taskCount number of tasks that were loaded
     * @param skippedLineCount number of lines that could not be understood
     */
    public static void printLoadReport(int taskCount, int skippedLineCount) {
        if (skippedLineCount > 0) {
            System.out.printf(" %d line(s) of my records were illegible, sir/madam. "
                    + "I have set them aside.%n", skippedLineCount);
        }
        if (taskCount > 0) {
            System.out.printf(" I have retrieved %d task(s) from my records, sir/madam.%n",
                    taskCount);
        }
        if (skippedLineCount > 0 || taskCount > 0) {
            printLine();
        }
    }

    /**
     * Greets the user, then reads and carries out commands until told to stop.
     *
     * @param args command line arguments, which this program does not use
     */
    public static void main(String[] args) {
        String banner ="  ____           _   _           _ ____        _   \n"
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

        // An ArrayList grows as needed, so there is no fixed ceiling on the
        // number of tasks the bot can hold. It starts off holding whatever was
        // saved at the end of the previous session.
        Storage storage = new Storage(DATA_FILE_PATH);
        ArrayList<Task> tasks;

        try {
            tasks = storage.load();
            printLoadReport(tasks.size(), storage.getSkippedLineCount());
        } catch (CortisolException e) {
            // The records could not be opened at all. The message is already
            // phrased for the user, and the evening begins with an empty list.
            System.out.println(" " + e.getMessage());
            printLine();
            tasks = new ArrayList<>();
        }

        Scanner scanner = new Scanner(System.in);
        boolean isExiting = false;

        while (!isExiting) {
            String userInput = scanner.nextLine();
            printLine();

            // Set by any command that alters the list, so that the file is
            // rewritten once per command rather than in six separate places.
            boolean isListChanged = false;

            // Split into the command word and everything after it, so that a
            // command typed on its own (e.g. "todo") can still be recognised.
            String[] inputParts = userInput.trim().split("\\s+", 2);
            String commandWord = inputParts[0];
            String arguments = inputParts.length > 1 ? inputParts[1].trim() : "";

            try {
                switch (commandWord) {
                case "list":
                    listTasks(tasks);
                    break;
                case "todo":
                    addTodo(tasks, arguments);
                    isListChanged = true;
                    break;
                case "deadline":
                    addDeadline(tasks, arguments);
                    isListChanged = true;
                    break;
                case "event":
                    addEvent(tasks, arguments);
                    isListChanged = true;
                    break;
                case "mark":
                    markTask(tasks, arguments);
                    isListChanged = true;
                    break;
                case "unmark":
                    unmarkTask(tasks, arguments);
                    isListChanged = true;
                    break;
                case "delete":
                    deleteTask(tasks, arguments);
                    isListChanged = true;
                    break;
                case "bye":
                    isExiting = true;
                    break;
                default:
                    throw new CortisolException("I do beg your pardon, sir/madam, but that "
                            + "instruction is not in my repertoire.\n"
                            + " I can manage: todo, deadline, event, list, mark, unmark, "
                            + "delete, bye.");
                }
            } catch (CortisolException e) {
                // The exception message is already phrased for the user.
                System.out.println(" " + e.getMessage());
                printLine();
            }

            // Only reached when the command succeeded, since a failed command
            // leaves isListChanged false. A save that fails is reported but
            // does not end the session, so the user can carry on working even
            // if the file cannot be written.
            if (isListChanged) {
                try {
                    storage.save(tasks);
                } catch (CortisolException e) {
                    System.out.println(" " + e.getMessage());
                    printLine();
                }
            }
        }

        System.out.println("Tonight has been an honour. I shall bid thee farewell!");
        printLine();
    }
}
