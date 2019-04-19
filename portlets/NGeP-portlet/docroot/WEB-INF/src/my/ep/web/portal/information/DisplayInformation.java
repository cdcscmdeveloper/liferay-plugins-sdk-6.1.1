package my.ep.web.portal.information;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.calendar.service.CalEventLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;

@ManagedBean(name="DisplayInformation")
@RequestScoped

public class DisplayInformation implements Serializable {  
    
	private final static Logger logger = Logger.getLogger(DisplayInformation.class.getName());  
    private List<InformationContent> informationcontents;  
    private List<InformationContent> newscontents; 
    private List<InformationContent> eventcontents; 
    
   
    public DisplayInformation() {  
    	final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		/***********************************************************************
		 ** Start Query Latest Announcement
		 * 
		 **/
		informationcontents = new ArrayList<InformationContent>();
				
		DynamicQuery subQuery = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleSub", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("articleId").eqProperty("articleParent.articleId"))
		.setProjection(ProjectionFactoryUtil.max("id"));
					
		DynamicQuery query = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleParent", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("id").eq(subQuery))
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"announcements"}))
		.addOrder(OrderFactoryUtil.desc("displayDate"));
		query.setLimit(0, 4);

		List<JournalArticle> jaAnnouncements = null;	
		try 
		{
			jaAnnouncements = (List<JournalArticle>) JournalArticleLocalServiceUtil.dynamicQuery(query);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}

		String latestAnnouncementsArticleId = null;
		String latestAnnouncementsTitle = "";
		Double latestAnnouncementsVersion = null;
		Date latestAnnouncementsDisplayDate = null;

		for(JournalArticle latestannouncementsresult : jaAnnouncements)
		{
			latestAnnouncementsArticleId = latestannouncementsresult.getArticleId();
			latestAnnouncementsTitle = latestannouncementsresult.getTitle();
			latestAnnouncementsVersion = latestannouncementsresult.getVersion();
			latestAnnouncementsDisplayDate = latestannouncementsresult.getDisplayDate();
				
			JournalArticle article;
			try {
				article = JournalArticleLocalServiceUtil.getArticle(groupId, latestAnnouncementsArticleId, latestAnnouncementsVersion);
				JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(groupId, article.getArticleId(), article.getTemplateId(), languageKey, themeDisplay);

				informationcontents.add(new InformationContent(latestAnnouncementsTitle, latestAnnouncementsDisplayDate, articleDisplay.getContent()));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/***********************************************************************
		 ** End Query Latest Announcement
		 * 
		 **/
		
		
		
		/***********************************************************************
		 ** Start Query Latest News
		 * 
		 **/
		newscontents = new ArrayList<InformationContent>();
	
		DynamicQuery subQueryNews = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleSub", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("articleId").eqProperty("articleParent.articleId"))
		.setProjection(ProjectionFactoryUtil.max("id"));
					
		DynamicQuery queryNews = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleParent", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("id").eq(subQueryNews))
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"news"}))
		.addOrder(OrderFactoryUtil.desc("displayDate"));
		queryNews.setLimit(0, 4);
	
		List<JournalArticle> jaNews = null;	
		try 
		{
			jaNews = (List<JournalArticle>) JournalArticleLocalServiceUtil.dynamicQuery(queryNews);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}
	
		String latestNewsArticleId = null;
		String latestNewsTitle = "";
		Double latestNewsVersion = null;
		Date latestNewsDisplayDate = null;
	
		for(JournalArticle latestnewsresult : jaNews)
		{
			latestNewsArticleId = latestnewsresult.getArticleId();
			latestNewsTitle = latestnewsresult.getTitle();
			latestNewsVersion = latestnewsresult.getVersion();
			latestNewsDisplayDate = latestnewsresult.getDisplayDate();
				
			JournalArticle article;
			try {
				article = JournalArticleLocalServiceUtil.getArticle(groupId, latestNewsArticleId, latestNewsVersion);
				JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(groupId, article.getArticleId(), article.getTemplateId(), languageKey, themeDisplay);
	
				newscontents.add(new InformationContent(latestNewsTitle, latestNewsDisplayDate, articleDisplay.getContent()));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/***********************************************************************
		 ** End Query Latest News
		 * 
		 **/
		
		
		
		/***********************************************************************
		 ** Start Query Latest Event
		 * 
		 **/
		eventcontents = new ArrayList<InformationContent>();
		
		DynamicQuery queryEvent = DynamicQueryFactoryUtil.forClass(CalEvent.class, PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"event"}))
		.addOrder(OrderFactoryUtil.desc("startDate"));
		queryEvent.setLimit(0, 4);
	
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
		 ** End Query Latest Event
		 * 
		 **/
	}  
    
	public List<InformationContent> getInformationcontents() {  
        return informationcontents;  
    } 
	
	public List<InformationContent> getNewscontents() {  
        return newscontents;  
    }
	
	public List<InformationContent> getEventcontents() {  
        return eventcontents;  
    }
}
