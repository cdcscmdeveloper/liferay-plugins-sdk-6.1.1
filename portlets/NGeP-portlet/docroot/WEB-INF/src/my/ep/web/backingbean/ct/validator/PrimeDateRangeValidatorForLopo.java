/**
 * Copyright © 2019 CDC. All rights reserved.
 */
package my.ep.web.backingbean.ct.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.constants.CommonConstants;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import java.util.concurrent.TimeUnit;

import org.primefaces.component.calendar.Calendar;


@FacesValidator("primeDateRangeValidatorForLopo")
public class PrimeDateRangeValidatorForLopo implements Validator{

	@Override
    public void validate(FacesContext context, UIComponent component, Object value) 
    		throws ValidatorException {
        
        Object startDateValue = component.getAttributes().get("startDate");
        
        if (startDateValue == null || startDateValue == "") {
            return;
        }       
        if (value == null || value == "") {
            return;
        }
        
        String sdate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT, 
        		(Date) startDateValue);
		String edate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT, 
				(Date) value);
		Date startDate;
		Date endDate;
		boolean dateValidation = true;
		long days = 0;
		
		try {
			startDate = DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate);
			endDate = DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate);
			long diff = endDate.getTime() - startDate.getTime();
			
			days = TimeUnit.MILLISECONDS.toDays(diff);
			System.out.println("days= "+days);
			
			if (days != 14) {
				
				dateValidation = false;
				
				/*startDate.setTime(startDate.getTime() + 
						TimeUnit.DAYS.toMillis(days));
				Date suggestedDate = startDate;*/
								
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!dateValidation) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,((Calendar)component).getLabel(), 
        		  ResourceBundleUtils.getMessage("lbl.ct.validation.expiry.date",
        				  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
		}
        
    }
}
