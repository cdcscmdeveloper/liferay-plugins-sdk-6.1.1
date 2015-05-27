package my.ep.web.backingbean.pm.settings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalendarManageBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

@ManagedBean(name = "CalendarBackingBean")
@ViewScoped
public class CalendarBackingBean extends
		BaseBackingBean<CalendarManageBean> {

	private static Logger log = Logger
			.getLogger(CalendarBackingBean.class);

	public void addOrUpdateEvent(ActionEvent actionEvent) {
		
		ICalendarSO calendarSO = null;
		CalendarResDTO resDTO = null;
		try {
			calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICalendarSO.class);
			CalendarReqDTO reqDTO = new CalendarReqDTO();
			PmHoliday holiday = new PmHoliday();
			fillHolidayModelByEvent(holiday);
			String id = getManagedBean().getEvent().getId();
			
			if (id == null) {
				reqDTO.setHoliday(holiday);
				resDTO = calendarSO.addHoliday(reqDTO);
				if (resDTO.isSuccessful()) {
					this.getManagedBean().getEventModel()
							.addEvent(getManagedBean().getEvent());
				}
			} else {
				Long stateId = getManagedBean().getSelectedStateId();
				reqDTO.setStateId(stateId);
				reqDTO.setHoliday(holiday);
				resDTO = calendarSO.updateHoliday(reqDTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setDefaultEvent();
	}

	public void deleteEvent(ActionEvent actionEvent) {
		if (getManagedBean().getEvent().getId() != null) {
			String holidayName = getManagedBean().getEvent().getTitle();
			Long stateId = getManagedBean().getSelectedStateId();
			ICalendarSO calendarSO = null;
			CalendarResDTO resDTO = null;
			try {
				calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
						.getServiceObject("pm", ICalendarSO.class);
				CalendarReqDTO reqDTO = new CalendarReqDTO();
				
				PmHoliday holiday = new PmHoliday();
				holiday.setHolidayName(holidayName);
				PmState state = new PmState();
				state.setStateId(stateId);
				holiday.setState(state);
				reqDTO.setHoliday(holiday);
				
				resDTO = calendarSO.removeHoliday(reqDTO);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.getManagedBean().loadScheduler(stateId);
		}

		setDefaultEvent();
	}
	
	public void selectedState(AjaxBehaviorEvent e) {
		Long id = (Long) e.getComponent().getAttributes().get("value");
		if (id != null) {
			getManagedBean().loadScheduler(id);
		}
	}

	public void fillHolidayModelByEvent(PmHoliday holiday) {
		holiday.setState(getManagedBean().getSelectedState());
		holiday.setHolidayName(getManagedBean().getEvent().getTitle());
		holiday.setStartDate(getManagedBean().getEvent().getStartDate());
		holiday.setEndDate(getManagedBean().getEvent().getEndDate());
	}

	public void onEventSelect(SelectEvent selectEvent) {
		getManagedBean().setEvent((ScheduleEvent) selectEvent.getObject());
	}

	public void onDateSelect(SelectEvent selectEvent) {
		setDefaultEvent();
	}

	public List<PmState> getStateListOfMalaysia() {
		return PmCommonUtil.retrievePmStateList(131L);
	}
	
	private void setDefaultEvent(){
		getManagedBean().setEvent(new DefaultScheduleEvent());
	}
}
