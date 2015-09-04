package my.ep.web.managedbean.pm.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
	
	@PostConstruct
	public void init() {
		//loadSchedule(2);
	}

	public void loadSchedule(long stateId) {
		System.out.println("...........loadSchedule............");
		System.out.println("...........stateId="+stateId);
		eventModel = new DefaultScheduleModel();
		event = new DefaultScheduleEvent();
		
//		this.loadCountrySchedule();  //always result null???!!
	
		if(stateId==0 || stateId== -1){
			System.out.println("...........inside if");
			this.setSelectedState(new PmState());
		}else{
			System.out.println("...........inside else");
			this.loadStateSchedule(stateId);
		}
		
		selectedStateId = stateId;
	}

	public Map<String, String> getEventToExTitleValueMap() {
		return eventToExTitleValueMap;
	}

	public void setEventToExTitleValueMap(Map<String, String> eventToExTitleValueMap) {
		this.eventToExTitleValueMap = eventToExTitleValueMap;
	}

	private void loadStateSchedule(long stateId) {
		System.out.println("...........loadStateSchedule............");
		PmState state = PmCommonUtil.retrievePmStateByStateId(stateId);
		setSelectedState(state);
		ICalendarSO calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICalendarSO.class);
		CalendarReqDTO reqDTO = new CalendarReqDTO();
		reqDTO.setStateId(stateId);
		CalendarResDTO resDTO = calendarSO.getHolidaysForState(reqDTO);
		List<PmHoliday> stateHolidayList = resDTO.getHolidayList();
		System.out.println("...........resDTO.getHolidayList()="+resDTO.getHolidayList());
		if (stateHolidayList != null) {
			for (PmHoliday holiday : stateHolidayList) {
				ScheduleEvent event = new DefaultScheduleEvent(
						holiday.getHolidayName(), holiday.getStartDate(),
						holiday.getEndDate());
				event.setId(String.valueOf(holiday.getHolidayId()));
				eventModel.addEvent(event);
			}
		}
	}

	private void loadCountrySchedule() {
		System.out.println("...........loadCountrySchedule............");
		setSelectedState(new PmState());
		ICalendarSO calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICalendarSO.class);
		CalendarReqDTO reqDTO = new CalendarReqDTO();
		CalendarResDTO resDTO = calendarSO.getHolidaysForCountry(reqDTO);
		System.out.println("...........resDTO.getHolidayList()="+resDTO.getHolidayList());
		if(resDTO.getHolidayList() != null){
			System.out.println("...........resDTO.getHolidayList().size()="+resDTO.getHolidayList().size());
		}
		List<PmHoliday> countryHolidayList = resDTO.getHolidayList();

		if (countryHolidayList != null) {
			for (PmHoliday holiday : countryHolidayList) {
				ScheduleEvent event = new DefaultScheduleEvent(
						holiday.getHolidayName(), holiday.getStartDate(),
						holiday.getEndDate());
				event.setId(String.valueOf(holiday.getHolidayId()));
				eventModel.addEvent(event);
			}
		}
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public ScheduleEvent getEvent() {
		System.out.println("get event title to= "+this.event.getTitle());
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		System.out.println("set event title to= "+event.getTitle());
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
}
