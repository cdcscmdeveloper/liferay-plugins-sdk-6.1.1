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
	private String fileDirectory;
	private String textBeforeSign;
	private String buttonAction;
	
	private boolean autoUpdate = true;
	private boolean viewOnly = false;
	
	private PmDigiSign pmDigiSign;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtContractItem selectedItemForSpec;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContractItem> ctItemFilter;
	private HashMap<Long, CtAgency> agencyMap;
	private HashMap<Long, CtContractItem> itemMap;	
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName, the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
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
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
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
		if(attachmentList == null)
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
	 * @return the ctItemFilter
	 */
	public List<CtContractItem> getCtItemFilter() {
		return ctItemFilter;
	}

	/**
	 * @param ctItemFilter the ctItemFilter to set
	 */
	public void setCtItemFilter(List<CtContractItem> ctItemFilter) {
		this.ctItemFilter = ctItemFilter;
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
	 * @return the viewOnly
	 */
	public boolean isViewOnly() {
		return viewOnly;
	}

	/**
	 * @param viewOnly the viewOnly to set
	 */
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}

	/**
	 * @return the agencyMap
	 */
	public HashMap<Long, CtAgency> getAgencyMap() {
		if(agencyMap == null) {
			setAgencyMap(new HashMap<Long, CtAgency>());
		}
		return agencyMap;
	}

	/**
	 * @param agencyMap the agencyMap to set
	 */
	public void setAgencyMap(HashMap<Long, CtAgency> agencyMap) {
		this.agencyMap = agencyMap;
	}
	
	/**
	 * @return the itemMap
	 */
	public HashMap<Long, CtContractItem> getItemMap() {
		if(itemMap == null) {
			setItemMap(new HashMap<Long, CtContractItem>());
		}
		return itemMap;
	}

	/**
	 * @param itemMap the itemMap to set
	 */
	public void setItemMap(HashMap<Long, CtContractItem> itemMap) {
		this.itemMap = itemMap;
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
	 * @return the selectedItemForSpec
	 */
	public CtContractItem getSelectedItemForSpec() {
		return selectedItemForSpec;
	}

	/**
	 * @param selectedItemForSpec the selectedItemForSpec to set
	 */
	public void setSelectedItemForSpec(CtContractItem selectedItemForSpec) {
		this.selectedItemForSpec = selectedItemForSpec;
	}	
}
