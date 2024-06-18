package exceptions;

public class ManagerNotFileFound extends RuntimeException {
    public ManagerNotFileFound(String message) {
        super(message);
    }
}
