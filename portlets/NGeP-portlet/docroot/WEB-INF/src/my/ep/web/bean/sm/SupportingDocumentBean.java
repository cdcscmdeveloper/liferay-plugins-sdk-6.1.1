package my.ep.web.bean.sm;

public class SupportingDocumentBean {

	private int no;
	private String documentName;
	private String documentdescription;
	private boolean docSent;
	private String uploadDocument;
	private String addedDocumentName;
	/* private String Databeanbean; */
	private String isSentBySupplier;
	private String documentDownload;
	private boolean documentValid;
	private String remarksBySdo;
	// cancelRegisteredCategory
	private int code;
	private String parentCategory;
	private String childCategory;
	private String subChildCategory;
	private String businessNature;
	private String type;
	private boolean action;
	private String remarks;
	// DisciplinaryAction
	private String actionPerformed;
	private String dateActionPerformed;
	private boolean recordSelected;

	public SupportingDocumentBean() {
	}

	public SupportingDocumentBean(int no, String documentname,
			String documentDescription, boolean sent, String uploadDocument,
			String addedDocumentName, String isSentBySupplier,
			String documentDownload, boolean documentValid,
			String remarksBySdo, int code, String parentCategory,
			String childCategory, String subChildCategory,
			String businessNature, String type, boolean action, String remarks,
			String actionPerformed, String dateactionPerformed) {
		this.no = no;
		this.documentName = documentname;
		this.documentdescription = documentDescription;
		this.docSent = sent;
		this.uploadDocument = uploadDocument;
		this.addedDocumentName = addedDocumentName;
		this.isSentBySupplier = isSentBySupplier;
		this.documentDownload = documentDownload;
		this.documentValid = documentValid;
		this.remarksBySdo = remarksBySdo;
		this.code = code;
		this.parentCategory = parentCategory;
		this.childCategory = childCategory;
		this.subChildCategory = subChildCategory;
		this.businessNature = businessNature;
		this.type = type;
		this.action = action;
		this.remarks = remarks;
		this.actionPerformed = actionPerformed;
		this.dateActionPerformed = dateactionPerformed;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentdescription() {
		return documentdescription;
	}

	public void setDocumentdescription(String documentdescription) {
		this.documentdescription = documentdescription;
	}

	public boolean isDocSent() {
		return docSent;
	}

	public void setDocSent(boolean docSent) {
		this.docSent = docSent;
	}

	/*
	 * public String getDatabeanbean() { return Databeanbean; }
	 * 
	 * public void setDatabeanbean(String databeanbean) { Databeanbean =
	 * databeanbean; }
	 */

	public String getUploadDocument() {
		return uploadDocument;
	}

	public void setUploadDocument(String uploadDocument) {
		this.uploadDocument = uploadDocument;
	}

	public String getAddedDocumentName() {
		return addedDocumentName;
	}

	public void setAddedDocumentName(String addedDocumentName) {
		this.addedDocumentName = addedDocumentName;
	}

	public String getIsSentBySupplier() {
		return isSentBySupplier;
	}

	public void setIsSentBySupplier(String isSentBySupplier) {
		this.isSentBySupplier = isSentBySupplier;
	}

	public String getDocumentDownload() {
		return documentDownload;
	}

	public void setDocumentDownload(String documentDownload) {
		this.documentDownload = documentDownload;
	}

	public boolean isDocumentValid() {
		return documentValid;
	}

	public void setDocumentValid(boolean documentValid) {
		this.documentValid = documentValid;
	}

	public String getRemarksBySdo() {
		return remarksBySdo;
	}

	public void setRemarksBySdo(String remarksBySdo) {
		this.remarksBySdo = remarksBySdo;
	}

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(String childCategory) {
		this.childCategory = childCategory;
	}

	public String getSubChildCategory() {
		return subChildCategory;
	}

	public void setSubChildCategory(String subChildCategory) {
		this.subChildCategory = subChildCategory;
	}

	public String getBusinessNature() {
		return businessNature;
	}

	public void setBusinessNature(String businessNature) {
		this.businessNature = businessNature;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public String getActionPerformed() {
		return actionPerformed;
	}

	public void setActionPerformed(String actionPerformed) {
		this.actionPerformed = actionPerformed;
	}

	public String getDateActionPerformed() {
		return dateActionPerformed;
	}

	public void setDateActionPerformed(String dateActionPerformed) {
		this.dateActionPerformed = dateActionPerformed;
	}

	public boolean isRecordSelected() {
		return recordSelected;
	}

	public void setRecordSelected(boolean recordSelected) {
		this.recordSelected = recordSelected;
	}

}
