package my.ep.web.managedbean.pm.settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "calendarSchedulerMB")
@SessionScoped
public class CalendarManageBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ScheduleModel eventModel;
	private Long selectedStateId;
	private PmState selectedState;
	private ScheduleEvent event;
	private Map<String, String> eventToExTitleValueMap = new HashMap<String, String>();
	private boolean isNewEvent;
	private Date initialDate;

	public Map<String, String> getEventToExTitleValueMap() {
		return eventToExTitleValueMap;
	}

	public void setEventToExTitleValueMap(Map<String, String> eventToExTitleValueMap) {
		this.eventToExTitleValueMap = eventToExTitleValueMap;
	}
	
	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public Long getSelectedStateId() {
		return selectedStateId;
	}

	public void setSelectedStateId(Long selectedStateId) {
		this.selectedStateId = selectedStateId;
	}

	public PmState getSelectedState() {
		return selectedState;
	}

	public void setSelectedState(PmState selectedState) {
		this.selectedState = selectedState;
	}

	/**
	 * @return the isNewEvent
	 */
	public boolean isNewEvent() {
		return isNewEvent;
	}

	/**
	 * @param isNewEvent the isNewEvent to set
	 */
	public void setNewEvent(boolean isNewEvent) {
		this.isNewEvent = isNewEvent;
	}

	/**
	 * @return the initialDate
	 */
	public Date getInitialDate() {
		return initialDate;
	}

	/**
	 * @param initialDate the initialDate to set
	 */
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}
	
}
