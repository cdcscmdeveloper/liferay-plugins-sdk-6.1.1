package my.ep.web.backingbean.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;

import com.lowagie.text.pdf.PdfDocument;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.FileOutputStream;
import java.net.ConnectException;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name="reportBackingBean")
@RequestScoped
public class ReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{
	private static final Logger log = EpLogger.getLogger(ReportBackingBean.class);

	public ReportBackingBean() {
		log.info("inside constructor");

	}
	private String userId;	
	private Date fromDate;
	private Date toDate;
	/*SS start*/
	private String quotationId;
	private String paymentId;
	/*SS end*/
	/*CM start*/
	private String itemType;
	private String itemStatus;
	private String unspscCode;
	private String unspscTitle;
	private String extentionCode;
	private String companyName;
	private String registrationType;	
	private String itemCategory;
	private String itemName;
	private String proofIdentity;
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
	List<SelectItem> reportList;
	ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<SelectItem> moduleList;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public static Logger getLog() {
		return log;
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
	public List<SelectItem> getModuleList() {
		return moduleList;
	}
	public void setModuleList(List<SelectItem> moduleList) {
		this.moduleList = moduleList;
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
	/*
	 *Retrieve module list 
	 */
	public List<SelectItem> retriveModuleList() {
		moduleList = new ArrayList<SelectItem>();		
		SelectItem selectItem= new SelectItem();
		selectItem.setLabel(ReportConst.MOD_SUP_SEL_TTL);
		selectItem.setValue(ModuleCodeConst.SUPPLIER_SELECTION);
		moduleList.add(selectItem);			
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(ReportConst.MOD_CAT_MGT_TTL);
		selectItem1.setValue(ModuleCodeConst.CATALOGUE_MANAGEMENT);
		moduleList.add(selectItem1);			
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(ReportConst.MOD_FL_TTL);
		selectItem2.setValue(ModuleCodeConst.ORDER_FULFILLMENT);
		moduleList.add(selectItem2);				
		return moduleList;
	}
	/*
	 *Retrieve report list by module 
	 */
	public void retriveReportListBymoduleName() {
		rptGenBean = new ReportsGenerateBean();
		this.getManagedBean().setReportName(null);
		this.getManagedBean().setReportNameValue(null);
		this.getManagedBean().setReportList(new ArrayList<SelectItem>());
		this.reportList = this.getManagedBean().getReportList();
		if(getManagedBean().getModuleName()!=null){
			if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){			
				retriveReportListBySCModule();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT)){
				retriveReportListByCMModule();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)){
				retriveReportListByFLModule();
			}
		}
		this.getManagedBean().setReportList(reportList);		
		getManagedBean().setRptGenBean(rptGenBean);
	}
	/*
	 *Retrieve report list by SC module 
	 */
	public void retriveReportListBySCModule() {
		SelectItem selectItem= new SelectItem();
		selectItem.setLabel(ReportConst.SPPPP_PEMATUHAN_OC_RPT_NAME);
		selectItem.setValue(ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL);
		reportList.add(selectItem);
		
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(ReportConst.KPPP1_PEMATUHAN_OC_RPT_NAME);
		selectItem1.setValue(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL);
		reportList.add(selectItem1);
		
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(ReportConst.PDDW_OC_RPT_NAME);
		selectItem2.setValue(ReportConst.PDDW_OC_RPT_VAL);
		reportList.add(selectItem2);			
		
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(ReportConst.SPPSAP2_TEC_RPT_NAME);
		selectItem3.setValue(ReportConst.SPPSAP2_TEC_RPT_VAL);
		reportList.add(selectItem3);			
		
		SelectItem selectItem4= new SelectItem();
		selectItem4.setLabel(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_NAME);
		selectItem4.setValue(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL);
		reportList.add(selectItem4);
		
		SelectItem selectItem5= new SelectItem();
		selectItem5.setLabel(ReportConst.AKPT_TEC_RPT_NAME);
		selectItem5.setValue(ReportConst.AKPT_TEC_RPT_VAL);
		reportList.add(selectItem5);
		
		SelectItem selectItem6= new SelectItem();
		selectItem6.setLabel(ReportConst.TECSPEC_TEC_RPT_NAME);
		selectItem6.setValue(ReportConst.TECSPEC_TEC_RPT_VAL);
		reportList.add(selectItem6);
		
		SelectItem selectItem7= new SelectItem();
		selectItem7.setLabel(ReportConst.OVRL_TECRES_TEC_RPT_NAME);
		selectItem7.setValue(ReportConst.OVRL_TECRES_TEC_RPT_VAL);
		reportList.add(selectItem7);
		
		SelectItem selectItem8= new SelectItem();
		selectItem8.setLabel(ReportConst.SPPSAP2_FEC_RPT_NAME);
		selectItem8.setValue(ReportConst.SPPSAP2_FEC_RPT_VAL);
		reportList.add(selectItem8);
		
		SelectItem selectItem9= new SelectItem();
		selectItem9.setLabel(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_NAME);
		selectItem9.setValue(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL);
		reportList.add(selectItem9);
		
		SelectItem selectItem10= new SelectItem();
		selectItem10.setLabel(ReportConst.AKPT_FEC_RPT_NAME);
		selectItem10.setValue(ReportConst.AKPT_FEC_RPT_VAL);
		reportList.add(selectItem10);
		
		SelectItem selectItem11= new SelectItem();
		selectItem11.setLabel(ReportConst.HARGA_TAWARAN_FEC_RPT_NAME);
		selectItem11.setValue(ReportConst.HARGA_TAWARAN_FEC_RPT_VAL);
		reportList.add(selectItem11);
		
		SelectItem selectItem12= new SelectItem();
		selectItem12.setLabel(ReportConst.OVRL_FINRS_FEC_RPT_NAME);
		selectItem12.setValue(ReportConst.OVRL_FINRS_FEC_RPT_VAL);
		reportList.add(selectItem12);
		
		SelectItem selectItem13= new SelectItem();
		selectItem13.setLabel(ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL);
		selectItem13.setValue(ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL);
		reportList.add(selectItem13);
		
		SelectItem selectItem14= new SelectItem();
		selectItem14.setLabel(ReportConst.KPP1_PEMATUHAN_EC_RPT_NAME);
		selectItem14.setValue(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL);
		reportList.add(selectItem14);
		
		SelectItem selectItem15= new SelectItem();
		selectItem15.setLabel(ReportConst.SPPSAP2_EC_RPT_NAME);
		selectItem15.setValue(ReportConst.SPPSAP2_EC_RPT_VAL);
		reportList.add(selectItem15);
		
		SelectItem selectItem16= new SelectItem();
		selectItem16.setLabel(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_NAME);
		selectItem16.setValue(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL);
		reportList.add(selectItem16);
		
		SelectItem selectItem17= new SelectItem();
		selectItem17.setLabel(ReportConst.AKPTPH_EC_RPT_NAME);
		selectItem17.setValue(ReportConst.AKPTPH_EC_RPT_VAL);
		reportList.add(selectItem17);
		
		SelectItem selectItem18= new SelectItem();
		selectItem18.setLabel(ReportConst.TECSPEC_EC_RPT_NAME);
		selectItem18.setValue(ReportConst.TECSPEC_EC_RPT_VAL);
		reportList.add(selectItem18);
		
		SelectItem selectItem19= new SelectItem();
		selectItem19.setLabel(ReportConst.OVRL_TECRS_EC_RPT_NAME);
		selectItem19.setValue(ReportConst.OVRL_TECRS_EC_RPT_VAL);
		reportList.add(selectItem19);
		
		SelectItem selectItem20= new SelectItem();
		selectItem20.setLabel(ReportConst.OVRL_FINRS_EC_RPT_NAME);
		selectItem20.setValue(ReportConst.OVRL_FINRS_EC_RPT_VAL);
		reportList.add(selectItem20);
		
		SelectItem selectItem21= new SelectItem();
		selectItem21.setLabel(ReportConst.PYMT_RCPT_RPT_NAME);
		selectItem21.setValue(ReportConst.PYMT_RCPT_RPT_VAL);
		reportList.add(selectItem21);
	}
	/*
	 *Retrieve report list by CM module 
	 */
	public void retriveReportListByCMModule() {
		if(this.getUserId()=="Catalogue Administrator"){
			
		}else if(this.getUserId()=="Supplier"){
			
		}
		SelectItem selectItem= new SelectItem();
		selectItem.setLabel(ReportConst.LST_PUB_CAT_ITM_RPT_NAME);
		selectItem.setValue(ReportConst.LST_PUB_CAT_ITM_RPT_VAL);
		reportList.add(selectItem);
		
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(ReportConst.LST_APPR_CAT_ITM_RPT_NAME);
		selectItem1.setValue(ReportConst.LST_APPR_CAT_ITM_RPT_VAL);
		reportList.add(selectItem1);
		
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(ReportConst.LST_REJ_CAT_ITM_RPT_NAME);
		selectItem2.setValue(ReportConst.LST_REJ_CAT_ITM_RPT_VAL);
		reportList.add(selectItem2);			
		
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME);
		selectItem3.setValue(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL);
		reportList.add(selectItem3);			
		
		SelectItem selectItem4= new SelectItem();
		selectItem4.setLabel(ReportConst.LST_EXP_CAT_ITM_RPT_NAME);
		selectItem4.setValue(ReportConst.LST_EXP_CAT_ITM_RPT_VAL);
		reportList.add(selectItem4);
		
		SelectItem selectItem5= new SelectItem();
		selectItem5.setLabel(ReportConst.TOT_CAT_ITM_STS_RPT_NAME);
		selectItem5.setValue(ReportConst.TOT_CAT_ITM_STS_RPT_VAL);
		reportList.add(selectItem5);
		
		SelectItem selectItem6= new SelectItem();
		selectItem6.setLabel(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_NAME);
		selectItem6.setValue(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL);
		reportList.add(selectItem6);
		
		SelectItem selectItem7= new SelectItem();
		selectItem7.setLabel(ReportConst.LST_CAT_ITM_STD_PRC_RPT_NAME);
		selectItem7.setValue(ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL);
		reportList.add(selectItem7);
		
		SelectItem selectItem8= new SelectItem();
		selectItem8.setLabel(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_NAME);
		selectItem8.setValue(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL);
		reportList.add(selectItem8);
		
		SelectItem selectItem9= new SelectItem();
		selectItem9.setLabel(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_NAME);
		selectItem9.setValue(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL);
		reportList.add(selectItem9);
		
		SelectItem selectItem10= new SelectItem();
		selectItem10.setLabel(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_NAME);
		selectItem10.setValue(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL);
		reportList.add(selectItem10);
		
		SelectItem selectItem11= new SelectItem();
		selectItem11.setLabel(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_NAME);
		selectItem11.setValue(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL);
		reportList.add(selectItem11);
		
		SelectItem selectItem12= new SelectItem();
		selectItem12.setLabel(ReportConst.SUM_REJ_CAT_ITM_RPT_NAME);
		selectItem12.setValue(ReportConst.SUM_REJ_CAT_ITM_RPT_VAL);
		reportList.add(selectItem12);
		
		SelectItem selectItem13= new SelectItem();
		selectItem13.setLabel(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_NAME);
		selectItem13.setValue(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL);
		reportList.add(selectItem13);
		
		SelectItem selectItem14= new SelectItem();
		selectItem14.setLabel(ReportConst.LST_UNSPSC_CODE_REQ_RPT_NAME);
		selectItem14.setValue(ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL);
		reportList.add(selectItem14);
		
		SelectItem selectItem15= new SelectItem();
		selectItem15.setLabel(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_NAME);
		selectItem15.setValue(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL);
		reportList.add(selectItem15);
		
		SelectItem selectItem16= new SelectItem();
		selectItem16.setLabel(ReportConst.LST_ITM_CODE_RPT_NAME);
		selectItem16.setValue(ReportConst.LST_ITM_CODE_RPT_VAL);
		reportList.add(selectItem16);
	}	
	/*
	 * Retrieve report list by FL module
	 */
	public void retriveReportListByFLModule() {
				
		SelectItem selectItem1= new SelectItem();
		selectItem1.setLabel(ReportConst.CON_REQ_RPT_MIN_NAME);
		selectItem1.setValue(ReportConst.CON_REQ_RPT_MIN_VAL);
		reportList.add(selectItem1);
		
		SelectItem selectItem2= new SelectItem();
		selectItem2.setLabel(ReportConst.CON_ORD_RPT_MIN_NAME);
		selectItem2.setValue(ReportConst.CON_ORD_RPT_MIN_VAL);
		reportList.add(selectItem2);			
		
		SelectItem selectItem3= new SelectItem();
		selectItem3.setLabel(ReportConst.DEL_ORD_RPT_MIN_NAME);
		selectItem3.setValue(ReportConst.DEL_ORD_RPT_MIN_VAL);
		reportList.add(selectItem3);			
		
		SelectItem selectItem4= new SelectItem();
		selectItem4.setLabel(ReportConst.FL_REC_NOTE_RPT_MIN_NAME);
		selectItem4.setValue(ReportConst.FL_REC_NOTE_RPT_MIN_VAL);
		reportList.add(selectItem4);
		
		SelectItem selectItem5= new SelectItem();
		selectItem5.setLabel(ReportConst.LATE_DEL_RPT_MIN_NAME);
		selectItem5.setValue(ReportConst.LATE_DEL_RPT_MIN_VAL);
		reportList.add(selectItem5);
		
		SelectItem selectItem6= new SelectItem();
		selectItem6.setLabel(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME);
		selectItem6.setValue(ReportConst.DEL_ORD_REJ_RPT_MIN_VAL);
		reportList.add(selectItem6);
		
		SelectItem selectItem7= new SelectItem();
		selectItem7.setLabel(ReportConst.SHP_INSTR_RPT_MIN_NAME);
		selectItem7.setValue(ReportConst.SHP_INSTR_RPT_MIN_VAL);
		reportList.add(selectItem7);
		
		SelectItem selectItem8= new SelectItem();
		selectItem8.setLabel(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME);
		selectItem8.setValue(ReportConst.STP_INSTR_DEL_RPT_MIN_VAL);
		reportList.add(selectItem8);
		
		SelectItem selectItem9= new SelectItem();
		selectItem9.setLabel(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME);
		selectItem9.setValue(ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL);
		reportList.add(selectItem9);
		
		SelectItem selectItem10= new SelectItem();
		selectItem10.setLabel(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME);
		selectItem10.setValue(ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL);
		reportList.add(selectItem10);
		
		SelectItem selectItem11= new SelectItem();
		selectItem11.setLabel(ReportConst.PYMT_MATCH_RPT_MIN_NAME);
		selectItem11.setValue(ReportConst.PYMT_MATCH_RPT_MIN_VAL);
		reportList.add(selectItem11);
		
		SelectItem selectItem12= new SelectItem();
		selectItem12.setLabel(ReportConst.DED_RPT_MIN_NAME);
		selectItem12.setValue(ReportConst.DED_RPT_MIN_VAL);
		reportList.add(selectItem12);
		
		SelectItem selectItem13= new SelectItem();
		selectItem13.setLabel(ReportConst.CON_REQ_CANC_RPT_MIN_NAME);
		selectItem13.setValue(ReportConst.CON_REQ_CANC_RPT_MIN_VAL);
		reportList.add(selectItem13);
		
		SelectItem selectItem14= new SelectItem();
		selectItem14.setLabel(ReportConst.CON_ORD_CANC_RPT_MIN_NAME);
		selectItem14.setValue(ReportConst.CON_ORD_CANC_RPT_MIN_VAL);
		reportList.add(selectItem14);
		
		SelectItem selectItem15= new SelectItem();
		selectItem15.setLabel(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME);
		selectItem15.setValue(ReportConst.DEL_ORD_CANC_RPT_MIN_VAL);
		reportList.add(selectItem15);
		
		SelectItem selectItem16= new SelectItem();
		selectItem16.setLabel(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME);
		selectItem16.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL);
		reportList.add(selectItem16);
		
		SelectItem selectItem17= new SelectItem();
		selectItem17.setLabel(ReportConst.INV_CANC_RPT_MIN_NAME);
		selectItem17.setValue(ReportConst.INV_CANC_RPT_MIN_VAL);
		reportList.add(selectItem17);
		
		SelectItem selectItem18= new SelectItem();
		selectItem18.setLabel(ReportConst.YEP_RPT_MIN_NAME);
		selectItem18.setValue(ReportConst.YEP_RPT_MIN_VAL);
		reportList.add(selectItem18);
		
		SelectItem selectItem19= new SelectItem();
		selectItem19.setLabel(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME);
		selectItem19.setValue(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL);
		reportList.add(selectItem19);
		
		SelectItem selectItem20= new SelectItem();
		selectItem20.setLabel(ReportConst.SUP_PERF_RPT_MIN_NAME);
		selectItem20.setValue(ReportConst.SUP_PERF_RPT_MIN_VAL);
		reportList.add(selectItem20);
		
		SelectItem selectItem21= new SelectItem();
		selectItem21.setLabel(ReportConst.CON_REQ_RPT_CEN_PEN_NAME);
		selectItem21.setValue(ReportConst.CON_REQ_RPT_CEN_PEN_VAL);
		reportList.add(selectItem21);
		
		SelectItem selectItem22= new SelectItem();
		selectItem22.setLabel(ReportConst.CON_ORD_RPT_CEN_PEN_NAME);
		selectItem22.setValue(ReportConst.CON_ORD_RPT_CEN_PEN_VAL);
		reportList.add(selectItem22);
		
		SelectItem selectItem23= new SelectItem();
		selectItem23.setLabel(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME);
		selectItem23.setValue(ReportConst.DEL_ORD_RPT_CEN_PEN_VAL);
		reportList.add(selectItem23);
		
		SelectItem selectItem24= new SelectItem();
		selectItem24.setLabel(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME);
		selectItem24.setValue(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL);
		reportList.add(selectItem24);
		
		SelectItem selectItem25= new SelectItem();
		selectItem25.setLabel(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME);
		selectItem25.setValue(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL);
		reportList.add(selectItem25);
		
		SelectItem selectItem26= new SelectItem();
		selectItem26.setLabel(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME);
		selectItem26.setValue(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL);
		reportList.add(selectItem26);
		
		SelectItem selectItem27= new SelectItem();
		selectItem27.setLabel(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME);
		selectItem27.setValue(ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL);
		reportList.add(selectItem27);
		
		SelectItem selectItem28= new SelectItem();
		selectItem28.setLabel(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME);
		selectItem28.setValue(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL);
		reportList.add(selectItem28);
		
		SelectItem selectItem29= new SelectItem();
		selectItem29.setLabel(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME);
		selectItem29.setValue(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL);
		reportList.add(selectItem29);
		
		SelectItem selectItem30= new SelectItem();
		selectItem30.setLabel(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME);
		selectItem30.setValue(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL);
		reportList.add(selectItem30);
		
		SelectItem selectItem31= new SelectItem();
		selectItem31.setLabel(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME);
		selectItem31.setValue(ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL);
		reportList.add(selectItem31);
		
		SelectItem selectItem32= new SelectItem();
		selectItem32.setLabel(ReportConst.DED_RPT_CEN_PEN_NAME);
		selectItem32.setValue(ReportConst.DED_RPT_CEN_PEN_VAL);
		reportList.add(selectItem32);
		
		SelectItem selectItem33= new SelectItem();
		selectItem33.setLabel(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME);
		selectItem33.setValue(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL);
		reportList.add(selectItem33);
		
		SelectItem selectItem34= new SelectItem();
		selectItem34.setLabel(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME);
		selectItem34.setValue(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL);
		reportList.add(selectItem34);
		
		SelectItem selectItem35= new SelectItem();
		selectItem35.setLabel(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME);
		selectItem35.setValue(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL);
		reportList.add(selectItem35);
		
		SelectItem selectItem36= new SelectItem();
		selectItem36.setLabel(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME);
		selectItem36.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL);
		reportList.add(selectItem36);

		SelectItem selectItem37= new SelectItem();
		selectItem37.setLabel(ReportConst.INV_CANC_RPT_CEN_PEN_NAME);
		selectItem37.setValue(ReportConst.INV_CANC_RPT_CEN_PEN_VAL);
		reportList.add(selectItem37);

		SelectItem selectItem38= new SelectItem();
		selectItem38.setLabel(ReportConst.YEP_RPT_CEN_PEN_NAME);
		selectItem38.setValue(ReportConst.YEP_RPT_CEN_PEN_VAL);
		reportList.add(selectItem38);

		SelectItem selectItem39= new SelectItem();
		selectItem39.setLabel(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME);
		selectItem39.setValue(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL);
		reportList.add(selectItem39);

		SelectItem selectItem40= new SelectItem();
		selectItem40.setLabel(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME);
		selectItem40.setValue(ReportConst.SUP_PERF_RPT_CEN_PEN_VAL);
		reportList.add(selectItem40);

		SelectItem selectItem41= new SelectItem();
		selectItem41.setLabel(ReportConst.PUR_REQ_RPT_PO_NAME);
		selectItem41.setValue(ReportConst.PUR_REQ_RPT_PO_VAL);
		reportList.add(selectItem41);

		SelectItem selectItem42= new SelectItem();
		selectItem42.setLabel(ReportConst.PUR_ORD_RPT_PO_NAME);
		selectItem42.setValue(ReportConst.PUR_ORD_RPT_PO_VAL);
		reportList.add(selectItem42);

		SelectItem selectItem43= new SelectItem();
		selectItem43.setLabel(ReportConst.EMR_PROCUR_RPT_PO_NAME);
		selectItem43.setValue(ReportConst.EMR_PROCUR_RPT_PO_VAL);
		reportList.add(selectItem43);

		SelectItem selectItem44= new SelectItem();
		selectItem44.setLabel(ReportConst.PUR_ORD_REJ_RPT_PO_NAME);
		selectItem44.setValue(ReportConst.PUR_ORD_REJ_RPT_PO_VAL);
		reportList.add(selectItem44);

		SelectItem selectItem45= new SelectItem();
		selectItem45.setLabel(ReportConst.DEL_ORD_RPT_PO_NAME);
		selectItem45.setValue(ReportConst.DEL_ORD_RPT_PO_VAL);
		reportList.add(selectItem45);

		SelectItem selectItem46= new SelectItem();
		selectItem46.setLabel(ReportConst.FL_REC_NOTE_RPT_PO_NAME);
		selectItem46.setValue(ReportConst.FL_REC_NOTE_RPT_PO_VAL);
		reportList.add(selectItem46);

		SelectItem selectItem47= new SelectItem();
		selectItem47.setLabel(ReportConst.LATE_DEL_RPT_PO_NAME);
		selectItem47.setValue(ReportConst.LATE_DEL_RPT_PO_VAL);
		reportList.add(selectItem47);

		SelectItem selectItem48= new SelectItem();
		selectItem48.setLabel(ReportConst.DEL_ORD_REJ_RPT_PO_NAME);
		selectItem48.setValue(ReportConst.DEL_ORD_REJ_RPT_PO_VAL);
		reportList.add(selectItem48);

		SelectItem selectItem49= new SelectItem();
		selectItem49.setLabel(ReportConst.STP_INSTR_DEL_RPT_PO_NAME);
		selectItem49.setValue(ReportConst.STP_INSTR_DEL_RPT_PO_VAL);
		reportList.add(selectItem49);

		SelectItem selectItem50= new SelectItem();
		selectItem50.setLabel(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME);
		selectItem50.setValue(ReportConst.INV_RPT_VIEW_SUPL_PO_VAL);
		reportList.add(selectItem50);

		SelectItem selectItem51= new SelectItem();
		selectItem51.setLabel(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME);
		selectItem51.setValue(ReportConst.INV_RPT_VIEW_PTJ_PO_VAL);
		reportList.add(selectItem51);

		SelectItem selectItem52= new SelectItem();
		selectItem52.setLabel(ReportConst.PYMT_MATCH_RPT_PO_NAME);
		selectItem52.setValue(ReportConst.PYMT_MATCH_RPT_PO_VAL);
		reportList.add(selectItem52);

		SelectItem selectItem53= new SelectItem();
		selectItem53.setLabel(ReportConst.PUR_REQ_CANC_RPT_PO_NAME);
		selectItem53.setValue(ReportConst.PUR_REQ_CANC_RPT_PO_VAL);
		reportList.add(selectItem53);

		SelectItem selectItem54= new SelectItem();
		selectItem54.setLabel(ReportConst.PUR_ORD_CANC_RPT_PO_NAME);
		selectItem54.setValue(ReportConst.PUR_ORD_CANC_RPT_PO_VAL);
		reportList.add(selectItem54);

		SelectItem selectItem55= new SelectItem();
		selectItem55.setLabel(ReportConst.DEL_ORD_CANC_RPT_PO_NAME);
		selectItem55.setValue(ReportConst.DEL_ORD_CANC_RPT_PO_VAL);
		reportList.add(selectItem55);

		SelectItem selectItem56= new SelectItem();
		selectItem56.setLabel(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME);
		selectItem56.setValue(ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL);
		reportList.add(selectItem56);

		SelectItem selectItem57= new SelectItem();
		selectItem57.setLabel(ReportConst.INV_CANC_RPT_PO_NAME);
		selectItem57.setValue(ReportConst.INV_CANC_RPT_PO_VAL);
		reportList.add(selectItem57);
		
		SelectItem selectItem58= new SelectItem();
		selectItem58.setLabel(ReportConst.YEP_RPT_PO_NAME);
		selectItem58.setValue(ReportConst.YEP_RPT_PO_VAL);
		reportList.add(selectItem58);
		
		SelectItem selectItem59= new SelectItem();
		selectItem59.setLabel(ReportConst.YEP_AP58A_RPT_PO_NAME);
		selectItem59.setValue(ReportConst.YEP_AP58A_RPT_PO_VAL);
		reportList.add(selectItem59);
		
		SelectItem selectItem60= new SelectItem();
		selectItem60.setLabel(ReportConst.SUP_PERF_RPT_PO_NAME);
		selectItem60.setValue(ReportConst.SUP_PERF_RPT_PO_VAL);
		reportList.add(selectItem60);	
		
		SelectItem selectItem61= new SelectItem();
		selectItem61.setLabel(ReportConst.SUMM_RPT_PAN_ITM_NAME);
		selectItem61.setValue(ReportConst.SUMM_RPT_PAN_ITM_VAL);
		reportList.add(selectItem61);
	}	
	/*
	 *Enable  criteria variables by report name 
	 */
	public void criteriaVariablesByReport() {
		this.getManagedBean().setRptGenBean(null);		
		if((getManagedBean().getModuleName())!=null && (getManagedBean().getReportNameValue())!=null){
			rptGenBean = new ReportsGenerateBean();					
			if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))){ 
				criteriaVariablesByReportByCM();					
			}else if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))){ 
				criteriaVariablesByReportBySC();				
			}else if((getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))){ 
				criteriaVariablesByReportByFL();
			}
			this.getManagedBean().setRptGenBean(rptGenBean);
		}else{
			
			throw new EpException(ReportConst.PLZ_SEL_ONE_RPT_SEARCH);
		}
	}
	/*
	 *Enable CM module criteria by report name 
	 */
	public void criteriaVariablesByReportByCM(){
		if(this.getUserId()==ReportConst.CATLOG_ADMIN){		
			switch (getManagedBean().getReportNameValue()) {
				case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
		        	rptGenBean.setItemTypeView(true);
					rptGenBean.setItemStatusView(true);
					rptGenBean.setUnspscCodeView(true);
					rptGenBean.setUnspscTitleView(true);
					rptGenBean.setExtentionCodeView(true);
					rptGenBean.setFromDateView(true);
					rptGenBean.setToDateView(true);	
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					getManagedBean().setReportName(ReportConst.LST_PUB_CAT_ITM_RPT_NAME);
		            break;
		        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:
		        	rptGenBean.setItemTypeView(true);			
					rptGenBean.setUnspscCodeView(true);
					rptGenBean.setUnspscTitleView(true);
					rptGenBean.setExtentionCodeView(true);	
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					rptGenBean.setFromDateView(true);
					rptGenBean.setToDateView(true);			
					getManagedBean().setReportName(ReportConst.LST_APPR_CAT_ITM_RPT_NAME);
		            break;
		        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:
		        	rptGenBean.setItemTypeView(true);			
					rptGenBean.setUnspscCodeView(true);
					rptGenBean.setUnspscTitleView(true);
					rptGenBean.setExtentionCodeView(true);
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					rptGenBean.setFromDateView(true);
					rptGenBean.setToDateView(true);			
					getManagedBean().setReportName(ReportConst.LST_REJ_CAT_ITM_RPT_NAME);
		            break;
		        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:
		        	rptGenBean.setItemTypeView(true);			
					rptGenBean.setUnspscCodeView(true);
					rptGenBean.setUnspscTitleView(true);
					rptGenBean.setExtentionCodeView(true);
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					rptGenBean.setFromDateView(true);
					rptGenBean.setToDateView(true);			
					getManagedBean().setReportName(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME);	
		            break;
		        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:
		        	rptGenBean.setItemTypeView(true);			
					rptGenBean.setUnspscCodeView(true);
					rptGenBean.setUnspscTitleView(true);
					rptGenBean.setExtentionCodeView(true);
					rptGenBean.setCompanyNameView(true);
					rptGenBean.setRegistrationTypeView(true);
					rptGenBean.setProofIdentityView(true);
					rptGenBean.setFromDateView(true);
					rptGenBean.setToDateView(true);			
					getManagedBean().setReportName(ReportConst.LST_EXP_CAT_ITM_RPT_NAME);
		            break;
		        default:            
		            break;
			}
		}else if(this.getUserId()==ReportConst.SUPPL){
		}else if(this.getUserId()==ReportConst.CODIF_ADMIN){
		}		
		switch (getManagedBean().getReportNameValue()) {
		
	        case ReportConst.LST_PUB_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);
				rptGenBean.setItemStatusView(true);
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_PUB_CAT_ITM_RPT_NAME);
	            break;
	        case ReportConst.LST_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemCategoryView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_APPR_CAT_ITM_RPT_NAME);
	            break;
	        case ReportConst.LST_REJ_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_REJ_CAT_ITM_RPT_NAME);
	            break;
	        case ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_NAME);	
	            break;
	        case ReportConst.LST_EXP_CAT_ITM_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_EXP_CAT_ITM_RPT_NAME);
	            break;
	        case ReportConst.TOT_CAT_ITM_STS_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(ReportConst.TOT_CAT_ITM_STS_RPT_NAME);	
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
				getManagedBean().setReportName(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_NAME);
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
				getManagedBean().setReportName(ReportConst.LST_CAT_ITM_STD_PRC_RPT_NAME);
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
				getManagedBean().setReportName(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_NAME);	
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
				getManagedBean().setReportName(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_NAME);
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
				getManagedBean().setReportName(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_NAME);
	            break;
	        case ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				getManagedBean().setReportName(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_NAME);	
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
				getManagedBean().setReportName(ReportConst.SUM_REJ_CAT_ITM_RPT_NAME);
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
				getManagedBean().setReportName(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_NAME);
	            break;
	        case ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL:
	        	rptGenBean.setItemTypeView(true);						
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(ReportConst.LST_UNSPSC_CODE_REQ_RPT_NAME);
	            break;
	        case ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL:
	        	rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);							
				rptGenBean.setCompanyNameView(true);
				rptGenBean.setRegistrationTypeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);			
				rptGenBean.setProofIdentityView(true);
				getManagedBean().setReportName(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_NAME);	
	            break;
	        case ReportConst.LST_ITM_CODE_RPT_VAL:
	        	rptGenBean.setItemNameView(true);			
				rptGenBean.setUnspscCodeView(true);
				rptGenBean.setUnspscTitleView(true);
				rptGenBean.setExtentionCodeView(true);			
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.LST_ITM_CODE_RPT_NAME);
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
		 		rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.SPPPP_PEMATUHAN_OC_RPT_NAME);
	            break; 
	        case ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.KPPP1_PEMATUHAN_OC_RPT_NAME);
	            break;
	        case ReportConst.PDDW_OC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.PDDW_OC_RPT_NAME);
	            break;
	        case ReportConst.SPPSAP2_TEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.SPPSAP2_TEC_RPT_NAME);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_NAME);
	            break;
	        case ReportConst.AKPT_TEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.AKPT_TEC_RPT_NAME);	
	            break;
	        case ReportConst.TECSPEC_TEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.TECSPEC_TEC_RPT_NAME);
	            break;
	        case ReportConst.OVRL_TECRES_TEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.OVRL_TECRES_TEC_RPT_NAME);
	            break;
	        case ReportConst.SPPSAP2_FEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.SPPSAP2_FEC_RPT_NAME);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_NAME);	
	            break;
	        case ReportConst.AKPT_FEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.AKPT_FEC_RPT_NAME);
	            break;
	        case ReportConst.HARGA_TAWARAN_FEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.HARGA_TAWARAN_FEC_RPT_NAME);
	            break;
	        case ReportConst.OVRL_FINRS_FEC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.OVRL_FINRS_FEC_RPT_NAME);
	            break;
	        case ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.SPPP1_PEMATUHAN_EC_RPT_NAME);
	            break;
	        case ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.KPP1_PEMATUHAN_EC_RPT_NAME);		
	            break;
	        case ReportConst.SPPSAP2_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.SPPSAP2_EC_RPT_NAME);
	            break;
	        case ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_NAME);
	            break;
	        case ReportConst.AKPTPH_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.AKPTPH_EC_RPT_NAME);	
	            break;
	        case ReportConst.TECSPEC_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.TECSPEC_EC_RPT_NAME);
	            break;
	        case ReportConst.OVRL_TECRS_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.OVRL_TECRS_EC_RPT_NAME);
	            break;
	        case ReportConst.OVRL_FINRS_EC_RPT_VAL:
	        	rptGenBean.setQuotationIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.OVRL_FINRS_EC_RPT_NAME);			
	            break;
	        case ReportConst.PYMT_RCPT_RPT_VAL:
	        	rptGenBean.setPaymentIdView(true);
				rptGenBean.setFromDateView(true);
				rptGenBean.setToDateView(true);
				getManagedBean().setReportName(ReportConst.PYMT_RCPT_RPT_NAME);
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
			getManagedBean().setReportName(ReportConst.CON_REQ_RPT_MIN_NAME);		
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
			getManagedBean().setReportName(ReportConst.CON_ORD_RPT_MIN_NAME);
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
			getManagedBean().setReportName(ReportConst.DEL_ORD_RPT_MIN_NAME);
			 break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_RPT_MIN_NAME);
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
			getManagedBean().setReportName(ReportConst.LATE_DEL_RPT_MIN_NAME);
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
			getManagedBean().setReportName(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME);
			 break;
		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setMtoContractView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.SHP_INSTR_RPT_MIN_NAME);	
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
			getManagedBean().setReportName(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME);		
			 break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME);	
			 break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME);	
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
			getManagedBean().setReportName(ReportConst.PYMT_MATCH_RPT_MIN_NAME);	
			 break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setDeductionTypeView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.DED_RPT_MIN_NAME);
			 break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			rptGenBean.setCrNoView(true);
			rptGenBean.setApproverNameView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.CON_REQ_CANC_RPT_MIN_NAME);	
			 break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setApproverNameView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.CON_ORD_CANC_RPT_MIN_NAME);		
			 break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME);
			 break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME);	
			 break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);	
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_CANC_RPT_MIN_NAME);
			break;
		case ReportConst.YEP_RPT_MIN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_RPT_MIN_NAME);	
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME);	
			break;
		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.SUP_PERF_RPT_MIN_NAME);	
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
			getManagedBean().setReportName(ReportConst.CON_REQ_RPT_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.CON_ORD_RPT_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME);	
			break;
		case ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME);
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setMtoContractView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME);
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
			getManagedBean().setReportName(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME);		
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME);	
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setContractNoView(true);
			rptGenBean.setSstView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME);
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
			getManagedBean().setReportName(ReportConst.DED_RPT_CEN_PEN_NAME);	
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
			getManagedBean().setReportName(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME);	
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setApproverNameView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME);	
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME);	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME);		
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_CANC_RPT_CEN_PEN_NAME);	
			break;
		case ReportConst.YEP_RPT_CEN_PEN_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_RPT_CEN_PEN_NAME);
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setPtjView(true);
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME);
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME);		
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:	
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setRequisitionerView(true);
			rptGenBean.setStatusView(true);			
			rptGenBean.setFromDateView(true);			
			getManagedBean().setReportName(ReportConst.PUR_REQ_RPT_PO_NAME);	
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
			getManagedBean().setReportName(ReportConst.PUR_ORD_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.EMR_PROCUR_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.PUR_ORD_REJ_RPT_PO_NAME);	
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setDeliveryOrderNoView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.DEL_ORD_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.LATE_DEL_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.DEL_ORD_REJ_RPT_PO_NAME);	
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
			getManagedBean().setReportName(ReportConst.STP_INSTR_DEL_RPT_PO_NAME);
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setStatusView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME);		
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME);	
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			rptGenBean.setDurationView(true);
			rptGenBean.setInvoiceNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPmoView(true);
			rptGenBean.setStatusView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.PYMT_MATCH_RPT_PO_NAME);		
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
			getManagedBean().setReportName(ReportConst.PUR_REQ_CANC_RPT_PO_NAME);
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
			getManagedBean().setReportName(ReportConst.PUR_ORD_CANC_RPT_PO_NAME);
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setRecievingOfficerView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.DEL_ORD_CANC_RPT_PO_NAME);	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setDeliveryAddressView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME);
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);			
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.INV_CANC_RPT_PO_NAME);	
			break;
		case ReportConst.YEP_RPT_PO_VAL:
			rptGenBean.setDocumentTypeView(true);
			rptGenBean.setDurationView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setPkpNoView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_RPT_PO_NAME);	
			break;
		case ReportConst.YEP_AP58A_RPT_PO_VAL:
			rptGenBean.setPkpNoView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.YEP_AP58A_RPT_PO_NAME);	
			break;
		case ReportConst.SUP_PERF_RPT_PO_VAL:
			rptGenBean.setSupplierView(true);
			rptGenBean.setCriteriaView(true);
			rptGenBean.setScoringView(true);
			rptGenBean.setPtjView(true);
			rptGenBean.setMinistryView(true);
			rptGenBean.setJabatanView(true);
			rptGenBean.setFromDateView(true);
			getManagedBean().setReportName(ReportConst.SUP_PERF_RPT_PO_NAME);	
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
			getManagedBean().setReportName(ReportConst.SUMM_RPT_PAN_ITM_NAME);	
			break;
		default:            
            break;
		}
		
	}
	/*
	 *Generate all modules reports 
	 */
	public void generateReport(){		
		if((getManagedBean().getModuleName()!=null)&&(getManagedBean().getReportNameValue()!=null)){
			if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION)){			
				generateSCReport();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT)){
				generateCMReport();			
			}else if(getManagedBean().getModuleName().equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)){
				generateFLReport();
			}
		}else{
			throw new EpException(ReportConst.PLZ_SEL_ONE_RPT_SEARCH);
		}
	}
	/*
	 *Generate SC module report  
	 */
	public void generateSCReport(){		
		if(((this.getQuotationId()!=null)&&(this.getQuotationId()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))){	
			rptGenBean.setQuotationIdTotVar(ReportConst.AMP_QUOTE_ID_EQ+this.getQuotationId());	
			Map<String, String> urlSCReportJsps = new HashMap<String, String>();
			urlSCReportJsps.put(ReportConst.SPPPP_PEMATUHAN_OC_RPT_VAL, "firststage.rdf");
			urlSCReportJsps.put(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.PDDW_OC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.SPPSAP2_TEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_TEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.AKPT_TEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.TECSPEC_TEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.OVRL_TECRES_TEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.SPPSAP2_FEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_FEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.AKPT_FEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.HARGA_TAWARAN_FEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.OVRL_FINRS_FEC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.SPPP1_PEMATUHAN_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.SPPSAP2_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.KPSAP2_PEMARKAHAN_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.AKPTPH_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.TECSPEC_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.OVRL_TECRS_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.OVRL_FINRS_EC_RPT_VAL, ReportConst.EMPTY_VAL);
			urlSCReportJsps.put(ReportConst.PYMT_RCPT_RPT_VAL, ReportConst.EMPTY_VAL);
			rptGenBean.setUrlReportJSPName(urlSCReportJsps.get(getManagedBean().getReportNameValue().trim()));
			
			String url =ReportConst.EXC_PREV_URL+rptGenBean.getUrlReportJSPName()+rptGenBean.getQuotationIdTotVar()+ReportConst.EXC_NEXT_URL;
			try {
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(url);				
			
			} catch (IOException e) {				
				e.printStackTrace();
			}			
			log.info("url="+url);
		}else{
			throw new EpException(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH);
		}
	}
	/*
	 *Generate CM module report  
	 */
	public void generateCMReport(){		
		if(((this.getItemName()!=null)&&(this.getItemName()!=ReportConst.EMPTY_VAL))||((this.getItemType()!=null)&&(this.getItemType()!=ReportConst.EMPTY_VAL))||((this.getItemStatus()!=null)&&(this.getItemStatus()!=ReportConst.EMPTY_VAL))||((this.getUnspscCode()!=null)&&(this.getUnspscCode()!=ReportConst.EMPTY_VAL))||((this.getUnspscTitle()!=null)&&(this.getUnspscTitle()!=ReportConst.EMPTY_VAL))||((this.getExtentionCode()!=null)&&(this.getExtentionCode()!=ReportConst.EMPTY_VAL))||((this.getCompanyName()!=null)&&(this.getCompanyName()!=ReportConst.EMPTY_VAL))||((this.getRegistrationType()!=null)&&(this.getRegistrationType()!=ReportConst.EMPTY_VAL))||((this.getItemCategory()!=null)&&(this.getItemCategory()!=ReportConst.EMPTY_VAL))||((this.getProofIdentity()!=null)&&(this.getProofIdentity()!=ReportConst.EMPTY_VAL)||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)))){
			
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
			//-----------
			Map<String, String> urlCMReportNameJsps = new HashMap<String, String>();
			urlCMReportNameJsps.put(ReportConst.LST_PUB_CAT_ITM_RPT_VAL, ReportConst.CM_CAT_ITM_STD_PRC_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_APPR_CAT_ITM_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_REJ_CAT_ITM_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_PEND_APPR_CAT_ITM_RPT_VAL, ReportConst.CM_EXP_CAT_ITM_JSP);
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_STS_RPT_VAL, ReportConst.CM_TOT_CAT_ITM_STWISE_JSP);
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_UNSPSC_CODE_RPT_VAL, ReportConst.CM_TOT_CAT_ITM_UNSPSCWISE_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_STD_PRC_RPT_VAL, ReportConst.CM_CAT_ITM_STD_PRC_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_TIER_PRC_RPT_VAL, ReportConst.CM_CAT_ITM_TIER_PRC_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_ZON_PRC_RPT_VAL, ReportConst.CM_CAT_ITEM_ZONAL_PRC_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_CAT_ITM_ZON_TIER_PRC_RPT_VAL, ReportConst.CM_CAT_ITEM_ZONAL_TIER_PRC_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_PEND_CODE_ASGN_AGNCY_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.SUM_REJ_CAT_ITM_RPT_VAL, ReportConst.CM_REJ_CAT_ITM_SMRY_JSP);
			urlCMReportNameJsps.put(ReportConst.TOT_CAT_ITM_APPR_REJ_RPT_VAL, ReportConst.CM_APPR_N_REJ_CAT_ITM_JSP);
			urlCMReportNameJsps.put(ReportConst.LST_UNSPSC_CODE_REQ_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_NEW_EXT_CODE_CREA_RPT_VAL, ReportConst.EMPTY_VAL);
			urlCMReportNameJsps.put(ReportConst.LST_ITM_CODE_RPT_VAL, ReportConst.EMPTY_VAL);
			//-----------			
			rptGenBean.setUrlReportJSPName(urlCMReportNameJsps.get(getManagedBean().getReportNameValue().trim()));			
			String url=ReportConst.EXC_PREV_URL+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionCodeTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getProofIdentityTotVar()+ReportConst.EXC_NEXT_URL;
			try {				
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(url);				
			
			} catch (IOException e) {				
				e.printStackTrace();
			}
			log.info(url);
		}else{
			throw new EpException(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH);
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
			
			String url=ReportConst.EXC_PREV_URL+rptGenBean.getUrlReportJSPName()+rptGenBean.getItemNameTotVar()+rptGenBean.getItemTypeTotVar()+rptGenBean.getItemStatusTotVar()+rptGenBean.getUnspscCodeTotVar()+rptGenBean.getUnspscTitleTotVar()+rptGenBean.getExtentionCodeTotVar()+rptGenBean.getCompanyNameTotVar()+rptGenBean.getRegistrationTypeTotVar()+rptGenBean.getItemCategoryTotVar()+rptGenBean.getProofIdentityTotVar()+rptGenBean.getPtjTotVar()+rptGenBean.getContractNoTotVar()+rptGenBean.getSstTotVar()+rptGenBean.getJabatanTotVar()+rptGenBean.getMinistryTotVar()+rptGenBean.getSupplierTotVar()+rptGenBean.getStatusTotVar()+rptGenBean.getDurationTotVar()+rptGenBean.getRecievingOfficerTotVar()+rptGenBean.getDeliveryAddressTotVar()+rptGenBean.getFlRecNoteNoTotVar()+rptGenBean.getContractTypeTotVar()+rptGenBean.getRequisitionerTotVar()+rptGenBean.getMtoContractTotVar()+rptGenBean.getInvoiceNoTotVar()+rptGenBean.getDeductionTypeTotVar()+rptGenBean.getCrNoTotVar()+rptGenBean.getApproverNameTotVar()+rptGenBean.getDocumentTypeTotVar()+rptGenBean.getCriteriaTotVar()+rptGenBean.getScoringTotVar()+rptGenBean.getPkpNoTotVar()+rptGenBean.getDeliveryOrderNoTotVar()+rptGenBean.getItemCodeTotVar()+rptGenBean.getPoAmountTotVar()+rptGenBean.getPrNoTotVar()+rptGenBean.getPmoTotVar()+ReportConst.EXC_NEXT_URL;
			try {
				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				externalContext.redirect(url);
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
			log.info(url);
		}else{
			throw new EpException(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH);
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
		this.setPaymentId(null);
		this.setItemCategory(null);
		this.setItemName(null);
		this.setProofIdentity(null);		
	}	
}
