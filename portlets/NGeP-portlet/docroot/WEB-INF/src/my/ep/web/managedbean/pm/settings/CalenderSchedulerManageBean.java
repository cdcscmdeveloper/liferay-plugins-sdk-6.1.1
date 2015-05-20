package my.ep.web.managedbean.pm.settings;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
 
import org.apache.log4j.Logger;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.web.backingbean.pm.settings.CalenderSchedulerBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "calenderSchedulerMB")
@SessionScoped
public class CalenderSchedulerManageBean extends BaseManagedBean{
	private static Logger log = Logger
			.getLogger(CalenderSchedulerManageBean.class);
	
	 private ScheduleModel eventModel;
	 private Long selectedStateId;
	 private PmState selectedState;
     
	 private ScheduleEvent event = new DefaultScheduleEvent();
	 
	    @PostConstruct
	    public void init() {
	    	log.info("..........init...........");
	        eventModel = new DefaultScheduleModel();
	        //default state to putrajaya , stateId = 17
	        PmState state = PmCommonUtil.retrievePmStateByStateId(17L);
	        setSelectedState(state);
	        
	        ICalendarSO calendarSO = null;
	        CalendarResDTO resDTO = new CalendarResDTO();
	    	try {
	    		calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
	    				.getServiceObject("pm", ICalendarSO.class);
	    		CalendarReqDTO reqDTO = new CalendarReqDTO();
	    		reqDTO.setStateId(state.getStateId());

	    	    resDTO = calendarSO.getHolidaysForState(reqDTO);
	    		List<PmHoliday> stateHolidayList = resDTO.getHolidayList();
	    	
	    	log.info(".............stateHolidayList.size()="+stateHolidayList.size());
	    	
	    	for(PmHoliday holiday : stateHolidayList){
	             eventModel.addEvent(new DefaultScheduleEvent(holiday.getHolidayName(), holiday.getStartDate(), holiday.getEndDate()));
	    	}
	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }      
	       
	    public ScheduleEvent getEvent() {
	        return event;
	    }
	 
	    public Long getSelectedStateId() {
			return selectedStateId;
		}


		public void setSelectedStateId(Long selectedStateId) {
			this.selectedStateId = selectedStateId;
		}


		public void setEvent(ScheduleEvent event) {
			log.info(".......setEvent.....");
	        this.event = event;
	    }

		public ScheduleModel getEventModel() {
			return eventModel;
		}

		public void setEventModel(ScheduleModel eventModel) {
			this.eventModel = eventModel;
		}

		public PmState getSelectedState() {
			if(selectedState == null){
				selectedState = new PmState();
			}
			return selectedState;
		}

		public void setSelectedState(PmState selectedState) {
			this.selectedState = selectedState;
		}
	     
	  
}
