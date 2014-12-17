/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.suspension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Suspension
 * 
 * @author Alam
 * @since 1/13/2014	 
 */

@ManagedBean(name ="suspensionManagedBean")
@SessionScoped
public class SuspensionManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private String[] action;
	private String editorValue;
	
	private CtContract ctContract;
	private List<CtRemark> remarkList;
	private CtContractVer contractVer;
	private List<CtAttachment> attachmentList;
	private List<CtContractItem> ctItemFilter;
	private String fileDirectory;
	
	private double performanceBondRuleAmt;
	
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate;
	private String textBeforeSign;
	private String buttonAction;
	private boolean viewOnly = false;
	private HashMap<Long, CtAgency> agencyMap;
	private HashMap<Long, CtContractItem> itemMap;	
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	
	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public CtContractVer getContractVer() {
		return contractVer;
	}

	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
			attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public List<CtContractItem> getCtItemFilter() {
		return ctItemFilter;
	}

	public void setCtItemFilter(List<CtContractItem> ctItemFilter) {
		this.ctItemFilter = ctItemFilter;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public double getPerformanceBondRuleAmt() {
		return performanceBondRuleAmt;
	}

	public void setPerformanceBondRuleAmt(double performanceBondRuleAmt) {
		this.performanceBondRuleAmt = performanceBondRuleAmt;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	public String getButtonAction() {
		return buttonAction;
	}

	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	public boolean isViewOnly() {
		return viewOnly;
	}

	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}

	public HashMap<Long, CtAgency> getAgencyMap() {
		if(agencyMap == null) {
			setAgencyMap(new HashMap<Long, CtAgency>());
		}
		return agencyMap;
	}

	public void setAgencyMap(HashMap<Long, CtAgency> agencyMap) {
		this.agencyMap = agencyMap;
	}
	
	public HashMap<Long, CtContractItem> getItemMap() {
		if(itemMap == null) {
			setItemMap(new HashMap<Long, CtContractItem>());
		}
		return itemMap;
	}

	public void setItemMap(HashMap<Long, CtContractItem> itemMap) {
		this.itemMap = itemMap;
	}
	
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}
}
