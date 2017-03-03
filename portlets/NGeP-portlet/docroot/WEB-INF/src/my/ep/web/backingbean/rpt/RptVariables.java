package my.ep.web.backingbean.rpt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.rpt.model.CTRptViewModel;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.util.FacesScopes;

public class RptVariables extends RptGenerateBean{

	protected UserProfilesSearchBean userProfileSearchBean;
	
	protected List<PmRoleDesc> roleDescResults;
	protected boolean  disabledOrgFields=false;
	protected PmOrgProfile currentActiveOrg;
	protected PmRoleDesc selectedRoleDesc;
	protected String reportTitle;
	protected String reportUrlValue;
	protected boolean requiredForReport;
	boolean errorValue=false;
	protected SmSupplier smSupplier;
	protected String estimatedAmount;
	protected String reportStr;
	protected HashMap<String, String> monthMap = new HashMap<String, String>();
	protected List<ScLoaReport> loaReportDocumentList;
	protected HashMap<String,String> lovValuesMap=new HashMap<String,String>();
	protected HashMap<String,String> lovNewValuesMap=new HashMap<String,String>();
	protected ScLoa scLoa;
	//protected PmStatus pmStatus;
	protected List<ScLoaReport> scLoaReport;
	protected Map<Long, List<ScLoiLoa>> suppLoiLoaMap;
	protected ScQt scQt;
	protected Long scQtId;
	protected String moduleNameValue;
	protected PmOrgProfile selectedMinistry;
	protected PmOrgProfile selectedPegwaiPengawal;
	protected PmOrgProfile selectedKumpulanPTJ;
	protected PmOrgProfile selectedPTJ;
	protected PmOrgProfile selectedOrgProfile;
	protected PmRoleDesc selectedPmRoleDesc;
	
	protected PmUser selectedPmUser;
	protected CmItem selectedItem;
	protected CmUnspsc selectedUnspscItem;
	protected PmParameter selectedOrgType;
	protected Long ptjId = null;
	protected String[] searchValue = new String[3];
	protected String[] searchFieldValue = new String[3];
	protected String[] searchOperatorValue = new String[3];
	protected CmUnspsc selectedLOV;
	protected List<SelectItem> searchField;
	protected List<SelectItem> searchOperator;
	protected String requestId;
	protected String jobId;
	
	protected Long userId;
	protected String strLanguage;
	
	protected String reportValue;
	protected String moduleValue;
	protected String reportName;
	protected String reportCategory;
	protected String reportCode;
	protected String reportJsp;
	protected String stringZone;
	
	protected List<SelectItem> reportCategoryList = new ArrayList<SelectItem>();
	protected List<SelectItem> moduleList;
	
	protected List<SelectItem> regTypeList;
	protected List<String> authoriztionList = new ArrayList<String>();
	protected List<String> orgProfileList = new ArrayList<String>();
	
	protected List<String> scYearList = new ArrayList<String>();
	protected List<SelectItem> orgProfileIdNameList = new ArrayList<SelectItem>();
	protected List<SelectItem> userListbyprofileRole = new ArrayList<SelectItem>();
	
	protected List<SelectItem> stateLst = new ArrayList<SelectItem>();
	protected List<SelectItem> smStateList = new ArrayList<SelectItem>();
	protected List<SelectItem> districtLst = new ArrayList<SelectItem>();
	
	protected List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();
	protected List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	protected List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();
	protected List<SelectItem> procurementList = new ArrayList<SelectItem>();
	
	protected String[] scProcureAllList = null;
	protected String[] scQuotationList = null;
	protected String[] scTenderList = null;
	protected String[] scRstrctdTndrList = null;
	
	protected List<SelectItem> scMonthList = new ArrayList<SelectItem>();
	protected List<SelectItem> requisitionerList = new ArrayList<SelectItem>();
	protected List<SelectItem> statusList = new ArrayList<SelectItem>();
	protected List<SelectItem> ministryList = new ArrayList<SelectItem>();
	protected List<SelectItem> pegaWaiPengawalList = new ArrayList<SelectItem>();
	protected List<SelectItem> kumpulanPTJList = new ArrayList<SelectItem>();
	protected List<SelectItem> ptjList = new ArrayList<SelectItem>();
	protected List<SelectItem> pmMinistryList = new ArrayList<SelectItem>();
	protected List<String> pmPtjList = new ArrayList<String>();
	protected List<SelectItem> processingOfficerList = new ArrayList<SelectItem>();
	protected List<String> loanList = new ArrayList<String>();
	protected List<SelectItem> unspcList = new ArrayList<SelectItem>();
	protected SelectItem[] itemsList = null;
	protected List<SelectItem> brandList = new ArrayList<SelectItem>();
	protected List<SelectItem> extTypeList = new ArrayList<SelectItem>();
	protected List<SelectItem> measurmentList = new ArrayList<SelectItem>();
	protected List<SelectItem> colorList = new ArrayList<SelectItem>();
	protected List<SelectItem> approverNayakTarafList = new ArrayList<SelectItem>();
	protected List<SelectItem> scPtjList = new ArrayList<SelectItem>();
	protected List<SelectItem> scRequisitionerList = new ArrayList<SelectItem>();
	protected List<SelectItem> scDescOfficerList = new ArrayList<SelectItem>();
	protected List<SelectItem> smUPSSVOUserList = new ArrayList<SelectItem>();
	protected List<SelectItem> ctSubTypeList = new ArrayList<SelectItem>();
	
	protected List<SelectItem> zoneList = new ArrayList<SelectItem>();
	protected List<String> paramList = new ArrayList<String>();
	protected List<String> highRoleCodeList = new ArrayList<String>();
	protected List<String> allRoleCodesList = new ArrayList<String>();
	protected List<BigDecimal> isByZonelist = new ArrayList<BigDecimal>();
	protected List<String> paramTableRoleCodeList = new ArrayList<String>();
	protected List<SelectItem> rolecodeRquisitionerList = new ArrayList<SelectItem>();
	protected List<SelectItem> rolecodeApproverList = new ArrayList<SelectItem>();
	protected List<SelectItem> rolecodeAcknowledgeOfficerList = new ArrayList<SelectItem>();
	protected List<SelectItem> rolecodePayOfficerList = new ArrayList<SelectItem>();
	protected List<SelectItem> rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
	protected List<SelectItem> identificationTypeList = new ArrayList<SelectItem>();
	protected List<SelectItem> accumulatedPaidUpCptlList = new ArrayList<SelectItem>();
	protected List<SelectItem> registrationStatusList = new ArrayList<SelectItem>();
	protected List<SelectItem> registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
	protected List<SelectItem> smBusCompanyTypeList = new ArrayList<SelectItem>();
	protected List<String> simpleQuoteNoList = new ArrayList<String>();
	protected List<SelectItem> serviceTypeList = new ArrayList<SelectItem>();
	protected List<String> paymentMethodList = new ArrayList<String>();
	protected List<SelectItem> paymentTypeList = new ArrayList<SelectItem>();
	protected List<SelectItem> scSOAStatusList = new ArrayList<SelectItem>();
	
	protected String parameterCodeType = new String();

	protected List<SelectItem> reportList;

	boolean isCategoryView;
	
	protected CTRptViewModel ctRptViewModel;
	
	protected String reportFileNameJSP;

	protected Long unspid = null;
	protected Long ministryId = null;
	boolean valuesEmpty = false;
	protected List<String> roleCodeList = new ArrayList<String>();
	protected List<String> supplCompanyList = new ArrayList<String>();
	protected boolean valueReqd;
	protected boolean mainCatValueReqd;
	protected boolean subCatValueReqd;
	protected boolean subCatDivisionValueReqd;
	protected boolean showParentMsg;
	protected List<SmSupplierCategory> selectedCatList;
	protected List<SmSupplierCategory> selectCatDeletionList;
	protected Map<String, List<SelectItem>> moduleItemMap = new HashMap<String, List<SelectItem>>();
	protected SelectItem[] items = null;
	protected SelectItem[] docTypeStatusItems = null;
	protected List<SelectItem> docTypeStatusItemsList = new ArrayList<SelectItem>();
	protected List<SelectItem> docTypeTempStatusItemsList = new ArrayList<SelectItem>();
	protected String localToDateVar;
	protected Long lngUserId = new FacesScopes().getUserSessionBean().getUserId();
	protected String roleCode;
	protected Date mofAccountEndDateFromDate;
	protected Date mofAccountEndDateToDate;
	protected Date fromDate;
	protected Date toDate;
	protected String subModule;
	protected String commonPtj;
	protected String commonMinistry;
	protected String commonPegaWaiPengawal;
	protected String commonKumpulanPTJ;
	protected String ministryName;
	
	protected String scQuotationId;
	protected String scEstimatedAmount;
	protected String scQuotationNo;
	protected String scPaymentId;
	protected String scCreatedToYear;
	protected String scCreatedToMonth;
	protected String scCreatedFromYear;
	protected String scCreatedFromMonth;
	protected String scDeskOfficer;
	protected String scPtj;
	protected String scStatus;
	protected Date scPublishFromDate;
	protected Date scPublishToDate;
	protected Date scSoneCrtdFromDate;
	protected Date scSoneCrtdToDate;
	protected String scRequisenerId;
	protected String scSupplier;
	protected String scLogin;
	protected String scPIno;
	protected String scSoneStatus;
	protected String scPrcRptGroup;
	protected String scPIDtlsPrcRptGroup;
	protected String scProcurementType;
	protected String scProcessRptQuotationNo;
	protected String scCommonStatus;
	protected String scLAPRNPURCHDEFAULTGroup;
	protected String scREQUESTNOTESUMMARYGroup;
	protected String scPIDNo;
	protected String scContractOrPKPType;
	protected String scContractOrPKPNo;
	protected String scContractOrPKPName;
	protected String scRequestQuoteNo;
	protected String scRequestNoteStatus;
	protected String scSimpleQuoteStatus;
	protected String scSimpleQuoteDtlGroup;
	protected String scPurchaseInDefaultStatus;
	protected String scSOAStatus;
	protected String scSimpleQuoteType;
	protected String scLOANo;
	
	protected String cmItemType;
	protected String cmItemStatus;
	protected String cmUnspscCode;
	protected String cmUnspscTitle;
	protected String cmCompanyName;
	protected String cmRegistrationType;
	protected String cmItemCategory;
	protected String cmItemName;
	protected String cmEpNumber;
	protected String cmBusinessRegNo;
	protected String cmIcNumber;
	protected String cmMofNumber;
	protected String cmExtentionCode;
	protected String cmExtentionType;
	protected String cmExtentionBrand;
	protected String cmExtentionMeasurment;
	protected String cmExtentionColour;
	protected String cmProofIdentity;
	protected String cmSupplOrAdmin;
	protected String cmExtention;
	protected String cmGroupOne;
	protected String cmRequestNumber;
	protected String flItmType;
	protected String flContractNo;
	protected String flContractSystem;
	protected String flPhysicalContractNo;
	protected String flLoaPhysicalNo;
	protected String flContractRequestNo;
	protected String flSst;
	protected String flJabatan;
	protected String flSupplier;
	protected String flStatus;
	protected String flDuration;
	protected String flRecievingOfficer;
	protected String flDeliveryAddress;
	protected String flRecNoteNo;
	protected String flContractType;
	protected String flRequisitioner;
	protected String flAcknowledgeOfficer;
	protected String flPaymentofficer;
	protected String flMtoContract;
	protected String flInvoiceNo;
	protected String flSupplierInvoiceNo;
	protected String flDeductionType;
	protected String flCrNo;
	protected String flApproverName;
	protected String flDocumentType;
	protected String flCriteria;
	protected String flScoring;
	protected String flPkpNo;
	protected String flDeliveryOrderNo;
	protected String flSupplierDeliveryOrderNo;
	protected String flItemCode;
	protected String flPoAmount;
	protected String flPrNo;
	protected String flPmo;
	protected String flItemName;
	protected String flContractOrder;
	protected String flContractRequest;
	protected String flPurchaseOrderNo;
	protected String flGroupOne;
	protected String flGroupTwo;
	protected String flGroupThree;
	protected String flGroupFour;
	protected String flGroupFive;
	protected String flGroupSix;
	protected String flGroupSeven;
	protected String flGroupEight;
	protected String flGroupNine;
	protected String flGroupTen;
	protected String flGroupEleven;
	protected String flGroupTwelve;
	protected String flGroupThirteen;
	protected String flGroupFourteen;
	protected String flGroupFifteen;
	protected String flGroupSixteen;
	protected String flGroupSeventeen;
	
	protected String flStopInstructionNo;
	protected String flPaymentAdviceNo;
	protected String smThresholdFromYear;
	protected String smThresholdToYear;
	protected String smCreatedFromYear;
	protected String smCreatedToYear;
	protected String smMonthlyRptByYear;
	protected String smSVOUser;
	protected String smApplicationNo;
	protected String smZone;
	protected String smSupplierType;
	protected String smMofRegNo;
	protected String smRegistrationStatus;
	protected String smBusCompTypeRegistrationStatus;
	protected String smcategory;
	protected String smMainCategory;
	protected String smSubCategory;
	protected String smSubCategoryDivision;
	protected String smApplicationStatus;
	protected String smCode;
	protected String paymentMethod;
	protected String smPaymentType;
	protected String smApplicationType;
	protected String smState;
	protected String smDistrict;
	protected String smSiteVisitStatus;
	protected String smCompanyName;
	protected String smRegistrationType;
	protected String smYearText;
	protected String smMofRegNoFrom;
	protected String smMofRegNoTo;
	protected String smPaidUpCapitalFrm;
	protected String smPaidUpCapitalTo;
	protected String smBusPaidUpCapitalFrm;
	protected String smBusPaidUpCapitalTo;
	protected String smPaidUpCapitalNaccum;
	protected String smPaymentMethod;
	protected String smProcessingType;
	protected String smReceiptNo;
	protected String smReportType;
	protected String smRTReportType;
	protected String smSupplierName;
	protected List<SelectItem> smMofRegistrationFeeList = new ArrayList<SelectItem>();
	protected List<SelectItem> smSoftCertificateFeeList = new ArrayList<SelectItem>();
	protected List<SelectItem> smProcessingFeeList = new ArrayList<SelectItem>();
	
	protected String smProcessingOfficerName;
	protected String smRole;
	protected String smEpNo;
	protected String smRegistrationNo;
	protected String smSupportingDocumentMode;
	protected String smSupportingDocumentStatus;
	protected String smPersonnelRole;
	protected String smIdentificationType;
	protected String smIdentificationNumber;
	protected String smCancellationBy;
	protected String smBusCompanyType;
	protected String smFromMonth;
	protected String smToMonth;
	protected String smUserName;
	protected String smRequestExpiryRenewalDate;
	protected String smProfileStatus;
	protected String smGSTRegistrationNo;
	protected String smGSTRegisteredStatus;
	protected String smPaymentTo;
	
	protected String ctContractNo;
	protected String ctAgreement;
	protected String ctStatus;
	protected String ctContrMontrStatus;
	protected String ctComplete;
	protected String ctGroupOne;
	protected String ctGroupTwo;
	protected String ctProductService;
	protected String ctLoanNo;
	protected String ctProcurementMode;
	protected String ctPhysicalContractNo;
	protected String ctGroupThree;
	protected String ctStatusTwo;
	protected String ctLOAPhysicalNo;
	protected String ctUserType;
	protected String ctSubType;
	protected String ctTerminationType;
	protected String ctCircularNo;
	protected String ctCircularName;
	protected String ctCircularType;
	protected Date ctCircularEffectiveFromDate;
	protected Date ctCircularEffectiveToDate;
	protected String ctSupplierName;
	
	protected String ppYear;
	protected String ppMinistry;
	protected String ppPTJ;
	
	protected String pmServiceType;
	protected String pmStatus;
	protected String pmFinancialOrgName;
	protected String pmBusinessRegNo;
	protected String pmEpNo;
	protected String pmOrgCategory;
	protected String pmOrgType;
	protected String pmOrgName;
	protected String pmOrgCode;
	protected String pmItemName;
	protected String pmItemCode;
	protected String pmCompanyName;
	protected String pmIdentificationNo;
	protected String pmUserName;
	protected String pmRoleCode;
	protected String pmContractNo;
	protected String pmPhysicalContractNo;
	protected String pmLOANo;
	protected String pmPhysicalLOANo;
	protected String pmZone;
	protected String pmProfileStatus;
	
	
	
	protected boolean selected;
	
	protected List<PmCategoryL1> supplierParentCatList;
	
	boolean testing;

	public UserProfilesSearchBean getUserProfileSearchBean() {
		return userProfileSearchBean;
	}

	public void setUserProfileSearchBean(
			UserProfilesSearchBean userProfileSearchBean) {
		this.userProfileSearchBean = userProfileSearchBean;
	}

	public List<PmRoleDesc> getRoleDescResults() {
		return roleDescResults;
	}

	public void setRoleDescResults(List<PmRoleDesc> roleDescResults) {
		this.roleDescResults = roleDescResults;
	}

	public boolean isDisabledOrgFields() {
		return disabledOrgFields;
	}

	public void setDisabledOrgFields(boolean disabledOrgFields) {
		this.disabledOrgFields = disabledOrgFields;
	}

	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	public PmRoleDesc getSelectedRoleDesc() {
		return selectedRoleDesc;
	}

	public void setSelectedRoleDesc(PmRoleDesc selectedRoleDesc) {
		this.selectedRoleDesc = selectedRoleDesc;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportUrlValue() {
		return reportUrlValue;
	}

	public void setReportUrlValue(String reportUrlValue) {
		this.reportUrlValue = reportUrlValue;
	}

	public boolean isRequiredForReport() {
		return requiredForReport;
	}

	public void setRequiredForReport(boolean requiredForReport) {
		this.requiredForReport = requiredForReport;
	}

	public boolean isErrorValue() {
		return errorValue;
	}

	public void setErrorValue(boolean errorValue) {
		this.errorValue = errorValue;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public String getEstimatedAmount() {
		return estimatedAmount;
	}

	public void setEstimatedAmount(String estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}

	public String getReportStr() {
		return reportStr;
	}

	public void setReportStr(String reportStr) {
		this.reportStr = reportStr;
	}

	public HashMap<String, String> getMonthMap() {
		return monthMap;
	}

	public void setMonthMap(HashMap<String, String> monthMap) {
		this.monthMap = monthMap;
	}

	public List<ScLoaReport> getLoaReportDocumentList() {
		return loaReportDocumentList;
	}

	public void setLoaReportDocumentList(List<ScLoaReport> loaReportDocumentList) {
		this.loaReportDocumentList = loaReportDocumentList;
	}

	public HashMap<String, String> getLovValuesMap() {
		return lovValuesMap;
	}

	public void setLovValuesMap(HashMap<String, String> lovValuesMap) {
		this.lovValuesMap = lovValuesMap;
	}

	public HashMap<String, String> getLovNewValuesMap() {
		return lovNewValuesMap;
	}

	public void setLovNewValuesMap(HashMap<String, String> lovNewValuesMap) {
		this.lovNewValuesMap = lovNewValuesMap;
	}

	public ScLoa getScLoa() {
		return scLoa;
	}

	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}

	public List<ScLoaReport> getScLoaReport() {
		return scLoaReport;
	}

	public void setScLoaReport(List<ScLoaReport> scLoaReport) {
		this.scLoaReport = scLoaReport;
	}

	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}

	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {
		this.suppLoiLoaMap = suppLoiLoaMap;
	}

	public ScQt getScQt() {
		return scQt;
	}

	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	public Long getScQtId() {
		return scQtId;
	}

	public void setScQtId(Long scQtId) {
		this.scQtId = scQtId;
	}

	public String getModuleNameValue() {
		return moduleNameValue;
	}

	public void setModuleNameValue(String moduleNameValue) {
		this.moduleNameValue = moduleNameValue;
	}

	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}

	public PmOrgProfile getSelectedPegwaiPengawal() {
		return selectedPegwaiPengawal;
	}

	public void setSelectedPegwaiPengawal(PmOrgProfile selectedPegwaiPengawal) {
		this.selectedPegwaiPengawal = selectedPegwaiPengawal;
	}

	public PmOrgProfile getSelectedKumpulanPTJ() {
		return selectedKumpulanPTJ;
	}

	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	public PmOrgProfile getSelectedPTJ() {
		return selectedPTJ;
	}

	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public PmRoleDesc getSelectedPmRoleDesc() {
		return selectedPmRoleDesc;
	}

	public void setSelectedPmRoleDesc(PmRoleDesc selectedPmRoleDesc) {
		this.selectedPmRoleDesc = selectedPmRoleDesc;
	}

	public PmUser getSelectedPmUser() {
		return selectedPmUser;
	}

	public void setSelectedPmUser(PmUser selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	public CmItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CmItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public CmUnspsc getSelectedUnspscItem() {
		return selectedUnspscItem;
	}

	public void setSelectedUnspscItem(CmUnspsc selectedUnspscItem) {
		this.selectedUnspscItem = selectedUnspscItem;
	}

	public PmParameter getSelectedOrgType() {
		return selectedOrgType;
	}

	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	public Long getPtjId() {
		return ptjId;
	}

	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	public String[] getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String[] searchValue) {
		this.searchValue = searchValue;
	}

	public String[] getSearchFieldValue() {
		return searchFieldValue;
	}

	public void setSearchFieldValue(String[] searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}

	public String[] getSearchOperatorValue() {
		return searchOperatorValue;
	}

	public void setSearchOperatorValue(String[] searchOperatorValue) {
		this.searchOperatorValue = searchOperatorValue;
	}

	public CmUnspsc getSelectedLOV() {
		return selectedLOV;
	}

	public void setSelectedLOV(CmUnspsc selectedLOV) {
		this.selectedLOV = selectedLOV;
	}

	public List<SelectItem> getSearchField() {
		return searchField;
	}

	public void setSearchField(List<SelectItem> searchField) {
		this.searchField = searchField;
	}

	public List<SelectItem> getSearchOperator() {
		return searchOperator;
	}

	public void setSearchOperator(List<SelectItem> searchOperator) {
		this.searchOperator = searchOperator;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getStrLanguage() {
		return strLanguage;
	}

	public void setStrLanguage(String strLanguage) {
		this.strLanguage = strLanguage;
	}

	public String getReportValue() {
		System.out.println("get reportValue" + reportValue);
		return reportValue;
	}

	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
		System.out.println("get reportValue" + reportValue);
	}

	public String getModuleValue() {
		System.out.println("get moduleValue" + moduleValue);
		return moduleValue;
	}

	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
		System.out.println("set moduleValue" + moduleValue);
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportCategory() {
		
		System.out.println("getreportcategory = "+ reportCategory);
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
		System.out.println("setreportcategory = "+ reportCategory);
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getReportJsp() {
		return reportJsp;
	}

	public void setReportJsp(String reportJsp) {
		this.reportJsp = reportJsp;
	}

	public String getStringZone() {
		return stringZone;
	}

	public void setStringZone(String stringZone) {
		this.stringZone = stringZone;
	}

	public List<SelectItem> getReportCategoryList() {
		return reportCategoryList;
	}

	public void setReportCategoryList(List<SelectItem> reportCategoryList) {
		this.reportCategoryList = reportCategoryList;
	}

	public List<SelectItem> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<SelectItem> moduleList) {
		this.moduleList = moduleList;
	}

	public List<SelectItem> getRegTypeList() {
		return regTypeList;
	}

	public void setRegTypeList(List<SelectItem> regTypeList) {
		this.regTypeList = regTypeList;
	}

	public List<String> getAuthoriztionList() {
		return authoriztionList;
	}

	public void setAuthoriztionList(List<String> authoriztionList) {
		this.authoriztionList = authoriztionList;
	}

	public List<String> getOrgProfileList() {
		return orgProfileList;
	}

	public void setOrgProfileList(List<String> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	public List<String> getScYearList() {
		return scYearList;
	}

	public void setScYearList(List<String> scYearList) {
		this.scYearList = scYearList;
	}

	public List<SelectItem> getOrgProfileIdNameList() {
		return orgProfileIdNameList;
	}

	public void setOrgProfileIdNameList(List<SelectItem> orgProfileIdNameList) {
		this.orgProfileIdNameList = orgProfileIdNameList;
	}

	public List<SelectItem> getUserListbyprofileRole() {
		return userListbyprofileRole;
	}

	public void setUserListbyprofileRole(List<SelectItem> userListbyprofileRole) {
		this.userListbyprofileRole = userListbyprofileRole;
	}

	public List<SelectItem> getStateLst() {
		return stateLst;
	}

	public void setStateLst(List<SelectItem> stateLst) {
		this.stateLst = stateLst;
	}

	public List<SelectItem> getSmStateList() {
		return smStateList;
	}

	public void setSmStateList(List<SelectItem> smStateList) {
		this.smStateList = smStateList;
	}

	public List<SelectItem> getDistrictLst() {
		return districtLst;
	}

	public void setDistrictLst(List<SelectItem> districtLst) {
		this.districtLst = districtLst;
	}

	public List<SelectItem> getLevOneCatLst() {
		return levOneCatLst;
	}

	public void setLevOneCatLst(List<SelectItem> levOneCatLst) {
		this.levOneCatLst = levOneCatLst;
	}

	public List<SelectItem> getLevTwoCatLst() {
		return levTwoCatLst;
	}

	public void setLevTwoCatLst(List<SelectItem> levTwoCatLst) {
		this.levTwoCatLst = levTwoCatLst;
	}

	public List<SelectItem> getLevThreeCatLst() {
		return levThreeCatLst;
	}

	public void setLevThreeCatLst(List<SelectItem> levThreeCatLst) {
		this.levThreeCatLst = levThreeCatLst;
	}

	public List<SelectItem> getProcurementList() {
		return procurementList;
	}

	public void setProcurementList(List<SelectItem> procurementList) {
		this.procurementList = procurementList;
	}

	public String[] getScProcureAllList() {
		return scProcureAllList;
	}

	public void setScProcureAllList(String[] scProcureAllList) {
		this.scProcureAllList = scProcureAllList;
	}

	public String[] getScQuotationList() {
		return scQuotationList;
	}

	public void setScQuotationList(String[] scQuotationList) {
		this.scQuotationList = scQuotationList;
	}

	public String[] getScTenderList() {
		return scTenderList;
	}

	public void setScTenderList(String[] scTenderList) {
		this.scTenderList = scTenderList;
	}

	public String[] getScRstrctdTndrList() {
		return scRstrctdTndrList;
	}

	public void setScRstrctdTndrList(String[] scRstrctdTndrList) {
		this.scRstrctdTndrList = scRstrctdTndrList;
	}

	public List<SelectItem> getScMonthList() {
		return scMonthList;
	}

	public void setScMonthList(List<SelectItem> scMonthList) {
		this.scMonthList = scMonthList;
	}

	public List<SelectItem> getRequisitionerList() {
		return requisitionerList;
	}

	public void setRequisitionerList(List<SelectItem> requisitionerList) {
		this.requisitionerList = requisitionerList;
	}

	public List<SelectItem> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}

	public List<SelectItem> getMinistryList() {
		return ministryList;
	}

	public void setMinistryList(List<SelectItem> ministryList) {
		this.ministryList = ministryList;
	}

	public List<SelectItem> getPegaWaiPengawalList() {
		return pegaWaiPengawalList;
	}

	public void setPegaWaiPengawalList(List<SelectItem> pegaWaiPengawalList) {
		this.pegaWaiPengawalList = pegaWaiPengawalList;
	}

	public List<SelectItem> getKumpulanPTJList() {
		return kumpulanPTJList;
	}

	public void setKumpulanPTJList(List<SelectItem> kumpulanPTJList) {
		this.kumpulanPTJList = kumpulanPTJList;
	}

	public List<SelectItem> getPtjList() {
		return ptjList;
	}

	public void setPtjList(List<SelectItem> ptjList) {
		this.ptjList = ptjList;
	}

	public List<SelectItem> getPmMinistryList() {
		return pmMinistryList;
	}

	public void setPmMinistryList(List<SelectItem> pmMinistryList) {
		this.pmMinistryList = pmMinistryList;
	}

	public List<String> getPmPtjList() {
		return pmPtjList;
	}

	public void setPmPtjList(List<String> pmPtjList) {
		this.pmPtjList = pmPtjList;
	}

	public List<SelectItem> getProcessingOfficerList() {
		return processingOfficerList;
	}

	public void setProcessingOfficerList(List<SelectItem> processingOfficerList) {
		this.processingOfficerList = processingOfficerList;
	}

	public List<String> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<String> loanList) {
		this.loanList = loanList;
	}

	public List<SelectItem> getUnspcList() {
		return unspcList;
	}

	public void setUnspcList(List<SelectItem> unspcList) {
		this.unspcList = unspcList;
	}

	public SelectItem[] getItemsList() {
		return itemsList;
	}

	public void setItemsList(SelectItem[] itemsList) {
		this.itemsList = itemsList;
	}

	public List<SelectItem> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<SelectItem> brandList) {
		this.brandList = brandList;
	}

	public List<SelectItem> getExtTypeList() {
		return extTypeList;
	}

	public void setExtTypeList(List<SelectItem> extTypeList) {
		this.extTypeList = extTypeList;
	}

	public List<SelectItem> getMeasurmentList() {
		return measurmentList;
	}

	public void setMeasurmentList(List<SelectItem> measurmentList) {
		this.measurmentList = measurmentList;
	}

	public List<SelectItem> getColorList() {
		return colorList;
	}

	public void setColorList(List<SelectItem> colorList) {
		this.colorList = colorList;
	}

	public List<SelectItem> getApproverNayakTarafList() {
		return approverNayakTarafList;
	}

	public void setApproverNayakTarafList(List<SelectItem> approverNayakTarafList) {
		this.approverNayakTarafList = approverNayakTarafList;
	}

	public List<SelectItem> getScPtjList() {
		return scPtjList;
	}

	public void setScPtjList(List<SelectItem> scPtjList) {
		this.scPtjList = scPtjList;
	}

	public List<SelectItem> getScRequisitionerList() {
		return scRequisitionerList;
	}

	public void setScRequisitionerList(List<SelectItem> scRequisitionerList) {
		this.scRequisitionerList = scRequisitionerList;
	}

	public List<SelectItem> getScDescOfficerList() {
		return scDescOfficerList;
	}

	public void setScDescOfficerList(List<SelectItem> scDescOfficerList) {
		this.scDescOfficerList = scDescOfficerList;
	}

	public List<SelectItem> getSmUPSSVOUserList() {
		return smUPSSVOUserList;
	}

	public void setSmUPSSVOUserList(List<SelectItem> smUPSSVOUserList) {
		this.smUPSSVOUserList = smUPSSVOUserList;
	}

	public List<SelectItem> getCtSubTypeList() {
		return ctSubTypeList;
	}

	public void setCtSubTypeList(List<SelectItem> ctSubTypeList) {
		this.ctSubTypeList = ctSubTypeList;
	}

	public List<SelectItem> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	public List<String> getHighRoleCodeList() {
		return highRoleCodeList;
	}

	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	public List<String> getAllRoleCodesList() {
		return allRoleCodesList;
	}

	public void setAllRoleCodesList(List<String> allRoleCodesList) {
		this.allRoleCodesList = allRoleCodesList;
	}

	public List<BigDecimal> getIsByZonelist() {
		return isByZonelist;
	}

	public void setIsByZonelist(List<BigDecimal> isByZonelist) {
		this.isByZonelist = isByZonelist;
	}

	public List<String> getParamTableRoleCodeList() {
		return paramTableRoleCodeList;
	}

	public void setParamTableRoleCodeList(List<String> paramTableRoleCodeList) {
		this.paramTableRoleCodeList = paramTableRoleCodeList;
	}

	public List<SelectItem> getRolecodeRquisitionerList() {
		return rolecodeRquisitionerList;
	}

	public void setRolecodeRquisitionerList(
			List<SelectItem> rolecodeRquisitionerList) {
		this.rolecodeRquisitionerList = rolecodeRquisitionerList;
	}

	public List<SelectItem> getRolecodeApproverList() {
		return rolecodeApproverList;
	}

	public void setRolecodeApproverList(List<SelectItem> rolecodeApproverList) {
		this.rolecodeApproverList = rolecodeApproverList;
	}

	public List<SelectItem> getRolecodeAcknowledgeOfficerList() {
		return rolecodeAcknowledgeOfficerList;
	}

	public void setRolecodeAcknowledgeOfficerList(
			List<SelectItem> rolecodeAcknowledgeOfficerList) {
		this.rolecodeAcknowledgeOfficerList = rolecodeAcknowledgeOfficerList;
	}

	public List<SelectItem> getRolecodePayOfficerList() {
		return rolecodePayOfficerList;
	}

	public void setRolecodePayOfficerList(List<SelectItem> rolecodePayOfficerList) {
		this.rolecodePayOfficerList = rolecodePayOfficerList;
	}

	public List<SelectItem> getRolecodeRecievingOfficerList() {
		return rolecodeRecievingOfficerList;
	}

	public void setRolecodeRecievingOfficerList(
			List<SelectItem> rolecodeRecievingOfficerList) {
		this.rolecodeRecievingOfficerList = rolecodeRecievingOfficerList;
	}

	public List<SelectItem> getIdentificationTypeList() {
		return identificationTypeList;
	}

	public void setIdentificationTypeList(List<SelectItem> identificationTypeList) {
		this.identificationTypeList = identificationTypeList;
	}

	public List<SelectItem> getAccumulatedPaidUpCptlList() {
		return accumulatedPaidUpCptlList;
	}

	public void setAccumulatedPaidUpCptlList(
			List<SelectItem> accumulatedPaidUpCptlList) {
		this.accumulatedPaidUpCptlList = accumulatedPaidUpCptlList;
	}

	public List<SelectItem> getRegistrationStatusList() {
		return registrationStatusList;
	}

	public void setRegistrationStatusList(List<SelectItem> registrationStatusList) {
		this.registrationStatusList = registrationStatusList;
	}

	public List<SelectItem> getRegistrationStatusListOfBusCompType() {
		return registrationStatusListOfBusCompType;
	}

	public void setRegistrationStatusListOfBusCompType(
			List<SelectItem> registrationStatusListOfBusCompType) {
		this.registrationStatusListOfBusCompType = registrationStatusListOfBusCompType;
	}

	public List<SelectItem> getSmBusCompanyTypeList() {
		return smBusCompanyTypeList;
	}

	public void setSmBusCompanyTypeList(List<SelectItem> smBusCompanyTypeList) {
		this.smBusCompanyTypeList = smBusCompanyTypeList;
	}

	public List<String> getSimpleQuoteNoList() {
		return simpleQuoteNoList;
	}

	public void setSimpleQuoteNoList(List<String> simpleQuoteNoList) {
		this.simpleQuoteNoList = simpleQuoteNoList;
	}

	public List<SelectItem> getServiceTypeList() {
		return serviceTypeList;
	}

	public void setServiceTypeList(List<SelectItem> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	public List<String> getPaymentMethodList() {
		return paymentMethodList;
	}

	public void setPaymentMethodList(List<String> paymentMethodList) {
		this.paymentMethodList = paymentMethodList;
	}

	public List<SelectItem> getPaymentTypeList() {
		return paymentTypeList;
	}

	public void setPaymentTypeList(List<SelectItem> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	public List<SelectItem> getScSOAStatusList() {
		return scSOAStatusList;
	}

	public void setScSOAStatusList(List<SelectItem> scSOAStatusList) {
		this.scSOAStatusList = scSOAStatusList;
	}

	public String getParameterCodeType() {
		return parameterCodeType;
	}

	public void setParameterCodeType(String parameterCodeType) {
		this.parameterCodeType = parameterCodeType;
	}

	public List<SelectItem> getReportList() {
		return reportList;
	}

	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	public boolean isCategoryView() {
		return isCategoryView;
	}

	public void setCategoryView(boolean isCategoryView) {
		this.isCategoryView = isCategoryView;
	}

	public CTRptViewModel getCtRptViewModel() {
		return ctRptViewModel;
	}

	public void setCtRptViewModel(CTRptViewModel ctRptViewModel) {
		this.ctRptViewModel = ctRptViewModel;
	}

	public String getReportFileNameJSP() {
		return reportFileNameJSP;
	}

	public void setReportFileNameJSP(String reportFileNameJSP) {
		this.reportFileNameJSP = reportFileNameJSP;
	}

	public Long getUnspid() {
		return unspid;
	}

	public void setUnspid(Long unspid) {
		this.unspid = unspid;
	}

	public Long getMinistryId() {
		return ministryId;
	}

	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}

	public boolean isValuesEmpty() {
		return valuesEmpty;
	}

	public void setValuesEmpty(boolean valuesEmpty) {
		this.valuesEmpty = valuesEmpty;
	}

	public List<String> getRoleCodeList() {
		return roleCodeList;
	}

	public void setRoleCodeList(List<String> roleCodeList) {
		this.roleCodeList = roleCodeList;
	}

	public List<String> getSupplCompanyList() {
		return supplCompanyList;
	}

	public void setSupplCompanyList(List<String> supplCompanyList) {
		this.supplCompanyList = supplCompanyList;
	}

	public boolean isValueReqd() {
		return valueReqd;
	}

	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	public boolean isMainCatValueReqd() {
		return mainCatValueReqd;
	}

	public void setMainCatValueReqd(boolean mainCatValueReqd) {
		this.mainCatValueReqd = mainCatValueReqd;
	}

	public boolean isSubCatValueReqd() {
		return subCatValueReqd;
	}

	public void setSubCatValueReqd(boolean subCatValueReqd) {
		this.subCatValueReqd = subCatValueReqd;
	}

	public boolean isSubCatDivisionValueReqd() {
		return subCatDivisionValueReqd;
	}

	public void setSubCatDivisionValueReqd(boolean subCatDivisionValueReqd) {
		this.subCatDivisionValueReqd = subCatDivisionValueReqd;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public List<SmSupplierCategory> getSelectedCatList() {
		return selectedCatList;
	}

	public void setSelectedCatList(List<SmSupplierCategory> selectedCatList) {
		this.selectedCatList = selectedCatList;
	}

	public List<SmSupplierCategory> getSelectCatDeletionList() {
		return selectCatDeletionList;
	}

	public void setSelectCatDeletionList(
			List<SmSupplierCategory> selectCatDeletionList) {
		this.selectCatDeletionList = selectCatDeletionList;
	}

	public Map<String, List<SelectItem>> getModuleItemMap() {
		return moduleItemMap;
	}

	public void setModuleItemMap(Map<String, List<SelectItem>> moduleItemMap) {
		this.moduleItemMap = moduleItemMap;
	}

	public SelectItem[] getItems() {
		return items;
	}

	public void setItems(SelectItem[] items) {
		this.items = items;
	}

	public SelectItem[] getDocTypeStatusItems() {
		return docTypeStatusItems;
	}

	public void setDocTypeStatusItems(SelectItem[] docTypeStatusItems) {
		this.docTypeStatusItems = docTypeStatusItems;
	}

	public List<SelectItem> getDocTypeStatusItemsList() {
		return docTypeStatusItemsList;
	}

	public void setDocTypeStatusItemsList(List<SelectItem> docTypeStatusItemsList) {
		this.docTypeStatusItemsList = docTypeStatusItemsList;
	}

	public List<SelectItem> getDocTypeTempStatusItemsList() {
		return docTypeTempStatusItemsList;
	}

	public void setDocTypeTempStatusItemsList(
			List<SelectItem> docTypeTempStatusItemsList) {
		this.docTypeTempStatusItemsList = docTypeTempStatusItemsList;
	}

	public String getLocalToDateVar() {
		return localToDateVar;
	}

	public void setLocalToDateVar(String localToDateVar) {
		this.localToDateVar = localToDateVar;
	}

	public long getLngUserId() {
		return lngUserId;
	}

	public void setLngUserId(Long lngUserId) {
		this.lngUserId = lngUserId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Date getMofAccountEndDateFromDate() {
		return mofAccountEndDateFromDate;
	}

	public void setMofAccountEndDateFromDate(Date mofAccountEndDateFromDate) {
		this.mofAccountEndDateFromDate = mofAccountEndDateFromDate;
	}

	public Date getMofAccountEndDateToDate() {
		return mofAccountEndDateToDate;
	}

	public void setMofAccountEndDateToDate(Date mofAccountEndDateToDate) {
		this.mofAccountEndDateToDate = mofAccountEndDateToDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getSubModule() {
		return subModule;
	}

	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	public String getCommonPtj() {
		return commonPtj;
	}

	public void setCommonPtj(String commonPtj) {
		this.commonPtj = commonPtj;
	}

	public String getCommonMinistry() {
		return commonMinistry;
	}

	public void setCommonMinistry(String commonMinistry) {
		this.commonMinistry = commonMinistry;
	}

	public String getCommonPegaWaiPengawal() {
		return commonPegaWaiPengawal;
	}

	public void setCommonPegaWaiPengawal(String commonPegaWaiPengawal) {
		this.commonPegaWaiPengawal = commonPegaWaiPengawal;
	}

	public String getCommonKumpulanPTJ() {
		return commonKumpulanPTJ;
	}

	public void setCommonKumpulanPTJ(String commonKumpulanPTJ) {
		this.commonKumpulanPTJ = commonKumpulanPTJ;
	}

	public String getMinistryName() {
		return ministryName;
	}

	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	public String getScQuotationId() {
		return scQuotationId;
	}

	public void setScQuotationId(String scQuotationId) {
		this.scQuotationId = scQuotationId;
	}

	public String getScEstimatedAmount() {
		return scEstimatedAmount;
	}

	public void setScEstimatedAmount(String scEstimatedAmount) {
		this.scEstimatedAmount = scEstimatedAmount;
	}

	public String getScQuotationNo() {
		return scQuotationNo;
	}

	public void setScQuotationNo(String scQuotationNo) {
		this.scQuotationNo = scQuotationNo;
	}

	public String getScPaymentId() {
		return scPaymentId;
	}

	public void setScPaymentId(String scPaymentId) {
		this.scPaymentId = scPaymentId;
	}

	public String getScCreatedToYear() {
		return scCreatedToYear;
	}

	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}

	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}

	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}

	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}

	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}

	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}

	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}

	public String getScDeskOfficer() {
		return scDeskOfficer;
	}

	public void setScDeskOfficer(String scDeskOfficer) {
		this.scDeskOfficer = scDeskOfficer;
	}

	public String getScPtj() {
		return scPtj;
	}

	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}

	public String getScStatus() {
		return scStatus;
	}

	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}

	public Date getScPublishFromDate() {
		return scPublishFromDate;
	}

	public void setScPublishFromDate(Date scPublishFromDate) {
		this.scPublishFromDate = scPublishFromDate;
	}

	public Date getScPublishToDate() {
		return scPublishToDate;
	}

	public void setScPublishToDate(Date scPublishToDate) {
		this.scPublishToDate = scPublishToDate;
	}

	public Date getScSoneCrtdFromDate() {
		return scSoneCrtdFromDate;
	}

	public void setScSoneCrtdFromDate(Date scSoneCrtdFromDate) {
		this.scSoneCrtdFromDate = scSoneCrtdFromDate;
	}

	public Date getScSoneCrtdToDate() {
		return scSoneCrtdToDate;
	}

	public void setScSoneCrtdToDate(Date scSoneCrtdToDate) {
		this.scSoneCrtdToDate = scSoneCrtdToDate;
	}

	public String getScRequisenerId() {
		return scRequisenerId;
	}

	public void setScRequisenerId(String scRequisenerId) {
		this.scRequisenerId = scRequisenerId;
	}

	public String getScSupplier() {
		return scSupplier;
	}

	public void setScSupplier(String scSupplier) {
		this.scSupplier = scSupplier;
	}

	public String getScLogin() {
		return scLogin;
	}

	public void setScLogin(String scLogin) {
		this.scLogin = scLogin;
	}

	public String getScPIno() {
		return scPIno;
	}

	public void setScPIno(String scPIno) {
		this.scPIno = scPIno;
	}

	public String getScSoneStatus() {
		return scSoneStatus;
	}

	public void setScSoneStatus(String scSoneStatus) {
		this.scSoneStatus = scSoneStatus;
	}

	public String getScPrcRptGroup() {
		return scPrcRptGroup;
	}

	public void setScPrcRptGroup(String scPrcRptGroup) {
		this.scPrcRptGroup = scPrcRptGroup;
	}

	public String getScPIDtlsPrcRptGroup() {
		return scPIDtlsPrcRptGroup;
	}

	public void setScPIDtlsPrcRptGroup(String scPIDtlsPrcRptGroup) {
		this.scPIDtlsPrcRptGroup = scPIDtlsPrcRptGroup;
	}

	public String getScProcurementType() {
		return scProcurementType;
	}

	public void setScProcurementType(String scProcurementType) {
		this.scProcurementType = scProcurementType;
	}

	public String getScProcessRptQuotationNo() {
		return scProcessRptQuotationNo;
	}

	public void setScProcessRptQuotationNo(String scProcessRptQuotationNo) {
		this.scProcessRptQuotationNo = scProcessRptQuotationNo;
	}

	public String getScCommonStatus() {
		return scCommonStatus;
	}

	public void setScCommonStatus(String scCommonStatus) {
		this.scCommonStatus = scCommonStatus;
	}

	public String getScLAPRNPURCHDEFAULTGroup() {
		return scLAPRNPURCHDEFAULTGroup;
	}

	public void setScLAPRNPURCHDEFAULTGroup(String scLAPRNPURCHDEFAULTGroup) {
		this.scLAPRNPURCHDEFAULTGroup = scLAPRNPURCHDEFAULTGroup;
	}

	public String getScREQUESTNOTESUMMARYGroup() {
		return scREQUESTNOTESUMMARYGroup;
	}

	public void setScREQUESTNOTESUMMARYGroup(String scREQUESTNOTESUMMARYGroup) {
		this.scREQUESTNOTESUMMARYGroup = scREQUESTNOTESUMMARYGroup;
	}

	public String getScPIDNo() {
		return scPIDNo;
	}

	public void setScPIDNo(String scPIDNo) {
		this.scPIDNo = scPIDNo;
	}

	public String getScContractOrPKPType() {
		return scContractOrPKPType;
	}

	public void setScContractOrPKPType(String scContractOrPKPType) {
		this.scContractOrPKPType = scContractOrPKPType;
	}

	public String getScContractOrPKPNo() {
		return scContractOrPKPNo;
	}

	public void setScContractOrPKPNo(String scContractOrPKPNo) {
		this.scContractOrPKPNo = scContractOrPKPNo;
	}

	public String getScContractOrPKPName() {
		return scContractOrPKPName;
	}

	public void setScContractOrPKPName(String scContractOrPKPName) {
		this.scContractOrPKPName = scContractOrPKPName;
	}

	public String getScRequestQuoteNo() {
		return scRequestQuoteNo;
	}

	public void setScRequestQuoteNo(String scRequestQuoteNo) {
		this.scRequestQuoteNo = scRequestQuoteNo;
	}

	public String getScRequestNoteStatus() {
		return scRequestNoteStatus;
	}

	public void setScRequestNoteStatus(String scRequestNoteStatus) {
		this.scRequestNoteStatus = scRequestNoteStatus;
	}

	public String getScSimpleQuoteStatus() {
		return scSimpleQuoteStatus;
	}

	public void setScSimpleQuoteStatus(String scSimpleQuoteStatus) {
		this.scSimpleQuoteStatus = scSimpleQuoteStatus;
	}

	public String getScSimpleQuoteDtlGroup() {
		return scSimpleQuoteDtlGroup;
	}

	public void setScSimpleQuoteDtlGroup(String scSimpleQuoteDtlGroup) {
		this.scSimpleQuoteDtlGroup = scSimpleQuoteDtlGroup;
	}

	public String getScPurchaseInDefaultStatus() {
		return scPurchaseInDefaultStatus;
	}

	public void setScPurchaseInDefaultStatus(String scPurchaseInDefaultStatus) {
		this.scPurchaseInDefaultStatus = scPurchaseInDefaultStatus;
	}

	public String getScSOAStatus() {
		return scSOAStatus;
	}

	public void setScSOAStatus(String scSOAStatus) {
		this.scSOAStatus = scSOAStatus;
	}

	public String getScSimpleQuoteType() {
		return scSimpleQuoteType;
	}

	public void setScSimpleQuoteType(String scSimpleQuoteType) {
		this.scSimpleQuoteType = scSimpleQuoteType;
	}

	public String getScLOANo() {
		return scLOANo;
	}

	public void setScLOANo(String scLOANo) {
		this.scLOANo = scLOANo;
	}

	public String getCmItemType() {
		return cmItemType;
	}

	public void setCmItemType(String cmItemType) {
		this.cmItemType = cmItemType;
	}

	public String getCmItemStatus() {
		return cmItemStatus;
	}

	public void setCmItemStatus(String cmItemStatus) {
		this.cmItemStatus = cmItemStatus;
	}

	public String getCmUnspscCode() {
		return cmUnspscCode;
	}

	public void setCmUnspscCode(String cmUnspscCode) {
		this.cmUnspscCode = cmUnspscCode;
	}

	public String getCmUnspscTitle() {
		return cmUnspscTitle;
	}

	public void setCmUnspscTitle(String cmUnspscTitle) {
		this.cmUnspscTitle = cmUnspscTitle;
	}

	public String getCmCompanyName() {
		return cmCompanyName;
	}

	public void setCmCompanyName(String cmCompanyName) {
		this.cmCompanyName = cmCompanyName;
	}

	public String getCmRegistrationType() {
		return cmRegistrationType;
	}

	public void setCmRegistrationType(String cmRegistrationType) {
		this.cmRegistrationType = cmRegistrationType;
	}

	public String getCmItemCategory() {
		return cmItemCategory;
	}

	public void setCmItemCategory(String cmItemCategory) {
		this.cmItemCategory = cmItemCategory;
	}

	public String getCmItemName() {
		return cmItemName;
	}

	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	public String getCmEpNumber() {
		return cmEpNumber;
	}

	public void setCmEpNumber(String cmEpNumber) {
		this.cmEpNumber = cmEpNumber;
	}

	public String getCmBusinessRegNo() {
		return cmBusinessRegNo;
	}

	public void setCmBusinessRegNo(String cmBusinessRegNo) {
		this.cmBusinessRegNo = cmBusinessRegNo;
	}

	public String getCmIcNumber() {
		return cmIcNumber;
	}

	public void setCmIcNumber(String cmIcNumber) {
		this.cmIcNumber = cmIcNumber;
	}

	public String getCmMofNumber() {
		return cmMofNumber;
	}

	public void setCmMofNumber(String cmMofNumber) {
		this.cmMofNumber = cmMofNumber;
	}

	public String getCmExtentionCode() {
		return cmExtentionCode;
	}

	public void setCmExtentionCode(String cmExtentionCode) {
		this.cmExtentionCode = cmExtentionCode;
	}

	public String getCmExtentionType() {
		return cmExtentionType;
	}

	public void setCmExtentionType(String cmExtentionType) {
		this.cmExtentionType = cmExtentionType;
	}

	public String getCmExtentionBrand() {
		return cmExtentionBrand;
	}

	public void setCmExtentionBrand(String cmExtentionBrand) {
		this.cmExtentionBrand = cmExtentionBrand;
	}

	public String getCmExtentionMeasurment() {
		return cmExtentionMeasurment;
	}

	public void setCmExtentionMeasurment(String cmExtentionMeasurment) {
		this.cmExtentionMeasurment = cmExtentionMeasurment;
	}

	public String getCmExtentionColour() {
		return cmExtentionColour;
	}

	public void setCmExtentionColour(String cmExtentionColour) {
		this.cmExtentionColour = cmExtentionColour;
	}

	public String getCmProofIdentity() {
		return cmProofIdentity;
	}

	public void setCmProofIdentity(String cmProofIdentity) {
		this.cmProofIdentity = cmProofIdentity;
	}

	public String getCmSupplOrAdmin() {
		return cmSupplOrAdmin;
	}

	public void setCmSupplOrAdmin(String cmSupplOrAdmin) {
		this.cmSupplOrAdmin = cmSupplOrAdmin;
	}

	public String getCmExtention() {
		return cmExtention;
	}

	public void setCmExtention(String cmExtention) {
		this.cmExtention = cmExtention;
	}

	public String getCmGroupOne() {
		return cmGroupOne;
	}

	public void setCmGroupOne(String cmGroupOne) {
		this.cmGroupOne = cmGroupOne;
	}

	public String getCmRequestNumber() {
		return cmRequestNumber;
	}

	public void setCmRequestNumber(String cmRequestNumber) {
		this.cmRequestNumber = cmRequestNumber;
	}

	public String getFlItmType() {
		return flItmType;
	}

	public void setFlItmType(String flItmType) {
		this.flItmType = flItmType;
	}

	public String getFlContractNo() {
		return flContractNo;
	}

	public void setFlContractNo(String flContractNo) {
		this.flContractNo = flContractNo;
	}

	public String getFlContractSystem() {
		return flContractSystem;
	}

	public void setFlContractSystem(String flContractSystem) {
		this.flContractSystem = flContractSystem;
	}

	public String getFlPhysicalContractNo() {
		return flPhysicalContractNo;
	}

	public void setFlPhysicalContractNo(String flPhysicalContractNo) {
		this.flPhysicalContractNo = flPhysicalContractNo;
	}

	public String getFlLoaPhysicalNo() {
		return flLoaPhysicalNo;
	}

	public void setFlLoaPhysicalNo(String flLoaPhysicalNo) {
		this.flLoaPhysicalNo = flLoaPhysicalNo;
	}

	public String getFlContractRequestNo() {
		return flContractRequestNo;
	}

	public void setFlContractRequestNo(String flContractRequestNo) {
		this.flContractRequestNo = flContractRequestNo;
	}

	public String getFlSst() {
		return flSst;
	}

	public void setFlSst(String flSst) {
		this.flSst = flSst;
	}

	public String getFlJabatan() {
		return flJabatan;
	}

	public void setFlJabatan(String flJabatan) {
		this.flJabatan = flJabatan;
	}

	public String getFlSupplier() {
		return flSupplier;
	}

	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}

	public String getFlStatus() {
		return flStatus;
	}

	public void setFlStatus(String flStatus) {
		this.flStatus = flStatus;
	}

	public String getFlDuration() {
		return flDuration;
	}

	public void setFlDuration(String flDuration) {
		this.flDuration = flDuration;
	}

	public String getFlRecievingOfficer() {
		return flRecievingOfficer;
	}

	public void setFlRecievingOfficer(String flRecievingOfficer) {
		this.flRecievingOfficer = flRecievingOfficer;
	}

	public String getFlDeliveryAddress() {
		return flDeliveryAddress;
	}

	public void setFlDeliveryAddress(String flDeliveryAddress) {
		this.flDeliveryAddress = flDeliveryAddress;
	}

	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}

	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
	}

	public String getFlContractType() {
		return flContractType;
	}

	public void setFlContractType(String flContractType) {
		this.flContractType = flContractType;
	}

	public String getFlRequisitioner() {
		return flRequisitioner;
	}

	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}

	public String getFlAcknowledgeOfficer() {
		return flAcknowledgeOfficer;
	}

	public void setFlAcknowledgeOfficer(String flAcknowledgeOfficer) {
		this.flAcknowledgeOfficer = flAcknowledgeOfficer;
	}

	public String getFlPaymentofficer() {
		return flPaymentofficer;
	}

	public void setFlPaymentofficer(String flPaymentofficer) {
		this.flPaymentofficer = flPaymentofficer;
	}

	public String getFlMtoContract() {
		return flMtoContract;
	}

	public void setFlMtoContract(String flMtoContract) {
		this.flMtoContract = flMtoContract;
	}

	public String getFlInvoiceNo() {
		return flInvoiceNo;
	}

	public void setFlInvoiceNo(String flInvoiceNo) {
		this.flInvoiceNo = flInvoiceNo;
	}

	public String getFlSupplierInvoiceNo() {
		return flSupplierInvoiceNo;
	}

	public void setFlSupplierInvoiceNo(String flSupplierInvoiceNo) {
		this.flSupplierInvoiceNo = flSupplierInvoiceNo;
	}

	public String getFlDeductionType() {
		return flDeductionType;
	}

	public void setFlDeductionType(String flDeductionType) {
		this.flDeductionType = flDeductionType;
	}

	public String getFlCrNo() {
		return flCrNo;
	}

	public void setFlCrNo(String flCrNo) {
		this.flCrNo = flCrNo;
	}

	public String getFlApproverName() {
		return flApproverName;
	}

	public void setFlApproverName(String flApproverName) {
		this.flApproverName = flApproverName;
	}

	public String getFlDocumentType() {
		return flDocumentType;
	}

	public void setFlDocumentType(String flDocumentType) {
		this.flDocumentType = flDocumentType;
	}

	public String getFlCriteria() {
		return flCriteria;
	}

	public void setFlCriteria(String flCriteria) {
		this.flCriteria = flCriteria;
	}

	public String getFlScoring() {
		return flScoring;
	}

	public void setFlScoring(String flScoring) {
		this.flScoring = flScoring;
	}

	public String getFlPkpNo() {
		return flPkpNo;
	}

	public void setFlPkpNo(String flPkpNo) {
		this.flPkpNo = flPkpNo;
	}

	public String getFlDeliveryOrderNo() {
		return flDeliveryOrderNo;
	}

	public void setFlDeliveryOrderNo(String flDeliveryOrderNo) {
		this.flDeliveryOrderNo = flDeliveryOrderNo;
	}

	public String getFlSupplierDeliveryOrderNo() {
		return flSupplierDeliveryOrderNo;
	}

	public void setFlSupplierDeliveryOrderNo(String flSupplierDeliveryOrderNo) {
		this.flSupplierDeliveryOrderNo = flSupplierDeliveryOrderNo;
	}

	public String getFlItemCode() {
		return flItemCode;
	}

	public void setFlItemCode(String flItemCode) {
		this.flItemCode = flItemCode;
	}

	public String getFlPoAmount() {
		return flPoAmount;
	}

	public void setFlPoAmount(String flPoAmount) {
		this.flPoAmount = flPoAmount;
	}

	public String getFlPrNo() {
		return flPrNo;
	}

	public void setFlPrNo(String flPrNo) {
		this.flPrNo = flPrNo;
	}

	public String getFlPmo() {
		return flPmo;
	}

	public void setFlPmo(String flPmo) {
		this.flPmo = flPmo;
	}

	public String getFlItemName() {
		return flItemName;
	}

	public void setFlItemName(String flItemName) {
		this.flItemName = flItemName;
	}

	public String getFlContractOrder() {
		return flContractOrder;
	}

	public void setFlContractOrder(String flContractOrder) {
		this.flContractOrder = flContractOrder;
	}

	public String getFlContractRequest() {
		return flContractRequest;
	}

	public void setFlContractRequest(String flContractRequest) {
		this.flContractRequest = flContractRequest;
	}

	public String getFlPurchaseOrderNo() {
		return flPurchaseOrderNo;
	}

	public void setFlPurchaseOrderNo(String flPurchaseOrderNo) {
		this.flPurchaseOrderNo = flPurchaseOrderNo;
	}

	public String getFlGroupOne() {
		return flGroupOne;
	}

	public void setFlGroupOne(String flGroupOne) {
		this.flGroupOne = flGroupOne;
	}

	public String getFlGroupTwo() {
		return flGroupTwo;
	}

	public void setFlGroupTwo(String flGroupTwo) {
		this.flGroupTwo = flGroupTwo;
	}

	public String getFlGroupThree() {
		return flGroupThree;
	}

	public void setFlGroupThree(String flGroupThree) {
		this.flGroupThree = flGroupThree;
	}

	public String getFlGroupFour() {
		return flGroupFour;
	}

	public void setFlGroupFour(String flGroupFour) {
		this.flGroupFour = flGroupFour;
	}

	public String getFlGroupFive() {
		return flGroupFive;
	}

	public void setFlGroupFive(String flGroupFive) {
		this.flGroupFive = flGroupFive;
	}

	public String getFlGroupSix() {
		return flGroupSix;
	}

	public void setFlGroupSix(String flGroupSix) {
		this.flGroupSix = flGroupSix;
	}

	public String getFlGroupSeven() {
		return flGroupSeven;
	}

	public void setFlGroupSeven(String flGroupSeven) {
		this.flGroupSeven = flGroupSeven;
	}

	public String getFlGroupEight() {
		return flGroupEight;
	}

	public void setFlGroupEight(String flGroupEight) {
		this.flGroupEight = flGroupEight;
	}

	public String getFlGroupNine() {
		return flGroupNine;
	}

	public void setFlGroupNine(String flGroupNine) {
		this.flGroupNine = flGroupNine;
	}

	public String getFlGroupTen() {
		return flGroupTen;
	}

	public void setFlGroupTen(String flGroupTen) {
		this.flGroupTen = flGroupTen;
	}

	public String getFlGroupEleven() {
		return flGroupEleven;
	}

	public void setFlGroupEleven(String flGroupEleven) {
		this.flGroupEleven = flGroupEleven;
	}

	public String getFlGroupTwelve() {
		return flGroupTwelve;
	}

	public void setFlGroupTwelve(String flGroupTwelve) {
		this.flGroupTwelve = flGroupTwelve;
	}

	public String getFlGroupThirteen() {
		return flGroupThirteen;
	}

	public void setFlGroupThirteen(String flGroupThirteen) {
		this.flGroupThirteen = flGroupThirteen;
	}

	public String getFlGroupFourteen() {
		return flGroupFourteen;
	}

	public void setFlGroupFourteen(String flGroupFourteen) {
		this.flGroupFourteen = flGroupFourteen;
	}

	public String getFlGroupFifteen() {
		return flGroupFifteen;
	}

	public void setFlGroupFifteen(String flGroupFifteen) {
		this.flGroupFifteen = flGroupFifteen;
	}

	public String getFlGroupSixteen() {
		return flGroupSixteen;
	}

	public void setFlGroupSixteen(String flGroupSixteen) {
		this.flGroupSixteen = flGroupSixteen;
	}

	public String getFlGroupSeventeen() {
		return flGroupSeventeen;
	}

	public void setFlGroupSeventeen(String flGroupSeventeen) {
		this.flGroupSeventeen = flGroupSeventeen;
	}

	public String getFlStopInstructionNo() {
		return flStopInstructionNo;
	}

	public void setFlStopInstructionNo(String flStopInstructionNo) {
		this.flStopInstructionNo = flStopInstructionNo;
	}

	public String getFlPaymentAdviceNo() {
		return flPaymentAdviceNo;
	}

	public void setFlPaymentAdviceNo(String flPaymentAdviceNo) {
		this.flPaymentAdviceNo = flPaymentAdviceNo;
	}

	public String getSmThresholdFromYear() {
		return smThresholdFromYear;
	}

	public void setSmThresholdFromYear(String smThresholdFromYear) {
		this.smThresholdFromYear = smThresholdFromYear;
	}

	public String getSmThresholdToYear() {
		return smThresholdToYear;
	}

	public void setSmThresholdToYear(String smThresholdToYear) {
		this.smThresholdToYear = smThresholdToYear;
	}

	public String getSmCreatedFromYear() {
		return smCreatedFromYear;
	}

	public void setSmCreatedFromYear(String smCreatedFromYear) {
		this.smCreatedFromYear = smCreatedFromYear;
	}

	public String getSmCreatedToYear() {
		return smCreatedToYear;
	}

	public void setSmCreatedToYear(String smCreatedToYear) {
		this.smCreatedToYear = smCreatedToYear;
	}

	public String getSmMonthlyRptByYear() {
		return smMonthlyRptByYear;
	}

	public void setSmMonthlyRptByYear(String smMonthlyRptByYear) {
		this.smMonthlyRptByYear = smMonthlyRptByYear;
	}

	public String getSmSVOUser() {
		return smSVOUser;
	}

	public void setSmSVOUser(String smSVOUser) {
		this.smSVOUser = smSVOUser;
	}

	public String getSmApplicationNo() {
		return smApplicationNo;
	}

	public void setSmApplicationNo(String smApplicationNo) {
		this.smApplicationNo = smApplicationNo;
	}

	public String getSmZone() {
		return smZone;
	}

	public void setSmZone(String smZone) {
		this.smZone = smZone;
	}

	public String getSmSupplierType() {
		return smSupplierType;
	}

	public void setSmSupplierType(String smSupplierType) {
		this.smSupplierType = smSupplierType;
	}

	public String getSmMofRegNo() {
		return smMofRegNo;
	}

	public void setSmMofRegNo(String smMofRegNo) {
		this.smMofRegNo = smMofRegNo;
	}

	public String getSmRegistrationStatus() {
		return smRegistrationStatus;
	}

	public void setSmRegistrationStatus(String smRegistrationStatus) {
		this.smRegistrationStatus = smRegistrationStatus;
	}

	public String getSmBusCompTypeRegistrationStatus() {
		return smBusCompTypeRegistrationStatus;
	}

	public void setSmBusCompTypeRegistrationStatus(
			String smBusCompTypeRegistrationStatus) {
		this.smBusCompTypeRegistrationStatus = smBusCompTypeRegistrationStatus;
	}

	public String getSmcategory() {
		return smcategory;
	}

	public void setSmcategory(String smcategory) {
		this.smcategory = smcategory;
	}

	public String getSmMainCategory() {
		return smMainCategory;
	}

	public void setSmMainCategory(String smMainCategory) {
		this.smMainCategory = smMainCategory;
	}

	public String getSmSubCategory() {
		return smSubCategory;
	}

	public void setSmSubCategory(String smSubCategory) {
		this.smSubCategory = smSubCategory;
	}

	public String getSmSubCategoryDivision() {
		return smSubCategoryDivision;
	}

	public void setSmSubCategoryDivision(String smSubCategoryDivision) {
		this.smSubCategoryDivision = smSubCategoryDivision;
	}

	public String getSmApplicationStatus() {
		return smApplicationStatus;
	}

	public void setSmApplicationStatus(String smApplicationStatus) {
		this.smApplicationStatus = smApplicationStatus;
	}

	public String getSmCode() {
		return smCode;
	}

	public void setSmCode(String smCode) {
		this.smCode = smCode;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getSmPaymentType() {
		return smPaymentType;
	}

	public void setSmPaymentType(String smPaymentType) {
		this.smPaymentType = smPaymentType;
	}

	public String getSmApplicationType() {
		return smApplicationType;
	}

	public void setSmApplicationType(String smApplicationType) {
		this.smApplicationType = smApplicationType;
	}

	public String getSmState() {
		return smState;
	}

	public void setSmState(String smState) {
		this.smState = smState;
	}

	public String getSmDistrict() {
		return smDistrict;
	}

	public void setSmDistrict(String smDistrict) {
		this.smDistrict = smDistrict;
	}

	public String getSmSiteVisitStatus() {
		return smSiteVisitStatus;
	}

	public void setSmSiteVisitStatus(String smSiteVisitStatus) {
		this.smSiteVisitStatus = smSiteVisitStatus;
	}

	public String getSmCompanyName() {
		return smCompanyName;
	}

	public void setSmCompanyName(String smCompanyName) {
		this.smCompanyName = smCompanyName;
	}

	public String getSmRegistrationType() {
		return smRegistrationType;
	}

	public void setSmRegistrationType(String smRegistrationType) {
		this.smRegistrationType = smRegistrationType;
	}

	public String getSmYearText() {
		return smYearText;
	}

	public void setSmYearText(String smYearText) {
		this.smYearText = smYearText;
	}

	public String getSmMofRegNoFrom() {
		return smMofRegNoFrom;
	}

	public void setSmMofRegNoFrom(String smMofRegNoFrom) {
		this.smMofRegNoFrom = smMofRegNoFrom;
	}

	public String getSmMofRegNoTo() {
		return smMofRegNoTo;
	}

	public void setSmMofRegNoTo(String smMofRegNoTo) {
		this.smMofRegNoTo = smMofRegNoTo;
	}

	public String getSmPaidUpCapitalFrm() {
		return smPaidUpCapitalFrm;
	}

	public void setSmPaidUpCapitalFrm(String smPaidUpCapitalFrm) {
		this.smPaidUpCapitalFrm = smPaidUpCapitalFrm;
	}

	public String getSmPaidUpCapitalTo() {
		return smPaidUpCapitalTo;
	}

	public void setSmPaidUpCapitalTo(String smPaidUpCapitalTo) {
		this.smPaidUpCapitalTo = smPaidUpCapitalTo;
	}

	public String getSmBusPaidUpCapitalFrm() {
		return smBusPaidUpCapitalFrm;
	}

	public void setSmBusPaidUpCapitalFrm(String smBusPaidUpCapitalFrm) {
		this.smBusPaidUpCapitalFrm = smBusPaidUpCapitalFrm;
	}

	public String getSmBusPaidUpCapitalTo() {
		return smBusPaidUpCapitalTo;
	}

	public void setSmBusPaidUpCapitalTo(String smBusPaidUpCapitalTo) {
		this.smBusPaidUpCapitalTo = smBusPaidUpCapitalTo;
	}

	public String getSmPaidUpCapitalNaccum() {
		return smPaidUpCapitalNaccum;
	}

	public void setSmPaidUpCapitalNaccum(String smPaidUpCapitalNaccum) {
		this.smPaidUpCapitalNaccum = smPaidUpCapitalNaccum;
	}

	public String getSmPaymentMethod() {
		return smPaymentMethod;
	}

	public void setSmPaymentMethod(String smPaymentMethod) {
		this.smPaymentMethod = smPaymentMethod;
	}

	public String getSmProcessingType() {
		return smProcessingType;
	}

	public void setSmProcessingType(String smProcessingType) {
		this.smProcessingType = smProcessingType;
	}

	public String getSmReceiptNo() {
		return smReceiptNo;
	}

	public void setSmReceiptNo(String smReceiptNo) {
		this.smReceiptNo = smReceiptNo;
	}

	public String getSmReportType() {
		return smReportType;
	}

	public void setSmReportType(String smReportType) {
		this.smReportType = smReportType;
	}

	public String getSmRTReportType() {
		return smRTReportType;
	}

	public void setSmRTReportType(String smRTReportType) {
		this.smRTReportType = smRTReportType;
	}

	public String getSmSupplierName() {
		return smSupplierName;
	}

	public void setSmSupplierName(String smSupplierName) {
		this.smSupplierName = smSupplierName;
	}

	public List<SelectItem> getSmMofRegistrationFeeList() {
		return smMofRegistrationFeeList;
	}

	public void setSmMofRegistrationFeeList(
			List<SelectItem> smMofRegistrationFeeList) {
		this.smMofRegistrationFeeList = smMofRegistrationFeeList;
	}

	public List<SelectItem> getSmSoftCertificateFeeList() {
		return smSoftCertificateFeeList;
	}

	public void setSmSoftCertificateFeeList(
			List<SelectItem> smSoftCertificateFeeList) {
		this.smSoftCertificateFeeList = smSoftCertificateFeeList;
	}

	public List<SelectItem> getSmProcessingFeeList() {
		return smProcessingFeeList;
	}

	public void setSmProcessingFeeList(List<SelectItem> smProcessingFeeList) {
		this.smProcessingFeeList = smProcessingFeeList;
	}

	public String getSmProcessingOfficerName() {
		return smProcessingOfficerName;
	}

	public void setSmProcessingOfficerName(String smProcessingOfficerName) {
		this.smProcessingOfficerName = smProcessingOfficerName;
	}

	public String getSmRole() {
		return smRole;
	}

	public void setSmRole(String smRole) {
		this.smRole = smRole;
	}

	public String getSmEpNo() {
		return smEpNo;
	}

	public void setSmEpNo(String smEpNo) {
		this.smEpNo = smEpNo;
	}

	public String getSmRegistrationNo() {
		return smRegistrationNo;
	}

	public void setSmRegistrationNo(String smRegistrationNo) {
		this.smRegistrationNo = smRegistrationNo;
	}

	public String getSmSupportingDocumentMode() {
		return smSupportingDocumentMode;
	}

	public void setSmSupportingDocumentMode(String smSupportingDocumentMode) {
		this.smSupportingDocumentMode = smSupportingDocumentMode;
	}

	public String getSmSupportingDocumentStatus() {
		return smSupportingDocumentStatus;
	}

	public void setSmSupportingDocumentStatus(String smSupportingDocumentStatus) {
		this.smSupportingDocumentStatus = smSupportingDocumentStatus;
	}

	public String getSmPersonnelRole() {
		return smPersonnelRole;
	}

	public void setSmPersonnelRole(String smPersonnelRole) {
		this.smPersonnelRole = smPersonnelRole;
	}

	public String getSmIdentificationType() {
		return smIdentificationType;
	}

	public void setSmIdentificationType(String smIdentificationType) {
		this.smIdentificationType = smIdentificationType;
	}

	public String getSmIdentificationNumber() {
		return smIdentificationNumber;
	}

	public void setSmIdentificationNumber(String smIdentificationNumber) {
		this.smIdentificationNumber = smIdentificationNumber;
	}

	public String getSmCancellationBy() {
		return smCancellationBy;
	}

	public void setSmCancellationBy(String smCancellationBy) {
		this.smCancellationBy = smCancellationBy;
	}

	public String getSmBusCompanyType() {
		return smBusCompanyType;
	}

	public void setSmBusCompanyType(String smBusCompanyType) {
		this.smBusCompanyType = smBusCompanyType;
	}

	public String getSmFromMonth() {
		return smFromMonth;
	}

	public void setSmFromMonth(String smFromMonth) {
		this.smFromMonth = smFromMonth;
	}

	public String getSmToMonth() {
		return smToMonth;
	}

	public void setSmToMonth(String smToMonth) {
		this.smToMonth = smToMonth;
	}

	public String getSmUserName() {
		return smUserName;
	}

	public void setSmUserName(String smUserName) {
		this.smUserName = smUserName;
	}

	public String getSmRequestExpiryRenewalDate() {
		return smRequestExpiryRenewalDate;
	}

	public void setSmRequestExpiryRenewalDate(String smRequestExpiryRenewalDate) {
		this.smRequestExpiryRenewalDate = smRequestExpiryRenewalDate;
	}

	public String getSmProfileStatus() {
		return smProfileStatus;
	}

	public void setSmProfileStatus(String smProfileStatus) {
		this.smProfileStatus = smProfileStatus;
	}

	public String getSmGSTRegistrationNo() {
		return smGSTRegistrationNo;
	}

	public void setSmGSTRegistrationNo(String smGSTRegistrationNo) {
		this.smGSTRegistrationNo = smGSTRegistrationNo;
	}

	public String getSmGSTRegisteredStatus() {
		return smGSTRegisteredStatus;
	}

	public void setSmGSTRegisteredStatus(String smGSTRegisteredStatus) {
		this.smGSTRegisteredStatus = smGSTRegisteredStatus;
	}

	public String getSmPaymentTo() {
		return smPaymentTo;
	}

	public void setSmPaymentTo(String smPaymentTo) {
		this.smPaymentTo = smPaymentTo;
	}

	public String getCtContractNo() {
		return ctContractNo;
	}

	public void setCtContractNo(String ctContractNo) {
		this.ctContractNo = ctContractNo;
	}

	public String getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(String ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	public String getCtStatus() {
		return ctStatus;
	}

	public void setCtStatus(String ctStatus) {
		this.ctStatus = ctStatus;
	}

	public String getCtContrMontrStatus() {
		return ctContrMontrStatus;
	}

	public void setCtContrMontrStatus(String ctContrMontrStatus) {
		this.ctContrMontrStatus = ctContrMontrStatus;
	}

	public String getCtComplete() {
		return ctComplete;
	}

	public void setCtComplete(String ctComplete) {
		this.ctComplete = ctComplete;
	}

	public String getCtGroupOne() {
		return ctGroupOne;
	}

	public void setCtGroupOne(String ctGroupOne) {
		this.ctGroupOne = ctGroupOne;
	}

	public String getCtGroupTwo() {
		return ctGroupTwo;
	}

	public void setCtGroupTwo(String ctGroupTwo) {
		this.ctGroupTwo = ctGroupTwo;
	}

	public String getCtProductService() {
		return ctProductService;
	}

	public void setCtProductService(String ctProductService) {
		this.ctProductService = ctProductService;
	}

	public String getCtLoanNo() {
		return ctLoanNo;
	}

	public void setCtLoanNo(String ctLoanNo) {
		this.ctLoanNo = ctLoanNo;
	}

	public String getCtProcurementMode() {
		return ctProcurementMode;
	}

	public void setCtProcurementMode(String ctProcurementMode) {
		this.ctProcurementMode = ctProcurementMode;
	}

	public String getCtPhysicalContractNo() {
		return ctPhysicalContractNo;
	}

	public void setCtPhysicalContractNo(String ctPhysicalContractNo) {
		this.ctPhysicalContractNo = ctPhysicalContractNo;
	}

	public String getCtGroupThree() {
		return ctGroupThree;
	}

	public void setCtGroupThree(String ctGroupThree) {
		this.ctGroupThree = ctGroupThree;
	}

	public String getCtStatusTwo() {
		return ctStatusTwo;
	}

	public void setCtStatusTwo(String ctStatusTwo) {
		this.ctStatusTwo = ctStatusTwo;
	}

	public String getCtLOAPhysicalNo() {
		return ctLOAPhysicalNo;
	}

	public void setCtLOAPhysicalNo(String ctLOAPhysicalNo) {
		this.ctLOAPhysicalNo = ctLOAPhysicalNo;
	}

	public String getCtUserType() {
		return ctUserType;
	}

	public void setCtUserType(String ctUserType) {
		this.ctUserType = ctUserType;
	}

	public String getCtSubType() {
		return ctSubType;
	}

	public void setCtSubType(String ctSubType) {
		this.ctSubType = ctSubType;
	}

	public String getCtTerminationType() {
		return ctTerminationType;
	}

	public void setCtTerminationType(String ctTerminationType) {
		this.ctTerminationType = ctTerminationType;
	}

	public String getCtCircularNo() {
		return ctCircularNo;
	}

	public void setCtCircularNo(String ctCircularNo) {
		this.ctCircularNo = ctCircularNo;
	}

	public String getCtCircularName() {
		return ctCircularName;
	}

	public void setCtCircularName(String ctCircularName) {
		this.ctCircularName = ctCircularName;
	}

	public String getCtCircularType() {
		return ctCircularType;
	}

	public void setCtCircularType(String ctCircularType) {
		this.ctCircularType = ctCircularType;
	}

	public Date getCtCircularEffectiveFromDate() {
		return ctCircularEffectiveFromDate;
	}

	public void setCtCircularEffectiveFromDate(Date ctCircularEffectiveFromDate) {
		this.ctCircularEffectiveFromDate = ctCircularEffectiveFromDate;
	}

	public Date getCtCircularEffectiveToDate() {
		return ctCircularEffectiveToDate;
	}

	public void setCtCircularEffectiveToDate(Date ctCircularEffectiveToDate) {
		this.ctCircularEffectiveToDate = ctCircularEffectiveToDate;
	}

	public String getCtSupplierName() {
		return ctSupplierName;
	}

	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}

	public String getPpYear() {
		return ppYear;
	}

	public void setPpYear(String ppYear) {
		this.ppYear = ppYear;
	}

	public String getPpMinistry() {
		return ppMinistry;
	}

	public void setPpMinistry(String ppMinistry) {
		this.ppMinistry = ppMinistry;
	}

	public String getPpPTJ() {
		return ppPTJ;
	}

	public void setPpPTJ(String ppPTJ) {
		this.ppPTJ = ppPTJ;
	}

	public String getPmServiceType() {
		return pmServiceType;
	}

	public void setPmServiceType(String pmServiceType) {
		this.pmServiceType = pmServiceType;
	}

	public String getPmStatus() {
		return pmStatus;
	}

	public void setPmStatus(String pmStatus) {
		this.pmStatus = pmStatus;
	}

	public String getPmFinancialOrgName() {
		return pmFinancialOrgName;
	}

	public void setPmFinancialOrgName(String pmFinancialOrgName) {
		this.pmFinancialOrgName = pmFinancialOrgName;
	}

	public String getPmBusinessRegNo() {
		return pmBusinessRegNo;
	}

	public void setPmBusinessRegNo(String pmBusinessRegNo) {
		this.pmBusinessRegNo = pmBusinessRegNo;
	}

	public String getPmEpNo() {
		return pmEpNo;
	}

	public void setPmEpNo(String pmEpNo) {
		this.pmEpNo = pmEpNo;
	}

	public String getPmOrgCategory() {
		return pmOrgCategory;
	}

	public void setPmOrgCategory(String pmOrgCategory) {
		this.pmOrgCategory = pmOrgCategory;
	}

	public String getPmOrgType() {
		return pmOrgType;
	}

	public void setPmOrgType(String pmOrgType) {
		this.pmOrgType = pmOrgType;
	}

	public String getPmOrgName() {
		return pmOrgName;
	}

	public void setPmOrgName(String pmOrgName) {
		this.pmOrgName = pmOrgName;
	}

	public String getPmOrgCode() {
		return pmOrgCode;
	}

	public void setPmOrgCode(String pmOrgCode) {
		this.pmOrgCode = pmOrgCode;
	}

	public String getPmItemName() {
		return pmItemName;
	}

	public void setPmItemName(String pmItemName) {
		this.pmItemName = pmItemName;
	}

	public String getPmItemCode() {
		return pmItemCode;
	}

	public void setPmItemCode(String pmItemCode) {
		this.pmItemCode = pmItemCode;
	}

	public String getPmCompanyName() {
		return pmCompanyName;
	}

	public void setPmCompanyName(String pmCompanyName) {
		this.pmCompanyName = pmCompanyName;
	}

	public String getPmIdentificationNo() {
		return pmIdentificationNo;
	}

	public void setPmIdentificationNo(String pmIdentificationNo) {
		this.pmIdentificationNo = pmIdentificationNo;
	}

	public String getPmUserName() {
		return pmUserName;
	}

	public void setPmUserName(String pmUserName) {
		this.pmUserName = pmUserName;
	}

	public String getPmRoleCode() {
		return pmRoleCode;
	}

	public void setPmRoleCode(String pmRoleCode) {
		this.pmRoleCode = pmRoleCode;
	}

	public String getPmContractNo() {
		return pmContractNo;
	}

	public void setPmContractNo(String pmContractNo) {
		this.pmContractNo = pmContractNo;
	}

	public String getPmPhysicalContractNo() {
		return pmPhysicalContractNo;
	}

	public void setPmPhysicalContractNo(String pmPhysicalContractNo) {
		this.pmPhysicalContractNo = pmPhysicalContractNo;
	}

	public String getPmLOANo() {
		return pmLOANo;
	}

	public void setPmLOANo(String pmLOANo) {
		this.pmLOANo = pmLOANo;
	}

	public String getPmPhysicalLOANo() {
		return pmPhysicalLOANo;
	}

	public void setPmPhysicalLOANo(String pmPhysicalLOANo) {
		this.pmPhysicalLOANo = pmPhysicalLOANo;
	}

	public String getPmZone() {
		return pmZone;
	}

	public void setPmZone(String pmZone) {
		this.pmZone = pmZone;
	}

	public String getPmProfileStatus() {
		return pmProfileStatus;
	}

	public void setPmProfileStatus(String pmProfileStatus) {
		this.pmProfileStatus = pmProfileStatus;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public List<PmCategoryL1> getSupplierParentCatList() {
		return supplierParentCatList;
	}

	public void setSupplierParentCatList(List<PmCategoryL1> supplierParentCatList) {
		this.supplierParentCatList = supplierParentCatList;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}
	
	public List<SelectItem> assignListToSelectItem(List<Object[]> objectList){
		
		List<SelectItem> list = new ArrayList<SelectItem>();
		
		for (Object[] object : objectList) {
			
			SelectItem selectItem = new SelectItem();
			
			selectItem.setLabel(object[0].toString());
			selectItem.setValue(object[1]);
			
			list.add(selectItem);
		}
		
		return list;
	}
	
	protected boolean isEmpty(List<?> list){
		return list == null || list.isEmpty();
	}
	
	protected boolean isEmpty(String str){
		return str == null || str.isEmpty();
	}

	protected boolean isEmpty(Object obj){
		return obj == null;
	}
	


	protected boolean enableValue;

	public boolean isEnableValue() {
		return enableValue;
	}

	public void setEnableValue(boolean enableValue) {
		this.enableValue = enableValue;
	}
}
