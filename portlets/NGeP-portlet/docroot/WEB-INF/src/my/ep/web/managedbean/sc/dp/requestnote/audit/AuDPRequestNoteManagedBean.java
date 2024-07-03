/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.dp.requestnote.audit;

import java.math.BigInteger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.dto.sc.audit.AuDPRequestNoteDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestReqDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.web.bean.sc.directpurchase.AuditDPSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * 
 * @author normuliana #28895_35492
 * 
 */
@ManagedBean(name = "auDPRequestNoteMB")
@SessionScoped
public class AuDPRequestNoteManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PurchaseRequestResDTO purchaseRequestResDTO;

	private PurchaseRequestReqDTO purchaseRequestReqDTO;

	private RequestNoteReqDTO reqNoteDTO;

	private AuDPRequestNoteDTO auDPRequestNoteDTO;

	private AuditDPSearchBean auditDPSearchBean;

	private boolean isLazy = false;

	/** The Audit trail view. */
	private boolean auditTrailView;

	/** The Audit trail view URL. */
	private String backURL;

	private String language;

	private BigInteger docId;

	/**
	 * Instantiates a new request note managed bean.
	 */
	public AuDPRequestNoteManagedBean() {
		/*
    	 * 
    	 */
	}

	public PurchaseRequestResDTO getPurchaseRequestResDTO() {
		return purchaseRequestResDTO;
	}

	public void setPurchaseRequestResDTO(PurchaseRequestResDTO purchaseRequestResDTO) {
		this.purchaseRequestResDTO = purchaseRequestResDTO;
	}

	public PurchaseRequestReqDTO getPurchaseRequestReqDTO() {
		return purchaseRequestReqDTO;
	}

	public void setPurchaseRequestReqDTO(PurchaseRequestReqDTO purchaseRequestReqDTO) {
		this.purchaseRequestReqDTO = purchaseRequestReqDTO;
	}

	public RequestNoteReqDTO getReqNoteDTO() {
		return reqNoteDTO;
	}

	public void setReqNoteDTO(RequestNoteReqDTO reqNoteDTO) {
		this.reqNoteDTO = reqNoteDTO;
	}

	public AuDPRequestNoteDTO getAuDPRequestNoteDTO() {
		return auDPRequestNoteDTO;
	}

	public void setAuDPRequestNoteDTO(AuDPRequestNoteDTO auDPRequestNoteDTO) {
		this.auDPRequestNoteDTO = auDPRequestNoteDTO;
	}

	public AuditDPSearchBean getAuditDPSearchBean() {
		if (auditDPSearchBean == null)
			auditDPSearchBean = new AuditDPSearchBean();
		return auditDPSearchBean;
	}

	public void setAuditDPSearchBean(AuditDPSearchBean auditDPSearchBean) {
		this.auditDPSearchBean = auditDPSearchBean;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public boolean isAuditTrailView() {
		return auditTrailView;
	}

	public void setAuditTrailView(boolean auditTrailView) {
		this.auditTrailView = auditTrailView;
	}

	public String getBackURL() {
		return backURL;
	}

	public void setBackURL(String backURL) {
		this.backURL = backURL;
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
}
