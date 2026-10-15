/**
 * 
 */
package my.ep.web.common.converter;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

import my.ep.util.ResourceBundleUtils;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.sun.faces.util.MessageFactory;

/**
 * @author VidyaSagarReddy
 * 
 */
@FacesConverter(value = "decimalConverter")
public class DecimalConverter extends NumberConverter {

	public static final String INTEGER_ID = "my.ep.converter.IntegerConverter.INTEGER";
	public static final String DECIMAL_ID = "my.ep.converter.BigDecimalConverter.DECIMAL";

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (value != null && value.trim().length() > 0) {
			int integralValue = 0;
			int decimalValue = 0;
			int maxIntegerDigits = getMaxIntegerDigits();
			int maxFractionDigits = getMaxFractionDigits();
			boolean isIntegerOnly = isIntegerOnly();
			String patternAttr = (String) component.getAttributes().get("pattern");
			
			if (patternAttr != null) {
				setPattern(patternAttr);	
			}

			// To Remove the , if exist while doing the conversion...
			if (value.contains(",")) {
				value = value.replace(",", "");

			}

			if (value.contains(".")) {
				String[] digits = value.split("\\.");
				if (ArrayUtils.isNotEmpty(digits)) {
					integralValue = digits[0].length();
					decimalValue = digits[1].length();
				}
			} else {
				integralValue = value.length();
			}

			if (getPattern() != null && 0 == maxFractionDigits) {
				String pattern = getPattern();
				if (pattern.contains(".")) {
					String[] digits = pattern.split("\\.");
					if (ArrayUtils.isNotEmpty(digits)) {
						maxFractionDigits = digits[1].length();
					}
				}
			}
			
			if (maxFractionDigits == 0) {
				isIntegerOnly = true;
			}

			BigDecimal minValue = getMinValue(component, isIntegerOnly,
					maxFractionDigits);
			BigDecimal maxValue = getMaxValue(component, isIntegerOnly,
					maxIntegerDigits, maxFractionDigits);

			if (maxIntegerDigits < integralValue) {
				throw getDecimalConverterException(context, component, value,
						minValue, maxValue, isIntegerOnly);

			} else if (maxFractionDigits < decimalValue) {
				// #35293 - Start
				String label = (String) MessageFactory.getLabel(context, component);
				String labelManualScore = ResourceBundleUtils.getMessage("lbl.tpl.spec.content.user.score",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
				if (!labelManualScore.equalsIgnoreCase(label)) {
					throw getDecimalConverterException(context, component, value, minValue, maxValue, isIntegerOnly);
				}
				// #35293 - End

			}
			// Code to allow only digits and one dot. dot should be in between
			// digits
			Pattern pattern = null;

			if (isIntegerOnly) {
				pattern = Pattern.compile(CommonUIConst.NUMBER_REG_EX_PATTERN);

			} else {
				pattern = Pattern.compile(CommonUIConst.DECIMAL_REG_EX_PATTERN);
			}

			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				try {
					BigDecimal decValue = (BigDecimal) super.getAsObject(
							context, component, value);

					if (decValue.compareTo(minValue) < 0) {
						throw getDecimalConverterException(context, component,
								value, minValue, maxValue, isIntegerOnly);
					}

					if (decValue.compareTo(maxValue) > 0) {
						throw getDecimalConverterException(context, component,
								value, minValue, maxValue, isIntegerOnly);
					}

					return decValue;

				} catch (Exception ex) {
					throw getDecimalConverterException(context, component,
							value, minValue, maxValue, isIntegerOnly);
				}
			} else {
				throw getDecimalConverterException(context, component, value,
						minValue, maxValue, isIntegerOnly);
			}
		} else {
			return super.getAsObject(context, component, value);
		}
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if (null == value) {
			return "";
		}
		String patternAttr = (String) component.getAttributes().get("pattern");
		
		if (patternAttr != null) {
			setPattern(patternAttr);	
		}
		return super.getAsString(context, component, value);

	}

	private ConverterException getDecimalConverterException(
			FacesContext context, UIComponent component, Object value,
			BigDecimal minValue, BigDecimal maxValue, boolean isIntegerOnly) {

		String msgId = null;

		if (isIntegerOnly) {
			msgId = INTEGER_ID;

		} else {
			msgId = DECIMAL_ID;
		}

		return new ConverterException(MessageFactory.getMessage(context, msgId,
				value, minValue.toString(), maxValue.toString(),
				MessageFactory.getLabel(context, component)));
	}

	private BigDecimal getMinValue(UIComponent component,
			boolean isIntegerOnly, int maxFractionDigits) {
		String min = (String) component.getAttributes().get("min");
		BigDecimal minValue = null;

		if (StringUtils.isNotBlank(min)) {
			minValue = new BigDecimal(min);
		} else {
			if (isIntegerOnly || maxFractionDigits == 0) {
				minValue = BigDecimal.valueOf(1);
			} else {
				minValue = new BigDecimal("0."
						+ String.format("%1$0" + maxFractionDigits + "d", 1));
			}
		}

		return minValue;
	}

	private BigDecimal getMaxValue(UIComponent component,
			boolean isIntegerOnly, int maxIntegerDigits, int maxFractionDigits) {
		String max = (String) component.getAttributes().get("max");
		BigDecimal maxValue = null;

		if (StringUtils.isNotBlank(max)) {
			maxValue = new BigDecimal(max);
		} else {
			maxValue = BigDecimal.valueOf(Double.valueOf(
					(Math.pow(10, maxIntegerDigits) - 1)).longValue());

			if (!isIntegerOnly && maxFractionDigits > 0) {
				maxValue = maxValue.add(BigDecimal.valueOf(1 - (1 / (Math.pow(
						10, maxFractionDigits)))));
			}
		}

		return maxValue;
	}
}
