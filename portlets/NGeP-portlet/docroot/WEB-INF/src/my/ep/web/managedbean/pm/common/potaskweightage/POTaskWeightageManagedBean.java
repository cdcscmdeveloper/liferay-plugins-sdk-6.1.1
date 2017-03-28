package my.ep.web.managedbean.pm.common.potaskweightage;

import java.util.List;

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
	private PmPoRoleTask selectedRoleTask;
	private List<PmParameterDesc> applTypeSelection;
	private PmPoRoleTask existingRoleTask;
	private List<PmUser> userLs;
	private String userName;
	private String identificationNo;

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

	public PmPoRoleTask getSelectedRoleTask() {
		return selectedRoleTask;
	}

	public void setSelectedRoleTask(PmPoRoleTask selectedRoleTask) {
		this.selectedRoleTask = selectedRoleTask;
	}

	public PmPoRoleTask getExistingRoleTask() {
		return existingRoleTask;
	}

	public void setExistingRoleTask(PmPoRoleTask existingRoleTask) {
		this.existingRoleTask = existingRoleTask;
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

}
