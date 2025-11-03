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
	//29550 - start
	public static final String FL_073_CR_GENERATED_AS_PO = "FL_073";
	public static final String FL_074_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT = "FL_074";
	public static final String FL_075_SUM_FOR_PO_CANCELLATION_REASON = "FL_075";
	public static final String FL_076_SUPPLIER_TRANSACTION = "FL_076";
	//29550 - end
	
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
	public static final String FL_066_DEDUCTION = "FL_066";
	//29550 - start
	public static final String FL_077_CR_GENERATED_AS_PO = "FL_077";
	public static final String FL_078_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT = "FL_078";
	public static final String FL_079_SUM_FOR_PO_CANCELLATION_REASON = "FL_079";
	public static final String FL_080_SUPPLIER_TRANSACTION = "FL_080";
	//29550 - end
	
	// Purchase Order Const
	
	public static final String FL_034_PURCHASE_REQUEST = "FL_034";
	public static final String FL_035_PURCHASE_ORDER = "FL_035";
	public static final String FL_038_PURCHASE_DELIVERY = "FL_038";
	public static final String FL_039_PURCHASE_FUL_REC_NOTE = "FL_039";
	public static final String FL_040_PURCHASE_LATE_DELIVERY = "FL_040";
	public static final String FL_043_PURCHASE_INV_PTJ = "FL_043";
	public static final String FL_044_PURCHASE_PAYMENT_MATCH = "FL_044";
	public static final String FL_045_PURCHASE_REQ_REJECTION = "FL_045";
	public static final String FL_046_PURCHASE_ORDER_CANCEL = "FL_046";
	public static final String FL_047_PURCHASE_DELI_ORD_CANCEL = "FL_047";
	public static final String FL_048_PURCHASE_FULFI_NOTE_CANCEL = "FL_048";
	public static final String FL_049_PURCHASE_INVOICE_CANCEL = "FL_049";
	public static final String FL_052_PURCHASE_STOP_INSTR_PTJ = "FL_052";
	public static final String FL_055_PURCHASE_STOP_INSTR_SUPP = "FL_055";
	//29550 - start
	public static final String FL_081_PR_GENERATED_AS_PO = "FL_081";
	public static final String FL_082_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT= "FL_082";
	public static final String FL_083_SUM_FOR_PO_CANCELLATION_REASON = "FL_083";
	public static final String FL_084_SUPPLIER_TRANSACTION = "FL_084";
	//29550 - end
	
	// Panel Const
	public static final String FL_033_SUMMARY = "FL_033";
	
	//Year End Report Const
	public static final String FL_067_YEAR_END_REPORT = "FL_067";
	
	/*29554 - Beginning - Logan*/
	// Service Report Const
	public static final String FL_071_SERVICE_FEE_REPORT_BASED_OF_ACCOUNTING_OFFICE = "FL_071";
	public static final String FL_072_ACCUMULATED_SERVICE_FEE_REPORT = "FL_072";
	//end 29554
	
	public static final String FL_RPT_FROM_DATE = "PFROMDATE";
	public static final String FL_RPT_TO_DATE = "PTODATE";
	public static final String FL_RPT_COTRACT_ORDER = "PCONO";
	public static final String FL_RPT_COTRACT_REQUEST = "PCRNO";
	public static final String FL_RPT_PURCHASE_ORDER = "PPONO";
	public static final String FL_RPT_TREASURY_CIRCULAR = "PPKPNO";//Physical Treasury Circular (1PP) No.
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
	public static final String FL_RPT_CONTRACT_NO = "PCONTRACTNO";	
	public static final String FL_RPT_SUPPL_NAME = "PSUPP";
	public static final String FL_RPT_MINISTRY = "PMINIS";
	public static final String FL_RPT_PEGAWAI_PENGAWAI = "PFPP";
	public static final String FL_RPT_KUMPULAN = "PFJA";
	public static final String FL_RPT_PTJ_NAME = "PPTJ";
	public static final String FL_RPT_SST_NO = "PLOA";//loa no
	public static final String FL_RPT_CONTRACT_REQ_NO = "PCRNO";
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
	public static final String FL_RPT_PURCHASE_REQUEST = "PPRNO";
	public static final String FL_RPT_PO_STATUS = "PSTATUS";
	public static final String FL_RPT_BC_NAME = "PSUPP";
	public static final String FL_RPT_PO_TITLE = "PTITLE";
	public static final String FL_RPT_AP58A = "PAP58A";
	public static final String FL_RPT_CARRY_FORWARD = "PCRFWD";
	public static final String FL_RPT_CMNL_PAYMENT = "PCMNLPYMT";
	public static final String FL_RPT_NA_TRNS = "PNATRNS";
	public static final String FL_RPT_FN_YEAR = "PYEAR";
	public static final String FL_RPT_REF_NO = "PREFNO";
	public static final String FL_RPT_CT_FROM_DATE = "PCTFRDATE";
	public static final String FL_RPT_CT_TO_DATE = "PCTTODATE";
	
	//29554 start
	public static final String FL_RPT_TRANSACTION_FROM_DATE = "PFROMDATE";
	public static final String FL_RPT_TRANSACTION_TO_DATE = "PTODATE";
	public static final String FL_RPT_AG_OFFICE = "POFFICECODE";
	public static final String FL_RPT_PSF_ONE_OFF = "PSFONEOFF";
	public static final String FL_RPT_PSF_PERIOD = "PSFPERIOD";
	//29554 end
	
	//29550 - Start
	public static final String FL_RPT_SUBMISSION_FROM_DATE = "PSUBFROMDATE";
	public static final String FL_RPT_SUBMISSION_TO_DATE = "PSUBTODATE";
	public static final String FL_RPT_APPROVED_FROM_DATE = "PAPPFROMDATE";
	public static final String FL_RPT_APPROVED_TO_DATE = "PAPPTODATE";
	public static final String FL_RPT_PO_GENERATED_FROM_DATE = "PPOGENFROMDATE";
	public static final String FL_RPT_PO_GENERATED_TO_DATE = "PPOGENTODATE";
	public static final String FL_RPT_BUSINESS_REG_NO = "PBUSREGNO"; //business registration no.
	public static final String FL_RPT_MOF_NO = "PMOFNO";
	public static final String FL_RPT_EP_NO = "PEPNO";
	public static final String FL_RPT_PURCHASE_GROUP_STATUS = "PGSTATUS";
	//29550 - End
	
	//#27476 start
	public static final String FL_RPT_PROCUMENT_TYPE_CODE = "PPROCTYPECODE";
	//#27476 end
	
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
	public String getFl066Deduction(){
		return FL_066_DEDUCTION;
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
		
	public String getFl034PurchaseRequest() {
		return FL_034_PURCHASE_REQUEST;
	}

	public String getFl035PurchaseOrder() {
		return FL_035_PURCHASE_ORDER;
	}

	public String getFl038PurchaseDelivery() {
		return FL_038_PURCHASE_DELIVERY;
	}

	public String getFl039PurchaseFulRecNote() {
		return FL_039_PURCHASE_FUL_REC_NOTE;
	}

	public String getFl040PurchaseLateDelivery() {
		return FL_040_PURCHASE_LATE_DELIVERY;
	}

	public String getFl043PurchaseInvPtj() {
		return FL_043_PURCHASE_INV_PTJ;
	}

	public String getFl044PurchasePaymentMatch() {
		return FL_044_PURCHASE_PAYMENT_MATCH;
	}

	public String getFl045PurchaseReqRejection() {
		return FL_045_PURCHASE_REQ_REJECTION;
	}

	public String getFl046PurchaseOrderCancel() {
		return FL_046_PURCHASE_ORDER_CANCEL;
	}

	public String getFl047PurchaseDeliOrdCancel() {
		return FL_047_PURCHASE_DELI_ORD_CANCEL;
	}

	public String getFl048PurchaseFulfiNoteCancel() {
		return FL_048_PURCHASE_FULFI_NOTE_CANCEL;
	}

	public String getFl049PurchaseInvoiceCancel() {
		return FL_049_PURCHASE_INVOICE_CANCEL;
	}

	public String getFl052PurchaseStopInstrPtj() {
		return FL_052_PURCHASE_STOP_INSTR_PTJ;
	}

	public String getFl055PurchaseStopInstrSupp() {
		return FL_055_PURCHASE_STOP_INSTR_SUPP;
	}
	
	public String getFl033Summary() {
		return FL_033_SUMMARY;
	}
	
	public String getFl067YearEndReport() {
		return FL_067_YEAR_END_REPORT;
	}

	/*29554 - Logan - Beginning*/
	public String getFl071ServiceFeeReportBasedOfAccountingOffice() {
		return FL_071_SERVICE_FEE_REPORT_BASED_OF_ACCOUNTING_OFFICE;
	}

	public String getFl072AccumulatedServiceFeeReport() {
		return FL_072_ACCUMULATED_SERVICE_FEE_REPORT;
	}
	/*Ends 29554*/

	//29550 - start
	public String getFl073CrGeneratedAsPo() {
		return FL_073_CR_GENERATED_AS_PO;
	}

	public String getFl074SumForTotalNumAndValOfPoByStat() {
		return FL_074_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT;
	}

	public String getFl075SumForPoCancellationReason() {
		return FL_075_SUM_FOR_PO_CANCELLATION_REASON;
	}

	public String getFl076SupplierTransaction() {
		return FL_076_SUPPLIER_TRANSACTION;
	}

	public String getFl077CrGeneratedAsPo() {
		return FL_077_CR_GENERATED_AS_PO;
	}

	public String getFl078SumForTotalNumAndValOfPoByStat() {
		return FL_078_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT;
	}

	public String getFl079SumForPoCancellationReason() {
		return FL_079_SUM_FOR_PO_CANCELLATION_REASON;
	}

	public String getFl080SupplierTransaction() {
		return FL_080_SUPPLIER_TRANSACTION;
	}

	public String getFl081PrGeneratedAsPo() {
		return FL_081_PR_GENERATED_AS_PO;
	}

	public String getFl082SumForTotalNumAndValOfPoByStat() {
		return FL_082_SUM_FOR_TOTAL_NUM_AND_VAL_OF_PO_BY_STAT;
	}

	public String getFl083SumForPoCancellationReason() {
		return FL_083_SUM_FOR_PO_CANCELLATION_REASON;
	}

	public String getFl084SupplierTransaction() {
		return FL_084_SUPPLIER_TRANSACTION;
	}
	//29550 - end

	public static String getFlRptPurchaseGroupStatus() {
		return FL_RPT_PURCHASE_GROUP_STATUS;
	}
	
	
 }