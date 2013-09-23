package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ss.directpurchase.RequestListItem;
import my.ep.persistence.entity.ss.directpurchase.RequestListSupplierItem;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
/**
 * 
 * @author Tran Ngoc Hoa
 *
 */
@ManagedBean(name="directPurchaseMB")
@SessionScoped
public class DirectPurchaseManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(DirectPurchaseManagedBean.class);
	
	
	private	String	action	=	SourcingConst.CREATE_DIRECT_PURCHASE;
	private	List<RequestListItem> requestListItems;
	
	public DirectPurchaseManagedBean() {
		super();
		prepareDummyData();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<RequestListItem> getRequestListItems() {
		return requestListItems;
	}

	public void setRequestListItems(List<RequestListItem> requestListItems) {
		this.requestListItems = requestListItems;
	}
	
	private	void prepareDummyData(){
		requestListItems	=	new ArrayList<RequestListItem>();
		RequestListItem requestListItem1 = new RequestListItem("Item1",1000,"KG");
		List<RequestListSupplierItem> supplierItems1 = new ArrayList<RequestListSupplierItem>();
		supplierItems1.add(new RequestListSupplierItem(1L,1L,1000,new Double(4.5)));
		supplierItems1.add(new RequestListSupplierItem(2L,2L,1000,new Double(4.6)));
		supplierItems1.add(new RequestListSupplierItem(3L,3L,1000,new Double(4.4)));
		requestListItem1.setSupplierItems(supplierItems1);
		requestListItems.add(requestListItem1);
		
		RequestListItem requestListItem2 = new RequestListItem("Item2",1000,"KG");
		List<RequestListSupplierItem> supplierItems2 = new ArrayList<RequestListSupplierItem>();
		supplierItems2.add(new RequestListSupplierItem(4L,1L,1000,new Double(4.5)));
		supplierItems2.add(new RequestListSupplierItem(5L,2L,1000,new Double(4.6)));
		supplierItems2.add(new RequestListSupplierItem(6L,3L,1000,new Double(4.4)));
		requestListItem2.setSupplierItems(supplierItems2);
		requestListItems.add(requestListItem2);
	}
}
