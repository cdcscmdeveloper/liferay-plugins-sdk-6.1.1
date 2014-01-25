package my.ep.web.managedbean.sm.registration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
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
	private List<SmSupplierBank> smSuppBanks;
	
	private SmPersonnel smPersonnel;
	private SmSupplierOwner smSuppOwner;
	private SmPersonnelAddress smPersonnelAdd;
	
	private List<SmSupplierCert> smSuppCerts;

	private SmAddress smBusinessAdd;
	private SmAddress smPermenantAdd;
	private SmAddress smResAdd;
	
	private String applType;
	private String suppOwnerIdMask;
	
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
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	public SmSupplierOwner getSmSuppOwner() {
		return smSuppOwner;
	}

	public void setSmSuppOwner(SmSupplierOwner smSuppOwner) {
		this.smSuppOwner = smSuppOwner;
	}

	public List<SmSupplierCert> getSmSuppCerts() {
		return smSuppCerts;
	}

	public void setSmSuppCerts(List<SmSupplierCert> smSuppCerts) {
		this.smSuppCerts = smSuppCerts;
	}

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
	
	public List<SmSupplierBank> getSmSuppBanks() {
		return smSuppBanks;
	}

	public void setSmSuppBanks(List<SmSupplierBank> smSuppBanks) {
		this.smSuppBanks = smSuppBanks;
	}

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

}
