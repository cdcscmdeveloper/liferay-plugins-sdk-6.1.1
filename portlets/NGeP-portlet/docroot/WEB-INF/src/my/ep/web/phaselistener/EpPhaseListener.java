/**
 * 
 */
package my.ep.web.phaselistener;

import javax.faces.event.PhaseEvent;

import org.apache.log4j.Logger;

import com.liferay.faces.util.lifecycle.DebugPhaseListener;

/**
 * @author siew mei
 *
 */
public class EpPhaseListener extends DebugPhaseListener {
	private static Logger log = Logger.getLogger(EpPhaseListener.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void afterPhase(PhaseEvent arg0) {
		if (log.isDebugEnabled()){
			log.debug("afterPhase.phaseId: " + arg0.getPhaseId().toString());
		}
		super.afterPhase(arg0);
	}

	@Override
	public void beforePhase(PhaseEvent arg0) {
		if (log.isDebugEnabled()){
			log.debug("beforePhase.phaseId: " + arg0.getPhaseId().toString());
		}
		
		super.beforePhase(arg0);
	}

	
	
	

}
