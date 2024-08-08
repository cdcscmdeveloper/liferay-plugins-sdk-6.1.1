package my.ep.web.bean.sm;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

public class AuSmSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String language;
	private BigInteger docId;
	private String docType;
	private String tabName;
	private BigInteger auGroupSection;
	private boolean isSearching = true;

	public AuSmSearchBean() {
	}

	public AuSmSearchBean(String language, BigInteger docId) {
		this.language = language;
		this.docId = docId;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public BigInteger getAuGroupSection() {
		return auGroupSection;
	}

	public void setAuGroupSection(BigInteger auGroupSection) {
		this.auGroupSection = auGroupSection;
	}
}
