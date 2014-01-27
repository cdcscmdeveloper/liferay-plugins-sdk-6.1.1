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

@ManagedBean(name="NewsContent")
@RequestScoped

public class NewsContent implements Serializable {
	
	private final static Logger logger = Logger.getLogger(NewsContent.class.getName()); 
    private List<InformationContent> newscontents; 
       
    public NewsContent() {  
    	final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		/***********************************************************************
		 ** Start Query All News
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
		 ** End Query All News
		 * 
		 **/
	}     
	
	public List<InformationContent> getNewscontents() {  
        return newscontents;  
    }	
}
