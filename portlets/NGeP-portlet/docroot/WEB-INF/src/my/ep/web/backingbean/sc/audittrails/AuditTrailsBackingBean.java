package my.ep.web.backingbean.sc.audittrails;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.managedbean.sc.audittrails.AuditTrailsManagedBean;
import my.ep.web.managedbean.trackingdiary.TrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RoleHelper;

@ManagedBean(name = "auditTrailsBackingBean")
@RequestScoped
public class AuditTrailsBackingBean extends
BaseBackingBean<AuditTrailsManagedBean> {
	private ISearchBean<PmParameterDesc> docTypeSearchBean;
	private static Logger log = Logger
			.getLogger(AuditTrailsBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/sc/quatationtender/audittrails/auditTrailsMain.xhtml";
	public static final String SC_TRACKING_DIARY_MANAGED_BEAN = "scTrackingDiaryManagedBean";
//	public static final String DP_TRACKING_DIARY_MANAGED_BEAN = "dpTrackingDiaryManagedBean";
//	public static final String FL_TRACKING_DIARY_MANAGED_BEAN = "flTrackingDiaryManagedBean";
//	public static final String CT_TRACKING_DIARY_MANAGED_BEAN = "ctTrackingDiaryManagedBean";
//	public static final String CM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String CD_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String SM_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String PP_TRACKING_DIARY_MANAGED_BEAN = "smTrackingDiaryManagedBean";
//	public static final String PM_TRACKING_DIARY_MANAGED_BEAN = "pmTrackingDiaryManagedBean";

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		log.debug("preRenderView");
		this.renewSesionObject();
		
		//Enhancement 32675 - shaqimi start
//		if(getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())
//				|| getFacesScopes().hasRoleUser(RoleCode.FACTORING_USER.value())) {
//			this.getManagedBean().setFactoringUser(true);
//		}
		//Enhancement 32675 - shaqimi start
		
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setBackUrl(fc.getViewRoot().getViewId());
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_CT_REPORT_MAIN);
		
//		getManagedBean().
	}
	
	public void redirectToMainPage(){
		log.info(".....redirectToMainPage.....");
		log.info("............this.getManagedBean().getBackURL()"+this.getManagedBean().getBackUrl());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, this.getManagedBean().getBackUrl()); 
	}


	public String getUserName(Long userId) {
		if(userId.longValue()==CommonConstants.TRACKING_DIARY_SYSTEM_USER_ID) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = new UserResDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		if(userSO != null && resDTO.getPmUser() !=null){
			return resDTO.getPmUser().getUserName();
		} else {
			return "";
		}	
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String getRoleDescByRoleCode(String roleCode) {
		log.info("getRoleDescByRoleCode");
		if(roleCode==null || roleCode.isEmpty()) {
			return ResourceBundleUtils.getMessage("lbl.pm.system", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale());
		}
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}
	
}

