package my.ep.web.backingbean.fl.po;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.FlProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
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
//			SmSupplier smSupplier = new SmSupplier();
//			smSupplier.setSupplierId(1L);
//			flRequest.setSmSupplier(smSupplier);
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			reqDTO.setFlFulfilmentRequest(flRequest);

			if(getManagedBean().isContractRequest() == Boolean.TRUE){
				reqDTO.setContractRequest(getManagedBean().isContractRequest());
				checkValidity(flRequest);
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
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			FulfilmentResDTO resDTO = fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700011;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, 
						null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_PURCHASE_REQUEST), getManagedBean().getFulfilmentOrder().getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, 
								new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}	
			else {
				throw new EpException("Failed.");
			}

		}catch(Exception ex){
			log.error("Exception>>", ex);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
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
		//System.out.println("getSelectedFlOrder: "+getManagedBean().getSelectedFlOrder().getFulfilmentOrderId());
		//		this.getManagedBean().setFulfilmentOrder(this.getManagedBean().getPendingDoList().get(index));
		String statusDesc = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("status");
		//System.out.println("Status:::: "+statusDesc);
		FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		flReqDTO.setFlRequestId(getManagedBean().getSelectedFlOrder().getFlFulfilmentRequest().getFulfilmentReqId());
		IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
		getManagedBean().setFulfilmentOrder(getManagedBean().getSelectedFlOrder());

		DeliveryOrderBackingBean doMB = new DeliveryOrderBackingBean();
		doMB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
		doMB.getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
		doMB.getManagedBean().setStatusDesc(statusDesc);
		doMB.getManagedBean().setFulfilmentOrder(getManagedBean().getSelectedFlOrder());
		combineSupAddress(resDTO.getFlFulfilmentRequest());

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

	

	public void preRenderViewCR() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			if (res.getProcessName().equalsIgnoreCase(FlProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();

				//flReqDTO.setFlOrderId(stopFlData.getFulfillmentOrderId());
				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);

			}else {
				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();

				resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setContractRequest(Boolean.TRUE);


			}  
		}
		
	}
	
	private IFulfilmentOrderSO getFulfilmentOrderSO() {
		return (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
	}
	
	public void preRenderViewPR() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		//		String loginId = originalServletRequest.getParameter("loginId");
		System.out.println("taskId:" + taskId);
// temp for delivery term days
		getManagedBean().setTermDays("Days");
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
		if(null==res.getProcessName() || res.getProcessName().equalsIgnoreCase("")){
			System.out.println("Process name is NUll");
		} else if (FlProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.equalsIgnoreCase(res.getProcessName())) {
			FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			//flReqDTO.setFlOrderId(stopFlData.getFullfillmentRequestId());
			
			FulfilmentResDTO resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
			getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setContractRequest(Boolean.TRUE);
			combineSupAddress(resDTO.getFlFulfilmentRequest());
		} else {
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			IFulfilmentRequestSO fulfilmentSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;
			flReqDTO.setFlRequestId(flPurchaseOrd.getOrderDetails().getRequestId());
			resDTO = fulfilmentSO.retrieveFulfilmentRequest(flReqDTO);
			
			combineSupAddress(resDTO.getFlFulfilmentRequest());
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			if(resDTO.getFlFulfilmentRequest().getDocType().equalsIgnoreCase("PR")){
				flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), resDTO.getAttachmentList());
			}else{
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			
			getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			getManagedBean().setDocType(resDTO.getFlFulfilmentRequest().getDocType());
			
			flReqDTO.setFlOrderId(resDTO.getFlFulfilmentRequest().getFlFulfilmentOrder().getFulfilmentOrderId());
			resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
			getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
		}  
	}
	
	public void factoringChecked(AjaxBehaviorEvent e) {
		
		System.out.println("====enter process checked====");
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setFactoringCheck(id);
	}
	 public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    	StringBuilder address = new StringBuilder();
	    	String strSpace = " ";
	    	if(null != fulfilmentRequest.getSupplierBranch()){
	    		if(null != fulfilmentRequest.getSupplierBranch().getSmAddress()){
	    			if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    	    		address.append(strSpace);
	    	    		
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    	    		address.append("\n");
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	    	}
	    	    	if(null != address.toString()){
	    	    		System.out.println("addresss     "+address.toString());
	    	    	}
	    		}
	    	}	
	    	
	    	this.getManagedBean().setSupplierAddress(address.toString());
	    }
	 public void retrieveOrderAmount(FlFulfilmentItemAddr fulfilmentItemAddr){
			int unitPrice = 0;
			int ordQnty = 0;
			int ordAmoun = 0;
			if(null != fulfilmentItemAddr.getUnitPrice())
				unitPrice= fulfilmentItemAddr.getUnitPrice().intValue();
			if(null != fulfilmentItemAddr.getOrderedQty())
				ordQnty = fulfilmentItemAddr.getOrderedQty().intValue();
			ordAmoun =((unitPrice) * (ordQnty));
			BigDecimal ordAmount = new BigDecimal("0");
			ordAmount=new BigDecimal(ordAmoun);
			fulfilmentItemAddr.setOrderedAmt(ordAmount);
		}
		
		public void checkValidity(FlFulfilmentRequest flRequest) {
			Date date = new Date();
			
			if(flRequest.getCtContract().isRecordStatus()){
				if(date.compareTo(flRequest.getCtContract().getLatestContractVer().getCtContractValue().getExpDate()) >= 0 ){
					throw new EpException("Contract has been expired");
				}
			}else{
				throw new EpException("Contract has been suspended/terminated");
			}
		}
}
