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
package my.ep.web.converter.sc;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.sc.common.ScRequestItem;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

// TODO: Auto-generated Javadoc
/**
 * The Class SourcingItemPickListConverter.
 * 
 * @author kenny
 * @since 23.03.2015
 */
@FacesConverter(value = "sourcingItemPickListConverter")
public class SourcingItemPickListConverter implements Converter {
	
	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	/**
	 *Convert from PickList to object.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the object instance.
	 */
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			Object dualList = ((PickList) arg1).getValue();
			DualListModel dl = (DualListModel) dualList;
			for (Object o : dl.getSource()) {
				ScRequestItem	item	=	(ScRequestItem) o;
				String id = "";
				//item must be catalogue item or bid/quote item 
				if(item.getRequestNoteItemId() != null){
					id = id + item.getRequestNoteItemId();
				}else{
					id = id + item.getCmItem().getItemId();
				}
				
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					ScRequestItem	item	=	(ScRequestItem) o;
					String id = "";
					//item must be catalogue item or bid/quote item 
					if(item.getRequestNoteItemId() != null){
						id = id + item.getRequestNoteItemId();
					}else{
						id = id + item.getCmItem().getItemId();
					}
					if (arg2.equals(id)) {
						ret = o;
						break;
					}
				}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	/**
	 *Convert from Object to String.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the formated data.
	 */
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 instanceof ScRequestItem) {
			ScRequestItem	item	=	(ScRequestItem) arg2;
			if(item.getRequestNoteItemId() != null){
				str = str + item.getRequestNoteItemId();
			}else{
				str = str + item.getCmItem().getItemId();
			}
		}
		return str;
	}
}
