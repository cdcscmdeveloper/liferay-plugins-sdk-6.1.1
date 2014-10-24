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
@ManagedBean(name="flReportBackingBean")
@ViewScoped
public class FLReportBackingBean extends SourcingBaseBackingBean<ReportMangedBean>{

	ReportsGenerateBean rptGenerateBean = new ReportsGenerateBean();
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
	private Date fromDate;
	private Date toDate;
	private String flItemName;
	private String constructedParamString; 
	
	private String flContractOrder;
	private String flContractRequest;
	private String flPurchaseOrderNo;
	
	public String getFlPurchaseOrderNo() {
		return flPurchaseOrderNo;
	}
	public void setFlPurchaseOrderNo(String flPurchaseOrderNo) {
		this.flPurchaseOrderNo = flPurchaseOrderNo;
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
	public String getFlItemName() {
		return flItemName;
	}
	public void setFlItemName(String flItemName) {
		this.flItemName = flItemName;
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

	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}

	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
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
	 * Enable FL module criteria by report name
	 */
	public void criteriaVariablesByReportByFL(ReportsGenerateBean rptGenBean){
		
	switch (getManagedBean().getReportValue()) {
		case ReportConst.CON_REQ_RPT_MIN_VAL:	
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_MIN_NAME));		
			 break;
	 	case ReportConst.CON_ORD_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);			
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_MIN_NAME));
			 break;
		case ReportConst.LATE_DEL_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_MIN_NAME));
			 break;
		case ReportConst.DEL_ORD_REJ_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_MIN_NAME));
			 break;
		case ReportConst.SHP_INSTR_RPT_MIN_VAL:
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_MIN_NAME));	
			 break;
		case ReportConst.STP_INSTR_DEL_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true); 
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_MIN_NAME));		
			 break;
		case ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_MIN_NAME));	
			 break;
		case ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_MIN_NAME));	
			 break;
		case ReportConst.PYMT_MATCH_RPT_MIN_VAL:
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);	
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_MIN_NAME));	
			 break;
		case ReportConst.DED_RPT_MIN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlDeductionTypeView(true);
			//rptGenBean.setFlPmoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_MIN_NAME));
			 break;
		case ReportConst.CON_REQ_CANC_RPT_MIN_VAL:
			//rptGenBean.setFlCrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.CON_ORD_CANC_RPT_MIN_VAL:
			//rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_MIN_NAME));		
			 break;
		case ReportConst.DEL_ORD_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_MIN_NAME));
			 break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_MIN_NAME));	
			 break;
		case ReportConst.INV_CANC_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_MIN_NAME));
			break;
		case ReportConst.YEP_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_MIN_NAME));	
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_MIN_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_MIN_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlCriteriaView(true);
			//rptGenBean.setFlScoringView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_MIN_NAME));	
			break;
		case ReportConst.CON_REQ_RPT_CEN_PEN_VAL:	
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);		
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_RPT_CEN_PEN_VAL:			
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlStatusView(true);		
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL:
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlContractRequestView(true);
			rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.LATE_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlContractTypeView(true);
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlItemTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_CEN_PEN_NAME));	
			break;
			
			
			
		case ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlContractNoView(true);
			//rptGenBean.setFlSstView(true);
			//rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SHP_INSTR_RPT_CEN_PEN_VAL:
			/*rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMtoContractView(true);
			rptGenBean.setFromDateView(true);*/
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SHP_INSTR_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_VAL:
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlItemTypeView(true);
			rptGenBean.setFlContractTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_NAME));	
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlContractOrderView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.DED_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDeductionTypeView(true);
			rptGenBean.setFlPmoView(true);
			rptGenBean.setFlPkpNoView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DED_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_REQ_CANC_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlCrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_REQ_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.CON_ORD_CANC_RPT_CEN_PEN_VAL:
			//rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.CON_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.INV_CANC_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_CEN_PEN_NAME));	
			break;
		case ReportConst.YEP_RPT_CEN_PEN_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlDocumentTypeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AUTO_CMT_CON_ORD_RPT_CEN_PEN_NAME));
			break;
		case ReportConst.SUP_PERF_RPT_CEN_PEN_VAL:
			rptGenBean.setFlSupplierView(true);
			/*rptGenBean.setFlCriteriaView(true);
			rptGenBean.setFlScoringView(true);
*/			rptGenBean.setFlPtjView(true);
			/*rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);*/			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_CEN_PEN_NAME));		
			break;
		case ReportConst.PUR_REQ_RPT_PO_VAL:	
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_RPT_PO_NAME));	
			break;
		case ReportConst.PUR_ORD_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlApproverNameView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_RPT_PO_NAME));
			break;
		case ReportConst.EMR_PROCUR_RPT_PO_VAL:
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlPoAmountView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.EMR_PROCUR_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFlDurationView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.DEL_ORD_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlDeliveryOrderNoView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_RPT_PO_NAME));
			break;
		case ReportConst.FL_REC_NOTE_RPT_PO_VAL:
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlRecNoteNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_RPT_PO_NAME));
			break;
		case ReportConst.LATE_DEL_RPT_PO_VAL:
			//rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.LATE_DEL_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_REJ_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlDeliveryAddressView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlItemNameView(true);
			rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_REJ_RPT_PO_NAME));	
			break;
		case ReportConst.STP_INSTR_DEL_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.STP_INSTR_DEL_RPT_PO_NAME));
			break;
		case ReportConst.INV_RPT_VIEW_SUPL_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true); 
			rptGenBean.setFlStatusView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_SUPL_PO_NAME));		
			break;
		case ReportConst.INV_RPT_VIEW_PTJ_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_RPT_VIEW_PTJ_PO_NAME));	
			break;
		case ReportConst.PYMT_MATCH_RPT_PO_VAL:
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlInvoiceNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPurchaseOrderNoView(true);
			rptGenBean.setFlStatusView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PYMT_MATCH_RPT_PO_NAME));		
			break;
		case ReportConst.PUR_REQ_CANC_RPT_PO_VAL:
			//rptGenBean.setFlPrNoView(true);
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlRequisitionerView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			rptGenBean.setToDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_REQ_CANC_RPT_PO_NAME));
			break;
		case ReportConst.PUR_ORD_CANC_RPT_PO_VAL:
			//rptGenBean.setFlApproverNameView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.PUR_ORD_CANC_RPT_PO_NAME));
			break;
		case ReportConst.DEL_ORD_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlRecievingOfficerView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.DEL_ORD_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.FL_REC_NOTE_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlDeliveryAddressView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlContractNoView(true);	
			rptGenBean.setFlSstView(true);
			//rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);			
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.FL_REC_NOTE_CANC_RPT_PO_NAME));
			break;
		case ReportConst.INV_CANC_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);			
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.INV_CANC_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_RPT_PO_VAL:
			rptGenBean.setFlDocumentTypeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_RPT_PO_NAME));	
			break;
		case ReportConst.YEP_AP58A_RPT_PO_VAL:
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.YEP_AP58A_RPT_PO_NAME));	
			break;
		case ReportConst.SUP_PERF_RPT_PO_VAL:
			rptGenBean.setFlSupplierView(true);
			//rptGenBean.setFlCriteriaView(true);
			//rptGenBean.setFlScoringView(true);
			rptGenBean.setFlPtjView(true);
			//rptGenBean.setFlMinistryView(true);
			//rptGenBean.setFlJabatanView(true);
			//rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUP_PERF_RPT_PO_NAME));	
			break;
		case ReportConst.SUMM_RPT_PAN_ITM_VAL:
			//rptGenBean.setFlItemNameView(true);
			//rptGenBean.setFlItemCodeView(true);
			//rptGenBean.setFlDurationView(true);
			rptGenBean.setFlPtjView(true);
			rptGenBean.setFlPkpNoView(true);
			rptGenBean.setFlSupplierView(true);
			rptGenBean.setFlMinistryView(true);
			rptGenBean.setFlStatusView(true);
			//rptGenBean.setFlJabatanView(true);
			rptGenBean.setFromDateView(true);
			//getManagedBean().setReportName(CommonMethod.getMessageCommon(ReportConst.SUMM_RPT_PAN_ITM_NAME));	
			break;
		default:            
            break;
		}		
	}
	/*
	 *Generate FL module report 
	 */
	public String generateFLReport(){	
		
		if(((this.getFlPtj()!=null)&&(this.getFlPtj()!=ReportConst.EMPTY_VAL))||((this.getFlContractNo()!=null)&&(this.getFlContractNo()!=ReportConst.EMPTY_VAL))||((this.getFlSst()!=null)&&(this.getFlSst()!=ReportConst.EMPTY_VAL))||((this.getFlJabatan()!=null)&&(this.getFlJabatan()!=ReportConst.EMPTY_VAL))||((this.getFlMinistry()!=null)&&(this.getFlMinistry()!=ReportConst.EMPTY_VAL))||((this.getFlSupplier()!=null)&&(this.getFlSupplier()!=ReportConst.EMPTY_VAL))||((this.getFlStatus()!=null)&&(this.getFlStatus()!=ReportConst.EMPTY_VAL))||((this.getFlDuration()!=null)&&(this.getFlDuration()!=ReportConst.EMPTY_VAL))||((this.getFlRecievingOfficer()!=null)&&(this.getFlRecievingOfficer()!=ReportConst.EMPTY_VAL))||((this.getFlDeliveryAddress()!=null)&&(this.getFlDeliveryAddress()!=ReportConst.EMPTY_VAL))
			||((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL))||((this.getFlContractType()!=null)&&(this.getFlContractType()!=ReportConst.EMPTY_VAL))||((this.getFlRequisitioner()!=null)&&(this.getFlRequisitioner()!=ReportConst.EMPTY_VAL))||((this.getFlMtoContract()!=null)&&(this.getFlMtoContract()!=ReportConst.EMPTY_VAL))||((this.getFlInvoiceNo()!=null)&&(this.getFlInvoiceNo()!=ReportConst.EMPTY_VAL))||((this.getFlDeductionType()!=null)&&(this.getFlDeductionType()!=ReportConst.EMPTY_VAL))||((this.getFlCrNo()!=null)&&(this.getFlCrNo()!=ReportConst.EMPTY_VAL))||((this.getFlApproverName()!=null)&&(this.getFlApproverName()!=ReportConst.EMPTY_VAL))||((this.getFlDocumentType()!=null)&&(this.getFlDocumentType()!=ReportConst.EMPTY_VAL))||((this.getFlCriteria()!=null)&&(this.getFlCriteria()!=ReportConst.EMPTY_VAL))||((this.getFlScoring()!=null)&&(this.getFlScoring()!=ReportConst.EMPTY_VAL))||((this.getFlPkpNo()!=null)&&(this.getFlPkpNo()!=ReportConst.EMPTY_VAL))||((this.getFlDeliveryOrderNo()!=null)&&(this.getFlDeliveryOrderNo()!=ReportConst.EMPTY_VAL))||((this.getFlItemCode()!=null)&&(this.getFlItemCode()!=ReportConst.EMPTY_VAL))||((this.getFlPoAmount()!=null)&&(this.getFlPoAmount()!=ReportConst.EMPTY_VAL))||((this.getFlPrNo()!=null)&&(this.getFlPrNo()!=ReportConst.EMPTY_VAL))||((this.getFlPmo()!=null)&&(this.getFlPmo()!=ReportConst.EMPTY_VAL))
			||((this.getFlItemName()!=null)&&(this.getFlItemName()!=ReportConst.EMPTY_VAL))||((this.getFlItmType()!=null)&&(this.getFlItmType()!=ReportConst.EMPTY_VAL))||((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL))||((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL))){		
			if((this.getFlPtj()!=null)&&(this.getFlPtj()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPtjTotVar(ReportConst.AMP_PTJ_EQ+this.getFlPtj());
			}
			if((this.getFlContractNo()!=null)&&(this.getFlContractNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setContractNoTotVar(ReportConst.AMP_CONTR_NO_EQ+this.getFlContractNo());
			}
			if((this.getFlSst()!=null)&&(this.getFlSst()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSstTotVar(ReportConst.AMP_SST_EQ+this.getFlSst());
			}
			if((this.getFlJabatan()!=null)&&(this.getFlJabatan()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setJabatanTotVar(ReportConst.AMP_JABATAN_EQ+this.getFlJabatan());
			}
			if((this.getFlMinistry()!=null)&&(this.getFlMinistry()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMinistryTotVar(ReportConst.AMP_MINISTRY_EQ+this.getFlMinistry());
			}
			if((this.getFlSupplier()!=null)&&(this.getFlSupplier()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setSupplierTotVar(ReportConst.AMP_SUPPLIER_EQ+this.getFlSupplier());
			}
			if((this.getFlStatus()!=null)&&(this.getFlStatus()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setStatusTotVar(ReportConst.AMP_STATUS_EQ+this.getFlStatus());
			}
			if((this.getFlDuration()!=null)&&(this.getFlDuration()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDurationTotVar(ReportConst.AMP_DURATION_EQ+this.getFlDuration());
			}
			if((this.getFlRecievingOfficer()!=null)&&(this.getFlRecievingOfficer()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRecievingOfficerTotVar(ReportConst.AMP_REC_OFFCR_EQ+this.getFlRecievingOfficer());
			}
			if((this.getFlDeliveryAddress()!=null)&&(this.getFlDeliveryAddress()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDeliveryAddressTotVar(ReportConst.AMP_DEL_ADDR_EQ+this.getFlDeliveryAddress());
			}
			if((this.getFlRecNoteNo()!=null)&&(this.getFlRecNoteNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setFlRecNoteNoTotVar(ReportConst.AMP_FL_REC_NOTE_NO_EQ+this.getFlRecNoteNo());
			}
			if((this.getFlContractType()!=null)&&(this.getFlContractType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setContractTypeTotVar(ReportConst.AMP_CONTR_TYPE_EQ+this.getFlContractType());
			}
			if((this.getFlRequisitioner()!=null)&&(this.getFlRequisitioner()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setRequisitionerTotVar(ReportConst.AMP_REQSNR_EQ+this.getFlRequisitioner());
			}
			if((this.getFlMtoContract()!=null)&&(this.getFlMtoContract()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setMtoContractTotVar(ReportConst.AMP_MTO_CONTR_EQ+this.getFlMtoContract());
			}
			if((this.getFlInvoiceNo()!=null)&&(this.getFlInvoiceNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setInvoiceNoTotVar(ReportConst.AMP_INVOICE_NO_EQ+this.getFlInvoiceNo());
			}
			if((this.getFlDeductionType()!=null)&&(this.getFlDeductionType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDeductionTypeTotVar(ReportConst.AMP_DEDUCT_TYPE_EQ+this.getFlDeductionType());
			}
			if((this.getFlCrNo()!=null)&&(this.getFlCrNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCrNoTotVar(ReportConst.AMP_CR_NO_EQ+this.getFlCrNo());
			}
			if((this.getFlApproverName()!=null)&&(this.getFlApproverName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setApproverNameTotVar(ReportConst.AMP_APPROVER_NAME_EQ+this.getFlApproverName());
			}
			if((this.getFlDocumentType()!=null)&&(this.getFlDocumentType()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDocumentTypeTotVar(ReportConst.AMP_DOC_TYPE_EQ+this.getFlDocumentType());
			}
			if((this.getFlCriteria()!=null)&&(this.getFlCriteria()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setCriteriaTotVar(ReportConst.AMP_CRITERIA_EQ+this.getFlCriteria());
			}
			if((this.getFlScoring()!=null)&&(this.getFlScoring()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setScoringTotVar(ReportConst.AMP_SCORING_EQ+this.getFlScoring());
			}
			if((this.getFlPkpNo()!=null)&&(this.getFlPkpNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPkpNoTotVar(ReportConst.AMP_PKP_NO_EQ+this.getFlPkpNo());
			}
			if((this.getFlDeliveryOrderNo()!=null)&&(this.getFlDeliveryOrderNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setDeliveryOrderNoTotVar(ReportConst.AMP_DEL_ORDER_NO_EQ+this.getFlDeliveryOrderNo());
			}
			if((this.getFlItemCode()!=null)&&(this.getFlItemCode()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemCodeTotVar(ReportConst.AMP_ITM_CODE_EQ+this.getFlItemCode());
			}
			if((this.getFlPoAmount()!=null)&&(this.getFlPoAmount()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPoAmountTotVar(ReportConst.AMP_PO_AMT_EQ+this.getFlPoAmount());
			}
			if((this.getFlPrNo()!=null)&&(this.getFlPrNo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPrNoTotVar(ReportConst.AMP_PR_NO_EQ+this.getFlPrNo());
			}
			if((this.getFlPmo()!=null)&&(this.getFlPmo()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setPmoTotVar(ReportConst.AMP_PMO_EQ+this.getFlPmo());
			}
			//-----			
			if((this.getFlItemName()!=null)&&(this.getFlItemName()!=ReportConst.EMPTY_VAL)){
				rptGenerateBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ+this.getFlItemName());
			}			
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(ReportConst.URL_RPT_DATE_FORMAT);
			if((this.getFromDate()!=null)&&(this.getFromDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setFromDateVar(ReportConst.AMP_FROM_DATE_EQ+outputDateFormat.format(this.getFromDate()));
			}
			if((this.getToDate()!=null)&&(this.getToDate().toString()!=ReportConst.EMPTY_VAL)){				 
				rptGenerateBean.setToDateVar(ReportConst.AMP_TO_DATE_EQ+outputDateFormat.format(this.getToDate()));
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
			rptGenerateBean.setUrlReportJSPName(urlFLReportNameJsps.get(getManagedBean().getReportValue().trim()));			
			if((rptGenerateBean.getUrlReportJSPName()!=null)&&(rptGenerateBean.getUrlReportJSPName()!=ReportConst.EMPTY_VAL)){
				//String url=ReportConst.EXC_PREV_URL+ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionCodeTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getProofIdentityTotVar()+rptGenerateBean.getPtjTotVar()+rptGenerateBean.getContractNoTotVar()+rptGenerateBean.getSstTotVar()+rptGenerateBean.getJabatanTotVar()+rptGenerateBean.getMinistryTotVar()+rptGenerateBean.getSupplierTotVar()+rptGenerateBean.getStatusTotVar()+rptGenerateBean.getDurationTotVar()+rptGenerateBean.getRecievingOfficerTotVar()+rptGenerateBean.getDeliveryAddressTotVar()+rptGenerateBean.getFlRecNoteNoTotVar()+rptGenerateBean.getContractTypeTotVar()+rptGenerateBean.getRequisitionerTotVar()+rptGenerateBean.getMtoContractTotVar()+rptGenerateBean.getInvoiceNoTotVar()+rptGenerateBean.getDeductionTypeTotVar()+rptGenerateBean.getCrNoTotVar()+rptGenerateBean.getApproverNameTotVar()+rptGenerateBean.getDocumentTypeTotVar()+rptGenerateBean.getCriteriaTotVar()+rptGenerateBean.getScoringTotVar()+rptGenerateBean.getPkpNoTotVar()+rptGenerateBean.getDeliveryOrderNoTotVar()+rptGenerateBean.getItemCodeTotVar()+rptGenerateBean.getPoAmountTotVar()+rptGenerateBean.getPrNoTotVar()+rptGenerateBean.getPmoTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar()+ReportConst.EXC_NEXT_URL;				
				//setGeneratedURL(url);
				setConstructedParamString(ReportConst.AMP_REPORT_NAME+rptGenerateBean.getUrlReportJSPName()+rptGenerateBean.getItemNameTotVar()+rptGenerateBean.getItemTypeTotVar()+rptGenerateBean.getItemStatusTotVar()+rptGenerateBean.getUnspscCodeTotVar()+rptGenerateBean.getUnspscTitleTotVar()+rptGenerateBean.getExtentionCodeTotVar()+rptGenerateBean.getCompanyNameTotVar()+rptGenerateBean.getRegistrationTypeTotVar()+rptGenerateBean.getItemCategoryTotVar()+rptGenerateBean.getProofIdentityTotVar()+rptGenerateBean.getPtjTotVar()+rptGenerateBean.getContractNoTotVar()+rptGenerateBean.getSstTotVar()+rptGenerateBean.getJabatanTotVar()+rptGenerateBean.getMinistryTotVar()+rptGenerateBean.getSupplierTotVar()+rptGenerateBean.getStatusTotVar()+rptGenerateBean.getDurationTotVar()+rptGenerateBean.getRecievingOfficerTotVar()+rptGenerateBean.getDeliveryAddressTotVar()+rptGenerateBean.getFlRecNoteNoTotVar()+rptGenerateBean.getContractTypeTotVar()+rptGenerateBean.getRequisitionerTotVar()+rptGenerateBean.getMtoContractTotVar()+rptGenerateBean.getInvoiceNoTotVar()+rptGenerateBean.getDeductionTypeTotVar()+rptGenerateBean.getCrNoTotVar()+rptGenerateBean.getApproverNameTotVar()+rptGenerateBean.getDocumentTypeTotVar()+rptGenerateBean.getCriteriaTotVar()+rptGenerateBean.getScoringTotVar()+rptGenerateBean.getPkpNoTotVar()+rptGenerateBean.getDeliveryOrderNoTotVar()+rptGenerateBean.getItemCodeTotVar()+rptGenerateBean.getPoAmountTotVar()+rptGenerateBean.getPrNoTotVar()+rptGenerateBean.getPmoTotVar()+rptGenerateBean.getFromDateVar()+rptGenerateBean.getToDateVar());				
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
		return getConstructedParamString();
	}
}
