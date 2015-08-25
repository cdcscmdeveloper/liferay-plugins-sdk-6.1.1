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

/**
 * @Author Jangari Kondal Reddy
 */
public class ReportsGenerateBean {
	private static final Logger log = EpLogger.getLogger(ReportsGenerateBean.class);
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();	
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();
	
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();	
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();
	private List<String> stsLst = new ArrayList<String>();
	private List<String> yearList = new ArrayList<String>();
	private List<SelectItem> monthList = new ArrayList<SelectItem>();
	private HashMap<String, String> monthMap= new HashMap<String, String>();
	private Object[] statusLst;
	private boolean fromDateView = false;
	private boolean toDateView = false;
	private boolean approvedDateView = false;
	private long lngUserId;
	private List<SelectItem> levOneCatLst1 = new ArrayList<SelectItem>();
	private boolean madatoryIndicatorView = false;
	private boolean cmMadatoryIndicatorView= false;
	private boolean enableCriteriaMenuView = false;
	private boolean reportCategoryView = false;
	private boolean generateButtonView = false;
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
		private boolean scMandatoryProcessRptQuotationNoView = false;
		private boolean scCommonStatusView = false;
		private boolean scLAPRNPURCHDEFAULTGroupView = false;
		private boolean scREQUESTNOTESUMMARYGroupView = false;
		private boolean scPIDNoView = false;
		private boolean scContractOrPKPTypeView = false;
		private boolean scContractOrPKPNoView = false;
		private boolean scContractOrPKPNameView = false;
		private boolean scRequestQuoteNoView = false;
		private boolean scRequestNoteStatusView = false;
		private boolean scSimpleQuoteStatusView = false;
		private boolean scSimpleQuoteDtlGroupView = false;
		private boolean scPurchaseInDefaultStatusView = false;
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
		private boolean cmSupplOrAdminView = false;
		private boolean cmExtentionView = false;
		private boolean cmGroupOneView = false;
		
		//SM module
		private boolean smCompanyNameView = false;
		private boolean smApplicationNoView = false;
		private boolean smZoneView = false;
		private boolean smZoneLabelView = false;
		private boolean smSupplierTypeView = false;
		private boolean smMofRegNoView = false;
		private boolean smCategoryView = false;
		private boolean smNoOfCategoryView = false;
		private boolean smMainCategoryView = false;
		private boolean smSubCategoryView = false;
		private boolean smSubCategoryDivisionView = false;
		private boolean smRegistrationStatusView = false;
		private boolean smConsultantRegistrationStatusView = false;
		private boolean smPaymentTypeView = false;
		private boolean smPaymentTypeTwoView = false;
		private boolean smMandatoryPaymentTypeView = false;
		private boolean smApplicationTypeView = false;
		private boolean smStateView = false;
		private boolean smDistrictView = false;		
		private boolean smReportTypeView = false;
		private boolean smApplicationStatusView = false;
		private boolean smExtraTwoColumnView = false;
		private boolean smExtraTwoColumnTwoView = false;
		private boolean smRegistrationTypeView = false;
		private boolean smMofRegView = false;
		private boolean smMandatorySupplierTypeView = false;
		private boolean smMandatoryAllSupplierTypeView = false;
		private boolean smMandatoryApplicationDateView = false;
		private boolean smMandatoryApplicationStatusView = false;
		private boolean smMandatoryApplicationStatusTwoView = false;
		private boolean smMandatoryApplicationStatusThreeView = false;
		private boolean smApplicationStatusThreeView = false;
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
		private boolean smMandatoryStaticByStateView = false;
		private boolean smStaticStateView = false;
		private boolean smMandatoryApplicationNoView = false;
		private boolean smSVOUserView = false;
		private boolean smMandatoryReportTypeView = false;
		private boolean smMandatoryRecievedReportTypeView = false;
		private boolean smMandatoryApprovedReportTypeView = false;
		private boolean smMandatoryRejectedReportTypeView = false;
		private boolean smMandatoryRegisteredReportTypeDateView = false;
		private boolean smMandatoryZoneView = false;
		private boolean smMandatoryEPAdminPTJAdminZoneView = false;
		private boolean smEPAdminPTJAdminZoneView = false;
		private boolean smCommonZoneView = false;
		private boolean smBasicRegView = false;
		private boolean smMandatoryBothSupplierTypeView = false;
		private boolean smYearTextView = false;
		private boolean smMandatoryReceivedFromDateView = false;
		private boolean smMandatoryCreatedFromDateView = false;
		private boolean smRejectedDateView = false;
		private boolean smRegisteredDateView = false;
		private boolean smScheduleSiteVisitDateView = false;
		private boolean smActivationDateView = false;
		private boolean smSubmissionDateView = false;
		private boolean smExpiryDateView = false;
		private boolean smCreatedDateView = false;
		private boolean smApplicationDateView = false;
		private boolean smApplicationTypeTwoView = false;
		private boolean smMandatoryDateView = false;
		private boolean smNonMandatoryDateView = false;
		private boolean smApplicationReceivedDateView = false;
		private boolean smPaymentDateView = false;
		
		private boolean smRoleView = false;
		private boolean smEpNoView = false;
		private boolean smRegistrationNoView = false;
		private boolean smSupportingDocumentModeView = false;
		private boolean smSupportingDocumentStatusView = false;
		private boolean smMofRegNoFromView = false;
		private boolean smMofRegNoToView = false;
		private boolean smPaidUpCapitalFrmView = false;
		private boolean smPaidUpCptlDateView = false;
		private boolean smAccmltdCptlDateView = false;
		private boolean smPaidUpCapitalToView = false;
		private boolean smPaidUpCapitalNaccumView = false;
		private boolean smMandatoryMofRegNoFromView = false;
		private boolean smMandatoryMofRegNoToView = false;
		private boolean smPersonnelRoleView = false;
		private boolean smIdentficationTypeView = false;
		private boolean smIdentificationNumberView = false;
		private boolean smCancellationByView = false;
		private boolean smMofRegNoTwoView = false;
		private boolean smRegistrationTypeTwoView = false;
		private boolean smActiveSubmissiveFromDateView = false;
		private boolean smActSubmisAllDateView = false;
		private boolean smProcessingOfficerNameView = false;
		private boolean smReportTypeTwoView = false;
		private boolean smBothSupplierTypeView = false;
		private boolean smBothMandatorySupplierTypeView = false;
		
		private boolean smReceiptNoView = false;
		private boolean smProcessingTypeView = false;
		private boolean smPaymentMethodView = false;
		private boolean smFromMonthView = false;
		private boolean smToMonthView = false;
		private boolean smSupplierView = false;
		private boolean smCompanyNameDropdownView = false;
		private boolean smFromAndToYearView = false;
		private boolean smUserNameView = false;
		private boolean smRequestExpiryDateView = false;
		private boolean smYearFromView = false;
		private boolean smThresholdYearFromView = false;
		private boolean smApprovalDateView = false;
		private boolean smMOFRegisteredDateView = false;
		private boolean smAllSupplierTypeView = false;
		private boolean smProfileStatusView = false;
		private boolean smMandatoryExpectedSiteVisitDateView = false;
		private boolean smMandatoryActualSiteVisitDateView = false;
		private boolean smGSTEffectiveDateView = false;
		private boolean smGSTRegistrationNoView = false;
		private boolean smGSTRegisteredStatusView = false;
		private boolean smMandatoryGSTRegisteredStatusView = false;
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
		private boolean flGroupOneWithSupplView = false;
		private boolean flGroupOneWithoutSupplView = false;
		private boolean flGroupTwoView = false;
		private boolean flGroupTwoWithoutSupplView = false;
		private boolean flGroupTwoWithSupplView = false;
		private boolean flGroupThreeView = false;
		private boolean flGroupThreeWithoutSupplView = false;
		private boolean flGroupThreeWithSupplView = false;
		private boolean flGroupFourView = false;
		private boolean flGroupFourWithoutSupplView = false;
		private boolean flGroupFourWithSupplView = false;
		private boolean flGroupFiveView = false;
		private boolean flGroupFiveWithoutSupplView = false;
		private boolean flGroupFiveWithSupplView = false;
		private boolean flGroupSixView = false;
		private boolean flGroupSixWithoutSupplView = false;
		private boolean flGroupSixWithSupplView = false;
		private boolean flGroupSevenView = false;
		private boolean flGroupSevenWithoutSupplView = false;
		private boolean flGroupSevenWithSupplView = false;
		private boolean flGroupEightView = false;
		private boolean flGroupEightWithoutSupplView = false;
		private boolean flGroupEightWithSupplView = false;
		private boolean flGroupNineView = false;
		private boolean flGroupNineWithoutSupplView = false;
		private boolean flGroupNineWithSupplView = false;
		private boolean flGroupTenView = false;
		private boolean flGroupTenWithoutSupplView = false;
		private boolean flGroupTenWithSupplView = false;
		private boolean flGroupElevenView = false;
		private boolean flGroupElevenWithoutSupplView = false;
		private boolean flGroupElevenWithSupplView = false;
		private boolean flGroupTwelveView = false;
		private boolean flGroupTwelveWithoutSupplView = false;
		private boolean flGroupTwelveWithSupplView = false;
		private boolean flGroupThirteenView = false;
		private boolean flGroupThirteenWithoutSupplView = false;
		private boolean flGroupThirteenWithSupplView = false;
		private boolean flGroupFourteenView = false;
		private boolean flGroupFourteenWithoutSupplView = false;
		private boolean flGroupFourteenWithSupplView = false;
		private boolean flGroupFifteenView = false;
		private boolean flGroupFifteenWithoutSupplView = false;		
		private boolean flGroupFifteenWithSupplView = false;
		private boolean flGroupSixteenView = false;
		private boolean flGroupSixteenWithoutSupplView = false;
		private boolean flGroupSixteenWithSupplView = false;
		private boolean flSupplierTextView = false;
		private boolean flSupplierDropdownView = false;
		private boolean flGroupSeventeenView = false;
		private boolean flGroupSeventeenWithoutSupplView = false;
		private boolean flGroupSeventeenWithSupplView = false;
		private boolean flMandatoryMinistryView = false;
		private boolean flMandatoryPtjView = false;
		private boolean flStopInstructionNoView = false;
		private boolean flGroupEighteenView = false;
		private boolean flGroupEighteenWithoutSupplView = false;
		private boolean flGroupEighteenWithSupplView = false;
		private boolean flGroupNineteenView = false;
		private boolean flGroupNineteenWithoutSupplView = false;
		private boolean flGroupNineteenWithSupplView = false;
		private boolean flGroupTwentyView = false;
		private boolean flGroupTwentyWithoutSupplView = false;
		private boolean flGroupTwentyWithSupplView = false;
		private boolean flGroupTwentyOneView = false;
		private boolean flGroupTwentyOneWithoutSupplView = false;
		private boolean flGroupTwentyOneWithSupplView = false;
		private boolean flGroupTwentyTwoView = false;
		private boolean flGroupTwentyTwoWithoutSupplView = false;
		private boolean flGroupTwentyTwoWithSupplView = false;
		private boolean flSupplierInvoiceNoView = false;
		private boolean flSupplierDeliveryOrderNoView = false;
		private boolean flPaymentAdviceNoView = false;
		private boolean flGroupTwentyThreeView = false;
		private boolean flGroupTwentyThreeWithoutSupplView = false;
		private boolean flGroupTwentyThreeWithSupplView = false;
		private boolean flGroupTwentyFourView = false;
		private boolean flGroupTwentyFourWithoutSupplView = false;
		private boolean flGroupTwentyFourWithSupplView = false;
		private boolean flFromDateView = false;
		private boolean flContractRequestCreatedDateView = false;
		private boolean flContractOrderDateView = false;
		private boolean flDeliveryOrderCreatedDateView = false;
		private boolean flFRNCreatedDateView = false;
		private boolean flStopInstructionCreatedDateView = false;
		private boolean flInvoiceCreatedDateView = false;
		private boolean flContractOrderCreatedDateView = false;
		private boolean flContractRequestRejectionDateView = false;
		private boolean flGroupTwentyFiveView = false;
		private boolean flGroupTwentyFiveWithoutSupplView = false;
		private boolean flGroupTwentyFiveWithSupplView = false;
		private boolean flContractOrderCancellationDateView = false;
		private boolean flDeliveryOrderCancellationDateView = false;
		private boolean flGroupTwentySixView = false;
		private boolean flGroupTwentySixWithoutSupplView = false;
		private boolean flGroupTwentySixWithSupplView = false;
		private boolean flFRNCancellationDateView = false;
		private boolean flInvoiceCancellationDateView = false;
		private boolean flGroupTwentySevenView = false;
		private boolean flGroupTwentySevenWithoutSupplView = false;
		private boolean flGroupTwentySevenWithSupplView = false;
		
		
		
		public boolean isSmPaidUpCptlDateView() {
			return smPaidUpCptlDateView;
		}
		public void setSmPaidUpCptlDateView(boolean smPaidUpCptlDateView) {
			this.smPaidUpCptlDateView = smPaidUpCptlDateView;
		}
		public boolean isSmAccmltdCptlDateView() {
			return smAccmltdCptlDateView;
		}
		public void setSmAccmltdCptlDateView(boolean smAccmltdCptlDateView) {
			this.smAccmltdCptlDateView = smAccmltdCptlDateView;
		}
		public boolean isFlGroupFifteenWithSupplView() {
			return flGroupFifteenWithSupplView;
		}
		public void setFlGroupFifteenWithSupplView(boolean flGroupFifteenWithSupplView) {
			this.flGroupFifteenWithSupplView = flGroupFifteenWithSupplView;
		}
		public boolean isFlGroupTwoWithoutSupplView() {
			return flGroupTwoWithoutSupplView;
		}
		public void setFlGroupTwoWithoutSupplView(boolean flGroupTwoWithoutSupplView) {
			this.flGroupTwoWithoutSupplView = flGroupTwoWithoutSupplView;
		}
		public boolean isFlGroupTwoWithSupplView() {
			return flGroupTwoWithSupplView;
		}
		public void setFlGroupTwoWithSupplView(boolean flGroupTwoWithSupplView) {
			this.flGroupTwoWithSupplView = flGroupTwoWithSupplView;
		}
		public boolean isFlGroupThreeWithoutSupplView() {
			return flGroupThreeWithoutSupplView;
		}
		public void setFlGroupThreeWithoutSupplView(boolean flGroupThreeWithoutSupplView) {
			this.flGroupThreeWithoutSupplView = flGroupThreeWithoutSupplView;
		}
		public boolean isFlGroupThreeWithSupplView() {
			return flGroupThreeWithSupplView;
		}
		public void setFlGroupThreeWithSupplView(boolean flGroupThreeWithSupplView) {
			this.flGroupThreeWithSupplView = flGroupThreeWithSupplView;
		}
		public boolean isFlGroupFourWithoutSupplView() {
			return flGroupFourWithoutSupplView;
		}
		public void setFlGroupFourWithoutSupplView(boolean flGroupFourWithoutSupplView) {
			this.flGroupFourWithoutSupplView = flGroupFourWithoutSupplView;
		}
		public boolean isFlGroupFourWithSupplView() {
			return flGroupFourWithSupplView;
		}
		public void setFlGroupFourWithSupplView(boolean flGroupFourWithSupplView) {
			this.flGroupFourWithSupplView = flGroupFourWithSupplView;
		}
		public boolean isFlGroupFiveWithoutSupplView() {
			return flGroupFiveWithoutSupplView;
		}
		public void setFlGroupFiveWithoutSupplView(boolean flGroupFiveWithoutSupplView) {
			this.flGroupFiveWithoutSupplView = flGroupFiveWithoutSupplView;
		}
		public boolean isFlGroupFiveWithSupplView() {
			return flGroupFiveWithSupplView;
		}
		public void setFlGroupFiveWithSupplView(boolean flGroupFiveWithSupplView) {
			this.flGroupFiveWithSupplView = flGroupFiveWithSupplView;
		}
		public boolean isFlGroupSixWithoutSupplView() {
			return flGroupSixWithoutSupplView;
		}
		public void setFlGroupSixWithoutSupplView(boolean flGroupSixWithoutSupplView) {
			this.flGroupSixWithoutSupplView = flGroupSixWithoutSupplView;
		}
		public boolean isFlGroupSixWithSupplView() {
			return flGroupSixWithSupplView;
		}
		public void setFlGroupSixWithSupplView(boolean flGroupSixWithSupplView) {
			this.flGroupSixWithSupplView = flGroupSixWithSupplView;
		}
		public boolean isFlGroupSevenWithoutSupplView() {
			return flGroupSevenWithoutSupplView;
		}
		public void setFlGroupSevenWithoutSupplView(boolean flGroupSevenWithoutSupplView) {
			this.flGroupSevenWithoutSupplView = flGroupSevenWithoutSupplView;
		}
		public boolean isFlGroupSevenWithSupplView() {
			return flGroupSevenWithSupplView;
		}
		public void setFlGroupSevenWithSupplView(boolean flGroupSevenWithSupplView) {
			this.flGroupSevenWithSupplView = flGroupSevenWithSupplView;
		}
		public boolean isFlGroupEightWithoutSupplView() {
			return flGroupEightWithoutSupplView;
		}
		public void setFlGroupEightWithoutSupplView(boolean flGroupEightWithoutSupplView) {
			this.flGroupEightWithoutSupplView = flGroupEightWithoutSupplView;
		}
		public boolean isFlGroupEightWithSupplView() {
			return flGroupEightWithSupplView;
		}
		public void setFlGroupEightWithSupplView(boolean flGroupEightWithSupplView) {
			this.flGroupEightWithSupplView = flGroupEightWithSupplView;
		}
		public boolean isFlGroupNineWithoutSupplView() {
			return flGroupNineWithoutSupplView;
		}
		public void setFlGroupNineWithoutSupplView(boolean flGroupNineWithoutSupplView) {
			this.flGroupNineWithoutSupplView = flGroupNineWithoutSupplView;
		}
		public boolean isFlGroupNineWithSupplView() {
			return flGroupNineWithSupplView;
		}
		public void setFlGroupNineWithSupplView(boolean flGroupNineWithSupplView) {
			this.flGroupNineWithSupplView = flGroupNineWithSupplView;
		}
		public boolean isFlGroupTenWithoutSupplView() {
			return flGroupTenWithoutSupplView;
		}
		public void setFlGroupTenWithoutSupplView(boolean flGroupTenWithoutSupplView) {
			this.flGroupTenWithoutSupplView = flGroupTenWithoutSupplView;
		}
		public boolean isFlGroupTenWithSupplView() {
			return flGroupTenWithSupplView;
		}
		public void setFlGroupTenWithSupplView(boolean flGroupTenWithSupplView) {
			this.flGroupTenWithSupplView = flGroupTenWithSupplView;
		}
		public boolean isFlGroupElevenWithoutSupplView() {
			return flGroupElevenWithoutSupplView;
		}
		public void setFlGroupElevenWithoutSupplView(
				boolean flGroupElevenWithoutSupplView) {
			this.flGroupElevenWithoutSupplView = flGroupElevenWithoutSupplView;
		}
		public boolean isFlGroupElevenWithSupplView() {
			return flGroupElevenWithSupplView;
		}
		public void setFlGroupElevenWithSupplView(boolean flGroupElevenWithSupplView) {
			this.flGroupElevenWithSupplView = flGroupElevenWithSupplView;
		}
		public boolean isFlGroupTwelveWithoutSupplView() {
			return flGroupTwelveWithoutSupplView;
		}
		public void setFlGroupTwelveWithoutSupplView(
				boolean flGroupTwelveWithoutSupplView) {
			this.flGroupTwelveWithoutSupplView = flGroupTwelveWithoutSupplView;
		}
		public boolean isFlGroupTwelveWithSupplView() {
			return flGroupTwelveWithSupplView;
		}
		public void setFlGroupTwelveWithSupplView(boolean flGroupTwelveWithSupplView) {
			this.flGroupTwelveWithSupplView = flGroupTwelveWithSupplView;
		}
		public boolean isFlGroupThirteenWithoutSupplView() {
			return flGroupThirteenWithoutSupplView;
		}
		public void setFlGroupThirteenWithoutSupplView(
				boolean flGroupThirteenWithoutSupplView) {
			this.flGroupThirteenWithoutSupplView = flGroupThirteenWithoutSupplView;
		}
		public boolean isFlGroupThirteenWithSupplView() {
			return flGroupThirteenWithSupplView;
		}
		public void setFlGroupThirteenWithSupplView(boolean flGroupThirteenWithSupplView) {
			this.flGroupThirteenWithSupplView = flGroupThirteenWithSupplView;
		}
		public boolean isFlGroupFourteenWithoutSupplView() {
			return flGroupFourteenWithoutSupplView;
		}
		public void setFlGroupFourteenWithoutSupplView(
				boolean flGroupFourteenWithoutSupplView) {
			this.flGroupFourteenWithoutSupplView = flGroupFourteenWithoutSupplView;
		}
		public boolean isFlGroupFourteenWithSupplView() {
			return flGroupFourteenWithSupplView;
		}
		public void setFlGroupFourteenWithSupplView(boolean flGroupFourteenWithSupplView) {
			this.flGroupFourteenWithSupplView = flGroupFourteenWithSupplView;
		}
		public boolean isFlGroupFifteenWithoutSupplView() {
			return flGroupFifteenWithoutSupplView;
		}
		public void setFlGroupFifteenWithoutSupplView(
				boolean flGroupFifteenWithoutSupplView) {
			this.flGroupFifteenWithoutSupplView = flGroupFifteenWithoutSupplView;
		}		
		public boolean isFlGroupSixteenWithoutSupplView() {
			return flGroupSixteenWithoutSupplView;
		}
		public void setFlGroupSixteenWithoutSupplView(
				boolean flGroupSixteenWithoutSupplView) {
			this.flGroupSixteenWithoutSupplView = flGroupSixteenWithoutSupplView;
		}
		public boolean isFlGroupSixteenWithSupplView() {
			return flGroupSixteenWithSupplView;
		}
		public void setFlGroupSixteenWithSupplView(boolean flGroupSixteenWithSupplView) {
			this.flGroupSixteenWithSupplView = flGroupSixteenWithSupplView;
		}
		public boolean isFlGroupSeventeenWithoutSupplView() {
			return flGroupSeventeenWithoutSupplView;
		}
		public void setFlGroupSeventeenWithoutSupplView(
				boolean flGroupSeventeenWithoutSupplView) {
			this.flGroupSeventeenWithoutSupplView = flGroupSeventeenWithoutSupplView;
		}
		public boolean isFlGroupSeventeenWithSupplView() {
			return flGroupSeventeenWithSupplView;
		}
		public void setFlGroupSeventeenWithSupplView(
				boolean flGroupSeventeenWithSupplView) {
			this.flGroupSeventeenWithSupplView = flGroupSeventeenWithSupplView;
		}
		public boolean isFlGroupEighteenWithoutSupplView() {
			return flGroupEighteenWithoutSupplView;
		}
		public void setFlGroupEighteenWithoutSupplView(
				boolean flGroupEighteenWithoutSupplView) {
			this.flGroupEighteenWithoutSupplView = flGroupEighteenWithoutSupplView;
		}
		public boolean isFlGroupEighteenWithSupplView() {
			return flGroupEighteenWithSupplView;
		}
		public void setFlGroupEighteenWithSupplView(boolean flGroupEighteenWithSupplView) {
			this.flGroupEighteenWithSupplView = flGroupEighteenWithSupplView;
		}
		public boolean isFlGroupNineteenWithoutSupplView() {
			return flGroupNineteenWithoutSupplView;
		}
		public void setFlGroupNineteenWithoutSupplView(
				boolean flGroupNineteenWithoutSupplView) {
			this.flGroupNineteenWithoutSupplView = flGroupNineteenWithoutSupplView;
		}
		public boolean isFlGroupNineteenWithSupplView() {
			return flGroupNineteenWithSupplView;
		}
		public void setFlGroupNineteenWithSupplView(boolean flGroupNineteenWithSupplView) {
			this.flGroupNineteenWithSupplView = flGroupNineteenWithSupplView;
		}
		public boolean isFlGroupTwentyWithoutSupplView() {
			return flGroupTwentyWithoutSupplView;
		}
		public void setFlGroupTwentyWithoutSupplView(
				boolean flGroupTwentyWithoutSupplView) {
			this.flGroupTwentyWithoutSupplView = flGroupTwentyWithoutSupplView;
		}
		public boolean isFlGroupTwentyWithSupplView() {
			return flGroupTwentyWithSupplView;
		}
		public void setFlGroupTwentyWithSupplView(boolean flGroupTwentyWithSupplView) {
			this.flGroupTwentyWithSupplView = flGroupTwentyWithSupplView;
		}
		public boolean isFlGroupTwentyOneWithoutSupplView() {
			return flGroupTwentyOneWithoutSupplView;
		}
		public void setFlGroupTwentyOneWithoutSupplView(
				boolean flGroupTwentyOneWithoutSupplView) {
			this.flGroupTwentyOneWithoutSupplView = flGroupTwentyOneWithoutSupplView;
		}
		public boolean isFlGroupTwentyOneWithSupplView() {
			return flGroupTwentyOneWithSupplView;
		}
		public void setFlGroupTwentyOneWithSupplView(
				boolean flGroupTwentyOneWithSupplView) {
			this.flGroupTwentyOneWithSupplView = flGroupTwentyOneWithSupplView;
		}
		public boolean isFlGroupTwentyTwoWithoutSupplView() {
			return flGroupTwentyTwoWithoutSupplView;
		}
		public void setFlGroupTwentyTwoWithoutSupplView(
				boolean flGroupTwentyTwoWithoutSupplView) {
			this.flGroupTwentyTwoWithoutSupplView = flGroupTwentyTwoWithoutSupplView;
		}
		public boolean isFlGroupTwentyTwoWithSupplView() {
			return flGroupTwentyTwoWithSupplView;
		}
		public void setFlGroupTwentyTwoWithSupplView(
				boolean flGroupTwentyTwoWithSupplView) {
			this.flGroupTwentyTwoWithSupplView = flGroupTwentyTwoWithSupplView;
		}
		public boolean isFlGroupTwentyThreeWithoutSupplView() {
			return flGroupTwentyThreeWithoutSupplView;
		}
		public void setFlGroupTwentyThreeWithoutSupplView(
				boolean flGroupTwentyThreeWithoutSupplView) {
			this.flGroupTwentyThreeWithoutSupplView = flGroupTwentyThreeWithoutSupplView;
		}
		public boolean isFlGroupTwentyThreeWithSupplView() {
			return flGroupTwentyThreeWithSupplView;
		}
		public void setFlGroupTwentyThreeWithSupplView(
				boolean flGroupTwentyThreeWithSupplView) {
			this.flGroupTwentyThreeWithSupplView = flGroupTwentyThreeWithSupplView;
		}
		public boolean isFlGroupTwentyFourWithoutSupplView() {
			return flGroupTwentyFourWithoutSupplView;
		}
		public void setFlGroupTwentyFourWithoutSupplView(
				boolean flGroupTwentyFourWithoutSupplView) {
			this.flGroupTwentyFourWithoutSupplView = flGroupTwentyFourWithoutSupplView;
		}
		public boolean isFlGroupTwentyFourWithSupplView() {
			return flGroupTwentyFourWithSupplView;
		}
		public void setFlGroupTwentyFourWithSupplView(
				boolean flGroupTwentyFourWithSupplView) {
			this.flGroupTwentyFourWithSupplView = flGroupTwentyFourWithSupplView;
		}
		public boolean isFlGroupTwentyFiveWithoutSupplView() {
			return flGroupTwentyFiveWithoutSupplView;
		}
		public void setFlGroupTwentyFiveWithoutSupplView(
				boolean flGroupTwentyFiveWithoutSupplView) {
			this.flGroupTwentyFiveWithoutSupplView = flGroupTwentyFiveWithoutSupplView;
		}
		public boolean isFlGroupTwentyFiveWithSupplView() {
			return flGroupTwentyFiveWithSupplView;
		}
		public void setFlGroupTwentyFiveWithSupplView(
				boolean flGroupTwentyFiveWithSupplView) {
			this.flGroupTwentyFiveWithSupplView = flGroupTwentyFiveWithSupplView;
		}
		public boolean isFlGroupTwentySixWithoutSupplView() {
			return flGroupTwentySixWithoutSupplView;
		}
		public void setFlGroupTwentySixWithoutSupplView(
				boolean flGroupTwentySixWithoutSupplView) {
			this.flGroupTwentySixWithoutSupplView = flGroupTwentySixWithoutSupplView;
		}
		public boolean isFlGroupTwentySixWithSupplView() {
			return flGroupTwentySixWithSupplView;
		}
		public void setFlGroupTwentySixWithSupplView(
				boolean flGroupTwentySixWithSupplView) {
			this.flGroupTwentySixWithSupplView = flGroupTwentySixWithSupplView;
		}
		public boolean isFlGroupTwentySevenWithoutSupplView() {
			return flGroupTwentySevenWithoutSupplView;
		}
		public void setFlGroupTwentySevenWithoutSupplView(
				boolean flGroupTwentySevenWithoutSupplView) {
			this.flGroupTwentySevenWithoutSupplView = flGroupTwentySevenWithoutSupplView;
		}
		public boolean isFlGroupTwentySevenWithSupplView() {
			return flGroupTwentySevenWithSupplView;
		}
		public void setFlGroupTwentySevenWithSupplView(
				boolean flGroupTwentySevenWithSupplView) {
			this.flGroupTwentySevenWithSupplView = flGroupTwentySevenWithSupplView;
		}
		public boolean isFlGroupOneWithSupplView() {
			return flGroupOneWithSupplView;
		}
		public void setFlGroupOneWithSupplView(boolean flGroupOneWithSupplView) {
			this.flGroupOneWithSupplView = flGroupOneWithSupplView;
		}
		public boolean isFlGroupOneWithoutSupplView() {
			return flGroupOneWithoutSupplView;
		}
		public void setFlGroupOneWithoutSupplView(boolean flGroupOneWithoutSupplView) {
			this.flGroupOneWithoutSupplView = flGroupOneWithoutSupplView;
		}
		public boolean isFlGroupTwentySevenView() {
			return flGroupTwentySevenView;
		}
		public void setFlGroupTwentySevenView(boolean flGroupTwentySevenView) {
			this.flGroupTwentySevenView = flGroupTwentySevenView;
		}
		public boolean isFlInvoiceCancellationDateView() {
			return flInvoiceCancellationDateView;
		}
		public void setFlInvoiceCancellationDateView(
				boolean flInvoiceCancellationDateView) {
			this.flInvoiceCancellationDateView = flInvoiceCancellationDateView;
		}
		public boolean isFlFRNCancellationDateView() {
			return flFRNCancellationDateView;
		}
		public void setFlFRNCancellationDateView(boolean flFRNCancellationDateView) {
			this.flFRNCancellationDateView = flFRNCancellationDateView;
		}
		public boolean isFlGroupTwentySixView() {
			return flGroupTwentySixView;
		}
		public void setFlGroupTwentySixView(boolean flGroupTwentySixView) {
			this.flGroupTwentySixView = flGroupTwentySixView;
		}
		public boolean isFlDeliveryOrderCancellationDateView() {
			return flDeliveryOrderCancellationDateView;
		}
		public void setFlDeliveryOrderCancellationDateView(
				boolean flDeliveryOrderCancellationDateView) {
			this.flDeliveryOrderCancellationDateView = flDeliveryOrderCancellationDateView;
		}
		public boolean isFlContractOrderCancellationDateView() {
			return flContractOrderCancellationDateView;
		}
		public void setFlContractOrderCancellationDateView(
				boolean flContractOrderCancellationDateView) {
			this.flContractOrderCancellationDateView = flContractOrderCancellationDateView;
		}
		public boolean isFlGroupTwentyFiveView() {
			return flGroupTwentyFiveView;
		}
		public void setFlGroupTwentyFiveView(boolean flGroupTwentyFiveView) {
			this.flGroupTwentyFiveView = flGroupTwentyFiveView;
		}
		public boolean isFlContractRequestRejectionDateView() {
			return flContractRequestRejectionDateView;
		}
		public void setFlContractRequestRejectionDateView(
				boolean flContractRequestRejectionDateView) {
			this.flContractRequestRejectionDateView = flContractRequestRejectionDateView;
		}
		public boolean isFlContractOrderCreatedDateView() {
			return flContractOrderCreatedDateView;
		}
		public void setFlContractOrderCreatedDateView(
				boolean flContractOrderCreatedDateView) {
			this.flContractOrderCreatedDateView = flContractOrderCreatedDateView;
		}
		public boolean isFlInvoiceCreatedDateView() {
			return flInvoiceCreatedDateView;
		}
		public void setFlInvoiceCreatedDateView(boolean flInvoiceCreatedDateView) {
			this.flInvoiceCreatedDateView = flInvoiceCreatedDateView;
		}
		public boolean isFlStopInstructionCreatedDateView() {
			return flStopInstructionCreatedDateView;
		}
		public void setFlStopInstructionCreatedDateView(
				boolean flStopInstructionCreatedDateView) {
			this.flStopInstructionCreatedDateView = flStopInstructionCreatedDateView;
		}
		public boolean isFlFromDateView() {
			return flFromDateView;
		}
		public void setFlFromDateView(boolean flFromDateView) {
			this.flFromDateView = flFromDateView;
		}
		public boolean isFlContractRequestCreatedDateView() {
			return flContractRequestCreatedDateView;
		}
		public void setFlContractRequestCreatedDateView(
				boolean flContractRequestCreatedDateView) {
			this.flContractRequestCreatedDateView = flContractRequestCreatedDateView;
		}
		public boolean isFlContractOrderDateView() {
			return flContractOrderDateView;
		}
		public void setFlContractOrderDateView(boolean flContractOrderDateView) {
			this.flContractOrderDateView = flContractOrderDateView;
		}
		public boolean isFlDeliveryOrderCreatedDateView() {
			return flDeliveryOrderCreatedDateView;
		}
		public void setFlDeliveryOrderCreatedDateView(
				boolean flDeliveryOrderCreatedDateView) {
			this.flDeliveryOrderCreatedDateView = flDeliveryOrderCreatedDateView;
		}
		public boolean isFlFRNCreatedDateView() {
			return flFRNCreatedDateView;
		}
		public void setFlFRNCreatedDateView(boolean flFRNCreatedDateView) {
			this.flFRNCreatedDateView = flFRNCreatedDateView;
		}
		public boolean isSmStaticStateView() {
			return smStaticStateView;
		}
		public void setSmStaticStateView(boolean smStaticStateView) {
			this.smStaticStateView = smStaticStateView;
		}
		//CT
		private boolean ctContractNoView = false;
		private boolean ctAgreementView = false;
		private boolean ctStatusView = false;
		private boolean ctContrMontrStatusView = false;
		private boolean ctCompleteView = false;
		private boolean ctGroupOneView = false;
		private boolean ctGroupTwoView = false;
		private boolean ctProductServiceView = false;
		private boolean ctLoanNoView = false;
		private boolean ctMinistryView = false;
		private boolean ctPtjView = false;
		private boolean ctProcurementModeView = false;
		private boolean ctPhysicalContractNoView = false;
		private boolean ctGroupThreeView = false;
		private boolean ctStatusTwoView = false;
		private boolean ctLOAPhysicalNoView = false;
		private boolean ctUserTypeView = false;
		private boolean ctSubTypeView = false;
		private boolean ctGroupFourView = false;
		private boolean ctGroupFiveView = false;
		private boolean ctStatusThreeView = false;
		private boolean ctSubTypeOnlyView = false;
		private boolean ctSubTypeMCAView = false;
		private boolean ctSubTypeApproverView = false;
		//PP
		private boolean ppYearView = false;
		private boolean ppMinistryView = false;
		private boolean ppPTJView = false;
		//PM
		private boolean pmServiceTypeView = false;		
		private boolean pmStatusView = false;
		private boolean pmFinancialOrgNameView = false;
		private boolean pmBusinessRegNoView = false;
		
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
		private String flSupplierDeliveryOrderNoVar=ReportConst.EMPTY_VAL;	
		private String itemCodeTotVar=ReportConst.EMPTY_VAL;
		private String poAmountTotVar=ReportConst.EMPTY_VAL;
		private String prNoTotVar=ReportConst.EMPTY_VAL;
		private String pmoTotVar=ReportConst.EMPTY_VAL;
		private String fromDateVar=ReportConst.EMPTY_VAL;
		private String toDateVar=ReportConst.EMPTY_VAL;		
		//cm
		private String cmSupplOrAdminVar=ReportConst.EMPTY_VAL;	
		private String cmExtentionCodeVar=ReportConst.EMPTY_VAL;
		private String cmExtentionVar=ReportConst.EMPTY_VAL;
		private String cmGroupOneVar=ReportConst.EMPTY_VAL;
		private String cmExtentionBrandVar=ReportConst.EMPTY_VAL;
		private String cmExtentionTypeVar=ReportConst.EMPTY_VAL;
		private String cmExtentionMeasurmentVar=ReportConst.EMPTY_VAL;
		private String cmExtentionColourVar=ReportConst.EMPTY_VAL;
		//sm
		private String paymentTypeTotVar=ReportConst.EMPTY_VAL;	
		private String reportTypeTotVar=ReportConst.EMPTY_VAL;	
		private String applicationTypeTotVar=ReportConst.EMPTY_VAL;	
		private String stateTotVar=ReportConst.EMPTY_VAL;	
		private String districtTotVar=ReportConst.EMPTY_VAL;
		private String applicationNoTotVar=ReportConst.EMPTY_VAL;	
		private String zoneTotVar=ReportConst.EMPTY_VAL;	
		private String supplierTypeTotVar=ReportConst.EMPTY_VAL;	
		private String mofRegNoTotVar=ReportConst.EMPTY_VAL;	
		private String registrationStatusTotVar=ReportConst.EMPTY_VAL;	
		private String mainCategoryTotVar=ReportConst.EMPTY_VAL;	
		private String subCategoryTotVar=ReportConst.EMPTY_VAL;	
		private String subCategoryDivisionTotVar=ReportConst.EMPTY_VAL;	
		private String applicationStatusTotVar=ReportConst.EMPTY_VAL;
		private String smCreatedFromYearVar=ReportConst.EMPTY_VAL;
		private String smCreatedToYearVar=ReportConst.EMPTY_VAL;
		private String smMonthlyRptByYearVar=ReportConst.EMPTY_VAL;
		private String smMandatoryCreatedFromYearVar=ReportConst.EMPTY_VAL;
		private String smMandatoryCreatedToYearVar=ReportConst.EMPTY_VAL;
		private String smSVOUserVar=ReportConst.EMPTY_VAL;
		private String smYearTextVar=ReportConst.EMPTY_VAL;
		private String smRoleVar=ReportConst.EMPTY_VAL;
		
		private String smEpNoVar=ReportConst.EMPTY_VAL;
		private String smRegistrationNoVar=ReportConst.EMPTY_VAL;
		private String smSupportingDocumentModeVar=ReportConst.EMPTY_VAL;
		private String smSupportingDocumentStatusVar=ReportConst.EMPTY_VAL;
		private String smMofRegNoFromVar=ReportConst.EMPTY_VAL;
		private String smMofRegNoToVar=ReportConst.EMPTY_VAL;
		private String smPaidUpCapitalFrmVar=ReportConst.EMPTY_VAL;
		private String smPaidUpCapitalToVar=ReportConst.EMPTY_VAL;
		private String smPaidUpCapitalNaccumVar=ReportConst.EMPTY_VAL;
		private String smPersonnelRoleVar=ReportConst.EMPTY_VAL;
		private String smIdentificationTypeVar=ReportConst.EMPTY_VAL;
		private String smIdentificationNumberVar=ReportConst.EMPTY_VAL;
		private String smCancellationByVar=ReportConst.EMPTY_VAL;
		private String smProcessingOfficerNameVar=ReportConst.EMPTY_VAL;
		private String smReportTypeVar=ReportConst.EMPTY_VAL;
		private String smReceiptNoVar=ReportConst.EMPTY_VAL;
		private String smProcessingTypeVar=ReportConst.EMPTY_VAL;
		private String smPaymentMethodVar=ReportConst.EMPTY_VAL;
		private String smFromMonthVar=ReportConst.EMPTY_VAL;
		private String smToMonthVar=ReportConst.EMPTY_VAL;
		private String SmThresholdFromYearVar=ReportConst.EMPTY_VAL;
		private String SmThresholdToYearVar=ReportConst.EMPTY_VAL;
		private String smUserNameVar=ReportConst.EMPTY_VAL;
		private String smRequestExpiryDateVar=ReportConst.EMPTY_VAL;
		private String smProfileStatusVar=ReportConst.EMPTY_VAL;
		private String smGSTRegistrationNoVar=ReportConst.EMPTY_VAL;
		private String smGSTRegisteredStatusVar=ReportConst.EMPTY_VAL;
		
		private String reportValueIdVar=ReportConst.EMPTY_VAL;
		private String userIdVar=ReportConst.EMPTY_VAL;
		//sc
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
		private String scRequestNoteStatusVar=ReportConst.EMPTY_VAL;
		private String scSimpleQuoteStatusVar=ReportConst.EMPTY_VAL;
		private String scSimpleQuoteDtlGroupVar=ReportConst.EMPTY_VAL;
		private String scPurchaseInDefaultStatusVar=ReportConst.EMPTY_VAL;
		//CT
		private String ctContractNoVar=ReportConst.EMPTY_VAL;
		private String ctAgreementVar=ReportConst.EMPTY_VAL;
		private String ctStatusVar=ReportConst.EMPTY_VAL;
		private String ctContrMontrStatusVar=ReportConst.EMPTY_VAL;
		private String ctHiddenParamGroupVar=ReportConst.EMPTY_VAL;
		private String ctCompleteVar=ReportConst.EMPTY_VAL;
		private String ctGroupOneVar=ReportConst.EMPTY_VAL;
		private String ctGroupTwoVar=ReportConst.EMPTY_VAL;
		private String ctProductServiceVar=ReportConst.EMPTY_VAL;
		private String ctLoanNoVar=ReportConst.EMPTY_VAL;
		private String ctMinistryVar=ReportConst.EMPTY_VAL;
		private String ctPtjVar=ReportConst.EMPTY_VAL;
		private String ctProcurementModeVar=ReportConst.EMPTY_VAL;
		private String ctPhysicalContractNoVar=ReportConst.EMPTY_VAL;
		private String ctGroupThreeVar=ReportConst.EMPTY_VAL;
		private String ctStatusTwoVar=ReportConst.EMPTY_VAL;
		private String ctLOAPhysicalNoVar=ReportConst.EMPTY_VAL;
		private String ctUserTypeVar=ReportConst.EMPTY_VAL;
		private String ctSubTypeVar=ReportConst.EMPTY_VAL;
		//ct
		private String flContractOrderVar=ReportConst.EMPTY_VAL;		
		private String flContractRequestVar=ReportConst.EMPTY_VAL;
		private String flPurchaseOrderNoVar=ReportConst.EMPTY_VAL;
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
		private String flGroupSeventeenVar=ReportConst.EMPTY_VAL;
		private String flStopInstructionNoVar=ReportConst.EMPTY_VAL;
		private String flSupplierInvoiceNoVar=ReportConst.EMPTY_VAL;
		private String flPaymentAdviceNoVar=ReportConst.EMPTY_VAL;
		//PP
		private String ppYearVar=ReportConst.EMPTY_VAL;
		private String ppMinistryVar=ReportConst.EMPTY_VAL;
		private String ppPTJVar=ReportConst.EMPTY_VAL;
		//PM
		private String pmServiceTypeVar=ReportConst.EMPTY_VAL;
		private String pmStatusVar=ReportConst.EMPTY_VAL;
		private String pmFinancialOrgNameVar=ReportConst.EMPTY_VAL;
		private String pmBusinessRegNoVar=ReportConst.EMPTY_VAL;
		
		
		public boolean isCtSubTypeOnlyView() {
			return ctSubTypeOnlyView;
		}
		public void setCtSubTypeOnlyView(boolean ctSubTypeOnlyView) {
			this.ctSubTypeOnlyView = ctSubTypeOnlyView;
		}
		public boolean isCtSubTypeMCAView() {
			return ctSubTypeMCAView;
		}
		public void setCtSubTypeMCAView(boolean ctSubTypeMCAView) {
			this.ctSubTypeMCAView = ctSubTypeMCAView;
		}
		public boolean isCtSubTypeApproverView() {
			return ctSubTypeApproverView;
		}
		public void setCtSubTypeApproverView(boolean ctSubTypeApproverView) {
			this.ctSubTypeApproverView = ctSubTypeApproverView;
		}
		public boolean isPmStatusView() {
			return pmStatusView;
		}
		public void setPmStatusView(boolean pmStatusView) {
			this.pmStatusView = pmStatusView;
		}
		public boolean isPmFinancialOrgNameView() {
			return pmFinancialOrgNameView;
		}
		public void setPmFinancialOrgNameView(boolean pmFinancialOrgNameView) {
			this.pmFinancialOrgNameView = pmFinancialOrgNameView;
		}
		public boolean isPmBusinessRegNoView() {
			return pmBusinessRegNoView;
		}
		public void setPmBusinessRegNoView(boolean pmBusinessRegNoView) {
			this.pmBusinessRegNoView = pmBusinessRegNoView;
		}
		public String getPmStatusVar() {
			return pmStatusVar;
		}
		public void setPmStatusVar(String pmStatusVar) {
			this.pmStatusVar = pmStatusVar;
		}
		public String getPmFinancialOrgNameVar() {
			return pmFinancialOrgNameVar;
		}
		public void setPmFinancialOrgNameVar(String pmFinancialOrgNameVar) {
			this.pmFinancialOrgNameVar = pmFinancialOrgNameVar;
		}
		public String getPmBusinessRegNoVar() {
			return pmBusinessRegNoVar;
		}
		public void setPmBusinessRegNoVar(String pmBusinessRegNoVar) {
			this.pmBusinessRegNoVar = pmBusinessRegNoVar;
		}
		public String getPmServiceTypeVar() {
			return pmServiceTypeVar;
		}
		public void setPmServiceTypeVar(String pmServiceTypeVar) {
			this.pmServiceTypeVar = pmServiceTypeVar;
		}
		public boolean isPmServiceTypeView() {
			return pmServiceTypeView;
		}
		public void setPmServiceTypeView(boolean pmServiceTypeView) {
			this.pmServiceTypeView = pmServiceTypeView;
		}
		public boolean isFlGroupTwentyFourView() {
			return flGroupTwentyFourView;
		}
		public void setFlGroupTwentyFourView(boolean flGroupTwentyFourView) {
			this.flGroupTwentyFourView = flGroupTwentyFourView;
		}
		public boolean isFlGroupTwentyThreeView() {
			return flGroupTwentyThreeView;
		}
		public void setFlGroupTwentyThreeView(boolean flGroupTwentyThreeView) {
			this.flGroupTwentyThreeView = flGroupTwentyThreeView;
		}
		public boolean isFlPaymentAdviceNoView() {
			return flPaymentAdviceNoView;
		}
		public void setFlPaymentAdviceNoView(boolean flPaymentAdviceNoView) {
			this.flPaymentAdviceNoView = flPaymentAdviceNoView;
		}
		public String getFlPaymentAdviceNoVar() {
			return flPaymentAdviceNoVar;
		}
		public void setFlPaymentAdviceNoVar(String flPaymentAdviceNoVar) {
			this.flPaymentAdviceNoVar = flPaymentAdviceNoVar;
		}
		public boolean isFlSupplierDeliveryOrderNoView() {
			return flSupplierDeliveryOrderNoView;
		}
		public void setFlSupplierDeliveryOrderNoView(
				boolean flSupplierDeliveryOrderNoView) {
			this.flSupplierDeliveryOrderNoView = flSupplierDeliveryOrderNoView;
		}
		
		public String getFlSupplierDeliveryOrderNoVar() {
			return flSupplierDeliveryOrderNoVar;
		}
		public void setFlSupplierDeliveryOrderNoVar(String flSupplierDeliveryOrderNoVar) {
			this.flSupplierDeliveryOrderNoVar = flSupplierDeliveryOrderNoVar;
		}
		public boolean isSmMandatoryGSTRegisteredStatusView() {
			return smMandatoryGSTRegisteredStatusView;
		}
		public void setSmMandatoryGSTRegisteredStatusView(
				boolean smMandatoryGSTRegisteredStatusView) {
			this.smMandatoryGSTRegisteredStatusView = smMandatoryGSTRegisteredStatusView;
		}
		public boolean isSmGSTRegistrationNoView() {
			return smGSTRegistrationNoView;
		}
		public void setSmGSTRegistrationNoView(boolean smGSTRegistrationNoView) {
			this.smGSTRegistrationNoView = smGSTRegistrationNoView;
		}
		public boolean isSmGSTRegisteredStatusView() {
			return smGSTRegisteredStatusView;
		}
		public void setSmGSTRegisteredStatusView(boolean smGSTRegisteredStatusView) {
			this.smGSTRegisteredStatusView = smGSTRegisteredStatusView;
		}
		public String getSmGSTRegistrationNoVar() {
			return smGSTRegistrationNoVar;
		}
		public void setSmGSTRegistrationNoVar(String smGSTRegistrationNoVar) {
			this.smGSTRegistrationNoVar = smGSTRegistrationNoVar;
		}
		public String getSmGSTRegisteredStatusVar() {
			return smGSTRegisteredStatusVar;
		}
		public void setSmGSTRegisteredStatusVar(String smGSTRegisteredStatusVar) {
			this.smGSTRegisteredStatusVar = smGSTRegisteredStatusVar;
		}
		public boolean isSmGSTEffectiveDateView() {
			return smGSTEffectiveDateView;
		}
		public void setSmGSTEffectiveDateView(boolean smGSTEffectiveDateView) {
			this.smGSTEffectiveDateView = smGSTEffectiveDateView;
		}
		public boolean isCtGroupFiveView() {
			return ctGroupFiveView;
		}
		public void setCtGroupFiveView(boolean ctGroupFiveView) {
			this.ctGroupFiveView = ctGroupFiveView;
		}
		public boolean isSmMandatoryExpectedSiteVisitDateView() {
			return smMandatoryExpectedSiteVisitDateView;
		}
		public void setSmMandatoryExpectedSiteVisitDateView(
				boolean smMandatoryExpectedSiteVisitDateView) {
			this.smMandatoryExpectedSiteVisitDateView = smMandatoryExpectedSiteVisitDateView;
		}
		public boolean isSmMandatoryActualSiteVisitDateView() {
			return smMandatoryActualSiteVisitDateView;
		}
		public void setSmMandatoryActualSiteVisitDateView(
				boolean smMandatoryActualSiteVisitDateView) {
			this.smMandatoryActualSiteVisitDateView = smMandatoryActualSiteVisitDateView;
		}
		public boolean isSmMandatoryPaymentTypeView() {
			return smMandatoryPaymentTypeView;
		}
		public void setSmMandatoryPaymentTypeView(boolean smMandatoryPaymentTypeView) {
			this.smMandatoryPaymentTypeView = smMandatoryPaymentTypeView;
		}
		public boolean isSmPaymentDateView() {
			return smPaymentDateView;
		}
		public void setSmPaymentDateView(boolean smPaymentDateView) {
			this.smPaymentDateView = smPaymentDateView;
		}
		public boolean isSmPaymentTypeTwoView() {
			return smPaymentTypeTwoView;
		}
		public void setSmPaymentTypeTwoView(boolean smPaymentTypeTwoView) {
			this.smPaymentTypeTwoView = smPaymentTypeTwoView;
		}
		public boolean isSmProfileStatusView() {
			return smProfileStatusView;
		}
		public void setSmProfileStatusView(boolean smProfileStatusView) {
			this.smProfileStatusView = smProfileStatusView;
		}
		public String getSmProfileStatusVar() {
			return smProfileStatusVar;
		}
		public void setSmProfileStatusVar(String smProfileStatusVar) {
			this.smProfileStatusVar = smProfileStatusVar;
		}
		public boolean isSmApplicationStatusThreeView() {
			return smApplicationStatusThreeView;
		}
		public void setSmApplicationStatusThreeView(boolean smApplicationStatusThreeView) {
			this.smApplicationStatusThreeView = smApplicationStatusThreeView;
		}
		public boolean isSmMandatoryAllSupplierTypeView() {
			return smMandatoryAllSupplierTypeView;
		}
		public void setSmMandatoryAllSupplierTypeView(
				boolean smMandatoryAllSupplierTypeView) {
			this.smMandatoryAllSupplierTypeView = smMandatoryAllSupplierTypeView;
		}
		public boolean isSmAllSupplierTypeView() {
			return smAllSupplierTypeView;
		}
		public void setSmAllSupplierTypeView(boolean smAllSupplierTypeView) {
			this.smAllSupplierTypeView = smAllSupplierTypeView;
		}
		public boolean isSmMOFRegisteredDateView() {
			return smMOFRegisteredDateView;
		}
		public void setSmMOFRegisteredDateView(boolean smMOFRegisteredDateView) {
			this.smMOFRegisteredDateView = smMOFRegisteredDateView;
		}
		public boolean isSmConsultantRegistrationStatusView() {
			return smConsultantRegistrationStatusView;
		}
		public void setSmConsultantRegistrationStatusView(
				boolean smConsultantRegistrationStatusView) {
			this.smConsultantRegistrationStatusView = smConsultantRegistrationStatusView;
		}
		public boolean isScMandatoryProcessRptQuotationNoView() {
			return scMandatoryProcessRptQuotationNoView;
		}
		public void setScMandatoryProcessRptQuotationNoView(
				boolean scMandatoryProcessRptQuotationNoView) {
			this.scMandatoryProcessRptQuotationNoView = scMandatoryProcessRptQuotationNoView;
		}
		public boolean isSmBothMandatorySupplierTypeView() {
			return smBothMandatorySupplierTypeView;
		}
		public void setSmBothMandatorySupplierTypeView(
				boolean smBothMandatorySupplierTypeView) {
			this.smBothMandatorySupplierTypeView = smBothMandatorySupplierTypeView;
		}
		public boolean isSmMandatoryEPAdminPTJAdminZoneView() {
			return smMandatoryEPAdminPTJAdminZoneView;
		}
		public void setSmMandatoryEPAdminPTJAdminZoneView(
				boolean smMandatoryEPAdminPTJAdminZoneView) {
			this.smMandatoryEPAdminPTJAdminZoneView = smMandatoryEPAdminPTJAdminZoneView;
		}
		public boolean isSmCommonZoneView() {
			return smCommonZoneView;
		}
		public void setSmCommonZoneView(boolean smCommonZoneView) {
			this.smCommonZoneView = smCommonZoneView;
		}
		public boolean isSmEPAdminPTJAdminZoneView() {
			return smEPAdminPTJAdminZoneView;
		}
		public void setSmEPAdminPTJAdminZoneView(boolean smEPAdminPTJAdminZoneView) {
			this.smEPAdminPTJAdminZoneView = smEPAdminPTJAdminZoneView;
		}
		public String getFlSupplierInvoiceNoVar() {
			return flSupplierInvoiceNoVar;
		}
		public void setFlSupplierInvoiceNoVar(String flSupplierInvoiceNoVar) {
			this.flSupplierInvoiceNoVar = flSupplierInvoiceNoVar;
		}
		public boolean isFlSupplierInvoiceNoView() {
			return flSupplierInvoiceNoView;
		}
		public void setFlSupplierInvoiceNoView(boolean flSupplierInvoiceNoView) {
			this.flSupplierInvoiceNoView = flSupplierInvoiceNoView;
		}
		public boolean isCtStatusThreeView() {
			return ctStatusThreeView;
		}
		public void setCtStatusThreeView(boolean ctStatusThreeView) {
			this.ctStatusThreeView = ctStatusThreeView;
		}
		public boolean isCtGroupFourView() {
			return ctGroupFourView;
		}
		public void setCtGroupFourView(boolean ctGroupFourView) {
			this.ctGroupFourView = ctGroupFourView;
		}
		public boolean isSmZoneLabelView() {
			return smZoneLabelView;
		}
		public void setSmZoneLabelView(boolean smZoneLabelView) {
			this.smZoneLabelView = smZoneLabelView;
		}
		public boolean isGenerateButtonView() {
			return generateButtonView;
		}
		public void setGenerateButtonView(boolean generateButtonView) {
			this.generateButtonView = generateButtonView;
		}
		public boolean isFlGroupTwentyTwoView() {
			return flGroupTwentyTwoView;
		}
		public void setFlGroupTwentyTwoView(boolean flGroupTwentyTwoView) {
			this.flGroupTwentyTwoView = flGroupTwentyTwoView;
		}
		public boolean isFlGroupTwentyOneView() {
			return flGroupTwentyOneView;
		}
		public void setFlGroupTwentyOneView(boolean flGroupTwentyOneView) {
			this.flGroupTwentyOneView = flGroupTwentyOneView;
		}
		public boolean isFlGroupTwentyView() {
			return flGroupTwentyView;
		}
		public void setFlGroupTwentyView(boolean flGroupTwentyView) {
			this.flGroupTwentyView = flGroupTwentyView;
		}
		public boolean isFlGroupNineteenView() {
			return flGroupNineteenView;
		}
		public void setFlGroupNineteenView(boolean flGroupNineteenView) {
			this.flGroupNineteenView = flGroupNineteenView;
		}
		public boolean isFlGroupEighteenView() {
			return flGroupEighteenView;
		}
		public void setFlGroupEighteenView(boolean flGroupEighteenView) {
			this.flGroupEighteenView = flGroupEighteenView;
		}
		public boolean isFlStopInstructionNoView() {
			return flStopInstructionNoView;
		}
		public void setFlStopInstructionNoView(boolean flStopInstructionNoView) {
			this.flStopInstructionNoView = flStopInstructionNoView;
		}
		public String getFlStopInstructionNoVar() {
			return flStopInstructionNoVar;
		}
		public void setFlStopInstructionNoVar(String flStopInstructionNoVar) {
			this.flStopInstructionNoVar = flStopInstructionNoVar;
		}
		public boolean isFlMandatoryMinistryView() {
			return flMandatoryMinistryView;
		}
		public void setFlMandatoryMinistryView(boolean flMandatoryMinistryView) {
			this.flMandatoryMinistryView = flMandatoryMinistryView;
		}
		public boolean isFlMandatoryPtjView() {
			return flMandatoryPtjView;
		}
		public void setFlMandatoryPtjView(boolean flMandatoryPtjView) {
			this.flMandatoryPtjView = flMandatoryPtjView;
		}
		public boolean isSmExtraTwoColumnTwoView() {
			return smExtraTwoColumnTwoView;
		}
		public void setSmExtraTwoColumnTwoView(boolean smExtraTwoColumnTwoView) {
			this.smExtraTwoColumnTwoView = smExtraTwoColumnTwoView;
		}
		public boolean isSmApprovalDateView() {
			return smApprovalDateView;
		}
		public void setSmApprovalDateView(boolean smApprovalDateView) {
			this.smApprovalDateView = smApprovalDateView;
		}
		public boolean isSmApplicationTypeTwoView() {
			return smApplicationTypeTwoView;
		}
		public void setSmApplicationTypeTwoView(boolean smApplicationTypeTwoView) {
			this.smApplicationTypeTwoView = smApplicationTypeTwoView;
		}
		public boolean isSmYearFromView() {
			return smYearFromView;
		}
		public void setSmYearFromView(boolean smYearFromView) {
			this.smYearFromView = smYearFromView;
		}
		public boolean isSmThresholdYearFromView() {
			return smThresholdYearFromView;
		}
		public void setSmThresholdYearFromView(boolean smThresholdYearFromView) {
			this.smThresholdYearFromView = smThresholdYearFromView;
		}
		public boolean isSmRequestExpiryDateView() {
			return smRequestExpiryDateView;
		}
		public void setSmRequestExpiryDateView(boolean smRequestExpiryDateView) {
			this.smRequestExpiryDateView = smRequestExpiryDateView;
		}
		public String getSmRequestExpiryDateVar() {
			return smRequestExpiryDateVar;
		}
		public void setSmRequestExpiryDateVar(String smRequestExpiryDateVar) {
			this.smRequestExpiryDateVar = smRequestExpiryDateVar;
		}
		public boolean isSmUserNameView() {
			return smUserNameView;
		}
		public void setSmUserNameView(boolean smUserNameView) {
			this.smUserNameView = smUserNameView;
		}
		public String getSmUserNameVar() {
			return smUserNameVar;
		}
		public void setSmUserNameVar(String smUserNameVar) {
			this.smUserNameVar = smUserNameVar;
		}
		public String getSmThresholdFromYearVar() {
			return SmThresholdFromYearVar;
		}
		public void setSmThresholdFromYearVar(String smThresholdFromYearVar) {
			SmThresholdFromYearVar = smThresholdFromYearVar;
		}
		public String getSmThresholdToYearVar() {
			return SmThresholdToYearVar;
		}
		public void setSmThresholdToYearVar(String smThresholdToYearVar) {
			SmThresholdToYearVar = smThresholdToYearVar;
		}
		public boolean isSmFromAndToYearView() {
			return smFromAndToYearView;
		}
		public void setSmFromAndToYearView(boolean smFromAndToYearView) {
			this.smFromAndToYearView = smFromAndToYearView;
		}
		public boolean isSmCompanyNameDropdownView() {
			return smCompanyNameDropdownView;
		}
		public void setSmCompanyNameDropdownView(boolean smCompanyNameDropdownView) {
			this.smCompanyNameDropdownView = smCompanyNameDropdownView;
		}
		public boolean isSmSupplierView() {
			return smSupplierView;
		}
		public void setSmSupplierView(boolean smSupplierView) {
			this.smSupplierView = smSupplierView;
		}
		public boolean isSmFromMonthView() {
			return smFromMonthView;
		}
		public void setSmFromMonthView(boolean smFromMonthView) {
			this.smFromMonthView = smFromMonthView;
		}
		public boolean isSmToMonthView() {
			return smToMonthView;
		}
		public void setSmToMonthView(boolean smToMonthView) {
			this.smToMonthView = smToMonthView;
		}
		public String getSmFromMonthVar() {
			return smFromMonthVar;
		}
		public void setSmFromMonthVar(String smFromMonthVar) {
			this.smFromMonthVar = smFromMonthVar;
		}
		public String getSmToMonthVar() {
			return smToMonthVar;
		}
		public void setSmToMonthVar(String smToMonthVar) {
			this.smToMonthVar = smToMonthVar;
		}
		public boolean isSmMandatoryRegisteredReportTypeDateView() {
			return smMandatoryRegisteredReportTypeDateView;
		}
		public void setSmMandatoryRegisteredReportTypeDateView(
				boolean smMandatoryRegisteredReportTypeDateView) {
			this.smMandatoryRegisteredReportTypeDateView = smMandatoryRegisteredReportTypeDateView;
		}
		public boolean isSmMandatoryRecievedReportTypeView() {
			return smMandatoryRecievedReportTypeView;
		}
		public void setSmMandatoryRecievedReportTypeView(
				boolean smMandatoryRecievedReportTypeView) {
			this.smMandatoryRecievedReportTypeView = smMandatoryRecievedReportTypeView;
		}
		public boolean isSmMandatoryApprovedReportTypeView() {
			return smMandatoryApprovedReportTypeView;
		}
		public void setSmMandatoryApprovedReportTypeView(
				boolean smMandatoryApprovedReportTypeView) {
			this.smMandatoryApprovedReportTypeView = smMandatoryApprovedReportTypeView;
		}
		public boolean isSmMandatoryRejectedReportTypeView() {
			return smMandatoryRejectedReportTypeView;
		}
		public void setSmMandatoryRejectedReportTypeView(
				boolean smMandatoryRejectedReportTypeView) {
			this.smMandatoryRejectedReportTypeView = smMandatoryRejectedReportTypeView;
		}
		public boolean isReportCategoryView() {
			return reportCategoryView;
		}
		public void setReportCategoryView(boolean reportCategoryView) {
			this.reportCategoryView = reportCategoryView;
		}
		public boolean isSmApplicationReceivedDateView() {
			return smApplicationReceivedDateView;
		}
		public void setSmApplicationReceivedDateView(
				boolean smApplicationReceivedDateView) {
			this.smApplicationReceivedDateView = smApplicationReceivedDateView;
		}
		public boolean isSmBothSupplierTypeView() {
			return smBothSupplierTypeView;
		}
		public void setSmBothSupplierTypeView(boolean smBothSupplierTypeView) {
			this.smBothSupplierTypeView = smBothSupplierTypeView;
		}
		public boolean isSmReportTypeTwoView() {
			return smReportTypeTwoView;
		}
		public void setSmReportTypeTwoView(boolean smReportTypeTwoView) {
			this.smReportTypeTwoView = smReportTypeTwoView;
		}
		
		public boolean isSmProcessingOfficerNameView() {
			return smProcessingOfficerNameView;
		}
		public void setSmProcessingOfficerNameView(boolean smProcessingOfficerNameView) {
			this.smProcessingOfficerNameView = smProcessingOfficerNameView;
		}
		public boolean isSmReceiptNoView() {
			return smReceiptNoView;
		}
		public void setSmReceiptNoView(boolean smReceiptNoView) {
			this.smReceiptNoView = smReceiptNoView;
		}
		public boolean isSmProcessingTypeView() {
			return smProcessingTypeView;
		}
		public void setSmProcessingTypeView(boolean smProcessingTypeView) {
			this.smProcessingTypeView = smProcessingTypeView;
		}
		public boolean isSmPaymentMethodView() {
			return smPaymentMethodView;
		}
		public void setSmPaymentMethodView(boolean smPaymentMethodView) {
			this.smPaymentMethodView = smPaymentMethodView;
		}
		public String getSmProcessingOfficerNameVar() {
			return smProcessingOfficerNameVar;
		}
		public void setSmProcessingOfficerNameVar(String smProcessingOfficerNameVar) {
			this.smProcessingOfficerNameVar = smProcessingOfficerNameVar;
		}
		public String getSmReportTypeVar() {
			return smReportTypeVar;
		}
		public void setSmReportTypeVar(String smReportTypeVar) {
			this.smReportTypeVar = smReportTypeVar;
		}
		public String getSmReceiptNoVar() {
			return smReceiptNoVar;
		}
		public void setSmReceiptNoVar(String smReceiptNoVar) {
			this.smReceiptNoVar = smReceiptNoVar;
		}
		public String getSmProcessingTypeVar() {
			return smProcessingTypeVar;
		}
		public void setSmProcessingTypeVar(String smProcessingTypeVar) {
			this.smProcessingTypeVar = smProcessingTypeVar;
		}
		public String getSmPaymentMethodVar() {
			return smPaymentMethodVar;
		}
		public void setSmPaymentMethodVar(String smPaymentMethodVar) {
			this.smPaymentMethodVar = smPaymentMethodVar;
		}
		public boolean isCmMadatoryIndicatorView() {
			return cmMadatoryIndicatorView;
		}
		public void setCmMadatoryIndicatorView(boolean cmMadatoryIndicatorView) {
			this.cmMadatoryIndicatorView = cmMadatoryIndicatorView;
		}
		public boolean isSmActSubmisAllDateView() {
			return smActSubmisAllDateView;
		}
		public void setSmActSubmisAllDateView(boolean smActSubmisAllDateView) {
			this.smActSubmisAllDateView = smActSubmisAllDateView;
		}
		public boolean isSmActiveSubmissiveFromDateView() {
			return smActiveSubmissiveFromDateView;
		}
		public void setSmActiveSubmissiveFromDateView(
				boolean smActiveSubmissiveFromDateView) {
			this.smActiveSubmissiveFromDateView = smActiveSubmissiveFromDateView;
		}
		public boolean isSmMofRegNoTwoView() {
			return smMofRegNoTwoView;
		}
		public void setSmMofRegNoTwoView(boolean smMofRegNoTwoView) {
			this.smMofRegNoTwoView = smMofRegNoTwoView;
		}
		public boolean isSmRegistrationTypeTwoView() {
			return smRegistrationTypeTwoView;
		}
		public void setSmRegistrationTypeTwoView(boolean smRegistrationTypeTwoView) {
			this.smRegistrationTypeTwoView = smRegistrationTypeTwoView;
		}
		public boolean isSmMandatoryApplicationStatusThreeView() {
			return smMandatoryApplicationStatusThreeView;
		}
		public void setSmMandatoryApplicationStatusThreeView(
				boolean smMandatoryApplicationStatusThreeView) {
			this.smMandatoryApplicationStatusThreeView = smMandatoryApplicationStatusThreeView;
		}
		public boolean isSmMandatoryApplicationStatusTwoView() {
			return smMandatoryApplicationStatusTwoView;
		}
		public void setSmMandatoryApplicationStatusTwoView(
				boolean smMandatoryApplicationStatusTwoView) {
			this.smMandatoryApplicationStatusTwoView = smMandatoryApplicationStatusTwoView;
		}
		public boolean isSmIdentficationTypeView() {
			return smIdentficationTypeView;
		}
		public void setSmIdentficationTypeView(boolean smIdentficationTypeView) {
			this.smIdentficationTypeView = smIdentficationTypeView;
		}
		public boolean isSmIdentificationNumberView() {
			return smIdentificationNumberView;
		}
		public void setSmIdentificationNumberView(boolean smIdentificationNumberView) {
			this.smIdentificationNumberView = smIdentificationNumberView;
		}
		public boolean isSmCancellationByView() {
			return smCancellationByView;
		}
		public void setSmCancellationByView(boolean smCancellationByView) {
			this.smCancellationByView = smCancellationByView;
		}
		public String getSmIdentificationTypeVar() {
			return smIdentificationTypeVar;
		}
		public void setSmIdentificationTypeVar(String smIdentificationTypeVar) {
			this.smIdentificationTypeVar = smIdentificationTypeVar;
		}
		public String getSmIdentificationNumberVar() {
			return smIdentificationNumberVar;
		}
		public void setSmIdentificationNumberVar(String smIdentificationNumberVar) {
			this.smIdentificationNumberVar = smIdentificationNumberVar;
		}
		public String getSmCancellationByVar() {
			return smCancellationByVar;
		}
		public void setSmCancellationByVar(String smCancellationByVar) {
			this.smCancellationByVar = smCancellationByVar;
		}
		public boolean isSmPersonnelRoleView() {
			return smPersonnelRoleView;
		}
		public void setSmPersonnelRoleView(boolean smPersonnelRoleView) {
			this.smPersonnelRoleView = smPersonnelRoleView;
		}
		public String getSmPersonnelRoleVar() {
			return smPersonnelRoleVar;
		}
		public void setSmPersonnelRoleVar(String smPersonnelRoleVar) {
			this.smPersonnelRoleVar = smPersonnelRoleVar;
		}
		public boolean isSmMandatoryMofRegNoFromView() {
			return smMandatoryMofRegNoFromView;
		}
		public void setSmMandatoryMofRegNoFromView(boolean smMandatoryMofRegNoFromView) {
			this.smMandatoryMofRegNoFromView = smMandatoryMofRegNoFromView;
		}
		public boolean isSmMandatoryMofRegNoToView() {
			return smMandatoryMofRegNoToView;
		}
		public void setSmMandatoryMofRegNoToView(boolean smMandatoryMofRegNoToView) {
			this.smMandatoryMofRegNoToView = smMandatoryMofRegNoToView;
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
		public boolean isSmPaidUpCapitalFrmView() {
			return smPaidUpCapitalFrmView;
		}
		public void setSmPaidUpCapitalFrmView(boolean smPaidUpCapitalFrmView) {
			this.smPaidUpCapitalFrmView = smPaidUpCapitalFrmView;
		}
		public boolean isSmPaidUpCapitalToView() {
			return smPaidUpCapitalToView;
		}
		public void setSmPaidUpCapitalToView(boolean smPaidUpCapitalToView) {
			this.smPaidUpCapitalToView = smPaidUpCapitalToView;
		}
		public boolean isSmPaidUpCapitalNaccumView() {
			return smPaidUpCapitalNaccumView;
		}
		public void setSmPaidUpCapitalNaccumView(boolean smPaidUpCapitalNaccumView) {
			this.smPaidUpCapitalNaccumView = smPaidUpCapitalNaccumView;
		}
		public String getSmMofRegNoFromVar() {
			return smMofRegNoFromVar;
		}
		public void setSmMofRegNoFromVar(String smMofRegNoFromVar) {
			this.smMofRegNoFromVar = smMofRegNoFromVar;
		}
		public String getSmMofRegNoToVar() {
			return smMofRegNoToVar;
		}
		public void setSmMofRegNoToVar(String smMofRegNoToVar) {
			this.smMofRegNoToVar = smMofRegNoToVar;
		}
		public String getSmPaidUpCapitalFrmVar() {
			return smPaidUpCapitalFrmVar;
		}
		public void setSmPaidUpCapitalFrmVar(String smPaidUpCapitalFrmVar) {
			this.smPaidUpCapitalFrmVar = smPaidUpCapitalFrmVar;
		}
		public String getSmPaidUpCapitalToVar() {
			return smPaidUpCapitalToVar;
		}
		public void setSmPaidUpCapitalToVar(String smPaidUpCapitalToVar) {
			this.smPaidUpCapitalToVar = smPaidUpCapitalToVar;
		}
		public String getSmPaidUpCapitalNaccumVar() {
			return smPaidUpCapitalNaccumVar;
		}
		public void setSmPaidUpCapitalNaccumVar(String smPaidUpCapitalNaccumVar) {
			this.smPaidUpCapitalNaccumVar = smPaidUpCapitalNaccumVar;
		}
		public boolean isSmRoleView() {
			return smRoleView;
		}
		public void setSmRoleView(boolean smRoleView) {
			this.smRoleView = smRoleView;
		}
		public boolean isSmEpNoView() {
			return smEpNoView;
		}
		public void setSmEpNoView(boolean smEpNoView) {
			this.smEpNoView = smEpNoView;
		}
		public boolean isSmRegistrationNoView() {
			return smRegistrationNoView;
		}
		public void setSmRegistrationNoView(boolean smRegistrationNoView) {
			this.smRegistrationNoView = smRegistrationNoView;
		}
		public boolean isSmSupportingDocumentModeView() {
			return smSupportingDocumentModeView;
		}
		public void setSmSupportingDocumentModeView(boolean smSupportingDocumentModeView) {
			this.smSupportingDocumentModeView = smSupportingDocumentModeView;
		}
		public boolean isSmSupportingDocumentStatusView() {
			return smSupportingDocumentStatusView;
		}
		public void setSmSupportingDocumentStatusView(
				boolean smSupportingDocumentStatusView) {
			this.smSupportingDocumentStatusView = smSupportingDocumentStatusView;
		}
		public String getSmRoleVar() {
			return smRoleVar;
		}
		public void setSmRoleVar(String smRoleVar) {
			this.smRoleVar = smRoleVar;
		}
		public String getSmEpNoVar() {
			return smEpNoVar;
		}
		public void setSmEpNoVar(String smEpNoVar) {
			this.smEpNoVar = smEpNoVar;
		}
		public String getSmRegistrationNoVar() {
			return smRegistrationNoVar;
		}
		public void setSmRegistrationNoVar(String smRegistrationNoVar) {
			this.smRegistrationNoVar = smRegistrationNoVar;
		}
		public String getSmSupportingDocumentModeVar() {
			return smSupportingDocumentModeVar;
		}
		public void setSmSupportingDocumentModeVar(String smSupportingDocumentModeVar) {
			this.smSupportingDocumentModeVar = smSupportingDocumentModeVar;
		}
		public String getSmSupportingDocumentStatusVar() {
			return smSupportingDocumentStatusVar;
		}
		public void setSmSupportingDocumentStatusVar(
				String smSupportingDocumentStatusVar) {
			this.smSupportingDocumentStatusVar = smSupportingDocumentStatusVar;
		}
		public boolean isPpYearView() {
			return ppYearView;
		}
		public void setPpYearView(boolean ppYearView) {
			this.ppYearView = ppYearView;
		}
		public boolean isPpMinistryView() {
			return ppMinistryView;
		}
		public void setPpMinistryView(boolean ppMinistryView) {
			this.ppMinistryView = ppMinistryView;
		}
		public boolean isPpPTJView() {
			return ppPTJView;
		}
		public void setPpPTJView(boolean ppPTJView) {
			this.ppPTJView = ppPTJView;
		}
		public String getPpYearVar() {
			return ppYearVar;
		}
		public void setPpYearVar(String ppYearVar) {
			this.ppYearVar = ppYearVar;
		}
		public String getPpMinistryVar() {
			return ppMinistryVar;
		}
		public void setPpMinistryVar(String ppMinistryVar) {
			this.ppMinistryVar = ppMinistryVar;
		}
		public String getPpPTJVar() {
			return ppPTJVar;
		}
		public void setPpPTJVar(String ppPTJVar) {
			this.ppPTJVar = ppPTJVar;
		}
		public boolean isSmMandatoryCreatedFromDateView() {
			return smMandatoryCreatedFromDateView;
		}
		public void setSmMandatoryCreatedFromDateView(
				boolean smMandatoryCreatedFromDateView) {
			this.smMandatoryCreatedFromDateView = smMandatoryCreatedFromDateView;
		}
		public boolean isScPurchaseInDefaultStatusView() {
			return scPurchaseInDefaultStatusView;
		}
		public void setScPurchaseInDefaultStatusView(
				boolean scPurchaseInDefaultStatusView) {
			this.scPurchaseInDefaultStatusView = scPurchaseInDefaultStatusView;
		}
		public String getScPurchaseInDefaultStatusVar() {
			return scPurchaseInDefaultStatusVar;
		}
		public void setScPurchaseInDefaultStatusVar(String scPurchaseInDefaultStatusVar) {
			this.scPurchaseInDefaultStatusVar = scPurchaseInDefaultStatusVar;
		}
		public boolean isScSimpleQuoteDtlGroupView() {
			return scSimpleQuoteDtlGroupView;
		}
		public void setScSimpleQuoteDtlGroupView(boolean scSimpleQuoteDtlGroupView) {
			this.scSimpleQuoteDtlGroupView = scSimpleQuoteDtlGroupView;
		}
		public String getScSimpleQuoteDtlGroupVar() {
			return scSimpleQuoteDtlGroupVar;
		}
		public void setScSimpleQuoteDtlGroupVar(String scSimpleQuoteDtlGroupVar) {
			this.scSimpleQuoteDtlGroupVar = scSimpleQuoteDtlGroupVar;
		}
		public boolean isScSimpleQuoteStatusView() {
			return scSimpleQuoteStatusView;
		}
		public void setScSimpleQuoteStatusView(boolean scSimpleQuoteStatusView) {
			this.scSimpleQuoteStatusView = scSimpleQuoteStatusView;
		}
		public String getScSimpleQuoteStatusVar() {
			return scSimpleQuoteStatusVar;
		}
		public void setScSimpleQuoteStatusVar(String scSimpleQuoteStatusVar) {
			this.scSimpleQuoteStatusVar = scSimpleQuoteStatusVar;
		}
		public boolean isScRequestNoteStatusView() {
			return scRequestNoteStatusView;
		}
		public void setScRequestNoteStatusView(boolean scRequestNoteStatusView) {
			this.scRequestNoteStatusView = scRequestNoteStatusView;
		}
		public String getScRequestNoteStatusVar() {
			return scRequestNoteStatusVar;
		}
		public void setScRequestNoteStatusVar(String scRequestNoteStatusVar) {
			this.scRequestNoteStatusVar = scRequestNoteStatusVar;
		}
		public boolean isApprovedDateView() {
			return approvedDateView;
		}
		public void setApprovedDateView(boolean approvedDateView) {
			this.approvedDateView = approvedDateView;
		}
		public boolean isCtSubTypeView() {
			return ctSubTypeView;
		}
		public void setCtSubTypeView(boolean ctSubTypeView) {
			this.ctSubTypeView = ctSubTypeView;
		}
		public String getCtSubTypeVar() {
			return ctSubTypeVar;
		}
		public void setCtSubTypeVar(String ctSubTypeVar) {
			this.ctSubTypeVar = ctSubTypeVar;
		}
		public boolean isCtUserTypeView() {
			return ctUserTypeView;
		}
		public void setCtUserTypeView(boolean ctUserTypeView) {
			this.ctUserTypeView = ctUserTypeView;
		}
		public String getCtUserTypeVar() {
			return ctUserTypeVar;
		}
		public void setCtUserTypeVar(String ctUserTypeVar) {
			this.ctUserTypeVar = ctUserTypeVar;
		}
		public boolean isCtLOAPhysicalNoView() {
			return ctLOAPhysicalNoView;
		}
		public void setCtLOAPhysicalNoView(boolean ctLOAPhysicalNoView) {
			this.ctLOAPhysicalNoView = ctLOAPhysicalNoView;
		}
		public String getCtLOAPhysicalNoVar() {
			return ctLOAPhysicalNoVar;
		}
		public void setCtLOAPhysicalNoVar(String ctLOAPhysicalNoVar) {
			this.ctLOAPhysicalNoVar = ctLOAPhysicalNoVar;
		}
		public boolean isCtStatusTwoView() {
			return ctStatusTwoView;
		}
		public void setCtStatusTwoView(boolean ctStatusTwoView) {
			this.ctStatusTwoView = ctStatusTwoView;
		}
		public String getCtStatusTwoVar() {
			return ctStatusTwoVar;
		}
		public void setCtStatusTwoVar(String ctStatusTwoVar) {
			this.ctStatusTwoVar = ctStatusTwoVar;
		}
		public boolean isCtGroupThreeView() {
			return ctGroupThreeView;
		}
		public void setCtGroupThreeView(boolean ctGroupThreeView) {
			this.ctGroupThreeView = ctGroupThreeView;
		}
		public String getCtGroupThreeVar() {
			return ctGroupThreeVar;
		}
		public void setCtGroupThreeVar(String ctGroupThreeVar) {
			this.ctGroupThreeVar = ctGroupThreeVar;
		}
		public boolean isCtPhysicalContractNoView() {
			return ctPhysicalContractNoView;
		}
		public void setCtPhysicalContractNoView(boolean ctPhysicalContractNoView) {
			this.ctPhysicalContractNoView = ctPhysicalContractNoView;
		}
		public String getCtPhysicalContractNoVar() {
			return ctPhysicalContractNoVar;
		}
		public void setCtPhysicalContractNoVar(String ctPhysicalContractNoVar) {
			this.ctPhysicalContractNoVar = ctPhysicalContractNoVar;
		}
		public boolean isFlGroupSeventeenView() {
			return flGroupSeventeenView;
		}
		public void setFlGroupSeventeenView(boolean flGroupSeventeenView) {
			this.flGroupSeventeenView = flGroupSeventeenView;
		}
		public String getFlGroupSeventeenVar() {
			return flGroupSeventeenVar;
		}
		public void setFlGroupSeventeenVar(String flGroupSeventeenVar) {
			this.flGroupSeventeenVar = flGroupSeventeenVar;
		}
		public boolean isEnableCriteriaMenuView() {
			return enableCriteriaMenuView;
		}
		public void setEnableCriteriaMenuView(boolean enableCriteriaMenuView) {
			this.enableCriteriaMenuView = enableCriteriaMenuView;
		}
		public boolean isFlSupplierTextView() {
			return flSupplierTextView;
		}
		public void setFlSupplierTextView(boolean flSupplierTextView) {
			this.flSupplierTextView = flSupplierTextView;
		}
		public boolean isFlSupplierDropdownView() {
			return flSupplierDropdownView;
		}
		public void setFlSupplierDropdownView(boolean flSupplierDropdownView) {
			this.flSupplierDropdownView = flSupplierDropdownView;
		}
		public boolean isSmApplicationDateView() {
			return smApplicationDateView;
		}
		public void setSmApplicationDateView(boolean smApplicationDateView) {
			this.smApplicationDateView = smApplicationDateView;
		}
		public boolean isSmMandatoryDateView() {
			return smMandatoryDateView;
		}
		public void setSmMandatoryDateView(boolean smMandatoryDateView) {
			this.smMandatoryDateView = smMandatoryDateView;
		}
		public boolean isSmNonMandatoryDateView() {
			return smNonMandatoryDateView;
		}
		public void setSmNonMandatoryDateView(boolean smNonMandatoryDateView) {
			this.smNonMandatoryDateView = smNonMandatoryDateView;
		}
		public boolean isSmRejectedDateView() {
			return smRejectedDateView;
		}
		public void setSmRejectedDateView(boolean smRejectedDateView) {
			this.smRejectedDateView = smRejectedDateView;
		}
		public boolean isSmRegisteredDateView() {
			return smRegisteredDateView;
		}
		public void setSmRegisteredDateView(boolean smRegisteredDateView) {
			this.smRegisteredDateView = smRegisteredDateView;
		}
		public boolean isSmScheduleSiteVisitDateView() {
			return smScheduleSiteVisitDateView;
		}
		public void setSmScheduleSiteVisitDateView(boolean smScheduleSiteVisitDateView) {
			this.smScheduleSiteVisitDateView = smScheduleSiteVisitDateView;
		}
		public boolean isSmActivationDateView() {
			return smActivationDateView;
		}
		public void setSmActivationDateView(boolean smActivationDateView) {
			this.smActivationDateView = smActivationDateView;
		}
		public boolean isSmSubmissionDateView() {
			return smSubmissionDateView;
		}
		public void setSmSubmissionDateView(boolean smSubmissionDateView) {
			this.smSubmissionDateView = smSubmissionDateView;
		}
		public boolean isSmExpiryDateView() {
			return smExpiryDateView;
		}
		public void setSmExpiryDateView(boolean smExpiryDateView) {
			this.smExpiryDateView = smExpiryDateView;
		}
		public boolean isSmCreatedDateView() {
			return smCreatedDateView;
		}
		public void setSmCreatedDateView(boolean smCreatedDateView) {
			this.smCreatedDateView = smCreatedDateView;
		}
		public boolean isSmMandatoryReceivedFromDateView() {
			return smMandatoryReceivedFromDateView;
		}
		public void setSmMandatoryReceivedFromDateView(
				boolean smMandatoryReceivedFromDateView) {
			this.smMandatoryReceivedFromDateView = smMandatoryReceivedFromDateView;
		}
		public boolean isMadatoryIndicatorView() {
			return madatoryIndicatorView;
		}
		public void setMadatoryIndicatorView(boolean madatoryIndicatorView) {
			this.madatoryIndicatorView = madatoryIndicatorView;
		}
		public boolean isCtProcurementModeView() {
			return ctProcurementModeView;
		}
		public void setCtProcurementModeView(boolean ctProcurementModeView) {
			this.ctProcurementModeView = ctProcurementModeView;
		}
		public String getCtProcurementModeVar() {
			return ctProcurementModeVar;
		}
		public void setCtProcurementModeVar(String ctProcurementModeVar) {
			this.ctProcurementModeVar = ctProcurementModeVar;
		}
		public List<SelectItem> getLevOneCatLst1() {
			return levOneCatLst1;
		}
		public void setLevOneCatLst1(List<SelectItem> levOneCatLst1) {
			this.levOneCatLst1 = levOneCatLst1;
		}		
		public static Logger getLog() {
			return log;
		}
		public String getCmExtentionBrandVar() {
			return cmExtentionBrandVar;
		}
		public void setCmExtentionBrandVar(String cmExtentionBrandVar) {
			this.cmExtentionBrandVar = cmExtentionBrandVar;
		}
		public String getCmExtentionTypeVar() {
			return cmExtentionTypeVar;
		}
		public void setCmExtentionTypeVar(String cmExtentionTypeVar) {
			this.cmExtentionTypeVar = cmExtentionTypeVar;
		}
		public String getCmExtentionMeasurmentVar() {
			return cmExtentionMeasurmentVar;
		}
		public void setCmExtentionMeasurmentVar(String cmExtentionMeasurmentVar) {
			this.cmExtentionMeasurmentVar = cmExtentionMeasurmentVar;
		}
		public String getCmExtentionColourVar() {
			return cmExtentionColourVar;
		}
		public void setCmExtentionColourVar(String cmExtentionColourVar) {
			this.cmExtentionColourVar = cmExtentionColourVar;
		}
		public boolean isSmYearTextView() {
			return smYearTextView;
		}
		public void setSmYearTextView(boolean smYearTextView) {
			this.smYearTextView = smYearTextView;
		}
		public String getSmYearTextVar() {
			return smYearTextVar;
		}
		public void setSmYearTextVar(String smYearTextVar) {
			this.smYearTextVar = smYearTextVar;
		}
		public boolean isCmGroupOneView() {
			return cmGroupOneView;
		}
		public void setCmGroupOneView(boolean cmGroupOneView) {
			this.cmGroupOneView = cmGroupOneView;
		}
		public String getCmGroupOneVar() {
			return cmGroupOneVar;
		}
		public void setCmGroupOneVar(String cmGroupOneVar) {
			this.cmGroupOneVar = cmGroupOneVar;
		}
		public boolean isCtMinistryView() {
			return ctMinistryView;
		}
		public void setCtMinistryView(boolean ctMinistryView) {
			this.ctMinistryView = ctMinistryView;
		}
		public boolean isCtPtjView() {
			return ctPtjView;
		}
		public void setCtPtjView(boolean ctPtjView) {
			this.ctPtjView = ctPtjView;
		}
		public String getCtMinistryVar() {
			return ctMinistryVar;
		}
		public void setCtMinistryVar(String ctMinistryVar) {
			this.ctMinistryVar = ctMinistryVar;
		}
		public String getCtPtjVar() {
			return ctPtjVar;
		}
		public void setCtPtjVar(String ctPtjVar) {
			this.ctPtjVar = ctPtjVar;
		}
		public boolean isCtLoanNoView() {
			return ctLoanNoView;
		}
		public void setCtLoanNoView(boolean ctLoanNoView) {
			this.ctLoanNoView = ctLoanNoView;
		}
		public String getCtLoanNoVar() {
			return ctLoanNoVar;
		}
		public void setCtLoanNoVar(String ctLoanNoVar) {
			this.ctLoanNoVar = ctLoanNoVar;
		}
		public boolean isCtProductServiceView() {
			return ctProductServiceView;
		}
		public void setCtProductServiceView(boolean ctProductServiceView) {
			this.ctProductServiceView = ctProductServiceView;
		}
		public String getCtProductServiceVar() {
			return ctProductServiceVar;
		}
		public void setCtProductServiceVar(String ctProductServiceVar) {
			this.ctProductServiceVar = ctProductServiceVar;
		}
		public boolean isCtGroupOneView() {
			return ctGroupOneView;
		}
		public void setCtGroupOneView(boolean ctGroupOneView) {
			this.ctGroupOneView = ctGroupOneView;
		}
		public boolean isCtGroupTwoView() {
			return ctGroupTwoView;
		}
		public void setCtGroupTwoView(boolean ctGroupTwoView) {
			this.ctGroupTwoView = ctGroupTwoView;
		}
		public String getCtGroupOneVar() {
			return ctGroupOneVar;
		}
		public void setCtGroupOneVar(String ctGroupOneVar) {
			this.ctGroupOneVar = ctGroupOneVar;
		}
		public String getCtGroupTwoVar() {
			return ctGroupTwoVar;
		}
		public void setCtGroupTwoVar(String ctGroupTwoVar) {
			this.ctGroupTwoVar = ctGroupTwoVar;
		}
		public boolean isCmExtentionView() {
			return cmExtentionView;
		}
		public void setCmExtentionView(boolean cmExtentionView) {
			this.cmExtentionView = cmExtentionView;
		}
		public String getCmExtentionVar() {
			return cmExtentionVar;
		}
		public void setCmExtentionVar(String cmExtentionVar) {
			this.cmExtentionVar = cmExtentionVar;
		}
		public long getLngUserId() {
			return lngUserId;
		}
		public void setLngUserId(long lngUserId) {
			this.lngUserId = lngUserId;
		}
		public String getCmExtentionCodeVar() {
			return cmExtentionCodeVar;
		}
		public void setCmExtentionCodeVar(String cmExtentionCodeVar) {
			this.cmExtentionCodeVar = cmExtentionCodeVar;
		}
		public boolean isCmSupplOrAdminView() {
			return cmSupplOrAdminView;
		}
		public void setCmSupplOrAdminView(boolean cmSupplOrAdminView) {
			this.cmSupplOrAdminView = cmSupplOrAdminView;
		}
		public String getCmSupplOrAdminVar() {
			return cmSupplOrAdminVar;
		}
		public void setCmSupplOrAdminVar(String cmSupplOrAdminVar) {
			this.cmSupplOrAdminVar = cmSupplOrAdminVar;
		}
		public boolean isSmMandatoryBothSupplierTypeView() {
			return smMandatoryBothSupplierTypeView;
		}
		public void setSmMandatoryBothSupplierTypeView(
				boolean smMandatoryBothSupplierTypeView) {
			this.smMandatoryBothSupplierTypeView = smMandatoryBothSupplierTypeView;
		}
		public boolean isSmMandatoryReportTypeView() {
			return smMandatoryReportTypeView;
		}
		public void setSmMandatoryReportTypeView(boolean smMandatoryReportTypeView) {
			this.smMandatoryReportTypeView = smMandatoryReportTypeView;
		}
		public boolean isSmMandatoryApplicationNoView() {
			return smMandatoryApplicationNoView;
		}
		public void setSmMandatoryApplicationNoView(boolean smMandatoryApplicationNoView) {
			this.smMandatoryApplicationNoView = smMandatoryApplicationNoView;
		}
		public boolean isSmSVOUserView() {
			return smSVOUserView;
		}
		public void setSmSVOUserView(boolean smSVOUserView) {
			this.smSVOUserView = smSVOUserView;
		}
		public String getSmSVOUserVar() {
			return smSVOUserVar;
		}
		public void setSmSVOUserVar(String smSVOUserVar) {
			this.smSVOUserVar = smSVOUserVar;
		}
		public String getSmMandatoryCreatedFromYearVar() {
			return smMandatoryCreatedFromYearVar;
		}
		public void setSmMandatoryCreatedFromYearVar(
				String smMandatoryCreatedFromYearVar) {
			this.smMandatoryCreatedFromYearVar = smMandatoryCreatedFromYearVar;
		}
		public String getSmMandatoryCreatedToYearVar() {
			return smMandatoryCreatedToYearVar;
		}
		public void setSmMandatoryCreatedToYearVar(String smMandatoryCreatedToYearVar) {
			this.smMandatoryCreatedToYearVar = smMandatoryCreatedToYearVar;
		}
		public List<String> getYearList() {
			return yearList;
		}
		public void setYearList(List<String> yearList) {
			this.yearList = yearList;
		}		
		public List<SelectItem> getMonthList() {
			return monthList;
		}		
		
		public HashMap<String, String> getMonthMap() {
			return monthMap;
		}
		public void setMonthMap(HashMap<String, String> monthMap) {
			this.monthMap = monthMap;
		}
		public void setMonthList(List<SelectItem> monthList) {
			this.monthList = monthList;
		}
		public boolean isSmMandatoryStaticByStateView() {
			return smMandatoryStaticByStateView;
		}
		public void setSmMandatoryStaticByStateView(boolean smMandatoryStaticByStateView) {
			this.smMandatoryStaticByStateView = smMandatoryStaticByStateView;
		}
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
		
		public boolean isSmRegistrationTypeView() {
			return smRegistrationTypeView;
		}
		public void setSmRegistrationTypeView(boolean smRegistrationTypeView) {
			this.smRegistrationTypeView = smRegistrationTypeView;
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
		
		public boolean isSmRegistrationStatusView() {
			return smRegistrationStatusView;
		}
		public void setSmRegistrationStatusView(boolean smRegistrationStatusView) {
			this.smRegistrationStatusView = smRegistrationStatusView;
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
		
		public boolean isSmApplicationStatusView() {
			return smApplicationStatusView;
		}
		public void setSmApplicationStatusView(boolean smApplicationStatusView) {
			this.smApplicationStatusView = smApplicationStatusView;
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
		public boolean isSmCompanyNameView() {
			return smCompanyNameView;
		}
		public void setSmCompanyNameView(boolean smCompanyNameView) {
			this.smCompanyNameView = smCompanyNameView;
		}
		
		public boolean isSmReportTypeView() {
			return smReportTypeView;
		}
		public void setSmReportTypeView(boolean smReportTypeView) {
			this.smReportTypeView = smReportTypeView;
		}
		
		public boolean isSmExtraTwoColumnView() {
			return smExtraTwoColumnView;
		}
		public void setSmExtraTwoColumnView(boolean smExtraTwoColumnView) {
			this.smExtraTwoColumnView = smExtraTwoColumnView;
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
		
		public String getRegistrationStatusTotVar() {
			return registrationStatusTotVar;
		}
		public void setRegistrationStatusTotVar(String registrationStatusTotVar) {
			this.registrationStatusTotVar = registrationStatusTotVar;
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
		
		public String getApplicationStatusTotVar() {
			return applicationStatusTotVar;
		}
		public void setApplicationStatusTotVar(String applicationStatusTotVar) {
			this.applicationStatusTotVar = applicationStatusTotVar;
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
		
		public String getReportTypeTotVar() {
			return reportTypeTotVar;
		}
		public void setReportTypeTotVar(String reportTypeTotVar) {
			this.reportTypeTotVar = reportTypeTotVar;
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
			IStateSO stateSO	=	BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);		
			StateReqDTO	stateReqDTO	=	new StateReqDTO();
			stateReqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			//stateReqDTO.setCountryId(1L);//TODO malaysia		
			StateResDTO resStateDTO	=	stateSO.retriveStatesListByCountryCode(stateReqDTO);		
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
			IDistrictSO	districtSO	=	BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
			DistrictReqDTO	districtReqDTO	=	new DistrictReqDTO();
			if(state!=null&&state!=""){			
				districtReqDTO.setStateId( Long.parseLong(state));
				DistrictResDTO districtResDTO = districtSO.retriveDistrictListByStateID(districtReqDTO);			
				for(PmDistrict pmDistrict: districtResDTO.getDistrictList()){				
					SelectItem selectItem= new SelectItem();
					selectItem.setLabel(pmDistrict.getDistrictName());
					selectItem.setValue(pmDistrict.getDistrictId());
					districtLst.add(selectItem);
				}
			}
		}
		public List<SelectItem> retrieveCategoryLevOne(){
			levOneCatLst = new ArrayList<SelectItem>();		
			ICategoryCodeSO categoryCodeSO	=	BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
			//CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
			//CategoryCodeResDTO	catCodeResDTO	=	new CategoryCodeResDTO();
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL1List();		
			for(PmCategoryL1 catOne: catCodeResDTO.getCategoryL1List()){			
				SelectItem selectItem= new SelectItem();
				selectItem.setLabel(catOne.getCategoryL1Code()+" - "+catOne.getCategoryName());
				selectItem.setValue(catOne.getCategoryL1Id());
				levOneCatLst.add(selectItem);			
			}
			return levOneCatLst;
		}
		
		public void retrieveCategoryLevOne1(){
			levOneCatLst1 = retrieveCategoryLevOne();
		}
		
		
		public List<String> generateYearList(){
			yearList = new ArrayList<String>();
			 for (int i = 1950; i <= 2150; i++) {
				 yearList.add(Integer.toString(i));
			 }
			 return yearList; 
		}
		public List<SelectItem> generateCommonMonthList(){
			monthList = new ArrayList<SelectItem>();		
			String month = ReportConst.EMPTY_VAL;
			int monthNoInt;
			String monthNoStr= ReportConst.EMPTY_VAL;
	        DateFormatSymbols dfs = new DateFormatSymbols();
	        String[] months = dfs.getMonths();        
	       for (int i = 0; i <= 11; i++) {
	    	   monthNoInt= i+1;
	    	   if(monthNoInt<10){
	    		   monthNoStr=ReportConst.ZERO_VAL+monthNoInt;
	    	   }else{
	    		   monthNoStr=monthNoInt+ReportConst.EMPTY_VAL;
	    	   }
	            month = months[i];            
	            SelectItem selectItem= new SelectItem();
	            selectItem.setLabel(month);
	            selectItem.setValue(monthNoStr);	            
	            monthList.add(selectItem);
	            monthMap.put(monthNoStr, month);
	        }      
	        return monthList;
		}
		public List<String> generateCommonYearList(){
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			
			Date currentDate = new Date();
			Integer year = null;
			String yearFrom=outputDateFormat.format(currentDate).substring(6, 10);
			if(yearFrom!=null){
				year = Integer.parseInt(yearFrom);
			}
			yearList = new ArrayList<String>();
			if(year!=null){
			 for (int i = year-7; i <= year; i++) {
				 yearList.add(Integer.toString(i));
			 }
			}
			 return yearList; 
		}
}
