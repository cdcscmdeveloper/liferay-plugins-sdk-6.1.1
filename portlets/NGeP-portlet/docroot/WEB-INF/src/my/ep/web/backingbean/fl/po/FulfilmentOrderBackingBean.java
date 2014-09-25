package my.ep.web.backingbean.fl.po;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.jaxb.fl.fc.FLFactoringRemoveReqType;
import my.ep.jaxb.fl.podata.FLPurchaseOrderDataType;
import my.ep.jaxb.fl.stopfullfillmentdata.FLStopFullfillmentDataType;
import my.ep.persistence.entity.fl.order.FlFulfilmentOrder;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.po.FulfilmentOrderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name="flOrderBB")
@ViewScoped
public class FulfilmentOrderBackingBean extends BaseBackingBean<FulfilmentOrderManagedBean>{

	public static final String NAV_TO_CREATE_DO = "createDo";
	public static final String NAV_TO_FL_ORDER = "flOrder";

	private static Logger log = Logger.getLogger(FulfilmentOrderBackingBean.class);
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "MM/dd/yy hh:mm a" );
	
	private IPagingDataModel<FlFulfilmentOrder> pendingDoDataModel = new PendingDoPagingDataModel();
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public FulfilmentOrderBackingBean(){
	}

	public String fulfillmentOrderAction(String action) {
		log.debug("FulfilmentOrderBackingBean.fulfillmentOrderAction()");
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				if(flBB.getManagedBean().getRemarkList().get(0).getRemark()==null || flBB.getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
				}
			}
			
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			FlFulfilmentOrder flOrder = null;
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flOrder = getManagedBean().getFulfilmentOrder();
			}
				
			
			
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
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
				if(!flBB.getManagedBean().getRemarkList().isEmpty()){
					reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
				}
				
			}
			reqDTO.setStopRequestId(getManagedBean().getStopRequestId());
			reqDTO.setContractOrder(getManagedBean().isContractRequest());
			if(getManagedBean().getFactoringCheck().equalsIgnoreCase("true")) {
				getManagedBean().getFulfilmentOrder().setFactored(true);
			} else {
				getManagedBean().getFulfilmentOrder().setPmFinancialOrg(null);
				getManagedBean().getFulfilmentOrder().setPmFinancialBranch(null);
			}
			reqDTO.setFulfilmentOrder(getManagedBean().getFulfilmentOrder());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			
			String messagePrefix = "";
			FulfilmentResDTO resDTO = null;
			if(getManagedBean().getProcessName() == null && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT))){
				resDTO = fulfilmentOrderSO.acknowledgeFulfillmentOrder(reqDTO);
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_STOP_FULFILMENT)){
				if(isFlFulfilmentApprover()) {
					reqDTO.setApprover(Boolean.TRUE);
				}
				reqDTO.setStopIns(getManagedBean().getStopIns());
				resDTO = fulfilmentOrderSO.stopFulfilment(reqDTO);
			}else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				reqDTO.setFulfilmentOrder(flOrder);
				if(flOrder.getFlFulfilmentRequest().getCtContract() != null){
					reqDTO.setContractRequest(true);
				}
				resDTO = fulfilmentOrderSO.cancelFulfillmentOrder(reqDTO);
			}else if(action.equalsIgnoreCase(FulfilmentConst.REMOVE_FACTORING) 
					|| getManagedBean().getProcessName() != null 
					&& getManagedBean().getProcessName().equalsIgnoreCase(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME)){
				resDTO = fulfilmentOrderSO.removeFactoringComp(reqDTO);
				messagePrefix = "Removal Factoring Company for ";
			}
			
			if(resDTO.isSuccessful()) {
				String messageId = MessageConst.INFO_700005;
				String documentType = ParameterConst.DOCTYPE_PURCHASE_ORDER;
				String docNo = getManagedBean().getFulfilmentOrder().getDocNo();
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if(action.equalsIgnoreCase(FulfilmentConst.STOP_FULFILMENT)) {
					documentType = ParameterConst.DOCTYPE_STOP_DELIVERY;
					docNo = resDTO.getFlStopInstr().getStopInstrNo();
					if(reqDTO.isApprover()) {
						messageId = MessageConst.INFO_700026;
					} else {
						messageId = MessageConst.INFO_700005;
					}
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{messagePrefix+getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, documentType), docNo}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}	
			else {
				throw new EpException("Failed.");
			}
		} finally {
			fulfilmentOrderSO = null;
		}
	}

	public String navigateToFlOrder(int index) {
		IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setFlOrderId(getManagedBean().getSelectedFlOrder().getFulfilmentOrderId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_DELIVERY_ORDER);
		FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(reqDTO);
		
		DeliveryOrderBackingBean doMB = new DeliveryOrderBackingBean();
		doMB.setDeliveryOrder(resDTO);
		doMB.sortOrderSummaryList(doMB.getManagedBean().getFulfilmentRequest().getFlDeliveryAddresses());
		doMB.getManagedBean().setFromPageUrl("/xhtml/fl/dopending/doPendingTable.xhtml");
		return NAV_TO_CREATE_DO;
	}

	public void selectedAddrRow(){
		//TODO
		//		getManagedBean().setSelectedRowFlg(true);
	}

	public void preRenderViewCR() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		
		if(taskId != null){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			if (res.getProcessName().equalsIgnoreCase(FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME)){
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();

				FulfilmentResDTO resDTO = orderSO.retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);
			}else {
				FLContractOrderDataType crDataType = (FLContractOrderDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				
				flReqDTO.setFlOrderId(crDataType.getOrderDetails().getOrderId());
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				getManagedBean().setContractRequest(Boolean.TRUE);
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractNo());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}  
			getManagedBean().setSelectedTabIndex(0);
		}
		
	}
	
	private IFulfilmentOrderSO getFulfilmentOrderSO() {
		return (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
	}
	
	public void preRenderViewPR() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");

		if(taskId != null){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();

			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			if(null==res.getProcessName() || res.getProcessName().equalsIgnoreCase("")){
				System.out.println("Process name is NUll");
			} else if (FLProcessNameConst.STOP_FULFILMENT_REQUEST_PROCESS_NAME.equalsIgnoreCase(res.getProcessName())) {
				FLStopFullfillmentDataType stopFlData = (FLStopFullfillmentDataType)obj;
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				flReqDTO.setFlOrderId(stopFlData.getOrderId());
				
				FulfilmentResDTO resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setStopRequestId(stopFlData.getStopRequestId().longValue());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				getManagedBean().setContractRequest(Boolean.TRUE);
			} else if(FLProcessNameConst.FC_REMOVAL_REQUEST_PROCESS_NAME.equalsIgnoreCase(res.getProcessName())){
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				FLFactoringRemoveReqType removeReqType = (FLFactoringRemoveReqType)obj;
				flReqDTO.setDocType(ParameterConst.DOCTYPE_FACTORING_COMPANY);
				flReqDTO.setFlOrderId(removeReqType.getOrderId());
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				if(null != resDTO.getFlOrder().getPmFinancialBranch()){
					getRfcAddress(null, resDTO.getFlOrder().getPmFinancialBranch().getFinancialBranchId());
				} else if(null != resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId()){
					getRfcAddress(resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId(), null);
				}				
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				getManagedBean().setProcessName(res.getProcessName());
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			}else {
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				FLPurchaseOrderDataType flPurchaseOrd = (FLPurchaseOrderDataType)obj;
				flReqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				flReqDTO.setFlOrderId(flPurchaseOrd.getOrderDetails().getOrderId());
				resDTO = getFulfilmentOrderSO().retrieveFulfilmentOrderById(flReqDTO);
				getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
				getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().setDocType(resDTO.getFlOrder().getFlFulfilmentRequest().getDocType());
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				
				FulfilmentBackingBean flBB = new FulfilmentBackingBean(resDTO.getFlOrder().getDocType(), resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(), resDTO.getAttachmentList());
			}  
			getManagedBean().setSelectedTabIndex(0);
		}
		
	}
	
	public void factoringChecked(AjaxBehaviorEvent e) {
		System.out.println("====enter process checked====");
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setFactoringCheck(id);
		
		getFactoringCompanyDtl();
		
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
		
	public void getFactoringDetails(){
		getManagedBean().getFulfilmentOrder().isFactored();
	}
	
	public void getFactoringCompanyDtl(){
		if(getManagedBean().getFulfilmentOrder().getPmFinancialOrg() == null){
			getManagedBean().getFulfilmentOrder().setPmFinancialOrg(new PmFinancialOrg());
			getManagedBean().getFulfilmentOrder().setPmFinancialBranch(new PmFinancialBranch());
		}
		
		
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType(ParameterConst.BANK_SERVICES_FACTORING_COMPANY);
		FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
		getManagedBean().setPmFinancialOrgs(resDTO.getFinancialOrgList());
	}
	public void getSelectedFactoringCompDtl(AjaxBehaviorEvent e){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		Long financialOrgId = (Long) e.getComponent().getAttributes().get("value");
		
		getManagedBean().getFulfilmentOrder().getPmFinancialBranch().setFinancialBranchId(null);
		getManagedBean().setFactoringBranchList(new ArrayList<PmFinancialBranch>());
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialOrgId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
			
			IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class); 
			FinancialOrgReqDTO financialOrgReqDTO = new FinancialOrgReqDTO();
			financialOrgReqDTO.setFinancialOrgId(financialOrgId);
			FinancialOrgResDTO financialOrgResDTO = financialOrgSO.retrieveFinancialOrgById(financialOrgReqDTO);
			if(financialOrgResDTO.getPmFinancialOrg().getPmFinancialBranchs() != null && !financialOrgResDTO.getPmFinancialOrg().getPmFinancialBranchs().isEmpty()){
				getManagedBean().setFactoringBranchList(financialOrgResDTO.getPmFinancialOrg().getPmFinancialBranchs());
			}
		}
	}
		
	public void getSelectedFactoringBranchDtl(AjaxBehaviorEvent e){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		Long financialBranchId = (Long) e.getComponent().getAttributes().get("value");
		
		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);
		
		if(financialBranchId!=null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialBranchId(financialBranchId);
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.toString());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(resDTO.getAddressTypeList().get(0).getPmContacts() != null ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
		} else { //reset back to HQ info
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(getManagedBean().getFulfilmentOrder().getPmFinancialOrg().getFinancialOrgId());
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(!resDTO.getAddressTypeList().isEmpty()){
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}				
		}
	}
	
	 public void getRfcAddress(Long financialOrgId, Long financialBranchId){
		 IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		
		 getManagedBean().setRfcAddress(null);
		 getManagedBean().setRfcPhoneNo(null);
		 getManagedBean().setRfcFaxNo(null);
		 getManagedBean().setRfcContactPerson(null);
		 getManagedBean().setRfcContactEmail(null);
		 getManagedBean().setRfcContactPhoneNo(null);
		 if(financialOrgId!=null) {
				AddressReqDTO reqDTO = new AddressReqDTO();
				reqDTO.setFinancialOrgId(financialOrgId);
				reqDTO.setFinancialBranchId(financialBranchId);
				reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
				if(!resDTO.getAddressTypeList().isEmpty()){
					getManagedBean().setRfcAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
					getManagedBean().setRfcPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
					getManagedBean().setRfcFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
					if(!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty() ){
						PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
						getManagedBean().setRfcContactPerson(pmContact.getContactName());
						getManagedBean().setRfcContactEmail(pmContact.getEmail());
						getManagedBean().setRfcContactPhoneNo(pmContact.getPhoneNo());
					}
					
				}
			}
	}	
	 
	public void getFactoringAddress(Long financialOrgId, Long financialBranchId) {
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);

		getManagedBean().setSupplierAddress(null);
		getManagedBean().setPhoneNo(null);
		getManagedBean().setFaxNo(null);
		getManagedBean().setContactPerson(null);
		getManagedBean().setContactEmail(null);
		getManagedBean().setContactPhoneNo(null);

		if (financialOrgId != null) {
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setFinancialOrgId(financialOrgId);
			reqDTO.setFinancialBranchId(financialBranchId);
			reqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());

			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if (!resDTO.getAddressTypeList().isEmpty()) {
				getManagedBean().setSupplierAddress(getFullAddress(resDTO.getAddressTypeList().get(0).getPmAddress()));
				getManagedBean().setPhoneNo(resDTO.getAddressTypeList().get(0).getPmAddress().getPhoneNo());
				getManagedBean().setFaxNo(resDTO.getAddressTypeList().get(0).getPmAddress().getFaxNo());
				if (!resDTO.getAddressTypeList().get(0).getPmContacts().isEmpty()) {
					PmContact pmContact = resDTO.getAddressTypeList().get(0).getPmContacts().get(0);
					getManagedBean().setContactPerson(pmContact.getContactName());
					getManagedBean().setContactEmail(pmContact.getEmail());
					getManagedBean().setContactPhoneNo(pmContact.getPhoneNo());
				}
			}
		}
	}
		
	public String getPortletTitle() {
		String portletTitle = " Fulfilment Document Maintenance";
		if(getManagedBean().isRemoveFactoring()) {
			portletTitle = "Factoring Company Removal";
		}
		return portletTitle;
	}
		
	public List<SelectItem> getPopulateReasonList() {
		IReasonSO reasonSO = (IReasonSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		
		List<SelectItem> populateReasonList = new ArrayList<SelectItem>();
		
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setLanguage(CommonConstants.DEFAULT_LANGUAGE_CODE);//temp for testing purposes will set back after gustia got the proper data
		reqDTO.setReasonCategory(ParameterConst.FULFILMENT_REASON_CODE);
		ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		getManagedBean().setReasonDescs(resDTO.getReasonDescList());
		List<PmReasonDesc> reasonDescs = resDTO.getReasonDescList();
		for (PmReasonDesc obj : reasonDescs) {
			populateReasonList.add(new SelectItem(obj.getPmReason().getReasonCode(), obj.getReasonDesc()));
		}
		
		if(getManagedBean().getStopIns() == null){
			getManagedBean().setStopIns(new FlStopInstr());
			getManagedBean().getStopIns().setPmReason(new PmReason());
		}
		
		return populateReasonList;
	}
		
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==1){
			if(getManagedBean().getFlRequest().getFlDeliveryAddresses()!=null) {
				sortAddress(getManagedBean().getFlRequest().getFlDeliveryAddresses());
				sortOrderSummaryList(getManagedBean().getFlRequest().getFlDeliveryAddresses());
			}
		}
	}

	private class PendingDoPagingDataModel extends PagingDataModel<FlFulfilmentOrder> {
		private static final long serialVersionUID = 1L;

		public PendingDoPagingDataModel() {
			super("flPendingDoListDt");
		}
		
		@Override
		public ISearchResultBean<FlFulfilmentOrder> pageChangeListener(ISearchBean<FlFulfilmentOrder> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
				reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				reqDTO.setPendingDoSearchBean(searchBean);
				IFulfilmentOrderSO orderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				FulfilmentResDTO resDTO = orderSO.searchPendingDoList(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getPendingDoResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public IPagingDataModel<FlFulfilmentOrder> getPendingDoDataModel() {
		return pendingDoDataModel;
	}

	public void setPendingDoDataModel(
			IPagingDataModel<FlFulfilmentOrder> pendingDoDataModel) {
		this.pendingDoDataModel = pendingDoDataModel;
	}

	// Please add role if there is new role for this backingbean
	private String getUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();	
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return RoleCode.MOF_SUPPLIER_ADMIN.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();			
		else
			return null;
	}
	
	public void sortAddress(List<FlDeliveryAddress> flDelAddressList) {
		//Creating Comparator
		if(null==flDelAddressList || flDelAddressList.isEmpty()){
			return;
		}
		  Comparator<FlDeliveryAddress> comparator = new Comparator<FlDeliveryAddress>() {
	            @Override
	            public int compare(FlDeliveryAddress o1, FlDeliveryAddress o2) {
	                return new CompareToBuilder()
		                .append(o1.getPmAddress().getAddress1(), o2.getPmAddress().getAddress1())
		                .append(o1.getPmAddress().getAddress2(), o2.getPmAddress().getAddress2())
		                .append(o1.getPmAddress().getAddress3(), o2.getPmAddress().getAddress3())
		                .toComparison();
	            }
	        };
			Collections.sort(flDelAddressList, comparator);
    }
	
	// sorting Item for OrderSummary Tab
		public void sortOrderSummaryList(List<FlDeliveryAddress> deliveryAddressList) {
			//Creating Comparator
			if(null==deliveryAddressList || deliveryAddressList.isEmpty()){
				return;
			}
			for(FlDeliveryAddress flDeliveryAddress : deliveryAddressList) {
				sortItems(flDeliveryAddress.getFlFulfilmentItemAddr());
			}
	    }
		public void sortItems(List<FlFulfilmentItemAddr> flItemAddressList) {
			//Creating Comparator
			if(null==flItemAddressList || flItemAddressList.isEmpty()){
				return;
			}
			  Comparator<FlFulfilmentItemAddr> comparator = new Comparator<FlFulfilmentItemAddr>() {
		            @Override
		            public int compare(FlFulfilmentItemAddr o1, FlFulfilmentItemAddr o2) {
		                return new CompareToBuilder()
			                .append(o1.getScRequestItem().getCmItem().getExtensionCode(), o2.getScRequestItem().getCmItem().getExtensionCode())
			                .toComparison();
		            }
		        };
				Collections.sort(flItemAddressList, comparator);
	    }
		
		public String retrieveMofNo(SmSupplier smSupplier) {
			IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setSupplierId(smSupplier.getSupplierId());
			MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
			if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
				return resDTO.getMofAccount().getMofNo();
			}
			return null;
		}
}
