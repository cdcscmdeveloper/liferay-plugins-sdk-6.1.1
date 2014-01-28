package my.ep.web.portal.help;

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
import com.liferay.portal.kernel.dao.orm.Junction;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;

@ManagedBean(name="SearchHelp")
@RequestScoped

public class SearchHelp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(SearchHelp.class.getName());  
	
	private List<OnlineContent> resulthelpcontents;  
	
	private String helpSearch;
	
	public String getHelpSearch() {  
        return helpSearch;  
	}  
	
	public void setHelpSearch(String helpSearch) {  
        this.helpSearch = helpSearch;  
	} 
	
	public String searchcontent(){
		
		final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		setHelpSearch(helpSearch);
		
		/***********************************************************************
		 ** Start Query Search Online Help
		 * 
		 **/
		resulthelpcontents = new ArrayList<OnlineContent>();
				
		DynamicQuery subquerysearchwebcontent = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleSub", PortalClassLoaderUtil.getClassLoader());
		subquerysearchwebcontent.add(PropertyFactoryUtil.forName("articleId").eqProperty("articleParent.articleId"));
		subquerysearchwebcontent.setProjection(ProjectionFactoryUtil.max("id"));
		Junction junction2 = RestrictionsFactoryUtil.disjunction();
		for (String retval: helpSearch.split(" ", 0)){
	         junction2.add(RestrictionsFactoryUtil.ilike("content", "%" + retval + "%"));
	         junction2.add(RestrictionsFactoryUtil.ilike("title", "%" + retval + "%"));
	    }
		subquerysearchwebcontent.add(junction2);
		subquerysearchwebcontent.add(PropertyFactoryUtil.forName("type").in(new String[] {"user-guide"}));
		
		DynamicQuery querysearchwebcontent = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleParent", PortalClassLoaderUtil.getClassLoader());
		querysearchwebcontent.add(PropertyFactoryUtil.forName("id").eq(subquerysearchwebcontent));
		
		List<JournalArticle> jaHelps = null;	
		try 
		{
			jaHelps = (List<JournalArticle>) JournalArticleLocalServiceUtil.dynamicQuery(querysearchwebcontent);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}
		
		Long searchHelpsId = null;
		String searchHelpsArticleId = null;
		String searchHelpsTitle = "";
		Double searchHelpsVersion = null;
		Date searchHelpsDisplayDate = null;
		int searchHelpsCount = 0;
		String searchHelpsCountString = null;

		for(JournalArticle searchhelpsresult : jaHelps)
		{			
			searchHelpsId = searchhelpsresult.getId();
			searchHelpsArticleId = searchhelpsresult.getArticleId();
			searchHelpsTitle = searchhelpsresult.getTitle();
			searchHelpsVersion = searchhelpsresult.getVersion();
			searchHelpsDisplayDate = searchhelpsresult.getDisplayDate();
			searchHelpsCountString = String.valueOf(searchHelpsCount);
			
			String catlistdata = null;
			
			long jaClassNameId = ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName()); 
			ExpandoTable table = null;
			try {
				table = ExpandoTableLocalServiceUtil.getDefaultTable(themeDisplay.getCompanyId(), jaClassNameId );
			} catch (PortalException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			ExpandoColumn column = null;
			try {
				column = ExpandoColumnLocalServiceUtil.getColumn(table.getTableId(), "listcat");
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			ExpandoValue expandoValue;
			try {
				expandoValue = ExpandoValueLocalServiceUtil.getValue(table.getTableId(), column.getColumnId(), searchHelpsId);

				catlistdata = expandoValue.getData();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JournalArticle article;
			try {
				article = JournalArticleLocalServiceUtil.getArticle(groupId, searchHelpsArticleId, searchHelpsVersion);
				String helptitle = article.getTitle(languageKey);
				JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(groupId, article.getArticleId(), article.getTemplateId(), languageKey, themeDisplay);
				
				resulthelpcontents.add(new OnlineContent(searchHelpsCount,searchHelpsCountString, helptitle, searchHelpsDisplayDate, articleDisplay.getContent(), catlistdata));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			searchHelpsCount = searchHelpsCount + 1;
		}
		/***********************************************************************
		 ** End Query Search Online Help
		 * 
		 **/
		
        return "search";
    }
	
	public List<OnlineContent> getResulthelpcontents() {  
        return resulthelpcontents;  
    } 
	
	public SearchHelp() {  
		
	}	
}
