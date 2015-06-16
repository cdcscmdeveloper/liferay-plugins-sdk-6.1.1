package my.ep.web.backingbean.pm.settings;

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

import my.ep.biz.service.pm.general.settings.calendar.ICalendarSO;
import my.ep.constant.MessageConst;
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
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
    private Date endDateTemp;
    
    
	public Date getEndDateTemp() {
		return endDateTemp;
	}

	public void setEndDateTemp(Date endDateTemp) {
		this.endDateTemp = endDateTemp;
	}
    public void popUpError(){
    	System.out.println("......popUpError.......");
    	FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"", MessageUtils.getMessage(MessageConst.INFO_711001,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

    }
    public void popUpSuccess(){
    	System.out.println("......popUpSuccess.......");
    	

    }
	public void updateEndDateVariable(){
		Date endDate = DateUtils.minusDays(this.getManagedBean().getEvent().getEndDate(), 1);
		setEndDateTemp(endDate);
//	/????can not set event endDate
		
	}
	public void addOrUpdateEvent(ActionEvent actionEvent) {
		System.out.println("......addOrUpdateEvent.......");
		System.out.println("........getManagedBean().getEvent().getTitle()="+getManagedBean().getEvent().getTitle());
	

		if(getManagedBean().getEvent().getTitle() == null || getManagedBean().getEvent().getTitle().isEmpty()){
			//
			  FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "key in the title of Event" , "");
		        addMessage(message);
//			RequestContext.getCurrentInstance().execute("eventDialog.hide();");
//		       throw new EpException("key in the title of Event");
		}
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
	
	public void preLoadingCalendar() {
		System.out.println("........preLoadingCalendar.......");
		renewSesionObject() ;	
		this.getManagedBean().setSelectedState(new PmState());
		this.getManagedBean().setEventModel(new DefaultScheduleModel());
		this.getManagedBean().setEvent(new DefaultScheduleEvent());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "calendar");
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
						.getServiceObject("pm", ICalendarSO.class);
				CalendarReqDTO reqDTO = new CalendarReqDTO();
				
				PmHoliday holiday = new PmHoliday();
				holiday.setHolidayName(holidayName);
				holiday.setStartDate(startDate);
				holiday.setEndDate(endDate);
				
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
		System.out.println("........updateState........");
		this.getManagedBean().setEventModel(new DefaultScheduleModel());
		this.getManagedBean().setEvent(new DefaultScheduleEvent());
//		Long id = (Long) e.getComponent().getAttributes().get("value");
//		System.out.println(".......id="+id);
		System.out.println(".......this.getManagedBean().getSelectedState()="+this.getManagedBean().getSelectedState());
		System.out.println(".......this.getManagedBean().getSelectedState().getStateId()="+this.getManagedBean().getSelectedState().getStateId());
		if (this.getManagedBean().getSelectedState() != null && this.getManagedBean().getSelectedState().getStateId() != null) {
			getManagedBean().loadSchedule(this.getManagedBean().getSelectedState().getStateId());
		}
	}

	public void fillHolidayModelByEvent(PmHoliday holiday) {
		holiday.setCountry(getDefaultCountry());
		holiday.setState(getManagedBean().getSelectedState());
		holiday.setHolidayName(getManagedBean().getEvent().getTitle());
		holiday.setStartDate(getManagedBean().getEvent().getStartDate());
		//increase end date for one day before persist because Scheduler component don't include endDate for holiday event
		holiday.setEndDate(DateUtils.addDays(getManagedBean().getEvent().getEndDate(), 1));
	}
	
	public Calendar today() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

        return calendar;
    }
	public void onEventSelect(SelectEvent selectEvent) {
		System.out.println("......getObject()).getStartDate().toGMTString()="+((ScheduleEvent) selectEvent.getObject()).getStartDate().toGMTString());
		System.out.println("......getObject()).getEndDate().toGMTString()="+((ScheduleEvent) selectEvent.getObject()).getEndDate().toGMTString());
		
		getManagedBean().setEvent((ScheduleEvent) selectEvent.getObject());
    }
	     
    public void onDateSelect(SelectEvent selectEvent) {
    	try{
    	if(this.getManagedBean().getSelectedState() != null && (this.getManagedBean().getSelectedState().getStateId() == null || this.getManagedBean().getSelectedState().getStateId() == -1)){
    		this.getManagedBean().setEvent(new DefaultScheduleEvent());
    		RequestContext.getCurrentInstance().execute("errDialog.show()");
    		return;
    	}
    	
//    	renewSesionObject();

        getManagedBean().setEvent(new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject()));
    	System.out.println("(Date) selectEvent.getObject()="+((Date) selectEvent.getObject()));
    	 
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
		return PmCommonUtil.retrievePmStateList(131L);
	}
	
	public PmCountry getDefaultCountry(){
		return PmCommonUtil.retrieveCountryByCountryCode("MY");
	}
}
