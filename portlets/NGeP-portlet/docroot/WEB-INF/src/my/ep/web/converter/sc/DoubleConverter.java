/**
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.converter.sc;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

/**
 * Class for DoubleConverter.
 * 
 * @author zeinab
 * @since 04.09.2015
 * 
 */
@FacesConverter("DoubleConverter")
public class DoubleConverter implements Converter{

	/**
	 *Convert from Double to object.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the object instance.
	 */
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException{
		Object ret = null;
		try{
		if(StringUtils.isNotBlank(arg2)){
			ret	= Double.valueOf(arg2);
		}
		}catch(NumberFormatException e){
			 throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "doubleValueError", SmCommonUtil.getMessage(
						"lbl.pm.settings.double.value.error.msg", null)));
		}
		return ret;
	}
	
	/**
	 *Convert from Object to String.
	 *@param arg0
	 *@param arg1
	 *@param arg2
	 *@return the formated data.
	 */
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2){
		String str = "";
		if (arg2 != null) {
			str = "" + ((Double) arg2).toString();
			return str;
		}else{
			return null;
		}
	}
}
