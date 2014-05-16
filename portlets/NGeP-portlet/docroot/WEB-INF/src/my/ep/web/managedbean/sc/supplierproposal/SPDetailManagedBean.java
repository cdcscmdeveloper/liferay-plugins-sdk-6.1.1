package my.ep.web.managedbean.sc.supplierproposal;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "sPDetailManagedBean")
@SessionScoped
public class SPDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SPDetailManagedBean.class);

	private ScQt qt;
	private ScQtProposal qtProposal;
	private ScQtProposalChecklist qtProposalChecklist;
	private List<ScQtProposalChecklist> tecCheckDetailsList;
	private List<ScQtProposalChecklist> financeCheckDetailsList;
	private List<ScQtProposalChecklist> selectedCheckDetails;

	private ScQtCheckList scQtCheckList;

	// Tab Menu
	private int prepareActiveIndex;

	private ScQtProposalChecklist selectDetails;
	public boolean checkStatus = true;

	// payLoad object Added By Vidyasagar.
	private Object proposalData;
	private String supplierId;

	// private ScAttachment scAttachment = new ScAttachment();
	private List<ScAttachment> scAttachmentList;

	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	// Non-Federal Government
	private List<ScQtPRPExperience> scQtPRPExperiences;
	private List<ScQtPRPExperience> selectedPRPExperiences;

	public SPDetailManagedBean() {
		log.info("inside ");
	}

	public boolean isCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	public ScQtProposalChecklist getSelectDetails() {
		return selectDetails;
	}

	public void setSelectDetails(ScQtProposalChecklist selectDetails) {
		this.selectDetails = selectDetails;
	}

	public List<ScAttachment> getScAttachmentList() {
		return scAttachmentList;
	}

	public void setScAttachmentList(List<ScAttachment> scAttachmentList) {
		this.scAttachmentList = scAttachmentList;
	}

	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

	public List<ScQtProposalChecklist> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	public void setSelectedCheckDetails(
			List<ScQtProposalChecklist> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
	}

	public List<ScQtProposalChecklist> getFinanceCheckDetailsList() {
		return financeCheckDetailsList;
	}

	public void setFinanceCheckDetailsList(
			List<ScQtProposalChecklist> financeCheckDetailsList) {
		this.financeCheckDetailsList = financeCheckDetailsList;
	}

	public List<ScQtProposalChecklist> getTecCheckDetailsList() {
		return tecCheckDetailsList;
	}

	public void setTecCheckDetailsList(
			List<ScQtProposalChecklist> tecCheckDetailsList) {
		this.tecCheckDetailsList = tecCheckDetailsList;
	}

	public ScQtProposal getQtProposal() {
		return qtProposal;
	}

	public void setQtProposal(ScQtProposal qtProposal) {
		this.qtProposal = qtProposal;
	}

	public ScQt getQt() {
		return qt;
	}

	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	public Object getProposalData() {
		return proposalData;
	}

	public void setProposalData(Object proposalData) {
		this.proposalData = proposalData;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	public ScQtCheckList getScQtCheckList() {
		return scQtCheckList;
	}

	public void setScQtCheckList(ScQtCheckList scQtCheckList) {
		this.scQtCheckList = scQtCheckList;
	}

	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	public List<ScQtPRPExperience> getScQtPRPExperiences() {
		return scQtPRPExperiences;
	}

	public void setScQtPRPExperiences(List<ScQtPRPExperience> scQtPRPExperiences) {
		this.scQtPRPExperiences = scQtPRPExperiences;
	}

	public List<ScQtPRPExperience> getSelectedPRPExperiences() {
		return selectedPRPExperiences;
	}

	public void setSelectedPRPExperiences(
			List<ScQtPRPExperience> selectedPRPExperiences) {
		this.selectedPRPExperiences = selectedPRPExperiences;
	}

}
