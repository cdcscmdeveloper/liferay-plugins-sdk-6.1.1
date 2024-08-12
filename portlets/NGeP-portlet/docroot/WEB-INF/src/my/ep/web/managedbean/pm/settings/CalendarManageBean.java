/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.settings;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Calendar
 * 
 * @author jylee
 * @since 27/5/2015
 * 
 */
@ManagedBean(name = "calendarSchedulerMB")
@SessionScoped
public class CalendarManageBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ScheduleModel eventModel;
	private Long selectedStateId;
	private PmState selectedState;
	private ScheduleEvent event;
	private Map<String, String> eventToExTitleValueMap = new HashMap<String, String>();
	private boolean isNewEvent;
	private Date initialDate;
	private boolean disabledUpdate;
	private boolean deletedEvent = false;
	private CalendarResDTO resCalendarDTO ;
	private CalendarReqDTO reqCalendarDTO;
	private String newTitleValue;	
	private Date newStartDate;
	private Date newEndDate;
	private String OldTitleValue;
	private Date oldEndDate;
	private Date oldStartDate;

	public CalendarManageBean(){}

	/**
	 * @return the eventModel
	 */
	public ScheduleModel getEventModel() {
		return eventModel;
	}

	/**
	 * @param eventModel the eventModel to set
	 */
	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	/**
	 * @return the selectedStateId
	 */
	public Long getSelectedStateId() {
		return selectedStateId;
	}

	/**
	 * @param selectedStateId the selectedStateId to set
	 */
	public void setSelectedStateId(Long selectedStateId) {
		this.selectedStateId = selectedStateId;
	}

	/**
	 * @return the selectedState
	 */
	public PmState getSelectedState() {
		return selectedState;
	}

	/**
	 * @param selectedState the selectedState to set
	 */
	public void setSelectedState(PmState selectedState) {
		this.selectedState = selectedState;
	}

	/**
	 * @return the event
	 */
	public ScheduleEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	/**
	 * @return the eventToExTitleValueMap
	 */
	public Map<String, String> getEventToExTitleValueMap() {
		return eventToExTitleValueMap;
	}

	/**
	 * @param eventToExTitleValueMap the eventToExTitleValueMap to set
	 */
	public void setEventToExTitleValueMap(Map<String, String> eventToExTitleValueMap) {
		this.eventToExTitleValueMap = eventToExTitleValueMap;
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

	/**
	 * @return the disabledUpdate
	 */
	public boolean isDisabledUpdate() {
		return disabledUpdate;
	}

	/**
	 * @param disabledUpdate the disabledUpdate to set
	 */
	public void setDisabledUpdate(boolean disabledUpdate) {
		this.disabledUpdate = disabledUpdate;
	}

	public boolean isDeletedEvent() {
		return deletedEvent;
	}

	public void setDeletedEvent(boolean deletedEvent) {
		this.deletedEvent = deletedEvent;
	}

	public CalendarResDTO getResCalendarDTO() {
		return resCalendarDTO;
	}

	public void setResCalendarDTO(CalendarResDTO resCalendarDTO) {
		this.resCalendarDTO = resCalendarDTO;
	}

	public CalendarReqDTO getReqCalendarDTO() {
		return reqCalendarDTO;
	}

	public void setReqCalendarDTO(CalendarReqDTO reqCalendarDTO) {
		this.reqCalendarDTO = reqCalendarDTO;
	}

	public String getNewTitleValue() {
		return newTitleValue;
	}

	public void setNewTitleValue(String newTitleValue) {
		this.newTitleValue = newTitleValue;
	}

	public Date getOldStartDate() {
		return oldStartDate;
	}

	public void setOldStartDate(Date oldStartDate) {
		this.oldStartDate = oldStartDate;
	}

	public Date getNewStartDate() {
		return newStartDate;
	}

	public void setNewStartDate(Date newStartDate) {
		this.newStartDate = newStartDate;
	}

	public Date getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(Date newEndDate) {
		this.newEndDate = newEndDate;
	}

	public String getOldTitleValue() {
		return OldTitleValue;
	}

	public void setOldTitleValue(String oldTitleValue) {
		OldTitleValue = oldTitleValue;
	}

	public Date getOldEndDate() {
		return oldEndDate;
	}

	public void setOldEndDate(Date oldEndDate) {
		this.oldEndDate = oldEndDate;
	}
	
}
