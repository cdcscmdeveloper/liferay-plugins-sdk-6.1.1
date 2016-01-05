package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmSupplierCategory;

public class SmSiteVisitFacilityVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8541133926840139549L;
	private String facTypeLabel;
	private String facType;
	private List<SmSupplierCategory> suppCategories;
	private List<SmFacility> facilities;

	public String getFacType() {
		return facType;
	}

	public void setFacType(String facType) {
		this.facType = facType;
	}

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

	public String getFacTypeLabel() {
		return facTypeLabel;
	}

	public void setFacTypeLabel(String facTypeLabel) {
		this.facTypeLabel = facTypeLabel;
	}
}
