package my.ep.web.backingbean.pm.userprofile;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.userprofile.UserprofilesManagedBean;
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
	private IPagingDataModel<PmOrgProfile> orgDataModel = new OrgPagingDataModel();
	private IPagingDataModel<PmRoleDesc> searchRoleNameList = new RoleNamesPagingDataModel();
	private IPagingDataModel<PmAddressType> searchReceivingAddress = new userAddressPagingDataModel();
	private IPagingDataModel<PmRoleDesc> searchApproverTypeList = new ApproverNamesPagingDataModel();

	// reset search detail
	public void reset() {
		// RequestContext.getCurrentInstance().reset("f:searchProfile");
		this.getManagedBean().setUserNames(null);
		this.getManagedBean().setUserRole(null);
		this.getManagedBean().setUserStatus(false);

	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}

	public void searchRoleNames() {
		searchRoleNameList.reloadData();
	}
	
	public void searchReceivingAddress() {
		searchReceivingAddress.reloadData();
	}	
	
	public void searchApproverTypeList() {
		searchApproverTypeList.reloadData();
	}	

	/**
	 * search user profiles
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

	public UserprofilesBackingBean() {

//		retriveUserDtls();
		retriveStateList();
	}

	// pagination for user search list
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

	// pagination for org search list
	private class OrgPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public OrgPagingDataModel() {
			super("searchOrgList");
		}

		@Override
		protected ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	// pagination for role search list
	private class RoleNamesPagingDataModel extends PagingDataModel<PmRoleDesc> {
		private static final long serialVersionUID = 1L;

		public RoleNamesPagingDataModel() {
			super("roleReceiving");
		}

		@Override
		public ISearchResultBean<PmRoleDesc> pageChangeListener(
				ISearchBean<PmRoleDesc> searchBean) {
				IRoleSO roleSO;
				RoleResDTO resDto;
				System.out.println("====search by name=====");
				
				try {
					
					RoleReqDTO roleReqDTO = new RoleReqDTO();
					roleReqDTO.setLanguage("en");
					
					roleSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
					resDto = roleSO.searchRoleNameList(roleReqDTO);

					if (resDto != null) {
                   System.out.println("David rolesssssssssssssssssssssssssss"+resDto
                		    .getSearchRoleResultBean().getTotalRecord());
						return resDto.getSearchRoleResultBean();
					}

				} catch (Exception e) {
					e.printStackTrace();
					
				}
				return null;
				
			}

		}

	
	// pagination for user address search list
	private class userAddressPagingDataModel extends PagingDataModel<PmAddressType> {
		private static final long serialVersionUID = 1L;

		public userAddressPagingDataModel() {
			super("receivingAddress");
		}

		public ISearchResultBean<PmAddressType> pageChangeListener(
			ISearchBean<PmAddressType> searchBean) {
			IAddressSO addressSO;
			AddressResDTO resDto;
			System.out.println("====search by====="
					+ getManagedBean().getStateCode());
			try {
				// SearchBean<PmAddressType> searchBean2=searchBean;
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO
						.setSearchAddressBean(new SearchBean<PmAddressType>());
				if (getManagedBean().getStateCode() != null
						&& !getManagedBean().getStateCode().endsWith("")) {
					SearchFilterBean stateSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					stateSearchBean
							.setSearchField("pmAddress.pmState.stateCode");
					stateSearchBean.setSearchValue(getManagedBean()
							.getStateCode());
					searchBean.getDataFilterBeans().add(stateSearchBean);
				}

				if (getManagedBean().getDistrictId() != null) {
					SearchFilterBean districtSearchBean = new SearchFilterBean(
							SearchOperator.OPERATOR_EQUALS.value());
					districtSearchBean
							.setSearchField("pmAddress.pmDistrict.districtId");
					districtSearchBean.setSearchValue(getManagedBean()
							.getDistrictId());
					searchBean.getDataFilterBeans().add(districtSearchBean);
				}
				addressReqDTO.setSearchAddressBean(searchBean);

				addressSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
				resDto = addressSO.searchDeliveryAddress(addressReqDTO);

				if (resDto != null) {

					return resDto.getSearchAddressResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	// pagination for approver type list
	private class ApproverNamesPagingDataModel extends PagingDataModel<PmRoleDesc> {
		private static final long serialVersionUID = 1L;

		public ApproverNamesPagingDataModel() {
			super("approverReceiving");
		}

		@Override
		public ISearchResultBean<PmRoleDesc> pageChangeListener(
				ISearchBean<PmRoleDesc> searchBean) {
				IRoleSO roleSO;
				RoleResDTO resDto;
				System.out.println("====search by approver type=====");
				
				try {
					
					RoleReqDTO roleReqDTO = new RoleReqDTO();
					roleReqDTO.setLanguage("en");
					roleReqDTO.setParentRole("APPROVER");
					
					
					roleSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
					resDto = roleSO.searchRoleNameList(roleReqDTO);

					if (resDto != null) {
                   System.out.println("Approver Type"+resDto
                		    .getSearchRoleResultBean().getTotalRecord());
						return resDto.getSearchRoleResultBean();
					}

				} catch (Exception e) {
					e.printStackTrace();
					
				}
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

	public IPagingDataModel<PmOrgProfile> getOrgDataModel() {
		return orgDataModel;
	}

	public void setOrgDataModel(IPagingDataModel<PmOrgProfile> orgDataModel) {
		this.orgDataModel = orgDataModel;
	}

	public IPagingDataModel<PmRoleDesc> getSearchRoleNameList() {
		return searchRoleNameList;
	}

	public void setSearchRoleNameList(IPagingDataModel<PmRoleDesc> searchRoleNameList) {
		this.searchRoleNameList = searchRoleNameList;
	}

	public IPagingDataModel<PmAddressType> getSearchReceivingAddress() {
		return searchReceivingAddress;
	}

	public void setSearchReceivingAddress(
			IPagingDataModel<PmAddressType> searchReceivingAddress) {
		this.searchReceivingAddress = searchReceivingAddress;
	}	

	public IPagingDataModel<PmRoleDesc> getSearchApproverTypeList() {
		return searchApproverTypeList;
	}

	public void setSearchApproverTypeList(
			IPagingDataModel<PmRoleDesc> searchApproverTypeList) {
		this.searchApproverTypeList = searchApproverTypeList;
	}

	public String backToMainPages() {
		reset();

		return "/xhtml/pm/profilemanagement/profileManagementMain.xhtml";
	}

	public String goToMaintainceAction(PmUser pmUser) {
		this.getManagedBean().setPmUser(pmUser);

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(pmUser.getLoginId());

		IUserSO userSO = null;

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		} catch (Exception e) {
			System.out.println(e.toString());
		}		
		
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);

		PmUser user = resDTO.getPmUser();
		
		this.getManagedBean().setPmAddress(new PmAddress());

		
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

		return "/xhtml/pm/profilemanagement/userProfileEdit.xhtml";
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
	
	public void retriveStateList() {
		IStateSO stateSo = null;
		StateResDTO resDto = null;

		try {
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1L);
			stateSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			resDto = stateSo.retriveStatesListByCountryID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setStateList(resDto.getStateList());
			}

		} finally {
			stateSo = null;
		}

	}

	public void districtBystate(AjaxBehaviorEvent event) {
		IDistrictSO districtSo = null;
		DistrictResDTO resDto = null;
		try {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			Long stateId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (stateId != null) {
				List<PmState> stateList = this.getManagedBean().getStateList();
				for (PmState sate : stateList) {
					if (sate.getStateId().equals(stateId)) {
						this.getManagedBean().setStateCode(sate.getStateCode());

					}
				}
			}

			reqDTO.setStateId(stateId);
			districtSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			resDto = districtSo.retriveDistrictListByStateID(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setDistrictList(resDto.getDistrictList());
			}

		} finally {
			districtSo = null;
		}

	}
	
	public void cityBydistrict(AjaxBehaviorEvent event) {
		ICitySO citySo = null;
		CityResDTO resDto = null;
		try {
			CityReqDTO reqDTO = new CityReqDTO();
			Long districtId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (districtId != null) {
				List<PmDistrict> districtList = this.getManagedBean().getDistrictList();
				for (PmDistrict distr : districtList) {
					if (distr.getDistrictId().equals(districtId)) {
						this.getManagedBean().setDistrictCode(distr.getDistrictCode());

					}
				}
			}

			reqDTO.setDistrictId(districtId);
			citySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			resDto = citySo.retriveCityListByDistrictId(reqDTO);

			if (resDto != null) {
				this.getManagedBean().setCityList(resDto.getCityList());
			}

		} finally {
			citySo = null;
		}

	}	

	public void districtCode(AjaxBehaviorEvent event) {
		try {
			Long districtId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (null != districtId) {
				List<PmDistrict> districtList = this.getManagedBean()
						.getDistrictList();
				for (PmDistrict district : districtList) {
					if (district.getDistrictId().equals(districtId)) {
						this.getManagedBean().setDistrictCode(
								district.getDistrictCode());

					}

				}

			}

		} finally {

		}

	}	
	
	public void cityCode(AjaxBehaviorEvent event) {
		try {
			Long cityId = (Long) event.getComponent().getAttributes()
					.get("value");
			if (null != cityId) {
				List<PmCity> cityList = this.getManagedBean()
						.getCityList();
				for (PmCity city : cityList) {
					if (city.getCityId().equals(cityId)) {
						this.getManagedBean().setCityCode(
								city.getCityCode());

					}

				}

			}

		} finally {

		}

	}		


	/**
	 * Method to update the user details
	 */
	public void saveUPDetailsAction(PmUser pmUser) {
		debugLogger("saveUPDetailsAction");

		pmUser.setPmAddressTypes(null);

		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(this.getManagedBean().getPmUser());

			UserResDTO resDTO = userSO.saveMyProfile(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record save successfully", "User ID: "
										+ resDTO.getPmUser().getUserId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
	}

	public String createUserProfiles() {
        //create user detail
//		PmUser pmUser = new PmUser();
//		PmCountry country = new PmCountry();
//		pmUser.setPmCountry(country);
//		PmParameter parameter = new PmParameter();
//		pmUser.setSalutationCode(parameter);
//		PmParameter parameter2 = new PmParameter();
//		pmUser.setIdentificationCode(parameter2);
//		PmParameter parameter3 = new PmParameter();
//		pmUser.setOrgTypeCode(parameter3);
//		this.getManagedBean().setPmUser(pmUser);
//		
//		//create user address
//		List<PmAddressType> pmAddressTypes = new ArrayList<PmAddressType>();		
//		PmAddressType addressType = new PmAddressType();		
//		addressType.setAddressType("C");		
//		addressType.setPmUser(pmUser);
//		PmAddress pmAddress = new PmAddress();
//		addressType.setPmAddress(pmAddress);
//		pmAddressTypes.add(addressType);
//		pmUser.setPmAddressTypes(pmAddressTypes);
//		this.getManagedBean().setPmUser(pmUser);
		
		return "/xhtml/pm/profilemanagement/userProfileCreation.xhtml";
	}

	// to create new profiles into database
	public void createNewProfiles(PmUser user) {

		IUserSO userSO;

		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(this.getManagedBean().getUser());

			UserResDTO resDTO = userSO.saveMyProfile(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record save successfully", "User ID: "
										+ resDTO.getPmUser().getUserId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
	}

	// to add roles names datatables
	public void addRolesNames() {

		// getManagedBean().setSelectedRolesList(new PmRoleDesc());

		// getManagedBean().getSelectedRolesList().setPmRole(
		// getManagedBean().getRoles());

	}

	/**
	 * To add empty row to org unit data table
	 */
	public void addNewOrgAction() {

		System.out.println("====enter new org details====");

		List<PmOrgProfile> orgUnitList = this.getManagedBean().getOrgUnits();
		if (orgUnitList == null) {
			orgUnitList = new ArrayList<PmOrgProfile>();

		}

		PmOrgProfile orgprofile = new PmOrgProfile();

		orgprofile.setPmOrgValidity(new PmOrgValidity());
		orgUnitList.add(orgprofile);
		this.getManagedBean().setOrgUnits(orgUnitList);
	}

	public void deleteOrgAction() {
		List<PmOrgProfile> orgUnitList = this.getManagedBean().getOrgUnits();

		List<PmOrgProfile> selectData = this.getManagedBean()
				.getDeleteorgUnits();

		if (orgUnitList.size() == selectData.size()) {
			orgUnitList.clear();
		} else {
			for (PmOrgProfile orglist : selectData)
				orgUnitList.remove(orglist);
		}

	}
	
	public void addSelectedRole() {
		debugLogger("addSelectedRole");
		List<PmRoleDesc> roledescList = this.getManagedBean()
				.getSelectedUserRole();
		if (roledescList == null) {
			roledescList = new ArrayList<PmRoleDesc>();
		}
		PmRoleDesc roledesc = new PmRoleDesc();
		this.getManagedBean().setSelectUserRole(roledesc);
		getManagedBean().setSelectedUserRole(roledescList);
	}	
	
	public void acceptRoleListAction() {
		debugLogger("acceptRoleListAction");
		
		List<PmRoleDesc> roleList = getManagedBean()
				.getSelectedUserRole();
	
		if (null == roleList) {
			roleList = new ArrayList<PmRoleDesc>();
		}
		

			PmRoleDesc name = new PmRoleDesc();
			name.setRoleName(getManagedBean().getSelectUserRole()
					.getRoleName());

			roleList.add(name);
		
	}
	
	public void addRecevingAddress() {
		debugLogger("addRecevingAddress");
		List<PmAddressType> addressTypeList = this.getManagedBean()
				.getPmRecevingAddressList();
		if (addressTypeList == null) {
			addressTypeList = new ArrayList<PmAddressType>();
		}
		PmAddressType addressname = new PmAddressType();
		this.getManagedBean().setSelectRecevingAddress(addressname);
		getManagedBean().setPmRecevingAddressList(addressTypeList);
	}	
	
	
	
	
}
