/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.taskassign;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
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
//	private boolean epAdmin;
	private boolean filterOrg;
	private String filterOrgCondition;
	private String userActiveOrgs;
	
	
	private PmUser resultUser;
	private PmOrgProfile resultOrgProfile;
	
	private String tempRole;
	
	

	/**
	 * @return the tempRole
	 */
	public String getTempRole() {
		return tempRole;
	}

	/**
	 * @param tempRole the tempRole to set
	 */
	public void setTempRole(String tempRole) {
		this.tempRole = tempRole;
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

//	/**
//	 * @return the epAdmin
//	 */
//	public boolean isEpAdmin() {
//		return epAdmin;
//	}
//
//	/**
//	 * @param epAdmin the epAdmin to set
//	 */
//	public void setEpAdmin(boolean epAdmin) {
//		this.epAdmin = epAdmin;
//	}

	/**
	 * @return the userActiveOrgs
	 */
	public String getUserActiveOrgs() {
		return userActiveOrgs;
	}

	/**
	 * @param userActiveOrgs the userActiveOrgs to set
	 */
	public void setUserActiveOrgs(String userActiveOrgs) {
		this.userActiveOrgs = userActiveOrgs;
	}

	/**
	 * @return the filterOrg
	 */
	public boolean isFilterOrg() {
		return filterOrg;
	}

	/**
	 * @param filterOrg the filterOrg to set
	 */
	public void setFilterOrg(boolean filterOrg) {
		this.filterOrg = filterOrg;
	}

	/**
	 * @return the filterOrgCondition
	 */
	public String getFilterOrgCondition() {
		return filterOrgCondition;
	}

	/**
	 * @param filterOrgCondition the filterOrgCondition to set
	 */
	public void setFilterOrgCondition(String filterOrgCondition) {
		this.filterOrgCondition = filterOrgCondition;
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