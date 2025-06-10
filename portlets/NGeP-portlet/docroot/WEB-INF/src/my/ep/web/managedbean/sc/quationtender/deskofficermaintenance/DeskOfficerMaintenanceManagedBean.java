/**
 * 
 */
package my.ep.web.managedbean.sc.quationtender.deskofficermaintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.bean.sc.quatationtender.DeskOfficerMaintenanceSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.PmTaskReassignmentVO;

/**
 * @author Mallareddy Belageri
 *
 */
@ManagedBean(name = "deskOfficerMaintenanceManagedBean")
@SessionScoped
public class DeskOfficerMaintenanceManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private DeskOfficerMaintenanceSearchBean doMaintenanceSearchBean;
	
	private IPagingDataModel<ScQt> searchQTDataModel;
	
	private List<String> selectQtNos = null;
	
	private Map<Long, PmUser> qtNoDeskOfficerMap = null;
	
	private boolean disableAssignTo = true;
	
	private boolean disableSubmit = true;
	
	private String searchUserName;
	
	private String searchUserIdNo;
	
	private PmTaskReassignmentVO taskReassignmentVO;
	
	private TaskAssignmentSearchBean taskAssignmentSearchBean;
	
	private boolean isCommitteeUpdate;
	
	private PmUser selectedUser;
	
	private boolean isRemarkRequired;
	
	private String remarks;
	
	private ArrayList<WorklistItemVO> assignedTasks;
	
	private List<Long> selectedQtIds;
	
	private ScQtApprovalRequest  currentApprovalRequest;
	
	private ScQtApproval  currentApproval;

	private PmUser originalDeskOfficer;

	private PmUser newDeskOfficer;

	private String action;

	private boolean autoUpdate;
	
	private boolean disableAssign = true;
	
	//#28859 - 35500
	private PmUser newTempDeskOfficer;
	
	
	public DeskOfficerMaintenanceManagedBean() {
		this.doMaintenanceSearchBean = new DeskOfficerMaintenanceSearchBean();
	}

	/**
	 * @return the doMaintenanceSearchBean
	 */
	public DeskOfficerMaintenanceSearchBean getDoMaintenanceSearchBean() {
		return doMaintenanceSearchBean;
	}

	/**
	 * @param doMaintenanceSearchBean the doMaintenanceSearchBean to set
	 */
	public void setDoMaintenanceSearchBean(DeskOfficerMaintenanceSearchBean doMaintenanceSearchBean) {
		this.doMaintenanceSearchBean = doMaintenanceSearchBean;
	}

	/**
	 * @return the searchQTDataModel
	 */
	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	/**
	 * @param searchQTDataModel the searchQTDataModel to set
	 */
	public void setSearchQTDataModel(IPagingDataModel<ScQt> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
	}

	/**
	 * @return the selectQtNos
	 */
	public List<String> getSelectQtNos() {
		return selectQtNos;
	}

	/**
	 * @param selectQtNos the selectQtNos to set
	 */
	public void setSelectQtNos(List<String> selectQtNos) {
		this.selectQtNos = selectQtNos;
	}

	/**
	 * @return the disableAssignTo
	 */
	public boolean isDisableAssignTo() {
		return disableAssignTo;
	}

	/**
	 * @param disableAssignTo the disableAssignTo to set
	 */
	public void setDisableAssignTo(boolean disableAssignTo) {
		this.disableAssignTo = disableAssignTo;
	}

	/**
	 * @return the disableSubmit
	 */
	public boolean isDisableSubmit() {
		return disableSubmit;
	}

	/**
	 * @param disableSubmit the disableSubmit to set
	 */
	public void setDisableSubmit(boolean disableSubmit) {
		this.disableSubmit = disableSubmit;
	}

	/**
	 * @return the searchUserName
	 */
	public String getSearchUserName() {
		return searchUserName;
	}

	/**
	 * @param searchUserName the searchUserName to set
	 */
	public void setSearchUserName(String searchUserName) {
		this.searchUserName = searchUserName;
	}

	/**
	 * @return the taskReassignmentVO
	 */
	public PmTaskReassignmentVO getTaskReassignmentVO() {
		return taskReassignmentVO;
	}

	/**
	 * @param taskReassignmentVO the taskReassignmentVO to set
	 */
	public void setTaskReassignmentVO(PmTaskReassignmentVO taskReassignmentVO) {
		this.taskReassignmentVO = taskReassignmentVO;
	}

	/**
	 * @return the taskAssignmentSearchBean
	 */
	public TaskAssignmentSearchBean getTaskAssignmentSearchBean() {
		return taskAssignmentSearchBean;
	}

	/**
	 * @param taskAssignmentSearchBean the taskAssignmentSearchBean to set
	 */
	public void setTaskAssignmentSearchBean(TaskAssignmentSearchBean taskAssignmentSearchBean) {
		this.taskAssignmentSearchBean = taskAssignmentSearchBean;
	}

	/**
	 * @return the isCommitteeUpdate
	 */
	public boolean isCommitteeUpdate() {
		return isCommitteeUpdate;
	}

	/**
	 * @param isCommitteeUpdate the isCommitteeUpdate to set
	 */
	public void setCommitteeUpdate(boolean isCommitteeUpdate) {
		this.isCommitteeUpdate = isCommitteeUpdate;
	}

	/**
	 * @return the selectedUser
	 */
	public PmUser getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	/**
	 * @return the isRemarkRequired
	 */
	public boolean isRemarkRequired() {
		return isRemarkRequired;
	}

	/**
	 * @param isRemarkRequired the isRemarkRequired to set
	 */
	public void setRemarkRequired(boolean isRemarkRequired) {
		this.isRemarkRequired = isRemarkRequired;
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
	 * @return the searchUserIdNo
	 */
	public String getSearchUserIdNo() {
		return searchUserIdNo;
	}

	/**
	 * @param searchUserIdNo the searchUserIdNo to set
	 */
	public void setSearchUserIdNo(String searchUserIdNo) {
		this.searchUserIdNo = searchUserIdNo;
	}

	/**
	 * @return the qtNoDeskOfficerMap
	 */
	public Map<Long, PmUser> getQtNoDeskOfficerMap() {
		return qtNoDeskOfficerMap;
	}

	/**
	 * @param qtNoDeskOfficerMap the qtNoDeskOfficerMap to set
	 */
	public void setQtNoDeskOfficerMap(Map<Long, PmUser> qtNoDeskOfficerMap) {
		this.qtNoDeskOfficerMap = qtNoDeskOfficerMap;
	}

	/**
	 * @return the assignedTasks
	 */
	public ArrayList<WorklistItemVO> getAssignedTasks() {
		return assignedTasks;
	}

	/**
	 * @param assignedTasks the assignedTasks to set
	 */
	public void setAssignedTasks(ArrayList<WorklistItemVO> assignedTasks) {
		this.assignedTasks = assignedTasks;
	}

	public List<Long> getSelectedQtIds() {
		return selectedQtIds;
	}

	public void setSelectedQtIds(List<Long> selectedQtIds) {
		this.selectedQtIds = selectedQtIds;
	}

	public ScQtApprovalRequest getCurrentApprovalRequest() {
		return currentApprovalRequest;
	}

	public void setCurrentApprovalRequest(ScQtApprovalRequest currentApprovalRequest) {
		this.currentApprovalRequest = currentApprovalRequest;
	}

	public PmUser getOriginalDeskOfficer() {
		return originalDeskOfficer;
	}

	public void setOriginalDeskOfficer(PmUser originalDeskOfficer) {
		this.originalDeskOfficer = originalDeskOfficer;
	}

	public PmUser getNewDeskOfficer() {
		return newDeskOfficer;
	}

	public void setNewDeskOfficer(PmUser newDeskOfficer) {
		this.newDeskOfficer = newDeskOfficer;
	}

	public ScQtApproval getCurrentApproval() {
		return currentApproval;
	}

	public void setCurrentApproval(ScQtApproval currentApproval) {
		this.currentApproval = currentApproval;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the disableAssign
	 */
	public boolean isDisableAssign() {
		return disableAssign;
	}

	/**
	 * @param disableAssign the disableAssign to set
	 */
	public void setDisableAssign(boolean disableAssign) {
		this.disableAssign = disableAssign;
	}

	//#28895-35500 start
	/**
	 * @return the newTempDeskOfficer
	 */
	public PmUser getNewTempDeskOfficer() {
		return newTempDeskOfficer;
	}

	/**
	 * @param newTempDeskOfficer the newTempDeskOfficer to set
	 */
	public void setNewTempDeskOfficer(PmUser newTempDeskOfficer) {
		this.newTempDeskOfficer = newTempDeskOfficer;
	}
	//#28895-35500 end
}
