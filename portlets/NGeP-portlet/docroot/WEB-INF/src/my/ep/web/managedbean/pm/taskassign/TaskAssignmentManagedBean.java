/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.taskassign;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "taskAssignManagedBean")
@SessionScoped
public class TaskAssignmentManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private TaskAssignmentSearchBean taskAssignmentSearchBean;
	private WorklistItemVO selectedTask;
//	private List<PmUser> selectedUsers;
	private List<PmUser> availableUsers;
	private PmUser selectedUser;
	private boolean orgProfileRequired;

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

//	/**
//	 * @return the selectedUsers
//	 */
//	public List<PmUser> getSelectedUsers() {
//		return selectedUsers;
//	}
//
//	/**
//	 * @param selectedUsers the selectedUsers to set
//	 */
//	public void setSelectedUsers(List<PmUser> selectedUsers) {
//		this.selectedUsers = selectedUsers;
//	}

	
	
}