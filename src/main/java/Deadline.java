public class Deadline extends Task {
    private String deadline;
    @Override
    public String toString() {
        return "[D]" + (isDone() ? "[X] " : "[ ] ") + getDescription() + " (by: " + deadline + ")";
    }

    public Deadline(String description, String deadline) {
        this.setDescription(description);
        this.markAsNotDone();
        this.deadline = deadline;
    }
}
