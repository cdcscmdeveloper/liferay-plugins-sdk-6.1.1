package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;

public class RequestListHelper {
	public	static	List<ScRequestSupplierItem>	getRecommendRequestSupplierItems(ScRequestList requestList){
		List<ScRequestSupplierItem>	results	=	new ArrayList<ScRequestSupplierItem>();
		for(ScRequestListItem requestListItem: requestList.getRequestItems()){
			for(ScRequestSupplierItem supplierItem: requestListItem.getSupplierItems()){
				if(supplierItem.getIsRecommended()){
					results.add(supplierItem);
				}
			}
		}
		return results;
	}
	
	public	static	boolean	isMultipleRecommend(List<ScRequestListItem> requestListItems){
		int numberOfSelectedItem	= 0;
		for(ScRequestListItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				numberOfSelectedItem = numberOfSelectedItem + 1;
			}
		}
		if(numberOfSelectedItem >=2){
			return true;
		}else{
			return false;
		}
	}
	
	public	static	Collection<SmSupplier>	getRecommendSupplier(List<ScRequestListItem> requestListItems){
		Map<Long,SmSupplier>	uniqueSmSuppliers	=	new HashMap<Long,SmSupplier>();
		for(ScRequestListItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				for(ScRequestSupplierItem requestSupplierItem: requestListItem.getSupplierItems()){
					uniqueSmSuppliers.put(requestSupplierItem.getSmSupplier().getSupplierId(), requestSupplierItem.getSmSupplier());
				}
			}
		}
		return uniqueSmSuppliers.values();
	}
	public static	List<ScRequestListItem>		getUniqueItemList(List<CmSupplierItem> supplierItems){
		List<ScRequestListItem>	results	=	new ArrayList<ScRequestListItem>();
		Map<CmItem, List<CmSupplierItem>>	uniqueItemList	=	new HashMap<CmItem, List<CmSupplierItem>>();
		
		for(CmSupplierItem supplierItem: supplierItems){
			if(uniqueItemList.get(supplierItem.getCmItem()) != null){
				uniqueItemList.get(supplierItem.getCmItem()).add(supplierItem);
			}else{
				List<CmSupplierItem>	supplierItemList	=	new ArrayList<CmSupplierItem>();
				supplierItemList.add(supplierItem);
				uniqueItemList.put(supplierItem.getCmItem(), supplierItemList);
			}
		}
		Set<CmItem>	cmItems	=	uniqueItemList.keySet();
		for(CmItem cmItem: cmItems){
			List<CmSupplierItem>	supplierItemList	=	uniqueItemList.get(cmItem);
			ScRequestListItem	requestListItem	=	RequestListItemHelper.createRequestListItem(cmItem);
			requestListItem.setSupplierItems(RequestSupplierItemHelper.createRequestSupplierItems(requestListItem, supplierItemList));
			results.add(requestListItem);
		}
		return results;
	}
}
