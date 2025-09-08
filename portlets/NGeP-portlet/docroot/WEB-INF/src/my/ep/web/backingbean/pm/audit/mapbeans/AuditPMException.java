package my.ep.web.backingbean.pm.audit.mapbeans;

public class AuditPMException extends Exception {

	/**
	 * #35509
	 * 
	 * @author normuliana
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuditPMException(String message) {
		super(message);
	}

	public AuditPMException(String message, Throwable cause) {
		super(message, cause);
	}
}
