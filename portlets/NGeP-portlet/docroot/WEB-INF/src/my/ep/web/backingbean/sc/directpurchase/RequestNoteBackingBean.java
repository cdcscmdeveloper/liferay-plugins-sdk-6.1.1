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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
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
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.managedbean.sc.directpurchase.RequestNoteManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
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
	    IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
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
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
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
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
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
		 List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
		 ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus(SourcingConstants.RN_PENDING_APPROVAL);
		 workFlowStatus.setRequestNote(requestNote);
	     workFlowStatus.setCurrent(true);
	     workFlowStatuses.add(workFlowStatus);
    	 requestNote.setWorkFlowStatuses(workFlowStatuses);
		// Remarks
		requestNote.setRemarks(initializeRemarks(requestNote));
		requestNoteReqDTO.setRequestNote(requestNote);
		
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		requestNoteReqDTO.setRequestNote(requestNote);
		//Hoa: when submit need to set submit in the reqDTO to true
		requestNoteReqDTO.setSubmit(Boolean.TRUE);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		requestNoteReqDTO.setToken(token);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
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
	
	/**
	 * render the view base on the task id
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if (StringUtils.isNotBlank(taskId)) {
			this.getManagedBean().setTaskId(taskId);
			log.info("taskId : " + taskId);
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			this.getManagedBean().setOutComes(res.getAction());
			this.getManagedBean().setProcessName(res.getProcessName());
			Object obj = res.getPojo();
			SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType) obj;
			String	dpMode	=	directPurchaseDataType.getDpMode();
			
			if("RN".equalsIgnoreCase(dpMode)){
				//RequestNote Details
				IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
				RequestNoteReqDTO	requestDTO	=	new RequestNoteReqDTO();
				requestDTO.setRequestNoteId(Long.valueOf(directPurchaseDataType.getDpId()));
				RequestNoteResDTO	resDTO	=	directPurchaseSO.getRequestNote(requestDTO);
				ScRequestNote	requestNote	=	resDTO.getRequestNoteDetail();
				getManagedBean().setRequestNoteDtls(requestNote.getRequestNoteDtls());
				getManagedBean().setDirectPurchaseDataType(directPurchaseDataType);
				List<ScRequestNoteRemark> results= new ArrayList<ScRequestNoteRemark>();
				for (ScRequestNoteRemark remark : requestNote.getRemarks()) {
					remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
					remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					remark.setChangedBy(remark.getCreatedBy());
					remark.setChangedDate(remark.getCreatedDate());
					remark.setUser(facesScopes.getUserSessionBean());
				    results.add(remark);
		       }
		       requestNote.setRemarks(results);
			   getManagedBean().setRequestNote(requestNote);
			   initialInformation(requestNote);
			   FacesContext fc = FacesContext.getCurrentInstance();
			   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "commonRequestNote");
			
			} else if("PRI".equalsIgnoreCase(dpMode)){ 
				//Purchase Inquiry
				IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
				PrInquiryReqDTO prInquiryReqDTO = new PrInquiryReqDTO();
				Long prInquiryID = Long.valueOf(directPurchaseDataType.getDpId());
				prInquiryReqDTO.setPrInquiryId(prInquiryID);
				PrInquiryResDTO prInquiryResDTO = prInquirySO.getPrInquiry(prInquiryReqDTO);
				PurchaseInquiryBackingBean inquiryBackingBean = new PurchaseInquiryBackingBean();
				inquiryBackingBean.getManagedBean().setScPrInquiry(prInquiryResDTO.getPrInquiryDetail());
			    FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "prInquiryMain");
			
			} else if("PID".equalsIgnoreCase(dpMode)){ 
				// Purchase In Default	
				Long pidID = Long.valueOf(directPurchaseDataType.getDpId());
		    }
			getManagedBean().setOutComes(res.getAction());
	}
  }	
	
	public void updateBpmTask(String outcome) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO reqBpm = new BpmRequestDTO();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		try {
			reqBpm.setTaskId(getManagedBean().getTaskId());
			reqBpm.setOutcome(outcome);
			if (null != ctxToken) {
				reqBpm.setCtxToken(ctxToken);
				BpmResponseDTO resBpm = bpmSO.updateTask(reqBpm);
				boolean result = resBpm != null ? resBpm.isSuccessful() : false;
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to process.", ""));
			ex.printStackTrace();
		} finally {
			bpmSO = null;
		}
	}
	public void applyForRequestNote() {
	   log.info("applyForRequestNote");
	   List<ScRequestNoteDtl>	requestNoteDtls	=	getManagedBean().getRequestNoteDtls();
	   if(getManagedBean().getSelectedRequestNoteDtls() != null) {
		  for(ScRequestNoteDtl selectedReqDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			 if(getManagedBean().getRequestNoteDtls().contains(selectedReqDtl)){
				selectedReqDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
				PmUser pmUser = new PmUser();
				pmUser.setUserId(getManagedBean().getAssignedToUserId());
			    selectedReqDtl.setAssignedUser(pmUser);
				selectedReqDtl.setDeliverPeriodType(getManagedBean().getPeriodType());
				selectedReqDtl.setDeliverPeriod(getManagedBean().getPeriodValue());
				requestNoteDtls.add(selectedReqDtl);
				getManagedBean().getRequestNoteDtls().remove(selectedReqDtl);
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			  }
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

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
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
			ScRequestItem requestItem = new ScRequestItem();
			ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			CmSupplierItem cmSupplierItem = catResDTO.getSupplierItemList().get(0);
		    ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
		    requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
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
				getManagedBean().setPriceTypes(
						getPriceTypeDropdownValues(
								ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
		}
		//TODO default value to Procurement type and price type drop down listing
	    getManagedBean().setPriceTypeCode(ParameterConst.PRICE_TYPE_STANDARD);
	    getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
	}
	
	/**
	 * get AddtoCart details in Add To Cart PopUp in compare catalogue
	 */
	public void compareAddToCart(CmSupplierItem cmSupplierItem) {
		log.info("addToCart");
		 if(cmSupplierItem != null) {
		    ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			ScRequestItem requestItem = new ScRequestItem();
			ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
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
		  //TODO default value to Procurement type and price type drop down listing
		  getManagedBean().setPriceTypeCode(ParameterConst.PRICE_TYPE_STANDARD);
		  getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
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
				//requestSupplierItem.setPricing(cmSupplierItem.getCmPricings().get(0));
				requestSupplierItem.setSmSupplier(cmSupplierItem
						.getSmSupplier());
				getManagedBean().setRequestSupplierItem(requestSupplierItem);
				getManagedBean().setRequestNoteDtl(inquiryBackingBean.getManagedBean().getRequestNoteDtl());
			}
		}
		FacesScopes facesScopes = new FacesScopes();
		ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
		
		ScRequestNote requestNote =  null;
		if(getManagedBean().getRequestNote() == null || getManagedBean().getRequestNote().getRequestNoteNo() == null){
			requestNote	=	new ScRequestNote();
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			// TODO hard coding PmOrgProfile to Request Note
			orgProfileReqDTO.setOrgProfileId(4L);
			OrgProfileResDTO pmResDTO = orgProfileSO
					.retrieveOrgProfileByProfileId(orgProfileReqDTO);
			requestNote.setPmOrgProfile(pmResDTO.getPmOrgProfile());
			//Hoa: use getRequestNoteNo() to get Request Note Number
			requestNote.setRequestNoteNo(directPurchaseSO.generateRequestNoteNo());
			
			if (sourceMethod.equalsIgnoreCase(ParameterConst.METHOD_SC)) {
				   requestNote.setSourceMethod("SC");
				   getManagedBean().setSourceMethod(sourceMethod);
			} else if (sourceMethod.equalsIgnoreCase(ParameterConst.METHOD_PIQ)) {
				  requestNote.setSourceMethod("PIQ");
				  getManagedBean().setSourceMethod(sourceMethod);
			}
			initializeRemarks(requestNote);
		}else{
			requestNote	=	getManagedBean().getRequestNote();
		}
		
		//Hoa: TODO WorkFlow Status should be set In the save, submit method
	    List<ScRNWorkFlowStatus> workFlowStatuses =  new  ArrayList<ScRNWorkFlowStatus>();
	    ScRNWorkFlowStatus workFlowStatus = new ScRNWorkFlowStatus(SourcingConstants.RN_PENDING_SUBMISSION);
	    workFlowStatus.setRequestNote(requestNote);
	    workFlowStatus.setCurrent(true);
	    workFlowStatuses.add(workFlowStatus);
	    if(requestNote.getWorkFlowStatuses() != null){
	    	requestNote.getWorkFlowStatuses().addAll(workFlowStatuses);
	    }else{
	    	requestNote.setWorkFlowStatuses(workFlowStatuses);
	    }
		ScRequestSupplierItem requestSupplierItem = getManagedBean().getRequestSupplierItem();
		scRequestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
		scRequestNoteDtl.setAssignedUser(facesScopes.getUserSessionBean());
		scRequestNoteDtl.setScRequestNote(requestNote);
		getManagedBean().getRequestNoteDtls().add(scRequestNoteDtl);
		
		populateUserDivisionList();
		getManagedBean().setRequestNote(requestNote);
		initialInformation(requestNote);
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
	
	public String showJustDescription(String justficationCode){
		String justDesc = "";
		if(getManagedBean().getJustDropdown() !=null){
		  for (SelectItem selectItem : getManagedBean().getJustDropdown()) {
			  if(selectItem.getValue() != null){
			   if(selectItem.getValue().equals(justficationCode)){
				  justDesc = selectItem.getLabel();
			    }
			  }
			}
		}
		return justDesc;
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
			IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
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
	
	public void deleteRequestNoteDtl() {
		log.info("deleteRequestNoteDtl");
		if (getManagedBean().getSelectedRequestNoteDtls() != null) {
			for (ScRequestNoteDtl requestNoteDtl : getManagedBean()
					.getSelectedRequestNoteDtls()) {
				if (getManagedBean().getRequestNoteDtls().contains(
						requestNoteDtl)) {
					List<ScRequestNoteDtl> reqNoteDtl = getManagedBean()
							.getRequestNoteDtls();
					reqNoteDtl.remove(requestNoteDtl);
					getManagedBean().setRequestNoteDtls(reqNoteDtl);
				}
			}
		}
	}
	
	public SelectItem[]  getProcTypeDropdown() {
		SelectItem[] selectItems = new SelectItem[4];
		selectItems[0] = new SelectItem("N", "Normal");
		selectItems[1] = new SelectItem("A","AdHoc");
		selectItems[2] = new SelectItem("E", "Emergency");
		selectItems[3] = new SelectItem("G", "G2G");
		return selectItems;
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
	
	public	void	initialInformation(ScRequestNote requestNote){
		List<PmUser> availableApprover	=	new ArrayList<PmUser>();
		List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
		if(requestNote.getApprovalUsers() != null && requestNote.getApprovalUsers().size()>0){
		for (ScRequestNoteApprovalUser requestNoteApprovalUser : requestNote.getApprovalUsers()) {
			selectedApprover.add(requestNoteApprovalUser.getPmUser());
		} 
		}else {
			IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDto = new UserReqDTO();
			userReqDto.setRoleCode(RoleCode.APPROVER.value());
			UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
			 availableApprover	=	userResDTO.getUserList();
		}
		getManagedBean().setApprovalUsers(new DualListModel<PmUser>(availableApprover, selectedApprover));
	}
}