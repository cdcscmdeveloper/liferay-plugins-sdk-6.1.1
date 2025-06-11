package my.ep.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.phaselistener.TokenPhaseListener;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOKEN_KEY = CommonConstants.TOKEN_UNIQUE_ID;
	private static final String TOKEN_HISTORY_KEY = TOKEN_KEY + "_HISTORY";
	private static String BACK_BUTTON_ALLOWED = EpPropertiesUtils.getInstance()
			.getProperty("back.btn.allowed");
	private static Logger log = Logger.getLogger(TokenPhaseListener.class);

	public boolean isBackBtnAllowed() {
		return (BACK_BUTTON_ALLOWED != null && BACK_BUTTON_ALLOWED.trim()
				.equalsIgnoreCase("1")) ? true : false;
	}

	public String generateToken() {
		FacesScopes fs = new FacesScopes();
		List<String> tokenHistory = (List<String>) fs
				.getSessionScope(TOKEN_HISTORY_KEY);
		if (tokenHistory == null) {
			tokenHistory = new ArrayList<String>();
		}
		String currTokenSession = getTokenSession();
		if (currTokenSession != null && !currTokenSession.isEmpty()) {
			tokenHistory.add(currTokenSession);
			fs.setSessionScope(TOKEN_HISTORY_KEY, tokenHistory);
		}
		for (String history : tokenHistory) {
			log.info("#38229 history:" + history);
		}
		String token = UUID.randomUUID().toString();
		new FacesScopes().setSessionScope(TOKEN_KEY, token);
		return token;
	}

	public boolean isValidToken(String submittedToken) {
		boolean isValid = true;
		if (!isBackBtnAllowed()) {
			String sessionToken = getTokenSession();
			if ((submittedToken != null && submittedToken.length() > 0)
					&& (sessionToken != null && sessionToken.length() > 0)) {
				isValid = submittedToken.equals(sessionToken);
			}
		}
		return isValid;
	}

	public String getTokenSession() {
		return (String) new FacesScopes().getSessionScope(TOKEN_KEY);
	}

	public String getPartialToken(String token) {
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

}
