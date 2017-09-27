package my.ep.web.backingbean.bpm.errorlist;

import java.util.HashMap;
import java.util.Map;

import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.dandata.FLDanDataType;
import my.ep.jaxb.fl.dodata.FLDeliveryOrderDataType;
import my.ep.jaxb.fl.dodata.list.FLDeliveryOrderListDataType;
import my.ep.jaxb.fl.fc.FLFactoringRemoveReqType;
import my.ep.jaxb.fl.frndata.FLFulFillmentReceivedNoteDataType;
import my.ep.jaxb.fl.invoice.FLInvoiceDataType;
import my.ep.jaxb.fl.maintenace.ap59.codata.FLCOCloseReqDataType;
import my.ep.jaxb.fl.maintenace.cancel.codata.FLCOCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.common.order.FLOrderCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.dofulfillment.FLDOFulfillmentCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.frn.FLFRNCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.invoice.FLInvoiceCancelReqType;
import my.ep.jaxb.fl.maintenace.cancel.podata.FLPOCancelReqType;
import my.ep.jaxb.fl.maintenace.modification.dofulfillment.FLDOFulfillmentModificationReqType;
import my.ep.jaxb.fl.maintenace.modification.invoice.FLInvoiceModificationReqType;
import my.ep.jaxb.fl.payment.instruction.FLPaymentInstructionDataType;
import my.ep.jaxb.fl.phis.codata.FLPHISContractOrderDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;

public class BidirectionMap {

	@SuppressWarnings("rawtypes")
	private HashMap<Class,String>mapClass = new HashMap<Class,String>();
	@SuppressWarnings("rawtypes")
	private HashMap<String,Class>mapValue = new HashMap<String,Class>();
	
	@SuppressWarnings("rawtypes")
	public void put(Class clazz, String key) {
		mapClass.put(clazz, key);
		mapValue.put(key, clazz);
	}
	
	@SuppressWarnings("rawtypes")
	public Class getClass(String key) {
		return mapValue.get(key);
	}
	
	@SuppressWarnings("rawtypes")
	public String getValue(Class clazz) {
		return mapClass.get(clazz);
	}
	
	@SuppressWarnings("rawtypes")
	public String getQName(Class clazz){
		if(null==qNameMap){
			initQnames();
		}
		String qName=qNameMap.get(clazz);
		if(null==qName){
			qName=mapClass.get(clazz);
		}
		return qName;
	}
	@SuppressWarnings("rawtypes")
	private static Map<Class,String> qNameMap=null;
	
	/**
	 * Method, for Initializing the Qualified Name Space.
	 * Add all the schemas which have different folders other than Data.
	 */
	@SuppressWarnings("rawtypes")
	private static void initQnames(){
			qNameMap=new HashMap<Class, String>();
		//Order Fulfillment
				qNameMap.put(FLContractOrderDataType.class, "Order/FL_ContractOrder_Data");
				qNameMap.put(FLDeliveryOrderDataType.class, "DoFulfillment/FL_DeliveryOrder_Data");
				qNameMap.put(FLDeliveryOrderListDataType.class, "DoFulfillment/FL_Delivery_Order_List_Data");
				qNameMap.put(FLPurchaseOrderDataType.class, "Order/FL_PurchaseOrder_Data");
				qNameMap.put(FLDanDataType.class, "Dan/FL_Dan_Data");
				qNameMap.put(FLFulFillmentReceivedNoteDataType.class, "DoFulfillment/FL_FulFillment_Received_Note_Data");
				qNameMap.put(FLInvoiceDataType.class, "InvoicePayment/FL_Invoice_Data");
				qNameMap.put(FLPaymentInstructionDataType.class, "InvoicePayment/FL_Payment_Instruction_Data");
				qNameMap.put(FLStopFullfillmentDataType.class, "StopFulfillment/FL_StopFullfillment_Data");
				//Modification
				qNameMap.put(FLDOFulfillmentModificationReqType.class, "Modification/FL_DOFulfillment_Modification_Req");
				qNameMap.put(FLInvoiceModificationReqType.class, "Modification/FL_Invoice_Modification_Req");
				//Cancellation
				qNameMap.put(FLDOFulfillmentCancelReqType.class, "Cancel/FL_DO_Fulfillment_Cancel_Req");
				qNameMap.put(FLFRNCancelReqType.class, "Cancel/FL_FRN_Cancel_Req");
				qNameMap.put(FLInvoiceCancelReqType.class, "Cancel/FL_Invoice_Cancel_Req");
				qNameMap.put(FLPOCancelReqType.class, "Cancel/FL_PO_Cancel_Req");
				qNameMap.put(FLCOCancelReqType.class, "Cancel/FL_CO_Cancel_Req");
				qNameMap.put(FLOrderCancelReqType.class, "Cancel/FL_Order_Cancel_Req");
				//Remove Factoring
				qNameMap.put(FLFactoringRemoveReqType.class, "FC/FL_Factoring_Remove_Req");
				qNameMap.put(FLPHISContractOrderDataType.class, "Order/FL_PHIS_ContractOrder_Data");
				qNameMap.put(FLCOCloseReqDataType.class, "AP59/FL_CO_Close_Req_Data");
				
				
	}
}
