package models;

public class Subtask extends Task {
    private Integer relatedEpicId;
    public Subtask(String name, String description, Integer relatedEpicId) {
        super(name, description);
        this.relatedEpicId = relatedEpicId;
    }
    public Integer getEpicId() {
        return relatedEpicId;
    }
    @Override
    public void setTaskStatus(Status taskStatus) {
        super.setTaskStatus(taskStatus);
    }
    @Override
    public String toString() {
        return "Subtask (" + "id = " + id +
                "; name = " + name +
                "; description = " + description +
                "; status = " + taskStatus +
                "; relatedEpicId = " + relatedEpicId +
                ")\n";
    }
}