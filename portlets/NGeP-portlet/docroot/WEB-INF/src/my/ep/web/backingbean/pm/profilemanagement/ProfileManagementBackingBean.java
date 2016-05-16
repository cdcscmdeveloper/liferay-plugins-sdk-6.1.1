package my.ep.web.backingbean.pm.profilemanagement;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
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
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.authorization.usergroup.IUserGroupSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
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
import my.ep.dto.pm.authorization.usergroup.UserGroupReqDTO;
import my.ep.dto.pm.authorization.usergroup.UserGroupResDTO;
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
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RoleHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author DK, RAIN POH
 * @since 8/7/2013
 * 
 */
@ManagedBean(name = "profileManagementBackingBean")
@ViewScoped
public class ProfileManagementBackingBean extends
		BaseBackingBean<ProfileManagementManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(ProfileManagementBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/profilemanagement/search/profileManagementMain.xhtml?faces-redirect=true"; // main
																													// page
																													// for
																													// portlet
																													// profile
																													// maintenance
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

	// Paging for listing
	private IPagingDataModel<PmUserOrg> userOrgDataModel = new UserProfilePagingDataModel();
	private IPagingDataModel<PmRoleDesc> roleDataModel = new RolePagingDataModel();

	// Mapper for DDL object
	private static Map<String, Object> identityTypeMapper;
	private static Map<Long, Object> orgTypeMapper;

	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;
	private String roleDecriptionFilter;

	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		logger.info("getOrgTypeSearchBean");
		if (this.orgTypeSearchBean == null) {
			this.orgTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "somOrgType");

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(new ArrayList<String>(Arrays
					.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE)));
			orgTypeSearchBean.getDataFilterBeans().add(bean);

			if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
				if (this.getManagedBean().getCurrentActiveOrg() != null) {
					SearchFilterBean orgTypeBean = new SearchFilterBean();
					orgTypeBean
							.setSearchOperator(SearchOperator.OPERATOR_EQUALS
									.value());
					orgTypeBean.setSearchField("pmParameter.parameterCode");
					orgTypeBean.setSearchValue(this.getManagedBean()
							.getCurrentActiveOrg().getOrgCode());
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

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
				getManagedBean().getSelectedOrgType().getParameterCode(),
				getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())
				));

		return sb.toString();
	}

	public String constructConditionFieldsForCreate() {

		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(getManagedBean().getSelectedUserOrg().getPmOrgProfile()
				.getOrgTypeCode().getParameterCode());
		sb.append("|orgTypeCode.parameterType^");
		sb.append(ParameterTypeConst.ORGANISATION_TYPES);
		sb.append("|recordStatus^1");
		logger.info("....getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode()="
				+ getManagedBean().getSelectedUserOrg().getPmOrgProfile()
						.getOrgTypeCode().getParameterCode());
		logger.info("....this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()="
				+ this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		logger.info("....getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()="
				+ getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()));

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
				getManagedBean().getSelectedUserOrg().getPmOrgProfile()
				.getOrgTypeCode().getParameterCode(),
				getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())
		));
		
		return sb.toString();
	}

	private void generateAdditionalFilter() {
		logger.info("generateAdditionalFilter");
		this.getManagedBean().setAdditionalOrgFilter("");
		// generate additional filter
		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId()
							+ "*parentOrgProfile.orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId()
							+ "*parentOrgProfile.orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
		} else if (getFacesScopes().hasRoleUser(
				RoleCode.FACTORING_ADMIN.value())) {
			this.getManagedBean().setAdditionalOrgFilter(
					"orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
		}
	}

	public void updateSearchCriteriaForRole(BaseEntity entity) {
		logger.info("...............updateSearchCriteriaForRole...............");
		getManagedBean().getUserProfileSearchBean().setUserRoleStatus(null);
		if (entity != null) {
			PmRoleMatrix pmRoleMatrix = (PmRoleMatrix) entity;
			if (pmRoleMatrix != null) {
				String userRole = pmRoleMatrix.getAssigneeRole().getRoleCode();
				this.getManagedBean().getUserProfileSearchBean()
						.setUserRole(userRole);
			}
		}
	}

	public void updateSearchCriteriaForOrgName(BaseEntity entity) {
		logger.info("updateSearchCriteriaForOrgName");
		// if (this.getManagedBean().getSelectedOrgProfile().getPmOrgValidity()
		// != null) {
		// String orgName = this.getManagedBean().getSelectedOrgProfile()
		// .getPmOrgValidity().getOrgName();
		//
		// this.getManagedBean().getUserProfileSearchBean()
		// .setUserOrganizationName(orgName);
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getUserProfileSearchBean()
				.setSelectedOrgProfileId(orgProfile.getOrgProfileId());
		// get OrgTypeCode from selected orgProfile
		// this.getManagedBean().getSelectedOrgProfile().getOrgTypeCode()
		// .getParameterId();
		// }

	}

	public void getListOfAssignedRoles(PmUserOrg org) {
		// find the userOrg
		// List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveAllUserOrg(userId);
		if (org != null) {
			this.getManagedBean().setActiveUserOrg(org);// not
														// active
														// only
														// TODO
			this.getManagedBean().setActiveUserRoleFor(
					new ArrayList<PmUserRole>());
			for (PmUserRole pmUserRole : this.getManagedBean()
					.getActiveUserOrg().getPmUserRoles()) {
				// if (pmUserRole.getRecordStatusFlg() ==
				// DbValueConst.RECORD_STATUS_ACTIVE) {
				this.getManagedBean().getActiveUserRoleFor().add(pmUserRole);
				// }
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
			System.out.println("***userId: " + pmUser.getUserId());

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
		retrieveOrgTypeAndOrgName();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	/**
	 * reset basic search bean
	 */
	public void resetUserProfileSearchBean() {
		logger.info("resetUserProfileSearchBean");
		this.getManagedBean().setUserProfileSearchBean(
				new UserProfilesSearchBean());
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		this.getManagedBean().setSelectedRoleDesc(null);
		getManagedBean().setLazy(false);
		userOrgDataModel = new UserProfilePagingDataModel();
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
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), isFactoring);

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
	public String redirectToMaintainUser(Long id, PmUserOrg selectedOrg)
			throws Exception {
		logger.info("redirectToMaintainUser");
		getManagedBean().setRoleTabActiveIndex(0);
		this.setCurrentActiveOrg();
		this.generateAdditionalFilter();
		this.retrieveIdentityTypeMap();
		this.retrieveOrgTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		logger.info("selectedOrg=" + selectedOrg);
		if (selectedOrg != null) {
			logger.info("selectedOrg.getUserOrgId()="
					+ selectedOrg.getUserOrgId());
		}
		if (id != null && id > 0) {
			loadUserProfile(id, selectedOrg);
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
	private void loadUserProfile(Long id, PmUserOrg selectedOrg) {
		logger.info("loadUserProfile - Start");
		logger.info("loadUserProfile - userId=" + id);
		logger.info("loadUserProfile - selectedOrg="
				+ selectedOrg.getUserOrgId());
		try {
			// initialUserProfile();
			if (id == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}
			// load user
			// PmUser pmUser = PmCommonUtil.retrieveUserById(id, true);
			PmUserOrg pmUserOrg = PmCommonUtil.retrieveUserOrgByUserIdAndOrgId(
					id, selectedOrg.getUserOrgId());
			logger.info("pmUserOrg=" + pmUserOrg);
			pmUserOrg.setPMP(pmUserOrg.isPMP() ? true : false);
			PmUser pmUser = pmUserOrg.getPmUser();
			pmUser.setIdentificationCode(pmUser.getIdentificationCode() == null ? new PmParameter()
					: pmUser.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode() == null ? new PmSalutation()
					: pmUser.getSalutationCode());
			this.getManagedBean().setPmUserOriginal(
					(PmUser) pmUser.cloneEntity());
			this.getManagedBean().setPmUserOrgOriginal(
					(PmUserOrg) pmUserOrg.cloneEntity());
			
			if(DbValueConst.RECORD_STATUS_ACTIVE != pmUserOrg.getRecordStatusFlg() ||
					DbValueConst.RECORD_STATUS_ACTIVE != pmUser.getRecordStatusFlg() ){
				getManagedBean().setActiveUser(false);
			}else{
				getManagedBean().setActiveUser(true);
			}
			
			
			for (PmUserRole userRole : pmUserOrg.getPmUserRoles()) {// put
																	// letterNo
																	// and
																	// approvalLimit
																	// in maps
																	// to check
																	// later
																	// changes
																	// for
																	// tracking
																	// diary
				if (userRole.getPmRole().getRoleCode().equals("FL_APPROVER")) {
					this.getManagedBean()
							.getRoleCodeToLetterNoMap()
							.put(userRole.getPmRole().getRoleCode(),
									userRole.getLetterNo());
				}
				if (userRole.getPmRole().isLimitRequired()) {
					this.getManagedBean()
							.getRoleCodeToApprovalLimitMap()
							.put(userRole.getPmRole().getRoleCode(),
									userRole.getApprovalLimit());
				}

			}
			// pmUser.setOrgTypeCode(pmUser.getOrgTypeCode() == null ? new
			// PmParameter()
			// : pmUser.getOrgTypeCode());

			// populateUserAddress(pmUser);

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
			this.getManagedBean().setFileEventMapper(
					new HashMap<PmUserAttachment, FileUploadEvent>());
			// for the initial load
			// if (pmUser.getPmUserOrgs() != null
			// && !pmUser.getPmUserOrgs().isEmpty()) {
			this.getManagedBean().setSelectedUserOrg(pmUserOrg);// TODO
																// retrive
																// userORg
																// with
																// relations
																// again
			showReceivingAddress();
			this.getManagedBean().setIsNewUser(false);
			// set org category
			if (Arrays.asList(
					ProfileManagementConst.ORG_CATEGORY_FEDERAL_GOVT_UP)
					.contains(
							pmUserOrg.getPmOrgProfile().getOrgTypeCode()
									.getParameterCode())) {
				getManagedBean().setOrgCategory(FEDERAL_GOVT);
			} else if (Arrays.asList(
					ProfileManagementConst.ORG_CATEGORY_OTHERS_UP).contains(
					pmUserOrg.getPmOrgProfile().getOrgTypeCode()
							.getParameterCode())) {
				getManagedBean().setOrgCategory(OTHERS);
			}
			// }
			// addressEntered();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016,
					new String[] { e.getMessage() });
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
				pmUser.setIdentificationCode(pmUser.getIdentificationCode() == null ? new PmParameter()
						: pmUser.getIdentificationCode());
				pmUser.setSalutationCode(pmUser.getSalutationCode() == null ? new PmSalutation()
						: pmUser.getSalutationCode());
				// pmUser.setOrgTypeCode(pmUser.getOrgTypeCode() == null ? new
				// PmParameter()
				// : pmUser.getOrgTypeCode());
				// // for the initial load
				PmUserOrg pmUserOrg = this.getManagedBean()
						.getSelectedUserOrg();
				pmUserOrg.setPmUser(pmUser);
				pmUser.getPmUserOrgs().add(pmUserOrg);
				this.getManagedBean().setSelectedUserOrg(pmUserOrg);
				///////////////////////the following info need to use for Tracking Dairy
				this.getManagedBean().setPmUserOriginal(
						(PmUser) pmUser.cloneEntity());
				this.getManagedBean().setPmUserOrgOriginal(
						(PmUserOrg) pmUserOrg.cloneEntity());
				
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
			this.getManagedBean().setFileEventMapper(
					new HashMap<PmUserAttachment, FileUploadEvent>());

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016,
					new String[] { e.getMessage() });
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
		identityType
				.setParameterCode(ProfileManagementConst.DEFAULT_IDENTIFY_TYPE);
		pmUser.setIdentificationCode(identityType);

		pmUser.setSalutationCode(new PmSalutation());

		pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());

		// ////////////////////fill up relations to PmUser
		PmUserOrg pmUserOrg = new PmUserOrg();
		pmUserOrg.setPMP(false);
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		pmOrgProfile.setOrgTypeCode(new PmParameter());
		pmOrgProfile.setPmOrgValidity(new PmOrgValidity());
		pmOrgProfile.setPmFinancialOrg(new PmFinancialOrg());
		pmUserOrg.setPmOrgProfile(pmOrgProfile);
		pmUserOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
		pmUserOrg.setPmUser(pmUser);
		
		if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value()) || 
				getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value()) ||
				getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())|| 
				getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value()) ){
			if(getManagedBean().getCurrentActiveOrg() != null){
				pmUserOrg.setPmOrgProfile(getManagedBean().getCurrentActiveOrg());
			}
			pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
		}
		
		pmUser.getPmUserOrgs().add(pmUserOrg);
		this.getManagedBean().setSelectedUserOrg(pmUserOrg);
		logger.info(".........this.getManagedBean().getSelectedUserOrg()="
				+ this.getManagedBean().getSelectedUserOrg());

		// pmUser.setOrgTypeCode(new PmParameter());
		// pmUser.setOrgAddress(ProfileManagementConst.DEFAULT_IS_SAME_ORG_ADDR);
		pmUser.setNotifyByEmail(ProfileManagementConst.DEFAULT_IS_NOTIFY_BY_EMAIL);

		// initial preference
		PmCommonUtil.loadPmUserPreference(pmUser);

		// initial address
		this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());

		// load password
		pmUser.setPassword(null);
		// pmUser.setOldPassword(null);
		pmUser.setConfirmPassword(null);
		pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());

		if(getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())){
			getManagedBean().setOrgCategory(OTHERS);
		}else{
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
		this.getManagedBean().setFileEventMapper(
				new HashMap<PmUserAttachment, FileUploadEvent>());
		this.getManagedBean().setIsNewUser(true);
		System.out.println("login "
				+ this.getManagedBean().getSelectedUser().getLoginId());
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
					.retrieveAddressTypesByUserIdOrAddressType(
							pmUser.getUserId(),
							ProfileManagementConst.ADDRESS_USER);
			this.getManagedBean()
					.setUserAddress(
							(addressTypes != null && !addressTypes.isEmpty()) ? addressTypes
									.get(0).getPmAddress() : PmCommonUtil
									.getNewPmAddress());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770017,
					new String[] { e.getMessage() });
		}
	}

	public String getRoleDecriptionFilter() {
		String assignorRole = getRoleCode();
		StringBuilder sb = new StringBuilder();
		sb.append("assignorRole.roleCode^");
		sb.append(assignorRole);
		sb.append("|");
		sb.append("assigneeRole.pmRoleDescs.languageCode^");
		sb.append(getFacesScopes().getUserLocale()
		.getLanguage());
		return sb.toString();
	}

	public void setRoleDecriptionFilter(String roleDecriptionFilter) {
		this.roleDecriptionFilter = roleDecriptionFilter;
	}

	/**
	 * retrieve salutation list
	 * 
	 * @return List<PmSalutation>
	 */
	public List<PmSalutation> salutationOptions() {
		logger.info("salutationOptions");
		return PmCommonUtil.retrievePmSalutationList();
		// return getDropdownValues(ParameterTypeConst.SALUTATIONS);
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
//		if (!this.getManagedBean().getSelectedUser().isOrgAddress()) {
//			PmAddress pmAddress = this.getManagedBean().getUserAddress();
//			// address is optional
//			if (pmAddress.getPmCountry().getCountryId() != null
//					|| !StringUtils.isBlank(pmAddress.getAddress1())
//					|| !StringUtils.isBlank(pmAddress.getPostcode())
//					|| pmAddress.getPmState().getStateId() != null
//					|| pmAddress.getPmDistrict().getDistrictId() != null
//					|| pmAddress.getPmDivision().getDivisionId() != null) {
//				this.getManagedBean().setAddressEntered(true);
//			} else {
//				this.getManagedBean().setAddressEntered(false);
//			}
//		}
		logger.info("addressEntered: "
				+ this.getManagedBean().isAddressEntered());
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
					(pmState != null && ParameterConst.SARAK_SK_CODE
							.equals(pmState.getStateCode())) ? true : false);
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
			this.getManagedBean().getUserAddress()
					.setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress()
					.setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		} else if (1 == address) { // state
			this.getManagedBean().getUserAddress()
					.setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress()
					.setPmDistrict(new PmDistrict());
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
				.getParameterDescListBySearchBean(
						ParameterTypeConst.IDENTIFICATION_TYPES, null);

		identityTypeMapper = new HashMap<String, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterCode(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * retrieve all org type into mapper
	 */
	private void retrieveOrgTypeMap() {
		logger.info("....retrieveOrgTypeMap....");
		List<PmParameterDesc> parameterDescs = PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.ORGANISATION_TYPES,null,
						null);

		orgTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

	}

	/**
	 * set user identity type
	 */
	public void identityTypeChange() {
		logger.info("identityTypeChange");

		if (this.getManagedBean().getSelectedUser().getIdentificationCode()
				.getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) identityTypeMapper.get(this
					.getManagedBean().getSelectedUser().getIdentificationCode()
					.getParameterCode());
			this.getManagedBean().getSelectedUser().getIdentificationCode()
					.setParameterCode(pmParameter.getParameterCode());
		}
	}

	/**
	 * login id = identification no.
	 */
	public void updateLoginId() {
		this.getManagedBean()
				.getSelectedUser()
				.setLoginId(
						this.getManagedBean().getSelectedUser()
								.getIdentificationNo());
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
			Date orgDate = new Date(orgEffDate.getTime()); // DateUtils.convertStringToDate("dd/MM/yyyy",
															// argValue);
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

	//
	// /**
	// * Retrieve Org. Type from PM
	// * @return List<PmParameterDesc>
	// */
	// public List<PmParameterDesc> getOrgTypeList() {
	// logger.info("getOrgTypeList");
	// List<PmParameterDesc> parameterDescs =
	// PmCommonUtil.retrieveFilteredParamDesc(ParameterTypeConst.ORGANISATION_TYPES
	// , new ArrayList(Arrays.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE))
	// , null);
	//
	// orgTypeMapper = new HashMap<Long,Object>();
	// for(PmParameterDesc pmDesc: parameterDescs){
	// orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
	// pmDesc.getPmParameter());
	// }
	//
	// return parameterDescs;
	// }

	/**
	 * To add new item to Responsible Org. Unit list
	 */
	public void addOrgUnit() {
		logger.info("addOrgUnit");
		if (this.getManagedBean().getSelectedUser().getPmUserOrgs() == null) {
			this.getManagedBean().getSelectedUser()
					.setPmUserOrgs(new ArrayList<PmUserOrg>());
		}
		// if(!this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()){
		// for(PmUserOrg pmUserOrg:
		// this.getManagedBean().getSelectedUser().getPmUserOrgs()){
		// if(pmUserOrg.getExpDate()==null){
		// throw new EpException(MessageConst.ERROR_770018);
		// }
		// }
		// }

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
				this.getManagedBean().getSelectedUser()
						.setPmUserOrgs(new ArrayList<PmUserOrg>());
			}
			this.getManagedBean().getSelectedUser().getPmUserOrgs()
					.remove(pmUserOrg);
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
					&& !this.getManagedBean().getSelectedUser().getPmUserOrgs()
							.isEmpty()) {
				int i = 0;
				for (PmUserOrg pmUserOrg : this.getManagedBean()
						.getSelectedUser().getPmUserOrgs()) {
					if (pmUserOrg.getPmOrgProfile().getOrgProfileId()
							.equals(pmOrgProfile.getOrgProfileId())
							&& i != index) {
						isValid = false;
						break;
					}
					i++;
				}
				if (!isValid) {
					this.getManagedBean().getSelectedUser().getPmUserOrgs()
							.get(index).getPmOrgProfile()
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

				if (isAnyPendingTaskForAdressType(addressType, this
						.getManagedBean().getSelectedUser().getLoginId())) {
					// revert it to active
					addressType
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw new EpException(MessageConst.ERROR_770170);
				}
			}

		}
		getManagedBean().setRoleTabActiveIndex(1);

	}

	public void orgUnitStatusChange() {
		logger.info("orgUnitStatusChange");
		if (this.getManagedBean().getSelectedUserOrg() != null
				&& this.getManagedBean().getSelectedUserOrg().getUserOrgId() != null) {

			int status = this.getManagedBean().getSelectedUserOrg()
					.getRecordStatusFlg();
			if (status == DbValueConst.RECORD_STATUS_SUSPENDED) {// if inactive
																	// set
																	// expire
																	// Date to
																	// SyetemDate

				if (isAnyPendingTaskForUser(this.getManagedBean()
						.getSelectedUser().getLoginId())) {
					// revert it to active
					this.getManagedBean()
							.getSelectedUserOrg()
							.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);

					// hit error and exit
					throw new EpException(MessageConst.ERROR_770167);
				} else {
					this.getManagedBean().getSelectedUserOrg()
							.setExpDateDisplay(null);
					this.getManagedBean().getSelectedUser()
							.setHasActiveOrg(false);
				}

				if (this.getManagedBean().getSelectedUserOrg() != null
						&& this.getManagedBean().getSelectedUserOrg()
								.getPmUserRoles() != null) {
					for (PmUserRole pmUserRoles : this.getManagedBean()
							.getSelectedUserOrg().getPmUserRoles()) {
						pmUserRoles.setRecordStatusFlg(status);
						userRoleStatusChange(pmUserRoles);
					}
				}
				if (this.getManagedBean().getSelectedReceivingAddrTypes() != null
						&& !this.getManagedBean()
								.getSelectedReceivingAddrTypes().isEmpty()) {
					for (PmAddressType addressType : this.getManagedBean()
							.getSelectedReceivingAddrTypes()) {
						addressType.setRecordStatusFlg(status);
					}
				}
				
				//check if user group user is active
				PmUserGroup userGroup = retrieveUserActiveUserGroup(this.getManagedBean()
						.getSelectedUser().getUserId());
					
				if(userGroup != null){
					// revert it to active
					this.getManagedBean()
							.getSelectedUserOrg()
							.setRecordStatusFlg(
									DbValueConst.RECORD_STATUS_ACTIVE);

					throw new EpException(MessageConst.ERROR_770202, new
							 String[]{userGroup.getGroupCode(),userGroup.getGroupName()});
				}
			}

		}
	}

	public void userStatusChange() {
		logger.info("userStatusChange");
		// check for any pending tasks belong to this user

		if (this.getManagedBean().getSelectedUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			if (isAnyPendingTaskForUser(this.getManagedBean().getSelectedUser()
					.getLoginId())) {
				// revert it to active
				this.getManagedBean().getSelectedUser()
						.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				// hit error and exit
				throw new EpException(MessageConst.ERROR_770166);
			} else {

				if (this.getManagedBean().getSelectedUser().getPmUserOrgs() != null
						&& !this.getManagedBean().getSelectedUser()
								.getPmUserOrgs().isEmpty()) {
					for (PmUserOrg pmUserOrg : this.getManagedBean()
							.getSelectedUser().getPmUserOrgs()) {
						pmUserOrg
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						if (pmUserOrg.getPmUserRoles() != null) {
							for (PmUserRole pmUserRole : pmUserOrg
									.getPmUserRoles()) {
								pmUserRole
										.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
								userRoleStatusChange(pmUserRole);
							}
						}
						for (PmAddressType addressType : this.getManagedBean()
								.getSelectedReceivingAddrTypes()) {
							addressType
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}
					}
				}
			}
		}
	}

	/*
	 * public void changeResponsibleOrgDates(ValueChangeEvent changeEvent){
	 * logger.info("changeResponsibleOrgDates"); System.out.println("NEW " +
	 * changeEvent.getNewValue()); System.out.println("OLD " +
	 * changeEvent.getOldValue()); }
	 */
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
		this.getManagedBean().getSelectedUserOrg()
				.setPmUserRoles(new ArrayList<PmUserRole>());
		// clear reciving Addresses
		showReceivingAddress();

		if (this.getManagedBean().getSelectedUserOrg().getPmOrgProfile()
				.getOrgTypeCode().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
					.getManagedBean().getSelectedUserOrg().getPmOrgProfile()
					.getOrgTypeCode().getParameterId());
			// this.getManagedBean().getSelectedUserOrg().getPmOrgProfile()
			// .getOrgTypeCode()
			// .setParameterCode(pmParameter.getParameterCode());
			//
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
				&& getManagedBean().getSelectedUserOrg().getPmOrgProfile()
						.getOrgProfileId() != null) {
			if (getManagedBean().getSelectedCurrentOrgProfile() == null) {
				getManagedBean()
						.setSelectedCurrentOrgProfile(
								getManagedBean().getSelectedUserOrg()
										.getPmOrgProfile());
			} else {
				if (!getManagedBean().getSelectedCurrentOrgProfile()
						.getOrgProfileId().equals(getManagedBean()
						.getSelectedUserOrg().getPmOrgProfile()
						.getOrgProfileId())) {
					// clear role list
					this.getManagedBean().getSelectedUserOrg()
							.setPmUserRoles(new ArrayList<PmUserRole>());
					// clear reciving Addresses
					showReceivingAddress();
					getManagedBean().setSelectedCurrentOrgProfile(
							getManagedBean().getSelectedUserOrg()
									.getPmOrgProfile());
				}
			}
		}

	}

	public void clearOrgNameAndRoleLovSearchFields() {
		logger.info("clearOrgNameAndRoleLovSearchFields");
		// if (this.getManagedBean().getSelectedOrgProfile() != null) {
		// this.getManagedBean().getSelectedOrgProfile()
		// .setPmOrgValidity(new PmOrgValidity());
		// // this.getManagedBean().getSelectedUserOrg()
		// // .setPmUserRoles(new ArrayList<PmUserRole>());
		// }
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setSelectedRoleDesc(null);
		getManagedBean().getUserProfileSearchBean().setUserRole(null);
		getManagedBean().getUserProfileSearchBean().setSelectedOrgProfileId(null);
		getManagedBean().getUserProfileSearchBean().setUserRoleStatus(null);
		if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
					.getManagedBean().getSelectedOrgType().getParameterId());
			this.getManagedBean().getSelectedOrgType()
					.setParameterCode(pmParameter.getParameterCode());
		}else{
			this.getManagedBean().getSelectedOrgType()
			.setParameterCode(null);
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
	private boolean checkUniqueRole(String orgTypeCode, String roleCode,
			boolean searchDB) {
		boolean isExist = false;
		if (searchDB) {
			SearchBean<PmUser> userSearchBean = new SearchBean<PmUser>();

			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_EQUALS.value(), roleCode,
							true));

			userSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserOrgs.pmUserRoles.recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			userSearchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean(
							"pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgTypeCode, true));

			List<PmUser> users = PmCommonUtil
					.searchUsersBySearchBean(userSearchBean);
			if (users != null && !users.isEmpty()) {
				isExist = true;
			}
		} else {
			if (this.getManagedBean().getSelectedUser().getPmUserOrgs() != null) {
				for (PmUserOrg userOrg : this.getManagedBean()
						.getSelectedUser().getPmUserOrgs()) {
					for (PmUserRole userRole : userOrg.getPmUserRoles()) {
						if (DbValueConst.RECORD_STATUS_ACTIVE == userRole
								.getRecordStatusFlg()) {
							// check if contains unique role
							if (roleCode.equals(userRole.getPmRole()
									.getRoleCode())) {
								if (orgTypeCode.equals(userOrg
										.getPmOrgProfile().getOrgTypeCode()
										.getParameterCode())) {
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

	// /**
	// * 9 Dec 2014
	// * Add new selected roles (multiple) to user role list
	// */
	// public void confirmRoles(){
	// logger.info("confirmRoles");
	// String orgType =
	// this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode();
	// if(this.getManagedBean().getSelectedRoles()!=null &&
	// !this.getManagedBean().getSelectedRoles().isEmpty()){
	// if(this.getManagedBean().getSelectedUserOrg()!=null){
	// //Validation for unique roles added to list
	// if(isUniqueRoles()){
	// List <String> uniqueRoles = new
	// ArrayList(Arrays.asList(ProfileManagementConst.UNIQUE_ORG_ROLES));
	// for(PmRoleDesc pmRoleDesc : this.getManagedBean().getSelectedRoles()){
	// if(uniqueRoles.contains(pmRoleDesc.getPmRole().getRoleCode())){
	// if(checkUniqueRole(orgType, pmRoleDesc.getPmRole().getRoleCode(),
	// false)){
	// throw new EpException(MessageConst.ERROR_770106, new
	// String[]{pmRoleDesc.getRoleName()});
	// }
	// }
	//
	// PmUserRole pmUserRole = new PmUserRole();
	// pmUserRole.setPmRole(pmRoleDesc.getPmRole());
	// this.getManagedBean().getSelectedUserOrg().addPmUserRole(pmUserRole);
	// }
	// this.getManagedBean().getSelectedRoles().clear();
	// updateReceivingAddressFlag();
	// }else{
	// throw new EpException(MessageConst.ERROR_770020);
	// }
	// }else{
	// throw new EpException(MessageConst.ERROR_770021);
	// }
	// }else{
	// throw new EpException(MessageConst.ERROR_770022);
	// }
	// RequestContext.getCurrentInstance().execute("dlRoleOptions.hide()");
	//
	// }
	/*
	 * Add new selected role (single) to user role list
	 */
	public void confirmRoles() {
		logger.info("confirmRoles");
		roleValidation();

		// Add to user role list
		PmUserRole pmUserRole = new PmUserRole();
		pmUserRole.setPmRole(this.getManagedBean().getSelectedRole()
				.getPmRole());
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

		// Validation for unique roles added to list (front end handle this
		// already)
		// if(!isUniqueRoles()){
		// throw new EpException(MessageConst.ERROR_770020);
		// }

		String orgType = this.getManagedBean().getSelectedUserOrg()
				.getPmOrgProfile().getOrgTypeCode().getParameterCode();

		// Check unique org role
		// List<String> uniqueRoles = new ArrayList<String>(
		// Arrays.asList(ProfileManagementConst.UNIQUE_ORG_ROLES));
		// if (uniqueRoles.contains(this.getManagedBean().getSelectedRole()
		// .getPmRole().getRoleCode())) {
		// if (checkUniqueRole(orgType, this.getManagedBean()
		// .getSelectedRole().getPmRole().getRoleCode(), false)) {
		// throw new EpException(MessageConst.ERROR_770106,
		// new String[] { this.getManagedBean().getSelectedRole()
		// .getRoleName() });
		// }
		// }

		// Check single role for all users
		if (this.getManagedBean().getSelectedRole().getPmRole().isSingleRole()) {
			if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
					&& !this.getManagedBean().getSelectedUserOrg()
							.getPmUserRoles().isEmpty()) {
				for (PmUserRole pmUserRole : this.getManagedBean()
						.getSelectedUserOrg().getPmUserRoles()) {
					if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						throw new EpException(MessageConst.ERROR_770134);
					}
				}
			}
		} else {
			if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
					&& !this.getManagedBean().getSelectedUserOrg()
							.getPmUserRoles().isEmpty()) {
				for (PmUserRole pmUserRole : this.getManagedBean()
						.getSelectedUserOrg().getPmUserRoles()) {

					if (pmUserRole.getPmRole().isSingleRole()
							&& (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)) {
						throw new EpException(MessageConst.ERROR_770134);
					}
				}
			}
		}

		if (roleRestrictionValidation(this.getManagedBean().getSelectedRole()
				.getPmRole())) {
			throw new EpException(MessageConst.ERROR_770135,
					new String[] { this.getManagedBean().getSelectedRole()
							.getRoleName() });
		}

	}

	public boolean roleRestrictionValidation(PmRole pmRole) {
		// Check role conflict/restriction
		List<PmUserRole> activeRoles = new ArrayList<PmUserRole>();
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {
			for (PmUserRole userRole : this.getManagedBean()
					.getSelectedUserOrg().getPmUserRoles()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == userRole
						.getRecordStatusFlg()
						&& !userRole.getPmRole().getRoleCode()
								.equals(pmRole.getRoleCode())) {
					activeRoles.add(userRole);
				}
			}
		}

		boolean roleRestictionError = false;
		if (pmRole.isRestrRole()) {
			if (activeRoles != null && !activeRoles.isEmpty()) {
				if (PmCommonUtil.checkRoleRestriction(pmRole.getRoleCode(),
						activeRoles)) {
					roleRestictionError = true;

				}
			}
		}
		return roleRestictionError;
	}

	/**
	 * update ui receiving address tab if selected roles contain
	 * "Receiving Role"
	 */
	private void updateReceivingAddressFlag() {
		logger.info("updateReceivingAddressFlag");
		boolean isReceivingOfficer = false;
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles()
						.isEmpty()) {
			for (PmUserRole userRole : this.getManagedBean()
					.getSelectedUserOrg().getPmUserRoles()) {
				if (ProfileManagementConst.RECEIVING_OFFICER.equals(userRole
						.getPmRole().getRoleCode())) {
					this.getManagedBean().setReceivingAddressFlag(true);
					isReceivingOfficer = true;
					break;
				}
			}			
			if(!isReceivingOfficer){
				if(getManagedBean().getSelectedReceivingAddrTypes() != null &&
						!getManagedBean().getSelectedReceivingAddrTypes().isEmpty()){
					List<PmAddressType> removeNewAddressTypes = new ArrayList<PmAddressType>();
					for(PmAddressType addType : getManagedBean().getSelectedReceivingAddrTypes()){
						if(addType.isBrandNew()){
							removeNewAddressTypes.add(addType);
						}
					}
					if(!removeNewAddressTypes.isEmpty()){
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
				this.getManagedBean().getSelectedUserOrg()
						.setPmUserRoles(new ArrayList<PmUserRole>());
			}
			// if(this.getManagedBean().getDeleteUserRoles().size() ==
			// this.getManagedBean().getSelectedUserOrg().getPmUserRoles().size()){
			// throw new EpException(MessageConst.ERROR_770022);
			// }
			for (PmUserRole userRole : this.getManagedBean()
					.getDeleteUserRoles()) {
				if (userRole.isBrandNew()) {
					this.getManagedBean().getSelectedUserOrg().getPmUserRoles()
							.remove(userRole);
				}
			}
			this.getManagedBean().getDeleteUserRoles().clear();
			updateReceivingAddressFlag();
		}

	}

	/**
	 * Remove check box tick from data table
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void unSelectRole(AjaxBehaviorEvent e) {
		if (this.getManagedBean().getDeleteUserRoles() != null
				&& !this.getManagedBean().getDeleteUserRoles().isEmpty()) {
			Iterator<PmUserRole> iterator = getManagedBean()
					.getDeleteUserRoles().iterator();
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
			for (PmUserRole userRole : this.getManagedBean()
					.getDeleteUserRoles()) {
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
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles()
						.isEmpty()) {
			List<PmRole> oriPmRoles = new ArrayList<PmRole>();

			for (PmUserRole obj : this.getManagedBean().getSelectedUserOrg()
					.getPmUserRoles()) {
				if (obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					oriPmRoles.add(obj.getPmRole());
				}
			}

			if (oriPmRoles.contains(this.getManagedBean().getSelectedRole()
					.getPmRole())) {
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
				if (isAnyPendingTaskForUserRole(pmUserRole, this
						.getManagedBean().getSelectedUser().getLoginId())) {
					// revert it to active
					pmUserRole
							.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					// hit error and exit
					throw new EpException(MessageConst.ERROR_770168);
				} else {

					if (!pmUserRole.isModified()) {
						pmUserRole.setModified(true);
					} else {
						pmUserRole.setModified(false);
					}
				}
				
				
				if(getManagedBean().getSelectedReceivingAddrTypes() != null &&
						!getManagedBean().getSelectedReceivingAddrTypes().isEmpty()){
					List<PmAddressType> removeNewAddressTypes = new ArrayList<PmAddressType>();
					for(PmAddressType addType : getManagedBean().getSelectedReceivingAddrTypes()){
						if(addType.isBrandNew()){
							removeNewAddressTypes.add(addType);
						}else{
							if(DbValueConst.RECORD_STATUS_ACTIVE == addType.getRecordStatusFlg()){
								addType.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
							}
						}
					}
					if(!removeNewAddressTypes.isEmpty()){
						getManagedBean().getSelectedReceivingAddrTypes().removeAll(removeNewAddressTypes);
					}					
				}
			} else if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				if (!pmUserRole.isModified()) {
					pmUserRole.setModified(true);
				} else {
					pmUserRole.setModified(false);
				}
			}

			

		// check for role restriction
		if (DbValueConst.RECORD_STATUS_ACTIVE == pmUserRole
				.getRecordStatusFlg()) {
			if (roleRestrictionValidation(pmUserRole.getPmRole())) {
				pmUserRole
						.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				throw new EpException(MessageConst.ERROR_770135,
						new String[] { PmCommonUtil
								.getRoleDescByRoleCode(pmUserRole.getPmRole()
										.getRoleCode()) });

			}
		}
		}
		getManagedBean().setRoleTabActiveIndex(0);

	}

	private boolean isAnyPendingTaskForAdressType(PmAddressType addressType,
			String loginId) {
		logger.info("isAnyPendingTaskForAdressType");
		boolean anyPendingTask = false;
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(43000L);
		statusIds.add(43004L);

		List<Integer> intStatusIds = new ArrayList<Integer>();
		intStatusIds.add(43000);
		intStatusIds.add(43004);

		IFulfilmentSO fulfilmentSO = null;
		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		String token = null;
		TaskAssignmentSearchBean taskSearchBean = null;
		SearchResultBean<WorklistItemVO> resultBean = null;

		try {

			fulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,
							IFulfilmentSO.class);
			List<String> noteNumberList = fulfilmentSO
					.getNumberOfFRNByAddressAndStatus(addressType
							.getPmAddress().getAddressId(), statusIds);
			//System.out.println("noteNumberList******" + noteNumberList.size());
			if (noteNumberList != null && noteNumberList.size() > 0
					&& loginId != null && loginId.length() > 0) {
				// token = getFacesScopes().getBPMAuthenticationCtxToken();

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				// token = bpmSO.authenticate("weblogic","welcome1"); //TODO

				reqDTO = new BpmRequestDTO();

				// logger.info("BPM loginId******" + loginId);
				System.out.println("BPM statusIds******" + intStatusIds.size());

				// if(token==null){
				// throw new EpException(MessageConst.ERROR_770099);
				// }
				// logger.info("BPM auth******" + token);

				// set useful info
				// reqDTO.setCtxToken(token);
				reqDTO.setStatusList(intStatusIds);
				reqDTO.setUserId(loginId);
				for (String docNum : noteNumberList) {
					System.out.println("docNum******" + docNum);
					reqDTO.getDocNumberList().add(docNum);
				}
				// if(getManagedBean().getTaskAssignmentSearchBean().getDocType()
				// !=null &&
				// getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter()
				// !=null ){
				// reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter().getParameterCode());
				// }
				//

				// Invoke BPM
				responseDTO = bpmSO.getReassignedItems(reqDTO);

				if (responseDTO.getWorklistVO() != null) {
					logger.info("BPM size******"
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					logger.info("BPM total******"
							+ responseDTO.getWorklistVO().getTotal());
					if (responseDTO.getWorklistVO().getTotal() > 0) {
						anyPendingTask = true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bpmSO = null;
		}

		return anyPendingTask;
	}

	private boolean isAnyPendingTaskForUserRole(PmUserRole pmUserRole,
			String loginId) {
		logger.info("isAnyPendingTaskForUserRole");
		boolean anyPendingTask = false;
		List<Integer> statusIds = retrieveAvailableStatusIdForRole(pmUserRole);
		if (statusIds != null && !statusIds.isEmpty()) {

			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String token = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;

			try {

				if (loginId != null && loginId.length() > 0) {
					// token = getFacesScopes().getBPMAuthenticationCtxToken();

					bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
							.getServiceObject(IBpmSO.class);
					// token = bpmSO.authenticate("weblogic","welcome1"); //TODO

					reqDTO = new BpmRequestDTO();

					logger.info("BPM loginId******" + loginId);
					System.out
							.println("BPM statusIds******" + statusIds.size());

					// if(token==null){
					// throw new EpException(MessageConst.ERROR_770099);
					// }
					// logger.info("BPM auth******" + token);

					// set useful info
					// reqDTO.setCtxToken(token);
					reqDTO.setStatusList(statusIds);
					reqDTO.setUserId(loginId);
					// reqDTO.setDocNumber(getManagedBean().getTaskAssignmentSearchBean().getDocNo());

					// if(getManagedBean().getTaskAssignmentSearchBean().getDocType()
					// !=null &&
					// getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter()
					// !=null ){
					// reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter().getParameterCode());
					// }
					//

					// Invoke BPM
					responseDTO = bpmSO.getReassignedItems(reqDTO);

					if (responseDTO.getWorklistVO() != null) {
						logger.info("BPM size******"
								+ responseDTO.getWorklistVO().getWorklistItem()
										.size());
						logger.info("BPM total******"
								+ responseDTO.getWorklistVO().getTotal());
						if (responseDTO.getWorklistVO().getTotal() > 0) {
							anyPendingTask = true;
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
		}
		return anyPendingTask;
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

				IStatusSO statusSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStatusSO.class);
				StatusResDTO resDTO = statusSO
						.retrieveStatusRoleListByRoleCode(reqDTO);
				if (resDTO.isSuccessful()) {
					for (int i = 0; i < resDTO.getStatusRoleList().size(); i++) {
						statusIds.add(resDTO.getStatusRoleList().get(i)
								.getPmStatus().getStatusId().intValue());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770094,
					new String[] { e.getMessage() });
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
					&& this.getManagedBean().getSelectedUser()
							.getPmAddressTypes() != null) {
				if (this.getManagedBean().getSelectedReceivingAddrTypes() == null) {
					this.getManagedBean().setSelectedReceivingAddrTypes(
							new ArrayList<PmAddressType>());
				}
				this.getManagedBean().getSelectedReceivingAddrTypes().clear();
				for (PmAddressType addressTypeObj : this.getManagedBean()
						.getSelectedUser().getPmAddressTypes()) {
					if (ProfileManagementConst.ADDRESS_RECEIVING
							.equalsIgnoreCase(addressTypeObj.getAddressType())
							&& (addressTypeObj.getPmOrgProfile() != null && this
									.getManagedBean().getSelectedUserOrg()
									.getPmOrgProfile().getOrgProfileId().equals(addressTypeObj
									.getPmOrgProfile().getOrgProfileId()))) {
						this.getManagedBean().getSelectedReceivingAddrTypes()
								.add(addressTypeObj);
					}
				}
				updateReceivingAddressFlag();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770023,
					new String[] { e.getMessage() });
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
				this.getManagedBean().getSelectedUser()
						.setPmAddressTypes(new ArrayList<PmAddressType>());
			}
			if (isUniqueAddress()) {
				System.out.println("selected size " + deliveringAddrs.size());
				for (int i = 0; i < deliveringAddrs.size(); i++) {
					PmAddressType pmAddressType = (PmAddressType) deliveringAddrs
							.get(i);
					PmAddressType tempAddTypeObj = new PmAddressType();
					tempAddTypeObj
							.setAddressType(ProfileManagementConst.ADDRESS_RECEIVING);
					tempAddTypeObj.setPmAddress(pmAddressType.getPmAddress());
					tempAddTypeObj.setPmUser(this.getManagedBean()
							.getSelectedUser());
					tempAddTypeObj.setPmOrgProfile(this.getManagedBean()
							.getSelectedUserOrg().getPmOrgProfile());
					this.getManagedBean().getSelectedUser()
							.addPmAddressType(tempAddTypeObj);
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
				&& !this.getManagedBean().getDeleteReceivingAddrTypes()
						.isEmpty()) {
			if (this.getManagedBean().getSelectedReceivingAddrTypes() == null) {
				this.getManagedBean().setSelectedReceivingAddrTypes(
						new ArrayList<PmAddressType>());
			}
			for (PmAddressType pmAddressType : this.getManagedBean()
					.getDeleteReceivingAddrTypes()) {
				this.getManagedBean().getSelectedUser().getPmAddressTypes()
						.remove(pmAddressType);
				// this.getManagedBean().getSelectedReceivingAddrTypes().remove(pmAddressType);
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
				&& !this.getManagedBean().getSelectedDeliveringAddrTypes()
						.isEmpty()) {
			if (this.getManagedBean().getSelectedReceivingAddrTypes() != null
					&& !this.getManagedBean().getSelectedReceivingAddrTypes()
							.isEmpty()) {
				List<Long> deliveringAddr = new ArrayList<Long>();

				for (PmAddressType pmAddressType : this.getManagedBean()
						.getSelectedDeliveringAddrTypes()) {
					deliveringAddr.add(pmAddressType.getPmAddress()
							.getAddressId());
				}

				for (PmAddressType pmAddressType : this.getManagedBean()
						.getSelectedReceivingAddrTypes()) {
					if (deliveringAddr.contains(pmAddressType.getPmAddress()
							.getAddressId())) {
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
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		logger.info("handleFileUpload - START");
		try {
			if (event != null) {
				if (this.getManagedBean().getSelectedUser()
						.getPmUserAttachments() == null) {
					this.getManagedBean()
							.getSelectedUser()
							.setPmUserAttachments(
									new ArrayList<PmUserAttachment>());
				}
				if (this.getManagedBean().getFileEventMapper() == null) {
					this.getManagedBean().setFileEventMapper(
							new HashMap<PmUserAttachment, FileUploadEvent>());
				}
				String loginId = this.getManagedBean().getSelectedUser()
						.getLoginId();
				UploadedFile item = event.getFile();
				PmUserAttachment attachment = new PmUserAttachment();
				attachment.setFileName(item.getFileName());
				attachment.setFilePath(PmCommonUtil.getUploadPath(loginId,
						item.getFileName()));
				attachment.setCreatedBy(getFacesScopes().getUserSessionBean()
						.getUserId());
				attachment
						.setCreatedUser(getFacesScopes().getUserSessionBean());
				attachment.setCreatedDate(new Timestamp(System
						.currentTimeMillis()));

				this.getManagedBean().getSelectedUser()
						.addPmUserAttachments(attachment);
				this.getManagedBean().getFileEventMapper()
						.put(attachment, event);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060,
					new String[] { e.getMessage() });

		}
		logger.info("handleFileUpload - END");
	}

	/**
	 * Delete attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachment(PmUserAttachment attachment)
			throws FileNotFoundException, IOException {
		logger.info("deleteAttachment - START");
		try {
			if (attachment != null) {
				if (this.getManagedBean().getDeletePmUserAttachments() == null) {
					this.getManagedBean().setDeletePmUserAttachments(
							new ArrayList<PmUserAttachment>());
				}
				if (!attachment.isBrandNew()) {
					this.getManagedBean().getDeletePmUserAttachments()
							.add(attachment);
				}
				this.getManagedBean().getSelectedUser().getPmUserAttachments()
						.remove(attachment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054,
					new String[] { e.getMessage() });

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
	public void downloadAttachment(PmUserAttachment attachment)
			throws FileNotFoundException, IOException {
		logger.info("downloadAttachment - START");
		try {
			if (attachment != null) {
				downloadFile(attachment.getFilePath(), attachment.getFileName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770055,
					new String[] { e.getMessage() });

		}
		logger.info("downloadAttachment - END");
	}

	/**
	 * Download attachment from server
	 * 
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	private void downloadFile(String filePath, String fileName)
			throws IOException {
		logger.info("Inside downloadFile start");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);
		res.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");//
		res.setHeader("Content-Transfer-Encoding", "binary");
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		if (filePath != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096];
			}
			input.close();
		}
		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
		logger.info("Inside downloadFile end");
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

	private ISearchBean<PmRoleDesc> getRoleBeanData(
			ISearchBean<PmRoleDesc> searchBean,Long currentOrgTypeId) {
		logger.info("getRoleBeanData");
		
		logger.info("currentOrgTypeId " + currentOrgTypeId);
		List<String> selectedRolesToExclude = new ArrayList<String>();
		if (currentOrgTypeId != null) {			
//			SearchFilterBean orgBean = new SearchFilterBean(
//					SearchOperator.OPERATOR_EQUALS.value());
//			orgBean.setSearchField("pmRole.pmOrgRoles.orgType.parameterId");
//			orgBean.setSearchValue(currentOrgTypeId);
//			searchBean.getDataFilterBeans().add(orgBean);
//
			SearchFilterBean statusBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			statusBean.setSearchField("pmRole.recordStatusFlg");
			statusBean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(statusBean);

//			// remove parent role as children were selected
//			SearchFilterBean approverBean = new SearchFilterBean(
//					SearchOperator.OPERATOR_NOT_EQUALS.value());
//			approverBean.setSearchField("pmRole.roleCode");
//			approverBean
//				.setSearchValue(ProfileManagementConst.ROLE_APPROVER);
//			searchBean.getDataFilterBeans().add(approverBean);

			// remove selected roles (if any)
			if (getManagedBean().getSelectedUserOrg() != null
					&& getManagedBean().getSelectedUserOrg()
							.getPmUserRoles() != null) {

				for (PmUserRole obj : getManagedBean().getSelectedUserOrg()
						.getPmUserRoles()) {
					if (obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							|| obj.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						selectedRolesToExclude.add(obj.getPmRole()
									.getRoleCode());
					}
				}
			}
		} else {
			SearchFilterBean statusBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			statusBean.setSearchField("pmRole.recordStatusFlg");
			statusBean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(statusBean);
		}
//		// remove admin user role from list if current user had the role
//
//		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
//			selectedRolesToExclude.add(RoleCode.MINISTRY_ADMIN.value());
//		} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
//			selectedRolesToExclude.add(RoleCode.PTJ_ADMIN.value());
//		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
//			selectedRolesToExclude.add(RoleCode.JABATAN_ADMIN.value());
//		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
//			selectedRolesToExclude.add(RoleCode.BPK_ADMIN.value());
//		} else if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
//			selectedRolesToExclude.add(RoleCode.SUPPORT_ADMIN.value());
//		}
		if (!selectedRolesToExclude.isEmpty()) {
			logger.info("remove selected roles");
			SearchFilterBean selectedRole = new SearchFilterBean(
					SearchOperator.OPERATOR_NOT_IN.value());
			selectedRole.setSearchField("pmRole.roleCode");
			selectedRole.setSearchValue(selectedRolesToExclude);
			searchBean.getDataFilterBeans().add(selectedRole);
		}
		
		
		return searchBean;
	}

	/**
	 * search user roles
	 */
	public void getAvailbleUserRole() {
		logger.info("getAvailbleUserRole");
		roleDataModel.reloadData();
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
		public ISearchResultBean<PmRoleDesc> pageChangeListener(
				ISearchBean<PmRoleDesc> searchBean) {
			if (logger.isDebugEnabled()) {
				logger.info("pageChangeListener");
			}
			
			IRoleSO roleSO = null;
			
			try {
				// boolean isApprover = false;
				Long currentOrgTypeId = getCurrentOrgTypeId();
				ISearchBean<PmRoleDesc> searchBeanData = getRoleBeanData(
						searchBean,currentOrgTypeId);
				RoleReqDTO roleReqDTO = new RoleReqDTO();
				roleReqDTO.setLanguage(getFacesScopes().getUserLocale()
						.getLanguage());
				roleReqDTO.setRoleCode(getRoleCode());
				roleReqDTO.setOrgTypeId(currentOrgTypeId);
				roleReqDTO.setBpk(getManagedBean().getSelectedUserOrg().getPmOrgProfile().getBpk() != null 
						? getManagedBean().getSelectedUserOrg().getPmOrgProfile().getBpk() : false);
				// roleReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				logger.info("..getFacesScopes().getUserSessionBean().getUserId()="
								+ getFacesScopes().getUserSessionBean()
										.getUserId());

				roleReqDTO.setSearchRoleBean(searchBeanData);
				roleSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IRoleSO.class);
				RoleResDTO resDto = roleSO
						.retrieveRoleDecsFromRoleMatrix(roleReqDTO);

				if (resDto != null && resDto.isSuccessful()) {
					/*
					 * List <PmRoleDesc> roleResults =
					 * resDto.getSearchRoleResultBean().getResultList();
					 * for(PmRoleDesc roleDesc: roleResults){
					 * if(ProfileManagementConst
					 * .ROLE_APPROVER.equals(roleDesc.getPmRole
					 * ().getRoleCode())){ isApprover = true; break; } }
					 * if(isApprover){ searchBeanData =
					 * getRoleBeanData(searchBean,
					 * ProfileManagementConst.ROLE_APPROVER); resDto =
					 * roleSO.retrieveRoleDecsBySearchBean(roleReqDTO);
					 * System.out
					 * .println("with parent "+resDto.getSearchRoleResultBean
					 * ().getTotalRecord()); }
					 */
					// getManagedBean().setAvailableRoles(resDto.getSearchRoleResultBean().getResultList());

					// disabled selected roles
					// markupRoleResult(resDto.getSearchRoleResultBean());

					return resDto.getSearchRoleResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				roleSO = null;
			}
			return null;
		}
	}

	private void markupRoleResult(
			ISearchResultBean<PmRoleDesc> searchRoleResultBean) {
		logger.info("markupRoleResult");
		// disabled selected roles
		if (searchRoleResultBean != null
				&& searchRoleResultBean.getResultList() != null
				&& !searchRoleResultBean.getResultList().isEmpty()) {
			if (getManagedBean().getSelectedUserOrg() != null
					&& getManagedBean().getSelectedUserOrg().getPmUserRoles() != null) {
				List<PmRole> selectedRoles = new ArrayList<PmRole>();
				// selected roles
				for (PmUserRole obj : getManagedBean().getSelectedUserOrg()
						.getPmUserRoles()) {
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
//		if (this.getManagedBean().getUserProfileSearchBean()
//				.getSelectedOrgProfileId() == null) {
//			// hit error and return
//			throw new EpException(MessageConst.ERROR_775116, new String[] {});
//
//		}
		
		if (getManagedBean().getUserProfileSearchBean().getSelectedOrgProfileId() == null
				&& getManagedBean().getSelectedOrgType().getParameterId() == null
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getIdentificationNo())
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getUserName())
				&& StringUtils.isBlank(getManagedBean().getUserProfileSearchBean().getUserRole())
				&& getManagedBean().getUserProfileSearchBean().getUserOrgStatus() == null) {			
			throw new EpException(MessageConst.ERROR_770049);
		}

		logger.info("searchUserProfile");
		getManagedBean().setLazy(true);
		userOrgDataModel.reloadData();
	}

	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmUser>
	 */
	private ISearchBean<PmUserOrg> getSearchBeanData(
			ISearchBean<PmUserOrg> searchBean) {
		logger.info("getSearchBeanData - Start");
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		UserProfilesSearchBean upSearchBean = this.getManagedBean()
				.getUserProfileSearchBean();
		// if (!getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
		// if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
		// searchBean
		// .getDataFilterBeans()
		// .add(new SearchMultiFilterBean(
		// new SearchFilterBean(
		// "pmUserOrgs.pmOrgProfile.orgProfileId",
		// SearchOperator.OPERATOR_EQUALS.value(),
		// this.getManagedBean()
		// .getCurrentActiveOrg()
		// .getOrgProfileId()),
		// new SearchFilterBean(
		// "pmUserOrgs.pmOrgProfile.parentOrgProfile.orgProfileId",
		// SearchOperator.OPERATOR_EQUALS.value(),
		// this.getManagedBean()
		// .getCurrentActiveOrg()
		// .getOrgProfileId(),
		// SearchCondition.CONDITION_OR.value())));
		// // }else
		// // if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
		// // SearchFilterBean orgSearchBean = new SearchFilterBean(
		// // SearchOperator.OPERATOR_EQUALS.value());
		// //
		// orgSearchBean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
		// //
		// orgSearchBean.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// // searchBean.getDataFilterBeans().add(orgSearchBean);
		// // }else
		// // if(getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())
		// // ||
		// // getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())){
		// // SearchFilterBean orgSearchBean = new SearchFilterBean(
		// // SearchOperator.OPERATOR_EQUALS.value());
		// //
		// orgSearchBean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
		// //
		// orgSearchBean.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// // searchBean.getDataFilterBeans().add(orgSearchBean);
		// } else {
		// SearchFilterBean orgSearchBean = new SearchFilterBean(
		// SearchOperator.OPERATOR_EQUALS.value());
		// orgSearchBean
		// .setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
		// orgSearchBean.setSearchValue(this.getManagedBean()
		// .getCurrentActiveOrg().getOrgProfileId());
		// searchBean.getDataFilterBeans().add(orgSearchBean);
		// }
		// }

//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("pmOrgProfile.orgTypeCode.parameterCode",
//						SearchOperator.OPERATOR_NOT_EQUALS.value(),
//						ParameterConst.ORG_TYPE_SUPPLIER));
		if (upSearchBean.getSelectedOrgProfileId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							upSearchBean.getSelectedOrgProfileId()));
			isEmpty = false;
		} 
		
		if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmOrgProfile.orgTypeCode.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(), this
									.getManagedBean().getSelectedOrgType()
									.getParameterId()));			
			isEmpty = false;
		}else{
			if(getManagedBean().getSearchOrgCategory() != null){
				SelectItem[] items =  getOrgTypeDropdownValues();
				List<String> orgTypeIds = new ArrayList<>();
				for(SelectItem item: items){
					if(item.getValue() != null){
						orgTypeIds.add(item.getValue().toString());
					}
				}
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_IN.value(), orgTypeIds));		
			}
		}
		
//		else {
//			throw new EpException(MessageConst.ERROR_770163);
//		}

		// Login Id
		// if (upSearchBean.getLoginId() != null
		// && !"".equalsIgnoreCase(upSearchBean.getLoginId())) {
		// logger.info("+ loginId " + upSearchBean.getLoginId());
		// SearchFilterBean userSearchBean = new SearchFilterBean(
		// SearchOperator.OPERATOR_LIKE.value());
		// userSearchBean.setCaseInsensitive(true);
		// userSearchBean.setSearchField("loginId");
		// userSearchBean.setSearchValue(upSearchBean.getLoginId());
		// searchBean.getDataFilterBeans().add(userSearchBean);
		// isEmpty = false;
		// }
		// IdentificationNO
		if (upSearchBean.getIdentificationNo() != null
				&& !"".equalsIgnoreCase(upSearchBean.getIdentificationNo())) {
			logger.info("+ identificationNo "
					+ upSearchBean.getIdentificationNo());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("pmUser.identificationNo");
			userSearchBean.setSearchValue(upSearchBean.getIdentificationNo());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		// User Name
		if (upSearchBean.getUserName() != null
				&& !"".equalsIgnoreCase(upSearchBean.getUserName())) {
			logger.info("+ userName " + upSearchBean.getUserName());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("pmUser.userName");
			userSearchBean.setSearchValue(upSearchBean.getUserName());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		// Org Name
		// if (upSearchBean.getUserOrganizationName() != null
		// && !"".equalsIgnoreCase(upSearchBean.getUserOrganizationName())) {
		// logger.info("+ userOrganizationName "
		// + upSearchBean.getUserOrganizationName());
		// SearchFilterBean userSearchBean = new SearchFilterBean(
		// SearchOperator.OPERATOR_LIKE.value());
		// userSearchBean.setCaseInsensitive(true);
		// userSearchBean
		// .setSearchField("pmUserOrgs.pmOrgProfile.pmOrgValidity.orgName");
		// userSearchBean.setSearchValue(upSearchBean
		// .getUserOrganizationName());
		// searchBean.getDataFilterBeans().add(userSearchBean);
		// isEmpty = false;
		// }

		// Role
		if (upSearchBean.getUserRole() != null
				&& !"".equalsIgnoreCase(upSearchBean.getUserRole())) {
			logger.info("+ userRole " + upSearchBean.getUserRole());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean
					.setSearchField("pmUserRoles.pmRole.roleCode");
			userSearchBean.setSearchValue(upSearchBean.getUserRole());
			searchBean.getDataFilterBeans().add(userSearchBean);
			
			//Role Status
			if (upSearchBean.getUserRoleStatus() != null) {
				logger.info("+ userRole Status " + upSearchBean.getUserRoleStatus());
				userSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				userSearchBean
						.setSearchField("pmUserRoles.recordStatusFlg");
				userSearchBean.setSearchValue(upSearchBean.getUserRoleStatus());
				searchBean.getDataFilterBeans().add(userSearchBean);
				isEmpty = false;
			}

		}
		
		//EP_ADMIN cannot be listed/search in user
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"pmUserRoles.pmRole.roleCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.EP_ADMIN.value()));
		
		// EP_ADMIN_2 cannot see EP_ADMIN_1 or EP_ADMIN_2
		if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value()) || getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_IN.value(), 
							new ArrayList<String>(Arrays
									.asList(RoleCode.EP_ADMIN_1.value(),
											RoleCode.EP_ADMIN_2.value()))));
		}
		
		// MINISTRY_ADMIN cannot see MINISTRY_ADMIN
		if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.MINISTRY_ADMIN.value()));
		}

		// PTJ_ADMIN cannot see PTJ_ADMIN
		if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.PTJ_ADMIN.value()));
		}
		
		
		// BPK_ADMIN cannot see BPK_ADMIN
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.BPK_ADMIN.value()));
		}
		
		// UPS_ADMIN cannot see UPS_ADMIN
		if(getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.UPS_ADMIN.value()));
		}
		
		// SUPPORT_ADMIN cannot see SUPPORT_ADMIN
		if(getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUserRoles.pmRole.roleCode",
							SearchOperator.OPERATOR_NOT_EQUALS.value(), RoleCode.SUPPORT_ADMIN.value()));
		}
		
		
		// Status if non provided then select default (active)
		if (upSearchBean.getUserOrgStatus() != null) {
			logger.info("+ status " + upSearchBean.getUserOrgStatus());
			SearchFilterBean userSearchBean = new SearchFilterBean();
			userSearchBean.setSearchField("recordStatusFlg");
			userSearchBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
					.value());
			userSearchBean.setSearchValue(upSearchBean.getUserOrgStatus());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}else{
			logger.info("+ status 1 and 0");
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), 
							new ArrayList<Integer>(Arrays
									.asList(DbValueConst.RECORD_STATUS_ACTIVE,
											DbValueConst.RECORD_STATUS_SUSPENDED))));
		}
		
		//Not Equals to Login User
		Long loginSessionUserId = getFacesScopes().getUserSessionBean()
				.getUserId();
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"pmUser.userId",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), loginSessionUserId));
		//
		// logger.info("+ status try only " + null);
		// // base on the flag, default list is either select all or empty
		// if (isEmpty && !isSearchable && upSearchBean.getUserOrgStatus() ==
		// null) {
		// logger.info("+ status try only " + null);
		// logger.info("+ status " + null);
		// SearchFilterBean bean = new SearchFilterBean();
		// bean.setSearchField("recordStatusFlg");
		// bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		// bean.setSearchValue(null);
		// searchBean.getDataFilterBeans().add(bean);
		// }
		
		
		// Authorisation	
		if(StringUtils.isNotBlank(getRoleCode())){
			if(upSearchBean.getSelectedOrgProfileId() == null && getManagedBean().getSelectedOrgType() != null 
					&& StringUtils.isNotBlank(getManagedBean().getSelectedOrgType().getParameterCode())){
					if (this.getManagedBean().getCurrentActiveOrg() != null){
					OrgProfileUtil.retrieveOrgProfileAuthorisationSearchBean(
							getManagedBean().getSelectedOrgType().getParameterCode(),
								getManagedBean().getCurrentActiveOrg(),
								getRoleCode(),
								searchBean);
					}
					
			}else if(upSearchBean.getSelectedOrgProfileId() == null && (getManagedBean().getSelectedOrgType() == null 
					|| StringUtils.isBlank(getManagedBean().getSelectedOrgType().getParameterCode()))){
				if (this.getManagedBean().getCurrentActiveOrg() != null){
					OrgProfileUtil.retrieveOrgProfileAuthorisationSearchBean(
							getManagedBean().getSelectedOrgType().getParameterCode(),
								getManagedBean().getCurrentActiveOrg(),
								getRoleCode(),
								searchBean);
				}
			}
		}else{
			if ((getManagedBean().getSelectedOrgType() == null 
					|| StringUtils.isBlank(getManagedBean().getSelectedOrgType().getParameterCode())) 
					&& this.getManagedBean().getCurrentActiveOrg() != null){
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(),
								getManagedBean().getCurrentActiveOrg().getOrgTypeCode().getParameterId()));	
			}
		}
		
		SearchOrderBean orderBean = null;
		if(getManagedBean().getSelectedOrgType() !=null && getManagedBean().getSelectedOrgType().getParameterCode() !=null){
			if(OrganizationType.SUPPLIER.value().equals(getManagedBean().getSelectedOrgType().getParameterCode())){
				orderBean = new SearchOrderBean("pmOrgProfile.smSupplier.companyName", true);
			}else if(OrganizationType.FACTORING_COMPANY.value().equals(getManagedBean().getSelectedOrgType().getParameterCode())){
				orderBean = new SearchOrderBean("pmOrgProfile.pmFinancialOrg.finOrgName", true);
			}else{
				Date currentDate = new Date();
				// Filter By -  effDate
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmOrgProfile.pmOrgValidity.effDate",
								SearchOperator.OPERATOR_LESS_EQUAL.value(),
								currentDate));

				// Filter By -  expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"pmOrgProfile.pmOrgValidity.expDate",
								SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
								"pmOrgProfile.pmOrgValidity.expDate",
								SearchOperator.OPERATOR_IS_NULL.value(), null,
								SearchCondition.CONDITION_OR.value())));
				orderBean = new SearchOrderBean("pmOrgProfile.pmOrgValidity.orgName", true);
			}
		}
		
		if(orderBean != null){
			searchBean.getSortColumns().add(orderBean);
		}
		searchBean.getSortColumns().add(new SearchOrderBean("pmUser.userName",true));
		
		
		logger.info("getSearchBeanData - End");
		return searchBean;
	}

	public IPagingDataModel<PmUserOrg> getUserOrgDataModel() {
		return userOrgDataModel;
	}

	public void setUserOrgDataModel(IPagingDataModel<PmUserOrg> userOrgDataModel) {
		this.userOrgDataModel = userOrgDataModel;
	}

	// pagination for user search list
	private class UserProfilePagingDataModel extends PagingDataModel<PmUserOrg> {
		private static final long serialVersionUID = 1L;

		public UserProfilePagingDataModel() {
			super("dtUserResult", getManagedBean().isLazy());
		}

		@Override
		protected ISearchResultBean<PmUserOrg> pageChangeListener(
				ISearchBean<PmUserOrg> searchBean) {
			IUserSO userSO = null;
			try {
				ISearchBean<PmUserOrg> searchBeanData = getSearchBeanData(searchBean);
				UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
				userOrgReqDTO.setUserOrgSearchBean(searchBeanData);
				userSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				UserOrgResDTO resDto = userSO
						.searchUserAndUserOrgProfile(userOrgReqDTO);
				if (resDto != null && resDto.isSuccessful()) {
					markupUserResult(resDto.getUserOrgResultBean());
					return resDto.getUserOrgResultBean();
				}
			}finally{
				userSO = null;
			}
			return null;
		}

	}

	private void markupUserResult(ISearchResultBean<PmUserOrg> userResultBean) {
		logger.info("markupUserResult");
		if (userResultBean != null && userResultBean.getResultList() != null
				&& !userResultBean.getResultList().isEmpty()) {

			for (PmUserOrg pmUserOrg : userResultBean.getResultList()) {
				List<PmAddressType> addressTypes = PmCommonUtil
						.retrieveAddressTypesByUserIdOrAddressType(pmUserOrg
								.getPmUser().getUserId(),
								ProfileManagementConst.ADDRESS_USER);
				pmUserOrg.getPmUser()
						.setAddressTypeC(
								(addressTypes != null && !addressTypes
										.isEmpty()) ? addressTypes.get(0)
										: new PmAddressType());
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
			if (user.getPmAddressTypes() != null
					&& !user.getPmAddressTypes().isEmpty()) {
				for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj
							.getRecordStatusFlg()) {
						if (ProfileManagementConst.ADDRESS_USER
								.equalsIgnoreCase(addressTypeObj
										.getAddressType())) {
							// update address
							if (isAddressEntered) {
								logger.info("update address");
								addressTypeObj.setPmAddress(this
										.getManagedBean().getUserAddress());
								if (addressTypeObj.getPmAddress()
										.getPmDivision() != null
										&& addressTypeObj.getPmAddress()
												.getPmDivision()
												.getDivisionId() == null) {
									addressTypeObj.getPmAddress()
											.setPmDivision(null);
								}
							} else {
								logger.info("update address (remove)");
								// remove address
								addressTypeObj
										.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
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
					pmAddressType
							.setAddressType(ProfileManagementConst.ADDRESS_USER);
					pmAddressType.setPmAddress(this.getManagedBean()
							.getUserAddress());
					if (pmAddressType.getPmAddress().getPmDivision() != null
							&& pmAddressType.getPmAddress().getPmDivision()
									.getDivisionId() == null) {
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
				pmAddressType
						.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(this.getManagedBean()
						.getUserAddress());
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision()
								.getDivisionId() == null) {
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
			List<PmAddressType> addressTypes = PmCommonUtil
					.retrieveAddressTypesByOrgId(orgProfileId,
							ProfileManagementConst.ADDRESS_BILLING);
			orgAddress = (addressTypes != null && !addressTypes.isEmpty()) ? addressTypes
					.get(0).getPmAddress() : null;
			logger.info(orgAddress != null ? "orgAddress exist: YES"
					: "orgAddress exist: NO");
		}

		if (!user.isBrandNew()) {
			boolean isUserAddressExist = false;
			// update contact address
			if (user.getPmAddressTypes() != null
					&& !user.getPmAddressTypes().isEmpty()) {
				for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
					if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj
							.getRecordStatusFlg()) {
						if (ProfileManagementConst.ADDRESS_USER
								.equalsIgnoreCase(addressTypeObj
										.getAddressType())) {
							// update address
							if (orgAddress != null) {
								logger.info("update address");
								// copy orgAddress and update address
								addressTypeObj.setPmAddress(orgAddress);
								if (addressTypeObj.getPmAddress()
										.getPmDivision() != null
										&& addressTypeObj.getPmAddress()
												.getPmDivision()
												.getDivisionId() == null) {
									addressTypeObj.getPmAddress()
											.setPmDivision(null);
								}
							} else {
								logger.info("update address (remove)");
								// remove address because org doesn't have an
								// address
								addressTypeObj
										.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
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
						pmAddressType
								.setAddressType(ProfileManagementConst.ADDRESS_USER);
						pmAddressType.setPmAddress(orgAddress);
						if (pmAddressType.getPmAddress().getPmDivision() != null
								&& pmAddressType.getPmAddress().getPmDivision()
										.getDivisionId() == null) {
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
				pmAddressType
						.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(orgAddress);
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision()
								.getDivisionId() == null) {
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
		if (userOrg != null
				&& userOrg.getPmOrgProfile().getOrgTypeCode() != null) {
			// for (PmUserOrg userOrg : user.getPmUserOrgs()) {
			// if (userOrg.getRecordStatusFlg() ==
			// DbValueConst.RECORD_STATUS_ACTIVE) {
			// if (checkEffDate(userOrg)) {
			user.setOrgTypeCode(userOrg.getPmOrgProfile().getOrgTypeCode());
			// break;
			// }
			// }
			// }
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
			System.out.println("......user.getPmUserOrgs()="
					+ user.getPmUserOrgs());
			System.out.println("......user.getPmUserOrgs().size()="
					+ user.getPmUserOrgs().size());
			for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
				if (pmUserOrg.isBrandNew()) {
					isChanged = true;
				}
				if (pmUserOrg.isBrandNew()
						|| pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					// if (checkEffDate(pmUserOrg)) {
					activeUserOrgId = pmUserOrg.getUserOrgId() != null ? pmUserOrg
							.getUserOrgId() : 0;
					for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
						if (pmUserRole.isBrandNew()
								|| (pmUserRole.isModified() && pmUserRole
										.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)) {
							newRoleList.add(pmUserRole);
							System.out.println("ADD NEW: "
									+ pmUserRole.getPmRole().getRoleCode());
						} else if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED
								&& pmUserRole.isModified()) {
							deleteRoleList.add(pmUserRole);
							System.out.println("DELETE OLD: "
									+ pmUserRole.getPmRole().getRoleCode());
						}
					}
					// }
				} else if (pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					// TODO : remove role.brandNew first!
					deleteRoleList.addAll(pmUserOrg.getPmUserRoles());
				}
			}
			if (activeUserOrgId > 0 || isChanged) {
				// retrieve previous active org list
				// List<PmUserOrg> pmUserOrgs = PmCommonUtil
				// .retrieveActiveOrg(user.getUserId());
				System.out.println("size " + user.getPmUserOrgs().size());
				for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
					if (pmUserOrg.getUserOrgId() != null
							&& pmUserOrg.getUserOrgId() != activeUserOrgId) {
						for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
							if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								deleteRoleList.add(pmUserRole);
								System.out.println("2 DELETE OLD: "
										+ pmUserRole.getPmRole().getRoleCode());
							}
						}
					}
				}
				reqDTO.setNewRoleList(newRoleList);
				reqDTO.setRemoveRoleList(deleteRoleList);
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
		} else if (pmUserOrg.getExpDateDisplay() != null
				&& pmUserOrg.getExpDateDisplay().compareTo(currentDate) < 0) {
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
						for (int index2 = index1 + 1; index2 < user
								.getPmUserOrgs().size(); index2++) {
							if (user.getPmUserOrgs().get(index2)
									.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								// Overlap exists if (StartA <= EndB) and (EndA
								// >= StartB)
								Date startA = user.getPmUserOrgs().get(index1)
										.getEffDate();
								Date endA = user.getPmUserOrgs().get(index1)
										.getExpDate();
								Date startB = user.getPmUserOrgs().get(index2)
										.getEffDate();
								Date endB = user.getPmUserOrgs().get(index2)
										.getExpDate();

								// not null
								if (endA == null) {
									endA = endLess;
								}
								if (endB == null) {
									endB = endLess;
								}

								boolean isCurrentPairOverlap = false;
								isCurrentPairOverlap = PmCommonUtil
										.dateOverlapping(startA, endA, startB,
												endB, false);

								if (isCurrentPairOverlap) {
									throw new EpException(
											MessageConst.ERROR_770027);
								}
							}
						}
					}
					// else {
					// // make sure it is currently active if only one item
					// if (!checkEffDate(user.getPmUserOrgs().get(index1))) {
					// throw new
					// EpException("Active Responsible Org. Unit is required.");
					// }
					// }
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
						if (userOrg.getPmUserRoles() == null
								|| userOrg.getPmUserRoles().isEmpty()) {

						}
						for (PmUserRole userRole : userOrg.getPmUserRoles()) {
							if (DbValueConst.RECORD_STATUS_ACTIVE == userRole
									.getRecordStatusFlg()) {
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
		List<String> uniqueRoles = new ArrayList<String>(
				Arrays.asList(ProfileManagementConst.UNIQUE_ORG_ROLES));
		boolean activeRole = false;
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		List<PmUserRole> userRoleList = null;
		if (userOrg != null) {
			// for (PmUserOrg userOrg : user.getPmUserOrgs()) {
			if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				userRoleList = userOrg.getPmUserRoles();
				for (PmUserRole userRole : userOrg.getPmUserRoles()) {
					String roleCode = userRole.getPmRole().getRoleCode();
					if (userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							&& userRole.getPmRole().isLimitRequired()) {
						if (userRole.getApprovalLimit() == null
								|| userRole.getApprovalLimit().compareTo(
										BigDecimal.ZERO) != 1) {
							throw new EpException(
									MessageConst.ERROR_770148,
									new String[] { getRoleDescByRoleCode(roleCode, languageCode) });
						}
					}
					
					// Check single role for all users
					if (userRole.getPmRole().isSingleRole() && userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
								&& !this.getManagedBean().getSelectedUserOrg()
										.getPmUserRoles().isEmpty()) {
							for (PmUserRole current : userRoleList) {
								if (!current.getPmRole().getRoleCode().equals(userRole.getPmRole().getRoleCode()) && current.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
									throw new EpException(MessageConst.ERROR_770134);
								}
							}
						}
					} 
					// check if contains unique role
					// if (uniqueRoles
					// .contains(userRole.getPmRole().getRoleCode())) {
					// String orgType = userOrg.getPmOrgProfile()
					// .getOrgTypeCode().getParameterCode();
					// if (checkUniqueRole(orgType, roleCode, true)) {
					// throw new EpException(
					// MessageConst.ERROR_770106,
					// new String[] { getRoleDescByRoleCode(roleCode) });
					// }
					// }

					// check if one role is active for user org active status
					// only
					if (DbValueConst.RECORD_STATUS_ACTIVE == userRole
							.getRecordStatusFlg()) {
						activeRole = true;
					}
				}
			} else {
				activeRole = true;
			}
			// }
		}

		if (!activeRole) {
			throw new EpException(MessageConst.ERROR_770022);
		}

	}

	/**
	 * Validation for PmUser's fields
	 * 
	 * @param user
	 */
	private void validateUser(PmUser user, PmUserOrg pmUserOrg) {
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
		if (StringUtils
				.isBlank(user.getIdentificationCode().getParameterCode())) {
			throw new EpException(MessageConst.ERROR_770033);
		}
		if (StringUtils.isBlank(user.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_770034);
		}
		
		 if(user.isNotifyBySms() 
				 && StringUtils.isBlank(pmUserOrg.getMobileNo())){
			 throw new EpException(MessageConst.ERROR_770090);
		 }
		 if(user.isNotifyBySms() 
				 && (pmUserOrg.getMobileTelcoId() == null)){
			 throw new EpException(MessageConst.ERROR_770250);
		 }

		// if (user.getPmCountry().getCountryId() == null) {
		// throw new EpException(MessageConst.ERROR_770035);
		// }
		
		//Validate if org profile not chosen.
		if(ParameterConst.ORG_TYPE_FACTORING.equals(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())){
			if(pmUserOrg.getPmOrgProfile() == null 
					|| pmUserOrg.getPmOrgProfile().getOrgProfileId() == null 
					|| pmUserOrg.getPmOrgProfile().getPmFinancialOrg() == null
					|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile().getPmFinancialOrg().getFinOrgName())){
				throw new EpException(MessageConst.ERROR_770175);	
			}
		}else if(ParameterConst.ORG_TYPE_SUPPLIER.equals(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())){
			if(pmUserOrg.getPmOrgProfile() == null 
					|| pmUserOrg.getPmOrgProfile().getOrgProfileId() == null 
					|| pmUserOrg.getPmOrgProfile().getSmSupplier() == null
					|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile().getSmSupplier().getCompanyName())
					|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile().getSmSupplier().getRegNo())){
				throw new EpException(MessageConst.ERROR_770175);	
			}
		}else{
			if(pmUserOrg.getPmOrgProfile() == null 
					|| pmUserOrg.getPmOrgProfile().getOrgProfileId() == null 
					|| pmUserOrg.getPmOrgProfile().getPmOrgValidity() == null
					|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile().getPmOrgValidity().getOrgCode())
					|| StringUtils.isBlank(pmUserOrg.getPmOrgProfile().getPmOrgValidity().getOrgName())){
				throw new EpException(MessageConst.ERROR_770175);	
			}
		}		
		
		if(!ParameterConst.ORG_TYPE_SUPPLIER.equals(pmUserOrg.getPmOrgProfile()
				.getOrgTypeCode().getParameterCode()) && StringUtils.isBlank(pmUserOrg.getDesignation())){			
				throw new EpException(MessageConst.ERROR_770108);			
		}
		if (StringUtils.isBlank(pmUserOrg.getEmail())) {
			throw new EpException(MessageConst.ERROR_770036);
		}
		
		
		if(user.getUserId() != null && DbValueConst.RECORD_STATUS_ACTIVE == pmUserOrg.getRecordStatusFlg()){
			//validate if the user contains any active user orgs (exclude supplier type and own org profile)			
			 IUserSO userSO = null;
			 try {
				UserReqDTO userOrgReqDTO = new UserReqDTO();
				userOrgReqDTO.setUserId(user.getUserId());
				userOrgReqDTO.setExcludeOrgType(OrganizationType.SUPPLIER.value());
				userOrgReqDTO.setExcludeOrgProfileId(pmUserOrg.getPmOrgProfile().getOrgProfileId());
				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
						 ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveAllUserOrgListByUserId(userOrgReqDTO);
				if(resDTO.getUserOrgList() != null && !resDTO.getUserOrgList().isEmpty()){
					PmOrgProfile orgProfile = resDTO.getUserOrgList().get(0).getPmOrgProfile();
					throw new EpException(MessageConst.ERROR_770155,new String[] {
							orgProfile.getOrgName(),orgProfile.getOrgCode()					
					});
				}
			 }finally{
				 userSO = null; 
			 }
		}
		// if(StringUtils.isBlank(user.getPreferredLanguage())){
		// throw new EpException(MessageConst.ERROR_770037);
		// }
		// if(user.getNotifyRemovalDay()==null
		// || (user.getNotifyRemovalDay()!=null
		// && user.getNotifyRemovalDay()==0)){
		// throw new EpException(MessageConst.ERROR_770038);
		// }
		// if(!StringUtils.isBlank(user.getConfirmPassword()) &&
		// !StringUtils.isBlank(user.getPassword()) &&
		// StringUtils.isBlank(user.getOldPassword())){
		// throw new EpException("Old Password is mandatory.");
		// }
	}
	
	
	private void validateUserOrg(PmUser user){
		
	}

//	/**
//	 * Validation for PmAddress's fields
//	 * 
//	 * @param pmAddress
//	 * @return boolean
//	 */
//	private boolean validateUserAddress(PmAddress pmAddress) {
//		logger.info("validateUserAddress");
//		boolean addressEntered = false;
//		if (!this.getManagedBean().getSelectedUser().isOrgAddress()) {
//			// address is optional
//			if (pmAddress.getPmCountry().getCountryId() != null
//					|| !StringUtils.isBlank(pmAddress.getAddress1())
//					|| !StringUtils.isBlank(pmAddress.getPostcode())
//					|| pmAddress.getPmState().getStateId() != null
//					|| pmAddress.getPmDistrict().getDistrictId() != null
//					|| pmAddress.getPmDivision().getDivisionId() != null) {
//				addressEntered = true;
//			}
//			if (addressEntered) {
//				if (pmAddress.getPmCountry().getCountryId() == null) {
//					throw new EpException(MessageConst.ERROR_770039);
//				}
//
//				if (StringUtils.isBlank(pmAddress.getAddress1())) {
//					throw new EpException(MessageConst.ERROR_770040);
//				}
//
//				if (StringUtils.isBlank(pmAddress.getPostcode())) {
//					throw new EpException(MessageConst.ERROR_770041);
//				}
//
//				if (pmAddress.getPmState().getStateId() == null) {
//					throw new EpException(MessageConst.ERROR_770042);
//				}
//
//				if (pmAddress.getPmDistrict().getDistrictId() == null) {
//					throw new EpException(MessageConst.ERROR_770043);
//				}
//
//				if (this.getManagedBean().isDivisionFlagC()) {
//					if (pmAddress.getPmDivision().getDivisionId() == null) {
//						throw new EpException(MessageConst.ERROR_770044);
//					}
//				}
//			}
//		}
//		return addressEntered;
//	}

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
			
			// if (pmUser.getNotifyRemovalDay() == null
			// || pmUser.getNotifyRemovalDay() <= 0) {
			// throw new EpException(MessageConst.ERROR_770047);
			// }
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

	private void checkChangesAndFillTrackingDiary(PmUser user,
			PmUserOrg userOrg, UserReqDTO reqDTO) {
		
		boolean isProfileDetailsChanged = false;
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setStatusId(userOrg.getRecordStatusFlg()
				.longValue());
		StatusResDTO statusResDTO = statusSO
				.retrieveStatusByStatusId(statusReqDTO);
		String oldDesignation = user.getDesignation();
		String newDesignation = userOrg.getDesignation();
		if (oldDesignation != null
				&& !oldDesignation.equalsIgnoreCase(newDesignation)) {// if
																		// designation
																		// was
																		// modified
			isProfileDetailsChanged = true;

		}
		String oldEmail = user.getEmail();
		String newEmail = userOrg.getEmail();
		if (oldEmail != null && !oldEmail.equalsIgnoreCase(newEmail)) {
			isProfileDetailsChanged = true;
		}
		String oldGrade = user.getGrade();
		String newGrade = userOrg.getGrade();
		if (oldGrade != null && !oldGrade.equalsIgnoreCase(newGrade)) {
			isProfileDetailsChanged = true;
		}
		boolean oldPMP = user.isPMP();
		boolean newPMP = userOrg.isPMP();
		if (oldPMP != newPMP) {
			isProfileDetailsChanged = true;
		}
		String oldUsername = this.getManagedBean().getPmUserOriginal()
				.getUserName();
		logger.info("oldUsername=" + oldUsername);
		String newUsername = user.getUserName();
		if (!oldUsername.equalsIgnoreCase(newUsername)) {
			isProfileDetailsChanged = true;	
		}
		if(isProfileDetailsChanged){
			logger.info("Tracking diary for user profile updated :saveUserProfile");
			String messageId = MessageConst.TD_80014;
			String[] msgParam = new String[] {user.getUserName()};
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
									.getPmStatus() : new PmStatus()));
		}
		if (!this.getManagedBean().getPmUserOrgOriginal().getRecordStatusFlg().equals(userOrg
				.getRecordStatusFlg())) {
			logger.info("Tracking diary for user profile: change user state :saveUserProfile");
			String messageId = userOrg.getRecordStatusFlg() == 0 ? MessageConst.TD_80004
					: MessageConst.TD_80005;
			String[] msgParam = new String[] {
					user.getUserName(),
					userOrg.getPmOrgProfile().getOrgCode(),
					userOrg.getPmOrgProfile().getOrgName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(
					PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
									.getPmStatus() : new PmStatus()));
		}

		if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {// ????

			for (PmUserRole userRole : reqDTO.getNewRoleList()) {
				//
				if (userRole.isBrandNew()) {

					logger.info("Tracking diary for user profile: add new Role :saveUserProfile");
					String messageId = MessageConst.TD_80001;
					String[] msgParam = new String[] { user.getUserName() , getRoleDescByRoleCode(userRole
							.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					logger.info("......msgParam[1]"+msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyValues(user,
									getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
											.getPmStatus() : new PmStatus()));

				} else if (userRole.isModified()) {// the role's activated
					logger.info("Tracking diary for user profile: Role is re-activated :saveUserProfile");
					String messageId = MessageConst.TD_80003;
					String[] msgParam = new String[] { user.getUserName() ,getRoleDescByRoleCode(userRole
							.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY)};
					logger.info("..222....msgParam[1]"+msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyValues(user,
									getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
											.getPmStatus() : new PmStatus()));
				}
			}
			for (PmUserRole userRole : reqDTO.getRemoveRoleList()) {
				if (!userRole.isBrandNew()) {// the role's de-activated
					logger.info("Tracking diary for user profile: Role is de-activated :saveUserProfile");
					
					String messageId = MessageConst.TD_80002;
					String[] msgParam = new String[] {user.getUserName() ,getRoleDescByRoleCode(userRole
							.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY)};
					logger.info("..333....msgParam[1]"+msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyValues(user,
									getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
											.getPmStatus() : new PmStatus()));
				}
			}

			for (PmUserRole userRole : userOrg.getPmUserRoles()) {
				if (userRole.getPmRole().getRoleCode().equals("FL_APPROVER")) {
					// getFromMap
					String oldLetterNo = this.getManagedBean()
							.getRoleCodeToLetterNoMap()
							.get(userRole.getPmRole().getRoleCode());
					String newLetterNo = userRole.getLetterNo();
					if (oldLetterNo != null && !oldLetterNo.isEmpty()
							&& !oldLetterNo.equalsIgnoreCase(newLetterNo)) {
						logger.info("Tracking diary for user profile: letterNo is changed :saveUserProfile");
						String messageId = MessageConst.TD_80007;
						String[] msgParam = new String[] {user.getUserName() , oldLetterNo,
								newLetterNo };
						reqDTO.setTrackingStatus(true);
						reqDTO.getTrackDairyVOs().add(
								PmCommonUtil.assignTrackingDairyValues(user,
										getActionDesc(messageId, msgParam),
										statusResDTO.getPmStatus() != null ? statusResDTO
												.getPmStatus() : new PmStatus()));

					}
				}
				if (userRole.getPmRole().isLimitRequired()) {
					// getFromMap
					BigDecimal oldApprovalLimit = this.getManagedBean()
							.getRoleCodeToApprovalLimitMap()
							.get(userRole.getPmRole().getRoleCode());
					BigDecimal newApprovalLimit = userRole.getApprovalLimit();
					if (oldApprovalLimit != null
							&& (oldApprovalLimit.compareTo(newApprovalLimit) != 0)) {
						logger.info("Tracking diary for user profile: approvalLimit is changed :saveUserProfile");
						String messageId = MessageConst.TD_80006;
						String[] msgParam = new String[] {user.getUserName() ,
								oldApprovalLimit.toString(),
								newApprovalLimit.toString() };
						reqDTO.setTrackingStatus(true);
						reqDTO.getTrackDairyVOs().add(
								PmCommonUtil.assignTrackingDairyValues(user,
										getActionDesc(messageId, msgParam),
										statusResDTO.getPmStatus() != null ? statusResDTO
												.getPmStatus() : new PmStatus()));

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
			
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			reqDTO.setPmUser(pmUser);
			logger.info("Tracking diary for user profile: reset password :saveUserProfile");
			String messageId = MessageConst.TD_80015;
			String[] msgParam = new String[] {pmUser.getUserName()};
			reqDTO.setTrackingStatus(true);
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(pmUser.getRecordStatusFlg()
					.longValue());
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyValues(pmUser,
								getActionDesc(messageId, msgParam),statusResDTO.getPmStatus() != null ? statusResDTO
										.getPmStatus() : new PmStatus()));
			
			UserResDTO resDTO = userSO.resendNotificationAndPwd(reqDTO);
			if (resDTO != null && resDTO.isSuccessful()) {
				// Reset password and notification sent successfully.
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770107,
										new String[] { resDTO.getPmUser()
												.getLoginId() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
		// boolean addressEntered = false;
		boolean isNewUser = this.getManagedBean().getSelectedUser()
				.isBrandNew();
		logger.info("..............saveUserProfile.................isNewUser="
				+ isNewUser);

		try {

			user = this.getManagedBean().getSelectedUser();
			userOrg = this.getManagedBean().getSelectedUserOrg();
			// Validation
			if(isNewUser){
		    	checkIdentificationNumber1();
			}
			validateUser(user, userOrg);
			// addressEntered = validateUserAddress(this.getManagedBean()
			// .getUserAddress());
			// validateResponsibleOrg(user);
			// oneActiveResponsibleOrg(user);
			validatePreference(user);
			validateRoles(userOrg);
			reqDTO = new UserReqDTO();
			reqDTO.setServerName(getFacesScopes().getRequest().getServerName());
			reqDTO.setServerPort(getFacesScopes().getRequest().getServerPort());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			/*
			 * no need check as assumed user have logged in
			 * if(!StringUtils.isBlank(user.getConfirmPassword()) &&
			 * !StringUtils.isBlank(user.getPassword())){
			 * //!StringUtils.isBlank(user.getOldPassword()) && isChangePwd =
			 * true; }
			 * 
			 * 
			 * if(isChangePwd){ if(!checkOldPassword(user)){ throw new
			 * EpException("Old Password is invalid."); } }
			 */
			// if (this.getManagedBean().getSelectedUser().isOrgAddress()) {
			// cloneUserAddress(user);
			// } else {
			// loadUserAddress(user, addressEntered);
			// }

			loadUserOrgType(user, userOrg);
			List<PmUserOrg> userOrgList = new ArrayList<PmUserOrg>();// always
																		// will
																		// have
																		// one
																		// org
																		// for
																		// each
																		// user
			Long loginSessionUserId = getFacesScopes().getUserSessionBean()
					.getUserId();
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
			if (!isNewUser) {
				checkChangesAndFillTrackingDiary(user, userOrg, reqDTO);

			}
			fillUserInfoFromUserOrg(user, userOrg);
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
			statusReqDTO.setStatusId(userOrg.getRecordStatusFlg()
					.longValue());
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			if (isNewUser) {
				logger.info("Tracking diary for user profile: create new user :saveUserProfile");
				String orgCode = null;
				String orgName = null;
				if(ParameterConst.ORG_TYPE_FACTORING.equals(userOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())){
					orgCode = userOrg.getPmOrgProfile().getPmFinancialOrg().getBizRegNumber();
					orgName = userOrg.getPmOrgProfile().getPmFinancialOrg().getFinOrgName();
				}else if(ParameterConst.ORG_TYPE_SUPPLIER.equals(userOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())){
					orgCode = userOrg.getPmOrgProfile().getSmSupplier().getRegNo();
					orgName = userOrg.getPmOrgProfile().getSmSupplier().getCompanyName();
				}else{
					orgCode = userOrg.getPmOrgProfile().getOrgCode();
					orgName = userOrg.getPmOrgProfile().getOrgName();
				}
				
				String messageId = MessageConst.TD_80000;
				String[] msgParam = new String[] {
						user.getUserName() ,
						orgCode,
						orgName };
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
										.getPmStatus() : new PmStatus()));
			}
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			reqDTO.setPmUser(user);
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale()
					.getLanguage());
			
			if(ParameterConst.ORG_TYPE_FACTORING.equals(user.getOrgTypeCode().getParameterCode())){
				reqDTO.setFactoringUser(true);
			}
			
			if(ParameterConst.ORG_TYPE_SUPPLIER.equals(user.getOrgTypeCode().getParameterCode())){
				reqDTO.setSupplierUser(true);
			}
			
			UserResDTO resDTO = userSO.saveUserAndUserOrg(reqDTO);
			logger.info("save user -END");
			if (resDTO.isSuccessful()) {

				uploadAttachments();
				deleteAttachments();
				/*
				 * if(user.getPortrait()!=null){
				 * uploadPortrait(resDTO.getPmUser()); }
				 */
				if (!isNewUser) {
					if (!userOrg.isBrandNew()) {
						loadUserProfile(resDTO.getPmUser().getUserId(), userOrg);
					} else {
						loadUserProfile(resDTO.getPmUser().getUserId(), resDTO
								.getPmUser().getActiveUserOrgs().get(0));
					}
				}
				if (!StringUtils.isBlank(resDTO.getRemarks())) {
					// OIM user creation successfully, problem with roles.
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, resDTO
									.getRemarks(), resDTO.getRemarks()));
				} else {
					if (isNewUser) {
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_INFO,
												null,
												MessageUtils
														.getMessage(
																MessageConst.INFO_770049,
																new String[] { resDTO
																		.getPmUser()
																		.getLoginId() },
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes()
																		.getUserLocale())));
					} else {
						FacesContext
								.getCurrentInstance()
								.addMessage(
										null,
										new FacesMessage(
												FacesMessage.SEVERITY_INFO,
												null,
												MessageUtils
														.getMessage(
																MessageConst.INFO_770050,
																new String[] { resDTO
																		.getPmUser()
																		.getLoginId() },
																MessageUtils.BUNDLE_COMMON_MESSAGES,
																new FacesScopes()
																		.getUserLocale())));
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
			// throw new EpException(MessageConst.ERROR_770059, new
			// String[]{e.getMessage(),e.getMessage()});
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
				for (Map.Entry<PmUserAttachment, FileUploadEvent> entry : this
						.getManagedBean().getFileEventMapper().entrySet()) {
					tempPmUserAttachment = entry.getKey(); // PmUserAttachment
					tempFileUploadEvent = entry.getValue(); // FileUploadEvent
					PmCommonUtil.commonUploadFile(tempFileUploadEvent,
							PmCommonUtil.getAttachmentPath(tempPmUserAttachment
									.getFilePath()));
				}
				this.getManagedBean().getFileEventMapper().clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060,
					new String[] { e.getMessage() });

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
					&& !this.getManagedBean().getDeletePmUserAttachments()
							.isEmpty()) {
				for (PmUserAttachment pmUserAttachment : this.getManagedBean()
						.getDeletePmUserAttachments()) {
					if (!pmUserAttachment.isBrandNew()) {
						PmCommonUtil.deleteUploadFile(pmUserAttachment
								.getFilePath());
					}
				}
				PmCommonUtil.deleteUserAttachment(this.getManagedBean()
						.getDeletePmUserAttachments());
				this.getManagedBean().getDeletePmUserAttachments().clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054,
					new String[] { e.getMessage() });

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

			reqDTO.setPortrait(this.getManagedBean().getSelectedUser()
					.getPortrait());
			reqDTO.setLoginId(this.getManagedBean().getSelectedUser()
					.getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.updatePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_770117,
														new String[] { this
																.getManagedBean()
																.getSelectedUser()
																.getLoginId() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770115,
					new String[] { e.getMessage() });
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

				reqDTO.setLoginId(this.getManagedBean().getSelectedUser()
						.getLoginId());

				userSO = (IUserSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);

				resDTO = userSO.deletePortrait(reqDTO);

				if (resDTO != null && resDTO.isSuccessful()) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_770118,
															new String[] { this
																	.getManagedBean()
																	.getSelectedUser()
																	.getLoginId() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));

				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770116,
						new String[] { e.getMessage() });
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

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getLoginId())) {
				throw new EpException(MessageConst.ERROR_770125);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getPassword())) {
				throw new EpException(MessageConst.ERROR_770126);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getConfirmPassword())) {
				throw new EpException(MessageConst.ERROR_770127);
			}

			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.changePassword(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_770124,
														new String[] { this
																.getManagedBean()
																.getSelectedUser()
																.getLoginId() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				clearPassword();

			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770128,
					new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("changePassword - END");
	}
	public void checkIdentificationNumber1() {
		System.out.println(".............checkIdentificationNumber1...........");
		this.getManagedBean().getSelectedUser().setHasActiveOrg(false);
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		if (getManagedBean().getSelectedUser().getIdentificationNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getSelectedUser()
						.getIdentificationNo())
				&& getManagedBean().getSelectedUser().getIdentificationCode()
						.getParameterCode() != null) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean().getSelectedUser()
					.getIdentificationNo());

			searchBean.getDataFilterBeans().add(idNoSearchBean);

			searchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean(
							"identificationCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedUser()
									.getIdentificationCode().getParameterCode()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserSearchBean(searchBean);
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.searchUserProfile(userReqDTO);

				if (resDTO != null
						&& resDTO.getUserResultBean().getResultList() != null
						&& resDTO.getUserResultBean().getResultList().size() > 0) {
					PmUser user = resDTO.getUserResultBean().getResultList()
							.get(0);
					// TODO set current user info
					// check for any active org
					List<PmUserOrg> pmUserOrgs = PmCommonUtil
							.retrieveActiveOrg(user.getUserId());
					
					if (pmUserOrgs != null && pmUserOrgs.size() > 0) {
						
						// loadUserInfo(user.getUserId(), pmUserOrgs.get(0));
						// the identification Number already in DB
						this.getManagedBean().getSelectedUser()
								.setHasActiveOrg(true);
						PmOrgProfile orgProfile = pmUserOrgs.get(0)
								.getPmOrgProfile();
//						System.out.println("orgProfile.getPmFinancialOrg().getFinOrgName()="+orgProfile.getPmFinancialOrg().getFinOrgName());
						if( orgProfile.getOrgName()== null || orgProfile.getOrgCode() == null){
							String param1 =null;
//							System.out.println("orgProfile.getOrgTypeCode().getParameterCode()="+orgProfile.getOrgTypeCode().getParameterCode());
							if(orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_SUPPLIER)){
								param1 = orgProfile.getSmSupplier().getCompanyName();
								
							}else if(orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_FACTORING)){
								param1 = orgProfile.getPmFinancialOrg().getFinOrgName();
								
							}
//							System.out.println("param1="+param1);
							
							throw new EpException(MessageConst.ERROR_770215,
									new String[] {param1});
						}
						throw new EpException(MessageConst.ERROR_770155,
								new String[] { orgProfile.getOrgName(),
										orgProfile.getOrgCode() });
							
				     }else {
				    		
				    	 IPersonnelSO personnelSO;
				    	 personnelSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
				    		
				    		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				    		reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
				    		PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
				    		
				    	 if(resDTO1.isSupplierPersonnel()){
				    		 throw new EpException(MessageConst.ERROR_770214,
										new String[] {getManagedBean().getSelectedUser().getIdentificationNo()});
				    	 }
					}

				}else{//do validate by isSupplier
					
			    	 IPersonnelSO personnelSO;
			    	 personnelSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
			    		
			    		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			    		reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
			    		PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
			    		
			    	 if(resDTO1.isSupplierPersonnel()){
			    		 throw new EpException(MessageConst.ERROR_770214,
									new String[] {getManagedBean().getSelectedUser().getIdentificationNo()});
				 }
			 	}

			} finally {
				userSO = null;
			}
		}

	}
	public void checkIdentificationNumber() {
		System.out.println(".............checkIdentificationNumber...........");
		this.getManagedBean().getSelectedUser().setHasActiveOrg(false);
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		if (getManagedBean().getSelectedUser().getIdentificationNo() != null
				&& !"".equalsIgnoreCase(getManagedBean().getSelectedUser()
						.getIdentificationNo())
				&& getManagedBean().getSelectedUser().getIdentificationCode()
						.getParameterCode() != null) {
			SearchFilterBean idNoSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			idNoSearchBean.setSearchField("identificationNo");
			idNoSearchBean.setSearchValue(getManagedBean().getSelectedUser()
					.getIdentificationNo());

			searchBean.getDataFilterBeans().add(idNoSearchBean);

			searchBean
					.getDataFilterBeans()
					.add(new SearchFilterBean(
							"identificationCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getSelectedUser()
									.getIdentificationCode().getParameterCode()));

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));

			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserSearchBean(searchBean);
			IUserSO userSO;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.searchUserProfile(userReqDTO);

				if (resDTO != null
						&& resDTO.getUserResultBean().getResultList() != null
						&& resDTO.getUserResultBean().getResultList().size() > 0) {
					PmUser user = resDTO.getUserResultBean().getResultList()
							.get(0);
					// TODO set current user info
					// check for any active org
					List<PmUserOrg> pmUserOrgs = PmCommonUtil
							.retrieveActiveOrg(user.getUserId());
					
					if (pmUserOrgs != null && pmUserOrgs.size() > 0) {
						
						// loadUserInfo(user.getUserId(), pmUserOrgs.get(0));
						// the identification Number already in DB
						this.getManagedBean().getSelectedUser()
								.setHasActiveOrg(true);
						PmOrgProfile orgProfile = pmUserOrgs.get(0)
								.getPmOrgProfile();
//						System.out.println("orgProfile.getPmFinancialOrg().getFinOrgName()="+orgProfile.getPmFinancialOrg().getFinOrgName());
						if( orgProfile.getOrgName()== null || orgProfile.getOrgCode() == null){
							String param1 =null;
//							System.out.println("orgProfile.getOrgTypeCode().getParameterCode()="+orgProfile.getOrgTypeCode().getParameterCode());
							if(orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_SUPPLIER)){
								param1 = orgProfile.getSmSupplier().getCompanyName();
								
							}else if(orgProfile.getOrgTypeCode().getParameterCode() != null && orgProfile.getOrgTypeCode().getParameterCode().equals(ParameterConst.ORG_TYPE_FACTORING)){
								param1 = orgProfile.getPmFinancialOrg().getFinOrgName();
								
							}
//							System.out.println("param1="+param1);
							
							throw new EpException(MessageConst.ERROR_770215,
									new String[] {param1});
						}
						throw new EpException(MessageConst.ERROR_770155,
								new String[] { orgProfile.getOrgName(),
										orgProfile.getOrgCode() });
							
				     }else {
				    		
				    	 IPersonnelSO personnelSO;
				    	 personnelSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);

				    		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				    		reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
				    		PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
//				    		
				    	 if(resDTO1.isSupplierPersonnel()){
				    		 throw new EpException(MessageConst.ERROR_770214,
										new String[] {getManagedBean().getSelectedUser().getIdentificationNo()});
				    	 }else{
				    		
						     loadUserInfo(user.getUserId());
						// assign new pmUserOrg object to the current user
				    	 }
					}

				}else{//do validate by isSupplier
					
			    	 IPersonnelSO personnelSO;
			    	 personnelSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);

			    		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			    		reqDTO.setIdentificationNo(getManagedBean().getSelectedUser().getIdentificationNo());
			    		PersonnelResDTO resDTO1 = personnelSO.isSupplierPersonnel(reqDTO);
			    	 if(resDTO1.isSupplierPersonnel()){
			    		 throw new EpException(MessageConst.ERROR_770214,
									new String[] {getManagedBean().getSelectedUser().getIdentificationNo()});
				 }
			 	}
			} finally {
				userSO = null;
			}
		}

	}

	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}

	private boolean isAnyPendingTaskForUser(String loginId) {
		logger.info("isAnyPendingTaskForUser");
		boolean anyPendingTask = false;

		IBpmSO bpmSO = null;
		BpmRequestDTO reqDTO = null;
		BpmResponseDTO responseDTO = null;
		String token = null;
		// List<Integer> statusIds = null;
		TaskAssignmentSearchBean taskSearchBean = null;
		SearchResultBean<WorklistItemVO> resultBean = null;

		try {

			if (loginId != null && loginId.length() > 0) {
				// statusIds = retrieveAvailableStatusId();
				// token = getFacesScopes().getBPMAuthenticationCtxToken();

				bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
						.getServiceObject(IBpmSO.class);
				// token = bpmSO.authenticate("weblogic","welcome1"); //TODO

				reqDTO = new BpmRequestDTO();

				logger.info("BPM loginId******" + loginId);
				// System.out.println("BPM statusIds******"+statusIds.size());

				// if(token==null){
				// throw new EpException(MessageConst.ERROR_770099);
				// }
				// logger.info("BPM auth******" + token);

				// set useful info
				// reqDTO.setCtxToken(token);
				// reqDTO.setStatusList(statusIds);
				reqDTO.setUserId(loginId);
				// reqDTO.setDocNumber(getManagedBean().getTaskAssignmentSearchBean().getDocNo());

				// if(getManagedBean().getTaskAssignmentSearchBean().getDocType()
				// !=null &&
				// getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter()
				// !=null ){
				// reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter().getParameterCode());
				// }
				//

				// Invoke BPM
				responseDTO = bpmSO.getReassignedItems(reqDTO);

				if (responseDTO.getWorklistVO() != null) {
					logger.info("BPM size******"
							+ responseDTO.getWorklistVO().getWorklistItem()
									.size());
					logger.info("BPM total******"
							+ responseDTO.getWorklistVO().getTotal());
					if (responseDTO.getWorklistVO().getTotal() > 0) {
						anyPendingTask = true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bpmSO = null;
		}
		return anyPendingTask;
	}
	
	
	private Long getCurrentOrgTypeId(){
		Long currentOrgTypeId = null;
		if (this.getManagedBean().getSelectedUserOrg() != null
				&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile() != null
				&& this.getManagedBean().getSelectedUserOrg().getPmOrgProfile()
						.getOrgTypeCode() != null) {
			currentOrgTypeId = this.getManagedBean().getSelectedUserOrg()
					.getPmOrgProfile().getOrgTypeCode().getParameterId();
		}		
		return currentOrgTypeId;
	}
	
	
	private PmUserGroup retrieveUserActiveUserGroup(Long userId) {
		logger.info("userActiveUserGroup");
		PmUserGroup userActiveUserGroup = null;
		IUserGroupSO userGroupSO = null;
		UserGroupReqDTO reqDTO = null;		
		try {
			if (userId != null) {
				SearchBean<PmUserGroup> searchBean = new SearchBean<PmUserGroup>();
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserGroupUsers.recordStatusFlg",
								SearchOperator.OPERATOR_EQUALS.value(),
								DbValueConst.RECORD_STATUS_ACTIVE));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmUserGroupUsers.pmUser.userId",
								SearchOperator.OPERATOR_EQUALS.value(),
								userId));
				
				reqDTO = new UserGroupReqDTO();
				reqDTO.setUserGroupSearchBean(searchBean);
				userGroupSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserGroupSO.class);
				UserGroupResDTO resDTO = userGroupSO.searchUserGroupProfile(reqDTO);
				if(resDTO != null && resDTO.getPmUserGroupList() != null 
						&& !resDTO.getPmUserGroupList().isEmpty()){
					userActiveUserGroup = resDTO.getPmUserGroupList().get(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		} finally {
			userGroupSO = null;
			reqDTO = null;
		}
		return userActiveUserGroup;
	}

	/*
	 * private boolean checkOldPassword(PmUser pmUser) throws PortalException,
	 * SystemException{ logger.info("checkOldPassword"); boolean result = false;
	 * long status = 0; long companyId =
	 * Long.parseLong(EpPropertiesUtils.getInstance
	 * ().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID)); String loginId =
	 * pmUser.getLoginId(); String pwd = pmUser.getOldPassword();
	 * System.out.println("loginId: " + loginId); System.out.println("pwd: " +
	 * pwd); System.out.println("companyId: " + companyId); status =
	 * UserLocalServiceUtil.authenticateForBasic(companyId,
	 * CompanyConstants.AUTH_TYPE_SN, loginId, pwd); if(status>0){ result =
	 * true; } return result; }
	 */

	// /**
	// * delete portrait
	// * @throws Exception
	// */
	// public void deletePortrait() throws Exception{
	// logger.info("deletePortrait - START");
	// this.getManagedBean().getSelectedUser().setPortrait(null);
	// this.getManagedBean().getSelectedUser().setReplacePortrait(true);
	// this.getManagedBean().setDeletePortrait(true);
	// logger.info("deletePortrait - END");
	// }
	//
	//
	//
	//

	// /**
	// * fix the remote updatePortrait
	// * @param pmUser
	// * @throws Exception
	// */
	// private void uploadPortrait(PmUser pmUser) throws Exception{
	// logger.info("uploadPortrait - START");
	// long imageMaxSize =
	// PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
	// long companyId =
	// Long.parseLong(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID));
	// byte[] bytes = pmUser.getPortrait();
	// long userId;
	// logger.info("imageMaxSize: " + imageMaxSize);
	// logger.info("companyId: " + companyId);
	// logger.info("bytes: " + bytes);
	// logger.info("pmUser.getLoginId(): " + pmUser.getLoginId());
	// if(bytes!=null){
	// User user = UserServiceUtil.getUserByScreenName(companyId,
	// pmUser.getLoginId());
	// if(user!=null){
	// System.out.println("user id" + user.getUserId());
	// }else{
	// System.out.println("user null");
	// }
	// userId = user.getUserId();
	// logger.info("userId: " + userId);
	// UserServiceUtil.updatePortrait(user.getUserId(), bytes);
	// }
	// logger.info("uploadPortrait - END");
	// }
	//
	// /**
	// * Get user portrait
	// * @param event
	// * @throws Exception
	// */
	// public void handleFileUpload1() throws Exception{
	// try{
	// logger.info("handleFileUpload - START - UserServiceUtil");
	// byte[] bytes = this.getManagedBean().getSelectedUser().getPortrait();
	// long imageMaxSize =
	// PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
	// System.out.println("size = " + imageMaxSize);
	// System.out.println("bytes.length = " + bytes.length);
	// System.out.println("screen name = " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("identity no = " +
	// this.getManagedBean().getSelectedUser().getIdentificationNo());
	// String screenName =
	// this.getManagedBean().getSelectedUser().getLoginId()==null
	// ? this.getManagedBean().getSelectedUser().getIdentificationNo()
	// : this.getManagedBean().getSelectedUser().getLoginId();
	// long companyId =
	// Long.parseLong(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID));
	// User user = UserServiceUtil.getUserByScreenName(companyId , screenName);
	// if(user!=null){
	// System.out.println("user id" + user.getUserId());
	// }else{
	// System.out.println("user null");
	// }
	// long userId = user.getUserId();
	// UserServiceUtil.updatePortrait(userId, bytes);
	// }catch(Exception e){
	// e.printStackTrace();
	// throw e;
	// }
	// logger.info("handleFileUpload - END");
	// }
	//
	// /**
	// * Get user portrait
	// * @param event
	// * @throws Exception
	// */
	// public void handleFileUpload2() throws Exception{
	// logger.info("handleFileUpload2 - START - Remote");
	// byte[] bytes = this.getManagedBean().getSelectedUser().getPortrait();
	// long imageMaxSize =
	// PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
	// System.out.println("size = " + imageMaxSize);
	// System.out.println("bytes.length = " + bytes.length);
	// System.out.println("screen name = " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("identity no = " +
	// this.getManagedBean().getSelectedUser().getIdentificationNo());
	//
	// IUserSO userSO = null;
	// UserReqDTO reqDTO = new UserReqDTO();
	// try {
	// PmUser pmUser = this.getManagedBean().getSelectedUser();
	// reqDTO.setPmUser(pmUser);
	// userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	// UserResDTO resDTO = userSO.updatePortrait(reqDTO); //saveuser2
	// if(resDTO!=null && resDTO.isSuccessful()){
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO,
	// null, "Delete Portrait Successfull for Login ID: "
	// + resDTO.getPmUser().getLoginId()));
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
	// e.getMessage()));
	// } finally {
	// userSO = null;
	// }
	// logger.info("handleFileUpload - END");
	// }
	//

	//
	//
	// // to create new profiles into database
	// public void createNewProfiles(PmUser user) {
	//
	// IUserSO userSO;
	// user.getOrgTypeCode().setParameterId(166L);
	// try {
	// userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	// UserReqDTO reqDTO = new UserReqDTO();
	// reqDTO.setPmUser(this.getManagedBean().getUser());
	//
	// UserResDTO resDTO = userSO.saveMyProfile(reqDTO);
	//
	// if (resDTO.isSuccessful()) {
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "Record save successfully", "User ID: "
	// + resDTO.getPmUser().getUserId()));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
	// e.getMessage()));
	// } finally {
	// userSO = null;
	// }
	// }
	/*
	 * // to add roles names datatables public void addRolesNames() {
	 * 
	 * // getManagedBean().setSelectedRolesList(new PmRoleDesc());
	 * 
	 * // getManagedBean().getSelectedRolesList().setPmRole( //
	 * getManagedBean().getRoles());
	 * 
	 * }
	 * 
	 * 
	 * 
	 * public void addSelectedRole() { debugLogger("addSelectedRole");
	 * List<PmRoleDesc> roledescList = this.getManagedBean()
	 * .getSelectedUserRole(); if (roledescList == null) { roledescList = new
	 * ArrayList<PmRoleDesc>(); } PmRoleDesc roledesc = new PmRoleDesc();
	 * this.getManagedBean().setSelectUserRole(roledesc);
	 * getManagedBean().setSelectedUserRole(roledescList); }
	 * 
	 * public void acceptRoleListAction() { debugLogger("acceptRoleListAction");
	 * 
	 * List<PmRoleDesc> roleList = getManagedBean() .getSelectedUserRole();
	 * 
	 * if (null == roleList) { roleList = new ArrayList<PmRoleDesc>(); }
	 * 
	 * 
	 * PmRoleDesc name = new PmRoleDesc();
	 * name.setRoleName(getManagedBean().getSelectUserRole() .getRoleName());
	 * 
	 * roleList.add(name);
	 * 
	 * }
	 * 
	 * public void addRecevingAddress() { debugLogger("addRecevingAddress");
	 * List<PmAddressType> addressTypeList = this.getManagedBean()
	 * .getPmRecevingAddressList(); if (addressTypeList == null) {
	 * addressTypeList = new ArrayList<PmAddressType>(); } PmAddressType
	 * addressname = new PmAddressType();
	 * this.getManagedBean().setSelectRecevingAddress(addressname);
	 * getManagedBean().setPmRecevingAddressList(addressTypeList); }
	 */

	// System.out.println("handleFileUploadTest - start");
	// UploadedFile item = event.getFile();
	//
	// System.out.println("login ID: " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("username: " +
	// this.getManagedBean().getSelectedUser().getUserName());
	//
	// this.getManagedBean().getSelectedUser().setPortrait(item.getContents());
	//
	// System.out.println("login ID: " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("username: " +
	// this.getManagedBean().getSelectedUser().getUserName());
	// IUserSO userSO = null;
	// try {
	// userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	// UserReqDTO reqDTO = new UserReqDTO();
	// reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
	//
	// UserResDTO resDTO = userSO.updatePortrait(reqDTO);
	//
	// if (resDTO.isSuccessful()) {
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO,
	// null, "Record save successfully for Login ID: "
	// + resDTO.getPmUser().getLoginId()));
	// this.preLoadingUserProfile();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
	// e.getMessage()));
	// } finally {
	// userSO = null;
	// }
	//
	// FacesMessage msg = new FacesMessage("Succesful", event.getFile()
	// .getFileName() + " is uploaded.");
	// FacesContext.getCurrentInstance().addMessage(null, msg);
	//
	// System.out.println("handleFileUploadTest - end");
	// }

	// private List<PmRoleDesc> getChildRoles(String parentCode){
	// List<PmRoleDesc> childList = new ArrayList<PmRoleDesc>();
	// try{
	// if(parentCode!=null){
	// RoleReqDTO roleReqDTO = new RoleReqDTO();
	// roleReqDTO.setParentRole(parentCode);
	// roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
	// IRoleSO roleSO = BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
	// RoleResDTO resDto = roleSO.searchRoleNameList(roleReqDTO);
	// if(resDto!=null && resDto.isSuccessful()){
	// childList = resDto.getRoleList();
	// }
	//
	// }
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// return childList;
	// }
	//

	/*
	 * private void dummyUser(PmUser pmUser){ System.out.println("dummyUser");
	 * 
	 * //dummy a role here if(!pmUser.isBrandNew()){
	 * System.out.println("dummy user not brand new ");
	 * if(pmUser.getPmUserOrgs()==null){
	 * System.out.println("dummy user empty pmUserOrgs");
	 * pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>()); //pmUserOrg PmUserOrg
	 * pmUserOrg = new PmUserOrg();
	 * 
	 * 
	 * //pmUserOrg.pmOrgProfile PmOrgProfile pmOrgProfile = new PmOrgProfile();
	 * pmOrgProfile.setOrgProfileId(1L);
	 * 
	 * //pmOrgProfile.orgTypeCode PmParameter pmParameter = new PmParameter();
	 * pmParameter.setParameterId(166L);
	 * pmOrgProfile.setOrgTypeCode(pmParameter);
	 * pmOrgProfile.setBrandNew(false); pmUserOrg.setPmOrgProfile(pmOrgProfile);
	 * 
	 * //pmUserOrg.pmUserRole.pmRole PmRole pmRole = new PmRole();
	 * pmRole.setRoleCode("BUDGET_APPROVER");
	 * 
	 * PmRole pmRole2 = new PmRole(); pmRole2.setRoleCode("CD_OFFICER");
	 * 
	 * //pmUserOrg.pmUserRole PmUserRole pmUserRole = new PmUserRole();
	 * pmUserRole.setPmRole(pmRole);
	 * 
	 * PmUserRole pmUserRole2 = new PmUserRole();
	 * pmUserRole2.setPmRole(pmRole2);
	 * 
	 * pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
	 * 
	 * pmUserOrg.addPmUserRole(pmUserRole);
	 * pmUserOrg.addPmUserRole(pmUserRole2);
	 * 
	 * pmUser.addPmUserOrg(pmUserOrg);
	 * 
	 * 
	 * }else{ //ADD PmUserOrg tempUserOrg = pmUser.getPmUserOrgs().get(0);
	 * PmUserRole pmUserRole = new PmUserRole(); //pmUserOrg.pmUserRole.pmRole
	 * PmRole pmRole = new PmRole(); pmRole.setRoleCode("CD_COMMITTEE");
	 * pmUserRole.setPmRole(pmRole); tempUserOrg.setPmUserRoles(new
	 * ArrayList<PmUserRole>()); tempUserOrg.addPmUserRole(pmUserRole);
	 * 
	 * }
	 * 
	 * }else{ pmUser.getSalutationCode().setSalutationId(454L);
	 * pmUser.getIdentificationCode().setParameterId(70L);
	 * pmUser.setLoginId(pmUser.getIdentificationNo());
	 * pmUser.setDesignation("test"); pmUser.setGrade("test");
	 * pmUser.setFaxNo("1234567890"); pmUser.setMobileNo("1234567890");
	 * pmUser.setPhoneNo("1234567890"); pmUser.setEmail(pmUser.getUserName() +
	 * "@test.com"); pmUser.getOrgTypeCode().setParameterId(166L);
	 * 
	 * pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>()); //pmUserOrg PmUserOrg
	 * pmUserOrg = new PmUserOrg();
	 * 
	 * 
	 * //pmUserOrg.pmOrgProfile PmOrgProfile pmOrgProfile = new PmOrgProfile();
	 * pmOrgProfile.setOrgProfileId(1L);
	 * 
	 * //pmOrgProfile.orgTypeCode PmParameter pmParameter = new PmParameter();
	 * pmParameter.setParameterId(166L);
	 * pmOrgProfile.setOrgTypeCode(pmParameter);
	 * pmOrgProfile.setBrandNew(false); pmUserOrg.setPmOrgProfile(pmOrgProfile);
	 * pmUserOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
	 * //pmUserOrg.pmUserRole.pmRole PmRole pmRole1 = new PmRole();
	 * pmRole1.setRoleCode("BUDGET_APPROVER");
	 * 
	 * PmRole pmRole2 = new PmRole(); pmRole2.setRoleCode("CD_OFFICER");
	 * 
	 * //pmUserOrg.pmUserRole PmUserRole pmUserRole1 = new PmUserRole();
	 * pmUserRole1.setPmRole(pmRole1);
	 * 
	 * PmUserRole pmUserRole2 = new PmUserRole();
	 * pmUserRole2.setPmRole(pmRole2);
	 * 
	 * pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
	 * 
	 * pmUserOrg.addPmUserRole(pmUserRole1);
	 * pmUserOrg.addPmUserRole(pmUserRole2);
	 * 
	 * pmUser.addPmUserOrg(pmUserOrg);
	 * 
	 * }
	 * 
	 * 
	 * }
	 * 
	 * 
	 * private void dummyTestUpdateRole(UserReqDTO reqDTO){ //ADD List
	 * <PmUserRole> newUserRoles = new ArrayList<PmUserRole>(); PmUserRole
	 * pmUserRole1 = new PmUserRole(); PmRole pmRole1 = new PmRole();
	 * pmRole1.setRoleCode("CD_COMMITTEE"); pmUserRole1.setPmRole(pmRole1);
	 * newUserRoles.add(pmUserRole1);
	 * 
	 * List <PmUserRole> delUserRoles = new ArrayList<PmUserRole>(); PmUserRole
	 * pmUserRole2 = new PmUserRole(); PmRole pmRole2 = new PmRole();
	 * pmRole2.setRoleCode("BUDGET_APPROVER"); pmUserRole2.setPmRole(pmRole2);
	 * delUserRoles.add(pmUserRole2);
	 * 
	 * reqDTO.setNewRoleList(newUserRoles);
	 * reqDTO.setRemoveRoleList(delUserRoles); }
	 */
	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		logger.info("getOrgTypeDropdownValues");
		ISearchBean<PmParameterDesc> searchBean = null;
		if (getManagedBean().getSearchOrgCategory() == FEDERAL_GOVT) {
			searchBean = getUserOrgTypeSearchBean("somFederalOrgType");
		} else if (getManagedBean().getSearchOrgCategory() == OTHERS) {
			searchBean = getUserOrgTypeSearchBean("somOtherOrgType");			
		}
		
//		if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())
//				|| getFacesScopes().hasRoleUser(RoleCode.PROCUREMENT_ADMIN.value())) {
//			if (this.getManagedBean().getCurrentActiveOrg() != null) {
//				SearchFilterBean orgTypeBean = new SearchFilterBean();
//				orgTypeBean
//						.setSearchOperator(SearchOperator.OPERATOR_EQUALS
//								.value());
//				orgTypeBean.setSearchField("pmParameter.parameterCode");
//				orgTypeBean.setSearchValue(this.getManagedBean()
//						.getCurrentActiveOrg().getOrgCode());
//				searchBean.getDataFilterBeans().add(orgTypeBean);
//			}
//		}

		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
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
			if(getManagedBean().getSelectedUserOrg() != null 
					&& getManagedBean().getSelectedUserOrg().isBrandNew()){
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
				bean.setSearchField("pmParameter.parameterCode");
				bean.setSearchValue(new ArrayList<String>(Arrays
						.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE)));
				searchBean.getDataFilterBeans().add(bean);
			}
		}
		
//		if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value()) 
//				|| getFacesScopes().hasRoleUser(RoleCode.PROCUREMENT_ADMIN.value())) {
//			if (this.getManagedBean().getCurrentActiveOrg() != null) {
//				SearchFilterBean orgTypeBean = new SearchFilterBean();
//				orgTypeBean
//						.setSearchOperator(SearchOperator.OPERATOR_EQUALS
//								.value());
//				orgTypeBean.setSearchField("pmParameter.parameterCode");
//				orgTypeBean.setSearchValue(this.getManagedBean()
//						.getCurrentActiveOrg().getOrgCode());
//				searchBean.getDataFilterBeans().add(orgTypeBean);
//			}
//		}

		SelectItem[] items = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}

		return items;
	}

	private ISearchBean<PmParameterDesc> getUserOrgTypeSearchBean(
			String componentId) {
		logger.info("getUserOrgTypeSearchBean+componentId=" + componentId);
		ISearchBean<PmParameterDesc> userOrgTypeSearchBean = super
				.createSearchBean(PmParameterDesc.class, componentId);
		return userOrgTypeSearchBean;
	}
	
	public String retrieveOrgName(PmOrgProfile orgProfile){
		String orgName = null;
		if(orgProfile != null){
			if(ParameterConst.ORG_TYPE_SUPPLIER.equals(orgProfile.getOrgTypeCode().getParameterCode())){
				orgName = orgProfile.getSmSupplier().getCompanyName();
			}else if(ParameterConst.ORG_TYPE_FACTORING.equals(orgProfile.getOrgTypeCode().getParameterCode())){
				orgName = orgProfile.getPmFinancialOrg().getFinOrgName();
			}else{
				orgName = orgProfile.getPmOrgValidity().getOrgName();
			}
		}
		
		return orgName;
	}
	
	public void deleteUserRole(int rowIndex) {
		List<PmUserRole> userRoles = getManagedBean().getSelectedUserOrg().getPmUserRoles();
		PmUserRole removeUserRole = userRoles.get(rowIndex);
		userRoles.remove(removeUserRole);
		getManagedBean().getSelectedUserOrg().setPmUserRoles(userRoles);
		updateReceivingAddressFlag();
		getManagedBean().setRoleTabActiveIndex(0);
	}
	
	public void deleteUserReceivingAddr(int rowIndex){
		List<PmAddressType> addressTypes = getManagedBean().getSelectedReceivingAddrTypes();
		PmAddressType removeAddressType = addressTypes.get(rowIndex);
		addressTypes.remove(removeAddressType);
		getManagedBean().setSelectedReceivingAddrTypes(addressTypes);
		updateReceivingAddressFlag();
		getManagedBean().setRoleTabActiveIndex(1);
	}
	
	public boolean checkActiveReceivingOfficerExist() {
		logger.info("checkReceivingOfficerExist");
		boolean isReceivingOfficer = false;
		if (this.getManagedBean().getSelectedUserOrg().getPmUserRoles() != null
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles()
						.isEmpty()) {
			for (PmUserRole userRole : this.getManagedBean()
					.getSelectedUserOrg().getPmUserRoles()) {
				if (ProfileManagementConst.RECEIVING_OFFICER.equals(userRole
						.getPmRole().getRoleCode()) && DbValueConst.RECORD_STATUS_ACTIVE == userRole.getRecordStatusFlg()) {					
					isReceivingOfficer = true;
					break;
				}
			}
		} 		
		return isReceivingOfficer;
	}
	
	public void notifySmsChangeListener(){
		 System.out.println("...........notifySmsChangeListener............");
		 
		 if(getManagedBean().getSelectedUser().isNotifyBySms() 
				 && StringUtils.isBlank(getManagedBean().getSelectedUserOrg().getMobileNo())){
			 getManagedBean().getSelectedUser().setNotifyBySms(false); 
			 throw new EpException(MessageConst.ERROR_770090);
		 }
		 if(getManagedBean().getSelectedUser().isNotifyBySms() 
				 && getManagedBean().getSelectedUserOrg().getMobileTelcoId() == null){
			 getManagedBean().getSelectedUser().setNotifyBySms(false); 
			 throw new EpException(MessageConst.ERROR_770250);
		 }
		this.getManagedBean().getSelectedUser().setModified(true);	
	}
	
	private String getRoleCode(){
		String roleCode = null;
		if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
			roleCode = RoleCode.EP_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value())){
			roleCode = RoleCode.EP_ADMIN_1.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value())){
			roleCode = RoleCode.EP_ADMIN_2.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
			roleCode = RoleCode.MINISTRY_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
			roleCode = RoleCode.JABATAN_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
			roleCode = RoleCode.PTJ_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())){
			roleCode = RoleCode.BPK_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())){
			roleCode = RoleCode.UPS_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.PROCUREMENT_ADMIN.value())){
			roleCode = RoleCode.PROCUREMENT_ADMIN.value();
		}else if(getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())){
			roleCode = RoleCode.SUPPORT_ADMIN.value();
		}
		
		return roleCode;
	}
	
	// Org Type and Org Name will be defaulted to the admin's organisation
	// Only applicable for PTJ Admin,BPK Admin and Support Admin
	private void retrieveOrgTypeAndOrgName(){
		if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value()) || 
				getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value()) ||
				getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value()) || 
				getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value()) ){
			if(getManagedBean().getCurrentActiveOrg() != null){
				getManagedBean().setSelectedOrgType(getManagedBean().getCurrentActiveOrg().getOrgTypeCode());
				getManagedBean().setSelectedOrgProfile(getManagedBean().getCurrentActiveOrg());
				getManagedBean().setDisabledOrgFields(true);
				if(getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())){
					this.getManagedBean().setSearchOrgCategory(OTHERS);
				}
			}			
		}else{
			getManagedBean().setDisabledOrgFields(false);
		}
	}
	
	
	public String getRoleDesc(String roleCode){
		if(StringUtils.isNotBlank(roleCode)){
			return RoleHelper.getInstance().getRoleDescByCode(roleCode).getRoleName();
		}else{
			return null;
		}
		
	}
	
	public void searchRoleMatrix(){
		IRoleSO roleSO = null;		
		UserProfilesSearchBean searchBean = getManagedBean().getUserProfileSearchBean();
		if(searchBean.getSelectedRoleOptionSearch() == null && StringUtils.isNotBlank(searchBean.getUserRoleNameSearch())){
			throw new EpException("Please select the role options");
		}
		
		try {
			ISearchBean<PmRoleDesc> searchBeanData =  new SearchBean<PmRoleDesc>();
			
			if(StringUtils.isNotBlank(searchBean.getUserRoleNameSearch())){
				searchBeanData.getDataFilterBeans().add(
					new SearchFilterBean("roleName",
							SearchOperator.OPERATOR_LIKE.value(),
							searchBean.getUserRoleNameSearch(),true));
			}
			
			
			RoleReqDTO roleReqDTO = new RoleReqDTO();
			roleReqDTO.setLanguage(getFacesScopes().getUserLocale()
					.getLanguage());

			roleReqDTO.setSearchRoleBean(searchBeanData);
			
			roleReqDTO.setLanguage(getFacesScopes().getUserLocale()
					.getLanguage());
			roleReqDTO.setRoleCode(getRoleCode());
			roleReqDTO.setOrgTypeId(getManagedBean().getSelectedOrgType().getParameterId());
			roleReqDTO.setBpk(true);
			roleSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IRoleSO.class);
			RoleResDTO resDto = roleSO
					.retrieveRoleDecsFromRoleMatrix(roleReqDTO);

			if (resDto != null && resDto.isSuccessful()) {
				getManagedBean().setRoleDescResults(resDto.getSearchRoleResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			roleSO = null;
		}
	}
	
	public void resetRoleSearchAction(){
		getManagedBean().getUserProfileSearchBean().setSelectedRoleOptionSearch(null);
		getManagedBean().getUserProfileSearchBean().setUserRoleNameSearch(null);
		getManagedBean().getRoleDescResults().clear();
		searchRoleMatrix();
	}
	
	public void populateToRoleSaveObject(){
		if(getManagedBean().getSelectedRoleDesc() != null){
			getManagedBean()
			.getUserProfileSearchBean().setUserRole(getManagedBean().getSelectedRoleDesc().getPmRole().getRoleCode());
		}
	}

}
