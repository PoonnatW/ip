public class Event extends Task {
    private String startTime;
    private String endTime;
    @Override
    public String toString() {
        return "[D]" + (isDone() ? "[X] " : "[ ] ") + getDescription() + " (from: " + startTime + "to: " + endTime + ")";
    }

    public Event(String description, String startTime, String endTime) {
        this.setDescription(description);
        this.markAsNotDone();
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
