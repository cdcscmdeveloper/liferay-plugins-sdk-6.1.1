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

/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="cmReportBackingBean")
@ViewScoped
public class CMReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	
	/*
	 *Enable CM module criteria by report name 
	 */
	public void criteriaVariablesByReportByCM(ReportsGenerateBean rptGenBean, List<String> authoriztionList){
		
		switch (getManagedBean().getReportValue()) {		
	        case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmItemStatusView(true);
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
	            break;
	        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:
	        	//rptGenBean.setCmSupplOrAdminView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);						
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					//rptGenBean.setCmProofIdentityView(true);
					//rptGenBean.setCmItemCategoryView(false);
				//};
	            break;
	        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:
	        	//rptGenBean.setCmSupplOrAdminView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
	            break;
	        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:
	        	//rptGenBean.setCmSupplOrAdminView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					//rptGenBean.setCmProofIdentityView(true);
					//rptGenBean.setCmItemCategoryView(false);
				//};
	            break;
	        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:	        		
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
				//};
	            break;
	        case ReportConst.TOT_CAT_ITM_STS_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);
	            break;
	        case ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
	            break;
	        case ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				//rptGenBean.setCmSupplOrAdminView(true);					
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				//rptGenBean.setCmSupplOrAdminView(true);					
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					//rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					//rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
	            break;
	        case ReportConst.SUM_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL:
	        					
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);					
				//};
				break;
	        case ReportConst.LST_ITM_CODE_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);					
				rptGenBean.setCmItemNameView(true);		
				rptGenBean.setCmExtentionView(true);
				break;
	       /* case ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);							
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_NAME));
	            break;
	        case ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);						
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_UNSPSC_CODE_REQ_RPT_NAME));
	            break;
	        case ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL:
	        	rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);							
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_NAME));	
	            break;
	        case ReportConst.LST_ITM_CODE_RPT_VAL:
	        	rptGenBean.setCmItemNameView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_ITM_CODE_RPT_NAME));
	            break;*/	       
	        default: 
	        	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_REPORT_ID));
	            //break;
		}		
	}	
}
