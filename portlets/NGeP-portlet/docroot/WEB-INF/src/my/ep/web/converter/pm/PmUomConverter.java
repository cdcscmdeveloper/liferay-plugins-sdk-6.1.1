/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2016 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.converter.pm;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.util.pm.helper.UomHelper;

import org.apache.commons.lang.StringUtils;

/**
 * The converter Class for PmUom.
 * 
 * @author Kenny
 */
@FacesConverter(value = "PmUomConverter")
public class PmUomConverter implements Converter {

	/**
	 *Convert from Long to object.
	 *@param arg0
	 *@param arg1
	 *@param value
	 *@return the object instance.
	 */
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
		
		if (value != null && value.trim().length() > 0 && StringUtils.isNumeric(value)) {
			return UomHelper.getInstance().retrieveUomById(Long.valueOf(value));
		} else {
			return null;
		}
	}

	/**
	 *Convert from Object to String.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the formated data.
	 */
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		if (arg2 != null && arg2 instanceof PmUom) {
			PmUom pmUom = (PmUom) arg2;
			Long uomId = pmUom.getUomId();
			return uomId != null ? String.valueOf(uomId) : "";
		}
		return "";
	}
}