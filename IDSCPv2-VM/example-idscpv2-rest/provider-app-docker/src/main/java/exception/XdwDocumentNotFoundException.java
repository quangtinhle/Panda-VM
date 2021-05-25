package exception;

public class XdwDocumentNotFoundException extends Exception {

    public XdwDocumentNotFoundException(String message) {
        super(message);
    }

    public XdwDocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
