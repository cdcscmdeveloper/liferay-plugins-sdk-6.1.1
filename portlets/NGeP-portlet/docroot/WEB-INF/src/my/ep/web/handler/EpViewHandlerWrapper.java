package my.ep.web.handler;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SecurityFilterUtil;

import org.apache.log4j.Logger;

public class EpViewHandlerWrapper extends ViewHandlerWrapper {
	private static Logger log = EpLogger.getLogger(EpViewHandlerWrapper.class);

	private ViewHandler wrapped;

	protected void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		// TODO Remove this after bug fixing
		//log.info(message);
	}

	public EpViewHandlerWrapper(ViewHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ViewHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void renderView(FacesContext context, UIViewRoot viewToRender)
			throws IOException, FacesException {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			debugLogger("userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
			// apply security filters.
			SecurityFilterUtil.applyScreenFilters(viewToRender,
					pmUser.getUserId(), EpPortletUtil.getDisplayPortletId());
		}

		super.renderView(context, viewToRender);
	}
}
