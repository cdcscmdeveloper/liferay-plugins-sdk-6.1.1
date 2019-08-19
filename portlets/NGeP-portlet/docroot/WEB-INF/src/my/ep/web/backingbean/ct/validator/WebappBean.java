/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.validator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: bob Date: 5/29/12 Time: 5:12 PM
 */

@ManagedBean(name = "webappBean")
@ApplicationScoped
public class WebappBean {

	public void validateEmail(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.email",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String inputValue = (String) obj;
		inputValue = inputValue == null ? "" : inputValue;
		Pattern mask = null;
		String pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		mask = Pattern.compile(pattern);
		Matcher matcher = mask.matcher(inputValue);

		if (!matcher.matches()) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void checkSpaces(FacesContext ctx, UIComponent fieldName, Object obj) {
		String inputValue = (String) obj;
		inputValue = inputValue == null ? "" : inputValue;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.alphabet.hyphen",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String pattern = "^[a-zA-Z\\s]+$";

		if (!inputValue.matches(pattern)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void checkInputField(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		String inputValue = (String) obj;
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			String errMsg = ResourceBundleUtils.getMessage(
					"lbl.ct.validation.alphabet.space",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern = "^[a-zA-Z\\s]+$";
			if (!inputValue.matches(pattern)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg));
			}
		}
	}

	public void checkIntegerField(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		System.out.println("..checkIntegerField.....");
		String inputValue = (String) obj;
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			String errMsg = ResourceBundleUtils.getMessage(
					"lbl.ct.validation.integer.no",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern = "^[0-9\\s]+$";
			if (!inputValue.matches(pattern)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg));
			}
		}
	}

	public void checkFloatField(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		System.out.println("..checkFloatField.....");
		Double inputValue = (Double) obj;
		String strValue = Double.toString(inputValue);
		inputValue = inputValue == null ? 0 : inputValue;
		if (!strValue.isEmpty()) {
			String errMsg = ResourceBundleUtils.getMessage(
					"lbl.ct.validation.integer.no",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern = "[0-9]*\\.?[0-9]+";
			if (!strValue.matches(pattern)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg));
			}
		}
	}

	public void validateNumbers(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		String inputValue = (String) obj;
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			String errMsg = ResourceBundleUtils.getMessage(
					"lbl.ct.validation.alphabet.number",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern = "^[a-zA-Z0-9]+$";
			if (!inputValue.matches(pattern)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg));
			}
		}
	}

	public void checkPhoneNumber(FacesContext ctx, UIComponent fieldName,
			Object obj) {

		String inputValue = (String) obj;
		if (inputValue == null || inputValue.equalsIgnoreCase(""))
			return;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.phone.number",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String pattern = "\\d+";
		if (!inputValue.matches(pattern)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void checkMobileNumber(FacesContext ctx, UIComponent fieldName,
			Object obj) {

		String inputValue = (String) obj;
		if (inputValue == null || inputValue.equalsIgnoreCase(""))
			return;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.mobile.number",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String pattern ="^\\+[0-9]*$";
		if (!inputValue.matches(pattern)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void checkFaxNumber(FacesContext ctx, UIComponent fieldName,
			Object obj) {

		String inputValue = (String) obj;
		if (inputValue == null || inputValue.equalsIgnoreCase(""))
			return;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.fax.number",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String pattern = "\\d+";
		if (!inputValue.matches(pattern)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void checkEmail(FacesContext ctx, UIComponent fieldName, Object obj) {
		String inputValue = (String) obj;
		if (inputValue == null || inputValue.equalsIgnoreCase(""))
			return;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.email",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		String pattern = "[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]";// [a-zA-Z]=alphabet;
																										// [\\d]or[0-9]=number;
																										// $=line
																										// end
																										// follows;

		if (!inputValue.matches(pattern)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	public void validateDPeriod(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		Integer inputValue = (Integer) value;
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.delivery.term",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		if (inputValue >= 1)
			return;
		else
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
	}
	public void validateDecimalNumber(FacesContext ctx,
			UIComponent fieldName, Object obj) {
		String inputValue = (String) obj;
		// String inputValueStr = inputValue.toString();
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			// 1-step validation: must not be negative digit
			String errMsg1 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.negetive.number.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern1 = "(-[0-9]+)";
			String errMsg2 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.threshod.decimal.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern2 = "[0-9]+(\\.[0-9]+)?";
			if (inputValue.matches(pattern1)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg1));
			} else if (!inputValue.matches(pattern2)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg2));
			} 

		}
	}
	public void validateNonDecimalNumber(FacesContext ctx,
			UIComponent fieldName, Object obj) {
		String inputValue = (String) obj;
		// String inputValueStr = inputValue.toString();
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			// 1-step validation: must not be negative digit
			String errMsg1 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.negetive.number.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern1 = "(-[0-9]+)";
			String errMsg2 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.threshod.notdecimal.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern2 = "[0-9]+";
			if (inputValue.matches(pattern1)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg1));
			} else if (!inputValue.matches(pattern2)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg2));
			} 

		}
	}
	public void validatePercentageNumber(FacesContext ctx,
			UIComponent fieldName, Object obj) {
		String inputValue = (String) obj;
		// String inputValueStr = inputValue.toString();
		inputValue = inputValue == null ? "" : inputValue;
		if (!inputValue.isEmpty()) {
			// 1-step validation: must not be negative digit
			String errMsg1 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.negetive.number.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern1 = "(-[0-9]+)";
			String errMsg2 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.threshod.percentage.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern2 = "^((100)|(\\d{0,2}(\\.[0-9]+)?))$";
			String errMsg3 = ResourceBundleUtils.getMessage(
					"lbl.pm.settings.threshod.decimal.validation",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale());
			String pattern3 = "[0-9]+(\\.[0-9]+)?";
			if (inputValue.matches(pattern1)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg1));
			} else if (!inputValue.matches(pattern3)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg3));
			} else if (!inputValue.matches(pattern2)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg2));
			}

		}
	}

	public void validateDate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		String errMsg = ResourceBundleUtils.getMessage(
				"lbl.ct.validation.start.end.period",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		if (value == null || component.getAttributes().get("fromDate") == null) {
			return;
		}

		Date toDate = (Date) value;

		// <f:attribute name="fromDate" value="#{edf.value}" />
		Date fromDate = (Date) component.getAttributes().get("fromDate");// <===
		if (!toDate.after(fromDate)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}
	//Bug #25631
	public void validateInteger(FacesContext ctx, UIComponent fieldName,
			Object obj) {
		Integer inputValue = (Integer) obj;
		String labelNameVal = (String) fieldName.getAttributes().get("label");
		if (inputValue == null) {
			return;
		}
		
		if((inputValue <= 0) && (!StringUtils.isBlank(labelNameVal))) {
			String errMsg = ResourceBundleUtils.getMessage(
					"lbl.sc.validation.delivery.period", new Object[]{labelNameVal, inputValue},
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
					new FacesScopes().getUserLocale());
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "", errMsg));
		}
	}

	protected void addInfo(String message) {
		addMessage(FacesMessage.SEVERITY_INFO, "Information:", message);
	}

	protected void addInfo(String id, String message) {
		addMessage(id, FacesMessage.SEVERITY_INFO, "Information:", message);
	}

	protected void addError(String message) {
		addMessage(FacesMessage.SEVERITY_ERROR, "Error:", message);
	}

	protected void addError(String id, String message) {
		addMessage(id, FacesMessage.SEVERITY_ERROR, "Error:", message);
	}

	private void addMessage(FacesMessage.Severity severity, String msgTitle,
			String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(severity, msgTitle, message));
	}

	private void addMessage(String id, FacesMessage.Severity severity,
			String msgTitle, String message) {
		FacesContext.getCurrentInstance().addMessage(id,
				new FacesMessage(severity, msgTitle, message));
	}

}
