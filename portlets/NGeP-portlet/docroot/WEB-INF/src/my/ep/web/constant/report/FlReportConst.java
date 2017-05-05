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
	public static final String FL_001_CONTRACT_REQ = "FL_001";
	
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
	
	public FlReportConst(){}

	public String getFl001ContractReq() {
		return FL_001_CONTRACT_REQ;
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
