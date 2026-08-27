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
	
	/**
	 * Gets the hours dropdown values.
	 *
	 * @return the hours dropdown values
	 */
	public SelectItem[] getHoursDropdownValues() {
		SelectItem[] selectItems = new SelectItem[25];
		
		selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.hour", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		
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
	public SelectItem[] getMinutesDropdownValues(String modVal) {
		if ((StringUtils.isNotEmpty(modVal))){
			int modValue = Integer.valueOf(modVal);
			SelectItem[] selectItems = new SelectItem[((60/modValue)+1)];

			selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.minute", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));

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
		UIComponent composite = UIComponent
				.getCurrentCompositeComponent(context);

		Date bsvNormalMaintain = (Date) composite.getAttributes().get(
				"checkBSVNormalMain");

		Date minDate = (Date) composite.getAttributes().get(
				"restrictionDateTime");

		Date selectedDate = getSelectedCalendarDate(composite, minDate);

		// 28188 -start-
		/*
		 * Use effective restriction: - existing BSV -> retain existing
		 * restriction - postpone today -> use current time if later
		 */
		Date effectiveMinDate = getEffectiveBSVRestriction(composite, minDate,
				selectedDate);
		// 28188 -end-

		int minHour = 0;

		// 28188 -start-
		if (effectiveMinDate != null && selectedDate != null) {
			Calendar calMin = Calendar.getInstance();
			calMin.setTime(effectiveMinDate);
			Calendar calSel = Calendar.getInstance();
			calSel.setTime(selectedDate);

			boolean sameDay = calMin.get(Calendar.YEAR) == calSel
					.get(Calendar.YEAR)
					&& calMin.get(Calendar.DAY_OF_YEAR) == calSel
					.get(Calendar.DAY_OF_YEAR);

			if (sameDay) {
				int modValue = Integer.parseInt(composite.getAttributes()
						.get("modVal").toString());
				int oldHour = calMin.get(Calendar.HOUR_OF_DAY);
				int oldMinute = calMin.get(Calendar.MINUTE);
				if (bsvNormalMaintain != null) {
					/*
					 * Existing/edit BSV: retain existing hour.
					 */
					minHour = oldHour;
				} else {
					/*
					 * Postpone/new BSV: add minimum interval first.
					 */
					int nextMinute = oldMinute + modValue;
					/*
					 * Round UP to next valid slot.
					 * 
					 * Example: 15:41 + 15 = 15:56 rounded = 16:00
					 */
					if (nextMinute % modValue != 0) {
						nextMinute = ((nextMinute / modValue) + 1) * modValue;
					}
					/*
					 * If minute rolls to 60 or above, minimum hour becomes next
					 * hour.
					 */
					if (nextMinute >= 60) {
						minHour = oldHour + 1;
					} else {
						minHour = oldHour;
					}
				}
			} else {
				minHour = 0;
			}
		}
		// 28188 -end-

		/*
		 * Safety for end of day.
		 */
		if (minHour > 23) {
			minHour = 23;
		}

		for (int i = minHour; i < 24; i++) {

			hours.add(new SelectItem(String.valueOf(i), String
					.format("%02d", i)));
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
		UIComponent composite = UIComponent
				.getCurrentCompositeComponent(context);

		// 28188 -start-
		Date bsvNormalMaintain = (Date) composite.getAttributes().get(
				"checkBSVNormalMain");
		// 28188 -end-

		Date minDate = (Date) composite.getAttributes().get(
				"restrictionDateTime");

		FacesScopes facesScopes = new FacesScopes();

		Object hourObj = ((HtmlSelectOneMenu) facesScopes.findComponentById(
				composite, "calHour")).getValue();

		if (hourObj == null || StringUtils.isBlank(hourObj.toString())) {
			hourObj = getFirstAvailableHour(component);
		}

		Date selectedDate = getSelectedCalendarDate(composite, minDate);

		// 28188 -start-
		/*
		 * Use exactly the same restriction as hour dropdown.
		 */
		Date effectiveMinDate = getEffectiveBSVRestriction(composite, minDate,
				selectedDate);
		// 28188 -end-

		int modValue = Integer.parseInt(composite.getAttributes().get("modVal")
				.toString());

		int minMinute = 0;

		if (effectiveMinDate != null && selectedDate != null && hourObj != null) {
			Calendar calMin = Calendar.getInstance();
			calMin.setTime(effectiveMinDate);
			Calendar calSel = Calendar.getInstance();
			calSel.setTime(selectedDate);

			boolean sameDay = calMin.get(Calendar.YEAR) == calSel
					.get(Calendar.YEAR)
					&& calMin.get(Calendar.DAY_OF_YEAR) == calSel
					.get(Calendar.DAY_OF_YEAR);

			int selectedHour = Integer.parseInt(hourObj.toString());

			// 28188 -start-
			if (sameDay) {
				int oldHour = calMin.get(Calendar.HOUR_OF_DAY);
				int oldMinute = calMin.get(Calendar.MINUTE);
				int nextMinute = oldMinute;

				/*
				 * Postpone/new BSV: move one interval forward.
				 */
				if (bsvNormalMaintain == null) {
					nextMinute = oldMinute + modValue;
					/*
					 * Round UP to next valid slot.
					 * 
					 * Example: 41 + 15 = 56 56 -> 60
					 */
					if (nextMinute % modValue != 0) {
						nextMinute = ((nextMinute / modValue) + 1) * modValue;
					}
				}

				/*
				 * Rollover case.
				 * 
				 * Example: 15:41 -> 16:00
				 * 
				 * Hour dropdown starts from 16, therefore all minute slots must
				 * be available.
				 */
				if (nextMinute >= 60) {
					minMinute = 0;
				} else if (selectedHour <= oldHour) {
					/*
					 * Same minimum hour.
					 */
					minMinute = nextMinute;
				} else {
					/*
					 * User selected a later hour. Allow all minute slots.
					 */
					minMinute = 0;
				}
			} else {
				/*
				 * Different/future date: allow all minute slots.
				 */
				minMinute = 0;
			}
			// 28188 -end-
		}

		/*
		 * Safety: never allow 60 to become starting minute, otherwise dropdown
		 * becomes empty.
		 */
		if (minMinute >= 60) {
			minMinute = 0;
		}

		/*
		 * Build allowed minute list.
		 */
		for (int i = minMinute; i < 60; i++) {
			if (i % modValue == 0) {
				minutes.add(new SelectItem(String.valueOf(i), String.format(
						"%02d", i)));
			}
		}

		// 28188 -start-
		/*
		 * Restore existing BSV minute.
		 * 
		 * Required when reopening the popup because calMin local value may not
		 * yet be available.
		 */
		if (selectedDate != null && hourObj != null) {
			Calendar existingCal = Calendar.getInstance();
			existingCal.setTime(selectedDate);

			int existingHour = existingCal.get(Calendar.HOUR_OF_DAY);
			int existingMinute = existingCal.get(Calendar.MINUTE);
			int displayedHour = Integer.parseInt(hourObj.toString());

			/*
			 * Only restore the minute when the displayed hour matches existing
			 * BSV hour.
			 */
			if (displayedHour == existingHour) {
				boolean minuteFound = false;
				for (SelectItem item : minutes) {
					if (item.getValue() != null
							&& Integer.parseInt(item.getValue().toString()) == existingMinute) {
						minuteFound = true;
						break;
					}
				}
				/*
				 * Existing minute was filtered out. Put it back so JSF can
				 * display it.
				 */
				if (!minuteFound) {
					minutes.add(0,
							new SelectItem(String.valueOf(existingMinute),
									String.format("%02d", existingMinute)));
				}
			}
		}
		// 28188 -end-
		
		return minutes;
	}
	//#28188 - End

	//#28188 - Start
	private Date getSelectedCalendarDate(UIComponent composite, Date fallbackDate) {
	    FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	        (org.primefaces.component.calendar.Calendar)
	            facesScopes.findComponentById(composite, "calDate");

	    if (calDateObj != null) {

	        Object value = calDateObj.getLocalValue();

	        if (value == null) {
	            value = calDateObj.getValue();
	        }

	        if (value instanceof Date) {
	            return (Date) value;
	        }
	    }

	    // For existing/edit record, use composite value first.
	    Object componentValue = composite.getAttributes().get("value");

	    if (componentValue instanceof Date) {
	        return (Date) componentValue;
	    }

	    // Only use restriction as last fallback.
	    return fallbackDate;
	}
	//#28188 - End
	
	//#28188 - Start
	private Date getEffectiveBSVRestriction(UIComponent composite,Date restrictionDateTime,
	        Date selectedDate) {

	    if (selectedDate == null) {
	        return restrictionDateTime;
	    }

	    Date bsvNormalMaintain =
	            (Date) composite.getAttributes().get("checkBSVNormalMain");

	    Calendar selectedCal = Calendar.getInstance();
	    selectedCal.setTime(selectedDate);

	    Calendar todayCal = Calendar.getInstance();

	    boolean selectedToday =
	            selectedCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)
	            &&
	            selectedCal.get(Calendar.DAY_OF_YEAR)
	                    == todayCal.get(Calendar.DAY_OF_YEAR);

	    /*
	     * Normal existing/edit record.
	     * Do NOT replace its original time.
	     */
	    if (bsvNormalMaintain != null) {
	        return restrictionDateTime;
	    }

	    /*
	     * Postpone mode + selected date is today.
	     *
	     * Must not use old BSV time when current
	     * time is already later.
	     */
	    if (selectedToday) {

	        Date now = new Date();

	        if (restrictionDateTime == null ||
	                now.after(restrictionDateTime)) {

	            return now;
	        }
	    }

	    return restrictionDateTime;
	}
	//#28188 - End

}
