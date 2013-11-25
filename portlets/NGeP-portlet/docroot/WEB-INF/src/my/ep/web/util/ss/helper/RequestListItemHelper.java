package my.ep.web.util.ss.helper;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;

public class RequestListItemHelper {
	public	static	ScRequestListItem	createRequestListItem(CmItem cmItem){
		ScRequestListItem result	=	new ScRequestListItem();
		result.setCmItem(cmItem);
		result.setItemName(cmItem.getItemName());
		result.setItemDesc(cmItem.getItemDesc());
		//result.setItemType(cmItem.getCmType());
		result.setUom(cmItem.getCmMeasurement().getMeasurementCode());
		result.setIsQuoteBidItem(Boolean.FALSE);
		return result;
	}
}
