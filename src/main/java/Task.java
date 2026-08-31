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