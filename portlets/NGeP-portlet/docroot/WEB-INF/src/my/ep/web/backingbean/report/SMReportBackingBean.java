/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

// TODO: Auto-generated Javadoc

/**
 * The Class SMReportBackingBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "smReportBackingBean")
@ViewScoped
public class SMReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {
	
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(SMReportBackingBean.class);

	/** The state lst. */
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();

	/** The lev one cat lst. */
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();

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
	 * enableCriteriaVariablesByReportBySM - Enable criteria variables of report
	 * by SUPPLIER MANAGEMENT.
	 * 
	 * @param rptGenBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	public void enableCriteriaVariablesByReportBySM(
			ReportsGenerateBean rptGenBean, List<String> authoriztionList) {
		log.info("SMReportBackingBean-enableCriteriaVariablesByReportBySM");
		switch (getManagedBean().getReportValue().trim()) {

		case ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL:			
			rptGenBean.setSmProfileRegCompanyView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmPRMandatorySupplierTypeView(true);
			rptGenBean.setSmPRMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL:			
			// rptGenBean.retrieveState();
			rptGenBean.setSmCategoryStatisticView(true);
			rptGenBean.setSmPSSupplierTypeView(true);
			rptGenBean.setSmBusCompanyRegistrationStatusView(true);
			rptGenBean.retrieveCategoryLevOne1();
			rptGenBean.setSmCategoryBySupplierTypeView(true);		
			/*rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmRegisteredDateView(true);*/
			rptGenBean.setSmExtraTwoColumnView(true);

			break;
		case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:
			rptGenBean.generateCommonYearList();
			rptGenBean.setSmMandatoryCreatedFromYearView(true);
			rptGenBean.setSmMandatoryRegisteredReportTypeDateView(true);
			break;
		case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
			rptGenBean.retrieveCategoryLevOne1();		
			rptGenBean.setSmCategoryStatisticByStateView(true);
			rptGenBean.setSmMainCatMandatoryCategoryView(true);
			rptGenBean.setSmStateView(true);
			rptGenBean.setSmDistrictView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_APPLICATION_VERIFICATION_RPT_VAL:
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmApplicationReceivedDateView(true);
			break;
		case ReportConst.SM_APPLICATION_APPROVED_RPT_VAL:
			rptGenBean.setSmApplicationApprovedView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmCDCompanyNameView(true);
			rptGenBean.setSmARMofRegNoView(true);
			rptGenBean.setApprovedDateView(true);

			break;
		case ReportConst.SM_APPLICATION_REJECTED_RPT_VAL:
			rptGenBean.setSmApplicationRejectedView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmApplicationTypeSixView(true);			
			rptGenBean.setSmCDCompanyNameView(true);
			rptGenBean.setSmARMofRegNoView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmRejectedDateView(true);

			break;
		case ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL:
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmScheduleSiteVisitDateView(true);
			rptGenBean.retrieveCategoryLevOne1();
			rptGenBean.setSmNonMandatoryCategoryView(true);
			rptGenBean.setSmPPCompanyNameView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:			
			rptGenBean.setSmTotalNoOfYearlyARView(true);
			rptGenBean.generateCommonMonthList();
			rptGenBean.generateCommonYearList();
			getManagedBean().setMonthMap(rptGenBean.getMonthMap());
			rptGenBean.setSmExtraTwoColumnView(true);
			// rptGenBean.setSmMandatoryReportTypeView(true);
			rptGenBean.setSmFromMonthView(true);
			rptGenBean.setSmMandatoryCreatedFromYearView(true);
			rptGenBean.setSmMandatoryYearFromReportTypeView(true);
			// rptGenBean.setSmMandatoryFromDateView(true);
			// rptGenBean.setSmMandatoryDateView(true);
			// rptGenBean.setSmMandatoryCreatedFromYearView(true);
			break;
		case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:			
			rptGenBean.setSmMonthlyReportView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmMandatoryMonthlyRptByYearView(true);
			rptGenBean.setSmExtraTwoColumnTwoView(true);
			break;
		case ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL:
			rptGenBean.setSmExtraTwoColumnView(true);
			rptGenBean.setSmMandatoryAllSupplierTypeView(true);
			rptGenBean.setSmMandatoryReceivedFromDateView(true);
			break;
		case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:
			rptGenBean.setSmListOfDailyRegisteredApplicationView(true);
			rptGenBean.setSmCDCompanyNameView(true);
			rptGenBean.setSmMofRegNoTwoView(true);
			rptGenBean.setSmRegistrationTypeTwoView(true);
			rptGenBean.setSmActiveSubmissiveFromDateView(true);
			rptGenBean.setSmActSubmisAllDateView(true);
			break;
		case ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL:			
			rptGenBean.setSmSupplierStatisticByCategoryView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmLCMOFRegisteredDateView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			break;
		case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:	
			rptGenBean.setSmTotalNoOfDailyARView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			rptGenBean.setSmTNMandatoryReportTypeView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmTNApplicationDateView(true);
			break;
		case ReportConst.SM_SUPPL_STAT_RPT_VAL:
			/*
			 * rptGenBean.setSmMandatoryFromDateView(true);
			 * rptGenBean.setSmMandatoryDateView(true);
			 */
			 rptGenBean.setSmSupplierStatisticView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmMOFRegisteredDateView(true);
			rptGenBean.setSmSupStateView(true);
			rptGenBean.setSmGenDistrictView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_QUESTIONAIRE_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatoryBothSupplierTypeView(true);
			rptGenBean.setSmReportTypeView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmMandatoryDateView(true);
			break;
		case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:
			rptGenBean.setSmMandatoryPaymentTypeView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmPaymentDateView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL:	
			rptGenBean.setSmListOfExpiredCompanyView(true);
			rptGenBean.setSmLPCompanyNameView(true);
			rptGenBean.setSmECMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmExpiryDateView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL:
			rptGenBean.setSmQRSView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmQRAllSupplierTypeView(true);
			rptGenBean.setSmReportTypeView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmCreatedDateView(true);
			break;
		case ReportConst.SM_PAY_REGIS_FEE_RPT_VAL:			
			rptGenBean.setSmListOfPPRFView(true);
			rptGenBean.setSmGenCompanyNameView(true);			
			rptGenBean.setSmApplicationTypeView(true);
			rptGenBean.setSmPaymentTypeView(true);
			/*
			 * rptGenBean.setFromDateView(true); rptGenBean.setToDateView(true);
			 */
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmPaymentPRDateView(true);
			break;
		case ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL:
			rptGenBean.retrieveCategoryLevOne1();			
			rptGenBean.setSmListOfSOAView(true);
			rptGenBean.setSmMainCatMandatoryCategoryView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmCompanyNameView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmFromDateView(true);
			rptGenBean.setSmSupStateView(true);
			rptGenBean.setSmGenDistrictView(true);
			rptGenBean.setSmSAApplicationNoView(true);
			break;
		case ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL:
			rptGenBean.setSmListOfSVOSVLView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatoryCompanyNameView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmMandatoryExpectedSiteVisiRpttDateView(true);
			rptGenBean.setSmMandatoryStateView(true);
			rptGenBean.setSmSVMandatoryDistrictView(true);
			rptGenBean.setSmSVOUserView(true);
			rptGenBean.setSmMandatoryApplicationNoView(true);
			break;
		case ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL:
			rptGenBean.setSmListOfSOPPSVView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			// rptGenBean.setSmMandatoryFromDateView(true);
			// rptGenBean.setSmMandatoryDateView(true);
			rptGenBean.setSmSVMandatoryStateView(true);
			rptGenBean.setSmMandatoryDistrictView(true);
			// rptGenBean.setSmSVOUserView(true);
			rptGenBean.setSmSVMandatoryApplicationNoView(true);
			rptGenBean.setSmMandatoryApplicationStatusView(true);
			break;
		case ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL:
			// rptGenBean.retrieveState();			
			rptGenBean.setSmListOfApplicationView(true);
			rptGenBean.setSmSupplierTypeView(true);
			rptGenBean.setSmLAApplicationNoView(true);
			rptGenBean.setSmApplicationStatusView(true);
			rptGenBean.setSmEpNoView(true);
			rptGenBean.setSmLARegistrationNoView(true);
			rptGenBean.setSmSupportingDocumentModeView(true);
			rptGenBean.setSmSupportingDocumentStatusView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmApplicationDateView(true);
			break;
		case ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL:			
			rptGenBean.setSmListOfOfflineSDPSView(true);
			rptGenBean.setSmOFApplicationNoView(true);
			rptGenBean.setSmEpNoView(true);
			rptGenBean.setSmRegistrationNoView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmActiveSubmissiveFromDateView(true);
			rptGenBean.setSmPendingSubmissionDateView(true);
			break;
		case ReportConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL:			
			rptGenBean.setSmVirtualCertificateRelasedListView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmLPCompanyNameView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmVirtualCertDateView(true);

			break;
		case ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL:			
			rptGenBean.setSmListOfCompaniesSRSCView(true);
			rptGenBean.retrieveCategoryLevOne1();
			rptGenBean.setSmNonMandatoryCategoryView(true);
			rptGenBean.setSmSuppCompanyNameView(true);
			rptGenBean.setSmLCBusCompanyRegistrationStatusView(true);
			//rptGenBean.setSmRegistrationTypeView(true);
			rptGenBean.setSmLCMandatorySupplierTypeView(true);
			rptGenBean.setSmBusCompanyTypeView(true);			
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatoryMofRegNoFromView(true);
			rptGenBean.setSmMandatoryMofRegNoToView(true);			
			rptGenBean.setSmBusCompanyPaidUpAccumlatedShareView(true);
			rptGenBean.setSmPaidAccmltdShareCptlSelView(true);
			/*
			 * rptGenBean.setSmPaidUpCapitalFrmView(true);
			 * rptGenBean.setSmPaidUpCapitalToView(true);
			 */
			//rptGenBean.setSmPaidUpCapitalNaccumView(true);

			break;
		case ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL:
			rptGenBean.setSmListOfCompaniesWithDOHEView(true);
			rptGenBean.setSmGenCompanyNameView(true);
			rptGenBean.setSmMandatoryDOCSupplierTypeView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmMofRegNoFromView(true);
			rptGenBean.setSmMofRegNoToView(true);
			rptGenBean.setSmPersonnelRoleView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmMOFRegisteredDateView(true);
			break;
		case ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL:			
			rptGenBean.setSmListOfDOHEView(true);
			rptGenBean.setSmDOCompanyNameView(true);
			rptGenBean.setSmDOHMandatorySupplierTypeView(true);
			rptGenBean.setSmMofRegNoFromView(true);
			rptGenBean.setSmMofRegNoToView(true);
			rptGenBean.setSmGenIdentificationNumberView(true);
			break;
		case ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL:
			//rptGenBean.setSmDOHCompanyNameView(true);
			rptGenBean.setSmListOfCompaniesSortAlphabetView(true);
			rptGenBean.setSmComSupplierTypeView(true);
			rptGenBean.setSmLCMofRegNoFromView(true);
			rptGenBean.setSmMofRegNoToView(true);
			rptGenBean.setSmGenCompanyNameView(true);
			
			/*
			 * rptGenBean.setSmMandatoryMofRegNoFromView(true);
			 * rptGenBean.setSmMandatoryMofRegNoToView(true);
			 */
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmExtraTwoColumnTwoView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmLCMOFRegisteredDateView(true);
			rptGenBean.setSmMOFAccountEndDateView(true);
			break;
		case ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL:			
			rptGenBean.setSmStatisticListCBCView(true);
			rptGenBean.setSmQRAllSupplierTypeView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			/*
			 * rptGenBean.setFromDateView(true); rptGenBean.setToDateView(true);
			 */
			rptGenBean.setSmSLBusCompanyTypeView(true);	
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmMOFRegisteredDateView(true);
			break;
		case ReportConst.SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL:
			// rptGenBean.setSmIdentficationTypeView(true);			
			rptGenBean.setSmListPSMoreView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			rptGenBean.setSmPSSupplierTypeView(true);
			rptGenBean.setSmCompanyNameView(true);
			rptGenBean.setSmMofRegNoFromView(true);
			rptGenBean.setSmMofRegNoToView(true);
			rptGenBean.setSmPSIdentificationNumberView(true);
			rptGenBean.setSmPersonnelStaffRoleView(true);

			/*
			 * rptGenBean.setSmCompanyNameView(true);
			 * rptGenBean.setSmMandatorySupplierTypeView(true);
			 * rptGenBean.setSmMofRegNoFromView(true);
			 * rptGenBean.setSmMofRegNoToView(true);
			 * rptGenBean.setSmIdentificationNumberView(true);
			 */

			/*
			 * rptGenBean.setFromDateView(true); rptGenBean.setToDateView(true);
			 */
			/*
			 * rptGenBean.setSmNonMandatoryDateView(true);
			 * rptGenBean.setSmMOFRegisteredDateView(true);
			 */
			break;
		case ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL:
			rptGenBean.setSmListDeleteMOFAccountView(true);
			rptGenBean.setSmCanMandatorySupplierTypeView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmMandatoryApplicationStatusTwoView(true);
			rptGenBean.setSmCancellationByView(true);
			/*
			 * rptGenBean.setSmMandatoryFromDateView(true);
			 * rptGenBean.setSmMandatoryDateView(true);
			 */
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmCanApplicationDateView(true);
			break;
		case ReportConst.SM_COMPANY_ADDRESS_LIST_RPT_VAL:			
			rptGenBean.setSmCompanyAddressListView(true);
			rptGenBean.setSmCDMandatoryAllSupplierTypeView(true);
			rptGenBean.setSmApplicationTypeFiveView(true);
			rptGenBean.setSmMandatoryApplicationStatusThreeView(true);
			rptGenBean.setSmMandatorySupportingDocumentModeView(true);
			rptGenBean.setSmCDCompanyNameView(true);
			/*
			 * rptGenBean.setSmMandatoryFromDateView(true);
			 * rptGenBean.setSmMandatoryDateView(true);
			 */
			break;
			//This Report Moved to PM_008
		case ReportConst.SM_BPK_USER_PROFILE_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
			//This Report Moved to PM_009
		case ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL:
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmProfileStatusView(true);
			// rptGenBean.setSmMandatoryZoneView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_DAILY_EARNING_RECEIPT:			
			rptGenBean.setSmDailyReceiptsESView(true);
			rptGenBean.setSmFromDateView(true);
			rptGenBean.setSmPaymentMethodView(true);
			// rptGenBean.setSmProcessingTypeView(true);
			rptGenBean.setSmDERSmReceiptNoView(true);
			rptGenBean.setSmCompanyNameView(true);
			rptGenBean.setSmPaymentTypeThreeView(true);
			rptGenBean.setSmPaymentToView(true);
			break;
		case ReportConst.SM_DELETE_PENDING_APPL:
			rptGenBean.setSmDeletePendingApplicationView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmFromDateView(true);
			rptGenBean.setSmGenCompanyNameView(true);
			// rptGenBean.setSmBothMandatorySupplierTypeView(true);
			rptGenBean.setSmDEMandatoryAllSupplierTypeView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmDelApplicationNoView(true);
			rptGenBean.setSmReportTypeTwoView(true);
			rptGenBean.setSmProcessingOfficerNameView(true);
			break;
		case ReportConst.SM_SUPPLIER_PERFORMANCE:			
			rptGenBean.setSmSupplierPerformanceView(true);
			rptGenBean.setSmRegistrationTypeView(true);
			rptGenBean.setSmSupplierView(true);
			rptGenBean.setSmEpNoView(true);
			rptGenBean.setSmLAMofRegNoView(true);
			rptGenBean.setSmFromAndToYearView(true);
			rptGenBean.setSmYearFromView(true);
			/*
			 * rptGenBean.setSmCompanyNameView(true);
			 * rptGenBean.setSmMofRegNoTwoView(true);
			 * rptGenBean.setSmRegistrationTypeTwoView(true);
			 * rptGenBean.setSmActiveSubmissiveFromDateView(true);
			 * rptGenBean.setSmActSubmisAllDateView(true);
			 */
			break;
		case ReportConst.SM_PERSON_BRANCH_BANK:			
			rptGenBean.setSmListOfSOCPBAView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			// rptGenBean.setSmMandatorySupplierTypeView(true);
			rptGenBean.setSmSCPSupplierTypeView(true);
			// rptGenBean.setSmSupplierView(true);
			rptGenBean.setSmRegistrationNoView(true);
			// rptGenBean.setSmMofRegNoView(true);
			rptGenBean.setSmRegistrationTypeView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmMOFRegisteredDateView(true);
			break;
		case ReportConst.SM_THRESHOLD:			
			rptGenBean.setSmThresholdOrSupplierTransactionView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptGenBean.setSmZoneView(true);
			rptGenBean.setSmTHMandatorySupplierTypeView(true);
			rptGenBean.setSmSupplierView(true);
			rptGenBean.setSmTHRegistrationTypeView(true);
			rptGenBean.setSmLARegistrationNoView(true);
			rptGenBean.setSmEpMofRegNoView(true);
			rptGenBean.setSmFromAndToYearView(true);
			rptGenBean.setSmThresholdYearFromView(true);
			break;
		case ReportConst.SM_LIST_OF_USERS:
			rptGenBean.setSmListOfUsersSCRView(true);
			rptGenBean.setSmRequestExpiryDateView(true);
			rptGenBean.setSmUserNameView(true);
			rptGenBean.setSmIdentificationNumberView(true);
			rptGenBean.setSmSupplierView(true);
			//rptGenBean.setSmSuppCompanyNameView(true);
			rptGenBean.setSmRegistrationNoView(true);
			rptGenBean.setSmMofRegNoView(true);
			checkAuthorizationForSuppliers(rptGenBean, authoriztionList);
			break;
		case ReportConst.SM_DAILY_APP_SUBMITTED:
			// rptGenBean.retrieveState();
			rptGenBean.setSmListOfDailyApplicationBasicAndMOFView(true);
			rptGenBean.setSmDARegistrationTypeView(true);
			rptGenBean.setSmStaticStateView(true);
			rptGenBean.setSmApplicationStatusView(true);
			rptGenBean.setSmActiveSubmissiveFromDateView(true);
			rptGenBean.setSmSubmissionDateView(true);
			break;
		case ReportConst.SM_THRESHOLD_VALUE:
			/*
			 * rptGenBean.setSmSupplierView(true);
			 * rptGenBean.setSmRegistrationNoView(true);
			 * rptGenBean.setSmMofRegNoView(true);
			 */
			
			rptGenBean.setSmListOfOfflineSDPSView(true);
			rptGenBean.setSmFromAndToYearView(true);
			rptGenBean.setSmThresholdValYearFromView(true);
			break;
		case ReportConst.SM_LIST_OF_APPL_STATUS:			
			rptGenBean.setSmListOfApplicationStatusView(true);
			rptGenBean.setSmLAApplicationNoView(true);
			rptGenBean.setSmApplicationTypeTwoView(true);
			//rptGenBean.setSmSupplierView(true);
			rptGenBean.setSmRegistrationNoView(true);
			//rptGenBean.setSmLAMofRegNoView(true);
			rptGenBean.setSmMofRegNoView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmApplicationDateView(true);
			checkAuthorizationForSuppliers(rptGenBean, authoriztionList);
			break;
		case ReportConst.SM_AGENCY_STATISTICAL:
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmApprovalDateView(true);
			break;
		case ReportConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER:
			rptGenBean.setSmListOfGSTRegSupplierView(true);
			rptGenBean.setSmCommonZoneView(true);
			rptGenBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptGenBean.setSmGSTSupplierTypeView(true);
			rptGenBean.setSmGSTRegistrationTypeView(true);
			rptGenBean.setSmCompanyNameView(true);
			rptGenBean.setSmGSRegistrationNoView(true);
			rptGenBean.setSmGSTRegistrationNoView(true);
			rptGenBean.setSmMandatoryGSTRegisteredStatusView(true);
			rptGenBean.setSmMandatoryFromDateView(true);
			rptGenBean.setSmGSTEffectiveDateView(true);
			rptGenBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL:			
			rptGenBean.setSmCollectionReceiptTransationView(true);
			rptGenBean.setSmMandatoryReciptTransactionReportTypeView(true);
			rptGenBean.setSmMandatoryRTDateView(true);
			rptGenBean.setSmMandatoryReceiptDateView(true);
			rptGenBean.setSmDOHCompanyNameView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.SM_COLLECTION_PAYMENT_METHODS_RPT_VAL:			
			rptGenBean.setSmCollectionReportPaymentMethodView(true);
			rptGenBean.setSmMandatoryPaymentMethodView(true);
			rptGenBean.setSmNonMandatoryReciptTransactionReportTypeView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmNonMandatoryTransactionDateView(true);
			rptGenBean.setSmCPSupplierNameView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL:			
			rptGenBean.setSmCollectionReportClasificationCodeView(true);
			rptGenBean.setSmMandatoryForCodeView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmNonMandatoryTransactionDateView(true);
			break;
		case ReportConst.SM_REVENUE_COLLECTION_RPT_VAL:
			rptGenBean.setSmRevenueCollectionReportView(true);
			rptGenBean.setSmNonMandatoryDateView(true);
			rptGenBean.setSmNonMandatoryTransactionDateView(true);
			checkAuthorizationByRoleCode(rptGenBean, authoriztionList);
			break;
		case ReportConst.SM_LIST_DAILY_RECIPT_RPT_VAL:
			rptGenBean.setSmListOfDailyReceiptView(true);
			rptGenBean.setSmMandatoryRTDateView(true);
			rptGenBean.setSmMandatoryTransactionDateView(true);
			rptGenBean.setSmDailyReceiptNoView(true);
			rptGenBean.setSmApplicationNoView(true);
			break;
		default:
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
			// break;
		}
		log.info("End Of SMReportBackingBean-enableCriteriaVariablesByReportBySM");
	}

	private void checkAuthorizationByRoleCode(ReportsGenerateBean rptGenBean,
			List<String> authoriztionList) {
		log.info("SMReportBackingBean-checkAuthorizationByRoleCode");
		if (authoriztionList != null) {
			if (authoriztionList.contains(ReportConst.ROLE_CODE_COLLECTOR_CDC)) {
				rptGenBean.setCollectorCDCRegistrationChkView(true);

			} else {
				rptGenBean.setCollectorGovRegistrationChkView(true);
			}
		}
	}

	private void checkAuthorizationForSuppliers(ReportsGenerateBean rptGenBean,
			List<String> authoriztionList) {
		log.info("SMReportBackingBean-checkAuthorizationForSuppliers");
		if (authoriztionList != null) {
			if (authoriztionList
					.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_ADMIN)
					|| authoriztionList
							.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_USER)
					|| authoriztionList
							.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_USER)
					|| authoriztionList
							.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_ADMIN)) {

				rptGenBean.setSmRegistrationNoView(false);
				rptGenBean.setSmMofRegNoView(false);
			}
		}
	}

	/**
	 * Retrieve state.
	 */
	public void retrieveState() {
		log.info("SMReportBackingBean-retrieveState");
		stateLst = new ArrayList<SelectItem>();
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
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
}
