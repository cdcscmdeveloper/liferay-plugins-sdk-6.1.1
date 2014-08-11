package my.ep.web.managedbean.pm.profilepersonalization;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.bean.pm.AddressList;
import my.ep.web.bean.pm.PreferenceList;
import my.ep.web.bean.pm.RoleList;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;


/**
 * @author DK
 *
 */
@ManagedBean(name="profilePersonalizationManagedBean")
@SessionScoped
public class ProfilePersonalizationManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(ProfilePersonalizationManagedBean.class);	
	
	
	private PmUser selectedUser;
	private PmAddress userAddress; //Address Type - C
	private boolean divisionFlagC;
	
	private int selectedTabIndex;
//
	/**
	 * @return the selectedUser
	 */
	public PmUser getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	/**
	 * @return the userAddress
	 */
	public PmAddress getUserAddress() {
		return userAddress;
	}

	/**
	 * @param userAddress the userAddress to set
	 */
	public void setUserAddress(PmAddress userAddress) {
		this.userAddress = userAddress;
	}

	/**
	 * @return the divisionFlagC
	 */
	public boolean isDivisionFlagC() {
		return divisionFlagC;
	}

	/**
	 * @param divisionFlagC the divisionFlagC to set
	 */
	public void setDivisionFlagC(boolean divisionFlagC) {
		this.divisionFlagC = divisionFlagC;
	}
	
	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}


//	enable only for multi delete
//	/**
//	 * @return the deleteOrgUnits
//	 */
//	public List<PmUserOrg> getDeleteOrgUnits() {
//		return deleteOrgUnits;
//	}
//
//	/**
//	 * @param deleteOrgUnits the deleteOrgUnits to set
//	 */
//	public void setDeleteOrgUnits(List<PmUserOrg> deleteOrgUnits) {
//		this.deleteOrgUnits = deleteOrgUnits;
//	}

	







	/**
	 * TO BE VERIFIED
	 */


	private PmUser user;
	
	
	private PmUser pmUser;
	
	private PmCriteria pmCriteria;		

	private PmAddress pmAddress;
	
	private PmAddress cAddress;
	
	private PmAddressType addressType;	
	
	private PmOrgValidity orgValidity;
	
	private PmRole roles;
				
	
	
    // user profile	search	
	private String userNames;
	
	private String userRole;	
	
	private String userorganizationNames;	
	
	private boolean userStatus = false;
	
	private String stateCode;
	
	private String roleNames;
	
	private String language;
	
	private String districtCode;
	
	private String cityCode;
	
	// user group profile
	private String userGroupNames;	
	
	private String userGroupRole;	
	
	private String userGroupOrganizationNames;	
	
	private String userGroupStatus;
		
	private PmState state;
	
	private PmCity city = new PmCity();	
	
	private PmDivision division = new PmDivision();

	private PmDistrict district = new PmDistrict();	

	private boolean isEditable = true;	
	
	private List<PmUser> users;
	
    private List<PmOrgProfile> orgUnits; 
    
    private List<PmOrgProfile> deleteorgUnits;      
	
	private List<PmAddress> address;	

	private List<PmUserGroup> usersGroup;
	
	private List<SelectItem> countryName;
		
	// PM datatable array list
	private List<PreferenceList> preferencelists;
	
	private List<RoleList> rolelists;
	
	private List<AddressList> addresslistss;	
	
	private List<PmCountry> countries;	
	
	// get user profiles detail
	
	private Long userId;
	
	private Long districtId;
	
	private Long countryId;

	private Long stateId;

	private Long divisionId;
	
	private Long cityId;	
	
	private boolean status;		
	
	// list of location
	private List<PmState> stateList;
		
	private List<PmDistrict> districtList;
	
	private List<PmCity> cityList;	
	
	// org search	
    private String orgNames;
    
    // get the lists of roles
    private List<PmRoleDesc> userRoleSelect;
	
    // get the lists of approver type
	private List<PmRoleDesc> userApproverSelect;	
	
	//get the lists of address
	private List<PmAddressType> pmRecevingAddressList;	
	private PmAddressType selectRecevingAddress;
	private List<PmAddressType> selectedUserAddress;
	//get the lists of role
	private List<PmRoleDesc> selectedUserRole;
	private PmRoleDesc selectUserRole;
	
	private PmOrgProfile orgProfile;
	
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}	

	public ProfilePersonalizationManagedBean(){
		log.info("inside ");
		
	
	}	
	
		
	public PmUser getPmUser() {
		return pmUser;
	}

	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	
	public PmCriteria getPmCriteria() {
		return pmCriteria;
	}

	public void setPmCriteria(PmCriteria pmCriteria) {
		this.pmCriteria = pmCriteria;
	}
	
	public List<PmUser> getUsers() {
		return users;
	}

	public void setUsers(List<PmUser> users) {
		this.users = users;
	}
	
	public List<PmOrgProfile> getOrgUnits() {
		return orgUnits;
	}

	public void setOrgUnits(List<PmOrgProfile> orgUnits) {
		this.orgUnits = orgUnits;
	}		

	public List<PmOrgProfile> getDeleteorgUnits() {
		return deleteorgUnits;
	}

	public void setDeleteorgUnits(List<PmOrgProfile> deleteorgUnits) {
		this.deleteorgUnits = deleteorgUnits;
	}

	public void setAddress(List<PmAddress> address) {
		this.address = address;
	}
	
	public List<PmAddress> getAdddress() {
		return address;
	}
	
	public List<PmUserGroup> getUsersGroup() {
		return usersGroup;
	}

	public void setUsersGroup(List<PmUserGroup> usersGroup) {
		this.usersGroup = usersGroup;
	}	
	
	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}	
	
	public String getUserRole() {
		return userRole;
	}



	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}



	public String getUserorganizationNames() {
		return userorganizationNames;
	}



	public void setUserorganizationNames(String userorganizationNames) {
		this.userorganizationNames = userorganizationNames;
	}



	public boolean getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(boolean userStatus) {
		this.userStatus = userStatus;
	}	

	public List<PmState> getStateList() {
		return stateList;
	}

	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}	

	public List<PmCity> getCityList() {
		return cityList;
	}

	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
		

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}		

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public PmAddress getPmAddress() {
		return pmAddress;
	}


	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}		

	public PmAddress getcAddress() {
		return cAddress;
	}

	public void setcAddress(PmAddress cAddress) {
		this.cAddress = cAddress;
	}

	public PmAddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(PmAddressType addressType) {
		this.addressType = addressType;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
		
	public String getUserGroupNames() {
		return userGroupNames;
	}

	public void setUserGroupNames(String userGroupNames) {
		this.userGroupNames = userGroupNames;
	}

	public String getUserGroupRole() {
		return userGroupRole;
	}

	public void setUserGroupRole(String userGroupRole) {
		this.userGroupRole = userGroupRole;
	}

	public String getUserGroupOrganizationNames() {
		return userGroupOrganizationNames;
	}

	public void setUserGroupOrganizationNames(String userGroupOrganizationNames) {
		this.userGroupOrganizationNames = userGroupOrganizationNames;
	}

	public String getUserGroupStatus() {
		return userGroupStatus;
	}

	public void setUserGroupStatus(String userGroupStatus) {
		this.userGroupStatus = userGroupStatus;
	}	
	
	public PmOrgValidity getOrgValidity() {
		return orgValidity;
	}

	public void setOrgValidity(PmOrgValidity orgValidity) {
		this.orgValidity = orgValidity;
	}	

	public PmRole getRoles() {
		return roles;
	}

	public void setRoles(PmRole roles) {
		this.roles = roles;
	}	

	public List<SelectItem> getCountryName() {
		return countryName;
	}

	public void setCountryName(List<SelectItem> countryName) {
		this.countryName = countryName;
	}		

	public PmUser getUser() {
		return user;
	}

	public void setUser(PmUser user) {
		this.user = user;
	}
	
	public PmState getState() {
		return state;
	}

	public void setState(PmState state) {
		this.state = state;
	}
		

	public PmCity getCity() {
		return city;
	}

	public void setCity(PmCity city) {
		this.city = city;
	}	

	public PmDivision getDivision() {
		return division;
	}

	public void setDivision(PmDivision division) {
		this.division = division;
	}

	public PmDistrict getDistrict() {
		return district;
	}

	public void setDistrict(PmDistrict district) {
		this.district = district;
	}
	
	public List<PmCountry> getCountries() {
		return countries;
	}

	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}	
	
	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}
				
	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}		

	public String getOrgNames() {
		return orgNames;
	}

	public void setOrgNames(String orgNames) {
		this.orgNames = orgNames;
	}	
	
	public List<PmRoleDesc> getUserRoleSelect() {
		return userRoleSelect;
	}

	public void setUserRoleSelect(List<PmRoleDesc> userRoleSelect) {
		this.userRoleSelect = userRoleSelect;
	}
	
	public List<PmRoleDesc> getUserApproverSelect() {
		return userApproverSelect;
	}

	public void setUserApproverSelect(List<PmRoleDesc> userApproverSelect) {
		this.userApproverSelect = userApproverSelect;
	}			

	public List<PmAddressType> getPmRecevingAddressList() {
		return pmRecevingAddressList;
	}

	public void setPmRecevingAddressList(List<PmAddressType> pmRecevingAddressList) {
		this.pmRecevingAddressList = pmRecevingAddressList;
	}	

	public PmAddressType getSelectRecevingAddress() {
		return selectRecevingAddress;
	}

	public void setSelectRecevingAddress(PmAddressType selectRecevingAddress) {
		this.selectRecevingAddress = selectRecevingAddress;
	}	

	public List<PmAddressType> getSelectedUserAddress() {
		return selectedUserAddress;
	}

	public void setSelectedUserAddress(List<PmAddressType> selectedUserAddress) {
		this.selectedUserAddress = selectedUserAddress;
	}

	public List<PmRoleDesc> getSelectedUserRole() {
		return selectedUserRole;
	}

	public void setSelectedUserRole(List<PmRoleDesc> selectedUserRole) {
		this.selectedUserRole = selectedUserRole;
	}	

	public PmRoleDesc getSelectUserRole() {
		return selectUserRole;
	}

	public void setSelectUserRole(PmRoleDesc selectUserRole) {
		this.selectUserRole = selectUserRole;
	}
		
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<PreferenceList> getPreferencelists() {
		
		if(preferencelists==null){
			preferencelists = new ArrayList<PreferenceList>();
		
		PreferenceList aa = new PreferenceList();
		PreferenceList bb = new PreferenceList();
		PreferenceList cc = new PreferenceList();
		
		aa.setNoticeMode("DASHBOARD");
		aa.setPrefered("YES");
	
		bb.setNoticeMode("EMAIL");
		bb.setPrefered("YES");		

		cc.setNoticeMode("SMS");
		cc.setPrefered("YES");		
		
		preferencelists.add(aa);
		preferencelists.add(bb);
		preferencelists.add(cc);

		
		}
		return preferencelists;
	}	
	
	public void setPreferencelists(List<PreferenceList> preferencelists) {
		this.preferencelists = preferencelists;
	}
	
	public List<RoleList> getRolelists() {
		
		if(rolelists==null){
			rolelists = new ArrayList<RoleList>();
		
		RoleList aaa = new RoleList();
		RoleList bbb = new RoleList();
		RoleList ccc = new RoleList();
		RoleList ddd = new RoleList();		
		
		aaa.setRoleName("Approver");
		aaa.setRoleTypes("Fulfilment");
		aaa.setApprLimit("300,000.00");
		aaa.setRoleStatus("Active");
	
		bbb.setRoleName("Contract Signer");
		bbb.setRoleTypes("");
		bbb.setApprLimit("");
		bbb.setRoleStatus("Active");

		ccc.setRoleName("Plan Officer");
		ccc.setRoleTypes("Expenditure Plan");
		ccc.setApprLimit("");
		ccc.setRoleStatus("Active");
		
		ddd.setRoleName("Supplier Admin");
		ddd.setRoleTypes("Basic Account");
		ddd.setApprLimit("500,000.00");
		ddd.setRoleStatus("Active");		
		
		rolelists.add(aaa);
		rolelists.add(bbb);
		rolelists.add(ccc);
		rolelists.add(ddd);		

		
		}
		return rolelists;
	}	
	
	public void setRoleLists(List<RoleList> rolelists) {
		this.rolelists = rolelists;
	}	
	
	public List<AddressList> getAddressListss() {
		
		if(addresslistss==null){
			addresslistss = new ArrayList<AddressList>();
		
			AddressList aaaa = new AddressList();
			AddressList bbbb = new AddressList();
			AddressList cccc = new AddressList();
			AddressList dddd = new AddressList();		
		
		aaaa.setAddressName("Address 1");
		aaaa.setAddressStatus("Active");
	
		bbbb.setAddressName("Address 2");
		bbbb.setAddressStatus("Active");

		cccc.setAddressName("Address 3");
		cccc.setAddressStatus("Active");
		
		dddd.setAddressName("Address 4");
		dddd.setAddressStatus("Active");

		addresslistss.add(aaaa);
		addresslistss.add(bbbb);
		addresslistss.add(cccc);
		addresslistss.add(dddd);		

		
		}
		return addresslistss;
	}	
	
	public void setAddressListss(List<AddressList> addresslistss) {
		this.addresslistss = addresslistss;
	}


}	
	

