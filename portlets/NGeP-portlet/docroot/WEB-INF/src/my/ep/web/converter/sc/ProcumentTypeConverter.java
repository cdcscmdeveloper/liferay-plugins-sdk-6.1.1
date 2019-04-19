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

// TODO: Auto-generated Javadoc
/**
 * The Class ProcumentTypeConverter.
 * @author kenny
 * @since 23.03.2015
 */
@FacesConverter(value = "procurementTypeConverter")
public class ProcumentTypeConverter implements Converter {
	
	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	/**
	 *Convert from String to object.
	 *@param facesContext
	 *@param uiComponent
	 *@param s
	 *@return the object instance.
	 */
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
		String result = s;
		if (result != null) {
			result = result.trim().replaceAll(" ", "");
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	/**
	 *Convert from Object to String.
	 *@param facesContext
	 *@param uiComponent
	 *@param o
	 *@return the formated data.
	 */
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
		StringBuilder sb = new StringBuilder();
		if (o != null) {
			sb.append(o);
		}

		return sb.toString();
	}
}
