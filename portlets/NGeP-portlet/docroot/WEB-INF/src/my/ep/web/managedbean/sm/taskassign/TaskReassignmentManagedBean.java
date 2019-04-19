/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.sm.taskassign;

import java.util.List;

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
	private SmTaskReassignmentVO taskReassignmentVO;
	private String backURL;
	private SmSupplier smSupplier;
	private String docType;
	
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

	/**
	 * @return the taskReassignmentVO
	 */
	public SmTaskReassignmentVO getTaskReassignmentVO() {
		return taskReassignmentVO;
	}

	/**
	 * @param taskReassignmentVO the taskReassignmentVO to set
	 */
	public void setTaskReassignmentVO(SmTaskReassignmentVO taskReassignmentVO) {
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

	
}