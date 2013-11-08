package my.ep.web.backingbean.fl.pendingdo;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;

@ManagedBean(name = "pendingListBackingBean")
@RequestScoped
public class PendingDoListBackingBean extends BaseBackingBean<DeliveryOrderManagedBean> {/*
	public static final String NAV_TO_CREATE_DO = "createDo";
	public static final String NAV_TO_FL_ORDER = "flOrder";
	private static Logger log = Logger.getLogger(PurchaseRequestBackingBean.class);

	public PendingDoListBackingBean() {
		FulfilmentResDTO resDTO = retrievePendingDoItems();
		getDeliveryAddress();
	}
	public FulfilmentResDTO retrievePendingDoItems() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlFulfilmentOrder> pendingDoList = filmentOrderSO.getPendingDoList();
		this.getManagedBean().setPendingDoList(pendingDoList);
		return resDTO;

	}
	public FulfilmentResDTO getDeliveryAddress() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlDeliveryAddress> deliveryAddressList = filmentOrderSO.getDeliveryAddressList();
		this.getManagedBean().setDeliveryAddressList(deliveryAddressList);
		return resDTO;
		
	}
	public String navigateToDO(int index) {
		this.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		return NAV_TO_CREATE_DO;

	}

	public String navigateToFlOrder(int index) {
		String fulfilmentOrderId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentOrderId");
		String statusId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusId");
		String fulfilmentReqId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentReqId");
		System.out.println("fulfilmentOrderId::: " + fulfilmentOrderId);
		System.out.println("fulfilmentReqId::: " + fulfilmentReqId);
		System.out.println("statusId::: " + statusId);
		this.getManagedBean().setFulfilmentOrder(this.getManagedBean().getPendingDoList().get(index));
		this.getManagedBean().setFulfilmentOrderId(fulfilmentOrderId);
		this.getManagedBean().setStatusId(statusId);
		this.getManagedBean().setFulfillmentReqId(fulfilmentReqId);
		//return NAV_TO_CREATE_DO;
		return NAV_TO_FL_ORDER;

	}

*/}
