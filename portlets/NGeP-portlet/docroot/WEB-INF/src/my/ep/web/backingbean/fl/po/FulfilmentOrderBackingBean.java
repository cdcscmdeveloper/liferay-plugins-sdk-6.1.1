package my.ep.web.backingbean.fl.po;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.FlProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;
//import my.ep.web.backingbean.fl.frn.CreateFRNBackingBean;

@ManagedBean(name="flOrderBB")
@RequestScoped
public class FulfilmentOrderBackingBean extends BaseBackingBean<FulfilmentOrderManagedBean>{

	public static final String NAV_TO_CREATE_DO = "createDo";
	public static final String NAV_TO_FL_ORDER = "flOrder";


	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public FulfilmentOrderBackingBean(){
		retrievePendingDo();
		//		getDeliveryAddress();
		getDeliveryOrderStatus();

	}

	public String fulfillmentOrderAction(String action) {
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
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
			reqDTO.setToken(token);
			if(getManagedBean().isContractRequest() == Boolean.FALSE){
				reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
				reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			}
			reqDTO.setStopRequestId(getManagedBean().getStopRequestId());
			reqDTO.setContractOrder(getManagedBean().isContractRequest());
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
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		flReqDTO.setFlRequestId(getManagedBean().getSelectedFlOrder().getFlFulfilmentRequest().getFulfilmentReqId());
		IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), flBB.getManagedBean().getAttachmentList());
		getManagedBean().setFulfilmentOrder(getManagedBean().getSelectedFlOrder());

		DeliveryOrderBackingBean doMB = new DeliveryOrderBackingBean();
		doMB.getManagedBean().setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
		//		doMB.getManagedBean().setDeliveryAddress(this.getManagedBean().getDeliveryAddressList().get(index));

		return NAV_TO_CREATE_DO;

	}

	public void stopDelivery() {
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		Long orderId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orderId"));
		IFulfilmentOrderSO flOrderSO = BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
		FulfilmentReqDTO flDTO = new FulfilmentReqDTO();
		flDTO.setFlOrderId(orderId);
		getManagedBean().setFulfilmentOrder(flOrderSO.retrieveFulfilmentOrder(flDTO).getFlOrder());
		reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
		//		IFulfilmentOrderSO flOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
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

	public void preRenderViewCR() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			System.out.println("res.getProcessName(): "+res.getProcessName());
			if (res.getProcessName().equalsIgnoreCase(FlProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();

				flReqDTO.setFlOrderId(stopFlData.getFullfillmentRequestId());
				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				//			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);

			}else {
				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();

				FLContractOrderDataType coObj = (FLContractOrderDataType)obj;
				if(coObj.getContractRequestId().intValue() == 0){
					flReqDTO.setFlRequestId(coObj.getParentContractRequestId());
				}else{
					flReqDTO.setFlRequestId(coObj.getContractRequestId());
				}

				flReqDTO.setSelectedApprovers(coObj.getApproverList());

				resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

				//			FulfilmentOrderBackingBean florderBB  = new FulfilmentOrderBackingBean();
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setContractRequest(Boolean.TRUE);


				//			FacesContext fc = FacesContext.getCurrentInstance();
				//			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/fl/contractorder/coBasePage.xhtml");
			}  
		}
		
	}

	public void preRenderViewPR() {
		//		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		//		String loginId = originalServletRequest.getParameter("loginId");
		System.out.println("taskId:" + taskId);
		//		System.out.println("loginId:" + loginId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		
		if (res.getProcessName().equalsIgnoreCase(FlProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME)){
			IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();

			flReqDTO.setFlOrderId(stopFlData.getFullfillmentRequestId());
			FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(flReqDTO);
			//			FulfilmentOrderBackingBean flOrderBB = new FulfilmentOrderBackingBean();
			getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			//			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
			getManagedBean().setContractRequest(Boolean.TRUE);

		}else {
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;

			flReqDTO.setFlRequestId(flPurchaseOrd.getPurchaseRequestId());
			flReqDTO.setSelectedApprovers(flPurchaseOrd.getApproverList());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			flReqDTO.setFlFulfilmentRequest(resDTO.getFlFulfilmentRequest());

			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), flBB.getManagedBean().getAttachmentList());
			//		FulfilmentOrderBackingBean florderBB = new FulfilmentOrderBackingBean();
			getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			//			florderBB.getManagedBean().setTempLoginId(this.getManagedBean().getTempLoginId());

			//			FacesContext fc = FacesContext.getCurrentInstance();
			//			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/xhtml/fl/contractorder/coBasePage.xhtml");
		}  
	}

}
