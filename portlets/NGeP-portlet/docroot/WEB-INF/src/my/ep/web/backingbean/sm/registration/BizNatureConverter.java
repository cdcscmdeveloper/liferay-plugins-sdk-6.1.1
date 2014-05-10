package my.ep.web.backingbean.sm.registration;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import my.ep.persistence.entity.pm.common.PmBusinessNature;

@FacesConverter(value = "bizNatureConverter")
public class BizNatureConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String bizId) {
		System.out.println("**************bizNatureConverter getAsObject***************" + bizId);
		PmBusinessNature biz = new PmBusinessNature();
		biz.setBusinessNatureId(Long.parseLong(bizId));
		return biz;
	}

	@Override
	public String getAsString(FacesContext facesContext,UIComponent uiComponent, Object obj) {
		System.out.println("**************bizNatureConverter getAsString***************");
		if (obj != null) {
			PmBusinessNature biz = (PmBusinessNature) obj;
			if(biz.getBusinessNatureId() != null){
				return biz.getBusinessNatureId().toString();
			}else{
				return "";
			}
		} else {
			return "";
		}
	}
}
