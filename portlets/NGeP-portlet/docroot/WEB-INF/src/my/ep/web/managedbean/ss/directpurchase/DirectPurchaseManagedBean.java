package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScDpRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;

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
	private	List<ScRequestListItem> requestListItems = new ArrayList<ScRequestListItem>();
	private	List<ScRequestSupplierItem> recommendRequestListSupplierItems = new ArrayList<ScRequestSupplierItem>();
	private	ScRequestSupplierItem	selectedItem	=	new ScRequestSupplierItem();
	private	List<ScRequestSupplierItem>	selectedSupplierItems	=	new ArrayList<ScRequestSupplierItem>();
	private	String	requestListRemark;
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
	public List<ScRequestSupplierItem>	getScRequestSupplierItems(){
		List<ScRequestSupplierItem> results	=	new ArrayList<ScRequestSupplierItem>();
		if(requestList.getRequestItems() != null && requestList.getRequestItems().size() > 0){
			for(ScRequestListItem requestItem: requestList.getRequestItems()){
				results.addAll(requestItem.getSupplierItems());
			}
		}
		return results;
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
		ScRequestSupplierItem	supplierItem	=	new ScRequestSupplierItem(3L,1L,1000,new Double(4.5));
		supplierItem.setScRequestListItem(requestListItem1);
		supplierItems1.add(supplierItem);
		
		supplierItem	=	new ScRequestSupplierItem(21L,1L,1000,new Double(4.6));
		supplierItem.setScRequestListItem(requestListItem1);
		supplierItems1.add(supplierItem);
		
		supplierItem	=	new ScRequestSupplierItem(22L,3L,1000,new Double(4.4));
		supplierItem.setScRequestListItem(requestListItem1);
		supplierItems1.add(supplierItem);
		
		requestListItem1.setSupplierItems(supplierItems1);
		requestListItems.add(requestListItem1);
		
		ScRequestListItem requestListItem2 = new ScRequestListItem(2L,"Item2",2000,"KG");
		List<ScRequestSupplierItem> supplierItems2 = new ArrayList<ScRequestSupplierItem>();
		
		supplierItem	=	new ScRequestSupplierItem(31L,1L,1000,new Double(4.5));
		supplierItem.setScRequestListItem(requestListItem2);
		supplierItems2.add(supplierItem);
		
		/*
		supplierItems2.add(new ScRequestSupplierItem(21L,3L,1000,new Double(4.6)));
		supplierItems2.add(new ScRequestSupplierItem(33L,3L,1000,new Double(4.4)));*/
		requestListItem2.setSupplierItems(supplierItems2);
		requestListItems.add(requestListItem2);
		
		requestList.setRequestItems(requestListItems);
		recommendRequestListSupplierItems.add(new ScRequestSupplierItem(1L,1L,1000,new Double(4.5)));
		recommendRequestListSupplierItems.add(new ScRequestSupplierItem(5L,2L,1000,new Double(4.6)));
	}
	public void prepareRequestNoteDtl(){
		List<ScRequestNote>		requestNotes	=	new ArrayList<ScRequestNote>();
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

	public String getRequestListRemark() {
		return requestListRemark;
	}

	public void setRequestListRemark(String requestListRemark) {
		this.requestListRemark = requestListRemark;
	}

	public List<ScRequestSupplierItem> getSelectedSupplierItems() {
		return selectedSupplierItems;
	}

	public void setSelectedSupplierItems(List<ScRequestSupplierItem> selectedSupplierItems) {
		this.selectedSupplierItems = selectedSupplierItems;
	}
	
}
