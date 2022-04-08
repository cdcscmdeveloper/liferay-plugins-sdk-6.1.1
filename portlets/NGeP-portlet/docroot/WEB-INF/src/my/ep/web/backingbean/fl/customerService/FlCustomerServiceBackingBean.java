/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.customerService;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.fl.FulfilmentBaseBackingBean;
import my.ep.web.bean.ct.CustomerServiceEntity;
import my.ep.web.managedbean.fl.customerService.FlCustomerServiceManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * Backingbean for Customer Service.
 *
 * @author Giribabu
 * @since 23/11/2016
 */
@ManagedBean(name="flCustomerServiceBackingBean")
@ViewScoped
public class FlCustomerServiceBackingBean extends FulfilmentBaseBackingBean<FlCustomerServiceManagedBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(FlCustomerServiceBackingBean.class);
	
	/**
	 * Advance search.
	 */
	public void advanceSearch(){
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
	}
	
	
	/**
	 * Transaction Type is mandatory field.
	 * <p>Document Type is mandatory field.
	 * <p>if the user specify the specific document no, the document type is not required to be filled in.
	 * <p>get fl document Search result based on selected and filled in parameters.
	 */
	public void searchDocument(){
		if(null == getManagedBean().getPtj()){ 
			throw new EpException(MessageConst.ERROR_100022, 
					new String[] {ResourceBundleUtils.getMessage("lbl.fl.ptj.code", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		
		//Document type become mandatory
		if (null == getManagedBean().getSearchOrderType()|| null == getManagedBean().getSearchDocType()) {
			throw new EpException(MessageConst.ERROR_740023);
		} else if ((null == getManagedBean().getSearchDocType()&& "".equalsIgnoreCase(getManagedBean().getSearchOrderNo()) && "".equalsIgnoreCase(getManagedBean().getSearchDocNo()))) {
			throw new EpException(MessageConst.ERROR_740039, new String[] {
					ResourceBundleUtils.getMessage("lbl.fl.doc.maintain.doc.type",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.fl.do.poco.no",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) });
		}
		getManagedBean().getFlDocumentDataModel().reloadData();
	}
	
	/**
	 * Gets all document types for document type drop down.
	 *
	 * @return the document type drop down
	 */
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
		    if (getManagedBean().getSearchOrderType()!= null) {
		    	flDocTypes = getManagedBean().getDocTypeListByRole();
		    }
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
	public String navigateToPage(String docType, String flDocId,Long statusId,String docNo,String ordrNo){
		
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		String navgPage="/xhtml/fl/customerservice/customerServiceViewResult.xhtml";
		getManagedBean().setDocType(docType);
		getManagedBean().setStatusId(statusId);
		getManagedBean().setViewDocNo(docNo);
		getManagedBean().setViewOrdrNo(ordrNo);
		getManagedBean().setViewKumpulanPTJ(retrieveJabatanByOrgProfileId(getManagedBean().getPtj().getOrgProfileId()));
		getManagedBean().setViewMinistry(retrieveMinistryByOrgProfileId(getManagedBean().getPtj().getOrgProfileId()));
		getManagedBean().setViewPtjName(getManagedBean().getPtj().getOrgName());
		getManagedBean().setViewPtjCode(getManagedBean().getPtj().getOrgCode());
		
		// need to use RN number if PR is pending creation
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_REQUEST) && statusId.longValue()==FulfilmentStatusConst.PR_PENDING_PURCHASE_REQUEST_CREATION.longValue()) {
			IFulfilmentRequestSO fulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			flReqDTO.setFlDocNo(docNo);
			FulfilmentResDTO flResDTO = fulfilmentRequestSO.retrieveFulfilmentRequestByDocNo(flReqDTO);
			docNo = flResDTO.getFlFulfilmentRequest().getScPurchaseRequest().getRequestNote().getRequestNoteNo();
		}
		
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		 Object ctxToken = bpmSO.authenticate(prop.getProperty(EpPropertiesConst.BPM_PRINCIPAL));
		 getManagedBean().getCustomerServiceEntity().clear();
		 BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		 bpmReqDTO.setDocNumber(docNo);
		 bpmReqDTO.setUserId(prop.getProperty(EpPropertiesConst.BPM_PRINCIPAL));
		 bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		 bpmReqDTO.setCtxToken(ctxToken);
		 BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		 if(bpmResDTO.getWorklistVO().getWorklistItem()!=null && bpmResDTO.getWorklistVO().getWorklistItem().size() > 0) {
			 IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			 UserReqDTO reqUserDTO = new UserReqDTO();
			 CustomerServiceEntity custServiceEntity = null;
			 if(FulfilmentStatusConst.PO_PENDING_SUPPLIER_ACKNOWLEDGEMENT.longValue() == statusId 
					 || FulfilmentStatusConst.CO_PENDING_SUPPLIER_ACKNOWLEDGEMENT.longValue() == statusId 
					 ||FulfilmentStatusConst.CO_PENDING_INVOICE.longValue() == statusId
					 ||FulfilmentStatusConst.PO_PENDING_INVOICE.longValue() == statusId){
				 ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
				 SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
				 SupplierSearchResDTO supplierSearchResDTO = null; 
				 for(WorklistItemVO worklistItemVO : bpmResDTO.getWorklistVO().getWorklistItem()){
					 custServiceEntity = new CustomerServiceEntity();
					 if(worklistItemVO.getAssignees() != null && !worklistItemVO.getAssignees().isEmpty()){
						 for(String loginId : worklistItemVO.getAssignees()){
							 reqUserDTO.setLoginId(loginId);
							 UserResDTO userResDTO=	userSO.retrieveUserByLoginId(reqUserDTO);
							 if(null!=userResDTO && null!=userResDTO.getPmUser()){
								 supplierSearchReqDTO.setUserId(userResDTO.getPmUser().getUserId());
								 supplierSearchResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
								 if(supplierSearchResDTO.getResultBean() != null){
									 custServiceEntity.setOfficerName(supplierSearchResDTO.getResultBean().getResultList().get(0).getCompanyName());
									 break;
								 }
							 }
						 }
						 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
					 }
				 }
			 }else{
				 for(WorklistItemVO worklistItemVO : bpmResDTO.getWorklistVO().getWorklistItem()){
					 custServiceEntity = new CustomerServiceEntity();
					 if(worklistItemVO.getAcquiredBy() != null){
						 custServiceEntity.setOfficerName(worklistItemVO.getAcquiredBy());
						 reqUserDTO.setLoginId(worklistItemVO.getAcquiredBy());
						 UserResDTO userResDTO=	userSO.retrieveUserByLoginId(reqUserDTO);
						 if(null!=userResDTO && null!=userResDTO.getPmUser()){
							 custServiceEntity.setOfficerName(userResDTO.getPmUser().getUserName());
							 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
						 }
					 }else if(worklistItemVO.getAssignees() != null && !worklistItemVO.getAssignees().isEmpty()){
						 for(String assignee : worklistItemVO.getAssignees()){
							 custServiceEntity = new CustomerServiceEntity();
							 reqUserDTO.setLoginId(assignee);
							 UserResDTO userResDTO=	userSO.retrieveUserByLoginId(reqUserDTO);
							 if(null!=userResDTO && null!=userResDTO.getPmUser()){
								 custServiceEntity.setOfficerName(userResDTO.getPmUser().getUserName());
								 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
							 }
						 }
					 }
				 }	
			 }
		 }
		 if(FulfilmentStatusConst.CO_AWAITING_1GFMAS.longValue() == statusId 
				 || FulfilmentStatusConst.CR_AWAITING_1GFMAS.longValue() == statusId 
				 ||FulfilmentStatusConst.DN_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.FN_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.IN_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.PA_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.PO_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.PR_AWAITING_1GFMAS.longValue() == statusId
				 ||FulfilmentStatusConst.SD_AWAITING_1GFMAS_RESPONSE.longValue() == statusId){
				 CustomerServiceEntity custServiceEntity = new CustomerServiceEntity();
				 custServiceEntity.setOfficerName("1GFMAS");
				 getManagedBean().getCustomerServiceEntity().add(custServiceEntity);
		 }
		
		return navgPage;
	}
	
	/**
	 * Search reset.
	 */
	public void searchReset(){
		renewSesionObject();
	}
	
	/**
	 * retrieve user name.
	 */
	public String getUsername(Long userId) {
		return PmCommonUtil.retriveUserName(userId);
	}
	
	/**
	 * to go back to previous screen.
	 * 
	 * @return customerService
	 */
	public String backToPervious() {
		
	return "customerService";
	}
	/**
	 * Selected order type.
	 *
	 * @param e the e
	 */
	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchDocType(null);

		resetSearchCriteria();
	}
	
	/**
	 * Selected doctype.
	 *
	 * @param e the e
	 */
	public void selectedDoctype(AjaxBehaviorEvent e) {
		String docType = (String) e.getComponent().getAttributes().get("value");
		
		resetSearchCriteria();
		
		getManagedBean().setSearchDocType(docType);
	}
	
	/**
	 * Reset search criteria.
	 */
	private void resetSearchCriteria() {
		getManagedBean().setSearchDocNo(null);
		getManagedBean().setStatusId(null);
		getManagedBean().setSearchSuppName(null);
		getManagedBean().setSearchOrderNo(null);
	}
	
}
