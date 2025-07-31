package my.ep.web.util;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;

import my.ep.constants.CommonConstants;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.common.TokenSessionBean;
import my.ep.web.phaselistener.TokenPhaseListener;

import org.apache.log4j.Logger;

public class TokenManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TOKEN_KEY = CommonConstants.TOKEN_UNIQUE_ID;
	private static String BACK_BUTTON_ALLOWED = EpPropertiesUtils.getInstance()
			.getProperty("back.btn.allowed");
	private static Logger log = Logger.getLogger(TokenPhaseListener.class);

	public boolean isBackBtnAllowed() {
		return (BACK_BUTTON_ALLOWED != null && BACK_BUTTON_ALLOWED.trim()
				.equalsIgnoreCase("1")) ? true : false;
	}

	public String generateToken() {
		FacesScopes fs = new FacesScopes();
		String token = UUID.randomUUID().toString();
		fs.setSessionScope(TOKEN_KEY, token);
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

	public String getTokenSessionBean() {
		String tokenSess = "N/A";
		TokenSessionBean tokenSessionBean = (TokenSessionBean) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get("tokenSessionMB");
		tokenSess = tokenSessionBean != null ? tokenSessionBean.getSessToken()
				: tokenSess;
		return tokenSess;
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
