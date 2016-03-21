package my.ep.web.backingbean.pm.settings;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.general.settings.calendar.CalendarReqDTO;
import my.ep.dto.pm.general.settings.calendar.CalendarResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.setting.calendar.PmHoliday;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.CalendarManageBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;


@ManagedBean(name = "calendarBackingBean")
@ViewScoped
public class CalendarBackingBean extends
		BaseBackingBean<CalendarManageBean> {
	public static Logger log = Logger.getLogger(CalendarBackingBean.class);
	private Date endDateTemp;    
    
	public Date getEndDateTemp() {
		return endDateTemp;
	}

	public void setEndDateTemp(Date endDateTemp) {
		this.endDateTemp = endDateTemp;
	}
	
	private void loadSchedule(long stateId) {
		log.info("...........loadSchedule............");
		log.info("...........stateId="+stateId);
		getManagedBean().setEventModel(new DefaultScheduleModel());
		getManagedBean().setEvent(new DefaultScheduleEvent());
	
		if(stateId==0 || stateId== -1){
			getManagedBean().setSelectedState(new PmState());
		}else{
			this.loadStateSchedule(stateId);
		}
		
		getManagedBean().setSelectedStateId(stateId);
		changeWeekEndColor();
	}

	private void loadStateSchedule(long stateId) {
		
		log.info("...........loadStateSchedule............");
		PmState state = PmCommonUtil.retrievePmStateByStateId(stateId);
		getManagedBean().setSelectedState(state);
		ICalendarSO calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICalendarSO.class);
		CalendarReqDTO reqDTO = new CalendarReqDTO();
		reqDTO.setStateId(stateId);
		CalendarResDTO resDTO = calendarSO.getHolidaysForState(reqDTO);
		List<PmHoliday> stateHolidayList = resDTO.getHolidayList();
		log.info("...........resDTO.getHolidayList()="+resDTO.getHolidayList());
		
		
		
		if (stateHolidayList != null) {
			for (PmHoliday holiday : stateHolidayList) {
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(holiday.getEndDate());
				endDate.set(Calendar.HOUR_OF_DAY, 1);
				endDate.set(Calendar.MINUTE, 0);
				endDate.set(Calendar.SECOND, 0);
				endDate.set(Calendar.MILLISECOND, 0);
				
				DefaultScheduleEvent event = new DefaultScheduleEvent(
						holiday.getHolidayName(), holiday.getStartDate(),
						endDate.getTime());
				event.setId(String.valueOf(holiday.getHolidayId()));
				event.setData(holiday.getHolidayId());
				getManagedBean().getEventModel().addEvent(event);
			}
		}
		
	}
	
    public void popUpError(){
    	log.info("......popUpError.......");
    	FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"", MessageUtils.getMessage(MessageConst.INFO_711001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

    }
    
    public void popUpSuccess(){
    	log.info("......popUpSuccess.......");
    	

    }
    
	public void updateEndDateVariable(){
		Date endDate = DateUtils.addDays(this.getManagedBean().getEvent().getEndDate(), 1);
		setEndDateTemp(endDate);
		
	}
	public void addOrUpdateEvent(ActionEvent actionEvent) {
		log.info("......addOrUpdateEvent.......");
		log.info("........getManagedBean().getEvent().getTitle()="+getManagedBean().getEvent().getTitle());
		log.info("........getManagedBean().getEvent().getStartDate()="+getManagedBean().getEvent().getStartDate());
		log.info("........getManagedBean().getEvent().getEndDate()="+getManagedBean().getEvent().getEndDate());
		log.info("........getManagedBean().getEvent().getId()="+getManagedBean().getEvent().getId());

		ICalendarSO calendarSO = null;
		CalendarResDTO resDTO = null;
		
		Long stateId = getManagedBean().getSelectedStateId();
		try {
			
			calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICalendarSO.class);
			CalendarReqDTO reqDTO = new CalendarReqDTO();
			PmHoliday holiday = new PmHoliday();
			fillHolidayModelByEvent(holiday);
			validateCalendar(holiday);
			String id = getManagedBean().getEvent().getId();
			
			if (id == null) {
				reqDTO.setHoliday(holiday);
				resDTO = calendarSO.addHoliday(reqDTO);
			} else {
				 String exTitle = this.getManagedBean().getEventToExTitleValueMap().get(id);
				 if(exTitle != null && !exTitle.isEmpty()){
					 holiday.setHolidayExName(exTitle);
				 }
				reqDTO.setStateId(stateId);
				reqDTO.setHoliday(holiday);
				resDTO = calendarSO.updateHoliday(reqDTO);
			}
			
			if (resDTO.isSuccessful()) {
				getManagedBean().setInitialDate(holiday.getStartDate());
				getManagedBean().getEventModel()
						.updateEvent(getManagedBean().getEvent());
				RequestContext.getCurrentInstance()
				.execute("myschedule.update();");
				RequestContext.getCurrentInstance()
				.execute("eventDialog.hide();");
				RequestContext.getCurrentInstance().update("schedule");				
			
//				this.getManagedBean().setEvent(new DefaultScheduleEvent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loadSchedule(stateId);
		}
		
	}
	
	public void holidayTitleValChange(ValueChangeEvent event){
		log.info("holidayTitleValChange");
		log.info("event.getOldValue()="+event.getOldValue());
		log.info("event.getNewValue()="+event.getNewValue());

		String eventId = this.getManagedBean().getEvent().getId();
		log.info("........getManagedBean().getEvent().getId()="+eventId);
		if (eventId != null) {
			String exTitle = this.getManagedBean().getEventToExTitleValueMap()
					.get(eventId);
			if (exTitle != null) {
				if (exTitle.equals((String) event.getNewValue())) {
					this.getManagedBean().getEventToExTitleValueMap()
							.remove(eventId);
				}
			} else {
				this.getManagedBean().getEventToExTitleValueMap()
						.put(eventId, (String) event.getOldValue());
			}
		}
	}
	public void preLoadingCalendar() {
		log.info("........preLoadingCalendar.......");
		renewSesionObject() ;	
		this.getManagedBean().setSelectedState(new PmState());
		this.getManagedBean().setEventModel(new DefaultScheduleModel());
		this.getManagedBean().setEvent(new DefaultScheduleEvent());
		this.getManagedBean().setInitialDate(getTodayDate());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "calendar");
	}
	public String getLocale(){
		return new FacesScopes().getUserLocale().getLanguage();
	}
	
	public void deleteEvent(ActionEvent actionEvent) {
		
		if (getManagedBean().getEvent().getId() != null) {
			String holidayName = getManagedBean().getEvent().getTitle();
			Date startDate = getManagedBean().getEvent().getStartDate();
			Date endDate = getManagedBean().getEvent().getEndDate();
			Long stateId = getManagedBean().getSelectedStateId();
			ICalendarSO calendarSO = null;
			CalendarResDTO resDTO = null;
			try {
				calendarSO = (ICalendarSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICalendarSO.class);
				CalendarReqDTO reqDTO = new CalendarReqDTO();
				
				PmHoliday holiday = new PmHoliday();
				holiday.setHolidayName(holidayName);
				holiday.setStartDate(startDate);
				holiday.setEndDate(endDate);
				holiday.setHolidayId((Long)getManagedBean().getEvent().getData());
				
				PmState state = new PmState();
				state.setStateId(stateId);
				
				holiday.setState(state);
				
				reqDTO.setHoliday(holiday);
				
				resDTO = calendarSO.removeHoliday(reqDTO);
				
				if (resDTO.isSuccessful()) {
					getManagedBean().setInitialDate(holiday.getStartDate());
					this.getManagedBean().getEventModel()
							.deleteEvent(getManagedBean().getEvent());
					RequestContext.getCurrentInstance()
					.execute("myschedule.update();");
					RequestContext.getCurrentInstance()
					.execute("eventDialog.hide();");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				loadSchedule(stateId);
			}
		}
	}
	
	public void updateState(AjaxBehaviorEvent e) {
		log.info("........updateState........");
		this.getManagedBean().setEventModel(new DefaultScheduleModel());
		this.getManagedBean().setEvent(new DefaultScheduleEvent());
//		Long id = (Long) e.getComponent().getAttributes().get("value");
//		log.info(".......id="+id);
		log.info(".......this.getManagedBean().getSelectedState()="+this.getManagedBean().getSelectedState());
		log.info(".......this.getManagedBean().getSelectedState().getStateId()="+this.getManagedBean().getSelectedState().getStateId());
		if (this.getManagedBean().getSelectedState() != null && this.getManagedBean().getSelectedState().getStateId() != null) {
			loadSchedule(this.getManagedBean().getSelectedState().getStateId());
		}
		
//			FacesContext facesContext = FacesContext.getCurrentInstance();
//			facesContext.addMessage(
//					null,
//					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
//							MessageUtils.getMessage(
//									"test",
//									MessageUtils.BUNDLE_COMMON_MESSAGES,
//									new FacesScopes().getUserLocale())));
	}

	public void fillHolidayModelByEvent(PmHoliday holiday) {
		holiday.setCountry(getDefaultCountry());
		holiday.setState(getManagedBean().getSelectedState());
		holiday.setHolidayName(getManagedBean().getEvent().getTitle());
		holiday.setStartDate(getManagedBean().getEvent().getStartDate());
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(getManagedBean().getEvent().getEndDate());
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 0);
	    
	    Date endDate = new Timestamp(calendar.getTime().getTime());
	    log.info(".....getManagedBean().getEvent().getEndDate()"+getManagedBean().getEvent().getEndDate());
		log.info(".....getManagedBean().getEvent().getEndDate()"+endDate);
		
		holiday.setEndDate(endDate);
	}
	
	public Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    } 
	
	public void onEventSelect(SelectEvent selectEvent) {
//		log.info("......getObject()).getStartDate()="+((ScheduleEvent) selectEvent.getObject()).getStartDate());
//		log.info("......(ScheduleEvent) selectEvent.getObject()).getTitle()="+((ScheduleEvent) selectEvent.getObject()).getTitle());
		RequestContext.getCurrentInstance().reset(":form:eventDetails");
		getManagedBean().setEvent((ScheduleEvent) selectEvent.getObject());
		getManagedBean().setNewEvent(false);
//		 RequestContext.getCurrentInstance().execute("eventDialog.show()");
    }
	     
    public void onDateSelect(SelectEvent selectEvent) {
    	try{
    	RequestContext.getCurrentInstance().reset(":form:eventDetails");
    	if(this.getManagedBean().getSelectedState() != null && (this.getManagedBean().getSelectedState().getStateId() == null || this.getManagedBean().getSelectedState().getStateId() == -1)){
    		this.getManagedBean().setEvent(new DefaultScheduleEvent());
    		RequestContext.getCurrentInstance().execute("errDialog.show()");
    		return;
    	}
    	
//    	renewSesionObject();

	        getManagedBean().setEvent(new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject()));
	    	log.info("(Date) selectEvent.getObject()="+((Date) selectEvent.getObject()));
	    	getManagedBean().setNewEvent(true);
	    	RequestContext.getCurrentInstance().execute("eventDialog.show()");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
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
		return PmCommonUtil.retrievePmStateListForCalendar(131L);
	}
	
	public PmCountry getDefaultCountry(){
		return PmCommonUtil.retrieveCountryByCountryCode("MY");
	}
	
	private void validateCalendar(PmHoliday holiday){
		if(StringUtils.isBlank(holiday.getHolidayName())){
			throw new EpException("Title is mandatory.");
		}
		
		if(holiday.getEndDate().before(holiday.getStartDate())){
			throw new EpException("End Date entered must after Start Date.");
		}
	}
	

	 private Date getTodayDate() {
	     Calendar calendar = Calendar.getInstance();
	     calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
	  
	     return calendar.getTime();
	}
	 
	 public void nextPrevious(ActionEvent event){
	      String increment = (String) event.getComponent().getAttributes().get("increment");
	      int moveValue = increment.equals("next")?1:-1;	    
	      Calendar cal = Calendar.getInstance();
	      cal.setTime(getManagedBean().getInitialDate());
	      cal.set(Calendar.HOUR_OF_DAY, 23);
	      cal.set(Calendar.MINUTE, 59);
	      cal.set(Calendar.SECOND, 0);
	      cal.add(Calendar.MONTH, moveValue);
	      this.getManagedBean().setInitialDate(cal.getTime());
	      changeWeekEndColor();
	   }

	   public void today(ActionEvent event){
		  Calendar cal = Calendar.getInstance();
	      this.getManagedBean().setInitialDate(cal.getTime());
	      changeWeekEndColor();
	   }
	   
	   public void changeWeekEndColor(){
		   RequestContext.getCurrentInstance()
	  		.execute("changeWeekEndColor();");
	   }
}
