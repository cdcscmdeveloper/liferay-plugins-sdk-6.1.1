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

		if (currentPhaseId == PhaseId.RESTORE_VIEW) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 RESTORE_VIEW phase submittedToken:"
					+ submittedToken
					+ "  tokenSession:"
					+ tokenSession
					+ "  loginId:" + loginId);
		} else if (currentPhaseId == PhaseId.APPLY_REQUEST_VALUES) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 APPLY_REQUEST_VALUES phase submittedToken:"
					+ submittedToken
					+ "  tokenSession:"
					+ tokenSession
					+ "  loginId:" + loginId);
		} else if (currentPhaseId == PhaseId.PROCESS_VALIDATIONS) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 PROCESS_VALIDATIONS phase submittedToken:"
					+ submittedToken
					+ "  tokenSession:"
					+ tokenSession
					+ "  loginId:" + loginId);
		} else if (currentPhaseId == PhaseId.INVOKE_APPLICATION) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 INVOKE_APPLICATION phase submittedToken:"
					+ submittedToken
					+ "  tokenSession:"
					+ tokenSession
					+ "  loginId:" + loginId);
		} else if (currentPhaseId == PhaseId.RENDER_RESPONSE) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 RENDER_RESPONSE phase submittedToken:"
					+ submittedToken
					+ "  tokenSession:"
					+ tokenSession
					+ "  loginId:" + loginId);
		}

	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}
}
