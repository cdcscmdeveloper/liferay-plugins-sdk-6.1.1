package my.ep.web.backingbean.pm.userprofile;

import java.sql.Timestamp;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.userprofile.UserprofilesManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "userprofilesBackingBean")
@RequestScoped
public class UserprofilesBackingBean extends
		BaseBackingBean<UserprofilesManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(UserprofilesBackingBean.class);

	private IPagingDataModel<PmUser> userProfileDataModel = new UserProfilePagingDataModel();

	// reset search detail
	public void reset() {
	//	RequestContext.getCurrentInstance().reset("f:searchProfile");
		this.getManagedBean().setUserNames(null);
		this.getManagedBean().setUserRole(null);
		this.getManagedBean().setUserRole(null);
		this.getManagedBean().setUserStatus(false);


	}

	/**
	 * retrive pmuser
	 * 
	 * @return
	 */
	public void retriveUserProfile() {
		UserReqDTO userReqDTO = new UserReqDTO();
		SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		searchBean.setNavigatable(false);
		UserResDTO resDto = null;

		if (getManagedBean().getUserNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserNames())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean().getUserNames());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

		}

		/*
		 * if (getManagedBean().getUserRole() != null) { SearchFilterBean
		 * userRoleSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * userRoleSearchBean.setSearchField("scCommittee.createdDate");
		 * userRoleSearchBean.setSearchValue(getManagedBean().getUserRole());
		 * searchBean.getDataFilterBeans().add(userRoleSearchBean); }
		 * 
		 * if (getManagedBean().getUserorganizationNames() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getUserorganizationNames())) {
		 * SearchFilterBean UserOrgNamesSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * UserOrgNamesSearchBean.setSearchField("scQt.qtNo");
		 * UserOrgNamesSearchBean
		 * .setSearchValue(getManagedBean().getUserorganizationNames());
		 * 
		 * searchBean.getDataFilterBeans().add(UserOrgNamesSearchBean); } if
		 * (getManagedBean().getUserStatus() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getUserStatus())) {
		 * 
		 * SearchFilterBean statusSearchBean = new SearchFilterBean(
		 * SearchOperator.OPERATOR_EQUALS.value());
		 * statusSearchBean.setSearchField("scQt.qtNo");
		 * statusSearchBean.setSearchValue(getManagedBean().getQtNo());
		 * 
		 * searchBean.getDataFilterBeans().add(statusSearchBean);
		 * 
		 * }
		 */
		userReqDTO.setUserSearchBean(searchBean);

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setUsers(
						resDto.getUserResultBean().getResultList());
			}
			System.out.println("#########################################"
					+ resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * retrive pmusergroup
	 * 
	 * @return
	 */
	public void retriveUserGroupProfile() {
		UserReqDTO userReqDTO = new UserReqDTO();
		SearchBean<PmUserGroup> searchBean = new SearchBean<PmUserGroup>();
		searchBean.setNavigatable(false);
		UserResDTO resDto = null;

		if (getManagedBean().getUserGroupNames() != null
				&& !"".equalsIgnoreCase(getManagedBean().getUserGroupNames())) {
			SearchFilterBean usernameSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			usernameSearchBean.setSearchField("userName");
			usernameSearchBean.setSearchValue(getManagedBean()
					.getUserGroupNames());
			searchBean.getDataFilterBeans().add(usernameSearchBean);

		}

		userReqDTO.setUserGroupSearchBean(searchBean);

		try {
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDto = userSO.searchUserProfile(userReqDTO);
			if (resDto != null) {
				this.getManagedBean().setUsers(
						resDto.getUserResultBean().getResultList());
			}
			System.out.println("#########################################"
					+ resDto.getUserResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public UserprofilesBackingBean() {

		retriveUserDtls();
		retrievePmStateList();
		retrievePmDistrictList();
		retrievePmCityList();
		retrievePmDivisionList();
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	private void retriveUserDtls() {
		FacesScopes facesScopes = new FacesScopes();
		PmUser user = facesScopes.getUserSessionBean();
		if (user != null && null != user.getUserId()) {
			this.getManagedBean().setUser(user);
			List<PmAddressType> addressType = user.getPmAddressTypes();

			if (addressType != null) {
				for (PmAddressType addressType2 : addressType) {
					if (addressType2.getAddressType().equalsIgnoreCase("C")) {
						this.getManagedBean().setPmAddress(
								addressType2.getPmAddress());
						logger.info(addressType2.getPmAddress());

						break;
					}
				}
			}
		} else {
			logger.debug("Unable to get the userDetails");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to Retrive User Details", ""));
		}
	}

	private class UserProfilePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public UserProfilePagingDataModel() {
			super("searchProfile");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public IPagingDataModel<PmUser> getUserProfileDataModel() {
		return userProfileDataModel;
	}

	public void setUserProfileDataModel(
			IPagingDataModel<PmUser> userProfileDataModel) {
		this.userProfileDataModel = userProfileDataModel;

	}

	public String goToMaintainceAction(PmUser user) {
		this.getManagedBean().setUser(user);	
		return "/xhtml/pm/profilemanagement/userProfileCreation.xhtml";
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		FacesMessage msg = new FacesMessage("Succesful", event.getFile()
				.getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	public List<PmCountry> retrieveCountryList() {
		ICountrySO countrySO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = countrySO.retrieveCountryList();
		return resDTO.getCountryList();

	}

	public void retrievePmStateList() {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);

		this.getManagedBean().setStates(resDTO.getStateList());
	}

	public void retrievePmDistrictList() {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(1l);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);

		this.getManagedBean().setDistricts(resDTO.getDistrictList());
	}

	public void retrievePmCityList() {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(1L);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);

		this.getManagedBean().setCitys(resDTO.getCityList());
	}

	public void retrievePmDivisionList() {
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(14L);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);

		this.getManagedBean().setDivisions(resDTO.getDivisionList());
	}

	/**
	 * Method to save the user details
	 */
	public String saveUPDetailsAction() {
		debugLogger("saveUPDetailsAction");

		IUserSO iUserSO = null;

		iUserSO = BusinessDelegate.getInstance().getServiceObject("pm",
				IUserSO.class);

		PmUser user = this.getManagedBean().getUser();
		UserReqDTO reqDTO = new UserReqDTO();
		user.setSalutationCode(null);
		IParameterSO iParameterSO=BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reParameterReqDTO= new ParameterReqDTO();
		reParameterReqDTO.setParameterId(166l);
		ParameterResDTO parameterResDTO = iParameterSO.retrieveParameterById(reParameterReqDTO);
		user.setOrgTypeCode(parameterResDTO.getPmParameter());
		
		user.setChangedDate(new Timestamp(System.currentTimeMillis()));
		reqDTO.setPmUser(user);
		iUserSO.saveUser(reqDTO);
		return null;
	}
	
	
	public String createUserDetails(){
		
		return "userProfileCreation.xhtml?faces-redirect=true";
		
	}
}
