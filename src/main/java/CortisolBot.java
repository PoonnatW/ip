import java.util.Optional;

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

            try {
                // Commands that have a class of their own are built by the
                // Parser and carried out without this loop knowing which one it
                // is holding. The rest are still handled by the switch below,
                // and move across one at a time.
                Optional<Command> command = Parser.parse(userInput);
                if (command.isPresent()) {
                    command.get().execute(tasks, ui, storage);
                    isExiting = command.get().isExit();
                } else {
                    runRemainingCommand(Parser.parseCommandWord(userInput),
                            Parser.parseArguments(userInput));
                }
            } catch (CortisolException e) {
                // The exception message is already phrased for the user.
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Carries out a command that does not yet have a class of its own.
     *
     * @param commandWord the first word the user typed
     * @param arguments everything after the command word
     * @throws CortisolException if the command is unknown or cannot be carried out
     */
    private void runRemainingCommand(String commandWord, String arguments)
            throws CortisolException {
        switch (commandWord) {
        case "todo":
            addTask(Parser.parseTodo(arguments));
            break;
        case "deadline":
            addTask(Parser.parseDeadline(arguments));
            break;
        case "event":
            addTask(Parser.parseEvent(arguments));
            break;
        case "mark":
            markTask(arguments);
            break;
        case "unmark":
            unmarkTask(arguments);
            break;
        case "delete":
            deleteTask(arguments);
            break;
        default:
            throw new CortisolException("I do beg your pardon, sir/madam, but that "
                    + "instruction is not in my repertoire.\n"
                    + " I can manage: todo, deadline, event, list, mark, unmark, "
                    + "delete, bye.");
        }

        // Every command still handled here alters the list, and an unknown one
        // has already thrown, so reaching this line means a save is due.
        saveTasks();
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
     * Adds an already-built task to the task list and announces it.
     * <p>
     * The three add commands differ only in how their text is read, which is
     * {@link Parser}'s business, so they share this one method once the task
     * exists.
     *
     * @param task the task to add
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Marks a task as done.
     *
     * @param arguments text following the "mark" command word
     * @throws CortisolException if the task number is missing or invalid
     */
    private void markTask(String arguments) throws CortisolException {
        Task task = tasks.get(Parser.parseTaskNumber(arguments, "mark"), "mark");
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
        Task task = tasks.get(Parser.parseTaskNumber(arguments, "unmark"), "unmark");
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
        Task removedTask = tasks.remove(Parser.parseTaskNumber(arguments, "delete"), "delete");

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
