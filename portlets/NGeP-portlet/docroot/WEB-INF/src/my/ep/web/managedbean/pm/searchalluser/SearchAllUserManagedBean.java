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

	
	private List<String[]> resultStatus;
	
	
    // user profile	search	
	private String loginId;
	private String resultLoginId;
	private List<String> pmRoleList;
	private List<String> liferayRoleList;
	private List<String> oimRoleList;
	
	

	/**
	 * @return the resultStatus
	 */
	public List<String[]> getResultStatus() {
		return resultStatus;
	}
	/**
	 * @param resultStatus the resultStatus to set
	 */
	public void setResultStatus(List<String[]> resultStatus) {
		this.resultStatus = resultStatus;
	}
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the pmRoleList
	 */
	public List<String> getPmRoleList() {
		return pmRoleList;
	}
	/**
	 * @param pmRoleList the pmRoleList to set
	 */
	public void setPmRoleList(List<String> pmRoleList) {
		this.pmRoleList = pmRoleList;
	}
	/**
	 * @return the liferayRoleList
	 */
	public List<String> getLiferayRoleList() {
		return liferayRoleList;
	}
	/**
	 * @param liferayRoleList the liferayRoleList to set
	 */
	public void setLiferayRoleList(List<String> liferayRoleList) {
		this.liferayRoleList = liferayRoleList;
	}
	/**
	 * @return the oimRoleList
	 */
	public List<String> getOimRoleList() {
		return oimRoleList;
	}
	/**
	 * @param oimRoleList the oimRoleList to set
	 */
	public void setOimRoleList(List<String> oimRoleList) {
		this.oimRoleList = oimRoleList;
	}
	/**
	 * @return the resultLoginId
	 */
	public String getResultLoginId() {
		return resultLoginId;
	}
	/**
	 * @param resultLoginId the resultLoginId to set
	 */
	public void setResultLoginId(String resultLoginId) {
		this.resultLoginId = resultLoginId;
	}

	
	
	
}	
	

