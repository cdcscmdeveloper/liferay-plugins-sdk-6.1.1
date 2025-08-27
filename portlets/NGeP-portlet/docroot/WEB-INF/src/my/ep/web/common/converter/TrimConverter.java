package my.ep.web.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("trimConverter")
public class TrimConverter implements Converter {
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? value.trim() : null;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value != null ? value.toString().trim() : "";
    }
}
