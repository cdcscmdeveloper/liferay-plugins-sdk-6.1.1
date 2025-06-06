package my.ep.web.util;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import my.ep.constants.CommonConstants;
import my.ep.util.EpPropertiesUtils;

import org.apache.log4j.Logger;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOKEN_KEY = CommonConstants.TOKEN_UNIQUE_ID;
	private static String BACK_BUTTON_ALLOWED = EpPropertiesUtils.getInstance()
			.getProperty("back.btn.allowed");
	private static Logger log = Logger.getLogger(TokenManager.class);

	public static boolean isBackBtnAllowed() {
		return (BACK_BUTTON_ALLOWED != null && BACK_BUTTON_ALLOWED.trim()
				.equalsIgnoreCase("1")) ? true : false;
	}

	public static String generateToken() {
		String token = UUID.randomUUID().toString();
		setTokenSession(token);
		return token;
	}

	public static boolean isValidToken(String submittedToken) {
		if (isBackBtnAllowed()) {
			return true;
		} else {
			String sessionToken = getTokenSession();
			return (submittedToken != null && submittedToken
					.equals(sessionToken));
		}
	}

	public static String getTokenSession() {
		return (String) getPortletSession().getAttribute(TOKEN_KEY);
	}

	public static void setTokenSession(String token) {
		getPortletSession().setAttribute(TOKEN_KEY, token);
	}

	public static String getPartialToken(String token) {
		String firstPartial = "";
		if (token != null && token.length() > 0) {
			try {
				String[] parts = token.split("-");
				if (parts.length > 0) {
					firstPartial = parts[0];
				}
			} catch (Exception ex) {

			}
		}
		return firstPartial;
	}

	private static PortletSession getPortletSession() {
		return (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}
}
