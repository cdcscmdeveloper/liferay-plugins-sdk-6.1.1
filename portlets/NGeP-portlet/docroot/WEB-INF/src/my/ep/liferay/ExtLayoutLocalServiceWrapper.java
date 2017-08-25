package my.ep.liferay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalService;
import com.liferay.portal.service.LayoutLocalServiceWrapper;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

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

	/*public List<Layout>	getLayouts(int start, int end) throws SystemException {
		return super.getLayouts(start, end);
	}*/
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout));	
		
		
		if(getLoggedInUserId() > 0 && isBasicSupplier(getLoggedInUserId()) ) {
		
			Iterator<Layout> iter = layouts.iterator();
			
			Integer validBasicToRenewMOF = validBasicToRenewMOF();
			log.info("11 validBasicToRenewMOF(): " + validBasicToRenewMOF);
			
			while (iter.hasNext()) {
				Layout layout = iter.next();
				
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
	
					if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("11 removing register MOF");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("11 removing renew MOF");
						iter.remove();
					}
				}
			}
		}
		
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout, parentLayoutId));
		
		if(getLoggedInUserId() > 0 && isBasicSupplier(getLoggedInUserId()) ) {
			Iterator<Layout> iter = layouts.iterator();
			
			Integer validBasicToRenewMOF = validBasicToRenewMOF();
			log.info("22 validBasicToRenewMOF(): " + validBasicToRenewMOF);
			
			while (iter.hasNext()) {
				Layout layout = iter.next();
				
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL)) {
					
					if(layout.getFriendlyURL().equals(REGISTER_MOF_URL)  && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("22 removing register MOF");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("22 removing renew MOF");
						iter.remove();
					}
				}
			}
		}
		
		return layouts;
	}
	

	private Integer validBasicToRenewMOF() {
		
		long logedInUserId = getLoggedInUserId();
		
		if(PortalSessionThreadLocal.getHttpSession() != null) {
			if(PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF") != null) {
				log.info("returning value from http session: " + PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF"));
				return (Integer)PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF");
			}
		}
		
		if(logedInUserId > 0) {
			log.info("current logged in user Id: " + logedInUserId);
			try {
				User user = UserLocalServiceUtil.getUser(logedInUserId);
				if(user != null) {
					String loginId = user.getScreenName();
					if(loginId != null) {
						MofAccountReqDTO reqDTO = new MofAccountReqDTO();
						reqDTO.setLoginId(loginId);
						
						IMofAccountSO mofAccountSO = null;
						
						try {
							mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
							MofAccountResDTO resDTO = mofAccountSO.checkExpiredDowngradedToBasic(reqDTO);
							log.info("Supplier Status: " + resDTO.getBasicMOFRenewFlg());
							
							PortalSessionThreadLocal.getHttpSession().setAttribute(logedInUserId + "validToRenewMOF", resDTO.getBasicMOFRenewFlg());
							return (Integer)PortalSessionThreadLocal.getHttpSession().getAttribute(logedInUserId + "validToRenewMOF");
	
						} finally {
							mofAccountSO = null;
						}
					}
				}
			} catch(SystemException se) {
				log.error(se.getMessage());
			} catch(PortalException pe) {
				log.error(pe.getMessage());
			}
		}
		
		/*if(FacesContext.getCurrentInstance() != null) {

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

			} finally {
				mofAccountSO = null;
			}
		}*/
//		return SupplierManagementConst.BASIC_ONLY;
		return SupplierManagementConst.MOF_CAN_RENEW;
	}	
	
	public long getLoggedInUserId() {
		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
//		long logedInUserId = 0;
		if(serviceContext != null) {
			return serviceContext.getUserId();
		}
		return 0L;
	}
	
	public boolean isBasicSupplier(long userId) {
		boolean isBasic = false;
        try {
			isBasic = UserGroupRoleLocalServiceUtil.hasUserGroupRole(
					userId,
					Long.valueOf(
							EpPropertiesUtils.getInstance().getProperty(
									EpPropertiesConst.PORTAL_SITE_ID))
							.longValue(), BASIC_SUPPLIER_ADMIN, true)
					|| UserGroupRoleLocalServiceUtil
							.hasUserGroupRole(
									userId,
									Long.valueOf(
											EpPropertiesUtils.getInstance().getProperty(
															EpPropertiesConst.PORTAL_SITE_ID))
											.longValue(), BASIC_SUPPLIER_USER, true);
        } catch(Exception e) {
        	System.err.println(e);
        } 
        return isBasic;
	}
	
	/*public boolean isBasicSupplierUser() {
		List<Role> loginUserRoles = getUserRoleList();
		log.info("loginUserRoles: " + loginUserRoles);
		for (Role r : loginUserRoles) {
			log.info("role name: " + r.getName());
			if(StringUtils.containsAny(r.getName(), "BASIC")) {
				log.info(r.getName() + " is BASIC");
				return true;
			}
		}

		return false;
	}*/

	/*public boolean hasRoleUser(String roleName) {

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
	}*/
	
	/*public String getScreenName() {

		if(FacesContext.getCurrentInstance() != null) {
			ThemeDisplay themeDisplay = (ThemeDisplay)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
			if(themeDisplay != null) {
				if(themeDisplay.getUser() != null) {
					return themeDisplay.getUser().getScreenName();
				}
			}
		}
		return null;
	}*/
	
	/*public List<Role> getUserRoleList() {
		List<Role> userRoleList = new ArrayList<Role>();
		try {
			if(FacesContext.getCurrentInstance() != null) {
				if(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(USER_ROLE_SESSION_KEY) != null) {
					log.info("Getting role session");
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
									
							log.info("userGroupRoleList: " + userGroupRoleList);
							for (UserGroupRole userGroupRole : userGroupRoleList) {
						         Get Role object based on userGroupRole.getRoleId() 
						        Role role = RoleLocalServiceUtil.getRole(userGroupRole.getRoleId());
						        
						        log.info("role: " + role.getName());
						        userRoleList.add(role);
						    }
							if(userRoleList != null && userRoleList.size() > 0){
								log.info("userRoleList adding to session: " + userRoleList);
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
	}*/
	
	/*private void getHttpSession() {
		if(FacesContext.getCurrentInstance() != null) {
			ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
			PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
			HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
			
			HttpSession session = httpServletRequest.getSession();
		}
	}*/
	
/*	public static void main(String[] args) {
		String roleName = "BASIC_SUPPLIER_ADMIN";
		System.out.println("roleName.indexOf(\"BASIC\"): " + roleName.indexOf("BASIC"));
		System.out.println("ddddd" + StringUtils.containsAny(roleName, "BASIC"));
		
		if(roleName.indexOf("B") > 0) {
			System.out.println(true);
		}
	}*/
}
