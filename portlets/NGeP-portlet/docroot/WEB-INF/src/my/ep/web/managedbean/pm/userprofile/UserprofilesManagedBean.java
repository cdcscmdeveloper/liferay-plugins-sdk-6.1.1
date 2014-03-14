package my.ep.web.managedbean.pm.userprofile;


import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCountry;
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

	private PmAddress pmAddress;
	
	private PmCountry pmCountry;
	
	private String loginId;
	
    // user profile	
	private String userName;	
	
	private String userRole;	
	
	private String userorganizationNames;	
	
	private String userStatus;
	
	// user group profile
	private String userGroupNames;	
	
	private String userGroupRole;	
	
	private String userGroupOrganizationNames;	
	
	private String userGroupStatus;
	
	private PmState pmState;	
	
    private PmDistrict pmDistrict;	

	private boolean isEditable = true;	
	
	private List<PmUser> users;
	
	private List<PmAddress> useraddress;	

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
	
	// get user profiles detail
	private String userDetailNames = null;
	
	private String userIdNo = null;
	
	private String userIdType;
	
	private String userDesignation = null;	
	
	private String userNationality;	
	
	private String userOrgType;	
	
	private String usergrade = null;
	
    private String userSalutation;	


	public UserprofilesManagedBean(){
		log.info("inside ");		
	}	

	public List<PmUser> getUsers() {
		return users;
	}

	public void setUseraddress(List<PmAddress> useraddress) {
		this.useraddress = useraddress;
	}
	
	public List<PmAddress> getUseraddress() {
		return useraddress;
	}

	public void setUsers(List<PmUser> users) {
		this.users = users;
	}	
	
	public List<PmUserGroup> getUsersGroup() {
		return usersGroup;
	}

	public void setUsersGroup(List<PmUserGroup> usersGroup) {
		this.usersGroup = usersGroup;
	}	

	public String getUserNames() {
		return userName;
	}



	public void setUserNames(String userName) {
		this.userName = userName;
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



	public String getUserStatus() {
		return userStatus;
	}



	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}


	public String getLoginId() {
		return loginId;
	}



	public void setLoginId(String loginId) {
		this.loginId = loginId;
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

	public PmCountry getPmCountry() {
		return pmCountry;
	}

	public void setPmCountry(PmCountry pmCountry) {
		this.pmCountry = pmCountry;
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

	public PmState getPmState() {
		return pmState;
	}

	public void setPmState(PmState pmState) {
		this.pmState = pmState;
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

	public PmDistrict getPmDistrict() {
		return pmDistrict;
	}

	public void setPmDistrict(PmDistrict pmDistrict) {
		this.pmDistrict = pmDistrict;
	}	
		
	public List<PmDivision> getDivisions() {
		return divisions;
	}

	public void setDivisions(List<PmDivision> divisions) {
		this.divisions = divisions;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDetailNames() {
		return userDetailNames;
	}

	public void setUserDetailNames(String userDetailNames) {
		this.userDetailNames = userDetailNames;
	}

	public String getUserIdNo() {
		return userIdNo;
	}

	public void setUserIdNo(String userIdNo) {
		this.userIdNo = userIdNo;
	}

	public String getUserIdType() {
		return userIdType;
	}

	public void setUserIdType(String userIdType) {
		this.userIdType = userIdType;
	}

	public String getUserDesignation() {
		return userDesignation;
	}

	public void setUserDesignation(String userDesignation) {
		this.userDesignation = userDesignation;
	}

	public String getUserNationality() {
		return userNationality;
	}

	public void setUserNationality(String userNationality) {
		this.userNationality = userNationality;
	}

	public String getUserOrgType() {
		return userOrgType;
	}

	public void setUserOrgType(String userOrgType) {
		this.userOrgType = userOrgType;
	}
	
	public String getUsergrade() {
		return usergrade;
	}

	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}
		

	public String getUserSalutation() {
		return userSalutation;
	}

	public void setUserSalutation(String userSalutation) {
		this.userSalutation = userSalutation;
	}

	public List<PreferenceList> getPreferencelists() {
		
		if(preferencelists==null){
			preferencelists = new ArrayList<PreferenceList>();
		
		PreferenceList aa = new PreferenceList();
		PreferenceList bb = new PreferenceList();
		PreferenceList cc = new PreferenceList();
		
		aa.setNoticeMode("DASHBOARD");
		aa.setPrefered("YES");
		aa.setMobileNo("DISABLE");
		aa.setEmailAddress("DISABLE");
	
		bb.setNoticeMode("EMAIL");
		bb.setPrefered("YES");		
		bb.setMobileNo("DISABLE");
		bb.setEmailAddress("DISABLE TO USER EMAIL ADDRESS");

		cc.setNoticeMode("SMS");
		cc.setPrefered("YES");		
		cc.setMobileNo("DISABLE");
		cc.setEmailAddress("DISABLE");
		
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
	

