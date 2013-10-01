package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.Date;

public class CreateContractDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sourceNo;
	private String sourcingType;
	private String contractType;
	private String sourcingCategory;
	private String createdDate;
	private String publishingDate;
	private String loaNo;
	private String loaDate;
	private String contractName;
	private String efectiveDate;
	private String expiryDate;
	private String contractAmount;
	private String fulfilmentType;
	private String serviceFee;
	private String contractPhysicalNo;
	private String liquidatedDamages;
	private String deliveryPeriod;
	private String contractOwner;
	private String contractOwnerJabatan;
	private String contractOwnerPtj;
	private String circularNo;
	private String agreementNo;
	private Date date;
	private String ministryName;
	private String departmentName;
	private String ptjCode;
	private String ptjName;
	private String selingAmount;
	private String currencyType;
	private String ZoneType;
	private Date efdate;
	private Date exdate;
	
	public Date getEfdate() {
		return efdate;
	}
	public void setEfdate(Date efdate) {
		this.efdate = efdate;
	}
	public Date getExdate() {
		return exdate;
	}
	public void setExdate(Date exdate) {
		this.exdate = exdate;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCircularNo() {
		return circularNo;
	}
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}
	public String getAgreementNo() {
		return agreementNo;
	}
	public void setAgreementNo(String agreementNo) {
		this.agreementNo = agreementNo;
	}
	
	
	
	
	public String getSourceNo() {
		return sourceNo;
	}
	public void setSourceNo(String sourceNo) {
		this.sourceNo = sourceNo;
	}
	public String getSourcingType() {
		return sourcingType;
	}
	public void setSourcingType(String sourcingType) {
		this.sourcingType = sourcingType;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getSourcingCategory() {
		return sourcingCategory;
	}
	public void setSourcingCategory(String sourcingCategory) {
		this.sourcingCategory = sourcingCategory;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getPublishingDate() {
		return publishingDate;
	}
	public void setPublishingDate(String publishingDate) {
		this.publishingDate = publishingDate;
	}
	public String getLoaNo() {
		return loaNo;
	}
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}
	public String getLoaDate() {
		return loaDate;
	}
	public void setLoaDate(String loaDate) {
		this.loaDate = loaDate;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getEfectiveDate() {
		return efectiveDate;
	}
	public void setEfectiveDate(String efectiveDate) {
		this.efectiveDate = efectiveDate;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getContractAmount() {
		return contractAmount;
	}
	public void setContractAmount(String contractAmount) {
		this.contractAmount = contractAmount;
	}
	public String getFulfilmentType() {
		return fulfilmentType;
	}
	public void setFulfilmentType(String fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}
	public String getServiceFee() {
		return serviceFee;
	}
	public void setServiceFee(String serviceFee) {
		this.serviceFee = serviceFee;
	}
	public String getContractPhysicalNo() {
		return contractPhysicalNo;
	}
	public void setContractPhysicalNo(String contractPhysicalNo) {
		this.contractPhysicalNo = contractPhysicalNo;
	}
	public String getLiquidatedDamages() {
		return liquidatedDamages;
	}
	public void setLiquidatedDamages(String liquidatedDamages) {
		this.liquidatedDamages = liquidatedDamages;
	}
	public String getDeliveryPeriod() {
		return deliveryPeriod;
	}
	public void setDeliveryPeriod(String deliveryPeriod) {
		this.deliveryPeriod = deliveryPeriod;
	}
	public String getContractOwner() {
		return contractOwner;
	}
	public void setContractOwner(String contractOwner) {
		this.contractOwner = contractOwner;
	}
	
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	
	public String getContractOwnerJabatan() {
		return contractOwnerJabatan;
	}
	public void setContractOwnerJabatan(String contractOwnerJabatan) {
		this.contractOwnerJabatan = contractOwnerJabatan;
	}
	public String getContractOwnerPtj() {
		return contractOwnerPtj;
	}
	public void setContractOwnerPtj(String contractOwnerPtj) {
		this.contractOwnerPtj = contractOwnerPtj;
	}
	
	
	public String getSelingAmount() {
		return selingAmount;
	}
	public void setSelingAmount(String selingAmount) {
		this.selingAmount = selingAmount;
	}
	public String getPtjCode() {
		return ptjCode;
	}
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}
	public String getPtjName() {
		return ptjName;
	}
	public void setPtjName(String ptjName) {
		this.ptjName = ptjName;
	}
	

	
	public String getZoneType() {
		
		return ZoneType;
	}
	public void setZoneType(String zoneType) {
		ZoneType = zoneType;
	}
	
	
	
	
	public String getMinistryName() {
		return ministryName;
	}
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	
	public CreateContractDetails(){
		
	}
	
	public CreateContractDetails(String ministryName){
		this.ministryName=ministryName;
	}
	
	public CreateContractDetails(String ptjCode,String ptjName,String selingAmount, Date efdate, Date exdate ){
		this.ptjCode=ptjCode;
		this.ptjName=ptjName;
		this.selingAmount = selingAmount;
		this.efdate = efdate;
		this.exdate = exdate;
	}
}
