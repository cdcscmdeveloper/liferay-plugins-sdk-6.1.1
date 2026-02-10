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
@FacesConverter(value = "pmNumberConverter")
public class PmNumberConverter extends NumberConverter {
	String decimal = "DECIMAL";
	String decimalPattern = "#,##0.00";
	String integerPattern = "#,##0";

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
			String ruleType = (String) comp.getAttributes().get("ruleType");

			FacesMessage fac = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
					"lbl.pm.settings.number.error", new String[] { label, value,
							ruleType }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new my.ep.web.util.FacesScopes().getUserLocale()));

			throw new ConverterException(fac, e);
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent comp, Object value) {

		String numberType = (String) comp.getAttributes().get("numberType");

		if (value == null) {
			return "";
		}

		if (decimal.equals(numberType)) {
			setPattern(decimalPattern);
		} else {
			setPattern(integerPattern);
		}

		return super.getAsString(ctx, comp, value);
	}
}
