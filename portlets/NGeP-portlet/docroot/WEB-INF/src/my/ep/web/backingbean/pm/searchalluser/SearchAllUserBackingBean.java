package my.ep.web.backingbean.pm.searchalluser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.pm.searchalluser.SearchAllUserManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "searchAllUserBackingBean")
@ViewScoped //@RequestScoped
public class SearchAllUserBackingBean extends BaseBackingBean<SearchAllUserManagedBean> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NAV_TO_SEARCH = "/xhtml/pm/searchAllUser/tempSearch.xhtml"; // main page for portlet
	private static final Logger logger = EpLogger
			.getLogger(SearchAllUserBackingBean.class);

	public static final boolean SEARCH_DEFAULT = false;
	private static Map<Long, Object> orgTypeMapper;
	
	// Paging for listing
	private IPagingDataModel<PmUserOrg> userOrgDataModel = new UserProfilePagingDataModel();
	
	/**
	 * @return the orgTypeMapper
	 */
	public static Map<Long, Object> getOrgTypeMapper() {
		return orgTypeMapper;
	}


	/**
	 * @param orgTypeMapper the orgTypeMapper to set
	 */
	public static void setOrgTypeMapper(Map<Long, Object> orgTypeMapper) {
		SearchAllUserBackingBean.orgTypeMapper = orgTypeMapper;
	}


	/**
	 * @return the userOrgDataModel
	 */
	public IPagingDataModel<PmUserOrg> getUserOrgDataModel() {
		return userOrgDataModel;
	}


	/**
	 * @param userOrgDataModel the userOrgDataModel to set
	 */
	public void setUserOrgDataModel(IPagingDataModel<PmUserOrg> userOrgDataModel) {
		this.userOrgDataModel = userOrgDataModel;
	}


	public SearchAllUserBackingBean(){
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	
	}
	
	/**
	 * load clean view for search all user
	 */
	public void preLoadingSearchUser(){
		logger.info("preLoadingUserProfile");
		this.getManagedBean().setLoginId(null);
		this.setCurrentActiveOrg();
		getManagedBean().setLazy(false);
		this.resetUserProfileSearchBean();
		this.retrieveOrgTypeMap();
		this.resetSearchInfo();
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	private void resetSearchInfo(){
		this.getManagedBean().setResultStatus(null);
		this.getManagedBean().setOimRoleList(null);
		this.getManagedBean().setPmRoleList(null);
		this.getManagedBean().setLiferayRoleList(null);
		this.getManagedBean().setResultLoginId(null);
	}
	
	public void tempSearchUserProfile() throws Exception{
		logger.info("tempSearchUserProfile start");
		this.resetSearchInfo();
		
		String loginId = this.getManagedBean().getLoginId();
		
		if(StringUtils.isBlank(loginId)){
			throw new IllegalArgumentException("Login ID is mandatory.");
		}
		
		try{
			UserReqDTO userReqDTO = new UserReqDTO();
			UserResDTO resDto = null;
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);	
			userReqDTO.setLoginId(loginId.toLowerCase());
			userReqDTO.setRoleRequired(true);
			resDto = userSO.isUserExist(userReqDTO);
			if(resDto!=null && resDto.isSuccessful()){
				this.getManagedBean().setResultStatus(resDto.getAllResult());
				this.getManagedBean().setOimRoleList(resDto.getOimRoleNames());
				this.getManagedBean().setPmRoleList(resDto.getPmRoleNames());
				this.getManagedBean().setLiferayRoleList(resDto.getLiferayRoleNames());
				this.getManagedBean().setResultLoginId(loginId);
			}
				
			
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		logger.info("tempSearchUserProfile end");
		
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
		userOrgDataModel = new UserProfilePagingDataModel();
	}
	
	// pagination for user search list
		private class UserProfilePagingDataModel extends PagingDataModel<PmUserOrg> {
			private static final long serialVersionUID = 1L;

			public UserProfilePagingDataModel() {
				super("dtUserAllStatusResult", getManagedBean().isLazy());
			}

			@Override
			protected ISearchResultBean<PmUserOrg> pageChangeListener(
					ISearchBean<PmUserOrg> searchBean) {
				try {
					ISearchBean<PmUserOrg> searchBeanData = getSearchBeanData(searchBean);

					UserOrgReqDTO userOrgReqDTO = new UserOrgReqDTO();
					userOrgReqDTO.setUserOrgSearchBean(searchBeanData);
					IUserSO userSO = BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IUserSO.class);
					UserOrgResDTO resDto = userSO
							.searchUserAllStatus(userOrgReqDTO);
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
		 * The method used for generating a list of available display type of org
		 * type as selection.
		 * 
		 */
		public SelectItem[] getOrgTypeDropdownValues() {
			logger.info("getOrgTypeDropdownValues");
			ISearchBean<PmParameterDesc> searchBean = null;
				searchBean = getUserOrgTypeSearchBean("somOrgType");
			
			SelectItem[] items = null;
			if (searchBean != null) {
				searchBean.getSortColumns();
				items = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			}

			return items;
		}
		
		/**
		 * get search bean
		 * 
		 * @return ISearchBean<PmUser>
		 */
		private ISearchBean<PmUserOrg> getSearchBeanData(
				ISearchBean<PmUserOrg> searchBean) {
			logger.info("getSearchBeanData - Start");
			SearchFilterBean userSearchBean  = null;
			UserProfilesSearchBean upSearchBean = this.getManagedBean()
					.getUserProfileSearchBean();
			
			if (this.getManagedBean().getSelectedOrgType().getParameterId() != null
					&& upSearchBean.getSelectedOrgProfileId() != null) {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmOrgProfile.orgTypeCode.parameterId",
								SearchOperator.OPERATOR_EQUALS.value(), this
										.getManagedBean().getSelectedOrgType()
										.getParameterId()));

				logger.info("+ userOrganizationName "
						+ upSearchBean.getSelectedOrgProfileId());
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(),
								upSearchBean.getSelectedOrgProfileId()));

			} else {
				throw new EpException(MessageConst.ERROR_770163);
			}

			// IdentificationNO
			if (upSearchBean.getIdentificationNo() != null
					&& !"".equalsIgnoreCase(upSearchBean.getIdentificationNo())) {
				logger.info("+ identificationNo "
						+ upSearchBean.getIdentificationNo());
				userSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				userSearchBean.setCaseInsensitive(true);
				userSearchBean.setSearchField("pmUser.identificationNo");
				userSearchBean.setSearchValue(upSearchBean.getIdentificationNo());
				searchBean.getDataFilterBeans().add(userSearchBean);				
			}
			// User Name
			if (upSearchBean.getUserName() != null
					&& !"".equalsIgnoreCase(upSearchBean.getUserName())) {
				logger.info("+ userName " + upSearchBean.getUserName());
				userSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				userSearchBean.setCaseInsensitive(true);
				userSearchBean.setSearchField("pmUser.userName");
				userSearchBean.setSearchValue(upSearchBean.getUserName());
				searchBean.getDataFilterBeans().add(userSearchBean);
			}
			
			// Login id
			if (StringUtils.isNotBlank(upSearchBean.getLoginId())) {
				userSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				userSearchBean.setCaseInsensitive(true);
				userSearchBean.setSearchField("pmUser.loginId");
				userSearchBean.setSearchValue(upSearchBean.getLoginId());
				searchBean.getDataFilterBeans().add(userSearchBean);
			}

			// Status if non provided then select default (active)
			if (upSearchBean.getUserOrgStatus() != null) {
				logger.info("+ status " + upSearchBean.getUserOrgStatus());
				userSearchBean = new SearchFilterBean();
				userSearchBean.setSearchField("recordStatusFlg");
				userSearchBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
						.value());
				userSearchBean.setSearchValue(upSearchBean.getUserOrgStatus());
				searchBean.getDataFilterBeans().add(userSearchBean);
			}
			
			if (upSearchBean.getUserStatus() != null) {
				userSearchBean = new SearchFilterBean();
				userSearchBean.setSearchField("pmUser.recordStatusFlg");
				userSearchBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
						.value());
				userSearchBean.setSearchValue(upSearchBean.getUserStatus());
				searchBean.getDataFilterBeans().add(userSearchBean);
			}
			logger.info("getSearchBeanData - End");
			return searchBean;
		}
		
		private ISearchBean<PmParameterDesc> getUserOrgTypeSearchBean(
				String componentId) {
			logger.info("getUserOrgTypeSearchBean+componentId=" + componentId);
			ISearchBean<PmParameterDesc> userOrgTypeSearchBean = super
					.createSearchBean(PmParameterDesc.class, componentId);
			return userOrgTypeSearchBean;
		}
		
		public void clearOrgNameAndRoleLovSearchFields() {
			logger.info("clearOrgNameAndRoleLovSearchFields");
			// if (this.getManagedBean().getSelectedOrgProfile() != null) {
			// this.getManagedBean().getSelectedOrgProfile()
			// .setPmOrgValidity(new PmOrgValidity());
			// // this.getManagedBean().getSelectedUserOrg()
			// // .setPmUserRoles(new ArrayList<PmUserRole>());
			// }
			this.getManagedBean().setSelectedOrgProfile(null);

			if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
						.getManagedBean().getSelectedOrgType().getParameterId());
				this.getManagedBean().getSelectedOrgType()
						.setParameterCode(pmParameter.getParameterCode());

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
		 * get faces scopes
		 * 
		 * @return FacesScopes
		 */
		private FacesScopes getFacesScopes() {
			FacesScopes facesScopes = new FacesScopes();
			return facesScopes;
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

			if (OrganizationType.MINISTRY.value().equals(
					getManagedBean().getSelectedOrgType().getParameterCode())) {
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					sb.append("|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
				}
			} else if (OrganizationType.PENGAWAI_PENGAWAL.value().equals(
					getManagedBean().getSelectedOrgType().getParameterCode())) {
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					sb.append("|");
					sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.PENGAWAI_PENGAWAL.value(),OrganizationType.MINISTRY.value()));
					sb.append("^");
					sb.append(this.getManagedBean().getCurrentActiveOrg()
							.getOrgProfileId());
				} 
			}else if (OrganizationType.DEPARTMENT.value().equals(
					getManagedBean().getSelectedOrgType().getParameterCode())) {
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					sb.append("|");
					sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
					sb.append("^");
					sb.append(this.getManagedBean().getCurrentActiveOrg()
							.getOrgProfileId());
				} else if (getFacesScopes().hasRoleUser(
						RoleCode.JABATAN_ADMIN.value())) {
					sb.append("|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
				}
			} else if (OrganizationType.DIVISION.value().equals(
					getManagedBean().getSelectedOrgType().getParameterCode())) {
				if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					sb.append("|");				
					sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
					sb.append("^");
					sb.append(this.getManagedBean().getCurrentActiveOrg()
							.getOrgProfileId());
				} else if (getFacesScopes().hasRoleUser(
						RoleCode.JABATAN_ADMIN.value())) {
					sb.append("|");				
					sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
					sb.append("^");
					sb.append(this.getManagedBean().getCurrentActiveOrg()
							.getOrgProfileId());
				} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
					sb.append("|orgProfileId^"
							+ this.getManagedBean().getCurrentActiveOrg()
									.getOrgProfileId());
				}
			}

			return sb.toString();
		}
		
			
		
		/**
		 * search user profiles
		 */
		public void searchUserProfile() {
			if (this.getManagedBean().getUserProfileSearchBean()
					.getSelectedOrgProfileId() == null) {
				// hit error and return
				throw new EpException(MessageConst.ERROR_775116, new String[] {});

			}

			logger.info("searchUserProfile");
			getManagedBean().setLazy(true);
			userOrgDataModel.reloadData();
		}
		
	
	
}
