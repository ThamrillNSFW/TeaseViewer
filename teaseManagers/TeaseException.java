package teaseManagers;

public class TeaseException extends Exception {
	String message;
	
	public TeaseException(String cause) {
		message=cause;
	}

	private static final long serialVersionUID = 1L;

}
