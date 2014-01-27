package my.ep.web.portal.information;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.calendar.service.CalEventLocalServiceUtil;

@ManagedBean(name="EventContent")
@RequestScoped

public class EventContent implements Serializable {
	
	private final static Logger logger = Logger.getLogger(EventContent.class.getName());  
    private List<InformationContent> eventcontents; 
       
    public EventContent() {  
    	final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		/***********************************************************************
		 ** Start Query All Event
		 * 
		 **/
		eventcontents = new ArrayList<InformationContent>();
		
		DynamicQuery queryEvent = DynamicQueryFactoryUtil.forClass(CalEvent.class, PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"event"}))
		.addOrder(OrderFactoryUtil.desc("startDate"));
		
		List<CalEvent> jaEvent = null;	
		try 
		{
			jaEvent = (List<CalEvent>) CalEventLocalServiceUtil.dynamicQuery(queryEvent);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}
	
		Long latestEventId = null;
		String latestEventTitle = "";
		String latestEventDescription = "";
		Date latestEventStartDate = null;
	
		for(CalEvent latesteventresult : jaEvent)
		{
			latestEventId = latesteventresult.getEventId();
			latestEventTitle = latesteventresult.getTitle();
			latestEventDescription = latesteventresult.getDescription();
			latestEventStartDate = latesteventresult.getStartDate();
				
			CalEvent event;
			try {
				event = CalEventLocalServiceUtil.getCalEvent(latestEventId);
				eventcontents.add(new InformationContent(latestEventTitle, latestEventStartDate, latestEventDescription));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/***********************************************************************
		 ** End Query All Event
		 * 
		 **/
	}  	
	
	public List<InformationContent> getEventcontents() {  
        return eventcontents;  
    }
}
