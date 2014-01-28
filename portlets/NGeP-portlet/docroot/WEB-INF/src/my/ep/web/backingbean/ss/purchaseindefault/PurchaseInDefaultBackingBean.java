package my.ep.web.backingbean.ss.purchaseindefault;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ss.directpurchase.IPurchaseInDefaultSO;
import my.ep.constant.ss.SourcingConstants;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ss.directpurchase.PurchaseInDefaultReqDTO;
import my.ep.dto.ss.directpurchase.PurchaseInDefaultResDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScPIDApprovalUser;
import my.ep.persistence.entity.ss.directpurchase.ScPIDRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPIDWorkFlowStatus;
import my.ep.persistence.entity.ss.directpurchase.ScPurchaseInDefault;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.PurchaseInDefaultManagedBean;
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
@ManagedBean(name = "purchaseInDefaultBackingBean")
@RequestScoped
public class PurchaseInDefaultBackingBean extends BaseBackingBean<PurchaseInDefaultManagedBean> {
	private static Logger log = Logger.getLogger(PurchaseInDefaultBackingBean.class);
	private static final String NAV_TO_PURCHASE_IN_DEFAULT_PAGE = "purchaseInDefaultMain";
	
	public PurchaseInDefaultBackingBean() {
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
	  * pricingDetails for getting pricing details in PID
	  */
		public void pricingDetails(VCmSupplierItem vCmSupplierItem){
			debugLogger("pricingDetails");
			IContractItemSO contractItemSO	=	(IContractItemSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
			ContractReqDTO ConIreqDTO = new ContractReqDTO();
			ConIreqDTO.setContractItemId(vCmSupplierItem.getDocId());
			ContractResDTO ConIresDTO = contractItemSO.getContractItem(ConIreqDTO);
		    this.getManagedBean().setCtContractItem(ConIresDTO.getCtContractItem());
		    
			IContractSO contractSO	=	(IContractSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(1l);
			ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);
			this.getManagedBean().setContract(resDTO.getContract());
			
			ICatalogueSO catalogueSO	=	(ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
			CatalogueReqDTO catReqDTO = new CatalogueReqDTO();
			catReqDTO.setDocId(vCmSupplierItem.getDocId());
			CatalogueResDTO catResDTO = catalogueSO.searchSupplierItemByStatus(catReqDTO);
			if (catResDTO.getSupplierItemList() !=null) {
			   ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
			   CmPricing cmPricing = new CmPricing();
			   cmPricing.setCmSupplierItem(catResDTO.getSupplierItemList().get(0));
			   requestSupplierItem.setPricing(cmPricing);
			   requestSupplierItem.setSmSupplier(cmPricing.getCmSupplierItem().getSmSupplier());
			   getManagedBean().setScRequestSupplierItem(requestSupplierItem);
			   // initializing remarks
			   ScPurchaseInDefault scPurchaseInDefault = new ScPurchaseInDefault(); 
			   scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
			   // WorkFlow Status
			   List<ScPIDWorkFlowStatus> wflowStatus =  new  ArrayList<ScPIDWorkFlowStatus>();
			   ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus(SourcingConstants.PRI_PENDING_SUBMISSION);
			   workFlowStatus.setCurrent(true);
			   workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
			   wflowStatus.add(workFlowStatus);
			   scPurchaseInDefault.setWorkFlowStatuses(wflowStatus);
		       getManagedBean().setPurchaseInDefault(scPurchaseInDefault);
			
			   FacesContext fc = FacesContext.getCurrentInstance();
		       fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_PURCHASE_IN_DEFAULT_PAGE);
		   }
		}	
		 
		 /**
		  *  submitPurchaseInDefault  method to submit PID details in Purchase In Default
		  */	
		  public String submitPurchaseInDefault() {
			  debugLogger("submitPurchaseInDefault");
			  IPurchaseInDefaultSO purchaseInDefaultSO = null;
			    try {
			    	purchaseInDefaultSO = (IPurchaseInDefaultSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IPurchaseInDefaultSO.class);
			    	PurchaseInDefaultReqDTO reqDTO = new PurchaseInDefaultReqDTO();
					
					ScPurchaseInDefault scPurchaseInDefault =  new ScPurchaseInDefault();
					List<ScPIDWorkFlowStatus> wflowStatuses =  new  ArrayList<ScPIDWorkFlowStatus>();
		 		    scPurchaseInDefault.setPurchaseInDefaultNo("123");
		 		    scPurchaseInDefault.setReasonType("R");
		 		    scPurchaseInDefault.setRecordStatus(true);
		 		    scPurchaseInDefault.setCreatedDate(DateUtils.getCurrentDate());
				    // Target Approver
				    ScPIDApprovalUser approvalUser = new ScPIDApprovalUser();
				    scPurchaseInDefault.setApprovalUsers(createTargetApprover(approvalUser));
				    // Contract Item
				    scPurchaseInDefault.setContractedItem(getManagedBean().getCtContractItem());
				    // WorkFlow Status 
				    ScPIDWorkFlowStatus workFlowStatus = new ScPIDWorkFlowStatus(SourcingConstants.PID_PENDING_SUBMISSION);
					workFlowStatus.setPurchaseInDefault(scPurchaseInDefault);
					wflowStatuses.add(workFlowStatus);
					scPurchaseInDefault.setWorkFlowStatuses(wflowStatuses);
					 // remarks
				    scPurchaseInDefault.setRemarks(initializeRemarks(scPurchaseInDefault));
					reqDTO.setPurchaseInDefaultDtl(scPurchaseInDefault);
					PurchaseInDefaultResDTO resDTO = purchaseInDefaultSO.submitPurchaseInDefault(reqDTO);
					
					if (resDTO.isSuccessful()) {
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
					purchaseInDefaultSO = null;
				}
				return null;
			}
			
		/**
		  * initializing remarks in Purchase In Default
		 */
		private List<ScPIDRemark> initializeRemarks(ScPurchaseInDefault scPurchaseInDefault) {
			List<ScPIDRemark> results = new ArrayList<ScPIDRemark>();
			FacesScopes facesScopes = new FacesScopes();
			if (results.size() == 0
					|| !compareUserName(results.get(0).getUser().getLoginId())) {
				ScPIDRemark remark = new ScPIDRemark();
				remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
				remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				remark.setChangedBy(remark.getCreatedBy());
				remark.setChangedDate(remark.getCreatedDate());
				remark.setUser(facesScopes.getUserSessionBean());
				if (getManagedBean().getPurchaseInDefault().getRemarks() != null && getManagedBean().getPurchaseInDefault().getRemarks().size()>0) {
				  remark.setRemark(getManagedBean().getPurchaseInDefault().getRemarks().get(0).getRemark());
				} else {
					remark.setRemark("");
				}
				PmStatus status = new PmStatus();
				status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
				remark.setPmStatus(status);
				remark.setPurchaseInDefault(scPurchaseInDefault);
				results.add(0, remark);
			}
			return results;
		}
		
		/**
		  * To get Approver Pick List  in Purchase In Default
		 */
		private	List<ScPIDApprovalUser> createTargetApprover(ScPIDApprovalUser scPIDApprovalUser){
			List<ScPIDApprovalUser> results = new ArrayList<ScPIDApprovalUser>();
			 scPIDApprovalUser = new ScPIDApprovalUser();
			 for (PmUser pmUser :  getManagedBean().getApprovalUsers().getTarget()) {
				  scPIDApprovalUser.setPmUser(pmUser);
			      results.add(scPIDApprovalUser);
			}
			return results;
		}
	  
	    public String  getCurrentStatus() {
			String result = "";
			if (getManagedBean().getPurchaseInDefault().getWorkFlowStatuses() != null) {
				for (ScPIDWorkFlowStatus status : getManagedBean()
						.getPurchaseInDefault().getWorkFlowStatuses()) {
					if (status.isCurrent()) {
						result = getStatusDescById(status.getPmStatus()
								.getStatusId());
						break;
					}
				}
			}
			return result;
		}
	  
		private void debugLogger(Object message) {
			if (log.isDebugEnabled()) {
				log.debug(message);
			}
			log.info(message);
		}
}