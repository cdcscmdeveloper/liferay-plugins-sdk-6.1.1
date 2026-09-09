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
	    Date dateValue = null;
	    FacesScopes facesScopes = new FacesScopes();

	    org.primefaces.component.calendar.Calendar calDateObj =
	            (org.primefaces.component.calendar.Calendar)
	                    facesScopes.findComponentById(component, "calDate");

	    Object dateObj = calDateObj.getValue();

	    HtmlSelectOneMenu hourMenu =
	            (HtmlSelectOneMenu)
	                    facesScopes.findComponentById(component, "calHour");

	    HtmlSelectOneMenu minuteMenu =
	            (HtmlSelectOneMenu)
	                    facesScopes.findComponentById(component, "calMin");

	    Object hourObj = hourMenu.getValue();
	    Object minuteObj = minuteMenu.getValue();

	    //#28188 - Start
	    if (dateObj != null) {

	        /*
	         * Always check whether the currently selected hour is still valid.
	         *
	         * This is important when postponing on CURRENT DATE.
	         *
	         * Example:
	         * Old BSV = 01/09/2026 09:00
	         * Current time = 01/09/2026 13:08
	         *
	         * Old hour 09 is no longer valid.
	         * The first available hour may be 13.
	         */
	        String firstAvailableHour = getFirstAvailableHour(component);

	        if (hourObj == null || StringUtils.isBlank(hourObj.toString())) {
	            hourObj = firstAvailableHour;
	        } else {
	            int selectedHour = Integer.parseInt(hourObj.toString());
	            int availableHour = Integer.parseInt(firstAvailableHour);

	            if (selectedHour < availableHour) {
	                hourObj = firstAvailableHour;
	            }
	        }

	        /*
	         * Check the minute against the selected/effective hour.
	         *
	         * This prevents an old minute such as 00 from being used when
	         * the current-day restriction requires 30, 45, etc.
	         */
	        String firstAvailableMinute = getFirstAvailableMinute(component);

	        if (minuteObj == null || StringUtils.isBlank(minuteObj.toString())) {
	            minuteObj = firstAvailableMinute;
	        } else {
	            int selectedMinute = Integer.parseInt(minuteObj.toString());
	            int availableMinute = Integer.parseInt(firstAvailableMinute);

	            /*
	             * Only replace the minute when it is below the minimum
	             * minute for the currently selected/effective hour.
	             */
	            if (selectedMinute < availableMinute) {
	                minuteObj = firstAvailableMinute;
	            }
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

	    Date bsvNormalMaintain = (Date) composite.getAttributes().get(
	            "checkBSVNormalMain");

	    Date minDate = (Date) composite.getAttributes().get(
	            "restrictionDateTime");

	    FacesScopes facesScopes = new FacesScopes();

	    Object hourObj = ((HtmlSelectOneMenu) facesScopes.findComponentById(
	            composite, "calHour")).getValue();

	    /*
	     * Get the selected calendar date first.
	     */
	    Date selectedDate = getSelectedCalendarDate(composite, minDate);

	    /*
	     * Use exactly the same restriction as hour dropdown.
	     *
	     * IMPORTANT:
	     * Keep this unchanged so the future-date fix continues to work.
	     */
	    Date effectiveMinDate = getEffectiveBSVRestriction(composite, minDate,
	            selectedDate);

	    int modValue = Integer.parseInt(composite.getAttributes().get("modVal")
	            .toString());

	    /*
	     * Determine the first valid hour.
	     *
	     * If the existing hour is older than the valid hour, use the valid hour
	     * for minute calculation.
	     *
	     * Example:
	     *
	     * Old BSV       = 09:00
	     * Current time  = 13:08
	     * Valid hour    = 13
	     *
	     * Even if calHour still contains 09, calculate minutes using 13.
	     */
	    String firstAvailableHour = getFirstAvailableHour(component);

	    if (hourObj == null || StringUtils.isBlank(hourObj.toString())) {
	        hourObj = firstAvailableHour;
	    } else {
	        int selectedHour = Integer.parseInt(hourObj.toString());
	        int availableHour = Integer.parseInt(firstAvailableHour);

	        if (selectedHour < availableHour) {
	            hourObj = firstAvailableHour;
	        }
	    }

	    int minMinute = 0;

	    if (effectiveMinDate != null && selectedDate != null && hourObj != null) {

	        Calendar calMin = Calendar.getInstance();
	        calMin.setTime(effectiveMinDate);

	        Calendar calSel = Calendar.getInstance();
	        calSel.setTime(selectedDate);

	        boolean sameDay =
	                calMin.get(Calendar.YEAR) == calSel.get(Calendar.YEAR)
	                &&
	                calMin.get(Calendar.DAY_OF_YEAR)
	                        == calSel.get(Calendar.DAY_OF_YEAR);

	        int selectedHour = Integer.parseInt(hourObj.toString());

	        //#28188 - Start
	        if (sameDay) {

	            int oldHour = calMin.get(Calendar.HOUR_OF_DAY);
	            int oldMinute = calMin.get(Calendar.MINUTE);

	            int nextMinute = oldMinute;

	            /*
	             * Postpone/new BSV:
	             * move one interval forward.
	             *
	             * Keep your existing logic.
	             */
	            if (bsvNormalMaintain == null) {
	                nextMinute = oldMinute + modValue;

	                /*
	                 * Round UP to next valid slot.
	                 *
	                 * Example:
	                 * 13:08 + 15 = 13:23
	                 * Rounded = 13:30
	                 */
	                if (nextMinute % modValue != 0) {
	                    nextMinute = ((nextMinute / modValue) + 1)
	                            * modValue;
	                }
	            }

	            /*
	             * Rollover case.
	             *
	             * Example:
	             * 13:50 + 15 = 14:05
	             * Minimum hour becomes 14.
	             *
	             * Hour dropdown already starts from 14,
	             * therefore minutes start from 00.
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
	                 * User selected a later hour.
	                 * All minute slots are available.
	                 */
	                minMinute = 0;
	            }
	        } else {
	            /*
	             * Different/future date:
	             * keep ALL minute slots available.
	             *
	             * This is important for your Video 1 fix.
	             */
	            minMinute = 0;
	        }
	        //#28188 - End
	    }

	    /*
	     * Safety:
	     * never allow 60 to become the starting minute.
	     */
	    if (minMinute >= 60) {
	        minMinute = 0;
	    }

	    /*
	     * Build allowed minute list.
	     */
	    for (int i = minMinute; i < 60; i++) {
	        if (i % modValue == 0) {
	            minutes.add(new SelectItem(
	                    String.valueOf(i),
	                    String.format("%02d", i)));
	        }
	    }

	    /*
	     * Restore existing BSV minute only when it is actually valid
	     * for the currently displayed/effective hour.
	     */
	    if (selectedDate != null && hourObj != null) {

	        Calendar existingCal = Calendar.getInstance();
	        existingCal.setTime(selectedDate);

	        int existingHour = existingCal.get(Calendar.HOUR_OF_DAY);
	        int existingMinute = existingCal.get(Calendar.MINUTE);
	        int displayedHour = Integer.parseInt(hourObj.toString());

	        /*
	         * Only restore the existing minute when:
	         *
	         * 1. the displayed hour is the existing BSV hour, AND
	         * 2. the existing minute is not earlier than the calculated minimum.
	         *
	         * This prevents an old minute such as 00 from being inserted back
	         * into a current-date postpone selection that requires 30 or 45.
	         */
	        	
        	if (displayedHour == existingHour
        	        && existingMinute >= minMinute
        	        && existingMinute % modValue == 0) {
	 
	            boolean minuteFound = false;

	            for (SelectItem item : minutes) {
	                if (item.getValue() != null
	                        && Integer.parseInt(item.getValue().toString())
	                                == existingMinute) {

	                    minuteFound = true;
	                    break;
	                }
	            }

	            if (!minuteFound) {
	                minutes.add(0,
	                        new SelectItem(
	                                String.valueOf(existingMinute),
	                                String.format("%02d", existingMinute)));
	            }
	        }
	    }

	    //#28188 - End

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

	            /*
	             * For postpone on CURRENT DATE, always move to the
	             * NEXT valid interval.
	             *
	             */
	            int modValue = Integer.parseInt(
	                    composite.getAttributes().get("modVal").toString());

	            return getFirstAvailableBSVTime(now, modValue);
	        }
	    }

	    return restrictionDateTime;
	}
	//#28188 - End
	
	//#28188 - Start
	/**
	 * Gets the first valid BSV time based on the current minute.
	 *
	 * This is only used when postponing to the current date.
	 *
	 */
	private Date getFirstAvailableBSVTime(Date restrictionDateTime,
	        int modValue) {

	    if (restrictionDateTime == null) {
	        return null;
	    }

	    Calendar cal = Calendar.getInstance();
	    cal.setTime(restrictionDateTime);

	    int minute = cal.get(Calendar.MINUTE);

	    /*
	     * Move to the next valid interval.
	     */
	    int nextMinute = ((minute / modValue) + 1) * modValue;

	    if (nextMinute >= 60) {
	        cal.add(Calendar.HOUR_OF_DAY, 1);
	        cal.set(Calendar.MINUTE, 0);
	    } else {
	        cal.set(Calendar.MINUTE, nextMinute);
	    }

	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);

	    return cal.getTime();
	}
	//#28188 - End


}
