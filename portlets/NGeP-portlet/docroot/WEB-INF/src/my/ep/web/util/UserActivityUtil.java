package my.ep.web.util;

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.constant.EpPropertiesConst;
import my.ep.util.EpPropertiesUtils;
import my.epx.bean.UserActivityBean;
import my.epx.monitor.UserActivityLogger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Redmine #31062
 * 
 * UserActivityUtil is used as the utility class to log user activities. 
 * 
 * @author	fadhli
 * @version	1.3
 * @since	2021-07-23
 */
public class UserActivityUtil {
	private static Logger logger = Logger.getLogger(UserActivityUtil.class);
	private static UserActivityUtil instance;
	private static boolean activityLoggingEnabled;
	
	private static final String TRUE = "true";
	private static final String PORTLET_CONFIG = "javax.portlet.config";
	private static final String PORTLET_DEFAULT_VIEW_ID = "javax.portlet.faces.defaultViewId.view";
	private static final String PORTLET_ID = "PORTLET_ID";
	private static final String CURRENT_COMPLETE_URL = "CURRENT_COMPLETE_URL";
	private static final String FRIENDLY_URL = "FRIENDLY_URL";
	private static final String USER_AGENT = "user-agent";
	
	private UserActivityUtil() {
	}
	
	public static UserActivityUtil getInstance() {
		if (null == instance) {
			instance = new UserActivityUtil();
			
			String status = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_USER_ACTIVITY_LOGGING);
			if (TRUE.equalsIgnoreCase(status)) {
				activityLoggingEnabled = true;
			} else {
				activityLoggingEnabled = false;
			}
		}
		return instance;
	}
	
	public void logUserActivity() {
		if (activityLoggingEnabled) {
			FacesScopes facesScopes = new FacesScopes();
			String userScreenName = facesScopes.getUserScreenName();
			logger.info("[logUserActivity] userScreenName: " + userScreenName);
			
			if (null != userScreenName && !StringUtils.EMPTY.equals(userScreenName)) {
				HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest((PortletRequest) FacesContext
						.getCurrentInstance().getExternalContext().getRequest());
				
				HttpServletResponse httpServletResponse = PortalUtil.getHttpServletResponse((PortletResponse) FacesContext
						.getCurrentInstance().getExternalContext().getResponse());
				
				PortletRequest portletRequest = (PortletRequest) FacesContext.getCurrentInstance()
						.getExternalContext().getRequest();
				
				String portletPage = StringUtils.EMPTY;
				Object portletConfig = portletRequest.getAttribute(PORTLET_CONFIG);
				if (null != portletConfig && portletConfig instanceof PortletConfig) {
					portletPage = ((PortletConfig) portletConfig).getInitParameter(PORTLET_DEFAULT_VIEW_ID);
				}
				
				UserActivityBean userActivityBean = new UserActivityBean();
				userActivityBean.setLoginId(userScreenName);
				userActivityBean.setSessionId(portletRequest.getRequestedSessionId());
				userActivityBean.setPortletPage(portletPage);
				userActivityBean.setPortletId(getObjectToString(httpServletRequest.getAttribute(PORTLET_ID)));
				userActivityBean.setRequestMethod(httpServletRequest.getMethod());
				userActivityBean.setRequestedUrl(getObjectToString(httpServletRequest.getAttribute(CURRENT_COMPLETE_URL)));
				userActivityBean.setFriendlyUrl(getObjectToString(httpServletRequest.getAttribute(FRIENDLY_URL)));
				userActivityBean.setUserAgent(httpServletRequest.getHeader(USER_AGENT));
				userActivityBean.setResponseStatus(httpServletResponse.getStatus());
				
				UserActivityLogger.log(userActivityBean);
			}
		}
	}
	
	private static String getObjectToString(Object object) {
		String string = StringUtils.EMPTY;
		if (null != object) {
			string = object.toString();
		}
		return string;
	}
	
	//20210914@fadhli - #31092 - Start
	public void associateRevisionDummyMethod() {
		// This dummy method is created to check issues with Redmine associated revision
		return;
	}
	//20210914@fadhli - #31092 - End
}
