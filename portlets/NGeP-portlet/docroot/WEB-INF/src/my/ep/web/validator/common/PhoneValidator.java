package my.ep.web.validator.common;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang.StringUtils;

@FacesValidator("phoneValidator")
public class PhoneValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		UIInput areaCodeInput = (UIInput) component.getParent().findComponent(
				"areaCodeCompId");
		UIInput countryCodeInput = (UIInput) component.getParent()
				.findComponent("countryCodeCompId");
		UIInput telcoIdInput = (UIInput) component.getParent().findComponent(
				"telcoCompId");

		String telcoIdVal = (telcoIdInput != null && telcoIdInput.getValue() != null) ? telcoIdInput
				.getValue().toString() : "";
		String isMobileVal = (String) component.getAttributes().get(
				"isMobileName");
		String labelNameVal = (String) component.getAttributes().get(
				"labelName");
		String areaCodeVal = (areaCodeInput != null && areaCodeInput.getValue() != null) ? areaCodeInput
				.getValue().toString() : "";
		String phoneNoVal = value != null ? value.toString() : "";
		String countryCodeVal = (countryCodeInput != null && countryCodeInput
				.getValue() != null) ? countryCodeInput.getValue().toString()
				: "";

		if (StringUtils.isBlank(countryCodeVal)
				&& (StringUtils.isNotBlank(phoneNoVal) || StringUtils
						.isNotBlank(areaCodeVal))) {
			throwErrorMsg(labelNameVal,
					SmCommonUtil.getMessage("lbl.sm.country.code", null));
		} else if (StringUtils.isBlank(areaCodeVal)
				&& StringUtils.isNotBlank(phoneNoVal)) {
			throwErrorMsg(labelNameVal,
					SmCommonUtil.getMessage("lbl.sm.area.code", null));
		} else if (StringUtils.isBlank(phoneNoVal)
				&& StringUtils.isNotBlank(areaCodeVal)) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Phone No Error",
					SmCommonUtil.getMessage("lbl.sm.is.required",
							new String[] { labelNameVal })));
		} else if ((StringUtils.isNotBlank(areaCodeVal) && !StringUtils
				.isNumeric(areaCodeVal))
				|| (StringUtils.isNotBlank(countryCodeVal) && !StringUtils
						.isNumeric(countryCodeVal))
				|| (StringUtils.isNotBlank(phoneNoVal) && !StringUtils
						.isNumeric(phoneNoVal))) {
			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Phone No Error",
					SmCommonUtil.getMessage("lbl.sm.phone.number.format.error",
							new String[] { labelNameVal })));
		} else if (isMobileVal.equals("Y")
				&& StringUtils.isNotBlank(telcoIdVal)) {
			if (StringUtils.isBlank(phoneNoVal)) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Phone No Error",
						SmCommonUtil.getMessage("lbl.sm.is.required",
								new String[] { labelNameVal })));
			} else if (StringUtils.isBlank(areaCodeVal)) {
				throwErrorMsg(labelNameVal,
						SmCommonUtil.getMessage("lbl.sm.area.code", null));
			} else if (StringUtils.isBlank(countryCodeVal)) {
				throwErrorMsg(labelNameVal,
						SmCommonUtil.getMessage("lbl.sm.country.code", null));
			}
		}

	}

	private void throwErrorMsg(String phoneType, String phoneComponent) {
		throw new ValidatorException(new FacesMessage(
				FacesMessage.SEVERITY_ERROR, "Phone No Error",
				SmCommonUtil.getMessage("lbl.sm.is.required",
						new String[] { phoneType + " - " + phoneComponent })));
	}

}
