package my.ep.web.bean.sm;

public class SupportingDocumentBean {

	private String no;
	private String documentName;
	private String documentdescription;
	private boolean docSent;
	private String uploadDocument;
	private String addedDocumentName;
	private String Databeanbean;
	private String isSentBySupplier;
	private String documentDownload;
	private boolean documentValid;

	public SupportingDocumentBean() {
	}

	public SupportingDocumentBean(String no, String documentname, String documentDescription,
			boolean sent, String uploadDocument, String addedDocumentName, String isSentBySupplier, String documentDownload, boolean documentValid  ) {
		this.no = no;
		this.documentName = documentname;
		this.documentdescription = documentDescription;
		this.docSent = sent;
		this.uploadDocument = uploadDocument;
		this.addedDocumentName = addedDocumentName;
		this.isSentBySupplier = isSentBySupplier;
		this.documentDownload = documentDownload;
		this.documentValid = documentValid;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
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

	public String getDatabeanbean() {
		return Databeanbean;
	}

	public void setDatabeanbean(String databeanbean) {
		Databeanbean = databeanbean;
	}

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

	

}
