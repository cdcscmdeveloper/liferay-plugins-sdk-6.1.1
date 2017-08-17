package my.ep.liferay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceWrapper;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class ExtLayoutLocalServiceWrapper extends LayoutLocalServiceWrapper {

	public static Logger log = Logger.getLogger(ExtLayoutLocalServiceWrapper.class);
	
	public final String RENEW_MOF_URL = "/renew-mof-account";
	public final String REGISTER_MOF_URL = "/upgrade-basic-to-mof";
	
	public final String BASIC_SUPPLIER_ADMIN = "BASIC_SUPPLIER_ADMIN";
	public final String BASIC_SUPPLIER_USER = "BASIC_SUPPLIER_USER";
	
	public final String USER_ROLE_SESSION_KEY = "USER_ROLE_SESSION_KEY";
	
	public ExtLayoutLocalServiceWrapper(LayoutLocalService layoutLocalService) {
		super(layoutLocalService);
		// TODO Auto-generated constructor stub
	}

	public List<Layout>	getLayouts(int start, int end) throws SystemException {
		return super.getLayouts(start, end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout));		
		Iterator<Layout> iter = layouts.iterator();
		
		Integer validBasicToRenewMOF = validBasicToRenewMOF();
		System.out.println("11 validBasicToRenewMOF(): " + validBasicToRenewMOF);
		
		while (iter.hasNext()) {
			Layout layout = iter.next();
			
			if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {

				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
					System.out.println("11 removing register MOF");
					iter.remove();
				}
				
				if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
					System.out.println("11 removing renew MOF");
					iter.remove();
				}
			}
		}
		
		/*if(!isBasicSupplierUser()) {
			log.info("inside not Basic");
			return super.getLayouts( groupId,  privateLayout);
		} else {
			log.info("inside Basic");
			for(Layout layout : super.getLayouts( groupId,  privateLayout)) {
				log.info(layout.getFriendlyURL());
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					log.info("IN RENEW AND REGISTER");
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && validBasicToRenewMOF()) {
						log.info("IN RENEW");
						layouts.add(layout);
					} else if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && !validBasicToRenewMOF()) {
						log.info("IN REGISTER");
						layouts.add(layout);
					}
				} else {
					log.info("NOT BOTH");
					layouts.add(layout);
				}
			}
		}*/
		
		/*List<Layout> layouts = new ArrayList<Layout>();
		
		for(Layout layout : super.getLayouts( groupId,  privateLayout)) {
			if(isBasicSupplierUser()) {
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL)) {
					if(validBasicToRenewMOF()) {
						layouts.add(layout);
					}
				} else if(layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					layouts.add(layout);
				} else {
					layouts.add(layout);
				}
			} else {
				layouts.add(layout);
			}			
		}*/
//		remoteLayout(layouts, "/catalogue-search");
		
//		return super.getLayouts( groupId,  privateLayout);
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout, parentLayoutId));
		Iterator<Layout> iter = layouts.iterator();
		
		Integer validBasicToRenewMOF = validBasicToRenewMOF();
		System.out.println("22 validBasicToRenewMOF(): " + validBasicToRenewMOF);
		
		while (iter.hasNext()) {
			Layout layout = iter.next();
			
			if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
				
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL)  && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
					System.out.println("22 removing register MOF");
					iter.remove();
				}
				
				if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
					System.out.println("22 removing renew MOF");
					iter.remove();
				}
			}
		}
		
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long[] layoutIds) throws SystemException, PortalException {

		return super.getLayouts( groupId,  privateLayout, layoutIds);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId, boolean incomplete, int start, int end) throws SystemException {

		return super.getLayouts( groupId,  privateLayout,  parentLayoutId,  incomplete,  start,  end);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, String type) throws SystemException {

		return super.getLayouts( groupId,  privateLayout,  type);
	}
	
	public List<Layout>	getNullFriendlyURLLayouts() throws SystemException {
		return super.getNullFriendlyURLLayouts();
	}
	
	public List<Layout>	getScopeGroupLayouts(long groupId, boolean privateLayout) throws SystemException {

		return super.getScopeGroupLayouts(groupId, privateLayout);
	}
	
	private Integer validBasicToRenewMOF() {
		
		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
		long logedInUserId = 0;
		if(serviceContext != null) {
			logedInUserId = serviceContext.getUserId();
		}
		
		if(PortalSessionThreadLocal.getHttpSession() != null) {
			if(PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF") != null) {
				System.out.println("returning value from http session: " + PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF"));
				return (Integer)PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF");
			}
		}
			
		if(FacesContext.getCurrentInstance() != null) {
			
			/*if(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("validToRenewMOF") != null) {
				return (boolean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("validToRenewMOF");
			}*/
			
			long userId = 0;
			if(new FacesScopes().getUserSessionBean() != null) {
				userId = new FacesScopes().getUserSessionBean().getUserId();
			}
			
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setUserId(userId);
			
			IMofAccountSO mofAccountSO = null;
			
			try {
				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
				MofAccountResDTO resDTO = mofAccountSO.checkExpiredDowngradedToBasic(reqDTO);
				log.info("resDTO.isExpired(): " + resDTO.getBasicMOFRenewFlg());
				
				PortalSessionThreadLocal.getHttpSession().setAttribute(logedInUserId + "validToRenewMOF", resDTO.getBasicMOFRenewFlg());
				return (Integer)PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF");
				
//				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("validToRenewMOF", resDTO.isExpired());
//				return (boolean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("validToRenewMOF");
			} finally {
				mofAccountSO = null;
			}
		}
		return SupplierManagementConst.BASIC_ONLY;
	}	
	
	public boolean isBasicSupplierUser() {
		List<Role> loginUserRoles = getUserRoleList();
		log.info("loginUserRoles: " + loginUserRoles);
		for (Role r : loginUserRoles) {
			log.info("role name: " + r.getName());
			if(StringUtils.containsAny(r.getName(), "BASIC")) {
				log.info(r.getName() + " is BASIC");
				return true;
			}
		}
		
		/*if(hasRoleUser(BASIC_SUPPLIER_ADMIN)) {
			return true;
		}
		if(hasRoleUser(BASIC_SUPPLIER_USER)) {
			return true;
		}*/
		return false;
	}

	public boolean hasRoleUser(String roleName) {

		boolean hasRoleUser = false;
		if(FacesContext.getCurrentInstance() != null) {
	        ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
	        long portletGroupId= themeDisplay.getScopeGroupId();
	        long userId = themeDisplay.getUserId();
	        try {
	        	hasRoleUser = UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, portletGroupId, roleName, true);
	        } catch(Exception e) {
	        	System.err.println(e);
	        }        
		}
		return hasRoleUser;
	}
	
	public String getScreenName() {

		if(FacesContext.getCurrentInstance() != null) {
			ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
			if(themeDisplay != null) {
				if(themeDisplay.getUser() != null) {
					return themeDisplay.getUser().getScreenName();
				}
			}
		}
		return null;
	}
	
	public List<Role> getUserRoleList() {
		List<Role> userRoleList = new ArrayList<Role>();
		try {
			if(FacesContext.getCurrentInstance() != null) {
				if(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USER_ROLE_SESSION_KEY) != null) {
					System.out.println("Getting role session");
					userRoleList = (List<Role>)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USER_ROLE_SESSION_KEY);
				} else {
				
					ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
					if(themeDisplay != null) {
						if(themeDisplay.getUser() != null) {
							
							User user = UserServiceUtil.getUserById(themeDisplay.getUser().getUserId());
	//						userRoleList = user.getRoles();
							List<UserGroupRole> userGroupRoleList = new ArrayList<UserGroupRole>();
							for(Group group : user.getMySites()) {
								userGroupRoleList.addAll(UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId(), group.getGroupId()));
							}
									
							System.out.println("userGroupRoleList: " + userGroupRoleList);
							for (UserGroupRole userGroupRole : userGroupRoleList) {
						        /* Get Role object based on userGroupRole.getRoleId() */
						        Role role = RoleLocalServiceUtil.getRole(userGroupRole.getRoleId());
						        
						        System.out.println("role: " + role.getName());
						        userRoleList.add(role);
						    }
							if(userRoleList != null && userRoleList.size() > 0){
								System.out.println("userRoleList adding to session: " + userRoleList);
								FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(USER_ROLE_SESSION_KEY, userRoleList);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
		return userRoleList;
	}
	
	private void getHttpSession() {
		if(FacesContext.getCurrentInstance() != null) {
			ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
			PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
			HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
			
			HttpSession session = httpServletRequest.getSession();
		}
	}
	
/*	public static void main(String[] args) {
		String roleName = "BASIC_SUPPLIER_ADMIN";
		System.out.println("roleName.indexOf(\"BASIC\"): " + roleName.indexOf("BASIC"));
		System.out.println("ddddd" + StringUtils.containsAny(roleName, "BASIC"));
		
		if(roleName.indexOf("B") > 0) {
			System.out.println(true);
		}
	}*/
}
