package my.ep.web.converter.cm;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.constant.MessageConst;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;


@FacesConverter("deliveryTermConverter")
public class PublicCatalogueDeliveryTermConverter extends javax.faces.convert.NumberConverter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue) {
		 if (submittedValue.trim().equals("")) {
	            return null;
	        } else {
	            try {
	            	return Integer.parseInt(submittedValue);
	            } catch(Exception ex) {
	                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
							MessageConst.ERROR_716028, new String[] {submittedValue},
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
	            }
		}
	}
}
