package my.ep.web.backingbean.pm.profilemanagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmRoleMatrix;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmPersonnel; //#36489
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.SLAUtils; //#36489
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RoleHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author DK, RAIN POH, KEN
 * @since 8/7/2013
 * 
 */
@ManagedBean(name = "profileManagementBackingBean")
@ViewScoped
public class ProfileManagementBackingBean extends BaseBackingBean<ProfileManagementManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = EpLogger.getLogger(ProfileManagementBackingBean.class);

	// Navigation Details
	// main page for portlet profile maintenance
	public static final String NAV_TO_SEARCH = "/xhtml/pm/profilemanagement/search/profileManagementMain.xhtml?faces-redirect=true";
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/profilemanagement/userDetails/profileManagementMaintain.xhtml";

	// Default value
	public static final boolean SEARCH_DEFAULT = false;

	// Tab values
	public static final int TAB_USER_PROFILE = 0;
	public static final int TAB_AUTHORIZATION = 1;
	public static final int TAB_PERSONALIZATION = 2;
	public static final int TAB_CHANGE_PASSWORD = 3;

	// Org Category values
	public static final int FEDERAL_GOVT = 0;
	public static final int OTHERS = 1;
	String roleNameVal;
	boolean disableDeleteRole;

	// Paging for listing
	private IPagingDataModel<Object[]> userOrgDataModel = new UserProfilePagingDataModel();
	private IPagingDataModel<PmRoleDesc> roleDataModel = new RolePagingDataModel();

	// Mapper for DDL object
	private static Map<String, Object> identityTypeMapper;
	private static Map<Long, Object> orgTypeMapper;

	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;

	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		logger.info("getOrgTypeSearchBean");
		if (this.orgTypeSearchBean == null) {
			this.orgTypeSearchBean = super.createSearchBean(PmParameterDesc.class, "somOrgType");

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(new ArrayList<String>(Arrays.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE)));
			orgTypeSearchBean.getDataFilterBeans().add(bean);

			if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
				if (this.getManagedBean().getCurrentActiveOrg() != null) {
					SearchFilterBean orgTypeBean = new SearchFilterBean();
					orgTypeBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					orgTypeBean.setSearchField("pmParameter.parameterCode");
					orgTypeBean.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgCode());
					orgTypeSearchBean.getDataFilterBeans().add(orgTypeBean);
				}
			}
		}
		return this.orgTypeSearchBean;
	}

	public boolean isNotEpAdmin() {
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
			return true;
		}
		return false;
	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getSelectedOrgType().getParameterId());
		sb.append("|recordStatus^1");
		logger.info("....getManagedBean().getSelectedOrgType().getParameterCode()="
				+ getManagedBean().getSelectedOrgType().getParameterCode());
		logger.info("....this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()="
				+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		logger.info("....getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()="
				+ getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()));

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(getManagedBean().getSelectedOrgType().getParameterCode(),
				getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));

		return sb.toString();
	}

	public String constructConditionFieldsForCreate() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode());
		sb.append("|orgTypeCode.parameterType^");
		sb.append(ParameterTypeConst.ORGANISATION_TYPES);
		sb.append("|recordStatus^1");
		logger.info("....getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode()="
				+ getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode());
		logger.info("....this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()="
				+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		logger.info("....getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()="
				+ getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()));

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
				getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode(),
				getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));
		return sb.toString();
	}

	private void generateAdditionalFilter() {
		logger.info("generateAdditionalFilter");
		this.getManagedBean().setAdditionalOrgFilter("");
		// generate additional filter
		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter("|orgProfileId^"
					+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId() + "*parentOrgProfile.orgProfileId^"
					+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter("|orgProfileId^"
					+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId() + "*parentOrgProfile.orgProfileId^"
					+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^" + this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^" + this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"orgProfileId^" + this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		}
	}

	public void updateSearchCriteriaForRole(BaseEntity entity) {
		logger.info("...............updateSearchCriteriaForRole...............");
		getManagedBean().getUserProfileSearchBean().setUserRoleStatus(null);
		if (entity != null) {
			PmRoleMatrix pmRoleMatrix = (PmRoleMatrix) entity;
			if (pmRoleMatrix != null) {
				String userRole = pmRoleMatrix.getAssigneeRole().getRoleCode();
				this.getManagedBean().getUserProfileSearchBean().setUserRole(userRole);
			}
		}
	}

	public void updateSearchCriteriaForOrgName(BaseEntity entity) {
		logger.info("updateSearchCriteriaForOrgName");

		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(orgProfile.getOrgProfileId());
	}

	public void getListOfAssignedRoles(PmUserOrg org) {
		if (org != null) {
			this.getManagedBean().setActiveUserOrg(org);//not active only
			this.getManagedBean().setActiveUserRoleFor(new ArrayList<PmUserRole>());
			for (PmUserRole pmUserRole : this.getManagedBean().getActiveUserOrg().getPmUserRoles()) {
				this.getManagedBean().getActiveUserRoleFor().add(pmUserRole);
			}
		}
	}
	
	public void getListOfAssignedRoles(Long userOrgId) {
		if (userOrgId != null) {
			
			IUserSO userSO = null;
			try {
	
				UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
				userOrgReqDTO.setUserOrgId(userOrgId);
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
				UserOrgResDTO resDto = userSO.retrieveUserOrgByUserOrgId(userOrgReqDTO);
				
				if (resDto != null && resDto.isSuccessful()) {
					this.getManagedBean().setActiveUserOrg(resDto.getPmUserOrg()); //not active only
					this.getManagedBean().setActiveUserRoleFor(new ArrayList<PmUserRole>());
					for (PmUserRole pmUserRole : this.getManagedBean().getActiveUserOrg().getPmUserRoles()) {
						this.getManagedBean().getActiveUserRoleFor().add(pmUserRole);
					}
				}
			} finally {
				userSO = null;
			}
		}
	}

	/**
	 * @return the tabUserProfile
	 */
	public int getTabUserProfile() {
		return TAB_USER_PROFILE;
	}

	/**
	 * @return the tabAuthorization
	 */
	public int getTabAuthorization() {
		return TAB_AUTHORIZATION;
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

	public ProfileManagementBackingBean() {

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			logger.info("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

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

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.info(message);
		}
		logger.info(message);
	}

	/**
	 * load clean view for Profile Maintenance
	 */
	public void preLoadingUserProfile() {
		logger.info("preLoadingUserProfile");
		this.setCurrentActiveOrg();
		this.getManagedBean().setSearchOrgCategory(FEDERAL_GOVT);
		getManagedBean().setLazy(false);
		this.resetUserProfileSearchBean();
		this.retrieveOrgTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/**
	 * reset basic search bean
	 */
	public void resetUserProfileSearchBean() {
		logger.info("resetUserProfileSearchBean");
		this.getManagedBean().setUserProfileSearchBean(new UserProfilesSearchBean());
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		this.getManagedBean().setSelectedRoleDesc(null);
		getManagedBean().setLazy(false);
		userOrgDataModel = new UserProfilePagingDataModel();
		retrieveOrgTypeAndOrgName();
		
		ISearchBean<Object[]> searchBean = null;
		if (getManagedBean().getSearchOrgCategory() == FEDERAL_GOVT) {
			searchBean = getUserOrgTypeObjectSearchBean("somFederalOrgType");
		} else if (getManagedBean().getSearchOrgCategory() == OTHERS) {
			searchBean = getUserOrgTypeObjectSearchBean("somOtherOrgType");
		}

		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownObjectValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}
		
		getManagedBean().setOrgTypeList(items);
	}

	/**
	 * USER PROFILE - START
	 */

	public String getParameterDescNullable(PmParameter parameter) {
		logger.info("getParameterDescNullable");
		if (parameter != null) {
			return getParameterDesc(parameter);
		} else {
			return "";
		}
	}

	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		logger.info("setCurrentActiveOrg");
		boolean isFactoring = false;
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
			isFactoring = true;
		}
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes().getUserSessionBean().getUserId(), isFactoring);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
	}

	/**
	 * redirect to user details
	 * 
	 * @param pmUser
	 * @return
	 */
	public String redirectToMaintainUser(Long userId, Long userOrgId) throws Exception {
		logger.info("redirectToMaintainUser");
		getManagedBean().setRoleTabActiveIndex(0);
		this.setCurrentActiveOrg();
		this.generateAdditionalFilter();
		this.retrieveIdentityTypeMap();
		this.retrieveOrgTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		getManagedBean().setSearchRoleName(null);
		getManagedBean().setSelectedRoleSearchOption(null);
		getManagedBean().setPmUserExist(false);
		logger.info("selectedOrg=" + userOrgId);
		if (userOrgId != null) {
			logger.info("selectedOrg.getUserOrgId()=" + userOrgId);
		}
		if (userId != null && userId > 0) {
			loadUserProfile(userId, userOrgId);
		} else {
			initialUserProfile();
		}
		RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * redirect to main page
	 * 
	 * @return String
	 */
	public String redirectToMainPage() {
		logger.info("redirectToMainPage");

		return "/xhtml/pm/profilemanagement/search/profileManagementMain.xhtml";

	}

	/**
	 * load pm user object by id
	 * 
	 * @param id
	 */
	private void loadUserProfile(Long userId, Long userOrgId) {
		logger.info("loadUserProfile - Start");
		logger.info("loadUserProfile - userId=" + userId);
		logger.info("loadUserProfile - selectedOrg=" + userOrgId);
		try {
			if (userId == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}
			// load user
			PmUserOrg pmUserOrg = PmCommonUtil.retrieveUserOrgByUserIdAndOrgId(userId, userOrgId);
			logger.info("pmUserOrg=" + pmUserOrg);
			pmUserOrg.setPMP(pmUserOrg.isPMP() ? true : false);
			PmUser pmUser = pmUserOrg.getPmUser();
			logger.info("#26766 loadUserProfile - fetched pmUser.CHANGED_BY ID: " + pmUser.getChangedBy());
			pmUser.setIdentificationCode(
					pmUser.getIdentificationCode() == null ? new PmParameter() : pmUser.getIdentificationCode());
			pmUser.setSalutationCode(
					pmUser.getSalutationCode() == null ? new PmSalutation() : pmUser.getSalutationCode());
			this.getManagedBean().setPmUserOriginal((PmUser) pmUser.cloneEntity());
			this.getManagedBean().setPmUserOrgOriginal((PmUserOrg) pmUserOrg.cloneEntity());

			if (DbValueConst.RECORD_STATUS_ACTIVE != pmUserOrg.getRecordStatusFlg()
					|| DbValueConst.RECORD_STATUS_ACTIVE != pmUser.getRecordStatusFlg()) {
				getManagedBean().setActiveUser(false);
			} else {
				getManagedBean().setActiveUser(true);
			}

			for (PmUserRole userRole : pmUserOrg.getPmUserRoles()) {//put letterNo and approvalLimit in maps to check later changes for tracking diary
				if (userRole.getPmRole().getRoleCode().equals("FL_APPROVER")) {
					this.getManagedBean().getRoleCodeToLetterNoMap().put(userRole.getPmRole().getRoleCode(),
							userRole.getLetterNo());
				}
				if (userRole.getPmRole().isLimitRequired()) {
					this.getManagedBean().getRoleCodeToApprovalLimitMap().put(userRole.getPmRole().getRoleCode(),
							userRole.getApprovalLimit());
				}

			}

			// load preference
			PmCommonUtil.loadPmUserPreference(pmUser);

			// load attachment
			if (pmUser.getPmUserAttachments() == null) {
				logger.info("...........attachment list is empty");
				pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());
			}

			// load password
			pmUser.setPassword(null);
			pmUser.setConfirmPassword(null);

			this.getManagedBean().setSelectedUser(pmUser);
			this.getManagedBean().setDeleteUserRoles(null);
			this.getManagedBean().setDeleteReceivingAddrTypes(null);
			this.getManagedBean().setSelectedDeliveringAddrTypes(null);
			this.getManagedBean().setSelectedUserOrg(null);
			this.getManagedBean().setSelectedRoles(null);
			this.getManagedBean().setSelectedReceivingAddrTypes(null);
			this.getManagedBean().setReceivingAddressFlag(false);
			this.getManagedBean().setDeletePmUserAttachments(null);
			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
			this.getManagedBean().setSelectedUserOrg(pmUserOrg);//retrive userORg with relations again
			showReceivingAddress();
			this.getManagedBean().setIsNewUser(false);
			// set org category
			if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_FEDERAL_GOVT_UP)
					.contains(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
				getManagedBean().setOrgCategory(FEDERAL_GOVT);
			} else if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_OTHERS_UP)
					.contains(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
				getManagedBean().setOrgCategory(OTHERS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
		logger.info("loadUserProfile - End");
	}

	/**
	 * load pm user object by id
	 * 
	 * @param id
	 */
	private void loadUserInfo(Long id) {
		logger.info("loadUserInfo - Start");
		try {

			// load user
			PmUser pmUser = PmCommonUtil.retrieveUserById(id, true);
			if (pmUser != null) {
				// if pm user exist (previously created)
				getManagedBean().setPmUserExist(true);

				pmUser.setIdentificationCode(
						pmUser.getIdentificationCode() == null ? new PmParameter() : pmUser.getIdentificationCode());
				pmUser.setSalutationCode(
						pmUser.getSalutationCode() == null ? new PmSalutation() : pmUser.getSalutationCode());
				
				// #35562- Start
				if (!pmUser.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
					pmUser.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				}
				// #35562- End

				PmUserOrg pmUserOrg = this.getManagedBean().getSelectedUserOrg();
				pmUserOrg.setPmUser(pmUser);
				pmUser.getPmUserOrgs().add(pmUserOrg);
				this.getManagedBean().setSelectedUserOrg(pmUserOrg);
				// the following info need to use for Tracking Dairy
				this.getManagedBean().setPmUserOriginal((PmUser) pmUser.cloneEntity());
				this.getManagedBean().setPmUserOrgOriginal((PmUserOrg) pmUserOrg.cloneEntity());

			}

			this.getManagedBean().setSelectedUser(pmUser);
			this.getManagedBean().setDeleteUserRoles(null);
			this.getManagedBean().setDeleteReceivingAddrTypes(null);
			this.getManagedBean().setSelectedDeliveringAddrTypes(null);
			this.getManagedBean().setSelectedRoles(null);
			this.getManagedBean().setSelectedReceivingAddrTypes(null);
			this.getManagedBean().setReceivingAddressFlag(false);
			this.getManagedBean().setDeletePmUserAttachments(null);
			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
		logger.info("loadUserInfo - End");
	}

	private void clearUserOrgInfo(PmUserOrg userOrg) {
		userOrg.setDesignation(null);
		userOrg.setGrade(null);
		userOrg.setEmail(null);
		userOrg.setPhoneNo(null);
		userOrg.setFaxNo(null);
		userOrg.setMobileNo(null);
	}

	/**
	 * initial new pmUser object
	 */
	private void initialUserProfile() {
		logger.info("initialUserProfile");
		// load user attributes
		PmCountry defaultCountry = PmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		PmUser pmUser = new PmUser();
		pmUser.setPmCountry(defaultCountry);

		PmParameter identityType = new PmParameter();
		identityType.setParameterCode(ProfileManagementConst.DEFAULT_IDENTIFY_TYPE);
		pmUser.setIdentificationCode(identityType);

		pmUser.setSalutationCode(new PmSalutation());

		pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());

		// fill up relations to PmUser
		PmUserOrg pmUserOrg = new PmUserOrg();
		pmUserOrg.setPMP(false);
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		pmOrgProfile.setOrgTypeCode(new PmParameter());
		pmOrgProfile.setPmOrgValidity(new PmOrgValidity());
		pmOrgProfile.setPmFinancialOrg(new PmFinancialOrg());
		pmUserOrg.setPmOrgProfile(pmOrgProfile);
		pmUserOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
		pmUserOrg.setPmUser(pmUser);

		if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			if (getManagedBean().getCurrentActiveOrg() != null) {
				pmUserOrg.setPmOrgProfile(getManagedBean().getCurrentActiveOrg());
			}
			pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
		}

		pmUser.getPmUserOrgs().add(pmUserOrg);
		this.getManagedBean().setSelectedUserOrg(pmUserOrg);
		logger.info(
				".........this.getManagedBean().getSelectedUserOrg()=" + this.getManagedBean().getSelectedUserOrg());

		pmUser.setNotifyByEmail(ProfileManagementConst.DEFAULT_IS_NOTIFY_BY_EMAIL);

		// initial preference
		PmCommonUtil.loadPmUserPreference(pmUser);

		// initial address
		this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());

		// load password
		pmUser.setPassword(null);
		pmUser.setConfirmPassword(null);
		pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());

		if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			getManagedBean().setOrgCategory(OTHERS);
		} else {
			getManagedBean().setOrgCategory(FEDERAL_GOVT);
		}
		this.getManagedBean().setAddressEntered(false);
		this.getManagedBean().setSelectedUser(pmUser);
		this.getManagedBean().setSelectedRoles(null);
		this.getManagedBean().setDeleteUserRoles(null);
		this.getManagedBean().setDeleteReceivingAddrTypes(null);
		this.getManagedBean().setSelectedDeliveringAddrTypes(null);
		this.getManagedBean().setSelectedReceivingAddrTypes(null);
		this.getManagedBean().setDeletePmUserAttachments(null);
		this.getManagedBean().setDeletePortrait(false);
		this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
		this.getManagedBean().setIsNewUser(true);
		logger.info("login " + this.getManagedBean().getSelectedUser().getLoginId());
	}

	/**
	 * populate user address to managed bean
	 * 
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser) {
		logger.info("populateUserAddress");
		try {
			List<PmAddressType> addressTypes = PmCommonUtil
					.retrieveAddressTypesByUserIdOrAddressType(pmUser.getUserId(), ProfileManagementConst.ADDRESS_USER);
			this.getManagedBean().setUserAddress((addressTypes != null && !addressTypes.isEmpty())
					? addressTypes.get(0).getPmAddress() : PmCommonUtil.getNewPmAddress());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770017, new String[] { e.getMessage() });
		}
	}

	/**
	 * retrieve salutation list
	 * 
	 * @return List<PmSalutation>
	 */
	public List<PmSalutation> salutationOptions() {
		logger.info("salutationOptions");
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
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
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

	public Date getCurrentDate() {
		Date currentDate = new Date();
		return currentDate;
	}

	public void addressEntered() {
		logger.info("addressEntered");
		logger.info("addressEntered: " + this.getManagedBean().isAddressEntered());
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
		return getDropdownCodeValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}

	/**
	 * retrieve all identity type into mapper
	 */
	private void retrieveIdentityTypeMap() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(ParameterTypeConst.IDENTIFICATION_TYPES, null);

		identityTypeMapper = new HashMap<String, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterCode(), pmDesc.getPmParameter());
		}
	}

	/**
	 * retrieve all org type into mapper
	 */
	private void retrieveOrgTypeMap() {
		logger.info("....retrieveOrgTypeMap....");
		List<PmParameterDesc> parameterDescs = PmCommonUtil
				.retrieveFilteredParamDesc(ParameterTypeConst.ORGANISATION_TYPES, null, null);

		orgTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}

	}

	/**
	 * set user identity type
	 */
	public void identityTypeChange() {
		logger.info("identityTypeChange");

		if (this.getManagedBean().getSelectedUser().getIdentificationCode().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) identityTypeMapper
					.get(this.getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode());
			this.getManagedBean().getSelectedUser().getIdentificationCode()
					.setParameterCode(pmParameter.getParameterCode());
		}
	}

	/**
	 * login id = identification no.
	 */
	public void updateLoginId() {
		this.getManagedBean().getSelectedUser()
				.setLoginId(this.getManagedBean().getSelectedUser().getIdentificationNo());
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
	 * USER PROFILE - END
	 */

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
	 * TAB Authorization - START
	 */
	/**
	 * Enabled/disabled eff date
	 * 
	 * @param orgEffDate
	 * @return boolean
	 */
	public boolean editableOrg(Timestamp orgEffDate) {
		logger.info("editableOrg");
		Date today = new Date();
		if (orgEffDate != null) {
			Date orgDate = new Date(orgEffDate.getTime()); // DateUtils.convertStringToDate("dd/MM/yyyy", argValue);
			if (today.compareTo(orgDate) > 0) {
				// today > orgDate
				return false;
			} else if (today.compareTo(orgDate) == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}

	}

	/**
	 * generate min date
	 * 
	 * @return String (date in string)
	 */
	public String getMinDate() {
		logger.info("getMinDate");
		String dateStr = "";
		Date today = new Date();
		dateStr = DateUtils.dateToString("dd/MM/yyyy", today);
		return dateStr;
	}

	/**
	 * To add new item to Responsible Org. Unit list
	 */
	public void addOrgUnit() {
		logger.info("addOrgUnit");
		if (this.getManagedBean().getSelectedUser().getPmUserOrgs() == null) {
			this.getManagedBean().getSelectedUser().setPmUserOrgs(new ArrayList<PmUserOrg>());
		}

		PmUserOrg pmUserOrg = new PmUserOrg();
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		pmOrgProfile.setOrgTypeCode(new PmParameter());
		pmOrgProfile.setPmOrgValidity(new PmOrgValidity());
		pmUserOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
		pmUserOrg.setPmOrgProfile(pmOrgProfile);
		pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
		this.getManagedBean().getSelectedUser().addPmUserOrg(pmUserOrg);
		this.getManagedBean().setSelectedUserOrg(pmUserOrg);
		showReceivingAddress();
	}

	/**
	 * Remove selected Responsible Org. Unit
	 */
	public void deleteOrgUnit(PmUserOrg pmUserOrg) {
		logger.info("deleteOrgUnit");
		if (pmUserOrg != null && pmUserOrg.isBrandNew()) {
			if (this.getManagedBean().getSelectedUser().getPmUserOrgs() == null) {
				this.getManagedBean().getSelectedUser().setPmUserOrgs(new ArrayList<PmUserOrg>());
			}
			this.getManagedBean().getSelectedUser().getPmUserOrgs().remove(pmUserOrg);
		}
	}

	/**
	 * validate unique responsible org unit
	 * 
	 * @param pmOrgProfile
	 */
	public void updateOrgCode(PmOrgProfile pmOrgProfile, int index) {
		logger.info("updateOrgCode");

		boolean isValid = true;
		if (pmOrgProfile != null && pmOrgProfile.getOrgProfileId() != null) {
			if (this.getManagedBean().getSelectedUser().getPmUserOrgs() != null
					&& !this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()) {
				int i = 0;
				for (PmUserOrg pmUserOrg : this.getManagedBean().getSelectedUser().getPmUserOrgs()) {
					if (pmUserOrg.getPmOrgProfile().getOrgProfileId().equals(pmOrgProfile.getOrgProfileId())
							&& i != index) {
						isValid = false;
						break;
					}
					i++;
				}
				if (!isValid) {
					this.getManagedBean().getSelectedUser().getPmUserOrgs().get(index).getPmOrgProfile()
							.setPmOrgValidity(new PmOrgValidity());
					throw new EpException(MessageConst.ERROR_770019);
				}

			}
		}
	}

	public void receivAddStatusChange(PmAddressType addressType) {
		logger.info("receivAddStatusChange");
		if (addressType != null) {

			int status = addressType.getRecordStatusFlg();
			if (status == DbValueConst.RECORD_STATUS_SUSPENDED) {

				if (isAnyPendingTaskForAdressType(addressType, this.getManagedBean().getSelectedUser().getLoginId())) {
					// revert it to active
					addressType.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw new EpException(MessageConst.ERROR_770170);
				}
			}
			// #35489 - Start
			addressType.setChangedBy(getUserIdSessionFromLoginId());
			// #35489 - End
		}
		getManagedBean().setRoleTabActiveIndex(1);

	}
	   
	//#36489 - Start
	/**
	 * check if user still have:
	 * 1) pending task (BPM)
	 * 2) pending task for user role (any module transaction)
	 * 3) active at any user group (FL Approver and RN Approver)
	 * 4) active at Supplier User Appointment in Org Profile Maintenance
	 */
	public void orgUnitStatusChange() {
		logger.info("orgUnitStatusChange");
		if (validateUserOrg()) {
			int status = this.getManagedBean().getSelectedUserOrg().getRecordStatusFlg();
			if (status == DbValueConst.RECORD_STATUS_SUSPENDED) {
				EpException ex;
				if (isAnyPendingTaskForUser(this.getManagedBean().getSelectedUser().getLoginId())) {
					// revert it to active
					this.getManagedBean().getSelectedUserOrg().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw new EpException(MessageConst.ERROR_770167);
				} else if ((ex = isAnyPendingDocForUserRole(validateUserOrgandRole(), this.getManagedBean()
						.getSelectedUser().getUserId())) != null) {
					// revert it to active
					this.getManagedBean().getSelectedUserOrg().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw ex;
				} else if (isActiveUserGroupUser() != null) {
					this.getManagedBean().getSelectedUserOrg().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					throw new EpException(MessageConst.ERROR_770202, isActiveUserGroupUser());
				} else if (validateUserOrgProfileAndStatus()) {
					checkIsActiveGovtSellerUser();
				} else {
					// if inactive set expire Date to SyetemDate
					inactiveUserOrgRoleAndReceivingAddress(status);
				}
			}
			//#35489 - Start
			this.getManagedBean().setOrgUnitStatusChange(true);
			//#35489 - End
		}
	}
	//#36489 - End

	public void userStatusChange() {
		logger.info("userStatusChange");
		// check for any pending tasks belong to this user

		if (this.getManagedBean().getSelectedUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			if (isAnyPendingTaskForUser(this.getManagedBean().getSelectedUser().getLoginId())) {
				// revert it to active
				this.getManagedBean().getSelectedUser().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				// hit error and exit
				throw new EpException(MessageConst.ERROR_770166);
			} else {

				if (this.getManagedBean().getSelectedUser().getPmUserOrgs() != null
						&& !this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()) {
					for (PmUserOrg pmUserOrg : this.getManagedBean().getSelectedUser().getPmUserOrgs()) {
						pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						if (pmUserOrg.getPmUserRoles() != null) {
							for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
								pmUserRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
								userRoleStatusChange(pmUserRole);
							}
						}
						for (PmAddressType addressType : this.getManagedBean().getSelectedReceivingAddrTypes()) {
							addressType.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}
					}
				}
			}
		}
		// #35489 - Start
		this.getManagedBean().setUserStatusChange(true);
		// #35489 - End
	}

	public void resetOrgTypeAndCode() {
		PmOrgProfile orgProfile = new PmOrgProfile();
		orgProfile.setPmOrgValidity(new PmOrgValidity());
		orgProfile.setPmFinancialOrg(new PmFinancialOrg());
		orgProfile.setOrgTypeCode(new PmParameter());
		getManagedBean().getSelectedUserOrg().setPmOrgProfile(orgProfile);
	}

	/**
	 * Reset PmUserOrg info
	 * 
	 * @param index
	 */
	public void changeResOrgType() {
		logger.info("changeResOrgType");
		// clear role list
		this.getManagedBean().getSelectedUserOrg().setPmUserRoles(new ArrayList<PmUserRole>());
		// clear reciving Addresses
		showReceivingAddress();

		if (this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) orgTypeMapper.get(
					this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId());
			PmOrgProfile orgProfile = new PmOrgProfile();
			orgProfile.setPmOrgValidity(new PmOrgValidity());
			orgProfile.setPmFinancialOrg(new PmFinancialOrg());
			orgProfile.setOrgTypeCode(pmParameter);
			getManagedBean().getSelectedUserOrg().setPmOrgProfile(orgProfile);
		}
	}

	public void changeOrgUnit(BaseEntity entity) {
		logger.info("changeOrgUnit");
		if (getManagedBean().getSelectedUserOrg() != null
				&& getManagedBean().getSelectedUserOrg().getPmOrgProfile() != null
				&& getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgProfileId() != null) {
			if (getManagedBean().getSelectedCurrentOrgProfile() == null) {
				getManagedBean().setSelectedCurrentOrgProfile(getManagedBean().getSelectedUserOrg().getPmOrgProfile());
			} else {
				if (!getManagedBean().getSelectedCurrentOrgProfile().getOrgProfileId()
						.equals(getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgProfileId())) {
					// clear role list
					this.getManagedBean().getSelectedUserOrg().setPmUserRoles(new ArrayList<PmUserRole>());
					// clear reciving Addresses
					showReceivingAddress();
					getManagedBean()
							.setSelectedCurrentOrgProfile(getManagedBean().getSelectedUserOrg().getPmOrgProfile());
				}
			}
		}
	}

	public void clearOrgNameAndRoleLovSearchFields() {
		logger.info("clearOrgNameAndRoleLovSearchFields");

		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setSelectedRoleDesc(null);
		getManagedBean().getUserProfileSearchBean().setUserRole(null);
		getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(null);
		getManagedBean().getUserProfileSearchBean().setUserRoleStatus(null);
		if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) orgTypeMapper
					.get(this.getManagedBean().getSelectedOrgType().getParameterId());
			this.getManagedBean().getSelectedOrgType().setParameterCode(pmParameter.getParameterCode());
		} else {
			this.getManagedBean().getSelectedOrgType().setParameterCode(null);
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
	 * Get Role description by role code and language.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @param languageCode
	 *            the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode, String languageCode) {
		logger.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}

	/**
	 * check unique role from the ui list or from DB
	 * 
	 * @param orgTypeCode
	 * @param roleCode
	 * @param searchDB
	 * @return boolean
	 */
	private boolean checkUniqueRole(String orgTypeCode, String roleCode, boolean searchDB) {
		boolean isExist = false;
		if (searchDB) {
			SearchBean<PmUser> userSearchBean = new SearchBean<PmUser>();

			userSearchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserOrgs.pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_EQUALS.value(), roleCode, true));

			userSearchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserOrgs.pmUserRoles.recordStatusFlg",
					SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));

			userSearchBean.getDataFilterBeans()
					.add(new SearchFilterBean("pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(), orgTypeCode, true));

			List<PmUser> users = PmCommonUtil.searchUsersBySearchBean(userSearchBean);
			if (users != null && !users.isEmpty()) {
				isExist = true;
			}
		} else {
			if (this.getManagedBean().getSelectedUser().getPmUserOrgs() != null) {
				for (PmUserOrg userOrg : this.getManagedBean().getSelectedUser().getPmUserOrgs()) {
					for (PmUserRole userRole : userOrg.getPmUserRoles()) {
						if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()) {
							// check if contains unique role
							if (roleCode.equals(userRole.getPmRole().getRoleCode())) {
								if (orgTypeCode.equals(userOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
									isExist = true;
									break;
								}
							}
						}
					}
				}
			}
		}

		return isExist;
	}

	/*
	 * Add new selected role (single) to user role list
	 */
	public void confirmRoles() {
		logger.info("confirmRoles");
		roleValidation();

		// Add to user role list
		PmUserRole pmUserRole = new PmUserRole();
		pmUserRole.setPmRole(this.getManagedBean().getSelectedRole().getPmRole());
		this.getManagedBean().getSelectedUserOrg().addPmUserRole(pmUserRole);
		this.getManagedBean().setSelectedRole(null); // clear
		updateReceivingAddressFlag();

		RequestContext.getCurrentInstance().execute("dlRoleOptions.hide()");

	}

	/**
	 * Role validation (unique org role, single role & role restriction)
	 */
	private void roleValidation() {
		logger.info("roleValidation");

		if (this.getManagedBean().getSelectedRole() == null) {
			throw new EpException(MessageConst.ERROR_770022);
		}

		if (this.getManagedBean().getSelectedUserOrg() == null) {
			throw new EpException(MessageConst.ERROR_770021);
		}

		// Check single role for all users
		if (this.getManagedBean().getSelectedRole().getPmRole().isSingleRole()) {
			if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
					&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
				for (PmUserRole pmUserRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
					if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						throw new EpException(MessageConst.ERROR_770134);
					}
				}
			}
		} else {
			if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
					&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
				for (PmUserRole pmUserRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {

					if (pmUserRole.getPmRole().isSingleRole()
							&& (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)) {
						throw new EpException(MessageConst.ERROR_770134);
					}
				}
			}
		}

		if (roleRestrictionValidation(this.getManagedBean().getSelectedRole().getPmRole())) {
			throw new EpException(MessageConst.ERROR_770135,
					new String[] { this.getManagedBean().getSelectedRole().getRoleName() });
		}
		
		//[22117] - start
		// if login user is BPK_ADMIN
		// validate if selected user has BPK_ADMIN role and selected new role is COMMON_USER or PPB_CHAIRPERSON
		logger.info("[#22117 - start]");
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
			if (!roleRestrictionValidation(this.getManagedBean().getSelectedRole().getPmRole())) {
				if(this.getManagedBean().getSelectedUserOrg().getPmOrgProfile() != null
						&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode() != null
						&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_BPK)) {
					logger.info("[#22117] Org code - " + this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode());
					logger.info("[#22117] Selected role - " + this.getManagedBean().getSelectedRole().getPmRole().getRoleCode());
					logger.info("[#22117] Search for BPK_ADMIN role... ");
					for (PmUserRole userRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
						if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()
								&& userRole.getPmRole().getRoleCode().equals(RoleCode.BPK_ADMIN.value())
								&& (this.getManagedBean().getSelectedRole().getPmRole().getRoleCode().equals(RoleCode.COMMON_USER.value()) 
										|| this.getManagedBean().getSelectedRole().getPmRole().getRoleCode().equals(RoleCode.PPB_CHAIRPERSON.value()))) {
							logger.info("[#22117] Found match condition - Throw Exception!!");
							//to restrict only role EP_ADMIN or EP_ADMIN_2 can assign role
							String errMsg = this.getManagedBean().getSelectedRole().getRoleName();
							throw new EpException(MessageConst.ERROR_770230, new String[] { errMsg });						
						}
					}
					logger.info("[#22117] BPK_ADMIN not found!!");
				}
			}
		}
		//[22117] - end
	}

	public boolean roleRestrictionValidation(PmRole pmRole) {
		// Check role conflict/restriction
		List<PmUserRole> activeRoles = new ArrayList<PmUserRole>();
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {
			for (PmUserRole userRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()
						&& !userRole.getPmRole().getRoleCode().equals(pmRole.getRoleCode())) {
					activeRoles.add(userRole);
				}
			}
		}

		boolean roleRestictionError = false;
		if (pmRole.isRestrRole()) {
			if (activeRoles != null && !activeRoles.isEmpty()) {
				if (PmCommonUtil.checkRoleRestriction(pmRole.getRoleCode(), activeRoles)) {
					roleRestictionError = true;

				}
			}
		}
		return roleRestictionError;
	}

	/**
	 * update ui receiving address tab if selected roles contain "Receiving Role"
	 */
	private void updateReceivingAddressFlag() {
		logger.info("updateReceivingAddressFlag");
		boolean isReceivingOfficer = false;
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
			for (PmUserRole userRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
				if (ProfileManagementConst.RECEIVING_OFFICER.equals(userRole.getPmRole().getRoleCode())) {
					this.getManagedBean().setReceivingAddressFlag(true);
					isReceivingOfficer = true;
					break;
				}
			}
			if (!isReceivingOfficer) {
				if (getManagedBean().getSelectedReceivingAddrTypes() != null
						&& !getManagedBean().getSelectedReceivingAddrTypes().isEmpty()) {
					List<PmAddressType> removeNewAddressTypes = new ArrayList<PmAddressType>();
					for (PmAddressType addType : getManagedBean().getSelectedReceivingAddrTypes()) {
						if (addType.isBrandNew()) {
							removeNewAddressTypes.add(addType);
						}
					}
					if (!removeNewAddressTypes.isEmpty()) {
						getManagedBean().getSelectedReceivingAddrTypes().removeAll(removeNewAddressTypes);
					}
				}
			}
		} else {
			this.getManagedBean().setReceivingAddressFlag(false);
		}
	}

	/**
	 * Delete selected Role
	 */
	public void deleteRole() {
		logger.info("deleteRole");
		checkDeleteRoles();

		if (this.getManagedBean().getDeleteUserRoles() != null
				&& !this.getManagedBean().getDeleteUserRoles().isEmpty()) {
			if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() == null) {
				this.getManagedBean().getSelectedUserOrg().setPmUserRoles(new ArrayList<PmUserRole>());
			}
			for (PmUserRole userRole : this.getManagedBean().getDeleteUserRoles()) {
				if (userRole.isBrandNew()) {
					this.getManagedBean().getSelectedUserOrg().getPmUserRoles().remove(userRole);
				}
			}
			this.getManagedBean().getDeleteUserRoles().clear();
			updateReceivingAddressFlag();
		}
	}

	/**
	 * Remove check box tick from data table
	 * 
	 * @param AjaxBehaviorEvent e
	 */
	public void unSelectRole(AjaxBehaviorEvent e) {
		if (this.getManagedBean().getDeleteUserRoles() != null
				&& !this.getManagedBean().getDeleteUserRoles().isEmpty()) {
			Iterator<PmUserRole> iterator = getManagedBean().getDeleteUserRoles().iterator();
			while (iterator.hasNext()) {
				PmUserRole userRole = iterator.next();
				if (!userRole.isBrandNew()) {
					iterator.remove();
				}
			}
			// check after iterator if any record remained in the delete list
			if (this.getManagedBean().getDeleteUserRoles().size() > 0) {
				setDisableDeleteRole(false);
			} else {
				setDisableDeleteRole(true);
			}
		}
	}

	public void checkDeleteRoles() {
		if (this.getManagedBean().getDeleteUserRoles() != null
				&& !this.getManagedBean().getDeleteUserRoles().isEmpty()) {
			List<PmUserRole> deleteRoles = new ArrayList<PmUserRole>();
			for (PmUserRole userRole : this.getManagedBean().getDeleteUserRoles()) {
				if (!userRole.isBrandNew()) {
					deleteRoles.add(userRole);
				}
			}
			for (PmUserRole userRole : deleteRoles) {
				this.getManagedBean().getDeleteUserRoles().remove(userRole);
			}

		}
	}

	/**
	 * Validate if selected roles are not duplicated
	 * 
	 * @return boolean
	 */
	private boolean isUniqueRoles() {
		logger.info("isUniqueRoles");
		boolean isUnique = true;
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
			List<PmRole> oriPmRoles = new ArrayList<PmRole>();

			for (PmUserRole obj : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
				if (obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					oriPmRoles.add(obj.getPmRole());
				}
			}

			if (oriPmRoles.contains(this.getManagedBean().getSelectedRole().getPmRole())) {
				isUnique = false;
			}
		}
		return isUnique;
	}

	/**
	 * Track user role status
	 * 
	 * @param pmUserRole
	 */
	public void userRoleStatusChange(PmUserRole pmUserRole) {
		logger.info("userRoleStatusChange");

		if (pmUserRole != null) {
			if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				EpException ex = null;
				if (isAnyPendingTaskForUserRole(pmUserRole, this.getManagedBean().getSelectedUser().getLoginId())) {
					// revert it to active
					pmUserRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw new EpException(MessageConst.ERROR_770168);
				} else if ((ex = isAnyPendingDocForUserRole(pmUserRole, this.getManagedBean().getSelectedUser().getUserId())) != null) {
					// revert it to active
					pmUserRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw ex;
				} else if ((RoleCode.FL_APPROVER.value().equals(pmUserRole.getPmRole().getRoleCode())
						|| RoleCode.RN_APPROVER.value().equals(pmUserRole.getPmRole().getRoleCode()))
						//Bug #31509 - Start 
						&& isActiveAtUserGroupUser(pmUserRole.getPmRole().getRoleCode()) != null) {
					// check for FL Approver and RN Approver only revert it to active
					throw new EpException(MessageConst.ERROR_770202, isActiveAtUserGroupUser(pmUserRole.getPmRole().getRoleCode()));
					//Bug #31509 - End
				} else {
					if (!pmUserRole.isModified()) {
						pmUserRole.setModified(true);
					} else {
						pmUserRole.setModified(false);
					}
				}
				
				if (getManagedBean().getSelectedReceivingAddrTypes() != null
						&& !getManagedBean().getSelectedReceivingAddrTypes().isEmpty()) {
					//Bug #32656 - start
					List<PmAddressType> removeNewAddressTypes = new ArrayList<PmAddressType>();
					List<PmAddressType> removeNewAddressRecTypes = new ArrayList<PmAddressType>();
					for (PmAddressType addType : getManagedBean().getSelectedReceivingAddrTypes()) {
						//Bug #32656 - Check whether addressTypeId just remove or inactive 
						logger.info("userRoleStatusChange : inactive addressTypeId :" + addType.getAddressTypeId());
						if (addType.isBrandNew() && addType.getAddressTypeId()== null) {
							removeNewAddressTypes.add(addType);
							removeNewAddressRecTypes.add(addType);
						} else {
							if (DbValueConst.RECORD_STATUS_ACTIVE == addType.getRecordStatusFlg()) {
								addType.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
							}
						}
					}
					if (!removeNewAddressTypes.isEmpty()) {
						this.getManagedBean().getSelectedUser().getPmAddressTypes().removeAll(removeNewAddressTypes);
						this.getManagedBean().getSelectedReceivingAddrTypes().removeAll(removeNewAddressRecTypes);
					}
				}
				  //Bug #32656 - end
			} else if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				if (!pmUserRole.isModified()) {
					pmUserRole.setModified(true);
				} else {
					pmUserRole.setModified(false);
				}
			}

			// check for role restriction
			if (DbValueConst.RECORD_STATUS_ACTIVE == pmUserRole.getRecordStatusFlg()) {
				if (roleRestrictionValidation(pmUserRole.getPmRole())) {
					pmUserRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
					throw new EpException(MessageConst.ERROR_770135,
							new String[] { PmCommonUtil.getRoleDescByRoleCode(pmUserRole.getPmRole().getRoleCode()) });

				}
			}
		}
		getManagedBean().setRoleTabActiveIndex(0);
	}

	/**
	 * Check if any pending BPM Task for address type
	 * 
	 * @param addressType
	 * @param loginId
	 * @return boolean
	 */
	private boolean isAnyPendingTaskForAdressType(PmAddressType addressType, String loginId) {
		logger.info("isAnyPendingTaskForAdressType");
		boolean anyPendingTask = false;
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(43000L);
		statusIds.add(43004L);

		List<Integer> intStatusIds = new ArrayList<Integer>();
		intStatusIds.add(43000);
		intStatusIds.add(43004);

		IFulfilmentSO fulfilmentSO = null;
		try {

			fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentSO.class);
			List<String> noteNumberList = fulfilmentSO
					.getNumberOfFRNByAddressAndStatus(addressType.getPmAddress().getAddressId(), statusIds);
			if (noteNumberList != null && noteNumberList.size() > 0 && loginId != null && loginId.length() > 0) {
				anyPendingTask = PmCommonUtil.isAnyPendingTask(loginId, intStatusIds, noteNumberList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fulfilmentSO = null;
		}

		return anyPendingTask;
	}

	/**
	 * Check if any pending BPM task for user role
	 * 
	 * @param pmUserRole
	 * @param loginId
	 * @return
	 */
	private boolean isAnyPendingTaskForUserRole(PmUserRole pmUserRole, String loginId) {
		logger.info("isAnyPendingTaskForUserRole");
		boolean anyPendingTask = false;
		List<Integer> statusIds = retrieveAvailableStatusIdForRole(pmUserRole);
		if (statusIds != null && !statusIds.isEmpty()) {
			anyPendingTask = PmCommonUtil.isAnyPendingTask(loginId, statusIds);
		}
		return anyPendingTask;
	}
	
	/**
	 * Check if any pending document for user role
	 * 
	 * @param pmUserRole
	 * @param userId
	 * @return
	 */
	private EpException isAnyPendingDocForUserRole(PmUserRole pmUserRole, long userId) {
		List<PmUserRole> pmUserRoles = new ArrayList<PmUserRole>();
		pmUserRoles.add(pmUserRole);
		return isAnyPendingDocForUserRole(pmUserRoles, userId);
	}
	
	/**
	 * Check if any pending document for user roles
	 * 
	 * @param pmUserRoles
	 * @param userId
	 * @return
	 */
	private EpException isAnyPendingDocForUserRole(List<PmUserRole> pmUserRoles, long userId) {
		logger.info("isAnyPendingDocForUserRole");
		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(userId);
			reqDTO.setRoleCodes(new ArrayList<String>());
			if (CollectionUtils.isNotEmpty(pmUserRoles)) {
				for (PmUserRole pmUserRole : pmUserRoles) {
					if (pmUserRole.getPmRole() != null) {
						reqDTO.getRoleCodes().add(pmUserRole.getPmRole().getRoleCode());	
					}
				}
			}			
			userSO.checkAnyActiveTransactions(reqDTO);
		} catch (EpException epx) {
			return epx;
		} catch (Exception ex) {
			return new EpException(ex.getMessage());
		} finally {
			userSO = null;
		}
		
		return null;
	}

	/**
	 * retrieve status id list for BPM
	 * 
	 * @return
	 */
	private List<Integer> retrieveAvailableStatusIdForRole(PmUserRole pmUserRole) {
		logger.info("retrieveAvailableStatusId");
		List<Integer> statusIds = new ArrayList<Integer>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			if (pmUserRole != null) {
				reqDTO = new StatusReqDTO();
				List<String> roleCodeList = new ArrayList<String>();
				roleCodeList.add(pmUserRole.getPmRole().getRoleCode());
				reqDTO.setRoleCodeList(roleCodeList);

				IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
				StatusResDTO resDTO = statusSO.retrieveStatusRoleListByRoleCode(reqDTO);
				if (resDTO.isSuccessful()) {
					for (int i = 0; i < resDTO.getStatusRoleList().size(); i++) {
						statusIds.add(resDTO.getStatusRoleList().get(i).getPmStatus().getStatusId().intValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770094, new String[] { e.getMessage() });
		}
		return statusIds;
	}

	/**
	 * Show receiving addresses and user roles for selected responsible org.
	 * 
	 * @param index
	 */
	public void showReceivingAddress() {
		logger.info("showReceivingAddress");
		try {

			/* show selected org's receiving addr */
			if (this.getManagedBean().getSelectedUserOrg() != null
					&& this.getManagedBean().getSelectedUser().getPmAddressTypes() != null) {
				if (this.getManagedBean().getSelectedReceivingAddrTypes() == null) {
					this.getManagedBean().setSelectedReceivingAddrTypes(new ArrayList<PmAddressType>());
				}
				this.getManagedBean().getSelectedReceivingAddrTypes().clear();
				for (PmAddressType addressTypeObj : this.getManagedBean().getSelectedUser().getPmAddressTypes()) {
					if (ProfileManagementConst.ADDRESS_RECEIVING.equalsIgnoreCase(addressTypeObj.getAddressType())
							&& (addressTypeObj.getPmOrgProfile() != null
									&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgProfileId()
											.equals(addressTypeObj.getPmOrgProfile().getOrgProfileId()))) {
						this.getManagedBean().getSelectedReceivingAddrTypes().add(addressTypeObj);
					}
				}
				updateReceivingAddressFlag();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770023, new String[] { e.getMessage() });
		}
	}

	/**
	 * Add selected delivering address to receiving address
	 * 
	 * @param deliveringAddrs
	 */
	public void updateReceivingAddr(List<BaseEntity> deliveringAddrs) {
		logger.info("updateReceivingAddr");
		if (deliveringAddrs != null) {
			if (this.getManagedBean().getSelectedUser().getPmAddressTypes() == null) {
				this.getManagedBean().getSelectedUser().setPmAddressTypes(new ArrayList<PmAddressType>());
			}
			if (isUniqueAddress()) {
				logger.info("selected size " + deliveringAddrs.size());
				for (int i = 0; i < deliveringAddrs.size(); i++) {
					PmAddressType pmAddressType = (PmAddressType) deliveringAddrs.get(i);
					PmAddressType tempAddTypeObj = new PmAddressType();
					tempAddTypeObj.setAddressType(ProfileManagementConst.ADDRESS_RECEIVING);
					tempAddTypeObj.setPmAddress(pmAddressType.getPmAddress());
					tempAddTypeObj.setPmUser(this.getManagedBean().getSelectedUser());
					tempAddTypeObj.setPmOrgProfile(this.getManagedBean().getSelectedUserOrg().getPmOrgProfile());
					this.getManagedBean().getSelectedUser().addPmAddressType(tempAddTypeObj);
				}
				this.getManagedBean().getSelectedDeliveringAddrTypes().clear();
				showReceivingAddress();
			} else {
				throw new EpException(MessageConst.ERROR_770024);
			}
		}
	}

	/**
	 * Delete selected receiving address
	 */
	public void deleteReceivingAddr() {
		logger.info("deleteReceivingAddr");
		if (this.getManagedBean().getDeleteReceivingAddrTypes() != null
				&& !this.getManagedBean().getDeleteReceivingAddrTypes().isEmpty()) {
			if (this.getManagedBean().getSelectedReceivingAddrTypes() == null) {
				this.getManagedBean().setSelectedReceivingAddrTypes(new ArrayList<PmAddressType>());
			}
			for (PmAddressType pmAddressType : this.getManagedBean().getDeleteReceivingAddrTypes()) {
				this.getManagedBean().getSelectedUser().getPmAddressTypes().remove(pmAddressType);
			}
			this.getManagedBean().getDeleteReceivingAddrTypes().clear();
			showReceivingAddress();
		} else {
			throw new EpException(MessageConst.ERROR_770025);
		}
	}

	/**
	 * Validate if selected delivering address are not duplicated
	 * 
	 * @return boolean
	 */
	private boolean isUniqueAddress() {
		logger.info("isUniqueAddress");
		boolean isUnique = true;
		if (this.getManagedBean().getSelectedDeliveringAddrTypes() != null
				&& !this.getManagedBean().getSelectedDeliveringAddrTypes().isEmpty()) {
			if (this.getManagedBean().getSelectedReceivingAddrTypes() != null
					&& !this.getManagedBean().getSelectedReceivingAddrTypes().isEmpty()) {
				List<Long> deliveringAddr = new ArrayList<Long>();

				for (PmAddressType pmAddressType : this.getManagedBean().getSelectedDeliveringAddrTypes()) {
					deliveringAddr.add(pmAddressType.getPmAddress().getAddressId());
				}

				for (PmAddressType pmAddressType : this.getManagedBean().getSelectedReceivingAddrTypes()) {
					if (deliveringAddr.contains(pmAddressType.getPmAddress().getAddressId())) {
						isUnique = false;
						break;
					}
				}
			}
		}
		return isUnique;
	}

	/**
	 * Add attachment to list
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		logger.info("handleFileUpload - START");
		try {
			if (event != null) {
				if (this.getManagedBean().getSelectedUser().getPmUserAttachments() == null) {
					this.getManagedBean().getSelectedUser().setPmUserAttachments(new ArrayList<PmUserAttachment>());
				}
				if (this.getManagedBean().getFileEventMapper() == null) {
					this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
				}
				String loginId = this.getManagedBean().getSelectedUser().getLoginId();
				UploadedFile item = event.getFile();
				PmUserAttachment attachment = new PmUserAttachment();
				attachment.setFileName(item.getFileName());
				attachment.setFilePath(PmCommonUtil.getUploadPath(loginId, item.getFileName()));
				attachment.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
				attachment.setCreatedUser(getFacesScopes().getUserSessionBean());
				attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));

				this.getManagedBean().getSelectedUser().addPmUserAttachments(attachment);
				this.getManagedBean().getFileEventMapper().put(attachment, event);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060, new String[] { e.getMessage() });

		}
		logger.info("handleFileUpload - END");
	}

	/**
	 * Delete attachment
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachment() throws FileNotFoundException, IOException {
		logger.info("deleteAttachment - START");
		try {
			PmUserAttachment attachment = getManagedBean().getSelectedDeleteAttachment();
			if (attachment != null) {
				if (this.getManagedBean().getDeletePmUserAttachments() == null) {
					this.getManagedBean().setDeletePmUserAttachments(new ArrayList<PmUserAttachment>());
				}
				if (!attachment.isBrandNew()) {
					this.getManagedBean().getDeletePmUserAttachments().add(attachment);
				}
				this.getManagedBean().getSelectedUser().getPmUserAttachments().remove(attachment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054, new String[] { e.getMessage() });

		}
		logger.info("deleteAttachment - END");
	}

	/**
	 * Download attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void downloadAttachment(PmUserAttachment attachment) throws FileNotFoundException, IOException {
		logger.info("downloadAttachment - START");
		try {
			if (attachment != null) {
				commonDownloadUtil(attachment.getFilePath(), attachment.getFileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770055, new String[] { e.getMessage() });

		}
		logger.info("downloadAttachment - END");
	}

	public String getUsername(Long userId) {
		return PmCommonUtil.retriveUserName(userId);
	}

	/**
	 * TAB Authorization - END
	 */

	/** Roles & Paging - Start */
	/**
	 * show user role options
	 */
	private ISearchBean<PmRoleDesc> getRoleBeanData(ISearchBean<PmRoleDesc> searchBean, Long currentOrgTypeId) {
		logger.info("getRoleBeanData");

		logger.info("currentOrgTypeId " + currentOrgTypeId);
		List<String> selectedRolesToExclude = new ArrayList<String>();
		if (currentOrgTypeId != null) {
			SearchFilterBean statusBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			statusBean.setSearchField("pmRole.recordStatusFlg");
			statusBean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(statusBean);

			// remove selected roles (if any)
			if (getManagedBean().getSelectedUserOrg() != null
					&& getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {

				for (PmUserRole obj : getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
					if (obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							|| obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						selectedRolesToExclude.add(obj.getPmRole().getRoleCode());
					}
				}
			}
		} else {
			SearchFilterBean statusBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			statusBean.setSearchField("pmRole.recordStatusFlg");
			statusBean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(statusBean);
		}

		if (!selectedRolesToExclude.isEmpty()) {
			logger.info("remove selected roles");
			SearchFilterBean selectedRole = new SearchFilterBean(SearchOperator.OPERATOR_NOT_IN.value());
			selectedRole.setSearchField("pmRole.roleCode");
			selectedRole.setSearchValue(selectedRolesToExclude);
			searchBean.getDataFilterBeans().add(selectedRole);
		}

		return searchBean;
	}

	/**
	 * search user roles
	 */
	public void searchAvailbleUserRole() {
		logger.info("searchAvailbleUserRole");
		if (getManagedBean().getSelectedRoleSearchOption() == null
				&& StringUtils.isNotBlank(getManagedBean().getSearchRoleName())) {
			throw new EpException(MessageConst.ERROR_100060);
		}
		roleDataModel.reloadData();
	}

	/**
	 * reset user roles
	 */
	public void resetAvailbleUserRole() {
		logger.info("resetAvailbleUserRole");
		getManagedBean().setSearchRoleName(null);
		getManagedBean().setSelectedRoleSearchOption(null);
		roleDataModel.resetData();
	}

	/**
	 * @return the roleDataModel
	 */
	public IPagingDataModel<PmRoleDesc> getRoleDataModel() {
		return roleDataModel;
	}

	/**
	 * @param roleDataModel
	 *            the roleDataModel to set
	 */
	public void setRoleDataModel(IPagingDataModel<PmRoleDesc> roleDataModel) {
		this.roleDataModel = roleDataModel;
	}

	// pagination for role search list
	private class RolePagingDataModel extends PagingDataModel<PmRoleDesc> {
		private static final long serialVersionUID = 1L;

		public RolePagingDataModel() {
			super("dtRoleOptions");
		}

		@Override
		public ISearchResultBean<PmRoleDesc> pageChangeListener(ISearchBean<PmRoleDesc> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.info("pageChangeListener");
			}

			IRoleSO roleSO = null;

			try {
				Long currentOrgTypeId = getCurrentOrgTypeId();
				ISearchBean<PmRoleDesc> searchBeanData = getRoleBeanData(searchBean, currentOrgTypeId);
				RoleReqDTO roleReqDTO = new RoleReqDTO();
				roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
				roleReqDTO.setRoleCode(getRoleCode());
				roleReqDTO.setOrgTypeId(currentOrgTypeId);
				roleReqDTO.setBpk(getManagedBean().getSelectedUserOrg().getPmOrgProfile().getBpk() != null
						? getManagedBean().getSelectedUserOrg().getPmOrgProfile().getBpk() : false);
				roleReqDTO.setAssignRole(true);
				logger.info("..getFacesScopes().getUserSessionBean().getUserId()=" + getFacesScopes().getUserSessionBean().getUserId());

				roleReqDTO.setSearchRoleBean(searchBeanData);
				roleReqDTO.setRoleName(getManagedBean().getSearchRoleName());
				roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRoleSO.class);
				RoleResDTO resDto = roleSO.retrieveRoleDecsFromRoleMatrix(roleReqDTO);

				if (resDto != null && resDto.isSuccessful()) {
					return resDto.getSearchRoleResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				roleSO = null;
			}
			return null;
		}
	}

	private void markupRoleResult(ISearchResultBean<PmRoleDesc> searchRoleResultBean) {
		logger.info("markupRoleResult");
		// disabled selected roles
		if (searchRoleResultBean != null && searchRoleResultBean.getResultList() != null
				&& !searchRoleResultBean.getResultList().isEmpty()) {
			if (getManagedBean().getSelectedUserOrg() != null
					&& getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {
				List<PmRole> selectedRoles = new ArrayList<PmRole>();
				// selected roles
				for (PmUserRole obj : getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
					if (obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						selectedRoles.add(obj.getPmRole());
					}
				}
				for (PmRoleDesc obj : searchRoleResultBean.getResultList()) {
					if (selectedRoles.contains(obj.getPmRole())) {
						obj.setSelected(true);
					}
				}
			}
		}
	}
	/** Roles & Paging - End */

	/** User Search & Paging - Start */
	/**
	 * search user profiles
	 */
	public void searchUserProfile() {
		logger.info("searchUserProfile - start");
		
		if (getManagedBean().getSelectedOrgType().getParameterId() == null) {
			throw new EpException(MessageConst.ERROR_770227);
		}
	
		if (getManagedBean().getUserProfileSearchBean().getSelectedOrgProfileId() == null
				&& isDefaultPtjAdminOrgNameEmpty()
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getIdentificationNo())
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getUserName())
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getUserRole())
				&& getManagedBean().getUserProfileSearchBean().getUserOrgStatus() == null) {
			throw new EpException(MessageConst.ERROR_770049);
		}
	
		logger.info("searchUserProfile - end");
		getManagedBean().setLazy(true);
		userOrgDataModel.reloadData();
	}
	
	/**
	 * check if logged in as PTJ administrator, organization name is empty (displayed in the non-mandatory fields)
	 * 
	 * @return boolean
	 */
	private boolean isDefaultPtjAdminOrgNameEmpty() {
		String ptjOrgName = null;
		if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			if (getManagedBean().getSelectedOrgProfile().getPmOrgValidity() != null) {
				ptjOrgName = getManagedBean().getSelectedOrgProfile().getPmOrgValidity().getOrgName();
			} 
		}
	    return StringUtils.isBlank(ptjOrgName);
	}

	/**
	 * get user maintenance search bean
	 * 
	 * @return ISearchBean<Object[]>
	 */
	private ISearchBean<Object[]> getSearchBeanData(ISearchBean<Object[]> searchBean) {
		logger.info("getUserMaintenanceSearchBeanData - Start");
		UserProfilesSearchBean upSearchBean = this.getManagedBean().getUserProfileSearchBean();

		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.recordStatusFlg",
				SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));

		if (upSearchBean.getSelectedOrgProfileId() != null) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgProfileId",
					SearchOperator.OPERATOR_EQUALS.value(), upSearchBean.getSelectedOrgProfileId()));
		}
		
		if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(),
							this.getManagedBean().getSelectedOrgType().getParameterId()));
		} else {
			if (getManagedBean().getSearchOrgCategory() != null) {
				SelectItem[] items = getOrgTypeDropdownValues();
				List<String> orgTypeIds = new ArrayList<String>();
				for (SelectItem item : items) {
					if (item.getValue() != null) {
						orgTypeIds.add(item.getValue().toString());
					}
				}
				searchBean.getDataFilterBeans().add(new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterId",
						SearchOperator.OPERATOR_IN.value(), orgTypeIds));
			}
		}

		// IdentificationNO
		if (upSearchBean.getIdentificationNo() != null && !"".equalsIgnoreCase(upSearchBean.getIdentificationNo())) {
			logger.info("+ identificationNo " + upSearchBean.getIdentificationNo());
			SearchFilterBean userSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setSearchField("pmUser.identificationNo");
			userSearchBean.setSearchValue(upSearchBean.getIdentificationNo());
			searchBean.getDataFilterBeans().add(userSearchBean);
		}
		// User Name
		if (upSearchBean.getUserName() != null && !"".equalsIgnoreCase(upSearchBean.getUserName())) {
			logger.info("+ userName " + upSearchBean.getUserName());
			SearchFilterBean userSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_STARTS_WITH.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("pmUser.userName");
			userSearchBean.setSearchValue(upSearchBean.getUserName());
			searchBean.getDataFilterBeans().add(userSearchBean);

		}

		// Role
		if (upSearchBean.getUserRole() != null && !"".equalsIgnoreCase(upSearchBean.getUserRole())) {
			logger.info("+ userRole " + upSearchBean.getUserRole());
			SearchFilterBean userSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setSearchField("pmUserRoles.pmRole.roleCode");
			userSearchBean.setSearchValue(upSearchBean.getUserRole());
			searchBean.getDataFilterBeans().add(userSearchBean);

			// Role Status
			if (upSearchBean.getUserRoleStatus() != null) {
				logger.info("+ userRole Status " + upSearchBean.getUserRoleStatus());
				userSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
				userSearchBean.setSearchField("pmUserRoles.recordStatusFlg");
				userSearchBean.setSearchValue(upSearchBean.getUserRoleStatus());
				searchBean.getDataFilterBeans().add(userSearchBean);
			}

		}

		// EP_ADMIN is not allowed to be listed/search in user
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
				SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.EP_ADMIN.value()));

		// EP_ADMIN_2 is not allowed to view/edit EP_ADMIN_1 or EP_ADMIN_2
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value())
				|| getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_IN.value(),
					new ArrayList<String>(Arrays.asList(RoleCode.EP_ADMIN_1.value(), RoleCode.EP_ADMIN_2.value()))));
		}

		// MINISTRY_ADMIN is not allowed to view/edit MINISTRY_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.MINISTRY_ADMIN.value()));
		}

		// JABATAN_ADMIN is not allowed to view/edit JABATAN_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.JABATAN_ADMIN.value()));
		}

		// PTJ_ADMIN is not allowed to view/edit PTJ_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.PTJ_ADMIN.value()));
		}

		// BPK_ADMIN is not allowed to view/edit BPK_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.BPK_ADMIN.value()));
			
			//[#22117] - start
			//get list of user_org_id which has role BPK_ADMIN
			List<Long> userOrgIds = getListOfOrgIdByOrgIdAndRole(this.getManagedBean().getSelectedOrgType().getParameterId(), 
																	upSearchBean.getSelectedOrgProfileId(), 
																	RoleCode.BPK_ADMIN.value());
			if(userOrgIds != null && userOrgIds.size() > 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("userOrgId",
					SearchOperator.OPERATOR_NOT_IN.value(), userOrgIds));
			}
			//[#22117] - end
		}

		// UPS_ADMIN is not allowed to view/edit UPS_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.UPS_ADMIN.value()));
		}

		// SUPPORT_ADMIN is not allowed to view/edit SUPPORT_ADMIN
		if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("pmUserRoles.pmRole.roleCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.SUPPORT_ADMIN.value()));
		}

		// Status if non provided then select default (active)
		if (upSearchBean.getUserOrgStatus() != null) {
			logger.info("+ status " + upSearchBean.getUserOrgStatus());
			SearchFilterBean userSearchBean = new SearchFilterBean();
			userSearchBean.setSearchField("recordStatusFlg");
			userSearchBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setSearchValue(upSearchBean.getUserOrgStatus());
			searchBean.getDataFilterBeans().add(userSearchBean);

		} else {
			logger.info("+ status 1 and 0");
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg", SearchOperator.OPERATOR_IN.value(), new ArrayList<Integer>(
							Arrays.asList(DbValueConst.RECORD_STATUS_ACTIVE, DbValueConst.RECORD_STATUS_SUSPENDED))));
		}

		// Authorisation
		if (StringUtils.isNotBlank(getRoleCode())) {
			if (upSearchBean.getSelectedOrgProfileId() == null && getManagedBean().getSelectedOrgType() != null
					&& StringUtils.isNotBlank(getManagedBean().getSelectedOrgType().getParameterCode())) {
				if (this.getManagedBean().getCurrentActiveOrg() != null) {
					OrgProfileUtil.retrieveOrgProfileAuthorisationSearchBean(
							getManagedBean().getSelectedOrgType().getParameterCode(),
							getManagedBean().getCurrentActiveOrg(), getRoleCode(), searchBean);
				}

			} else if (upSearchBean.getSelectedOrgProfileId() == null && (getManagedBean().getSelectedOrgType() == null
					|| StringUtils.isBlank(getManagedBean().getSelectedOrgType().getParameterCode()))) {
				if (this.getManagedBean().getCurrentActiveOrg() != null) {
					OrgProfileUtil.retrieveOrgProfileAuthorisationSearchBean(
							getManagedBean().getSelectedOrgType().getParameterCode(),
							getManagedBean().getCurrentActiveOrg(), getRoleCode(), searchBean);
				}
			}
		} else {
			if ((getManagedBean().getSelectedOrgType() == null
					|| StringUtils.isBlank(getManagedBean().getSelectedOrgType().getParameterCode()))
					&& this.getManagedBean().getCurrentActiveOrg() != null) {
				searchBean.getDataFilterBeans()
						.add(new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getCurrentActiveOrg().getOrgTypeCode().getParameterId()));
			}
		}

		searchBean.getSortColumns().add(new SearchOrderBean("createdDate", false));
		searchBean.getSortColumns().add(new SearchOrderBean("pmUser.userName", true));
		logger.info("getUserMaintenanceSearchBeanData - End");
		return searchBean;
	}


	public IPagingDataModel<Object[]> getUserOrgDataModel() {
		return userOrgDataModel;
	}

	public void setUserOrgDataModel(IPagingDataModel<Object[]> userOrgDataModel) {
		this.userOrgDataModel = userOrgDataModel;
	}

	// pagination for user search list
	private class UserProfilePagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public UserProfilePagingDataModel() {
			super("dtUserResult", getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			
			logger.info("pageChangeListener - start");
			IUserSO userSO = null;
			try {
				ISearchBean<Object[]> searchBeanData = getSearchBeanData(searchBean);
				UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
				userOrgReqDTO.setUserOrgMaintenanceSearchBean(searchBeanData);
				
				String paramCode = getManagedBean().getSelectedOrgType().getParameterCode();
				userOrgReqDTO.setParameterCode(paramCode);
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
				UserOrgResDTO resDto = userSO.searchUserAndUserOrgProfileMaintenance(userOrgReqDTO);
				if (resDto != null && resDto.isSuccessful()) {
					return resDto.getUserOrgMaintenanceResultBean();
				}
			} finally {
				userSO = null;
			}
			
			logger.info("pageChangeListener - end");
			return null;
		}
	}
	
	private void markupUserResult(ISearchResultBean<PmUserOrg> userResultBean) {
		logger.info("markupUserResult");
		if (userResultBean != null && userResultBean.getResultList() != null
				&& !userResultBean.getResultList().isEmpty()) {

			for (PmUserOrg pmUserOrg : userResultBean.getResultList()) {
				List<PmAddressType> addressTypes = PmCommonUtil.retrieveAddressTypesByUserIdOrAddressType(
						pmUserOrg.getPmUser().getUserId(), ProfileManagementConst.ADDRESS_USER);
				pmUserOrg.getPmUser().setAddressTypeC(
						(addressTypes != null && !addressTypes.isEmpty()) ? addressTypes.get(0) : new PmAddressType());
			}
		}
	}

	/** User Search & Paging - End */

	/**
	 * load user address - C
	 * 
	 * @param user
	 */
	private void loadUserAddress(PmUser user, boolean isAddressEntered) {
		logger.info("loadUserAddress");
		if (!user.isBrandNew()) {
			boolean isUserAddressExist = false;
			// update contact address
			if (user.getPmAddressTypes() != null && !user.getPmAddressTypes().isEmpty()) {
				for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj.getRecordStatusFlg()) {
						if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressTypeObj.getAddressType())) {
							// update address
							if (isAddressEntered) {
								logger.info("update address");
								addressTypeObj.setPmAddress(this.getManagedBean().getUserAddress());
								if (addressTypeObj.getPmAddress().getPmDivision() != null
										&& addressTypeObj.getPmAddress().getPmDivision().getDivisionId() == null) {
									addressTypeObj.getPmAddress().setPmDivision(null);
								}
							} else {
								logger.info("update address (remove)");
								// remove address
								addressTypeObj.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}
							isUserAddressExist = true;
							break;
						}
					}
				}
				if (!isUserAddressExist) {
					logger.info("add address");
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
			}
		} else {
			if (isAddressEntered) {
				logger.info("add address");
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
	 * load user address - C
	 * 
	 * @param user
	 */
	private void cloneUserAddress(PmUser user) {
		logger.info("cloneUserAddress");
		// getting current active org's address
		PmAddress orgAddress = null;

		if (user.getPmUserOrgs() != null) {
			Long orgProfileId = null;
			for (PmUserOrg userOrg : user.getPmUserOrgs()) {
				if (userOrg.isCurrentActive()) {
					orgProfileId = userOrg.getPmOrgProfile().getOrgProfileId();
					break;
				}
			}
			// search address type B and orgProfileId
			List<PmAddressType> addressTypes = PmCommonUtil.retrieveAddressTypesByOrgId(orgProfileId,
					ProfileManagementConst.ADDRESS_BILLING);
			orgAddress = (addressTypes != null && !addressTypes.isEmpty()) ? addressTypes.get(0).getPmAddress() : null;
			logger.info(orgAddress != null ? "orgAddress exist: YES" : "orgAddress exist: NO");
		}

		if (!user.isBrandNew()) {
			boolean isUserAddressExist = false;
			// update contact address
			if (user.getPmAddressTypes() != null && !user.getPmAddressTypes().isEmpty()) {
				for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj.getRecordStatusFlg()) {
						if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressTypeObj.getAddressType())) {
							// update address
							if (orgAddress != null) {
								logger.info("update address");
								// copy orgAddress and update address
								addressTypeObj.setPmAddress(orgAddress);
								if (addressTypeObj.getPmAddress().getPmDivision() != null
										&& addressTypeObj.getPmAddress().getPmDivision().getDivisionId() == null) {
									addressTypeObj.getPmAddress().setPmDivision(null);
								}
							} else {
								logger.info("update address (remove)");
								// remove address because org doesn't have an address
								addressTypeObj.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}
							isUserAddressExist = true;
							break;
						}
					}
				}
				if (!isUserAddressExist) {
					logger.info("add address");
					// copy orgAddress and add address
					if (orgAddress != null) {
						PmAddressType pmAddressType = new PmAddressType();
						pmAddressType.setAddressType(ProfileManagementConst.ADDRESS_USER);
						pmAddressType.setPmAddress(orgAddress);
						if (pmAddressType.getPmAddress().getPmDivision() != null
								&& pmAddressType.getPmAddress().getPmDivision().getDivisionId() == null) {
							pmAddressType.getPmAddress().setPmDivision(null);
						}
						pmAddressType.setPmOrgProfile(null);
						user.addPmAddressType(pmAddressType);
					}
				}
			}
		} else {
			// copy orgAddress and update address
			if (orgAddress != null) {
				logger.info("add address");
				if (user.getPmAddressTypes() == null) {
					user.setPmAddressTypes(new ArrayList<PmAddressType>());
				}
				PmAddressType pmAddressType = new PmAddressType();
				pmAddressType.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(orgAddress);
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
	 * load user org type with the last PmUserOrgs
	 * 
	 * @param user
	 */
	private void loadUserOrgType(PmUser user, PmUserOrg userOrg) {
		logger.info("loadUserOrgType");
		if (userOrg != null && userOrg.getPmOrgProfile().getOrgTypeCode() != null) {
			user.setOrgTypeCode(userOrg.getPmOrgProfile().getOrgTypeCode());
		} else {
			throw new EpException(MessageConst.ERROR_770026);
		}
	}

	/**
	 * Generate new role and deleted role to update Liferay and OIM
	 * 
	 * @param user
	 * @param reqDTO
	 */
	private void loadUserRoles(PmUser user, UserReqDTO reqDTO) {
		logger.info("loadUserRoles");
		if (user.getPmUserOrgs() != null && !user.getPmUserOrgs().isEmpty()) {
			List<PmUserRole> newRoleList = reqDTO.getNewRoleList();
			List<PmUserRole> deleteRoleList = reqDTO.getRemoveRoleList();
			long activeUserOrgId = 0;
			boolean isChanged = false;
			logger.info("......user.getPmUserOrgs()=" + user.getPmUserOrgs());
			logger.info("......user.getPmUserOrgs().size()=" + user.getPmUserOrgs().size());
			for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
				if (pmUserOrg.isBrandNew()) {
					isChanged = true;
				}
				if (pmUserOrg.isBrandNew() || pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					activeUserOrgId = pmUserOrg.getUserOrgId() != null ? pmUserOrg.getUserOrgId() : 0;
					for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
						if (pmUserRole.isBrandNew() || (isUserRoleModified(pmUserRole)
								&& pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)) {
							newRoleList.add(pmUserRole);
							logger.info("ADD NEW: " + pmUserRole.getPmRole().getRoleCode());
						} else if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED
								&& isUserRoleModified(pmUserRole)) {
							deleteRoleList.add(pmUserRole);
							logger.info("DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
						}
					}
					// }
				} else if (pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					// TODO : remove role.brandNew first!
					logger.info("pmUserOrg : RECORD_STATUS_SUSPENDED -> deleteRoleList :");
					for(PmUserRole role : pmUserOrg.getPmUserRoles()){		
						role.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						if (isUserRoleModified(role)) {
							deleteRoleList.add(role);
							logger.info("pmUserOrg : RECORD_STATUS_SUSPENDED DELETE OLD: " + role.getPmRole().getRoleCode());
						}
					}
				}
			}
			if (activeUserOrgId > 0 || isChanged) {
				logger.info("size " + user.getPmUserOrgs().size());
				for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
					if (pmUserOrg.getUserOrgId() != null && pmUserOrg.getUserOrgId() != activeUserOrgId) {
						for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
							if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								deleteRoleList.add(pmUserRole);
								logger.info("2 DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
							}
						}
					}
				}
				reqDTO.setNewRoleList(newRoleList);
				reqDTO.setRemoveRoleList(deleteRoleList);
				
				if(newRoleList != null){
					for(PmUserRole newRole : newRoleList){
						logger.info("newRoleList : " + newRole.getPmRole().getRoleCode() + "|RoleStatus :" + newRole.getPmRole().getRecordStatusFlg());
					}
				}
				
				if(deleteRoleList != null){
					for(PmUserRole deleteRole : deleteRoleList){
						logger.info("deleteRoleList : " + deleteRole.getPmRole().getRoleCode() + "|RoleStatus :" + deleteRole.getPmRole().getRecordStatusFlg());
					}
				}
			}
		}
	}

	/**
	 * update only active
	 * 
	 * @param pmUserOrg
	 * @return
	 */
	private boolean checkEffDate(PmUserOrg pmUserOrg) {
		logger.info("checkEffDate");
		Date currentDate = new Date();
		boolean isActive = true;
		// effDate after currentDate
		if (pmUserOrg.getEffDateDisplay().compareTo(currentDate) > 0) {
			isActive = false;
			// expDate not null and expDate before currentDate
		} else if (pmUserOrg.getExpDateDisplay() != null && pmUserOrg.getExpDateDisplay().compareTo(currentDate) < 0) {
			isActive = false;
		}
		logger.info("checkEffDate active:" + isActive);

		return isActive;
	}

	/**
	 * Validation for Responsible Org. Unit date range
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void validateResponsibleOrg(PmUser user) throws Exception {
		logger.info("validateResponsibleOrg");

		if (user.getPmUserOrgs() != null && !user.getPmUserOrgs().isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date endLess = sdf.parse("2099-12-31");
			for (int index1 = 0; index1 < user.getPmUserOrgs().size(); index1++) {
				if (user.getPmUserOrgs().get(index1).getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					if (user.getPmUserOrgs().size() > 1) {
						for (int index2 = index1 + 1; index2 < user.getPmUserOrgs().size(); index2++) {
							if (user.getPmUserOrgs().get(index2)
									.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								// Overlap exists if (StartA <= EndB) and (EndA >= StartB)
								Date startA = user.getPmUserOrgs().get(index1).getEffDate();
								Date endA = user.getPmUserOrgs().get(index1).getExpDate();
								Date startB = user.getPmUserOrgs().get(index2).getEffDate();
								Date endB = user.getPmUserOrgs().get(index2).getExpDate();

								// not null
								if (endA == null) {
									endA = endLess;
								}
								if (endB == null) {
									endB = endLess;
								}

								boolean isCurrentPairOverlap = false;
								isCurrentPairOverlap = PmCommonUtil.dateOverlapping(startA, endA, startB, endB, false);

								if (isCurrentPairOverlap) {
									throw new EpException(MessageConst.ERROR_770027);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Check if only one active org unit.
	 * 
	 * @param user
	 */
	private void oneActiveResponsibleOrg(PmUser user) {
		int activeOrgNumber = 0;
		int activeRoleNumber = 0;
		if (user.getPmUserOrgs() != null) {
			for (PmUserOrg userOrg : user.getPmUserOrgs()) {
				if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					if (checkEffDate(userOrg)) {
						userOrg.setCurrentActive(true);
						activeOrgNumber++;
						if (userOrg.getPmUserRoles() == null || userOrg.getPmUserRoles().isEmpty()) {
						}
						for (PmUserRole userRole : userOrg.getPmUserRoles()) {
							if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()) {
								activeRoleNumber++;
							}
						}
						if (activeRoleNumber == 0) {
							throw new EpException(MessageConst.ERROR_770150);
						}
					} else {
						userOrg.setCurrentActive(false);
					}
				}
			}
		}
		if (activeOrgNumber > 1) {
			throw new EpException(MessageConst.ERROR_770028);
		} else if (user.isBrandNew() && activeOrgNumber == 0) {
			throw new EpException(MessageConst.ERROR_770029);
		}
	}

	/**
	 * validate unique role for each org type
	 * 
	 * @param user
	 */
	private void validateRoles(PmUserOrg userOrg) {
		boolean activeRole = false;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		List<PmUserRole> userRoleList = null;
		if (userOrg != null) {
			if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				userRoleList = userOrg.getPmUserRoles();
				for (PmUserRole userRole : userOrg.getPmUserRoles()) {
					String roleCode = userRole.getPmRole().getRoleCode();
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							&& userRole.getPmRole().isLimitRequired()) {
						if (userRole.getApprovalLimit() == null
								|| userRole.getApprovalLimit().compareTo(BigDecimal.ZERO) != 1) {
							throw new EpException(MessageConst.ERROR_770148,
									new String[] { getRoleDescByRoleCode(roleCode, languageCode) });
						}
					}
					
					// Bug #31509 - Start
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED
							&& (RoleCode.FL_APPROVER.value().equals(userRole.getPmRole().getRoleCode()) || RoleCode.RN_APPROVER
									.value().equals(userRole.getPmRole().getRoleCode()))
							&& isActiveAtUserGroupUser(userRole.getPmRole().getRoleCode()) != null) {
						// check for FL Approver and RN Approver only revert it to active
						userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						throw new EpException(MessageConst.ERROR_770202, isActiveAtUserGroupUser(userRole.getPmRole()
								.getRoleCode()));
					}
					// Bug #31509 - End

					// Check single role for all users
					if (userRole.getPmRole().isSingleRole()
							&& userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
								&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
							for (PmUserRole current : userRoleList) {
								if (!current.getPmRole().getRoleCode().equals(userRole.getPmRole().getRoleCode())
										&& current.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
									throw new EpException(MessageConst.ERROR_770134);
								}
							}
						}
					}
					// check if one role is active for user org active status only
					if (DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()) {
						activeRole = true;
					}
				}
			} else {
				activeRole = true;
			}
		}

		if (!activeRole) {
			throw new EpException(MessageConst.ERROR_770022);
		}
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
		}
	}

	private void fillUserInfoFromUserOrg(PmUser user, PmUserOrg userOrg) {
		user.setDesignation(userOrg.getDesignation());
		user.setPMP(userOrg.isPMP());
		user.setGrade(userOrg.getGrade());
		user.setEmail(userOrg.getEmail());
		user.setPhoneNo(userOrg.getPhoneNo());
		user.setPhoneArea(userOrg.getPhoneArea());
		user.setPhoneCountry(userOrg.getPhoneCountry());
		user.setFaxNo(userOrg.getFaxNo());
		user.setFaxArea(userOrg.getFaxArea());
		user.setFaxCountry(userOrg.getFaxCountry());
		user.setMobileNo(userOrg.getMobileNo());
		user.setMobileArea(userOrg.getMobileArea());
		user.setMobileCountry(userOrg.getMobileCountry());
		user.setMobileTelcoId(userOrg.getMobileTelcoId());
	}

	private void checkChangesAndFillTrackingDiary(PmUser user, PmUserOrg userOrg, UserReqDTO reqDTO) {

		boolean isProfileDetailsChanged = false;

		String oldDesignation = user.getDesignation();
		String newDesignation = userOrg.getDesignation();
		if (oldDesignation != null && !oldDesignation.equalsIgnoreCase(newDesignation)) {// if designation was modified
			logger.info("oldDesignation : " + oldDesignation);
			logger.info("newDesignation : " + newDesignation);
			isProfileDetailsChanged = true;

		}
		String oldEmail = user.getEmail();
		String newEmail = userOrg.getEmail();
		if (oldEmail != null && !oldEmail.equalsIgnoreCase(newEmail)) {
			logger.info("oldEmail : " + oldEmail);
			logger.info("newEmail : " + newEmail);
			isProfileDetailsChanged = true;
		}
		String oldGrade = user.getGrade();
		String newGrade = userOrg.getGrade();
		if (oldGrade != null && !oldGrade.equalsIgnoreCase(newGrade)) {
			logger.info("oldGrade : " + oldGrade);
			logger.info("newGrade : " + newGrade);
			isProfileDetailsChanged = true;
		}
		boolean oldPMP = user.isPMP();
		boolean newPMP = userOrg.isPMP();
		if (oldPMP != newPMP) {
			logger.info("oldPMP : " + oldPMP);
			logger.info("newPMP : " + newPMP);
			isProfileDetailsChanged = true;
		}
		String oldUsername = this.getManagedBean().getPmUserOriginal().getUserName();

		String newUsername = user.getUserName();
		if (!oldUsername.equalsIgnoreCase(newUsername)) {
			logger.info("oldUsername=" + oldUsername);
			logger.info("newUsername=" + newUsername);
			isProfileDetailsChanged = true;
		}
		if (isProfileDetailsChanged) {
			logger.info("Tracking diary for user profile updated :saveUserProfile");
			String messageId = MessageConst.TD_80014;
			String[] msgParam = new String[] { user.getUserName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
					getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
		}
		if (!this.getManagedBean().getPmUserOrgOriginal().getRecordStatusFlg().equals(userOrg.getRecordStatusFlg())) {
			logger.info("Tracking diary for user profile: change user state :saveUserProfile");
			String messageId = userOrg.getRecordStatusFlg() == 0 ? MessageConst.TD_80004 : MessageConst.TD_80005;
			String[] msgParam = new String[] { user.getUserName(), userOrg.getPmOrgProfile().getOrgCode(),
					userOrg.getPmOrgProfile().getOrgName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
					getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
		}

		if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {// ????

			for (PmUserRole userRole : reqDTO.getNewRoleList()) {
				
				if (userRole.isBrandNew()) {

					logger.info("Tracking diary for user profile: add new Role :saveUserProfile");
					String messageId = MessageConst.TD_80001;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					logger.info("......msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));

				} else if (isUserRoleModified(userRole)) {// the role's activated
					logger.info("Tracking diary for user profile: Role is re-activated :saveUserProfile");
					String messageId = MessageConst.TD_80003;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					logger.info("..222....msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
				}
			}
			for (PmUserRole userRole : reqDTO.getRemoveRoleList()) {
				if (!userRole.isBrandNew()) {// the role's de-activated
					logger.info("Tracking diary for user profile: Role is de-activated :saveUserProfile");

					String messageId = MessageConst.TD_80002;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					logger.info("..333....msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
				}
			}

			for (PmUserRole userRole : userOrg.getPmUserRoles()) {
				if (userRole.getPmRole().getRoleCode().equals("FL_APPROVER")) {
					// getFromMap
					String oldLetterNo = this.getManagedBean().getRoleCodeToLetterNoMap()
							.get(userRole.getPmRole().getRoleCode());
					String newLetterNo = userRole.getLetterNo();
					if (oldLetterNo != null && !oldLetterNo.isEmpty() && !oldLetterNo.equalsIgnoreCase(newLetterNo)) {
						logger.info("Tracking diary for user profile: letterNo is changed :saveUserProfile");
						String messageId = MessageConst.TD_80007;
						String[] msgParam = new String[] { user.getUserName(), oldLetterNo, newLetterNo };
						reqDTO.setTrackingStatus(true);
						reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));

					}
				}
				if (userRole.getPmRole().isLimitRequired()) {
					// getFromMap
					BigDecimal oldApprovalLimit = this.getManagedBean().getRoleCodeToApprovalLimitMap()
							.get(userRole.getPmRole().getRoleCode());
					BigDecimal newApprovalLimit = userRole.getApprovalLimit();
					if (oldApprovalLimit != null && (oldApprovalLimit.compareTo(newApprovalLimit) != 0)) {
						logger.info("Tracking diary for user profile: approvalLimit is changed :saveUserProfile");
						String messageId = MessageConst.TD_80006;
						String[] msgParam = new String[] { user.getUserName(), oldApprovalLimit.toString(),
								newApprovalLimit.toString() };
						reqDTO.setTrackingStatus(true);
						reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
					}
				}
			}
		}
	}

	/**
	 * reset user password and re-send notification to user.
	 * 
	 * @throws Exception
	 */
	public void resendNotification() throws Exception {
		UserReqDTO reqDTO = null;
		IUserSO userSO = null;
		PmUser pmUser = null;
		Long userId = null;
		try {
			userId = this.getManagedBean().getSelectedUser().getUserId();
			if (userId == null) {
				throw new EpException("User Id is required.");
			}
			pmUser = PmCommonUtil.retrieveUserById(userId, true);

			reqDTO = new UserReqDTO();
			reqDTO.setServerName(getFacesScopes().getRequest().getServerName());
			reqDTO.setServerPort(getFacesScopes().getRequest().getServerPort());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			reqDTO.setPmUser(pmUser);
			logger.info("Tracking diary for user profile: reset password :saveUserProfile");
			String messageId = MessageConst.TD_80015;
			String[] msgParam = new String[] { pmUser.getUserName() };
			reqDTO.setTrackingStatus(true);

			reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(pmUser,
					getActionDesc(messageId, msgParam), pmUser.getRecordStatusFlg().longValue()));

			UserResDTO resDTO = userSO.resendNotificationAndPwd(reqDTO);
			if (resDTO != null && resDTO.isSuccessful()) {
				// Reset password and notification sent successfully.
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_770107,
										new String[] { resDTO.getPmUser().getLoginId(), resDTO.getPmUser().getEmail() },
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770225);
		} finally {
			userSO = null;
		}

	}

	/**
	 * Method to update the user details
	 */
	public String saveUserProfile() throws Exception {
		logger.info("saveUserProfile");

		UserReqDTO reqDTO = null;
		IUserSO userSO = null;
		PmUser user = null;
		PmUserOrg userOrg = null;
		boolean isNewUser = this.getManagedBean().getSelectedUser().isBrandNew();
		logger.info("..............saveUserProfile.................isNewUser=" + isNewUser);

		try {
			user = this.getManagedBean().getSelectedUser();
			userOrg = this.getManagedBean().getSelectedUserOrg();
			// Validation
			if (isNewUser) {
				checkIdentificationNumber1();

				// MyIdentity Checking
				//Redmine Bug #32423 & #32032 - START
				if (getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode()
						.equals(ProfileManagementConst.DEFAULT_IDENTIFY_TYPE)) {
					SmCommonUtil.getInstance().verifyWithMyIdentity(user.getUserName(), user.getIdentificationNo(),
							null, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN, false);
				}
				//Redmine Bug #32423 & #32032 - END
			}
			validateUser(user, userOrg);
			validatePreference(user);
			validateRoles(userOrg);

			reqDTO = new UserReqDTO();
			reqDTO.setServerName(getFacesScopes().getRequest().getServerName());
			reqDTO.setServerPort(getFacesScopes().getRequest().getServerPort());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			loadUserOrgType(user, userOrg);
			List<PmUserOrg> userOrgList = new ArrayList<PmUserOrg>();// always will have one org for each user
			
			// Bug #26766 - START
			Long loginSessionUserId = 1L;
			userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(getFacesScopes().getUserScreenName());
			PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
			if (pmUser == null) {
				loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
			}
			else{
				loginSessionUserId = pmUser.getUserId();
			}
			new LogKey(loginSessionUserId);
			// Bug #26766 - END
			
			if (!userOrg.isBrandNew()) {
				userOrg.setChangedBy(loginSessionUserId);
			} else {
				userOrg.setChangedBy(loginSessionUserId);
				userOrg.setCreatedBy(loginSessionUserId);
			}
			userOrgList.add(userOrg);
			user.setPmUserOrgs(userOrgList);
			if (!user.isBrandNew()) {
				reqDTO.setNewRoleList(new ArrayList<PmUserRole>());
				reqDTO.setRemoveRoleList(new ArrayList<PmUserRole>());
				loadUserRoles(user, reqDTO);
				user.setChangedBy(loginSessionUserId);
			} else {
				user.setChangedBy(loginSessionUserId);
				user.setCreatedBy(loginSessionUserId);
			}
			
			// #35769 - Start // fixing null pointer after inactive user
			// #35489 - Start
			if (this.getManagedBean().isOrgUnitStatusChange()
					&& isCheckSelectedReceivingAddrTypes()) {
				for (PmAddressType addressType : this.getManagedBean()
						.getSelectedReceivingAddrTypes()) {
					addressType.setChangedBy(getUserIdSessionFromLoginId());
				}
			}

			if (this.getManagedBean().isUserStatusChange()
					&& isCheckSelectedReceivingAddrTypes()) {
				for (PmAddressType addressType : this.getManagedBean()
						.getSelectedReceivingAddrTypes()) {
					addressType.setChangedBy(getUserIdSessionFromLoginId());
				}
			}
			// #35489 - End
			// #35769 - End // fixing null pointer after inactive user
			
			//Redmine Bug #31541 - START
			if (this.getManagedBean().getIsNewUser()) {
				logger.info("Tracking diary for user profile: create new user :saveUserProfile");
				String orgCode = null;
				String orgName = null;
				if (ParameterConst.ORG_TYPE_FACTORING
						.equals(userOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
					orgCode = userOrg.getPmOrgProfile().getPmFinancialOrg().getBizRegNumber();
					orgName = userOrg.getPmOrgProfile().getPmFinancialOrg().getFinOrgName();
				} else if (ParameterConst.ORG_TYPE_SUPPLIER
						.equals(userOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
					orgCode = userOrg.getPmOrgProfile().getSmSupplier().getRegNo();
					orgName = userOrg.getPmOrgProfile().getSmSupplier().getCompanyName();
				} else {
					orgCode = userOrg.getPmOrgProfile().getOrgCode();
					orgName = userOrg.getPmOrgProfile().getOrgName();
				}

				String messageId = MessageConst.TD_80000;
				String[] msgParam = new String[] { user.getUserName(), orgCode, orgName };
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
						getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
			}
			
			if (!isNewUser) {
				checkChangesAndFillTrackingDiary(user, userOrg, reqDTO);
			}
			//Redmine Bug #31541 - END
			
			//Having other active user org?
			boolean deactivateRequired = true;
			//if inactive user org, check whether the user has another active user org.
			if (!user.isBrandNew() && userOrg.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED) {
				List<PmUserOrg> userOrgs = PmCommonUtil.retrieveAllUserOrg(user.getUserId());
				if (userOrgs != null && !userOrgs.isEmpty()) {
					for (PmUserOrg uo : userOrgs) {
						if (uo.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE
								&& !uo.getUserOrgId().equals(userOrg.getUserOrgId())) {
							deactivateRequired = false;
							break;
						}
					}
				}
			}
			logger.info("deactivateRequired : " + deactivateRequired);
			reqDTO.setDeactivateRequired(deactivateRequired);
			
			if(deactivateRequired){
				fillUserInfoFromUserOrg(user, userOrg);
			}
			
			reqDTO.setPmUser(user);
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());

			if (ParameterConst.ORG_TYPE_FACTORING.equals(user.getOrgTypeCode().getParameterCode())) {
				reqDTO.setFactoringUser(true);
			}

			if (ParameterConst.ORG_TYPE_SUPPLIER.equals(user.getOrgTypeCode().getParameterCode())) {
				reqDTO.setSupplierUser(true);
			}
			
			//#36489 - Start
			if (validateUserOrgProfileAndStatus()
					&& ParameterConst.ORG_TYPE_PTJ.equals(user.getOrgTypeCode().getParameterCode())
					&& this.getManagedBean().isSuppGovSellerUser()) {
				reqDTO.setSuppGovSeller(true);
			}
			//#36489 - End
			
			UserResDTO resDTO = userSO.saveUserAndUserOrg(reqDTO);
			logger.info("save user -END");
			if (resDTO.isSuccessful()) {

				uploadAttachments();
				deleteAttachments();
				
				if (!isNewUser) {
					if (!userOrg.isBrandNew()) {
						loadUserProfile(resDTO.getPmUser().getUserId(), userOrg.getUserOrgId());
					} else {
						loadUserProfile(resDTO.getPmUser().getUserId(), resDTO.getPmUser().getActiveUserOrgs().get(0).getUserOrgId());
					}
				}
				if (!StringUtils.isBlank(resDTO.getRemarks())) {
					// OIM user creation successfully, problem with roles.
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, resDTO.getRemarks(), resDTO.getRemarks()));
				} else {
					if (isNewUser) {
						PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
								MessageConst.INFO_770049, new String[] { resDTO.getPmUser().getUserName(),
										resDTO.getPmUser().getIdentificationNo(), resDTO.getPmUser().getEmail() });
					} else {
						if (getManagedBean().isPmUserExist()) {
							PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
									MessageConst.INFO_770052, new String[] { resDTO.getPmUser().getUserName(),
											resDTO.getPmUser().getIdentificationNo() });
						} else {
							PmCommonUtil.showFacesMsg(FacesContext.getCurrentInstance(), FacesMessage.SEVERITY_INFO,
									MessageConst.INFO_770050, new String[] { resDTO.getPmUser().getLoginId() });
						}
					}
				}
				//
				logger.info("befor redirecting method");
				if (isNewUser) {
					return redirectToMainPage();
				} else {
					return NAV_TO_MAINTAIN;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			userSO = null;
		}
		return null;
	}

	/**
	 * Upload files
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void uploadAttachments() throws FileNotFoundException, IOException {
		logger.info("uploadAttachments - START");
		try {
			if (this.getManagedBean().getFileEventMapper() != null
					&& !this.getManagedBean().getFileEventMapper().isEmpty()) {
				PmUserAttachment tempPmUserAttachment = null;
				FileUploadEvent tempFileUploadEvent = null;
				for (Map.Entry<PmUserAttachment, FileUploadEvent> entry : this.getManagedBean().getFileEventMapper()
						.entrySet()) {
					tempPmUserAttachment = entry.getKey(); // PmUserAttachment
					tempFileUploadEvent = entry.getValue(); // FileUploadEvent
					PmCommonUtil.commonUploadFile(tempFileUploadEvent,
							PmCommonUtil.getAttachmentPath(tempPmUserAttachment.getFilePath()));
				}
				this.getManagedBean().getFileEventMapper().clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060, new String[] { e.getMessage() });

		}
		logger.info("uploadAttachments - END");
	}

	/**
	 * Delete attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachments() throws FileNotFoundException, IOException {
		logger.info("deleteAttachment - START");
		try {
			if (this.getManagedBean().getDeletePmUserAttachments() != null
					&& !this.getManagedBean().getDeletePmUserAttachments().isEmpty()) {
				for (PmUserAttachment pmUserAttachment : this.getManagedBean().getDeletePmUserAttachments()) {
					if (!pmUserAttachment.isBrandNew()) {
						PmCommonUtil.deleteUploadFile(pmUserAttachment.getFilePath());
					}
				}
				PmCommonUtil.deleteUserAttachment(this.getManagedBean().getDeletePmUserAttachments());
				this.getManagedBean().getDeletePmUserAttachments().clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054, new String[] { e.getMessage() });

		}
		logger.info("deleteAttachment - END");
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
		if (!this.getManagedBean().getSelectedUser().isBrandNew()) {
			updatePortrait();
			this.getManagedBean().getSelectedUser().setPortrait(null);
		}

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
				throw new EpException(MessageConst.ERROR_770114);
			}

			reqDTO.setPortrait(this.getManagedBean().getSelectedUser().getPortrait());
			reqDTO.setLoginId(this.getManagedBean().getSelectedUser().getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			resDTO = userSO.updatePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_770117,
										new String[] { this.getManagedBean().getSelectedUser().getLoginId() },
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				PmCommonUtil.clearLiferayCache();

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770115, new String[] { e.getMessage() });
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
		if (this.getManagedBean().getSelectedUser().isBrandNew()) {
			this.getManagedBean().getSelectedUser().setPortrait(null);
			this.getManagedBean().getSelectedUser().setReplacePortrait(true);
			this.getManagedBean().setDeletePortrait(true);
		} else {
			IUserSO userSO = null;
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			try {

				reqDTO.setLoginId(this.getManagedBean().getSelectedUser().getLoginId());

				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);

				resDTO = userSO.deletePortrait(reqDTO);

				if (resDTO != null && resDTO.isSuccessful()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(MessageConst.INFO_770118,
											new String[] { this.getManagedBean().getSelectedUser().getLoginId() },
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					PmCommonUtil.clearLiferayCache();

				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770116, new String[] { e.getMessage() });
			} finally {
				userSO = null;

			}
		}

		logger.info("deletePortrait - END");
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

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getPassword())) {
				throw new EpException(MessageConst.ERROR_770126);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser().getConfirmPassword())) {
				throw new EpException(MessageConst.ERROR_770127);
			}

			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			resDTO = userSO.changePassword(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_770124,
										new String[] { this.getManagedBean().getSelectedUser().getLoginId() },
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				clearPassword();

			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770128, new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("changePassword - END");
	}

	public void checkIdentificationNumber1() {
		logger.info(".............checkIdentificationNumber1...........");
		this.getManagedBean().getSelectedUser().setHasActiveOrg(false);
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		if (getManagedBean().getSelectedUser().getIdentificationNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getSelectedUser().getIdentificationNo())
				&& getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode() != null) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean().getSelectedUser().getIdentificationNo());

			searchBean.getDataFilterBeans().add(idNoSearchBean);

			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("identificationCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode()));

			searchBean.getDataFilterBeans().add(new SearchFilterBean("recordStatusFlg",
					SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));

			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserSearchBean(searchBean);
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
				UserResDTO resDTO = userSO.searchUserProfile(userReqDTO);

				if (resDTO != null && resDTO.getUserResultBean().getResultList() != null
						&& resDTO.getUserResultBean().getResultList().size() > 0) {
					PmUser user = resDTO.getUserResultBean().getResultList().get(0);
					// TODO set current user info
					// check for any active org
					List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(user.getUserId());

					if (pmUserOrgs != null && pmUserOrgs.size() > 0) {
						// the identification Number already in DB
						this.getManagedBean().getSelectedUser().setHasActiveOrg(true);
						PmOrgProfile orgProfile = pmUserOrgs.get(0).getPmOrgProfile();
						
						if (orgProfile.getOrgName() == null || orgProfile.getOrgCode() == null) {
							String param1 = null;
							
							if (orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode()
									.getParameterCode().equals(ParameterConst.ORG_TYPE_SUPPLIER)) {
								param1 = orgProfile.getSmSupplier().getCompanyName();

							} else if (orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile
									.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_FACTORING)) {
								param1 = orgProfile.getPmFinancialOrg().getFinOrgName();

							}
							
							throw new EpException(MessageConst.ERROR_770215, new String[] { param1 });
						}
						throw new EpException(MessageConst.ERROR_770155,
								new String[] { orgProfile.getOrgName(), orgProfile.getOrgCode() });

					} else {

						IPersonnelSO personnelSO;
						personnelSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);

						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
						PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);

						if (resDTO1.isSupplierPersonnel()) {
							throw new EpException(MessageConst.ERROR_770214,
									new String[] { getManagedBean().getSelectedUser().getIdentificationNo() });
						}
					}

				} else {// do validate by isSupplier

					IPersonnelSO personnelSO;
					personnelSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);

					PersonnelReqDTO reqDTO = new PersonnelReqDTO();
					reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
					PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);

					if (resDTO1.isSupplierPersonnel()) {
						throw new EpException(MessageConst.ERROR_770214,
								new String[] { getManagedBean().getSelectedUser().getIdentificationNo() });
					}
				}

			} finally {
				userSO = null;
			}
		}

	}

	public void checkIdentificationNumber() {
		logger.info(".............checkIdentificationNumber...........");
		getManagedBean().getSelectedUser().setLoginId(null);
		this.getManagedBean().getSelectedUser().setHasActiveOrg(false);
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		if (getManagedBean().getSelectedUser().getIdentificationNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getSelectedUser().getIdentificationNo())
				&& getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode() != null) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean().getSelectedUser().getIdentificationNo());

			searchBean.getDataFilterBeans().add(idNoSearchBean);

			searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("identificationCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode()));

			//#35562 - Start
			/*searchBean.getDataFilterBeans().add(new SearchFilterBean("recordStatusFlg",
					SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));*/
			//#35562 - End

			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo()); //#35769 - set Identification No
			userReqDTO.setUserSearchBean(searchBean);
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
				UserResDTO resDTO = userSO.searchUserProfileUsingIdNo(userReqDTO);//#35769 - Call New Method

				if (resDTO != null && resDTO.getUserResultBean().getResultList() != null
						&& resDTO.getUserResultBean().getResultList().size() > 0) {
					PmUser user = resDTO.getUserResultBean().getResultList().get(0);
					
					//#35562 - Start (Checking identification no same with login id)
					if (getManagedBean().getSelectedUser().getIdentificationNo().compareTo(user.getLoginId()) == 0) {
				
					// TODO set current user info
					// check for any active org
					List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(user.getUserId());

					if (pmUserOrgs != null && pmUserOrgs.size() > 0) {

						// the identification Number already in DB
						this.getManagedBean().getSelectedUser().setHasActiveOrg(true);
						PmOrgProfile orgProfile = pmUserOrgs.get(0).getPmOrgProfile();
						
						if (orgProfile.getOrgName() == null || orgProfile.getOrgCode() == null) {
							String param1 = null;
							
							if (orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode()
									.getParameterCode().equals(ParameterConst.ORG_TYPE_SUPPLIER)) {
								param1 = orgProfile.getSmSupplier().getCompanyName();

							} else if (orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile
									.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_FACTORING)) {
								param1 = orgProfile.getPmFinancialOrg().getFinOrgName();

							}

							throw new EpException(MessageConst.ERROR_770215, new String[] { param1 });
						}
						throw new EpException(MessageConst.ERROR_770155,
								new String[] { orgProfile.getOrgName(), orgProfile.getOrgCode() });
					} else {

						IPersonnelSO personnelSO;
						personnelSO = BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);

						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
						PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
						//
						if (resDTO1.isSupplierPersonnel()) {
							throw new EpException(MessageConst.ERROR_770214,
									new String[] { getManagedBean().getSelectedUser().getIdentificationNo() });
						} else {

							loadUserInfo(user.getUserId());
							// assign new pmUserOrg object to the current user
						}
					}
					} else {// do validate by isSupplier

						IPersonnelSO personnelSO;
						personnelSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.SUPPLIER_MANAGEMENT,
										IPersonnelSO.class);

						PersonnelReqDTO reqDTO = new PersonnelReqDTO();
						reqDTO.setIdentificationNo(getManagedBean()
								.getSelectedUser().getIdentificationNo());
						PersonnelResDTO resDTO1 = personnelSO
								.isSupplierPersonnel(reqDTO);
						if (resDTO1.isSupplierPersonnel()) {
							throw new EpException(MessageConst.ERROR_770214,
									new String[] { getManagedBean()
											.getSelectedUser()
											.getIdentificationNo() });
						}

						// new user org / user record
						getManagedBean().setPmUserExist(false);
						if (ParameterConst.IDENTIFICATION_TYPES_IC
								.equals(getManagedBean().getSelectedUser()
										.getIdentificationCode()
										.getParameterCode())) {
							getManagedBean().getSelectedUser().setLoginId(
									getManagedBean().getSelectedUser()
											.getIdentificationNo());
						} else {
							PmCountry country = PmCommonUtil
									.retrievePmCountryById(getManagedBean()
											.getSelectedUser().getPmCountry()
											.getCountryId());
							StringBuilder sb = new StringBuilder();
							sb.append(getManagedBean().getSelectedUser()
									.getIdentificationNo().trim());
							sb.append(country.getCountryCode());
							getManagedBean().getSelectedUser().setLoginId(
									sb.toString());
						}
					}
					// #35562 - End
				} else {// do validate by isSupplier

					IPersonnelSO personnelSO;
					personnelSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);

					PersonnelReqDTO reqDTO = new PersonnelReqDTO();
					reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
					PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
					if (resDTO1.isSupplierPersonnel()) {
						throw new EpException(MessageConst.ERROR_770214,
								new String[] { getManagedBean().getSelectedUser().getIdentificationNo() });
					}

					// new user org / user record
					getManagedBean().setPmUserExist(false);
					if (ParameterConst.IDENTIFICATION_TYPES_IC
							.equals(getManagedBean().getSelectedUser().getIdentificationCode().getParameterCode())) {
						getManagedBean().getSelectedUser()
								.setLoginId(getManagedBean().getSelectedUser().getIdentificationNo());
					} else {
						PmCountry country = PmCommonUtil.retrievePmCountryById(
								getManagedBean().getSelectedUser().getPmCountry().getCountryId());
						StringBuilder sb = new StringBuilder();
						sb.append(getManagedBean().getSelectedUser().getIdentificationNo().trim());
						sb.append(country.getCountryCode());
						getManagedBean().getSelectedUser().setLoginId(sb.toString());
					}
				}
			} finally {
				userSO = null;
			}
		}
	}

	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params, MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	private boolean isAnyPendingTaskForUser(String loginId) {
		logger.info("isAnyPendingTaskForUser");
		return PmCommonUtil.isAnyPendingTask(loginId);
	}

	private Long getCurrentOrgTypeId() {
		Long currentOrgTypeId = null;
		if (this.getManagedBean().getSelectedUserOrg() != null
				&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile() != null
				&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode() != null) {
			currentOrgTypeId = this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode()
					.getParameterId();
		}
		return currentOrgTypeId;
	}

	private PmUserGroup retrieveUserActiveUserGroup(Long userId) {
		logger.info("userActiveUserGroup");
		List<PmUserGroup> userGroups = PmCommonUtil.retrieveUserActiveUserGroup(userId, null);
		return userGroups != null ? userGroups.get(0) : null;
	}
	
	//Bug #31509 - Start 
	private PmUserGroup retrieveUserActiveAtUserGroup(Long userId, String role) {
		logger.info("userActiveAtUserGroup");
		List<PmUserGroup> userGroups = PmCommonUtil.retrieveUserActiveAtUserGroup(userId, role, null);
		return userGroups != null ? userGroups.get(0) : null;
	}
	//Bug #31509 - Start
	
	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		logger.info("getOrgTypeDropdownValues");
		ISearchBean<Object[]> searchBean = null;
		if (getManagedBean().getSearchOrgCategory() == FEDERAL_GOVT) {
			searchBean = getUserOrgTypeObjectSearchBean("somFederalOrgType");
		} else if (getManagedBean().getSearchOrgCategory() == OTHERS) {
			searchBean = getUserOrgTypeObjectSearchBean("somOtherOrgType");
		}
		
		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownObjectValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}

		return items;
	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getNewOrgTypeDropdownValues() {
		logger.info("getOrgTypeDropdownValues");
		ISearchBean<PmParameterDesc> searchBean = null;
		if (getManagedBean().getOrgCategory() == FEDERAL_GOVT) {
			searchBean = getUserOrgTypeSearchBean("orgTypeFederalList");
		} else if (getManagedBean().getOrgCategory() == OTHERS) {
			searchBean = getUserOrgTypeSearchBean("orgTypeOthersList");
			if (getManagedBean().getSelectedUserOrg() != null && getManagedBean().getSelectedUserOrg().isBrandNew()) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchField("pmParameter.parameterCode");
				bean.setSearchValue(new ArrayList<String>(Arrays.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE)));
				searchBean.getDataFilterBeans().add(bean);
			}
		}

		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}

		return items;
	}

	private ISearchBean<PmParameterDesc> getUserOrgTypeSearchBean(String componentId) {
		logger.info("getUserOrgTypeSearchBean+componentId=" + componentId);
		ISearchBean<PmParameterDesc> userOrgTypeSearchBean = super.createSearchBean(PmParameterDesc.class, componentId);
		return userOrgTypeSearchBean;
	}
	
	private ISearchBean<Object[]> getUserOrgTypeObjectSearchBean(String componentId) {
		logger.info("getUserOrgTypeSearchBean+componentId=" + componentId);
		ISearchBean<Object[]> userOrgTypeSearchBean = super.createSearchBean(Object[].class, componentId);
		return userOrgTypeSearchBean;
	}

	public String retrieveOrgName(PmOrgProfile orgProfile) {
		String orgName = null;
		if (orgProfile != null) {
			if (ParameterConst.ORG_TYPE_SUPPLIER.equals(orgProfile.getOrgTypeCode().getParameterCode())) {
				orgName = orgProfile.getSmSupplier().getCompanyName();
			} else if (ParameterConst.ORG_TYPE_FACTORING.equals(orgProfile.getOrgTypeCode().getParameterCode())) {
				orgName = orgProfile.getPmFinancialOrg().getFinOrgName();
			} else {
				orgName = orgProfile.getPmOrgValidity().getOrgName();
			}
		}

		return orgName;
	}

	public void deleteUserRole() {
		List<PmUserRole> userRoles = getManagedBean().getSelectedUserOrg().getPmUserRoles();
		PmUserRole removeUserRole = getManagedBean().getSelectedDeleteUserRole();
		userRoles.remove(removeUserRole);
		getManagedBean().getSelectedUserOrg().setPmUserRoles(userRoles);
		updateReceivingAddressFlag();
		getManagedBean().setRoleTabActiveIndex(0);
	}

	public void deleteUserReceivingAddr() {
		List<PmAddressType> addressTypes = getManagedBean().getSelectedReceivingAddrTypes();
		PmAddressType removeAddressType = getManagedBean().getSelectedDeleteReceivingAddr();
		addressTypes.remove(removeAddressType);
		this.getManagedBean().getSelectedUser().getPmAddressTypes().remove(removeAddressType);
		getManagedBean().setSelectedReceivingAddrTypes(addressTypes);
		updateReceivingAddressFlag();
		getManagedBean().setRoleTabActiveIndex(1);
	}

	public boolean checkActiveReceivingOfficerExist() {
		logger.info("checkReceivingOfficerExist");
		boolean isReceivingOfficer = false;
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
			for (PmUserRole userRole : this.getManagedBean().getSelectedUserOrg().getPmUserRoles()) {
				if (ProfileManagementConst.RECEIVING_OFFICER.equals(userRole.getPmRole().getRoleCode())
						&& DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()) {
					isReceivingOfficer = true;
					break;
				}
			}
		}
		return isReceivingOfficer;
	}

	public void notifySmsChangeListener() {
		logger.info("...........notifySmsChangeListener............");

		if (getManagedBean().getSelectedUser().isNotifyBySms()
				&& StringUtils.isBlank(getManagedBean().getSelectedUserOrg().getMobileNo())) {
			getManagedBean().getSelectedUser().setNotifyBySms(false);
			throw new EpException(MessageConst.ERROR_770090);
		}
		if (getManagedBean().getSelectedUser().isNotifyBySms()
				&& getManagedBean().getSelectedUserOrg().getMobileTelcoId() == null) {
			getManagedBean().getSelectedUser().setNotifyBySms(false);
			throw new EpException(MessageConst.ERROR_770250);
		}
		this.getManagedBean().getSelectedUser().setModified(true);
	}

	private String getRoleCode() {
		String roleCode = null;
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
			roleCode = RoleCode.EP_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value())) {
			roleCode = RoleCode.EP_ADMIN_1.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value())) {
			roleCode = RoleCode.EP_ADMIN_2.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			roleCode = RoleCode.MINISTRY_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			roleCode = RoleCode.JABATAN_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			roleCode = RoleCode.PTJ_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
			roleCode = RoleCode.BPK_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			roleCode = RoleCode.UPS_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.PROCUREMENT_ADMIN.value())) {
			roleCode = RoleCode.PROCUREMENT_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			roleCode = RoleCode.SUPPORT_ADMIN.value();
		}

		return roleCode;
	}

	// Org Type and Org Name will be defaulted to the admin's organisation
	// Only applicable for PTJ Admin,BPK Admin and Support Admin
	private void retrieveOrgTypeAndOrgName() {
		if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			if (getManagedBean().getCurrentActiveOrg() != null) {
				getManagedBean().setSelectedOrgType(getManagedBean().getCurrentActiveOrg().getOrgTypeCode());
				getManagedBean().setSelectedOrgProfile(getManagedBean().getCurrentActiveOrg());
				getManagedBean().setDisabledOrgFields(true);
				if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
					this.getManagedBean().setSearchOrgCategory(OTHERS);
				}
			}
		} else {
			getManagedBean().setDisabledOrgFields(false);
		}
	}

	public String getRoleDesc(String roleCode) {
		if (StringUtils.isNotBlank(roleCode)) {
			return RoleHelper.getInstance().getRoleDescByCode(roleCode).getRoleName();
		} else {
			return null;
		}

	}

	public void searchRoleMatrix() {
		IRoleSO roleSO = null;
		UserProfilesSearchBean searchBean = getManagedBean().getUserProfileSearchBean();
		if (searchBean.getSelectedRoleOptionSearch() == null
				&& StringUtils.isNotBlank(searchBean.getUserRoleNameSearch())) {
			throw new EpException(MessageConst.ERROR_100060);
		}

		try {
			ISearchBean<PmRoleDesc> searchBeanData = new SearchBean<PmRoleDesc>();

			if (StringUtils.isNotBlank(searchBean.getUserRoleNameSearch())) {
				searchBeanData.getDataFilterBeans().add(new SearchFilterBean("roleName",
						SearchOperator.OPERATOR_LIKE.value(), searchBean.getUserRoleNameSearch(), true));
			}

			RoleReqDTO roleReqDTO = new RoleReqDTO();
			roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());

			roleReqDTO.setSearchRoleBean(searchBeanData);

			roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
			roleReqDTO.setRoleCode(getRoleCode());
			roleReqDTO.setOrgTypeId(getManagedBean().getSelectedOrgType().getParameterId());
			roleReqDTO.setBpk(true);
			roleSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
			RoleResDTO resDto = roleSO.retrieveRoleDecsFromRoleMatrix(roleReqDTO);

			if (resDto != null && resDto.isSuccessful()) {
				getManagedBean().setRoleDescResults(resDto.getSearchRoleResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roleSO = null;
		}
	}

	public void resetRoleSearchAction() {
		getManagedBean().getUserProfileSearchBean().setSelectedRoleOptionSearch(null);
		getManagedBean().getUserProfileSearchBean().setUserRoleNameSearch(null);
		getManagedBean().getRoleDescResults().clear();
		searchRoleMatrix();
	}

	public void populateToRoleSaveObject() {
		if (getManagedBean().getSelectedRoleDesc() != null) {
			getManagedBean().getUserProfileSearchBean()
					.setUserRole(getManagedBean().getSelectedRoleDesc().getPmRole().getRoleCode());
		}
	}

	public boolean isUserRolesEmpty() {
		boolean isEmpty = true;
		if (getManagedBean().getSelectedUserOrg() != null
				&& getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()) {
			isEmpty = false;
		}
		return isEmpty;
	}

	public boolean isMYCountry() {
		boolean isMy = false;
		if (getManagedBean().getSelectedUser() != null && getManagedBean().getSelectedUser().getPmCountry() != null
				&& getManagedBean().getSelectedUser().getPmCountry().getCountryId() != null) {
			PmCountry country = PmCommonUtil
					.retrievePmCountryById(getManagedBean().getSelectedUser().getPmCountry().getCountryId());
			if (CommonConstants.DEFAULT_COUNTRY_CODE.equals(country.getCountryCode())) {
				isMy = true;
			}
		}
		return isMy;
	}

	public void resetIdType() {
		PmParameter identityType = new PmParameter();
		if (isMYCountry()) {
			identityType.setParameterCode(ProfileManagementConst.DEFAULT_IDENTIFY_TYPE);
		} else {
			identityType.setParameterCode(ProfileManagementConst.PASSPORT_IDENTIFY_TYPE);
		}

		getManagedBean().getSelectedUser().setIdentificationCode(identityType);
		getManagedBean().getSelectedUser().setIdentificationNo(null);
		getManagedBean().getSelectedUser().setLoginId(null);
	}

	public void resetIcAndLoginId() {
		getManagedBean().getSelectedUser().setIdentificationNo(null);
		getManagedBean().getSelectedUser().setLoginId(null);
	}

	public void selectPtjAction(PmOrgProfile orgProfile) {
		getManagedBean().setSelectedOrgProfile(orgProfile);
		this.getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(orgProfile.getOrgProfileId());
	}

	public void selectPtjActionForCreateUser(PmOrgProfile orgProfile) {
		getManagedBean().getSelectedUserOrg().setPmOrgProfile(orgProfile);
	}

	public boolean isDisableDeleteRole() {
		return disableDeleteRole;
	}

	public void setDisableDeleteRole(boolean disableDeleteRole) {
		this.disableDeleteRole = disableDeleteRole;
	}

	public String getRoleNameVal() {
		return roleNameVal;
	}

	public void setRoleNameVal(String roleNameVal) {
		this.roleNameVal = roleNameVal;
	}

	/**
	 * @return the federalGovt
	 */
	public int getFederalGovt() {
		return FEDERAL_GOVT;
	}

	/**
	 * @return the others
	 */
	public int getOthers() {
		return OTHERS;
	}

	private String[] isActiveUserGroupUser() {
		String[] errorParams = null;
		// check if user group user is active
		PmUserGroup userGroup = retrieveUserActiveUserGroup(this.getManagedBean().getSelectedUser().getUserId());

		if (userGroup != null) {
			errorParams = new String[] { userGroup.getGroupCode(), userGroup.getGroupName() };
		}

		return errorParams;
	}
	
	//Bug #31509 - Start 
	private String[] isActiveAtUserGroupUser(String role) {

		System.out.println("isActiveUserGroupUserTest" + role);

		String[] errorParams = null;
		// check if user group user is active
		PmUserGroup userGroup = retrieveUserActiveAtUserGroup(this.getManagedBean().getSelectedUser().getUserId(), role);

		if (userGroup != null) {
			errorParams = new String[] { userGroup.getGroupCode(), userGroup.getGroupName() };
		}

		return errorParams;
	}
	//Bug #31509 - End
	
	private boolean isUserRoleModified(PmUserRole userRole) {
		logger.info("isUserRoleModified");
		if (!userRole.isBrandNew()) {
			PmUserRole currentUserRole = PmCommonUtil.findUserRoleByUserRoleId(userRole.getUserRoleId());
			if (currentUserRole != null && currentUserRole.getRecordStatusFlg() != userRole.getRecordStatusFlg()) {
				logger.info("userRoleId :" + userRole.getUserRoleId() + " is modified.");
				return true;
			}
		}
		return false;
	}
	
	//[#22117]
	public List<Long> getListOfOrgIdByOrgIdAndRole(Long orgTypeId, Long orgProfileId, String role) {		
			
		IUserSO userSO = null;
			
		try {
			List<Long> usrOrgId = null;
			UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
			userOrgReqDTO.setOrgTypeId(orgTypeId);
			userOrgReqDTO.setOrgProfileId(orgProfileId);
			userOrgReqDTO.setRoleCode(role);
			
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			UserOrgResDTO resDTO = userSO.retrieveUserOrgIdByOrgIdAndRole(userOrgReqDTO);
			
			if (resDTO != null && resDTO.isSuccessful()) {
				if(resDTO.getPmUserOrgList() != null && resDTO.getPmUserOrgList().size() > 0) {
					usrOrgId = new ArrayList<Long>();
					for(PmUserOrg pmusrorg : resDTO.getPmUserOrgList()) {
						usrOrgId.add(pmusrorg.getUserOrgId());
					}							
					return usrOrgId;
				}					
			}					
		} finally {
			userSO = null;
		}

		return null;
	}
	
	//#29594 - START
	public String retrieveMinistryName() {
		return retrieveMinistryCodeNameByOrgProfileId(getManagedBean().getSelectedUserOrg().getPmOrgProfile()
				.getOrgProfileId());
	}
	
	public String retrievePTJGroupName() {
		return retrieveJabatanCodeNameByOrgProfileId(getManagedBean().getSelectedUserOrg().getPmOrgProfile()
				.getOrgProfileId());
	}
	//#29594 - END
	
	/*** checks If Selected Receiving Address Type. */
	// #35769 - Start // fixing null pointer after inactive user
	private boolean isCheckSelectedReceivingAddrTypes() {
		return this.getManagedBean().getSelectedReceivingAddrTypes() != null
				&& !this.getManagedBean().getSelectedReceivingAddrTypes()
						.isEmpty();
	}
	// #35769 - End // fixing null pointer after inactive user
	
	//#36489 and SonarLint Fixing - Start
	/**
	 * Check if user active at Supplier User Appointment in Org Profile
	 * Maintenance
	 */
	public void checkIsActiveGovtSellerUser() {
		logger.info("checkIsActiveGovtSellerUser...");

		IPersonnelSO personnelSO;
		personnelSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(this.getManagedBean().getPmUserOrgOriginal()
				.getPmOrgProfile().getOrgProfileId());
		PersonnelResDTO personnelResDTO = personnelSO
				.retrieveActiveSupUserApptByApplId(reqDTO);

		if (!personnelResDTO.getSmPersonnelList().isEmpty()
				&& personnelResDTO.getSmPersonnelList().size() == 1) {

			Iterator<SmPersonnel> a = personnelResDTO.getSmPersonnelList()
					.iterator();
			while (a.hasNext()) {
				SmPersonnel user = a.next();
				if (this.getManagedBean().getSelectedUser()
						.getIdentificationNo()
						.compareToIgnoreCase(user.getIdentificationNo()) == 0) {
					this.getManagedBean()
							.getSelectedUserOrg()
							.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);
					this.getManagedBean().setSuppGovSellerUser(false);
					throw new EpException(MessageConst.ERROR_775219);
				}
			}
		} else {
			Iterator<SmPersonnel> a = personnelResDTO.getSmPersonnelList()
					.iterator();
			while (a.hasNext()) {
				SmPersonnel user = a.next();
				if (this.getManagedBean().getSelectedUser()
						.getIdentificationNo()
						.compareToIgnoreCase(user.getIdentificationNo()) == 0) {
					logger.info("inactive smpersonnel ");
					this.getManagedBean().setSuppGovSellerUser(true);
				}
			}
		}
	}

	/**
	 * Set user role or receiving address to inactive
	 * 
	 * @param status
	 */
	public void inactiveUserOrgRoleAndReceivingAddress(int status) {
		logger.info("orgUnitStatusChange : inactive userOrgId :"
				+ this.getManagedBean().getSelectedUserOrg().getUserOrgId());
		this.getManagedBean().getSelectedUserOrg().setExpDateDisplay(null);
		this.getManagedBean().getSelectedUser().setHasActiveOrg(false);

		if (this.getManagedBean().getSelectedUserOrg() != null
				&& this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {
			for (PmUserRole pmUserRoles : this.getManagedBean()
					.getSelectedUserOrg().getPmUserRoles()) {
				logger.info("orgUnitStatusChange : inactive userRoleId :"
						+ pmUserRoles.getUserRoleId());
				pmUserRoles.setRecordStatusFlg(status);
				userRoleStatusChange(pmUserRoles);
			}
		}
		if (this.getManagedBean().getSelectedReceivingAddrTypes() != null
				&& !this.getManagedBean().getSelectedReceivingAddrTypes()
						.isEmpty()) {
			for (PmAddressType addressType : this.getManagedBean()
					.getSelectedReceivingAddrTypes()) {
				addressType.setRecordStatusFlg(status);
			}
		}
	}

	private boolean validateUserOrg() {
		return this.getManagedBean().getSelectedUserOrg() != null
				&& this.getManagedBean().getSelectedUserOrg().getUserOrgId() != null;
	}

	private List<PmUserRole> validateUserOrgandRole() {
		return this.getManagedBean().getSelectedUserOrg() != null ? this
				.getManagedBean().getSelectedUserOrg().getPmUserRoles() : null;
	}

	private boolean validateUserOrgProfileAndStatus() {
		return DbValueConst.RECORD_STATUS_ACTIVE != this.getManagedBean()
				.getSelectedUserOrg().getRecordStatusFlg()
				&& this.getManagedBean().getPmUserOrgOriginal() != null
				&& this.getManagedBean().getPmUserOrgOriginal()
						.getPmOrgProfile() != null
				&& this.getManagedBean().getPmUserOrgOriginal()
						.getPmOrgProfile().isGovtSeller();
	}

	/**
	 * Validation for PmUser's fields
	 * 
	 * @param user
	 * @param pmUserOrg
	 */
	private void validateUser(PmUser user, PmUserOrg pmUserOrg) {
		logger.info("validateUser");

		validateBasicUserDetails(user);
		validateIdentification(user);
		validateEmailAndNotificationPreferences(user, pmUserOrg);
		validateOrgProfile(pmUserOrg);
		validateExistingUserOrg(user, pmUserOrg);

		if (validateUserOrgProfileAndStatus()) {
			checkIsActiveGovtSellerUser();
		}
	}

	private void validateBasicUserDetails(PmUser user) {
		if (StringUtils.isBlank(user.getLoginId())) {
			throw new EpException(MessageConst.ERROR_770030);
		}
		if (user.getSalutationCode().getSalutationId() == null) {
			throw new EpException(MessageConst.ERROR_770031);
		}
		if (StringUtils.isBlank(user.getUserName())) {
			throw new EpException(MessageConst.ERROR_770032);
		}
	}

	private void validateIdentification(PmUser user) {
		if (StringUtils
				.isBlank(user.getIdentificationCode().getParameterCode())) {
			throw new EpException(MessageConst.ERROR_770033);
		}
		if (StringUtils.isBlank(user.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_770034);
		}
	}

	private void validateEmailAndNotificationPreferences(PmUser user,
			PmUserOrg pmUserOrg) {
		if (StringUtils.isBlank(pmUserOrg.getEmail())) {
			throw new EpException(MessageConst.ERROR_770036);
		}

		if (user.isNotifyBySms()) {
			if (StringUtils.isBlank(pmUserOrg.getMobileNo())) {
				throw new EpException(MessageConst.ERROR_770090);
			}
			if (pmUserOrg.getMobileTelcoId() == null) {
				throw new EpException(MessageConst.ERROR_770250);
			}
		}
	}

	private void validateOrgProfile(PmUserOrg pmUserOrg) {
		if (pmUserOrg.getPmOrgProfile() == null
				|| pmUserOrg.getPmOrgProfile().getOrgProfileId() == null) {
			throw new EpException(MessageConst.ERROR_770175);
		}

		String orgType = pmUserOrg.getPmOrgProfile().getOrgTypeCode()
				.getParameterCode();
		if (ParameterConst.ORG_TYPE_FACTORING.equals(orgType)) {
			validateFactoringOrg(pmUserOrg);
		} else if (ParameterConst.ORG_TYPE_SUPPLIER.equals(orgType)) {
			validateSupplierOrg(pmUserOrg);
		} else {
			validateOtherOrg(pmUserOrg);
		}

		if (!ParameterConst.ORG_TYPE_SUPPLIER.equals(orgType)
				&& StringUtils.isBlank(pmUserOrg.getDesignation())) {
			throw new EpException(MessageConst.ERROR_770108);
		}
	}

	private void validateFactoringOrg(PmUserOrg pmUserOrg) {
		if (pmUserOrg.getPmOrgProfile().getPmFinancialOrg() == null
				|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile()
						.getPmFinancialOrg().getFinOrgName())) {
			throw new EpException(MessageConst.ERROR_770175);
		}
	}

	private void validateSupplierOrg(PmUserOrg pmUserOrg) {
		if (pmUserOrg.getPmOrgProfile().getSmSupplier() == null
				|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile()
						.getSmSupplier().getCompanyName())
				|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile()
						.getSmSupplier().getRegNo())) {
			throw new EpException(MessageConst.ERROR_770175);
		}
	}

	private void validateOtherOrg(PmUserOrg pmUserOrg) {
		if (pmUserOrg.getPmOrgProfile().getPmOrgValidity() == null
				|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile()
						.getPmOrgValidity().getOrgCode())
				|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile()
						.getPmOrgValidity().getOrgName())) {
			throw new EpException(MessageConst.ERROR_770175);
		}
	}

	private void validateExistingUserOrg(PmUser user, PmUserOrg pmUserOrg) {
		if (user.getUserId() != null
				&& DbValueConst.RECORD_STATUS_ACTIVE == pmUserOrg
						.getRecordStatusFlg()) {
			// validate if the user contains any active user orgs (exclude supplier type and own org profile)
			IUserSO userSO = null;
			try {
				UserReqDTO userOrgReqDTO = new UserReqDTO();
				userOrgReqDTO.setUserId(user.getUserId());
				userOrgReqDTO.setExcludeOrgType(OrganizationType.SUPPLIER
						.value());
				userOrgReqDTO.setUserOrgChecking(true);

				if (!pmUserOrg.isBrandNew() && pmUserOrg.getUserOrgId() != null) {
					userOrgReqDTO.setUserOrgId(pmUserOrg.getUserOrgId());
					userOrgReqDTO.setExcludeOrgProfileId(pmUserOrg
							.getPmOrgProfile().getOrgProfileId());
				}

				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO
						.retrieveAllUserOrgListByUserId(userOrgReqDTO);

				if (resDTO.getUserOrgList() != null
						&& !resDTO.getUserOrgList().isEmpty()) {
					PmOrgProfile orgProfile = resDTO.getUserOrgList().get(0)
							.getPmOrgProfile();
					throw new EpException(MessageConst.ERROR_770155,
							new String[] { orgProfile.getOrgName(),
									orgProfile.getOrgCode() });
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(
						ModuleCodeConst.PROFILE_MANAGEMENT, null, null,
						"ProfileManagementBackingBean.validateExistingUserOrg",
						null, e);
			}
		}
	}
	//#36489 and SonarLint Fixing - End
}
