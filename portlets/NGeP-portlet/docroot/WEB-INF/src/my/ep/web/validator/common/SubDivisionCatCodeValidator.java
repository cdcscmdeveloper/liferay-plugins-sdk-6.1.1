package my.ep.web.validator.common;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
@FacesValidator("catCodeValidator")
public class SubDivisionCatCodeValidator implements Validator  {

		@Override
		public void validate(FacesContext context, UIComponent component,
				Object value) throws ValidatorException {
			String myValue = value.toString();

			if (myValue != null && StringUtils.isNotBlank(myValue)
					&& (!StringUtils.isNumeric(myValue))) {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "catCode Error",
						PmCommonUtil.getMessage(
								"lbl.pm.val.subdiv.cat.code.invalid", null)));
			}
		}
	

}
