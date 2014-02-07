package my.ep.web.sc.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesConverter(value = "OrgProfileConverter")
public class OrgProfileConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		return arg2;
	}

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
