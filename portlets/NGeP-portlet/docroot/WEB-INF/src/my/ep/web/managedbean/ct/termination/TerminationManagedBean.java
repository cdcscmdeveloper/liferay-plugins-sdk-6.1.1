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
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Termination
 * 
 * @author Alam
 * @since 1/13/2014
 */

@ManagedBean(name = "terminationManagedBean")
@SessionScoped
public class TerminationManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String taskName;
	private String editorValue;
	private String userLoginId;
	private String externalPageUrl;
	private String fileDirectory;
	private String textBeforeSign;
	private String buttonAction;
	private String ptjCodeName;
	private String ptjCodeNameSearchValue;

	private boolean termination;
	private boolean mutualTermination;
	private boolean remedy;
	private boolean autoUpdate = true;
	private boolean viewContract = false;
	private boolean  buttonHideActive = true;

	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtTermination> terminationList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;

	private PmDigiSign pmDigiSign;
	private CtContract ctContract;
	private CtContractVer contractVer;

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * @return the contractVer
	 */
	public CtContractVer getContractVer() {
		return contractVer;
	}

  /**
	 * @param contractVer the contractVer to set
	 */
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	/**
	 * @return the userLoginId
	 */
	public String getUserLoginId() {
		return userLoginId;
	}

	/**
	 * @param userLoginId the userLoginId to set
	 */
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if (attachmentList == null)
			attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the termination
	 */
	public boolean isTermination() {
		return termination;
	}

	/**
	 * @param termination the termination to set
	 */
	public void setTermination(boolean termination) {
		this.termination = termination;
	}

	/**
	 * @return the mutualTermination
	 */
	public boolean isMutualTermination() {
		return mutualTermination;
	}

	/**
	 * @param mutualTermination the mutualTermination to set
	 */
	public void setMutualTermination(boolean mutualTermination) {
		this.mutualTermination = mutualTermination;
	}

	/**
	 * @return the remedy
	 */
	public boolean isRemedy() {
		return remedy;
	}

	/**
	 * @param remedy the remedy to set
	 */
	public void setRemedy(boolean remedy) {
		this.remedy = remedy;
	}

	/**
	 * @return the terminationList
	 */
	public List<CtTermination> getTerminationList() {
		return terminationList;
	}

	/**
	 * @param terminationList the terminationList to set
	 */
	public void setTerminationList(List<CtTermination> terminationList) {
		this.terminationList = terminationList;
	}

	/**
	 * @return the viewContract
	 */
	public boolean isViewContract() {
		return viewContract;
	}

	/**
	 * @param viewContract the viewContract to set
	 */
	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}

	/**
	 * @return the externalPageUrl
	 */
	public String getExternalPageUrl() {
		return externalPageUrl;
	}

	/**
	 * @param externalPageUrl the externalPageUrl to set
	 */
	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
	}

	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}

	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
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
		if(userMap==null) {
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

	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValue
	 */
	public String getPtjCodeNameSearchValue() {
		return ptjCodeNameSearchValue;
	}

	/**
	 * @param ptjCodeNameSearchValue the ptjCodeNameSearchValue to set
	 */
	public void setPtjCodeNameSearchValue(String ptjCodeNameSearchValue) {
		this.ptjCodeNameSearchValue = ptjCodeNameSearchValue;
	}

	public boolean isButtonHideActive() {
		return buttonHideActive;
	}

	public void setButtonHideActive(boolean buttonHideActive) {
		this.buttonHideActive = buttonHideActive;
	}
}
