/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.fl.frn.FlFulfilmentNoteItem;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.common.PmReason;
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
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
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
	public static final String NAV_TO_REMOVE_FACTORING_COMPANY = "/xhtml/fl/removefactoringcomp/rfcBasePage.xhtml";
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}
	public void preRenderDocSearch() {
		renewSesionObject();
		resetSearchCriteria();
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "flDocSearch?faces-redirect=true");
		
	}

	public void searchDocument(){
		if(null == getManagedBean().getSearchOrderType()){ 
			throw new EpException(MessageConst.ERROR_740023);
		}else if((null ==getManagedBean().getSearchDocType() &&  "".equalsIgnoreCase(getManagedBean().getSearchOrderNo()) && "".equalsIgnoreCase(getManagedBean().getSearchDocNo()))){
			throw new EpException(MessageConst.ERROR_740023);
		}
		getManagedBean().getFlDocumentDataModel().reloadData();
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
			List<String> flDocTypes = getDocTypeListByRole();
			List<String> selectedItemCode = new ArrayList<String>();
		    for(SelectItem selectItem : itemList) {
		    	if(flDocTypes.contains(selectItem.getValue()) && !selectedItemCode.contains(selectItem.getValue().toString())) {
		    		selectedItemCode.add(selectItem.getValue().toString());
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
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		
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
			PrBB.setFulfilmentRequestField(resDTO);
			PrBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			navgPage=NAV_TO_MAINTAIN_PR;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_CR)){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			crBB.renewSesionObject();
			crBB = new ContractRequestBackingBean();
			crBB.getManagedBean().setSelectedTabIndex(0);
			crBB.getManagedBean().setDocProcess(docType);
			crBB.getManagedBean().setFlDocId(flDocId);
			crBB.getManagedBean().setButtonVal(buttonVal);
			crBB.setFulfilmentRequestField(resDTO);
			crBB.initializeApproverGroup();
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
				String[] outcome = new String[2];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
				crBB.getManagedBean().setOutComes(outcome);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
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
			reqDTO.setDocType(ParameterConst.DOCTYPE_STOP_DELIVERY);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);			
			if(buttonVal.equalsIgnoreCase("Stop Fulfilment")) {
				FlStopInstr flStopInstr = new FlStopInstr();

				statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(FulfilmentStatusConst.SD_PENDING_SUBMISSION);
				flStopInstr.setPmStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				flStopInstr.setPmReason(new PmReason());
				flStopInstr.setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				flStopInstr.setFlFulfilmentOrder(resDTO.getFlOrder());
				
				StopInstrBackingBean stopInstrBB = new StopInstrBackingBean();
				stopInstrBB.renewSesionObject();
				stopInstrBB.getManagedBean().setStopInstr(flStopInstr);
				stopInstrBB.getManagedBean().setSelectedTabIndex(0);
				stopInstrBB.populateStopInstrItem();
				stopInstrBB.populateChargeLineData();
				
				if(resDTO.getFlOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractNo());
				}
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				navgPage = "stopInstr";
			}
		} else if(docType.equals(FulfilmentConst.DOC_TYPE_PO) && buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_REMOVE_FACTORING)) {
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_FACTORING_COMPANY);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);			
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);

			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setButtonVal(null);
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			flOrderBB.getManagedBean().setRemoveFactoring(false);
			flOrderBB.getManagedBean().setFactoringCheck("true");
			flOrderBB.getManagedBean().setDocMaintainRemarks(true);
			if(resDTO.getFlOrder().isFactored()){
				flOrderBB.getManagedBean().setRemoveFactoring(true);
			}
			if(null != resDTO.getFlOrder().getPmFinancialBranch()){
				flOrderBB.getRfcAddress(null, resDTO.getFlOrder().getPmFinancialBranch().getFinancialBranchId());
			} else if(null != resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId()){
				flOrderBB.getRfcAddress(resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId(), null);
			}
			String[] outcome = new String[1];
			outcome[0] = BpmOutcomeConst.BPM_REMOVE_FACTORING;
			flOrderBB.getManagedBean().setOutComes(outcome);
			flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			navgPage=NAV_TO_REMOVE_FACTORING_COMPANY;
		} else if(docType.equals(FulfilmentConst.DOC_TYPE_PO)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);			
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);

			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			flOrderBB.renewSesionObject();
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			String[] outcome = new String[1];
			if(BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(buttonVal)){
				outcome[0] = BpmOutcomeConst.BPM_CANCEL;
				flOrderBB.getManagedBean().setOutComes(outcome);
			}else if(BpmOutcomeConst.BPM_STOP_FULFILMENT.equalsIgnoreCase(buttonVal)){
				outcome[0] = BpmOutcomeConst.BPM_STOP_FULFILMENT;
				flOrderBB.getManagedBean().setOutComes(outcome);
			}
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			flOrderBB.getManagedBean().setRemoveFactoring(false);
			flOrderBB.getManagedBean().setDocMaintainRemarks(true);
			if(resDTO.getFlOrder().isFactored()) {
				flOrderBB.getManagedBean().setFactoringCheck("true");
				flOrderBB.getFactoringCompanyDtl();
				if(null != resDTO.getFlOrder().getPmFinancialBranch()){
					IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class); 
					FinancialOrgReqDTO financialOrgReqDTO = new FinancialOrgReqDTO();
					financialOrgReqDTO.setFinancialOrgId(resDTO.getFlOrder().getPmFinancialBranch().getFinancialBranchId());
					FinancialOrgResDTO financialOrgResDTO = financialOrgSO.retriveFinancialBranchByFinancialOrgId(financialOrgReqDTO);
					if(financialOrgResDTO.getFinancialBranchList() != null && !financialOrgResDTO.getFinancialBranchList().isEmpty()){
						flOrderBB.getManagedBean().setFactoringBranchList(financialOrgResDTO.getFinancialBranchList());
					}
					flOrderBB.getFactoringAddress(null, resDTO.getFlOrder().getPmFinancialBranch().getFinancialBranchId());
				} else if(null != resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId()){
					flOrderBB.getFactoringAddress(resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId(), null);
				}				
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
				getSumOrderedQty(flOrderBB.getManagedBean().getFlRequest());
			} else {
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}
			navgPage=NAV_TO_MAINTAIN_PO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_CO)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			navgPage=NAV_TO_MAINTAIN_CO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_DO)){
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
			reqDTO.setDeliveryOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = deliveryOrderSO.retrieveDeliveryOrderById(reqDTO);
			DeliveryOrderBackingBean doBB=new DeliveryOrderBackingBean();
			doBB.getManagedBean().setSelectedTabIndex(0);
			doBB.getManagedBean().setButtonVal(buttonVal);
			doBB.getManagedBean().setDocProcess(docType);
			doBB.getManagedBean().setDocType(docType);
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			doBB.getManagedBean().setFulfilmentRequest(resDTO.getFlDeliveryOrder().getFlRequest());
			doBB.getManagedBean().setFulfilmentOrder(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder());
			doBB.getManagedBean().setDocMaintainRemarks(true);
			doBB.getManagedBean().setFromPageUrl(navgPage);
			doBB.getManagedBean().getFulfilmentOrder().setStatus(deliveryOrderSO.retrieveDelOrderStatus(reqDTO).getPmStatus());
			
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else { // view
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			navgPage=NAV_TO_MAINTAIN_DO;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_IN)){
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			reqDTO.setInvoiceId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setSelectedTabIndex(0);
			invBB.getManagedBean().setButtonVal(buttonVal);
			invBB.getManagedBean().setDocProcess(docType);
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			//invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().setFlFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			invBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			navgPage=NAV_TO_MAINTAIN_INV;
		}
		else if(docType.equals(FulfilmentConst.DOC_TYPE_FN)){
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			reqDTO.setFulfilmentNoteId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNoteByReceivedNoteId(reqDTO);
			ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
			recNoteBB.getManagedBean().setButtonVal(buttonVal);
			recNoteBB.getManagedBean().setSelectedTabIndex(0);
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
			recNoteBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			}	 else {
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			navgPage=NAV_TO_MAINTAIN_FRN;
		}else if(docType.equals(FulfilmentConst.DOC_TYPE_PA)){
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IPaymentMatchSO.class);
			reqDTO.setPaymentAdviseId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = paymentSO.retrievePaymentAdviseByPaymentId(reqDTO);
			PaymentMatchBackingBean pmBB = new PaymentMatchBackingBean();
			pmBB.getManagedBean().setSelectedTabIndex(0);
			pmBB.getManagedBean().setButtonVal(buttonVal);
			pmBB.getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
			pmBB.populateChargeLineData();
			pmBB.getManagedBean().setFulfilmentRequest(resDTO.getPaymentAdvice().getFlRequest());
			pmBB.getManagedBean().getPaymentAdvice().setFlPaymentDeduction(resDTO.getPaymentAdvice().getFlPaymentDeduction());
			pmBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getRemarkList(),resDTO.getAttachmentList());
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
	
	private void resetSearchCriteria() {
		getManagedBean().setSearchDocNo(null);
		getManagedBean().setStatusDescId(null);
		getManagedBean().setSearchSuppName(null);
		getManagedBean().setSearchSuppDoRef(null);
		getManagedBean().setSearchSuppInvRef(null);
		getManagedBean().setSearchLoaNo(null);
		getManagedBean().setSearchContractNo(null);
		getManagedBean().setSearchContractFromDuration(null);
		getManagedBean().setSearchContractToDuration(null);
		getManagedBean().setSearchOrderNo(null);
	}
	
	public void selectedDoctype(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		
		// reset everything to false
		//this.getManagedBean().setCheckDocNo(true);
		if(id!=null && id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			this.getManagedBean().setCheckDocType(true);
		}else{
			this.getManagedBean().setCheckDocType(false);
		}
		
		this.getManagedBean().setCheckPoDocType(false);
		this.getManagedBean().setCheckDoDocType(false);
		this.getManagedBean().setCheckInDocType(false);
		this.getManagedBean().setCheckDoInDocType(false);
		resetSearchCriteria();
		
		if(id!=null) {
			/*if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				this.getManagedBean().setCheckDocType(true);
			}else */
			if(getManagedBean().getSearchOrderType().equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				this.getManagedBean().setCheckDocType(true);
			}
			if(id.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
				this.getManagedBean().setCheckPoDocType(true);
				getProcurementTypeList();
			}else if(id.equals(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
				this.getManagedBean().setCheckDoDocType(true);
			}else if(id.equals(ParameterConst.DOCTYPE_INVOICE)){
				this.getManagedBean().setCheckInDocType(true);
			}
		}
		getManagedBean().setSearchDocType(id);
	}
	
	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchDocType(null);

		// reset everything to false
		//this.getManagedBean().setCheckDocNo(false);
		this.getManagedBean().setCheckDocType(false);
		this.getManagedBean().setCheckPoDocType(false);
		this.getManagedBean().setCheckDoDocType(false);
		this.getManagedBean().setCheckInDocType(false);
		this.getManagedBean().setCheckDoInDocType(false);
		resetSearchCriteria();
		if(id!=null) {
			if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				this.getManagedBean().setCheckDocType(true);
			}	
		}
		
		
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
		this.getManagedBean().setSearchDocType(null);
		
		// reset everything to false
		//this.getManagedBean().setCheckDocNo(false);
		this.getManagedBean().setCheckDocType(false);
		this.getManagedBean().setCheckPoDocType(false);
		this.getManagedBean().setCheckDoDocType(false);
		this.getManagedBean().setCheckInDocType(false);
		this.getManagedBean().setCheckDoInDocType(false);
	}
	
	public void filterSearchResults(List<VFlDocument> searchResultList){
		IFulfilmentDocumentSO flDocumentSO  = null;
			for(VFlDocument docsearchResult:searchResultList){
				docsearchResult.setUpdateAction(false);
				docsearchResult.setViewAction(false);
				docsearchResult.setCancelAction(false);
				if(null == docsearchResult.getStatus() || ParameterConst.DOCTYPE_PURCHASE_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) ){
					docsearchResult.setViewAction(true);
				}else if(ParameterConst.DOCTYPE_CONTRACT_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) && FulfilmentStatusConst.CR_PENDING_SUBMISSION.equals(docsearchResult.getStatus().getStatusId())){
					docsearchResult.setUpdateAction(true);
					docsearchResult.setViewAction(true);
				}else if(ParameterConst.DOCTYPE_CONTRACT_REQUEST.equalsIgnoreCase(docsearchResult.getDocType())){
					docsearchResult.setViewAction(true);
				}else{
					docsearchResult.setViewAction(true);
					boolean isUpdate=false;
					boolean isCancel=false;
					boolean isStopFulfilment=false;
					boolean isRemoveFactoring=false;
					FLOrderInfo orderInfo=null;
					orderInfo=getStatusInfo(docsearchResult.getDocId(),docsearchResult.getDocType());
						if(null != orderInfo){
							flDocumentSO  = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
							isUpdate = flDocumentSO.canDocumentUpdate(docsearchResult.getStatus().getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole());
							isCancel = flDocumentSO.canDocumentCancel(docsearchResult.getStatus().getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole());
							isStopFulfilment = flDocumentSO.canDocumentStopFulfilment(docsearchResult.getStatus().getStatusId(),docsearchResult.getDocId(),orderInfo.getRole());
							if(docsearchResult.getDocType().equals(FulfilmentConst.DOC_TYPE_PO)){
								isRemoveFactoring = flDocumentSO.canDocumentRemoveFactoring(docsearchResult.getStatus().getStatusId(),docsearchResult.getDocId(), orderInfo.getRole(),orderInfo.isFactored());
							}	
							if(isUpdate){
								docsearchResult.setUpdateAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action update ### :: "+docsearchResult.isUpdateAction());
							}
							if(isCancel){
								docsearchResult.setCancelAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  cancel @@@@@@@:: "+docsearchResult.isCancelAction());
							}
							if(isStopFulfilment){
								docsearchResult.setStopFulfilmentAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  StopFulfilmen %%%%%%:: "+docsearchResult.isStopFulfilmentAction());
							}
							if(isRemoveFactoring){
								docsearchResult.setRemoveFactoringAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  RemoveFactoring @@@@@@@:: "+docsearchResult.isRemoveFactoringAction());
							}
					  }
			     }
		 }		
	}
	
	private FLOrderInfo getStatusInfo(Long docId,String docType){
		IReceivedNoteSO receivedNoteSO  = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IDeliveryOrderSO deliveryOrderSO = null;
		IInvoiceSO invoiceSO = null;
		FLOrderInfo orderInfo = new FLOrderInfo();
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		if(null != docId && null != docType){
			if(ParameterConst.DOCTYPE_FULFILMENT_NOTE.equalsIgnoreCase(docType)){
				reqDTO.setFulfilmentNoteId(Long.valueOf(docId));
				receivedNoteSO  = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
				resDTO = receivedNoteSO.getReceivedNoteById(reqDTO);
				if(null != resDTO.getFulfilmentNote()){
					if(null != resDTO.getFulfilmentNote().getFlRequest().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getFulfilmentNote().getFlRequest().getFlFulfilmentOrder().getFulfilmentOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentRecevingOfficer()) {
											orderInfo.setRole( RoleCode.RECEIVING_OFFICER.value());
										}
									}
								}
							}
					}
				}
			}else if(ParameterConst.DOCTYPE_DELIVERY_ORDER.equalsIgnoreCase(docType)){
				deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
				reqDTO.setDeliveryOrderId(Long.valueOf(docId));
				resDTO = deliveryOrderSO.getDeliveryOrderById(reqDTO);
				if(null != resDTO.getFlDeliveryOrder()){
					if(null != resDTO.getFlDeliveryOrder().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentSupplier()) {
											orderInfo.setRole( RoleCode.BASIC_SUPPLIER_USER.value());
										}
									}
								}
							}
					}
				}
				
			}else if(ParameterConst.DOCTYPE_INVOICE.equalsIgnoreCase(docType)){
				invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
				reqDTO.setInvoiceId(Long.valueOf(docId));
				resDTO = invoiceSO.getInvoiceById(reqDTO);
				if(null != resDTO.getInvoice()){
					if(null != resDTO.getInvoice().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getInvoice().getFlFulfilmentOrder().getFulfilmentOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentSupplier()) {
											orderInfo.setRole( RoleCode.BASIC_SUPPLIER_USER.value());
										}
									}
								}
							}
					}
				}
			}else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(docType) || ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(docType)){
				fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				reqDTO.setFlOrderId(Long.valueOf(docId));
		        resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);			
				if(null != resDTO.getFlOrder()){
					if(null != resDTO.getFlOrder()){
						if(null != resDTO.getFlOrder().getStatus()){
							if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
								//System.out.println("2222222222222222   po/co staus id is :::: "+resDTO.getFlOrder().getStatus().getStatusId());
								orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
								if(isFlFulfilmentApprover()){
									orderInfo.setRole(RoleCode.FL_APPROVER.value());
								}else if(isFlFulfilmentSupplier()){
									orderInfo.setRole(RoleCode.BASIC_SUPPLIER_USER.value());
								}
								if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(docType)){
									orderInfo.setFactored(resDTO.getFlOrder().isFactored());
								}
							}
						}
					}
				}
			}
		}
		return orderInfo;
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
	
	private class FLOrderInfo {
		private Long orderStatusId;
		private String role;
		private boolean factored = false;

		/**
		 * @return the role
		 */
		public String getRole() {
			return role;
		}

		/**
		 * @return the factored
		 */
		public boolean isFactored() {
			return factored;
		}

		/**
		 * @param role the role to set
		 */
		public void setRole(String role) {
			this.role = role;
		}

		/**
		 * @param factored the factored to set
		 */
		public void setFactored(boolean factored) {
			this.factored = factored;
		}

		/**
		 * @return the orderStatusId
		 */
		public Long getOrderStatusId() {
			return orderStatusId;
		}

		/**
		 * @param orderStatusId the orderStatusId to set
		 */
		public void setOrderStatusId(Long orderStatusId) {
			this.orderStatusId = orderStatusId;
		}
		
	}
	
	private List<String> getDocTypeListByRole() {
		List<String> flDocTypes = new ArrayList<String>();
		if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getManagedBean().getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
			}
			if(isFlFulfilmentApprover()){
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			}
			if(isFlFulfilmentPaymentOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);	
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentSupplier()) {
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
			}
		} else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(getManagedBean().getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			}
			if(isFlFulfilmentApprover()){
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			}
			if(isFlFulfilmentPaymentOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);				
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
		}
		if(isFlFulfilmentSupplier()) {
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		}
		return flDocTypes;
	}
}
