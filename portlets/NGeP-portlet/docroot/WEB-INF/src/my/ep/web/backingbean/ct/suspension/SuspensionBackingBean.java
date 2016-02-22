/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspension;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.suspension.IContractSuspensionSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Suspension Details
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "suspensionBackingBean")
@RequestScoped
public class SuspensionBackingBean extends ContractBaseBackingBean<SuspensionManagedBean> {
	
	    private ContractBackingBean ctBB = null;
	    private static Logger log = Logger.getLogger(SuspensionBackingBean.class);
	    private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
		
	    /**
		 * This constructor method used to initialize the variable on start up
		 * Setting button if user not call from BPM
		 * Method used to initialize approver list
		 * Method used to check view able mode
		 */
	      public SuspensionBackingBean() {
	    	   
	    	  if(ctBB == null){
	    		  ctBB = new ContractBackingBean();
	    	  }
	    	    /*Initialization of button*/
	    	   if(getManagedBean().getOutComes() == null) 
	    		      getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		
	    	
	    	   if(getManagedBean().getUserMap().size()==0) {
				      initializeApproverList();
		        }
	    	
	    	   HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
	           HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
	    	
	           if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
	    		    getManagedBean().setViewOnly(true);

	    }
	   
	    /**
		 * This  method used to initialize approver list
		 */
	     private void initializeApproverList() {
			if(getManagedBean().getApproverModel() == null) {
				 getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
			}
		}
	    
	  	/**
		 * This  method call once user call suspension page from task list
		 * Method used to retrieve all information from BPM
		 * Method used to retrieve contract information from BPM
		 * Method return null if task id null
	 */
	   public void preRenderView(){
			
			       ContractReqDTO reqDTO = null;
		           HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
				   HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
				        
				   String taskId = originalServletRequest.getParameter(ContractConst.TASK_ID);
				   String contractVerId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);
				        
				   /*Start if statement to check login user*/
				    if(contractVerId!=null) {
				        	 reqDTO = new ContractReqDTO();
					         reqDTO.setContractVerId(Long.parseLong(contractVerId));
					         reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
					         reqDTO.setUpdateAction(false);
					         setSuspensionContractVer(reqDTO);
					        	   
					           /*Start if statement if login user not contract admin*/
					          if (!getFacesScopes().getUserSessionBean().getUserId().equals(getManagedBean().getContractVer().getContractAdmin().getUserId())) {
									        FacesContext fc = FacesContext.getCurrentInstance();
										fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"", MessageUtils.getMessage(MessageConst.ERROR_700116,
												MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
										fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
										return;
								   }/*end if statement*/
				        	  
				        	  getManagedBean().setViewOnly(true);
				        	  getManagedBean().setTaskId(taskId);
				        	  return;
				          }/*End if statement to check  login user */
			       
				          /*Return when not call from tasklist*/
				          if(taskId==null) 
				        	  return;
	
				          getManagedBean().setViewOnly(false);
				          CTContractSuspensionDataType dataType =  super.getBpmTaskDetails(CTContractSuspensionDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});		
				       
						  /*Retrieve contract id from BPM  */
						  ContractReqDTO ctReqDTO = new ContractReqDTO();
						  ctReqDTO.setUpdateAction(false);
						  ctReqDTO.setContractId(new Long(dataType.getContractId()));
						  ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
						  ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
						  setSuspensionContractVer(ctReqDTO);
						  /*End*/
	      }
	   
	   /**
		 * This  method used to set retrieve value from BPM or contract maintenance
	 */
	 public void setSuspensionContractVer(ContractReqDTO reqDTO){
		 
		    ctBB.renewSesionObject();
		    IContractSuspensionSO contractSuspensionSO= null;
		    ContractResDTO resDTO = null;
		     
		    try{
		    	      contractSuspensionSO = (IContractSuspensionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSuspensionSO.class);
		    	      resDTO = contractSuspensionSO.retrieveSuspensionContractVerDtls(reqDTO);
		              getManagedBean().setContractVer(resDTO.getCtContractVer());	
		              getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			         getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					     ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE, getManagedBean().getCtContract().getContractNo()));
				     ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	  
			
			         //Retrieve agreement contract version for physical no & signed date
			         retrieveContractVerOfAgreement();
		
			         /*If statement  for check contact item start*/
			         if(getManagedBean().getContractVer().getCtContractItems() != null && getManagedBean().getContractVer().getCtContractItems().size() > 0 ) {
				             Collections.sort(getManagedBean().getContractVer().getCtContractItems(), new CtContractItem());
				 
			 	             for (CtContractItem contractItem : getManagedBean().getContractVer().getCtContractItems()) {
			 		          /*Get latest contract item and map with the sc request item*/
			 		               for (CtContractItem latestContractItem : getManagedBean().getCtContract().getLatestContractVer().getCtContractItems()) {
			 			               if (contractItem.getScRequestItem().getRequestNoteItemId().equals(latestContractItem.getScRequestItem().getRequestNoteItemId())) {
			 				                  getManagedBean().getItemMap().put(contractItem.getScRequestItem().getRequestNoteItemId(), latestContractItem);
			 				                  break;
			 			                }
			 		             }
		 		           }
			          }/*If statement for contract item end*/			
		    
			          /*If statement  to check agency start*/
			         if(getManagedBean().getContractVer().getCtAgencies() != null && getManagedBean().getContractVer().getCtAgencies().size() > 0 )  {
			 	            //Collections.sort(getManagedBean().getContractVer().getCtAgencies(), new CtAgency());			 	
				            sortAgencyList(getManagedBean().getContractVer().getCtAgencies());
			 	            for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			 		             /*Get latest agency and map with the org profile*/
			 		              for (CtAgency latestAgency : getManagedBean().getCtContract().getLatestContractVer().getCtAgencies()) {
			 			              if(ctAgency.getOrgProfile().getOrgProfileId().equals(latestAgency.getOrgProfile().getOrgProfileId())) {
			 				              getManagedBean().getAgencyMap().put(ctAgency.getOrgProfile().getOrgProfileId(), latestAgency);
			 				              break;
			 			              }
			 		             }			 		
			 	           }
		              }/*If statement for agency end*/    
		    
			         if(getManagedBean().getUserMap().size()==0) {
					      initializeApproverList();
			          }
			         /*Setting to list of approver start */
		             getManagedBean().getApproverModel().setSource(
					     getAvailableApproverList(getManagedBean().getCtContract().getContractType().getParameterCode(),
							getManagedBean().getUserMap()));
		             if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			              getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			              for (PmUser approverUser : resDTO.getApproverList())  {
					            Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
					            while (sourceIt.hasNext()) {
						            PmUser user = sourceIt.next();
						            if (user.getUserId() == approverUser.getUserId()) {
							            sourceIt.remove();
						            }
					           }
				         }
		    	    }/*End*/
		    
		            /*Setting to list of remark and attachement of document*/
		            getManagedBean().setRemarkList(resDTO.getCtRemarks());
		            getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		            ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
		            ctBB.getManagedBean().setContractVer(getManagedBean().getContractVer());
			        ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			         /*End remark setting*/
		             if (isContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
		    	           if (getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
		    		          ctBB.getManagedBean().getContractVer().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				           }
		    	       ctBB.getManagedBean().setGstCheckingGUI(true);
		           } else {
		    	      ctBB.getManagedBean().setGstCheckingGUI(false);
		           }
	          }catch (Exception e) {
				    e.printStackTrace();
				    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		   	} finally {
		   		contractSuspensionSO = null;
				resDTO = null;
			}
	    }
	
	 /**
	 * This  method used for back button
	 * @return the string
	 * @throws PortalException the portal exception
	 * @throws SystemException the system exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	 public String backAction() throws PortalException, SystemException, IOException {
		 if (!getManagedBean().isViewOnly()) {
			return CONTRACT_MAINT_PAGE;
		 } else { // view suspension from notification
			String commonUrl = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL);
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(commonUrl + "notifications");
			return null;
		 }
	 }
	 
	 /**
	  * The method used for retrieving the contract version 
	  * for agreement or non-agreement contract.
	  * If it's original agreement, return the first contract version.
	  * If it's supplementary agreement, return the amended contract version.
	  */
	 private void retrieveContractVerOfAgreement() {
			 IContractSO contractSO = null;
			 IContractAmendmentSO amendmentSO = null;
			 ContractResDTO resDTO = null;
			 ContractAmendmentResDTO amendmentResDTO = null;
			 try {
				 		contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				 			ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				 		amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				 			ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
				 		ContractReqDTO reqDTO = new ContractReqDTO();
				 		ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();
			
				 		if (getManagedBean().getContractVer().getCtContractAmount().isAgreement()) {
				 			//Retrieve amendment
				 			amendmentReqDTO.setContractId(getManagedBean().getCtContract().getContractId());
				 			amendmentReqDTO.setAmendmentEffectiveDate(getManagedBean().getContractVer().getCtContractAmount().getEffDate());
				 			amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);
				
				 			if (amendmentResDTO.getCtAmendment() != null) {
				 				ctBB.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				 			} else {
				 				reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
				 				reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);
					
				 				resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
				 				ctBB.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				 			}				
				 	} else {
				 		ctBB.getManagedBean().setAgreementContractVer(getManagedBean().getContractVer());
				 	}
		 } catch(Exception e) {
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		 } finally {
			 contractSO = null;
			 amendmentSO = null;
			 amendmentResDTO = null;
			 resDTO = null;
		 }
	 }
	 
	 /**
		 * This  method used to set contract item status value for suspension
		 * Status set to zero is suspension of item
	 */
	 public void setSuspensionStatusValue(CtContractItem item) {
		 Date currentDate = DateUtils.getCurrentDateWithoutTime();
		 for(CtContractItem ctItem :this.getManagedBean().getContractVer().getCtContractItems()) {
			 if(ctItem.equals(item)) {		
				 CtContractItem latestVerItem = getManagedBean().getItemMap().get(ctItem.getScRequestItem().getRequestNoteItemId());
				 Date effDate = null;
				 Date expDate = null;
				 
				 if(item.getRecordStatusFlg().intValue() == 1) {					 
					 effDate = (latestVerItem.getRecordStatusFlg() == 0) ? currentDate : latestVerItem.getEffDate();
					 expDate = (latestVerItem.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestVerItem.getExpDate();
					 item.setEffDate(effDate);
					 item.setExpDate(expDate);					
			     } else {
					 effDate = (latestVerItem.getRecordStatusFlg() == 0) ? latestVerItem.getEffDate() : currentDate;
					 expDate = (latestVerItem.getRecordStatusFlg() == 0) ?  latestVerItem.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
					 item.setEffDate(effDate);
					 item.setExpDate(expDate);		
			     }
			 }
		 }
	 }
	 
	 /**
		 * This  method used to set agency status  for suspension
		 * Status set to 0 is suspension of item
	 */
	 public void setSuspensionAgencyStatusValue(CtAgency agency){
		 Date currentDate = DateUtils.getCurrentDateWithoutTime();
		 for(CtAgency ctAgency  : getManagedBean().getContractVer().getCtAgencies()) {
			 if(ctAgency.equals(agency)) {
				 CtAgency latestAgency = getManagedBean().getAgencyMap().get(agency.getOrgProfile().getOrgProfileId());
				 Date effDate = null;
				 Date expDate = null;
				 
				 if(ctAgency.getRecordStatusFlg().intValue() == 1) {
					 effDate = (latestAgency.getRecordStatusFlg() == 0) ? currentDate : latestAgency.getEffDate();
					 expDate = (latestAgency.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestAgency.getExpDate();
					 ctAgency.setEffDate(effDate);
					 ctAgency.setExpDate(expDate);					
			     } else {
			    	 effDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getEffDate() : currentDate;
					 expDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
					 ctAgency.setEffDate(effDate);
					 ctAgency.setExpDate(expDate);		
			     }
			 }
		 }  
	 }
	 
	 /**
		 * This method used to suspend item and agency
		 * This method call validate function to validate suspension details
		 *  This method will navigate to acknowledgment page with
		 *   a confirmation message once submit successfully
		 */
	 public String saveContractSuspension(String outcome) throws Exception{
		  
		 IContractSuspensionSO contractSuspensionSO = null;
		 ContractReqDTO reqDTO = null;
		 ContractResDTO resDTO = null;
		 try{
			     validateSuspensionDetails(outcome);		
			     contractSuspensionSO = (IContractSuspensionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSuspensionSO.class);
		       
				 Object token = getFacesScopes().getBPMAuthenticationCtxToken();
				 reqDTO = new ContractReqDTO();
		         reqDTO.setToken(token);
		         reqDTO.setOutcome(outcome);
		         reqDTO.setTaskId(getManagedBean().getTaskId());
		         this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
		             
		         reqDTO.setCtContract(getManagedBean().getContractVer().getCtContract());
		         reqDTO.setCtContractVer(getManagedBean().getContractVer());
		         reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		         reqDTO.setUserRole(getUserRole());
	             reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			     reqDTO.setAttachments(getManagedBean().getAttachmentList());
			     reqDTO.setUserMap(getManagedBean().getUserMap());
		        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		        reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			        
				 if(this.getManagedBean().getPmDigiSign() !=null){
					reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
				 }
			    resDTO = contractSuspensionSO.saveContractSuspensionDetails(reqDTO);
			     if(resDTO.isSuccessful()) {
					 log.info("Record saved successfully");
					 String messageId = MessageConst.INFO_700005;
					 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					     messageId = MessageConst.INFO_700025;
				    } else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						 messageId = MessageConst.INFO_700009;
					 } else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					 } else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						messageId = MessageConst.INFO_700008;
					 } else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
						messageId = MessageConst.INFO_700010;
					 }						
					 FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				      if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
						   renewSesionObject();
						   return CommonUIConst.ACKNOWLEDGE_PAGE;
					  }
				      else {	
				    	  ContractReqDTO ctReqDTO = new ContractReqDTO();
				    	  ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				    	  ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
				    	  setSuspensionContractVer(ctReqDTO);
				      }
		   	     } else {
					throw new EpException(MessageConst.ERROR_700020);
				 }
			  } finally {
				  contractSuspensionSO = null;
				  reqDTO = null;
			      resDTO = null;
			  }
		   return null;
	 } 
	 
	 /**
		 * This method used to validate suspend item and agency as per rule
	 */
	 public void validateSuspensionDetails(String outcome){
				 getManagedBean().setAutoUpdate(true);
				 
				  /*Remark is mandatory during revise and reject*/
				  if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					 CtRemark remark = getManagedBean().getRemarkList().get(0);
					 if(remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")){
						  throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.txt.remarks", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())}); 
					 }
			      }
				 
				  /*Approver is mandatory for document submission. Please select at least one approver. */
				 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getApproverModel().getTarget().size()==0) {
					  throw new EpException(MessageConst.ERROR_750010); 
			   	 }
	   }
		
	 /**
		 * Method to set user role as per user login
		 * User role used in BPM to identify the task
		 */
	private String getUserRole() {
		String userRole = null;		
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FS_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FS_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FS_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FS_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		}
		
		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if(filePath!=null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1){ 
				out.write( buffer, 0, bytesRead );
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	 /**
		 * Method to used auto update of contract
	*/
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	 
	 /**
	 * Method to retrieve portal name from property file
    */
	 public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.suspension", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
	 
	 /**
		 * Method used to set value for security check
	    */
	public String preSubmitContractSuspension(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContractSuspension(getManagedBean().getButtonAction());
	}
		
	 /**
	 * Method used to set update value false for disable purpose
    */
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}
	
	 /**
	 * Method used to check login user as central or ministry admin
    */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
	
	 /**
		 * Method used to check navigation of page for approver list
		 * Return list of user for contract admin 
	    */
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FS_HT_NAME)))
			return true;
		else 
			return false;
	}
	
	/**
	 * Method to authenticate user during submit of suspension
	 * Navigate confirmation dialog for authenticate of login user
	 * Call save suspension method once authentication done
    */
	public String gpkiSignAction(String outcome) throws Exception{
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
			validateSuspensionDetails(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(getManagedBean().getContractVer().getDocType());
			sb.append(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			sb.append(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			sb.append(getManagedBean().getContractVer().getCtContractAmount().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		} else {
			return saveContractSuspension(outcome);
		}
	}
}
