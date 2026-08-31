/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private String deadline;

    /**
     * Constructs a deadline task with the given description and deadline.
     *
     * @param description description of the task
     * @param deadline deadline of the task
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "[D]" + getStatusIcon()
                + getDescription()
                + " (by: " + deadline + ")";
    }
}