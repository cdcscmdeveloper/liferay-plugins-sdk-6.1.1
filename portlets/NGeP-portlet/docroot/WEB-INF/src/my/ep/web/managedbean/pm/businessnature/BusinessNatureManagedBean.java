package my.ep.web.managedbean.pm.businessnature;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "businessNatureManagedBean")
@SessionScoped
public class BusinessNatureManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	/**
	 * business nature
	 * search variable
	 */	
	private PmBusinessNature pmBusinessNature;
	
	
	public PmBusinessNature getPmBusinessNature() {
		return pmBusinessNature;
	}
	public void setPmBusinessNature(PmBusinessNature pmBusinessNature) {
		this.pmBusinessNature = pmBusinessNature;
	}

}
