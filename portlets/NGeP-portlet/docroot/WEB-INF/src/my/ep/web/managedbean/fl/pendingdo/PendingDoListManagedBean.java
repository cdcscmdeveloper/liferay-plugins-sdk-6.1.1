package my.ep.web.managedbean.fl.pendingdo;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

@ManagedBean
@SessionScoped
public class PendingDoListManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(PendingDoListManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private List<FlFulfilmentOrder> pendingDoList;

	/**
	 * @return the pendingDoList
	 */
	public List<FlFulfilmentOrder> getPendingDoList() {
		return pendingDoList;
	}

	/**
	 * @param pendingDoList the pendingDoList to set
	 */
	public void setPendingDoList(List<FlFulfilmentOrder> pendingDoList) {
		this.pendingDoList = pendingDoList;
	}
}
