package my.ep.web.backingbean.bpm.errorlist;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class EpNamespacePrefixMapper extends NamespacePrefixMapper {
	
	private static Map<String, String> namesapceMap = null;

	@Override
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		String prefix = "";
		if (null == namesapceMap) {
			initEpNameSapces();
		}
		for (Entry<String, String> namespaceEntry : namesapceMap.entrySet()) {
			if (namespaceEntry.getKey().equals(namespaceUri)) {
				prefix = namespaceEntry.getValue();
				break;
			}
		}
		return prefix;
	}

	/**
	 * 
	 */
	public static void initEpNameSapces() {
		namesapceMap = new HashMap<String, String>();
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Order/FL_Order_Data","order");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/FL_User_Data","user");
		//namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/DoFulfillment/FL_DeliveryOrder_Data", "do");
		
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Common/FL_ChargeLine_Item","lineItem");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Common/FL_Bank_Details_Data", "bank");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Common/FL_Factoring_Company_Data", "fc");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Common/FL_Item_Data", "item");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/InvoicePayment/FL_Deduction_Penalty_Data", "penalty");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/InvoicePayment/FL_Payment_Data", "payment");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Cancel/FL_Order_Cancel_Req", "cancelOrder");
		namesapceMap.put("http://xmlns.oracle.com/bpm/bpmobject/Data/Cancel/FL_DO_Fulfillment_Cancel_Req", "cancelDO");
	}

}