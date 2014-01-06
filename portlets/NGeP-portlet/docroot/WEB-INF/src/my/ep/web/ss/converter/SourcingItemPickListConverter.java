package my.ep.web.ss.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

@FacesConverter(value = "sourcingItemPickListConverter")
public class SourcingItemPickListConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		Object ret = null;
		if (arg1 instanceof PickList) {
			Object dualList = ((PickList) arg1).getValue();
			DualListModel dl = (DualListModel) dualList;
			for (Object o : dl.getSource()) {
				ScRequestItem	item	=	(ScRequestItem) o;
				String id = "";
				//item must be catalogue item or bid/quote item 
				if(item.getRequestListItemId() != null){
					id = id + item.getRequestListItemId();
				}else{
					id = id + item.getCmItem().getItemId();
				}
				
				if (arg2.equals(id)) {
					ret = o;
					break;
				}
			}
			if (ret == null)
				for (Object o : dl.getTarget()) {
					ScRequestItem	item	=	(ScRequestItem) o;
					String id = "";
					//item must be catalogue item or bid/quote item 
					if(item.getRequestListItemId() != null){
						id = id + item.getRequestListItemId();
					}else{
						id = id + item.getCmItem().getItemId();
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
		if (arg2 instanceof ScRequestItem) {
			ScRequestItem	item	=	(ScRequestItem) arg2;
			if(item.getRequestListItemId() != null){
				str = str + item.getRequestListItemId();
			}else{
				str = str + item.getCmItem().getItemId();
			}
		}
		return str;
	}
}
