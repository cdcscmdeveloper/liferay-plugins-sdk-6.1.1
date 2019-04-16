/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.custservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.pm.custservice.CustServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * Backing Bean for Pm Customer Service
 * 
 * @author ken
 * @since 2/12/2016
 * 
 */
@ManagedBean(name = "custServiceBackingBean")
@ViewScoped
public class CustServiceBackingBean extends
		BaseBackingBean<CustServiceManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CustServiceBackingBean.class);

	// select one menu - org type
	private static final String orgTypeId = "somCustServiceOrgType";

	// Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;

	// Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/custservice/search/custServiceMain.xhtml?faces-redirect=true";
	public static final String NAV_TO_INFO = "/xhtml/pm/custservice/userDetails/custServiceInfoMain.xhtml";
	
	// Data Model
	private IPagingDataModel<PmUserOrg> custSvcDataModel = new CustSvcDataModel();

	public CustServiceBackingBean() {

	}

	/**
	 * This method used for reset the search bean field and navigate to the
	 * search main page.
	 * 
	 **/
	public void preRenderView() {
		log.info("preRenderView");
		renewSesionObject();
		resetUserDataModel();
		retrieveOrgTypeMap();

		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);

	}

	/**
	 * retrieve certains org type into mapper
	 */
	private void retrieveOrgTypeMap() {
		log.info("....retrieveOrgTypeMap....");
		List<PmParameterDesc> parameterDescs = PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.ORGANISATION_TYPES, null,
						getFilteredOrgTypes());

		setOrgTypeMapper(new HashMap<Long, Object>());
		for (PmParameterDesc pmDesc : parameterDescs) {
			getOrgTypeMapper().put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, orgTypeId);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(),
						getFilteredOrgTypes()));

		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}

		return items;
	}

	/**
	 * Change Org Type From Selection/Drop Down List
	 */
	public void changeOrgType() {
		log.info("changeResOrgType");
		if (this.getManagedBean().getSearchBean().getSelectedOrgType() != null) {
			PmParameter pmParameter = (PmParameter) getOrgTypeMapper().get(
					this.getManagedBean().getSearchBean().getSelectedOrgType());
			PmOrgProfile orgProfile = new PmOrgProfile();
			orgProfile.setPmOrgValidity(new PmOrgValidity());
			getManagedBean().getSearchBean().setSelectedOrgProfile(orgProfile);
			getManagedBean().setSelectedOrgTypeParam(pmParameter);
		}
	}

	/**
	 * 
	 * @param orgProfile
	 */
	public void selectPtjAction(PmOrgProfile orgProfile) {
		this.getManagedBean().getSearchBean().setSelectedOrgProfile(orgProfile);
	}

	/**
	 * To construct condition fields for LOV
	 * 
	 * @return String
	 */
	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getSearchBean().getSelectedOrgType());
		sb.append("|orgTypeCode.parameterType^");
		sb.append(ParameterTypeConst.ORGANISATION_TYPES);
		sb.append("|recordStatus^1");
		getManagedBean().getSearchBean().getSelectedOrgProfile().setSmSupplier(new SmSupplier());
		
		return sb.toString();
	}



	/**
	 * get the param code list for customer service org types.
	 * 
	 * @return List<String>
	 */
	private List<String> getFilteredOrgTypes() {
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(OrganizationType.MINISTRY.value());
		recommendList.add(OrganizationType.DEPARTMENT.value());
		recommendList.add(OrganizationType.DIVISION.value());
		recommendList.add(OrganizationType.SUPPLIER.value());
		return recommendList;

	}

	/**
	 * To search user with data model
	 */
	public void searchUser() {
		log.info("Invoked searchUser");
		if(getManagedBean().getSearchBean().getSelectedOrgType() == null &&
				getManagedBean().getSearchBean().getSelectedOrgProfile() == null &&
				StringUtils.isBlank(getManagedBean().getSearchBean().getUserName()) &&
				StringUtils.isBlank(getManagedBean().getSearchBean().getIdentificationNo())){
			throw new EpException(MessageConst.ERROR_770049);
		}
		log.info("test");
		getManagedBean().setLazy(true);
		getCustSvcDataModel().reloadData();
	}

	/**
	 * To reset the search panel and result
	 */
	public void resetUserDataModel() {
		getManagedBean().setLazy(false);
		getManagedBean().setSearchBean(new ProfileManagementSearchBean());
		getManagedBean().setSelectedOrgTypeParam(new PmParameter());
		setCustSvcDataModel(new CustSvcDataModel()); 
	}

	private class CustSvcDataModel extends PagingDataModel<PmUserOrg> {

		private static final long serialVersionUID = 1L;

		public CustSvcDataModel() {
			super("custSvcTb",getManagedBean().isLazy());
		}

		public ISearchResultBean<PmUserOrg> pageChangeListener(
				ISearchBean<PmUserOrg> searchBean) {
			IUserSO userSO;
			try {
				ISearchBean<PmUserOrg> searchBeanData = getSearchBeanData(searchBean);
				UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
				userOrgReqDTO.setUserOrgSearchBean(searchBeanData);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserOrgResDTO resDto = userSO
						.searchUserAndUserOrgProfile(userOrgReqDTO);
				if (resDto != null && resDto.isSuccessful()) {
					return resDto.getUserOrgResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	/**
	 * get search bean
	 * 
	 * @return ISearchBean<PmUserOrg>
	 */
	private ISearchBean<PmUserOrg> getSearchBeanData(
			ISearchBean<PmUserOrg> searchBean) {
		ProfileManagementSearchBean pmSearchBean = getManagedBean()
				.getSearchBean();

		// org profile id
		if (pmSearchBean.getSelectedOrgProfile() != null
				&& pmSearchBean.getSelectedOrgProfile().getOrgProfileId() != null) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgProfile.orgProfileId",
							SearchOperator.OPERATOR_EQUALS.value(),
							pmSearchBean.getSelectedOrgProfile()
									.getOrgProfileId()));
		}else{
			// org type(s)
			if (pmSearchBean.getSelectedOrgType() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(),
								pmSearchBean.getSelectedOrgType()));
			} else {
				List<Long> orgTypeIds = new ArrayList<Long>();
				for (Long key : getOrgTypeMapper().keySet()) {
					orgTypeIds.add(key);
				}
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_IN.value(), orgTypeIds));

			}
		}		

		// IdentificationNO
		if (StringUtils.isNotBlank(pmSearchBean.getIdentificationNo())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUser.identificationNo",
							SearchOperator.OPERATOR_EQUALS.value(),
							pmSearchBean.getIdentificationNo(),
							true));
		}
		
		// User Name
		if (StringUtils.isNotBlank(pmSearchBean.getUserName())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmUser.userName",
							SearchOperator.OPERATOR_LIKE.value(),
							pmSearchBean.getUserName(),
							true));
		}
		
		// recordStatusFlg 1 and 0
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatusFlg",
						SearchOperator.OPERATOR_IN.value(),
						new ArrayList<Integer>(Arrays.asList(
								DbValueConst.RECORD_STATUS_ACTIVE,
								DbValueConst.RECORD_STATUS_SUSPENDED))));

		searchBean.getSortColumns().add(
				new SearchOrderBean("pmUser.userName"));

		return searchBean;
	}

	/**
	 * @return the custSvcDataModel
	 */
	public IPagingDataModel<PmUserOrg> getCustSvcDataModel() {
		return custSvcDataModel;
	}

	/**
	 * @param custSvcDataModel
	 *            the custSvcDataModel to set
	 */
	public void setCustSvcDataModel(IPagingDataModel<PmUserOrg> custSvcDataModel) {
		this.custSvcDataModel = custSvcDataModel;
	}

	/**
	 * @return the orgTypeMapper
	 */
	public static Map<Long, Object> getOrgTypeMapper() {
		return orgTypeMapper;
	}

	/**
	 * @param orgTypeMapper
	 *            the orgTypeMapper to set
	 */
	public static void setOrgTypeMapper(Map<Long, Object> orgTypeMapper) {
		CustServiceBackingBean.orgTypeMapper = orgTypeMapper;
	}

	/**
	 * @return the orgtypeid
	 */
	public static String getOrgtypeid() {
		return orgTypeId;
	}
	
	/**
	 * Method to get user roles for pop up
	 * 
	 * @param userOrg
	 */
	public void getListOfAssignedRoles(PmUserOrg userOrg) {
		log.info("getListOfAssignedRoles");
		if (userOrg != null) {
			this.getManagedBean().setSelectedRoleList(
					new ArrayList<PmUserRole>());
			for (PmUserRole pmUserRole : userOrg.getPmUserRoles()) {
				this.getManagedBean().getSelectedRoleList().add(pmUserRole);
				
			}
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
		log.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCode(roleCode);
	}
	
	/**
	 * Redirect to user info page
	 * 
	 * @param userOrg
	 * @return String
	 */
	public String redirectToUserInfo(PmUserOrg userOrg){
		log.info("redirectToUserInfo");
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setSelectedUserOrg(userOrg);
		getManagedBean().setFlUserGroups(retrieveUserGroup(userOrg.getPmUser().getUserId(), RoleCode.FL_APPROVER.value()));
		getManagedBean().setRnUserGroups(retrieveUserGroup(userOrg.getPmUser().getUserId(), RoleCode.RN_APPROVER.value()));
		getListOfAssignedRoles(userOrg);
		return NAV_TO_INFO;
	}
	
	/**
	 * To retrieve active list of user group (RN/FL)
	 * 
	 * @param userId
	 * @param roleCode
	 * @return List<PmUserGroup>
	 */
	private List<PmUserGroup> retrieveUserGroup(Long userId,String roleCode) {
		log.info("userActiveUserGroup");
		SearchBean<PmUserGroup> searchBean = new SearchBean<PmUserGroup>();
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("roleCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						roleCode));
		return PmCommonUtil.retrieveUserActiveUserGroup(userId,searchBean);
	}
	
	/**
	 * This method used to set the data table lazy loading to true
	 * 
	 **/
	public String backToSearch() {
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setSelectedUserOrg(null);
		getManagedBean().setFlUserGroups(null);
		getManagedBean().setRnUserGroups(null);
		this.getManagedBean().setSelectedRoleList(null);
		return "/xhtml/pm/custservice/search/custServiceMain.xhtml";
	}
	
	public void resendNotification() throws Exception {
		UserReqDTO reqDTO = null;
		IUserSO userSO = null;
		PmUser pmUser = null;
		Long userId = null;
		try {
			userId = this.getManagedBean().getSelectedUserOrg().getPmUser().getUserId();
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
			log.info("Tracking diary for user profile: reset password :saveUserProfile");
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
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params, MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}
	
	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() throws Exception {
		log.info("saveUserProfile");

		UserReqDTO reqDTO = null;
		IUserSO userSO = null;
		PmUser user = null;
		PmUserOrg userOrg = null;
		try {
			userOrg = this.getManagedBean().getSelectedUserOrg();
			user = userOrg.getPmUser();
			if(user.getSalutationCode() != null){
				if(user.getSalutationCode().getSalutationId() == null){
					user.setSalutationCode(null);
				}
			}

			reqDTO = new UserReqDTO();
			reqDTO.setServerName(getFacesScopes().getRequest().getServerName());
			reqDTO.setServerPort(getFacesScopes().getRequest().getServerPort());
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());

			loadUserOrgType(user, userOrg);
			List<PmUserOrg> userOrgList = new ArrayList<PmUserOrg>();
			Long loginSessionUserId = getFacesScopes().getUserSessionBean().getUserId();
			userOrg.setChangedBy(loginSessionUserId);
			userOrgList.add(userOrg);
			user.setPmUserOrgs(userOrgList);
			reqDTO.setNewRoleList(new ArrayList<PmUserRole>());
			reqDTO.setRemoveRoleList(new ArrayList<PmUserRole>());
			loadUserRoles(user, reqDTO);
			user.setChangedBy(loginSessionUserId);
			checkChangesAndFillTrackingDiary(user, userOrg, reqDTO);	
			
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
			log.info("deactivateRequired : " + deactivateRequired);
			reqDTO.setDeactivateRequired(deactivateRequired);
			
			if(deactivateRequired){
				fillUserInfoFromUserOrg(user, userOrg);
			}
			
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			reqDTO.setPmUser(user);
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());

			if (ParameterConst.ORG_TYPE_FACTORING.equals(user.getOrgTypeCode().getParameterCode())) {
				reqDTO.setFactoringUser(true);
			}

			if (ParameterConst.ORG_TYPE_SUPPLIER.equals(user.getOrgTypeCode().getParameterCode())) {
				reqDTO.setSupplierUser(true);
			}
			
			UserResDTO resDTO = userSO.saveUserAndUserOrg(reqDTO);
			log.info("save user -END");
			if (resDTO.isSuccessful()) {

					if (!userOrg.isBrandNew()) {
						loadUserProfile(resDTO.getPmUser().getUserId(), userOrg.getUserOrgId());
					} else {
						loadUserProfile(resDTO.getPmUser().getUserId(), resDTO.getPmUser().getActiveUserOrgs().get(0).getUserOrgId());
					}
			
				if (!StringUtils.isBlank(resDTO.getRemarks())) {
					// OIM user creation successfully, problem with roles.
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, resDTO.getRemarks(), resDTO.getRemarks()));
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
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			// throw new EpException(MessageConst.ERROR_770059, new
			// String[]{e.getMessage(),e.getMessage()});
		} finally {
			userSO = null;
		}		
	}
	
	/**
	 * Validation for PmUser's fields
	 * 
	 * @param user
	 */
	private void validateUser(PmUser user, PmUserOrg pmUserOrg) {
		log.info("validateUser");

		if (StringUtils.isBlank(pmUserOrg.getEmail())) {
			throw new EpException(MessageConst.ERROR_770036);
		}
	}
	
	/**
	 * load user org type with the last PmUserOrgs
	 * 
	 * @param user
	 */
	private void loadUserOrgType(PmUser user, PmUserOrg userOrg) {
		log.info("loadUserOrgType");
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
		log.info("loadUserRoles");
		if (user.getPmUserOrgs() != null && !user.getPmUserOrgs().isEmpty()) {
			List<PmUserRole> newRoleList = reqDTO.getNewRoleList();
			List<PmUserRole> deleteRoleList = reqDTO.getRemoveRoleList();
			long activeUserOrgId = 0;
			boolean isChanged = false;
			log.info("......user.getPmUserOrgs()=" + user.getPmUserOrgs());
			log.info("......user.getPmUserOrgs().size()=" + user.getPmUserOrgs().size());
			for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
				if (pmUserOrg.isBrandNew()) {
					isChanged = true;
				}
				if (pmUserOrg.isBrandNew() || pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					// if (checkEffDate(pmUserOrg)) {
					activeUserOrgId = pmUserOrg.getUserOrgId() != null ? pmUserOrg.getUserOrgId() : 0;
					for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
						if (pmUserRole.isBrandNew() || (isUserRoleModified(pmUserRole)
								&& pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE)) {
							newRoleList.add(pmUserRole);
							log.info("ADD NEW: " + pmUserRole.getPmRole().getRoleCode());
						} else if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED
								&& isUserRoleModified(pmUserRole)) {
							deleteRoleList.add(pmUserRole);
							log.info("DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
						}
					}
					// }
				} else if (pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
					// TODO : remove role.brandNew first!
					log.info("pmUserOrg : RECORD_STATUS_SUSPENDED -> deleteRoleList :");
					for(PmUserRole role : pmUserOrg.getPmUserRoles()){		
						role.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						if (isUserRoleModified(role)) {
							deleteRoleList.add(role);
							log.info("pmUserOrg : RECORD_STATUS_SUSPENDED DELETE OLD: " + role.getPmRole().getRoleCode());
						}
					}
					//deleteRoleList.addAll(pmUserOrg.getPmUserRoles());
				}
			}
			if (activeUserOrgId > 0 || isChanged) {
				// retrieve previous active org list
				// List<PmUserOrg> pmUserOrgs = PmCommonUtil
				// .retrieveActiveOrg(user.getUserId());
				log.info("size " + user.getPmUserOrgs().size());
				for (PmUserOrg pmUserOrg : user.getPmUserOrgs()) {
					if (pmUserOrg.getUserOrgId() != null && pmUserOrg.getUserOrgId() != activeUserOrgId) {
						for (PmUserRole pmUserRole : pmUserOrg.getPmUserRoles()) {
							if (pmUserRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								deleteRoleList.add(pmUserRole);
								log.info("2 DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
							}
						}
					}
				}
				reqDTO.setNewRoleList(newRoleList);
				reqDTO.setRemoveRoleList(deleteRoleList);
				
				if(newRoleList != null){
					for(PmUserRole newRole : newRoleList){
						log.info("newRoleList : " + newRole.getPmRole().getRoleCode() + "|RoleStatus :" + newRole.getPmRole().getRecordStatusFlg());
					}
				}
				
				if(deleteRoleList != null){
					for(PmUserRole deleteRole : deleteRoleList){
						log.info("deleteRoleList : " + deleteRole.getPmRole().getRoleCode() + "|RoleStatus :" + deleteRole.getPmRole().getRecordStatusFlg());
					}
				}
				
			}
		}
	}
	
	private void checkChangesAndFillTrackingDiary(PmUser user, PmUserOrg userOrg, UserReqDTO reqDTO) {

		boolean isProfileDetailsChanged = false;

		String newEmail = user.getEmail();
		String oldEmail = userOrg.getEmail();
		if (oldEmail != null && !oldEmail.equalsIgnoreCase(newEmail)) {
			log.info("oldEmail : " + oldEmail);
			log.info("newEmail : " + newEmail);
			isProfileDetailsChanged = true;
		}

		if (isProfileDetailsChanged) {
			log.info("Tracking diary for user profile updated :saveUserProfile");
			String messageId = MessageConst.TD_80014;
			String[] msgParam = new String[] { user.getUserName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
					getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
		}
/*		if (!this.getManagedBean().getPmUserOrgOriginal().getRecordStatusFlg().equals(userOrg.getRecordStatusFlg())) { comment by sadun
			log.info("Tracking diary for user profile: change user state :saveUserProfile");
			String messageId = userOrg.getRecordStatusFlg() == 0 ? MessageConst.TD_80004 : MessageConst.TD_80005;
			String[] msgParam = new String[] { user.getUserName(), userOrg.getPmOrgProfile().getOrgCode(),
					userOrg.getPmOrgProfile().getOrgName() };
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
					getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
		}*/

		if (userOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {// ????

			for (PmUserRole userRole : reqDTO.getNewRoleList()) {
				//
				if (userRole.isBrandNew()) {

					log.info("Tracking diary for user profile: add new Role :saveUserProfile");
					String messageId = MessageConst.TD_80001;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					log.info("......msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));

				} else if (isUserRoleModified(userRole)) {// the role's activated
					log.info("Tracking diary for user profile: Role is re-activated :saveUserProfile");
					String messageId = MessageConst.TD_80003;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					log.info("..222....msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
				}
			}
/*			for (PmUserRole userRole : reqDTO.getRemoveRoleList()) { //comment by sadun
				if (!userRole.isBrandNew()) {// the role's de-activated
					log.info("Tracking diary for user profile: Role is de-activated :saveUserProfile");

					String messageId = MessageConst.TD_80002;
					String[] msgParam = new String[] { user.getUserName(), getRoleDescByRoleCode(
							userRole.getPmRole().getRoleCode(), CommonConstants.LANGUAGE_CODE_MALAY) };
					log.info("..333....msgParam[1]" + msgParam[1]);
					reqDTO.setTrackingStatus(true);
					reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
							getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));
				}
			}*/

/*			for (PmUserRole userRole : userOrg.getPmUserRoles()) { comment by sadun
				if (userRole.getPmRole().getRoleCode().equals("FL_APPROVER")) {
					// getFromMap
					String oldLetterNo = this.getManagedBean().getRoleCodeToLetterNoMap()
							.get(userRole.getPmRole().getRoleCode());
					String newLetterNo = userRole.getLetterNo();
					if (oldLetterNo != null && !oldLetterNo.isEmpty() && !oldLetterNo.equalsIgnoreCase(newLetterNo)) {
						log.info("Tracking diary for user profile: letterNo is changed :saveUserProfile");
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
						log.info("Tracking diary for user profile: approvalLimit is changed :saveUserProfile");
						String messageId = MessageConst.TD_80006;
						String[] msgParam = new String[] { user.getUserName(), oldApprovalLimit.toString(),
								newApprovalLimit.toString() };
						reqDTO.setTrackingStatus(true);
						reqDTO.getTrackDairyVOs().add(PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam), userOrg.getRecordStatusFlg().longValue()));

					}
				}
			}*/

		}

	}
	
	private void fillUserInfoFromUserOrg(PmUser user, PmUserOrg userOrg) {
		user.setDesignation(userOrg.getDesignation());
		user.setPMP(userOrg.isPMP());
		user.setGrade(userOrg.getGrade());
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
		userOrg.setEmail(user.getEmail());
	}
	
	/**
	 * load pm user object by id
	 * 
	 * @param id
	 */
	private void loadUserProfile(Long userId, Long userOrgId) {
		log.info("loadUserProfile - Start");
		log.info("loadUserProfile - userId=" + userId);
		log.info("loadUserProfile - selectedOrg=" + userOrgId);
		try {
			// initialUserProfile();
			if (userId == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}
			// load user
			// PmUser pmUser = PmCommonUtil.retrieveUserById(id, true);
			PmUserOrg pmUserOrg = PmCommonUtil.retrieveUserOrgByUserIdAndOrgId(userId, userOrgId);
			log.info("pmUserOrg=" + pmUserOrg);
			pmUserOrg.setPMP(pmUserOrg.isPMP() ? true : false);
			PmUser pmUser = pmUserOrg.getPmUser();
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
					this.getManagedBean().getRoleCodeToLetterNoMap().put(userRole.getPmRole().getRoleCode(),
							userRole.getLetterNo());
				}
				if (userRole.getPmRole().isLimitRequired()) {
					this.getManagedBean().getRoleCodeToApprovalLimitMap().put(userRole.getPmRole().getRoleCode(),
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
				log.info("...........attachment list is empty");
				pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());
			}

			// load password
			pmUser.setPassword(null);
			pmUser.setConfirmPassword(null);

			this.getManagedBean().setSelectedUser(pmUser);
	/*		this.getManagedBean().setDeleteUserRoles(null); comment by sadun
			this.getManagedBean().setDeleteReceivingAddrTypes(null);
			this.getManagedBean().setSelectedDeliveringAddrTypes(null);
			this.getManagedBean().setSelectedUserOrg(null);
			this.getManagedBean().setSelectedRoles(null);
			this.getManagedBean().setSelectedReceivingAddrTypes(null);
			this.getManagedBean().setReceivingAddressFlag(false);
			this.getManagedBean().setDeletePmUserAttachments(null);
			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());*/
			// for the initial load
			// if (pmUser.getPmUserOrgs() != null
			// && !pmUser.getPmUserOrgs().isEmpty()) {
			this.getManagedBean().setSelectedUserOrg(pmUserOrg);// TODO
																// retrive
																// userORg
																// with
																// relations
																// again
		/*	showReceivingAddress(); comment by sadun
			this.getManagedBean().setIsNewUser(false);
			// set org category
			if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_FEDERAL_GOVT_UP)
					.contains(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
				getManagedBean().setOrgCategory(FEDERAL_GOVT);
			} else if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_OTHERS_UP)
					.contains(pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode())) {
				getManagedBean().setOrgCategory(OTHERS);
			}*/
			// }
			// addressEntered();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[] { e.getMessage() });
		}
		log.info("loadUserProfile - End");
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
		log.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}
	
	private boolean isUserRoleModified(PmUserRole userRole) {
		log.info("isUserRoleModified");
		if (!userRole.isBrandNew()) {
			PmUserRole currentUserRole = PmCommonUtil.findUserRoleByUserRoleId(userRole.getUserRoleId());
			if (currentUserRole != null && currentUserRole.getRecordStatusFlg() != userRole.getRecordStatusFlg()) {
				log.info("userRoleId :" + userRole.getUserRoleId() + " is modified.");
				return true;
			}
		}
		return false;
	}
	
	public void changeOrgUnit(PmOrgProfile entity) {
		this.getManagedBean().getSearchBean().getSelectedOrgProfile().setSmSupplier(entity.getSmSupplier());
		log.info("changeOrgUnit");
	}
	public String constructConditionFieldsForCreate() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(getManagedBean().getSearchBean().getSelectedOrgProfile().getOrgTypeCode().getParameterCode());
		sb.append("|orgTypeCode.parameterType^");
		sb.append(ParameterTypeConst.ORGANISATION_TYPES);
		sb.append("|recordStatus^1");
		log.info("....getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode()="
				+ getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode());
		log.info("....this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()="
				+ this.getManagedBean().getPmUserOrgOriginal().getPmOrgProfile().getOrgProfileId());
		log.info("....getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()="
				+ getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()));

		sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
				getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode(),
				getManagedBean().getPmUserOrgOriginal().getPmOrgProfile().getOrgProfileId(),
				getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
				getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));
		return sb.toString();
	}
	
	public void updateSearchCriteriaForOrgName(BaseEntity entity) {
		log.info("updateSearchCriteriaForOrgName");

		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getSearchBean().getSelectedOrgProfile().setSmSupplier(orgProfile.getSmSupplier());


	}


}
