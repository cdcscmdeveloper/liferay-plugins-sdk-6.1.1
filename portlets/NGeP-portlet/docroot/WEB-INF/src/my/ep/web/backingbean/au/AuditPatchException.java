package my.ep.web.backingbean.au;

public class AuditPatchException extends Exception {

	/**
	 * #35740
	 * 
	 * @author shaqimi
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuditPatchException(String message) {
		super(message);
	}

	public AuditPatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
