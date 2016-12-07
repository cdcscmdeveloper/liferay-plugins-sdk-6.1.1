/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.custservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.pm.custservice.CustServiceManagedBean;
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
		return recommendList;

	}

	/**
	 * To search user with data model
	 */
	public void searchUser() {
		if(getManagedBean().getSearchBean().getSelectedOrgType() == null &&
				getManagedBean().getSearchBean().getSelectedOrgProfile() == null &&
				StringUtils.isBlank(getManagedBean().getSearchBean().getUserName()) &&
				StringUtils.isBlank(getManagedBean().getSearchBean().getIdentificationNo())){
			throw new EpException(MessageConst.ERROR_770049);
		}
		
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
							SearchOperator.OPERATOR_EQUALS.value(),
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
				new SearchOrderBean("createdDate", false));

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
	
	

}
