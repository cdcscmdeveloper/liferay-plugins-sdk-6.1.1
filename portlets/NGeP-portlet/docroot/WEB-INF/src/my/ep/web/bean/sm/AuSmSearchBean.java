package my.ep.web.bean.sm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Firdaus Omar
 * @since 06.08.2024 #28895_35501
 */

public class AuSmSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String reqType;
	private String language;
	private BigInteger docId;
	private String docType;
	private BigDecimal auGroupSection;

	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public BigDecimal getAuGroupSection() {
		return auGroupSection;
	}

	public void setAuGroupSection(BigDecimal auGroupSection) {
		this.auGroupSection = auGroupSection;
	}
}
