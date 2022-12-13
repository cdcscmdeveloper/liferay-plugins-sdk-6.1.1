package my.ep.web.managedbean.sm.application;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

@ManagedBean(name = "suppApplMB")
@SessionScoped
public class SupplierApplicationManagedBean extends SmApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3101853568516292882L;
	
	private SmCompanyBasic smCompanyBasic;
	private List<SmStaffTotal> smStaffTotals;
	private List<SmSupplierCert> smSupplierCerts;
	private List<SmAssociateCompany> smAssociateCompanies;
	private List<SmCompanyExperience> smCompanyExperiences;
	private List<SmSupplierCategory> smSupplierCategories;
	private List<SmPersonnel> smPersonnels;
	private List<SmSvTask> smSvTasks;
	private List<SmApplSectionReview> smApplSectionReviews;
	private List<SmApplSupportDoc> smApplSupportDocs;
	public List<SmSvTask> getSmSvTasks() {
		return smSvTasks;
	}

	public void setSmSvTasks(List<SmSvTask> smSvTasks) {
		this.smSvTasks = smSvTasks;
	}

	private SmFacility basicCompanyFacility;
	private boolean loadCompInfo;
	private boolean loadAssocCompInfo;
	private boolean loadPersonnelBranchInfo;
	private boolean loadCapitalEquityInfo;
	private boolean loadBankInfo;
	private boolean loadCategoryInfo;

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

	public SmFacility getBasicCompanyFacility() {
		return basicCompanyFacility;
	}

	public void setBasicCompanyFacility(SmFacility basicCompanyFacility) {
		this.basicCompanyFacility = basicCompanyFacility;
	}

	public List<SmSupplierCert> getSmSupplierCerts() {
		return smSupplierCerts;
	}

	public void setSmSupplierCerts(List<SmSupplierCert> smSupplierCerts) {
		this.smSupplierCerts = smSupplierCerts;
	}

	public List<SmAssociateCompany> getSmAssociateCompanies() {
		return smAssociateCompanies;
	}

	public void setSmAssociateCompanies(
			List<SmAssociateCompany> smAssociateCompanies) {
		this.smAssociateCompanies = smAssociateCompanies;
	}

	public List<SmCompanyExperience> getSmCompanyExperiences() {
		return smCompanyExperiences;
	}

	public void setSmCompanyExperiences(
			List<SmCompanyExperience> smCompanyExperiences) {
		this.smCompanyExperiences = smCompanyExperiences;
	}

	public boolean isLoadCompInfo() {
		return loadCompInfo;
	}

	public void setLoadCompInfo(boolean loadCompInfo) {
		this.loadCompInfo = loadCompInfo;
	}

	public boolean isLoadAssocCompInfo() {
		return loadAssocCompInfo;
	}

	public void setLoadAssocCompInfo(boolean loadAssocCompInfo) {
		this.loadAssocCompInfo = loadAssocCompInfo;
	}

	public boolean isLoadPersonnelBranchInfo() {
		return loadPersonnelBranchInfo;
	}

	public void setLoadPersonnelBranchInfo(boolean loadPersonnelBranchInfo) {
		this.loadPersonnelBranchInfo = loadPersonnelBranchInfo;
	}

	public boolean isLoadCapitalEquityInfo() {
		return loadCapitalEquityInfo;
	}

	public void setLoadCapitalEquityInfo(boolean loadCapitalEquityInfo) {
		this.loadCapitalEquityInfo = loadCapitalEquityInfo;
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

	public List<SmSupplierCategory> getSmSupplierCategories() {
		return smSupplierCategories;
	}

	public void setSmSupplierCategories(
			List<SmSupplierCategory> smSupplierCategories) {
		this.smSupplierCategories = smSupplierCategories;
	}

	public List<SmPersonnel> getSmPersonnels() {
		return smPersonnels;
	}

	public void setSmPersonnels(List<SmPersonnel> smPersonnels) {
		this.smPersonnels = smPersonnels;
	}

	public List<SmApplSectionReview> getSmApplSectionReviews() {
		return smApplSectionReviews;
	}

	public void setSmApplSectionReviews(
			List<SmApplSectionReview> smApplSectionReviews) {
		this.smApplSectionReviews = smApplSectionReviews;
	}

	public List<SmApplSupportDoc> getSmApplSupportDocs() {
		return smApplSupportDocs;
	}

	public void setSmApplSupportDocs(List<SmApplSupportDoc> smApplSupportDocs) {
		this.smApplSupportDocs = smApplSupportDocs;
	}

}
