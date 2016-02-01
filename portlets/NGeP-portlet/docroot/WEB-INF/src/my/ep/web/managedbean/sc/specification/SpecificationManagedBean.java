/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSpecificationRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.TreeNode;

/**
 * The Class SpecificationManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */
@ManagedBean(name = "specificationMB")
@SessionScoped
public class SpecificationManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(SpecificationManagedBean.class);

	/** The sc qt remarks. */
	private ScQtRemark scQtRemarks;

	/** The sc qt checklist. */
	private ScQtCheckList scQtChecklist;

	/** The sc qt check st2tech proposal. */
	private List<ScQtCheckList> scQtCheckSt2techProposal;

	/** The mark deleted tech checklist. */
	private List<ScQtCheckList> markDeletedTechChecklist;

	/** The sc qt check st2finance proposal list. */
	private List<ScQtCheckList> scQtCheckSt2financeProposalList;

	/** The mark deleted fin checklist. */
	private List<ScQtCheckList> markDeletedFinChecklist;

	/** The sc remark. */
	private ScRemark scRemark;

	/** The committee activities. */
	private List<ScQtCommitteeActivity> committeeActivities;

	/** The is sample req. */
	public boolean isSampleReq = true;

	/** The is spec status. */
	private boolean isSpecStatus;

	/** The procurement id. */
	private Long procurementId;

	/** The evaluation type. */
	private String evaluationType;

	/** The sc qt. */
	private ScQt scQt;

	/** The stage two tech compliance list. */
	private List<PmDocument> stageTwoTechComplianceList;

	/** The stage two tech compliance select. */
	private List<PmDocument> stageTwoTechComplianceSelect;

	/** The stage two fin compliance list. */
	private List<PmDocument> stageTwoFinComplianceList;

	/** The stage two fin compliance select. */
	private List<PmDocument> stageTwoFinComplianceSelect;

	/** The s2 tech select. */
	private List<ScQtCheckList> s2TechSelect;

	/** The s2 fin select. */
	private List<ScQtCheckList> s2FinSelect;

	/** The schedule type. */
	private String scheduleType;

	/** The from month. */
	private String fromMonth;

	/** The to month. */
	private String toMonth;

	/** The start year. */
	private String startYear;

	/** The end year. */
	private String endYear;
	// B,S
	/** The business owner. */
	private String businessOwner = "B";

	/** The check list score. */
	private List<ScQtCheckListScore> checkListScore;

	/** The scoring for years. */
	private List<ScQtCheckListScore> scoringForYears;

	/** The check list select score. */
	private List<ScQtCheckListScore> checkListSelectScore;

	/** The schedule list. */
	private List<ScQtSchedule> scheduleList;

	/** The progressive payment list. */
	private List<ScQtProgressivePayment> progressivePaymentList;

	/** The total frequency. */
	private int totalFrequency;

	/** The total progressive payment. */
	private int totalProgressivePayment;

	/** The cir cular no. */
	private String cirCularNo;

	/** The circular name. */
	private String circularName;

	/** The item name. */
	private String itemName;

	/** The technical weightage score. */
	private BigDecimal technicalWeightageScore;

	/** The financial weightage score. */
	private BigDecimal financialWeightageScore;

	/** The total amount. */
	private int totalAmount;

	/** The total amount avg. */
	private BigDecimal totalAmountAvg;

	/** The selectpanel item. */
	private List<CtPanelItem> selectpanelItem;

	/** The panel item list. */
	private List<CtPanelItem> panelItemList;

	/** The is panel. */
	private boolean isPanel;

	/** The ct zone trees. */
	private TreeNode ctZoneTrees;

	/** The ct zone tree selected nodes. */
	private TreeNode[] ctZoneTreeSelectedNodes;

	/** The ct zone suppliers. */
	private List<TreeNode> ctZoneSuppliers = new ArrayList<TreeNode>();

	/** The ct zone supplier list. */
	private List<CtPanelSupplier> ctZoneSupplierList;

	// supplier Qualification Criteria List and variables

	/** The supp criteriaopen list. */
	private List<ScQtCategory> suppCriteriaopenList;

	/** The supp criteriaopen select list. */
	private List<ScQtCategory> suppCriteriaopenSelectList;

	/** The root location tree. */
	private TreeNode rootLocationTree;

	/** The root zoanl tree. */
	private TreeNode rootZoanlTree;

	/** The zone name. */
	private String zoneName;

	/** The panel zone select. */
	private TreeNode panelZoneSelect;

	/** The zone trees. */
	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();

	/** The zone general trees. */
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();

	/** The zones list. */
	private List<TreeNode> zonesList = new ArrayList<TreeNode>();

	/** The zone tree list. */
	private List<TreeNode> zoneTreeList = new ArrayList<TreeNode>();

	/** The zone tree addr list. */
	private List<TreeNode> zoneTreeAddrList = new ArrayList<TreeNode>();

	/** The zone name1. */
	private String zoneName1;

	/** The zone addr name. */
	private String zoneAddrName;

	/** The root location tree list. */
	private TreeNode rootLocationTreeList;

	/** The root address location tree list. */
	private TreeNode rootAddressLocationTreeList;

	/** The selected nodes. */
	private TreeNode[] selectedNodes;

	/** The selected general tree nodes. */
	private TreeNode[] selectedGeneralTreeNodes;

	/** The selected loc nodes. */
	private TreeNode[] selectedLocNodes;

	/** The circular exp date. */
	private Date circularExpDate;

	/** The circular type. */
	private String circularType;

	// template Specification variables
	/** The standard template. */
	private String standardTemplate = ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD;

	/** The supporting doc. */
	private String supportingDoc;

	/** The template type. */
	private String templateType;

	/** The specification type. */
	private String specificationType;

	/** The scqt doc. */
	private ScQtDoc scqtDoc;

	/** The ministry name. */
	private String ministryName;

	/** The search item type id. */
	private Long searchItemTypeId;

	/** The search spec type id. */
	private Long searchSpecTypeId;

	/** The search procurement id. */
	private Long searchProcurementId;
	
	/** The search created by. */
	private Long searchCreatedBy;

	/** The doc searchlist. */
	private List<ScQtDoc> docSearchlist;

	/** The check list item. */
	private List<ScQtCheckList> checkListItem;

	/** The zone list. */
	private List<SelectItem> zoneList;

	/** The spec name. */
	private String specName;

	/** The status id. */
	private Long statusId;

	/** The is record status. */
	private boolean isRecordStatus;

	/** The index. */
	private int index = -1;

	// technical Scoring Financial Scoring lists
	/** The technical scoring. */
	private List<ScQtCheckList> technicalScoring;

	/** The financial scoring. */
	private List<ScQtCheckList> financialScoring;

	/** The tech points. */
	private int techPoints;

	/** The fin points. */
	private int finPoints;

	/** The technical spec list. */
	private List<ScQtCheckList> technicalSpecList;

	/** The financial spec list. */
	private List<ScQtCheckList> financialSpecList;

	/** The year list. */
	private List<SelectItem> yearList;

	/** The years display list. */
	private List<SelectItem> yearsDisplayList;

	/** The total suppliers. */
	private int totalSuppliers;

	/** The template spec bean. */
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	/** The delivery loc bean. */
	private List<BriefingZoneBean> deliveryLocBean;

	/** The delivery loc. */
	public List<ScQtDeliveryLoc> deliveryLoc;

	/** The delivery non zone list. */
	public List<ScQtDeliveryLoc> deliveryNonZoneList;

	/** The delete delivery list. */
	public List<ScQtDeliveryLoc> deleteDeliveryList;

	/** The select delivery list. */
	public List<ScQtDeliveryLoc> selectDeliveryList;

	/** The supplier criteria. */
	public ScQtSupplierCriteria supplierCriteria;

	/** The deliveyzone name. */
	public String deliveyzoneName;

	/** The technical checklist scoring. */
	private int technicalChecklistScoring;

	/** The checklist score type. */
	private String checklistScoreType;

	/** The company score type. */
	private String companyScoreType;

	/** The sc qt check list score. */
	private ScQtCheckListScore scQtCheckListScore;

	/** The sc qt contract score. */
	private ScQtCheckListScore scQtContractScore;

	/** The technical checklist action. */
	private List<SelectItem> technicalChecklistAction;

	/** The score varience. */
	private BigDecimal scoreVarience;

	/** The offer price package. */
	private ScQtScoreOfferPrice offerPricePackage;

	/** The Offer price item. */
	private ScQtScoreOfferPrice OfferPriceItem;

	/** The sc qt score weightage. */
	private ScQtScoreWeightage scQtScoreWeightage;

	/** The technical score setting. */
	private ScQtScoreSetting technicalScoreSetting;

	/** The financial score setting. */
	private ScQtScoreSetting financialScoreSetting;

	/** The demo score setting. */
	private ScQtScoreSetting demoScoreSetting;

	/** The recommendation package score. */
	private ScQtScoreSetting recommendationPackageScore;

	/** The recommendation score. */
	private ScQtScoreSetting recommendationScore;

	/** The procuremnt category. */
	private String procuremntCategory;

	/** The fulfilment type. */
	private String fulfilmentType;

	/** The technicalpackage. */
	private BigDecimal technicalpackage;

	/** The financialpackage. */
	private BigDecimal financialpackage;

	/** The recommendation package total. */
	private BigDecimal recommendationPackageTotal;

	/** The recommend technical score. */
	private BigDecimal recommendTechnicalScore;

	/** The recommendfinancial score. */
	private BigDecimal recommendfinancialScore;

	/** The tab active index. */
	private int tabActiveIndex;

	/** The sc qt remarks list. */
	private List<ScQtRemark> scQtRemarksList;

	/** The state id. */
	private Long stateId;

	/** The district id. */
	private Long districtId;

	/** The ministry id. */
	private Long ministryId;

	/** The department id. */
	private Long departmentId;

	/** The ptj id. */
	private Long ptjId;

	/** The state code. */
	private String stateCode;

	/** The district code. */
	private String districtCode;

	/** The ministry list. */
	private List<PmOrgProfile> ministryList;

	/** The department list. */
	private List<PmOrgProfile> departmentList;

	/** The ptjt list. */
	private List<Object[]> ptjtList;

	/** The ptj profile dd list. */
	private List<Object[]> ptjProfileDDList;

	/** The delivery address select. */
	private List<PmAddressType> deliveryAddressSelect;

	/** The delivery zones. */
	private List<TplSpecZoneDtl> deliveryZones;

	/** The tpl spec zone dtl. */
	private TplSpecZoneDtl tplSpecZoneDtl;;

	/** The address zone bean. */
	private List<BriefingZoneBean> addressZoneBean;

	/** The panel item category. */
	private String panelItemCategory;

	/** The fin spec list. */
	private List<ScQtCheckList> finSpecList;

	/** The panel item id. */
	private Long panelItemId;

	/** The active index. */
	private int activeIndex;

	/** The ct panel item. */
	private CtPanelItem ctPanelItem;

	/** The required. */
	private boolean required = Boolean.FALSE;

	/** The ct panel zones. */
	private List<CtPanelZone> ctPanelZones;

	/** The ctpanel supplier. */
	private CtPanelSupplier ctpanelSupplier;

	/** The contractremark. */
	private ScQtRemark contractremark;

	/** The circular ver. */
	private CtCircularVer circularVer;

	/** The subject. */
	private String subject;

	/** The message. */
	private String message;

	/** The chair person pin num. */
	private String chairPersonPinNum;

	// Added By Sagar.. for Delete funcationality..
	/** The selected tree node. */
	private TreeNode selectedTreeNode;

	/** The ct search bean. */
	private CtCircularSearchBean ctSearchBean = new CtCircularSearchBean();

	/** The evaluation type item. */
	private List<SelectItem> evaluationTypeItem;

	/** The supplier selected tree node. */
	private TreeNode supplierSelectedTreeNode;

	/** The personnel. */
	private SmPersonnel personnel;

	/** The supplier branch. */
	private String supplierBranch;

	/** The supplier address. */
	private SmAddress supplierAddress;

	/** The supplier contact. */
	private String supplierContact;

	/** The supplier email. */
	private String supplierEmail;

	/** The ct circular ver. */
	private CtCircularVer ctCircularVer;

	/** The finspec checklist. */
	private List<ScQtCheckList> finspecChecklist;

	/** The button out come. */
	private String buttonOutCome;

	/** The all state code. */
	private PTJLocalityCode allStateCode;

	/** The is panel change. */
	private boolean isPanelChange;

	/** The new item category. */
	private String newItemCategory;

	/** The bankstmnt tomonth. */
	private String bankstmntTomonth;

	/** The temp index. */
	private int tempIndex;

	/** The ctzones. */
	private boolean ctzones = Boolean.FALSE;

	/** The select panel item. */
	private boolean selectPanelItem = Boolean.FALSE;

	/** The spec status id. */
	private Long specStatusId;

	/** The spec select stats. */
	private boolean specSelectStats = Boolean.FALSE;

	/** The selected check list. */
	private int selectedCheckList;

	/** The spec type. */
	private String specType;

	/** The to be remove list. */
	private List<ScQtCheckListScore> toBeRemoveList;

	/** The other score selected. */
	private List<ScQtCheckListScore> otherScoreSelected;

	/** The criteria type. */
	private String criteriaType;

	/** The other criteria type. */
	private String otherCriteriaType;

	/** The check list title. */
	private String checkListTitle;

	/** The Local level code. */
	private String LocalLevelCode;

	/** The delivery loc remove list. */
	private List<ScQtDeliveryLoc> deliveryLocRemoveList;

	/** The checklist remove list. */
	private List<ScQtCheckList> checklistRemoveList;
	// Added by vidyasagarReddy.. for making readonly based on userrole.
	/** The read only. */
	private boolean readOnly;

	/** The technical table row. */
	private int technicalTableRow;

	/** The financial table row. */
	private int financialTableRow;

	/** The total suppliers spec loc. */
	private List<TplSpecLoc> totalSuppliersSpecLoc;

	/** The state list. */
	private List<PmState> stateList;

	/** The district list. */
	private List<PmDistrict> districtList;

	/** The division list. */
	private List<PmDivision> divisionList;

	/** The Score type list. */
	private List<SelectItem> ScoreTypeList;

	/** The is tech standara checklist. */
	private boolean isTechStandaraChecklist;

	/** The is fin standara checklist. */
	private boolean isFinStandaraChecklist;

	/** The local out come. */
	private String localOutCome;

	/** The is zone location. */
	private boolean isZoneLocation;

	/** The specification remarks. */
	private List<ScQtSpecificationRemark> specificationRemarks;

	/** The supplier state code. */
	private List<String> supplierStateCode;

	/** The supplier district code. */
	private List<String> supplierDistrictCode;

	/** The supplier division code. */
	private List<String> supplierDivisionCode;

	/** The division id. */
	private Long divisionId;

	/** The division code. */
	private String divisionCode;

	/** The select ct panel item. */
	private CtPanelItem selectCtPanelItem;

	/** The spec complete. */
	private boolean specComplete;

	/** The beans. */
	private List<ZonesBean> beans = new ArrayList<ZonesBean>();

	/** The existing supplier location list. */
	private List<ScQtLocCriteria> existingSupplierLocationList;

	/** The is supplier validation. */
	private boolean isSupplierValidation;

	/** The technical spec count. */
	private int technicalSpecCount;

	/** The financial spec count. */
	private int financialSpecCount;

	/** The bankyears list. */
	private List<SelectItem> bankyearsList;

	/** The restricted supplier list. */
	private List<ScQtSupplier> restrictedSupplierList;

	/** The sm supplier. */
	private SmSupplier smSupplier;

	/** The sm mof account. */
	private SmMofAccount smMofAccount;

	/** The new template spec. */
	private boolean newTemplateSpec;

	/** The delete panel treenode. */
	private TreeNode deletePanelTreenode;

	/** The supplier location list. */
	private List<ScQtLocCriteria> supplierLocationList;

	/** The financial checklist action. */
	private List<SelectItem> financialChecklistAction;

	/** The templatespec Newable. */
	private boolean specNewable = Boolean.TRUE;

	/** For Create Specification Screen. */
	private QtnTenderSearchBean qtnTendertemplateSearchBean;

	/** The temp zone name. */
	private String tempZoneName;

	private boolean isCheckAllList = Boolean.FALSE;

	/** The financial Item score setting. */
	private ScQtScoreSetting finacialItemScoreSetting;

	private BigDecimal financialItemScore;

	private BigDecimal totalScore = new BigDecimal(0);

	private String locationLevel;

	/** The search supplier data model. */
	private IPagingDataModel<PmAddressType> searchDeliveryAddressDataModel;

	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;

	private Long ptjGroupId;
	private int checkListYesIndex;
	private boolean technicalOtherChecklistSubmit;
	private boolean financialOtherChecklistSubmit;

	private List<ScQtSchedule> toBeRemoveSchedules;
	private List<ScQtProgressivePayment> toBeRemoveProgPayments;
	private boolean zoneSpecUpdate;
	private boolean zonesWarnMessageUpdate;
	private List<ScQtCategory> tobeRemoveCategoryCodes;
	private boolean isSpecItemWeightageRequired;
	private int ministryRemarksSize;
	private boolean isTechnicalUserScoringSchema;
	private boolean isFinancialUserScoringSchema;
	
	/** The selected flag. */
	private boolean selectedFlag;

	

	/**
	 * Checks if is spec complete.
	 * 
	 * @return true, if is spec complete
	 */
	public boolean isSpecComplete() {
		return specComplete;
	}

	/**
	 * Sets the spec complete.
	 * 
	 * @param specComplete
	 *            the new spec complete
	 */
	public void setSpecComplete(boolean specComplete) {
		this.specComplete = specComplete;
	}

	/**
	 * Gets the beans.
	 * 
	 * @return the beans
	 */
	public List<ZonesBean> getBeans() {
		return beans;
	}

	/**
	 * Sets the beans.
	 * 
	 * @param beans
	 *            the new beans
	 */
	public void setBeans(List<ZonesBean> beans) {
		this.beans = beans;
	}

	/**
	 * Gets the select ct panel item.
	 * 
	 * @return the select ct panel item
	 */
	public CtPanelItem getSelectCtPanelItem() {
		return selectCtPanelItem;
	}

	/**
	 * Sets the select ct panel item.
	 * 
	 * @param selectCtPanelItem
	 *            the new select ct panel item
	 */
	public void setSelectCtPanelItem(CtPanelItem selectCtPanelItem) {
		this.selectCtPanelItem = selectCtPanelItem;
	}

	/**
	 * Gets the division id.
	 * 
	 * @return the division id
	 */
	public Long getDivisionId() {
		return divisionId;
	}

	/**
	 * Sets the division id.
	 * 
	 * @param divisionId
	 *            the new division id
	 */
	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	/**
	 * Gets the division code.
	 * 
	 * @return the division code
	 */
	public String getDivisionCode() {
		return divisionCode;
	}

	/**
	 * Sets the division code.
	 * 
	 * @param divisionCode
	 *            the new division code
	 */
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	/**
	 * Gets the supplier state code.
	 * 
	 * @return the supplier state code
	 */
	public List<String> getSupplierStateCode() {
		return supplierStateCode;
	}

	/**
	 * Sets the supplier state code.
	 * 
	 * @param supplierStateCode
	 *            the new supplier state code
	 */
	public void setSupplierStateCode(List<String> supplierStateCode) {
		this.supplierStateCode = supplierStateCode;
	}

	/**
	 * Gets the supplier district code.
	 * 
	 * @return the supplier district code
	 */
	public List<String> getSupplierDistrictCode() {
		return supplierDistrictCode;
	}

	/**
	 * Sets the supplier district code.
	 * 
	 * @param supplierDistrictCode
	 *            the new supplier district code
	 */
	public void setSupplierDistrictCode(List<String> supplierDistrictCode) {
		this.supplierDistrictCode = supplierDistrictCode;
	}

	/**
	 * Gets the supplier division code.
	 * 
	 * @return the supplier division code
	 */
	public List<String> getSupplierDivisionCode() {
		return supplierDivisionCode;
	}

	/**
	 * Sets the supplier division code.
	 * 
	 * @param supplierDivisionCode
	 *            the new supplier division code
	 */
	public void setSupplierDivisionCode(List<String> supplierDivisionCode) {
		this.supplierDivisionCode = supplierDivisionCode;
	}

	/**
	 * Gets the specification remarks.
	 * 
	 * @return the specification remarks
	 */
	public List<ScQtSpecificationRemark> getSpecificationRemarks() {
		return specificationRemarks;
	}

	/**
	 * Sets the specification remarks.
	 * 
	 * @param specificationRemarks
	 *            the new specification remarks
	 */
	public void setSpecificationRemarks(
			List<ScQtSpecificationRemark> specificationRemarks) {
		this.specificationRemarks = specificationRemarks;
	}

	/**
	 * Checks if is zone location.
	 * 
	 * @return true, if is zone location
	 */
	public boolean isZoneLocation() {
		return isZoneLocation;
	}

	/**
	 * Sets the zone location.
	 * 
	 * @param isZoneLocation
	 *            the new zone location
	 */
	public void setZoneLocation(boolean isZoneLocation) {
		this.isZoneLocation = isZoneLocation;
	}

	/**
	 * Gets the local out come.
	 * 
	 * @return the local out come
	 */
	public String getLocalOutCome() {
		return localOutCome;
	}

	/**
	 * Sets the local out come.
	 * 
	 * @param localOutCome
	 *            the new local out come
	 */
	public void setLocalOutCome(String localOutCome) {
		this.localOutCome = localOutCome;
	}

	/**
	 * Checks if is tech standara checklist.
	 * 
	 * @return true, if is tech standara checklist
	 */
	public boolean isTechStandaraChecklist() {
		return isTechStandaraChecklist;
	}

	/**
	 * Sets the tech standara checklist.
	 * 
	 * @param isTechStandaraChecklist
	 *            the new tech standara checklist
	 */
	public void setTechStandaraChecklist(boolean isTechStandaraChecklist) {
		this.isTechStandaraChecklist = isTechStandaraChecklist;
	}

	/**
	 * Checks if is fin standara checklist.
	 * 
	 * @return true, if is fin standara checklist
	 */
	public boolean isFinStandaraChecklist() {
		return isFinStandaraChecklist;
	}

	/**
	 * Sets the fin standara checklist.
	 * 
	 * @param isFinStandaraChecklist
	 *            the new fin standara checklist
	 */
	public void setFinStandaraChecklist(boolean isFinStandaraChecklist) {
		this.isFinStandaraChecklist = isFinStandaraChecklist;
	}

	/**
	 * Gets the score type list.
	 * 
	 * @return the score type list
	 */
	public List<SelectItem> getScoreTypeList() {
		return ScoreTypeList;
	}

	/**
	 * Sets the score type list.
	 * 
	 * @param scoreTypeList
	 *            the new score type list
	 */
	public void setScoreTypeList(List<SelectItem> scoreTypeList) {
		ScoreTypeList = scoreTypeList;
	}

	/**
	 * Gets the division list.
	 * 
	 * @return the division list
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}

	/**
	 * Sets the division list.
	 * 
	 * @param divisionList
	 *            the new division list
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * Gets the total suppliers spec loc.
	 * 
	 * @return the total suppliers spec loc
	 */
	public List<TplSpecLoc> getTotalSuppliersSpecLoc() {
		return totalSuppliersSpecLoc;
	}

	/**
	 * Sets the total suppliers spec loc.
	 * 
	 * @param totalSuppliersSpecLoc
	 *            the new total suppliers spec loc
	 */
	public void setTotalSuppliersSpecLoc(List<TplSpecLoc> totalSuppliersSpecLoc) {
		this.totalSuppliersSpecLoc = totalSuppliersSpecLoc;
	}

	/**
	 * Gets the technical table row.
	 * 
	 * @return the technical table row
	 */
	public int getTechnicalTableRow() {
		return technicalTableRow;
	}

	/**
	 * Sets the technical table row.
	 * 
	 * @param technicalTableRow
	 *            the new technical table row
	 */
	public void setTechnicalTableRow(int technicalTableRow) {
		this.technicalTableRow = technicalTableRow;
	}

	/**
	 * Gets the financial table row.
	 * 
	 * @return the financial table row
	 */
	public int getFinancialTableRow() {
		return financialTableRow;
	}

	/**
	 * Sets the financial table row.
	 * 
	 * @param financialTableRow
	 *            the new financial table row
	 */
	public void setFinancialTableRow(int financialTableRow) {
		this.financialTableRow = financialTableRow;
	}

	/**
	 * Gets the delivery loc remove list.
	 * 
	 * @return the delivery loc remove list
	 */
	public List<ScQtDeliveryLoc> getDeliveryLocRemoveList() {
		return deliveryLocRemoveList;
	}

	/**
	 * Sets the delivery loc remove list.
	 * 
	 * @param deliveryLocRemoveList
	 *            the new delivery loc remove list
	 */
	public void setDeliveryLocRemoveList(
			List<ScQtDeliveryLoc> deliveryLocRemoveList) {
		this.deliveryLocRemoveList = deliveryLocRemoveList;
	}

	/**
	 * Gets the checklist remove list.
	 * 
	 * @return the checklist remove list
	 */
	public List<ScQtCheckList> getChecklistRemoveList() {
		return checklistRemoveList;
	}

	/**
	 * Sets the checklist remove list.
	 * 
	 * @param checklistRemoveList
	 *            the new checklist remove list
	 */
	public void setChecklistRemoveList(List<ScQtCheckList> checklistRemoveList) {
		this.checklistRemoveList = checklistRemoveList;
	}

	/**
	 * Instantiates a new specification managed bean.
	 */
	public SpecificationManagedBean() {
		qtnTendertemplateSearchBean = new QtnTenderSearchBean();
	}

	/**
	 * Gets the sc qt remarks.
	 * 
	 * @return the sc qt remarks
	 */
	public ScQtRemark getScQtRemarks() {
		return scQtRemarks;
	}

	/**
	 * Sets the sc qt remarks.
	 * 
	 * @param scQtRemarks
	 *            the new sc qt remarks
	 */
	public void setScQtRemarks(ScQtRemark scQtRemarks) {
		this.scQtRemarks = scQtRemarks;
	}

	/**
	 * Gets the sc qt checklist.
	 * 
	 * @return the sc qt checklist
	 */
	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	/**
	 * Sets the sc qt checklist.
	 * 
	 * @param scQtChecklist
	 *            the new sc qt checklist
	 */
	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
	}

	/**
	 * Gets the sc qt check st2tech proposal.
	 * 
	 * @return the sc qt check st2tech proposal
	 */
	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	/**
	 * Sets the sc qt check st2tech proposal.
	 * 
	 * @param scQtCheckSt2techProposal
	 *            the new sc qt check st2tech proposal
	 */
	public void setScQtCheckSt2techProposal(
			List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}

	/**
	 * Gets the sc qt check st2finance proposal list.
	 * 
	 * @return the sc qt check st2finance proposal list
	 */
	public List<ScQtCheckList> getScQtCheckSt2financeProposalList() {
		return scQtCheckSt2financeProposalList;
	}

	/**
	 * Sets the sc qt check st2finance proposal list.
	 * 
	 * @param scQtCheckSt2financeProposalList
	 *            the new sc qt check st2finance proposal list
	 */
	public void setScQtCheckSt2financeProposalList(
			List<ScQtCheckList> scQtCheckSt2financeProposalList) {
		this.scQtCheckSt2financeProposalList = scQtCheckSt2financeProposalList;
	}

	/**
	 * Gets the sc remark.
	 * 
	 * @return the sc remark
	 */
	public ScRemark getScRemark() {
		return scRemark;
	}

	/**
	 * Sets the sc remark.
	 * 
	 * @param scRemark
	 *            the new sc remark
	 */
	public void setScRemark(ScRemark scRemark) {
		this.scRemark = scRemark;
	}

	/**
	 * Checks if is sample req.
	 * 
	 * @return true, if is sample req
	 */
	public boolean isSampleReq() {
		return isSampleReq;
	}

	/**
	 * Sets the sample req.
	 * 
	 * @param isSampleReq
	 *            the new sample req
	 */
	public void setSampleReq(boolean isSampleReq) {
		this.isSampleReq = isSampleReq;
	}

	/**
	 * Checks if is spec status.
	 * 
	 * @return true, if is spec status
	 */
	public boolean isSpecStatus() {
		return isSpecStatus;
	}

	/**
	 * Sets the spec status.
	 * 
	 * @param isSpecStatus
	 *            the new spec status
	 */
	public void setSpecStatus(boolean isSpecStatus) {
		this.isSpecStatus = isSpecStatus;
	}

	/**
	 * Gets the procurement id.
	 * 
	 * @return the procurement id
	 */
	public Long getProcurementId() {
		return procurementId;
	}

	/**
	 * Sets the procurement id.
	 * 
	 * @param procurementId
	 *            the new procurement id
	 */
	public void setProcurementId(Long procurementId) {
		this.procurementId = procurementId;
	}

	/**
	 * Gets the evaluation type.
	 * 
	 * @return the evaluation type
	 */
	public String getEvaluationType() {
		return evaluationType;
	}

	/**
	 * Sets the evaluation type.
	 * 
	 * @param evaluationType
	 *            the new evaluation type
	 */
	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	/**
	 * Gets the s2 tech select.
	 * 
	 * @return the s2 tech select
	 */
	public List<ScQtCheckList> getS2TechSelect() {
		return s2TechSelect;
	}

	/**
	 * Sets the s2 tech select.
	 * 
	 * @param s2TechSelect
	 *            the new s2 tech select
	 */
	public void setS2TechSelect(List<ScQtCheckList> s2TechSelect) {
		this.s2TechSelect = s2TechSelect;
	}

	/**
	 * Gets the s2 fin select.
	 * 
	 * @return the s2 fin select
	 */
	public List<ScQtCheckList> getS2FinSelect() {
		return s2FinSelect;
	}

	/**
	 * Sets the s2 fin select.
	 * 
	 * @param s2FinSelect
	 *            the new s2 fin select
	 */
	public void setS2FinSelect(List<ScQtCheckList> s2FinSelect) {
		this.s2FinSelect = s2FinSelect;
	}

	/**
	 * Gets the schedule type.
	 * 
	 * @return the schedule type
	 */
	public String getScheduleType() {
		return scheduleType;
	}

	/**
	 * Sets the schedule type.
	 * 
	 * @param scheduleType
	 *            the new schedule type
	 */
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	/**
	 * Gets the business owner.
	 * 
	 * @return the business owner
	 */
	public String getBusinessOwner() {
		return businessOwner;
	}

	/**
	 * Sets the business owner.
	 * 
	 * @param businessOwner
	 *            the new business owner
	 */
	public void setBusinessOwner(String businessOwner) {
		this.businessOwner = businessOwner;
	}

	/**
	 * Gets the from month.
	 * 
	 * @return the from month
	 */
	public String getFromMonth() {
		return fromMonth;
	}

	/**
	 * Sets the from month.
	 * 
	 * @param fromMonth
	 *            the new from month
	 */
	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	/**
	 * Gets the to month.
	 * 
	 * @return the to month
	 */
	public String getToMonth() {
		return toMonth;
	}

	/**
	 * Sets the to month.
	 * 
	 * @param toMonth
	 *            the new to month
	 */
	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

	/**
	 * Gets the check list score.
	 * 
	 * @return the check list score
	 */
	public List<ScQtCheckListScore> getCheckListScore() {
		return checkListScore;
	}

	/**
	 * Sets the check list score.
	 * 
	 * @param checkListScore
	 *            the new check list score
	 */
	public void setCheckListScore(List<ScQtCheckListScore> checkListScore) {
		this.checkListScore = checkListScore;
	}

	/**
	 * Gets the scoring for years.
	 * 
	 * @return the scoring for years
	 */
	public List<ScQtCheckListScore> getScoringForYears() {
		return scoringForYears;
	}

	/**
	 * Sets the scoring for years.
	 * 
	 * @param scoringForYears
	 *            the new scoring for years
	 */
	public void setScoringForYears(List<ScQtCheckListScore> scoringForYears) {
		this.scoringForYears = scoringForYears;
	}

	/**
	 * Gets the serialversionuid.
	 * 
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the log.
	 * 
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * Gets the total frequency.
	 * 
	 * @return the total frequency
	 */
	public int getTotalFrequency() {
		return totalFrequency;
	}

	/**
	 * Sets the total frequency.
	 * 
	 * @param totalFrequency
	 *            the new total frequency
	 */
	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	/**
	 * Gets the cir cular no.
	 * 
	 * @return the cir cular no
	 */
	public String getCirCularNo() {
		return cirCularNo;
	}

	/**
	 * Sets the cir cular no.
	 * 
	 * @param cirCularNo
	 *            the new cir cular no
	 */
	public void setCirCularNo(String cirCularNo) {
		this.cirCularNo = cirCularNo;
	}

	/**
	 * Gets the circular name.
	 * 
	 * @return the circular name
	 */
	public String getCircularName() {
		return circularName;
	}

	/**
	 * Sets the circular name.
	 * 
	 * @param circularName
	 *            the new circular name
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	/**
	 * Gets the selectpanel item.
	 * 
	 * @return the selectpanel item
	 */
	public List<CtPanelItem> getSelectpanelItem() {
		return selectpanelItem;
	}

	/**
	 * Sets the selectpanel item.
	 * 
	 * @param selectpanelItem
	 *            the new selectpanel item
	 */
	public void setSelectpanelItem(List<CtPanelItem> selectpanelItem) {
		this.selectpanelItem = selectpanelItem;
	}

	/**
	 * Gets the technical weightage score.
	 * 
	 * @return the technical weightage score
	 */
	public BigDecimal getTechnicalWeightageScore() {
		return technicalWeightageScore;
	}

	/**
	 * Sets the technical weightage score.
	 * 
	 * @param technicalWeightageScore
	 *            the new technical weightage score
	 */
	public void setTechnicalWeightageScore(BigDecimal technicalWeightageScore) {
		this.technicalWeightageScore = technicalWeightageScore;
	}

	/**
	 * Gets the total amount.
	 * 
	 * @return the total amount
	 */
	public int getTotalAmount() {
		return totalAmount;
	}

	/**
	 * Sets the total amount.
	 * 
	 * @param totalAmount
	 *            the new total amount
	 */
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * Gets the total amount avg.
	 * 
	 * @return the total amount avg
	 */
	public BigDecimal getTotalAmountAvg() {
		return totalAmountAvg;
	}

	/**
	 * Sets the total amount avg.
	 * 
	 * @param totalAmountAvg
	 *            the new total amount avg
	 */
	public void setTotalAmountAvg(BigDecimal totalAmountAvg) {
		this.totalAmountAvg = totalAmountAvg;
	}

	/**
	 * Gets the financial weightage score.
	 * 
	 * @return the financial weightage score
	 */
	public BigDecimal getFinancialWeightageScore() {
		return financialWeightageScore;
	}

	/**
	 * Sets the financial weightage score.
	 * 
	 * @param financialWeightageScore
	 *            the new financial weightage score
	 */
	public void setFinancialWeightageScore(BigDecimal financialWeightageScore) {
		this.financialWeightageScore = financialWeightageScore;
	}

	/**
	 * Checks if is panel.
	 * 
	 * @return true, if is panel
	 */
	public boolean isPanel() {
		return isPanel;
	}

	/**
	 * Sets the panel.
	 * 
	 * @param isPanel
	 *            the new panel
	 */
	public void setPanel(boolean isPanel) {
		this.isPanel = isPanel;
	}

	/**
	 * Gets the item name.
	 * 
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets the item name.
	 * 
	 * @param itemName
	 *            the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Gets the supp criteriaopen list.
	 * 
	 * @return the supp criteriaopen list
	 */
	public List<ScQtCategory> getSuppCriteriaopenList() {
		return suppCriteriaopenList;
	}

	/**
	 * Sets the supp criteriaopen list.
	 * 
	 * @param suppCriteriaopenList
	 *            the new supp criteriaopen list
	 */
	public void setSuppCriteriaopenList(List<ScQtCategory> suppCriteriaopenList) {
		this.suppCriteriaopenList = suppCriteriaopenList;
	}

	/**
	 * Gets the root location tree.
	 * 
	 * @return the root location tree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * Sets the root location tree.
	 * 
	 * @param rootLocationTree
	 *            the new root location tree
	 */
	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	/**
	 * Gets the zone name.
	 * 
	 * @return the zone name
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * Sets the zone name.
	 * 
	 * @param zoneName
	 *            the new zone name
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * Gets the zone trees.
	 * 
	 * @return the zone trees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	/**
	 * Sets the zone trees.
	 * 
	 * @param zoneTrees
	 *            the new zone trees
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	/**
	 * Gets the zone tree list.
	 * 
	 * @return the zone tree list
	 */
	public List<TreeNode> getZoneTreeList() {
		return zoneTreeList;
	}

	/**
	 * Sets the zone tree list.
	 * 
	 * @param zoneTreeList
	 *            the new zone tree list
	 */
	public void setZoneTreeList(List<TreeNode> zoneTreeList) {
		this.zoneTreeList = zoneTreeList;
	}

	/**
	 * Gets the zone tree addr list.
	 * 
	 * @return the zone tree addr list
	 */
	public List<TreeNode> getZoneTreeAddrList() {
		return zoneTreeAddrList;
	}

	/**
	 * Sets the zone tree addr list.
	 * 
	 * @param zoneTreeAddrList
	 *            the new zone tree addr list
	 */
	public void setZoneTreeAddrList(List<TreeNode> zoneTreeAddrList) {
		this.zoneTreeAddrList = zoneTreeAddrList;
	}

	/**
	 * Gets the zone name1.
	 * 
	 * @return the zone name1
	 */
	public String getZoneName1() {
		return zoneName1;
	}

	/**
	 * Sets the zone name1.
	 * 
	 * @param zoneName1
	 *            the new zone name1
	 */
	public void setZoneName1(String zoneName1) {
		this.zoneName1 = zoneName1;
	}

	/**
	 * Gets the zone addr name.
	 * 
	 * @return the zone addr name
	 */
	public String getZoneAddrName() {
		return zoneAddrName;
	}

	/**
	 * Sets the zone addr name.
	 * 
	 * @param zoneAddrName
	 *            the new zone addr name
	 */
	public void setZoneAddrName(String zoneAddrName) {
		this.zoneAddrName = zoneAddrName;
	}

	/**
	 * Gets the root location tree list.
	 * 
	 * @return the root location tree list
	 */
	public TreeNode getRootLocationTreeList() {
		return rootLocationTreeList;
	}

	/**
	 * Sets the root location tree list.
	 * 
	 * @param rootLocationTreeList
	 *            the new root location tree list
	 */
	public void setRootLocationTreeList(TreeNode rootLocationTreeList) {
		this.rootLocationTreeList = rootLocationTreeList;
	}

	/**
	 * Gets the root address location tree list.
	 * 
	 * @return the root address location tree list
	 */
	public TreeNode getRootAddressLocationTreeList() {
		return rootAddressLocationTreeList;
	}

	/**
	 * Sets the root address location tree list.
	 * 
	 * @param rootAddressLocationTreeList
	 *            the new root address location tree list
	 */
	public void setRootAddressLocationTreeList(
			TreeNode rootAddressLocationTreeList) {
		this.rootAddressLocationTreeList = rootAddressLocationTreeList;
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Sets the selected nodes.
	 * 
	 * @param selectedNodes
	 *            the new selected nodes
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	/**
	 * Gets the selected loc nodes.
	 * 
	 * @return the selected loc nodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * Sets the selected loc nodes.
	 * 
	 * @param selectedLocNodes
	 *            the new selected loc nodes
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * Gets the standard template.
	 * 
	 * @return the standard template
	 */
	public String getStandardTemplate() {
		return standardTemplate;
	}

	/**
	 * Sets the standard template.
	 * 
	 * @param standardTemplate
	 *            the new standard template
	 */
	public void setStandardTemplate(String standardTemplate) {
		this.standardTemplate = standardTemplate;
	}

	/**
	 * Gets the supporting doc.
	 * 
	 * @return the supporting doc
	 */
	public String getSupportingDoc() {
		return supportingDoc;
	}

	/**
	 * Sets the supporting doc.
	 * 
	 * @param supportingDoc
	 *            the new supporting doc
	 */
	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
	}

	/**
	 * Gets the template type.
	 * 
	 * @return the template type
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * Sets the template type.
	 * 
	 * @param templateType
	 *            the new template type
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * Gets the specification type.
	 * 
	 * @return the specification type
	 */
	public String getSpecificationType() {
		return specificationType;
	}

	/**
	 * Sets the specification type.
	 * 
	 * @param specificationType
	 *            the new specification type
	 */
	public void setSpecificationType(String specificationType) {
		this.specificationType = specificationType;
	}

	/**
	 * Gets the scqt doc.
	 * 
	 * @return the scqt doc
	 */
	public ScQtDoc getScqtDoc() {
		return scqtDoc;
	}

	/**
	 * Sets the scqt doc.
	 * 
	 * @param scqtDoc
	 *            the new scqt doc
	 */
	public void setScqtDoc(ScQtDoc scqtDoc) {
		this.scqtDoc = scqtDoc;
	}

	/**
	 * Gets the ministry name.
	 * 
	 * @return the ministry name
	 */
	public String getMinistryName() {
		return ministryName;
	}

	/**
	 * Sets the ministry name.
	 * 
	 * @param ministryName
	 *            the new ministry name
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	/**
	 * Gets the search item type id.
	 * 
	 * @return the search item type id
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	/**
	 * Sets the search item type id.
	 * 
	 * @param searchItemTypeId
	 *            the new search item type id
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	/**
	 * Gets the search spec type id.
	 * 
	 * @return the search spec type id
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	/**
	 * Sets the search spec type id.
	 * 
	 * @param searchSpecTypeId
	 *            the new search spec type id
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	/**
	 * Gets the search procurement id.
	 * 
	 * @return the search procurement id
	 */
	public Long getSearchProcurementId() {
		return searchProcurementId;
	}

	/**
	 * Sets the search procurement id.
	 * 
	 * @param searchProcurementId
	 *            the new search procurement id
	 */
	public void setSearchProcurementId(Long searchProcurementId) {
		this.searchProcurementId = searchProcurementId;
	}

	/**
	 * @return the searchCreatedBy
	 */
	public Long getSearchCreatedBy() {
		return searchCreatedBy;
	}

	/**
	 * @param searchCreatedBy the searchCreatedBy to set
	 */
	public void setSearchCreatedBy(Long searchCreatedBy) {
		this.searchCreatedBy = searchCreatedBy;
	}

	/**
	 * Gets the doc searchlist.
	 * 
	 * @return the doc searchlist
	 */
	public List<ScQtDoc> getDocSearchlist() {
		return docSearchlist;
	}

	/**
	 * Sets the doc searchlist.
	 * 
	 * @param docSearchlist
	 *            the new doc searchlist
	 */
	public void setDocSearchlist(List<ScQtDoc> docSearchlist) {
		this.docSearchlist = docSearchlist;
	}

	/**
	 * Gets the check list item.
	 * 
	 * @return the check list item
	 */
	public List<ScQtCheckList> getCheckListItem() {
		return checkListItem;
	}

	/**
	 * Sets the check list item.
	 * 
	 * @param checkListItem
	 *            the new check list item
	 */
	public void setCheckListItem(List<ScQtCheckList> checkListItem) {
		this.checkListItem = checkListItem;
	}

	/**
	 * Gets the zone list.
	 * 
	 * @return the zone list
	 */
	public List<SelectItem> getZoneList() {
		return zoneList;
	}

	/**
	 * Sets the zone list.
	 * 
	 * @param zoneList
	 *            the new zone list
	 */
	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

	/**
	 * Gets the spec name.
	 * 
	 * @return the spec name
	 */
	public String getSpecName() {
		return specName;
	}

	/**
	 * Sets the spec name.
	 * 
	 * @param specName
	 *            the new spec name
	 */
	public void setSpecName(String specName) {
		this.specName = specName;
	}

	/**
	 * Gets the status id.
	 * 
	 * @return the status id
	 */
	public Long getStatusId() {
		return statusId;
	}

	/**
	 * Sets the status id.
	 * 
	 * @param statusId
	 *            the new status id
	 */
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	/**
	 * Checks if is record status.
	 * 
	 * @return true, if is record status
	 */
	public boolean isRecordStatus() {
		return isRecordStatus;
	}

	/**
	 * Sets the record status.
	 * 
	 * @param isRecordStatus
	 *            the new record status
	 */
	public void setRecordStatus(boolean isRecordStatus) {
		this.isRecordStatus = isRecordStatus;
	}

	/**
	 * Gets the template spec bean.
	 * 
	 * @return the template spec bean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * Sets the template spec bean.
	 * 
	 * @param templateSpecBean
	 *            the new template spec bean
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * Gets the circular type.
	 * 
	 * @return the circular type
	 */
	public String getCircularType() {
		return circularType;
	}

	/**
	 * Sets the circular type.
	 * 
	 * @param circularType
	 *            the new circular type
	 */
	public void setCircularType(String circularType) {
		this.circularType = circularType;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Gets the total progressive payment.
	 * 
	 * @return the total progressive payment
	 */
	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	/**
	 * Sets the total progressive payment.
	 * 
	 * @param totalProgressivePayment
	 *            the new total progressive payment
	 */
	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}

	/**
	 * Gets the zones list.
	 * 
	 * @return the zones list
	 */
	public List<TreeNode> getZonesList() {
		return zonesList;
	}

	/**
	 * Sets the zones list.
	 * 
	 * @param zonesList
	 *            the new zones list
	 */
	public void setZonesList(List<TreeNode> zonesList) {
		this.zonesList = zonesList;
	}

	/**
	 * Gets the technical scoring.
	 * 
	 * @return the technical scoring
	 */
	public List<ScQtCheckList> getTechnicalScoring() {
		return technicalScoring;
	}

	/**
	 * Sets the technical scoring.
	 * 
	 * @param technicalScoring
	 *            the new technical scoring
	 */
	public void setTechnicalScoring(List<ScQtCheckList> technicalScoring) {
		this.technicalScoring = technicalScoring;
	}

	/**
	 * Gets the financial scoring.
	 * 
	 * @return the financial scoring
	 */
	public List<ScQtCheckList> getFinancialScoring() {
		return financialScoring;
	}

	/**
	 * Sets the financial scoring.
	 * 
	 * @param financialScoring
	 *            the new financial scoring
	 */
	public void setFinancialScoring(List<ScQtCheckList> financialScoring) {
		this.financialScoring = financialScoring;
	}

	/**
	 * Gets the ct zone supplier list.
	 * 
	 * @return the ct zone supplier list
	 */
	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	/**
	 * Sets the ct zone supplier list.
	 * 
	 * @param ctZoneSupplierList
	 *            the new ct zone supplier list
	 */
	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

	/**
	 * Gets the total suppliers.
	 * 
	 * @return the total suppliers
	 */
	public int getTotalSuppliers() {
		return totalSuppliers;
	}

	/**
	 * Sets the total suppliers.
	 * 
	 * @param totalSuppliers
	 *            the new total suppliers
	 */
	public void setTotalSuppliers(int totalSuppliers) {
		this.totalSuppliers = totalSuppliers;
	}

	/**
	 * Gets the ct zone trees.
	 * 
	 * @return the ct zone trees
	 */
	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	/**
	 * Sets the ct zone trees.
	 * 
	 * @param ctZoneTrees
	 *            the new ct zone trees
	 */
	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}

	/**
	 * Gets the ct zone tree selected nodes.
	 * 
	 * @return the ct zone tree selected nodes
	 */
	public TreeNode[] getCtZoneTreeSelectedNodes() {
		return ctZoneTreeSelectedNodes;
	}

	/**
	 * Sets the ct zone tree selected nodes.
	 * 
	 * @param ctZoneTreeSelectedNodes
	 *            the new ct zone tree selected nodes
	 */
	public void setCtZoneTreeSelectedNodes(TreeNode[] ctZoneTreeSelectedNodes) {
		this.ctZoneTreeSelectedNodes = ctZoneTreeSelectedNodes;
	}

	/**
	 * Gets the year list.
	 * 
	 * @return the year list
	 */
	public List<SelectItem> getYearList() {
		return yearList;
	}

	/**
	 * Sets the year list.
	 * 
	 * @param yearList
	 *            the new year list
	 */
	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	/**
	 * Gets the sc qt.
	 * 
	 * @return the sc qt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * Sets the sc qt.
	 * 
	 * @param scQt
	 *            the new sc qt
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * Gets the tech points.
	 * 
	 * @return the tech points
	 */
	public int getTechPoints() {
		return techPoints;
	}

	/**
	 * Sets the tech points.
	 * 
	 * @param techPoints
	 *            the new tech points
	 */
	public void setTechPoints(int techPoints) {
		this.techPoints = techPoints;
	}

	/**
	 * Gets the fin points.
	 * 
	 * @return the fin points
	 */
	public int getFinPoints() {
		return finPoints;
	}

	/**
	 * Sets the fin points.
	 * 
	 * @param finPoints
	 *            the new fin points
	 */
	public void setFinPoints(int finPoints) {
		this.finPoints = finPoints;
	}

	/**
	 * Gets the technical spec list.
	 * 
	 * @return the technical spec list
	 */
	public List<ScQtCheckList> getTechnicalSpecList() {
		return technicalSpecList;
	}

	/**
	 * Sets the technical spec list.
	 * 
	 * @param technicalSpecList
	 *            the new technical spec list
	 */
	public void setTechnicalSpecList(List<ScQtCheckList> technicalSpecList) {
		this.technicalSpecList = technicalSpecList;
	}

	/**
	 * Gets the financial spec list.
	 * 
	 * @return the financial spec list
	 */
	public List<ScQtCheckList> getFinancialSpecList() {
		return financialSpecList;
	}

	/**
	 * Sets the financial spec list.
	 * 
	 * @param financialSpecList
	 *            the new financial spec list
	 */
	public void setFinancialSpecList(List<ScQtCheckList> financialSpecList) {
		this.financialSpecList = financialSpecList;
	}

	/**
	 * Gets the zone general trees.
	 * 
	 * @return the zone general trees
	 */
	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	/**
	 * Sets the zone general trees.
	 * 
	 * @param zoneGeneralTrees
	 *            the new zone general trees
	 */
	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	/**
	 * Gets the selected general tree nodes.
	 * 
	 * @return the selected general tree nodes
	 */
	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	/**
	 * Sets the selected general tree nodes.
	 * 
	 * @param selectedGeneralTreeNodes
	 *            the new selected general tree nodes
	 */
	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	/**
	 * Gets the delivery loc bean.
	 * 
	 * @return the delivery loc bean
	 */
	public List<BriefingZoneBean> getDeliveryLocBean() {
		return deliveryLocBean;
	}

	/**
	 * Sets the delivery loc bean.
	 * 
	 * @param deliveryLocBean
	 *            the new delivery loc bean
	 */
	public void setDeliveryLocBean(List<BriefingZoneBean> deliveryLocBean) {
		this.deliveryLocBean = deliveryLocBean;
	}

	/**
	 * Gets the delivery loc.
	 * 
	 * @return the delivery loc
	 */
	public List<ScQtDeliveryLoc> getDeliveryLoc() {
		return deliveryLoc;
	}

	/**
	 * Sets the delivery loc.
	 * 
	 * @param deliveryLoc
	 *            the new delivery loc
	 */
	public void setDeliveryLoc(List<ScQtDeliveryLoc> deliveryLoc) {
		this.deliveryLoc = deliveryLoc;
	}

	/**
	 * Gets the deliveyzone name.
	 * 
	 * @return the deliveyzone name
	 */
	public String getDeliveyzoneName() {
		return deliveyzoneName;
	}

	/**
	 * Sets the deliveyzone name.
	 * 
	 * @param deliveyzoneName
	 *            the new deliveyzone name
	 */
	public void setDeliveyzoneName(String deliveyzoneName) {
		this.deliveyzoneName = deliveyzoneName;
	}

	/**
	 * Gets the delivery non zone list.
	 * 
	 * @return the delivery non zone list
	 */
	public List<ScQtDeliveryLoc> getDeliveryNonZoneList() {
		return deliveryNonZoneList;
	}

	/**
	 * Sets the delivery non zone list.
	 * 
	 * @param deliveryNonZoneList
	 *            the new delivery non zone list
	 */
	public void setDeliveryNonZoneList(List<ScQtDeliveryLoc> deliveryNonZoneList) {
		this.deliveryNonZoneList = deliveryNonZoneList;
	}

	/**
	 * Gets the delete delivery list.
	 * 
	 * @return the delete delivery list
	 */
	public List<ScQtDeliveryLoc> getDeleteDeliveryList() {
		return deleteDeliveryList;
	}

	/**
	 * Sets the delete delivery list.
	 * 
	 * @param deleteDeliveryList
	 *            the new delete delivery list
	 */
	public void setDeleteDeliveryList(List<ScQtDeliveryLoc> deleteDeliveryList) {
		this.deleteDeliveryList = deleteDeliveryList;
	}

	/**
	 * Gets the select delivery list.
	 * 
	 * @return the select delivery list
	 */
	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	/**
	 * Sets the select delivery list.
	 * 
	 * @param selectDeliveryList
	 *            the new select delivery list
	 */
	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	/**
	 * Gets the supplier criteria.
	 * 
	 * @return the supplier criteria
	 */
	public ScQtSupplierCriteria getSupplierCriteria() {
		return supplierCriteria;
	}

	/**
	 * Sets the supplier criteria.
	 * 
	 * @param supplierCriteria
	 *            the new supplier criteria
	 */
	public void setSupplierCriteria(ScQtSupplierCriteria supplierCriteria) {
		this.supplierCriteria = supplierCriteria;
	}

	/**
	 * Gets the technical checklist scoring.
	 * 
	 * @return the technical checklist scoring
	 */
	public int getTechnicalChecklistScoring() {
		return technicalChecklistScoring;
	}

	/**
	 * Sets the technical checklist scoring.
	 * 
	 * @param technicalChecklistScoring
	 *            the new technical checklist scoring
	 */
	public void setTechnicalChecklistScoring(int technicalChecklistScoring) {
		this.technicalChecklistScoring = technicalChecklistScoring;
	}

	/**
	 * Gets the checklist score type.
	 * 
	 * @return the checklist score type
	 */
	public String getChecklistScoreType() {
		return checklistScoreType;
	}

	/**
	 * Sets the checklist score type.
	 * 
	 * @param checklistScoreType
	 *            the new checklist score type
	 */
	public void setChecklistScoreType(String checklistScoreType) {
		this.checklistScoreType = checklistScoreType;
	}

	/**
	 * Gets the sc qt check list score.
	 * 
	 * @return the sc qt check list score
	 */
	public ScQtCheckListScore getScQtCheckListScore() {
		return scQtCheckListScore;
	}

	/**
	 * Sets the sc qt check list score.
	 * 
	 * @param scQtCheckListScore
	 *            the new sc qt check list score
	 */
	public void setScQtCheckListScore(ScQtCheckListScore scQtCheckListScore) {
		this.scQtCheckListScore = scQtCheckListScore;
	}

	/**
	 * Gets the company score type.
	 * 
	 * @return the company score type
	 */
	public String getCompanyScoreType() {
		return companyScoreType;
	}

	/**
	 * Sets the company score type.
	 * 
	 * @param companyScoreType
	 *            the new company score type
	 */
	public void setCompanyScoreType(String companyScoreType) {
		this.companyScoreType = companyScoreType;
	}

	/**
	 * Gets the technical checklist action.
	 * 
	 * @return the technical checklist action
	 */
	public List<SelectItem> getTechnicalChecklistAction() {
		return technicalChecklistAction;
	}

	/**
	 * Sets the technical checklist action.
	 * 
	 * @param technicalChecklistAction
	 *            the new technical checklist action
	 */
	public void setTechnicalChecklistAction(
			List<SelectItem> technicalChecklistAction) {
		this.technicalChecklistAction = technicalChecklistAction;
	}

	/**
	 * Gets the score varience.
	 * 
	 * @return the score varience
	 */
	public BigDecimal getScoreVarience() {
		return scoreVarience;
	}

	/**
	 * Sets the score varience.
	 * 
	 * @param scoreVarience
	 *            the new score varience
	 */
	public void setScoreVarience(BigDecimal scoreVarience) {
		this.scoreVarience = scoreVarience;
	}

	/**
	 * Gets the sc qt score weightage.
	 * 
	 * @return the sc qt score weightage
	 */
	public ScQtScoreWeightage getScQtScoreWeightage() {
		return scQtScoreWeightage;
	}

	/**
	 * Sets the sc qt score weightage.
	 * 
	 * @param scQtScoreWeightage
	 *            the new sc qt score weightage
	 */
	public void setScQtScoreWeightage(ScQtScoreWeightage scQtScoreWeightage) {
		this.scQtScoreWeightage = scQtScoreWeightage;
	}

	/**
	 * Gets the schedule list.
	 * 
	 * @return the schedule list
	 */
	public List<ScQtSchedule> getScheduleList() {
		return scheduleList;
	}

	/**
	 * Sets the schedule list.
	 * 
	 * @param scheduleList
	 *            the new schedule list
	 */
	public void setScheduleList(List<ScQtSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	/**
	 * Gets the sc qt contract score.
	 * 
	 * @return the sc qt contract score
	 */
	public ScQtCheckListScore getScQtContractScore() {
		return scQtContractScore;
	}

	/**
	 * Sets the sc qt contract score.
	 * 
	 * @param scQtContractScore
	 *            the new sc qt contract score
	 */
	public void setScQtContractScore(ScQtCheckListScore scQtContractScore) {
		this.scQtContractScore = scQtContractScore;
	}

	/**
	 * Gets the procuremnt category.
	 * 
	 * @return the procuremnt category
	 */
	public String getProcuremntCategory() {
		return procuremntCategory;
	}

	/**
	 * Sets the procuremnt category.
	 * 
	 * @param procuremntCategory
	 *            the new procuremnt category
	 */
	public void setProcuremntCategory(String procuremntCategory) {
		this.procuremntCategory = procuremntCategory;
	}

	/**
	 * Gets the fulfilment type.
	 * 
	 * @return the fulfilment type
	 */
	public String getFulfilmentType() {
		return fulfilmentType;
	}

	/**
	 * Sets the fulfilment type.
	 * 
	 * @param fulfilmentType
	 *            the new fulfilment type
	 */
	public void setFulfilmentType(String fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}

	/**
	 * Gets the technical score setting.
	 * 
	 * @return the technical score setting
	 */
	public ScQtScoreSetting getTechnicalScoreSetting() {
		return technicalScoreSetting;
	}

	/**
	 * Sets the technical score setting.
	 * 
	 * @param technicalScoreSetting
	 *            the new technical score setting
	 */
	public void setTechnicalScoreSetting(ScQtScoreSetting technicalScoreSetting) {
		this.technicalScoreSetting = technicalScoreSetting;
	}

	/**
	 * Gets the financial score setting.
	 * 
	 * @return the financial score setting
	 */
	public ScQtScoreSetting getFinancialScoreSetting() {
		return financialScoreSetting;
	}

	/**
	 * Sets the financial score setting.
	 * 
	 * @param financialScoreSetting
	 *            the new financial score setting
	 */
	public void setFinancialScoreSetting(ScQtScoreSetting financialScoreSetting) {
		this.financialScoreSetting = financialScoreSetting;
	}

	/**
	 * Gets the technicalpackage.
	 * 
	 * @return the technicalpackage
	 */
	public BigDecimal getTechnicalpackage() {
		return technicalpackage;
	}

	/**
	 * Sets the technicalpackage.
	 * 
	 * @param technicalpackage
	 *            the new technicalpackage
	 */
	public void setTechnicalpackage(BigDecimal technicalpackage) {
		this.technicalpackage = technicalpackage;
	}

	/**
	 * Gets the financialpackage.
	 * 
	 * @return the financialpackage
	 */
	public BigDecimal getFinancialpackage() {
		return financialpackage;
	}

	/**
	 * Sets the financialpackage.
	 * 
	 * @param financialpackage
	 *            the new financialpackage
	 */
	public void setFinancialpackage(BigDecimal financialpackage) {
		this.financialpackage = financialpackage;
	}

	/**
	 * Gets the recommendation package total.
	 * 
	 * @return the recommendation package total
	 */
	public BigDecimal getRecommendationPackageTotal() {
		return recommendationPackageTotal;
	}

	/**
	 * Sets the recommendation package total.
	 * 
	 * @param recommendationPackageTotal
	 *            the new recommendation package total
	 */
	public void setRecommendationPackageTotal(
			BigDecimal recommendationPackageTotal) {
		this.recommendationPackageTotal = recommendationPackageTotal;
	}

	/**
	 * Gets the recommend technical score.
	 * 
	 * @return the recommend technical score
	 */
	public BigDecimal getRecommendTechnicalScore() {
		return recommendTechnicalScore;
	}

	/**
	 * Sets the recommend technical score.
	 * 
	 * @param recommendTechnicalScore
	 *            the new recommend technical score
	 */
	public void setRecommendTechnicalScore(BigDecimal recommendTechnicalScore) {
		this.recommendTechnicalScore = recommendTechnicalScore;
	}

	/**
	 * Gets the recommendfinancial score.
	 * 
	 * @return the recommendfinancial score
	 */
	public BigDecimal getRecommendfinancialScore() {
		return recommendfinancialScore;
	}

	/**
	 * Sets the recommendfinancial score.
	 * 
	 * @param recommendfinancialScore
	 *            the new recommendfinancial score
	 */
	public void setRecommendfinancialScore(BigDecimal recommendfinancialScore) {
		this.recommendfinancialScore = recommendfinancialScore;
	}

	/**
	 * Gets the check list select score.
	 * 
	 * @return the check list select score
	 */
	public List<ScQtCheckListScore> getCheckListSelectScore() {
		return checkListSelectScore;
	}

	/**
	 * Sets the check list select score.
	 * 
	 * @param checkListSelectScore
	 *            the new check list select score
	 */
	public void setCheckListSelectScore(
			List<ScQtCheckListScore> checkListSelectScore) {
		this.checkListSelectScore = checkListSelectScore;
	}

	/**
	 * Gets the tab active index.
	 * 
	 * @return the tab active index
	 */
	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	/**
	 * Sets the tab active index.
	 * 
	 * @param tabActiveIndex
	 *            the new tab active index
	 */
	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	/**
	 * Gets the root zoanl tree.
	 * 
	 * @return the root zoanl tree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * Sets the root zoanl tree.
	 * 
	 * @param rootZoanlTree
	 *            the new root zoanl tree
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	/**
	 * Gets the supp criteriaopen select list.
	 * 
	 * @return the supp criteriaopen select list
	 */
	public List<ScQtCategory> getSuppCriteriaopenSelectList() {
		return suppCriteriaopenSelectList;
	}

	/**
	 * Sets the supp criteriaopen select list.
	 * 
	 * @param suppCriteriaopenSelectList
	 *            the new supp criteriaopen select list
	 */
	public void setSuppCriteriaopenSelectList(
			List<ScQtCategory> suppCriteriaopenSelectList) {
		this.suppCriteriaopenSelectList = suppCriteriaopenSelectList;
	}

	/**
	 * Gets the progressive payment list.
	 * 
	 * @return the progressive payment list
	 */
	public List<ScQtProgressivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	/**
	 * Sets the progressive payment list.
	 * 
	 * @param progressivePaymentList
	 *            the new progressive payment list
	 */
	public void setProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}

	/**
	 * Gets the sc qt remarks list.
	 * 
	 * @return the sc qt remarks list
	 */
	public List<ScQtRemark> getScQtRemarksList() {
		return scQtRemarksList;
	}

	/**
	 * Sets the sc qt remarks list.
	 * 
	 * @param scQtRemarksList
	 *            the new sc qt remarks list
	 */
	public void setScQtRemarksList(List<ScQtRemark> scQtRemarksList) {
		this.scQtRemarksList = scQtRemarksList;
	}

	/**
	 * Gets the state list.
	 * 
	 * @return the state list
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * Sets the state list.
	 * 
	 * @param stateList
	 *            the new state list
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the district list.
	 * 
	 * @return the district list
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * Sets the district list.
	 * 
	 * @param districtList
	 *            the new district list
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * Gets the state id.
	 * 
	 * @return the state id
	 */
	public Long getStateId() {
		return stateId;
	}

	/**
	 * Sets the state id.
	 * 
	 * @param stateId
	 *            the new state id
	 */
	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	/**
	 * Gets the district id.
	 * 
	 * @return the district id
	 */
	public Long getDistrictId() {
		return districtId;
	}

	/**
	 * Sets the district id.
	 * 
	 * @param districtId
	 *            the new district id
	 */
	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	/**
	 * Gets the ministry list.
	 * 
	 * @return the ministry list
	 */
	public List<PmOrgProfile> getMinistryList() {
		return ministryList;
	}

	/**
	 * Sets the ministry list.
	 * 
	 * @param ministryList
	 *            the new ministry list
	 */
	public void setMinistryList(List<PmOrgProfile> ministryList) {
		this.ministryList = ministryList;
	}

	/**
	 * Gets the ministry id.
	 * 
	 * @return the ministry id
	 */
	public Long getMinistryId() {
		return ministryId;
	}

	/**
	 * Sets the ministry id.
	 * 
	 * @param ministryId
	 *            the new ministry id
	 */
	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}

	/**
	 * Gets the department list.
	 * 
	 * @return the department list
	 */
	public List<PmOrgProfile> getDepartmentList() {
		return departmentList;
	}

	/**
	 * Sets the department list.
	 * 
	 * @param departmentList
	 *            the new department list
	 */
	public void setDepartmentList(List<PmOrgProfile> departmentList) {
		this.departmentList = departmentList;
	}

	/**
	 * Gets the ptjt list.
	 * 
	 * @return the ptjt list
	 */
	public List<Object[]> getPtjtList() {
		return ptjtList;
	}

	/**
	 * Sets the ptjt list.
	 * 
	 * @param ptjtList
	 *            the new ptjt list
	 */
	public void setPtjtList(List<Object[]> ptjtList) {
		this.ptjtList = ptjtList;
	}

	/**
	 * Gets the department id.
	 * 
	 * @return the department id
	 */
	public Long getDepartmentId() {
		return departmentId;
	}

	/**
	 * Sets the department id.
	 * 
	 * @param departmentId
	 *            the new department id
	 */
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	/**
	 * Gets the ptj id.
	 * 
	 * @return the ptj id
	 */
	public Long getPtjId() {
		return ptjId;
	}

	/**
	 * Sets the ptj id.
	 * 
	 * @param ptjId
	 *            the new ptj id
	 */
	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	/**
	 * Gets the delivery address select.
	 * 
	 * @return the delivery address select
	 */
	public List<PmAddressType> getDeliveryAddressSelect() {
		return deliveryAddressSelect;
	}

	/**
	 * Sets the delivery address select.
	 * 
	 * @param deliveryAddressSelect
	 *            the new delivery address select
	 */
	public void setDeliveryAddressSelect(
			List<PmAddressType> deliveryAddressSelect) {
		this.deliveryAddressSelect = deliveryAddressSelect;
	}

	/**
	 * Gets the stage two tech compliance list.
	 * 
	 * @return the stage two tech compliance list
	 */
	public List<PmDocument> getStageTwoTechComplianceList() {
		return stageTwoTechComplianceList;
	}

	/**
	 * Sets the stage two tech compliance list.
	 * 
	 * @param stageTwoTechComplianceList
	 *            the new stage two tech compliance list
	 */
	public void setStageTwoTechComplianceList(
			List<PmDocument> stageTwoTechComplianceList) {
		this.stageTwoTechComplianceList = stageTwoTechComplianceList;
	}

	/**
	 * Gets the stage two tech compliance select.
	 * 
	 * @return the stage two tech compliance select
	 */
	public List<PmDocument> getStageTwoTechComplianceSelect() {
		return stageTwoTechComplianceSelect;
	}

	/**
	 * Sets the stage two tech compliance select.
	 * 
	 * @param stageTwoTechComplianceSelect
	 *            the new stage two tech compliance select
	 */
	public void setStageTwoTechComplianceSelect(
			List<PmDocument> stageTwoTechComplianceSelect) {
		this.stageTwoTechComplianceSelect = stageTwoTechComplianceSelect;
	}

	/**
	 * Gets the stage two fin compliance list.
	 * 
	 * @return the stage two fin compliance list
	 */
	public List<PmDocument> getStageTwoFinComplianceList() {
		return stageTwoFinComplianceList;
	}

	/**
	 * Sets the stage two fin compliance list.
	 * 
	 * @param stageTwoFinComplianceList
	 *            the new stage two fin compliance list
	 */
	public void setStageTwoFinComplianceList(
			List<PmDocument> stageTwoFinComplianceList) {
		this.stageTwoFinComplianceList = stageTwoFinComplianceList;
	}

	/**
	 * Gets the stage two fin compliance select.
	 * 
	 * @return the stage two fin compliance select
	 */
	public List<PmDocument> getStageTwoFinComplianceSelect() {
		return stageTwoFinComplianceSelect;
	}

	/**
	 * Sets the stage two fin compliance select.
	 * 
	 * @param stageTwoFinComplianceSelect
	 *            the new stage two fin compliance select
	 */
	public void setStageTwoFinComplianceSelect(
			List<PmDocument> stageTwoFinComplianceSelect) {
		this.stageTwoFinComplianceSelect = stageTwoFinComplianceSelect;
	}

	/**
	 * Gets the start year.
	 * 
	 * @return the start year
	 */
	public String getStartYear() {
		return startYear;
	}

	/**
	 * Sets the start year.
	 * 
	 * @param startYear
	 *            the new start year
	 */
	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	/**
	 * Gets the end year.
	 * 
	 * @return the end year
	 */
	public String getEndYear() {
		return endYear;
	}

	/**
	 * Sets the end year.
	 * 
	 * @param endYear
	 *            the new end year
	 */
	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	/**
	 * Gets the offer price package.
	 * 
	 * @return the offer price package
	 */
	public ScQtScoreOfferPrice getOfferPricePackage() {
		return offerPricePackage;
	}

	/**
	 * Sets the offer price package.
	 * 
	 * @param offerPricePackage
	 *            the new offer price package
	 */
	public void setOfferPricePackage(ScQtScoreOfferPrice offerPricePackage) {
		this.offerPricePackage = offerPricePackage;
	}

	/**
	 * Gets the offer price item.
	 * 
	 * @return the offer price item
	 */
	public ScQtScoreOfferPrice getOfferPriceItem() {
		return OfferPriceItem;
	}

	/**
	 * Sets the offer price item.
	 * 
	 * @param offerPriceItem
	 *            the new offer price item
	 */
	public void setOfferPriceItem(ScQtScoreOfferPrice offerPriceItem) {
		OfferPriceItem = offerPriceItem;
	}

	/**
	 * Gets the demo score setting.
	 * 
	 * @return the demo score setting
	 */
	public ScQtScoreSetting getDemoScoreSetting() {
		return demoScoreSetting;
	}

	/**
	 * Sets the demo score setting.
	 * 
	 * @param demoScoreSetting
	 *            the new demo score setting
	 */
	public void setDemoScoreSetting(ScQtScoreSetting demoScoreSetting) {
		this.demoScoreSetting = demoScoreSetting;
	}

	/**
	 * Gets the recommendation package score.
	 * 
	 * @return the recommendation package score
	 */
	public ScQtScoreSetting getRecommendationPackageScore() {
		return recommendationPackageScore;
	}

	/**
	 * Sets the recommendation package score.
	 * 
	 * @param recommendationPackageScore
	 *            the new recommendation package score
	 */
	public void setRecommendationPackageScore(
			ScQtScoreSetting recommendationPackageScore) {
		this.recommendationPackageScore = recommendationPackageScore;
	}

	/**
	 * Gets the recommendation score.
	 * 
	 * @return the recommendation score
	 */
	public ScQtScoreSetting getRecommendationScore() {
		return recommendationScore;
	}

	/**
	 * Sets the recommendation score.
	 * 
	 * @param recommendationScore
	 *            the new recommendation score
	 */
	public void setRecommendationScore(ScQtScoreSetting recommendationScore) {
		this.recommendationScore = recommendationScore;
	}

	/**
	 * Gets the state code.
	 * 
	 * @return the state code
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * Sets the state code.
	 * 
	 * @param stateCode
	 *            the new state code
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * Gets the district code.
	 * 
	 * @return the district code
	 */
	public String getDistrictCode() {
		return districtCode;
	}

	/**
	 * Sets the district code.
	 * 
	 * @param districtCode
	 *            the new district code
	 */
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	/**
	 * Gets the delivery zones.
	 * 
	 * @return the delivery zones
	 */
	public List<TplSpecZoneDtl> getDeliveryZones() {
		return deliveryZones;
	}

	/**
	 * Sets the delivery zones.
	 * 
	 * @param deliveryZones
	 *            the new delivery zones
	 */
	public void setDeliveryZones(List<TplSpecZoneDtl> deliveryZones) {
		this.deliveryZones = deliveryZones;
	}

	/**
	 * Gets the tpl spec zone dtl.
	 * 
	 * @return the tpl spec zone dtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * Sets the tpl spec zone dtl.
	 * 
	 * @param tplSpecZoneDtl
	 *            the new tpl spec zone dtl
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * Gets the address zone bean.
	 * 
	 * @return the address zone bean
	 */
	public List<BriefingZoneBean> getAddressZoneBean() {
		return addressZoneBean;
	}

	/**
	 * Sets the address zone bean.
	 * 
	 * @param addressZoneBean
	 *            the new address zone bean
	 */
	public void setAddressZoneBean(List<BriefingZoneBean> addressZoneBean) {
		this.addressZoneBean = addressZoneBean;
	}

	/**
	 * Gets the panel item category.
	 * 
	 * @return the panel item category
	 */
	public String getPanelItemCategory() {
		return panelItemCategory;
	}

	/**
	 * Sets the panel item category.
	 * 
	 * @param panelItemCategory
	 *            the new panel item category
	 */
	public void setPanelItemCategory(String panelItemCategory) {
		this.panelItemCategory = panelItemCategory;
	}

	/**
	 * Gets the fin spec list.
	 * 
	 * @return the fin spec list
	 */
	public List<ScQtCheckList> getFinSpecList() {
		return finSpecList;
	}

	/**
	 * Sets the fin spec list.
	 * 
	 * @param finSpecList
	 *            the new fin spec list
	 */
	public void setFinSpecList(List<ScQtCheckList> finSpecList) {
		this.finSpecList = finSpecList;
	}

	/**
	 * Gets the panel item id.
	 * 
	 * @return the panel item id
	 */
	public Long getPanelItemId() {
		return panelItemId;
	}

	/**
	 * Sets the panel item id.
	 * 
	 * @param panelItemId
	 *            the new panel item id
	 */
	public void setPanelItemId(Long panelItemId) {
		this.panelItemId = panelItemId;
	}

	/**
	 * Gets the active index.
	 * 
	 * @return the active index
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * Sets the active index.
	 * 
	 * @param activeIndex
	 *            the new active index
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * Gets the ct panel item.
	 * 
	 * @return the ct panel item
	 */
	public CtPanelItem getCtPanelItem() {
		return ctPanelItem;
	}

	/**
	 * Sets the ct panel item.
	 * 
	 * @param ctPanelItem
	 *            the new ct panel item
	 */
	public void setCtPanelItem(CtPanelItem ctPanelItem) {
		this.ctPanelItem = ctPanelItem;
	}

	/**
	 * Checks if is required.
	 * 
	 * @return true, if is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * 
	 * @param required
	 *            the new required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Gets the ct zone suppliers.
	 * 
	 * @return the ct zone suppliers
	 */
	public List<TreeNode> getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	/**
	 * Sets the ct zone suppliers.
	 * 
	 * @param ctZoneSuppliers
	 *            the new ct zone suppliers
	 */
	public void setCtZoneSuppliers(List<TreeNode> ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	/**
	 * Gets the panel zone select.
	 * 
	 * @return the panel zone select
	 */
	public TreeNode getPanelZoneSelect() {
		return panelZoneSelect;
	}

	/**
	 * Sets the panel zone select.
	 * 
	 * @param panelZoneSelect
	 *            the new panel zone select
	 */
	public void setPanelZoneSelect(TreeNode panelZoneSelect) {
		this.panelZoneSelect = panelZoneSelect;
	}

	/**
	 * Gets the ct panel zones.
	 * 
	 * @return the ct panel zones
	 */
	public List<CtPanelZone> getCtPanelZones() {
		return ctPanelZones;
	}

	/**
	 * Sets the ct panel zones.
	 * 
	 * @param ctPanelZones
	 *            the new ct panel zones
	 */
	public void setCtPanelZones(List<CtPanelZone> ctPanelZones) {
		this.ctPanelZones = ctPanelZones;
	}

	/**
	 * Gets the ctpanel supplier.
	 * 
	 * @return the ctpanel supplier
	 */
	public CtPanelSupplier getCtpanelSupplier() {
		return ctpanelSupplier;
	}

	/**
	 * Sets the ctpanel supplier.
	 * 
	 * @param ctpanelSupplier
	 *            the new ctpanel supplier
	 */
	public void setCtpanelSupplier(CtPanelSupplier ctpanelSupplier) {
		this.ctpanelSupplier = ctpanelSupplier;
	}

	/**
	 * Gets the circular exp date.
	 * 
	 * @return the circular exp date
	 */
	public Date getCircularExpDate() {
		return circularExpDate;
	}

	/**
	 * Sets the circular exp date.
	 * 
	 * @param circularExpDate
	 *            the new circular exp date
	 */
	public void setCircularExpDate(Date circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	/**
	 * Gets the contractremark.
	 * 
	 * @return the contractremark
	 */
	public ScQtRemark getContractremark() {
		return contractremark;
	}

	/**
	 * Sets the contractremark.
	 * 
	 * @param contractremark
	 *            the new contractremark
	 */
	public void setContractremark(ScQtRemark contractremark) {
		this.contractremark = contractremark;
	}

	/**
	 * Gets the selected tree node.
	 * 
	 * @return the selected tree node
	 */
	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	/**
	 * Sets the selected tree node.
	 * 
	 * @param selectedTreeNode
	 *            the new selected tree node
	 */
	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	/**
	 * Gets the circular ver.
	 * 
	 * @return the circular ver
	 */
	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	/**
	 * Sets the circular ver.
	 * 
	 * @param circularVer
	 *            the new circular ver
	 */
	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	/**
	 * Gets the ct search bean.
	 * 
	 * @return the ct search bean
	 */
	public CtCircularSearchBean getCtSearchBean() {
		return ctSearchBean;
	}

	/**
	 * Sets the ct search bean.
	 * 
	 * @param ctSearchBean
	 *            the new ct search bean
	 */
	public void setCtSearchBean(CtCircularSearchBean ctSearchBean) {
		this.ctSearchBean = ctSearchBean;
	}

	/**
	 * Gets the evaluation type item.
	 * 
	 * @return the evaluation type item
	 */
	public List<SelectItem> getEvaluationTypeItem() {
		return evaluationTypeItem;
	}

	/**
	 * Sets the evaluation type item.
	 * 
	 * @param evaluationTypeItem
	 *            the new evaluation type item
	 */
	public void setEvaluationTypeItem(List<SelectItem> evaluationTypeItem) {
		this.evaluationTypeItem = evaluationTypeItem;
	}

	/**
	 * Gets the supplier selected tree node.
	 * 
	 * @return the supplier selected tree node
	 */
	public TreeNode getSupplierSelectedTreeNode() {
		return supplierSelectedTreeNode;
	}

	/**
	 * Sets the supplier selected tree node.
	 * 
	 * @param supplierSelectedTreeNode
	 *            the new supplier selected tree node
	 */
	public void setSupplierSelectedTreeNode(TreeNode supplierSelectedTreeNode) {
		this.supplierSelectedTreeNode = supplierSelectedTreeNode;
	}

	/**
	 * Gets the subject.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the chair person pin num.
	 * 
	 * @return the chair person pin num
	 */
	public String getChairPersonPinNum() {
		return chairPersonPinNum;
	}

	/**
	 * Sets the chair person pin num.
	 * 
	 * @param chairPersonPinNum
	 *            the new chair person pin num
	 */
	public void setChairPersonPinNum(String chairPersonPinNum) {
		this.chairPersonPinNum = chairPersonPinNum;
	}

	/**
	 * Gets the personnel.
	 * 
	 * @return the personnel
	 */
	public SmPersonnel getPersonnel() {
		return personnel;
	}

	/**
	 * Sets the personnel.
	 * 
	 * @param personnel
	 *            the new personnel
	 */
	public void setPersonnel(SmPersonnel personnel) {
		this.personnel = personnel;
	}

	/**
	 * Gets the supplier branch.
	 * 
	 * @return the supplier branch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * Sets the supplier branch.
	 * 
	 * @param supplierBranch
	 *            the new supplier branch
	 */
	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	/**
	 * Gets the supplier address.
	 * 
	 * @return the supplier address
	 */
	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * Sets the supplier address.
	 * 
	 * @param supplierAddress
	 *            the new supplier address
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * Gets the supplier contact.
	 * 
	 * @return the supplier contact
	 */
	public String getSupplierContact() {
		return supplierContact;
	}

	/**
	 * Sets the supplier contact.
	 * 
	 * @param supplierContact
	 *            the new supplier contact
	 */
	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	/**
	 * Gets the supplier email.
	 * 
	 * @return the supplier email
	 */
	public String getSupplierEmail() {
		return supplierEmail;
	}

	/**
	 * Sets the supplier email.
	 * 
	 * @param supplierEmail
	 *            the new supplier email
	 */
	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	/**
	 * Gets the ct circular ver.
	 * 
	 * @return the ct circular ver
	 */
	public CtCircularVer getCtCircularVer() {
		return ctCircularVer;
	}

	/**
	 * Sets the ct circular ver.
	 * 
	 * @param ctCircularVer
	 *            the new ct circular ver
	 */
	public void setCtCircularVer(CtCircularVer ctCircularVer) {
		this.ctCircularVer = ctCircularVer;
	}

	/**
	 * Gets the finspec checklist.
	 * 
	 * @return the finspec checklist
	 */
	public List<ScQtCheckList> getFinspecChecklist() {
		return finspecChecklist;
	}

	/**
	 * Sets the finspec checklist.
	 * 
	 * @param finspecChecklist
	 *            the new finspec checklist
	 */
	public void setFinspecChecklist(List<ScQtCheckList> finspecChecklist) {
		this.finspecChecklist = finspecChecklist;
	}

	/**
	 * Gets the button out come.
	 * 
	 * @return the button out come
	 */
	public String getButtonOutCome() {
		return buttonOutCome;
	}

	/**
	 * Sets the button out come.
	 * 
	 * @param buttonOutCome
	 *            the new button out come
	 */
	public void setButtonOutCome(String buttonOutCome) {
		this.buttonOutCome = buttonOutCome;
	}

	/**
	 * Gets the all state code.
	 * 
	 * @return the all state code
	 */
	public PTJLocalityCode getAllStateCode() {
		return allStateCode;
	}

	/**
	 * Sets the all state code.
	 * 
	 * @param allStateCode
	 *            the new all state code
	 */
	public void setAllStateCode(PTJLocalityCode allStateCode) {
		this.allStateCode = allStateCode;
	}

	/**
	 * Checks if is panel change.
	 * 
	 * @return true, if is panel change
	 */
	public boolean isPanelChange() {
		return isPanelChange;
	}

	/**
	 * Sets the panel change.
	 * 
	 * @param isPanelChange
	 *            the new panel change
	 */
	public void setPanelChange(boolean isPanelChange) {
		this.isPanelChange = isPanelChange;
	}

	/**
	 * Gets the new item category.
	 * 
	 * @return the new item category
	 */
	public String getNewItemCategory() {
		return newItemCategory;
	}

	/**
	 * Sets the new item category.
	 * 
	 * @param newItemCategory
	 *            the new new item category
	 */
	public void setNewItemCategory(String newItemCategory) {
		this.newItemCategory = newItemCategory;
	}

	/**
	 * Gets the years display list.
	 * 
	 * @return the years display list
	 */
	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	/**
	 * Sets the years display list.
	 * 
	 * @param yearsDisplayList
	 *            the new years display list
	 */
	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	/**
	 * Gets the bankstmnt tomonth.
	 * 
	 * @return the bankstmnt tomonth
	 */
	public String getBankstmntTomonth() {
		return bankstmntTomonth;
	}

	/**
	 * Sets the bankstmnt tomonth.
	 * 
	 * @param bankstmntTomonth
	 *            the new bankstmnt tomonth
	 */
	public void setBankstmntTomonth(String bankstmntTomonth) {
		this.bankstmntTomonth = bankstmntTomonth;
	}

	/**
	 * Gets the temp index.
	 * 
	 * @return the temp index
	 */
	public int getTempIndex() {
		return tempIndex;
	}

	/**
	 * Sets the temp index.
	 * 
	 * @param tempIndex
	 *            the new temp index
	 */
	public void setTempIndex(int tempIndex) {
		this.tempIndex = tempIndex;
	}

	/**
	 * Checks if is ctzones.
	 * 
	 * @return true, if is ctzones
	 */
	public boolean isCtzones() {
		return ctzones;
	}

	/**
	 * Sets the ctzones.
	 * 
	 * @param ctzones
	 *            the new ctzones
	 */
	public void setCtzones(boolean ctzones) {
		this.ctzones = ctzones;
	}

	/**
	 * Checks if is select panel item.
	 * 
	 * @return true, if is select panel item
	 */
	public boolean isSelectPanelItem() {
		return selectPanelItem;
	}

	/**
	 * Sets the select panel item.
	 * 
	 * @param selectPanelItem
	 *            the new select panel item
	 */
	public void setSelectPanelItem(boolean selectPanelItem) {
		this.selectPanelItem = selectPanelItem;
	}

	/**
	 * Gets the committee activities.
	 * 
	 * @return the committee activities
	 */
	public List<ScQtCommitteeActivity> getCommitteeActivities() {
		return committeeActivities;
	}

	/**
	 * Sets the committee activities.
	 * 
	 * @param committeeActivities
	 *            the new committee activities
	 */
	public void setCommitteeActivities(
			List<ScQtCommitteeActivity> committeeActivities) {
		this.committeeActivities = committeeActivities;
	}

	/**
	 * Gets the spec status id.
	 * 
	 * @return the spec status id
	 */
	public Long getSpecStatusId() {
		return specStatusId;
	}

	/**
	 * Sets the spec status id.
	 * 
	 * @param specStatusId
	 *            the new spec status id
	 */
	public void setSpecStatusId(Long specStatusId) {
		this.specStatusId = specStatusId;
	}

	/**
	 * Checks if is spec select stats.
	 * 
	 * @return true, if is spec select stats
	 */
	public boolean isSpecSelectStats() {
		return specSelectStats;
	}

	/**
	 * Sets the spec select stats.
	 * 
	 * @param specSelectStats
	 *            the new spec select stats
	 */
	public void setSpecSelectStats(boolean specSelectStats) {
		this.specSelectStats = specSelectStats;
	}

	/**
	 * Gets the spec type.
	 * 
	 * @return the spec type
	 */
	public String getSpecType() {
		return specType;
	}

	/**
	 * Sets the spec type.
	 * 
	 * @param specType
	 *            the new spec type
	 */
	public void setSpecType(String specType) {
		this.specType = specType;
	}

	/**
	 * Gets the selected check list.
	 * 
	 * @return the selected check list
	 */
	public int getSelectedCheckList() {
		return selectedCheckList;
	}

	/**
	 * Sets the selected check list.
	 * 
	 * @param selectedCheckList
	 *            the new selected check list
	 */
	public void setSelectedCheckList(int selectedCheckList) {
		this.selectedCheckList = selectedCheckList;
	}

	/**
	 * Gets the to be remove list.
	 * 
	 * @return the to be remove list
	 */
	public List<ScQtCheckListScore> getToBeRemoveList() {
		return toBeRemoveList;
	}

	/**
	 * Sets the to be remove list.
	 * 
	 * @param toBeRemoveList
	 *            the new to be remove list
	 */
	public void setToBeRemoveList(List<ScQtCheckListScore> toBeRemoveList) {
		this.toBeRemoveList = toBeRemoveList;
	}

	/**
	 * Gets the other score selected.
	 * 
	 * @return the other score selected
	 */
	public List<ScQtCheckListScore> getOtherScoreSelected() {
		return otherScoreSelected;
	}

	/**
	 * Sets the other score selected.
	 * 
	 * @param otherScoreSelected
	 *            the new other score selected
	 */
	public void setOtherScoreSelected(
			List<ScQtCheckListScore> otherScoreSelected) {
		this.otherScoreSelected = otherScoreSelected;
	}

	/**
	 * Gets the criteria type.
	 * 
	 * @return the criteria type
	 */
	public String getCriteriaType() {
		return criteriaType;
	}

	/**
	 * Sets the criteria type.
	 * 
	 * @param criteriaType
	 *            the new criteria type
	 */
	public void setCriteriaType(String criteriaType) {
		this.criteriaType = criteriaType;
	}

	/**
	 * Gets the other criteria type.
	 * 
	 * @return the other criteria type
	 */
	public String getOtherCriteriaType() {
		return otherCriteriaType;
	}

	/**
	 * Sets the other criteria type.
	 * 
	 * @param otherCriteriaType
	 *            the new other criteria type
	 */
	public void setOtherCriteriaType(String otherCriteriaType) {
		this.otherCriteriaType = otherCriteriaType;
	}

	/**
	 * Gets the panel item list.
	 * 
	 * @return the panel item list
	 */
	public List<CtPanelItem> getPanelItemList() {
		return panelItemList;
	}

	/**
	 * Sets the panel item list.
	 * 
	 * @param panelItemList
	 *            the new panel item list
	 */
	public void setPanelItemList(List<CtPanelItem> panelItemList) {
		this.panelItemList = panelItemList;
	}

	/**
	 * Gets the check list title.
	 * 
	 * @return the check list title
	 */
	public String getCheckListTitle() {
		return checkListTitle;
	}

	/**
	 * Sets the check list title.
	 * 
	 * @param checkListTitle
	 *            the new check list title
	 */
	public void setCheckListTitle(String checkListTitle) {
		this.checkListTitle = checkListTitle;
	}

	/**
	 * Gets the local level code.
	 * 
	 * @return the local level code
	 */
	public String getLocalLevelCode() {
		return LocalLevelCode;
	}

	/**
	 * Sets the local level code.
	 * 
	 * @param localLevelCode
	 *            the new local level code
	 */
	public void setLocalLevelCode(String localLevelCode) {
		LocalLevelCode = localLevelCode;
	}

	/**
	 * Checks if is read only.
	 * 
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Sets the read only.
	 * 
	 * @param readOnly
	 *            the new read only
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the mark deleted tech checklist.
	 * 
	 * @return the mark deleted tech checklist
	 */
	public List<ScQtCheckList> getMarkDeletedTechChecklist() {
		return markDeletedTechChecklist;
	}

	/**
	 * Sets the mark deleted tech checklist.
	 * 
	 * @param markDeletedTechChecklist
	 *            the new mark deleted tech checklist
	 */
	public void setMarkDeletedTechChecklist(
			List<ScQtCheckList> markDeletedTechChecklist) {
		this.markDeletedTechChecklist = markDeletedTechChecklist;
	}

	/**
	 * Gets the mark deleted fin checklist.
	 * 
	 * @return the mark deleted fin checklist
	 */
	public List<ScQtCheckList> getMarkDeletedFinChecklist() {
		return markDeletedFinChecklist;
	}

	/**
	 * Sets the mark deleted fin checklist.
	 * 
	 * @param markDeletedFinChecklist
	 *            the new mark deleted fin checklist
	 */
	public void setMarkDeletedFinChecklist(
			List<ScQtCheckList> markDeletedFinChecklist) {
		this.markDeletedFinChecklist = markDeletedFinChecklist;
	}

	/**
	 * Gets the existing supplier location list.
	 * 
	 * @return the existing supplier location list
	 */
	public List<ScQtLocCriteria> getExistingSupplierLocationList() {
		return existingSupplierLocationList;
	}

	/**
	 * Sets the existing supplier location list.
	 * 
	 * @param existingSupplierLocationList
	 *            the new existing supplier location list
	 */
	public void setExistingSupplierLocationList(
			List<ScQtLocCriteria> existingSupplierLocationList) {
		this.existingSupplierLocationList = existingSupplierLocationList;
	}

	/**
	 * Checks if is supplier validation.
	 * 
	 * @return true, if is supplier validation
	 */
	public boolean isSupplierValidation() {
		return isSupplierValidation;
	}

	/**
	 * Sets the supplier validation.
	 * 
	 * @param isSupplierValidation
	 *            the new supplier validation
	 */
	public void setSupplierValidation(boolean isSupplierValidation) {
		this.isSupplierValidation = isSupplierValidation;
	}

	/**
	 * Gets the technical spec count.
	 * 
	 * @return the technical spec count
	 */
	public int getTechnicalSpecCount() {
		return technicalSpecCount;
	}

	/**
	 * Sets the technical spec count.
	 * 
	 * @param technicalSpecCount
	 *            the new technical spec count
	 */
	public void setTechnicalSpecCount(int technicalSpecCount) {
		this.technicalSpecCount = technicalSpecCount;
	}

	/**
	 * Gets the financial spec count.
	 * 
	 * @return the financial spec count
	 */
	public int getFinancialSpecCount() {
		return financialSpecCount;
	}

	/**
	 * Sets the financial spec count.
	 * 
	 * @param financialSpecCount
	 *            the new financial spec count
	 */
	public void setFinancialSpecCount(int financialSpecCount) {
		this.financialSpecCount = financialSpecCount;
	}

	/**
	 * Gets the bankyears list.
	 * 
	 * @return the bankyears list
	 */
	public List<SelectItem> getBankyearsList() {
		return bankyearsList;
	}

	/**
	 * Sets the bankyears list.
	 * 
	 * @param bankyearsList
	 *            the new bankyears list
	 */
	public void setBankyearsList(List<SelectItem> bankyearsList) {
		this.bankyearsList = bankyearsList;
	}

	/**
	 * Checks if is new template spec.
	 * 
	 * @return true, if is new template spec
	 */
	public boolean isNewTemplateSpec() {
		return newTemplateSpec;
	}

	/**
	 * Sets the new template spec.
	 * 
	 * @param newTemplateSpec
	 *            the new new template spec
	 */
	public void setNewTemplateSpec(boolean newTemplateSpec) {
		this.newTemplateSpec = newTemplateSpec;
	}

	/**
	 * Gets the restricted supplier list.
	 * 
	 * @return the restricted supplier list
	 */
	public List<ScQtSupplier> getRestrictedSupplierList() {
		return restrictedSupplierList;
	}

	/**
	 * Sets the restricted supplier list.
	 * 
	 * @param restrictedSupplierList
	 *            the new restricted supplier list
	 */
	public void setRestrictedSupplierList(
			List<ScQtSupplier> restrictedSupplierList) {
		this.restrictedSupplierList = restrictedSupplierList;
	}

	/**
	 * Gets the sm supplier.
	 * 
	 * @return the sm supplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * Sets the sm supplier.
	 * 
	 * @param smSupplier
	 *            the new sm supplier
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * Gets the sm mof account.
	 * 
	 * @return the sm mof account
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * Sets the sm mof account.
	 * 
	 * @param smMofAccount
	 *            the new sm mof account
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * Gets the delete panel treenode.
	 * 
	 * @return the delete panel treenode
	 */
	public TreeNode getDeletePanelTreenode() {
		return deletePanelTreenode;
	}

	/**
	 * Sets the delete panel treenode.
	 * 
	 * @param deletePanelTreenode
	 *            the new delete panel treenode
	 */
	public void setDeletePanelTreenode(TreeNode deletePanelTreenode) {
		this.deletePanelTreenode = deletePanelTreenode;
	}

	/**
	 * Gets the supplier location list.
	 * 
	 * @return the supplierLocationList
	 */
	public List<ScQtLocCriteria> getSupplierLocationList() {
		return supplierLocationList;
	}

	/**
	 * Sets the supplier location list.
	 * 
	 * @param supplierLocationList
	 *            the supplierLocationList to set
	 */
	public void setSupplierLocationList(
			List<ScQtLocCriteria> supplierLocationList) {
		this.supplierLocationList = supplierLocationList;
	}

	/**
	 * @return the financialChecklistAction
	 */
	public List<SelectItem> getFinancialChecklistAction() {
		return financialChecklistAction;
	}

	/**
	 * @param financialChecklistAction
	 *            the financialChecklistAction to set
	 */
	public void setFinancialChecklistAction(
			List<SelectItem> financialChecklistAction) {
		this.financialChecklistAction = financialChecklistAction;
	}

	/**
	 * @return the specNewable
	 */
	public boolean isSpecNewable() {
		return specNewable;
	}

	/**
	 * @param specNewable
	 *            the specNewable to set
	 */
	public void setSpecNewable(boolean specNewable) {
		this.specNewable = specNewable;
	}

	/**
	 * @return the qtnTendertemplateSearchBean
	 */
	public QtnTenderSearchBean getQtnTendertemplateSearchBean() {
		return qtnTendertemplateSearchBean;
	}

	/**
	 * @param qtnTendertemplateSearchBean
	 *            the qtnTendertemplateSearchBean to set
	 */
	public void setQtnTendertemplateSearchBean(
			QtnTenderSearchBean qtnTendertemplateSearchBean) {
		this.qtnTendertemplateSearchBean = qtnTendertemplateSearchBean;
	}

	/**
	 * @return the tempZoneName
	 */
	public String getTempZoneName() {
		return tempZoneName;
	}

	/**
	 * @param tempZoneName
	 *            the tempZoneName to set
	 */
	public void setTempZoneName(String tempZoneName) {
		this.tempZoneName = tempZoneName;
	}

	/**
	 * @return the isCheckAllList
	 */
	public boolean isCheckAllList() {
		return isCheckAllList;
	}

	/**
	 * @param isCheckAllList
	 *            the isCheckAllList to set
	 */
	public void setCheckAllList(boolean isCheckAllList) {
		this.isCheckAllList = isCheckAllList;
	}

	/**
	 * @return the finacialItemScoreSetting
	 */
	public ScQtScoreSetting getFinacialItemScoreSetting() {
		return finacialItemScoreSetting;
	}

	/**
	 * @param finacialItemScoreSetting
	 *            the finacialItemScoreSetting to set
	 */
	public void setFinacialItemScoreSetting(
			ScQtScoreSetting finacialItemScoreSetting) {
		this.finacialItemScoreSetting = finacialItemScoreSetting;
	}

	/**
	 * @return the financialItemScore
	 */
	public BigDecimal getFinancialItemScore() {
		return financialItemScore;
	}

	/**
	 * @param financialItemScore
	 *            the financialItemScore to set
	 */
	public void setFinancialItemScore(BigDecimal financialItemScore) {
		this.financialItemScore = financialItemScore;
	}

	/**
	 * @return the totalScore
	 */
	public BigDecimal getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore
	 *            the totalScore to set
	 */
	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return the locationLevel
	 */
	public String getLocationLevel() {
		return locationLevel;
	}

	/**
	 * @param locationLevel
	 *            the locationLevel to set
	 */
	public void setLocationLevel(String locationLevel) {
		this.locationLevel = locationLevel;
	}

	/**
	 * @return the ptjProfileDDList
	 */
	public List<Object[]> getPtjProfileDDList() {
		return ptjProfileDDList;
	}

	/**
	 * @param ptjProfileDDList
	 *            the ptjProfileDDList to set
	 */
	public void setPtjProfileDDList(List<Object[]> ptjProfileDDList) {
		this.ptjProfileDDList = ptjProfileDDList;
	}

	/**
	 * @return the searchDeliveryAddressDataModel
	 */
	public IPagingDataModel<PmAddressType> getSearchDeliveryAddressDataModel() {
		return searchDeliveryAddressDataModel;
	}

	/**
	 * @param searchDeliveryAddressDataModel
	 *            the searchDeliveryAddressDataModel to set
	 */
	public void setSearchDeliveryAddressDataModel(
			IPagingDataModel<PmAddressType> searchDeliveryAddressDataModel) {
		this.searchDeliveryAddressDataModel = searchDeliveryAddressDataModel;
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
	 * @return the ptjGroupId
	 */
	public Long getPtjGroupId() {
		return ptjGroupId;
	}

	/**
	 * @param ptjGroupId
	 *            the ptjGroupId to set
	 */
	public void setPtjGroupId(Long ptjGroupId) {
		this.ptjGroupId = ptjGroupId;
	}

	/**
	 * @return the checkListYesIndex
	 */
	public int getCheckListYesIndex() {
		return checkListYesIndex;
	}

	/**
	 * @param checkListYesIndex
	 *            the checkListYesIndex to set
	 */
	public void setCheckListYesIndex(int checkListYesIndex) {
		this.checkListYesIndex = checkListYesIndex;
	}

	/**
	 * @return the technicalOtherChecklistSubmit
	 */
	public boolean isTechnicalOtherChecklistSubmit() {
		return technicalOtherChecklistSubmit;
	}

	/**
	 * @param technicalOtherChecklistSubmit
	 *            the technicalOtherChecklistSubmit to set
	 */
	public void setTechnicalOtherChecklistSubmit(
			boolean technicalOtherChecklistSubmit) {
		this.technicalOtherChecklistSubmit = technicalOtherChecklistSubmit;
	}

	/**
	 * @return the financialOtherChecklistSubmit
	 */
	public boolean isFinancialOtherChecklistSubmit() {
		return financialOtherChecklistSubmit;
	}

	/**
	 * @param financialOtherChecklistSubmit
	 *            the financialOtherChecklistSubmit to set
	 */
	public void setFinancialOtherChecklistSubmit(
			boolean financialOtherChecklistSubmit) {
		this.financialOtherChecklistSubmit = financialOtherChecklistSubmit;
	}

	public List<ScQtSchedule> getToBeRemoveSchedules() {
		return toBeRemoveSchedules;
	}

	public void setToBeRemoveSchedules(List<ScQtSchedule> toBeRemoveSchedules) {
		this.toBeRemoveSchedules = toBeRemoveSchedules;
	}

	public List<ScQtProgressivePayment> getToBeRemoveProgPayments() {
		return toBeRemoveProgPayments;
	}

	public void setToBeRemoveProgPayments(
			List<ScQtProgressivePayment> toBeRemoveProgPayments) {
		this.toBeRemoveProgPayments = toBeRemoveProgPayments;
	}

	/**
	 * @return the zoneSpecUpdate
	 */
	public boolean isZoneSpecUpdate() {
		return zoneSpecUpdate;
	}

	/**
	 * @param zoneSpecUpdate
	 *            the zoneSpecUpdate to set
	 */
	public void setZoneSpecUpdate(boolean zoneSpecUpdate) {
		this.zoneSpecUpdate = zoneSpecUpdate;
	}

	/**
	 * @return the zonesWarnMessageUpdate
	 */
	public boolean isZonesWarnMessageUpdate() {
		return zonesWarnMessageUpdate;
	}

	/**
	 * @param zonesWarnMessageUpdate
	 *            the zonesWarnMessageUpdate to set
	 */
	public void setZonesWarnMessageUpdate(boolean zonesWarnMessageUpdate) {
		this.zonesWarnMessageUpdate = zonesWarnMessageUpdate;
	}

	/**
	 * @return the tobeRemoveCategoryCodes
	 */
	public List<ScQtCategory> getTobeRemoveCategoryCodes() {
		return tobeRemoveCategoryCodes;
	}

	/**
	 * @param tobeRemoveCategoryCodes the tobeRemoveCategoryCodes to set
	 */
	public void setTobeRemoveCategoryCodes(
			List<ScQtCategory> tobeRemoveCategoryCodes) {
		this.tobeRemoveCategoryCodes = tobeRemoveCategoryCodes;
	}

	public boolean isSpecItemWeightageRequired() {
		return isSpecItemWeightageRequired;
	}

	public void setSpecItemWeightageRequired(boolean isSpecItemWeightageRequired) {
		this.isSpecItemWeightageRequired = isSpecItemWeightageRequired;
	}

	/**
	 * @return the ministryRemarksSize
	 */
	public int getMinistryRemarksSize() {
		return ministryRemarksSize;
	}

	/**
	 * @param ministryRemarksSize the ministryRemarksSize to set
	 */
	public void setMinistryRemarksSize(int ministryRemarksSize) {
		this.ministryRemarksSize = ministryRemarksSize;
	}

	/**
	 * @return the selectedFlag
	 */
	public boolean isSelectedFlag() {
		return selectedFlag;
	}

	/**
	 * @param selectedFlag the selectedFlag to set
	 */
	public void setSelectedFlag(boolean selectedFlag) {
		this.selectedFlag = selectedFlag;
	}

	/**
	 * @return the isTechnicalUserScoringSchema
	 */
	public boolean isTechnicalUserScoringSchema() {
		return isTechnicalUserScoringSchema;
	}

	/**
	 * @param isTechnicalUserScoringSchema the isTechnicalUserScoringSchema to set
	 */
	public void setTechnicalUserScoringSchema(boolean isTechnicalUserScoringSchema) {
		this.isTechnicalUserScoringSchema = isTechnicalUserScoringSchema;
	}

	/**
	 * @return the isFinancialUserScoringSchema
	 */
	public boolean isFinancialUserScoringSchema() {
		return isFinancialUserScoringSchema;
	}

	/**
	 * @param isFinancialUserScoringSchema the isFinancialUserScoringSchema to set
	 */
	public void setFinancialUserScoringSchema(boolean isFinancialUserScoringSchema) {
		this.isFinancialUserScoringSchema = isFinancialUserScoringSchema;
	}

}
