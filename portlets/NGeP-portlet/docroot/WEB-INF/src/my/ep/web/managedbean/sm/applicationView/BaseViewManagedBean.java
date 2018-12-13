package my.ep.web.managedbean.sm.applicationView;

import java.util.List;

import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;

public abstract class BaseViewManagedBean extends
		BaseReviewApplicationManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmTrackingDiaryBackingBean trackingDiary;
	private Long applId;
	private SmCompanyBasic smCompanyBasic;
	private List<SmStaffTotal> smStaffTotals;
	private List<SmSupplierCert> smSupplierCerts;
	private List<SmAssociationBody> smAssociationBodies;
	private List<SmAssociateCompany> smAssociateCompanies;
	private List<SmCompanyExperience> smCompanyExperiences;
	private List<SmPersonnel> smPersonnels;
	private List<SmSupplierBranch> smSupplierBranches;
	private List<SmEquityCompany> smEquityCompanies;
	private List<SmMachineDtl> smMachineDtls;
	private List<SmManufacturingDtl> smManufacturingDtls;
	private List<SmSupplierSystem> smSupplierSystems;
	private List<SmApplSupportDoc> smApplSupportDocs;
	private SmApplSectionReview poSecReview;
	private SmApplSectionReview apprvrSecReview;

	private boolean loadCompanyInfo;
	private boolean loadAssocCompanyProjects;
	private boolean loadPersonnelBranch;
	private boolean loadCapitalEquityStatus;
	private boolean loadBankInfo;
	private boolean loadCategoryInfo;
	private boolean loadFacilityInfo;
	private boolean loadEquipmentInfo;
	private boolean loadSuppDocInfo;

	public SmCompanyBasic getSmCompanyBasic() {
		return smCompanyBasic;
	}

	public void setSmCompanyBasic(SmCompanyBasic smCompanyBasic) {
		this.smCompanyBasic = smCompanyBasic;
	}

	public List<SmStaffTotal> getSmStaffTotals() {
		return smStaffTotals;
	}

	public void setSmStaffTotals(List<SmStaffTotal> smStaffTotals) {
		this.smStaffTotals = smStaffTotals;
	}

	public SmApplSectionReview getPoSecReview() {
		return poSecReview;
	}

	public void setPoSecReview(SmApplSectionReview poSecReview) {
		this.poSecReview = poSecReview;
	}

	public SmApplSectionReview getApprvrSecReview() {
		return apprvrSecReview;
	}

	public void setApprvrSecReview(SmApplSectionReview apprvrSecReview) {
		this.apprvrSecReview = apprvrSecReview;
	}

	public List<SmSupplierCert> getSmSupplierCerts() {
		return smSupplierCerts;
	}

	public void setSmSupplierCerts(List<SmSupplierCert> smSupplierCerts) {
		this.smSupplierCerts = smSupplierCerts;
	}

	public List<SmAssociationBody> getSmAssociationBodies() {
		return smAssociationBodies;
	}

	public void setSmAssociationBodies(
			List<SmAssociationBody> smAssociationBodies) {
		this.smAssociationBodies = smAssociationBodies;
	}

	public boolean isLoadCompanyInfo() {
		return loadCompanyInfo;
	}

	public void setLoadCompanyInfo(boolean loadCompanyInfo) {
		this.loadCompanyInfo = loadCompanyInfo;
	}

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
	}

	public List<SmAssociateCompany> getSmAssociateCompanies() {
		return smAssociateCompanies;
	}

	public void setSmAssociateCompanies(
			List<SmAssociateCompany> smAssociateCompanies) {
		this.smAssociateCompanies = smAssociateCompanies;
	}

	public boolean isLoadAssocCompanyProjects() {
		return loadAssocCompanyProjects;
	}

	public void setLoadAssocCompanyProjects(boolean loadAssocCompanyProjects) {
		this.loadAssocCompanyProjects = loadAssocCompanyProjects;
	}

	public List<SmCompanyExperience> getSmCompanyExperiences() {
		return smCompanyExperiences;
	}

	public void setSmCompanyExperiences(
			List<SmCompanyExperience> smCompanyExperiences) {
		this.smCompanyExperiences = smCompanyExperiences;
	}

	public boolean isLoadPersonnelBranch() {
		return loadPersonnelBranch;
	}

	public void setLoadPersonnelBranch(boolean loadPersonnelBranch) {
		this.loadPersonnelBranch = loadPersonnelBranch;
	}

	public List<SmPersonnel> getSmPersonnels() {
		return smPersonnels;
	}

	public void setSmPersonnels(List<SmPersonnel> smPersonnels) {
		this.smPersonnels = smPersonnels;
	}

	public List<SmSupplierBranch> getSmSupplierBranches() {
		return smSupplierBranches;
	}

	public void setSmSupplierBranches(List<SmSupplierBranch> smSupplierBranches) {
		this.smSupplierBranches = smSupplierBranches;
	}

	public boolean isLoadCapitalEquityStatus() {
		return loadCapitalEquityStatus;
	}

	public void setLoadCapitalEquityStatus(boolean loadCapitalEquityStatus) {
		this.loadCapitalEquityStatus = loadCapitalEquityStatus;
	}

	public List<SmEquityCompany> getSmEquityCompanies() {
		return smEquityCompanies;
	}

	public void setSmEquityCompanies(List<SmEquityCompany> smEquityCompanies) {
		this.smEquityCompanies = smEquityCompanies;
	}

	public boolean isLoadBankInfo() {
		return loadBankInfo;
	}

	public void setLoadBankInfo(boolean loadBankInfo) {
		this.loadBankInfo = loadBankInfo;
	}

	public boolean isLoadCategoryInfo() {
		return loadCategoryInfo;
	}

	public void setLoadCategoryInfo(boolean loadCategoryInfo) {
		this.loadCategoryInfo = loadCategoryInfo;
	}

	public boolean isLoadFacilityInfo() {
		return loadFacilityInfo;
	}

	public void setLoadFacilityInfo(boolean loadFacilityInfo) {
		this.loadFacilityInfo = loadFacilityInfo;
	}

	public boolean isLoadEquipmentInfo() {
		return loadEquipmentInfo;
	}

	public void setLoadEquipmentInfo(boolean loadEquipmentInfo) {
		this.loadEquipmentInfo = loadEquipmentInfo;
	}

	public List<SmMachineDtl> getSmMachineDtls() {
		return smMachineDtls;
	}

	public void setSmMachineDtls(List<SmMachineDtl> smMachineDtls) {
		this.smMachineDtls = smMachineDtls;
	}

	public boolean isLoadSuppDocInfo() {
		return loadSuppDocInfo;
	}

	public void setLoadSuppDocInfo(boolean loadSuppDocInfo) {
		this.loadSuppDocInfo = loadSuppDocInfo;
	}

	public List<SmManufacturingDtl> getSmManufacturingDtls() {
		return smManufacturingDtls;
	}

	public void setSmManufacturingDtls(
			List<SmManufacturingDtl> smManufacturingDtls) {
		this.smManufacturingDtls = smManufacturingDtls;
	}

	public List<SmSupplierSystem> getSmSupplierSystems() {
		return smSupplierSystems;
	}

	public void setSmSupplierSystems(List<SmSupplierSystem> smSupplierSystems) {
		this.smSupplierSystems = smSupplierSystems;
	}

	public List<SmApplSupportDoc> getSmApplSupportDocs() {
		return smApplSupportDocs;
	}

	public void setSmApplSupportDocs(List<SmApplSupportDoc> smApplSupportDocs) {
		this.smApplSupportDocs = smApplSupportDocs;
	}

	public PmTrackingDiaryBackingBean getTrackingDiary() {
		if (trackingDiary == null) {
			return new PmTrackingDiaryBackingBean();
		} else {
			return trackingDiary;
		}
	}

	public void setTrackingDiary(PmTrackingDiaryBackingBean trackingDiary) {
		this.trackingDiary = trackingDiary;
	}
}
