/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.pm.accessappl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.FileUploadEvent;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.persistence.entity.pm.common.PmAccessApplDoc;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The managed bean for Access Appl
 * 
 * @author Ken
 * @since 21.12.2015
 */
@ManagedBean(name="accessApplManagedBean")
@SessionScoped
public class AccessApplManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmAccessAppl accessAppl;
	private Map<String,SelectItem[]> orgTypeMap = 
			new HashMap<String, SelectItem[]>();
	private Long selectedOrgType;
	private PmOrgProfile selectedOrgProfile;
	private List<PmState> stateList;
	private List<PmDistrict> districtList;
	private List<PmCity> cityList;
	private PmAccessApplDoc selectedPmAccessApplDoc;
	private boolean tncChecked;
	private boolean declarationChecked;
	private List<PmAccessApplDoc> accessApplDocList;
	private String remarks;
	private Map<PmAccessApplDoc, FileUploadEvent> fileEventMapper;
	private boolean showTrackingDairy = false;
	
	public AccessApplManagedBean(){}

	/**
	 * @return the accessAppl
	 */
	public PmAccessAppl getAccessAppl() {
		return accessAppl;
	}

	/**
	 * @param accessAppl the accessAppl to set
	 */
	public void setAccessAppl(PmAccessAppl accessAppl) {
		this.accessAppl = accessAppl;
	}

	/**
	 * @return the orgTypeMap
	 */
	public Map<String,SelectItem[]> getOrgTypeMap() {
		return orgTypeMap;
	}

	/**
	 * @param orgTypeMap the orgTypeMap to set
	 */
	public void setOrgTypeMap(Map<String,SelectItem[]> orgTypeMap) {
		this.orgTypeMap = orgTypeMap;
	}

	/**
	 * @return the selectedOrgType
	 */
	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	/**
	 * @param selectedOrgType the selectedOrgType to set
	 */
	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * @param stateList the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * @param districtList the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * @return the cityList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}

	/**
	 * @param cityList the cityList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}

	/**
	 * @return the selectedPmAccessApplDoc
	 */
	public PmAccessApplDoc getSelectedPmAccessApplDoc() {
		return selectedPmAccessApplDoc;
	}

	/**
	 * @param selectedPmAccessApplDoc the selectedPmAccessApplDoc to set
	 */
	public void setSelectedPmAccessApplDoc(PmAccessApplDoc selectedPmAccessApplDoc) {
		this.selectedPmAccessApplDoc = selectedPmAccessApplDoc;
	}

	/**
	 * @return the tncChecked
	 */
	public boolean isTncChecked() {
		return tncChecked;
	}

	/**
	 * @param tncChecked the tncChecked to set
	 */
	public void setTncChecked(boolean tncChecked) {
		this.tncChecked = tncChecked;
	}

	/**
	 * @return the declarationChecked
	 */
	public boolean isDeclarationChecked() {
		return declarationChecked;
	}

	/**
	 * @param declarationChecked the declarationChecked to set
	 */
	public void setDeclarationChecked(boolean declarationChecked) {
		this.declarationChecked = declarationChecked;
	}

	/**
	 * @return the accessApplDocList
	 */
	public List<PmAccessApplDoc> getAccessApplDocList() {
		return accessApplDocList;
	}

	/**
	 * @param accessApplDocList the accessApplDocList to set
	 */
	public void setAccessApplDocList(List<PmAccessApplDoc> accessApplDocList) {
		this.accessApplDocList = accessApplDocList;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the fileEventMapper
	 */
	public Map<PmAccessApplDoc, FileUploadEvent> getFileEventMapper() {
		return fileEventMapper;
	}

	/**
	 * @param fileEventMapper the fileEventMapper to set
	 */
	public void setFileEventMapper(Map<PmAccessApplDoc, FileUploadEvent> fileEventMapper) {
		this.fileEventMapper = fileEventMapper;
	}

	public boolean isShowTrackingDairy() {
		return showTrackingDairy;
	}

	public void setShowTrackingDairy(boolean showTrackingDairy) {
		this.showTrackingDairy = showTrackingDairy;
	}
	
	
}
