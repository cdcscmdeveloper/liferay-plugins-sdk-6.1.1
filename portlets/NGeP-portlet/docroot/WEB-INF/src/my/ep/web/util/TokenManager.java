package my.ep.web.util;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import my.ep.constants.CommonConstants;
import my.ep.util.EpPropertiesUtils;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOKEN_KEY = CommonConstants.TOKEN_UNIQUE_ID;
	private static String BACK_BUTTON_ALLOWED = EpPropertiesUtils.getInstance()
			.getProperty("back.btn.allowed");

	public static boolean isBackBtnAllowed() {
		return (BACK_BUTTON_ALLOWED != null && BACK_BUTTON_ALLOWED.trim()
				.equalsIgnoreCase("1")) ? true : false;
	}

	public static String generateToken() {
		String token = UUID.randomUUID().toString();
		getPortletSession().setAttribute(TOKEN_KEY, token);
		return token;
	}

	public static boolean isValidToken(String submittedToken) {
		String sessionToken = (String) getPortletSession().getAttribute(
				TOKEN_KEY);
		return isBackBtnAllowed() ? true
				: (submittedToken != null && submittedToken
						.equals(sessionToken));
	}

	public static void invalidateToken() {
		getPortletSession().removeAttribute(TOKEN_KEY);
	}

	private static PortletSession getPortletSession() {
		return (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}
}
