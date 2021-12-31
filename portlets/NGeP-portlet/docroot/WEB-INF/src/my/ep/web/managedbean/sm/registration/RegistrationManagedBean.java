package my.ep.web.managedbean.sm.registration;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.BlacklistOwnerDirectorVO;

@ManagedBean(name = "registrationMB")
@SessionScoped
public class RegistrationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private SmAppl smAppl;
	private SmSupplier smSupp;

	private List<SelectItem> countryLs;
	private List<SelectItem> businessStateLs;
	private List<SelectItem> residentialStateLs;
	private List<SelectItem> permenantStateLs;

	private List<SelectItem> businessDistrictLs;
	private List<SelectItem> residentialDistrictLs;
	private List<SelectItem> permenantDistrictLs;

	
	private List<SelectItem> businessCityLs;
	private List<SelectItem> residentialCityLs;
	private List<SelectItem> permenantCityLs;
	
	
	//13320-Start
	private List<SelectItem> businessDivisionLs;
	private List<SelectItem> residentialDivisionLs;
	private List<SelectItem> permenantDivisionLs;
	//13320-End

	private List<SelectItem> salutationItems;
	
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

	private List<PmParameterDesc> serviceLs;

	private List<PmBusinessNature> businessNatureL1;
	private List<PmBusinessNature> businessNatureL2;
	private List<PmBusinessNature> businessNatureL3;
	private List<PmBusinessNature> businessNatureL4;
	private List<PmBusinessNature> businessNatureL5;
	private List<PmBusinessNature> bizNatures;

	private SmSupplierBusiness[] selectedBusinessNatureLs;
	private SmSupplierBusiness selectedBusinessNature;
	
	private Long bizIdL1;
	private Long bizIdL2;
	private Long bizIdL3;
	private Long bizIdL4;
	
	private boolean agreeTC;
	
	private String confirmEmail;
	
	private boolean sabahSarawakType;
	
	private SmAssociationBody[] selectedAssociationBodies;
	
	private Long myCountryId;

	private Integer userBumiFlg;
	private Integer userMuslimFlg;
	
	private boolean sameAsOwner;
	private boolean disabledCompanyNameInMaintenance;
	private boolean disabledBusinessTypeInMaintenance;
	private boolean regAccountMaintainance = false;
	
	private boolean gstFlag;
	//harris
	private boolean sstFlag;

	private SmGstSupplier smGstSupplier;
	//harris
	private SmSstSupplier smSstSupplier;
	
	private List<PmDocument> gstDocLs;
	private SmGstSupplierDoc selectedGstSupplierDoc;
	
	private boolean myOwner;
	private boolean aggreeDeclareAdmin;
	
	private boolean hasActiveTransaction;
	private boolean viewMode;
	private boolean deletedNotActivatedSupplier;
	//#16052019 - SSM INTEGRATION CR - Fahmi
	private List<SmSupplierOwner> smSuppOwners = new ArrayList<SmSupplierOwner>();
	private List<SmPersonnel> smPersonnelDirector = new ArrayList<SmPersonnel>();
	private List dummy = new ArrayList();
	private List<SmPersonnel> smPersonnelOwners = new ArrayList<SmPersonnel>();
	private SmPersonnel smPersonnelSelected= new SmPersonnel();
	private List<SelectItem> selectItemsSmPersonnelOwners = new ArrayList<SelectItem>();
	private List<SmPersonnel> SmPersonnelAll = new ArrayList<SmPersonnel>();
	private List<SmEquityCompany> smEquityCompany = new ArrayList<SmEquityCompany>();
	 private List<SmSupplierBranch> smSupplierBranches = new ArrayList<SmSupplierBranch>();
	private String headerName = "";
	private boolean ownerIsEquity=false;
	private boolean ownerIsDirector=false;
	private boolean showOwner=false;
	private boolean showDirector=false;
	private boolean showDirectorInOwner=false;
	private boolean disableRole=false;
	private boolean disableEquity=false;
	private boolean disableStaff=false;
	private boolean hideButton=false;
	private String labelDataTable;
	//26238 - Disciplinary Action
	private boolean warning;
	private boolean suspendRegistration;
	private boolean cancelRegistration;
	private boolean blacklistowner;
	//30842 Start
	private boolean cancelCategory;
	//30842 End
	private String startDate;
	private String endDate;
	private String remarks;
	private List<SmPersonnel> blacklistOwner;
	private List<BlacklistOwnerDirectorVO> blacklistownerDirectorList;
	private boolean noDisciplinary;
	private String regIcCheckSSM;
	private boolean activeDisicplinaryAction;
	private boolean divisionBox=true;//13320
	private Long divisionId;//13320
	private List <SmSupplierOwner> supplierOwnerls = new ArrayList<SmSupplierOwner>(); // #27691
	//#30829 Start
	private boolean disableManagement=false;
	private boolean disableProf=false;
	private boolean disableSemiProf=false;
	//#30829 end
	
	// 27691 - Add/Delete Supplier Owner
	private SmSupplierOwner[] selectedSuppOwners;
	
	private boolean showOwnerPersonnel = false;
	
	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public List<SelectItem> getCountryLs() {
		return countryLs;
	}

	public void setCountryLs(List<SelectItem> countryLs) {
		this.countryLs = countryLs;
	}

	public List<SelectItem> getBusinessStateLs() {
		return businessStateLs;
	}

	public void setBusinessStateLs(List<SelectItem> businessStateLs) {
		this.businessStateLs = businessStateLs;
	}

	public List<SelectItem> getResidentialStateLs() {
		return residentialStateLs;
	}

	public void setResidentialStateLs(List<SelectItem> residentialStateLs) {
		this.residentialStateLs = residentialStateLs;
	}

	public List<SelectItem> getPermenantStateLs() {
		return permenantStateLs;
	}

	public List<SelectItem> getBusinessDistrictLs() {
		return businessDistrictLs;
	}

	public void setBusinessDistrictLs(List<SelectItem> businessDistrictLs) {
		this.businessDistrictLs = businessDistrictLs;
	}

	public List<SelectItem> getResidentialDistrictLs() {
		return residentialDistrictLs;
	}

	public void setResidentialDistrictLs(List<SelectItem> residentialDistrictLs) {
		this.residentialDistrictLs = residentialDistrictLs;
	}

	public List<SelectItem> getPermenantDistrictLs() {
		return permenantDistrictLs;
	}

	public void setPermenantDistrictLs(List<SelectItem> permenantDistrictLs) {
		this.permenantDistrictLs = permenantDistrictLs;
	}

	public void setPermenantStateLs(List<SelectItem> permenantStateLs) {
		this.permenantStateLs = permenantStateLs;
	}

	public List<SelectItem> getBusinessCityLs() {
		return businessCityLs;
	}

	public void setBusinessCityLs(List<SelectItem> businessCityLs) {
		this.businessCityLs = businessCityLs;
	}

	public List<SelectItem> getResidentialCityLs() {
		return residentialCityLs;
	}

	public void setResidentialCityLs(List<SelectItem> residentialCityLs) {
		this.residentialCityLs = residentialCityLs;
	}

	public List<SelectItem> getPermenantCityLs() {
		return permenantCityLs;
	}

	public void setPermenantCityLs(List<SelectItem> permenantCityLs) {
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

	public boolean isSabahSarawakType() {
		return sabahSarawakType;
	}

	public void setSabahSarawakType(boolean sabahSarawakType) {
		this.sabahSarawakType = sabahSarawakType;
	}

	public SmAssociationBody[] getSelectedAssociationBodies() {
		return selectedAssociationBodies;
	}

	public void setSelectedAssociationBodies(SmAssociationBody[] selectedAssociationBodies) {
		this.selectedAssociationBodies = selectedAssociationBodies;
	}

	public Long getMyCountryId() {
		return myCountryId;
	}

	public void setMyCountryId(Long myCountryId) {
		this.myCountryId = myCountryId;
	}

	public Integer getUserMuslimFlg() {
		return userMuslimFlg;
	}

	public void setUserMuslimFlg(Integer userMuslimFlg) {
		this.userMuslimFlg = userMuslimFlg;
	}

	public Integer getUserBumiFlg() {
		return userBumiFlg;
	}

	public void setUserBumiFlg(Integer userBumiFlg) {
		this.userBumiFlg = userBumiFlg;
	}

	public List<PmBusinessNature> getBizNatures() {
		return bizNatures;
	}

	public void setBizNatures(List<PmBusinessNature> bizNatures) {
		this.bizNatures = bizNatures;
	}

	public boolean isSameAsOwner() {
		return sameAsOwner;
	}

	public void setSameAsOwner(boolean sameAsOwner) {
		this.sameAsOwner = sameAsOwner;
	}

	public boolean isDisabledCompanyNameInMaintenance() {
		return disabledCompanyNameInMaintenance;
	}

	public void setDisabledCompanyNameInMaintenance(
			boolean disabledCompanyNameInMaintenance) {
		this.disabledCompanyNameInMaintenance = disabledCompanyNameInMaintenance;
	}

	public boolean isDisabledBusinessTypeInMaintenance() {
		return disabledBusinessTypeInMaintenance;
	}

	public void setDisabledBusinessTypeInMaintenance(
			boolean disabledBusinessTypeInMaintenance) {
		this.disabledBusinessTypeInMaintenance = disabledBusinessTypeInMaintenance;
	}

	public boolean isGstFlag() {
		return gstFlag;
	}

	public void setGstFlag(boolean gstFlag) {
		this.gstFlag = gstFlag;
	}

	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	public List<PmDocument> getGstDocLs() {
		return gstDocLs;
	}

	public void setGstDocLs(List<PmDocument> gstDocLs) {
		this.gstDocLs = gstDocLs;
	}

	public SmGstSupplierDoc getSelectedGstSupplierDoc() {
		return selectedGstSupplierDoc;
	}

	public void setSelectedGstSupplierDoc(SmGstSupplierDoc selectedGstSupplierDoc) {
		this.selectedGstSupplierDoc = selectedGstSupplierDoc;
	}


	public List<SelectItem> getSalutationItems() {
		if(salutationItems == null || salutationItems.size() > 0) {
			salutationItems = SmCommonUtil.getSalutationItems();
		}
		return salutationItems;
	}

	public void setSalutationItems(List<SelectItem> salutationItems) {
		this.salutationItems = salutationItems;
	}

	public boolean isMyOwner() {
		return myOwner;
	}

	public void setMyOwner(boolean myOwner) {
		this.myOwner = myOwner;
	}

	public boolean isAggreeDeclareAdmin() {
		return aggreeDeclareAdmin;
	}

	public void setAggreeDeclareAdmin(boolean aggreeDeclareAdmin) {
		this.aggreeDeclareAdmin = aggreeDeclareAdmin;
	}

	public boolean isHasActiveTransaction() {
		return hasActiveTransaction;
	}

	public void setHasActiveTransaction(boolean hasActiveTransaction) {
		this.hasActiveTransaction = hasActiveTransaction;
	}

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isDeletedNotActivatedSupplier() {
		return deletedNotActivatedSupplier;
	}

	public void setDeletedNotActivatedSupplier(boolean deletedNotActivatedSupplier) {
		this.deletedNotActivatedSupplier = deletedNotActivatedSupplier;
	}

	public boolean isSstFlag() {
		return sstFlag;
	}

	public void setSstFlag(boolean sstFlag) {
		this.sstFlag = sstFlag;
	}

	public SmSstSupplier getSmSstSupplier() {
		return smSstSupplier;
	}

	public void setSmSstSupplier(SmSstSupplier smSstSupplier) {
		this.smSstSupplier = smSstSupplier;
	}

	public List<SmSupplierOwner> getSmSuppOwners() {
		return smSuppOwners;
	}

	public void setSmSuppOwners(List<SmSupplierOwner> smSuppOwners) {
		this.smSuppOwners = smSuppOwners;
	}

	// 27691 - start -  Add/Delete Supplier Owner
	public SmSupplierOwner[] getSelectedSuppOwners() {
		return selectedSuppOwners;
	}

	public void setSelectedSuppOwners(SmSupplierOwner[] selectedSuppOwners) {
		this.selectedSuppOwners = selectedSuppOwners;
	}
	// 27691 - end -  Add/Delete Supplier Owner
	
	public List getDummy() {
		return dummy;
	}

	public void setDummy(List dummy) {
		this.dummy = dummy;
	}

	public List<SmPersonnel> getSmPersonnelOwners() {
		return smPersonnelOwners;
	}

	public void setSmPersonnelOwners(List<SmPersonnel> smPersonnelOwners) {
		this.smPersonnelOwners = smPersonnelOwners;
	}

	public List<SelectItem> getSelectItemsSmPersonnelOwners() {
		return selectItemsSmPersonnelOwners;
	}

	public void setSelectItemsSmPersonnelOwners(List<SelectItem> selectItemsSmPersonnelOwners) {
		this.selectItemsSmPersonnelOwners = selectItemsSmPersonnelOwners;
	}

	public SmPersonnel getSmPersonnelSelected() {
		return smPersonnelSelected;
	}

	public void setSmPersonnelSelected(SmPersonnel smPersonnelSelected) {
		this.smPersonnelSelected = smPersonnelSelected;
	}

	public List<SmPersonnel> getSmPersonnelDirector() {
		return smPersonnelDirector;
	}

	public void setSmPersonnelDirector(List<SmPersonnel> smPersonnelDirector) {
		this.smPersonnelDirector = smPersonnelDirector;
	}

	public List<SmPersonnel> getSmPersonnelAll() {
		return SmPersonnelAll;
	}

	public void setSmPersonnelAll(List<SmPersonnel> smPersonnelAll) {
		SmPersonnelAll = smPersonnelAll;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public List<SmEquityCompany> getSmEquityCompany() {
		return smEquityCompany;
	}

	public void setSmEquityCompany(List<SmEquityCompany> smEquityCompany) {
		this.smEquityCompany = smEquityCompany;
	}

	public boolean isOwnerIsEquity() {
		return ownerIsEquity;
	}

	public void setOwnerIsEquity(boolean ownerIsEquity) {
		this.ownerIsEquity = ownerIsEquity;
	}

	public boolean isOwnerIsDirector() {
		return ownerIsDirector;
	}

	public void setOwnerIsDirector(boolean ownerIsDirector) {
		this.ownerIsDirector = ownerIsDirector;
	}

	public boolean isShowOwner() {
		return showOwner;
	}

	public void setShowOwner(boolean showOwner) {
		this.showOwner = showOwner;
	}

	public boolean isShowDirector() {
		return showDirector;
	}

	public void setShowDirector(boolean showDirector) {
		this.showDirector = showDirector;
	}

	public boolean isDisableRole() {
		return disableRole;
	}

	public void setDisableRole(boolean disableRole) {
		this.disableRole = disableRole;
	}

	public boolean isShowDirectorInOwner() {
		return showDirectorInOwner;
	}

	public void setShowDirectorInOwner(boolean showDirectorInOwner) {
		this.showDirectorInOwner = showDirectorInOwner;
	}

	public List<SmSupplierBranch> getSmSupplierBranches() {
		return smSupplierBranches;
	}

	public void setSmSupplierBranches(List<SmSupplierBranch> smSupplierBranches) {
		this.smSupplierBranches = smSupplierBranches;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isSuspendRegistration() {
		return suspendRegistration;
	}

	public void setSuspendRegistration(boolean suspendRegistration) {
		this.suspendRegistration = suspendRegistration;
	}

	public boolean isCancelRegistration() {
		return cancelRegistration;
	}

	public void setCancelRegistration(boolean cancelRegistration) {
		this.cancelRegistration = cancelRegistration;
	}

	public boolean isBlacklistowner() {
		return blacklistowner;
	}

	public void setBlacklistowner(boolean blacklistowner) {
		this.blacklistowner = blacklistowner;
	}

	//30842 Start
	public boolean isCancelCategory() {
		return cancelCategory;
	}

	public void setCancelCategory(boolean cancelCategory) {
		this.cancelCategory = cancelCategory;
	}
	//30842 End
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<SmPersonnel> getBlacklistOwner() {
		return blacklistOwner;
	}

	public void setBlacklistOwner(List<SmPersonnel> blacklistOwner) {
		this.blacklistOwner = blacklistOwner;
	}

	public List<BlacklistOwnerDirectorVO> getBlacklistownerDirectorList() {
		return blacklistownerDirectorList;
	}

	public void setBlacklistownerDirectorList(List<BlacklistOwnerDirectorVO> blacklistownerDirectorList) {
		this.blacklistownerDirectorList = blacklistownerDirectorList;
	}

	public boolean isNoDisciplinary() {
		return noDisciplinary;
	}

	public void setNoDisciplinary(boolean noDisciplinary) {
		this.noDisciplinary = noDisciplinary;
	}

	public boolean isShowOwnerPersonnel() {
		return showOwnerPersonnel;
	}

	public void setShowOwnerPersonnel(boolean showOwnerPersonnel) {
		this.showOwnerPersonnel = showOwnerPersonnel;
	}

	public boolean isDisableEquity() {
		return disableEquity;
	}

	public void setDisableEquity(boolean disableEquity) {
		this.disableEquity = disableEquity;
	}

	public boolean isDisableStaff() {
		return disableStaff;
	}

	public void setDisableStaff(boolean disableStaff) {
		this.disableStaff = disableStaff;
	}

	public boolean isHideButton() {
		return hideButton;
	}

	public void setHideButton(boolean hideButton) {
		this.hideButton = hideButton;
	}

	public String getLabelDataTable() {
		labelDataTable = new FacesScopes().getUserLocale().toString();
		return labelDataTable;
	}

	public void setLabelDataTable(String labelDataTable) {
		this.labelDataTable = labelDataTable;
	}

	public String getRegIcCheckSSM() {
		return regIcCheckSSM;
	}

	public void setRegIcCheckSSM(String regIcCheckSSM) {
		this.regIcCheckSSM = regIcCheckSSM;
	}

	public boolean isRegAccountMaintainance() {
		return regAccountMaintainance;
	}

	public void setRegAccountMaintainance(boolean regAccountMaintainance) {
		this.regAccountMaintainance = regAccountMaintainance;
	}

	public boolean isActiveDisicplinaryAction() {
		return activeDisicplinaryAction;
	}

	public void setActiveDisicplinaryAction(boolean activeDisicplinaryAction) {
		this.activeDisicplinaryAction = activeDisicplinaryAction;
	}

	public boolean isDivisionBox() {
		return divisionBox;
	}

	public void setDivisionBox(boolean divisionBox) {
		this.divisionBox = divisionBox;
	}

	public List<SelectItem> getBusinessDivisionLs() {
		return businessDivisionLs;
	}

	public void setBusinessDivisionLs(List<SelectItem> businessDivisionLs) {
		this.businessDivisionLs = businessDivisionLs;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	public List<SelectItem> getResidentialDivisionLs() {
		return residentialDivisionLs;
	}

	public void setResidentialDivisionLs(List<SelectItem> residentialDivisionLs) {
		this.residentialDivisionLs = residentialDivisionLs;
	}

	public List<SelectItem> getPermenantDivisionLs() {
		return permenantDivisionLs;
	}

	public void setPermenantDivisionLs(List<SelectItem> permenantDivisionLs) {
		this.permenantDivisionLs = permenantDivisionLs;
	}
	
	// #27691 - Start
	public List<SmSupplierOwner> getSupplierOwnerls() {
		return supplierOwnerls;
	}

	public void setSupplierOwnerls(List<SmSupplierOwner> supplierOwnerls) {
		this.supplierOwnerls = supplierOwnerls;
	}
	// #27691 - End

	//#30829 Start
	public boolean isDisableManagement() {
		return disableManagement;
	}

	public void setDisableManagement(boolean disableManagement) {
		this.disableManagement = disableManagement;
	}

	public boolean isDisableProf() {
		return disableProf;
	}

	public void setDisableProf(boolean disableProf) {
		this.disableProf = disableProf;
	}

	public boolean isDisableSemiProf() {
		return disableSemiProf;
	}

	public void setDisableSemiProf(boolean disableSemiProf) {
		this.disableSemiProf = disableSemiProf;
	}
	//#30829 End
}
