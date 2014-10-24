package my.ep.web.backingbean.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="smReportBackingBean")
@ViewScoped
public class SMReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{
	ReportsGenerateBean rptGenerateBean = new ReportsGenerateBean();
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
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();	
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();
	
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();	
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();
	
	private String smSupportingDocument;	
	private String smDocStatus;
	private String smSuppDocStatus;	
	private String smRegistrationNo;
	private String smEpNo;
	private String smApplicationNo;
	private String smZone;
	private String smSupplierType;
	private String smMofRegNo;
	private String smMofRegNoFrom;
	private String smMofRegNoTo;
	private String smRegistrationStatus;
	private String smCapitalNaccumFrm;
	private String smCapitalNaccumTo;	
	private String smPaidUpCapitalNaccumShare;
	private String smcategory;
	private String smNoOfCategory;
	private String smMainCategory;
	private String smSubCategory;
	private String smSubCategoryDivision;
	private String smRole;
	private String smApplicationStatus;
	private String smCancelledBy;
	private String smPaymentType;
	private String smApplicationType;
	private String smState;
	private String smDistrict;
	private String smSupplierName;
	private String smReportType;
	private String smOrganizationType;
	private String smKementerian;
	private String smNamaAgensi;
	private String smComplaintNo;
	private String smProofIdentity;
	private String smCompanyStatus;
	private String smCaseNo;
	private String smCompApplStatus;
	private String smCategoryCode;
	private String smSiteVisitOfficer;
	private Date smReleaseFromDate;
	private Date smReleaseToDate;
	private Date smApplicationFromDate;
	private Date smApplicationToDate;
	private Date smReceiveFromDate;	
	private Date smReceiveToDate;	
	private Date smExpiryFromDate;	
	private Date smExpiryToDate;
	private Date smPrintedFromDate;	
	private Date smPrintedToDate;
	private Date fromDate;
	private Date toDate;
	private String smCompanyName;
	private String smSupplier;
	private String smEpNumber;
	private String smMofNumber;
	private String smRegistrationType;
	private String smBusinessRegNo;
	private String smIcNumber;
	private String constructedParamString;	
	
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
	public String getSmSupportingDocument() {
		return smSupportingDocument;
	}
	public void setSmSupportingDocument(String smSupportingDocument) {
		this.smSupportingDocument = smSupportingDocument;
	}
	public String getSmDocStatus() {
		return smDocStatus;
	}
	public void setSmDocStatus(String smDocStatus) {
		this.smDocStatus = smDocStatus;
	}
	public String getSmSuppDocStatus() {
		return smSuppDocStatus;
	}
	public void setSmSuppDocStatus(String smSuppDocStatus) {
		this.smSuppDocStatus = smSuppDocStatus;
	}
	public String getSmRegistrationNo() {
		return smRegistrationNo;
	}
	public void setSmRegistrationNo(String smRegistrationNo) {
		this.smRegistrationNo = smRegistrationNo;
	}
	public String getSmEpNo() {
		return smEpNo;
	}
	public void setSmEpNo(String smEpNo) {
		this.smEpNo = smEpNo;
	}
	public String getSmApplicationNo() {
		return smApplicationNo;
	}
	public void setSmApplicationNo(String smApplicationNo) {
		this.smApplicationNo = smApplicationNo;
	}
	public String getSmZone() {
		return smZone;
	}
	public void setSmZone(String smZone) {
		this.smZone = smZone;
	}
	public String getSmSupplierType() {
		return smSupplierType;
	}
	public void setSmSupplierType(String smSupplierType) {
		this.smSupplierType = smSupplierType;
	}
	public String getSmMofRegNo() {
		return smMofRegNo;
	}
	public void setSmMofRegNo(String smMofRegNo) {
		this.smMofRegNo = smMofRegNo;
	}
	public String getSmMofRegNoFrom() {
		return smMofRegNoFrom;
	}
	public void setSmMofRegNoFrom(String smMofRegNoFrom) {
		this.smMofRegNoFrom = smMofRegNoFrom;
	}
	public String getSmMofRegNoTo() {
		return smMofRegNoTo;
	}
	public void setSmMofRegNoTo(String smMofRegNoTo) {
		this.smMofRegNoTo = smMofRegNoTo;
	}
	public String getSmRegistrationStatus() {
		return smRegistrationStatus;
	}
	public void setSmRegistrationStatus(String smRegistrationStatus) {
		this.smRegistrationStatus = smRegistrationStatus;
	}
	public String getSmCapitalNaccumFrm() {
		return smCapitalNaccumFrm;
	}
	public void setSmCapitalNaccumFrm(String smCapitalNaccumFrm) {
		this.smCapitalNaccumFrm = smCapitalNaccumFrm;
	}
	public String getSmCapitalNaccumTo() {
		return smCapitalNaccumTo;
	}
	public void setSmCapitalNaccumTo(String smCapitalNaccumTo) {
		this.smCapitalNaccumTo = smCapitalNaccumTo;
	}
	public String getSmPaidUpCapitalNaccumShare() {
		return smPaidUpCapitalNaccumShare;
	}
	public void setSmPaidUpCapitalNaccumShare(String smPaidUpCapitalNaccumShare) {
		this.smPaidUpCapitalNaccumShare = smPaidUpCapitalNaccumShare;
	}
	public String getSmcategory() {
		return smcategory;
	}
	public void setSmcategory(String smcategory) {
		this.smcategory = smcategory;
	}
	public String getSmNoOfCategory() {
		return smNoOfCategory;
	}
	public void setSmNoOfCategory(String smNoOfCategory) {
		this.smNoOfCategory = smNoOfCategory;
	}
	public String getSmMainCategory() {
		return smMainCategory;
	}
	public void setSmMainCategory(String smMainCategory) {
		this.smMainCategory = smMainCategory;
	}
	public String getSmSubCategory() {
		return smSubCategory;
	}
	public void setSmSubCategory(String smSubCategory) {
		this.smSubCategory = smSubCategory;
	}
	public String getSmSubCategoryDivision() {
		return smSubCategoryDivision;
	}
	public void setSmSubCategoryDivision(String smSubCategoryDivision) {
		this.smSubCategoryDivision = smSubCategoryDivision;
	}
	public String getSmRole() {
		return smRole;
	}
	public void setSmRole(String smRole) {
		this.smRole = smRole;
	}
	public String getSmApplicationStatus() {
		return smApplicationStatus;
	}
	public void setSmApplicationStatus(String smApplicationStatus) {
		this.smApplicationStatus = smApplicationStatus;
	}
	public String getSmCancelledBy() {
		return smCancelledBy;
	}
	public void setSmCancelledBy(String smCancelledBy) {
		this.smCancelledBy = smCancelledBy;
	}
	public String getSmPaymentType() {
		return smPaymentType;
	}
	public void setSmPaymentType(String smPaymentType) {
		this.smPaymentType = smPaymentType;
	}
	public String getSmApplicationType() {
		return smApplicationType;
	}
	public void setSmApplicationType(String smApplicationType) {
		this.smApplicationType = smApplicationType;
	}
	public String getSmState() {
		return smState;
	}
	public void setSmState(String smState) {
		this.smState = smState;
	}
	public String getSmDistrict() {
		return smDistrict;
	}
	public void setSmDistrict(String smDistrict) {
		this.smDistrict = smDistrict;
	}
	public String getSmSupplierName() {
		return smSupplierName;
	}
	public void setSmSupplierName(String smSupplierName) {
		this.smSupplierName = smSupplierName;
	}
	public String getSmReportType() {
		return smReportType;
	}
	public void setSmReportType(String smReportType) {
		this.smReportType = smReportType;
	}
	public String getSmOrganizationType() {
		return smOrganizationType;
	}
	public void setSmOrganizationType(String smOrganizationType) {
		this.smOrganizationType = smOrganizationType;
	}
	public String getSmKementerian() {
		return smKementerian;
	}
	public void setSmKementerian(String smKementerian) {
		this.smKementerian = smKementerian;
	}
	public String getSmNamaAgensi() {
		return smNamaAgensi;
	}
	public void setSmNamaAgensi(String smNamaAgensi) {
		this.smNamaAgensi = smNamaAgensi;
	}
	public String getSmComplaintNo() {
		return smComplaintNo;
	}
	public void setSmComplaintNo(String smComplaintNo) {
		this.smComplaintNo = smComplaintNo;
	}
	public String getSmProofIdentity() {
		return smProofIdentity;
	}
	public void setSmProofIdentity(String smProofIdentity) {
		this.smProofIdentity = smProofIdentity;
	}
	public String getSmCompanyStatus() {
		return smCompanyStatus;
	}
	public void setSmCompanyStatus(String smCompanyStatus) {
		this.smCompanyStatus = smCompanyStatus;
	}
	public String getSmCaseNo() {
		return smCaseNo;
	}
	public void setSmCaseNo(String smCaseNo) {
		this.smCaseNo = smCaseNo;
	}
	public String getSmCompApplStatus() {
		return smCompApplStatus;
	}
	public void setSmCompApplStatus(String smCompApplStatus) {
		this.smCompApplStatus = smCompApplStatus;
	}
	public String getSmCategoryCode() {
		return smCategoryCode;
	}
	public void setSmCategoryCode(String smCategoryCode) {
		this.smCategoryCode = smCategoryCode;
	}
	public String getSmSiteVisitOfficer() {
		return smSiteVisitOfficer;
	}
	public void setSmSiteVisitOfficer(String smSiteVisitOfficer) {
		this.smSiteVisitOfficer = smSiteVisitOfficer;
	}
	public Date getSmReleaseFromDate() {
		return smReleaseFromDate;
	}
	public void setSmReleaseFromDate(Date smReleaseFromDate) {
		this.smReleaseFromDate = smReleaseFromDate;
	}
	public Date getSmReleaseToDate() {
		return smReleaseToDate;
	}
	public void setSmReleaseToDate(Date smReleaseToDate) {
		this.smReleaseToDate = smReleaseToDate;
	}
	public Date getSmApplicationFromDate() {
		return smApplicationFromDate;
	}
	public void setSmApplicationFromDate(Date smApplicationFromDate) {
		this.smApplicationFromDate = smApplicationFromDate;
	}
	public Date getSmApplicationToDate() {
		return smApplicationToDate;
	}
	public void setSmApplicationToDate(Date smApplicationToDate) {
		this.smApplicationToDate = smApplicationToDate;
	}
	public Date getSmReceiveFromDate() {
		return smReceiveFromDate;
	}
	public void setSmReceiveFromDate(Date smReceiveFromDate) {
		this.smReceiveFromDate = smReceiveFromDate;
	}
	public Date getSmReceiveToDate() {
		return smReceiveToDate;
	}
	public void setSmReceiveToDate(Date smReceiveToDate) {
		this.smReceiveToDate = smReceiveToDate;
	}
	public Date getSmExpiryFromDate() {
		return smExpiryFromDate;
	}
	public void setSmExpiryFromDate(Date smExpiryFromDate) {
		this.smExpiryFromDate = smExpiryFromDate;
	}
	public Date getSmExpiryToDate() {
		return smExpiryToDate;
	}
	public void setSmExpiryToDate(Date smExpiryToDate) {
		this.smExpiryToDate = smExpiryToDate;
	}
	public Date getSmPrintedFromDate() {
		return smPrintedFromDate;
	}
	public void setSmPrintedFromDate(Date smPrintedFromDate) {
		this.smPrintedFromDate = smPrintedFromDate;
	}
	public Date getSmPrintedToDate() {
		return smPrintedToDate;
	}
	public void setSmPrintedToDate(Date smPrintedToDate) {
		this.smPrintedToDate = smPrintedToDate;
	}
	
	public String getSmCompanyName() {
		return smCompanyName;
	}
	public void setSmCompanyName(String smCompanyName) {
		this.smCompanyName = smCompanyName;
	}
	public String getSmSupplier() {
		return smSupplier;
	}
	public void setSmSupplier(String smSupplier) {
		this.smSupplier = smSupplier;
	}
	public String getSmEpNumber() {
		return smEpNumber;
	}
	public void setSmEpNumber(String smEpNumber) {
		this.smEpNumber = smEpNumber;
	}
	public String getSmMofNumber() {
		return smMofNumber;
	}
	public void setSmMofNumber(String smMofNumber) {
		this.smMofNumber = smMofNumber;
	}
	public String getSmRegistrationType() {
		return smRegistrationType;
	}
	public void setSmRegistrationType(String smRegistrationType) {
		this.smRegistrationType = smRegistrationType;
	}
	public String getSmBusinessRegNo() {
		return smBusinessRegNo;
	}
	public void setSmBusinessRegNo(String smBusinessRegNo) {
		this.smBusinessRegNo = smBusinessRegNo;
	}
	public String getSmIcNumber() {
		return smIcNumber;
	}
	public void setSmIcNumber(String smIcNumber) {
		this.smIcNumber = smIcNumber;
	}

	/*
	 *Enable SM module criteria by report name 
	 */
	public void criteriaVariablesByReportBySM(ReportsGenerateBean rptGenBean){
		switch (getManagedBean().getReportValue()) {		
	        case ReportConst.SM_LST_OF_APP_RPT_VAL:
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
	            break;
	        case ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_VAL:
	        	rptGenBean.setSmMandatoryZoneView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmMandatoryMofRegNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL:
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
	            break;
	        case ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_VAL:
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMofRegNoFromView(true);
	        	rptGenBean.setSmMofRegNoToView(true);
	        	rptGenBean.setSmCompanyNameView(true);	        	
	        	rptGenBean.setSmRoleView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL:
	        	
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
	            break;
	        case ReportConst.SM_CAT_STATIC_RPT_VAL:	        	
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDistrictView(true);
	        	rptGenBean.setSmCategoryView(true);
	        	rptGenBean.setSmRegistrationStatusView(true);
	        	rptGenBean.setSmNoOfCategoryView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveCategoryLevOne();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_RPT_NAME));	
				rptGenBean.retrieveState();
	            break;
	        case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:		        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_NAME));			
	            break;
	        case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
	        	rptGenBean.setSmStateView(true);	        	
	        	rptGenBean.setSmCategoryView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveState();
				rptGenBean.retrieveCategoryLevOne();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_VERIFY_RPT_VAL:		        	
	        	rptGenBean.setSmReceiveDateView(true);						
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERIFY_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_APPROVED_RPT_VAL:		        	
	        	rptGenBean.setSmSupplierNameView(true);
				rptGenBean.setSmMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_APPROVED_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_REJECTED_RPT_VAL:		        	
	        	rptGenBean.setSmSupplierNameView(true);
				rptGenBean.setSmMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_REJECTED_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_VAL:		        	
	        	rptGenBean.setSmCompanyNameView(true);
				rptGenBean.setSmCategoryView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveCategoryLevOne();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
				rptGenBean.setSmReportTypeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setSmApplicationDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL:	        	
				//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
				rptGenBean.setSmCompanyNameView(true);
				//rptGenBean.setSmExpiryDateView(true);
				rptGenBean.setSmMandatoryExpiryDateView(true);
				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_PRE_EXP_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_MONTH_WISE_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_VAL:	
	        	rptGenBean.setSmApplicationTypeView(true);
	        	rptGenBean.setSmSupplierTypeView(true);
				rptGenBean.setSmReceiveDateView(true);				
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:	
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmSupplierNameView(true);
	        	rptGenBean.setSmMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_COMP_ADDR_RPT_VAL:	
	        	//rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmMandatorySupplierTypeView(true);
	        	rptGenBean.setSmCompApplStatusView(true);
				//rptGenBean.setSmPrintedDateView(true);	
	        	rptGenBean.setSmMandatoryPrintedDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMP_ADDR_RPT_NAME));			
	            break;
	        case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:
	        	rptGenBean.setSmExtraTwoColumnView(true);
	        	rptGenBean.setSmReportTypeView(true);
				rptGenBean.setSmApplicationDateView(true);
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPPL_STAT_RPT_VAL:	
	        	rptGenBean.setSmZoneView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDistrictView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveState();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPPL_STAT_RPT_NAME));			
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
	        case ReportConst.SM_QUESTIONAIRE_RPT_VAL:	
	        	rptGenBean.setSmZoneView(true);	        	
	        	rptGenBean.setSmSupplierTypeView(true);
	        	rptGenBean.setSmReportTypeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_QUESTIONAIRE_RPT_NAME));			
	            break;
	        case ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPP_STAT_BY_CAT_RPT_VAL:
	           	rptGenBean.setSmExtraTwoColumnView(true);
	        	//rptGenBean.setSmZoneView(true);
	           	rptGenBean.setSmMandatoryZoneView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_STAT_BY_CAT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL:
	        	rptGenBean.setSmRegistrationTypeView(true);
	        	rptGenBean.setSmStateView(true);
	        	rptGenBean.setSmDocStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				rptGenBean.retrieveState();
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_VAL:
	        	rptGenBean.setSmSupplierNameView(true);
	        	rptGenBean.setSmCaseNoView(true);
	        	rptGenBean.setSmRegistrationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_VAL:
	        	rptGenBean.setSmSupplierNameView(true);
	        	rptGenBean.setSmComplaintNoView(true);	        	
	        	rptGenBean.setSmRegistrationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_VAL:
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
	            break;
	        default:            
	            break;
		}
	}
	public void enableRegistrationType(){
		this.setSmEpNumber(null);
		this.setSmIcNumber(null);
		this.setSmBusinessRegNo(null);
		this.setSmMofNumber(null); 
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
		if((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL)){			
			if(this.getSmRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_BASIC)){
				getManagedBean().getRptGenBean().setSmBasicRegView(true);
				getManagedBean().getRptGenBean().setSmMofRegView(false);
				this.setSmMofNumber(null);
				this.setSmEpNumber(null);				
				this.setSmIcNumber(null);
				this.setSmBusinessRegNo(null);
			}else if(this.getSmRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_MOF)){
				getManagedBean().getRptGenBean().setSmMofRegView(true);
				getManagedBean().getRptGenBean().setSmBasicRegView(false);
				this.setSmMofNumber(null);
				this.setSmEpNumber(null);				
				this.setSmIcNumber(null);
				this.setSmBusinessRegNo(null);
			}			
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
	public void retrieveDistrict(){		
		districtLst = new ArrayList<SelectItem>();		
		IDistrictSO	districtSO	=	(IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
		DistrictReqDTO	districtReqDTO	=	new DistrictReqDTO();
		if(this.getSmState()!=null&&this.getSmState()!=""){			
			districtReqDTO.setStateId( Long.parseLong(this.getSmState()));
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
	public void retrieveCategoryLevTwo(){
		levTwoCatLst = new ArrayList<SelectItem>();		
		ICategoryCodeSO categoryCodeSO	=	(ICategoryCodeSO)BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
		CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
		if(this.getSmMainCategory()!=null&&this.getSmMainCategory()!=""){	
			catCodeReqDTO.setCategoryL1Id(Long.parseLong(this.getSmMainCategory()));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL2ListByL1Id(catCodeReqDTO);
			for(PmCategoryL2 catTwo: catCodeResDTO.getCategoryL2List()){			
				SelectItem selectItem= new SelectItem();
				selectItem.setLabel(catTwo.getCategoryName());
				selectItem.setValue(catTwo.getCategoryL2Id());
				levTwoCatLst.add(selectItem);			
			}
		}
	}
	public void retrieveCategoryLevThree(){
		levThreeCatLst = new ArrayList<SelectItem>();		
		ICategoryCodeSO categoryCodeSO	=	(ICategoryCodeSO)BusinessDelegate.getInstance().getServiceObject("pm", ICategoryCodeSO.class);		
		CategoryCodeReqDTO	catCodeReqDTO	=	new CategoryCodeReqDTO();
		if(this.getSmSubCategory()!=null&&this.getSmSubCategory()!=""){	
			catCodeReqDTO.setCategoryL2Id(Long.parseLong(this.getSmSubCategory()));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO.retrieveCategoryL3ListByL2Id(catCodeReqDTO);
			for(PmCategoryL3 catThree: catCodeResDTO.getCategoryL3List()){			
				SelectItem selectItem= new SelectItem();
				selectItem.setLabel(catThree.getCategoryName());
				selectItem.setValue(catThree.getCategoryL3Id());
				levThreeCatLst.add(selectItem);			
			}
		}
	}
	/*
	 *Generate SM module report  
	 */	
	public String generateSMReport(){	
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	
		if(((this.getSmSiteVisitOfficer()!=null)&&(this.getSmSiteVisitOfficer()!=ReportConst.EMPTY_VAL))||((this.getSmCategoryCode()!=null)&&(this.getSmCategoryCode()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationType()!=null)&&(this.getSmApplicationType()!=ReportConst.EMPTY_VAL))||((this.getSmPaymentType()!=null)&&(this.getSmPaymentType()!=ReportConst.EMPTY_VAL))||((this.getSmCancelledBy()!=null)&&(this.getSmCancelledBy()!=ReportConst.EMPTY_VAL))||((this.getSmCompanyName()!=null)&&(this.getSmCompanyName()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getSmSupportingDocument()!=null)&&(this.getSmSupportingDocument()!=ReportConst.EMPTY_VAL))||((this.getSmDocStatus()!=null)&&(this.getSmDocStatus()!=ReportConst.EMPTY_VAL))||((this.getSmSuppDocStatus()!=null)&&(this.getSmSuppDocStatus()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationNo()!=null)&&(this.getSmRegistrationNo()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationNo()!=null)&&(this.getSmApplicationNo()!=ReportConst.EMPTY_VAL))||
				((this.getSmNamaAgensi()!=null)&&(this.getSmNamaAgensi()!=ReportConst.EMPTY_VAL))||((this.getSmZone()!=null)&&(this.getSmZone()!=ReportConst.EMPTY_VAL))||((this.getSmSupplierType()!=null)&&(this.getSmSupplierType()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNo()!=null)&&(this.getSmMofRegNo()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNoFrom()!=null)&&(this.getSmMofRegNoFrom()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNoTo()!=null)&&(this.getSmMofRegNoTo()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationStatus()!=null)&&(this.getSmRegistrationStatus()!=ReportConst.EMPTY_VAL))||((this.getSmCapitalNaccumFrm()!=null)&&(this.getSmCapitalNaccumFrm()!=ReportConst.EMPTY_VAL))||((this.getSmCapitalNaccumTo()!=null)&&(this.getSmCapitalNaccumTo().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPaidUpCapitalNaccumShare()!=null)&&(this.getSmPaidUpCapitalNaccumShare().toString()!=ReportConst.EMPTY_VAL))||((this.getSmNoOfCategory()!=null)&&(this.getSmNoOfCategory()!=ReportConst.EMPTY_VAL))||((this.getSmMainCategory()!=null)&&(this.getSmMainCategory()!=ReportConst.EMPTY_VAL))||((this.getSmSubCategory()!=null)&&(this.getSmSubCategory()!=ReportConst.EMPTY_VAL))||
				((this.getSmSubCategoryDivision()!=null)&&(this.getSmSubCategoryDivision()!=ReportConst.EMPTY_VAL))||((this.getSmRole()!=null)&&(this.getSmRole()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationStatus()!=null)&&(this.getSmApplicationStatus()!=ReportConst.EMPTY_VAL))||((this.getSmState()!=null)&&(this.getSmState()!=ReportConst.EMPTY_VAL))||((this.getSmDistrict()!=null)&&(this.getSmDistrict()!=ReportConst.EMPTY_VAL))||((this.getSmSupplierName()!=null)&&(this.getSmSupplierName()!=ReportConst.EMPTY_VAL))||((this.getSmReportType()!=null)&&(this.getSmReportType()!=ReportConst.EMPTY_VAL))||((this.getSmOrganizationType()!=null)&&(this.getSmOrganizationType()!=ReportConst.EMPTY_VAL))||((this.getSmKementerian()!=null)&&(this.getSmKementerian()!=ReportConst.EMPTY_VAL))||((this.getSmComplaintNo()!=null)&&(this.getSmComplaintNo()!=ReportConst.EMPTY_VAL))||((this.getSmProofIdentity()!=null)&&(this.getSmProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getSmCompanyStatus()!=null)&&(this.getSmCompanyStatus()!=ReportConst.EMPTY_VAL))||((this.getSmCaseNo()!=null)&&(this.getSmCaseNo()!=ReportConst.EMPTY_VAL))||
				((this.getSmCompApplStatus()!=null)&&(this.getSmCompApplStatus()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationFromDate()!=null)&&(this.getSmApplicationFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationToDate()!=null)&&(this.getSmApplicationToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReceiveFromDate()!=null)&&(this.getSmReceiveFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReceiveToDate()!=null)&&(this.getSmReceiveToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReleaseFromDate()!=null)&&(this.getSmReleaseFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmExpiryToDate()!=null)&&(this.getSmExpiryToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPrintedFromDate()!=null)&&(this.getSmPrintedFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPrintedToDate()!=null)&&(this.getSmPrintedToDate().toString()!=ReportConst.EMPTY_VAL))){
			
			if((this.getSmApplicationType()!=null)&&(this.getSmApplicationType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setApplicationTypeTotVar(""+this.getSmApplicationType());
			}
			if((this.getSmPaymentType()!=null)&&(this.getSmPaymentType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPaymentTypeTotVar(""+this.getSmPaymentType());
			}
			if((this.getSmCancelledBy()!=null)&&(this.getSmCancelledBy()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCancelledByTotVar(""+this.getSmCancelledBy());
			}
			if((this.getSmCompanyName()!=null)&&(this.getSmCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getSmCompanyName());
			}
			if((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getSmRegistrationType());
			}
			if((this.getSmSupportingDocument()!=null)&&(this.getSmSupportingDocument()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSupportingDocumentTotVar(ReportConst.AMP_SUPP_DOC_EQ+this.getSmSupportingDocument());
			}
			if((this.getSmDocStatus()!=null)&&(this.getSmDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDocStatusTotVar(ReportConst.AMP_DOC_STS_NAME_EQ+this.getSmDocStatus());
			}
			if((this.getSmSuppDocStatus()!=null)&&(this.getSmSuppDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSuppDocStatusTotVar(ReportConst.AMP_SUPP_DOC_STATUS_MODE_EQ+this.getSmSuppDocStatus());
			}
			if((this.getSmRegistrationNo()!=null)&&(this.getSmRegistrationNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRegistrationNoTotVar(ReportConst.AMP_REG_NO_EQ+this.getSmRegistrationNo());
			}					
			if((this.getSmApplicationNo()!=null)&&(this.getSmApplicationNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setApplicationNoTotVar(ReportConst.AMP_APPLICATION_NO_EQ+this.getSmApplicationNo());
			}
			if((this.getSmZone()!=null)&&(this.getSmZone()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setZoneTotVar(ReportConst.AMP_ZONE_NAME_EQ+this.getSmZone());
			}
			if((this.getSmSupplierType()!=null)&&(this.getSmSupplierType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSupplierTypeTotVar(ReportConst.AMP_SUPPLIER_TYPE_EQ+this.getSmSupplierType());
			}
			if((this.getSmMofRegNo()!=null)&&(this.getSmMofRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofRegNoTotVar(""+this.getSmMofRegNo());
			}				
			if((this.getSmMofRegNoFrom()!=null)&&(this.getSmMofRegNoFrom()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofRegNoFromTotVar(ReportConst.AMP_REG_MOF_FROM_EQ+this.getSmMofRegNoFrom());
			}
			if((this.getSmMofRegNoTo()!=null)&&(this.getSmMofRegNoTo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofRegNoToTotVar(ReportConst.AMP_REG_MOF_TO_EQ+this.getSmMofRegNoTo());
			}			
			if((this.getSmRegistrationStatus()!=null)&&(this.getSmRegistrationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRegistrationStatusTotVar(ReportConst.AMP_REG_STATUS_EQ+this.getSmRegistrationStatus());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getSmReleaseFromDate()!=null)&&(this.getSmReleaseFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setFromDateVar(ReportConst.AMP_FROM_RELEASE_DATE_EQ+outputDateFormat.format(this.getSmReleaseFromDate()));
			}
			if((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setExpiryFromDateTotVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getSmExpiryFromDate()));
			}
			if((this.getSmExpiryToDate()!=null)&&(this.getSmExpiryToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setExpiryToDateTotVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getSmExpiryToDate()));
			}
			if((this.getSmCapitalNaccumFrm()!=null)&&(this.getSmCapitalNaccumFrm()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCapitalNaccumFrmTotVar(ReportConst.AMP_FRM_PUC_EQ+this.getSmCapitalNaccumFrm());
			}
			if((this.getSmCapitalNaccumTo()!=null)&&(this.getSmCapitalNaccumTo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCapitalNaccumToTotVar(ReportConst.AMP_TO_PUC_EQ+this.getSmCapitalNaccumTo());
			}
			if((this.getSmPaidUpCapitalNaccumShare()!=null)&&(this.getSmPaidUpCapitalNaccumShare()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPaidUpCapitalNaccumShareTotVar(ReportConst.AMP_PUC_N_AS_EQ+this.getSmPaidUpCapitalNaccumShare());
			}
			
			if((this.getSmNoOfCategory()!=null)&&(this.getSmNoOfCategory()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setNoOfCategoryTotVar(""+this.getSmNoOfCategory());
			}
			if((this.getSmMainCategory()!=null)&&(this.getSmMainCategory()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMainCategoryTotVar(ReportConst.AMP_MAIN_CATEGORY_EQ+this.getSmMainCategory());
			}
			if((this.getSmSubCategory()!=null)&&(this.getSmSubCategory()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSubCategoryTotVar(ReportConst.AMP_SUB_CATEGORY_EQ+this.getSmSubCategory());
			}
			if((this.getSmSubCategoryDivision()!=null)&&(this.getSmSubCategoryDivision()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSubCategoryDivisionTotVar(ReportConst.AMP_SUB_DIV_CATEGORY_EQ+this.getSmSubCategoryDivision());
			}
			if((this.getSmRole()!=null)&&(this.getSmRole()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRoleTotVar(ReportConst.AMP_ROLE_EQ+this.getSmRole());
			}
			if((this.getSmApplicationStatus()!=null)&&(this.getSmApplicationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setApplicationStatusTotVar(""+this.getSmApplicationStatus());
			}
			if((this.getSmState()!=null)&&(this.getSmState()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofNumberTotVar(""+this.getSmState());
			}
			if((this.getSmDistrict()!=null)&&(this.getSmDistrict()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setStateTotVar(""+this.getSmDistrict());
			}
			if((this.getSmSupplierName()!=null)&&(this.getSmSupplierName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSupplierNameTotVar(""+this.getSmSupplierName());
			}
			if((this.getSmReportType()!=null)&&(this.getSmReportType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setReportTypeTotVar(""+this.getSmReportType());
			}
			if((this.getSmOrganizationType()!=null)&&(this.getSmOrganizationType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setOrganizationTypeTotVar(""+this.getSmOrganizationType());
			}
			if((this.getSmKementerian()!=null)&&(this.getSmKementerian()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setKementerianTotVar(""+this.getSmKementerian());
			}
			if((this.getSmNamaAgensi()!=null)&&(this.getSmNamaAgensi()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setNamAgensiTotVar(""+this.getSmNamaAgensi());
			}			
			if((this.getSmComplaintNo()!=null)&&(this.getSmComplaintNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setComplaintNoTotVar(""+this.getSmComplaintNo());
			}
			if((this.getSmProofIdentity()!=null)&&(this.getSmProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setProofIdentityTotVar(ReportConst.AMP_IC_NO_EQ+this.getSmProofIdentity());
			}
			if((this.getSmCompanyStatus()!=null)&&(this.getSmCompanyStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCompanyStatusTotVar(""+this.getSmCompanyStatus());
			}
			if((this.getSmCaseNo()!=null)&&(this.getSmCaseNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCaseNoTotVar(""+this.getSmCaseNo());
			}
			if((this.getSmCompApplStatus()!=null)&&(this.getSmCompApplStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCompApplStatusTotVar(""+this.getSmCompApplStatus());
			}
			if((this.getSmCategoryCode()!=null)&&(this.getSmCategoryCode()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCategoryCodeTotVar(""+this.getSmCategoryCode());
			}
			if((this.getSmSiteVisitOfficer()!=null)&&(this.getSmSiteVisitOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSiteVisitOfficerTotVar(""+this.getSmSiteVisitOfficer());
			}
			if((this.getSmMofNumber()!=null)&&(this.getSmMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getSmMofNumber());
			}			
			Map<String, String> urlCMReportNameJsps = new HashMap<String, String>();
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_APP_RPT_VAL, ReportConst.SM_LST_OF_APP_RPT_JSP);
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL, ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_VAL, ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL, ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_VAL, ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL, ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_VAL, ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_JSP);				
			urlCMReportNameJsps.put(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL, ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_VAL, ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_VAL, ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_VAL, ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_CAT_STATIC_RPT_VAL, ReportConst.EMPTY_VAL);				
			urlCMReportNameJsps.put(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_APPL_VERIFY_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_APPL_APPROVED_RPT_VAL, ReportConst.EMPTY_VAL);			//-----
			urlCMReportNameJsps.put(ReportConst.SM_APPL_REJECTED_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL, ReportConst.SM_LST_PRE_EXP_COMP_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_MONTH_WISE_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_COMP_ADDR_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_SUPPL_STAT_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_BPK_USER_PROF_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_BPK_ADMIN_PROF_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_AGENCY_STAT_SUMM_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_QUESTIONAIRE_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_SUPP_STAT_BY_CAT_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL, ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_JSP);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_SUPP_PEROFRM_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_VAL, ReportConst.EMPTY_VAL);			
			urlCMReportNameJsps.put(ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.SM_DETAILS_OF_APPLICATION_RPT_VAL, ReportConst.EMPTY_VAL);			
			//-----------			
			rptGenerateBean.setUrlReportJSPName(urlCMReportNameJsps.get(getManagedBean().getReportValue().trim()));			
			if((rptGenerateBean.getUrlReportJSPName()!=null)&&(rptGenerateBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionTypeTotVar()+rptGenerateBean.getExtentionBrandTotVar()+rptGenerateBean.getExtentionMeasurmentTotVar()+rptGenerateBean.getExtentionColourTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getEpNumberTotVar()+rptGenerateBean.getBusinessRegNoTotVar()+rptGenerateBean.getIcNumberTotVar()+rptGenerateBean.getMofNumberTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar()+ReportConst.EXC_NEXT_URL;
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionTypeTotVar()+rptGenerateBean.getExtentionBrandTotVar()+rptGenerateBean.getExtentionMeasurmentTotVar()+rptGenerateBean.getExtentionColourTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getEpNumberTotVar()+rptGenerateBean.getBusinessRegNoTotVar()+rptGenerateBean.getIcNumberTotVar()+rptGenerateBean.getMofNumberTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar()+rptGenerateBean.getSupportingDocumentTotVar()+rptGenerateBean.getDocStatusTotVar()+rptGenerateBean.getSuppDocStatusTotVar()+rptGenerateBean.getRegistrationNoTotVar()+rptGenerateBean.getePNoTotVar()+rptGenerateBean.getApplicationNoTotVar()+rptGenerateBean.getZoneTotVar()+rptGenerateBean.getSupplierTypeTotVar()+rptGenerateBean.getMofRegNoTotVar()+rptGenerateBean.getMofRegNoFromTotVar()+rptGenerateBean.getMofRegNoToTotVar()+rptGenerateBean.getRegistrationStatusTotVar()+rptGenerateBean.getCapitalNaccumFrmTotVar()+rptGenerateBean.getCapitalNaccumToTotVar()+	rptGenerateBean.getPaidUpCapitalNaccumShareTotVar()+rptGenerateBean.getCategoryTotVar()+rptGenerateBean.getNoOfCategoryTotVar()+rptGenerateBean.getMainCategoryTotVar()+rptGenerateBean.getSubCategoryTotVar()+rptGenerateBean.getSubCategoryDivisionTotVar()+rptGenerateBean.getRoleTotVar()+rptGenerateBean.getApplicationStatusTotVar()+rptGenerateBean.getCancelledByTotVar()+rptGenerateBean.getPaymentTypeTotVar()+rptGenerateBean.getApplicationTypeTotVar()+rptGenerateBean.getStateTotVar()+rptGenerateBean.getDistrictTotVar()+rptGenerateBean.getSupplierNameTotVar()+rptGenerateBean.getReportTypeTotVar()+rptGenerateBean.getOrganizationTypeTotVar()+rptGenerateBean.getKementerianTotVar()+rptGenerateBean.getNamAgensiTotVar()+rptGenerateBean.getComplaintNoTotVar()+	
						rptGenerateBean.getCompanyStatusTotVar()+rptGenerateBean.getCaseNoTotVar()+rptGenerateBean.getCompApplStatusTotVar()+rptGenerateBean.getCategoryCodeTotVar()+rptGenerateBean.getSiteVisitOfficerTotVar()+rptGenerateBean.getReleaseFromDateTotVar()+rptGenerateBean.getApplicationFromDateTotVar()+rptGenerateBean.getApplicationToDateTotVar()+rptGenerateBean.getReceiveFromDateTotVar()+rptGenerateBean.getReceiveToDateTotVar()+rptGenerateBean.getExpiryFromDateTotVar()+rptGenerateBean.getExpiryToDateTotVar()+rptGenerateBean.getPrintedFromDateTotVar()+rptGenerateBean.getPrintedToDateTotVar());
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		}else{
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
		return getConstructedParamString();
	}
}
