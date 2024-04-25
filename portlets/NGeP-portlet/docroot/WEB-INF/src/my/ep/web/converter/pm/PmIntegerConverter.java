package my.ep.web.converter.pm;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.NumberConverter;
import javax.faces.convert.FacesConverter;
import my.ep.util.ResourceBundleUtils;


/**
 * Throw custom converter error for integer
 */
@FacesConverter(value = "pmIntegerConverter")
public class PmIntegerConverter extends NumberConverter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		setPattern(new my.ep.web.util.CommonUtils().getDisplayNumberFormat(
				my.ep.constants.CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		try {
			return super.getAsObject(arg0, arg1, arg2);
			
		} catch (ConverterException e) {
			FacesMessage fac =  e.getFacesMessage();
			fac.setDetail(ResourceBundleUtils.getMessage("lbl.pm.settings.int.error",
					new String[] {(String) arg1.getAttributes().get("label"), arg2}, 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
					new my.ep.web.util.FacesScopes().getUserLocale()));
			throw new ConverterException(fac, e);
		}
	}
}
