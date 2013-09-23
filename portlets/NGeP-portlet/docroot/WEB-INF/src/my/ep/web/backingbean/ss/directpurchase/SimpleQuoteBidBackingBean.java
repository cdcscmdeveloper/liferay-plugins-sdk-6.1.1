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
import org.primefaces.model.DualListModel; 


@ManagedBean
@SessionScoped
public class SimpleQuoteBidBackingBean extends
		BaseBackingBean<SimpleQuoteBidManagedBean> {

	private List<SimpleQuoteBidBean> utilList;
	private DualListModel<String> items;
	public SimpleQuoteBidBackingBean() {

		utilList = new ArrayList<SimpleQuoteBidBean>();
		List<String> citiesSource = new ArrayList<String>();  
        List<String> citiesTarget = new ArrayList<String>();  
          
        citiesSource.add("Item1");  
        citiesSource.add("Item2");  
        citiesSource.add("Item3");  
        citiesSource.add("Item4");  
        citiesSource.add("Item5");  
          
        items = new DualListModel<String>(citiesSource, citiesTarget); 
		prepareUtilList();
	}

	public List<SimpleQuoteBidBean> getUtilList() {
		return utilList;
	}

	public void setUtilList(List<SimpleQuoteBidBean> utilList) {
		this.utilList = utilList;
	}
	
	

	

	public DualListModel<String> getItems() {
		return items;
	}

	public void setItems(DualListModel<String> items) {
		this.items = items;
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
				((SimpleQuoteBidBean) event.getObject()).getDescription());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Cancelled",
				((SimpleQuoteBidBean) event.getObject()).getDescription());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
}
