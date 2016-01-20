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
package my.ep.web.bean.report;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;

import java.util.HashMap;
import org.apache.log4j.Logger;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;

import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

// TODO: Auto-generated Javadoc
/**
 * The Class ReportsGenerateBean.
 * 
 * @Author Jangari Kondal Reddy
 */
public class ReportsGenerateBean {

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(ReportsGenerateBean.class);

	/** The state lst. */
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();

	/** The district lst. */
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();

	/** The lev one cat lst. */
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();

	/** The lev two cat lst. */
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();

	/** The lev three cat lst. */
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();

	/** The sts lst. */
	private List<String> stsLst = new ArrayList<String>();

	/** The year list. */
	private List<String> yearList = new ArrayList<String>();

	/** The month list. */
	private List<SelectItem> monthList = new ArrayList<SelectItem>();

	/** The month map. */
	private HashMap<String, String> monthMap = new HashMap<String, String>();

	/** The status lst. */
	private Object[] statusLst;

	/** The from date view. */
	private boolean fromDateView = false;

	/** The to date view. */
	private boolean toDateView = false;

	/** The approved date view. */
	private boolean approvedDateView = false;

	/** The lng user id. */
	private long lngUserId;

	/** The lev one cat lst1. */
	private List<SelectItem> levOneCatLst1 = new ArrayList<SelectItem>();

	/** The madatory indicator view. */
	private boolean madatoryIndicatorView = false;

	/** The cm madatory indicator view. */
	private boolean cmMadatoryIndicatorView = false;

	/** The enable criteria menu view. */
	private boolean enableCriteriaMenuView = false;

	/** The report category view. */
	private boolean reportCategoryView = false;

	/** The collector gov registration chk view. */
	private boolean collectorGovRegistrationChkView = false;

	/** The collector cdc registration chk view. */
	private boolean collectorCDCRegistrationChkView = false;
	
	
	/** The sm application approved view. */
	private boolean smApplicationApprovedView=false;
	
	/** The sm application rejected view. */
	private boolean smApplicationRejectedView=false;
 
	/** The sm application verification performance view. */
	private boolean smApplicationVerificationPerformanceView=false;
	
	/** The sm application verification view. */
	private boolean smApplicationVerificationView=false;
	
	/** The sm agency statistical status view. */
	private boolean smAgencyStatisticalStatusView=false;
	
	/** The sm category statistic view. */
	private boolean smCategoryStatisticView=false;
	
	/** The sm category statistic by state view. */
	private boolean smCategoryStatisticByStateView=false;
	
	/** The sm category statistic by year view. */
	private boolean smCategoryStatisticByYearView=false;
	
	/** The sm company address list view. */
	private boolean smCompanyAddressListView=false;
	
	/** The sm daily receipts es view. */
	private boolean smDailyReceiptsESView=false;
	
	/** The sm delete pending application view. */
	private boolean smDeletePendingApplicationView=false;
	
	/** The sm laporan terimaan harian view. */
	private boolean smLaporanTerimaanHarianView=false;
	
	/** The sm list of application view. */
	private boolean smListOfApplicationView=false;
	
	/** The sm list of application pending for sv view. */
	private boolean smListOfApplicationPendingForSVView=false;
	
	/** The sm list of application status view. */
	private boolean smListOfApplicationStatusView=false;
	
	/** The sm list of companies sort alphabet view. */
	private boolean smListOfCompaniesSortAlphabetView=false;
	
	/** The sm list of companies srsc view. */
	private boolean smListOfCompaniesSRSCView=false;
	
	/** The sm list of companies with dohe view. */
	private boolean smListOfCompaniesWithDOHEView=false;
	
	/** The sm list of daily application basic and mof view. */
	private boolean smListOfDailyApplicationBasicAndMOFView=false;
	
	/** The sm list of daily registered application view. */
	private boolean smListOfDailyRegisteredApplicationView=false;
	
	/** The sm list of dohe view. */
	private boolean smListOfDOHEView=false;
	
	/** The sm list of gst reg supplier view. */
	private boolean smListOfGSTRegSupplierView=false;
	
	/** The sm list delete mof account view. */
	private boolean smListDeleteMOFAccountView=false;
	
	/** The sm list of offline sdps view. */
	private boolean smListOfOfflineSDPSView=false;
	
	/** The sm list ps more view. */
	private boolean smListPSMoreView=false;
	
	/** The sm list of expired company view. */
	private boolean smListOfExpiredCompanyView=false;
	
	/** The sm list of svosvl view. */
	private boolean smListOfSVOSVLView=false;
	
	/** The sm list of soa view. */
	private boolean smListOfSOAView=false;
	
	/** The sm list of soppsv view. */
	private boolean smListOfSOPPSVView=false;
	
	/** The sm list of socpba view. */
	private boolean smListOfSOCPBAView=false;
	
	/** The sm list of users scr view. */
	private boolean smListOfUsersSCRView=false;
	
	/** The sm list of pprf view. */
	private boolean smListOfPPRFView=false;
	
	/** The sm monthly report view. */
	private boolean smMonthlyReportView=false;
	
	/** The sm profile reg company view. */
	private boolean smProfileRegCompanyView=false;
	
	/** The sm qrs view. */
	private boolean smQRSView=false;
	
	/** The sm statistic list cbc view. */
	private boolean smStatisticListCBCView=false;
	
	/** The sm supplier performance view. */
	private boolean smSupplierPerformanceView=false;
	
	/** The sm supplier statistic view. */
	private boolean smSupplierStatisticView=false;
	
	/** The sm supplier statistic by category view. */
	private boolean smSupplierStatisticByCategoryView=false;
	
	/** The sm threshold or supplier transaction view. */
	private boolean smThresholdOrSupplierTransactionView=false;
	
	/** The sm threshold year view. */
	private boolean smThresholdYearView=false;
	
	/** The sm total no of daily ar view. */
	private boolean smTotalNoOfDailyARView=false;
	
	/** The sm total no of yearly ar view. */
	private boolean smTotalNoOfYearlyARView=false;
	
	/** The sm virtual certificate relased list view. */
	private boolean smVirtualCertificateRelasedListView=false;
	
	/** The sm collection receipt transation view. */
	private boolean smCollectionReceiptTransationView=false;
	
	/** The sm collection report payment method view. */
	private boolean smCollectionReportPaymentMethodView=false;
	
	/** The sm collection report clasification code view. */
	private boolean smCollectionReportClasificationCodeView=false;
	
	/** The sm revenue collection report view. */
	private boolean smRevenueCollectionReportView=false;
	
	/** The sm list of daily receipt view. */
	private boolean smListOfDailyReceiptView=false;	

	/** The generate button view. */
	private boolean generateButtonView = false;
	// sc module
	/** The extra two column view. */
	private boolean extraTwoColumnView = false;

	/** The sc quotation no view. */
	private boolean scQuotationNoView = false;

	/** The sc created from view. */
	private boolean scCreatedFromView = false;

	/** The sc status view. */
	private boolean scStatusView = false;

	/** The sc ptj view. */
	private boolean scPtjView = false;

	/** The sc desk officer view. */
	private boolean scDeskOfficerView = false;

	/** The sc rstrctd chk view. */
	private boolean scRstrctdChkView = false;

	/** The sc tender chk view. */
	private boolean scTenderChkView = false;

	/** The sc quotation chk view. */
	private boolean scQuotationChkView = false;

	/** The sc procure all chk view. */
	private boolean scProcureAllChkView = false;

	/** The sc publish date view. */
	private boolean scPublishDateView = false;

	/** The sc procure all edit view. */
	private boolean scProcureAllEditView = false;

	/** The sc procure quote edit view. */
	private boolean scProcureQuoteEditView = false;

	/** The sc procure tender edit view. */
	private boolean scProcureTenderEditView = false;

	/** The sc procure restrctd edit view. */
	private boolean scProcureRestrctdEditView = false;

	/** The sc sone crtd from date view. */
	private boolean scSoneCrtdFromDateView = false;

	/** The sc requisener id view. */
	private boolean scRequisenerIdView = false;

	/** The sc supplier view. */
	private boolean scSupplierView = false;

	/** The sc p ino view. */
	private boolean scPInoView = false;

	/** The sc sone status view. */
	private boolean scSoneStatusView = false;

	/** The sc prc rpt group view. */
	private boolean scPrcRptGroupView = false;

	/** The sc pi dtls prc rpt group view. */
	private boolean scPIDtlsPrcRptGroupView = false;

	/** The sc procurement type view. */
	private boolean scProcurementTypeView = false;

	/** The sc process rpt quotation no view. */
	private boolean scProcessRptQuotationNoView = false;

	/** The sc mandatory process rpt quotation no view. */
	private boolean scMandatoryProcessRptQuotationNoView = false;

	/** The sc common status view. */
	private boolean scCommonStatusView = false;

	/** The sc laprnpurchdefault group view. */
	private boolean scLAPRNPURCHDEFAULTGroupView = false;

	/** The sc requestnotesummary group view. */
	private boolean scREQUESTNOTESUMMARYGroupView = false;

	/** The sc pid no view. */
	private boolean scPIDNoView = false;

	/** The sc contract or pkp type view. */
	private boolean scContractOrPKPTypeView = false;

	/** The sc contract or pkp no view. */
	private boolean scContractOrPKPNoView = false;

	/** The sc contract or pkp name view. */
	private boolean scContractOrPKPNameView = false;

	/** The sc request quote no view. */
	private boolean scRequestQuoteNoView = false;

	/** The sc request note status view. */
	private boolean scRequestNoteStatusView = false;

	/** The sc simple quote status view. */
	private boolean scSimpleQuoteStatusView = false;

	/** The sc simple quote dtl group view. */
	private boolean scSimpleQuoteDtlGroupView = false;

	/** The sc purchase in default status view. */
	private boolean scPurchaseInDefaultStatusView = false;

	/** The sc simple quote dtl without requi group view. */
	private boolean scSimpleQuoteDtlWithoutRequiGroupView = false;

	/** The sc soa status view. */
	private boolean scSOAStatusView = false;

	/** The sc simple quote type view. */
	private boolean scSimpleQuoteTypeView = false;

	/** The sc loa no view. */
	private boolean scLOANoView = false;

	/** The sc loa content view. */
	private boolean scLOAContentView = false;
	// cm module
	/** The cm item type view. */
	private boolean cmItemTypeView = false;

	/** The cm item status view. */
	private boolean cmItemStatusView = false;

	/** The cm unspsc code view. */
	private boolean cmUnspscCodeView = false;

	/** The cm unspsc title view. */
	private boolean cmUnspscTitleView = false;

	/** The cm company name view. */
	private boolean cmCompanyNameView = false;

	/** The cm registration type view. */
	private boolean cmRegistrationTypeView = false;

	/** The cm item category view. */
	private boolean cmItemCategoryView = false;

	/** The cm item name view. */
	private boolean cmItemNameView = false;

	/** The cm ep number view. */
	private boolean cmEpNumberView = false;

	/** The cm business reg no view. */
	private boolean cmBusinessRegNoView = false;

	/** The cm ic number view. */
	private boolean cmIcNumberView = false;

	/** The cm mof number view. */
	private boolean cmMofNumberView = false;

	/** The cm extention code view. */
	private boolean cmExtentionCodeView = false;

	/** The cm extention type view. */
	private boolean cmExtentionTypeView = false;

	/** The cm extention brand view. */
	private boolean cmExtentionBrandView = false;

	/** The cm extention measurment view. */
	private boolean cmExtentionMeasurmentView = false;

	/** The cm extention colour view. */
	private boolean cmExtentionColourView = false;

	/** The cm proof identity view. */
	private boolean cmProofIdentityView = false;

	/** The cm basic reg view. */
	private boolean cmBasicRegView = false;

	/** The cm mof reg view. */
	private boolean cmMofRegView = false;

	/** The cm suppl or admin view. */
	private boolean cmSupplOrAdminView = false;

	/** The cm extention view. */
	private boolean cmExtentionView = false;

	/** The cm group one view. */
	private boolean cmGroupOneView = false;

	/** The cm published date view. */
	private boolean cmPublishedDateView = false;

	/** The cm catelog date view. */
	private boolean cmCatelogDateView = false;

	/** The cm approved date view. */
	private boolean cmApprovedDateView = false;

	/** The cm submitted date view. */
	private boolean cmSubmittedDateView = false;

	/** The cm expired date view. */
	private boolean cmExpiredDateView = false;

	/** The cm item validity date view. */
	private boolean cmItemValidityDateView = false;

	/** The cm request number view. */
	private boolean cmRequestNumberView = false;

	// SM module
	/** The sm company name view. */
	private boolean smCompanyNameView = false;

	/** The sm der sm receipt no view. */
	private boolean smDERSmReceiptNoView = false;

	/** The sm ec mandatory supplier type view. */
	private boolean smECMandatorySupplierTypeView = false;

	/** The sm lp company name view. */
	private boolean smLPCompanyNameView = false;

	/** The sm ar mof reg no view. */
	private boolean smARMofRegNoView = false;

	/** The sm pp company name view. */
	private boolean smPPCompanyNameView = false;

	/** The sm doh company name view. */
	private boolean smDOHCompanyNameView = false;

	/** The sm application no view. */
	private boolean smApplicationNoView = false;

	/** The sm zone view. */
	private boolean smZoneView = false;

	/** The sm zone label view. */
	private boolean smZoneLabelView = false;

	/** The sm supplier type view. */
	private boolean smSupplierTypeView = false;

	/** The sm la application no view. */
	private boolean smLAApplicationNoView = false;

	/** The sm la registration no view. */
	private boolean smLARegistrationNoView = false;

	/** The sm pr mandatory supplier type view. */
	private boolean smPRMandatorySupplierTypeView = false;

	/** The sm mandatory doc supplier type view. */
	private boolean smMandatoryDOCSupplierTypeView = false;

	/** The sm mandatory for code view. */
	private boolean smMandatoryForCodeView = false;

	/** The sm mof reg no view. */
	private boolean smMofRegNoView = false;

	/** The sm pr mandatory mof reg no view. */
	private boolean smPRMandatoryMofRegNoView = false;

	/** The sm ep mof reg no view. */
	private boolean smEpMofRegNoView = false;

	/** The sm category view. */
	private boolean smCategoryView = false;

	/** The sm no of category view. */
	private boolean smNoOfCategoryView = false;

	/** The sm main category view. */
	private boolean smMainCategoryView = false;

	/** The sm gen company name view. */
	private boolean smGenCompanyNameView = false;

	/** The sm supp company name view. */
	private boolean smSuppCompanyNameView = false;

	/** The sm lc bus company registration status view. */
	private boolean smLCBusCompanyRegistrationStatusView = false;

	/** The sm lc mandatory supplier type view. */
	private boolean smLCMandatorySupplierTypeView = false;
	/** The sm mandatory gen supplier type view. */
	private boolean smMandatoryGenSupplierTypeView;

	/** The sm sub category view. */
	private boolean smSubCategoryView = false;

	/** The sm supplier name view. */
	private boolean smSupplierNameView = false;

	/** The sm supplier name. */
	private boolean smSupplierName = false;

	/** The sm sub category division view. */
	private boolean smSubCategoryDivisionView = false;

	/** The sm registration status view. */
	private boolean smRegistrationStatusView = false;

	/** The sm consultant registration status view. */
	private boolean smConsultantRegistrationStatusView = false;

	/** The sm payment type view. */
	private boolean smPaymentTypeView = false;

	/** The sm payment type two view. */
	private boolean smPaymentTypeTwoView = false;

	/** The sm mandatory payment type view. */
	private boolean smMandatoryPaymentTypeView = false;

	/** The sm application type view. */
	private boolean smApplicationTypeView = false;

	/** The sm state view. */
	private boolean smStateView = false;

	/** The sm sup state view. */
	private boolean smSupStateView = false;

	/** The sm district view. */
	private boolean smDistrictView = false;

	/** The sm report type view. */
	private boolean smReportTypeView = false;

	/** The sm qr all supplier type view. */
	private boolean smQRAllSupplierTypeView = false;

	/** The sm sl bus company type view. */
	private boolean smSLBusCompanyTypeView = false;

	/** The sm mandatory recipt transaction report type view. */
	private boolean smMandatoryReciptTransactionReportTypeView = false;

	/** The sm non mandatory recipt transaction report type view. */
	private boolean smNonMandatoryReciptTransactionReportTypeView = false;

	/** The sm mandatory payment method view. */
	private boolean smMandatoryPaymentMethodView = false;

	/** The sm application status view. */
	private boolean smApplicationStatusView = false;

	/** The Sm gen application status view. */
	private boolean SmGenApplicationStatusView = false;

	/** The sm mandatory doh supplier type view. */
	private boolean smMandatoryDOHSupplierTypeView = false;

	/** The sm gen identification number view. */
	private boolean smGenIdentificationNumberView = false;

	/** The sm ps identification number view. */
	private boolean smPSIdentificationNumberView = false;

	/** The sm gen district view. */
	private boolean smGenDistrictView = false;

	/** The sm sa application no view. */
	private boolean smSAApplicationNoView = false;

	/** The sm extra two column view. */
	private boolean smExtraTwoColumnView = false;

	/** The sm extra two column two view. */
	private boolean smExtraTwoColumnTwoView = false;

	/** The sm registration type view. */
	private boolean smRegistrationTypeView = false;

	/** The sm mof reg view. */
	private boolean smMofRegView = false;

	/** The sm mandatory supplier type view. */
	private boolean smMandatorySupplierTypeView = false;

	/** The sm com supplier type view. */
	private boolean smComSupplierTypeView = false;

	/** The sm scp supplier type view. */
	private boolean smSCPSupplierTypeView = false;

	/** The sm th mandatory supplier type view. */
	private boolean smTHMandatorySupplierTypeView = false;

	/** The sm gst supplier type view. */
	private boolean smGSTSupplierTypeView = false;

	/** The sm doh mandatory supplier type view. */
	private boolean smDOHMandatorySupplierTypeView = false;

	/** The sm do company name view. */
	private boolean smDOCompanyNameView = false;

	/** The sm bus company registration status view. */
	private boolean smBusCompanyRegistrationStatusView = false;

	/** The sm mandatory all supplier type view. */
	private boolean smMandatoryAllSupplierTypeView = false;

	/** The sm del application no view. */
	private boolean smDelApplicationNoView = false;

	/** The sm mof mandatory supplier type view. */
	private boolean smMOFMandatorySupplierTypeView = false;

	/** The sm sv mandatory application no view. */
	private boolean smSVMandatoryApplicationNoView = false;

	/** The sm mandatory application date view. */
	private boolean smMandatoryApplicationDateView = false;

	/** The sm mandatory application status view. */
	private boolean smMandatoryApplicationStatusView = false;

	/** The sm mandatory application status two view. */
	private boolean smMandatoryApplicationStatusTwoView = false;

	/** The sm mandatory application status three view. */
	private boolean smMandatoryApplicationStatusThreeView = false;

	/** The sm application status three view. */
	private boolean smApplicationStatusThreeView = false;

	/** The sm mandatory mof reg no view. */
	private boolean smMandatoryMofRegNoView = false;

	/** The sm bus company type view. */
	private boolean smBusCompanyTypeView = false;

	/** The sm mandatory expiry date view. */
	private boolean smMandatoryExpiryDateView = false;

	/** The sm mandatory printed date view. */
	private boolean smMandatoryPrintedDateView = false;

	/** The sm mandatory category view. */
	private boolean smMandatoryCategoryView = false;

	/** The sm non mandatory category view. */
	private boolean smNonMandatoryCategoryView = false;

	/** The sm main cat mandatory category view. */
	private boolean smMainCatMandatoryCategoryView = false;

	/** The sm category by supplier type view. */
	private boolean smCategoryBySupplierTypeView = false;

	/** The sm daily receipt no view. */
	private boolean smDailyReceiptNoView = false;

	/** The sm mandatory from date view. */
	private boolean smMandatoryFromDateView = false;

	/** The sm tn mandatory report type view. */
	private boolean smTNMandatoryReportTypeView = false;

	/** The sm tn application date view. */
	private boolean smTNApplicationDateView = false;

	/** The sm virtual cert date view. */
	private boolean smVirtualCertDateView = false;

	/** The sm mandatory state view. */
	private boolean smMandatoryStateView = false;

	/** The sm sv mandatory state view. */
	private boolean smSVMandatoryStateView = false;

	/** The sm mandatory district view. */
	private boolean smMandatoryDistrictView = false;

	/** The sm mandatory registration status view. */
	private boolean smMandatoryRegistrationStatusView = false;

	/** The sm mandatory created from year view. */
	private boolean smMandatoryCreatedFromYearView = false;

	/** The sm from date view. */
	private boolean smFromDateView = false;

	/** The sm mandatory year from report type view. */
	private boolean smMandatoryYearFromReportTypeView = false;

	/** The sm mandatory company name view. */
	private boolean smMandatoryCompanyNameView = false;

	/** The sm mandatory registration type view. */
	private boolean smMandatoryRegistrationTypeView = false;

	/** The sm mandatory monthly rpt by year view. */
	private boolean smMandatoryMonthlyRptByYearView = false;

	/** The sm mandatory static by state view. */
	private boolean smMandatoryStaticByStateView = false;

	/** The sm static state view. */
	private boolean smStaticStateView = false;

	/** The sm da registration type view. */
	private boolean smDARegistrationTypeView = false;

	/** The sm mandatory application no view. */
	private boolean smMandatoryApplicationNoView = false;

	/** The sm sv mandatory district view. */
	private boolean smSVMandatoryDistrictView = false;

	/** The sm svo user view. */
	private boolean smSVOUserView = false;

	/** The sm mandatory report type view. */
	private boolean smMandatoryReportTypeView = false;

	/** The sm mandatory recieved report type view. */
	private boolean smMandatoryRecievedReportTypeView = false;

	/** The sm mandatory approved report type view. */
	private boolean smMandatoryApprovedReportTypeView = false;

	/** The sm mandatory rejected report type view. */
	private boolean smMandatoryRejectedReportTypeView = false;

	/** The sm mandatory registered report type date view. */
	private boolean smMandatoryRegisteredReportTypeDateView = false;

	/** The sm mandatory zone view. */
	private boolean smMandatoryZoneView = false;

	/** The sm mandatory ep admin ptj admin zone view. */
	private boolean smMandatoryEPAdminPTJAdminZoneView = false;

	/** The sm ep admin ptj admin zone view. */
	private boolean smEPAdminPTJAdminZoneView = false;

	/** The sm common zone view. */
	private boolean smCommonZoneView = false;

	/** The sm basic reg view. */
	private boolean smBasicRegView = false;

	/** The sm mandatory both supplier type view. */
	private boolean smMandatoryBothSupplierTypeView = false;

	/** The sm year text view. */
	private boolean smYearTextView = false;

	/** The sm mandatory received from date view. */
	private boolean smMandatoryReceivedFromDateView = false;

	/** The sm mandatory created from date view. */
	private boolean smMandatoryCreatedFromDateView = false;

	/** The sm rejected date view. */
	private boolean smRejectedDateView = false;

	/** The sm registered date view. */
	private boolean smRegisteredDateView = false;

	/** The sm schedule site visit date view. */
	private boolean smScheduleSiteVisitDateView = false;

	/** The sm soft cert req date view. */
	private boolean smSoftCertReqDateView = false;

	/** The sm soft cert exp date view. */
	private boolean smSoftCertExpDateView = false;

	/** The sm activation date view. */
	private boolean smActivationDateView = false;

	/** The sm submission date view. */
	private boolean smSubmissionDateView = false;

	/** The sm pending submission date view. */
	private boolean smPendingSubmissionDateView = false;

	/** The sm expiry date view. */
	private boolean smExpiryDateView = false;

	/** The sm created date view. */
	private boolean smCreatedDateView = false;

	/** The sm application date view. */
	private boolean smApplicationDateView = false;

	/** The sm application type two view. */
	private boolean smApplicationTypeTwoView = false;

	/** The sm mandatory date view. */
	private boolean smMandatoryDateView = false;

	/** The sm non mandatory date view. */
	private boolean smNonMandatoryDateView = false;

	/** The sm mof account end date view. */
	private boolean smMOFAccountEndDateView = false;

	/** The sm application received date view. */
	private boolean smApplicationReceivedDateView = false;

	/** The sm mandatory rt date view. */
	private boolean smMandatoryRTDateView = false;

	/** The sm non mandatory rt date view. */
	private boolean smNonMandatoryRTDateView = false;

	/** The sm mandatory for date view. */
	private boolean smMandatoryForDateView = false;

	/** The sm mandatory receipt date view. */
	private boolean smMandatoryReceiptDateView = false;

	/** The sm non mandatory receipt date view. */
	private boolean smNonMandatoryReceiptDateView = false;

	/** The sm mandatory transact date view. */
	private boolean smMandatoryTransactionDateView = false;

	/** The sm non mandatory transaction date view. */
	private boolean smNonMandatoryTransactionDateView = false;

	/** The sm cp supplier name view. */
	private boolean smCPSupplierNameView = false;

	/** The sm payment date view. */
	private boolean smPaymentDateView = false;

	/** The sm payment pr date view. */
	private boolean smPaymentPRDateView = false;

	/** The sm role view. */
	private boolean smRoleView = false;

	/** The sm ep no view. */
	private boolean smEpNoView = false;

	/** The sm of application no view. */
	private boolean smOFApplicationNoView = false;

	/** The sm registration no view. */
	private boolean smRegistrationNoView = false;

	/** The sm la mof reg no view. */
	private boolean smLAMofRegNoView = false;

	/** The sm supporting document mode view. */
	private boolean smSupportingDocumentModeView = false;

	/** The sm mandatory supporting document mode view. */
	private boolean smMandatorySupportingDocumentModeView = false;

	/** The sm cd mandatory all supplier type view. */
	private boolean smCDMandatoryAllSupplierTypeView = false;

	/** The sm de mandatory all supplier type view. */
	private boolean smDEMandatoryAllSupplierTypeView = false;

	/** The sm cd company name view. */
	private boolean smCDCompanyNameView = false;

	/** The sm application type five view. */
	private boolean smApplicationTypeFiveView = false;

	/** The sm application type six view. */
	private boolean smApplicationTypeSixView = false;

	/** The sm supporting document status view. */
	private boolean smSupportingDocumentStatusView = false;

	/** The sm mof reg no from view. */
	private boolean smMofRegNoFromView = false;

	/** The sm mof reg no to view. */
	private boolean smMofRegNoToView = false;

	/** The sm lc mof reg no from view. */
	private boolean smLCMofRegNoFromView = false;

	/** The sm lcmof registered date view. */
	private boolean smLCMOFRegisteredDateView = false;

	/** The sm bus company paid up accumlated share view. */
	private boolean smBusCompanyPaidUpAccumlatedShareView = false;

	/** The sm paid up cptl sel view. */
	private boolean smPaidUpCptlSelView = false;

	/** The sm accmltd cptl sel view. */
	private boolean smAccmltdCptlSelView = false;

	/** The sm accmltd share sel view. */
	private boolean smAccmltdShareSelView = false;

	/** The sm paid accmltd share cptl sel view. */
	private boolean smPaidAccmltdShareCptlSelView = false;

	/** The sm paid up capital frm view. */
	private boolean smPaidUpCapitalFrmView = false;

	/** The sm paid up cptl date view. */
	private boolean smPaidUpCptlDateView = false;

	/** The sm accmltd cptl date view. */
	private boolean smAccmltdCptlDateView = false;

	/** The sm paid up capital to view. */
	private boolean smPaidUpCapitalToView = false;

	/** The sm paid up capital naccum view. */
	private boolean smPaidUpCapitalNaccumView = false;

	/** The sm mandatory mof reg no from view. */
	private boolean smMandatoryMofRegNoFromView = false;

	/** The sm mandatory mof reg no to view. */
	private boolean smMandatoryMofRegNoToView = false;

	/** The sm personnel role view. */
	private boolean smPersonnelRoleView = false;

	/** The sm personnel staff role view. */
	private boolean smPersonnelStaffRoleView = false;

	/** The sm identfication type view. */
	private boolean smIdentficationTypeView = false;

	/** The sm identification number view. */
	private boolean smIdentificationNumberView = false;

	/** The sm cancellation by view. */
	private boolean smCancellationByView = false;

	/** The sm can application date view. */
	private boolean smCanApplicationDateView = false;

	/** The sm mof reg no two view. */
	private boolean smMofRegNoTwoView = false;

	/** The sm registration type two view. */
	private boolean smRegistrationTypeTwoView = false;

	/** The sm dr mof reg no two view. */
	private boolean smDRMofRegNoTwoView = false;

	/** The sm active submissive from date view. */
	private boolean smActiveSubmissiveFromDateView = false;

	/** The sm act submis all date view. */
	private boolean smActSubmisAllDateView = false;

	/** The sm processing officer name view. */
	private boolean smProcessingOfficerNameView = false;

	/** The sm report type two view. */
	private boolean smReportTypeTwoView = false;

	/** The sm both supplier type view. */
	private boolean smBothSupplierTypeView = false;

	/** The sm both mandatory supplier type view. */
	private boolean smBothMandatorySupplierTypeView = false;

	/** The sm receipt no view. */
	private boolean smReceiptNoView = false;

	/** The sm processing type view. */
	private boolean smProcessingTypeView = false;

	/** The sm payment method view. */
	private boolean smPaymentMethodView = false;

	/** The sm from month view. */
	private boolean smFromMonthView = false;

	/** The sm to month view. */
	private boolean smToMonthView = false;

	/** The sm supplier view. */
	private boolean smSupplierView = false;

	/** The sm th registration type view. */
	private boolean smTHRegistrationTypeView = false;

	/** The sm company name dropdown view. */
	private boolean smCompanyNameDropdownView = false;

	/** The sm from and to year view. */
	private boolean smFromAndToYearView = false;

	/** The sm threshold val year from view. */
	private boolean smThresholdValYearFromView = false;

	/** The sm user name view. */
	private boolean smUserNameView = false;

	/** The sm request expiry date view. */
	private boolean smRequestExpiryDateView = false;

	/** The sm year from view. */
	private boolean smYearFromView = false;

	/** The sm threshold year from view. */
	private boolean smThresholdYearFromView = false;

	/** The sm approval date view. */
	private boolean smApprovalDateView = false;

	/** The sm mof registered date view. */
	private boolean smMOFRegisteredDateView = false;

	/** The sm all supplier type view. */
	private boolean smAllSupplierTypeView = false;

	/** The sm ps supplier type view. */
	private boolean smPSSupplierTypeView = false;

	/** The sm profile status view. */
	private boolean smProfileStatusView = false;

	/** The sm mandatory expected site visit date view. */
	private boolean smMandatoryExpectedSiteVisitDateView = false;

	/** The sm mandatory expected site visi rptt date view. */
	private boolean smMandatoryExpectedSiteVisiRpttDateView = false;

	/** The sm mandatory actual site visit date view. */
	private boolean smMandatoryActualSiteVisitDateView = false;

	/** The sm gst effective date view. */
	private boolean smGSTEffectiveDateView = false;

	/** The sm gst registration no view. */
	private boolean smGSTRegistrationNoView = false;

	/** The sm gst registration type view. */
	private boolean smGSTRegistrationTypeView = false;

	/** The sm can mandatory supplier type view. */
	private boolean smCanMandatorySupplierTypeView = false;

	/** The sm gs registration no view. */
	private boolean smGSRegistrationNoView = false;

	/** The sm gst registered status view. */
	private boolean smGSTRegisteredStatusView = false;

	/** The sm mandatory gst registered status view. */
	private boolean smMandatoryGSTRegisteredStatusView = false;

	/** The sm payment type three view. */
	private boolean smPaymentTypeThreeView = false;

	/** The sm payment to view. */
	private boolean smPaymentToView = false;
	// FL module
	/** The fl ptj view. */
	private boolean flPtjView = false;

	/** The fl contract no view. */
	private boolean flContractNoView = false;

	/** The fl sst view. */
	private boolean flSstView = false;

	/** The fl jabatan view. */
	private boolean flJabatanView = false;

	/** The fl ministry view. */
	private boolean flMinistryView = false;

	/** The fl supplier view. */
	private boolean flSupplierView = false;

	/** The fl status view. */
	private boolean flStatusView = false;

	/** The fl duration view. */
	private boolean flDurationView = false;

	/** The fl recieving officer view. */
	private boolean flRecievingOfficerView = false;

	/** The fl delivery address view. */
	private boolean flDeliveryAddressView = false;

	/** The fl rec note no view. */
	private boolean flRecNoteNoView = false;

	/** The fl contract type view. */
	private boolean flContractTypeView = false;

	/** The fl requisitioner view. */
	private boolean flRequisitionerView = false;

	/** The fl mto contract view. */
	private boolean flMtoContractView = false;

	/** The fl invoice no view. */
	private boolean flInvoiceNoView = false;

	/** The fl deduction type view. */
	private boolean flDeductionTypeView = false;

	/** The fl cr no view. */
	private boolean flCrNoView = false;

	/** The fl approver name view. */
	private boolean flApproverNameView = false;

	/** The fl document type view. */
	private boolean flDocumentTypeView = false;

	/** The fl criteria view. */
	private boolean flCriteriaView = false;

	/** The fl scoring view. */
	private boolean flScoringView = false;

	/** The fl pkp no view. */
	private boolean flPkpNoView = false;

	/** The fl delivery order no view. */
	private boolean flDeliveryOrderNoView = false;

	/** The fl item code view. */
	private boolean flItemCodeView = false;

	/** The fl po amount view. */
	private boolean flPoAmountView = false;

	/** The fl pr no view. */
	private boolean flPrNoView = false;

	/** The fl pmo view. */
	private boolean flPmoView = false;

	/** The fl item name view. */
	private boolean flItemNameView = false;

	/** The fl item type view. */
	private boolean flItemTypeView = false;

	/** The fl contract order view. */
	private boolean flContractOrderView = false;

	/** The fl contract request view. */
	private boolean flContractRequestView = false;

	/** The fl purchase order no view. */
	private boolean flPurchaseOrderNoView = false;

	/** The fl group one view. */
	private boolean flGroupOneView = false;

	/** The fl group one with suppl view. */
	private boolean flGroupOneWithSupplView = false;

	/** The fl group one without suppl view. */
	private boolean flGroupOneWithoutSupplView = false;

	/** The fl group two view. */
	private boolean flGroupTwoView = false;

	/** The fl group two without suppl view. */
	private boolean flGroupTwoWithoutSupplView = false;

	/** The fl group two with suppl view. */
	private boolean flGroupTwoWithSupplView = false;

	/** The fl group three view. */
	private boolean flGroupThreeView = false;

	/** The fl group three without suppl view. */
	private boolean flGroupThreeWithoutSupplView = false;

	/** The fl group three all rolecodes without suppl view. */
	private boolean flGroupThreeAllRolecodesWithoutSupplView = false;

	/** The fl group three with suppl view. */
	private boolean flGroupThreeWithSupplView = false;

	/** The fl group four view. */
	private boolean flGroupFourView = false;

	/** The fl group four without suppl view. */
	private boolean flGroupFourWithoutSupplView = false;

	/** The fl group four with suppl view. */
	private boolean flGroupFourWithSupplView = false;

	/** The fl group five view. */
	private boolean flGroupFiveView = false;

	/** The fl group five without suppl view. */
	private boolean flGroupFiveWithoutSupplView = false;

	/** The fl group five with suppl view. */
	private boolean flGroupFiveWithSupplView = false;

	/** The fl group six view. */
	private boolean flGroupSixView = false;

	/** The fl group six without suppl view. */
	private boolean flGroupSixWithoutSupplView = false;

	/** The fl group six with suppl view. */
	private boolean flGroupSixWithSupplView = false;

	/** The fl group seven view. */
	private boolean flGroupSevenView = false;

	/** The fl group seven without suppl view. */
	private boolean flGroupSevenWithoutSupplView = false;

	/** The fl group seven with suppl view. */
	private boolean flGroupSevenWithSupplView = false;

	/** The fl group eight view. */
	private boolean flGroupEightView = false;

	/** The fl group eight without suppl view. */
	private boolean flGroupEightWithoutSupplView = false;

	/** The fl group eight with suppl view. */
	private boolean flGroupEightWithSupplView = false;

	/** The fl group nine view. */
	private boolean flGroupNineView = false;

	/** The fl group nine without suppl view. */
	private boolean flGroupNineWithoutSupplView = false;

	/** The fl group nine with suppl view. */
	private boolean flGroupNineWithSupplView = false;

	/** The fl group ten view. */
	private boolean flGroupTenView = false;

	/** The fl group ten without suppl view. */
	private boolean flGroupTenWithoutSupplView = false;

	/** The fl group ten with suppl view. */
	private boolean flGroupTenWithSupplView = false;

	/** The fl group eleven view. */
	private boolean flGroupElevenView = false;

	/** The fl group eleven without suppl view. */
	private boolean flGroupElevenWithoutSupplView = false;

	/** The fl group eleven with suppl view. */
	private boolean flGroupElevenWithSupplView = false;

	/** The fl group twelve view. */
	private boolean flGroupTwelveView = false;

	/** The fl group twelve without suppl view. */
	private boolean flGroupTwelveWithoutSupplView = false;

	/** The fl group twelve with suppl view. */
	private boolean flGroupTwelveWithSupplView = false;

	/** The fl group thirteen view. */
	private boolean flGroupThirteenView = false;

	/** The fl group thirteen without suppl view. */
	private boolean flGroupThirteenWithoutSupplView = false;

	/** The fl group thirteen with suppl view. */
	private boolean flGroupThirteenWithSupplView = false;

	/** The fl group fourteen view. */
	private boolean flGroupFourteenView = false;

	/** The fl group fourteen without suppl view. */
	private boolean flGroupFourteenWithoutSupplView = false;

	/** The fl group fourteen with suppl view. */
	private boolean flGroupFourteenWithSupplView = false;

	/** The fl group fourteen all rolecodes without suppl view. */
	private boolean flGroupFourteenAllRolecodesWithoutSupplView = false;

	/** The fl group fifteen view. */
	private boolean flGroupFifteenView = false;

	/** The fl group fifteen without suppl view. */
	private boolean flGroupFifteenWithoutSupplView = false;

	/** The fl group fifteen with suppl view. */
	private boolean flGroupFifteenWithSupplView = false;

	/** The fl group sixteen view. */
	private boolean flGroupSixteenView = false;

	/** The fl group sixteen without suppl view. */
	private boolean flGroupSixteenWithoutSupplView = false;

	/** The fl group sixteen with suppl view. */
	private boolean flGroupSixteenWithSupplView = false;

	/** The fl supplier text view. */
	private boolean flSupplierTextView = false;

	/** The fl supplier dropdown view. */
	private boolean flSupplierDropdownView = false;

	/** The fl group seventeen view. */
	private boolean flGroupSeventeenView = false;

	/** The fl group seventeen without suppl view. */
	private boolean flGroupSeventeenWithoutSupplView = false;

	/** The fl group seventeen with suppl view. */
	private boolean flGroupSeventeenWithSupplView = false;

	/** The fl mandatory ministry view. */
	private boolean flMandatoryMinistryView = false;

	/** The fl mandatory ptj view. */
	private boolean flMandatoryPtjView = false;

	/** The fl stop instruction no view. */
	private boolean flStopInstructionNoView = false;

	/** The fl group eighteen view. */
	private boolean flGroupEighteenView = false;

	/** The fl group eighteen without suppl view. */
	private boolean flGroupEighteenWithoutSupplView = false;

	/** The fl group eighteen with suppl view. */
	private boolean flGroupEighteenWithSupplView = false;

	/** The fl group nineteen view. */
	private boolean flGroupNineteenView = false;

	/** The fl group nineteen without suppl view. */
	private boolean flGroupNineteenWithoutSupplView = false;

	/** The fl group nineteen with suppl view. */
	private boolean flGroupNineteenWithSupplView = false;

	/** The fl group twenty view. */
	private boolean flGroupTwentyView = false;

	/** The fl group twenty without suppl view. */
	private boolean flGroupTwentyWithoutSupplView = false;

	/** The fl group twenty with suppl view. */
	private boolean flGroupTwentyWithSupplView = false;

	/** The fl group twenty one view. */
	private boolean flGroupTwentyOneView = false;

	/** The fl group twenty one without suppl view. */
	private boolean flGroupTwentyOneWithoutSupplView = false;

	/** The fl group twenty one with suppl view. */
	private boolean flGroupTwentyOneWithSupplView = false;

	/** The fl group twenty two view. */
	private boolean flGroupTwentyTwoView = false;

	/** The fl group twenty two without suppl view. */
	private boolean flGroupTwentyTwoWithoutSupplView = false;

	/** The fl group twenty two with suppl view. */
	private boolean flGroupTwentyTwoWithSupplView = false;

	/** The fl supplier invoice no view. */
	private boolean flSupplierInvoiceNoView = false;

	/** The fl supplier delivery order no view. */
	private boolean flSupplierDeliveryOrderNoView = false;

	/** The fl payment advice no view. */
	private boolean flPaymentAdviceNoView = false;

	/** The fl group twenty three view. */
	private boolean flGroupTwentyThreeView = false;

	/** The fl group twenty three without suppl view. */
	private boolean flGroupTwentyThreeWithoutSupplView = false;

	/** The fl group twenty three with suppl view. */
	private boolean flGroupTwentyThreeWithSupplView = false;

	/** The fl group twenty four view. */
	private boolean flGroupTwentyFourView = false;

	/** The fl group twenty four without suppl view. */
	private boolean flGroupTwentyFourWithoutSupplView = false;

	/** The fl group twenty four with suppl view. */
	private boolean flGroupTwentyFourWithSupplView = false;

	/** The fl from date view. */
	private boolean flFromDateView = false;

	/** The fl contract request created date view. */
	private boolean flContractRequestCreatedDateView = false;

	/** The fl contract order date view. */
	private boolean flContractOrderDateView = false;

	/** The fl delivery order created date view. */
	private boolean flDeliveryOrderCreatedDateView = false;

	/** The fl frn created date view. */
	private boolean flFRNCreatedDateView = false;

	/** The fl stop instruction created date view. */
	private boolean flStopInstructionCreatedDateView = false;

	/** The fl invoice created date view. */
	private boolean flInvoiceCreatedDateView = false;

	/** The fl contract order created date view. */
	private boolean flContractOrderCreatedDateView = false;

	/** The fl contract request rejection date view. */
	private boolean flContractRequestRejectionDateView = false;

	/** The fl group twenty five view. */
	private boolean flGroupTwentyFiveView = false;

	/** The fl group twenty five without suppl view. */
	private boolean flGroupTwentyFiveWithoutSupplView = false;

	/** The fl group twenty five with suppl view. */
	private boolean flGroupTwentyFiveWithSupplView = false;

	/** The fl contract order cancellation date view. */
	private boolean flContractOrderCancellationDateView = false;

	/** The fl delivery order cancellation date view. */
	private boolean flDeliveryOrderCancellationDateView = false;

	/** The fl group twenty six view. */
	private boolean flGroupTwentySixView = false;

	/** The fl group twenty six without suppl view. */
	private boolean flGroupTwentySixWithoutSupplView = false;

	/** The fl group twenty six with suppl view. */
	private boolean flGroupTwentySixWithSupplView = false;

	/** The fl frn cancellation date view. */
	private boolean flFRNCancellationDateView = false;

	/** The fl invoice cancellation date view. */
	private boolean flInvoiceCancellationDateView = false;

	/** The fl group twenty seven view. */
	private boolean flGroupTwentySevenView = false;

	/** The fl group twenty seven without suppl view. */
	private boolean flGroupTwentySevenWithoutSupplView = false;

	/** The fl group twenty seven with suppl view. */
	private boolean flGroupTwentySevenWithSupplView = false;

	/** The ct contract from date view. */
	private boolean ctContractFromDateView = false;

	/** The ct termination type view. */
	private boolean ctTerminationTypeView = false;

	/** The ct circular no view. */
	private boolean ctCircularNoView = false;

	/** The ct circular name view. */
	private boolean ctCircularNameView = false;

	/** The ct circular type view. */
	private boolean ctCircularTypeView = false;

	/** The ct contract effective from date label view. */
	private boolean ctContractEffectiveFromDateLabelView = false;

	/** The ct contract order from date label view. */
	private boolean ctContractOrderFromDateLabelView = false;

	/** The ct supplier name view. */
	private boolean ctSupplierNameView = false;

	/** The ct supplier default view. */
	private boolean ctSupplierDefaultView = false;

	/** The ct supplier text view. */
	private boolean ctSupplierTextView = false;

	/** The fl purchase order created date view. */
	private boolean flPurchaseOrderCreatedDateView = false;

	/** The fl purchase order rejected date view. */
	private boolean flPurchaseOrderRejectedDateView = false;

	/** The fl delivery order date view. */
	private boolean flDeliveryOrderDateView = false;

	/** The fl purchase request rejection date view. */
	private boolean flPurchaseRequestRejectionDateView = false;

	/** The fl purchase order cancellation date view. */
	private boolean flPurchaseOrderCancellationDateView = false;

	/**
	 * Checks if is fl purchase order cancellation date view.
	 * 
	 * @return true, if is fl purchase order cancellation date view
	 */
	public boolean isFlPurchaseOrderCancellationDateView() {
		return flPurchaseOrderCancellationDateView;
	}

	/**
	 * Sets the fl purchase order cancellation date view.
	 * 
	 * @param flPurchaseOrderCancellationDateView
	 *            the new fl purchase order cancellation date view
	 */
	public void setFlPurchaseOrderCancellationDateView(
			boolean flPurchaseOrderCancellationDateView) {
		this.flPurchaseOrderCancellationDateView = flPurchaseOrderCancellationDateView;
	}

	/**
	 * Checks if is fl purchase request rejection date view.
	 * 
	 * @return true, if is fl purchase request rejection date view
	 */
	public boolean isFlPurchaseRequestRejectionDateView() {
		return flPurchaseRequestRejectionDateView;
	}

	/**
	 * Sets the fl purchase request rejection date view.
	 * 
	 * @param flPurchaseRequestRejectionDateView
	 *            the new fl purchase request rejection date view
	 */
	public void setFlPurchaseRequestRejectionDateView(
			boolean flPurchaseRequestRejectionDateView) {
		this.flPurchaseRequestRejectionDateView = flPurchaseRequestRejectionDateView;
	}

	/**
	 * Checks if is fl delivery order date view.
	 * 
	 * @return true, if is fl delivery order date view
	 */
	public boolean isFlDeliveryOrderDateView() {
		return flDeliveryOrderDateView;
	}

	/**
	 * Sets the fl delivery order date view.
	 * 
	 * @param flDeliveryOrderDateView
	 *            the new fl delivery order date view
	 */
	public void setFlDeliveryOrderDateView(boolean flDeliveryOrderDateView) {
		this.flDeliveryOrderDateView = flDeliveryOrderDateView;
	}

	/**
	 * Checks if is fl purchase order rejected date view.
	 * 
	 * @return true, if is fl purchase order rejected date view
	 */
	public boolean isFlPurchaseOrderRejectedDateView() {
		return flPurchaseOrderRejectedDateView;
	}

	/**
	 * Sets the fl purchase order rejected date view.
	 * 
	 * @param flPurchaseOrderRejectedDateView
	 *            the new fl purchase order rejected date view
	 */
	public void setFlPurchaseOrderRejectedDateView(
			boolean flPurchaseOrderRejectedDateView) {
		this.flPurchaseOrderRejectedDateView = flPurchaseOrderRejectedDateView;
	}

	/**
	 * Checks if is fl purchase order created date view.
	 * 
	 * @return true, if is fl purchase order created date view
	 */
	public boolean isFlPurchaseOrderCreatedDateView() {
		return flPurchaseOrderCreatedDateView;
	}

	/**
	 * Sets the fl purchase order created date view.
	 * 
	 * @param flPurchaseOrderCreatedDateView
	 *            the new fl purchase order created date view
	 */
	public void setFlPurchaseOrderCreatedDateView(
			boolean flPurchaseOrderCreatedDateView) {
		this.flPurchaseOrderCreatedDateView = flPurchaseOrderCreatedDateView;
	}

	/**
	 * Checks if is fl group fourteen all rolecodes without suppl view.
	 * 
	 * @return true, if is fl group fourteen all rolecodes without suppl view
	 */
	public boolean isFlGroupFourteenAllRolecodesWithoutSupplView() {
		return flGroupFourteenAllRolecodesWithoutSupplView;
	}

	/**
	 * Sets the fl group fourteen all rolecodes without suppl view.
	 * 
	 * @param flGroupFourteenAllRolecodesWithoutSupplView
	 *            the new fl group fourteen all rolecodes without suppl view
	 */
	public void setFlGroupFourteenAllRolecodesWithoutSupplView(
			boolean flGroupFourteenAllRolecodesWithoutSupplView) {
		this.flGroupFourteenAllRolecodesWithoutSupplView = flGroupFourteenAllRolecodesWithoutSupplView;
	}

	/**
	 * Checks if is fl group three all rolecodes without suppl view.
	 * 
	 * @return true, if is fl group three all rolecodes without suppl view
	 */
	public boolean isFlGroupThreeAllRolecodesWithoutSupplView() {
		return flGroupThreeAllRolecodesWithoutSupplView;
	}

	/**
	 * Sets the fl group three all rolecodes without suppl view.
	 * 
	 * @param flGroupThreeAllRolecodesWithoutSupplView
	 *            the new fl group three all rolecodes without suppl view
	 */
	public void setFlGroupThreeAllRolecodesWithoutSupplView(
			boolean flGroupThreeAllRolecodesWithoutSupplView) {
		this.flGroupThreeAllRolecodesWithoutSupplView = flGroupThreeAllRolecodesWithoutSupplView;
	}

	/**
	 * Checks if is cm item validity date view.
	 * 
	 * @return true, if is cm item validity date view
	 */
	public boolean isCmItemValidityDateView() {
		return cmItemValidityDateView;
	}

	/**
	 * Sets the cm item validity date view.
	 * 
	 * @param cmItemValidityDateView
	 *            the new cm item validity date view
	 */
	public void setCmItemValidityDateView(boolean cmItemValidityDateView) {
		this.cmItemValidityDateView = cmItemValidityDateView;
	}

	/**
	 * Checks if is cm expired date view.
	 * 
	 * @return true, if is cm expired date view
	 */
	public boolean isCmExpiredDateView() {
		return cmExpiredDateView;
	}

	/**
	 * Sets the cm expired date view.
	 * 
	 * @param cmExpiredDateView
	 *            the new cm expired date view
	 */
	public void setCmExpiredDateView(boolean cmExpiredDateView) {
		this.cmExpiredDateView = cmExpiredDateView;
	}

	/**
	 * Checks if is cm submitted date view.
	 * 
	 * @return true, if is cm submitted date view
	 */
	public boolean isCmSubmittedDateView() {
		return cmSubmittedDateView;
	}

	/**
	 * Sets the cm submitted date view.
	 * 
	 * @param cmSubmittedDateView
	 *            the new cm submitted date view
	 */
	public void setCmSubmittedDateView(boolean cmSubmittedDateView) {
		this.cmSubmittedDateView = cmSubmittedDateView;
	}

	/**
	 * Checks if is cm catelog date view.
	 * 
	 * @return true, if is cm catelog date view
	 */
	public boolean isCmCatelogDateView() {
		return cmCatelogDateView;
	}

	/**
	 * Sets the cm catelog date view.
	 * 
	 * @param cmCatelogDateView
	 *            the new cm catelog date view
	 */
	public void setCmCatelogDateView(boolean cmCatelogDateView) {
		this.cmCatelogDateView = cmCatelogDateView;
	}

	/**
	 * Checks if is cm approved date view.
	 * 
	 * @return true, if is cm approved date view
	 */
	public boolean isCmApprovedDateView() {
		return cmApprovedDateView;
	}

	/**
	 * Sets the cm approved date view.
	 * 
	 * @param cmApprovedDateView
	 *            the new cm approved date view
	 */
	public void setCmApprovedDateView(boolean cmApprovedDateView) {
		this.cmApprovedDateView = cmApprovedDateView;
	}

	/**
	 * Checks if is cm published date view.
	 * 
	 * @return true, if is cm published date view
	 */
	public boolean isCmPublishedDateView() {
		return cmPublishedDateView;
	}

	/**
	 * Sets the cm published date view.
	 * 
	 * @param cmPublishedDateView
	 *            the new cm published date view
	 */
	public void setCmPublishedDateView(boolean cmPublishedDateView) {
		this.cmPublishedDateView = cmPublishedDateView;
	}

	/**
	 * Checks if is ct supplier default view.
	 * 
	 * @return true, if is ct supplier default view
	 */
	public boolean isCtSupplierDefaultView() {
		return ctSupplierDefaultView;
	}

	/**
	 * Sets the ct supplier default view.
	 * 
	 * @param ctSupplierDefaultView
	 *            the new ct supplier default view
	 */
	public void setCtSupplierDefaultView(boolean ctSupplierDefaultView) {
		this.ctSupplierDefaultView = ctSupplierDefaultView;
	}

	/**
	 * Checks if is ct supplier text view.
	 * 
	 * @return true, if is ct supplier text view
	 */
	public boolean isCtSupplierTextView() {
		return ctSupplierTextView;
	}

	/**
	 * Sets the ct supplier text view.
	 * 
	 * @param ctSupplierTextView
	 *            the new ct supplier text view
	 */
	public void setCtSupplierTextView(boolean ctSupplierTextView) {
		this.ctSupplierTextView = ctSupplierTextView;
	}

	/**
	 * Checks if is ct contract from date view.
	 * 
	 * @return true, if is ct contract from date view
	 */
	public boolean isCtContractFromDateView() {
		return ctContractFromDateView;
	}

	/**
	 * Sets the ct contract from date view.
	 * 
	 * @param ctContractFromDateView
	 *            the new ct contract from date view
	 */
	public void setCtContractFromDateView(boolean ctContractFromDateView) {
		this.ctContractFromDateView = ctContractFromDateView;
	}

	/**
	 * Checks if is ct contract effective from date label view.
	 * 
	 * @return true, if is ct contract effective from date label view
	 */
	public boolean isCtContractEffectiveFromDateLabelView() {
		return ctContractEffectiveFromDateLabelView;
	}

	/**
	 * Sets the ct contract effective from date label view.
	 * 
	 * @param ctContractEffectiveFromDateLabelView
	 *            the new ct contract effective from date label view
	 */
	public void setCtContractEffectiveFromDateLabelView(
			boolean ctContractEffectiveFromDateLabelView) {
		this.ctContractEffectiveFromDateLabelView = ctContractEffectiveFromDateLabelView;
	}

	/**
	 * Checks if is ct contract order from date label view.
	 * 
	 * @return true, if is ct contract order from date label view
	 */
	public boolean isCtContractOrderFromDateLabelView() {
		return ctContractOrderFromDateLabelView;
	}

	/**
	 * Sets the ct contract order from date label view.
	 * 
	 * @param ctContractOrderFromDateLabelView
	 *            the new ct contract order from date label view
	 */
	public void setCtContractOrderFromDateLabelView(
			boolean ctContractOrderFromDateLabelView) {
		this.ctContractOrderFromDateLabelView = ctContractOrderFromDateLabelView;
	}

	/**
	 * Checks if is sm payment to view.
	 * 
	 * @return true, if is sm payment to view
	 */
	public boolean isSmPaymentToView() {
		return smPaymentToView;
	}

	/**
	 * Sets the sm payment to view.
	 * 
	 * @param smPaymentToView
	 *            the new sm payment to view
	 */
	public void setSmPaymentToView(boolean smPaymentToView) {
		this.smPaymentToView = smPaymentToView;
	}

	/**
	 * Checks if is sm payment type three view.
	 * 
	 * @return true, if is sm payment type three view
	 */
	public boolean isSmPaymentTypeThreeView() {
		return smPaymentTypeThreeView;
	}

	/**
	 * Sets the sm payment type three view.
	 * 
	 * @param smPaymentTypeThreeView
	 *            the new sm payment type three view
	 */
	public void setSmPaymentTypeThreeView(boolean smPaymentTypeThreeView) {
		this.smPaymentTypeThreeView = smPaymentTypeThreeView;
	}

	/**
	 * Checks if is sm paid up cptl date view.
	 * 
	 * @return true, if is sm paid up cptl date view
	 */
	public boolean isSmPaidUpCptlDateView() {
		return smPaidUpCptlDateView;
	}

	/**
	 * Sets the sm paid up cptl date view.
	 * 
	 * @param smPaidUpCptlDateView
	 *            the new sm paid up cptl date view
	 */
	public void setSmPaidUpCptlDateView(boolean smPaidUpCptlDateView) {
		this.smPaidUpCptlDateView = smPaidUpCptlDateView;
	}

	/**
	 * Checks if is sm accmltd cptl date view.
	 * 
	 * @return true, if is sm accmltd cptl date view
	 */
	public boolean isSmAccmltdCptlDateView() {
		return smAccmltdCptlDateView;
	}

	/**
	 * Sets the sm accmltd cptl date view.
	 * 
	 * @param smAccmltdCptlDateView
	 *            the new sm accmltd cptl date view
	 */
	public void setSmAccmltdCptlDateView(boolean smAccmltdCptlDateView) {
		this.smAccmltdCptlDateView = smAccmltdCptlDateView;
	}

	/**
	 * Checks if is fl group fifteen with suppl view.
	 * 
	 * @return true, if is fl group fifteen with suppl view
	 */
	public boolean isFlGroupFifteenWithSupplView() {
		return flGroupFifteenWithSupplView;
	}

	/**
	 * Sets the fl group fifteen with suppl view.
	 * 
	 * @param flGroupFifteenWithSupplView
	 *            the new fl group fifteen with suppl view
	 */
	public void setFlGroupFifteenWithSupplView(
			boolean flGroupFifteenWithSupplView) {
		this.flGroupFifteenWithSupplView = flGroupFifteenWithSupplView;
	}

	/**
	 * Checks if is fl group two without suppl view.
	 * 
	 * @return true, if is fl group two without suppl view
	 */
	public boolean isFlGroupTwoWithoutSupplView() {
		return flGroupTwoWithoutSupplView;
	}

	/**
	 * Sets the fl group two without suppl view.
	 * 
	 * @param flGroupTwoWithoutSupplView
	 *            the new fl group two without suppl view
	 */
	public void setFlGroupTwoWithoutSupplView(boolean flGroupTwoWithoutSupplView) {
		this.flGroupTwoWithoutSupplView = flGroupTwoWithoutSupplView;
	}

	/**
	 * Checks if is fl group two with suppl view.
	 * 
	 * @return true, if is fl group two with suppl view
	 */
	public boolean isFlGroupTwoWithSupplView() {
		return flGroupTwoWithSupplView;
	}

	/**
	 * Sets the fl group two with suppl view.
	 * 
	 * @param flGroupTwoWithSupplView
	 *            the new fl group two with suppl view
	 */
	public void setFlGroupTwoWithSupplView(boolean flGroupTwoWithSupplView) {
		this.flGroupTwoWithSupplView = flGroupTwoWithSupplView;
	}

	/**
	 * Checks if is fl group three without suppl view.
	 * 
	 * @return true, if is fl group three without suppl view
	 */
	public boolean isFlGroupThreeWithoutSupplView() {
		return flGroupThreeWithoutSupplView;
	}

	/**
	 * Sets the fl group three without suppl view.
	 * 
	 * @param flGroupThreeWithoutSupplView
	 *            the new fl group three without suppl view
	 */
	public void setFlGroupThreeWithoutSupplView(
			boolean flGroupThreeWithoutSupplView) {
		this.flGroupThreeWithoutSupplView = flGroupThreeWithoutSupplView;
	}

	/**
	 * Checks if is fl group three with suppl view.
	 * 
	 * @return true, if is fl group three with suppl view
	 */
	public boolean isFlGroupThreeWithSupplView() {
		return flGroupThreeWithSupplView;
	}

	/**
	 * Sets the fl group three with suppl view.
	 * 
	 * @param flGroupThreeWithSupplView
	 *            the new fl group three with suppl view
	 */
	public void setFlGroupThreeWithSupplView(boolean flGroupThreeWithSupplView) {
		this.flGroupThreeWithSupplView = flGroupThreeWithSupplView;
	}

	/**
	 * Checks if is fl group four without suppl view.
	 * 
	 * @return true, if is fl group four without suppl view
	 */
	public boolean isFlGroupFourWithoutSupplView() {
		return flGroupFourWithoutSupplView;
	}

	/**
	 * Sets the fl group four without suppl view.
	 * 
	 * @param flGroupFourWithoutSupplView
	 *            the new fl group four without suppl view
	 */
	public void setFlGroupFourWithoutSupplView(
			boolean flGroupFourWithoutSupplView) {
		this.flGroupFourWithoutSupplView = flGroupFourWithoutSupplView;
	}

	/**
	 * Checks if is fl group four with suppl view.
	 * 
	 * @return true, if is fl group four with suppl view
	 */
	public boolean isFlGroupFourWithSupplView() {
		return flGroupFourWithSupplView;
	}

	/**
	 * Sets the fl group four with suppl view.
	 * 
	 * @param flGroupFourWithSupplView
	 *            the new fl group four with suppl view
	 */
	public void setFlGroupFourWithSupplView(boolean flGroupFourWithSupplView) {
		this.flGroupFourWithSupplView = flGroupFourWithSupplView;
	}

	/**
	 * Checks if is fl group five without suppl view.
	 * 
	 * @return true, if is fl group five without suppl view
	 */
	public boolean isFlGroupFiveWithoutSupplView() {
		return flGroupFiveWithoutSupplView;
	}

	/**
	 * Sets the fl group five without suppl view.
	 * 
	 * @param flGroupFiveWithoutSupplView
	 *            the new fl group five without suppl view
	 */
	public void setFlGroupFiveWithoutSupplView(
			boolean flGroupFiveWithoutSupplView) {
		this.flGroupFiveWithoutSupplView = flGroupFiveWithoutSupplView;
	}

	/**
	 * Checks if is fl group five with suppl view.
	 * 
	 * @return true, if is fl group five with suppl view
	 */
	public boolean isFlGroupFiveWithSupplView() {
		return flGroupFiveWithSupplView;
	}

	/**
	 * Sets the fl group five with suppl view.
	 * 
	 * @param flGroupFiveWithSupplView
	 *            the new fl group five with suppl view
	 */
	public void setFlGroupFiveWithSupplView(boolean flGroupFiveWithSupplView) {
		this.flGroupFiveWithSupplView = flGroupFiveWithSupplView;
	}

	/**
	 * Checks if is fl group six without suppl view.
	 * 
	 * @return true, if is fl group six without suppl view
	 */
	public boolean isFlGroupSixWithoutSupplView() {
		return flGroupSixWithoutSupplView;
	}

	/**
	 * Sets the fl group six without suppl view.
	 * 
	 * @param flGroupSixWithoutSupplView
	 *            the new fl group six without suppl view
	 */
	public void setFlGroupSixWithoutSupplView(boolean flGroupSixWithoutSupplView) {
		this.flGroupSixWithoutSupplView = flGroupSixWithoutSupplView;
	}

	/**
	 * Checks if is fl group six with suppl view.
	 * 
	 * @return true, if is fl group six with suppl view
	 */
	public boolean isFlGroupSixWithSupplView() {
		return flGroupSixWithSupplView;
	}

	/**
	 * Sets the fl group six with suppl view.
	 * 
	 * @param flGroupSixWithSupplView
	 *            the new fl group six with suppl view
	 */
	public void setFlGroupSixWithSupplView(boolean flGroupSixWithSupplView) {
		this.flGroupSixWithSupplView = flGroupSixWithSupplView;
	}

	/**
	 * Checks if is fl group seven without suppl view.
	 * 
	 * @return true, if is fl group seven without suppl view
	 */
	public boolean isFlGroupSevenWithoutSupplView() {
		return flGroupSevenWithoutSupplView;
	}

	/**
	 * Sets the fl group seven without suppl view.
	 * 
	 * @param flGroupSevenWithoutSupplView
	 *            the new fl group seven without suppl view
	 */
	public void setFlGroupSevenWithoutSupplView(
			boolean flGroupSevenWithoutSupplView) {
		this.flGroupSevenWithoutSupplView = flGroupSevenWithoutSupplView;
	}

	/**
	 * Checks if is fl group seven with suppl view.
	 * 
	 * @return true, if is fl group seven with suppl view
	 */
	public boolean isFlGroupSevenWithSupplView() {
		return flGroupSevenWithSupplView;
	}

	/**
	 * Sets the fl group seven with suppl view.
	 * 
	 * @param flGroupSevenWithSupplView
	 *            the new fl group seven with suppl view
	 */
	public void setFlGroupSevenWithSupplView(boolean flGroupSevenWithSupplView) {
		this.flGroupSevenWithSupplView = flGroupSevenWithSupplView;
	}

	/**
	 * Checks if is fl group eight without suppl view.
	 * 
	 * @return true, if is fl group eight without suppl view
	 */
	public boolean isFlGroupEightWithoutSupplView() {
		return flGroupEightWithoutSupplView;
	}

	/**
	 * Sets the fl group eight without suppl view.
	 * 
	 * @param flGroupEightWithoutSupplView
	 *            the new fl group eight without suppl view
	 */
	public void setFlGroupEightWithoutSupplView(
			boolean flGroupEightWithoutSupplView) {
		this.flGroupEightWithoutSupplView = flGroupEightWithoutSupplView;
	}

	/**
	 * Checks if is fl group eight with suppl view.
	 * 
	 * @return true, if is fl group eight with suppl view
	 */
	public boolean isFlGroupEightWithSupplView() {
		return flGroupEightWithSupplView;
	}

	/**
	 * Sets the fl group eight with suppl view.
	 * 
	 * @param flGroupEightWithSupplView
	 *            the new fl group eight with suppl view
	 */
	public void setFlGroupEightWithSupplView(boolean flGroupEightWithSupplView) {
		this.flGroupEightWithSupplView = flGroupEightWithSupplView;
	}

	/**
	 * Checks if is fl group nine without suppl view.
	 * 
	 * @return true, if is fl group nine without suppl view
	 */
	public boolean isFlGroupNineWithoutSupplView() {
		return flGroupNineWithoutSupplView;
	}

	/**
	 * Sets the fl group nine without suppl view.
	 * 
	 * @param flGroupNineWithoutSupplView
	 *            the new fl group nine without suppl view
	 */
	public void setFlGroupNineWithoutSupplView(
			boolean flGroupNineWithoutSupplView) {
		this.flGroupNineWithoutSupplView = flGroupNineWithoutSupplView;
	}

	/**
	 * Checks if is fl group nine with suppl view.
	 * 
	 * @return true, if is fl group nine with suppl view
	 */
	public boolean isFlGroupNineWithSupplView() {
		return flGroupNineWithSupplView;
	}

	/**
	 * Sets the fl group nine with suppl view.
	 * 
	 * @param flGroupNineWithSupplView
	 *            the new fl group nine with suppl view
	 */
	public void setFlGroupNineWithSupplView(boolean flGroupNineWithSupplView) {
		this.flGroupNineWithSupplView = flGroupNineWithSupplView;
	}

	/**
	 * Checks if is fl group ten without suppl view.
	 * 
	 * @return true, if is fl group ten without suppl view
	 */
	public boolean isFlGroupTenWithoutSupplView() {
		return flGroupTenWithoutSupplView;
	}

	/**
	 * Sets the fl group ten without suppl view.
	 * 
	 * @param flGroupTenWithoutSupplView
	 *            the new fl group ten without suppl view
	 */
	public void setFlGroupTenWithoutSupplView(boolean flGroupTenWithoutSupplView) {
		this.flGroupTenWithoutSupplView = flGroupTenWithoutSupplView;
	}

	/**
	 * Checks if is fl group ten with suppl view.
	 * 
	 * @return true, if is fl group ten with suppl view
	 */
	public boolean isFlGroupTenWithSupplView() {
		return flGroupTenWithSupplView;
	}

	/**
	 * Sets the fl group ten with suppl view.
	 * 
	 * @param flGroupTenWithSupplView
	 *            the new fl group ten with suppl view
	 */
	public void setFlGroupTenWithSupplView(boolean flGroupTenWithSupplView) {
		this.flGroupTenWithSupplView = flGroupTenWithSupplView;
	}

	/**
	 * Checks if is fl group eleven without suppl view.
	 * 
	 * @return true, if is fl group eleven without suppl view
	 */
	public boolean isFlGroupElevenWithoutSupplView() {
		return flGroupElevenWithoutSupplView;
	}

	/**
	 * Sets the fl group eleven without suppl view.
	 * 
	 * @param flGroupElevenWithoutSupplView
	 *            the new fl group eleven without suppl view
	 */
	public void setFlGroupElevenWithoutSupplView(
			boolean flGroupElevenWithoutSupplView) {
		this.flGroupElevenWithoutSupplView = flGroupElevenWithoutSupplView;
	}

	/**
	 * Checks if is fl group eleven with suppl view.
	 * 
	 * @return true, if is fl group eleven with suppl view
	 */
	public boolean isFlGroupElevenWithSupplView() {
		return flGroupElevenWithSupplView;
	}

	/**
	 * Sets the fl group eleven with suppl view.
	 * 
	 * @param flGroupElevenWithSupplView
	 *            the new fl group eleven with suppl view
	 */
	public void setFlGroupElevenWithSupplView(boolean flGroupElevenWithSupplView) {
		this.flGroupElevenWithSupplView = flGroupElevenWithSupplView;
	}

	/**
	 * Checks if is fl group twelve without suppl view.
	 * 
	 * @return true, if is fl group twelve without suppl view
	 */
	public boolean isFlGroupTwelveWithoutSupplView() {
		return flGroupTwelveWithoutSupplView;
	}

	/**
	 * Sets the fl group twelve without suppl view.
	 * 
	 * @param flGroupTwelveWithoutSupplView
	 *            the new fl group twelve without suppl view
	 */
	public void setFlGroupTwelveWithoutSupplView(
			boolean flGroupTwelveWithoutSupplView) {
		this.flGroupTwelveWithoutSupplView = flGroupTwelveWithoutSupplView;
	}

	/**
	 * Checks if is fl group twelve with suppl view.
	 * 
	 * @return true, if is fl group twelve with suppl view
	 */
	public boolean isFlGroupTwelveWithSupplView() {
		return flGroupTwelveWithSupplView;
	}

	/**
	 * Sets the fl group twelve with suppl view.
	 * 
	 * @param flGroupTwelveWithSupplView
	 *            the new fl group twelve with suppl view
	 */
	public void setFlGroupTwelveWithSupplView(boolean flGroupTwelveWithSupplView) {
		this.flGroupTwelveWithSupplView = flGroupTwelveWithSupplView;
	}

	/**
	 * Checks if is fl group thirteen without suppl view.
	 * 
	 * @return true, if is fl group thirteen without suppl view
	 */
	public boolean isFlGroupThirteenWithoutSupplView() {
		return flGroupThirteenWithoutSupplView;
	}

	/**
	 * Sets the fl group thirteen without suppl view.
	 * 
	 * @param flGroupThirteenWithoutSupplView
	 *            the new fl group thirteen without suppl view
	 */
	public void setFlGroupThirteenWithoutSupplView(
			boolean flGroupThirteenWithoutSupplView) {
		this.flGroupThirteenWithoutSupplView = flGroupThirteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group thirteen with suppl view.
	 * 
	 * @return true, if is fl group thirteen with suppl view
	 */
	public boolean isFlGroupThirteenWithSupplView() {
		return flGroupThirteenWithSupplView;
	}

	/**
	 * Sets the fl group thirteen with suppl view.
	 * 
	 * @param flGroupThirteenWithSupplView
	 *            the new fl group thirteen with suppl view
	 */
	public void setFlGroupThirteenWithSupplView(
			boolean flGroupThirteenWithSupplView) {
		this.flGroupThirteenWithSupplView = flGroupThirteenWithSupplView;
	}

	/**
	 * Checks if is fl group fourteen without suppl view.
	 * 
	 * @return true, if is fl group fourteen without suppl view
	 */
	public boolean isFlGroupFourteenWithoutSupplView() {
		return flGroupFourteenWithoutSupplView;
	}

	/**
	 * Sets the fl group fourteen without suppl view.
	 * 
	 * @param flGroupFourteenWithoutSupplView
	 *            the new fl group fourteen without suppl view
	 */
	public void setFlGroupFourteenWithoutSupplView(
			boolean flGroupFourteenWithoutSupplView) {
		this.flGroupFourteenWithoutSupplView = flGroupFourteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group fourteen with suppl view.
	 * 
	 * @return true, if is fl group fourteen with suppl view
	 */
	public boolean isFlGroupFourteenWithSupplView() {
		return flGroupFourteenWithSupplView;
	}

	/**
	 * Sets the fl group fourteen with suppl view.
	 * 
	 * @param flGroupFourteenWithSupplView
	 *            the new fl group fourteen with suppl view
	 */
	public void setFlGroupFourteenWithSupplView(
			boolean flGroupFourteenWithSupplView) {
		this.flGroupFourteenWithSupplView = flGroupFourteenWithSupplView;
	}

	/**
	 * Checks if is fl group fifteen without suppl view.
	 * 
	 * @return true, if is fl group fifteen without suppl view
	 */
	public boolean isFlGroupFifteenWithoutSupplView() {
		return flGroupFifteenWithoutSupplView;
	}

	/**
	 * Sets the fl group fifteen without suppl view.
	 * 
	 * @param flGroupFifteenWithoutSupplView
	 *            the new fl group fifteen without suppl view
	 */
	public void setFlGroupFifteenWithoutSupplView(
			boolean flGroupFifteenWithoutSupplView) {
		this.flGroupFifteenWithoutSupplView = flGroupFifteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group sixteen without suppl view.
	 * 
	 * @return true, if is fl group sixteen without suppl view
	 */
	public boolean isFlGroupSixteenWithoutSupplView() {
		return flGroupSixteenWithoutSupplView;
	}

	/**
	 * Sets the fl group sixteen without suppl view.
	 * 
	 * @param flGroupSixteenWithoutSupplView
	 *            the new fl group sixteen without suppl view
	 */
	public void setFlGroupSixteenWithoutSupplView(
			boolean flGroupSixteenWithoutSupplView) {
		this.flGroupSixteenWithoutSupplView = flGroupSixteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group sixteen with suppl view.
	 * 
	 * @return true, if is fl group sixteen with suppl view
	 */
	public boolean isFlGroupSixteenWithSupplView() {
		return flGroupSixteenWithSupplView;
	}

	/**
	 * Sets the fl group sixteen with suppl view.
	 * 
	 * @param flGroupSixteenWithSupplView
	 *            the new fl group sixteen with suppl view
	 */
	public void setFlGroupSixteenWithSupplView(
			boolean flGroupSixteenWithSupplView) {
		this.flGroupSixteenWithSupplView = flGroupSixteenWithSupplView;
	}

	/**
	 * Checks if is fl group seventeen without suppl view.
	 * 
	 * @return true, if is fl group seventeen without suppl view
	 */
	public boolean isFlGroupSeventeenWithoutSupplView() {
		return flGroupSeventeenWithoutSupplView;
	}

	/**
	 * Sets the fl group seventeen without suppl view.
	 * 
	 * @param flGroupSeventeenWithoutSupplView
	 *            the new fl group seventeen without suppl view
	 */
	public void setFlGroupSeventeenWithoutSupplView(
			boolean flGroupSeventeenWithoutSupplView) {
		this.flGroupSeventeenWithoutSupplView = flGroupSeventeenWithoutSupplView;
	}

	/**
	 * Checks if is fl group seventeen with suppl view.
	 * 
	 * @return true, if is fl group seventeen with suppl view
	 */
	public boolean isFlGroupSeventeenWithSupplView() {
		return flGroupSeventeenWithSupplView;
	}

	/**
	 * Sets the fl group seventeen with suppl view.
	 * 
	 * @param flGroupSeventeenWithSupplView
	 *            the new fl group seventeen with suppl view
	 */
	public void setFlGroupSeventeenWithSupplView(
			boolean flGroupSeventeenWithSupplView) {
		this.flGroupSeventeenWithSupplView = flGroupSeventeenWithSupplView;
	}

	/**
	 * Checks if is fl group eighteen without suppl view.
	 * 
	 * @return true, if is fl group eighteen without suppl view
	 */
	public boolean isFlGroupEighteenWithoutSupplView() {
		return flGroupEighteenWithoutSupplView;
	}

	/**
	 * Sets the fl group eighteen without suppl view.
	 * 
	 * @param flGroupEighteenWithoutSupplView
	 *            the new fl group eighteen without suppl view
	 */
	public void setFlGroupEighteenWithoutSupplView(
			boolean flGroupEighteenWithoutSupplView) {
		this.flGroupEighteenWithoutSupplView = flGroupEighteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group eighteen with suppl view.
	 * 
	 * @return true, if is fl group eighteen with suppl view
	 */
	public boolean isFlGroupEighteenWithSupplView() {
		return flGroupEighteenWithSupplView;
	}

	/**
	 * Sets the fl group eighteen with suppl view.
	 * 
	 * @param flGroupEighteenWithSupplView
	 *            the new fl group eighteen with suppl view
	 */
	public void setFlGroupEighteenWithSupplView(
			boolean flGroupEighteenWithSupplView) {
		this.flGroupEighteenWithSupplView = flGroupEighteenWithSupplView;
	}

	/**
	 * Checks if is fl group nineteen without suppl view.
	 * 
	 * @return true, if is fl group nineteen without suppl view
	 */
	public boolean isFlGroupNineteenWithoutSupplView() {
		return flGroupNineteenWithoutSupplView;
	}

	/**
	 * Sets the fl group nineteen without suppl view.
	 * 
	 * @param flGroupNineteenWithoutSupplView
	 *            the new fl group nineteen without suppl view
	 */
	public void setFlGroupNineteenWithoutSupplView(
			boolean flGroupNineteenWithoutSupplView) {
		this.flGroupNineteenWithoutSupplView = flGroupNineteenWithoutSupplView;
	}

	/**
	 * Checks if is fl group nineteen with suppl view.
	 * 
	 * @return true, if is fl group nineteen with suppl view
	 */
	public boolean isFlGroupNineteenWithSupplView() {
		return flGroupNineteenWithSupplView;
	}

	/**
	 * Sets the fl group nineteen with suppl view.
	 * 
	 * @param flGroupNineteenWithSupplView
	 *            the new fl group nineteen with suppl view
	 */
	public void setFlGroupNineteenWithSupplView(
			boolean flGroupNineteenWithSupplView) {
		this.flGroupNineteenWithSupplView = flGroupNineteenWithSupplView;
	}

	/**
	 * Checks if is fl group twenty without suppl view.
	 * 
	 * @return true, if is fl group twenty without suppl view
	 */
	public boolean isFlGroupTwentyWithoutSupplView() {
		return flGroupTwentyWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty without suppl view.
	 * 
	 * @param flGroupTwentyWithoutSupplView
	 *            the new fl group twenty without suppl view
	 */
	public void setFlGroupTwentyWithoutSupplView(
			boolean flGroupTwentyWithoutSupplView) {
		this.flGroupTwentyWithoutSupplView = flGroupTwentyWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty with suppl view.
	 * 
	 * @return true, if is fl group twenty with suppl view
	 */
	public boolean isFlGroupTwentyWithSupplView() {
		return flGroupTwentyWithSupplView;
	}

	/**
	 * Sets the fl group twenty with suppl view.
	 * 
	 * @param flGroupTwentyWithSupplView
	 *            the new fl group twenty with suppl view
	 */
	public void setFlGroupTwentyWithSupplView(boolean flGroupTwentyWithSupplView) {
		this.flGroupTwentyWithSupplView = flGroupTwentyWithSupplView;
	}

	/**
	 * Checks if is fl group twenty one without suppl view.
	 * 
	 * @return true, if is fl group twenty one without suppl view
	 */
	public boolean isFlGroupTwentyOneWithoutSupplView() {
		return flGroupTwentyOneWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty one without suppl view.
	 * 
	 * @param flGroupTwentyOneWithoutSupplView
	 *            the new fl group twenty one without suppl view
	 */
	public void setFlGroupTwentyOneWithoutSupplView(
			boolean flGroupTwentyOneWithoutSupplView) {
		this.flGroupTwentyOneWithoutSupplView = flGroupTwentyOneWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty one with suppl view.
	 * 
	 * @return true, if is fl group twenty one with suppl view
	 */
	public boolean isFlGroupTwentyOneWithSupplView() {
		return flGroupTwentyOneWithSupplView;
	}

	/**
	 * Sets the fl group twenty one with suppl view.
	 * 
	 * @param flGroupTwentyOneWithSupplView
	 *            the new fl group twenty one with suppl view
	 */
	public void setFlGroupTwentyOneWithSupplView(
			boolean flGroupTwentyOneWithSupplView) {
		this.flGroupTwentyOneWithSupplView = flGroupTwentyOneWithSupplView;
	}

	/**
	 * Checks if is fl group twenty two without suppl view.
	 * 
	 * @return true, if is fl group twenty two without suppl view
	 */
	public boolean isFlGroupTwentyTwoWithoutSupplView() {
		return flGroupTwentyTwoWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty two without suppl view.
	 * 
	 * @param flGroupTwentyTwoWithoutSupplView
	 *            the new fl group twenty two without suppl view
	 */
	public void setFlGroupTwentyTwoWithoutSupplView(
			boolean flGroupTwentyTwoWithoutSupplView) {
		this.flGroupTwentyTwoWithoutSupplView = flGroupTwentyTwoWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty two with suppl view.
	 * 
	 * @return true, if is fl group twenty two with suppl view
	 */
	public boolean isFlGroupTwentyTwoWithSupplView() {
		return flGroupTwentyTwoWithSupplView;
	}

	/**
	 * Sets the fl group twenty two with suppl view.
	 * 
	 * @param flGroupTwentyTwoWithSupplView
	 *            the new fl group twenty two with suppl view
	 */
	public void setFlGroupTwentyTwoWithSupplView(
			boolean flGroupTwentyTwoWithSupplView) {
		this.flGroupTwentyTwoWithSupplView = flGroupTwentyTwoWithSupplView;
	}

	/**
	 * Checks if is fl group twenty three without suppl view.
	 * 
	 * @return true, if is fl group twenty three without suppl view
	 */
	public boolean isFlGroupTwentyThreeWithoutSupplView() {
		return flGroupTwentyThreeWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty three without suppl view.
	 * 
	 * @param flGroupTwentyThreeWithoutSupplView
	 *            the new fl group twenty three without suppl view
	 */
	public void setFlGroupTwentyThreeWithoutSupplView(
			boolean flGroupTwentyThreeWithoutSupplView) {
		this.flGroupTwentyThreeWithoutSupplView = flGroupTwentyThreeWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty three with suppl view.
	 * 
	 * @return true, if is fl group twenty three with suppl view
	 */
	public boolean isFlGroupTwentyThreeWithSupplView() {
		return flGroupTwentyThreeWithSupplView;
	}

	/**
	 * Sets the fl group twenty three with suppl view.
	 * 
	 * @param flGroupTwentyThreeWithSupplView
	 *            the new fl group twenty three with suppl view
	 */
	public void setFlGroupTwentyThreeWithSupplView(
			boolean flGroupTwentyThreeWithSupplView) {
		this.flGroupTwentyThreeWithSupplView = flGroupTwentyThreeWithSupplView;
	}

	/**
	 * Checks if is sm non mandatory recipt transaction report type view.
	 * 
	 * @return the smNonMandatoryReciptTransactionReportTypeView
	 */
	public boolean isSmNonMandatoryReciptTransactionReportTypeView() {
		return smNonMandatoryReciptTransactionReportTypeView;
	}

	/**
	 * Checks if is sm non mandatory transaction date view.
	 * 
	 * @return the smNonMandatoryTransactionDateView
	 */
	public boolean isSmNonMandatoryTransactionDateView() {
		return smNonMandatoryTransactionDateView;
	}

	/**
	 * Sets the sm non mandatory transaction date view.
	 * 
	 * @param smNonMandatoryTransactionDateView
	 *            the smNonMandatoryTransactionDateView to set
	 */
	public void setSmNonMandatoryTransactionDateView(
			boolean smNonMandatoryTransactionDateView) {
		this.smNonMandatoryTransactionDateView = smNonMandatoryTransactionDateView;
	}

	/**
	 * Checks if is sm non mandatory rt date view.
	 * 
	 * @return the smNonMandatoryRTDateView
	 */
	public boolean isSmNonMandatoryRTDateView() {
		return smNonMandatoryRTDateView;
	}

	/**
	 * Sets the sm non mandatory rt date view.
	 * 
	 * @param smNonMandatoryRTDateView
	 *            the smNonMandatoryRTDateView to set
	 */
	public void setSmNonMandatoryRTDateView(boolean smNonMandatoryRTDateView) {
		this.smNonMandatoryRTDateView = smNonMandatoryRTDateView;
	}

	/**
	 * Checks if is sm mandatory payment method view.
	 * 
	 * @return the smMandatoryPaymentMethodView
	 */
	public boolean isSmMandatoryPaymentMethodView() {
		return smMandatoryPaymentMethodView;
	}

	/**
	 * Sets the sm mandatory payment method view.
	 * 
	 * @param smMandatoryPaymentMethodView
	 *            the smMandatoryPaymentMethodView to set
	 */
	public void setSmMandatoryPaymentMethodView(
			boolean smMandatoryPaymentMethodView) {
		this.smMandatoryPaymentMethodView = smMandatoryPaymentMethodView;
	}

	/**
	 * Sets the sm non mandatory recipt transaction report type view.
	 * 
	 * @param smNonMandatoryReciptTransactionReportTypeView
	 *            the smNonMandatoryReciptTransactionReportTypeView to set
	 */
	public void setSmNonMandatoryReciptTransactionReportTypeView(
			boolean smNonMandatoryReciptTransactionReportTypeView) {
		this.smNonMandatoryReciptTransactionReportTypeView = smNonMandatoryReciptTransactionReportTypeView;
	}

	/**
	 * Checks if is fl group twenty four without suppl view.
	 * 
	 * @return true, if is fl group twenty four without suppl view
	 */
	public boolean isFlGroupTwentyFourWithoutSupplView() {
		return flGroupTwentyFourWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty four without suppl view.
	 * 
	 * @param flGroupTwentyFourWithoutSupplView
	 *            the new fl group twenty four without suppl view
	 */
	public void setFlGroupTwentyFourWithoutSupplView(
			boolean flGroupTwentyFourWithoutSupplView) {
		this.flGroupTwentyFourWithoutSupplView = flGroupTwentyFourWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty four with suppl view.
	 * 
	 * @return true, if is fl group twenty four with suppl view
	 */
	public boolean isFlGroupTwentyFourWithSupplView() {
		return flGroupTwentyFourWithSupplView;
	}

	/**
	 * Sets the fl group twenty four with suppl view.
	 * 
	 * @param flGroupTwentyFourWithSupplView
	 *            the new fl group twenty four with suppl view
	 */
	public void setFlGroupTwentyFourWithSupplView(
			boolean flGroupTwentyFourWithSupplView) {
		this.flGroupTwentyFourWithSupplView = flGroupTwentyFourWithSupplView;
	}

	/**
	 * Checks if is fl group twenty five without suppl view.
	 * 
	 * @return true, if is fl group twenty five without suppl view
	 */
	public boolean isFlGroupTwentyFiveWithoutSupplView() {
		return flGroupTwentyFiveWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty five without suppl view.
	 * 
	 * @param flGroupTwentyFiveWithoutSupplView
	 *            the new fl group twenty five without suppl view
	 */
	public void setFlGroupTwentyFiveWithoutSupplView(
			boolean flGroupTwentyFiveWithoutSupplView) {
		this.flGroupTwentyFiveWithoutSupplView = flGroupTwentyFiveWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty five with suppl view.
	 * 
	 * @return true, if is fl group twenty five with suppl view
	 */
	public boolean isFlGroupTwentyFiveWithSupplView() {
		return flGroupTwentyFiveWithSupplView;
	}

	/**
	 * Sets the fl group twenty five with suppl view.
	 * 
	 * @param flGroupTwentyFiveWithSupplView
	 *            the new fl group twenty five with suppl view
	 */
	public void setFlGroupTwentyFiveWithSupplView(
			boolean flGroupTwentyFiveWithSupplView) {
		this.flGroupTwentyFiveWithSupplView = flGroupTwentyFiveWithSupplView;
	}

	/**
	 * Checks if is fl group twenty six without suppl view.
	 * 
	 * @return true, if is fl group twenty six without suppl view
	 */
	public boolean isFlGroupTwentySixWithoutSupplView() {
		return flGroupTwentySixWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty six without suppl view.
	 * 
	 * @param flGroupTwentySixWithoutSupplView
	 *            the new fl group twenty six without suppl view
	 */
	public void setFlGroupTwentySixWithoutSupplView(
			boolean flGroupTwentySixWithoutSupplView) {
		this.flGroupTwentySixWithoutSupplView = flGroupTwentySixWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty six with suppl view.
	 * 
	 * @return true, if is fl group twenty six with suppl view
	 */
	public boolean isFlGroupTwentySixWithSupplView() {
		return flGroupTwentySixWithSupplView;
	}

	/**
	 * Sets the fl group twenty six with suppl view.
	 * 
	 * @param flGroupTwentySixWithSupplView
	 *            the new fl group twenty six with suppl view
	 */
	public void setFlGroupTwentySixWithSupplView(
			boolean flGroupTwentySixWithSupplView) {
		this.flGroupTwentySixWithSupplView = flGroupTwentySixWithSupplView;
	}

	/**
	 * Checks if is fl group twenty seven without suppl view.
	 * 
	 * @return true, if is fl group twenty seven without suppl view
	 */
	public boolean isFlGroupTwentySevenWithoutSupplView() {
		return flGroupTwentySevenWithoutSupplView;
	}

	/**
	 * Sets the fl group twenty seven without suppl view.
	 * 
	 * @param flGroupTwentySevenWithoutSupplView
	 *            the new fl group twenty seven without suppl view
	 */
	public void setFlGroupTwentySevenWithoutSupplView(
			boolean flGroupTwentySevenWithoutSupplView) {
		this.flGroupTwentySevenWithoutSupplView = flGroupTwentySevenWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty seven with suppl view.
	 * 
	 * @return true, if is fl group twenty seven with suppl view
	 */
	public boolean isFlGroupTwentySevenWithSupplView() {
		return flGroupTwentySevenWithSupplView;
	}

	/**
	 * Sets the fl group twenty seven with suppl view.
	 * 
	 * @param flGroupTwentySevenWithSupplView
	 *            the new fl group twenty seven with suppl view
	 */
	public void setFlGroupTwentySevenWithSupplView(
			boolean flGroupTwentySevenWithSupplView) {
		this.flGroupTwentySevenWithSupplView = flGroupTwentySevenWithSupplView;
	}

	/**
	 * Checks if is fl group one with suppl view.
	 * 
	 * @return true, if is fl group one with suppl view
	 */
	public boolean isFlGroupOneWithSupplView() {
		return flGroupOneWithSupplView;
	}

	/**
	 * Sets the fl group one with suppl view.
	 * 
	 * @param flGroupOneWithSupplView
	 *            the new fl group one with suppl view
	 */
	public void setFlGroupOneWithSupplView(boolean flGroupOneWithSupplView) {
		this.flGroupOneWithSupplView = flGroupOneWithSupplView;
	}

	/**
	 * Checks if is fl group one without suppl view.
	 * 
	 * @return true, if is fl group one without suppl view
	 */
	public boolean isFlGroupOneWithoutSupplView() {
		return flGroupOneWithoutSupplView;
	}

	/**
	 * Sets the fl group one without suppl view.
	 * 
	 * @param flGroupOneWithoutSupplView
	 *            the new fl group one without suppl view
	 */
	public void setFlGroupOneWithoutSupplView(boolean flGroupOneWithoutSupplView) {
		this.flGroupOneWithoutSupplView = flGroupOneWithoutSupplView;
	}

	/**
	 * Checks if is fl group twenty seven view.
	 * 
	 * @return true, if is fl group twenty seven view
	 */
	public boolean isFlGroupTwentySevenView() {
		return flGroupTwentySevenView;
	}

	/**
	 * Sets the fl group twenty seven view.
	 * 
	 * @param flGroupTwentySevenView
	 *            the new fl group twenty seven view
	 */
	public void setFlGroupTwentySevenView(boolean flGroupTwentySevenView) {
		this.flGroupTwentySevenView = flGroupTwentySevenView;
	}

	/**
	 * Checks if is fl invoice cancellation date view.
	 * 
	 * @return true, if is fl invoice cancellation date view
	 */
	public boolean isFlInvoiceCancellationDateView() {
		return flInvoiceCancellationDateView;
	}

	/**
	 * Sets the fl invoice cancellation date view.
	 * 
	 * @param flInvoiceCancellationDateView
	 *            the new fl invoice cancellation date view
	 */
	public void setFlInvoiceCancellationDateView(
			boolean flInvoiceCancellationDateView) {
		this.flInvoiceCancellationDateView = flInvoiceCancellationDateView;
	}

	/**
	 * Checks if is fl frn cancellation date view.
	 * 
	 * @return true, if is fl frn cancellation date view
	 */
	public boolean isFlFRNCancellationDateView() {
		return flFRNCancellationDateView;
	}

	/**
	 * Sets the fl frn cancellation date view.
	 * 
	 * @param flFRNCancellationDateView
	 *            the new fl frn cancellation date view
	 */
	public void setFlFRNCancellationDateView(boolean flFRNCancellationDateView) {
		this.flFRNCancellationDateView = flFRNCancellationDateView;
	}

	/**
	 * Checks if is fl group twenty six view.
	 * 
	 * @return true, if is fl group twenty six view
	 */
	public boolean isFlGroupTwentySixView() {
		return flGroupTwentySixView;
	}

	/**
	 * Sets the fl group twenty six view.
	 * 
	 * @param flGroupTwentySixView
	 *            the new fl group twenty six view
	 */
	public void setFlGroupTwentySixView(boolean flGroupTwentySixView) {
		this.flGroupTwentySixView = flGroupTwentySixView;
	}

	/**
	 * Checks if is fl delivery order cancellation date view.
	 * 
	 * @return true, if is fl delivery order cancellation date view
	 */
	public boolean isFlDeliveryOrderCancellationDateView() {
		return flDeliveryOrderCancellationDateView;
	}

	/**
	 * Sets the fl delivery order cancellation date view.
	 * 
	 * @param flDeliveryOrderCancellationDateView
	 *            the new fl delivery order cancellation date view
	 */
	public void setFlDeliveryOrderCancellationDateView(
			boolean flDeliveryOrderCancellationDateView) {
		this.flDeliveryOrderCancellationDateView = flDeliveryOrderCancellationDateView;
	}

	/**
	 * Checks if is fl contract order cancellation date view.
	 * 
	 * @return true, if is fl contract order cancellation date view
	 */
	public boolean isFlContractOrderCancellationDateView() {
		return flContractOrderCancellationDateView;
	}

	/**
	 * Sets the fl contract order cancellation date view.
	 * 
	 * @param flContractOrderCancellationDateView
	 *            the new fl contract order cancellation date view
	 */
	public void setFlContractOrderCancellationDateView(
			boolean flContractOrderCancellationDateView) {
		this.flContractOrderCancellationDateView = flContractOrderCancellationDateView;
	}

	/**
	 * Checks if is fl group twenty five view.
	 * 
	 * @return true, if is fl group twenty five view
	 */
	public boolean isFlGroupTwentyFiveView() {
		return flGroupTwentyFiveView;
	}

	/**
	 * Sets the fl group twenty five view.
	 * 
	 * @param flGroupTwentyFiveView
	 *            the new fl group twenty five view
	 */
	public void setFlGroupTwentyFiveView(boolean flGroupTwentyFiveView) {
		this.flGroupTwentyFiveView = flGroupTwentyFiveView;
	}

	/**
	 * Checks if is fl contract request rejection date view.
	 * 
	 * @return true, if is fl contract request rejection date view
	 */
	public boolean isFlContractRequestRejectionDateView() {
		return flContractRequestRejectionDateView;
	}

	/**
	 * Sets the fl contract request rejection date view.
	 * 
	 * @param flContractRequestRejectionDateView
	 *            the new fl contract request rejection date view
	 */
	public void setFlContractRequestRejectionDateView(
			boolean flContractRequestRejectionDateView) {
		this.flContractRequestRejectionDateView = flContractRequestRejectionDateView;
	}

	/**
	 * Checks if is fl contract order created date view.
	 * 
	 * @return true, if is fl contract order created date view
	 */
	public boolean isFlContractOrderCreatedDateView() {
		return flContractOrderCreatedDateView;
	}

	/**
	 * Sets the fl contract order created date view.
	 * 
	 * @param flContractOrderCreatedDateView
	 *            the new fl contract order created date view
	 */
	public void setFlContractOrderCreatedDateView(
			boolean flContractOrderCreatedDateView) {
		this.flContractOrderCreatedDateView = flContractOrderCreatedDateView;
	}

	/**
	 * Checks if is fl invoice created date view.
	 * 
	 * @return true, if is fl invoice created date view
	 */
	public boolean isFlInvoiceCreatedDateView() {
		return flInvoiceCreatedDateView;
	}

	/**
	 * Sets the fl invoice created date view.
	 * 
	 * @param flInvoiceCreatedDateView
	 *            the new fl invoice created date view
	 */
	public void setFlInvoiceCreatedDateView(boolean flInvoiceCreatedDateView) {
		this.flInvoiceCreatedDateView = flInvoiceCreatedDateView;
	}

	/**
	 * Checks if is fl stop instruction created date view.
	 * 
	 * @return true, if is fl stop instruction created date view
	 */
	public boolean isFlStopInstructionCreatedDateView() {
		return flStopInstructionCreatedDateView;
	}

	/**
	 * Sets the fl stop instruction created date view.
	 * 
	 * @param flStopInstructionCreatedDateView
	 *            the new fl stop instruction created date view
	 */
	public void setFlStopInstructionCreatedDateView(
			boolean flStopInstructionCreatedDateView) {
		this.flStopInstructionCreatedDateView = flStopInstructionCreatedDateView;
	}

	/**
	 * Checks if is fl from date view.
	 * 
	 * @return true, if is fl from date view
	 */
	public boolean isFlFromDateView() {
		return flFromDateView;
	}

	/**
	 * Sets the fl from date view.
	 * 
	 * @param flFromDateView
	 *            the new fl from date view
	 */
	public void setFlFromDateView(boolean flFromDateView) {
		this.flFromDateView = flFromDateView;
	}

	/**
	 * Checks if is fl contract request created date view.
	 * 
	 * @return true, if is fl contract request created date view
	 */
	public boolean isFlContractRequestCreatedDateView() {
		return flContractRequestCreatedDateView;
	}

	/**
	 * Sets the fl contract request created date view.
	 * 
	 * @param flContractRequestCreatedDateView
	 *            the new fl contract request created date view
	 */
	public void setFlContractRequestCreatedDateView(
			boolean flContractRequestCreatedDateView) {
		this.flContractRequestCreatedDateView = flContractRequestCreatedDateView;
	}

	/**
	 * Checks if is fl contract order date view.
	 * 
	 * @return true, if is fl contract order date view
	 */
	public boolean isFlContractOrderDateView() {
		return flContractOrderDateView;
	}

	/**
	 * Sets the fl contract order date view.
	 * 
	 * @param flContractOrderDateView
	 *            the new fl contract order date view
	 */
	public void setFlContractOrderDateView(boolean flContractOrderDateView) {
		this.flContractOrderDateView = flContractOrderDateView;
	}

	/**
	 * Checks if is fl delivery order created date view.
	 * 
	 * @return true, if is fl delivery order created date view
	 */
	public boolean isFlDeliveryOrderCreatedDateView() {
		return flDeliveryOrderCreatedDateView;
	}

	/**
	 * Sets the fl delivery order created date view.
	 * 
	 * @param flDeliveryOrderCreatedDateView
	 *            the new fl delivery order created date view
	 */
	public void setFlDeliveryOrderCreatedDateView(
			boolean flDeliveryOrderCreatedDateView) {
		this.flDeliveryOrderCreatedDateView = flDeliveryOrderCreatedDateView;
	}

	/**
	 * Checks if is fl frn created date view.
	 * 
	 * @return true, if is fl frn created date view
	 */
	public boolean isFlFRNCreatedDateView() {
		return flFRNCreatedDateView;
	}

	/**
	 * Sets the fl frn created date view.
	 * 
	 * @param flFRNCreatedDateView
	 *            the new fl frn created date view
	 */
	public void setFlFRNCreatedDateView(boolean flFRNCreatedDateView) {
		this.flFRNCreatedDateView = flFRNCreatedDateView;
	}

	/**
	 * Checks if is sm static state view.
	 * 
	 * @return true, if is sm static state view
	 */
	public boolean isSmStaticStateView() {
		return smStaticStateView;
	}

	/**
	 * Sets the sm static state view.
	 * 
	 * @param smStaticStateView
	 *            the new sm static state view
	 */
	public void setSmStaticStateView(boolean smStaticStateView) {
		this.smStaticStateView = smStaticStateView;
	}

	/**
	 * Checks if is sm pending submission date view.
	 * 
	 * @return the smPendingSubmissionDateView
	 */
	public boolean isSmPendingSubmissionDateView() {
		return smPendingSubmissionDateView;
	}

	/**
	 * Sets the sm pending submission date view.
	 * 
	 * @param smPendingSubmissionDateView
	 *            the smPendingSubmissionDateView to set
	 */
	public void setSmPendingSubmissionDateView(
			boolean smPendingSubmissionDateView) {
		this.smPendingSubmissionDateView = smPendingSubmissionDateView;
	}

	// CT
	/** The ct contract no view. */
	private boolean ctContractNoView = false;

	/** The ct agreement view. */
	private boolean ctAgreementView = false;

	/** The ct status view. */
	private boolean ctStatusView = false;

	/** The ct contr montr status view. */
	private boolean ctContrMontrStatusView = false;

	/** The ct complete view. */
	private boolean ctCompleteView = false;

	/** The ct group one view. */
	private boolean ctGroupOneView = false;

	/** The ct group two view. */
	private boolean ctGroupTwoView = false;

	/** The ct product service view. */
	private boolean ctProductServiceView = false;

	/** The ct loan no view. */
	private boolean ctLoanNoView = false;

	/** The ct ministry view. */
	private boolean ctMinistryView = false;

	/** The ct ptj view. */
	private boolean ctPtjView = false;

	/** The ct procurement mode view. */
	private boolean ctProcurementModeView = false;

	/** The ct physical contract no view. */
	private boolean ctPhysicalContractNoView = false;

	/** The ct group three view. */
	private boolean ctGroupThreeView = false;

	/** The ct status two view. */
	private boolean ctStatusTwoView = false;

	/** The ct loa physical no view. */
	private boolean ctLOAPhysicalNoView = false;

	/** The ct user type view. */
	private boolean ctUserTypeView = false;

	/** The ct sub type view. */
	private boolean ctSubTypeView = false;

	/** The ct group four view. */
	private boolean ctGroupFourView = false;

	/** The ct group five view. */
	private boolean ctGroupFiveView = false;

	/** The ct status three view. */
	private boolean ctStatusThreeView = false;

	/** The ct sub type only view. */
	private boolean ctSubTypeOnlyView = false;

	/** The ct sub type mca view. */
	private boolean ctSubTypeMCAView = false;

	/** The ct sub type approver view. */
	private boolean ctSubTypeApproverView = false;

	/** The ct circular effective from date view. */
	private boolean ctCircularEffectiveFromDateView = false;

	/** The ct circular effective to date view. */
	private boolean ctCircularEffectiveToDateView = false;

	/** The ct group five without suppl view. */
	private boolean ctGroupFiveWithoutSupplView = false;

	/** The ct group five with suppl view. */
	private boolean ctGroupFiveWithSupplView = false;
	// PP
	/** The pp year view. */
	private boolean ppYearView = false;

	/** The pp ministry view. */
	private boolean ppMinistryView = false;

	/** The pp mandatory view. */
	private boolean ppMandatoryView = false;

	/** The pp non mandatory view. */
	private boolean ppNonMandatoryView = false;

	/** The pp ptj view. */
	private boolean ppPTJView = false;
	// PM
	/** The pm service type view. */
	private boolean pmServiceTypeView = false;

	/** The pm status view. */
	private boolean pmStatusView = false;

	/** The pm financial org name view. */
	private boolean pmFinancialOrgNameView = false;

	/** The pm business reg no view. */
	private boolean pmBusinessRegNoView = false;

	/** The pm exempted company name view. */
	private boolean pmExemptedCompanyNameView = false;

	/** The pm exempted business reg no view. */
	private boolean pmExemptedBusinessRegNoView = false;

	/** The pm exempted ep no view. */
	private boolean pmExemptedEpNoView = false;

	/** The pm exempted item name view. */
	private boolean pmExemptedItemNameView = false;

	/** The pm exempted itme code view. */
	private boolean pmExemptedItemCodeView = false;

	// URL variables start
	/** The quotation id tot var. */
	private String quotationIdTotVar = ReportConst.EMPTY_VAL;

	/** The item type tot var. */
	private String itemTypeTotVar = ReportConst.EMPTY_VAL;

	/** The item status tot var. */
	private String itemStatusTotVar = ReportConst.EMPTY_VAL;

	/** The unspsc code tot var. */
	private String unspscCodeTotVar = ReportConst.EMPTY_VAL;

	/** The unspsc title tot var. */
	private String unspscTitleTotVar = ReportConst.EMPTY_VAL;

	/** The extention code tot var. */
	private String extentionCodeTotVar = ReportConst.EMPTY_VAL;

	/** The extention type tot var. */
	private String extentionTypeTotVar = ReportConst.EMPTY_VAL;

	/** The extention brand tot var. */
	private String extentionBrandTotVar = ReportConst.EMPTY_VAL;

	/** The extention measurment tot var. */
	private String extentionMeasurmentTotVar = ReportConst.EMPTY_VAL;

	/** The extention colour tot var. */
	private String extentionColourTotVar = ReportConst.EMPTY_VAL;

	/** The company name tot var. */
	private String companyNameTotVar = ReportConst.EMPTY_VAL;

	/** The registration type tot var. */
	private String registrationTypeTotVar = ReportConst.EMPTY_VAL;

	/** The item category tot var. */
	private String itemCategoryTotVar = ReportConst.EMPTY_VAL;

	/** The item name tot var. */
	private String itemNameTotVar = ReportConst.EMPTY_VAL;

	/** The proof identity tot var. */
	private String proofIdentityTotVar = ReportConst.EMPTY_VAL;

	/** The ep number tot var. */
	private String epNumberTotVar = ReportConst.EMPTY_VAL;

	/** The business reg no tot var. */
	private String businessRegNoTotVar = ReportConst.EMPTY_VAL;

	/** The ic number tot var. */
	private String icNumberTotVar = ReportConst.EMPTY_VAL;

	/** The mof number tot var. */
	private String mofNumberTotVar = ReportConst.EMPTY_VAL;

	/** The ptj tot var. */
	private String ptjTotVar = ReportConst.EMPTY_VAL;

	/** The contract no tot var. */
	private String contractNoTotVar = ReportConst.EMPTY_VAL;

	/** The sst tot var. */
	private String sstTotVar = ReportConst.EMPTY_VAL;

	/** The jabatan tot var. */
	private String jabatanTotVar = ReportConst.EMPTY_VAL;

	/** The ministry tot var. */
	private String ministryTotVar = ReportConst.EMPTY_VAL;

	/** The supplier tot var. */
	private String supplierTotVar = ReportConst.EMPTY_VAL;

	/** The status tot var. */
	private String statusTotVar = ReportConst.EMPTY_VAL;

	/** The duration tot var. */
	private String durationTotVar = ReportConst.EMPTY_VAL;

	/** The recieving officer tot var. */
	private String recievingOfficerTotVar = ReportConst.EMPTY_VAL;

	/** The delivery address tot var. */
	private String deliveryAddressTotVar = ReportConst.EMPTY_VAL;

	/** The fl rec note no tot var. */
	private String flRecNoteNoTotVar = ReportConst.EMPTY_VAL;

	/** The contract type tot var. */
	private String contractTypeTotVar = ReportConst.EMPTY_VAL;

	/** The requisitioner tot var. */
	private String requisitionerTotVar = ReportConst.EMPTY_VAL;

	/** The mto contract tot var. */
	private String mtoContractTotVar = ReportConst.EMPTY_VAL;

	/** The invoice no tot var. */
	private String invoiceNoTotVar = ReportConst.EMPTY_VAL;

	/** The deduction type tot var. */
	private String deductionTypeTotVar = ReportConst.EMPTY_VAL;

	/** The cr no tot var. */
	private String crNoTotVar = ReportConst.EMPTY_VAL;

	/** The approver name tot var. */
	private String approverNameTotVar = ReportConst.EMPTY_VAL;

	/** The document type tot var. */
	private String documentTypeTotVar = ReportConst.EMPTY_VAL;

	/** The criteria tot var. */
	private String criteriaTotVar = ReportConst.EMPTY_VAL;

	/** The scoring tot var. */
	private String scoringTotVar = ReportConst.EMPTY_VAL;

	/** The pkp no tot var. */
	private String pkpNoTotVar = ReportConst.EMPTY_VAL;

	/** The delivery order no tot var. */
	private String deliveryOrderNoTotVar = ReportConst.EMPTY_VAL;

	/** The fl supplier delivery order no var. */
	private String flSupplierDeliveryOrderNoVar = ReportConst.EMPTY_VAL;

	/** The item code tot var. */
	private String itemCodeTotVar = ReportConst.EMPTY_VAL;

	/** The po amount tot var. */
	private String poAmountTotVar = ReportConst.EMPTY_VAL;

	/** The pr no tot var. */
	private String prNoTotVar = ReportConst.EMPTY_VAL;

	/** The pmo tot var. */
	private String pmoTotVar = ReportConst.EMPTY_VAL;

	/** The from date var. */
	private String fromDateVar = ReportConst.EMPTY_VAL;

	/** The to date var. */
	private String toDateVar = ReportConst.EMPTY_VAL;

	/** The mof account end date from date var. */
	private String mofAccountEndDateFromDateVar = ReportConst.EMPTY_VAL;

	/** The mof account end date to date var. */
	private String mofAccountEndDateToDateVar = ReportConst.EMPTY_VAL;
	// cm
	/** The cm suppl or admin var. */
	private String cmSupplOrAdminVar = ReportConst.EMPTY_VAL;

	/** The cm extention code var. */
	private String cmExtentionCodeVar = ReportConst.EMPTY_VAL;

	/** The cm extention var. */
	private String cmExtentionVar = ReportConst.EMPTY_VAL;

	/** The cm group one var. */
	private String cmGroupOneVar = ReportConst.EMPTY_VAL;

	/** The cm extention brand var. */
	private String cmExtentionBrandVar = ReportConst.EMPTY_VAL;

	/** The cm extention type var. */
	private String cmExtentionTypeVar = ReportConst.EMPTY_VAL;

	/** The cm extention measurment var. */
	private String cmExtentionMeasurmentVar = ReportConst.EMPTY_VAL;

	/** The cm extention colour var. */
	private String cmExtentionColourVar = ReportConst.EMPTY_VAL;

	/** The cm request number var. */
	private String cmRequestNumberVar = ReportConst.EMPTY_VAL;
	// sm
	/** The payment type tot var. */
	private String paymentTypeTotVar = ReportConst.EMPTY_VAL;

	/** The report type tot var. */
	private String reportTypeTotVar = ReportConst.EMPTY_VAL;

	/** The application type tot var. */
	private String applicationTypeTotVar = ReportConst.EMPTY_VAL;

	/** The state tot var. */
	private String stateTotVar = ReportConst.EMPTY_VAL;

	/** The district tot var. */
	private String districtTotVar = ReportConst.EMPTY_VAL;

	/** The application no tot var. */
	private String applicationNoTotVar = ReportConst.EMPTY_VAL;

	/** The zone tot var. */
	private String zoneTotVar = ReportConst.EMPTY_VAL;

	/** The supplier type tot var. */
	private String supplierTypeTotVar = ReportConst.EMPTY_VAL;

	/** The mof reg no tot var. */
	private String mofRegNoTotVar = ReportConst.EMPTY_VAL;

	/** The registration status tot var. */
	private String registrationStatusTotVar = ReportConst.EMPTY_VAL;

	/** The main category tot var. */
	private String mainCategoryTotVar = ReportConst.EMPTY_VAL;

	/** The sub category tot var. */
	private String subCategoryTotVar = ReportConst.EMPTY_VAL;

	/** The sub category division tot var. */
	private String subCategoryDivisionTotVar = ReportConst.EMPTY_VAL;

	/** The application status tot var. */
	private String applicationStatusTotVar = ReportConst.EMPTY_VAL;

	/** The sm created from year var. */
	private String smCreatedFromYearVar = ReportConst.EMPTY_VAL;

	/** The sm created to year var. */
	private String smCreatedToYearVar = ReportConst.EMPTY_VAL;

	/** The sm monthly rpt by year var. */
	private String smMonthlyRptByYearVar = ReportConst.EMPTY_VAL;

	/** The sm mandatory created from year var. */
	private String smMandatoryCreatedFromYearVar = ReportConst.EMPTY_VAL;

	/** The sm mandatory created to year var. */
	private String smMandatoryCreatedToYearVar = ReportConst.EMPTY_VAL;

	/** The sm svo user var. */
	private String smSVOUserVar = ReportConst.EMPTY_VAL;

	/** The sm year text var. */
	private String smYearTextVar = ReportConst.EMPTY_VAL;

	/** The sm role var. */
	private String smRoleVar = ReportConst.EMPTY_VAL;

	/** The sm ep no var. */
	private String smEpNoVar = ReportConst.EMPTY_VAL;

	/** The sm registration no var. */
	private String smRegistrationNoVar = ReportConst.EMPTY_VAL;

	/** The sm supporting document mode var. */
	private String smSupportingDocumentModeVar = ReportConst.EMPTY_VAL;

	/** The sm supporting document status var. */
	private String smSupportingDocumentStatusVar = ReportConst.EMPTY_VAL;

	/** The sm mof reg no from var. */
	private String smMofRegNoFromVar = ReportConst.EMPTY_VAL;

	/** The sm mof reg no to var. */
	private String smMofRegNoToVar = ReportConst.EMPTY_VAL;

	/** The sm paid up capital frm var. */
	private String smPaidUpCapitalFrmVar = ReportConst.EMPTY_VAL;

	/** The sm paid up capital to var. */
	private String smPaidUpCapitalToVar = ReportConst.EMPTY_VAL;

	/** The sm paid up capital naccum var. */
	private String smPaidUpCapitalNaccumVar = ReportConst.EMPTY_VAL;

	/** The sm personnel role var. */
	private String smPersonnelRoleVar = ReportConst.EMPTY_VAL;

	/** The sm supplier name var. */
	private String smSupplierNameVar = ReportConst.EMPTY_VAL;

	/** The sm mof registration fee var var. */
	private String smMofRegistrationFeeVar = ReportConst.EMPTY_VAL;

	/** The sm processing fee var. */
	private String smProcessingFeeVar = ReportConst.EMPTY_VAL;

	/** The sm soft certificate fee var. */
	private String smSoftCertificateFeeVar = ReportConst.EMPTY_VAL;

	/** The sm identification type var. */
	private String smIdentificationTypeVar = ReportConst.EMPTY_VAL;

	/** The sm identification number var. */
	private String smIdentificationNumberVar = ReportConst.EMPTY_VAL;

	/** The sm cancellation by var. */
	private String smCancellationByVar = ReportConst.EMPTY_VAL;

	/** The sm bus comp type registration status by var. */
	private String smBusCompTypeRegistrationStatusByVar = ReportConst.EMPTY_VAL;

	/** The sm bus company type by var. */
	private String smBusCompanyTypeByVar = ReportConst.EMPTY_VAL;

	/** The sm bus paid up capital frm by var. */
	private String smBusPaidUpCapitalFrmByVar = ReportConst.EMPTY_VAL;

	/** The sm bus paid up capital to by var. */
	private String smBusPaidUpCapitalToByVar = ReportConst.EMPTY_VAL;

	/** The sm processing officer name var. */
	private String smProcessingOfficerNameVar = ReportConst.EMPTY_VAL;

	/** The sm report type var. */
	private String smReportTypeVar = ReportConst.EMPTY_VAL;

	/** The sm receipt no var. */
	private String smReceiptNoVar = ReportConst.EMPTY_VAL;

	/** The sm processing type var. */
	private String smProcessingTypeVar = ReportConst.EMPTY_VAL;

	/** The sm payment method var. */
	private String smPaymentMethodVar = ReportConst.EMPTY_VAL;

	/** The Sm code var. */
	private String SmCodeVar = ReportConst.EMPTY_VAL;

	/** The sm from month var. */
	private String smFromMonthVar = ReportConst.EMPTY_VAL;

	/** The sm to month var. */
	private String smToMonthVar = ReportConst.EMPTY_VAL;

	/** The sm rt report type var. */
	private String smRTReportTypeVar = ReportConst.EMPTY_VAL;

	/** The Sm threshold from year var. */
	private String SmThresholdFromYearVar = ReportConst.EMPTY_VAL;

	/** The Sm threshold to year var. */
	private String SmThresholdToYearVar = ReportConst.EMPTY_VAL;

	/** The sm user name var. */
	private String smUserNameVar = ReportConst.EMPTY_VAL;

	/** The sm request expiry date var. */
	private String smRequestExpiryDateVar = ReportConst.EMPTY_VAL;

	/** The sm profile status var. */
	private String smProfileStatusVar = ReportConst.EMPTY_VAL;

	/** The sm gst registration no var. */
	private String smGSTRegistrationNoVar = ReportConst.EMPTY_VAL;

	/** The sm gst registered status var. */
	private String smGSTRegisteredStatusVar = ReportConst.EMPTY_VAL;

	/** The sm payment to var. */
	private String smPaymentToVar = ReportConst.EMPTY_VAL;

	/** The report value id var. */
	private String reportValueIdVar = ReportConst.EMPTY_VAL;

	/** The user id var. */
	private String userIdVar = ReportConst.EMPTY_VAL;
	// sc
	/** The sc created from dt var. */
	private String scCreatedFromDtVar = ReportConst.EMPTY_VAL;

	/** The sc created to dt var. */
	private String scCreatedToDtVar = ReportConst.EMPTY_VAL;

	/** The sc status var. */
	private String scStatusVar = ReportConst.EMPTY_VAL;

	/** The sc ptj var. */
	private String scPtjVar = ReportConst.EMPTY_VAL;

	/** The sc desk officer var. */
	private String scDeskOfficerVar = ReportConst.EMPTY_VAL;

	/** The sc procure method chk var. */
	private String scProcureMethodChkVar = ReportConst.EMPTY_VAL;

	/** The sc publish from date var. */
	private String scPublishFromDateVar = ReportConst.EMPTY_VAL;

	/** The sc publish to date var. */
	private String scPublishToDateVar = ReportConst.EMPTY_VAL;

	/** The sc user access role var. */
	private String scUserAccessRoleVar = ReportConst.EMPTY_VAL;

	/** The sc sone crtd from date var. */
	private String scSoneCrtdFromDateVar = ReportConst.EMPTY_VAL;

	/** The sc sone crtd to date var. */
	private String scSoneCrtdToDateVar = ReportConst.EMPTY_VAL;

	/** The sc requisener id var. */
	private String scRequisenerIdVar = ReportConst.EMPTY_VAL;

	/** The sc supplier var. */
	private String scSupplierVar = ReportConst.EMPTY_VAL;

	/** The sc p ino var. */
	private String scPInoVar = ReportConst.EMPTY_VAL;

	/** The sc sone status var. */
	private String scSoneStatusVar = ReportConst.EMPTY_VAL;

	/** The sc prc rpt group var. */
	private String scPrcRptGroupVar = ReportConst.EMPTY_VAL;

	/** The sc pi dtls prc rpt group var. */
	private String scPIDtlsPrcRptGroupVar = ReportConst.EMPTY_VAL;

	/** The sc procurement type var. */
	private String scProcurementTypeVar = ReportConst.EMPTY_VAL;

	/** The sc process rpt quotation no var. */
	private String scProcessRptQuotationNoVar = ReportConst.EMPTY_VAL;

	/** The sc common status var. */
	private String scCommonStatusVar = ReportConst.EMPTY_VAL;

	/** The sc laprnpurchdefault group var. */
	private String scLAPRNPURCHDEFAULTGroupVar = ReportConst.EMPTY_VAL;

	/** The sc requestnotesummary group var. */
	private String scREQUESTNOTESUMMARYGroupVar = ReportConst.EMPTY_VAL;

	/** The sc pid no var. */
	private String scPIDNoVar = ReportConst.EMPTY_VAL;

	/** The sc contract or pkp type var. */
	private String scContractOrPKPTypeVar = ReportConst.EMPTY_VAL;

	/** The sc contract or pkp no var. */
	private String scContractOrPKPNoVar = ReportConst.EMPTY_VAL;

	/** The sc contract or pkp name var. */
	private String scContractOrPKPNameVar = ReportConst.EMPTY_VAL;

	/** The sc request quote no var. */
	private String scRequestQuoteNoVar = ReportConst.EMPTY_VAL;

	/** The sc request note status var. */
	private String scRequestNoteStatusVar = ReportConst.EMPTY_VAL;

	/** The sc simple quote status var. */
	private String scSimpleQuoteStatusVar = ReportConst.EMPTY_VAL;

	/** The sc simple quote dtl group var. */
	private String scSimpleQuoteDtlGroupVar = ReportConst.EMPTY_VAL;

	/** The sc purchase in default status var. */
	private String scPurchaseInDefaultStatusVar = ReportConst.EMPTY_VAL;

	/** The sc soa status var. */
	private String scSOAStatusVar = ReportConst.EMPTY_VAL;

	/** The sc simple quote type var. */
	private String scSimpleQuoteTypeVar = ReportConst.EMPTY_VAL;

	/** The sc loa no var. */
	private String scLOANoVar = ReportConst.EMPTY_VAL;
	// CT
	/** The ct contract no var. */
	private String ctContractNoVar = ReportConst.EMPTY_VAL;

	/** The ct agreement var. */
	private String ctAgreementVar = ReportConst.EMPTY_VAL;

	/** The ct status var. */
	private String ctStatusVar = ReportConst.EMPTY_VAL;

	/** The ct contr montr status var. */
	private String ctContrMontrStatusVar = ReportConst.EMPTY_VAL;

	/** The ct hidden param group var. */
	private String ctHiddenParamGroupVar = ReportConst.EMPTY_VAL;

	/** The ct complete var. */
	private String ctCompleteVar = ReportConst.EMPTY_VAL;

	/** The ct group one var. */
	private String ctGroupOneVar = ReportConst.EMPTY_VAL;

	/** The ct group two var. */
	private String ctGroupTwoVar = ReportConst.EMPTY_VAL;

	/** The ct product service var. */
	private String ctProductServiceVar = ReportConst.EMPTY_VAL;

	/** The ct loan no var. */
	private String ctLoanNoVar = ReportConst.EMPTY_VAL;

	/** The ct ministry var. */
	private String ctMinistryVar = ReportConst.EMPTY_VAL;

	/** The ct ptj var. */
	private String ctPtjVar = ReportConst.EMPTY_VAL;

	/** The ct procurement mode var. */
	private String ctProcurementModeVar = ReportConst.EMPTY_VAL;

	/** The ct physical contract no var. */
	private String ctPhysicalContractNoVar = ReportConst.EMPTY_VAL;

	/** The ct group three var. */
	private String ctGroupThreeVar = ReportConst.EMPTY_VAL;

	/** The ct status two var. */
	private String ctStatusTwoVar = ReportConst.EMPTY_VAL;

	/** The ct loa physical no var. */
	private String ctLOAPhysicalNoVar = ReportConst.EMPTY_VAL;

	/** The ct user type var. */
	private String ctUserTypeVar = ReportConst.EMPTY_VAL;

	/** The ct sub type var. */
	private String ctSubTypeVar = ReportConst.EMPTY_VAL;

	/** The ct circular no var. */
	private String ctCircularNoVar = ReportConst.EMPTY_VAL;

	/** The ct circular name var. */
	private String ctCircularNameVar = ReportConst.EMPTY_VAL;

	/** The ct circular type var. */
	private String ctCircularTypeVar = ReportConst.EMPTY_VAL;

	/** The ct circular effective from date var. */
	private String ctCircularEffectiveFromDateVar = ReportConst.EMPTY_VAL;

	/** The ct circular effective to date var. */
	private String ctCircularEffectiveToDateVar = ReportConst.EMPTY_VAL;

	/** The ct supplier name var. */
	private String ctSupplierNameVar = ReportConst.EMPTY_VAL;
	// ct
	/** The fl contract order var. */
	private String flContractOrderVar = ReportConst.EMPTY_VAL;

	/** The fl contract request var. */
	private String flContractRequestVar = ReportConst.EMPTY_VAL;

	/** The fl purchase order no var. */
	private String flPurchaseOrderNoVar = ReportConst.EMPTY_VAL;

	/** The fl group one var. */
	private String flGroupOneVar = ReportConst.EMPTY_VAL;

	/** The fl group two var. */
	private String flGroupTwoVar = ReportConst.EMPTY_VAL;

	/** The fl group three var. */
	private String flGroupThreeVar = ReportConst.EMPTY_VAL;

	/** The fl group four var. */
	private String flGroupFourVar = ReportConst.EMPTY_VAL;

	/** The fl group five var. */
	private String flGroupFiveVar = ReportConst.EMPTY_VAL;

	/** The fl group six var. */
	private String flGroupSixVar = ReportConst.EMPTY_VAL;

	/** The fl group seven var. */
	private String flGroupSevenVar = ReportConst.EMPTY_VAL;

	/** The fl group eight var. */
	private String flGroupEightVar = ReportConst.EMPTY_VAL;

	/** The fl group nine var. */
	private String flGroupNineVar = ReportConst.EMPTY_VAL;

	/** The fl group ten var. */
	private String flGroupTenVar = ReportConst.EMPTY_VAL;

	/** The fl group eleven var. */
	private String flGroupElevenVar = ReportConst.EMPTY_VAL;

	/** The fl group twelve var. */
	private String flGroupTwelveVar = ReportConst.EMPTY_VAL;

	/** The fl group thirteen var. */
	private String flGroupThirteenVar = ReportConst.EMPTY_VAL;

	/** The fl group fourteen var. */
	private String flGroupFourteenVar = ReportConst.EMPTY_VAL;

	/** The fl group fifteen var. */
	private String flGroupFifteenVar = ReportConst.EMPTY_VAL;

	/** The fl group sixteen var. */
	private String flGroupSixteenVar = ReportConst.EMPTY_VAL;

	/** The fl group seventeen var. */
	private String flGroupSeventeenVar = ReportConst.EMPTY_VAL;

	/** The fl stop instruction no var. */
	private String flStopInstructionNoVar = ReportConst.EMPTY_VAL;

	/** The fl supplier invoice no var. */
	private String flSupplierInvoiceNoVar = ReportConst.EMPTY_VAL;

	/** The fl payment advice no var. */
	private String flPaymentAdviceNoVar = ReportConst.EMPTY_VAL;
	// PP
	/** The pp year var. */
	private String ppYearVar = ReportConst.EMPTY_VAL;

	/** The pp ministry var. */
	private String ppMinistryVar = ReportConst.EMPTY_VAL;

	/** The pp ptj var. */
	private String ppPTJVar = ReportConst.EMPTY_VAL;
	// PM
	/** The pm service type var. */
	private String pmServiceTypeVar = ReportConst.EMPTY_VAL;

	/** The pm status var. */
	private String pmStatusVar = ReportConst.EMPTY_VAL;

	/** The pm financial org name var. */
	private String pmFinancialOrgNameVar = ReportConst.EMPTY_VAL;

	/** The pm business reg no var. */
	private String pmBusinessRegNoVar = ReportConst.EMPTY_VAL;

	/** The pm ep no var. */
	private String pmEpNoVar = ReportConst.EMPTY_VAL;

	/** The pm org category var. */
	private String pmOrgCategoryVar = ReportConst.EMPTY_VAL;

	/** The pm org type var. */
	private String pmOrgTypeVar = ReportConst.EMPTY_VAL;

	/** The pm org name var. */
	private String pmOrgNameVar = ReportConst.EMPTY_VAL;

	/** The pm org code var. */
	private String pmOrgCodeVar = ReportConst.EMPTY_VAL;

	/** The pm item name var. */
	private String pmItemNameVar = ReportConst.EMPTY_VAL;

	/** The pm item code var. */
	private String pmItemCodeVar = ReportConst.EMPTY_VAL;

	/** The pm company name var. */
	private String pmCompanyNameVar = ReportConst.EMPTY_VAL;

	/** The pm identification no var. */
	private String pmIdentificationNoVar = ReportConst.EMPTY_VAL;

	/** The pm user name var. */
	private String pmUserNameVar = ReportConst.EMPTY_VAL;

	/** The pm role code var. */
	private String pmRoleCodeVar = ReportConst.EMPTY_VAL;

	/** The pm contract no var. */
	private String pmContractNoVar = ReportConst.EMPTY_VAL;

	/** The pm physical contract no var. */
	private String pmPhysicalContractNoVar = ReportConst.EMPTY_VAL;

	/** The pm loa no var. */
	private String pmLOANoVar = ReportConst.EMPTY_VAL;

	/** The pm physical loa no var. */
	private String pmPhysicalLOANoVar = ReportConst.EMPTY_VAL;

	/** The pm zone var. */
	private String pmZoneVar = ReportConst.EMPTY_VAL;

	/** The pm profile status var. */
	private String pmProfileStatusVar = ReportConst.EMPTY_VAL;

	/** The Pm mandatory org category type view. */
	private boolean pmMandatoryOrgCategoryTypeView = false;

	/** The Pm mandatory org type view. */
	private boolean pmMandatoryOrgTypeView = false;

	/** The Pm org name view. */
	private boolean pmOrgNameView = false;

	/** The Pm org code view. */
	private boolean pmOrgCodeView = false;

	/** The Pm mandatory org name view. */
	private boolean pmMandatoryOrgNameView = false;

	/** The Pm identification no view. */
	private boolean pmIdentificationNoView = false;

	/** The Pm user name view. */
	private boolean pmUserNameView = false;

	/** The Pm role code view. */
	private boolean pmRoleCodeView = false;

	/** The Pm non mandatory org name view. */
	private boolean pmNonMandatoryOrgNameView = false;

	/** The Pm non mandatroy org code view. */
	private boolean pmNonMandatroyOrgCodeView = false;
	
	/** The pm exempted item view. */
	private boolean pmExemptedItemView=false;
	
	/** The pm exempted contracted item view. */
	private boolean pmExemptedContractedItemView=false;

	/** The Pm company name view. */
	private boolean pmCompanyNameView = false;

	/** The Pm ep no view. */
	private boolean pmEpNoView = false;

	/** The Pm itme name view. */
	private boolean pmItemNameView = false;

	/** The Pm itme code view. */
	private boolean pmItemCodeView = false;

	/** The Pm from date view. */
	private boolean pmFromDateView = false;

	/** The pm non mandatory date view. */
	private boolean pmNonMandatoryDateView = false;

	/** The pm non mandatory sup date view. */
	private boolean pmNonMandatorySupDateView = false;

	/** The Pm contract no view. */
	private boolean pmContractNoView = false;

	/** The pm physical contract no view. */
	private boolean pmPhysicalContractNoView = false;

	/** The pm loa no view. */
	private boolean pmLOANoView = false;

	/** The pm physical loa no view. */
	private boolean pmPhysicalLOANoView = false;

	/** The pm mandatory zone view. */
	private boolean pmMandatoryZoneView = false;

	/** The pm mandatory zone view bpk. */
	private boolean pmMandatoryZoneViewBPK = false;

	/** The pm profile status vie view. */
	private boolean pmProfileStatusView = false;

	/** The ct termination type var. */
	private String ctTerminationTypeVar = ReportConst.EMPTY_VAL;

	/**
	 * Checks if is sc soa status view.
	 * 
	 * @return true, if is sc soa status view
	 */
	public boolean isScSOAStatusView() {
		return scSOAStatusView;
	}

	/**
	 * Sets the sc soa status view.
	 * 
	 * @param scSOAStatusView
	 *            the new sc soa status view
	 */
	public void setScSOAStatusView(boolean scSOAStatusView) {
		this.scSOAStatusView = scSOAStatusView;
	}

	/**
	 * Checks if is sc simple quote type view.
	 * 
	 * @return true, if is sc simple quote type view
	 */
	public boolean isScSimpleQuoteTypeView() {
		return scSimpleQuoteTypeView;
	}

	/**
	 * Sets the sc simple quote type view.
	 * 
	 * @param scSimpleQuoteTypeView
	 *            the new sc simple quote type view
	 */
	public void setScSimpleQuoteTypeView(boolean scSimpleQuoteTypeView) {
		this.scSimpleQuoteTypeView = scSimpleQuoteTypeView;
	}

	/**
	 * Checks if is sc loa no view.
	 * 
	 * @return true, if is sc loa no view
	 */
	public boolean isScLOANoView() {
		return scLOANoView;
	}

	/**
	 * Sets the sc loa no view.
	 * 
	 * @param scLOANoView
	 *            the new sc loa no view
	 */
	public void setScLOANoView(boolean scLOANoView) {
		this.scLOANoView = scLOANoView;
	}

	/**
	 * Gets the sc soa status var.
	 * 
	 * @return the sc soa status var
	 */
	public String getScSOAStatusVar() {
		return scSOAStatusVar;
	}

	/**
	 * Sets the sc soa status var.
	 * 
	 * @param scSOAStatusVar
	 *            the new sc soa status var
	 */
	public void setScSOAStatusVar(String scSOAStatusVar) {
		this.scSOAStatusVar = scSOAStatusVar;
	}

	/**
	 * Gets the sc simple quote type var.
	 * 
	 * @return the sc simple quote type var
	 */
	public String getScSimpleQuoteTypeVar() {
		return scSimpleQuoteTypeVar;
	}

	/**
	 * Sets the sc simple quote type var.
	 * 
	 * @param scSimpleQuoteTypeVar
	 *            the new sc simple quote type var
	 */
	public void setScSimpleQuoteTypeVar(String scSimpleQuoteTypeVar) {
		this.scSimpleQuoteTypeVar = scSimpleQuoteTypeVar;
	}

	/**
	 * Gets the sc loa no var.
	 * 
	 * @return the sc loa no var
	 */
	public String getScLOANoVar() {
		return scLOANoVar;
	}

	/**
	 * Sets the sc loa no var.
	 * 
	 * @param scLOANoVar
	 *            the new sc loa no var
	 */
	public void setScLOANoVar(String scLOANoVar) {
		this.scLOANoVar = scLOANoVar;
	}

	/**
	 * Checks if is cm request number view.
	 * 
	 * @return true, if is cm request number view
	 */
	public boolean isCmRequestNumberView() {
		return cmRequestNumberView;
	}

	/**
	 * Sets the cm request number view.
	 * 
	 * @param cmRequestNumberView
	 *            the new cm request number view
	 */
	public void setCmRequestNumberView(boolean cmRequestNumberView) {
		this.cmRequestNumberView = cmRequestNumberView;
	}

	/**
	 * Gets the cm request number var.
	 * 
	 * @return the cm request number var
	 */
	public String getCmRequestNumberVar() {
		return cmRequestNumberVar;
	}

	/**
	 * Sets the cm request number var.
	 * 
	 * @param cmRequestNumberVar
	 *            the new cm request number var
	 */
	public void setCmRequestNumberVar(String cmRequestNumberVar) {
		this.cmRequestNumberVar = cmRequestNumberVar;
	}

	/**
	 * Checks if is ct group five without suppl view.
	 * 
	 * @return true, if is ct group five without suppl view
	 */
	public boolean isCtGroupFiveWithoutSupplView() {
		return ctGroupFiveWithoutSupplView;
	}

	/**
	 * Sets the ct group five without suppl view.
	 * 
	 * @param ctGroupFiveWithoutSupplView
	 *            the new ct group five without suppl view
	 */
	public void setCtGroupFiveWithoutSupplView(
			boolean ctGroupFiveWithoutSupplView) {
		this.ctGroupFiveWithoutSupplView = ctGroupFiveWithoutSupplView;
	}

	/**
	 * Checks if is ct group five with suppl view.
	 * 
	 * @return true, if is ct group five with suppl view
	 */
	public boolean isCtGroupFiveWithSupplView() {
		return ctGroupFiveWithSupplView;
	}

	/**
	 * Sets the ct group five with suppl view.
	 * 
	 * @param ctGroupFiveWithSupplView
	 *            the new ct group five with suppl view
	 */
	public void setCtGroupFiveWithSupplView(boolean ctGroupFiveWithSupplView) {
		this.ctGroupFiveWithSupplView = ctGroupFiveWithSupplView;
	}

	/**
	 * Checks if is ct supplier name view.
	 * 
	 * @return true, if is ct supplier name view
	 */
	public boolean isCtSupplierNameView() {
		return ctSupplierNameView;
	}

	/**
	 * Sets the ct supplier name view.
	 * 
	 * @param ctSupplierNameView
	 *            the new ct supplier name view
	 */
	public void setCtSupplierNameView(boolean ctSupplierNameView) {
		this.ctSupplierNameView = ctSupplierNameView;
	}

	/**
	 * Gets the ct supplier name var.
	 * 
	 * @return the ct supplier name var
	 */
	public String getCtSupplierNameVar() {
		return ctSupplierNameVar;
	}

	/**
	 * Sets the ct supplier name var.
	 * 
	 * @param ctSupplierNameVar
	 *            the new ct supplier name var
	 */
	public void setCtSupplierNameVar(String ctSupplierNameVar) {
		this.ctSupplierNameVar = ctSupplierNameVar;
	}

	/**
	 * Checks if is ct circular effective from date view.
	 * 
	 * @return true, if is ct circular effective from date view
	 */
	public boolean isCtCircularEffectiveFromDateView() {
		return ctCircularEffectiveFromDateView;
	}

	/**
	 * Sets the ct circular effective from date view.
	 * 
	 * @param ctCircularEffectiveFromDateView
	 *            the new ct circular effective from date view
	 */
	public void setCtCircularEffectiveFromDateView(
			boolean ctCircularEffectiveFromDateView) {
		this.ctCircularEffectiveFromDateView = ctCircularEffectiveFromDateView;
	}

	/**
	 * Checks if is ct circular effective to date view.
	 * 
	 * @return true, if is ct circular effective to date view
	 */
	public boolean isCtCircularEffectiveToDateView() {
		return ctCircularEffectiveToDateView;
	}

	/**
	 * Sets the ct circular effective to date view.
	 * 
	 * @param ctCircularEffectiveToDateView
	 *            the new ct circular effective to date view
	 */
	public void setCtCircularEffectiveToDateView(
			boolean ctCircularEffectiveToDateView) {
		this.ctCircularEffectiveToDateView = ctCircularEffectiveToDateView;
	}

	/**
	 * Gets the ct circular effective from date var.
	 * 
	 * @return the ct circular effective from date var
	 */
	public String getCtCircularEffectiveFromDateVar() {
		return ctCircularEffectiveFromDateVar;
	}

	/**
	 * Sets the ct circular effective from date var.
	 * 
	 * @param ctCircularEffectiveFromDateVar
	 *            the new ct circular effective from date var
	 */
	public void setCtCircularEffectiveFromDateVar(
			String ctCircularEffectiveFromDateVar) {
		this.ctCircularEffectiveFromDateVar = ctCircularEffectiveFromDateVar;
	}

	/**
	 * Gets the ct circular effective to date var.
	 * 
	 * @return the ct circular effective to date var
	 */
	public String getCtCircularEffectiveToDateVar() {
		return ctCircularEffectiveToDateVar;
	}

	/**
	 * Sets the ct circular effective to date var.
	 * 
	 * @param ctCircularEffectiveToDateVar
	 *            the new ct circular effective to date var
	 */
	public void setCtCircularEffectiveToDateVar(
			String ctCircularEffectiveToDateVar) {
		this.ctCircularEffectiveToDateVar = ctCircularEffectiveToDateVar;
	}

	/**
	 * Checks if is ct circular no view.
	 * 
	 * @return true, if is ct circular no view
	 */
	public boolean isCtCircularNoView() {
		return ctCircularNoView;
	}

	/**
	 * Sets the ct circular no view.
	 * 
	 * @param ctCircularNoView
	 *            the new ct circular no view
	 */
	public void setCtCircularNoView(boolean ctCircularNoView) {
		this.ctCircularNoView = ctCircularNoView;
	}

	/**
	 * Checks if is ct circular name view.
	 * 
	 * @return true, if is ct circular name view
	 */
	public boolean isCtCircularNameView() {
		return ctCircularNameView;
	}

	/**
	 * Sets the ct circular name view.
	 * 
	 * @param ctCircularNameView
	 *            the new ct circular name view
	 */
	public void setCtCircularNameView(boolean ctCircularNameView) {
		this.ctCircularNameView = ctCircularNameView;
	}

	/**
	 * Checks if is ct circular type view.
	 * 
	 * @return true, if is ct circular type view
	 */
	public boolean isCtCircularTypeView() {
		return ctCircularTypeView;
	}

	/**
	 * Sets the ct circular type view.
	 * 
	 * @param ctCircularTypeView
	 *            the new ct circular type view
	 */
	public void setCtCircularTypeView(boolean ctCircularTypeView) {
		this.ctCircularTypeView = ctCircularTypeView;
	}

	/**
	 * Gets the ct circular no var.
	 * 
	 * @return the ct circular no var
	 */
	public String getCtCircularNoVar() {
		return ctCircularNoVar;
	}

	/**
	 * Sets the ct circular no var.
	 * 
	 * @param ctCircularNoVar
	 *            the new ct circular no var
	 */
	public void setCtCircularNoVar(String ctCircularNoVar) {
		this.ctCircularNoVar = ctCircularNoVar;
	}

	/**
	 * Gets the ct circular name var.
	 * 
	 * @return the ct circular name var
	 */
	public String getCtCircularNameVar() {
		return ctCircularNameVar;
	}

	/**
	 * Sets the ct circular name var.
	 * 
	 * @param ctCircularNameVar
	 *            the new ct circular name var
	 */
	public void setCtCircularNameVar(String ctCircularNameVar) {
		this.ctCircularNameVar = ctCircularNameVar;
	}

	/**
	 * Gets the ct circular type var.
	 * 
	 * @return the ct circular type var
	 */
	public String getCtCircularTypeVar() {
		return ctCircularTypeVar;
	}

	/**
	 * Sets the ct circular type var.
	 * 
	 * @param ctCircularTypeVar
	 *            the new ct circular type var
	 */
	public void setCtCircularTypeVar(String ctCircularTypeVar) {
		this.ctCircularTypeVar = ctCircularTypeVar;
	}

	/**
	 * Checks if is sc simple quote dtl without requi group view.
	 * 
	 * @return true, if is sc simple quote dtl without requi group view
	 */
	public boolean isScSimpleQuoteDtlWithoutRequiGroupView() {
		return scSimpleQuoteDtlWithoutRequiGroupView;
	}

	/**
	 * Sets the sc simple quote dtl without requi group view.
	 * 
	 * @param scSimpleQuoteDtlWithoutRequiGroupView
	 *            the new sc simple quote dtl without requi group view
	 */
	public void setScSimpleQuoteDtlWithoutRequiGroupView(
			boolean scSimpleQuoteDtlWithoutRequiGroupView) {
		this.scSimpleQuoteDtlWithoutRequiGroupView = scSimpleQuoteDtlWithoutRequiGroupView;
	}

	/**
	 * Checks if is ct termination type view.
	 * 
	 * @return true, if is ct termination type view
	 */
	public boolean isCtTerminationTypeView() {
		return ctTerminationTypeView;
	}

	/**
	 * Sets the ct termination type view.
	 * 
	 * @param ctTerminationTypeView
	 *            the new ct termination type view
	 */
	public void setCtTerminationTypeView(boolean ctTerminationTypeView) {
		this.ctTerminationTypeView = ctTerminationTypeView;
	}

	/**
	 * Gets the ct termination type var.
	 * 
	 * @return the ct termination type var
	 */
	public String getCtTerminationTypeVar() {
		return ctTerminationTypeVar;
	}

	/**
	 * Sets the ct termination type var.
	 * 
	 * @param ctTerminationTypeVar
	 *            the new ct termination type var
	 */
	public void setCtTerminationTypeVar(String ctTerminationTypeVar) {
		this.ctTerminationTypeVar = ctTerminationTypeVar;
	}

	/**
	 * Gets the sm payment to var.
	 * 
	 * @return the sm payment to var
	 */
	public String getSmPaymentToVar() {
		return smPaymentToVar;
	}

	/**
	 * Sets the sm payment to var.
	 * 
	 * @param smPaymentToVar
	 *            the new sm payment to var
	 */
	public void setSmPaymentToVar(String smPaymentToVar) {
		this.smPaymentToVar = smPaymentToVar;
	}

	/**
	 * Checks if is ct sub type only view.
	 * 
	 * @return true, if is ct sub type only view
	 */
	public boolean isCtSubTypeOnlyView() {
		return ctSubTypeOnlyView;
	}

	/**
	 * Sets the ct sub type only view.
	 * 
	 * @param ctSubTypeOnlyView
	 *            the new ct sub type only view
	 */
	public void setCtSubTypeOnlyView(boolean ctSubTypeOnlyView) {
		this.ctSubTypeOnlyView = ctSubTypeOnlyView;
	}

	/**
	 * Checks if is ct sub type mca view.
	 * 
	 * @return true, if is ct sub type mca view
	 */
	public boolean isCtSubTypeMCAView() {
		return ctSubTypeMCAView;
	}

	/**
	 * Sets the ct sub type mca view.
	 * 
	 * @param ctSubTypeMCAView
	 *            the new ct sub type mca view
	 */
	public void setCtSubTypeMCAView(boolean ctSubTypeMCAView) {
		this.ctSubTypeMCAView = ctSubTypeMCAView;
	}

	/**
	 * Checks if is ct sub type approver view.
	 * 
	 * @return true, if is ct sub type approver view
	 */
	public boolean isCtSubTypeApproverView() {
		return ctSubTypeApproverView;
	}

	/**
	 * Sets the ct sub type approver view.
	 * 
	 * @param ctSubTypeApproverView
	 *            the new ct sub type approver view
	 */
	public void setCtSubTypeApproverView(boolean ctSubTypeApproverView) {
		this.ctSubTypeApproverView = ctSubTypeApproverView;
	}

	/**
	 * Checks if is pm status view.
	 * 
	 * @return true, if is pm status view
	 */
	public boolean isPmStatusView() {
		return pmStatusView;
	}

	/**
	 * Sets the pm status view.
	 * 
	 * @param pmStatusView
	 *            the new pm status view
	 */
	public void setPmStatusView(boolean pmStatusView) {
		this.pmStatusView = pmStatusView;
	}

	/**
	 * Checks if is pm financial org name view.
	 * 
	 * @return true, if is pm financial org name view
	 */
	public boolean isPmFinancialOrgNameView() {
		return pmFinancialOrgNameView;
	}

	/**
	 * Sets the pm financial org name view.
	 * 
	 * @param pmFinancialOrgNameView
	 *            the new pm financial org name view
	 */
	public void setPmFinancialOrgNameView(boolean pmFinancialOrgNameView) {
		this.pmFinancialOrgNameView = pmFinancialOrgNameView;
	}

	/**
	 * Checks if is pm business reg no view.
	 * 
	 * @return true, if is pm business reg no view
	 */
	public boolean isPmBusinessRegNoView() {
		return pmBusinessRegNoView;
	}

	/**
	 * Sets the pm business reg no view.
	 * 
	 * @param pmBusinessRegNoView
	 *            the new pm business reg no view
	 */
	public void setPmBusinessRegNoView(boolean pmBusinessRegNoView) {
		this.pmBusinessRegNoView = pmBusinessRegNoView;
	}

	/**
	 * Gets the pm status var.
	 * 
	 * @return the pm status var
	 */
	public String getPmStatusVar() {
		return pmStatusVar;
	}

	/**
	 * Sets the pm status var.
	 * 
	 * @param pmStatusVar
	 *            the new pm status var
	 */
	public void setPmStatusVar(String pmStatusVar) {
		this.pmStatusVar = pmStatusVar;
	}

	/**
	 * Gets the pm financial org name var.
	 * 
	 * @return the pm financial org name var
	 */
	public String getPmFinancialOrgNameVar() {
		return pmFinancialOrgNameVar;
	}

	/**
	 * Sets the pm financial org name var.
	 * 
	 * @param pmFinancialOrgNameVar
	 *            the new pm financial org name var
	 */
	public void setPmFinancialOrgNameVar(String pmFinancialOrgNameVar) {
		this.pmFinancialOrgNameVar = pmFinancialOrgNameVar;
	}

	/**
	 * Gets the pm business reg no var.
	 * 
	 * @return the pm business reg no var
	 */
	public String getPmBusinessRegNoVar() {
		return pmBusinessRegNoVar;
	}

	/**
	 * Sets the pm business reg no var.
	 * 
	 * @param pmBusinessRegNoVar
	 *            the new pm business reg no var
	 */
	public void setPmBusinessRegNoVar(String pmBusinessRegNoVar) {
		this.pmBusinessRegNoVar = pmBusinessRegNoVar;
	}

	/**
	 * Gets the pm service type var.
	 * 
	 * @return the pm service type var
	 */
	public String getPmServiceTypeVar() {
		return pmServiceTypeVar;
	}

	/**
	 * Sets the pm service type var.
	 * 
	 * @param pmServiceTypeVar
	 *            the new pm service type var
	 */
	public void setPmServiceTypeVar(String pmServiceTypeVar) {
		this.pmServiceTypeVar = pmServiceTypeVar;
	}

	/**
	 * Checks if is pm service type view.
	 * 
	 * @return true, if is pm service type view
	 */
	public boolean isPmServiceTypeView() {
		return pmServiceTypeView;
	}

	/**
	 * Sets the pm service type view.
	 * 
	 * @param pmServiceTypeView
	 *            the new pm service type view
	 */
	public void setPmServiceTypeView(boolean pmServiceTypeView) {
		this.pmServiceTypeView = pmServiceTypeView;
	}

	/**
	 * Checks if is fl group twenty four view.
	 * 
	 * @return true, if is fl group twenty four view
	 */
	public boolean isFlGroupTwentyFourView() {
		return flGroupTwentyFourView;
	}

	/**
	 * Sets the fl group twenty four view.
	 * 
	 * @param flGroupTwentyFourView
	 *            the new fl group twenty four view
	 */
	public void setFlGroupTwentyFourView(boolean flGroupTwentyFourView) {
		this.flGroupTwentyFourView = flGroupTwentyFourView;
	}

	/**
	 * Checks if is fl group twenty three view.
	 * 
	 * @return true, if is fl group twenty three view
	 */
	public boolean isFlGroupTwentyThreeView() {
		return flGroupTwentyThreeView;
	}

	/**
	 * Sets the fl group twenty three view.
	 * 
	 * @param flGroupTwentyThreeView
	 *            the new fl group twenty three view
	 */
	public void setFlGroupTwentyThreeView(boolean flGroupTwentyThreeView) {
		this.flGroupTwentyThreeView = flGroupTwentyThreeView;
	}

	/**
	 * Checks if is fl payment advice no view.
	 * 
	 * @return true, if is fl payment advice no view
	 */
	public boolean isFlPaymentAdviceNoView() {
		return flPaymentAdviceNoView;
	}

	/**
	 * Sets the fl payment advice no view.
	 * 
	 * @param flPaymentAdviceNoView
	 *            the new fl payment advice no view
	 */
	public void setFlPaymentAdviceNoView(boolean flPaymentAdviceNoView) {
		this.flPaymentAdviceNoView = flPaymentAdviceNoView;
	}

	/**
	 * Gets the fl payment advice no var.
	 * 
	 * @return the fl payment advice no var
	 */
	public String getFlPaymentAdviceNoVar() {
		return flPaymentAdviceNoVar;
	}

	/**
	 * Sets the fl payment advice no var.
	 * 
	 * @param flPaymentAdviceNoVar
	 *            the new fl payment advice no var
	 */
	public void setFlPaymentAdviceNoVar(String flPaymentAdviceNoVar) {
		this.flPaymentAdviceNoVar = flPaymentAdviceNoVar;
	}

	/**
	 * Checks if is fl supplier delivery order no view.
	 * 
	 * @return true, if is fl supplier delivery order no view
	 */
	public boolean isFlSupplierDeliveryOrderNoView() {
		return flSupplierDeliveryOrderNoView;
	}

	/**
	 * Sets the fl supplier delivery order no view.
	 * 
	 * @param flSupplierDeliveryOrderNoView
	 *            the new fl supplier delivery order no view
	 */
	public void setFlSupplierDeliveryOrderNoView(
			boolean flSupplierDeliveryOrderNoView) {
		this.flSupplierDeliveryOrderNoView = flSupplierDeliveryOrderNoView;
	}

	/**
	 * Gets the fl supplier delivery order no var.
	 * 
	 * @return the fl supplier delivery order no var
	 */
	public String getFlSupplierDeliveryOrderNoVar() {
		return flSupplierDeliveryOrderNoVar;
	}

	/**
	 * Sets the fl supplier delivery order no var.
	 * 
	 * @param flSupplierDeliveryOrderNoVar
	 *            the new fl supplier delivery order no var
	 */
	public void setFlSupplierDeliveryOrderNoVar(
			String flSupplierDeliveryOrderNoVar) {
		this.flSupplierDeliveryOrderNoVar = flSupplierDeliveryOrderNoVar;
	}

	/**
	 * Checks if is sm mandatory gst registered status view.
	 * 
	 * @return true, if is sm mandatory gst registered status view
	 */
	public boolean isSmMandatoryGSTRegisteredStatusView() {
		return smMandatoryGSTRegisteredStatusView;
	}

	/**
	 * Sets the sm mandatory gst registered status view.
	 * 
	 * @param smMandatoryGSTRegisteredStatusView
	 *            the new sm mandatory gst registered status view
	 */
	public void setSmMandatoryGSTRegisteredStatusView(
			boolean smMandatoryGSTRegisteredStatusView) {
		this.smMandatoryGSTRegisteredStatusView = smMandatoryGSTRegisteredStatusView;
	}

	/**
	 * Checks if is sm gst registration no view.
	 * 
	 * @return true, if is sm gst registration no view
	 */
	public boolean isSmGSTRegistrationNoView() {
		return smGSTRegistrationNoView;
	}

	/**
	 * Sets the sm gst registration no view.
	 * 
	 * @param smGSTRegistrationNoView
	 *            the new sm gst registration no view
	 */
	public void setSmGSTRegistrationNoView(boolean smGSTRegistrationNoView) {
		this.smGSTRegistrationNoView = smGSTRegistrationNoView;
	}

	/**
	 * Checks if is sm gst registered status view.
	 * 
	 * @return true, if is sm gst registered status view
	 */
	public boolean isSmGSTRegisteredStatusView() {
		return smGSTRegisteredStatusView;
	}

	/**
	 * Sets the sm gst registered status view.
	 * 
	 * @param smGSTRegisteredStatusView
	 *            the new sm gst registered status view
	 */
	public void setSmGSTRegisteredStatusView(boolean smGSTRegisteredStatusView) {
		this.smGSTRegisteredStatusView = smGSTRegisteredStatusView;
	}

	/**
	 * Gets the sm gst registration no var.
	 * 
	 * @return the sm gst registration no var
	 */
	public String getSmGSTRegistrationNoVar() {
		return smGSTRegistrationNoVar;
	}

	/**
	 * Sets the sm gst registration no var.
	 * 
	 * @param smGSTRegistrationNoVar
	 *            the new sm gst registration no var
	 */
	public void setSmGSTRegistrationNoVar(String smGSTRegistrationNoVar) {
		this.smGSTRegistrationNoVar = smGSTRegistrationNoVar;
	}

	/**
	 * Gets the sm gst registered status var.
	 * 
	 * @return the sm gst registered status var
	 */
	public String getSmGSTRegisteredStatusVar() {
		return smGSTRegisteredStatusVar;
	}

	/**
	 * Sets the sm gst registered status var.
	 * 
	 * @param smGSTRegisteredStatusVar
	 *            the new sm gst registered status var
	 */
	public void setSmGSTRegisteredStatusVar(String smGSTRegisteredStatusVar) {
		this.smGSTRegisteredStatusVar = smGSTRegisteredStatusVar;
	}

	/**
	 * Checks if is sm gst effective date view.
	 * 
	 * @return true, if is sm gst effective date view
	 */
	public boolean isSmGSTEffectiveDateView() {
		return smGSTEffectiveDateView;
	}

	/**
	 * Sets the sm gst effective date view.
	 * 
	 * @param smGSTEffectiveDateView
	 *            the new sm gst effective date view
	 */
	public void setSmGSTEffectiveDateView(boolean smGSTEffectiveDateView) {
		this.smGSTEffectiveDateView = smGSTEffectiveDateView;
	}

	/**
	 * Checks if is ct group five view.
	 * 
	 * @return true, if is ct group five view
	 */
	public boolean isCtGroupFiveView() {
		return ctGroupFiveView;
	}

	/**
	 * Sets the ct group five view.
	 * 
	 * @param ctGroupFiveView
	 *            the new ct group five view
	 */
	public void setCtGroupFiveView(boolean ctGroupFiveView) {
		this.ctGroupFiveView = ctGroupFiveView;
	}

	/**
	 * Checks if is sm mandatory expected site visit date view.
	 * 
	 * @return true, if is sm mandatory expected site visit date view
	 */
	public boolean isSmMandatoryExpectedSiteVisitDateView() {
		return smMandatoryExpectedSiteVisitDateView;
	}

	/**
	 * Sets the sm mandatory expected site visit date view.
	 * 
	 * @param smMandatoryExpectedSiteVisitDateView
	 *            the new sm mandatory expected site visit date view
	 */
	public void setSmMandatoryExpectedSiteVisitDateView(
			boolean smMandatoryExpectedSiteVisitDateView) {
		this.smMandatoryExpectedSiteVisitDateView = smMandatoryExpectedSiteVisitDateView;
	}

	/**
	 * Checks if is sm mandatory actual site visit date view.
	 * 
	 * @return true, if is sm mandatory actual site visit date view
	 */
	public boolean isSmMandatoryActualSiteVisitDateView() {
		return smMandatoryActualSiteVisitDateView;
	}

	/**
	 * Sets the sm mandatory actual site visit date view.
	 * 
	 * @param smMandatoryActualSiteVisitDateView
	 *            the new sm mandatory actual site visit date view
	 */
	public void setSmMandatoryActualSiteVisitDateView(
			boolean smMandatoryActualSiteVisitDateView) {
		this.smMandatoryActualSiteVisitDateView = smMandatoryActualSiteVisitDateView;
	}

	/**
	 * Checks if is sm mandatory payment type view.
	 * 
	 * @return true, if is sm mandatory payment type view
	 */
	public boolean isSmMandatoryPaymentTypeView() {
		return smMandatoryPaymentTypeView;
	}

	/**
	 * Sets the sm mandatory payment type view.
	 * 
	 * @param smMandatoryPaymentTypeView
	 *            the new sm mandatory payment type view
	 */
	public void setSmMandatoryPaymentTypeView(boolean smMandatoryPaymentTypeView) {
		this.smMandatoryPaymentTypeView = smMandatoryPaymentTypeView;
	}

	/**
	 * Checks if is sm payment date view.
	 * 
	 * @return true, if is sm payment date view
	 */
	public boolean isSmPaymentDateView() {
		return smPaymentDateView;
	}

	/**
	 * Sets the sm payment date view.
	 * 
	 * @param smPaymentDateView
	 *            the new sm payment date view
	 */
	public void setSmPaymentDateView(boolean smPaymentDateView) {
		this.smPaymentDateView = smPaymentDateView;
	}

	/**
	 * Checks if is sm payment type two view.
	 * 
	 * @return true, if is sm payment type two view
	 */
	public boolean isSmPaymentTypeTwoView() {
		return smPaymentTypeTwoView;
	}

	/**
	 * Sets the sm payment type two view.
	 * 
	 * @param smPaymentTypeTwoView
	 *            the new sm payment type two view
	 */
	public void setSmPaymentTypeTwoView(boolean smPaymentTypeTwoView) {
		this.smPaymentTypeTwoView = smPaymentTypeTwoView;
	}

	/**
	 * Checks if is sm profile status view.
	 * 
	 * @return true, if is sm profile status view
	 */
	public boolean isSmProfileStatusView() {
		return smProfileStatusView;
	}

	/**
	 * Sets the sm profile status view.
	 * 
	 * @param smProfileStatusView
	 *            the new sm profile status view
	 */
	public void setSmProfileStatusView(boolean smProfileStatusView) {
		this.smProfileStatusView = smProfileStatusView;
	}

	/**
	 * Gets the sm profile status var.
	 * 
	 * @return the sm profile status var
	 */
	public String getSmProfileStatusVar() {
		return smProfileStatusVar;
	}

	/**
	 * Sets the sm profile status var.
	 * 
	 * @param smProfileStatusVar
	 *            the new sm profile status var
	 */
	public void setSmProfileStatusVar(String smProfileStatusVar) {
		this.smProfileStatusVar = smProfileStatusVar;
	}

	/**
	 * Checks if is sm application status three view.
	 * 
	 * @return true, if is sm application status three view
	 */
	public boolean isSmApplicationStatusThreeView() {
		return smApplicationStatusThreeView;
	}

	/**
	 * Sets the sm application status three view.
	 * 
	 * @param smApplicationStatusThreeView
	 *            the new sm application status three view
	 */
	public void setSmApplicationStatusThreeView(
			boolean smApplicationStatusThreeView) {
		this.smApplicationStatusThreeView = smApplicationStatusThreeView;
	}

	/**
	 * Checks if is sm mandatory all supplier type view.
	 * 
	 * @return true, if is sm mandatory all supplier type view
	 */
	public boolean isSmMandatoryAllSupplierTypeView() {
		return smMandatoryAllSupplierTypeView;
	}

	/**
	 * Sets the sm mandatory all supplier type view.
	 * 
	 * @param smMandatoryAllSupplierTypeView
	 *            the new sm mandatory all supplier type view
	 */
	public void setSmMandatoryAllSupplierTypeView(
			boolean smMandatoryAllSupplierTypeView) {
		this.smMandatoryAllSupplierTypeView = smMandatoryAllSupplierTypeView;
	}

	/**
	 * Checks if is sm all supplier type view.
	 * 
	 * @return true, if is sm all supplier type view
	 */
	public boolean isSmAllSupplierTypeView() {
		return smAllSupplierTypeView;
	}

	/**
	 * Sets the sm all supplier type view.
	 * 
	 * @param smAllSupplierTypeView
	 *            the new sm all supplier type view
	 */
	public void setSmAllSupplierTypeView(boolean smAllSupplierTypeView) {
		this.smAllSupplierTypeView = smAllSupplierTypeView;
	}

	/**
	 * Checks if is sm mof registered date view.
	 * 
	 * @return true, if is sm mof registered date view
	 */
	public boolean isSmMOFRegisteredDateView() {
		return smMOFRegisteredDateView;
	}

	/**
	 * Checks if is sm non mandatory receipt date view.
	 * 
	 * @return the smNonSmMandatoryReceiptDateView
	 */
	public boolean isSmNonMandatoryReceiptDateView() {
		return smNonMandatoryReceiptDateView;
	}

	/**
	 * Sets the sm non mandatory receipt date view.
	 * 
	 * @param smNonMandatoryReceiptDateView
	 *            the new sm non mandatory receipt date view
	 */
	public void setSmNonMandatoryReceiptDateView(
			boolean smNonMandatoryReceiptDateView) {
		this.smNonMandatoryReceiptDateView = smNonMandatoryReceiptDateView;
	}

	/**
	 * Sets the sm mof registered date view.
	 * 
	 * @param smMOFRegisteredDateView
	 *            the new sm mof registered date view
	 */
	public void setSmMOFRegisteredDateView(boolean smMOFRegisteredDateView) {
		this.smMOFRegisteredDateView = smMOFRegisteredDateView;
	}

	/**
	 * Checks if is sm consultant registration status view.
	 * 
	 * @return true, if is sm consultant registration status view
	 */
	public boolean isSmConsultantRegistrationStatusView() {
		return smConsultantRegistrationStatusView;
	}

	/**
	 * Sets the sm consultant registration status view.
	 * 
	 * @param smConsultantRegistrationStatusView
	 *            the new sm consultant registration status view
	 */
	public void setSmConsultantRegistrationStatusView(
			boolean smConsultantRegistrationStatusView) {
		this.smConsultantRegistrationStatusView = smConsultantRegistrationStatusView;
	}

	/**
	 * Checks if is sc mandatory process rpt quotation no view.
	 * 
	 * @return true, if is sc mandatory process rpt quotation no view
	 */
	public boolean isScMandatoryProcessRptQuotationNoView() {
		return scMandatoryProcessRptQuotationNoView;
	}

	/**
	 * Sets the sc mandatory process rpt quotation no view.
	 * 
	 * @param scMandatoryProcessRptQuotationNoView
	 *            the new sc mandatory process rpt quotation no view
	 */
	public void setScMandatoryProcessRptQuotationNoView(
			boolean scMandatoryProcessRptQuotationNoView) {
		this.scMandatoryProcessRptQuotationNoView = scMandatoryProcessRptQuotationNoView;
	}

	/**
	 * Checks if is sm both mandatory supplier type view.
	 * 
	 * @return true, if is sm both mandatory supplier type view
	 */
	public boolean isSmBothMandatorySupplierTypeView() {
		return smBothMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm both mandatory supplier type view.
	 * 
	 * @param smBothMandatorySupplierTypeView
	 *            the new sm both mandatory supplier type view
	 */
	public void setSmBothMandatorySupplierTypeView(
			boolean smBothMandatorySupplierTypeView) {
		this.smBothMandatorySupplierTypeView = smBothMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm mandatory ep admin ptj admin zone view.
	 * 
	 * @return true, if is sm mandatory ep admin ptj admin zone view
	 */
	public boolean isSmMandatoryEPAdminPTJAdminZoneView() {
		return smMandatoryEPAdminPTJAdminZoneView;
	}

	/**
	 * Sets the sm mandatory ep admin ptj admin zone view.
	 * 
	 * @param smMandatoryEPAdminPTJAdminZoneView
	 *            the new sm mandatory ep admin ptj admin zone view
	 */
	public void setSmMandatoryEPAdminPTJAdminZoneView(
			boolean smMandatoryEPAdminPTJAdminZoneView) {
		this.smMandatoryEPAdminPTJAdminZoneView = smMandatoryEPAdminPTJAdminZoneView;
	}

	/**
	 * Checks if is sm common zone view.
	 * 
	 * @return true, if is sm common zone view
	 */
	public boolean isSmCommonZoneView() {
		return smCommonZoneView;
	}

	/**
	 * Sets the sm common zone view.
	 * 
	 * @param smCommonZoneView
	 *            the new sm common zone view
	 */
	public void setSmCommonZoneView(boolean smCommonZoneView) {
		this.smCommonZoneView = smCommonZoneView;
	}

	/**
	 * Checks if is sm ep admin ptj admin zone view.
	 * 
	 * @return true, if is sm ep admin ptj admin zone view
	 */
	public boolean isSmEPAdminPTJAdminZoneView() {
		return smEPAdminPTJAdminZoneView;
	}

	/**
	 * Sets the sm ep admin ptj admin zone view.
	 * 
	 * @param smEPAdminPTJAdminZoneView
	 *            the new sm ep admin ptj admin zone view
	 */
	public void setSmEPAdminPTJAdminZoneView(boolean smEPAdminPTJAdminZoneView) {
		this.smEPAdminPTJAdminZoneView = smEPAdminPTJAdminZoneView;
	}

	/**
	 * Gets the fl supplier invoice no var.
	 * 
	 * @return the fl supplier invoice no var
	 */
	public String getFlSupplierInvoiceNoVar() {
		return flSupplierInvoiceNoVar;
	}

	/**
	 * Sets the fl supplier invoice no var.
	 * 
	 * @param flSupplierInvoiceNoVar
	 *            the new fl supplier invoice no var
	 */
	public void setFlSupplierInvoiceNoVar(String flSupplierInvoiceNoVar) {
		this.flSupplierInvoiceNoVar = flSupplierInvoiceNoVar;
	}

	/**
	 * Checks if is fl supplier invoice no view.
	 * 
	 * @return true, if is fl supplier invoice no view
	 */
	public boolean isFlSupplierInvoiceNoView() {
		return flSupplierInvoiceNoView;
	}

	/**
	 * Sets the fl supplier invoice no view.
	 * 
	 * @param flSupplierInvoiceNoView
	 *            the new fl supplier invoice no view
	 */
	public void setFlSupplierInvoiceNoView(boolean flSupplierInvoiceNoView) {
		this.flSupplierInvoiceNoView = flSupplierInvoiceNoView;
	}

	/**
	 * Checks if is ct status three view.
	 * 
	 * @return true, if is ct status three view
	 */
	public boolean isCtStatusThreeView() {
		return ctStatusThreeView;
	}

	/**
	 * Sets the ct status three view.
	 * 
	 * @param ctStatusThreeView
	 *            the new ct status three view
	 */
	public void setCtStatusThreeView(boolean ctStatusThreeView) {
		this.ctStatusThreeView = ctStatusThreeView;
	}

	/**
	 * Checks if is ct group four view.
	 * 
	 * @return true, if is ct group four view
	 */
	public boolean isCtGroupFourView() {
		return ctGroupFourView;
	}

	/**
	 * Sets the ct group four view.
	 * 
	 * @param ctGroupFourView
	 *            the new ct group four view
	 */
	public void setCtGroupFourView(boolean ctGroupFourView) {
		this.ctGroupFourView = ctGroupFourView;
	}

	/**
	 * Checks if is sm zone label view.
	 * 
	 * @return true, if is sm zone label view
	 */
	public boolean isSmZoneLabelView() {
		return smZoneLabelView;
	}

	/**
	 * Sets the sm zone label view.
	 * 
	 * @param smZoneLabelView
	 *            the new sm zone label view
	 */
	public void setSmZoneLabelView(boolean smZoneLabelView) {
		this.smZoneLabelView = smZoneLabelView;
	}

	/**
	 * Checks if is generate button view.
	 * 
	 * @return true, if is generate button view
	 */
	public boolean isGenerateButtonView() {
		return generateButtonView;
	}

	/**
	 * Sets the generate button view.
	 * 
	 * @param generateButtonView
	 *            the new generate button view
	 */
	public void setGenerateButtonView(boolean generateButtonView) {
		this.generateButtonView = generateButtonView;
	}

	/**
	 * Checks if is fl group twenty two view.
	 * 
	 * @return true, if is fl group twenty two view
	 */
	public boolean isFlGroupTwentyTwoView() {
		return flGroupTwentyTwoView;
	}

	/**
	 * Sets the fl group twenty two view.
	 * 
	 * @param flGroupTwentyTwoView
	 *            the new fl group twenty two view
	 */
	public void setFlGroupTwentyTwoView(boolean flGroupTwentyTwoView) {
		this.flGroupTwentyTwoView = flGroupTwentyTwoView;
	}

	/**
	 * Checks if is fl group twenty one view.
	 * 
	 * @return true, if is fl group twenty one view
	 */
	public boolean isFlGroupTwentyOneView() {
		return flGroupTwentyOneView;
	}

	/**
	 * Sets the fl group twenty one view.
	 * 
	 * @param flGroupTwentyOneView
	 *            the new fl group twenty one view
	 */
	public void setFlGroupTwentyOneView(boolean flGroupTwentyOneView) {
		this.flGroupTwentyOneView = flGroupTwentyOneView;
	}

	/**
	 * Checks if is fl group twenty view.
	 * 
	 * @return true, if is fl group twenty view
	 */
	public boolean isFlGroupTwentyView() {
		return flGroupTwentyView;
	}

	/**
	 * Sets the fl group twenty view.
	 * 
	 * @param flGroupTwentyView
	 *            the new fl group twenty view
	 */
	public void setFlGroupTwentyView(boolean flGroupTwentyView) {
		this.flGroupTwentyView = flGroupTwentyView;
	}

	/**
	 * Checks if is fl group nineteen view.
	 * 
	 * @return true, if is fl group nineteen view
	 */
	public boolean isFlGroupNineteenView() {
		return flGroupNineteenView;
	}

	/**
	 * Sets the fl group nineteen view.
	 * 
	 * @param flGroupNineteenView
	 *            the new fl group nineteen view
	 */
	public void setFlGroupNineteenView(boolean flGroupNineteenView) {
		this.flGroupNineteenView = flGroupNineteenView;
	}

	/**
	 * Checks if is fl group eighteen view.
	 * 
	 * @return true, if is fl group eighteen view
	 */
	public boolean isFlGroupEighteenView() {
		return flGroupEighteenView;
	}

	/**
	 * Sets the fl group eighteen view.
	 * 
	 * @param flGroupEighteenView
	 *            the new fl group eighteen view
	 */
	public void setFlGroupEighteenView(boolean flGroupEighteenView) {
		this.flGroupEighteenView = flGroupEighteenView;
	}

	/**
	 * Checks if is fl stop instruction no view.
	 * 
	 * @return true, if is fl stop instruction no view
	 */
	public boolean isFlStopInstructionNoView() {
		return flStopInstructionNoView;
	}

	/**
	 * Sets the fl stop instruction no view.
	 * 
	 * @param flStopInstructionNoView
	 *            the new fl stop instruction no view
	 */
	public void setFlStopInstructionNoView(boolean flStopInstructionNoView) {
		this.flStopInstructionNoView = flStopInstructionNoView;
	}

	/**
	 * Gets the fl stop instruction no var.
	 * 
	 * @return the fl stop instruction no var
	 */
	public String getFlStopInstructionNoVar() {
		return flStopInstructionNoVar;
	}

	/**
	 * Sets the fl stop instruction no var.
	 * 
	 * @param flStopInstructionNoVar
	 *            the new fl stop instruction no var
	 */
	public void setFlStopInstructionNoVar(String flStopInstructionNoVar) {
		this.flStopInstructionNoVar = flStopInstructionNoVar;
	}

	/**
	 * Checks if is fl mandatory ministry view.
	 * 
	 * @return true, if is fl mandatory ministry view
	 */
	public boolean isFlMandatoryMinistryView() {
		return flMandatoryMinistryView;
	}

	/**
	 * Sets the fl mandatory ministry view.
	 * 
	 * @param flMandatoryMinistryView
	 *            the new fl mandatory ministry view
	 */
	public void setFlMandatoryMinistryView(boolean flMandatoryMinistryView) {
		this.flMandatoryMinistryView = flMandatoryMinistryView;
	}

	/**
	 * Checks if is fl mandatory ptj view.
	 * 
	 * @return true, if is fl mandatory ptj view
	 */
	public boolean isFlMandatoryPtjView() {
		return flMandatoryPtjView;
	}

	/**
	 * Sets the fl mandatory ptj view.
	 * 
	 * @param flMandatoryPtjView
	 *            the new fl mandatory ptj view
	 */
	public void setFlMandatoryPtjView(boolean flMandatoryPtjView) {
		this.flMandatoryPtjView = flMandatoryPtjView;
	}

	/**
	 * Checks if is sm extra two column two view.
	 * 
	 * @return true, if is sm extra two column two view
	 */
	public boolean isSmExtraTwoColumnTwoView() {
		return smExtraTwoColumnTwoView;
	}

	/**
	 * Sets the sm extra two column two view.
	 * 
	 * @param smExtraTwoColumnTwoView
	 *            the new sm extra two column two view
	 */
	public void setSmExtraTwoColumnTwoView(boolean smExtraTwoColumnTwoView) {
		this.smExtraTwoColumnTwoView = smExtraTwoColumnTwoView;
	}

	/**
	 * Checks if is sm mandatory for date view.
	 * 
	 * @return the smMandatoryForDateView
	 */
	public boolean isSmMandatoryForDateView() {
		return smMandatoryForDateView;
	}

	/**
	 * Sets the sm mandatory for date view.
	 * 
	 * @param smMandatoryForDateView
	 *            the smMandatoryForDateView to set
	 */
	public void setSmMandatoryForDateView(boolean smMandatoryForDateView) {
		this.smMandatoryForDateView = smMandatoryForDateView;
	}

	/**
	 * Checks if is sm approval date view.
	 * 
	 * @return true, if is sm approval date view
	 */
	public boolean isSmApprovalDateView() {
		return smApprovalDateView;
	}

	/**
	 * Sets the sm approval date view.
	 * 
	 * @param smApprovalDateView
	 *            the new sm approval date view
	 */
	public void setSmApprovalDateView(boolean smApprovalDateView) {
		this.smApprovalDateView = smApprovalDateView;
	}

	/**
	 * Checks if is sm application type two view.
	 * 
	 * @return true, if is sm application type two view
	 */
	public boolean isSmApplicationTypeTwoView() {
		return smApplicationTypeTwoView;
	}

	/**
	 * Sets the sm application type two view.
	 * 
	 * @param smApplicationTypeTwoView
	 *            the new sm application type two view
	 */
	public void setSmApplicationTypeTwoView(boolean smApplicationTypeTwoView) {
		this.smApplicationTypeTwoView = smApplicationTypeTwoView;
	}

	/**
	 * Checks if is sm year from view.
	 * 
	 * @return true, if is sm year from view
	 */
	public boolean isSmYearFromView() {
		return smYearFromView;
	}

	/**
	 * Checks if is sm from date view.
	 * 
	 * @return the smFromDateView
	 */
	public boolean isSmFromDateView() {
		return smFromDateView;
	}

	/**
	 * Sets the sm from date view.
	 * 
	 * @param smFromDateView
	 *            the smFromDateView to set
	 */
	public void setSmFromDateView(boolean smFromDateView) {
		this.smFromDateView = smFromDateView;
	}

	/**
	 * Sets the sm year from view.
	 * 
	 * @param smYearFromView
	 *            the new sm year from view
	 */
	public void setSmYearFromView(boolean smYearFromView) {
		this.smYearFromView = smYearFromView;
	}

	/**
	 * Checks if is sm threshold year from view.
	 * 
	 * @return true, if is sm threshold year from view
	 */
	public boolean isSmThresholdYearFromView() {
		return smThresholdYearFromView;
	}

	/**
	 * Sets the sm threshold year from view.
	 * 
	 * @param smThresholdYearFromView
	 *            the new sm threshold year from view
	 */
	public void setSmThresholdYearFromView(boolean smThresholdYearFromView) {
		this.smThresholdYearFromView = smThresholdYearFromView;
	}

	/**
	 * Checks if is sm request expiry date view.
	 * 
	 * @return true, if is sm request expiry date view
	 */
	public boolean isSmRequestExpiryDateView() {
		return smRequestExpiryDateView;
	}

	/**
	 * Sets the sm request expiry date view.
	 * 
	 * @param smRequestExpiryDateView
	 *            the new sm request expiry date view
	 */
	public void setSmRequestExpiryDateView(boolean smRequestExpiryDateView) {
		this.smRequestExpiryDateView = smRequestExpiryDateView;
	}

	/**
	 * Gets the sm request expiry date var.
	 * 
	 * @return the sm request expiry date var
	 */
	public String getSmRequestExpiryDateVar() {
		return smRequestExpiryDateVar;
	}

	/**
	 * Sets the sm request expiry date var.
	 * 
	 * @param smRequestExpiryDateVar
	 *            the new sm request expiry date var
	 */
	public void setSmRequestExpiryDateVar(String smRequestExpiryDateVar) {
		this.smRequestExpiryDateVar = smRequestExpiryDateVar;
	}

	/**
	 * Checks if is sm user name view.
	 * 
	 * @return true, if is sm user name view
	 */
	public boolean isSmUserNameView() {
		return smUserNameView;
	}

	/**
	 * Sets the sm user name view.
	 * 
	 * @param smUserNameView
	 *            the new sm user name view
	 */
	public void setSmUserNameView(boolean smUserNameView) {
		this.smUserNameView = smUserNameView;
	}

	/**
	 * Gets the sm user name var.
	 * 
	 * @return the sm user name var
	 */
	public String getSmUserNameVar() {
		return smUserNameVar;
	}

	/**
	 * Sets the sm user name var.
	 * 
	 * @param smUserNameVar
	 *            the new sm user name var
	 */
	public void setSmUserNameVar(String smUserNameVar) {
		this.smUserNameVar = smUserNameVar;
	}

	/**
	 * Gets the sm threshold from year var.
	 * 
	 * @return the sm threshold from year var
	 */
	public String getSmThresholdFromYearVar() {
		return SmThresholdFromYearVar;
	}

	/**
	 * Sets the sm threshold from year var.
	 * 
	 * @param smThresholdFromYearVar
	 *            the new sm threshold from year var
	 */
	public void setSmThresholdFromYearVar(String smThresholdFromYearVar) {
		SmThresholdFromYearVar = smThresholdFromYearVar;
	}

	/**
	 * Gets the sm threshold to year var.
	 * 
	 * @return the sm threshold to year var
	 */
	public String getSmThresholdToYearVar() {
		return SmThresholdToYearVar;
	}

	/**
	 * Sets the sm threshold to year var.
	 * 
	 * @param smThresholdToYearVar
	 *            the new sm threshold to year var
	 */
	public void setSmThresholdToYearVar(String smThresholdToYearVar) {
		SmThresholdToYearVar = smThresholdToYearVar;
	}

	/**
	 * Checks if is sm from and to year view.
	 * 
	 * @return true, if is sm from and to year view
	 */
	public boolean isSmFromAndToYearView() {
		return smFromAndToYearView;
	}

	/**
	 * Sets the sm from and to year view.
	 * 
	 * @param smFromAndToYearView
	 *            the new sm from and to year view
	 */
	public void setSmFromAndToYearView(boolean smFromAndToYearView) {
		this.smFromAndToYearView = smFromAndToYearView;
	}

	/**
	 * Checks if is sm company name dropdown view.
	 * 
	 * @return true, if is sm company name dropdown view
	 */
	public boolean isSmCompanyNameDropdownView() {
		return smCompanyNameDropdownView;
	}

	/**
	 * Sets the sm company name dropdown view.
	 * 
	 * @param smCompanyNameDropdownView
	 *            the new sm company name dropdown view
	 */
	public void setSmCompanyNameDropdownView(boolean smCompanyNameDropdownView) {
		this.smCompanyNameDropdownView = smCompanyNameDropdownView;
	}

	/**
	 * Checks if is sm supplier view.
	 * 
	 * @return true, if is sm supplier view
	 */
	public boolean isSmSupplierView() {
		return smSupplierView;
	}

	/**
	 * Sets the sm supplier view.
	 * 
	 * @param smSupplierView
	 *            the new sm supplier view
	 */
	public void setSmSupplierView(boolean smSupplierView) {
		this.smSupplierView = smSupplierView;
	}

	/**
	 * Checks if is sm from month view.
	 * 
	 * @return true, if is sm from month view
	 */
	public boolean isSmFromMonthView() {
		return smFromMonthView;
	}

	/**
	 * Sets the sm from month view.
	 * 
	 * @param smFromMonthView
	 *            the new sm from month view
	 */
	public void setSmFromMonthView(boolean smFromMonthView) {
		this.smFromMonthView = smFromMonthView;
	}

	/**
	 * Checks if is sm to month view.
	 * 
	 * @return true, if is sm to month view
	 */
	public boolean isSmToMonthView() {
		return smToMonthView;
	}

	/**
	 * Sets the sm to month view.
	 * 
	 * @param smToMonthView
	 *            the new sm to month view
	 */
	public void setSmToMonthView(boolean smToMonthView) {
		this.smToMonthView = smToMonthView;
	}

	/**
	 * Gets the sm from month var.
	 * 
	 * @return the sm from month var
	 */
	public String getSmFromMonthVar() {
		return smFromMonthVar;
	}

	/**
	 * Sets the sm from month var.
	 * 
	 * @param smFromMonthVar
	 *            the new sm from month var
	 */
	public void setSmFromMonthVar(String smFromMonthVar) {
		this.smFromMonthVar = smFromMonthVar;
	}

	/**
	 * Gets the sm to month var.
	 * 
	 * @return the sm to month var
	 */
	public String getSmToMonthVar() {
		return smToMonthVar;
	}

	/**
	 * Sets the sm to month var.
	 * 
	 * @param smToMonthVar
	 *            the new sm to month var
	 */
	public void setSmToMonthVar(String smToMonthVar) {
		this.smToMonthVar = smToMonthVar;
	}

	/**
	 * Checks if is sm mandatory registered report type date view.
	 * 
	 * @return true, if is sm mandatory registered report type date view
	 */
	public boolean isSmMandatoryRegisteredReportTypeDateView() {
		return smMandatoryRegisteredReportTypeDateView;
	}

	/**
	 * Sets the sm mandatory registered report type date view.
	 * 
	 * @param smMandatoryRegisteredReportTypeDateView
	 *            the new sm mandatory registered report type date view
	 */
	public void setSmMandatoryRegisteredReportTypeDateView(
			boolean smMandatoryRegisteredReportTypeDateView) {
		this.smMandatoryRegisteredReportTypeDateView = smMandatoryRegisteredReportTypeDateView;
	}

	/**
	 * Checks if is sm mandatory recieved report type view.
	 * 
	 * @return true, if is sm mandatory recieved report type view
	 */
	public boolean isSmMandatoryRecievedReportTypeView() {
		return smMandatoryRecievedReportTypeView;
	}

	/**
	 * Sets the sm mandatory recieved report type view.
	 * 
	 * @param smMandatoryRecievedReportTypeView
	 *            the new sm mandatory recieved report type view
	 */
	public void setSmMandatoryRecievedReportTypeView(
			boolean smMandatoryRecievedReportTypeView) {
		this.smMandatoryRecievedReportTypeView = smMandatoryRecievedReportTypeView;
	}

	/**
	 * Checks if is sm mandatory approved report type view.
	 * 
	 * @return true, if is sm mandatory approved report type view
	 */
	public boolean isSmMandatoryApprovedReportTypeView() {
		return smMandatoryApprovedReportTypeView;
	}

	/**
	 * Sets the sm mandatory approved report type view.
	 * 
	 * @param smMandatoryApprovedReportTypeView
	 *            the new sm mandatory approved report type view
	 */
	public void setSmMandatoryApprovedReportTypeView(
			boolean smMandatoryApprovedReportTypeView) {
		this.smMandatoryApprovedReportTypeView = smMandatoryApprovedReportTypeView;
	}

	/**
	 * Checks if is sm mandatory rejected report type view.
	 * 
	 * @return true, if is sm mandatory rejected report type view
	 */
	public boolean isSmMandatoryRejectedReportTypeView() {
		return smMandatoryRejectedReportTypeView;
	}

	/**
	 * Sets the sm mandatory rejected report type view.
	 * 
	 * @param smMandatoryRejectedReportTypeView
	 *            the new sm mandatory rejected report type view
	 */
	public void setSmMandatoryRejectedReportTypeView(
			boolean smMandatoryRejectedReportTypeView) {
		this.smMandatoryRejectedReportTypeView = smMandatoryRejectedReportTypeView;
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
	 * Checks if is sm application received date view.
	 * 
	 * @return true, if is sm application received date view
	 */
	public boolean isSmApplicationReceivedDateView() {
		return smApplicationReceivedDateView;
	}

	/**
	 * Sets the sm application received date view.
	 * 
	 * @param smApplicationReceivedDateView
	 *            the new sm application received date view
	 */
	public void setSmApplicationReceivedDateView(
			boolean smApplicationReceivedDateView) {
		this.smApplicationReceivedDateView = smApplicationReceivedDateView;
	}

	/**
	 * Checks if is sm both supplier type view.
	 * 
	 * @return true, if is sm both supplier type view
	 */
	public boolean isSmBothSupplierTypeView() {
		return smBothSupplierTypeView;
	}

	/**
	 * Sets the sm both supplier type view.
	 * 
	 * @param smBothSupplierTypeView
	 *            the new sm both supplier type view
	 */
	public void setSmBothSupplierTypeView(boolean smBothSupplierTypeView) {
		this.smBothSupplierTypeView = smBothSupplierTypeView;
	}

	/**
	 * Checks if is sm report type two view.
	 * 
	 * @return true, if is sm report type two view
	 */
	public boolean isSmReportTypeTwoView() {
		return smReportTypeTwoView;
	}

	/**
	 * Sets the sm report type two view.
	 * 
	 * @param smReportTypeTwoView
	 *            the new sm report type two view
	 */
	public void setSmReportTypeTwoView(boolean smReportTypeTwoView) {
		this.smReportTypeTwoView = smReportTypeTwoView;
	}

	/**
	 * Checks if is sm processing officer name view.
	 * 
	 * @return true, if is sm processing officer name view
	 */
	public boolean isSmProcessingOfficerNameView() {
		return smProcessingOfficerNameView;
	}

	/**
	 * Sets the sm processing officer name view.
	 * 
	 * @param smProcessingOfficerNameView
	 *            the new sm processing officer name view
	 */
	public void setSmProcessingOfficerNameView(
			boolean smProcessingOfficerNameView) {
		this.smProcessingOfficerNameView = smProcessingOfficerNameView;
	}

	/**
	 * Checks if is sm receipt no view.
	 * 
	 * @return true, if is sm receipt no view
	 */
	public boolean isSmReceiptNoView() {
		return smReceiptNoView;
	}

	/**
	 * Sets the sm receipt no view.
	 * 
	 * @param smReceiptNoView
	 *            the new sm receipt no view
	 */
	public void setSmReceiptNoView(boolean smReceiptNoView) {
		this.smReceiptNoView = smReceiptNoView;
	}

	/**
	 * Checks if is sm processing type view.
	 * 
	 * @return true, if is sm processing type view
	 */
	public boolean isSmProcessingTypeView() {
		return smProcessingTypeView;
	}

	/**
	 * Sets the sm processing type view.
	 * 
	 * @param smProcessingTypeView
	 *            the new sm processing type view
	 */
	public void setSmProcessingTypeView(boolean smProcessingTypeView) {
		this.smProcessingTypeView = smProcessingTypeView;
	}

	/**
	 * Checks if is sm payment method view.
	 * 
	 * @return true, if is sm payment method view
	 */
	public boolean isSmPaymentMethodView() {
		return smPaymentMethodView;
	}

	/**
	 * Sets the sm payment method view.
	 * 
	 * @param smPaymentMethodView
	 *            the new sm payment method view
	 */
	public void setSmPaymentMethodView(boolean smPaymentMethodView) {
		this.smPaymentMethodView = smPaymentMethodView;
	}

	/**
	 * Gets the sm processing officer name var.
	 * 
	 * @return the sm processing officer name var
	 */
	public String getSmProcessingOfficerNameVar() {
		return smProcessingOfficerNameVar;
	}

	/**
	 * Sets the sm processing officer name var.
	 * 
	 * @param smProcessingOfficerNameVar
	 *            the new sm processing officer name var
	 */
	public void setSmProcessingOfficerNameVar(String smProcessingOfficerNameVar) {
		this.smProcessingOfficerNameVar = smProcessingOfficerNameVar;
	}

	/**
	 * Gets the sm report type var.
	 * 
	 * @return the sm report type var
	 */
	public String getSmReportTypeVar() {
		return smReportTypeVar;
	}

	/**
	 * Sets the sm report type var.
	 * 
	 * @param smReportTypeVar
	 *            the new sm report type var
	 */
	public void setSmReportTypeVar(String smReportTypeVar) {
		this.smReportTypeVar = smReportTypeVar;
	}

	/**
	 * Gets the sm receipt no var.
	 * 
	 * @return the sm receipt no var
	 */
	public String getSmReceiptNoVar() {
		return smReceiptNoVar;
	}

	/**
	 * Sets the sm receipt no var.
	 * 
	 * @param smReceiptNoVar
	 *            the new sm receipt no var
	 */
	public void setSmReceiptNoVar(String smReceiptNoVar) {
		this.smReceiptNoVar = smReceiptNoVar;
	}

	/**
	 * Gets the sm processing type var.
	 * 
	 * @return the sm processing type var
	 */
	public String getSmProcessingTypeVar() {
		return smProcessingTypeVar;
	}

	/**
	 * Sets the sm processing type var.
	 * 
	 * @param smProcessingTypeVar
	 *            the new sm processing type var
	 */
	public void setSmProcessingTypeVar(String smProcessingTypeVar) {
		this.smProcessingTypeVar = smProcessingTypeVar;
	}

	/**
	 * Gets the sm payment method var.
	 * 
	 * @return the sm payment method var
	 */
	public String getSmPaymentMethodVar() {
		return smPaymentMethodVar;
	}

	/**
	 * Sets the sm payment method var.
	 * 
	 * @param smPaymentMethodVar
	 *            the new sm payment method var
	 */
	public void setSmPaymentMethodVar(String smPaymentMethodVar) {
		this.smPaymentMethodVar = smPaymentMethodVar;
	}

	/**
	 * Checks if is cm madatory indicator view.
	 * 
	 * @return true, if is cm madatory indicator view
	 */
	public boolean isCmMadatoryIndicatorView() {
		return cmMadatoryIndicatorView;
	}

	/**
	 * Sets the cm madatory indicator view.
	 * 
	 * @param cmMadatoryIndicatorView
	 *            the new cm madatory indicator view
	 */
	public void setCmMadatoryIndicatorView(boolean cmMadatoryIndicatorView) {
		this.cmMadatoryIndicatorView = cmMadatoryIndicatorView;
	}

	/**
	 * Checks if is sm act submis all date view.
	 * 
	 * @return true, if is sm act submis all date view
	 */
	public boolean isSmActSubmisAllDateView() {
		return smActSubmisAllDateView;
	}

	/**
	 * Sets the sm act submis all date view.
	 * 
	 * @param smActSubmisAllDateView
	 *            the new sm act submis all date view
	 */
	public void setSmActSubmisAllDateView(boolean smActSubmisAllDateView) {
		this.smActSubmisAllDateView = smActSubmisAllDateView;
	}

	/**
	 * Checks if is sm active submissive from date view.
	 * 
	 * @return true, if is sm active submissive from date view
	 */
	public boolean isSmActiveSubmissiveFromDateView() {
		return smActiveSubmissiveFromDateView;
	}

	/**
	 * Sets the sm active submissive from date view.
	 * 
	 * @param smActiveSubmissiveFromDateView
	 *            the new sm active submissive from date view
	 */
	public void setSmActiveSubmissiveFromDateView(
			boolean smActiveSubmissiveFromDateView) {
		this.smActiveSubmissiveFromDateView = smActiveSubmissiveFromDateView;
	}

	/**
	 * Checks if is sm mof reg no two view.
	 * 
	 * @return true, if is sm mof reg no two view
	 */
	public boolean isSmMofRegNoTwoView() {
		return smMofRegNoTwoView;
	}

	/**
	 * Sets the sm mof reg no two view.
	 * 
	 * @param smMofRegNoTwoView
	 *            the new sm mof reg no two view
	 */
	public void setSmMofRegNoTwoView(boolean smMofRegNoTwoView) {
		this.smMofRegNoTwoView = smMofRegNoTwoView;
	}

	/**
	 * Gets the sm supplier name var.
	 * 
	 * @return the smSupplierNameVar
	 */
	public String getSmSupplierNameVar() {
		return smSupplierNameVar;
	}

	/**
	 * Sets the sm supplier name var.
	 * 
	 * @param smSupplierNameVar
	 *            the smSupplierNameVar to set
	 */
	public void setSmSupplierNameVar(String smSupplierNameVar) {
		this.smSupplierNameVar = smSupplierNameVar;
	}

	/**
	 * Checks if is sm registration type two view.
	 * 
	 * @return true, if is sm registration type two view
	 */
	public boolean isSmRegistrationTypeTwoView() {
		return smRegistrationTypeTwoView;
	}

	/**
	 * Sets the sm registration type two view.
	 * 
	 * @param smRegistrationTypeTwoView
	 *            the new sm registration type two view
	 */
	public void setSmRegistrationTypeTwoView(boolean smRegistrationTypeTwoView) {
		this.smRegistrationTypeTwoView = smRegistrationTypeTwoView;
	}

	/**
	 * Checks if is sm mandatory application status three view.
	 * 
	 * @return true, if is sm mandatory application status three view
	 */
	public boolean isSmMandatoryApplicationStatusThreeView() {
		return smMandatoryApplicationStatusThreeView;
	}

	/**
	 * Sets the sm mandatory application status three view.
	 * 
	 * @param smMandatoryApplicationStatusThreeView
	 *            the new sm mandatory application status three view
	 */
	public void setSmMandatoryApplicationStatusThreeView(
			boolean smMandatoryApplicationStatusThreeView) {
		this.smMandatoryApplicationStatusThreeView = smMandatoryApplicationStatusThreeView;
	}

	/**
	 * Checks if is sm mandatory application status two view.
	 * 
	 * @return true, if is sm mandatory application status two view
	 */
	public boolean isSmMandatoryApplicationStatusTwoView() {
		return smMandatoryApplicationStatusTwoView;
	}

	/**
	 * Sets the sm mandatory application status two view.
	 * 
	 * @param smMandatoryApplicationStatusTwoView
	 *            the new sm mandatory application status two view
	 */
	public void setSmMandatoryApplicationStatusTwoView(
			boolean smMandatoryApplicationStatusTwoView) {
		this.smMandatoryApplicationStatusTwoView = smMandatoryApplicationStatusTwoView;
	}

	/**
	 * Checks if is sm identfication type view.
	 * 
	 * @return true, if is sm identfication type view
	 */
	public boolean isSmIdentficationTypeView() {
		return smIdentficationTypeView;
	}

	/**
	 * Sets the sm identfication type view.
	 * 
	 * @param smIdentficationTypeView
	 *            the new sm identfication type view
	 */
	public void setSmIdentficationTypeView(boolean smIdentficationTypeView) {
		this.smIdentficationTypeView = smIdentficationTypeView;
	}

	/**
	 * Checks if is sm identification number view.
	 * 
	 * @return true, if is sm identification number view
	 */
	public boolean isSmIdentificationNumberView() {
		return smIdentificationNumberView;
	}

	/**
	 * Sets the sm identification number view.
	 * 
	 * @param smIdentificationNumberView
	 *            the new sm identification number view
	 */
	public void setSmIdentificationNumberView(boolean smIdentificationNumberView) {
		this.smIdentificationNumberView = smIdentificationNumberView;
	}

	/**
	 * Checks if is sm cancellation by view.
	 * 
	 * @return true, if is sm cancellation by view
	 */
	public boolean isSmCancellationByView() {
		return smCancellationByView;
	}

	/**
	 * Sets the sm cancellation by view.
	 * 
	 * @param smCancellationByView
	 *            the new sm cancellation by view
	 */
	public void setSmCancellationByView(boolean smCancellationByView) {
		this.smCancellationByView = smCancellationByView;
	}

	/**
	 * Gets the sm identification type var.
	 * 
	 * @return the sm identification type var
	 */
	public String getSmIdentificationTypeVar() {
		return smIdentificationTypeVar;
	}

	/**
	 * Sets the sm identification type var.
	 * 
	 * @param smIdentificationTypeVar
	 *            the new sm identification type var
	 */
	public void setSmIdentificationTypeVar(String smIdentificationTypeVar) {
		this.smIdentificationTypeVar = smIdentificationTypeVar;
	}

	/**
	 * Gets the sm identification number var.
	 * 
	 * @return the sm identification number var
	 */
	public String getSmIdentificationNumberVar() {
		return smIdentificationNumberVar;
	}

	/**
	 * Sets the sm identification number var.
	 * 
	 * @param smIdentificationNumberVar
	 *            the new sm identification number var
	 */
	public void setSmIdentificationNumberVar(String smIdentificationNumberVar) {
		this.smIdentificationNumberVar = smIdentificationNumberVar;
	}

	/**
	 * Gets the sm cancellation by var.
	 * 
	 * @return the sm cancellation by var
	 */
	public String getSmCancellationByVar() {
		return smCancellationByVar;
	}

	/**
	 * Sets the sm cancellation by var.
	 * 
	 * @param smCancellationByVar
	 *            the new sm cancellation by var
	 */
	public void setSmCancellationByVar(String smCancellationByVar) {
		this.smCancellationByVar = smCancellationByVar;
	}

	/**
	 * Checks if is sm personnel role view.
	 * 
	 * @return true, if is sm personnel role view
	 */
	public boolean isSmPersonnelRoleView() {
		return smPersonnelRoleView;
	}

	/**
	 * Sets the sm personnel role view.
	 * 
	 * @param smPersonnelRoleView
	 *            the new sm personnel role view
	 */
	public void setSmPersonnelRoleView(boolean smPersonnelRoleView) {
		this.smPersonnelRoleView = smPersonnelRoleView;
	}

	/**
	 * Gets the sm personnel role var.
	 * 
	 * @return the sm personnel role var
	 */
	public String getSmPersonnelRoleVar() {
		return smPersonnelRoleVar;
	}

	/**
	 * Sets the sm personnel role var.
	 * 
	 * @param smPersonnelRoleVar
	 *            the new sm personnel role var
	 */
	public void setSmPersonnelRoleVar(String smPersonnelRoleVar) {
		this.smPersonnelRoleVar = smPersonnelRoleVar;
	}

	/**
	 * Checks if is sm mandatory mof reg no from view.
	 * 
	 * @return true, if is sm mandatory mof reg no from view
	 */
	public boolean isSmMandatoryMofRegNoFromView() {
		return smMandatoryMofRegNoFromView;
	}

	/**
	 * Sets the sm mandatory mof reg no from view.
	 * 
	 * @param smMandatoryMofRegNoFromView
	 *            the new sm mandatory mof reg no from view
	 */
	public void setSmMandatoryMofRegNoFromView(
			boolean smMandatoryMofRegNoFromView) {
		this.smMandatoryMofRegNoFromView = smMandatoryMofRegNoFromView;
	}

	/**
	 * Checks if is sm mandatory mof reg no to view.
	 * 
	 * @return true, if is sm mandatory mof reg no to view
	 */
	public boolean isSmMandatoryMofRegNoToView() {
		return smMandatoryMofRegNoToView;
	}

	/**
	 * Sets the sm mandatory mof reg no to view.
	 * 
	 * @param smMandatoryMofRegNoToView
	 *            the new sm mandatory mof reg no to view
	 */
	public void setSmMandatoryMofRegNoToView(boolean smMandatoryMofRegNoToView) {
		this.smMandatoryMofRegNoToView = smMandatoryMofRegNoToView;
	}

	/**
	 * Checks if is sm mof reg no from view.
	 * 
	 * @return true, if is sm mof reg no from view
	 */
	public boolean isSmMofRegNoFromView() {
		return smMofRegNoFromView;
	}

	/**
	 * Sets the sm mof reg no from view.
	 * 
	 * @param smMofRegNoFromView
	 *            the new sm mof reg no from view
	 */
	public void setSmMofRegNoFromView(boolean smMofRegNoFromView) {
		this.smMofRegNoFromView = smMofRegNoFromView;
	}

	/**
	 * Checks if is sm mof reg no to view.
	 * 
	 * @return true, if is sm mof reg no to view
	 */
	public boolean isSmMofRegNoToView() {
		return smMofRegNoToView;
	}

	/**
	 * Sets the sm mof reg no to view.
	 * 
	 * @param smMofRegNoToView
	 *            the new sm mof reg no to view
	 */
	public void setSmMofRegNoToView(boolean smMofRegNoToView) {
		this.smMofRegNoToView = smMofRegNoToView;
	}

	/**
	 * Checks if is sm paid up capital frm view.
	 * 
	 * @return true, if is sm paid up capital frm view
	 */
	public boolean isSmPaidUpCapitalFrmView() {
		return smPaidUpCapitalFrmView;
	}

	/**
	 * Sets the sm paid up capital frm view.
	 * 
	 * @param smPaidUpCapitalFrmView
	 *            the new sm paid up capital frm view
	 */
	public void setSmPaidUpCapitalFrmView(boolean smPaidUpCapitalFrmView) {
		this.smPaidUpCapitalFrmView = smPaidUpCapitalFrmView;
	}

	/**
	 * Checks if is sm paid up capital to view.
	 * 
	 * @return true, if is sm paid up capital to view
	 */
	public boolean isSmPaidUpCapitalToView() {
		return smPaidUpCapitalToView;
	}

	/**
	 * Sets the sm paid up capital to view.
	 * 
	 * @param smPaidUpCapitalToView
	 *            the new sm paid up capital to view
	 */
	public void setSmPaidUpCapitalToView(boolean smPaidUpCapitalToView) {
		this.smPaidUpCapitalToView = smPaidUpCapitalToView;
	}

	/**
	 * Checks if is sm paid up capital naccum view.
	 * 
	 * @return true, if is sm paid up capital naccum view
	 */
	public boolean isSmPaidUpCapitalNaccumView() {
		return smPaidUpCapitalNaccumView;
	}

	/**
	 * Sets the sm paid up capital naccum view.
	 * 
	 * @param smPaidUpCapitalNaccumView
	 *            the new sm paid up capital naccum view
	 */
	public void setSmPaidUpCapitalNaccumView(boolean smPaidUpCapitalNaccumView) {
		this.smPaidUpCapitalNaccumView = smPaidUpCapitalNaccumView;
	}

	/**
	 * Gets the sm mof reg no from var.
	 * 
	 * @return the sm mof reg no from var
	 */
	public String getSmMofRegNoFromVar() {
		return smMofRegNoFromVar;
	}

	/**
	 * Sets the sm mof reg no from var.
	 * 
	 * @param smMofRegNoFromVar
	 *            the new sm mof reg no from var
	 */
	public void setSmMofRegNoFromVar(String smMofRegNoFromVar) {
		this.smMofRegNoFromVar = smMofRegNoFromVar;
	}

	/**
	 * Gets the sm mof reg no to var.
	 * 
	 * @return the sm mof reg no to var
	 */
	public String getSmMofRegNoToVar() {
		return smMofRegNoToVar;
	}

	/**
	 * Sets the sm mof reg no to var.
	 * 
	 * @param smMofRegNoToVar
	 *            the new sm mof reg no to var
	 */
	public void setSmMofRegNoToVar(String smMofRegNoToVar) {
		this.smMofRegNoToVar = smMofRegNoToVar;
	}

	/**
	 * Gets the sm paid up capital frm var.
	 * 
	 * @return the sm paid up capital frm var
	 */
	public String getSmPaidUpCapitalFrmVar() {
		return smPaidUpCapitalFrmVar;
	}

	/**
	 * Sets the sm paid up capital frm var.
	 * 
	 * @param smPaidUpCapitalFrmVar
	 *            the new sm paid up capital frm var
	 */
	public void setSmPaidUpCapitalFrmVar(String smPaidUpCapitalFrmVar) {
		this.smPaidUpCapitalFrmVar = smPaidUpCapitalFrmVar;
	}

	/**
	 * Gets the sm paid up capital to var.
	 * 
	 * @return the sm paid up capital to var
	 */
	public String getSmPaidUpCapitalToVar() {
		return smPaidUpCapitalToVar;
	}

	/**
	 * Sets the sm paid up capital to var.
	 * 
	 * @param smPaidUpCapitalToVar
	 *            the new sm paid up capital to var
	 */
	public void setSmPaidUpCapitalToVar(String smPaidUpCapitalToVar) {
		this.smPaidUpCapitalToVar = smPaidUpCapitalToVar;
	}

	/**
	 * Gets the sm paid up capital naccum var.
	 * 
	 * @return the sm paid up capital naccum var
	 */
	public String getSmPaidUpCapitalNaccumVar() {
		return smPaidUpCapitalNaccumVar;
	}

	/**
	 * Sets the sm paid up capital naccum var.
	 * 
	 * @param smPaidUpCapitalNaccumVar
	 *            the new sm paid up capital naccum var
	 */
	public void setSmPaidUpCapitalNaccumVar(String smPaidUpCapitalNaccumVar) {
		this.smPaidUpCapitalNaccumVar = smPaidUpCapitalNaccumVar;
	}

	/**
	 * Checks if is sm role view.
	 * 
	 * @return true, if is sm role view
	 */
	public boolean isSmRoleView() {
		return smRoleView;
	}

	/**
	 * Sets the sm role view.
	 * 
	 * @param smRoleView
	 *            the new sm role view
	 */
	public void setSmRoleView(boolean smRoleView) {
		this.smRoleView = smRoleView;
	}

	/**
	 * Checks if is sm ep no view.
	 * 
	 * @return true, if is sm ep no view
	 */
	public boolean isSmEpNoView() {
		return smEpNoView;
	}

	/**
	 * Sets the sm ep no view.
	 * 
	 * @param smEpNoView
	 *            the new sm ep no view
	 */
	public void setSmEpNoView(boolean smEpNoView) {
		this.smEpNoView = smEpNoView;
	}

	/**
	 * Checks if is sm registration no view.
	 * 
	 * @return true, if is sm registration no view
	 */
	public boolean isSmRegistrationNoView() {
		return smRegistrationNoView;
	}

	/**
	 * Sets the sm registration no view.
	 * 
	 * @param smRegistrationNoView
	 *            the new sm registration no view
	 */
	public void setSmRegistrationNoView(boolean smRegistrationNoView) {
		this.smRegistrationNoView = smRegistrationNoView;
	}

	/**
	 * Checks if is sm supporting document mode view.
	 * 
	 * @return true, if is sm supporting document mode view
	 */
	public boolean isSmSupportingDocumentModeView() {
		return smSupportingDocumentModeView;
	}

	/**
	 * Sets the sm supporting document mode view.
	 * 
	 * @param smSupportingDocumentModeView
	 *            the new sm supporting document mode view
	 */
	public void setSmSupportingDocumentModeView(
			boolean smSupportingDocumentModeView) {
		this.smSupportingDocumentModeView = smSupportingDocumentModeView;
	}

	/**
	 * Checks if is sm supporting document status view.
	 * 
	 * @return true, if is sm supporting document status view
	 */
	public boolean isSmSupportingDocumentStatusView() {
		return smSupportingDocumentStatusView;
	}

	/**
	 * Gets the sm code var.
	 * 
	 * @return the smCodeVar
	 */
	public String getSmCodeVar() {
		return SmCodeVar;
	}

	/**
	 * Sets the sm code var.
	 * 
	 * @param smCodeVar
	 *            the smCodeVar to set
	 */
	public void setSmCodeVar(String smCodeVar) {
		SmCodeVar = smCodeVar;
	}

	/**
	 * Sets the sm supporting document status view.
	 * 
	 * @param smSupportingDocumentStatusView
	 *            the new sm supporting document status view
	 */
	public void setSmSupportingDocumentStatusView(
			boolean smSupportingDocumentStatusView) {
		this.smSupportingDocumentStatusView = smSupportingDocumentStatusView;
	}

	/**
	 * Gets the sm role var.
	 * 
	 * @return the sm role var
	 */
	public String getSmRoleVar() {
		return smRoleVar;
	}

	/**
	 * Sets the sm role var.
	 * 
	 * @param smRoleVar
	 *            the new sm role var
	 */
	public void setSmRoleVar(String smRoleVar) {
		this.smRoleVar = smRoleVar;
	}

	/**
	 * Gets the sm ep no var.
	 * 
	 * @return the sm ep no var
	 */
	public String getSmEpNoVar() {
		return smEpNoVar;
	}

	/**
	 * Sets the sm ep no var.
	 * 
	 * @param smEpNoVar
	 *            the new sm ep no var
	 */
	public void setSmEpNoVar(String smEpNoVar) {
		this.smEpNoVar = smEpNoVar;
	}

	/**
	 * Gets the sm registration no var.
	 * 
	 * @return the sm registration no var
	 */
	public String getSmRegistrationNoVar() {
		return smRegistrationNoVar;
	}

	/**
	 * Sets the sm registration no var.
	 * 
	 * @param smRegistrationNoVar
	 *            the new sm registration no var
	 */
	public void setSmRegistrationNoVar(String smRegistrationNoVar) {
		this.smRegistrationNoVar = smRegistrationNoVar;
	}

	/**
	 * Gets the sm supporting document mode var.
	 * 
	 * @return the sm supporting document mode var
	 */
	public String getSmSupportingDocumentModeVar() {
		return smSupportingDocumentModeVar;
	}

	/**
	 * Sets the sm supporting document mode var.
	 * 
	 * @param smSupportingDocumentModeVar
	 *            the new sm supporting document mode var
	 */
	public void setSmSupportingDocumentModeVar(
			String smSupportingDocumentModeVar) {
		this.smSupportingDocumentModeVar = smSupportingDocumentModeVar;
	}

	/**
	 * Gets the sm supporting document status var.
	 * 
	 * @return the sm supporting document status var
	 */
	public String getSmSupportingDocumentStatusVar() {
		return smSupportingDocumentStatusVar;
	}

	/**
	 * Sets the sm supporting document status var.
	 * 
	 * @param smSupportingDocumentStatusVar
	 *            the new sm supporting document status var
	 */
	public void setSmSupportingDocumentStatusVar(
			String smSupportingDocumentStatusVar) {
		this.smSupportingDocumentStatusVar = smSupportingDocumentStatusVar;
	}

	/**
	 * Checks if is pp year view.
	 * 
	 * @return true, if is pp year view
	 */
	public boolean isPpYearView() {
		return ppYearView;
	}

	/**
	 * Sets the pp year view.
	 * 
	 * @param ppYearView
	 *            the new pp year view
	 */
	public void setPpYearView(boolean ppYearView) {
		this.ppYearView = ppYearView;
	}

	/**
	 * Checks if is pp ministry view.
	 * 
	 * @return true, if is pp ministry view
	 */
	public boolean isPpMinistryView() {
		return ppMinistryView;
	}

	/**
	 * Sets the pp ministry view.
	 * 
	 * @param ppMinistryView
	 *            the new pp ministry view
	 */
	public void setPpMinistryView(boolean ppMinistryView) {
		this.ppMinistryView = ppMinistryView;
	}

	/**
	 * Checks if is pp ptj view.
	 * 
	 * @return true, if is pp ptj view
	 */
	public boolean isPpPTJView() {
		return ppPTJView;
	}

	/**
	 * Sets the pp ptj view.
	 * 
	 * @param ppPTJView
	 *            the new pp ptj view
	 */
	public void setPpPTJView(boolean ppPTJView) {
		this.ppPTJView = ppPTJView;
	}

	/**
	 * Gets the pp year var.
	 * 
	 * @return the pp year var
	 */
	public String getPpYearVar() {
		return ppYearVar;
	}

	/**
	 * Sets the pp year var.
	 * 
	 * @param ppYearVar
	 *            the new pp year var
	 */
	public void setPpYearVar(String ppYearVar) {
		this.ppYearVar = ppYearVar;
	}

	/**
	 * Gets the pp ministry var.
	 * 
	 * @return the pp ministry var
	 */
	public String getPpMinistryVar() {
		return ppMinistryVar;
	}

	/**
	 * Sets the pp ministry var.
	 * 
	 * @param ppMinistryVar
	 *            the new pp ministry var
	 */
	public void setPpMinistryVar(String ppMinistryVar) {
		this.ppMinistryVar = ppMinistryVar;
	}

	/**
	 * Gets the pp ptj var.
	 * 
	 * @return the pp ptj var
	 */
	public String getPpPTJVar() {
		return ppPTJVar;
	}

	/**
	 * Sets the pp ptj var.
	 * 
	 * @param ppPTJVar
	 *            the new pp ptj var
	 */
	public void setPpPTJVar(String ppPTJVar) {
		this.ppPTJVar = ppPTJVar;
	}

	/**
	 * Checks if is sm mandatory created from date view.
	 * 
	 * @return true, if is sm mandatory created from date view
	 */
	public boolean isSmMandatoryCreatedFromDateView() {
		return smMandatoryCreatedFromDateView;
	}

	/**
	 * Sets the sm mandatory created from date view.
	 * 
	 * @param smMandatoryCreatedFromDateView
	 *            the new sm mandatory created from date view
	 */
	public void setSmMandatoryCreatedFromDateView(
			boolean smMandatoryCreatedFromDateView) {
		this.smMandatoryCreatedFromDateView = smMandatoryCreatedFromDateView;
	}

	/**
	 * Checks if is sc purchase in default status view.
	 * 
	 * @return true, if is sc purchase in default status view
	 */
	public boolean isScPurchaseInDefaultStatusView() {
		return scPurchaseInDefaultStatusView;
	}

	/**
	 * Sets the sc purchase in default status view.
	 * 
	 * @param scPurchaseInDefaultStatusView
	 *            the new sc purchase in default status view
	 */
	public void setScPurchaseInDefaultStatusView(
			boolean scPurchaseInDefaultStatusView) {
		this.scPurchaseInDefaultStatusView = scPurchaseInDefaultStatusView;
	}

	/**
	 * Gets the sc purchase in default status var.
	 * 
	 * @return the sc purchase in default status var
	 */
	public String getScPurchaseInDefaultStatusVar() {
		return scPurchaseInDefaultStatusVar;
	}

	/**
	 * Sets the sc purchase in default status var.
	 * 
	 * @param scPurchaseInDefaultStatusVar
	 *            the new sc purchase in default status var
	 */
	public void setScPurchaseInDefaultStatusVar(
			String scPurchaseInDefaultStatusVar) {
		this.scPurchaseInDefaultStatusVar = scPurchaseInDefaultStatusVar;
	}

	/**
	 * Checks if is sc simple quote dtl group view.
	 * 
	 * @return true, if is sc simple quote dtl group view
	 */
	public boolean isScSimpleQuoteDtlGroupView() {
		return scSimpleQuoteDtlGroupView;
	}

	/**
	 * Sets the sc simple quote dtl group view.
	 * 
	 * @param scSimpleQuoteDtlGroupView
	 *            the new sc simple quote dtl group view
	 */
	public void setScSimpleQuoteDtlGroupView(boolean scSimpleQuoteDtlGroupView) {
		this.scSimpleQuoteDtlGroupView = scSimpleQuoteDtlGroupView;
	}

	/**
	 * Gets the sc simple quote dtl group var.
	 * 
	 * @return the sc simple quote dtl group var
	 */
	public String getScSimpleQuoteDtlGroupVar() {
		return scSimpleQuoteDtlGroupVar;
	}

	/**
	 * Sets the sc simple quote dtl group var.
	 * 
	 * @param scSimpleQuoteDtlGroupVar
	 *            the new sc simple quote dtl group var
	 */
	public void setScSimpleQuoteDtlGroupVar(String scSimpleQuoteDtlGroupVar) {
		this.scSimpleQuoteDtlGroupVar = scSimpleQuoteDtlGroupVar;
	}

	/**
	 * Checks if is sc simple quote status view.
	 * 
	 * @return true, if is sc simple quote status view
	 */
	public boolean isScSimpleQuoteStatusView() {
		return scSimpleQuoteStatusView;
	}

	/**
	 * Sets the sc simple quote status view.
	 * 
	 * @param scSimpleQuoteStatusView
	 *            the new sc simple quote status view
	 */
	public void setScSimpleQuoteStatusView(boolean scSimpleQuoteStatusView) {
		this.scSimpleQuoteStatusView = scSimpleQuoteStatusView;
	}

	/**
	 * Gets the sc simple quote status var.
	 * 
	 * @return the sc simple quote status var
	 */
	public String getScSimpleQuoteStatusVar() {
		return scSimpleQuoteStatusVar;
	}

	/**
	 * Sets the sc simple quote status var.
	 * 
	 * @param scSimpleQuoteStatusVar
	 *            the new sc simple quote status var
	 */
	public void setScSimpleQuoteStatusVar(String scSimpleQuoteStatusVar) {
		this.scSimpleQuoteStatusVar = scSimpleQuoteStatusVar;
	}

	/**
	 * Checks if is sc request note status view.
	 * 
	 * @return true, if is sc request note status view
	 */
	public boolean isScRequestNoteStatusView() {
		return scRequestNoteStatusView;
	}

	/**
	 * Sets the sc request note status view.
	 * 
	 * @param scRequestNoteStatusView
	 *            the new sc request note status view
	 */
	public void setScRequestNoteStatusView(boolean scRequestNoteStatusView) {
		this.scRequestNoteStatusView = scRequestNoteStatusView;
	}

	/**
	 * Gets the sc request note status var.
	 * 
	 * @return the sc request note status var
	 */
	public String getScRequestNoteStatusVar() {
		return scRequestNoteStatusVar;
	}

	/**
	 * Sets the sc request note status var.
	 * 
	 * @param scRequestNoteStatusVar
	 *            the new sc request note status var
	 */
	public void setScRequestNoteStatusVar(String scRequestNoteStatusVar) {
		this.scRequestNoteStatusVar = scRequestNoteStatusVar;
	}

	/**
	 * Checks if is approved date view.
	 * 
	 * @return true, if is approved date view
	 */
	public boolean isApprovedDateView() {
		return approvedDateView;
	}

	/**
	 * Sets the approved date view.
	 * 
	 * @param approvedDateView
	 *            the new approved date view
	 */
	public void setApprovedDateView(boolean approvedDateView) {
		this.approvedDateView = approvedDateView;
	}

	/**
	 * Checks if is ct sub type view.
	 * 
	 * @return true, if is ct sub type view
	 */
	public boolean isCtSubTypeView() {
		return ctSubTypeView;
	}

	/**
	 * Sets the ct sub type view.
	 * 
	 * @param ctSubTypeView
	 *            the new ct sub type view
	 */
	public void setCtSubTypeView(boolean ctSubTypeView) {
		this.ctSubTypeView = ctSubTypeView;
	}

	/**
	 * Gets the ct sub type var.
	 * 
	 * @return the ct sub type var
	 */
	public String getCtSubTypeVar() {
		return ctSubTypeVar;
	}

	/**
	 * Sets the ct sub type var.
	 * 
	 * @param ctSubTypeVar
	 *            the new ct sub type var
	 */
	public void setCtSubTypeVar(String ctSubTypeVar) {
		this.ctSubTypeVar = ctSubTypeVar;
	}

	/**
	 * Checks if is ct user type view.
	 * 
	 * @return true, if is ct user type view
	 */
	public boolean isCtUserTypeView() {
		return ctUserTypeView;
	}

	/**
	 * Sets the ct user type view.
	 * 
	 * @param ctUserTypeView
	 *            the new ct user type view
	 */
	public void setCtUserTypeView(boolean ctUserTypeView) {
		this.ctUserTypeView = ctUserTypeView;
	}

	/**
	 * Gets the ct user type var.
	 * 
	 * @return the ct user type var
	 */
	public String getCtUserTypeVar() {
		return ctUserTypeVar;
	}

	/**
	 * Gets the sm mof registration fee var.
	 * 
	 * @return the smMofRegistrationFeeVarVar
	 */
	public String getSmMofRegistrationFeeVar() {
		return smMofRegistrationFeeVar;
	}

	/**
	 * Sets the sm mof registration fee var.
	 * 
	 * @param smMofRegistrationFeeVar
	 *            the new sm mof registration fee var
	 */
	public void setSmMofRegistrationFeeVar(String smMofRegistrationFeeVar) {
		this.smMofRegistrationFeeVar = smMofRegistrationFeeVar;
	}

	/**
	 * Gets the sm processing fee var.
	 * 
	 * @return the smProcessingFeeVar
	 */
	public String getSmProcessingFeeVar() {
		return smProcessingFeeVar;
	}

	/**
	 * Sets the sm processing fee var.
	 * 
	 * @param smProcessingFeeVar
	 *            the smProcessingFeeVar to set
	 */
	public void setSmProcessingFeeVar(String smProcessingFeeVar) {
		this.smProcessingFeeVar = smProcessingFeeVar;
	}

	/**
	 * Gets the sm soft certificate fee var.
	 * 
	 * @return the smSoftCertificateFeeVar
	 */
	public String getSmSoftCertificateFeeVar() {
		return smSoftCertificateFeeVar;
	}

	/**
	 * Sets the sm soft certificate fee var.
	 * 
	 * @param smSoftCertificateFeeVar
	 *            the smSoftCertificateFeeVar to set
	 */
	public void setSmSoftCertificateFeeVar(String smSoftCertificateFeeVar) {
		this.smSoftCertificateFeeVar = smSoftCertificateFeeVar;
	}

	/**
	 * Sets the ct user type var.
	 * 
	 * @param ctUserTypeVar
	 *            the new ct user type var
	 */
	public void setCtUserTypeVar(String ctUserTypeVar) {
		this.ctUserTypeVar = ctUserTypeVar;
	}

	/**
	 * Checks if is ct loa physical no view.
	 * 
	 * @return true, if is ct loa physical no view
	 */
	public boolean isCtLOAPhysicalNoView() {
		return ctLOAPhysicalNoView;
	}

	/**
	 * Sets the ct loa physical no view.
	 * 
	 * @param ctLOAPhysicalNoView
	 *            the new ct loa physical no view
	 */
	public void setCtLOAPhysicalNoView(boolean ctLOAPhysicalNoView) {
		this.ctLOAPhysicalNoView = ctLOAPhysicalNoView;
	}

	/**
	 * Gets the ct loa physical no var.
	 * 
	 * @return the ct loa physical no var
	 */
	public String getCtLOAPhysicalNoVar() {
		return ctLOAPhysicalNoVar;
	}

	/**
	 * Sets the ct loa physical no var.
	 * 
	 * @param ctLOAPhysicalNoVar
	 *            the new ct loa physical no var
	 */
	public void setCtLOAPhysicalNoVar(String ctLOAPhysicalNoVar) {
		this.ctLOAPhysicalNoVar = ctLOAPhysicalNoVar;
	}

	/**
	 * Checks if is ct status two view.
	 * 
	 * @return true, if is ct status two view
	 */
	public boolean isCtStatusTwoView() {
		return ctStatusTwoView;
	}

	/**
	 * Sets the ct status two view.
	 * 
	 * @param ctStatusTwoView
	 *            the new ct status two view
	 */
	public void setCtStatusTwoView(boolean ctStatusTwoView) {
		this.ctStatusTwoView = ctStatusTwoView;
	}

	/**
	 * Gets the ct status two var.
	 * 
	 * @return the ct status two var
	 */
	public String getCtStatusTwoVar() {
		return ctStatusTwoVar;
	}

	/**
	 * Sets the ct status two var.
	 * 
	 * @param ctStatusTwoVar
	 *            the new ct status two var
	 */
	public void setCtStatusTwoVar(String ctStatusTwoVar) {
		this.ctStatusTwoVar = ctStatusTwoVar;
	}

	/**
	 * Checks if is ct group three view.
	 * 
	 * @return true, if is ct group three view
	 */
	public boolean isCtGroupThreeView() {
		return ctGroupThreeView;
	}

	/**
	 * Sets the ct group three view.
	 * 
	 * @param ctGroupThreeView
	 *            the new ct group three view
	 */
	public void setCtGroupThreeView(boolean ctGroupThreeView) {
		this.ctGroupThreeView = ctGroupThreeView;
	}

	/**
	 * Gets the ct group three var.
	 * 
	 * @return the ct group three var
	 */
	public String getCtGroupThreeVar() {
		return ctGroupThreeVar;
	}

	/**
	 * Sets the ct group three var.
	 * 
	 * @param ctGroupThreeVar
	 *            the new ct group three var
	 */
	public void setCtGroupThreeVar(String ctGroupThreeVar) {
		this.ctGroupThreeVar = ctGroupThreeVar;
	}

	/**
	 * Checks if is ct physical contract no view.
	 * 
	 * @return true, if is ct physical contract no view
	 */
	public boolean isCtPhysicalContractNoView() {
		return ctPhysicalContractNoView;
	}

	/**
	 * Sets the ct physical contract no view.
	 * 
	 * @param ctPhysicalContractNoView
	 *            the new ct physical contract no view
	 */
	public void setCtPhysicalContractNoView(boolean ctPhysicalContractNoView) {
		this.ctPhysicalContractNoView = ctPhysicalContractNoView;
	}

	/**
	 * Gets the ct physical contract no var.
	 * 
	 * @return the ct physical contract no var
	 */
	public String getCtPhysicalContractNoVar() {
		return ctPhysicalContractNoVar;
	}

	/**
	 * Sets the ct physical contract no var.
	 * 
	 * @param ctPhysicalContractNoVar
	 *            the new ct physical contract no var
	 */
	public void setCtPhysicalContractNoVar(String ctPhysicalContractNoVar) {
		this.ctPhysicalContractNoVar = ctPhysicalContractNoVar;
	}

	/**
	 * Checks if is fl group seventeen view.
	 * 
	 * @return true, if is fl group seventeen view
	 */
	public boolean isFlGroupSeventeenView() {
		return flGroupSeventeenView;
	}

	/**
	 * Sets the fl group seventeen view.
	 * 
	 * @param flGroupSeventeenView
	 *            the new fl group seventeen view
	 */
	public void setFlGroupSeventeenView(boolean flGroupSeventeenView) {
		this.flGroupSeventeenView = flGroupSeventeenView;
	}

	/**
	 * Gets the fl group seventeen var.
	 * 
	 * @return the fl group seventeen var
	 */
	public String getFlGroupSeventeenVar() {
		return flGroupSeventeenVar;
	}

	/**
	 * Sets the fl group seventeen var.
	 * 
	 * @param flGroupSeventeenVar
	 *            the new fl group seventeen var
	 */
	public void setFlGroupSeventeenVar(String flGroupSeventeenVar) {
		this.flGroupSeventeenVar = flGroupSeventeenVar;
	}

	/**
	 * Gets the sm rt report type var.
	 * 
	 * @return the smRTReportTypeVar
	 */
	public String getSmRTReportTypeVar() {
		return smRTReportTypeVar;
	}

	/**
	 * Sets the sm rt report type var.
	 * 
	 * @param smRTReportTypeVar
	 *            the smRTReportTypeVar to set
	 */
	public void setSmRTReportTypeVar(String smRTReportTypeVar) {
		this.smRTReportTypeVar = smRTReportTypeVar;
	}

	/**
	 * Checks if is enable criteria menu view.
	 * 
	 * @return true, if is enable criteria menu view
	 */
	public boolean isEnableCriteriaMenuView() {
		return enableCriteriaMenuView;
	}

	/**
	 * Sets the enable criteria menu view.
	 * 
	 * @param enableCriteriaMenuView
	 *            the new enable criteria menu view
	 */
	public void setEnableCriteriaMenuView(boolean enableCriteriaMenuView) {
		this.enableCriteriaMenuView = enableCriteriaMenuView;
	}

	/**
	 * Checks if is fl supplier text view.
	 * 
	 * @return true, if is fl supplier text view
	 */
	public boolean isFlSupplierTextView() {
		return flSupplierTextView;
	}

	/**
	 * Sets the fl supplier text view.
	 * 
	 * @param flSupplierTextView
	 *            the new fl supplier text view
	 */
	public void setFlSupplierTextView(boolean flSupplierTextView) {
		this.flSupplierTextView = flSupplierTextView;
	}

	/**
	 * Checks if is fl supplier dropdown view.
	 * 
	 * @return true, if is fl supplier dropdown view
	 */
	public boolean isFlSupplierDropdownView() {
		return flSupplierDropdownView;
	}

	/**
	 * Sets the fl supplier dropdown view.
	 * 
	 * @param flSupplierDropdownView
	 *            the new fl supplier dropdown view
	 */
	public void setFlSupplierDropdownView(boolean flSupplierDropdownView) {
		this.flSupplierDropdownView = flSupplierDropdownView;
	}

	/**
	 * Checks if is sm application date view.
	 * 
	 * @return true, if is sm application date view
	 */
	public boolean isSmApplicationDateView() {
		return smApplicationDateView;
	}

	/**
	 * Sets the sm application date view.
	 * 
	 * @param smApplicationDateView
	 *            the new sm application date view
	 */
	public void setSmApplicationDateView(boolean smApplicationDateView) {
		this.smApplicationDateView = smApplicationDateView;
	}

	/**
	 * Checks if is sm mandatory date view.
	 * 
	 * @return true, if is sm mandatory date view
	 */
	public boolean isSmMandatoryDateView() {
		return smMandatoryDateView;
	}

	/**
	 * Sets the sm mandatory date view.
	 * 
	 * @param smMandatoryDateView
	 *            the new sm mandatory date view
	 */
	public void setSmMandatoryDateView(boolean smMandatoryDateView) {
		this.smMandatoryDateView = smMandatoryDateView;
	}

	/**
	 * Checks if is sm non mandatory date view.
	 * 
	 * @return true, if is sm non mandatory date view
	 */
	public boolean isSmNonMandatoryDateView() {
		return smNonMandatoryDateView;
	}

	/**
	 * Sets the sm non mandatory date view.
	 * 
	 * @param smNonMandatoryDateView
	 *            the new sm non mandatory date view
	 */
	public void setSmNonMandatoryDateView(boolean smNonMandatoryDateView) {
		this.smNonMandatoryDateView = smNonMandatoryDateView;
	}

	/**
	 * Checks if is sm rejected date view.
	 * 
	 * @return true, if is sm rejected date view
	 */
	public boolean isSmRejectedDateView() {
		return smRejectedDateView;
	}

	/**
	 * Sets the sm rejected date view.
	 * 
	 * @param smRejectedDateView
	 *            the new sm rejected date view
	 */
	public void setSmRejectedDateView(boolean smRejectedDateView) {
		this.smRejectedDateView = smRejectedDateView;
	}

	/**
	 * Checks if is sm registered date view.
	 * 
	 * @return true, if is sm registered date view
	 */
	public boolean isSmRegisteredDateView() {
		return smRegisteredDateView;
	}

	/**
	 * Sets the sm registered date view.
	 * 
	 * @param smRegisteredDateView
	 *            the new sm registered date view
	 */
	public void setSmRegisteredDateView(boolean smRegisteredDateView) {
		this.smRegisteredDateView = smRegisteredDateView;
	}

	/**
	 * Checks if is sm schedule site visit date view.
	 * 
	 * @return true, if is sm schedule site visit date view
	 */
	public boolean isSmScheduleSiteVisitDateView() {
		return smScheduleSiteVisitDateView;
	}

	/**
	 * Sets the sm schedule site visit date view.
	 * 
	 * @param smScheduleSiteVisitDateView
	 *            the new sm schedule site visit date view
	 */
	public void setSmScheduleSiteVisitDateView(
			boolean smScheduleSiteVisitDateView) {
		this.smScheduleSiteVisitDateView = smScheduleSiteVisitDateView;
	}

	/**
	 * Checks if is sm activation date view.
	 * 
	 * @return true, if is sm activation date view
	 */
	public boolean isSmActivationDateView() {
		return smActivationDateView;
	}

	/**
	 * Sets the sm activation date view.
	 * 
	 * @param smActivationDateView
	 *            the new sm activation date view
	 */
	public void setSmActivationDateView(boolean smActivationDateView) {
		this.smActivationDateView = smActivationDateView;
	}

	/**
	 * Checks if is sm submission date view.
	 * 
	 * @return true, if is sm submission date view
	 */
	public boolean isSmSubmissionDateView() {
		return smSubmissionDateView;
	}

	/**
	 * Sets the sm submission date view.
	 * 
	 * @param smSubmissionDateView
	 *            the new sm submission date view
	 */
	public void setSmSubmissionDateView(boolean smSubmissionDateView) {
		this.smSubmissionDateView = smSubmissionDateView;
	}

	/**
	 * Checks if is sm expiry date view.
	 * 
	 * @return true, if is sm expiry date view
	 */
	public boolean isSmExpiryDateView() {
		return smExpiryDateView;
	}

	/**
	 * Sets the sm expiry date view.
	 * 
	 * @param smExpiryDateView
	 *            the new sm expiry date view
	 */
	public void setSmExpiryDateView(boolean smExpiryDateView) {
		this.smExpiryDateView = smExpiryDateView;
	}

	/**
	 * Checks if is sm created date view.
	 * 
	 * @return true, if is sm created date view
	 */
	public boolean isSmCreatedDateView() {
		return smCreatedDateView;
	}

	/**
	 * Sets the sm created date view.
	 * 
	 * @param smCreatedDateView
	 *            the new sm created date view
	 */
	public void setSmCreatedDateView(boolean smCreatedDateView) {
		this.smCreatedDateView = smCreatedDateView;
	}

	/**
	 * Checks if is sm mandatory received from date view.
	 * 
	 * @return true, if is sm mandatory received from date view
	 */
	public boolean isSmMandatoryReceivedFromDateView() {
		return smMandatoryReceivedFromDateView;
	}

	/**
	 * Sets the sm mandatory received from date view.
	 * 
	 * @param smMandatoryReceivedFromDateView
	 *            the new sm mandatory received from date view
	 */
	public void setSmMandatoryReceivedFromDateView(
			boolean smMandatoryReceivedFromDateView) {
		this.smMandatoryReceivedFromDateView = smMandatoryReceivedFromDateView;
	}

	/**
	 * Checks if is madatory indicator view.
	 * 
	 * @return true, if is madatory indicator view
	 */
	public boolean isMadatoryIndicatorView() {
		return madatoryIndicatorView;
	}

	/**
	 * Sets the madatory indicator view.
	 * 
	 * @param madatoryIndicatorView
	 *            the new madatory indicator view
	 */
	public void setMadatoryIndicatorView(boolean madatoryIndicatorView) {
		this.madatoryIndicatorView = madatoryIndicatorView;
	}

	/**
	 * Checks if is ct procurement mode view.
	 * 
	 * @return true, if is ct procurement mode view
	 */
	public boolean isCtProcurementModeView() {
		return ctProcurementModeView;
	}

	/**
	 * Sets the ct procurement mode view.
	 * 
	 * @param ctProcurementModeView
	 *            the new ct procurement mode view
	 */
	public void setCtProcurementModeView(boolean ctProcurementModeView) {
		this.ctProcurementModeView = ctProcurementModeView;
	}

	/**
	 * Gets the ct procurement mode var.
	 * 
	 * @return the ct procurement mode var
	 */
	public String getCtProcurementModeVar() {
		return ctProcurementModeVar;
	}

	/**
	 * Sets the ct procurement mode var.
	 * 
	 * @param ctProcurementModeVar
	 *            the new ct procurement mode var
	 */
	public void setCtProcurementModeVar(String ctProcurementModeVar) {
		this.ctProcurementModeVar = ctProcurementModeVar;
	}

	/**
	 * Gets the lev one cat lst1.
	 * 
	 * @return the lev one cat lst1
	 */
	public List<SelectItem> getLevOneCatLst1() {
		return levOneCatLst1;
	}

	/**
	 * Sets the lev one cat lst1.
	 * 
	 * @param levOneCatLst1
	 *            the new lev one cat lst1
	 */
	public void setLevOneCatLst1(List<SelectItem> levOneCatLst1) {
		this.levOneCatLst1 = levOneCatLst1;
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
	 * Gets the cm extention brand var.
	 * 
	 * @return the cm extention brand var
	 */
	public String getCmExtentionBrandVar() {
		return cmExtentionBrandVar;
	}

	/**
	 * Sets the cm extention brand var.
	 * 
	 * @param cmExtentionBrandVar
	 *            the new cm extention brand var
	 */
	public void setCmExtentionBrandVar(String cmExtentionBrandVar) {
		this.cmExtentionBrandVar = cmExtentionBrandVar;
	}

	/**
	 * Gets the cm extention type var.
	 * 
	 * @return the cm extention type var
	 */
	public String getCmExtentionTypeVar() {
		return cmExtentionTypeVar;
	}

	/**
	 * Sets the cm extention type var.
	 * 
	 * @param cmExtentionTypeVar
	 *            the new cm extention type var
	 */
	public void setCmExtentionTypeVar(String cmExtentionTypeVar) {
		this.cmExtentionTypeVar = cmExtentionTypeVar;
	}

	/**
	 * Gets the cm extention measurment var.
	 * 
	 * @return the cm extention measurment var
	 */
	public String getCmExtentionMeasurmentVar() {
		return cmExtentionMeasurmentVar;
	}

	/**
	 * Sets the cm extention measurment var.
	 * 
	 * @param cmExtentionMeasurmentVar
	 *            the new cm extention measurment var
	 */
	public void setCmExtentionMeasurmentVar(String cmExtentionMeasurmentVar) {
		this.cmExtentionMeasurmentVar = cmExtentionMeasurmentVar;
	}

	/**
	 * Gets the cm extention colour var.
	 * 
	 * @return the cm extention colour var
	 */
	public String getCmExtentionColourVar() {
		return cmExtentionColourVar;
	}

	/**
	 * Sets the cm extention colour var.
	 * 
	 * @param cmExtentionColourVar
	 *            the new cm extention colour var
	 */
	public void setCmExtentionColourVar(String cmExtentionColourVar) {
		this.cmExtentionColourVar = cmExtentionColourVar;
	}

	/**
	 * Checks if is sm year text view.
	 * 
	 * @return true, if is sm year text view
	 */
	public boolean isSmYearTextView() {
		return smYearTextView;
	}

	/**
	 * Sets the sm year text view.
	 * 
	 * @param smYearTextView
	 *            the new sm year text view
	 */
	public void setSmYearTextView(boolean smYearTextView) {
		this.smYearTextView = smYearTextView;
	}

	/**
	 * Gets the sm year text var.
	 * 
	 * @return the sm year text var
	 */
	public String getSmYearTextVar() {
		return smYearTextVar;
	}

	/**
	 * Sets the sm year text var.
	 * 
	 * @param smYearTextVar
	 *            the new sm year text var
	 */
	public void setSmYearTextVar(String smYearTextVar) {
		this.smYearTextVar = smYearTextVar;
	}

	/**
	 * Checks if is cm group one view.
	 * 
	 * @return true, if is cm group one view
	 */
	public boolean isCmGroupOneView() {
		return cmGroupOneView;
	}

	/**
	 * Sets the cm group one view.
	 * 
	 * @param cmGroupOneView
	 *            the new cm group one view
	 */
	public void setCmGroupOneView(boolean cmGroupOneView) {
		this.cmGroupOneView = cmGroupOneView;
	}

	/**
	 * Gets the cm group one var.
	 * 
	 * @return the cm group one var
	 */
	public String getCmGroupOneVar() {
		return cmGroupOneVar;
	}

	/**
	 * Sets the cm group one var.
	 * 
	 * @param cmGroupOneVar
	 *            the new cm group one var
	 */
	public void setCmGroupOneVar(String cmGroupOneVar) {
		this.cmGroupOneVar = cmGroupOneVar;
	}

	/**
	 * Checks if is ct ministry view.
	 * 
	 * @return true, if is ct ministry view
	 */
	public boolean isCtMinistryView() {
		return ctMinistryView;
	}

	/**
	 * Sets the ct ministry view.
	 * 
	 * @param ctMinistryView
	 *            the new ct ministry view
	 */
	public void setCtMinistryView(boolean ctMinistryView) {
		this.ctMinistryView = ctMinistryView;
	}

	/**
	 * Checks if is ct ptj view.
	 * 
	 * @return true, if is ct ptj view
	 */
	public boolean isCtPtjView() {
		return ctPtjView;
	}

	/**
	 * Sets the ct ptj view.
	 * 
	 * @param ctPtjView
	 *            the new ct ptj view
	 */
	public void setCtPtjView(boolean ctPtjView) {
		this.ctPtjView = ctPtjView;
	}

	/**
	 * Gets the ct ministry var.
	 * 
	 * @return the ct ministry var
	 */
	public String getCtMinistryVar() {
		return ctMinistryVar;
	}

	/**
	 * Sets the ct ministry var.
	 * 
	 * @param ctMinistryVar
	 *            the new ct ministry var
	 */
	public void setCtMinistryVar(String ctMinistryVar) {
		this.ctMinistryVar = ctMinistryVar;
	}

	/**
	 * Gets the ct ptj var.
	 * 
	 * @return the ct ptj var
	 */
	public String getCtPtjVar() {
		return ctPtjVar;
	}

	/**
	 * Sets the ct ptj var.
	 * 
	 * @param ctPtjVar
	 *            the new ct ptj var
	 */
	public void setCtPtjVar(String ctPtjVar) {
		this.ctPtjVar = ctPtjVar;
	}

	/**
	 * Checks if is ct loan no view.
	 * 
	 * @return true, if is ct loan no view
	 */
	public boolean isCtLoanNoView() {
		return ctLoanNoView;
	}

	/**
	 * Sets the ct loan no view.
	 * 
	 * @param ctLoanNoView
	 *            the new ct loan no view
	 */
	public void setCtLoanNoView(boolean ctLoanNoView) {
		this.ctLoanNoView = ctLoanNoView;
	}

	/**
	 * Gets the ct loan no var.
	 * 
	 * @return the ct loan no var
	 */
	public String getCtLoanNoVar() {
		return ctLoanNoVar;
	}

	/**
	 * Sets the ct loan no var.
	 * 
	 * @param ctLoanNoVar
	 *            the new ct loan no var
	 */
	public void setCtLoanNoVar(String ctLoanNoVar) {
		this.ctLoanNoVar = ctLoanNoVar;
	}

	/**
	 * Checks if is ct product service view.
	 * 
	 * @return true, if is ct product service view
	 */
	public boolean isCtProductServiceView() {
		return ctProductServiceView;
	}

	/**
	 * Sets the ct product service view.
	 * 
	 * @param ctProductServiceView
	 *            the new ct product service view
	 */
	public void setCtProductServiceView(boolean ctProductServiceView) {
		this.ctProductServiceView = ctProductServiceView;
	}

	/**
	 * Gets the ct product service var.
	 * 
	 * @return the ct product service var
	 */
	public String getCtProductServiceVar() {
		return ctProductServiceVar;
	}

	/**
	 * Sets the ct product service var.
	 * 
	 * @param ctProductServiceVar
	 *            the new ct product service var
	 */
	public void setCtProductServiceVar(String ctProductServiceVar) {
		this.ctProductServiceVar = ctProductServiceVar;
	}

	/**
	 * Checks if is ct group one view.
	 * 
	 * @return true, if is ct group one view
	 */
	public boolean isCtGroupOneView() {
		return ctGroupOneView;
	}

	/**
	 * Sets the ct group one view.
	 * 
	 * @param ctGroupOneView
	 *            the new ct group one view
	 */
	public void setCtGroupOneView(boolean ctGroupOneView) {
		this.ctGroupOneView = ctGroupOneView;
	}

	/**
	 * Checks if is ct group two view.
	 * 
	 * @return true, if is ct group two view
	 */
	public boolean isCtGroupTwoView() {
		return ctGroupTwoView;
	}

	/**
	 * Sets the ct group two view.
	 * 
	 * @param ctGroupTwoView
	 *            the new ct group two view
	 */
	public void setCtGroupTwoView(boolean ctGroupTwoView) {
		this.ctGroupTwoView = ctGroupTwoView;
	}

	/**
	 * Gets the ct group one var.
	 * 
	 * @return the ct group one var
	 */
	public String getCtGroupOneVar() {
		return ctGroupOneVar;
	}

	/**
	 * Sets the ct group one var.
	 * 
	 * @param ctGroupOneVar
	 *            the new ct group one var
	 */
	public void setCtGroupOneVar(String ctGroupOneVar) {
		this.ctGroupOneVar = ctGroupOneVar;
	}

	/**
	 * Gets the ct group two var.
	 * 
	 * @return the ct group two var
	 */
	public String getCtGroupTwoVar() {
		return ctGroupTwoVar;
	}

	/**
	 * Sets the ct group two var.
	 * 
	 * @param ctGroupTwoVar
	 *            the new ct group two var
	 */
	public void setCtGroupTwoVar(String ctGroupTwoVar) {
		this.ctGroupTwoVar = ctGroupTwoVar;
	}

	/**
	 * Checks if is cm extention view.
	 * 
	 * @return true, if is cm extention view
	 */
	public boolean isCmExtentionView() {
		return cmExtentionView;
	}

	/**
	 * Sets the cm extention view.
	 * 
	 * @param cmExtentionView
	 *            the new cm extention view
	 */
	public void setCmExtentionView(boolean cmExtentionView) {
		this.cmExtentionView = cmExtentionView;
	}

	/**
	 * Gets the cm extention var.
	 * 
	 * @return the cm extention var
	 */
	public String getCmExtentionVar() {
		return cmExtentionVar;
	}

	/**
	 * Sets the cm extention var.
	 * 
	 * @param cmExtentionVar
	 *            the new cm extention var
	 */
	public void setCmExtentionVar(String cmExtentionVar) {
		this.cmExtentionVar = cmExtentionVar;
	}

	/**
	 * Gets the lng user id.
	 * 
	 * @return the lng user id
	 */
	public long getLngUserId() {
		return lngUserId;
	}

	/**
	 * Sets the lng user id.
	 * 
	 * @param lngUserId
	 *            the new lng user id
	 */
	public void setLngUserId(long lngUserId) {
		this.lngUserId = lngUserId;
	}

	/**
	 * Gets the cm extention code var.
	 * 
	 * @return the cm extention code var
	 */
	public String getCmExtentionCodeVar() {
		return cmExtentionCodeVar;
	}

	/**
	 * Sets the cm extention code var.
	 * 
	 * @param cmExtentionCodeVar
	 *            the new cm extention code var
	 */
	public void setCmExtentionCodeVar(String cmExtentionCodeVar) {
		this.cmExtentionCodeVar = cmExtentionCodeVar;
	}

	/**
	 * Checks if is cm suppl or admin view.
	 * 
	 * @return true, if is cm suppl or admin view
	 */
	public boolean isCmSupplOrAdminView() {
		return cmSupplOrAdminView;
	}

	/**
	 * Sets the cm suppl or admin view.
	 * 
	 * @param cmSupplOrAdminView
	 *            the new cm suppl or admin view
	 */
	public void setCmSupplOrAdminView(boolean cmSupplOrAdminView) {
		this.cmSupplOrAdminView = cmSupplOrAdminView;
	}

	/**
	 * Gets the cm suppl or admin var.
	 * 
	 * @return the cm suppl or admin var
	 */
	public String getCmSupplOrAdminVar() {
		return cmSupplOrAdminVar;
	}

	/**
	 * Sets the cm suppl or admin var.
	 * 
	 * @param cmSupplOrAdminVar
	 *            the new cm suppl or admin var
	 */
	public void setCmSupplOrAdminVar(String cmSupplOrAdminVar) {
		this.cmSupplOrAdminVar = cmSupplOrAdminVar;
	}

	/**
	 * Checks if is sm mandatory both supplier type view.
	 * 
	 * @return true, if is sm mandatory both supplier type view
	 */
	public boolean isSmMandatoryBothSupplierTypeView() {
		return smMandatoryBothSupplierTypeView;
	}

	/**
	 * Sets the sm mandatory both supplier type view.
	 * 
	 * @param smMandatoryBothSupplierTypeView
	 *            the new sm mandatory both supplier type view
	 */
	public void setSmMandatoryBothSupplierTypeView(
			boolean smMandatoryBothSupplierTypeView) {
		this.smMandatoryBothSupplierTypeView = smMandatoryBothSupplierTypeView;
	}

	/**
	 * Checks if is sm mandatory report type view.
	 * 
	 * @return true, if is sm mandatory report type view
	 */
	public boolean isSmMandatoryReportTypeView() {
		return smMandatoryReportTypeView;
	}

	/**
	 * Sets the sm mandatory report type view.
	 * 
	 * @param smMandatoryReportTypeView
	 *            the new sm mandatory report type view
	 */
	public void setSmMandatoryReportTypeView(boolean smMandatoryReportTypeView) {
		this.smMandatoryReportTypeView = smMandatoryReportTypeView;
	}

	/**
	 * Checks if is sm mandatory application no view.
	 * 
	 * @return true, if is sm mandatory application no view
	 */
	public boolean isSmMandatoryApplicationNoView() {
		return smMandatoryApplicationNoView;
	}

	/**
	 * Sets the sm mandatory application no view.
	 * 
	 * @param smMandatoryApplicationNoView
	 *            the new sm mandatory application no view
	 */
	public void setSmMandatoryApplicationNoView(
			boolean smMandatoryApplicationNoView) {
		this.smMandatoryApplicationNoView = smMandatoryApplicationNoView;
	}

	/**
	 * Checks if is sm svo user view.
	 * 
	 * @return true, if is sm svo user view
	 */
	public boolean isSmSVOUserView() {
		return smSVOUserView;
	}

	/**
	 * Checks if is sm mandatory for code view.
	 * 
	 * @return the smMandatoryForCodeView
	 */
	public boolean isSmMandatoryForCodeView() {
		return smMandatoryForCodeView;
	}

	/**
	 * Sets the sm mandatory for code view.
	 * 
	 * @param smMandatoryForCodeView
	 *            the smMandatoryForCodeView to set
	 */
	public void setSmMandatoryForCodeView(boolean smMandatoryForCodeView) {
		this.smMandatoryForCodeView = smMandatoryForCodeView;
	}

	/**
	 * Sets the sm svo user view.
	 * 
	 * @param smSVOUserView
	 *            the new sm svo user view
	 */
	public void setSmSVOUserView(boolean smSVOUserView) {
		this.smSVOUserView = smSVOUserView;
	}

	/**
	 * Gets the sm svo user var.
	 * 
	 * @return the sm svo user var
	 */
	public String getSmSVOUserVar() {
		return smSVOUserVar;
	}

	/**
	 * Sets the sm svo user var.
	 * 
	 * @param smSVOUserVar
	 *            the new sm svo user var
	 */
	public void setSmSVOUserVar(String smSVOUserVar) {
		this.smSVOUserVar = smSVOUserVar;
	}

	/**
	 * Gets the sm mandatory created from year var.
	 * 
	 * @return the sm mandatory created from year var
	 */
	public String getSmMandatoryCreatedFromYearVar() {
		return smMandatoryCreatedFromYearVar;
	}

	/**
	 * Sets the sm mandatory created from year var.
	 * 
	 * @param smMandatoryCreatedFromYearVar
	 *            the new sm mandatory created from year var
	 */
	public void setSmMandatoryCreatedFromYearVar(
			String smMandatoryCreatedFromYearVar) {
		this.smMandatoryCreatedFromYearVar = smMandatoryCreatedFromYearVar;
	}

	/**
	 * Gets the sm mandatory created to year var.
	 * 
	 * @return the sm mandatory created to year var
	 */
	public String getSmMandatoryCreatedToYearVar() {
		return smMandatoryCreatedToYearVar;
	}

	/**
	 * Sets the sm mandatory created to year var.
	 * 
	 * @param smMandatoryCreatedToYearVar
	 *            the new sm mandatory created to year var
	 */
	public void setSmMandatoryCreatedToYearVar(
			String smMandatoryCreatedToYearVar) {
		this.smMandatoryCreatedToYearVar = smMandatoryCreatedToYearVar;
	}

	/**
	 * Gets the year list.
	 * 
	 * @return the year list
	 */
	public List<String> getYearList() {
		return yearList;
	}

	/**
	 * Sets the year list.
	 * 
	 * @param yearList
	 *            the new year list
	 */
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}

	/**
	 * Gets the month list.
	 * 
	 * @return the month list
	 */
	public List<SelectItem> getMonthList() {
		return monthList;
	}

	/**
	 * Gets the month map.
	 * 
	 * @return the month map
	 */
	public HashMap<String, String> getMonthMap() {
		return monthMap;
	}

	/**
	 * Sets the month map.
	 * 
	 * @param monthMap
	 *            the month map
	 */
	public void setMonthMap(HashMap<String, String> monthMap) {
		this.monthMap = monthMap;
	}

	/**
	 * Sets the month list.
	 * 
	 * @param monthList
	 *            the new month list
	 */
	public void setMonthList(List<SelectItem> monthList) {
		this.monthList = monthList;
	}

	/**
	 * Checks if is sm mandatory static by state view.
	 * 
	 * @return true, if is sm mandatory static by state view
	 */
	public boolean isSmMandatoryStaticByStateView() {
		return smMandatoryStaticByStateView;
	}

	/**
	 * Sets the sm mandatory static by state view.
	 * 
	 * @param smMandatoryStaticByStateView
	 *            the new sm mandatory static by state view
	 */
	public void setSmMandatoryStaticByStateView(
			boolean smMandatoryStaticByStateView) {
		this.smMandatoryStaticByStateView = smMandatoryStaticByStateView;
	}

	/**
	 * Checks if is sm mandatory monthly rpt by year view.
	 * 
	 * @return true, if is sm mandatory monthly rpt by year view
	 */
	public boolean isSmMandatoryMonthlyRptByYearView() {
		return smMandatoryMonthlyRptByYearView;
	}

	/**
	 * Sets the sm mandatory monthly rpt by year view.
	 * 
	 * @param smMandatoryMonthlyRptByYearView
	 *            the new sm mandatory monthly rpt by year view
	 */
	public void setSmMandatoryMonthlyRptByYearView(
			boolean smMandatoryMonthlyRptByYearView) {
		this.smMandatoryMonthlyRptByYearView = smMandatoryMonthlyRptByYearView;
	}

	/**
	 * Gets the sm monthly rpt by year var.
	 * 
	 * @return the sm monthly rpt by year var
	 */
	public String getSmMonthlyRptByYearVar() {
		return smMonthlyRptByYearVar;
	}

	/**
	 * Sets the sm monthly rpt by year var.
	 * 
	 * @param smMonthlyRptByYearVar
	 *            the new sm monthly rpt by year var
	 */
	public void setSmMonthlyRptByYearVar(String smMonthlyRptByYearVar) {
		this.smMonthlyRptByYearVar = smMonthlyRptByYearVar;
	}

	/**
	 * Checks if is sm mandatory registration type view.
	 * 
	 * @return true, if is sm mandatory registration type view
	 */
	public boolean isSmMandatoryRegistrationTypeView() {
		return smMandatoryRegistrationTypeView;
	}

	/**
	 * Sets the sm mandatory registration type view.
	 * 
	 * @param smMandatoryRegistrationTypeView
	 *            the new sm mandatory registration type view
	 */
	public void setSmMandatoryRegistrationTypeView(
			boolean smMandatoryRegistrationTypeView) {
		this.smMandatoryRegistrationTypeView = smMandatoryRegistrationTypeView;
	}

	/**
	 * Checks if is sm mandatory company name view.
	 * 
	 * @return true, if is sm mandatory company name view
	 */
	public boolean isSmMandatoryCompanyNameView() {
		return smMandatoryCompanyNameView;
	}

	/**
	 * Sets the sm mandatory company name view.
	 * 
	 * @param smMandatoryCompanyNameView
	 *            the new sm mandatory company name view
	 */
	public void setSmMandatoryCompanyNameView(boolean smMandatoryCompanyNameView) {
		this.smMandatoryCompanyNameView = smMandatoryCompanyNameView;
	}

	/**
	 * Checks if is sm mandatory created from year view.
	 * 
	 * @return true, if is sm mandatory created from year view
	 */
	public boolean isSmMandatoryCreatedFromYearView() {
		return smMandatoryCreatedFromYearView;
	}

	/**
	 * Sets the sm mandatory created from year view.
	 * 
	 * @param smMandatoryCreatedFromYearView
	 *            the new sm mandatory created from year view
	 */
	public void setSmMandatoryCreatedFromYearView(
			boolean smMandatoryCreatedFromYearView) {
		this.smMandatoryCreatedFromYearView = smMandatoryCreatedFromYearView;
	}

	/**
	 * Gets the sm created from year var.
	 * 
	 * @return the sm created from year var
	 */
	public String getSmCreatedFromYearVar() {
		return smCreatedFromYearVar;
	}

	/**
	 * Sets the sm created from year var.
	 * 
	 * @param smCreatedFromYearVar
	 *            the new sm created from year var
	 */
	public void setSmCreatedFromYearVar(String smCreatedFromYearVar) {
		this.smCreatedFromYearVar = smCreatedFromYearVar;
	}

	/**
	 * Gets the sm created to year var.
	 * 
	 * @return the sm created to year var
	 */
	public String getSmCreatedToYearVar() {
		return smCreatedToYearVar;
	}

	/**
	 * Sets the sm created to year var.
	 * 
	 * @param smCreatedToYearVar
	 *            the new sm created to year var
	 */
	public void setSmCreatedToYearVar(String smCreatedToYearVar) {
		this.smCreatedToYearVar = smCreatedToYearVar;
	}

	/**
	 * Checks if is sm mandatory registration status view.
	 * 
	 * @return true, if is sm mandatory registration status view
	 */
	public boolean isSmMandatoryRegistrationStatusView() {
		return smMandatoryRegistrationStatusView;
	}

	/**
	 * Sets the sm mandatory registration status view.
	 * 
	 * @param smMandatoryRegistrationStatusView
	 *            the new sm mandatory registration status view
	 */
	public void setSmMandatoryRegistrationStatusView(
			boolean smMandatoryRegistrationStatusView) {
		this.smMandatoryRegistrationStatusView = smMandatoryRegistrationStatusView;
	}

	/**
	 * Checks if is sm mandatory district view.
	 * 
	 * @return true, if is sm mandatory district view
	 */
	public boolean isSmMandatoryDistrictView() {
		return smMandatoryDistrictView;
	}

	/**
	 * Sets the sm mandatory district view.
	 * 
	 * @param smMandatoryDistrictView
	 *            the new sm mandatory district view
	 */
	public void setSmMandatoryDistrictView(boolean smMandatoryDistrictView) {
		this.smMandatoryDistrictView = smMandatoryDistrictView;
	}

	/**
	 * Checks if is sm mandatory state view.
	 * 
	 * @return true, if is sm mandatory state view
	 */
	public boolean isSmMandatoryStateView() {
		return smMandatoryStateView;
	}

	/**
	 * Sets the sm mandatory state view.
	 * 
	 * @param smMandatoryStateView
	 *            the new sm mandatory state view
	 */
	public void setSmMandatoryStateView(boolean smMandatoryStateView) {
		this.smMandatoryStateView = smMandatoryStateView;
	}

	/**
	 * Checks if is sm mandatory from date view.
	 * 
	 * @return true, if is sm mandatory from date view
	 */
	public boolean isSmMandatoryFromDateView() {
		return smMandatoryFromDateView;
	}

	/**
	 * Sets the sm mandatory from date view.
	 * 
	 * @param smMandatoryFromDateView
	 *            the new sm mandatory from date view
	 */
	public void setSmMandatoryFromDateView(boolean smMandatoryFromDateView) {
		this.smMandatoryFromDateView = smMandatoryFromDateView;
	}

	/**
	 * Checks if is sm mandatory category view.
	 * 
	 * @return true, if is sm mandatory category view
	 */
	public boolean isSmMandatoryCategoryView() {
		return smMandatoryCategoryView;
	}

	/**
	 * Sets the sm mandatory category view.
	 * 
	 * @param smMandatoryCategoryView
	 *            the new sm mandatory category view
	 */
	public void setSmMandatoryCategoryView(boolean smMandatoryCategoryView) {
		this.smMandatoryCategoryView = smMandatoryCategoryView;
	}

	/**
	 * Checks if is sc request quote no view.
	 * 
	 * @return true, if is sc request quote no view
	 */
	public boolean isScRequestQuoteNoView() {
		return scRequestQuoteNoView;
	}

	/**
	 * Sets the sc request quote no view.
	 * 
	 * @param scRequestQuoteNoView
	 *            the new sc request quote no view
	 */
	public void setScRequestQuoteNoView(boolean scRequestQuoteNoView) {
		this.scRequestQuoteNoView = scRequestQuoteNoView;
	}

	/**
	 * Gets the sc request quote no var.
	 * 
	 * @return the sc request quote no var
	 */
	public String getScRequestQuoteNoVar() {
		return scRequestQuoteNoVar;
	}

	/**
	 * Sets the sc request quote no var.
	 * 
	 * @param scRequestQuoteNoVar
	 *            the new sc request quote no var
	 */
	public void setScRequestQuoteNoVar(String scRequestQuoteNoVar) {
		this.scRequestQuoteNoVar = scRequestQuoteNoVar;
	}

	/**
	 * Checks if is sc pid no view.
	 * 
	 * @return true, if is sc pid no view
	 */
	public boolean isScPIDNoView() {
		return scPIDNoView;
	}

	/**
	 * Sets the sc pid no view.
	 * 
	 * @param scPIDNoView
	 *            the new sc pid no view
	 */
	public void setScPIDNoView(boolean scPIDNoView) {
		this.scPIDNoView = scPIDNoView;
	}

	/**
	 * Checks if is sc contract or pkp type view.
	 * 
	 * @return true, if is sc contract or pkp type view
	 */
	public boolean isScContractOrPKPTypeView() {
		return scContractOrPKPTypeView;
	}

	/**
	 * Sets the sc contract or pkp type view.
	 * 
	 * @param scContractOrPKPTypeView
	 *            the new sc contract or pkp type view
	 */
	public void setScContractOrPKPTypeView(boolean scContractOrPKPTypeView) {
		this.scContractOrPKPTypeView = scContractOrPKPTypeView;
	}

	/**
	 * Checks if is sc contract or pkp no view.
	 * 
	 * @return true, if is sc contract or pkp no view
	 */
	public boolean isScContractOrPKPNoView() {
		return scContractOrPKPNoView;
	}

	/**
	 * Sets the sc contract or pkp no view.
	 * 
	 * @param scContractOrPKPNoView
	 *            the new sc contract or pkp no view
	 */
	public void setScContractOrPKPNoView(boolean scContractOrPKPNoView) {
		this.scContractOrPKPNoView = scContractOrPKPNoView;
	}

	/**
	 * Checks if is sc contract or pkp name view.
	 * 
	 * @return true, if is sc contract or pkp name view
	 */
	public boolean isScContractOrPKPNameView() {
		return scContractOrPKPNameView;
	}

	/**
	 * Sets the sc contract or pkp name view.
	 * 
	 * @param scContractOrPKPNameView
	 *            the new sc contract or pkp name view
	 */
	public void setScContractOrPKPNameView(boolean scContractOrPKPNameView) {
		this.scContractOrPKPNameView = scContractOrPKPNameView;
	}

	/**
	 * Gets the sc pid no var.
	 * 
	 * @return the sc pid no var
	 */
	public String getScPIDNoVar() {
		return scPIDNoVar;
	}

	/**
	 * Sets the sc pid no var.
	 * 
	 * @param scPIDNoVar
	 *            the new sc pid no var
	 */
	public void setScPIDNoVar(String scPIDNoVar) {
		this.scPIDNoVar = scPIDNoVar;
	}

	/**
	 * Gets the sc contract or pkp type var.
	 * 
	 * @return the sc contract or pkp type var
	 */
	public String getScContractOrPKPTypeVar() {
		return scContractOrPKPTypeVar;
	}

	/**
	 * Sets the sc contract or pkp type var.
	 * 
	 * @param scContractOrPKPTypeVar
	 *            the new sc contract or pkp type var
	 */
	public void setScContractOrPKPTypeVar(String scContractOrPKPTypeVar) {
		this.scContractOrPKPTypeVar = scContractOrPKPTypeVar;
	}

	/**
	 * Gets the sc contract or pkp no var.
	 * 
	 * @return the sc contract or pkp no var
	 */
	public String getScContractOrPKPNoVar() {
		return scContractOrPKPNoVar;
	}

	/**
	 * Sets the sc contract or pkp no var.
	 * 
	 * @param scContractOrPKPNoVar
	 *            the new sc contract or pkp no var
	 */
	public void setScContractOrPKPNoVar(String scContractOrPKPNoVar) {
		this.scContractOrPKPNoVar = scContractOrPKPNoVar;
	}

	/**
	 * Gets the sc contract or pkp name var.
	 * 
	 * @return the sc contract or pkp name var
	 */
	public String getScContractOrPKPNameVar() {
		return scContractOrPKPNameVar;
	}

	/**
	 * Sets the sc contract or pkp name var.
	 * 
	 * @param scContractOrPKPNameVar
	 *            the new sc contract or pkp name var
	 */
	public void setScContractOrPKPNameVar(String scContractOrPKPNameVar) {
		this.scContractOrPKPNameVar = scContractOrPKPNameVar;
	}

	/**
	 * Checks if is sc laprnpurchdefault group view.
	 * 
	 * @return true, if is sc laprnpurchdefault group view
	 */
	public boolean isScLAPRNPURCHDEFAULTGroupView() {
		return scLAPRNPURCHDEFAULTGroupView;
	}

	/**
	 * Sets the sc laprnpurchdefault group view.
	 * 
	 * @param scLAPRNPURCHDEFAULTGroupView
	 *            the new sc laprnpurchdefault group view
	 */
	public void setScLAPRNPURCHDEFAULTGroupView(
			boolean scLAPRNPURCHDEFAULTGroupView) {
		this.scLAPRNPURCHDEFAULTGroupView = scLAPRNPURCHDEFAULTGroupView;
	}

	/**
	 * Checks if is sc requestnotesummary group view.
	 * 
	 * @return true, if is sc requestnotesummary group view
	 */
	public boolean isScREQUESTNOTESUMMARYGroupView() {
		return scREQUESTNOTESUMMARYGroupView;
	}

	/**
	 * Sets the sc requestnotesummary group view.
	 * 
	 * @param scREQUESTNOTESUMMARYGroupView
	 *            the new sc requestnotesummary group view
	 */
	public void setScREQUESTNOTESUMMARYGroupView(
			boolean scREQUESTNOTESUMMARYGroupView) {
		this.scREQUESTNOTESUMMARYGroupView = scREQUESTNOTESUMMARYGroupView;
	}

	/**
	 * Gets the sc laprnpurchdefault group var.
	 * 
	 * @return the sc laprnpurchdefault group var
	 */
	public String getScLAPRNPURCHDEFAULTGroupVar() {
		return scLAPRNPURCHDEFAULTGroupVar;
	}

	/**
	 * Sets the sc laprnpurchdefault group var.
	 * 
	 * @param scLAPRNPURCHDEFAULTGroupVar
	 *            the new sc laprnpurchdefault group var
	 */
	public void setScLAPRNPURCHDEFAULTGroupVar(
			String scLAPRNPURCHDEFAULTGroupVar) {
		this.scLAPRNPURCHDEFAULTGroupVar = scLAPRNPURCHDEFAULTGroupVar;
	}

	/**
	 * Gets the sc requestnotesummary group var.
	 * 
	 * @return the sc requestnotesummary group var
	 */
	public String getScREQUESTNOTESUMMARYGroupVar() {
		return scREQUESTNOTESUMMARYGroupVar;
	}

	/**
	 * Sets the sc requestnotesummary group var.
	 * 
	 * @param scREQUESTNOTESUMMARYGroupVar
	 *            the new sc requestnotesummary group var
	 */
	public void setScREQUESTNOTESUMMARYGroupVar(
			String scREQUESTNOTESUMMARYGroupVar) {
		this.scREQUESTNOTESUMMARYGroupVar = scREQUESTNOTESUMMARYGroupVar;
	}

	/**
	 * Checks if is sc common status view.
	 * 
	 * @return true, if is sc common status view
	 */
	public boolean isScCommonStatusView() {
		return scCommonStatusView;
	}

	/**
	 * Sets the sc common status view.
	 * 
	 * @param scCommonStatusView
	 *            the new sc common status view
	 */
	public void setScCommonStatusView(boolean scCommonStatusView) {
		this.scCommonStatusView = scCommonStatusView;
	}

	/**
	 * Gets the sc common status var.
	 * 
	 * @return the sc common status var
	 */
	public String getScCommonStatusVar() {
		return scCommonStatusVar;
	}

	/**
	 * Sets the sc common status var.
	 * 
	 * @param scCommonStatusVar
	 *            the new sc common status var
	 */
	public void setScCommonStatusVar(String scCommonStatusVar) {
		this.scCommonStatusVar = scCommonStatusVar;
	}

	/**
	 * Checks if is fl group one view.
	 * 
	 * @return true, if is fl group one view
	 */
	public boolean isFlGroupOneView() {
		return flGroupOneView;
	}

	/**
	 * Sets the fl group one view.
	 * 
	 * @param flGroupOneView
	 *            the new fl group one view
	 */
	public void setFlGroupOneView(boolean flGroupOneView) {
		this.flGroupOneView = flGroupOneView;
	}

	/**
	 * Checks if is fl group two view.
	 * 
	 * @return true, if is fl group two view
	 */
	public boolean isFlGroupTwoView() {
		return flGroupTwoView;
	}

	/**
	 * Sets the fl group two view.
	 * 
	 * @param flGroupTwoView
	 *            the new fl group two view
	 */
	public void setFlGroupTwoView(boolean flGroupTwoView) {
		this.flGroupTwoView = flGroupTwoView;
	}

	/**
	 * Checks if is fl group three view.
	 * 
	 * @return true, if is fl group three view
	 */
	public boolean isFlGroupThreeView() {
		return flGroupThreeView;
	}

	/**
	 * Sets the fl group three view.
	 * 
	 * @param flGroupThreeView
	 *            the new fl group three view
	 */
	public void setFlGroupThreeView(boolean flGroupThreeView) {
		this.flGroupThreeView = flGroupThreeView;
	}

	/**
	 * Checks if is fl group four view.
	 * 
	 * @return true, if is fl group four view
	 */
	public boolean isFlGroupFourView() {
		return flGroupFourView;
	}

	/**
	 * Sets the fl group four view.
	 * 
	 * @param flGroupFourView
	 *            the new fl group four view
	 */
	public void setFlGroupFourView(boolean flGroupFourView) {
		this.flGroupFourView = flGroupFourView;
	}

	/**
	 * Checks if is fl group five view.
	 * 
	 * @return true, if is fl group five view
	 */
	public boolean isFlGroupFiveView() {
		return flGroupFiveView;
	}

	/**
	 * Sets the fl group five view.
	 * 
	 * @param flGroupFiveView
	 *            the new fl group five view
	 */
	public void setFlGroupFiveView(boolean flGroupFiveView) {
		this.flGroupFiveView = flGroupFiveView;
	}

	/**
	 * Checks if is fl group six view.
	 * 
	 * @return true, if is fl group six view
	 */
	public boolean isFlGroupSixView() {
		return flGroupSixView;
	}

	/**
	 * Sets the fl group six view.
	 * 
	 * @param flGroupSixView
	 *            the new fl group six view
	 */
	public void setFlGroupSixView(boolean flGroupSixView) {
		this.flGroupSixView = flGroupSixView;
	}

	/**
	 * Checks if is fl group seven view.
	 * 
	 * @return true, if is fl group seven view
	 */
	public boolean isFlGroupSevenView() {
		return flGroupSevenView;
	}

	/**
	 * Sets the fl group seven view.
	 * 
	 * @param flGroupSevenView
	 *            the new fl group seven view
	 */
	public void setFlGroupSevenView(boolean flGroupSevenView) {
		this.flGroupSevenView = flGroupSevenView;
	}

	/**
	 * Checks if is fl group eight view.
	 * 
	 * @return true, if is fl group eight view
	 */
	public boolean isFlGroupEightView() {
		return flGroupEightView;
	}

	/**
	 * Sets the fl group eight view.
	 * 
	 * @param flGroupEightView
	 *            the new fl group eight view
	 */
	public void setFlGroupEightView(boolean flGroupEightView) {
		this.flGroupEightView = flGroupEightView;
	}

	/**
	 * Checks if is fl group nine view.
	 * 
	 * @return true, if is fl group nine view
	 */
	public boolean isFlGroupNineView() {
		return flGroupNineView;
	}

	/**
	 * Sets the fl group nine view.
	 * 
	 * @param flGroupNineView
	 *            the new fl group nine view
	 */
	public void setFlGroupNineView(boolean flGroupNineView) {
		this.flGroupNineView = flGroupNineView;
	}

	/**
	 * Checks if is fl group ten view.
	 * 
	 * @return true, if is fl group ten view
	 */
	public boolean isFlGroupTenView() {
		return flGroupTenView;
	}

	/**
	 * Sets the fl group ten view.
	 * 
	 * @param flGroupTenView
	 *            the new fl group ten view
	 */
	public void setFlGroupTenView(boolean flGroupTenView) {
		this.flGroupTenView = flGroupTenView;
	}

	/**
	 * Checks if is fl group eleven view.
	 * 
	 * @return true, if is fl group eleven view
	 */
	public boolean isFlGroupElevenView() {
		return flGroupElevenView;
	}

	/**
	 * Sets the fl group eleven view.
	 * 
	 * @param flGroupElevenView
	 *            the new fl group eleven view
	 */
	public void setFlGroupElevenView(boolean flGroupElevenView) {
		this.flGroupElevenView = flGroupElevenView;
	}

	/**
	 * Checks if is fl group twelve view.
	 * 
	 * @return true, if is fl group twelve view
	 */
	public boolean isFlGroupTwelveView() {
		return flGroupTwelveView;
	}

	/**
	 * Sets the fl group twelve view.
	 * 
	 * @param flGroupTwelveView
	 *            the new fl group twelve view
	 */
	public void setFlGroupTwelveView(boolean flGroupTwelveView) {
		this.flGroupTwelveView = flGroupTwelveView;
	}

	/**
	 * Checks if is fl group thirteen view.
	 * 
	 * @return true, if is fl group thirteen view
	 */
	public boolean isFlGroupThirteenView() {
		return flGroupThirteenView;
	}

	/**
	 * Sets the fl group thirteen view.
	 * 
	 * @param flGroupThirteenView
	 *            the new fl group thirteen view
	 */
	public void setFlGroupThirteenView(boolean flGroupThirteenView) {
		this.flGroupThirteenView = flGroupThirteenView;
	}

	/**
	 * Checks if is fl group fourteen view.
	 * 
	 * @return true, if is fl group fourteen view
	 */
	public boolean isFlGroupFourteenView() {
		return flGroupFourteenView;
	}

	/**
	 * Sets the fl group fourteen view.
	 * 
	 * @param flGroupFourteenView
	 *            the new fl group fourteen view
	 */
	public void setFlGroupFourteenView(boolean flGroupFourteenView) {
		this.flGroupFourteenView = flGroupFourteenView;
	}

	/**
	 * Checks if is fl group fifteen view.
	 * 
	 * @return true, if is fl group fifteen view
	 */
	public boolean isFlGroupFifteenView() {
		return flGroupFifteenView;
	}

	/**
	 * Sets the fl group fifteen view.
	 * 
	 * @param flGroupFifteenView
	 *            the new fl group fifteen view
	 */
	public void setFlGroupFifteenView(boolean flGroupFifteenView) {
		this.flGroupFifteenView = flGroupFifteenView;
	}

	/**
	 * Checks if is fl group sixteen view.
	 * 
	 * @return true, if is fl group sixteen view
	 */
	public boolean isFlGroupSixteenView() {
		return flGroupSixteenView;
	}

	/**
	 * Sets the fl group sixteen view.
	 * 
	 * @param flGroupSixteenView
	 *            the new fl group sixteen view
	 */
	public void setFlGroupSixteenView(boolean flGroupSixteenView) {
		this.flGroupSixteenView = flGroupSixteenView;
	}

	/**
	 * Gets the fl group one var.
	 * 
	 * @return the fl group one var
	 */
	public String getFlGroupOneVar() {
		return flGroupOneVar;
	}

	/**
	 * Sets the fl group one var.
	 * 
	 * @param flGroupOneVar
	 *            the new fl group one var
	 */
	public void setFlGroupOneVar(String flGroupOneVar) {
		this.flGroupOneVar = flGroupOneVar;
	}

	/**
	 * Gets the fl group two var.
	 * 
	 * @return the fl group two var
	 */
	public String getFlGroupTwoVar() {
		return flGroupTwoVar;
	}

	/**
	 * Sets the fl group two var.
	 * 
	 * @param flGroupTwoVar
	 *            the new fl group two var
	 */
	public void setFlGroupTwoVar(String flGroupTwoVar) {
		this.flGroupTwoVar = flGroupTwoVar;
	}

	/**
	 * Gets the fl group three var.
	 * 
	 * @return the fl group three var
	 */
	public String getFlGroupThreeVar() {
		return flGroupThreeVar;
	}

	/**
	 * Sets the fl group three var.
	 * 
	 * @param flGroupThreeVar
	 *            the new fl group three var
	 */
	public void setFlGroupThreeVar(String flGroupThreeVar) {
		this.flGroupThreeVar = flGroupThreeVar;
	}

	/**
	 * Gets the fl group four var.
	 * 
	 * @return the fl group four var
	 */
	public String getFlGroupFourVar() {
		return flGroupFourVar;
	}

	/**
	 * Sets the fl group four var.
	 * 
	 * @param flGroupFourVar
	 *            the new fl group four var
	 */
	public void setFlGroupFourVar(String flGroupFourVar) {
		this.flGroupFourVar = flGroupFourVar;
	}

	/**
	 * Gets the fl group five var.
	 * 
	 * @return the fl group five var
	 */
	public String getFlGroupFiveVar() {
		return flGroupFiveVar;
	}

	/**
	 * Sets the fl group five var.
	 * 
	 * @param flGroupFiveVar
	 *            the new fl group five var
	 */
	public void setFlGroupFiveVar(String flGroupFiveVar) {
		this.flGroupFiveVar = flGroupFiveVar;
	}

	/**
	 * Gets the fl group six var.
	 * 
	 * @return the fl group six var
	 */
	public String getFlGroupSixVar() {
		return flGroupSixVar;
	}

	/**
	 * Sets the fl group six var.
	 * 
	 * @param flGroupSixVar
	 *            the new fl group six var
	 */
	public void setFlGroupSixVar(String flGroupSixVar) {
		this.flGroupSixVar = flGroupSixVar;
	}

	/**
	 * Gets the fl group seven var.
	 * 
	 * @return the fl group seven var
	 */
	public String getFlGroupSevenVar() {
		return flGroupSevenVar;
	}

	/**
	 * Sets the fl group seven var.
	 * 
	 * @param flGroupSevenVar
	 *            the new fl group seven var
	 */
	public void setFlGroupSevenVar(String flGroupSevenVar) {
		this.flGroupSevenVar = flGroupSevenVar;
	}

	/**
	 * Gets the fl group eight var.
	 * 
	 * @return the fl group eight var
	 */
	public String getFlGroupEightVar() {
		return flGroupEightVar;
	}

	/**
	 * Sets the fl group eight var.
	 * 
	 * @param flGroupEightVar
	 *            the new fl group eight var
	 */
	public void setFlGroupEightVar(String flGroupEightVar) {
		this.flGroupEightVar = flGroupEightVar;
	}

	/**
	 * Gets the fl group nine var.
	 * 
	 * @return the fl group nine var
	 */
	public String getFlGroupNineVar() {
		return flGroupNineVar;
	}

	/**
	 * Sets the fl group nine var.
	 * 
	 * @param flGroupNineVar
	 *            the new fl group nine var
	 */
	public void setFlGroupNineVar(String flGroupNineVar) {
		this.flGroupNineVar = flGroupNineVar;
	}

	/**
	 * Gets the fl group ten var.
	 * 
	 * @return the fl group ten var
	 */
	public String getFlGroupTenVar() {
		return flGroupTenVar;
	}

	/**
	 * Sets the fl group ten var.
	 * 
	 * @param flGroupTenVar
	 *            the new fl group ten var
	 */
	public void setFlGroupTenVar(String flGroupTenVar) {
		this.flGroupTenVar = flGroupTenVar;
	}

	/**
	 * Gets the fl group eleven var.
	 * 
	 * @return the fl group eleven var
	 */
	public String getFlGroupElevenVar() {
		return flGroupElevenVar;
	}

	/**
	 * Sets the fl group eleven var.
	 * 
	 * @param flGroupElevenVar
	 *            the new fl group eleven var
	 */
	public void setFlGroupElevenVar(String flGroupElevenVar) {
		this.flGroupElevenVar = flGroupElevenVar;
	}

	/**
	 * Gets the fl group twelve var.
	 * 
	 * @return the fl group twelve var
	 */
	public String getFlGroupTwelveVar() {
		return flGroupTwelveVar;
	}

	/**
	 * Sets the fl group twelve var.
	 * 
	 * @param flGroupTwelveVar
	 *            the new fl group twelve var
	 */
	public void setFlGroupTwelveVar(String flGroupTwelveVar) {
		this.flGroupTwelveVar = flGroupTwelveVar;
	}

	/**
	 * Gets the fl group thirteen var.
	 * 
	 * @return the fl group thirteen var
	 */
	public String getFlGroupThirteenVar() {
		return flGroupThirteenVar;
	}

	/**
	 * Sets the fl group thirteen var.
	 * 
	 * @param flGroupThirteenVar
	 *            the new fl group thirteen var
	 */
	public void setFlGroupThirteenVar(String flGroupThirteenVar) {
		this.flGroupThirteenVar = flGroupThirteenVar;
	}

	/**
	 * Gets the fl group fourteen var.
	 * 
	 * @return the fl group fourteen var
	 */
	public String getFlGroupFourteenVar() {
		return flGroupFourteenVar;
	}

	/**
	 * Sets the fl group fourteen var.
	 * 
	 * @param flGroupFourteenVar
	 *            the new fl group fourteen var
	 */
	public void setFlGroupFourteenVar(String flGroupFourteenVar) {
		this.flGroupFourteenVar = flGroupFourteenVar;
	}

	/**
	 * Gets the fl group fifteen var.
	 * 
	 * @return the fl group fifteen var
	 */
	public String getFlGroupFifteenVar() {
		return flGroupFifteenVar;
	}

	/**
	 * Sets the fl group fifteen var.
	 * 
	 * @param flGroupFifteenVar
	 *            the new fl group fifteen var
	 */
	public void setFlGroupFifteenVar(String flGroupFifteenVar) {
		this.flGroupFifteenVar = flGroupFifteenVar;
	}

	/**
	 * Gets the fl group sixteen var.
	 * 
	 * @return the fl group sixteen var
	 */
	public String getFlGroupSixteenVar() {
		return flGroupSixteenVar;
	}

	/**
	 * Sets the fl group sixteen var.
	 * 
	 * @param flGroupSixteenVar
	 *            the new fl group sixteen var
	 */
	public void setFlGroupSixteenVar(String flGroupSixteenVar) {
		this.flGroupSixteenVar = flGroupSixteenVar;
	}

	/**
	 * Checks if is ct complete view.
	 * 
	 * @return true, if is ct complete view
	 */
	public boolean isCtCompleteView() {
		return ctCompleteView;
	}

	/**
	 * Sets the ct complete view.
	 * 
	 * @param ctCompleteView
	 *            the new ct complete view
	 */
	public void setCtCompleteView(boolean ctCompleteView) {
		this.ctCompleteView = ctCompleteView;
	}

	/**
	 * Gets the ct complete var.
	 * 
	 * @return the ct complete var
	 */
	public String getCtCompleteVar() {
		return ctCompleteVar;
	}

	/**
	 * Sets the ct complete var.
	 * 
	 * @param ctCompleteVar
	 *            the new ct complete var
	 */
	public void setCtCompleteVar(String ctCompleteVar) {
		this.ctCompleteVar = ctCompleteVar;
	}

	/**
	 * Checks if is ct contr montr status view.
	 * 
	 * @return true, if is ct contr montr status view
	 */
	public boolean isCtContrMontrStatusView() {
		return ctContrMontrStatusView;
	}

	/**
	 * Sets the ct contr montr status view.
	 * 
	 * @param ctContrMontrStatusView
	 *            the new ct contr montr status view
	 */
	public void setCtContrMontrStatusView(boolean ctContrMontrStatusView) {
		this.ctContrMontrStatusView = ctContrMontrStatusView;
	}

	/**
	 * Gets the ct contr montr status var.
	 * 
	 * @return the ct contr montr status var
	 */
	public String getCtContrMontrStatusVar() {
		return ctContrMontrStatusVar;
	}

	/**
	 * Sets the ct contr montr status var.
	 * 
	 * @param ctContrMontrStatusVar
	 *            the new ct contr montr status var
	 */
	public void setCtContrMontrStatusVar(String ctContrMontrStatusVar) {
		this.ctContrMontrStatusVar = ctContrMontrStatusVar;
	}

	/**
	 * Gets the ct hidden param group var.
	 * 
	 * @return the ct hidden param group var
	 */
	public String getCtHiddenParamGroupVar() {
		return ctHiddenParamGroupVar;
	}

	/**
	 * Sets the ct hidden param group var.
	 * 
	 * @param ctHiddenParamGroupVar
	 *            the new ct hidden param group var
	 */
	public void setCtHiddenParamGroupVar(String ctHiddenParamGroupVar) {
		this.ctHiddenParamGroupVar = ctHiddenParamGroupVar;
	}

	/**
	 * Checks if is ct contract no view.
	 * 
	 * @return true, if is ct contract no view
	 */
	public boolean isCtContractNoView() {
		return ctContractNoView;
	}

	/**
	 * Sets the ct contract no view.
	 * 
	 * @param ctContractNoView
	 *            the new ct contract no view
	 */
	public void setCtContractNoView(boolean ctContractNoView) {
		this.ctContractNoView = ctContractNoView;
	}

	/**
	 * Checks if is ct agreement view.
	 * 
	 * @return true, if is ct agreement view
	 */
	public boolean isCtAgreementView() {
		return ctAgreementView;
	}

	/**
	 * Sets the ct agreement view.
	 * 
	 * @param ctAgreementView
	 *            the new ct agreement view
	 */
	public void setCtAgreementView(boolean ctAgreementView) {
		this.ctAgreementView = ctAgreementView;
	}

	/**
	 * Checks if is ct status view.
	 * 
	 * @return true, if is ct status view
	 */
	public boolean isCtStatusView() {
		return ctStatusView;
	}

	/**
	 * Sets the ct status view.
	 * 
	 * @param ctStatusView
	 *            the new ct status view
	 */
	public void setCtStatusView(boolean ctStatusView) {
		this.ctStatusView = ctStatusView;
	}

	/**
	 * Gets the ct contract no var.
	 * 
	 * @return the ct contract no var
	 */
	public String getCtContractNoVar() {
		return ctContractNoVar;
	}

	/**
	 * Sets the ct contract no var.
	 * 
	 * @param ctContractNoVar
	 *            the new ct contract no var
	 */
	public void setCtContractNoVar(String ctContractNoVar) {
		this.ctContractNoVar = ctContractNoVar;
	}

	/**
	 * Gets the ct agreement var.
	 * 
	 * @return the ct agreement var
	 */
	public String getCtAgreementVar() {
		return ctAgreementVar;
	}

	/**
	 * Sets the ct agreement var.
	 * 
	 * @param ctAgreementVar
	 *            the new ct agreement var
	 */
	public void setCtAgreementVar(String ctAgreementVar) {
		this.ctAgreementVar = ctAgreementVar;
	}

	/**
	 * Gets the ct status var.
	 * 
	 * @return the ct status var
	 */
	public String getCtStatusVar() {
		return ctStatusVar;
	}

	/**
	 * Sets the ct status var.
	 * 
	 * @param ctStatusVar
	 *            the new ct status var
	 */
	public void setCtStatusVar(String ctStatusVar) {
		this.ctStatusVar = ctStatusVar;
	}

	/**
	 * Checks if is sc prc rpt group view.
	 * 
	 * @return true, if is sc prc rpt group view
	 */
	public boolean isScPrcRptGroupView() {
		return scPrcRptGroupView;
	}

	/**
	 * Sets the sc prc rpt group view.
	 * 
	 * @param scPrcRptGroupView
	 *            the new sc prc rpt group view
	 */
	public void setScPrcRptGroupView(boolean scPrcRptGroupView) {
		this.scPrcRptGroupView = scPrcRptGroupView;
	}

	/**
	 * Checks if is sc pi dtls prc rpt group view.
	 * 
	 * @return true, if is sc pi dtls prc rpt group view
	 */
	public boolean isScPIDtlsPrcRptGroupView() {
		return scPIDtlsPrcRptGroupView;
	}

	/**
	 * Sets the sc pi dtls prc rpt group view.
	 * 
	 * @param scPIDtlsPrcRptGroupView
	 *            the new sc pi dtls prc rpt group view
	 */
	public void setScPIDtlsPrcRptGroupView(boolean scPIDtlsPrcRptGroupView) {
		this.scPIDtlsPrcRptGroupView = scPIDtlsPrcRptGroupView;
	}

	/**
	 * Checks if is sc procurement type view.
	 * 
	 * @return true, if is sc procurement type view
	 */
	public boolean isScProcurementTypeView() {
		return scProcurementTypeView;
	}

	/**
	 * Sets the sc procurement type view.
	 * 
	 * @param scProcurementTypeView
	 *            the new sc procurement type view
	 */
	public void setScProcurementTypeView(boolean scProcurementTypeView) {
		this.scProcurementTypeView = scProcurementTypeView;
	}

	/**
	 * Checks if is sc process rpt quotation no view.
	 * 
	 * @return true, if is sc process rpt quotation no view
	 */
	public boolean isScProcessRptQuotationNoView() {
		return scProcessRptQuotationNoView;
	}

	/**
	 * Sets the sc process rpt quotation no view.
	 * 
	 * @param scProcessRptQuotationNoView
	 *            the new sc process rpt quotation no view
	 */
	public void setScProcessRptQuotationNoView(
			boolean scProcessRptQuotationNoView) {
		this.scProcessRptQuotationNoView = scProcessRptQuotationNoView;
	}

	/**
	 * Gets the sc prc rpt group var.
	 * 
	 * @return the sc prc rpt group var
	 */
	public String getScPrcRptGroupVar() {
		return scPrcRptGroupVar;
	}

	/**
	 * Sets the sc prc rpt group var.
	 * 
	 * @param scPrcRptGroupVar
	 *            the new sc prc rpt group var
	 */
	public void setScPrcRptGroupVar(String scPrcRptGroupVar) {
		this.scPrcRptGroupVar = scPrcRptGroupVar;
	}

	/**
	 * Gets the sc pi dtls prc rpt group var.
	 * 
	 * @return the sc pi dtls prc rpt group var
	 */
	public String getScPIDtlsPrcRptGroupVar() {
		return scPIDtlsPrcRptGroupVar;
	}

	/**
	 * Sets the sc pi dtls prc rpt group var.
	 * 
	 * @param scPIDtlsPrcRptGroupVar
	 *            the new sc pi dtls prc rpt group var
	 */
	public void setScPIDtlsPrcRptGroupVar(String scPIDtlsPrcRptGroupVar) {
		this.scPIDtlsPrcRptGroupVar = scPIDtlsPrcRptGroupVar;
	}

	/**
	 * Gets the sc procurement type var.
	 * 
	 * @return the sc procurement type var
	 */
	public String getScProcurementTypeVar() {
		return scProcurementTypeVar;
	}

	/**
	 * Sets the sc procurement type var.
	 * 
	 * @param scProcurementTypeVar
	 *            the new sc procurement type var
	 */
	public void setScProcurementTypeVar(String scProcurementTypeVar) {
		this.scProcurementTypeVar = scProcurementTypeVar;
	}

	/**
	 * Gets the sc process rpt quotation no var.
	 * 
	 * @return the sc process rpt quotation no var
	 */
	public String getScProcessRptQuotationNoVar() {
		return scProcessRptQuotationNoVar;
	}

	/**
	 * Sets the sc process rpt quotation no var.
	 * 
	 * @param scProcessRptQuotationNoVar
	 *            the new sc process rpt quotation no var
	 */
	public void setScProcessRptQuotationNoVar(String scProcessRptQuotationNoVar) {
		this.scProcessRptQuotationNoVar = scProcessRptQuotationNoVar;
	}

	/**
	 * Checks if is sc sone crtd from date view.
	 * 
	 * @return true, if is sc sone crtd from date view
	 */
	public boolean isScSoneCrtdFromDateView() {
		return scSoneCrtdFromDateView;
	}

	/**
	 * Sets the sc sone crtd from date view.
	 * 
	 * @param scSoneCrtdFromDateView
	 *            the new sc sone crtd from date view
	 */
	public void setScSoneCrtdFromDateView(boolean scSoneCrtdFromDateView) {
		this.scSoneCrtdFromDateView = scSoneCrtdFromDateView;
	}

	/**
	 * Checks if is sc requisener id view.
	 * 
	 * @return true, if is sc requisener id view
	 */
	public boolean isScRequisenerIdView() {
		return scRequisenerIdView;
	}

	/**
	 * Sets the sc requisener id view.
	 * 
	 * @param scRequisenerIdView
	 *            the new sc requisener id view
	 */
	public void setScRequisenerIdView(boolean scRequisenerIdView) {
		this.scRequisenerIdView = scRequisenerIdView;
	}

	/**
	 * Checks if is sc supplier view.
	 * 
	 * @return true, if is sc supplier view
	 */
	public boolean isScSupplierView() {
		return scSupplierView;
	}

	/**
	 * Sets the sc supplier view.
	 * 
	 * @param scSupplierView
	 *            the new sc supplier view
	 */
	public void setScSupplierView(boolean scSupplierView) {
		this.scSupplierView = scSupplierView;
	}

	/**
	 * Checks if is sc p ino view.
	 * 
	 * @return true, if is sc p ino view
	 */
	public boolean isScPInoView() {
		return scPInoView;
	}

	/**
	 * Sets the sc p ino view.
	 * 
	 * @param scPInoView
	 *            the new sc p ino view
	 */
	public void setScPInoView(boolean scPInoView) {
		this.scPInoView = scPInoView;
	}

	/**
	 * Checks if is sc sone status view.
	 * 
	 * @return true, if is sc sone status view
	 */
	public boolean isScSoneStatusView() {
		return scSoneStatusView;
	}

	/**
	 * Sets the sc sone status view.
	 * 
	 * @param scSoneStatusView
	 *            the new sc sone status view
	 */
	public void setScSoneStatusView(boolean scSoneStatusView) {
		this.scSoneStatusView = scSoneStatusView;
	}

	/**
	 * Gets the sc sone crtd from date var.
	 * 
	 * @return the sc sone crtd from date var
	 */
	public String getScSoneCrtdFromDateVar() {
		return scSoneCrtdFromDateVar;
	}

	/**
	 * Sets the sc sone crtd from date var.
	 * 
	 * @param scSoneCrtdFromDateVar
	 *            the new sc sone crtd from date var
	 */
	public void setScSoneCrtdFromDateVar(String scSoneCrtdFromDateVar) {
		this.scSoneCrtdFromDateVar = scSoneCrtdFromDateVar;
	}

	/**
	 * Gets the sc sone crtd to date var.
	 * 
	 * @return the sc sone crtd to date var
	 */
	public String getScSoneCrtdToDateVar() {
		return scSoneCrtdToDateVar;
	}

	/**
	 * Sets the sc sone crtd to date var.
	 * 
	 * @param scSoneCrtdToDateVar
	 *            the new sc sone crtd to date var
	 */
	public void setScSoneCrtdToDateVar(String scSoneCrtdToDateVar) {
		this.scSoneCrtdToDateVar = scSoneCrtdToDateVar;
	}

	/**
	 * Gets the sc requisener id var.
	 * 
	 * @return the sc requisener id var
	 */
	public String getScRequisenerIdVar() {
		return scRequisenerIdVar;
	}

	/**
	 * Sets the sc requisener id var.
	 * 
	 * @param scRequisenerIdVar
	 *            the new sc requisener id var
	 */
	public void setScRequisenerIdVar(String scRequisenerIdVar) {
		this.scRequisenerIdVar = scRequisenerIdVar;
	}

	/**
	 * Gets the sc supplier var.
	 * 
	 * @return the sc supplier var
	 */
	public String getScSupplierVar() {
		return scSupplierVar;
	}

	/**
	 * Sets the sc supplier var.
	 * 
	 * @param scSupplierVar
	 *            the new sc supplier var
	 */
	public void setScSupplierVar(String scSupplierVar) {
		this.scSupplierVar = scSupplierVar;
	}

	/**
	 * Gets the sc p ino var.
	 * 
	 * @return the sc p ino var
	 */
	public String getScPInoVar() {
		return scPInoVar;
	}

	/**
	 * Sets the sc p ino var.
	 * 
	 * @param scPInoVar
	 *            the new sc p ino var
	 */
	public void setScPInoVar(String scPInoVar) {
		this.scPInoVar = scPInoVar;
	}

	/**
	 * Gets the sc sone status var.
	 * 
	 * @return the sc sone status var
	 */
	public String getScSoneStatusVar() {
		return scSoneStatusVar;
	}

	/**
	 * Sets the sc sone status var.
	 * 
	 * @param scSoneStatusVar
	 *            the new sc sone status var
	 */
	public void setScSoneStatusVar(String scSoneStatusVar) {
		this.scSoneStatusVar = scSoneStatusVar;
	}

	/**
	 * Gets the sc user access role var.
	 * 
	 * @return the sc user access role var
	 */
	public String getScUserAccessRoleVar() {
		return scUserAccessRoleVar;
	}

	/**
	 * Sets the sc user access role var.
	 * 
	 * @param scUserAccessRoleVar
	 *            the new sc user access role var
	 */
	public void setScUserAccessRoleVar(String scUserAccessRoleVar) {
		this.scUserAccessRoleVar = scUserAccessRoleVar;
	}

	/**
	 * Checks if is sc procure all edit view.
	 * 
	 * @return true, if is sc procure all edit view
	 */
	public boolean isScProcureAllEditView() {
		return scProcureAllEditView;
	}

	/**
	 * Sets the sc procure all edit view.
	 * 
	 * @param scProcureAllEditView
	 *            the new sc procure all edit view
	 */
	public void setScProcureAllEditView(boolean scProcureAllEditView) {
		this.scProcureAllEditView = scProcureAllEditView;
	}

	/**
	 * Checks if is sc procure quote edit view.
	 * 
	 * @return true, if is sc procure quote edit view
	 */
	public boolean isScProcureQuoteEditView() {
		return scProcureQuoteEditView;
	}

	/**
	 * Sets the sc procure quote edit view.
	 * 
	 * @param scProcureQuoteEditView
	 *            the new sc procure quote edit view
	 */
	public void setScProcureQuoteEditView(boolean scProcureQuoteEditView) {
		this.scProcureQuoteEditView = scProcureQuoteEditView;
	}

	/**
	 * Checks if is sc procure tender edit view.
	 * 
	 * @return true, if is sc procure tender edit view
	 */
	public boolean isScProcureTenderEditView() {
		return scProcureTenderEditView;
	}

	/**
	 * Sets the sc procure tender edit view.
	 * 
	 * @param scProcureTenderEditView
	 *            the new sc procure tender edit view
	 */
	public void setScProcureTenderEditView(boolean scProcureTenderEditView) {
		this.scProcureTenderEditView = scProcureTenderEditView;
	}

	/**
	 * Checks if is sc procure restrctd edit view.
	 * 
	 * @return true, if is sc procure restrctd edit view
	 */
	public boolean isScProcureRestrctdEditView() {
		return scProcureRestrctdEditView;
	}

	/**
	 * Sets the sc procure restrctd edit view.
	 * 
	 * @param scProcureRestrctdEditView
	 *            the new sc procure restrctd edit view
	 */
	public void setScProcureRestrctdEditView(boolean scProcureRestrctdEditView) {
		this.scProcureRestrctdEditView = scProcureRestrctdEditView;
	}

	/**
	 * Gets the sc procure method chk var.
	 * 
	 * @return the sc procure method chk var
	 */
	public String getScProcureMethodChkVar() {
		return scProcureMethodChkVar;
	}

	/**
	 * Sets the sc procure method chk var.
	 * 
	 * @param scProcureMethodChkVar
	 *            the new sc procure method chk var
	 */
	public void setScProcureMethodChkVar(String scProcureMethodChkVar) {
		this.scProcureMethodChkVar = scProcureMethodChkVar;
	}

	/**
	 * Gets the sc created from dt var.
	 * 
	 * @return the sc created from dt var
	 */
	public String getScCreatedFromDtVar() {
		return scCreatedFromDtVar;
	}

	/**
	 * Sets the sc created from dt var.
	 * 
	 * @param scCreatedFromDtVar
	 *            the new sc created from dt var
	 */
	public void setScCreatedFromDtVar(String scCreatedFromDtVar) {
		this.scCreatedFromDtVar = scCreatedFromDtVar;
	}

	/**
	 * Gets the sc created to dt var.
	 * 
	 * @return the sc created to dt var
	 */
	public String getScCreatedToDtVar() {
		return scCreatedToDtVar;
	}

	/**
	 * Sets the sc created to dt var.
	 * 
	 * @param scCreatedToDtVar
	 *            the new sc created to dt var
	 */
	public void setScCreatedToDtVar(String scCreatedToDtVar) {
		this.scCreatedToDtVar = scCreatedToDtVar;
	}

	/**
	 * Gets the sc publish from date var.
	 * 
	 * @return the sc publish from date var
	 */
	public String getScPublishFromDateVar() {
		return scPublishFromDateVar;
	}

	/**
	 * Sets the sc publish from date var.
	 * 
	 * @param scPublishFromDateVar
	 *            the new sc publish from date var
	 */
	public void setScPublishFromDateVar(String scPublishFromDateVar) {
		this.scPublishFromDateVar = scPublishFromDateVar;
	}

	/**
	 * Gets the sc publish to date var.
	 * 
	 * @return the sc publish to date var
	 */
	public String getScPublishToDateVar() {
		return scPublishToDateVar;
	}

	/**
	 * Sets the sc publish to date var.
	 * 
	 * @param scPublishToDateVar
	 *            the new sc publish to date var
	 */
	public void setScPublishToDateVar(String scPublishToDateVar) {
		this.scPublishToDateVar = scPublishToDateVar;
	}

	/**
	 * Gets the sc status var.
	 * 
	 * @return the sc status var
	 */
	public String getScStatusVar() {
		return scStatusVar;
	}

	/**
	 * Sets the sc status var.
	 * 
	 * @param scStatusVar
	 *            the new sc status var
	 */
	public void setScStatusVar(String scStatusVar) {
		this.scStatusVar = scStatusVar;
	}

	/**
	 * Gets the sc ptj var.
	 * 
	 * @return the sc ptj var
	 */
	public String getScPtjVar() {
		return scPtjVar;
	}

	/**
	 * Sets the sc ptj var.
	 * 
	 * @param scPtjVar
	 *            the new sc ptj var
	 */
	public void setScPtjVar(String scPtjVar) {
		this.scPtjVar = scPtjVar;
	}

	/**
	 * Gets the sc desk officer var.
	 * 
	 * @return the sc desk officer var
	 */
	public String getScDeskOfficerVar() {
		return scDeskOfficerVar;
	}

	/**
	 * Sets the sc desk officer var.
	 * 
	 * @param scDeskOfficerVar
	 *            the new sc desk officer var
	 */
	public void setScDeskOfficerVar(String scDeskOfficerVar) {
		this.scDeskOfficerVar = scDeskOfficerVar;
	}

	/**
	 * Checks if is sc publish date view.
	 * 
	 * @return true, if is sc publish date view
	 */
	public boolean isScPublishDateView() {
		return scPublishDateView;
	}

	/**
	 * Sets the sc publish date view.
	 * 
	 * @param scPublishDateView
	 *            the new sc publish date view
	 */
	public void setScPublishDateView(boolean scPublishDateView) {
		this.scPublishDateView = scPublishDateView;
	}

	/**
	 * Checks if is sc created from view.
	 * 
	 * @return true, if is sc created from view
	 */
	public boolean isScCreatedFromView() {
		return scCreatedFromView;
	}

	/**
	 * Sets the sc created from view.
	 * 
	 * @param scCreatedFromView
	 *            the new sc created from view
	 */
	public void setScCreatedFromView(boolean scCreatedFromView) {
		this.scCreatedFromView = scCreatedFromView;
	}

	/**
	 * Checks if is sc status view.
	 * 
	 * @return true, if is sc status view
	 */
	public boolean isScStatusView() {
		return scStatusView;
	}

	/**
	 * Sets the sc status view.
	 * 
	 * @param scStatusView
	 *            the new sc status view
	 */
	public void setScStatusView(boolean scStatusView) {
		this.scStatusView = scStatusView;
	}

	/**
	 * Checks if is sc ptj view.
	 * 
	 * @return true, if is sc ptj view
	 */
	public boolean isScPtjView() {
		return scPtjView;
	}

	/**
	 * Sets the sc ptj view.
	 * 
	 * @param scPtjView
	 *            the new sc ptj view
	 */
	public void setScPtjView(boolean scPtjView) {
		this.scPtjView = scPtjView;
	}

	/**
	 * Checks if is sc desk officer view.
	 * 
	 * @return true, if is sc desk officer view
	 */
	public boolean isScDeskOfficerView() {
		return scDeskOfficerView;
	}

	/**
	 * Sets the sc desk officer view.
	 * 
	 * @param scDeskOfficerView
	 *            the new sc desk officer view
	 */
	public void setScDeskOfficerView(boolean scDeskOfficerView) {
		this.scDeskOfficerView = scDeskOfficerView;
	}

	/**
	 * Checks if is sc rstrctd chk view.
	 * 
	 * @return true, if is sc rstrctd chk view
	 */
	public boolean isScRstrctdChkView() {
		return scRstrctdChkView;
	}

	/**
	 * Sets the sc rstrctd chk view.
	 * 
	 * @param scRstrctdChkView
	 *            the new sc rstrctd chk view
	 */
	public void setScRstrctdChkView(boolean scRstrctdChkView) {
		this.scRstrctdChkView = scRstrctdChkView;
	}

	/**
	 * Checks if is sc tender chk view.
	 * 
	 * @return true, if is sc tender chk view
	 */
	public boolean isScTenderChkView() {
		return scTenderChkView;
	}

	/**
	 * Sets the sc tender chk view.
	 * 
	 * @param scTenderChkView
	 *            the new sc tender chk view
	 */
	public void setScTenderChkView(boolean scTenderChkView) {
		this.scTenderChkView = scTenderChkView;
	}

	/**
	 * Checks if is sc quotation chk view.
	 * 
	 * @return true, if is sc quotation chk view
	 */
	public boolean isScQuotationChkView() {
		return scQuotationChkView;
	}

	/**
	 * Sets the sc quotation chk view.
	 * 
	 * @param scQuotationChkView
	 *            the new sc quotation chk view
	 */
	public void setScQuotationChkView(boolean scQuotationChkView) {
		this.scQuotationChkView = scQuotationChkView;
	}

	/**
	 * Checks if is sc procure all chk view.
	 * 
	 * @return true, if is sc procure all chk view
	 */
	public boolean isScProcureAllChkView() {
		return scProcureAllChkView;
	}

	/**
	 * Sets the sc procure all chk view.
	 * 
	 * @param scProcureAllChkView
	 *            the new sc procure all chk view
	 */
	public void setScProcureAllChkView(boolean scProcureAllChkView) {
		this.scProcureAllChkView = scProcureAllChkView;
	}

	/**
	 * Gets the report value id var.
	 * 
	 * @return the report value id var
	 */
	public String getReportValueIdVar() {
		return reportValueIdVar;
	}

	/**
	 * Sets the report value id var.
	 * 
	 * @param reportValueIdVar
	 *            the new report value id var
	 */
	public void setReportValueIdVar(String reportValueIdVar) {
		this.reportValueIdVar = reportValueIdVar;
	}

	/**
	 * Gets the user id var.
	 * 
	 * @return the user id var
	 */
	public String getUserIdVar() {
		return userIdVar;
	}

	/**
	 * Sets the user id var.
	 * 
	 * @param userIdVar
	 *            the new user id var
	 */
	public void setUserIdVar(String userIdVar) {
		this.userIdVar = userIdVar;
	}

	/**
	 * Checks if is fl purchase order no view.
	 * 
	 * @return true, if is fl purchase order no view
	 */
	public boolean isFlPurchaseOrderNoView() {
		return flPurchaseOrderNoView;
	}

	/**
	 * Sets the fl purchase order no view.
	 * 
	 * @param flPurchaseOrderNoView
	 *            the new fl purchase order no view
	 */
	public void setFlPurchaseOrderNoView(boolean flPurchaseOrderNoView) {
		this.flPurchaseOrderNoView = flPurchaseOrderNoView;
	}

	/**
	 * Gets the fl purchase order no var.
	 * 
	 * @return the fl purchase order no var
	 */
	public String getFlPurchaseOrderNoVar() {
		return flPurchaseOrderNoVar;
	}

	/**
	 * Sets the fl purchase order no var.
	 * 
	 * @param flPurchaseOrderNoVar
	 *            the new fl purchase order no var
	 */
	public void setFlPurchaseOrderNoVar(String flPurchaseOrderNoVar) {
		this.flPurchaseOrderNoVar = flPurchaseOrderNoVar;
	}

	/**
	 * Checks if is fl contract order view.
	 * 
	 * @return true, if is fl contract order view
	 */
	public boolean isFlContractOrderView() {
		return flContractOrderView;
	}

	/**
	 * Sets the fl contract order view.
	 * 
	 * @param flContractOrderView
	 *            the new fl contract order view
	 */
	public void setFlContractOrderView(boolean flContractOrderView) {
		this.flContractOrderView = flContractOrderView;
	}

	/**
	 * Checks if is fl contract request view.
	 * 
	 * @return true, if is fl contract request view
	 */
	public boolean isFlContractRequestView() {
		return flContractRequestView;
	}

	/**
	 * Sets the fl contract request view.
	 * 
	 * @param flContractRequestView
	 *            the new fl contract request view
	 */
	public void setFlContractRequestView(boolean flContractRequestView) {
		this.flContractRequestView = flContractRequestView;
	}

	/**
	 * Gets the fl contract order var.
	 * 
	 * @return the fl contract order var
	 */
	public String getFlContractOrderVar() {
		return flContractOrderVar;
	}

	/**
	 * Sets the fl contract order var.
	 * 
	 * @param flContractOrderVar
	 *            the new fl contract order var
	 */
	public void setFlContractOrderVar(String flContractOrderVar) {
		this.flContractOrderVar = flContractOrderVar;
	}

	/**
	 * Gets the fl contract request var.
	 * 
	 * @return the fl contract request var
	 */
	public String getFlContractRequestVar() {
		return flContractRequestVar;
	}

	/**
	 * Sets the fl contract request var.
	 * 
	 * @param flContractRequestVar
	 *            the new fl contract request var
	 */
	public void setFlContractRequestVar(String flContractRequestVar) {
		this.flContractRequestVar = flContractRequestVar;
	}

	/**
	 * Checks if is fl item name view.
	 * 
	 * @return true, if is fl item name view
	 */
	public boolean isFlItemNameView() {
		return flItemNameView;
	}

	/**
	 * Sets the fl item name view.
	 * 
	 * @param flItemNameView
	 *            the new fl item name view
	 */
	public void setFlItemNameView(boolean flItemNameView) {
		this.flItemNameView = flItemNameView;
	}

	/**
	 * Checks if is fl item type view.
	 * 
	 * @return true, if is fl item type view
	 */
	public boolean isFlItemTypeView() {
		return flItemTypeView;
	}

	/**
	 * Sets the fl item type view.
	 * 
	 * @param flItemTypeView
	 *            the new fl item type view
	 */
	public void setFlItemTypeView(boolean flItemTypeView) {
		this.flItemTypeView = flItemTypeView;
	}

	/**
	 * Checks if is fl ptj view.
	 * 
	 * @return true, if is fl ptj view
	 */
	public boolean isFlPtjView() {
		return flPtjView;
	}

	/**
	 * Sets the fl ptj view.
	 * 
	 * @param flPtjView
	 *            the new fl ptj view
	 */
	public void setFlPtjView(boolean flPtjView) {
		this.flPtjView = flPtjView;
	}

	/**
	 * Checks if is fl contract no view.
	 * 
	 * @return true, if is fl contract no view
	 */
	public boolean isFlContractNoView() {
		return flContractNoView;
	}

	/**
	 * Sets the fl contract no view.
	 * 
	 * @param flContractNoView
	 *            the new fl contract no view
	 */
	public void setFlContractNoView(boolean flContractNoView) {
		this.flContractNoView = flContractNoView;
	}

	/**
	 * Checks if is fl sst view.
	 * 
	 * @return true, if is fl sst view
	 */
	public boolean isFlSstView() {
		return flSstView;
	}

	/**
	 * Sets the fl sst view.
	 * 
	 * @param flSstView
	 *            the new fl sst view
	 */
	public void setFlSstView(boolean flSstView) {
		this.flSstView = flSstView;
	}

	/**
	 * Checks if is fl jabatan view.
	 * 
	 * @return true, if is fl jabatan view
	 */
	public boolean isFlJabatanView() {
		return flJabatanView;
	}

	/**
	 * Sets the fl jabatan view.
	 * 
	 * @param flJabatanView
	 *            the new fl jabatan view
	 */
	public void setFlJabatanView(boolean flJabatanView) {
		this.flJabatanView = flJabatanView;
	}

	/**
	 * Checks if is fl ministry view.
	 * 
	 * @return true, if is fl ministry view
	 */
	public boolean isFlMinistryView() {
		return flMinistryView;
	}

	/**
	 * Sets the fl ministry view.
	 * 
	 * @param flMinistryView
	 *            the new fl ministry view
	 */
	public void setFlMinistryView(boolean flMinistryView) {
		this.flMinistryView = flMinistryView;
	}

	/**
	 * Checks if is fl supplier view.
	 * 
	 * @return true, if is fl supplier view
	 */
	public boolean isFlSupplierView() {
		return flSupplierView;
	}

	/**
	 * Sets the fl supplier view.
	 * 
	 * @param flSupplierView
	 *            the new fl supplier view
	 */
	public void setFlSupplierView(boolean flSupplierView) {
		this.flSupplierView = flSupplierView;
	}

	/**
	 * Checks if is fl status view.
	 * 
	 * @return true, if is fl status view
	 */
	public boolean isFlStatusView() {
		return flStatusView;
	}

	/**
	 * Sets the fl status view.
	 * 
	 * @param flStatusView
	 *            the new fl status view
	 */
	public void setFlStatusView(boolean flStatusView) {
		this.flStatusView = flStatusView;
	}

	/**
	 * Checks if is fl duration view.
	 * 
	 * @return true, if is fl duration view
	 */
	public boolean isFlDurationView() {
		return flDurationView;
	}

	/**
	 * Sets the fl duration view.
	 * 
	 * @param flDurationView
	 *            the new fl duration view
	 */
	public void setFlDurationView(boolean flDurationView) {
		this.flDurationView = flDurationView;
	}

	/**
	 * Checks if is fl recieving officer view.
	 * 
	 * @return true, if is fl recieving officer view
	 */
	public boolean isFlRecievingOfficerView() {
		return flRecievingOfficerView;
	}

	/**
	 * Sets the fl recieving officer view.
	 * 
	 * @param flRecievingOfficerView
	 *            the new fl recieving officer view
	 */
	public void setFlRecievingOfficerView(boolean flRecievingOfficerView) {
		this.flRecievingOfficerView = flRecievingOfficerView;
	}

	/**
	 * Checks if is fl delivery address view.
	 * 
	 * @return true, if is fl delivery address view
	 */
	public boolean isFlDeliveryAddressView() {
		return flDeliveryAddressView;
	}

	/**
	 * Sets the fl delivery address view.
	 * 
	 * @param flDeliveryAddressView
	 *            the new fl delivery address view
	 */
	public void setFlDeliveryAddressView(boolean flDeliveryAddressView) {
		this.flDeliveryAddressView = flDeliveryAddressView;
	}

	/**
	 * Checks if is fl rec note no view.
	 * 
	 * @return true, if is fl rec note no view
	 */
	public boolean isFlRecNoteNoView() {
		return flRecNoteNoView;
	}

	/**
	 * Sets the fl rec note no view.
	 * 
	 * @param flRecNoteNoView
	 *            the new fl rec note no view
	 */
	public void setFlRecNoteNoView(boolean flRecNoteNoView) {
		this.flRecNoteNoView = flRecNoteNoView;
	}

	/**
	 * Checks if is fl contract type view.
	 * 
	 * @return true, if is fl contract type view
	 */
	public boolean isFlContractTypeView() {
		return flContractTypeView;
	}

	/**
	 * Sets the fl contract type view.
	 * 
	 * @param flContractTypeView
	 *            the new fl contract type view
	 */
	public void setFlContractTypeView(boolean flContractTypeView) {
		this.flContractTypeView = flContractTypeView;
	}

	/**
	 * Checks if is fl requisitioner view.
	 * 
	 * @return true, if is fl requisitioner view
	 */
	public boolean isFlRequisitionerView() {
		return flRequisitionerView;
	}

	/**
	 * Sets the fl requisitioner view.
	 * 
	 * @param flRequisitionerView
	 *            the new fl requisitioner view
	 */
	public void setFlRequisitionerView(boolean flRequisitionerView) {
		this.flRequisitionerView = flRequisitionerView;
	}

	/**
	 * Checks if is fl mto contract view.
	 * 
	 * @return true, if is fl mto contract view
	 */
	public boolean isFlMtoContractView() {
		return flMtoContractView;
	}

	/**
	 * Sets the fl mto contract view.
	 * 
	 * @param flMtoContractView
	 *            the new fl mto contract view
	 */
	public void setFlMtoContractView(boolean flMtoContractView) {
		this.flMtoContractView = flMtoContractView;
	}

	/**
	 * Checks if is fl invoice no view.
	 * 
	 * @return true, if is fl invoice no view
	 */
	public boolean isFlInvoiceNoView() {
		return flInvoiceNoView;
	}

	/**
	 * Sets the fl invoice no view.
	 * 
	 * @param flInvoiceNoView
	 *            the new fl invoice no view
	 */
	public void setFlInvoiceNoView(boolean flInvoiceNoView) {
		this.flInvoiceNoView = flInvoiceNoView;
	}

	/**
	 * Checks if is fl deduction type view.
	 * 
	 * @return true, if is fl deduction type view
	 */
	public boolean isFlDeductionTypeView() {
		return flDeductionTypeView;
	}

	/**
	 * Sets the fl deduction type view.
	 * 
	 * @param flDeductionTypeView
	 *            the new fl deduction type view
	 */
	public void setFlDeductionTypeView(boolean flDeductionTypeView) {
		this.flDeductionTypeView = flDeductionTypeView;
	}

	/**
	 * Checks if is fl cr no view.
	 * 
	 * @return true, if is fl cr no view
	 */
	public boolean isFlCrNoView() {
		return flCrNoView;
	}

	/**
	 * Sets the fl cr no view.
	 * 
	 * @param flCrNoView
	 *            the new fl cr no view
	 */
	public void setFlCrNoView(boolean flCrNoView) {
		this.flCrNoView = flCrNoView;
	}

	/**
	 * Checks if is fl approver name view.
	 * 
	 * @return true, if is fl approver name view
	 */
	public boolean isFlApproverNameView() {
		return flApproverNameView;
	}

	/**
	 * Sets the fl approver name view.
	 * 
	 * @param flApproverNameView
	 *            the new fl approver name view
	 */
	public void setFlApproverNameView(boolean flApproverNameView) {
		this.flApproverNameView = flApproverNameView;
	}

	/**
	 * Checks if is fl document type view.
	 * 
	 * @return true, if is fl document type view
	 */
	public boolean isFlDocumentTypeView() {
		return flDocumentTypeView;
	}

	/**
	 * Sets the fl document type view.
	 * 
	 * @param flDocumentTypeView
	 *            the new fl document type view
	 */
	public void setFlDocumentTypeView(boolean flDocumentTypeView) {
		this.flDocumentTypeView = flDocumentTypeView;
	}

	/**
	 * Checks if is fl criteria view.
	 * 
	 * @return true, if is fl criteria view
	 */
	public boolean isFlCriteriaView() {
		return flCriteriaView;
	}

	/**
	 * Sets the fl criteria view.
	 * 
	 * @param flCriteriaView
	 *            the new fl criteria view
	 */
	public void setFlCriteriaView(boolean flCriteriaView) {
		this.flCriteriaView = flCriteriaView;
	}

	/**
	 * Checks if is fl scoring view.
	 * 
	 * @return true, if is fl scoring view
	 */
	public boolean isFlScoringView() {
		return flScoringView;
	}

	/**
	 * Sets the fl scoring view.
	 * 
	 * @param flScoringView
	 *            the new fl scoring view
	 */
	public void setFlScoringView(boolean flScoringView) {
		this.flScoringView = flScoringView;
	}

	/**
	 * Checks if is fl pkp no view.
	 * 
	 * @return true, if is fl pkp no view
	 */
	public boolean isFlPkpNoView() {
		return flPkpNoView;
	}

	/**
	 * Sets the fl pkp no view.
	 * 
	 * @param flPkpNoView
	 *            the new fl pkp no view
	 */
	public void setFlPkpNoView(boolean flPkpNoView) {
		this.flPkpNoView = flPkpNoView;
	}

	/**
	 * Checks if is fl delivery order no view.
	 * 
	 * @return true, if is fl delivery order no view
	 */
	public boolean isFlDeliveryOrderNoView() {
		return flDeliveryOrderNoView;
	}

	/**
	 * Sets the fl delivery order no view.
	 * 
	 * @param flDeliveryOrderNoView
	 *            the new fl delivery order no view
	 */
	public void setFlDeliveryOrderNoView(boolean flDeliveryOrderNoView) {
		this.flDeliveryOrderNoView = flDeliveryOrderNoView;
	}

	/**
	 * Checks if is fl item code view.
	 * 
	 * @return true, if is fl item code view
	 */
	public boolean isFlItemCodeView() {
		return flItemCodeView;
	}

	/**
	 * Sets the fl item code view.
	 * 
	 * @param flItemCodeView
	 *            the new fl item code view
	 */
	public void setFlItemCodeView(boolean flItemCodeView) {
		this.flItemCodeView = flItemCodeView;
	}

	/**
	 * Checks if is fl po amount view.
	 * 
	 * @return true, if is fl po amount view
	 */
	public boolean isFlPoAmountView() {
		return flPoAmountView;
	}

	/**
	 * Sets the fl po amount view.
	 * 
	 * @param flPoAmountView
	 *            the new fl po amount view
	 */
	public void setFlPoAmountView(boolean flPoAmountView) {
		this.flPoAmountView = flPoAmountView;
	}

	/**
	 * Checks if is fl pr no view.
	 * 
	 * @return true, if is fl pr no view
	 */
	public boolean isFlPrNoView() {
		return flPrNoView;
	}

	/**
	 * Sets the fl pr no view.
	 * 
	 * @param flPrNoView
	 *            the new fl pr no view
	 */
	public void setFlPrNoView(boolean flPrNoView) {
		this.flPrNoView = flPrNoView;
	}

	/**
	 * Checks if is fl pmo view.
	 * 
	 * @return true, if is fl pmo view
	 */
	public boolean isFlPmoView() {
		return flPmoView;
	}

	/**
	 * Sets the fl pmo view.
	 * 
	 * @param flPmoView
	 *            the new fl pmo view
	 */
	public void setFlPmoView(boolean flPmoView) {
		this.flPmoView = flPmoView;
	}

	/**
	 * Checks if is sm mof reg view.
	 * 
	 * @return true, if is sm mof reg view
	 */
	public boolean isSmMofRegView() {
		return smMofRegView;
	}

	/**
	 * Sets the sm mof reg view.
	 * 
	 * @param smMofRegView
	 *            the new sm mof reg view
	 */
	public void setSmMofRegView(boolean smMofRegView) {
		this.smMofRegView = smMofRegView;
	}

	/**
	 * Checks if is sm basic reg view.
	 * 
	 * @return true, if is sm basic reg view
	 */
	public boolean isSmBasicRegView() {
		return smBasicRegView;
	}

	/**
	 * Sets the sm basic reg view.
	 * 
	 * @param smBasicRegView
	 *            the new sm basic reg view
	 */
	public void setSmBasicRegView(boolean smBasicRegView) {
		this.smBasicRegView = smBasicRegView;
	}

	/**
	 * Checks if is extra two column view.
	 * 
	 * @return true, if is extra two column view
	 */
	public boolean isExtraTwoColumnView() {
		return extraTwoColumnView;
	}

	/**
	 * Sets the extra two column view.
	 * 
	 * @param extraTwoColumnView
	 *            the new extra two column view
	 */
	public void setExtraTwoColumnView(boolean extraTwoColumnView) {
		this.extraTwoColumnView = extraTwoColumnView;
	}

	/**
	 * Checks if is sm registration type view.
	 * 
	 * @return true, if is sm registration type view
	 */
	public boolean isSmRegistrationTypeView() {
		return smRegistrationTypeView;
	}

	/**
	 * Sets the sm registration type view.
	 * 
	 * @param smRegistrationTypeView
	 *            the new sm registration type view
	 */
	public void setSmRegistrationTypeView(boolean smRegistrationTypeView) {
		this.smRegistrationTypeView = smRegistrationTypeView;
	}

	/**
	 * Checks if is sm application no view.
	 * 
	 * @return true, if is sm application no view
	 */
	public boolean isSmApplicationNoView() {
		return smApplicationNoView;
	}

	/**
	 * Sets the sm application no view.
	 * 
	 * @param smApplicationNoView
	 *            the new sm application no view
	 */
	public void setSmApplicationNoView(boolean smApplicationNoView) {
		this.smApplicationNoView = smApplicationNoView;
	}

	/**
	 * Checks if is sm zone view.
	 * 
	 * @return true, if is sm zone view
	 */
	public boolean isSmZoneView() {
		return smZoneView;
	}

	/**
	 * Sets the sm zone view.
	 * 
	 * @param smZoneView
	 *            the new sm zone view
	 */
	public void setSmZoneView(boolean smZoneView) {
		this.smZoneView = smZoneView;
	}

	/**
	 * Checks if is sm supplier type view.
	 * 
	 * @return true, if is sm supplier type view
	 */
	public boolean isSmSupplierTypeView() {
		return smSupplierTypeView;
	}

	/**
	 * Sets the sm supplier type view.
	 * 
	 * @param smSupplierTypeView
	 *            the new sm supplier type view
	 */
	public void setSmSupplierTypeView(boolean smSupplierTypeView) {
		this.smSupplierTypeView = smSupplierTypeView;
	}

	/**
	 * Checks if is sm mof reg no view.
	 * 
	 * @return true, if is sm mof reg no view
	 */
	public boolean isSmMofRegNoView() {
		return smMofRegNoView;
	}

	/**
	 * Sets the sm mof reg no view.
	 * 
	 * @param smMofRegNoView
	 *            the new sm mof reg no view
	 */
	public void setSmMofRegNoView(boolean smMofRegNoView) {
		this.smMofRegNoView = smMofRegNoView;
	}

	/**
	 * Checks if is sm registration status view.
	 * 
	 * @return true, if is sm registration status view
	 */
	public boolean isSmRegistrationStatusView() {
		return smRegistrationStatusView;
	}

	/**
	 * Sets the sm registration status view.
	 * 
	 * @param smRegistrationStatusView
	 *            the new sm registration status view
	 */
	public void setSmRegistrationStatusView(boolean smRegistrationStatusView) {
		this.smRegistrationStatusView = smRegistrationStatusView;
	}

	/**
	 * Checks if is sm category view.
	 * 
	 * @return true, if is sm category view
	 */
	public boolean isSmCategoryView() {
		return smCategoryView;
	}

	/**
	 * Sets the sm category view.
	 * 
	 * @param smCategoryView
	 *            the new sm category view
	 */
	public void setSmCategoryView(boolean smCategoryView) {
		this.smCategoryView = smCategoryView;
	}

	/**
	 * Checks if is sm no of category view.
	 * 
	 * @return true, if is sm no of category view
	 */
	public boolean isSmNoOfCategoryView() {
		return smNoOfCategoryView;
	}

	/**
	 * Sets the sm no of category view.
	 * 
	 * @param smNoOfCategoryView
	 *            the new sm no of category view
	 */
	public void setSmNoOfCategoryView(boolean smNoOfCategoryView) {
		this.smNoOfCategoryView = smNoOfCategoryView;
	}

	/**
	 * Checks if is sm main category view.
	 * 
	 * @return true, if is sm main category view
	 */
	public boolean isSmMainCategoryView() {
		return smMainCategoryView;
	}

	/**
	 * Sets the sm main category view.
	 * 
	 * @param smMainCategoryView
	 *            the new sm main category view
	 */
	public void setSmMainCategoryView(boolean smMainCategoryView) {
		this.smMainCategoryView = smMainCategoryView;
	}

	/**
	 * Checks if is sm sub category view.
	 * 
	 * @return true, if is sm sub category view
	 */
	public boolean isSmSubCategoryView() {
		return smSubCategoryView;
	}

	/**
	 * Sets the sm sub category view.
	 * 
	 * @param smSubCategoryView
	 *            the new sm sub category view
	 */
	public void setSmSubCategoryView(boolean smSubCategoryView) {
		this.smSubCategoryView = smSubCategoryView;
	}

	/**
	 * Checks if is sm sub category division view.
	 * 
	 * @return true, if is sm sub category division view
	 */
	public boolean isSmSubCategoryDivisionView() {
		return smSubCategoryDivisionView;
	}

	/**
	 * Sets the sm sub category division view.
	 * 
	 * @param smSubCategoryDivisionView
	 *            the new sm sub category division view
	 */
	public void setSmSubCategoryDivisionView(boolean smSubCategoryDivisionView) {
		this.smSubCategoryDivisionView = smSubCategoryDivisionView;
	}

	/**
	 * Checks if is sm application status view.
	 * 
	 * @return true, if is sm application status view
	 */
	public boolean isSmApplicationStatusView() {
		return smApplicationStatusView;
	}

	/**
	 * Sets the sm application status view.
	 * 
	 * @param smApplicationStatusView
	 *            the new sm application status view
	 */
	public void setSmApplicationStatusView(boolean smApplicationStatusView) {
		this.smApplicationStatusView = smApplicationStatusView;
	}

	/**
	 * Checks if is sm payment type view.
	 * 
	 * @return true, if is sm payment type view
	 */
	public boolean isSmPaymentTypeView() {
		return smPaymentTypeView;
	}

	/**
	 * Sets the sm payment type view.
	 * 
	 * @param smPaymentTypeView
	 *            the new sm payment type view
	 */
	public void setSmPaymentTypeView(boolean smPaymentTypeView) {
		this.smPaymentTypeView = smPaymentTypeView;
	}

	/**
	 * Checks if is sm application type view.
	 * 
	 * @return true, if is sm application type view
	 */
	public boolean isSmApplicationTypeView() {
		return smApplicationTypeView;
	}

	/**
	 * Sets the sm application type view.
	 * 
	 * @param smApplicationTypeView
	 *            the new sm application type view
	 */
	public void setSmApplicationTypeView(boolean smApplicationTypeView) {
		this.smApplicationTypeView = smApplicationTypeView;
	}

	/**
	 * Checks if is sm state view.
	 * 
	 * @return true, if is sm state view
	 */
	public boolean isSmStateView() {
		return smStateView;
	}

	/**
	 * Sets the sm state view.
	 * 
	 * @param smStateView
	 *            the new sm state view
	 */
	public void setSmStateView(boolean smStateView) {
		this.smStateView = smStateView;
	}

	/**
	 * Checks if is sm district view.
	 * 
	 * @return true, if is sm district view
	 */
	public boolean isSmDistrictView() {
		return smDistrictView;
	}

	/**
	 * Sets the sm district view.
	 * 
	 * @param smDistrictView
	 *            the new sm district view
	 */
	public void setSmDistrictView(boolean smDistrictView) {
		this.smDistrictView = smDistrictView;
	}

	/**
	 * Checks if is sm company name view.
	 * 
	 * @return true, if is sm company name view
	 */
	public boolean isSmCompanyNameView() {
		return smCompanyNameView;
	}

	/**
	 * Sets the sm company name view.
	 * 
	 * @param smCompanyNameView
	 *            the new sm company name view
	 */
	public void setSmCompanyNameView(boolean smCompanyNameView) {
		this.smCompanyNameView = smCompanyNameView;
	}

	/**
	 * Checks if is sm report type view.
	 * 
	 * @return true, if is sm report type view
	 */
	public boolean isSmReportTypeView() {
		return smReportTypeView;
	}

	/**
	 * Sets the sm report type view.
	 * 
	 * @param smReportTypeView
	 *            the new sm report type view
	 */
	public void setSmReportTypeView(boolean smReportTypeView) {
		this.smReportTypeView = smReportTypeView;
	}

	/**
	 * Checks if is sm extra two column view.
	 * 
	 * @return true, if is sm extra two column view
	 */
	public boolean isSmExtraTwoColumnView() {
		return smExtraTwoColumnView;
	}

	/**
	 * Sets the sm extra two column view.
	 * 
	 * @param smExtraTwoColumnView
	 *            the new sm extra two column view
	 */
	public void setSmExtraTwoColumnView(boolean smExtraTwoColumnView) {
		this.smExtraTwoColumnView = smExtraTwoColumnView;
	}

	/**
	 * Checks if is sm mandatory zone view.
	 * 
	 * @return true, if is sm mandatory zone view
	 */
	public boolean isSmMandatoryZoneView() {
		return smMandatoryZoneView;
	}

	/**
	 * Sets the sm mandatory zone view.
	 * 
	 * @param smMandatoryZoneView
	 *            the new sm mandatory zone view
	 */
	public void setSmMandatoryZoneView(boolean smMandatoryZoneView) {
		this.smMandatoryZoneView = smMandatoryZoneView;
	}

	/**
	 * Checks if is sm mandatory supplier type view.
	 * 
	 * @return true, if is sm mandatory supplier type view
	 */
	public boolean isSmMandatorySupplierTypeView() {
		return smMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm mandatory supplier type view.
	 * 
	 * @param smMandatorySupplierTypeView
	 *            the new sm mandatory supplier type view
	 */
	public void setSmMandatorySupplierTypeView(
			boolean smMandatorySupplierTypeView) {
		this.smMandatorySupplierTypeView = smMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm mandatory application date view.
	 * 
	 * @return true, if is sm mandatory application date view
	 */
	public boolean isSmMandatoryApplicationDateView() {
		return smMandatoryApplicationDateView;
	}

	/**
	 * Sets the sm mandatory application date view.
	 * 
	 * @param smMandatoryApplicationDateView
	 *            the new sm mandatory application date view
	 */
	public void setSmMandatoryApplicationDateView(
			boolean smMandatoryApplicationDateView) {
		this.smMandatoryApplicationDateView = smMandatoryApplicationDateView;
	}

	/**
	 * Checks if is sm mandatory application status view.
	 * 
	 * @return true, if is sm mandatory application status view
	 */
	public boolean isSmMandatoryApplicationStatusView() {
		return smMandatoryApplicationStatusView;
	}

	/**
	 * Sets the sm mandatory application status view.
	 * 
	 * @param smMandatoryApplicationStatusView
	 *            the new sm mandatory application status view
	 */
	public void setSmMandatoryApplicationStatusView(
			boolean smMandatoryApplicationStatusView) {
		this.smMandatoryApplicationStatusView = smMandatoryApplicationStatusView;
	}

	/**
	 * Checks if is sm mandatory mof reg no view.
	 * 
	 * @return true, if is sm mandatory mof reg no view
	 */
	public boolean isSmMandatoryMofRegNoView() {
		return smMandatoryMofRegNoView;
	}

	/**
	 * Sets the sm mandatory mof reg no view.
	 * 
	 * @param smMandatoryMofRegNoView
	 *            the new sm mandatory mof reg no view
	 */
	public void setSmMandatoryMofRegNoView(boolean smMandatoryMofRegNoView) {
		this.smMandatoryMofRegNoView = smMandatoryMofRegNoView;
	}

	/**
	 * Checks if is sm mandatory expiry date view.
	 * 
	 * @return true, if is sm mandatory expiry date view
	 */
	public boolean isSmMandatoryExpiryDateView() {
		return smMandatoryExpiryDateView;
	}

	/**
	 * Sets the sm mandatory expiry date view.
	 * 
	 * @param smMandatoryExpiryDateView
	 *            the new sm mandatory expiry date view
	 */
	public void setSmMandatoryExpiryDateView(boolean smMandatoryExpiryDateView) {
		this.smMandatoryExpiryDateView = smMandatoryExpiryDateView;
	}

	/**
	 * Checks if is sm mandatory printed date view.
	 * 
	 * @return true, if is sm mandatory printed date view
	 */
	public boolean isSmMandatoryPrintedDateView() {
		return smMandatoryPrintedDateView;
	}

	/**
	 * Sets the sm mandatory printed date view.
	 * 
	 * @param smMandatoryPrintedDateView
	 *            the new sm mandatory printed date view
	 */
	public void setSmMandatoryPrintedDateView(boolean smMandatoryPrintedDateView) {
		this.smMandatoryPrintedDateView = smMandatoryPrintedDateView;
	}

	/**
	 * Checks if is cm basic reg view.
	 * 
	 * @return true, if is cm basic reg view
	 */
	public boolean isCmBasicRegView() {
		return cmBasicRegView;
	}

	/**
	 * Sets the cm basic reg view.
	 * 
	 * @param cmBasicRegView
	 *            the new cm basic reg view
	 */
	public void setCmBasicRegView(boolean cmBasicRegView) {
		this.cmBasicRegView = cmBasicRegView;
	}

	/**
	 * Checks if is cm mof reg view.
	 * 
	 * @return true, if is cm mof reg view
	 */
	public boolean isCmMofRegView() {
		return cmMofRegView;
	}

	/**
	 * Sets the cm mof reg view.
	 * 
	 * @param cmMofRegView
	 *            the new cm mof reg view
	 */
	public void setCmMofRegView(boolean cmMofRegView) {
		this.cmMofRegView = cmMofRegView;
	}

	/**
	 * Checks if is cm proof identity view.
	 * 
	 * @return true, if is cm proof identity view
	 */
	public boolean isCmProofIdentityView() {
		return cmProofIdentityView;
	}

	/**
	 * Sets the cm proof identity view.
	 * 
	 * @param cmProofIdentityView
	 *            the new cm proof identity view
	 */
	public void setCmProofIdentityView(boolean cmProofIdentityView) {
		this.cmProofIdentityView = cmProofIdentityView;
	}

	/**
	 * Checks if is cm item type view.
	 * 
	 * @return true, if is cm item type view
	 */
	public boolean isCmItemTypeView() {
		return cmItemTypeView;
	}

	/**
	 * Sets the cm item type view.
	 * 
	 * @param cmItemTypeView
	 *            the new cm item type view
	 */
	public void setCmItemTypeView(boolean cmItemTypeView) {
		this.cmItemTypeView = cmItemTypeView;
	}

	/**
	 * Checks if is cm item status view.
	 * 
	 * @return true, if is cm item status view
	 */
	public boolean isCmItemStatusView() {
		return cmItemStatusView;
	}

	/**
	 * Sets the cm item status view.
	 * 
	 * @param cmItemStatusView
	 *            the new cm item status view
	 */
	public void setCmItemStatusView(boolean cmItemStatusView) {
		this.cmItemStatusView = cmItemStatusView;
	}

	/**
	 * Checks if is cm unspsc code view.
	 * 
	 * @return true, if is cm unspsc code view
	 */
	public boolean isCmUnspscCodeView() {
		return cmUnspscCodeView;
	}

	/**
	 * Sets the cm unspsc code view.
	 * 
	 * @param cmUnspscCodeView
	 *            the new cm unspsc code view
	 */
	public void setCmUnspscCodeView(boolean cmUnspscCodeView) {
		this.cmUnspscCodeView = cmUnspscCodeView;
	}

	/**
	 * Checks if is cm unspsc title view.
	 * 
	 * @return true, if is cm unspsc title view
	 */
	public boolean isCmUnspscTitleView() {
		return cmUnspscTitleView;
	}

	/**
	 * Sets the cm unspsc title view.
	 * 
	 * @param cmUnspscTitleView
	 *            the new cm unspsc title view
	 */
	public void setCmUnspscTitleView(boolean cmUnspscTitleView) {
		this.cmUnspscTitleView = cmUnspscTitleView;
	}

	/**
	 * Checks if is cm company name view.
	 * 
	 * @return true, if is cm company name view
	 */
	public boolean isCmCompanyNameView() {
		return cmCompanyNameView;
	}

	/**
	 * Sets the cm company name view.
	 * 
	 * @param cmCompanyNameView
	 *            the new cm company name view
	 */
	public void setCmCompanyNameView(boolean cmCompanyNameView) {
		this.cmCompanyNameView = cmCompanyNameView;
	}

	/**
	 * Checks if is cm registration type view.
	 * 
	 * @return true, if is cm registration type view
	 */
	public boolean isCmRegistrationTypeView() {
		return cmRegistrationTypeView;
	}

	/**
	 * Sets the cm registration type view.
	 * 
	 * @param cmRegistrationTypeView
	 *            the new cm registration type view
	 */
	public void setCmRegistrationTypeView(boolean cmRegistrationTypeView) {
		this.cmRegistrationTypeView = cmRegistrationTypeView;
	}

	/**
	 * Checks if is cm item category view.
	 * 
	 * @return true, if is cm item category view
	 */
	public boolean isCmItemCategoryView() {
		return cmItemCategoryView;
	}

	/**
	 * Sets the cm item category view.
	 * 
	 * @param cmItemCategoryView
	 *            the new cm item category view
	 */
	public void setCmItemCategoryView(boolean cmItemCategoryView) {
		this.cmItemCategoryView = cmItemCategoryView;
	}

	/**
	 * Checks if is cm item name view.
	 * 
	 * @return true, if is cm item name view
	 */
	public boolean isCmItemNameView() {
		return cmItemNameView;
	}

	/**
	 * Sets the cm item name view.
	 * 
	 * @param cmItemNameView
	 *            the new cm item name view
	 */
	public void setCmItemNameView(boolean cmItemNameView) {
		this.cmItemNameView = cmItemNameView;
	}

	/**
	 * Checks if is cm ep number view.
	 * 
	 * @return true, if is cm ep number view
	 */
	public boolean isCmEpNumberView() {
		return cmEpNumberView;
	}

	/**
	 * Sets the cm ep number view.
	 * 
	 * @param cmEpNumberView
	 *            the new cm ep number view
	 */
	public void setCmEpNumberView(boolean cmEpNumberView) {
		this.cmEpNumberView = cmEpNumberView;
	}

	/**
	 * Checks if is cm business reg no view.
	 * 
	 * @return true, if is cm business reg no view
	 */
	public boolean isCmBusinessRegNoView() {
		return cmBusinessRegNoView;
	}

	/**
	 * Sets the cm business reg no view.
	 * 
	 * @param cmBusinessRegNoView
	 *            the new cm business reg no view
	 */
	public void setCmBusinessRegNoView(boolean cmBusinessRegNoView) {
		this.cmBusinessRegNoView = cmBusinessRegNoView;
	}

	/**
	 * Checks if is cm ic number view.
	 * 
	 * @return true, if is cm ic number view
	 */
	public boolean isCmIcNumberView() {
		return cmIcNumberView;
	}

	/**
	 * Sets the cm ic number view.
	 * 
	 * @param cmIcNumberView
	 *            the new cm ic number view
	 */
	public void setCmIcNumberView(boolean cmIcNumberView) {
		this.cmIcNumberView = cmIcNumberView;
	}

	/**
	 * Checks if is cm mof number view.
	 * 
	 * @return true, if is cm mof number view
	 */
	public boolean isCmMofNumberView() {
		return cmMofNumberView;
	}

	/**
	 * Sets the cm mof number view.
	 * 
	 * @param cmMofNumberView
	 *            the new cm mof number view
	 */
	public void setCmMofNumberView(boolean cmMofNumberView) {
		this.cmMofNumberView = cmMofNumberView;
	}

	/**
	 * Checks if is cm extention code view.
	 * 
	 * @return true, if is cm extention code view
	 */
	public boolean isCmExtentionCodeView() {
		return cmExtentionCodeView;
	}

	/**
	 * Sets the cm extention code view.
	 * 
	 * @param cmExtentionCodeView
	 *            the new cm extention code view
	 */
	public void setCmExtentionCodeView(boolean cmExtentionCodeView) {
		this.cmExtentionCodeView = cmExtentionCodeView;
	}

	/**
	 * Checks if is cm extention type view.
	 * 
	 * @return true, if is cm extention type view
	 */
	public boolean isCmExtentionTypeView() {
		return cmExtentionTypeView;
	}

	/**
	 * Sets the cm extention type view.
	 * 
	 * @param cmExtentionTypeView
	 *            the new cm extention type view
	 */
	public void setCmExtentionTypeView(boolean cmExtentionTypeView) {
		this.cmExtentionTypeView = cmExtentionTypeView;
	}

	/**
	 * Checks if is cm extention brand view.
	 * 
	 * @return true, if is cm extention brand view
	 */
	public boolean isCmExtentionBrandView() {
		return cmExtentionBrandView;
	}

	/**
	 * Sets the cm extention brand view.
	 * 
	 * @param cmExtentionBrandView
	 *            the new cm extention brand view
	 */
	public void setCmExtentionBrandView(boolean cmExtentionBrandView) {
		this.cmExtentionBrandView = cmExtentionBrandView;
	}

	/**
	 * Checks if is cm extention measurment view.
	 * 
	 * @return true, if is cm extention measurment view
	 */
	public boolean isCmExtentionMeasurmentView() {
		return cmExtentionMeasurmentView;
	}

	/**
	 * Sets the cm extention measurment view.
	 * 
	 * @param cmExtentionMeasurmentView
	 *            the new cm extention measurment view
	 */
	public void setCmExtentionMeasurmentView(boolean cmExtentionMeasurmentView) {
		this.cmExtentionMeasurmentView = cmExtentionMeasurmentView;
	}

	/**
	 * Checks if is cm extention colour view.
	 * 
	 * @return true, if is cm extention colour view
	 */
	public boolean isCmExtentionColourView() {
		return cmExtentionColourView;
	}

	/**
	 * Sets the cm extention colour view.
	 * 
	 * @param cmExtentionColourView
	 *            the new cm extention colour view
	 */
	public void setCmExtentionColourView(boolean cmExtentionColourView) {
		this.cmExtentionColourView = cmExtentionColourView;
	}

	/**
	 * Checks if is sc quotation no view.
	 * 
	 * @return true, if is sc quotation no view
	 */
	public boolean isScQuotationNoView() {
		return scQuotationNoView;
	}

	/**
	 * Sets the sc quotation no view.
	 * 
	 * @param scQuotationNoView
	 *            the new sc quotation no view
	 */
	public void setScQuotationNoView(boolean scQuotationNoView) {
		this.scQuotationNoView = scQuotationNoView;
	}

	/**
	 * Gets the application no tot var.
	 * 
	 * @return the application no tot var
	 */
	public String getApplicationNoTotVar() {
		return applicationNoTotVar;
	}

	/**
	 * Sets the application no tot var.
	 * 
	 * @param applicationNoTotVar
	 *            the new application no tot var
	 */
	public void setApplicationNoTotVar(String applicationNoTotVar) {
		this.applicationNoTotVar = applicationNoTotVar;
	}

	/**
	 * Gets the zone tot var.
	 * 
	 * @return the zone tot var
	 */
	public String getZoneTotVar() {
		return zoneTotVar;
	}

	/**
	 * Sets the zone tot var.
	 * 
	 * @param zoneTotVar
	 *            the new zone tot var
	 */
	public void setZoneTotVar(String zoneTotVar) {
		this.zoneTotVar = zoneTotVar;
	}

	/**
	 * Gets the supplier type tot var.
	 * 
	 * @return the supplier type tot var
	 */
	public String getSupplierTypeTotVar() {
		return supplierTypeTotVar;
	}

	/**
	 * Sets the supplier type tot var.
	 * 
	 * @param supplierTypeTotVar
	 *            the new supplier type tot var
	 */
	public void setSupplierTypeTotVar(String supplierTypeTotVar) {
		this.supplierTypeTotVar = supplierTypeTotVar;
	}

	/**
	 * Gets the mof reg no tot var.
	 * 
	 * @return the mof reg no tot var
	 */
	public String getMofRegNoTotVar() {
		return mofRegNoTotVar;
	}

	/**
	 * Sets the mof reg no tot var.
	 * 
	 * @param mofRegNoTotVar
	 *            the new mof reg no tot var
	 */
	public void setMofRegNoTotVar(String mofRegNoTotVar) {
		this.mofRegNoTotVar = mofRegNoTotVar;
	}

	/**
	 * Gets the registration status tot var.
	 * 
	 * @return the registration status tot var
	 */
	public String getRegistrationStatusTotVar() {
		return registrationStatusTotVar;
	}

	/**
	 * Sets the registration status tot var.
	 * 
	 * @param registrationStatusTotVar
	 *            the new registration status tot var
	 */
	public void setRegistrationStatusTotVar(String registrationStatusTotVar) {
		this.registrationStatusTotVar = registrationStatusTotVar;
	}

	/**
	 * Gets the main category tot var.
	 * 
	 * @return the main category tot var
	 */
	public String getMainCategoryTotVar() {
		return mainCategoryTotVar;
	}

	/**
	 * Sets the main category tot var.
	 * 
	 * @param mainCategoryTotVar
	 *            the new main category tot var
	 */
	public void setMainCategoryTotVar(String mainCategoryTotVar) {
		this.mainCategoryTotVar = mainCategoryTotVar;
	}

	/**
	 * Gets the sub category tot var.
	 * 
	 * @return the sub category tot var
	 */
	public String getSubCategoryTotVar() {
		return subCategoryTotVar;
	}

	/**
	 * Sets the sub category tot var.
	 * 
	 * @param subCategoryTotVar
	 *            the new sub category tot var
	 */
	public void setSubCategoryTotVar(String subCategoryTotVar) {
		this.subCategoryTotVar = subCategoryTotVar;
	}

	/**
	 * Gets the sub category division tot var.
	 * 
	 * @return the sub category division tot var
	 */
	public String getSubCategoryDivisionTotVar() {
		return subCategoryDivisionTotVar;
	}

	/**
	 * Sets the sub category division tot var.
	 * 
	 * @param subCategoryDivisionTotVar
	 *            the new sub category division tot var
	 */
	public void setSubCategoryDivisionTotVar(String subCategoryDivisionTotVar) {
		this.subCategoryDivisionTotVar = subCategoryDivisionTotVar;
	}

	/**
	 * Gets the application status tot var.
	 * 
	 * @return the application status tot var
	 */
	public String getApplicationStatusTotVar() {
		return applicationStatusTotVar;
	}

	/**
	 * Sets the application status tot var.
	 * 
	 * @param applicationStatusTotVar
	 *            the new application status tot var
	 */
	public void setApplicationStatusTotVar(String applicationStatusTotVar) {
		this.applicationStatusTotVar = applicationStatusTotVar;
	}

	/**
	 * Gets the payment type tot var.
	 * 
	 * @return the payment type tot var
	 */
	public String getPaymentTypeTotVar() {
		return paymentTypeTotVar;
	}

	/**
	 * Sets the payment type tot var.
	 * 
	 * @param paymentTypeTotVar
	 *            the new payment type tot var
	 */
	public void setPaymentTypeTotVar(String paymentTypeTotVar) {
		this.paymentTypeTotVar = paymentTypeTotVar;
	}

	/**
	 * Gets the application type tot var.
	 * 
	 * @return the application type tot var
	 */
	public String getApplicationTypeTotVar() {
		return applicationTypeTotVar;
	}

	/**
	 * Sets the application type tot var.
	 * 
	 * @param applicationTypeTotVar
	 *            the new application type tot var
	 */
	public void setApplicationTypeTotVar(String applicationTypeTotVar) {
		this.applicationTypeTotVar = applicationTypeTotVar;
	}

	/**
	 * Gets the state tot var.
	 * 
	 * @return the state tot var
	 */
	public String getStateTotVar() {
		return stateTotVar;
	}

	/**
	 * Sets the state tot var.
	 * 
	 * @param stateTotVar
	 *            the new state tot var
	 */
	public void setStateTotVar(String stateTotVar) {
		this.stateTotVar = stateTotVar;
	}

	/**
	 * Gets the district tot var.
	 * 
	 * @return the district tot var
	 */
	public String getDistrictTotVar() {
		return districtTotVar;
	}

	/**
	 * Sets the district tot var.
	 * 
	 * @param districtTotVar
	 *            the new district tot var
	 */
	public void setDistrictTotVar(String districtTotVar) {
		this.districtTotVar = districtTotVar;
	}

	/**
	 * Gets the report type tot var.
	 * 
	 * @return the report type tot var
	 */
	public String getReportTypeTotVar() {
		return reportTypeTotVar;
	}

	/**
	 * Sets the report type tot var.
	 * 
	 * @param reportTypeTotVar
	 *            the new report type tot var
	 */
	public void setReportTypeTotVar(String reportTypeTotVar) {
		this.reportTypeTotVar = reportTypeTotVar;
	}

	/**
	 * Gets the ep number tot var.
	 * 
	 * @return the ep number tot var
	 */
	public String getEpNumberTotVar() {
		return epNumberTotVar;
	}

	/**
	 * Sets the ep number tot var.
	 * 
	 * @param epNumberTotVar
	 *            the new ep number tot var
	 */
	public void setEpNumberTotVar(String epNumberTotVar) {
		this.epNumberTotVar = epNumberTotVar;
	}

	/**
	 * Gets the business reg no tot var.
	 * 
	 * @return the business reg no tot var
	 */
	public String getBusinessRegNoTotVar() {
		return businessRegNoTotVar;
	}

	/**
	 * Sets the business reg no tot var.
	 * 
	 * @param businessRegNoTotVar
	 *            the new business reg no tot var
	 */
	public void setBusinessRegNoTotVar(String businessRegNoTotVar) {
		this.businessRegNoTotVar = businessRegNoTotVar;
	}

	/**
	 * Gets the ic number tot var.
	 * 
	 * @return the ic number tot var
	 */
	public String getIcNumberTotVar() {
		return icNumberTotVar;
	}

	/**
	 * Sets the ic number tot var.
	 * 
	 * @param icNumberTotVar
	 *            the new ic number tot var
	 */
	public void setIcNumberTotVar(String icNumberTotVar) {
		this.icNumberTotVar = icNumberTotVar;
	}

	/**
	 * Gets the mof number tot var.
	 * 
	 * @return the mof number tot var
	 */
	public String getMofNumberTotVar() {
		return mofNumberTotVar;
	}

	/**
	 * Sets the mof number tot var.
	 * 
	 * @param mofNumberTotVar
	 *            the new mof number tot var
	 */
	public void setMofNumberTotVar(String mofNumberTotVar) {
		this.mofNumberTotVar = mofNumberTotVar;
	}

	/**
	 * Gets the extention type tot var.
	 * 
	 * @return the extention type tot var
	 */
	public String getExtentionTypeTotVar() {
		return extentionTypeTotVar;
	}

	/**
	 * Sets the extention type tot var.
	 * 
	 * @param extentionTypeTotVar
	 *            the new extention type tot var
	 */
	public void setExtentionTypeTotVar(String extentionTypeTotVar) {
		this.extentionTypeTotVar = extentionTypeTotVar;
	}

	/**
	 * Gets the extention brand tot var.
	 * 
	 * @return the extention brand tot var
	 */
	public String getExtentionBrandTotVar() {
		return extentionBrandTotVar;
	}

	/**
	 * Sets the extention brand tot var.
	 * 
	 * @param extentionBrandTotVar
	 *            the new extention brand tot var
	 */
	public void setExtentionBrandTotVar(String extentionBrandTotVar) {
		this.extentionBrandTotVar = extentionBrandTotVar;
	}

	/**
	 * Gets the extention measurment tot var.
	 * 
	 * @return the extention measurment tot var
	 */
	public String getExtentionMeasurmentTotVar() {
		return extentionMeasurmentTotVar;
	}

	/**
	 * Sets the extention measurment tot var.
	 * 
	 * @param extentionMeasurmentTotVar
	 *            the new extention measurment tot var
	 */
	public void setExtentionMeasurmentTotVar(String extentionMeasurmentTotVar) {
		this.extentionMeasurmentTotVar = extentionMeasurmentTotVar;
	}

	/**
	 * Gets the extention colour tot var.
	 * 
	 * @return the extention colour tot var
	 */
	public String getExtentionColourTotVar() {
		return extentionColourTotVar;
	}

	/**
	 * Sets the extention colour tot var.
	 * 
	 * @param extentionColourTotVar
	 *            the new extention colour tot var
	 */
	public void setExtentionColourTotVar(String extentionColourTotVar) {
		this.extentionColourTotVar = extentionColourTotVar;
	}

	/**
	 * Gets the from date var.
	 * 
	 * @return the from date var
	 */
	public String getFromDateVar() {
		return fromDateVar;
	}

	/**
	 * Sets the from date var.
	 * 
	 * @param fromDateVar
	 *            the new from date var
	 */
	public void setFromDateVar(String fromDateVar) {
		this.fromDateVar = fromDateVar;
	}

	/**
	 * Gets the to date var.
	 * 
	 * @return the to date var
	 */
	public String getToDateVar() {
		return toDateVar;
	}

	/**
	 * Sets the to date var.
	 * 
	 * @param toDateVar
	 *            the new to date var
	 */
	public void setToDateVar(String toDateVar) {
		this.toDateVar = toDateVar;
	}

	/** The url report jsp name. */
	private String urlReportJSPName = ReportConst.EMPTY_VAL;

	// URL variables end

	/**
	 * Gets the url report jsp name.
	 * 
	 * @return the url report jsp name
	 */
	public String getUrlReportJSPName() {
		return urlReportJSPName;
	}

	/**
	 * Sets the url report jsp name.
	 * 
	 * @param urlReportJSPName
	 *            the new url report jsp name
	 */
	public void setUrlReportJSPName(String urlReportJSPName) {
		this.urlReportJSPName = urlReportJSPName;
	}

	/**
	 * Gets the quotation id tot var.
	 * 
	 * @return the quotation id tot var
	 */
	public String getQuotationIdTotVar() {
		return quotationIdTotVar;
	}

	/**
	 * Sets the quotation id tot var.
	 * 
	 * @param quotationIdTotVar
	 *            the new quotation id tot var
	 */
	public void setQuotationIdTotVar(String quotationIdTotVar) {
		this.quotationIdTotVar = quotationIdTotVar;
	}

	/**
	 * Gets the item type tot var.
	 * 
	 * @return the item type tot var
	 */
	public String getItemTypeTotVar() {
		return itemTypeTotVar;
	}

	/**
	 * Sets the item type tot var.
	 * 
	 * @param itemTypeTotVar
	 *            the new item type tot var
	 */
	public void setItemTypeTotVar(String itemTypeTotVar) {
		this.itemTypeTotVar = itemTypeTotVar;
	}

	/**
	 * Gets the item status tot var.
	 * 
	 * @return the item status tot var
	 */
	public String getItemStatusTotVar() {
		return itemStatusTotVar;
	}

	/**
	 * Sets the item status tot var.
	 * 
	 * @param itemStatusTotVar
	 *            the new item status tot var
	 */
	public void setItemStatusTotVar(String itemStatusTotVar) {
		this.itemStatusTotVar = itemStatusTotVar;
	}

	/**
	 * Gets the unspsc code tot var.
	 * 
	 * @return the unspsc code tot var
	 */
	public String getUnspscCodeTotVar() {
		return unspscCodeTotVar;
	}

	/**
	 * Sets the unspsc code tot var.
	 * 
	 * @param unspscCodeTotVar
	 *            the new unspsc code tot var
	 */
	public void setUnspscCodeTotVar(String unspscCodeTotVar) {
		this.unspscCodeTotVar = unspscCodeTotVar;
	}

	/**
	 * Gets the unspsc title tot var.
	 * 
	 * @return the unspsc title tot var
	 */
	public String getUnspscTitleTotVar() {
		return unspscTitleTotVar;
	}

	/**
	 * Sets the unspsc title tot var.
	 * 
	 * @param unspscTitleTotVar
	 *            the new unspsc title tot var
	 */
	public void setUnspscTitleTotVar(String unspscTitleTotVar) {
		this.unspscTitleTotVar = unspscTitleTotVar;
	}

	/**
	 * Gets the extention code tot var.
	 * 
	 * @return the extention code tot var
	 */
	public String getExtentionCodeTotVar() {
		return extentionCodeTotVar;
	}

	/**
	 * Sets the extention code tot var.
	 * 
	 * @param extentionCodeTotVar
	 *            the new extention code tot var
	 */
	public void setExtentionCodeTotVar(String extentionCodeTotVar) {
		this.extentionCodeTotVar = extentionCodeTotVar;
	}

	/**
	 * Gets the company name tot var.
	 * 
	 * @return the company name tot var
	 */
	public String getCompanyNameTotVar() {
		return companyNameTotVar;
	}

	/**
	 * Sets the company name tot var.
	 * 
	 * @param companyNameTotVar
	 *            the new company name tot var
	 */
	public void setCompanyNameTotVar(String companyNameTotVar) {
		this.companyNameTotVar = companyNameTotVar;
	}

	/**
	 * Gets the registration type tot var.
	 * 
	 * @return the registration type tot var
	 */
	public String getRegistrationTypeTotVar() {
		return registrationTypeTotVar;
	}

	/**
	 * Sets the registration type tot var.
	 * 
	 * @param registrationTypeTotVar
	 *            the new registration type tot var
	 */
	public void setRegistrationTypeTotVar(String registrationTypeTotVar) {
		this.registrationTypeTotVar = registrationTypeTotVar;
	}

	/**
	 * Gets the item category tot var.
	 * 
	 * @return the item category tot var
	 */
	public String getItemCategoryTotVar() {
		return itemCategoryTotVar;
	}

	/**
	 * Sets the item category tot var.
	 * 
	 * @param itemCategoryTotVar
	 *            the new item category tot var
	 */
	public void setItemCategoryTotVar(String itemCategoryTotVar) {
		this.itemCategoryTotVar = itemCategoryTotVar;
	}

	/**
	 * Gets the item name tot var.
	 * 
	 * @return the item name tot var
	 */
	public String getItemNameTotVar() {
		return itemNameTotVar;
	}

	/**
	 * Sets the item name tot var.
	 * 
	 * @param itemNameTotVar
	 *            the new item name tot var
	 */
	public void setItemNameTotVar(String itemNameTotVar) {
		this.itemNameTotVar = itemNameTotVar;
	}

	/**
	 * Gets the proof identity tot var.
	 * 
	 * @return the proof identity tot var
	 */
	public String getProofIdentityTotVar() {
		return proofIdentityTotVar;
	}

	/**
	 * Sets the proof identity tot var.
	 * 
	 * @param proofIdentityTotVar
	 *            the new proof identity tot var
	 */
	public void setProofIdentityTotVar(String proofIdentityTotVar) {
		this.proofIdentityTotVar = proofIdentityTotVar;
	}

	/**
	 * Gets the ptj tot var.
	 * 
	 * @return the ptj tot var
	 */
	public String getPtjTotVar() {
		return ptjTotVar;
	}

	/**
	 * Sets the ptj tot var.
	 * 
	 * @param ptjTotVar
	 *            the new ptj tot var
	 */
	public void setPtjTotVar(String ptjTotVar) {
		this.ptjTotVar = ptjTotVar;
	}

	/**
	 * Gets the contract no tot var.
	 * 
	 * @return the contract no tot var
	 */
	public String getContractNoTotVar() {
		return contractNoTotVar;
	}

	/**
	 * Sets the contract no tot var.
	 * 
	 * @param contractNoTotVar
	 *            the new contract no tot var
	 */
	public void setContractNoTotVar(String contractNoTotVar) {
		this.contractNoTotVar = contractNoTotVar;
	}

	/**
	 * Gets the sst tot var.
	 * 
	 * @return the sst tot var
	 */
	public String getSstTotVar() {
		return sstTotVar;
	}

	/**
	 * Sets the sst tot var.
	 * 
	 * @param sstTotVar
	 *            the new sst tot var
	 */
	public void setSstTotVar(String sstTotVar) {
		this.sstTotVar = sstTotVar;
	}

	/**
	 * Gets the jabatan tot var.
	 * 
	 * @return the jabatan tot var
	 */
	public String getJabatanTotVar() {
		return jabatanTotVar;
	}

	/**
	 * Sets the jabatan tot var.
	 * 
	 * @param jabatanTotVar
	 *            the new jabatan tot var
	 */
	public void setJabatanTotVar(String jabatanTotVar) {
		this.jabatanTotVar = jabatanTotVar;
	}

	/**
	 * Gets the ministry tot var.
	 * 
	 * @return the ministry tot var
	 */
	public String getMinistryTotVar() {
		return ministryTotVar;
	}

	/**
	 * Sets the ministry tot var.
	 * 
	 * @param ministryTotVar
	 *            the new ministry tot var
	 */
	public void setMinistryTotVar(String ministryTotVar) {
		this.ministryTotVar = ministryTotVar;
	}

	/**
	 * Gets the supplier tot var.
	 * 
	 * @return the supplier tot var
	 */
	public String getSupplierTotVar() {
		return supplierTotVar;
	}

	/**
	 * Sets the supplier tot var.
	 * 
	 * @param supplierTotVar
	 *            the new supplier tot var
	 */
	public void setSupplierTotVar(String supplierTotVar) {
		this.supplierTotVar = supplierTotVar;
	}

	/**
	 * Gets the status tot var.
	 * 
	 * @return the status tot var
	 */
	public String getStatusTotVar() {
		return statusTotVar;
	}

	/**
	 * Sets the status tot var.
	 * 
	 * @param statusTotVar
	 *            the new status tot var
	 */
	public void setStatusTotVar(String statusTotVar) {
		this.statusTotVar = statusTotVar;
	}

	/**
	 * Gets the duration tot var.
	 * 
	 * @return the duration tot var
	 */
	public String getDurationTotVar() {
		return durationTotVar;
	}

	/**
	 * Sets the duration tot var.
	 * 
	 * @param durationTotVar
	 *            the new duration tot var
	 */
	public void setDurationTotVar(String durationTotVar) {
		this.durationTotVar = durationTotVar;
	}

	/**
	 * Gets the recieving officer tot var.
	 * 
	 * @return the recieving officer tot var
	 */
	public String getRecievingOfficerTotVar() {
		return recievingOfficerTotVar;
	}

	/**
	 * Sets the recieving officer tot var.
	 * 
	 * @param recievingOfficerTotVar
	 *            the new recieving officer tot var
	 */
	public void setRecievingOfficerTotVar(String recievingOfficerTotVar) {
		this.recievingOfficerTotVar = recievingOfficerTotVar;
	}

	/**
	 * Gets the delivery address tot var.
	 * 
	 * @return the delivery address tot var
	 */
	public String getDeliveryAddressTotVar() {
		return deliveryAddressTotVar;
	}

	/**
	 * Sets the delivery address tot var.
	 * 
	 * @param deliveryAddressTotVar
	 *            the new delivery address tot var
	 */
	public void setDeliveryAddressTotVar(String deliveryAddressTotVar) {
		this.deliveryAddressTotVar = deliveryAddressTotVar;
	}

	/**
	 * Gets the fl rec note no tot var.
	 * 
	 * @return the fl rec note no tot var
	 */
	public String getFlRecNoteNoTotVar() {
		return flRecNoteNoTotVar;
	}

	/**
	 * Sets the fl rec note no tot var.
	 * 
	 * @param flRecNoteNoTotVar
	 *            the new fl rec note no tot var
	 */
	public void setFlRecNoteNoTotVar(String flRecNoteNoTotVar) {
		this.flRecNoteNoTotVar = flRecNoteNoTotVar;
	}

	/**
	 * Gets the contract type tot var.
	 * 
	 * @return the contract type tot var
	 */
	public String getContractTypeTotVar() {
		return contractTypeTotVar;
	}

	/**
	 * Sets the contract type tot var.
	 * 
	 * @param contractTypeTotVar
	 *            the new contract type tot var
	 */
	public void setContractTypeTotVar(String contractTypeTotVar) {
		this.contractTypeTotVar = contractTypeTotVar;
	}

	/**
	 * Gets the requisitioner tot var.
	 * 
	 * @return the requisitioner tot var
	 */
	public String getRequisitionerTotVar() {
		return requisitionerTotVar;
	}

	/**
	 * Sets the requisitioner tot var.
	 * 
	 * @param requisitionerTotVar
	 *            the new requisitioner tot var
	 */
	public void setRequisitionerTotVar(String requisitionerTotVar) {
		this.requisitionerTotVar = requisitionerTotVar;
	}

	/**
	 * Gets the mto contract tot var.
	 * 
	 * @return the mto contract tot var
	 */
	public String getMtoContractTotVar() {
		return mtoContractTotVar;
	}

	/**
	 * Sets the mto contract tot var.
	 * 
	 * @param mtoContractTotVar
	 *            the new mto contract tot var
	 */
	public void setMtoContractTotVar(String mtoContractTotVar) {
		this.mtoContractTotVar = mtoContractTotVar;
	}

	/**
	 * Gets the invoice no tot var.
	 * 
	 * @return the invoice no tot var
	 */
	public String getInvoiceNoTotVar() {
		return invoiceNoTotVar;
	}

	/**
	 * Sets the invoice no tot var.
	 * 
	 * @param invoiceNoTotVar
	 *            the new invoice no tot var
	 */
	public void setInvoiceNoTotVar(String invoiceNoTotVar) {
		this.invoiceNoTotVar = invoiceNoTotVar;
	}

	/**
	 * Gets the deduction type tot var.
	 * 
	 * @return the deduction type tot var
	 */
	public String getDeductionTypeTotVar() {
		return deductionTypeTotVar;
	}

	/**
	 * Sets the deduction type tot var.
	 * 
	 * @param deductionTypeTotVar
	 *            the new deduction type tot var
	 */
	public void setDeductionTypeTotVar(String deductionTypeTotVar) {
		this.deductionTypeTotVar = deductionTypeTotVar;
	}

	/**
	 * Gets the cr no tot var.
	 * 
	 * @return the cr no tot var
	 */
	public String getCrNoTotVar() {
		return crNoTotVar;
	}

	/**
	 * Sets the cr no tot var.
	 * 
	 * @param crNoTotVar
	 *            the new cr no tot var
	 */
	public void setCrNoTotVar(String crNoTotVar) {
		this.crNoTotVar = crNoTotVar;
	}

	/**
	 * Gets the approver name tot var.
	 * 
	 * @return the approver name tot var
	 */
	public String getApproverNameTotVar() {
		return approverNameTotVar;
	}

	/**
	 * Sets the approver name tot var.
	 * 
	 * @param approverNameTotVar
	 *            the new approver name tot var
	 */
	public void setApproverNameTotVar(String approverNameTotVar) {
		this.approverNameTotVar = approverNameTotVar;
	}

	/**
	 * Gets the document type tot var.
	 * 
	 * @return the document type tot var
	 */
	public String getDocumentTypeTotVar() {
		return documentTypeTotVar;
	}

	/**
	 * Sets the document type tot var.
	 * 
	 * @param documentTypeTotVar
	 *            the new document type tot var
	 */
	public void setDocumentTypeTotVar(String documentTypeTotVar) {
		this.documentTypeTotVar = documentTypeTotVar;
	}

	/**
	 * Gets the criteria tot var.
	 * 
	 * @return the criteria tot var
	 */
	public String getCriteriaTotVar() {
		return criteriaTotVar;
	}

	/**
	 * Sets the criteria tot var.
	 * 
	 * @param criteriaTotVar
	 *            the new criteria tot var
	 */
	public void setCriteriaTotVar(String criteriaTotVar) {
		this.criteriaTotVar = criteriaTotVar;
	}

	/**
	 * Gets the scoring tot var.
	 * 
	 * @return the scoring tot var
	 */
	public String getScoringTotVar() {
		return scoringTotVar;
	}

	/**
	 * Sets the scoring tot var.
	 * 
	 * @param scoringTotVar
	 *            the new scoring tot var
	 */
	public void setScoringTotVar(String scoringTotVar) {
		this.scoringTotVar = scoringTotVar;
	}

	/**
	 * Gets the pkp no tot var.
	 * 
	 * @return the pkp no tot var
	 */
	public String getPkpNoTotVar() {
		return pkpNoTotVar;
	}

	/**
	 * Sets the pkp no tot var.
	 * 
	 * @param pkpNoTotVar
	 *            the new pkp no tot var
	 */
	public void setPkpNoTotVar(String pkpNoTotVar) {
		this.pkpNoTotVar = pkpNoTotVar;
	}

	/**
	 * Gets the delivery order no tot var.
	 * 
	 * @return the delivery order no tot var
	 */
	public String getDeliveryOrderNoTotVar() {
		return deliveryOrderNoTotVar;
	}

	/**
	 * Sets the delivery order no tot var.
	 * 
	 * @param deliveryOrderNoTotVar
	 *            the new delivery order no tot var
	 */
	public void setDeliveryOrderNoTotVar(String deliveryOrderNoTotVar) {
		this.deliveryOrderNoTotVar = deliveryOrderNoTotVar;
	}

	/**
	 * Gets the item code tot var.
	 * 
	 * @return the item code tot var
	 */
	public String getItemCodeTotVar() {
		return itemCodeTotVar;
	}

	/**
	 * Sets the item code tot var.
	 * 
	 * @param itemCodeTotVar
	 *            the new item code tot var
	 */
	public void setItemCodeTotVar(String itemCodeTotVar) {
		this.itemCodeTotVar = itemCodeTotVar;
	}

	/**
	 * Gets the po amount tot var.
	 * 
	 * @return the po amount tot var
	 */
	public String getPoAmountTotVar() {
		return poAmountTotVar;
	}

	/**
	 * Sets the po amount tot var.
	 * 
	 * @param poAmountTotVar
	 *            the new po amount tot var
	 */
	public void setPoAmountTotVar(String poAmountTotVar) {
		this.poAmountTotVar = poAmountTotVar;
	}

	/**
	 * Gets the pr no tot var.
	 * 
	 * @return the pr no tot var
	 */
	public String getPrNoTotVar() {
		return prNoTotVar;
	}

	/**
	 * Sets the pr no tot var.
	 * 
	 * @param prNoTotVar
	 *            the new pr no tot var
	 */
	public void setPrNoTotVar(String prNoTotVar) {
		this.prNoTotVar = prNoTotVar;
	}

	/**
	 * Gets the pmo tot var.
	 * 
	 * @return the pmo tot var
	 */
	public String getPmoTotVar() {
		return pmoTotVar;
	}

	/**
	 * Sets the pmo tot var.
	 * 
	 * @param pmoTotVar
	 *            the new pmo tot var
	 */
	public void setPmoTotVar(String pmoTotVar) {
		this.pmoTotVar = pmoTotVar;
	}

	/**
	 * Checks if is from date view.
	 * 
	 * @return true, if is from date view
	 */
	public boolean isFromDateView() {
		return fromDateView;
	}

	/**
	 * Sets the from date view.
	 * 
	 * @param fromDateView
	 *            the new from date view
	 */
	public void setFromDateView(boolean fromDateView) {
		this.fromDateView = fromDateView;
	}

	/**
	 * Checks if is to date view.
	 * 
	 * @return true, if is to date view
	 */
	public boolean isToDateView() {
		return toDateView;
	}

	/**
	 * Sets the to date view.
	 * 
	 * @param toDateView
	 *            the new to date view
	 */
	public void setToDateView(boolean toDateView) {
		this.toDateView = toDateView;
	}

	/**
	 * Gets the state lst.
	 * 
	 * @return the state lst
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
	 * @return the district lst
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
	 * Gets the lev one cat lst.
	 * 
	 * @return the lev one cat lst
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
	 * @return the lev two cat lst
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
	 * @return the lev three cat lst
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

	/*
	 * public List<String> getStatusLst() { return statusLst; } public void
	 * setStatusLst(List<String> statusLst) { this.statusLst = statusLst; }
	 */

	/**
	 * Retrieve state.
	 */
	public void retrieveState() {
		stateLst = new ArrayList<SelectItem>();
		IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStateSO.class);
		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
		// stateReqDTO.setCountryId(1L);//TODO malaysia
		StateResDTO resStateDTO = stateSO
				.retriveStatesListByCountryCode(stateReqDTO);
		for (PmState state : resStateDTO.getStateList()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(state.getStateName());
			selectItem.setValue(state.getStateId());
			stateLst.add(selectItem);
		}
	}

	/**
	 * Gets the sts lst.
	 * 
	 * @return the sts lst
	 */
	public List<String> getStsLst() {
		return stsLst;
	}

	/**
	 * Sets the sts lst.
	 * 
	 * @param stsLst
	 *            the new sts lst
	 */
	public void setStsLst(List<String> stsLst) {
		this.stsLst = stsLst;
	}

	/**
	 * Gets the status lst.
	 * 
	 * @return the status lst
	 */
	public Object[] getStatusLst() {
		return statusLst;
	}

	/**
	 * Sets the status lst.
	 * 
	 * @param statusLst
	 *            the new status lst
	 */
	public void setStatusLst(Object[] statusLst) {
		this.statusLst = statusLst;
	}

	/**
	 * Retrieve district.
	 * 
	 * @param state
	 *            the state
	 */
	public void retrieveDistrict(String state) {
		districtLst = new ArrayList<SelectItem>();
		IDistrictSO districtSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		if (state != null && state != "") {
			districtReqDTO.setStateId(Long.parseLong(state));
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
	 * Retrieve category lev one.
	 * 
	 * @return the list
	 */
	public List<SelectItem> retrieveCategoryLevOne() {
		levOneCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
				.getServiceObject("pm", ICategoryCodeSO.class);
		// CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
		// CategoryCodeResDTO catCodeResDTO = new CategoryCodeResDTO();
		CategoryCodeResDTO catCodeResDTO = categoryCodeSO
				.retrieveCategoryL1List();
		for (PmCategoryL1 catOne : catCodeResDTO.getCategoryL1List()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(catOne.getCategoryL1Code() + " - "
					+ catOne.getCategoryName());
			selectItem.setValue(catOne.getCategoryL1Id());
		}
		return levOneCatLst;
	}

	/**
	 * Retrieve category lev one1.
	 */
	public void retrieveCategoryLevOne1() {
		levOneCatLst1 = retrieveCategoryLevOne();
	}

	/**
	 * Generate year list.
	 * 
	 * @return the list
	 */
	public List<String> generateYearList() {
		yearList = new ArrayList<String>();
		for (int i = 1950; i <= 2150; i++) {
			yearList.add(Integer.toString(i));
		}
		return yearList;
	}

	/**
	 * Generate common month list.
	 * 
	 * @return the list
	 */
	public List<SelectItem> generateCommonMonthList() {
		monthList = new ArrayList<SelectItem>();
		String month = ReportConst.EMPTY_VAL;
		int monthNoInt;
		String monthNoStr = ReportConst.EMPTY_VAL;
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		for (int i = 0; i <= 11; i++) {
			monthNoInt = i + 1;
			if (monthNoInt < 10) {
				monthNoStr = ReportConst.ZERO_VAL + monthNoInt;
			} else {
				monthNoStr = monthNoInt + ReportConst.EMPTY_VAL;
			}
			month = months[i];
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(month);
			selectItem.setValue(monthNoStr);
			monthList.add(selectItem);
			monthMap.put(monthNoStr, month);
		}
		return monthList;
	}

	/**
	 * Generate common year list.
	 * 
	 * @return the list
	 */
	public List<String> generateCommonYearList() {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
		}
		yearList = new ArrayList<String>();
		if (year != null) {
			for (int i = year - 7; i <= year; i++) {
				yearList.add(Integer.toString(i));
			}
		}
		return yearList;
	}

	/**
	 * Checks if is sc loa content view.
	 * 
	 * @return the scLOAContentView
	 */
	public boolean isScLOAContentView() {
		return scLOAContentView;
	}

	/**
	 * Sets the sc loa content view.
	 * 
	 * @param scLOAContentView
	 *            the scLOAContentView to set
	 */
	public void setScLOAContentView(boolean scLOAContentView) {
		this.scLOAContentView = scLOAContentView;
	}

	/**
	 * Retrieve states by zone name.
	 * 
	 * @param zoneName
	 *            the zone name
	 */
	public void retrieveStatesByZoneName(String zoneName) {

		StateReqDTO stateReqDTO = new StateReqDTO();
		stateLst = new ArrayList<SelectItem>();

		IStateSO stateSO = BusinessDelegate.getInstance().getServiceObject(
				"pm", IStateSO.class);

		stateReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);

		if (stateSO != null && zoneName != null) {
			stateReqDTO.setZoneName(zoneName);
			StateResDTO resStateDTO = stateSO
					.retriveStatesListByCountryCodeWithZoneName(stateReqDTO);
			for (PmState state : resStateDTO.getStateList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(state.getStateName());
				selectItem.setValue(state.getStateId());
				stateLst.add(selectItem);
			}
		} else if (stateSO != null) {
			StateResDTO resStateDTO = stateSO
					.retriveStatesListByCountryCode(stateReqDTO);
			for (PmState state : resStateDTO.getStateList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(state.getStateName());
				selectItem.setValue(state.getStateId());
				stateLst.add(selectItem);
			}
		}
	}

	/**
	 * Checks if is sm mandatory doc supplier type view.
	 * 
	 * @return the smMandatoryDOCSupplierTypeView
	 */
	public boolean isSmMandatoryDOCSupplierTypeView() {
		return smMandatoryDOCSupplierTypeView;
	}

	/**
	 * Sets the sm mandatory doc supplier type view.
	 * 
	 * @param smMandatoryDOCSupplierTypeView
	 *            the smMandatoryDOCSupplierTypeView to set
	 */
	public void setSmMandatoryDOCSupplierTypeView(
			boolean smMandatoryDOCSupplierTypeView) {
		this.smMandatoryDOCSupplierTypeView = smMandatoryDOCSupplierTypeView;
	}

	/**
	 * Checks if is sm mof account end date view.
	 * 
	 * @return the smMOFAccountEndDateView
	 */
	public boolean isSmMOFAccountEndDateView() {
		return smMOFAccountEndDateView;
	}

	/**
	 * Sets the sm mof account end date view.
	 * 
	 * @param smMOFAccountEndDateView
	 *            the smMOFAccountEndDateView to set
	 */
	public void setSmMOFAccountEndDateView(boolean smMOFAccountEndDateView) {
		this.smMOFAccountEndDateView = smMOFAccountEndDateView;
	}

	/**
	 * Gets the mof account end date from date var.
	 * 
	 * @return the mofAccountEndDateFromDateVar
	 */
	public String getMofAccountEndDateFromDateVar() {
		return mofAccountEndDateFromDateVar;
	}

	/**
	 * Sets the mof account end date from date var.
	 * 
	 * @param mofAccountEndDateFromDateVar
	 *            the mofAccountEndDateFromDateVar to set
	 */
	public void setMofAccountEndDateFromDateVar(
			String mofAccountEndDateFromDateVar) {
		this.mofAccountEndDateFromDateVar = mofAccountEndDateFromDateVar;
	}

	/**
	 * Gets the mof account end date to date var.
	 * 
	 * @return the mofAccountEndDateToDateVar
	 */
	public String getMofAccountEndDateToDateVar() {
		return mofAccountEndDateToDateVar;
	}

	/**
	 * Sets the mof account end date to date var.
	 * 
	 * @param mofAccountEndDateToDateVar
	 *            the mofAccountEndDateToDateVar to set
	 */
	public void setMofAccountEndDateToDateVar(String mofAccountEndDateToDateVar) {
		this.mofAccountEndDateToDateVar = mofAccountEndDateToDateVar;
	}

	/**
	 * Checks if is sm payment pr date view.
	 * 
	 * @return the smPaymentPRDateView
	 */
	public boolean isSmPaymentPRDateView() {
		return smPaymentPRDateView;
	}

	/**
	 * Sets the sm payment pr date view.
	 * 
	 * @param smPaymentPRDateView
	 *            the smPaymentPRDateView to set
	 */
	public void setSmPaymentPRDateView(boolean smPaymentPRDateView) {
		this.smPaymentPRDateView = smPaymentPRDateView;
	}

	/**
	 * Checks if is sm personnel staff role view.
	 * 
	 * @return the smPersonnelStaffRoleView
	 */
	public boolean isSmPersonnelStaffRoleView() {
		return smPersonnelStaffRoleView;
	}

	/**
	 * Sets the sm personnel staff role view.
	 * 
	 * @param smPersonnelStaffRoleView
	 *            the smPersonnelStaffRoleView to set
	 */
	public void setSmPersonnelStaffRoleView(boolean smPersonnelStaffRoleView) {
		this.smPersonnelStaffRoleView = smPersonnelStaffRoleView;
	}

	/**
	 * Checks if is sm mandatory year from report type view.
	 * 
	 * @return the smMandatoryYearFromReportTypeView
	 */
	public boolean isSmMandatoryYearFromReportTypeView() {
		return smMandatoryYearFromReportTypeView;
	}

	/**
	 * Sets the sm mandatory year from report type view.
	 * 
	 * @param smMandatoryYearFromReportTypeView
	 *            the smMandatoryYearFromReportTypeView to set
	 */
	public void setSmMandatoryYearFromReportTypeView(
			boolean smMandatoryYearFromReportTypeView) {
		this.smMandatoryYearFromReportTypeView = smMandatoryYearFromReportTypeView;
	}

	/**
	 * Checks if is pp mandatory view.
	 * 
	 * @return the ppMandatoryView
	 */
	public boolean isPpMandatoryView() {
		return ppMandatoryView;
	}

	/**
	 * Sets the pp mandatory view.
	 * 
	 * @param ppMandatoryView
	 *            the ppMandatoryView to set
	 */
	public void setPpMandatoryView(boolean ppMandatoryView) {
		this.ppMandatoryView = ppMandatoryView;
	}

	/**
	 * Checks if is pp non mandatory view.
	 * 
	 * @return the ppNonMandatoryView
	 */
	public boolean isPpNonMandatoryView() {
		return ppNonMandatoryView;
	}

	/**
	 * Sets the pp non mandatory view.
	 * 
	 * @param ppNonMandatoryView
	 *            the ppNonMandatoryView to set
	 */
	public void setPpNonMandatoryView(boolean ppNonMandatoryView) {
		this.ppNonMandatoryView = ppNonMandatoryView;
	}

	/**
	 * Checks if is sm mandatory transaction date view.
	 * 
	 * @return the smMandatoryTransactDateView
	 */
	public boolean isSmMandatoryTransactionDateView() {
		return smMandatoryTransactionDateView;
	}

	/**
	 * Sets the sm mandatory transaction date view.
	 * 
	 * @param smMandatoryTransactionDateView
	 *            the new sm mandatory transaction date view
	 */
	public void setSmMandatoryTransactionDateView(
			boolean smMandatoryTransactionDateView) {
		this.smMandatoryTransactionDateView = smMandatoryTransactionDateView;
	}

	/**
	 * Checks if is sm mandatory receipt date view.
	 * 
	 * @return the smMandatoryReceiptDateView
	 */
	public boolean isSmMandatoryReceiptDateView() {
		return smMandatoryReceiptDateView;
	}

	/**
	 * Sets the sm mandatory receipt date view.
	 * 
	 * @param smMandatoryReceiptDateView
	 *            the smMandatoryReceiptDateView to set
	 */
	public void setSmMandatoryReceiptDateView(boolean smMandatoryReceiptDateView) {
		this.smMandatoryReceiptDateView = smMandatoryReceiptDateView;
	}

	/**
	 * Checks if is sm supplier name view.
	 * 
	 * @return the smSupplierNameView
	 */
	public boolean isSmSupplierNameView() {
		return smSupplierNameView;
	}

	/**
	 * Sets the sm supplier name view.
	 * 
	 * @param smSupplierNameView
	 *            the smSupplierNameView to set
	 */
	public void setSmSupplierNameView(boolean smSupplierNameView) {
		this.smSupplierNameView = smSupplierNameView;
	}

	/**
	 * Checks if is sm supplier name.
	 * 
	 * @return the smSupplierName
	 */
	public boolean isSmSupplierName() {
		return smSupplierName;
	}

	/**
	 * Sets the sm supplier name.
	 * 
	 * @param smSupplierName
	 *            the smSupplierName to set
	 */
	public void setSmSupplierName(boolean smSupplierName) {
		this.smSupplierName = smSupplierName;
	}

	/**
	 * Checks if is sm mandatory recipt transaction report type view.
	 * 
	 * @return the smMandatoryReciptTransactionReportTypeView
	 */
	public boolean isSmMandatoryReciptTransactionReportTypeView() {
		return smMandatoryReciptTransactionReportTypeView;
	}

	/**
	 * Sets the sm mandatory recipt transaction report type view.
	 * 
	 * @param smMandatoryReciptTransactionReportTypeView
	 *            the smMandatoryReciptTransactionReportTypeView to set
	 */
	public void setSmMandatoryReciptTransactionReportTypeView(
			boolean smMandatoryReciptTransactionReportTypeView) {
		this.smMandatoryReciptTransactionReportTypeView = smMandatoryReciptTransactionReportTypeView;
	}

	/**
	 * Checks if is sm mandatory rt date view.
	 * 
	 * @return the smMandatoryRTDateView
	 */
	public boolean isSmMandatoryRTDateView() {
		return smMandatoryRTDateView;
	}

	/**
	 * Sets the sm mandatory rt date view.
	 * 
	 * @param smMandatoryRTDateView
	 *            the smMandatoryRTDateView to set
	 */
	public void setSmMandatoryRTDateView(boolean smMandatoryRTDateView) {
		this.smMandatoryRTDateView = smMandatoryRTDateView;
	}

	/**
	 * Checks if is collector gov registration chk view.
	 * 
	 * @return the collectorGovRegistrationChkView
	 */
	public boolean isCollectorGovRegistrationChkView() {
		return collectorGovRegistrationChkView;
	}

	/**
	 * Sets the collector gov registration chk view.
	 * 
	 * @param collectorGovRegistrationChkView
	 *            the collectorGovRegistrationChkView to set
	 */
	public void setCollectorGovRegistrationChkView(
			boolean collectorGovRegistrationChkView) {
		this.collectorGovRegistrationChkView = collectorGovRegistrationChkView;
	}

	/**
	 * Checks if is collector cdc registration chk view.
	 * 
	 * @return the collectorCDCRegistrationChkView
	 */
	public boolean isCollectorCDCRegistrationChkView() {
		return collectorCDCRegistrationChkView;
	}

	/**
	 * Sets the collector cdc registration chk view.
	 * 
	 * @param collectorCDCRegistrationChkView
	 *            the collectorCDCRegistrationChkView to set
	 */
	public void setCollectorCDCRegistrationChkView(
			boolean collectorCDCRegistrationChkView) {
		this.collectorCDCRegistrationChkView = collectorCDCRegistrationChkView;
	}

	/**
	 * Checks if is pm mandatory org category type view.
	 * 
	 * @return the pmMandatoryOrgCategoryTypeView
	 */
	public boolean isPmMandatoryOrgCategoryTypeView() {
		return pmMandatoryOrgCategoryTypeView;
	}

	/**
	 * Sets the pm mandatory org category type view.
	 * 
	 * @param pmMandatoryOrgCategoryTypeView
	 *            the pmMandatoryOrgCategoryTypeView to set
	 */
	public void setPmMandatoryOrgCategoryTypeView(
			boolean pmMandatoryOrgCategoryTypeView) {
		this.pmMandatoryOrgCategoryTypeView = pmMandatoryOrgCategoryTypeView;
	}

	/**
	 * Checks if is pm mandatory org type view.
	 * 
	 * @return the pmMandatoryOrgTypeView
	 */
	public boolean isPmMandatoryOrgTypeView() {
		return pmMandatoryOrgTypeView;
	}

	/**
	 * Sets the pm mandatory org type view.
	 * 
	 * @param pmMandatoryOrgTypeView
	 *            the pmMandatoryOrgTypeView to set
	 */
	public void setPmMandatoryOrgTypeView(boolean pmMandatoryOrgTypeView) {
		this.pmMandatoryOrgTypeView = pmMandatoryOrgTypeView;
	}

	/**
	 * Checks if is pm org name view.
	 * 
	 * @return the pmOrgNameView
	 */
	public boolean isPmOrgNameView() {
		return pmOrgNameView;
	}

	/**
	 * Sets the pm org name view.
	 * 
	 * @param pmOrgNameView
	 *            the pmOrgNameView to set
	 */
	public void setPmOrgNameView(boolean pmOrgNameView) {
		this.pmOrgNameView = pmOrgNameView;
	}

	/**
	 * Checks if is pm org code view.
	 * 
	 * @return the pmOrgCodeView
	 */
	public boolean isPmOrgCodeView() {
		return pmOrgCodeView;
	}

	/**
	 * Sets the pm org code view.
	 * 
	 * @param pmOrgCodeView
	 *            the pmOrgCodeView to set
	 */
	public void setPmOrgCodeView(boolean pmOrgCodeView) {
		this.pmOrgCodeView = pmOrgCodeView;
	}

	/**
	 * Checks if is pm mandatory org name view.
	 * 
	 * @return the pmMandatoryOrgNameView
	 */
	public boolean isPmMandatoryOrgNameView() {
		return pmMandatoryOrgNameView;
	}

	/**
	 * Sets the pm mandatory org name view.
	 * 
	 * @param pmMandatoryOrgNameView
	 *            the pmMandatoryOrgNameView to set
	 */
	public void setPmMandatoryOrgNameView(boolean pmMandatoryOrgNameView) {
		this.pmMandatoryOrgNameView = pmMandatoryOrgNameView;
	}

	/**
	 * Checks if is pm identification no view.
	 * 
	 * @return the pmIdentificationNoView
	 */
	public boolean isPmIdentificationNoView() {
		return pmIdentificationNoView;
	}

	/**
	 * Sets the pm identification no view.
	 * 
	 * @param pmIdentificationNoView
	 *            the pmIdentificationNoView to set
	 */
	public void setPmIdentificationNoView(boolean pmIdentificationNoView) {
		this.pmIdentificationNoView = pmIdentificationNoView;
	}

	/**
	 * Checks if is pm user name view.
	 * 
	 * @return the pmUserNameView
	 */
	public boolean isPmUserNameView() {
		return pmUserNameView;
	}

	/**
	 * Sets the pm user name view.
	 * 
	 * @param pmUserNameView
	 *            the pmUserNameView to set
	 */
	public void setPmUserNameView(boolean pmUserNameView) {
		this.pmUserNameView = pmUserNameView;
	}

	/**
	 * Checks if is pm role code view.
	 * 
	 * @return the pmRoleCodeView
	 */
	public boolean isPmRoleCodeView() {
		return pmRoleCodeView;
	}

	/**
	 * Sets the pm role code view.
	 * 
	 * @param pmRoleCodeView
	 *            the pmRoleCodeView to set
	 */
	public void setPmRoleCodeView(boolean pmRoleCodeView) {
		this.pmRoleCodeView = pmRoleCodeView;
	}

	/**
	 * Checks if is pm non mandatory org name view.
	 * 
	 * @return the pmNonMandatoryOrgNameView
	 */
	public boolean isPmNonMandatoryOrgNameView() {
		return pmNonMandatoryOrgNameView;
	}

	/**
	 * Sets the pm non mandatory org name view.
	 * 
	 * @param pmNonMandatoryOrgNameView
	 *            the pmNonMandatoryOrgNameView to set
	 */
	public void setPmNonMandatoryOrgNameView(boolean pmNonMandatoryOrgNameView) {
		this.pmNonMandatoryOrgNameView = pmNonMandatoryOrgNameView;
	}

	/**
	 * Checks if is pm non mandatroy org code view.
	 * 
	 * @return the pmNonMandatroyOrgCodeView
	 */
	public boolean isPmNonMandatroyOrgCodeView() {
		return pmNonMandatroyOrgCodeView;
	}

	/**
	 * Sets the pm non mandatroy org code view.
	 * 
	 * @param pmNonMandatroyOrgCodeView
	 *            the pmNonMandatroyOrgCodeView to set
	 */
	public void setPmNonMandatroyOrgCodeView(boolean pmNonMandatroyOrgCodeView) {
		this.pmNonMandatroyOrgCodeView = pmNonMandatroyOrgCodeView;
	}

	/**
	 * Checks if is pm company name view.
	 * 
	 * @return the pmCompanyNameView
	 */
	public boolean isPmCompanyNameView() {
		return pmCompanyNameView;
	}

	/**
	 * Sets the pm company name view.
	 * 
	 * @param pmCompanyNameView
	 *            the pmCompanyNameView to set
	 */
	public void setPmCompanyNameView(boolean pmCompanyNameView) {
		this.pmCompanyNameView = pmCompanyNameView;
	}

	/**
	 * Checks if is pm ep no view.
	 * 
	 * @return the pmEpNoView
	 */
	public boolean isPmEpNoView() {
		return pmEpNoView;
	}

	/**
	 * Sets the pm ep no view.
	 * 
	 * @param pmEpNoView
	 *            the pmEpNoView to set
	 */
	public void setPmEpNoView(boolean pmEpNoView) {
		this.pmEpNoView = pmEpNoView;
	}

	/**
	 * Checks if is pm item name view.
	 * 
	 * @return the pmItmeNameView
	 */
	public boolean isPmItemNameView() {
		return pmItemNameView;
	}

	/**
	 * Sets the pm item name view.
	 * 
	 * @param pmItemNameView
	 *            the new pm item name view
	 */
	public void setPmItemNameView(boolean pmItemNameView) {
		this.pmItemNameView = pmItemNameView;
	}

	/**
	 * Checks if is pm item code view.
	 * 
	 * @return the pmItmeCodeView
	 */
	public boolean isPmItemCodeView() {
		return pmItemCodeView;
	}

	/**
	 * Sets the pm itme code view.
	 * 
	 * @param pmItemCodeView
	 *            the new pm itme code view
	 */
	public void setPmItmeCodeView(boolean pmItemCodeView) {
		this.pmItemCodeView = pmItemCodeView;
	}

	/**
	 * Checks if is pm from date view.
	 * 
	 * @return the pmFromDateView
	 */
	public boolean isPmFromDateView() {
		return pmFromDateView;
	}

	/**
	 * Sets the pm from date view.
	 * 
	 * @param pmFromDateView
	 *            the pmFromDateView to set
	 */
	public void setPmFromDateView(boolean pmFromDateView) {
		this.pmFromDateView = pmFromDateView;
	}

	/**
	 * Checks if is pm non mandatory date view.
	 * 
	 * @return the pmNonMandatoryDateView
	 */
	public boolean isPmNonMandatoryDateView() {
		return pmNonMandatoryDateView;
	}

	/**
	 * Sets the pm non mandatory date view.
	 * 
	 * @param pmNonMandatoryDateView
	 *            the pmNonMandatoryDateView to set
	 */
	public void setPmNonMandatoryDateView(boolean pmNonMandatoryDateView) {
		this.pmNonMandatoryDateView = pmNonMandatoryDateView;
	}

	/**
	 * Checks if is pm contract no view.
	 * 
	 * @return the pmContractNoView
	 */
	public boolean isPmContractNoView() {
		return pmContractNoView;
	}

	/**
	 * Sets the pm contract no view.
	 * 
	 * @param pmContractNoView
	 *            the pmContractNoView to set
	 */
	public void setPmContractNoView(boolean pmContractNoView) {
		this.pmContractNoView = pmContractNoView;
	}

	/**
	 * Gets the pm ep no var.
	 * 
	 * @return the pmEpNoVarr
	 */
	public String getPmEpNoVar() {
		return pmEpNoVar;
	}

	/**
	 * Sets the pm ep no var.
	 * 
	 * @param pmEpNoVar
	 *            the new pm ep no var
	 */
	public void setPmEpNoVar(String pmEpNoVar) {
		this.pmEpNoVar = pmEpNoVar;
	}

	/**
	 * Gets the pm org category var.
	 * 
	 * @return the pmOrgCategoryVar
	 */
	public String getPmOrgCategoryVar() {
		return pmOrgCategoryVar;
	}

	/**
	 * Sets the pm org category var.
	 * 
	 * @param pmOrgCategoryVar
	 *            the pmOrgCategoryVar to set
	 */
	public void setPmOrgCategoryVar(String pmOrgCategoryVar) {
		this.pmOrgCategoryVar = pmOrgCategoryVar;
	}

	/**
	 * Gets the pm org type var.
	 * 
	 * @return the pmOrgTypeVar
	 */
	public String getPmOrgTypeVar() {
		return pmOrgTypeVar;
	}

	/**
	 * Sets the pm org type var.
	 * 
	 * @param pmOrgTypeVar
	 *            the pmOrgTypeVar to set
	 */
	public void setPmOrgTypeVar(String pmOrgTypeVar) {
		this.pmOrgTypeVar = pmOrgTypeVar;
	}

	/**
	 * Gets the pm org name var.
	 * 
	 * @return the pmOrgNameVar
	 */
	public String getPmOrgNameVar() {
		return pmOrgNameVar;
	}

	/**
	 * Sets the pm org name var.
	 * 
	 * @param pmOrgNameVar
	 *            the pmOrgNameVar to set
	 */
	public void setPmOrgNameVar(String pmOrgNameVar) {
		this.pmOrgNameVar = pmOrgNameVar;
	}

	/**
	 * Gets the pm org code var.
	 * 
	 * @return the pmOrgCodeVar
	 */
	public String getPmOrgCodeVar() {
		return pmOrgCodeVar;
	}

	/**
	 * Sets the pm org code var.
	 * 
	 * @param pmOrgCodeVar
	 *            the pmOrgCodeVar to set
	 */
	public void setPmOrgCodeVar(String pmOrgCodeVar) {
		this.pmOrgCodeVar = pmOrgCodeVar;
	}

	/**
	 * Gets the pm item name var.
	 * 
	 * @return the pmItemNameVar
	 */
	public String getPmItemNameVar() {
		return pmItemNameVar;
	}

	/**
	 * Sets the pm item name var.
	 * 
	 * @param pmItemNameVar
	 *            the pmItemNameVar to set
	 */
	public void setPmItemNameVar(String pmItemNameVar) {
		this.pmItemNameVar = pmItemNameVar;
	}

	/**
	 * Gets the pm item code var.
	 * 
	 * @return the pmItemCodeVar
	 */
	public String getPmItemCodeVar() {
		return pmItemCodeVar;
	}

	/**
	 * Sets the pm item code var.
	 * 
	 * @param pmItemCodeVar
	 *            the pmItemCodeVar to set
	 */
	public void setPmItemCodeVar(String pmItemCodeVar) {
		this.pmItemCodeVar = pmItemCodeVar;
	}

	/**
	 * Gets the pm company name var.
	 * 
	 * @return the pmCompanyNameVar
	 */
	public String getPmCompanyNameVar() {
		return pmCompanyNameVar;
	}

	/**
	 * Sets the pm company name var.
	 * 
	 * @param pmCompanyNameVar
	 *            the pmCompanyNameVar to set
	 */
	public void setPmCompanyNameVar(String pmCompanyNameVar) {
		this.pmCompanyNameVar = pmCompanyNameVar;
	}

	/**
	 * Gets the pm identification no var.
	 * 
	 * @return the pmIdentificationNoVar
	 */
	public String getPmIdentificationNoVar() {
		return pmIdentificationNoVar;
	}

	/**
	 * Sets the pm identification no var.
	 * 
	 * @param pmIdentificationNoVar
	 *            the pmIdentificationNoVar to set
	 */
	public void setPmIdentificationNoVar(String pmIdentificationNoVar) {
		this.pmIdentificationNoVar = pmIdentificationNoVar;
	}

	/**
	 * Gets the pm user name var.
	 * 
	 * @return the pmUserName
	 */
	public String getPmUserNameVar() {
		return pmUserNameVar;
	}

	/**
	 * Sets the pm user name var.
	 * 
	 * @param pmUserNameVar
	 *            the new pm user name var
	 */
	public void setPmUserNameVar(String pmUserNameVar) {
		this.pmUserNameVar = pmUserNameVar;
	}

	/**
	 * Gets the pm role code var.
	 * 
	 * @return the pmRoleCodeVar
	 */
	public String getPmRoleCodeVar() {
		return pmRoleCodeVar;
	}

	/**
	 * Sets the pm role code var.
	 * 
	 * @param pmRoleCodeVar
	 *            the pmRoleCodeVar to set
	 */
	public void setPmRoleCodeVar(String pmRoleCodeVar) {
		this.pmRoleCodeVar = pmRoleCodeVar;
	}

	/**
	 * Gets the pm contract no var.
	 * 
	 * @return the pmContractNoVar
	 */
	public String getPmContractNoVar() {
		return pmContractNoVar;
	}

	/**
	 * Sets the pm contract no var.
	 * 
	 * @param pmContractNoVar
	 *            the pmContractNoVar to set
	 */
	public void setPmContractNoVar(String pmContractNoVar) {
		this.pmContractNoVar = pmContractNoVar;
	}

	/**
	 * Sets the pm item code view.
	 * 
	 * @param pmItemCodeView
	 *            the pmItemCodeView to set
	 */
	public void setPmItemCodeView(boolean pmItemCodeView) {
		this.pmItemCodeView = pmItemCodeView;
	}

	/**
	 * Checks if is pm physical contract no view.
	 * 
	 * @return the pmPhysicalContractNoView
	 */
	public boolean isPmPhysicalContractNoView() {
		return pmPhysicalContractNoView;
	}

	/**
	 * Sets the pm physical contract no view.
	 * 
	 * @param pmPhysicalContractNoView
	 *            the pmPhysicalContractNoView to set
	 */
	public void setPmPhysicalContractNoView(boolean pmPhysicalContractNoView) {
		this.pmPhysicalContractNoView = pmPhysicalContractNoView;
	}

	/**
	 * Checks if is pm loa no view.
	 * 
	 * @return the pmLOANoView
	 */
	public boolean isPmLOANoView() {
		return pmLOANoView;
	}

	/**
	 * Sets the pm loa no view.
	 * 
	 * @param pmLOANoView
	 *            the pmLOANoView to set
	 */
	public void setPmLOANoView(boolean pmLOANoView) {
		this.pmLOANoView = pmLOANoView;
	}

	/**
	 * Checks if is pm physical loa no view.
	 * 
	 * @return the pmPhysicalLOANoView
	 */
	public boolean isPmPhysicalLOANoView() {
		return pmPhysicalLOANoView;
	}

	/**
	 * Sets the pm physical loa no view.
	 * 
	 * @param pmPhysicalLOANoView
	 *            the pmPhysicalLOANoView to set
	 */
	public void setPmPhysicalLOANoView(boolean pmPhysicalLOANoView) {
		this.pmPhysicalLOANoView = pmPhysicalLOANoView;
	}

	/**
	 * Gets the pm physical contract no var.
	 * 
	 * @return the pmPhysicalContractNoVar
	 */
	public String getPmPhysicalContractNoVar() {
		return pmPhysicalContractNoVar;
	}

	/**
	 * Sets the pm physical contract no var.
	 * 
	 * @param pmPhysicalContractNoVar
	 *            the pmPhysicalContractNoVar to set
	 */
	public void setPmPhysicalContractNoVar(String pmPhysicalContractNoVar) {
		this.pmPhysicalContractNoVar = pmPhysicalContractNoVar;
	}

	/**
	 * Gets the pm loa no var.
	 * 
	 * @return the pmLOANoVar
	 */
	public String getPmLOANoVar() {
		return pmLOANoVar;
	}

	/**
	 * Sets the pm loa no var.
	 * 
	 * @param pmLOANoVar
	 *            the pmLOANoVar to set
	 */
	public void setPmLOANoVar(String pmLOANoVar) {
		this.pmLOANoVar = pmLOANoVar;
	}

	/**
	 * Gets the pm physical loa no var.
	 * 
	 * @return the pmPhysicalLOANoVar
	 */
	public String getPmPhysicalLOANoVar() {
		return pmPhysicalLOANoVar;
	}

	/**
	 * Sets the pm physical loa no var.
	 * 
	 * @param pmPhysicalLOANoVar
	 *            the pmPhysicalLOANoVar to set
	 */
	public void setPmPhysicalLOANoVar(String pmPhysicalLOANoVar) {
		this.pmPhysicalLOANoVar = pmPhysicalLOANoVar;
	}

	/**
	 * Checks if is pm exempted company name view.
	 * 
	 * @return the pmExemptedCompanyNameView
	 */
	public boolean isPmExemptedCompanyNameView() {
		return pmExemptedCompanyNameView;
	}

	/**
	 * Sets the pm exempted company name view.
	 * 
	 * @param pmExemptedCompanyNameView
	 *            the pmExemptedCompanyNameView to set
	 */
	public void setPmExemptedCompanyNameView(boolean pmExemptedCompanyNameView) {
		this.pmExemptedCompanyNameView = pmExemptedCompanyNameView;
	}

	/**
	 * Checks if is pm exempted business reg no view.
	 * 
	 * @return the pmExemptedBusinessRegNoView
	 */
	public boolean isPmExemptedBusinessRegNoView() {
		return pmExemptedBusinessRegNoView;
	}

	/**
	 * Sets the pm exempted business reg no view.
	 * 
	 * @param pmExemptedBusinessRegNoView
	 *            the pmExemptedBusinessRegNoView to set
	 */
	public void setPmExemptedBusinessRegNoView(
			boolean pmExemptedBusinessRegNoView) {
		this.pmExemptedBusinessRegNoView = pmExemptedBusinessRegNoView;
	}

	/**
	 * Checks if is pm exempted ep no view.
	 * 
	 * @return the pmExemptedEpNoView
	 */
	public boolean isPmExemptedEpNoView() {
		return pmExemptedEpNoView;
	}

	/**
	 * Sets the pm exempted ep no view.
	 * 
	 * @param pmExemptedEpNoView
	 *            the pmExemptedEpNoView to set
	 */
	public void setPmExemptedEpNoView(boolean pmExemptedEpNoView) {
		this.pmExemptedEpNoView = pmExemptedEpNoView;
	}

	/**
	 * Checks if is pm exempted item name view.
	 * 
	 * @return the pmExemptedItemNameView
	 */
	public boolean isPmExemptedItemNameView() {
		return pmExemptedItemNameView;
	}

	/**
	 * Sets the pm exempted item name view.
	 * 
	 * @param pmExemptedItemNameView
	 *            the pmExemptedItemNameView to set
	 */
	public void setPmExemptedItemNameView(boolean pmExemptedItemNameView) {
		this.pmExemptedItemNameView = pmExemptedItemNameView;
	}

	/**
	 * Checks if is pm exempted itme code view.
	 * 
	 * @return the pmExemptedItmeCodeView
	 */
	public boolean isPmExemptedItemCodeView() {
		return pmExemptedItemCodeView;
	}

	/**
	 * Sets the pm exempted itme code view.
	 * 
	 * @param pmExemptedItemCodeView
	 *            the new pm exempted item code view
	 */
	public void setPmExemptedItemCodeView(boolean pmExemptedItemCodeView) {
		this.pmExemptedItemCodeView = pmExemptedItemCodeView;
	}

	/**
	 * Checks if is sm virtual cert date view.
	 * 
	 * @return the smVirtualCertDateView
	 */
	public boolean isSmVirtualCertDateView() {
		return smVirtualCertDateView;
	}

	/**
	 * Sets the sm virtual cert date view.
	 * 
	 * @param smVirtualCertDateView
	 *            the smVirtualCertDateView to set
	 */
	public void setSmVirtualCertDateView(boolean smVirtualCertDateView) {
		this.smVirtualCertDateView = smVirtualCertDateView;
	}

	/**
	 * Checks if is sm ep mof reg no view.
	 * 
	 * @return the smEpMofRegNoView
	 */
	public boolean isSmEpMofRegNoView() {
		return smEpMofRegNoView;
	}

	/**
	 * Sets the sm ep mof reg no view.
	 * 
	 * @param smEpMofRegNoView
	 *            the smEpMofRegNoView to set
	 */
	public void setSmEpMofRegNoView(boolean smEpMofRegNoView) {
		this.smEpMofRegNoView = smEpMofRegNoView;
	}

	/**
	 * Checks if is sm mandatory expected site visi rptt date view.
	 * 
	 * @return the smMandatoryExpectedSiteVisiRpttDateView
	 */
	public boolean isSmMandatoryExpectedSiteVisiRpttDateView() {
		return smMandatoryExpectedSiteVisiRpttDateView;
	}

	/**
	 * Sets the sm mandatory expected site visi rptt date view.
	 * 
	 * @param smMandatoryExpectedSiteVisiRpttDateView
	 *            the smMandatoryExpectedSiteVisiRpttDateView to set
	 */
	public void setSmMandatoryExpectedSiteVisiRpttDateView(
			boolean smMandatoryExpectedSiteVisiRpttDateView) {
		this.smMandatoryExpectedSiteVisiRpttDateView = smMandatoryExpectedSiteVisiRpttDateView;
	}

	/**
	 * Checks if is sm non mandatory category view.
	 * 
	 * @return the smNonMandatoryCategoryView
	 */
	public boolean isSmNonMandatoryCategoryView() {
		return smNonMandatoryCategoryView;
	}

	/**
	 * Sets the sm non mandatory category view.
	 * 
	 * @param smNonMandatoryCategoryView
	 *            the smNonMandatoryCategoryView to set
	 */
	public void setSmNonMandatoryCategoryView(boolean smNonMandatoryCategoryView) {
		this.smNonMandatoryCategoryView = smNonMandatoryCategoryView;
	}

	/**
	 * Checks if is sm soft cert req date view.
	 * 
	 * @return the smSoftCertReqDateView
	 */
	public boolean isSmSoftCertReqDateView() {
		return smSoftCertReqDateView;
	}

	/**
	 * Sets the sm soft cert req date view.
	 * 
	 * @param smSoftCertReqDateView
	 *            the smSoftCertReqDateView to set
	 */
	public void setSmSoftCertReqDateView(boolean smSoftCertReqDateView) {
		this.smSoftCertReqDateView = smSoftCertReqDateView;
	}

	/**
	 * Checks if is sm soft cert exp date view.
	 * 
	 * @return the smSoftCertExpDateView
	 */
	public boolean isSmSoftCertExpDateView() {
		return smSoftCertExpDateView;
	}

	/**
	 * Sets the sm soft cert exp date view.
	 * 
	 * @param smSoftCertExpDateView
	 *            the smSoftCertExpDateView to set
	 */
	public void setSmSoftCertExpDateView(boolean smSoftCertExpDateView) {
		this.smSoftCertExpDateView = smSoftCertExpDateView;
	}

	/**
	 * Checks if is sm bus company type view.
	 * 
	 * @return the smBusCompanyTypeView
	 */
	public boolean isSmBusCompanyTypeView() {
		return smBusCompanyTypeView;
	}

	/**
	 * Sets the sm bus company type view.
	 * 
	 * @param smBusCompanyTypeView
	 *            the smBusCompanyTypeView to set
	 */
	public void setSmBusCompanyTypeView(boolean smBusCompanyTypeView) {
		this.smBusCompanyTypeView = smBusCompanyTypeView;
	}

	/**
	 * Checks if is sm bus company paid up accumlated share view.
	 * 
	 * @return the smBusCompanyPaidUpAccumlatedShareView
	 */
	public boolean isSmBusCompanyPaidUpAccumlatedShareView() {
		return smBusCompanyPaidUpAccumlatedShareView;
	}

	/**
	 * Sets the sm bus company paid up accumlated share view.
	 * 
	 * @param smBusCompanyPaidUpAccumlatedShareView
	 *            the smBusCompanyPaidUpAccumlatedShareView to set
	 */
	public void setSmBusCompanyPaidUpAccumlatedShareView(
			boolean smBusCompanyPaidUpAccumlatedShareView) {
		this.smBusCompanyPaidUpAccumlatedShareView = smBusCompanyPaidUpAccumlatedShareView;
	}

	/**
	 * Checks if is sm paid up cptl sel view.
	 * 
	 * @return the smPaidUpCptlSelView
	 */
	public boolean isSmPaidUpCptlSelView() {
		return smPaidUpCptlSelView;
	}

	/**
	 * Sets the sm paid up cptl sel view.
	 * 
	 * @param smPaidUpCptlSelView
	 *            the smPaidUpCptlSelView to set
	 */
	public void setSmPaidUpCptlSelView(boolean smPaidUpCptlSelView) {
		this.smPaidUpCptlSelView = smPaidUpCptlSelView;
	}

	/**
	 * Checks if is sm accmltd cptl sel view.
	 * 
	 * @return the smAccmltdCptlSelView
	 */
	public boolean isSmAccmltdCptlSelView() {
		return smAccmltdCptlSelView;
	}

	/**
	 * Sets the sm accmltd cptl sel view.
	 * 
	 * @param smAccmltdCptlSelView
	 *            the smAccmltdCptlSelView to set
	 */
	public void setSmAccmltdCptlSelView(boolean smAccmltdCptlSelView) {
		this.smAccmltdCptlSelView = smAccmltdCptlSelView;
	}

	/**
	 * Checks if is sm accmltd share sel view.
	 * 
	 * @return the smAccmltdShareSelView
	 */
	public boolean isSmAccmltdShareSelView() {
		return smAccmltdShareSelView;
	}

	/**
	 * Sets the sm accmltd share sel view.
	 * 
	 * @param smAccmltdShareSelView
	 *            the smAccmltdShareSelView to set
	 */
	public void setSmAccmltdShareSelView(boolean smAccmltdShareSelView) {
		this.smAccmltdShareSelView = smAccmltdShareSelView;
	}

	/**
	 * Checks if is sm paid accmltd share cptl sel view.
	 * 
	 * @return the smPaidAccmltdShareCptlSelView
	 */
	public boolean isSmPaidAccmltdShareCptlSelView() {
		return smPaidAccmltdShareCptlSelView;
	}

	/**
	 * Sets the sm paid accmltd share cptl sel view.
	 * 
	 * @param smPaidAccmltdShareCptlSelView
	 *            the smPaidAccmltdShareCptlSelView to set
	 */
	public void setSmPaidAccmltdShareCptlSelView(
			boolean smPaidAccmltdShareCptlSelView) {
		this.smPaidAccmltdShareCptlSelView = smPaidAccmltdShareCptlSelView;
	}

	/**
	 * Checks if is sm bus company registration status view.
	 * 
	 * @return the smBusCompanyRegistrationStatusView
	 */
	public boolean isSmBusCompanyRegistrationStatusView() {
		return smBusCompanyRegistrationStatusView;
	}

	/**
	 * Sets the sm bus company registration status view.
	 * 
	 * @param smBusCompanyRegistrationStatusView
	 *            the smBusCompanyRegistrationStatusView to set
	 */
	public void setSmBusCompanyRegistrationStatusView(
			boolean smBusCompanyRegistrationStatusView) {
		this.smBusCompanyRegistrationStatusView = smBusCompanyRegistrationStatusView;
	}

	/**
	 * Gets the sm bus comp type registration status by var.
	 * 
	 * @return the smBusCompTypeRegistrationStatusByVar
	 */
	public String getSmBusCompTypeRegistrationStatusByVar() {
		return smBusCompTypeRegistrationStatusByVar;
	}

	/**
	 * Sets the sm bus comp type registration status by var.
	 * 
	 * @param smBusCompTypeRegistrationStatusByVar
	 *            the smBusCompTypeRegistrationStatusByVar to set
	 */
	public void setSmBusCompTypeRegistrationStatusByVar(
			String smBusCompTypeRegistrationStatusByVar) {
		this.smBusCompTypeRegistrationStatusByVar = smBusCompTypeRegistrationStatusByVar;
	}

	/**
	 * Gets the sm bus company type by var.
	 * 
	 * @return the smBusCompanyTypeByVar
	 */
	public String getSmBusCompanyTypeByVar() {
		return smBusCompanyTypeByVar;
	}

	/**
	 * Sets the sm bus company type by var.
	 * 
	 * @param smBusCompanyTypeByVar
	 *            the smBusCompanyTypeByVar to set
	 */
	public void setSmBusCompanyTypeByVar(String smBusCompanyTypeByVar) {
		this.smBusCompanyTypeByVar = smBusCompanyTypeByVar;
	}

	/**
	 * Gets the sm bus paid up capital frm by var.
	 * 
	 * @return the smBusPaidUpCapitalFrmByVar
	 */
	public String getSmBusPaidUpCapitalFrmByVar() {
		return smBusPaidUpCapitalFrmByVar;
	}

	/**
	 * Sets the sm bus paid up capital frm by var.
	 * 
	 * @param smBusPaidUpCapitalFrmByVar
	 *            the smBusPaidUpCapitalFrmByVar to set
	 */
	public void setSmBusPaidUpCapitalFrmByVar(String smBusPaidUpCapitalFrmByVar) {
		this.smBusPaidUpCapitalFrmByVar = smBusPaidUpCapitalFrmByVar;
	}

	/**
	 * Gets the sm bus paid up capital to by var.
	 * 
	 * @return the smBusPaidUpCapitalToByVar
	 */
	public String getSmBusPaidUpCapitalToByVar() {
		return smBusPaidUpCapitalToByVar;
	}

	/**
	 * Sets the sm bus paid up capital to by var.
	 * 
	 * @param smBusPaidUpCapitalToByVar
	 *            the smBusPaidUpCapitalToByVar to set
	 */
	public void setSmBusPaidUpCapitalToByVar(String smBusPaidUpCapitalToByVar) {
		this.smBusPaidUpCapitalToByVar = smBusPaidUpCapitalToByVar;
	}

	/**
	 * Checks if is sm gen company name view.
	 * 
	 * @return the smGenCompanyNameView
	 */
	public boolean isSmGenCompanyNameView() {
		return smGenCompanyNameView;
	}

	/**
	 * Sets the sm gen company name view.
	 * 
	 * @param smGenCompanyNameView
	 *            the smGenCompanyNameView to set
	 */
	public void setSmGenCompanyNameView(boolean smGenCompanyNameView) {
		this.smGenCompanyNameView = smGenCompanyNameView;
	}

	/**
	 * Checks if is sm supp company name view.
	 * 
	 * @return the smSuppCompanyNameView
	 */
	public boolean isSmSuppCompanyNameView() {
		return smSuppCompanyNameView;
	}

	/**
	 * Sets the sm supp company name view.
	 * 
	 * @param smSuppCompanyNameView
	 *            the smSuppCompanyNameView to set
	 */
	public void setSmSuppCompanyNameView(boolean smSuppCompanyNameView) {
		this.smSuppCompanyNameView = smSuppCompanyNameView;
	}

	/**
	 * Checks if is sm mandatory gen supplier type view.
	 * 
	 * @return the smMandatoryGenSupplierTypeView
	 */
	public boolean isSmMandatoryGenSupplierTypeView() {
		return smMandatoryGenSupplierTypeView;
	}

	/**
	 * Sets the sm mandatory gen supplier type view.
	 * 
	 * @param smMandatoryGenSupplierTypeView
	 *            the smMandatoryGenSupplierTypeView to set
	 */
	public void setSmMandatoryGenSupplierTypeView(
			boolean smMandatoryGenSupplierTypeView) {
		this.smMandatoryGenSupplierTypeView = smMandatoryGenSupplierTypeView;
	}

	/**
	 * Checks if is sm gen application status view.
	 * 
	 * @return the smGenApplicationStatusView
	 */
	public boolean isSmGenApplicationStatusView() {
		return SmGenApplicationStatusView;
	}

	/**
	 * Sets the sm gen application status view.
	 * 
	 * @param smGenApplicationStatusView
	 *            the smGenApplicationStatusView to set
	 */
	public void setSmGenApplicationStatusView(boolean smGenApplicationStatusView) {
		SmGenApplicationStatusView = smGenApplicationStatusView;
	}

	/**
	 * Checks if is sm mandatory doh supplier type view.
	 * 
	 * @return the smMandatoryDOHSupplierTypeView
	 */
	public boolean isSmMandatoryDOHSupplierTypeView() {
		return smMandatoryDOHSupplierTypeView;
	}

	/**
	 * Sets the sm mandatory doh supplier type view.
	 * 
	 * @param smMandatoryDOHSupplierTypeView
	 *            the smMandatoryDOHSupplierTypeView to set
	 */
	public void setSmMandatoryDOHSupplierTypeView(
			boolean smMandatoryDOHSupplierTypeView) {
		this.smMandatoryDOHSupplierTypeView = smMandatoryDOHSupplierTypeView;
	}

	/**
	 * Checks if is sm gen identification number view.
	 * 
	 * @return the smGenIdentificationNumberView
	 */
	public boolean isSmGenIdentificationNumberView() {
		return smGenIdentificationNumberView;
	}

	/**
	 * Sets the sm gen identification number view.
	 * 
	 * @param smGenIdentificationNumberView
	 *            the smGenIdentificationNumberView to set
	 */
	public void setSmGenIdentificationNumberView(
			boolean smGenIdentificationNumberView) {
		this.smGenIdentificationNumberView = smGenIdentificationNumberView;
	}

	/**
	 * Checks if is sm ps identification number view.
	 * 
	 * @return the smPSIdentificationNumberView
	 */
	public boolean isSmPSIdentificationNumberView() {
		return smPSIdentificationNumberView;
	}

	/**
	 * Sets the sm ps identification number view.
	 * 
	 * @param smPSIdentificationNumberView
	 *            the smPSIdentificationNumberView to set
	 */
	public void setSmPSIdentificationNumberView(
			boolean smPSIdentificationNumberView) {
		this.smPSIdentificationNumberView = smPSIdentificationNumberView;
	}

	/**
	 * Checks if is sm gen district view.
	 * 
	 * @return the smGenDistrictView
	 */
	public boolean isSmGenDistrictView() {
		return smGenDistrictView;
	}

	/**
	 * Sets the sm gen district view.
	 * 
	 * @param smGenDistrictView
	 *            the smGenDistrictView to set
	 */
	public void setSmGenDistrictView(boolean smGenDistrictView) {
		this.smGenDistrictView = smGenDistrictView;
	}

	/**
	 * Checks if is sm category by supplier type view.
	 * 
	 * @return the smCategoryBySupplierTypeView
	 */
	public boolean isSmCategoryBySupplierTypeView() {
		return smCategoryBySupplierTypeView;
	}

	/**
	 * Sets the sm category by supplier type view.
	 * 
	 * @param smCategoryBySupplierTypeView
	 *            the smCategoryBySupplierTypeView to set
	 */
	public void setSmCategoryBySupplierTypeView(
			boolean smCategoryBySupplierTypeView) {
		this.smCategoryBySupplierTypeView = smCategoryBySupplierTypeView;
	}

	/**
	 * Checks if is sm daily receipt no view.
	 * 
	 * @return the smDailyReceiptNoView
	 */
	public boolean isSmDailyReceiptNoView() {
		return smDailyReceiptNoView;
	}

	/**
	 * Sets the sm daily receipt no view.
	 * 
	 * @param smDailyReceiptNoView
	 *            the smDailyReceiptNoView to set
	 */
	public void setSmDailyReceiptNoView(boolean smDailyReceiptNoView) {
		this.smDailyReceiptNoView = smDailyReceiptNoView;
	}

	/**
	 * Checks if is sm doh company name view.
	 * 
	 * @return the smDOHCompanyNameView
	 */
	public boolean isSmDOHCompanyNameView() {
		return smDOHCompanyNameView;
	}

	/**
	 * Sets the sm doh company name view.
	 * 
	 * @param smDOHCompanyNameView
	 *            the smDOHCompanyNameView to set
	 */
	public void setSmDOHCompanyNameView(boolean smDOHCompanyNameView) {
		this.smDOHCompanyNameView = smDOHCompanyNameView;
	}

	/**
	 * Checks if is sm ps supplier type view.
	 * 
	 * @return the smPSSupplierTypeView
	 */
	public boolean isSmPSSupplierTypeView() {
		return smPSSupplierTypeView;
	}

	/**
	 * Sets the sm ps supplier type view.
	 * 
	 * @param smPSSupplierTypeView
	 *            the smPSSupplierTypeView to set
	 */
	public void setSmPSSupplierTypeView(boolean smPSSupplierTypeView) {
		this.smPSSupplierTypeView = smPSSupplierTypeView;
	}

	/**
	 * Checks if is sm com supplier type view.
	 * 
	 * @return the smComSupplierTypeView
	 */
	public boolean isSmComSupplierTypeView() {
		return smComSupplierTypeView;
	}

	/**
	 * Sets the sm com supplier type view.
	 * 
	 * @param smComSupplierTypeView
	 *            the smComSupplierTypeView to set
	 */
	public void setSmComSupplierTypeView(boolean smComSupplierTypeView) {
		this.smComSupplierTypeView = smComSupplierTypeView;
	}

	/**
	 * Checks if is sm doh mandatory supplier type view.
	 * 
	 * @return the smDOHMandatorySupplierTypeView
	 */
	public boolean isSmDOHMandatorySupplierTypeView() {
		return smDOHMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm doh mandatory supplier type view.
	 * 
	 * @param smDOHMandatorySupplierTypeView
	 *            the smDOHMandatorySupplierTypeView to set
	 */
	public void setSmDOHMandatorySupplierTypeView(
			boolean smDOHMandatorySupplierTypeView) {
		this.smDOHMandatorySupplierTypeView = smDOHMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm gst supplier type view.
	 * 
	 * @return the smGSTSupplierTypeView
	 */
	public boolean isSmGSTSupplierTypeView() {
		return smGSTSupplierTypeView;
	}

	/**
	 * Sets the sm gst supplier type view.
	 * 
	 * @param smGSTSupplierTypeView
	 *            the smGSTSupplierTypeView to set
	 */
	public void setSmGSTSupplierTypeView(boolean smGSTSupplierTypeView) {
		this.smGSTSupplierTypeView = smGSTSupplierTypeView;
	}

	/**
	 * Checks if is sm mof mandatory supplier type view.
	 * 
	 * @return the smMOFMandatorySupplierTypeView
	 */
	public boolean isSmMOFMandatorySupplierTypeView() {
		return smMOFMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm mof mandatory supplier type view.
	 * 
	 * @param smMOFMandatorySupplierTypeView
	 *            the smMOFMandatorySupplierTypeView to set
	 */
	public void setSmMOFMandatorySupplierTypeView(
			boolean smMOFMandatorySupplierTypeView) {
		this.smMOFMandatorySupplierTypeView = smMOFMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm sv mandatory application no view.
	 * 
	 * @return the smSVMandatoryApplicationNoView
	 */
	public boolean isSmSVMandatoryApplicationNoView() {
		return smSVMandatoryApplicationNoView;
	}

	/**
	 * Sets the sm sv mandatory application no view.
	 * 
	 * @param smSVMandatoryApplicationNoView
	 *            the smSVMandatoryApplicationNoView to set
	 */
	public void setSmSVMandatoryApplicationNoView(
			boolean smSVMandatoryApplicationNoView) {
		this.smSVMandatoryApplicationNoView = smSVMandatoryApplicationNoView;
	}

	/**
	 * Checks if is sm sv mandatory state view.
	 * 
	 * @return the smSVMandatoryStateView
	 */
	public boolean isSmSVMandatoryStateView() {
		return smSVMandatoryStateView;
	}

	/**
	 * Sets the sm sv mandatory state view.
	 * 
	 * @param smSVMandatoryStateView
	 *            the smSVMandatoryStateView to set
	 */
	public void setSmSVMandatoryStateView(boolean smSVMandatoryStateView) {
		this.smSVMandatoryStateView = smSVMandatoryStateView;
	}

	/**
	 * Checks if is sm scp supplier type view.
	 * 
	 * @return the smSCPSupplierTypeView
	 */
	public boolean isSmSCPSupplierTypeView() {
		return smSCPSupplierTypeView;
	}

	/**
	 * Sets the sm scp supplier type view.
	 * 
	 * @param smSCPSupplierTypeView
	 *            the smSCPSupplierTypeView to set
	 */
	public void setSmSCPSupplierTypeView(boolean smSCPSupplierTypeView) {
		this.smSCPSupplierTypeView = smSCPSupplierTypeView;
	}

	/**
	 * Checks if is sm pp company name view.
	 * 
	 * @return the smPPCompanyNameView
	 */
	public boolean isSmPPCompanyNameView() {
		return smPPCompanyNameView;
	}

	/**
	 * Sets the sm pp company name view.
	 * 
	 * @param smPPCompanyNameView
	 *            the smPPCompanyNameView to set
	 */
	public void setSmPPCompanyNameView(boolean smPPCompanyNameView) {
		this.smPPCompanyNameView = smPPCompanyNameView;
	}

	/**
	 * Checks if is sm pr mandatory mof reg no view.
	 * 
	 * @return the smPRMandatoryMofRegNoView
	 */
	public boolean isSmPRMandatoryMofRegNoView() {
		return smPRMandatoryMofRegNoView;
	}

	/**
	 * Sets the sm pr mandatory mof reg no view.
	 * 
	 * @param smPRMandatoryMofRegNoView
	 *            the smPRMandatoryMofRegNoView to set
	 */
	public void setSmPRMandatoryMofRegNoView(boolean smPRMandatoryMofRegNoView) {
		this.smPRMandatoryMofRegNoView = smPRMandatoryMofRegNoView;
	}

	/**
	 * Checks if is sm pr mandatory supplier type view.
	 * 
	 * @return the smPRMandatorySupplierTypeView
	 */
	public boolean isSmPRMandatorySupplierTypeView() {
		return smPRMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm pr mandatory supplier type view.
	 * 
	 * @param smPRMandatorySupplierTypeView
	 *            the smPRMandatorySupplierTypeView to set
	 */
	public void setSmPRMandatorySupplierTypeView(
			boolean smPRMandatorySupplierTypeView) {
		this.smPRMandatorySupplierTypeView = smPRMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm th registration type view.
	 * 
	 * @return the smTHRegistrationTypeView
	 */
	public boolean isSmTHRegistrationTypeView() {
		return smTHRegistrationTypeView;
	}

	/**
	 * Sets the sm th registration type view.
	 * 
	 * @param smTHRegistrationTypeView
	 *            the smTHRegistrationTypeView to set
	 */
	public void setSmTHRegistrationTypeView(boolean smTHRegistrationTypeView) {
		this.smTHRegistrationTypeView = smTHRegistrationTypeView;
	}

	/**
	 * Checks if is sm main cat mandatory category view.
	 * 
	 * @return the smMainCatMandatoryCategoryView
	 */
	public boolean isSmMainCatMandatoryCategoryView() {
		return smMainCatMandatoryCategoryView;
	}

	/**
	 * Sets the sm main cat mandatory category view.
	 * 
	 * @param smMainCatMandatoryCategoryView
	 *            the smMainCatMandatoryCategoryView to set
	 */
	public void setSmMainCatMandatoryCategoryView(
			boolean smMainCatMandatoryCategoryView) {
		this.smMainCatMandatoryCategoryView = smMainCatMandatoryCategoryView;
	}

	/**
	 * Checks if is sm mandatory supporting document mode view.
	 * 
	 * @return the smMandatorySupportingDocumentModeView
	 */
	public boolean isSmMandatorySupportingDocumentModeView() {
		return smMandatorySupportingDocumentModeView;
	}

	/**
	 * Sets the sm mandatory supporting document mode view.
	 * 
	 * @param smMandatorySupportingDocumentModeView
	 *            the smMandatorySupportingDocumentModeView to set
	 */
	public void setSmMandatorySupportingDocumentModeView(
			boolean smMandatorySupportingDocumentModeView) {
		this.smMandatorySupportingDocumentModeView = smMandatorySupportingDocumentModeView;
	}

	/**
	 * Checks if is sm ar mof reg no view.
	 * 
	 * @return the smARMofRegNoView
	 */
	public boolean isSmARMofRegNoView() {
		return smARMofRegNoView;
	}

	/**
	 * Sets the sm ar mof reg no view.
	 * 
	 * @param smARMofRegNoView
	 *            the smARMofRegNoView to set
	 */
	public void setSmARMofRegNoView(boolean smARMofRegNoView) {
		this.smARMofRegNoView = smARMofRegNoView;
	}

	/**
	 * Checks if is sm del application no view.
	 * 
	 * @return the smDelApplicationNoView
	 */
	public boolean isSmDelApplicationNoView() {
		return smDelApplicationNoView;
	}

	/**
	 * Sets the sm del application no view.
	 * 
	 * @param smDelApplicationNoView
	 *            the smDelApplicationNoView to set
	 */
	public void setSmDelApplicationNoView(boolean smDelApplicationNoView) {
		this.smDelApplicationNoView = smDelApplicationNoView;
	}

	/**
	 * Checks if is sm th mandatory supplier type view.
	 * 
	 * @return the smTHMandatorySupplierTypeView
	 */
	public boolean isSmTHMandatorySupplierTypeView() {
		return smTHMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm th mandatory supplier type view.
	 * 
	 * @param smTHMandatorySupplierTypeView
	 *            the smTHMandatorySupplierTypeView to set
	 */
	public void setSmTHMandatorySupplierTypeView(
			boolean smTHMandatorySupplierTypeView) {
		this.smTHMandatorySupplierTypeView = smTHMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm ec mandatory supplier type view.
	 * 
	 * @return the smECMandatorySupplierTypeView
	 */
	public boolean isSmECMandatorySupplierTypeView() {
		return smECMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm ec mandatory supplier type view.
	 * 
	 * @param smECMandatorySupplierTypeView
	 *            the smECMandatorySupplierTypeView to set
	 */
	public void setSmECMandatorySupplierTypeView(
			boolean smECMandatorySupplierTypeView) {
		this.smECMandatorySupplierTypeView = smECMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm sup state view.
	 * 
	 * @return the smSupStateView
	 */
	public boolean isSmSupStateView() {
		return smSupStateView;
	}

	/**
	 * Sets the sm sup state view.
	 * 
	 * @param smSupStateView
	 *            the smSupStateView to set
	 */
	public void setSmSupStateView(boolean smSupStateView) {
		this.smSupStateView = smSupStateView;
	}

	/**
	 * Checks if is sm cd mandatory all supplier type view.
	 * 
	 * @return the smCDMandatoryAllSupplierTypeView
	 */
	public boolean isSmCDMandatoryAllSupplierTypeView() {
		return smCDMandatoryAllSupplierTypeView;
	}

	/**
	 * Sets the sm cd mandatory all supplier type view.
	 * 
	 * @param smCDMandatoryAllSupplierTypeView
	 *            the smCDMandatoryAllSupplierTypeView to set
	 */
	public void setSmCDMandatoryAllSupplierTypeView(
			boolean smCDMandatoryAllSupplierTypeView) {
		this.smCDMandatoryAllSupplierTypeView = smCDMandatoryAllSupplierTypeView;
	}

	/**
	 * Checks if is sm cd company name view.
	 * 
	 * @return the smCDCompanyNameView
	 */
	public boolean isSmCDCompanyNameView() {
		return smCDCompanyNameView;
	}

	/**
	 * Sets the sm cd company name view.
	 * 
	 * @param smCDCompanyNameView
	 *            the smCDCompanyNameView to set
	 */
	public void setSmCDCompanyNameView(boolean smCDCompanyNameView) {
		this.smCDCompanyNameView = smCDCompanyNameView;
	}

	/**
	 * Checks if is sm der sm receipt no view.
	 * 
	 * @return the smDERSmReceiptNoView
	 */
	public boolean isSmDERSmReceiptNoView() {
		return smDERSmReceiptNoView;
	}

	/**
	 * Sets the sm der sm receipt no view.
	 * 
	 * @param smDERSmReceiptNoView
	 *            the smDERSmReceiptNoView to set
	 */
	public void setSmDERSmReceiptNoView(boolean smDERSmReceiptNoView) {
		this.smDERSmReceiptNoView = smDERSmReceiptNoView;
	}

	/**
	 * Checks if is sm de mandatory all supplier type view.
	 * 
	 * @return the smDEMandatoryAllSupplierTypeView
	 */
	public boolean isSmDEMandatoryAllSupplierTypeView() {
		return smDEMandatoryAllSupplierTypeView;
	}

	/**
	 * Sets the sm de mandatory all supplier type view.
	 * 
	 * @param smDEMandatoryAllSupplierTypeView
	 *            the smDEMandatoryAllSupplierTypeView to set
	 */
	public void setSmDEMandatoryAllSupplierTypeView(
			boolean smDEMandatoryAllSupplierTypeView) {
		this.smDEMandatoryAllSupplierTypeView = smDEMandatoryAllSupplierTypeView;
	}

	/**
	 * Checks if is sm la application no view.
	 * 
	 * @return the smLAApplicationNoView
	 */
	public boolean isSmLAApplicationNoView() {
		return smLAApplicationNoView;
	}

	/**
	 * Sets the sm la application no view.
	 * 
	 * @param smLAApplicationNoView
	 *            the smLAApplicationNoView to set
	 */
	public void setSmLAApplicationNoView(boolean smLAApplicationNoView) {
		this.smLAApplicationNoView = smLAApplicationNoView;
	}

	/**
	 * Checks if is sm la registration no view.
	 * 
	 * @return the smLARegistrationNoView
	 */
	public boolean isSmLARegistrationNoView() {
		return smLARegistrationNoView;
	}

	/**
	 * Sets the sm la registration no view.
	 * 
	 * @param smLARegistrationNoView
	 *            the smLARegistrationNoView to set
	 */
	public void setSmLARegistrationNoView(boolean smLARegistrationNoView) {
		this.smLARegistrationNoView = smLARegistrationNoView;
	}

	/**
	 * Checks if is sm la mof reg no view.
	 * 
	 * @return the smLAMofRegNoView
	 */
	public boolean isSmLAMofRegNoView() {
		return smLAMofRegNoView;
	}

	/**
	 * Sets the sm la mof reg no view.
	 * 
	 * @param smLAMofRegNoView
	 *            the smLAMofRegNoView to set
	 */
	public void setSmLAMofRegNoView(boolean smLAMofRegNoView) {
		this.smLAMofRegNoView = smLAMofRegNoView;
	}

	/**
	 * Checks if is sm lc mof reg no from view.
	 * 
	 * @return the smLCMofRegNoFromView
	 */
	public boolean isSmLCMofRegNoFromView() {
		return smLCMofRegNoFromView;
	}

	/**
	 * Sets the sm lc mof reg no from view.
	 * 
	 * @param smLCMofRegNoFromView
	 *            the smLCMofRegNoFromView to set
	 */
	public void setSmLCMofRegNoFromView(boolean smLCMofRegNoFromView) {
		this.smLCMofRegNoFromView = smLCMofRegNoFromView;
	}

	/**
	 * Checks if is sm lcmof registered date view.
	 * 
	 * @return the smLCMOFRegisteredDateView
	 */
	public boolean isSmLCMOFRegisteredDateView() {
		return smLCMOFRegisteredDateView;
	}

	/**
	 * Sets the sm lcmof registered date view.
	 * 
	 * @param smLCMOFRegisteredDateView
	 *            the smLCMOFRegisteredDateView to set
	 */
	public void setSmLCMOFRegisteredDateView(boolean smLCMOFRegisteredDateView) {
		this.smLCMOFRegisteredDateView = smLCMOFRegisteredDateView;
	}

	/**
	 * Checks if is sm lc bus company registration status view.
	 * 
	 * @return the smLCBusCompanyRegistrationStatusView
	 */
	public boolean isSmLCBusCompanyRegistrationStatusView() {
		return smLCBusCompanyRegistrationStatusView;
	}

	/**
	 * Sets the sm lc bus company registration status view.
	 * 
	 * @param smLCBusCompanyRegistrationStatusView
	 *            the smLCBusCompanyRegistrationStatusView to set
	 */
	public void setSmLCBusCompanyRegistrationStatusView(
			boolean smLCBusCompanyRegistrationStatusView) {
		this.smLCBusCompanyRegistrationStatusView = smLCBusCompanyRegistrationStatusView;
	}

	/**
	 * Checks if is sm lc mandatory supplier type view.
	 * 
	 * @return the smLCMandatorySupplierTypeView
	 */
	public boolean isSmLCMandatorySupplierTypeView() {
		return smLCMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm lc mandatory supplier type view.
	 * 
	 * @param smLCMandatorySupplierTypeView
	 *            the smLCMandatorySupplierTypeView to set
	 */
	public void setSmLCMandatorySupplierTypeView(
			boolean smLCMandatorySupplierTypeView) {
		this.smLCMandatorySupplierTypeView = smLCMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm da registration type view.
	 * 
	 * @return the smDARegistrationTypeView
	 */
	public boolean isSmDARegistrationTypeView() {
		return smDARegistrationTypeView;
	}

	/**
	 * Sets the sm da registration type view.
	 * 
	 * @param smDARegistrationTypeView
	 *            the smDARegistrationTypeView to set
	 */
	public void setSmDARegistrationTypeView(boolean smDARegistrationTypeView) {
		this.smDARegistrationTypeView = smDARegistrationTypeView;
	}

	/**
	 * Checks if is sm dr mof reg no two view.
	 * 
	 * @return the smDRMofRegNoTwoView
	 */
	public boolean isSmDRMofRegNoTwoView() {
		return smDRMofRegNoTwoView;
	}

	/**
	 * Sets the sm dr mof reg no two view.
	 * 
	 * @param smDRMofRegNoTwoView
	 *            the smDRMofRegNoTwoView to set
	 */
	public void setSmDRMofRegNoTwoView(boolean smDRMofRegNoTwoView) {
		this.smDRMofRegNoTwoView = smDRMofRegNoTwoView;
	}

	/**
	 * Checks if is sm do company name view.
	 * 
	 * @return the smDOCompanyNameView
	 */
	public boolean isSmDOCompanyNameView() {
		return smDOCompanyNameView;
	}

	/**
	 * Sets the sm do company name view.
	 * 
	 * @param smDOCompanyNameView
	 *            the smDOCompanyNameView to set
	 */
	public void setSmDOCompanyNameView(boolean smDOCompanyNameView) {
		this.smDOCompanyNameView = smDOCompanyNameView;
	}

	/**
	 * Checks if is sm gst registration type view.
	 * 
	 * @return the smGSTRegistrationTypeView
	 */
	public boolean isSmGSTRegistrationTypeView() {
		return smGSTRegistrationTypeView;
	}

	/**
	 * Sets the sm gst registration type view.
	 * 
	 * @param smGSTRegistrationTypeView
	 *            the smGSTRegistrationTypeView to set
	 */
	public void setSmGSTRegistrationTypeView(boolean smGSTRegistrationTypeView) {
		this.smGSTRegistrationTypeView = smGSTRegistrationTypeView;
	}

	/**
	 * Checks if is sm gs registration no view.
	 * 
	 * @return the smGSRegistrationNoView
	 */
	public boolean isSmGSRegistrationNoView() {
		return smGSRegistrationNoView;
	}

	/**
	 * Sets the sm gs registration no view.
	 * 
	 * @param smGSRegistrationNoView
	 *            the smGSRegistrationNoView to set
	 */
	public void setSmGSRegistrationNoView(boolean smGSRegistrationNoView) {
		this.smGSRegistrationNoView = smGSRegistrationNoView;
	}

	/**
	 * Checks if is sm can mandatory supplier type view.
	 * 
	 * @return the smCanMandatorySupplierTypeView
	 */
	public boolean isSmCanMandatorySupplierTypeView() {
		return smCanMandatorySupplierTypeView;
	}

	/**
	 * Sets the sm can mandatory supplier type view.
	 * 
	 * @param smCanMandatorySupplierTypeView
	 *            the smCanMandatorySupplierTypeView to set
	 */
	public void setSmCanMandatorySupplierTypeView(
			boolean smCanMandatorySupplierTypeView) {
		this.smCanMandatorySupplierTypeView = smCanMandatorySupplierTypeView;
	}

	/**
	 * Checks if is sm can application date view.
	 * 
	 * @return the smCanApplicationDateView
	 */
	public boolean isSmCanApplicationDateView() {
		return smCanApplicationDateView;
	}

	/**
	 * Sets the sm can application date view.
	 * 
	 * @param smCanApplicationDateView
	 *            the smCanApplicationDateView to set
	 */
	public void setSmCanApplicationDateView(boolean smCanApplicationDateView) {
		this.smCanApplicationDateView = smCanApplicationDateView;
	}

	/**
	 * Checks if is sm of application no view.
	 * 
	 * @return the smOFApplicationNoView
	 */
	public boolean isSmOFApplicationNoView() {
		return smOFApplicationNoView;
	}

	/**
	 * Sets the sm of application no view.
	 * 
	 * @param smOFApplicationNoView
	 *            the smOFApplicationNoView to set
	 */
	public void setSmOFApplicationNoView(boolean smOFApplicationNoView) {
		this.smOFApplicationNoView = smOFApplicationNoView;
	}

	/**
	 * Checks if is sm lp company name view.
	 * 
	 * @return the smLPCompanyNameView
	 */
	public boolean isSmLPCompanyNameView() {
		return smLPCompanyNameView;
	}

	/**
	 * Sets the sm lp company name view.
	 * 
	 * @param smLPCompanyNameView
	 *            the smLPCompanyNameView to set
	 */
	public void setSmLPCompanyNameView(boolean smLPCompanyNameView) {
		this.smLPCompanyNameView = smLPCompanyNameView;
	}

	/**
	 * Checks if is sm sv mandatory district view.
	 * 
	 * @return the smSVMandatoryDistrictView
	 */
	public boolean isSmSVMandatoryDistrictView() {
		return smSVMandatoryDistrictView;
	}

	/**
	 * Sets the sm sv mandatory district view.
	 * 
	 * @param smSVMandatoryDistrictView
	 *            the smSVMandatoryDistrictView to set
	 */
	public void setSmSVMandatoryDistrictView(boolean smSVMandatoryDistrictView) {
		this.smSVMandatoryDistrictView = smSVMandatoryDistrictView;
	}

	/**
	 * Checks if is sm sa application no view.
	 * 
	 * @return the smSAApplicationNoView
	 */
	public boolean isSmSAApplicationNoView() {
		return smSAApplicationNoView;
	}

	/**
	 * Sets the sm sa application no view.
	 * 
	 * @param smSAApplicationNoView
	 *            the smSAApplicationNoView to set
	 */
	public void setSmSAApplicationNoView(boolean smSAApplicationNoView) {
		this.smSAApplicationNoView = smSAApplicationNoView;
	}

	/**
	 * Checks if is sm qr all supplier type view.
	 * 
	 * @return the smQRAllSupplierTypeView
	 */
	public boolean isSmQRAllSupplierTypeView() {
		return smQRAllSupplierTypeView;
	}

	/**
	 * Sets the sm qr all supplier type view.
	 * 
	 * @param smQRAllSupplierTypeView
	 *            the smQRAllSupplierTypeView to set
	 */
	public void setSmQRAllSupplierTypeView(boolean smQRAllSupplierTypeView) {
		this.smQRAllSupplierTypeView = smQRAllSupplierTypeView;
	}

	/**
	 * Checks if is sm sl bus company type view.
	 * 
	 * @return the smSLBusCompanyTypeView
	 */
	public boolean isSmSLBusCompanyTypeView() {
		return smSLBusCompanyTypeView;
	}

	/**
	 * Sets the sm sl bus company type view.
	 * 
	 * @param smSLBusCompanyTypeView
	 *            the smSLBusCompanyTypeView to set
	 */
	public void setSmSLBusCompanyTypeView(boolean smSLBusCompanyTypeView) {
		this.smSLBusCompanyTypeView = smSLBusCompanyTypeView;
	}

	/**
	 * Checks if is sm threshold val year from view.
	 * 
	 * @return the smThresholdValYearFromView
	 */
	public boolean isSmThresholdValYearFromView() {
		return smThresholdValYearFromView;
	}

	/**
	 * Sets the sm threshold val year from view.
	 * 
	 * @param smThresholdValYearFromView
	 *            the smThresholdValYearFromView to set
	 */
	public void setSmThresholdValYearFromView(boolean smThresholdValYearFromView) {
		this.smThresholdValYearFromView = smThresholdValYearFromView;
	}

	/**
	 * Checks if is sm tn mandatory report type view.
	 * 
	 * @return the smTNMandatoryReportTypeView
	 */
	public boolean isSmTNMandatoryReportTypeView() {
		return smTNMandatoryReportTypeView;
	}

	/**
	 * Sets the sm tn mandatory report type view.
	 * 
	 * @param smTNMandatoryReportTypeView
	 *            the smTNMandatoryReportTypeView to set
	 */
	public void setSmTNMandatoryReportTypeView(
			boolean smTNMandatoryReportTypeView) {
		this.smTNMandatoryReportTypeView = smTNMandatoryReportTypeView;
	}

	/**
	 * Checks if is sm tn application date view.
	 * 
	 * @return the smTNApplicationDateView
	 */
	public boolean isSmTNApplicationDateView() {
		return smTNApplicationDateView;
	}

	/**
	 * Sets the sm tn application date view.
	 * 
	 * @param smTNApplicationDateView
	 *            the smTNApplicationDateView to set
	 */
	public void setSmTNApplicationDateView(boolean smTNApplicationDateView) {
		this.smTNApplicationDateView = smTNApplicationDateView;
	}

	/**
	 * Checks if is sm cp supplier name view.
	 * 
	 * @return the smCPSupplierNameView
	 */
	public boolean isSmCPSupplierNameView() {
		return smCPSupplierNameView;
	}

	/**
	 * Sets the sm cp supplier name view.
	 * 
	 * @param smCPSupplierNameView
	 *            the smCPSupplierNameView to set
	 */
	public void setSmCPSupplierNameView(boolean smCPSupplierNameView) {
		this.smCPSupplierNameView = smCPSupplierNameView;
	}

	/**
	 * Checks if is pm mandatory zone view.
	 * 
	 * @return the pmMandatoryZoneView
	 */
	public boolean isPmMandatoryZoneView() {
		return pmMandatoryZoneView;
	}

	/**
	 * Sets the pm mandatory zone view.
	 * 
	 * @param pmMandatoryZoneView
	 *            the pmMandatoryZoneView to set
	 */
	public void setPmMandatoryZoneView(boolean pmMandatoryZoneView) {
		this.pmMandatoryZoneView = pmMandatoryZoneView;
	}

	/**
	 * Checks if is pm profile status vie view.
	 * 
	 * @return the pmProfileStatusVieView
	 */
	public boolean isPmProfileStatusView() {
		return pmProfileStatusView;
	}

	/**
	 * Sets the pm profile status vie view.
	 * 
	 * @param pmProfileStatusView
	 *            the new pm profile status view
	 */
	public void setPmProfileStatusView(boolean pmProfileStatusView) {
		this.pmProfileStatusView = pmProfileStatusView;
	}

	/**
	 * Gets the pm zone var.
	 * 
	 * @return the pmZoneVar
	 */
	public String getPmZoneVar() {
		return pmZoneVar;
	}

	/**
	 * Sets the pm zone var.
	 * 
	 * @param pmZoneVar
	 *            the pmZoneVar to set
	 */
	public void setPmZoneVar(String pmZoneVar) {
		this.pmZoneVar = pmZoneVar;
	}

	/**
	 * Gets the pm profile status var.
	 * 
	 * @return the pmProfileStatusVar
	 */
	public String getPmProfileStatusVar() {
		return pmProfileStatusVar;
	}

	/**
	 * Sets the pm profile status var.
	 * 
	 * @param pmProfileStatusVar
	 *            the pmProfileStatusVar to set
	 */
	public void setPmProfileStatusVar(String pmProfileStatusVar) {
		this.pmProfileStatusVar = pmProfileStatusVar;
	}

	/**
	 * Checks if is sm application type five view.
	 * 
	 * @return the smApplicationTypeFiveView
	 */
	public boolean isSmApplicationTypeFiveView() {
		return smApplicationTypeFiveView;
	}

	/**
	 * Sets the sm application type five view.
	 * 
	 * @param smApplicationTypeFiveView
	 *            the smApplicationTypeFiveView to set
	 */
	public void setSmApplicationTypeFiveView(boolean smApplicationTypeFiveView) {
		this.smApplicationTypeFiveView = smApplicationTypeFiveView;
	}

	/**
	 * Checks if is sm application type six view.
	 * 
	 * @return the smApplicationTypeSixView
	 */
	public boolean isSmApplicationTypeSixView() {
		return smApplicationTypeSixView;
	}

	/**
	 * Sets the sm application type six view.
	 * 
	 * @param smApplicationTypeSixView
	 *            the smApplicationTypeSixView to set
	 */
	public void setSmApplicationTypeSixView(boolean smApplicationTypeSixView) {
		this.smApplicationTypeSixView = smApplicationTypeSixView;
	}

	/**
	 * Checks if is pm non mandatory sup date view.
	 * 
	 * @return the pmNonMandatorySupDateView
	 */
	public boolean isPmNonMandatorySupDateView() {
		return pmNonMandatorySupDateView;
	}

	/**
	 * Sets the pm non mandatory sup date view.
	 * 
	 * @param pmNonMandatorySupDateView
	 *            the pmNonMandatorySupDateView to set
	 */
	public void setPmNonMandatorySupDateView(boolean pmNonMandatorySupDateView) {
		this.pmNonMandatorySupDateView = pmNonMandatorySupDateView;
	}

	/**
	 * Checks if is pm mandatory zone view bpk.
	 * 
	 * @return the pmMandatoryZoneViewBPK
	 */
	public boolean isPmMandatoryZoneViewBPK() {
		return pmMandatoryZoneViewBPK;
	}

	/**
	 * Sets the pm mandatory zone view bpk.
	 * 
	 * @param pmMandatoryZoneViewBPK
	 *            the pmMandatoryZoneViewBPK to set
	 */
	public void setPmMandatoryZoneViewBPK(boolean pmMandatoryZoneViewBPK) {
		this.pmMandatoryZoneViewBPK = pmMandatoryZoneViewBPK;
	}

	/**
	 * Checks if is sm application approved view.
	 * 
	 * @return the smApplicationApprovedView
	 */
	public boolean isSmApplicationApprovedView() {
		return smApplicationApprovedView;
	}

	/**
	 * Sets the sm application approved view.
	 * 
	 * @param smApplicationApprovedView
	 *            the smApplicationApprovedView to set
	 */
	public void setSmApplicationApprovedView(boolean smApplicationApprovedView) {
		this.smApplicationApprovedView = smApplicationApprovedView;
	}

	/**
	 * Checks if is sm application rejected view.
	 * 
	 * @return the smApplicationRejectedView
	 */
	public boolean isSmApplicationRejectedView() {
		return smApplicationRejectedView;
	}

	/**
	 * Sets the sm application rejected view.
	 * 
	 * @param smApplicationRejectedView
	 *            the smApplicationRejectedView to set
	 */
	public void setSmApplicationRejectedView(boolean smApplicationRejectedView) {
		this.smApplicationRejectedView = smApplicationRejectedView;
	}

	/**
	 * Checks if is sm application verification performance view.
	 * 
	 * @return the smApplicationVerificationPerformanceView
	 */
	public boolean isSmApplicationVerificationPerformanceView() {
		return smApplicationVerificationPerformanceView;
	}

	/**
	 * Sets the sm application verification performance view.
	 * 
	 * @param smApplicationVerificationPerformanceView
	 *            the smApplicationVerificationPerformanceView to set
	 */
	public void setSmApplicationVerificationPerformanceView(
			boolean smApplicationVerificationPerformanceView) {
		this.smApplicationVerificationPerformanceView = smApplicationVerificationPerformanceView;
	}

	/**
	 * Checks if is sm application verification view.
	 * 
	 * @return the smApplicationVerificationView
	 */
	public boolean isSmApplicationVerificationView() {
		return smApplicationVerificationView;
	}

	/**
	 * Sets the sm application verification view.
	 * 
	 * @param smApplicationVerificationView
	 *            the smApplicationVerificationView to set
	 */
	public void setSmApplicationVerificationView(
			boolean smApplicationVerificationView) {
		this.smApplicationVerificationView = smApplicationVerificationView;
	}

	/**
	 * Checks if is sm agency statistical status view.
	 * 
	 * @return the smAgencyStatisticalStatusView
	 */
	public boolean isSmAgencyStatisticalStatusView() {
		return smAgencyStatisticalStatusView;
	}

	/**
	 * Sets the sm agency statistical status view.
	 * 
	 * @param smAgencyStatisticalStatusView
	 *            the smAgencyStatisticalStatusView to set
	 */
	public void setSmAgencyStatisticalStatusView(
			boolean smAgencyStatisticalStatusView) {
		this.smAgencyStatisticalStatusView = smAgencyStatisticalStatusView;
	}

	/**
	 * Checks if is sm category statistic view.
	 * 
	 * @return the smCategoryStatisticView
	 */
	public boolean isSmCategoryStatisticView() {
		return smCategoryStatisticView;
	}

	/**
	 * Sets the sm category statistic view.
	 * 
	 * @param smCategoryStatisticView
	 *            the smCategoryStatisticView to set
	 */
	public void setSmCategoryStatisticView(boolean smCategoryStatisticView) {
		this.smCategoryStatisticView = smCategoryStatisticView;
	}

	/**
	 * Checks if is sm category statistic by state view.
	 * 
	 * @return the smCategoryStatisticByStateView
	 */
	public boolean isSmCategoryStatisticByStateView() {
		return smCategoryStatisticByStateView;
	}

	/**
	 * Sets the sm category statistic by state view.
	 * 
	 * @param smCategoryStatisticByStateView
	 *            the smCategoryStatisticByStateView to set
	 */
	public void setSmCategoryStatisticByStateView(
			boolean smCategoryStatisticByStateView) {
		this.smCategoryStatisticByStateView = smCategoryStatisticByStateView;
	}

	/**
	 * Checks if is sm category statistic by year view.
	 * 
	 * @return the smCategoryStatisticByYearView
	 */
	public boolean isSmCategoryStatisticByYearView() {
		return smCategoryStatisticByYearView;
	}

	/**
	 * Sets the sm category statistic by year view.
	 * 
	 * @param smCategoryStatisticByYearView
	 *            the smCategoryStatisticByYearView to set
	 */
	public void setSmCategoryStatisticByYearView(
			boolean smCategoryStatisticByYearView) {
		this.smCategoryStatisticByYearView = smCategoryStatisticByYearView;
	}

	/**
	 * Checks if is sm company address list view.
	 * 
	 * @return the smCompanyAddressListView
	 */
	public boolean isSmCompanyAddressListView() {
		return smCompanyAddressListView;
	}

	/**
	 * Sets the sm company address list view.
	 * 
	 * @param smCompanyAddressListView
	 *            the smCompanyAddressListView to set
	 */
	public void setSmCompanyAddressListView(boolean smCompanyAddressListView) {
		this.smCompanyAddressListView = smCompanyAddressListView;
	}

	/**
	 * Checks if is sm daily receipts es view.
	 * 
	 * @return the smDailyReceiptsESView
	 */
	public boolean isSmDailyReceiptsESView() {
		return smDailyReceiptsESView;
	}

	/**
	 * Sets the sm daily receipts es view.
	 * 
	 * @param smDailyReceiptsESView
	 *            the smDailyReceiptsESView to set
	 */
	public void setSmDailyReceiptsESView(boolean smDailyReceiptsESView) {
		this.smDailyReceiptsESView = smDailyReceiptsESView;
	}

	/**
	 * Checks if is sm delete pending application view.
	 * 
	 * @return the smDeletePendingApplicationView
	 */
	public boolean isSmDeletePendingApplicationView() {
		return smDeletePendingApplicationView;
	}

	/**
	 * Sets the sm delete pending application view.
	 * 
	 * @param smDeletePendingApplicationView
	 *            the smDeletePendingApplicationView to set
	 */
	public void setSmDeletePendingApplicationView(
			boolean smDeletePendingApplicationView) {
		this.smDeletePendingApplicationView = smDeletePendingApplicationView;
	}

	/**
	 * Checks if is sm laporan terimaan harian view.
	 * 
	 * @return the smLaporanTerimaanHarianView
	 */
	public boolean isSmLaporanTerimaanHarianView() {
		return smLaporanTerimaanHarianView;
	}

	/**
	 * Sets the sm laporan terimaan harian view.
	 * 
	 * @param smLaporanTerimaanHarianView
	 *            the smLaporanTerimaanHarianView to set
	 */
	public void setSmLaporanTerimaanHarianView(boolean smLaporanTerimaanHarianView) {
		this.smLaporanTerimaanHarianView = smLaporanTerimaanHarianView;
	}

	/**
	 * Checks if is sm list of application view.
	 * 
	 * @return the smListOfApplicationView
	 */
	public boolean isSmListOfApplicationView() {
		return smListOfApplicationView;
	}

	/**
	 * Sets the sm list of application view.
	 * 
	 * @param smListOfApplicationView
	 *            the smListOfApplicationView to set
	 */
	public void setSmListOfApplicationView(boolean smListOfApplicationView) {
		this.smListOfApplicationView = smListOfApplicationView;
	}

	/**
	 * Checks if is sm list of application pending for sv view.
	 * 
	 * @return the smListOfApplicationPendingForSVView
	 */
	public boolean isSmListOfApplicationPendingForSVView() {
		return smListOfApplicationPendingForSVView;
	}

	/**
	 * Sets the sm list of application pending for sv view.
	 * 
	 * @param smListOfApplicationPendingForSVView
	 *            the smListOfApplicationPendingForSVView to set
	 */
	public void setSmListOfApplicationPendingForSVView(
			boolean smListOfApplicationPendingForSVView) {
		this.smListOfApplicationPendingForSVView = smListOfApplicationPendingForSVView;
	}

	/**
	 * Checks if is sm list of application status view.
	 * 
	 * @return the smListOfApplicationStatusView
	 */
	public boolean isSmListOfApplicationStatusView() {
		return smListOfApplicationStatusView;
	}

	/**
	 * Sets the sm list of application status view.
	 * 
	 * @param smListOfApplicationStatusView
	 *            the smListOfApplicationStatusView to set
	 */
	public void setSmListOfApplicationStatusView(
			boolean smListOfApplicationStatusView) {
		this.smListOfApplicationStatusView = smListOfApplicationStatusView;
	}

	/**
	 * Checks if is sm list of companies sort alphabet view.
	 * 
	 * @return the smListOfCompaniesSortAlphabetView
	 */
	public boolean isSmListOfCompaniesSortAlphabetView() {
		return smListOfCompaniesSortAlphabetView;
	}

	/**
	 * Sets the sm list of companies sort alphabet view.
	 * 
	 * @param smListOfCompaniesSortAlphabetView
	 *            the smListOfCompaniesSortAlphabetView to set
	 */
	public void setSmListOfCompaniesSortAlphabetView(
			boolean smListOfCompaniesSortAlphabetView) {
		this.smListOfCompaniesSortAlphabetView = smListOfCompaniesSortAlphabetView;
	}

	/**
	 * Checks if is sm list of companies srsc view.
	 * 
	 * @return the smListOfCompaniesSRSCView
	 */
	public boolean isSmListOfCompaniesSRSCView() {
		return smListOfCompaniesSRSCView;
	}

	/**
	 * Sets the sm list of companies srsc view.
	 * 
	 * @param smListOfCompaniesSRSCView
	 *            the smListOfCompaniesSRSCView to set
	 */
	public void setSmListOfCompaniesSRSCView(boolean smListOfCompaniesSRSCView) {
		this.smListOfCompaniesSRSCView = smListOfCompaniesSRSCView;
	}

	/**
	 * Checks if is sm list of companies with dohe view.
	 * 
	 * @return the smListOfCompaniesWithDOHEView
	 */
	public boolean isSmListOfCompaniesWithDOHEView() {
		return smListOfCompaniesWithDOHEView;
	}

	/**
	 * Sets the sm list of companies with dohe view.
	 * 
	 * @param smListOfCompaniesWithDOHEView
	 *            the smListOfCompaniesWithDOHEView to set
	 */
	public void setSmListOfCompaniesWithDOHEView(
			boolean smListOfCompaniesWithDOHEView) {
		this.smListOfCompaniesWithDOHEView = smListOfCompaniesWithDOHEView;
	}

	/**
	 * Checks if is sm list of daily application basic and mof view.
	 * 
	 * @return the smListOfDailyApplicationBasicAndMOFView
	 */
	public boolean isSmListOfDailyApplicationBasicAndMOFView() {
		return smListOfDailyApplicationBasicAndMOFView;
	}

	/**
	 * Sets the sm list of daily application basic and mof view.
	 * 
	 * @param smListOfDailyApplicationBasicAndMOFView
	 *            the smListOfDailyApplicationBasicAndMOFView to set
	 */
	public void setSmListOfDailyApplicationBasicAndMOFView(
			boolean smListOfDailyApplicationBasicAndMOFView) {
		this.smListOfDailyApplicationBasicAndMOFView = smListOfDailyApplicationBasicAndMOFView;
	}

	/**
	 * Checks if is sm list of daily registered application view.
	 * 
	 * @return the smListOfDailyRegisteredApplicationView
	 */
	public boolean isSmListOfDailyRegisteredApplicationView() {
		return smListOfDailyRegisteredApplicationView;
	}

	/**
	 * Sets the sm list of daily registered application view.
	 * 
	 * @param smListOfDailyRegisteredApplicationView
	 *            the smListOfDailyRegisteredApplicationView to set
	 */
	public void setSmListOfDailyRegisteredApplicationView(
			boolean smListOfDailyRegisteredApplicationView) {
		this.smListOfDailyRegisteredApplicationView = smListOfDailyRegisteredApplicationView;
	}

	/**
	 * Checks if is sm list of dohe view.
	 * 
	 * @return the smListOfDOHEView
	 */
	public boolean isSmListOfDOHEView() {
		return smListOfDOHEView;
	}

	/**
	 * Sets the sm list of dohe view.
	 * 
	 * @param smListOfDOHEView
	 *            the smListOfDOHEView to set
	 */
	public void setSmListOfDOHEView(boolean smListOfDOHEView) {
		this.smListOfDOHEView = smListOfDOHEView;
	}

	/**
	 * Checks if is sm list of gst reg supplier view.
	 * 
	 * @return the smListOfGSTRegSupplierView
	 */
	public boolean isSmListOfGSTRegSupplierView() {
		return smListOfGSTRegSupplierView;
	}

	/**
	 * Sets the sm list of gst reg supplier view.
	 * 
	 * @param smListOfGSTRegSupplierView
	 *            the smListOfGSTRegSupplierView to set
	 */
	public void setSmListOfGSTRegSupplierView(boolean smListOfGSTRegSupplierView) {
		this.smListOfGSTRegSupplierView = smListOfGSTRegSupplierView;
	}

	/**
	 * Checks if is sm list delete mof account view.
	 * 
	 * @return the smListDeleteMOFAccountView
	 */
	public boolean isSmListDeleteMOFAccountView() {
		return smListDeleteMOFAccountView;
	}

	/**
	 * Sets the sm list delete mof account view.
	 * 
	 * @param smListDeleteMOFAccountView
	 *            the smListDeleteMOFAccountView to set
	 */
	public void setSmListDeleteMOFAccountView(boolean smListDeleteMOFAccountView) {
		this.smListDeleteMOFAccountView = smListDeleteMOFAccountView;
	}

	/**
	 * Checks if is sm list of offline sdps view.
	 * 
	 * @return the smListOfOfflineSDPSView
	 */
	public boolean isSmListOfOfflineSDPSView() {
		return smListOfOfflineSDPSView;
	}

	/**
	 * Sets the sm list of offline sdps view.
	 * 
	 * @param smListOfOfflineSDPSView
	 *            the smListOfOfflineSDPSView to set
	 */
	public void setSmListOfOfflineSDPSView(boolean smListOfOfflineSDPSView) {
		this.smListOfOfflineSDPSView = smListOfOfflineSDPSView;
	}

	/**
	 * Checks if is sm list ps more view.
	 * 
	 * @return the smListPSMoreView
	 */
	public boolean isSmListPSMoreView() {
		return smListPSMoreView;
	}

	/**
	 * Sets the sm list ps more view.
	 * 
	 * @param smListPSMoreView
	 *            the smListPSMoreView to set
	 */
	public void setSmListPSMoreView(boolean smListPSMoreView) {
		this.smListPSMoreView = smListPSMoreView;
	}

	/**
	 * Checks if is sm list of expired company view.
	 * 
	 * @return the smListOfExpiredCompanyView
	 */
	public boolean isSmListOfExpiredCompanyView() {
		return smListOfExpiredCompanyView;
	}

	/**
	 * Sets the sm list of expired company view.
	 * 
	 * @param smListOfExpiredCompanyView
	 *            the smListOfExpiredCompanyView to set
	 */
	public void setSmListOfExpiredCompanyView(boolean smListOfExpiredCompanyView) {
		this.smListOfExpiredCompanyView = smListOfExpiredCompanyView;
	}

	/**
	 * Checks if is sm list of svosvl view.
	 * 
	 * @return the smListOfSVOSVLView
	 */
	public boolean isSmListOfSVOSVLView() {
		return smListOfSVOSVLView;
	}

	/**
	 * Sets the sm list of svosvl view.
	 * 
	 * @param smListOfSVOSVLView
	 *            the smListOfSVOSVLView to set
	 */
	public void setSmListOfSVOSVLView(boolean smListOfSVOSVLView) {
		this.smListOfSVOSVLView = smListOfSVOSVLView;
	}

	/**
	 * Checks if is sm list of soa view.
	 * 
	 * @return the smListOfSOAView
	 */
	public boolean isSmListOfSOAView() {
		return smListOfSOAView;
	}

	/**
	 * Sets the sm list of soa view.
	 * 
	 * @param smListOfSOAView
	 *            the smListOfSOAView to set
	 */
	public void setSmListOfSOAView(boolean smListOfSOAView) {
		this.smListOfSOAView = smListOfSOAView;
	}

	/**
	 * Checks if is sm list of soppsv view.
	 * 
	 * @return the smListOfSOPPSVView
	 */
	public boolean isSmListOfSOPPSVView() {
		return smListOfSOPPSVView;
	}

	/**
	 * Sets the sm list of soppsv view.
	 * 
	 * @param smListOfSOPPSVView
	 *            the smListOfSOPPSVView to set
	 */
	public void setSmListOfSOPPSVView(boolean smListOfSOPPSVView) {
		this.smListOfSOPPSVView = smListOfSOPPSVView;
	}

	/**
	 * Checks if is sm list of socpba view.
	 * 
	 * @return the smListOfSOCPBAView
	 */
	public boolean isSmListOfSOCPBAView() {
		return smListOfSOCPBAView;
	}

	/**
	 * Sets the sm list of socpba view.
	 * 
	 * @param smListOfSOCPBAView
	 *            the smListOfSOCPBAView to set
	 */
	public void setSmListOfSOCPBAView(boolean smListOfSOCPBAView) {
		this.smListOfSOCPBAView = smListOfSOCPBAView;
	}

	/**
	 * Checks if is sm list of users scr view.
	 * 
	 * @return the smListOfUsersSCRView
	 */
	public boolean isSmListOfUsersSCRView() {
		return smListOfUsersSCRView;
	}

	/**
	 * Sets the sm list of users scr view.
	 * 
	 * @param smListOfUsersSCRView
	 *            the smListOfUsersSCRView to set
	 */
	public void setSmListOfUsersSCRView(boolean smListOfUsersSCRView) {
		this.smListOfUsersSCRView = smListOfUsersSCRView;
	}

	/**
	 * Checks if is sm list of pprf view.
	 * 
	 * @return the smListOfPPRFView
	 */
	public boolean isSmListOfPPRFView() {
		return smListOfPPRFView;
	}

	/**
	 * Sets the sm list of pprf view.
	 * 
	 * @param smListOfPPRFView
	 *            the smListOfPPRFView to set
	 */
	public void setSmListOfPPRFView(boolean smListOfPPRFView) {
		this.smListOfPPRFView = smListOfPPRFView;
	}

	/**
	 * Checks if is sm monthly report view.
	 * 
	 * @return the smMonthlyReportView
	 */
	public boolean isSmMonthlyReportView() {
		return smMonthlyReportView;
	}

	/**
	 * Sets the sm monthly report view.
	 * 
	 * @param smMonthlyReportView
	 *            the smMonthlyReportView to set
	 */
	public void setSmMonthlyReportView(boolean smMonthlyReportView) {
		this.smMonthlyReportView = smMonthlyReportView;
	}

	/**
	 * Checks if is sm profile reg company view.
	 * 
	 * @return the smProfileRegCompanyView
	 */
	public boolean isSmProfileRegCompanyView() {
		return smProfileRegCompanyView;
	}

	/**
	 * Sets the sm profile reg company view.
	 * 
	 * @param smProfileRegCompanyView
	 *            the smProfileRegCompanyView to set
	 */
	public void setSmProfileRegCompanyView(boolean smProfileRegCompanyView) {
		this.smProfileRegCompanyView = smProfileRegCompanyView;
	}

	/**
	 * Checks if is sm qrs view.
	 * 
	 * @return the smQRSView
	 */
	public boolean isSmQRSView() {
		return smQRSView;
	}

	/**
	 * Sets the sm qrs view.
	 * 
	 * @param smQRSView
	 *            the smQRSView to set
	 */
	public void setSmQRSView(boolean smQRSView) {
		this.smQRSView = smQRSView;
	}

	/**
	 * Checks if is sm statistic list cbc view.
	 * 
	 * @return the smStatisticListCBCView
	 */
	public boolean isSmStatisticListCBCView() {
		return smStatisticListCBCView;
	}

	/**
	 * Sets the sm statistic list cbc view.
	 * 
	 * @param smStatisticListCBCView
	 *            the smStatisticListCBCView to set
	 */
	public void setSmStatisticListCBCView(boolean smStatisticListCBCView) {
		this.smStatisticListCBCView = smStatisticListCBCView;
	}

	/**
	 * Checks if is sm supplier performance view.
	 * 
	 * @return the smSupplierPerformanceView
	 */
	public boolean isSmSupplierPerformanceView() {
		return smSupplierPerformanceView;
	}

	/**
	 * Sets the sm supplier performance view.
	 * 
	 * @param smSupplierPerformanceView
	 *            the smSupplierPerformanceView to set
	 */
	public void setSmSupplierPerformanceView(boolean smSupplierPerformanceView) {
		this.smSupplierPerformanceView = smSupplierPerformanceView;
	}

	/**
	 * Checks if is sm supplier statistic view.
	 * 
	 * @return the smSupplierStatisticView
	 */
	public boolean isSmSupplierStatisticView() {
		return smSupplierStatisticView;
	}

	/**
	 * Sets the sm supplier statistic view.
	 * 
	 * @param smSupplierStatisticView
	 *            the smSupplierStatisticView to set
	 */
	public void setSmSupplierStatisticView(boolean smSupplierStatisticView) {
		this.smSupplierStatisticView = smSupplierStatisticView;
	}

	/**
	 * Checks if is sm supplier statistic by category view.
	 * 
	 * @return the smSupplierStatisticByCategoryView
	 */
	public boolean isSmSupplierStatisticByCategoryView() {
		return smSupplierStatisticByCategoryView;
	}

	/**
	 * Sets the sm supplier statistic by category view.
	 * 
	 * @param smSupplierStatisticByCategoryView
	 *            the smSupplierStatisticByCategoryView to set
	 */
	public void setSmSupplierStatisticByCategoryView(
			boolean smSupplierStatisticByCategoryView) {
		this.smSupplierStatisticByCategoryView = smSupplierStatisticByCategoryView;
	}

	/**
	 * Checks if is sm threshold or supplier transaction view.
	 * 
	 * @return the smThresholdOrSupplierTransactionView
	 */
	public boolean isSmThresholdOrSupplierTransactionView() {
		return smThresholdOrSupplierTransactionView;
	}

	/**
	 * Sets the sm threshold or supplier transaction view.
	 * 
	 * @param smThresholdOrSupplierTransactionView
	 *            the smThresholdOrSupplierTransactionView to set
	 */
	public void setSmThresholdOrSupplierTransactionView(
			boolean smThresholdOrSupplierTransactionView) {
		this.smThresholdOrSupplierTransactionView = smThresholdOrSupplierTransactionView;
	}

	/**
	 * Checks if is sm threshold year view.
	 * 
	 * @return the smThresholdYearView
	 */
	public boolean isSmThresholdYearView() {
		return smThresholdYearView;
	}

	/**
	 * Sets the sm threshold year view.
	 * 
	 * @param smThresholdYearView
	 *            the smThresholdYearView to set
	 */
	public void setSmThresholdYearView(boolean smThresholdYearView) {
		this.smThresholdYearView = smThresholdYearView;
	}

	/**
	 * Checks if is sm total no of daily ar view.
	 * 
	 * @return the smTotalNoOfDailyARView
	 */
	public boolean isSmTotalNoOfDailyARView() {
		return smTotalNoOfDailyARView;
	}

	/**
	 * Sets the sm total no of daily ar view.
	 * 
	 * @param smTotalNoOfDailyARView
	 *            the smTotalNoOfDailyARView to set
	 */
	public void setSmTotalNoOfDailyARView(boolean smTotalNoOfDailyARView) {
		this.smTotalNoOfDailyARView = smTotalNoOfDailyARView;
	}

	/**
	 * Checks if is sm total no of yearly ar view.
	 * 
	 * @return the smTotalNoOfYearlyARView
	 */
	public boolean isSmTotalNoOfYearlyARView() {
		return smTotalNoOfYearlyARView;
	}

	/**
	 * Sets the sm total no of yearly ar view.
	 * 
	 * @param smTotalNoOfYearlyARView
	 *            the smTotalNoOfYearlyARView to set
	 */
	public void setSmTotalNoOfYearlyARView(boolean smTotalNoOfYearlyARView) {
		this.smTotalNoOfYearlyARView = smTotalNoOfYearlyARView;
	}

	/**
	 * Checks if is sm virtual certificate relased list view.
	 * 
	 * @return the smVirtualCertificateRelasedListView
	 */
	public boolean isSmVirtualCertificateRelasedListView() {
		return smVirtualCertificateRelasedListView;
	}

	/**
	 * Sets the sm virtual certificate relased list view.
	 * 
	 * @param smVirtualCertificateRelasedListView
	 *            the smVirtualCertificateRelasedListView to set
	 */
	public void setSmVirtualCertificateRelasedListView(
			boolean smVirtualCertificateRelasedListView) {
		this.smVirtualCertificateRelasedListView = smVirtualCertificateRelasedListView;
	}

	/**
	 * Checks if is sm collection receipt transation view.
	 * 
	 * @return the smCollectionReceiptTransationView
	 */
	public boolean isSmCollectionReceiptTransationView() {
		return smCollectionReceiptTransationView;
	}

	/**
	 * Sets the sm collection receipt transation view.
	 * 
	 * @param smCollectionReceiptTransationView
	 *            the smCollectionReceiptTransationView to set
	 */
	public void setSmCollectionReceiptTransationView(
			boolean smCollectionReceiptTransationView) {
		this.smCollectionReceiptTransationView = smCollectionReceiptTransationView;
	}

	/**
	 * Checks if is sm collection report payment method view.
	 * 
	 * @return the smCollectionReportPaymentMethodView
	 */
	public boolean isSmCollectionReportPaymentMethodView() {
		return smCollectionReportPaymentMethodView;
	}

	/**
	 * Sets the sm collection report payment method view.
	 * 
	 * @param smCollectionReportPaymentMethodView
	 *            the smCollectionReportPaymentMethodView to set
	 */
	public void setSmCollectionReportPaymentMethodView(
			boolean smCollectionReportPaymentMethodView) {
		this.smCollectionReportPaymentMethodView = smCollectionReportPaymentMethodView;
	}

	/**
	 * Checks if is sm collection report clasification code view.
	 * 
	 * @return the smCollectionReportClasificationCodeView
	 */
	public boolean isSmCollectionReportClasificationCodeView() {
		return smCollectionReportClasificationCodeView;
	}

	/**
	 * Sets the sm collection report clasification code view.
	 * 
	 * @param smCollectionReportClasificationCodeView
	 *            the smCollectionReportClasificationCodeView to set
	 */
	public void setSmCollectionReportClasificationCodeView(
			boolean smCollectionReportClasificationCodeView) {
		this.smCollectionReportClasificationCodeView = smCollectionReportClasificationCodeView;
	}

	/**
	 * Checks if is sm revenue collection report view.
	 * 
	 * @return the smRevenueCollectionReportView
	 */
	public boolean isSmRevenueCollectionReportView() {
		return smRevenueCollectionReportView;
	}

	/**
	 * Sets the sm revenue collection report view.
	 * 
	 * @param smRevenueCollectionReportView
	 *            the smRevenueCollectionReportView to set
	 */
	public void setSmRevenueCollectionReportView(
			boolean smRevenueCollectionReportView) {
		this.smRevenueCollectionReportView = smRevenueCollectionReportView;
	}

	/**
	 * Checks if is sm list of daily receipt view.
	 * 
	 * @return the smListOfDailyReceiptView
	 */
	public boolean isSmListOfDailyReceiptView() {
		return smListOfDailyReceiptView;
	}

	/**
	 * Sets the sm list of daily receipt view.
	 * 
	 * @param smListOfDailyReceiptView
	 *            the smListOfDailyReceiptView to set
	 */
	public void setSmListOfDailyReceiptView(boolean smListOfDailyReceiptView) {
		this.smListOfDailyReceiptView = smListOfDailyReceiptView;
	}

	/**
	 * Checks if is pm exempted item view.
	 * 
	 * @return the pmExemptedItemView
	 */
	public boolean isPmExemptedItemView() {
		return pmExemptedItemView;
	}

	/**
	 * Sets the pm exempted item view.
	 * 
	 * @param pmExemptedItemView
	 *            the pmExemptedItemView to set
	 */
	public void setPmExemptedItemView(boolean pmExemptedItemView) {
		this.pmExemptedItemView = pmExemptedItemView;
	}

	/**
	 * Checks if is pm exempted contracted item view.
	 * 
	 * @return the pmExemptedContractedItemView
	 */
	public boolean isPmExemptedContractedItemView() {
		return pmExemptedContractedItemView;
	}

	/**
	 * Sets the pm exempted contracted item view.
	 * 
	 * @param pmExemptedContractedItemView
	 *            the pmExemptedContractedItemView to set
	 */
	public void setPmExemptedContractedItemView(boolean pmExemptedContractedItemView) {
		this.pmExemptedContractedItemView = pmExemptedContractedItemView;
	}
}
