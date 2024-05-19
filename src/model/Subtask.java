package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epicId = epic.getId();
    }

    @Override
    public void setId(int id) {
        if (id != epicId) {
            super.setId(id);
        }
    }

    public int getEpicId() {
        return epicId;
    }
}
