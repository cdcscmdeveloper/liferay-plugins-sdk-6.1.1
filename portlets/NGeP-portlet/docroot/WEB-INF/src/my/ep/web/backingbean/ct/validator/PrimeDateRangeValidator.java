package my.ep.web.backingbean.ct.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.primefaces.component.calendar.Calendar;

@FacesValidator("primeDateRangeValidator")
public class PrimeDateRangeValidator implements Validator{

	@Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        
        //Leave the null handling of startDate to required="true"
        Object startDateValue = component.getAttributes().get("startDate");
        if (startDateValue==null || startDateValue =="") {
            return;
        }
        
        if (value == null || value == "") {
            return;
        }
         
        Date startDate = (Date)startDateValue;
        Date endDate = (Date)value;
        
        if (startDate.compareTo(endDate) == 1) { // end date less than start date
        	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,((Calendar)component).getLabel(), 
          		  ResourceBundleUtils.getMessage("lbl.ct.validation.expiry.date",
          				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
        }
    }
}
