/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.paymentmatch.PaymentMatchBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.fl.stopinstr.StopInstrBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
//import my.ep.constant.BpmOutcomeConst;

/**
 * Backingbean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flDocumentBackingBean")
@RequestScoped
public class FlDocumentBackingBean extends BaseBackingBean<FlDocumentManagedBean>{

	private static Logger log = Logger.getLogger(FlDocumentBackingBean.class);
	
	public static final String NAV_TO_MAINTAIN_PR = "MaintainPR";
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";
	public static final String NAV_TO_MAINTAIN_DO = "MaintainDO";
	public static final String NAV_TO_MAINTAIN_CR = "MaintainCR";
	public static final String NAV_TO_MAINTAIN_INV = "MaintainINV";
	public static final String NAV_TO_MAINTAIN_FRN = "MaintainFRN";
	public static final String NAV_TO_COMPLAIN_CC_ITEM = "/xhtml/fl/complaincontractitem/complainCentralContractItem.xhtml";
	public static final String NAV_TO_MAINTAIN_PA = "/xhtml/fl/paymentmatch/pmBasePage.xhtml";

	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}

	public void searchDocument(){
		flDocumentDataModel.reloadData();
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentSupplier()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlSupplier(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentRecevingOfficer()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRecevingOfficer(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlApprover(facesScopes.getUserSessionBean().getLoginId());
		}
	}
	
	public SelectItem[] getDocumentTypeDropDown() {
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentSupplier()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlSupplier(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentRecevingOfficer()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRecevingOfficer(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlApprover(facesScopes.getUserSessionBean().getLoginId());
		}
		
		List<SelectItem> selectedItemList = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FULFILMENT_DOCUMENT_TYPE);
			selectedItemList.add(items[0]);
			List<SelectItem> itemList = new ArrayList<SelectItem>();
		    for(SelectItem selectItem : items) {
		    	itemList.add(selectItem);
		    }
			List<String> flDocTypes = new ArrayList<String>();
			if(getManagedBean().getSearchOrderType()!=null) {
				// TODO Should filter by user role
				getManagedBean().getFlRequisitioner();
				isFlFulfilmentApprover();
				isFlFulfilmentRecevingOfficer();
				isFlFulfilmentApprover();
				isFlFulfilmentSupplier();
				if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentRequisitioner()) {
					if(isFlFulfilmentRecevingOfficer()){
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
						flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
					}else{
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
					}	
				} else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentRequisitioner()) {
					
					if(isFlFulfilmentRecevingOfficer()){
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
					}else{
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
					}	
				} else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentApprover()) {
					if(isFlFulfilmentPaymentOfficer()){
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
						flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
						flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
					}else{
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
						flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
					}
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentApprover()) {
					if(isFlFulfilmentPaymentOfficer()){
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
						flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
					}else{
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
						flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					}
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentSupplier()) {
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentSupplier()) {
					flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentRecevingOfficer()) {
					flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
					
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentRecevingOfficer()) {
					flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentApprover() && isFlFulfilmentRecevingOfficer()) {
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				}
			}
		    for(SelectItem selectItem : itemList) {
		    	if(flDocTypes.contains(selectItem.getValue())) {
		    		selectedItemList.add(selectItem);
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(),e.getMessage()));
		}
	    return selectedItemList.toArray(new SelectItem[selectedItemList.size()]);
	}
	
	public String navigateToPage(){
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IDeliveryOrderSO deliveryOrderSO = null;
		IInvoiceSO invoiceSO = null;
		IReceivedNoteSO receivedNoteSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IPaymentMatchSO paymentSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.getManagedBean().setDocMaintainRemarks(false);
		String navgPage="/xhtml/fl/docsearch/flDocSearch.xhtml";
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		String flDocNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docNo");
		String flDocId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		String statusId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusId");
		String buttonVal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("buttonVal");
		
		System.out.println("docType::: "+docType);
		System.out.println("flDocNo::: "+flDocNo);
		System.out.println("flDocId::: "+flDocId);
		System.out.println("buttonVal::: "+buttonVal);
		System.out.println("statusId>>::: "+statusId);
		
		if(docType.equals(FulfilmentConst.DOC_TYPE_PR)){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			PurchaseRequestBackingBean PrBB=new PurchaseRequestBackingBean();
			PrBB.getManagedBean().setDocProcess(docType);
			PrBB.getManagedBean().setButtonVal(buttonVal);
			PrBB.getManagedBean().setFlDocId(flDocId);
			if(null!=buttonVal && !"".equals(buttonVal)){
				PrBB.getManagedBean().setSubmitButtonStatus(false);
			}
			// for selected Approver List
			if (PrBB.getManagedBean().getApproverModel() == null) {
				//System.out.println("--renew--");
				//List<String> source = getAvailableApproverList();
				List<String> source = new ArrayList<String>();
				List<String> target = new ArrayList<String>();
				PrBB.getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}
			PrBB.getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
			PrBB.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			PrBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			PrBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			navgPage=NAV_TO_MAINTAIN_PR;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_CR)){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			crBB.renewSesionObject();
			crBB = new ContractRequestBackingBean();
			crBB.getManagedBean().setDocProcess(docType);
			crBB.getManagedBean().setFlDocId(flDocId);
			crBB.getManagedBean().setButtonVal(buttonVal);
			crBB.getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
			crBB.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
				String[] outcome = new String[2];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
				crBB.getManagedBean().setOutComes(outcome);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase("complain")){
				return NAV_TO_COMPLAIN_CC_ITEM;	
			}else{
				return NAV_TO_MAINTAIN_CR;	
			}
		} else if(buttonVal.equalsIgnoreCase("Stop Fulfilment")) { 
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrder(reqDTO);			
			if(buttonVal.equalsIgnoreCase("Stop Fulfilment")) {
				FlStopInstr flStopInstr = new FlStopInstr();
				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(FulfilmentStatusConst.SD_PENDING_SUBMISSION);

				flStopInstr.setPmStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				flStopInstr.setPmReason(new PmReason());
				flStopInstr.setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				flStopInstr.setFlFulfilmentOrder(resDTO.getFlOrder());
				
				StopInstrBackingBean stopInstrBB = new StopInstrBackingBean();
				stopInstrBB.renewSesionObject();
				stopInstrBB.getManagedBean().setStopInstr(flStopInstr);
				stopInstrBB.populateStopInstrItem();
				
				if(resDTO.getFlOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractNo());
				}
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				navgPage = "stopInstr";
			}
		} else if(docType.equals(FulfilmentConst.DOC_TYPE_PO)){
			
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrder(reqDTO);			
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);

			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			flOrderBB.getManagedBean().setRemoveFactoring(false);
			flOrderBB.getManagedBean().setDocMaintainRemarks(true);
			if(resDTO.getFlOrder().isFactored()){
				flOrderBB.getManagedBean().setRemoveFactoring(true);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
				getSumOrderedQty(flOrderBB.getManagedBean().getFlRequest());
			}
			flOrderBB.getManagedBean().getFulfilmentOrder();
			navgPage=NAV_TO_MAINTAIN_PO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_CO)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO=fulfilmentOrderSO.retrieveFulfilmentOrder(reqDTO);
			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			Long flReqId=resDTO.getFlOrder().getFlFulfilmentRequest().getFulfilmentReqId();
			reqDTO.setFlRequestId(flReqId);
			FulfilmentResDTO resFlDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setFlRequest(resFlDTO.getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			navgPage=NAV_TO_MAINTAIN_CO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_DO)){
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
			reqDTO.setDeliveryOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
			DeliveryOrderBackingBean doBB=new DeliveryOrderBackingBean();
			doBB.getManagedBean().setButtonVal(buttonVal);
			doBB.getManagedBean().setDocProcess(docType);
			doBB.getManagedBean().setDocType(docType);
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			resDTO.getFlDeliveryOrder().getFlDeliveryAddress().getFlFulfilmentItemAddr().get(0).getScRequestItem().getItemName();
			doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			doBB.getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
			doBB.getManagedBean().getFulfilmentOrder().setStatus(deliveryOrderSO.retrieveDelOrderStatus(reqDTO).getPmStatus());
			doBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			doBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				//flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
				flBB.getManagedBean().setDocMaintainRemarks(true);
				
			}	
			navgPage=NAV_TO_MAINTAIN_DO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_IN)){
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			reqDTO.setInvoiceId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setButtonVal(buttonVal);
			invBB.getManagedBean().setDocProcess(docType);
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			//invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().setFlFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			invBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.getManagedBean().setDocMaintainRemarks(false);
				//flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
				flBB.getManagedBean().setDocMaintainRemarks(true);
				
			}	
			navgPage=NAV_TO_MAINTAIN_INV;
		}
		else if(docType.equals(FulfilmentConst.DOC_TYPE_FN)){
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			reqDTO.setFulfilmentNoteId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
			ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
			recNoteBB.getManagedBean().setButtonVal(buttonVal);
			recNoteBB.getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
			recNoteBB.getManagedBean().setFulfilmentRequest(resDTO.getFulfilmentNote().getFlRequest());
			recNoteBB.getManagedBean().getFulfilmentNote().setFlRequest(resDTO.getFulfilmentNote().getFlRequest());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				recNoteBB.getManagedBean().setFrnCancellationProcess(true);
			}
			
			reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			recNoteBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			for(FlFulfilmentNoteItem fulfilmentNoteItem: resDTO.getFulfilmentNote().getFlFulfilmentNoteItem()){
				recNoteBB.getManagedBean().setSelectedItem(fulfilmentNoteItem);
			}
			recNoteBB.getManagedBean().getFulfilmentNote().setStatus(receivedNoteSO.retrieveReceivedNoteStatus(reqDTO).getPmStatus());
			recNoteBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			}	
			navgPage=NAV_TO_MAINTAIN_FRN;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_PA)){
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IPaymentMatchSO.class);
			reqDTO.setPaymentAdviseId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = paymentSO.retrievePaymentAdviseByPaymentId(reqDTO);
			PaymentMatchBackingBean pmBB = new PaymentMatchBackingBean();
			pmBB.getManagedBean().setButtonVal(buttonVal);
			pmBB.getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
			pmBB.getManagedBean().setFulfilmentRequest(resDTO.getPaymentAdvice().getFlRequest());
			pmBB.getManagedBean().getPaymentAdvice().setFlPaymentDeduction(resDTO.getPaymentAdvice().getFlPaymentDeduction());
			pmBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			}	
			navgPage=NAV_TO_MAINTAIN_PA;
		}
		return navgPage;
	}
	
	public void selectedUser(AjaxBehaviorEvent e) {
		// doing some stuff here
		//System.out.println("====enter process checked===="+ getManagedBean().getTempLoginId());
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setTempLoginId(id);
	}
	
	public void selectedDoctype(AjaxBehaviorEvent e) {
		// doing some stuff here
		//System.out.println("====enter process checked===="+ getManagedBean().getSearchDocType());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get Doc Type value=====" + id);
		if(id!=null) {
			if(id.equals(FulfilmentConst.DOC_TYPE_CO)){
				this.getManagedBean().setCheckDocType(true);
				this.getManagedBean().setCheckPoDocType(false);
				this.getManagedBean().setCheckTitle(true);
			}else if(id.equals(FulfilmentConst.DOC_TYPE_PO)){
				this.getManagedBean().setCheckPoDocType(true);
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckTitle(true);
				getProcurementTypeList();
			}else if(id.equals(FulfilmentConst.DOC_TYPE_PA) || id.equals(FulfilmentConst.DOC_TYPE_CR) || id.equals(FulfilmentConst.DOC_TYPE_PR)){
				this.getManagedBean().setCheckTitle(true);
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckPoDocType(false);
			}else if(id.equals(FulfilmentConst.DOC_TYPE_DO)){
				this.getManagedBean().setCheckDoDocType(true);
				this.getManagedBean().setCheckInDocType(false);
				this.getManagedBean().setCheckDocType(false);
			}else if(id.equals(FulfilmentConst.DOC_TYPE_IN)){
				this.getManagedBean().setCheckInDocType(true);
				this.getManagedBean().setCheckDoDocType(false);
				this.getManagedBean().setCheckDocType(false);
			}else{
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckPoDocType(false);
				this.getManagedBean().setCheckDoDocType(false);
				this.getManagedBean().setCheckInDocType(false);
				this.getManagedBean().setCheckDoInDocType(false);
			}
		}
		getManagedBean().setSearchDocType(id);
		getStatusDescList(id, "FL", "en");
	}
	
	public void selectedOrderType(AjaxBehaviorEvent e) {
		getManagedBean().setSearchDocType(null);
		getManagedBean().setCheckDocType(false);
		getManagedBean().setCheckPoDocType(false);
		this.getManagedBean().setCheckTitle(false);
	}
	public void searchReset(){
		this.getManagedBean().setSearchTitle(null);
		this.getManagedBean().setSearchSuppName(null);
		this.getManagedBean().setSearchSuppDoRef(null);
		this.getManagedBean().setSearchSuppInvRef(null);
		this.getManagedBean().setSearchContractNo(null);
		this.getManagedBean().setSearchContractToDuration(null);
		this.getManagedBean().setSearchContractFromDuration(null);
		this.getManagedBean().setSearchLoaNo(null);
		this.getManagedBean().setSearchCircularNo(null);
		this.getManagedBean().setSearchOrderType(null);
	}

	public IPagingDataModel<VFlDocument> getFlDocumentDataModel() {
		return flDocumentDataModel;
	}

	public void setFlDocumentDataModel(IPagingDataModel<VFlDocument> flDocumentDataModel) {
		this.flDocumentDataModel = flDocumentDataModel;
	}

	private class FlDocumentPagingDataModel extends PagingDataModel<VFlDocument> {
		private static final long serialVersionUID = 1L;

		public FlDocumentPagingDataModel() {
			super("flDocListDt");
		}
		
		@Override
		public ISearchResultBean<VFlDocument> pageChangeListener(ISearchBean<VFlDocument> searchBean) {
			SmSupplier smSupp = new SmSupplier();
			PmOrgProfile defaultOrgProfile = new PmOrgProfile();
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orderType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchOrderType()));
				if(getManagedBean().getSearchDocType()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchDocType()));
				}
				if(getManagedBean().getSearchDocNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchDocNo()));
				}
				if(getManagedBean().getSearchContractNo()!=null && getManagedBean().getSearchContractNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractNo()));
				}
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo()!="")  {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularNo()));
				}
				if(getManagedBean().getSearchLoaNo()!=null && getManagedBean().getSearchLoaNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchLoaNo()));
				}		
				if(getManagedBean().getSearchProcurementTypeId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchProcurementTypeId()));
				}			
				if(getManagedBean().getStatusDescId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getStatusDescId()));
				}
				if(getManagedBean().getSearchTitle()!=null && getManagedBean().getSearchTitle()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("title",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchTitle()));
				}
				if(getManagedBean().getSearchSuppName()!=null && getManagedBean().getSearchSuppName()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.companyName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSuppName()));
				}
				if(getManagedBean().getSearchSuppDoRef()!=null && getManagedBean().getSearchSuppDoRef()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("supplierDoRef",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSuppDoRef()));
				}
				if(getManagedBean().getSearchSuppInvRef()!=null && getManagedBean().getSearchSuppInvRef()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("supplierInvRef",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSuppInvRef()));
				}
				IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				if(isFlFulfilmentRequisitioner()){
					FacesScopes facesScopes = new FacesScopes();
					getManagedBean().setPmUser(facesScopes.getUserSessionBean());
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setUserId(getManagedBean().getPmUser().getUserId());
					reqDTO.setLanguageCode("en");
					UserResDTO resDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
					Date currentDate = new Timestamp(System.currentTimeMillis());
					for(PmRoleDesc roleDesc : resDTO.getRoleDescList()){
						roleDesc.getPmRole().getRoleCode();
					}
					for(PmUserOrg pmUserOrg : getManagedBean().getPmUser().getPmUserOrgs()){
						if(pmUserOrg.getEffDate().before(currentDate)){
							if(pmUserOrg.getExpDate().after(currentDate)){
								defaultOrgProfile = pmUserOrg.getPmOrgProfile();
								
								break;
							}
						}
					}
				}
			
				if(isFlFulfilmentSupplier()){
					FacesScopes facesScopes = new FacesScopes();
					getManagedBean().setPmUser(facesScopes.getUserSessionBean());
					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(getManagedBean().getPmUser().getUserId());
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
					SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
					smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0);
				}
				FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
				reqDTO.setSearchBean(searchBean);
				reqDTO.setOrgProfileId(defaultOrgProfile.getOrgProfileId());
				reqDTO.setSupplierId(smSupp.getSupplierId());
				
				IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
				FlDocumentResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void getStatusDescList(String docType, String moduleCode, String languageCode){
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode("en");//language code is "en"
		reqDTO.setDocType(docType);
		reqDTO.setModuleCode(moduleCode);
		StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
		resDTO.getStatusDescList();
		getManagedBean().setDocStatusList(resDTO.getStatusDescList());
	}
	public void getProcurementTypeList(){
		IParameterSO parameterSO = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setLanguageCode("en");
		reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
		ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
		resDTO.getParameterDescList();
		getManagedBean().setProcurementTypeList(resDTO.getParameterDescList());
	}
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	public boolean isFlFulfilmentSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
	}
	
	private void getSumOrderedQty(FlFulfilmentRequest flRequest) {
		if(flRequest.getFlFulfilmentItems() != null && !flRequest.getFlFulfilmentItems().isEmpty()){
			BigDecimal sumOrderedQty = new BigDecimal(0);
			for(FlFulfilmentItem fulfilmentItem : flRequest.getFlFulfilmentItems()){
				for(FlFulfilmentItemAddr itemAddress : fulfilmentItem.getFlFulfilmentItemAddr()){
					sumOrderedQty = sumOrderedQty.add(itemAddress.getOrderedQty());
				}
				fulfilmentItem.setSumOrderedQty(sumOrderedQty);
			}
		}
	}
}
