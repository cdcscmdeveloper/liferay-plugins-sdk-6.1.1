package my.ep.web.backingbean.report;


import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;

import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.constant.report.CommonMethod;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="smReportBackingBean")
@ViewScoped
public class SMReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{
	
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();	

	public List<SelectItem> getStateLst() {
		return stateLst;
	}
	public void setStateLst(List<SelectItem> stateLst) {
		this.stateLst = stateLst;
	}
	public List<SelectItem> getLevOneCatLst() {
		return levOneCatLst;
	}
	public void setLevOneCatLst(List<SelectItem> levOneCatLst) {
		this.levOneCatLst = levOneCatLst;
	}
	/*
	 *Enable SM module criteria by report name 
	 */
	public void enableCriteriaVariablesByReportBySM(ReportsGenerateBean rptGenBean,	List<String> authoriztionList){
		switch (getManagedBean().getReportValue()) {
		
		case ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL:		        	
				rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);							
            break;
		case ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL:
				rptGenBean.retrieveCategoryLevOne();				
				rptGenBean.retrieveState();
				rptGenBean.setSmMandatoryCategoryView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);	        	
	        	rptGenBean.setSmMandatoryRegistrationStatusView(true);	        	
	        	rptGenBean.setSmMandatoryFromDateView(true);						
	        break;
		case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:	
			    rptGenBean.generateYearList();
				rptGenBean.setSmMandatoryCreatedFromYearView(true);				
            break;
		case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
				rptGenBean.retrieveCategoryLevOne();				
				rptGenBean.retrieveState();	
				rptGenBean.setSmMandatoryCategoryView(true);
	        	rptGenBean.setSmMandatoryStaticByStateView(true);
            break;
		 case ReportConst.SM_APPLICATION_VERIFICATION_RPT_VAL:		        	
			 	//rptGenBean.setSmReceiveDateView(true);
			 	rptGenBean.setSmMandatoryFromDateView(true);			
            break;
		 case ReportConst.SM_APPLICATION_APPROVED_RPT_VAL:		        	
	        	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break;
		 case ReportConst.SM_APPLICATION_REJECTED_RPT_VAL:		        	
			 	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
	            break;
		 case ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL:					
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveCategoryLevOne();
				rptGenBean.setSmCategoryView(true);
				rptGenBean.setSmCompanyNameView(true);					
	            break;
		 case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:
			    rptGenBean.generateYearList();
	        	rptGenBean.setSmExtraTwoColumnView(true);
				rptGenBean.setSmMandatoryReportTypeView(true);
				rptGenBean.setSmMandatoryFromDateView(true);
				rptGenBean.setSmMandatoryCreatedFromYearView(true);				
	            break;
		 case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break;
		 case ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL:	
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);				
	            break;
		 case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:	
			    rptGenBean.setSmMandatoryCompanyNameView(true);
				rptGenBean.setSmMandatoryMofRegNoView(true);
				rptGenBean.setSmMandatoryRegistrationTypeView(true);
				rptGenBean.setSmMandatoryFromDateView(true);					
	            break;
		 case ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL:
	           	rptGenBean.setSmExtraTwoColumnView(true);	        	
	           	rptGenBean.setSmMandatoryZoneView(true);							
	            break;
		 case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmMandatoryReportTypeView(true);
				rptGenBean.setSmMandatoryFromDateView(true);				
	            break;
		 case ReportConst.SM_SUPPL_STAT_RPT_VAL:
			    rptGenBean.retrieveState();
			    rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDistrictView(true);						
	            break;
		 case ReportConst.SM_QUESTIONAIRE_RPT_VAL:	
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryBothSupplierTypeView(true);
	        	rptGenBean.setSmReportTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);					
	            break;
		 case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:								
	            break;
		 case ReportConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL:
			 	rptGenBean.setSmCompanyNameView(true);
			 	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	            break;
		 case ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL:
			 	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);						
	            break;
		 case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL:
			    rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);					
	            break;	            
		 case ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL:
			    rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);					
	            break;
		 case ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL:
			    rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);					
	            break;
		 case ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL:
			    rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);					
	            break;
		 case ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL:
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryBothSupplierTypeView(true);
	        	rptGenBean.setSmReportTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);					
	            break;
		 case ReportConst.SM_PAY_REGIS_FEE_RPT_VAL:
			    rptGenBean.setSmCompanyNameView(true);	        	
	        	rptGenBean.setSmApplicationTypeView(true);
	        	rptGenBean.setSmPaymentTypeView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break; 
		 case ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL:
			    rptGenBean.retrieveCategoryLevOne();
			    rptGenBean.retrieveState();
				rptGenBean.setSmMandatoryCategoryView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryCompanyNameView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);
	        	rptGenBean.setSmMandatoryApplicationNoView(true);
	            break;
		 case ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL:
			    rptGenBean.retrieveState();
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryCompanyNameView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);
	        	rptGenBean.setSmSVOUserView(true);
	        	rptGenBean.setSmMandatoryApplicationNoView(true);
	            break;
		 case ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL:
			    rptGenBean.retrieveState();
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);
	        	rptGenBean.setSmSVOUserView(true);
	        	rptGenBean.setSmMandatoryApplicationNoView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusView(true);
	            break;	            
	            
	            
	        /*case ReportConst.SM_LST_OF_APP_RPT_VAL:
	        	rptGenBean.setSmSupportingDocumentView(true);
				rptGenBean.setSmDocStatusView(true);
				rptGenBean.setSmSuppDocStatusView(true);
				rptGenBean.setSmRegistrationNoView(true);
				rptGenBean.setSmEpNoView(true);
				rptGenBean.setSmApplicationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_APP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL:	        	
	        	rptGenBean.setSmRegistrationNoView(true);
				rptGenBean.setSmEpNoView(true);
				rptGenBean.setSmApplicationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_NAME));
				
	            break;
	        case ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_VAL:			
				rptGenBean.setSmReleaseDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_NAME));			
	            break;*/
	        
	        /*case ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL:
	        	//rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	//rptGenBean.setSmMofRegNoFromView(true);
	        	//rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);
	        	rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmRegistrationStatusView(true);
	        	rptGenBean.setSmPaidUpCapitalNaccumShareView(true);
	        	rptGenBean.setSmCapitalNaccumView(true);
	        	rptGenBean.setSmCategoryView(true);
				rptGenBean.setFromDateView(true); 
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveCategoryLevOne();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_NAME));			
	            break;*/
	        
	        /*case ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL:
	        	
	        	//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmCompanyNameView(true);	        	
	        	rptGenBean.setSmProofIdentityView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_VAL:
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL:
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmSupplierTypeView(true);	        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_VAL:	
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmProofIdentityView(true);	        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_VAL:	        	
	        	//rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	//rptGenBean.setSmApplicationStatusView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusView(true);
	        	rptGenBean.setSmCancelledByView(true);
	        	//rptGenBean.setSmApplicationDateView(true);
	        	rptGenBean.setSmMandatoryApplicationDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_VAL:	        	
	        	rptGenBean.setSmPaymentTypeView(true);
	        	rptGenBean.setSmSupplierView(true); 
	        	rptGenBean.setSmApplicationTypeView(true);	  
	        	rptGenBean.setSmApplicationDateView(true);		
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_NAME));			
	            break;*/	        
	       /* case ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_VAL:		        	
	        	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmCategoryView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveCategoryLevOne();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME));			
	            break;	       
	        case ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL:	        	
				//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
				rptGenBean.setSmCompanyNameView(true);
				//rptGenBean.setSmExpiryDateView(true);
				rptGenBean.setSmMandatoryExpiryDateView(true);
				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_PRE_EXP_COMP_RPT_NAME));			
	            break;       
	        case ReportConst.SM_LST_COMP_ADDR_RPT_VAL:	
	        	//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmCompApplStatusView(true);
				//rptGenBean.setSmPrintedDateView(true);	
	        	rptGenBean.setSmMandatoryPrintedDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMP_ADDR_RPT_NAME));			
	            break;        
	        case ReportConst.SM_BPK_USER_PROF_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmZoneView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_BPK_USER_PROF_RPT_NAME));			
	            break;
	        case ReportConst.SM_BPK_ADMIN_PROF_RPT_VAL:	
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmZoneView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_BPK_ADMIN_PROF_RPT_NAME));			
	            break;
	        case ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_VAL:	
	        	//rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmOrganizationTypeView(true);
	        	rptGenBean.setSmKementerianView(true);
	        	rptGenBean.setSmNamaAgensiView(true);
	        	//rptGenBean.setSmCompApplStatusView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusView(true);
	        	rptGenBean.setSmCompanyStatusView(true);
				rptGenBean.setSmMandatoryApplicationDateView(true);
				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_NAME));			
	            break;
	        case ReportConst.SM_AGENCY_STAT_SUMM_RPT_VAL:		        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_AGENCY_STAT_SUMM_RPT_NAME));			
	            break;	        
	        case ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME));			
	            break;        
	        case ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL:
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDocStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveState();			
	            break;
	       /* case ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmProofIdentityView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_VAL:	        		        	
	        	rptGenBean.setSmEpNoView(true);
	        	rptGenBean.setSmMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_NAME));			
	            break;
	        case ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_VAL:	        		        	
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmSupplierNameView(true);
	        	rptGenBean.setSmCategoryCodeView(true); 
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPP_PEROFRM_RPT_VAL:	        		        	
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmSupplierNameView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_PEROFRM_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_VAL:	        		        	
	        	rptGenBean.setSmApplicationStatusView(true);
	        	rptGenBean.setSmSupplierNameView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_VAL:	        		        	
	        	rptGenBean.setSmSiteVisitOfficerView(true);
	        	rptGenBean.setSmSupplierNameView(true);
	         	rptGenBean.setSmStateView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveState();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_VAL:	        		        	
	        	rptGenBean.setSmSupplierNameView(true);
	         	rptGenBean.setSmStateView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				rptGenBean.retrieveState();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_DETAILS_OF_APPLICATION_RPT_VAL:	        	
	        	rptGenBean.setSmApplicationNoView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_DETAILS_OF_APPLICATION_RPT_NAME));			
	            break;*/
	        default: 
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
	            //break;
		}
	}

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

}
