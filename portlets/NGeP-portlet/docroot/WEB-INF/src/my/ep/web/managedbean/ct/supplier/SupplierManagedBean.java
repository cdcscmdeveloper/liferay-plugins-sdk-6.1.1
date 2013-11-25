/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.supplier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.bank.CtSupplierBank;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean For Contract Supplier
 * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean
@SessionScoped
public class SupplierManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String taskId;
	private String[] outComes;
	
	private Date endDate;
	private String loaNo;
	private Date startDate;
	private Long contractNo;
	private String contractName;
	private String loginUser;
	
	private int selectedIndex;
	private CtFactoring factoring;
	private CtTermination termination;
	private CtSupplierBank supplierBank;
	private CtTerminationReason ctTerminationReason;
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	private CtContractValue contractValues = new CtContractValue();
	private CtContract ctContract;
	private CtCircular ctCircular = new CtCircular();
	private CtAgreement ctAgreement = new CtAgreement();
	
	public SupplierManagedBean(){
		
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
	
	public String getLoaNo() {
		return loaNo;
	}
	
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Long getContractNo() {
		return contractNo;
	}
	
	public void setContractNo(Long contractNo) {
		this.contractNo = contractNo;
	}
	
	public String getContractName() {
		return contractName;
	}
	
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public CtFactoring getFactoring() {
		return factoring;
	}
	
	public void setFactoring(CtFactoring factoring) {
		this.factoring = factoring;
	}
	
	public CtSupplierBank getSupplierBank() {
		return supplierBank;
	}
	
	public void setSupplierBank(CtSupplierBank supplierBank) {
		this.supplierBank = supplierBank;
	}
	
	public CtTermination getTermination() {
		return termination;
	}
	
	public void setTermination(CtTermination termination) {
		this.termination = termination;
	}
	
	public CtTerminationReason getCtTerminationReason() {
		return ctTerminationReason;
	}
	
	public void setCtTerminationReason(CtTerminationReason ctTerminationReason) {
		this.ctTerminationReason = ctTerminationReason;
	}
	
	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}
	
	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}
	
	public List<CtContract> getContractList() {
		return contractList;
	}
	
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
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

	public CtContractValue getContractValues() {
		return contractValues;
	}

	public void setContractValues(CtContractValue contractValues) {
		this.contractValues = contractValues;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public CtCircular getCtCircular() {
		return ctCircular;
	}

	public void setCtCircular(CtCircular ctCircular) {
		this.ctCircular = ctCircular;
	}

	public CtAgreement getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(CtAgreement ctAgreement) {
		this.ctAgreement = ctAgreement;
	}
	
	
	
}
