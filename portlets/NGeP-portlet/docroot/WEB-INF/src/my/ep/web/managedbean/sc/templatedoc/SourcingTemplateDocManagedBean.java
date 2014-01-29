/**
 * 
 */
package my.ep.web.managedbean.sc.templatedoc;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "TemplateDMB")
@SessionScoped
public class SourcingTemplateDocManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ScQtDoc scQtDoc;
	private Long procurementId;
	private Long statusId;
	private boolean isRecordStatus;
	private List<ScQtDoc> templateDocList;
	private String docType;
	private List<ScQtCheckList> checkListItem;
	private String checklistTitle;
	private String docTitle;
	private String docDescription;

	/**
	 * @return the scQtDoc
	 */
	public ScQtDoc getScQtDoc() {
		return scQtDoc;
	}

	/**
	 * @param scQtDoc
	 *            the scQtDoc to set
	 */
	public void setScQtDoc(ScQtDoc scQtDoc) {
		this.scQtDoc = scQtDoc;
	}

	/**
	 * @return the procurementId
	 */
	public Long getProcurementId() {
		return procurementId;
	}

	/**
	 * @param procurementId
	 *            the procurementId to set
	 */
	public void setProcurementId(Long procurementId) {
		this.procurementId = procurementId;
	}

	/**
	 * @return the statusId
	 */
	public Long getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId
	 *            the statusId to set
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the templateDocList
	 */
	public List<ScQtDoc> getTemplateDocList() {
		return templateDocList;
	}

	/**
	 * @param templateDocList
	 *            the templateDocList to set
	 */
	public void setTemplateDocList(List<ScQtDoc> templateDocList) {
		this.templateDocList = templateDocList;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the checklistTitle
	 */
	public String getChecklistTitle() {
		return checklistTitle;
	}

	/**
	 * @param checklistTitle
	 *            the checklistTitle to set
	 */
	public void setChecklistTitle(String checklistTitle) {
		this.checklistTitle = checklistTitle;
	}

	/**
	 * @return the checkListItem
	 */
	public List<ScQtCheckList> getCheckListItem() {
		return checkListItem;
	}

	/**
	 * @param checkListItem
	 *            the checkListItem to set
	 */
	public void setCheckListItem(List<ScQtCheckList> checkListItem) {
		this.checkListItem = checkListItem;
	}

	/**
	 * @return the docTitle
	 */
	public String getDocTitle() {
		return docTitle;
	}

	/**
	 * @param docTitle
	 *            the docTitle to set
	 */
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	/**
	 * @return the docDescription
	 */
	public String getDocDescription() {
		return docDescription;
	}

	/**
	 * @param docDescription
	 *            the docDescription to set
	 */
	public void setDocDescription(String docDescription) {
		this.docDescription = docDescription;
	}

	/**
	 * @return the isRecordStatus
	 */
	public boolean isRecordStatus() {
		return isRecordStatus;
	}

	/**
	 * @param isRecordStatus
	 *            the isRecordStatus to set
	 */
	public void setRecordStatus(boolean isRecordStatus) {
		this.isRecordStatus = isRecordStatus;
	}

}
