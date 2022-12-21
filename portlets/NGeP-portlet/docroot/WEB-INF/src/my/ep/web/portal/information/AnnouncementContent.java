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
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;

@ManagedBean(name="AnnouncementContent")
@RequestScoped

public class AnnouncementContent implements Serializable {
	
	private final static Logger logger = Logger.getLogger(AnnouncementContent.class.getName());  
    private List<InformationContent> announcementcontents;  
    private InformationContent selectedContent; 
    
    public AnnouncementContent() {  
    	final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		/***********************************************************************
		 ** Start Query All Announcement
		 * 
		 **/
		announcementcontents = new ArrayList<InformationContent>();
				
		DynamicQuery subQuery = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleSub", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("articleId").eqProperty("articleParent.articleId"))
		.setProjection(ProjectionFactoryUtil.max("id"));
					
		DynamicQuery query = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleParent", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("id").eq(subQuery))
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"announcements"}))
		.addOrder(OrderFactoryUtil.desc("displayDate"));
		
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

				announcementcontents.add(new InformationContent(latestAnnouncementsTitle, latestAnnouncementsDisplayDate, articleDisplay.getContent()));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/***********************************************************************
		 ** End Query All Announcement
		 * 
		 **/
	}  
    
	public List<InformationContent> getAnnouncementcontents() {  
        return announcementcontents;  
    } 
	
	public InformationContent getSelectedContent() {  
	    return selectedContent;  
	}  
	  
	public void setSelectedContent(InformationContent selectedContent) {  
	    this.selectedContent = selectedContent;  
	}  
}
