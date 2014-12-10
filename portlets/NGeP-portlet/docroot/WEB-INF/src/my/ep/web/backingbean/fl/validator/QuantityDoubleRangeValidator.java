package my.ep.web.backingbean.fl.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("quantityDoubleRangeValidator")
public class QuantityDoubleRangeValidator implements Validator {
	@Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
	
        Object max = component.getAttributes().get("max");
        Object min = component.getAttributes().get("min");
        if (max==null || max =="") {
            return;
        }
        
        if (min==null || min =="") {
            min = 0;
        }
        
        if (value == null || value == "") {
            return;
        }
        
        DoubleRangeValidator validator = new DoubleRangeValidator(Double.parseDouble(max.toString()),Double.parseDouble(min.toString()));    	
    	validator.validate(context, component, value);
    }
}
