package my.ep.web.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * FacesScopes is a helper class to access FacesContext instance of the current
 * request.
 * 
 * @created 25.03.2008
 */
public class FacesScopes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(FacesScopes.class);

	public static final String SESS_USER_SESSION_BEAN = "USER_SESSION_BEAN";
	private static final String BPM_AUTH_CONTEXT_TOKEN = "BPM_AUTH_CONTEXT_TOKEN";

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
		return facesContext.getExternalContext().getSessionMap().get(key);
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
		facesContext.getExternalContext().getSessionMap().put(key, object);
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
	 * 
	 * @return the object removed.
	 */
	public Object removeFromSession(String key) {
		return facesContext.getExternalContext().getSessionMap().remove(key);
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
	public PmUser getUserSessionBean() {
		PmUser user = (PmUser) getSessionScope(SESS_USER_SESSION_BEAN);
		
		if (user == null) {
			try {
				String loginId = getUserScreenName();

				if (StringUtils.isNotBlank(loginId)) {
					IUserSO userSO;
					try {
						userSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

						UserReqDTO reqDTO = new UserReqDTO();
						reqDTO.setLoginId(loginId);

						UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);

						user = resDTO.getPmUser();
					
					} catch (Exception e) {
						e.printStackTrace();
						throw new EpException(e.getMessage());
					} finally {
						userSO = null;
					}
				}
				
				log.info("stored in user session:" + (user != null ? user.getUserId() : null));
				
				setSessionScope(SESS_USER_SESSION_BEAN, user);
			} catch (Exception e) {
				log.error(e.getMessage());
				
				FacesContext.getCurrentInstance().addMessage(
						null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, 
								e.toString(), 
								e.getMessage()));
			}
		}
		
		if (user == null) {
			user = new PmUser();
		}

		return user;
	}

	public String getUserScreenName() {
		String screenName = "";
		
		if(this.getRequest().getRemoteUser() != null) {
			try {
				User user = UserServiceUtil.getUserById(Long.valueOf(this
						.getRequest().getRemoteUser()));
				screenName = user.getScreenName();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return screenName;
	}

	public List<Role> getUserRoleList() {
		List<Role> userRoleList = null;
		try {
			User user = UserServiceUtil.getUserById(Long.valueOf(this.getRequest().getRemoteUser()));
			userRoleList = user.getRoles();
			
//			PermissionServiceUtil.set
			
//			userRoleList = RoleServiceUtil.getUserRoles(Long.valueOf(this
//					.getRequest().getRemoteUser()));
/*			List<UserGroup> userGroupList = user.getUserGroups();
			for(UserGroup ug : userGroupList) {
				System.out.println(ug.getName());
			}*/
			
//			user.get
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userRoleList;

	}

	/**
	 * check whether the current login user has the passed in role
	 * 
	 * @param roleId
	 * @return
	 */
	public boolean hasRoleUser(long roleId) {
		boolean hasRoleUser = false;
		try {
			hasRoleUser = UserServiceUtil.hasRoleUser(roleId,
					Long.valueOf(this.getRequest().getRemoteUser()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasRoleUser;
	}

	/**
	 * check whether the current login user has the passed in role
	 * 
	 * @param roleName
	 * @return
	 */
	public boolean hasRoleUser(String roleName) {

		boolean hasRoleUser = false;

        ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
        long portletGroupId= themeDisplay.getScopeGroupId();
        long userId = themeDisplay.getUserId();
        try {
        	hasRoleUser = UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, portletGroupId, roleName, true);
        } catch(Exception e) {
        	System.err.println(e);
        }        
        
		return hasRoleUser;
	}

	public Locale getUserLocale() {
		return ((ThemeDisplay) this.getRequest().getAttribute(
				WebKeys.THEME_DISPLAY)).getLocale();
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

	public static boolean isValueReference(String value) {
		if (value == null)
			throw new NullPointerException("value");

		int start = value.indexOf("#{");
		if (start < 0)
			return false;

		int end = value.lastIndexOf('}');
		return (end >= 0 && start < end);
	}

	public Object getBPMAuthenticationCtxToken() {
		Object ctxToken = getSessionScope(BPM_AUTH_CONTEXT_TOKEN);
		if (null == ctxToken) {
			String loginId = getUserScreenName();
			System.out.println("loginId: " + loginId);
			
			if (loginId != null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				ctxToken = bpmSO.authenticate(loginId);
				setSessionScope(BPM_AUTH_CONTEXT_TOKEN, ctxToken);
			}
		}
		return ctxToken;
	}
	
	public Object removeBPMAuthenticationCtxToken() {
		return removeFromSession(BPM_AUTH_CONTEXT_TOKEN);
	}

	public static MethodExpression createMethodExpression(FacesContext context, String methodExpression,
			Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
		if (!isValueReference(methodExpression)) {
			methodExpression = "#{" + methodExpression + "}";
		}

		return context.getApplication().getExpressionFactory()
				.createMethodExpression(context.getELContext(),
						methodExpression, expectedReturnType,
						expectedParamTypes);
	}
	
	public HttpServletRequest getOriginalServletRequest() {
		HttpServletRequest request = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext
				  .getCurrentInstance().getExternalContext().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(request);
		
		return originalServletRequest;
	}
	
	
	
	public static void preRenderErrorMessage(String errorMsg){
		
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils
						.getMessage(MessageConst.ERROR_700200, new String[] { ""
								+ errorMsg },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "/xhtml/cm/catalogue/preRenderErrorPage.xhtml");
		
	}

	/**
	 * extend Liferay session by calling Liferay.Session.extend() on every request to the backing bean.
	 */
	public void extendLiferaySession() {
		log.info("in extendLiferaySession");
		if(this.getUserSessionBean() != null && this.getUserSessionBean().getUserId() != null) {
			if(RequestContext.getCurrentInstance() != null) {
				log.info("extending liferay session");
				RequestContext.getCurrentInstance().execute("extendLiferaySession();");	
			}
		}
	}
}

