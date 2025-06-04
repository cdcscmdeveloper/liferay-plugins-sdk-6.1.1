package my.ep.web.phaselistener;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.util.MessageUtils;
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
		
//		if (!TokenManager.isValidToken(submittedToken)) {
//			UIViewRoot viewRoot = context.getViewRoot();
//			UIComponent component = findComponentById(viewRoot,
//					CommonConstants.GLOBAL_MESSAGE_ID);
//
//			if (component != null) {
//				String growlClientId = component.getClientId();
//				context.addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
//								MessageUtils.getMessage(
//										MessageConst.ERROR_760556, null,
//										MessageUtils.BUNDLE_COMMON_MESSAGES,
//										new FacesScopes().getUserLocale())));
//
//				context.renderResponse(); // Block further processing
//			}
//		}
	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}

	public UIComponent findComponentById(UIComponent root, String id) {
		UIComponent component = null;
		Iterator<UIComponent> itr = root.getFacetsAndChildren();

		while (itr.hasNext() && component == null) {
			component = findComponentById(itr.next(), id);
		}

		if (component == null && id.contains(root.getId())) {
			component = root;
		}
		return component;
	}
}
