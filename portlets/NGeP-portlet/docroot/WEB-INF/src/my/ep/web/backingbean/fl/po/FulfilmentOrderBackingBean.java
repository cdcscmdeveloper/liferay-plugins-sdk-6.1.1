package my.ep.web.backingbean.fl.po;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
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
		retrievePendingDo();
//		getDeliveryAddress();
		getDeliveryOrderStatus();
		
	}
	
	public String fulfillmentOrderAction(String action) {
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(27);
			flRequest.setSmSupplier(smSupplier);
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			reqDTO.setFlFulfilmentRequest(flRequest);
			
			if(getManagedBean().isContractRequest() == Boolean.TRUE){
				reqDTO.setContractRequest(getManagedBean().isContractRequest());
			}else{
				reqDTO.setContractRequest(false);
			}
			
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			System.out.println("temploginId: "+ getManagedBean().getTempLoginId());
			if(null==getManagedBean().getTempLoginId() || "".equals(getManagedBean().getTempLoginId())){
				reqDTO.setTempLogin("roslan");
			}else{
				reqDTO.setTempLogin(getManagedBean().getTempLoginId());
			}
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+flRequest.getFulfilmentReqId();
				if(flRequest.getDocNo()!=null) {
					message = message + ". Doc No. : "+flRequest.getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}

		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: ",e.getMessage()));
			log.error("Exception>>", e);
		}finally {
			fulfilmentOrderSO = null;
		}
		return null;
	}
	
	public FulfilmentResDTO retrievePendingDo() {
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
	public void delAddrList(){
		IFulfilmentOrderSO filmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlRequestId(getManagedBean().getFulfilmentOrder().getFlFulfilmentRequest().getFulfilmentReqId());
		FulfilmentResDTO resDTO = filmentOrderSO.getDeliveryAddressListById(reqDTO);
		this.getManagedBean().setDeliveryAddressList(resDTO.getDelAddrList());
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
		System.out.println("getSelectedFlOrder: "+getManagedBean().getSelectedFlOrder().getFulfilmentOrderId());
//		this.getManagedBean().setFulfilmentOrder(this.getManagedBean().getPendingDoList().get(index));
		getManagedBean().setFulfilmentOrder(getManagedBean().getSelectedFlOrder());
		
		DeliveryOrderBackingBean doMB = new DeliveryOrderBackingBean();
		doMB.getManagedBean().setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
//		doMB.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));
		
		return NAV_TO_CREATE_DO;

	}
	
	public void stopDelivery() {
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
		IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
		flOrderSO.stopDeliveryWSDL(reqDTO);
		
	}
	
	public void selectedAddrRow(){
		//TODO
//		getManagedBean().setSelectedRowFlg(true);
	}
	
	public void updateDeliveryAddrText(){
		getManagedBean().setSelectedLovDelAddr(getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress1()
				+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress2()
				+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress3()
				+" "+getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress().getAddress4());

	}
	
}
