package my.ep.web.sc.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang.StringUtils;

@FacesConverter(value = "LongConverter")
public class LongConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if(StringUtils.isNotBlank(arg2)){
			ret	=	Long.valueOf(arg2);
		}
		return ret;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 != null) {
			str = "" + ((Long) arg2).toString();
			return str;
		}else{
			return null;
		}
	}
}
