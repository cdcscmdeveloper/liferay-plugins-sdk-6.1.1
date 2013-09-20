package my.ep.web.managedbean.ss.directpurchase;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.ss.directpurchase.SimpleQuoteBidBean;
import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean
@SessionScoped
public class SimpleQuoteBidManagedBean extends BaseManagedBean{

	
	private static final long serialVersionUID = 1L;

	public SimpleQuoteBidManagedBean() {
	}

	
	private List<SimpleQuoteBidBean> selectedUtilList;

	public List<SimpleQuoteBidBean> getSelectedUtilList() {
		return selectedUtilList;
	}

	public void setSelectedUtilList(List<SimpleQuoteBidBean> selectedUtilList) {
		this.selectedUtilList = selectedUtilList;
	}
	
	

	
	
}
