package my.ep.web.backingbean.ss.purchaseinquiry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.ss.directpurchase.IPrInquirySO;
import my.ep.constant.ss.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.ss.directpurchase.PrInquiryReqDTO;
import my.ep.dto.ss.directpurchase.PrInquiryResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScPRIRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPRIWorkFlowStatus;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.purchaseinquiry.PurchaseInquiryManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	private static final String NAV_TO_PURCHASE_INQUIRY_PAGE = "prInquiryMain";
	private static final String NAV_TO_DIRECT_PURCHASE_PAGE = "/xhtml/ss/directpurchase/directPurchase.xhtml";
	
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
	  */
	  public void pricingDetails(VCmSupplierItem vCmSupplierItem){
		   debugLogger("pricingDetails");
		   ICatalogueSO catalogueSO	=	(ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		   CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
		   catReqDTO.setDocId(vCmSupplierItem.getDocId());
		   CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
		   System.out.println("supplerItem List "+catResDTO.getSupplierItemList().size());
			
		   if(catResDTO.getSupplierItemList() != null) {
			  ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			  CmSupplierItem cmSupplierItem= catResDTO.getSupplierItemList().get(0);
			  this.getManagedBean().setSupplierItem(cmSupplierItem);
			  requestSupplierItem.setPricing(cmSupplierItem.getCmPricings().get(0));
			  requestSupplierItem.setSmSupplier(catResDTO.getSupplierItemList().get(0).getSmSupplier());
		      ScPrInquiry scPrInquiry = new ScPrInquiry(); 
		      // WorkFlow Status
			  List<ScPRIWorkFlowStatus> wflowStatus =  new  ArrayList<ScPRIWorkFlowStatus>();
			  ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			  workFlowStatus.setCurrent(true);
			  workFlowStatus.setPrInquiry(scPrInquiry);
			  wflowStatus.add(workFlowStatus);
			  scPrInquiry.setWorkflowStatuses(wflowStatus);
			  // Initializing Remarks 
			  scPrInquiry.setRemarks(initializeRemarks(scPrInquiry));
			  scPrInquiry.setRequestSupplierItem(requestSupplierItem);
		      getManagedBean().setScPrInquiry(scPrInquiry);

			 if (cmSupplierItem.getCmPricings() != null
					&& cmSupplierItem.getCmPricings().size() > 0) {
				List<CmPricing> cmPricings = new ArrayList<CmPricing>();
				for (CmPricing princing : cmSupplierItem.getCmPricings()) {
					if (!getManagedBean().getPriceTypes().contains(princing.getPricingType())) {
						CmPricing ptype = new CmPricing();
						ptype.setPricingType(princing.getPricingType());
						cmPricings.add(ptype);
						getManagedBean().setPriceTypes(cmPricings);
					}
				 }
			 }
		  }
		   FacesContext fc = FacesContext.getCurrentInstance();
		   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_PURCHASE_INQUIRY_PAGE);
		}
		
		 /**
	      *  priceTypeChangeListener for getting Item Pricing list in Purchase inquiry
		  */
		  public void priceTypeChangeListener(){
			debugLogger("priceTypeChangeListener");
			List<CmPricing> princings = new ArrayList<CmPricing>();
			if (getManagedBean().getSupplierItem() != null) {
				for (CmPricing cmPricing : getManagedBean().getSupplierItem()
						.getCmPricings()) {
					if (getManagedBean().getPriceType().equalsIgnoreCase(
							cmPricing.getPricingType())) {
						princings.add(cmPricing);
					}
				}
			}
			List<ScRequestSupplierItem> ScRequestSupplierItems = new ArrayList<ScRequestSupplierItem>();
			for (CmPricing price : princings) {
				ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
				requestSupplierItem.setPricing(price);
				// set request Item
				ScRequestItem requestItem = new ScRequestItem();
				requestSupplierItem.setRequestNoteItem(requestItem);
				requestSupplierItem.setSmSupplier(getManagedBean()
						.getSupplierItem().getSmSupplier());
				ScRequestSupplierItems.add(requestSupplierItem);
			}
			getManagedBean().getScPrInquiry().setScRequestSupplierItems(
					ScRequestSupplierItems);
		}
		
	 /**
	  *  submitPrEnquiryDtl  method to submit purchase inquiry details  in Purchase inquiry
	  */	
	  public String submitPrEnquiryDtl() {
		  debugLogger("submitPrEnquiryDtl");
		    IPrInquirySO prInquirySO = null;
		    try {
			     prInquirySO = (IPrInquirySO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IPrInquirySO.class);
				 PrInquiryReqDTO reqDTO = new PrInquiryReqDTO();
				 ScPrInquiry prInquiry = getManagedBean().getScPrInquiry();
			     prInquiry.setDateToResponse(new Timestamp(System.currentTimeMillis()));
			     prInquiry.setPrInquiryNo("123");
			     prInquiry.setCreatedDate(DateUtils.getCurrentDate());
			     // setting remarks 
			     prInquiry.setRemarks(initializeRemarks(prInquiry));
			     // WorkFlow Status
			     List<ScPRIWorkFlowStatus> wflowStatus =  new  ArrayList<ScPRIWorkFlowStatus>();
			     ScPRIWorkFlowStatus workFlowStatus = new ScPRIWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			     workFlowStatus.setPrInquiry(prInquiry);
			     wflowStatus.add(workFlowStatus);
			     prInquiry.setWorkflowStatuses(wflowStatus);
			     
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
		
	  /**
	   *  initializeRemarks  method for initializing remarks  in Purchase inquiry
      */	
		private List<ScPRIRemark> initializeRemarks(ScPrInquiry prInquiry) {
			List<ScPRIRemark> results = new ArrayList<ScPRIRemark>();
			FacesScopes facesScopes = new FacesScopes();
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
				} else {
					remark.setRemark("");
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
	   *  to get Division drop down Listing in Purchase inquiry
	   */
		private void populateUserDivisionList() {
			debugLogger("populateUserMinistryList");
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
				System.out.println("userDivisionList" + userDivisionList.size());
				getManagedBean().setUserDivisionList(userDivisionList);
			}
		}
			
	   /**
	     *  Page navigation to Catalogue main page in Purchase inquiry
		*/
		public String backToDirectPurchase() {
			debugLogger("backToSearchCatalogue");
			return NAV_TO_DIRECT_PURCHASE_PAGE;
		}
			
		private void debugLogger(Object message) {
			if (log.isDebugEnabled()) {
				log.debug(message);
			}
			log.info(message);
		}
		
		public String getCurrentStatus(){
			String result = "";
			if (getManagedBean().getScPrInquiry().getWorkflowStatuses() != null) {
			for(ScPRIWorkFlowStatus status : getManagedBean().getScPrInquiry().getWorkflowStatuses()){
				if(status.isCurrent()){
					result = getStatusDescById(status.getPmStatus().getStatusId());
					break;
				}
			}
			}
			return result;
		}
}