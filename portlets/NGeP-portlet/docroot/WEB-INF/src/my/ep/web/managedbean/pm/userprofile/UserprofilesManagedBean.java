package my.ep.web.managedbean.pm.userprofile;


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
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmCriteria;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;


/**
 * @author DK
 *
 */
@ManagedBean(name="userprofilesManagedBean")
@SessionScoped
public class UserprofilesManagedBean extends BaseManagedBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(UserprofilesManagedBean.class);	
	
	private PmUser user;
	
	private PmUser pmUser = new PmUser();	
	
	private PmCriteria pmCriteria;		

	private PmAddress pmAddress;
	
	private PmOrgValidity orgValidity;
	
	private PmRole roles;
				
    // user profile	search	
	private String userNames;
	
	private String criteriaDescs;	
	
	private String userRole;	
	
	private String userorganizationNames;	
	
	private boolean userStatus = false;
	
	private String stateCode;
	
	private String roleNames;
	
	private String districtCode;	
	
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
	
	private List<PmCriteriaDesc> criterias;	
	
	private List<PmAddress> address;	

	private List<PmUserGroup> usersGroup;
	
	private List<SelectItem> countryName;
		
	// PM datatable array list
	private List<PreferenceList> preferencelists;
	
	private List<RoleList> rolelists;
	
	private List<AddressList> addresslistss;	
	
	private List<PmState> states;	
	
	private List<PmDistrict> districts;
	
	private List<PmCity> citys;	
	
	private List<PmDivision> divisions;	
	
	private List<PmCountry> countries;	
	
	// get user profiles detail
	
	private Long userId;
	
	private Long districtId;
	
	private Long countryId;

	private Long stateId;

	private Long divisionId;
	
	private boolean status;	
	
	// org search	
    private String orgNames;
    
    // get the lists of roles
	public List<PmRoleDesc> userRoleSelect;;
	
	//get the lists of address
	private List<PmAddressType> userAddressSelect;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}	

	public UserprofilesManagedBean(){
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
	
	public String getCriteriaDesc() {
		return criteriaDescs;
	}

	public void setCriteriaDesc(String criteriaDescs) {
		this.criteriaDescs = criteriaDescs;
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

	public PmAddress getPmAddress() {
		return pmAddress;
	}


	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
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

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
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

	public List<PmDistrict> getDistricts() {
		return districts;
	}

	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}
	
	public List<PmCity> getCitys() {
		return citys;
	}

	public void setCitys(List<PmCity> citys) {
		this.citys = citys;
	}
	
	
	
	public List<PmCriteriaDesc> getCriterias() {
		return criterias;
	}

	public void setCriterias(List<PmCriteriaDesc> criterias) {
		this.criterias = criterias;
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

	public List<PmDivision> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<PmDivision> divisions) {
		this.divisions = divisions;
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

	public List<PmAddressType> getUserAddressSelect() {
		return userAddressSelect;
	}

	public void setUserAddressSelect(List<PmAddressType> userAddressSelect) {
		this.userAddressSelect = userAddressSelect;
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
	

