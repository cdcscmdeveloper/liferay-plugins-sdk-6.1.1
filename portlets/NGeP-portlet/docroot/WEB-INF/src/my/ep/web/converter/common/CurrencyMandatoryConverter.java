package my.ep.web.converter.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang3.StringUtils;

@FacesConverter("currencyMandatoryConverter")
public class CurrencyMandatoryConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		
		NumberFormat formatter = new DecimalFormat("#0.00");
		BigDecimal currVal = new BigDecimal(formatter.format(0));
		try {
			if (value != null && StringUtils.isNotBlank(value)) {
				value = value.replace(",", "");
				currVal = new BigDecimal(value);
				currVal.setScale(2, RoundingMode.HALF_UP);
				if (currVal.compareTo(BigDecimal.ZERO) == 0) {
					throw new ConverterException(
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Currency Numeric Error",
									SmCommonUtil
											.getMessage(
													"lbl.sm.inputcurrency.zero.errmsg",
													null)));
				} else {
					if (getLength(currVal) > 14) {
						throw new ConverterException(
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Currency Numeric Error",
										SmCommonUtil
												.getMessage(
														"lbl.sm.inputcurrency.numericformat.errmsg",
														null)));
					} else {
						if (getNumberOfDecimalPlaces(currVal) > 2) {
							throw new ConverterException(
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											"Currency Decimal Error",
											SmCommonUtil
													.getMessage(
															"lbl.sm.inputcurrency.decimalformat.errmsg",
															null)));
						}
					}
				}

			}
		} catch (NumberFormatException ne) {
			throw new ConverterException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Currency Numeric Error",
					SmCommonUtil.getMessage(
							"lbl.sm.inputcurrency.numericformat.errmsg", null)));
		}

		return currVal;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		// TODO Auto-generated method stub
		return value == null ? null : value.toString();
	}

	private int getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
		String string = bigDecimal.stripTrailingZeros().toPlainString();
		int index = string.indexOf(".");
		return index < 0 ? 0 : string.length() - index - 1;
	}

	private int getLength(BigDecimal bigDecimal) {
		String string = bigDecimal.toPlainString();
		int index = string.indexOf(".") == -1 ? string.length() : string
				.indexOf(".");
		return index;
	}

}
