package my.ep.web.integration.crm;

import java.util.List;

public class SupplierUserInfo {

	private String userName;
	private String identityNo;
	private String identityType;
	private String nationality;
	private String recordStatus;
	private String email;
	private String mobileNo;
	private String position;
	private String accessStatus;

	private List<BelongsTo> belongsToList;
	private List<SoftCert> softCertList;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIdentityNo() {
		return identityNo;
	}

	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getAccessStatus() {
		return accessStatus;
	}

	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}

	public List<BelongsTo> getBelongsToList() {
		return belongsToList;
	}

	public void setBelongsToList(List<BelongsTo> belongsToList) {
		this.belongsToList = belongsToList;
	}

	public List<SoftCert> getSoftCertList() {
		return softCertList;
	}

	public void setSoftCertList(List<SoftCert> softCertList) {
		this.softCertList = softCertList;
	}

}
