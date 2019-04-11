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

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

// TODO: Auto-generated Javadoc
/**
 * The Class ApproverPickListConverter.
 * 
 * @author kenny
 * @since 23.03.2015
 */
@FacesConverter(value = "approverPickListConverter")
public class ApproverPickListConverter implements Converter {

	/* 
	 * (non-Javadoc)
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
			String id = null;
			Object dualList = ((PickList) arg1).getValue();
			@SuppressWarnings("unchecked")
			DualListModel<Object> dl = (DualListModel<Object>) dualList;
			for (Object o : dl.getSource()) {
				if (o instanceof PmUser) {
					id = "" + ((PmUser) o).getUserId();	
				} else if (o instanceof ScQtApproverSettings) {
					id = "" + ((ScQtApproverSettings) o).getPmUser().getUserId();
				}
				
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					if (o instanceof PmUser) {
						id = "" + ((PmUser) o).getUserId();	
					} else if (o instanceof ScQtApproverSettings) {
						id = "" + ((ScQtApproverSettings) o).getPmUser().getUserId();
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
		if (arg2 instanceof PmUser) {
			str = "" + ((PmUser) arg2).getUserId();
		} else if (arg2 instanceof ScQtApproverSettings) {
			str = "" + ((ScQtApproverSettings) arg2).getPmUser().getUserId();
		}
		return str;
	}
}