/**
 * 
 */
package my.ep.web.managedbean.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.sm.MofRegistrationUtilBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class MofRegistrationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MofRegistrationManagedBean() {
	}

	private List<MofRegistrationUtilBean> selectedUtilList;

	public List<MofRegistrationUtilBean> getSelectedUtilList() {
		return selectedUtilList;
	}

	public void setSelectedUtilList(
			List<MofRegistrationUtilBean> selectedUtilList) {
		this.selectedUtilList = selectedUtilList;
	}

	/*
	 * public MofRegistrationManagedBean(List<Car> data) { super(data); }
	 */

}