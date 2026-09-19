package amatsagu.grimdepth.compat.penchant;

public class IncompatiblePenchantVersionException extends RuntimeException {
    public IncompatiblePenchantVersionException(String message) {
        super(message);
    }

    public IncompatiblePenchantVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
