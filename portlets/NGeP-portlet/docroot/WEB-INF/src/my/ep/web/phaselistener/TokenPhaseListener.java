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

		if (!TokenManager.isValidToken(submittedToken)) {
			log.info(">>>>>>>>>>>>>>>>>>>>#38229 token is not valid!!");
//			RequestContext.getCurrentInstance().execute(
//					CommonConstants.GLOBAL_MESSAGE_ID + ".show()");
			context.renderResponse(); // Block further processing
		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}
}
