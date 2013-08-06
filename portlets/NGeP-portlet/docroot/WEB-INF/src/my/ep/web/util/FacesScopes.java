package my.ep.web.util;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * FacesScopes is a helper class to access FacesContext instance of the current
 * request.
 * 
 * @created 25.03.2008
 */
public class FacesScopes {
	private static Logger log = Logger.getLogger(FacesScopes.class);
	FacesContext facesContext = null;
	Application application = null;
	PortletSession portletSession = null;
	PortletRequest portletRequest = null;

	/**
	 * FacesScopes constructor.
	 */
	public FacesScopes() {

		portletSession = (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		portletRequest = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		facesContext = FacesContext.getCurrentInstance();
		application = FacesContext.getCurrentInstance().getApplication();
	}

	/**
	 * FacesScopes constructor
	 * 
	 * @param createSes
	 *            A boolean instance to indicate to create session if true.
	 */
	public FacesScopes(boolean createSes) {
		portletSession = (PortletSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(createSes);
		portletRequest = (PortletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		facesContext = FacesContext.getCurrentInstance();
		application = FacesContext.getCurrentInstance().getApplication();
	}

	/**
	 * Returns the object bound with the specified name in this session, or null
	 * if no object is bound under the name.
	 * 
	 * @param key
	 *            A String instance specifying the name of the object
	 * @return The object with the specified name
	 */
	public Object getSessionScope(String key) {
		Object obj = portletSession.getAttribute(key);
		return obj;
	}

	/**
	 * Binds an object to this session, using the name specified. If an object
	 * of the same name is already bound to the session, the object is replaced.
	 * 
	 * @param key
	 *            The name to which the object is bound; cannot be null
	 * @param object
	 *            The object to be bound; cannot be null
	 */
	public void setSessionScope(String key, Object object) {
		portletSession.setAttribute(key, object);
	}

	public Object getRequestScope(String key) {
		Object obj = portletRequest.getAttribute(key);
		return obj;
	}

	/**
	 * Stores an attribute in this request. Attributes are reset between
	 * requests. This method is most often used in conjunction with
	 * RequestDispatcher.
	 * 
	 * @param key
	 *            A String instance specifying the name of the attribute
	 * @param object
	 *            The Object to be stored
	 */
	public void setRequestScope(String key, Object object) {
		portletRequest.setAttribute(key, object);
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the
	 * parameter does not exist. Request parameters are extra information sent
	 * with the request. For HTTP servlets, parameters are contained in the
	 * query string or posted form data.
	 * 
	 * @param key
	 *            A String instance specifying the name of the parameter
	 * @return A String instance representing the single value of the parameter
	 */
	public String getParameter(String key) {
		String value = portletRequest.getParameter(key);
		return value;
	}

	/**
	 * Removes the object bound with the specified name from this session. If
	 * the session does not have an object bound with the specified name, this
	 * method does nothing.
	 * 
	 * @param key
	 *            The name of the object to remove from this session
	 */
	public void removeFromSession(String key) {
		portletSession.removeAttribute(key);
	}

	/**
	 * Get Application scope
	 * 
	 * @return The application scope
	 */
	public Application getApplicationScope() {
		return application;
	}

	public PortletRequest getRequest() {
		return portletRequest;
	}

	/**
	 * Get user session bean.
	 * 
	 * @return The UserSessionBean instance
	 */
	/*
	 * public UserSessionBean getUserSessionBean() { return (UserSessionBean)
	 * getSessionScope(SessionConstants.SESS_USER_SESSION_BEAN); }
	 */

	public String getUserScreenName() {
		String screenName = "";
		try {

			//FacesScopes facesScopes = new FacesScopes();
			User user = UserServiceUtil.getUserById(Long.valueOf(this.getRequest().getRemoteUser()));
			screenName = user.getScreenName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return screenName;
	}
	
	public List<Role> getUserRoleList(){
		List<Role> userRoleList = null;
		try{
			User user = UserServiceUtil.getUserById(Long.valueOf(this.getRequest().getRemoteUser()));
			userRoleList = user.getRoles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userRoleList;

	}
	
	public Locale getUserLocale() {
		return ((ThemeDisplay)this.getRequest().getAttribute(WebKeys.THEME_DISPLAY)).getLocale();
	}
	
	
	public UIComponent findComponentById(UIComponent root, String id) {
		UIComponent component = null;
		Iterator<UIComponent> itr = root.getFacetsAndChildren();
		
		while (itr.hasNext() && component == null) {
			component = findComponentById(itr.next(), id);
		}

		if (component == null && id.equals(root.getId())) {
			component = root;
		}
		return component;
	}
}
