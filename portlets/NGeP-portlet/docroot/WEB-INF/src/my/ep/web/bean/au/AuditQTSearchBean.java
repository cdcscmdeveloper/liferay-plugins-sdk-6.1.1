package my.ep.web.bean.au;

import java.io.Serializable;
import java.math.BigInteger;

public class AuditQTSearchBean implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private BigInteger docId;
	private boolean isSearching = true;
	
	public AuditQTSearchBean(String language, BigInteger docId) {
		this.language = language;
		this.docId = docId;
	}
	public AuditQTSearchBean() {
		// TODO Auto-generated constructor stub
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public BigInteger getDocId() {
		return docId;
	}
	public void setDocId(BigInteger docId) {
		this.docId = docId;
	}
	public boolean isSearching() {
		return isSearching;
	}
	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}

}
