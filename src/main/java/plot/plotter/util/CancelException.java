package plot.plotter.util;

public class CancelException extends Exception {
    public CancelException() {
        super("Operation cancelled by the user");
    }

    public CancelException(String message) {
        super(message);
    }
}
