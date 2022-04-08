package my.ep.web.util.sm.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;

@FacesValidator("loginIdValidator")
public class LoginIdValidator implements Validator{
private static final String LOGIN_ID_PATTERN = "^[a-zA-Z0-9.-]+([_](?![_])|[a-zA-Z0-9._-](?![_]))+$";
	
	private Pattern pattern;
	private Matcher matcher;
	
	public LoginIdValidator(){
		  pattern = Pattern.compile(LOGIN_ID_PATTERN);
	}
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String loginId = value.toString();

		if(loginId.length() < 8 || loginId.length() > 20){
			FacesMessage msg = new FacesMessage(null,null);
			throw new ValidatorException(msg);
		}

		matcher = pattern.matcher(value.toString());
		if(!matcher.matches()){
			FacesMessage msg = new FacesMessage(null,null);
			throw new ValidatorException(msg);
		}

	}
}
