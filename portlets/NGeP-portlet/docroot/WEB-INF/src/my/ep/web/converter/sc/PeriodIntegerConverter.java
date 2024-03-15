/**
 *  Copyright  2013 Archisoft. All rights reserved.
 */
package my.ep.web.converter.sc;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.IntegerConverter;
import my.ep.util.ResourceBundleUtils;


/**
 * Class for PeriodIntegerConverter.
 * 
 * @author haziq
 * @since 13.02.2024
 * 
 */
@FacesConverter("periodIntegerConverter")
public class PeriodIntegerConverter extends IntegerConverter{

	/**
	 *@param context
	 *@param component
	 *@param value
	 *@return the object instance.
	 */
	   @Override
	    public Object getAsObject(FacesContext context, UIComponent component, String value) {
	        try {
	            return super.getAsObject(context, component, value);
	        } catch (ConverterException e) {
	        	FacesMessage fac =  e.getFacesMessage();
				fac.setDetail(ResourceBundleUtils.getMessage("lbl.sc.settings.int.error",
						new String[] {(String) component.getAttributes().get("label")}, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
						new my.ep.web.util.FacesScopes().getUserLocale()));
				throw new ConverterException(fac, e);
	        }
	    }
	
}
