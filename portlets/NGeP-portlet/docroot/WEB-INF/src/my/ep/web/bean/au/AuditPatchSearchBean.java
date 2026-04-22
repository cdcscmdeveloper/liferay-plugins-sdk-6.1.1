package my.ep.web.bean.au;

import java.io.Serializable;

/**
 * #35740 
 * Search Bean for Audit Search Data Patching
 * 
 */
public class AuditPatchSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer changeTypeID;
	private String changeType;
	private String changeTypeCode;
	private Integer docTypeID;
	private String docType;
	private String docTypeCode;
	private String docNo;
	private String eAduanNo;
	private boolean isSearching = true;
	private String createdBy;
	private String roleCode;
	private Integer changeTypeIDOldVal;
	private Integer docTypeIDOldVal;
	private String docNoOldVal;
	private String eAduanNoOldVal;
	private String pType;


	/**
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleCode the roleCode to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the isSearching
	 */
	public boolean isSearching() {
		return isSearching;
	}

	/**
	 * @param isSearching the isSearching to set
	 */
	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
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
	 * @return the changeType
	 */
	public String getChangeType() {
		return changeType;
	}

	/**
	 * @param changeType the changeType to set
	 */
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	public AuditPatchSearchBean() {
	}

	/**
	 * @return the changeTypeIDOldVal
	 */
	public Integer getChangeTypeIDOldVal() {
		return changeTypeIDOldVal;
	}

	/**
	 * @param changeTypeIDOldVal the changeTypeIDOldVal to set
	 */
	public void setChangeTypeIDOldVal(Integer changeTypeIDOldVal) {
		this.changeTypeIDOldVal = changeTypeIDOldVal;
	}

	/**
	 * @return the docTypeCodeOldVal
	 */
	public Integer getDocTypeIDOldVal() {
		return docTypeIDOldVal;
	}

	/**
	 * @param docTypeIDOldVal the docTypeIDOldVal to set
	 */
	public void setDocTypeIDOldVal(Integer docTypeIDOldVal) {
		this.docTypeIDOldVal = docTypeIDOldVal;
	}

	/**
	 * @return the docNoOldVal
	 */
	public String getDocNoOldVal() {
		return docNoOldVal;
	}

	/**
	 * @param docNoOldVal the docNoOldVal to set
	 */
	public void setDocNoOldVal(String docNoOldVal) {
		this.docNoOldVal = docNoOldVal;
	}

	/**
	 * @return the eAduanNoOldVal
	 */
	public String geteAduanNoOldVal() {
		return eAduanNoOldVal;
	}

	/**
	 * @param eAduanNoOldVal the eAduanNoOldVal to set
	 */
	public void seteAduanNoOldVal(String eAduanNoOldVal) {
		this.eAduanNoOldVal = eAduanNoOldVal;
	}

	/**
	 * @return the changeTypeID
	 */
	public Integer getChangeTypeID() {
		return changeTypeID;
	}

	/**
	 * @param changeTypeID the changeTypeID to set
	 */
	public void setChangeTypeID(Integer changeTypeID) {
		this.changeTypeID = changeTypeID;
	}

	/**
	 * @return the docTypeID
	 */
	public Integer getDocTypeID() {
		return docTypeID;
	}

	/**
	 * @param docTypeID the docTypeID to set
	 */
	public void setDocTypeID(Integer docTypeID) {
		this.docTypeID = docTypeID;
	}

	/**
	 * @return the pType
	 */
	public String getpType() {
		return pType;
	}

	/**
	 * @param pType the pType to set
	 */
	public void setpType(String pType) {
		this.pType = pType;
	}
}
