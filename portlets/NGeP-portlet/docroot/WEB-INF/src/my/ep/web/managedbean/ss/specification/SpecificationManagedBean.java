/**
 * 
 */
package my.ep.web.managedbean.ss.specification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScRemark;
import my.ep.web.backingbean.ss.specification.SpecificationCompilanceBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "specificationMB")
@SessionScoped
public class SpecificationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SpecificationManagedBean.class);

	private ScQtSpec scQtSpec;
	
	private ScQtRemark scQtRemarks;
	
	private ScQtCheckList scQtChecklist;
	
	private List<ScQtCheckList> scQtCheckSt1techProposal;
	
	private ScQtDeduction scQtDeduction;
	
	private List<ScQtDeduction> scQtDeductionList;
	
	private List<ScQtCheckList> scQtCheckSt1techAdditionalProposalList;
	private List<ScQtCheckList> scQtCheckSt1financeProposalList;
	
	private List<ScQtCheckList> scQtCheckSt1financeAdditionalProposalList;
	
	private List<ScQtCheckList> scQtCheckSt2techProposal;
	
	private List<ScQtCheckList> scQtCheckSt2techAdditionalProposalList;
	private List<ScQtCheckList> scQtCheckSt2financeProposalList;
	
	private List<ScQtCheckList> scQtCheckSt2financeAdditionalProposalList;
	
	private List<String> compilanceList=new ArrayList<String>();
	 public List<SpecificationCompilanceBean> compilaenceList;
	 private ScRemark scRemark;
	 private List<SelectItem> comitteeUsers;
	 private List<SelectItem> currencyListDetails;
	 private List<SelectItem> procurementListDetails;
	 private List<ScCommitteeMember> committeeMemberList;
	 
	 private Object specificationDataType;
	
	
	public boolean isSampleReq=true;
	public String approvalDate;

	/**
	 * @return the scQtSpec
	 */
	public ScQtSpec getScQtSpec() {
		return scQtSpec;
	}

	/**
	 * @param scQtSpec the scQtSpec to set
	 */
	public void setScQtSpec(ScQtSpec scQtSpec) {
		this.scQtSpec = scQtSpec;
	}

	/**
	 * @return the scQtRemarks
	 */
	public ScQtRemark getScQtRemarks() {
		return scQtRemarks;
	}

	/**
	 * @param scQtRemarks the scQtRemarks to set
	 */
	public void setScQtRemarks(ScQtRemark scQtRemarks) {
		this.scQtRemarks = scQtRemarks;
	}

	/**
	 * @return the scQtChecklist
	 */
	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	/**
	 * @param scQtChecklist the scQtChecklist to set
	 */
	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
	}

	/**
	 * @return the scQtCheckSt1techProposal
	 */
	public List<ScQtCheckList> getScQtCheckSt1techProposal() {
		return scQtCheckSt1techProposal;
	}

	/**
	 * @param scQtCheckSt1techProposal the scQtCheckSt1techProposal to set
	 */
	public void setScQtCheckSt1techProposal(
			List<ScQtCheckList> scQtCheckSt1techProposal) {
		this.scQtCheckSt1techProposal = scQtCheckSt1techProposal;
	}

	/**
	 * @return the scQtDeduction
	 */
	public ScQtDeduction getScQtDeduction() {
		return scQtDeduction;
	}

	/**
	 * @param scQtDeduction the scQtDeduction to set
	 */
	public void setScQtDeduction(ScQtDeduction scQtDeduction) {
		this.scQtDeduction = scQtDeduction;
	}

	/**
	 * @return the scQtDeductionList
	 */
	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	/**
	 * @param scQtDeductionList the scQtDeductionList to set
	 */
	public void setScQtDeductionList(List<ScQtDeduction> scQtDeductionList) {
		this.scQtDeductionList = scQtDeductionList;
	}

	/**
	 * @return the scQtCheckSt1techAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1techAdditionalProposalList() {
		return scQtCheckSt1techAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt1techAdditionalProposalList the scQtCheckSt1techAdditionalProposalList to set
	 */
	public void setScQtCheckSt1techAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt1techAdditionalProposalList) {
		this.scQtCheckSt1techAdditionalProposalList = scQtCheckSt1techAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt1financeProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1financeProposalList() {
		return scQtCheckSt1financeProposalList;
	}

	/**
	 * @param scQtCheckSt1financeProposalList the scQtCheckSt1financeProposalList to set
	 */
	public void setScQtCheckSt1financeProposalList(
			List<ScQtCheckList> scQtCheckSt1financeProposalList) {
		this.scQtCheckSt1financeProposalList = scQtCheckSt1financeProposalList;
	}

	/**
	 * @return the scQtCheckSt1financeAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt1financeAdditionalProposalList() {
		return scQtCheckSt1financeAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt1financeAdditionalProposalList the scQtCheckSt1financeAdditionalProposalList to set
	 */
	public void setScQtCheckSt1financeAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt1financeAdditionalProposalList) {
		this.scQtCheckSt1financeAdditionalProposalList = scQtCheckSt1financeAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt2techProposal
	 */
	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	/**
	 * @param scQtCheckSt2techProposal the scQtCheckSt2techProposal to set
	 */
	public void setScQtCheckSt2techProposal(
			List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}

	/**
	 * @return the scQtCheckSt2techAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2techAdditionalProposalList() {
		return scQtCheckSt2techAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt2techAdditionalProposalList the scQtCheckSt2techAdditionalProposalList to set
	 */
	public void setScQtCheckSt2techAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt2techAdditionalProposalList) {
		this.scQtCheckSt2techAdditionalProposalList = scQtCheckSt2techAdditionalProposalList;
	}

	/**
	 * @return the scQtCheckSt2financeProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeProposalList() {
		return scQtCheckSt2financeProposalList;
	}

	/**
	 * @param scQtCheckSt2financeProposalList the scQtCheckSt2financeProposalList to set
	 */
	public void setScQtCheckSt2financeProposalList(
			List<ScQtCheckList> scQtCheckSt2financeProposalList) {
		this.scQtCheckSt2financeProposalList = scQtCheckSt2financeProposalList;
	}

	/**
	 * @return the scQtCheckSt2financeAdditionalProposalList
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeAdditionalProposalList() {
		return scQtCheckSt2financeAdditionalProposalList;
	}

	/**
	 * @param scQtCheckSt2financeAdditionalProposalList the scQtCheckSt2financeAdditionalProposalList to set
	 */
	public void setScQtCheckSt2financeAdditionalProposalList(
			List<ScQtCheckList> scQtCheckSt2financeAdditionalProposalList) {
		this.scQtCheckSt2financeAdditionalProposalList = scQtCheckSt2financeAdditionalProposalList;
	}

	/**
	 * @return the compilanceList
	 */
	public List<String> getCompilanceList() {
		return compilanceList;
	}

	/**
	 * @param compilanceList the compilanceList to set
	 */
	public void setCompilanceList(List<String> compilanceList) {
		this.compilanceList = compilanceList;
	}

	/**
	 * @return the compilaenceList
	 */
	public List<SpecificationCompilanceBean> getCompilaenceList() {
		return compilaenceList;
	}

	/**
	 * @param compilaenceList the compilaenceList to set
	 */
	public void setCompilaenceList(List<SpecificationCompilanceBean> compilaenceList) {
		this.compilaenceList = compilaenceList;
	}

	/**
	 * @return the scRemark
	 */
	public ScRemark getScRemark() {
		return scRemark;
	}

	/**
	 * @param scRemark the scRemark to set
	 */
	public void setScRemark(ScRemark scRemark) {
		this.scRemark = scRemark;
	}

	/**
	 * @return the isSampleReq
	 */
	public boolean isSampleReq() {
		return isSampleReq;
	}

	/**
	 * @param isSampleReq the isSampleReq to set
	 */
	public void setSampleReq(boolean isSampleReq) {
		this.isSampleReq = isSampleReq;
	}

	/**
	 * @return the comitteeUsers
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * @param comitteeUsers the comitteeUsers to set
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * @return the approvalDate
	 */
	public String getApprovalDate() {
		return approvalDate;
	}

	/**
	 * @param approvalDate the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return the currencyListDetails
	 */
	public List<SelectItem> getCurrencyListDetails() {
		return currencyListDetails;
	}

	/**
	 * @param currencyListDetails the currencyListDetails to set
	 */
	public void setCurrencyListDetails(List<SelectItem> currencyListDetails) {
		this.currencyListDetails = currencyListDetails;
	}

	/**
	 * @return the procurementListDetails
	 */
	public List<SelectItem> getProcurementListDetails() {
		return procurementListDetails;
	}

	/**
	 * @param procurementListDetails the procurementListDetails to set
	 */
	public void setProcurementListDetails(List<SelectItem> procurementListDetails) {
		this.procurementListDetails = procurementListDetails;
	}

	/**
	 * @return the committeeMemberList
	 */
	public List<ScCommitteeMember> getCommitteeMemberList() {
		return committeeMemberList;
	}

	/**
	 * @param committeeMemberList the committeeMemberList to set
	 */
	public void setCommitteeMemberList(List<ScCommitteeMember> committeeMemberList) {
		this.committeeMemberList = committeeMemberList;
	}

	public Object getSpecificationDataType() {
		return specificationDataType;
	}

	public void setSpecificationDataType(Object specificationDataType) {
		this.specificationDataType = specificationDataType;
	}

	

	


	
	

}
