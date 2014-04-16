/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.termination;

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
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Termination
 * 
 * @author Alam
 * @since 1/13/2014	 
 */

@ManagedBean(name ="terminationManagedBean")
@SessionScoped
public class TerminationManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String taskName;
	private String[] action;
	private String editorValue;
	private String complaintCategory ="Breach The Obligation Of Tender / Contract";
	
	private String userLoginId;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	private boolean terminateNotice;
	private boolean disciplinaryValue;
	private boolean terminateRejectLetter;
	private boolean terminateWarningLetter;
	
	private CtContract ctContract;
	private CtContractVer contractVer;
	private List<CtTerminationReason> ctTerminationReason;
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	
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
	
	public String getEditorValue() {
		return editorValue;
	}
	
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
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
	public String getUserLoginId() {
		return userLoginId;
	}
	
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
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

	public boolean isTerminateNotice() {
		return terminateNotice;
	}

	public void setTerminateNotice(boolean terminateNotice) {
		this.terminateNotice = terminateNotice;
	}

	public boolean isDisciplinaryValue() {
		return disciplinaryValue;
	}

	public void setDisciplinaryValue(boolean disciplinaryValue) {
		this.disciplinaryValue = disciplinaryValue;
	}

	public boolean isTerminateRejectLetter() {
		return terminateRejectLetter;
	}

	public void setTerminateRejectLetter(boolean terminateRejectLetter) {
		this.terminateRejectLetter = terminateRejectLetter;
	}

	public boolean isTerminateWarningLetter() {
		return terminateWarningLetter;
	}

	public void setTerminateWarningLetter(boolean terminateWarningLetter) {
		this.terminateWarningLetter = terminateWarningLetter;
	}

	public String getComplaintCategory() {
		return complaintCategory;
	}

	public void setComplaintCategory(String complaintCategory) {
		this.complaintCategory = complaintCategory;
	}

	public List<CtTerminationReason> getCtTerminationReason() {
		if(ctTerminationReason == null)
			ctTerminationReason = new ArrayList<CtTerminationReason>();
		return ctTerminationReason;
	}

	public void setCtTerminationReason(List<CtTerminationReason> ctTerminationReason) {
		this.ctTerminationReason = ctTerminationReason;
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
