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
import my.ep.persistence.entity.pm.authorization.PmUser;
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
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	private boolean chkConceValue;

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

	public boolean isChkConceValue() {
		return chkConceValue;
	}

	public void setChkConceValue(boolean chkConceValue) {
		this.chkConceValue = chkConceValue;
	}
	
}
