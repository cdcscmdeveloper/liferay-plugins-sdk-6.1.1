package my.ep.web.bean.sc.directpurchase;

import java.io.Serializable;
import java.math.BigDecimal;

public class AuditDPSearchBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private BigDecimal docId;
	private boolean isSearching = true;
	
	public AuditDPSearchBean() {}
	
	public AuditDPSearchBean(String language, BigDecimal docId) {
		this.language = language;
		this.docId = docId;
	}

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
	public BigDecimal getDocId() {
		return docId;
	}

	/**
	 * @param docId the docId to set
	 */
	public void setDocId(BigDecimal docId) {
		this.docId = docId;
	}

	public boolean isSearching() {
		return isSearching;
	}

	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}
}
