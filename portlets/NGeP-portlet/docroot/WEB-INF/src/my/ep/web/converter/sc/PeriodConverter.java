/**
 *  Copyright  2013 Archisoft. All rights reserved.
 */
package my.ep.web.converter.sc;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.constant.sc.QTFieldConst;
import my.ep.util.ResourceBundleUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Class for PeriodConverter.
 * 
 * @author haziq
 * @since 13.02.2024
 * 
 */
@FacesConverter("periodConverter")
public class PeriodConverter implements Converter{

	   
		private static final String ID = "id";


		/**
		 *Convert from String to object.
		 *@param arg0
		 *@param arg1
		 *@param arg2
		 *@return the object instance.
		 */
		@Override
		public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		try {
			
			if(StringUtils.isNotBlank(arg2)){
				ret = Integer.valueOf(arg2); //checking can convert to Integer
				if (arg1.getAttributes().get(ID).equals(QTFieldConst.FIELD_LOA_CONTRACT_PERIOD_ID)){			    
					ret = new BigDecimal(arg2);
				}
			}
			return ret;
			
		} catch (NumberFormatException e) {
			 throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "integerValueError", ResourceBundleUtils.getMessage("lbl.sc.settings.int.error",
						new String[] {(String) arg1.getAttributes().get("label")}, 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
						new my.ep.web.util.FacesScopes().getUserLocale())));
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
			String str = "";
			if (arg2 != null) {
				
				if (arg1.getAttributes().get(ID).equals(QTFieldConst.FIELD_LOA_CONTRACT_PERIOD_ID)){			    
					str = "" + ((BigDecimal) arg2).toString();
				}else{
					str = "" + ((Integer) arg2).toString();
				}
				
				return str;
			}else{
				return null;
			}
		}
		
		
	
}
