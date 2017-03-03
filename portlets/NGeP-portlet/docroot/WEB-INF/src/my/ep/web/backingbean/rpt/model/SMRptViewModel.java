/*
 * 
 */
package my.ep.web.backingbean.rpt.model;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.report.ReportBackingBean;
import my.ep.web.backingbean.rpt.RptBackingBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class SMRptViewModel{

	private static final Logger log = EpLogger
			.getLogger(SMRptViewModel.class);
	
	protected RptBackingBean rptBackingBean;
	protected String reportValue;
	
	public SMRptViewModel(RptBackingBean rptBackingBean, String reportValue){
		this.rptBackingBean = rptBackingBean;
		this.reportValue = reportValue;
	}
	
	public RptBackingBean getBackingBean(){
		return rptBackingBean;
	}
	
	public String getReportValue(){
		return reportValue;
	}
	
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
	 * @param rptBackingBean
	 *            the rpt gen bean
	 * @param authoriztionList
	 *            the authoriztion list
	 */
	public RptBackingBean enableCriteriaVariablesByReportBySM(List<String> authoriztionList, RptBackingBean rptBackingBean) {
		
		log.info("SMReportBackingBean-enableCriteriaVariablesByReportBySM");
		    rptBackingBean.setGenerateButton(true);		
		switch (reportValue) { 
		

		case ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL:			
			rptBackingBean.setSmProfileRegCompanyView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmPRMandatorySupplierTypeView(true);
			rptBackingBean.setSmPRMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL:			
			// rptBackingBean.retrieveState();
			rptBackingBean.setSmCategoryStatisticView(true);
			rptBackingBean.setSmPSSupplierTypeView(true);
			rptBackingBean.setSmBusCompanyRegistrationStatusView(true);
			rptBackingBean.retrieveCategoryLevOne1();
			rptBackingBean.setSmCategoryBySupplierTypeView(true);
			rptBackingBean.setSmStateView(true);
			rptBackingBean.setSmDistrictView(true);
			/*rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmRegisteredDateView(true);*/
			rptBackingBean.setSmExtraTwoColumnView(true);

			break;
		case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:
			rptBackingBean.generateCommonYearList();
			rptBackingBean.setSmMandatoryCreatedFromYearView(true);
			//rptBackingBean.setSmMandatoryRegisteredReportTypeDateView(true);
			if(rptBackingBean.getStrLanguage().equalsIgnoreCase("en")){
				rptBackingBean.setSmMandatoryRegisteredReportTypeDateView(true);
			}else{
				rptBackingBean.setSmMandatoryRegisteredReportTypeDateViewForMY(true);
			}
			break;
		case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
			rptBackingBean.retrieveCategoryLevOne1();		
			rptBackingBean.setSmCategoryStatisticByStateView(true);
			rptBackingBean.setSmMainCatMandatoryCategoryView(true);
			rptBackingBean.setSmStateView(true);
			rptBackingBean.setSmDistrictView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_APPLICATION_VERIFICATION_RPT_VAL:
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmApplicationReceivedDateView(true);
			break;
		case ReportConst.SM_APPLICATION_APPROVED_RPT_VAL:
			rptBackingBean.setSmApplicationApprovedView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmCDCompanyNameView(true);
			rptBackingBean.setSmARMofRegNoView(true);
			rptBackingBean.setApprovedDateView(true);

			break;
		case ReportConst.SM_APPLICATION_REJECTED_RPT_VAL:
			rptBackingBean.setSmApplicationRejectedView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmApplicationTypeSixView(true);			
			rptBackingBean.setSmCDCompanyNameView(true);
			rptBackingBean.setSmARMofRegNoView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmRejectedDateView(true);

			break;
		case ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL:
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmScheduleSiteVisitDateView(true);
			rptBackingBean.retrieveCategoryLevOne1();
			rptBackingBean.setSmNonMandatoryCategoryView(true);
			rptBackingBean.setSmPPCompanyNameView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:			
			rptBackingBean.setSmTotalNoOfYearlyARView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.generateCommonMonthList(rptBackingBean.getStrLanguage());
			//rptBackingBean.generateCommonMonthList(rptBackingBean.getStrLanguage());
			rptBackingBean.generateCommonYearList();
			//getManagedBean().setMonthMap(rptBackingBean.getMonthMap());
			rptBackingBean.setSmExtraTwoColumnView(true);
			// rptBackingBean.setSmMandatoryReportTypeView(true);
			rptBackingBean.setSmFromMonthView(true);
			rptBackingBean.setSmMandatoryCreatedFromYearView(true);
			//rptBackingBean.setSmMandatoryYearFromReportTypeView(true);
			if(rptBackingBean.getStrLanguage().equalsIgnoreCase("en")){
				rptBackingBean.setSmMandatoryYearFromReportTypeView(true);
			}else{
				rptBackingBean.setSmMandatoryYearFromReportTypeViewForMY(true);
			}
			
			// rptBackingBean.setSmMandatoryFromDateView(true);
			// rptBackingBean.setSmMandatoryDateView(true);
			// rptBackingBean.setSmMandatoryCreatedFromYearView(true);
			break;
		case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:			
			rptBackingBean.setSmMonthlyReportView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmMandatoryMonthlyRptByYearView(true);
			rptBackingBean.setSmExtraTwoColumnTwoView(true);
			break;
		case ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL:
			rptBackingBean.setSmExtraTwoColumnView(true);
			rptBackingBean.setSmMandatoryAllSupplierTypeView(true);
			rptBackingBean.setSmMandatoryReceivedFromDateView(true);
			break;
		case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:
			rptBackingBean.setSmListOfDailyRegisteredApplicationView(true);
			rptBackingBean.setSmCDCompanyNameView(true);
			rptBackingBean.setSmMofRegNoTwoView(true);
			rptBackingBean.setSmRegistrationTypeTwoView(true);
			rptBackingBean.setSmActiveSubmissiveFromDateView(true);
			rptBackingBean.setSmActSubmisAllDateView(true);
			break;
		case ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL:			
			rptBackingBean.setSmSupplierStatisticByCategoryView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmLCMOFRegisteredDateView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			break;
		case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:	
			rptBackingBean.setSmTotalNoOfDailyARView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			//rptBackingBean.setSmTNMandatoryReportTypeView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmTNApplicationDateView(true);
			break;
		case ReportConst.SM_SUPPL_STAT_RPT_VAL:
			/*
			 * rptBackingBean.setSmMandatoryFromDateView(true);
			 * rptBackingBean.setSmMandatoryDateView(true);
			 */
			 rptBackingBean.setSmSupplierStatisticView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmMOFRegisteredDateView(true);
			rptBackingBean.setSmSupStateView(true);
			rptBackingBean.setSmGenDistrictView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_QUESTIONAIRE_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatoryBothSupplierTypeView(true);
			rptBackingBean.setSmReportTypeView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmMandatoryDateView(true);
			break;
		case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:
			rptBackingBean.setSmMandatoryPaymentTypeView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmPaymentDateView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL:	
			rptBackingBean.setSmListOfExpiredCompanyView(true);
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setSmLPCompanyNameView(true);
			rptBackingBean.setSmECMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmExpiryDateView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmMandatoryMofRegNoView(true);
			break;
		case ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL:
			rptBackingBean.setSmQRSView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmQRAllSupplierTypeView(true);
			// rptBackingBean.setSmReportTypeView(true);
                        rptBackingBean.setSmMandatoryReportTypeQRView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmCreatedDateView(true);
			break;
		case ReportConst.SM_PAY_REGIS_FEE_RPT_VAL:			
			rptBackingBean.setSmListOfPPRFView(true);
			rptBackingBean.setSmGenCompanyNameView(true);			
			rptBackingBean.setSmApplicationTypeView(true);
			rptBackingBean.setSmPaymentTypeView(true);
			/*
			 * rptBackingBean.setFromDateView(true); rptBackingBean.setToDateView(true);
			 */
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmPaymentPRDateView(true);
			break;
		case ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL:
			rptBackingBean.retrieveCategoryLevOne1();
			/*rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);*/
			rptBackingBean.setSmListOfSOAView(true);
			//rptBackingBean.setSmMainCatMandatoryCategoryView(true);
			rptBackingBean.setSmNonMandatoryCategoryView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmCompanyNameView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmFromDateView(true);
			rptBackingBean.setSmSupStateView(true);
			rptBackingBean.setSmGenDistrictView(true);
			rptBackingBean.setSmSiteVisitStatusView(true);
			rptBackingBean.setSmSAApplicationNoView(true);
			break;
		case ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL:
			rptBackingBean.setSmListOfSVOSVLView(true);
			/*rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);*/
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			//rptBackingBean.setSmMandatoryCompanyNameView(true);
			rptBackingBean.setSmNonMandatoryCompanyNameView(true);
			rptBackingBean.setSmNonMandatoryStateView(true);
			rptBackingBean.setSmSVNonMandatoryDistrictView(true);
			rptBackingBean.setSmSVONonMandatoryUserView(true);
			rptBackingBean.setSmNonMandatoryApplicationNoView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmNonMandatoryExpectedSiteVisiRpttDateView(true);
			/*rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmMandatoryExpectedSiteVisiRpttDateView(true);*/
			/*rptBackingBean.setSmMandatoryStateView(true);
			rptBackingBean.setSmSVMandatoryDistrictView(true);
			rptBackingBean.setSmSVOUserView(true);
			rptBackingBean.setSmMandatoryApplicationNoView(true);*/
			break;
		case ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL:
			rptBackingBean.setSmListOfSOPPSVView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			// rptBackingBean.setSmMandatoryFromDateView(true);
			// rptBackingBean.setSmMandatoryDateView(true);
			rptBackingBean.setSmSVMandatoryStateView(true);
			rptBackingBean.setSmMandatoryDistrictView(true);
			// rptBackingBean.setSmSVOUserView(true);
			rptBackingBean.setSmSVMandatoryApplicationNoView(true);
			rptBackingBean.setSmMandatoryApplicationStatusView(true);
			break;
		case ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL:
			// rptBackingBean.retrieveState();			
			rptBackingBean.setSmListOfApplicationView(true);
			rptBackingBean.setSmSupplierTypeView(true);
			rptBackingBean.setSmLAApplicationNoView(true);
			rptBackingBean.setSmApplicationStatusView(true);
			rptBackingBean.setSmEpNoView(true);
			rptBackingBean.setSmLARegistrationNoView(true);
			rptBackingBean.setSmSupportingDocumentModeView(true);
			rptBackingBean.setSmSupportingDocumentStatusView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmApplicationDateView(true);
			break;
		case ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL:			
			rptBackingBean.setSmListOfOfflineSDPSView(true);
			rptBackingBean.setSmOFApplicationNoView(true);
			rptBackingBean.setSmEpNoView(true);
			rptBackingBean.setSmRegistrationNoView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmActiveSubmissiveFromDateView(true);
			rptBackingBean.setSmPendingSubmissionDateView(true);
			break;
		case ReportConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL:			
			rptBackingBean.setSmVirtualCertificateRelasedListView(true);
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmLPCompanyNameView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmVirtualCertDateView(true);

			break;
		case ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL:			
			rptBackingBean.setSmListOfCompaniesSRSCView(true);
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.retrieveCategoryLevOne1();
			rptBackingBean.setSmNonMandatoryCategoryView(true);
			rptBackingBean.setSmSuppCompanyNameView(true);
			rptBackingBean.setSmLCBusCompanyRegistrationStatusView(true);
			//rptBackingBean.setSmRegistrationTypeView(true);
			rptBackingBean.setSmLCMandatorySupplierTypeView(true);
			rptBackingBean.setSmBusCompanyTypeView(true);			
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatoryMofRegNoFromView(true);
			rptBackingBean.setSmMandatoryMofRegNoToView(true);			
			rptBackingBean.setSmBusCompanyPaidUpAccumlatedShareView(true);
			rptBackingBean.setSmPaidAccmltdShareCptlSelView(true);
			/*
			 * rptBackingBean.setSmPaidUpCapitalFrmView(true);
			 * rptBackingBean.setSmPaidUpCapitalToView(true);
			 */
			//rptBackingBean.setSmPaidUpCapitalNaccumView(true);

			break;
		case ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL:
			rptBackingBean.setSmListOfCompaniesWithDOHEView(true);
			rptBackingBean.setSmGenCompanyNameView(true);
			rptBackingBean.setSmMandatoryDOCSupplierTypeView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmMofRegNoFromView(true);
			rptBackingBean.setSmMofRegNoToView(true);
			rptBackingBean.setSmPersonnelRoleView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmMOFRegisteredDateView(true);
			break;
		case ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL:			
			rptBackingBean.setSmListOfDOHEView(true);
			rptBackingBean.setSmDOCompanyNameView(true);
			rptBackingBean.setSmDOHMandatorySupplierTypeView(true);
			rptBackingBean.setSmMofRegNoFromView(true);
			rptBackingBean.setSmMofRegNoToView(true);
			rptBackingBean.setSmGenIdentificationNumberView(true);
			break;
		case ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL:
			//rptBackingBean.setSmDOHCompanyNameView(true);
			rptBackingBean.setSmListOfCompaniesSortAlphabetView(true);
			rptBackingBean.setSmComSupplierTypeView(true);
			rptBackingBean.setSmLCMofRegNoFromView(true);
			rptBackingBean.setSmMofRegNoToView(true);
			rptBackingBean.setSmGenCompanyNameView(true);
			
			/*
			 * rptBackingBean.setSmMandatoryMofRegNoFromView(true);
			 * rptBackingBean.setSmMandatoryMofRegNoToView(true);
			 */
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmExtraTwoColumnTwoView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmLCMOFRegisteredDateView(true);
			rptBackingBean.setSmMOFAccountEndDateView(true);
			break;
		case ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL:			
			rptBackingBean.setSmStatisticListCBCView(true);
			rptBackingBean.setSmQRAllSupplierTypeView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			/*
			 * rptBackingBean.setFromDateView(true); rptBackingBean.setToDateView(true);
			 */
			rptBackingBean.setSmSLBusCompanyTypeView(true);	
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmMOFRegisteredDateView(true);
			break;
		case ReportConst.SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL:
			// rptBackingBean.setSmIdentficationTypeView(true);			
			rptBackingBean.setSmListPSMoreView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			rptBackingBean.setSmPSSupplierTypeView(true);
			rptBackingBean.setSmCompanyNameView(true);
			rptBackingBean.setSmMofRegNoFromView(true);
			rptBackingBean.setSmMofRegNoToView(true);
			rptBackingBean.setSmPSIdentificationNumberView(true);
			rptBackingBean.setSmPersonnelStaffRoleView(true);

			/*
			 * rptBackingBean.setSmCompanyNameView(true);
			 * rptBackingBean.setSmMandatorySupplierTypeView(true);
			 * rptBackingBean.setSmMofRegNoFromView(true);
			 * rptBackingBean.setSmMofRegNoToView(true);
			 * rptBackingBean.setSmIdentificationNumberView(true);
			 */

			/*
			 * rptBackingBean.setFromDateView(true); rptBackingBean.setToDateView(true);
			 */
			/*
			 * rptBackingBean.setSmNonMandatoryDateView(true);
			 * rptBackingBean.setSmMOFRegisteredDateView(true);
			 */
			break;
		case ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL:
			rptBackingBean.setSmListDeleteMOFAccountView(true);
			rptBackingBean.setSmCanMandatorySupplierTypeView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmMandatoryApplicationStatusTwoView(true);
			rptBackingBean.setSmCancellationByView(true);
			/*
			 * rptBackingBean.setSmMandatoryFromDateView(true);
			 * rptBackingBean.setSmMandatoryDateView(true);
			 */
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmCanApplicationDateView(true);
			break;
		case ReportConst.SM_COMPANY_ADDRESS_LIST_RPT_VAL:			
			rptBackingBean.setSmCompanyAddressListView(true);
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setSmCDMandatoryAllSupplierTypeView(true);
			rptBackingBean.setSmApplicationTypeFiveView(true);
			rptBackingBean.setSmMandatoryApplicationStatusThreeView(true);
			rptBackingBean.setSmMandatorySupportingDocumentModeView(true);
			rptBackingBean.setSmCDCompanyNameView(true);
			/*
			 * rptBackingBean.setSmMandatoryFromDateView(true);
			 * rptBackingBean.setSmMandatoryDateView(true);
			 */
			break;
			//This Report Moved to PM_008
		case ReportConst.SM_BPK_USER_PROFILE_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
			//This Report Moved to PM_009
		case ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL:
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmProfileStatusView(true);
			// rptBackingBean.setSmMandatoryZoneView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_DAILY_EARNING_RECEIPT:			
			rptBackingBean.setSmDailyReceiptsESView(true);
			rptBackingBean.setSmFromDateView(true);
			rptBackingBean.setSmPaymentMethodView(true);
			// rptBackingBean.setSmProcessingTypeView(true);
			rptBackingBean.setSmDERSmReceiptNoView(true);
			rptBackingBean.setSmCompanyNameView(true);
			rptBackingBean.setSmPaymentTypeThreeView(true);
			rptBackingBean.setSmPaymentToView(true);
			break;
		case ReportConst.SM_DELETE_PENDING_APPL:
			rptBackingBean.setSmDeletePendingApplicationView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmFromDateView(true);
			rptBackingBean.setSmGenCompanyNameView(true);
			// rptBackingBean.setSmBothMandatorySupplierTypeView(true);
			rptBackingBean.setSmDEMandatoryAllSupplierTypeView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmDelApplicationNoView(true);
			rptBackingBean.setSmReportTypeTwoView(true);
			rptBackingBean.setSmProcessingOfficerNameView(true);
			break;
		case ReportConst.SM_SUPPLIER_PERFORMANCE:			
			rptBackingBean.setSmSupplierPerformanceView(true);
			rptBackingBean.setSmRegistrationTypeView(true);
			rptBackingBean.setSmSupplierView(true);
			rptBackingBean.setSmEpNumberView(true);
			rptBackingBean.setSmLAMofRegNoView(true);
			rptBackingBean.setSmFromAndToYearView(true);
			rptBackingBean.setSmYearFromView(true);
			/*
			 * rptBackingBean.setSmCompanyNameView(true);
			 * rptBackingBean.setSmMofRegNoTwoView(true);
			 * rptBackingBean.setSmRegistrationTypeTwoView(true);
			 * rptBackingBean.setSmActiveSubmissiveFromDateView(true);
			 * rptBackingBean.setSmActSubmisAllDateView(true);
			 */
			break;
		case ReportConst.SM_PERSON_BRANCH_BANK:			
			rptBackingBean.setSmListOfSOCPBAView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			// rptBackingBean.setSmMandatorySupplierTypeView(true);
			rptBackingBean.setSmSCPSupplierTypeView(true);
			rptBackingBean.setSmSupplierView(true);
			rptBackingBean.setSmRegistrationNoView(true);
			// rptBackingBean.setSmMofRegNoView(true);
			rptBackingBean.setSmRegistrationTypeView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);			
			rptBackingBean.setSmAllRegisteredDateView(true);
			break;
		case ReportConst.SM_THRESHOLD:			
			rptBackingBean.setSmThresholdOrSupplierTransactionView(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			// rptBackingBean.setSmZoneView(true);
			rptBackingBean.setSmTHMandatorySupplierTypeView(true);
			rptBackingBean.setSmSupplierView(true);
			rptBackingBean.setSmTHRegistrationTypeView(true);
			rptBackingBean.setSmLARegistrationNoView(true);
			rptBackingBean.setSmEpMofRegNoView(true);
			rptBackingBean.setSmFromAndToYearView(true);
			rptBackingBean.setSmThresholdYearFromView(true);
			break;
		case ReportConst.SM_LIST_OF_USERS:
			rptBackingBean.setSmListOfUsersSCRView(true);
			rptBackingBean.setSmRequestExpiryRenewalDateView(true);
			rptBackingBean.setSmUserNameView(true);
			rptBackingBean.setSmIdentificationNumberView(true);
			rptBackingBean.setSmSupplierView(true);
			//rptBackingBean.setSmSuppCompanyNameView(true);
			rptBackingBean.setSmRegistrationNoView(true);
			rptBackingBean.setSmMofRegNoView(true);
			checkAuthorizationForSuppliers(rptBackingBean, authoriztionList);
			break;
		case ReportConst.SM_DAILY_APP_SUBMITTED:
			// rptBackingBean.retrieveState();
			rptBackingBean.setSmListOfDailyApplicationBasicAndMOFView(true);
			rptBackingBean.setSmDARegistrationTypeView(true);
			rptBackingBean.setSmStaticStateView(true);
			rptBackingBean.setSmApplicationStatusView(true);
			rptBackingBean.setSmActiveSubmissiveFromDateView(true);
			rptBackingBean.setSmSubmissionDateView(true);
			break;
		case ReportConst.SM_THRESHOLD_VALUE:
			/*
			 * rptBackingBean.setSmSupplierView(true);
			 * rptBackingBean.setSmRegistrationNoView(true);
			 * rptBackingBean.setSmMofRegNoView(true);
			 */
			
			rptBackingBean.setSmListOfOfflineSDPSView(true);
			rptBackingBean.setSmFromAndToYearView(true);
			rptBackingBean.setSmThresholdValYearFromView(true);
			break;
		case ReportConst.SM_LIST_OF_APPL_STATUS:			
			rptBackingBean.setSmListOfApplicationStatusView(true);
			rptBackingBean.setSmLAApplicationNoView(true);
			rptBackingBean.setSmApplicationTypeTwoView(true);
			//rptBackingBean.setSmSupplierView(true);
			rptBackingBean.setSmRegistrationNoView(true);
			//rptBackingBean.setSmLAMofRegNoView(true);
			rptBackingBean.setSmMofRegNoView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmApplicationDateView(true);
			checkAuthorizationForSuppliers(rptBackingBean, authoriztionList);
			break;
		case ReportConst.SM_AGENCY_STATISTICAL:
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmApprovalDateView(true);
			break;
		case ReportConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER:
			rptBackingBean.setSmListOfGSTRegSupplierView(true);
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setSmCommonZoneView(true);
			rptBackingBean.setSmMandatoryEPAdminPTJAdminZoneView(true);
			rptBackingBean.setSmGSTSupplierTypeView(true);
			rptBackingBean.setSmGSTRegistrationTypeView(true);
			rptBackingBean.setSmCompanyNameView(true);
			rptBackingBean.setSmGSRegistrationNoView(true);
			rptBackingBean.setSmGSTRegistrationNoView(true);
			rptBackingBean.setSmMandatoryGSTRegisteredStatusView(true);
			rptBackingBean.setSmMandatoryFromDateView(true);
			rptBackingBean.setSmGSTEffectiveDateView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;
		case ReportConst.SM_COLLECTION_AND_TRANSACTION_DATE_RPT_VAL:		
			rptBackingBean.setGenerateButton(false);
			rptBackingBean.setConditionGenerateButton(true);
			rptBackingBean.setSmCollectionReceiptTransationView(true);
			rptBackingBean.setSmMandatoryReciptTransactionReportTypeView(true);
			rptBackingBean.setSmMandatoryRTDateView(true);
			rptBackingBean.setSmMandatoryReceiptDateView(true);
			rptBackingBean.setSmDOHCompanyNameView(true);
			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
			break;
		case ReportConst.SM_COLLECTION_PAYMENT_METHODS_RPT_VAL:			
			rptBackingBean.setSmCollectionReportPaymentMethodView(true);
			rptBackingBean.setSmMandatoryPaymentMethodView(true);
			rptBackingBean.setSmNonMandatoryReciptTransactionReportTypeView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmNonMandatoryTransactionDateView(true);
			rptBackingBean.setSmCPSupplierNameView(true);
			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
			break;
		case ReportConst.SM_COLLECTION_CLASSIFICATION_CODE_RPT_VAL:			
			rptBackingBean.setSmCollectionReportClasificationCodeView(true);
			rptBackingBean.setSmMandatoryForCodeView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmNonMandatoryTransactionDateView(true);
			break;
		case ReportConst.SM_REVENUE_COLLECTION_RPT_VAL:
			rptBackingBean.setSmRevenueCollectionReportView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmNonMandatoryTransactionDateView(true);
			checkAuthorizationByRoleCode(rptBackingBean, authoriztionList);
			break;
		case ReportConst.SM_LIST_DAILY_RECIPT_RPT_VAL:
			rptBackingBean.setSmListOfDailyReceiptView(true);
			rptBackingBean.setSmMandatoryRTDateView(true);
			rptBackingBean.setSmMandatoryTransactionDateView(true);
			rptBackingBean.setSmDailyReceiptNoView(true);
			rptBackingBean.setSmApplicationNoView(true);
			break;
		case ReportConst.SM_CATEGORY_OVERLAPPING_RPT_VAL:		
			rptBackingBean.setSmCategoryOverLappingView(true);
			rptBackingBean.setSmPSSupplierTypeView(true);
			rptBackingBean.setSmCategoryBySupplierTypeView(true);
			rptBackingBean.setSmExtraTwoColumnView(true);
			break;			
		case ReportConst.SM_CDCSS_DEPOSIT_ADJUSTMENT_RPT_VAL:
			rptBackingBean.setSmCPCompanyNameView(true);	
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmFromDateView(true);	
			break;
		case ReportConst.SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL:
			rptBackingBean.setSmCPCompanyNameView(true);
			rptBackingBean.setSmNonMandatoryDateView(true);
			rptBackingBean.setSmFromDateView(true);
			break;	
		case ReportConst.SM_REJECTED_SOFTCERT_RPT_VAL:
			rptBackingBean.setSmCPCompanyNameView(true);			
			rptBackingBean.setSmMandatoryForDateView(true);
			rptBackingBean.setSmFromDateView(true);
			break;	
		default:
			throw new EpException(
					CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
			// break;
		}
		
		log.info("End Of SMReportBackingBean-enableCriteriaVariablesByReportBySM");
		
		return rptBackingBean;
	}

	private void checkAuthorizationByRoleCode(RptBackingBean rptBackingBean,
			List<String> authoriztionList) {
		log.info("SMReportBackingBean-checkAuthorizationByRoleCode");
		if (authoriztionList != null) {
			if (authoriztionList.contains(ReportConst.ROLE_CODE_COLLECTOR_CDC)) {
				rptBackingBean.setCollectorCDCRegistrationChkView(true);

			} else {
				rptBackingBean.setCollectorGovRegistrationChkView(true);
			}
		}
	}

	private void checkAuthorizationForSuppliers(RptBackingBean rptBackingBean,
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

				rptBackingBean.setSmRegistrationNoView(false);
				rptBackingBean.setSmMofRegNoView(false);
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
