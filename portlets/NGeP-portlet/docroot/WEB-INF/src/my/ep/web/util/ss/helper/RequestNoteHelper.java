package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;

public class RequestNoteHelper {
	
	public static	List<ScRequestNote>	createRequestNotes(List<ScRequestSupplierItem>	supplierItems){
		List<ScRequestNote>	results	=	new ArrayList<ScRequestNote>();
		for(ScRequestSupplierItem supplierItem: supplierItems){
			results.add(createRequestNote(supplierItem));
		}
		return results;
	}
	
	public	static	ScRequestNote	createRequestNote(ScRequestSupplierItem supplierItem){
		ScRequestNote	result	=	new ScRequestNote();
		ScRequestNoteDtl	requestNoteDtl	=	new ScRequestNoteDtl();
		List<ScRequestNoteDtl> requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
		requestNoteDtl.setScRequestSupplierItem(supplierItem);
		requestNoteDtls.add(requestNoteDtl);
		result.setScRequestNoteDtls(requestNoteDtls);
		return result;
	}
}
