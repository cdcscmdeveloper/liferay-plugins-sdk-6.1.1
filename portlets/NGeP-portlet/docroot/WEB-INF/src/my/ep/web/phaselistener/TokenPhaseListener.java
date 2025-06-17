package my.ep.web.phaselistener;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import my.ep.constants.CommonConstants;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.TokenManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

public class TokenPhaseListener implements PhaseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TokenPhaseListener.class);

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.PROCESS_VALIDATIONS;
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (!context.isPostback())
			return;

		TokenManager tokenManager = new TokenManager();
		String submittedToken = StringUtils.EMPTY;
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().contains(CommonConstants.TOKEN_UNIQUE_ID)) {
				submittedToken = entry.getValue();
				break;
			}
		}

		String tokenSession = tokenManager.getTokenSession();
		String loginId = new FacesScopes().getUserScreenName();
		loginId = (loginId == null || loginId.isEmpty()) ? "anonymous"
				: loginId;
		boolean isValid = tokenManager.isValidToken(submittedToken);
		String viewId = "N/A";
		if (context.getViewRoot() != null) {
			viewId = context.getViewRoot().getViewId();
		}
		PhaseId currentPhaseId = event.getPhaseId();

		log.info("#38229||" + isValid + "||" + currentPhaseId + "||"
				+ tokenManager.getPartialToken(submittedToken) + "||"
				+ tokenManager.getPartialToken(tokenSession) + "||" + loginId
				+ "||" + viewId);

		if (!isValid && currentPhaseId != PhaseId.RESTORE_VIEW) {
			String message = ResourceBundleUtils.getMessage(
					"lbl.back.btn.err.msg",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String header = ResourceBundleUtils.getMessage(
					"lbl.back.btn.err.header",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			// context.addMessage(null, new FacesMessage(
			// FacesMessage.SEVERITY_ERROR, null, message));
			// Show error message
			RequestContext.getCurrentInstance().execute(
					"showCustomDialog('" + header + "', '" + message + "')");
			log.info("#38229 after blocking for " + loginId);
			context.renderResponse(); // Block further processing
		}

	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}
}
