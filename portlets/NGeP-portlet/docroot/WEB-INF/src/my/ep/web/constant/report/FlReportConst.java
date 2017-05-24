package my.ep.web.constant.report;

import java.io.Serializable;

public class FlReportConst implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String PARAM_FL_SUPP_ZONE_EQ = "PSUPPZONE";
	public static final String PARAM_FL_SUPP_PROFILE_STATUS_EQ = "PPRSTAT";
	public static final String PARAM_FL_USER_ID_EQ = "PUSER";
	public static final String PARAM_FL_REPORT_ID_EQ = "PREPORTID";
	
	// Report Parameter Key
	public static final String PARAM_KEY_REPORT = "report";
	public static final String PARAM_USER_ID = "PUSER";
	
	// Ministry Const
	public static final String FL_001_CONTRACT_REQUEST = "FL_001";
	public static final String FL_002_PURCHASE_ORDER = "FL_002";
	public static final String FL_003_DELIVERY_ORDER = "FL_003";
	public static final String FL_004_FULLFILLMENT_RECEIVED_NOTE = "FL_004";
	public static final String FL_005_LATE_DELIVERY = "FL_005";
	public static final String FL_009_INVOICE = "FL_009";
	public static final String FL_010_PAYMENT_MATCH = "FL_010";
	public static final String FL_011_DEDUCTION = "FL_011";
	public static final String FL_012_CONTRACT_REQUEST_REJECTION = "FL_012";
	public static final String FL_013_PURCHASE_ORDER_CANCELLATION = "FL_013";
	public static final String FL_014_DELIVERY_ORDER_CANCELLATION = "FL_014";
	public static final String FL_015_FULLFILLMENT_RECEIVED_NOTE_CANCELLATION = "FL_015";
	public static final String FL_016_INVOICE_CANCELLATION = "FL_016";
	public static final String FL_050_STOP_INSTRUCTION_INITIATED_BY_PTJ = "FL_050";
	public static final String FL_053_STOP_INSTRUCTION_INITIATED_BY_SUPPLIER = "FL_053";
	
	// Central Contract Const
	public static final String FL_018_CONTRACT_REQ = "FL_018";
	public static final String FL_019_PURCHASE_ORDER = "FL_019";
	public static final String FL_020_DELIVERY_ORDER = "FL_020";
	public static final String FL_021_FULFILMENT_RECEIVED_NOTE = "FL_021";
	public static final String FL_022_LATE_DELIVERY = "FL_022";
	public static final String FL_025_INVOICE = "FL_025";
	public static final String FL_026_PAYMENT_MATCH = "FL_026";
	public static final String FL_027_CONTRACT_REQ_REJECTION = "FL_027";
	public static final String FL_028_PURCHASE_ORDER_CANCELLATION = "FL_028";
	public static final String FL_029_DELIVERY_ORDER_CANCELLATION = "FL_029";
	public static final String FL_030_FULFILMENT_RECEIVED_NOTE_CANCELLATION = "FL_030";
	public static final String FL_031_INVOICE_CANCELLATION = "FL_031";
	public static final String FL_051_STOP_INST_BY_PTJ = "FL_051";
	public static final String FL_054_STOP_INST_BY_SUPPLIER = "FL_054";
	
	// Purchase Order Const
	
	
	public static final String FL_RPT_FROM_DATE = "PFROMDATE";
	public static final String FL_RPT_TO_DATE = "PTODATE";
	public static final String FL_RPT_COTRACT_ORDER = "PCONO";
	public static final String FL_RPT_COTRACT_REQUEST = "PCRNO";
	public static final String FL_RPT_PURCHASE_ORDER = "PPONO";
	public static final String FL_RPT_TREASURY_CIRCULAR = "PKPNO";//Physical Treasury Circular (1PP) No.
	public static final String FL_RPT_FULLFILLEMENT_RECEIVE = "FRNNO";
	public static final String FL_RPT_COTRACT_TYPE = "PCONTYPE";
	public static final String FL_RPT_DELI_ADDR = "PDOADD";
	public static final String FL_RPT_DELI_ORD_NO = "PDONO";
	public static final String FL_RPT_SUP_DELI_ORD_NO = "PSUPPDONO";
	public static final String FL_RPT_PKP_NO = "PPKPNO";
	public static final String FL_RPT_FRN_NO = "PFRNNO";
	public static final String FL_RPT_REC_OFF = "PRECOFF";
	public static final String FL_RPT_STATUS = "PSTATUS";
	public static final String FL_RPT_APPROVER_NAME = "PAPPROVER";
	public static final String FL_RPT_REQUI_NAME = "PREQUEST";
	public static final String FL_RPT_ACKNOWLEDGE_NAME = "PACKOFF";
	public static final String FL_RPT_PAYMENT_NAME = "PPAYOFF";
	public static final String FL_RPT_PHY_CONTRACT_NO = "PPHYCON";
	public static final String FL_RPT_PHY_LOA_NO = "PPHYLOA";
	public static final String FL_RPT_CONTRACT_NO = "PCRNO";	
	public static final String FL_RPT_SUPPL_NAME = "PSUPP";
	public static final String FL_RPT_MINISTRY = "PMINIS";
	public static final String FL_RPT_PEGAWAI_PENGAWAI = "PFPP";
	public static final String FL_RPT_KUMPULAN = "PFJA";
	public static final String FL_RPT_PTJ_NAME = "PPTJ";
	public static final String FL_RPT_SST_NO = "PLOA";//loa no
	public static final String FL_RPT_CONTRACT_REQ_NO = "PCONTRACTNO";
	public static final String FL_RPT_CC_PANEL_NAME = "PPKPNAME";//panel name
	public static final String FL_RPT_REPORT_ID = "PREPORTID";
	public static final String FL_RPT_USER_ID = "PUSER";
	public static final String FL_RPT_STOP_INSTR_NO = "PSINO";
	public static final String FL_RPT_INV_NO = "PINVNO";
	public static final String FL_RPT_SUPPL_INV_NO = "PSUPPINVNO";
	public static final String FL_RPT_PAYMENT_ADV_NO = "PPANO";
	public static final String FL_RPT_ITM_NAME = "PITEM";
	public static final String FL_RPT_DOC_TYPE = "PDOCTYPE";
	public static final String FL_RPT_GROUP_BY = "PGROUP"; 
	
	public FlReportConst(){}

	public String getFl001ContractRequest() {
		return FL_001_CONTRACT_REQUEST;
	}

	public String getFl002PurchaseOrder() {
		return FL_002_PURCHASE_ORDER;
	}
	
	public String getFl003DeliveryOrder() {
		return FL_003_DELIVERY_ORDER;
	}
	
	public String getFl004FullfillmentReceivedNote() {
		return FL_004_FULLFILLMENT_RECEIVED_NOTE;
	}
	
	public String getFl005LateDelivery(){
		return FL_005_LATE_DELIVERY;
	}
	
	public String getFl009Invoice(){
		return FL_009_INVOICE;
	}
	
	public String getFl010PaymentMatch(){
		return FL_010_PAYMENT_MATCH;
	}
	
	public String getFl011Deduction(){
		return FL_011_DEDUCTION;
	}
	
	public String getFl012ContractRequestRejection(){
		return FL_012_CONTRACT_REQUEST_REJECTION;
	}
	
	public String getFl013PurchaseOrderCancellation(){
		return FL_013_PURCHASE_ORDER_CANCELLATION;
	}
	
	public String getFl014DeliveryOrderCancellation(){
		return FL_014_DELIVERY_ORDER_CANCELLATION;
	}
	
	public String getFl015FullfillmentReceivedNoteCancellation(){
		return FL_015_FULLFILLMENT_RECEIVED_NOTE_CANCELLATION;
	}
	
	public String getFl016InvoiceCancellation(){
		return FL_016_INVOICE_CANCELLATION;
	}
	
	public String getFl050StopInstructionInitiatedByPtj(){
		return FL_050_STOP_INSTRUCTION_INITIATED_BY_PTJ;
	}
	
	public String getFl053StopInstructionInitiatedBySupplier(){
		return FL_053_STOP_INSTRUCTION_INITIATED_BY_SUPPLIER;
	}
	
	public String getFl018ContractReq() {
		return FL_018_CONTRACT_REQ;
	}

	public String getFl019PurchaseOrder() {
		return FL_019_PURCHASE_ORDER;
	}

	public String getFl020DeliveryOrder() {
		return FL_020_DELIVERY_ORDER;
	}

	public String getFl021FulfilmentReceivedNote() {
		return FL_021_FULFILMENT_RECEIVED_NOTE;
	}

	public String getFl022LateDelivery() {
		return FL_022_LATE_DELIVERY;
	}

	public String getFl025Invoice() {
		return FL_025_INVOICE;
	}

	public String getFl026PaymentMatch() {
		return FL_026_PAYMENT_MATCH;
	}

	public String getFl027ContractReqRejection() {
		return FL_027_CONTRACT_REQ_REJECTION;
	}

	public String getFl028PurchaseOrderCancellation() {
		return FL_028_PURCHASE_ORDER_CANCELLATION;
	}

	public String getFl029DeliveryOrderCancellation() {
		return FL_029_DELIVERY_ORDER_CANCELLATION;
	}

	public String getFl030FulfilmentReceivedNoteCancellation() {
		return FL_030_FULFILMENT_RECEIVED_NOTE_CANCELLATION;
	}

	public String getFl031InvoiceCancellation() {
		return FL_031_INVOICE_CANCELLATION;
	}

	public String getFl051StopInstByPtj() {
		return FL_051_STOP_INST_BY_PTJ;
	}

	public String getFl054StopInstBySupplier() {
		return FL_054_STOP_INST_BY_SUPPLIER;
	}

 }