package my.ep.web.managedbean.pm.common.poworkload;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "poWorkloadMB")
@SessionScoped
public class PoWorkloadManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private List<PmUser> semenanjungUserLs;
	private List<PmUser> sabahUserLs;
	private List<PmUser> sarawakUserLs;
	private List<PmParameterDesc> applTypeSelection;
	private PmUser selectedUser;
	
	private Map<String, List<PmUser>> zoneUserMap;
	
	public List<PmUser> getSemenanjungUserLs() {
		return semenanjungUserLs;
	}
	public void setSemenanjungUserLs(List<PmUser> semenanjungUserLs) {
		this.semenanjungUserLs = semenanjungUserLs;
	}
	public List<PmUser> getSabahUserLs() {
		return sabahUserLs;
	}
	public void setSabahUserLs(List<PmUser> sabahUserLs) {
		this.sabahUserLs = sabahUserLs;
	}
	public List<PmUser> getSarawakUserLs() {
		return sarawakUserLs;
	}
	public void setSarawakUserLs(List<PmUser> sarawakUserLs) {
		this.sarawakUserLs = sarawakUserLs;
	}
	public Map<String, List<PmUser>> getZoneUserMap() {
		return zoneUserMap;
	}
	public void setZoneUserMap(Map<String, List<PmUser>> zoneUserMap) {
		this.zoneUserMap = zoneUserMap;
	}
	public List<PmParameterDesc> getApplTypeSelection() {
		return applTypeSelection;
	}
	public void setApplTypeSelection(List<PmParameterDesc> applTypeSelection) {
		this.applTypeSelection = applTypeSelection;
	}
	public PmUser getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}
}
