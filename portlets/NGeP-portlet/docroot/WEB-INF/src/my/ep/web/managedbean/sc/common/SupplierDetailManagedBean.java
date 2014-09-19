/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.common;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Common Supplier Details Managed Bean
 * 
 * @author Kenny
 * @since 05.07.2014
 */

@ManagedBean(name="supplierDetailMB")
@SessionScoped
public class SupplierDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	 
	private SmSupplier smSupplier;
	private String mofNo;
	
	public SupplierDetailManagedBean() {
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the mofNo
	 */
	public String getMofNo() {
		return mofNo;
	}

	/**
	 * @param mofNo the mofNo to set
	 */
	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

}
