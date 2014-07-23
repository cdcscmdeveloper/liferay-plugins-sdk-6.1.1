package my.ep.web.managedbean.sm.registration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.UploadedFile;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "registrationMB")
@SessionScoped
public class RegistrationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmAppl smAppl;
	private SmSupplier smSupp;

	private List<PmCountry> countryLs;
	private List<PmState> businessStateLs;
	private List<PmState> residentialStateLs;
	private List<PmState> permenantStateLs;

	private List<PmDistrict> businessDistrictLs;
	private List<PmDistrict> residentialDistrictLs;
	private List<PmDistrict> permenantDistrictLs;

	private List<PmCity> businessCityLs;
	private List<PmCity> residentialCityLs;
	private List<PmCity> permenantCityLs;

	private boolean sameAddress;
	private SmSupplierBank[] selectedSuppBanks;
//	private List<SmSupplierBank> smSuppBanks;

	private SmPersonnel smPersonnel;
	private SmSupplierOwner smSuppOwner;
	private SmPersonnelAddress smPersonnelAdd;

	private SmAddress smBusinessAdd;
	private SmAddress smPermenantAdd;
	private SmAddress smResAdd;

	private String applType;
	private String suppOwnerIdMask;
	private String personnelIdMask;
	private List<PmParameterDesc> businessTypeLs;
	private List<PmFinancialOrg> financialOrgLs;
	private List<PmCertBody> profCertBodyLs;

	private SmSupplierCert[] selectedSuppCerts;
//	private List<SmSupplierCert> smSuppCerts;
//	private Map<String, String> serviceLs;
	private List<PmParameterDesc> serviceLs;

	private List<PmBusinessNature> businessNatureL1;
	private List<PmBusinessNature> businessNatureL2;
	private List<PmBusinessNature> businessNatureL3;
	private List<PmBusinessNature> businessNatureL4;
	private List<PmBusinessNature> businessNatureL5;
	//private List<SmSupplierBusiness> suppBusinessNatureLs;
	private SmSupplierBusiness[] selectedBusinessNatureLs;
	private SmSupplierBusiness selectedBusinessNature;
	private SmSupplierBusiness suppBusinessNature;
	
	private Long bizIdL1;
	private Long bizIdL2;
	private Long bizIdL3;
	private Long bizIdL4;
	
	private boolean agreeTC;
	
	private String confirmEmail;
	
	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public List<PmCountry> getCountryLs() {
		return countryLs;
	}

	public void setCountryLs(List<PmCountry> countryLs) {
		this.countryLs = countryLs;
	}

	public List<PmState> getBusinessStateLs() {
		return businessStateLs;
	}

	public void setBusinessStateLs(List<PmState> businessStateLs) {
		this.businessStateLs = businessStateLs;
	}

	public List<PmState> getResidentialStateLs() {
		return residentialStateLs;
	}

	public void setResidentialStateLs(List<PmState> residentialStateLs) {
		this.residentialStateLs = residentialStateLs;
	}

	public List<PmState> getPermenantStateLs() {
		return permenantStateLs;
	}

	public List<PmDistrict> getBusinessDistrictLs() {
		return businessDistrictLs;
	}

	public void setBusinessDistrictLs(List<PmDistrict> businessDistrictLs) {
		this.businessDistrictLs = businessDistrictLs;
	}

	public List<PmDistrict> getResidentialDistrictLs() {
		return residentialDistrictLs;
	}

	public void setResidentialDistrictLs(List<PmDistrict> residentialDistrictLs) {
		this.residentialDistrictLs = residentialDistrictLs;
	}

	public List<PmDistrict> getPermenantDistrictLs() {
		return permenantDistrictLs;
	}

	public void setPermenantDistrictLs(List<PmDistrict> permenantDistrictLs) {
		this.permenantDistrictLs = permenantDistrictLs;
	}

	public void setPermenantStateLs(List<PmState> permenantStateLs) {
		this.permenantStateLs = permenantStateLs;
	}

	public List<PmCity> getBusinessCityLs() {
		return businessCityLs;
	}

	public void setBusinessCityLs(List<PmCity> businessCityLs) {
		this.businessCityLs = businessCityLs;
	}

	public List<PmCity> getResidentialCityLs() {
		return residentialCityLs;
	}

	public void setResidentialCityLs(List<PmCity> residentialCityLs) {
		this.residentialCityLs = residentialCityLs;
	}

	public List<PmCity> getPermenantCityLs() {
		return permenantCityLs;
	}

	public void setPermenantCityLs(List<PmCity> permenantCityLs) {
		this.permenantCityLs = permenantCityLs;
	}

	public SmPersonnel getSmPersonnel() {
		if(smPersonnel == null)
			smPersonnel = new SmPersonnel();
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	public SmSupplierOwner getSmSuppOwner() {
		if(smSuppOwner == null) {
			smSuppOwner = new SmSupplierOwner();
		}
		return smSuppOwner;
	}

	public void setSmSuppOwner(SmSupplierOwner smSuppOwner) {
		this.smSuppOwner = smSuppOwner;
	}

//	public List<SmSupplierCert> getSmSuppCerts() {
//		return smSuppCerts;
//	}
//
//	public void setSmSuppCerts(List<SmSupplierCert> smSuppCerts) {
//		this.smSuppCerts = smSuppCerts;
//	}

	public SmPersonnelAddress getSmPersonnelAdd() {
		return smPersonnelAdd;
	}

	public void setSmPersonnelAdd(SmPersonnelAddress smPersonnelAdd) {
		this.smPersonnelAdd = smPersonnelAdd;
	}

	public SmAddress getSmBusinessAdd() {
		return smBusinessAdd;
	}

	public void setSmBusinessAdd(SmAddress smBusinessAdd) {
		this.smBusinessAdd = smBusinessAdd;
	}

	public SmAddress getSmPermenantAdd() {
		return smPermenantAdd;
	}

	public void setSmPermenantAdd(SmAddress smPermenantAdd) {
		this.smPermenantAdd = smPermenantAdd;
	}

	public SmAddress getSmResAdd() {
		return smResAdd;
	}

	public void setSmResAdd(SmAddress smResAdd) {
		this.smResAdd = smResAdd;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

//	public List<SmSupplierBank> getSmSuppBanks() {
//		return smSuppBanks;
//	}
//
//	public void setSmSuppBanks(List<SmSupplierBank> smSuppBanks) {
//		this.smSuppBanks = smSuppBanks;
//	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	}

	public boolean isSameAddress() {
		return sameAddress;
	}

	public void setSameAddress(boolean sameAddress) {
		this.sameAddress = sameAddress;
	}

	public SmSupplierBank[] getSelectedSuppBanks() {
		return selectedSuppBanks;
	}

	public void setSelectedSuppBanks(SmSupplierBank[] selectedSuppBanks) {
		this.selectedSuppBanks = selectedSuppBanks;
	}

	public String getSuppOwnerIdMask() {
		return suppOwnerIdMask;
	}

	public void setSuppOwnerIdMask(String suppOwnerIdMask) {
		this.suppOwnerIdMask = suppOwnerIdMask;
	}

	public String getPersonnelIdMask() {
		return personnelIdMask;
	}

	public void setPersonnelIdMask(String personnelIdMask) {
		this.personnelIdMask = personnelIdMask;
	}

	public List<PmParameterDesc> getBusinessTypeLs() {
		return businessTypeLs;
	}

	public void setBusinessTypeLs(List<PmParameterDesc> businessTypeLs) {
		this.businessTypeLs = businessTypeLs;
	}

	public List<PmFinancialOrg> getFinancialOrgLs() {
		return financialOrgLs;
	}

	public void setFinancialOrgLs(List<PmFinancialOrg> financialOrgLs) {
		this.financialOrgLs = financialOrgLs;
	}

	public SmSupplierCert[] getSelectedSuppCerts() {
		return selectedSuppCerts;
	}

	public void setSelectedSuppCerts(SmSupplierCert[] selectedSuppCerts) {
		this.selectedSuppCerts = selectedSuppCerts;
	}

	public List<PmCertBody> getProfCertBodyLs() {
		return profCertBodyLs;
	}

	public void setProfCertBodyLs(List<PmCertBody> profCertBodyLs) {
		this.profCertBodyLs = profCertBodyLs;
	}

//	public Map<String, String> getServiceLs() {
//		serviceLs = new LinkedHashMap<String, String>();
//
//		if (this.smSupp.getServiceCategory() != null){
//			if (this.smSupp.getServiceCategory().equals("P")) {
//				serviceLs.put("1", "Accountant");
//				serviceLs.put("2", "Doctor");
//				serviceLs.put("3", "Lawyer");
//			} else if (this.smSupp.getServiceCategory().equals("S")) {
//				serviceLs.put("4", "Bomoh hujan");
//				serviceLs.put("5", "Penggali kubur");
//				serviceLs.put("6", "Bidan");
//				serviceLs.put("7", "Penyewaan sampan");
//			}
//		}
//
//		return this.serviceLs;
//	}
//
//	public void setServiceLs(Map<String, String> serviceLs) {
//		this.serviceLs = serviceLs;
//	}

	public List<PmParameterDesc> getServiceLs() {
		return serviceLs;
	}

	public void setServiceLs(List<PmParameterDesc> serviceLs) {
		this.serviceLs = serviceLs;
	}

	public List<PmBusinessNature> getBusinessNatureL1() {
		return businessNatureL1;
	}

	public void setBusinessNatureL1(List<PmBusinessNature> businessNatureL1) {
		this.businessNatureL1 = businessNatureL1;
	}

	public List<PmBusinessNature> getBusinessNatureL2() {
		return businessNatureL2;
	}

	public void setBusinessNatureL2(List<PmBusinessNature> businessNatureL2) {
		this.businessNatureL2 = businessNatureL2;
	}

	public List<PmBusinessNature> getBusinessNatureL3() {
		return businessNatureL3;
	}

	public void setBusinessNatureL3(List<PmBusinessNature> businessNatureL3) {
		this.businessNatureL3 = businessNatureL3;
	}

	public List<PmBusinessNature> getBusinessNatureL4() {
		return businessNatureL4;
	}

	public void setBusinessNatureL4(List<PmBusinessNature> businessNatureL4) {
		this.businessNatureL4 = businessNatureL4;
	}

	public List<PmBusinessNature> getBusinessNatureL5() {
		return businessNatureL5;
	}

	public void setBusinessNatureL5(List<PmBusinessNature> businessNatureL5) {
		this.businessNatureL5 = businessNatureL5;
	}

	public SmSupplierBusiness getSuppBusinessNature() {
		return suppBusinessNature;
	}

	public void setSuppBusinessNature(SmSupplierBusiness suppBusinessNature) {
		this.suppBusinessNature = suppBusinessNature;
	}
	
//	public List<SmSupplierBusiness> getSuppBusinessNatureLs() {
//		return suppBusinessNatureLs;
//	}
//
//	public void setSuppBusinessNatureLs(
//			List<SmSupplierBusiness> suppBusinessNatureLs) {
//		this.suppBusinessNatureLs = suppBusinessNatureLs;
//	}


	public SmSupplierBusiness[] getSelectedBusinessNatureLs() {
		return selectedBusinessNatureLs;
	}

	public void setSelectedBusinessNatureLs(
			SmSupplierBusiness[] selectedBusinessNatureLs) {
		this.selectedBusinessNatureLs = selectedBusinessNatureLs;
	}

	public SmSupplierBusiness getSelectedBusinessNature() {
		return selectedBusinessNature;
	}

	public void setSelectedBusinessNature(SmSupplierBusiness selectedBusinessNature) {
		this.selectedBusinessNature = selectedBusinessNature;
	}

	public Long getBizIdL1() {
		return bizIdL1;
	}

	public void setBizIdL1(Long bizIdL1) {
		this.bizIdL1 = bizIdL1;
	}

	public Long getBizIdL2() {
		return bizIdL2;
	}

	public void setBizIdL2(Long bizIdL2) {
		this.bizIdL2 = bizIdL2;
	}

	public Long getBizIdL3() {
		return bizIdL3;
	}

	public void setBizIdL3(Long bizIdL3) {
		this.bizIdL3 = bizIdL3;
	}

	public Long getBizIdL4() {
		return bizIdL4;
	}

	public void setBizIdL4(Long bizIdL4) {
		this.bizIdL4 = bizIdL4;
	}

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}

}
