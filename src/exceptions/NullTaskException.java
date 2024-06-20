package exceptions;

public class NullTaskException extends RuntimeException {
    public NullTaskException(String message) {
        super(message);
    }
}
