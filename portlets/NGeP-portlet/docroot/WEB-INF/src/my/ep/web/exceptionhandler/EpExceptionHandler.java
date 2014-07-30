/**
 * 
 */
package my.ep.web.exceptionhandler;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

import my.ep.constant.MessageConst;
import my.ep.exception.EpBpmException;
import my.ep.exception.EpException;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * @author siew mei
 * 
 */
public class EpExceptionHandler extends ExceptionHandlerWrapper {
	private static Logger log = Logger.getLogger(EpExceptionHandler.class);
	private ExceptionHandler wrapped;
	//private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("messageprovider-config.bundles.message.common.message-common");
	
	public EpExceptionHandler(ExceptionHandler eh) {
		wrapped = eh;
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExceptionHandler getWrapped() {
		// TODO Auto-generated method stub
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {
		if (log.isDebugEnabled()) {
			log.debug("In handle");
		}

		if (log.isDebugEnabled()) {
			log.debug("getUnhandledExceptionQueuedEvents: " + getUnhandledExceptionQueuedEvents());
		}

		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents()
				.iterator();

		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();			
			Throwable exception = event.getContext().getException();

			/*final FacesContext fc = FacesContext.getCurrentInstance();
         	final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
         	final NavigationHandler nav = fc.getApplication().getNavigationHandler();*/

			// here you do what ever you want with exception
			try {
				handleException(exception);
			} finally {
				// remove it from queue
				i.remove();
			}
		}

		// parent hanle
		//getWrapped().handle();

		if (log.isDebugEnabled()) {
			log.debug("End handle");
		}

	}
	
	public void handleException(Throwable exception) {
		// exception.printStackTrace();
		log.error(exception.getMessage(), exception);
		
		Locale userLocale = new FacesScopes().getUserLocale();
		String msgCode = MessageConst.ERROR_000001;
		int bpmcode = 0;
		String componentId = null;
		//String componentId = (event.getContext()!=null && event.getContext().getComponent()!=null)?event.getContext().getComponent().getId():null;
		Object[] params = null;
		@SuppressWarnings("unchecked")
		List<Throwable> throwables = ExceptionUtils
				.getThrowableList(exception);

		while (throwables.size() > 0) {
			// get the root exception from context
			Throwable rootException = throwables.remove(throwables
					.size() - 1);

			if (log.isDebugEnabled()) {
				log.debug("Throwable: " + rootException);
				// log.debug("Message: " + rootException.getMessage());
			}

			if (rootException instanceof EpException) {
				EpException epex = (EpException) rootException;
				msgCode = epex.getMessage();
				params = epex.getParameters();
				// FIXME - have to set the component id if wan to let
				// the exception decide where to display the msg
				break;
			} else if (rootException instanceof EpBpmException) {
				EpBpmException epex = (EpBpmException) rootException;
				msgCode = epex.getMessage();
				bpmcode = epex.getCode();
				break;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("msgCode: " + msgCode);
		}

		// String message = BUNDLE.getString(msgCode);
		String message = MessageUtils.getMessage(msgCode, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);
		// t.getCause().getMessage()
		// log error ?
		// log.log(Level.SEVERE, "Critical Exception!", t);

		// redirect error page
		// requestMap.put("exceptionMessage", t.getMessage());
		// nav.handleNavigation(fc, null, "/error");
		// fc.renderResponse();

		// remove the comment below if you want to report the error in a
		// jsf error message
		// JsfUtil.addErrorMessage(t.getMessage());
		// ErrorBean bean = new ErrorBean();
		// bean.setMessage(ExceptionUtils.getRootCauseMessage(t));

		if (!msgCode.equals(message)) {
			msgCode += ": " + message;
		}

		if (bpmcode > 0) {
			msgCode = MessageUtils.getMessage(
					MessageConst.ERROR_BPM_CODE
							+ String.valueOf(bpmcode), params,
					MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);
		}

		FacesContext.getCurrentInstance().addMessage(
				componentId,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						msgCode));
		// FacesScopes facesScopes = new FacesScopes();
		// facesScopes.setRequestScope("errorBean", bean);
	}
}
