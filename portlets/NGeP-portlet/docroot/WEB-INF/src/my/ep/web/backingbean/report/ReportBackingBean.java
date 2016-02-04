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
package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import com.liferay.portal.util.PortalUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportBackingBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportBackingBean")
@ViewScoped
public class ReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(ReportBackingBean.class);

	/**
	 * Instantiates a new report backing bean.
	 */
	public ReportBackingBean() {
	}

	/** The module value. */
	private String moduleValue;

	/** The report name. */
	private String reportName;

	/** The report value. */
	private String reportValue;

	/** The report category. */
	private String reportCategory;

	/** The string zone. */
	private String stringZone;

	/** The scqt. */
	private ScQt scqt;

	/** The sc loa id no. */
	private String scLOAIdNo;

	/** The enable value. */
	private boolean enableValue = false;

	/** The parameter code type. */
	private String parameterCodeType = new String();

	/** The report list. */
	private List<SelectItem> reportList = new ArrayList<SelectItem>();

	/** The report category list. */
	private List<SelectItem> reportCategoryList = new ArrayList<SelectItem>();

	/** The module list. */
	private List<SelectItem> moduleList;

	/** The reg type list. */
	private List<SelectItem> regTypeList;

	/** The rpt gen bean. */
	private ReportsGenerateBean rptGenBean = new ReportsGenerateBean();

	/** The authoriztion list. */
	private List<String> authoriztionList = new ArrayList<String>();

	/** The org profile list. */
	private List<String> orgProfileList = new ArrayList<String>();

	/** The sc year list. */
	private List<String> scYearList = new ArrayList<String>();

	/** The org profile id name list. */
	private List<SelectItem> orgProfileIdNameList = new ArrayList<SelectItem>();

	/** The user listbyprofile role. */
	private List<SelectItem> userListbyprofileRole = new ArrayList<SelectItem>();

	/** The state lst. */
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();

	/** The sm state list. */
	private List<SelectItem> smStateList = new ArrayList<SelectItem>();

	/** The district lst. */
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();

	/** The lev one cat lst. */
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();

	/** The lev two cat lst. */
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();

	/** The lev three cat lst. */
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();

	/** The procurement list. */
	private List<SelectItem> procurementList = new ArrayList<SelectItem>();

	/** The sc procure all list. */
	private List<SelectItem> scProcureAllList = new ArrayList<SelectItem>();

	/** The sc quotation list. */
	private List<SelectItem> scQuotationList = new ArrayList<SelectItem>();

	/** The sc tender list. */
	private List<SelectItem> scTenderList = new ArrayList<SelectItem>();

	/** The sc rstrctd tndr list. */
	private List<SelectItem> scRstrctdTndrList = new ArrayList<SelectItem>();

	/** The sc month list. */
	private List<SelectItem> scMonthList = new ArrayList<SelectItem>();

	/** The requisitioner list. */
	private List<SelectItem> requisitionerList = new ArrayList<SelectItem>();

	/** The status list. */
	private List<SelectItem> statusList = new ArrayList<SelectItem>();

	/** The ministry list. */
	private List<SelectItem> ministryList = new ArrayList<SelectItem>();

	/** The ptj list. */
	private List<SelectItem> ptjList = new ArrayList<SelectItem>();

	/** The pm ministry list. */
	private List<SelectItem> pmMinistryList = new ArrayList<SelectItem>();

	/** The pm ptj list. */
	private List<String> pmPtjList = new ArrayList<String>();

	/** The processing officer list. */
	private List<SelectItem> processingOfficerList = new ArrayList<SelectItem>();
	// private List<PmOrgProfile> ptjList;
	/** The loan list. */
	private List<String> loanList = new ArrayList<String>();

	/** The unspc list. */
	private List<SelectItem> unspcList = new ArrayList<SelectItem>();

	/** The brand list. */
	private List<SelectItem> brandList = new ArrayList<SelectItem>();

	/** The ext type list. */
	private List<SelectItem> extTypeList = new ArrayList<SelectItem>();

	/** The measurment list. */
	private List<SelectItem> measurmentList = new ArrayList<SelectItem>();

	/** The color list. */
	private List<SelectItem> colorList = new ArrayList<SelectItem>();

	/** The approver nayak taraf list. */
	private List<SelectItem> approverNayakTarafList = new ArrayList<SelectItem>();

	/** The sc ptj list. */
	private List<SelectItem> scPtjList = new ArrayList<SelectItem>();

	/** The sc requisitioner list. */
	private List<SelectItem> scRequisitionerList = new ArrayList<SelectItem>();

	/** The sc desc officer list. */
	private List<SelectItem> scDescOfficerList = new ArrayList<SelectItem>();

	/** The sm upssvo user list. */
	private List<SelectItem> smUPSSVOUserList = new ArrayList<SelectItem>();

	/** The ct sub type list. */
	private List<SelectItem> ctSubTypeList = new ArrayList<SelectItem>();

	/** The zone list. */
	private List<SelectItem> zoneList = new ArrayList<SelectItem>();

	/** The param list. */
	private List<String> paramList = new ArrayList<String>();

	/** The high role code list. */
	private List<String> highRoleCodeList = new ArrayList<String>();

	/** The all role codes list. */
	private List<String> allRoleCodesList = new ArrayList<String>();

	/** The param table role code list. */
	private List<String> paramTableRoleCodeList = new ArrayList<String>();

	/** The rolecode rquisitioner list. */
	private List<SelectItem> rolecodeRquisitionerList = new ArrayList<SelectItem>();

	/** The rolecode approver list. */
	private List<SelectItem> rolecodeApproverList = new ArrayList<SelectItem>();

	/** The rolecode recieving officer list. */
	private List<SelectItem> rolecodeRecievingOfficerList = new ArrayList<SelectItem>();

	/** The identification type list. */
	private List<SelectItem> identificationTypeList = new ArrayList<SelectItem>();

	/** The accumulated paid up cptl list. */
	private List<SelectItem> accumulatedPaidUpCptlList = new ArrayList<SelectItem>();

	/** The registration status list. */
	private List<SelectItem> registrationStatusList = new ArrayList<SelectItem>();

	/** The registration status list of bus comp type. */
	private List<SelectItem> registrationStatusListOfBusCompType = new ArrayList<SelectItem>();

	/** The sm bus company list. */
	private List<SelectItem> smBusCompanyTypeList = new ArrayList<SelectItem>();

	/** The simple quote no list. */
	private List<String> simpleQuoteNoList = new ArrayList<String>();

	/** The service type list. */
	private List<SelectItem> serviceTypeList = new ArrayList<SelectItem>();

	/** The payment method list. */
	private List<String> paymentMethodList = new ArrayList<String>();

	/** The payment type list. */
	private List<SelectItem> paymentTypeList = new ArrayList<SelectItem>();

	/** The sc soa status list. */
	private List<SelectItem> scSOAStatusList = new ArrayList<SelectItem>();

	/** The search lov paging data model. */
	private IPagingDataModel<CmUnspsc> searchUnspscPagingDataModel = new SearchUnspscPagingDataModel();

	/** The sc report backing bean. */
	private SCReportBackingBean scReportBackingBean;

	/** The cm report backing bean. */
	private CMReportBackingBean cmReportBackingBean;

	/** The fl report backing bean. */
	private FLReportBackingBean flReportBackingBean;

	/** The sm report backing bean. */
	private SMReportBackingBean smReportBackingBean;

	/** The ct report backing bean. */
	private CTReportBackingBean ctReportBackingBean;

	/** The pp report backing bean. */
	private PPReportBackingBean ppReportBackingBean;

	/** The pm report backing bean. */
	private PMReportBackingBean pmReportBackingBean;

	/** The unspid. */
	private Long unspid = null;

	/** The ministry id. */
	private Long ministryId = null;

	/** The role code list. */
	private List<String> roleCodeList = new ArrayList<String>();

	/** The suppl company list. */
	private List<String> supplCompanyList = new ArrayList<String>();

	/** The value reqd. */
	private boolean valueReqd;

	/** The show parent msg. */
	private boolean showParentMsg;

	/** The selected cat list. */
	private List<SmSupplierCategory> selectedCatList;

	/** The select cat deletion list. */
	private List<SmSupplierCategory> selectCatDeletionList;

	/** The module item map. */
	private Map<String, List<SelectItem>> moduleItemMap = new HashMap<String, List<SelectItem>>();

	/** The items. */
	private SelectItem[] items = null;

	/** The doc type status items. */
	private SelectItem[] docTypeStatusItems = null;

	/** The doc type status items list. */
	private List<SelectItem> docTypeStatusItemsList = new ArrayList<SelectItem>();

	/** The local to date var. */
	private String localToDateVar;

	/** The user id. */
	private String userId;

	/** The lng user id. */
	private Long lngUserId = new FacesScopes().getUserSessionBean().getUserId();
	// private Long lngUserId =
	// (long)243371;//116205;//243371;//61745;//219490;//240790;//183931;//20854;//243371;//20854;//168887;//221747;//219490;//243394;//243371;//243371;//219490;//148500;//103852;//46;//61745;//41747;//220152;//229703;//25;//45;//62114;//235459;
	/** The str language. */
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	// private String strLanguage = "ms";
	/** The role code. */
	private String roleCode;

	/** The mof account end date from date. */
	private Date mofAccountEndDateFromDate;

	/** The mof account end date to date. */
	private Date mofAccountEndDateToDate;

	/** The from date. */
	private Date fromDate;

	/** The to date. */
	private Date toDate;

	/** The sub module. */
	private String subModule;

	/** The common ptj. */
	private String commonPtj;

	/** The common ministry. */
	private String commonMinistry;

	/** The ministry name. */
	private String ministryName;
	// sc
	/** The sc quotation id. */
	private String scQuotationId;

	/** The sc quotation no. */
	private String scQuotationNo;

	/** The sc payment id. */
	private String scPaymentId;

	/** The sc created to year. */
	private String scCreatedToYear;

	/** The sc created to month. */
	private String scCreatedToMonth;

	/** The sc created from year. */
	private String scCreatedFromYear;

	/** The sc created from month. */
	private String scCreatedFromMonth;

	/** The sc desk officer. */
	private String scDeskOfficer;

	/** The sc ptj. */
	private String scPtj;

	/** The sc status. */
	private String scStatus;

	/** The sc publish from date. */
	private Date scPublishFromDate;

	/** The sc publish to date. */
	private Date scPublishToDate;

	/** The sc sone crtd from date. */
	private Date scSoneCrtdFromDate;

	/** The sc sone crtd to date. */
	private Date scSoneCrtdToDate;

	/** The sc requisener id. */
	private String scRequisenerId;

	/** The sc supplier. */
	private String scSupplier;

	/** The sc login. */
	private String scLogin;

	/** The sc p ino. */
	private String scPIno;

	/** The sc sone status. */
	private String scSoneStatus;

	/** The sc prc rpt group. */
	private String scPrcRptGroup;

	/** The sc pi dtls prc rpt group. */
	private String scPIDtlsPrcRptGroup;

	/** The sc procurement type. */
	private String scProcurementType;

	/** The sc process rpt quotation no. */
	private String scProcessRptQuotationNo;

	/** The sc common status. */
	private String scCommonStatus;

	/** The sc laprnpurchdefault group. */
	private String scLAPRNPURCHDEFAULTGroup;

	/** The sc requestnotesummary group. */
	private String scREQUESTNOTESUMMARYGroup;

	/** The sc pid no. */
	private String scPIDNo;

	/** The sc contract or pkp type. */
	private String scContractOrPKPType;

	/** The sc contract or pkp no. */
	private String scContractOrPKPNo;

	/** The sc contract or pkp name. */
	private String scContractOrPKPName;

	/** The sc request quote no. */
	private String scRequestQuoteNo;

	/** The sc request note status. */
	private String scRequestNoteStatus;

	/** The sc simple quote status. */
	private String scSimpleQuoteStatus;

	/** The sc simple quote dtl group. */
	private String scSimpleQuoteDtlGroup;

	/** The sc purchase in default status. */
	private String scPurchaseInDefaultStatus;

	/** The sc soa status. */
	private String scSOAStatus;

	/** The sc simple quote type. */
	private String scSimpleQuoteType;

	/** The sc loa no. */
	private String scLOANo;

	// cm
	/** The cm item type. */
	private String cmItemType;

	/** The cm item status. */
	private String cmItemStatus;

	/** The cm unspsc code. */
	private String cmUnspscCode;

	/** The cm unspsc title. */
	private String cmUnspscTitle;

	/** The cm company name. */
	private String cmCompanyName;

	/** The cm registration type. */
	private String cmRegistrationType;

	/** The cm item category. */
	private String cmItemCategory;

	/** The cm item name. */
	private String cmItemName;

	/** The cm ep number. */
	private String cmEpNumber;

	/** The cm business reg no. */
	private String cmBusinessRegNo;

	/** The cm ic number. */
	private String cmIcNumber;

	/** The cm mof number. */
	private String cmMofNumber;

	/** The cm extention code. */
	private String cmExtentionCode;

	/** The cm extention type. */
	private String cmExtentionType;

	/** The cm extention brand. */
	private String cmExtentionBrand;

	/** The cm extention measurment. */
	private String cmExtentionMeasurment;

	/** The cm extention colour. */
	private String cmExtentionColour;

	/** The cm proof identity. */
	private String cmProofIdentity;

	/** The cm suppl or admin. */
	private String cmSupplOrAdmin;

	/** The cm extention. */
	private String cmExtention;

	/** The cm group one. */
	private String cmGroupOne;

	/** The cm request number. */
	private String cmRequestNumber;
	// fl
	/** The fl itm type. */
	private String flItmType;

	/** The fl contract no. */
	private String flContractNo;

	/** The fl sst. */
	private String flSst;

	/** The fl jabatan. */
	private String flJabatan;

	/** The fl supplier. */
	private String flSupplier;

	/** The fl status. */
	private String flStatus;

	/** The fl duration. */
	private String flDuration;

	/** The fl recieving officer. */
	private String flRecievingOfficer;

	/** The fl delivery address. */
	private String flDeliveryAddress;

	/** The fl rec note no. */
	private String flRecNoteNo;

	/** The fl contract type. */
	private String flContractType;

	/** The fl requisitioner. */
	private String flRequisitioner;

	/** The fl mto contract. */
	private String flMtoContract;

	/** The fl invoice no. */
	private String flInvoiceNo;

	/** The fl supplier invoice no. */
	private String flSupplierInvoiceNo;

	/** The fl deduction type. */
	private String flDeductionType;

	/** The fl cr no. */
	private String flCrNo;

	/** The fl approver name. */
	private String flApproverName;

	/** The fl document type. */
	private String flDocumentType;

	/** The fl criteria. */
	private String flCriteria;

	/** The fl scoring. */
	private String flScoring;

	/** The fl pkp no. */
	private String flPkpNo;

	/** The fl delivery order no. */
	private String flDeliveryOrderNo;

	/** The fl supplier delivery order no. */
	private String flSupplierDeliveryOrderNo;

	/** The fl item code. */
	private String flItemCode;

	/** The fl po amount. */
	private String flPoAmount;

	/** The fl pr no. */
	private String flPrNo;

	/** The fl pmo. */
	private String flPmo;

	/** The fl item name. */
	private String flItemName;

	/** The fl contract order. */
	private String flContractOrder;

	/** The fl contract request. */
	private String flContractRequest;

	/** The fl purchase order no. */
	private String flPurchaseOrderNo;

	/** The fl group one. */
	private String flGroupOne;

	/** The fl group two. */
	private String flGroupTwo;

	/** The fl group three. */
	private String flGroupThree;

	/** The fl group four. */
	private String flGroupFour;

	/** The fl group five. */
	private String flGroupFive;

	/** The fl group six. */
	private String flGroupSix;

	/** The fl group seven. */
	private String flGroupSeven;

	/** The fl group eight. */
	private String flGroupEight;

	/** The fl group nine. */
	private String flGroupNine;

	/** The fl group ten. */
	private String flGroupTen;

	/** The fl group eleven. */
	private String flGroupEleven;

	/** The fl group twelve. */
	private String flGroupTwelve;

	/** The fl group thirteen. */
	private String flGroupThirteen;

	/** The fl group fourteen. */
	private String flGroupFourteen;

	/** The fl group fifteen. */
	private String flGroupFifteen;

	/** The fl group sixteen. */
	private String flGroupSixteen;

	/** The fl group seventeen. */
	private String flGroupSeventeen;

	/** The report category view. */
	private boolean reportCategoryView = false;

	/** The fl stop instruction no. */
	private String flStopInstructionNo;

	/** The fl payment advice no. */
	private String flPaymentAdviceNo;
	// sm
	/** The sm threshold from year. */
	private String smThresholdFromYear;

	/** The sm threshold to year. */
	private String smThresholdToYear;

	/** The sm created from year. */
	private String smCreatedFromYear;

	/** The sm created to year. */
	private String smCreatedToYear;

	/** The sm monthly rpt by year. */
	private String smMonthlyRptByYear;

	/** The sm svo user. */
	private String smSVOUser;

	/** The sm application no. */
	private String smApplicationNo;

	/** The sm zone. */
	private String smZone;

	/** The sm supplier type. */
	private String smSupplierType;

	/** The sm mof reg no. */
	private String smMofRegNo;

	/** The sm registration status. */
	private String smRegistrationStatus;

	/** The sm bus comp type registration status. */
	private String smBusCompTypeRegistrationStatus;

	/** The smcategory. */
	private String smcategory;

	/** The sm main category. */
	private String smMainCategory;

	/** The sm sub category. */
	private String smSubCategory;

	/** The sm sub category division. */
	private String smSubCategoryDivision;

	/** The sm application status. */
	private String smApplicationStatus;

	/** The sm code. */
	private String smCode;

	/** The payment method. */
	private String paymentMethod;

	/** The sm payment type. */
	private String smPaymentType;

	/** The sm application type. */
	private String smApplicationType;

	/** The sm state. */
	private String smState;

	/** The sm district. */
	private String smDistrict;

	/** The sm company name. */
	private String smCompanyName;

	/** The sm registration type. */
	private String smRegistrationType;

	/** The sm year text. */
	private String smYearText;

	/** The sm mof reg no from. */
	private String smMofRegNoFrom;

	/** The sm mof reg no to. */
	private String smMofRegNoTo;

	/** The sm paid up capital frm. */
	private String smPaidUpCapitalFrm;

	/** The sm paid up capital to. */
	private String smPaidUpCapitalTo;

	/** The sm bus paid up capital frm. */
	private String smBusPaidUpCapitalFrm;

	/** The sm bus paid up capital to. */
	private String smBusPaidUpCapitalTo;

	/** The sm paid up capital naccum. */
	private String smPaidUpCapitalNaccum;

	/** The sm payment method. */
	private String smPaymentMethod;

	/** The sm processing type. */
	private String smProcessingType;

	/** The sm receipt no. */
	private String smReceiptNo;

	/** The sm report type. */
	private String smReportType;

	/** The sm rt report type. */
	private String smRTReportType;

	/** The sm supplier name. */
	private String smSupplierName;

	/** The sm mof registration fee list. */
	private List<SelectItem> smMofRegistrationFeeList = new ArrayList<SelectItem>();

	/** The sm soft certificate fee list. */
	private List<SelectItem> smSoftCertificateFeeList = new ArrayList<SelectItem>();

	/** The sm processing fee list. */
	private List<SelectItem> smProcessingFeeList = new ArrayList<SelectItem>();

	/** The sm processing officer name. */
	private String smProcessingOfficerName;

	/** The sm role. */
	private String smRole;

	/** The sm ep no. */
	private String smEpNo;

	/** The sm registration no. */
	private String smRegistrationNo;

	/** The sm supporting document mode. */
	private String smSupportingDocumentMode;

	/** The sm supporting document status. */
	private String smSupportingDocumentStatus;

	/** The sm personnel role. */
	private String smPersonnelRole;

	/** The sm identification type. */
	private String smIdentificationType;

	/** The sm identification number. */
	private String smIdentificationNumber;

	/** The sm cancellation by. */
	private String smCancellationBy;

	/** The sm bus company type. */
	private String smBusCompanyType;

	/** The sm from month. */
	private String smFromMonth;

	/** The sm to month. */
	private String smToMonth;

	/** The sm user name. */
	private String smUserName;

	/** The sm request expiry date. */
	private String smRequestExpiryDate;

	/** The sm profile status. */
	private String smProfileStatus;

	/** The sm gst registration no. */
	private String smGSTRegistrationNo;

	/** The sm gst registered status. */
	private String smGSTRegisteredStatus;

	/** The sm payment to. */
	private String smPaymentTo;
	// CT
	/** The ct contract no. */
	private String ctContractNo;

	/** The ct agreement. */
	private String ctAgreement;

	/** The ct status. */
	private String ctStatus;

	/** The ct contr montr status. */
	private String ctContrMontrStatus;

	/** The ct complete. */
	private String ctComplete;

	/** The ct group one. */
	private String ctGroupOne;

	/** The ct group two. */
	private String ctGroupTwo;

	/** The ct product service. */
	private String ctProductService;

	/** The ct loan no. */
	private String ctLoanNo;

	/** The ct procurement mode. */
	private String ctProcurementMode;

	/** The ct physical contract no. */
	private String ctPhysicalContractNo;

	/** The ct group three. */
	private String ctGroupThree;

	/** The ct status two. */
	private String ctStatusTwo;

	/** The ct loa physical no. */
	private String ctLOAPhysicalNo;

	/** The ct user type. */
	private String ctUserType;

	/** The ct sub type. */
	private String ctSubType;

	/** The ct termination type. */
	private String ctTerminationType;

	/** The ct circular no. */
	private String ctCircularNo;

	/** The ct circular name. */
	private String ctCircularName;

	/** The ct circular type. */
	private String ctCircularType;

	/** The ct circular effective from date. */
	private Date ctCircularEffectiveFromDate;

	/** The ct circular effective to date. */
	private Date ctCircularEffectiveToDate;

	/** The ct supplier name. */
	private String ctSupplierName;
	// PP
	/** The pp year. */
	private String ppYear;

	/** The pp ministry. */
	private String ppMinistry;

	/** The pp ptj. */
	private String ppPTJ;

	/** The lng state id. */
	private long[] lngStateId;

	/** The lng district id. */
	private long[] lngDistrictId;
	// PM
	/** The pm service type. */
	private String pmServiceType;

	/** The pm status. */
	private String pmStatus;

	/** The pm financial org name. */
	private String pmFinancialOrgName;

	/** The pm business reg no. */
	private String pmBusinessRegNo;

	/** The pm ep no. */
	private String pmEpNo;

	/** The pm org cateogry. */
	private String pmOrgCategory;

	/** The pm org type. */
	private String pmOrgType;

	/** The pm org name. */
	private String pmOrgName;

	/** The pm org code. */
	private String pmOrgCode;

	/** The pm item name. */
	private String pmItemName;

	/** The pm item code. */
	private String pmItemCode;

	/** The pm company name. */
	private String pmCompanyName;

	/** The pm identification no. */
	private String pmIdentificationNo;

	/** The pm user name. */
	private String pmUserName;

	/** The pm role code. */
	private String pmRoleCode;

	/** The pm contract no. */
	private String pmContractNo;

	/** The pm physical contract no. */
	private String pmPhysicalContractNo;

	/** The pm loa no. */
	private String pmLOANo;

	/** The pm physical loa no. */
	private String pmPhysicalLOANo;

	private String pmZone;

	private String pmProfileStatus;

	/** The selected all. */
	private boolean selectedAll;

	/** The selected. */
	private boolean selected;

	/** The supplier parent cat list. */
	private List<PmCategoryL1> supplierParentCatList;

	/** The supplier child cat list. */
	private List<PmCategoryL2> supplierChildCatList;

	/** The supplier sub child cat list. */
	private List<PmCategoryL3> supplierSubChildCatList;

	/**
	 * Gets the select cat deletion list.
	 * 
	 * @return the select cat deletion list
	 */
	public List<SmSupplierCategory> getSelectCatDeletionList() {
		return selectCatDeletionList;
	}

	/**
	 * Gets the sc loa id no.
	 * 
	 * @return the scLOAIdNo
	 */
	public String getScLOAIdNo() {
		return scLOAIdNo;
	}

	/**
	 * Sets the sc loa id no.
	 * 
	 * @param scLOAIdNo
	 *            the scLOAIdNo to set
	 */
	public void setScLOAIdNo(String scLOAIdNo) {
		this.scLOAIdNo = scLOAIdNo;
	}

	/**
	 * Sets the select cat deletion list.
	 * 
	 * @param selectCatDeletionList
	 *            the new select cat deletion list
	 */
	public void setSelectCatDeletionList(
			List<SmSupplierCategory> selectCatDeletionList) {
		this.selectCatDeletionList = selectCatDeletionList;
	}

	/**
	 * Gets the payment method list.
	 * 
	 * @return the paymentMethodList
	 */
	public List<String> getPaymentMethodList() {
		return paymentMethodList;
	}

	/**
	 * Sets the payment method list.
	 * 
	 * @param paymentMethodList
	 *            the new payment method list
	 */
	public void setPaymentMethodList(List<String> paymentMethodList) {
		this.paymentMethodList = paymentMethodList;
	}

	/**
	 * Gets the payment type list.
	 * 
	 * @return the paymentTypeList
	 */
	public List<SelectItem> getPaymentTypeList() {
		return paymentTypeList;
	}

	/**
	 * Sets the payment type list.
	 * 
	 * @param paymentTypeList
	 *            the new payment type list
	 */
	public void setPaymentTypeList(List<SelectItem> paymentTypeList) {
		this.paymentTypeList = paymentTypeList;
	}

	/**
	 * Gets the service type list.
	 * 
	 * @return the serviceTypeList
	 */
	public List<SelectItem> getServiceTypeList() {
		return serviceTypeList;
	}

	/**
	 * Sets the service type list.
	 * 
	 * @param serviceTypeList
	 *            the new service type list
	 */
	public void setServiceTypeList(List<SelectItem> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}

	/**
	 * Gets the report category.
	 * 
	 * @return the reportCategory
	 */
	public String getReportCategory() {
		return reportCategory;
	}

	/**
	 * Sets the report category.
	 * 
	 * @param reportCategory
	 *            the new report category
	 */
	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	/**
	 * Gets the processing officer list.
	 * 
	 * @return the processingOfficerList
	 */
	public List<SelectItem> getProcessingOfficerList() {
		return processingOfficerList;
	}

	/**
	 * Sets the processing officer list.
	 * 
	 * @param processingOfficerList
	 *            the new processing officer list
	 */
	public void setProcessingOfficerList(List<SelectItem> processingOfficerList) {
		this.processingOfficerList = processingOfficerList;
	}

	/**
	 * Gets the sm mof registration fee list.
	 * 
	 * @return the smMofRegistrationFeeList
	 */
	public List<SelectItem> getSmMofRegistrationFeeList() {
		return smMofRegistrationFeeList;
	}

	/**
	 * Sets the sm mof registration fee list.
	 * 
	 * @param smMofRegistrationFeeList
	 *            the smMofRegistrationFeeList to set
	 */
	public void setSmMofRegistrationFeeList(
			List<SelectItem> smMofRegistrationFeeList) {
		this.smMofRegistrationFeeList = smMofRegistrationFeeList;
	}

	/**
	 * Gets the sm soft certificate fee list.
	 * 
	 * @return the smSoftCertificateFeeList
	 */
	public List<SelectItem> getSmSoftCertificateFeeList() {
		return smSoftCertificateFeeList;
	}

	/**
	 * Sets the sm soft certificate fee list.
	 * 
	 * @param smSoftCertificateFeeList
	 *            the smSoftCertificateFeeList to set
	 */
	public void setSmSoftCertificateFeeList(
			List<SelectItem> smSoftCertificateFeeList) {
		this.smSoftCertificateFeeList = smSoftCertificateFeeList;
	}

	/**
	 * Gets the sm processing fee list.
	 * 
	 * @return the smProcessingFeeList
	 */
	public List<SelectItem> getSmProcessingFeeList() {
		return smProcessingFeeList;
	}

	/**
	 * Sets the sm processing fee list.
	 * 
	 * @param smProcessingFeeList
	 *            the smProcessingFeeList to set
	 */
	public void setSmProcessingFeeList(List<SelectItem> smProcessingFeeList) {
		this.smProcessingFeeList = smProcessingFeeList;
	}

	/**
	 * Gets the zone list.
	 * 
	 * @return the zoneList
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
	 * Gets the report category list.
	 * 
	 * @return the reportCategoryList
	 */
	public List<SelectItem> getReportCategoryList() {
		return reportCategoryList;
	}

	/**
	 * Sets the report category list.
	 * 
	 * @param reportCategoryList
	 *            the new report category list
	 */
	public void setReportCategoryList(List<SelectItem> reportCategoryList) {
		this.reportCategoryList = reportCategoryList;
	}

	/**
	 * Gets the param table role code list.
	 * 
	 * @return the paramTableRoleCodeList
	 */
	public List<String> getParamTableRoleCodeList() {
		return paramTableRoleCodeList;
	}

	/**
	 * Sets the param table role code list.
	 * 
	 * @param paramTableRoleCodeList
	 *            the new param table role code list
	 */
	public void setParamTableRoleCodeList(List<String> paramTableRoleCodeList) {
		this.paramTableRoleCodeList = paramTableRoleCodeList;
	}

	/**
	 * Gets the identification type list.
	 * 
	 * @return the identificationTypeList
	 */
	public List<SelectItem> getIdentificationTypeList() {
		return identificationTypeList;
	}

	/**
	 * Sets the identification type list.
	 * 
	 * @param identificationTypeList
	 *            the new identification type list
	 */
	public void setIdentificationTypeList(
			List<SelectItem> identificationTypeList) {
		this.identificationTypeList = identificationTypeList;
	}

	/**
	 * Gets the accumulated paid up cptl list.
	 * 
	 * @return the accumulatedPaidUpCptlList
	 */
	public List<SelectItem> getAccumulatedPaidUpCptlList() {
		return accumulatedPaidUpCptlList;
	}

	/**
	 * Sets the accumulated paid up cptl list.
	 * 
	 * @param accumulatedPaidUpCptlList
	 *            the new accumulated paid up cptl list
	 */
	public void setAccumulatedPaidUpCptlList(
			List<SelectItem> accumulatedPaidUpCptlList) {
		this.accumulatedPaidUpCptlList = accumulatedPaidUpCptlList;
	}

	/**
	 * Gets the sm state list.
	 * 
	 * @return the smStateList
	 */
	public List<SelectItem> getSmStateList() {
		return smStateList;
	}

	/**
	 * Sets the sm state list.
	 * 
	 * @param smStateList
	 *            the new sm state list
	 */
	public void setSmStateList(List<SelectItem> smStateList) {
		this.smStateList = smStateList;
	}

	/**
	 * Gets the ministry id.
	 * 
	 * @return the ministryId
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
	 * Gets the pm ministry list.
	 * 
	 * @return the pmMinistryList
	 */
	public List<SelectItem> getPmMinistryList() {
		return pmMinistryList;
	}

	/**
	 * Sets the pm ministry list.
	 * 
	 * @param pmMinistryList
	 *            the new pm ministry list
	 */
	public void setPmMinistryList(List<SelectItem> pmMinistryList) {
		this.pmMinistryList = pmMinistryList;
	}

	/**
	 * Gets the pm ptj list.
	 * 
	 * @return the pmPtjList
	 */
	public List<String> getPmPtjList() {
		return pmPtjList;
	}

	/**
	 * Sets the pm ptj list.
	 * 
	 * @param pmPtjList
	 *            the new pm ptj list
	 */
	public void setPmPtjList(List<String> pmPtjList) {
		this.pmPtjList = pmPtjList;
	}

	/**
	 * Gets the doc type status items list.
	 * 
	 * @return the docTypeStatusItemsList
	 */
	public List<SelectItem> getDocTypeStatusItemsList() {
		return docTypeStatusItemsList;
	}

	/**
	 * Sets the doc type status items list.
	 * 
	 * @param docTypeStatusItemsList
	 *            the new doc type status items list
	 */
	public void setDocTypeStatusItemsList(
			List<SelectItem> docTypeStatusItemsList) {
		this.docTypeStatusItemsList = docTypeStatusItemsList;
	}

	/**
	 * Gets the doc type status items.
	 * 
	 * @return the docTypeStatusItems
	 */
	public SelectItem[] getDocTypeStatusItems() {
		return docTypeStatusItems;
	}

	/**
	 * Sets the doc type status items.
	 * 
	 * @param docTypeStatusItems
	 *            the new doc type status items
	 */
	public void setDocTypeStatusItems(SelectItem[] docTypeStatusItems) {
		this.docTypeStatusItems = docTypeStatusItems;
	}

	/**
	 * Gets the rolecode rquisitioner list.
	 * 
	 * @return the rolecodeRquisitionerList
	 */
	public List<SelectItem> getRolecodeRquisitionerList() {
		return rolecodeRquisitionerList;
	}

	/**
	 * Gets the sm supplier name.
	 * 
	 * @return the smSupplierName
	 */
	public String getSmSupplierName() {
		return smSupplierName;
	}

	/**
	 * Sets the sm supplier name.
	 * 
	 * @param smSupplierName
	 *            the smSupplierName to set
	 */
	public void setSmSupplierName(String smSupplierName) {
		this.smSupplierName = smSupplierName;
	}

	/**
	 * Sets the rolecode rquisitioner list.
	 * 
	 * @param rolecodeRquisitionerList
	 *            the new rolecode rquisitioner list
	 */
	public void setRolecodeRquisitionerList(
			List<SelectItem> rolecodeRquisitionerList) {
		this.rolecodeRquisitionerList = rolecodeRquisitionerList;
	}

	/**
	 * Gets the rolecode approver list.
	 * 
	 * @return the rolecodeApproverList
	 */
	public List<SelectItem> getRolecodeApproverList() {
		return rolecodeApproverList;
	}

	/**
	 * Sets the rolecode approver list.
	 * 
	 * @param rolecodeApproverList
	 *            the new rolecode approver list
	 */
	public void setRolecodeApproverList(List<SelectItem> rolecodeApproverList) {
		this.rolecodeApproverList = rolecodeApproverList;
	}

	/**
	 * Gets the rolecode recieving officer list.
	 * 
	 * @return the rolecodeRecievingOfficerList
	 */
	public List<SelectItem> getRolecodeRecievingOfficerList() {
		return rolecodeRecievingOfficerList;
	}

	/**
	 * Sets the rolecode recieving officer list.
	 * 
	 * @param rolecodeRecievingOfficerList
	 *            the new rolecode recieving officer list
	 */
	public void setRolecodeRecievingOfficerList(
			List<SelectItem> rolecodeRecievingOfficerList) {
		this.rolecodeRecievingOfficerList = rolecodeRecievingOfficerList;
	}

	/**
	 * Gets the high role code list.
	 * 
	 * @return the highRoleCodeList
	 */
	public List<String> getHighRoleCodeList() {
		return highRoleCodeList;
	}

	/**
	 * Sets the high role code list.
	 * 
	 * @param highRoleCodeList
	 *            the new high role code list
	 */
	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	/**
	 * Gets the all role codes list.
	 * 
	 * @return the allRoleCodesList
	 */
	public List<String> getAllRoleCodesList() {
		return allRoleCodesList;
	}

	/**
	 * Sets the all role codes list.
	 * 
	 * @param allRoleCodesList
	 *            the new all role codes list
	 */
	public void setAllRoleCodesList(List<String> allRoleCodesList) {
		this.allRoleCodesList = allRoleCodesList;
	}

	/**
	 * Gets the param list.
	 * 
	 * @return the paramList
	 */
	public List<String> getParamList() {
		return paramList;
	}

	/**
	 * Sets the param list.
	 * 
	 * @param paramList
	 *            the new param list
	 */
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	/**
	 * Gets the ct sub type list.
	 * 
	 * @return the ctSubTypeList
	 */
	public List<SelectItem> getCtSubTypeList() {
		return ctSubTypeList;
	}

	/**
	 * Sets the ct sub type list.
	 * 
	 * @param ctSubTypeList
	 *            the new ct sub type list
	 */
	public void setCtSubTypeList(List<SelectItem> ctSubTypeList) {
		this.ctSubTypeList = ctSubTypeList;
	}

	/**
	 * Gets the sm upssvo user list.
	 * 
	 * @return the smUPSSVOUserList
	 */
	public List<SelectItem> getSmUPSSVOUserList() {
		return smUPSSVOUserList;
	}

	/**
	 * Sets the sm upssvo user list.
	 * 
	 * @param smUPSSVOUserList
	 *            the new sm upssvo user list
	 */
	public void setSmUPSSVOUserList(List<SelectItem> smUPSSVOUserList) {
		this.smUPSSVOUserList = smUPSSVOUserList;
	}

	/**
	 * Gets the sc desc officer list.
	 * 
	 * @return the scDescOfficerList
	 */
	public List<SelectItem> getScDescOfficerList() {
		return scDescOfficerList;
	}

	/**
	 * Sets the sc desc officer list.
	 * 
	 * @param scDescOfficerList
	 *            the new sc desc officer list
	 */
	public void setScDescOfficerList(List<SelectItem> scDescOfficerList) {
		this.scDescOfficerList = scDescOfficerList;
	}

	/**
	 * Gets the sc ptj list.
	 * 
	 * @return the scPtjList
	 */
	public List<SelectItem> getScPtjList() {
		return scPtjList;
	}

	/**
	 * Sets the sc ptj list.
	 * 
	 * @param scPtjList
	 *            the new sc ptj list
	 */
	public void setScPtjList(List<SelectItem> scPtjList) {
		this.scPtjList = scPtjList;
	}

	/**
	 * Gets the sc requisitioner list.
	 * 
	 * @return the scRequisitionerList
	 */
	public List<SelectItem> getScRequisitionerList() {
		return scRequisitionerList;
	}

	/**
	 * Sets the sc requisitioner list.
	 * 
	 * @param scRequisitionerList
	 *            the new sc requisitioner list
	 */
	public void setScRequisitionerList(List<SelectItem> scRequisitionerList) {
		this.scRequisitionerList = scRequisitionerList;
	}

	/**
	 * Gets the approver nayak taraf list.
	 * 
	 * @return the approverNayakTarafList
	 */
	public List<SelectItem> getApproverNayakTarafList() {
		return approverNayakTarafList;
	}

	/**
	 * Sets the approver nayak taraf list.
	 * 
	 * @param approverNayakTarafList
	 *            the new approver nayak taraf list
	 */
	public void setApproverNayakTarafList(
			List<SelectItem> approverNayakTarafList) {
		this.approverNayakTarafList = approverNayakTarafList;
	}

	/**
	 * Gets the role code list.
	 * 
	 * @return the roleCodeList
	 */
	public List<String> getRoleCodeList() {
		return roleCodeList;
	}

	/**
	 * Sets the role code list.
	 * 
	 * @param roleCodeList
	 *            the new role code list
	 */
	public void setRoleCodeList(List<String> roleCodeList) {
		this.roleCodeList = roleCodeList;
	}

	/**
	 * Gets the suppl company list.
	 * 
	 * @return the supplCompanyList
	 */
	public List<String> getSupplCompanyList() {
		return supplCompanyList;
	}

	/**
	 * Sets the suppl company list.
	 * 
	 * @param supplCompanyList
	 *            the new suppl company list
	 */
	public void setSupplCompanyList(List<String> supplCompanyList) {
		this.supplCompanyList = supplCompanyList;
	}

	/**
	 * Checks if is show parent msg.
	 * 
	 * @return true, if is show parent msg
	 */
	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	/**
	 * Sets the show parent msg.
	 * 
	 * @param showParentMsg
	 *            the new show parent msg
	 */
	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	/**
	 * Gets the supplier child cat list.
	 * 
	 * @return the supplierChildCatList
	 */
	public List<PmCategoryL2> getSupplierChildCatList() {
		return supplierChildCatList;
	}

	/**
	 * Sets the supplier child cat list.
	 * 
	 * @param supplierChildCatList
	 *            the new supplier child cat list
	 */
	public void setSupplierChildCatList(List<PmCategoryL2> supplierChildCatList) {
		this.supplierChildCatList = supplierChildCatList;
	}

	/**
	 * Gets the supplier sub child cat list.
	 * 
	 * @return the supplierSubChildCatList
	 */
	public List<PmCategoryL3> getSupplierSubChildCatList() {
		return supplierSubChildCatList;
	}

	/**
	 * Sets the supplier sub child cat list.
	 * 
	 * @param supplierSubChildCatList
	 *            the new supplier sub child cat list
	 */
	public void setSupplierSubChildCatList(
			List<PmCategoryL3> supplierSubChildCatList) {
		this.supplierSubChildCatList = supplierSubChildCatList;
	}

	/**
	 * Gets the supplier parent cat list.
	 * 
	 * @return the supplierParentCatList
	 */
	public List<PmCategoryL1> getSupplierParentCatList() {
		return supplierParentCatList;
	}

	/**
	 * Sets the supplier parent cat list.
	 * 
	 * @param supplierParentCatList
	 *            the new supplier parent cat list
	 */
	public void setSupplierParentCatList(
			List<PmCategoryL1> supplierParentCatList) {
		this.supplierParentCatList = supplierParentCatList;
	}

	/**
	 * Checks if is selected all.
	 * 
	 * @return true, if is selected all
	 */
	public boolean isSelectedAll() {
		return selectedAll;
	}

	/**
	 * Sets the selected all.
	 * 
	 * @param selectedAll
	 *            the new selected all
	 */
	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	/**
	 * Checks if is selected.
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the selected.
	 * 
	 * @param selected
	 *            the new selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Gets the selected cat list.
	 * 
	 * @return the selectedCatList
	 */
	public List<SmSupplierCategory> getSelectedCatList() {
		return selectedCatList;
	}

	/**
	 * Sets the selected cat list.
	 * 
	 * @param selectedCatList
	 *            the new selected cat list
	 */
	public void setSelectedCatList(List<SmSupplierCategory> selectedCatList) {
		this.selectedCatList = selectedCatList;
	}

	/**
	 * Gets the registration status list.
	 * 
	 * @return the registrationStatusList
	 */
	public List<SelectItem> getRegistrationStatusList() {
		return registrationStatusList;
	}

	/**
	 * Sets the registration status list.
	 * 
	 * @param registrationStatusList
	 *            the new registration status list
	 */
	public void setRegistrationStatusList(
			List<SelectItem> registrationStatusList) {
		this.registrationStatusList = registrationStatusList;
	}

	/**
	 * Gets the string zone.
	 * 
	 * @return the string zone
	 */
	public String getStringZone() {
		return stringZone;
	}

	/**
	 * Sets the string zone.
	 * 
	 * @param stringZone
	 *            the new string zone
	 */
	public void setStringZone(String stringZone) {
		this.stringZone = stringZone;
	}

	/**
	 * Checks if is value reqd.
	 * 
	 * @return true, if is value reqd
	 */
	public boolean isValueReqd() {
		return valueReqd;
	}

	/**
	 * Sets the value reqd.
	 * 
	 * @param valueReqd
	 *            the new value reqd
	 */
	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	/**
	 * Gets the brand list.
	 * 
	 * @return the brandList
	 */
	public List<SelectItem> getBrandList() {
		return brandList;
	}

	/**
	 * Sets the brand list.
	 * 
	 * @param brandList
	 *            the new brand list
	 */
	public void setBrandList(List<SelectItem> brandList) {
		this.brandList = brandList;
	}

	/**
	 * Gets the ext type list.
	 * 
	 * @return the extTypeList
	 */
	public List<SelectItem> getExtTypeList() {
		return extTypeList;
	}

	/**
	 * Sets the ext type list.
	 * 
	 * @param extTypeList
	 *            the new ext type list
	 */
	public void setExtTypeList(List<SelectItem> extTypeList) {
		this.extTypeList = extTypeList;
	}

	/**
	 * Gets the measurment list.
	 * 
	 * @return the measurmentList
	 */
	public List<SelectItem> getMeasurmentList() {
		return measurmentList;
	}

	/**
	 * Sets the measurment list.
	 * 
	 * @param measurmentList
	 *            the new measurment list
	 */
	public void setMeasurmentList(List<SelectItem> measurmentList) {
		this.measurmentList = measurmentList;
	}

	/**
	 * Gets the color list.
	 * 
	 * @return the colorList
	 */
	public List<SelectItem> getColorList() {
		return colorList;
	}

	/**
	 * Sets the color list.
	 * 
	 * @param colorList
	 *            the new color list
	 */
	public void setColorList(List<SelectItem> colorList) {
		this.colorList = colorList;
	}

	/**
	 * Gets the unspid.
	 * 
	 * @return the unspid
	 */
	public Long getUnspid() {
		return unspid;
	}

	/**
	 * Sets the unspid.
	 * 
	 * @param unspid
	 *            the new unspid
	 */
	public void setUnspid(Long unspid) {
		this.unspid = unspid;
	}

	/**
	 * Gets the unspc list.
	 * 
	 * @return the unspcList
	 */
	public List<SelectItem> getUnspcList() {
		return unspcList;
	}

	/**
	 * Sets the unspc list.
	 * 
	 * @param unspcList
	 *            the new unspc list
	 */
	public void setUnspcList(List<SelectItem> unspcList) {
		this.unspcList = unspcList;
	}

	/**
	 * Gets the report value.
	 * 
	 * @return the reportValue
	 */
	public String getReportValue() {
		return reportValue;
	}

	/**
	 * Sets the report value.
	 * 
	 * @param reportValue
	 *            the new report value
	 */
	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	/**
	 * Gets the report name.
	 * 
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * Sets the report name.
	 * 
	 * @param reportName
	 *            the new report name
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * Gets the module value.
	 * 
	 * @return themoduleValue
	 */
	public String getModuleValue() {
		return moduleValue;
	}

	/**
	 * Sets the module value.
	 * 
	 * @param moduleValue
	 *            the new module value
	 */
	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}

	/**
	 * Gets the ct report backing bean.
	 * 
	 * @return the ctReportBackingBean
	 */
	public CTReportBackingBean getCtReportBackingBean() {
		return ctReportBackingBean;
	}

	/**
	 * Sets the ct report backing bean.
	 * 
	 * @param ctReportBackingBean
	 *            the new ct report backing bean
	 */
	public void setCtReportBackingBean(CTReportBackingBean ctReportBackingBean) {
		this.ctReportBackingBean = ctReportBackingBean;
	}

	/**
	 * Gets the sc report backing bean.
	 * 
	 * @return the scReportBackingBean
	 */
	public SCReportBackingBean getScReportBackingBean() {
		return scReportBackingBean;
	}

	/**
	 * Sets the sc report backing bean.
	 * 
	 * @param scReportBackingBean
	 *            the new sc report backing bean
	 */
	public void setScReportBackingBean(SCReportBackingBean scReportBackingBean) {
		this.scReportBackingBean = scReportBackingBean;
	}

	/**
	 * Gets the cm report backing bean.
	 * 
	 * @return the cmReportBackingBean
	 */
	public CMReportBackingBean getCmReportBackingBean() {
		return cmReportBackingBean;
	}

	/**
	 * Sets the cm report backing bean.
	 * 
	 * @param cmReportBackingBean
	 *            the new cm report backing bean
	 */
	public void setCmReportBackingBean(CMReportBackingBean cmReportBackingBean) {
		this.cmReportBackingBean = cmReportBackingBean;
	}

	/**
	 * Gets the fl report backing bean.
	 * 
	 * @return the flReportBackingBean
	 */
	public FLReportBackingBean getFlReportBackingBean() {
		return flReportBackingBean;
	}

	/**
	 * Sets the fl report backing bean.
	 * 
	 * @param flReportBackingBean
	 *            the new fl report backing bean
	 */
	public void setFlReportBackingBean(FLReportBackingBean flReportBackingBean) {
		this.flReportBackingBean = flReportBackingBean;
	}

	/**
	 * Gets the sm report backing bean.
	 * 
	 * @return the smReportBackingBean
	 */
	public SMReportBackingBean getSmReportBackingBean() {
		return smReportBackingBean;
	}

	/**
	 * Sets the sm report backing bean.
	 * 
	 * @param smReportBackingBean
	 *            the new sm report backing bean
	 */
	public void setSmReportBackingBean(SMReportBackingBean smReportBackingBean) {
		this.smReportBackingBean = smReportBackingBean;
	}

	/**
	 * Gets the pm report backing bean.
	 * 
	 * @return the pmReportBackingBean
	 */
	public PMReportBackingBean getPmReportBackingBean() {
		return pmReportBackingBean;
	}

	/**
	 * Sets the pm report backing bean.
	 * 
	 * @param pmReportBackingBean
	 *            the new pm report backing bean
	 */
	public void setPmReportBackingBean(PMReportBackingBean pmReportBackingBean) {
		this.pmReportBackingBean = pmReportBackingBean;
	}

	/**
	 * Gets the pp report backing bean.
	 * 
	 * @return the ppReportBackingBean
	 */
	public PPReportBackingBean getPpReportBackingBean() {
		return ppReportBackingBean;
	}

	/**
	 * Sets the pp report backing bean.
	 * 
	 * @param ppReportBackingBean
	 *            the new pp report backing bean
	 */
	public void setPpReportBackingBean(PPReportBackingBean ppReportBackingBean) {
		this.ppReportBackingBean = ppReportBackingBean;
	}

	/**
	 * Gets the org profile id name list.
	 * 
	 * @return the orgProfileIdNameList
	 */
	public List<SelectItem> getOrgProfileIdNameList() {
		return orgProfileIdNameList;
	}

	/**
	 * Sets the org profile id name list.
	 * 
	 * @param orgProfileIdNameList
	 *            the new org profile id name list
	 */
	public void setOrgProfileIdNameList(List<SelectItem> orgProfileIdNameList) {
		this.orgProfileIdNameList = orgProfileIdNameList;
	}

	/**
	 * Gets the sc month list.
	 * 
	 * @return the scMonthList
	 */
	public List<SelectItem> getScMonthList() {
		return scMonthList;
	}

	/**
	 * Sets the sc month list.
	 * 
	 * @param scMonthList
	 *            the new sc month list
	 */
	public void setScMonthList(List<SelectItem> scMonthList) {
		this.scMonthList = scMonthList;
	}

	/**
	 * Gets the sc year list.
	 * 
	 * @return the scYearList
	 */
	public List<String> getScYearList() {
		return scYearList;
	}

	/**
	 * Sets the sc year list.
	 * 
	 * @param scYearList
	 *            the new sc year list
	 */
	public void setScYearList(List<String> scYearList) {
		this.scYearList = scYearList;
	}

	/**
	 * Gets the user listbyprofile role.
	 * 
	 * @return the userListbyprofileRole
	 */
	public List<SelectItem> getUserListbyprofileRole() {
		return userListbyprofileRole;
	}

	/**
	 * Sets the user listbyprofile role.
	 * 
	 * @param userListbyprofileRole
	 *            the new user listbyprofile role
	 */
	public void setUserListbyprofileRole(List<SelectItem> userListbyprofileRole) {
		this.userListbyprofileRole = userListbyprofileRole;
	}

	/**
	 * Gets the org profile list.
	 * 
	 * @return the orgProfileList
	 */
	public List<String> getOrgProfileList() {
		return orgProfileList;
	}

	/**
	 * Sets the org profile list.
	 * 
	 * @param orgProfileList
	 *            the new org profile list
	 */
	public void setOrgProfileList(List<String> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	/**
	 * Gets the loan list.
	 * 
	 * @return the loanList
	 */
	public List<String> getLoanList() {
		return loanList;
	}

	/**
	 * Sets the loan list.
	 * 
	 * @param loanList
	 *            the new loan list
	 */
	public void setLoanList(List<String> loanList) {
		this.loanList = loanList;
	}

	/**
	 * Gets the requisitioner list.
	 * 
	 * @return the requisitionerList
	 */
	public List<SelectItem> getRequisitionerList() {
		return requisitionerList;
	}

	/**
	 * Sets the requisitioner list.
	 * 
	 * @param requisitionerList
	 *            the new requisitioner list
	 */
	public void setRequisitionerList(List<SelectItem> requisitionerList) {
		this.requisitionerList = requisitionerList;
	}

	/**
	 * Gets the status list.
	 * 
	 * @return the statusList
	 */
	public List<SelectItem> getStatusList() {
		return statusList;
	}

	/**
	 * Sets the status list.
	 * 
	 * @param statusList
	 *            the new status list
	 */
	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}

	/**
	 * Gets the parameter code type.
	 * 
	 * @return the parameterCodeType
	 */
	public String getParameterCodeType() {
		return parameterCodeType;
	}

	/**
	 * Sets the parameter code type.
	 * 
	 * @param parameterCodeType
	 *            the new parameter code type
	 */
	public void setParameterCodeType(String parameterCodeType) {
		this.parameterCodeType = parameterCodeType;
	}

	/**
	 * Gets the ministry list.
	 * 
	 * @return the ministryList
	 */
	public List<SelectItem> getMinistryList() {
		return ministryList;
	}

	/**
	 * Sets the ministry list.
	 * 
	 * @param ministryList
	 *            the new ministry list
	 */
	public void setMinistryList(List<SelectItem> ministryList) {
		this.ministryList = ministryList;
	}

	/**
	 * Gets the ptj list.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> getPtjList() {
		return ptjList;
	}

	/**
	 * Sets the ptj list.
	 * 
	 * @param ptjList
	 *            the new ptj list
	 */
	public void setPtjList(List<SelectItem> ptjList) {
		this.ptjList = ptjList;
	}

	/** The adding state list. */
	private List<String> addingStateList = new ArrayList<String>();

	/** The adding district list. */
	private List<String> addingDistrictList = new ArrayList<String>();

	/**
	 * Gets the adding state list.
	 * 
	 * @return the adding state list
	 */
	public List<String> getAddingStateList() {
		return addingStateList;
	}

	/**
	 * Sets the adding state list.
	 * 
	 * @param addingStateList
	 *            the new adding state list
	 */
	public void setAddingStateList(List<String> addingStateList) {
		this.addingStateList = addingStateList;
	}

	/**
	 * Gets the adding district list.
	 * 
	 * @return the addingDistrictList
	 */
	public List<String> getAddingDistrictList() {
		return addingDistrictList;
	}

	/**
	 * Sets the adding district list.
	 * 
	 * @param addingDistrictList
	 *            the new adding district list
	 */
	public void setAddingDistrictList(List<String> addingDistrictList) {
		this.addingDistrictList = addingDistrictList;
	}

	/**
	 * Gets the cm request number.
	 * 
	 * @return the cmRequestNumber
	 */
	public String getCmRequestNumber() {
		return cmRequestNumber;
	}

	/**
	 * Sets the cm request number.
	 * 
	 * @param cmRequestNumber
	 *            the new cm request number
	 */
	public void setCmRequestNumber(String cmRequestNumber) {
		this.cmRequestNumber = cmRequestNumber;
	}

	/**
	 * Gets the ct supplier name.
	 * 
	 * @return the ctSupplierName
	 */
	public String getCtSupplierName() {
		return ctSupplierName;
	}

	/**
	 * Sets the ct supplier name.
	 * 
	 * @param ctSupplierName
	 *            the new ct supplier name
	 */
	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}

	/**
	 * Gets the ct circular effective from date.
	 * 
	 * @return the ctCircularEffectiveFromDate
	 */
	public Date getCtCircularEffectiveFromDate() {
		return ctCircularEffectiveFromDate;
	}

	/**
	 * Sets the ct circular effective from date.
	 * 
	 * @param ctCircularEffectiveFromDate
	 *            the new ct circular effective from date
	 */
	public void setCtCircularEffectiveFromDate(Date ctCircularEffectiveFromDate) {
		this.ctCircularEffectiveFromDate = ctCircularEffectiveFromDate;
	}

	/**
	 * Gets the ct circular effective to date.
	 * 
	 * @return the ctCircularEffectiveToDate
	 */
	public Date getCtCircularEffectiveToDate() {
		return ctCircularEffectiveToDate;
	}

	/**
	 * Sets the ct circular effective to date.
	 * 
	 * @param ctCircularEffectiveToDate
	 *            the new ct circular effective to date
	 */
	public void setCtCircularEffectiveToDate(Date ctCircularEffectiveToDate) {
		this.ctCircularEffectiveToDate = ctCircularEffectiveToDate;
	}

	/**
	 * Gets the ct circular no.
	 * 
	 * @return the ctCircularNo
	 */
	public String getCtCircularNo() {
		return ctCircularNo;
	}

	/**
	 * Sets the ct circular no.
	 * 
	 * @param ctCircularNo
	 *            the new ct circular no
	 */
	public void setCtCircularNo(String ctCircularNo) {
		this.ctCircularNo = ctCircularNo;
	}

	/**
	 * Gets the ct circular name.
	 * 
	 * @return the ctCircularName
	 */
	public String getCtCircularName() {
		return ctCircularName;
	}

	/**
	 * Sets the ct circular name.
	 * 
	 * @param ctCircularName
	 *            the new ct circular name
	 */
	public void setCtCircularName(String ctCircularName) {
		this.ctCircularName = ctCircularName;
	}

	/**
	 * Gets the ct circular type.
	 * 
	 * @return the ctCircularType
	 */
	public String getCtCircularType() {
		return ctCircularType;
	}

	/**
	 * Sets the ct circular type.
	 * 
	 * @param ctCircularType
	 *            the new ct circular type
	 */
	public void setCtCircularType(String ctCircularType) {
		this.ctCircularType = ctCircularType;
	}

	/**
	 * Gets the ct termination type.
	 * 
	 * @return the ctTerminationType
	 */
	public String getCtTerminationType() {
		return ctTerminationType;
	}

	/**
	 * Sets the ct termination type.
	 * 
	 * @param ctTerminationType
	 *            the new ct termination type
	 */
	public void setCtTerminationType(String ctTerminationType) {
		this.ctTerminationType = ctTerminationType;
	}

	/**
	 * Gets the sm payment to.
	 * 
	 * @return the smPaymentTo
	 */
	public String getSmPaymentTo() {
		return smPaymentTo;
	}

	/**
	 * Sets the sm payment to.
	 * 
	 * @param smPaymentTo
	 *            the new sm payment to
	 */
	public void setSmPaymentTo(String smPaymentTo) {
		this.smPaymentTo = smPaymentTo;
	}

	/**
	 * Gets the str language.
	 * 
	 * @return the strLanguage
	 */
	public String getStrLanguage() {
		return strLanguage;
	}

	/**
	 * Sets the str language.
	 * 
	 * @param strLanguage
	 *            the new str language
	 */
	public void setStrLanguage(String strLanguage) {
		this.strLanguage = strLanguage;
	}

	/**
	 * Gets the pm status.
	 * 
	 * @return the pmStatus
	 */
	public String getPmStatus() {
		return pmStatus;
	}

	/**
	 * Sets the pm status.
	 * 
	 * @param pmStatus
	 *            the new pm status
	 */
	public void setPmStatus(String pmStatus) {
		this.pmStatus = pmStatus;
	}

	/**
	 * Gets the pm financial org name.
	 * 
	 * @return the pmFinancialOrgName
	 */
	public String getPmFinancialOrgName() {
		return pmFinancialOrgName;
	}

	/**
	 * Sets the pm financial org name.
	 * 
	 * @param pmFinancialOrgName
	 *            the new pm financial org name
	 */
	public void setPmFinancialOrgName(String pmFinancialOrgName) {
		this.pmFinancialOrgName = pmFinancialOrgName;
	}

	/**
	 * Gets the pm business reg no.
	 * 
	 * @return the pmBusinessRegNo
	 */
	public String getPmBusinessRegNo() {
		return pmBusinessRegNo;
	}

	/**
	 * Sets the pm business reg no.
	 * 
	 * @param pmBusinessRegNo
	 *            the new pm business reg no
	 */
	public void setPmBusinessRegNo(String pmBusinessRegNo) {
		this.pmBusinessRegNo = pmBusinessRegNo;
	}

	/**
	 * Gets the pm service type.
	 * 
	 * @return the pmServiceType
	 */
	public String getPmServiceType() {
		return pmServiceType;
	}

	/**
	 * Sets the pm service type.
	 * 
	 * @param pmServiceType
	 *            the new pm service type
	 */
	public void setPmServiceType(String pmServiceType) {
		this.pmServiceType = pmServiceType;
	}

	/**
	 * Gets the fl payment advice no.
	 * 
	 * @return the flPaymentAdviceNo
	 */
	public String getFlPaymentAdviceNo() {
		return flPaymentAdviceNo;
	}

	/**
	 * Sets the fl payment advice no.
	 * 
	 * @param flPaymentAdviceNo
	 *            the new fl payment advice no
	 */
	public void setFlPaymentAdviceNo(String flPaymentAdviceNo) {
		this.flPaymentAdviceNo = flPaymentAdviceNo;
	}

	/**
	 * Gets the fl supplier delivery order no.
	 * 
	 * @return the flSupplierDeliveryOrderNo
	 */
	public String getFlSupplierDeliveryOrderNo() {
		return flSupplierDeliveryOrderNo;
	}

	/**
	 * Sets the fl supplier delivery order no.
	 * 
	 * @param flSupplierDeliveryOrderNo
	 *            the new fl supplier delivery order no
	 */
	public void setFlSupplierDeliveryOrderNo(String flSupplierDeliveryOrderNo) {
		this.flSupplierDeliveryOrderNo = flSupplierDeliveryOrderNo;
	}

	/**
	 * Gets the sm gst registration no.
	 * 
	 * @return the smGSTRegistrationNo
	 */
	public String getSmGSTRegistrationNo() {
		return smGSTRegistrationNo;
	}

	/**
	 * Sets the sm gst registration no.
	 * 
	 * @param smGSTRegistrationNo
	 *            the new sm gst registration no
	 */
	public void setSmGSTRegistrationNo(String smGSTRegistrationNo) {
		this.smGSTRegistrationNo = smGSTRegistrationNo;
	}

	/**
	 * Gets the sm gst registered status.
	 * 
	 * @return the smGSTRegisteredStatus
	 */
	public String getSmGSTRegisteredStatus() {
		return smGSTRegisteredStatus;
	}

	/**
	 * Sets the sm gst registered status.
	 * 
	 * @param smGSTRegisteredStatus
	 *            the new sm gst registered status
	 */
	public void setSmGSTRegisteredStatus(String smGSTRegisteredStatus) {
		this.smGSTRegisteredStatus = smGSTRegisteredStatus;
	}

	/**
	 * Gets the sm profile status.
	 * 
	 * @return the smProfileStatus
	 */
	public String getSmProfileStatus() {
		return smProfileStatus;
	}

	/**
	 * Sets the sm profile status.
	 * 
	 * @param smProfileStatus
	 *            the new sm profile status
	 */
	public void setSmProfileStatus(String smProfileStatus) {
		this.smProfileStatus = smProfileStatus;
	}

	/**
	 * Gets the fl supplier invoice no.
	 * 
	 * @return the flSupplierInvoiceNo
	 */
	public String getFlSupplierInvoiceNo() {
		return flSupplierInvoiceNo;
	}

	/**
	 * Sets the fl supplier invoice no.
	 * 
	 * @param flSupplierInvoiceNo
	 *            the new fl supplier invoice no
	 */
	public void setFlSupplierInvoiceNo(String flSupplierInvoiceNo) {
		this.flSupplierInvoiceNo = flSupplierInvoiceNo;
	}

	/**
	 * Gets the fl stop instruction no.
	 * 
	 * @return the fflStopInstructionNo
	 */
	public String getFlStopInstructionNo() {
		return flStopInstructionNo;
	}

	/**
	 * Sets the fl stop instruction no.
	 * 
	 * @param flStopInstructionNo
	 *            the new fl stop instruction no
	 */
	public void setFlStopInstructionNo(String flStopInstructionNo) {
		this.flStopInstructionNo = flStopInstructionNo;
	}

	/**
	 * Gets the sm request expiry date.
	 * 
	 * @return the smRequestExpiryDate
	 */
	public String getSmRequestExpiryDate() {
		return smRequestExpiryDate;
	}

	/**
	 * Sets the sm request expiry date.
	 * 
	 * @param smRequestExpiryDate
	 *            the new sm request expiry date
	 */
	public void setSmRequestExpiryDate(String smRequestExpiryDate) {
		this.smRequestExpiryDate = smRequestExpiryDate;
	}

	/**
	 * Gets the sm user name.
	 * 
	 * @return the smUserName
	 */
	public String getSmUserName() {
		return smUserName;
	}

	/**
	 * Sets the sm user name.
	 * 
	 * @param smUserName
	 *            the new sm user name
	 */
	public void setSmUserName(String smUserName) {
		this.smUserName = smUserName;
	}

	/**
	 * Gets the sm threshold from year.
	 * 
	 * @return the smThresholdFromYear
	 */
	public String getSmThresholdFromYear() {
		return smThresholdFromYear;
	}

	/**
	 * Sets the sm threshold from year.
	 * 
	 * @param smThresholdFromYear
	 *            the new sm threshold from year
	 */
	public void setSmThresholdFromYear(String smThresholdFromYear) {
		this.smThresholdFromYear = smThresholdFromYear;
	}

	/**
	 * Gets the sm threshold to year.
	 * 
	 * @return the smThresholdToYear
	 */
	public String getSmThresholdToYear() {
		return smThresholdToYear;
	}

	/**
	 * Sets the sm threshold to year.
	 * 
	 * @param smThresholdToYear
	 *            the new sm threshold to year
	 */
	public void setSmThresholdToYear(String smThresholdToYear) {
		this.smThresholdToYear = smThresholdToYear;
	}

	/**
	 * Gets the sm from month.
	 * 
	 * @return the smFromMonth
	 */
	public String getSmFromMonth() {
		return smFromMonth;
	}

	/**
	 * Sets the sm from month.
	 * 
	 * @param smFromMonth
	 *            the new sm from month
	 */
	public void setSmFromMonth(String smFromMonth) {
		this.smFromMonth = smFromMonth;
	}

	/**
	 * Gets the sm to month.
	 * 
	 * @return the smToMonth
	 */
	public String getSmToMonth() {
		return smToMonth;
	}

	/**
	 * Sets the sm to month.
	 * 
	 * @param smToMonth
	 *            the new sm to month
	 */
	public void setSmToMonth(String smToMonth) {
		this.smToMonth = smToMonth;
	}

	/**
	 * Checks if is report category view.
	 * 
	 * @return true, if is report category view
	 */
	public boolean isReportCategoryView() {
		return reportCategoryView;
	}

	/**
	 * Sets the report category view.
	 * 
	 * @param reportCategoryView
	 *            the new report category view
	 */
	public void setReportCategoryView(boolean reportCategoryView) {
		this.reportCategoryView = reportCategoryView;
	}

	/**
	 * Gets the ministry name.
	 * 
	 * @return the ministryName
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
	 * Gets the sm payment method.
	 * 
	 * @return the smPaymentMethod
	 */
	public String getSmPaymentMethod() {
		return smPaymentMethod;
	}

	/**
	 * Sets the sm payment method.
	 * 
	 * @param smPaymentMethod
	 *            the new sm payment method
	 */
	public void setSmPaymentMethod(String smPaymentMethod) {
		this.smPaymentMethod = smPaymentMethod;
	}

	/**
	 * Gets the sm processing type.
	 * 
	 * @return the smProcessingType
	 */
	public String getSmProcessingType() {
		return smProcessingType;
	}

	/**
	 * Sets the sm processing type.
	 * 
	 * @param smProcessingType
	 *            the new sm processing type
	 */
	public void setSmProcessingType(String smProcessingType) {
		this.smProcessingType = smProcessingType;
	}

	/**
	 * Gets the sm receipt no.
	 * 
	 * @return the smReceiptNo
	 */
	public String getSmReceiptNo() {
		return smReceiptNo;
	}

	/**
	 * Sets the sm receipt no.
	 * 
	 * @param smReceiptNo
	 *            the new sm receipt no
	 */
	public void setSmReceiptNo(String smReceiptNo) {
		this.smReceiptNo = smReceiptNo;
	}

	/**
	 * Gets the sm processing officer name.
	 * 
	 * @return the smProcessingOfficerName
	 */
	public String getSmProcessingOfficerName() {
		return smProcessingOfficerName;
	}

	/**
	 * Sets the sm processing officer name.
	 * 
	 * @param smProcessingOfficerName
	 *            the new sm processing officer name
	 */
	public void setSmProcessingOfficerName(String smProcessingOfficerName) {
		this.smProcessingOfficerName = smProcessingOfficerName;
	}

	/**
	 * Gets the sm identification type.
	 * 
	 * @return the smIdentificationType
	 */
	public String getSmIdentificationType() {
		return smIdentificationType;
	}

	/**
	 * Sets the sm identification type.
	 * 
	 * @param smIdentificationType
	 *            the new sm identification type
	 */
	public void setSmIdentificationType(String smIdentificationType) {
		this.smIdentificationType = smIdentificationType;
	}

	/**
	 * Gets the sm identification number.
	 * 
	 * @return the smIdentificationNumber
	 */
	public String getSmIdentificationNumber() {
		return smIdentificationNumber;
	}

	/**
	 * Sets the sm identification number.
	 * 
	 * @param smIdentificationNumber
	 *            the new sm identification number
	 */
	public void setSmIdentificationNumber(String smIdentificationNumber) {
		this.smIdentificationNumber = smIdentificationNumber;
	}

	/**
	 * Gets the sm cancellation by.
	 * 
	 * @return the smCancellationBy
	 */
	public String getSmCancellationBy() {
		return smCancellationBy;
	}

	/**
	 * Sets the sm cancellation by.
	 * 
	 * @param smCancellationBy
	 *            the new sm cancellation by
	 */
	public void setSmCancellationBy(String smCancellationBy) {
		this.smCancellationBy = smCancellationBy;
	}

	/**
	 * Gets the sm personnel role.
	 * 
	 * @return the smPersonnelRole
	 */
	public String getSmPersonnelRole() {
		return smPersonnelRole;
	}

	/**
	 * Sets the sm personnel role.
	 * 
	 * @param smPersonnelRole
	 *            the new sm personnel role
	 */
	public void setSmPersonnelRole(String smPersonnelRole) {
		this.smPersonnelRole = smPersonnelRole;
	}

	/**
	 * Gets the sm mof reg no from.
	 * 
	 * @return the smMofRegNoFrom
	 */
	public String getSmMofRegNoFrom() {
		return smMofRegNoFrom;
	}

	/**
	 * Gets the sm code.
	 * 
	 * @return the smCode
	 */
	public String getSmCode() {
		return smCode;
	}

	/**
	 * Sets the sm code.
	 * 
	 * @param smCode
	 *            the smCode to set
	 */
	public void setSmCode(String smCode) {
		this.smCode = smCode;
	}

	/**
	 * Gets the payment method.
	 * 
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * Sets the payment method.
	 * 
	 * @param paymentMethod
	 *            the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * Sets the sm mof reg no from.
	 * 
	 * @param smMofRegNoFrom
	 *            the new sm mof reg no from
	 */
	public void setSmMofRegNoFrom(String smMofRegNoFrom) {
		this.smMofRegNoFrom = smMofRegNoFrom;
	}

	/**
	 * Gets the sm mof reg no to.
	 * 
	 * @return the smMofRegNoTo
	 */
	public String getSmMofRegNoTo() {
		return smMofRegNoTo;
	}

	/**
	 * Sets the sm mof reg no to.
	 * 
	 * @param smMofRegNoTo
	 *            the new sm mof reg no to
	 */
	public void setSmMofRegNoTo(String smMofRegNoTo) {
		this.smMofRegNoTo = smMofRegNoTo;
	}

	/**
	 * Gets the sm paid up capital frm.
	 * 
	 * @return the smPaidUpCapitalFrm
	 */
	public String getSmPaidUpCapitalFrm() {
		return smPaidUpCapitalFrm;
	}

	/**
	 * Gets the sm bus paid up capital frm.
	 * 
	 * @return the smBusPaidUpCapitalFrm
	 */
	public String getSmBusPaidUpCapitalFrm() {
		return smBusPaidUpCapitalFrm;
	}

	/**
	 * Sets the sm bus paid up capital frm.
	 * 
	 * @param smBusPaidUpCapitalFrm
	 *            the smBusPaidUpCapitalFrm to set
	 */
	public void setSmBusPaidUpCapitalFrm(String smBusPaidUpCapitalFrm) {
		this.smBusPaidUpCapitalFrm = smBusPaidUpCapitalFrm;
	}

	/**
	 * Gets the sm bus paid up capital to.
	 * 
	 * @return the smBusPaidUpCapitalTo
	 */
	public String getSmBusPaidUpCapitalTo() {
		return smBusPaidUpCapitalTo;
	}

	/**
	 * Sets the sm bus paid up capital to.
	 * 
	 * @param smBusPaidUpCapitalTo
	 *            the smBusPaidUpCapitalTo to set
	 */
	public void setSmBusPaidUpCapitalTo(String smBusPaidUpCapitalTo) {
		this.smBusPaidUpCapitalTo = smBusPaidUpCapitalTo;
	}

	/**
	 * Sets the sm paid up capital frm.
	 * 
	 * @param smPaidUpCapitalFrm
	 *            the new sm paid up capital frm
	 */
	public void setSmPaidUpCapitalFrm(String smPaidUpCapitalFrm) {
		this.smPaidUpCapitalFrm = smPaidUpCapitalFrm;
	}

	/**
	 * Gets the sm paid up capital to.
	 * 
	 * @return the smPaidUpCapitalTo
	 */
	public String getSmPaidUpCapitalTo() {
		return smPaidUpCapitalTo;
	}

	/**
	 * Sets the sm paid up capital to.
	 * 
	 * @param smPaidUpCapitalTo
	 *            the new sm paid up capital to
	 */
	public void setSmPaidUpCapitalTo(String smPaidUpCapitalTo) {
		this.smPaidUpCapitalTo = smPaidUpCapitalTo;
	}

	/**
	 * Gets the sm paid up capital naccum.
	 * 
	 * @return the smPaidUpCapitalNaccum
	 */
	public String getSmPaidUpCapitalNaccum() {
		return smPaidUpCapitalNaccum;
	}

	/**
	 * Sets the sm paid up capital naccum.
	 * 
	 * @param smPaidUpCapitalNaccum
	 *            the new sm paid up capital naccum
	 */
	public void setSmPaidUpCapitalNaccum(String smPaidUpCapitalNaccum) {
		this.smPaidUpCapitalNaccum = smPaidUpCapitalNaccum;
	}

	/**
	 * Gets the sm role.
	 * 
	 * @return the smRole
	 */
	public String getSmRole() {
		return smRole;
	}

	/**
	 * Sets the sm role.
	 * 
	 * @param smRole
	 *            the new sm role
	 */
	public void setSmRole(String smRole) {
		this.smRole = smRole;
	}

	/**
	 * Gets the sm ep no.
	 * 
	 * @return the smEpNo
	 */
	public String getSmEpNo() {
		return smEpNo;
	}

	/**
	 * Sets the sm ep no.
	 * 
	 * @param smEpNo
	 *            the new sm ep no
	 */
	public void setSmEpNo(String smEpNo) {
		this.smEpNo = smEpNo;
	}

	/**
	 * Gets the sm registration no.
	 * 
	 * @return the smRegistrationNo
	 */
	public String getSmRegistrationNo() {
		return smRegistrationNo;
	}

	/**
	 * Sets the sm registration no.
	 * 
	 * @param smRegistrationNo
	 *            the new sm registration no
	 */
	public void setSmRegistrationNo(String smRegistrationNo) {
		this.smRegistrationNo = smRegistrationNo;
	}

	/**
	 * Gets the sm supporting document mode.
	 * 
	 * @return the smSupportingDocumentMode
	 */
	public String getSmSupportingDocumentMode() {
		return smSupportingDocumentMode;
	}

	/**
	 * Sets the sm supporting document mode.
	 * 
	 * @param smSupportingDocumentMode
	 *            the new sm supporting document mode
	 */
	public void setSmSupportingDocumentMode(String smSupportingDocumentMode) {
		this.smSupportingDocumentMode = smSupportingDocumentMode;
	}

	/**
	 * Gets the sm supporting document status.
	 * 
	 * @return the smSupportingDocumentStatus
	 */
	public String getSmSupportingDocumentStatus() {
		return smSupportingDocumentStatus;
	}

	/**
	 * Sets the sm supporting document status.
	 * 
	 * @param smSupportingDocumentStatus
	 *            the new sm supporting document status
	 */
	public void setSmSupportingDocumentStatus(String smSupportingDocumentStatus) {
		this.smSupportingDocumentStatus = smSupportingDocumentStatus;
	}

	/**
	 * Gets the lng district id.
	 * 
	 * @return the lngDistrictId
	 */
	public long[] getLngDistrictId() {
		return lngDistrictId;
	}

	/**
	 * Sets the lng district id.
	 * 
	 * @param lngDistrictId
	 *            the new lng district id
	 */
	public void setLngDistrictId(long[] lngDistrictId) {
		this.lngDistrictId = lngDistrictId;
	}

	/**
	 * Gets the lng state id.
	 * 
	 * @return the lngStateId
	 */
	public long[] getLngStateId() {
		return lngStateId;
	}

	/**
	 * Sets the lng state id.
	 * 
	 * @param lngStateId
	 *            the new lng state id
	 */
	public void setLngStateId(long[] lngStateId) {
		this.lngStateId = lngStateId;
	}

	/**
	 * Gets the pp year.
	 * 
	 * @return the ppYear
	 */
	public String getPpYear() {
		return ppYear;
	}

	/**
	 * Sets the pp year.
	 * 
	 * @param ppYear
	 *            the new pp year
	 */
	public void setPpYear(String ppYear) {
		this.ppYear = ppYear;
	}

	/**
	 * Gets the pp ministry.
	 * 
	 * @return the ppMinistry
	 */
	public String getPpMinistry() {
		return ppMinistry;
	}

	/**
	 * Sets the pp ministry.
	 * 
	 * @param ppMinistry
	 *            the new pp ministry
	 */
	public void setPpMinistry(String ppMinistry) {
		this.ppMinistry = ppMinistry;
	}

	/**
	 * Gets the pp ptj.
	 * 
	 * @return the ppPTJ
	 */
	public String getPpPTJ() {
		return ppPTJ;
	}

	/**
	 * Sets the pp ptj.
	 * 
	 * @param ppPTJ
	 *            the new pp ptj
	 */
	public void setPpPTJ(String ppPTJ) {
		this.ppPTJ = ppPTJ;
	}

	/**
	 * Gets the sc purchase in default status.
	 * 
	 * @return the scPurchaseInDefaultStatus
	 */
	public String getScPurchaseInDefaultStatus() {
		return scPurchaseInDefaultStatus;
	}

	/**
	 * Sets the sc purchase in default status.
	 * 
	 * @param scPurchaseInDefaultStatus
	 *            the new sc purchase in default status
	 */
	public void setScPurchaseInDefaultStatus(String scPurchaseInDefaultStatus) {
		this.scPurchaseInDefaultStatus = scPurchaseInDefaultStatus;
	}

	/**
	 * Gets the sc simple quote dtl group.
	 * 
	 * @return the scSimpleQuoteDtlGroup
	 */
	public String getScSimpleQuoteDtlGroup() {
		return scSimpleQuoteDtlGroup;
	}

	/**
	 * Sets the sc simple quote dtl group.
	 * 
	 * @param scSimpleQuoteDtlGroup
	 *            the new sc simple quote dtl group
	 */
	public void setScSimpleQuoteDtlGroup(String scSimpleQuoteDtlGroup) {
		this.scSimpleQuoteDtlGroup = scSimpleQuoteDtlGroup;
	}

	/**
	 * Gets the sc simple quote status.
	 * 
	 * @return the scSimpleQuoteStatus
	 */
	public String getScSimpleQuoteStatus() {
		return scSimpleQuoteStatus;
	}

	/**
	 * Sets the sc simple quote status.
	 * 
	 * @param scSimpleQuoteStatus
	 *            the new sc simple quote status
	 */
	public void setScSimpleQuoteStatus(String scSimpleQuoteStatus) {
		this.scSimpleQuoteStatus = scSimpleQuoteStatus;
	}

	/**
	 * Gets the items.
	 * 
	 * @return the items
	 */
	public SelectItem[] getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 * 
	 * @param items
	 *            the new items
	 */
	public void setItems(SelectItem[] items) {
		this.items = items;
	}

	/**
	 * Gets the sc request note status.
	 * 
	 * @return the scRequestNoteStatus
	 */
	public String getScRequestNoteStatus() {
		return scRequestNoteStatus;
	}

	/**
	 * Sets the sc request note status.
	 * 
	 * @param scRequestNoteStatus
	 *            the new sc request note status
	 */
	public void setScRequestNoteStatus(String scRequestNoteStatus) {
		this.scRequestNoteStatus = scRequestNoteStatus;
	}

	/**
	 * Gets the ct sub type.
	 * 
	 * @return the ctSubType
	 */
	public String getCtSubType() {
		return ctSubType;
	}

	/**
	 * Sets the ct sub type.
	 * 
	 * @param ctSubType
	 *            the new ct sub type
	 */
	public void setCtSubType(String ctSubType) {
		this.ctSubType = ctSubType;
	}

	/**
	 * Gets the ct user type.
	 * 
	 * @return the ctUserType
	 */
	public String getCtUserType() {
		return ctUserType;
	}

	/**
	 * Sets the ct user type.
	 * 
	 * @param ctUserType
	 *            the new ct user type
	 */
	public void setCtUserType(String ctUserType) {
		this.ctUserType = ctUserType;
	}

	/**
	 * Gets the ct loa physical no.
	 * 
	 * @return the ctLOAPhysicalNo
	 */
	public String getCtLOAPhysicalNo() {
		return ctLOAPhysicalNo;
	}

	/**
	 * Sets the ct loa physical no.
	 * 
	 * @param ctLOAPhysicalNo
	 *            the new ct loa physical no
	 */
	public void setCtLOAPhysicalNo(String ctLOAPhysicalNo) {
		this.ctLOAPhysicalNo = ctLOAPhysicalNo;
	}

	/**
	 * Gets the ct status two.
	 * 
	 * @return the ctStatusTwo
	 */
	public String getCtStatusTwo() {
		return ctStatusTwo;
	}

	/**
	 * Sets the ct status two.
	 * 
	 * @param ctStatusTwo
	 *            the new ct status two
	 */
	public void setCtStatusTwo(String ctStatusTwo) {
		this.ctStatusTwo = ctStatusTwo;
	}

	/**
	 * Gets the ct group three.
	 * 
	 * @return the ctGroupThree
	 */
	public String getCtGroupThree() {
		return ctGroupThree;
	}

	/**
	 * Sets the ct group three.
	 * 
	 * @param ctGroupThree
	 *            the new ct group three
	 */
	public void setCtGroupThree(String ctGroupThree) {
		this.ctGroupThree = ctGroupThree;
	}

	/**
	 * Gets the ct physical contract no.
	 * 
	 * @return the ctPhysicalContractNo
	 */
	public String getCtPhysicalContractNo() {
		return ctPhysicalContractNo;
	}

	/**
	 * Sets the ct physical contract no.
	 * 
	 * @param ctPhysicalContractNo
	 *            the new ct physical contract no
	 */
	public void setCtPhysicalContractNo(String ctPhysicalContractNo) {
		this.ctPhysicalContractNo = ctPhysicalContractNo;
	}

	/**
	 * Gets the fl group seventeen.
	 * 
	 * @return the flGroupSeventeen
	 */
	public String getFlGroupSeventeen() {
		return flGroupSeventeen;
	}

	/**
	 * Sets the fl group seventeen.
	 * 
	 * @param flGroupSeventeen
	 *            the new fl group seventeen
	 */
	public void setFlGroupSeventeen(String flGroupSeventeen) {
		this.flGroupSeventeen = flGroupSeventeen;
	}

	/**
	 * Gets the ct procurement mode.
	 * 
	 * @return the ctProcurementMode
	 */
	public String getCtProcurementMode() {
		return ctProcurementMode;
	}

	/**
	 * Sets the ct procurement mode.
	 * 
	 * @param ctProcurementMode
	 *            the new ct procurement mode
	 */
	public void setCtProcurementMode(String ctProcurementMode) {
		this.ctProcurementMode = ctProcurementMode;
	}

	/**
	 * Gets the sm year text.
	 * 
	 * @return the smYearText
	 */
	public String getSmYearText() {
		return smYearText;
	}

	/**
	 * Sets the sm year text.
	 * 
	 * @param smYearText
	 *            the new sm year text
	 */
	public void setSmYearText(String smYearText) {
		this.smYearText = smYearText;
	}

	/**
	 * Gets the sm rt report type.
	 * 
	 * @return the smRTReportType
	 */
	public String getSmRTReportType() {
		return smRTReportType;
	}

	/**
	 * Sets the sm rt report type.
	 * 
	 * @param smRTReportType
	 *            the smRTReportType to set
	 */
	public void setSmRTReportType(String smRTReportType) {
		this.smRTReportType = smRTReportType;
	}

	/**
	 * Gets the cm group one.
	 * 
	 * @return the cmGroupOne
	 */
	public String getCmGroupOne() {
		return cmGroupOne;
	}

	/**
	 * Sets the cm group one.
	 * 
	 * @param cmGroupOne
	 *            the new cm group one
	 */
	public void setCmGroupOne(String cmGroupOne) {
		this.cmGroupOne = cmGroupOne;
	}

	/**
	 * Gets the ct loan no.
	 * 
	 * @return the ctLoanNo
	 */
	public String getCtLoanNo() {
		return ctLoanNo;
	}

	/**
	 * Sets the ct loan no.
	 * 
	 * @param ctLoanNo
	 *            the new ct loan no
	 */
	public void setCtLoanNo(String ctLoanNo) {
		this.ctLoanNo = ctLoanNo;
	}

	/**
	 * Gets the ct product service.
	 * 
	 * @return the ctProductService
	 */
	public String getCtProductService() {
		return ctProductService;
	}

	/**
	 * Sets the ct product service.
	 * 
	 * @param ctProductService
	 *            the new ct product service
	 */
	public void setCtProductService(String ctProductService) {
		this.ctProductService = ctProductService;
	}

	/**
	 * Gets the ct group one.
	 * 
	 * @return the ctGroupOne
	 */
	public String getCtGroupOne() {
		return ctGroupOne;
	}

	/**
	 * Sets the ct group one.
	 * 
	 * @param ctGroupOne
	 *            the new ct group one
	 */
	public void setCtGroupOne(String ctGroupOne) {
		this.ctGroupOne = ctGroupOne;
	}

	/**
	 * Gets the ct group two.
	 * 
	 * @return the ctGroupTwo
	 */
	public String getCtGroupTwo() {
		return ctGroupTwo;
	}

	/**
	 * Sets the ct group two.
	 * 
	 * @param ctGroupTwo
	 *            the new ct group two
	 */
	public void setCtGroupTwo(String ctGroupTwo) {
		this.ctGroupTwo = ctGroupTwo;
	}

	/**
	 * Gets the cm extention.
	 * 
	 * @return the cmExtention
	 */
	public String getCmExtention() {
		return cmExtention;
	}

	/**
	 * Sets the cm extention.
	 * 
	 * @param cmExtention
	 *            the new cm extention
	 */
	public void setCmExtention(String cmExtention) {
		this.cmExtention = cmExtention;
	}

	/**
	 * Gets the local to date var.
	 * 
	 * @return the localToDateVar
	 */
	public String getLocalToDateVar() {
		return localToDateVar;
	}

	/**
	 * Sets the local to date var.
	 * 
	 * @param localToDateVar
	 *            the new local to date var
	 */
	public void setLocalToDateVar(String localToDateVar) {
		this.localToDateVar = localToDateVar;
	}

	/**
	 * Gets the lng user id.
	 * 
	 * @return the lngUserId
	 */
	public Long getLngUserId() {
		return lngUserId;
	}

	/**
	 * Sets the lng user id.
	 * 
	 * @param lngUserId
	 *            the new lng user id
	 */
	public void setLngUserId(Long lngUserId) {
		this.lngUserId = lngUserId;
	}

	/**
	 * Gets the cm suppl or admin.
	 * 
	 * @return the cmSupplOrAdmin
	 */
	public String getCmSupplOrAdmin() {
		return cmSupplOrAdmin;
	}

	/**
	 * Sets the cm suppl or admin.
	 * 
	 * @param cmSupplOrAdmin
	 *            the new cm suppl or admin
	 */
	public void setCmSupplOrAdmin(String cmSupplOrAdmin) {
		this.cmSupplOrAdmin = cmSupplOrAdmin;
	}

	/**
	 * Gets the sm svo user.
	 * 
	 * @return the smSVOUser
	 */
	public String getSmSVOUser() {
		return smSVOUser;
	}

	/**
	 * Sets the sm svo user.
	 * 
	 * @param smSVOUser
	 *            the new sm svo user
	 */
	public void setSmSVOUser(String smSVOUser) {
		this.smSVOUser = smSVOUser;
	}

	/**
	 * Gets the sm monthly rpt by year.
	 * 
	 * @return the smMonthlyRptByYear
	 */
	public String getSmMonthlyRptByYear() {
		return smMonthlyRptByYear;
	}

	/**
	 * Sets the sm monthly rpt by year.
	 * 
	 * @param smMonthlyRptByYear
	 *            the new sm monthly rpt by year
	 */
	public void setSmMonthlyRptByYear(String smMonthlyRptByYear) {
		this.smMonthlyRptByYear = smMonthlyRptByYear;
	}

	/**
	 * Gets the sm created from year.
	 * 
	 * @return the smCreatedFromYear
	 */
	public String getSmCreatedFromYear() {
		return smCreatedFromYear;
	}

	/**
	 * Sets the sm created from year.
	 * 
	 * @param smCreatedFromYear
	 *            the new sm created from year
	 */
	public void setSmCreatedFromYear(String smCreatedFromYear) {
		this.smCreatedFromYear = smCreatedFromYear;
	}

	/**
	 * Gets the sm created to year.
	 * 
	 * @return the smCreatedToYear
	 */
	public String getSmCreatedToYear() {
		return smCreatedToYear;
	}

	/**
	 * Sets the sm created to year.
	 * 
	 * @param smCreatedToYear
	 *            the new sm created to year
	 */
	public void setSmCreatedToYear(String smCreatedToYear) {
		this.smCreatedToYear = smCreatedToYear;
	}

	/**
	 * Gets the sc request quote no.
	 * 
	 * @return the scRequestQuoteNo
	 */
	public String getScRequestQuoteNo() {
		return scRequestQuoteNo;
	}

	/**
	 * Sets the sc request quote no.
	 * 
	 * @param scRequestQuoteNo
	 *            the new sc request quote no
	 */
	public void setScRequestQuoteNo(String scRequestQuoteNo) {
		this.scRequestQuoteNo = scRequestQuoteNo;
	}

	/**
	 * Gets the sc pid no.
	 * 
	 * @return the scPIDNo
	 */
	public String getScPIDNo() {
		return scPIDNo;
	}

	/**
	 * Sets the sc pid no.
	 * 
	 * @param scPIDNo
	 *            the new sc pid no
	 */
	public void setScPIDNo(String scPIDNo) {
		this.scPIDNo = scPIDNo;
	}

	/**
	 * Gets the sc contract or pkp type.
	 * 
	 * @return the scContractOrPKPType
	 */
	public String getScContractOrPKPType() {
		return scContractOrPKPType;
	}

	/**
	 * Sets the sc contract or pkp type.
	 * 
	 * @param scContractOrPKPType
	 *            the new sc contract or pkp type
	 */
	public void setScContractOrPKPType(String scContractOrPKPType) {
		this.scContractOrPKPType = scContractOrPKPType;
	}

	/**
	 * Gets the sc contract or pkp no.
	 * 
	 * @return the scContractOrPKPNo
	 */
	public String getScContractOrPKPNo() {
		return scContractOrPKPNo;
	}

	/**
	 * Sets the sc contract or pkp no.
	 * 
	 * @param scContractOrPKPNo
	 *            the new sc contract or pkp no
	 */
	public void setScContractOrPKPNo(String scContractOrPKPNo) {
		this.scContractOrPKPNo = scContractOrPKPNo;
	}

	/**
	 * Gets the sc contract or pkp name.
	 * 
	 * @return the scContractOrPKPName
	 */
	public String getScContractOrPKPName() {
		return scContractOrPKPName;
	}

	/**
	 * Sets the sc contract or pkp name.
	 * 
	 * @param scContractOrPKPName
	 *            the new sc contract or pkp name
	 */
	public void setScContractOrPKPName(String scContractOrPKPName) {
		this.scContractOrPKPName = scContractOrPKPName;
	}

	/**
	 * Gets the sc laprnpurchdefault group.
	 * 
	 * @return the scLAPRNPURCHDEFAULTGroup
	 */
	public String getScLAPRNPURCHDEFAULTGroup() {
		return scLAPRNPURCHDEFAULTGroup;
	}

	/**
	 * Sets the sc laprnpurchdefault group.
	 * 
	 * @param scLAPRNPURCHDEFAULTGroup
	 *            the new sc laprnpurchdefault group
	 */
	public void setScLAPRNPURCHDEFAULTGroup(String scLAPRNPURCHDEFAULTGroup) {
		this.scLAPRNPURCHDEFAULTGroup = scLAPRNPURCHDEFAULTGroup;
	}

	/**
	 * Gets the sc requestnotesummary group.
	 * 
	 * @return the scREQUESTNOTESUMMARYGroup
	 */
	public String getScREQUESTNOTESUMMARYGroup() {
		return scREQUESTNOTESUMMARYGroup;
	}

	/**
	 * Sets the sc requestnotesummary group.
	 * 
	 * @param scREQUESTNOTESUMMARYGroup
	 *            the new sc requestnotesummary group
	 */
	public void setScREQUESTNOTESUMMARYGroup(String scREQUESTNOTESUMMARYGroup) {
		this.scREQUESTNOTESUMMARYGroup = scREQUESTNOTESUMMARYGroup;
	}

	/**
	 * Gets the sc common status.
	 * 
	 * @return the scCommonStatus
	 */
	public String getScCommonStatus() {
		return scCommonStatus;
	}

	/**
	 * Sets the sc common status.
	 * 
	 * @param scCommonStatus
	 *            the new sc common status
	 */
	public void setScCommonStatus(String scCommonStatus) {
		this.scCommonStatus = scCommonStatus;
	}

	/**
	 * Gets the fl group one.
	 * 
	 * @return the flGroupOne
	 */
	public String getFlGroupOne() {
		return flGroupOne;
	}

	/**
	 * Sets the fl group one.
	 * 
	 * @param flGroupOne
	 *            the new fl group one
	 */
	public void setFlGroupOne(String flGroupOne) {
		this.flGroupOne = flGroupOne;
	}

	/**
	 * Gets the fl group two.
	 * 
	 * @return the flGroupTwo
	 */
	public String getFlGroupTwo() {
		return flGroupTwo;
	}

	/**
	 * Sets the fl group two.
	 * 
	 * @param flGroupTwo
	 *            the new fl group two
	 */
	public void setFlGroupTwo(String flGroupTwo) {
		this.flGroupTwo = flGroupTwo;
	}

	/**
	 * Gets the fl group three.
	 * 
	 * @return the flGroupThree
	 */
	public String getFlGroupThree() {
		return flGroupThree;
	}

	/**
	 * Sets the fl group three.
	 * 
	 * @param flGroupThree
	 *            the new fl group three
	 */
	public void setFlGroupThree(String flGroupThree) {
		this.flGroupThree = flGroupThree;
	}

	/**
	 * Gets the fl group four.
	 * 
	 * @return the flGroupFour
	 */
	public String getFlGroupFour() {
		return flGroupFour;
	}

	/**
	 * Sets the fl group four.
	 * 
	 * @param flGroupFour
	 *            the new fl group four
	 */
	public void setFlGroupFour(String flGroupFour) {
		this.flGroupFour = flGroupFour;
	}

	/**
	 * Gets the fl group five.
	 * 
	 * @return the flGroupFive
	 */
	public String getFlGroupFive() {
		return flGroupFive;
	}

	/**
	 * Sets the fl group five.
	 * 
	 * @param flGroupFive
	 *            the new fl group five
	 */
	public void setFlGroupFive(String flGroupFive) {
		this.flGroupFive = flGroupFive;
	}

	/**
	 * Gets the fl group six.
	 * 
	 * @return the flGroupSix
	 */
	public String getFlGroupSix() {
		return flGroupSix;
	}

	/**
	 * Sets the fl group six.
	 * 
	 * @param flGroupSix
	 *            the new fl group six
	 */
	public void setFlGroupSix(String flGroupSix) {
		this.flGroupSix = flGroupSix;
	}

	/**
	 * Gets the fl group seven.
	 * 
	 * @return the flGroupSeven
	 */
	public String getFlGroupSeven() {
		return flGroupSeven;
	}

	/**
	 * Sets the fl group seven.
	 * 
	 * @param flGroupSeven
	 *            the new fl group seven
	 */
	public void setFlGroupSeven(String flGroupSeven) {
		this.flGroupSeven = flGroupSeven;
	}

	/**
	 * Gets the fl group eight.
	 * 
	 * @return the flGroupEight
	 */
	public String getFlGroupEight() {
		return flGroupEight;
	}

	/**
	 * Sets the fl group eight.
	 * 
	 * @param flGroupEight
	 *            the new fl group eight
	 */
	public void setFlGroupEight(String flGroupEight) {
		this.flGroupEight = flGroupEight;
	}

	/**
	 * Gets the fl group nine.
	 * 
	 * @return the flGroupNine
	 */
	public String getFlGroupNine() {
		return flGroupNine;
	}

	/**
	 * Sets the fl group nine.
	 * 
	 * @param flGroupNine
	 *            the new fl group nine
	 */
	public void setFlGroupNine(String flGroupNine) {
		this.flGroupNine = flGroupNine;
	}

	/**
	 * Gets the fl group ten.
	 * 
	 * @return the flGroupTen
	 */
	public String getFlGroupTen() {
		return flGroupTen;
	}

	/**
	 * Sets the fl group ten.
	 * 
	 * @param flGroupTen
	 *            the new fl group ten
	 */
	public void setFlGroupTen(String flGroupTen) {
		this.flGroupTen = flGroupTen;
	}

	/**
	 * Gets the fl group eleven.
	 * 
	 * @return the flGroupEleven
	 */
	public String getFlGroupEleven() {
		return flGroupEleven;
	}

	/**
	 * Sets the fl group eleven.
	 * 
	 * @param flGroupEleven
	 *            the new fl group eleven
	 */
	public void setFlGroupEleven(String flGroupEleven) {
		this.flGroupEleven = flGroupEleven;
	}

	/**
	 * Gets the fl group twelve.
	 * 
	 * @return the flGroupTwelve
	 */
	public String getFlGroupTwelve() {
		return flGroupTwelve;
	}

	/**
	 * Sets the fl group twelve.
	 * 
	 * @param flGroupTwelve
	 *            the new fl group twelve
	 */
	public void setFlGroupTwelve(String flGroupTwelve) {
		this.flGroupTwelve = flGroupTwelve;
	}

	/**
	 * Gets the fl group thirteen.
	 * 
	 * @return the flGroupThirteen
	 */
	public String getFlGroupThirteen() {
		return flGroupThirteen;
	}

	/**
	 * Sets the fl group thirteen.
	 * 
	 * @param flGroupThirteen
	 *            the new fl group thirteen
	 */
	public void setFlGroupThirteen(String flGroupThirteen) {
		this.flGroupThirteen = flGroupThirteen;
	}

	/**
	 * Gets the fl group fourteen.
	 * 
	 * @return the flGroupFourteen
	 */
	public String getFlGroupFourteen() {
		return flGroupFourteen;
	}

	/**
	 * Sets the fl group fourteen.
	 * 
	 * @param flGroupFourteen
	 *            the new fl group fourteen
	 */
	public void setFlGroupFourteen(String flGroupFourteen) {
		this.flGroupFourteen = flGroupFourteen;
	}

	/**
	 * Gets the fl group fifteen.
	 * 
	 * @return the flGroupFifteen
	 */
	public String getFlGroupFifteen() {
		return flGroupFifteen;
	}

	/**
	 * Sets the fl group fifteen.
	 * 
	 * @param flGroupFifteen
	 *            the new fl group fifteen
	 */
	public void setFlGroupFifteen(String flGroupFifteen) {
		this.flGroupFifteen = flGroupFifteen;
	}

	/**
	 * Gets the fl group sixteen.
	 * 
	 * @return the flGroupSixteen
	 */
	public String getFlGroupSixteen() {
		return flGroupSixteen;
	}

	/**
	 * Sets the fl group sixteen.
	 * 
	 * @param flGroupSixteen
	 *            the new fl group sixteen
	 */
	public void setFlGroupSixteen(String flGroupSixteen) {
		this.flGroupSixteen = flGroupSixteen;
	}

	/**
	 * Gets the ct complete.
	 * 
	 * @return the ctComplete
	 */
	public String getCtComplete() {
		return ctComplete;
	}

	/**
	 * Sets the ct complete.
	 * 
	 * @param ctComplete
	 *            the new ct complete
	 */
	public void setCtComplete(String ctComplete) {
		this.ctComplete = ctComplete;
	}

	/**
	 * Gets the ct contr montr status.
	 * 
	 * @return the ctContrMontrStatus
	 */
	public String getCtContrMontrStatus() {
		return ctContrMontrStatus;
	}

	/**
	 * Sets the ct contr montr status.
	 * 
	 * @param ctContrMontrStatus
	 *            the new ct contr montr status
	 */
	public void setCtContrMontrStatus(String ctContrMontrStatus) {
		this.ctContrMontrStatus = ctContrMontrStatus;
	}

	/** The generated url. */
	private String generatedURL;

	/** The report url. */
	private String reportURL;

	/** The pdf file. */
	private StreamedContent pdfFile;

	/** The constructed param string. */
	private String constructedParamString;

	/** The report file name jsp. */
	private String reportFileNameJSP;

	/**
	 * Gets the ct contract no.
	 * 
	 * @return the ctContractNo
	 */
	public String getCtContractNo() {
		return ctContractNo;
	}

	/**
	 * Sets the ct contract no.
	 * 
	 * @param ctContractNo
	 *            the new ct contract no
	 */
	public void setCtContractNo(String ctContractNo) {
		this.ctContractNo = ctContractNo;
	}

	/**
	 * Gets the ct agreement.
	 * 
	 * @return the ctAgreement
	 */
	public String getCtAgreement() {
		return ctAgreement;
	}

	/**
	 * Sets the ct agreement.
	 * 
	 * @param ctAgreement
	 *            the new ct agreement
	 */
	public void setCtAgreement(String ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	/**
	 * Gets the ct status.
	 * 
	 * @return the ctStatus
	 */
	public String getCtStatus() {
		return ctStatus;
	}

	/**
	 * Sets the ct status.
	 * 
	 * @param ctStatus
	 *            the new ct status
	 */
	public void setCtStatus(String ctStatus) {
		this.ctStatus = ctStatus;
	}

	/**
	 * Gets the sub module.
	 * 
	 * @return the subModule
	 */
	public String getSubModule() {
		return subModule;
	}

	/**
	 * Sets the sub module.
	 * 
	 * @param subModule
	 *            the new sub module
	 */
	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	/**
	 * Gets the sc prc rpt group.
	 * 
	 * @return the scPrcRptGroup
	 */
	public String getScPrcRptGroup() {
		return scPrcRptGroup;
	}

	/**
	 * Sets the sc prc rpt group.
	 * 
	 * @param scPrcRptGroup
	 *            the new sc prc rpt group
	 */
	public void setScPrcRptGroup(String scPrcRptGroup) {
		this.scPrcRptGroup = scPrcRptGroup;
	}

	/**
	 * Gets the sc pi dtls prc rpt group.
	 * 
	 * @return the scPIDtlsPrcRptGroup
	 */
	public String getScPIDtlsPrcRptGroup() {
		return scPIDtlsPrcRptGroup;
	}

	/**
	 * Sets the sc pi dtls prc rpt group.
	 * 
	 * @param scPIDtlsPrcRptGroup
	 *            the new sc pi dtls prc rpt group
	 */
	public void setScPIDtlsPrcRptGroup(String scPIDtlsPrcRptGroup) {
		this.scPIDtlsPrcRptGroup = scPIDtlsPrcRptGroup;
	}

	/**
	 * Gets the sc procurement type.
	 * 
	 * @return the scProcurementType
	 */
	public String getScProcurementType() {
		return scProcurementType;
	}

	/**
	 * Sets the sc procurement type.
	 * 
	 * @param scProcurementType
	 *            the new sc procurement type
	 */
	public void setScProcurementType(String scProcurementType) {
		this.scProcurementType = scProcurementType;
	}

	/**
	 * Gets the sc process rpt quotation no.
	 * 
	 * @return the scProcessRptQuotationNo
	 */
	public String getScProcessRptQuotationNo() {
		return scProcessRptQuotationNo;
	}

	/**
	 * Sets the sc process rpt quotation no.
	 * 
	 * @param scProcessRptQuotationNo
	 *            the new sc process rpt quotation no
	 */
	public void setScProcessRptQuotationNo(String scProcessRptQuotationNo) {
		this.scProcessRptQuotationNo = scProcessRptQuotationNo;
	}

	/**
	 * Gets the sc sone status.
	 * 
	 * @return the scSoneStatus
	 */
	public String getScSoneStatus() {
		return scSoneStatus;
	}

	/**
	 * Sets the sc sone status.
	 * 
	 * @param scSoneStatus
	 *            the new sc sone status
	 */
	public void setScSoneStatus(String scSoneStatus) {
		this.scSoneStatus = scSoneStatus;
	}

	/**
	 * Gets the sc sone crtd from date.
	 * 
	 * @return the scSoneCrtdFromDate
	 */
	public Date getScSoneCrtdFromDate() {
		return scSoneCrtdFromDate;
	}

	/**
	 * Sets the sc sone crtd from date.
	 * 
	 * @param scSoneCrtdFromDate
	 *            the new sc sone crtd from date
	 */
	public void setScSoneCrtdFromDate(Date scSoneCrtdFromDate) {
		this.scSoneCrtdFromDate = scSoneCrtdFromDate;
	}

	/**
	 * Gets the sc sone crtd to date.
	 * 
	 * @return the scSoneCrtdToDate
	 */
	public Date getScSoneCrtdToDate() {
		return scSoneCrtdToDate;
	}

	/**
	 * Sets the sc sone crtd to date.
	 * 
	 * @param scSoneCrtdToDate
	 *            the new sc sone crtd to date
	 */
	public void setScSoneCrtdToDate(Date scSoneCrtdToDate) {
		this.scSoneCrtdToDate = scSoneCrtdToDate;
	}

	/**
	 * Gets the sc requisener id.
	 * 
	 * @return the scRequisenerId
	 */
	public String getScRequisenerId() {
		return scRequisenerId;
	}

	/**
	 * Sets the sc requisener id.
	 * 
	 * @param scRequisenerId
	 *            the new sc requisener id
	 */
	public void setScRequisenerId(String scRequisenerId) {
		this.scRequisenerId = scRequisenerId;
	}

	/**
	 * Gets the sc supplier.
	 * 
	 * @return the scSupplier
	 */
	public String getScSupplier() {
		return scSupplier;
	}

	/**
	 * Sets the sc supplier.
	 * 
	 * @param scSupplier
	 *            the new sc supplier
	 */
	public void setScSupplier(String scSupplier) {
		this.scSupplier = scSupplier;
	}

	/**
	 * Gets the sc login.
	 * 
	 * @return the scLogin
	 */
	public String getScLogin() {
		return scLogin;
	}

	/**
	 * Sets the sc login.
	 * 
	 * @param scLogin
	 *            the new sc login
	 */
	public void setScLogin(String scLogin) {
		this.scLogin = scLogin;
	}

	/**
	 * Gets the sc p ino.
	 * 
	 * @return the scPIno
	 */
	public String getScPIno() {
		return scPIno;
	}

	/**
	 * Sets the sc p ino.
	 * 
	 * @param scPIno
	 *            the new sc p ino
	 */
	public void setScPIno(String scPIno) {
		this.scPIno = scPIno;
	}

	/**
	 * Gets the sc publish from date.
	 * 
	 * @return the scPublishFromDate
	 */
	public Date getScPublishFromDate() {
		return scPublishFromDate;
	}

	/**
	 * Sets the sc publish from date.
	 * 
	 * @param scPublishFromDate
	 *            the new sc publish from date
	 */
	public void setScPublishFromDate(Date scPublishFromDate) {
		this.scPublishFromDate = scPublishFromDate;
	}

	/**
	 * Gets the sc publish to date.
	 * 
	 * @return the scPublishToDate
	 */
	public Date getScPublishToDate() {
		return scPublishToDate;
	}

	/**
	 * Sets the sc publish to date.
	 * 
	 * @param scPublishToDate
	 *            the new sc publish to date
	 */
	public void setScPublishToDate(Date scPublishToDate) {
		this.scPublishToDate = scPublishToDate;
	}

	/**
	 * Gets the sc procure all list.
	 * 
	 * @return the scProcureAllList
	 */
	public List<SelectItem> getScProcureAllList() {
		return scProcureAllList;
	}

	/**
	 * Sets the sc procure all list.
	 * 
	 * @param scProcureAllList
	 *            the new sc procure all list
	 */
	public void setScProcureAllList(List<SelectItem> scProcureAllList) {
		this.scProcureAllList = scProcureAllList;
	}

	/**
	 * Gets the sc quotation list.
	 * 
	 * @return the scQuotationList
	 */
	public List<SelectItem> getScQuotationList() {
		return scQuotationList;
	}

	/**
	 * Sets the sc quotation list.
	 * 
	 * @param scQuotationList
	 *            the new sc quotation list
	 */
	public void setScQuotationList(List<SelectItem> scQuotationList) {
		this.scQuotationList = scQuotationList;
	}

	/**
	 * Gets the sc tender list.
	 * 
	 * @return the scTenderList
	 */
	public List<SelectItem> getScTenderList() {
		return scTenderList;
	}

	/**
	 * Sets the sc tender list.
	 * 
	 * @param scTenderList
	 *            the new sc tender list
	 */
	public void setScTenderList(List<SelectItem> scTenderList) {
		this.scTenderList = scTenderList;
	}

	/**
	 * Gets the sc rstrctd tndr list.
	 * 
	 * @return the scRstrctdTndrList
	 */
	public List<SelectItem> getScRstrctdTndrList() {
		return scRstrctdTndrList;
	}

	/**
	 * Sets the sc rstrctd tndr list.
	 * 
	 * @param scRstrctdTndrList
	 *            the new sc rstrctd tndr list
	 */
	public void setScRstrctdTndrList(List<SelectItem> scRstrctdTndrList) {
		this.scRstrctdTndrList = scRstrctdTndrList;
	}

	/**
	 * Gets the sc created to year.
	 * 
	 * @return the scCreatedToYear
	 */
	public String getScCreatedToYear() {
		return scCreatedToYear;
	}

	/**
	 * Sets the sc created to year.
	 * 
	 * @param scCreatedToYear
	 *            the new sc created to year
	 */
	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}

	/**
	 * Gets the sc created to month.
	 * 
	 * @return the scCreatedToMonth
	 */
	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}

	/**
	 * Sets the sc created to month.
	 * 
	 * @param scCreatedToMonth
	 *            the new sc created to month
	 */
	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}

	/**
	 * Gets the sc created from year.
	 * 
	 * @return the scCreatedFromYear
	 */
	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}

	/**
	 * Sets the sc created from year.
	 * 
	 * @param scCreatedFromYear
	 *            the new sc created from year
	 */
	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}

	/**
	 * Gets the sc created from month.
	 * 
	 * @return the scCreatedFromMonth
	 */
	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}

	/**
	 * Sets the sc created from month.
	 * 
	 * @param scCreatedFromMonth
	 *            the new sc created from month
	 */
	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}

	/**
	 * Gets the sc desk officer.
	 * 
	 * @return the scDeskOfficer
	 */
	public String getScDeskOfficer() {
		return scDeskOfficer;
	}

	/**
	 * Sets the sc desk officer.
	 * 
	 * @param scDeskOfficer
	 *            the new sc desk officer
	 */
	public void setScDeskOfficer(String scDeskOfficer) {
		this.scDeskOfficer = scDeskOfficer;
	}

	/**
	 * Gets the sc ptj.
	 * 
	 * @return the scPtj
	 */
	public String getScPtj() {
		return scPtj;
	}

	/**
	 * Sets the sc ptj.
	 * 
	 * @param scPtj
	 *            the new sc ptj
	 */
	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}

	/**
	 * Gets the sc status.
	 * 
	 * @return the scStatus
	 */
	public String getScStatus() {
		return scStatus;
	}

	/**
	 * Sets the sc status.
	 * 
	 * @param scStatus
	 *            the new sc status
	 */
	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}

	/**
	 * Gets the procurement list.
	 * 
	 * @return the procurementList
	 */
	public List<SelectItem> getProcurementList() {
		return procurementList;
	}

	/**
	 * Sets the procurement list.
	 * 
	 * @param procurementList
	 *            the new procurement list
	 */
	public void setProcurementList(List<SelectItem> procurementList) {
		this.procurementList = procurementList;
	}

	/**
	 * Gets the fl contract order.
	 * 
	 * @return the flContractOrder
	 */
	public String getFlContractOrder() {
		return flContractOrder;
	}

	/**
	 * Sets the fl contract order.
	 * 
	 * @param flContractOrder
	 *            the new fl contract order
	 */
	public void setFlContractOrder(String flContractOrder) {
		this.flContractOrder = flContractOrder;
	}

	/**
	 * Gets the fl contract request.
	 * 
	 * @return the flContractRequest
	 */
	public String getFlContractRequest() {
		return flContractRequest;
	}

	/**
	 * Sets the fl contract request.
	 * 
	 * @param flContractRequest
	 *            the new fl contract request
	 */
	public void setFlContractRequest(String flContractRequest) {
		this.flContractRequest = flContractRequest;
	}

	/**
	 * Gets the fl purchase order no.
	 * 
	 * @return the flPurchaseOrderNo
	 */
	public String getFlPurchaseOrderNo() {
		return flPurchaseOrderNo;
	}

	/**
	 * Sets the fl purchase order no.
	 * 
	 * @param flPurchaseOrderNo
	 *            the new fl purchase order no
	 */
	public void setFlPurchaseOrderNo(String flPurchaseOrderNo) {
		this.flPurchaseOrderNo = flPurchaseOrderNo;
	}

	/**
	 * Gets the report file name jsp.
	 * 
	 * @return the reportFileNameJSP
	 */
	public String getReportFileNameJSP() {
		return reportFileNameJSP;
	}

	/**
	 * Sets the report file name jsp.
	 * 
	 * @param reportFileNameJSP
	 *            the new report file name jsp
	 */
	public void setReportFileNameJSP(String reportFileNameJSP) {
		this.reportFileNameJSP = reportFileNameJSP;
	}

	/**
	 * Gets the pdf file.
	 * 
	 * @return the pdfFile
	 */
	public StreamedContent getPdfFile() {
		return pdfFile;
	}

	/**
	 * Gets the fl itm type.
	 * 
	 * @return the flItmType
	 */
	public String getFlItmType() {
		return flItmType;
	}

	/**
	 * Sets the fl itm type.
	 * 
	 * @param flItmType
	 *            the new fl itm type
	 */
	public void setFlItmType(String flItmType) {
		this.flItmType = flItmType;
	}

	/**
	 * Gets the fl contract no.
	 * 
	 * @return the flContractNo
	 */
	public String getFlContractNo() {
		return flContractNo;
	}

	/**
	 * Sets the fl contract no.
	 * 
	 * @param flContractNo
	 *            the new fl contract no
	 */
	public void setFlContractNo(String flContractNo) {
		this.flContractNo = flContractNo;
	}

	/**
	 * Gets the fl sst.
	 * 
	 * @return the flSst
	 */
	public String getFlSst() {
		return flSst;
	}

	/**
	 * Sets the fl sst.
	 * 
	 * @param flSst
	 *            the new fl sst
	 */
	public void setFlSst(String flSst) {
		this.flSst = flSst;
	}

	/**
	 * Gets the fl jabatan.
	 * 
	 * @return the flJabatan
	 */
	public String getFlJabatan() {
		return flJabatan;
	}

	/**
	 * Sets the fl jabatan.
	 * 
	 * @param flJabatan
	 *            the new fl jabatan
	 */
	public void setFlJabatan(String flJabatan) {
		this.flJabatan = flJabatan;
	}

	/**
	 * Gets the common ptj.
	 * 
	 * @return the commonPtj
	 */
	public String getCommonPtj() {
		return commonPtj;
	}

	/**
	 * Sets the common ptj.
	 * 
	 * @param commonPtj
	 *            the new common ptj
	 */
	public void setCommonPtj(String commonPtj) {
		this.commonPtj = commonPtj;
	}

	/**
	 * Gets the common ministry.
	 * 
	 * @return the commonMinistry
	 */
	public String getCommonMinistry() {
		return commonMinistry;
	}

	/**
	 * Sets the common ministry.
	 * 
	 * @param commonMinistry
	 *            the new common ministry
	 */
	public void setCommonMinistry(String commonMinistry) {
		this.commonMinistry = commonMinistry;
	}

	/**
	 * Gets the fl supplier.
	 * 
	 * @return the flSupplier
	 */
	public String getFlSupplier() {
		return flSupplier;
	}

	/**
	 * Sets the fl supplier.
	 * 
	 * @param flSupplier
	 *            the new fl supplier
	 */
	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}

	/**
	 * Gets the fl status.
	 * 
	 * @return the flStatus
	 */
	public String getFlStatus() {
		return flStatus;
	}

	/**
	 * Sets the fl status.
	 * 
	 * @param flStatus
	 *            the new fl status
	 */
	public void setFlStatus(String flStatus) {
		this.flStatus = flStatus;
	}

	/**
	 * Gets the fl duration.
	 * 
	 * @return the flDuration
	 */
	public String getFlDuration() {
		return flDuration;
	}

	/**
	 * Sets the fl duration.
	 * 
	 * @param flDuration
	 *            the new fl duration
	 */
	public void setFlDuration(String flDuration) {
		this.flDuration = flDuration;
	}

	/**
	 * Gets the fl recieving officer.
	 * 
	 * @return the flRecievingOfficer
	 */
	public String getFlRecievingOfficer() {
		return flRecievingOfficer;
	}

	/**
	 * Sets the fl recieving officer.
	 * 
	 * @param flRecievingOfficer
	 *            the new fl recieving officer
	 */
	public void setFlRecievingOfficer(String flRecievingOfficer) {
		this.flRecievingOfficer = flRecievingOfficer;
	}

	/**
	 * Gets the fl delivery address.
	 * 
	 * @return the flDeliveryAddress
	 */
	public String getFlDeliveryAddress() {
		return flDeliveryAddress;
	}

	/**
	 * Sets the fl delivery address.
	 * 
	 * @param flDeliveryAddress
	 *            the new fl delivery address
	 */
	public void setFlDeliveryAddress(String flDeliveryAddress) {
		this.flDeliveryAddress = flDeliveryAddress;
	}

	/**
	 * Gets the fl contract type.
	 * 
	 * @return the flContractType
	 */
	public String getFlContractType() {
		return flContractType;
	}

	/**
	 * Sets the fl contract type.
	 * 
	 * @param flContractType
	 *            the new fl contract type
	 */
	public void setFlContractType(String flContractType) {
		this.flContractType = flContractType;
	}

	/**
	 * Gets the fl requisitioner.
	 * 
	 * @return the flRequisitioner
	 */
	public String getFlRequisitioner() {
		return flRequisitioner;
	}

	/**
	 * Sets the fl requisitioner.
	 * 
	 * @param flRequisitioner
	 *            the new fl requisitioner
	 */
	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}

	/**
	 * Gets the fl mto contract.
	 * 
	 * @return the flMtoContract
	 */
	public String getFlMtoContract() {
		return flMtoContract;
	}

	/**
	 * Sets the fl mto contract.
	 * 
	 * @param flMtoContract
	 *            the new fl mto contract
	 */
	public void setFlMtoContract(String flMtoContract) {
		this.flMtoContract = flMtoContract;
	}

	/**
	 * Gets the fl invoice no.
	 * 
	 * @return the flInvoiceNo
	 */
	public String getFlInvoiceNo() {
		return flInvoiceNo;
	}

	/**
	 * Sets the fl invoice no.
	 * 
	 * @param flInvoiceNo
	 *            the new fl invoice no
	 */
	public void setFlInvoiceNo(String flInvoiceNo) {
		this.flInvoiceNo = flInvoiceNo;
	}

	/**
	 * Gets the fl deduction type.
	 * 
	 * @return the flDeductionType
	 */
	public String getFlDeductionType() {
		return flDeductionType;
	}

	/**
	 * Sets the fl deduction type.
	 * 
	 * @param flDeductionType
	 *            the new fl deduction type
	 */
	public void setFlDeductionType(String flDeductionType) {
		this.flDeductionType = flDeductionType;
	}

	/**
	 * Gets the fl cr no.
	 * 
	 * @return the flCrNo
	 */
	public String getFlCrNo() {
		return flCrNo;
	}

	/**
	 * Sets the fl cr no.
	 * 
	 * @param flCrNo
	 *            the new fl cr no
	 */
	public void setFlCrNo(String flCrNo) {
		this.flCrNo = flCrNo;
	}

	/**
	 * Gets the fl approver name.
	 * 
	 * @return the flApproverName
	 */
	public String getFlApproverName() {
		return flApproverName;
	}

	/**
	 * Sets the fl approver name.
	 * 
	 * @param flApproverName
	 *            the new fl approver name
	 */
	public void setFlApproverName(String flApproverName) {
		this.flApproverName = flApproverName;
	}

	/**
	 * Gets the fl document type.
	 * 
	 * @return the flDocumentType
	 */
	public String getFlDocumentType() {
		return flDocumentType;
	}

	/**
	 * Sets the fl document type.
	 * 
	 * @param flDocumentType
	 *            the new fl document type
	 */
	public void setFlDocumentType(String flDocumentType) {
		this.flDocumentType = flDocumentType;
	}

	/**
	 * Gets the fl criteria.
	 * 
	 * @return the flCriteria
	 */
	public String getFlCriteria() {
		return flCriteria;
	}

	/**
	 * Sets the fl criteria.
	 * 
	 * @param flCriteria
	 *            the new fl criteria
	 */
	public void setFlCriteria(String flCriteria) {
		this.flCriteria = flCriteria;
	}

	/**
	 * Gets the fl scoring.
	 * 
	 * @return the flScoring
	 */
	public String getFlScoring() {
		return flScoring;
	}

	/**
	 * Sets the fl scoring.
	 * 
	 * @param flScoring
	 *            the new fl scoring
	 */
	public void setFlScoring(String flScoring) {
		this.flScoring = flScoring;
	}

	/**
	 * Gets the fl pkp no.
	 * 
	 * @return the flPkpNo
	 */
	public String getFlPkpNo() {
		return flPkpNo;
	}

	/**
	 * Sets the fl pkp no.
	 * 
	 * @param flPkpNo
	 *            the new fl pkp no
	 */
	public void setFlPkpNo(String flPkpNo) {
		this.flPkpNo = flPkpNo;
	}

	/**
	 * Gets the fl delivery order no.
	 * 
	 * @return the flDeliveryOrderNo
	 */
	public String getFlDeliveryOrderNo() {
		return flDeliveryOrderNo;
	}

	/**
	 * Sets the fl delivery order no.
	 * 
	 * @param flDeliveryOrderNo
	 *            the new fl delivery order no
	 */
	public void setFlDeliveryOrderNo(String flDeliveryOrderNo) {
		this.flDeliveryOrderNo = flDeliveryOrderNo;
	}

	/**
	 * Gets the fl item code.
	 * 
	 * @return the flItemCode
	 */
	public String getFlItemCode() {
		return flItemCode;
	}

	/**
	 * Sets the fl item code.
	 * 
	 * @param flItemCode
	 *            the new fl item code
	 */
	public void setFlItemCode(String flItemCode) {
		this.flItemCode = flItemCode;
	}

	/**
	 * Gets the fl po amount.
	 * 
	 * @return the flPoAmount
	 */
	public String getFlPoAmount() {
		return flPoAmount;
	}

	/**
	 * Sets the fl po amount.
	 * 
	 * @param flPoAmount
	 *            the new fl po amount
	 */
	public void setFlPoAmount(String flPoAmount) {
		this.flPoAmount = flPoAmount;
	}

	/**
	 * Gets the fl pr no.
	 * 
	 * @return the flPrNo
	 */
	public String getFlPrNo() {
		return flPrNo;
	}

	/**
	 * Sets the fl pr no.
	 * 
	 * @param flPrNo
	 *            the new fl pr no
	 */
	public void setFlPrNo(String flPrNo) {
		this.flPrNo = flPrNo;
	}

	/**
	 * Gets the fl pmo.
	 * 
	 * @return the flPmo
	 */
	public String getFlPmo() {
		return flPmo;
	}

	/**
	 * Sets the fl pmo.
	 * 
	 * @param flPmo
	 *            the new fl pmo
	 */
	public void setFlPmo(String flPmo) {
		this.flPmo = flPmo;
	}

	/**
	 * Gets the fl item name.
	 * 
	 * @return the flItemName
	 */
	public String getFlItemName() {
		return flItemName;
	}

	/**
	 * Sets the fl item name.
	 * 
	 * @param flItemName
	 *            the new fl item name
	 */
	public void setFlItemName(String flItemName) {
		this.flItemName = flItemName;
	}

	/**
	 * Gets the sm application no.
	 * 
	 * @return the smApplicationNo
	 */
	public String getSmApplicationNo() {
		return smApplicationNo;
	}

	/**
	 * Sets the sm application no.
	 * 
	 * @param smApplicationNo
	 *            the new sm application no
	 */
	public void setSmApplicationNo(String smApplicationNo) {
		this.smApplicationNo = smApplicationNo;
	}

	/**
	 * Gets the sm zone.
	 * 
	 * @return the smZone
	 */
	public String getSmZone() {
		return smZone;
	}

	/**
	 * Sets the sm zone.
	 * 
	 * @param smZone
	 *            the new sm zone
	 */
	public void setSmZone(String smZone) {
		this.smZone = smZone;
	}

	/**
	 * Gets the sm supplier type.
	 * 
	 * @return the smSupplierType
	 */
	public String getSmSupplierType() {
		return smSupplierType;
	}

	/**
	 * Sets the sm supplier type.
	 * 
	 * @param smSupplierType
	 *            the new sm supplier type
	 */
	public void setSmSupplierType(String smSupplierType) {
		this.smSupplierType = smSupplierType;
	}

	/**
	 * Gets the sm mof reg no.
	 * 
	 * @return the smMofRegNo
	 */
	public String getSmMofRegNo() {
		return smMofRegNo;
	}

	/**
	 * Sets the sm mof reg no.
	 * 
	 * @param smMofRegNo
	 *            the new sm mof reg no
	 */
	public void setSmMofRegNo(String smMofRegNo) {
		this.smMofRegNo = smMofRegNo;
	}

	/**
	 * Gets the sm registration status.
	 * 
	 * @return the smRegistrationStatus
	 */
	public String getSmRegistrationStatus() {
		return smRegistrationStatus;
	}

	/**
	 * Sets the sm registration status.
	 * 
	 * @param smRegistrationStatus
	 *            the new sm registration status
	 */
	public void setSmRegistrationStatus(String smRegistrationStatus) {
		this.smRegistrationStatus = smRegistrationStatus;
	}

	/**
	 * Gets the smcategory.
	 * 
	 * @return the smcategory
	 */
	public String getSmcategory() {
		return smcategory;
	}

	/**
	 * Sets the smcategory.
	 * 
	 * @param smcategory
	 *            the new smcategory
	 */
	public void setSmcategory(String smcategory) {
		this.smcategory = smcategory;
	}

	/**
	 * Gets the sm main category.
	 * 
	 * @return the smMainCategory
	 */
	public String getSmMainCategory() {
		return smMainCategory;
	}

	/**
	 * Sets the sm main category.
	 * 
	 * @param smMainCategory
	 *            the new sm main category
	 */
	public void setSmMainCategory(String smMainCategory) {
		this.smMainCategory = smMainCategory;
	}

	/**
	 * Gets the sm sub category.
	 * 
	 * @return the smSubCategory
	 */
	public String getSmSubCategory() {
		return smSubCategory;
	}

	/**
	 * Sets the sm sub category.
	 * 
	 * @param smSubCategory
	 *            the new sm sub category
	 */
	public void setSmSubCategory(String smSubCategory) {
		this.smSubCategory = smSubCategory;
	}

	/**
	 * Gets the sm sub category division.
	 * 
	 * @return the smSubCategoryDivision
	 */
	public String getSmSubCategoryDivision() {
		return smSubCategoryDivision;
	}

	/**
	 * Sets the sm sub category division.
	 * 
	 * @param smSubCategoryDivision
	 *            the new sm sub category division
	 */
	public void setSmSubCategoryDivision(String smSubCategoryDivision) {
		this.smSubCategoryDivision = smSubCategoryDivision;
	}

	/**
	 * Gets the sm application status.
	 * 
	 * @return the smApplicationStatus
	 */
	public String getSmApplicationStatus() {
		return smApplicationStatus;
	}

	/**
	 * Sets the sm application status.
	 * 
	 * @param smApplicationStatus
	 *            the new sm application status
	 */
	public void setSmApplicationStatus(String smApplicationStatus) {
		this.smApplicationStatus = smApplicationStatus;
	}

	/**
	 * Gets the sm payment type.
	 * 
	 * @return the smPaymentType
	 */
	public String getSmPaymentType() {
		return smPaymentType;
	}

	/**
	 * Sets the sm payment type.
	 * 
	 * @param smPaymentType
	 *            the new sm payment type
	 */
	public void setSmPaymentType(String smPaymentType) {
		this.smPaymentType = smPaymentType;
	}

	/**
	 * Gets the sm application type.
	 * 
	 * @return the smApplicationType
	 */
	public String getSmApplicationType() {
		return smApplicationType;
	}

	/**
	 * Sets the sm application type.
	 * 
	 * @param smApplicationType
	 *            the new sm application type
	 */
	public void setSmApplicationType(String smApplicationType) {
		this.smApplicationType = smApplicationType;
	}

	/**
	 * Gets the sm state.
	 * 
	 * @return the smState
	 */
	public String getSmState() {
		return smState;
	}

	/**
	 * Sets the sm state.
	 * 
	 * @param smState
	 *            the new sm state
	 */
	public void setSmState(String smState) {
		this.smState = smState;
	}

	/**
	 * Gets the sm district.
	 * 
	 * @return the smDistrict
	 */
	public String getSmDistrict() {
		return smDistrict;
	}

	/**
	 * Sets the sm district.
	 * 
	 * @param smDistrict
	 *            the new sm district
	 */
	public void setSmDistrict(String smDistrict) {
		this.smDistrict = smDistrict;
	}

	/**
	 * Gets the sm report type.
	 * 
	 * @return the smReportType
	 */
	public String getSmReportType() {
		return smReportType;
	}

	/**
	 * Sets the sm report type.
	 * 
	 * @param smReportType
	 *            the new sm report type
	 */
	public void setSmReportType(String smReportType) {
		this.smReportType = smReportType;
	}

	/**
	 * Gets the sm company name.
	 * 
	 * @return the smCompanyName
	 */
	public String getSmCompanyName() {
		return smCompanyName;
	}

	/**
	 * Sets the sm company name.
	 * 
	 * @param smCompanyName
	 *            the new sm company name
	 */
	public void setSmCompanyName(String smCompanyName) {
		this.smCompanyName = smCompanyName;
	}

	/**
	 * Gets the sm registration type.
	 * 
	 * @return the smRegistrationType
	 */
	public String getSmRegistrationType() {
		return smRegistrationType;
	}

	/**
	 * Sets the sm registration type.
	 * 
	 * @param smRegistrationType
	 *            the new sm registration type
	 */
	public void setSmRegistrationType(String smRegistrationType) {
		this.smRegistrationType = smRegistrationType;
	}

	/**
	 * Gets the cm item type.
	 * 
	 * @return the cmItemType
	 */
	public String getCmItemType() {
		return cmItemType;
	}

	/**
	 * Sets the cm item type.
	 * 
	 * @param cmItemType
	 *            the new cm item type
	 */
	public void setCmItemType(String cmItemType) {
		this.cmItemType = cmItemType;
	}

	/**
	 * Gets the cm item status.
	 * 
	 * @return the cmItemStatus
	 */
	public String getCmItemStatus() {
		return cmItemStatus;
	}

	/**
	 * Sets the cm item status.
	 * 
	 * @param cmItemStatus
	 *            the new cm item status
	 */
	public void setCmItemStatus(String cmItemStatus) {
		this.cmItemStatus = cmItemStatus;
	}

	/**
	 * Gets the cm unspsc code.
	 * 
	 * @return the cmUnspscCode
	 */
	public String getCmUnspscCode() {
		return cmUnspscCode;
	}

	/**
	 * Sets the cm unspsc code.
	 * 
	 * @param cmUnspscCode
	 *            the new cm unspsc code
	 */
	public void setCmUnspscCode(String cmUnspscCode) {
		this.cmUnspscCode = cmUnspscCode;
	}

	/**
	 * Gets the cm unspsc title.
	 * 
	 * @return the cmUnspscTitle
	 */
	public String getCmUnspscTitle() {
		return cmUnspscTitle;
	}

	/**
	 * Sets the cm unspsc title.
	 * 
	 * @param cmUnspscTitle
	 *            the new cm unspsc title
	 */
	public void setCmUnspscTitle(String cmUnspscTitle) {
		this.cmUnspscTitle = cmUnspscTitle;
	}

	/**
	 * Gets the cm company name.
	 * 
	 * @return the cmCompanyName
	 */
	public String getCmCompanyName() {
		return cmCompanyName;
	}

	/**
	 * Sets the cm company name.
	 * 
	 * @param cmCompanyName
	 *            the new cm company name
	 */
	public void setCmCompanyName(String cmCompanyName) {
		this.cmCompanyName = cmCompanyName;
	}

	/**
	 * Gets the cm registration type.
	 * 
	 * @return the cmRegistrationType
	 */
	public String getCmRegistrationType() {
		return cmRegistrationType;
	}

	/**
	 * Sets the cm registration type.
	 * 
	 * @param cmRegistrationType
	 *            the new cm registration type
	 */
	public void setCmRegistrationType(String cmRegistrationType) {
		this.cmRegistrationType = cmRegistrationType;
	}

	/**
	 * Gets the cm item category.
	 * 
	 * @return the cmItemCategory
	 */
	public String getCmItemCategory() {
		return cmItemCategory;
	}

	/**
	 * Sets the cm item category.
	 * 
	 * @param cmItemCategory
	 *            the new cm item category
	 */
	public void setCmItemCategory(String cmItemCategory) {
		this.cmItemCategory = cmItemCategory;
	}

	/**
	 * Gets the cm item name.
	 * 
	 * @return the cmItemName
	 */
	public String getCmItemName() {
		return cmItemName;
	}

	/**
	 * Sets the cm item name.
	 * 
	 * @param cmItemName
	 *            the new cm item name
	 */
	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	/**
	 * Gets the cm ep number.
	 * 
	 * @return the cmEpNumber
	 */
	public String getCmEpNumber() {
		return cmEpNumber;
	}

	/**
	 * Sets the cm ep number.
	 * 
	 * @param cmEpNumber
	 *            the new cm ep number
	 */
	public void setCmEpNumber(String cmEpNumber) {
		this.cmEpNumber = cmEpNumber;
	}

	/**
	 * Gets the cm business reg no.
	 * 
	 * @return the cmBusinessRegNo
	 */
	public String getCmBusinessRegNo() {
		return cmBusinessRegNo;
	}

	/**
	 * Sets the cm business reg no.
	 * 
	 * @param cmBusinessRegNo
	 *            the new cm business reg no
	 */
	public void setCmBusinessRegNo(String cmBusinessRegNo) {
		this.cmBusinessRegNo = cmBusinessRegNo;
	}

	/**
	 * Gets the cm ic number.
	 * 
	 * @return the cmIcNumber
	 */
	public String getCmIcNumber() {
		return cmIcNumber;
	}

	/**
	 * Sets the cm ic number.
	 * 
	 * @param cmIcNumber
	 *            the new cm ic number
	 */
	public void setCmIcNumber(String cmIcNumber) {
		this.cmIcNumber = cmIcNumber;
	}

	/**
	 * Gets the cm mof number.
	 * 
	 * @return the cmMofNumber
	 */
	public String getCmMofNumber() {
		return cmMofNumber;
	}

	/**
	 * Sets the cm mof number.
	 * 
	 * @param cmMofNumber
	 *            the new cm mof number
	 */
	public void setCmMofNumber(String cmMofNumber) {
		this.cmMofNumber = cmMofNumber;
	}

	/**
	 * Gets the cm extention code.
	 * 
	 * @return the cmExtentionCode
	 */
	public String getCmExtentionCode() {
		return cmExtentionCode;
	}

	/**
	 * Sets the cm extention code.
	 * 
	 * @param cmExtentionCode
	 *            the new cm extention code
	 */
	public void setCmExtentionCode(String cmExtentionCode) {
		this.cmExtentionCode = cmExtentionCode;
	}

	/**
	 * Gets the cm extention type.
	 * 
	 * @return the cmExtentionType
	 */
	public String getCmExtentionType() {
		return cmExtentionType;
	}

	/**
	 * Sets the cm extention type.
	 * 
	 * @param cmExtentionType
	 *            the new cm extention type
	 */
	public void setCmExtentionType(String cmExtentionType) {
		this.cmExtentionType = cmExtentionType;
	}

	/**
	 * Gets the cm extention brand.
	 * 
	 * @return the cmExtentionBrand
	 */
	public String getCmExtentionBrand() {
		return cmExtentionBrand;
	}

	/**
	 * Sets the cm extention brand.
	 * 
	 * @param cmExtentionBrand
	 *            the new cm extention brand
	 */
	public void setCmExtentionBrand(String cmExtentionBrand) {
		this.cmExtentionBrand = cmExtentionBrand;
	}

	/**
	 * Gets the cm extention measurment.
	 * 
	 * @return the cmExtentionMeasurment
	 */
	public String getCmExtentionMeasurment() {
		return cmExtentionMeasurment;
	}

	/**
	 * Sets the cm extention measurment.
	 * 
	 * @param cmExtentionMeasurment
	 *            the new cm extention measurment
	 */
	public void setCmExtentionMeasurment(String cmExtentionMeasurment) {
		this.cmExtentionMeasurment = cmExtentionMeasurment;
	}

	/**
	 * Gets the cm extention colour.
	 * 
	 * @return the cmExtentionColour
	 */
	public String getCmExtentionColour() {
		return cmExtentionColour;
	}

	/**
	 * Sets the cm extention colour.
	 * 
	 * @param cmExtentionColour
	 *            the new cm extention colour
	 */
	public void setCmExtentionColour(String cmExtentionColour) {
		this.cmExtentionColour = cmExtentionColour;
	}

	/**
	 * Gets the cm proof identity.
	 * 
	 * @return the cmProofIdentity
	 */
	public String getCmProofIdentity() {
		return cmProofIdentity;
	}

	/**
	 * Sets the cm proof identity.
	 * 
	 * @param cmProofIdentity
	 *            the new cm proof identity
	 */
	public void setCmProofIdentity(String cmProofIdentity) {
		this.cmProofIdentity = cmProofIdentity;
	}

	/**
	 * Gets the sc quotation id.
	 * 
	 * @return the scQuotationId
	 */
	public String getScQuotationId() {
		return scQuotationId;
	}

	/**
	 * Sets the sc quotation id.
	 * 
	 * @param scQuotationId
	 *            the new sc quotation id
	 */
	public void setScQuotationId(String scQuotationId) {
		this.scQuotationId = scQuotationId;
	}

	/**
	 * Gets the sc quotation no.
	 * 
	 * @return the scQuotationNo
	 */
	public String getScQuotationNo() {
		return scQuotationNo;
	}

	/**
	 * Sets the sc quotation no.
	 * 
	 * @param scQuotationNo
	 *            the new sc quotation no
	 */
	public void setScQuotationNo(String scQuotationNo) {
		this.scQuotationNo = scQuotationNo;
	}

	/**
	 * Gets the sc payment id.
	 * 
	 * @return the scPaymentId
	 */
	public String getScPaymentId() {
		return scPaymentId;
	}

	/**
	 * Sets the sc payment id.
	 * 
	 * @param scPaymentId
	 *            the new sc payment id
	 */
	public void setScPaymentId(String scPaymentId) {
		this.scPaymentId = scPaymentId;
	}

	/**
	 * Sets the pdf file.
	 * 
	 * @param pdfFile
	 *            the new pdf file
	 */
	public void setPdfFile(StreamedContent pdfFile) {
		this.pdfFile = pdfFile;
	}

	/**
	 * Gets the constructed param string.
	 * 
	 * @return the constructedParamString
	 */
	public String getConstructedParamString() {
		return constructedParamString;
	}

	/**
	 * Sets the constructed param string.
	 * 
	 * @param constructedParamString
	 *            the new constructed param string
	 */
	public void setConstructedParamString(String constructedParamString) {
		this.constructedParamString = constructedParamString;
	}

	/**
	 * Gets the lev one cat lst.
	 * 
	 * @return the levOneCatLst
	 */
	public List<SelectItem> getLevOneCatLst() {
		return levOneCatLst;
	}

	/**
	 * Sets the lev one cat lst.
	 * 
	 * @param levOneCatLst
	 *            the new lev one cat lst
	 */
	public void setLevOneCatLst(List<SelectItem> levOneCatLst) {
		this.levOneCatLst = levOneCatLst;
	}

	/**
	 * Gets the lev two cat lst.
	 * 
	 * @return the levTwoCatLst
	 */
	public List<SelectItem> getLevTwoCatLst() {
		return levTwoCatLst;
	}

	/**
	 * Sets the lev two cat lst.
	 * 
	 * @param levTwoCatLst
	 *            the new lev two cat lst
	 */
	public void setLevTwoCatLst(List<SelectItem> levTwoCatLst) {
		this.levTwoCatLst = levTwoCatLst;
	}

	/**
	 * Gets the lev three cat lst.
	 * 
	 * @return the levThreeCatLst
	 */
	public List<SelectItem> getLevThreeCatLst() {
		return levThreeCatLst;
	}

	/**
	 * Sets the lev three cat lst.
	 * 
	 * @param levThreeCatLst
	 *            the new lev three cat lst
	 */
	public void setLevThreeCatLst(List<SelectItem> levThreeCatLst) {
		this.levThreeCatLst = levThreeCatLst;
	}

	/**
	 * Gets the state lst.
	 * 
	 * @return the stateLst
	 */
	public List<SelectItem> getStateLst() {
		return stateLst;
	}

	/**
	 * Sets the state lst.
	 * 
	 * @param stateLst
	 *            the new state lst
	 */
	public void setStateLst(List<SelectItem> stateLst) {
		this.stateLst = stateLst;
	}

	/**
	 * Gets the district lst.
	 * 
	 * @return the districtLst
	 */
	public List<SelectItem> getDistrictLst() {
		return districtLst;
	}

	/**
	 * Sets the district lst.
	 * 
	 * @param districtLst
	 *            the new district lst
	 */
	public void setDistrictLst(List<SelectItem> districtLst) {
		this.districtLst = districtLst;
	}

	/**
	 * Gets the generated url.
	 * 
	 * @return the generatedURL
	 */
	public String getGeneratedURL() {
		return generatedURL;
	}

	/**
	 * Sets the generated url.
	 * 
	 * @param generatedURL
	 *            the new generated url
	 */
	public void setGeneratedURL(String generatedURL) {
		this.generatedURL = generatedURL;
	}

	/**
	 * Gets the report url.
	 * 
	 * @return the reportURL
	 */
	public String getReportURL() {
		return reportURL;
	}

	/**
	 * Sets the report url.
	 * 
	 * @param reportURL
	 *            the new report url
	 */
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}

	/**
	 * Gets the user id.
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userId
	 *            the new user id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the role code.
	 * 
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * Sets the role code.
	 * 
	 * @param roleCode
	 *            the new role code
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * Gets the from date.
	 * 
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 * 
	 * @param fromDate
	 *            the new from date
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 * 
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 * 
	 * @param toDate
	 *            the new to date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	/**
	 * Gets the fl rec note no.
	 * 
	 * @return the flRecNoteNo
	 */
	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}

	/**
	 * Sets the fl rec note no.
	 * 
	 * @param flRecNoteNo
	 *            the new fl rec note no
	 */
	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
	}

	/**
	 * Gets the authoriztion list.
	 * 
	 * @return the authoriztionList
	 */
	public List<String> getAuthoriztionList() {
		return authoriztionList;
	}

	/**
	 * Sets the authoriztion list.
	 * 
	 * @param authoriztionList
	 *            the new authoriztion list
	 */
	public void setAuthoriztionList(List<String> authoriztionList) {
		this.authoriztionList = authoriztionList;
	}

	/**
	 * Gets the scqt.
	 * 
	 * @return the scqt
	 */
	public ScQt getScqt() {
		return scqt;
	}

	/**
	 * Sets the scqt.
	 * 
	 * @param scqt
	 *            the new scqt
	 */
	public void setScqt(ScQt scqt) {
		this.scqt = scqt;
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
	 * Gets the reg type list.
	 * 
	 * @return the regTypeList
	 */
	public List<SelectItem> getRegTypeList() {
		return regTypeList;
	}

	/**
	 * Sets the reg type list.
	 * 
	 * @param regTypeList
	 *            the new reg type list
	 */
	public void setRegTypeList(List<SelectItem> regTypeList) {
		this.regTypeList = regTypeList;
	}

	/**
	 * Gets the module list.
	 * 
	 * @return the moduleList
	 */
	public List<SelectItem> getModuleList() {
		return moduleList;
	}

	/**
	 * Sets the module list.
	 * 
	 * @param moduleList
	 *            the new module list
	 */
	public void setModuleList(List<SelectItem> moduleList) {
		this.moduleList = moduleList;
	}

	/**
	 * Gets the rpt gen bean.
	 * 
	 * @return the rptGenBean
	 */
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}

	/**
	 * Sets the rpt gen bean.
	 * 
	 * @param rptGenBean
	 *            the new rpt gen bean
	 */
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}

	/**
	 * Gets the report list.
	 * 
	 * @return the reportList
	 */
	public List<SelectItem> getReportList() {
		return reportList;
	}

	/**
	 * Sets the report list.
	 * 
	 * @param reportList
	 *            the new report list
	 */
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	/**
	 * Checks if is enable value.
	 * 
	 * @return true, if is enable value
	 */
	public boolean isEnableValue() {
		return enableValue;
	}

	/**
	 * Sets the enable value.
	 * 
	 * @param enableValue
	 *            the new enable value
	 */
	public void setEnableValue(boolean enableValue) {
		this.enableValue = enableValue;
	}

	/**
	 * Gets the simple quote no list.
	 * 
	 * @return the simpleQuoteNoList
	 */
	public List<String> getSimpleQuoteNoList() {
		return simpleQuoteNoList;
	}

	/**
	 * Sets the simple quote no list.
	 * 
	 * @param simpleQuoteNoList
	 *            the new simple quote no list
	 */
	public void setSimpleQuoteNoList(List<String> simpleQuoteNoList) {
		this.simpleQuoteNoList = simpleQuoteNoList;
	}

	/**
	 * Gets the sc soa status list.
	 * 
	 * @return the scSOAStatusList
	 */
	public List<SelectItem> getScSOAStatusList() {
		return scSOAStatusList;
	}

	/**
	 * Sets the sc soa status list.
	 * 
	 * @param scSOAStatusList
	 *            the new sc soa status list
	 */
	public void setScSOAStatusList(List<SelectItem> scSOAStatusList) {
		this.scSOAStatusList = scSOAStatusList;
	}

	/**
	 * Gets the sc soa status.
	 * 
	 * @return the scSOAStatus
	 */
	public String getScSOAStatus() {
		return scSOAStatus;
	}

	/**
	 * Sets the sc soa status.
	 * 
	 * @param scSOAStatus
	 *            the new sc soa status
	 */
	public void setScSOAStatus(String scSOAStatus) {
		this.scSOAStatus = scSOAStatus;
	}

	/**
	 * Gets the sc simple quote type.
	 * 
	 * @return the scSimpleQuoteType
	 */
	public String getScSimpleQuoteType() {
		return scSimpleQuoteType;
	}

	/**
	 * Sets the sc simple quote type.
	 * 
	 * @param scSimpleQuoteType
	 *            the new sc simple quote type
	 */
	public void setScSimpleQuoteType(String scSimpleQuoteType) {
		this.scSimpleQuoteType = scSimpleQuoteType;
	}

	/**
	 * Gets the sc loa no.
	 * 
	 * @return the scLOANo
	 */
	public String getScLOANo() {
		return scLOANo;
	}

	/**
	 * Sets the sc loa no.
	 * 
	 * @param scLOANo
	 *            the new sc loa no
	 */
	public void setScLOANo(String scLOANo) {
		this.scLOANo = scLOANo;
	}

	/**
	 * Gets the search unspsc paging data model.
	 * 
	 * @return the searchLOVPagingDataModel
	 */
	public IPagingDataModel<CmUnspsc> getSearchUnspscPagingDataModel() {
		return searchUnspscPagingDataModel;
	}

	/**
	 * Sets the search unspsc paging data model.
	 * 
	 * @param searchUnspscPagingDataModel
	 *            the new search unspsc paging data model
	 */
	public void setSearchUnspscPagingDataModel(
			IPagingDataModel<CmUnspsc> searchUnspscPagingDataModel) {
		this.searchUnspscPagingDataModel = searchUnspscPagingDataModel;
	}

	/**
	 * Gets the mof account end date from date.
	 * 
	 * @return the mofAccountEndDateFromDate
	 */
	public Date getMofAccountEndDateFromDate() {
		return mofAccountEndDateFromDate;
	}

	/**
	 * Sets the mof account end date from date.
	 * 
	 * @param mofAccountEndDateFromDate
	 *            the mofAccountEndDateFromDate to set
	 */
	public void setMofAccountEndDateFromDate(Date mofAccountEndDateFromDate) {
		this.mofAccountEndDateFromDate = mofAccountEndDateFromDate;
	}

	/**
	 * Gets the mof account end date to date.
	 * 
	 * @return the mofAccountEndDateToDate
	 */
	public Date getMofAccountEndDateToDate() {
		return mofAccountEndDateToDate;
	}

	/**
	 * Sets the mof account end date to date.
	 * 
	 * @param mofAccountEndDateToDate
	 *            the mofAccountEndDateToDate to set
	 */
	public void setMofAccountEndDateToDate(Date mofAccountEndDateToDate) {
		this.mofAccountEndDateToDate = mofAccountEndDateToDate;
	}

	/**
	 * Gets the pm ep no.
	 * 
	 * @return the pmEpNo
	 */
	public String getPmEpNo() {
		return pmEpNo;
	}

	/**
	 * Sets the pm ep no.
	 * 
	 * @param pmEpNo
	 *            the pmEpNo to set
	 */
	public void setPmEpNo(String pmEpNo) {
		this.pmEpNo = pmEpNo;
	}

	/**
	 * Gets the pm org cateogry.
	 * 
	 * @return the pmOrgCateogry
	 */
	public String getPmOrgCategory() {
		return pmOrgCategory;
	}

	/**
	 * Sets the pm org cateogry.
	 * 
	 * @param pmOrgCategory
	 *            the new pm org category
	 */
	public void setPmOrgCategory(String pmOrgCategory) {
		this.pmOrgCategory = pmOrgCategory;
	}

	/**
	 * Gets the pm org type.
	 * 
	 * @return the pmOrgType
	 */
	public String getPmOrgType() {
		return pmOrgType;
	}

	/**
	 * Sets the pm org type.
	 * 
	 * @param pmOrgType
	 *            the pmOrgType to set
	 */
	public void setPmOrgType(String pmOrgType) {
		this.pmOrgType = pmOrgType;
	}

	/**
	 * Gets the pm org name.
	 * 
	 * @return the pmOrgName
	 */
	public String getPmOrgName() {
		return pmOrgName;
	}

	/**
	 * Sets the pm org name.
	 * 
	 * @param pmOrgName
	 *            the pmOrgName to set
	 */
	public void setPmOrgName(String pmOrgName) {
		this.pmOrgName = pmOrgName;
	}

	/**
	 * Gets the pm org code.
	 * 
	 * @return the pmOrgCode
	 */
	public String getPmOrgCode() {
		return pmOrgCode;
	}

	/**
	 * Sets the pm org code.
	 * 
	 * @param pmOrgCode
	 *            the pmOrgCode to set
	 */
	public void setPmOrgCode(String pmOrgCode) {
		this.pmOrgCode = pmOrgCode;
	}

	/**
	 * Gets the pm item name.
	 * 
	 * @return the pmItemName
	 */
	public String getPmItemName() {
		return pmItemName;
	}

	/**
	 * Sets the pm item name.
	 * 
	 * @param pmItemName
	 *            the pmItemName to set
	 */
	public void setPmItemName(String pmItemName) {
		this.pmItemName = pmItemName;
	}

	/**
	 * Gets the pm item code.
	 * 
	 * @return the pmItemCode
	 */
	public String getPmItemCode() {
		return pmItemCode;
	}

	/**
	 * Sets the pm item code.
	 * 
	 * @param pmItemCode
	 *            the pmItemCode to set
	 */
	public void setPmItemCode(String pmItemCode) {
		this.pmItemCode = pmItemCode;
	}

	/**
	 * Gets the pm company name.
	 * 
	 * @return the pmCompanyName
	 */
	public String getPmCompanyName() {
		return pmCompanyName;
	}

	/**
	 * Sets the pm company name.
	 * 
	 * @param pmCompanyName
	 *            the pmCompanyName to set
	 */
	public void setPmCompanyName(String pmCompanyName) {
		this.pmCompanyName = pmCompanyName;
	}

	/**
	 * Gets the pm identification no.
	 * 
	 * @return the pmIdentificationNo
	 */
	public String getPmIdentificationNo() {
		return pmIdentificationNo;
	}

	/**
	 * Sets the pm identification no.
	 * 
	 * @param pmIdentificationNo
	 *            the pmIdentificationNo to set
	 */
	public void setPmIdentificationNo(String pmIdentificationNo) {
		this.pmIdentificationNo = pmIdentificationNo;
	}

	/**
	 * Gets the pm user name.
	 * 
	 * @return the pmUserName
	 */
	public String getPmUserName() {
		return pmUserName;
	}

	/**
	 * Gets the pm physical contract no.
	 * 
	 * @return the pmPhysicalContractNo
	 */
	public String getPmPhysicalContractNo() {
		return pmPhysicalContractNo;
	}

	/**
	 * Sets the pm physical contract no.
	 * 
	 * @param pmPhysicalContractNo
	 *            the pmPhysicalContractNo to set
	 */
	public void setPmPhysicalContractNo(String pmPhysicalContractNo) {
		this.pmPhysicalContractNo = pmPhysicalContractNo;
	}

	/**
	 * Gets the pm loa no.
	 * 
	 * @return the pmLOANo
	 */
	public String getPmLOANo() {
		return pmLOANo;
	}

	/**
	 * Sets the pm loa no.
	 * 
	 * @param pmLOANo
	 *            the pmLOANo to set
	 */
	public void setPmLOANo(String pmLOANo) {
		this.pmLOANo = pmLOANo;
	}

	/**
	 * Gets the pm physical loa no.
	 * 
	 * @return the pmPhysicalLOANo
	 */
	public String getPmPhysicalLOANo() {
		return pmPhysicalLOANo;
	}

	/**
	 * Sets the pm physical loa no.
	 * 
	 * @param pmPhysicalLOANo
	 *            the pmPhysicalLOANo to set
	 */
	public void setPmPhysicalLOANo(String pmPhysicalLOANo) {
		this.pmPhysicalLOANo = pmPhysicalLOANo;
	}

	/**
	 * Sets the pm user name.
	 * 
	 * @param pmUserName
	 *            the pmUserName to set
	 */
	public void setPmUserName(String pmUserName) {
		this.pmUserName = pmUserName;
	}

	/**
	 * Gets the pm role code.
	 * 
	 * @return the pmRoleCode
	 */
	public String getPmRoleCode() {
		return pmRoleCode;
	}

	/**
	 * Sets the pm role code.
	 * 
	 * @param pmRoleCode
	 *            the pmRoleCode to set
	 */
	public void setPmRoleCode(String pmRoleCode) {
		this.pmRoleCode = pmRoleCode;
	}

	/**
	 * Gets the pm contract no.
	 * 
	 * @return the pmContractNo
	 */
	public String getPmContractNo() {
		return pmContractNo;
	}

	/**
	 * Sets the pm contract no.
	 * 
	 * @param pmContractNo
	 *            the pmContractNo to set
	 */
	public void setPmContractNo(String pmContractNo) {
		this.pmContractNo = pmContractNo;
	}

	/**
	 * @return the pmZone
	 */
	public String getPmZone() {
		return pmZone;
	}

	/**
	 * @param pmZone
	 *            the pmZone to set
	 */
	public void setPmZone(String pmZone) {
		this.pmZone = pmZone;
	}

	/**
	 * @return the pmProfileStatus
	 */
	public String getPmProfileStatus() {
		return pmProfileStatus;
	}

	/**
	 * @param pmProfileStatus
	 *            the pmProfileStatus to set
	 */
	public void setPmProfileStatus(String pmProfileStatus) {
		this.pmProfileStatus = pmProfileStatus;
	}

	/**
	 * Gets the sm bus company type.
	 * 
	 * @return the smBusCompanyType
	 */
	public String getSmBusCompanyType() {
		return smBusCompanyType;
	}

	/**
	 * Sets the sm bus company type.
	 * 
	 * @param smBusCompanyType
	 *            the smBusCompanyType to set
	 */
	public void setSmBusCompanyType(String smBusCompanyType) {
		this.smBusCompanyType = smBusCompanyType;
	}

	/**
	 * Gets the sm bus company type list.
	 * 
	 * @return the smBusCompanyList
	 */
	public List<SelectItem> getSmBusCompanyTypeList() {
		return smBusCompanyTypeList;
	}

	/**
	 * Sets the sm bus company type list.
	 * 
	 * @param smBusCompanyTypeList
	 *            the new sm bus company type list
	 */
	public void setSmBusCompanyTypeList(List<SelectItem> smBusCompanyTypeList) {
		this.smBusCompanyTypeList = smBusCompanyTypeList;
	}

	/**
	 * Gets the registration status list of bus comp type.
	 * 
	 * @return the registrationStatusListOfBusCompType
	 */
	public List<SelectItem> getRegistrationStatusListOfBusCompType() {
		return registrationStatusListOfBusCompType;
	}

	/**
	 * Sets the registration status list of bus comp type.
	 * 
	 * @param registrationStatusListOfBusCompType
	 *            the registrationStatusListOfBusCompType to set
	 */
	public void setRegistrationStatusListOfBusCompType(
			List<SelectItem> registrationStatusListOfBusCompType) {
		this.registrationStatusListOfBusCompType = registrationStatusListOfBusCompType;
	}

	/**
	 * Gets the sm bus comp type registration status.
	 * 
	 * @return the smBusCompTypeRegistrationStatus
	 */
	public String getSmBusCompTypeRegistrationStatus() {
		return smBusCompTypeRegistrationStatus;
	}

	/**
	 * Sets the sm bus comp type registration status.
	 * 
	 * @param smBusCompTypeRegistrationStatus
	 *            the smBusCompTypeRegistrationStatus to set
	 */
	public void setSmBusCompTypeRegistrationStatus(
			String smBusCompTypeRegistrationStatus) {
		this.smBusCompTypeRegistrationStatus = smBusCompTypeRegistrationStatus;
	}

	/**
	 * retrieveLOAStatusList- will retrieve LOA status(SOA Status) list.
	 * 
	 * @return the SOAStatusList
	 */
	public List<SelectItem> retrieveLOAStatusList() {
		log.info("ReportBackingBean-retrieveLOAStatusList");
		scSOAStatusList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> soaStatusList = reportSO
					.retrieveLOAStatusList(strLanguage);
			for (Object[] object : soaStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				scSOAStatusList.add(selectItem);
			}
		}
		return scSOAStatusList;
	}

	/**
	 * supplierNameValidate-will validate Supplier name, like supplier name
	 * should have minimum 3 characters length.
	 */
	public void supplierNameValidate() {
		log.info("ReportBackingBean-supplierNameValidate");
		if ((this.getScSupplier() != null)
				&& (this.getScSupplier() != ReportConst.EMPTY_VAL)) {
			if (this.getScSupplier().trim().length() < 3) {
				this.setScSupplier(null);
				throw new EpException(MessageConst.ERROR_REPORT_780025);
			}
		}
	}

	/**
	 * changeTerminate- based on status =TERMINATE, it will enable and disable
	 * the termination type field.
	 */
	public void changeTerminate() {
		log.info("ReportBackingBean-changeTerminate");
		this.setCtTerminationType(null);
		getManagedBean().getRptGenBean().setCtTerminationTypeView(false);
		if ((null != this.getCtStatusTwo())
				&& (ReportConst.EMPTY_VAL != this.getCtStatusTwo())) {
			if (this.getCtStatusTwo().trim().equalsIgnoreCase("TERMINATE")) {
				getManagedBean().getRptGenBean().setCtTerminationTypeView(true);
			} else {
				getManagedBean().getRptGenBean()
						.setCtTerminationTypeView(false);
			}
		} else {
			getManagedBean().getRptGenBean().setCtTerminationTypeView(false);
		}
	}

	/**
	 * changePaymentType- will change to retrieve payment type list based on
	 * selecting payment type like Government, CDC or simple payment type list.
	 */
	public void changePaymentType() {
		log.info("ReportBackingBean-changePaymentType");
		this.setSmPaymentType(null);
		paymentTypeList = new ArrayList<SelectItem>();
		if ((null != this.getSmPaymentTo())
				&& (ReportConst.EMPTY_VAL != this.getSmPaymentTo())) {
			if (this.getSmPaymentTo().trim().equalsIgnoreCase("Government")) {
				retrieveGovtPaymentTypeList();
			} else if (this.getSmPaymentTo().trim().equalsIgnoreCase("CDC")) {
				retrieveCDCPaymentTypeList();
			} else {
				retrievePaymentTypeList();
			}
		} else {
			retrievePaymentTypeList();
		}
	}

	/**
	 * retrievePaymentTypeList - will retrieve simple payment type list.
	 * 
	 * @return the paymentTypeList
	 */
	public List<SelectItem> retrievePaymentTypeList() {
		log.info("ReportBackingBean-retrievePaymentTypeList");
		paymentTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> regStatusList = reportSO
					.retrievePaymentTypeList(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				paymentTypeList.add(selectItem);
			}
		}
		return paymentTypeList;
	}

	/**
	 * retrieveGovtPaymentTypeList - will retrieve govt payment type list.
	 * 
	 * @return the paymentTypeList
	 */
	public List<SelectItem> retrieveGovtPaymentTypeList() {
		log.info("ReportBackingBean-retrieveGovtPaymentTypeList");
		paymentTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> regStatusList = reportSO
					.retrieveGovtPaymentTypeList(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				paymentTypeList.add(selectItem);
			}
		}
		return paymentTypeList;
	}

	/**
	 * retrieveCDCPaymentTypeList - will retrieve cdc payment type list.
	 * 
	 * @return the paymentTypeList
	 */
	public List<SelectItem> retrieveCDCPaymentTypeList() {
		log.info("ReportBackingBean-retrieveCDCPaymentTypeList");
		paymentTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> regStatusList = reportSO
					.retrieveCDCPaymentTypeList(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				paymentTypeList.add(selectItem);
			}
		}
		return paymentTypeList;
	}

	/**
	 * retrievePaymentMethod - will etrieve payment method list.
	 * 
	 * @return the paymentMethodList
	 */
	public List<String> retrievePaymentMethod() {
		log.info("ReportBackingBean-retrievePaymentMethod");
		paymentMethodList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> payMethodLst = reportSO.retrievePaymentMethod();
			for (int i = 0; i < payMethodLst.size(); i++) {
				paymentMethodList.add(payMethodLst.get(i));
			}
		}
		return paymentMethodList;
	}

	/**
	 * retrievePMServiceType - will Retrieve service type List.
	 * 
	 * @return the serviceTypeList
	 */
	public List<SelectItem> retrievePMServiceType() {
		log.info("ReportBackingBean-retrievePMServiceType");
		serviceTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> serviceTypeLst = reportSO
					.retrievePMServiceType(strLanguage);

			for (Object[] object : serviceTypeLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				serviceTypeList.add(selectItem);
			}
		}
		return serviceTypeList;
	}

	/**
	 * showCapitalNaccumShare - will change to Show capital or accumulated share
	 * date.
	 */
	public void showCapitalNaccumShare() {
		log.info("ReportBackingBean-showCapitalNaccumShare");
		getManagedBean().getRptGenBean().setSmPaidUpCapitalFrmView(false);
		if ((null != this.getReportValue())
				&& (this.getReportValue()
						.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {
			if ((null != this.getSmPaidUpCapitalNaccum())
					&& (ReportConst.EMPTY_VAL != this.getSmApplicationStatus())) {
				if (this.getSmPaidUpCapitalNaccum().trim()
						.equalsIgnoreCase("P")) {
					getManagedBean().getRptGenBean().setSmPaidUpCapitalFrmView(
							true);
					getManagedBean().getRptGenBean().setSmPaidUpCptlDateView(
							true);
				} else if (this.getSmPaidUpCapitalNaccum().trim()
						.equalsIgnoreCase("A")) {
					getManagedBean().getRptGenBean().setSmPaidUpCapitalFrmView(
							true);
					getManagedBean().getRptGenBean().setSmAccmltdCptlDateView(
							true);
				}
			}
		}
	}

	/**
	 * retrieveProcessingOfficerList - will retrieve processing officer list.
	 * 
	 * @return the processingOfficerList
	 */
	public List<SelectItem> retrieveProcessingOfficerList() {
		log.info("ReportBackingBean-retrieveProcessingOfficerList");
		this.setSmState(null);
		rptGenBean.retrieveStatesByZoneName(this.smZone);
		districtLst = new ArrayList<SelectItem>();

		processingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO
					.retrieveProcessingOfficerList(this.getSmZone());
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				processingOfficerList.add(selectItem);
			}

		}
		return processingOfficerList;
	}

	/**
	 * applicationStatusDateChange - will change application status date.
	 */
	public void applicationStatusDateChange() {
		log.info("ReportBackingBean-applicationStatusDateChange");
		getManagedBean().getRptGenBean()
				.setSmMandatoryExpectedSiteVisitDateView(false);
		getManagedBean().getRptGenBean().setSmMandatoryActualSiteVisitDateView(
				false);
		// getManagedBean().getRptGenBean().setSmMandatoryFromDateView(false);
		if ((null != this.getReportValue())
				&& (this.getReportValue()
						.equals(ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL))) {
			if ((null != this.getSmApplicationStatus())
					&& (this.getSmApplicationStatus()
							.equals(ReportConst.PEND_PRPR_SITE_VISIT))) {
				getManagedBean().getRptGenBean().setSmMandatoryFromDateView(
						true);
				getManagedBean().getRptGenBean()
						.setSmMandatoryExpectedSiteVisitDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryDateView(false);
			} else if ((null != this.getSmApplicationStatus())
					&& (this.getSmApplicationStatus()
							.equals(ReportConst.SITE_VISIT_COMPLTD))) {
				getManagedBean().getRptGenBean().setSmMandatoryFromDateView(
						true);
				getManagedBean().getRptGenBean()
						.setSmMandatoryActualSiteVisitDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryDateView(false);
			}/*
			 * else{
			 * getManagedBean().getRptGenBean().setSmMandatoryDateView(true); }
			 */
		}
	}

	/**
	 * changeReportType - will change report type.
	 */
	public void changeReportType() {
		log.info("ReportBackingBean-changeReportType");
		if ((null != this.getReportValue())
				&& (this.getReportValue()
						.equals(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
			getManagedBean().getRptGenBean().setSmMandatoryCreatedFromYearView(
					false);
			getManagedBean().getRptGenBean()
					.setSmMandatoryRegisteredReportTypeDateView(false);
			getManagedBean().getRptGenBean()
					.setSmMandatoryRecievedReportTypeView(false);
			getManagedBean().getRptGenBean()
					.setSmMandatoryApprovedReportTypeView(false);
			getManagedBean().getRptGenBean()
					.setSmMandatoryRejectedReportTypeView(false);
			if (null != this.getSmReportType()
					&& this.getSmReportType().equalsIgnoreCase("RECEIVED")) {
				getManagedBean().getRptGenBean()
						.setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean()
						.setSmMandatoryRecievedReportTypeView(true);
			} else if (null != this.getSmReportType()
					&& this.getSmReportType().equalsIgnoreCase("APPROVED")) {
				getManagedBean().getRptGenBean()
						.setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean()
						.setSmMandatoryApprovedReportTypeView(true);
			} else if (null != this.getSmReportType()
					&& this.getSmReportType().equalsIgnoreCase("REJECTED")) {
				getManagedBean().getRptGenBean()
						.setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean()
						.setSmMandatoryRejectedReportTypeView(true);
			}
			/*
			 * * else { rptGenBean.setSmMandatoryCreatedFromYearView(true);
			 * rptGenBean.setSmMandatoryRegisteredReportTypeDateView(true); }
			 */

		}
	}

	/**
	 * retrieveMinistryName - will retrieve ministry name based on organization
	 * profile id.
	 * 
	 * @param orgprofileid
	 *            the orgprofileid
	 * @return the string ministryName
	 */
	public String retrieveMinistryName(Long orgprofileid) {
		log.info("ReportBackingBean-retrieveMinistryName");
		ministryName = null;
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> ministryNameLst = reportSO
					.retrieveMinistryNameByOrgprofileid(orgprofileid);
			if (ministryNameLst.size() > 0) {

				ministryName = ministryNameLst.get(0);
			}
		}
		return ministryName;
	}

	/**
	 * retrieveRegStatusList - will retrieve registration status list.
	 * 
	 * @return the registrationStatusList
	 */
	public List<SelectItem> retrieveRegStatusList() {
		log.info("ReportBackingBean-retrieveRegStatusList");
		registrationStatusList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO.retrieveRegStatusList("en");
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				registrationStatusList.add(selectItem);
			}
		}
		return registrationStatusList;
	}

	/**
	 * Retrieve reg status list for contract.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveRegStatusListForContract() {
		log.info("ReportBackingBean-retrieveRegStatusListForContract");
		registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
		this.setSmBusCompTypeRegistrationStatus(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO
					.retrieveRegStatusListForContract(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				registrationStatusListOfBusCompType.add(selectItem);
			}
		}
		return registrationStatusListOfBusCompType;
	}

	/**
	 * Retrieve reg status list for consultant.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveRegStatusListForConsultant() {
		log.info("ReportBackingBean-retrieveRegStatusListForConsultant");
		registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
		this.setSmBusCompTypeRegistrationStatus(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO
					.retrieveRegStatusListForConsultant(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				registrationStatusListOfBusCompType.add(selectItem);
			}
		}
		return registrationStatusListOfBusCompType;
	}

	/**
	 * Retriev category list by supplier type.
	 * 
	 * @param supplierType
	 *            the supplier type
	 * @return the list
	 */
	public List<PmCategoryL1> retrievCategoryListBySupplierType(
			String supplierType) {
		log.info("ReportBackingBean-retrievCategoryListBySupplierType");

		// to clear the category drop down values
		this.setSupplierParentCatList(new ArrayList<PmCategoryL1>());
		for (SmSupplierCategory cat : this.getSelectedCatList()) {
			cat.setPmCategoryL1(new PmCategoryL1());
			cat.setPmCategoryL2(new PmCategoryL2());
			cat.setPmCategoryL3(new PmCategoryL3());
		}

		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setSupplierTypeCode(supplierType);

		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = reqSO
				.retrieveCategoryL1ListBySupplierType(reqDTO);
		if (catCodeResDTO != null) {
			return catCodeResDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve reg status list for all.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveRegStatusListForAll() {
		log.info("ReportBackingBean-retrieveRegStatusListForAll");
		this.registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
		this.setSmBusCompTypeRegistrationStatus(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> regStatusList = reportSO
					.retrieveRegStatusListForAll(strLanguage);
			for (Object[] object : regStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				this.registrationStatusListOfBusCompType.add(selectItem);
			}
		}
		return this.registrationStatusListOfBusCompType;
	}

	/**
	 * Retrieve consultant bus company data.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveConsultantBusCompanyData() {
		log.info("ReportBackingBean-retrieveConsultantBusCompanyData");
		this.smBusCompanyTypeList = new ArrayList<SelectItem>();
		this.setSmBusCompanyType(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> retrieveConsultantList = reportSO
					.getConsultantBusCompanyData(strLanguage);
			for (Object[] object : retrieveConsultantList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				this.smBusCompanyTypeList.add(selectItem);
			}
		}
		return this.smBusCompanyTypeList;
	}

	/**
	 * Retrieve contract bus company data.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveContractBusCompanyData() {
		log.info("ReportBackingBean-retrieveContractBusCompanyData");
		this.smBusCompanyTypeList = new ArrayList<SelectItem>();
		this.setSmBusCompanyType(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> retrieveContractList = reportSO
					.getContractBusCompanyData(strLanguage);
			for (Object[] object : retrieveContractList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				this.smBusCompanyTypeList.add(selectItem);
			}
		}
		return this.smBusCompanyTypeList;
	}

	/**
	 * Retrieve all bus company data.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveAllBusCompanyData() {
		log.info("ReportBackingBean-retrieveAllBusCompanyData");
		this.smBusCompanyTypeList = new ArrayList<SelectItem>();

		this.setSmBusCompanyType(null);
		this.getManagedBean().getRptGenBean()
				.setSmBusCompanyPaidUpAccumlatedShareView(true);
		this.getManagedBean().getRptGenBean()
				.setSmPaidAccmltdShareCptlSelView(true);
		this.getManagedBean().getRptGenBean().setSmAccmltdCptlSelView(false);
		this.getManagedBean().getRptGenBean().setSmPaidUpCptlSelView(false);
		this.getManagedBean().getRptGenBean().setSmAccmltdShareSelView(false);

		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> retrieveAllList = reportSO
					.getAllBusCompanyData(strLanguage);
			for (Object[] object : retrieveAllList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[1].toString());
				selectItem.setLabel(object[0].toString());
				this.smBusCompanyTypeList.add(selectItem);
			}
		}
		return this.smBusCompanyTypeList;
	}

	/**
	 * Supplier type consultant.
	 */
	public void supplierTypeConsultant() {
		log.info("ReportBackingBean-supplierTypeConsultant");
		if ((null != this.getReportValue())
				&& (this.getReportValue()
						.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {
			if (null != this.getSmSupplierType()) {
				if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONSULT_TYPE)) {
					retrieveConsultantBusCompanyData();
					retrieveRegStatusListForConsultant();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				} else if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONTRACT_TYPE)) {
					retrieveContractBusCompanyData();
					retrieveRegStatusListForContract();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				}
			} else {
				retrieveAllBusCompanyData();
				retrieveRegStatusListForAll();
				for (SmSupplierCategory cat : this.getSelectedCatList()) {
					cat.setPmCategoryL1(new PmCategoryL1());
					cat.setPmCategoryL2(new PmCategoryL2());
					cat.setPmCategoryL3(new PmCategoryL3());
				}
				this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
			}
		} else if (null != this.getReportValue()) {
			if (this.getReportValue().equalsIgnoreCase(
					ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)) {
				if (this.getSmSupplierType() != null) {
					if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONSULT_TYPE)) {
						this.setSmMofRegNo(ReportConst.SUPPL_CONSULT_MOF_NO);
					} else if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONTRACT_TYPE)) {
						this.setSmMofRegNo(ReportConst.SUPPL_CONTRACT_MOF_NO);
					}
				} else {
					this.setSmMofRegNo(null);
				}
			} else if (this.getReportValue().equalsIgnoreCase(
					ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL)) {
				if (this.getSmSupplierType() != null) {
					if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONSULT_TYPE)) {
						this.setSmMofRegNoFrom(ReportConst.SUPPL_CONSULT_MOF_NO);
						this.setSmMofRegNoTo(ReportConst.SUPPL_CONSULT_MOF_NO);
					} else if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONTRACT_TYPE)) {
						this.setSmMofRegNoFrom(ReportConst.SUPPL_CONTRACT_MOF_NO);
						this.setSmMofRegNoTo(ReportConst.SUPPL_CONTRACT_MOF_NO);
					}
				} else {
					this.setSmMofRegNoFrom(null);
					this.setSmMofRegNoTo(null);
				}
			}
		}

	}

	/**
	 * getAllModuleList - will retrieve the all module list to dropdown list.
	 * 
	 * @return the moduleList
	 */
	public List<SelectItem> getAllModuleList() {
		log.info("ReportBackingBean-getAllModuleList");
		moduleList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			/*
			 * String language =
			 * ResourceBundleUtils.getMessage(ReportConst.LABEL_LANG,
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
			 * FacesScopes().getUserLocale()); //String language = "ms";
			 */
			List<Object[]> moduleLst = reportSO.retrieveModuleList(strLanguage,
					lngUserId);
			for (Object[] object : moduleLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				moduleList.add(selectItem);
			}
		}
		return moduleList;
	}

	/**
	 * retrieveIdentificationType - will retrieve identification type List.
	 */
	public void retrieveIdentificationType() {
		log.info("ReportBackingBean-retrieveIdentificationType");
		identificationTypeList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> identificationTypeLst = reportSO
					.retrieveIdentificationType();
			for (Object[] object : identificationTypeLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				identificationTypeList.add(selectItem);
			}
		}
	}

	/**
	 * retrieveAccumulatedPaidUpCptl - will retrieve accumulated paid-up
	 * capital.
	 */
	public void retrieveAccumulatedPaidUpCptl() {
		log.info("ReportBackingBean-retrieveAccumulatedPaidUpCptl");
		accumulatedPaidUpCptlList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);

			List<Object[]> accumulatedPaidUpCptlLst = reportSO
					.retrieveAccumulatedPaidUpCptl();
			for (Object[] object : accumulatedPaidUpCptlLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				accumulatedPaidUpCptlList.add(selectItem);
			}
		}
	}

	/**
	 * getLtstMinistryList - Gets the latest ministry list based on organization
	 * type.
	 * 
	 * @return the ministryList selectItem
	 */
	public SelectItem[] getLtstMinistryList() {
		log.info("ReportBackingBean-getLtstMinistryList");
		SelectItem[] items = null;

		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			items = new SelectItem[resDTO.getOrgProfileList().size() + 1];
			// items[0] = new SelectItem(null, "- Select One -");
			// int i = 0;
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			selectItem.setValue(null);
			ministryList.add(selectItem);
			for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(orgProfile.getOrgName());
				selectItem1.setValue(orgProfile.getOrgProfileId());
				ministryList.add(selectItem1);
				/*
				 * items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
				 * orgProfile.getOrgName());
				 */
			}
		} finally {
			orgProfileSO = null;
		}
		return items;
	}

	/**
	 * getLtstMinistryListByUserid - Gets the latest ministry list by userid.
	 * 
	 * @return the lministryList selectItem by userid
	 */
	public SelectItem[] getLtstMinistryListByUserid() {
		log.info("ReportBackingBean-getLtstMinistryListByUserid");
		SelectItem[] items = null;

		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(lngUserId);
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			items = new SelectItem[resDTO.getOrgProfileList().size() + 1];
			// items[0] = new SelectItem(null, "- Select One -");
			// int i = 0;
			/*
			 * SelectItem selectItem = new SelectItem();
			 * selectItem.setLabel("- Select One -"); selectItem.setValue(null);
			 * ministryList.add(selectItem);
			 */
			for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(orgProfile.getOrgName());
				selectItem1.setValue(orgProfile.getOrgProfileId());
				ministryList.add(selectItem1);
				/*
				 * items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
				 * orgProfile.getOrgName());
				 */
			}
		} finally {
			orgProfileSO = null;
		}
		return items;
	}

	/**
	 * retrievePtjListByMinistryId - Retrieve PTJ list by ministry id.
	 */
	public void retrievePtjListByMinistryId() {
		log.info("ReportBackingBean-retrievePtjListByMinistryId");
		pmPtjList = new ArrayList<String>();
		if (ministryId != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(ministryId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> orgProfileList = resDTO.getOrgProfileList();
			for (PmOrgProfile ptj : orgProfileList) {
				ptj.getOrgName();
				pmPtjList.add(ptj.getOrgName());
			}
			/*
			 * reqDTO.setParentOrgProfileId(ministryId);
			 * //reqDTO.setParentOrgProfileId((long) 231618); OrgProfileResDTO
			 * resDTO = orgProfileSO .retrievePtjListByMinistryId(reqDTO);
			 * List<PmOrgProfile> orgProfileList =resDTO.getOrgProfileList();
			 * for (PmOrgProfile ptj : orgProfileList) { ptj.getOrgName(); }
			 */
		}
	}

	/**
	 * getDocTypeStatusDropDown - Gets the document type status dropdown.
	 * 
	 * @return the docTypeStatusItems
	 */
	public SelectItem[] getDocTypeStatusDropDown() {
		log.info("ReportBackingBean-getDocTypeStatusDropDown");
		docTypeStatusItems = null;
		String doctype = null;
		SelectItem[] tempOneStatusItems = null;
		SelectItem[] tempTwoStatusItems = null;
		docTypeStatusItemsList = new ArrayList<SelectItem>();
		if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.CON_REQ_RPT_MIN_VAL) || this
						.getReportValue().equals(
								ReportConst.CON_REQ_RPT_CEN_PEN_VAL))) {
			doctype = ReportConst.DOC_TYPE_CR;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.CON_ORD_RPT_MIN_VAL)
						|| this.getReportValue().equals(
								ReportConst.CON_ORD_RPT_CEN_PEN_VAL)
						|| this.getReportValue().equals(
								ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL) || this
						.getReportValue().equals(
								ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL))) {
			doctype = ReportConst.DOC_TYPE_CO;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.DEL_ORD_RPT_MIN_VAL)
						|| this.getReportValue().equals(
								ReportConst.DEL_ORD_RPT_CEN_PEN_VAL) || this
						.getReportValue()
						.equals(ReportConst.DEL_ORD_RPT_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_DO;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.FL_REC_NOTE_RPT_MIN_VAL) || this
						.getReportValue().equals(
								ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL))
				|| (this.getReportValue() != null)
				&& (this.getReportValue()
						.equals(ReportConst.FL_REC_NOTE_RPT_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_FN;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL)
						|| this.getReportValue().equals(
								ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL) || this
						.getReportValue().equals(
								ReportConst.INV_RPT_VIEW_SUPL_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_IN;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.PYMT_MATCH_RPT_MIN_VAL)
						|| this.getReportValue().equals(
								ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL) || this
						.getReportValue().equals(
								ReportConst.PYMT_MATCH_RPT_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_PA;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(ReportConst.DED_RPT_MIN_VAL))) {
			doctype = ReportConst.DOC_TYPE_DN;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue().equals(
						ReportConst.SUMM_RPT_PAN_ITM_VAL)
						|| this.getReportValue().equals(
								ReportConst.PUR_ORD_RPT_PO_VAL) || this
						.getReportValue().equals(
								ReportConst.INV_RPT_VIEW_PTJ_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_PO;
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue()
						.equals(ReportConst.PUR_REQ_RPT_PO_VAL))) {
			doctype = ReportConst.DOC_TYPE_PR;
			/*
			 * }else
			 * if((this.getReportValue()!=null)&&(this.getReportValue().equals
			 * (ReportConst.FL_REC_NOTE_RPT_PO_VAL))){ doctype
			 * =ReportConst.DOC_TYPE_FRN;
			 */
		} else if ((this.getReportValue() != null)
				&& (this.getReportValue()
						.equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))) {
			docTypeStatusItemsList = new ArrayList<SelectItem>();
			tempOneStatusItems = getStatusDropdownValues(this.getModuleValue(),
					ReportConst.DOC_TYPE_PO);
			tempTwoStatusItems = getStatusDropdownValues(this.getModuleValue(),
					ReportConst.DOC_TYPE_PR);
			for (SelectItem item1 : tempOneStatusItems) {
				docTypeStatusItemsList.add(item1);
			}
			for (SelectItem item2 : tempTwoStatusItems) {
				if ((!item2.getLabel().equals(
						ResourceBundleUtils.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())))) {
					docTypeStatusItemsList.add(item2);
				}
			}
		}
		if (null != doctype) {
			docTypeStatusItems = null;
			docTypeStatusItemsList = new ArrayList<SelectItem>();
			docTypeStatusItems = getStatusDropdownValues(this.getModuleValue(),
					doctype);
			for (SelectItem itemCommon : docTypeStatusItems) {
				docTypeStatusItemsList.add(itemCommon);
			}
		}
		return docTypeStatusItems;
	}

	/**
	 * retrieveSubTypeList - Retrieve sub type list.
	 * 
	 * @return the subTypeList
	 */
	public List<SelectItem> retrieveSubTypeList() {
		log.info("ReportBackingBean-retrieveSubTypeList");
		ctSubTypeList = new ArrayList<SelectItem>();
		getManagedBean().getRptGenBean().setCtSubTypeView(false);
		getManagedBean().getRptGenBean().setCtSubTypeApproverView(false);
		getManagedBean().getRptGenBean().setCtSubTypeMCAView(false);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			if (null != this.getCtUserType()) {
				String userTypeRoleCode = null;
				if (this.getCtUserType().equalsIgnoreCase(
						ReportConst.MINISTRY_CONTRACT_ADMIN)) {
					userTypeRoleCode = ReportConst.MIN_CONTRACT_ADMIN;
					getManagedBean().getRptGenBean().setCtSubTypeView(true);
					getManagedBean().getRptGenBean().setCtSubTypeApproverView(
							false);
					getManagedBean().getRptGenBean().setCtSubTypeMCAView(true);
				} else if (this.getCtUserType().equalsIgnoreCase(
						ReportConst.APPROVER_LBL)) {
					userTypeRoleCode = ReportConst.ROLE_CODE_CT_APPROVER;
					getManagedBean().getRptGenBean().setCtSubTypeView(true);
					getManagedBean().getRptGenBean().setCtSubTypeMCAView(false);
					getManagedBean().getRptGenBean().setCtSubTypeApproverView(
							true);
				}
				if (null != userTypeRoleCode) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<Object[]> ptLst = reportSO.retrieveSubTypeList(
							lngUserId, userTypeRoleCode);
					for (Object[] object : ptLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						ctSubTypeList.add(selectItem);
					}
				}
			}
		}
		return ctSubTypeList;
	}

	/**
	 * retrieveZoneByUser - Retrieve zone by user.
	 * 
	 * @return the zoneList
	 */
	public List<SelectItem> retrieveZoneByUser() {
		log.info("ReportBackingBean-retrieveZoneByUser");
		zoneList = new ArrayList<SelectItem>();
		this.setStringZone(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {

			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> zoneLst = reportSO.retrieveZoneByUser(lngUserId);
			for (Object[] object : zoneLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0]);
				selectItem.setLabel(object[1].toString());
				stringZone = object[1].toString();
				zoneList.add(selectItem);
			}
		}
		return zoneList;
	}

	/**
	 * retrieveHighRoleCodeByUser - Retrieve high role code by user.
	 * 
	 * @return the highRoleCodeList
	 */
	public List<String> retrieveHighRoleCodeByUser() {
		log.info("ReportBackingBean-retrieveHighRoleCodeByUser");
		highRoleCodeList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(lngUserId);
			for (int i = 0; i < ptLst.size(); i++) {
				highRoleCodeList.add(ptLst.get(i));
			}
		}
		return highRoleCodeList;
	}

	/**
	 * directPurchaseWithRequisitioner - Direct purchase with requisitioner.
	 */
	public void directPurchaseWithRequisitioner() {
		log.info("ReportBackingBean-directPurchaseWithRequisitioner");
		if ((this.getReportValue() != null)
				&& (this.getReportValue()
						.equals(ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL))) {
			getManagedBean().getRptGenBean()
					.setScSimpleQuoteDtlWithoutRequiGroupView(false);
			getManagedBean().getRptGenBean()
					.setScSimpleQuoteDtlGroupView(false);
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_REQUISITIONER))) {
				getManagedBean().getRptGenBean()
						.setScSimpleQuoteDtlWithoutRequiGroupView(true);
			} else {
				getManagedBean().getRptGenBean().setScSimpleQuoteDtlGroupView(
						true);
			}
		}
	}

	/**
	 * roleCodeUsersSetupOnload - Role code users setup onload based on PtjId.
	 * 
	 * @param lngPtjId
	 *            the lng ptj id
	 */
	public void roleCodeUsersSetupOnload(Long lngPtjId) {
		log.info("ReportBackingBean-roleCodeUsersSetupOnload");
		rolecodeRquisitionerList = new ArrayList<SelectItem>();
		rolecodeApproverList = new ArrayList<SelectItem>();
		rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_REQUISITIONER))) {
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_REQUISITIONER);
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			} else {
				if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.CON_REQ_RPT_MIN_VAL)
								|| this.getReportValue().equals(
										ReportConst.CON_REQ_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.PUR_REQ_RPT_PO_VAL) || this
								.getReportValue().equals(
										ReportConst.EMR_PROCUR_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_REQUISITIONER,
									lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRquisitionerList.add(selectItem);
					}
				} else if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.FL_REC_NOTE_RPT_MIN_VAL)
								|| this.getReportValue()
										.equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.LATE_DEL_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.LATE_DEL_RPT_MIN_VAL) || this
								.getReportValue().equals(
										ReportConst.FL_REC_NOTE_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_RECEIVING_OFFICER,
									lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRecievingOfficerList.add(selectItem);
					}
				} else if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.CON_ORD_RPT_MIN_VAL)
								|| this.getReportValue().equals(
										ReportConst.CON_ORD_RPT_CEN_PEN_VAL) || this
								.getReportValue().equals(
										ReportConst.PUR_ORD_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_FL_APPROVER, lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeApproverList.add(selectItem);
					}
				}
			}
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_APPROVER)
							|| highRoleCodeList
									.contains(ReportConst.ROLE_CODE_CT_APPROVER) || highRoleCodeList
								.contains(ReportConst.ROLE_CODE_RN_APPROVER))) {
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								highRoleCodeList.get(0));
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeApproverList.add(selectItem);
				}
			}
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER))) {
				List<Object[]> recoffLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_RECEIVING_OFFICER);
				for (Object[] object : recoffLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRecievingOfficerList.add(selectItem);
				}
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_REQUISITIONER);
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			}
		}
	}

	/**
	 * roleCodeUsersSetup - Role code users setup will be done by user id.
	 */
	public void roleCodeUsersSetup() {
		log.info("ReportBackingBean-roleCodeUsersSetup");
		rolecodeRquisitionerList = new ArrayList<SelectItem>();
		rolecodeApproverList = new ArrayList<SelectItem>();
		rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			/*
			 * if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst
			 * .
			 * ROLE_CODE_MIN_CONTRACT_ADMIN)||highRoleCodeList.contains(ReportConst
			 * .ROLE_CODE_FL_APPROVER)||highRoleCodeList.contains(ReportConst.
			 * ROLE_CODE_BPK_CCA
			 * )||highRoleCodeList.contains(ReportConst.ROLE_CODE_BPK_PCA))){
			 * //List<String> ptLst =
			 * reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId);
			 * if((this
			 * .getReportValue()!=null)&&(this.getReportValue().equals(ReportConst
			 * .CON_REQ_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.
			 * CON_REQ_RPT_CEN_PEN_VAL
			 * )||this.getReportValue().equals(ReportConst
			 * .PUR_REQ_RPT_PO_VAL)||this
			 * .getReportValue().equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))){
			 * List<Object[]> rlcdLst =
			 * reportSO.retrieveUserListByAdminOrBKPRoleCode
			 * (lngUserId,ReportConst.ROLE_CODE_REQUISITIONER); for (Object[]
			 * object : rlcdLst) { SelectItem selectItem = new SelectItem();
			 * selectItem.setLabel(object[0].toString());
			 * selectItem.setValue(object[1]);
			 * rolecodeRquisitionerList.add(selectItem); } }else
			 * if((this.getReportValue
			 * ()!=null)&&(this.getReportValue().equals(ReportConst
			 * .FL_REC_NOTE_RPT_MIN_VAL
			 * )||this.getReportValue().equals(ReportConst
			 * .FL_REC_NOTE_RPT_CEN_PEN_VAL
			 * )||this.getReportValue().equals(ReportConst
			 * .LATE_DEL_RPT_CEN_PEN_VAL
			 * )||this.getReportValue().equals(ReportConst
			 * .LATE_DEL_RPT_MIN_VAL)||
			 * this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_PO_VAL
			 * ))){ List<Object[]> rlcdLst =
			 * reportSO.retrieveUserListByAdminOrBKPRoleCode
			 * (lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER); for
			 * (Object[] object : rlcdLst) { SelectItem selectItem = new
			 * SelectItem(); selectItem.setLabel(object[0].toString());
			 * selectItem.setValue(object[1]);
			 * rolecodeRecievingOfficerList.add(selectItem); } }else
			 * if((this.getReportValue
			 * ()!=null)&&(this.getReportValue().equals(ReportConst
			 * .CON_ORD_RPT_MIN_VAL
			 * )||this.getReportValue().equals(ReportConst.CON_ORD_RPT_CEN_PEN_VAL
			 * )
			 * ||this.getReportValue().equals(ReportConst.PUR_ORD_RPT_PO_VAL))){
			 * List<Object[]> rlcdLst =
			 * reportSO.retrieveUserListByAdminOrBKPRoleCode
			 * (lngUserId,ReportConst.ROLE_CODE_FL_APPROVER); for (Object[]
			 * object : rlcdLst) { SelectItem selectItem = new SelectItem();
			 * selectItem.setLabel(object[0].toString());
			 * selectItem.setValue(object[1]);
			 * rolecodeApproverList.add(selectItem); } } }else
			 */if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_REQUISITIONER))) {
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_REQUISITIONER);
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			} else {
				Long lngPtjId = null;
				if (this.getCommonPtj() != null) {
					lngPtjId = Long.parseLong(this.getCommonPtj());
				}
				// List<String> ptLst =
				// reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId);
				if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.CON_REQ_RPT_MIN_VAL)
								|| this.getReportValue().equals(
										ReportConst.CON_REQ_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.PUR_REQ_RPT_PO_VAL) || this
								.getReportValue().equals(
										ReportConst.EMR_PROCUR_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_REQUISITIONER,
									lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRquisitionerList.add(selectItem);
					}
				} else if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.FL_REC_NOTE_RPT_MIN_VAL)
								|| this.getReportValue()
										.equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.LATE_DEL_RPT_CEN_PEN_VAL)
								|| this.getReportValue().equals(
										ReportConst.LATE_DEL_RPT_MIN_VAL) || this
								.getReportValue().equals(
										ReportConst.FL_REC_NOTE_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_RECEIVING_OFFICER,
									lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRecievingOfficerList.add(selectItem);
					}
				} else if ((this.getReportValue() != null)
						&& (this.getReportValue().equals(
								ReportConst.CON_ORD_RPT_MIN_VAL)
								|| this.getReportValue().equals(
										ReportConst.CON_ORD_RPT_CEN_PEN_VAL) || this
								.getReportValue().equals(
										ReportConst.PUR_ORD_RPT_PO_VAL))) {
					List<Object[]> rlcdLst = reportSO
							.retrieveUserListByAdminOrBKPRoleCode(lngUserId,
									ReportConst.ROLE_CODE_FL_APPROVER, lngPtjId);
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeApproverList.add(selectItem);
					}
				}
			}
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_APPROVER)
							|| highRoleCodeList
									.contains(ReportConst.ROLE_CODE_CT_APPROVER) || highRoleCodeList
								.contains(ReportConst.ROLE_CODE_RN_APPROVER))) {
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								highRoleCodeList.get(0));
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeApproverList.add(selectItem);
				}
			}
			if ((highRoleCodeList != null)
					&& (highRoleCodeList
							.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER))) {
				List<Object[]> recoffLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_RECEIVING_OFFICER);
				for (Object[] object : recoffLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRecievingOfficerList.add(selectItem);
				}
				List<Object[]> rlcdLst = reportSO
						.retrieveUserListByREQUISITIONER(lngUserId,
								ReportConst.ROLE_CODE_REQUISITIONER);
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			}
		}
	}

	/**
	 * retrieveAllRoleCodesByUser - Retrieve all role codes by user.
	 * 
	 * @return the allRoleCodesList
	 */
	public List<String> retrieveAllRoleCodesByUser() {
		log.info("ReportBackingBean-retrieveAllRoleCodesByUser");
		allRoleCodesList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> roleCodesLst = reportSO
					.retrieveAllRoleCodesByUser(lngUserId);
			for (int i = 0; i < roleCodesLst.size(); i++) {
				allRoleCodesList.add(roleCodesLst.get(i));
			}
		}
		return allRoleCodesList;
	}

	/**
	 * zoneByRolecode - will retrieve Zone by rolecode.
	 */
	public void zoneByRolecode() {
		log.info("ReportBackingBean-zoneByRolecode");
		if ((this.getModuleValue()
				.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
			getManagedBean().getRptGenBean().setSmZoneLabelView(false);
			getManagedBean().getRptGenBean().setSmZoneView(false);
			getManagedBean().getRptGenBean().setSmMandatoryZoneView(false);
			if ((null != this.getReportValue())
					&& (this.getReportValue().equals(
							ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_SUPPL_STAT_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_QUESTIONAIRE_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_BPK_USER_PROFILE_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_DELETE_PENDING_APPL)
							|| this.getReportValue().equals(
									ReportConst.SM_PERSON_BRANCH_BANK)
							|| this.getReportValue().equals(
									ReportConst.SM_THRESHOLD)
							|| this.getReportValue().equals(
									ReportConst.SM_LST_MONTH_WISE_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_APPLICATION_APPROVED_RPT_VAL)
							|| this.getReportValue()
									.equals(ReportConst.SM_APPLICATION_REJECTED_RPT_VAL)
							|| this.getReportValue().equals(
									ReportConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER) || this
							.getReportValue().equals(
									ReportConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL))) {

				if ((allRoleCodesList != null)
						&& (allRoleCodesList
								.contains(ReportConst.ROLE_CODE_UPS_PO)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_UPS_APPROVER)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_UPS_ADMIN)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_UPS_SVO)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_UPS_SVC) || allRoleCodesList
									.contains(ReportConst.ROLE_CODE_UPS_SDO))) {
					// if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK))){
					this.setSmZone(this.getStringZone());
					getManagedBean().getRptGenBean().setSmZoneLabelView(true);
					// }

				} else if ((allRoleCodesList != null)
						&& (allRoleCodesList
								.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_ADMIN)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_USER)
								|| allRoleCodesList
										.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_USER) || allRoleCodesList
									.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_ADMIN))) {
					if ((this.getReportValue() != null)
							&& (this.getReportValue().equals(
									ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL)
									|| this.getReportValue()
											.equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)
									|| this.getReportValue()
											.equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL)
									|| this.getReportValue()
											.equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL) || this
									.getReportValue().equals(
											ReportConst.SM_PERSON_BRANCH_BANK))) {
						rptGenBean.setSmMandatorySupplierTypeView(false);
						rptGenBean.setSmSupplierTypeView(false);
					}
				} else {
					// if((allRoleCodesList!=null)&&(allRoleCodesList.contains(ReportConst.ROLE_CODE_EP_ADMIN)||allRoleCodesList.contains(ReportConst.ROLE_CODE_PTJ_ADMIN))){
					// if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK))){
					getManagedBean().getRptGenBean().setSmZoneView(true);
					getManagedBean().getRptGenBean().setSmMandatoryZoneView(
							true);
					// }

				}
			}
		}
	}

	/**
	 * retrieveParamCodeByUser - will retrieve param code by user.
	 * 
	 * @return the paramList
	 */
	public List<String> retrieveParamCodeByUser() {
		log.info("ReportBackingBean-retrieveParamCodeByUser");
		paramList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> ptLst = reportSO.retrieveParamCodeByUser(lngUserId);
			for (int i = 0; i < ptLst.size(); i++) {
				paramList.add(ptLst.get(i));
			}
		}
		return paramList;
	}

	/**
	 * assignNewCompanyExperience - Assign new company experience of categories
	 * based on category level type.
	 * 
	 * @param catList
	 *            the cat list
	 */
	public void assignNewCompanyExperience(List<SmSupplierCategory> catList) {
		log.info("ReportBackingBean-assignNewCompanyExperience");
		if (catList == null) {
			catList = new ArrayList<SmSupplierCategory>();
		}
		SmSupplierCategory cat = new SmSupplierCategory();
		// cat.setPmCategoryL1(catOneList);
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		catList.add(cat);
		this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
		this.setSelectedCatList(catList);
		resetChildCatList();
		assignValReqdToTrue();
	}

	/**
	 * assignValReqdToTrue - will assign value required to true.
	 */
	private void assignValReqdToTrue() {
		log.info("ReportBackingBean-assignValReqdToTrue");
		this.setShowParentMsg(false);
		this.setValueReqd(true);
	}

	/**
	 * resetChildCatList - Reset child category list.
	 */
	private void resetChildCatList() {
		log.info("ReportBackingBean-resetChildCatList");
		this.setSupplierChildCatList(new ArrayList<PmCategoryL2>());
		this.setSupplierSubChildCatList(new ArrayList<PmCategoryL3>());
	}

	/**
	 * retrieveParentCategoryListForExperiencePage - Retrieve parent category
	 * list for experience page.
	 * 
	 * @return the CategoryL1List
	 */
	public static List<PmCategoryL1> retrieveParentCategoryListForExperiencePage() {
		log.info("ReportBackingBean-retrieveParentCategoryListForExperiencePage");
		// CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = reqSO.retrieveCategoryL1List();
		if (catCodeResDTO != null) {
			return catCodeResDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	/**
	 * Select all categories.
	 */
	public void selectAll() {
		log.info("ReportBackingBean-selectAll");
		if (this.isSelectedAll()) {
			this.setSelected(true);
			this.getSelectedCatList();
		} else {
			this.setSelected(false);
			this.setSelectedCatList(null);
		}
	}

	/**
	 * selected- Selected category based on selected index.
	 * 
	 * @param index
	 *            the index
	 */
	public void selected(int index) {
		log.info("ReportBackingBean-selected");
		List<SmSupplierCategory> selectedCatsList = this.getSelectedCatList();
		if (this.isSelected()) {
			if (selectedCatsList == null) {
				selectedCatsList = new ArrayList<SmSupplierCategory>();
			}
			selectedCatsList.add(this.getSelectedCatList().get(index));
		} else {

			if (selectedCatsList.contains(this.getSelectedCatList().get(index))) {
				selectedCatsList.remove(this.getSelectedCatList().get(index));
			}
		}
		this.setSelectedCatList(selectedCatsList);
	}

	/**
	 * selectAllCompanyExperinceCategories - will select for all company
	 * experince categories.
	 */
	public void selectAllCompanyExperinceCategories() {
		log.info("ReportBackingBean-selectAllCompanyExperinceCategories");
		if (this.isSelectedAll()) {
			this.setSelected(true);
			this.setSelectedCatList(this.getSelectedCatList());
		} else {
			this.setSelected(false);
			this.setSelectedCatList(null);
		}
	}

	/**
	 * selectedCompanyExperinceCategory - Selected for perticular company
	 * experince category.
	 * 
	 * @param index
	 *            the index
	 */
	public void selectedCompanyExperinceCategory(int index) {
		log.info("ReportBackingBean-selectedCompanyExperinceCategory");
		List<SmSupplierCategory> selectedCatsList = this.getSelectedCatList();
		if (this.isSelected()) {
			if (selectedCatsList == null) {
				selectedCatsList = new ArrayList<SmSupplierCategory>();
			}
			selectedCatsList.add(this.getSelectedCatList().get(index));
		} else {
			if (selectedCatsList.contains(this.getSelectedCatList().get(index))) {
				selectedCatsList.remove(this.getSelectedCatList().get(index));
			}
		}
		this.setSelectedCatList(selectedCatsList);
	}

	/**
	 * populateExperienceChildCategoryByParent - will populate experience child
	 * category by parent.
	 * 
	 * @param parentId
	 *            the parent id
	 * @param catL2
	 *            the cat l2
	 * @param catL3
	 *            the cat l3
	 */
	public void populateExperienceChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		log.info("ReportBackingBean-populateExperienceChildCategoryByParent");
		try {
			List<PmCategoryL2> categoryL2s = retrieveExperienceChildCategoryList(parentId);
			catL2.setPmCategoryL2s(categoryL2s);
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * retrieveExperienceChildCategoryList - Retrieve experience child category
	 * list.
	 * 
	 * @param categoryL1Id
	 *            the category l1 id
	 * @return the categoryL2List
	 */
	public static List<PmCategoryL2> retrieveExperienceChildCategoryList(
			long categoryL1Id) {
		log.info("ReportBackingBean-retrieveExperienceChildCategoryList");
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);
		return resDTO.getCategoryL2List();
	}

	/**
	 * retrieveReportListByAuthorization - Retrieve report list by user
	 * authorization.
	 * 
	 * @return the reportList
	 */
	public List<SelectItem> retrieveReportListByAuthorization() {
		log.info("ReportBackingBean-retrieveReportListByAuthorization");
		reportList = new ArrayList<SelectItem>();
		rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		rptGenBean = new ReportsGenerateBean();
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		rptGenBean.setReportCategoryView(false);
		this.setReportCategory(null);
		this.setReportValue(null);
		List<Object[]> reportModList = null;
		/*
		 * String language =
		 * ResourceBundleUtils.getMessage(ReportConst.LABEL_LANG,
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()); //String language = "ms";
		 */reportList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			if ((this.getModuleValue() != null)
					&& (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.FL_MOD_VAL))) {
				rptGenBean.setReportCategoryView(true);
				retrieveSubModuleReportCategoryList(strLanguage);
			} else {
				if ((strLanguage != null)
						&& (strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG))) {
					reportModList = reportSO
							.retrieveReportListByModuleNUserAuthorization(
									lngUserId, this.getModuleValue());
				} else if ((strLanguage != null)
						&& (strLanguage
								.equalsIgnoreCase(ReportConst.MALAY_LANG))) {
					reportModList = reportSO
							.retrieveMalayReportListByModuleNUserAuthorization(
									lngUserId, this.getModuleValue());
				}
				for (Object[] object : reportModList) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1].toString());
					reportList.add(selectItem);
				}
			}
		}
		return reportList;
	}

	/**
	 * retrieveSubModuleReportCategoryList - Retrieve sub module report category
	 * list based on module and user authorization.
	 * 
	 * @param language
	 *            the language
	 * @return the reportCategoryList
	 */
	public List<SelectItem> retrieveSubModuleReportCategoryList(String language) {
		log.info("ReportBackingBean-retrieveSubModuleReportCategoryList");
		reportList = new ArrayList<SelectItem>();
		reportCategoryList = new ArrayList<SelectItem>();
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		resetReportForm();
		this.setReportValue(null);
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> reportCatList = reportSO
					.retrieveSubModuleReportCategoryList(language,
							this.getModuleValue(), lngUserId);
			/*
			 * for (int i = 0; i < reportCatList.size(); i++) {
			 * reportCategoryList.add(reportCatList.get(i)); }
			 */
			for (Object[] object : reportCatList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				reportCategoryList.add(selectItem);
			}
		}
		return reportCategoryList;
	}

	/**
	 * retrieveSimpleQuoteNo - Retrieve simple quote no.
	 * 
	 * @return the simpleQuoteNoList
	 */
	public List<String> retrieveSimpleQuoteNo() {
		log.info("ReportBackingBean-retrieveSimpleQuoteNo");
		simpleQuoteNoList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> smplQtNoList = reportSO.retrieveSimpleQuoteNo(this
					.getScProcessRptQuotationNo());
			if (smplQtNoList.size() > 0) {
				for (int i = 0; i < smplQtNoList.size(); i++) {
					simpleQuoteNoList.add(smplQtNoList.get(i));
				}
			} else {
				this.setScProcessRptQuotationNo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780021);
			}
		}
		return simpleQuoteNoList;
	}

	/**
	 * retrieveReportListBySubModule - Retrieve report list by sub module and
	 * user authorization.
	 * 
	 * @return the reportList
	 */
	public List<SelectItem> retrieveReportListBySubModule() {
		log.info("ReportBackingBean-retrieveReportListBySubModule");
		reportList = new ArrayList<SelectItem>();
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		resetReportForm();
		this.setReportValue(null);
		if (rptGenBean.isReportCategoryView() == true
				|| (this.getModuleValue().equalsIgnoreCase(
						ModuleCodeConst.ORDER_FULFILLMENT) && this
						.getReportCategory() != null)) {
			rptGenBean = new ReportsGenerateBean();
			rptGenBean.setReportCategoryView(true);
			// this.setReportCategoryView(true);
		} else {
			rptGenBean = new ReportsGenerateBean();
			// rptGenBean.setReportCategoryView(this.isReportCategoryView());
		}
		getManagedBean().setRptGenBean(null);
		this.setSubModule(null);
		this.setReportName(ReportConst.EMPTY_VAL);
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		/*
		 * String language =
		 * ResourceBundleUtils.getMessage(ReportConst.LABEL_LANG,
		 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
		 * FacesScopes().getUserLocale()); //String language = "ms";
		 */
		List<Object[]> reportModList = null;
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			if ((strLanguage != null)
					&& (strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG))) {
				reportModList = reportSO.retrieveReportListBySubModule(
						this.getModuleValue(), this.getReportCategory(),
						lngUserId);
			} else if ((strLanguage != null)
					&& (strLanguage.equalsIgnoreCase(ReportConst.MALAY_LANG))) {
				reportModList = reportSO.retrieveMalayReportListBySubModule(
						this.getModuleValue(), this.getReportCategory(),
						lngUserId);
			}
			// List<Object[]> reportModList =
			// reportSO.retrieveReportListBySubModule(this.getModuleValue(),this.getReportCategory(),lngUserId);
			for (Object[] object : reportModList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				reportList.add(selectItem);
			}
		}
		return reportList;
	}

	/**
	 * getReportIdlist - Gets the report id list based on paramModule type =
	 * "REP" .
	 * 
	 * @return the report idlist
	 */
	public void getReportIdlist() {
		log.info("ReportBackingBean-getReportIdlist");
		rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
		String paramModule = null;
		items = null;
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			if ((this.getModuleValue()) != null) {
				paramModule = "REP";
			}
			items = super.getRadioButtonCodeValuesBySearchBean(paramModule,
					this.getReportSearchBean());
			processItemsByModule(items);
		}
	}

	/*
	 * public void getAllModulelist() { rptGenBean = new ReportsGenerateBean();
	 * this.setReportValue(null); String paramModule = null; items = null; if
	 * (null != new FacesScopes().getUserSessionBean().getUserId()) { if
	 * ((this.getModuleValue()) != null){ paramModule="MOD"; }
	 * items=super.getRadioButtonCodeValuesBySearchBean
	 * (paramModule,this.getReportSearchBean()); //processItemsByModule(items);
	 * } }
	 */
	/**
	 * processItemsByModule - Process items by module.
	 * 
	 * @param items
	 *            the items
	 */
	private void processItemsByModule(SelectItem[] items) {
		log.info("ReportBackingBean-processItemsByModule");
		Map<String, List<SelectItem>> localModuleItemMap = new HashMap<String, List<SelectItem>>();
		if (null != items) {
			for (SelectItem item : items) {
				String[] moduleItemArr = (String[]) ((String) item.getValue())
						.split(ReportConst.UNDERSCORE);
				if (localModuleItemMap.keySet().contains(moduleItemArr[0])) {
					// code for contains
					localModuleItemMap.get(moduleItemArr[0]).add(item);
				} else {
					// not contains
					List<SelectItem> moduleItems = new ArrayList<SelectItem>();
					moduleItems.add(item);
					localModuleItemMap.put(moduleItemArr[0], moduleItems);
				}
			}
		}
		this.setReportList(localModuleItemMap.get(this.getModuleValue()));
		setModuleItemMap(localModuleItemMap);
	}

	/**
	 * Gets the report search bean based on moduleName .
	 * 
	 * @return the report search bean
	 */
	public ISearchBean<PmParameterDesc> getReportSearchBean() {
		return super.createSearchBean(PmParameterDesc.class, "moduleName");
	}

	/**
	 * populateExperienceSubChildCategoryByChild - Populate experience sub child
	 * category by child.
	 * 
	 * @param childId
	 *            the child id
	 * @param catL3
	 *            the cat l3
	 */
	public void populateExperienceSubChildCategoryByChild(long childId,
			PmCategoryL3 catL3) {
		log.info("ReportBackingBean-populateExperienceSubChildCategoryByChild");
		try {
			List<PmCategoryL3> categoryL3s = retrieveExperienceSubChildCategoryList(childId);
			catL3.setPmCategoryL3s(categoryL3s);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * retrieveExperienceSubChildCategoryList - Retrieve experience sub child
	 * category list.
	 * 
	 * @param categoryL2Id
	 *            the category l2 id
	 * @return the categoryL3List
	 */
	public static List<PmCategoryL3> retrieveExperienceSubChildCategoryList(
			long categoryL2Id) {
		log.info("ReportBackingBean-retrieveExperienceSubChildCategoryList");
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);
		return resDTO.getCategoryL3List();
	}

	/**
	 * checkDuplicateCategoryForProject - Check duplicate category for project.
	 * 
	 * @param newCat
	 *            the new category
	 */
	public void checkDuplicateCategoryForProject(SmSupplierCategory newCat) {
		for (SmSupplierCategory cat : this.getSelectedCatList()) {
			log.info("ReportBackingBean-checkDuplicateCategoryForProject");
			if ((cat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (newCat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (cat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (newCat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (cat.getPmCategoryL3().getCategoryL3Id() != null)
					&& (newCat.getPmCategoryL3().getCategoryL3Id() != null)) {
				if (newCat.equals(cat)) {
					log.info(" *** Same Obj ****"
							+ cat.getPmCategoryL1().getCategoryL1Code()
							+ " -- --"
							+ newCat.getPmCategoryL1().getCategoryL1Code());

				} else if (cat.getPmCategoryL1().getCategoryL1Id()
						.equals(newCat.getPmCategoryL1().getCategoryL1Id())
						&& cat.getPmCategoryL2()
								.getCategoryL2Id()
								.equals(newCat.getPmCategoryL2()
										.getCategoryL2Id())
						&& cat.getPmCategoryL3()
								.getCategoryL3Id()
								.equals(newCat.getPmCategoryL3()
										.getCategoryL3Id())) {
					newCat.getPmCategoryL3().setCategoryL3Id(null);
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}
	}

	/**
	 * deleteSelectedCodes - Delete selected codes.
	 */
	public void deleteSelectedCodes() {
		log.info("ReportBackingBean-deleteSelectedCodes");
		for (SmSupplierCategory cat : this.getSelectCatDeletionList()) {
			this.getSelectedCatList().remove(cat);
		}
		if ((this.getSelectedCatList().size() > 9)) {
			this.setEnableValue(true);
			// throw new EpException(MessageConst.ERROR_REPORT_780010);
		} else {
			this.setEnableValue(false);
		}
	}

	/**
	 * assignCategoryForCompExperience - Assign category for comp experience.
	 */
	public void assignCategoryForCompExperience() {
		log.info("ReportBackingBean-assignCategoryForCompExperience");
		SmSupplierCategory cat = new SmSupplierCategory();
		if ((this.getSelectedCatList().size() > 8)) {
			this.setEnableValue(true);
			// throw new EpException(MessageConst.ERROR_REPORT_780010);
		} else {
			this.setEnableValue(false);
		}
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		// cat.setSmCompanyExperience(exp);
		this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
		resetChildCatList();
		assignValReqdToTrue();
		this.getSelectedCatList().add(cat);
	}

	/**
	 * retriveModuleList - Retrive module list.
	 * 
	 * @return the moduleList
	 */
	public List<SelectItem> retriveModuleList() {
		log.info("ReportBackingBean-retriveModuleList");
		moduleList = new ArrayList<SelectItem>();
		SelectItem selectItem = new SelectItem();
		selectItem.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_SC_TTL));
		selectItem.setValue(ReportConst.SC_MOD_VAL);
		moduleList.add(selectItem);
		SelectItem selectItem5 = new SelectItem();
		selectItem5.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_DP_TTL));
		selectItem5.setValue(ReportConst.DP_MOD_VAL);
		moduleList.add(selectItem5);
		SelectItem selectItem1 = new SelectItem();
		selectItem1.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_CM_TTL));
		selectItem1.setValue(ReportConst.CM_MOD_VAL);
		moduleList.add(selectItem1);
		SelectItem selectItem2 = new SelectItem();
		selectItem2.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_FL_TTL));
		selectItem2.setValue(ReportConst.FL_MOD_VAL);
		moduleList.add(selectItem2);
		SelectItem selectItem3 = new SelectItem();
		selectItem3.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_SM_TTL));
		selectItem3.setValue(ReportConst.SM_MOD_VAL);
		moduleList.add(selectItem3);
		SelectItem selectItem4 = new SelectItem();
		selectItem4.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_CT_TTL));
		selectItem4.setValue(ReportConst.CT_MOD_VAL);
		moduleList.add(selectItem4);
		SelectItem selectItem6 = new SelectItem();
		selectItem6.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_PP_TTL));
		selectItem6.setValue(ReportConst.PP_MOD_VAL);
		moduleList.add(selectItem6);

		// getAllModulelist();
		return moduleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#
	 * preRenderView()
	 */
	/**
	 * Pre render view.
	 */
	public void preRenderView() {
		log.info("ReportBackingBean-preRenderView");
		rptGenBean = new ReportsGenerateBean();
		getManagedBean().setRptGenBean(rptGenBean);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setReportValue(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ReportConst.REPORT_PAGE);
	}

	/**
	 * retriveReportListBymoduleName - Retrieve report list by module name.
	 */
	public void retriveReportListBymoduleName() {
		log.info("ReportBackingBean-retriveReportListBymoduleName");
		reportList = new ArrayList<SelectItem>();
		rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();

		if (this.getModuleValue() != null) {
			retriveReportListByModule(this.getModuleValue());
		}
		getManagedBean().setReportList(reportList);
		getManagedBean().setRptGenBean(rptGenBean);
		retrieveRolecode();
	}

	/**
	 * madatoryIndicator - Indicator for Mandatory .
	 */
	public void madatoryIndicator() {
		log.info("ReportBackingBean-madatoryIndicator");
		if ((null != this.getReportValue())
				&& (this.getModuleValue()
						.equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))) {
			getManagedBean().getRptGenBean().setCmMadatoryIndicatorView(true);
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(false);
		} else {
			getManagedBean().getRptGenBean().setCmMadatoryIndicatorView(false);
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(true);
		}
		if ((null != this.getReportValue())
				&& (this.getReportValue()
						.equalsIgnoreCase(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL))) {
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(true);
		}
	}

	/**
	 * retriveReportListByModule - Retrive report list by module.
	 * 
	 * @param module
	 *            the module
	 * @return the reportList
	 */
	public List<SelectItem> retriveReportListByModule(String module) {
		log.info("ReportBackingBean-retriveReportListByModule");
		reportList = new ArrayList<SelectItem>();
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setModuleCode(module);
		PMReportResDTO rptResDTO = reportSO.retriveReportByModule(reqDTO);
		List<PMReport> rptLst = rptResDTO.getPmReportList();
		if (null != rptLst) {
			for (PMReport pmReport : rptLst) {
				SelectItem selectItem = new SelectItem();
				String label = ReportConst.EMPTY_VAL;
				if ((null != pmReport.getSubModule())
						&& (ReportConst.EMPTY_VAL != pmReport.getSubModule())) {
					label = pmReport.getReportTitle() + ReportConst.RPT_EIFON
							+ pmReport.getSubModule();
				} else {
					label = pmReport.getReportTitle();
				}
				if (!(pmReport.getReportCode()
						.equals(ReportConst.SC_S1_RCNT_PRICE_RPT_VAL))
						&& !(pmReport.getReportCode()
								.equals(ReportConst.SC_ITEM_PRICING_SUM_RPT_VAL))
						&& !(pmReport.getReportCode()
								.equals(ReportConst.SC_S1_REQUEST_NOTE_FINAL_RPT_VAL))) {
					selectItem.setValue(pmReport.getReportCode());
					selectItem.setLabel(label);
					reportList.add(selectItem);
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780012);
		}
		return reportList;
	}

	/**
	 * retrieveUPSSVOUser - Retrieve ups svo user, based on user id.
	 * 
	 * @return the smUPSSVOUserList
	 */
	public List<SelectItem> retrieveUPSSVOUser() {
		log.info("ReportBackingBean-retrieveUPSSVOUser");
		smUPSSVOUserList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrieveUPSSVOUser(lngUserId);
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				smUPSSVOUserList.add(selectItem);
			}
		}
		return smUPSSVOUserList;
	}

	/**
	 * retrievePTJS1 - Retrieve PTJ for s1, based on user id.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> retrievePTJS1() {
		log.info("ReportBackingBean-retrievePTJS1");
		scPtjList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrievePTJS1(lngUserId);
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scPtjList.add(selectItem);
			}
		}
		return scPtjList;
	}

	/**
	 * retrievePTJS2 - Retrieve PTJ for s2, based on user id.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> retrievePTJS2() {
		log.info("ReportBackingBean-retrievePTJS2");
		scPtjList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrievePTJS2(lngUserId);
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scPtjList.add(selectItem);
			}
		}
		return scPtjList;
	}

	/**
	 * equisitionerList - Retrieve requisitioner, based on user id and
	 * organization profile id.
	 * 
	 * @return the requisitionerList
	 */
	public List<SelectItem> retrieveRequisitioner() {
		log.info("ReportBackingBean-retrieveRequisitioner");
		scRequisitionerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			if ((this.getScPtj() != null)
					&& (this.getScPtj().toString() != ReportConst.EMPTY_VAL)) {
				orgProfileId = Long.parseLong(this.getScPtj());
			}
			List<Object[]> reqLst = reportSO.retrieveRequisitioner(lngUserId,
					orgProfileId);
			for (Object[] object : reqLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scRequisitionerList.add(selectItem);
			}
		}
		return scRequisitionerList;
	}

	/**
	 * retrieveDescOfficer - Retrieve desc officer, based on user id and
	 * organization profile id.
	 * 
	 * @return the descOfficerList
	 */
	public List<SelectItem> retrieveDescOfficer() {
		log.info("ReportBackingBean-retrieveDescOfficer");
		scDescOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			if ((this.getScPtj() != null)
					&& (this.getScPtj().toString() != ReportConst.EMPTY_VAL)) {
				orgProfileId = Long.parseLong(this.getScPtj());
			}
			List<Object[]> reqLst = reportSO.retrieveDeskOfficer(lngUserId,
					orgProfileId);
			for (Object[] object : reqLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scDescOfficerList.add(selectItem);
			}
		}
		return scDescOfficerList;
	}

	/**
	 * retrieveApprovarNaikTarafDPS1 - Retrieve approvar naik taraf for dp s1,
	 * based on user id.
	 * 
	 * @return the approverNayakTarafList
	 */
	public List<SelectItem> retrieveApprovarNaikTarafDPS1() {
		log.info("ReportBackingBean-retrieveApprovarNaikTarafDPS1");
		approverNayakTarafList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO
					.retrieveApprovarNaikTarafDPS1(lngUserId);
			for (Object[] object : aprnktrfLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				approverNayakTarafList.add(selectItem);
			}
		}
		return approverNayakTarafList;
	}

	/**
	 * retrieveApprovarNaikTarafSCS2 - Retrieve approvar naik taraf for sc s2,
	 * based on user id.
	 * 
	 * @return the approverNayakTarafList
	 */
	public List<SelectItem> retrieveApprovarNaikTarafSCS2() {
		log.info("ReportBackingBean-retrieveApprovarNaikTarafSCS2");
		approverNayakTarafList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO
					.retrieveApprovarNaikTarafSCS2(lngUserId);
			for (Object[] object : aprnktrfLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				approverNayakTarafList.add(selectItem);
			}
		}
		return approverNayakTarafList;
	}

	/**
	 * retriveUserrloeByUserId - Retrive userrole by user id, based on module
	 * name.
	 * 
	 * @param moduleName
	 *            the module name
	 * @return the authoriztionList
	 */
	public List<String> retriveUserrloeByUserId(String moduleName) {
		log.info("ReportBackingBean-retriveUserrloeByUserId");
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			reqDTO.setUserId(lngUserId);
			// reqDTO.setUserId((long) 240500);
			reqDTO.setLanguageCode(ReportConst.ENG_LANG);
			UserResDTO userResDTO = userSO
					.retrieveUserRoleDescListByUserId(reqDTO);
			List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
			if (null != roleDesc) {
				for (PmRoleDesc pmRoleDesc : roleDesc) {
					pmRoleDesc.getPmRole().getRoleCode();
					if (!authoriztionList.contains(pmRoleDesc.getPmRole()
							.getRoleCode())) {
						authoriztionList.add(pmRoleDesc.getPmRole()
								.getRoleCode());
					}
				}
			}
		}
		return authoriztionList;
	}

	/**
	 * retrieveAllMinistry - Retrieve all ministry, based on rolecode.
	 */
	public void retrieveAllMinistry() {
		log.info("ReportBackingBean-retrieveAllMinistry");
		ptjList = new ArrayList<SelectItem>();
		ministryList = new ArrayList<SelectItem>();
		retrieveRoleCodeFromParamTable();
		if (highRoleCodeList != null) {
			if ((paramTableRoleCodeList != null)
					&& (paramTableRoleCodeList
							.contains(highRoleCodeList.get(0)))) {
				// if((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value()))))
				// {
				getLtstMinistryList();
				getParameterCodeTypeString();
				getManagedBean().getRptGenBean().setFlSupplierDropdownView(
						false);
				getManagedBean().getRptGenBean().setFlSupplierTextView(false);
				if ((parameterCodeType != null)
						&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
					retrieveSupplierCompName();
					if ((this.getSupplCompanyList().size() > 0)
							&& (getManagedBean().getRptGenBean()
									.isFlSupplierView())) {
						getManagedBean().getRptGenBean()
								.setFlSupplierDropdownView(true);
					} else if ((this.getSupplCompanyList().size() == 0)
							&& (getManagedBean().getRptGenBean()
									.isFlSupplierView())) {
						getManagedBean().getRptGenBean().setFlSupplierTextView(
								true);
					}
				} else {
					if (getManagedBean().getRptGenBean().isFlSupplierView()) {
						getManagedBean().getRptGenBean().setFlSupplierTextView(
								true);
					}
				}
				paramCodeSupplierGroupDropdown(parameterCodeType);
			} else {
				retrieveParameterCodeType();
			}
		}
		if ((this.getModuleValue()
				.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT))) {
			getManagedBean().getRptGenBean().setCtSupplierTextView(false);
			getManagedBean().getRptGenBean().setCtSupplierDefaultView(false);
			getManagedBean().getRptGenBean().setCtGroupFiveWithoutSupplView(
					false);
			getManagedBean().getRptGenBean().setCtGroupFiveWithSupplView(false);
			if ((this.getReportValue() != null)
					&& (this.getReportValue()
							.equals(ReportConst.CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL))) {
				getParameterCodeTypeString();
				if ((parameterCodeType != null)
						&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
					retrieveSupplierCompName();
					if ((this.getSupplCompanyList().size() > 0)
							&& (getManagedBean().getRptGenBean()
									.isCtSupplierNameView())) {
						getManagedBean().getRptGenBean()
								.setCtSupplierDefaultView(true);
					} else if ((this.getSupplCompanyList().size() == 0)
							&& (getManagedBean().getRptGenBean()
									.isFlSupplierView())) {
						getManagedBean().getRptGenBean().setCtSupplierTextView(
								true);
					}
					if (getManagedBean().getRptGenBean().isCtGroupFiveView()) {
						getManagedBean().getRptGenBean()
								.setCtGroupFiveWithoutSupplView(true);
					}
				} else {
					if (getManagedBean().getRptGenBean().isCtSupplierNameView()) {
						getManagedBean().getRptGenBean().setCtSupplierTextView(
								true);
					}
					if (getManagedBean().getRptGenBean().isCtGroupFiveView()) {
						getManagedBean().getRptGenBean()
								.setCtGroupFiveWithSupplView(true);
					}
				}
			}

		}
	}

	/**
	 * retrieveRoleCodeFromParamTable - Retrieve role code from param table.
	 * 
	 * @return the paramTableRoleCodeList
	 */
	public List<String> retrieveRoleCodeFromParamTable() {
		log.info("ReportBackingBean-retrieveRoleCodeFromParamTable");
		paramTableRoleCodeList = new ArrayList<String>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> roleCodeFromParamTableLst = reportSO
					.retrieveRoleCodeFromParamTable();
			for (int i = 0; i < roleCodeFromParamTableLst.size(); i++) {
				paramTableRoleCodeList.add(roleCodeFromParamTableLst.get(i));
			}
		}
		return paramTableRoleCodeList;
	}

	/**
	 * getParameterCodeTypeString - Gets the parameter code type string.
	 * 
	 * @return the parameterCodeType string
	 */
	public String getParameterCodeTypeString() {
		log.info("ReportBackingBean-getParameterCodeTypeString");
		parameterCodeType = new String();
		/*
		 * ptjList = new ArrayList<String>(); ministryList = new
		 * ArrayList<SelectItem>();
		 */
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> paramcodeLst = reportSO
					.retrieveParameterCodeByUserid(lngUserId);
			for (Object[] object : paramcodeLst) {
				parameterCodeType = object[0].toString();
				// parameterCodeType = "SUP";
			}
		}
		return parameterCodeType;
	}

	/**
	 * retrieveParameterCodeType - Retrieve parameter code type.
	 */
	public void retrieveParameterCodeType() {
		log.info("ReportBackingBean-retrieveParameterCodeType");
		ptjList = new ArrayList<SelectItem>();
		ministryList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			getParameterCodeTypeString();
			if ((parameterCodeType != null)
					&& (parameterCodeType
							.equals(ReportConst.PARAM_CODE_TYPE_FPJ))) {
				// retrieveFPJMinistryList();
				// getLtstMinistryListByUserid();
				retrieveOnlyFPJMinistryList();
				retrieveFPJPTJList();
				if (getManagedBean().getRptGenBean().isFlSupplierView()) {
					getManagedBean().getRptGenBean()
							.setFlSupplierTextView(true);
				}
			} else if ((parameterCodeType != null)
					&& (parameterCodeType
							.equals(ReportConst.PARAM_CODE_TYPE_FMI))) {
				retrieveFPJMinistryList();
				// retrieveFPJPTJList();
				if (getManagedBean().getRptGenBean().isFlSupplierView()) {
					getManagedBean().getRptGenBean()
							.setFlSupplierTextView(true);
				}
			} else if ((parameterCodeType != null)
					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
				retrieveSupplierCompName();
				if ((this.getSupplCompanyList().size() > 0)
						&& (getManagedBean().getRptGenBean().isFlSupplierView())) {
					getManagedBean().getRptGenBean().setFlSupplierDropdownView(
							true);
				} else if ((this.getSupplCompanyList().size() == 0)
						&& (getManagedBean().getRptGenBean().isFlSupplierView())) {
					getManagedBean().getRptGenBean()
							.setFlSupplierTextView(true);
				}

			} else {
				if (getManagedBean().getRptGenBean().isFlSupplierView()) {
					getManagedBean().getRptGenBean()
							.setFlSupplierTextView(true);
				}
			}
			paramCodeSupplierGroupDropdown(parameterCodeType);
		}
	}

	/**
	 * paramCodeSupplierGroupDropdown - Param code supplier group dropdown.
	 * 
	 * @param parameterCodeType
	 *            the parameter code type
	 */
	public void paramCodeSupplierGroupDropdown(String parameterCodeType) {
		log.info("ReportBackingBean-paramCodeSupplierGroupDropdown");
		if ((parameterCodeType != null)
				&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
			if ((getManagedBean().getRptGenBean().isFlGroupOneView())) {
				getManagedBean().getRptGenBean().setFlGroupOneWithoutSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwoView())) {
				getManagedBean().getRptGenBean().setFlGroupTwoWithoutSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupThreeView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupThreeWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFourView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFourWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFiveView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFiveWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSixView())) {
				getManagedBean().getRptGenBean().setFlGroupSixWithoutSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSevenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupSevenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupEightView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupEightWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupNineView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupNineWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTenView())) {
				getManagedBean().getRptGenBean().setFlGroupTenWithoutSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupElevenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupElevenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwelveView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwelveWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupThirteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupThirteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupFourteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFourteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFifteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFifteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSixteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupSixteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupSeventeenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupSeventeenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupEighteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupEighteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupNineteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupNineteenWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwentyView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyOneView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyOneWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyTwoView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyTwoWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyThreeView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyThreeWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyFourView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyFourWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyFiveView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyFiveWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentySixView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentySixWithoutSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentySevenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentySevenWithoutSupplView(true);
			}
		} else {
			if ((getManagedBean().getRptGenBean().isFlGroupOneView())) {
				getManagedBean().getRptGenBean().setFlGroupOneWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwoView())) {
				getManagedBean().getRptGenBean().setFlGroupTwoWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupThreeView())) {
				getManagedBean().getRptGenBean().setFlGroupThreeWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFourView())) {
				getManagedBean().getRptGenBean().setFlGroupFourWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFiveView())) {
				getManagedBean().getRptGenBean().setFlGroupFiveWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSixView())) {
				getManagedBean().getRptGenBean().setFlGroupSixWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSevenView())) {
				getManagedBean().getRptGenBean().setFlGroupSevenWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupEightView())) {
				getManagedBean().getRptGenBean().setFlGroupEightWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupNineView())) {
				getManagedBean().getRptGenBean().setFlGroupNineWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTenView())) {
				getManagedBean().getRptGenBean().setFlGroupTenWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupElevenView())) {
				getManagedBean().getRptGenBean().setFlGroupElevenWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwelveView())) {
				getManagedBean().getRptGenBean().setFlGroupTwelveWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupThirteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupThirteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupFourteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFourteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupFifteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupFifteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupSixteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupSixteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupSeventeenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupSeventeenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupEighteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupEighteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupNineteenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupNineteenWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean().isFlGroupTwentyView())) {
				getManagedBean().getRptGenBean().setFlGroupTwentyWithSupplView(
						true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyOneView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyOneWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyTwoView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyTwoWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyThreeView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyThreeWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyFourView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyFourWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentyFiveView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentyFiveWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentySixView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentySixWithSupplView(true);
			} else if ((getManagedBean().getRptGenBean()
					.isFlGroupTwentySevenView())) {
				getManagedBean().getRptGenBean()
						.setFlGroupTwentySevenWithSupplView(true);
			}
		}
	}

	/**
	 * enableSupplierCompNameTextOrDropdown - Enable supplier company name text
	 * or dropdown.
	 */
	public void enableSupplierCompNameTextOrDropdown() {
		log.info("ReportBackingBean-enableSupplierCompNameTextOrDropdown");
		if ((this.getModuleValue() != null)
				&& (this.getModuleValue()
						.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
			getParameterCodeTypeString();
			retrieveSupplierCompName();
			if (((parameterCodeType != null) && (parameterCodeType
					.equals(ReportConst.ROLE_CODE_SUP)))
					&& (this.getReportValue()
							.equalsIgnoreCase(ReportConst.SM_PERSON_BRANCH_BANK))) {
				if ((this.getSupplCompanyList().size() > 0)
						&& (getManagedBean().getRptGenBean().isSmSupplierView())) {
					getManagedBean().getRptGenBean()
							.setSmCompanyNameDropdownView(true);
				} else if ((this.getSupplCompanyList().size() == 0)
						&& (getManagedBean().getRptGenBean().isSmSupplierView())) {
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}

				getManagedBean().getRptGenBean().setSmExtraTwoColumnView(true);
				getManagedBean().getRptGenBean().setSmRegistrationTypeView(
						false);
				getManagedBean().getRptGenBean().setSmNonMandatoryDateView(
						false);
				getManagedBean().getRptGenBean().setSmMOFRegisteredDateView(
						false);
			} else if ((parameterCodeType != null)
					&& (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {

				if ((this.getSupplCompanyList().size() > 0)
						&& (getManagedBean().getRptGenBean().isSmSupplierView())) {
					getManagedBean().getRptGenBean()
							.setSmCompanyNameDropdownView(true);
				} else if ((this.getSupplCompanyList().size() == 0)
						&& (getManagedBean().getRptGenBean().isSmSupplierView())) {
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}
			} else {
				if (getManagedBean().getRptGenBean().isSmSupplierView()) {
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}
			}
		}
	}

	/**
	 * retrieveFPJPTJList - Retrieve fpj PTJ list.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> retrieveFPJPTJList() {
		log.info("ReportBackingBean-retrieveFPJPTJList");
		ptjList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptjLst = reportSO.retrieveFPJPTJList(lngUserId);
			if (ptjLst.size() > 0) {
				if (ptjLst.size() > 2) {
					SelectItem selectItem1 = new SelectItem();
					selectItem1.setLabel(ResourceBundleUtils.getMessage(
							"lbl.txt.select",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
					selectItem1.setValue(null);
					ptjList.add(selectItem1);
				}
				for (Object[] object : ptjLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1].toString());
					ptjList.add(selectItem);
				}
			}
		}
		return ptjList;
	}

	/**
	 * retrieveFMIPTJList - Retrieve fmi PTJ list.
	 * 
	 * @return the ptjList
	 */
	public List<SelectItem> retrieveFMIPTJList() {
		log.info("ReportBackingBean-retrieveFMIPTJList");
		ptjList = new ArrayList<SelectItem>();
		// if ((null
		// !=this.getCommonMinistry())&&(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)))||((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value()))))))
		// {
		if (null != this.getCommonMinistry()) {
			getParameterCodeTypeString();
			if (((parameterCodeType != null) && (parameterCodeType
					.equals(ReportConst.PARAM_CODE_TYPE_FMI)))
					|| ((paramTableRoleCodeList != null) && (paramTableRoleCodeList
							.contains(highRoleCodeList.get(0))))) {
				Long lngorgprflid = Long.parseLong(this.getCommonMinistry());
				IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				List<Object[]> ptjLst = reportSO
						.retrieveFMIPTJList(lngorgprflid);
				if (ptjLst.size() > 0) {
					if (ptjLst.size() > 2) {
						SelectItem selectItem1 = new SelectItem();
						selectItem1.setLabel(ResourceBundleUtils.getMessage(
								"lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()));
						selectItem1.setValue(null);
						ptjList.add(selectItem1);
					}
					for (Object[] object : ptjLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1].toString());
						ptjList.add(selectItem);
					}
				}
			}
		}
		return ptjList;
	}

	/**
	 * retrieveFMIPTJListWithArg - Retrieve fmi PTJ list with arg.
	 * 
	 * @param lngMinistry
	 *            the lng ministry
	 * @return the ptjList
	 */
	public List<SelectItem> retrieveFMIPTJListWithArg(Long lngMinistry) {
		log.info("ReportBackingBean-retrieveFMIPTJListWithArg");
		ptjList = new ArrayList<SelectItem>();
		Long firstPTJValue = null;
		// if ((null
		// !=this.getCommonMinistry())&&(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)))||((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value()))))))
		// {

		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> ptjLst = reportSO.retrieveFMIPTJList(lngMinistry);
		/*
		 * for (int i = 0; i < ptjLst.size(); i++) { ptjList.add(ptjLst.get(i));
		 * }
		 */
		for (Object[] object : ptjLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(object[0].toString());
			selectItem.setValue(object[1].toString());
			ptjList.add(selectItem);
		}
		if (ptjList.size() > 0) {
			firstPTJValue = Long.parseLong((String) ptjList.get(0).getValue());
			roleCodeUsersSetupOnload(firstPTJValue);
		}
		return ptjList;
	}

	/**
	 * retrieveOnlyFPJMinistryList - Retrieve only fpj ministry list.
	 * 
	 * @return the ministryList
	 */
	public List<SelectItem> retrieveOnlyFPJMinistryList() {
		log.info("ReportBackingBean-retrieveOnlyFPJMinistryList");
		ministryList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> minstryLst = reportSO
					.retrieveFPJMinistryList(lngUserId);
			for (Object[] object : minstryLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ministryList.add(selectItem);
			}
		}
		return ministryList;
	}

	/**
	 * retrieveFPJMinistryList - Retrieve fpj ministry list.
	 * 
	 * @return the ministryList
	 */
	public List<SelectItem> retrieveFPJMinistryList() {
		log.info("ReportBackingBean-retrieveFPJMinistryList");
		ministryList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> minstryLst = reportSO
					.retrieveFPJMinistryList(lngUserId);
			for (Object[] object : minstryLst) {
				/*
				 * SelectItem selectItem1 = new SelectItem();
				 * selectItem1.setLabel
				 * (ResourceBundleUtils.getMessage("lbl.txt.select",
				 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
				 * FacesScopes().getUserLocale())); selectItem1.setValue(null);
				 * ministryList.add(selectItem1);
				 */
				SelectItem selectItem = new SelectItem();

				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ministryList.add(selectItem);
				if ((null != object[1].toString())
						&& (ReportConst.EMPTY_VAL != object[1].toString())) {
					Long lngMinistry = Long.parseLong(object[1].toString());
					retrieveFMIPTJListWithArg(lngMinistry);
				}
			}
		}
		return ministryList;
	}

	/*
	 * public List<SelectItem> retrieveFMIMinistryList() { if (null != new
	 * FacesScopes().getUserSessionBean().getUserId()) { IPMReportSO reportSO =
	 * EpEjbBusinessDelegate.getInstance()
	 * .getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
	 * List<Object[]> minstryLst = reportSO.retrieveFMIMinistryList(lngUserId);
	 * for (Object[] object : minstryLst) { SelectItem selectItem = new
	 * SelectItem(); selectItem.setLabel(object[0].toString());
	 * selectItem.setValue(object[1].toString()); ministryList.add(selectItem);
	 * } } return ministryList; }
	 */
	/**
	 * retrieveCMUnspc - Retrieve cm unspc.
	 */
	public void retrieveCMUnspc() {
		log.info("ReportBackingBean-retrieveCMUnspc");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> cmunspcLst = reportSO.retrieveCMUnspc();
		for (Object[] object : cmunspcLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(object[0].toString());
			selectItem.setValue(object[1]);
			unspcList.add(selectItem);
		}
	}

	/**
	 * enableExtension - Enable extention will enable brand, extension
	 * type,measurement and color extentions.
	 */
	public void enableExtension() {
		log.info("ReportBackingBean-enableExtension");
		this.setCmExtentionCode(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		rptGenBean.setCmExtentionCodeView(false);
		rptGenBean.setCmExtentionBrandView(false);
		rptGenBean.setCmExtentionTypeView(false);
		rptGenBean.setCmExtentionMeasurmentView(false);
		rptGenBean.setCmExtentionColourView(false);
		if (null != this.getCmUnspscCode()
				&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
			if ((this.getCmUnspscCode().length() == 8)) {
				unspid = retrieveCMUnspcIdbyCode(this.getCmUnspscCode());
				if (null != unspid) {
					rptGenBean.setCmExtentionCodeView(true);
				} else {
					this.setCmUnspscCode(null);
					this.setCmExtentionCode(null);
					rptGenBean.setCmExtentionCodeView(false);
					throw new EpException(MessageConst.ERROR_REPORT_780013);
				}
			} else {
				this.setCmUnspscCode(null);
				this.setCmExtentionCode(null);
				rptGenBean.setCmExtentionCodeView(false);
				throw new EpException(MessageConst.ERROR_REPORT_780013);
			}
		} else {
			this.setCmExtentionCode(null);
			rptGenBean.setCmExtentionCodeView(false);
		}
	}

	/**
	 * enableExtensionTypes - Enable extention types will enable brand,
	 * extension type,measurement and color extentions.
	 */
	public void enableExtensionTypes() {
		log.info("ReportBackingBean-enableExtensionTypes");
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
		getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
		getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
		getManagedBean().getRptGenBean().setCmExtentionColourView(false);
		if (null != this.getCmUnspscCode()
				&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
			if ((this.getCmUnspscCode().length() == 8)) {
				unspid = retrieveCMUnspcIdbyCode(this.getCmUnspscCode());
				// unspid = (long) 11606;
				if (null != unspid) {
					retrieveBrand();
					retrieveExtType();
					retrieveMeasurement();
					retrieveColor();
					if (CollectionUtils.isNotEmpty(brandList)
							&& CollectionUtils.isNotEmpty(extTypeList)
							&& CollectionUtils.isNotEmpty(measurmentList)
							&& CollectionUtils.isNotEmpty(colorList)) {

						getManagedBean().getRptGenBean()
								.setCmExtentionBrandView(true);
						getManagedBean().getRptGenBean()
								.setCmExtentionTypeView(true);
						getManagedBean().getRptGenBean()
								.setCmExtentionMeasurmentView(true);
						getManagedBean().getRptGenBean()
								.setCmExtentionColourView(true);
					}
				} /*
				 * else { this.setCmUnspscCode(null);
				 * this.setCmUnspscCode(null); this.setCmExtentionBrand(null);
				 * this.setCmExtentionType(null);
				 * this.setCmExtentionMeasurment(null);
				 * this.setCmExtentionColour(null); brandList = new
				 * ArrayList<SelectItem>(); extTypeList = new
				 * ArrayList<SelectItem>(); measurmentList = new
				 * ArrayList<SelectItem>(); colorList = new
				 * ArrayList<SelectItem>();
				 * getManagedBean().getRptGenBean().setCmExtentionBrandView(
				 * false);
				 * getManagedBean().getRptGenBean().setCmExtentionTypeView(
				 * false); getManagedBean().getRptGenBean()
				 * .setCmExtentionMeasurmentView(false);
				 * getManagedBean().getRptGenBean().setCmExtentionColourView(
				 * false); throw new
				 * EpException(MessageConst.ERROR_REPORT_780013); }
				 */
			} /*
			 * else { this.setCmUnspscCode(null);
			 * this.setCmExtentionBrand(null); this.setCmExtentionType(null);
			 * this.setCmExtentionMeasurment(null);
			 * this.setCmExtentionColour(null); brandList = new
			 * ArrayList<SelectItem>(); extTypeList = new
			 * ArrayList<SelectItem>(); measurmentList = new
			 * ArrayList<SelectItem>(); colorList = new ArrayList<SelectItem>();
			 * getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
			 * getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
			 * getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(
			 * false); getManagedBean().getRptGenBean()
			 * .setCmExtentionColourView(false); throw new
			 * EpException(MessageConst.ERROR_REPORT_780013); }
			 */
		} else {
			this.setCmUnspscCode(null);
			this.setCmExtentionBrand(null);
			this.setCmExtentionType(null);
			this.setCmExtentionMeasurment(null);
			this.setCmExtentionColour(null);
			brandList = new ArrayList<SelectItem>();
			extTypeList = new ArrayList<SelectItem>();
			measurmentList = new ArrayList<SelectItem>();
			colorList = new ArrayList<SelectItem>();
			getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
			getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
			getManagedBean().getRptGenBean()
					.setCmExtentionMeasurmentView(false);
			getManagedBean().getRptGenBean().setCmExtentionColourView(false);
		}
	}

	/**
	 * retrieveCMUnspcIdbyCode - Retrieve cm unspc id by code by unspcCode.
	 * 
	 * @param unspcCode
	 *            the unspc code
	 * @return the long unspcid
	 */
	public Long retrieveCMUnspcIdbyCode(String unspcCode) {
		log.info("ReportBackingBean-retrieveCMUnspcIdbyCode");
		Long unspcid = null;
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<BigDecimal> cmunspcLst = reportSO
				.retrieveCMUnspcIdbyCode(unspcCode);
		if ((null != cmunspcLst) && (cmunspcLst.size() != 0)) {
			unspcid = ((BigDecimal) cmunspcLst.get(0)).longValue();
		}
		return unspcid;
	}

	/**
	 * retrieveExtention - Retrieve extention bsed on extention type.
	 */
	public void retrieveExtention() {
		log.info("ReportBackingBean-retrieveExtention");
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		rptGenBean.setCmExtentionBrandView(false);
		rptGenBean.setCmExtentionTypeView(false);
		rptGenBean.setCmExtentionMeasurmentView(false);
		rptGenBean.setCmExtentionColourView(false);
		if (null != this.getCmExtentionCode()) {
			if (this.getCmExtentionCode().equals(ReportConst.EXT_BRAND)) {
				rptGenBean.setCmExtentionBrandView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveBrand();
			} else if (this.getCmExtentionCode().equals(ReportConst.EXT_TYPE)) {
				rptGenBean.setCmExtentionTypeView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveExtType();
			} else if (this.getCmExtentionCode().equals(
					ReportConst.EXT_MEASUREMENT)) {
				rptGenBean.setCmExtentionMeasurmentView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveMeasurement();
			} else if (this.getCmExtentionCode().equals(ReportConst.EXT_COLOUR)) {
				rptGenBean.setCmExtentionColourView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveColor();
			}
		}
	}

	/**
	 * retrieveBrand - Retrieve brand extention type.
	 */
	public void retrieveBrand() {
		log.info("ReportBackingBean-retrieveBrand");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> brandLst = reportSO.retrieveBrandByUnspsc(unspid);
		for (Object[] object : brandLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			brandList.add(selectItem);
		}
	}

	/**
	 * retrieveExtType - Retrieve extention type.
	 */
	public void retrieveExtType() {
		log.info("ReportBackingBean-retrieveExtType");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> typeLst = reportSO.retrieveTypeByUnspsc(unspid);
		for (Object[] object : typeLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			extTypeList.add(selectItem);
		}
	}

	/**
	 * retrieveMeasurement - Retrieve measurement extention type.
	 */
	public void retrieveMeasurement() {
		log.info("ReportBackingBean-retrieveMeasurement");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> msrmntLst = reportSO.retrieveMeasurementByUnspsc(unspid);
		for (Object[] object : msrmntLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			measurmentList.add(selectItem);
		}
	}

	/**
	 * retrieveColor - Retrieve color extention type.
	 */
	public void retrieveColor() {
		log.info("ReportBackingBean-retrieveColor");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> clrLst = reportSO.retrieveColorByUnspsc(unspid);
		for (Object[] object : clrLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			colorList.add(selectItem);
		}
	}

	/**
	 * retrieveRolecode - Retrieve rolecode.
	 */
	public void retrieveRolecode() {
		log.info("ReportBackingBean-retrieveRolecode");
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<String> rolecodeLst = reportSO.retrieveRolecode(lngUserId);
		for (int i = 0; i < rolecodeLst.size(); i++) {
			this.getRoleCodeList().add(rolecodeLst.get(i));
		}
	}

	/**
	 * retrieveSupplierCompName - Retrieve supplier company name.
	 */
	public void retrieveSupplierCompName() {
		log.info("ReportBackingBean-retrieveSupplierCompName");
		supplCompanyList = new ArrayList<String>();
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<String> supplCompNameLst = reportSO
				.retrieveSupplierCompName(lngUserId);
		for (int i = 0; i < supplCompNameLst.size(); i++) {
			supplCompanyList.add(supplCompNameLst.get(i));
		}
		if (supplCompNameLst.size() > 0) {
			if ((this.getModuleValue() != null)
					&& (this.getModuleValue()
							.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
				this.setSmCompanyName(supplCompNameLst.get(0));
			}
			if ((this.getModuleValue() != null)
					&& (this.getModuleValue()
							.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))) {
				this.setFlSupplier(supplCompNameLst.get(0));
			}
		}

	}

	/**
	 * changeDateBasedOnRegType - Change date based on registration type.
	 */
	public void changeDateBasedOnRegType() {
		log.info("ReportBackingBean-changeDateBasedOnRegType");
		getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(
				false);
		getManagedBean().getRptGenBean().setSmActivationDateView(false);
		getManagedBean().getRptGenBean().setSmSubmissionDateView(false);
		getManagedBean().getRptGenBean().setSmActSubmisAllDateView(false);
		getManagedBean().getRptGenBean().setSmMofRegNoTwoView(false);
		getManagedBean().getRptGenBean().setSmMofRegNoView(false);

		if (this.getSmRegistrationType() != null) {
			if (this.getSmRegistrationType().equals(ReportConst.RPT_REG_BASIC)) {

				if (this.getReportValue().equals(
						ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)) {
					
					getManagedBean().getRptGenBean().setSmMofRegNoTwoView(true);
					getManagedBean().getRptGenBean()
							.setSmActiveSubmissiveFromDateView(true);					
					getManagedBean().getRptGenBean().setSmActivationDateView(
							true);
				} else if (this.getReportValue().equals(
						ReportConst.SM_SUPPLIER_PERFORMANCE)) {
					getManagedBean().getRptGenBean()
							.setSmActiveSubmissiveFromDateView(true);
					getManagedBean().getRptGenBean().setSmActivationDateView(
							true);
				}
			} else if (this.getSmRegistrationType().equals(
					ReportConst.RPT_REG_MOF)
					|| this.getSmRegistrationType().equals(
							ReportConst.RPT_REG_BASIC_TO_MOF)) {
				if (this.getReportValue().equals(
						ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)) {
					getManagedBean().getRptGenBean().setSmMofRegNoView(true);
					getManagedBean().getRptGenBean()
							.setSmActiveSubmissiveFromDateView(true);
					getManagedBean().getRptGenBean().setSmSubmissionDateView(
							true);

				} else if (this.getReportValue().equals(
						ReportConst.SM_SUPPLIER_PERFORMANCE)) {
					getManagedBean().getRptGenBean()
							.setSmActiveSubmissiveFromDateView(true);
					getManagedBean().getRptGenBean().setSmSubmissionDateView(
							true);
				}
			} else if (this.getSmRegistrationType().equals(
					ReportConst.RPT_REG_ALL)) {
				if (this.getReportValue().equals(
						ReportConst.SM_SUPPLIER_PERFORMANCE)) {
					getManagedBean().getRptGenBean()
							.setSmActiveSubmissiveFromDateView(true);
					getManagedBean().getRptGenBean().setSmActSubmisAllDateView(
							true);
				}
			}
		}
		/*
		 * if (((this.getSmRegistrationType() != null) &&
		 * (this.getSmRegistrationType() .equals(ReportConst.RPT_REG_BASIC)) &&
		 * (this .getReportValue().equals(
		 * ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL) || this
		 * .getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))) {
		 * getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(
		 * true);
		 * getManagedBean().getRptGenBean().setSmActivationDateView(true); }
		 * else if (((this.getSmRegistrationType() != null) &&
		 * (this.getSmRegistrationType() .equals(ReportConst.RPT_REG_MOF)) &&
		 * (this .getReportValue().equals(
		 * ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL) || this
		 * .getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))) {
		 * getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(
		 * true);
		 * getManagedBean().getRptGenBean().setSmSubmissionDateView(true); }
		 * else if (((this.getSmRegistrationType() != null) &&
		 * (this.getSmRegistrationType() .equals(ReportConst.RPT_REG_ALL)) &&
		 * (this .getReportValue().equals(
		 * ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL) || this
		 * .getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))) {
		 * getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(
		 * true);
		 * getManagedBean().getRptGenBean().setSmActSubmisAllDateView(true); }
		 */
	}

	/**
	 * enableCriteriaMenu - Enable criteria menu.
	 */
	public void enableCriteriaMenu() {
		log.info("ReportBackingBean-enableCriteriaMenu");
		getManagedBean().getRptGenBean().setEnableCriteriaMenuView(false);
		if ((this.getReportValue() != null)
				&& (!this.getReportValue().equals(ReportConst.EMPTY_VAL))) {
			getManagedBean().getRptGenBean().setEnableCriteriaMenuView(true);
		}
	}

	/**
	 * criteriaVariablesByReport - Enable criteria variables by report name.
	 */
	public void criteriaVariablesByReport() {
		log.info("ReportBackingBean-criteriaVariablesByReport");
		parameterCodeType = new String();
		scReportBackingBean = new SCReportBackingBean();
		cmReportBackingBean = new CMReportBackingBean();
		flReportBackingBean = new FLReportBackingBean();
		smReportBackingBean = new SMReportBackingBean();
		ctReportBackingBean = new CTReportBackingBean();
		ppReportBackingBean = new PPReportBackingBean();
		pmReportBackingBean = new PMReportBackingBean();
		if (rptGenBean.isReportCategoryView() == true
				|| (this.getModuleValue().equalsIgnoreCase(
						ModuleCodeConst.ORDER_FULFILLMENT) && this
						.getReportCategory() != null)) {
			rptGenBean = new ReportsGenerateBean();
			rptGenBean.setReportCategoryView(true);
			// this.setReportCategoryView(true);
		} else {
			rptGenBean = new ReportsGenerateBean();
			// rptGenBean.setReportCategoryView(this.isReportCategoryView());
		}
		getManagedBean().setModuleNameValue(this.getModuleValue());
		getManagedBean().setRptGenBean(null);
		this.setSubModule(null);
		this.setReportName(ReportConst.EMPTY_VAL);
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		levOneCatLst = new ArrayList<SelectItem>();
		levTwoCatLst = new ArrayList<SelectItem>();
		levThreeCatLst = new ArrayList<SelectItem>();
		smMofRegistrationFeeList = new ArrayList<SelectItem>();
		smProcessingFeeList = new ArrayList<SelectItem>();
		smSoftCertificateFeeList = new ArrayList<SelectItem>();

		getManagedBean().setReportValue(this.getReportValue());
		if ((this.getModuleValue()) != null
				&& (getManagedBean().getReportValue()) != null) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			PMReportReqDTO reqDTO = new PMReportReqDTO();
			reqDTO.setReportCode(getManagedBean().getReportValue());
			PMReportResDTO rptResDTO = reportSO.retriveReportByrptId(reqDTO);
			PMReport pmReport = rptResDTO.getPmReportDtls();
			/*
			 * String language =
			 * ResourceBundleUtils.getMessage(ReportConst.LABEL_LANG,
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, new
			 * FacesScopes().getUserLocale()); //String language = "ms";
			 */
			if (null != pmReport) {
				String label = ReportConst.EMPTY_VAL;
				if ((null != pmReport.getSubModule())
						&& (ReportConst.EMPTY_VAL != pmReport.getSubModule())) {
					/*
					 * label = pmReport.getReportTitle() + ReportConst.RPT_EIFON
					 * + pmReport.getSubModule();
					 */
					if ((strLanguage != null)
							&& (strLanguage
									.equalsIgnoreCase(ReportConst.ENG_LANG))) {
						label = pmReport.getReportTitle();
						this.getManagedBean().setReportTitle(
								pmReport.getReportTitle());
					} else if ((strLanguage != null)
							&& (strLanguage
									.equalsIgnoreCase(ReportConst.MALAY_LANG))) {
						label = pmReport.getReportTitleMalay();
						this.getManagedBean().setReportTitle(
								pmReport.getReportTitleMalay());
					}
					this.setSubModule(pmReport.getSubModule());
				} else {
					if ((strLanguage != null)
							&& (strLanguage
									.equalsIgnoreCase(ReportConst.ENG_LANG))) {
						label = pmReport.getReportTitle();
						this.getManagedBean().setReportTitle(
								pmReport.getReportTitle());
					} else if ((strLanguage != null)
							&& (strLanguage
									.equalsIgnoreCase(ReportConst.MALAY_LANG))) {
						label = pmReport.getReportTitleMalay();
						this.getManagedBean().setReportTitle(
								pmReport.getReportTitle());
					}
				}
				this.setReportName(label);
				reportFileNameJSP = pmReport.getReportFilename();
				getManagedBean().setReportJsp(pmReport.getReportFilename());

			}
			retrieveHighRoleCodeByUser();
			retrieveAllRoleCodesByUser();
			retrieveZoneByUser();
			// retrieveMinistryList();
			retriveUserrloeByUserId(this.getModuleValue());
			retrieveParamCodeByUser();

			roleCodeUsersSetup();

			if ((this.getModuleValue().equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				retrieveApprovarNaikTarafDPS1();
				retrievePTJS1();

			}
			if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))) {
				retrieveApprovarNaikTarafSCS2();
				retrievePTJS2();
			}
			retrieveRequisitioner();
			retrieveDescOfficer();
			generateYearList();
			if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))) {
				cmReportBackingBean.criteriaVariablesByReportByCM(rptGenBean,
						authoriztionList, paramList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))
					|| (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				generateMonthList();

				scReportBackingBean.enableCriteriaVariablesByReportBySC(
						rptGenBean, authoriztionList, approverNayakTarafList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))) {
				getDocTypeStatusDropDown();
				flReportBackingBean.enableCriteriaVariablesByReportByFL(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
				assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
				retrieveUPSSVOUser();
				retrieveIdentificationType();
				retrieveAccumulatedPaidUpCptl();
				retrieveRegStatusList();
				retrievePaymentTypeList();
				retrievePaymentMethod();

				smReportBackingBean.enableCriteriaVariablesByReportBySM(
						rptGenBean, authoriztionList);

			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT))) {
				ctReportBackingBean.enableCriteriaVariablesByReportByCT(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.PROCUREMENT_PLAN))) {
				ppReportBackingBean.enableCriteriaVariablesByReportByPP(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.PROFILE_MANAGEMENT))) {

				pmReportBackingBean.enableCriteriaVariablesByReportByPM(
						rptGenBean, authoriztionList);
			}
			rptGenBean.isReportCategoryView();
			getManagedBean().setRptGenBean(rptGenBean);
			resetReportForm();
			if (this.getReportValue().equals(ReportConst.SM_LIST_OF_USERS)) {
				changeSoftCertSelection();
			}
			getGenBusCompanyData();
			if (pmReport != null) {
				getDefaultValuesFromAndToDate(pmReport.getReportCode());
			}
			retrieveDates();
			madatoryIndicator();
			retrieveAllMinistry();
			enableSupplierCompNameTextOrDropdown();
			// retrieveParameterCodeType();
			enableCriteriaMenu();
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			zoneByRolecode();
			retrieveProcessingOfficerList();
			retrievePMServiceType();
			directPurchaseWithRequisitioner();
		}
	}

	/**
	 * Gets the gen bus company data.
	 * 
	 * @return the gen bus company data
	 */
	private void getGenBusCompanyData() {

		if (this.getReportValue() != null) {
			if (this.getReportValue().equals(
					ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)
					|| this.getReportValue().equals(
							ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)
					|| this.getReportValue().equals(
							ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)) {
				if (this.smSupplierType == null) {
					if (this.getReportValue().equals(
							ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)) {
						retrieveAllBusCompanyData();
						retrieveRegStatusListForAll();
					} else if (this.getReportValue().equals(
							ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)) {
						retrieveAllBusCompanyData();
					} else if (this.getReportValue().equals(
							ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)) {
						retrieveRegStatusListForAll();
					}
				}
			}
		}
	}

	/**
	 * getDefaultValuesFromAndToDate - Gets the default values from and to date.
	 * 
	 * @param reportCode
	 *            the report code
	 * @return the default values from and to date
	 */
	private void getDefaultValuesFromAndToDate(String reportCode) {
		log.info("ReportBackingBean-getDefaultValuesFromAndToDate");
		Date currentDate = new Date();
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);

		List<String> reportNameList = new ArrayList<String>();

		// for CM Default Dates
		getDefaultDatesforCM(reportNameList);
		// for SM Defaul Dates
		getDefaultDatesforSM(reportNameList);
		// for CT Default Dates
		getDefaultDatesforCT(reportNameList);
		// For PM Dafault Dates
		getDefaultDatesforPM(reportNameList);
		// For FL Default Dates
		getDefaultDatesforFL(reportNameList);

		for (String code : reportNameList)
			if (code.equalsIgnoreCase(reportCode)) {
				this.setFromDate(fromDate.getTime());
				this.setToDate(currentDate);

			}
	}

	/**
	 * Gets the default datesfor pm.
	 * 
	 * @param reportNameList
	 *            the report name list
	 * @return the default datesfor pm
	 */
	private void getDefaultDatesforPM(List<String> reportNameList) {
		log.info("ReportBackingBean-getDefaultDatesforPM");
		reportNameList
				.add(ReportConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL);
		reportNameList.add(ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL);
		reportNameList
				.add(ReportConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL);
	}

	/**
	 * Gets the default datesfor ct.
	 * 
	 * @param reportNameList
	 *            the report name list
	 * @return the default datesfor ct
	 */
	private void getDefaultDatesforCT(List<String> reportNameList) {
		log.info("ReportBackingBean-getDefaultDatesforCT");
		reportNameList.add(ReportConst.CT_CONTRACT_MONITORING_RPT_VAL);
		reportNameList
				.add(ReportConst.CT_CONTRACT_TRANSACTION_MONITORING_CC_RPT_VAL);
		reportNameList
				.add(ReportConst.CT_CONTRACT_UTILIZATION_MONITORING_CC_RPT_VAL);

		reportNameList.add(ReportConst.CT_FULFILMENT_STATUS_RPT_VAL);
		reportNameList.add(ReportConst.CT_LIST_OF_CONTRACT_CC_RPT_VAL);
		reportNameList.add(ReportConst.CT_LST_OF_CIRCULAR_RPT_VAL);

		reportNameList.add(ReportConst.CT_LST_OF_CONTRACT_ALL_RPT_VAL);
		reportNameList
				.add(ReportConst.CT_STATEMENT_OF_ORDER_REPORT_CC_PANEL_RPT_VAL);

	}

	/**
	 * Gets the default datesfor cm.
	 * 
	 * @param reportNameList
	 *            the report name list
	 * @return the default datesfor cm
	 */
	private void getDefaultDatesforCM(List<String> reportNameList) {
		log.info("ReportBackingBean-getDefaultDatesforCM");
		reportNameList.add(ReportConst.LST_PUB_CAT_ITM_RPT_VAL);
		reportNameList.add(ReportConst.LST_APPR_CAT_ITM_RPT_VAL);
		reportNameList.add(ReportConst.LST_REJ_CAT_ITM_RPT_VAL);

		reportNameList.add(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL);
		reportNameList.add(ReportConst.LST_EXP_CAT_ITM_RPT_VAL);
		reportNameList.add(ReportConst.TOT_CAT_ITM_STS_RPT_VAL);

		reportNameList.add(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL);
		reportNameList.add(ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL);
		reportNameList.add(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL);

		reportNameList.add(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL);
		reportNameList.add(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL);
		reportNameList.add(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL);

		reportNameList.add(ReportConst.SUM_REJ_CAT_ITM_RPT_VAL);
		reportNameList.add(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL);
		reportNameList.add(ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL);

		reportNameList.add(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL);
		reportNameList.add(ReportConst.LST_ITM_CODE_RPT_VAL);

	}

	/**
	 * Gets the default datesfor sm.
	 * 
	 * @param reportNameList
	 *            the report name list
	 * @return the default datesfor sm
	 */
	private void getDefaultDatesforSM(List<String> reportNameList) {
		log.info("ReportBackingBean-getDefaultDatesforSM");
		reportNameList.add(ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL);
		reportNameList.add(ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL);
		reportNameList.add(ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL);
		reportNameList.add(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL);
		reportNameList.add(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL);
		reportNameList.add(ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL);

	}

	/**
	 * Gets the default datesfor fl.
	 * 
	 * @param reportNameList
	 *            the report name list
	 * @return the default datesfor fl
	 */
	private void getDefaultDatesforFL(List<String> reportNameList) {
		log.info("ReportBackingBean-getDefaultDatesforFL");
		reportNameList.add(ReportConst.CON_REQ_RPT_MIN_VAL);
		reportNameList.add(ReportConst.CON_ORD_RPT_MIN_VAL);
		reportNameList.add(ReportConst.DEL_ORD_RPT_MIN_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_RPT_MIN_VAL);
		reportNameList.add(ReportConst.LATE_DEL_RPT_MIN_VAL);
		reportNameList.add(ReportConst.SHP_INSTR_RPT_MIN_VAL);
		reportNameList.add(ReportConst.STP_INSTR_DEL_RPT_MIN_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL);
		reportNameList.add(ReportConst.PYMT_MATCH_RPT_MIN_VAL);
		reportNameList.add(ReportConst.DED_RPT_MIN_VAL);
		reportNameList.add(ReportConst.CON_REQ_CANC_RPT_MIN_VAL);
		reportNameList.add(ReportConst.CON_ORD_CANC_RPT_MIN_VAL);
		reportNameList.add(ReportConst.DEL_ORD_CANC_RPT_MIN_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL);
		reportNameList.add(ReportConst.INV_CANC_RPT_MIN_VAL);
		reportNameList.add(ReportConst.SUP_PERF_RPT_MIN_VAL);
		reportNameList.add(ReportConst.CON_REQ_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.CON_ORD_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.DEL_ORD_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL);
		reportNameList.add(ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.INV_CANC_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.SUP_PERF_RPT_CEN_PEN_VAL);
		reportNameList.add(ReportConst.SUMM_RPT_PAN_ITM_VAL);
		reportNameList.add(ReportConst.PUR_REQ_RPT_PO_VAL);
		reportNameList.add(ReportConst.PUR_ORD_RPT_PO_VAL);
		reportNameList.add(ReportConst.EMR_PROCUR_RPT_PO_VAL);
		reportNameList.add(ReportConst.PUR_ORD_REJ_RPT_PO_VAL);
		reportNameList.add(ReportConst.DEL_ORD_RPT_PO_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_RPT_PO_VAL);
		reportNameList.add(ReportConst.LATE_DEL_RPT_PO_VAL);
		reportNameList.add(ReportConst.STP_INSTR_DEL_RPT_PO_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_SUPL_PO_VAL);
		reportNameList.add(ReportConst.INV_RPT_VIEW_PTJ_PO_VAL);
		reportNameList.add(ReportConst.PYMT_MATCH_RPT_PO_VAL);
		reportNameList.add(ReportConst.PUR_REQ_CANC_RPT_PO_VAL);
		reportNameList.add(ReportConst.PUR_ORD_CANC_RPT_PO_VAL);
		reportNameList.add(ReportConst.DEL_ORD_CANC_RPT_PO_VAL);
		reportNameList.add(ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL);
		reportNameList.add(ReportConst.INV_CANC_RPT_PO_VAL);
		reportNameList.add(ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_MIN_VAL);
		reportNameList
				.add(ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_CEN_PEN_VAL);
		reportNameList.add(ReportConst.FL_STP_INSTR_INIT_PTJ_RPT_FL_PO_VAL);
		reportNameList.add(ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_MIN_VAL);
		reportNameList
				.add(ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_CEN_PEN_VAL);
		reportNameList.add(ReportConst.FL_STP_INSTR_INIT_SUP_RPT_FL_PO_VAL);
	}

	/**
	 * retrieveDates - Retrieve dates.
	 */
	public void retrieveDates() {
		log.info("ReportBackingBean-retrieveDates");
		if ((this.getReportValue() != null)
				&& (this.getReportValue() != ReportConst.EMPTY_VAL)) {
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_SELECTION)) {
				Date currentDate = new Date();
				this.setScCreatedFromYear(outputDateFormat.format(currentDate)
						.substring(6, 10));
				this.setScCreatedToYear(outputDateFormat.format(currentDate)
						.substring(6, 10));
			}
			if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_MANAGEMENT)) {
				if ((null != this.getReportValue())
						&& (this.getReportValue().equals(
								ReportConst.SM_SUPPLIER_PERFORMANCE)
								|| this.getReportValue().equals(
										ReportConst.SM_THRESHOLD)
								|| this.getReportValue().equals(
										ReportConst.SM_THRESHOLD_VALUE) || this
								.getReportValue().equals(
										ReportConst.SM_LST_MONTH_WISE_RPT_VAL))) {
					Date currentDate = new Date();
					this.setSmCreatedFromYear(outputDateFormat.format(
							currentDate).substring(6, 10));
					this.setSmCreatedToYear(outputDateFormat
							.format(currentDate).substring(6, 10));
					this.setSmThresholdFromYear(outputDateFormat.format(
							currentDate).substring(6, 10));
					this.setSmThresholdToYear(outputDateFormat.format(
							currentDate).substring(6, 10));
					this.setSmMonthlyRptByYear(outputDateFormat.format(
							currentDate).substring(6, 10));
				}
			}
			if ((this.getModuleValue().equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				Date currentDate = new Date();
				this.setScSoneCrtdToDate(currentDate);
				Date newDate = new Date();
				newDate.setMonth(0);
				newDate.setDate(1);
				this.setScSoneCrtdFromDate(newDate);
			}
		}
	}

	/**
	 * checkOneYearDefference - Check for one year defference.
	 * 
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 */
	public void checkOneYearDefference(Date fromDate, Date toDate) {
		log.info("ReportBackingBean-checkOneYearDefference");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		String yearFrom = outputDateFormat.format(fromDate).substring(6, 10);
		String yearTo = outputDateFormat.format(toDate).substring(6, 10);
		String monthFrom = outputDateFormat.format(fromDate).substring(3, 5);
		String monthTo = outputDateFormat.format(toDate).substring(3, 5);
		String dayFrom = outputDateFormat.format(fromDate).substring(0, 2);
		String dayTo = outputDateFormat.format(toDate).substring(0, 2);
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		int monthFromInt = Integer.parseInt(monthFrom);
		int monthToInt = Integer.parseInt(monthTo);
		int dayFromInt = Integer.parseInt(dayFrom);
		int dayToInt = Integer.parseInt(dayTo);
		if (yearToInt == yearFromInt + 1) {
			if (monthToInt == monthFromInt) {
				if (dayToInt > dayFromInt) {
					throw new EpException(MessageConst.ERROR_REPORT_780011);
				}
			} else if (monthToInt > monthFromInt) {
				throw new EpException(MessageConst.ERROR_REPORT_780011);
			}
		} else if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780011);
		}
	}

	/**
	 * checkOnlyYearFieldDefference - Check only year field defference.
	 * 
	 * @param yearFrom
	 *            the year from
	 * @param yearTo
	 *            the year to
	 */
	public void checkOnlyYearFieldDefference(String yearFrom, String yearTo) {
		log.info("ReportBackingBean-checkOnlyYearFieldDefference");
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		if (yearFromInt > yearToInt) {
			throw new EpException(MessageConst.ERROR_REPORT_780015);
		}
	}

	/**
	 * checkTwoYearsFieldDefference - Check two years field defference.
	 * 
	 * @param yearFrom
	 *            the year from
	 * @param yearTo
	 *            the year to
	 */
	public void checkTwoYearsFieldDefference(String yearFrom, String yearTo) {
		log.info("ReportBackingBean-checkTwoYearsFieldDefference");
		int yearFromInt = Integer.parseInt(yearFrom);
		int yearToInt = Integer.parseInt(yearTo);
		if (yearToInt > yearFromInt + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780017);
		}
	}

	/**
	 * retrieveState - Retrieve state.
	 */
	public void retrieveState() {
		log.info("ReportBackingBean-retrieveState");
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		addingStateList = new ArrayList<String>();
		addingDistrictList = new ArrayList<String>();
		IStateSO stateSO = (IStateSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ReportConst.PM_MODULE_SER_OBJ, IStateSO.class);
		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(1L);// TODO malaysia
		StateResDTO resStateDTO = stateSO
				.retriveStatesListByCountryID(stateReqDTO);
		for (PmState state : resStateDTO.getStateList()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(state.getStateName());
			selectItem.setValue(state.getStateId());
			stateLst.add(selectItem);
		}
	}

	/**
	 * retrieveDistrict - Retrieve district.
	 */
	public void retrieveDistrict() {
		log.info("ReportBackingBean-retrieveDistrict");
		districtLst = new ArrayList<SelectItem>();
		addingStateList = new ArrayList<String>();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						IDistrictSO.class);
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		if (this.getSmState() != null
				&& this.getSmState() != ReportConst.EMPTY_VAL) {
			districtReqDTO.setStateId(Long.parseLong(this.getSmState()));
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			for (PmDistrict pmDistrict : districtResDTO.getDistrictList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(pmDistrict.getDistrictName());
				selectItem.setValue(pmDistrict.getDistrictId());
				districtLst.add(selectItem);
			}
		}
	}

	/**
	 * retrieveCategoryLevOne - Retrieve category level one.
	 * 
	 * @return the levOneCatLst
	 */
	public List<SelectItem> retrieveCategoryLevOne() {
		log.info("ReportBackingBean-retrieveCategoryLevOne");
		levOneCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = categoryCodeSO
				.retrieveCategoryL1List();
		for (PmCategoryL1 catOne : catCodeResDTO.getCategoryL1List()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(catOne.getCategoryL1Code()
					+ ReportConst.RPT_EIFON + catOne.getCategoryName());
			selectItem.setValue(catOne.getCategoryL1Id());
			levOneCatLst.add(selectItem);
		}
		return levOneCatLst;
	}

	/**
	 * retrieveCategoryLevTwo - Retrieve category level two.
	 * 
	 * @param mainCategory
	 *            the main category
	 * @return the levTwoCatLst
	 */
	public List<SelectItem> retrieveCategoryLevTwo(String mainCategory) {
		log.info("ReportBackingBean-retrieveCategoryLevTwo");
		levTwoCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
		if ((mainCategory != null) && (mainCategory != ReportConst.EMPTY_VAL)) {
			catCodeReqDTO.setCategoryL1Id(Long.parseLong(mainCategory));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO
					.retrieveCategoryL2ListByL1Id(catCodeReqDTO);
			for (PmCategoryL2 catTwo : catCodeResDTO.getCategoryL2List()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(catTwo.getCategoryL2Code()
						+ ReportConst.RPT_EIFON + catTwo.getCategoryName());
				selectItem.setValue(catTwo.getCategoryL2Id());
				levTwoCatLst.add(selectItem);
			}
		}
		return levTwoCatLst;
	}

	/**
	 * retrieveCategoryLevThree - Retrieve category level three.
	 * 
	 * @param subCategory
	 *            the sub category
	 * @return the levThreeCatLst
	 */
	public List<SelectItem> retrieveCategoryLevThree(String subCategory) {
		log.info("ReportBackingBean-retrieveCategoryLevThree");
		levThreeCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
		if ((subCategory != null) && (subCategory != ReportConst.EMPTY_VAL)) {
			catCodeReqDTO.setCategoryL2Id(Long.parseLong(subCategory));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO
					.retrieveCategoryL3ListByL2Id(catCodeReqDTO);
			for (PmCategoryL3 catThree : catCodeResDTO.getCategoryL3List()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(catThree.getCategoryL3Code()
						+ ReportConst.RPT_EIFON + catThree.getCategoryName());
				selectItem.setValue(catThree.getCategoryL3Id());
				levThreeCatLst.add(selectItem);
			}
		}
		return levThreeCatLst;
	}

	/**
	 * enableRegistrationType - Enable registration type.
	 */
	public void enableRegistrationType() {
		log.info("ReportBackingBean-enableRegistrationType");
		this.setCmEpNumber(null);
		this.setCmIcNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmMofNumber(null);
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
		if ((this.getCmRegistrationType() != null)
				&& (this.getCmRegistrationType() != ReportConst.EMPTY_VAL)) {
			if (this.getCmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_BASIC)) {
				getManagedBean().getRptGenBean().setCmBasicRegView(true);
				getManagedBean().getRptGenBean().setCmMofRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			} else if (this.getCmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_MOF)) {
				getManagedBean().getRptGenBean().setCmMofRegView(true);
				getManagedBean().getRptGenBean().setCmBasicRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			}
		} else if ((this.getSmRegistrationType() != null)
				&& (this.getSmRegistrationType() != ReportConst.EMPTY_VAL)) {
			if (this.getSmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_BASIC)) {
				getManagedBean().getRptGenBean().setSmBasicRegView(true);
				getManagedBean().getRptGenBean().setSmMofRegView(false);

			} else if (this.getSmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_MOF)) {
				getManagedBean().getRptGenBean().setSmMofRegView(true);
				getManagedBean().getRptGenBean().setSmBasicRegView(false);
			}
		}
	}

	/**
	 * Generate month list.
	 * 
	 * @return the list
	 */
	public List<SelectItem> generateMonthList() {
		scMonthList = scReportBackingBean.generateMonthList(strLanguage);
		return scMonthList;
	}

	/**
	 * Generate year list.
	 * 
	 * @return the list
	 */
	public List<String> generateYearList() {
		scYearList = rptGenBean.generateCommonYearList();
		return scYearList;
	}

	/**
	 * Created to date update.
	 */
	public void createdToDtUpdate() {
		this.setScCreatedToYear(this.getScCreatedFromYear());
	}

	/**
	 * Created to month update.
	 */
	public void createdToMonthUpdate() {
		this.setScCreatedToMonth(this.getScCreatedFromMonth());
	}

	/**
	 * onSelectEffectedValues - On select effected values.S
	 */
	public void onSelectEffectedValues() {
		log.info("ReportBackingBean-onSelectEffectedValues");
		if (this.getScProcureAllList().size() > 0) {
			rptGenBean.setScProcureQuoteEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
			rptGenBean.setScProcureRestrctdEditView(true);
			scQuotationList = new ArrayList<SelectItem>();
			scTenderList = new ArrayList<SelectItem>();
			scRstrctdTndrList = new ArrayList<SelectItem>();
		} else if ((this.getScQuotationList().size() > 0)
				&& (this.getScTenderList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureRestrctdEditView(true);
		} else if ((this.getScRstrctdTndrList().size() > 0)
				&& (this.getScTenderList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureQuoteEditView(true);
		} else if ((this.getScRstrctdTndrList().size() > 0)
				&& (this.getScQuotationList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
		} else {
			rptGenBean.setScProcureAllEditView(false);
			rptGenBean.setScProcureQuoteEditView(false);
			rptGenBean.setScProcureTenderEditView(false);
			rptGenBean.setScProcureRestrctdEditView(false);
		}
	}

	/**
	 * checkSMYearNMonthDefference - Check sm year n month defference.
	 */
	public void checkSMYearNMonthDefference() {
		log.info("ReportBackingBean-checkSMYearNMonthDefference");
		int fromyear = 0;
		int toyear = 0;
		int fromMonth = 0;
		int toMonth = 0;
		if ((this.getSmCreatedFromYear() != null)
				&& (this.getSmCreatedFromYear() != ReportConst.EMPTY_VAL)) {
			fromyear = Integer.parseInt(this.getSmCreatedFromYear());
		}
		if ((this.getSmCreatedToYear() != null)
				&& (this.getSmCreatedToYear() != ReportConst.EMPTY_VAL)) {
			toyear = Integer.parseInt(this.getSmCreatedToYear());
		}
		if ((this.getSmFromMonth() != null)
				&& (this.getSmFromMonth() != ReportConst.EMPTY_VAL)) {
			fromMonth = Integer.parseInt(this.getSmFromMonth());
		}
		if ((this.getSmToMonth() != null)
				&& (this.getSmToMonth() != ReportConst.EMPTY_VAL)) {
			toMonth = Integer.parseInt(this.getSmToMonth());
		}
		if (fromyear == toyear) {
			if (fromMonth > toMonth) {
				throw new EpException(MessageConst.ERROR_REPORT_780014);
			}
		} else if (fromyear > toyear) {
			throw new EpException(MessageConst.ERROR_REPORT_780015);
		} else if (toyear > fromyear + 1) {
			throw new EpException(MessageConst.ERROR_REPORT_780011);
		}

	}

	/**
	 * generateReport - Generate all modules reports.
	 */
	public void generateReport() {
		log.info("ReportBackingBean-generateReport");
		if (((this.getReportValue() != null) && this.getReportValue()
				.toString()
				.equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL))
				|| ((this.getReportValue() != null) && this
						.getReportValue()
						.toString()
						.equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL))
				|| ((this.getReportValue() != null) && this
						.getReportValue()
						.toString()
						.equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL))
				|| ((this.getReportValue() != null) && this.getReportValue()
						.toString()
						.equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {
			mofRegNoNumericValidation();
		}

		rptGenBean = new ReportsGenerateBean();
		if ((this.getModuleValue() != null)
				&& (getManagedBean().getReportValue() != null)) {
			if (!(getManagedBean().getReportValue().toString()
					.equals(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))
					&& !(getManagedBean().getReportValue().toString()
							.equals(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))
					&& !(getManagedBean().getReportValue().toString()
							.equals(ReportConst.PDW_EC_RPT_VAL))) {
				if ((((this.getFromDate() == null) && (this.getToDate() != null)) || ((this
						.getFromDate() != null) && (this.getToDate() == null)))
						|| (((this.getScCreatedToMonth() == null)
								&& (this.getScCreatedFromMonth() != null) || (this
								.getScCreatedToMonth() != null)
								&& (this.getScCreatedFromMonth() == null)))) {
					throw new EpException(MessageConst.ERROR_REPORT_780004);
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getFromDate().after(this.getToDate())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getToDate().after(this.getFromDate())) {
					checkOneYearDefference(this.getFromDate(), this.getToDate());
				}
			}
			if ((this.getCtCircularEffectiveFromDate() != null)
					&& (this.getCtCircularEffectiveToDate() != null)) {
				if (this.getCtCircularEffectiveFromDate().after(
						this.getCtCircularEffectiveToDate())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				}
			}
			if ((this.getCtCircularEffectiveFromDate() != null)
					&& (this.getCtCircularEffectiveToDate() != null)) {
				if (this.getCtCircularEffectiveFromDate().after(
						this.getCtCircularEffectiveToDate())) {
					checkOneYearDefference(
							this.getCtCircularEffectiveFromDate(),
							this.getCtCircularEffectiveToDate());
				}
			}

			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdToDate() != null)) {
				if (this.getScSoneCrtdFromDate().after(
						this.getScSoneCrtdToDate())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				}
			}
			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdToDate() != null)) {
				if (this.getScSoneCrtdToDate().after(
						this.getScSoneCrtdFromDate())) {
					checkOneYearDefference(this.getScSoneCrtdFromDate(),
							this.getScSoneCrtdToDate());
				}
			}
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setReportValueIdVar(ReportConst.FL_AMP_REPORT_ID_EQ
						+ getManagedBean().getReportValue());
			}

			if ((((PmUser) new FacesScopes().getUserSessionBean()).getUserId() != null)
					&& (new FacesScopes().getUserSessionBean().getUserId()
							.toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setUserIdVar(ReportConst.FL_AMP_USER_ID_EQ
						+ new FacesScopes().getUserSessionBean().getUserId()
								.toString());
			}
			if ((this.getReportValue() != null && (getManagedBean()
					.getReportValue().trim()
					.equalsIgnoreCase(ReportConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL)))
					|| (this.getReportValue() != null && (getManagedBean()
							.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_COLLECTION_PAYMENT_METHODS_RPT_VAL)))
					|| (this.getReportValue() != null && (getManagedBean()
							.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_REVENUE_COLLECTION_RPT_VAL)))) {
				if ((CollectionUtils
						.isEmpty(this.getSmMofRegistrationFeeList())
						&& CollectionUtils.isEmpty(this
								.getSmProcessingFeeList()) && CollectionUtils
							.isEmpty(this.getSmSoftCertificateFeeList()))) {
					throw new EpException(MessageConst.ERROR_REPORT_780027);
				}
			}

			if (((this.getSmThresholdFromYear() != null) && (this
					.getSmThresholdToYear() == null))
					|| ((this.getSmThresholdFromYear() == null) && (this
							.getSmThresholdToYear() != null))) {
				throw new EpException(MessageConst.ERROR_REPORT_780016);
			}
			if ((this.getSmThresholdFromYear() != null)
					&& (this.getSmThresholdToYear() != null)) {
				checkOnlyYearFieldDefference(this.getSmThresholdFromYear(),
						this.getSmThresholdToYear());
			}
			if ((this.getSmCreatedFromYear() != null)
					&& (this.getSmCreatedToYear() != null)) {
				checkOnlyYearFieldDefference(this.getSmCreatedFromYear(),
						this.getSmCreatedToYear());
			}
			if ((this.getReportValue() != null && (getManagedBean()
					.getReportValue().trim()
					.equalsIgnoreCase(ReportConst.SM_SUPPLIER_PERFORMANCE)))
					|| (this.getReportValue() != null && (getManagedBean()
							.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_THRESHOLD)))
					|| (this.getReportValue() != null && (getManagedBean()
							.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_THRESHOLD_VALUE)))) {
				if ((this.getSmThresholdFromYear() != null)
						&& (this.getSmThresholdToYear() != null)) {
					checkTwoYearsFieldDefference(this.getSmThresholdFromYear(),
							this.getSmThresholdToYear());
				}
			}
			if (this.getReportValue() != null
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL))) {
				if ((this.getSmCreatedFromYear() != null)
						&& (this.getSmCreatedToYear() != null)) {
					checkTwoYearsFieldDefference(this.getSmCreatedFromYear(),
							this.getSmCreatedToYear());
				}
			}

			if (this.getReportValue() != null
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL))) {
				if ((this.getSmMofRegNo() == null || this.getSmMofRegNo()
						.isEmpty())) {
					// throw new EpException("MOF No. is mandatory.");
					throw new EpException(MessageConst.ERROR_REPORT_780029);
				}
			}

			if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_SELECTION)
					|| this.getModuleValue().equalsIgnoreCase(
							ReportConst.DP_MOD_VAL)) {
				generateSCReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.CATALOGUE_MANAGEMENT)) {
				generateCMReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.ORDER_FULFILLMENT)) {
				generateFLReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_MANAGEMENT)) {
				generateSMReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.CONTRACT_MANAGEMENT)) {
				generateCTReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.PROCUREMENT_PLAN)) {
				generatePPReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.PROFILE_MANAGEMENT)) {
				generatePMReport();
			}
			if (null != constructedParamString
					&& !ReportConst.EMPTY_VAL.equals(constructedParamString)) {
				// try {
				getManagedBean().getRptGenBean().setGenerateButtonView(true);
				// generateReportTimer();
				// generateReportIntime();

				if ((strLanguage != null)
						&& (strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG))) {
					fileGenerateReport(getManagedBean().getReportTitle(),
							constructedParamString);
				} else if ((strLanguage != null)
						&& (strLanguage
								.equalsIgnoreCase(ReportConst.MALAY_LANG))) {
					fileGenerateReport(getManagedBean().getReportTitle(),
							constructedParamString);
				}

				/*
				 * FacesContext.getCurrentInstance().getPartialViewContext().
				 * getRenderIds().add("reportGenDialog.hide()");//execute(
				 * "reportGenDialog.hide()");
				 * RequestContext.getCurrentInstance()
				 * .execute("reportGenDialog.hide()");
				 */
				getManagedBean().getRptGenBean().setGenerateButtonView(false);
				/*
				 * } catch (IOException e) {
				 * getManagedBean().getRptGenBean().setGenerateButtonView
				 * (false); throw new
				 * EpException(MessageConst.ERROR_REPORT_780009); }
				 */
			}
			// InputStream stream =
			// ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/css/images/optimus.jpg");
			// //file = new DefaultStreamedContent(stream, "image/jpg",
			// "downloaded_optimus.jpg");
			// RequestContext.getCurrentInstance().execute("statusDialog.hide()");
			/*
			 * RequestContext context = RequestContext.getCurrentInstance();
			 * context.addCallbackParam(ReportConst.RPT_IN_VALID,
			 * ReportConst.RPT_TRUE);
			 */
			// externalContext.addResponseCookie("primefaces.download", "true",
			// Collections.<String, Object>emptyMap());
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.addResponseCookie("primefaces.download",
					ReportConst.RPT_TRUE,
					Collections.<String, Object> emptyMap());
		} else {
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.addResponseCookie("primefaces.download",
					ReportConst.RPT_TRUE,
					Collections.<String, Object> emptyMap());
			throw new EpException(MessageConst.ERROR_REPORT_780001);
		}
	}

	/**
	 * generateReportIntime - Generate report intime.
	 */
	public void generateReportIntime() {
		log.info("ReportBackingBean-generateReportIntime");
		ExecutorService service = Executors.newSingleThreadExecutor();

		try {
			Runnable rnbl = new Runnable() {
				@Override
				public void run() {
					fileGenerateReport(getManagedBean().getReportTitle(),
							constructedParamString);
				}
			};
			Future<?> f = service.submit(rnbl);
			f.get(1, TimeUnit.MINUTES); // attempt the task for one minutes
		} catch (final InterruptedException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		} catch (final TimeoutException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780020);
		} catch (final ExecutionException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780023);
		} finally {
			service.shutdown();
		}
	}

	/**
	 * generateReportTimer - Generate report timer.
	 */
	public void generateReportTimer() {
		log.info("ReportBackingBean-generateReportTimer");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				fileGenerateReport(getManagedBean().getReportTitle(),
						constructedParamString);
			}
		});
		thread.start();
		long endTimeMillis = System.currentTimeMillis() + 60000;
		while (thread.isAlive()) {
			if (System.currentTimeMillis() > endTimeMillis) {
				getManagedBean().getRptGenBean().setGenerateButtonView(false);
				break;
			}
			try {
				Thread.sleep(50000);
			} catch (InterruptedException t) {
				getManagedBean().getRptGenBean().setGenerateButtonView(false);
				log.info(MessageConst.ERROR_REPORT_780020);
			}
		}
	}

	/**
	 * loadReportWithParams - Load report with constructed paramList.
	 * 
	 * @param paramLst
	 *            the param lst
	 */
	public void loadReportWithParams(List<String> paramLst) {
		log.info("ReportBackingBean-loadReportWithParams");
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		for (int i = 1; i <= paramLst.size(); i++) {
			gnrtdURL.append(paramLst.get(i));
		}
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			req.getSession().setAttribute(ReportConst.GENERATED_URL,
					gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * loadReport - Load report with generated URL.
	 * 
	 * @param gnrtdURL
	 *            the gnrtd url
	 */
	public void loadReport(String gnrtdURL) {
		log.info("ReportBackingBean-loadReport");
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			req.getSession().setAttribute(ReportConst.GENERATED_URL, gnrtdURL);
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * loadReportWithParamsString - Load report with costructed param string.
	 * 
	 * @param paramConstrString
	 *            the param constr string
	 */
	public void loadReportWithParamsString(String paramConstrString) {
		log.info("ReportBackingBean-loadReportWithParamsString");
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString);
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			// String
			// serverDetails="http://"+req.getServerName()+":"+req.getLocalPort();
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			log.info(gnrtdURL.toString());
			req.getSession().setAttribute(ReportConst.GENERATED_URL,
					gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * generateCTReport - Generate CONTRACT MANAGEMENT module reports.
	 */
	public void generateCTReport() {
		log.info("ReportBackingBean-generateCTReport");
		if (((this.getCtContrMontrStatus() != null) && (this
				.getCtContrMontrStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtUserType() != null) && (this.getCtUserType() != ReportConst.EMPTY_VAL))
				|| ((this.getCtSubType() != null) && (this.getCtSubType() != ReportConst.EMPTY_VAL))
				|| ((this.getCtProductService() != null) && (this
						.getCtProductService() != ReportConst.EMPTY_VAL))
				|| ((this.getCtLoanNo() != null) && (this.getCtLoanNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtLOAPhysicalNo() != null) && (this
						.getCtLOAPhysicalNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this
						.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getCtContractNo() != null) && (this.getCtContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtPhysicalContractNo() != null) && (this
						.getCtPhysicalContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtAgreement() != null) && (this.getCtAgreement() != ReportConst.EMPTY_VAL))
				|| ((this.getCtStatus() != null) && (this.getCtStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtStatusTwo() != null) && (this.getCtStatusTwo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtTerminationType() != null) && (this
						.getCtTerminationType() != ReportConst.EMPTY_VAL))
				/*
				 * || ((this.getCtAgreement() != null) &&
				 * (!this.getCtAgreement()
				 * .equals(ReportConst.CT_GROUP_VAL_ALL))) ||
				 * ((this.getCtStatus() != null) &&
				 * (!this.getCtStatus().equals(ReportConst.CT_GROUP_VAL_ALL)))
				 * || ((this.getCtStatusTwo() != null) &&
				 * (!this.getCtStatusTwo()
				 * .equals(ReportConst.CT_GROUP_VAL_ALL)))
				 */
				|| ((this.getCtComplete() != null) && (this.getCtComplete() != ReportConst.EMPTY_VAL))
				|| ((this.getCtCircularNo() != null) && (this.getCtCircularNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtCircularName() != null) && (this
						.getCtCircularName() != ReportConst.EMPTY_VAL))
				|| ((this.getCtCircularType() != null) && (this
						.getCtCircularType() != ReportConst.EMPTY_VAL))
				|| ((this.getCtSupplierName() != null) && (!this
						.getCtSupplierName().equals(
								ReportConst.CT_GROUP_VAL_ALL)))
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getCtCircularEffectiveFromDate() != null) && (getCtCircularEffectiveFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getCtCircularEffectiveToDate() != null) && (getCtCircularEffectiveToDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getCtProcurementMode() != null) && (this
						.getCtProcurementMode().toString() != ReportConst.EMPTY_VAL)))
		/*
		 * || ((this.getCtProcurementMode() != null) &&
		 * (!this.getCtProcurementMode
		 * ().toString().equals(ReportConst.CT_GROUP_VAL_ALL))))
		 */
		{

			if ((this.getCtSupplierName() != null)
					&& (this.getCtSupplierName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtSupplierNameVar(ReportConst.CT_AMP_SUPPLIER_NAME_EQ
								+ this.getCtSupplierName());
			}
			if ((this.getCtCircularNo() != null)
					&& (this.getCtCircularNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtCircularNoVar(ReportConst.CT_AMP_CIRCULAR_NO_EQ
						+ this.getCtCircularNo());
			}
			if ((this.getCtCircularName() != null)
					&& (this.getCtCircularName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtCircularNameVar(ReportConst.CT_AMP_CIRCULAR_NAME_EQ
								+ this.getCtCircularName());
			}
			if ((this.getCtCircularType() != null)
					&& (this.getCtCircularType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtCircularTypeVar(ReportConst.CT_AMP_CIRCULAR_TYPE_EQ
								+ this.getCtCircularType());
			}
			if ((this.getCtTerminationType() != null)
					&& (this.getCtTerminationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtTerminationTypeVar(ReportConst.CT_AMP_TERMINATION_TYPE_EQ
								+ this.getCtTerminationType());
			}
			if ((this.getCtStatus() != null)
					&& (this.getCtStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtStatusVar(ReportConst.CT_AMP_STATUS_EQ
						+ this.getCtStatus());
			}
			if ((this.getCtStatusTwo() != null)
					&& (this.getCtStatusTwo() != ReportConst.EMPTY_VAL)) {
				/*
				 * if(!this.getCtStatusTwo().trim().equalsIgnoreCase("TERMINATE")
				 * ){ rptGenBean.setCtStatusTwoVar(ReportConst.CT_AMP_STATUS_EQ
				 * + this.getCtStatusTwo()); }
				 */
				rptGenBean.setCtStatusTwoVar(ReportConst.CT_AMP_STATUS_EQ
						+ this.getCtStatusTwo());
			}
			if ((this.getCtContractNo() != null)
					&& (this.getCtContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtContractNoVar(ReportConst.CT_AMP_CONTRACT_NO_EQ
						+ this.getCtContractNo());
			}
			if ((this.getCtPhysicalContractNo() != null)
					&& (this.getCtPhysicalContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtPhysicalContractNoVar(ReportConst.CT_AMP_PHYSICALCONTRACT_NO_EQ
								+ this.getCtPhysicalContractNo());
			}

			if ((this.getCtAgreement() != null)
					&& (this.getCtAgreement() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtAgreementVar(ReportConst.CT_AMP_AGREEMENT_EQ
						+ this.getCtAgreement());
			}
			if ((this.getCtComplete() != null)
					&& (this.getCtComplete() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtCompleteVar(ReportConst.CT_AMP_COMPLETE_EQ
						+ this.getCtComplete());
			}
			if ((this.getCtContrMontrStatus() != null)
					&& (this.getCtContrMontrStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtContrMontrStatusVar(ReportConst.CT_AMP_STATUS_EQ
								+ this.getCtContrMontrStatus());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.FL_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.FL_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getCtCircularEffectiveFromDate() != null)
					&& (this.getCtCircularEffectiveFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtCircularEffectiveFromDateVar(ReportConst.CT_AMP_CIRCULAR_FROM_DATE_EQ
								+ outputDateFormat.format(this
										.getCtCircularEffectiveFromDate()));
			}
			if ((this.getCtCircularEffectiveToDate() != null)
					&& (this.getCtCircularEffectiveToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtCircularEffectiveToDateVar(ReportConst.CT_AMP_CIRCULAR_TO_DATE_EQ
								+ outputDateFormat.format(this
										.getCtCircularEffectiveToDate()));
			}
			if ((this.getCtProcurementMode() != null)
					&& (this.getCtProcurementMode() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtProcurementModeVar(ReportConst.CT_AMP_PROCUREMENTMODE_EQ
								+ this.getCtProcurementMode());
			}
			if ((this.getCtGroupOne() != null)
					&& (this.getCtGroupOne() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupOneVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupOne());
			}
			if ((this.getCtGroupTwo() != null)
					&& (this.getCtGroupTwo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupTwoVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupTwo());
			}
			if ((this.getCtGroupThree() != null)
					&& (this.getCtGroupThree() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupThreeVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupThree());
			}
			if ((this.getCtProductService() != null)
					&& (this.getCtProductService() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtProductServiceVar(ReportConst.CT_AMP_PPRODUCTSERVICE_EQ
								+ this.getCtProductService());
			}
			if ((this.getCtLoanNo() != null)
					&& (this.getCtLoanNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtLoanNoVar(ReportConst.CT_AMP_PLOANO_EQ
						+ this.getCtLoanNo());
			}
			if ((this.getCtLOAPhysicalNo() != null)
					&& (this.getCtLOAPhysicalNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtLOAPhysicalNoVar(ReportConst.CT_AMP_PLOAPHYSICALNO_EQ
								+ this.getCtLOAPhysicalNo());
			}
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				/*
				 * Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				 * rptGenBean.setCtMinistryVar(ReportConst.CT_AMP_PMINISTRY_EQ +
				 * retrieveMinistryName(orgprofileid));
				 */
				rptGenBean.setCtMinistryVar(ReportConst.CT_AMP_PMINISTRY_EQ
						+ this.getCommonMinistry());
			}
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtPtjVar(ReportConst.CT_AMP_PPTJNAME_EQ
						+ this.getCommonPtj());
			}
			if ((this.getCtUserType() != null)
					&& (this.getCtUserType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtUserTypeVar(ReportConst.CT_AMP_USERTYPE_EQ
						+ this.getCtUserType());
			}
			if ((this.getCtSubType() != null)
					&& (this.getCtSubType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtSubTypeVar(ReportConst.CT_AMP_SUBTYPE_EQ
						+ this.getCtSubType());
			}

			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getCtContractNoVar()
						+ rptGenBean.getCtPhysicalContractNoVar()
						+ rptGenBean.getCtStatusTwoVar()
						+ rptGenBean.getCtSupplierNameVar()
						+ rptGenBean.getCtTerminationTypeVar()
						+ rptGenBean.getCtAgreementVar()
						+ rptGenBean.getCtStatusVar()
						+ rptGenBean.getCtCompleteVar()
						+ rptGenBean.getCtContrMontrStatusVar()
						+ rptGenBean.getCtGroupOneVar()
						+ rptGenBean.getCtGroupTwoVar()
						+ rptGenBean.getCtGroupThreeVar()
						+ rptGenBean.getCtProductServiceVar()
						+ rptGenBean.getCtMinistryVar()
						+ rptGenBean.getCtPtjVar()
						+ rptGenBean.getCtLoanNoVar()
						+ rptGenBean.getCtLOAPhysicalNoVar()
						+ rptGenBean.getCtUserTypeVar()
						+ rptGenBean.getCtSubTypeVar()
						+ rptGenBean.getCtProcurementModeVar()
						+ rptGenBean.getCtCircularNoVar()
						+ rptGenBean.getCtCircularNameVar()
						+ rptGenBean.getCtCircularTypeVar()
						+ rptGenBean.getCtCircularEffectiveFromDateVar()
						+ rptGenBean.getCtCircularEffectiveToDateVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * generateSMReport - Generate SUPPLIER MANAGEMENT module reports.
	 */
	public void generateSMReport() {
		log.info("ReportBackingBean-generateSMReport");
		if ((getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL))
				|| (getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
				checkSMYearNMonthDefference();
			}
			if ((this.getSmCreatedFromYear() != null)
					&& (this.getSmCreatedFromYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMandatoryCreatedFromYearVar(ReportConst.AMP_SM_YEAR_FROM_EQ
								+ this.getSmCreatedFromYear());
			}
			if ((this.getSmCreatedToYear() != null)
					&& (this.getSmCreatedToYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMandatoryCreatedToYearVar(ReportConst.AMP_SM_YEAR_TO_EQ
								+ this.getSmCreatedToYear());
			}
			if ((this.getSmReportType() != null)
					&& (this.getSmReportType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setReportTypeTotVar(ReportConst.AMP_SM_REPORT_TYPE_EQ
								+ this.getSmReportType());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.AMP_SM_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.AMP_SM_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getSmFromMonth() != null)
					&& (this.getSmFromMonth() != ReportConst.EMPTY_VAL)) {
				String month = getManagedBean().getMonthMap().get(
						this.getSmFromMonth());
				rptGenBean.setSmFromMonthVar(ReportConst.AMP_SM_FROM_DATE_EQ
						+ month);
			}
			if ((this.getSmToMonth() != null)
					&& (this.getSmToMonth() != ReportConst.EMPTY_VAL)) {
				String month = getManagedBean().getMonthMap().get(
						this.getSmToMonth());
				rptGenBean.setSmToMonthVar(ReportConst.AMP_SM_TO_DATE_EQ
						+ month);
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getSmMandatoryCreatedFromYearVar()
						+ rptGenBean.getSmMandatoryCreatedToYearVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getReportTypeTotVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getSmFromMonthVar()
						+ rptGenBean.getSmToMonthVar()
						+ rptGenBean.getUserIdVar());

			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} /*
		 * else if ((getManagedBean().getReportValue() != null) &&
		 * (getManagedBean().getReportValue().trim()
		 * .equalsIgnoreCase(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL))) {
		 * 
		 * rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp()); if
		 * ((rptGenBean.getUrlReportJSPName() != null) &&
		 * (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
		 * setConstructedParamString(ReportConst.AMP_REPORT_NAME +
		 * rptGenBean.getUrlReportJSPName() + rptGenBean.getReportValueIdVar() +
		 * rptGenBean.getUserIdVar()); } else { throw new
		 * EpException(MessageConst.ERROR_REPORT_780007); } }
		 */else if (((this.getSmYearText() != null) && (this.getSmYearText() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationType() != null) && (this
						.getSmApplicationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMonthlyRptByYear() != null) && (this
						.getSmMonthlyRptByYear() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaymentType() != null) && (this
						.getSmPaymentType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaymentTo() != null) && (this.getSmPaymentTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCompanyName() != null) && (this
						.getSmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationType() != null) && (this
						.getSmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationNo() != null) && (this
						.getSmApplicationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmZone() != null) && (this.getSmZone() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupplierType() != null) && (this
						.getSmSupplierType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNo() != null) && (this.getSmMofRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationStatus() != null) && (this
						.getSmRegistrationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMainCategory() != null) && (this
						.getSmMainCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSubCategory() != null) && (this
						.getSmSubCategory() != ReportConst.EMPTY_VAL))// this.getSelectedCatList()
				// || ((this.getSelectedCatList() != null) &&
				// (this.getSelectedCatList().size()> 0))
				|| (((getManagedBean().getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL))
						|| (getManagedBean().getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL))
						|| (getManagedBean().getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL))
						|| (getManagedBean().getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL)) || (getManagedBean()
						.getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) && (this
						.getSelectedCatList().size() > 0))
				|| ((this.getSmSubCategoryDivision() != null) && (this
						.getSmSubCategoryDivision() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationStatus() != null) && (this
						.getSmApplicationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmState() != null) && (this.getSmState() != ReportConst.EMPTY_VAL))
				|| ((this.getSmDistrict() != null) && (this.getSmDistrict() != ReportConst.EMPTY_VAL))
				|| ((this.getSmReportType() != null) && (this.getSmReportType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNoFrom() != null) && (this
						.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNoTo() != null) && (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalFrm() != null) && (this
						.getSmPaidUpCapitalFrm() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalTo() != null) && (this
						.getSmPaidUpCapitalTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalNaccum() != null) && (this
						.getSmPaidUpCapitalNaccum() != ReportConst.EMPTY_VAL))
				// || ((this.getSmRole() != null) && (this.getSmRole() !=
				// ReportConst.EMPTY_VAL))
				|| ((this.getSmBusCompTypeRegistrationStatus() != null) && (this
						.getSmBusCompTypeRegistrationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmBusCompanyType() != null) && (this
						.getSmBusCompanyType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmBusPaidUpCapitalFrm() != null) && (this
						.getSmBusPaidUpCapitalFrm() != ReportConst.EMPTY_VAL))
				|| ((this.getSmBusPaidUpCapitalTo() != null) && (this
						.getSmBusPaidUpCapitalTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmEpNo() != null) && (this.getSmEpNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationNo() != null) && (this
						.getSmRegistrationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmGSTRegistrationNo() != null) && (this
						.getSmGSTRegistrationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmGSTRegisteredStatus() != null) && (this
						.getSmGSTRegisteredStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupportingDocumentMode() != null) && (this
						.getSmSupportingDocumentMode() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupportingDocumentStatus() != null) && (this
						.getSmSupportingDocumentStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPersonnelRole() != null) && (this
						.getSmPersonnelRole() != ReportConst.EMPTY_VAL))
				|| ((this.getSmIdentificationType() != null) && (this
						.getSmIdentificationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmIdentificationNumber() != null) && (this
						.getSmIdentificationNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCancellationBy() != null) && (this
						.getSmCancellationBy() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmUserName() != null) && (this.getSmUserName()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRequestExpiryDate() != null) && (this
						.getSmRequestExpiryDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmProfileStatus() != null) && (this
						.getSmProfileStatus().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmThresholdFromYear() != null) && (this
						.getSmThresholdFromYear().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmThresholdToYear() != null) && (this
						.getSmThresholdToYear().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRTReportType() != null) && (this
						.getSmRTReportType().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupplierName() != null) && (this
						.getSmSupplierName().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmReceiptNo() != null) && (this.getSmReceiptNo()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaymentMethod() != null) && (this
						.getSmPaymentMethod().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCode() != null) && (this.getSmCode().toString() != ReportConst.EMPTY_VAL))
				|| (this.getSmMofRegistrationFeeList() != null && this
						.getSmMofRegistrationFeeList().size() > 0)
				|| (this.getSmProcessingFeeList() != null && this
						.getSmProcessingFeeList().size() > 0)
				|| (this.getSmSoftCertificateFeeList() != null && this
						.getSmSoftCertificateFeeList().size() > 0)) {

			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_APPLICATION_APPROVED_RPT_VAL))) {
				if (((this.getSmCompanyName() == null) || (this
						.getSmCompanyName().equals(ReportConst.EMPTY_VAL)))
						&& ((this.getSmMofRegNo() == null) || (this
								.getSmMofRegNo().equals(ReportConst.EMPTY_VAL)))
						&& ((this.getFromDate() == null) || (this.getFromDate()
								.toString().equals(ReportConst.EMPTY_VAL)))
						&& ((this.getToDate() == null) || (this.getToDate()
								.toString().equals(ReportConst.EMPTY_VAL)))) {
					throw new EpException(MessageConst.ERROR_REPORT_780003);
				}
			}

			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_SUPPLIER_PERFORMANCE))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_THRESHOLD))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_THRESHOLD_VALUE))) {
				if ((this.getSmThresholdFromYear() != null)
						&& (this.getSmThresholdFromYear() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setSmThresholdFromYearVar(ReportConst.AMP_SM_FROM_YEAR_EQ
									+ this.getSmThresholdFromYear());
				}
				if ((this.getSmThresholdToYear() != null)
						&& (this.getSmThresholdToYear() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setSmThresholdToYearVar(ReportConst.AMP_SM_TO_YEAR_EQ
									+ this.getSmThresholdToYear());
				}
			}

			if (getManagedBean().getReportValue() != null) {
				if (getManagedBean()
						.getReportValue()
						.trim()
						.equalsIgnoreCase(
								ReportConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL)
						|| getManagedBean()
								.getReportValue()
								.trim()
								.equalsIgnoreCase(
										ReportConst.SM_COLLECTION_PAYMENT_METHODS_RPT_VAL)
						|| getManagedBean()
								.getReportValue()
								.trim()
								.equalsIgnoreCase(
										ReportConst.SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL)) {

					if ((this.getSmCode() != null)
							&& (this.getSmCode() != ReportConst.EMPTY_VAL)) {
						rptGenBean.setSmCodeVar(ReportConst.AMP_SM_OCDE_TYPE
								+ this.getSmCode());
					}

					if ((this.getSmPaymentMethod() != null)
							&& (this.getSmPaymentMethod() != ReportConst.EMPTY_VAL)) {
						rptGenBean
								.setSmPaymentMethodVar(ReportConst.AMP_SM_PAYMENT_TYPE_METHOD_EQ
										+ this.getSmPaymentMethod());
					}

					if ((this.getSmRTReportType() != null)
							&& (this.getSmRTReportType() != ReportConst.EMPTY_VAL)) {
						rptGenBean
								.setSmRTReportTypeVar(ReportConst.AMP_SM_REP_TYPE_EQ
										+ this.getSmRTReportType());
					}

					if ((this.getSmSupplierName() != null)
							&& (this.getSmSupplierName() != ReportConst.EMPTY_VAL)) {
						rptGenBean
								.setSmSupplierNameVar(ReportConst.AMP_SM_SUP_NAME_EQ
										+ this.getSmSupplierName());
					}

					if (this.getSmMofRegistrationFeeList() != null
							&& this.getSmMofRegistrationFeeList().size() > 0) {
						rptGenBean
								.setSmMofRegistrationFeeVar(ReportConst.AMP_SM_MOF_REG_FEE_TYPE_EQ
										+ this.getSmMofRegistrationFeeList()
												.get(0));
					}

					if (this.getSmProcessingFeeList() != null
							&& this.getSmProcessingFeeList().size() > 0) {
						rptGenBean
								.setSmProcessingFeeVar(ReportConst.AMP_SM_PROCESSING_FEE_TYPE_EQ
										+ this.getSmProcessingFeeList().get(0));
					}

					if (this.getSmSoftCertificateFeeList() != null
							&& this.getSmSoftCertificateFeeList().size() > 0) {
						rptGenBean
								.setSmSoftCertificateFeeVar(ReportConst.AMP_SM_SOFTCERTIFICATE_TYPE_EQ
										+ this.getSmSoftCertificateFeeList()
												.get(0));
					}

				}
			}

			if ((this.getSmProfileStatus() != null)
					&& (this.getSmProfileStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmProfileStatusVar(ReportConst.AMP_SM_PRFL_STS_EQ
						+ this.getSmProfileStatus());
			}
			if ((this.getSmGSTRegistrationNo() != null)
					&& (this.getSmGSTRegistrationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmGSTRegistrationNoVar(ReportConst.AMP_SM_GST_REGNO_EQ
								+ this.getSmGSTRegistrationNo());
			}
			if ((this.getSmGSTRegisteredStatus() != null)
					&& (this.getSmGSTRegisteredStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmGSTRegisteredStatusVar(ReportConst.AMP_SM_GST_STS_EQ
								+ this.getSmGSTRegisteredStatus());
			}
			if ((this.getSmUserName() != null)
					&& (this.getSmUserName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmUserNameVar(ReportConst.AMP_SM_USER_NAME_EQ
						+ this.getSmUserName());
			}
			if ((this.getSmRequestExpiryDate() != null)
					&& (this.getSmRequestExpiryDate() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmRequestExpiryDateVar(ReportConst.AMP_SM_REQ_ENQ_DATE_EQ
								+ this.getSmRequestExpiryDate());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.AMP_SM_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.AMP_SM_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getMofAccountEndDateFromDate() != null)
					&& (this.getMofAccountEndDateFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setMofAccountEndDateFromDateVar(ReportConst.AMP_SM_MOF_ACC_END_FROM_DATE_EQ
								+ outputDateFormat.format(this
										.getMofAccountEndDateFromDate()));
			}
			if ((this.getMofAccountEndDateToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setMofAccountEndDateToDateVar(ReportConst.AMP_SM_MOF_ACC_END_TO_DATE_EQ
								+ outputDateFormat.format(this
										.getMofAccountEndDateToDate()));
			}

			if ((this.getSmMainCategory() != null)
					&& (this.getSmMainCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setMainCategoryTotVar(ReportConst.AMP_SM_MAIN_CATEGORY_EQ
								+ this.getSmMainCategory());
			}
			if ((this.getSmSubCategory() != null)
					&& (this.getSmSubCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSubCategoryTotVar(ReportConst.AMP_SM_SUB_CATEGORY_EQ
								+ this.getSmSubCategory());
			}
			if ((this.getSmSubCategoryDivision() != null)
					&& (this.getSmSubCategoryDivision() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSubCategoryDivisionTotVar(ReportConst.AMP_SM_SUB_DIV_CATEGORY_EQ
								+ this.getSmSubCategoryDivision());
			}
			if ((this.getSmRegistrationType() != null)
					&& (this.getSmRegistrationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationTypeTotVar(ReportConst.AMP_SM_REG_TYPE_EQ
								+ this.getSmRegistrationType());
			}
			if ((this.getSmMofRegNo() != null)
					&& (this.getSmMofRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMofRegNoTotVar(ReportConst.AMP_SM_MOF_REG_NO_EQ
						+ this.getSmMofRegNo());
			}
			if ((this.getSmApplicationStatus() != null)
					&& (this.getSmApplicationStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationStatusTotVar(ReportConst.AMP_SM_APPL_STATUS_EQ
								+ this.getSmApplicationStatus());
			}
			if ((this.getSmPaymentTo() != null)
					&& (this.getSmPaymentTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmPaymentToVar(ReportConst.AMP_SM_PAYMENT_TO_EQ
						+ this.getSmPaymentTo());
			}
			if ((this.getSmCompanyName() != null)
					&& (this.getSmCompanyName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_SM_COMP_NAME_EQ
						+ this.getSmCompanyName());
			}
			if ((this.getSmRegistrationStatus() != null)
					&& (this.getSmRegistrationStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationStatusTotVar(ReportConst.AMP_SM_REG_STATUS_EQ
								+ this.getSmRegistrationStatus());
			}
			if ((this.getSmState() != null)
					&& (this.getSmState() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setStateTotVar(ReportConst.AMP_SM_STATE_NAME_EQ
						+ this.getSmState());
			}
			if ((this.getSmDistrict() != null)
					&& (this.getSmDistrict() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDistrictTotVar(ReportConst.AMP_SM_DISTRICT_NAME_EQ
								+ this.getSmDistrict());
			}
			if ((this.getSmSupplierType() != null)
					&& (this.getSmSupplierType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSupplierTypeTotVar(ReportConst.AMP_SM_SUPPLIER_TYPE_EQ
								+ this.getSmSupplierType());
			}
			if ((this.getSmZone() != null)
					&& (this.getSmZone() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setZoneTotVar(ReportConst.AMP_SM_SUPPLIER_ZONE_NAME_EQ
								+ this.getSmZone());
			}
			if ((this.getSmReportType() != null)
					&& (this.getSmReportType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setReportTypeTotVar(ReportConst.AMP_SM_REPORT_TYPE_EQ
								+ this.getSmReportType());
			}

			if ((this.getSmPaymentMethod() != null)
					&& (this.getSmPaymentMethod() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPaymentMethodVar(ReportConst.AMP_SM_PAYMENT_MENTHOD_EQ
								+ this.getSmPaymentMethod());
			}
			if ((this.getSmProcessingType() != null)
					&& (this.getSmProcessingType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmProcessingTypeVar(ReportConst.AMP_SM_PRO_TYPE_EQ
								+ this.getSmProcessingType());
			}
			if ((this.getSmReceiptNo() != null)
					&& (this.getSmReceiptNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmReceiptNoVar(ReportConst.AMP_SM_RECEIPT_NO_EQ
						+ this.getSmReceiptNo());
			}
			if ((this.getSmProcessingOfficerName() != null)
					&& (this.getSmProcessingOfficerName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmProcessingOfficerNameVar(ReportConst.AMP_SM_PRO_OFFICERNAME_EQ
								+ this.getSmProcessingOfficerName());
			}

			if ((this.getSmApplicationNo() != null)
					&& (this.getSmApplicationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationNoTotVar(ReportConst.AMP_SM_APPLICATION_NO_EQ
								+ this.getSmApplicationNo());
			}
			if ((this.getSmPaymentType() != null)
					&& (this.getSmPaymentType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPaymentTypeTotVar(ReportConst.AMP_SM_PAYMENT_TYPE_EQ
								+ this.getSmPaymentType());
			}
			if ((this.getSmApplicationType() != null)
					&& (this.getSmApplicationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationTypeTotVar(ReportConst.AMP_SM_APPL_TYPE_EQ
								+ this.getSmApplicationType());
			}
			if ((this.getSmSVOUser() != null)
					&& (this.getSmSVOUser() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmSVOUserVar(ReportConst.AMP_SM_APPL_TYPE_EQ
						+ this.getSmSVOUser());
			}
			if ((this.getSmYearText() != null)
					&& (this.getSmYearText() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmYearTextVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getSmYearText());
			}
			if ((this.getSmMonthlyRptByYear() != null)
					&& (this.getSmMonthlyRptByYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmMonthlyRptByYearVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getSmMonthlyRptByYear());
			}
			if ((this.getSmMofRegNoFrom() != null && (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL))
					&& (this.getSmMofRegNoTo() != null && (this
							.getSmMofRegNoTo() != ReportConst.EMPTY_VAL))) {
				String mofRegNoFrom = null;
				String mofRegNoTo = null;
				if (this.getSmMofRegNoFrom().contains(ReportConst.PARAM_IFON)) {
					mofRegNoFrom = this.getSmMofRegNoFrom().replace(
							ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
					if (mofRegNoFrom.contains(ReportConst.PARAM_SPACE)) {
						mofRegNoFrom = mofRegNoFrom.replace(
								ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
					}
				} else {
					mofRegNoFrom = this.getSmMofRegNoFrom();
				}

				if (this.getSmMofRegNoTo().contains(ReportConst.PARAM_IFON)) {
					mofRegNoTo = this.getSmMofRegNoTo().replace(
							ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
					if (mofRegNoTo.contains(ReportConst.PARAM_SPACE)) {
						mofRegNoTo = mofRegNoTo.replace(
								ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
					}
				} else {
					mofRegNoTo = this.getSmMofRegNoTo();
				}

				ValidateMofRegNo(mofRegNoFrom, mofRegNoTo);
			}
			if ((this.getSmMofRegNoFrom() != null)
					&& (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMofRegNoFromVar(ReportConst.AMP_SM_MOF_REG_NO_FRM_EQ
								+ this.getSmMofRegNoFrom());
			}
			if ((this.getSmMofRegNoTo() != null)
					&& (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMofRegNoToVar(ReportConst.AMP_SM_MOF_REG_NO_TO_EQ
								+ this.getSmMofRegNoTo());
			}
			if ((this.getSmPaidUpCapitalFrm() != null)
					&& (this.getSmPaidUpCapitalFrm() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPaidUpCapitalFrmVar(ReportConst.AMP_SM_PAIDUP_CPTL_FROM_EQ
								+ this.getSmPaidUpCapitalFrm());
			}
			if ((this.getSmPaidUpCapitalTo() != null)
					&& (this.getSmPaidUpCapitalTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPaidUpCapitalToVar(ReportConst.AMP_SM_PAIDUP_CPTL_TO_EQ
								+ this.getSmPaidUpCapitalTo());
			}
			if ((this.getSmPaidUpCapitalNaccum() != null)
					&& (this.getSmPaidUpCapitalNaccum() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPaidUpCapitalNaccumVar(ReportConst.AMP_SM_PAIDUP_CPTL_R_ACLTD_EQ
								+ this.getSmPaidUpCapitalNaccum());
			}
			if ((this.getSmEpNo() != null)
					&& (this.getSmEpNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmEpNoVar(ReportConst.AMP_SM_EP_NO_EQ
						+ this.getSmEpNo());
			}
			if ((this.getSmRegistrationNo() != null)
					&& (this.getSmRegistrationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmRegistrationNoVar(ReportConst.AMP_SM_REG_NO_EQ
						+ this.getSmRegistrationNo());
			}
			if ((this.getSmSupportingDocumentMode() != null)
					&& (this.getSmSupportingDocumentMode() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmSupportingDocumentModeVar(ReportConst.AMP_SM_SUPP_DOC_MODE_EQ
								+ this.getSmSupportingDocumentMode());
			}
			if ((this.getSmSupportingDocumentStatus() != null)
					&& (this.getSmSupportingDocumentStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmSupportingDocumentStatusVar(ReportConst.AMP_SM_SUPP_DOC_STATUS_EQ
								+ this.getSmSupportingDocumentStatus());
			}
			if ((this.getSmPersonnelRole() != null)
					&& (this.getSmPersonnelRole() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPersonnelRoleVar(ReportConst.AMP_SM_PERSONNEL_ROLE_EQ
								+ this.getSmPersonnelRole());
			}
			if ((this.getSmIdentificationType() != null)
					&& (this.getSmIdentificationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmIdentificationTypeVar(ReportConst.AMP_SM_IDENT_TYPE_EQ
								+ this.getSmIdentificationType());
			}
			if ((this.getSmIdentificationNumber() != null)
					&& (this.getSmIdentificationNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmIdentificationNumberVar(ReportConst.AMP_SM_IDENT_NO_EQ
								+ this.getSmIdentificationNumber());
			}
			if ((this.getSmCancellationBy() != null)
					&& (this.getSmCancellationBy() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmCancellationByVar(ReportConst.AMP_SM_CANCEL_BY_EQ
								+ this.getSmCancellationBy());
			}
			if ((this.getSmBusCompTypeRegistrationStatus() != null)
					&& (this.getSmBusCompTypeRegistrationStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmBusCompTypeRegistrationStatusByVar(ReportConst.AMP_SM_BUS_REG_TYPE_BY_EQ
								+ this.getSmBusCompTypeRegistrationStatus());
			}

			if ((this.getSmBusCompanyType() != null)
					&& (this.getSmBusCompanyType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmBusCompanyTypeByVar(ReportConst.AMP_SM_BUS_TYPE_BY_EQ
								+ this.getSmBusCompanyType());
			}
			if ((this.getSmBusPaidUpCapitalFrm() != null)
					&& (this.getSmBusPaidUpCapitalFrm() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmBusPaidUpCapitalFrmByVar(ReportConst.AMP_SM_BUS_TYPE_BY_EQ
								+ this.getSmBusPaidUpCapitalFrm());
			}
			if ((this.getSmBusPaidUpCapitalTo() != null)
					&& (this.getSmBusPaidUpCapitalTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmBusPaidUpCapitalToByVar(ReportConst.AMP_SM_BUS_TYPE_BY_EQ
								+ this.getSmBusPaidUpCapitalTo());
			}

			StringBuffer maincategory = new StringBuffer();
			StringBuffer subcategory = new StringBuffer();
			StringBuffer subcategorydivision = new StringBuffer();
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {
				maincategory.append(ReportConst.AMP_SM_MAIN_CATEGORY_EQ);
				subcategory.append(ReportConst.AMP_SM_SUB_CATEGORY_EQ);
				subcategorydivision
						.append(ReportConst.AMP_SM_SUB_DIV_CATEGORY_EQ);
				for (SmSupplierCategory smSupplier : this.getSelectedCatList()) {

					if (smSupplier.getPmCategoryL1().getCategoryL1Id() != null
							&& smSupplier.getPmCategoryL2().getCategoryL2Id() != null
							&& smSupplier.getPmCategoryL3().getCategoryL3Id() != null) {
						maincategory.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL1()
										.getCategoryL1Id());
						subcategory.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL2()
										.getCategoryL2Id());
						subcategorydivision.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL3()
										.getCategoryL3Id());
					} else if (smSupplier.getPmCategoryL1().getCategoryL1Id() != null
							&& (smSupplier.getPmCategoryL2().getCategoryL2Id() == null
									|| smSupplier.getPmCategoryL2()
											.getCategoryL2Id() != null
									|| smSupplier.getPmCategoryL3()
											.getCategoryL3Id() == null || smSupplier
									.getPmCategoryL3().getCategoryL3Id() != null)) {
						maincategory.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL1()
										.getCategoryL1Id());
						subcategory.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL2()
										.getCategoryL2Id());
						subcategorydivision.append(ReportConst.KAMA
								+ smSupplier.getPmCategoryL3()
										.getCategoryL3Id());
					}

					/*
					 * else if ((smSupplier.getPmCategoryL1().getCategoryL1Id()
					 * != null || smSupplier.getPmCategoryL2().getCategoryL2Id()
					 * == null || smSupplier
					 * .getPmCategoryL3().getCategoryL3Id() == null) ||
					 * (smSupplier.getPmCategoryL2().getCategoryL2Id() != null
					 * || smSupplier.getPmCategoryL1() .getCategoryL1Id() ==
					 * null || smSupplier .getPmCategoryL3().getCategoryL3Id()
					 * == null) ||
					 * (smSupplier.getPmCategoryL3().getCategoryL3Id() != null
					 * || smSupplier.getPmCategoryL1() .getCategoryL1Id() ==
					 * null || smSupplier .getPmCategoryL2().getCategoryL2Id()
					 * == null)) { throw new
					 * EpException(MessageConst.ERROR_REPORT_780028); }
					 */
				}
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getExtentionTypeTotVar()
						+ rptGenBean.getExtentionBrandTotVar()
						+ rptGenBean.getExtentionMeasurmentTotVar()
						+ rptGenBean.getExtentionColourTotVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getSmPaymentToVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getSmBusCompTypeRegistrationStatusByVar()
						+ rptGenBean.getSmBusCompanyTypeByVar()
						+ rptGenBean.getSmBusPaidUpCapitalFrmByVar()
						+ rptGenBean.getSmBusPaidUpCapitalToByVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getEpNumberTotVar()
						+ rptGenBean.getBusinessRegNoTotVar()
						+ rptGenBean.getIcNumberTotVar()
						+ rptGenBean.getSmRTReportTypeVar()
						+ rptGenBean.getSmCodeVar()
						+ rptGenBean.getSmSupplierNameVar()
						+ rptGenBean.getSmMofRegistrationFeeVar()
						+ rptGenBean.getSmProcessingFeeVar()
						+ rptGenBean.getSmSoftCertificateFeeVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getMofAccountEndDateFromDateVar()
						+ rptGenBean.getMofAccountEndDateToDateVar()
						+ rptGenBean.getApplicationNoTotVar()
						+ rptGenBean.getZoneTotVar()
						+ rptGenBean.getSupplierTypeTotVar()
						+ rptGenBean.getMofRegNoTotVar()
						+ rptGenBean.getRegistrationStatusTotVar()
						+ rptGenBean.getSmMofRegNoFromVar()
						+ rptGenBean.getSmMofRegNoToVar()
						+ rptGenBean.getSmPaidUpCapitalFrmVar()
						+ rptGenBean.getSmPaidUpCapitalToVar()
						+ rptGenBean.getSmPaidUpCapitalNaccumVar()
						+ rptGenBean.getSmProfileStatusVar()
						// + rptGenBean.getSmRoleVar()
						+ rptGenBean.getSmGSTRegistrationNoVar()
						+ rptGenBean.getSmGSTRegisteredStatusVar()
						+ rptGenBean.getSmEpNoVar()
						+ rptGenBean.getSmRegistrationNoVar()
						+ rptGenBean.getSmSupportingDocumentModeVar()
						+ rptGenBean.getSmSupportingDocumentStatusVar()
						+ rptGenBean.getSmPersonnelRoleVar()
						+ rptGenBean.getSmIdentificationTypeVar()
						+ rptGenBean.getSmIdentificationNumberVar()
						+ rptGenBean.getSmCancellationByVar() + maincategory
						+ subcategory + subcategorydivision
						+ rptGenBean.getApplicationStatusTotVar()
						+ rptGenBean.getSmYearTextVar()
						+ rptGenBean.getSmMonthlyRptByYearVar()
						+ rptGenBean.getPaymentTypeTotVar()
						+ rptGenBean.getSmPaymentMethodVar()
						+ rptGenBean.getSmProcessingTypeVar()
						+ rptGenBean.getSmReceiptNoVar()
						+ rptGenBean.getSmThresholdFromYearVar()
						+ rptGenBean.getSmThresholdToYearVar()
						+ rptGenBean.getSmUserNameVar()
						+ rptGenBean.getSmRequestExpiryDateVar()
						+ rptGenBean.getSmProcessingOfficerNameVar()
						+ rptGenBean.getApplicationTypeTotVar()
						+ rptGenBean.getStateTotVar()
						+ rptGenBean.getDistrictTotVar()
						+ rptGenBean.getReportTypeTotVar()
						+ rptGenBean.getSmSVOUserVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else if (!getManagedBean().getRptGenBean()
				.isSmMandatoryDOCSupplierTypeView()) {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * generateSCReport - Generate SOURCING(SUPPLIER SELECTION and DIRECT
	 * PURCHASE) module reports.
	 */
	public void generateSCReport() {
		log.info("ReportBackingBean-generateSCReport");
		/*
		 * if ((getManagedBean().getReportValue() != null) &&
		 * (getManagedBean().getReportValue().trim()
		 * .equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL)) ||
		 * (getManagedBean().getReportValue() != null) &&
		 * (getManagedBean().getReportValue().trim()
		 * .equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))) {
		 * rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp()); if
		 * ((rptGenBean.getUrlReportJSPName() != null) &&
		 * (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
		 * setConstructedParamString(ReportConst.AMP_REPORT_NAME +
		 * rptGenBean.getUrlReportJSPName() + rptGenBean.getReportValueIdVar() +
		 * rptGenBean.getUserIdVar()); } else { throw new
		 * EpException(MessageConst.ERROR_REPORT_780007); } } else
		 */

		if (((getScQuotationNo() != null) && (getScQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdFromDate() != null) && (getScSoneCrtdFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdToDate() != null) && (getScSoneCrtdToDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPIno() != null) && (getScPIno() != ReportConst.EMPTY_VAL))
				|| ((getScRequisenerId() != null) && (getScRequisenerId() != ReportConst.EMPTY_VAL))
				|| ((getScSupplier() != null) && (getScSupplier() != ReportConst.EMPTY_VAL))
				|| ((getScSoneStatus() != null) && (getScSoneStatus() != ReportConst.EMPTY_VAL))
				|| (this.getScProcureAllList().size() > 0)
				|| (this.getScQuotationList().size() > 0)
				|| (this.getScTenderList().size() > 0)
				|| (this.getScRstrctdTndrList().size() > 0)
				|| ((getScCreatedToYear() != null) && (getScCreatedToYear()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScCreatedFromYear() != null) && (getScCreatedFromYear()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScDeskOfficer() != null) && (getScDeskOfficer()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPtj() != null) && (getScPtj().toString() != ReportConst.EMPTY_VAL))
				|| ((getScStatus() != null) && (getScStatus().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishFromDate() != null) && (getScPublishFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishToDate() != null) && (getScPublishToDate()
						.toString() != ReportConst.EMPTY_VAL))
				/*
				 * || ((getScPrcRptGroup() != null) && (getScPrcRptGroup() !=
				 * ReportConst.EMPTY_VAL)) || ((getScPIDtlsPrcRptGroup() !=
				 * null) && (getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL))
				 */
				|| ((getScProcurementType() != null) && (getScProcurementType() != ReportConst.EMPTY_VAL))
				|| ((getScProcessRptQuotationNo() != null) && (getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getScCommonStatus() != null) && (getScCommonStatus() != ReportConst.EMPTY_VAL))
				|| ((getScSimpleQuoteStatus() != null) && (getScSimpleQuoteStatus() != ReportConst.EMPTY_VAL))
				|| ((getScPurchaseInDefaultStatus() != null) && (getScPurchaseInDefaultStatus() != ReportConst.EMPTY_VAL))
				|| ((getScRequestNoteStatus() != null) && (getScRequestNoteStatus() != ReportConst.EMPTY_VAL))
				|| ((getScLAPRNPURCHDEFAULTGroup() != null) && (getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL))
				|| ((getScPIDNo() != null) && (getScPIDNo() != ReportConst.EMPTY_VAL))
				|| ((getScSOAStatus() != null) && (getScSOAStatus() != ReportConst.EMPTY_VAL))
				|| ((getScSimpleQuoteType() != null) && (getScSimpleQuoteType() != ReportConst.EMPTY_VAL))
				|| ((getScLOANo() != null) && (getScLOANo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPType() != null) && (getScContractOrPKPType() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPNo() != null) && (getScContractOrPKPNo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPName() != null) && (getScContractOrPKPName() != ReportConst.EMPTY_VAL))
				|| ((getScRequestQuoteNo() != null) && (getScRequestQuoteNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getScCreatedToMonth() == null)
					&& (this.getScCreatedFromMonth() != null)
					|| (this.getScCreatedToMonth() != null)
					&& (this.getScCreatedFromMonth() == null)) {
				throw new EpException(MessageConst.ERROR_REPORT_780004);
			}
			/*
			 * if ((this.getScCreatedToMonth() != null) &&
			 * (this.getScCreatedFromMonth() != null)) {
			 */
			if ((this.getScCreatedToYear() != null)
					&& (this.getScCreatedFromYear() != null)) {
				if (Integer.parseInt(this.getScCreatedToYear()) < Integer
						.parseInt(this.getScCreatedFromYear())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				} else if (Integer.parseInt(this.getScCreatedToYear()) == Integer
						.parseInt(this.getScCreatedFromYear())) {
					if ((this.getScCreatedToMonth() != null)
							&& (this.getScCreatedFromMonth() != null)) {
						if (Integer.parseInt(this.getScCreatedToMonth()) < Integer
								.parseInt(this.getScCreatedFromMonth())) {
							throw new EpException(
									MessageConst.ERROR_REPORT_780008);
						}
					}
				}
				if ((this.getScCreatedToMonth() != null)
						&& (this.getScCreatedFromMonth() != null)) {
					rptGenBean
							.setScCreatedFromDtVar(ReportConst.AMP_CREAT_FRM_DT_EQ
									+ this.getScCreatedFromMonth()
									+ this.getScCreatedFromYear());
					rptGenBean
							.setScCreatedToDtVar(ReportConst.AMP_CREAT_TO_DT_EQ
									+ this.getScCreatedToMonth()
									+ this.getScCreatedToYear());
				} else {
					rptGenBean
							.setScCreatedFromDtVar(ReportConst.AMP_CREAT_FRM_DT_EQ
									+ this.getScCreatedFromYear());
					rptGenBean
							.setScCreatedToDtVar(ReportConst.AMP_CREAT_TO_DT_EQ
									+ this.getScCreatedToYear());
				}
			}
			// }

			if ((this.getScSOAStatus() != null)
					&& (this.getScSOAStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScSOAStatusVar(ReportConst.AMP_PROCURE_STATUS_EQ
						+ this.getScSOAStatus());
			}
			if ((this.getScSimpleQuoteType() != null)
					&& (this.getScSimpleQuoteType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSimpleQuoteTypeVar(ReportConst.SC_AMP_SIMPL_QUOTE_TYPE_EQ
								+ this.getScSimpleQuoteType());
			}
			if ((this.getScLOANo() != null)
					&& (this.getScLOANo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScLOANoVar(ReportConst.SC_AMP_LOA_NO_EQ
						+ this.getScLOANo());
			}
			if ((this.getScRequestQuoteNo() != null)
					&& (this.getScRequestQuoteNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScRequestQuoteNoVar(ReportConst.SC_AMP_RQ_NO_EQ
						+ this.getScRequestQuoteNo());
			}
			if ((this.getScContractOrPKPName() != null)
					&& (this.getScContractOrPKPName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScContractOrPKPNameVar(ReportConst.SC_AMP_CON_M_EQ
								+ this.getScContractOrPKPName());
			}
			if ((this.getScContractOrPKPNo() != null)
					&& (this.getScContractOrPKPNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScContractOrPKPNoVar(ReportConst.SC_AMP_CON_NO_EQ
						+ this.getScContractOrPKPNo());
			}
			if ((this.getScContractOrPKPType() != null)
					&& (this.getScContractOrPKPType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScContractOrPKPTypeVar(ReportConst.SC_AMP_CON_TYPE_EQ
								+ this.getScContractOrPKPType());
			}
			if ((this.getScPIDNo() != null)
					&& (this.getScPIDNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPIDNoVar(ReportConst.SC_AMP_PID_NO_EQ
						+ this.getScPIDNo());
			}
			if ((this.getReportValue() != null)
					&& (this.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL))) {
				if ((this.getScREQUESTNOTESUMMARYGroup() != null)
						&& (this.getScREQUESTNOTESUMMARYGroup() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScREQUESTNOTESUMMARYGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
									+ this.getScREQUESTNOTESUMMARYGroup());
				}
			}

			if ((this.getScLAPRNPURCHDEFAULTGroup() != null)
					&& (this.getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScLAPRNPURCHDEFAULTGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScLAPRNPURCHDEFAULTGroup());
			}
			if ((this.getScSimpleQuoteDtlGroup() != null)
					&& (this.getScSimpleQuoteDtlGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSimpleQuoteDtlGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScSimpleQuoteDtlGroup());
			}
			if ((this.getScCommonStatus() != null)
					&& (this.getScCommonStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScCommonStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScCommonStatus());
			}
			if ((this.getScPurchaseInDefaultStatus() != null)
					&& (this.getScPurchaseInDefaultStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPurchaseInDefaultStatusVar(ReportConst.SC_AMP_STATUS_EQ
								+ this.getScPurchaseInDefaultStatus());
			}
			if ((this.getScSimpleQuoteStatus() != null)
					&& (this.getScSimpleQuoteStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSimpleQuoteStatusVar(ReportConst.SC_AMP_STATUS_EQ
								+ this.getScSimpleQuoteStatus());
			}
			if ((this.getScRequestNoteStatus() != null)
					&& (this.getScRequestNoteStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScRequestNoteStatusVar(ReportConst.SC_AMP_STATUS_EQ
								+ this.getScRequestNoteStatus());
			}
			if ((this.getScQuotationNo() != null)
					&& (this.getScQuotationNo() != ReportConst.EMPTY_VAL)) {
				quotationNumberValidation();
				rptGenBean.setQuotationIdTotVar(ReportConst.AMP_QUOTE_ID_EQ
						+ this.getScQuotationId());
			}
			if ((this.getScDeskOfficer() != null)
					&& (this.getScDeskOfficer() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScDeskOfficerVar(ReportConst.AMP_DESK_OFFCR_EQ
						+ this.getScDeskOfficer());
			}
			if ((this.getScPtj() != null)
					&& (this.getScPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPtjVar(ReportConst.AMP_PROCURE_PTJ_EQ
						+ this.getScPtj());
			}
			if ((this.getScStatus() != null)
					&& (this.getScStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScStatusVar(ReportConst.AMP_PROCURE_STATUS_EQ
						+ this.getScStatus());
			}
			if ((this.getScProcessRptQuotationNo() != null)
					&& (this.getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL)) {
				if ((getManagedBean().getReportValue() != null)
						&& (getManagedBean().getReportValue().trim()
								.equalsIgnoreCase(ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL))) {
					simpleQuoteNoValidation();
				}
				rptGenBean
						.setScProcessRptQuotationNoVar(ReportConst.SC_AMP_QT_NO_EQ
								+ this.getScProcessRptQuotationNo());
			}
			if ((this.getScProcurementType() != null)
					&& (this.getScProcurementType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScProcurementTypeVar(ReportConst.SC_AMP_PROC_TYPE_EQ
								+ this.getScProcurementType());
			}
			StringBuilder procureMethod = new StringBuilder();
			if (this.getScProcureAllList().size() > 0) {
				rptGenBean
						.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ
								+ this.getScProcureAllList().get(0));
			} else {
				if (this.getScQuotationList().size() > 0) {
					procureMethod.append(this.getScQuotationList().get(0));
				}
				if (this.getScTenderList().size() > 0) {
					procureMethod.append(this.getScTenderList().get(0));
				}
				if (this.getScRstrctdTndrList().size() > 0) {
					procureMethod.append(this.getScRstrctdTndrList().get(0));
				}
				if ((procureMethod.length() > 0)) {
					rptGenBean
							.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ
									+ procureMethod);
				}
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					ReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
			if ((this.getScPublishFromDate() != null)
					&& (this.getScPublishFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPublishFromDateVar(ReportConst.AMP_PUB_FRM_DT_EQ
								+ outputDateFormat.format(this
										.getScPublishFromDate()));
			}
			if ((this.getScPublishToDate() != null)
					&& (this.getScPublishToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPublishToDateVar(ReportConst.AMP_PUB_TO_DT_EQ
						+ outputDateFormat.format(this.getScPublishToDate()));
			}
			SimpleDateFormat outputDateFormat2 = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSoneCrtdFromDateVar(ReportConst.AMP_CREAT_FRM_DT_EQ
								+ outputDateFormat2.format(this
										.getScSoneCrtdFromDate()));
			}
			if ((this.getScSoneCrtdToDate() != null)
					&& (this.getScSoneCrtdToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSoneCrtdToDateVar(ReportConst.AMP_CREAT_TO_DT_EQ
								+ outputDateFormat2.format(this
										.getScSoneCrtdToDate()));
			}
			if ((this.getScPIno() != null)
					&& (this.getScPIno() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPInoVar(ReportConst.SC_AMP_PI_NO_EQ
						+ this.getScPIno());
			}

			if ((this.getScSupplier() != null)
					&& (this.getScSupplier() != ReportConst.EMPTY_VAL)) {
				supplierNameValidate();
				rptGenBean.setScSupplierVar(ReportConst.SC_AMP_SUPPLIER_EQ
						+ this.getScSupplier());
			}
			if ((this.getScSoneStatus() != null)
					&& (this.getScSoneStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScSoneStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScSoneStatus());
			}
			if ((this.getReportValue() != null)
					&& (this.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL))) {
				if ((this.getScPIDtlsPrcRptGroup() != null)
						&& (this.getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScPIDtlsPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
									+ this.getScPIDtlsPrcRptGroup());
				}
			}
			if ((this.getScPrcRptGroup() != null)
					&& (this.getScPrcRptGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScPrcRptGroup());
			}
			String userRoleLogin = ReportConst.EMPTY_VAL;
			if (approverNayakTarafList.size() > 0) {
				userRoleLogin = ReportConst.APPROVERNT;
			} else if ((authoriztionList.contains(ReportConst.BPK_APPROVER) || authoriztionList
					.contains(ReportConst.RN_APPROVER))
					&& (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				userRoleLogin = ReportConst.APPROVER;
			} else if ((authoriztionList.contains(ReportConst.BUDGET_APPROVER)
					|| authoriztionList.contains(ReportConst.BPK_APPROVER)
					|| authoriztionList
							.contains(ReportConst.COMMITTEE_APPROVER) || authoriztionList
						.contains(ReportConst.PUBLICATION_APPROVER))
					&& ((this.getModuleValue()
							.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)))) {
				userRoleLogin = ReportConst.APPROVER;
			} else if (authoriztionList.contains(ReportConst.DESK_OFFICER)
					&& ((this.getModuleValue()
							.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)))) {
				userRoleLogin = ReportConst.DESKOFFR;
			} else if (authoriztionList.contains(ReportConst.REQUISITIONER)
					&& (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				userRoleLogin = ReportConst.REQUISITIONER;
			}
			if (!userRoleLogin.equals(ReportConst.REQUISITIONER)) {
				if ((this.getScRequisenerId() != null)
						&& (this.getScRequisenerId() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScRequisenerIdVar(ReportConst.SC_AMP_REQUISENER_EQ
									+ this.getScRequisenerId());
				}
			}
			if ((userRoleLogin != null)
					&& (userRoleLogin != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScUserAccessRoleVar(ReportConst.AMP_USER_ACCESS_ROLE_EQ
								+ userRoleLogin);
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getQuotationIdTotVar()
						+ rptGenBean.getScProcureMethodChkVar()
						+ rptGenBean.getScDeskOfficerVar()
						+ rptGenBean.getScPtjVar()
						+ rptGenBean.getScStatusVar()
						+ rptGenBean.getScSupplierVar()
						+ rptGenBean.getScRequisenerIdVar()
						+ rptGenBean.getScSoneStatusVar()
						+ rptGenBean.getScProcessRptQuotationNoVar()
						+ rptGenBean.getScProcurementTypeVar()
						+ rptGenBean.getScPInoVar()
						+ rptGenBean.getScPIDtlsPrcRptGroupVar()
						+ rptGenBean.getScPrcRptGroupVar()
						+ rptGenBean.getScSoneCrtdFromDateVar()
						+ rptGenBean.getScSoneCrtdToDateVar()
						+ rptGenBean.getScUserAccessRoleVar()
						+ rptGenBean.getScCreatedFromDtVar()
						+ rptGenBean.getScCreatedToDtVar()
						+ rptGenBean.getScPublishFromDateVar()
						+ rptGenBean.getScPublishToDateVar()
						+ rptGenBean.getScCommonStatusVar()
						+ rptGenBean.getScPurchaseInDefaultStatusVar()
						+ rptGenBean.getScSimpleQuoteStatusVar()
						+ rptGenBean.getScRequestNoteStatusVar()
						+ rptGenBean.getScLAPRNPURCHDEFAULTGroupVar()
						+ rptGenBean.getScSimpleQuoteDtlGroupVar()
						+ rptGenBean.getScREQUESTNOTESUMMARYGroupVar()
						+ rptGenBean.getScPIDNoVar()
						+ rptGenBean.getScSOAStatusVar()
						+ rptGenBean.getScSimpleQuoteTypeVar()
						+ rptGenBean.getScLOANoVar()
						+ rptGenBean.getScContractOrPKPTypeVar()
						+ rptGenBean.getScContractOrPKPNoVar()
						+ rptGenBean.getScContractOrPKPNameVar()
						+ rptGenBean.getScRequestQuoteNoVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * generateCMReport - Generate CATALOGUE MANAGEMENT module reports.
	 */
	public void generateCMReport() {
		log.info("ReportBackingBean-generateCMReport");
		if (((this.getCmExtention() != null) && (this.getCmExtention() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionCode() != null) && (this
						.getCmExtentionCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemName() != null) && (this.getCmItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				/*
				 * || ((this.getCmItemStatus() != null) &&
				 * (this.getCmItemStatus() != ReportConst.EMPTY_VAL))
				 */
				|| ((this.getCmUnspscCode() != null) && (this.getCmUnspscCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmUnspscTitle() != null) && (this
						.getCmUnspscTitle() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionType() != null) && (this
						.getCmExtentionType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionBrand() != null) && (this
						.getCmExtentionBrand() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionMeasurment() != null) && (this
						.getCmExtentionMeasurment() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionColour() != null) && (this
						.getCmExtentionColour() != ReportConst.EMPTY_VAL))
				|| ((this.getCmCompanyName() != null) && (this
						.getCmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmRegistrationType() != null) && (this
						.getCmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemCategory() != null) && (this
						.getCmItemCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getCmProofIdentity() != null) && (this
						.getCmProofIdentity() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getCmEpNumber() != null) && (this.getCmEpNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmBusinessRegNo() != null) && (this
						.getCmBusinessRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCmIcNumber() != null) && (this.getCmIcNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmRequestNumber() != null) && (this
						.getCmRequestNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmMofNumber() != null) && (this.getCmMofNumber() != ReportConst.EMPTY_VAL))) {

			if ((this.getCmUnspscCode() != null)
					&& (this.getCmUnspscTitle() != null)
					&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)
					&& (this.getCmUnspscTitle() != ReportConst.EMPTY_VAL)) {

				if (CollectionUtils.isNotEmpty(brandList)
						&& CollectionUtils.isNotEmpty(extTypeList)
						&& CollectionUtils.isNotEmpty(measurmentList)
						&& CollectionUtils.isNotEmpty(colorList)) {
					if (((this.getCmExtentionType() == null) || (this
							.getCmExtentionType() == ReportConst.EMPTY_VAL))
							&& ((this.getCmExtentionBrand() == null) || (this
									.getCmExtentionBrand() == ReportConst.EMPTY_VAL))
							&& ((this.getCmExtentionMeasurment() == null) || (this
									.getCmExtentionMeasurment() == ReportConst.EMPTY_VAL))
							&& ((this.getCmExtentionColour() == null) || (this
									.getCmExtentionColour() == ReportConst.EMPTY_VAL))) {

						throw new EpException(MessageConst.ERROR_REPORT_780024);

					}
				}

				/*
				 * FacesContext.getCurrentInstance().addMessage(null,new
				 * FacesMessage(FacesMessage.SEVERITY_ERROR,
				 * null,MessageUtils.getMessage(
				 * MessageConst.ERROR_REPORT_780024,
				 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
				 * FacesScopes().getUserLocale()))); String pageUrl =
				 * "/xhtml/reports/reportsmain.xhtml"; //String pageUrl =
				 * "/xhtml/reports/reportcontent.xhtml"; FacesContext context =
				 * FacesContext.getCurrentInstance();
				 * context.getApplication().getNavigationHandler()
				 * .handleNavigation(context, null, pageUrl);
				 */
			}

			if ((this.getCmRequestNumber() != null)
					&& (this.getCmRequestNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCmRequestNumberVar(ReportConst.CM_AMP_REQUEST_NO_EQ
								+ this.getCmRequestNumber());
			}
			if ((this.getCmItemName() != null)
					&& (this.getCmItemName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemNameTotVar(ReportConst.CM_AMP_ITM_NAME_EQ
						+ this.getCmItemName());
			}
			if ((this.getCmItemType() != null)
					&& (this.getCmItemType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemTypeTotVar(ReportConst.CM_AMP_ITEM_TYPE_EQ
						+ this.getCmItemType());
			}

			if ((this.getCmUnspscCode() != null)
					&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setUnspscCodeTotVar(ReportConst.CM_AMP_UNSPSC_CODE_EQ
								+ this.getCmUnspscCode());
			}
			if ((this.getCmUnspscTitle() != null)
					&& (this.getCmUnspscTitle() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setUnspscTitleTotVar(ReportConst.CM_AMP_UNSPSC_TITLE_EQ
								+ this.getCmUnspscTitle());
			}
			if ((this.getCmExtentionType() != null)
					&& (this.getCmExtentionType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionTypeVar(ReportConst.AMP_EXT_TYPE_EQ
						+ this.getCmExtentionType());
			}
			if ((this.getCmExtentionBrand() != null)
					&& (this.getCmExtentionBrand() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionBrandVar(ReportConst.AMP_EXT_BRAND_EQ
						+ this.getCmExtentionBrand());
			}
			if ((this.getCmExtentionMeasurment() != null)
					&& (this.getCmExtentionMeasurment() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCmExtentionMeasurmentVar(ReportConst.AMP_EXT_MEASURMENT_EQ
								+ this.getCmExtentionMeasurment());
			}
			if ((this.getCmExtentionColour() != null)
					&& (this.getCmExtentionColour() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCmExtentionColourVar(ReportConst.AMP_EXT_COLOUR_EQ
								+ this.getCmExtentionColour());
			}
			if ((this.getCmExtentionCode() != null)
					&& (this.getCmExtentionCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionCodeVar(ReportConst.CM_AMP_EXT_CODE_EQ
						+ this.getCmExtentionCode());
			}
			if ((this.getCmCompanyName() != null)
					&& (this.getCmCompanyName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCompanyNameTotVar(ReportConst.CM_AMP_COMP_NAME_EQ
						+ this.getCmCompanyName());
			}
			if ((this.getCmRegistrationType() != null)
					&& (this.getCmRegistrationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationTypeTotVar(ReportConst.CM_AMP_REG_TYPE_EQ
								+ this.getCmRegistrationType());
			}
			if ((this.getCmItemCategory() != null)
					&& (this.getCmItemCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ
						+ this.getCmItemCategory());
			}
			if ((this.getCmProofIdentity() != null)
					&& (this.getCmProofIdentity() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ
						+ this.getCmProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.CM_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.CM_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getCmEpNumber() != null)
					&& (this.getCmEpNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setEpNumberTotVar(ReportConst.CM_AMP_EP_NO_EQ
						+ this.getCmEpNumber());
			}
			if ((this.getCmBusinessRegNo() != null)
					&& (this.getCmBusinessRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setBusinessRegNoTotVar(ReportConst.CM_AMP_BUSI_REG_NO_EQ
								+ this.getCmBusinessRegNo());
			}
			if ((this.getCmIcNumber() != null)
					&& (this.getCmIcNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setIcNumberTotVar(ReportConst.CM_AMP_IC_NO_EQ
						+ this.getCmIcNumber());
			}
			if ((this.getCmMofNumber() != null)
					&& (this.getCmMofNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMofNumberTotVar(ReportConst.CM_AMP_MOF_NO_EQ
						+ this.getCmMofNumber());
			}

			if ((this.getCmExtention() != null)
					&& (this.getCmExtention() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionVar(ReportConst.CM_AMP_EXTENSION_EQ
						+ this.getCmExtention());
			}
			if ((this.getCmGroupOne() != null)
					&& (this.getCmGroupOne() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmGroupOneVar(ReportConst.CM_AMP_PGROUP_EQ
						+ this.getCmGroupOne());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getCmExtentionTypeVar()
						+ rptGenBean.getCmExtentionBrandVar()
						+ rptGenBean.getCmExtentionMeasurmentVar()
						+ rptGenBean.getCmExtentionColourVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getCmExtentionVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getEpNumberTotVar()
						+ rptGenBean.getBusinessRegNoTotVar()
						+ rptGenBean.getIcNumberTotVar()
						+ rptGenBean.getCmGroupOneVar()
						+ rptGenBean.getMofNumberTotVar()
						+ rptGenBean.getCmRequestNumberVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getCmExtentionCodeVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);

			/*
			 * //throw new EpException(MessageConst.ERROR_REPORT_780003);
			 * FacesContext.getCurrentInstance().addMessage( null, new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR, null,
			 * MessageUtils.getMessage( MessageConst.ERROR_REPORT_780003,
			 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
			 * FacesScopes().getUserLocale())));
			 * FacesContext.getCurrentInstance().addMessage(null,new
			 * FacesMessage(FacesMessage.SEVERITY_ERROR,
			 * null,MessageUtils.getMessage( MessageConst.ERROR_REPORT_780003,
			 * MessageUtils.BUNDLE_COMMON_MESSAGES, new
			 * FacesScopes().getUserLocale()))); String pageUrl =
			 * "/xhtml/reports/reportsmain.xhtml"; //String pageUrl =
			 * "/xhtml/reports/reportcontent.xhtml"; FacesContext context =
			 * FacesContext.getCurrentInstance();
			 * context.getApplication().getNavigationHandler()
			 * .handleNavigation(context, null, pageUrl);
			 */
		}
	}

	/**
	 * generateFLReport - Generate ORDER FULFILLMENT module reports.
	 */
	public void generateFLReport() {
		log.info("ReportBackingBean-generateFLReport");
		if (((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractNo() != null) && (this.getFlContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSst() != null) && (this.getFlSst() != ReportConst.EMPTY_VAL))
				|| ((this.getFlJabatan() != null) && (this.getFlJabatan() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this
						.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplier() != null) && (this.getFlSupplier() != ReportConst.EMPTY_VAL))
				|| ((this.getFlStatus() != null) && (this.getFlStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDuration() != null) && (this.getFlDuration() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecievingOfficer() != null) && (this
						.getFlRecievingOfficer() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryAddress() != null) && (this
						.getFlDeliveryAddress() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecNoteNo() != null) && (this.getFlRecNoteNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractType() != null) && (this
						.getFlContractType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRequisitioner() != null) && (this
						.getFlRequisitioner() != ReportConst.EMPTY_VAL))
				|| ((this.getFlMtoContract() != null) && (this
						.getFlMtoContract() != ReportConst.EMPTY_VAL))
				|| ((this.getFlInvoiceNo() != null) && (this.getFlInvoiceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplierInvoiceNo() != null) && (this
						.getFlSupplierInvoiceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPaymentAdviceNo() != null) && (this
						.getFlPaymentAdviceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeductionType() != null) && (this
						.getFlDeductionType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCrNo() != null) && (this.getFlCrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlApproverName() != null) && (this
						.getFlApproverName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDocumentType() != null) && (this
						.getFlDocumentType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCriteria() != null) && (this.getFlCriteria() != ReportConst.EMPTY_VAL))
				|| ((this.getFlScoring() != null) && (this.getFlScoring() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPkpNo() != null) && (this.getFlPkpNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlStopInstructionNo() != null) && (this
						.getFlStopInstructionNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryOrderNo() != null) && (this
						.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplierDeliveryOrderNo() != null) && (this
						.getFlSupplierDeliveryOrderNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemCode() != null) && (this.getFlItemCode() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPoAmount() != null) && (this.getFlPoAmount() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPrNo() != null) && (this.getFlPrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPmo() != null) && (this.getFlPmo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemName() != null) && (this.getFlItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItmType() != null) && (this.getFlItmType() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractOrder() != null) && (this
						.getFlContractOrder() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractRequest() != null) && (this
						.getFlContractRequest() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPurchaseOrderNo() != null) && (this
						.getFlPurchaseOrderNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPtjTotVar(ReportConst.FL_AMP_PTJ_NAME_EQ
						+ this.getCommonPtj());
			}
			if ((this.getFlContractNo() != null)
					&& (this.getFlContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setContractNoTotVar(ReportConst.FL_AMP_CONTRACT_NO_EQ
								+ this.getFlContractNo());
			}
			if ((this.getFlSst() != null)
					&& (this.getFlSst() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSstTotVar(ReportConst.FL_AMP_SST_NO_EQ
						+ this.getFlSst());
			}
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				/*
				 * Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				 * 
				 * rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ +
				 * retrieveMinistryName(orgprofileid));
				 */
				rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ
						+ this.getCommonMinistry());
			}
			if ((this.getFlSupplier() != null)
					&& (this.getFlSupplier() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSupplierTotVar(ReportConst.FL_AMP_SUPPL_NAME_EQ
						+ this.getFlSupplier());
			}
			if ((this.getFlStatus() != null)
					&& (this.getFlStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setStatusTotVar(ReportConst.FL_AMP_STATUS_EQ
						+ this.getFlStatus());
			}
			if ((this.getFlRecievingOfficer() != null)
					&& (this.getFlRecievingOfficer() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRecievingOfficerTotVar(ReportConst.FL_AMP_FL_REC_OFF_EQ
								+ this.getFlRecievingOfficer());
			}
			if ((this.getFlDeliveryAddress() != null)
					&& (this.getFlDeliveryAddress() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeliveryAddressTotVar(ReportConst.FL_AMP_DELI_ADDR_EQ
								+ this.getFlDeliveryAddress());
			}
			if ((this.getFlRecNoteNo() != null)
					&& (this.getFlRecNoteNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlRecNoteNoTotVar(ReportConst.FL_AMP_FL_REC_NOTE_EQ
								+ this.getFlRecNoteNo());
			}
			if ((this.getFlContractType() != null)
					&& (this.getFlContractType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setContractTypeTotVar(ReportConst.FL_AMP_COTRACT_TYPE_EQ
								+ this.getFlContractType());
			}
			if ((this.getFlRequisitioner() != null)
					&& (this.getFlRequisitioner() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRequisitionerTotVar(ReportConst.FL_AMP_REQUI_NAME_EQ
								+ this.getFlRequisitioner());
			}
			if ((this.getFlMtoContract() != null)
					&& (this.getFlMtoContract() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMtoContractTotVar(ReportConst.AMP_MTO_CONTR_EQ
						+ this.getFlMtoContract());
			}
			if ((this.getFlInvoiceNo() != null)
					&& (this.getFlInvoiceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setInvoiceNoTotVar(ReportConst.FL_AMP_INV_NO_EQ
						+ this.getFlInvoiceNo());
			}
			if ((this.getFlSupplierInvoiceNo() != null)
					&& (this.getFlSupplierInvoiceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlSupplierInvoiceNoVar(ReportConst.FL_AMP_SUPPL_INV_NO_EQ
								+ this.getFlSupplierInvoiceNo());
			}

			if ((this.getFlPaymentAdviceNo() != null)
					&& (this.getFlPaymentAdviceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlPaymentAdviceNoVar(ReportConst.FL_AMP_PAYMENT_ADV_NO_EQ
								+ this.getFlPaymentAdviceNo());
			}
			if ((this.getFlDeductionType() != null)
					&& (this.getFlDeductionType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeductionTypeTotVar(ReportConst.AMP_DEDUCT_TYPE_EQ
								+ this.getFlDeductionType());
			}
			if ((this.getFlCrNo() != null)
					&& (this.getFlCrNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCrNoTotVar(ReportConst.AMP_CR_NO_EQ
						+ this.getFlCrNo());
			}
			if ((this.getFlApproverName() != null)
					&& (this.getFlApproverName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApproverNameTotVar(ReportConst.FL_AMP_APPROVER_NAME_EQ
								+ this.getFlApproverName());
			}
			if ((this.getFlDocumentType() != null)
					&& (this.getFlDocumentType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setDocumentTypeTotVar(ReportConst.FL_AMP_DOC_TYPE_EQ
						+ this.getFlDocumentType());
			}
			if ((this.getFlCriteria() != null)
					&& (this.getFlCriteria() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCriteriaTotVar(ReportConst.AMP_CRITERIA_EQ
						+ this.getFlCriteria());
			}
			if ((this.getFlScoring() != null)
					&& (this.getFlScoring() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScoringTotVar(ReportConst.AMP_SCORING_EQ
						+ this.getFlScoring());
			}
			if ((this.getFlPkpNo() != null)
					&& (this.getFlPkpNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPkpNoTotVar(ReportConst.FL_AMP_PKP_NO_EQ
						+ this.getFlPkpNo());
			}
			if ((this.getFlStopInstructionNo() != null)
					&& (this.getFlStopInstructionNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlStopInstructionNoVar(ReportConst.FL_AMP_STOP_INSTR_NO_EQ
								+ this.getFlStopInstructionNo());
			}
			if ((this.getFlDeliveryOrderNo() != null)
					&& (this.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeliveryOrderNoTotVar(ReportConst.FL_AMP_DELI_ORD_NO_EQ
								+ this.getFlDeliveryOrderNo());
			}
			if ((this.getFlSupplierDeliveryOrderNo() != null)
					&& (this.getFlSupplierDeliveryOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlSupplierDeliveryOrderNoVar(ReportConst.FL_AMP_SUP_DELI_ORD_NO_EQ
								+ this.getFlSupplierDeliveryOrderNo());
			}
			if ((this.getFlItemCode() != null)
					&& (this.getFlItemCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemCodeTotVar(ReportConst.AMP_ITM_CODE_EQ
						+ this.getFlItemCode());
			}
			if ((this.getFlPoAmount() != null)
					&& (this.getFlPoAmount() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPoAmountTotVar(ReportConst.AMP_PO_AMT_EQ
						+ this.getFlPoAmount());
			}
			if ((this.getFlPrNo() != null)
					&& (this.getFlPrNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPrNoTotVar(ReportConst.AMP_PR_NO_EQ
						+ this.getFlPrNo());
			}
			if ((this.getFlPmo() != null)
					&& (this.getFlPmo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmoTotVar(ReportConst.AMP_PMO_EQ
						+ this.getFlPmo());
			}
			if ((this.getFlItemName() != null)
					&& (this.getFlItemName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemNameTotVar(ReportConst.FL_AMP_ITM_NAME_EQ
						+ this.getFlItemName());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.FL_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.FL_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getFlContractOrder() != null)
					&& (this.getFlContractOrder() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlContractOrderVar(ReportConst.FL_AMP_COTRACT_ORDER_EQ
								+ this.getFlContractOrder());
			}
			if ((this.getFlContractRequest() != null)
					&& (this.getFlContractRequest() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlContractRequestVar(ReportConst.FL_AMP_COTRACT_REQUEST_EQ
								+ this.getFlContractRequest());
			}
			if ((this.getFlPurchaseOrderNo() != null)
					&& (this.getFlPurchaseOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlPurchaseOrderNoVar(ReportConst.FL_AMP_PURCHASE_ORDER_EQ
								+ this.getFlPurchaseOrderNo());
			}
			if ((this.getFlGroupOne() != null)
					&& (this.getFlGroupOne() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupOneVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupOne());
			}
			if ((this.getFlGroupTwo() != null)
					&& (this.getFlGroupTwo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTwoVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTwo());
			}
			if ((this.getFlGroupThree() != null)
					&& (this.getFlGroupThree() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupThreeVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupThree());
			}
			if ((this.getFlGroupFour() != null)
					&& (this.getFlGroupFour() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFourVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFour());
			}
			if ((this.getFlGroupFive() != null)
					&& (this.getFlGroupFive() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFiveVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFive());
			}
			if ((this.getFlGroupSix() != null)
					&& (this.getFlGroupSix() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSixVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSix());
			}
			if ((this.getFlGroupSeven() != null)
					&& (this.getFlGroupSeven() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSevenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSeven());
			}
			if ((this.getFlGroupEight() != null)
					&& (this.getFlGroupEight() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupEightVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupEight());
			}
			if ((this.getFlGroupNine() != null)
					&& (this.getFlGroupNine() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupNineVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupNine());
			}
			if ((this.getFlGroupTen() != null)
					&& (this.getFlGroupTen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTen());
			}
			if ((this.getFlGroupEleven() != null)
					&& (this.getFlGroupEleven() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupElevenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupEleven());
			}
			if ((this.getFlGroupTwelve() != null)
					&& (this.getFlGroupTwelve() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTwelveVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTwelve());
			}
			if ((this.getFlGroupThirteen() != null)
					&& (this.getFlGroupThirteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupThirteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupThirteen());
			}
			if ((this.getFlGroupFourteen() != null)
					&& (this.getFlGroupFourteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFourteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFourteen());
			}
			if ((this.getFlGroupFifteen() != null)
					&& (this.getFlGroupFifteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFifteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFifteen());
			}
			if ((this.getFlGroupSixteen() != null)
					&& (this.getFlGroupSixteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSixteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSixteen());
			}
			if ((this.getFlGroupSeventeen() != null)
					&& (this.getFlGroupSeventeen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSeventeenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSeventeen());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getExtentionCodeTotVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getProofIdentityTotVar()
						+ rptGenBean.getPtjTotVar()
						+ rptGenBean.getContractNoTotVar()
						+ rptGenBean.getSstTotVar()
						+ rptGenBean.getJabatanTotVar()
						+ rptGenBean.getMinistryTotVar()
						+ rptGenBean.getSupplierTotVar()
						+ rptGenBean.getStatusTotVar()
						+ rptGenBean.getDurationTotVar()
						+ rptGenBean.getRecievingOfficerTotVar()
						+ rptGenBean.getDeliveryAddressTotVar()
						+ rptGenBean.getFlRecNoteNoTotVar()
						+ rptGenBean.getContractTypeTotVar()
						+ rptGenBean.getRequisitionerTotVar()
						+ rptGenBean.getMtoContractTotVar()
						+ rptGenBean.getInvoiceNoTotVar()
						+ rptGenBean.getFlSupplierInvoiceNoVar()
						+ rptGenBean.getFlPaymentAdviceNoVar()
						+ rptGenBean.getDeductionTypeTotVar()
						+ rptGenBean.getCrNoTotVar()
						+ rptGenBean.getApproverNameTotVar()
						+ rptGenBean.getDocumentTypeTotVar()
						+ rptGenBean.getCriteriaTotVar()
						+ rptGenBean.getScoringTotVar()
						+ rptGenBean.getPkpNoTotVar()
						+ rptGenBean.getFlStopInstructionNoVar()
						+ rptGenBean.getDeliveryOrderNoTotVar()
						+ rptGenBean.getFlSupplierDeliveryOrderNoVar()
						+ rptGenBean.getItemCodeTotVar()
						+ rptGenBean.getPoAmountTotVar()
						+ rptGenBean.getPrNoTotVar()
						+ rptGenBean.getPmoTotVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getFlContractOrderVar()
						+ rptGenBean.getFlContractRequestVar()
						+ rptGenBean.getFlPurchaseOrderNoVar()
						+ rptGenBean.getFlGroupOneVar()
						+ rptGenBean.getFlGroupTwoVar()
						+ rptGenBean.getFlGroupThreeVar()
						+ rptGenBean.getFlGroupFourVar()
						+ rptGenBean.getFlGroupFiveVar()
						+ rptGenBean.getFlGroupSixVar()
						+ rptGenBean.getFlGroupSevenVar()
						+ rptGenBean.getFlGroupEightVar()
						+ rptGenBean.getFlGroupNineVar()
						+ rptGenBean.getFlGroupTenVar()
						+ rptGenBean.getFlGroupElevenVar()
						+ rptGenBean.getFlGroupTwelveVar()
						+ rptGenBean.getFlGroupThirteenVar()
						+ rptGenBean.getFlGroupFourteenVar()
						+ rptGenBean.getFlGroupFifteenVar()
						+ rptGenBean.getFlGroupSixteenVar()
						+ rptGenBean.getFlGroupSeventeenVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * generatePPReport - Generate PROCUREMENT PLAN module reports.
	 */
	public void generatePPReport() {
		log.info("ReportBackingBean-generatePPReport");
		if (((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this
						.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getPpYear() != null) && (this.getPpYear() != ReportConst.EMPTY_VAL))) {
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPpPTJVar(ReportConst.FL_AMP_PTJ_NAME_EQ
						+ this.getCommonPtj());
			}
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				/*
				 * Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				 * 
				 * rptGenBean.setPpMinistryVar(ReportConst.FL_AMP_MINISTRY_EQ +
				 * retrieveMinistryName(orgprofileid));
				 */
				rptGenBean.setPpMinistryVar(ReportConst.FL_AMP_MINISTRY_EQ
						+ this.getCommonMinistry());
			}
			if ((this.getPpYear() != null)
					&& (this.getPpYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPpYearVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getPpYear());
			}

			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getPpPTJVar()
						+ rptGenBean.getPpMinistryVar()
						+ rptGenBean.getPpYearVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * generatePMReport - Generate PROFILE MANAGEMENT module reports.
	 */
	public void generatePMReport() {
		log.info("ReportBackingBean-generatePMReport");
		if (((this.getPmServiceType() != null) && (this.getPmServiceType() != ReportConst.EMPTY_VAL))
				|| ((this.getPmBusinessRegNo() != null) && (this
						.getPmBusinessRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getPmStatus() != null) && (this.getPmStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getPmFinancialOrgName() != null) && (this
						.getPmFinancialOrgName() != ReportConst.EMPTY_VAL))
				|| ((this.getPmEpNo() != null) && (this.getPmEpNo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmOrgCategory() != null) && (this
						.getPmOrgCategory() != ReportConst.EMPTY_VAL))

				|| ((this.getPmIdentificationNo() != null) && (this
						.getPmIdentificationNo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmCompanyName() != null) && (this
						.getPmCompanyName() != ReportConst.EMPTY_VAL))

				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))

				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))

				|| ((this.getPmOrgType() != null) && (this.getPmOrgType() != ReportConst.EMPTY_VAL))

				|| ((this.getPmContractNo() != null) && (this.getPmContractNo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmPhysicalContractNo() != null) && (this
						.getPmPhysicalContractNo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmLOANo() != null) && (this.getPmLOANo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmPhysicalLOANo() != null) && (this
						.getPmPhysicalLOANo() != ReportConst.EMPTY_VAL))

				|| ((this.getPmRoleCode() != null) && (this.getPmRoleCode() != ReportConst.EMPTY_VAL))

				|| ((this.getPmUserName() != null) && (this.getPmUserName() != ReportConst.EMPTY_VAL))

				|| ((this.getPmItemCode() != null) && (this.getPmItemCode() != ReportConst.EMPTY_VAL))

				|| ((this.getPmItemName() != null) && (this.getPmItemName() != ReportConst.EMPTY_VAL))

				|| ((this.getPmOrgCode() != null) && (this.getPmOrgCode() != ReportConst.EMPTY_VAL))

				|| ((this.getPmOrgName() != null) && (this.getPmOrgName() != ReportConst.EMPTY_VAL))

				|| ((this.getPmZone() != null) && (this.getPmZone() != ReportConst.EMPTY_VAL))

				|| ((this.getPmProfileStatus() != null) && (this
						.getPmProfileStatus() != ReportConst.EMPTY_VAL))) {

			if ((this.getPmServiceType() != null)
					&& (this.getPmServiceType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmServiceTypeVar(ReportConst.PM_AMP_SERVICE_TYPE_EQ
								+ this.getPmServiceType());
			}
			if ((this.getPmBusinessRegNo() != null)
					&& (this.getPmBusinessRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmBusinessRegNoVar(ReportConst.PM_AMP_BIZREG_NO_EQ
								+ this.getPmBusinessRegNo());
			}
			if ((this.getPmStatus() != null)
					&& (this.getPmStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmStatusVar(ReportConst.PM_AMP_STATUS_EQ
						+ this.getPmStatus());
			}
			if ((this.getPmFinancialOrgName() != null)
					&& (this.getPmFinancialOrgName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmFinancialOrgNameVar(ReportConst.PM_AMP_FINORG_NAME_EQ
								+ this.getPmFinancialOrgName());
			}

			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.AMP_PM_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.AMP_PM_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}

			if ((this.getPmEpNo() != null)
					&& (this.getPmEpNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmEpNoVar(ReportConst.AMP_PM_EP_NO_EQ
						+ this.getPmEpNo());
			}

			if ((this.getPmOrgCategory() != null)
					&& (this.getPmOrgCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmOrgCategoryVar(ReportConst.AMP_PM_ORG_CATEGORY_EQ
								+ this.getPmOrgCategory());
			}

			if ((this.getPmOrgType() != null)
					&& (this.getPmOrgType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmOrgTypeVar(ReportConst.AMP_PM_ORG_TYPE_EQ
						+ this.getPmOrgType());
			}

			if ((this.getPmOrgName() != null)
					&& (this.getPmOrgName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmOrgNameVar(ReportConst.AMP_PM_ORG_NAME_EQ
						+ this.getPmOrgName());
			}

			if ((this.getPmOrgCode() != null)
					&& (this.getPmOrgCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmOrgCodeVar(ReportConst.AMP_PM_ORG_CODE_EQ
						+ this.getPmOrgCode());
			}

			if ((this.getPmItemName() != null)
					&& (this.getPmItemName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmItemNameVar(ReportConst.AMP_PM_ITEM_NAME_EQ
						+ this.getPmItemName());
			}

			if ((this.getPmItemCode() != null)
					&& (this.getPmItemCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmItemCodeVar(ReportConst.AMP_PM_ITEM_CODE_EQ
						+ this.getPmItemCode());
			}

			if ((this.getPmCompanyName() != null)
					&& (this.getPmCompanyName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmCompanyNameVar(ReportConst.AMP_PM_COMPANY_NAME_EQ
								+ this.getPmCompanyName());
			}

			if ((this.getPmIdentificationNo() != null)
					&& (this.getPmIdentificationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmIdentificationNoVar(ReportConst.AMP_PM_IDENTIFICATION_NO_EQ
								+ this.getPmIdentificationNo());
			}

			if ((this.getPmUserName() != null)
					&& (this.getPmUserName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmUserNameVar(ReportConst.AMP_PM_USERNAME_EQ
						+ this.getPmUserName());
			}

			if ((this.getPmRoleCode() != null)
					&& (this.getPmRoleCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmRoleCodeVar(ReportConst.AMP_PM_ROLE_CODE_EQ
						+ this.getPmRoleCode());
			}

			if ((this.getPmContractNo() != null)
					&& (this.getPmContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmContractNoVar(ReportConst.AMP_PM_CONTRACT_NO_EQ
						+ this.getPmContractNo());
			}

			if ((this.getPmPhysicalContractNo() != null)
					&& (this.getPmPhysicalContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmPhysicalContractNoVar(ReportConst.AMP_PM_CONTRACT_NO_EQ
								+ this.getPmPhysicalContractNo());
			}

			if ((this.getPmLOANo() != null)
					&& (this.getPmLOANo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmLOANoVar(ReportConst.AMP_PM_CONTRACT_NO_EQ
						+ this.getPmLOANo());
			}

			if ((this.getPmPhysicalLOANo() != null)
					&& (this.getPmPhysicalLOANo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmPhysicalLOANoVar(ReportConst.AMP_PM_CONTRACT_NO_EQ
								+ this.getPmPhysicalLOANo());
			}

			if ((this.getPmZone() != null)
					&& (this.getPmZone() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmZoneVar(ReportConst.AMP_PM_SUPP_ZONE_EQ
						+ this.getPmZone());
			}

			if ((this.getPmProfileStatus() != null)
					&& (this.getPmProfileStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPmProfileStatusVar(ReportConst.AMP_PM_SUPP_PROFILE_STATUS_EQ
								+ this.getPmProfileStatus());
			}

			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getPmServiceTypeVar()
						+ rptGenBean.getPmBusinessRegNoVar()
						+ rptGenBean.getPmStatusVar()
						+ rptGenBean.getPmFinancialOrgNameVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar() + rptGenBean.getPmEpNoVar()
						+ rptGenBean.getPmOrgCategoryVar()
						+ rptGenBean.getPmOrgTypeVar()
						+ rptGenBean.getPmOrgNameVar()
						+ rptGenBean.getPmOrgCodeVar()
						+ rptGenBean.getPmItemNameVar()
						+ rptGenBean.getPmItemCodeVar()
						+ rptGenBean.getPmCompanyNameVar()
						+ rptGenBean.getPmIdentificationNoVar()
						+ rptGenBean.getPmUserNameVar()
						+ rptGenBean.getPmRoleCodeVar()
						+ rptGenBean.getPmContractNoVar()
						+ rptGenBean.getPmPhysicalContractNoVar()
						+ rptGenBean.getPmLOANoVar()
						+ rptGenBean.getPmPhysicalLOANoVar()
						+ rptGenBean.getPmZoneVar()
						+ rptGenBean.getPmProfileStatusVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/**
	 * quotationNumberValidation - Quotation number validation.
	 */
	public void quotationNumberValidation() {
		log.info("ReportBackingBean-quotationNumberValidation");
		if ((this.getScQuotationNo() != null)
				&& (this.getScQuotationNo() != ReportConst.EMPTY_VAL)) {
			specialCharValidation();
		}
	}

	/**
	 * simpleQuoteNoValidation - Simple quote no validation.
	 */
	public void simpleQuoteNoValidation() {
		log.info("ReportBackingBean-simpleQuoteNoValidation");
		if ((this.getScProcessRptQuotationNo() != null)
				&& (this.getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL)) {
			specialCharValidation();
		}
	}

	/**
	 * specialCharValidation - Special char validation.
	 */
	public void specialCharValidation() {
		log.info("ReportBackingBean-specialCharValidation");
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		Matcher match;
		if ((getManagedBean().getReportValue() != null)
				&& (getManagedBean()
						.getReportValue()
						.trim()
						.equalsIgnoreCase(
								ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL)
						|| getManagedBean()
								.getReportValue()
								.trim()
								.equalsIgnoreCase(
										ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL) || getManagedBean()
						.getReportValue()
						.trim()
						.equalsIgnoreCase(
								ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL))) {
			// if(this.getScProcessRptQuotationNo() != null){
			match = pattern.matcher(this.getScProcessRptQuotationNo());
			// }
		} else {
			match = pattern.matcher(this.getScQuotationNo());
		}
		if (match.matches()) {
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean()
							.getReportValue()
							.trim()
							.equalsIgnoreCase(
									ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL)
							|| getManagedBean()
									.getReportValue()
									.trim()
									.equalsIgnoreCase(
											ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL) || getManagedBean()
							.getReportValue()
							.trim()
							.equalsIgnoreCase(
									ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL))) {
				retrieveSimpleQuoteNo();
			} else {
				getQuotationNumber();
			}

		} else {
			this.setScQuotationNo(null);
			this.setScProcessRptQuotationNo(null);
			throw new EpException(MessageConst.ERROR_REPORT_780006);
		}
	}

	/**
	 * mofRegNoNumericValidation - Mof reg no numeric validation.
	 */
	public void mofRegNoNumericValidation() {
		log.info("ReportBackingBean-mofRegNoNumericValidation");
		String ptString1 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern1 = Pattern.compile(ptString1);
		if ((this.getSmMofRegNoFrom() != null)
				&& (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL)) {
			String mofRegNoFrom = null;
			if (this.getSmMofRegNoFrom().contains(ReportConst.PARAM_IFON)) {
				mofRegNoFrom = this.getSmMofRegNoFrom().replace(
						ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				if (mofRegNoFrom.contains(ReportConst.PARAM_SPACE)) {
					mofRegNoFrom = mofRegNoFrom.replace(
							ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);
				}
			} else {
				mofRegNoFrom = this.getSmMofRegNoFrom();
			}

			Matcher match1 = pattern1.matcher(mofRegNoFrom);
			if (match1.matches()) {
				// this.setSmMofRegNoFrom(mofRegNoFrom);
			} else {
				this.setSmMofRegNoFrom(null);
				this.setSmMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
		String ptString2 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern2 = Pattern.compile(ptString2);
		if ((this.getSmMofRegNoTo() != null)
				&& (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL)) {
			String mofRegNoTo = null;
			if (this.getSmMofRegNoTo().contains(ReportConst.PARAM_IFON)) {
				mofRegNoTo = this.getSmMofRegNoTo().replace(
						ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
				if (mofRegNoTo.contains(ReportConst.PARAM_SPACE)) {
					mofRegNoTo = mofRegNoTo.replace(ReportConst.PARAM_SPACE,
							ReportConst.EMPTY_VAL);
				}
			} else {
				mofRegNoTo = this.getSmMofRegNoTo();
			}

			Matcher match2 = pattern2.matcher(mofRegNoTo);
			if (match2.matches()) {
				// this.setSmMofRegNoTo(mofRegNoTo);
			} else {
				this.setSmMofRegNoFrom(null);
				this.setSmMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
	}

	/**
	 * ValidateMofRegNo - will validate mof reg no.
	 * 
	 * @param fromMofNo
	 *            the from mof no
	 * @param toMofNo
	 *            the to mof no
	 */
	public void ValidateMofRegNo(String fromMofNo, String toMofNo) {
		log.info("ReportBackingBean-ValidateMofRegNo");
		Long fromMofNoLng = Long.parseLong(fromMofNo);
		Long toMofNoLng = Long.parseLong(toMofNo);
		if (fromMofNoLng > toMofNoLng) {
			throw new EpException(MessageConst.ERROR_REPORT_780019);
		}
	}

	/**
	 * getQuotationNumber - Gets the quotation number.
	 * 
	 * @return the quotationNumber
	 */
	public void getQuotationNumber() {
		log.info("ReportBackingBean-getQuotationNumber");
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		// log.info("QtNo="+this.getScQuotationNo());
		if (this.getScQuotationNo() != null) {
			tenderReqDTO.setQtTndrNo(this.getScQuotationNo().toUpperCase());
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderResDTO resDTO = detailSO
					.retriveQtByQtNumber(tenderReqDTO);
			ScQt scqt = resDTO.getQtDetail();
			if (scqt != null) {
				if (scqt.getQtId() != null) {
					// log.info("QtId="+scqt.getQtId());
					this.setScQuotationId(scqt.getQtId().toString());
				} else {
					this.setScQuotationNo(null);
					throw new EpException(MessageConst.ERROR_REPORT_780005);
				}
			} else {
				this.setScQuotationNo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780005);
			}
		}
	}

	/**
	 * resetReportForm - Reset the criteria and clears the criteria.
	 */
	public void resetReportForm() {
		log.info("ReportBackingBean-resetReportForm");
		assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
		getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
		getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
		getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
		getManagedBean().getRptGenBean().setCmExtentionColourView(false);
		this.setFromDate(null);
		this.setToDate(null);
		this.setScQuotationId(null);
		this.setScQuotationNo(null);
		this.setScPaymentId(null);
		this.getManagedBean().setLoaReportDocumentList(
				new ArrayList<ScLoaReport>());
		this.setScLOAIdNo(null);
		this.setCmItemType(null);
		this.setCmItemStatus(null);
		this.setCmUnspscCode(null);
		this.setCmUnspscTitle(null);
		// this.setCmUnspscTitle(null);
		this.setCmCompanyName(null);
		this.setCmRegistrationType(null);
		this.setCmItemCategory(null);
		this.setCmItemName(null);
		this.setCmRequestNumber(null);
		this.setCmEpNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmIcNumber(null);
		this.setCmMofNumber(null);
		this.setCmExtentionCode(null);
		this.setCmExtentionType(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		this.setCmProofIdentity(null);
		this.setCmSupplOrAdmin(null);
		this.setCmGroupOne(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		this.setFlItmType(null);
		this.setCommonPtj(null);
		this.setFlContractNo(null);
		this.setFlSst(null);
		this.setFlJabatan(null);
		this.setCommonMinistry(null);
		this.setFlSupplier(null);
		this.setFlStatus(null);
		this.setFlDuration(null);
		this.setFlRecievingOfficer(null);
		this.setFlDeliveryAddress(null);
		this.setFlRecNoteNo(null);
		this.setFlContractType(null);
		this.setFlRequisitioner(null);
		this.setFlMtoContract(null);
		this.setFlInvoiceNo(null);
		this.setFlDeductionType(null);
		this.setFlCrNo(null);
		this.setFlApproverName(null);
		this.setFlDocumentType(null);
		this.setFlCriteria(null);
		this.setFlScoring(null);
		this.setFlPkpNo(null);
		this.setFlStopInstructionNo(null);
		this.setFlInvoiceNo(null);
		this.setFlSupplierInvoiceNo(null);
		this.setFlPaymentAdviceNo(null);
		this.setFlDeliveryOrderNo(null);
		this.setFlSupplierDeliveryOrderNo(null);
		this.setFlItemCode(null);
		this.setFlPoAmount(null);
		this.setFlPrNo(null);
		this.setFlPmo(null);
		this.setFlItemName(null);
		this.setSmApplicationNo(null);
		if ((allRoleCodesList != null)
				&& (allRoleCodesList.contains(ReportConst.ROLE_CODE_EP_ADMIN) || allRoleCodesList
						.contains(ReportConst.ROLE_CODE_PTJ_ADMIN))) {
			this.setSmZone(null);
		}
		this.setSmSupplierType(null);
		this.setSmRTReportType(null);
		this.setSmSupplierName(null);
		smMofRegistrationFeeList = new ArrayList<SelectItem>();
		smProcessingFeeList = new ArrayList<SelectItem>();
		smSoftCertificateFeeList = new ArrayList<SelectItem>();
		this.setSmMofRegNo(null);
		this.setSmRegistrationStatus(null);
		this.setSmBusCompTypeRegistrationStatus(null);
		this.registrationStatusListOfBusCompType = new ArrayList<SelectItem>();
		this.smBusCompanyTypeList = new ArrayList<SelectItem>();
		this.setSmBusPaidUpCapitalFrm(null);
		this.setSmBusPaidUpCapitalTo(null);
		this.setSmcategory(null);
		this.setSmCode(null);
		this.setSmBusCompanyType(null);
		this.setSmMainCategory(null);
		this.setSmSubCategory(null);
		this.setSmSubCategoryDivision(null);
		this.setSmApplicationStatus(null);
		this.setSmPaymentType(null);
		this.setSmApplicationType(null);
		this.setSmZone(null);
		this.setSmState(null);
		this.setSmDistrict(null);
		this.setSmSVOUser(null);
		this.setSmReportType(null);
		this.setSmProcessingType(null);
		this.setSmReceiptNo(null);
		this.setSmProcessingOfficerName(null);
		this.setSmPaymentMethod(null);
		this.setSmFromMonth(null);
		this.setSmToMonth(null);
		this.setSmCompanyName(null);
		this.setSmPaymentTo(null);
		this.setSmRegistrationType(null);
		this.setSmCreatedFromYear(null);
		this.setSmCreatedToYear(null);
		this.setSmYearText(null);
		this.setSmMofRegNoFrom(null);
		this.setSmMofRegNoTo(null);
		this.setSmPaidUpCapitalFrm(null);
		this.setSmPaidUpCapitalTo(null);
		this.setSmPaidUpCapitalNaccum(null);
		this.setSmRole(null);
		this.setSmEpNo(null);
		this.setSmRegistrationNo(null);
		this.setSmSupportingDocumentMode(null);
		this.setSmPersonnelRole(null);
		this.setSmSupportingDocumentStatus(null);
		this.setSmIdentificationType(null);
		this.setSmIdentificationNumber(null);
		this.setSmCancellationBy(null);
		this.setSmThresholdFromYear(null);
		this.setSmThresholdToYear(null);
		this.setSmRequestExpiryDate(null);
		this.setSmUserName(null);
		this.setSmProfileStatus(null);
		this.setFlContractOrder(null);
		this.setFlContractRequest(null);
		this.setFlPurchaseOrderNo(null);
		this.setScCreatedToYear(null);
		this.setScCreatedToMonth(null);
		this.setScCreatedFromYear(null);
		this.setScCreatedFromMonth(null);
		this.setScDeskOfficer(null);
		this.setScPtj(null);
		this.setScStatus(null);
		this.setScPublishFromDate(null);
		this.setScPublishToDate(null);
		this.setScSoneCrtdFromDate(null);
		this.setScSoneCrtdToDate(null);
		this.setScRequisenerId(null);
		this.setScSupplier(null);
		this.setScLogin(null);
		this.setScPIno(null);
		this.setScSoneStatus(null);
		this.setScPrcRptGroup(null);
		this.setScPIDtlsPrcRptGroup(null);
		this.setScProcurementType(null);
		this.setScProcessRptQuotationNo(null);
		this.setScCommonStatus(null);
		this.setScPurchaseInDefaultStatus(null);
		this.setScSimpleQuoteStatus(null);
		this.setScRequestNoteStatus(null);
		this.setScLAPRNPURCHDEFAULTGroup(null);
		this.setScSimpleQuoteDtlGroup(null);
		this.setScREQUESTNOTESUMMARYGroup(null);
		this.setScPIDNo(null);
		this.setScContractOrPKPType(null);
		this.setScContractOrPKPNo(null);
		this.setScContractOrPKPName(null);
		this.setScRequestQuoteNo(null);
		this.setScSOAStatus(null);
		this.setScSimpleQuoteType(null);
		this.setScLOANo(null);
		scProcureAllList = new ArrayList<SelectItem>();
		scQuotationList = new ArrayList<SelectItem>();
		scTenderList = new ArrayList<SelectItem>();
		scRstrctdTndrList = new ArrayList<SelectItem>();
		this.setCtContractNo(null);
		this.setCtPhysicalContractNo(null);
		this.setCtAgreement(null);
		this.setCtStatus(null);
		this.setCtStatusTwo(null);
		this.setCtTerminationType(null);
		this.setCtComplete(null);
		this.setCtGroupOne(null);
		this.setCtGroupTwo(null);
		this.setCtGroupThree(null);
		this.setCtProductService(null);
		this.setCtLoanNo(null);
		this.setCtLOAPhysicalNo(null);
		this.setCtSubType(null);
		this.setCtUserType(null);
		this.setCtProcurementMode(null);
		this.setCtCircularNo(null);
		this.setCtCircularName(null);
		this.setCtCircularType(null);
		this.setCtCircularEffectiveFromDate(null);
		this.setCtCircularEffectiveToDate(null);
		this.setCtSupplierName(null);
		ctSubTypeList = new ArrayList<SelectItem>();
		this.setFlGroupOne(null);
		this.setFlGroupTwo(null);
		this.setFlGroupThree(null);
		this.setFlGroupFour(null);
		this.setFlGroupFive(null);
		this.setFlGroupSix(null);
		this.setFlGroupSeven(null);
		this.setFlGroupEight(null);
		this.setFlGroupNine(null);
		this.setFlGroupTen(null);
		this.setFlGroupEleven(null);
		this.setFlGroupTwelve(null);
		this.setFlGroupThirteen(null);
		this.setFlGroupFourteen(null);
		this.setFlGroupFifteen(null);
		this.setFlGroupSixteen(null);
		this.setFlGroupSeventeen(null);
		this.setPpMinistry(null);
		this.setPpPTJ(null);
		this.setPpYear(null);
		this.setMinistryName(null);
		this.setPmServiceType(null);
		this.setPmBusinessRegNo(null);
		this.setPmStatus(null);
		this.setPmFinancialOrgName(null);
		this.setPmEpNo(null);
		this.setPmOrgCategory(null);
		this.setPmOrgType(null);
		this.setPmOrgName(null);
		this.setPmOrgCode(null);
		this.setPmItemName(null);
		this.setPmItemCode(null);
		this.setPmCompanyName(null);
		this.setPmIdentificationNo(null);
		this.setPmUserName(null);
		this.setPmRoleCode(null);
		this.setPmContractNo(null);
		this.setPmPhysicalContractNo(null);
		this.setPmLOANo(null);
		this.setPmPhysicalLOANo(null);
		this.setPmZone(null);
		this.setPmProfileStatus(null);
		// changeReportType();
		retrieveDates();
		applicationStatusDateChange();
	}

	/**
	 * fileGenerateReport - Ut will generate report based on generated URL.
	 * 
	 * @param reportFile
	 *            the report file
	 * @param paramConstrString
	 *            the param constr string
	 */
	protected void fileGenerateReport(String reportTitle,
			String paramConstrString) {
		log.info("ReportBackingBean-fileGenerateReport");
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString);
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		HttpURLConnection urlConnection = null;
		URL obj;
		BufferedInputStream buf;
		try {
			obj = new URL(gnrtdURL.toString());
			log.info(gnrtdURL.toString());
			urlConnection = (HttpURLConnection) obj.openConnection();

			urlConnection.setConnectTimeout(290000);
			urlConnection.setReadTimeout(290000);
			new Thread(new InterruptThread(urlConnection)).start();

			buf = new BufferedInputStream(urlConnection.getInputStream());
			PortletResponse portletResponse = (PortletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			HttpServletResponse res = PortalUtil
					.getHttpServletResponse(portletResponse);
			/*
			 * int position = reportFile.lastIndexOf(ReportConst.DOTJSP); if
			 * (position != -1) { reportFile = reportFile.substring(0,
			 * position); }
			 */
			res.setHeader(ReportConst.CONTENT_DISPOSITION,
					ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportTitle
							+ ReportConst.UNDERSCORE
							+ ReportConst.REPORT_DATE_TIME.format(new Date())
							+ ReportConst.DOTPDFEIFON);//
			res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING,
					ReportConst.BINARY);
			res.setContentType(ReportConst.APPLICATIONEIFONPDF);
			res.flushBuffer();
			OutputStream out = res.getOutputStream();
			byte[] b = new byte[128];
			int size;
			while ((size = buf.read(b, 0, b.length)) != -1) {
				out.write(b, 0, size);
			}
			buf.close();
			out.close();
		} catch (MalformedURLException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.addResponseCookie(
					org.primefaces.util.Constants.DOWNLOAD_COOKIE,
					ReportConst.RPT_TRUE,
					Collections.<String, Object> emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		} catch (IllegalArgumentException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.addResponseCookie(
					org.primefaces.util.Constants.DOWNLOAD_COOKIE,
					ReportConst.RPT_TRUE,
					Collections.<String, Object> emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		} catch (IOException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();
			externalContext.addResponseCookie("primefaces.download",
					ReportConst.RPT_TRUE,
					Collections.<String, Object> emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780020);
		} finally {
			urlConnection.disconnect();
		}

	}

	/**
	 * Gets the module item map.
	 * 
	 * @return the module item map
	 */
	public Map<String, List<SelectItem>> getModuleItemMap() {
		return moduleItemMap;
	}

	/**
	 * Sets the module item map.
	 * 
	 * @param moduleItemMap
	 *            the module item map
	 */
	public void setModuleItemMap(Map<String, List<SelectItem>> moduleItemMap) {
		this.moduleItemMap = moduleItemMap;
	}

	/**
	 * getApplicationStatusData - Gets the application status data.
	 * 
	 * @return the applicationStatusList
	 */
	public List<SelectItem> getApplicationStatusData() {
		log.info("ReportBackingBean-getApplicationStatusData");
		List<SelectItem> smApplStatusList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {

			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> applStatusList = reportSO
					.retrieveApplicationStatusList(strLanguage);
			for (Object[] object : applStatusList) {
				SelectItem selectItem = new SelectItem();
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				smApplStatusList.add(selectItem);
			}
		}
		return smApplStatusList;

	}

	/**
	 * getScLOAReports - Gets the sc loa reports.
	 * 
	 * @return the sc loaReportLList
	 */
	public void getScLOAReports() {

		log.info("ReportBackingBean-getLOAReports");
		if (this.getScLOAIdNo() != null && !(this.getScLOAIdNo() == "")
				&& this.getScLOAIdNo().startsWith("LA")) {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

			reqDTO.setLoaNo(this.getScLOAIdNo());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtlByLoaNo(
					reqDTO);

			AwardSupplierReqDTO reqDTOLOA = new AwardSupplierReqDTO();
			log.info("ScLoa--->" + resDTO.getScLoa());
			if (resDTO.getScLoa() != null) {

				reqDTOLOA.setScLoa(resDTO.getScLoa());

				AwardSupplierResDTO reportsResDTO = getQtAwardSO()
						.retrieveLOADtl(reqDTOLOA);

				this.getManagedBean().setScLoa(reportsResDTO.getScLoa());

				this.getManagedBean().setLoaReportDocumentList(
						reportsResDTO.getScLoa().getScLoaReports());

				this.getManagedBean().setScQt(reportsResDTO.getScQt());

			}

		} else {
			this.getManagedBean().setLoaReportDocumentList(
					new ArrayList<ScLoaReport>());
		}
	}

	/**
	 * getQtAwardSO - Gets the qt award so.
	 * 
	 * @return the qtAwardSO
	 */
	private IQTAwardSO getQtAwardSO() {
		log.info("ReportBackingBean-getQtAwardSO");
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}

	/**
	 * downloadReport - Download report as pdf or xlsx files in Letter of
	 * Acceptance.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {

		log.info("ReportBackingBean-downloadReport");

		LOADetailBackingBean loaDetail = new LOADetailBackingBean();

		loaDetail.getManagedBean().setScLoa(this.getManagedBean().getScLoa());
		loaDetail.getManagedBean().setScQt(this.getManagedBean().getScQt());

		if (this.getManagedBean().getScLoa().getContractSigner() == null) {
			this.getManagedBean().getScLoa().setContractSigner(new PmUser());
			loaDetail.getManagedBean().setContractSignerUser(
					this.getManagedBean().getScLoa().getContractSigner());
		} else {
			loaDetail.getManagedBean().setContractSignerUser(
					this.getManagedBean().getScLoa().getContractSigner());
		}

		getSuppLoiLoaMapData(loaDetail);

		if (scLoaReport != null) {
			loaDetail.downloadReport(scLoaReport);
		}
	}

	/**
	 * getSuppLoiLoaMapData - Gets the supp loi loa map data.
	 * 
	 * @param loaDetail
	 *            the loa detail
	 * @return the suppLoiLoaMap
	 */
	private void getSuppLoiLoaMapData(LOADetailBackingBean loaDetail) {
		log.info("ReportBackingBean-getSuppLoiLoaMapData");
		if (this.getManagedBean().getScQt() != null) {
			retrieveSuppLoiLoaMapForReport(this.getManagedBean().getScQt()
					.getQtId());
		}
		loaDetail.getManagedBean().setSuppLoiLoaMap(
				this.getManagedBean().getSuppLoiLoaMap());

	}

	/**
	 * downloadAllReports - Download all reports as pdf or xlsx files in Letter
	 * of Acceptance.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		log.info("ReportBackingBean-downloadAllReports");

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getLoaReportDocumentList())) {

			LOADetailBackingBean loaDetailAll = new LOADetailBackingBean();

			if (this.getManagedBean().getScLoa() != null) {

				loaDetailAll.getManagedBean().setScLoa(
						this.getManagedBean().getScLoa());
				loaDetailAll
						.getManagedBean()
						.getScLoa()
						.setPmStatus(
								this.getManagedBean().getScLoa().getPmStatus());
				loaDetailAll
						.getManagedBean()
						.getScLoa()
						.setScLoaReports(
								(this.getManagedBean().getScLoa()
										.getScLoaReports()));
				loaDetailAll.getManagedBean().setScQt(
						this.getManagedBean().getScQt());

				if (this.getManagedBean().getScLoa().getContractSigner() == null) {
					this.getManagedBean().getScLoa()
							.setContractSigner(new PmUser());
					loaDetailAll.getManagedBean().setContractSignerUser(
							this.getManagedBean().getScLoa()
									.getContractSigner());
				} else {
					loaDetailAll.getManagedBean().setContractSignerUser(
							this.getManagedBean().getScLoa()
									.getContractSigner());
				}

				getSuppLoiLoaMapData(loaDetailAll);
				loaDetailAll.downloadAllReports();
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780026);
		}
	}

	/**
	 * selectAllDocuments - Select all documents based an event.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAllDocuments(AjaxBehaviorEvent event) {
		log.info("Inside of ReportBackingBean-getQtAwardSO()");
		LOADetailBackingBean loaDetailDocs = new LOADetailBackingBean();
		loaDetailDocs.selectAllDocuments(event);
	}

	/**
	 * retrieveSuppLoiLoaMapForReport - will retrieving SuppLoiLoaMap based on
	 * qtid.
	 * 
	 * @param qtId
	 *            the qt id
	 */
	private void retrieveSuppLoiLoaMapForReport(Long qtId) {
		log.info("ReportBackingBean-retrieveSuppLoiLoaMapForReport");

		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocId(qtId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);

			if (resDTO.isSuccessful()) {

				this.getManagedBean().setSuppLoiLoaMap(
						resDTO.getLoiLoaSupplierMap());

			}

		} finally {
			qtAwardSO = null;
		}

	}

	/**
	 * The Class SearchUnspscPagingDataModel.
	 */
	private class SearchUnspscPagingDataModel extends PagingDataModel<CmUnspsc> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search unspsc paging data model.
		 */
		public SearchUnspscPagingDataModel() {
			super("lovTb2");

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		public ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			log.info("ReportBackingBean-pageChangeListener");
			ICodificationSO iCodificationSO = null;

			try {

				for (int i = 0; i < 3; i++) {
					if (getManagedBean().getSearchValue()[i] != null
							&& !getManagedBean().getSearchValue()[i].equals("")) {
						if (getManagedBean().getSearchFieldValue()[i] != null
								&& !getManagedBean().getSearchFieldValue()[i]
										.equals("")) {

							if (getManagedBean().getSearchFieldValue()[i]
									.equals("1"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("unspscCode",
												SearchOperator.OPERATOR_LIKE
														.value(),
												getManagedBean()
														.getSearchValue()[i]));

							else if (getManagedBean().getSearchFieldValue()[i]
									.equals("2"))
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("unspscTitle",
												SearchOperator.OPERATOR_LIKE
														.value(),
												getManagedBean()
														.getSearchValue()[i],
												true));
						}
					}
				}

				iCodificationSO = (ICodificationSO) BusinessDelegate
						.getInstance().getServiceObject("cm",
								ICodificationSO.class);

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				CodificationResDTO resDTO = iCodificationSO
						.searchUNSPSCCodeForReport(reqDTO);

				if (resDTO.isSuccessful()) {

					return resDTO.getResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iCodificationSO = null;
			}

			return null;

		}
	}

	/**
	 * addUnspscCode - Adds the unspsc code.
	 */
	public void addUnspscCode() {
		log.info("ReportBackingBean-addUnspscCode");
		List<SelectItem> searchField = new ArrayList<SelectItem>();

		searchField.add(new SelectItem(1, ResourceBundleUtils.getMessage(
				"lbl.rpt.unspscCodeScreen",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));
		searchField.add(new SelectItem(2, ResourceBundleUtils.getMessage(
				"lbl.rpt.unspscTitle",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())));

		List<SelectItem> searchOperator = new ArrayList<SelectItem>();

		SelectItem item = new SelectItem(
				SearchOperator.OPERATOR_EQUALS.value(),
				ResourceBundleUtils.getMessage("lbl.operator.equals",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
		searchOperator.add(item);
		this.getManagedBean().setSearchField(searchField);
		this.getManagedBean().setSearchOperator(searchOperator);

	}

	/**
	 * searchLOV - Searching for lov object.
	 */
	public void searchLOV() {
		log.info("ReportBackingBean-searchLOV");
		searchUnspscPagingDataModel.reloadData();
	}

	/**
	 * populateToSaveLOV - Populate to save lov.
	 */
	public void populateToSaveLOV() {
		log.info("ReportBackingBean-populateToSaveLOV");
		this.setCmUnspscCode(this.getManagedBean().getSelectedLOV()
				.getUnspscCode());
		this.setCmUnspscTitle(this.getManagedBean().getSelectedLOV()
				.getUnspscTitle());
		enableExtensionTypes();
	}

	/**
	 * resetLOV - Reset lov.
	 * 
	 * @return the searchUnspsc
	 */

	public void resetLOV() {
		log.info("ReportBackingBean-resetLOV");
		getManagedBean().setSearchValue(new String[3]);
		getManagedBean().setSearchFieldValue(new String[3]);
		getManagedBean().setSearchOperatorValue(new String[3]);
		this.getSearchUnspscPagingDataModel().resetData();
	}

	/**
	 * Application status change.
	 */
	public void applicationStatusChange() {
		log.info("ReportBackingBean-applicationStatusChange");
		this.getManagedBean().getRptGenBean().setSmNonMandatoryDateView(false);
		this.getManagedBean().getRptGenBean().setSmApprovalDateView(false);
		this.getManagedBean().getRptGenBean().setSmRejectedDateView(false);

		if (this.getSmApplicationStatus() != null) {

			if (this.getSmApplicationStatus().equalsIgnoreCase("APPROVED")) {
				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmApprovalDateView(true);

			} else if (this.getSmApplicationStatus().equalsIgnoreCase(
					"REJECTED")) {
				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmRejectedDateView(true);
			}
		}
	}

	/**
	 * Change receipt and transaction report type.
	 */
	public void changeRTReportType() {
		log.info("ReportBackingBean-changeRTReportType");
		this.getManagedBean().getRptGenBean()
				.setSmMandatoryReceiptDateView(false);
		this.getManagedBean().getRptGenBean()
				.setSmMandatoryTransactionDateView(false);
		this.getManagedBean().getRptGenBean().setSmMandatoryRTDateView(false);

		if (this.getSmRTReportType() != null) {
			if (this.getSmRTReportType().equalsIgnoreCase(
					ReportConst.SM_REPORT_RESIT)) {
				this.getManagedBean().getRptGenBean()
						.setSmMandatoryRTDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmMandatoryReceiptDateView(true);

			} else if (this.getSmRTReportType().equalsIgnoreCase(
					ReportConst.SM_REPORT_TRANSACTION)) {
				this.getManagedBean().getRptGenBean()
						.setSmMandatoryRTDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmMandatoryTransactionDateView(true);
			}
		}
	}

	/**
	 * Change rt report type for payment.
	 */
	public void changeRTReportTypeForPayment() {
		log.info("ReportBackingBean-changeRTReportTypeForPayment");
		this.getManagedBean().getRptGenBean()
				.setSmNonMandatoryReceiptDateView(false);
		this.getManagedBean().getRptGenBean()
				.setSmNonMandatoryTransactionDateView(false);

		if (this.getSmRTReportType() != null) {
			if (this.getSmRTReportType().equalsIgnoreCase(
					ReportConst.SM_REPORT_RESIT)) {

				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryReceiptDateView(true);

			} else if (this.getSmRTReportType().equalsIgnoreCase(
					ReportConst.SM_REPORT_TRANSACTION)) {
				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryTransactionDateView(true);
			}
		} else {
			this.getManagedBean().getRptGenBean()
					.setSmNonMandatoryTransactionDateView(true);
		}
	}

	/**
	 * Gets the payment type method list.
	 * 
	 * @return the payment type method list
	 */
	public List<SelectItem> getPaymentTypeMethodList() {
		log.info("ReportBackingBean-getPaymentTypeMethodList");
		List<SelectItem> paymentTypeMethodList = new ArrayList<SelectItem>();

		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);

		List<Object[]> paymentTypeList = reportSO
				.retrievePayementTypeMethodList();

		for (Object object : paymentTypeList) {

			SelectItem selectItem1 = new SelectItem();
			selectItem1.setValue(object);
			selectItem1.setLabel(object.toString());
			paymentTypeMethodList.add(selectItem1);

		}

		return paymentTypeMethodList;
	}

	/**
	 * Gets the payment type method list.
	 * 
	 * @return the payment type method list
	 */
	public List<SelectItem> getOrgTypeList() {
		log.info("ReportBackingBean-getOrgTypeList");
		List<SelectItem> orgTypeList = new ArrayList<SelectItem>();

		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);

		List<Object[]> pmOrgTypeList = reportSO
				.retrieveOrgTypeList(strLanguage);

		for (Object[] object : pmOrgTypeList) {

			SelectItem selectItem1 = new SelectItem();
			selectItem1.setValue(object[0].toString());
			selectItem1.setLabel(object[1].toString());
			orgTypeList.add(selectItem1);

		}

		return orgTypeList;
	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 * @return the org type dropdown values
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		log.info("ReportBackingBean-getOrgTypeDropdownValues");
		ISearchBean<PmParameterDesc> searchBean1 = null;
		ISearchBean<PmParameterDesc> searchBean2 = null;
		SelectItem[] items = null;
		SelectItem[] federalItems = null;
		SelectItem[] otherItems = null;

		if (this.reportValue
				.equals(ReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL)) {

			if (this.getPmOrgCategory() != null
					&& this.getPmOrgCategory().equals(ReportConst.FEDERAL_GOVT)) {
				searchBean1 = getOrgTypeSearchBean("orgTypeFederalList");

			} else if (this.getPmOrgCategory() != null
					&& this.getPmOrgCategory().equals(ReportConst.OTHERS)) {
				searchBean1 = getOrgTypeSearchBean("orgTypeOthersList");
			} else {
				searchBean1 = getOrgTypeSearchBean("orgTypeFederalList");
				if (searchBean1 != null) {
					searchBean1.getSortColumns();
					items = getDropdownValuesBySearchBean(
							ParameterTypeConst.ORGANISATION_TYPES, searchBean1);
				}
			}
			if (this.getPmOrgCategory() != null) {
				if (searchBean1 != null) {
					searchBean1.getSortColumns();
					items = getDropdownValuesBySearchBean(
							ParameterTypeConst.ORGANISATION_TYPES, searchBean1);
				}
			}

		} else if (this.reportValue
				.equals(ReportConst.PM_LIST_OF_PROFILES_RPT_VAL)) {

			searchBean1 = getOrgTypeSearchBean("orgTypeFederalList");
			if (searchBean1 != null) {
				searchBean1.getSortColumns();
				federalItems = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean1);
			}
			searchBean2 = getOrgTypeSearchBean("orgTypeOthersList");
			if (searchBean2 != null) {
				searchBean2.getSortColumns();
				otherItems = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean2);
				// for the removal of 'Select One' in the dropdown
				otherItems = (SelectItem[]) ArrayUtils.remove(otherItems, 0);
			}
			items = ArrayUtils.addAll(federalItems, otherItems);

		}
		return items;
	}

	/**
	 * Gets the org type search bean.
	 * 
	 * @param componentId
	 *            the component id
	 * @return the org type search bean
	 */
	private ISearchBean<PmParameterDesc> getOrgTypeSearchBean(String componentId) {
		log.info("ReportBackingBean-getOrgTypeSearchBean");
		ISearchBean<PmParameterDesc> orgTypeSearchBean = super
				.createSearchBean(PmParameterDesc.class, componentId);
		return orgTypeSearchBean;
	}

	/**
	 * Change soft cert selection.
	 */
	public void changeSoftCertSelection() {
		log.info("ReportBackingBean-changeSoftCertSelection");
		this.getManagedBean().setRptGenBean(rptGenBean);
		this.getManagedBean().getRptGenBean().setSmNonMandatoryDateView(false);
		this.getManagedBean().getRptGenBean().setSmSoftCertExpDateView(false);
		this.getManagedBean().getRptGenBean().setSmSoftCertReqDateView(false);

		if (this.getSmRequestExpiryDate() != null) {
			if (this.getSmRequestExpiryDate().equals("R")) {
				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmSoftCertReqDateView(true);
			} else if (this.getSmRequestExpiryDate().equals("E")) {
				this.getManagedBean().getRptGenBean()
						.setSmNonMandatoryDateView(true);
				this.getManagedBean().getRptGenBean()
						.setSmSoftCertExpDateView(true);
			}
		} else {
			this.setSmRequestExpiryDate("R");
			this.getManagedBean().getRptGenBean()
					.setSmNonMandatoryDateView(true);
			this.getManagedBean().getRptGenBean()
					.setSmSoftCertReqDateView(true);

		}
	}

	/**
	 * Change supplier type selection.
	 */
	public void changeSupplierTypeSelection() {
		log.info("ReportBackingBean-changeSupplierTypeSelection");
		if (this.getReportValue() != null) {
			if (this.getReportValue().equalsIgnoreCase(
					ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)) {
				if (null != this.getSmSupplierType()) {
					if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONSULT_TYPE)) {
						retrieveConsultantBusCompanyData();
					} else if (this.getSmSupplierType().equalsIgnoreCase(
							ReportConst.SUPPL_CONTRACT_TYPE)) {
						retrieveContractBusCompanyData();
					}
				} else {
					retrieveAllBusCompanyData();

				}

			}
		}

	}

	/**
	 * Change bus company type.
	 */
	public void changeBusCompanyType() {
		log.info("ReportBackingBean-changeBusCompanyType");
		this.getManagedBean().getRptGenBean()
				.setSmBusCompanyPaidUpAccumlatedShareView(false);
		this.getManagedBean().getRptGenBean().setSmAccmltdCptlSelView(false);
		this.getManagedBean().getRptGenBean().setSmPaidUpCptlSelView(false);
		this.getManagedBean().getRptGenBean().setSmAccmltdShareSelView(false);
		this.getManagedBean().getRptGenBean()
				.setSmPaidAccmltdShareCptlSelView(false);

		if (this.smBusCompanyType != null) {
			if (this.smBusCompanyType.equals("C")
					|| this.smBusCompanyType.equals("D")
					|| this.smBusCompanyType.equals("E")
					|| this.smBusCompanyType.equals("F")
					|| this.smBusCompanyType.equals("G")
					|| this.smBusCompanyType.equals("H")) {

				this.getManagedBean().getRptGenBean()
						.setSmBusCompanyPaidUpAccumlatedShareView(true);
				this.getManagedBean().getRptGenBean()
						.setSmAccmltdCptlSelView(true);

			} else if (this.smBusCompanyType.equals("I")
					|| this.smBusCompanyType.equals("J")
					|| this.smBusCompanyType.equals("B")) {
				this.getManagedBean().getRptGenBean()
						.setSmBusCompanyPaidUpAccumlatedShareView(true);
				this.getManagedBean().getRptGenBean()
						.setSmPaidUpCptlSelView(true);
			} else if (this.smBusCompanyType.equals("K")) {
				this.getManagedBean().getRptGenBean()
						.setSmBusCompanyPaidUpAccumlatedShareView(true);
				this.getManagedBean().getRptGenBean()
						.setSmAccmltdShareSelView(true);
			}
		} else {
			this.getManagedBean().getRptGenBean()
					.setSmBusCompanyPaidUpAccumlatedShareView(true);
			this.getManagedBean().getRptGenBean()
					.setSmPaidAccmltdShareCptlSelView(true);
		}

	}

	/**
	 * Change supplier type and get reg status.
	 */
	public void changeSupplierTypeAndGetRegStatus() {
		if (this.getReportValue().equalsIgnoreCase(
				ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL)) {

			if (this.getSmSupplierType() != null) {
				if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONSULT_TYPE)) {
					retrieveRegStatusListForConsultant();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				} else if (this.getSmSupplierType().equalsIgnoreCase(
						ReportConst.SUPPL_CONTRACT_TYPE)) {
					retrieveRegStatusListForContract();
					this.setSupplierParentCatList(retrievCategoryListBySupplierType(this
							.getSmSupplierType()));
				}
			} else {
				retrieveRegStatusListForAll();
				for (SmSupplierCategory cat : this.getSelectedCatList()) {
					cat.setPmCategoryL1(new PmCategoryL1());
					cat.setPmCategoryL2(new PmCategoryL2());
					cat.setPmCategoryL3(new PmCategoryL3());
				}
				this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
			}
		}
	}

	public String constructMinistryConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}

	public String constructPegwaiPengawalConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.PENGAWAI_PENGAWAL.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.PENGAWAI_PENGAWAL.value(),
				OrganizationType.MINISTRY.value()));
		sb.append("^");
		sb.append(this.getManagedBean().getSelectedMinistry().getOrgProfileId());
		return sb.toString();
	}

	public String constructKumpulanPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.DEPARTMENT.value(),
				OrganizationType.PENGAWAI_PENGAWAL.value()));
		sb.append("^");
		sb.append(this.getManagedBean().getSelectedPegwaiPengawal()
				.getOrgProfileId());
		return sb.toString();
	}

	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil
				.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		sb.append(this.getManagedBean().getSelectedKumpulanPTJ()
				.getOrgProfileId());
		return sb.toString();
	}

	/*
	 * public void updateSearchCriteria(BaseEntity entity) {
	 * log.info("updateSearchCriteria"); PmOrgProfile orgProfile =
	 * (PmOrgProfile) entity;
	 * 
	 * }
	 */

	/*
	 * public void updateKPTJSearchCriteria(BaseEntity entity) {
	 * log.info("updateKPTJSearchCriteria"); PmOrgProfile orgProfile =
	 * (PmOrgProfile) entity; this.getManagedBean().getPlanSearchBean()
	 * .setKptj(orgProfile); }
	 */

	/*
	 * public void updateMinistrySearchCriteria(BaseEntity entity) {
	 * log.info("updateMinistrySearchCriteria"); PmOrgProfile orgProfile =
	 * (PmOrgProfile) entity; this.getManagedBean().getPlanSearchBean()
	 * .setMinistry(orgProfile); }
	 */

	/*
	 * public void retrieveMinistryOrgProfile(){
	 * this.getManagedBean().setSelectedMinistry
	 * (getMinistryOrgProfileByLoginUser()); }
	 */

	public PmOrgProfile getMinistryOrgProfileByLoginUser() {
		IOrgProfileSO orgProfileSO = null;
		PmOrgProfile pmOrgProfile = null;
		try {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
			if (pmOrgProfiles != null && pmOrgProfiles.size() > 0) {
				pmOrgProfile = pmOrgProfiles.get(0);
			}

		} finally {
			orgProfileSO = null;
		}

		return pmOrgProfile;
	}

}