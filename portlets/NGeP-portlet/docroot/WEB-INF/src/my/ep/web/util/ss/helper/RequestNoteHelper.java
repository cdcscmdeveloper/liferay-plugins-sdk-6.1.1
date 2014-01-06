package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;

public class RequestNoteHelper {
	
	public static	List<ScRequestItem>		getUniqueItemList(List<CmSupplierItem> supplierItems){
		List<ScRequestItem>	results	=	new ArrayList<ScRequestItem>();
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
			ScRequestItem	requestListItem	=	RequestListItemHelper.createRequestListItem(cmItem);
			requestListItem.setSupplierItems(RequestSupplierItemHelper.createRequestSupplierItems(requestListItem, supplierItemList));
			results.add(requestListItem);
		}
		return results;
	}
	public	static	List<ScRequestItem>	mergeItem(List<ScRequestItem> requestItems1, List<ScRequestItem> requestItems2){
		List<ScRequestItem>	results	=	requestItems1;
		if(requestItems2 != null){
			for(ScRequestItem requestItem: requestItems2){
				if(!isContain(requestItems1, requestItem)){
					results.add(requestItem);
				}
			}
		}
		return results;
	}
	private	static	boolean	isContain(List<ScRequestItem> requestItems, ScRequestItem requestItem){
		boolean	result	=	false;
		if(requestItem != null && requestItems != null){
			CmItem	cmItem	=	requestItem.getCmItem();
			Long	cmItemId	=	cmItem.getItemId();
			for(ScRequestItem requestItem2: requestItems){
				if(cmItemId.equals(requestItem2.getCmItem().getItemId())){
					result	=	true;
					break;
				}
			}
		}
		return result;
	}
	private	static	List<ScRequestSupplierItem>		mergeSupplierItem(List<ScRequestSupplierItem> listSupplierItems1, List<ScRequestSupplierItem> listSupplierItems2){
		List<ScRequestSupplierItem>	results	=	listSupplierItems1;
		if(listSupplierItems2 != null){
			for(ScRequestSupplierItem supplierItem: listSupplierItems2){
				if(!isContain(listSupplierItems1, supplierItem)){
					results.add(supplierItem);
				}
			}
		}
		return results;
	}
	private	static	boolean	isContain(List<ScRequestSupplierItem> supplierItems, ScRequestSupplierItem supplierItem){
		boolean	result	=	false;
		if(supplierItem != null && supplierItems != null){
			CmSupplierItem	cmSupplierItem	=	supplierItem.getCmSupplierItem();
			Long	supplierItemId = cmSupplierItem.getSupplierItemId();
			for(ScRequestSupplierItem supplierItem2: supplierItems){
				if(supplierItemId.equals(Long.valueOf(supplierItem2.getCmSupplierItem().getSupplierItemId()))){
					result	=	true;
					break;
				}
			}
		}
		return result;
	}
}
