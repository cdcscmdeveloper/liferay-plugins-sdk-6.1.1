package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
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
	private	ScRequestList	requestList;
	private	List<ScRequestListItem> requestListItems;
	private	List<ScRequestSupplierItem> recommendRequestListSupplierItems;
	private	ScRequestSupplierItem	selectedItem	=	new ScRequestSupplierItem();
	private	ScPrInquiry	scPrInquiry = new ScPrInquiry();
	
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

	public List<ScRequestListItem> getRequestListItems() {
		return requestListItems;
	}

	public void setRequestListItems(List<ScRequestListItem> requestListItems) {
		this.requestListItems = requestListItems;
	}
	
	public List<ScRequestSupplierItem> getRecommendRequestListSupplierItems() {
		return recommendRequestListSupplierItems;
	}

	public void setRecommendRequestListSupplierItems(List<ScRequestSupplierItem> recommendRequestListSupplierItems) {
		this.recommendRequestListSupplierItems = recommendRequestListSupplierItems;
	}

	private	void prepareDummyData(){
		requestList	=	new	ScRequestList();
		//TODO hard code org profile first
		PmOrgProfile	orgProfile	=	new PmOrgProfile();
		orgProfile.setOrgProfileId(1L);
		requestList.setPmOrgProfile(orgProfile);
		
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(61000L);
		requestList.setPmStatus(status);
		
		requestListItems	=	new ArrayList<ScRequestListItem>();
		recommendRequestListSupplierItems = new ArrayList<ScRequestSupplierItem>();
		
		ScRequestListItem requestListItem1 = new ScRequestListItem(1L,"Item1",1000,"KG");
		List<ScRequestSupplierItem> supplierItems1 = new ArrayList<ScRequestSupplierItem>();
		
		supplierItems1.add(new ScRequestSupplierItem(3L,1L,1000,new Double(4.5)));
		supplierItems1.add(new ScRequestSupplierItem(21L,1L,1000,new Double(4.6)));
		supplierItems1.add(new ScRequestSupplierItem(22L,3L,1000,new Double(4.4)));
		requestListItem1.setSupplierItems(supplierItems1);
		requestListItems.add(requestListItem1);
		
		ScRequestListItem requestListItem2 = new ScRequestListItem(2L,"Item2",1000,"KG");
		List<ScRequestSupplierItem> supplierItems2 = new ArrayList<ScRequestSupplierItem>();
		supplierItems2.add(new ScRequestSupplierItem(31L,1L,1000,new Double(4.5)));/*
		supplierItems2.add(new ScRequestSupplierItem(21L,3L,1000,new Double(4.6)));
		supplierItems2.add(new ScRequestSupplierItem(33L,3L,1000,new Double(4.4)));*/
		requestListItem2.setSupplierItems(supplierItems2);
		requestListItems.add(requestListItem2);
		
		requestList.setRequestItems(requestListItems);
		recommendRequestListSupplierItems.add(new ScRequestSupplierItem(1L,1L,1000,new Double(4.5)));
		recommendRequestListSupplierItems.add(new ScRequestSupplierItem(5L,2L,1000,new Double(4.6)));
	}

	public ScRequestList getRequestList() {
		return requestList;
	}

	public void setRequestList(ScRequestList requestList) {
		this.requestList = requestList;
	}

	public ScRequestSupplierItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ScRequestSupplierItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public ScPrInquiry getScPrInquiry() {
		return scPrInquiry;
	}

	public void setScPrInquiry(ScPrInquiry scPrInquiry) {
		this.scPrInquiry = scPrInquiry;
	}
	
}
