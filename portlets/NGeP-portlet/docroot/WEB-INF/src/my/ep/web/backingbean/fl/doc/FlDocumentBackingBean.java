/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.createcontract.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.fl.creatdo.DeliveryOrderManagedBean;
import my.ep.web.managedbean.fl.createcontract.ContractRequestManagedBean;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.managedbean.fl.frn.ReceivedNoteManagedBean;
import my.ep.web.managedbean.fl.invoice.InvoiceManagedBean;
import my.ep.web.managedbean.fl.pr.PurchaseRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
	
	
	

	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}

	public void searchDocument(){
		flDocumentDataModel.reloadData();
	}
	
	//public S
	
	public String navigateToPage(){
		IFulfilmentRequestSO fulfilmentRequestSO = null;
		IDeliveryOrderSO deliveryOrderSO = null;
		IInvoiceSO invoiceSO = null;
		IReceivedNoteSO receivedNoteSO = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		String docType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docType");
		String flDocNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docNo");
		String flDocId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		String buttonVal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("buttonVal");
		System.out.println("docType::: "+docType);
		System.out.println("flDocNo::: "+flDocNo);
		System.out.println("flDocId::: "+flDocId);
		System.out.println("buttonVal::: "+buttonVal);
		
		if(docType.equals("PR")){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			PurchaseRequestBackingBean PrBB=new PurchaseRequestBackingBean();
			//PurchaseRequestManagedBean prMB= new PurchaseRequestManagedBean();
			PrBB.setManagedBeanClazz(PurchaseRequestManagedBean.class);
			PrBB.getManagedBean().setDocProcess(docType);
			PrBB.getManagedBean().setButtonVal(buttonVal);
			PrBB.getManagedBean().setFlDocId(flDocId);
			PrBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			return NAV_TO_MAINTAIN_PR;
		}else if(docType.equals("CR")){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			//ContractRequestManagedBean crMB=new ContractRequestManagedBean();
			crBB.setManagedBeanClazz(ContractRequestManagedBean.class);
			crBB.getManagedBean().setDocProcess(docType);
			crBB.getManagedBean().setFlDocId(flDocId);
			crBB.getManagedBean().setButtonVal(buttonVal);
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			
			return NAV_TO_MAINTAIN_CR;	
		}else if(docType.equals("PO")){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO=fulfilmentOrderSO.retrieveFulfilmentOrder(reqDTO);
			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			Long flReqId=resDTO.getFlOrder().getFlFulfilmentRequest().getFulfilmentReqId();
			reqDTO.setFlRequestId(flReqId);
			FulfilmentResDTO resFlDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setFulfilmentRequest(resFlDTO.getFlFulfilmentRequest());
			return NAV_TO_MAINTAIN_PO;
		}else if(docType.equals("CO")){
			fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentOrderSO.class);
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO=fulfilmentOrderSO.retrieveFulfilmentOrder(reqDTO);
			FulfilmentOrderBackingBean flOrderBB=new FulfilmentOrderBackingBean();
			Long flReqId=resDTO.getFlOrder().getFlFulfilmentRequest().getFulfilmentReqId();
			reqDTO.setFlRequestId(flReqId);
			FulfilmentResDTO resFlDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			flOrderBB.getManagedBean().setButtonVal(buttonVal);
			flOrderBB.getManagedBean().setFulfilmentRequest(resFlDTO.getFlFulfilmentRequest());
			return NAV_TO_MAINTAIN_CO;			
		}else if(docType.equals("DO")){
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			reqDTO.setDeliveryOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = deliveryOrderSO.retrieveDeliveryOrder(reqDTO);
			DeliveryOrderBackingBean doBB=new DeliveryOrderBackingBean();
			//DeliveryOrderManagedBean dpMB=new DeliveryOrderManagedBean();
			doBB.setManagedBeanClazz(DeliveryOrderManagedBean.class);
			doBB.getManagedBean().setButtonVal(buttonVal);
			doBB.getManagedBean().setDocProcess(docType);
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			return NAV_TO_MAINTAIN_DO;			
		}else if(docType.equals("IN")){
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoice(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			//InvoiceManagedBean invMB=new InvoiceManagedBean();
			invBB.setManagedBeanClazz(InvoiceManagedBean.class);
			invBB.getManagedBean().setButtonVal(buttonVal);
			invBB.getManagedBean().setDocProcess(docType);
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			return NAV_TO_MAINTAIN_INV;			
		}
		else if(docType.equals("FN")){
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject("fl",IReceivedNoteSO.class);
			reqDTO.setFulfilmentNoteId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNote(reqDTO);
			ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
			//ReceivedNoteManagedBean RecNoteMB=new ReceivedNoteManagedBean();
			recNoteBB.setManagedBeanClazz(ReceivedNoteManagedBean.class);
			recNoteBB.getManagedBean().setButtonVal(buttonVal);
			recNoteBB.getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
			return NAV_TO_MAINTAIN_FRN;			
		}
		return NAV_TO_MAINTAIN_PR;
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
		System.out.println("=======get id value=====" + id);
		getManagedBean().setSearchDocType(id);
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
				if(getManagedBean().getSearchContractNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractNo()));
				}
				if(getManagedBean().getSearchCircularNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularNo()));
				}
				if(getManagedBean().getSearchLoaNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchLoaNo()));
				}		
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
					SearchBetweenValue<Date> betweenValue = new SearchBetweenValue<Date>(getManagedBean().getSearchStartDate(), getManagedBean().getSearchEndDate());
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
				}		
				if(getManagedBean().getSearchProcurementType()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchProcurementType()));
				}			
				if(getManagedBean().getSearchStatusId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));
				}
				
				FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
				reqDTO.setSearchBean(searchBean);
				
				IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
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
}
