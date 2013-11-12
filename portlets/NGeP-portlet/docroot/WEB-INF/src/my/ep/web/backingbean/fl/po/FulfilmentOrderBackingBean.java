package my.ep.web.backingbean.fl.po;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
//import my.ep.web.backingbean.fl.frn.CreateFRNBackingBean;

@ManagedBean(name="flOrderBB")
@RequestScoped
public class FulfilmentOrderBackingBean extends BaseBackingBean<FulfilmentOrderManagedBean>{
	
	public static final String NAV_TO_CREATE_DO = "createDo";
	public static final String NAV_TO_FL_ORDER = "flOrder";

	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );

	public FulfilmentOrderBackingBean(){
		retrievePendingDoItems();
		getDeliveryAddress();
		getDeliveryOrderStatus();

	}
	@PostConstruct
	public void init() {
//		if (getManagedBean().getFulfilmentRequest() == null) {
//			getManagedBean().setFulfilmentRequest(new FlFulfilmentRequest());
//			getManagedBean().getFulfilmentRequest().getSmSupplier().setSupplierId(27L);
//		}
	}
	public void fulfillmentOrderAction(String action) {
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		try {
			
			String actionName=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionName");
			System.out.println("action name in BB "+actionName);
			System.out.println("action in BB "+action);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlRemark flRemark = getManagedBean().getFlRemark();
			FlAttachment flAttachment = this.getManagedBean().getFlAttachment();
			FlFulfilmentRequest flFulfilmentRequest = getManagedBean().getFulfilmentRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(27);
			flFulfilmentRequest.setSmSupplier(smSupplier);
			String fileName = this.getManagedBean().getFlAttachment().getFileName();
			String fileDesc = this.getManagedBean().getFlAttachment().getFileDescription();
			String filePath = this.getManagedBean().getFlAttachment().getFilePath();
			flAttachment.setDocType("txt");
			flAttachment.setDocId(Long.valueOf(44));
			flAttachment.setFileName(fileName);
			flAttachment.setFileDescription(fileDesc);
			flAttachment.setFilePath(filePath);
			//log.info("flRemark.getRemark() in backing beans::"+ flRemark.getRemark());
			flRemark.setDocId(Long.valueOf(1));
			flRemark.setDocType("tex");
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			reqDTO.setFlRemark(flRemark);
			reqDTO.setFlAttachment(flAttachment);
			reqDTO.setFlFulfilmentRequest(flFulfilmentRequest);
			
			if(getManagedBean().isContractRequest() == Boolean.TRUE){
				reqDTO.setContractRequest(getManagedBean().isContractRequest());
			}else{
				reqDTO.setContractRequest(false);
			}
			
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			//System.out.println("resDTO.isSuccessful() in backing bean:: "+ resDTO.isSuccessful());
		} catch (Exception e) {
			log.error("Exception>>", e);
		} finally {
			fulfilmentOrderSO = null;
		}
	}
	public FulfilmentResDTO retrievePendingDoItems() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlFulfilmentOrder> pendingDoList = filmentOrderSO.getPendingDoList();
		this.getManagedBean().setPendingDoList(pendingDoList);
		return resDTO;

	}
	public FulfilmentResDTO getDeliveryAddress() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlDeliveryAddress> deliveryAddressList = filmentOrderSO.getDeliveryAddressList();
		this.getManagedBean().setDeliveryAddressList(deliveryAddressList);
		return resDTO;
		
	}
	public FulfilmentResDTO getDeliveryOrderStatus() {
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		List<FlDeliveryOrder> deliveryOrderStatusList = filmentOrderSO.getDeliveryOrderStatusList();
		this.getManagedBean().setDeliveryOrderStatusList(deliveryOrderStatusList);
		return resDTO;
		
	}
	public String navigateToDO(int index) {
		DeliveryOrderBackingBean doMB=new DeliveryOrderBackingBean();
		FlFulfilmentOrder fulfilmentOrder=this.getManagedBean().getFulfilmentOrder();
		doMB.getManagedBean().setFulfilmentOrder(fulfilmentOrder);
		doMB.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		//this.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		return NAV_TO_CREATE_DO;

	}

	public String navigateToFlOrder(int index) {
		this.getManagedBean().setFulfilmentOrder(this.getManagedBean().getPendingDoList().get(index));
		return NAV_TO_FL_ORDER;

	}
}
