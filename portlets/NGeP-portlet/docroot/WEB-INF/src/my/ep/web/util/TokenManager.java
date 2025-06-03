package my.ep.web.util;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import my.ep.constants.CommonConstants;

import org.apache.log4j.Logger;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TokenManager.class);
	private static final String TOKEN_KEY = CommonConstants.TOKEN_UNIQUE_ID;

	public static String generateToken() {
		String token = UUID.randomUUID().toString();
		getPortletSession().setAttribute(TOKEN_KEY, token);
		return token;
	}

	public static boolean isValidToken(String submittedToken) {
		String sessionToken = (String) getPortletSession().getAttribute(
				TOKEN_KEY);
		log.info(">>>>>>>>>>>>>>>>>>>>#38229 sessionToken:" + sessionToken
				+ " submittedToken:" + submittedToken);
		return submittedToken != null && submittedToken.equals(sessionToken);
	}

	public static void invalidateToken() {
		getPortletSession().removeAttribute(TOKEN_KEY);
	}

	private static PortletSession getPortletSession() {
		return (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
	}
}
