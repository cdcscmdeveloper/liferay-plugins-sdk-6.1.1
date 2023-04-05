/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.performanceConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.ct.CtConfig;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.PmTaskReassignmentVO;

/**
 * Managed Bean for PerformanceConfigurationManagedBean
 * 
 * @author Basri
 * @since 09.02.2023
 */
@ManagedBean(name = "performanceConfigurationManagedBean")
@SessionScoped
public class PerformanceConfigurationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	
	private DualListModel<CtConfig> ctConfigModel;
	
	private HashMap<String, PmUserGroupUser> ctConfigMap;
	
	private DualListModel<PmUser> approverModel;
	
	private Map<Long, PmUser> userMap;
	
	public PerformanceConfigurationManagedBean(){}

	
	
	/**
	 * @return the ctConfigModel
	 */
	public DualListModel<CtConfig> getCtConfigModel() {
		return ctConfigModel;
	}

	/**
	 * @param ctConfigModel the ctConfigModel to set
	 */
	public void setCtConfigModel(DualListModel<CtConfig> ctConfigModel) {
		this.ctConfigModel = ctConfigModel;
	}



	/**
	 * @return the ctConfigMap
	 */
	public HashMap<String, PmUserGroupUser> getCtConfigMap() {
		return ctConfigMap;
	}



	/**
	 * @param ctConfigMap the ctConfigMap to set
	 */
	public void setCtConfigMap(HashMap<String, PmUserGroupUser> ctConfigMap) {
		this.ctConfigMap = ctConfigMap;
	}
	
	
	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}
	
	/**
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}
}