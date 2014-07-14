package my.ep.web.managedbean.pm.settings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "ruleCatalogueMB")
@SessionScoped
public class RuleCatalogueManageBean extends BaseManagedBean {
	private Integer ruleCatTabActiveIndex = 0;

	public Integer getRuleCatTabActiveIndex() {
		return ruleCatTabActiveIndex;
	}

	public void setRuleCatTabActiveIndex(Integer ruleCatTabActiveIndex) {
		this.ruleCatTabActiveIndex = ruleCatTabActiveIndex;
	}
	
	
}
