/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.doc;

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
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.cr.ContractRequestBackingBean;
import my.ep.web.backingbean.fl.deliveryorder.DeliveryOrderBackingBean;
import my.ep.web.backingbean.fl.frn.ReceivedNoteBackingBean;
import my.ep.web.backingbean.fl.invoice.InvoiceBackingBean;
import my.ep.web.backingbean.fl.po.FulfilmentOrderBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.util.FacesScopes;
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
	public static final String NAV_TO_COMPLAIN_CC_ITEM = "/xhtml/fl/complaincontractitem/complainCentralContractItem.xhtml";

	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}

	public void searchDocument(){
		flDocumentDataModel.reloadData();
		if(isFlFulfilmentRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setFulfilmentRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		if(isFlFulfilmentSupplier()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setPmUser(facesScopes.getUserSessionBean());
			 
		}
	}
	
	public SelectItem[] getDocumentTypeDropDown() {
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
				getManagedBean().getFulfilmentRequisitioner();
				if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentRequisitioner()) {
					flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
					flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				} else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentRequisitioner()) {
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				} else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentApprover()) {
					flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
					flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentApprover()) {
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
					flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentSupplier()) {
					flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentSupplier()) {
					flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentSupplier()) {
					flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
					flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) && isFlFulfilmentRecevingOfficer()) {
					flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				}else if(getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER) && isFlFulfilmentRecevingOfficer()) {
					flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				}
				//flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				//flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				//flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				//flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				//flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				//flDocTypes.add(ParameterConst.DOCTYPE_ADJUSTMENT_NOTE);
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
		String NavgPage="/xhtml/fl/docsearch/flDocSearch.xhtml";
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
			PrBB.getManagedBean().setDocProcess(docType);
			PrBB.getManagedBean().setButtonVal(buttonVal);
			PrBB.getManagedBean().setFlDocId(flDocId);
			if(null!=buttonVal && !"".equals(buttonVal)){
				PrBB.getManagedBean().setSubmitButtonStatus(false);
			}
			PrBB.getManagedBean().setFulfilmentRequest(resDTO.getFlFulfilmentRequest());
			NavgPage=NAV_TO_MAINTAIN_PR;
		}else if(docType.equals("CR")){
			fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			reqDTO.setFlRequestId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = fulfilmentRequestSO.retrieveFulfilmentRequest(reqDTO);
			ContractRequestBackingBean crBB = new ContractRequestBackingBean();
			crBB.getManagedBean().setDocProcess(docType);
			crBB.getManagedBean().setFlDocId(flDocId);
			crBB.getManagedBean().setButtonVal(buttonVal);
			crBB.getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			if(buttonVal.equalsIgnoreCase("complain")){
				return NAV_TO_COMPLAIN_CC_ITEM;	
			}else{
				return NAV_TO_MAINTAIN_CR;	
			}
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
			flOrderBB.getManagedBean().setFlRequest(resFlDTO.getFlFulfilmentRequest());
			NavgPage=NAV_TO_MAINTAIN_PO;
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
			flOrderBB.getManagedBean().setFlRequest(resFlDTO.getFlFulfilmentRequest());
			NavgPage=NAV_TO_MAINTAIN_CO;
		}else if(docType.equals("DO")){
			deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject("fl",IDeliveryOrderSO.class);
			reqDTO.setDeliveryOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = deliveryOrderSO.retrieveDeliveryOrder(reqDTO);
			DeliveryOrderBackingBean doBB=new DeliveryOrderBackingBean();
			doBB.getManagedBean().setButtonVal(buttonVal);
			doBB.getManagedBean().setDocProcess(docType);
			doBB.getManagedBean().setFlDeliveryOrder(resDTO.getFlDeliveryOrder());
			NavgPage=NAV_TO_MAINTAIN_DO;
		}else if(docType.equals("IN")){
			invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject("fl",IInvoiceSO.class);
			reqDTO.setFlOrderId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = invoiceSO.retrieveInvoice(reqDTO);
			InvoiceBackingBean invBB = new InvoiceBackingBean();
			invBB.getManagedBean().setButtonVal(buttonVal);
			invBB.getManagedBean().setDocProcess(docType);
			invBB.getManagedBean().setInvoice(resDTO.getInvoice());
			NavgPage=NAV_TO_MAINTAIN_INV;
		}
		else if(docType.equals("FN")){
			receivedNoteSO = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject("fl",IReceivedNoteSO.class);
			reqDTO.setFulfilmentNoteId(Long.valueOf(flDocId));
			FulfilmentResDTO resDTO = receivedNoteSO.retrieveReceivedNote(reqDTO);
			ReceivedNoteBackingBean recNoteBB = new ReceivedNoteBackingBean();
			recNoteBB.getManagedBean().setButtonVal(buttonVal);
			recNoteBB.getManagedBean().setFulfilmentNote(resDTO.getFulfilmentNote());
			NavgPage=NAV_TO_MAINTAIN_FRN;
		}
		return NavgPage;
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
			if(id.equals("CO")){
				this.getManagedBean().setCheckDocType(true);
				this.getManagedBean().setCheckPoDocType(false);
				this.getManagedBean().setCheckTitle(true);
				
			}else if(id.equals("PO")){
				this.getManagedBean().setCheckPoDocType(true);
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckTitle(true);
				getProcurementTypeList();
				
			}else if(id.equals("PA") || id.equals("CR") || id.equals("PR")){
				this.getManagedBean().setCheckTitle(true);
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckPoDocType(false);
			}else if(id.equals("DO") || id.equals("IN")){
				this.getManagedBean().setCheckDoInDocType(true);
				this.getManagedBean().setCheckDocType(false);
			}else{
				this.getManagedBean().setCheckDocType(false);
				this.getManagedBean().setCheckPoDocType(false);
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
		this.getManagedBean().setSearchSuppRef(null);
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
				if(getManagedBean().getSearchSuppRef()!=null && getManagedBean().getSearchSuppRef()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.companyName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSuppRef()));
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
		return facesScopes.hasRoleUser(RoleCode.APPROVER.value());
	}
	public boolean isFlFulfilmentSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.SUPPLIER_USER.value());
	}
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RN_APPROVER.value());
	}
	
	
	
}
