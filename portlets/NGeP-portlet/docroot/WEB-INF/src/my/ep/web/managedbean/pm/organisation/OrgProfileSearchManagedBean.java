/*
 * Copyright © 2016 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.organisation;

import java.util.ArrayList; //#27868
import java.util.List; //#27868
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Organization Search
 * 
 * @author Ken Wong
 * @since 17/06/2016
 * 
 */
@ManagedBean(name = "orgProfileSearchMB")
@SessionScoped
public class OrgProfileSearchManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private Long ministryProfileId;
	private Long pegaiPengawaProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private PmOrgProfile selectedPtj;
	private IPagingDataModel<PmOrgProfile> orgProfileDataModel;
	private String orgType;
	private PmOrgProfile currentActiveOrg;
	private boolean disabledJabatan;
	private boolean disabledMinistry;
	private boolean disabledPegaiPengawa;	
	
	//start - 27868
	private List<Long> ptjIds = new ArrayList<>();
	private boolean specialItemProcType;
	private boolean specialItemSearching = false;
	//end - 27868	
	
	//27502 - Start
	private Long zone;
	private boolean ministrySearching = false;
	//27502 - End
	
	public OrgProfileSearchManagedBean() {

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
	 * @return the selectedPtj
	 */
	public PmOrgProfile getSelectedPtj() {
		return selectedPtj;
	}

	/**
	 * @param selectedPtj the selectedPtj to set
	 */
	public void setSelectedPtj(PmOrgProfile selectedPtj) {
		this.selectedPtj = selectedPtj;
	}

	/**
	 * @return the orgProfileDataModel
	 */
	public IPagingDataModel<PmOrgProfile> getOrgProfileDataModel() {
		return orgProfileDataModel;
	}

	/**
	 * @param orgProfileDataModel the orgProfileDataModel to set
	 */
	public void setOrgProfileDataModel(IPagingDataModel<PmOrgProfile> orgProfileDataModel) {
		this.orgProfileDataModel = orgProfileDataModel;
	}

	/**
	 * @return the orgType
	 */
	public String getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
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

	//start - #27868
	public List<Long> getPtjIds() {
		return ptjIds;
	}

	public void setPtjIds(List<Long> ptjIds) {
		this.ptjIds = ptjIds;
	}

	public boolean isSpecialItemProcType() {
		return specialItemProcType;
	}

	public void setSpecialItemProcType(boolean specialItemProcType) {
		this.specialItemProcType = specialItemProcType;
	}

	public boolean isSpecialItemSearching() {
		return specialItemSearching;
	}

	public void setSpecialItemSearching(boolean specialItemSearching) {
		this.specialItemSearching = specialItemSearching;
	}
	//end - #27868	
	// 27502 - Start
	public Long getZone() {
		return zone;
	}

	public void setZone(Long zone) {
		this.zone = zone;
	}

	/**
	 * @return the ministrySearching
	 */
	public boolean isMinistrySearching() {
		return ministrySearching;
	}

	/**
	 * @param ministrySearching
	 *            the ministrySearching to set
	 */
	public void setMinistrySearching(boolean ministrySearching) {
		this.ministrySearching = ministrySearching;
	}

	// 27502 - End
}
