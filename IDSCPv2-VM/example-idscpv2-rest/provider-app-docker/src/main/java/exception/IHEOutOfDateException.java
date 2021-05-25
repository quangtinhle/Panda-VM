package exception;

/**
 * Used when trying to replace an XDW document while another process already did the same.
 * This exception is thrown when a client-side race condition is detected.
 */
public class IHEOutOfDateException extends RuntimeException {
}
