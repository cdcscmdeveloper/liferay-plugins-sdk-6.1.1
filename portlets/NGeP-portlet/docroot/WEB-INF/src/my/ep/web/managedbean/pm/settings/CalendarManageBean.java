package my.ep.web.managedbean.pm.settings;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
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
	
	private static Logger log = Logger.getLogger(CalendarManageBean.class);
	private ScheduleModel eventModel;
	private Long selectedStateId;
	private PmState selectedState;
	private ScheduleEvent event = new DefaultScheduleEvent();

	@PostConstruct
	public void init() {
		// default state to putrajaya , stateId = 17
		loadScheduler(17L);
	}
	
	public void loadScheduler(long stateId){
		
		eventModel = new DefaultScheduleModel();
		
		PmState state = PmCommonUtil.retrievePmStateByStateId(stateId);
		setSelectedState(state);
		ICalendarSO calendarSO = null;
		CalendarResDTO resDTO = new CalendarResDTO();
		
		try {
			calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICalendarSO.class);
			CalendarReqDTO reqDTO = new CalendarReqDTO();
			reqDTO.setStateId(stateId);
			resDTO = calendarSO.getHolidaysForState(reqDTO);
			List<PmHoliday> stateHolidayList = resDTO.getHolidayList();

			if(stateHolidayList!=null){
				log.info(".............stateHolidayList.size()="
						+ stateHolidayList.size());
				for (PmHoliday holiday : stateHolidayList) {
					eventModel.addEvent(new DefaultScheduleEvent(holiday
							.getHolidayName(), holiday.getStartDate(), holiday
							.getEndDate()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		selectedStateId = stateId;
		
		setEvent(new DefaultScheduleEvent());
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

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public PmState getSelectedState() {
		if (selectedState == null) {
			selectedState = new PmState();
		}
		return selectedState;
	}

	public void setSelectedState(PmState selectedState) {
		this.selectedState = selectedState;
	}
}
