package my.ep.web.managedbean.cm.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.cm.AuditCMSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * New file introduced for Audit Trail Codification Management (#28895 - #35503)
 * Manage Bean for CM audit report/view
 * 
 * @author irfan
 * @since 01/11/2024
 */
@ManagedBean(name = "auCMMB")
@SessionScoped
public class AuditCodificationManagementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String backURL;
	private String language;
	private boolean isLazy = false;
	private AuditCMSearchBean auditCMSearchBean;
	private int activeIndex = 0;
	private String segmentCode;
	private String familyCode;
	private String classCode;
	private String commodityCode;

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
	
	public AuditCMSearchBean getAuditCMSearchBean() {
		if (auditCMSearchBean == null){
			auditCMSearchBean = new AuditCMSearchBean();			
		}
		return auditCMSearchBean;
	}

	public void setAuditCMSearchBean(AuditCMSearchBean auditCMSearchBean) {
		this.auditCMSearchBean = auditCMSearchBean;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

}
