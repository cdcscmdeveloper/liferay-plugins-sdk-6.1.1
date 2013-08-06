package my.ep.web.phaselistener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import my.ep.factory.EpLogger;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.SecurityFilterUtil;

import org.apache.log4j.Logger;

/**
 * This PhaseListner class will load the DataFilter based on screenID and userID to be use for
 * security filter in drop down, look up and paging component
 * 
 * It also will load up ScreenFilter based on screenID and userID
 * 
 * @author Kenny
 * @created 05.12.2012
 *
 */
public class EpSecurityPhaseListener implements PhaseListener {
	private static Logger log = EpLogger.getLogger(EpSecurityPhaseListener.class);
	
	private static final long serialVersionUID = 1L;
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
    }

	@Override
	public void afterPhase(PhaseEvent phaseEvent) {
		debugLogger("afterPhase");
	}

	@Override
	public void beforePhase(PhaseEvent phaseEvent) {
		debugLogger("beforePhase");
		
		FacesContext context = phaseEvent.getFacesContext();
		
		// Init the Data Filters
		SecurityFilterUtil.applyDataFilters(
				EpPortletUtil.getLoginUserId(context), 
				EpPortletUtil.getDisplayPortletId());
	}
	
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
