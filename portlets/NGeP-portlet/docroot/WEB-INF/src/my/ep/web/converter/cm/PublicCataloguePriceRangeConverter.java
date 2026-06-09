package my.ep.web.converter.cm;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.constant.MessageConst;
import my.ep.util.MessageUtils;
import my.ep.web.util.FacesScopes;

@FacesConverter("priceRangeConverter")
public class PublicCataloguePriceRangeConverter extends javax.faces.convert.NumberConverter {

	public static final String DECIMAL_PATTERN = "#,##0.00";

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String submittedValue) {

		if (submittedValue == null || submittedValue.trim().isEmpty()) {
			return null;
		}

		try {
			// remove grouping separators
			return new BigDecimal(submittedValue.replace(",", ""));
		} catch (NumberFormatException e) {

			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage(
					MessageConst.ERROR_716027, new String[] { submittedValue }, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale())));
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent comp, Object value) {

		if (value == null) {
			return "";
		}

		setPattern(DECIMAL_PATTERN);

		return super.getAsString(ctx, comp, value);
	}
}
