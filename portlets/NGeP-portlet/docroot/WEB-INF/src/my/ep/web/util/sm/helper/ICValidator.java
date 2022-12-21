package my.ep.web.util.sm.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("icValidator")
public class ICValidator implements Validator{
	private static final String IC_PATTERN = "[0-9]+";
	
	private Pattern pattern;
	private Matcher matcher;
	
	public ICValidator(){
		  pattern = Pattern.compile(IC_PATTERN);
	}
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String IcNo = value.toString();
		
		if(IcNo.length() > 0){
			if(IcNo.length() != 12){

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
}
