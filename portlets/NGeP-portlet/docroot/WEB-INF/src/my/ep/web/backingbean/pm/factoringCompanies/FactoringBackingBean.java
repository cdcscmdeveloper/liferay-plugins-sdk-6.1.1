package my.ep.web.backingbean.pm.factoringCompanies;

import java.io.IOException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.factoring.IFactoringCompaniesSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.factoringCompanie.FactoringManageBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
/**
 * The backing bean for list of factoring companies
 * 
 * @author Ken
 * @since 2.09.2015
 */
@ManagedBean(name = "factoringBackingBean")
@ViewScoped
public class FactoringBackingBean extends
		BaseBackingBean<FactoringManageBean> {
	private static Logger log = Logger
			.getLogger(FactoringManageBean.class);

	public FactoringBackingBean() {
		super();
	}

	/**
	 * Pre render Factoring Companies page
	 * 
	 * @return
	 */
	public String preRenderView() {
		log.info("........... factoringCompanies preRenderView............");		
		IFactoringCompaniesSO factoringCompaniesSO = null;
		try {
			factoringCompaniesSO = (IFactoringCompaniesSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFactoringCompaniesSO.class);
			
			List<PmFinancialOrg> result = factoringCompaniesSO.getFactoringCompanies();
			this.getManagedBean().setFactoringCompanies(result);
	
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "factoringCompanies");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public void pageNavigate() throws Exception{
		pageRedirect("view-factoring-company");		
	}
	
	/**
	 * Page Redirect
	 * @param url
	 * @throws IOException
	 * @throws PortalException
	 * @throws SystemException
	 */
	public void pageRedirect(String url)  throws IOException,PortalException, SystemException {
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String commonUrl = themeDisplay.getURLCurrent().substring(0,themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		externalContext.redirect(commonUrl + "/" + url);
	}

}
