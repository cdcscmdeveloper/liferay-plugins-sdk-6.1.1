package my.ep.web.vo;

import java.io.Serializable;

public class SmSectionEditorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4112148478042640712L;

	// company basic
	private boolean poEditCompBasic;
	private boolean appEditCompBasic;
	private boolean suppEditCompBasic;

	// associate company and project
	private boolean poEditAssocProj;
	private boolean appEditAssocProj;
	private boolean suppEditAssocProj;

	// personnel
	private boolean poEditPersonnel;
	private boolean appEditPersonnel;
	private boolean suppEditPersonnel;
	private boolean hasForeign;

	// capital/equity
	private boolean poEditEquity;
	private boolean appEditEquity;
	private boolean suppEditEquity;

	// bank
	private boolean poEditBank;
	private boolean appEditBank;
	private boolean suppEditBank;

	// category
	private boolean poEditCategory;
	private boolean appEditCategory;
	private boolean suppEditCategory;

	// facility
	private boolean poEditFacility;
	private boolean appEditFacility;
	private boolean suppEditFacility;

	// equipment
	private boolean poEditEquipment;
	private boolean appEditEquipment;
	private boolean suppEditEquipment;

	// suppdoc
	private boolean poEditSuppDoc;
	private boolean appEditSuppDoc;
	private boolean suppEditSuppDoc;
	
	// site visit report
	private boolean poEditSiteVisit;
	private boolean appEditSiteVisit;
	private boolean suppEditSiteVisit;

	//#31230
	private boolean hasOverlapped;
	//27641 : Disciplinary Action - #26238
	private boolean disciplinary;
	
	public boolean isPoEditCompBasic() {
		return poEditCompBasic;
	}

	public void setPoEditCompBasic(boolean poEditCompBasic) {
		this.poEditCompBasic = poEditCompBasic;
	}

	public boolean isAppEditCompBasic() {
		return appEditCompBasic;
	}

	public void setAppEditCompBasic(boolean appEditCompBasic) {
		this.appEditCompBasic = appEditCompBasic;
	}

	public boolean isSuppEditCompBasic() {
		return suppEditCompBasic;
	}

	public void setSuppEditCompBasic(boolean suppEditCompBasic) {
		this.suppEditCompBasic = suppEditCompBasic;
	}

	public boolean isPoEditAssocProj() {
		return poEditAssocProj;
	}

	public void setPoEditAssocProj(boolean poEditAssocProj) {
		this.poEditAssocProj = poEditAssocProj;
	}

	public boolean isAppEditAssocProj() {
		return appEditAssocProj;
	}

	public void setAppEditAssocProj(boolean appEditAssocProj) {
		this.appEditAssocProj = appEditAssocProj;
	}

	public boolean isSuppEditAssocProj() {
		return suppEditAssocProj;
	}

	public void setSuppEditAssocProj(boolean suppEditAssocProj) {
		this.suppEditAssocProj = suppEditAssocProj;
	}

	public boolean isPoEditPersonnel() {
		return poEditPersonnel;
	}

	public void setPoEditPersonnel(boolean poEditPersonnel) {
		this.poEditPersonnel = poEditPersonnel;
	}

	public boolean isAppEditPersonnel() {
		return appEditPersonnel;
	}

	public void setAppEditPersonnel(boolean appEditPersonnel) {
		this.appEditPersonnel = appEditPersonnel;
	}

	public boolean isSuppEditPersonnel() {
		return suppEditPersonnel;
	}

	public void setSuppEditPersonnel(boolean suppEditPersonnel) {
		this.suppEditPersonnel = suppEditPersonnel;
	}

	public boolean isPoEditEquity() {
		return poEditEquity;
	}

	public void setPoEditEquity(boolean poEditEquity) {
		this.poEditEquity = poEditEquity;
	}

	public boolean isAppEditEquity() {
		return appEditEquity;
	}

	public void setAppEditEquity(boolean appEditEquity) {
		this.appEditEquity = appEditEquity;
	}

	public boolean isSuppEditEquity() {
		return suppEditEquity;
	}

	public void setSuppEditEquity(boolean suppEditEquity) {
		this.suppEditEquity = suppEditEquity;
	}

	public boolean isPoEditBank() {
		return poEditBank;
	}

	public void setPoEditBank(boolean poEditBank) {
		this.poEditBank = poEditBank;
	}

	public boolean isAppEditBank() {
		return appEditBank;
	}

	public void setAppEditBank(boolean appEditBank) {
		this.appEditBank = appEditBank;
	}

	public boolean isSuppEditBank() {
		return suppEditBank;
	}

	public void setSuppEditBank(boolean suppEditBank) {
		this.suppEditBank = suppEditBank;
	}

	public boolean isPoEditCategory() {
		return poEditCategory;
	}

	public void setPoEditCategory(boolean poEditCategory) {
		this.poEditCategory = poEditCategory;
	}

	public boolean isAppEditCategory() {
		return appEditCategory;
	}

	public void setAppEditCategory(boolean appEditCategory) {
		this.appEditCategory = appEditCategory;
	}

	public boolean isSuppEditCategory() {
		return suppEditCategory;
	}

	public void setSuppEditCategory(boolean suppEditCategory) {
		this.suppEditCategory = suppEditCategory;
	}

	public boolean isPoEditFacility() {
		return poEditFacility;
	}

	public void setPoEditFacility(boolean poEditFacility) {
		this.poEditFacility = poEditFacility;
	}

	public boolean isAppEditFacility() {
		return appEditFacility;
	}

	public void setAppEditFacility(boolean appEditFacility) {
		this.appEditFacility = appEditFacility;
	}

	public boolean isSuppEditFacility() {
		return suppEditFacility;
	}

	public void setSuppEditFacility(boolean suppEditFacility) {
		this.suppEditFacility = suppEditFacility;
	}

	public boolean isPoEditEquipment() {
		return poEditEquipment;
	}

	public void setPoEditEquipment(boolean poEditEquipment) {
		this.poEditEquipment = poEditEquipment;
	}

	public boolean isAppEditEquipment() {
		return appEditEquipment;
	}

	public void setAppEditEquipment(boolean appEditEquipment) {
		this.appEditEquipment = appEditEquipment;
	}

	public boolean isSuppEditEquipment() {
		return suppEditEquipment;
	}

	public void setSuppEditEquipment(boolean suppEditEquipment) {
		this.suppEditEquipment = suppEditEquipment;
	}

	public boolean isPoEditSuppDoc() {
		return poEditSuppDoc;
	}

	public void setPoEditSuppDoc(boolean poEditSuppDoc) {
		this.poEditSuppDoc = poEditSuppDoc;
	}

	public boolean isAppEditSuppDoc() {
		return appEditSuppDoc;
	}

	public void setAppEditSuppDoc(boolean appEditSuppDoc) {
		this.appEditSuppDoc = appEditSuppDoc;
	}

	public boolean isSuppEditSuppDoc() {
		return suppEditSuppDoc;
	}

	public void setSuppEditSuppDoc(boolean suppEditSuppDoc) {
		this.suppEditSuppDoc = suppEditSuppDoc;
	}

	public boolean isHasForeign() {
		return hasForeign;
	}

	public void setHasForeign(boolean hasForeign) {
		this.hasForeign = hasForeign;
	}

	public boolean isPoEditSiteVisit() {
		return poEditSiteVisit;
	}

	public void setPoEditSiteVisit(boolean poEditSiteVisit) {
		this.poEditSiteVisit = poEditSiteVisit;
	}

	public boolean isAppEditSiteVisit() {
		return appEditSiteVisit;
	}

	public void setAppEditSiteVisit(boolean appEditSiteVisit) {
		this.appEditSiteVisit = appEditSiteVisit;
	}

	public boolean isSuppEditSiteVisit() {
		return suppEditSiteVisit;
	}

	public void setSuppEditSiteVisit(boolean suppEditSiteVisit) {
		this.suppEditSiteVisit = suppEditSiteVisit;
	}

	//#31230 START
	public boolean isHasOverlapped() {
		return hasOverlapped;
	}

	public void setHasOverlapped(boolean hasOverlapped) {
		this.hasOverlapped = hasOverlapped;
	}
	//#31230 END
	
	public boolean isDisciplinary() {
		return disciplinary;
	}

	public void setDisciplinary(boolean disciplinary) {
		this.disciplinary = disciplinary;
	}

}
