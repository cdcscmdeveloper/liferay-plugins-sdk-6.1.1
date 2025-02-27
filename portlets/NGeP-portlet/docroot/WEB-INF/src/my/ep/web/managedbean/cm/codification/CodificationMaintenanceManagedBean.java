package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;

//35503 Start
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
//35503 End

@ManagedBean(name = "codificationMaintenanceMB")
@SessionScoped
public class CodificationMaintenanceManagedBean extends BaseManagedBean {

	private String cmNavigationURL;

	private List<CmUnspsc> unspscList;
	private CmUnspsc selectedUNSPSCObj;
	

	private String unspscCode;
	private String unspscTitle;
	private String typeName; /*[#27625]*/

	private List<CmUnspsc> contractedItem;


	private List<CmUnspsc> segmentList;
	private List<CmUnspsc> familyList;
	private List<CmUnspsc> classList;
	private List<CmUnspsc> commodityList;

	private CmUnspsc selectedLovUnspscSegment;
	private CmUnspsc selectedLovUnspscFamily;
	private CmUnspsc selectedLovUnspscClass;
	private CmUnspsc selectedLovUnspsc;

	private String itemCode;
	
	
	private List<SelectItem> segmentCriteria;
	private List<SelectItem> familyCriteria;
	private List<SelectItem> classCriteria;
	private List<SelectItem> commodityCriteria;
	private List<SelectItem> typeList; /*[#27625]*/

	private String segmentCode;
	private String familyCode;
	private String classCode;
	private String commodityCode;

	private String segmentTitle;
	private String familyTitle;
	private String classTitle;
	private String commodityTitle;
	
	private String segmentTitleEng;
	private String familyTitleEng;
	private String classTitleEng;
	private String commodityTitleEng;
	
	
	private List<CmUnspsc> unspscPopUpList;

	private CmUnspsc autoCompleteUnspscCode;

	private boolean refreshPageNoFlag=false;	
	private boolean newSegment = false;
	private boolean newFamily = false;
	private boolean newClass = false;
	private boolean newCommodity = false;
	
	
	/** The back to previous page. */
	private String backToPreviousPage;
	
	private boolean assignCodeFlg = false;
	private boolean editMode = true;
	private boolean editModeFamily = true;
	private boolean editModeClass= true;

	private String data;
	private String status;
	private String extension;
	private String statusextension;
	
	private List<Object[]> updatedStatus;
	private boolean disableSelectBtn = false;
	
	private SearchFilterBean titleFilterBean = new SearchFilterBean(
			SearchOperator.OPERATOR_LIKE.value());

	// 35503 Start
	private String auditCommodityCode;
	private String auditCommodityTitle;
	private String auditCommodityTitleEng;
	private String auditClassCode;
	private String auditClassTitle;
	private String auditClassTitleEng;
	private String auditFamilyCode;
	private String auditFamilyTitle;
	private String auditFamilyTitleEng;
	private String auditSegmentCode;
	private String auditSegmentTitle;
	private String auditSegmentTitleEng;
	private List<CmType> auditTypeExtList;
	private List<CmBrand> auditBrandExtList;
	private List<CmMeasurement> auditMeasurementExtList;
	private List<CmColor> auditColourExtList;

	// 35503 End
	
	public CmUnspsc getAutoCompleteUnspscCode() {
		if (autoCompleteUnspscCode == null)
			autoCompleteUnspscCode = new CmUnspsc();
		return autoCompleteUnspscCode;
	}

	public void setAutoCompleteUnspscCode(CmUnspsc autoCompleteUnspscCode) {
		this.autoCompleteUnspscCode = autoCompleteUnspscCode;
	}

	public List<CmUnspsc> getUnspscPopUpList() {
		if (unspscPopUpList == null)
			unspscPopUpList = new ArrayList<CmUnspsc>();
		return unspscPopUpList;
	}

	public void setUnspscPopUpList(List<CmUnspsc> unspscPopUpList) {
		this.unspscPopUpList = unspscPopUpList;
	}
	
	
    public List<Object[]> getUpdatedStatus() {
		if (updatedStatus == null)
			updatedStatus = new ArrayList<Object[]>();
		return updatedStatus;
	}
    
   public void setUpdatedStatus(List<Object[]> updatedStatus) {
	  this.updatedStatus = updatedStatus;
   }

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public List<SelectItem> getSegmentCriteria() {
		if (segmentCriteria == null)
			segmentCriteria = new ArrayList<SelectItem>();
		return segmentCriteria;
	}

	public void setSegmentCriteria(List<SelectItem> segmentCriteria) {
		this.segmentCriteria = segmentCriteria;
	}

	public List<SelectItem> getFamilyCriteria() {
		if (familyCriteria == null)
			familyCriteria = new ArrayList<SelectItem>();
		return familyCriteria;
	}

	public void setFamilyCriteria(List<SelectItem> familyCriteria) {
		this.familyCriteria = familyCriteria;
	}

	public List<SelectItem> getClassCriteria() {
		if (classCriteria == null)
			classCriteria = new ArrayList<SelectItem>();
		return classCriteria;
	}

	public void setClassCriteria(List<SelectItem> classCriteria) {
		this.classCriteria = classCriteria;
	}

	public List<SelectItem> getCommodityCriteria() {
		if (commodityCriteria == null)
			commodityCriteria = new ArrayList<SelectItem>();
		return commodityCriteria;
	}

	public void setCommodityCriteria(List<SelectItem> commodityCriteria) {
		this.commodityCriteria = commodityCriteria;
	}

	public List<CmUnspsc> getSegmentList() {
		if (segmentList == null)
			segmentList = new ArrayList<CmUnspsc>();
		return segmentList;
	}

	public void setSegmentList(List<CmUnspsc> segmentList) {
		this.segmentList = segmentList;
	}

	public List<CmUnspsc> getFamilyList() {
		if (familyList == null)
			familyList = new ArrayList<CmUnspsc>();
		return familyList;
	}

	public void setFamilyList(List<CmUnspsc> familyList) {
		this.familyList = familyList;
	}

	public List<CmUnspsc> getClassList() {
		if (classList == null)
			classList = new ArrayList<CmUnspsc>();
		return classList;
	}

	public void setClassList(List<CmUnspsc> classList) {
		this.classList = classList;
	}

	public List<CmUnspsc> getCommodityList() {
		if (commodityList == null)
			commodityList = new ArrayList<CmUnspsc>();
		return commodityList;
	}

	public void setCommodityList(List<CmUnspsc> commodityList) {
		this.commodityList = commodityList;
	}

	public List<CmUnspsc> getContractedItem() {
		if (contractedItem == null)
			contractedItem = new ArrayList<CmUnspsc>();
		return contractedItem;
	}

	public void setContractedItem(List<CmUnspsc> contractedItem) {
		this.contractedItem = contractedItem;
	}

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getUnspscTitle() {
		return unspscTitle;
	}

	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}

	public String getCmNavigationURL() {
		return cmNavigationURL;
	}

	public void setCmNavigationURL(String cmNavigationURL) {
		this.cmNavigationURL = cmNavigationURL;
	}

	public List<CmUnspsc> getUnspscList() {
		return unspscList;
	}

	public void setUnspscList(List<CmUnspsc> unspscList) {
		this.unspscList = unspscList;
	}

	public CmUnspsc getSelectedUNSPSCObj() {
		if (selectedUNSPSCObj == null)
			selectedUNSPSCObj = new CmUnspsc();
		return selectedUNSPSCObj;
	}

	public void setSelectedUNSPSCObj(CmUnspsc selectedUNSPSCObj) {
		this.selectedUNSPSCObj = selectedUNSPSCObj;

	}

	public String getSegmentTitle() {
		return segmentTitle;
	}

	public void setSegmentTitle(String segmentTitle) {
		this.segmentTitle = segmentTitle;
	}

	public String getFamilyTitle() {
		return familyTitle;
	}

	public void setFamilyTitle(String familyTitle) {
		this.familyTitle = familyTitle;
	}

	public String getClassTitle() {
		return classTitle;
	}

	public void setClassTitle(String classTitle) {
		this.classTitle = classTitle;
	}

	public String getCommodityTitle() {
		return commodityTitle;
	}

	public void setCommodityTitle(String commodityTitle) {
		this.commodityTitle = commodityTitle;
	}
	
	public String getSegmentTitleEng() {
		return segmentTitleEng;
	}

	public void setSegmentTitleEng(String segmentTitleEng) {
		this.segmentTitleEng = segmentTitleEng;
	}

	public String getFamilyTitleEng() {
		return familyTitleEng;
	}

	public void setFamilyTitleEng(String familyTitleEng) {
		this.familyTitleEng = familyTitleEng;
	}

	public String getClassTitleEng() {
		return classTitleEng;
	}

	public void setClassTitleEng(String classTitleEng) {
		this.classTitleEng = classTitleEng;
	}

	public String getCommodityTitleEng() {
		return commodityTitleEng;
	}

	public void setCommodityTitleEng(String commodityTitleEng) {
		this.commodityTitleEng = commodityTitleEng;
	}

	public CmUnspsc getSelectedLovUnspscSegment() {
		if (selectedLovUnspscSegment == null)
			selectedLovUnspscSegment = new CmUnspsc();

		return selectedLovUnspscSegment;
	}

	public void setSelectedLovUnspscSegment(CmUnspsc selectedLovUnspscSegment) {

		this.selectedLovUnspscSegment = selectedLovUnspscSegment;
		System.out.println(">>>>>>>>>>seting unspscSegment="
				+ selectedLovUnspscSegment);
	}

	public CmUnspsc getSelectedLovUnspscFamily() {
		if (selectedLovUnspscFamily == null)
			selectedLovUnspscFamily = new CmUnspsc();
		return selectedLovUnspscFamily;
	}

	public void setSelectedLovUnspscFamily(CmUnspsc selectedLovUnspscFamily) {
		this.selectedLovUnspscFamily = selectedLovUnspscFamily;
		System.out.println(">>>>>>>>>>seting selectedLovUnspscFamily="
				+ selectedLovUnspscFamily);
	}

	public CmUnspsc getSelectedLovUnspscClass() {
		if (selectedLovUnspscClass == null)
			selectedLovUnspscClass = new CmUnspsc();
		return selectedLovUnspscClass;
	}

	public void setSelectedLovUnspscClass(CmUnspsc selectedLovUnspscClass) {
		this.selectedLovUnspscClass = selectedLovUnspscClass;

	}
	
	
	public CmUnspsc getSelectedLovUnspsc() {
		if (selectedLovUnspsc == null)
			selectedLovUnspsc = new CmUnspsc();
		return selectedLovUnspsc;
	}

	public void setSelectedLovUnspsc(CmUnspsc selectedLovUnspsc) {
		this.selectedLovUnspsc = selectedLovUnspsc;

	}


	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	
	
	public boolean isRefreshPageNoFlag() {
		return refreshPageNoFlag;
	}

	public void setRefreshPageNoFlag(boolean refreshPageNoFlag) {
		this.refreshPageNoFlag = refreshPageNoFlag;
	}
	
	
	private String textBeforeSign;
	private boolean autoUpdate = true;
	private PmDigiSign pmDigiSign;
	 
	@ManagedProperty(value="#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;


	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}


	/**
	 * @return the backToPreviousPage
	 */
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}

	/**
	 * @param backToPreviousPage the backToPreviousPage to set
	 */
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}

	public boolean isAssignCodeFlg() {
		return assignCodeFlg;
	}

	public void setAssignCodeFlg(boolean assignCodeFlg) {
		this.assignCodeFlg = assignCodeFlg;
	}
	
	public boolean isNewSegment() {
		return newSegment;
	}

	public void setNewSegment(boolean newSegment) {
		this.newSegment = newSegment;
	}

	public boolean isNewFamily() {
		return newFamily;
	}

	public void setNewFamily(boolean newFamily) {
		this.newFamily = newFamily;
	}

	public boolean isNewClass() {
		return newClass;
	}

	public void setNewClass(boolean newClass) {
		this.newClass = newClass;
	}

	public boolean isNewCommodity() {
		return newCommodity;
	}

	public void setNewCommodity(boolean newCommodity) {
		this.newCommodity = newCommodity;
	}

	public boolean isEditMode() {
		return editMode;
	}
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
	
	public boolean isEditModeFamily() {
		return editModeFamily;
	}
	public void setEditModeFamily(boolean editModeFamily) {
		this.editModeFamily = editModeFamily;
	}
	
	public boolean isEditModeClass() {
		return editModeClass;
	}
	public void setEditModeClass(boolean editModeClass) {
		this.editModeClass = editModeClass;
	}
	
	
   public String getstatus() {
	    return status;
	 }

   public void setStatus(String status) {
       this.status = status;
  }
   
   public String getData() {
	   return data;
   }

   public void setData(String data) {
	   this.data = data;
   }
   
   public String getExtension() {
	   return extension;
   }

   public void setExtension(String extension) {
	   this.extension = extension;
   }
   
   public String getstatusextension() {
	    return statusextension;
	 }

   public void setStatusextension(String statusextension) {
	      this.statusextension = statusextension;
	   }
	  
	  
   public boolean isDisableSelectBtn() {
		return disableSelectBtn;
	}
	
	public void setDisableSelectBtn(boolean disableSelectBtn) {
		this.disableSelectBtn = disableSelectBtn;
	}
	

	public SearchFilterBean getTitleFilterBean() {
		return titleFilterBean;
	}

	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
		this.titleFilterBean = titleFilterBean;
	}

	/*[#27625] begin*/
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<SelectItem> getTypeList() {
		typeList = new ArrayList<SelectItem>();
		typeList.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		typeList.add(new SelectItem("P", ResourceBundleUtils.getMessage("lbl.txt.product",ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		typeList.add(new SelectItem("S", ResourceBundleUtils.getMessage("lbl.txt.service",ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		
		return typeList;
	}

	public void setTypeList(List<SelectItem> typeList) {
		this.typeList = typeList;
	}
	/*[#27625] end*/

	// 35503 Start
	public String getAuditCommodityTitle() {
		return auditCommodityTitle;
	}

	public String getAuditCommodityCode() {
		return auditCommodityCode;
	}

	public void setAuditCommodityCode(String auditCommodityCode) {
		this.auditCommodityCode = auditCommodityCode;
	}

	public void setAuditCommodityTitle(String auditCommodityTitle) {
		this.auditCommodityTitle = auditCommodityTitle;
	}

	public String getAuditCommodityTitleEng() {
		return auditCommodityTitleEng;
	}

	public void setAuditCommodityTitleEng(String auditCommodityTitleEng) {
		this.auditCommodityTitleEng = auditCommodityTitleEng;
	}

	public String getAuditClassCode() {
		return auditClassCode;
	}

	public void setAuditClassCode(String auditClassCode) {
		this.auditClassCode = auditClassCode;
	}

	public String getAuditClassTitle() {
		return auditClassTitle;
	}

	public void setAuditClassTitle(String auditClassTitle) {
		this.auditClassTitle = auditClassTitle;
	}

	public String getAuditClassTitleEng() {
		return auditClassTitleEng;
	}

	public void setAuditClassTitleEng(String auditClassTitleEng) {
		this.auditClassTitleEng = auditClassTitleEng;
	}

	public String getAuditFamilyCode() {
		return auditFamilyCode;
	}

	public void setAuditFamilyCode(String auditFamilyCode) {
		this.auditFamilyCode = auditFamilyCode;
	}

	public String getAuditFamilyTitle() {
		return auditFamilyTitle;
	}

	public void setAuditFamilyTitle(String auditFamilyTitle) {
		this.auditFamilyTitle = auditFamilyTitle;
	}

	public String getAuditFamilyTitleEng() {
		return auditFamilyTitleEng;
	}

	public void setAuditFamilyTitleEng(String auditFamilyTitleEng) {
		this.auditFamilyTitleEng = auditFamilyTitleEng;
	}

	public String getAuditSegmentCode() {
		return auditSegmentCode;
	}

	public void setAuditSegmentCode(String auditSegmentCode) {
		this.auditSegmentCode = auditSegmentCode;
	}

	public String getAuditSegmentTitle() {
		return auditSegmentTitle;
	}

	public void setAuditSegmentTitle(String auditSegmentTitle) {
		this.auditSegmentTitle = auditSegmentTitle;
	}

	public String getAuditSegmentTitleEng() {
		return auditSegmentTitleEng;
	}

	public void setAuditSegmentTitleEng(String auditSegmentTitleEng) {
		this.auditSegmentTitleEng = auditSegmentTitleEng;
	}

	public List<CmType> getAuditTypeExtList() {
		if (auditTypeExtList == null)
			auditTypeExtList = new ArrayList<>();
		return auditTypeExtList;
	}

	public void setAuditTypeExtList(List<CmType> auditTypeExtList) {
		this.auditTypeExtList = auditTypeExtList;
	}

	public List<CmBrand> getAuditBrandExtList() {
		if (auditBrandExtList == null)
			auditBrandExtList = new ArrayList<>();
		return auditBrandExtList;
	}

	public void setAuditBrandExtList(List<CmBrand> auditBrandExtList) {
		this.auditBrandExtList = auditBrandExtList;
	}

	public List<CmMeasurement> getAuditMeasurementExtList() {
		if (auditMeasurementExtList == null)
			auditMeasurementExtList = new ArrayList<>();
		return auditMeasurementExtList;
	}

	public void setAuditMeasurementExtList(List<CmMeasurement> auditMeasurementExtList) {
		this.auditMeasurementExtList = auditMeasurementExtList;
	}

	public List<CmColor> getAuditColourExtList() {
		if (auditColourExtList == null)
			auditColourExtList = new ArrayList<>();
		return auditColourExtList;
	}

	public void setAuditColourExtList(List<CmColor> auditColourExtList) {
		this.auditColourExtList = auditColourExtList;
	}
	// 35503 End
}