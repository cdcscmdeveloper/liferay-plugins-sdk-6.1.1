package my.ep.web.managedbean.pm.searchalluser;


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
@ManagedBean(name="searchAllUserManagedBean")
@SessionScoped
public class SearchAllUserManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log= EpLogger.getLogger(SearchAllUserManagedBean.class);	

	
	private List<String[]> result;//TODO : TEMP REMOVE
    // user profile	search	
	private String userNames;
	
	
	/**
	 * @return the result
	 */
	public List<String[]> getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(List<String[]> result) {
		this.result = result;
	}
	/**
	 * @return the userNames
	 */
	public String getUserNames() {
		return userNames;
	}
	/**
	 * @param userNames the userNames to set
	 */
	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}	
	
	
	
	
}	
	

