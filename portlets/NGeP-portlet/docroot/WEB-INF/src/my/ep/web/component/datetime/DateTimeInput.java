package my.ep.web.component.datetime;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

/**
 * Date Time UI Component
 * 
 * @author Kenny
 * @since 18.12.2015
 */
@FacesComponent("ac.DateTimeInput")
public class DateTimeInput extends UIOutput implements ClientBehaviorHolder {

	private static Logger log = Logger.getLogger(DateTimeInput.class);
	
	public final static String COMPONENT_TYPE = "ac.DateTimeInput";

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}log.info(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIInput#getFamily()
	 */
	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Object dateObj = this.getValue();
		FacesScopes facesScopes = new FacesScopes();
		org.primefaces.component.calendar.Calendar dateComponent = ((org.primefaces.component.calendar.Calendar)facesScopes.findComponentById(this, "calDate"));
		HtmlSelectOneMenu hourComponent = ((HtmlSelectOneMenu)facesScopes.findComponentById(this, "calHour"));
		HtmlSelectOneMenu minuteComponent = ((HtmlSelectOneMenu)facesScopes.findComponentById(this, "calMin"));	
		
		if (dateObj != null) {
			debugLogger("set date time: " + dateObj);
			Date dateValue = (Date)dateObj;
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime(dateValue);
			
			dateComponent.setValue(c1.getTime());
			hourComponent.setValue(String.valueOf(c1.get(Calendar.HOUR_OF_DAY)));
			minuteComponent.setValue(String.valueOf(c1.get(Calendar.MINUTE)));
		}/* Kenny - cannot reset to null due to issue if date is selected and hour/minute not selected, 
			system will reset to null so selected date is gone during rerender.
			else {
			dateComponent.setValue(null);
			hourComponent.setValue(null);
			minuteComponent.setValue(null);
		}*/
		
		super.encodeBegin(context);
	}
	
	public void resetDateTime(){
		FacesScopes facesScopes = new FacesScopes();
		org.primefaces.component.calendar.Calendar dateComponent = ((org.primefaces.component.calendar.Calendar)facesScopes.findComponentById(this, "calDate"));
		HtmlSelectOneMenu hourComponent = ((HtmlSelectOneMenu)facesScopes.findComponentById(this, "calHour"));
		HtmlSelectOneMenu minuteComponent = ((HtmlSelectOneMenu)facesScopes.findComponentById(this, "calMin"));	
		dateComponent.resetValue();
		hourComponent.resetValue();;
		minuteComponent.resetValue();
	}
}
