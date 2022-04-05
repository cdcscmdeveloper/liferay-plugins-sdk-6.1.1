/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.taskassign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.PmTaskReassignmentVO;

/**
 * Managed Bean for Task Reassignment
 * 
 * @author Ken
 * @since 07.09.2016
 */
@ManagedBean(name = "taskReassignmentManagedBean")
@SessionScoped
public class TaskReassignmentManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private TaskAssignmentSearchBean taskAssignmentSearchBean;
	private PmOrgProfile currentActiveOrg;
	private boolean orgProfileRequired;
	private List<String> roleCodes;
	private PmUser resultUser;
	private PmOrgProfile resultOrgProfile;
	private List<PmUser> availableUsers;
	private WorklistItemVO selectedTask;
	private PmUser selectedUser;
	private WorklistItemVO selectedDeleteTask;
	private String searchUserName;
	private boolean isTaskLazy;
	private int taskType;
	private PmTaskReassignmentVO taskReassignmentVO;
	private String backURL;
	private boolean disableForTaskAssignment;
	private boolean isCommitteeUpdate;
	private List<Integer> statusIds;
	private boolean isRemarkRequired;
	private String remarks;
	private Map<String,String> reassigneeList = new LinkedHashMap<String,String>();
	private List<String> selectedReassignee;
	private boolean showReassigneeList;
	private boolean enableDeleteGroupTasksButton;
	private boolean checkAllGroupTasksEnabled;
	private boolean checkAllGroupTasks;
	private List<WorklistItemVO> allGroupTasks = new ArrayList<WorklistItemVO>();
	private Map<String,WorklistItemVO> selectedGroupTasks = new HashMap<String,WorklistItemVO>();
	private boolean enableDeleteOthersTasksButton;
	private boolean checkAllOthersTasksEnabled;
	private boolean checkAllOthersTasks;
	private List<WorklistItemVO> allOthersTasks = new ArrayList<WorklistItemVO>();
	private Map<String,WorklistItemVO> selectedOthersTasks = new HashMap<String,WorklistItemVO>();
	//[#25018] - start
	private boolean admin;
	private List<WorklistItemVO> selectedTasksToBulkReassign;
	private boolean bulkReassign;
	private boolean singleReassign;
	private boolean checkAllUserTasks;
	private boolean checkAllUserTasksEnabled;
	private List<WorklistItemVO> allUserTasks;
	private String selectedTasksTA;
	private String docType;
	private Long docStatus;
	private List<PmTaskReassignmentVO> tasksReassignmentVO;
	private Map<Long,String> excludeStatusId;
	private boolean showParentMsg;
	private boolean showChildMsg;
	//[#25018] - end
	
	//[#22117 - start]
	private boolean bpkAdminUser;
	//[#22117 - stop]
	
	public TaskReassignmentManagedBean(){}

	/**
	 * @return the taskAssignmentSearchBean
	 */
	public TaskAssignmentSearchBean getTaskAssignmentSearchBean() {
		return taskAssignmentSearchBean;
	}

	/**
	 * @param taskAssignmentSearchBean the taskAssignmentSearchBean to set
	 */
	public void setTaskAssignmentSearchBean(
			TaskAssignmentSearchBean taskAssignmentSearchBean) {
		this.taskAssignmentSearchBean = taskAssignmentSearchBean;
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
	 * @return the orgProfileRequired
	 */
	public boolean isOrgProfileRequired() {
		return orgProfileRequired;
	}

	/**
	 * @param orgProfileRequired the orgProfileRequired to set
	 */
	public void setOrgProfileRequired(boolean orgProfileRequired) {
		this.orgProfileRequired = orgProfileRequired;
	}

	/**
	 * @return the roleCodes
	 */
	public List<String> getRoleCodes() {
		return roleCodes;
	}

	/**
	 * @param roleCodes the roleCodes to set
	 */
	public void setRoleCodes(List<String> roleCodes) {
		this.roleCodes = roleCodes;
	}

	/**
	 * @return the resultUser
	 */
	public PmUser getResultUser() {
		return resultUser;
	}

	/**
	 * @param resultUser the resultUser to set
	 */
	public void setResultUser(PmUser resultUser) {
		this.resultUser = resultUser;
	}

	/**
	 * @return the resultOrgProfile
	 */
	public PmOrgProfile getResultOrgProfile() {
		return resultOrgProfile;
	}

	/**
	 * @param resultOrgProfile the resultOrgProfile to set
	 */
	public void setResultOrgProfile(PmOrgProfile resultOrgProfile) {
		this.resultOrgProfile = resultOrgProfile;
	}

	/**
	 * @return the availableUsers
	 */
	public List<PmUser> getAvailableUsers() {
		return availableUsers;
	}

	/**
	 * @param availableUsers the availableUsers to set
	 */
	public void setAvailableUsers(List<PmUser> availableUsers) {
		this.availableUsers = availableUsers;
	}

	/**
	 * @return the selectedTask
	 */
	public WorklistItemVO getSelectedTask() {
		return selectedTask;
	}

	/**
	 * @param selectedTask the selectedTask to set
	 */
	public void setSelectedTask(WorklistItemVO selectedTask) {
		this.selectedTask = selectedTask;
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
	 * @return the selectedDeleteTask
	 */
	public WorklistItemVO getSelectedDeleteTask() {
		return selectedDeleteTask;
	}

	/**
	 * @param selectedDeleteTask the selectedDeleteTask to set
	 */
	public void setSelectedDeleteTask(WorklistItemVO selectedDeleteTask) {
		this.selectedDeleteTask = selectedDeleteTask;
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
	 * @return the isTaskLazy
	 */
	public boolean isTaskLazy() {
		return isTaskLazy;
	}

	/**
	 * @param isTaskLazy the isTaskLazy to set
	 */
	public void setTaskLazy(boolean isTaskLazy) {
		this.isTaskLazy = isTaskLazy;
	}

	/**
	 * @return the taskType
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(int taskType) {
		this.taskType = taskType;
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
	 * @return the backURL
	 */
	public String getBackURL() {
		return backURL;
	}

	/**
	 * @param backURL the backURL to set
	 */
	public void setBackURL(String backURL) {
		this.backURL = backURL;
	}

	/**
	 * @return the disableForTaskAssignment
	 */
	public boolean isDisableForTaskAssignment() {
		return disableForTaskAssignment;
	}

	/**
	 * @param disableForTaskAssignment the disableForTaskAssignment to set
	 */
	public void setDisableForTaskAssignment(boolean disableForTaskAssignment) {
		this.disableForTaskAssignment = disableForTaskAssignment;
	}

	/**
	 * @return the statusIds
	 */
	public List<Integer> getStatusIds() {
		return statusIds;
	}

	/**
	 * @param statusIds the statusIds to set
	 */
	public void setStatusIds(List<Integer> statusIds) {
		this.statusIds = statusIds;
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
	 *  @return the reassigneeList
	 */
	public Map<String,String> getReassigneeList() {
		return reassigneeList;
	}
	
	/**
	 * @param selectedReassignee the selectedReassignee to set
	 */
	public void setSelectedReassignee(List<String> selectedReassignee) {
		this.selectedReassignee = selectedReassignee;
	}
	
	/**
	 * @return the selectedReassignee
	 */
	public List<String> getSelectedReassignee() {
		return selectedReassignee;
	}
	
	/**
	 * @return the showReassigneeList
	 */
	public boolean isShowReassigneeList() {
		return showReassigneeList;
	}

	/**
	 * @param showReassigneeList the showReassigneeList to set
	 */
	public void setShowReassigneeList(boolean showReassigneeList) {
		this.showReassigneeList = showReassigneeList;
	}
	
	/**
	 * @return the enableDeleteGroupTasksButton
	 */
	public boolean isEnableDeleteGroupTasksButton() {
		return enableDeleteGroupTasksButton;
	}
	
	/**
	 * @param enableDeleteGroupTasksButton the enableDeleteGroupTasksButton to set
	 */
	public void setEnableDeleteGroupTasksButton(boolean enableDeleteGroupTasksButton) {
		this.enableDeleteGroupTasksButton = enableDeleteGroupTasksButton;
	}
	
	/**
	 * @param checkAllGroupTasksEnabled the checkAllGroupTasksEnabled to set
	 */
	public void setCheckAllGroupTasksEnabled(boolean checkAllGroupTasksEnabled) {
		this.checkAllGroupTasksEnabled = checkAllGroupTasksEnabled;
	}
	
	/**
	 * @return the checkAllGroupTasksEnabled
	 */
	public boolean isCheckAllGroupTasksEnabled() {
		return checkAllGroupTasksEnabled;
	}

	/**
	 * @return the checkAllGroupTasks
	 */
	public boolean isCheckAllGroupTasks() {
		return checkAllGroupTasks;
	}

	/**
	 * @param checkAllGroupTasks the checkAllGroupTasks to set
	 */
	public void setCheckAllGroupTasks(boolean checkAllGroupTasks) {
		this.checkAllGroupTasks = checkAllGroupTasks;
	}
	
	public List<WorklistItemVO> getAllGroupTasks() {
	    return allGroupTasks;
	}
	
	public void setAllGroupTasks(List<WorklistItemVO> allGroupTasks) {
		this.allGroupTasks = allGroupTasks;
	}
	
	public void setSelectedGroupTasks(Map<String,WorklistItemVO> selectedGroupTasks) {
		this.selectedGroupTasks = selectedGroupTasks;
	}
	
	public Map<String,WorklistItemVO> getSelectedGroupTasks() {
	    return selectedGroupTasks;
	}
	
	/**
	 * @return the enableDeleteOthersTasksButton
	 */
	public boolean isEnableDeleteOthersTasksButton() {
		return enableDeleteOthersTasksButton;
	}
	
	/**
	 * @param enableDeleteOthersTasksButton the enableDeleteOthersTasksButton to set
	 */
	public void setEnableDeleteOthersTasksButton(boolean enableDeleteOthersTasksButton) {
		this.enableDeleteOthersTasksButton = enableDeleteOthersTasksButton;
	}
	
	/**
	 * @param checkAllOthersTasksEnabled the checkAllOthersTasksEnabled to set
	 */
	public void setCheckAllOthersTasksEnabled(boolean checkAllOthersTasksEnabled) {
		this.checkAllOthersTasksEnabled = checkAllOthersTasksEnabled;
	}
	
	/**
	 * @return the checkAllOthersTasksEnabled
	 */
	public boolean isCheckAllOthersTasksEnabled() {
		return checkAllOthersTasksEnabled;
	}

	/**
	 * @return the checkAllOthersTasks
	 */
	public boolean isCheckAllOthersTasks() {
		return checkAllOthersTasks;
	}

	/**
	 * @param checkAllOthersTasks the checkAllOthersTasks to set
	 */
	public void setCheckAllOthersTasks(boolean checkAllOthersTasks) {
		this.checkAllOthersTasks = checkAllOthersTasks;
	}
	
	public List<WorklistItemVO> getAllOthersTasks() {
	    return allOthersTasks;
	}
	
	public void setAllOthersTasks(List<WorklistItemVO> allOthersTasks) {
		this.allOthersTasks = allOthersTasks;
	}
	
	public void setSelectedOthersTasks(Map<String,WorklistItemVO> selectedOthersTasks) {
		this.selectedOthersTasks = selectedOthersTasks;
	}
	
	public Map<String,WorklistItemVO> getSelectedOthersTasks() {
	    return selectedOthersTasks;
	}
	
	//[#25018]
	public boolean isAdmin() {
		return admin;
	}
	//[#25018]
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	//[#25018]
	public List<WorklistItemVO> getSelectedTasksToBulkReassign() {
		return selectedTasksToBulkReassign;
	}
	//[#25018]
	public void setSelectedTasksToBulkReassign(List<WorklistItemVO> selectedTasksToBulkReassign) {
		this.selectedTasksToBulkReassign = selectedTasksToBulkReassign;
	}
	//[#25018]
	public boolean isBulkReassign() {
		return bulkReassign;
	}
	//[#25018]
	public void setBulkReassign(boolean bulkReassign) {
		this.bulkReassign = bulkReassign;
	}

	public boolean isCheckAllUserTasks() {
		return checkAllUserTasks;
	}

	public void setCheckAllUserTasks(boolean checkAllUserTasks) {
		this.checkAllUserTasks = checkAllUserTasks;
	}

	public boolean isCheckAllUserTasksEnabled() {
		return checkAllUserTasksEnabled;
	}

	public void setCheckAllUserTasksEnabled(boolean checkAllUserTasksEnabled) {
		this.checkAllUserTasksEnabled = checkAllUserTasksEnabled;
	}

	public List<WorklistItemVO> getAllUserTasks() {
		return allUserTasks;
	}

	public void setAllUserTasks(List<WorklistItemVO> allUserTasks) {
		this.allUserTasks = allUserTasks;
	}

	public boolean isSingleReassign() {
		return singleReassign;
	}

	public void setSingleReassign(boolean singleReassign) {
		this.singleReassign = singleReassign;
	}

	public String getSelectedTasksTA() {
		return selectedTasksTA;
	}

	public void setSelectedTasksTA(String selectedTasksTA) {
		this.selectedTasksTA = selectedTasksTA;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public List<PmTaskReassignmentVO> getTasksReassignmentVO() {
		return tasksReassignmentVO;
	}

	public void setTasksReassignmentVO(List<PmTaskReassignmentVO> tasksReassignmentVO) {
		this.tasksReassignmentVO = tasksReassignmentVO;
	}

	public Map<Long,String> getExcludeStatusId() {
		return excludeStatusId;
	}

	public void setExcludeStatusId(Map<Long,String> excludeStatusId) {
		this.excludeStatusId = excludeStatusId;
	}

	public Long getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(Long docStatus) {
		this.docStatus = docStatus;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public boolean isShowChildMsg() {
		return showChildMsg;
	}

	public void setShowChildMsg(boolean showChildMsg) {
		this.showChildMsg = showChildMsg;
	}

	public boolean isBpkAdminUser() {
		return bpkAdminUser;
	}

	public void setBpkAdminUser(boolean bpkAdminUser) {
		this.bpkAdminUser = bpkAdminUser;
	}
}