/**
 * 
 */
package my.ep.web.bean.fl;

import java.io.Serializable;

/**
 * class for audit fl search bean
 * 
 * @author razin
 *
 */
public class AuditFLSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private Long docId;
	private String docType;
	private boolean isSearching = true;
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the docId
	 */
	public Long getDocId() {
		return docId;
	}
	/**
	 * @param docId the docId to set
	 */
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}
	/**
	 * @return the isSearching
	 */
	public boolean isSearching() {
		return isSearching;
	}
	/**
	 * @param isSearching the isSearching to set
	 */
	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}


}
