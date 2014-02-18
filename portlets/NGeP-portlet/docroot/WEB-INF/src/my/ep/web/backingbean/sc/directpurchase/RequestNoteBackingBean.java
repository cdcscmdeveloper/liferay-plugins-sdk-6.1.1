/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteApprovalUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.sc.directpurchase.RequestNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
/**
 * 
 * @author sri
 * @since 11.02.2014
 * 
 */
@ManagedBean(name = "requestNoteBackingBean")
@RequestScoped
public class RequestNoteBackingBean extends BaseBackingBean<RequestNoteManagedBean> {
	private static Logger log = Logger.getLogger(RequestNoteBackingBean.class);
	 
	public RequestNoteBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
	}
	
	/**
	 * saveAction method to save request note details
	 * return String
	 */
	public String saveAction() {
	    IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
    try {
		if (this.getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() > 0) {
			requestNote.setRequestNoteDtls(this.getManagedBean().getRequestNoteDtls());
		}
		// Target Approver
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
			ScRequestNoteApprovalUser approvalUser = new ScRequestNoteApprovalUser();
		    requestNote.setApprovalUsers(createTargetApprover(approvalUser));
		}
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNoteReqDTO.setRequestNote(requestNote);
		
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		requestNoteReqDTO.setRequestNote(requestNote);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Record saved successfully.", ""));

		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			directPurchaseSO = null;
		}
		return null;
	}
	
	/**
	 * submitAction method to submit request note details
	 * return String
	 */
	public String submitAction() {
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
    try {
		if (this.getManagedBean().getRequestNoteDtls() != null && getManagedBean().getRequestNoteDtls().size() > 0) {
			requestNote.setRequestNoteDtls(this.getManagedBean().getRequestNoteDtls());
		}
		// Target Approver
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
			ScRequestNoteApprovalUser approvalUser = new ScRequestNoteApprovalUser();
		    requestNote.setApprovalUsers(createTargetApprover(approvalUser));
		}
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNoteReqDTO.setRequestNote(requestNote);
		
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		requestNoteReqDTO.setRequestNote(requestNote);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		
		// submit BPM task
		//BpmResponseDTO res = createBPMTask(requestListResDTO, true);
		//getManagedBean().setTaskId(requestNoteResDTO.getTaskId());
		//this.getManagedBean().setRequestNote(requestNoteResDTO.getRequestNoteDetail());

		if (requestNoteResDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""));
				// return "/xhtml/shared/acknowledge.xhtml";
			}

		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			directPurchaseSO = null;
		}
		return null;
	}
	
	public void applyForRequestNote() {
	   List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	   if (getManagedBean().getSelectedRequestNoteDtls() != null) {
		   for(ScRequestNoteDtl requestNoteDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
			PmUser pmUser = new PmUser();
			pmUser.setUserId(getManagedBean().getAssignedToUserId());
			requestNoteDtl.setAssignedUser(pmUser);
			requestNoteDtl.setDeliverPeriodType(getManagedBean().getPeriodType());
			requestNoteDtl.setDeliverPeriod(getManagedBean().getPeriodValue());
			requestNoteDtls.add(requestNoteDtl);
			getManagedBean().setRequestNoteDtls(requestNoteDtls);
		}
		}
	}
	
	private void populateUserDivisionList() {
		log.info("populateUserMinistryList");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userDivisionList = resDTO.getOrgProfileList();
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}
	
	private	ScRequestNote	removeEmptyRemarkBeforeSave(ScRequestNote requestList){
		ScRequestNote result	=	requestList;
		List<ScRequestNoteRemark> removeList = new ArrayList<ScRequestNoteRemark>();
		if(requestList.getRemarks() != null){
			for(ScRequestNoteRemark remark: requestList.getRemarks()){
				if(StringUtils.isBlank(remark.getRemark())){
					removeList.add(remark);
				}
			}
			result.getRemarks().removeAll(removeList);
		}
		return result;
	}
	
	/**
	  * initializing remarks in Request Note
	 */
	private List<ScRequestNoteRemark> initializeRemarks(ScRequestNote scRequestNote) {
		List<ScRequestNoteRemark> results =  scRequestNote.getRemarks();
		FacesScopes facesScopes = new FacesScopes();
		if (results.size() == 0
				|| !compareUserName(results.get(0).getUser().getLoginId())) {
			ScRequestNoteRemark remark = new ScRequestNoteRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getRequestNote().getRemarks() != null && getManagedBean().getRequestNote().getRemarks().size()>0) {
			   remark.setRemark(getManagedBean().getRequestNote().getRemarks().get(0).getRemark());
			} 
			PmStatus status = new PmStatus();
			status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
			remark.setPmStatus(status);
			remark.setRequestNote(scRequestNote);
			results.add(0, remark);
		}
		return results;
	}
	
	/**
	 * get AddtoCart details in Add To Cart PopUp
	 */
	public void addToCart(VCmSupplierItem vCmSupplierItem) {
		log.info("addToCart");
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		catReqDTO.setDocId(vCmSupplierItem.getDocId());
		CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
		
		if (catResDTO.getSupplierItemList() != null) {
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			ScRequestItem requestItem = new ScRequestItem();
			requestItem.setRequestNoteItemId(1l);
			CmSupplierItem cmSupplierItem = catResDTO.getSupplierItemList().get(0);
			requestSupplierItem.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(catResDTO.getSupplierItemList().get(0).getSmSupplier());
			requestSupplierItem.setRequestNoteItem(requestItem);
			getManagedBean().setRequestSupplierItem(requestSupplierItem);
			if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
				// TODO PriceType drop down list in AddToCart
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(
						ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
			}
		}
	
	/**
	 * get AddtoCart details in Add To Cart PopUp in compare catalogue
	 */
	public void compareAddToCart(CmSupplierItem cmSupplierItem) {
		log.info("addToCart");
		if (cmSupplierItem != null) {
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			ScRequestItem requestItem = new ScRequestItem();
			requestItem.setRequestNoteItemId(1l);
			requestSupplierItem.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			requestSupplierItem.setRequestNoteItem(requestItem);
			getManagedBean().setRequestSupplierItem(requestSupplierItem);
			if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
				// TODO PriceType drop down list in AddToCart
				getManagedBean().setPriceTypes(getPriceTypeDropdownValues(
						ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
			}
		}
	
	
	 public SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode,getParameterDesc(parameterType,
						priceTypeCode));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	
	/**
	 * Page navigation to catalogue search page in Request Note
	 */
	public String backToPervious() {
		log.info("backToPervious");
		return "catalogueSearch";
	}

	public String addToRequestNote(String sourceMethod) {
		log.info("addToRequestNote");
		if (sourceMethod
				.equalsIgnoreCase(ParameterConst.METHOD_PIQ)) {
			PurchaseInquiryBackingBean inquiryBackingBean = new PurchaseInquiryBackingBean();
			CmSupplierItem cmSupplierItem = inquiryBackingBean.getManagedBean()
					.getSupplierItem();
			if (cmSupplierItem != null) {
				ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
				requestSupplierItem.setPricing(cmSupplierItem.getCmPricings()
						.get(0));
				requestSupplierItem.setSmSupplier(cmSupplierItem
						.getSmSupplier());
				getManagedBean().setRequestSupplierItem(requestSupplierItem);
				getManagedBean().setRequestNoteDtl(inquiryBackingBean.getManagedBean().getRequestNoteDtl());
			}
		}
		FacesScopes facesScopes = new FacesScopes();
		List<ScRequestNoteDtl> requestNoteDtlList = new ArrayList<ScRequestNoteDtl>();
		ScRequestNoteDtl scRequestNoteDtl = getManagedBean().getRequestNoteDtl();
		ScRequestNote requestNote = new ScRequestNote();

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance().getServiceObject("pm",
						IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		// TODO hard coding PmOrgProfile to Request Note
		orgProfileReqDTO.setOrgProfileId(4L);
		OrgProfileResDTO pmResDTO = orgProfileSO
				.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		requestNote.setPmOrgProfile(pmResDTO.getPmOrgProfile());
		requestNote.setRequestNoteNo("215");
		requestNote.setSourceMethod(sourceMethod);
		
		// WorkFlow Status
	    List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
	    ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus(SourcingConstants.RN_PENDING_SUBMISSION);
	    workFlowStatus.setRequestNote(requestNote);
	    workFlowStatus.setCurrent(true);
	    workFlowStatuses.add(workFlowStatus);
	    requestNote.setWorkFlowStatuses(workFlowStatuses);
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		scRequestNoteDtl.setScRequestNote(requestNote);
		requestNoteDtlList.add(scRequestNoteDtl);
		getManagedBean().setRequestNoteDtls(requestNoteDtlList);
		initializeRemarks(requestNote);
		populateUserDivisionList();
		getManagedBean().setRequestNote(requestNote);
		
		return "commonRequestNote";
	}
	
	/**
	 * Event for procurement type  drop down in Add to Cart
	 */
	public void procurementTypeEvent() {
		log.info("procurementTypeEvent"); 
		if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_NORMAL)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_G2G)) {
			getManagedBean().setProcTypeOption(false);
			getManagedBean().setJustTypeOption(false);
		
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_AD_HOC)) {
			getManagedBean().setProcTypeOption(true);
		    getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));
			
		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
            getManagedBean().setProcTypeOption(true);
            getManagedBean().setJustDropdown(getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
	}
	
	/**
	 * Event for justification type drop down in Add to Cart
	 */
	public void justificationTypeEvent() {
		log.info("justificationTypeEvent"); 
		if (getManagedBean().getRequestNoteDtl().getJustificationType()
				.equalsIgnoreCase(ParameterTypeConst.JUSTIFICATION_TYPE_OTHER)) {
			getManagedBean().setJustTypeOption(true);
		} else {
			getManagedBean().setJustTypeOption(false);
		}
	}
	
	/**
	 * show Item Details, Justification, Pricing details in Request Note
	 */
	public void showReqNotePopUps(ScRequestNoteDtl scRequestNoteDtl){
		log.info("showReqNotePopUps");
		if(scRequestNoteDtl != null){
			getManagedBean().setRequestSupplierItem(scRequestNoteDtl.getScRequestSupplierItem());
		}
	}
	
	/**
	 * Event for fulfilment type drop down in request note
	 */
	public void fulfilmentTypeEvent(ScRequestNoteDtl requestNoteDtl) {
		log.info("fulfilmentTypeEvent"); 
		if(getManagedBean().getFulfillmentType() != null){
			requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
		}
		
		List<PmUser> userlist = new ArrayList<PmUser>();
		if(requestNoteDtl != null ){
		if (requestNoteDtl.getFulfillmentType().equalsIgnoreCase(ParameterConst.FULFILLMENT_ONE_OFF)) {
			userlist = userListByRole(RoleCode.REQUISITIONER.value());
			getManagedBean().setUserList(userlist);
			
		 } else if (requestNoteDtl.getFulfillmentType().equalsIgnoreCase(ParameterConst.FULFILLMENT_PERIODIC)) {
			List<String> roleList= new ArrayList<String>();
			roleList.add(RoleCode.DESK_OFFICER.value());
			roleList.add(RoleCode.REQUISITIONER.value());
			for (String role : roleList) {
			    userlist.addAll(userListByRole(role));
			}
		    getManagedBean().setUserList(userlist);
		   }
		}
	}
	
	public List<PmUser> userListByRole(String roleCode){
		log.info("userListByRole - Start");
		List<PmUser> options = new ArrayList<PmUser>();
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(roleCode);
			IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
			UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
				options = resDTO.getUserList();
			log.info("result size: " +options.size());
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.toString(),"Role "+RoleCode.PROGRAM_SECTOR.value()));
			throw e;
		}
		log.info("userListByRole - End");
		return options;
	}
	
	
	/**
	  * To get Approver Pick List  in Request Note
	 */
	private	List<ScRequestNoteApprovalUser> createTargetApprover(ScRequestNoteApprovalUser approver){
		List<ScRequestNoteApprovalUser> results = new ArrayList<ScRequestNoteApprovalUser>();
		 for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
			 approver.setPmUser(pmUser);
		      results.add(approver);
		}
		return results;
	}
    
	public String getCurrentStatus(){
		String result = "";
		if (getManagedBean().getRequestNote().getWorkFlowStatuses() != null) {
		for(ScRNWorkFlowStatus status : getManagedBean().getRequestNote().getWorkFlowStatuses()){
			if(status.isCurrent()){
				result = getStatusDescById(status.getPmStatus().getStatusId());
				break;
			}
		}
		}
		return result;
	}
}