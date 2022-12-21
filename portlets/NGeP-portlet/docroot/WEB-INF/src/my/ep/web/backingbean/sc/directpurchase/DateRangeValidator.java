package my.ep.web.backingbean.sc.directpurchase;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import my.ep.constants.CommonConstants;
import my.ep.web.util.DateUtils;

@FacesValidator("dateRangeValidator")
public class DateRangeValidator implements Validator {
	
	/* (non-Javadoc)
	 * @see javax.faces.validator.Validator#validate(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		try {
			if (value == null) {
				return;
			}
			
			String cDate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,
					DateUtils.getCurrentDate());
			Date currentDate = DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, cDate);
			
			if (component.getAttributes().get("startDate") == null) {
				String sdate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT, (Date) value);
				Date startDate = DateUtils
						.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate);
				if (currentDate.after(startDate)) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"start date must be after current date.", ""));
					
				}
				
			} else {
				Object startDateValue = component.getAttributes().get(
						"startDate");
				String sdate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT,
						(Date) startDateValue);
				String edate = DateUtils.dateToString(CommonConstants.DEFAULT_DATE_FORMAT, (Date) value);
				Date startDate = DateUtils
						.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, sdate);
				Date endDate = DateUtils.convertStringToDate(CommonConstants.DEFAULT_DATE_FORMAT, edate);
				
				/*if (currentDate.after(startDate)) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"start date must be after current date.", ""));
				}*/
			
				if (endDate.before(startDate)) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"End date must be after start date.", ""));
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}