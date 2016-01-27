/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.constant.report.CommonMethod;

// TODO: Auto-generated Javadoc
/**
 * The Class CMReportBackingBean.
 *
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="cmReportBackingBean")
@ViewScoped
public class CMReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	
	
	/**
	 * criteriaVariablesByReportByCM - Enable criteria variables by report of CATALOGUE MANAGEMENT.
	 *
	 * @param rptGenBean the rpt gen bean
	 * @param authoriztionList the authoriztion list
	 * @param paramList the param list
	 */
	public void criteriaVariablesByReportByCM(ReportsGenerateBean rptGenBean, List<String> authoriztionList,List<String> paramList){
		
		switch (getManagedBean().getReportValue().trim()) {		
	        case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmPublishedDateView(true);
				
				if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
					rptGenBean.setCmCompanyNameView(false);
					rptGenBean.setCmRegistrationTypeView(false);
					}else {
						rptGenBean.setCmCompanyNameView(true);
						rptGenBean.setCmRegistrationTypeView(true);	
					}
					rptGenBean.setCmGroupOneView(true);
				
	            break;
	        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:	        		
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);		
				
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
					rptGenBean.setCmCompanyNameView(false);
					rptGenBean.setCmRegistrationTypeView(false);
					rptGenBean.setCmCatelogDateView(true);					
					rptGenBean.setCmSubmittedDateView(true);
					}else {
						rptGenBean.setCmCompanyNameView(true);
						rptGenBean.setCmRegistrationTypeView(true);	
						rptGenBean.setCmCatelogDateView(true);		
						rptGenBean.setCmApprovedDateView(true);
					}
					rptGenBean.setCmGroupOneView(true);					
	            break;
	        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);		
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmSubmittedDateView(true);	
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
					rptGenBean.setCmGroupOneView(true);
	            break;
	        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:		
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);			
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmSubmittedDateView(true);
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
					rptGenBean.setCmGroupOneView(true);
	            break;
	        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:	        		
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);	
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmExpiredDateView(true);
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
					rptGenBean.setCmGroupOneView(true);
	            break;
	        case ReportConst.TOT_CAT_ITM_STS_RPT_VAL:	        	
	        	rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmItemValidityDateView(true);
				rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);
	            break;
	        case ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);		
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmItemValidityDateView(true);
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
					//rptGenBean.setCmGroupOneView(true);
	            break;
	        case ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);			
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmPublishedDateView(true);
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
				break;
	        case ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmPublishedDateView(true);
				if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
					rptGenBean.setCmCompanyNameView(false);
					rptGenBean.setCmRegistrationTypeView(false);
					}else {
						rptGenBean.setCmCompanyNameView(true);
						rptGenBean.setCmRegistrationTypeView(true);	
					}
				break;
	        case ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmPublishedDateView(true);
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
				break;
	        case ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setCmCatelogDateView(true);
				rptGenBean.setCmPublishedDateView(true);
				if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
					rptGenBean.setCmCompanyNameView(false);
					rptGenBean.setCmRegistrationTypeView(false);
					}else {
						rptGenBean.setCmCompanyNameView(true);
						rptGenBean.setCmRegistrationTypeView(true);	
					}
				break;
	        case ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break;
	        case ReportConst.SUM_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
				break;
	        case ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
					rptGenBean.setCmItemTypeView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
				break;
	        case ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL:	        					
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmRequestNumberView(true);
					if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
						rptGenBean.setCmCompanyNameView(false);
						rptGenBean.setCmRegistrationTypeView(false);
						}else {
							rptGenBean.setCmCompanyNameView(true);
							rptGenBean.setCmRegistrationTypeView(true);	
						}
				break;
	        case ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				if((paramList!=null)&&(paramList.contains(ReportConst.ROLE_CODE_SUP))){
					rptGenBean.setCmCompanyNameView(false);
					rptGenBean.setCmRegistrationTypeView(false);
					}else {
						rptGenBean.setCmCompanyNameView(true);
						rptGenBean.setCmRegistrationTypeView(true);	
					}
				break;
	        case ReportConst.LST_ITM_CODE_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				/*rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);*/					
				rptGenBean.setCmItemNameView(true);	
				break;	       
	        default: 
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
	            //break;
		}		
	}	
}
