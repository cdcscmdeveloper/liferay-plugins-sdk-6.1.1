package my.ep.web.backingbean.pm.factoringCompanies;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.factoring.IFactoringCompaniesSO;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.factoringCompanie.FactoringManageBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;


@ManagedBean(name = "factoringBackingBean")
@ViewScoped
public class FactoringBackingBean extends
		BaseBackingBean<FactoringManageBean> {
	private static Logger log = Logger
			.getLogger(FactoringManageBean.class);

	public FactoringBackingBean() {
		super();
	}

	public String preRenderView() {
		log.info("...........preRenderView............");
		
		IFactoringCompaniesSO factoringCompaniesSO = null;
		try {
			factoringCompaniesSO = (IFactoringCompaniesSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IFactoringCompaniesSO.class);
			
			List<PmFinancialOrg> result = factoringCompaniesSO.getFactoringCompanies();
			this.getManagedBean().setFactoringCompanies(result);
	

			System.out
					.println("...................result.size()="
							+ result.size());

			// ///////////////////////////////
			// ///////////////////////////////
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "factoringCompanies");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
