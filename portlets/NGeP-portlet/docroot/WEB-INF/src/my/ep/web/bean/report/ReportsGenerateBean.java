package my.ep.web.bean.report;


import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @Author Jangari Kondal Reddy
 */
public class ReportsGenerateBean {
	
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();	
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();
	
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();	
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();
	private List<String> stsLst = new ArrayList<String>();
	private Object[] statusLst;
	private boolean fromDateView = false;
	private boolean toDateView = false;
				
		//sc module
		private boolean extraTwoColumnView = false;		
		private boolean scQuotationNoView = false;
		private boolean scCreatedFromView = false;
		private boolean scStatusView = false;
		private boolean scPtjView = false;
		private boolean scDeskOfficerView = false;
		private boolean scRstrctdChkView = false;
		private boolean scTenderChkView = false;
		private boolean scQuotationChkView = false;
		private boolean scProcureAllChkView = false;
		private boolean scPublishDateView = false;
		private boolean scProcureAllEditView = false;
		private boolean scProcureQuoteEditView = false;
		private boolean scProcureTenderEditView = false;
		private boolean scProcureRestrctdEditView = false;
		private boolean scSoneCrtdFromDateView = false;
		private boolean scRequisenerIdView = false;
		private boolean scSupplierView = false;
		private boolean scPInoView = false;
		private boolean scSoneStatusView = false;
		private boolean scPrcRptGroupView = false;
		private boolean scPIDtlsPrcRptGroupView = false;
		private boolean scProcurementTypeView = false;
		private boolean scProcessRptQuotationNoView = false;
		private boolean scCommonStatusView = false;
		private boolean scLAPRNPURCHDEFAULTGroupView = false;
		private boolean scREQUESTNOTESUMMARYGroupView = false;
		private boolean scPIDNoView = false;
		private boolean scContractOrPKPTypeView = false;
		private boolean scContractOrPKPNoView = false;
		private boolean scContractOrPKPNameView = false;
		private boolean scRequestQuoteNoView = false;
		//cm module
		private boolean cmItemTypeView = false;
		private boolean cmItemStatusView = false;
		private boolean cmUnspscCodeView = false;
		private boolean cmUnspscTitleView = false;	
		private boolean cmCompanyNameView = false;
		private boolean cmRegistrationTypeView = false;	
		private boolean cmItemCategoryView = false;
		private boolean cmItemNameView = false;	
		private boolean cmEpNumberView = false;
		private boolean cmBusinessRegNoView = false;
		private boolean cmIcNumberView = false;
		private boolean cmMofNumberView = false;	
		private boolean cmExtentionCodeView = false;
		private boolean cmExtentionTypeView = false;	
		private boolean cmExtentionBrandView = false;
		private boolean cmExtentionMeasurmentView = false;
		private boolean cmExtentionColourView = false;
		private boolean cmProofIdentityView = false;
		private boolean cmBasicRegView = false;
		private boolean cmMofRegView = false;		
		//SM module
		private boolean smSupportingDocumentView = false;	
		private boolean smDocStatusView = false;
		private boolean smSuppDocStatusView = false;
		private boolean smRegistrationNoView = false;
		private boolean smEpNoView = false;
		private boolean smApplicationNoView = false;
		private boolean smZoneView = false;
		private boolean smSupplierTypeView = false;
		private boolean smMofRegNoView = false;
		private boolean smMofRegNoFromView = false;
		private boolean smMofRegNoToView = false;
		private boolean smRegistrationStatusView = false;
		private boolean smCapitalNaccumView = false;
		private boolean smPaidUpCapitalNaccumShareView = false;
		private boolean smCategoryView = false;
		private boolean smNoOfCategoryView = false;
		private boolean smMainCategoryView = false;
		private boolean smSubCategoryView = false;
		private boolean smSubCategoryDivisionView = false;
		private boolean smRoleView = false;
		private boolean smApplicationStatusView = false;
		private boolean smCancelledByView = false;
		private boolean smPaymentTypeView = false;
		private boolean smApplicationTypeView = false;
		private boolean smStateView = false;
		private boolean smDistrictView = false;
		private boolean smSupplierNameView = false;
		private boolean smReportTypeView = false;
		private boolean smOrganizationTypeView = false;
		private boolean smKementerianView = false;
		private boolean smNamaAgensiView = false;
		private boolean smComplaintNoView = false;
		private boolean smProofIdentityView = false;
		private boolean smMofNoView = false;	
		private boolean smCompanyStatusView = false;
		private boolean smCaseNoView = false;
		private boolean smExtraTwoColumnView = false;	
		private boolean smCompApplStatusView = false;		
		private boolean smCompanyNameView = false;	
		private boolean smcategoryCodeView = false;
		private boolean smSiteVisitOfficerView = false;
		private boolean smReleaseDateView = false;
		private boolean smApplicationDateView = false;
		private boolean smReceiveDateView = false;
		private boolean smExpiryDateView = false;
		private boolean smPrintedDateView = false;
		private boolean smMandatoryZoneView = false;
		private boolean smRegistrationTypeView = false;
		private boolean smSupplierView = false;
		private boolean smCategoryCodeView = false;
		private boolean smBasicRegView = false;
		private boolean smMofRegView = false;
		private boolean smMandatorySupplierTypeView = false;
		private boolean smMandatoryApplicationDateView = false;
		private boolean smMandatoryApplicationStatusView = false;
		private boolean smMandatoryMofRegNoView = false;
		private boolean smMandatoryExpiryDateView = false;
		private boolean smMandatoryPrintedDateView = false;	
		private boolean smMandatoryCategoryView = false;
		private boolean smMandatoryFromDateView = false;
		private boolean smMandatoryStateView = false;
		private boolean smMandatoryDistrictView = false;
		private boolean smMandatoryRegistrationStatusView = false;
		private boolean smMandatoryCreatedFromYearView = false;
		private boolean smMandatoryCompanyNameView = false;
		private boolean smMandatoryRegistrationTypeView = false;
		private boolean smMandatoryMonthlyRptByYearView = false;
		// FL module
		private boolean flPtjView = false;
		private boolean flContractNoView = false;
		private boolean flSstView = false;
		private boolean flJabatanView = false;
		private boolean flMinistryView = false;
		private boolean flSupplierView = false;	
		private boolean flStatusView = false;
		private boolean flDurationView = false;
		private boolean flRecievingOfficerView = false;
		private boolean flDeliveryAddressView = false;
		private boolean flRecNoteNoView = false;
		private boolean flContractTypeView = false;
		private boolean flRequisitionerView = false;
		private boolean flMtoContractView = false;
		private boolean flInvoiceNoView = false;
		private boolean flDeductionTypeView = false;
		private boolean flCrNoView = false;
		private boolean flApproverNameView = false;
		private boolean flDocumentTypeView = false;
		private boolean flCriteriaView = false;
		private boolean flScoringView = false;
		private boolean flPkpNoView = false;
		private boolean flDeliveryOrderNoView = false;	
		private boolean flItemCodeView = false;
		private boolean flPoAmountView = false;
		private boolean flPrNoView = false;
		private boolean flPmoView = false;		
		private boolean flItemNameView = false;
		private boolean flItemTypeView = false;
		private boolean flContractOrderView = false;
		private boolean flContractRequestView = false;
		private boolean flPurchaseOrderNoView = false;
		private boolean flGroupOneView = false;
		private boolean flGroupTwoView = false;
		private boolean flGroupThreeView = false;
		private boolean flGroupFourView = false;
		private boolean flGroupFiveView = false;
		private boolean flGroupSixView = false;
		private boolean flGroupSevenView = false;
		private boolean flGroupEightView = false;
		private boolean flGroupNineView = false;
		private boolean flGroupTenView = false;
		private boolean flGroupElevenView = false;
		private boolean flGroupTwelveView = false;
		private boolean flGroupThirteenView = false;
		private boolean flGroupFourteenView = false;
		private boolean flGroupFifteenView = false;
		private boolean flGroupSixteenView = false;
		//CT
		private boolean ctContractNoView = false;
		private boolean ctAgreementView = false;
		private boolean ctStatusView = false;
		private boolean ctContrMontrStatusView = false;
		private boolean ctCompleteView = false;
	   //URL variables start
		private String quotationIdTotVar=ReportConst.EMPTY_VAL;
		private String itemTypeTotVar=ReportConst.EMPTY_VAL;
		private String itemStatusTotVar=ReportConst.EMPTY_VAL;		
		private String unspscCodeTotVar=ReportConst.EMPTY_VAL;
		private String unspscTitleTotVar=ReportConst.EMPTY_VAL;
		private String extentionCodeTotVar=ReportConst.EMPTY_VAL;
		private String extentionTypeTotVar=ReportConst.EMPTY_VAL;
		private String extentionBrandTotVar=ReportConst.EMPTY_VAL;
		private String extentionMeasurmentTotVar=ReportConst.EMPTY_VAL;
		private String extentionColourTotVar=ReportConst.EMPTY_VAL;
		private String companyNameTotVar=ReportConst.EMPTY_VAL;
		private String registrationTypeTotVar=ReportConst.EMPTY_VAL;	
		private String itemCategoryTotVar=ReportConst.EMPTY_VAL;
		private String itemNameTotVar=ReportConst.EMPTY_VAL;
		private String proofIdentityTotVar=ReportConst.EMPTY_VAL;
		private String epNumberTotVar=ReportConst.EMPTY_VAL;	
		private String businessRegNoTotVar=ReportConst.EMPTY_VAL;
		private String icNumberTotVar=ReportConst.EMPTY_VAL;
		private String mofNumberTotVar=ReportConst.EMPTY_VAL;
		private String ptjTotVar=ReportConst.EMPTY_VAL;
		private String contractNoTotVar=ReportConst.EMPTY_VAL;
		private String sstTotVar=ReportConst.EMPTY_VAL;
		private String jabatanTotVar=ReportConst.EMPTY_VAL;
		private String ministryTotVar=ReportConst.EMPTY_VAL;
		private String supplierTotVar=ReportConst.EMPTY_VAL;
		private String statusTotVar=ReportConst.EMPTY_VAL;
		private String durationTotVar=ReportConst.EMPTY_VAL;
		private String recievingOfficerTotVar=ReportConst.EMPTY_VAL;
		private String deliveryAddressTotVar=ReportConst.EMPTY_VAL;
		private String flRecNoteNoTotVar=ReportConst.EMPTY_VAL;
		private String contractTypeTotVar=ReportConst.EMPTY_VAL;
		private String requisitionerTotVar=ReportConst.EMPTY_VAL;
		private String mtoContractTotVar=ReportConst.EMPTY_VAL;
		private String invoiceNoTotVar=ReportConst.EMPTY_VAL;
		private String deductionTypeTotVar=ReportConst.EMPTY_VAL;
		private String crNoTotVar=ReportConst.EMPTY_VAL;
		private String approverNameTotVar=ReportConst.EMPTY_VAL;
		private String documentTypeTotVar=ReportConst.EMPTY_VAL;
		private String criteriaTotVar=ReportConst.EMPTY_VAL;
		private String scoringTotVar=ReportConst.EMPTY_VAL;
		private String pkpNoTotVar=ReportConst.EMPTY_VAL;
		private String deliveryOrderNoTotVar=ReportConst.EMPTY_VAL;	
		private String itemCodeTotVar=ReportConst.EMPTY_VAL;
		private String poAmountTotVar=ReportConst.EMPTY_VAL;
		private String prNoTotVar=ReportConst.EMPTY_VAL;
		private String pmoTotVar=ReportConst.EMPTY_VAL;
		private String fromDateVar=ReportConst.EMPTY_VAL;
		private String toDateVar=ReportConst.EMPTY_VAL;
		
		private String supportingDocumentTotVar=ReportConst.EMPTY_VAL;	
		private String docStatusTotVar=ReportConst.EMPTY_VAL;	
		private String suppDocStatusTotVar=ReportConst.EMPTY_VAL;		
		private String registrationNoTotVar=ReportConst.EMPTY_VAL;	
		private String ePNoTotVar=ReportConst.EMPTY_VAL;	
		private String applicationNoTotVar=ReportConst.EMPTY_VAL;	
		private String zoneTotVar=ReportConst.EMPTY_VAL;	
		private String supplierTypeTotVar=ReportConst.EMPTY_VAL;	
		private String mofRegNoTotVar=ReportConst.EMPTY_VAL;	
		private String mofRegNoFromTotVar=ReportConst.EMPTY_VAL;	
		private String mofRegNoToTotVar=ReportConst.EMPTY_VAL;	
		private String registrationStatusTotVar=ReportConst.EMPTY_VAL;	
		private String capitalNaccumFrmTotVar=ReportConst.EMPTY_VAL;	
		private String capitalNaccumToTotVar=ReportConst.EMPTY_VAL;		
		private String paidUpCapitalNaccumShareTotVar=ReportConst.EMPTY_VAL;	
		private String categoryTotVar=ReportConst.EMPTY_VAL;	
		private String noOfCategoryTotVar=ReportConst.EMPTY_VAL;	
		private String mainCategoryTotVar=ReportConst.EMPTY_VAL;	
		private String subCategoryTotVar=ReportConst.EMPTY_VAL;	
		private String subCategoryDivisionTotVar=ReportConst.EMPTY_VAL;	
		private String roleTotVar=ReportConst.EMPTY_VAL;	
		private String applicationStatusTotVar=ReportConst.EMPTY_VAL;	
		private String cancelledByTotVar=ReportConst.EMPTY_VAL;	
		private String paymentTypeTotVar=ReportConst.EMPTY_VAL;	
		private String applicationTypeTotVar=ReportConst.EMPTY_VAL;	
		private String stateTotVar=ReportConst.EMPTY_VAL;	
		private String districtTotVar=ReportConst.EMPTY_VAL;	
		private String supplierNameTotVar=ReportConst.EMPTY_VAL;	
		private String reportTypeTotVar=ReportConst.EMPTY_VAL;	
		private String organizationTypeTotVar=ReportConst.EMPTY_VAL;	
		private String kementerianTotVar=ReportConst.EMPTY_VAL;	
		private String namAgensiTotVar=ReportConst.EMPTY_VAL;	
		private String complaintNoTotVar=ReportConst.EMPTY_VAL;	
	
		private String companyStatusTotVar=ReportConst.EMPTY_VAL;	
		private String caseNoTotVar=ReportConst.EMPTY_VAL;	
		private String compApplStatusTotVar=ReportConst.EMPTY_VAL;	
		private String categoryCodeTotVar=ReportConst.EMPTY_VAL;	
		private String siteVisitOfficerTotVar=ReportConst.EMPTY_VAL;	
		private String releaseFromDateTotVar=ReportConst.EMPTY_VAL;	
		private String releaseToDateTotVar=ReportConst.EMPTY_VAL;	
		private String applicationFromDateTotVar=ReportConst.EMPTY_VAL;	
		private String applicationToDateTotVar=ReportConst.EMPTY_VAL;	
		private String receiveFromDateTotVar=ReportConst.EMPTY_VAL;		
		private String receiveToDateTotVar=ReportConst.EMPTY_VAL;		
		private String expiryFromDateTotVar=ReportConst.EMPTY_VAL;		
		private String expiryToDateTotVar=ReportConst.EMPTY_VAL;	
		private String printedFromDateTotVar=ReportConst.EMPTY_VAL;		
		private String printedToDateTotVar=ReportConst.EMPTY_VAL;
		private String flContractOrderVar=ReportConst.EMPTY_VAL;		
		private String flContractRequestVar=ReportConst.EMPTY_VAL;
		private String flPurchaseOrderNoVar=ReportConst.EMPTY_VAL;
		private String reportValueIdVar=ReportConst.EMPTY_VAL;
		private String userIdVar=ReportConst.EMPTY_VAL;
		
		private String scCreatedFromDtVar=ReportConst.EMPTY_VAL;
		private String scCreatedToDtVar=ReportConst.EMPTY_VAL;
		private String scStatusVar=ReportConst.EMPTY_VAL;
		private String scPtjVar=ReportConst.EMPTY_VAL;
		private String scDeskOfficerVar=ReportConst.EMPTY_VAL;		
		private String scProcureMethodChkVar=ReportConst.EMPTY_VAL;
		private String scPublishFromDateVar=ReportConst.EMPTY_VAL;
		private String scPublishToDateVar=ReportConst.EMPTY_VAL;
		private String scUserAccessRoleVar=ReportConst.EMPTY_VAL;
		private String scSoneCrtdFromDateVar=ReportConst.EMPTY_VAL;
		private String scSoneCrtdToDateVar=ReportConst.EMPTY_VAL;
		private String scRequisenerIdVar=ReportConst.EMPTY_VAL;
		private String scSupplierVar=ReportConst.EMPTY_VAL;
		private String scPInoVar=ReportConst.EMPTY_VAL;
		private String scSoneStatusVar=ReportConst.EMPTY_VAL;				
		private String scPrcRptGroupVar=ReportConst.EMPTY_VAL;
		private String scPIDtlsPrcRptGroupVar=ReportConst.EMPTY_VAL;
		private String scProcurementTypeVar=ReportConst.EMPTY_VAL;
		private String scProcessRptQuotationNoVar=ReportConst.EMPTY_VAL;
		private String scCommonStatusVar=ReportConst.EMPTY_VAL;
		private String scLAPRNPURCHDEFAULTGroupVar=ReportConst.EMPTY_VAL;
		private String scREQUESTNOTESUMMARYGroupVar=ReportConst.EMPTY_VAL;
		private String scPIDNoVar=ReportConst.EMPTY_VAL;
		private String scContractOrPKPTypeVar=ReportConst.EMPTY_VAL;
		private String scContractOrPKPNoVar=ReportConst.EMPTY_VAL;
		private String scContractOrPKPNameVar=ReportConst.EMPTY_VAL;
		private String scRequestQuoteNoVar=ReportConst.EMPTY_VAL;
		//CT
		private String ctContractNoVar=ReportConst.EMPTY_VAL;
		private String ctAgreementVar=ReportConst.EMPTY_VAL;
		private String ctStatusVar=ReportConst.EMPTY_VAL;
		private String ctContrMontrStatusVar=ReportConst.EMPTY_VAL;
		private String ctHiddenParamGroupVar=ReportConst.EMPTY_VAL;
		private String ctCompleteVar=ReportConst.EMPTY_VAL;
		
		private String flGroupOneVar=ReportConst.EMPTY_VAL;
		private String flGroupTwoVar=ReportConst.EMPTY_VAL;
		private String flGroupThreeVar=ReportConst.EMPTY_VAL;
		private String flGroupFourVar=ReportConst.EMPTY_VAL;
		private String flGroupFiveVar=ReportConst.EMPTY_VAL;
		private String flGroupSixVar=ReportConst.EMPTY_VAL;
		private String flGroupSevenVar=ReportConst.EMPTY_VAL;
		private String flGroupEightVar=ReportConst.EMPTY_VAL;
		private String flGroupNineVar=ReportConst.EMPTY_VAL;
		private String flGroupTenVar=ReportConst.EMPTY_VAL;
		private String flGroupElevenVar=ReportConst.EMPTY_VAL;
		private String flGroupTwelveVar=ReportConst.EMPTY_VAL;
		private String flGroupThirteenVar=ReportConst.EMPTY_VAL;
		private String flGroupFourteenVar=ReportConst.EMPTY_VAL;
		private String flGroupFifteenVar=ReportConst.EMPTY_VAL;
		private String flGroupSixteenVar=ReportConst.EMPTY_VAL;
		
		private String smCreatedFromYearVar=ReportConst.EMPTY_VAL;
		private String smCreatedToYearVar=ReportConst.EMPTY_VAL;
		private String smMonthlyRptByYearVar=ReportConst.EMPTY_VAL;
		
	
		public boolean isSmMandatoryMonthlyRptByYearView() {
			return smMandatoryMonthlyRptByYearView;
		}
		public void setSmMandatoryMonthlyRptByYearView(
				boolean smMandatoryMonthlyRptByYearView) {
			this.smMandatoryMonthlyRptByYearView = smMandatoryMonthlyRptByYearView;
		}
		public String getSmMonthlyRptByYearVar() {
			return smMonthlyRptByYearVar;
		}
		public void setSmMonthlyRptByYearVar(String smMonthlyRptByYearVar) {
			this.smMonthlyRptByYearVar = smMonthlyRptByYearVar;
		}
		public boolean isSmMandatoryRegistrationTypeView() {
			return smMandatoryRegistrationTypeView;
		}
		public void setSmMandatoryRegistrationTypeView(
				boolean smMandatoryRegistrationTypeView) {
			this.smMandatoryRegistrationTypeView = smMandatoryRegistrationTypeView;
		}
		public boolean isSmMandatoryCompanyNameView() {
			return smMandatoryCompanyNameView;
		}
		public void setSmMandatoryCompanyNameView(boolean smMandatoryCompanyNameView) {
			this.smMandatoryCompanyNameView = smMandatoryCompanyNameView;
		}
		public boolean isSmMandatoryCreatedFromYearView() {
			return smMandatoryCreatedFromYearView;
		}
		public void setSmMandatoryCreatedFromYearView(
				boolean smMandatoryCreatedFromYearView) {
			this.smMandatoryCreatedFromYearView = smMandatoryCreatedFromYearView;
		}
		public String getSmCreatedFromYearVar() {
			return smCreatedFromYearVar;
		}
		public void setSmCreatedFromYearVar(String smCreatedFromYearVar) {
			this.smCreatedFromYearVar = smCreatedFromYearVar;
		}
		public String getSmCreatedToYearVar() {
			return smCreatedToYearVar;
		}
		public void setSmCreatedToYearVar(String smCreatedToYearVar) {
			this.smCreatedToYearVar = smCreatedToYearVar;
		}
		public boolean isSmMandatoryRegistrationStatusView() {
			return smMandatoryRegistrationStatusView;
		}
		public void setSmMandatoryRegistrationStatusView(
				boolean smMandatoryRegistrationStatusView) {
			this.smMandatoryRegistrationStatusView = smMandatoryRegistrationStatusView;
		}
		public boolean isSmMandatoryDistrictView() {
			return smMandatoryDistrictView;
		}
		public void setSmMandatoryDistrictView(boolean smMandatoryDistrictView) {
			this.smMandatoryDistrictView = smMandatoryDistrictView;
		}
		public boolean isSmMandatoryStateView() {
			return smMandatoryStateView;
		}
		public void setSmMandatoryStateView(boolean smMandatoryStateView) {
			this.smMandatoryStateView = smMandatoryStateView;
		}
		public boolean isSmMandatoryFromDateView() {
			return smMandatoryFromDateView;
		}
		public void setSmMandatoryFromDateView(boolean smMandatoryFromDateView) {
			this.smMandatoryFromDateView = smMandatoryFromDateView;
		}
		public boolean isSmMandatoryCategoryView() {
			return smMandatoryCategoryView;
		}
		public void setSmMandatoryCategoryView(boolean smMandatoryCategoryView) {
			this.smMandatoryCategoryView = smMandatoryCategoryView;
		}
		public boolean isScRequestQuoteNoView() {
			return scRequestQuoteNoView;
		}
		public void setScRequestQuoteNoView(boolean scRequestQuoteNoView) {
			this.scRequestQuoteNoView = scRequestQuoteNoView;
		}
		public String getScRequestQuoteNoVar() {
			return scRequestQuoteNoVar;
		}
		public void setScRequestQuoteNoVar(String scRequestQuoteNoVar) {
			this.scRequestQuoteNoVar = scRequestQuoteNoVar;
		}
		public boolean isScPIDNoView() {
			return scPIDNoView;
		}
		public void setScPIDNoView(boolean scPIDNoView) {
			this.scPIDNoView = scPIDNoView;
		}
		public boolean isScContractOrPKPTypeView() {
			return scContractOrPKPTypeView;
		}
		public void setScContractOrPKPTypeView(boolean scContractOrPKPTypeView) {
			this.scContractOrPKPTypeView = scContractOrPKPTypeView;
		}
		public boolean isScContractOrPKPNoView() {
			return scContractOrPKPNoView;
		}
		public void setScContractOrPKPNoView(boolean scContractOrPKPNoView) {
			this.scContractOrPKPNoView = scContractOrPKPNoView;
		}
		public boolean isScContractOrPKPNameView() {
			return scContractOrPKPNameView;
		}
		public void setScContractOrPKPNameView(boolean scContractOrPKPNameView) {
			this.scContractOrPKPNameView = scContractOrPKPNameView;
		}
		public String getScPIDNoVar() {
			return scPIDNoVar;
		}
		public void setScPIDNoVar(String scPIDNoVar) {
			this.scPIDNoVar = scPIDNoVar;
		}
		public String getScContractOrPKPTypeVar() {
			return scContractOrPKPTypeVar;
		}
		public void setScContractOrPKPTypeVar(String scContractOrPKPTypeVar) {
			this.scContractOrPKPTypeVar = scContractOrPKPTypeVar;
		}
		public String getScContractOrPKPNoVar() {
			return scContractOrPKPNoVar;
		}
		public void setScContractOrPKPNoVar(String scContractOrPKPNoVar) {
			this.scContractOrPKPNoVar = scContractOrPKPNoVar;
		}
		public String getScContractOrPKPNameVar() {
			return scContractOrPKPNameVar;
		}
		public void setScContractOrPKPNameVar(String scContractOrPKPNameVar) {
			this.scContractOrPKPNameVar = scContractOrPKPNameVar;
		}
		public boolean isScLAPRNPURCHDEFAULTGroupView() {
			return scLAPRNPURCHDEFAULTGroupView;
		}
		public void setScLAPRNPURCHDEFAULTGroupView(boolean scLAPRNPURCHDEFAULTGroupView) {
			this.scLAPRNPURCHDEFAULTGroupView = scLAPRNPURCHDEFAULTGroupView;
		}
		public boolean isScREQUESTNOTESUMMARYGroupView() {
			return scREQUESTNOTESUMMARYGroupView;
		}
		public void setScREQUESTNOTESUMMARYGroupView(
				boolean scREQUESTNOTESUMMARYGroupView) {
			this.scREQUESTNOTESUMMARYGroupView = scREQUESTNOTESUMMARYGroupView;
		}
		public String getScLAPRNPURCHDEFAULTGroupVar() {
			return scLAPRNPURCHDEFAULTGroupVar;
		}
		public void setScLAPRNPURCHDEFAULTGroupVar(String scLAPRNPURCHDEFAULTGroupVar) {
			this.scLAPRNPURCHDEFAULTGroupVar = scLAPRNPURCHDEFAULTGroupVar;
		}
		public String getScREQUESTNOTESUMMARYGroupVar() {
			return scREQUESTNOTESUMMARYGroupVar;
		}
		public void setScREQUESTNOTESUMMARYGroupVar(String scREQUESTNOTESUMMARYGroupVar) {
			this.scREQUESTNOTESUMMARYGroupVar = scREQUESTNOTESUMMARYGroupVar;
		}
		public boolean isScCommonStatusView() {
			return scCommonStatusView;
		}
		public void setScCommonStatusView(boolean scCommonStatusView) {
			this.scCommonStatusView = scCommonStatusView;
		}
		public String getScCommonStatusVar() {
			return scCommonStatusVar;
		}
		public void setScCommonStatusVar(String scCommonStatusVar) {
			this.scCommonStatusVar = scCommonStatusVar;
		}
		public boolean isFlGroupOneView() {
			return flGroupOneView;
		}
		public void setFlGroupOneView(boolean flGroupOneView) {
			this.flGroupOneView = flGroupOneView;
		}
		public boolean isFlGroupTwoView() {
			return flGroupTwoView;
		}
		public void setFlGroupTwoView(boolean flGroupTwoView) {
			this.flGroupTwoView = flGroupTwoView;
		}
		public boolean isFlGroupThreeView() {
			return flGroupThreeView;
		}
		public void setFlGroupThreeView(boolean flGroupThreeView) {
			this.flGroupThreeView = flGroupThreeView;
		}
		public boolean isFlGroupFourView() {
			return flGroupFourView;
		}
		public void setFlGroupFourView(boolean flGroupFourView) {
			this.flGroupFourView = flGroupFourView;
		}
		public boolean isFlGroupFiveView() {
			return flGroupFiveView;
		}
		public void setFlGroupFiveView(boolean flGroupFiveView) {
			this.flGroupFiveView = flGroupFiveView;
		}
		public boolean isFlGroupSixView() {
			return flGroupSixView;
		}
		public void setFlGroupSixView(boolean flGroupSixView) {
			this.flGroupSixView = flGroupSixView;
		}
		public boolean isFlGroupSevenView() {
			return flGroupSevenView;
		}
		public void setFlGroupSevenView(boolean flGroupSevenView) {
			this.flGroupSevenView = flGroupSevenView;
		}
		public boolean isFlGroupEightView() {
			return flGroupEightView;
		}
		public void setFlGroupEightView(boolean flGroupEightView) {
			this.flGroupEightView = flGroupEightView;
		}
		public boolean isFlGroupNineView() {
			return flGroupNineView;
		}
		public void setFlGroupNineView(boolean flGroupNineView) {
			this.flGroupNineView = flGroupNineView;
		}
		public boolean isFlGroupTenView() {
			return flGroupTenView;
		}
		public void setFlGroupTenView(boolean flGroupTenView) {
			this.flGroupTenView = flGroupTenView;
		}
		public boolean isFlGroupElevenView() {
			return flGroupElevenView;
		}
		public void setFlGroupElevenView(boolean flGroupElevenView) {
			this.flGroupElevenView = flGroupElevenView;
		}
		public boolean isFlGroupTwelveView() {
			return flGroupTwelveView;
		}
		public void setFlGroupTwelveView(boolean flGroupTwelveView) {
			this.flGroupTwelveView = flGroupTwelveView;
		}
		public boolean isFlGroupThirteenView() {
			return flGroupThirteenView;
		}
		public void setFlGroupThirteenView(boolean flGroupThirteenView) {
			this.flGroupThirteenView = flGroupThirteenView;
		}
		public boolean isFlGroupFourteenView() {
			return flGroupFourteenView;
		}
		public void setFlGroupFourteenView(boolean flGroupFourteenView) {
			this.flGroupFourteenView = flGroupFourteenView;
		}
		public boolean isFlGroupFifteenView() {
			return flGroupFifteenView;
		}
		public void setFlGroupFifteenView(boolean flGroupFifteenView) {
			this.flGroupFifteenView = flGroupFifteenView;
		}
		public boolean isFlGroupSixteenView() {
			return flGroupSixteenView;
		}
		public void setFlGroupSixteenView(boolean flGroupSixteenView) {
			this.flGroupSixteenView = flGroupSixteenView;
		}
		public String getFlGroupOneVar() {
			return flGroupOneVar;
		}
		public void setFlGroupOneVar(String flGroupOneVar) {
			this.flGroupOneVar = flGroupOneVar;
		}
		public String getFlGroupTwoVar() {
			return flGroupTwoVar;
		}
		public void setFlGroupTwoVar(String flGroupTwoVar) {
			this.flGroupTwoVar = flGroupTwoVar;
		}
		public String getFlGroupThreeVar() {
			return flGroupThreeVar;
		}
		public void setFlGroupThreeVar(String flGroupThreeVar) {
			this.flGroupThreeVar = flGroupThreeVar;
		}
		public String getFlGroupFourVar() {
			return flGroupFourVar;
		}
		public void setFlGroupFourVar(String flGroupFourVar) {
			this.flGroupFourVar = flGroupFourVar;
		}
		public String getFlGroupFiveVar() {
			return flGroupFiveVar;
		}
		public void setFlGroupFiveVar(String flGroupFiveVar) {
			this.flGroupFiveVar = flGroupFiveVar;
		}
		public String getFlGroupSixVar() {
			return flGroupSixVar;
		}
		public void setFlGroupSixVar(String flGroupSixVar) {
			this.flGroupSixVar = flGroupSixVar;
		}
		public String getFlGroupSevenVar() {
			return flGroupSevenVar;
		}
		public void setFlGroupSevenVar(String flGroupSevenVar) {
			this.flGroupSevenVar = flGroupSevenVar;
		}
		public String getFlGroupEightVar() {
			return flGroupEightVar;
		}
		public void setFlGroupEightVar(String flGroupEightVar) {
			this.flGroupEightVar = flGroupEightVar;
		}
		public String getFlGroupNineVar() {
			return flGroupNineVar;
		}
		public void setFlGroupNineVar(String flGroupNineVar) {
			this.flGroupNineVar = flGroupNineVar;
		}
		public String getFlGroupTenVar() {
			return flGroupTenVar;
		}
		public void setFlGroupTenVar(String flGroupTenVar) {
			this.flGroupTenVar = flGroupTenVar;
		}
		public String getFlGroupElevenVar() {
			return flGroupElevenVar;
		}
		public void setFlGroupElevenVar(String flGroupElevenVar) {
			this.flGroupElevenVar = flGroupElevenVar;
		}
		public String getFlGroupTwelveVar() {
			return flGroupTwelveVar;
		}
		public void setFlGroupTwelveVar(String flGroupTwelveVar) {
			this.flGroupTwelveVar = flGroupTwelveVar;
		}
		public String getFlGroupThirteenVar() {
			return flGroupThirteenVar;
		}
		public void setFlGroupThirteenVar(String flGroupThirteenVar) {
			this.flGroupThirteenVar = flGroupThirteenVar;
		}
		public String getFlGroupFourteenVar() {
			return flGroupFourteenVar;
		}
		public void setFlGroupFourteenVar(String flGroupFourteenVar) {
			this.flGroupFourteenVar = flGroupFourteenVar;
		}
		public String getFlGroupFifteenVar() {
			return flGroupFifteenVar;
		}
		public void setFlGroupFifteenVar(String flGroupFifteenVar) {
			this.flGroupFifteenVar = flGroupFifteenVar;
		}
		public String getFlGroupSixteenVar() {
			return flGroupSixteenVar;
		}
		public void setFlGroupSixteenVar(String flGroupSixteenVar) {
			this.flGroupSixteenVar = flGroupSixteenVar;
		}
		public boolean isCtCompleteView() {
			return ctCompleteView;
		}
		public void setCtCompleteView(boolean ctCompleteView) {
			this.ctCompleteView = ctCompleteView;
		}
		public String getCtCompleteVar() {
			return ctCompleteVar;
		}
		public void setCtCompleteVar(String ctCompleteVar) {
			this.ctCompleteVar = ctCompleteVar;
		}
		public boolean isCtContrMontrStatusView() {
			return ctContrMontrStatusView;
		}
		public void setCtContrMontrStatusView(boolean ctContrMontrStatusView) {
			this.ctContrMontrStatusView = ctContrMontrStatusView;
		}
		public String getCtContrMontrStatusVar() {
			return ctContrMontrStatusVar;
		}
		public void setCtContrMontrStatusVar(String ctContrMontrStatusVar) {
			this.ctContrMontrStatusVar = ctContrMontrStatusVar;
		}
		public String getCtHiddenParamGroupVar() {
			return ctHiddenParamGroupVar;
		}
		public void setCtHiddenParamGroupVar(String ctHiddenParamGroupVar) {
			this.ctHiddenParamGroupVar = ctHiddenParamGroupVar;
		}
		public boolean isCtContractNoView() {
			return ctContractNoView;
		}
		public void setCtContractNoView(boolean ctContractNoView) {
			this.ctContractNoView = ctContractNoView;
		}
		public boolean isCtAgreementView() {
			return ctAgreementView;
		}
		public void setCtAgreementView(boolean ctAgreementView) {
			this.ctAgreementView = ctAgreementView;
		}
		public boolean isCtStatusView() {
			return ctStatusView;
		}
		public void setCtStatusView(boolean ctStatusView) {
			this.ctStatusView = ctStatusView;
		}
		public String getCtContractNoVar() {
			return ctContractNoVar;
		}
		public void setCtContractNoVar(String ctContractNoVar) {
			this.ctContractNoVar = ctContractNoVar;
		}
		public String getCtAgreementVar() {
			return ctAgreementVar;
		}
		public void setCtAgreementVar(String ctAgreementVar) {
			this.ctAgreementVar = ctAgreementVar;
		}
		public String getCtStatusVar() {
			return ctStatusVar;
		}
		public void setCtStatusVar(String ctStatusVar) {
			this.ctStatusVar = ctStatusVar;
		}
		public boolean isScPrcRptGroupView() {
			return scPrcRptGroupView;
		}
		public void setScPrcRptGroupView(boolean scPrcRptGroupView) {
			this.scPrcRptGroupView = scPrcRptGroupView;
		}
		public boolean isScPIDtlsPrcRptGroupView() {
			return scPIDtlsPrcRptGroupView;
		}
		public void setScPIDtlsPrcRptGroupView(boolean scPIDtlsPrcRptGroupView) {
			this.scPIDtlsPrcRptGroupView = scPIDtlsPrcRptGroupView;
		}
		public boolean isScProcurementTypeView() {
			return scProcurementTypeView;
		}
		public void setScProcurementTypeView(boolean scProcurementTypeView) {
			this.scProcurementTypeView = scProcurementTypeView;
		}
		public boolean isScProcessRptQuotationNoView() {
			return scProcessRptQuotationNoView;
		}
		public void setScProcessRptQuotationNoView(boolean scProcessRptQuotationNoView) {
			this.scProcessRptQuotationNoView = scProcessRptQuotationNoView;
		}
		public String getScPrcRptGroupVar() {
			return scPrcRptGroupVar;
		}
		public void setScPrcRptGroupVar(String scPrcRptGroupVar) {
			this.scPrcRptGroupVar = scPrcRptGroupVar;
		}
		public String getScPIDtlsPrcRptGroupVar() {
			return scPIDtlsPrcRptGroupVar;
		}
		public void setScPIDtlsPrcRptGroupVar(String scPIDtlsPrcRptGroupVar) {
			this.scPIDtlsPrcRptGroupVar = scPIDtlsPrcRptGroupVar;
		}
		public String getScProcurementTypeVar() {
			return scProcurementTypeVar;
		}
		public void setScProcurementTypeVar(String scProcurementTypeVar) {
			this.scProcurementTypeVar = scProcurementTypeVar;
		}
		public String getScProcessRptQuotationNoVar() {
			return scProcessRptQuotationNoVar;
		}
		public void setScProcessRptQuotationNoVar(String scProcessRptQuotationNoVar) {
			this.scProcessRptQuotationNoVar = scProcessRptQuotationNoVar;
		}
		public boolean isScSoneCrtdFromDateView() {
			return scSoneCrtdFromDateView;
		}
		public void setScSoneCrtdFromDateView(boolean scSoneCrtdFromDateView) {
			this.scSoneCrtdFromDateView = scSoneCrtdFromDateView;
		}
		public boolean isScRequisenerIdView() {
			return scRequisenerIdView;
		}
		public void setScRequisenerIdView(boolean scRequisenerIdView) {
			this.scRequisenerIdView = scRequisenerIdView;
		}
		public boolean isScSupplierView() {
			return scSupplierView;
		}
		public void setScSupplierView(boolean scSupplierView) {
			this.scSupplierView = scSupplierView;
		}
		public boolean isScPInoView() {
			return scPInoView;
		}
		public void setScPInoView(boolean scPInoView) {
			this.scPInoView = scPInoView;
		}
		public boolean isScSoneStatusView() {
			return scSoneStatusView;
		}
		public void setScSoneStatusView(boolean scSoneStatusView) {
			this.scSoneStatusView = scSoneStatusView;
		}
		public String getScSoneCrtdFromDateVar() {
			return scSoneCrtdFromDateVar;
		}
		public void setScSoneCrtdFromDateVar(String scSoneCrtdFromDateVar) {
			this.scSoneCrtdFromDateVar = scSoneCrtdFromDateVar;
		}
		public String getScSoneCrtdToDateVar() {
			return scSoneCrtdToDateVar;
		}
		public void setScSoneCrtdToDateVar(String scSoneCrtdToDateVar) {
			this.scSoneCrtdToDateVar = scSoneCrtdToDateVar;
		}
		public String getScRequisenerIdVar() {
			return scRequisenerIdVar;
		}
		public void setScRequisenerIdVar(String scRequisenerIdVar) {
			this.scRequisenerIdVar = scRequisenerIdVar;
		}
		public String getScSupplierVar() {
			return scSupplierVar;
		}
		public void setScSupplierVar(String scSupplierVar) {
			this.scSupplierVar = scSupplierVar;
		}
		public String getScPInoVar() {
			return scPInoVar;
		}
		public void setScPInoVar(String scPInoVar) {
			this.scPInoVar = scPInoVar;
		}
		public String getScSoneStatusVar() {
			return scSoneStatusVar;
		}
		public void setScSoneStatusVar(String scSoneStatusVar) {
			this.scSoneStatusVar = scSoneStatusVar;
		}
		public String getScUserAccessRoleVar() {
			return scUserAccessRoleVar;
		}
		public void setScUserAccessRoleVar(String scUserAccessRoleVar) {
			this.scUserAccessRoleVar = scUserAccessRoleVar;
		}
		public boolean isScProcureAllEditView() {
			return scProcureAllEditView;
		}
		public void setScProcureAllEditView(boolean scProcureAllEditView) {
			this.scProcureAllEditView = scProcureAllEditView;
		}
		public boolean isScProcureQuoteEditView() {
			return scProcureQuoteEditView;
		}
		public void setScProcureQuoteEditView(boolean scProcureQuoteEditView) {
			this.scProcureQuoteEditView = scProcureQuoteEditView;
		}
		public boolean isScProcureTenderEditView() {
			return scProcureTenderEditView;
		}
		public void setScProcureTenderEditView(boolean scProcureTenderEditView) {
			this.scProcureTenderEditView = scProcureTenderEditView;
		}
		public boolean isScProcureRestrctdEditView() {
			return scProcureRestrctdEditView;
		}
		public void setScProcureRestrctdEditView(boolean scProcureRestrctdEditView) {
			this.scProcureRestrctdEditView = scProcureRestrctdEditView;
		}
		public String getScProcureMethodChkVar() {
			return scProcureMethodChkVar;
		}
		public void setScProcureMethodChkVar(String scProcureMethodChkVar) {
			this.scProcureMethodChkVar = scProcureMethodChkVar;
		}
		public String getScCreatedFromDtVar() {
			return scCreatedFromDtVar;
		}
		public void setScCreatedFromDtVar(String scCreatedFromDtVar) {
			this.scCreatedFromDtVar = scCreatedFromDtVar;
		}
		public String getScCreatedToDtVar() {
			return scCreatedToDtVar;
		}
		public void setScCreatedToDtVar(String scCreatedToDtVar) {
			this.scCreatedToDtVar = scCreatedToDtVar;
		}
		public String getScPublishFromDateVar() {
			return scPublishFromDateVar;
		}
		public void setScPublishFromDateVar(String scPublishFromDateVar) {
			this.scPublishFromDateVar = scPublishFromDateVar;
		}
		public String getScPublishToDateVar() {
			return scPublishToDateVar;
		}
		public void setScPublishToDateVar(String scPublishToDateVar) {
			this.scPublishToDateVar = scPublishToDateVar;
		}
		public String getScStatusVar() {
			return scStatusVar;
		}
		public void setScStatusVar(String scStatusVar) {
			this.scStatusVar = scStatusVar;
		}
		public String getScPtjVar() {
			return scPtjVar;
		}
		public void setScPtjVar(String scPtjVar) {
			this.scPtjVar = scPtjVar;
		}
		public String getScDeskOfficerVar() {
			return scDeskOfficerVar;
		}
		public void setScDeskOfficerVar(String scDeskOfficerVar) {
			this.scDeskOfficerVar = scDeskOfficerVar;
		}				
		public boolean isScPublishDateView() {
			return scPublishDateView;
		}
		public void setScPublishDateView(boolean scPublishDateView) {
			this.scPublishDateView = scPublishDateView;
		}
		public boolean isScCreatedFromView() {
			return scCreatedFromView;
		}
		public void setScCreatedFromView(boolean scCreatedFromView) {
			this.scCreatedFromView = scCreatedFromView;
		}
		public boolean isScStatusView() {
			return scStatusView;
		}
		public void setScStatusView(boolean scStatusView) {
			this.scStatusView = scStatusView;
		}
		public boolean isScPtjView() {
			return scPtjView;
		}
		public void setScPtjView(boolean scPtjView) {
			this.scPtjView = scPtjView;
		}
		public boolean isScDeskOfficerView() {
			return scDeskOfficerView;
		}
		public void setScDeskOfficerView(boolean scDeskOfficerView) {
			this.scDeskOfficerView = scDeskOfficerView;
		}
		public boolean isScRstrctdChkView() {
			return scRstrctdChkView;
		}
		public void setScRstrctdChkView(boolean scRstrctdChkView) {
			this.scRstrctdChkView = scRstrctdChkView;
		}
		public boolean isScTenderChkView() {
			return scTenderChkView;
		}
		public void setScTenderChkView(boolean scTenderChkView) {
			this.scTenderChkView = scTenderChkView;
		}
		public boolean isScQuotationChkView() {
			return scQuotationChkView;
		}
		public void setScQuotationChkView(boolean scQuotationChkView) {
			this.scQuotationChkView = scQuotationChkView;
		}
		public boolean isScProcureAllChkView() {
			return scProcureAllChkView;
		}
		public void setScProcureAllChkView(boolean scProcureAllChkView) {
			this.scProcureAllChkView = scProcureAllChkView;
		}
		public String getReportValueIdVar() {
			return reportValueIdVar;
		}
		public void setReportValueIdVar(String reportValueIdVar) {
			this.reportValueIdVar = reportValueIdVar;
		}
		public String getUserIdVar() {
			return userIdVar;
		}
		public void setUserIdVar(String userIdVar) {
			this.userIdVar = userIdVar;
		}
		public boolean isFlPurchaseOrderNoView() {
			return flPurchaseOrderNoView;
		}
		public void setFlPurchaseOrderNoView(boolean flPurchaseOrderNoView) {
			this.flPurchaseOrderNoView = flPurchaseOrderNoView;
		}
		public String getFlPurchaseOrderNoVar() {
			return flPurchaseOrderNoVar;
		}
		public void setFlPurchaseOrderNoVar(String flPurchaseOrderNoVar) {
			this.flPurchaseOrderNoVar = flPurchaseOrderNoVar;
		}
		public boolean isFlContractOrderView() {
			return flContractOrderView;
		}
		public void setFlContractOrderView(boolean flContractOrderView) {
			this.flContractOrderView = flContractOrderView;
		}
		public boolean isFlContractRequestView() {
			return flContractRequestView;
		}
		public void setFlContractRequestView(boolean flContractRequestView) {
			this.flContractRequestView = flContractRequestView;
		}
		public String getFlContractOrderVar() {
			return flContractOrderVar;
		}
		public void setFlContractOrderVar(String flContractOrderVar) {
			this.flContractOrderVar = flContractOrderVar;
		}
		public String getFlContractRequestVar() {
			return flContractRequestVar;
		}
		public void setFlContractRequestVar(String flContractRequestVar) {
			this.flContractRequestVar = flContractRequestVar;
		}
		public boolean isFlItemNameView() {
			return flItemNameView;
		}
		public void setFlItemNameView(boolean flItemNameView) {
			this.flItemNameView = flItemNameView;
		}
		public boolean isFlItemTypeView() {
			return flItemTypeView;
		}
		public void setFlItemTypeView(boolean flItemTypeView) {
			this.flItemTypeView = flItemTypeView;
		}
		
		public boolean isFlPtjView() {
			return flPtjView;
		}
		public void setFlPtjView(boolean flPtjView) {
			this.flPtjView = flPtjView;
		}
		public boolean isFlContractNoView() {
			return flContractNoView;
		}
		public void setFlContractNoView(boolean flContractNoView) {
			this.flContractNoView = flContractNoView;
		}
		public boolean isFlSstView() {
			return flSstView;
		}
		public void setFlSstView(boolean flSstView) {
			this.flSstView = flSstView;
		}
		public boolean isFlJabatanView() {
			return flJabatanView;
		}
		public void setFlJabatanView(boolean flJabatanView) {
			this.flJabatanView = flJabatanView;
		}
		public boolean isFlMinistryView() {
			return flMinistryView;
		}
		public void setFlMinistryView(boolean flMinistryView) {
			this.flMinistryView = flMinistryView;
		}
		public boolean isFlSupplierView() {
			return flSupplierView;
		}
		public void setFlSupplierView(boolean flSupplierView) {
			this.flSupplierView = flSupplierView;
		}
		public boolean isFlStatusView() {
			return flStatusView;
		}
		public void setFlStatusView(boolean flStatusView) {
			this.flStatusView = flStatusView;
		}
		public boolean isFlDurationView() {
			return flDurationView;
		}
		public void setFlDurationView(boolean flDurationView) {
			this.flDurationView = flDurationView;
		}
		public boolean isFlRecievingOfficerView() {
			return flRecievingOfficerView;
		}
		public void setFlRecievingOfficerView(boolean flRecievingOfficerView) {
			this.flRecievingOfficerView = flRecievingOfficerView;
		}
		public boolean isFlDeliveryAddressView() {
			return flDeliveryAddressView;
		}
		public void setFlDeliveryAddressView(boolean flDeliveryAddressView) {
			this.flDeliveryAddressView = flDeliveryAddressView;
		}
		public boolean isFlRecNoteNoView() {
			return flRecNoteNoView;
		}
		public void setFlRecNoteNoView(boolean flRecNoteNoView) {
			this.flRecNoteNoView = flRecNoteNoView;
		}
		public boolean isFlContractTypeView() {
			return flContractTypeView;
		}
		public void setFlContractTypeView(boolean flContractTypeView) {
			this.flContractTypeView = flContractTypeView;
		}
		public boolean isFlRequisitionerView() {
			return flRequisitionerView;
		}
		public void setFlRequisitionerView(boolean flRequisitionerView) {
			this.flRequisitionerView = flRequisitionerView;
		}
		public boolean isFlMtoContractView() {
			return flMtoContractView;
		}
		public void setFlMtoContractView(boolean flMtoContractView) {
			this.flMtoContractView = flMtoContractView;
		}
		public boolean isFlInvoiceNoView() {
			return flInvoiceNoView;
		}
		public void setFlInvoiceNoView(boolean flInvoiceNoView) {
			this.flInvoiceNoView = flInvoiceNoView;
		}
		public boolean isFlDeductionTypeView() {
			return flDeductionTypeView;
		}
		public void setFlDeductionTypeView(boolean flDeductionTypeView) {
			this.flDeductionTypeView = flDeductionTypeView;
		}
		public boolean isFlCrNoView() {
			return flCrNoView;
		}
		public void setFlCrNoView(boolean flCrNoView) {
			this.flCrNoView = flCrNoView;
		}
		public boolean isFlApproverNameView() {
			return flApproverNameView;
		}
		public void setFlApproverNameView(boolean flApproverNameView) {
			this.flApproverNameView = flApproverNameView;
		}
		public boolean isFlDocumentTypeView() {
			return flDocumentTypeView;
		}
		public void setFlDocumentTypeView(boolean flDocumentTypeView) {
			this.flDocumentTypeView = flDocumentTypeView;
		}
		public boolean isFlCriteriaView() {
			return flCriteriaView;
		}
		public void setFlCriteriaView(boolean flCriteriaView) {
			this.flCriteriaView = flCriteriaView;
		}
		public boolean isFlScoringView() {
			return flScoringView;
		}
		public void setFlScoringView(boolean flScoringView) {
			this.flScoringView = flScoringView;
		}
		public boolean isFlPkpNoView() {
			return flPkpNoView;
		}
		public void setFlPkpNoView(boolean flPkpNoView) {
			this.flPkpNoView = flPkpNoView;
		}
		public boolean isFlDeliveryOrderNoView() {
			return flDeliveryOrderNoView;
		}
		public void setFlDeliveryOrderNoView(boolean flDeliveryOrderNoView) {
			this.flDeliveryOrderNoView = flDeliveryOrderNoView;
		}
		public boolean isFlItemCodeView() {
			return flItemCodeView;
		}
		public void setFlItemCodeView(boolean flItemCodeView) {
			this.flItemCodeView = flItemCodeView;
		}
		public boolean isFlPoAmountView() {
			return flPoAmountView;
		}
		public void setFlPoAmountView(boolean flPoAmountView) {
			this.flPoAmountView = flPoAmountView;
		}
		public boolean isFlPrNoView() {
			return flPrNoView;
		}
		public void setFlPrNoView(boolean flPrNoView) {
			this.flPrNoView = flPrNoView;
		}
		public boolean isFlPmoView() {
			return flPmoView;
		}
		public void setFlPmoView(boolean flPmoView) {
			this.flPmoView = flPmoView;
		}
		public boolean isSmMofRegView() {
			return smMofRegView;
		}
		public void setSmMofRegView(boolean smMofRegView) {
			this.smMofRegView = smMofRegView;
		}
		public boolean isSmBasicRegView() {
			return smBasicRegView;
		}
		public void setSmBasicRegView(boolean smBasicRegView) {
			this.smBasicRegView = smBasicRegView;
		}
		public boolean isExtraTwoColumnView() {
			return extraTwoColumnView;
		}
		public void setExtraTwoColumnView(boolean extraTwoColumnView) {
			this.extraTwoColumnView = extraTwoColumnView;
		}
		public boolean isSmCategoryCodeView() {
			return smCategoryCodeView;
		}
		public void setSmCategoryCodeView(boolean smCategoryCodeView) {
			this.smCategoryCodeView = smCategoryCodeView;
		}
		public boolean isSmRegistrationTypeView() {
			return smRegistrationTypeView;
		}
		public void setSmRegistrationTypeView(boolean smRegistrationTypeView) {
			this.smRegistrationTypeView = smRegistrationTypeView;
		}
		public boolean isSmSupplierView() {
			return smSupplierView;
		}
		public void setSmSupplierView(boolean smSupplierView) {
			this.smSupplierView = smSupplierView;
		}
		
		public boolean isSmSupportingDocumentView() {
			return smSupportingDocumentView;
		}
		public void setSmSupportingDocumentView(boolean smSupportingDocumentView) {
			this.smSupportingDocumentView = smSupportingDocumentView;
		}
		public boolean isSmDocStatusView() {
			return smDocStatusView;
		}
		public void setSmDocStatusView(boolean smDocStatusView) {
			this.smDocStatusView = smDocStatusView;
		}
		public boolean isSmSuppDocStatusView() {
			return smSuppDocStatusView;
		}
		public void setSmSuppDocStatusView(boolean smSuppDocStatusView) {
			this.smSuppDocStatusView = smSuppDocStatusView;
		}
		public boolean isSmRegistrationNoView() {
			return smRegistrationNoView;
		}
		public void setSmRegistrationNoView(boolean smRegistrationNoView) {
			this.smRegistrationNoView = smRegistrationNoView;
		}
		public boolean isSmEpNoView() {
			return smEpNoView;
		}
		public void setSmEpNoView(boolean smEpNoView) {
			this.smEpNoView = smEpNoView;
		}
		public boolean isSmApplicationNoView() {
			return smApplicationNoView;
		}
		public void setSmApplicationNoView(boolean smApplicationNoView) {
			this.smApplicationNoView = smApplicationNoView;
		}
		public boolean isSmZoneView() {
			return smZoneView;
		}
		public void setSmZoneView(boolean smZoneView) {
			this.smZoneView = smZoneView;
		}
		public boolean isSmSupplierTypeView() {
			return smSupplierTypeView;
		}
		public void setSmSupplierTypeView(boolean smSupplierTypeView) {
			this.smSupplierTypeView = smSupplierTypeView;
		}
		public boolean isSmMofRegNoView() {
			return smMofRegNoView;
		}
		public void setSmMofRegNoView(boolean smMofRegNoView) {
			this.smMofRegNoView = smMofRegNoView;
		}
		public boolean isSmMofRegNoFromView() {
			return smMofRegNoFromView;
		}
		public void setSmMofRegNoFromView(boolean smMofRegNoFromView) {
			this.smMofRegNoFromView = smMofRegNoFromView;
		}
		public boolean isSmMofRegNoToView() {
			return smMofRegNoToView;
		}
		public void setSmMofRegNoToView(boolean smMofRegNoToView) {
			this.smMofRegNoToView = smMofRegNoToView;
		}
		public boolean isSmRegistrationStatusView() {
			return smRegistrationStatusView;
		}
		public void setSmRegistrationStatusView(boolean smRegistrationStatusView) {
			this.smRegistrationStatusView = smRegistrationStatusView;
		}
		public boolean isSmCapitalNaccumView() {
			return smCapitalNaccumView;
		}
		public void setSmCapitalNaccumView(boolean smCapitalNaccumView) {
			this.smCapitalNaccumView = smCapitalNaccumView;
		}
		public boolean isSmPaidUpCapitalNaccumShareView() {
			return smPaidUpCapitalNaccumShareView;
		}
		public void setSmPaidUpCapitalNaccumShareView(
				boolean smPaidUpCapitalNaccumShareView) {
			this.smPaidUpCapitalNaccumShareView = smPaidUpCapitalNaccumShareView;
		}
		public boolean isSmCategoryView() {
			return smCategoryView;
		}
		public void setSmCategoryView(boolean smCategoryView) {
			this.smCategoryView = smCategoryView;
		}
		public boolean isSmNoOfCategoryView() {
			return smNoOfCategoryView;
		}
		public void setSmNoOfCategoryView(boolean smNoOfCategoryView) {
			this.smNoOfCategoryView = smNoOfCategoryView;
		}
		public boolean isSmMainCategoryView() {
			return smMainCategoryView;
		}
		public void setSmMainCategoryView(boolean smMainCategoryView) {
			this.smMainCategoryView = smMainCategoryView;
		}
		public boolean isSmSubCategoryView() {
			return smSubCategoryView;
		}
		public void setSmSubCategoryView(boolean smSubCategoryView) {
			this.smSubCategoryView = smSubCategoryView;
		}
		public boolean isSmSubCategoryDivisionView() {
			return smSubCategoryDivisionView;
		}
		public void setSmSubCategoryDivisionView(boolean smSubCategoryDivisionView) {
			this.smSubCategoryDivisionView = smSubCategoryDivisionView;
		}
		public boolean isSmRoleView() {
			return smRoleView;
		}
		public void setSmRoleView(boolean smRoleView) {
			this.smRoleView = smRoleView;
		}
		public boolean isSmApplicationStatusView() {
			return smApplicationStatusView;
		}
		public void setSmApplicationStatusView(boolean smApplicationStatusView) {
			this.smApplicationStatusView = smApplicationStatusView;
		}
		public boolean isSmCancelledByView() {
			return smCancelledByView;
		}
		public void setSmCancelledByView(boolean smCancelledByView) {
			this.smCancelledByView = smCancelledByView;
		}
		public boolean isSmPaymentTypeView() {
			return smPaymentTypeView;
		}
		public void setSmPaymentTypeView(boolean smPaymentTypeView) {
			this.smPaymentTypeView = smPaymentTypeView;
		}
		public boolean isSmApplicationTypeView() {
			return smApplicationTypeView;
		}
		public void setSmApplicationTypeView(boolean smApplicationTypeView) {
			this.smApplicationTypeView = smApplicationTypeView;
		}
		public boolean isSmStateView() {
			return smStateView;
		}
		public void setSmStateView(boolean smStateView) {
			this.smStateView = smStateView;
		}
		public boolean isSmDistrictView() {
			return smDistrictView;
		}
		public void setSmDistrictView(boolean smDistrictView) {
			this.smDistrictView = smDistrictView;
		}
		public boolean isSmSupplierNameView() {
			return smSupplierNameView;
		}
		public void setSmSupplierNameView(boolean smSupplierNameView) {
			this.smSupplierNameView = smSupplierNameView;
		}
		public boolean isSmReportTypeView() {
			return smReportTypeView;
		}
		public void setSmReportTypeView(boolean smReportTypeView) {
			this.smReportTypeView = smReportTypeView;
		}
		public boolean isSmOrganizationTypeView() {
			return smOrganizationTypeView;
		}
		public void setSmOrganizationTypeView(boolean smOrganizationTypeView) {
			this.smOrganizationTypeView = smOrganizationTypeView;
		}
		public boolean isSmKementerianView() {
			return smKementerianView;
		}
		public void setSmKementerianView(boolean smKementerianView) {
			this.smKementerianView = smKementerianView;
		}
		public boolean isSmNamaAgensiView() {
			return smNamaAgensiView;
		}
		public void setSmNamaAgensiView(boolean smNamaAgensiView) {
			this.smNamaAgensiView = smNamaAgensiView;
		}
		public boolean isSmComplaintNoView() {
			return smComplaintNoView;
		}
		public void setSmComplaintNoView(boolean smComplaintNoView) {
			this.smComplaintNoView = smComplaintNoView;
		}
		public boolean isSmProofIdentityView() {
			return smProofIdentityView;
		}
		public void setSmProofIdentityView(boolean smProofIdentityView) {
			this.smProofIdentityView = smProofIdentityView;
		}
		public boolean isSmMofNoView() {
			return smMofNoView;
		}
		public void setSmMofNoView(boolean smMofNoView) {
			this.smMofNoView = smMofNoView;
		}
		public boolean isSmCompanyStatusView() {
			return smCompanyStatusView;
		}
		public void setSmCompanyStatusView(boolean smCompanyStatusView) {
			this.smCompanyStatusView = smCompanyStatusView;
		}
		public boolean isSmCaseNoView() {
			return smCaseNoView;
		}
		public void setSmCaseNoView(boolean smCaseNoView) {
			this.smCaseNoView = smCaseNoView;
		}
		public boolean isSmExtraTwoColumnView() {
			return smExtraTwoColumnView;
		}
		public void setSmExtraTwoColumnView(boolean smExtraTwoColumnView) {
			this.smExtraTwoColumnView = smExtraTwoColumnView;
		}
		public boolean isSmCompApplStatusView() {
			return smCompApplStatusView;
		}
		public void setSmCompApplStatusView(boolean smCompApplStatusView) {
			this.smCompApplStatusView = smCompApplStatusView;
		}
		public boolean isSmCompanyNameView() {
			return smCompanyNameView;
		}
		public void setSmCompanyNameView(boolean smCompanyNameView) {
			this.smCompanyNameView = smCompanyNameView;
		}
		public boolean isSmcategoryCodeView() {
			return smcategoryCodeView;
		}
		public void setSmcategoryCodeView(boolean smcategoryCodeView) {
			this.smcategoryCodeView = smcategoryCodeView;
		}
		public boolean isSmSiteVisitOfficerView() {
			return smSiteVisitOfficerView;
		}
		public void setSmSiteVisitOfficerView(boolean smSiteVisitOfficerView) {
			this.smSiteVisitOfficerView = smSiteVisitOfficerView;
		}
		public boolean isSmReleaseDateView() {
			return smReleaseDateView;
		}
		public void setSmReleaseDateView(boolean smReleaseDateView) {
			this.smReleaseDateView = smReleaseDateView;
		}
		public boolean isSmApplicationDateView() {
			return smApplicationDateView;
		}
		public void setSmApplicationDateView(boolean smApplicationDateView) {
			this.smApplicationDateView = smApplicationDateView;
		}
		public boolean isSmReceiveDateView() {
			return smReceiveDateView;
		}
		public void setSmReceiveDateView(boolean smReceiveDateView) {
			this.smReceiveDateView = smReceiveDateView;
		}
		public boolean isSmExpiryDateView() {
			return smExpiryDateView;
		}
		public void setSmExpiryDateView(boolean smExpiryDateView) {
			this.smExpiryDateView = smExpiryDateView;
		}
		public boolean isSmPrintedDateView() {
			return smPrintedDateView;
		}
		public void setSmPrintedDateView(boolean smPrintedDateView) {
			this.smPrintedDateView = smPrintedDateView;
		}
		public boolean isSmMandatoryZoneView() {
			return smMandatoryZoneView;
		}
		public void setSmMandatoryZoneView(boolean smMandatoryZoneView) {
			this.smMandatoryZoneView = smMandatoryZoneView;
		}
		public boolean isSmMandatorySupplierTypeView() {
			return smMandatorySupplierTypeView;
		}
		public void setSmMandatorySupplierTypeView(boolean smMandatorySupplierTypeView) {
			this.smMandatorySupplierTypeView = smMandatorySupplierTypeView;
		}
		public boolean isSmMandatoryApplicationDateView() {
			return smMandatoryApplicationDateView;
		}
		public void setSmMandatoryApplicationDateView(
				boolean smMandatoryApplicationDateView) {
			this.smMandatoryApplicationDateView = smMandatoryApplicationDateView;
		}
		public boolean isSmMandatoryApplicationStatusView() {
			return smMandatoryApplicationStatusView;
		}
		public void setSmMandatoryApplicationStatusView(
				boolean smMandatoryApplicationStatusView) {
			this.smMandatoryApplicationStatusView = smMandatoryApplicationStatusView;
		}
		public boolean isSmMandatoryMofRegNoView() {
			return smMandatoryMofRegNoView;
		}
		public void setSmMandatoryMofRegNoView(boolean smMandatoryMofRegNoView) {
			this.smMandatoryMofRegNoView = smMandatoryMofRegNoView;
		}
		public boolean isSmMandatoryExpiryDateView() {
			return smMandatoryExpiryDateView;
		}
		public void setSmMandatoryExpiryDateView(boolean smMandatoryExpiryDateView) {
			this.smMandatoryExpiryDateView = smMandatoryExpiryDateView;
		}
		public boolean isSmMandatoryPrintedDateView() {
			return smMandatoryPrintedDateView;
		}
		public void setSmMandatoryPrintedDateView(boolean smMandatoryPrintedDateView) {
			this.smMandatoryPrintedDateView = smMandatoryPrintedDateView;
		}
		public boolean isCmBasicRegView() {
			return cmBasicRegView;
		}
		public void setCmBasicRegView(boolean cmBasicRegView) {
			this.cmBasicRegView = cmBasicRegView;
		}
		public boolean isCmMofRegView() {
			return cmMofRegView;
		}
		public void setCmMofRegView(boolean cmMofRegView) {
			this.cmMofRegView = cmMofRegView;
		}
		public boolean isCmProofIdentityView() {
			return cmProofIdentityView;
		}
		public void setCmProofIdentityView(boolean cmProofIdentityView) {
			this.cmProofIdentityView = cmProofIdentityView;
		}
		public boolean isCmItemTypeView() {
			return cmItemTypeView;
		}
		public void setCmItemTypeView(boolean cmItemTypeView) {
			this.cmItemTypeView = cmItemTypeView;
		}
		public boolean isCmItemStatusView() {
			return cmItemStatusView;
		}
		public void setCmItemStatusView(boolean cmItemStatusView) {
			this.cmItemStatusView = cmItemStatusView;
		}
		public boolean isCmUnspscCodeView() {
			return cmUnspscCodeView;
		}
		public void setCmUnspscCodeView(boolean cmUnspscCodeView) {
			this.cmUnspscCodeView = cmUnspscCodeView;
		}
		public boolean isCmUnspscTitleView() {
			return cmUnspscTitleView;
		}
		public void setCmUnspscTitleView(boolean cmUnspscTitleView) {
			this.cmUnspscTitleView = cmUnspscTitleView;
		}
		public boolean isCmCompanyNameView() {
			return cmCompanyNameView;
		}
		public void setCmCompanyNameView(boolean cmCompanyNameView) {
			this.cmCompanyNameView = cmCompanyNameView;
		}
		public boolean isCmRegistrationTypeView() {
			return cmRegistrationTypeView;
		}
		public void setCmRegistrationTypeView(boolean cmRegistrationTypeView) {
			this.cmRegistrationTypeView = cmRegistrationTypeView;
		}
		public boolean isCmItemCategoryView() {
			return cmItemCategoryView;
		}
		public void setCmItemCategoryView(boolean cmItemCategoryView) {
			this.cmItemCategoryView = cmItemCategoryView;
		}
		public boolean isCmItemNameView() {
			return cmItemNameView;
		}
		public void setCmItemNameView(boolean cmItemNameView) {
			this.cmItemNameView = cmItemNameView;
		}
		public boolean isCmEpNumberView() {
			return cmEpNumberView;
		}
		public void setCmEpNumberView(boolean cmEpNumberView) {
			this.cmEpNumberView = cmEpNumberView;
		}
		public boolean isCmBusinessRegNoView() {
			return cmBusinessRegNoView;
		}
		public void setCmBusinessRegNoView(boolean cmBusinessRegNoView) {
			this.cmBusinessRegNoView = cmBusinessRegNoView;
		}
		public boolean isCmIcNumberView() {
			return cmIcNumberView;
		}
		public void setCmIcNumberView(boolean cmIcNumberView) {
			this.cmIcNumberView = cmIcNumberView;
		}
		public boolean isCmMofNumberView() {
			return cmMofNumberView;
		}
		public void setCmMofNumberView(boolean cmMofNumberView) {
			this.cmMofNumberView = cmMofNumberView;
		}
		public boolean isCmExtentionCodeView() {
			return cmExtentionCodeView;
		}
		public void setCmExtentionCodeView(boolean cmExtentionCodeView) {
			this.cmExtentionCodeView = cmExtentionCodeView;
		}
		public boolean isCmExtentionTypeView() {
			return cmExtentionTypeView;
		}
		public void setCmExtentionTypeView(boolean cmExtentionTypeView) {
			this.cmExtentionTypeView = cmExtentionTypeView;
		}
		public boolean isCmExtentionBrandView() {
			return cmExtentionBrandView;
		}
		public void setCmExtentionBrandView(boolean cmExtentionBrandView) {
			this.cmExtentionBrandView = cmExtentionBrandView;
		}
		public boolean isCmExtentionMeasurmentView() {
			return cmExtentionMeasurmentView;
		}
		public void setCmExtentionMeasurmentView(boolean cmExtentionMeasurmentView) {
			this.cmExtentionMeasurmentView = cmExtentionMeasurmentView;
		}
		public boolean isCmExtentionColourView() {
			return cmExtentionColourView;
		}
		public void setCmExtentionColourView(boolean cmExtentionColourView) {
			this.cmExtentionColourView = cmExtentionColourView;
		}	
		public boolean isScQuotationNoView() {
			return scQuotationNoView;
		}
		public void setScQuotationNoView(boolean scQuotationNoView) {
			this.scQuotationNoView = scQuotationNoView;
		}	
		
		public String getSupportingDocumentTotVar() {
			return supportingDocumentTotVar;
		}
		public void setSupportingDocumentTotVar(String supportingDocumentTotVar) {
			this.supportingDocumentTotVar = supportingDocumentTotVar;
		}
		public String getDocStatusTotVar() {
			return docStatusTotVar;
		}
		public void setDocStatusTotVar(String docStatusTotVar) {
			this.docStatusTotVar = docStatusTotVar;
		}
		
		public String getSuppDocStatusTotVar() {
			return suppDocStatusTotVar;
		}
		public void setSuppDocStatusTotVar(String suppDocStatusTotVar) {
			this.suppDocStatusTotVar = suppDocStatusTotVar;
		}
		public String getRegistrationNoTotVar() {
			return registrationNoTotVar;
		}
		public void setRegistrationNoTotVar(String registrationNoTotVar) {
			this.registrationNoTotVar = registrationNoTotVar;
		}
		public String getePNoTotVar() {
			return ePNoTotVar;
		}
		public void setePNoTotVar(String ePNoTotVar) {
			this.ePNoTotVar = ePNoTotVar;
		}
		public String getApplicationNoTotVar() {
			return applicationNoTotVar;
		}
		public void setApplicationNoTotVar(String applicationNoTotVar) {
			this.applicationNoTotVar = applicationNoTotVar;
		}
		public String getZoneTotVar() {
			return zoneTotVar;
		}
		public void setZoneTotVar(String zoneTotVar) {
			this.zoneTotVar = zoneTotVar;
		}
		public String getSupplierTypeTotVar() {
			return supplierTypeTotVar;
		}
		public void setSupplierTypeTotVar(String supplierTypeTotVar) {
			this.supplierTypeTotVar = supplierTypeTotVar;
		}
		public String getMofRegNoTotVar() {
			return mofRegNoTotVar;
		}
		public void setMofRegNoTotVar(String mofRegNoTotVar) {
			this.mofRegNoTotVar = mofRegNoTotVar;
		}
		public String getMofRegNoFromTotVar() {
			return mofRegNoFromTotVar;
		}
		public void setMofRegNoFromTotVar(String mofRegNoFromTotVar) {
			this.mofRegNoFromTotVar = mofRegNoFromTotVar;
		}
		public String getMofRegNoToTotVar() {
			return mofRegNoToTotVar;
		}
		public void setMofRegNoToTotVar(String mofRegNoToTotVar) {
			this.mofRegNoToTotVar = mofRegNoToTotVar;
		}
		public String getRegistrationStatusTotVar() {
			return registrationStatusTotVar;
		}
		public void setRegistrationStatusTotVar(String registrationStatusTotVar) {
			this.registrationStatusTotVar = registrationStatusTotVar;
		}
		public String getCapitalNaccumFrmTotVar() {
			return capitalNaccumFrmTotVar;
		}
		public void setCapitalNaccumFrmTotVar(String capitalNaccumFrmTotVar) {
			this.capitalNaccumFrmTotVar = capitalNaccumFrmTotVar;
		}
		public String getCapitalNaccumToTotVar() {
			return capitalNaccumToTotVar;
		}
		public void setCapitalNaccumToTotVar(String capitalNaccumToTotVar) {
			this.capitalNaccumToTotVar = capitalNaccumToTotVar;
		}
		public String getPaidUpCapitalNaccumShareTotVar() {
			return paidUpCapitalNaccumShareTotVar;
		}
		public void setPaidUpCapitalNaccumShareTotVar(
				String paidUpCapitalNaccumShareTotVar) {
			this.paidUpCapitalNaccumShareTotVar = paidUpCapitalNaccumShareTotVar;
		}
		public String getCategoryTotVar() {
			return categoryTotVar;
		}
		public void setCategoryTotVar(String categoryTotVar) {
			this.categoryTotVar = categoryTotVar;
		}
		public String getNoOfCategoryTotVar() {
			return noOfCategoryTotVar;
		}
		public void setNoOfCategoryTotVar(String noOfCategoryTotVar) {
			this.noOfCategoryTotVar = noOfCategoryTotVar;
		}
		public String getMainCategoryTotVar() {
			return mainCategoryTotVar;
		}
		public void setMainCategoryTotVar(String mainCategoryTotVar) {
			this.mainCategoryTotVar = mainCategoryTotVar;
		}
		public String getSubCategoryTotVar() {
			return subCategoryTotVar;
		}
		public void setSubCategoryTotVar(String subCategoryTotVar) {
			this.subCategoryTotVar = subCategoryTotVar;
		}
		public String getSubCategoryDivisionTotVar() {
			return subCategoryDivisionTotVar;
		}
		public void setSubCategoryDivisionTotVar(String subCategoryDivisionTotVar) {
			this.subCategoryDivisionTotVar = subCategoryDivisionTotVar;
		}
		public String getRoleTotVar() {
			return roleTotVar;
		}
		public void setRoleTotVar(String roleTotVar) {
			this.roleTotVar = roleTotVar;
		}
		public String getApplicationStatusTotVar() {
			return applicationStatusTotVar;
		}
		public void setApplicationStatusTotVar(String applicationStatusTotVar) {
			this.applicationStatusTotVar = applicationStatusTotVar;
		}
		public String getCancelledByTotVar() {
			return cancelledByTotVar;
		}
		public void setCancelledByTotVar(String cancelledByTotVar) {
			this.cancelledByTotVar = cancelledByTotVar;
		}
		public String getPaymentTypeTotVar() {
			return paymentTypeTotVar;
		}
		public void setPaymentTypeTotVar(String paymentTypeTotVar) {
			this.paymentTypeTotVar = paymentTypeTotVar;
		}
		public String getApplicationTypeTotVar() {
			return applicationTypeTotVar;
		}
		public void setApplicationTypeTotVar(String applicationTypeTotVar) {
			this.applicationTypeTotVar = applicationTypeTotVar;
		}
		public String getStateTotVar() {
			return stateTotVar;
		}
		public void setStateTotVar(String stateTotVar) {
			this.stateTotVar = stateTotVar;
		}
		public String getDistrictTotVar() {
			return districtTotVar;
		}
		public void setDistrictTotVar(String districtTotVar) {
			this.districtTotVar = districtTotVar;
		}
		public String getSupplierNameTotVar() {
			return supplierNameTotVar;
		}
		public void setSupplierNameTotVar(String supplierNameTotVar) {
			this.supplierNameTotVar = supplierNameTotVar;
		}
		public String getReportTypeTotVar() {
			return reportTypeTotVar;
		}
		public void setReportTypeTotVar(String reportTypeTotVar) {
			this.reportTypeTotVar = reportTypeTotVar;
		}
		public String getOrganizationTypeTotVar() {
			return organizationTypeTotVar;
		}
		public void setOrganizationTypeTotVar(String organizationTypeTotVar) {
			this.organizationTypeTotVar = organizationTypeTotVar;
		}
		public String getKementerianTotVar() {
			return kementerianTotVar;
		}
		public void setKementerianTotVar(String kementerianTotVar) {
			this.kementerianTotVar = kementerianTotVar;
		}
		public String getNamAgensiTotVar() {
			return namAgensiTotVar;
		}
		public void setNamAgensiTotVar(String namAgensiTotVar) {
			this.namAgensiTotVar = namAgensiTotVar;
		}
		public String getComplaintNoTotVar() {
			return complaintNoTotVar;
		}
		public void setComplaintNoTotVar(String complaintNoTotVar) {
			this.complaintNoTotVar = complaintNoTotVar;
		}
		public String getCompanyStatusTotVar() {
			return companyStatusTotVar;
		}
		public void setCompanyStatusTotVar(String companyStatusTotVar) {
			this.companyStatusTotVar = companyStatusTotVar;
		}
		public String getCaseNoTotVar() {
			return caseNoTotVar;
		}
		public void setCaseNoTotVar(String caseNoTotVar) {
			this.caseNoTotVar = caseNoTotVar;
		}
		public String getCompApplStatusTotVar() {
			return compApplStatusTotVar;
		}
		public void setCompApplStatusTotVar(String compApplStatusTotVar) {
			this.compApplStatusTotVar = compApplStatusTotVar;
		}
		public String getCategoryCodeTotVar() {
			return categoryCodeTotVar;
		}
		public void setCategoryCodeTotVar(String categoryCodeTotVar) {
			this.categoryCodeTotVar = categoryCodeTotVar;
		}
		public String getSiteVisitOfficerTotVar() {
			return siteVisitOfficerTotVar;
		}
		public void setSiteVisitOfficerTotVar(String siteVisitOfficerTotVar) {
			this.siteVisitOfficerTotVar = siteVisitOfficerTotVar;
		}
		public String getReleaseFromDateTotVar() {
			return releaseFromDateTotVar;
		}
		public void setReleaseFromDateTotVar(String releaseFromDateTotVar) {
			this.releaseFromDateTotVar = releaseFromDateTotVar;
		}
		public String getReleaseToDateTotVar() {
			return releaseToDateTotVar;
		}
		public void setReleaseToDateTotVar(String releaseToDateTotVar) {
			this.releaseToDateTotVar = releaseToDateTotVar;
		}
		public String getApplicationFromDateTotVar() {
			return applicationFromDateTotVar;
		}
		public void setApplicationFromDateTotVar(String applicationFromDateTotVar) {
			this.applicationFromDateTotVar = applicationFromDateTotVar;
		}
		public String getApplicationToDateTotVar() {
			return applicationToDateTotVar;
		}
		public void setApplicationToDateTotVar(String applicationToDateTotVar) {
			this.applicationToDateTotVar = applicationToDateTotVar;
		}
		public String getReceiveFromDateTotVar() {
			return receiveFromDateTotVar;
		}
		public void setReceiveFromDateTotVar(String receiveFromDateTotVar) {
			this.receiveFromDateTotVar = receiveFromDateTotVar;
		}
		public String getReceiveToDateTotVar() {
			return receiveToDateTotVar;
		}
		public void setReceiveToDateTotVar(String receiveToDateTotVar) {
			this.receiveToDateTotVar = receiveToDateTotVar;
		}
		public String getExpiryFromDateTotVar() {
			return expiryFromDateTotVar;
		}
		public void setExpiryFromDateTotVar(String expiryFromDateTotVar) {
			this.expiryFromDateTotVar = expiryFromDateTotVar;
		}
		public String getExpiryToDateTotVar() {
			return expiryToDateTotVar;
		}
		public void setExpiryToDateTotVar(String expiryToDateTotVar) {
			this.expiryToDateTotVar = expiryToDateTotVar;
		}
		public String getPrintedFromDateTotVar() {
			return printedFromDateTotVar;
		}
		public void setPrintedFromDateTotVar(String printedFromDateTotVar) {
			this.printedFromDateTotVar = printedFromDateTotVar;
		}
		public String getPrintedToDateTotVar() {
			return printedToDateTotVar;
		}
		public void setPrintedToDateTotVar(String printedToDateTotVar) {
			this.printedToDateTotVar = printedToDateTotVar;
		}
		
		
		public String getEpNumberTotVar() {
			return epNumberTotVar;
		}
		public void setEpNumberTotVar(String epNumberTotVar) {
			this.epNumberTotVar = epNumberTotVar;
		}
		public String getBusinessRegNoTotVar() {
			return businessRegNoTotVar;
		}
		public void setBusinessRegNoTotVar(String businessRegNoTotVar) {
			this.businessRegNoTotVar = businessRegNoTotVar;
		}
		public String getIcNumberTotVar() {
			return icNumberTotVar;
		}
		public void setIcNumberTotVar(String icNumberTotVar) {
			this.icNumberTotVar = icNumberTotVar;
		}
		public String getMofNumberTotVar() {
			return mofNumberTotVar;
		}
		public void setMofNumberTotVar(String mofNumberTotVar) {
			this.mofNumberTotVar = mofNumberTotVar;
		}
		
		public String getExtentionTypeTotVar() {
			return extentionTypeTotVar;
		}
		public void setExtentionTypeTotVar(String extentionTypeTotVar) {
			this.extentionTypeTotVar = extentionTypeTotVar;
		}
		public String getExtentionBrandTotVar() {
			return extentionBrandTotVar;
		}
		public void setExtentionBrandTotVar(String extentionBrandTotVar) {
			this.extentionBrandTotVar = extentionBrandTotVar;
		}
		public String getExtentionMeasurmentTotVar() {
			return extentionMeasurmentTotVar;
		}
		public void setExtentionMeasurmentTotVar(String extentionMeasurmentTotVar) {
			this.extentionMeasurmentTotVar = extentionMeasurmentTotVar;
		}
		public String getExtentionColourTotVar() {
			return extentionColourTotVar;
		}
		public void setExtentionColourTotVar(String extentionColourTotVar) {
			this.extentionColourTotVar = extentionColourTotVar;
		}
		public String getFromDateVar() {
			return fromDateVar;
		}
		public void setFromDateVar(String fromDateVar) {
			this.fromDateVar = fromDateVar;
		}
		public String getToDateVar() {
			return toDateVar;
		}
		public void setToDateVar(String toDateVar) {
			this.toDateVar = toDateVar;
		}
		private String urlReportJSPName=ReportConst.EMPTY_VAL;
		//URL variables end
		
		public String getUrlReportJSPName() {
			return urlReportJSPName;
		}
		public void setUrlReportJSPName(String urlReportJSPName) {
			this.urlReportJSPName = urlReportJSPName;
		}
		public String getQuotationIdTotVar() {
			return quotationIdTotVar;
		}
		public void setQuotationIdTotVar(String quotationIdTotVar) {
			this.quotationIdTotVar = quotationIdTotVar;
		}
		public String getItemTypeTotVar() {
			return itemTypeTotVar;
		}
		public void setItemTypeTotVar(String itemTypeTotVar) {
			this.itemTypeTotVar = itemTypeTotVar;
		}
		public String getItemStatusTotVar() {
			return itemStatusTotVar;
		}
		public void setItemStatusTotVar(String itemStatusTotVar) {
			this.itemStatusTotVar = itemStatusTotVar;
		}
		public String getUnspscCodeTotVar() {
			return unspscCodeTotVar;
		}
		public void setUnspscCodeTotVar(String unspscCodeTotVar) {
			this.unspscCodeTotVar = unspscCodeTotVar;
		}
		public String getUnspscTitleTotVar() {
			return unspscTitleTotVar;
		}
		public void setUnspscTitleTotVar(String unspscTitleTotVar) {
			this.unspscTitleTotVar = unspscTitleTotVar;
		}
		public String getExtentionCodeTotVar() {
			return extentionCodeTotVar;
		}
		public void setExtentionCodeTotVar(String extentionCodeTotVar) {
			this.extentionCodeTotVar = extentionCodeTotVar;
		}
		public String getCompanyNameTotVar() {
			return companyNameTotVar;
		}
		public void setCompanyNameTotVar(String companyNameTotVar) {
			this.companyNameTotVar = companyNameTotVar;
		}
		public String getRegistrationTypeTotVar() {
			return registrationTypeTotVar;
		}
		public void setRegistrationTypeTotVar(String registrationTypeTotVar) {
			this.registrationTypeTotVar = registrationTypeTotVar;
		}
		public String getItemCategoryTotVar() {
			return itemCategoryTotVar;
		}
		public void setItemCategoryTotVar(String itemCategoryTotVar) {
			this.itemCategoryTotVar = itemCategoryTotVar;
		}
		public String getItemNameTotVar() {
			return itemNameTotVar;
		}
		public void setItemNameTotVar(String itemNameTotVar) {
			this.itemNameTotVar = itemNameTotVar;
		}
		public String getProofIdentityTotVar() {
			return proofIdentityTotVar;
		}
		public void setProofIdentityTotVar(String proofIdentityTotVar) {
			this.proofIdentityTotVar = proofIdentityTotVar;
		}
		public String getPtjTotVar() {
			return ptjTotVar;
		}
		public void setPtjTotVar(String ptjTotVar) {
			this.ptjTotVar = ptjTotVar;
		}
		public String getContractNoTotVar() {
			return contractNoTotVar;
		}
		public void setContractNoTotVar(String contractNoTotVar) {
			this.contractNoTotVar = contractNoTotVar;
		}
		public String getSstTotVar() {
			return sstTotVar;
		}
		public void setSstTotVar(String sstTotVar) {
			this.sstTotVar = sstTotVar;
		}
		public String getJabatanTotVar() {
			return jabatanTotVar;
		}
		public void setJabatanTotVar(String jabatanTotVar) {
			this.jabatanTotVar = jabatanTotVar;
		}
		public String getMinistryTotVar() {
			return ministryTotVar;
		}
		public void setMinistryTotVar(String ministryTotVar) {
			this.ministryTotVar = ministryTotVar;
		}
		public String getSupplierTotVar() {
			return supplierTotVar;
		}
		public void setSupplierTotVar(String supplierTotVar) {
			this.supplierTotVar = supplierTotVar;
		}
		public String getStatusTotVar() {
			return statusTotVar;
		}
		public void setStatusTotVar(String statusTotVar) {
			this.statusTotVar = statusTotVar;
		}
		public String getDurationTotVar() {
			return durationTotVar;
		}
		public void setDurationTotVar(String durationTotVar) {
			this.durationTotVar = durationTotVar;
		}
		public String getRecievingOfficerTotVar() {
			return recievingOfficerTotVar;
		}
		public void setRecievingOfficerTotVar(String recievingOfficerTotVar) {
			this.recievingOfficerTotVar = recievingOfficerTotVar;
		}
		public String getDeliveryAddressTotVar() {
			return deliveryAddressTotVar;
		}
		public void setDeliveryAddressTotVar(String deliveryAddressTotVar) {
			this.deliveryAddressTotVar = deliveryAddressTotVar;
		}
		public String getFlRecNoteNoTotVar() {
			return flRecNoteNoTotVar;
		}
		public void setFlRecNoteNoTotVar(String flRecNoteNoTotVar) {
			this.flRecNoteNoTotVar = flRecNoteNoTotVar;
		}
		public String getContractTypeTotVar() {
			return contractTypeTotVar;
		}
		public void setContractTypeTotVar(String contractTypeTotVar) {
			this.contractTypeTotVar = contractTypeTotVar;
		}
		public String getRequisitionerTotVar() {
			return requisitionerTotVar;
		}
		public void setRequisitionerTotVar(String requisitionerTotVar) {
			this.requisitionerTotVar = requisitionerTotVar;
		}
		public String getMtoContractTotVar() {
			return mtoContractTotVar;
		}
		public void setMtoContractTotVar(String mtoContractTotVar) {
			this.mtoContractTotVar = mtoContractTotVar;
		}
		public String getInvoiceNoTotVar() {
			return invoiceNoTotVar;
		}
		public void setInvoiceNoTotVar(String invoiceNoTotVar) {
			this.invoiceNoTotVar = invoiceNoTotVar;
		}
		public String getDeductionTypeTotVar() {
			return deductionTypeTotVar;
		}
		public void setDeductionTypeTotVar(String deductionTypeTotVar) {
			this.deductionTypeTotVar = deductionTypeTotVar;
		}
		public String getCrNoTotVar() {
			return crNoTotVar;
		}
		public void setCrNoTotVar(String crNoTotVar) {
			this.crNoTotVar = crNoTotVar;
		}
		public String getApproverNameTotVar() {
			return approverNameTotVar;
		}
		public void setApproverNameTotVar(String approverNameTotVar) {
			this.approverNameTotVar = approverNameTotVar;
		}
		public String getDocumentTypeTotVar() {
			return documentTypeTotVar;
		}
		public void setDocumentTypeTotVar(String documentTypeTotVar) {
			this.documentTypeTotVar = documentTypeTotVar;
		}
		public String getCriteriaTotVar() {
			return criteriaTotVar;
		}
		public void setCriteriaTotVar(String criteriaTotVar) {
			this.criteriaTotVar = criteriaTotVar;
		}
		public String getScoringTotVar() {
			return scoringTotVar;
		}
		public void setScoringTotVar(String scoringTotVar) {
			this.scoringTotVar = scoringTotVar;
		}
		public String getPkpNoTotVar() {
			return pkpNoTotVar;
		}
		public void setPkpNoTotVar(String pkpNoTotVar) {
			this.pkpNoTotVar = pkpNoTotVar;
		}
		public String getDeliveryOrderNoTotVar() {
			return deliveryOrderNoTotVar;
		}
		public void setDeliveryOrderNoTotVar(String deliveryOrderNoTotVar) {
			this.deliveryOrderNoTotVar = deliveryOrderNoTotVar;
		}
		public String getItemCodeTotVar() {
			return itemCodeTotVar;
		}
		public void setItemCodeTotVar(String itemCodeTotVar) {
			this.itemCodeTotVar = itemCodeTotVar;
		}
		public String getPoAmountTotVar() {
			return poAmountTotVar;
		}
		public void setPoAmountTotVar(String poAmountTotVar) {
			this.poAmountTotVar = poAmountTotVar;
		}
		public String getPrNoTotVar() {
			return prNoTotVar;
		}
		public void setPrNoTotVar(String prNoTotVar) {
			this.prNoTotVar = prNoTotVar;
		}
		public String getPmoTotVar() {
			return pmoTotVar;
		}
		public void setPmoTotVar(String pmoTotVar) {
			this.pmoTotVar = pmoTotVar;
		}	
		
		public boolean isFromDateView() {
			return fromDateView;
		}
		public void setFromDateView(boolean fromDateView) {
			this.fromDateView = fromDateView;
		}
		public boolean isToDateView() {
			return toDateView;
		}
		public void setToDateView(boolean toDateView) {
			this.toDateView = toDateView;
		}
		public List<SelectItem> getStateLst() {
			return stateLst;
		}
		public void setStateLst(List<SelectItem> stateLst) {
			this.stateLst = stateLst;
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
		
		/*public List<String> getStatusLst() {
			return statusLst;
		}
		public void setStatusLst(List<String> statusLst) {
			this.statusLst = statusLst;
		}*/
		
		public void retrieveState(){
			stateLst = new ArrayList<SelectItem>();		
			IStateSO stateSO	=	(IStateSO)BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);		
			StateReqDTO	stateReqDTO	=	new StateReqDTO();
			stateReqDTO.setCountryId(1L);//TODO malaysia		
			StateResDTO resStateDTO	=	stateSO.retriveStatesListByCountryID(stateReqDTO);		
			for(PmState state: resStateDTO.getStateList()){			
				SelectItem selectItem= new SelectItem();
				selectItem.setLabel(state.getStateName());
				selectItem.setValue(state.getStateId());
				stateLst.add(selectItem);			
			}
		}
		public List<String> getStsLst() {
			return stsLst;
		}
		public void setStsLst(List<String> stsLst) {
			this.stsLst = stsLst;
		}
		public Object[] getStatusLst() {
			return statusLst;
		}
		public void setStatusLst(Object[] statusLst) {
			this.statusLst = statusLst;
		}
		public void retrieveDistrict(String state){		
			districtLst = new ArrayList<SelectItem>();		
			IDistrictSO	districtSO	=	(IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
			DistrictReqDTO	districtReqDTO	=	new DistrictReqDTO();
			if(state!=null&&state!=""){			
				districtReqDTO.setStateId( Long.parseLong(state));
				DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);			
				for(PmDistrict pmDistrict: districtResDTO.getDistrictList()){				
					SelectItem selectItem= new SelectItem();
					selectItem.setLabel(pmDistrict.getDistrictName());
					selectItem.setValue(pmDistrict.getDistrictCode());
					districtLst.add(selectItem);
				}
			}
		}
		public void retrieveCategoryLevOne(){
			levOneCatLst = new ArrayList<SelectItem>();		
			ICategoryCodeSO categoryCodeSO	=	(ICategoryCodeSO)BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
			//CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
			//CategoryCodeResDTO	catCodeResDTO	=	new CategoryCodeResDTO();
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL1List();		
			for(PmCategoryL1 catOne: catCodeResDTO.getCategoryL1List()){			
				SelectItem selectItem= new SelectItem();
				selectItem.setLabel(catOne.getCategoryName());
				selectItem.setValue(catOne.getCategoryL1Id());
				levOneCatLst.add(selectItem);			
			}
		}
		public void retrieveCategoryLevTwo(String mainCategory){
			levTwoCatLst = new ArrayList<SelectItem>();		
			ICategoryCodeSO categoryCodeSO	=	(ICategoryCodeSO)BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
			CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
			if(mainCategory!=null&&mainCategory!=""){	
				catCodeReqDTO.setCategoryL1Id(Long.parseLong(mainCategory));
				CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL2ListByL1Id(catCodeReqDTO);
				for(PmCategoryL2 catTwo: catCodeResDTO.getCategoryL2List()){			
					SelectItem selectItem= new SelectItem();
					selectItem.setLabel(catTwo.getCategoryName());
					selectItem.setValue(catTwo.getCategoryL2Id());
					levTwoCatLst.add(selectItem);			
				}
			}
		}
		public void retrieveCategoryLevThree(String subCategory){
			levThreeCatLst = new ArrayList<SelectItem>();		
			ICategoryCodeSO categoryCodeSO	=	(ICategoryCodeSO)BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
			CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
			if(subCategory!=null&&subCategory!=""){	
				catCodeReqDTO.setCategoryL2Id(Long.parseLong(subCategory));
				CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL3ListByL2Id(catCodeReqDTO);
				for(PmCategoryL3 catThree: catCodeResDTO.getCategoryL3List()){			
					SelectItem selectItem= new SelectItem();
					selectItem.setLabel(catThree.getCategoryName());
					selectItem.setValue(catThree.getCategoryL3Id());
					levThreeCatLst.add(selectItem);			
				}
			}
		}
		public List<String> retriveStatus(){					
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
			StatusResDTO statusResDTO= reportSO.retriveStatus();
			List<PmStatusDesc> stasLst = statusResDTO.getStatusDescList();
			if (null != stasLst) {
				statusLst=stasLst.toArray();
				/*for (int i = 0; i < stasLst.size(); i++) {					
					stsLst.add(stasLst.get(i)+"");				
				}*/
			}		
			return stsLst;
		}
}
