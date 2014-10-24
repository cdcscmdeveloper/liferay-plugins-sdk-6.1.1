package my.ep.web.backingbean.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.exception.EpException;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.pp.common.CommonMethod;

/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="cmReportBackingBean")
@ViewScoped
public class CMReportBackingBean  extends SourcingBaseBackingBean<ReportMangedBean>{
	ReportsGenerateBean rptGenerateBean = new ReportsGenerateBean();
	private String cmItemType;
	private String cmItemStatus;
	private String cmUnspscCode;
	private String cmUnspscTitle;	
	private String cmCompanyName;
	private String cmRegistrationType;	
	private String cmItemCategory;
	private String cmItemName;	
	private String cmEpNumber;
	private String cmBusinessRegNo;
	private String cmIcNumber;
	private String cmMofNumber;	
	private String cmExtentionCode;
	private String cmExtentionType;	
	private String cmExtentionBrand;
	private String cmExtentionMeasurment;
	private String cmExtentionColour;
	private String cmProofIdentity;
	private Date fromDate;
	private Date toDate;
	private String constructedParamString;
	
	public ReportsGenerateBean getRptGenerateBean() {
		return rptGenerateBean;
	}
	public void setRptGenerateBean(ReportsGenerateBean rptGenerateBean) {
		this.rptGenerateBean = rptGenerateBean;
	}
	public String getConstructedParamString() {
		return constructedParamString;
	}
	public void setConstructedParamString(String constructedParamString) {
		this.constructedParamString = constructedParamString;
	}
	public String getCmProofIdentity() {
		return cmProofIdentity;
	}
	public void setCmProofIdentity(String cmProofIdentity) {
		this.cmProofIdentity = cmProofIdentity;
	}
	public String getCmItemType() {
		return cmItemType;
	}

	public void setCmItemType(String cmItemType) {
		this.cmItemType = cmItemType;
	}

	public String getCmItemStatus() {
		return cmItemStatus;
	}

	public void setCmItemStatus(String cmItemStatus) {
		this.cmItemStatus = cmItemStatus;
	}

	public String getCmUnspscCode() {
		return cmUnspscCode;
	}

	public void setCmUnspscCode(String cmUnspscCode) {
		this.cmUnspscCode = cmUnspscCode;
	}

	public String getCmUnspscTitle() {
		return cmUnspscTitle;
	}

	public void setCmUnspscTitle(String cmUnspscTitle) {
		this.cmUnspscTitle = cmUnspscTitle;
	}

	public String getCmCompanyName() {
		return cmCompanyName;
	}

	public void setCmCompanyName(String cmCompanyName) {
		this.cmCompanyName = cmCompanyName;
	}

	public String getCmRegistrationType() {
		return cmRegistrationType;
	}

	public void setCmRegistrationType(String cmRegistrationType) {
		this.cmRegistrationType = cmRegistrationType;
	}

	public String getCmItemCategory() {
		return cmItemCategory;
	}

	public void setCmItemCategory(String cmItemCategory) {
		this.cmItemCategory = cmItemCategory;
	}

	public String getCmItemName() {
		return cmItemName;
	}

	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	public String getCmEpNumber() {
		return cmEpNumber;
	}

	public void setCmEpNumber(String cmEpNumber) {
		this.cmEpNumber = cmEpNumber;
	}

	public String getCmBusinessRegNo() {
		return cmBusinessRegNo;
	}

	public void setCmBusinessRegNo(String cmBusinessRegNo) {
		this.cmBusinessRegNo = cmBusinessRegNo;
	}

	public String getCmIcNumber() {
		return cmIcNumber;
	}

	public void setCmIcNumber(String cmIcNumber) {
		this.cmIcNumber = cmIcNumber;
	}

	public String getCmMofNumber() {
		return cmMofNumber;
	}

	public void setCmMofNumber(String cmMofNumber) {
		this.cmMofNumber = cmMofNumber;
	}

	public String getCmExtentionCode() {
		return cmExtentionCode;
	}

	public void setCmExtentionCode(String cmExtentionCode) {
		this.cmExtentionCode = cmExtentionCode;
	}

	public String getCmExtentionType() {
		return cmExtentionType;
	}

	public void setCmExtentionType(String cmExtentionType) {
		this.cmExtentionType = cmExtentionType;
	}

	public String getCmExtentionBrand() {
		return cmExtentionBrand;
	}

	public void setCmExtentionBrand(String cmExtentionBrand) {
		this.cmExtentionBrand = cmExtentionBrand;
	}

	public String getCmExtentionMeasurment() {
		return cmExtentionMeasurment;
	}

	public void setCmExtentionMeasurment(String cmExtentionMeasurment) {
		this.cmExtentionMeasurment = cmExtentionMeasurment;
	}

	public String getCmExtentionColour() {
		return cmExtentionColour;
	}

	public void setCmExtentionColour(String cmExtentionColour) {
		this.cmExtentionColour = cmExtentionColour;
	}

	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	/*
	 *Enable CM module criteria by report name 
	 */
	public void criteriaVariablesByReportByCM(ReportsGenerateBean rptGenBean){
		
		switch (getManagedBean().getReportValue()) {		
	        case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmItemStatusView(true);
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PUB_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					rptGenBean.setCmProofIdentityView(true);
				//};
	            break;
	        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemCategoryView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_APPR_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmItemTypeView(true);
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					rptGenBean.setCmProofIdentityView(true);
					rptGenBean.setCmItemCategoryView(false);
				//};
	            break;
	        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_REJ_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					rptGenBean.setCmProofIdentityView(true);						
				//};
	            break;
	        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME));	
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){					
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					rptGenBean.setCmProofIdentityView(true);
				//};
	            break;
	        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_EXP_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){					
					rptGenBean.setCmCompanyNameView(true);
					rptGenBean.setCmRegistrationTypeView(true);
					rptGenBean.setCmProofIdentityView(true);
				//};
	            break;
	        case ReportConst.TOT_CAT_ITM_STS_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);							
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_STS_RPT_NAME));	
	            break;
	        case ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);					
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);
				rptGenBean.setCmItemStatusView(false);
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);					
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_STD_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);						
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_NAME));	
	            break;
	        case ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);							
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);
				rptGenBean.setCmExtentionCodeView(true);					
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);	
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_NAME));	
	            break;
	        case ReportConst.SUM_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setCmItemTypeView(true);			
				rptGenBean.setCmUnspscCodeView(true);
				rptGenBean.setCmUnspscTitleView(true);							
				rptGenBean.setCmCompanyNameView(true);
				rptGenBean.setCmRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setCmProofIdentityView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUM_REJ_CAT_ITM_RPT_NAME));
	            break;
	        case ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL:
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
	            break;	       
	        default:            
	            break;
		}		
	}
	public void enableRegistrationType(){
		this.setCmEpNumber(null);
		this.setCmIcNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmMofNumber(null); 
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		if((this.getCmRegistrationType()!=null)&&(this.getCmRegistrationType()!=ReportConst.EMPTY_VAL)){			
			if(this.getCmRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_BASIC)){
				getManagedBean().getRptGenBean().setCmBasicRegView(true);
				getManagedBean().getRptGenBean().setCmMofRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);				
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			}else if(this.getCmRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_MOF)){
				getManagedBean().getRptGenBean().setCmMofRegView(true);
				getManagedBean().getRptGenBean().setCmBasicRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);				
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			}			
		}		
	}
	/*
	 *Generate CM module report  
	 */	
	public String generateCMReport(){	
		//String url="";
		if(((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL))||((this.getCmItemName()!=null)&&(this.getCmItemName()!=ReportConst.EMPTY_VAL))||((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL))||((this.getCmItemStatus()!=null)&&(this.getCmItemStatus()!=ReportConst.EMPTY_VAL))||((this.getCmUnspscCode()!=null)&&(this.getCmUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getCmUnspscTitle()!=null)&&(this.getCmUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionType()!=null)&&(this.getCmExtentionType()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionBrand()!=null)&&(this.getCmExtentionBrand()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionMeasurment()!=null)&&(this.getCmExtentionMeasurment()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionColour()!=null)&&(this.getCmExtentionColour()!=ReportConst.EMPTY_VAL))||((this.getCmCompanyName()!=null)&&(this.getCmCompanyName()!=ReportConst.EMPTY_VAL))||((this.getCmRegistrationType()!=null)&&(this.getCmRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getCmItemCategory()!=null)&&(this.getCmItemCategory()!=ReportConst.EMPTY_VAL))||((this.getCmProofIdentity()!=null)&&(this.getCmProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getCmEpNumber()!=null)&&(this.getCmEpNumber()!=ReportConst.EMPTY_VAL))||((this.getCmBusinessRegNo()!=null)&&(this.getCmBusinessRegNo()!=ReportConst.EMPTY_VAL))||((this.getCmIcNumber()!=null)&&(this.getCmIcNumber()!=ReportConst.EMPTY_VAL))||((this.getCmMofNumber()!=null)&&(this.getCmMofNumber()!=ReportConst.EMPTY_VAL))){			
			if((this.getCmItemName()!=null)&&(this.getCmItemName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getCmItemName());
			}
			if((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemTypeTotVar(ReportConst.AMP_ITM_TYPE_EQ+this.getCmItemType());
			}
			if((this.getCmItemStatus()!=null)&&(this.getCmItemStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemStatusTotVar(ReportConst.AMP_ITM_STATUS_EQ+this.getCmItemStatus());
			}
			if((this.getCmUnspscCode()!=null)&&(this.getCmUnspscCode()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setUnspscCodeTotVar(ReportConst.AMP_UNSPSC_CODE_EQ+this.getCmUnspscCode());
			}
			if((this.getCmUnspscTitle()!=null)&&(this.getCmUnspscTitle()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setUnspscTitleTotVar(ReportConst.AMP_UNSPSC_TIT_EQ+this.getCmUnspscTitle());
			}			
			if((this.getCmExtentionType()!=null)&&(this.getCmExtentionType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setExtentionTypeTotVar(ReportConst.AMP_EXT_TYPE_EQ+this.getCmExtentionType());
			}
			if((this.getCmExtentionBrand()!=null)&&(this.getCmExtentionBrand()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setExtentionBrandTotVar(ReportConst.AMP_EXT_BRAND_EQ+this.getCmExtentionBrand());
			}
			if((this.getCmExtentionMeasurment()!=null)&&(this.getCmExtentionMeasurment()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setExtentionMeasurmentTotVar(ReportConst.AMP_EXT_MEASURMENT_EQ+this.getCmExtentionMeasurment());
			}
			if((this.getCmExtentionColour()!=null)&&(this.getCmExtentionColour()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setExtentionColourTotVar(ReportConst.AMP_EXT_COLOUR_EQ+this.getCmExtentionColour());
			}				
			if((this.getCmCompanyName()!=null)&&(this.getCmCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getCmCompanyName());
			}
			if((this.getCmRegistrationType()!=null)&&(this.getCmRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getCmRegistrationType());
			}
			if((this.getCmItemCategory()!=null)&&(this.getCmItemCategory()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ+this.getCmItemCategory());
			}
			if((this.getCmProofIdentity()!=null)&&(this.getCmProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ+this.getCmProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getCmEpNumber()!=null)&&(this.getCmEpNumber()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setEpNumberTotVar(ReportConst.AMP_EP_NO_EQ+this.getCmEpNumber());
			}
			if((this.getCmBusinessRegNo()!=null)&&(this.getCmBusinessRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setBusinessRegNoTotVar(ReportConst.AMP_BUSI_REG_NO_EQ+this.getCmBusinessRegNo());
			}
			if((this.getCmIcNumber()!=null)&&(this.getCmIcNumber()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setIcNumberTotVar(ReportConst.AMP_IC_NO_EQ+this.getCmIcNumber());
			}
			if((this.getCmMofNumber()!=null)&&(this.getCmMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getCmMofNumber());
			}
			//-----------
			Map<String, String> urlCMReportNameJsps = new HashMap<String, String>();
			urlCMReportNameJsps.put(ReportConst.LST_PUB_CAT_ITM_RPT_VAL, ReportConst.CM_PUB_CAT_ITM_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_APPR_CAT_ITM_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_REJ_CAT_ITM_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_EXP_CAT_ITM_RPT_VAL, ReportConst.CM_EXP_CAT_ITM_JSP);			
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_STS_RPT_VAL, ReportConst.CM_TOT_CAT_ITM_STWISE_JSP);			
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL, ReportConst.CM_TOT_CAT_ITM_UNSPSCWISE_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL, ReportConst.CM_CAT_ITM_STD_PRC_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL, ReportConst.CM_CAT_ITM_TIER_PRC_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL, ReportConst.CM_CAT_ITEM_ZONAL_PRC_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL, ReportConst.CM_CAT_ITEM_ZONAL_TIER_PRC_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SUM_REJ_CAT_ITM_RPT_VAL, ReportConst.CM_REJ_CAT_ITM_SMRY_JSP);			
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL, ReportConst.CM_APPR_N_REJ_CAT_ITM_JSP);			
			urlCMReportNameJsps.put(ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL, ReportConst.CM_UNSPSC_CODE_REQ_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL, ReportConst.CM_NEW_EXT_CODE_CREAT_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_ITM_CODE_RPT_VAL, ReportConst.CM_LST_ITEM_CODE_JSP);
			//-----------			
			rptGenerateBean.setUrlReportJSPName(urlCMReportNameJsps.get(getManagedBean().getReportValue().trim()));
			if((rptGenerateBean.getUrlReportJSPName()!=null)&&(rptGenerateBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionTypeTotVar()+rptGenerateBean.getExtentionBrandTotVar()+rptGenerateBean.getExtentionMeasurmentTotVar()+rptGenerateBean.getExtentionColourTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getEpNumberTotVar()+rptGenerateBean.getBusinessRegNoTotVar()+rptGenerateBean.getIcNumberTotVar()+rptGenerateBean.getMofNumberTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar()+ReportConst.EXC_NEXT_URL;				
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionTypeTotVar()+rptGenerateBean.getExtentionBrandTotVar()+rptGenerateBean.getExtentionMeasurmentTotVar()+rptGenerateBean.getExtentionColourTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getEpNumberTotVar()+rptGenerateBean.getBusinessRegNoTotVar()+rptGenerateBean.getIcNumberTotVar()+rptGenerateBean.getMofNumberTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar());
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}			
		}else{
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
		return getConstructedParamString();
	}	
}
