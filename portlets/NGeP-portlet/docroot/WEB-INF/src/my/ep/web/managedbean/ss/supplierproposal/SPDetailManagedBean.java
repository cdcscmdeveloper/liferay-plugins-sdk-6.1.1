package my.ep.web.managedbean.ss.supplierproposal;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ss.quationtender.ScAttachment;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtProposal;
import my.ep.persistence.entity.ss.quationtender.ScQtProposalChecklist;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "sPDetailMB")
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
	
	private ScQtProposalChecklist selectDetails;
	
	//private ScAttachment scAttachment = new ScAttachment();
	private List<ScAttachment> scAttachmentList; 
	public SPDetailManagedBean() {
		log.info("inside ");
	}

	/**
	 * @return the selectDetails
	 */
	public ScQtProposalChecklist getSelectDetails() {
		return selectDetails;
	}

	/**
	 * @param selectDetails the selectDetails to set
	 */
	public void setSelectDetails(ScQtProposalChecklist selectDetails) {
		this.selectDetails = selectDetails;
	}

	/**
	 * @return the scAttachmentList
	 */
	public List<ScAttachment> getScAttachmentList() {
		return scAttachmentList;
	}

	/**
	 * @param scAttachmentList the scAttachmentList to set
	 */
	public void setScAttachmentList(List<ScAttachment> scAttachmentList) {
		this.scAttachmentList = scAttachmentList;
	}

	/**
	 * @return the qtProposalChecklist
	 */
	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	/**
	 * @param qtProposalChecklist the qtProposalChecklist to set
	 */
	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

/*	*//**
	 * @return the scAttachment
	 *//*
	public ScAttachment getScAttachment() {
		return scAttachment;
	}

	*//**
	 * @param scAttachment the scAttachment to set
	 *//*
	public void setScAttachment(ScAttachment scAttachment) {
		this.scAttachment = scAttachment;
	}
*/
	/**
	 * @return the selectedCheckDetails
	 */
	public List<ScQtProposalChecklist> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	/**
	 * @param selectedCheckDetails the selectedCheckDetails to set
	 */
	public void setSelectedCheckDetails(
			List<ScQtProposalChecklist> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
	}

	/**
	 * @return the financeCheckDetailsList
	 */
	public List<ScQtProposalChecklist> getFinanceCheckDetailsList() {
		return financeCheckDetailsList;
	}

	/**
	 * @param financeCheckDetailsList the financeCheckDetailsList to set
	 */
	public void setFinanceCheckDetailsList(
			List<ScQtProposalChecklist> financeCheckDetailsList) {
		this.financeCheckDetailsList = financeCheckDetailsList;
	}

	/**
	 * @return the tecCheckDetailsList
	 */
	public List<ScQtProposalChecklist> getTecCheckDetailsList() {
		return tecCheckDetailsList;
	}

	/**
	 * @param tecCheckDetailsList the tecCheckDetailsList to set
	 */
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

}
