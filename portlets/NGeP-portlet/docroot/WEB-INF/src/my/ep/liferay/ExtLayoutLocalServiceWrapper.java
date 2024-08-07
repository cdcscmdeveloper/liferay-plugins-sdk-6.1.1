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
	
}
