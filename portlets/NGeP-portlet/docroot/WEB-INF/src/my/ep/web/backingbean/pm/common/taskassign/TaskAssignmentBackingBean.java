/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.common.taskassign;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.managedbean.pm.taskassign.TaskAssignmentManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * Backing Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name="taskAssignBackingBean")
@ViewScoped
public class TaskAssignmentBackingBean extends BaseBackingBean<TaskAssignmentManagedBean>{
	private static Logger log = Logger.getLogger(TaskAssignmentBackingBean.class);
	
	//Navigation Details
	public static final String NAV_TO_SEARCH = "taskAssignment";
	public static final String NAV_TO_MAINTAIN = "taskAssignment";
	
	
	//List for Paging
//	private IPagingDataModel<PmDocument> pmDocumentDataModel = new PmDocumentDataModel();
		
	public void preLoading(){
		log.info("preLoading");
		this.resetTaskAssignSearchBean();
//		this.getManagedBean().setSelectedTabIndex(0);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * reset search bean fields
	 */
	public void resetTaskAssignSearchBean(){
		log.info("resetTaskAssignSearchBean");
		this.getManagedBean().setTaskAssignmentSearchBean(new TaskAssignmentSearchBean());
	}
	
	/**
	 * search task assignment
	 */
	public void searchTaskAssign(){
		log.info("searchDocument - Start");
//		pmDocumentDataModel.reloadData();
		log.info("searchDocument - End");
		
	}
	
	/**
	 * redirect to Task Re-assignment Details
	 * @param id
	 * @return xhtml
	 */
	public void redirectToTaskReassign(Long id){
		log.info("redirectToTaskReassign - Start");
		try{
//			if(id!=null && id>0){
//				
//			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToTaskReassign - End");
//		return NAV_TO_MAINTAIN;
		
		RequestContext.getCurrentInstance().execute("dlTaskReassignment.show()");
	}
	
}