package my.ep.web.converter.cm;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				System.out.println("submittedValue: " + submittedValue);
				String completeAmt = "";

				if (submittedValue.contains(".")) {
					String decimal = submittedValue.substring(submittedValue.indexOf(".") + 1, submittedValue.trim()
							.length());

					if (decimal.trim().length() == 2) {
						String amount = submittedValue.substring(0, submittedValue.indexOf("."));
						completeAmt = amount + "." + decimal;

					} else if (decimal.trim().length() == 1) {
						String amount = submittedValue.substring(0, submittedValue.indexOf("."));
						completeAmt = amount + "." + decimal + "0";

					} else {
						String amount = submittedValue.substring(0, submittedValue.indexOf("."));
						String subSrtingDecimal = decimal.substring(0, 2);
						completeAmt = amount + "." + subSrtingDecimal;
					}
				} else {
					completeAmt = submittedValue + ".00";
				}
				DecimalFormat df = new DecimalFormat("#,###.00");

				String formatAmt = df.format(new BigDecimal(completeAmt));
				System.out.println("formatAmt: " + formatAmt);
				return new BigDecimal(formatAmt);

			} catch (Exception ex) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						MessageUtils.getMessage(MessageConst.ERROR_716027, new String[] { submittedValue },
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		}
	}
}
