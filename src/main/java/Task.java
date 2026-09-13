/**
 * Tracks all tasks added in this session.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Constructs a Task object from a task description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }

    /**
     * Returns whether this task is done.
     *
     * @return true if the task is done, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon of this task.
     *
     * @return "[X] " if done, "[ ] " otherwise
     */
    protected String getStatusIcon() {
        return isDone ? "[X] " : "[ ] ";
    }

    /**
     * Returns the done-status of this task as it is written in the data file.
     *
     * @return "1" if done, "0" otherwise
     */
    protected String getStatusNumber() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task encoded as one line of the data file.
     * <p>
     * Subclasses prepend their type letter and append their own extra fields,
     * so this method supplies only the parts common to every task.
     *
     * @return the done-status and description, separated by " | "
     */
    public String toFileFormat() {
        return getStatusNumber() + " | " + description;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return description of the task
     */
    public String getDescription() {
        return description;
    }
}