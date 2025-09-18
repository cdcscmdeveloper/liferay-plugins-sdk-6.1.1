/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.settings;

import java.math.BigDecimal;
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
import my.ep.dto.pm.authorization.orgprofile.PtjSpecialApprovalResDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaGst;
import my.ep.persistence.entity.pm.general.settings.gst.PmDaValidity;
import my.ep.persistence.entity.pm.general.settings.gst.PmGstItem;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sm.SmSupplier;

import my.ep.web.managedbean.BaseManagedBean;

import my.ep.persistence.entity.pm.general.settings.threshold.PmThreshold;

/**
 * Managed Bean for Rule Catalogue
 * 
 * @author Zeinab
 * @since 14/7/2014
 * 
 */
@ManagedBean(name = "ruleCatalogueMB")
@SessionScoped
public class RuleCatalogueManageBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private Integer tabActiveIndex = 0;
	private Integer feeExemptionIndex = 0;
	private Integer thresholdIndex = 0;
	private Integer gstIndex = 0;
	private PmConfigGeneral selectedConfigGeneral;
	private PmConfigRuleValue selectedRuleValue;
	private PmConfigRuleDesc selectedRuleDescType;
	private List<PmConfigRuleDesc> configRuleDescList;
	private String selectedDocumentType;
	private Date today = new Date();
	private Date tomorrow = null;
	private Date aftertomorrow = null;
	private Date maxDate = null;
	private List<PmFeeExemption> feeExemptionsForOrgAndItem;
	private List<PmFeeExemption> feeExemptionsForComodity;
	private List<PmFeeExemption> feeExemptionsForContract;
	private List<PmFeeExemption> feeExemptionsForOrgType;
	private List<PmFeeExemption> feeExemptionsForYepProcessing;
	private PmFeeExemption selectedFeeExemption;
	private SmSupplier selectedSmSupplier;
	private CtContractItem selectedCtContractItem;
	// private List<SelectItem> typeCriteria;
	private CmUnspsc selectedComodity;
	private CmItem selectedCmItem;
	private String cmItemName;
	private List<PmFeeExemption> deletedOrgAndItemFeeExemptions;
	private List<PmFeeExemption> deletedComodityFeeExemptions;
	private List<PmFeeExemption> deletedContractFeeExemptions;
	private List<PmFeeExemption> deletedOrgTypeFeeExemptions;
	private List<PmFeeExemption> deletedYepProcessingFeeExemptions;
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
	// private List<PmDesignatedArea> designatedAreaList;
	private List<PmDaValidity> daValidityList;
	// private PmDesignatedArea selectedDesignatedArea;
	private PmDaValidity selectedDaValidity;
	// private List<PmDesignatedArea> deletedDesignatedAreaList;
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
	private Map<String, Integer> thresholdIdToS2UnplannedMap = new HashMap<String, Integer>();
	private Map<String, Integer> thresholdIdToBudgetSpentMap = new HashMap<String, Integer>();
	// private boolean error1 = false;
	// private boolean error2 = false;
	// private boolean error3 = false;
	// private boolean error4 = false;
	private PmCategoryL3 selectedCategoryL3;
	private PmOrgProfile currentActiveOrg;
	private Integer deleteRowIndex;
	private List<PmPtjSpecialApproval> mainPtjSpecialApprovalList;
	private List<PmPtjSpecialApproval> cloneMainPtjSpecialApprovalList;
    private List<PmPtjSpecialApproval> ptjSpecialApprovalList;
	private List<PmPtjSpecialApproval> deletedPtjSpecialApprovalList;
	private List<PmPtjSpecialApproval> newPtjSpecialApprovalList;
	private Long selectedMinistryId;
	private Long ministryProfileId;
	private boolean disabledSelectedMinistryId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	private List<PmOrgProfile> ptjList;
	private BigDecimal qtLimit;
	private Date minDate = null; //[#22119]
	private String ruleType;  //#22113
	private boolean LOPOSelected;
	/* 27827 - START */
	private List<PmZone> zoneList;
	private Long selectedZoneId;
	/* 27827 - END */

	/************ START: AUDIT #28895_35509 *************/
	private FeeExemptionResDTO resDTO;
	private FeeExemptionResDTO resDTOOld;

	private List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalListNewVal;
	private List<PmPtjSpecialApproval> auditDeletedMainPtjSpecialApprovalList;
	private List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalList;
	private List<PmPtjSpecialApproval> auditUpdateMainPtjSpecialApprovalList;
	private PtjSpecialApprovalResDTO qtValuesResDTO;
	private List<PmFeeExemption> auditDeletedFeeExemptionsForOrgType;
	private List<PmFeeExemption> auditUpdatedFeeExemptionsForOrgType;
	private List<PmFeeExemption> auditAddedFeeExemptionsForOrgType;
	private List<PmFeeExemption> auditFeeExemptionsForOrgTypeOld;
	private List<PmFeeExemption> auditDeletedFeeExemptionsForOrgItem;
	private List<PmFeeExemption> auditUpdatedFeeExemptionsForOrgItem;
	private List<PmFeeExemption> auditAddedFeeExemptionsForOrgItem;
	private List<PmFeeExemption> auditFeeExemptionsForOrgItemOld;
	private List<PmFeeExemption> auditDeletedFeeExemptionsForContract;
	private List<PmFeeExemption> auditUpdatedFeeExemptionsForContract;
	private List<PmFeeExemption> auditAddedFeeExemptionsForContract;
	private List<PmFeeExemption> auditFeeExemptionsForOrgContractOld;
	private List<PmFeeExemption> auditDeletedFeeExemptionsForComodity;
	private List<PmFeeExemption> auditUpdatedFeeExemptionsForComodity;
	private List<PmFeeExemption> auditAddedFeeExemptionsForComodity;
	private List<PmFeeExemption> auditFeeExemptionsForOrgComodityOld;

	private List<PmConfigRuleValue> auditResDTOSelectedRuleValueList;
	private List<PmConfigRuleValue> auditSelectedRuleValueListOld;
	private List<PmConfigRuleValue> auditDeletedSelectedRuleValueList;
	private List<PmConfigRuleValue> auditAddedSelectedRuleValueList;
	private List<PmConfigRuleValue> auditUpdatedSelectedRuleValueList;

	private String auditLevelCode;
	private String auditFeeExemptionType;

	/************ END: AUDIT #28895_35509 *************/
	
	public RuleCatalogueManageBean() {

	}
	/**
	 * @return the tomorrow
	 */
	public Date getTomorrow() {
		if (tomorrow == null) {
			tomorrow = tomorrowDate();
		}
		return tomorrow;
	}

	/**
	 * @param tomorrow
	 *            the tomorrow to set
	 */
	public void setTomorrow(Date tomorrow) {
		this.tomorrow = tomorrow;
	}

	
	/**
	 * @return the feeExemptionsForOrgAndItem
	 */
	public List<PmFeeExemption> getFeeExemptionsForOrgAndItem() {
		if (this.feeExemptionsForOrgAndItem == null) {
			this.feeExemptionsForOrgAndItem = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForOrgAndItem;
	}

	/**
	 * @param feeExemptionsForOrgAndItem
	 *            the feeExemptionsForOrgAndItem to set
	 */
	public void setFeeExemptionsForOrgAndItem(
			List<PmFeeExemption> feeExemptionsForOrgAndItem) {
		this.feeExemptionsForOrgAndItem = feeExemptionsForOrgAndItem;
	}

	/**
	 * @return the feeExemptionsForComodity
	 */
	public List<PmFeeExemption> getFeeExemptionsForComodity() {
		if (this.feeExemptionsForComodity == null) {
			this.feeExemptionsForComodity = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForComodity;
	}

	/**
	 * @param feeExemptionsForComodity
	 *            the feeExemptionsForComodity to set
	 */
	public void setFeeExemptionsForComodity(
			List<PmFeeExemption> feeExemptionsForComodity) {
		this.feeExemptionsForComodity = feeExemptionsForComodity;
	}

	/**
	 * @return the feeExemptionsForContract
	 */
	public List<PmFeeExemption> getFeeExemptionsForContract() {
		if (this.feeExemptionsForContract == null) {
			this.feeExemptionsForContract = new ArrayList<PmFeeExemption>();
		}
		return this.feeExemptionsForContract;
	}

	/**
	 * @param feeExemptionsForContract
	 *            the feeExemptionsForContract to set
	 */
	public void setFeeExemptionsForContract(
			List<PmFeeExemption> feeExemptionsForContract) {
		this.feeExemptionsForContract = feeExemptionsForContract;
	}


	// public List<SelectItem> getTypeCriteria() {
	// if (typeCriteria == null)
	// typeCriteria = new ArrayList<SelectItem>();
	// return typeCriteria;
	// }
	//
	// public void setTypeCriteria(List<SelectItem> typeCriteria) {
	// this.typeCriteria = typeCriteria;
	// }

	
	/**
	 * @return the deletedOrgAndItemFeeExemptions
	 */
	public List<PmFeeExemption> getDeletedOrgAndItemFeeExemptions() {
		if (deletedOrgAndItemFeeExemptions == null) {
			deletedOrgAndItemFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedOrgAndItemFeeExemptions;
	}

	/**
	 * @param deletedOrgAndItemFeeExemptions
	 *            the deletedOrgAndItemFeeExemptions to set
	 */
	public void setDeletedOrgAndItemFeeExemptions(
			List<PmFeeExemption> deletedOrgAndItemFeeExemptions) {
		this.deletedOrgAndItemFeeExemptions = deletedOrgAndItemFeeExemptions;
	}

	/**
	 * @return the deletedComodityFeeExemptions
	 */
	public List<PmFeeExemption> getDeletedComodityFeeExemptions() {
		if (deletedComodityFeeExemptions == null) {
			deletedComodityFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedComodityFeeExemptions;
	}

	/**
	 * @param deletedComodityFeeExemptions
	 *            the deletedComodityFeeExemptions to set
	 */
	public void setDeletedComodityFeeExemptions(
			List<PmFeeExemption> deletedComodityFeeExemptions) {
		this.deletedComodityFeeExemptions = deletedComodityFeeExemptions;
	}

	/**
	 * @return the deletedContractFeeExemptions
	 */
	public List<PmFeeExemption> getDeletedContractFeeExemptions() {
		if (deletedContractFeeExemptions == null) {
			deletedContractFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedContractFeeExemptions;
	}

	/**
	 * @param deletedContractFeeExemptions
	 *            the deletedContractFeeExemptions to set
	 */
	public void setDeletedContractFeeExemptions(
			List<PmFeeExemption> deletedContractFeeExemptions) {
		this.deletedContractFeeExemptions = deletedContractFeeExemptions;
	}

	/**
	 * @return the deletedOrgTypeFeeExemptions
	 */
	public List<PmFeeExemption> getDeletedOrgTypeFeeExemptions() {
		if (deletedOrgTypeFeeExemptions == null) {
			deletedOrgTypeFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedOrgTypeFeeExemptions;
	}

	/**
	 * @param deletedOrgTypeFeeExemptions
	 *            the deletedOrgTypeFeeExemptions to set
	 */
	public void setDeletedOrgTypeFeeExemptions(
			List<PmFeeExemption> deletedOrgTypeFeeExemptions) {
		this.deletedOrgTypeFeeExemptions = deletedOrgTypeFeeExemptions;
	}

	public List<PmFeeExemption> getDeletedYepProcessingFeeExemptions() {
		if (deletedYepProcessingFeeExemptions == null) {
			deletedYepProcessingFeeExemptions = new ArrayList<PmFeeExemption>();
		}
		return deletedYepProcessingFeeExemptions;
	}
	public void setDeletedYepProcessingFeeExemptions(List<PmFeeExemption> deletedYepProcessingFeeExemptions) {
		this.deletedYepProcessingFeeExemptions = deletedYepProcessingFeeExemptions;
	}
	/**
	 * @return the thresholds
	 */
	public List<PmThreshold> getThresholds() {
		if (this.thresholds == null) {
			this.thresholds = new ArrayList<PmThreshold>();
		}
		return this.thresholds;
	}

	/**
	 * @param thresholds
	 *            the thresholds to set
	 */
	public void setThresholds(List<PmThreshold> thresholds) {
		this.thresholds = thresholds;
	}

	/**
	 * @return the deletedThresholds
	 */
	public List<PmThreshold> getDeletedThresholds() {
		if (this.deletedThresholds == null) {
			this.deletedThresholds = new ArrayList<PmThreshold>();
		}
		return deletedThresholds;
	}

	/**
	 * @param deletedThresholds
	 *            the deletedThresholds to set
	 */
	public void setDeletedThresholds(List<PmThreshold> deletedThresholds) {
		this.deletedThresholds = deletedThresholds;
	}

	/**
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		if (selectedOrgProfile == null) {
			selectedOrgProfile = new PmOrgProfile();
		}
		return selectedOrgProfile;
	}

	/**
	 * @param selectedOrgProfile
	 *            the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}


	/**
	 * @return the taxableItems
	 */
	public List<PmGstItem> getTaxableItems() {
		if (taxableItems == null) {
			taxableItems = new ArrayList<PmGstItem>();
		}
		return taxableItems;
	}

	/**
	 * @param taxableItems
	 *            the taxableItems to set
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
	 * @param selectedGstItem
	 *            the selectedGstItem to set
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
	 * @param selectedSearchLvl
	 *            the selectedSearchLvl to set
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
	 * @param searchTitle
	 *            the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * @return the unspscResultList
	 */
	public List<CmUnspsc> getUnspscResultList() {
		if (unspscResultList == null) {
			unspscResultList = new ArrayList<CmUnspsc>();
		}
		return unspscResultList;
	}

	/**
	 * @param unspscResultList
	 *            the unspscResultList to set
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
	 * @param selectedUnspscResult
	 *            the selectedUnspscResult to set
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
	 * @param selectedType
	 *            the selectedType to set
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
	 * @param selectedBrand
	 *            the selectedBrand to set
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
	 * @param selectedMeasurement
	 *            the selectedMeasurement to set
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
	 * @param selectedColor
	 *            the selectedColor to set
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
	 * @param currentDate
	 *            the currentDate to set
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * @return the deletedTaxableItems
	 */
	public List<PmGstItem> getDeletedTaxableItems() {
		if (deletedTaxableItems == null) {
			deletedTaxableItems = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItems;
	}

	/**
	 * @param deletedTaxableItems
	 *            the deletedTaxableItems to set
	 */
	public void setDeletedTaxableItems(List<PmGstItem> deletedTaxableItems) {
		this.deletedTaxableItems = deletedTaxableItems;
	}

	public List<PmDaValidity> getDaValidityList() {
		if (daValidityList == null) {
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
		if (deletedDaValidityList == null) {
			deletedDaValidityList = new ArrayList<PmDaValidity>();
		}
		return deletedDaValidityList;
	}

	public void setSelectedDaValidity(PmDaValidity selectedDaValidity) {
		this.selectedDaValidity = selectedDaValidity;
	}

	public void setDeletedDaValidityList(
			List<PmDaValidity> deletedDaValidityList) {
		this.deletedDaValidityList = deletedDaValidityList;
	}

	/**
	 * @return the taxableItemsForDA
	 */
	public List<PmGstItem> getTaxableItemsForDA() {
		if (taxableItemsForDA == null) {
			taxableItemsForDA = new ArrayList<PmGstItem>();
		}
		return taxableItemsForDA;
	}

	/**
	 * @param taxableItemsForDA
	 *            the taxableItemsForDA to set
	 */
	public void setTaxableItemsForDA(List<PmGstItem> taxableItemsForDA) {
		this.taxableItemsForDA = taxableItemsForDA;
	}

	/**
	 * @param deletedTaxableItemsForDA the deletedTaxableItemsForDA to set
	 */
	public void setDeletedTaxableItemsForDA(List<PmGstItem> deletedTaxableItemsForDA) {
		this.deletedTaxableItemsForDA = deletedTaxableItemsForDA;
	}

	/**
	 * @return the deletedTaxableItemsForDA
	 */
	public List<PmGstItem> getDeletedTaxableItemsForDA() {
		if (deletedTaxableItemsForDA == null) {
			deletedTaxableItemsForDA = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItemsForDA;
	}

	/**
	 * @param deletedTaxableItemsForDA
	 *            the deletedTaxableItemsForDA to set
	 */
	public void setDeletedTaxableItemsDorDA(
			List<PmGstItem> deletedTaxableItemsForDA) {
		this.deletedTaxableItemsForDA = deletedTaxableItemsForDA;
	}

	/**
	 * @return the selectedGstItemForDA
	 */
	public PmGstItem getSelectedGstItemForDA() {
		return selectedGstItemForDA;
	}

	/**
	 * @param selectedGstItemForDA
	 *            the selectedGstItemForDA to set
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
	 * @param gstIndex
	 *            the gstIndex to set
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
	 * @param typeList
	 *            the typeList to set
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
	 * @param brandList
	 *            the brandList to set
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
	 * @param colorList
	 *            the colorList to set
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
	 * @param measurementList
	 *            the measurementList to set
	 */
	public void setMeasurementList(SelectItem[] measurementList) {
		this.measurementList = measurementList;
	}

	/**
	 * @return the taxableItemsForG2G
	 */
	public List<PmGstItem> getTaxableItemsForG2G() {
		if (taxableItemsForG2G == null) {
			taxableItemsForG2G = new ArrayList<PmGstItem>();
		}
		return taxableItemsForG2G;
	}

	/**
	 * @param taxableItemsForG2G
	 *            the taxableItemsForG2G to set
	 */
	public void setTaxableItemsForG2G(List<PmGstItem> taxableItemsForG2G) {
		this.taxableItemsForG2G = taxableItemsForG2G;
	}

	/**
	 * @return the deletedTaxableItemsForG2G
	 */
	public List<PmGstItem> getDeletedTaxableItemsForG2G() {
		if (deletedTaxableItemsForG2G == null) {
			deletedTaxableItemsForG2G = new ArrayList<PmGstItem>();
		}
		return deletedTaxableItemsForG2G;
	}

	/**
	 * @param deletedTaxableItemsForG2G
	 *            the deletedTaxableItemsForG2G to set
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
	 * @param selectedGstItemForG2G
	 *            the selectedGstItemForG2G to set
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
	 * @param autoUpdate
	 *            the autoUpdate to set
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
	 * @param deletedDaGstList
	 *            the deletedDaGstList to set
	 */
	public void setDeletedDaGstList(List<PmDaGst> deletedDaGstList) {
		this.deletedDaGstList = deletedDaGstList;
	}

	/**
	 * To get tomorrow date
	 * 
	 * @return tomorrow date
	 */
	public Date tomorrowDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), 0, 0, 0);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 1);

		return c1.getTime();
	}
	
	public Date AfterTomorrowDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), 0, 0, 0);
		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.DATE, 2);

		return c1.getTime();
	}

	/**
	 * To get Max Date
	 * 
	 * @return the maxDate
	 */
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
	 * @param currentActiveOrg
	 *            the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	public List<PmFeeExemption> getFeeExemptionsForOrgType() {
		if (this.feeExemptionsForOrgType == null) {
			this.feeExemptionsForOrgType = new ArrayList<PmFeeExemption>();
		}
		return feeExemptionsForOrgType;
	}

	public void setFeeExemptionsForOrgType(
			List<PmFeeExemption> feeExemptionsForOrgType) {
		this.feeExemptionsForOrgType = feeExemptionsForOrgType;
	}

	public List<PmFeeExemption> getFeeExemptionsForYepProcessing() {
		if (this.feeExemptionsForYepProcessing == null) {
			this.feeExemptionsForYepProcessing = new ArrayList<PmFeeExemption>();
		}
		return feeExemptionsForYepProcessing;
	}
	public void setFeeExemptionsForYepProcessing(List<PmFeeExemption> feeExemptionsForYepProcessing) {
		this.feeExemptionsForYepProcessing = feeExemptionsForYepProcessing;
	}
	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		if (maxDate == null) {
			maxDate = maxDate();
		}
		return maxDate;
	}

	/**
	 * @param maxDate
	 *            the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the deleteRowIndex
	 */
	public Integer getDeleteRowIndex() {
		return deleteRowIndex;
	}

	/**
	 * @param deleteRowIndex
	 *            the deleteRowIndex to set
	 */
	public void setDeleteRowIndex(Integer deleteRowIndex) {
		this.deleteRowIndex = deleteRowIndex;
	}

	/**
	 * @return the selectedCategoryL3
	 */
	public PmCategoryL3 getSelectedCategoryL3() {
		return selectedCategoryL3;
	}

	/**
	 * @param selectedCategoryL3
	 *            the selectedCategoryL3 to set
	 */
	public void setSelectedCategoryL3(PmCategoryL3 selectedCategoryL3) {
		this.selectedCategoryL3 = selectedCategoryL3;
	}

	/**
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}

	/**
	 * @param today the today to set
	 */
	public void setToday(Date today) {
		this.today = today;
	}

	/**
	 * @return the tabActiveIndex
	 */
	public Integer getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * @param tabActiveIndex the tabActiveIndex to set
	 */
	public void setTabActiveIndex(Integer tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * @return the feeExemptionIndex
	 */
	public Integer getFeeExemptionIndex() {
		return feeExemptionIndex;
	}

	/**
	 * @param feeExemptionIndex the feeExemptionIndex to set
	 */
	public void setFeeExemptionIndex(Integer feeExemptionIndex) {
		this.feeExemptionIndex = feeExemptionIndex;
	}

	/**
	 * @return the selectedConfigGeneral
	 */
	public PmConfigGeneral getSelectedConfigGeneral() {
		return selectedConfigGeneral;
	}

	/**
	 * @param selectedConfigGeneral the selectedConfigGeneral to set
	 */
	public void setSelectedConfigGeneral(PmConfigGeneral selectedConfigGeneral) {
		this.selectedConfigGeneral = selectedConfigGeneral;
	}

	/**
	 * @return the selectedRuleValue
	 */
	public PmConfigRuleValue getSelectedRuleValue() {
		return selectedRuleValue;
	}

	/**
	 * @param selectedRuleValue the selectedRuleValue to set
	 */
	public void setSelectedRuleValue(PmConfigRuleValue selectedRuleValue) {
		this.selectedRuleValue = selectedRuleValue;
	}

	/**
	 * @return the selectedDocumentType
	 */
	public String getSelectedDocumentType() {
		return selectedDocumentType;
	}

	/**
	 * @param selectedDocumentType the selectedDocumentType to set
	 */
	public void setSelectedDocumentType(String selectedDocumentType) {
		this.selectedDocumentType = selectedDocumentType;
	}

	/**
	 * @return the selectedFeeExemption
	 */
	public PmFeeExemption getSelectedFeeExemption() {
		return selectedFeeExemption;
	}

	/**
	 * @param selectedFeeExemption the selectedFeeExemption to set
	 */
	public void setSelectedFeeExemption(PmFeeExemption selectedFeeExemption) {
		this.selectedFeeExemption = selectedFeeExemption;
	}

	/**
	 * @return the selectedSmSupplier
	 */
	public SmSupplier getSelectedSmSupplier() {
		return selectedSmSupplier;
	}

	/**
	 * @param selectedSmSupplier the selectedSmSupplier to set
	 */
	public void setSelectedSmSupplier(SmSupplier selectedSmSupplier) {
		this.selectedSmSupplier = selectedSmSupplier;
	}

	/**
	 * @return the selectedCtContractItem
	 */
	public CtContractItem getSelectedCtContractItem() {
		return selectedCtContractItem;
	}

	/**
	 * @param selectedCtContractItem the selectedCtContractItem to set
	 */
	public void setSelectedCtContractItem(CtContractItem selectedCtContractItem) {
		this.selectedCtContractItem = selectedCtContractItem;
	}

	/**
	 * @return the thresholdIndex
	 */
	public Integer getThresholdIndex() {
		return thresholdIndex;
	}

	/**
	 * @param thresholdIndex the thresholdIndex to set
	 */
	public void setThresholdIndex(Integer thresholdIndex) {
		this.thresholdIndex = thresholdIndex;
	}

	/**
	 * @return the orgProfileList
	 */
	public List<PmOrgProfile> getOrgProfileList() {
		return orgProfileList;
	}

	/**
	 * @param orgProfileList the orgProfileList to set
	 */
	public void setOrgProfileList(List<PmOrgProfile> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	/**
	 * @return the threshold
	 */
	public PmThreshold getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(PmThreshold threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the unplannedThreshold
	 */
	public Number getUnplannedThreshold() {
		return unplannedThreshold;
	}

	/**
	 * @param unplannedThreshold the unplannedThreshold to set
	 */
	public void setUnplannedThreshold(Number unplannedThreshold) {
		this.unplannedThreshold = unplannedThreshold;
	}

	/**
	 * @return the budgetThreshold
	 */
	public Number getBudgetThreshold() {
		return budgetThreshold;
	}

	/**
	 * @param budgetThreshold the budgetThreshold to set
	 */
	public void setBudgetThreshold(Number budgetThreshold) {
		this.budgetThreshold = budgetThreshold;
	}

	/**
	 * @return the unplannedThresholdList
	 */
	public List<PmThreshold> getUnplannedThresholdList() {
		return unplannedThresholdList;
	}

	/**
	 * @param unplannedThresholdList the unplannedThresholdList to set
	 */
	public void setUnplannedThresholdList(List<PmThreshold> unplannedThresholdList) {
		this.unplannedThresholdList = unplannedThresholdList;
	}

	/**
	 * @return the budgetThresholdList
	 */
	public List<PmThreshold> getBudgetThresholdList() {
		return budgetThresholdList;
	}

	/**
	 * @param budgetThresholdList the budgetThresholdList to set
	 */
	public void setBudgetThresholdList(List<PmThreshold> budgetThresholdList) {
		this.budgetThresholdList = budgetThresholdList;
	}

	/**
	 * @return the selectedComodity
	 */
	public CmUnspsc getSelectedComodity() {
		return selectedComodity;
	}

	/**
	 * @param selectedComodity the selectedComodity to set
	 */
	public void setSelectedComodity(CmUnspsc selectedComodity) {
		this.selectedComodity = selectedComodity;
	}

	/**
	 * @return the selectedCmItem
	 */
	public CmItem getSelectedCmItem() {
		return selectedCmItem;
	}

	/**
	 * @param selectedCmItem the selectedCmItem to set
	 */
	public void setSelectedCmItem(CmItem selectedCmItem) {
		this.selectedCmItem = selectedCmItem;
	}

	/**
	 * @return the cmItemName
	 */
	public String getCmItemName() {
		return cmItemName;
	}

	/**
	 * @param cmItemName the cmItemName to set
	 */
	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	/**
	 * @return the thresholdIdToS2UnplannedMap
	 */
	public Map<String, Integer> getThresholdIdToS2UnplannedMap() {
		return thresholdIdToS2UnplannedMap;
	}

	/**
	 * @param thresholdIdToS2UnplannedMap the thresholdIdToS2UnplannedMap to set
	 */
	public void setThresholdIdToS2UnplannedMap(
			Map<String, Integer> thresholdIdToS2UnplannedMap) {
		this.thresholdIdToS2UnplannedMap = thresholdIdToS2UnplannedMap;
	}

	/**
	 * @return the thresholdIdToBudgetSpentMap
	 */
	public Map<String, Integer> getThresholdIdToBudgetSpentMap() {
		return thresholdIdToBudgetSpentMap;
	}

	/**
	 * @param thresholdIdToBudgetSpentMap the thresholdIdToBudgetSpentMap to set
	 */
	public void setThresholdIdToBudgetSpentMap(
			Map<String, Integer> thresholdIdToBudgetSpentMap) {
		this.thresholdIdToBudgetSpentMap = thresholdIdToBudgetSpentMap;
	}
	/**
	 * @return the configRuleDescList
	 */
	public List<PmConfigRuleDesc> getConfigRuleDescList() {
		if(configRuleDescList == null){
			configRuleDescList = new ArrayList<PmConfigRuleDesc>();
		}
		return configRuleDescList;
	}
	/**
	 * @param configRuleDescList the configRuleDescList to set
	 */
	public void setConfigRuleDescList(List<PmConfigRuleDesc> configRuleDescList) {
		this.configRuleDescList = configRuleDescList;
	}
	/**
	 * @return the selectedRuleDescType
	 */
	public PmConfigRuleDesc getSelectedRuleDescType() {
		return selectedRuleDescType;
	}
	/**
	 * @param selectedRuleDescType the selectedRuleDescType to set
	 */
	public void setSelectedRuleDescType(PmConfigRuleDesc selectedRuleDescType) {
		this.selectedRuleDescType = selectedRuleDescType;
	}

	/**
	 * @return the deletedPtjSpecialApprovalList
	 */
	public List<PmPtjSpecialApproval> getDeletedPtjSpecialApprovalList() {
		if(deletedPtjSpecialApprovalList == null){
			deletedPtjSpecialApprovalList = new ArrayList<PmPtjSpecialApproval>();
		}
		return deletedPtjSpecialApprovalList;
	}
	/**
	 * @param deletedPtjSpecialApprovalList the deletedPtjSpecialApprovalList to set
	 */
	public void setDeletedPtjSpecialApprovalList(
			List<PmPtjSpecialApproval> deletedPtjSpecialApprovalList) {
		this.deletedPtjSpecialApprovalList = deletedPtjSpecialApprovalList;
	}
	
	/**
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}
	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}
	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}
	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}
	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}
	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}
	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}
	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}
	/**
	 * @return the ptjList
	 */
	public List<PmOrgProfile> getPtjList() {
		if(ptjList == null){
			ptjList = new ArrayList<PmOrgProfile>();
		}
		return ptjList;
	}
	/**
	 * @param ptjList the ptjList to set
	 */
	public void setPtjList(List<PmOrgProfile> ptjList) {
		this.ptjList = ptjList;
	}
	/**
	 * @return the selectedMinistryId
	 */
	public Long getSelectedMinistryId() {
		return selectedMinistryId;
	}
	/**
	 * @param selectedMinistryId the selectedMinistryId to set
	 */
	public void setSelectedMinistryId(Long selectedMinistryId) {
		this.selectedMinistryId = selectedMinistryId;
	}
	/**
	 * @return the qtLimit
	 */
	public BigDecimal getQtLimit() {
		return qtLimit;
	}
	/**
	 * @param qtLimit the qtLimit to set
	 */
	public void setQtLimit(BigDecimal qtLimit) {
		this.qtLimit = qtLimit;
	}
	/**
	 * @return the disabledSelectedMinistryId
	 */
	public boolean isDisabledSelectedMinistryId() {
		return disabledSelectedMinistryId;
	}
	/**
	 * @param disabledSelectedMinistryId the disabledSelectedMinistryId to set
	 */
	public void setDisabledSelectedMinistryId(boolean disabledSelectedMinistryId) {
		this.disabledSelectedMinistryId = disabledSelectedMinistryId;
	}
	/**
	 * @return the newPtjSpecialApprovalList
	 */
	public List<PmPtjSpecialApproval> getNewPtjSpecialApprovalList() {
		if(newPtjSpecialApprovalList == null){
			newPtjSpecialApprovalList = new ArrayList<PmPtjSpecialApproval>();
		}
		return newPtjSpecialApprovalList;
	}
	/**
	 * @param newPtjSpecialApprovalList the newPtjSpecialApprovalList to set
	 */
	public void setNewPtjSpecialApprovalList(
			List<PmPtjSpecialApproval> newPtjSpecialApprovalList) {
		this.newPtjSpecialApprovalList = newPtjSpecialApprovalList;
	}
	/**
	 * @return the mainPtjSpecialApprovalList
	 */
	public List<PmPtjSpecialApproval> getMainPtjSpecialApprovalList() {
		if(mainPtjSpecialApprovalList == null){
			mainPtjSpecialApprovalList = new ArrayList<PmPtjSpecialApproval>();
		}
		return mainPtjSpecialApprovalList;
	}
	/**
	 * @param mainPtjSpecialApprovalList the mainPtjSpecialApprovalList to set
	 */
	public void setMainPtjSpecialApprovalList(
			List<PmPtjSpecialApproval> mainPtjSpecialApprovalList) {
		this.mainPtjSpecialApprovalList = mainPtjSpecialApprovalList;
	}
	/**
	 * @return the ptjSpecialApprovalList
	 */
	public List<PmPtjSpecialApproval> getPtjSpecialApprovalList() {
		if(ptjSpecialApprovalList == null){
			ptjSpecialApprovalList = new ArrayList<PmPtjSpecialApproval>();
		}
		return ptjSpecialApprovalList;
	}
	/**
	 * @param ptjSpecialApprovalList the ptjSpecialApprovalList to set
	 */
	public void setPtjSpecialApprovalList(List<PmPtjSpecialApproval> ptjSpecialApprovalList) {
		this.ptjSpecialApprovalList = ptjSpecialApprovalList;
	}
	/**
	 * @return the cloneMainPtjSpecialApprovalList
	 */
	public List<PmPtjSpecialApproval> getCloneMainPtjSpecialApprovalList() {
		if(cloneMainPtjSpecialApprovalList == null){
			cloneMainPtjSpecialApprovalList = new ArrayList<PmPtjSpecialApproval>();
		}
		return cloneMainPtjSpecialApprovalList;
	}
	/**
	 * @param cloneMainPtjSpecialApprovalList the cloneMainPtjSpecialApprovalList to set
	 */
	public void setCloneMainPtjSpecialApprovalList(
			List<PmPtjSpecialApproval> cloneMainPtjSpecialApprovalList) {
		this.cloneMainPtjSpecialApprovalList = cloneMainPtjSpecialApprovalList;
	}
	public Date getAftertomorrow() {
		
		if (aftertomorrow == null) {
			aftertomorrow = AfterTomorrowDate();
		}
		return aftertomorrow;
	}
	public void setAftertomorrow(Date aftertomorrow) {
		this.aftertomorrow = aftertomorrow;
	}
	
	public Date getMinDate() {
		if (minDate == null) {
			//get current date
			minDate = Calendar.getInstance().getTime();
		}
		return minDate;
	}
	
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	
	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	
	public boolean isLOPOSelected() {
		return LOPOSelected;
	}

	public void setLOPOSelected(boolean LOPOSelected) {
		this.LOPOSelected = LOPOSelected;
	}
	
	public List<PmZone> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}
	
	public Long getSelectedZoneId() {
		return selectedZoneId;
	}

	public void setSelectedZoneId(Long selectedZoneId) {
		this.selectedZoneId = selectedZoneId;
	}

	/************ START: AUDIT #28895_35509 *************/
	public FeeExemptionResDTO getResDTO() {
		if (resDTO == null) {
			resDTO = new FeeExemptionResDTO();
		}
		return resDTO;
	}

	public void setResDTO(FeeExemptionResDTO resDTO) {
		this.resDTO = resDTO;
	}

	public FeeExemptionResDTO getResDTOOld() {
		if (resDTOOld == null) {
			resDTOOld = new FeeExemptionResDTO();
		}
		return resDTOOld;
	}

	public void setResDTOOld(FeeExemptionResDTO resDTOOld) {
		this.resDTOOld = resDTOOld;
	}

	public List<PmPtjSpecialApproval> getAuditDeletedMainPtjSpecialApprovalList() {
		if (auditDeletedMainPtjSpecialApprovalList == null) {
			auditDeletedMainPtjSpecialApprovalList = new ArrayList<>();
		}
		return auditDeletedMainPtjSpecialApprovalList;
	}

	public void setAuditDeletedMainPtjSpecialApprovalList(List<PmPtjSpecialApproval> auditDeletedMainPtjSpecialApprovalList) {
		this.auditDeletedMainPtjSpecialApprovalList = auditDeletedMainPtjSpecialApprovalList;
	}

	public PtjSpecialApprovalResDTO getQtValuesResDTO() {
		return qtValuesResDTO;
	}

	public void setQtValuesResDTO(PtjSpecialApprovalResDTO qtValuesResDTO) {
		this.qtValuesResDTO = qtValuesResDTO;
	}

	public List<PmPtjSpecialApproval> getAuditMainPtjSpecialApprovalList() {
		if (auditMainPtjSpecialApprovalList == null) {
			auditMainPtjSpecialApprovalList = new ArrayList<>();
		}
		return auditMainPtjSpecialApprovalList;
	}

	public void setAuditMainPtjSpecialApprovalList(List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalList) {
		this.auditMainPtjSpecialApprovalList = auditMainPtjSpecialApprovalList;
	}

	public List<PmPtjSpecialApproval> getAuditMainPtjSpecialApprovalListNewVal() {
		if (auditMainPtjSpecialApprovalListNewVal == null) {
			auditMainPtjSpecialApprovalListNewVal = new ArrayList<>();
		}
		return auditMainPtjSpecialApprovalListNewVal;
	}

	public void setAuditMainPtjSpecialApprovalListNewVal(List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalListNewVal) {
		this.auditMainPtjSpecialApprovalListNewVal = auditMainPtjSpecialApprovalListNewVal;
	}

	public List<PmPtjSpecialApproval> getAuditUpdateMainPtjSpecialApprovalList() {
		if (auditUpdateMainPtjSpecialApprovalList == null) {
			auditUpdateMainPtjSpecialApprovalList = new ArrayList<>();
		}
		return auditUpdateMainPtjSpecialApprovalList;
	}

	public void setAuditUpdateMainPtjSpecialApprovalList(List<PmPtjSpecialApproval> auditUpdateMainPtjSpecialApprovalList) {
		this.auditUpdateMainPtjSpecialApprovalList = auditUpdateMainPtjSpecialApprovalList;
	}

	public List<PmFeeExemption> getAuditDeletedFeeExemptionsForOrgType() {
		if (auditDeletedFeeExemptionsForOrgType == null) {
			auditDeletedFeeExemptionsForOrgType = new ArrayList<>();
		}
		return auditDeletedFeeExemptionsForOrgType;
	}

	public void setAuditDeletedFeeExemptionsForOrgType(List<PmFeeExemption> auditDeletedFeeExemptionsForOrgType) {
		this.auditDeletedFeeExemptionsForOrgType = auditDeletedFeeExemptionsForOrgType;
	}

	public List<PmFeeExemption> getAuditUpdatedFeeExemptionsForOrgType() {
		if (auditUpdatedFeeExemptionsForOrgType == null) {
			auditUpdatedFeeExemptionsForOrgType = new ArrayList<>();
		}
		return auditUpdatedFeeExemptionsForOrgType;
	}

	public void setAuditUpdatedFeeExemptionsForOrgType(List<PmFeeExemption> auditUpdatedFeeExemptionsForOrgType) {
		this.auditUpdatedFeeExemptionsForOrgType = auditUpdatedFeeExemptionsForOrgType;
	}

	public List<PmFeeExemption> getAuditAddedFeeExemptionsForOrgType() {
		if (auditAddedFeeExemptionsForOrgType == null) {
			auditAddedFeeExemptionsForOrgType = new ArrayList<>();
		}
		return auditAddedFeeExemptionsForOrgType;
	}

	public void setAuditAddedFeeExemptionsForOrgType(List<PmFeeExemption> auditAddedFeeExemptionsForOrgType) {
		this.auditAddedFeeExemptionsForOrgType = auditAddedFeeExemptionsForOrgType;
	}

	public String getAuditLevelCode() {
		return auditLevelCode;
	}

	public void setAuditLevelCode(String auditLevelCode) {
		this.auditLevelCode = auditLevelCode;
	}

	public List<PmFeeExemption> getAuditFeeExemptionsForOrgTypeOld() {
		if (auditFeeExemptionsForOrgTypeOld == null) {
			auditFeeExemptionsForOrgTypeOld = new ArrayList<>();
		}
		return auditFeeExemptionsForOrgTypeOld;
	}

	public void setAuditFeeExemptionsForOrgTypeOld(List<PmFeeExemption> auditFeeExemptionsForOrgTypeOld) {
		this.auditFeeExemptionsForOrgTypeOld = auditFeeExemptionsForOrgTypeOld;
	}

	public List<PmFeeExemption> getAuditDeletedFeeExemptionsForOrgItem() {
		if (auditDeletedFeeExemptionsForOrgItem == null) {
			auditDeletedFeeExemptionsForOrgItem = new ArrayList<>();
		}
		return auditDeletedFeeExemptionsForOrgItem;
	}

	public void setAuditDeletedFeeExemptionsForOrgItem(List<PmFeeExemption> auditDeletedFeeExemptionsForOrgItem) {
		this.auditDeletedFeeExemptionsForOrgItem = auditDeletedFeeExemptionsForOrgItem;
	}

	public List<PmFeeExemption> getAuditUpdatedFeeExemptionsForOrgItem() {
		if (auditUpdatedFeeExemptionsForOrgItem == null) {
			auditUpdatedFeeExemptionsForOrgItem = new ArrayList<>();
		}
		return auditUpdatedFeeExemptionsForOrgItem;
	}

	public void setAuditUpdatedFeeExemptionsForOrgItem(List<PmFeeExemption> auditUpdatedFeeExemptionsForOrgItem) {
		this.auditUpdatedFeeExemptionsForOrgItem = auditUpdatedFeeExemptionsForOrgItem;
	}

	public List<PmFeeExemption> getAuditAddedFeeExemptionsForOrgItem() {
		if (auditAddedFeeExemptionsForOrgItem == null) {
			auditAddedFeeExemptionsForOrgItem = new ArrayList<>();
		}
		return auditAddedFeeExemptionsForOrgItem;
	}

	public void setAuditAddedFeeExemptionsForOrgItem(List<PmFeeExemption> auditAddedFeeExemptionsForOrgItem) {
		this.auditAddedFeeExemptionsForOrgItem = auditAddedFeeExemptionsForOrgItem;
	}

	public List<PmFeeExemption> getAuditFeeExemptionsForOrgItemOld() {
		if (auditFeeExemptionsForOrgItemOld == null) {
			auditFeeExemptionsForOrgItemOld = new ArrayList<>();
		}
		return auditFeeExemptionsForOrgItemOld;
	}

	public void setAuditFeeExemptionsForOrgItemOld(List<PmFeeExemption> auditFeeExemptionsForOrgItemOld) {
		this.auditFeeExemptionsForOrgItemOld = auditFeeExemptionsForOrgItemOld;
	}

	public List<PmFeeExemption> getAuditDeletedFeeExemptionsForContract() {
		if (auditDeletedFeeExemptionsForContract == null) {
			auditDeletedFeeExemptionsForContract = new ArrayList<>();
		}
		return auditDeletedFeeExemptionsForContract;
	}

	public void setAuditDeletedFeeExemptionsForContract(List<PmFeeExemption> auditDeletedFeeExemptionsForContract) {
		this.auditDeletedFeeExemptionsForContract = auditDeletedFeeExemptionsForContract;
	}

	public List<PmFeeExemption> getAuditUpdatedFeeExemptionsForContract() {
		if (auditUpdatedFeeExemptionsForContract == null) {
			auditUpdatedFeeExemptionsForContract = new ArrayList<>();
		}
		return auditUpdatedFeeExemptionsForContract;
	}

	public void setAuditUpdatedFeeExemptionsForContract(List<PmFeeExemption> auditUpdatedFeeExemptionsForContract) {
		this.auditUpdatedFeeExemptionsForContract = auditUpdatedFeeExemptionsForContract;
	}

	public List<PmFeeExemption> getAuditAddedFeeExemptionsForContract() {
		if (auditAddedFeeExemptionsForContract == null) {
			auditAddedFeeExemptionsForContract = new ArrayList<>();
		}
		return auditAddedFeeExemptionsForContract;
	}

	public void setAuditAddedFeeExemptionsForContract(List<PmFeeExemption> auditAddedFeeExemptionsForContract) {
		this.auditAddedFeeExemptionsForContract = auditAddedFeeExemptionsForContract;
	}

	public List<PmFeeExemption> getAuditFeeExemptionsForOrgContractOld() {
		if (auditFeeExemptionsForOrgContractOld == null) {
			auditFeeExemptionsForOrgContractOld = new ArrayList<>();
		}
		return auditFeeExemptionsForOrgContractOld;
	}

	public void setAuditFeeExemptionsForOrgContractOld(List<PmFeeExemption> auditFeeExemptionsForOrgContractOld) {
		this.auditFeeExemptionsForOrgContractOld = auditFeeExemptionsForOrgContractOld;
	}

	public List<PmFeeExemption> getAuditDeletedFeeExemptionsForComodity() {
		if (auditDeletedFeeExemptionsForComodity == null) {
			auditDeletedFeeExemptionsForComodity = new ArrayList<>();
		}
		return auditDeletedFeeExemptionsForComodity;
	}

	public void setAuditDeletedFeeExemptionsForComodity(List<PmFeeExemption> auditDeletedFeeExemptionsForComodity) {
		this.auditDeletedFeeExemptionsForComodity = auditDeletedFeeExemptionsForComodity;
	}

	public List<PmFeeExemption> getAuditUpdatedFeeExemptionsForComodity() {
		if (auditUpdatedFeeExemptionsForComodity == null) {
			auditUpdatedFeeExemptionsForComodity = new ArrayList<>();
		}
		return auditUpdatedFeeExemptionsForComodity;
	}

	public void setAuditUpdatedFeeExemptionsForComodity(List<PmFeeExemption> auditUpdatedFeeExemptionsForComodity) {
		this.auditUpdatedFeeExemptionsForComodity = auditUpdatedFeeExemptionsForComodity;
	}

	public List<PmFeeExemption> getAuditAddedFeeExemptionsForComodity() {
		if (auditAddedFeeExemptionsForComodity == null) {
			auditAddedFeeExemptionsForComodity = new ArrayList<>();
		}
		return auditAddedFeeExemptionsForComodity;
	}

	public void setAuditAddedFeeExemptionsForComodity(List<PmFeeExemption> auditAddedFeeExemptionsForComodity) {
		this.auditAddedFeeExemptionsForComodity = auditAddedFeeExemptionsForComodity;
	}

	public List<PmFeeExemption> getAuditFeeExemptionsForOrgComodityOld() {
		if (auditFeeExemptionsForOrgComodityOld == null) {
			auditFeeExemptionsForOrgComodityOld = new ArrayList<>();
		}
		return auditFeeExemptionsForOrgComodityOld;
	}

	public void setAuditFeeExemptionsForOrgComodityOld(List<PmFeeExemption> auditFeeExemptionsForOrgComodityOld) {
		this.auditFeeExemptionsForOrgComodityOld = auditFeeExemptionsForOrgComodityOld;
	}

	public String getAuditFeeExemptionType() {
		return auditFeeExemptionType;
	}

	public void setAuditFeeExemptionType(String auditFeeExemptionType) {
		this.auditFeeExemptionType = auditFeeExemptionType;
	}

	public List<PmConfigRuleValue> getAuditDeletedSelectedRuleValueList() {
		if (auditDeletedSelectedRuleValueList == null) {
			auditDeletedSelectedRuleValueList = new ArrayList<>();
		}
		return auditDeletedSelectedRuleValueList;
	}

	public void setAuditDeletedSelectedRuleValueList(List<PmConfigRuleValue> auditDeletedSelectedRuleValueList) {
		this.auditDeletedSelectedRuleValueList = auditDeletedSelectedRuleValueList;
	}

	public List<PmConfigRuleValue> getAuditUpdatedSelectedRuleValueList() {
		if (auditUpdatedSelectedRuleValueList == null) {
			auditUpdatedSelectedRuleValueList = new ArrayList<>();
		}
		return auditUpdatedSelectedRuleValueList;
	}

	public void setAuditUpdatedSelectedRuleValueList(List<PmConfigRuleValue> auditUpdatedSelectedRuleValueList) {
		this.auditUpdatedSelectedRuleValueList = auditUpdatedSelectedRuleValueList;
	}

	public List<PmConfigRuleValue> getAuditSelectedRuleValueListOld() {
		if (auditSelectedRuleValueListOld == null) {
			auditSelectedRuleValueListOld = new ArrayList<>();
		}
		return auditSelectedRuleValueListOld;
	}

	public void setAuditSelectedRuleValueListOld(List<PmConfigRuleValue> auditSelectedRuleValueListOld) {
		this.auditSelectedRuleValueListOld = auditSelectedRuleValueListOld;
	}

	public List<PmConfigRuleValue> getAuditAddedSelectedRuleValueList() {
		if (auditAddedSelectedRuleValueList == null) {
			auditAddedSelectedRuleValueList = new ArrayList<>();
		}
		return auditAddedSelectedRuleValueList;
	}

	public void setAuditAddedSelectedRuleValueList(List<PmConfigRuleValue> auditAddedSelectedRuleValueList) {
		this.auditAddedSelectedRuleValueList = auditAddedSelectedRuleValueList;
	}

	public List<PmConfigRuleValue> getAuditResDTOSelectedRuleValueList() {
		if (auditResDTOSelectedRuleValueList == null) {
			auditResDTOSelectedRuleValueList = new ArrayList<>();
		}
		return auditResDTOSelectedRuleValueList;
	}

	public void setAuditResDTOSelectedRuleValueList(List<PmConfigRuleValue> auditResDTOSelectedRuleValueList) {
		this.auditResDTOSelectedRuleValueList = auditResDTOSelectedRuleValueList;
	}
	/************ END: AUDIT #28895_35509 *************/
	
}
