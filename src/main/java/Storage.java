import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reads the task list from disk when the bot starts and writes it back
 * whenever the list changes.
 * <p>
 * Each task occupies one line, with its fields separated by " | ". For example:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | June 6th
 * E | 0 | project meeting | Aug 6th 2pm | 4pm
 * </pre>
 * The first field is the task type, the second is 1 when the task is done and
 * 0 otherwise, and the rest are the task's own fields.
 */
public class Storage {

    /**
     * Location of the data file, relative to the project root.
     * <p>
     * Built with {@link Paths#get(String, String...)} rather than written as
     * "data/cortisolbot.txt" so that the correct separator is used on every
     * operating system, and kept relative so the bot works on any computer.
     */
    private static final Path DATA_FILE = Paths.get("data", "cortisolbot.txt");

    /**
     * Loads saved tasks into the given array.
     * <p>
     * A missing file is treated as an empty list rather than an error, since
     * that is simply what the first run looks like. Lines that cannot be
     * understood are skipped so that one damaged line does not cost the user
     * the rest of their list.
     *
     * @param tasks array to fill with the loaded tasks
     * @return number of tasks that were loaded
     */
    public static int load(Task[] tasks) {
        if (!Files.exists(DATA_FILE)) {
            return 0;
        }

        int taskCount = 0;
        int skippedLines = 0;

        try {
            for (String line : Files.readAllLines(DATA_FILE)) {
                if (line.isBlank()) {
                    continue;
                }
                if (taskCount == tasks.length) {
                    System.out.printf(" My records hold more than the %d tasks I can carry, "
                            + "sir/madam. I have taken the first %d.%n", tasks.length, tasks.length);
                    break;
                }

                try {
                    tasks[taskCount] = parseTask(line);
                    taskCount++;
                } catch (CortisolException e) {
                    skippedLines++;
                }
            }
        } catch (IOException e) {
            System.out.println(" I could not open my records, sir/madam. "
                    + "I shall begin the evening with an empty list.");
            CortisolBot.printLine();
            return 0;
        }

        if (skippedLines > 0) {
            System.out.printf(" %d line(s) of my records were illegible, sir/madam. "
                    + "I have set them aside.%n", skippedLines);
        }
        if (taskCount > 0) {
            System.out.printf(" I have retrieved %d task(s) from my records, sir/madam.%n",
                    taskCount);
        }
        if (skippedLines > 0 || taskCount > 0) {
            CortisolBot.printLine();
        }

        return taskCount;
    }

    /**
     * Rebuilds a single task from one line of the data file.
     *
     * @param line one line of the data file
     * @return the task that line describes
     * @throws CortisolException if the line does not have the expected shape
     */
    private static Task parseTask(String line) throws CortisolException {
        // The separator is a literal "|", which must be escaped because
        // split() treats its argument as a regular expression.
        String[] fields = line.split("\\|");
        if (fields.length < 3) {
            throw new CortisolException("Too few fields.");
        }

        String type = fields[0].trim();
        String status = fields[1].trim();
        String description = fields[2].trim();

        if (description.isEmpty()) {
            throw new CortisolException("Missing description.");
        }
        if (!status.equals("0") && !status.equals("1")) {
            throw new CortisolException("Unrecognised done-status.");
        }

        Task task;
        switch (type) {
        case "T":
            task = new ToDo(description);
            break;
        case "D":
            if (fields.length < 4 || fields[3].isBlank()) {
                throw new CortisolException("Deadline is missing its due date.");
            }
            task = new Deadline(description, fields[3].trim());
            break;
        case "E":
            if (fields.length < 5 || fields[3].isBlank() || fields[4].isBlank()) {
                throw new CortisolException("Event is missing its start or end.");
            }
            task = new Event(description, fields[3].trim(), fields[4].trim());
            break;
        default:
            throw new CortisolException("Unrecognised task type.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Writes the whole task list to disk, replacing whatever was there before.
     * <p>
     * A failure to save is reported but does not stop the bot, so the user can
     * carry on working even if the file cannot be written.
     *
     * @param tasks array containing the tasks
     * @param taskCount number of tasks currently in the list
     */
    public static void save(Task[] tasks, int taskCount) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < taskCount; i++) {
            content.append(tasks[i].toFileFormat()).append(System.lineSeparator());
        }

        try {
            Path parentFolder = DATA_FILE.getParent();
            if (parentFolder != null) {
                // Creates the folder on the first run; does nothing if it exists.
                Files.createDirectories(parentFolder);
            }
            Files.writeString(DATA_FILE, content.toString());
        } catch (IOException e) {
            System.out.println(" I was unable to write to my records, sir/madam. "
                    + "This change may not survive the evening.");
            CortisolBot.printLine();
        }
    }
}
