package my.ep.web.managedbean.pm.factoringCompanie;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "FactoringMB")
@SessionScoped
public class FactoringManageBean extends BaseManagedBean{

 List<PmFinancialOrg> factoringCompanies;

 
public FactoringManageBean() {
	
}

public List<PmFinancialOrg> getFactoringCompanies() {
	return factoringCompanies;
}

public void setFactoringCompanies(List<PmFinancialOrg> factoringCompanies) {
	this.factoringCompanies = factoringCompanies;
} 
 
}
