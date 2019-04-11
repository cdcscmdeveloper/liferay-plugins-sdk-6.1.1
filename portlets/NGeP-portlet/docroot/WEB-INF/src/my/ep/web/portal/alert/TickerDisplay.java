package my.ep.web.portal.alert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.portal.alert.TickerDisplay;
import my.ep.web.portal.alert.TickerContent;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portlet.announcements.model.AnnouncementsEntry;
import com.liferay.portlet.announcements.service.AnnouncementsEntryLocalServiceUtil;

@ManagedBean(name="TickerDisplay")
@RequestScoped

public class TickerDisplay implements Serializable {  
    
	private final static Logger logger = Logger.getLogger(TickerDisplay.class.getName());  
    private List<TickerContent> alertcontents;  
    
   
    public TickerDisplay() throws SystemException {  
    	/***********************************************************************
		 ** Start Query Latest Alert
		 * 
		 **/
		alertcontents = new ArrayList<TickerContent>();
				
		DynamicQuery query = DynamicQueryFactoryUtil.forClass(AnnouncementsEntry.class,  PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"general"}))
		.addOrder(OrderFactoryUtil.desc("displayDate"));
		
		List<AnnouncementsEntry> aeAlerts = null;	
		try 
		{
			aeAlerts = (List<AnnouncementsEntry>) AnnouncementsEntryLocalServiceUtil.dynamicQuery(query);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}

		Long latestAlertEntryId = null;
		String latestAlertTitle = null;
		String latestAlertContent = null;
		String latestAlertType = null;
		Date latestAlertsDisplayDate = null;

		for(AnnouncementsEntry latestalertsresult : aeAlerts)
		{
			latestAlertEntryId = latestalertsresult.getEntryId();
			latestAlertTitle = latestalertsresult.getTitle();
			latestAlertContent = latestalertsresult.getContent();
			latestAlertType = latestalertsresult.getType();
			latestAlertsDisplayDate = latestalertsresult.getDisplayDate();
				
			alertcontents.add(new TickerContent(latestAlertEntryId, latestAlertTitle, latestAlertContent, latestAlertType, latestAlertsDisplayDate));
		}
		/***********************************************************************
		 ** End Query Latest Alert
		 * 
		 **/		
	}  
    
	public List<TickerContent> getAlertcontents() {  
        return alertcontents;  
    } 
}
