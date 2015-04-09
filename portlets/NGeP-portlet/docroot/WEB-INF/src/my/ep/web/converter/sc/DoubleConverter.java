package my.ep.web.converter.sc;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.ValidatorException;

import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

@FacesConverter("DoubleConverter")
public class DoubleConverter implements Converter{

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
