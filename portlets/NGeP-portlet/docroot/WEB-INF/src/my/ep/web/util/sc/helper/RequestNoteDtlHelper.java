/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.util.sc.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestNoteDtlHelper.
 * @author sri
 * @since 27.02.2014
 */
public class RequestNoteDtlHelper {
	
	/**
	 * Creates the request note dtl.
	 *
	 * @param supplierItem the supplier item
	 * @return the sc request note dtl
	 */
	public static	ScRequestNoteDtl	createRequestNoteDtl(ScRequestSupplierItem supplierItem){
		ScRequestNoteDtl	result =	new ScRequestNoteDtl();
		result.setScRequestSupplierItem(supplierItem);
		return result;
	}
	
	/**
	 * Creates the request note dtls.
	 *
	 * @param supplierItems the supplier items
	 * @return the list
	 */
	public static List<ScRequestNoteDtl>	createRequestNoteDtls(List<ScRequestSupplierItem> supplierItems){
		List<ScRequestNoteDtl>	results	=	new ArrayList<ScRequestNoteDtl>();
		for(ScRequestSupplierItem supplierItem: supplierItems){
			results.add(createRequestNoteDtl(supplierItem));
		}
		return results;
	}
}
