package my.ep.web.backingbean.components.datetime;

import java.util.Calendar;
import java.util.Date;

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
	
	public SelectItem[] getMinutesDropdownValues() {
		SelectItem[] selectItems = new SelectItem[61];
		
		selectItems[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				"lbl.txt.minute", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
		
		for (int i = 0; i < selectItems.length-1; i++) {
			selectItems[i+1] = new SelectItem(i, String.format("%1$02d", i));
		}
		
		return selectItems;
	}
	
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
}
