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

		String submittedToken = StringUtils.EMPTY;
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().contains(CommonConstants.TOKEN_UNIQUE_ID)) {
				submittedToken = entry.getValue();
				break;
			}
		}

		String tokenSession = TokenManager.getTokenSession();
		String loginId = new FacesScopes().getUserScreenName();
		loginId = (loginId == null || loginId.isEmpty()) ? "anonymous"
				: loginId;
		boolean isValid = TokenManager.isValidToken(submittedToken);
		String viewId = context.getViewRoot().getViewId();

		log.info("#38229||" + isValid + "||"
				+ TokenManager.getPartialToken(submittedToken) + "||"
				+ TokenManager.getPartialToken(tokenSession) + "||" + loginId
				+ "||" + viewId);

		if (!isValid) {
			String message = ResourceBundleUtils.getMessage(
					"lbl.sc.procurementtype.tooltip",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, message));
			// Show error message
//			RequestContext.getCurrentInstance().execute(
//					CommonConstants.GLOBAL_MESSAGE_ID + ".show()");
			context.renderResponse(); // Block further processing
		}

	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}
}
