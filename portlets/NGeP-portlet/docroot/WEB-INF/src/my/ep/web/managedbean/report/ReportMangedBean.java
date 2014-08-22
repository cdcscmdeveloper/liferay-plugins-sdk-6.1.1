package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.managedbean.BaseManagedBean;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportManagedBean")
@SessionScoped
public class ReportMangedBean extends BaseManagedBean{
	
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> reportList=new ArrayList<SelectItem>();
	private String moduleName;
	private String reportName;
	private String reportNameValue;
	private boolean notSelfView = false;
	private boolean selfView = true;
	
	public String getReportNameValue() {
		return reportNameValue;
	}
	public void setReportNameValue(String reportNameValue) {
		this.reportNameValue = reportNameValue;
	}
	private ReportsGenerateBean rptGenBean ;
	
	public boolean isNotSelfView() {
		return notSelfView;
	}
	public void setNotSelfView(boolean notSelfView) {
		this.notSelfView = notSelfView;
	}
	public boolean isSelfView() {
		return selfView;
	}
	public void setSelfView(boolean selfView) {
		this.selfView = selfView;
	}
	public String getModuleName() {
		return moduleName;
	}
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public List<SelectItem> getReportList() {
		return reportList;
	}
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}
	
	private String userId;	
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
	private String proofIdentity;
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
	
	public String getQuotationNo() {
		return quotationNo;
	}
	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}
	public String getMofNumber() {
		return mofNumber;
	}
	public void setMofNumber(String mofNumber) {
		this.mofNumber = mofNumber;
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
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
		
	public String getPmo() {
		return pmo;
	}
	public void setPmo(String pmo) {
		this.pmo = pmo;
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
	public String getStatus() {
		return status;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
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
	
	public String getProofIdentity() {
		return proofIdentity;
	}
	public void setProofIdentity(String proofIdentity) {
		this.proofIdentity = proofIdentity;
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
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getQuotationId() {
		return quotationId;
	}
	public void setQuotationId(String quotationId) {
		this.quotationId = quotationId;
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
	public String getExtentionCode() {
		return extentionCode;
	}
	public void setExtentionCode(String extentionCode) {
		this.extentionCode = extentionCode;
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
}
