/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.organisation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for User Organization Search
 * 
 * 
 */
//20230111@Haziq - #12487
/**
 * @author muhammadasyrafhaziq
 *
 */
@ManagedBean(name = "userOrgSearchMB")
@SessionScoped
public class UserOrgSearchManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private Long ministryProfileId;
	private Long pegaiPengawaProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private PmUserOrg selectedUser;
	private IPagingDataModel<PmUserOrg> userOrgDataModel;
	private String orgType;
	private PmOrgProfile currentActiveOrg;
	private PmUserOrg currentActiveUserOrg;
	private boolean disabledJabatan;
	private boolean disabledMinistry;
	private boolean disabledPegaiPengawa;
	
	private String userCodeName;
	private String userCodeNameSearchValues;
	private Long zone;
	
	
	public UserOrgSearchManagedBean() {
      //Do nothing
	}

	/**
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}

	/**
	 * @return the orgType
	 */
	@Override
	public String getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	@Override
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the disabledJabatan
	 */
	public boolean isDisabledJabatan() {
		return disabledJabatan;
	}

	/**
	 * @param disabledJabatan the disabledJabatan to set
	 */
	public void setDisabledJabatan(boolean disabledJabatan) {
		this.disabledJabatan = disabledJabatan;
	}
	/**
	 * @return the pegaiPengawaProfileId
	 */
	public Long getPegaiPengawaProfileId() {
		return pegaiPengawaProfileId;
	}

	/**
	 * @param pegaiPengawaProfileId the pegaiPengawaProfileId to set
	 */
	public void setPegaiPengawaProfileId(Long pegaiPengawaProfileId) {
		this.pegaiPengawaProfileId = pegaiPengawaProfileId;
	}

	/**
	 * @return the disabledMinistry
	 */
	public boolean isDisabledMinistry() {
		return disabledMinistry;
	}


	/**
	 * @param disabledMinistry the disabledMinistry to set
	 */
	public void setDisabledMinistry(boolean disabledMinistry) {
		this.disabledMinistry = disabledMinistry;
	}

	/**
	 * @return the disabledPegaiPengawa
	 */
	public boolean isDisabledPegaiPengawa() {
		return disabledPegaiPengawa;
	}

	/**
	 * @param disabledPegaiPengawa the disabledPegaiPengawa to set
	 */
	public void setDisabledPegaiPengawa(boolean disabledPegaiPengawa) {
		this.disabledPegaiPengawa = disabledPegaiPengawa;
	}

	/**
	 * @return userCodeName
	 */
	public String getUserCodeName() {
		return userCodeName;
	}

	/**
	 * @param userCodeName the userCodeName to set
	 */
	public void setUserCodeName(String userCodeName) {
		this.userCodeName = userCodeName;
	}

	/**
	 * @return userCodeNameSearchValues
	 */
	public String getUserCodeNameSearchValues() {
		return userCodeNameSearchValues;
	}

	/**
	 * @param userCodeNameSearchValues
	 */
	public void setUserCodeNameSearchValues(String userCodeNameSearchValues) {
		this.userCodeNameSearchValues = userCodeNameSearchValues;
	}

	/**
	 * @return zone
	 */
	public Long getZone() {
		return zone;
	}

	/**
	 * @param zone
	 */
	public void setZone(Long zone) {
		this.zone = zone;
	}

	/**
	 * @return userOrgDataModel
	 */
	public IPagingDataModel<PmUserOrg> getUserOrgDataModel() {
		return userOrgDataModel;
	}

	/**
	 * @param userOrgDataModel
	 */
	public void setUserOrgDataModel(IPagingDataModel<PmUserOrg> userOrgDataModel) {
		this.userOrgDataModel = userOrgDataModel;
	}

	/**
	 * @return selectedUser
	 */
	public PmUserOrg getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser
	 */
	public void setSelectedUser(PmUserOrg selectedUser) {
		this.selectedUser = selectedUser;
	}

	/**
	 * @return currentActiveUserOrg
	 */
	public PmUserOrg getCurrentActiveUserOrg() {
		return currentActiveUserOrg;
	}

	/**
	 * @param currentActiveUserOrg
	 */
	public void setCurrentActiveUserOrg(PmUserOrg currentActiveUserOrg) {
		this.currentActiveUserOrg = currentActiveUserOrg;
	}

	/**
	 * @return currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	
	
}
