package my.ep.web.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import my.epx.bean.UserActivityBean;
import my.epx.monitor.UserActivityLogger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.portletfaces.bridge.GenericFacesPortlet;
import org.primefaces.context.RequestContext;

import com.liferay.faces.bridge.context.BridgeContextImpl;
import com.liferay.faces.bridge.scope.BridgeRequestScope;
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
	private static final String BPM_AUTH_BYTE_TOKEN = "BPM_AUTH_BYTE_TOKEN";
	private static final String BPM_AUTH_FLAG = "BPM_AUTH_FLAG";

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

	public void logUserActivity() {
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest());
		
		HttpServletResponse httpServletResponse = PortalUtil.getHttpServletResponse((PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse());
		
		UserActivityBean userActivityBean = new UserActivityBean();
		userActivityBean.setLoginId(getUserScreenName());
		userActivityBean.setSessionId(portletRequest.getRequestedSessionId());
		userActivityBean.setPortletPage(getPortletPage());
		userActivityBean.setPortletId(getObjectToString(httpServletRequest.getAttribute("PORTLET_ID")));
		userActivityBean.setRequestMethod(httpServletRequest.getMethod());
		userActivityBean.setRequestedUrl(getObjectToString(httpServletRequest.getAttribute("CURRENT_COMPLETE_URL")));
		userActivityBean.setFriendlyUrl(getObjectToString(httpServletRequest.getAttribute("FRIENDLY_URL")));
		userActivityBean.setUserAgent(httpServletRequest.getHeader("user-agent"));
		userActivityBean.setResponseStatus(httpServletResponse.getStatus());
		
		UserActivityLogger.getInstance().log(userActivityBean);
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
	
	public Object getApplicationScope(String key) {
		Object obj = portletSession.getAttribute(key, PortletSession.APPLICATION_SCOPE);
		return obj;
	}

	/**
	 * Stores an attribute in this application session. Attributes are reset between
	 * requests. This method is most often used in conjunction with
	 * RequestDispatcher.
	 * 
	 * @param key
	 *            A String instance specifying the name of the attribute
	 * @param object
	 *            The Object to be stored
	 */
	public void setApplicationScope(String key, Object object) {
		portletSession.setAttribute(key, object, PortletSession.APPLICATION_SCOPE);
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
		PmUser user = (PmUser) getApplicationScope(SESS_USER_SESSION_BEAN);
		
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
						setApplicationScope(SESS_USER_SESSION_BEAN, user);
						
						log.info("[getUserSessionBean] userActivityTracing");
						showAdditionalInformation();
						logUserActivity();

						// update user last login
						if(user != null) {
							reqDTO.setPmUser(resDTO.getPmUser());
							userSO.updateUserLastLogin(reqDTO);							
						}					
					} catch (Exception e) {
						e.printStackTrace();
						throw new EpException(e.getMessage());
					} finally {
						userSO = null;
					}
				}
				
				log.info("stored in user session(" + loginId + "):" + (user != null ? user.getUserId() : null));
				
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
	
	public void showAdditionalInformation() {
		log.info("[showAdditionalInformation] userActivityTracing loginId: " + getUserScreenName());
		
		final Enumeration<String> attributeNames = portletRequest.getAttributeNames();
		String attributeName;
		Object attribute;
		while (attributeNames.hasMoreElements()) {
			attributeName = attributeNames.nextElement();
			attribute = portletRequest.getAttribute(attributeName);
			log.info("[showAdditionalInformation] userActivityTracing attributeName: " + attributeName + " | attribute: " + attribute);
			if (attribute instanceof PortletConfig) {
				PortletConfig pConf = (PortletConfig) attribute;
				final Enumeration<String> pConfParaNames = pConf.getInitParameterNames();;
				String pConfParaName;
				Object pConfPara;
				while (pConfParaNames.hasMoreElements()) {
					pConfParaName = pConfParaNames.nextElement();
					pConfPara = pConf.getInitParameter(pConfParaName);
					log.info("[showAdditionalInformation] userActivityTracing PortletConfig paraName: " + pConfParaName + " | PortletConfig para: " + pConfPara);
				}
			} else if (attribute instanceof ServletRequest) {
				ServletRequest sReq = (ServletRequest) attribute;
				final Enumeration<String> sReqAttNames = sReq.getAttributeNames();
				String sReqAttName;
				Object sReqAtt;
				while (sReqAttNames.hasMoreElements()) {
					sReqAttName = sReqAttNames.nextElement();
					sReqAtt = sReq.getAttribute(sReqAttName);
					log.info("[showAdditionalInformation] userActivityTracing ServletRequest attributeName: " + sReqAttName + " | ServletRequest attribute: " + sReqAtt);
				}
			} else if (attribute instanceof RenderRequest) {
				RenderRequest rReq = (RenderRequest) attribute;
				final Enumeration<String> rReqAttNames = rReq.getAttributeNames();
				String rReqAttName;
				Object rReqAtt;
				while (rReqAttNames.hasMoreElements()) {
					rReqAttName = rReqAttNames.nextElement();
					rReqAtt = rReq.getAttribute(rReqAttName);
					log.info("[showAdditionalInformation] userActivityTracing RenderRequest attributeName: " + rReqAttName + " | RenderRequest attribute: " + rReqAtt);
				}
			}
		}
		
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getAuthType(): " + portletRequest.getAuthType());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getContextPath(): " + portletRequest.getContextPath());
		
		Cookie[] cookies = portletRequest.getCookies();
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getCookies().length: " + cookies.length);
		int i = 0;
		for (Cookie cookie : cookies) {
		    log.info("[showAdditionalInformation] userActivityTracing cookies[" + i + "].getName(): " + cookie.getName() + " | cookies[" + i + "].getValue(): " + cookie.getValue() 
		    		+ " | cookies[" + i + "].getPath(): " + cookie.getPath());
		    i++;
		}
		
		final Enumeration<Locale> locales = portletRequest.getLocales();
		while (locales.hasMoreElements()) {
			log.info("[showAdditionalInformation] userActivityTracing locales.nextElement().getDisplayName(): " + locales.nextElement().getDisplayName());
		}
		
		Map<String, String[]> parameterMap = portletRequest.getParameterMap();
		if (null != parameterMap && !parameterMap.isEmpty()) {
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getParameterMap() is as follows");
			for (String key : parameterMap.keySet()) {
				log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + parameterMap.get(key));
			}
		} else {
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getParameterMap() is null/empty");
		}
		
		final Enumeration<String> parameterNames = portletRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			log.info("[showAdditionalInformation] userActivityTracing parameterNames.nextElement(): " + parameterNames.nextElement());
		}
		
		Map<String, String[]> privateParameterMap = portletRequest.getPrivateParameterMap();
		if (null != privateParameterMap && !privateParameterMap.isEmpty()) {
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getPrivateParameterMap() is as follows");
			for (String privateKey : privateParameterMap.keySet()) {
				log.info("[showAdditionalInformation] userActivityTracing key: " + privateKey + " | value: " + privateParameterMap.get(privateKey));
			}
		} else {
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getPrivateParameterMap() is null/empty");
		}
		
		final Enumeration<String> propertyNames = portletRequest.getPropertyNames();
		String propertyName;
		Object property;
		while (propertyNames.hasMoreElements()) {
			propertyName = propertyNames.nextElement();
			property = portletRequest.getProperty(propertyName);
			log.info("[showAdditionalInformation] userActivityTracing propertyName: " + propertyName + " | property: " + property);
		}
		
		Map<String, String[]> publicParameterMap = portletRequest.getPublicParameterMap();
		if (null != publicParameterMap && !publicParameterMap.isEmpty()) { 
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getPublicParameterMap() is as follows");
			for (String publicKey : publicParameterMap.keySet()) {
				log.info("[showAdditionalInformation] userActivityTracing key: " + publicKey + " | value: " + publicParameterMap.get(publicKey));
			}
		} else {
			log.info("[showAdditionalInformation] userActivityTracing portletRequest.getPublicParameterMap() is null/empty");
		}
		
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getRemoteUser(): " + portletRequest.getRemoteUser());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getRequestedSessionId(): " + portletRequest.getRequestedSessionId());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getResponseContentType(): " + portletRequest.getResponseContentType());
		
		final Enumeration<String> responseContentTypes = portletRequest.getResponseContentTypes();
		while (responseContentTypes.hasMoreElements()) {
			log.info("[showAdditionalInformation] userActivityTracing responseContentTypes.nextElement(): " + responseContentTypes.nextElement());
		}
		
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getScheme(): " + portletRequest.getScheme());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getServerName(): " + portletRequest.getServerName());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getServerPort(): " + portletRequest.getServerPort());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getUserPrincipal(): " + portletRequest.getUserPrincipal());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getWindowID(): " + portletRequest.getWindowID());
		log.info("[showAdditionalInformation] userActivityTracing portletRequest.getWindowState(): " + portletRequest.getWindowState());
		
//		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
//		HttpServletRequest httpServletRequest = getOriginalServletRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext
				  .getCurrentInstance().getExternalContext().getRequest());
		
		log.info("[showAdditionalInformation] userActivityTracing httpServletRequest.getRequestURI(): " + httpServletRequest.getRequestURI());
		log.info("[showAdditionalInformation] userActivityTracing httpServletRequest.getRequestURL(): " + httpServletRequest.getRequestURL());
		log.info("[showAdditionalInformation] userActivityTracing httpServletRequest.getMethod(): " + httpServletRequest.getMethod());
		
		final Enumeration<String> requestHeaderNames = httpServletRequest.getHeaderNames();
		String requestHeaderName;
		Object requestHeader;
		while (requestHeaderNames.hasMoreElements()) {
			requestHeaderName = requestHeaderNames.nextElement();
			requestHeader = httpServletRequest.getHeader(requestHeaderName);
			log.info("[showAdditionalInformation] userActivityTracing requestHeaderName: " + requestHeaderName + " | requestHeader: " + requestHeader);
		}
		
		final Enumeration<String> httpServReqAttNames = httpServletRequest.getAttributeNames();
		String httpServReqAttName;
		Object httpServReqAtt;
		while (httpServReqAttNames.hasMoreElements()) {
			httpServReqAttName = httpServReqAttNames.nextElement();
			httpServReqAtt = httpServletRequest.getAttribute(httpServReqAttName);
			log.info("[showAdditionalInformation] userActivityTracing httpServReqAttName: " + httpServReqAttName + " | httpServReqAtt: " + httpServReqAtt);
			
			if (httpServReqAtt instanceof GenericFacesPortlet) {
				GenericFacesPortlet genericFacesPortlet = (GenericFacesPortlet) httpServReqAtt;
				
				log.info("[showAdditionalInformation] userActivityTracing genericFacesPortlet.getPortletContext(): " + genericFacesPortlet.getPortletContext());
				log.info("[showAdditionalInformation] userActivityTracing genericFacesPortlet.getInitParameterNames(): " + genericFacesPortlet.getInitParameterNames());
			} else if (httpServReqAtt instanceof BridgeContextImpl) {
				BridgeContextImpl bridgeContextImpl = (BridgeContextImpl) httpServReqAtt;
				
				BridgeRequestScope brs = bridgeContextImpl.getBridgeRequestScope();
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getBeganInPhase(): " + brs.getBeganInPhase());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getClass(): " + brs.getClass());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getId(): " + brs.getId());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPortletMode(): " + brs.getPortletMode());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPreservedViewStateParam(): " + brs.getPreservedViewStateParam());
				
				Map<String, String> brsPreservedActionParameterMap = brs.getPreservedActionParameterMap();
				if (null != brsPreservedActionParameterMap && !brsPreservedActionParameterMap.isEmpty()) { 
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getBridgeRequestScope().getPreservedActionParameterMap() is as follows");
					for (String key : brsPreservedActionParameterMap.keySet()) {
						log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + brsPreservedActionParameterMap.get(key));
					}
				} else {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getBridgeRequestScope().getPreservedActionParameterMap() is null/empty");
				}
				
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRenderRedirectURL(): " + bridgeContextImpl.getRenderRedirectURL());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRequestPathInfo(): " + bridgeContextImpl.getRequestPathInfo());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRequestServletPath(): " + bridgeContextImpl.getRequestServletPath());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getResponseNamespace(): " + bridgeContextImpl.getResponseNamespace());
				log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getSavedViewState(): " + bridgeContextImpl.getSavedViewState());
				
				Map<String, Object> bcAttributeMap = bridgeContextImpl.getAttributes();
				if (null != bcAttributeMap && !bcAttributeMap.isEmpty()) { 
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getAttributes() is as follows");
					for (String key : bcAttributeMap.keySet()) {
						log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + bcAttributeMap.get(key));
					}
				} else {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getAttributes() is null/empty");
				}
				
				if (null != bridgeContextImpl.getBridgeConfig()) {
					Map<String, String[]> bcPublicParameterMap = bridgeContextImpl.getBridgeConfig().getPublicParameterMappings();
					if (null != bcPublicParameterMap && !bcPublicParameterMap.isEmpty()) { 
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getBridgeConfig().getPublicParameterMappings() is as follows");
						for (String key : bcPublicParameterMap.keySet()) {
							log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + bcPublicParameterMap.get(key));
						}
					} else {
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getBridgeConfig().getPublicParameterMappings() is null/empty");
					}
				}
				
				Map<String, String[]> bcPreservedActionParams = bridgeContextImpl.getPreservedActionParams();
				if (null != bcPreservedActionParams && !bcPreservedActionParams.isEmpty()) { 
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPreservedActionParams() is as follows");
					for (String key : bcPreservedActionParams.keySet()) {
						log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + bcPreservedActionParams.get(key));
					}
				} else {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPreservedActionParams() is null/empty");
				}
				
				List<String> preFacesRequestAttrNames = bridgeContextImpl.getPreFacesRequestAttrNames();
				if (null != preFacesRequestAttrNames && !preFacesRequestAttrNames.isEmpty()) {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPreFacesRequestAttrNames() is as follows");
					for (String name : preFacesRequestAttrNames) {
						log.info("[showAdditionalInformation] userActivityTracing name: " + name);
					}
				} else {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getPreFacesRequestAttrNames() is null/empty");
				}
				
				Map<String, String> bcRequestHeaderMap = bridgeContextImpl.getRequestHeaderMap();
				if (null != bcRequestHeaderMap && !bcRequestHeaderMap.isEmpty()) { 
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRequestHeaderMap() is as follows");
					for (String key : bcRequestHeaderMap.keySet()) {
						log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + bcRequestHeaderMap.get(key));
					}
				} else {
					log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRequestHeaderMap() is null/empty");
				}
				
				if (null != bridgeContextImpl.getRenderRedirectURL()) {
					Set<String> bcRruParameterNames = bridgeContextImpl.getRenderRedirectURL().getParameterNames();
					if (null != bcRruParameterNames && !bcRruParameterNames.isEmpty()) {
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRenderRedirectURL().getParameterNames() is as follows");
						for (String name : bcRruParameterNames) {
							String para = bridgeContextImpl.getRenderRedirectURL().getParameter(name);
							log.info("[showAdditionalInformation] userActivityTracing name: " + name + " | para: " 
									+ para);
						}
					} else {
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRenderRedirectURL().getParameterNames() is null/empty");
					}
					
					Map<String, String[]> bcRruParameterMap = bridgeContextImpl.getRenderRedirectURL().getParameterMap();
					if (null != bcRruParameterMap && !bcRruParameterMap.isEmpty()) { 
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRenderRedirectURL().getParameterMap() is as follows");
						for (String key : bcRruParameterMap.keySet()) {
							log.info("[showAdditionalInformation] userActivityTracing key: " + key + " | value: " + bcRruParameterMap.get(key));
						}
					} else {
						log.info("[showAdditionalInformation] userActivityTracing bridgeContextImpl.getRenderRedirectURL().getParameterMap() is empty");
					}
				}
			}
		}
		
		final Enumeration<String> httpServReqParaNames = httpServletRequest.getParameterNames();
		String httpServReqParaName;
		Object httpServReqPara;
		while (httpServReqParaNames.hasMoreElements()) {
			httpServReqParaName = httpServReqParaNames.nextElement();
			httpServReqPara = httpServletRequest.getParameter(httpServReqParaName);
			log.info("[showAdditionalInformation] userActivityTracing httpServReqParaName: " + httpServReqParaName + " | httpServReqPara: " + httpServReqPara);
		}
		
		HttpServletResponse httpServletResponse = PortalUtil.getHttpServletResponse((PortletResponse) FacesContext
				  .getCurrentInstance().getExternalContext().getResponse());
		log.info("[showAdditionalInformation] userActivityTracing httpServletResponse.getContentType(): " + httpServletResponse.getContentType());
		log.info("[showAdditionalInformation] userActivityTracing httpServletResponse.getStatus(): " + httpServletResponse.getStatus());
		for (String responseHeaderName : httpServletResponse.getHeaderNames()) {
			log.info("[showAdditionalInformation] userActivityTracing responseHeaderName: " + responseHeaderName 
					+ " | responseHeader: " + httpServletResponse.getHeader(responseHeaderName));
		}
	}

	public String getUserScreenName() {
//		log.info("#25061 getUserScreenName");
		String screenName = "";
		String remoteUser = this.getRequest().getRemoteUser();
//		log.info("#25061 remoteUser : " + remoteUser);
		if(remoteUser != null) {
//			log.info("#25061 remoteUser not null");
			try {
				User user = UserServiceUtil.getUserById(Long.valueOf(this
						.getRequest().getRemoteUser()));
				screenName = user.getScreenName();
//				log.info("#25061 screenName : " + screenName);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return screenName;
	}
	
	private String getObjectToString(Object object) {
		String string = StringUtils.EMPTY;
		if (null != object) {
			string = object.toString();
		}
		return string;
	}
	
	private String getPortletPage() {
		String portletPage = StringUtils.EMPTY;
		Object attributeObject = portletRequest.getAttribute("javax.portlet.config");
		if (null != attributeObject && attributeObject instanceof PortletConfig) {
			portletPage = ((PortletConfig) attributeObject).getInitParameter("javax.portlet.faces.defaultViewId.view");
		}
		
		return portletPage;
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
		Object ctxToken = getApplicationScope(BPM_AUTH_CONTEXT_TOKEN);
		if (null == ctxToken) {
			String loginId = getUserScreenName();
			System.out.println("loginId: " + loginId);
			
			if (loginId != null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				ctxToken = bpmSO.authenticate(loginId);
				setApplicationScope(BPM_AUTH_CONTEXT_TOKEN, ctxToken);
			}
		}
		
		log.info("[getBPMAuthenticationCtxToken] userActivityTracing");
		showAdditionalInformation();
		logUserActivity();
		
		return ctxToken;
	}
	
	public String needAuthenticate(){
		String auth = (String) getApplicationScope(BPM_AUTH_FLAG);
		if(StringUtils.isBlank(auth)){
			setApplicationScope(BPM_AUTH_FLAG, "true");
		}else if(auth.equals("true")){
			auth = "false";
			setApplicationScope(BPM_AUTH_FLAG, "false");
		}
		return auth;
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

