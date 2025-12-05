package my.ep.web.managedbean.pm.organisation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * 20230111@Haziq - #12487&29693
 * Managed Bean for User Organization Search
 * 
 * 
 * @author muhammadasyrafhaziq
 *
 */
@ManagedBean(name = "userOrgSearchMB")
@SessionScoped
public class UserOrgSearchManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private Long ministryProfileId;
	private Long pegawaiPengawalProfileId;
	private Long jabatanProfileId;
	private Long ptjProfileId; //#27868
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private PmUserOrg selectedUser;
	private IPagingDataModel<PmUserOrg> userOrgDataModel;
	private String orgType;
	private PmOrgProfile currentActiveOrg;
	private PmUserOrg currentActiveUserOrg;
	private boolean disabledJabatan;
	private boolean disabledMinistry;
	private boolean disabledPegawaiPengawal;
	
	private String userCodeName;
	private String userCodeNameSearchValues;
	private Long zone;
	
	//start - #27868
	private String searchName;
	private String searchIcNo;
	private String identificationTypeId;
	//end - #27868
	
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
	
	// start - 27868
	/**
	 * @return the ptjProfileId
	 */
	public Long getPtjProfileId() {
		return ptjProfileId;
	}

	/**
	 * @param ptjProfileId the ptjProfileId to set
	 */
	public void setPtjProfileId(Long ptjProfileId) {
		this.ptjProfileId = ptjProfileId;
	}

	// end - 27868

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
	 * @return the pegawaiPengawalProfileId
	 */
	public Long getPegawaiPengawalProfileId() {
		return pegawaiPengawalProfileId;
	}

	/**
	 * @param pegawaiPengawalProfileId the pegawaiPengawalProfileId to set
	 */
	public void setPegawaiPengawalProfileId(Long pegawaiPengawalProfileId) {
		this.pegawaiPengawalProfileId = pegawaiPengawalProfileId;
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
	 * @return the disabledPegawaiPengawal
	 */
	public boolean isDisabledPegawaiPengawal() {
		return disabledPegawaiPengawal;
	}

	/**
	 * @param disabledPegawaiPengawal the disabledPegawaiPengawal to set
	 */
	public void setDisabledPegawaiPengawal(boolean disabledPegawaiPengawal) {
		this.disabledPegawaiPengawal = disabledPegawaiPengawal;
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

	//start - #27868
	/**
	 * @return the searchName
	 */
	public String getSearchName() {
		return searchName;
	}

	/**
	 * @param searchName
	 *            the searchName to set
	 */
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	/**
	 * @return the searchIcNo
	 */
	public String getSearchIcNo() {
		return searchIcNo;
	}

	/**
	 * @param searchIcNo
	 *            the searchIcNo to set
	 */
	public void setSearchIcNo(String searchIcNo) {
		this.searchIcNo = searchIcNo;
	}

	/**
	 * @return the identificationTypeId
	 */
	public String getIdentificationTypeId() {
		return identificationTypeId;
	}

	/**
	 * @param identificationTypeId
	 *            the identificationTypeId to set
	 */
	public void setIdentificationTypeId(String identificationTypeId) {
		this.identificationTypeId = identificationTypeId;
	}
	//end - #27868
}
