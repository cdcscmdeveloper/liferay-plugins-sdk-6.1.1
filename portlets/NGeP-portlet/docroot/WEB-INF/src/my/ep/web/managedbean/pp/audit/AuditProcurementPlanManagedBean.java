package my.ep.web.managedbean.pp.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pp.AuditPPSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * New file introduced for Audit Trail Procurement Plan (#28895 - #35502) Manage
 * Bean for PP audit report/view
 * 
 * @author irfan
 * @since 20/03/2025
 */
@ManagedBean(name = "auPPMB")
@SessionScoped
public class AuditProcurementPlanManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String backURL;
	private String language;
	private boolean isLazy = false;
	private AuditPPSearchBean auditPPSearchBean;
	private String docType;
	private Integer docID;

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

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public AuditPPSearchBean getAuditPPSearchBean() {
		if (auditPPSearchBean == null) {
			auditPPSearchBean = new AuditPPSearchBean();
		}
		return auditPPSearchBean;
	}

	public void setAuditPPSearchBean(AuditPPSearchBean auditPPSearchBean) {
		this.auditPPSearchBean = auditPPSearchBean;
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
