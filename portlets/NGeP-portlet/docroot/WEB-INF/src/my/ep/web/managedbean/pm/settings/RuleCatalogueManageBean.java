package my.ep.web.managedbean.pm.settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ProfileManagementConst;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaGst;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaValidity;
import my.ep.persistence.entity.pm.general.settings.gst.PmGstItem;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sm.SmSupplier;

import my.ep.web.managedbean.BaseManagedBean;

import my.ep.persistence.entity.pm.general.settings.threshold.PmThreshold;

@ManagedBean(name = "ruleCatalogueMB")
@SessionScoped
public class RuleCatalogueManageBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer tabActiveIndex = 0;
	private Integer feeExemptionIndex = 0;
	private Integer thresholdIndex = 0;
	private Integer gstIndex = 0;
	private PmConfigGeneral selectedConfigGeneral;
	private PmConfigRuleValue selectedRuleValue;
	private PmConfigRule selectedRuleType;
	private List<PmConfigRule> configRuleList;
	private String selectedDocumentType;
	private Date today = new Date();
	private Date tomorrow = null;
	private Date maxDate = null;
	private List<PmFeeExemption> feeExemptionsForOrgAndItem;
	private List<PmFeeExemption> feeExemptionsForComodity;
	private List<PmFeeExemption> feeExemptionsForContract;
	private List<PmFeeExemption> feeExemptionsForOrgType;
	private PmFeeExemption selectedFeeExemption;
	private SmSupplier selectedSmSupplier;
	private CtContractItem selectedCtContractItem;
//	private List<SelectItem> typeCriteria;
	private CmUnspsc selectedComodity;
	private CmItem selectedCmItem;
	private String cmItemName;
	private List<PmFeeExemption> deletedOrgAndItemFeeExemptions;
	private List<PmFeeExemption> deletedComodityFeeExemptions;
	private List<PmFeeExemption> deletedContractFeeExemptions;
	private List<PmFeeExemption> deletedOrgTypeFeeExemptions;
	private List<PmThreshold> thresholds;
	private List<PmThreshold> deletedThresholds;
	private PmOrgProfile selectedOrgProfile;
	private List<PmOrgProfile> orgProfileList;
	private PmThreshold threshold;
	private Number unplannedThreshold;
	private Number budgetThreshold;
	private List<PmThreshold> unplannedThresholdList;
	private List<PmThreshold> budgetThresholdList;
	private List<PmGstItem> taxableItems;
	private List<PmGstItem> deletedTaxableItems;
	private PmGstItem selectedGstItem;
	private String selectedSearchLvl;
	private String searchTitle;
	private List<CmUnspsc> unspscResultList;
	private CmUnspsc selectedUnspscResult;
	private Long selectedType;
	private Long selectedBrand;
	private Long selectedMeasurement;
	private Long selectedColor;
	private Date currentDate = new Date();
//	private List<PmDesignatedArea> designatedAreaList;
	private List<PmDaValidity> daValidityList;
//	private PmDesignatedArea selectedDesignatedArea;
	private PmDaValidity selectedDaValidity;
//	private List<PmDesignatedArea> deletedDesignatedAreaList;
    private List<PmDaValidity> deletedDaValidityList;
	private List<PmGstItem> taxableItemsForDA;
	private List<PmGstItem> deletedTaxableItemsForDA;
	private PmGstItem selectedGstItemForDA;
	private SelectItem[] typeList;
	private SelectItem[] brandList;
	private SelectItem[] measurementList;
	private SelectItem[] colorList;
	private List<PmGstItem> taxableItemsForG2G;
	private List<PmGstItem> deletedTaxableItemsForG2G;
	private PmGstItem selectedGstItemForG2G;
	private boolean autoUpdate = true;
	private List<PmDaGst> deletedDaGstList;
	private Map<String , Integer> thresholdIdToS2UnplannedMap = new HashMap<String , Integer>();
	private Map<String , Integer> thresholdIdToBudgetSpentMap = new HashMap<String , Integer>();
//	private boolean error1 = false;
//	private boolean error2 = false;
//	private boolean error3 = false;
//	private boolean error4 = false;
	private PmCategoryL3 selectedCategoryL3;
	private PmOrgProfile currentActiveOrg;
	/**
	 * @return the selectedCategoryL3
	 */
	public PmCategoryL3 getSelectedCategoryL3() {
		return selectedCategoryL3;
	}

	/**
	 * @param selectedCategoryL3 the selectedCategoryL3 to set
	 */
	public void setSelectedCategoryL3(PmCategoryL3 selectedCategoryL3) {
		this.selectedCategoryL3 = selectedCategoryL3;
	}

	public RuleCatalogueManageBean() {
		
	}

	public Date getTomorrow() {
		if(tomorrow == null){
			tomorrow = tomorrowDate();
		}
		return tomorrow;
	}


	public void setTomorrow(Date tomorrow) {
		this.tomorrow = tomorrow;
	}


	public Map<String, Integer> getThresholdIdToS2UnplannedMap() {
		return thresholdIdToS2UnplannedMap;
	}

	public void setThresholdIdToS2UnplannedMap(
			Map<String, Integer> thresholdIdToS2UnplannedMap) {
		this.thresholdIdToS2UnplannedMap = thresholdIdToS2UnplannedMap;
	}

	public Map<String, Integer> getThresholdIdToBudgetSpentMap() {
		return thresholdIdToBudgetSpentMap;
	}

	public void setThresholdIdToBudgetSpentMap(
			Map<String, Integer> thresholdIdToBudgetSpentMap) {
		this.thresholdIdToBudgetSpentMap = thresholdIdToBudgetSpentMap;
	}

	public String getCmItemName() {
		return cmItemName;
	}

	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	public List<PmFeeExemption> getFeeExemptionsForOrgAndItem() {
		if(this.feeExemptionsForOrgAndItem == null){
			this.feeExemptionsForOrgAndItem = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForOrgAndItem;
	}

	public void setFeeExemptionsForOrgAndItem(
			List<PmFeeExemption> feeExemptionsForOrgAndItem) {
		this.feeExemptionsForOrgAndItem = feeExemptionsForOrgAndItem;
	}

	public List<PmFeeExemption> getFeeExemptionsForComodity() {
		if(this.feeExemptionsForComodity == null){
			this.feeExemptionsForComodity = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForComodity;
	}

	public void setFeeExemptionsForComodity(
			List<PmFeeExemption> feeExemptionsForComodity) {
		this.feeExemptionsForComodity = feeExemptionsForComodity;
	}

	public List<PmFeeExemption> getFeeExemptionsForContract() {
		if(this.feeExemptionsForContract == null){
			this.feeExemptionsForContract = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForContract;
	}

	public void setFeeExemptionsForContract(
			List<PmFeeExemption> feeExemptionsForContract) {
		this.feeExemptionsForContract = feeExemptionsForContract;
	}

	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public PmConfigGeneral getSelectedConfigGeneral() {
		return selectedConfigGeneral;
	}

	public void setSelectedConfigGeneral(PmConfigGeneral selectedConfigGeneral) {
		System.out.println(".....setSelectedConfigGeneral called");
		this.selectedConfigGeneral = selectedConfigGeneral;
	}

	public PmConfigRule getSelectedRuleType() {

		return selectedRuleType;
	}

	public void setSelectedRuleType(PmConfigRule selectedRuleType) {

		this.selectedRuleType = selectedRuleType;
	}

	public Date getToday() {
		return today;
	}

	public void setToday(Date today) {
		this.today = today;
	}

	public List<PmConfigRule> getConfigRuleList() {
		return configRuleList;
	}

	public void setConfigRuleList(List<PmConfigRule> configRuleList) {
		this.configRuleList = configRuleList;
	}

	public PmConfigRuleValue getSelectedRuleValue() {
		return selectedRuleValue;
	}

	public void setSelectedRuleValue(PmConfigRuleValue selectedRuleValue) {
		this.selectedRuleValue = selectedRuleValue;
	}

	public String getSelectedDocumentType() {
		return selectedDocumentType;
	}

	public void setSelectedDocumentType(String selectedDocumentType) {
		System.out.println("....................setSelectedDocumentType to="+selectedDocumentType);
		this.selectedDocumentType = selectedDocumentType;
	}

	public PmFeeExemption getSelectedFeeExemption() {
		return selectedFeeExemption;
	}

	public void setSelectedFeeExemption(PmFeeExemption selectedFeeExemption) {
		this.selectedFeeExemption = selectedFeeExemption;
	}

	public SmSupplier getSelectedSmSupplier() {
		return selectedSmSupplier;
	}

	public void setSelectedSmSupplier(SmSupplier selectedSmSupplier) {
		this.selectedSmSupplier = selectedSmSupplier;
	}

	public CtContractItem getSelectedCtContractItem() {
		return selectedCtContractItem;
	}

	public void setSelectedCtContractItem(CtContractItem selectedCtContractItem) {
		this.selectedCtContractItem = selectedCtContractItem;
	}

	public Integer getFeeExemptionIndex() {
		return feeExemptionIndex;
	}

	public void setFeeExemptionIndex(Integer feeExemptionIndex) {
		this.feeExemptionIndex = feeExemptionIndex;
	}
	
//	public List<SelectItem> getTypeCriteria() {
//		if (typeCriteria == null)
//			typeCriteria = new ArrayList<SelectItem>();
//		return typeCriteria;
//	}
//
//	public void setTypeCriteria(List<SelectItem> typeCriteria) {
//		this.typeCriteria = typeCriteria;
//	}

	public CmUnspsc getSelectedComodity() {
		return selectedComodity;
	}

	public void setSelectedComodity(CmUnspsc selectedComodity) {
		this.selectedComodity = selectedComodity;
	}

	public CmItem getSelectedCmItem() {
		return selectedCmItem;
	}

	public void setSelectedCmItem(CmItem selectedCmItem) {
		this.selectedCmItem = selectedCmItem;
	}

	public List<PmFeeExemption> getDeletedOrgAndItemFeeExemptions() {
		if(deletedOrgAndItemFeeExemptions == null){
			deletedOrgAndItemFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedOrgAndItemFeeExemptions;
	}

	public void setDeletedOrgAndItemFeeExemptions(
			List<PmFeeExemption> deletedOrgAndItemFeeExemptions) {
		this.deletedOrgAndItemFeeExemptions = deletedOrgAndItemFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedComodityFeeExemptions() {
		if(deletedComodityFeeExemptions == null){
			deletedComodityFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedComodityFeeExemptions;
	}

	public void setDeletedComodityFeeExemptions(
			List<PmFeeExemption> deletedComodityFeeExemptions) {
		this.deletedComodityFeeExemptions = deletedComodityFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedContractFeeExemptions() {
		if(deletedContractFeeExemptions == null){
			deletedContractFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedContractFeeExemptions;
	}

	public void setDeletedContractFeeExemptions(
			List<PmFeeExemption> deletedContractFeeExemptions) {
		this.deletedContractFeeExemptions = deletedContractFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedOrgTypeFeeExemptions() {
		if(deletedOrgTypeFeeExemptions == null){
			deletedOrgTypeFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedOrgTypeFeeExemptions;
	}

	public void setDeletedOrgTypeFeeExemptions(
			List<PmFeeExemption> deletedOrgTypeFeeExemptions) {
		this.deletedOrgTypeFeeExemptions = deletedOrgTypeFeeExemptions;
	}

	public List<PmThreshold> getThresholds() {
		if(this.thresholds == null){
			this.thresholds = new ArrayList<PmThreshold>();
		}
		return this.thresholds;
	}

	public void setThresholds(List<PmThreshold> thresholds) {
		this.thresholds = thresholds;
	}

	public List<PmThreshold> getDeletedThresholds() {
		if(this.deletedThresholds == null){
			this.deletedThresholds = new ArrayList<PmThreshold>();
		}
		return deletedThresholds;
	}

	public void setDeletedThresholds(List<PmThreshold> deletedThresholds) {
		this.deletedThresholds = deletedThresholds;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		if(selectedOrgProfile == null){
			selectedOrgProfile = new PmOrgProfile();
		}
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public List<PmOrgProfile> getOrgProfileList() {
		return orgProfileList;
	}

	public void setOrgProfileList(List<PmOrgProfile> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	public Integer getThresholdIndex() {
		return thresholdIndex;
	}

	public void setThresholdIndex(Integer thresholdIndex) {
		this.thresholdIndex = thresholdIndex;
	}

	public PmThreshold getThreshold() {
		return threshold;
	}

	public void setThreshold(PmThreshold threshold) {
		this.threshold = threshold;
	}

	public Number getUnplannedThreshold() {
		return unplannedThreshold;
	}

	public void setUnplannedThreshold(Number unplannedThreshold) {
		this.unplannedThreshold = unplannedThreshold;
	}

	public Number getBudgetThreshold() {
		return budgetThreshold;
	}

	public void setBudgetThreshold(Number budgetThreshold) {
		this.budgetThreshold = budgetThreshold;
	}

	public List<PmThreshold> getUnplannedThresholdList() {
		return unplannedThresholdList;
	}

	public void setUnplannedThresholdList(List<PmThreshold> unplannedThresholdList) {
		this.unplannedThresholdList = unplannedThresholdList;
	}

	public List<PmThreshold> getBudgetThresholdList() {
		return budgetThresholdList;
	}

	public void setBudgetThresholdList(List<PmThreshold> budgetThresholdList) {
		this.budgetThresholdList = budgetThresholdList;
	}

	/**
	 * @return the taxableItems
	 */
	public List<PmGstItem> getTaxableItems() {
		if(taxableItems == null){
			taxableItems = new ArrayList<PmGstItem>();
		}
		return taxableItems;
	}

	/**
	 * @param taxableItems the taxableItems to set
	 */
	public void setTaxableItems(List<PmGstItem> taxableItems) {
		this.taxableItems = taxableItems;
	}

	/**
	 * @return the selectedGstItem
	 */
	public PmGstItem getSelectedGstItem() {
		return selectedGstItem;
	}

	/**
	 * @param selectedGstItem the selectedGstItem to set
	 */
	public void setSelectedGstItem(PmGstItem selectedGstItem) {
		this.selectedGstItem = selectedGstItem;
	}

	/**
	 * @return the selectedSearchLvl
	 */
	public String getSelectedSearchLvl() {
		return selectedSearchLvl;
	}

	/**
	 * @param selectedSearchLvl the selectedSearchLvl to set
	 */
	public void setSelectedSearchLvl(String selectedSearchLvl) {
		this.selectedSearchLvl = selectedSearchLvl;
	}

	/**
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * @param searchTitle the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * @return the unspscResultList
	 */
	public List<CmUnspsc> getUnspscResultList() {
		if(unspscResultList == null){
			unspscResultList = new ArrayList<CmUnspsc>();
		}
		return unspscResultList;
	}

	/**
	 * @param unspscResultList the unspscResultList to set
	 */
	public void setUnspscResultList(List<CmUnspsc> unspscResultList) {
		this.unspscResultList = unspscResultList;
	}

	/**
	 * @return the selectedUnspscResult
	 */
	public CmUnspsc getSelectedUnspscResult() {
		return selectedUnspscResult;
	}

	/**
	 * @param selectedUnspscResult the selectedUnspscResult to set
	 */
	public void setSelectedUnspscResult(CmUnspsc selectedUnspscResult) {
		this.selectedUnspscResult = selectedUnspscResult;
	}

	/**
	 * @return the selectedType
	 */
	public Long getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType the selectedType to set
	 */
	public void setSelectedType(Long selectedType) {
		this.selectedType = selectedType;
	}

	/**
	 * @return the selectedBrand
	 */
	public Long getSelectedBrand() {
		return selectedBrand;
	}

	/**
	 * @param selectedBrand the selectedBrand to set
	 */
	public void setSelectedBrand(Long selectedBrand) {
		this.selectedBrand = selectedBrand;
	}

	/**
	 * @return the selectedMeasurement
	 */
	public Long getSelectedMeasurement() {
		return selectedMeasurement;
	}

	/**
	 * @param selectedMeasurement the selectedMeasurement to set
	 */
	public void setSelectedMeasurement(Long selectedMeasurement) {
		this.selectedMeasurement = selectedMeasurement;
	}

	/**
	 * @return the selectedColor
	 */
	public Long getSelectedColor() {
		return selectedColor;
	}

	/**
	 * @param selectedColor the selectedColor to set
	 */
	public void setSelectedColor(Long selectedColor) {
		this.selectedColor = selectedColor;
	}

	/**
	 * @return the currentDate
	 */
	public Date getCurrentDate() {
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * @return the deletedTaxableItems
	 */
	public List<PmGstItem> getDeletedTaxableItems() {
		if(deletedTaxableItems == null){
			deletedTaxableItems = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItems;
	}

	/**
	 * @param deletedTaxableItems the deletedTaxableItems to set
	 */
	public void setDeletedTaxableItems(List<PmGstItem> deletedTaxableItems) {
		this.deletedTaxableItems = deletedTaxableItems;
	}



	public List<PmDaValidity> getDaValidityList() {
		if(daValidityList == null){
			daValidityList = new ArrayList<PmDaValidity>();
		}
		return daValidityList;
	}

	public void setDaValidityList(List<PmDaValidity> daValidityList) {
		this.daValidityList = daValidityList;
	}


	public PmDaValidity getSelectedDaValidity() {
		return selectedDaValidity;
	}

	public List<PmDaValidity> getDeletedDaValidityList() {
		if(deletedDaValidityList == null){
			deletedDaValidityList = new ArrayList<PmDaValidity>();
		}
		return deletedDaValidityList;
	}

	public void setSelectedDaValidity(PmDaValidity selectedDaValidity) {
		this.selectedDaValidity = selectedDaValidity;
	}

	public void setDeletedDaValidityList(List<PmDaValidity> deletedDaValidityList) {
		this.deletedDaValidityList = deletedDaValidityList;
	}

	/**
	 * @return the taxableItemsForDA
	 */
	public List<PmGstItem> getTaxableItemsForDA() {
		if(taxableItemsForDA == null){
			taxableItemsForDA = new ArrayList<PmGstItem>();
		}
		return taxableItemsForDA;
	}

	/**
	 * @param taxableItemsForDA the taxableItemsForDA to set
	 */
	public void setTaxableItemsForDA(List<PmGstItem> taxableItemsForDA) {
		this.taxableItemsForDA = taxableItemsForDA;
	}

	/**
	 * @return the deletedTaxableItemsForDA
	 */
	public List<PmGstItem> getDeletedTaxableItemsForDA() {
		if(deletedTaxableItemsForDA == null){
			deletedTaxableItemsForDA = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItemsForDA;
	}

	/**
	 * @param deletedTaxableItemsForDA the deletedTaxableItemsForDA to set
	 */
	public void setDeletedTaxableItemsDorDA(List<PmGstItem> deletedTaxableItemsForDA) {
		this.deletedTaxableItemsForDA = deletedTaxableItemsForDA;
	}

	/**
	 * @return the selectedGstItemForDA
	 */
	public PmGstItem getSelectedGstItemForDA() {
		return selectedGstItemForDA;
	}

	/**
	 * @param selectedGstItemForDA the selectedGstItemForDA to set
	 */
	public void setSelectedGstItemForDA(PmGstItem selectedGstItemForDA) {
		this.selectedGstItemForDA = selectedGstItemForDA;
	}

	/**
	 * @return the gstIndex
	 */
	public Integer getGstIndex() {
		return gstIndex;
	}

	/**
	 * @param gstIndex the gstIndex to set
	 */
	public void setGstIndex(Integer gstIndex) {
		this.gstIndex = gstIndex;
	}

	/**
	 * @return the typeList
	 */
	public SelectItem[] getTypeList() {
		return typeList;
	}

	/**
	 * @param typeList the typeList to set
	 */
	public void setTypeList(SelectItem[] typeList) {
		this.typeList = typeList;
	}

	/**
	 * @return the brandList
	 */
	public SelectItem[] getBrandList() {
		return brandList;
	}

	/**
	 * @param brandList the brandList to set
	 */
	public void setBrandList(SelectItem[] brandList) {
		this.brandList = brandList;
	}

	/**
	 * @return the colorList
	 */
	public SelectItem[] getColorList() {
		return colorList;
	}

	/**
	 * @param colorList the colorList to set
	 */
	public void setColorList(SelectItem[] colorList) {
		this.colorList = colorList;
	}

	/**
	 * @return the measurementList
	 */
	public SelectItem[] getMeasurementList() {
		return measurementList;
	}

	/**
	 * @param measurementList the measurementList to set
	 */
	public void setMeasurementList(SelectItem[] measurementList) {
		this.measurementList = measurementList;
	}

	/**
	 * @return the taxableItemsForG2G
	 */
	public List<PmGstItem> getTaxableItemsForG2G() {
		if(taxableItemsForG2G == null){
			taxableItemsForG2G = new ArrayList<PmGstItem>();
		}
		return taxableItemsForG2G;
	}

	/**
	 * @param taxableItemsForG2G the taxableItemsForG2G to set
	 */
	public void setTaxableItemsForG2G(List<PmGstItem> taxableItemsForG2G) {
		this.taxableItemsForG2G = taxableItemsForG2G;
	}

	/**
	 * @return the deletedTaxableItemsForG2G
	 */
	public List<PmGstItem> getDeletedTaxableItemsForG2G() {
		if(deletedTaxableItemsForG2G == null){
			deletedTaxableItemsForG2G = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItemsForG2G;
	}

	/**
	 * @param deletedTaxableItemsForG2G the deletedTaxableItemsForG2G to set
	 */
	public void setDeletedTaxableItemsForG2G(
			List<PmGstItem> deletedTaxableItemsForG2G) {
		this.deletedTaxableItemsForG2G = deletedTaxableItemsForG2G;
	}

	/**
	 * @return the selectedGstItemForG2G
	 */
	public PmGstItem getSelectedGstItemForG2G() {
		return selectedGstItemForG2G;
	}

	/**
	 * @param selectedGstItemForG2G the selectedGstItemForG2G to set
	 */
	public void setSelectedGstItemForG2G(PmGstItem selectedGstItemForG2G) {
		this.selectedGstItemForG2G = selectedGstItemForG2G;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the deletedDaGstList
	 */
	public List<PmDaGst> getDeletedDaGstList() {
		return deletedDaGstList;
	}

	/**
	 * @param deletedDaGstList the deletedDaGstList to set
	 */
	public void setDeletedDaGstList(List<PmDaGst> deletedDaGstList) {
		this.deletedDaGstList = deletedDaGstList;
	}
	public Date tomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 1);
		
        return c1.getTime();
    }
	
	public Date maxDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String dateInString = ProfileManagementConst.CALENDAR_MAX_DATE;
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = formatter.parse(dateInString);
			calendar.setTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}		
        return calendar.getTime();
    }

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	public List<PmFeeExemption> getFeeExemptionsForOrgType() {
		if(this.feeExemptionsForOrgType == null){
			this.feeExemptionsForOrgType = new ArrayList<PmFeeExemption>();
		}
		return feeExemptionsForOrgType;
	}

	public void setFeeExemptionsForOrgType(
			List<PmFeeExemption> feeExemptionsForOrgType) {
		this.feeExemptionsForOrgType = feeExemptionsForOrgType;
	}

	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		if(maxDate == null){
			maxDate = maxDate();
		}
		return maxDate;
	}

	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	} 

}
