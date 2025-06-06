package my.ep.web.phaselistener;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import my.ep.constants.CommonConstants;
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
		return PhaseId.PROCESS_VALIDATIONS; // Good phase for this
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		if (!context.isPostback()) {
			return; // Only validate on postbacks
		}

		String submittedToken = StringUtils.EMPTY;
		Map<String, String> params = context.getExternalContext()
				.getRequestParameterMap();

		// Iterate to find the token, as its name might be decorated by JSF
		// (e.g., form:token_unique_id)
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().contains(CommonConstants.TOKEN_UNIQUE_ID)) {
				submittedToken = entry.getValue();
				break;
			}
		}

		// Get the token from the session (the one rendered with the page)
		String sessionToken = TokenManager.getSessionToken();

		log.info(">>>>>>>>>>>>>>>>>>>>#38229 sessionToken:" + sessionToken
				+ "  submittedToken:" + submittedToken);

		// Check if the submitted token is valid
		if (submittedToken != null && submittedToken.length() > 0
				&& sessionToken != null && sessionToken.length() > 0
				&& !TokenManager.isValidToken(submittedToken)) {

			// This is the crucial part: if the tokens don't match or the
			// session token is null
			// This indicates a potential back button use or an invalid form
			// submission.
			// You might need to refine the condition depending on how you want
			// to handle null session tokens.

			// Show error message
			// RequestContext.getCurrentInstance().execute(CommonConstants.GLOBAL_MESSAGE_ID
			// + ".show()");

			// Add a FacesMessage for better user feedback
			// context.addMessage(null, new
			// javax.faces.application.FacesMessage(
			// javax.faces.application.FacesMessage.SEVERITY_ERROR,
			// "Invalid request or session expired. Please try again.",
			// "It appears you may have used the browser's back button or the session has expired."));

			// context.renderResponse(); // Block further processing
		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		// No action needed here for this logic
	}
}
