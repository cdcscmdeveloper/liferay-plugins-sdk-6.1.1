package my.ep.web.backingbean.pm.settings;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalendarManageBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

@ManagedBean(name = "calendarBackingBean")
@ViewScoped
public class CalendarBackingBean extends
		BaseBackingBean<CalendarManageBean> {

	public void addOrUpdateEvent(ActionEvent actionEvent) {
		
		ICalendarSO calendarSO = null;
		CalendarResDTO resDTO = null;
		
		Long stateId = getManagedBean().getSelectedStateId();
		
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
			} else {
				reqDTO.setStateId(stateId);
				reqDTO.setHoliday(holiday);
				resDTO = calendarSO.updateHoliday(reqDTO);
			}
			
			if (resDTO.isSuccessful()) {
				getManagedBean().getEventModel()
						.updateEvent(getManagedBean().getEvent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getManagedBean().loadSchedule(stateId);
		}
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
				
				if (resDTO.isSuccessful()) {
					this.getManagedBean().getEventModel()
							.deleteEvent(getManagedBean().getEvent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				getManagedBean().loadSchedule(stateId);
			}
		}
	}
	
	public void updateState(AjaxBehaviorEvent e) {
		Long id = (Long) e.getComponent().getAttributes().get("value");
		if (id != null) {
			getManagedBean().loadSchedule(id);
		}
	}

	public void fillHolidayModelByEvent(PmHoliday holiday) {
		holiday.setCountry(getDefaultCountry());
		holiday.setState(getManagedBean().getSelectedState());
		holiday.setHolidayName(getManagedBean().getEvent().getTitle());
		holiday.setStartDate(getManagedBean().getEvent().getStartDate());
		holiday.setEndDate(getManagedBean().getEvent().getEndDate());
	}

	public void onEventSelect(SelectEvent selectEvent) {
		getManagedBean().setEvent((ScheduleEvent) selectEvent.getObject());
    }
	     
    public void onDateSelect(SelectEvent selectEvent) {
    	getManagedBean().setEvent(new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject()));
	}
	
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta());
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta());
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

	public List<PmState> getStateListOfMalaysia() {
		return PmCommonUtil.retrievePmStateList(131L);
	}
	
	public PmCountry getDefaultCountry(){
		return PmCommonUtil.retrieveCountryByCountryCode("MY");
	}
}
