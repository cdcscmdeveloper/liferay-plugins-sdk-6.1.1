package my.ep.web.backingbean.pm.settings;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalenderSchedulerManageBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

@ManagedBean(name = "calenderSchedulerBackingBean")
@ViewScoped
public class CalenderSchedulerBackingBean extends BaseBackingBean<CalenderSchedulerManageBean> {
private static Logger log = Logger
	.getLogger(CalenderSchedulerBackingBean.class);

public void addEvent(ActionEvent actionEvent) {
	log.info(".........addEvent..........");
	PmHoliday holiday = null;
    if(getManagedBean().getEvent().getId() == null){
        
        holiday = new PmHoliday(); 
        fillHolidayModelByEvent(holiday);
        //TODO update DB
        ICalendarSO calendarSO = null;
        CalendarResDTO resDTO = new CalendarResDTO();
    	try {
    		calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
    				.getServiceObject("pm", ICalendarSO.class);
    		CalendarReqDTO reqDTO = new CalendarReqDTO();
    		reqDTO.setHoliday(holiday);

    	    resDTO = calendarSO.addHoliday(reqDTO);
    		PmHoliday updatedHoliday = resDTO.getHoliday();
    		// retrieve
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(resDTO.isSuccessful()){
             this.getManagedBean().getEventModel().addEvent(getManagedBean().getEvent());
    	}
    }else{
    	log.info("update event part");
    	this.getManagedBean().getEventModel().updateEvent(getManagedBean().getEvent());
    }
   
    getManagedBean().setEvent(new DefaultScheduleEvent());
}
public void fillHolidayModelByEvent(PmHoliday holiday){
	holiday.setState(this.getManagedBean().getSelectedState());
	holiday.setHolidayName(getManagedBean().getEvent().getTitle());
	holiday.setStartDate(getManagedBean().getEvent().getStartDate());
	holiday.setEndDate(getManagedBean().getEvent().getEndDate());
}
 
public void onEventSelect(SelectEvent selectEvent) {
	getManagedBean().setEvent((ScheduleEvent) selectEvent.getObject());
}
 
public void onDateSelect(SelectEvent selectEvent) {
	getManagedBean().setEvent(new DefaultScheduleEvent());
}
 
public void onEventMove(ScheduleEntryMoveEvent event) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
     
    addMessage(message);
}
 
public void onEventResize(ScheduleEntryResizeEvent event) {
    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
     
    addMessage(message);
}
 
private void addMessage(FacesMessage message) {
    FacesContext.getCurrentInstance().addMessage(null, message);
}
/**
 * get state list by country id
 * 
 * @param countryId
 * @return List<PmState>
 */
public List<PmState> getStateListOfMalaysia() {
	log.info("getStateList");
	return PmCommonUtil.retrievePmStateList(131L);//countryId for Malaysia = 131
}
	
}
