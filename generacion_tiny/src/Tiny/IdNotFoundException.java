package Tiny;

public class IdNotFoundException extends Exception {

    public IdNotFoundException(String message) {
        super(message);
    }

    public IdNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}