package my.ep.web.backingbean.sm.sitevisit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import my.ep.persistence.entity.pm.authorization.PmUser;


@FacesConverter(value="svoListConverter")
public class SvoPickConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String svo) {
		Object ret = null;
		if (component instanceof PickList) {
			Object dualList = ((PickList) component).getValue();
			DualListModel dl = (DualListModel) dualList;
			for (Object o : dl.getSource()) {
				String id = "" + ((PmUser) o).getUserId();
				if (svo.equals(id)) {
					ret = o;
					break;
				}
			}
			
			if (ret == null)
				for (Object o : dl.getTarget()) {
					String id = "" + ((PmUser) o).getUserId();
					if (svo.equals(id)) {
						ret = o;
						break;
					}
				}
		}
		
		System.out.println("***********getAsObject***" + ret.getClass());
		return ret;
	}
	
	@Override
	public String getAsString(FacesContext facesContext,UIComponent uiComponent, Object obj) {
		String str = "";
		if (obj instanceof PmUser) {
			PmUser pmUser = (PmUser) obj;
			if(pmUser.getUserId() != null){
				str = String.valueOf(pmUser.getUserId());
				
				System.out.println("**********getAsString = " + str);
			}
        }
		
		return str;
	}
}
