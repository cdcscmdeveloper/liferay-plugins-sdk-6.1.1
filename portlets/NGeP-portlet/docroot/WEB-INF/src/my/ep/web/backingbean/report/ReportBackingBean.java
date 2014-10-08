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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
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
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;
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
	//-------------
	List<SelectItem> reportList;
	ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<SelectItem> moduleList;
	private List<SelectItem> regTypeList;
	List<String> authoriztionList;
	private ScQt scqt;
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();	
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();
	
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();;	
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();;
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();;
	
	private String userId;
	private String roleCode;	
	private Date fromDate;
	private Date toDate;
	/*SS start*/
	private String quotationId;
	private String quotationNo;
	
	private String paymentId;
	/*SS end*/
	/*CM start*/
	private String itemType;
	private String itemStatus;
	private String unspscCode;
	private String unspscTitle;	
	private String companyName;
	private String registrationType;	
	private String itemCategory;
	private String itemName;
	
	private String epNumber;
	private String businessRegNo;
	private String icNumber;
	private String mofNumber;	
	private String extentionCode;
	private String extentionType;	
	private String extentionBrand;
	private String extentionMeasurment;
	private String extentionColour;
	/*CM end*/	
	/*FL start*/
	private String ptj;
	private String contractNo;
	private String sst;
	private String jabatan;
	private String ministry;
	private String supplier;
	private String status;
	private String duration;
	private String recievingOfficer;
	private String deliveryAddress;
	private String flRecNoteNo;
	private String contractType;
	private String requisitioner;
	private String mtoContract;
	private String invoiceNo;
	private String deductionType;
	private String crNo;
	private String approverName;
	private String documentType;
	private String criteria;
	private String scoring;
	private String pkpNo;
	private String deliveryOrderNo;	
	private String itemCode;
	private String poAmount;
	private String prNo;
	private String pmo;
   /*FL end*/
   /*SM start*/
	private String supportingDocument;	
	private String docStatus;
	private String suppDocStatus;	
	private String registrationNo;
	private String ePNo;
	private String applicationNo;
	private String zone;
	private String supplierType;
	private String mofRegNo;
	private String mofRegNoFrom;
	private String mofRegNoTo;
	private String registrationStatus;
	private String capitalNaccumFrm;
	private String capitalNaccumTo;	
	private String paidUpCapitalNaccumShare;
	private String category;
	private String noOfCategory;
	private String mainCategory;
	private String subCategory;
	private String subCategoryDivision;
	private String role;
	private String applicationStatus;
	private String cancelledBy;
	private String paymentType;
	private String applicationType;
	private String state;
	private String district;
	private String supplierName;
	private String reportType;
	private String organizationType;
	private String kementerian;
	private String namAgensi;
	private String complaintNo;
	private String proofIdentity;
	private String companyStatus;
	private String caseNo;
	private String compApplStatus;
	private String categoryCode;
	private String siteVisitOfficer;
	private Date releaseFromDate;
	private Date releaseToDate;
	private Date applicationFromDate;
	private Date applicationToDate;
	private Date receiveFromDate;	
	private Date receiveToDate;	
	private Date expiryFromDate;	
	private Date expiryToDate;
	private Date printedFromDate;	
	private Date printedToDate;
	
	private String generatedURL;
	private String reportURL;
	
	private StreamedContent pdfFile;
	private String constructedParamString;
	/*SM end*/
	
	
	public StreamedContent getPdfFile() {
		return pdfFile;
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
	
	public Date getPrintedFromDate() {
		return printedFromDate;
	}
	public void setPrintedFromDate(Date printedFromDate) {
		this.printedFromDate = printedFromDate;
	}
	public Date getPrintedToDate() {
		return printedToDate;
	}
	public void setPrintedToDate(Date printedToDate) {
		this.printedToDate = printedToDate;
	}	
	public Date getExpiryFromDate() {
		return expiryFromDate;
	}
	public void setExpiryFromDate(Date expiryFromDate) {
		this.expiryFromDate = expiryFromDate;
	}
	public Date getExpiryToDate() {
		return expiryToDate;
	}
	public void setExpiryToDate(Date expiryToDate) {
		this.expiryToDate = expiryToDate;
	}
	public Date getReceiveFromDate() {
		return receiveFromDate;
	}
	public void setReceiveFromDate(Date receiveFromDate) {
		this.receiveFromDate = receiveFromDate;
	}
	public Date getReceiveToDate() {
		return receiveToDate;
	}
	public void setReceiveToDate(Date receiveToDate) {
		this.receiveToDate = receiveToDate;
	}
	public Date getApplicationFromDate() {
		return applicationFromDate;
	}	
	public void setApplicationFromDate(Date applicationFromDate) {
		this.applicationFromDate = applicationFromDate;
	}
	public Date getApplicationToDate() {
		return applicationToDate;
	}
	public void setApplicationToDate(Date applicationToDate) {
		this.applicationToDate = applicationToDate;
	}
	public Date getReleaseFromDate() {
		return releaseFromDate;
	}
	public void setReleaseFromDate(Date releaseFromDate) {
		this.releaseFromDate = releaseFromDate;
	}
	public Date getReleaseToDate() {
		return releaseToDate;
	}
	public void setReleaseToDate(Date releaseToDate) {
		this.releaseToDate = releaseToDate;
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
	public String getSiteVisitOfficer() {
		return siteVisitOfficer;
	}
	public void setSiteVisitOfficer(String siteVisitOfficer) {
		this.siteVisitOfficer = siteVisitOfficer;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getSuppDocStatus() {
		return suppDocStatus;
	}
	public void setSuppDocStatus(String suppDocStatus) {
		this.suppDocStatus = suppDocStatus;
	}
	public String getGeneratedURL() {
		return generatedURL;
	}
	public void setGeneratedURL(String generatedURL) {
		this.generatedURL = generatedURL;
	}
	public String getCompApplStatus() {
		return compApplStatus;
	}
	public void setCompApplStatus(String compApplStatus) {
		this.compApplStatus = compApplStatus;
	}
	public String getCaseNo() {
		return caseNo;
	}
	
	public String getReportURL() {
		return reportURL;
	}
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getCompanyStatus() {
		return companyStatus;
	}
	public void setCompanyStatus(String companyStatus) {
		this.companyStatus = companyStatus;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getSupplierType() {
		return supplierType;
	}
	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}
	public String getMofRegNo() {
		return mofRegNo;
	}
	public void setMofRegNo(String mofRegNo) {
		this.mofRegNo = mofRegNo;
	}
	public String getMofRegNoFrom() {
		return mofRegNoFrom;
	}
	public void setMofRegNoFrom(String mofRegNoFrom) {
		this.mofRegNoFrom = mofRegNoFrom;
	}
	public String getMofRegNoTo() {
		return mofRegNoTo;
	}
	public void setMofRegNoTo(String mofRegNoTo) {
		this.mofRegNoTo = mofRegNoTo;
	}
	public String getRegistrationStatus() {
		return registrationStatus;
	}
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}
	public String getCapitalNaccumFrm() {
		return capitalNaccumFrm;
	}
	public void setCapitalNaccumFrm(String capitalNaccumFrm) {
		this.capitalNaccumFrm = capitalNaccumFrm;
	}
	public String getCapitalNaccumTo() {
		return capitalNaccumTo;
	}
	public void setCapitalNaccumTo(String capitalNaccumTo) {
		this.capitalNaccumTo = capitalNaccumTo;
	}
	public String getPaidUpCapitalNaccumShare() {
		return paidUpCapitalNaccumShare;
	}
	public void setPaidUpCapitalNaccumShare(String paidUpCapitalNaccumShare) {
		this.paidUpCapitalNaccumShare = paidUpCapitalNaccumShare;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getNoOfCategory() {
		return noOfCategory;
	}
	public void setNoOfCategory(String noOfCategory) {
		this.noOfCategory = noOfCategory;
	}
	public String getMainCategory() {
		return mainCategory;
	}
	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getSubCategoryDivision() {
		return subCategoryDivision;
	}
	public void setSubCategoryDivision(String subCategoryDivision) {
		this.subCategoryDivision = subCategoryDivision;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getCancelledBy() {
		return cancelledBy;
	}
	public void setCancelledBy(String cancelledBy) {
		this.cancelledBy = cancelledBy;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}
	public String getKementerian() {
		return kementerian;
	}
	public void setKementerian(String kementerian) {
		this.kementerian = kementerian;
	}
	public String getNamAgensi() {
		return namAgensi;
	}
	public void setNamAgensi(String namAgensi) {
		this.namAgensi = namAgensi;
	}
	public String getComplaintNo() {
		return complaintNo;
	}
	public void setComplaintNo(String complaintNo) {
		this.complaintNo = complaintNo;
	}	
	public String getSupportingDocument() {
		return supportingDocument;
	}
	public void setSupportingDocument(String supportingDocument) {
		this.supportingDocument = supportingDocument;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	
	public String getRegistrationNo() {
		return registrationNo;
	}
	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}
	public String getePNo() {
		return ePNo;
	}
	public void setePNo(String ePNo) {
		this.ePNo = ePNo;
	}
	public String getApplicationNo() {
		return applicationNo;
	}
	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
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
	public String getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
	}
	public String getQuotationNo() {
		return quotationNo;
	}
	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getItemStatus() {
		return itemStatus;
	}
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}
	public String getUnspscCode() {
		return unspscCode;
	}
	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}
	public String getUnspscTitle() {
		return unspscTitle;
	}
	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	public String getItemCategory() {
		return itemCategory;
	}
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getProofIdentity() {
		return proofIdentity;
	}
	public void setProofIdentity(String proofIdentity) {
		this.proofIdentity = proofIdentity;
	}
	public String getEpNumber() {
		return epNumber;
	}
	public void setEpNumber(String epNumber) {
		this.epNumber = epNumber;
	}
	public String getBusinessRegNo() {
		return businessRegNo;
	}
	public void setBusinessRegNo(String businessRegNo) {
		this.businessRegNo = businessRegNo;
	}
	public String getIcNumber() {
		return icNumber;
	}
	public void setIcNumber(String icNumber) {
		this.icNumber = icNumber;
	}
	public String getMofNumber() {
		return mofNumber;
	}
	public void setMofNumber(String mofNumber) {
		this.mofNumber = mofNumber;
	}
	public String getExtentionCode() {
		return extentionCode;
	}
	public void setExtentionCode(String extentionCode) {
		this.extentionCode = extentionCode;
	}
	public String getExtentionType() {
		return extentionType;
	}
	public void setExtentionType(String extentionType) {
		this.extentionType = extentionType;
	}
	public String getExtentionBrand() {
		return extentionBrand;
	}
	public void setExtentionBrand(String extentionBrand) {
		this.extentionBrand = extentionBrand;
	}
	public String getExtentionMeasurment() {
		return extentionMeasurment;
	}
	public void setExtentionMeasurment(String extentionMeasurment) {
		this.extentionMeasurment = extentionMeasurment;
	}
	public String getExtentionColour() {
		return extentionColour;
	}
	public void setExtentionColour(String extentionColour) {
		this.extentionColour = extentionColour;
	}
	public String getPtj() {
		return ptj;
	}
	public void setPtj(String ptj) {
		this.ptj = ptj;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getSst() {
		return sst;
	}
	public void setSst(String sst) {
		this.sst = sst;
	}
	public String getJabatan() {
		return jabatan;
	}
	public void setJabatan(String jabatan) {
		this.jabatan = jabatan;
	}
	public String getMinistry() {
		return ministry;
	}
	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getRecievingOfficer() {
		return recievingOfficer;
	}
	public void setRecievingOfficer(String recievingOfficer) {
		this.recievingOfficer = recievingOfficer;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}
	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getRequisitioner() {
		return requisitioner;
	}
	public void setRequisitioner(String requisitioner) {
		this.requisitioner = requisitioner;
	}
	public String getMtoContract() {
		return mtoContract;
	}
	public void setMtoContract(String mtoContract) {
		this.mtoContract = mtoContract;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getDeductionType() {
		return deductionType;
	}
	public void setDeductionType(String deductionType) {
		this.deductionType = deductionType;
	}
	public String getCrNo() {
		return crNo;
	}
	public void setCrNo(String crNo) {
		this.crNo = crNo;
	}
	public String getApproverName() {
		return approverName;
	}
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getCriteria() {
		return criteria;
	}
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	public String getScoring() {
		return scoring;
	}
	public void setScoring(String scoring) {
		this.scoring = scoring;
	}
	public String getPkpNo() {
		return pkpNo;
	}
	public void setPkpNo(String pkpNo) {
		this.pkpNo = pkpNo;
	}
	public String getDeliveryOrderNo() {
		return deliveryOrderNo;
	}
	public void setDeliveryOrderNo(String deliveryOrderNo) {
		this.deliveryOrderNo = deliveryOrderNo;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getPoAmount() {
		return poAmount;
	}
	public void setPoAmount(String poAmount) {
		this.poAmount = poAmount;
	}
	public String getPrNo() {
		return prNo;
	}
	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}
	public String getPmo() {
		return pmo;
	}
	public void setPmo(String pmo) {
		this.pmo = pmo;
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
		selectItem.setValue(ModuleCodeConst.SUPPLIER_SELECTION);
		moduleList.add(selectItem);			
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_CM_TTL));
		selectItem1.setValue(ModuleCodeConst.CATALOGUE_MANAGEMENT);
		moduleList.add(selectItem1);			
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_FL_TTL));
		selectItem2.setValue(ModuleCodeConst.ORDER_FULFILLMENT);
		moduleList.add(selectItem2);
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.MOD_SM_TTL));
		selectItem3.setValue(ModuleCodeConst.SUPPLIER_MANAGEMENT);
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
		getManagedBean().setReportNameValue(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());		
		if(getManagedBean().getModuleName()!=null){
			retriveUserrloeByUserId(getManagedBean().getModuleName());
			if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){			
				retriveReportListBySCModule();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT)){
				retriveReportListByCMModule();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)){
				retriveReportListByFLModule();
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT)){
				retriveReportListBySMModule();
			}
		}		
		getManagedBean().setReportList(reportList);		
		getManagedBean().setRptGenBean(rptGenBean);		
		resetReportForm();		
	}
	/*
	 *Retrieve Role code by User id 
	 */
	public List<String> retriveUserrloeByUserId(String moduleName) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(moduleName, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		authoriztionList=new ArrayList<String>();   
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		reqDTO.setLanguageCode("en");
		UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
		List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();		
		if (null != roleDesc) {			
			for (PmRoleDesc pmRoleDesc : roleDesc) {				
				pmRoleDesc.getPmRole().getRoleCode();
				log.info("Role Code="+pmRoleDesc.getPmRole().getRoleCode());
				if(ReportConst.SUPPLIER_USER.toString().equalsIgnoreCase(pmRoleDesc.getPmRole().getRoleCode())) {					
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
				}
			}			
		}		
		return authoriztionList;
	}
	/*
	 *Retrieve report list by SM module 
	 */
	public void retriveReportListBySMModule() {
		reportList=new ArrayList<SelectItem>();		
		//if((authoriztionList.contains(ReportConst.UPS_SDO.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SK.toString()))||(authoriztionList.contains(RoleCode.UPS_ADMIN.toString()))){
			SelectItem selectItem1= new SelectItem();		
			selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_APP_RPT_NAME));
			selectItem1.setValue(ReportConst.SM_LST_OF_APP_RPT_VAL);
			reportList.add(selectItem1);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_SDO.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SK.toString()))){
			SelectItem selectItem2= new SelectItem();
			selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_NAME));
			selectItem2.setValue(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL);
			reportList.add(selectItem2);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem3= new SelectItem();
			selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_NAME));
			selectItem3.setValue(ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_VAL);
			reportList.add(selectItem3);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))||(authoriztionList.contains(ReportConst.AGENCY.toString()))||(authoriztionList.contains(ReportConst.EP_COORDINATOR.toString()))){
			SelectItem selectItem4= new SelectItem();
			selectItem4.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_NAME));
			selectItem4.setValue(ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_VAL);
			reportList.add(selectItem4);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))){
			SelectItem selectItem5= new SelectItem();
			selectItem5.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_NAME));
			selectItem5.setValue(ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL);
			reportList.add(selectItem5);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))){
			SelectItem selectItem6= new SelectItem();
			selectItem6.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_NAME));
			selectItem6.setValue(ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_VAL);
			reportList.add(selectItem6);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem7= new SelectItem();
			selectItem7.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_NAME));
			selectItem7.setValue(ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL);
			reportList.add(selectItem7);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))){
			SelectItem selectItem8= new SelectItem();
			selectItem8.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_NAME));
			selectItem8.setValue(ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_VAL);
			reportList.add(selectItem8);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem9= new SelectItem();
			selectItem9.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_NAME));
			selectItem9.setValue(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL);
			reportList.add(selectItem9);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))){
			SelectItem selectItem10= new SelectItem();
			selectItem10.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_NAME));
			selectItem10.setValue(ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_VAL);
			reportList.add(selectItem10);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))){
			SelectItem selectItem11= new SelectItem();
			selectItem11.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_NAME));
			selectItem11.setValue(ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_VAL);
			reportList.add(selectItem11);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem12= new SelectItem();
			selectItem12.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_NAME));
			selectItem12.setValue(ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_VAL);
			reportList.add(selectItem12);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_COORDINATOR.toString()))){
			SelectItem selectItem13= new SelectItem();
			selectItem13.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_RPT_NAME));
			selectItem13.setValue(ReportConst.SM_CAT_STATIC_RPT_VAL);
			reportList.add(selectItem13);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem14= new SelectItem();
			selectItem14.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_NAME));
			selectItem14.setValue(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL);
			reportList.add(selectItem14);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem15= new SelectItem();
			selectItem15.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_NAME));
			selectItem15.setValue(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL);
			reportList.add(selectItem15);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem16= new SelectItem();
			selectItem16.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERIFY_RPT_NAME));
			selectItem16.setValue(ReportConst.SM_APPL_VERIFY_RPT_VAL);
			reportList.add(selectItem16);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))){
			SelectItem selectItem17= new SelectItem();
			selectItem17.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_APPL_APPROVED_RPT_NAME));
			selectItem17.setValue(ReportConst.SM_APPL_APPROVED_RPT_VAL);
			reportList.add(selectItem17);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))){
			SelectItem selectItem18= new SelectItem();
			selectItem18.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_APPL_REJECTED_RPT_NAME));
			selectItem18.setValue(ReportConst.SM_APPL_REJECTED_RPT_VAL);
			reportList.add(selectItem18);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(ReportConst.UPS_SVC.toString()))||(authoriztionList.contains(ReportConst.UPS_SVC_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_SVC_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_SVC_SK.toString()))||(authoriztionList.contains(ReportConst.UPS_SVO.toString()))||(authoriztionList.contains(ReportConst.UPS_SVO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_SVO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_SVO_SK.toString()))){
			SelectItem selectItem19= new SelectItem();
			selectItem19.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME));
			selectItem19.setValue(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_VAL);
			reportList.add(selectItem19);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))){
			SelectItem selectItem20= new SelectItem();
			selectItem20.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_NAME));
			selectItem20.setValue(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL);
			reportList.add(selectItem20);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem21= new SelectItem();
			selectItem21.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_PRE_EXP_COMP_RPT_NAME));
			selectItem21.setValue(ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL);
			reportList.add(selectItem21);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_ADMIN.toString()))){
			SelectItem selectItem22= new SelectItem();
			selectItem22.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_MONTH_WISE_RPT_NAME));
			selectItem22.setValue(ReportConst.SM_LST_MONTH_WISE_RPT_VAL);
			reportList.add(selectItem22);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))){
			SelectItem selectItem23= new SelectItem();
			selectItem23.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_NAME));
			selectItem23.setValue(ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_VAL);
			reportList.add(selectItem23);
		//}
		//if((authoriztionList.contains(ReportConst.APPROVER.toString()))){
			SelectItem selectItem24= new SelectItem();
			selectItem24.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_NAME));
			selectItem24.setValue(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL);
			reportList.add(selectItem24);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_SDO.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_SDO_SK.toString()))||(authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem25= new SelectItem();
			selectItem25.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMP_ADDR_RPT_NAME));
			selectItem25.setValue(ReportConst.SM_LST_COMP_ADDR_RPT_VAL);
			reportList.add(selectItem25);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem26= new SelectItem();
			selectItem26.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_NAME));
			selectItem26.setValue(ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL);
			reportList.add(selectItem26);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))||(authoriztionList.contains(ReportConst.EP_COORDINATOR.toString()))){
			SelectItem selectItem27= new SelectItem();
			selectItem27.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_SUPPL_STAT_RPT_NAME));
			selectItem27.setValue(ReportConst.SM_SUPPL_STAT_RPT_VAL);
			reportList.add(selectItem27);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem28= new SelectItem();
			selectItem28.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_BPK_USER_PROF_RPT_NAME));
			selectItem28.setValue(ReportConst.SM_BPK_USER_PROF_RPT_VAL);
			reportList.add(selectItem28);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem29= new SelectItem();
			selectItem29.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_BPK_ADMIN_PROF_RPT_NAME));
			selectItem29.setValue(ReportConst.SM_BPK_ADMIN_PROF_RPT_VAL);
			reportList.add(selectItem29);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem30= new SelectItem();
			selectItem30.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_NAME));
			selectItem30.setValue(ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_VAL);
			reportList.add(selectItem30);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem31= new SelectItem();
			selectItem31.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_AGENCY_STAT_SUMM_RPT_NAME));
			selectItem31.setValue(ReportConst.SM_AGENCY_STAT_SUMM_RPT_VAL);
			reportList.add(selectItem31);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem32= new SelectItem();
			selectItem32.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_QUESTIONAIRE_RPT_NAME));
			selectItem32.setValue(ReportConst.SM_QUESTIONAIRE_RPT_VAL);
			reportList.add(selectItem32);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem33= new SelectItem();
			selectItem33.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME));
			selectItem33.setValue(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL);
			reportList.add(selectItem33);
		//}
		//if((authoriztionList.contains(ReportConst.EP_ADMIN.toString()))){
			SelectItem selectItem34= new SelectItem();
			selectItem34.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_NAME));
			selectItem34.setValue(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL);
			reportList.add(selectItem34);
		//}
		//if((authoriztionList.contains(ReportConst.BPK_ADMIN.toString()))){
			SelectItem selectItem35= new SelectItem();
			selectItem35.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_STAT_BY_CAT_RPT_NAME));
			selectItem35.setValue(ReportConst.SM_SUPP_STAT_BY_CAT_RPT_VAL);
			reportList.add(selectItem35);
		//}
		//if((authoriztionList.contains(ReportConst.UPS_PO.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_HQ.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SA.toString()))||(authoriztionList.contains(ReportConst.UPS_PO_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.EP_ADMIN.toString()))){
			SelectItem selectItem36= new SelectItem();
			selectItem36.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_NAME));
			selectItem36.setValue(ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL);
			reportList.add(selectItem36);
		//}
		//if((authoriztionList.contains(ReportConst.DISC_SECRETARY.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_HQ.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_SA.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_SK.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))){
			SelectItem selectItem37= new SelectItem();
			selectItem37.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_NAME));
			selectItem37.setValue(ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_VAL);
			reportList.add(selectItem37);
		//}
		//if((authoriztionList.contains(ReportConst.DISC_SECRETARY.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_HQ.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_SA.toString()))||(authoriztionList.contains(ReportConst.DISC_SECRETARY_SK.toString()))){
			SelectItem selectItem38= new SelectItem();
			selectItem38.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_NAME));
			selectItem38.setValue(ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_VAL);
			reportList.add(selectItem38);
		//}
		//if(){
			SelectItem selectItem39= new SelectItem();
			selectItem39.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_NAME));
			selectItem39.setValue(ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_VAL);
			reportList.add(selectItem39);
		//}
		//if(){
			SelectItem selectItem40= new SelectItem();
			selectItem40.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_NAME));
			selectItem40.setValue(ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_VAL);
			reportList.add(selectItem40);
		//}
		//if(){
			SelectItem selectItem41= new SelectItem();
			selectItem41.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_NAME));
			selectItem41.setValue(ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_VAL);
			reportList.add(selectItem41);
		//}
			//if(){
			SelectItem selectItem42= new SelectItem();
			selectItem42.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_PEROFRM_RPT_NAME));
			selectItem42.setValue(ReportConst.SM_SUPP_PEROFRM_RPT_VAL);
			reportList.add(selectItem42);
		//}
			//if(){
			SelectItem selectItem43= new SelectItem();
			selectItem43.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_NAME));
			selectItem43.setValue(ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_VAL);
			reportList.add(selectItem43);
		//}
			//if(){
			SelectItem selectItem44= new SelectItem();
			selectItem44.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_NAME));
			selectItem44.setValue(ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_VAL);
			reportList.add(selectItem44);
		//}
			//if(){
			SelectItem selectItem45= new SelectItem();
			selectItem45.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_NAME));
			selectItem45.setValue(ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_VAL);
			reportList.add(selectItem45);
		//}
			//if(){
			SelectItem selectItem46= new SelectItem();
			selectItem46.setLabel(CommonMethod.getMessageCommon(ReportConst.SM_DETAILS_OF_APPLICATION_RPT_NAME));
			selectItem46.setValue(ReportConst.SM_DETAILS_OF_APPLICATION_RPT_VAL);
			reportList.add(selectItem46);
		//}
			
	}
	/*
	 *Retrieve report list by SC module 
	 */
	public void retriveReportListBySCModule() {
		reportList=new ArrayList<SelectItem>();
		
		SelectItem selectItem= new SelectItem();
		selectItem.setLabel(CommonMethod.getMessageCommon(ReportConst.SPPPP_PEMATUHAN_OC_RPT_NAME));
		selectItem.setValue(ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL);
		reportList.add(selectItem);
		
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.KPPP1_PEMATUHAN_OC_RPT_NAME));
		selectItem1.setValue(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL);
		reportList.add(selectItem1);
		
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.PDDW_OC_RPT_NAME));
		selectItem2.setValue(ReportConst.PDDW_OC_RPT_VAL);
		reportList.add(selectItem2);
		
		SelectItem selectItem28= new SelectItem();
		selectItem28.setLabel(CommonMethod.getMessageCommon(ReportConst.AKPP_PEMATUHAN_OC_RPT_NAME));
		selectItem28.setValue(ReportConst.AKPP_PEMATUHAN_OC_RPT_VAL);
		reportList.add(selectItem28);
		
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_TEC_RPT_NAME));
		selectItem3.setValue(ReportConst.SPPSAP2_TEC_RPT_VAL);
		reportList.add(selectItem3);			
		
		SelectItem selectItem4= new SelectItem();
		selectItem4.setLabel(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_NAME));
		selectItem4.setValue(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL);
		reportList.add(selectItem4);
		
		SelectItem selectItem5= new SelectItem();
		selectItem5.setLabel(CommonMethod.getMessageCommon(ReportConst.AKPT_TEC_RPT_NAME));
		selectItem5.setValue(ReportConst.AKPT_TEC_RPT_VAL);
		reportList.add(selectItem5);
		
		SelectItem selectItem6= new SelectItem();
		selectItem6.setLabel(CommonMethod.getMessageCommon(ReportConst.TECSPEC_TEC_RPT_NAME));
		selectItem6.setValue(ReportConst.TECSPEC_TEC_RPT_VAL);
		reportList.add(selectItem6);
		
		SelectItem selectItem23= new SelectItem();
		selectItem23.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDAK_TEC_RPT_NAME));
		selectItem23.setValue(ReportConst.PSDAK_TEC_RPT_VAL);
		reportList.add(selectItem23);
		
		SelectItem selectItem24= new SelectItem();
		selectItem24.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDABK_TEC_RPT_NAME));
		selectItem24.setValue(ReportConst.PSDABK_TEC_RPT_VAL);
		reportList.add(selectItem24);
		
		SelectItem selectItem7= new SelectItem();
		selectItem7.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_NAME));
		selectItem7.setValue(ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL);
		reportList.add(selectItem7);
		SelectItem selectItem21= new SelectItem();
		selectItem21.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_NAME));
		selectItem21.setValue(ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL);
		reportList.add(selectItem21);
		
		SelectItem selectItem8= new SelectItem();
		selectItem8.setLabel(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_FEC_RPT_NAME));
		selectItem8.setValue(ReportConst.SPPSAP2_FEC_RPT_VAL);
		reportList.add(selectItem8);
		
		SelectItem selectItem9= new SelectItem();
		selectItem9.setLabel(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_NAME));
		selectItem9.setValue(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL);
		reportList.add(selectItem9);
		
		SelectItem selectItem10= new SelectItem();
		selectItem10.setLabel(CommonMethod.getMessageCommon(ReportConst.AKPT_FEC_RPT_NAME));
		selectItem10.setValue(ReportConst.AKPT_FEC_RPT_VAL);
		reportList.add(selectItem10);
		
		SelectItem selectItem11= new SelectItem();
		selectItem11.setLabel(CommonMethod.getMessageCommon(ReportConst.HARGA_TAWARAN_FEC_RPT_NAME));
		selectItem11.setValue(ReportConst.HARGA_TAWARAN_FEC_RPT_VAL);
		reportList.add(selectItem11);
		
		SelectItem selectItem27= new SelectItem();
		selectItem27.setLabel(CommonMethod.getMessageCommon(ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_NAME));
		selectItem27.setValue(ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_VAL);
		reportList.add(selectItem27);
		
		SelectItem selectItem25= new SelectItem();
		selectItem25.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_NAME));
		selectItem25.setValue(ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL);
		reportList.add(selectItem25);
		
		SelectItem selectItem26= new SelectItem();
		selectItem26.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_NAME));
		selectItem26.setValue(ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL);
		reportList.add(selectItem26);
		
		SelectItem selectItem12= new SelectItem();
		selectItem12.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_FINRS_FEC_RPT_NAME));
		selectItem12.setValue(ReportConst.OVRL_FINRS_FEC_RPT_VAL);
		reportList.add(selectItem12);
		
		SelectItem selectItem13= new SelectItem();
		selectItem13.setLabel(CommonMethod.getMessageCommon(ReportConst.SPPP1_PEMATUHAN_EC_RPT_NAME));
		selectItem13.setValue(ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL);
		reportList.add(selectItem13);
		
		SelectItem selectItem14= new SelectItem();
		selectItem14.setLabel(CommonMethod.getMessageCommon(ReportConst.KPP1_PEMATUHAN_EC_RPT_NAME));
		selectItem14.setValue(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL);
		reportList.add(selectItem14);
		
		SelectItem selectItem29 = new SelectItem();
		selectItem29.setLabel(CommonMethod.getMessageCommon(ReportConst.PDW_EC_RPT_NAME));
		selectItem29.setValue(ReportConst.PDW_EC_RPT_VAL);
		reportList.add(selectItem29);
		
		SelectItem selectItem15= new SelectItem();
		selectItem15.setLabel(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_EC_RPT_NAME));
		selectItem15.setValue(ReportConst.SPPSAP2_EC_RPT_VAL);
		reportList.add(selectItem15);
		
		SelectItem selectItem16= new SelectItem();
		selectItem16.setLabel(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_NAME));
		selectItem16.setValue(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL);
		reportList.add(selectItem16);
		
		SelectItem selectItem17= new SelectItem();
		selectItem17.setLabel(CommonMethod.getMessageCommon(ReportConst.AKPTPH_EC_RPT_NAME));
		selectItem17.setValue(ReportConst.AKPTPH_EC_RPT_VAL);
		reportList.add(selectItem17);
		
		SelectItem selectItem18= new SelectItem();
		selectItem18.setLabel(CommonMethod.getMessageCommon(ReportConst.TECSPEC_EC_RPT_NAME));
		selectItem18.setValue(ReportConst.TECSPEC_EC_RPT_VAL);
		reportList.add(selectItem18);
		
		SelectItem selectItem30= new SelectItem();
		selectItem30.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_NAME));
		selectItem30.setValue(ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL);
		reportList.add(selectItem30);
		
		SelectItem selectItem31= new SelectItem();
		selectItem31.setLabel(CommonMethod.getMessageCommon(ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_NAME));
		selectItem31.setValue(ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL);
		reportList.add(selectItem31);
		
		SelectItem selectItem32= new SelectItem();
		selectItem32.setLabel(CommonMethod.getMessageCommon(ReportConst.HARGA_TAWARAN_EC_RPT_NAME));
		selectItem32.setValue(ReportConst.HARGA_TAWARAN_EC_RPT_VAL);
		reportList.add(selectItem32);
		
		SelectItem selectItem33= new SelectItem();
		selectItem33.setLabel(CommonMethod.getMessageCommon(ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_NAME));
		selectItem33.setValue(ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_VAL);
		reportList.add(selectItem33);
		
		SelectItem selectItem34= new SelectItem();
		selectItem34.setLabel(CommonMethod.getMessageCommon(ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_NAME));
		selectItem34.setValue(ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL);
		reportList.add(selectItem34);
		
		SelectItem selectItem35= new SelectItem();
		selectItem35.setLabel(CommonMethod.getMessageCommon(ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_NAME));
		selectItem35.setValue(ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL);
		reportList.add(selectItem35);
		
		SelectItem selectItem19= new SelectItem();
		selectItem19.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_NAME));
		selectItem19.setValue(ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL);
		reportList.add(selectItem19);
		
		SelectItem selectItem22= new SelectItem();
		selectItem22.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_NAME));
		selectItem22.setValue(ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL);
		reportList.add(selectItem22);
		
		SelectItem selectItem20= new SelectItem();
		selectItem20.setLabel(CommonMethod.getMessageCommon(ReportConst.OVRL_FINRS_EC_RPT_NAME));
		selectItem20.setValue(ReportConst.OVRL_FINRS_EC_RPT_VAL);
		reportList.add(selectItem20);
		
		
		
				
		
	}
	/*
	 *Retrieve report list by CM module 
	 */
	public void retriveReportListByCMModule() {
		reportList=new ArrayList<SelectItem>();		
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem= new SelectItem();		
				selectItem.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_PUB_CAT_ITM_RPT_NAME));
				selectItem.setValue(ReportConst.LST_PUB_CAT_ITM_RPT_VAL);
				reportList.add(selectItem);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem1= new SelectItem();
				selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_APPR_CAT_ITM_RPT_NAME));
				selectItem1.setValue(ReportConst.LST_APPR_CAT_ITM_RPT_VAL);
				reportList.add(selectItem1);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem2= new SelectItem();
				selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_REJ_CAT_ITM_RPT_NAME));
				selectItem2.setValue(ReportConst.LST_REJ_CAT_ITM_RPT_VAL);
				reportList.add(selectItem2);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem3= new SelectItem();
				selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME));
				selectItem3.setValue(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL);
				reportList.add(selectItem3);			
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem4= new SelectItem();
				selectItem4.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_EXP_CAT_ITM_RPT_NAME));
				selectItem4.setValue(ReportConst.LST_EXP_CAT_ITM_RPT_VAL);
				reportList.add(selectItem4);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem5= new SelectItem();
				selectItem5.setLabel(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_STS_RPT_NAME));
				selectItem5.setValue(ReportConst.TOT_CAT_ITM_STS_RPT_VAL);
				reportList.add(selectItem5);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem6= new SelectItem();
				selectItem6.setLabel(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_NAME));
				selectItem6.setValue(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL);
				reportList.add(selectItem6);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem7= new SelectItem();
				selectItem7.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_STD_PRC_RPT_NAME));
				selectItem7.setValue(ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL);
				reportList.add(selectItem7);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem8= new SelectItem();
				selectItem8.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_NAME));
				selectItem8.setValue(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL);
				reportList.add(selectItem8);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem9= new SelectItem();
				selectItem9.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_NAME));
				selectItem9.setValue(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL);
				reportList.add(selectItem9);
			//}
			//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem10= new SelectItem();
				selectItem10.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_NAME));
				selectItem10.setValue(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL);
				reportList.add(selectItem10);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem11= new SelectItem();
				selectItem11.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_NAME));
				selectItem11.setValue(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL);
				reportList.add(selectItem11);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem12= new SelectItem();
				selectItem12.setLabel(CommonMethod.getMessageCommon(ReportConst.SUM_REJ_CAT_ITM_RPT_NAME));
				selectItem12.setValue(ReportConst.SUM_REJ_CAT_ITM_RPT_VAL);
				reportList.add(selectItem12);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
				SelectItem selectItem13= new SelectItem();
				selectItem13.setLabel(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_NAME));
				selectItem13.setValue(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL);
				reportList.add(selectItem13);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CD_OFFICER.toString()))){
				SelectItem selectItem14= new SelectItem();
				selectItem14.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_UNSPSC_CODE_REQ_RPT_NAME));
				selectItem14.setValue(ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL);
				reportList.add(selectItem14);
			//}
			//if((authoriztionList.contains(RoleCode.CM_ADMIN.toString()))||(authoriztionList.contains(RoleCode.CD_OFFICER.toString()))||(authoriztionList.contains(RoleCode.CD_COMMITTEE.toString()))){
				SelectItem selectItem15= new SelectItem();
				selectItem15.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_NAME));
				selectItem15.setValue(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL);
				reportList.add(selectItem15);
			//}
			//if((authoriztionList.contains(RoleCode.CD_OFFICER.toString()))){
				SelectItem selectItem16= new SelectItem();
				selectItem16.setLabel(CommonMethod.getMessageCommon(ReportConst.LST_ITM_CODE_RPT_NAME));
				selectItem16.setValue(ReportConst.LST_ITM_CODE_RPT_VAL);
				reportList.add(selectItem16);
			//}	 
	}	
	/*
	 * Retrieve report list by FL module
	 */
	public void retriveReportListByFLModule() {
		reportList=new ArrayList<SelectItem>();
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){		
			SelectItem selectItem1= new SelectItem();
			selectItem1.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_MIN_NAME));
			selectItem1.setValue(ReportConst.CON_REQ_RPT_MIN_VAL);
			reportList.add(selectItem1);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){		
			SelectItem selectItem2= new SelectItem();
			selectItem2.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_MIN_NAME));
			selectItem2.setValue(ReportConst.CON_ORD_RPT_MIN_VAL);
			reportList.add(selectItem2);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){			
			SelectItem selectItem3= new SelectItem();
			selectItem3.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_MIN_NAME));
			selectItem3.setValue(ReportConst.DEL_ORD_RPT_MIN_VAL);
			reportList.add(selectItem3);			
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem4= new SelectItem();
			selectItem4.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_MIN_NAME));
			selectItem4.setValue(ReportConst.FL_REC_NOTE_RPT_MIN_VAL);
			reportList.add(selectItem4);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){		
			SelectItem selectItem5= new SelectItem();
			selectItem5.setLabel(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_MIN_NAME));
			selectItem5.setValue(ReportConst.LATE_DEL_RPT_MIN_VAL);
			reportList.add(selectItem5);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){		
			SelectItem selectItem6= new SelectItem();
			selectItem6.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME));
			selectItem6.setValue(ReportConst.DEL_ORD_REJ_RPT_MIN_VAL);
			reportList.add(selectItem6);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem7= new SelectItem();
			selectItem7.setLabel(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_MIN_NAME));
			selectItem7.setValue(ReportConst.SHP_INSTR_RPT_MIN_VAL);
			reportList.add(selectItem7);
		//}	
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem8= new SelectItem();
			selectItem8.setLabel(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME));
			selectItem8.setValue(ReportConst.STP_INSTR_DEL_RPT_MIN_VAL);
			reportList.add(selectItem8);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))){
			SelectItem selectItem9= new SelectItem();
			selectItem9.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME));
			selectItem9.setValue(ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL);
			reportList.add(selectItem9);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem10= new SelectItem();
			selectItem10.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME));
			selectItem10.setValue(ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL);
			reportList.add(selectItem10);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem11= new SelectItem();
			selectItem11.setLabel(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_MIN_NAME));
			selectItem11.setValue(ReportConst.PYMT_MATCH_RPT_MIN_VAL);
			reportList.add(selectItem11);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem12= new SelectItem();
			selectItem12.setLabel(CommonMethod.getMessageCommon(ReportConst.DED_RPT_MIN_NAME));
			selectItem12.setValue(ReportConst.DED_RPT_MIN_VAL);
			reportList.add(selectItem12);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))){
			SelectItem selectItem13= new SelectItem();
			selectItem13.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_MIN_NAME));
			selectItem13.setValue(ReportConst.CON_REQ_CANC_RPT_MIN_VAL);
			reportList.add(selectItem13);
		//}	
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem14= new SelectItem();
			selectItem14.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_MIN_NAME));
			selectItem14.setValue(ReportConst.CON_ORD_CANC_RPT_MIN_VAL);
		    reportList.add(selectItem14);
		//}	
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem15= new SelectItem();
			selectItem15.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME));
			selectItem15.setValue(ReportConst.DEL_ORD_CANC_RPT_MIN_VAL);
			reportList.add(selectItem15);
		//}
		//if((authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem16= new SelectItem();
			selectItem16.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME));
			selectItem16.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL);
			reportList.add(selectItem16);
		//}	
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem17= new SelectItem();
			selectItem17.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_MIN_NAME));
			selectItem17.setValue(ReportConst.INV_CANC_RPT_MIN_VAL);
			reportList.add(selectItem17);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem18= new SelectItem();
			selectItem18.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_MIN_NAME));
			selectItem18.setValue(ReportConst.YEP_RPT_MIN_VAL);
			reportList.add(selectItem18);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem19= new SelectItem();
			selectItem19.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME));
			selectItem19.setValue(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL);
			reportList.add(selectItem19);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))){
			SelectItem selectItem20= new SelectItem();
			selectItem20.setLabel(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_MIN_NAME));
			selectItem20.setValue(ReportConst.SUP_PERF_RPT_MIN_VAL);
			reportList.add(selectItem20);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem21= new SelectItem();
			selectItem21.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_CEN_PEN_NAME));
			selectItem21.setValue(ReportConst.CON_REQ_RPT_CEN_PEN_VAL);
			reportList.add(selectItem21);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem22= new SelectItem();
			selectItem22.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_CEN_PEN_NAME));
			selectItem22.setValue(ReportConst.CON_ORD_RPT_CEN_PEN_VAL);
			reportList.add(selectItem22);
		//}	
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem23= new SelectItem();
			selectItem23.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME));
			selectItem23.setValue(ReportConst.DEL_ORD_RPT_CEN_PEN_VAL);
			reportList.add(selectItem23);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem24= new SelectItem();
			selectItem24.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME));
			selectItem24.setValue(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL);
			reportList.add(selectItem24);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem25= new SelectItem();
			selectItem25.setLabel(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME));
			selectItem25.setValue(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL);
			reportList.add(selectItem25);
		//}	
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem26= new SelectItem();
			selectItem26.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME));
			selectItem26.setValue(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL);
			reportList.add(selectItem26);
		//}	
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.MIN_CONTRACT_ADMIN.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem27= new SelectItem();
			selectItem27.setLabel(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME));
			selectItem27.setValue(ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL);
			reportList.add(selectItem27);
		//}
		//if((authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem28= new SelectItem();
			selectItem28.setLabel(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME));
			selectItem28.setValue(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL);
			reportList.add(selectItem28);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))){
			SelectItem selectItem29= new SelectItem();
			selectItem29.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME));
			selectItem29.setValue(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL);
			reportList.add(selectItem29);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem30= new SelectItem();
			selectItem30.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME));
			selectItem30.setValue(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL);
			reportList.add(selectItem30);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem31= new SelectItem();
			selectItem31.setLabel(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME));
			selectItem31.setValue(ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL);
			reportList.add(selectItem31);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem32= new SelectItem();
			selectItem32.setLabel(CommonMethod.getMessageCommon(ReportConst.DED_RPT_CEN_PEN_NAME));
			selectItem32.setValue(ReportConst.DED_RPT_CEN_PEN_VAL);
			reportList.add(selectItem32);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem33= new SelectItem();
			selectItem33.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME));
			selectItem33.setValue(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL);
			reportList.add(selectItem33);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem34= new SelectItem();
			selectItem34.setLabel(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME));
			selectItem34.setValue(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL);
			reportList.add(selectItem34);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem35= new SelectItem();
			selectItem35.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME));
			selectItem35.setValue(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL);
			reportList.add(selectItem35);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem36= new SelectItem();
			selectItem36.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME));
			selectItem36.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL);
			reportList.add(selectItem36);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem37= new SelectItem();
			selectItem37.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_CEN_PEN_NAME));
			selectItem37.setValue(ReportConst.INV_CANC_RPT_CEN_PEN_VAL);
			reportList.add(selectItem37);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem38= new SelectItem();
			selectItem38.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_CEN_PEN_NAME));
			selectItem38.setValue(ReportConst.YEP_RPT_CEN_PEN_VAL);
			reportList.add(selectItem38);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem39= new SelectItem();
			selectItem39.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME));
			selectItem39.setValue(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL);
			reportList.add(selectItem39);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem40= new SelectItem();
			selectItem40.setLabel(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME));
			selectItem40.setValue(ReportConst.SUP_PERF_RPT_CEN_PEN_VAL);
			reportList.add(selectItem40);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem41= new SelectItem();
			selectItem41.setLabel(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_RPT_PO_NAME));
			selectItem41.setValue(ReportConst.PUR_REQ_RPT_PO_VAL);
			reportList.add(selectItem41);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem42= new SelectItem();
			selectItem42.setLabel(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_RPT_PO_NAME));
			selectItem42.setValue(ReportConst.PUR_ORD_RPT_PO_VAL);
			reportList.add(selectItem42);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem43= new SelectItem();
			selectItem43.setLabel(CommonMethod.getMessageCommon(ReportConst.EMR_PROCUR_RPT_PO_NAME));
			selectItem43.setValue(ReportConst.EMR_PROCUR_RPT_PO_VAL);
			reportList.add(selectItem43);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem44= new SelectItem();
			selectItem44.setLabel(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_REJ_RPT_PO_NAME));
			selectItem44.setValue(ReportConst.PUR_ORD_REJ_RPT_PO_VAL);
			reportList.add(selectItem44);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem45= new SelectItem();
			selectItem45.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_PO_NAME));
			selectItem45.setValue(ReportConst.DEL_ORD_RPT_PO_VAL);
			reportList.add(selectItem45);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem46= new SelectItem();
			selectItem46.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_PO_NAME));
			selectItem46.setValue(ReportConst.FL_REC_NOTE_RPT_PO_VAL);
			reportList.add(selectItem46);
		//}
		//if((authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem47= new SelectItem();
			selectItem47.setLabel(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_PO_NAME));
			selectItem47.setValue(ReportConst.LATE_DEL_RPT_PO_VAL);
			reportList.add(selectItem47);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem48= new SelectItem();
			selectItem48.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_PO_NAME));
			selectItem48.setValue(ReportConst.DEL_ORD_REJ_RPT_PO_VAL);
			reportList.add(selectItem48);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem49= new SelectItem();
			selectItem49.setLabel(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_PO_NAME));
			selectItem49.setValue(ReportConst.STP_INSTR_DEL_RPT_PO_VAL);
			reportList.add(selectItem49);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))){
			SelectItem selectItem50= new SelectItem();
			selectItem50.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME));
			selectItem50.setValue(ReportConst.INV_RPT_VIEW_SUPL_PO_VAL);
			reportList.add(selectItem50);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem51= new SelectItem();
			selectItem51.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME));
			selectItem51.setValue(ReportConst.INV_RPT_VIEW_PTJ_PO_VAL);
			reportList.add(selectItem51);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem52= new SelectItem();
			selectItem52.setLabel(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_PO_NAME));
			selectItem52.setValue(ReportConst.PYMT_MATCH_RPT_PO_VAL);
			reportList.add(selectItem52);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem53= new SelectItem();
			selectItem53.setLabel(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_CANC_RPT_PO_NAME));
			selectItem53.setValue(ReportConst.PUR_REQ_CANC_RPT_PO_VAL);
			reportList.add(selectItem53);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem54= new SelectItem();
			selectItem54.setLabel(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_CANC_RPT_PO_NAME));
			selectItem54.setValue(ReportConst.PUR_ORD_CANC_RPT_PO_VAL);
			reportList.add(selectItem54);
		//}
		//if((authoriztionList.contains(ReportConst.SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(ReportConst.SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_USER.toString()))||(authoriztionList.contains(RoleCode.MOF_SUPPLIER_ADMIN.toString()))||(authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem55= new SelectItem();
			selectItem55.setLabel(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_PO_NAME));
			selectItem55.setValue(ReportConst.DEL_ORD_CANC_RPT_PO_VAL);
			reportList.add(selectItem55);
		//}
		//if((authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem56= new SelectItem();
			selectItem56.setLabel(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME));
			selectItem56.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL);
			reportList.add(selectItem56);
		//}
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem57= new SelectItem();
			selectItem57.setLabel(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_PO_NAME));
			selectItem57.setValue(ReportConst.INV_CANC_RPT_PO_VAL);
			reportList.add(selectItem57);
		//}
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem58= new SelectItem();
			selectItem58.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_PO_NAME));
			selectItem58.setValue(ReportConst.YEP_RPT_PO_VAL);
			reportList.add(selectItem58);
		//}	
		//if((authoriztionList.contains(RoleCode.PAY_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem59= new SelectItem();
			selectItem59.setLabel(CommonMethod.getMessageCommon(ReportConst.YEP_AP58A_RPT_PO_NAME));
			selectItem59.setValue(ReportConst.YEP_AP58A_RPT_PO_VAL);
			reportList.add(selectItem59);
		//}	
		//if((authoriztionList.contains(RoleCode.REQUISITIONER.toString()))||(authoriztionList.contains(RoleCode.RECEIVING_OFFICER.toString()))||(authoriztionList.contains(RoleCode.APPROVER.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem60= new SelectItem();
			selectItem60.setLabel(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_PO_NAME));
			selectItem60.setValue(ReportConst.SUP_PERF_RPT_PO_VAL);
			reportList.add(selectItem60);	
		//}	
		//if((authoriztionList.contains(RoleCode.BPK_CCA.toString()))||(authoriztionList.contains(RoleCode.BPK_PCA.toString()))){
			SelectItem selectItem61= new SelectItem();
			selectItem61.setLabel(CommonMethod.getMessageCommon(ReportConst.SUMM_RPT_PAN_ITM_NAME));
			selectItem61.setValue(ReportConst.SUMM_RPT_PAN_ITM_VAL);
			reportList.add(selectItem61);
		//}
	}	
	/*
	 *Enable  criteria variables by report name 
	 */
	public void criteriaVariablesByReport() {
		getManagedBean().setRptGenBean(null);
		getManagedBean().setReportName(ReportConst.EMPTY_VAL);
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		levOneCatLst = new ArrayList<SelectItem>();
		levTwoCatLst = new ArrayList<SelectItem>();
		levThreeCatLst = new ArrayList<SelectItem>();		
		if((getManagedBean().getModuleName())!=null && (getManagedBean().getReportNameValue())!=null){
			rptGenBean = new ReportsGenerateBean();					
			if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))){ 
				criteriaVariablesByReportByCM();					
			}else if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))){ 
				criteriaVariablesByReportBySC();				
			}else if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))){ 
				criteriaVariablesByReportByFL();
			}else if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))){ 
				criteriaVariablesByReportBySM();
			}
			resetReportForm();
			retrieveDates();
			getManagedBean().setRptGenBean(rptGenBean);
			resetReportForm();
		}
	}
	/*
	 *Enable SM module criteria by report name 
	 */
	public void criteriaVariablesByReportBySM(){
		switch (getManagedBean().getReportNameValue()) {		
	        case ReportConst.SM_LST_OF_APP_RPT_VAL:
	        	rptGenBean.setSupportingDocumentView(true);
				rptGenBean.setDocStatusView(true);
				rptGenBean.setSuppDocStatusView(true);
				rptGenBean.setRegistrationNoView(true);
				rptGenBean.setePNoView(true);
				rptGenBean.setApplicationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_APP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL:	        	
	        	rptGenBean.setRegistrationNoView(true);
				rptGenBean.setePNoView(true);
				rptGenBean.setApplicationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_NAME));
				
	            break;
	        case ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_VAL:			
				rptGenBean.setReleaseDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_VIRT_CERT_RLSD_ON_DAY_RPT_NAME));			
	            break;
	        case ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_VAL:
	        	rptGenBean.setMandatoryZoneView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
	        	rptGenBean.setMandatoryMofRegNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_PRFL_OF_REG_COMP_MIN_FIN_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_VAL:
	        	//rptGenBean.setZoneView(true);
	        	rptGenBean.setMandatoryZoneView(true);
	        	//rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
	        	//rptGenBean.setMofRegNoFromView(true);
	        	//rptGenBean.setMofRegNoToView(true);
	        	rptGenBean.setMandatoryMofRegNoView(true);
	        	rptGenBean.setCompanyNameView(true);
	        	rptGenBean.setRegistrationTypeView(true);
	        	rptGenBean.setRegistrationStatusView(true);
	        	rptGenBean.setPaidUpCapitalNaccumShareView(true);
	        	rptGenBean.setCapitalNaccumView(true);
	        	rptGenBean.setCategoryView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveCategoryLevOne();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SUPP_TYPE_BUMI_PUT_STS_CAT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_VAL:
	        	rptGenBean.setZoneView(true);
	        	rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMofRegNoFromView(true);
	        	rptGenBean.setMofRegNoToView(true);
	        	rptGenBean.setCompanyNameView(true);	        	
	        	rptGenBean.setRoleView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_DIRS_OWNS_CEO_OR_EQU_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_VAL:
	        	
	        	//rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
	        	rptGenBean.setMofRegNoFromView(true);
	        	rptGenBean.setMofRegNoToView(true);
	        	rptGenBean.setCompanyNameView(true);	        	
	        	rptGenBean.setProofIdentityView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_DIRS_OWNS_CEO_OR_EQU_BELONG_MOR_THAN_ONE_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_VAL:
	        	rptGenBean.setZoneView(true);
	        	rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMofRegNoFromView(true);
	        	rptGenBean.setMofRegNoToView(true);
	        	rptGenBean.setCompanyNameView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_COMP_SORT_ALPHABETIC_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL:
	        	rptGenBean.setZoneView(true);
	        	rptGenBean.setSupplierTypeView(true);	        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_VAL:	
	        	rptGenBean.setExtraTwoColumnView(true);
	        	rptGenBean.setProofIdentityView(true);	        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_PERSON_OR_STAFF_MORE_ONE_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_VAL:	        	
	        	//rptGenBean.setZoneView(true);
	        	rptGenBean.setMandatoryZoneView(true);
	        	//rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
	        	//rptGenBean.setApplicationStatusView(true);
	        	rptGenBean.setMandatoryApplicationStatusView(true);
	        	rptGenBean.setCancelledByView(true);
	        	//rptGenBean.setApplicationDateView(true);
	        	rptGenBean.setMandatoryApplicationDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_MOF_REG_CANCEL_REQ_SUPP_AUTO_CANC_STM_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_VAL:	        	
	        	rptGenBean.setPaymentTypeView(true);
	        	rptGenBean.setSupplierView(true);
	        	rptGenBean.setApplicationTypeView(true);	  
	        	rptGenBean.setApplicationDateView(true);		
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OF_PAYMENT_PROCESS_REG_FEE_RPT_NAME));			
	            break;
	        case ReportConst.SM_CAT_STATIC_RPT_VAL:	        	
	        	rptGenBean.setStateView(true);
	        	rptGenBean.setDistrictView(true);
	        	rptGenBean.setCategoryView(true);
	        	rptGenBean.setRegistrationStatusView(true);
	        	rptGenBean.setNoOfCategoryView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveCategoryLevOne();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_RPT_NAME));	
				retrieveState();
	            break;
	        case ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL:		        	
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_NAME));			
	            break;
	        case ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL:
	        	rptGenBean.setStateView(true);	        	
	        	rptGenBean.setCategoryView(true);
	        	rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveState();
				retrieveCategoryLevOne();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_VERIFY_RPT_VAL:		        	
	        	rptGenBean.setReceiveDateView(true);						
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERIFY_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_APPROVED_RPT_VAL:		        	
	        	rptGenBean.setSupplierNameView(true);
				rptGenBean.setMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_APPROVED_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_REJECTED_RPT_VAL:		        	
	        	rptGenBean.setSupplierNameView(true);
				rptGenBean.setMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_REJECTED_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_VAL:		        	
	        	rptGenBean.setCompanyNameView(true);
				rptGenBean.setCategoryView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveCategoryLevOne();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_APPL_PEND_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL:
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setReportTypeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setApplicationDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL:	        	
				//rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
				rptGenBean.setCompanyNameView(true);
				//rptGenBean.setExpiryDateView(true);
				rptGenBean.setMandatoryExpiryDateView(true);
				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_PRE_EXP_COMP_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_MONTH_WISE_RPT_VAL:				
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_MONTH_WISE_RPT_NAME));			
	            break;
	        case ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_VAL:	
	        	rptGenBean.setApplicationTypeView(true);
	        	rptGenBean.setSupplierTypeView(true);
				rptGenBean.setReceiveDateView(true);				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_APPL_VERI_PERFORM_CLNT_CHARTER_ACHIVE_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL:	
	        	rptGenBean.setRegistrationTypeView(true);
	        	rptGenBean.setSupplierNameView(true);
	        	rptGenBean.setMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_COMP_ADDR_RPT_VAL:	
	        	//rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setMandatorySupplierTypeView(true);
	        	rptGenBean.setCompApplStatusView(true);
				//rptGenBean.setPrintedDateView(true);	
	        	rptGenBean.setMandatoryPrintedDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMP_ADDR_RPT_NAME));			
	            break;
	        case ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_VAL:
	        	rptGenBean.setExtraTwoColumnView(true);
	        	rptGenBean.setReportTypeView(true);
				rptGenBean.setApplicationDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_TOT_NUM_DAILY_APPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPPL_STAT_RPT_VAL:	
	        	rptGenBean.setZoneView(true);
	        	rptGenBean.setStateView(true);
	        	rptGenBean.setDistrictView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveState();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPPL_STAT_RPT_NAME));			
	            break;
	        case ReportConst.SM_BPK_USER_PROF_RPT_VAL:
	        	rptGenBean.setExtraTwoColumnView(true);
	        	rptGenBean.setZoneView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_BPK_USER_PROF_RPT_NAME));			
	            break;
	        case ReportConst.SM_BPK_ADMIN_PROF_RPT_VAL:	
	        	rptGenBean.setExtraTwoColumnView(true);
	        	rptGenBean.setZoneView(true);	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_BPK_ADMIN_PROF_RPT_NAME));			
	            break;
	        case ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_VAL:	
	        	//rptGenBean.setZoneView(true);
	        	rptGenBean.setMandatoryZoneView(true);
	        	rptGenBean.setOrganizationTypeView(true);
	        	rptGenBean.setKementerianView(true);
	        	rptGenBean.setNamAgensiView(true);
	        	//rptGenBean.setCompApplStatusView(true);
	        	rptGenBean.setMandatoryApplicationStatusView(true);
	        	rptGenBean.setCompanyStatusView(true);
				rptGenBean.setMandatoryApplicationDateView(true);
				
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_ONLN_APPL_COMP_INFO_ACCESS_RPT_NAME));			
	            break;
	        case ReportConst.SM_AGENCY_STAT_SUMM_RPT_VAL:		        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_AGENCY_STAT_SUMM_RPT_NAME));			
	            break;
	        case ReportConst.SM_QUESTIONAIRE_RPT_VAL:	
	        	rptGenBean.setZoneView(true);	        	
	        	rptGenBean.setSupplierTypeView(true);
	        	rptGenBean.setReportTypeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_QUESTIONAIRE_RPT_NAME));			
	            break;
	        case ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_PAYMNT_RECPT_UPN_PAYMNT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL:	        	
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPP_STAT_BY_CAT_RPT_VAL:
	           	rptGenBean.setExtraTwoColumnView(true);
	        	//rptGenBean.setZoneView(true);
	           	rptGenBean.setMandatoryZoneView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_STAT_BY_CAT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_VAL:
	        	rptGenBean.setRegistrationTypeView(true);
	        	rptGenBean.setStateView(true);
	        	rptGenBean.setDocStatusView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveState();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DAILY_APPL_SUBM_BASIC_ACC_MOF_REG_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_VAL:
	        	rptGenBean.setSupplierNameView(true);
	        	rptGenBean.setCaseNoView(true);
	        	rptGenBean.setRegistrationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_DISCPLNRY_ACTN_IMPOS_AGAINST_SUPPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_VAL:
	        	rptGenBean.setSupplierNameView(true);
	        	rptGenBean.setComplaintNoView(true);	        	
	        	rptGenBean.setRegistrationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_COMPLAINTS_AGAINST_SUPPL_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_VAL:
	        	rptGenBean.setExtraTwoColumnView(true);
	        	rptGenBean.setProofIdentityView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_BLACKLLSTD_DIRECTOR_OWNER_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_VAL:	        		        	
	        	rptGenBean.setePNoView(true);
	        	rptGenBean.setMofNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_CONTACT_PERSON_BNK_AC_BRNCH_RPT_NAME));			
	            break;
	        case ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_VAL:	        		        	
	        	rptGenBean.setRegistrationTypeView(true);
	        	rptGenBean.setSupplierNameView(true);
	        	rptGenBean.setCategoryCodeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_THRESHLD_SUPP_TRNS_VAL_RPT_NAME));			
	            break;
	        case ReportConst.SM_SUPP_PEROFRM_RPT_VAL:	        		        	
	        	rptGenBean.setRegistrationTypeView(true);
	        	rptGenBean.setSupplierNameView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_SUPP_PEROFRM_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_VAL:	        		        	
	        	rptGenBean.setApplicationStatusView(true);
	        	rptGenBean.setSupplierNameView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_STAT_APPL_REQ_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_VAL:	        		        	
	        	rptGenBean.setSiteVisitOfficerView(true);
	        	rptGenBean.setSupplierNameView(true);
	         	rptGenBean.setStateView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				retrieveState();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_SITE_VISIT_OFFCR_SITE_VISIT_LOCATION_RPT_NAME));			
	            break;
	        case ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_VAL:	        		        	
	        	rptGenBean.setSupplierNameView(true);
	         	rptGenBean.setStateView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				retrieveState();
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_LST_OR_STAT_PENDING_SITE_VISIT_PERFRM_SITE_VISIT_RPT_NAME));			
	            break;
	        case ReportConst.SM_DETAILS_OF_APPLICATION_RPT_VAL:	        	
	        	rptGenBean.setApplicationNoView(true);
	        	rptGenBean.setExtraTwoColumnView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);	
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SM_DETAILS_OF_APPLICATION_RPT_NAME));			
	            break;
	        default:            
	            break;
		}
	}
	/*
	 *Enable CM module criteria by report name 
	 */
	public void criteriaVariablesByReportByCM(){
		
		switch (getManagedBean().getReportNameValue()) {		
	        case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);
				rptGenBean.setItemStatusView(true);
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PUB_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
				//};
	            break;
	        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemCategoryView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_APPR_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setItemTypeView(true);
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					rptGenBean.setItemCategoryView(false);
				//};
	            break;
	        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_REJ_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);						
				//};
	            break;
	        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME));	
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){					
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
				//};
	            break;
	        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_EXP_CAT_ITM_RPT_NAME));
				//if((authoriztionList!=null)&&(authoriztionList.contains(RoleCode.CM_ADMIN.toString()))){					
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
				//};
	            break;
	        case ReportConst.TOT_CAT_ITM_STS_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_STS_RPT_NAME));	
	            break;
	        case ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);					
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);
				rptGenBean.setItemStatusView(false);
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);					
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_STD_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);						
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_NAME));	
	            break;
	        case ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);					
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_NAME));
	            break;
	        case ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_NAME));	
	            break;
	        case ReportConst.SUM_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUM_REJ_CAT_ITM_RPT_NAME));
	            break;
	        case ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_NAME));
	            break;
	        case ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);						
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_UNSPSC_CODE_REQ_RPT_NAME));
	            break;
	        case ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL:
	        	rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_NAME));	
	            break;
	        case ReportConst.LST_ITM_CODE_RPT_VAL:
	        	rptGenBean.setItemNameView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LST_ITM_CODE_RPT_NAME));
	            break;	       
	        default:            
	            break;
		}		
	}
	/*
	 *Enable SC module criteria by report name 
	 */
	public void criteriaVariablesByReportBySC(){
		switch (getManagedBean().getReportNameValue()) {
		 	case ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL:
		 		rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SPPPP_PEMATUHAN_OC_RPT_NAME));
	            break; 
	        case ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL:
	        	/*rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);*/
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPPP1_PEMATUHAN_OC_RPT_NAME));
	            break;
	        case ReportConst.PDDW_OC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PDDW_OC_RPT_NAME));
	            break;
	        case ReportConst.AKPP_PEMATUHAN_OC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.AKPP_PEMATUHAN_OC_RPT_NAME));
	            break;
	        case ReportConst.SPPSAP2_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_TEC_RPT_NAME));
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_NAME));
	            break;
	        case ReportConst.AKPT_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.AKPT_TEC_RPT_NAME));	
	            break;
	        case ReportConst.TECSPEC_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TECSPEC_TEC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_NAME));
	            break;
	        case ReportConst.SPPSAP2_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_FEC_RPT_NAME));
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_NAME));	
	            break;
	        case ReportConst.AKPT_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.AKPT_FEC_RPT_NAME));
	            break;
	        case ReportConst.HARGA_TAWARAN_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.HARGA_TAWARAN_FEC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_FINRS_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_FINRS_FEC_RPT_NAME));
	            break;
	        case ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SPPP1_PEMATUHAN_EC_RPT_NAME));
	            break;
	        case ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL:
	        	/*rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);*/
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPP1_PEMATUHAN_EC_RPT_NAME));		
	            break;
	        case ReportConst.PDW_EC_RPT_VAL:
	        	/*rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);*/
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PDW_EC_RPT_NAME));		
	            break;
	        case ReportConst.SPPSAP2_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SPPSAP2_EC_RPT_NAME));
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_NAME));
	            break;
	        case ReportConst.AKPTPH_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.AKPTPH_EC_RPT_NAME));	
	            break;
	        case ReportConst.TECSPEC_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.TECSPEC_EC_RPT_NAME));
	            break;
	        case ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_NAME));
	            break;
	        case ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_NAME));
	            break;
	        case ReportConst.HARGA_TAWARAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.HARGA_TAWARAN_EC_RPT_NAME));
	            break;
	        case ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_NAME));
	            break;
	        case ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_NAME));
	            break;
	        case ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_NAME));
	            break;
	        case ReportConst.OVRL_FINRS_EC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.OVRL_FINRS_EC_RPT_NAME));			
	            break;	        	 
	        case ReportConst.PSDAK_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDAK_TEC_RPT_NAME));
	            break;
	        case ReportConst.PSDABK_TEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDABK_TEC_RPT_NAME));
	            break;
	        case ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_NAME));
	            break;
	        case ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_NAME));
	            break;
	        case ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_VAL:
	        	rptGenBean.setQuotationNoView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_NAME));
	            break;
	        default:            
	            break;
		}		
	}
	/*
	 * Enable FL module criteria by report name
	 */
	public void criteriaVariablesByReportByFL(){
		
	switch (getManagedBean().getReportNameValue()) {
		case ReportConst.CON_REQ_RPT_MIN_VAL:	
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_MIN_NAME));		
			 break;
	 	case ReportConst.CON_ORD_RPT_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);			
			rptGenBean.setPtjView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_RPT_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryOrderNoView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_MIN_NAME));
			 break;
		case ReportConst.LATE_DEL_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setItemTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_REJ_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME));
			 break;
		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setMtoContractView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_MIN_NAME));	
			 break;
		case ReportConst.STP_INSTR_DEL_RPT_MIN_VAL:
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setItemTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME));		
			 break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME));	
			 break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME));	
			 break;
		case ReportConst.PYMT_MATCH_RPT_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setInvoiceNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setStatusView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_MIN_NAME));	
			 break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setDeductionTypeView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_MIN_NAME));
			 break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			rptGenBean.setCrNoView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setApproverNameView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_MIN_NAME));		
			 break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_MIN_NAME));
			break;
		case ReportConst.YEP_RPT_MIN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_MIN_NAME));	
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_MIN_NAME));	
			break;
		case ReportConst.CON_REQ_RPT_CEN_PEN_VAL:	
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setSupplierView(true);		
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_RPT_CEN_PEN_VAL:			
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setStatusView(true);		
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryOrderNoView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.LATE_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setItemTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setMtoContractView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setItemTypeView(true);
			rptGenBean.setContractTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME));	
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setInvoiceNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.DED_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setSstView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setDeductionTypeView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setPkpNoView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setCrNoView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setApproverNameView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.YEP_RPT_CEN_PEN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setPtjView(true);
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:	
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setStatusView(true);			
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_RPT_PO_NAME));	
			break;
		case ReportConst.PUR_ORD_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_RPT_PO_NAME));
			break;
		case ReportConst.EMR_PROCUR_RPT_PO_VAL:
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPoAmountView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.EMR_PROCUR_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setDeliveryOrderNoView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_PO_NAME));
			break;
		case ReportConst.FL_REC_NOTE_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_PO_NAME));
			break;
		case ReportConst.LATE_DEL_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.STP_INSTR_DEL_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_PO_NAME));
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setStatusView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setInvoiceNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_PO_NAME));		
			break;
		case ReportConst.PUR_REQ_CANC_RPT_PO_VAL:
			rptGenBean.setPrNoView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_CANC_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setApproverNameView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_CANC_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME));
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_RPT_PO_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_AP58A_RPT_PO_VAL:
			rptGenBean.setPkpNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AP58A_RPT_PO_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_PO_NAME));	
			break;
		case ReportConst.SUMM_RPT_PAN_ITM_VAL:
			rptGenBean.setItemNameView(true);
			rptGenBean.setItemCodeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUMM_RPT_PAN_ITM_NAME));	
			break;
		default:            
            break;
		}		
	}	
	public void retrieveDates(){
		if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))&&((getManagedBean().getReportNameValue().equalsIgnoreCase(ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL))||(getManagedBean().getReportNameValue().equalsIgnoreCase(ReportConst.SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL))||(getManagedBean().getReportNameValue().equalsIgnoreCase(ReportConst.SM_LST_PRE_EXP_COMP_RPT_VAL)))){
			log.info("inside retrieveDates");
			Date currentDate= new Date();
			this.setToDate(currentDate);
			this.setExpiryToDate(currentDate);
			Date newDate= new Date();
			newDate.setMonth(0);
			newDate.setDate(1);
			this.setFromDate(newDate);
			this.setExpiryFromDate(newDate);
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
		if(this.getState()!=null&&this.getState()!=""){			
			districtReqDTO.setStateId( Long.parseLong(this.getState()));
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
		if(this.getMainCategory()!=null&&this.getMainCategory()!=""){	
			catCodeReqDTO.setCategoryL1Id(Long.parseLong(this.getMainCategory()));
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
		if(this.getSubCategory()!=null&&this.getSubCategory()!=""){	
			catCodeReqDTO.setCategoryL2Id(Long.parseLong(this.getSubCategory()));
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
		this.setEpNumber(null);
		this.setIcNumber(null);
		this.setBusinessRegNo(null);
		this.setMofNumber(null);
		getManagedBean().getRptGenBean().setMofRegView(false);
		getManagedBean().getRptGenBean().setBasicRegView(false);
		if((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL)){			
			if(this.getRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_BASIC)){
				getManagedBean().getRptGenBean().setBasicRegView(true);
				getManagedBean().getRptGenBean().setMofRegView(false);
				this.setMofNumber(null);
				this.setEpNumber(null);				
				this.setIcNumber(null);
				this.setBusinessRegNo(null);
			}else if(this.getRegistrationType().equalsIgnoreCase(ReportConst.RPT_REG_MOF)){
				getManagedBean().getRptGenBean().setMofRegView(true);
				getManagedBean().getRptGenBean().setBasicRegView(false);
				this.setMofNumber(null);
				this.setEpNumber(null);				
				this.setIcNumber(null);
				this.setBusinessRegNo(null);
			}			
		}		
	}
	/*
	 *Generate all modules reports 
	 */
	public void generateReport(){		
		if((getManagedBean().getModuleName()!=null)&&(getManagedBean().getReportNameValue()!=null)){
			if(!(getManagedBean().getReportNameValue().toString().equals(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))&&!(getManagedBean().getReportNameValue().toString().equals(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))&&!(getManagedBean().getReportNameValue().toString().equals(ReportConst.PDW_EC_RPT_VAL))){
				if((((this.getFromDate()==null)&&(this.getToDate()!=null))||((this.getFromDate()!=null)&&(this.getToDate()==null)))||(((this.getApplicationFromDate()==null)&&(this.getApplicationToDate()!=null))||((this.getApplicationFromDate()!=null)&&(this.getApplicationToDate()==null)))||(((this.getReceiveFromDate()==null)&&(this.getReceiveToDate()!=null))||((this.getReceiveFromDate()!=null)&&(this.getReceiveToDate()==null)))||(((this.getReleaseFromDate()==null)&&(this.getReleaseToDate()!=null))||((this.getReleaseFromDate()!=null)&&(this.getReleaseToDate()==null)))||(((this.getPrintedFromDate()==null)&&(this.getPrintedToDate()!=null))||((this.getPrintedFromDate()!=null)&&(this.getPrintedToDate()==null)))||(((this.getExpiryFromDate()==null)&&(this.getExpiryToDate()!=null))||((this.getExpiryFromDate()!=null)&&(this.getExpiryToDate()==null)))){
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.PLZ_SEL_BOTH_DTS_RPT_SEARCH));
				}
			}
			if((this.getFromDate()!=null)&&(this.getToDate()!=null)){
				if(this.getFromDate().after(this.getToDate())){					  
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}				
			}
			if((this.getApplicationFromDate()!=null)&&(this.getApplicationToDate()!=null)){
				if(this.getApplicationFromDate().after(this.getApplicationToDate())){					 
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if((this.getReceiveFromDate()!=null)&&(this.getReceiveToDate()!=null)){
				if(this.getReceiveFromDate().after(this.getReceiveToDate())){					  
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			/*if((this.getReleaseFromDate()!=null)&&(this.getReleaseToDate()!=null)){
				if(this.getReleaseFromDate().after(this.getReleaseToDate())){					
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}*/
			if((this.getPrintedFromDate()!=null)&&(this.getPrintedToDate()!=null)){
				if(this.getPrintedFromDate().after(this.getPrintedToDate())){					
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if((this.getExpiryFromDate()!=null)&&(this.getExpiryToDate()!=null)){
				if(this.getExpiryFromDate().after(this.getExpiryToDate())){					
					throw new EpException(CommonMethod.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}			
			if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){			
				generateSCReport();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT)){
				generateCMReport();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)){
				generateFLReport();
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT)){
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
		//String url="";																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																	
		if(((this.getSiteVisitOfficer()!=null)&&(this.getSiteVisitOfficer()!=ReportConst.EMPTY_VAL))||((this.getCategoryCode()!=null)&&(this.getCategoryCode()!=ReportConst.EMPTY_VAL))||((this.getApplicationType()!=null)&&(this.getApplicationType()!=ReportConst.EMPTY_VAL))||((this.getPaymentType()!=null)&&(this.getPaymentType()!=ReportConst.EMPTY_VAL))||((this.getCancelledBy()!=null)&&(this.getCancelledBy()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getSupportingDocument()!=null)&&(this.getSupportingDocument()!=ReportConst.EMPTY_VAL))||((this.getDocStatus()!=null)&&(this.getDocStatus()!=ReportConst.EMPTY_VAL))||((this.getSuppDocStatus()!=null)&&(this.getSuppDocStatus()!=ReportConst.EMPTY_VAL))||((this.getRegistrationNo()!=null)&&(this.getRegistrationNo()!=ReportConst.EMPTY_VAL))||((this.getePNo()!=null)&&(this.getePNo()!=ReportConst.EMPTY_VAL))||((this.getApplicationNo()!=null)&&(this.getApplicationNo()!=ReportConst.EMPTY_VAL))||
				((this.getZone()!=null)&&(this.getZone()!=ReportConst.EMPTY_VAL))||((this.getSupplierType()!=null)&&(this.getSupplierType()!=ReportConst.EMPTY_VAL))||((this.getMofRegNo()!=null)&&(this.getMofRegNo()!=ReportConst.EMPTY_VAL))||((this.getMofRegNoFrom()!=null)&&(this.getMofRegNoFrom()!=ReportConst.EMPTY_VAL))||((this.getMofRegNoTo()!=null)&&(this.getMofRegNoTo()!=ReportConst.EMPTY_VAL))||((this.getRegistrationStatus()!=null)&&(this.getRegistrationStatus()!=ReportConst.EMPTY_VAL))||((this.getCapitalNaccumFrm()!=null)&&(this.getCapitalNaccumFrm()!=ReportConst.EMPTY_VAL))||((this.getCapitalNaccumTo()!=null)&&(this.getCapitalNaccumTo().toString()!=ReportConst.EMPTY_VAL))||((this.getPaidUpCapitalNaccumShare()!=null)&&(this.getPaidUpCapitalNaccumShare().toString()!=ReportConst.EMPTY_VAL))||((this.getCategory()!=null)&&(this.getCategory()!=ReportConst.EMPTY_VAL))||((this.getNoOfCategory()!=null)&&(this.getNoOfCategory()!=ReportConst.EMPTY_VAL))||((this.getMainCategory()!=null)&&(this.getMainCategory()!=ReportConst.EMPTY_VAL))||((this.getSubCategory()!=null)&&(this.getSubCategory()!=ReportConst.EMPTY_VAL))||
				((this.getSubCategoryDivision()!=null)&&(this.getSubCategoryDivision()!=ReportConst.EMPTY_VAL))||((this.getRole()!=null)&&(this.getRole()!=ReportConst.EMPTY_VAL))||((this.getApplicationStatus()!=null)&&(this.getApplicationStatus()!=ReportConst.EMPTY_VAL))||((this.getState()!=null)&&(this.getState()!=ReportConst.EMPTY_VAL))||((this.getDistrict()!=null)&&(this.getDistrict()!=ReportConst.EMPTY_VAL))||((this.getSupplierName()!=null)&&(this.getSupplierName()!=ReportConst.EMPTY_VAL))||((this.getReportType()!=null)&&(this.getReportType()!=ReportConst.EMPTY_VAL))||((this.getOrganizationType()!=null)&&(this.getOrganizationType()!=ReportConst.EMPTY_VAL))||((this.getKementerian()!=null)&&(this.getKementerian()!=ReportConst.EMPTY_VAL))||((this.getNamAgensi()!=null)&&(this.getNamAgensi()!=ReportConst.EMPTY_VAL))||((this.getComplaintNo()!=null)&&(this.getComplaintNo()!=ReportConst.EMPTY_VAL))||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getCompanyStatus()!=null)&&(this.getCompanyStatus()!=ReportConst.EMPTY_VAL))||((this.getCaseNo()!=null)&&(this.getCaseNo()!=ReportConst.EMPTY_VAL))||
				((this.getCompApplStatus()!=null)&&(this.getCompApplStatus()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getApplicationFromDate()!=null)&&(this.getApplicationFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getApplicationToDate()!=null)&&(this.getApplicationToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getReceiveFromDate()!=null)&&(this.getReceiveFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getReceiveToDate()!=null)&&(this.getReceiveToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getReleaseFromDate()!=null)&&(this.getReleaseFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getExpiryFromDate()!=null)&&(this.getExpiryFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getExpiryToDate()!=null)&&(this.getExpiryToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getPrintedFromDate()!=null)&&(this.getPrintedFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getPrintedToDate()!=null)&&(this.getPrintedToDate().toString()!=ReportConst.EMPTY_VAL))){
			
			if((this.getApplicationType()!=null)&&(this.getApplicationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationTypeTotVar(""+this.getApplicationType());
			}
			if((this.getPaymentType()!=null)&&(this.getPaymentType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPaymentTypeTotVar(""+this.getPaymentType());
			}
			if((this.getCancelledBy()!=null)&&(this.getCancelledBy()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCancelledByTotVar(""+this.getCancelledBy());
			}
			if((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getCompanyName());
			}
			if((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getRegistrationType());
			}
			if((this.getSupportingDocument()!=null)&&(this.getSupportingDocument()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupportingDocumentTotVar(ReportConst.AMP_SUPP_DOC_EQ+this.getSupportingDocument());
			}
			if((this.getDocStatus()!=null)&&(this.getDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDocStatusTotVar(ReportConst.AMP_DOC_STS_NAME_EQ+this.getDocStatus());
			}
			if((this.getSuppDocStatus()!=null)&&(this.getSuppDocStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSuppDocStatusTotVar(ReportConst.AMP_SUPP_DOC_STATUS_MODE_EQ+this.getSuppDocStatus());
			}
			if((this.getRegistrationNo()!=null)&&(this.getRegistrationNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationNoTotVar(ReportConst.AMP_REG_NO_EQ+this.getRegistrationNo());
			}
			if((this.getePNo()!=null)&&(this.getePNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setePNoTotVar(ReportConst.AMP_EP_NO_EQ+this.getePNo());
			}			
			if((this.getApplicationNo()!=null)&&(this.getApplicationNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationNoTotVar(ReportConst.AMP_APPLICATION_NO_EQ+this.getApplicationNo());
			}
			if((this.getZone()!=null)&&(this.getZone()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setZoneTotVar(ReportConst.AMP_ZONE_NAME_EQ+this.getZone());
			}
			if((this.getSupplierType()!=null)&&(this.getSupplierType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierTypeTotVar(ReportConst.AMP_SUPPLIER_TYPE_EQ+this.getSupplierType());
			}
			if((this.getMofRegNo()!=null)&&(this.getMofRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoTotVar(""+this.getMofRegNo());
			}				
			if((this.getMofRegNoFrom()!=null)&&(this.getMofRegNoFrom()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoFromTotVar(ReportConst.AMP_REG_MOF_FROM_EQ+this.getMofRegNoFrom());
			}
			if((this.getMofRegNoTo()!=null)&&(this.getMofRegNoTo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofRegNoToTotVar(ReportConst.AMP_REG_MOF_TO_EQ+this.getMofRegNoTo());
			}			
			if((this.getRegistrationStatus()!=null)&&(this.getRegistrationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationStatusTotVar(ReportConst.AMP_REG_STATUS_EQ+this.getRegistrationStatus());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getReleaseFromDate()!=null)&&(this.getReleaseFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_RELEASE_DATE_EQ+outputDateFormat.format(this.getReleaseFromDate()));
			}
			if((this.getExpiryFromDate()!=null)&&(this.getExpiryFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setExpiryFromDateTotVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getExpiryFromDate()));
			}
			if((this.getExpiryToDate()!=null)&&(this.getExpiryToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setExpiryToDateTotVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getExpiryToDate()));
			}
			if((this.getCapitalNaccumFrm()!=null)&&(this.getCapitalNaccumFrm()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCapitalNaccumFrmTotVar(ReportConst.AMP_FRM_PUC_EQ+this.getCapitalNaccumFrm());
			}
			if((this.getCapitalNaccumTo()!=null)&&(this.getCapitalNaccumTo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCapitalNaccumToTotVar(ReportConst.AMP_TO_PUC_EQ+this.getCapitalNaccumTo());
			}
			if((this.getPaidUpCapitalNaccumShare()!=null)&&(this.getPaidUpCapitalNaccumShare()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPaidUpCapitalNaccumShareTotVar(ReportConst.AMP_PUC_N_AS_EQ+this.getPaidUpCapitalNaccumShare());
			}
			
			if((this.getNoOfCategory()!=null)&&(this.getNoOfCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setNoOfCategoryTotVar(""+this.getNoOfCategory());
			}
			if((this.getMainCategory()!=null)&&(this.getMainCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMainCategoryTotVar(ReportConst.AMP_MAIN_CATEGORY_EQ+this.getMainCategory());
			}
			if((this.getSubCategory()!=null)&&(this.getSubCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSubCategoryTotVar(ReportConst.AMP_SUB_CATEGORY_EQ+this.getSubCategory());
			}
			if((this.getSubCategoryDivision()!=null)&&(this.getSubCategoryDivision()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSubCategoryDivisionTotVar(ReportConst.AMP_SUB_DIV_CATEGORY_EQ+this.getSubCategoryDivision());
			}
			if((this.getRole()!=null)&&(this.getRole()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRoleTotVar(ReportConst.AMP_ROLE_EQ+this.getRole());
			}
			if((this.getApplicationStatus()!=null)&&(this.getApplicationStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApplicationStatusTotVar(""+this.getApplicationStatus());
			}
			if((this.getState()!=null)&&(this.getState()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofNumberTotVar(""+this.getState());
			}
			if((this.getDistrict()!=null)&&(this.getDistrict()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setStateTotVar(""+this.getDistrict());
			}
			if((this.getSupplierName()!=null)&&(this.getSupplierName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierNameTotVar(""+this.getSupplierName());
			}
			if((this.getReportType()!=null)&&(this.getReportType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setReportTypeTotVar(""+this.getReportType());
			}
			if((this.getOrganizationType()!=null)&&(this.getOrganizationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setOrganizationTypeTotVar(""+this.getOrganizationType());
			}
			if((this.getKementerian()!=null)&&(this.getKementerian()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setKementerianTotVar(""+this.getKementerian());
			}
			if((this.getNamAgensi()!=null)&&(this.getNamAgensi()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setNamAgensiTotVar(""+this.getNamAgensi());
			}			
			if((this.getComplaintNo()!=null)&&(this.getComplaintNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setComplaintNoTotVar(""+this.getComplaintNo());
			}
			if((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_IC_NO_EQ+this.getProofIdentity());
			}
			if((this.getCompanyStatus()!=null)&&(this.getCompanyStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyStatusTotVar(""+this.getCompanyStatus());
			}
			if((this.getCaseNo()!=null)&&(this.getCaseNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCaseNoTotVar(""+this.getCaseNo());
			}
			if((this.getCompApplStatus()!=null)&&(this.getCompApplStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompApplStatusTotVar(""+this.getCompApplStatus());
			}
			if((this.getCategoryCode()!=null)&&(this.getCategoryCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCategoryCodeTotVar(""+this.getCategoryCode());
			}
			if((this.getSiteVisitOfficer()!=null)&&(this.getSiteVisitOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSiteVisitOfficerTotVar(""+this.getSiteVisitOfficer());
			}
			if((this.getMofNumber()!=null)&&(this.getMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getMofNumber());
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
			rptGenBean.setUrlReportJSPName(urlCMReportNameJsps.get(getManagedBean().getReportNameValue().trim()));			
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+ReportConst.EXC_NEXT_URL;
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+rptGenBean.getSupportingDocumentTotVar()+rptGenBean.getDocStatusTotVar()+rptGenBean.getSuppDocStatusTotVar()+rptGenBean.getRegistrationNoTotVar()+rptGenBean.getePNoTotVar()+rptGenBean.getApplicationNoTotVar()+rptGenBean.getZoneTotVar()+rptGenBean.getSupplierTypeTotVar()+rptGenBean.getMofRegNoTotVar()+rptGenBean.getMofRegNoFromTotVar()+rptGenBean.getMofRegNoToTotVar()+rptGenBean.getRegistrationStatusTotVar()+rptGenBean.getCapitalNaccumFrmTotVar()+rptGenBean.getCapitalNaccumToTotVar()+	rptGenBean.getPaidUpCapitalNaccumShareTotVar()+rptGenBean.getCategoryTotVar()+rptGenBean.getNoOfCategoryTotVar()+rptGenBean.getMainCategoryTotVar()+rptGenBean.getSubCategoryTotVar()+rptGenBean.getSubCategoryDivisionTotVar()+rptGenBean.getRoleTotVar()+rptGenBean.getApplicationStatusTotVar()+rptGenBean.getCancelledByTotVar()+rptGenBean.getPaymentTypeTotVar()+rptGenBean.getApplicationTypeTotVar()+rptGenBean.getStateTotVar()+rptGenBean.getDistrictTotVar()+rptGenBean.getSupplierNameTotVar()+rptGenBean.getReportTypeTotVar()+rptGenBean.getOrganizationTypeTotVar()+rptGenBean.getKementerianTotVar()+rptGenBean.getNamAgensiTotVar()+rptGenBean.getComplaintNoTotVar()+	
						rptGenBean.getCompanyStatusTotVar()+rptGenBean.getCaseNoTotVar()+rptGenBean.getCompApplStatusTotVar()+rptGenBean.getCategoryCodeTotVar()+rptGenBean.getSiteVisitOfficerTotVar()+rptGenBean.getReleaseFromDateTotVar()+rptGenBean.getReleaseToDateTotVar()+rptGenBean.getApplicationFromDateTotVar()+rptGenBean.getApplicationToDateTotVar()+rptGenBean.getReceiveFromDateTotVar()+rptGenBean.getReceiveToDateTotVar()+rptGenBean.getExpiryFromDateTotVar()+rptGenBean.getExpiryToDateTotVar()+rptGenBean.getPrintedFromDateTotVar()+rptGenBean.getPrintedToDateTotVar());
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
		if((getManagedBean().getReportNameValue()!=null)&&(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))||(getManagedBean().getReportNameValue()!=null)&&(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))||(getManagedBean().getReportNameValue()!=null)&&(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.PDW_EC_RPT_VAL))){
			if(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL)){
				rptGenBean.setUrlReportJSPName(ReportConst.SC_KPPP1_PEMATUHAN_OC_RPT_JSP);	
			}else if(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL)){
				rptGenBean.setUrlReportJSPName(ReportConst.SC_KPP1_PEMATUHAN_EC_RPT_JSP);	
			}else if(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.PDW_EC_RPT_VAL)){
				rptGenBean.setUrlReportJSPName(ReportConst.SC_PDW_EC_RPT_JSP);	
			}			
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//String url =ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+ReportConst.EXC_NEXT_URL;				
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName());
			}else{				
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		}else if(((this.getQuotationNo()!=null)&&(this.getQuotationNo()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))){	
			if((this.getQuotationNo()!=null)&&(this.getQuotationNo()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setQuotationIdTotVar(ReportConst.AMP_QUOTE_ID_EQ+this.getQuotationId());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			Map<String, String> urlSCReportJsps = new HashMap<String, String>();
			urlSCReportJsps.put(ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL, ReportConst.SC_SPPPP_PEMATUHAN_OC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL, ReportConst.SC_KPPP1_PEMATUHAN_OC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PDDW_OC_RPT_VAL, ReportConst.SC_PDDW_OC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.AKPP_PEMATUHAN_OC_RPT_VAL, ReportConst.AKPP_PEMATUHAN_OC_RPT_JSP);			
			urlSCReportJsps.put(ReportConst.SPPSAP2_TEC_RPT_VAL, ReportConst.SC_SPPSAP2_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL, ReportConst.SC_KPSAP2_PEMARKAHAN_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.AKPT_TEC_RPT_VAL, ReportConst.SC_AKPT_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.TECSPEC_TEC_RPT_VAL, ReportConst.SC_TECSPEC_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_VAL, ReportConst.SC_OVRL_TECRES_WITH_OUT_WEI_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.OVRL_TECRES_WITH_WEI_TEC_RPT_VAL, ReportConst.SC_OVRL_TECRES_WITH_WEI_TEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.SPPSAP2_FEC_RPT_VAL, ReportConst.SC_SPPSAP2_FEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL, ReportConst.SC_KPSAP2_PEMARKAHAN_FEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.AKPT_FEC_RPT_VAL, ReportConst.SC_AKPT_FEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.HARGA_TAWARAN_FEC_RPT_VAL, ReportConst.SC_HARGA_TAWARAN_FEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.OVRL_FINRS_FEC_RPT_VAL, ReportConst.SC_OVRL_FINRS_FEC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL, ReportConst.SC_SPPP1_PEMATUHAN_EC_RPT_JSP);
			//urlSCReportJsps.put(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL, ReportConst.SC_KPP1_PEMATUHAN_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.SPPSAP2_EC_RPT_VAL, ReportConst.SC_SPPSAP2_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL, ReportConst.SC_KPSAP2_PEMARKAHAN_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.AKPTPH_EC_RPT_VAL, ReportConst.SC_AKPTPH_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.TECSPEC_EC_RPT_VAL, ReportConst.SC_TECSPEC_EC_RPT_JSP);			
			urlSCReportJsps.put(ReportConst.PSDAK_BILANGAN_KONTRAK_EC_RPT_VAL, ReportConst.SC_PSDAK_BILANGAN_KONTRAK_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PSDABK_BILANGAN_KONTRAK_EC_RPT_VAL, ReportConst.SC_PSDABK_BILANGAN_KONTRAK_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.HARGA_TAWARAN_EC_RPT_VAL, ReportConst.SC_HARGA_TAWARAN_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KEMUDAHAN_KEWANGAN_EC_RPT_VAL, ReportConst.SC_KEMUDAHAN_KEWANGAN_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_VAL, ReportConst.SC_COMP_EXP_GOVT_AGEN_CONTR_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_VAL, ReportConst.SC_COMP_EXP_NON_GOVT_AGEN_CONTR_EC_RPT_JSP);			
			urlSCReportJsps.put(ReportConst.OVRL_TECRS_WITH_WEI_EC_RPT_VAL, ReportConst.SC_OVRL_TECRS_WITH_WEI_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.OVRL_TECRS_WITH_OUT_WEI_EC_RPT_VAL, ReportConst.SC_OVRL_TECRS_WITH_OUT_WEI_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.OVRL_FINRS_EC_RPT_VAL, ReportConst.SC_OVRL_FINRS_EC_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PSDAK_TEC_RPT_VAL, ReportConst.SC_COMP_EXP_GOVT_AGEN_NO_OF_CONTR_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PSDABK_TEC_RPT_VAL, ReportConst.SC_COMP_EXP_NON_GOVT_AGEN_NO_OF_CONTR_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PSDAK_JUMLAH_KONTRAK_FEC_RPT_VAL, ReportConst.SC_COMP_EXP_GOVT_AGEN_CONTR_RPT_JSP);
			urlSCReportJsps.put(ReportConst.PSDABK_JUMLAH_KONTRAK_FEC_RPT_VAL, ReportConst.SC_COMP_EXP_NON_GOVT_AGEN_CONTR_RPT_JSP);
			urlSCReportJsps.put(ReportConst.KEDUDUKAN_KEWANGAN_FEC_RPT_VAL, ReportConst.SC_FINANCE_POSITION_RPT_JSP);			
			rptGenBean.setUrlReportJSPName(urlSCReportJsps.get(getManagedBean().getReportNameValue().trim()));						
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//String url =ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getQuotationIdTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+ReportConst.EXC_NEXT_URL;
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getQuotationIdTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar());				
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
		//String url="";
		if(((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL))||((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL))||((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL))||((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getExtentionType()!=null)&&(this.getExtentionType()!=ReportConst.EMPTY_VAL))||((this.getExtentionBrand()!=null)&&(this.getExtentionBrand()!=ReportConst.EMPTY_VAL))||((this.getExtentionMeasurment()!=null)&&(this.getExtentionMeasurment()!=ReportConst.EMPTY_VAL))||((this.getExtentionColour()!=null)&&(this.getExtentionColour()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL))||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))||((this.getEpNumber()!=null)&&(this.getEpNumber()!=ReportConst.EMPTY_VAL))||((this.getBusinessRegNo()!=null)&&(this.getBusinessRegNo()!=ReportConst.EMPTY_VAL))||((this.getIcNumber()!=null)&&(this.getIcNumber()!=ReportConst.EMPTY_VAL))||((this.getMofNumber()!=null)&&(this.getMofNumber()!=ReportConst.EMPTY_VAL))){			
			if((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getItemName());
			}
			if((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemTypeTotVar(ReportConst.AMP_ITM_TYPE_EQ+this.getItemType());
			}
			if((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemStatusTotVar(ReportConst.AMP_ITM_STATUS_EQ+this.getItemStatus());
			}
			if((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscCodeTotVar(ReportConst.AMP_UNSPSC_CODE_EQ+this.getUnspscCode());
			}
			if((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscTitleTotVar(ReportConst.AMP_UNSPSC_TIT_EQ+this.getUnspscTitle());
			}			
			if((this.getExtentionType()!=null)&&(this.getExtentionType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionTypeTotVar(ReportConst.AMP_EXT_TYPE_EQ+this.getExtentionType());
			}
			if((this.getExtentionBrand()!=null)&&(this.getExtentionBrand()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionBrandTotVar(ReportConst.AMP_EXT_BRAND_EQ+this.getExtentionBrand());
			}
			if((this.getExtentionMeasurment()!=null)&&(this.getExtentionMeasurment()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionMeasurmentTotVar(ReportConst.AMP_EXT_MEASURMENT_EQ+this.getExtentionMeasurment());
			}
			if((this.getExtentionColour()!=null)&&(this.getExtentionColour()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionColourTotVar(ReportConst.AMP_EXT_COLOUR_EQ+this.getExtentionColour());
			}				
			if((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getCompanyName());
			}
			if((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getRegistrationType());
			}
			if((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ+this.getItemCategory());
			}
			if((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ+this.getProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}
			if((this.getEpNumber()!=null)&&(this.getEpNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setEpNumberTotVar(ReportConst.AMP_EP_NO_EQ+this.getEpNumber());
			}
			if((this.getBusinessRegNo()!=null)&&(this.getBusinessRegNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setBusinessRegNoTotVar(ReportConst.AMP_BUSI_REG_NO_EQ+this.getBusinessRegNo());
			}
			if((this.getIcNumber()!=null)&&(this.getIcNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setIcNumberTotVar(ReportConst.AMP_IC_NO_EQ+this.getIcNumber());
			}
			if((this.getMofNumber()!=null)&&(this.getMofNumber()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMofNumberTotVar(ReportConst.AMP_MOF_NO_EQ+this.getMofNumber());
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
			rptGenBean.setUrlReportJSPName(urlCMReportNameJsps.get(getManagedBean().getReportNameValue().trim()));
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+ReportConst.EXC_NEXT_URL;				
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionTypeTotVar()+rptGenBean.getExtentionBrandTotVar()+rptGenBean.getExtentionMeasurmentTotVar()+rptGenBean.getExtentionColourTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getEpNumberTotVar()+rptGenBean.getBusinessRegNoTotVar()+rptGenBean.getIcNumberTotVar()+rptGenBean.getMofNumberTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar());
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
		
		if(((this.getPtj()!=null)&&(this.getPtj()!=ReportConst.EMPTY_VAL))||((this.getContractNo()!=null)&&(this.getContractNo()!=ReportConst.EMPTY_VAL))||((this.getSst()!=null)&&(this.getSst()!=ReportConst.EMPTY_VAL))||((this.getJabatan()!=null)&&(this.getJabatan()!=ReportConst.EMPTY_VAL))||((this.getMinistry()!=null)&&(this.getMinistry()!=ReportConst.EMPTY_VAL))||((this.getSupplier()!=null)&&(this.getSupplier()!=ReportConst.EMPTY_VAL))||((this.getStatus()!=null)&&(this.getStatus()!=ReportConst.EMPTY_VAL))||((this.getDuration()!=null)&&(this.getDuration()!=ReportConst.EMPTY_VAL))||((this.getRecievingOfficer()!=null)&&(this.getRecievingOfficer()!=ReportConst.EMPTY_VAL))||((this.getDeliveryAddress()!=null)&&(this.getDeliveryAddress()!=ReportConst.EMPTY_VAL))
			||((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL))||((this.getContractType()!=null)&&(this.getContractType()!=ReportConst.EMPTY_VAL))||((this.getRequisitioner()!=null)&&(this.getRequisitioner()!=ReportConst.EMPTY_VAL))||((this.getMtoContract()!=null)&&(this.getMtoContract()!=ReportConst.EMPTY_VAL))||((this.getInvoiceNo()!=null)&&(this.getInvoiceNo()!=ReportConst.EMPTY_VAL))||((this.getDeductionType()!=null)&&(this.getDeductionType()!=ReportConst.EMPTY_VAL))||((this.getCrNo()!=null)&&(this.getCrNo()!=ReportConst.EMPTY_VAL))||((this.getApproverName()!=null)&&(this.getApproverName()!=ReportConst.EMPTY_VAL))||((this.getDocumentType()!=null)&&(this.getDocumentType()!=ReportConst.EMPTY_VAL))||((this.getCriteria()!=null)&&(this.getCriteria()!=ReportConst.EMPTY_VAL))||((this.getScoring()!=null)&&(this.getScoring()!=ReportConst.EMPTY_VAL))||((this.getPkpNo()!=null)&&(this.getPkpNo()!=ReportConst.EMPTY_VAL))||((this.getDeliveryOrderNo()!=null)&&(this.getDeliveryOrderNo()!=ReportConst.EMPTY_VAL))||((this.getItemCode()!=null)&&(this.getItemCode()!=ReportConst.EMPTY_VAL))||((this.getPoAmount()!=null)&&(this.getPoAmount()!=ReportConst.EMPTY_VAL))||((this.getPrNo()!=null)&&(this.getPrNo()!=ReportConst.EMPTY_VAL))||((this.getPmo()!=null)&&(this.getPmo()!=ReportConst.EMPTY_VAL))
			||((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL))||((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL))||((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL))||((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getExtentionCode()!=null)&&(this.getExtentionCode()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL))||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))){		
			if((this.getPtj()!=null)&&(this.getPtj()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPtjTotVar(ReportConst.AMP_PTJ_EQ+this.getPtj());
			}
			if((this.getContractNo()!=null)&&(this.getContractNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setContractNoTotVar(ReportConst.AMP_CONTR_NO_EQ+this.getContractNo());
			}
			if((this.getSst()!=null)&&(this.getSst()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSstTotVar(ReportConst.AMP_SST_EQ+this.getSst());
			}
			if((this.getJabatan()!=null)&&(this.getJabatan()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setJabatanTotVar(ReportConst.AMP_JABATAN_EQ+this.getJabatan());
			}
			if((this.getMinistry()!=null)&&(this.getMinistry()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMinistryTotVar(ReportConst.AMP_MINISTRY_EQ+this.getMinistry());
			}
			if((this.getSupplier()!=null)&&(this.getSupplier()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setSupplierTotVar(ReportConst.AMP_SUPPLIER_EQ+this.getSupplier());
			}
			if((this.getStatus()!=null)&&(this.getStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setStatusTotVar(ReportConst.AMP_STATUS_EQ+this.getStatus());
			}
			if((this.getDuration()!=null)&&(this.getDuration()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDurationTotVar(ReportConst.AMP_DURATION_EQ+this.getDuration());
			}
			if((this.getRecievingOfficer()!=null)&&(this.getRecievingOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRecievingOfficerTotVar(ReportConst.AMP_REC_OFFCR_EQ+this.getRecievingOfficer());
			}
			if((this.getDeliveryAddress()!=null)&&(this.getDeliveryAddress()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeliveryAddressTotVar(ReportConst.AMP_DEL_ADDR_EQ+this.getDeliveryAddress());
			}
			if((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setFlRecNoteNoTotVar(ReportConst.AMP_FL_REC_NOTE_NO_EQ+this.getFlRecNoteNo());
			}
			if((this.getContractType()!=null)&&(this.getContractType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setContractTypeTotVar(ReportConst.AMP_CONTR_TYPE_EQ+this.getContractType());
			}
			if((this.getRequisitioner()!=null)&&(this.getRequisitioner()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRequisitionerTotVar(ReportConst.AMP_REQSNR_EQ+this.getRequisitioner());
			}
			if((this.getMtoContract()!=null)&&(this.getMtoContract()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setMtoContractTotVar(ReportConst.AMP_MTO_CONTR_EQ+this.getMtoContract());
			}
			if((this.getInvoiceNo()!=null)&&(this.getInvoiceNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setInvoiceNoTotVar(ReportConst.AMP_INVOICE_NO_EQ+this.getInvoiceNo());
			}
			if((this.getDeductionType()!=null)&&(this.getDeductionType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeductionTypeTotVar(ReportConst.AMP_DEDUCT_TYPE_EQ+this.getDeductionType());
			}
			if((this.getCrNo()!=null)&&(this.getCrNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCrNoTotVar(ReportConst.AMP_CR_NO_EQ+this.getCrNo());
			}
			if((this.getApproverName()!=null)&&(this.getApproverName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setApproverNameTotVar(ReportConst.AMP_APPROVER_NAME_EQ+this.getApproverName());
			}
			if((this.getDocumentType()!=null)&&(this.getDocumentType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDocumentTypeTotVar(ReportConst.AMP_DOC_TYPE_EQ+this.getDocumentType());
			}
			if((this.getCriteria()!=null)&&(this.getCriteria()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCriteriaTotVar(ReportConst.AMP_CRITERIA_EQ+this.getCriteria());
			}
			if((this.getScoring()!=null)&&(this.getScoring()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setScoringTotVar(ReportConst.AMP_SCORING_EQ+this.getScoring());
			}
			if((this.getPkpNo()!=null)&&(this.getPkpNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPkpNoTotVar(ReportConst.AMP_PKP_NO_EQ+this.getPkpNo());
			}
			if((this.getDeliveryOrderNo()!=null)&&(this.getDeliveryOrderNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setDeliveryOrderNoTotVar(ReportConst.AMP_DEL_ORDER_NO_EQ+this.getDeliveryOrderNo());
			}
			if((this.getItemCode()!=null)&&(this.getItemCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemCodeTotVar(ReportConst.AMP_ITM_CODE_EQ+this.getItemCode());
			}
			if((this.getPoAmount()!=null)&&(this.getPoAmount()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPoAmountTotVar(ReportConst.AMP_PO_AMT_EQ+this.getPoAmount());
			}
			if((this.getPrNo()!=null)&&(this.getPrNo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPrNoTotVar(ReportConst.AMP_PR_NO_EQ+this.getPrNo());
			}
			if((this.getPmo()!=null)&&(this.getPmo()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setPmoTotVar(ReportConst.AMP_PMO_EQ+this.getPmo());
			}
			//-----			
			if((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getItemName());
			}
			if((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemTypeTotVar(ReportConst.AMP_ITM_TYPE_EQ+this.getItemType());
			}
			if((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemStatusTotVar(ReportConst.AMP_ITM_STATUS_EQ+this.getItemStatus());
			}
			if((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscCodeTotVar(ReportConst.AMP_UNSPSC_CODE_EQ+this.getUnspscCode());
			}
			if((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setUnspscTitleTotVar(ReportConst.AMP_UNSPSC_TIT_EQ+this.getUnspscTitle());
			}
			if((this.getExtentionCode()!=null)&&(this.getExtentionCode()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setExtentionCodeTotVar(ReportConst.AMP_EXT_CODE_EQ+this.getExtentionCode());
			}
			if((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_COMP_NAME_EQ+this.getCompanyName());
			}
			if((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setRegistrationTypeTotVar(ReportConst.AMP_REG_TYPE_EQ+this.getRegistrationType());
			}
			if((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ+this.getItemCategory());
			}
			if((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL)){
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ+this.getProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
			}			
			//--
			Map<String, String> urlFLReportNameJsps = new HashMap<String, String>();
			urlFLReportNameJsps.put(ReportConst.CON_REQ_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_ORD_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.LATE_DEL_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_REJ_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SHP_INSTR_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.STP_INSTR_DEL_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PYMT_MATCH_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DED_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_REQ_CANC_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_ORD_CANC_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_CANC_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_CANC_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SUP_PERF_RPT_MIN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_REQ_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_ORD_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DED_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_CANC_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SUP_PERF_RPT_CEN_PEN_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PUR_REQ_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PUR_ORD_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.EMR_PROCUR_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PUR_ORD_REJ_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.LATE_DEL_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_REJ_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.STP_INSTR_DEL_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_SUPL_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_RPT_VIEW_PTJ_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PYMT_MATCH_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PUR_REQ_CANC_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.PUR_ORD_CANC_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.DEL_ORD_CANC_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.INV_CANC_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.YEP_AP58A_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SUP_PERF_RPT_PO_VAL, ReportConst.EMPTY_VAL);
			urlFLReportNameJsps.put(ReportConst.SUMM_RPT_PAN_ITM_VAL, ReportConst.EMPTY_VAL);			
			rptGenBean.setUrlReportJSPName(urlFLReportNameJsps.get(getManagedBean().getReportNameValue().trim()));			
			if((rptGenBean.getUrlReportJSPName()!=null)&&(rptGenBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//String url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionCodeTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getProofIdentityTotVar()+rptGenBean.getPtjTotVar()+rptGenBean.getContractNoTotVar()+rptGenBean.getSstTotVar()+rptGenBean.getJabatanTotVar()+rptGenBean.getMinistryTotVar()+rptGenBean.getSupplierTotVar()+rptGenBean.getStatusTotVar()+rptGenBean.getDurationTotVar()+rptGenBean.getRecievingOfficerTotVar()+rptGenBean.getDeliveryAddressTotVar()+rptGenBean.getFlRecNoteNoTotVar()+rptGenBean.getContractTypeTotVar()+rptGenBean.getRequisitionerTotVar()+rptGenBean.getMtoContractTotVar()+rptGenBean.getInvoiceNoTotVar()+rptGenBean.getDeductionTypeTotVar()+rptGenBean.getCrNoTotVar()+rptGenBean.getApproverNameTotVar()+rptGenBean.getDocumentTypeTotVar()+rptGenBean.getCriteriaTotVar()+rptGenBean.getScoringTotVar()+rptGenBean.getPkpNoTotVar()+rptGenBean.getDeliveryOrderNoTotVar()+rptGenBean.getItemCodeTotVar()+rptGenBean.getPoAmountTotVar()+rptGenBean.getPrNoTotVar()+rptGenBean.getPmoTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar()+ReportConst.EXC_NEXT_URL;				
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionCodeTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getProofIdentityTotVar()+rptGenBean.getPtjTotVar()+rptGenBean.getContractNoTotVar()+rptGenBean.getSstTotVar()+rptGenBean.getJabatanTotVar()+rptGenBean.getMinistryTotVar()+rptGenBean.getSupplierTotVar()+rptGenBean.getStatusTotVar()+rptGenBean.getDurationTotVar()+rptGenBean.getRecievingOfficerTotVar()+rptGenBean.getDeliveryAddressTotVar()+rptGenBean.getFlRecNoteNoTotVar()+rptGenBean.getContractTypeTotVar()+rptGenBean.getRequisitionerTotVar()+rptGenBean.getMtoContractTotVar()+rptGenBean.getInvoiceNoTotVar()+rptGenBean.getDeductionTypeTotVar()+rptGenBean.getCrNoTotVar()+rptGenBean.getApproverNameTotVar()+rptGenBean.getDocumentTypeTotVar()+rptGenBean.getCriteriaTotVar()+rptGenBean.getScoringTotVar()+rptGenBean.getPkpNoTotVar()+rptGenBean.getDeliveryOrderNoTotVar()+rptGenBean.getItemCodeTotVar()+rptGenBean.getPoAmountTotVar()+rptGenBean.getPrNoTotVar()+rptGenBean.getPmoTotVar()+rptGenBean.getFromDateVar()+rptGenBean.getToDateVar());				
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
	public void checkVariableValueExist(){		
		getManagedBean().setSelfView(true);
		getManagedBean().setNotSelfView(false);
		if((getManagedBean().getReportNameValue()!=null)){
			if((getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))||(getManagedBean().getReportNameValue().trim().equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))){
				getManagedBean().setSelfView(false);
				getManagedBean().setNotSelfView(true);
			}else{
				if(((this.getQuotationNo()!=null)&&(this.getQuotationNo()!=ReportConst.EMPTY_VAL))||((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL))||((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL))||((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL))||((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getExtentionType()!=null)&&(this.getExtentionType()!=ReportConst.EMPTY_VAL))||((this.getExtentionBrand()!=null)&&(this.getExtentionBrand()!=ReportConst.EMPTY_VAL))||((this.getExtentionMeasurment()!=null)&&(this.getExtentionMeasurment()!=ReportConst.EMPTY_VAL))||((this.getExtentionColour()!=null)&&(this.getExtentionColour()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL))
						||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getEpNumber()!=null)&&(this.getEpNumber()!=ReportConst.EMPTY_VAL))||((this.getBusinessRegNo()!=null)&&(this.getBusinessRegNo()!=ReportConst.EMPTY_VAL))||((this.getIcNumber()!=null)&&(this.getIcNumber()!=ReportConst.EMPTY_VAL))||((this.getMofNumber()!=null)&&(this.getMofNumber()!=ReportConst.EMPTY_VAL))||((this.getPtj()!=null)&&(this.getPtj()!=ReportConst.EMPTY_VAL))||((this.getContractNo()!=null)&&(this.getContractNo()!=ReportConst.EMPTY_VAL))||((this.getSst()!=null)&&(this.getSst()!=ReportConst.EMPTY_VAL))||((this.getJabatan()!=null)&&(this.getJabatan()!=ReportConst.EMPTY_VAL))||((this.getMinistry()!=null)&&(this.getMinistry()!=ReportConst.EMPTY_VAL))||((this.getSupplier()!=null)&&(this.getSupplier()!=ReportConst.EMPTY_VAL))||((this.getStatus()!=null)&&(this.getStatus()!=ReportConst.EMPTY_VAL))||((this.getDuration()!=null)&&(this.getDuration()!=ReportConst.EMPTY_VAL))||((this.getRecievingOfficer()!=null)&&(this.getRecievingOfficer()!=ReportConst.EMPTY_VAL))
						||((this.getDeliveryAddress()!=null)&&(this.getDeliveryAddress()!=ReportConst.EMPTY_VAL))||((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL))||((this.getContractType()!=null)&&(this.getContractType()!=ReportConst.EMPTY_VAL))||((this.getRequisitioner()!=null)&&(this.getRequisitioner()!=ReportConst.EMPTY_VAL))||((this.getMtoContract()!=null)&&(this.getMtoContract()!=ReportConst.EMPTY_VAL))||((this.getInvoiceNo()!=null)&&(this.getInvoiceNo()!=ReportConst.EMPTY_VAL))||((this.getDeductionType()!=null)&&(this.getDeductionType()!=ReportConst.EMPTY_VAL))||((this.getCrNo()!=null)&&(this.getCrNo()!=ReportConst.EMPTY_VAL))||((this.getApproverName()!=null)&&(this.getApproverName()!=ReportConst.EMPTY_VAL))||((this.getDocumentType()!=null)&&(this.getDocumentType()!=ReportConst.EMPTY_VAL))||((this.getCriteria()!=null)&&(this.getCriteria()!=ReportConst.EMPTY_VAL))||((this.getScoring()!=null)&&(this.getScoring()!=ReportConst.EMPTY_VAL))||((this.getPkpNo()!=null)&&(this.getPkpNo()!=ReportConst.EMPTY_VAL))||((this.getDeliveryOrderNo()!=null)&&(this.getDeliveryOrderNo()!=ReportConst.EMPTY_VAL))
						||((this.getItemCode()!=null)&&(this.getItemCode()!=ReportConst.EMPTY_VAL))||((this.getPoAmount()!=null)&&(this.getPoAmount()!=ReportConst.EMPTY_VAL))||((this.getPrNo()!=null)&&(this.getPrNo()!=ReportConst.EMPTY_VAL))||((this.getPmo()!=null)&&(this.getPmo()!=ReportConst.EMPTY_VAL))||((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL))||((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL))||((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL))||((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getExtentionCode()!=null)&&(this.getExtentionCode()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL))||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getToDate()!=null))){
					if(((this.getFromDate()==null)&&(this.getToDate()!=null))||((this.getFromDate()!=null)&&(this.getToDate()==null))){				
						getManagedBean().setNotSelfView(false);
						getManagedBean().setSelfView(true);
					}else{
						getManagedBean().setNotSelfView(true);
						getManagedBean().setSelfView(false);
					}			
				}else{
					getManagedBean().setNotSelfView(false);
					getManagedBean().setSelfView(true);
				}
			}
		}
	}
	/*
	 *validate quotNo 
	 */
	public void quotationNumberValidation() {
		if((this.getQuotationNo()!=null)&&(this.getQuotationNo()!=ReportConst.EMPTY_VAL)){
			specialCharValidation();			
		}		
	}
	public void specialCharValidation() {
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
	    Matcher match = pattern.matcher(this.getQuotationNo());
	    if(match.matches()){
	    	getQuotationNumber();
	    }else{
	    	this.setQuotationNo(null);
	    	throw new EpException(CommonMethod.getMessageCommon(ReportConst.NUMERIC_CHAR_VALID_ONLY));
	    }	  
	}
	/*
	 *retrieve qtid based on qtno 
	 */
	public void getQuotationNumber() {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		log.info("QtNo="+this.getQuotationNo()); 
		tenderReqDTO.setQtTndrNo(this.getQuotationNo());
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQtByQtNumber(tenderReqDTO);
		ScQt scqt = resDTO.getQtDetail();	
		if(scqt!=null){
			if(scqt.getQtId()!=null){
				log.info("QtId="+scqt.getQtId());
				this.setQuotationId(scqt.getQtId().toString());			
			}else {
				this.setQuotationNo(null);
				throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
			}
		}else{
			this.setQuotationNo(null);
			throw new EpException(CommonMethod.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
		}
	}	
	/*
	 *Clear report criteria 
	 */
	public void resetReportForm(){		
		retriveModuleList();		
		this.setPmo(null);
		this.setPrNo(null);
		this.setPoAmount(null);
		this.setItemCode(null);
		this.setDeliveryOrderNo(null);
		this.setPkpNo(null);
		this.setScoring(null);
		this.setCriteria(null);
		this.setDocumentType(null);
		this.setApproverName(null);
		this.setCrNo(null);
		this.setDeductionType(null);
		this.setInvoiceNo(null);
		this.setMtoContract(null);
		this.setRequisitioner(null);
		this.setContractType(null);
		this.setFlRecNoteNo(null);
		this.setDeliveryAddress(null);
		this.setRecievingOfficer(null);
		this.setDuration(null);
		this.setStatus(null);
		this.setSupplier(null);
		this.setMinistry(null);
		this.setJabatan(null);
		this.setSst(null);
		this.setContractNo(null);
		this.setPtj(null);
		this.setItemType(null);
		this.setItemStatus(null);
		this.setUnspscCode(null);
		this.setUnspscTitle(null);
		this.setExtentionCode(null);
		this.setCompanyName(null);
		this.setRegistrationType(null);
		this.setFromDate(null);
		this.setToDate(null);
		this.setQuotationId(null);
		this.setQuotationNo(null);
		this.setPaymentId(null);
		this.setItemCategory(null);
		this.setItemName(null);
		this.setProofIdentity(null);
		this.setExtentionType(null);
		this.setExtentionBrand(null);
		this.setExtentionMeasurment(null);
		this.setExtentionColour(null);
		this.setEpNumber(null);
		this.setIcNumber(null);
		this.setBusinessRegNo(null);
		this.setMofNumber(null);
		this.setSupportingDocument(null);	
		this.setDocStatus(null);
		this.setSuppDocStatus(null);
		this.setRegistrationNo(null);
		this.setePNo(null);
		this.setApplicationNo(null);
		this.setZone(null);
		this.setSupplierType(null);
		this.setMofRegNo(null);
		this.setMofRegNoFrom(null);
		this.setMofRegNoTo(null);
		this.setRegistrationStatus(null);
		this.setCapitalNaccumFrm(null);
		this.setCapitalNaccumTo(null);	
		this.setPaidUpCapitalNaccumShare(null);
		this.setCategory(null);
		this.setNoOfCategory(null);
		this.setMainCategory(null);
		this.setSubCategory(null);
		this.setSubCategoryDivision(null);
		this.setRole(null);
		this.setApplicationStatus(null);
		this.setCancelledBy(null);
		this.setPaymentType(null);
		this.setApplicationType(null);
		this.setState(null);
		this.setDistrict(null);
		this.setSupplierName(null);
		this.setReportType(null);
		this.setOrganizationType(null);
		this.setKementerian(null);
		this.setNamAgensi(null);
		this.setComplaintNo(null);
		this.setProofIdentity(null);
		this.setCompanyStatus(null);
		this.setCaseNo(null);
		this.setCompApplStatus(null);
		this.setCategoryCode(null);
		this.setSiteVisitOfficer(null);
		this.setReleaseFromDate(null);
		this.setReleaseToDate(null);
		this.setApplicationFromDate(null);
		this.setApplicationToDate(null);
		this.setReceiveFromDate(null);	
		this.setReceiveToDate(null);	
		this.setExpiryFromDate(null);	
		this.setExpiryToDate(null);
		this.setPrintedFromDate(null);	
		this.setPrintedToDate(null);		
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
	/*
	public StreamedContent getStreamedContentPDFFile(String url) {		  
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
		 }
	*/	
	public StreamedContent getReportPdf() {		
		String reportUrl = "http://ngepdevrps01.ngep.com:8891/reports/rwservlet?server=rptsvr_ngepdevrps01_asinst_2&report=Total_Catalog_Item_Unspscwise.jsp&frdt=26/09/2014&todt=30/09/2014&destype=cache&desformat=pdf&userid=ngep_dev/ngep_dev@ngepdb";
		HttpURLConnection urlConnection = null;
		try {
		    URL obj = new URL(reportUrl);
		    urlConnection = (HttpURLConnection) obj.openConnection();	    
		    BufferedInputStream buf = new BufferedInputStream(urlConnection.getInputStream());
		    ResourceResponse portletResponse = (ResourceResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();            
     /*       PortletResponse portletResponse = (PortletResponse) FacesContext
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
}
