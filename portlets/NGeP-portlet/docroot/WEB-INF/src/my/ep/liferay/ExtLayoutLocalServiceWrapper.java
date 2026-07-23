package my.ep.liferay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;//Fix - #34367
import javax.servlet.http.HttpSession;//Fix - #34367

import my.ep.biz.service.pm.authorization.user.IUserSO;//Fix - #34367
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;//Fix - #34367
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;//Fix - #34367
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;//Fix - #34367
import my.ep.dto.pm.authorization.user.UserResDTO;//Fix - #34367
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;//Fix - #34367
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

	private static final Logger log = Logger.getLogger(ExtLayoutLocalServiceWrapper.class);
	
	public static final String RENEW_MOF_URL = "/renew-mof-account";
	public static final String REGISTER_MOF_URL = "/upgrade-basic-to-mof";
	//#35580 - Start
	public static final String BASIC_ACCOUNT_STATUS_URL = "/basic-account-deactivation";
	public static final String CANCEL_MOF_URL = "/cancel-mof-registered";
	//#35580 - End
	
	public static final String BASIC_SUPPLIER_ADMIN = "BASIC_SUPPLIER_ADMIN";
	public static final String BASIC_SUPPLIER_USER = "BASIC_SUPPLIER_USER";
	
	public static final String USER_ROLE_SESSION_KEY = "USER_ROLE_SESSION_KEY";
	
	public static final String PANEL_CIRCULAR_ADMIN = "BPK_PCA";//Fix - #34367
	private static final String SESSION_CONSTANT_RULE ="CT_PANEL_CIRCULAR_CONSTANT_RULE";//Fix - #34367
	
	public ExtLayoutLocalServiceWrapper(LayoutLocalService layoutLocalService) {
		super(layoutLocalService);
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout));	
		
		
		if(getLoggedInUserId() > 0 && isBasicSupplier(getLoggedInUserId()) ) {
		
			Iterator<Layout> iter = layouts.iterator();
			
			Integer validBasicToRenewMOF = validBasicToRenewMOF();
			log.info("11 validBasicToRenewMOF(): " + validBasicToRenewMOF);
			
			while (iter.hasNext()) {
				Layout layout = iter.next();
				
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL) || layout.getFriendlyURL().equals(BASIC_ACCOUNT_STATUS_URL) || layout.getFriendlyURL().equals(CANCEL_MOF_URL)) {
	
					if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("11 removing register MOF");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("11 removing renew MOF");
						iter.remove();
					}
					//#35580 - Start
					if(layout.getFriendlyURL().equals(BASIC_ACCOUNT_STATUS_URL) && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("11 removing basic account status");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(CANCEL_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("11 removing cancel mof");
						iter.remove();
					}
					//35580 - End
				}
			}
		}
		
		return layouts;
	}
	
	public List<Layout>	getLayouts(long groupId, boolean privateLayout, long parentLayoutId) throws SystemException {
		
		List<Layout> layouts = new ArrayList<Layout>();
		layouts.addAll(super.getLayouts( groupId,  privateLayout, parentLayoutId));
		
		long loginIdValue = getLoggedInUserId();//Fix - #34367
		if(getLoggedInUserId() > 0 && isBasicSupplier(getLoggedInUserId()) ) {
			Iterator<Layout> iter = layouts.iterator();
			
			Integer validBasicToRenewMOF = validBasicToRenewMOF();
			log.info("22 validBasicToRenewMOF(): " + validBasicToRenewMOF);
			
			while (iter.hasNext()) {
				Layout layout = iter.next();
				
				if(layout.getFriendlyURL().equals(REGISTER_MOF_URL) || layout.getFriendlyURL().equals(RENEW_MOF_URL) || layout.getFriendlyURL().equals(BASIC_ACCOUNT_STATUS_URL) || layout.getFriendlyURL().equals(CANCEL_MOF_URL)) {
					
					if(layout.getFriendlyURL().equals(REGISTER_MOF_URL)  && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("22 removing register MOF");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(RENEW_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("22 removing renew MOF");
						iter.remove();
					}					
					//#35580 - Start
					if(layout.getFriendlyURL().equals(BASIC_ACCOUNT_STATUS_URL) && validBasicToRenewMOF == SupplierManagementConst.MOF_CAN_RENEW) {
						log.info("22 removing basic account status");
						iter.remove();
					}
					
					if(layout.getFriendlyURL().equals(CANCEL_MOF_URL) && ( validBasicToRenewMOF == SupplierManagementConst.BASIC_ONLY || validBasicToRenewMOF == SupplierManagementConst.MOF_EXPIRED_CANNOT_RENEW )) {
						log.info("22 removing cancel mof");
						iter.remove();
					}
					//#35580 - End
				}
			}
		} else if (loginIdValue > 0 && (isPanelCircular(loginIdValue) && !hasValidConstantRuleCached())) { //Start Fix - #34367
			layouts = CtPanelCircularLayoutFilter.filterLayouts(layouts);
		}
		//End Fix - #34367
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
	
						} catch(Exception e) {
							log.error(e.getMessage());
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
		
		return SupplierManagementConst.MOF_CAN_RENEW;
	}	
	
	public long getLoggedInUserId() {
		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
		if(serviceContext != null) {
			return serviceContext.getUserId();
		}
		return 0L;
	}
	
	public boolean isBasicSupplier(long userId) {
		boolean isBasic = false;
		try {
			isBasic = UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, Long.valueOf(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_SITE_ID)).longValue(),
					BASIC_SUPPLIER_ADMIN, true)
					|| UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, Long.valueOf(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_SITE_ID)).longValue(),
							BASIC_SUPPLIER_USER, true);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return isBasic;
	}
	// Start - #34367
	/**
	 * Determines whether the logged-in user belongs to at least one active
	 * organization profile that is associated with a constant rule.
	 *
	 * <p>The user's organization profiles are retrieved and evaluated against
	 * the configured constant rules. This method returns {@code true} as soon
	 * as a matching rule is found. If no applicable rule exists or an error
	 * occurs during processing, {@code false} is returned.</p>
	 *
	 * @return {@code true} if the logged-in user has at least one applicable
	 *         constant rule; otherwise {@code false}
	 */
	public boolean hasValidConstantRule() {
		long userId = getPmUserId();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IRuleSO.class);
		try {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(userId);
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			for (PmUserOrg orgProfile : userResDTO.getUserOrgList()) {
				if (orgProfile == null || orgProfile.getPmOrgProfile() == null) {
					continue;
				}
				Long orgProfileId = orgProfile.getPmOrgProfile().getOrgProfileId();
				log.info("Checking constant rule for organization profile ID: " + orgProfileId);
				if (ruleSO.getUserRulesConst(orgProfileId) != null) {
					return true;
				}
			}
		} catch (Exception e) {
			log.error("Error while checking the user's constant rule validity.", e);
		}
		return false;
	}
	
	
	
	/**
	 * Retrieves the application user ID associated with the currently logged-in
	 * Liferay user.
	 *
	 * <p>The current Liferay user ID is retrieved from the portal context and
	 * converted to the application's user ID by using the user's screen name
	 * to query the Profile Management service.</p>
	 *
	 * <p>If the logged-in user cannot be found or no corresponding application
	 * user exists, this method returns {@code 0}.</p>
	 *
	 * @return the application user ID if found; otherwise {@code 0}
	 */
	public long getPmUserId() {

	    try {
	        long loggedInUserId = getLoggedInUserId();
	        User user = UserLocalServiceUtil.getUser(loggedInUserId);
	        UserReqDTO reqDTO = new UserReqDTO();
	        reqDTO.setLoginId(user.getScreenName());
	        IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	        UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
	        if (resDTO != null && resDTO.getPmUser() != null) {
	            return resDTO.getPmUser().getUserId();
	        }
	    } catch (Exception e) {
	        log.error("Error while retrieving PM user ID for logged-in user.", e);
	    }
	    return 0L;
	}
	
	
	public boolean isPanelCircular(long userId) {
		boolean isPanel = false;
		try {
			isPanel = UserGroupRoleLocalServiceUtil.hasUserGroupRole(userId, Long.valueOf(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_SITE_ID)).longValue(),
					PANEL_CIRCULAR_ADMIN, true);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return isPanel;
	}
	
	
	/**
	 * Retrieves the cached constant rule validity status from the current user
	 * session.
	 *
	 * <p>If the validation result is not available in the session, this method
	 * executes {@link #hasValidConstantRule()} once and stores the result for
	 * subsequent requests during the same session.</p>
	 *
	 * <p>The cached value will be removed automatically when the user session
	 * expires or the user logs out.</p>
	 *
	 * @return {@code true} if the logged-in user has a valid constant rule;
	 *         otherwise {@code false}
	 */
	public boolean hasValidConstantRuleCached() {

	    ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
	    if (serviceContext == null || serviceContext.getRequest() == null) {
	        return hasValidConstantRule();
	    }
	    HttpServletRequest request = serviceContext.getRequest();
	    HttpSession session = request.getSession();
	    Boolean hasConstantRule = (Boolean) session.getAttribute(SESSION_CONSTANT_RULE);
	    if (hasConstantRule == null) {
	        hasConstantRule = hasValidConstantRule();
	        session.setAttribute(SESSION_CONSTANT_RULE, hasConstantRule);
	    }
	    return hasConstantRule;
	}
	// End - #34367
	
}
