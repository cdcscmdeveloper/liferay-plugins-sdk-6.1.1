/**
 * 
 */
package my.ep.web.common.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.web.constant.CommonUIConst;

/**
 * @author VidyaSagarReddy
 * 
 */
@FacesConverter(value = "decimalConverter")
public class DecimalConverter extends NumberConverter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value != null &&  value.trim().length() > 0) {

			// To Remove the , if exist while doing the conversion...
			if (value.contains(",")) {
				value = value.replace(",", "");

			}
			int integralValue = 0;
			int decimalValue = 0;

			if (value.contains(".")) {
				String[] digits = value.split("\\.");
				integralValue = digits[0].length();
				decimalValue = digits[1].length();
			} else {
				integralValue = value.length();
			}
			Integer maxFractionDegits = getMaxFractionDigits();
			if (getPattern() != null && 0 == maxFractionDegits.intValue()) {
				String pattern = getPattern();
				if (pattern.contains(".")) {
					String[] digits = pattern.split("\\.");
					maxFractionDegits = digits[1].length();
				}
			}

			if (getMaxIntegerDigits() < integralValue) {
				throw new EpException(
						MessageConst.ERROR_700050,
						new String[]{new Integer(getMaxIntegerDigits()).toString()});

			} else if (maxFractionDegits < decimalValue) {

				throw new EpException(
						MessageConst.ERROR_700051,
						new String[]{maxFractionDegits.toString()});

			}
			// Code to allow only digits and one dot. dot should be inbetween
			// digits
			if (isIntegerOnly()) {
				Pattern pattern = Pattern
						.compile(CommonUIConst.NUMBER_REG_EX_PATTERN);
				Matcher matcher = pattern.matcher(value);
				if (matcher.find()) {
					try {
						return super.getAsObject(context, component, value);
					} catch (Exception ex) {
						throw new EpException(
								MessageConst.ERROR_700050,
								new String[]{ex.getMessage()});
					}
				} else {
					throw new EpException(
							MessageConst.ERROR_700051);
				}
			} else {
				Pattern pattern = Pattern
						.compile(CommonUIConst.DECIMAL_REG_EX_PATTERN);
				Matcher matcher = pattern.matcher(value);
				if (matcher.find()) {
					try {
						return super.getAsObject(context, component, value);
					} catch (Exception ex) {
						throw new EpException(
								MessageConst.ERROR_700050,
								new String[]{ex.getMessage()});
					}
				} else {
					throw new EpException(
							MessageConst.ERROR_700052);
				}
			}
		}else{
			return super.getAsObject(context, component, value);
		}
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (null == value) {
			return "";
		}
		return super.getAsString(context, component, value);

	}
	

}
