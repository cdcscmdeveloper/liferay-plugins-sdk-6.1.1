package my.ep.web.managedbean.sm.compexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * ManagedBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "compExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private SmSupplier supplier;
	private boolean agreeTnC;

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	public boolean isAgreeTnC() {
		return agreeTnC;
	}

	public void setAgreeTnC(boolean agreeTnC) {
		this.agreeTnC = agreeTnC;
	}

}