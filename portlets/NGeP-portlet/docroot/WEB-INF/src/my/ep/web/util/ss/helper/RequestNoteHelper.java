package my.ep.web.util.ss.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.web.backingbean.ss.directpurchase.RequestNotePk;

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
	
	public	static	List<ScRequestNote>	createRequestNoteFromRquestNoteDtls(List<ScRequestNoteDtl>	requestNoteDtls){
		List<ScRequestNote>	results	=	new ArrayList<ScRequestNote>();
		Map<RequestNotePk, List<ScRequestNoteDtl>>	uniqueRequestNotePkMap	=	new HashMap<RequestNotePk, List<ScRequestNoteDtl>>();
		for(ScRequestNoteDtl requestNoteDtl: requestNoteDtls){
			RequestNotePk requestNotePk	=	createRequestNotePk(requestNoteDtl);
			if(uniqueRequestNotePkMap.get(requestNotePk) != null){
				uniqueRequestNotePkMap.get(requestNotePk).add(requestNoteDtl);
			}else{
				List<ScRequestNoteDtl>	listRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
				listRequestNoteDtls.add(requestNoteDtl);
				uniqueRequestNotePkMap.put(requestNotePk, listRequestNoteDtls);
			}
		}
		Collection<RequestNotePk> uniqueRequestNotePks	=	uniqueRequestNotePkMap.keySet();
		for(RequestNotePk requestNotePk: uniqueRequestNotePks){
			ScRequestNote	scRequestNote	=	createRequestNoteFromRequestNotePk(requestNotePk);
			scRequestNote.setScRequestNoteDtls(uniqueRequestNotePkMap.get(requestNotePk));
			PmUser	pmUser	=	new PmUser();
			Long userId	=	uniqueRequestNotePkMap.get(requestNotePk).get(0).getAssignedUserId();
			pmUser.setUserId(userId);
			scRequestNote.setAssignTo(pmUser);
			results.add(scRequestNote);
		}
		return results;
	}
	
	private	static	ScRequestNote	createRequestNoteFromRequestNotePk(RequestNotePk requestNotePk){
		ScRequestNote result	=	new ScRequestNote();
		result.setFulfillmentType(requestNotePk.getFulfillmentType());
		result.setSmSupplier(requestNotePk.getSupplier());
		result.setDeliverPeriodType(requestNotePk.getDeliverPeriodType());
		result.setDeliverPeriod(requestNotePk.getDeliveryPeriodValue().intValue());
		return result;
	}
	private	static	RequestNotePk createRequestNotePk(ScRequestNoteDtl requestNoteDtl){
		RequestNotePk	result	=	new RequestNotePk();
		result.setFulfillmentType(requestNoteDtl.getFulfillmentType());
		result.setSupplier(requestNoteDtl.getScRequestSupplierItem().getSmSupplier());
		result.setDeliverPeriodType(requestNoteDtl.getDeliverPeriodType());
		result.setDeliveryPeriodValue(new Double(requestNoteDtl.getDeliverPeriod()));
		return result;
	}
}
