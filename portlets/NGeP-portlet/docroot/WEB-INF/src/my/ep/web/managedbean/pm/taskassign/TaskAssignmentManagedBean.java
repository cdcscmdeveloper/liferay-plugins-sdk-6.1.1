/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.taskassign;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	
	
	
}