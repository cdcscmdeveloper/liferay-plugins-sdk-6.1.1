package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.portlet.ResourceResponse;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import com.liferay.portal.util.PortalUtil;
import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="reportBackingBean")
@ViewScoped
public class ReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{
	private static final Logger log = EpLogger.getLogger(ReportBackingBean.class);

	public ReportBackingBean() {		
	}	
	SCReportBackingBean scReportBackingBean;
	CMReportBackingBean cmReportBackingBean;
	FLReportBackingBean flReportBackingBean;
	SMReportBackingBean smReportBackingBean;	 
	public SCReportBackingBean getScReportBackingBean() {
		return scReportBackingBean;
	}
	public void setScReportBackingBean(SCReportBackingBean scReportBackingBean) {
		this.scReportBackingBean = scReportBackingBean;
	}
	public CMReportBackingBean getCmReportBackingBean() {
		return cmReportBackingBean;
	}
	public void setCmReportBackingBean(CMReportBackingBean cmReportBackingBean) {
		this.cmReportBackingBean = cmReportBackingBean;
	}
	public FLReportBackingBean getFlReportBackingBean() {
		return flReportBackingBean;
	}
	public void setFlReportBackingBean(FLReportBackingBean flReportBackingBean) {
		this.flReportBackingBean = flReportBackingBean;
	}
	public SMReportBackingBean getSmReportBackingBean() {
		return smReportBackingBean;
	}
	public void setSmReportBackingBean(SMReportBackingBean smReportBackingBean) {
		this.smReportBackingBean = smReportBackingBean;
	}
	List<SelectItem> reportList;
	ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<SelectItem> moduleList;
	private List<SelectItem> regTypeList;
	List<String> authoriztionList=new ArrayList<String>();
	private ScQt scqt;
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();	
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();	
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();	
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();	
	private List<SelectItem> procurementList = new ArrayList<SelectItem>();
	
	private List<SelectItem> scProcureAllList = new ArrayList<SelectItem>();	
	private List<SelectItem> scQuotationList = new ArrayList<SelectItem>();	
	private List<SelectItem> scTenderList = new ArrayList<SelectItem>();	
	private List<SelectItem> scRstrctdTndrList = new ArrayList<SelectItem>();
	
	private List<SelectItem> monthList = new ArrayList<SelectItem>();
	private List<String> yearList = new ArrayList<String>();
	
	public List<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(List<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public List<String> getYearList() {
		return yearList;
	}
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
	private String userId;
	private String roleCode;	
	private Date fromDate;
	private Date toDate;
	//sc		
	private String scQuotationId;
	private String scQuotationNo;	
	private String scPaymentId;
	
	private String scCreatedToYear;
	private String scCreatedToMonth;	
	private String scCreatedFromYear;
	private String scCreatedFromMonth;
	private String scDeskOfficer;
	private String scPtj;
	private String scStatus;
	private Date scPublishfromDate;
	private Date scPublishToDate;
	//cm
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
	//fl	
	private String flItmType;
	private String flPtj;
	private String flContractNo;
	private String flSst;
	private String flJabatan;
	private String flMinistry;
	private String flSupplier; 
	private String flStatus;
	private String flDuration;
	private String flRecievingOfficer;
	private String flDeliveryAddress;
	private String flRecNoteNo;
	private String flContractType;
	private String flRequisitioner;
	private String flMtoContract;
	private String flInvoiceNo;
	private String flDeductionType;
	private String flCrNo;
	private String flApproverName;
	private String flDocumentType;
	private String flCriteria;
	private String flScoring;
	private String flPkpNo;
	private String flDeliveryOrderNo;	
	private String flItemCode;
	private String flPoAmount;
	private String flPrNo;
	private String flPmo;	
	private String flItemName;
	private String flContractOrder;
	private String flContractRequest;
	private String flPurchaseOrderNo;
   //sm
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
	private String smCompanyName;
	private String smSupplier;
	private String smEpNumber;
	private String smMofNumber;
	private String smRegistrationType;
	private String smBusinessRegNo;
	private String smIcNumber;
	
	private String generatedURL;
	private String reportURL;	
	private StreamedContent pdfFile;
	private String constructedParamString;
	private String reportFileNameJSP;
	
	
	public Date getScPublishfromDate() {
		return scPublishfromDate;
	}
	public void setScPublishfromDate(Date scPublishfromDate) {
		this.scPublishfromDate = scPublishfromDate;
	}
	public Date getScPublishToDate() {
		return scPublishToDate;
	}
	public void setScPublishToDate(Date scPublishToDate) {
		this.scPublishToDate = scPublishToDate;
	}
	public List<SelectItem> getScProcureAllList() {
		return scProcureAllList;
	}
	public void setScProcureAllList(List<SelectItem> scProcureAllList) {
		this.scProcureAllList = scProcureAllList;
	}
	public List<SelectItem> getScQuotationList() {
		return scQuotationList;
	}
	public void setScQuotationList(List<SelectItem> scQuotationList) {
		this.scQuotationList = scQuotationList;
	}
	public List<SelectItem> getScTenderList() {
		return scTenderList;
	}
	public void setScTenderList(List<SelectItem> scTenderList) {
		this.scTenderList = scTenderList;
	}
	public List<SelectItem> getScRstrctdTndrList() {
		return scRstrctdTndrList;
	}
	public void setScRstrctdTndrList(List<SelectItem> scRstrctdTndrList) {
		this.scRstrctdTndrList = scRstrctdTndrList;
	}
	public String getScCreatedToYear() {
		return scCreatedToYear;
	}
	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}
	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}
	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}
	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}
	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}
	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}
	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}
	public String getScDeskOfficer() {
		return scDeskOfficer;
	}
	public void setScDeskOfficer(String scDeskOfficer) {
		this.scDeskOfficer = scDeskOfficer;
	}
	public String getScPtj() {
		return scPtj;
	}
	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}
	public String getScStatus() {
		return scStatus;
	}
	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}
	public List<SelectItem> getProcurementList() {
		return procurementList;
	}
	public void setProcurementList(List<SelectItem> procurementList) {
		this.procurementList = procurementList;
	}
	public String getFlContractOrder() {
		return flContractOrder;
	}
	public void setFlContractOrder(String flContractOrder) {
		this.flContractOrder = flContractOrder;
	}
	public String getFlContractRequest() {
		return flContractRequest;
	}
	public void setFlContractRequest(String flContractRequest) {
		this.flContractRequest = flContractRequest;
	}
	public String getFlPurchaseOrderNo() {
		return flPurchaseOrderNo;
	}
	public void setFlPurchaseOrderNo(String flPurchaseOrderNo) {
		this.flPurchaseOrderNo = flPurchaseOrderNo;
	}
	public String getReportFileNameJSP() {
		return reportFileNameJSP;
	}
	public void setReportFileNameJSP(String reportFileNameJSP) {
		this.reportFileNameJSP = reportFileNameJSP;
	}
	public StreamedContent getPdfFile() {
		return pdfFile;
	}
	public String getFlItmType() {
		return flItmType;
	}
	public void setFlItmType(String flItmType) {
		this.flItmType = flItmType;
	}
	public String getFlPtj() {
		return flPtj;
	}
	public void setFlPtj(String flPtj) {
		this.flPtj = flPtj;
	}
	public String getFlContractNo() {
		return flContractNo;
	}
	public void setFlContractNo(String flContractNo) {
		this.flContractNo = flContractNo;
	}
	public String getFlSst() {
		return flSst;
	}
	public void setFlSst(String flSst) {
		this.flSst = flSst;
	}
	public String getFlJabatan() {
		return flJabatan;
	}
	public void setFlJabatan(String flJabatan) {
		this.flJabatan = flJabatan;
	}
	public String getFlMinistry() {
		return flMinistry;
	}
	public void setFlMinistry(String flMinistry) {
		this.flMinistry = flMinistry;
	}
	public String getFlSupplier() {
		return flSupplier;
	}
	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}
	public String getFlStatus() {
		return flStatus;
	}
	public void setFlStatus(String flStatus) {
		this.flStatus = flStatus;
	}
	public String getFlDuration() {
		return flDuration;
	}
	public void setFlDuration(String flDuration) {
		this.flDuration = flDuration;
	}
	public String getFlRecievingOfficer() {
		return flRecievingOfficer;
	}
	public void setFlRecievingOfficer(String flRecievingOfficer) {
		this.flRecievingOfficer = flRecievingOfficer;
	}
	public String getFlDeliveryAddress() {
		return flDeliveryAddress;
	}
	public void setFlDeliveryAddress(String flDeliveryAddress) {
		this.flDeliveryAddress = flDeliveryAddress;
	}
	public String getFlContractType() {
		return flContractType;
	}
	public void setFlContractType(String flContractType) {
		this.flContractType = flContractType;
	}
	public String getFlRequisitioner() {
		return flRequisitioner;
	}
	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}
	public String getFlMtoContract() {
		return flMtoContract;
	}
	public void setFlMtoContract(String flMtoContract) {
		this.flMtoContract = flMtoContract;
	}
	public String getFlInvoiceNo() {
		return flInvoiceNo;
	}
	public void setFlInvoiceNo(String flInvoiceNo) {
		this.flInvoiceNo = flInvoiceNo;
	}
	public String getFlDeductionType() {
		return flDeductionType;
	}
	public void setFlDeductionType(String flDeductionType) {
		this.flDeductionType = flDeductionType;
	}
	public String getFlCrNo() {
		return flCrNo;
	}
	public void setFlCrNo(String flCrNo) {
		this.flCrNo = flCrNo;
	}
	public String getFlApproverName() {
		return flApproverName;
	}
	public void setFlApproverName(String flApproverName) {
		this.flApproverName = flApproverName;
	}
	public String getFlDocumentType() {
		return flDocumentType;
	}
	public void setFlDocumentType(String flDocumentType) {
		this.flDocumentType = flDocumentType;
	}
	public String getFlCriteria() {
		return flCriteria;
	}
	public void setFlCriteria(String flCriteria) {
		this.flCriteria = flCriteria;
	}
	public String getFlScoring() {
		return flScoring;
	}
	public void setFlScoring(String flScoring) {
		this.flScoring = flScoring;
	}
	public String getFlPkpNo() {
		return flPkpNo;
	}
	public void setFlPkpNo(String flPkpNo) {
		this.flPkpNo = flPkpNo;
	}
	public String getFlDeliveryOrderNo() {
		return flDeliveryOrderNo;
	}
	public void setFlDeliveryOrderNo(String flDeliveryOrderNo) {
		this.flDeliveryOrderNo = flDeliveryOrderNo;
	}
	public String getFlItemCode() {
		return flItemCode;
	}
	public void setFlItemCode(String flItemCode) {
		this.flItemCode = flItemCode;
	}
	public String getFlPoAmount() {
		return flPoAmount;
	}
	public void setFlPoAmount(String flPoAmount) {
		this.flPoAmount = flPoAmount;
	}
	public String getFlPrNo() {
		return flPrNo;
	}
	public void setFlPrNo(String flPrNo) {
		this.flPrNo = flPrNo;
	}
	public String getFlPmo() {
		return flPmo;
	}
	public void setFlPmo(String flPmo) {
		this.flPmo = flPmo;
	}	
	public String getFlItemName() {
		return flItemName;
	}
	public void setFlItemName(String flItemName) {
		this.flItemName = flItemName;
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
	public String getCmProofIdentity() {
		return cmProofIdentity;
	}
	public void setCmProofIdentity(String cmProofIdentity) {
		this.cmProofIdentity = cmProofIdentity;
	}	
	public String getScQuotationId() {
		return scQuotationId;
	}
	public void setScQuotationId(String scQuotationId) {
		this.scQuotationId = scQuotationId;
	}
	public String getScQuotationNo() {
		return scQuotationNo;
	}
	public void setScQuotationNo(String scQuotationNo) {
		this.scQuotationNo = scQuotationNo;
	}
	public String getScPaymentId() {
		return scPaymentId;
	}
	public void setScPaymentId(String scPaymentId) {
		this.scPaymentId = scPaymentId;
	}
	public void setPdfFile(StreamedContent pdfFile) {
		this.pdfFile = pdfFile;
	}
	public String getConstructedParamString() {
		return constructedParamString;
	}
	public void setConstructedParamString(String constructedParamString) {
		this.constructedParamString = constructedParamString;
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
	
	public String getGeneratedURL() {
		return generatedURL;
	}
	public void setGeneratedURL(String generatedURL) {
		this.generatedURL = generatedURL;
	}	
	public String getReportURL() {
		return reportURL;
	}
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
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
	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}
	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
	}	
	public List<String> getAuthoriztionList() {
		return authoriztionList;
	}
	public void setAuthoriztionList(List<String> authoriztionList) {
		this.authoriztionList = authoriztionList;
	}	
	public ScQt getScqt() {
		return scqt;
	}
	public void setScqt(ScQt scqt) {
		this.scqt = scqt;
	}
	public static Logger getLog() {
		return log;
	}	
	public List<SelectItem> getRegTypeList() {
		return regTypeList;
	}
	public void setRegTypeList(List<SelectItem> regTypeList) {
		this.regTypeList = regTypeList;
	}	
	public List<SelectItem> getModuleList() {
		return moduleList;
	}	
	public void setModuleList(List<SelectItem> moduleList) {
		this.moduleList = moduleList;
	}
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}
	public List<SelectItem> getReportList() {
		return reportList;
	}
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}	
	/*
	 *Retrieve module list 
	 */
	public List<SelectItem> retriveModuleList() {
		moduleList = new ArrayList<SelectItem>();		
		SelectItem selectItem= new SelectItem();
		selectItem.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_SC_TTL));
		selectItem.setValue("SC");
		moduleList.add(selectItem);			
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_CM_TTL));
		selectItem1.setValue("CM");
		moduleList.add(selectItem1);			
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_FL_TTL));
		selectItem2.setValue("FL");
		moduleList.add(selectItem2);
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_SM_TTL));
		selectItem3.setValue("SM");
		moduleList.add(selectItem3);
		//checkVariableValueExist();
		return moduleList;
	}
	/*
	 *Retrieve report list by module 
	 */
	public void retriveReportListBymoduleName() {
		rptGenBean = new ReportsGenerateBean();		
		getManagedBean().setReportName(null);
		getManagedBean().setReportValue(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setRptGenBean(null);
		retriveUserrloeByUserId(getManagedBean().getModuleValue());
		if(getManagedBean().getModuleValue()!=null){
			retriveReportListByModule(getManagedBean().getModuleValue());			
		}		
		getManagedBean().setReportList(reportList);		
		getManagedBean().setRptGenBean(rptGenBean);		
		resetReportForm();		
	}
	public List<SelectItem> retriveReportListByModule(String module){
		reportList=new ArrayList<SelectItem>();		
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject("rpt", IPMReportSO.class);
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setModuleCode(module);
		PMReportResDTO rptResDTO = reportSO.retriveReportByModule(reqDTO);
		List<PMReport> rptLst = rptResDTO.getPmReportList();
		if (null != rptLst) {
				
			for (PMReport pmReport : rptLst) {
				SelectItem selectItem= new SelectItem();
				String label = ReportConst.EMPTY_VAL;
				if((null != pmReport.getSubModule())&&(ReportConst.EMPTY_VAL != pmReport.getSubModule())){
				  label = pmReport.getReportTitle()+" - "+ pmReport.getSubModule();
				}else{
					label = pmReport.getReportTitle();
				}
				selectItem.setValue(pmReport.getReportId());
				selectItem.setLabel(label);
				reportList.add(selectItem);
			}
		}		
		return reportList;
	}
	/*
	 *Retrieve Role code by User id 
	 */
	public List<String> retriveUserrloeByUserId(String moduleName) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(moduleName, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		
		userId = new FacesScopes().getUserSessionBean().getUserId().toString();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode("en");
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();		
		if (null != roleDesc) {			
			for (PmRoleDesc pmRoleDesc : roleDesc) {				
				pmRoleDesc.getPmRole().getRoleCode();
				log.info("Role Code="+pmRoleDesc.getPmRole().getRoleCode());
				/*if(ReportConst.SUPPLIER_USER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.SUPPLIER_USER.toString());
				}else if(ReportConst.SUPPLIER_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.SUPPLIER_ADMIN.toString());
				}else if(RoleCode.BASIC_SUPPLIER_USER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.BASIC_SUPPLIER_USER.toString());
				}else if(RoleCode.BASIC_SUPPLIER_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.BASIC_SUPPLIER_ADMIN.toString());
				}else if(RoleCode.MOF_SUPPLIER_USER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.MOF_SUPPLIER_USER.toString());
				}else if(RoleCode.MOF_SUPPLIER_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.MOF_SUPPLIER_ADMIN.toString());
				}else if(RoleCode.CM_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.CM_ADMIN.toString());
				}else if(RoleCode.CD_OFFICER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.CD_OFFICER.toString());
				}else if(RoleCode.CD_COMMITTEE.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.CD_COMMITTEE.toString());
				}else if(RoleCode.REQUISITIONER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.REQUISITIONER.toString());
				}else if(RoleCode.MIN_CONTRACT_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.MIN_CONTRACT_ADMIN.toString());
				}else if(RoleCode.RECEIVING_OFFICER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.RECEIVING_OFFICER.toString());
				}else if(RoleCode.APPROVER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.APPROVER.toString());
				}else if(RoleCode.PAY_OFFICER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.PAY_OFFICER.toString());
				}else if(RoleCode.BPK_CCA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.BPK_CCA.toString());
				}else if(RoleCode.BPK_PCA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.BPK_PCA.toString());
				}else if(ReportConst.UPS_SDO.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SDO.toString());
				}else if(ReportConst.UPS_SDO_HQ.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SDO_HQ.toString());
				}else if(ReportConst.UPS_SDO_SA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SDO_SA.toString());
				}else if(ReportConst.UPS_SDO_SK.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SDO_SK.toString());
				}else if(RoleCode.UPS_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.UPS_ADMIN.toString());
				}else if(ReportConst.UPS_PO.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_PO.toString());
				}else if(ReportConst.UPS_PO_HQ.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_PO_HQ.toString());
				}else if(ReportConst.UPS_PO_SA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_PO_SA.toString());
				}else if(ReportConst.UPS_PO_SK.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_PO_SK.toString());
				}else if(RoleCode.BPK_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.BPK_ADMIN.toString());
				}else if(RoleCode.EP_ADMIN.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(RoleCode.EP_ADMIN.toString());
				}else if(ReportConst.AGENCY.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.AGENCY.toString());
				}else if(ReportConst.EP_COORDINATOR.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.EP_COORDINATOR.toString());
				}else if(ReportConst.UPS_SVC_HQ.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVC_HQ.toString());
				}else if(ReportConst.UPS_SVC_SA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVC_SA.toString());
				}else if(ReportConst.UPS_SVC_SK.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVC_SK.toString());
				}else if(ReportConst.UPS_SVC.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVC.toString());
				}else if(ReportConst.UPS_SVO_HQ.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVO_HQ.toString());
				}else if(ReportConst.UPS_SVO_SA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVO_SA.toString());
				}else if(ReportConst.UPS_SVO_SK.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVO_SK.toString());
				}else if(ReportConst.UPS_SVO.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.UPS_SVO.toString());
				}else if(ReportConst.DISC_SECRETARY_HQ.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.DISC_SECRETARY_HQ.toString());
				}else if(ReportConst.DISC_SECRETARY_SA.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.DISC_SECRETARY_SA.toString());
				}else if(ReportConst.DISC_SECRETARY_SK.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.DISC_SECRETARY_SK.toString());
				}else if(ReportConst.DISC_SECRETARY.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
					authoriztionList.add(ReportConst.DISC_SECRETARY.toString());
				}else{
					authoriztionList.add(pmRoleDesc.getPmRole().getRoleCode());
				}*/
				if(!authoriztionList.contains(pmRoleDesc.getPmRole().getRoleCode())){
					authoriztionList.add(pmRoleDesc.getPmRole().getRoleCode());
				}
			}			
		}		
		return authoriztionList;
	}	
	/*
	 *Enable  criteria variables by report name 
	 */
	public void criteriaVariablesByReport() {
		scReportBackingBean = new SCReportBackingBean();
		cmReportBackingBean = new CMReportBackingBean();
		flReportBackingBean = new FLReportBackingBean();
		smReportBackingBean = new SMReportBackingBean();
		rptGenBean = new ReportsGenerateBean();	
		getManagedBean().setRptGenBean(null);
		getManagedBean().setReportName(ReportConst.EMPTY_VAL);
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		levOneCatLst = new ArrayList<SelectItem>();
		levTwoCatLst = new ArrayList<SelectItem>();
		levThreeCatLst = new ArrayList<SelectItem>();		
		if((getManagedBean().getModuleValue())!=null && (getManagedBean().getReportValue())!=null){
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject("rpt", IPMReportSO.class);
			PMReportReqDTO reqDTO = new PMReportReqDTO();
			reqDTO.setReportId(getManagedBean().getReportValue());
			PMReportResDTO rptResDTO = reportSO.retriveReportByrptId(reqDTO);
			PMReport pmReport = rptResDTO.getPmReportDtls();			
			if (null != pmReport) {					
					String label = ReportConst.EMPTY_VAL;
					if((null != pmReport.getSubModule())&&(ReportConst.EMPTY_VAL != pmReport.getSubModule())){
					  label = pmReport.getReportTitle()+" - "+ pmReport.getSubModule();
					}else{
						label = pmReport.getReportTitle();
					}	
				getManagedBean().setReportName(label);
				reportFileNameJSP = pmReport.getReportFilename();
			}
			
			if((getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))){				
				cmReportBackingBean.criteriaVariablesByReportByCM(rptGenBean,authoriztionList);
			}else if((getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))){				
				scReportBackingBean.criteriaVariablesByReportBySC(rptGenBean,authoriztionList);
			}else if((getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))){				
				flReportBackingBean.criteriaVariablesByReportByFL(rptGenBean,authoriztionList);
			}else if((getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))){				
				smReportBackingBean.criteriaVariablesByReportBySM(rptGenBean,authoriztionList);
			}
			resetReportForm();
			retrieveDates();
			getManagedBean().setRptGenBean(rptGenBean);
			
		}
	}	
	public void retrieveDates(){
		log.info("inside retrieveDates");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
		Date currentDate= new Date();
		if(getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){
			this.setScCreatedFromYear(outputDateFormat.format(currentDate).substring(6,10));
			this.setScCreatedToYear(outputDateFormat.format(currentDate).substring(6,10));
		}
		if((getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))&&((getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL))||(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL))||(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL)))){
			this.setToDate(currentDate);
			this.setSmExpiryToDate(currentDate);
			Date newDate= new Date();
			newDate.setMonth(0);
			newDate.setDate(1);
			this.setFromDate(newDate);
			this.setSmExpiryFromDate(newDate);
			//SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);	
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
		if(this.getSmState()!=null&&this.getSmState()!=ReportConst.EMPTY_VAL){			
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
		if(this.getSmMainCategory()!=null&&this.getSmMainCategory()!=ReportConst.EMPTY_VAL){	
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
		if(this.getSmSubCategory()!=null&&this.getSmSubCategory()!=ReportConst.EMPTY_VAL){	
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
	public void enableRegistrationType(){
		this.setCmEpNumber(null);
		this.setCmIcNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmMofNumber(null);		
		this.setSmEpNumber(null);
		this.setSmIcNumber(null);
		this.setSmBusinessRegNo(null);
		this.setSmMofNumber(null);
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
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
		}else if((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL)){			
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
	/*
	 *Generate all modules reports 
	 */
	public void generateReport(){
		
		if((getManagedBean().getModuleValue()!=null)&&(getManagedBean().getReportValue()!=null)){
			if(!(getManagedBean().getReportValue().toString().equals(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))&&!(getManagedBean().getReportValue().toString().equals(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))&&!(getManagedBean().getReportValue().toString().equals(ReportConst.PDW_EC_RPT_VAL))){
				if((((this.getFromDate()==null)&&(this.getToDate()!=null))||((this.getFromDate()!=null)&&(this.getToDate()==null)))||(((this.getSmApplicationFromDate()==null)&&(this.getSmApplicationToDate()!=null))||((this.getSmApplicationFromDate()!=null)&&(this.getSmApplicationToDate()==null)))||(((this.getSmReceiveFromDate()==null)&&(this.getSmReceiveToDate()!=null))||((this.getSmReceiveFromDate()!=null)&&(this.getSmReceiveToDate()==null)))||(((this.getSmPrintedFromDate()==null)&&(this.getSmPrintedToDate()!=null))||((this.getSmPrintedFromDate()!=null)&&(this.getSmPrintedToDate()==null)))||(((this.getSmExpiryFromDate()==null)&&(this.getSmExpiryToDate()!=null))||((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryToDate()==null)))){
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_BOTH_DTS_RPT_SEARCH));
				}
			}
			if((this.getFromDate()!=null)&&(this.getToDate()!=null)){
				if(this.getFromDate().after(this.getToDate())){					  
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}				
			}
			if((this.getSmApplicationFromDate()!=null)&&(this.getSmApplicationToDate()!=null)){
				if(this.getSmApplicationFromDate().after(this.getSmApplicationToDate())){					 
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if((this.getSmReceiveFromDate()!=null)&&(this.getSmReceiveToDate()!=null)){
				if(this.getSmReceiveFromDate().after(this.getSmReceiveToDate())){					  
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}			
			if((this.getSmPrintedFromDate()!=null)&&(this.getSmPrintedToDate()!=null)){
				if(this.getSmPrintedFromDate().after(this.getSmPrintedToDate())){					
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryToDate()!=null)){
				if(this.getSmExpiryFromDate().after(this.getSmExpiryToDate())){					
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if((getManagedBean().getReportValue()!=null)&&(getManagedBean().getReportValue()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setReportValueIdVar(ReportConst.FL_AMP_REPORT_ID_EQ+getManagedBean().getReportValue());
			}
			if((new FacesScopes().getUserSessionBean().getUserId()!=null)&&(new FacesScopes().getUserSessionBean().getUserId().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setUserIdVar(ReportConst.FL_AMP_USER_ID_EQ+new FacesScopes().getUserSessionBean().getUserId().toString());
			}
			if(getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){			
				generateSCReport();				
			}else if(getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT)){
				generateCMReport();				
			}else if(getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)){
				generateFLReport();				
			}else if(getManagedBean().getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT)){
				generateSMReport();			
			}			
			if(null!=constructedParamString && !"".equals(constructedParamString)){
				//loadReport(generatedURL);	
				loadReportWithParamsString(constructedParamString);
				//loadReportWithParams(paramLst);
				//getReportPdf();				
				/*try {
					getStreamPDFFile(constructedParamString);
				} catch (IOException e) {					
					e.printStackTrace();
				}*/				
			}
			RequestContext context = RequestContext.getCurrentInstance();  
	        context.addCallbackParam(ReportConst.RPT_IN_VALID, ReportConst.RPT_TRUE);
		}else{			
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_SEARCH));
		}		
	}
	private void loadReportWithParams(List paramLst) {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_PREV_URL));
		 for (int i = 1; i <= paramLst.size(); i++) {
			 gnrtdURL.append(paramLst.get(i));
		 }
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_NEXT_URL));		
		try {			
			PortletRequest portalRequest=(PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req=com.liferay.portal.util.PortalUtil.getOriginalServletRequest(com.liferay.portal.util.PortalUtil.getHttpServletRequest(portalRequest));
			//String serverDetails="http://"+req.getServerName()+":"+req.getLocalPort();
			String baseUrl=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			String path=baseUrl+"/ReportViewServlet";			
			log.info(path);
			req.getSession().setAttribute("generatedUrl", gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam("reportViewUrl", path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}
	private void loadReport(String gnrtdURL) {
		try {			
			PortletRequest portalRequest=(PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req=com.liferay.portal.util.PortalUtil.getOriginalServletRequest(com.liferay.portal.util.PortalUtil.getHttpServletRequest(portalRequest));
			//String serverDetails="http://"+req.getServerName()+":"+req.getLocalPort();
			String baseUrl=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			String path=baseUrl+"/ReportViewServlet";			
			log.info(path);
			log.info(gnrtdURL);
			req.getSession().setAttribute("generatedUrl", gnrtdURL);
			RequestContext.getCurrentInstance().addCallbackParam("reportViewUrl", path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}
	private void loadReportWithParamsString(String paramConstrString) {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString); 
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_NEXT_URL));
		try {			
			PortletRequest portalRequest=(PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req=com.liferay.portal.util.PortalUtil.getOriginalServletRequest(com.liferay.portal.util.PortalUtil.getHttpServletRequest(portalRequest));
			//String serverDetails="http://"+req.getServerName()+":"+req.getLocalPort();
			String baseUrl=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			String path=baseUrl+"/ReportViewServlet";			
			log.info(path);
			log.info(gnrtdURL.toString());
			req.getSession().setAttribute("generatedUrl", gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam("reportViewUrl", path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();		
		}
	}	
	public void getStreamPDFFile(String paramConstrString) throws IOException {		
		EpPropertiesUtils propertiesUtils = EpPropertiesUtils.getInstance();
		String excPrevUrl = propertiesUtils.getProperty(EpPropertiesConst.EXC_PREV_URL);
		String excNextUrl = propertiesUtils.getProperty(EpPropertiesConst.EXC_NEXT_URL);
		StringBuilder reportUrl = new StringBuilder(excPrevUrl);
		reportUrl.append(ReportConst.AMP_REPORT_NAME);
		reportUrl.append(paramConstrString);		
		reportUrl.append(excNextUrl);		
		if (log.isDebugEnabled()) {
			log.debug("reportUrl: " + reportUrl);
		}		
		HttpURLConnection urlConnection = null;
		URL obj = new URL(reportUrl.toString());
	    urlConnection = (HttpURLConnection) obj.openConnection();	    
	    BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());        
	    PortletResponse portletResponse = (PortletResponse) FacesContext
      	      .getCurrentInstance().getExternalContext().getResponse();
	    HttpServletResponse res = PortalUtil
	      	      .getHttpServletResponse(portletResponse);
	    res.setHeader("Content-Disposition", "attachment; filename=\""
    	          + "NGeP_Report.pdf" + "\"");//
	    res.setHeader("Content-Transfer-Encoding", "binary");
	    res.setContentType("application/pdf");
	    res.flushBuffer();	    
	    OutputStream out = res.getOutputStream();        	  
	    byte[] b = new byte[128];
        int size;
        while ((size = buf.read(b, 0, b.length)) != -1) {
        	out.write(b, 0, size);
        }
        buf.close();
        out.close();
	}
	public StreamedContent getStreamedContentPDFFile(String paramConstrString) {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString); 
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.EXC_NEXT_URL));
		log.info( gnrtdURL.toString());
		HttpURLConnection urlConnection = null;		
		//ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
       // byte[] buf = new byte[1024];
		if(gnrtdURL!=null) {		
			try {
				URL obj = new URL(gnrtdURL.toString());
				urlConnection = (HttpURLConnection) obj.openConnection();				
				//input = new BufferedInputStream(urlConnection.getInputStream()); 
				pdfFile=new DefaultStreamedContent(urlConnection.getInputStream(),"application/pdf");	           
				/*InputStream in = urlConnection.getInputStream();
				byte[] buf = new byte[512];
				int len;
				while (true) {
					len = in.read(buf);
					if (len == -1) {
						break;
					}
					tmpOut.write(buf, 0, len);
				}
				tmpOut.flush();
				in.close();
				tmpOut.close();*/
				/*ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());
				ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());
				pdfFile= new DefaultStreamedContent(stream, "application/pdf");*/
				urlConnection.disconnect();
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        }
		}
		return pdfFile;
	}
	/*
	 *Generate SM module report  
	 */	
	public void generateSMReport(){		
		if(((this.getSmSiteVisitOfficer()!=null)&&(this.getSmSiteVisitOfficer()!=ReportConst.EMPTY_VAL))||((this.getSmCategoryCode()!=null)&&(this.getSmCategoryCode()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationType()!=null)&&(this.getSmApplicationType()!=ReportConst.EMPTY_VAL))||((this.getSmPaymentType()!=null)&&(this.getSmPaymentType()!=ReportConst.EMPTY_VAL))||((this.getSmCancelledBy()!=null)&&(this.getSmCancelledBy()!=ReportConst.EMPTY_VAL))||((this.getSmCompanyName()!=null)&&(this.getSmCompanyName()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getSmSupportingDocument()!=null)&&(this.getSmSupportingDocument()!=ReportConst.EMPTY_VAL))||((this.getSmDocStatus()!=null)&&(this.getSmDocStatus()!=ReportConst.EMPTY_VAL))||((this.getSmSuppDocStatus()!=null)&&(this.getSmSuppDocStatus()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationNo()!=null)&&(this.getSmRegistrationNo()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationNo()!=null)&&(this.getSmApplicationNo()!=ReportConst.EMPTY_VAL))||
				((this.getSmNamaAgensi()!=null)&&(this.getSmNamaAgensi()!=ReportConst.EMPTY_VAL))||((this.getSmZone()!=null)&&(this.getSmZone()!=ReportConst.EMPTY_VAL))||((this.getSmSupplierType()!=null)&&(this.getSmSupplierType()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNo()!=null)&&(this.getSmMofRegNo()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNoFrom()!=null)&&(this.getSmMofRegNoFrom()!=ReportConst.EMPTY_VAL))||((this.getSmMofRegNoTo()!=null)&&(this.getSmMofRegNoTo()!=ReportConst.EMPTY_VAL))||((this.getSmRegistrationStatus()!=null)&&(this.getSmRegistrationStatus()!=ReportConst.EMPTY_VAL))||((this.getSmCapitalNaccumFrm()!=null)&&(this.getSmCapitalNaccumFrm()!=ReportConst.EMPTY_VAL))||((this.getSmCapitalNaccumTo()!=null)&&(this.getSmCapitalNaccumTo().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPaidUpCapitalNaccumShare()!=null)&&(this.getSmPaidUpCapitalNaccumShare().toString()!=ReportConst.EMPTY_VAL))||((this.getSmNoOfCategory()!=null)&&(this.getSmNoOfCategory()!=ReportConst.EMPTY_VAL))||((this.getSmMainCategory()!=null)&&(this.getSmMainCategory()!=ReportConst.EMPTY_VAL))||((this.getSmSubCategory()!=null)&&(this.getSmSubCategory()!=ReportConst.EMPTY_VAL))||
				((this.getSmSubCategoryDivision()!=null)&&(this.getSmSubCategoryDivision()!=ReportConst.EMPTY_VAL))||((this.getSmRole()!=null)&&(this.getSmRole()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationStatus()!=null)&&(this.getSmApplicationStatus()!=ReportConst.EMPTY_VAL))||((this.getSmState()!=null)&&(this.getSmState()!=ReportConst.EMPTY_VAL))||((this.getSmDistrict()!=null)&&(this.getSmDistrict()!=ReportConst.EMPTY_VAL))||((this.getSmSupplierName()!=null)&&(this.getSmSupplierName()!=ReportConst.EMPTY_VAL))||((this.getSmReportType()!=null)&&(this.getSmReportType()!=ReportConst.EMPTY_VAL))||((this.getSmOrganizationType()!=null)&&(this.getSmOrganizationType()!=ReportConst.EMPTY_VAL))||((this.getSmKementerian()!=null)&&(this.getSmKementerian()!=ReportConst.EMPTY_VAL))||((this.getSmComplaintNo()!=null)&&(this.getSmComplaintNo()!=ReportConst.EMPTY_VAL))||((this.getSmProofIdentity()!=null)&&(this.getSmProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getSmCompanyStatus()!=null)&&(this.getSmCompanyStatus()!=ReportConst.EMPTY_VAL))||((this.getSmCaseNo()!=null)&&(this.getSmCaseNo()!=ReportConst.EMPTY_VAL))||
				((this.getSmCompApplStatus()!=null)&&(this.getSmCompApplStatus()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationFromDate()!=null)&&(this.getSmApplicationFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmApplicationToDate()!=null)&&(this.getSmApplicationToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReceiveFromDate()!=null)&&(this.getSmReceiveFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReceiveToDate()!=null)&&(this.getSmReceiveToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmReleaseFromDate()!=null)&&(this.getSmReleaseFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmExpiryToDate()!=null)&&(this.getSmExpiryToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPrintedFromDate()!=null)&&(this.getSmPrintedFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getSmPrintedToDate()!=null)&&(this.getSmPrintedToDate().toString()!=ReportConst.EMPTY_VAL))){			
			if((this.getSmApplicationType()!=null)&&(this.getSmApplicationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationTypeTotVar(ReportConst.AMP_APPL_TYPE_EQ+this.getSmApplicationType());
			}
			if((this.getSmPaymentType()!=null)&&(this.getSmPaymentType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPaymentTypeTotVar(ReportConst.AMP_PAYMENT_TYPE_EQ+this.getSmPaymentType());
			}
			if((this.getSmCancelledBy()!=null)&&(this.getSmCancelledBy()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCancelledByTotVar(ReportConst.AMP_USER_PRINTED_BY_EQ+this.getSmCancelledBy());
			}
			if((this.getSmCompanyName()!=null)&&(this.getSmCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getSmCompanyName());
			}
			if((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getSmRegistrationType());
			}
			if((this.getSmSupportingDocument()!=null)&&(this.getSmSupportingDocument()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupportingDocumentTotVar(ReportConst.AMP_SUPP_DOC_EQ+this.getSmSupportingDocument());
			}
			if((this.getSmDocStatus()!=null)&&(this.getSmDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDocStatusTotVar(ReportConst.AMP_DOC_STS_NAME_EQ+this.getSmDocStatus());
			}
			if((this.getSmSuppDocStatus()!=null)&&(this.getSmSuppDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSuppDocStatusTotVar(ReportConst.AMP_SUPP_DOC_STATUS_MODE_EQ+this.getSmSuppDocStatus());
			}
			if((this.getSmRegistrationNo()!=null)&&(this.getSmRegistrationNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationNoTotVar(ReportConst.AMP_REG_NO_EQ+this.getSmRegistrationNo());
			}					
			if((this.getSmApplicationNo()!=null)&&(this.getSmApplicationNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationNoTotVar(ReportConst.AMP_APPLICATION_NO_EQ+this.getSmApplicationNo());
			}
			if((this.getSmZone()!=null)&&(this.getSmZone()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setZoneTotVar(ReportConst.AMP_ZONE_NAME_EQ+this.getSmZone());
			}
			if((this.getSmSupplierType()!=null)&&(this.getSmSupplierType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierTypeTotVar(ReportConst.AMP_SUPPLIER_TYPE_EQ+this.getSmSupplierType());
			}
			if((this.getSmMofRegNo()!=null)&&(this.getSmMofRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoTotVar(ReportConst.AMP_MOF_NO_EQ+this.getSmMofRegNo());
			}				
			if((this.getSmMofRegNoFrom()!=null)&&(this.getSmMofRegNoFrom()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoFromTotVar(ReportConst.AMP_REG_MOF_FROM_EQ+this.getSmMofRegNoFrom());
			}
			if((this.getSmMofRegNoTo()!=null)&&(this.getSmMofRegNoTo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoToTotVar(ReportConst.AMP_REG_MOF_TO_EQ+this.getSmMofRegNoTo());
			}			
			if((this.getSmRegistrationStatus()!=null)&&(this.getSmRegistrationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationStatusTotVar(ReportConst.AMP_REG_STATUS_EQ+this.getSmRegistrationStatus());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getSmReleaseFromDate()!=null)&&(this.getSmReleaseFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setReleaseFromDateTotVar(ReportConst.AMP_FROM_RELEASE_DATE_EQ+outputDateFormat.format(this.getSmReleaseFromDate()));
			}
			if((this.getSmExpiryFromDate()!=null)&&(this.getSmExpiryFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setExpiryFromDateTotVar(ReportConst.AMP_FRM_EXPIRY_DT_EQ+outputDateFormat.format(this.getSmExpiryFromDate()));
			}
			if((this.getSmExpiryToDate()!=null)&&(this.getSmExpiryToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setExpiryToDateTotVar(ReportConst.AMP_TO_EXPIRY_DT_EQ+outputDateFormat.format(this.getSmExpiryToDate()));
			}
			if((this.getSmCapitalNaccumFrm()!=null)&&(this.getSmCapitalNaccumFrm()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCapitalNaccumFrmTotVar(ReportConst.AMP_FRM_PUC_EQ+this.getSmCapitalNaccumFrm());
			}
			if((this.getSmCapitalNaccumTo()!=null)&&(this.getSmCapitalNaccumTo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCapitalNaccumToTotVar(ReportConst.AMP_TO_PUC_EQ+this.getSmCapitalNaccumTo());
			}
			if((this.getSmPaidUpCapitalNaccumShare()!=null)&&(this.getSmPaidUpCapitalNaccumShare()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPaidUpCapitalNaccumShareTotVar(ReportConst.AMP_PUC_N_AS_EQ+this.getSmPaidUpCapitalNaccumShare());
			}			
			if((this.getSmNoOfCategory()!=null)&&(this.getSmNoOfCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setNoOfCategoryTotVar(""+this.getSmNoOfCategory());
			}
			if((this.getSmMainCategory()!=null)&&(this.getSmMainCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMainCategoryTotVar(ReportConst.AMP_MAIN_CATEGORY_EQ+this.getSmMainCategory());
			}
			if((this.getSmSubCategory()!=null)&&(this.getSmSubCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSubCategoryTotVar(ReportConst.AMP_SUB_CATEGORY_EQ+this.getSmSubCategory());
			}
			if((this.getSmSubCategoryDivision()!=null)&&(this.getSmSubCategoryDivision()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSubCategoryDivisionTotVar(ReportConst.AMP_SUB_DIV_CATEGORY_EQ+this.getSmSubCategoryDivision());
			}
			if((this.getSmRole()!=null)&&(this.getSmRole()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRoleTotVar(ReportConst.AMP_ROLE_EQ+this.getSmRole());
			}
			if((this.getSmApplicationStatus()!=null)&&(this.getSmApplicationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationStatusTotVar(ReportConst.AMP_APPL_STATUS_EQ+this.getSmApplicationStatus());
			}
			if((this.getSmState()!=null)&&(this.getSmState()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setStateTotVar(ReportConst.AMP_STATE_NAME_EQ+this.getSmState());
			}
			if((this.getSmDistrict()!=null)&&(this.getSmDistrict()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDistrictTotVar(""+this.getSmDistrict());
			}
			if((this.getSmSupplierName()!=null)&&(this.getSmSupplierName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierNameTotVar(""+this.getSmSupplierName());
			}
			if((this.getSmReportType()!=null)&&(this.getSmReportType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setReportTypeTotVar(""+this.getSmReportType());
			}
			if((this.getSmOrganizationType()!=null)&&(this.getSmOrganizationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setOrganizationTypeTotVar(""+this.getSmOrganizationType());
			}
			if((this.getSmKementerian()!=null)&&(this.getSmKementerian()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setKementerianTotVar(""+this.getSmKementerian());
			}
			if((this.getSmNamaAgensi()!=null)&&(this.getSmNamaAgensi()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setNamAgensiTotVar(""+this.getSmNamaAgensi());
			}			
			if((this.getSmComplaintNo()!=null)&&(this.getSmComplaintNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setComplaintNoTotVar(""+this.getSmComplaintNo());
			}
			if((this.getSmProofIdentity()!=null)&&(this.getSmProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_IC_NO_EQ+this.getSmProofIdentity());
			}
			if((this.getSmCompanyStatus()!=null)&&(this.getSmCompanyStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyStatusTotVar(""+this.getSmCompanyStatus());
			}
			if((this.getSmCaseNo()!=null)&&(this.getSmCaseNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCaseNoTotVar(""+this.getSmCaseNo());
			}
			if((this.getSmCompApplStatus()!=null)&&(this.getSmCompApplStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompApplStatusTotVar(""+this.getSmCompApplStatus());
			}
			if((this.getSmCategoryCode()!=null)&&(this.getSmCategoryCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCategoryCodeTotVar(""+this.getSmCategoryCode());
			}
			if((this.getSmSiteVisitOfficer()!=null)&&(this.getSmSiteVisitOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSiteVisitOfficerTotVar(""+this.getSmSiteVisitOfficer());
			}
			if((this.getSmMofNumber()!=null)&&(this.getSmMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getSmMofNumber());
			}		
			rptGenBean.setUrlReportJSPName(reportFileNameJSP);			
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+rptGenBean.getSupportingDocumentTotVar()+rptGenBean.getDocStatusTotVar()+rptGenBean.getSuppDocStatusTotVar()+rptGenBean.getRegistrationNoTotVar()+rptGenBean.getePNoTotVar()+rptGenBean.getApplicationNoTotVar()+rptGenBean.getZoneTotVar()+rptGenBean.getSupplierTypeTotVar()+rptGenBean.getMofRegNoTotVar()+rptGenBean.getMofRegNoFromTotVar()+rptGenBean.getMofRegNoToTotVar()+rptGenBean.getRegistrationStatusTotVar()+rptGenBean.getCapitalNaccumFrmTotVar()+rptGenBean.getCapitalNaccumToTotVar()+	rptGenBean.getPaidUpCapitalNaccumShareTotVar()+rptGenBean.getCategoryTotVar()+rptGenBean.getNoOfCategoryTotVar()+rptGenBean.getMainCategoryTotVar()+rptGenBean.getSubCategoryTotVar()+rptGenBean.getSubCategoryDivisionTotVar()+rptGenBean.getRoleTotVar()+rptGenBean.getApplicationStatusTotVar()+rptGenBean.getCancelledByTotVar()+rptGenBean.getPaymentTypeTotVar()+rptGenBean.getApplicationTypeTotVar()+rptGenBean.getStateTotVar()+rptGenBean.getDistrictTotVar()+rptGenBean.getSupplierNameTotVar()+rptGenBean.getReportTypeTotVar()+rptGenBean.getOrganizationTypeTotVar()+rptGenBean.getKementerianTotVar()+rptGenBean.getNamAgensiTotVar()+rptGenBean.getComplaintNoTotVar()+	
				rptGenBean.getCompanyStatusTotVar()+rptGenBean.getCaseNoTotVar()+rptGenBean.getCompApplStatusTotVar()+rptGenBean.getCategoryCodeTotVar()+rptGenBean.getSiteVisitOfficerTotVar()+rptGenBean.getReleaseFromDateTotVar()+rptGenBean.getApplicationFromDateTotVar()+rptGenBean.getApplicationToDateTotVar()+rptGenBean.getReceiveFromDateTotVar()+rptGenBean.getReceiveToDateTotVar()+rptGenBean.getExpiryFromDateTotVar()+rptGenBean.getExpiryToDateTotVar()+rptGenBean.getPrintedFromDateTotVar()+rptGenBean.getPrintedToDateTotVar()+rptGenBean.getReportValueIdVar()+rptGenBean.getUserIdVar());
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		}else{
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}		
	}	
	/*
	 *Generate SC module report  
	 */
	public void generateSCReport(){
		if((getManagedBean().getReportValue()!=null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))||(getManagedBean().getReportValue()!=null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))||(getManagedBean().getReportValue()!=null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.PDW_EC_RPT_VAL))){
			rptGenBean.setUrlReportJSPName(reportFileNameJSP);	
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName());
			}else{				
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
																												
		}else if(((getScQuotationNo()!=null)&&(getScQuotationNo()!=ReportConst.EMPTY_VAL))||((getFromDate()!=null)&&(getFromDate().toString()!=ReportConst.EMPTY_VAL))||((getToDate()!=null)&&(getToDate().toString()!=ReportConst.EMPTY_VAL))||
				((getScCreatedToMonth()!=null)&&(getScCreatedToMonth().toString()!=ReportConst.EMPTY_VAL))||((getScCreatedFromMonth()!=null)&&(getScCreatedFromMonth().toString()!=ReportConst.EMPTY_VAL))||((getScDeskOfficer()!=null)&&(getScDeskOfficer().toString()!=ReportConst.EMPTY_VAL))||((getScPtj()!=null)&&(getScPtj().toString()!=ReportConst.EMPTY_VAL))||((getScStatus()!=null)&&(getScStatus().toString()!=ReportConst.EMPTY_VAL))||((getScPublishfromDate()!=null)&&(getScPublishfromDate().toString()!=ReportConst.EMPTY_VAL))||((getScPublishToDate()!=null)&&(getScPublishToDate().toString()!=ReportConst.EMPTY_VAL))){	
			
			if((this.getScCreatedToMonth()==null)&&(this.getScCreatedFromMonth()!=null)||(this.getScCreatedToMonth()!=null)&&(this.getScCreatedFromMonth()==null)){				 
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_BOTH_DTS_RPT_SEARCH));
			}
			if((this.getScCreatedToMonth()!=null)&&(this.getScCreatedFromMonth()!=null)){
				if((this.getScCreatedToYear()!=null)&&(this.getScCreatedFromYear()!=null)){
					if(Integer.parseInt(this.getScCreatedToYear())<Integer.parseInt(this.getScCreatedFromYear())){					
						throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
					}else if(Integer.parseInt(this.getScCreatedToYear())== Integer.parseInt(this.getScCreatedFromYear())){
						if(Integer.parseInt(this.getScCreatedToMonth())<Integer.parseInt(this.getScCreatedFromMonth())){
							throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
						}		
					}
					rptGenBean.setScCreatedFromDtVar(ReportConst.AMP_CREAT_FRM_DT_EQ+this.getScCreatedFromMonth()+this.getScCreatedFromYear());
					rptGenBean.setScCreatedToDtVar(ReportConst.AMP_CREAT_TO_DT_EQ+this.getScCreatedToMonth()+this.getScCreatedToYear());
				}
			}
			if((this.getScQuotationNo()!=null)&&(this.getScQuotationNo()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setQuotationIdTotVar(ReportConst.AMP_QUOTE_ID_EQ+this.getScQuotationId());
			}
			if((this.getScDeskOfficer()!=null)&&(this.getScDeskOfficer()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setScDeskOfficerVar(ReportConst.AMP_DESK_OFFCR_EQ+this.getScDeskOfficer());
			}
			if((this.getScPtj()!=null)&&(this.getScPtj()!=ReportConst.EMPTY_VAL)){				 
			rptGenBean.setScPtjVar(ReportConst.AMP_PROCURE_PTJ_EQ+this.getScPtj());
			}			
			if((this.getScStatus()!=null)&&(this.getScStatus()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setScStatusVar(ReportConst.AMP_PROCURE_STATUS_EQ+this.getScStatus());
			}
			StringBuffer procureMethod = new StringBuffer();
			if(this.getScProcureAllList().size()>0){				 
				rptGenBean.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ+this.getScProcureAllList().get(0));
			}else{
				if(this.getScQuotationList().size()>0){
					procureMethod.append(this.getScQuotationList().get(0));
				}
				if(this.getScTenderList().size()>0){
					procureMethod.append(this.getScTenderList().get(0));
				}
				if(this.getScRstrctdTndrList().size()>0){
					procureMethod.append(this.getScRstrctdTndrList().get(0));
				}
				if((procureMethod!=null)&&(procureMethod.toString()!=ReportConst.EMPTY_VAL)){
					rptGenBean.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ+procureMethod);
				}
			}			
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
			if((this.getScPublishfromDate()!=null)&&(this.getScPublishfromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setScPublishFromDateVar(ReportConst.AMP_PUB_FRM_DT_EQ+outputDateFormat.format(this.getScPublishfromDate()));
			}
			if((this.getScPublishToDate()!=null)&&(this.getScPublishToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setScPublishToDateVar(ReportConst.AMP_PUB_TO_DT_EQ+outputDateFormat.format(this.getScPublishToDate()));
			}
			String userRoleLogin = "";
			log.info("authoriztionList="+authoriztionList.toString());
			if(authoriztionList.contains("APPROVER_NAIK_TARAF")){
				userRoleLogin = "APPROVERNT";
			}else if(authoriztionList.contains("APPROVER")){
				userRoleLogin = "APPROVER";
			}else if(authoriztionList.contains("DESK_OFFICER")){
				userRoleLogin = "DESKOFFR";
			}
			
			if((userRoleLogin!=null)&&(userRoleLogin!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setScUserAccessRoleVar(ReportConst.AMP_USER_ACCESS_ROLE_EQ+userRoleLogin);
			}
			rptGenBean.setUrlReportJSPName(reportFileNameJSP);
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getQuotationIdTotVar()+rptGenBean.getScProcureMethodChkVar()+rptGenBean.getScDeskOfficerVar()+rptGenBean.getScPtjVar()+rptGenBean.getScUserAccessRoleVar()+rptGenBean.getScCreatedFromDtVar()+rptGenBean.getScCreatedToDtVar()+rptGenBean.getScPublishFromDateVar()+rptGenBean.getScPublishToDateVar()+rptGenBean.getReportValueIdVar()+rptGenBean.getUserIdVar());				
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}			
		}else{			
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}		
	}	
	/*
	 *Generate CM module report  
	 */
	public void generateCMReport(){	
		if(((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL))||((this.getCmItemName()!=null)&&(this.getCmItemName()!=ReportConst.EMPTY_VAL))||((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL))||((this.getCmItemStatus()!=null)&&(this.getCmItemStatus()!=ReportConst.EMPTY_VAL))||((this.getCmUnspscCode()!=null)&&(this.getCmUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getCmUnspscTitle()!=null)&&(this.getCmUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionType()!=null)&&(this.getCmExtentionType()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionBrand()!=null)&&(this.getCmExtentionBrand()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionMeasurment()!=null)&&(this.getCmExtentionMeasurment()!=ReportConst.EMPTY_VAL))||((this.getCmExtentionColour()!=null)&&(this.getCmExtentionColour()!=ReportConst.EMPTY_VAL))||((this.getCmCompanyName()!=null)&&(this.getCmCompanyName()!=ReportConst.EMPTY_VAL))||((this.getCmRegistrationType()!=null)&&(this.getCmRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getCmItemCategory()!=null)&&(this.getCmItemCategory()!=ReportConst.EMPTY_VAL))||((this.getCmProofIdentity()!=null)&&(this.getCmProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getCmEpNumber()!=null)&&(this.getCmEpNumber()!=ReportConst.EMPTY_VAL))||((this.getCmBusinessRegNo()!=null)&&(this.getCmBusinessRegNo()!=ReportConst.EMPTY_VAL))||((this.getCmIcNumber()!=null)&&(this.getCmIcNumber()!=ReportConst.EMPTY_VAL))||((this.getCmMofNumber()!=null)&&(this.getCmMofNumber()!=ReportConst.EMPTY_VAL))){			
			if((this.getCmItemName()!=null)&&(this.getCmItemName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getCmItemName());
			}
			if((this.getCmItemType()!=null)&&(this.getCmItemType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemTypeTotVar(ReportConst.AMP_ITM_TYPE_EQ+this.getCmItemType());
			}
			if((this.getCmItemStatus()!=null)&&(this.getCmItemStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemStatusTotVar(ReportConst.AMP_ITM_STATUS_EQ+this.getCmItemStatus());
			}
			if((this.getCmUnspscCode()!=null)&&(this.getCmUnspscCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscCodeTotVar(ReportConst.AMP_UNSPSC_CODE_EQ+this.getCmUnspscCode());
			}
			if((this.getCmUnspscTitle()!=null)&&(this.getCmUnspscTitle()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscTitleTotVar(ReportConst.AMP_UNSPSC_TIT_EQ+this.getCmUnspscTitle());
			}			
			if((this.getCmExtentionType()!=null)&&(this.getCmExtentionType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionTypeTotVar(ReportConst.AMP_EXT_TYPE_EQ+this.getCmExtentionType());
			}
			if((this.getCmExtentionBrand()!=null)&&(this.getCmExtentionBrand()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionBrandTotVar(ReportConst.AMP_EXT_BRAND_EQ+this.getCmExtentionBrand());
			}
			if((this.getCmExtentionMeasurment()!=null)&&(this.getCmExtentionMeasurment()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionMeasurmentTotVar(ReportConst.AMP_EXT_MEASURMENT_EQ+this.getCmExtentionMeasurment());
			}
			if((this.getCmExtentionColour()!=null)&&(this.getCmExtentionColour()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionColourTotVar(ReportConst.AMP_EXT_COLOUR_EQ+this.getCmExtentionColour());
			}				
			if((this.getCmCompanyName()!=null)&&(this.getCmCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getCmCompanyName());
			}
			if((this.getCmRegistrationType()!=null)&&(this.getCmRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getCmRegistrationType());
			}
			if((this.getCmItemCategory()!=null)&&(this.getCmItemCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ+this.getCmItemCategory());
			}
			if((this.getCmProofIdentity()!=null)&&(this.getCmProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ+this.getCmProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getCmEpNumber()!=null)&&(this.getCmEpNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setEpNumberTotVar(ReportConst.AMP_EP_NO_EQ+this.getCmEpNumber());
			}
			if((this.getCmBusinessRegNo()!=null)&&(this.getCmBusinessRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setBusinessRegNoTotVar(ReportConst.AMP_BUSI_REG_NO_EQ+this.getCmBusinessRegNo());
			}
			if((this.getCmIcNumber()!=null)&&(this.getCmIcNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setIcNumberTotVar(ReportConst.AMP_IC_NO_EQ+this.getCmIcNumber());
			}
			if((this.getCmMofNumber()!=null)&&(this.getCmMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getCmMofNumber());
			}			
			rptGenBean.setUrlReportJSPName(reportFileNameJSP);
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+rptGenBean.getReportValueIdVar()+rptGenBean.getUserIdVar());
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}			
		}else{
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}		
	}	
	/*
	 *Generate FL module report 
	 */
  public void generateFLReport(){		
		if(((this.getFlPtj()!=null)&&(this.getFlPtj()!=ReportConst.EMPTY_VAL))||((this.getFlContractNo()!=null)&&(this.getFlContractNo()!=ReportConst.EMPTY_VAL))||((this.getFlSst()!=null)&&(this.getFlSst()!=ReportConst.EMPTY_VAL))||((this.getFlJabatan()!=null)&&(this.getFlJabatan()!=ReportConst.EMPTY_VAL))||((this.getFlMinistry()!=null)&&(this.getFlMinistry()!=ReportConst.EMPTY_VAL))||((this.getFlSupplier()!=null)&&(this.getFlSupplier()!=ReportConst.EMPTY_VAL))||((this.getFlStatus()!=null)&&(this.getFlStatus()!=ReportConst.EMPTY_VAL))||((this.getFlDuration()!=null)&&(this.getFlDuration()!=ReportConst.EMPTY_VAL))||((this.getFlRecievingOfficer()!=null)&&(this.getFlRecievingOfficer()!=ReportConst.EMPTY_VAL))||((this.getFlDeliveryAddress()!=null)&&(this.getFlDeliveryAddress()!=ReportConst.EMPTY_VAL))
			||((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL))||((this.getFlContractType()!=null)&&(this.getFlContractType()!=ReportConst.EMPTY_VAL))||((this.getFlRequisitioner()!=null)&&(this.getFlRequisitioner()!=ReportConst.EMPTY_VAL))||((this.getFlMtoContract()!=null)&&(this.getFlMtoContract()!=ReportConst.EMPTY_VAL))||((this.getFlInvoiceNo()!=null)&&(this.getFlInvoiceNo()!=ReportConst.EMPTY_VAL))||((this.getFlDeductionType()!=null)&&(this.getFlDeductionType()!=ReportConst.EMPTY_VAL))||((this.getFlCrNo()!=null)&&(this.getFlCrNo()!=ReportConst.EMPTY_VAL))||((this.getFlApproverName()!=null)&&(this.getFlApproverName()!=ReportConst.EMPTY_VAL))||((this.getFlDocumentType()!=null)&&(this.getFlDocumentType()!=ReportConst.EMPTY_VAL))||((this.getFlCriteria()!=null)&&(this.getFlCriteria()!=ReportConst.EMPTY_VAL))||((this.getFlScoring()!=null)&&(this.getFlScoring()!=ReportConst.EMPTY_VAL))||((this.getFlPkpNo()!=null)&&(this.getFlPkpNo()!=ReportConst.EMPTY_VAL))||((this.getFlDeliveryOrderNo()!=null)&&(this.getFlDeliveryOrderNo()!=ReportConst.EMPTY_VAL))||((this.getFlItemCode()!=null)&&(this.getFlItemCode()!=ReportConst.EMPTY_VAL))||((this.getFlPoAmount()!=null)&&(this.getFlPoAmount()!=ReportConst.EMPTY_VAL))||((this.getFlPrNo()!=null)&&(this.getFlPrNo()!=ReportConst.EMPTY_VAL))||((this.getFlPmo()!=null)&&(this.getFlPmo()!=ReportConst.EMPTY_VAL))
			||((this.getFlItemName()!=null)&&(this.getFlItemName()!=ReportConst.EMPTY_VAL))||((this.getFlItmType()!=null)&&(this.getFlItmType()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))		||((this.getFlContractOrder()!=null)&&(this.getFlContractOrder()!=ReportConst.EMPTY_VAL))||((this.getFlContractRequest()!=null)&&(this.getFlContractRequest()!=ReportConst.EMPTY_VAL))||((this.getFlPurchaseOrderNo()!=null)&&(this.getFlPurchaseOrderNo()!=ReportConst.EMPTY_VAL))){
			
			if((this.getFlPtj()!=null)&&(this.getFlPtj()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPtjTotVar(ReportConst.FL_AMP_PTJ_NAME_EQ+this.getFlPtj());
			}
			if((this.getFlContractNo()!=null)&&(this.getFlContractNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setContractNoTotVar(ReportConst.FL_AMP_CONTRACT_NO_EQ+this.getFlContractNo());
			}
			if((this.getFlSst()!=null)&&(this.getFlSst()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSstTotVar(ReportConst.FL_AMP_SST_NO_EQ+this.getFlSst());
			}			
			if((this.getFlMinistry()!=null)&&(this.getFlMinistry()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ+this.getFlMinistry());
			}
			if((this.getFlSupplier()!=null)&&(this.getFlSupplier()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierTotVar(ReportConst.FL_AMP_SUPPL_NAME_EQ+this.getFlSupplier());
			}
			if((this.getFlStatus()!=null)&&(this.getFlStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setStatusTotVar(ReportConst.FL_AMP_STATUS_EQ+this.getFlStatus());
			}			
			if((this.getFlRecievingOfficer()!=null)&&(this.getFlRecievingOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRecievingOfficerTotVar(ReportConst.FL_AMP_FL_REC_OFF_EQ+this.getFlRecievingOfficer());
			}
			if((this.getFlDeliveryAddress()!=null)&&(this.getFlDeliveryAddress()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeliveryAddressTotVar(ReportConst.FL_AMP_DELI_ADDR_EQ+this.getFlDeliveryAddress());
			}
			if((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setFlRecNoteNoTotVar(ReportConst.FL_AMP_FL_REC_NOTE_EQ+this.getFlRecNoteNo());
			}
			if((this.getFlContractType()!=null)&&(this.getFlContractType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setContractTypeTotVar(ReportConst.FL_AMP_COTRACT_TYPE_EQ+this.getFlContractType());
			}
			if((this.getFlRequisitioner()!=null)&&(this.getFlRequisitioner()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRequisitionerTotVar(ReportConst.FL_AMP_REQUI_NAME_EQ+this.getFlRequisitioner());
			}
			if((this.getFlMtoContract()!=null)&&(this.getFlMtoContract()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMtoContractTotVar(ReportConst.AMP_MTO_CONTR_EQ+this.getFlMtoContract());
			}
			if((this.getFlInvoiceNo()!=null)&&(this.getFlInvoiceNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setInvoiceNoTotVar(ReportConst.AMP_INVOICE_NO_EQ+this.getFlInvoiceNo());
			}
			if((this.getFlDeductionType()!=null)&&(this.getFlDeductionType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeductionTypeTotVar(ReportConst.AMP_DEDUCT_TYPE_EQ+this.getFlDeductionType());
			}
			if((this.getFlCrNo()!=null)&&(this.getFlCrNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCrNoTotVar(ReportConst.AMP_CR_NO_EQ+this.getFlCrNo());
			}
			if((this.getFlApproverName()!=null)&&(this.getFlApproverName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApproverNameTotVar(ReportConst.FL_AMP_APPROVER_NAME_EQ+this.getFlApproverName());
			}
			if((this.getFlDocumentType()!=null)&&(this.getFlDocumentType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDocumentTypeTotVar(ReportConst.AMP_DOC_TYPE_EQ+this.getFlDocumentType());
			}
			if((this.getFlCriteria()!=null)&&(this.getFlCriteria()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCriteriaTotVar(ReportConst.AMP_CRITERIA_EQ+this.getFlCriteria());
			}
			if((this.getFlScoring()!=null)&&(this.getFlScoring()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setScoringTotVar(ReportConst.AMP_SCORING_EQ+this.getFlScoring());
			}
			if((this.getFlPkpNo()!=null)&&(this.getFlPkpNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPkpNoTotVar(ReportConst.FL_AMP_PKP_NO_EQ+this.getFlPkpNo());
			}
			if((this.getFlDeliveryOrderNo()!=null)&&(this.getFlDeliveryOrderNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeliveryOrderNoTotVar(ReportConst.FL_AMP_DELI_ORD_NO_EQ+this.getFlDeliveryOrderNo());
			}
			if((this.getFlItemCode()!=null)&&(this.getFlItemCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemCodeTotVar(ReportConst.AMP_ITM_CODE_EQ+this.getFlItemCode());
			}
			if((this.getFlPoAmount()!=null)&&(this.getFlPoAmount()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPoAmountTotVar(ReportConst.AMP_PO_AMT_EQ+this.getFlPoAmount());
			}
			if((this.getFlPrNo()!=null)&&(this.getFlPrNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPrNoTotVar(ReportConst.AMP_PR_NO_EQ+this.getFlPrNo());
			}
			if((this.getFlPmo()!=null)&&(this.getFlPmo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPmoTotVar(ReportConst.AMP_PMO_EQ+this.getFlPmo());
			}	
			if((this.getFlItemName()!=null)&&(this.getFlItemName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getFlItemName());
			}			
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.FL_AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.FL_AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getFlContractOrder()!=null)&&(this.getFlContractOrder()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFlContractOrderVar(ReportConst.FL_AMP_COTRACT_ORDER_EQ+this.getFlContractOrder());
			}
			if((this.getFlContractRequest()!=null)&&(this.getFlContractRequest()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFlContractRequestVar(ReportConst.FL_AMP_COTRACT_REQUEST_EQ+this.getFlContractRequest());
			}
			if((this.getFlPurchaseOrderNo()!=null)&&(this.getFlPurchaseOrderNo()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFlPurchaseOrderNoVar(ReportConst.FL_AMP_PURCHASE_ORDER_EQ+this.getFlPurchaseOrderNo());
			}			
			rptGenBean.setUrlReportJSPName(reportFileNameJSP);			
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionCodeTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getProofIdentityTotVar()+rptGenBean.getPtjTotVar()+rptGenBean.getContractNoTotVar()+rptGenBean.getSstTotVar()+rptGenBean.getJabatanTotVar()+rptGenBean.getMinistryTotVar()+rptGenBean.getSupplierTotVar()+rptGenBean.getStatusTotVar()+rptGenBean.getDurationTotVar()+rptGenBean.getRecievingOfficerTotVar()+rptGenBean.getDeliveryAddressTotVar()+rptGenBean.getFlRecNoteNoTotVar()+rptGenBean.getContractTypeTotVar()+rptGenBean.getRequisitionerTotVar()+rptGenBean.getMtoContractTotVar()+rptGenBean.getInvoiceNoTotVar()+rptGenBean.getDeductionTypeTotVar()+rptGenBean.getCrNoTotVar()+rptGenBean.getApproverNameTotVar()+rptGenBean.getDocumentTypeTotVar()+rptGenBean.getCriteriaTotVar()+rptGenBean.getScoringTotVar()+rptGenBean.getPkpNoTotVar()+rptGenBean.getDeliveryOrderNoTotVar()+rptGenBean.getItemCodeTotVar()+rptGenBean.getPoAmountTotVar()+rptGenBean.getPrNoTotVar()+rptGenBean.getPmoTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+rptGenBean.getFlContractOrderVar()+rptGenBean.getFlContractRequestVar()+rptGenBean.getFlPurchaseOrderNoVar()+rptGenBean.getReportValueIdVar()+rptGenBean.getUserIdVar());				
			}else{
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}			
			/*try {
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(url);				
			} catch (IOException e) {				
				e.printStackTrace();
			}*/			
		}else{
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}		
	}	
	/*
	 *validate quotNo	 
	 */
	public void quotationNumberValidation() {
		if((this.getScQuotationNo()!=null)&&(this.getScQuotationNo()!=ReportConst.EMPTY_VAL)){
			specialCharValidation();			
		}		
	}
	public void specialCharValidation() {
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
	    Matcher match = pattern.matcher(this.getScQuotationNo());
	    if(match.matches()){
	    	getQuotationNumber();
	    }else{
	    	this.setScQuotationNo(null);
	    	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NUMERIC_CHAR_VALID_ONLY));
	    }	  
	}
	/*
	 *retrieve qtid based on qtno 	 
	 */
	public void getQuotationNumber() {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		log.info("QtNo="+this.getScQuotationNo()); 
		tenderReqDTO.setQtTndrNo(this.getScQuotationNo());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQtByQtNumber(tenderReqDTO);
		ScQt scqt = resDTO.getQtDetail();	
		if(scqt!=null){
			if(scqt.getQtId()!=null){
				log.info("QtId="+scqt.getQtId());
				this.setScQuotationId(scqt.getQtId().toString());			
			}else {
				this.setScQuotationNo(null);
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
			}
		}else{
			this.setScQuotationNo(null);
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
		}
	}
	/*
	 *Clear report criteria 
	 */
	public void resetReportForm(){		
		retriveModuleList();
		this.setFromDate(null);
		this.setToDate(null);	
		this.setScQuotationId(null);
		this.setScQuotationNo(null);	
		this.setScPaymentId(null);
		this.setCmItemType(null);
		this.setCmItemStatus(null);
		this.setCmUnspscCode(null);
		this.setCmUnspscTitle(null);	
		this.setCmCompanyName(null);
		this.setCmRegistrationType(null);	
		this.setCmItemCategory(null);
		this.setCmItemName(null);	
		this.setCmEpNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmIcNumber(null);
		this.setCmMofNumber(null);	
		this.setCmExtentionCode(null);
		this.setCmExtentionType(null);	
		this.setCmExtentionBrand(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		this.setCmProofIdentity(null);
		this.setFlItmType(null);
		this.setFlPtj(null);
		this.setFlContractNo(null);
		this.setFlSst(null);
		this.setFlJabatan(null);
		this.setFlMinistry(null);
		this.setFlSupplier(null); 
		this.setFlStatus(null);
		this.setFlDuration(null);
		this.setFlRecievingOfficer(null);
		this.setFlDeliveryAddress(null);
		this.setFlRecNoteNo(null);
		this.setFlContractType(null);
		this.setFlRequisitioner(null);
		this.setFlMtoContract(null);
		this.setFlInvoiceNo(null);
		this.setFlDeductionType(null);
		this.setFlCrNo(null);
		this.setFlApproverName(null);
		this.setFlDocumentType(null);
		this.setFlCriteria(null);
		this.setFlScoring(null);
		this.setFlPkpNo(null);
		this.setFlDeliveryOrderNo(null);	
		this.setFlItemCode(null);
		this.setFlPoAmount(null);
		this.setFlPrNo(null);
		this.setFlPmo(null);	
		this.setFlItemName(null);
		this.setSmSupportingDocument(null);	
		this.setSmDocStatus(null);
		this.setSmSuppDocStatus(null);	
		this.setSmRegistrationNo(null);
		this.setSmEpNo(null);
		this.setSmApplicationNo(null);
		this.setSmZone(null);
		this.setSmSupplierType(null);
		this.setSmMofRegNo(null);
		this.setSmMofRegNoFrom(null);
		this.setSmMofRegNoTo(null);
		this.setSmRegistrationStatus(null);
		this.setSmCapitalNaccumFrm(null);
		this.setSmCapitalNaccumTo(null);	
		this.setSmPaidUpCapitalNaccumShare(null);
		this.setSmcategory(null);
		this.setSmNoOfCategory(null);
		this.setSmMainCategory(null);
		this.setSmSubCategory(null);
		this.setSmSubCategoryDivision(null);
		this.setSmRole(null);
		this.setSmApplicationStatus(null);
		this.setSmCancelledBy(null);
		this.setSmPaymentType(null);
		this.setSmApplicationType(null);
		this.setSmState(null);
		this.setSmDistrict(null);
		this.setSmSupplierName(null);
		this.setSmReportType(null);
		this.setSmOrganizationType(null);
		this.setSmKementerian(null);
		this.setSmNamaAgensi(null);
		this.setSmComplaintNo(null);
		this.setSmProofIdentity(null);
		this.setSmCompanyStatus(null);
		this.setSmCaseNo(null);
		this.setSmCompApplStatus(null);
		this.setSmCategoryCode(null);
		this.setSmSiteVisitOfficer(null);
		this.setSmReleaseFromDate(null);
		this.setSmReleaseToDate(null);
		this.setSmApplicationFromDate(null);
		this.setSmApplicationToDate(null);
		this.setSmReceiveFromDate(null);	
		this.setSmReceiveToDate(null);	
		this.setSmExpiryFromDate(null);	
		this.setSmExpiryToDate(null);
		this.setSmPrintedFromDate(null);	
		this.setSmPrintedToDate(null);	
		this.setSmCompanyName(null);
		this.setSmSupplier(null);
		this.setSmEpNumber(null);
		this.setSmMofNumber(null);
		this.setSmRegistrationType(null);
		this.setSmBusinessRegNo(null);
		this.setSmIcNumber(null);
		this.setFlContractOrder(null);
		this.setFlContractRequest(null);
		this.setFlPurchaseOrderNo(null);
		//
		this.setScCreatedToYear(null);
		this.setScCreatedToMonth(null);	
		this.setScCreatedFromYear(null);
		this.setScCreatedFromMonth(null);
		this.setScDeskOfficer(null);
		this.setScPtj(null);
		this.setScStatus(null);
		this.setScPublishfromDate(null);
		this.setScPublishToDate(null);
	}	
	public File newDownloadingFile(String url) throws IOException {		
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + "abc.pdf" + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		File localFile = new File("C:\\Jangari\\abc.pdf");
		if(url!=null) {
			log.info(url);
			InputStream input = null;
			try {
				URL obj = new URL(url);
				HttpURLConnection uc = (HttpURLConnection) obj.openConnection();				
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				e.printStackTrace();
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] buf = new byte[1024];
	        try {
	            for (int readNum; (readNum = input.read(buf)) != -1;) {
	                bos.write(buf, 0, readNum); 	               
	            }
	        } catch (IOException ex) {
	        	ex.printStackTrace();
	        }
	        byte[] bytes = bos.toByteArray();	        
	        FileOutputStream fos = new FileOutputStream(localFile);
	        fos.write(bytes);
	        fos.flush();
	        fos.close();			
			input.close();	
		}				
		out.flush();
		out.close(); 		
		return localFile;
	}	
	/*public StreamedContent getStreamedContentPDFFile(String url) {		  
		  PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		  HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		  HttpURLConnection urlConnection = null;
		  HttpServletResponse res = com.liferay.portal.util.PortalUtil
		    .getHttpServletResponse(portletResponse);  
		  res.setHeader("Content-Disposition", "attachment; filename=\""
		    + "NGePReportView.pdf" + "\"");//
		  res.setHeader("Content-Transfer-Encoding", "binary");
		  res.setContentType("application/octet-stream"); 
		  res.flushBuffer();  
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();		  
		  ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
		       // byte[] buf = new byte[1024];
		  if(url!=null) {
		   InputStream input = null;
		   try {
		    URL obj = new URL(url);
		    urlConnection = (HttpURLConnection) obj.openConnection();    
		    //input = new BufferedInputStream(urlConnection.getInputStream()); 
		    //pdfFile=new DefaultStreamedContent(urlConnection.getInputStream(),"octet-stream");   
		             for (int readNum; (readNum = input.read(buf)) != -1;) {
		                 bos.write(buf, 0, readNum);                 
		             }
		            // bos.close();
		    // input.close();
		       //urlConnection.disconnect();
		    InputStream in = urlConnection.getInputStream();
		    byte[] buf = new byte[512];
		    int len;
		    while (true) {
		     len = in.read(buf);
		     if (len == -1) {
		      break;
		     }
		     tmpOut.write(buf, 0, len);
		    }
		    in.close();
		    tmpOut.close();
		    ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0,
		      tmpOut.size());
		    ByteArrayInputStream stream = new ByteArrayInputStream(bb.array());
		    pdfFile= new DefaultStreamedContent(stream, "application/pdf");
		    urlConnection.disconnect();
		         } catch (IOException ex) {
		          ex.printStackTrace();
		         }	  
		    //byte[] bytes = bos.toByteArray();       
		    //render.setDocumentFromString(html);
		          //render.layout();  
		  }
		  return pdfFile;
		 }*/		
	public StreamedContent getReportPdf() {		
		String reportUrl = "http://ngepdevrps01.ngep.com:8891/reports/rwservlet?server=rptsvr_ngepdevrps01_asinst_2&report=Total_Catalog_Item_Unspscwise.jsp&frdt=26/09/2014&todt=30/09/2014&destype=cache&desformat=pdf&userid=ngep_dev/ngep_dev@ngepdb";
		HttpURLConnection urlConnection = null;
		try {
		    URL obj = new URL(reportUrl);
		    urlConnection = (HttpURLConnection) obj.openConnection();	    
		    BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());
		    ResourceResponse portletResponse = (ResourceResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();            
            /*PortletResponse portletResponse = (PortletResponse) FacesContext
            	      .getCurrentInstance().getExternalContext().getResponse();*/
            HttpServletResponse res = PortalUtil.getHttpServletResponse(portletResponse);
            res.setHeader("Content-Disposition", "attachment; filename=\""
            	          + "test-report.pdf" + "\"");//
            res.setHeader("Content-Transfer-Encoding", "binary");
            res.setContentType("application/pdf");
            res.flushBuffer();
            OutputStream out = res.getOutputStream();            	  
            byte[] b = new byte[128];
            int size;
            while ((size = buf.read(b, 0, b.length)) != -1) {
                out.write(b, 0, size);
            }
            ITextRenderer renderer = new ITextRenderer();
            //renderer.setDocument(doc, null);
            //renderer.layout();               
		    renderer.createPDF(out);					
		    //out.write(getPdfStream());
            out.flush();
            out.close();
        } catch (IOException ex) {
	          ex.printStackTrace();
	    }catch (DocumentException e) {
			e.printStackTrace();
		}		
		return null;
	}
	public List<SelectItem> generateMonthList(){		
		return scReportBackingBean.generateMonthList();
	}
	public List<String> generateYearList(){		
		 return scReportBackingBean.generateYearList(); 
	}
	public void createdToDtUpdate(){
		this.setScCreatedToYear(this.getScCreatedFromYear());
	}
	public void onSelectEffectedValues(){		
		if(this.getScProcureAllList().size()>0){			
			rptGenBean.setScProcureQuoteEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
			rptGenBean.setScProcureRestrctdEditView(true);			 	
			scQuotationList = new ArrayList<SelectItem>();	
			scTenderList = new ArrayList<SelectItem>();	
			scRstrctdTndrList = new ArrayList<SelectItem>();			
		}else if((this.getScQuotationList().size()>0)&&(this.getScTenderList().size()>0)){			
			rptGenBean.setScProcureAllEditView(true);			
			rptGenBean.setScProcureRestrctdEditView(true);
		}else if((this.getScRstrctdTndrList().size()>0)&&(this.getScTenderList().size()>0)){
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureQuoteEditView(true);			
		}else if((this.getScRstrctdTndrList().size()>0)&&(this.getScQuotationList().size()>0)){	
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
		}else{
			rptGenBean.setScProcureAllEditView(false);
			rptGenBean.setScProcureQuoteEditView(false);
			rptGenBean.setScProcureTenderEditView(false);
			rptGenBean.setScProcureRestrctdEditView(false);
		}
	}	
}
