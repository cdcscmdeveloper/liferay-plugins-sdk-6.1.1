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
	public Date timeChangedAction(UIComponent component) {
		debugLogger("timeChangedAction");
		
		Date dateValue = null;
		FacesScopes facesScopes = new FacesScopes();
		org.primefaces.component.calendar.Calendar calDateObj = (org.primefaces.component.calendar.Calendar)facesScopes.findComponentById(component, "calDate");
		Object dateObj = calDateObj.getValue();
		Object hourObj = ((HtmlSelectOneMenu)facesScopes.findComponentById(component, "calHour")).getValue();
		Object minuteObj = ((HtmlSelectOneMenu)facesScopes.findComponentById(component, "calMin")).getValue();
		
		if (dateObj != null && hourObj != null && minuteObj != null) {
			int hourValue = Integer.parseInt((String)hourObj);
			int minuteValue = Integer.parseInt((String)minuteObj);
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime((Date)dateObj);
			c1.set(Calendar.HOUR_OF_DAY, hourValue);
			c1.set(Calendar.MINUTE, minuteValue);
			
			dateValue = c1.getTime();
		}
		
		return dateValue;
	}
	
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
	public Date timeChangedActionBSV(UIComponent component) {
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

	    //#28188 - Start
	    if (dateObj != null){
	        if (hourObj == null || StringUtils.isBlank(hourObj.toString())) {
	            //hourObj = "00";
	        	hourObj = getFirstAvailableHour(component);
	        }
	        
	        if (minuteObj == null || StringUtils.isBlank(minuteObj.toString())) {
	            minuteObj = getFirstAvailableMinute(component);
	        }

	        int hourValue = Integer.parseInt(hourObj.toString());
	        int minuteValue = Integer.parseInt(minuteObj.toString());

	        Calendar selectedCal = Calendar.getInstance();
	        selectedCal.setTime((Date) dateObj);
	        selectedCal.set(Calendar.HOUR_OF_DAY, hourValue);
	        selectedCal.set(Calendar.MINUTE, minuteValue);
	        selectedCal.set(Calendar.SECOND, 0);
	        selectedCal.set(Calendar.MILLISECOND, 0);

	        dateValue = selectedCal.getTime();
	    }
	    //#28188 - End
	    
	    return dateValue;
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Hours dropdown for selected date BSV
	 *
	 * @param component the component
	 * @return the date
	 */
	public List<SelectItem> getHoursDropdownValuesBSV(UIComponent component) {
	    List<SelectItem> hours = new ArrayList<>();

	    FacesContext context = FacesContext.getCurrentInstance();
	    UIComponent composite =
	            UIComponent.getCurrentCompositeComponent(context);

	    
	    Date minDate = (Date) composite.getAttributes().get("restrictionDateTime");
	    
	    /*FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	            (org.primefaces.component.calendar.Calendar)
	                    facesScopes.findComponentById(composite, "calDate");*/

	    
	    Date selectedDate = getSelectedCalendarDate(composite, minDate);

	    log.info("MIN DATE = " + minDate);
	    log.info("SELECTED DATE = " + selectedDate);

	    int minHour = 0;
	    
	    //#28188 - Start
	    if (minDate != null && selectedDate != null) {

	        Calendar calMin = Calendar.getInstance();
	        calMin.setTime(minDate);

	        Calendar calSel = Calendar.getInstance();
	        calSel.setTime(selectedDate);

	        boolean sameDay =
	            calMin.get(Calendar.YEAR) == calSel.get(Calendar.YEAR)
	            &&
	            calMin.get(Calendar.DAY_OF_YEAR) == calSel.get(Calendar.DAY_OF_YEAR);
	        
	        if (sameDay) {

	            int modValue = Integer.parseInt(
	                composite.getAttributes().get("modVal").toString()
	            );

	            int oldHour = calMin.get(Calendar.HOUR_OF_DAY);
	            int oldMinute = calMin.get(Calendar.MINUTE);

	            int lastMinuteSlot = 60 - modValue; // 45 if modVal = 15

	            if (oldMinute >= lastMinuteSlot) {
	                minHour = oldHour + 1;
	            } else {
	                minHour = oldHour;
	            }

	        } else {
	            minHour = 0;
	        }
	    }
	    //#28188 - End

	    for (int i = minHour; i < 24; i++) {
	        hours.add(new SelectItem(
	                String.format("%02d", i),
	                String.format("%02d", i)
	        ));
	    }

	    return hours;
	}
	//#28188 - End
	
	//#28188 - Start
	private String getFirstAvailableHour(UIComponent component) {
	    List<SelectItem> hours = getHoursDropdownValuesBSV(component);

	    for (SelectItem item : hours) {
	        if (item.getValue() != null) {
	            return item.getValue().toString();
	        }
	    }

	    return "00";
	}
	//#28188 - End
	
	//#28188 - Start
	private String getFirstAvailableMinute(UIComponent component) {
	    List<SelectItem> minutes = getMinutesDropdownValuesBSV(component);

	    for (SelectItem item : minutes) {
	        if (item.getValue() != null) {
	            return item.getValue().toString();
	        }
	    }

	    return "00";
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Gets the minutes dropdown values for BSV.
	 *
	 * @param UIComponent the component
	 * @return the minutes dropdown values
	 */
	public List<SelectItem> getMinutesDropdownValuesBSV(UIComponent component) {
	    List<SelectItem> minutes = new ArrayList<>();

	    FacesContext context = FacesContext.getCurrentInstance();
	    UIComponent composite =
	            UIComponent.getCurrentCompositeComponent(context);

		// 28188 -start-

		Date bsvNormalMaintain = (Date) composite.getAttributes().get(
				"checkBSVNormalMain");
		// 28188 -end-
	    
	    Date minDate = (Date) composite.getAttributes().get("restrictionDateTime");

	    FacesScopes facesScopes = new FacesScopes();

	    /*org.primefaces.component.calendar.Calendar calDateObj =
	            (org.primefaces.component.calendar.Calendar)
	                    facesScopes.findComponentById(composite, "calDate");*/

	    Object hourObj =
	            ((HtmlSelectOneMenu)
	                    facesScopes.findComponentById(composite, "calHour"))
	                    .getValue();

	    if (hourObj == null || StringUtils.isBlank(hourObj.toString())) {
	        hourObj = getFirstAvailableHour(component);
	    }
	    
	    Date selectedDate = getSelectedCalendarDate(composite, minDate);

	    int modValue = Integer.parseInt(
	            composite.getAttributes().get("modVal").toString()
	    );

	    int minMinute = 0;

	    if (minDate != null && selectedDate != null && hourObj != null) {

	        Calendar calMin = Calendar.getInstance();
	        calMin.setTime(minDate);

	        Calendar calSel = Calendar.getInstance();
	        calSel.setTime(selectedDate);

	        boolean sameDay =
	                calMin.get(Calendar.YEAR) == calSel.get(Calendar.YEAR)
	                        && calMin.get(Calendar.DAY_OF_YEAR) == calSel.get(Calendar.DAY_OF_YEAR);

	        int selectedHour = Integer.parseInt(hourObj.toString());


	        //#28188 - Start
	        if (sameDay) {

	            int oldHour = calMin.get(Calendar.HOUR_OF_DAY);
	            int oldMinute = calMin.get(Calendar.MINUTE);
	            
				// 28188 -start-
				int nextMinute = oldMinute;
				if (null == bsvNormalMaintain) {
					nextMinute = oldMinute + modValue;
				}
				// 28188 -end-
                
	            if (nextMinute >= 60) {
	                /*
	                 * Old time is at last minute slot, example 07:45.
	                 * Next valid time is next hour 08:00.
	                 */
	                if (selectedHour <= oldHour) {
	                    selectedHour = oldHour + 1;
	                }

	                minMinute = 0;

	            } else if (selectedHour <= oldHour) {

	                minMinute = nextMinute;

	                if (minMinute % modValue != 0) {
	                    minMinute = ((minMinute / modValue) + 1) * modValue;
	                }

	            } else {
	                minMinute = 0;
	            }

	        } else {
	            minMinute = 0;
	        }
	        
	        //#28188 - End
	    }

	    for (int i = minMinute; i < 60; i++) {
	        if (i % modValue == 0) {
	            minutes.add(new SelectItem(
	                    String.format("%02d", i),
	                    String.format("%02d", i)
	            ));
	        }
	    }

	    return minutes;
	}
	//#28188 - End

	//#28188 - Start
	private Date getSelectedCalendarDate(UIComponent composite, Date fallbackDate) {
	    FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	        (org.primefaces.component.calendar.Calendar)
	            facesScopes.findComponentById(composite, "calDate");

	    if (calDateObj == null) {
	        return fallbackDate;
	    }

	    Object value = calDateObj.getLocalValue();

	    if (value == null) {
	        value = calDateObj.getValue();
	    }

	    if (value instanceof Date) {
	        return (Date) value;
	    }

	    // IMPORTANT: if calendar value not available during AJAX/render,
	    // use restrictionDateTime date as selected date
	    return fallbackDate;
	}
	//#28188 - End
}
