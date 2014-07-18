/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.termination;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.terminate.CtTermination;
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
	private String externalPageUrl;
	
	private boolean disciplinaryValue;
	private boolean termination;
	private boolean mutualTermination;
	private boolean remedy;
	private boolean viewContract = false;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtTermination> terminationList;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private String fileDirectory;

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

	public boolean isDisciplinaryValue() {
		return disciplinaryValue;
	}

	public void setDisciplinaryValue(boolean disciplinaryValue) {
		this.disciplinaryValue = disciplinaryValue;
	}

	public String getComplaintCategory() {
		return complaintCategory;
	}

	public void setComplaintCategory(String complaintCategory) {
		this.complaintCategory = complaintCategory;
	}

	public boolean isTermination() {
		return termination;
	}

	public void setTermination(boolean termination) {
		this.termination = termination;
	}

	public boolean isMutualTermination() {
		return mutualTermination;
	}

	public void setMutualTermination(boolean mutualTermination) {
		this.mutualTermination = mutualTermination;
	}

	public boolean isRemedy() {
		return remedy;
	}

	public void setRemedy(boolean remedy) {
		this.remedy = remedy;
	}

	public List<CtTermination> getTerminationList() {
		return terminationList;
	}

	public void setTerminationList(List<CtTermination> terminationList) {
		this.terminationList = terminationList;
	}

	public boolean isViewContract() {
		return viewContract;
	}

	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}
	
	public String getExternalPageUrl() {
		return externalPageUrl;
	}
	
	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
}
