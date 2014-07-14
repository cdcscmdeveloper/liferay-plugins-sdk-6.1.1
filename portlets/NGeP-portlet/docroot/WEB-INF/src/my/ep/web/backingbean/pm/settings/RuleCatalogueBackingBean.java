package my.ep.web.backingbean.pm.settings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name = "ruleCatalogueBackingBean")
@ViewScoped
public class RuleCatalogueBackingBean extends
		BaseBackingBean<RuleCatalogueManageBean> {
	private static Logger log = Logger
			.getLogger(RuleCatalogueBackingBean.class);

	public RuleCatalogueBackingBean() {
		
	}
	
	public void saveRuleCatalog(){
		
	}
}
