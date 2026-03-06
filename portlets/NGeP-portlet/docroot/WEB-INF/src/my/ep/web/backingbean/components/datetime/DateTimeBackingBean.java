package my.ep.web.backingbean.components.datetime;

import java.util.ArrayList;//#28188
import java.util.Calendar;
import java.util.Date;
import java.util.List;//#28188

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.util.ResourceBundleUtils;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Backing Bean For Date Time Component
 * 
 * @author Kenny
 * @since 07.12.2015
 */
@ManagedBean(name = "dateTimeBackingBean")
@RequestScoped
public class DateTimeBackingBean {

	private static Logger log = Logger.getLogger(DateTimeBackingBean.class);

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}log.info(message);
	}
	//#29549 start
	/**
	 * Gets the hours dropdown values.
	 *
	 * @return the hours dropdown values without s(hour)
	 */
	public SelectItem[] getHoursDropdownValues() {
		return getHoursDropdownValues(false);
	}
	
	/**
	 * Gets the minutes dropdown values.
	 *
	 * @param modVal the mod val
	 * @return the minutes dropdown values without s (minute)
	 */
	public SelectItem[] getMinutesDropdownValues(String modVal) {
		return getMinutesDropdownValues(modVal, false);
	}
	/**
	 * Gets the hours dropdown values.
	 *
	 * @return the hours dropdown values
	 */
	public SelectItem[] getHoursDropdownValues(boolean withs) {
		SelectItem[] selectItems = new SelectItem[25];
		
		if(withs){
			selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.hours", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}else{
			selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.hour", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		
		for (int i = 0; i < selectItems.length-1; i++) {
			selectItems[i+1] = new SelectItem(i, String.format("%1$02d", i));
		}
		
		return selectItems;
	}
	
	/**
	 * Gets the minutes dropdown values.
	 *
	 * @param modVal the mod val
	 * @return the minutes dropdown values
	 */
	public SelectItem[] getMinutesDropdownValues(String modVal,boolean withs) {
		if ((StringUtils.isNotEmpty(modVal))){
			int modValue = Integer.valueOf(modVal);
			SelectItem[] selectItems = new SelectItem[((60/modValue)+1)];
			
			if(withs){
				selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.minutes", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
			}else{
				selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.minute", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));
			}
			
			int j = 1;
			for (int i = 0; i < 60; i++) {

				if ((i % modValue ) == 0){
					selectItems[j] = new SelectItem(i, String.format("%1$02d", i));
					j++;
				}
			}

			return selectItems;
		}
		return null;
	}
	//#29549 end
	/**
	 * Time changed action.
	 *
	 * @param component the component
	 * @return the date
	 */
//	public Date timeChangedAction(UIComponent component) {
//		debugLogger("timeChangedAction");
//		
//		Date dateValue = null;
//		FacesScopes facesScopes = new FacesScopes();
//		org.primefaces.component.calendar.Calendar calDateObj = (org.primefaces.component.calendar.Calendar)facesScopes.findComponentById(component, "calDate");
//		Object dateObj = calDateObj.getValue();
//		Object hourObj = ((HtmlSelectOneMenu)facesScopes.findComponentById(component, "calHour")).getValue();
//		Object minuteObj = ((HtmlSelectOneMenu)facesScopes.findComponentById(component, "calMin")).getValue();
//		
//		if (dateObj != null && hourObj != null && minuteObj != null) {
//			int hourValue = Integer.parseInt((String)hourObj);
//			int minuteValue = Integer.parseInt((String)minuteObj);
//			
//			Calendar c1 = Calendar.getInstance();
//			c1.setTime((Date)dateObj);
//			c1.set(Calendar.HOUR_OF_DAY, hourValue);
//			c1.set(Calendar.MINUTE, minuteValue);
//			
//			dateValue = c1.getTime();
//		}
//		
//		return dateValue;
//	}
	

	
	/**
	 * Post update listener.
	 *
	 * @param postUpdateListener the post update listener
	 */
	public void postUpdateListener(String postUpdateListener) {
		if (StringUtils.isNotBlank(postUpdateListener)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		postUpdateListener, null,
	                		new Class[] {});
			
			methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{});
		}
	}
	
	//#28188 - Start
	/**
	 * Time changed action for selected date
	 *
	 * @param component the component
	 * @return the date
	 */
	public Date timeChangedAction(UIComponent component) {
		debugLogger("timeChangedAction");

	    Date dateValue = null;
	    FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	            (org.primefaces.component.calendar.Calendar)
	                    facesScopes.findComponentById(component, "calDate");

	    Object dateObj = calDateObj.getValue();

	    Object hourObj =
	            ((HtmlSelectOneMenu)
	                    facesScopes.findComponentById(component, "calHour"))
	                    .getValue();

	    Object minuteObj =
	            ((HtmlSelectOneMenu)
	                    facesScopes.findComponentById(component, "calMin"))
	                    .getValue();

	    if (dateObj != null && hourObj != null && minuteObj != null) {
	        int hourValue = Integer.parseInt((String) hourObj);
	        int minuteValue = Integer.parseInt((String) minuteObj);

	        Calendar selectedCal = Calendar.getInstance();
	        selectedCal.setTime((Date) dateObj);
	        selectedCal.set(Calendar.HOUR_OF_DAY, hourValue);
	        selectedCal.set(Calendar.MINUTE, minuteValue);

	        UIComponent composite =
	                UIComponent.getCurrentCompositeComponent(
	                        FacesContext.getCurrentInstance());

	        Date minDate = (Date) composite.getAttributes().get("mindate");

	        if (minDate != null) {
	            Calendar minCal = Calendar.getInstance();
	            minCal.setTime(minDate);

	            boolean sameDay =
	                    selectedCal.get(Calendar.YEAR) == minCal.get(Calendar.YEAR) &&
	                    selectedCal.get(Calendar.DAY_OF_YEAR) == minCal.get(Calendar.DAY_OF_YEAR);

	            if (sameDay &&
	                selectedCal.get(Calendar.HOUR_OF_DAY) <
	                minCal.get(Calendar.HOUR_OF_DAY)) {

	                selectedCal.set(Calendar.HOUR_OF_DAY,
	                        minCal.get(Calendar.HOUR_OF_DAY));

	                selectedCal.set(Calendar.MINUTE,
	                        minCal.get(Calendar.MINUTE));
	            }
	        }
	        dateValue = selectedCal.getTime();
	    }
	    return dateValue;
	}
	
	public List<SelectItem> getHoursDropdownValues(UIComponent component) {
	    List<SelectItem> hours = new ArrayList<>();

	    FacesContext context = FacesContext.getCurrentInstance();
	    UIComponent composite =
	            UIComponent.getCurrentCompositeComponent(context);

	    Date minDate = (Date) composite.getAttributes().get("mindate");
	    
	    FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	            (org.primefaces.component.calendar.Calendar)
	                    facesScopes.findComponentById(composite, "calDate");

	    Date selectedDate = null;

	    if (calDateObj != null) {
	        // FIRST check local value
	        Object localValue = calDateObj.getValue();

	        if (localValue instanceof Date) {
	            selectedDate = (Date) localValue;
	        }
	    }


	    log.info("MIN DATE = " + minDate);
	    log.info("SELECTED DATE = " + selectedDate);

	    int minHour = 0;

	    if (minDate != null && selectedDate != null) {

	        Calendar calMin = Calendar.getInstance();
	        calMin.setTime(minDate);

	        Calendar calSel = Calendar.getInstance();
	        calSel.setTime(selectedDate);

	        boolean sameDay =
	                calMin.get(Calendar.YEAR) == calSel.get(Calendar.YEAR) &&
	                calMin.get(Calendar.DAY_OF_YEAR) == calSel.get(Calendar.DAY_OF_YEAR);

	        if (sameDay) {
	            minHour = calMin.get(Calendar.HOUR_OF_DAY);
	        }
	    }

	    for (int i = minHour; i < 24; i++) {
	        hours.add(new SelectItem(
	                String.format("%02d", i),
	                String.format("%02d", i)
	        ));
	    }

	    return hours;
	}
	//#28188 - End
	
}
