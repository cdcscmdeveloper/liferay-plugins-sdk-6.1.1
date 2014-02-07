package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestList;
import my.ep.persistence.entity.sc.directpurchase.ScRequestListItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sm.SmSupplier;


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
	
	public	static	boolean	isMultipleRecommend(List<ScRequestItem> requestListItems){
		int numberOfSelectedItem	= 0;
		for(ScRequestItem requestListItem: requestListItems){
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
	
	public	static	List<ScRequestItem>	removeSelectedItem(List<ScRequestItem> requestListItems){
		for(ScRequestItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				requestListItems.remove(requestListItem);
			}
		}
		return requestListItems;
	}
	
	public	static	Collection<SmSupplier>	getRecommendSupplier(List<ScRequestItem> requestListItems){
		Map<Long,SmSupplier>	uniqueSmSuppliers	=	new HashMap<Long,SmSupplier>();
		for(ScRequestItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				for(ScRequestSupplierItem requestSupplierItem: requestListItem.getSupplierItems()){
					uniqueSmSuppliers.put(requestSupplierItem.getSmSupplier().getSupplierId(), requestSupplierItem.getSmSupplier());
				}
			}
		}
		return uniqueSmSuppliers.values();
	}
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
	
	public	static	PmOrgProfile	getMinistryOrgProfile(PmOrgProfile requestListOrgProfile){
		if(requestListOrgProfile != null && requestListOrgProfile.getParentOrgProfile() != null){
			return getMinistryOrgProfile(requestListOrgProfile.getParentOrgProfile());
		}else{
			return requestListOrgProfile;
		}
	}
}
