package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.RowEditEvent;

import my.ep.web.managedbean.ss.directpurchase.SimpleQuoteBidManagedBean;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;

@ManagedBean
@SessionScoped
public class SimpleQuoteBidBackingBean extends
		BaseBackingBean<SimpleQuoteBidManagedBean> {

	private List<SimpleQuoteBidBean> utilList;

	public SimpleQuoteBidBackingBean() {

		utilList = new ArrayList<SimpleQuoteBidBean>();

		prepareUtilList();
	}

	public List<SimpleQuoteBidBean> getUtilList() {
		return utilList;
	}

	public void setUtilList(List<SimpleQuoteBidBean> utilList) {
		this.utilList = utilList;
	}

	private List<SimpleQuoteBidBean> prepareUtilList() {
		utilList.add(new SimpleQuoteBidBean(1, "Supplier1", "Item1", 1000, " ",
				3, 3000));
		utilList.add(new SimpleQuoteBidBean(2, "Supplier2", "Item2", 1000, " ",
				4, 4000));
		utilList.add(new SimpleQuoteBidBean(3, "Supplier3", "Item3", 1000, " ",
				5, 5000));
		utilList.add(new SimpleQuoteBidBean(4, "Supplier4", "Item4", 1000, " ",
				6, 6000));
		utilList.add(new SimpleQuoteBidBean(5, "Supplier5", "Item1", 1000, " ",
				7, 7000));

		return utilList;
	}

	
	public void onEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Edited",
				((QtnTenderBean) event.getObject()).getDeptCode());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Cancelled",
				((QtnTenderBean) event.getObject()).getDeptCode());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
}
