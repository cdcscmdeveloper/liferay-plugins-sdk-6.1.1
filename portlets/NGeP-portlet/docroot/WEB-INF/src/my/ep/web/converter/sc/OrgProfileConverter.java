package my.ep.web.converter.sc;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
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
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

// TODO: Auto-generated Javadoc
/**
 * The Class OrgProfileConverter.
 * 
 * @author kenny
 * @since 23.03.2015
 */
@FacesConverter(value = "OrgProfileConverter")
public class OrgProfileConverter implements Converter {
	
	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	/**
	 *Convert from String to object.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the object instance.
	 */
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return arg2;
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
		if (arg2 != null) {
			str = "" + ((PmOrgProfile) arg2).getOrgProfileId();
			return str;
		}else{
			return null;
		}
	}
}
