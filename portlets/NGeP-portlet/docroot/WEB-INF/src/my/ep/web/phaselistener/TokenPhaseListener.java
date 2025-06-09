package my.ep.web.phaselistener;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import my.ep.constants.CommonConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.TokenManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TokenPhaseListener implements PhaseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TokenPhaseListener.class);

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (!context.isPostback())
			return;

		String submittedToken = StringUtils.EMPTY;
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().contains(CommonConstants.TOKEN_UNIQUE_ID)) {
				submittedToken = entry.getValue();
				break;
			}
		}
		PhaseId currentPhaseId = event.getPhaseId();

		String tokenSession = TokenManager.getTokenSession();
		String loginId = new FacesScopes().getUserScreenName();
		loginId = (loginId == null || loginId.isEmpty()) ? "anonymous"
				: loginId;
		boolean isValid = true;
		if (isNotEmpty(submittedToken) && isNotEmpty(tokenSession)) {
			isValid = submittedToken.equals(tokenSession);
		}

		log.info(">>>>>>>>>>>>>>>>>>>>#38229 " + currentPhaseId
				+ " submittedToken:"
				+ TokenManager.getPartialToken(submittedToken)
				+ "  tokenSession:"
				+ TokenManager.getPartialToken(tokenSession) + "  loginId:"
				+ loginId + " isValid:" + isValid);

	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}

	private boolean isNotEmpty(String str) {
		if (str != null && str.length() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
