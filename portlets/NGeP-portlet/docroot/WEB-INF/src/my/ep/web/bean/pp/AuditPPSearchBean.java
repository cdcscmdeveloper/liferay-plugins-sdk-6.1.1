package my.ep.web.bean.pp;

import java.io.Serializable;

/**
 * New file introduced for Audit Trail Procurement Plan (#28895 - #35502)
 * Search Bean for PP audit report/view
 * 
 * @author irfan
 * @since 20/03/2025
 */

public class AuditPPSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String language;
	private boolean isSearching = true;
	private String docType;
	private Integer docID;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Integer getDocID() {
		return docID;
	}

	public void setDocID(Integer docID) {
		this.docID = docID;
	}

}
