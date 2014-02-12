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
	
	private boolean isEditable = true;	
	
	private List<PmUser> users;
	
	private List<PmAddress> userAddress;

	private List<PmUserGroup> usersGroup;
	
	private String selectedCountry;

	private List<SelectItem> countryName;
	
	private List<PreferenceList> preferencelists;		
		
	public UserprofilesManagedBean(){
		log.info("inside ");		
	}	

	public List<PmUser> getUsers() {
		return users;
	}

	public void setUsers(List<PmUser> users) {
		this.users = users;
	}
	
	public List<PmAddress> getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(List<PmAddress> userAddress) {
		this.userAddress = userAddress;
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


	public PmUser getPmUser() {
		return user;
	}



	public void setPmUser(PmUser user) {
		this.user = user;
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
	
	public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public List<SelectItem> getCountryName() {
		return countryName;
	}

	public void setCountryName(List<SelectItem> countryName) {
		this.countryName = countryName;
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
	
}	
	

