package my.ep.web.managedbean.pm.common.potaskweightage;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPoRoleTask;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "poTaskWeightageMB")
@SessionScoped
public class POTaskWeightageManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(POTaskWeightageManagedBean.class);
	private String roleCode;
	private String zoneCode;
	private PmUser selectedUser;
	private List<PmParameterDesc> applTypeSelection;
	private List<PmUser> userLs;
	private String userName;
	private String identificationNo;
	private int recordStatus;
	private Map<Long,PmPoRoleTask> existingRoleTaskMap;
	private int poStatus;
	/**********AUDIT***********/
	private List<PmPoRoleTask> oldPmPoRoleTask;
	/**********AUDIT***********/
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public PmUser getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public List<PmParameterDesc> getApplTypeSelection() {
		return applTypeSelection;
	}

	public void setApplTypeSelection(List<PmParameterDesc> applTypeSelection) {
		this.applTypeSelection = applTypeSelection;
	}

	public List<PmUser> getUserLs() {
		return userLs;
	}

	public void setUserLs(List<PmUser> userLs) {
		this.userLs = userLs;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public int getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(int recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Map<Long, PmPoRoleTask> getExistingRoleTaskMap() {
		return existingRoleTaskMap;
	}

	public void setExistingRoleTaskMap(Map<Long, PmPoRoleTask> existingRoleTaskMap) {
		this.existingRoleTaskMap = existingRoleTaskMap;
	}

	public int getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(int poStatus) {
		this.poStatus = poStatus;
	}
	/******AUDIT*******/
	public List<PmPoRoleTask> getOldPmPoRoleTask() {
		return oldPmPoRoleTask;
	}

	public void setOldPmPoRoleTask(List<PmPoRoleTask> oldPmPoRoleTask) {
		this.oldPmPoRoleTask = oldPmPoRoleTask;
	}
	/******AUDIT*******/
}
