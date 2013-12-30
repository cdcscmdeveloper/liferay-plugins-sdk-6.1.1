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
import my.ep.persistence.entity.pm.common.PmCheckListCategory;
import my.ep.persistence.entity.ss.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDeduction;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScQtSpec;
import my.ep.persistence.entity.ss.quationtender.ScQtWeightage;
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
	private List<ScQtWeightage> weightageTechnical;
	private List<ScQtWeightage> weightageFinancial;
	private List<ScQtWeightage> weightageTechnicalSelect;
	private List<ScQtWeightage> weightageFinancialSelect;
	 private boolean isSpecStatus;
	 private Long procurementId;
	 private List<SelectItem> approvalList;
	 private String evaluationType;
	private ScQt qt;
	private List<PmCheckListCategory> stageOneComplianceList;
	private List<PmCheckListCategory> stageOneComplianceSelect;
	private List<PmCheckListCategory> stageTwoTechComplianceList;
	private List<PmCheckListCategory> stageTwoTechComplianceSelect;
	private List<PmCheckListCategory> stageTwoFinComplianceList;
	private List<PmCheckListCategory> stageTwoFinComplianceSelect;
	private List<ScQtCheckList> s1TechSelect;
	private List<ScQtCheckList> s1TechAddSelect;
	private List<ScQtCheckList> s1FinSelect;
	private List<ScQtCheckList> s1FinAddSelect;
	private List<ScQtCheckList> s2TechSelect;
	private List<ScQtCheckList> s2TechAddSelect;
	private List<ScQtCheckList> s2FinSelect;
	private List<ScQtCheckList> s2FinAddSelect;
	public SpecificationManagedBean(){
		
	}

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

	/**
	 * @return the weightageTechnical
	 */
	public List<ScQtWeightage> getWeightageTechnical() {
		return weightageTechnical;
	}

	/**
	 * @param weightageTechnical the weightageTechnical to set
	 */
	public void setWeightageTechnical(List<ScQtWeightage> weightageTechnical) {
		this.weightageTechnical = weightageTechnical;
	}

	/**
	 * @return the weightageFinancial
	 */
	public List<ScQtWeightage> getWeightageFinancial() {
		return weightageFinancial;
	}

	/**
	 * @param weightageFinancial the weightageFinancial to set
	 */
	public void setWeightageFinancial(List<ScQtWeightage> weightageFinancial) {
		this.weightageFinancial = weightageFinancial;
	}

	/**
	 * @return the weightageTechnicalSelect
	 */
	public List<ScQtWeightage> getWeightageTechnicalSelect() {
		return weightageTechnicalSelect;
	}

	/**
	 * @param weightageTechnicalSelect the weightageTechnicalSelect to set
	 */
	public void setWeightageTechnicalSelect(
			List<ScQtWeightage> weightageTechnicalSelect) {
		this.weightageTechnicalSelect = weightageTechnicalSelect;
	}

	/**
	 * @return the weightageFinancialSelect
	 */
	public List<ScQtWeightage> getWeightageFinancialSelect() {
		return weightageFinancialSelect;
	}

	/**
	 * @param weightageFinancialSelect the weightageFinancialSelect to set
	 */
	public void setWeightageFinancialSelect(
			List<ScQtWeightage> weightageFinancialSelect) {
		this.weightageFinancialSelect = weightageFinancialSelect;
	}

	/**
	 * @return the isSpecStatus
	 */
	public boolean isSpecStatus() {
		return isSpecStatus;
	}

	/**
	 * @param isSpecStatus the isSpecStatus to set
	 */
	public void setSpecStatus(boolean isSpecStatus) {
		this.isSpecStatus = isSpecStatus;
	}

	/**
	 * @return the procurementId
	 */
	public Long getProcurementId() {
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(Long procurementId) {
		this.procurementId = procurementId;
	}

	/**
	 * @return the approvalList
	 */
	public List<SelectItem> getApprovalList() {
		return approvalList;
	}

	/**
	 * @param approvalList the approvalList to set
	 */
	public void setApprovalList(List<SelectItem> approvalList) {
		this.approvalList = approvalList;
	}

	/**
	 * @return the evaluationType
	 */
	public String getEvaluationType() {
		return evaluationType;
	}

	/**
	 * @param evaluationType the evaluationType to set
	 */
	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * @param qt the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * @return the stageOneComplianceList
	 */
	public List<PmCheckListCategory> getStageOneComplianceList() {
		return stageOneComplianceList;
	}

	/**
	 * @param stageOneComplianceList the stageOneComplianceList to set
	 */
	public void setStageOneComplianceList(
			List<PmCheckListCategory> stageOneComplianceList) {
		this.stageOneComplianceList = stageOneComplianceList;
	}

	/**
	 * @return the stageOneComplianceSelect
	 */
	public List<PmCheckListCategory> getStageOneComplianceSelect() {
		return stageOneComplianceSelect;
	}

	/**
	 * @param stageOneComplianceSelect the stageOneComplianceSelect to set
	 */
	public void setStageOneComplianceSelect(
			List<PmCheckListCategory> stageOneComplianceSelect) {
		this.stageOneComplianceSelect = stageOneComplianceSelect;
	}

	/**
	 * @return the stageTwoTechComplianceList
	 */
	public List<PmCheckListCategory> getStageTwoTechComplianceList() {
		return stageTwoTechComplianceList;
	}

	/**
	 * @param stageTwoTechComplianceList the stageTwoTechComplianceList to set
	 */
	public void setStageTwoTechComplianceList(
			List<PmCheckListCategory> stageTwoTechComplianceList) {
		this.stageTwoTechComplianceList = stageTwoTechComplianceList;
	}

	/**
	 * @return the stageTwoTechComplianceSelect
	 */
	public List<PmCheckListCategory> getStageTwoTechComplianceSelect() {
		return stageTwoTechComplianceSelect;
	}

	/**
	 * @param stageTwoTechComplianceSelect the stageTwoTechComplianceSelect to set
	 */
	public void setStageTwoTechComplianceSelect(
			List<PmCheckListCategory> stageTwoTechComplianceSelect) {
		this.stageTwoTechComplianceSelect = stageTwoTechComplianceSelect;
	}

	/**
	 * @return the stageTwoFinComplianceList
	 */
	public List<PmCheckListCategory> getStageTwoFinComplianceList() {
		return stageTwoFinComplianceList;
	}

	/**
	 * @param stageTwoFinComplianceList the stageTwoFinComplianceList to set
	 */
	public void setStageTwoFinComplianceList(
			List<PmCheckListCategory> stageTwoFinComplianceList) {
		this.stageTwoFinComplianceList = stageTwoFinComplianceList;
	}

	/**
	 * @return the stageTwoFinComplianceSelect
	 */
	public List<PmCheckListCategory> getStageTwoFinComplianceSelect() {
		return stageTwoFinComplianceSelect;
	}

	/**
	 * @param stageTwoFinComplianceSelect the stageTwoFinComplianceSelect to set
	 */
	public void setStageTwoFinComplianceSelect(
			List<PmCheckListCategory> stageTwoFinComplianceSelect) {
		this.stageTwoFinComplianceSelect = stageTwoFinComplianceSelect;
	}

	/**
	 * @return the s1TechSelect
	 */
	public List<ScQtCheckList> getS1TechSelect() {
		return s1TechSelect;
	}

	/**
	 * @param s1TechSelect the s1TechSelect to set
	 */
	public void setS1TechSelect(List<ScQtCheckList> s1TechSelect) {
		this.s1TechSelect = s1TechSelect;
	}

	/**
	 * @return the s1TechAddSelect
	 */
	public List<ScQtCheckList> getS1TechAddSelect() {
		return s1TechAddSelect;
	}

	/**
	 * @param s1TechAddSelect the s1TechAddSelect to set
	 */
	public void setS1TechAddSelect(List<ScQtCheckList> s1TechAddSelect) {
		this.s1TechAddSelect = s1TechAddSelect;
	}

	/**
	 * @return the s1FinSelect
	 */
	public List<ScQtCheckList> getS1FinSelect() {
		return s1FinSelect;
	}

	/**
	 * @param s1FinSelect the s1FinSelect to set
	 */
	public void setS1FinSelect(List<ScQtCheckList> s1FinSelect) {
		this.s1FinSelect = s1FinSelect;
	}

	/**
	 * @return the s1FinAddSelect
	 */
	public List<ScQtCheckList> getS1FinAddSelect() {
		return s1FinAddSelect;
	}

	/**
	 * @param s1FinAddSelect the s1FinAddSelect to set
	 */
	public void setS1FinAddSelect(List<ScQtCheckList> s1FinAddSelect) {
		this.s1FinAddSelect = s1FinAddSelect;
	}

	/**
	 * @return the s2TechSelect
	 */
	public List<ScQtCheckList> getS2TechSelect() {
		return s2TechSelect;
	}

	/**
	 * @param s2TechSelect the s2TechSelect to set
	 */
	public void setS2TechSelect(List<ScQtCheckList> s2TechSelect) {
		this.s2TechSelect = s2TechSelect;
	}

	/**
	 * @return the s2TechAddSelect
	 */
	public List<ScQtCheckList> getS2TechAddSelect() {
		return s2TechAddSelect;
	}

	/**
	 * @param s2TechAddSelect the s2TechAddSelect to set
	 */
	public void setS2TechAddSelect(List<ScQtCheckList> s2TechAddSelect) {
		this.s2TechAddSelect = s2TechAddSelect;
	}

	/**
	 * @return the s2FinSelect
	 */
	public List<ScQtCheckList> getS2FinSelect() {
		return s2FinSelect;
	}

	/**
	 * @param s2FinSelect the s2FinSelect to set
	 */
	public void setS2FinSelect(List<ScQtCheckList> s2FinSelect) {
		this.s2FinSelect = s2FinSelect;
	}

	/**
	 * @return the s2FinAddSelect
	 */
	public List<ScQtCheckList> getS2FinAddSelect() {
		return s2FinAddSelect;
	}

	/**
	 * @param s2FinAddSelect the s2FinAddSelect to set
	 */
	public void setS2FinAddSelect(List<ScQtCheckList> s2FinAddSelect) {
		this.s2FinAddSelect = s2FinAddSelect;
	}

	


	

	

	

	


	
	

}
