/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.purchaseinquiry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.sc.directpurchase.IPrInquirySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.sc.directpurchase.PrInquiryReqDTO;
import my.ep.dto.sc.directpurchase.PrInquiryResDTO;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.sc.directpurchase.ScPRIWorkFlowStatus;
import my.ep.persistence.entity.sc.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.directpurchase.RequestNoteBackingBean;
import my.ep.web.managedbean.sc.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.DateUtils;
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
 * 
 */
@ManagedBean(name = "purchaseInquiryBackingBean")
@RequestScoped
public class PurchaseInquiryBackingBean extends BaseBackingBean<PurchaseInquiryManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseInquiryBackingBean.class);
    public PurchaseInquiryBackingBean() {
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
	       populateUserDivisionList();
	  }
    
	/**
	  * pricingDetails for getting pricing details in Purchase Inquiry
	 * @return 
	  */
	  public String prInquiryAction(CmSupplierItem  cmSupplierItem){
		  IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		   debugLogger("prInquiryAction");
		   ScPrInquiry scPrInquiry = new ScPrInquiry(); 
		   ScRequestItem scRequestItem = new ScRequestItem();
		   getManagedBean().setSupplierItem(cmSupplierItem);
		   if(cmSupplierItem != null) {
			  ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			  this.getManagedBean().setSupplierItem(cmSupplierItem);
			  ScSupplierItemPrice supplierItemPrice = new ScSupplierItemPrice();
			  supplierItemPrice.setPricing(cmSupplierItem.getCmPricings().get(0));
			  requestSupplierItem.setSmSupplier(cmSupplierItem.getSmSupplier());
			  supplierItemPrice.setScRequestSupplierItem(requestSupplierItem);
			  requestSupplierItem.setSupplierItemPrice(supplierItemPrice);
			  requestSupplierItem.setRequestNoteItem(scRequestItem);
			  
		      // WorkFlow Status
			  List<ScPRIWorkFlowStatus> wflowStatus =  new  ArrayList<ScPRIWorkFlowStatus>();
			  ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			  workFlowStatus.setCurrent(true);
			  workFlowStatus.setPrInquiry(scPrInquiry);
			  wflowStatus.add(workFlowStatus);
			  scPrInquiry.setWorkflowStatuses(wflowStatus);
			  scPrInquiry.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			  scPrInquiry.setDayToResponse(ParameterConst.PIQ_RESP_DAYS); // by default days
			  scPrInquiry.setDateToResponse(calcDayToResponse(scPrInquiry));
			  scPrInquiry.setPrInquiryNo(prInquirySO.generatePrInquiryNo());
			  // Initializing Remarks 
			  scPrInquiry.setRemarks(initializeRemarks(scPrInquiry));
			 
		      List<CmPricing>  cmPricings = new ArrayList<CmPricing>();
		  	if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<String> priceTypes = new ArrayList<String>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!priceTypes.contains(princing.getPricingType())) {
						priceTypes.add(princing.getPricingType());
					}
				}
				if(priceTypes != null && priceTypes.size() > 0){
					String	priceType	=	priceTypes.get(0);
					for (CmPricing princing : cmSupplierItem.getCmPricings()) {
						if(princing.getPricingType().equalsIgnoreCase(priceType)){
							cmPricings.add(princing);
						}
					}
				   requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, cmPricings));
				}
			  // TODO PriceType drop down list in AddToCart
			  getManagedBean().setPriceTypes(getPriceTypeDropdownValues(
		      ParameterTypeConst.PRICE_TYPE, priceTypes));
			}
		  	// get the first type of the priceTypes and populate to requestSupplierItem
		  	List<ScRequestSupplierItem> scRequestSupplierItems = new  ArrayList<ScRequestSupplierItem>(); 
		  	scRequestSupplierItems.add(requestSupplierItem);
		  	// ScRequestSupplerItems
		  	scPrInquiry.setScRequestSupplierItems(scRequestSupplierItems);
			scPrInquiry.setRequestSupplierItem(requestSupplierItem);
		    getManagedBean().setScPrInquiry(scPrInquiry);
		  }
		 //TODO default value to Procurement type and price type drop down listing
		 getManagedBean().setPriceType(ParameterConst.PRICE_TYPE_STANDARD);
		 getManagedBean().getRequestNoteDtl().setProcurementType(ParameterConst.PROC_TYPE_NORMAL);
         return "prInquiryMain";		 
	 }
	  
	public Date calcDayToResponse(ScPrInquiry scPrInquiry) {
		Date dayToResp = null;
		if (scPrInquiry.getDayToResponse() != null) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(scPrInquiry.getCreatedDate());  
		cal.add(Calendar.DATE, scPrInquiry.getDayToResponse());
		dayToResp = cal.getTime();
		scPrInquiry.setDateToResponse(cal.getTime());
		getManagedBean().setScPrInquiry(scPrInquiry);
		}
		return dayToResp;
	}
		
	public SelectItem[] getPriceTypeDropdownValues(String parameterType,
			List<String> priceTypes) {
		SelectItem[] items = null;
		try {
			items = new SelectItem[priceTypes.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (String priceTypeCode : priceTypes) {
				items[i++] = new SelectItem(priceTypeCode, getParameterDesc(
						parameterType, priceTypeCode));
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
	 *  priceTypeChangeListener for getting Item Pricing list in Purchase inquiry
    */
	public void priceTypeChangeListener() {
		debugLogger("priceTypeChangeListener");
		List<CmPricing> pricings = new ArrayList<CmPricing>();
		if (getManagedBean().getSupplierItem() != null) {
			for (CmPricing cmPricing : getManagedBean().getSupplierItem()
					.getCmPricings()) {
				if (getManagedBean().getPriceType().equalsIgnoreCase(
						cmPricing.getPricingType())) {
					pricings.add(cmPricing);
				}
			}
		}
		if(getManagedBean().getScPrInquiry().getScRequestSupplierItems() != null){
			ScRequestSupplierItem requestSupplierItem = getManagedBean().getScPrInquiry().getScRequestSupplierItems().get(0);
			requestSupplierItem.setSupplierItemPrices(createScSupplierItemPrices(requestSupplierItem, pricings));
		}
		
	}
		
	 /**
	  *  submitPrEnquiryDtl  method to submit purchase inquiry details  in Purchase inquiry
	  */	
	  public String submitPrEnquiryDtl() {
		  debugLogger("submitPrEnquiryDtl");
		    IPrInquirySO prInquirySO = null;
		    try {
			     prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
				 PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();				 
				 ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
			     prInquiry.setDateToResponse(new Timestamp(System.currentTimeMillis()));
			     List<ScRequestNoteDtl> requestNoteDtls =  new  ArrayList<ScRequestNoteDtl>();
			     List<ScRequestSupplierItem> requestSupplierItems =  new  ArrayList<ScRequestSupplierItem>();
			     for (ScRequestSupplierItem scRequestSupplierItem :  prInquiry.getScRequestSupplierItems()) {
			    	 ScRequestNoteDtl requestNoteDtl = getManagedBean().getRequestNoteDtl();
				     requestNoteDtl.setScRequestNote(new ScRequestNote());
				     requestNoteDtls.add(requestNoteDtl);
				     scRequestSupplierItem.setScRequestNoteDtl(requestNoteDtls);
				     requestSupplierItems.add(scRequestSupplierItem);
				}
			     prInquiry.setScRequestSupplierItems(requestSupplierItems);
			     prInquiry.setCreatedDate(DateUtils.getCurrentDate());
			     // setting remarks 
			     prInquiry.setRemarks(initializeRemarks(prInquiry));
			     // WorkFlow Status
			     List<ScPRIWorkFlowStatus> wflowStatus =  new  ArrayList<ScPRIWorkFlowStatus>();
			     ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			     workFlowStatus.setPrInquiry(prInquiry);
			     wflowStatus.add(workFlowStatus);
			     prInquiry.setWorkflowStatuses(wflowStatus);
				 FacesScopes facesScopes = new FacesScopes();
				 String token = facesScopes.getBPMAuthenticationCtxToken();
				 reqDTO.setToken(token);
			     reqDTO.setOrgProfileId(getManagedBean().getOrgProfileId());
			     reqDTO.setPrInquiryDetail(prInquiry);
				 PrInquiryResDTO prInquiryResDTO = prInquirySO.submitPrInquiry(reqDTO);
				
				 if (prInquiryResDTO.isSuccessful()) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Record saved successfully.", ""
							));
					//return "/xhtml/shared/acknowledge.xhtml";
				} 
				
			} catch (Exception ex) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sorry We are unable to process.", ""));
				ex.printStackTrace();
			} finally {
				prInquirySO = null;
			}
			return null;
		}
		
	  
	  public void submitPrInquiryPrice(String outcome){
		   IPrInquirySO prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IPrInquirySO.class);
		   IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
		   if(StringUtils.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT,outcome)){
			   //call prInquirySO submitPrice method
			   PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
			   reqDTO.setPrInquiryDetail(getManagedBean().getScPrInquiry());
			   FacesScopes facesScopes = new FacesScopes();
				 String token = facesScopes.getBPMAuthenticationCtxToken();
			   reqDTO.setToken(token);
			   reqDTO.setTaskId(getManagedBean().getTaskId());
			   PrInquiryResDTO resDTO = prInquirySO.submitPrInquiryPrice(reqDTO);
			   if(resDTO.isSuccessful()){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""));
			   }else{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Sorry We are unable to process.", ""));
			   }
		   }else{
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
	  }
	  
	/**
	 * Initializing remarks in Purchase inquiry
	 */
	public List<ScPRIRemark> initializeRemarks(ScPrInquiry prInquiry) {
		FacesScopes facesScopes = new FacesScopes();
		List<ScPRIRemark> results = prInquiry.getRemarks();
		if(results == null){
			 results = new ArrayList<ScPRIRemark>();
		}
		if (results.size() == 0
				|| !compareUserName(results.get(0).getUser().getLoginId())) {
			ScPRIRemark remark = new ScPRIRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setUser(facesScopes.getUserSessionBean());
			if (getManagedBean().getScPrInquiry().getRemarks() != null
					&& getManagedBean().getScPrInquiry().getRemarks().size() > 0) {
				remark.setRemark(getManagedBean().getScPrInquiry().getRemarks()
						.get(0).getRemark());
			}
			PmStatus status = new PmStatus();
			status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
			remark.setPmStatus(status);
			remark.setPrInquiry(prInquiry);
			results.add(0, remark);
		}
		return results;
	}

	/**
	 * to get Division drop down Listing in Purchase inquiry
	 */
	private void populateUserDivisionList() {
		debugLogger("populateUserMinistryList");
		PmUser user = new FacesScopes().getUserSessionBean();
		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userDivisionList = resDTO.getOrgProfileList();
			System.out.println("userDivisionList" + userDivisionList.size());
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}

	/**
	 * Event for procurement type drop down in Purchase inquiry
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
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_ADHOC));

		} else if (getManagedBean().getRequestNoteDtl().getProcurementType()
				.equalsIgnoreCase(ParameterConst.PROC_TYPE_EMERGENCY)) {
			getManagedBean().setProcTypeOption(true);
			getManagedBean()
					.setJustDropdown(
							getDropdownCodeValues(ParameterTypeConst.JUSTIFICATION_TYPE_EMERGENCY));
		}
	}
		
	/**
	 * Event for justification type drop down in Purchase inquiry
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
	
	public SelectItem[]  getProcTypeDropdown() {
		debugLogger("getProcTypeDropdown");
		SelectItem[] selectItems = new SelectItem[4];
		selectItems[0] = new SelectItem("N", "Normal");
		selectItems[1] = new SelectItem("A","AdHoc");
		selectItems[2] = new SelectItem("E", "Emergency");
		selectItems[3] = new SelectItem("G", "G2G");
		return selectItems;
	}
	
	public	String	requisitionerAction(String action){
		if(StringUtils.equalsIgnoreCase("PROCESSED",action)){
			//redirect to requestNote screen
			ScRequestNote	requestNote	=	getManagedBean().getScPrInquiry().getScRequestSupplierItems().get(0).getRequestNoteItem().getRequestNote();
			RequestNoteReqDTO	reqDTO	=	new RequestNoteReqDTO();
			reqDTO.setRequestNoteId(requestNote.getRequestNoteId());
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
			RequestNoteResDTO resDTO = directPurchaseSO.getRequestNote(reqDTO);
			requestNote	=	resDTO.getRequestNoteDetail();
			//get RequestNote Backing bean and manage bean and set Request Note to RequestNote manage bean
			PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			RequestNoteBackingBean requestNoteBackingBean = (RequestNoteBackingBean) portletSession.getAttribute("requestNoteBackingBean");
			if (requestNoteBackingBean == null) {
				requestNoteBackingBean = new RequestNoteBackingBean();
			}
			List<ScRequestNoteDtl>	requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
            for (ScRequestSupplierItem requestSupplierItem : getManagedBean().getScPrInquiry().getScRequestSupplierItems()) {
            	ScRequestNoteDtl requestNoteDtl = requestSupplierItem.getScRequestNoteDtl().get(0);
            	requestNoteDtl.setScRequestSupplierItem(requestSupplierItem);
            	requestNoteDtl.setAssignedUser(new PmUser());
            	requestNoteDtls.add(requestNoteDtl);
			}
			requestNoteBackingBean.getManagedBean().setRequestNote(requestNote);
			requestNoteBackingBean.getManagedBean().setRequestNoteDtls(requestNoteDtls);
			requestNoteBackingBean.getManagedBean().setSourceMethod(getManagedBean().getMethod());
			initializeRemarks(getManagedBean().getScPrInquiry());
			requestNoteBackingBean.getManagedBean().setRequestSupplierItem(requestNote.getRequestNoteDtls().get(0).getScRequestSupplierItem());
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			BpmRequestDTO reqBPMDTO = new BpmRequestDTO();
			reqBPMDTO.setCtxToken(token);
			reqBPMDTO.setTaskId(getManagedBean().getTaskId());
			BpmResponseDTO res = bpmSO.getTaskDetails(reqBPMDTO);
			requestNoteBackingBean.getManagedBean().setOutComes(res.getAction());
			requestNoteBackingBean.initializeRemarks(requestNote);
			
			//redirect to RequestNote screen
			return "commonRequestNote";
		}else{
			//cancel BPM task
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			BpmRequestDTO reqBpm = new BpmRequestDTO();
			FacesScopes facesScopes = new FacesScopes();
			String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
			try {
				reqBpm.setTaskId(getManagedBean().getTaskId());
				//TODO : change to cancel later
				reqBpm.setOutcome(BpmOutcomeConst.BPM_REJECT);
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
			return null;
		}
	}
	
	/**
	 * Page navigation to catalogue search page in Purchase inquiry
	 */
	public String backToPervious() {
		log.info("backToPervious");
		return "catalogueSearch";
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
			
	public String getCurrentStatus() {
		String result = "";
		if (getManagedBean().getScPrInquiry().getWorkflowStatuses() != null) {
			for (ScPRIWorkFlowStatus status : getManagedBean().getScPrInquiry()
					.getWorkflowStatuses()) {
				if (status.isCurrent()) {
					result = getStatusDescById(status.getPmStatus()
							.getStatusId());
					if (result.equalsIgnoreCase("Pending Submission")) {
						getManagedBean().setEditFlag(false);
						getManagedBean().setButtonFlag(false);
					} else {
						getManagedBean().setEditFlag(true);
						getManagedBean().setButtonFlag(true);
					}
					break;
				}
			}
		}
		return result;
	}
	
	public void ptjNameEvent() {
		if (getManagedBean().getOrgProfileId() != null) {
			for (PmOrgProfile orgProfile : getManagedBean()
					.getUserDivisionList()) {
				if (orgProfile.getOrgProfileId().equals(
						getManagedBean().getOrgProfileId())) {
					getManagedBean().setPmOrgProfile(orgProfile);
				}
			}
		}
	}
	
	
	private	List<ScSupplierItemPrice>	createScSupplierItemPrices(ScRequestSupplierItem	scRequestSupplierItem, List<CmPricing> pricings){
		List<ScSupplierItemPrice> results	=	new ArrayList<>();
		for(CmPricing pricing: pricings){
			results.add(createSupplierItemPrice(scRequestSupplierItem, pricing));
		}
		return results;
	}
	private ScSupplierItemPrice		createSupplierItemPrice(ScRequestSupplierItem	scRequestSupplierItem, CmPricing pricing){
		ScSupplierItemPrice	result	=	new ScSupplierItemPrice();
		result.setScRequestSupplierItem(scRequestSupplierItem);
		result.setItemPrice(pricing.getPrice());
		result.setPricing(pricing);
		result.setPriceType("");
		return result;
	}
}