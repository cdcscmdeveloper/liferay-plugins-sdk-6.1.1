package my.ep.web.backingbean.pm.audit.mapbeans;

/**
 * #35509 - Audit Trail Profile Management
 * @author normuliana
 *
 */
public class AuditPMException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuditPMException(String message) {
		super(message);
	}

	public AuditPMException(String message, Throwable cause) {
		super(message, cause);
	}
}
