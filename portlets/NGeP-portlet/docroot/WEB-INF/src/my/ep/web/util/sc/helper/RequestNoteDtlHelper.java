package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;

public class RequestNoteDtlHelper {
	public static	ScRequestNoteDtl	createRequestNoteDtl(ScRequestSupplierItem supplierItem){
		ScRequestNoteDtl	result =	new ScRequestNoteDtl();
		result.setScRequestSupplierItem(supplierItem);
		return result;
	}
	public static List<ScRequestNoteDtl>	createRequestNoteDtls(List<ScRequestSupplierItem> supplierItems){
		List<ScRequestNoteDtl>	results	=	new ArrayList<ScRequestNoteDtl>();
		for(ScRequestSupplierItem supplierItem: supplierItems){
			results.add(createRequestNoteDtl(supplierItem));
		}
		return results;
	}
}
