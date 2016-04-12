/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
// TODO: Auto-generated Javadoc

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.fl.adjustment.IAdjustmentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.order.IReliefOrderSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.orgprofile.IPtjReorgSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgReqDTO;
import my.ep.dto.pm.authorization.orgprofile.PtjReorgResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.fl.FlActor;
import my.ep.persistence.entity.fl.FlAttachment;
import my.ep.persistence.entity.fl.FlRemark;
import my.ep.persistence.entity.fl.order.FlDeliveryOrder;
import my.ep.persistence.entity.fl.order.FlReliefOrderItem;
import my.ep.persistence.entity.fl.order.FlStopInstr;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmPtjReorg;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.dan.DebitAdjustmentNoteBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.paymentmatch.PaymentMatchBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.backingbean.fl.relieforder.ReliefOrderBackingBean;
import my.ep.web.backingbean.fl.stopinstr.StopInstrBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backingbean for Fulfillment document.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flDocumentBackingBean")
@RequestScoped
public class FlDocumentBackingBean extends FulfilmentBaseBackingBean<FlDocumentManagedBean>{

	/** The log. */
	private static Logger log = Logger.getLogger(FlDocumentBackingBean.class);
	
	/** The Constant NAV_TO_MAINTAIN_PR. */
	public static final String NAV_TO_MAINTAIN_PR = "MaintainPR";
	
	/** The Constant NAV_TO_MAINTAIN_PO. */
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	
	/** The Constant NAV_TO_MAINTAIN_CO. */
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";
	
	/** The Constant NAV_TO_MAINTAIN_DO. */
	public static final String NAV_TO_MAINTAIN_DO = "MaintainDO";
	
	/** The Constant NAV_TO_MAINTAIN_CR. */
	public static final String NAV_TO_MAINTAIN_CR = "MaintainCR";
	
	/** The Constant NAV_TO_MAINTAIN_INV. */
	public static final String NAV_TO_MAINTAIN_INV = "MaintainINV";
	
	/** The Constant NAV_TO_MAINTAIN_FRN. */
	public static final String NAV_TO_MAINTAIN_FRN = "MaintainFRN";
	
	/** The Constant NAV_TO_COMPLAIN_CC_ITEM. */
	public static final String NAV_TO_COMPLAIN_CC_ITEM = "/xhtml/fl/complaincontractitem/complainCentralContractItem.xhtml";
	
	/** The Constant NAV_TO_MAINTAIN_PA. */
	public static final String NAV_TO_MAINTAIN_PA = "/xhtml/fl/paymentmatch/pmBasePage.xhtml";
	
	/** The Constant NAV_TO_REMOVE_FACTORING_COMPANY. */
	public static final String NAV_TO_REMOVE_FACTORING_COMPANY = "/xhtml/fl/removefactoringcomp/rfcBasePage.xhtml";
	
	/** The Constant NAV_TO_STOP_INSTRUCTION. */
	public static final String NAV_TO_STOP_INSTRUCTION = "/xhtml/fl/stopInstruction/siBasePage.xhtml";
	
	/** The Constant NAV_TO_ADJUSTMENT_NOTE. */
	public static final String NAV_TO_ADJUSTMENT_NOTE = "/xhtml/fl/invoice/adjustment/danBasePage.xhtml";
	
	/** The Constant NAV_TO_ADJUSTMENT_NOTE. */
	public static final String NAV_TO_GST_RELIEF = "/xhtml/fl/relieforder/reliefOrderBasePage.xhtml";
	
	
	/**
	 * Advance search.
	 */
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}
	
	/**
	 * Pre render doc search.
	 * <p>remove managedBean from session.
	 * <p>reset doc search criteria.
	 */
	public void preRenderDocSearch(boolean trackingDiarySearch) {
		renewSesionObject();
		defaultPtjAndMinistryValues();
		resetSearchCriteria();
		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
		FacesContext fc=FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "flDocSearch?faces-redirect=true");
	}
	
	/**
	 * Transaction Type is mandatory field.
	 * <p>Document Type is mandatory field.
	 * <p>if the user specify the specific document no, the document type is not required to be filled in.
	 * <p>get fl document Search result based on selected and filled in parameters.
	 */
	public void searchDocument(){
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
			if(null == getManagedBean().getMinistry()){ 
				throw new EpException(MessageConst.ERROR_740044);
			}
			
		}
		
		if(null == getManagedBean().getSearchOrderType()){ 
			throw new EpException(MessageConst.ERROR_740023);
		}else if((null ==getManagedBean().getSearchDocType() &&  "".equalsIgnoreCase(getManagedBean().getSearchOrderNo()) && "".equalsIgnoreCase(getManagedBean().getSearchDocNo()))){
			throw new EpException(MessageConst.ERROR_740039, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.doc.maintain.doc.type",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.fl.do.poco",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		getManagedBean().getFlDocumentDataModel().reloadData();
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(hasSupplierRole()) {
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
	
	/**
	 * Gets all document types for document type drop down.
	 *
	 * @return the document type drop down
	 */
	public SelectItem[] getDocumentTypeDropDown() {
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFlRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(hasSupplierRole()) {
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
			List<String> flDocTypes = getManagedBean().getDocTypeListByRole();
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
	
	/**
	 * Navigate to page based on doc type.
	 * <p> if user select cancel action,to allow user to do document cancellation.
	 * <p> if user select update action,to allow user to do document modification.
	 * <p> if user select view action,to allow user to view document .
	 * <p> if user select stop instruction action,to allow user to do document Stop Delivery Instruction.
	 *
	 * @return the string
	 */
	public String navigateToPage(){
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IDeliveryOrderSO deliveryOrderSO = null;
		IInvoiceSO invoiceSO = null;
		IReceivedNoteSO receivedNoteSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IPaymentMatchSO paymentSO = null;
		IReliefOrderSO reliwfOrderSo=null;
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		StatusReqDTO statusReqDTO = null;
		IFulfilmentSO iFulfilmentSO = null;
		
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		flBB.getManagedBean().setDocMaintainRemarks(false);
		flBB.getManagedBean().setHideRemarks(false);
		String navgPage="/xhtml/fl/docsearch/flDocSearch.xhtml";
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		String flDocId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		String buttonVal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("buttonVal");
		String statusDesc = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusDesc");
		if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_REQUEST)){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			PurchaseRequestBackingBean PrBB=new PurchaseRequestBackingBean();
			PrBB.getManagedBean().setSubmitButtonStatus(false);
			PrBB.getManagedBean().setDocProcess(docType);
			PrBB.getManagedBean().setButtonVal(buttonVal);
			PrBB.getManagedBean().setFlDocId(flDocId);
			if(null!=buttonVal && !"".equals(buttonVal)){
				PrBB.getManagedBean().setSubmitButtonStatus(false);
			}
			if(Long.parseLong(statusDesc)==FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION){
				PrBB.getManagedBean().setFlDocMaintain(false);
			}else{
				PrBB.getManagedBean().setFlDocMaintain(true);
			}
			
			PrBB.setFulfilmentRequestField(resDTO);
			PrBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			PrBB.getManagedBean().setBackToDocument(null);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
				PrBB.getManagedBean().setDeclarationCheck(true);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} 
			navgPage=NAV_TO_MAINTAIN_PR;
		}else if(docType.equals(ParameterConst.DOCTYPE_CONTRACT_REQUEST)){
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
			crBB.getManagedBean().setBackToDocument(null);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
				crBB.getManagedBean().setDeclarationCheck(true);
			}
			
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)){
				String[] outcome = new String[2];
				outcome[0] = BpmOutcomeConst.BPM_SAVE;
				outcome[1] = BpmOutcomeConst.BPM_SUBMIT;
				crBB.getManagedBean().setOutComes(outcome);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} 
			
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			crBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			crBB.getManagedBean().setPmPriceType(resDTO.getFlFulfilmentRequest().getCtContract().getLatestContractVer().getCtContractItems().get(0).getPriceType());
			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(null != resDTO.getFlFulfilmentRequest()){
				if(resDTO.getFlFulfilmentRequest().getCtContract().isFactored()){
					Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
					contractSupplierReqDTO.setContractId(supplieContractId);
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							crBB.getSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring());
						}
					}else{
						resDTO.getFlFulfilmentRequest().getCtContract().setFactored(false);
					}
				}
				
			}
			
			for(CtAgency agency : crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()){
				if(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE || 
						(agency.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_SUSPENDED && agency.getEffDate().after(new Date()))) {
					crBB.getManagedBean().getAgentOrgProfileList().add(agency.getOrgProfile().getOrgProfileId());
					if(agency == crBB.getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0)){
						crBB.getManagedBean().setOrgProfileIds(agency.getOrgProfile().getOrgProfileId().toString());
					}else{
						crBB.getManagedBean().setOrgProfileIds(crBB.getManagedBean().getOrgProfileIds() + "," +agency.getOrgProfile().getOrgProfileId().toString());
					}
				}
			}
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
				StopInstrBackingBean stopInstrBB = new StopInstrBackingBean();
				statusReqDTO = new StatusReqDTO();
				
				statusReqDTO.setStatusId(FulfilmentStatusConst.SD_PENDING_SUBMISSION);
				SupplierGstResDTO suppGstResDTO =getSupplierGst(resDTO.getFlOrder().getFlFulfilmentRequest());
				if(suppGstResDTO.getSmGstSupplier() != null){
					flStopInstr.setGstEffectiveDate(suppGstResDTO.getSmGstSupplier().getGstEffDate());
					flStopInstr.setGstDeclaredDate(suppGstResDTO.getSmGstSupplier().getGstDeclaredDate());
					flStopInstr.setGstRegNo(suppGstResDTO.getSmGstSupplier().getGstRegNo());
				}
				flStopInstr.setPmStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				flStopInstr.setPmReason(new PmReason());
				flStopInstr.setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
				flStopInstr.setFlFulfilmentOrder(resDTO.getFlOrder());
				
				
				stopInstrBB.renewSesionObject();
				stopInstrBB.getManagedBean().setStopInstr(flStopInstr);
				stopInstrBB.getManagedBean().setSelectedTabIndex(0);
				stopInstrBB.populateStopInstrItem();
				stopInstrBB.populateChargeLineData();
				if(isFlFulfilmentApprover()){
					stopInstrBB.checkCogstr();
				}
				
				stopInstrBB.getManagedBean().setBackToDocument(null);
				if(resDTO.getFlOrder().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo());
				} else {
					new FulfilmentBackingBean(ParameterConst.DOCTYPE_STOP_DELIVERY, resDTO.getFlOrder().getFlFulfilmentRequest().getCtContract().getContractNo());
				}
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				navgPage = "stopInstr";
			}
		} else if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER) && buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_REMOVE_FACTORING)) {
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
//			if(null != resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId()){//TODO remove if no error
//				flOrderBB.getRfcAddress(resDTO.getFlOrder().getPmFinancialOrg().getFinancialOrgId());
//			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
			}
			String[] outcome = new String[1];
			outcome[0] = BpmOutcomeConst.BPM_REMOVE_FACTORING;
			flOrderBB.getManagedBean().setOutComes(outcome);
			flOrderBB.getManagedBean().setBackToPrevious(null);
			flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			navgPage=NAV_TO_REMOVE_FACTORING_COMPANY;
		} else if(docType.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
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
			flOrderBB.getManagedBean().setBackToPrevious(null);
			if(resDTO.getFlOrder().isFactored()) {
				flOrderBB.getManagedBean().setFactoringCheck("true");
				flOrderBB.getFactoringCompanyDtl();
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.initializeRemarkAndAttachement(null, resDTO.getRemarkList(), resDTO.getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
			} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
				getSumOrderedQty(flOrderBB.getManagedBean().getFlRequest());
			} else {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}
			
			if(!isGovtSeller()){
				flOrderBB.getManagedBean().setSupplier(hasSupplierRole());
			}
			
			navgPage=NAV_TO_MAINTAIN_PO;
		}else if(docType.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO coResDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setSelectedTabIndex(0);
			flOrderBB.getManagedBean().setFlRequest(coResDTO.getFlOrder().getFlFulfilmentRequest());
			flOrderBB.getManagedBean().setFulfilmentOrder(coResDTO.getFlOrder());
			flOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			flOrderBB.getManagedBean().setBackToPrevious(null);
			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(flOrderBB.getManagedBean().getFlRequest().getCtContract().isFactored()){
					Long supplieContractId=coResDTO.getFlFulfilmentRequest().getCtContract().getContractId();
					contractSupplierReqDTO.setContractId(supplieContractId);
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						coResDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							contractSupplierResDTO.getFactoring().getFinancialOrg().getFinOrgName();
							flOrderBB.getCrSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
						}
					}else{
						flOrderBB.getManagedBean().getFlRequest().getCtContract().setFactored(false);
					}
			}	
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.initializeRemarkAndAttachement(null, coResDTO.getRemarkList(), coResDTO.getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
			} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			if(!isGovtSeller()){
				flOrderBB.getManagedBean().setSupplier(hasSupplierRole());
			}
			navgPage=NAV_TO_MAINTAIN_CO;
		}else if(docType.equals(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
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
			doBB.getManagedBean().setBackToPrevious(null);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE)) {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			} else { // view
				flBB.initializeRemarkAndAttachement(resDTO.getFlDeliveryOrder().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			navgPage=NAV_TO_MAINTAIN_DO;
		}else if(docType.equals(ParameterConst.DOCTYPE_INVOICE)){
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
			reqDTO.setInvoiceId(Long.valueOf(flDocId));
			reqDTO.setDocType(ParameterConst.DOCTYPE_INVOICE);
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoiceByInvoiceId(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setSelectedTabIndex(0);
			invBB.getManagedBean().setButtonVal(buttonVal);
			invBB.getManagedBean().setDocProcess(docType);
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			invBB.getManagedBean().getInvoice().setFlRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setFulfilmentRequest(resDTO.getInvoice().getFlRequest());
			invBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			invBB.getManagedBean().setDocMaintainRemarks(true);
			invBB.getManagedBean().setDocMaintain(true);
			invBB.getManagedBean().setChargeLineItemRoot(null);
			invBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			invBB.getManagedBean().setBackToPrevious(null);
			for(FlDeliveryOrder flDeliveryOrder : invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					invBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}			
			if(invBB.getManagedBean().getInvoice().getFlFulfilmentOrder().isFactored()){
				invBB.getFactoringDtl(); 
			}			
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
				invBB.getManagedBean().setReliefDeclarationCheck(true);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getInvoice().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
			if(resDTO.isTaxable()){
				invBB.getManagedBean().setTaxable(true);
			}
			navgPage=NAV_TO_MAINTAIN_INV;
		}
		else if(docType.equals(ParameterConst.DOCTYPE_FULFILMENT_NOTE)){
			iFulfilmentSO = (IFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentSO.class);
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
			reqDTO.setFulfilmentNoteId(Long.valueOf(flDocId));
			reqDTO.setStatusDesc(statusDesc);
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
			recNoteBB.getManagedBean().setAcknowledged(false);
			if(Long.parseLong(statusDesc)==FulfilmentStatusConst.FN_ACKNOWLEDGED || Long.parseLong(statusDesc)==FulfilmentStatusConst.FN_REJECTED || Long.parseLong(statusDesc)==FulfilmentStatusConst.FN_CANCELLED){
				recNoteBB.getManagedBean().setAcknowledged(true);
			}
			//recNoteBB.getStatusDescById(statusDesc)
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
			}
			reqDTO.setDeliveryOrderId(resDTO.getFulfilmentNote().getFlDeliveryOrder().getDeliveryOrderId());
			recNoteBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			recNoteBB.getManagedBean().getFulfilmentNote().setFlFulfilmentNoteItem(resDTO.getFulfilmentNote().getFlFulfilmentNoteItem());
			if(recNoteBB.getManagedBean().getFlApprover() == null){
				FlActor actor = iFulfilmentSO.retrieveFlActors(recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getDocType(), recNoteBB.getManagedBean().getFulfilmentNote().getFlRequest().getFulfilmentReqId(), RoleCode.FL_APPROVER.value());
				if(actor != null){
					recNoteBB.getManagedBean().setFlApprover(actor.getUser());
					recNoteBB.getManagedBean().setAcknowledgeDate(actor.getActionDate());
				}
			}
			recNoteBB.getManagedBean().setDocMaintainRemarks(true);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getFulfilmentNote().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			recNoteBB.getManagedBean().setBackToPrevious(null);
			navgPage=NAV_TO_MAINTAIN_FRN;
		}else if(docType.equals(ParameterConst.DOCTYPE_PAYMENT_ADVICE)){
			paymentSO = (IPaymentMatchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IPaymentMatchSO.class);
			reqDTO.setPaymentAdviseId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = paymentSO.retrievePaymentAdviseByPaymentId(reqDTO);
			PaymentMatchBackingBean pmBB = new PaymentMatchBackingBean();
			pmBB.renewSesionObject();
			pmBB.getManagedBean().setSelectedTabIndex(0);
			pmBB.getManagedBean().setButtonVal(buttonVal);
			pmBB.getManagedBean().setPaymentAdvice(resDTO.getPaymentAdvice());
			pmBB.getManagedBean().setDelOrderList(new ArrayList<FlDeliveryOrder>()); 
			for(FlDeliveryOrder flDeliveryOrder : resDTO.getPaymentAdvice().getFulfilmentOrder().getFlDeliveryOrders()) {
				if(flDeliveryOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
					pmBB.getManagedBean().getDelOrderList().add(flDeliveryOrder);
				}
			}			
			pmBB.setAdjustmentDocNo(resDTO.getPaymentAdvice().getFlInvoice());
			pmBB.setStopInstrNo(resDTO.getPaymentAdvice().getFulfilmentOrder());
			pmBB.populateChargeLineData();
			pmBB.getManagedBean().setFulfilmentRequest(resDTO.getPaymentAdvice().getFlRequest());
			pmBB.getManagedBean().getPaymentAdvice().setFlPaymentDeduction(resDTO.getPaymentAdvice().getFlPaymentDeduction());
			pmBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			pmBB.checkHaveAdvancedPayment(resDTO.getPaymentAdvice());
			SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
			String newDate = df.format(pmBB.getManagedBean().getPaymentAdvice().getFlInvoice().getSubmissionDate());
			pmBB.getManagedBean().setInvoiceDate(newDate);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
			}
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_UPDATE) || buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)){
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
				flBB.getManagedBean().setDocMaintainRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(resDTO.getPaymentAdvice().getFlWorkflowStatus(), resDTO.getRemarkList(),resDTO.getAttachmentList());
			}
			if(pmBB.getManagedBean().getPaymentAdvice().getFulfilmentOrder().isFactored()){
				pmBB.getFactoringDtl(); 
			}
			navgPage=NAV_TO_MAINTAIN_PA;
		} else if(docType.equals(ParameterConst.DOCTYPE_STOP_DELIVERY)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setStopRequestId(Long.valueOf(flDocId));
			flReqDTO.setStatusDesc(statusDesc);
			FulfilmentResDTO resDTO = fulfilmentOrderSO.retrieveStopInstruction(flReqDTO);
			StopInstrBackingBean siBB = new StopInstrBackingBean();
			siBB.getManagedBean().setSelectedTabIndex(0);
			siBB.getManagedBean().setStopInstr(resDTO.getFlStopInstr());
			siBB.getManagedBean().setButtonVal(buttonVal);
			siBB.getManagedBean().setBackToDocument(null);
			siBB.getPopulateReasonList();
			siBB.populateChargeLineData();
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
			}
			flBB.initializeRemarkAndAttachement(resDTO.getFlStopInstr().getFlWorkflowStatus(), resDTO.getRemarkList(), resDTO.getAttachmentList());
			navgPage=NAV_TO_STOP_INSTRUCTION;
		} else if(docType.equals(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE) || docType.equals(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE)){
			DebitAdjustmentNoteBackingBean danBB = new DebitAdjustmentNoteBackingBean();
			danBB.renewSesionObject();
			IAdjustmentSO adjustmentSO = (IAdjustmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IAdjustmentSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setDanId(Long.valueOf(flDocId));
			flReqDTO.setDocType(docType);
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.getManagedBean().setHideRemarks(true);
				danBB.getManagedBean().setButtonVal(buttonVal);
			}
			FulfilmentResDTO resDTO = adjustmentSO.retrieveAdjustmentById(flReqDTO);
			danBB.getManagedBean().setSelectedTabIndex(0);
			danBB.setAdjustmentNote(resDTO);
			danBB.getManagedBean().setBackToPrevious(null);
			navgPage=NAV_TO_ADJUSTMENT_NOTE;
		}else if(docType.equals(ParameterConst.DOCTYPE_RELIEF_ORDER)){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
			reliwfOrderSo = (IReliefOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReliefOrderSO.class);
			reqDTO.setReliefOrderId(Long.valueOf(flDocId));
			reqDTO.setDocType(ParameterConst.DOCTYPE_RELIEF_ORDER);
			FulfilmentResDTO rsResDto = reliwfOrderSo.retrieveReliefOrderByReliefOrderId(reqDTO);
			
			reqDTO.setFlOrderId(Long.valueOf(rsResDto.getReliefOrder().getFlFulfilmentOrder().getFulfilmentOrderId()));
			FulfilmentResDTO resDTO=fulfilmentOrderSO.retrieveFulfilmentOrderById(reqDTO);
			ReliefOrderBackingBean reliefOrderBB=new ReliefOrderBackingBean();
			
			reliefOrderBB.getManagedBean().setButtonVal(buttonVal);
			reliefOrderBB.getManagedBean().setSelectedTabIndex(0);
			reliefOrderBB.getManagedBean().setFlRequest(resDTO.getFlOrder().getFlFulfilmentRequest());
			reliefOrderBB.getManagedBean().setFulfilmentOrder(resDTO.getFlOrder());
			reliefOrderBB.getManagedBean().setFlReliefOrder(rsResDto.getReliefOrder());
			reliefOrderBB.getManagedBean().setNonReliefItems(new ArrayList<FlReliefOrderItem>());
			reliefOrderBB.getManagedBean().setReliefItems(new ArrayList<FlReliefOrderItem>());
			reliefOrderBB.getManagedBean().setNonReliefMenuDisplay(false);
			for(FlReliefOrderItem  flReliefOrderItem : reliefOrderBB.getManagedBean().getFlReliefOrder().getFlReliefOrderItems()){
				if(!flReliefOrderItem.getGstFlag().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)){
					reliefOrderBB.getManagedBean().setNonReliefMenuDisplay(true);
					reliefOrderBB.getManagedBean().getNonReliefItems().add(flReliefOrderItem);
				}else{
					reliefOrderBB.getManagedBean().getReliefItems().add(flReliefOrderItem);
				}
			}
			reliefOrderBB.sortReliefOrderItems(reliefOrderBB.getManagedBean().getNonReliefItems());
			reliefOrderBB.sortReliefOrderItems(reliefOrderBB.getManagedBean().getReliefItems());
			reliefOrderBB.getManagedBean().setPmUser(reqDTO.getPmUser());
			IContractSupplierSO contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			if(reliefOrderBB.getManagedBean().getFlRequest().getCtContract() != null){
				if(reliefOrderBB.getManagedBean().getFlRequest().getCtContract().isFactored()){
					Long supplieContractId=resDTO.getFlFulfilmentRequest().getCtContract().getContractId();
					contractSupplierReqDTO.setContractId(supplieContractId);
					ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveFactoringByContract(contractSupplierReqDTO);
					if(contractSupplierResDTO.getFactoring() != null){
						resDTO.getFlFulfilmentRequest().getCtContract().setCtFactoring(contractSupplierResDTO.getFactoring());
						if(contractSupplierResDTO.getFactoring().getFinancialOrg() != null){
							contractSupplierResDTO.getFactoring().getFinancialOrg().getFinOrgName();
							reliefOrderBB.getCrSelectedFactoringCompDtl(contractSupplierResDTO.getFactoring().getFinancialOrg().getFinancialOrgId());
						}
					}else{
						reliefOrderBB.getManagedBean().getFlRequest().getCtContract().setFactored(false);
					}
				}
			}
				
			if(buttonVal.equalsIgnoreCase(BpmOutcomeConst.BPM_VIEW)){
				flBB.initializeRemarkAndAttachement(null, rsResDto.getRemarkList(), rsResDto.getAttachmentList());
				flBB.getManagedBean().setHideRemarks(true);
			} else {
				flBB.initializeRemarkAndAttachement(null, new ArrayList<FlRemark>(), new ArrayList<FlAttachment>());
			}	
			if(!isGovtSeller()){
				reliefOrderBB.getManagedBean().setSupplier(hasSupplierRole());
			}
			navgPage=NAV_TO_GST_RELIEF;
		}	
		return navgPage;
	}
	
	/**
	 * Selected user.
	 *
	 * @param e the e
	 */
	public void selectedUser(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setTempLoginId(id);
	}
	
	/**
	 * Reset search criteria.
	 */
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
		getManagedBean().setSearchPhysContractNo(null);
		getManagedBean().setSearchCentralContractName(null);
		getManagedBean().setSearchPhysLoaNo(null);
		defaultPtjAndMinistryValues();
	}
	
	/**
	 * Selected doctype.
	 *
	 * @param e the e
	 */
	public void selectedDoctype(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		
		// reset everything to false
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
	
	/**
	 * Selected order type.
	 *
	 * @param e the e
	 */
	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchDocType(null);

		// reset everything to false
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
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		renewSesionObject();
		defaultPtjAndMinistryValues();
	}
	
	/**
	 * Gets the procurement type list based on procurement type and language code.
	 *
	 * @return the procurement type list
	 */
	public void getProcurementTypeList(){
		IParameterSO parameterSO = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setLanguageCode("en");
		reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
		ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
		resDTO.getParameterDescList();
		getManagedBean().setProcurementTypeList(resDTO.getParameterDescList());
	}
	
	/**
	 * Checks if is fl fulfilment requisitioner.
	 *
	 * @return true, if is fl fulfilment requisitioner
	 */
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * Checks if is fl fulfilment receving officer.
	 *
	 * @return true, if is fl fulfilment receving officer
	 */
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	
	/**
	 * Checks if is fl fulfilment payment officer.
	 *
	 * @return true, if is fl fulfilment payment officer
	 */
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
	}
	
	/**
	 * Checks if is Auditor General/Internal user.
	 *
	 * @return true, if is Auditor General/Internal user
	 */
	public boolean isAuditor() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
	}
	
	/**
	 * Checks if is fl fulfilment govt seller.
	 *
	 * @return true, if is fl fulfilment govt seller.
	 */
	public boolean isGovtSeller() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value());
	}
	
	/**
	 * calculate order quantity sum based on ordered quantity
	 *
	 * @param flRequest the fl request
	 * @return the sum ordered qty
	 */
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
	
	/**
	 * Generate report based on doc type.
	 *
	 * @param docType the doc type
	 * @param docNo the doc no
	 */
	public void generateReport(String docType, String docNo , boolean taxable) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_DOC_NO, docNo);
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		String reportName = null;
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) || docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER)) {
			reportName = ReportConst.FL_NGEP_PURCHASE_ORDER;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_DELIVERY_ORDER)) {
			reportName = ReportConst.FL_NGEP_DELIVERY;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_FULFILMENT_NOTE)) {
			reportName = ReportConst.FL_NGEP_FRN;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_INVOICE)) {
			if(taxable){
				reportName = ReportConst.FL_NGEP_TAX_INVOICE;
			}else{
				reportName = ReportConst.FL_NGEP_INVOICE;
			}
			
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE)) {
			reportName = ReportConst.FL_NGEP_CREDIT_NOTE;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE)) {
			reportName = ReportConst.FL_NGEP_DEBIT_NOTE;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PAYMENT_ADVICE)) {
			reportName = ReportConst.FL_NGEP_PAYMENT_MATCH;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_RELIEF_ORDER)) {
			paramMap.put(ReportConst.PARAM_RELIEF_ORDER_DOC_NO, docNo);
			reportName = ReportConst.FL_NGEP_COGSTR;
		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_STOP_DELIVERY)) {
			reportName = ReportConst.FL_NGEP_STOP_DELIVERY;
		}
		
		if(reportName!=null) {
			try {
				generateReport(reportName, paramMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new EpException("Report is not available for this document.");
		}
	}
	
	/**
	 * Generate coGSTR report.
	 *
	 * @param orderDocNo the order document number
	 * @param coGSTRNo system generated coGSTR number
	 */
	public void generateCoGSTRReport(String orderDocNo, String coGSTRNo) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_DOC_NO, orderDocNo);
		paramMap.put(ReportConst.PARAM_USER_ID, getFacesScopes().getUserSessionBean().getUserId()+"");
		paramMap.put(ReportConst.PARAM_RELIEF_ORDER_DOC_NO, coGSTRNo);
		String reportName = ReportConst.FL_NGEP_COGSTR;		
		try {
			generateReport(reportName, paramMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 /**
	 * retrieve financial year options for current year and past 7 years
	 * @return financial year options
	 */
	public static List<SelectItem> getFinancialYearOpt() {
		int years = 0;
		List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
		for(Integer i = 0;i < 7;i++){
			years = CommonMethod.getYear(-i);
			 financialYearOpt.add(new SelectItem(years, Integer.toString(years)));
		}
		return financialYearOpt;
	}

	/**
	 * Gets the ministry List based on org type.
	 *
	 * @return the ministry List
	 */
	public List<SelectItem> getMinistryList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		retrievePTJList.add(new SelectItem("", ResourceBundleUtils.getMessage("lbl.txt.select",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())));
		orgProfileReqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
		resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		
		return retrievePTJList;
	}
	
	/**
	 * Retrieve supplier gst information by supplier ud and branch code
	 *
	 * @return SupplierGstResDTO resDTO
	 */
	public SupplierGstResDTO getSupplierGst(FlFulfilmentRequest flRequest) {
		ISupplierGstSO supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
		
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setBranchCode(flRequest.getFlSupplierDtl().getBranchCode());
		reqDTO.setSupplierId(flRequest.getSmSupplier().getSupplierId());
		SupplierGstResDTO resDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(reqDTO);
		
		return resDTO;
		
	}
	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 *
	 */
	private void defaultPtjAndMinistryValues() {
		if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| hasSupplierRole()) {
			//this.getManagedBean().setMinistry(null);
			//this.getManagedBean().setPtj(null);
			//this.getManagedBean().setPtjGroup(null);

		} else {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value())) {
				this.getManagedBean().setPtj(orgProfile);
				this.getManagedBean().setMinistry(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setPtjGroup(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
			} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
				this.getManagedBean().setPtj(null);
				this.getManagedBean().setMinistry(orgProfile);
			}
		}
	}
	
	/**
	 * Get Ministry condition for construct the LOV listing
	 * @return String
	 */
	public String getMinistryCondition() {
		String condition = "";
		if(hasGovernmentUserRole()) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
			IPtjReorgSO ptjReorgSO = (IPtjReorgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IPtjReorgSO.class);
			
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			condition = "orgProfileId^"+retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId();
			
			PtjReorgReqDTO ptjReqDTO = new PtjReorgReqDTO();
			ptjReqDTO.setDeepLevel(true);
			ptjReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			PtjReorgResDTO ptjResDTO = ptjReorgSO.retrieveOldPtjByNewPtjId(ptjReqDTO);
			for(PmPtjReorg pmPtjReorg : ptjResDTO.getPtjReorgList()) {
				condition = condition + "*" + "orgProfileId^" + retrieveMinistryOrgProfile(pmPtjReorg.getOldPtjId()).getOrgProfileId();
			}
		} 
		return condition;
	}
	
	/**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		return OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value())+
					"^"+this.getManagedBean().getPtjGroup().getOrgProfileId();
	}
		
	/**
	 * The method used reset PTJ/PTJ Group profile value once select the Ministry
	 */
	public void resetPtjGroupProfile() {
		getManagedBean().setPtj(null);
		getManagedBean().setPtjGroup(null);
	}
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setPtj(null);
	}
		
	/**
	  * The method used to disable the ministry Lov
	  * if login user is FL User
	  * or internal auditor.
	  * 
	  * @return true, if login user is FL User/internal auditor
	  *         false, if login user is not User/internal auditor
	*/	
	public boolean disableMinistryLov() {
		return (hasGovernmentUserRole()
				|| getFacesScopes().hasRoleUser(
						RoleCode.AUDITOR_INTERNAL.value()) );
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjLov() {
		return (hasGovernmentUserRole() || getManagedBean().getPtjGroup() == null );
	}
	
	/**
	  * The method used to disable the ptj Lov
	  * if login user is FL User
	  * or ministry is null.
	  * 
	  * @return true, if login user is FL User/ministry is null
	  *         false, if login user is not User/ministry is null
	*/	
	
	public boolean disablePtjGroupLov() {
		return (hasGovernmentUserRole() || getManagedBean().getMinistry() == null );
	}
	
	public boolean disableAllLov() {
		return (hasSupplierRole());
	}
	

	/**
	 * Get PTJ group condition for construct the LOV listing
	 * @return String
	 */
	//TODO need to test for this method.
	public String constuctConditionFieldsForPTJGroup() {		
		return OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getMinistry().getOrgProfileId();
	}
	
}
