/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.suspend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

import my.ep.web.managedbean.BaseManagedBean;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.pm.common.PmCountry;
/**
 * Managed Bean For Contract Suspension
 * 
 * @author Alam
 * @since 22/10/2013	 
 */
@ManagedBean(name ="contractSuspendManagedBean")
@SessionScoped
public class ContractSuspendManagedBean  extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ContractSuspendManagedBean.class);
	
	private String taskId;
	private String[] outComes;
	
	private Date endDate;
	private Date startDate;
	private boolean activeSuspension;
	private boolean booleanSuspendValue;
	
	private TreeNode suspendLocTN;
	private Map<PmCountry, TreeNode> suspendLocMap;
	private Long contractId;
	
	private CtContract contract = new CtContract();
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	public ContractSuspendManagedBean(){
		
	}

	public boolean isActiveSuspension() {
		return activeSuspension;
	}
	
	public CtContract getContract() {
		return contract;
	}

	public void setContract(CtContract contract) {
		this.contract = contract;
	}

	public void setActiveSuspension(boolean activeSuspension) {
		this.activeSuspension = activeSuspension;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Map<PmCountry, TreeNode> getSuspendLocMap() {
		return suspendLocMap;
	}

	public void setSuspendLocMap(Map<PmCountry, TreeNode> suspendLocMap) {
		this.suspendLocMap = suspendLocMap;
	}

	public TreeNode getSuspendLocTN() {
		return suspendLocTN;
	}

	public void setSuspendLocTN(TreeNode suspendLocTN) {
		this.suspendLocTN = suspendLocTN;
	}

	public boolean isBooleanSuspendValue() {
		return booleanSuspendValue;
	}

	public void setBooleanSuspendValue(boolean booleanSuspendValue) {
		this.booleanSuspendValue = booleanSuspendValue;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
}
