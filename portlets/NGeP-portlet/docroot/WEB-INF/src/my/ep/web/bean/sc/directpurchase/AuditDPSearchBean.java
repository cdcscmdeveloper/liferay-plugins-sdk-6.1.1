package my.ep.web.bean.sc.directpurchase;

import java.io.Serializable;
import java.math.BigInteger;
import org.apache.log4j.Logger;

/**
 * 
 * @author normuliana #28895_35492
 * 
 */
public class AuditDPSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	private BigInteger docId;
	private boolean isSearching = true;

	/**
	 * AuditDPSearchBean()
	 */
	public AuditDPSearchBean() {

	}

	/**
	 * 
	 * @param language
	 * @param docId
	 */
	public AuditDPSearchBean(String language, BigInteger docId) {
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
	 * @param language
	 *            the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the docId
	 */
	public BigInteger getDocId() {
		return docId;
	}

	/**
	 * @param docId
	 *            the docId to set
	 */
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
