package amatsagu.grimdepth.compat.penchant;

/**
 * Exception thrown when an incompatible version of the Penchant mod is detected at runtime.
 */
public class IncompatiblePenchantVersionException extends RuntimeException {
    public IncompatiblePenchantVersionException(String message) {
        super(message);
    }

    public IncompatiblePenchantVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
