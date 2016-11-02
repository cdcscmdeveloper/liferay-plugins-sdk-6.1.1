package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;

public class SmSiteVisitFacilityVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8541133926840139549L;
	private PmCategoryL3 l3;
	private String catTypeLabel;
	private List<SmSupplierCategory> suppCategories;
	private List<SmFacility> facilities;
	private List<SmFacility> headQuarters;
	private List<SmAssociateCompany> assocCompanies;
	private List<SmSupplierBranch> suppBranches;
	private boolean shareAddress;

	public List<SmSupplierCategory> getSuppCategories() {
		return suppCategories;
	}

	public void setSuppCategories(List<SmSupplierCategory> suppCategories) {
		this.suppCategories = suppCategories;
	}

	public List<SmFacility> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<SmFacility> facilities) {
		this.facilities = facilities;
	}

	public String getCatTypeLabel() {
		return catTypeLabel;
	}

	public void setCatTypeLabel(String catTypeLabel) {
		this.catTypeLabel = catTypeLabel;
	}

	public List<SmAssociateCompany> getAssocCompanies() {
		return assocCompanies;
	}

	public void setAssocCompanies(List<SmAssociateCompany> assocCompanies) {
		this.assocCompanies = assocCompanies;
	}

	public List<SmSupplierBranch> getSuppBranches() {
		return suppBranches;
	}

	public void setSuppBranches(List<SmSupplierBranch> suppBranches) {
		this.suppBranches = suppBranches;
	}

	public PmCategoryL3 getL3() {
		return l3;
	}

	public void setL3(PmCategoryL3 l3) {
		this.l3 = l3;
	}

	public List<SmFacility> getHeadQuarters() {
		return headQuarters;
	}

	public void setHeadQuarters(List<SmFacility> headQuarters) {
		this.headQuarters = headQuarters;
	}

	public boolean isShareAddress() {
		return shareAddress;
	}

	public void setShareAddress(boolean shareAddress) {
		this.shareAddress = shareAddress;
	}
}
