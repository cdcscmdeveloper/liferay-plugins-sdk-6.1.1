package my.ep.web.managedbean.au;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.au.AuditPatchSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * #35740
 * Managed Bean for Audit Patch
 * 
 */
/**
 * @author shaqimi
 *
 */
@ManagedBean(name = "auditPatchManagedBean")
@SessionScoped
public class AuditPatchManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private AuditPatchSearchBean searchBean;
	
	private String changeTypeCode;
	
	private String docTypeCode;
	
	private String docNo;
	
	private String eAduanNo;

	private boolean isLazy = false;
	
	private boolean hideAduan = false;

	/**
	 * @return the selectedType
	 */
	public String getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType the selectedType to set
	 */
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	private String selectedType;

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * Constructor
	 */
	public AuditPatchManagedBean() {
	}

	/**
	 * @return the searchBean
	 */
	public AuditPatchSearchBean getSearchBean() {
		if (searchBean == null)
			searchBean = new AuditPatchSearchBean();
		return searchBean;
	}

	/**
	 * @param searchBean the searchBean to set
	 */
	public void setSearchBean(AuditPatchSearchBean searchBean) {
		this.searchBean = searchBean;
	}

	/**
	 * @return the changeTypeCode
	 */
	public String getChangeTypeCode() {
		return changeTypeCode;
	}

	/**
	 * @param changeTypeCode the changeTypeCode to set
	 */
	public void setChangeTypeCode(String changeTypeCode) {
		this.changeTypeCode = changeTypeCode;
	}

	/**
	 * @return the docTypeCode
	 */
	public String getDocTypeCode() {
		return docTypeCode;
	}

	/**
	 * @param docTypeCode the docTypeCode to set
	 */
	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the eAduanNo
	 */
	public String geteAduanNo() {
		return eAduanNo;
	}

	/**
	 * @param eAduanNo the eAduanNo to set
	 */
	public void seteAduanNo(String eAduanNo) {
		this.eAduanNo = eAduanNo;
	}

	/**
	 * @return the hideAduan
	 */
	public boolean isHideAduan() {
		return hideAduan;
	}

	/**
	 * @param hideAduan the hideAduan to set
	 */
	public void setHideAduan(boolean hideAduan) {
		this.hideAduan = hideAduan;
	}
	
}
