/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.fl.doc.FlDocumentManagedBean;
import my.ep.web.managedbean.trackingdiary.FlTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backingbean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name="flTrackingDiaryBackingBean")
@RequestScoped
public class FlTrackingDiaryBackingBean extends BaseBackingBean<FlTrackingDiaryManagedBean>{

	private static Logger log = Logger.getLogger(FlTrackingDiaryBackingBean.class);
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}
//	public void preRenderDocSearch() {
//		renewSesionObject();
//		resetSearchCriteria();
//		FacesContext fc=FacesContext.getCurrentInstance();
//		fc.getApplication().getNavigationHandler()
//		.handleNavigation(fc, null, "flDocSearch?faces-redirect=true");
//		
//	}

	public void search(){
		if(null == getManagedBean().getSearchOrderType()){ 
			throw new EpException(MessageConst.ERROR_740023);
		}else if((null ==getManagedBean().getSearchDocType() &&  "".equalsIgnoreCase(getManagedBean().getSearchOrderNo()) && "".equalsIgnoreCase(getManagedBean().getSearchDocNo()))){
			throw new EpException(MessageConst.ERROR_740023);
		}
		getManagedBean().getFlDocumentDataModel().reloadData();
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
		getManagedBean().setSearchPhysContractNo(null);
		getManagedBean().setSearchPhysLoaNo(null);
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
	
	public void reset(){
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
		this.getManagedBean().setSearchPhysContractNo(null);
		this.getManagedBean().setSearchPhysLoaNo(null);
		
		// reset everything to false
		//this.getManagedBean().setCheckDocNo(false);
		this.getManagedBean().setCheckDocType(false);
		this.getManagedBean().setCheckPoDocType(false);
		this.getManagedBean().setCheckDoDocType(false);
		this.getManagedBean().setCheckInDocType(false);
		this.getManagedBean().setCheckDoInDocType(false);
		renewSesionObject();
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
		return facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value());
	}
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
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
