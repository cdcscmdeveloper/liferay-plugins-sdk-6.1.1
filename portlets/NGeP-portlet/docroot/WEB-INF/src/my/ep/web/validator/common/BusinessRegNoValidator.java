package my.ep.web.validator.common;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.lang3.StringUtils;

@FacesValidator("busRegNoValidator")
public class BusinessRegNoValidator implements Validator {

	private static List<String> robList = Arrays.asList("C", "F");
	private static List<String> rocList = Arrays.asList("I", "J");
	private static List<String> llpList = Arrays.asList("B");

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		UIInput dropdown = (UIInput) component.getParent().findComponent(
				"busTypeId");
		String businessType = "";
		String businessRegNo = "";

		if (value != null && StringUtils.isNotBlank(value.toString())) {
			businessRegNo = value.toString();
			if (dropdown != null) {
				businessType = dropdown.getValue().toString();

				if (robList.contains(businessType)) {
					validateRob(businessRegNo);
				} else if (rocList.contains(businessType)) {
					validateRoc(businessRegNo);
				} else if (llpList.contains(businessType)) {
					validateLlp(businessRegNo);
				}
			}
		}
	}

	private void validateRoc(String rocNo) {
		String[] splitRoc = rocNo.split("-");
		String splitRoc1 = "";
		String splitRoc2 = "";
		if (splitRoc != null && splitRoc.length > 1) {
			splitRoc1 = splitRoc[0] != null ? splitRoc[0] : "";
			splitRoc2 = splitRoc[1] != null ? splitRoc[1] : "";
			if (splitRoc1.length() > 7) {
				throwErrorMsg("lbl.sm.businessregno.beforedash.roc");
			} else {
				if (StringUtils.isBlank(splitRoc2) || splitRoc2.length() != 1) {
					throwErrorMsg("lbl.sm.businessregno.afterdash.roc");
				}
			}
		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.roc");
		}
	}

	private void validateRob(String robNo) {
		String[] splitRob = robNo.split("-");
		String splitRob1 = "";
		String splitRob2 = "";
		if (splitRob != null && splitRob.length > 1) {
			splitRob1 = splitRob[0] != null ? splitRob[0] : "";
			splitRob2 = splitRob[1] != null ? splitRob[1] : "";
			if (splitRob1.length() > 9) {
				throwErrorMsg("lbl.sm.businessregno.beforedash.rob");
			} else {
				if (StringUtils.isBlank(splitRob2) || splitRob2.length() != 1) {
					throwErrorMsg("lbl.sm.businessregno.afterdash.rob");
				}
			}
		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.rob");
		}
	}

	private void validateLlp(String llpNo) {
		String[] splitLlp = llpNo.split("-");
		String splitLlp1 = "";
		String splitLlp2 = "";
		if (splitLlp != null && splitLlp.length > 1) {
			splitLlp1 = splitLlp[0] != null ? splitLlp[0] : "";
			splitLlp2 = splitLlp[1] != null ? splitLlp[1] : "";
			if (llpNo.length() > 30) {
				throwErrorMsg("lbl.sm.businessregno.max.llp");
			} else {
				if (splitLlp1.length() < 3
						|| !splitLlp1.substring(0, 3).equals("LLP")) {
					throwErrorMsg("lbl.sm.businessregno.notvalid.firstthree.llp");
				} else {
					if (StringUtils.isBlank(splitLlp2)
							|| splitLlp2.length() != 3) {
						throwErrorMsg("lbl.sm.businessregno.afterdash.llp");
					}
				}
			}

		} else {
			throwErrorMsg("lbl.sm.businessregno.notvalid.llp");
		}
	}

	private void throwErrorMsg(String key) {
		throw new ValidatorException(new FacesMessage(
				FacesMessage.SEVERITY_ERROR, "Business Reg No Error",
				SmCommonUtil.getMessage(key, null)));
	}
}
