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
				rptGenBean.retrieveCategoryLevOne1();				
				rptGenBean.retrieveState();
				rptGenBean.setSmMandatoryCategoryView(true);
				rptGenBean.setSmStateView(true);
				rptGenBean.setSmDistrictView(true);	        	
	        	rptGenBean.setSmRegistrationStatusView(true);	        	
	        	rptGenBean.setFromDateView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	
	        break;
		case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:	
			    rptGenBean.generateYearList();
				rptGenBean.setSmMandatoryCreatedFromYearView(true);	
				rptGenBean.setSmMandatoryRegisteredReportTypeDateView(true);	
            break;
		case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
				rptGenBean.retrieveCategoryLevOne1();				
				rptGenBean.retrieveState();	
				rptGenBean.setSmMandatoryCategoryView(true);	        	
				rptGenBean.setSmStaticStateView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
            break;
		 case ReportConst.SM_APPLICATION_VERIFICATION_RPT_VAL:
			 	rptGenBean.setSmMandatoryFromDateView(true);
			 	rptGenBean.setSmApplicationReceivedDateView(true);
            break;
		 case ReportConst.SM_APPLICATION_APPROVED_RPT_VAL:		        	
	        	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoView(true);
				rptGenBean.setApprovedDateView(true);
					
	            break;
		 case ReportConst.SM_APPLICATION_REJECTED_RPT_VAL:		        	
			 	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoView(true);
				rptGenBean.setSmNonMandatoryDateView(true);
				rptGenBean.setSmRejectedDateView(true);
					
	            break;
		 case ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL:					
				rptGenBean.setSmNonMandatoryDateView(true);
				rptGenBean.setSmScheduleSiteVisitDateView(true);
				rptGenBean.retrieveCategoryLevOne1();
				rptGenBean.setSmMandatoryCategoryView(true);
				rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmExtraTwoColumnView(true);
	            break;
		 case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:
			    rptGenBean.generateCommonMonthList();
			    rptGenBean.generateCommonYearList();
			    getManagedBean().setMonthMap(rptGenBean.getMonthMap());
	        	rptGenBean.setSmExtraTwoColumnView(true);
				rptGenBean.setSmMandatoryReportTypeView(true);
				rptGenBean.setSmFromMonthView(true);
				//rptGenBean.setSmMandatoryFromDateView(true);
				//rptGenBean.setSmMandatoryDateView(true);
				//rptGenBean.setSmMandatoryCreatedFromYearView(true);				
	            break;
		 case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:
			    rptGenBean.setSmMandatoryMonthlyRptByYearView(true);
				rptGenBean.setSmExtraTwoColumnView(true);					
	            break;
		 case ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL:	
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryReceivedFromDateView(true);
	            break;
		 case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:	
			    rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoTwoView(true);
				rptGenBean.setSmRegistrationTypeTwoView(true);
				rptGenBean.setSmActiveSubmissiveFromDateView(true);
				rptGenBean.setSmActSubmisAllDateView(true);
	            break;
		 case ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL:
	           	rptGenBean.setSmExtraTwoColumnView(true);	        	
	           	rptGenBean.setSmMandatoryZoneView(true);							
	            break;
		 case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmMandatoryReportTypeView(true);
				rptGenBean.setSmMandatoryFromDateView(true);
				rptGenBean.setSmApplicationDateView(true);
	            break;
		 case ReportConst.SM_SUPPL_STAT_RPT_VAL:
			    rptGenBean.retrieveState();
			    rptGenBean.setSmMandatoryFromDateView(true);
			    rptGenBean.setSmMandatoryDateView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDistrictView(true);
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
	            break;
		 case ReportConst.SM_QUESTIONAIRE_RPT_VAL:	
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryBothSupplierTypeView(true);
	        	rptGenBean.setSmReportTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryDateView(true);
	            break;
		 case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:								
	            break;
		 case ReportConst.SM_LIST_OF_PRE_EXPIRY_COMPANY_RPT_VAL:
			 	rptGenBean.setSmCompanyNameView(true);
			 	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmExpiryDateView(true);
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
	        	rptGenBean.setSmMandatoryCreatedFromDateView(true);					
	            break;
		 case ReportConst.SM_PAY_REGIS_FEE_RPT_VAL:
			    rptGenBean.setSmCompanyNameView(true);	        	
	        	rptGenBean.setSmApplicationTypeView(true);
	        	rptGenBean.setSmPaymentTypeView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break; 
		 case ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL:
			    rptGenBean.retrieveCategoryLevOne1();
			    rptGenBean.retrieveState();
				rptGenBean.setSmMandatoryCategoryView(true);
	        	rptGenBean.setSmZoneView(true);	        	
	        	rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setFromDateView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDistrictView(true);
	        	rptGenBean.setSmApplicationNoView(true);
	            break;
		 case ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL:
			    rptGenBean.retrieveState();
	        	rptGenBean.setSmMandatoryZoneView(true);	        	
	        	rptGenBean.setSmMandatoryCompanyNameView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryDateView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);
	        	rptGenBean.setSmSVOUserView(true);
	        	rptGenBean.setSmMandatoryApplicationNoView(true);
	            break;
		 case ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL:
			    rptGenBean.retrieveState();
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryDateView(true);
	        	rptGenBean.setSmMandatoryStateView(true);
	        	rptGenBean.setSmMandatoryDistrictView(true);
	        	rptGenBean.setSmSVOUserView(true);
	        	rptGenBean.setSmMandatoryApplicationNoView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusView(true);
	            break;	            
		 case ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL:
			    rptGenBean.retrieveState();
	        	rptGenBean.setSmApplicationNoView(true);
	        	rptGenBean.setSmApplicationStatusView(true);
	        	rptGenBean.setSmEpNoView(true);
	        	rptGenBean.setSmRegistrationNoView(true);
	        	rptGenBean.setSmSupportingDocumentModeView(true);
	        	rptGenBean.setSmSupportingDocumentStatusView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
	            break;    
		 case ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL:
			   
	        	rptGenBean.setSmApplicationNoView(true);
	        	rptGenBean.setSmEpNoView(true);
	        	rptGenBean.setSmRegistrationNoView(true);	        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
	            break;
		 case ReportConst.SM_VIRTUAL_DAY_REPORT_RPT_VAL: 
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
	            break;
		 case ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL: 
			    rptGenBean.retrieveCategoryLevOne1();
				rptGenBean.setSmMandatoryCategoryView(true);
				rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setSmRegistrationStatusView(true);
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatoryMofRegNoFromView(true);
	        	rptGenBean.setSmMandatoryMofRegNoToView(true);
	        	rptGenBean.setSmPaidUpCapitalFrmView(true);
	        	rptGenBean.setSmPaidUpCapitalToView(true);
	        	rptGenBean.setSmPaidUpCapitalNaccumView(true);
	        	
	            break;
		 case ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL: 			    
				rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmPersonnelRoleView(true);
	            break;
		 case ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL: 			    
				rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmIdentficationTypeView(true);
	        	rptGenBean.setSmIdentificationNumberView(true);
	            break;    
		 case ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL: 			    
				rptGenBean.setSmCompanyNameView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmZoneView(true);
	            break;
		 case ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL:
	        	rptGenBean.setSmSupplierTypeView(true);	        	
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
	            break;      
		 case ReportConst.SM_LIST_OF_PER_MORE_COMPANY_RPT_VAL:
	        	rptGenBean.setSmIdentficationTypeView(true);
	        	rptGenBean.setSmIdentificationNumberView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
	            break;
		 case ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL:
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusTwoView(true);
	        	rptGenBean.setSmCancellationByView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryDateView(true);
	            break; 
		 case ReportConst.SM_COMPANY_ADDRESS_LIST_RPT_VAL:
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryApplicationStatusThreeView(true);
	        	rptGenBean.setSmMandatoryFromDateView(true);
	        	rptGenBean.setSmMandatoryDateView(true);
	            break;      
		 case ReportConst.SM_BPK_USER_PROFILE_RPT_VAL: 
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
	            break;     
		 case ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL:
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmExtraTwoColumnView(true);
	            break; 
		 case ReportConst.SM_DAILY_EARNING_RECEIPT: 				
			    rptGenBean.setFromDateView(true);
			    rptGenBean.setSmPaymentMethodView(true);
			    rptGenBean.setSmProcessingTypeView(true);
			    rptGenBean.setSmReceiptNoView(true);
	            break;
		 case ReportConst.SM_DELETE_PENDING_APPL: 				
			    rptGenBean.setFromDateView(true);
			    rptGenBean.setSmCompanyNameView(true);
			    rptGenBean.setSmBothSupplierTypeView(true);
			    rptGenBean.setSmZoneView(true);
			    rptGenBean.setSmApplicationNoView(true);
			    rptGenBean.setSmReportTypeTwoView(true);
			    rptGenBean.setSmProcessingOfficerNameView(true);
	            break;
		 case ReportConst.SM_SUPPLIER_PERFORMANCE:	
			    rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmMofRegNoTwoView(true);
				rptGenBean.setSmRegistrationTypeTwoView(true);
				rptGenBean.setSmActiveSubmissiveFromDateView(true);
				rptGenBean.setSmActSubmisAllDateView(true);
				
	            break;
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
}
