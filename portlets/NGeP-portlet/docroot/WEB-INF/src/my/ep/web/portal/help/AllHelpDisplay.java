package my.ep.web.portal.help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.component.datalist.DataList;
import org.primefaces.event.data.PageEvent;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
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

@SuppressWarnings("serial")
@ManagedBean(name="AllHelpDisplay")
@RequestScoped

public class AllHelpDisplay implements Serializable {  
    
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(AllHelpDisplay.class.getName());  
    
	private List<OnlineContent> allhelpcontents;  
    
	private int helpCount;
	 
	public int getHelpCount() {  
        return helpCount;  
	}  
	
	public void setHelpCount(int helpCount) {  
        this.helpCount = helpCount;  
	} 
	
	public String processPage(int helpCount){
		String temphelpcount = "";
		FacesContext facesContext = FacesContext.getCurrentInstance();  
        ExternalContext externalContext = facesContext.getExternalContext();  
  
        Map<String, String> params = externalContext.getRequestParameterMap();  
        temphelpcount = params.get("helpcount");
        
        helpCount = Integer.parseInt(temphelpcount);
        		        		
		setHelpCount(helpCount);
		
		return "page";
	}
	
    public AllHelpDisplay() {  
    	final FacesContext context = FacesContext.getCurrentInstance();
		final ThemeDisplay themeDisplay = (ThemeDisplay) context.getExternalContext().getRequestMap().get(WebKeys.THEME_DISPLAY);
		final long groupId = themeDisplay.getScopeGroupId();
		final String languageKey = context.getExternalContext().getRequestLocale().toString();
		
		/***********************************************************************
		 ** Start Query All Online Help
		 * 
		 **/
		allhelpcontents = new ArrayList<OnlineContent>();
				
		DynamicQuery subQuery = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleSub", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("articleId").eqProperty("articleParent.articleId"))
		.setProjection(ProjectionFactoryUtil.max("id"));
					
		DynamicQuery query = DynamicQueryFactoryUtil.forClass(JournalArticle.class, "articleParent", PortalClassLoaderUtil.getClassLoader())
		.add(PropertyFactoryUtil.forName("id").eq(subQuery))
		.add(PropertyFactoryUtil.forName("type").in(new String[] {"user-guide"}))
		.addOrder(OrderFactoryUtil.asc("urlTitle"));
				
		List<JournalArticle> jaHelps = null;	
		try 
		{
			jaHelps = (List<JournalArticle>) JournalArticleLocalServiceUtil.dynamicQuery(query);
		} 
		catch (SystemException e1) 
		{
			e1.printStackTrace();
		}
		
		Long allHelpsId = null;
		String allHelpsArticleId = null;
		String allHelpsTitle = "";
		Double allHelpsVersion = null;
		Date allHelpsDisplayDate = null;
		int allHelpsCount = 0;
		String allHelpsCountString = null;

		for(JournalArticle allhelpsresult : jaHelps)
		{			
			allHelpsId = allhelpsresult.getId();
			allHelpsArticleId = allhelpsresult.getArticleId();
			allHelpsTitle = allhelpsresult.getTitle();
			allHelpsVersion = allhelpsresult.getVersion();
			allHelpsDisplayDate = allhelpsresult.getDisplayDate();
			allHelpsCountString = String.valueOf(allHelpsCount);
			
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
				expandoValue = ExpandoValueLocalServiceUtil.getValue(table.getTableId(), column.getColumnId(), allHelpsId);

				catlistdata = expandoValue.getData();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JournalArticle article;
			try {
				article = JournalArticleLocalServiceUtil.getArticle(groupId, allHelpsArticleId, allHelpsVersion);
				String helptitle = article.getTitle(languageKey);
				JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(groupId, article.getArticleId(), article.getTemplateId(), languageKey, themeDisplay);
				
				allhelpcontents.add(new OnlineContent(allHelpsCount,allHelpsCountString, helptitle, allHelpsDisplayDate, articleDisplay.getContent(), catlistdata));
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			allHelpsCount = allHelpsCount + 1;
		}
		/***********************************************************************
		 ** End Query All Online Help
		 * 
		 **/
	}  
    
	public List<OnlineContent> getAllhelpcontents() {  
        return allhelpcontents;  
    }
}
