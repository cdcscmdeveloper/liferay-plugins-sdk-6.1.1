package my.ep.web.managedbean.fl.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.fl.AuditFLSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managedbean for FL audit
 * 
 * @author razin
 * @since 26/07/2024
 */
@ManagedBean(name = "auFlMB")
@SessionScoped
public class AuditFulfilmentManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long docId;
	private String docType;
	private String backURL;
	private String language;

	private AuditFLSearchBean auditFLSearchBean;

	private boolean isLazy = false;

	/**
	 * Instantiates a new request note managed bean.
	 */
	public AuditFulfilmentManagedBean() {
		/*
    	 * 
    	 */
	}

	/**
	 * @return the docId
	 */
	public Long getDocId() {
		return docId;
	}

	/**
	 * @param docId
	 *            the docId to set
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
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the backURL
	 */
	public String getBackURL() {
		return backURL;
	}

	/**
	 * @param backURL
	 *            the backURL to set
	 */
	public void setBackURL(String backURL) {
		this.backURL = backURL;
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
	 * @return the auditFLSearchBean
	 */
	public AuditFLSearchBean getAuditFLSearchBean() {
		if (auditFLSearchBean == null){
			auditFLSearchBean = new AuditFLSearchBean();			
		}
		return auditFLSearchBean;
	}

	/**
	 * @param auditFLSearchBean
	 *            the auditFLSearchBean to set
	 */
	public void setAuditFLSearchBean(AuditFLSearchBean auditFLSearchBean) {
		this.auditFLSearchBean = auditFLSearchBean;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

}
