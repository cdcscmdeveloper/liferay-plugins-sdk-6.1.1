package my.ep.web.util;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TokenManager.class);

	private static final String TOKEN_MAP_KEY = "viewTokens"; // Store tokens
																// per
																// view/session

	// You might manage this dynamically or via configuration
	private static final String BACK_BUTTON_ALLOWED = "0"; // Assuming 0 for not
															// allowed

	public static boolean isBackBtnAllowed() {
		return (BACK_BUTTON_ALLOWED != null && BACK_BUTTON_ALLOWED.trim()
				.equalsIgnoreCase("1"));
	}

	/**
	 * Generates a new token and associates it with the current view/session.
	 * This should typically be called when a new view is rendered.
	 */
	public static String generateTokenForNewView() {
		String token = UUID.randomUUID().toString();
		getPortletSession().setAttribute(TOKEN_MAP_KEY, token); // Store only
																// the latest
																// token for
																// simplicity
		log.info(">>>>>>>>>>>>>>>>>>>>#38229 new token:" + token);
		return token;
	}

	/**
	 * Retrieves the token currently associated with the session.
	 */
	public static String getSessionToken() {
		return (String) getPortletSession().getAttribute(TOKEN_MAP_KEY);
	}

	/**
	 * Validates the submitted token against the session token. If valid, it
	 * optionally invalidates/updates the session token for the next request.
	 */
	public static boolean isValidToken(String submittedToken) {
		if (isBackBtnAllowed()) {
			return true; // If back button is allowed, always return true for
							// token validation
		}

		String sessionToken = getSessionToken();

		// If no session token, or submitted token is null, it's invalid
		if (sessionToken == null || submittedToken == null) {
			return false;
		}

		boolean isValid = submittedToken.equals(sessionToken);

		// Crucial: Invalidate the token after a successful check to prevent
		// reuse.
		// For simple back button detection, you might just remove it or
		// generate a new one.
		// For more advanced scenarios (e.g., preventing multiple form
		// submissions for the same action),
		// you would generate a new one *after* the request is processed
		// successfully.
		if (isValid) {
			// For simple back button detection, we can just remove the token
			// after it's used.
			// A new one will be generated when the page re-renders or on the
			// next navigation.
			getPortletSession().removeAttribute(TOKEN_MAP_KEY);
		}

		log.info(">>>>>>>>>>>>>>>>>>>>#38229 isValid:" + isValid);
		return isValid;
	}

	private static PortletSession getPortletSession() {
		return (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}
}
