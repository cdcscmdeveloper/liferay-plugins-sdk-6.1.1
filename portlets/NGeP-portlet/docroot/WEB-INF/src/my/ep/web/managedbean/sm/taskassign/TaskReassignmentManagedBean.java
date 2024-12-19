/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.sm.taskassign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.sm.TaskAssignmentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.SmTaskReassignmentVO;

/**
 * Managed Bean for Task Reassignment
 * 
 * @author KK LIM
 * @since 15.11.2016
 */
@ManagedBean(name = "supplierTaskReassignmentManagedBean")
@SessionScoped
public class TaskReassignmentManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private TaskAssignmentSearchBean taskAssignmentSearchBean;
	private PmOrgProfile currentActiveOrg;
	private boolean orgProfileRequired;
	private List<String> roleCodes;
	private PmUser resultUser;
	private List<PmUser> availableUsers;
	private WorklistItemVO selectedTask;
	private SmPersonnel selectedUser;
	private WorklistItemVO selectedDeleteTask;
	private String searchUserName;
	private boolean isTaskLazy;
	private int taskType;

	private String backURL;
	private SmSupplier smSupplier;
	private String docType;
	//#29822 - START
	private List<SmTaskReassignmentVO> taskReassignmentVO;
	private List<WorklistItemVO> selectedTasksToBulkReassign;
	private List<String> selectedReassignee;
	private List<WorklistItemVO> allUserTasks;
	private List<WorklistItemVO> allGroupTasks = new ArrayList<>();
	private List<WorklistItemVO> allOthersTasks = new ArrayList<>();
	private boolean showParentMsg;
	private boolean showChildMsg;
	private boolean checkAllUserTasks;
	private boolean checkAllGroupTasks;
	private boolean checkAllOthersTasks;
	private boolean checkAllUserTasksEnabled;
	private boolean checkAllGroupTasksEnabled;
	private boolean checkAllOthersTasksEnabled;
	private boolean isCommitteeUpdate;
	private boolean bulkReassign;
	private boolean singleReassign;
	private boolean enableDeleteGroupTasksButton;
	private boolean enableDeleteOthersTasksButton;
	private String remarks;
	private String selectedTasksTA;
	private Map<String, WorklistItemVO> selectedGroupTasks = new HashMap<>();
	private Map<String, WorklistItemVO> selectedOthersTasks = new HashMap<>();
	private SmTaskReassignmentVO smTaskReassignmentVO;
	//#29822 - END
	
	// #29822 - start
	private boolean enableRemoveAllGroupTasksButton;
	private boolean enableRemoveAllOthersTasksButton;
	private boolean showRemoveProcessMessageGroup;
	private boolean showRemoveProcessMessageOther;
	// #29822 - end
	
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
	public SmPersonnel getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(SmPersonnel selectedUser) {
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

	//#29822 - START
	/**
	 * @return the taskReassignmentVO
	 */
	public List<SmTaskReassignmentVO> getTasksReassignmentVO() {
		return taskReassignmentVO;
	}

	/**
	 * @param taskReassignmentVO the taskReassignmentVO to set
	 */
	public void setTasksReassignmentVO(List<SmTaskReassignmentVO> tasksReassignmentVO) {
		this.taskReassignmentVO = tasksReassignmentVO;
	}
	//#29822 - END
	
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

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	//#29822 - START
	public List<WorklistItemVO> getSelectedTasksToBulkReassign() {
		return selectedTasksToBulkReassign;
	}

	public void setSelectedTasksToBulkReassign(List<WorklistItemVO> selectedTasksToBulkReassign) {
		this.selectedTasksToBulkReassign = selectedTasksToBulkReassign;
	}

	public List<WorklistItemVO> getAllUserTasks() {
		return allUserTasks;
	}

	public void setAllUserTasks(List<WorklistItemVO> allUserTasks) {
		this.allUserTasks = allUserTasks;
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

	public boolean isCheckAllGroupTasksEnabled() {
		return checkAllGroupTasksEnabled;
	}

	public void setCheckAllGroupTasksEnabled(boolean checkAllGroupTasksEnabled) {
		this.checkAllGroupTasksEnabled = checkAllGroupTasksEnabled;
	}

	public boolean isCheckAllOthersTasksEnabled() {
		return checkAllOthersTasksEnabled;
	}

	public void setCheckAllOthersTasksEnabled(boolean checkAllOthersTasksEnabled) {
		this.checkAllOthersTasksEnabled = checkAllOthersTasksEnabled;
	}

	public boolean isCheckAllGroupTasks() {
		return checkAllGroupTasks;
	}

	public void setCheckAllGroupTasks(boolean checkAllGroupTasks) {
		this.checkAllGroupTasks = checkAllGroupTasks;
	}

	public boolean isCheckAllOthersTasks() {
		return checkAllOthersTasks;
	}

	public void setCheckAllOthersTasks(boolean checkAllOthersTasks) {
		this.checkAllOthersTasks = checkAllOthersTasks;
	}
	
	public boolean isCommitteeUpdate() {
		return isCommitteeUpdate;
	}
	
	public void setCommitteeUpdate(boolean isCommitteeUpdate) {
		this.isCommitteeUpdate = isCommitteeUpdate;
	}
	
	public boolean isBulkReassign() {
		return bulkReassign;
	}
	
	public void setBulkReassign(boolean bulkReassign) {
		this.bulkReassign = bulkReassign;
	}
	
	public boolean isSingleReassign() {
		return singleReassign;
	}
	
	public void setSingleReassign(boolean singleReassign) {
		this.singleReassign = singleReassign;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSelectedTasksTA() {
		return selectedTasksTA;
	}

	public void setSelectedTasksTA(String selectedTasksTA) {
		this.selectedTasksTA = selectedTasksTA;
	}

	public Map<String, WorklistItemVO> getSelectedGroupTasks() {
		return selectedGroupTasks;
	}

	public void setSelectedGroupTasks(Map<String, WorklistItemVO> selectedGroupTasks) {
		this.selectedGroupTasks = selectedGroupTasks;
	}

	public List<WorklistItemVO> getAllGroupTasks() {
		return allGroupTasks;
	}

	public void setAllGroupTasks(List<WorklistItemVO> allGroupTasks) {
		this.allGroupTasks = allGroupTasks;
	}

	public boolean isEnableDeleteGroupTasksButton() {
		return enableDeleteGroupTasksButton;
	}

	public void setEnableDeleteGroupTasksButton(boolean enableDeleteGroupTasksButton) {
		this.enableDeleteGroupTasksButton = enableDeleteGroupTasksButton;
	}

	public List<String> getSelectedReassignee() {
		return selectedReassignee;
	}

	public void setSelectedReassignee(List<String> selectedReassignee) {
		this.selectedReassignee = selectedReassignee;
	}

	public boolean isEnableDeleteOthersTasksButton() {
		return enableDeleteOthersTasksButton;
	}

	public void setEnableDeleteOthersTasksButton(boolean enableDeleteOthersTasksButton) {
		this.enableDeleteOthersTasksButton = enableDeleteOthersTasksButton;
	}

	public Map<String, WorklistItemVO> getSelectedOthersTasks() {
		return selectedOthersTasks;
	}

	public void setSelectedOthersTasks(Map<String, WorklistItemVO> selectedOthersTasks) {
		this.selectedOthersTasks = selectedOthersTasks;
	}

	public List<WorklistItemVO> getAllOthersTasks() {
		return allOthersTasks;
	}

	public void setAllOthersTasks(List<WorklistItemVO> allOthersTasks) {
		this.allOthersTasks = allOthersTasks;
	}

	public List<SmTaskReassignmentVO> getTaskReassignmentVO() {
		return taskReassignmentVO;
	}

	public void setTaskReassignmentVO(List<SmTaskReassignmentVO> taskReassignmentVO) {
		this.taskReassignmentVO = taskReassignmentVO;
	}

	public SmTaskReassignmentVO getSmTaskReassignmentVO() {
		return smTaskReassignmentVO;
	}

	public void setSmTaskReassignmentVO(SmTaskReassignmentVO smTaskReassignmentVO) {
		this.smTaskReassignmentVO = smTaskReassignmentVO;
	}
	//#29822 - END

	// #29822 - start
	public boolean isEnableRemoveAllGroupTasksButton() {
		return enableRemoveAllGroupTasksButton;
	}

	public void setEnableRemoveAllGroupTasksButton(boolean enableRemoveAllGroupTasksButton) {
		this.enableRemoveAllGroupTasksButton = enableRemoveAllGroupTasksButton;
	}

	public boolean isEnableRemoveAllOthersTasksButton() {
		return enableRemoveAllOthersTasksButton;
	}

	public void setEnableRemoveAllOthersTasksButton(boolean enableRemoveAllOthersTasksButton) {
		this.enableRemoveAllOthersTasksButton = enableRemoveAllOthersTasksButton;
	}
	
	public boolean isShowRemoveProcessMessageGroup() {
		return showRemoveProcessMessageGroup;
	}

	public void setShowRemoveProcessMessageGroup(boolean showRemoveProcessMessageGroup) {
		this.showRemoveProcessMessageGroup = showRemoveProcessMessageGroup;
	}

	public boolean isShowRemoveProcessMessageOther() {
		return showRemoveProcessMessageOther;
	}

	public void setShowRemoveProcessMessageOther(boolean showRemoveProcessMessageOther) {
		this.showRemoveProcessMessageOther = showRemoveProcessMessageOther;
	}
	
	// #29822 - end
	
}
