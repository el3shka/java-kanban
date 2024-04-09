package model;

import main.status.Status;


import java.util.ArrayList;
import java.util.List;
import main.util.TaskType;
import java.util.Objects;


public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String description, String name, Status status) {
        super(description, name, status, LocalDateTime.of(2022,01,01,00,00),0);
        this.setStartTime(getStartTime());
        this.endTime = getEndTime();
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime(){
        return this.getStartTime().plusMinutes(getDuration());
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(int id) {
        subtaskIds.add(id);
    }

    public void addSubtaskAllIds(List<Integer> allIds) {
        this.subtaskIds = allIds;
    }

    @Override
    public TaskType getType() { return TaskType.EPIC; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        if (getStartTime() != null) {
            return getId() + "," + TaskType.EPIC + "," + getName() + "," + getStatus() + "," + getDescription() + "," +
                    getStartTime().format(getFormatter()) + "," + this.getEndTime().format(getFormatter()) + "," + getDuration() + ",\n";
        } else {
            return "startTime = null";
        }
    }
}

