package my.ep.web.backingbean.pm.profilepersonalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.admin.adminmatrix.IAdminMatrixSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.role.AdminReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.admin.PmAdminMatrix;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.profilepersonalization.ProfilePersonalizationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.persistence.logging.LogKey;
import my.ep.util.CustomUtils;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementUserProfileBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
//#28895_35501 End


/**
 * @author DK, RAIN POH
 * 
 */
@ManagedBean(name = "profilePersonalizationBackingBean")
@RequestScoped
public class ProfilePersonalizationBackingBean extends BaseBackingBean<ProfilePersonalizationManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = EpLogger.getLogger(ProfilePersonalizationBackingBean.class);

	public ProfilePersonalizationBackingBean() {

		adminUserRoleMap.put("AUDITOR_GENERAL", Arrays.asList(""));
		adminUserRoleMap.put("AUDITOR_INTERNAL", Arrays.asList("MINISTRY_ADMIN"));
		adminUserRoleMap.put("BPK_ADMIN", Arrays.asList("MINISTRY_ADMIN"));
		adminUserRoleMap.put("BPK_APPROVER", Arrays.asList("MINISTRY_ADMIN"));
		adminUserRoleMap.put("BPK_CCA", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("BPK_PCA", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("UPS_ADMIN", Arrays.asList("MINISTRY_ADMIN", "UPS_ADMIN"));
		adminUserRoleMap.put("UPS_APPROVER", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("BPK_TEMPL_ADMIN", Arrays.asList("BPK_TEMPL_ADMIN", "PTJ_ADMIN"));
		adminUserRoleMap.put("BIZ_OWNER", Arrays.asList("BIZ_OWNER", "PTJ_ADMIN"));
		adminUserRoleMap.put("CM_ADMIN", Arrays.asList("CM_ADMIN", "SUPPORT_ADMIN"));
		adminUserRoleMap.put("CM_USER", Arrays.asList("EP_ADMIN_2"));
		adminUserRoleMap.put("CD_OFFICER", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("CD_COMMITTEE", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("COMMITTEE_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("COMMON_USER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("CT_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("CONTRACT_SIGNER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("DESK_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("DESK_ASSISTANT", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("DISC_SECRETARY", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("EP_ADMIN", Arrays.asList("EP_ADMIN"));
		adminUserRoleMap.put("EC_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("EC_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("EC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FACTORING_USER", Arrays.asList("EP_ADMIN_2"));
		adminUserRoleMap.put("FPB_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FPB_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FPB_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FEC_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FEC_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FEC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("FL_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("GOVT_SELLER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("JABATAN_ADMIN", Arrays.asList("JABATAN_ADMIN", "MINISTRY_ADMIN"));
		adminUserRoleMap.put("MINISTRY_ADMIN", Arrays.asList("MINISTRY_ADMIN", "EP_ADMIN_2"));
		adminUserRoleMap.put("MIN_CONTRACT_ADMIN", Arrays.asList("MIN_CONTRACT_ADMIN", "PTJ_ADMIN"));
		adminUserRoleMap.put("MSC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("MIN_TEMPL_ADMIN", Arrays.asList("MIN_TEMPL_ADMIN", "PTJ_ADMIN"));
		adminUserRoleMap.put("OC_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("OC_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("OC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("PAY_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("PLAN_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("UPS_PO", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("PBA_CHAIRPERSON", Arrays.asList("PTJ_ADMIN", "BPK_ADMIN"));
		adminUserRoleMap.put("PBA_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("PBA_SECRETARY", Arrays.asList("PTJ_ADMIN", "BPK_ADMIN"));
		adminUserRoleMap.put("PBB_CHAIRPERSON", Arrays.asList("PTJ_ADMIN", "BPK_ADMIN"));
		adminUserRoleMap.put("PBB_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("PBB_SECRETARY", Arrays.asList("BPK_ADMIN", "PTJ_ADMIN"));
		adminUserRoleMap.put("PTJ_ADMIN", Arrays.asList("MINISTRY_ADMIN", "PTJ_ADMIN", "JABATAN_ADMIN"));
		adminUserRoleMap.put("PUBLICATION_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCA_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCA_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCA_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCB_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCB_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("QCB_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("RECEIVING_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("RN_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("REQUISITIONER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("UPS_SVC", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("UPS_SVO", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("SC_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("SC_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("SC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("BASIC_SUPPLIER_ADMIN", Arrays.asList("BASIC_SUPPLIER_ADMIN"));
		adminUserRoleMap.put("MOF_SUPPLIER_ADMIN", Arrays.asList("MOF_SUPPLIER_ADMIN"));
		adminUserRoleMap.put("BASIC_SUPPLIER_USER", Arrays.asList("BASIC_SUPPLIER_ADMIN"));
		adminUserRoleMap.put("MOF_SUPPLIER_USER", Arrays.asList("MOF_SUPPLIER_ADMIN"));
		adminUserRoleMap.put("SUPPORT_ADMIN", Arrays.asList("SUPPORT_ADMIN", "EP_ADMIN_2"));
		adminUserRoleMap.put("UPS_SDO", Arrays.asList("UPS_ADMIN"));
		adminUserRoleMap.put("TEC_CHAIRPERSON", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("TEC_MEMBER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("TEC_SECRETARY", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("PLAN_VERF_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("BI_USER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("EP_COMMITTEE", Arrays.asList("BPK_ADMIN"));
		adminUserRoleMap.put("SUPPORT_USER", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("BPK_OFFICER", Arrays.asList("BPK_ADMIN"));
		adminUserRoleMap.put("BPK_CQ_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("BPK_PQ_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("SUPPORT_AUTH_USER", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("SUPPLIER_TEMP", Arrays.asList(""));
		adminUserRoleMap.put("MARKET_RESEARCH", Arrays.asList("BPK_ADMIN", "PTJ_ADMIN"));
		adminUserRoleMap.put("BPK_AUDITOR", Arrays.asList("BPK_ADMIN"));
		adminUserRoleMap.put("BPK_CC_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("EP_TEMPL_ADMIN", Arrays.asList("EP_TEMPL_ADMIN", "BPK_ADMIN"));
		adminUserRoleMap.put("FL_USER", Arrays.asList("EP_ADMIN_2"));
		adminUserRoleMap.put("G2G_ADMIN", Arrays.asList("EP_ADMIN_2", "G2G_ADMIN"));
		adminUserRoleMap.put("G2G_USER", Arrays.asList("G2G_ADMIN"));
		adminUserRoleMap.put("SUPPORT_TECHNICAL", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("PLAN_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("ACKNOWLEDGE_OFFICER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("EP_ADMIN_1", Arrays.asList("EP_ADMIN_1"));
		adminUserRoleMap.put("EP_ADMIN_2", Arrays.asList("EP_ADMIN_2"));
		adminUserRoleMap.put("COLLECTOR_CDC", Arrays.asList("SUPPORT_ADMIN"));
		adminUserRoleMap.put("COLLECTOR_GOV", Arrays.asList("BPK_ADMIN"));
		adminUserRoleMap.put("PTJ_APPROVER", Arrays.asList("PTJ_ADMIN"));
		adminUserRoleMap.put("SUPPORT_APPL", Arrays.asList("SUPPORT_ADMIN"));

	}
	//26238
	public static final String NAV_TO_INFO_DISC_ACTION = "/xhtml/pm/profilePersonalization/userDetails/infoActiveDisciplinaryAction.xhtml";

	// Navigation Details
	public static final String NAV_TO_MYACCOUNT = "/xhtml/pm/profilePersonalization/userDetails/profileManagementMaintain.xhtml";
	public static final String NAV_TO_COMMON_DETAILS = "/xhtml/pm/profilePersonalization/userDetails/userCommonDetails.xhtml";
	public static final String NAV_TO_COMMON_DETAILS_R = "/xhtml/pm/profilePersonalization/userDetails/userCommonDetailsR.xhtml";
	//#16052019 - SSM INTEGRATION CR
	public static final String NAV_TO_INFO_ROB_EXPIRE = "/xhtml/pm/profilePersonalization/userDetails/infoRobExpire.xhtml";
	private static final String ERROR_PAGE = "/xhtml/pm/shared/outputMsg.xhtml";
	// Mapper for DDL object
	private static Map<Long, Object> identityTypeMapper;

	// Tab values
	public static final int TAB_USER_PROFILE = 0;
	public static final int TAB_PERSONALIZATION = 1;
	public static final int TAB_CHANGE_PASSWORD = 2;
	//public static final int TAB_CHANGE_CHALLENGE_QUESTION = 3;
	public static final int TAB_AUTH_PERSONNEL = 3;

	// AdminRoles
	public static final Map<String, List<String>> adminUserRoleMap = new HashMap<String, List<String>>();

	public static PmUser authUserDetails;

	/**
	 * @return the tabUserProfile
	 */
	public int getTabUserProfile() {
		return TAB_USER_PROFILE;
	}

	/**
	 * @return the tabPersonalization
	 */
	public int getTabPersonalization() {
		return TAB_PERSONALIZATION;
	}

	/**
	 * @return the tabChangePassword
	 */
	public int getTabChangePassword() {
		return TAB_CHANGE_PASSWORD;
	}

	public int getTabAuthPersonnel() {
		return TAB_AUTH_PERSONNEL;
	}

	/**
	 * @return the tabChangeChallengeQuestion
	 */
//	public int getTabChangeChallengeQuestion() {
//		return TAB_CHANGE_CHALLENGE_QUESTION;
//	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * load clean view for my account
	 */
	public void preLoadingMyAccount() {
		logger.info("preLoadingMyAccount");
		FacesScopes facesScopes = new FacesScopes();
		PmUser sUsers = facesScopes.getUserSessionBean();
		if (sUsers == null) {
			throw new EpException(MessageConst.ERROR_770045);
		}
		this.retrieveIdentityTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		loadUserProfile(sUsers.getUserId());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MYACCOUNT);
	}

	public void preLoadingUserCommonDetails() {
		logger.info("preLoadingUserCommonDetails");
		renewSesionObject();
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			PmUser sUsers = facesScopes.getUserSessionBean();
			if (sUsers == null) {
				throw new EpException(MessageConst.ERROR_770045);
			}
			loadUserCommonDetails(sUsers.getUserId());
			constructRoles();
			retrieveMofExpDate(getManagedBean().getCurrentProfile());
			retrieveSoftCertExpDate(getManagedBean().getSelectedUser());
			//#16052019 - SSM INTEGRATION CR
			retrieveRobExpDate(getManagedBean().getCurrentProfile());

			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_COMMON_DETAILS);
		} catch (Exception e) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageConst.ERROR_770045, PmCommonUtil
							.getMsgFromMsgUtils(MessageConst.ERROR_770045, null)));
			PmCommonUtil.redirectToPage(ERROR_PAGE, null, null);
		}

	}

	public void preLoadingUserCommonDetailsR() {
		logger.info("preLoadingUserCommonDetailsR");
		renewSesionObject();
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			PmUser sUsers = facesScopes.getUserSessionBean();
			if (sUsers == null) {
				throw new EpException(MessageConst.ERROR_770045);
			}
			loadUserCommonDetails(sUsers.getUserId());
			constructRoles();
			retrieveMofExpDate(getManagedBean().getCurrentProfile());
			retrieveSoftCertExpDate(getManagedBean().getSelectedUser());
			//#16052019 - SSM INTEGRATION CR
			retrieveRobExpDate(getManagedBean().getCurrentProfile());

			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_COMMON_DETAILS_R);
		} catch (Exception e) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageConst.ERROR_770045, PmCommonUtil
							.getMsgFromMsgUtils(MessageConst.ERROR_770045, null)));
			PmCommonUtil.redirectToPage(ERROR_PAGE, null, null);
		}

	}

	public void saluationChanged(ValueChangeEvent event) {
		System.out.println("...........saluationChanged............");
		this.getManagedBean().getSelectedUser().setModified(true);
	}

	public void notifyChangeListener(ValueChangeEvent event) {
		System.out.println("...........notifyChangeListener............");
		this.getManagedBean().getSelectedUser().setModified(true);
	}

	public void languageChangeListener(ValueChangeEvent event) {
		System.out.println("...........languageChangeListener............");
		this.getManagedBean().getSelectedUser().setModified(true);
	}

	public void notifySmsChangeListener() {
		System.out.println("...........notifySmsChangeListener............");
		if (getManagedBean().getSelectedUser().isNotifyBySms()
				&& StringUtils.isBlank(getManagedBean().getSelectedUser().getMobileNo())) {
			getManagedBean().getSelectedUser().setNotifyBySms(false);
			throw new EpException(MessageConst.ERROR_770090);
		}
		if (getManagedBean().getSelectedUser().isNotifyBySms()
				&& getManagedBean().getSelectedUser().getMobileTelcoId() == null) {
			getManagedBean().getSelectedUser().setNotifyBySms(false);
			throw new EpException(MessageConst.ERROR_770250);
		}
		this.getManagedBean().getSelectedUser().setModified(true);
	}

	/**
	 * load pm user object by id
	 * 
	 * @param id
	 */
	private void loadUserProfile(Long id) {
		logger.info("loadUserProfile");
		try {
			// initialUserProfile();
			if (id == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}

			// load user
			PmUser pmUser = PmCommonUtil.retrieveUserWithoutOrgInfoById(id);
			authUserDetails = pmUser;
			pmUser.setPMP(pmUser.isPMP() ? true : false);
			pmUser.setIdentificationCode(pmUser.getIdentificationCode() == null ? new PmParameter() : pmUser
					.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode() == null ? new PmSalutation() : pmUser
					.getSalutationCode());
			pmUser.setOrgTypeCode(pmUser.getOrgTypeCode() == null ? new PmParameter() : pmUser.getOrgTypeCode());

			PmCommonUtil.loadPmUserPreference(pmUser);

			// retrieve current active org
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(id);
			this.getManagedBean().setCurrentProfile(pmUserOrgs != null ? pmUserOrgs.get(0).getPmOrgProfile() : null);
			this.getManagedBean().setCurrentUserOrg(pmUserOrgs != null ? pmUserOrgs.get(0) : null);
			this.getManagedBean().setUserActiveRoles(new ArrayList<PmUserRole>());
			if (this.getManagedBean().getCurrentUserOrg() != null) {
				for (PmUserRole userRole : this.getManagedBean().getCurrentUserOrg().getPmUserRoles()) {
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						this.getManagedBean().getUserActiveRoles().add(userRole);
					}

				}
			}
			// load password
			pmUser.setPassword(null);
			pmUser.setConfirmPassword(null);

			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setSelectedUser(pmUser);

			getManagedBean().setPasswordPolicy(null);
			generatePasswordPolicy();
			// #28895_35501 Start - AUDIT TRAIL SM
			if (isSmAuditUser()) {
				this.getManagedBean().setOldSelectedUser((PmUser) CustomUtils.getInstance().deepCopy(this.getManagedBean().getSelectedUser()));
			}
			// #28895_35501 End

			// Load admin profile details of current user
			searchAdminProfile();

		} catch (Exception e) {
			LogKey logKey = new LogKey();
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "retrieve", id.toString(), "UserSO",
					logKey.getUserId(), e);
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
	}

	private void loadUserCommonDetails(Long id) {
		logger.info("loadUserCommonDetails");
		try {
			if (id == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}

			// load user
			PmUser pmUser = PmCommonUtil.retrieveUserWithoutOrgInfoById(id);
			this.getManagedBean().setSelectedUser(pmUser);
			// retrieve current active org
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(id);
			this.getManagedBean().setCurrentProfile(pmUserOrgs != null ? pmUserOrgs.get(0).getPmOrgProfile() : null);
			this.getManagedBean().setCurrentUserOrg(pmUserOrgs != null ? pmUserOrgs.get(0) : null);
			this.getManagedBean().setUserActiveRoles(new ArrayList<PmUserRole>());
			if (this.getManagedBean().getCurrentUserOrg() != null) {
				for (PmUserRole userRole : this.getManagedBean().getCurrentUserOrg().getPmUserRoles()) {
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						this.getManagedBean().getUserActiveRoles().add(userRole);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
	}

	/**
	 * Get Role description by role code.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode) {
		logger.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCode(roleCode);
	}

	/**
	 * retrieve user portrait, return default portrait if liferay return null
	 * 
	 * @param loginId
	 * @return String
	 */
	public String userImagePathWithDefaultImage(String loginId) {
		logger.info("userImagePathWithDefaultImage");
		String imagePath = "";

		imagePath = userImagePath(loginId);
		if (StringUtils.isBlank(imagePath)) {
			imagePath = ProfileManagementConst.DEFAULT_PORTRAIT;
		}
		return imagePath;
	}

	/**
	 * populate user address to managed bean
	 * 
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser) {
		logger.info("populateUserAddress");
		try {
			List<PmAddressType> addressTypes = pmUser.getPmAddressTypes();
			PmAddressType userAddress = null;
			for (PmAddressType addressType : addressTypes) {
				if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressType.getAddressType())) {
					userAddress = addressType;
					break;
				}

			}

			if (userAddress != null) {
				this.getManagedBean().setUserAddress(userAddress.getPmAddress());
			} else {
				this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());
				this.getManagedBean().getUserAddress().setPmCountry(new PmCountry());
			}

			// List<PmAddressType> addressTypes =
			// PmCommonUtil.retrieveAddressTypesByUserIdOrAddressType(pmUser.getUserId(),
			// ProfileManagementConst.ADDRESS_USER);
			// this.getManagedBean().setUserAddress((addressTypes!=null &&
			// !addressTypes.isEmpty()) ? addressTypes.get(0).getPmAddress() :
			// PmCommonUtil.getNewPmAddress());

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770017, new String[] { e.getMessage() });
		}
	}

	public String retrieveParamDesc(String type, String code) {
		if (code != null) {
			return getParameterDesc(type, code);
		}
		return null;
	}

	/**
	 * retrieve salutation list
	 * 
	 * @return List<PmSalutation>
	 */
	public List<PmSalutation> salutationOptions() {
		logger.info("salutationOptions");
		// return getDropdownValues(ParameterTypeConst.SALUTATIONS);
		return PmCommonUtil.retrievePmSalutationList();
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}

	/**
	 * get all country list
	 * 
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		logger.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}

	/**
	 * get state list by country id
	 * 
	 * @param countryId
	 * @return List<PmState>
	 */
	public List<PmState> getStateList(Long countryId) {
		logger.info("getStateList");
		return PmCommonUtil.retrievePmStateList(countryId);
	}

	/**
	 * get division list by state id (only applicable for sarawak)
	 * 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public List<PmDivision> getDivisionList(Long stateId) {
		logger.info("getDivisionList");
		return PmCommonUtil.retrievePmDivisionList(stateId);
	}

	/**
	 * get district list by state id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		logger.info("getDistrictList");
		return PmCommonUtil.retrievePmDistrictList(stateId);
	}

	/**
	 * get city list by district id
	 * 
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		logger.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}

	/**
	 * update division flag to either disable or enable division selection on UI
	 * 
	 * @param divisionType
	 * @param stateId
	 */
	public void updateDivisionFlag(Long stateId) {
		logger.info("updateDivisionFlag");
		if (stateId != null) {
			PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			this.getManagedBean().setDivisionFlagC(
					(pmState != null && ParameterConst.SARAK_SK_CODE.equals(pmState.getStateCode())) ? true : false);
		}
		updateAddress(1);
	}

	/**
	 * reset PmAddress accordingly
	 * 
	 * @param address
	 */
	public void updateAddress(int address) {
		logger.info("updateAddress");
		logger.info("address: " + address);
		if (0 == address) { // country
			this.getManagedBean().getUserAddress().setPmState(new PmState());
			this.getManagedBean().getUserAddress().setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress().setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		} else if (1 == address) { // state
			this.getManagedBean().getUserAddress().setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress().setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		} else if (2 == address) { // district);
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		}
		addressEntered();
	}

	/**
	 * retrieve identity type from PM
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getIdentityTypeList() {
		logger.info("getIdentityTypeList");
		return getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}

	/**
	 * retrieve all identity type into mapper
	 */
	protected void retrieveIdentityTypeMap() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescList(
				ParameterTypeConst.IDENTIFICATION_TYPES);

		identityTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}

	/**
	 * get all language list
	 * 
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		logger.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}

	/**
	 * Validation for PmUser's fields (User Profile Sub Menu)
	 * 
	 * @param user
	 */
	private void validateUser(PmUser user) {
		logger.info("validateUser");
		if (StringUtils.isBlank(user.getLoginId())) {
			throw new EpException(MessageConst.ERROR_770030);
		}
		if (user.getSalutationCode().getSalutationId() == null) {
			throw new EpException(MessageConst.ERROR_770031);
		}
		if (StringUtils.isBlank(user.getUserName())) {
			throw new EpException(MessageConst.ERROR_770032);
		}
		if (user.getIdentificationCode().getParameterId() == null) {
			throw new EpException(MessageConst.ERROR_770033);
		}
		if (StringUtils.isBlank(user.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_770034);
		}
		if (user.getPmCountry().getCountryId() == null) {
			throw new EpException(MessageConst.ERROR_770035);
		}
		if (StringUtils.isBlank(user.getEmail())) {
			throw new EpException(MessageConst.ERROR_770036);
		}
		// if(StringUtils.isBlank(user.getPreferredLanguage())){
		// throw new EpException(MessageConst.ERROR_770037);
		// }
		// if(user.getNotifyRemovalDay()==null
		// || (user.getNotifyRemovalDay()!=null
		// && user.getNotifyRemovalDay()==0)){
		// throw new EpException(MessageConst.ERROR_770038);
		// }

	}

	public void addressEntered() {
		PmAddress pmAddress = this.getManagedBean().getUserAddress();
		// address is optional
		if (pmAddress.getPmCountry().getCountryId() != null || !StringUtils.isBlank(pmAddress.getAddress1())
				|| !StringUtils.isBlank(pmAddress.getPostcode()) || pmAddress.getPmState().getStateId() != null
				|| pmAddress.getPmDistrict().getDistrictId() != null
				|| pmAddress.getPmDivision().getDivisionId() != null) {
			this.getManagedBean().setAddressEntered(true);
		} else {
			this.getManagedBean().setAddressEntered(false);
		}
	}

	/**
	 * Validation for PmAddress's fields (User Profile Sub Menu)
	 * 
	 * @param pmAddress
	 */
	private boolean validateUserAddress(PmAddress pmAddress) {
		logger.info("validateUserAddress");
		boolean addressEntered = false;

		// address is optional
		if (pmAddress.getPmCountry().getCountryId() != null || !StringUtils.isBlank(pmAddress.getAddress1())
				|| !StringUtils.isBlank(pmAddress.getPostcode()) || pmAddress.getPmState().getStateId() != null
				|| pmAddress.getPmDistrict().getDistrictId() != null
				|| pmAddress.getPmDivision().getDivisionId() != null) {
			addressEntered = true;
		}

		if (addressEntered) {
			if (pmAddress.getPmCountry().getCountryId() == null) {
				throw new EpException(MessageConst.ERROR_770039);
			}

			if (StringUtils.isBlank(pmAddress.getAddress1())) {
				throw new EpException(MessageConst.ERROR_770040);
			}

			if (StringUtils.isBlank(pmAddress.getPostcode())) {
				throw new EpException(MessageConst.ERROR_770041);
			}

			if (pmAddress.getPmState().getStateId() == null) {
				throw new EpException(MessageConst.ERROR_770042);
			}

			if (pmAddress.getPmDistrict().getDistrictId() == null) {
				throw new EpException(MessageConst.ERROR_770043);
			}

			if (this.getManagedBean().isDivisionFlagC()) {
				if (pmAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_770044);
				}
			}
		}
		return addressEntered;
	}

	/**
	 * Validation for PmUser
	 * 
	 * @param PmUser
	 */
	private void validatePreference(PmUser pmUser) {
		logger.info("validatePreferences");
		if (pmUser != null) {
			if (StringUtils.isBlank(pmUser.getPreferredLanguage())) {
				throw new EpException(MessageConst.ERROR_770046);
			}

			if (pmUser.isNotifyBySms() && StringUtils.isBlank(pmUser.getMobileNo())) {
				throw new EpException(MessageConst.ERROR_770090);
			}

			if (pmUser.isNotifyBySms() && (pmUser.getMobileTelcoId() == null)) {
				throw new EpException(MessageConst.ERROR_770250);
			}

			// if (pmUser.getNotifyRemovalDay() == null
			// || pmUser.getNotifyRemovalDay() <= 0) {
			// throw new EpException(MessageConst.ERROR_770047);
			// }
		}
	}

	/**
	 * load user address - C
	 * 
	 * @param user
	 * @param isAddressExist
	 */
	private void loadUserAddress(PmUser user, boolean isAddressExist) {
		logger.info("loadUserAddress");

		// update contact address
		if (user.getPmAddressTypes() != null && !user.getPmAddressTypes().isEmpty()) {
			boolean isUserAddressExist = false;
			for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj.getRecordStatusFlg()) {
					if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressTypeObj.getAddressType())) {
						// update address
						if (isAddressExist) {
							addressTypeObj.setPmAddress(this.getManagedBean().getUserAddress());
							if (addressTypeObj.getPmAddress().getPmDivision() != null
									&& addressTypeObj.getPmAddress().getPmDivision().getDivisionId() == null) {
								addressTypeObj.getPmAddress().setPmDivision(null);
							}
						} else {
							// remove address
							addressTypeObj.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						}
						isUserAddressExist = true;
						break;
					}
				}
			}

			if (!isUserAddressExist) {
				// add address
				PmAddressType pmAddressType = new PmAddressType();
				pmAddressType.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(this.getManagedBean().getUserAddress());
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision().getDivisionId() == null) {
					pmAddressType.getPmAddress().setPmDivision(null);
				}
				pmAddressType.setPmOrgProfile(null);
				user.addPmAddressType(pmAddressType);
			}
		} else {
			// add address
			if (isAddressExist) {
				if (user.getPmAddressTypes() == null) {
					user.setPmAddressTypes(new ArrayList<PmAddressType>());
				}
				PmAddressType pmAddressType = new PmAddressType();
				pmAddressType.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(this.getManagedBean().getUserAddress());
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision().getDivisionId() == null) {
					pmAddressType.getPmAddress().setPmDivision(null);
				}
				pmAddressType.setPmOrgProfile(null);
				user.addPmAddressType(pmAddressType);
			}
		}

	}

	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() throws Exception {
		debugLogger("saveUserProfile");
		PmUser user = this.getManagedBean().getSelectedUser();
		boolean addressEntered = false;
		// validation
		validateUser(user);
		// addressEntered
		// =validateUserAddress(this.getManagedBean().getUserAddress());
		// 19Nov2014
		validatePreference(user);
		fillUserOrgInfoFromUser(user);

		// loadUserAddress(user, addressEntered); 19Nov2014

		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			if (!user.isBrandNew()) {
				// track changes for tracking Diary
				if (user.isModified()) {

					logger.info("Tracking diary for my profile :saveUserProfile");
					String messageId = MessageConst.TD_80014;
					String[] msgParam = new String[] { user.getUserName() };
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyValues(user, getActionDesc(messageId, msgParam), user
									.getRecordStatusFlg().longValue()));

				}
			}
			reqDTO.setPmUser(user);

			UserResDTO resDTO = userSO.saveUserWithoutOrg(reqDTO); // saveUser2

			if (resDTO.isSuccessful()) {
				/*
				 * if(user.getPortrait()!=null){
				 * uploadPortrait(resDTO.getPmUser()); }
				 */
				// #28895_35501 Start - AUDIT TRAIL SM
				IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
				if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF) && isSmAuditUser()) {
					AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementUserProfileBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
				}
				// #28895_35501 End
				loadUserProfile(user.getUserId());
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_770051, new String[] { resDTO.getPmUser().getUserName() },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			LogKey logKey = new LogKey();
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "save", user.getUserId().toString(),
					"UserSO", logKey.getUserId(), e);
			throw e;
			// throw new EpException(MessageConst.ERROR_770050, new
			// String[]{e.getMessage()});
		} finally {
			userSO = null;
		}
	}

	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params, MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	/**
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}

	/**
	 * login id = identification no.
	 */
	public void updateLoginId() {
		this.getManagedBean().getSelectedUser()
				.setLoginId(this.getManagedBean().getSelectedUser().getIdentificationNo());
	}

	/**
	 * Update portrait
	 * 
	 * @throws Exception
	 */
	public void updatePortrait() throws Exception {
		logger.info("updatePortrait - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();

		try {
			if (this.getManagedBean().getSelectedUser().getPortrait() == null) {
				throw new EpException(MessageConst.ERROR_770119);
			}

			reqDTO.setPortrait(this.getManagedBean().getSelectedUser().getPortrait());
			reqDTO.setLoginId(this.getManagedBean().getSelectedUser().getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			resDTO = userSO.updatePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_770122, new String[] { this.getManagedBean().getSelectedUser()
										.getLoginId() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770120, new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("updatePortrait - END");
	}

	/**
	 * Update portrait
	 * 
	 * @throws Exception
	 */
	public void deletePortrait() throws Exception {
		logger.info("deletePortrait - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();
		try {

			reqDTO.setLoginId(this.getManagedBean().getSelectedUser().getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			resDTO = userSO.deletePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_770123, new String[] { this.getManagedBean().getSelectedUser()
										.getLoginId() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770121, new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("deletePortrait - END");
	}

	/**
	 * Get user portrait
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handlePortraitUpload(FileUploadEvent event) throws Exception {
		logger.info("handlePortraitUpload - START");
		UploadedFile item = event.getFile();
		byte[] bytes = item.getContents();
		this.getManagedBean().getSelectedUser().setPortrait(bytes);
		this.getManagedBean().getSelectedUser().setReplacePortrait(true);
		updatePortrait();
		logger.info("handlePortraitUpload - END");
	}

	/**
	 * clear user portrait
	 * 
	 * @throws Exception
	 */
	public void clearPortraitUpload() throws Exception {
		logger.info("clearPortraitUpload - START");
		this.getManagedBean().getSelectedUser().setPortrait(null);
		this.getManagedBean().getSelectedUser().setReplacePortrait(false);
		logger.info("clearPortraitUpload - END");
	}

	/**
	 * reset password ui
	 */
	public void clearPassword() {
		logger.info("clearPassword");
		this.getManagedBean().getSelectedUser().setPassword(null);
		this.getManagedBean().getSelectedUser().setConfirmPassword(null);
	}

	/**
	 * Change password
	 * 
	 * @throws Exception
	 */
	public void changePassword() throws Exception {
		logger.info("changePassword - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();
		try {

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getLoginId())) {
				throw new EpException(MessageConst.ERROR_770125);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getOldPassword())) {
				throw new EpException(MessageConst.ERROR_770124);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getPassword())) {
				throw new EpException(MessageConst.ERROR_770126);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getConfirmPassword())) {
				throw new EpException(MessageConst.ERROR_770127);
			}

			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
			PmUser user = this.getManagedBean().getSelectedUser();
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);

			logger.info("Tracking diary for my profile: change password :changePassword");
			String messageId = MessageConst.TD_80015;
			String[] msgParam = new String[] { user.getUserName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyValues(user, getActionDesc(messageId, msgParam), user
							.getRecordStatusFlg().longValue()));
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			resDTO = userSO.changePassword(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_770125, new String[] { this.getManagedBean().getSelectedUser()
										.getLoginId() }, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
				clearPassword();

			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770128, new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("changePassword - END");
	}

	// public SelectItem[] getChallengeQAList(){
	// PmUser pmUser = getFacesScopes().getUserSessionBean();
	// return PmCommonUtil.retrieveChallengeQAList(pmUser);
	// }
	//
	// public String getChallengeQuestionsLabel(Long index){
	// index = index + 1;
	// return ResourceBundleUtils.getMessage(
	// "lbl.pm.profiles.ccq.q"+index,
	// ResourceBundleUtils.BUNDLE_COMMON_LABELS,
	// new FacesScopes().getUserLocale());
	// }
	//
	// public String getChallengeAnswersLabel(Long index){
	// index = index + 1;
	// return ResourceBundleUtils.getMessage(
	// "lbl.pm.profiles.ccq.a"+index,
	// ResourceBundleUtils.BUNDLE_COMMON_LABELS,
	// new FacesScopes().getUserLocale());
	// }
	//
	//
	// /**
	// * Change password
	// *
	// * @throws Exception
	// */
	// public void changeChallengeQuestions() throws Exception {
	// logger.info("changeChallengeQuestions - START");
	// IUserSO userSO = null;
	// UserReqDTO reqDTO = new UserReqDTO();
	// UserResDTO resDTO = new UserResDTO();
	// try {
	// if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
	// .getLoginId())) {
	// throw new EpException(MessageConst.ERROR_770125);
	// }
	// if (StringUtils.isBlank(this.getManagedBean().getCurrentPassword())) {
	// throw new EpException(MessageConst.ERROR_770124);
	// }
	// Set<String> questionsSet = new HashSet<String>();
	// Set<String> answersSet = new HashSet<String>();
	// List<String> questionList = new ArrayList<String>();
	// List<String> answerList = new ArrayList<String>();
	// int counter = 1;
	// for(ChallengeQAList qa : getManagedBean().getChallengeQAList()){
	// if(!StringUtils.isBlank(qa.getQuestion())){
	// questionsSet.add(qa.getQuestion());
	// questionList.add(qa.getQuestion());
	// }else{
	// throw new EpException("Please Select Question " + counter);
	// }
	//
	// if(!StringUtils.isBlank(qa.getAnswer())){
	// answersSet.add(qa.getAnswer());
	// answerList.add(qa.getAnswer());
	// }else{
	// throw new EpException("Please Insert Answer " + counter);
	// }
	// counter ++;
	// }
	//
	//
	// if(questionsSet.size() != getManagedBean().getChallengeQAList().size()){
	// throw new EpException("Duplicate Questions are selected");
	// }
	//
	// if(answersSet.size() != getManagedBean().getChallengeQAList().size()){
	// throw new EpException("Duplicate Answers are inserted");
	// }
	//
	// userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	// reqDTO = new UserReqDTO();
	// reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
	// PmUser user = getManagedBean().getSelectedUser();
	// IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
	// .getServiceObject("pm", IStatusSO.class);
	// StatusReqDTO statusReqDTO = new StatusReqDTO();
	// statusReqDTO.setStatusId(user.getRecordStatusFlg()
	// .longValue());
	// StatusResDTO statusResDTO = statusSO
	// .retrieveStatusByStatusId(statusReqDTO);
	// logger.info("Tracking diary for my profile: change challenge Questions :changeChallengeQuestions");
	// String messageId = MessageConst.TD_80016;
	// String[] msgParam = new String[] {user.getUserName()};
	// reqDTO.setTrackingStatus(true);
	// reqDTO.getTrackDairyVOs().add(
	// PmCommonUtil.assignTrackingDairyValues(user,
	// getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ?
	// statusResDTO
	// .getPmStatus() : new PmStatus()));
	// reqDTO.setLoginId(getManagedBean().getSelectedUser()
	// .getLoginId());
	// reqDTO.setUserOimPassword(getManagedBean().getCurrentPassword());
	// reqDTO.setChallangeQuestions(questionList);
	// reqDTO.setChallangeAnswers(answerList);
	// resDTO = userSO.setOimChallengeQA(reqDTO);
	// if (resDTO != null && resDTO.isSuccessful()) {
	// FacesContext
	// .getCurrentInstance()
	// .addMessage(
	// null,
	// new FacesMessage(
	// FacesMessage.SEVERITY_INFO,
	// null,
	// MessageUtils
	// .getMessage(
	// MessageConst.INFO_770171,
	// new String[] { this
	// .getManagedBean()
	// .getSelectedUser()
	// .getLoginId() },
	// MessageUtils.BUNDLE_COMMON_MESSAGES,
	// new FacesScopes()
	// .getUserLocale())));
	// clearChallengeQuestions();
	//
	// }
	//
	// } catch (Exception e) {
	// throw new EpException(MessageConst.ERROR_770171,
	// new String[] { e.getMessage() });
	// } finally {
	// userSO = null;
	// }
	// logger.info("changeChallengeQuestions - END");
	// }

	// /**
	// * reset challenge questions ui
	// */
	// public void clearChallengeQuestions() {
	// logger.info("clearChallengeQuestions");
	// this.getManagedBean().setCurrentPassword(null);
	// //load challengeQA
	// List<ChallengeQAList> challengeQAList = new ArrayList<ChallengeQAList>();
	// challengeQAList.add(new ChallengeQAList(null,null));
	// challengeQAList.add(new ChallengeQAList(null,null));
	// challengeQAList.add(new ChallengeQAList(null,null));
	// getManagedBean().setChallengeQAList(challengeQAList);
	// }

	private void fillUserOrgInfoFromUser(PmUser user) {
		for (PmUserOrg activeOrg : user.getPmUserOrgs()) {
			if (DbValueConst.RECORD_STATUS_ACTIVE == activeOrg.getRecordStatusFlg()) {
				activeOrg.setEmail(user.getEmail());
				activeOrg.setPhoneNo(user.getPhoneNo());
				activeOrg.setPhoneArea(user.getPhoneArea());
				activeOrg.setPhoneCountry(user.getPhoneCountry());
				activeOrg.setFaxNo(user.getFaxNo());
				activeOrg.setFaxArea(user.getFaxArea());
				activeOrg.setFaxCountry(user.getFaxCountry());
				activeOrg.setMobileNo(user.getMobileNo());
				activeOrg.setMobileArea(user.getMobileArea());
				activeOrg.setMobileCountry(user.getMobileCountry());
				activeOrg.setMobileTelcoId(user.getMobileTelcoId());
				break;
			}
		}
	}

	public void generatePasswordPolicy() {
		IUserSO userSO = null;
		if (StringUtils.isBlank(getManagedBean().getPasswordPolicy())) {
			try {
				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setLoginId(getManagedBean().getSelectedUser().getLoginId());
				reqDTO.setUserLocale(getFacesScopes().getUserLocale());
				// reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
				UserResDTO resDTO = userSO.getPasswordPolicyInfo(reqDTO);
				if (resDTO.isSuccessful() && resDTO.getPolicyInfos() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append("<b>");
					sb.append(ResourceBundleUtils.getMessage("lbl.pm.profiles.cp.tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
					sb.append("</b><br/>");
					for (String policy : resDTO.getPolicyInfos()) {
						sb.append("&#160;&#8226;&#160; ");
						sb.append(policy);
						sb.append("<br/>");
					}
					getManagedBean().setPasswordPolicy(sb.toString());
				} else if (!resDTO.isSuccessful()) {
					StringBuilder sb = new StringBuilder();
					sb.append("<b>");
					sb.append(ResourceBundleUtils.getMessage("lbl.pm.profiles.cp.tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
					sb.append("</b><br/>");
					for (String policy : resDTO.getPolicyInfos()) {
						sb.append("&#160;&#8226;&#160; ");
						sb.append(policy);
						sb.append("<br/>");
					}
					getManagedBean().setPasswordPolicy(sb.toString());
					//
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, ResourceBundleUtils.getMessage(
									"lbl.pm.profiles.pass.policy.tooltip.err",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));

					// throw new EpException(ResourceBundleUtils.getMessage(
					// "lbl.pm.profiles.pass.policy.tooltip.err",
					// ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					// new FacesScopes().getUserLocale()));
				}
			} finally {
				userSO = null;
			}
		}

	}

	private void retrieveMofExpDate(PmOrgProfile orgProfile) {
		if (isMofExpiryShown()) {
			if (orgProfile.getSmSupplier() != null && orgProfile.getSmSupplier().getSmMofAccounts() != null
					&& !orgProfile.getSmSupplier().getSmMofAccounts().isEmpty())
				for (SmMofAccount account : orgProfile.getSmSupplier().getSmMofAccounts()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == account.getRecordStatusFlg()) {
						getManagedBean().setMofExpDate(account.getExpDate());
						break;
					}
				}
		}
	}

	private void retrieveSoftCertExpDate(PmUser user) {
		if (isSoftCertExpiryShown()) {
			if (user.getPmUserDigiCert() != null && user.getPmUserDigiCert().getPmDigiCert() != null) {
				getManagedBean().setSoftCertExpDate(user.getPmUserDigiCert().getPmDigiCert().getValidTo());
			}
		}

	}

	public boolean isMofExpiryShown() {
		if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())) {
			return true;
		}
		return false;
	}

	public boolean isVirtualCertShown() {
		if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			return true;
		}
		return false;
	}

	public boolean isSoftCertExpiryShown() {
		if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.FL_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			return true;
		}
		return false;
	}

	private void constructRoles() {
		List<String> rolesList = new ArrayList<String>();
		int i = 0;
		for (PmUserRole userRole : getManagedBean().getUserActiveRoles()) {
			rolesList.add(RoleHelper.getInstance().getRoleDescByCode(userRole.getPmRole().getRoleCode()).getRoleName());
			i++;
		}
		getManagedBean().setRoleTableScrollHeight(i > 2 ? 66 : (i * 22));
		getManagedBean().setRolesList(rolesList);
	}

	public int numberOfLines(String string, int cols) {
		int rvalue = 1;
		if (StringUtils.isNotBlank(string)) {
			for (String strTmp : string.split("\n")) {
				rvalue++;
				rvalue += strTmp.length() >= cols ? strTmp.length() / cols : 0;
			}
		} else {
			rvalue = 1;
		}

		return rvalue;
	}

	public boolean isTempSupplierRole() {
		return getFacesScopes().hasRoleUser(RoleCode.SUPPLIER_TEMP.value()) ? true : false;
	}

	/** admin Search & Paging - Start */

	/**
	 * search admin profiles
	 */
	
	private List<Long> getParentProfiles(OrgProfileReqDTO reqDTO,List<Long> orgProfileIds,int startLevel){

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		OrgProfileResDTO orgResDTO = orgProfileSO.retrieveOrgDetailsByOrgId(reqDTO);
		
		if (orgResDTO.getPmOrgProfile().getParentOrgProfile() != null) {
			
			orgProfileIds.set(startLevel,orgResDTO.getPmOrgProfile().getParentOrgProfile().getOrgProfileId());
			
			reqDTO.setOrgProfileId(orgResDTO.getPmOrgProfile().getParentOrgProfile().getOrgProfileId());
			startLevel++;
			getParentProfiles(reqDTO,orgProfileIds,startLevel);
		}
		
		return orgProfileIds;
		
	}
	
	
	public void searchAdminProfile() {
		logger.info("searchAdminProfile - start");
		IUserSO userSO = null;
		IParameterSO parameterSO = null;
		IAdminMatrixSO adminMatrixSO = null;
		getManagedBean().setLazy(true);
		FacesScopes facesScopes = new FacesScopes();
		userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		adminMatrixSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAdminMatrixSO.class);
		try {
			
			if(facesScopes.getUserSessionBean().getLoginId().equals(RoleCode.SYS_ADMIN.value())){
				getManagedBean().setAdminDetReceivedList(null);
			}else{
			
				AdminReqDTO adminReqDTO = new AdminReqDTO();
				if (authUserDetails.getPmUserOrgs() != null) {

				ParameterResDTO resDTO = parameterSO.retrieveOrgTypeByTypeByUserId(facesScopes.getUserSessionBean().getUserId());

				Object[] obj = resDTO.getParamterType();
			
				Long parameterId = Long.parseLong(obj[0].toString());
				Long orgProfileId = Long.parseLong(obj[1].toString());
				String parameterCode = obj[2].toString();
				
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileId(orgProfileId);
				List<Long> orgProfileIds = new ArrayList<Long>();
				orgProfileIds.add(orgProfileId);
				
				
				int maxNumberOfLevel = 3;
				int startLevel = 1;
				for(int i=0; i<maxNumberOfLevel; i++){
					if(i == maxNumberOfLevel-1){
						orgProfileIds.add(1l);
					}else{
						orgProfileIds.add(0l);
					}	
				}
				
				orgProfileIds = getParentProfiles(reqDTO,orgProfileIds,startLevel);
			
				List<PmRole> pmRole = userSO.retrieveUserRolesByUserId(facesScopes.getUserSessionBean().getUserId());
				
				List<PmAdminMatrix> pmAdminMatrix = adminMatrixSO.retrieveCorrespondingAdminRoleByOrgType(parameterId, pmRole, parameterCode);
				
				if(pmAdminMatrix.size()>0){
					
					Iterator<PmAdminMatrix> iterator = pmAdminMatrix.iterator();
					List<String> roleNames = new ArrayList<String>();
					while (iterator.hasNext()) {
						PmAdminMatrix pmAdminMatrixRole = iterator.next();
						roleNames.add(pmAdminMatrixRole.getAdminRoleCode());
					}
					adminReqDTO.setOrgProfileIds(orgProfileIds);
					adminReqDTO.setUserRoles(roleNames);
				}
				}
			
				if (adminReqDTO.getOrgProfileIds() !=null && adminReqDTO.getUserRoles() != null) {
					getManagedBean().setAdminDetReceivedList(userSO.searchAdminProfilesByOrgDetails(adminReqDTO,facesScopes.getUserSessionBean().getUserId()));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	//#16052019 - SSM INTEGRATION CR
	private void retrieveRobExpDate(PmOrgProfile orgProfile) {
		if (orgProfile.getSmSupplier() != null) {
			if(orgProfile.getSmSupplier().getSmAppl() != null){
				if(orgProfile.getSmSupplier().getSmAppl().getSmCompanyBasic() != null){
					if (orgProfile.getSmSupplier().getSmAppl().getSmCompanyBasic().getRobExpiryDate() != null) {
						getManagedBean().setRobExpDate(
								orgProfile.getSmSupplier().getSmAppl().getSmCompanyBasic().getRobExpiryDate());
					}
					if (orgProfile.getSmSupplier().getBusinessType()
							.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
							|| orgProfile
									.getSmSupplier()
									.getBusinessType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)) {
						getManagedBean().setShowRobExpiredDate(true);
					}
				}
			}
			
		}
	}
	//#16052019 - SSM INTEGRATION CR
	public void preLoadingRobExpire() {
		logger.info("preLoadingRobExpire");
		renewSesionObject();
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			PmUser sUsers = facesScopes.getUserSessionBean();
			if (sUsers == null) {
				throw new EpException(MessageConst.ERROR_770045);
			}
			loadUserCommonDetails(sUsers.getUserId());
			//#16052019 - SSM INTEGRATION CR
			retrieveRobExpDate(getManagedBean().getCurrentProfile());
			if(this.getManagedBean().isShowRobExpiredDate()){
				if(DateUtils.getDaysDifference(getManagedBean().getCurrentProfile().getSmSupplier().getSmAppl().getSmCompanyBasic().getRobExpiryDate(), new Date())<=90){
					this.getManagedBean().setShowRobExpiredDateMesej(true);
				}else{
					this.getManagedBean().setShowRobExpiredDateMesej(false);
				}
			}
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_INFO_ROB_EXPIRE);
		} catch (Exception e) {
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageConst.ERROR_770045, PmCommonUtil
							.getMsgFromMsgUtils(MessageConst.ERROR_770045, null)));
			PmCommonUtil.redirectToPage(ERROR_PAGE, null, null);
		}

	}
	
	
	//26238 - start
	/**
	 * Pre-Render Disciplinary Action
	 */
	public void preRenderDisciplinaryAction() {
		//26238 -  START Disciplinary Action - 28111,28496
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String docNo = originalServletRequest.getParameter("docNo");
		renewSesionObject(); 
		FacesScopes facesScopes = new FacesScopes();
		FacesContext fc = FacesContext.getCurrentInstance();  
		long startTime = System.currentTimeMillis();
		try {
			if(!StringUtils.isEmpty(docNo)){
				MofRegistrationReqDTO reqDTO1 = new MofRegistrationReqDTO();
				reqDTO1.setApplNo(docNo);
				SmAppl srcAppl =  SmServiceLookup.getMofRegistrationSO().retrieveSmSupplierByApplNo(reqDTO1).getSmAppl();								
				//26238 -  START Disciplinary Action - end
		
				this.getManagedBean().setSmSupplier(srcAppl != null ? srcAppl.getSmSupplier() : null);
			}
			if (getManagedBean().getSmSupplier() == null){
				getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
			}
			if (getManagedBean().getSmSupplier() == null){
				loadSupplierCommonDetails(getUserIdSessionFromLoginId());
				if(getManagedBean().getCurrentProfile().getSmSupplier() != null){
					getManagedBean().setSmSupplier(getManagedBean().getCurrentProfile().getSmSupplier());
				}
			}
			SmSupplier supplier = getManagedBean().getSmSupplier();
			String log = "ProfilePersonalizationBackingBean.preRenderDisciplinaryAction";
			getManagedBean().setDisciplinaryActionMsg(StringUtils.EMPTY);
			String msgDA = getDisciplinaryActionMsg(supplier);
			if(msgDA != null){
				//30842 Start
				getManagedBean().setSmAppl(SmApplicationUtil.getLatestApplBySupplierId(
						getManagedBean().getSmSupplier().getSupplierId()));
				//30842 End
				getManagedBean().setDisciplinaryActionMsg(msgDA);
				getManagedBean().setDisciplinaryAction(true);
				facesScopes.getUserSessionBean().setDisciplinaryAction(true);
				//start 32229
				SLAUtils.getInstance().logSQLPerformanceInfo("SM","DA-4", log, startTime,"32229");
				startTime = System.currentTimeMillis();
				//end 32229
			}
			SLAUtils.getInstance().logSLAInfo("SM","DA-5", log, startTime);
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_INFO_DISC_ACTION);
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("SM", "DA", "", "ProfilePersonalizationBackingBean", 
					(new LogKey()).getUserId(), e);
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageConst.ERROR_770045, PmCommonUtil
							.getMsgFromMsgUtils(MessageConst.ERROR_770045, 	null)));
			PmCommonUtil.redirectToPage(ERROR_PAGE, null, null);
		}
	}
	
	private String getDisciplinaryActionMsg(SmSupplier supplier) {
		if (supplier == null){
			return null;
		}
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setSupplier(supplier);
		Locale locale = new FacesScopes().getUserLocale();
		reqDTO.setLanguage(locale != null && locale.getLanguage() != null ? locale.getLanguage() : "en");
		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
				.retrieveDisciplinaryActionMsg(reqDTO);
		return resDTO.getErrorMessage();
	}
	
	//26238 -  END Disciplinary Action - 28111
	private void loadSupplierCommonDetails(Long userId) {
		if (userId == null){
			return;
		}
		try {
			long startTime = System.currentTimeMillis();
			// load user, retrieve current active org
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(userId);
			this.getManagedBean().setCurrentProfile(pmUserOrgs != null ? pmUserOrgs.get(0).getPmOrgProfile() : null);
			this.getManagedBean().setCurrentUserOrg(pmUserOrgs != null ? pmUserOrgs.get(0) : null);
			this.getManagedBean().setUserActiveRoles(new ArrayList<PmUserRole>());
			addUserActiveRoles(this.getManagedBean().getCurrentUserOrg());
			SLAUtils.getInstance().logSLAInfo("SM","DA",
					"ProfilePersonalizationBackingBean.loadSupplierCommonDetails", startTime);				
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("SM", "DA", userId.toString(), "ProfilePersonalizationBackingBean", 
					(new LogKey()).getUserId(), e);
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
	}
	
	private void addUserActiveRoles(PmUserOrg currentUserOrg){
		if (currentUserOrg == null || EntityHelper.isCollectionEmpty(currentUserOrg.getPmUserRoles())){
			return;
		}
		for (PmUserRole userRole : this.getManagedBean().getCurrentUserOrg().getPmUserRoles()) {
			if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				this.getManagedBean().getUserActiveRoles().add(userRole);
			}
		}
	}
	//26238 - end
	
	//28838 - start
	public boolean isCheckFactUser() {
		boolean checkFactUser = true;
		
		for(PmUserRole userRole : getManagedBean().getUserActiveRoles()) {
			logger.info("User Roles: " + userRole.getPmRole().getRoleCode());
			if(userRole.getPmRole().getRoleCode().equalsIgnoreCase(RoleCode.FACTORING_USER.value())) {
				checkFactUser = false;
			}
		}
		
		return checkFactUser;
	}
	//28838 - end
	
	// #28720 - Start
	public void onChangeEmail()
	{
		String email = this.getManagedBean().getSelectedUser().getEmail().toLowerCase();
		if(!isAllowedEmail(email))
		{
			this.getManagedBean().getSelectedUser().setEmail("");
			RequestContext.getCurrentInstance().execute(
					"emailPopUpWidgetVar.show()");
		}
	} // #28720 - End
	// #28895_35501 Start - AUDIT TRAIL SM
	private boolean isSmAuditUser() {
		String[] roles = { RoleCode.BASIC_SUPPLIER_USER.value(), RoleCode.BASIC_SUPPLIER_ADMIN.value(), RoleCode.G2G_SUPPLIER_USER.value(),
				RoleCode.G2G_SUPPLIER_ADMIN.value(), RoleCode.MOF_SUPPLIER_USER.value(), RoleCode.MOF_SUPPLIER_ADMIN.value() };
		boolean auditUser = false;
		for (String role : roles) {
			if (getFacesScopes().hasRoleUser(role)) {
				auditUser = true;
				break;
			}
		}
		return auditUser;
	}
	// #28895_35501 End
}
