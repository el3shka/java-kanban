package status;

public enum Status {
    NEW("NEW"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");
    private String translation;

    Status() {
    }

    Status(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    public String toString() {
        return translation;
    }
}