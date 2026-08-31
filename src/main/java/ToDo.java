public class ToDo extends Task {
    @Override
    public String toString() {
        return "[T]" + (isDone() ? "[X] " : "[ ] ") + getDescription();
    }

    public ToDo(String description) {
        this.setDescription(description);
        this.markAsNotDone();
    }
}
