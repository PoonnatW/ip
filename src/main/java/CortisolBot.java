/**
 * Talks to the user and keeps the task list.
 * <p>
 * One object holds the three things a session needs: a {@link Ui} for the
 * console, a {@link Storage} for the file, and the tasks themselves. Nothing
 * here prints or reads directly -- every word the user sees goes through the
 * Ui -- so this class is left with the one job of deciding what each command
 * means.
 */
public class CortisolBot {

    /** Where the task list is kept between sessions. */
    private static final String DATA_FILE_PATH = "data/cortisolbot.txt";

    /** Everything the user types and sees. */
    private final Ui ui;

    /** The file the task list is read from and written to. */
    private final Storage storage;

    /**
     * Tasks held for this session.
     * <p>
     * A TaskList grows as needed, so there is no fixed ceiling on the number of
     * tasks the bot can hold, and it is what refuses a task number that refers
     * to nothing.
     */
    private final TaskList tasks;

    /**
     * Constructs a bot that keeps its task list in the named file.
     * <p>
     * Nothing is read or printed yet; the session proper begins with
     * {@link #run()}.
     *
     * @param filePath path to the data file
     */
    public CortisolBot(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
    }

    /**
     * Greets the user, then reads and carries out commands until told to stop.
     */
    public void run() {
        ui.showWelcome();
        loadTasks();

        boolean isExiting = false;
        while (!isExiting) {
            String userInput = ui.readCommand();
            ui.showLine();

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
                    ui.showTaskList(tasks.asArrayList());
                    break;
                case "todo":
                    addTodo(arguments);
                    isListChanged = true;
                    break;
                case "deadline":
                    addDeadline(arguments);
                    isListChanged = true;
                    break;
                case "event":
                    addEvent(arguments);
                    isListChanged = true;
                    break;
                case "mark":
                    markTask(arguments);
                    isListChanged = true;
                    break;
                case "unmark":
                    unmarkTask(arguments);
                    isListChanged = true;
                    break;
                case "delete":
                    deleteTask(arguments);
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
                ui.showError(e.getMessage());
            }

            // Only reached when the command succeeded, since a failed command
            // leaves isListChanged false.
            if (isListChanged) {
                saveTasks();
            }
        }

        ui.showFarewell();
    }

    /**
     * Fills the task list from the data file and reports what was found.
     * <p>
     * A file that cannot be opened is reported and then left behind: the
     * session begins with an empty list rather than refusing to start.
     */
    private void loadTasks() {
        try {
            tasks.addAll(storage.load());
            ui.showLoadReport(tasks.size(), storage.getSkippedLineCount());
        } catch (CortisolException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Writes the task list to the data file.
     * <p>
     * A save that fails is reported but does not end the session, so the user
     * can carry on working even if the file cannot be written.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.asArrayList());
        } catch (CortisolException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Adds a todo task to the task list.
     *
     * @param arguments text following the "todo" command word
     * @throws CortisolException if no description was given
     */
    private void addTodo(String arguments) throws CortisolException {
        if (arguments.isEmpty()) {
            throw new CortisolException("A todo without a description is rather like tea "
                    + "without leaves, sir/madam.\n Do try: todo <description>");
        }

        Task task = new ToDo(arguments);
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Adds a deadline task to the task list.
     *
     * @param arguments text following the "deadline" command word
     * @throws CortisolException if the description or due date is missing
     */
    private void addDeadline(String arguments) throws CortisolException {
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

        Task task = new Deadline(description, deadline);
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Adds an event task to the task list.
     *
     * @param arguments text following the "event" command word
     * @throws CortisolException if the description, start or end is missing
     */
    private void addEvent(String arguments) throws CortisolException {
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

        Task task = new Event(description, startTime, endTime);
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Reads the task number out of the argument of a command that refers to a
     * task by number.
     * <p>
     * Whether the number refers to an existing task is not decided here: that
     * belongs to {@link TaskList}, which is the only place that knows how many
     * tasks there are.
     *
     * @param arguments text following the command word
     * @param commandWord command the user typed, used to phrase the error messages
     * @return the number the user typed, counting from 1
     * @throws CortisolException if no number was given, or the text is not a number
     */
    private static int parseTaskNumber(String arguments, String commandWord)
            throws CortisolException {
        if (arguments.isEmpty()) {
            throw new CortisolException("Which task shall I " + commandWord + ", sir/madam?\n"
                    + " Do try: " + commandWord + " <task number>");
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new CortisolException("'" + arguments + "' is not a number I recognise, "
                    + "sir/madam.\n Do try: " + commandWord + " <task number>");
        }
    }

    /**
     * Marks a task as done.
     *
     * @param arguments text following the "mark" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    private void markTask(String arguments) throws CortisolException {
        Task task = tasks.get(parseTaskNumber(arguments, "mark"), "mark");
        task.markAsDone();

        ui.showMarkedTask(task);
    }

    /**
     * Marks a task as not done.
     *
     * @param arguments text following the "unmark" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    private void unmarkTask(String arguments) throws CortisolException {
        Task task = tasks.get(parseTaskNumber(arguments, "unmark"), "unmark");
        task.markAsNotDone();

        ui.showUnmarkedTask(task);
    }

    /**
     * Removes a task from the task list.
     *
     * @param arguments text following the "delete" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    private void deleteTask(String arguments) throws CortisolException {
        // remove() returns the task it removed, so we can still report it
        // after it has left the list.
        Task removedTask = tasks.remove(parseTaskNumber(arguments, "delete"), "delete");

        ui.showRemovedTask(removedTask, tasks.size());
    }

    /**
     * Starts a session.
     *
     * @param args command line arguments, which this program does not use
     */
    public static void main(String[] args) {
        new CortisolBot(DATA_FILE_PATH).run();
    }
}
