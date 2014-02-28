package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;

public class RequestSupplierItemHelper {
	public static	ScRequestSupplierItem	createRequestSupplierItem(CmSupplierItem supplierItem){
		ScRequestSupplierItem	result	=	new ScRequestSupplierItem();
		if(supplierItem.getMinOrder() != null){
			result.setMinOrderQty(supplierItem.getMinOrder().intValue());
		}
		//TODO set offer price from pricings
		result.setSmSupplier(supplierItem.getSmSupplier());
		return result;
	}
	public	static	List<ScRequestSupplierItem>	createRequestSupplierItems(List<CmSupplierItem> supplierItems){
		List<ScRequestSupplierItem>	results = new ArrayList<>();
		for(CmSupplierItem supplierItem: supplierItems){
			results.add(createRequestSupplierItem(supplierItem));
		}
		return results;
	}
	public	static	List<ScRequestSupplierItem>	createRequestSupplierItems(ScRequestItem requestListItem, List<CmSupplierItem> supplierItems){
		List<ScRequestSupplierItem>	results = new ArrayList<>();
		for(CmSupplierItem supplierItem: supplierItems){
			ScRequestSupplierItem	requestSupplierItem	=	createRequestSupplierItem(supplierItem);
			requestSupplierItem.setRequestNoteItem(requestListItem);
			results.add(requestSupplierItem);
		}
		return results;
	}
}
