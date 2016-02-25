package my.ep.web.managedbean.pm.profilemanagementsearch;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;

/**
 * The managed bean for profile Management Search
 * 
 * @author Zeinab
 * @since 24.02.2016
 */
@ManagedBean(name = "profileManagSearchManagedBean")
@SessionScoped
public class ProfileManagementSearchManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(ProfileManagementManagedBean.class);
	private Map<String, SelectItem[]> orgTypeMap = new HashMap<String, SelectItem[]>();

	// private ProfileManagementSearchBean profileManagSearchBean;

	// public ProfileManagementSearchBean getProfileManagSearchBean() {
	// return profileManagSearchBean;
	// }
	//
	// public void setProfileManagSearchBean(
	// ProfileManagementSearchBean profileManagSearchBean) {
	// this.profileManagSearchBean = profileManagSearchBean;
	// }
	private Long selectedOrgType;

	private PmOrgProfile selectedOrgProfile;

	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public Map<String, SelectItem[]> getOrgTypeMap() {
		return orgTypeMap;
	}

	public void setOrgTypeMap(Map<String, SelectItem[]> orgTypeMap) {
		this.orgTypeMap = orgTypeMap;
	}

}
