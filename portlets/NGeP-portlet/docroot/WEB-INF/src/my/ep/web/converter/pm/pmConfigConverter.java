package my.ep.web.converter.pm;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.ct.CtConfig;
import my.ep.persistence.entity.ct.CtContract;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

@FacesConverter(value = "pmConfigConverter")
public class pmConfigConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			String id = null;
			Object dualList = ((PickList) arg1).getValue();
			@SuppressWarnings("unchecked")
			DualListModel<Object> dl = (DualListModel<Object>) dualList;
			for (Object o : dl.getSource()) {
				if (o instanceof CtConfig) {
					id = "" + ((CtConfig) o).getContractNo();	
				} 
				
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					if (o instanceof CtConfig) {
						id = "" + ((CtConfig) o).getContractNo();	
					} 
					
					if (arg2.equals(id)) {
						ret = o;
						break;
					}
				}
		}
		return ret;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		String str = "";
		if (arg2 instanceof CtConfig) {
			str = "" + ((CtConfig) arg2).getContractNo();
		} 
		return str;
	}
}