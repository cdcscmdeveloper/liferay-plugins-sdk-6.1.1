package my.ep.web.ss.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "procurementTypeConverter")
public class ProcumentTypeConverter implements Converter {
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
		String result = s;
		if (result != null) {
			result = result.trim().replaceAll(" ", "");
		}
		return result;
	}

	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
		StringBuilder sb = new StringBuilder();
		if (o != null) {
			sb.append(o);
		}

		return sb.toString();
	}
}
