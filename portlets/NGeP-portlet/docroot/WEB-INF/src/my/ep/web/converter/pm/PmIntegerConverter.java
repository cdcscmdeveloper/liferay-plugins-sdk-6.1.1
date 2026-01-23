package my.ep.web.converter.pm;

import java.math.BigDecimal;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

import my.ep.util.ResourceBundleUtils;

/**
 * Throw custom converter error for integer and decimal
 */
@FacesConverter(value = "pmIntegerConverter")
public class PmIntegerConverter extends NumberConverter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {

		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		try {
			// remove grouping separators
			return new BigDecimal(value.replace(",", ""));
		} catch (NumberFormatException e) {

			String label = (String) comp.getAttributes().get("label");
			String numberType = (String) comp.getAttributes().get("numberType");
			String ruleType = (String) comp.getAttributes().get("ruleType");

			FacesMessage fac = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
					"lbl.pm.settings.int.error", new String[] { label, value,
							ruleType }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new my.ep.web.util.FacesScopes().getUserLocale()));

			throw new ConverterException(fac, e);
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent comp, Object value) {

		if (value == null) {
			return "";
		}

		String numberType = (String) comp.getAttributes().get("numberType");

		if ("DECIMAL".equals(numberType)) {
			setPattern("#,##0.00");
		} else {
			setPattern("#,##0");
		}

		return super.getAsString(ctx, comp, value);
	}
}
