package my.ep.web.integration.eperunding;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/*
 *@author P Ganga prasad
 */
public class ConsultantProfile {

	private String companyName;
	private String SSMNo;
	private String MOFNo;
	private String GSTNo;
	private String virtualCertNo;
	private Date GSTEffectiveDate;
	private Date GSTEndDate;
	private String companyType;
	private String address1;
	private String address2;
	private String address3;
	private String postcode;
	private String city;
	private String district;
	private String state;
	private String country;
	private String telCountryCode;
	private String telAreaCode;
	private String telNo;
	private String faxCountryCode;
	private String faxAreaCode;
	private String faxNo;
	private BigDecimal accumulatedCapital;
	private BigDecimal authorisedCapital;
	private BigDecimal paidUpCapital;
	private BigDecimal accumulatedShare;
	private BigDecimal bumiputeraPercentage;
	private BigDecimal nonBumiPercentage;
	private Long regStatus;
	private Date effectDateTo;
	private Date establishedDate;
	private Date effectDateFrom;
	private String disciplinary;
	private String isAssociatedCompany;
	private Date profileUpdateDate;

	private List<ProfBodyDetail> profBodyDetailList;
	private List<RegistrationHistory> registrationHistoryList;
	private CapitalProportion capitalProportion;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSSMNo() {
		return SSMNo;
	}

	public void setSSMNo(String sSMNo) {
		SSMNo = sSMNo;
	}

	public String getMOFNo() {
		return MOFNo;
	}

	public void setMOFNo(String mOFNo) {
		MOFNo = mOFNo;
	}

	public String getGSTNo() {
		return GSTNo;
	}

	public void setGSTNo(String gSTNo) {
		GSTNo = gSTNo;
	}

	public String getVirtualCertNo() {
		return virtualCertNo;
	}

	public void setVirtualCertNo(String virtualCertNo) {
		this.virtualCertNo = virtualCertNo;
	}

	public Date getGSTEffectiveDate() {
		return GSTEffectiveDate;
	}

	public void setGSTEffectiveDate(Date gSTEffectiveDate) {
		GSTEffectiveDate = gSTEffectiveDate;
	}

	public Date getGSTEndDate() {
		return GSTEndDate;
	}

	public void setGSTEndDate(Date gSTEndDate) {
		GSTEndDate = gSTEndDate;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTelCountryCode() {
		return telCountryCode;
	}

	public void setTelCountryCode(String telCountryCode) {
		this.telCountryCode = telCountryCode;
	}

	public String getTelAreaCode() {
		return telAreaCode;
	}

	public void setTelAreaCode(String telAreaCode) {
		this.telAreaCode = telAreaCode;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public String getFaxCountryCode() {
		return faxCountryCode;
	}

	public void setFaxCountryCode(String faxCountryCode) {
		this.faxCountryCode = faxCountryCode;
	}

	public String getFaxAreaCode() {
		return faxAreaCode;
	}

	public void setFaxAreaCode(String faxAreaCode) {
		this.faxAreaCode = faxAreaCode;
	}

	public String getFaxNo() {
		return faxNo;
	}

	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	public BigDecimal getAccumulatedCapital() {
		return accumulatedCapital;
	}

	public void setAccumulatedCapital(BigDecimal accumulatedCapital) {
		this.accumulatedCapital = accumulatedCapital;
	}

	public BigDecimal getAuthorisedCapital() {
		return authorisedCapital;
	}

	public void setAuthorisedCapital(BigDecimal authorisedCapital) {
		this.authorisedCapital = authorisedCapital;
	}

	public BigDecimal getPaidUpCapital() {
		return paidUpCapital;
	}

	public void setPaidUpCapital(BigDecimal paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	public BigDecimal getAccumulatedShare() {
		return accumulatedShare;
	}

	public void setAccumulatedShare(BigDecimal accumulatedShare) {
		this.accumulatedShare = accumulatedShare;
	}

	public BigDecimal getBumiputeraPercentage() {
		return bumiputeraPercentage;
	}

	public void setBumiputeraPercentage(BigDecimal bumiputeraPercentage) {
		this.bumiputeraPercentage = bumiputeraPercentage;
	}

	public BigDecimal getNonBumiPercentage() {
		return nonBumiPercentage;
	}

	public void setNonBumiPercentage(BigDecimal nonBumiPercentage) {
		this.nonBumiPercentage = nonBumiPercentage;
	}

	public Long getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(Long regStatus) {
		this.regStatus = regStatus;
	}

	public Date getEffectDateTo() {
		return effectDateTo;
	}

	public void setEffectDateTo(Date effectDateTo) {
		this.effectDateTo = effectDateTo;
	}

	public Date getEstablishedDate() {
		return establishedDate;
	}

	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate;
	}

	public Date getEffectDateFrom() {
		return effectDateFrom;
	}

	public void setEffectDateFrom(Date effectDateFrom) {
		this.effectDateFrom = effectDateFrom;
	}

	public String getDisciplinary() {
		return disciplinary;
	}

	public void setDisciplinary(String disciplinary) {
		this.disciplinary = disciplinary;
	}

	public String getIsAssociatedCompany() {
		return isAssociatedCompany;
	}

	public void setIsAssociatedCompany(String isAssociatedCompany) {
		this.isAssociatedCompany = isAssociatedCompany;
	}

	public Date getProfileUpdateDate() {
		return profileUpdateDate;
	}

	public void setProfileUpdateDate(Date profileUpdateDate) {
		this.profileUpdateDate = profileUpdateDate;
	}

	public List<ProfBodyDetail> getProfBodyDetailList() {
		return profBodyDetailList;
	}

	public void setProfBodyDetailList(List<ProfBodyDetail> profBodyDetailList) {
		this.profBodyDetailList = profBodyDetailList;
	}

	public List<RegistrationHistory> getRegistrationHistoryList() {
		return registrationHistoryList;
	}

	public void setRegistrationHistoryList(
			List<RegistrationHistory> registrationHistoryList) {
		this.registrationHistoryList = registrationHistoryList;
	}

	public CapitalProportion getCapitalProportion() {
		return capitalProportion;
	}

	public void setCapitalProportion(CapitalProportion capitalProportion) {
		this.capitalProportion = capitalProportion;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConsultantProfile) {
			ConsultantProfile temp = (ConsultantProfile) obj;
			if (this.MOFNo.equalsIgnoreCase(temp.MOFNo)
					&& this.SSMNo.equalsIgnoreCase(temp.SSMNo))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.MOFNo.hashCode() + this.SSMNo.hashCode());
	}

}
