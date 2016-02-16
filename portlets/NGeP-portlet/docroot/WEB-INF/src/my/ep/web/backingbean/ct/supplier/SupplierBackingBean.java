/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.supplier;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.bank.IChangeBankAccSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Supplier
 *  * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean(name = "supplierBackingBean")
@ViewScoped
public class SupplierBackingBean extends ContractBaseBackingBean<SupplierManagedBean> {	

	private static Logger log = Logger.getLogger(SupplierBackingBean.class);
	private ContractBackingBean ctBB = null;
	private String editorValue;
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 * This method include additional checking for viewing factoring.
	 */
	public SupplierBackingBean(){
		/* Initialization of button */
		if (getManagedBean().getOutComes() == null) {
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SUBMIT});
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
		if (getManagedBean().getFactoring() == null) {
			initializeFactoring();
		}
		
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
        HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
    	if (originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null) {
    		getManagedBean().setViewOnly(true);
    	}
	}
	
	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
		  	 List<PmUser> source = new ArrayList<PmUser>();
			 List<PmUser> target = new ArrayList<PmUser>();
			 getManagedBean().setApproverModel(new DualListModel<PmUser>(source, target));
		}
	}	
	
	/**
	 * @return the ctBB
	 */
	public ContractBackingBean getCtBB() {
		return ctBB;
	}

	/**
	 * @param ctBB the ctBB to set
	 */
	public void setCtBB(ContractBackingBean ctBB) {
		this.ctBB = ctBB;
	}

	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	/**
	 * The method used to initialize factoring information.
	 */
	public void initializeFactoring() {
		getManagedBean().setFactoring(new CtFactoring());
		PmStatus pmStatus = new PmStatus();
		pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
		getManagedBean().getFactoring().setStatus(pmStatus);
		getManagedBean().getFactoring().setCtContract(new CtContract());
		getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
		getManagedBean().setAccBankName(null);
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(null, getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the Change Bank Account screen.
	 */
	public void preRenderChangeBankAccount() {
		renewSesionObject();
    	FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "changeBankAccount.xhtml?faces-redirect=true");
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the Request Factoring screen.
	 */
	public void preRenderRequestFactoring() {
		renewSesionObject();
	    FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "requestOfFactoring.xhtml?faces-redirect=true");
	}
	
	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {	   
	 
			ContractSupplierReqDTO reqDTO = null;
		    UserResDTO userResDTO = null;
		    IUserSO userSO = null;
		    
		    HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		    HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		    String taskId = originalServletRequest.getParameter(ContractConst.TASK_ID);
		    String factoringId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);
	    
	        if (factoringId != null) {
	    	      reqDTO = new ContractSupplierReqDTO();
	    	      reqDTO.setFactoringId(Long.parseLong(factoringId));
     	 	      setFactoringDetails(reqDTO);
     	 	
     	 	      //Restrict If login user not the contract admin
     	          if (!getFacesScopes().getUserSessionBean().getUserId().equals(getManagedBean().getFactoring().getLoa().getContractAdmin().getUserId())) {
				          FacesContext fc = FacesContext.getCurrentInstance();
				          fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
						  MessageUtils.getMessage(MessageConst.ERROR_700116,
								   MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				          fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
				        return;
     	 	        }
     	 	      getManagedBean().setViewOnly(true);
     	 	     return;
     	     }
	 
	          if (taskId == null) {
			         return;
	           }	
	     
	          Object payLoad = super.getBpmTaskDetails(Object.class, null);
		      getManagedBean().setAutoUpdate(true);		    
		   	  
		    // Change Bank Account
		    if (getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)) {

                        CTBankAccountDataType bankAccDataType= (CTBankAccountDataType) payLoad;
		    	        UserReqDTO userReqDTO = new UserReqDTO();
		    	        userReqDTO.setUserId(new Long(bankAccDataType.getSupplier().getUserId()));
		    	        userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,	IUserSO.class);
		    	        userResDTO = userSO.retrieveUserByUserId(userReqDTO);
				    	getManagedBean().setSupplierPmUser(userResDTO.getPmUser());
						      
		    	        ContractReqDTO ctReqDTO = new ContractReqDTO();
		    	        ctReqDTO.setContractVerId(new Long(bankAccDataType.getDocId()));
		    	        ctReqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
		    	        setChangeBankDetails(ctReqDTO);       

		     } else if (getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)) {	// Factoring Request		            
		    	     CTFactoringDataType dataType = (CTFactoringDataType) payLoad;
		    	     reqDTO = new ContractSupplierReqDTO();
		    	     reqDTO.setFactoringId(new Long(dataType.getDocId()));
		    	     setFactoringDetails(reqDTO);	
		    }
	}
	
	public void setChangeBankDetails(ContractReqDTO reqDTO) {
		
		   IChangeBankAccSO changeBankAccSO = null;
		   ContractResDTO resDTO = null;
		 
		    try{
			             changeBankAccSO = (IChangeBankAccSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IChangeBankAccSO.class);
				    	 resDTO = changeBankAccSO.retrieveContractVerForChangeAcc(reqDTO);
					    
				    	getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
				    	getManagedBean().setContractVer(resDTO.getCtContractVer());
				    	getManagedBean().setCurentAccNoBankName(null);
						
				    	if(getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankAccountNo() != null && getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankName() != null )
							 getManagedBean().setCurentAccNoBankName(getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankAccountNo() +ContractConst.SPACE_SLASH+ getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankName() );
				       
				    	getManagedBean().setRemarkList(resDTO.getCtRemarks());
				    	getManagedBean().setAttachmentList(resDTO.getCtAttachments());
				    	
				    	if (getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() != null) {
				    		String account = getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
				    		    if (getManagedBean().getContractVer().getCtContractValue().getBankName() != null) {
					                   account = account + ContractConst.SPACE_SLASH + getManagedBean().getContractVer().getCtContractValue().getBankName();
				    	 	     }
				    		  getManagedBean().setAccountNoAndName(account);
				    	 }
				    
				    	getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
				    			ModuleCodeConst.CONTRACT_MANAGEMENT, ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT, getManagedBean().getCtContract().getContractNo()));
				    	ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
				    	ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), 
				    			getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		   } catch (Exception e) {
			    e.printStackTrace();
			    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	   	   }finally{
	   		    resDTO = null;
	   		    changeBankAccSO = null;
		   }
	}
	/**
	 * The method used to set the factoring request details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for factoring request
	 */
    private void setFactoringDetails(ContractSupplierReqDTO reqDTO) {
    	   IContractSupplierSO contractSupplierSO = null;
    	   ContractSupplierResDTO resDTO = null;
    	   
    	   try {
	    	 	    contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
	    	 	    resDTO = contractSupplierSO.retrieveFactoringRequest(reqDTO);
	 	            getManagedBean().setFactoring(resDTO.getFactoring());
	                getManagedBean().setCtContract(resDTO.getFactoring().getCtContract());
	                getManagedBean().setRemarkList(resDTO.getCtRemarks());
	                getManagedBean().setAttachmentList(resDTO.getCtAttachments());
	                getManagedBean().setLoaDate(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getAckDate());
	                getManagedBean().setContractNo(getManagedBean().getCtContract().getContractNo());
	                getManagedBean().setContractName(getManagedBean().getCtContract().getContractName());
	                getManagedBean().setEffDate(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getEffDate());
	                getManagedBean().setExpDate(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getExpDate());
	                resetFactoringCompany();
        
                    // Remove Factoring Request
                    if (getManagedBean().getFactoring().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
        	              getManagedBean().setRemoveFactoring(true);
                    }
                    
                    if (getManagedBean().getUserMap().size() == 0) {
            			initializeApproverList();
            		}
            		if (getManagedBean().getFactoring() == null) {
            			initializeFactoring();
            		}
            		
                   getManagedBean().getApproverModel().setSource(
				   getAvailableApproverList(getManagedBean().getCtContract().getContractType().getParameterCode(),
						  getManagedBean().getUserMap()));
                 
                   if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
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
                     }
               
                   getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
        		          ModuleCodeConst.CONTRACT_MANAGEMENT, ParameterConst.DOCTYPE_REQUEST_FACTORING, getManagedBean().getCtContract().getContractNo()));
                   ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
                   ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
                   ctBB.initializeRemarkAndAttachement(getManagedBean().getFactoring().getCtWorkflowStatus(), 
        		       getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
              
    	   } catch (Exception e) {
			    e.printStackTrace();
			    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
    	   }finally{
    		   contractSupplierSO = null;
    		   resDTO = null;
    	     }
      }
	
	/**
	 * The method used to save the change bank request.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this change bank request
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public String saveChangeBankRequest(String action) throws Exception {
	//	IContractSO contractSO = null;
		IChangeBankAccSO changeBankAccSO =null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		try {
			validateChangeBankRequest(action);
            reqDTO = new ContractReqDTO();
		    reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
		    reqDTO.setOutcome(action);
		    reqDTO.setTaskId(getManagedBean().getTaskId());
		    reqDTO.setUserRole(getUserRole());
		    
		    if (isSupplierRole()) {
		    	getContractAdminlist(); 
			    reqDTO.setSupplierPmUser(getFacesScopes().getUserSessionBean());
			    reqDTO.setContractAdminlist(getManagedBean().getContractAdminlist()); 	
		    } else if (getManagedBean().getSupplierPmUser() != null) {
		    	reqDTO.setSupplierPmUser(getManagedBean().getSupplierPmUser());
		    }
		    
		    reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		    reqDTO.setCtContract(getManagedBean().getCtContract());
		    reqDTO.setCtContractVer(getManagedBean().getContractVer());
		    reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(getManagedBean().getAttachmentList());
			 
		    if (getManagedBean().getPmDigiSign() != null) {
		    	reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
		    }    
			
		    changeBankAccSO = (IChangeBankAccSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IChangeBankAccSO.class);
			resDTO = changeBankAccSO.saveChangeBankAccRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700043;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				}
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(messageId,
										new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
												this.getManagedBean().getContractVer().getDocType()), 
												getManagedBean().getContractVer().getCtContract().getContractNo()},
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} 
			} else {
				throw new EpException(ContractConst.FAILED);
			}
		} finally {	
			changeBankAccSO = null;
		}
		return null;
	}
     
	/**
	 * The method used for validating the change bank request information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this change bank request
	 *					The validation will invoke base on the action
	 */
	private void validateChangeBankRequest(String action){
		getManagedBean().setAutoUpdate(true);
		if (getManagedBean().getOriginalAccountNo() != null && getManagedBean().getOriginalAccountNo().equalsIgnoreCase(
				getManagedBean().getContractVer().getCtContractValue().getBankAccountNo())) {
			throw new EpException(MessageConst.ERROR_750000);
		}
		// Validation for valid contract admin for next task assignment
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isSupplierRole()) {
	    	getContractAdminlist();
	    	if (getManagedBean().getContractAdminlist() == null 
	    			|| getManagedBean().getContractAdminlist().isEmpty()) {
	    		throw new EpException(MessageConst.ERROR_750037);
	    	}
		}
		
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			 CtRemark remark = getManagedBean().getRemarkList().get(0);
			 if (remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")) {
				 throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			 }
	    }
	}
	
    /**
	 * The method used to save the factoring request
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this factoring request
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
    public String saveFactoringRequest(String action) throws Exception {
    	IContractSupplierSO supplierSO = null;
    	ContractSupplierReqDTO reqDTO = null;
    	
    	try {
    		validateFactoringRequest(action);
    		reqDTO = new ContractSupplierReqDTO();
		    reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
		    reqDTO.setOutcome(action);
		    reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
		
			if (isSupplierRole()) {
				getContractAdminlist();
				reqDTO.getContractAdminlist().addAll(getManagedBean().getContractAdminlist());
			}
			
			getManagedBean().getFactoring().setCtContract(getManagedBean().getCtContract());
		    getManagedBean().getFactoring().setSmSupplier(getManagedBean().getCtContract().getSmSupplier());
		    getManagedBean().getFactoring().setLoa(getManagedBean().getCtContract().getLoa());
		    
		    reqDTO.setFactoring(getManagedBean().getFactoring());
			reqDTO.setCtContract(getManagedBean().getFactoring().getCtContract());
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
		    reqDTO.setUserMap(getManagedBean().getUserMap());
	        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		    reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		    reqDTO.setRemoveFactoring(getManagedBean().isRemoveFactoring());
		    
		    if (getManagedBean().getPmDigiSign() != null) {
		    	reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
		    }
		    supplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
		    ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
		    
		    if (resDTO.isSuccessful()) {
		    	log.info("Record saved successfully");
		    	String messageId = MessageConst.INFO_700043;
		    	if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
		    		messageId = MessageConst.INFO_700011;
		    	} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
				    messageId = MessageConst.INFO_700045;
			    } else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				    messageId = MessageConst.INFO_700008;
			    }
		    	FacesContext.getCurrentInstance().addMessage(null,
		    			new FacesMessage(FacesMessage.SEVERITY_INFO, null,
		    					MessageUtils.getMessage(messageId,
		    							new String[] {getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES,
		    									ParameterConst.DOCTYPE_REQUEST_FACTORING), 
		    									this.getManagedBean().getFactoring().getLoa().getLoaNo() },
		    							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    
				    if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)
				    		|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) {
				    	renewSesionObject();
				    	return CommonUIConst.ACKNOWLEDGE_PAGE;
				    }
			} else {
				throw new EpException(ContractConst.FAILED);
			}
		} finally {
			supplierSO = null;		
		}
		return null;
	}
	
    /**
	 * The method used for validating the factoring request information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this factoring request
	 *					The validation will invoke base on the action
	 */
	private void validateFactoringRequest(String action) {
		getManagedBean().setAutoUpdate(true);		
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
			if (remark == null || remark.length() == 0) {
				RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		} else {		
			if (getManagedBean().getFactoring().getFileName() == null 
					|| getManagedBean().getFactoring().getFileName().isEmpty()) {
				RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.copy.factoring.agreement", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
	//		SIR#4106: Removed
	//		if (getManagedBean().getFactoring().getFactoringPayFileName() == null 
	//				|| getManagedBean().getFactoring().getFactoringPayFileName().isEmpty()) {
	//			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
	//			throw new EpException(MessageConst.ERROR_100022, 
	//					new String[]{ResourceBundleUtils.getMessage("lbl.ct.copy.factoring.form.fee", 
	//							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	//		}	
			if (getManagedBean().isRemoveFactoring()){
			    if (getManagedBean().getFactoring().getRemoveFileName() == null 
			    		|| getManagedBean().getFactoring().getRemoveFileName().isEmpty()) {
			    	RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			    	throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.copy.factoring.removal", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			  }
			}		
			if (!getManagedBean().isAgreeFactoringClause()) {
				if (!isApproverRole() && !getManagedBean().isRemoveFactoring()) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_750001);
				}
			}
			// Validation for valid contract admin for next task assignment
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isSupplierRole()) {
		    	getContractAdminlist();
		    	if (getManagedBean().getContractAdminlist() == null 
		    			|| getManagedBean().getContractAdminlist().isEmpty()) {
		    		throw new EpException(MessageConst.ERROR_750037);
		    	}
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) 
					&& getManagedBean().getApproverModel().getTarget().size() == 0) {
				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
				throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
			}
			
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
				StringBuilder sb = new StringBuilder();
				sb.append(getManagedBean().getContractNo());
				sb.append(ParameterConst.DOCTYPE_REQUEST_FACTORING);
				sb.append(df.format(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getEffDate()));
				sb.append(df.format(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getExpDate()));			
				sb.append(action);			
				getManagedBean().setTextBeforeSign(sb.toString());	
				getManagedBean().setButtonAction(action);
			}
		}
	}
	
	/**
	 * The method used to authenticate user during submit the change bank request.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save change bank request method once authentication success.
	 * 
	 * @param  outcome  the user action on this change bank request
	 * @return result of the saveChangeBankRequest process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String changeBankRequestSignAction(String outcome) throws Exception {
//    	SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
//    	StringBuilder sb = null;
//		 if (BpmOutcomeConst.BPM_ACKNOWLEDGE.equals(outcome) && !isSupplierRole()) {
//			validateChangeBankRequest(outcome);		
//			sb = new StringBuilder();
//			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
//			sb.append(getManagedBean().getContractVer().getDocType());
//			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
//			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
//			sb.append(getManagedBean().getContractVer().getCtContractValue().getContractAmount());
//			sb.append(outcome);			
//			getManagedBean().setTextBeforeSign(sb.toString());	
//			getManagedBean().setButtonAction(outcome);
//			getManagedBean().setAutoUpdate(Boolean.FALSE);
//			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
//			return null;
//		} else if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && isSupplierRole()) {
    	if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && isSupplierRole()) {
			validateChangeBankRequest(outcome);	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);			
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			return null;
		} else {
			return saveChangeBankRequest(outcome);
		}
	}
	
	/**
	 * The method used to set value for security check
	 * before save the change bank request.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveChangeBankRequest process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String preSubmitChangeBankRequest(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null && isSupplierRole()) {
			FacesScopes facesScope = new FacesScopes();
			Long userId = facesScope.getUserSessionBean().getUserId();
			Timestamp date = new Timestamp(System.currentTimeMillis());
			String originalText = prepareSupplierOriginalText();
			pmDigiSign.setCreatedBy(userId);
		    pmDigiSign.setCreatedDate(date);
		    pmDigiSign.setOriginalData(originalText);
		    pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText));
			getManagedBean().setPmDigiSign(pmDigiSign);			
		} else if(pmDigiSign != null) {
			getManagedBean().setPmDigiSign(pmDigiSign);		
		}
		return saveChangeBankRequest(getManagedBean().getButtonAction());
	}
	
	/**
	 * The method used to authenticate user during submit the factoring request.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save factoring request method once authentication success.
	 * 
	 * @param  outcome  the user action on this factoring request
	 * @return result of the saveFactoringRequest process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String factoringRequestSignAction(String outcome) throws Exception {
    	StringBuilder sb = null;
    	if (BpmOutcomeConst.BPM_ENDORSE.equals(outcome) && !isSupplierRole()) {
    		validateFactoringRequest(outcome);
			sb = new StringBuilder();
			sb.append(getManagedBean().getCtContract().getContractNo());
			sb.append(getManagedBean().getCtContract().getCurrentContractVer().getDocType());
			//sb.append(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && isSupplierRole()) {
			validateFactoringRequest(outcome);	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);			
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			return null;
		} else {
			// No GPKI for CA acknowledge
			if(BpmOutcomeConst.BPM_ACKNOWLEDGE.equals(outcome)) {
				validateFactoringRequest(outcome);
			}
 			return saveFactoringRequest(outcome);
		}
	}
	
	/**
	 * The method used to set value for security check
	 * before save the factoring request.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveFactoringRequest process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String preSubmitFactoringRequest(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null && isSupplierRole()) {
			FacesScopes facesScope = new FacesScopes();
			Long userId = facesScope.getUserSessionBean().getUserId();
			Timestamp date = new Timestamp(System.currentTimeMillis());
			String originalText = prepareSupplierOriginalText();
			pmDigiSign.setCreatedBy(userId);
		    pmDigiSign.setCreatedDate(date);
		    pmDigiSign.setOriginalData(originalText);
		    pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText));
			getManagedBean().setPmDigiSign(pmDigiSign);			
		} else if (pmDigiSign != null) {
			getManagedBean().setPmDigiSign(pmDigiSign);		
		}
		return saveFactoringRequest(getManagedBean().getButtonAction());
	}
	
	/**
	 * The method used to retrieve the supplier information for security check.
	 * 
	 * @return orginalText the supplier information
	 */
    private String prepareSupplierOriginalText() {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		SmSupplier smSupplier = null;
		String countryCode ="";
		String orginalText = "";

		if (getManagedBean().getCtContract().getSmSupplier() != null) {
			smSupplier = getManagedBean().getCtContract().getSmSupplier();
			if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null 
					&& smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size() > 0) {
				List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();

				for (SmCompanyAddress compaAddress : companyAddress) {
					if (compaAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) {
						countryCode = compaAddress.getSmAddress().getPmCountry().getCountryCode();
						break;
					}
				}
			}
		}		
		sb.append(getManagedBean().getCtContract().getContractNo()).append(ContractConst.SEPRATE_LINE)
			.append(facesScopes.getUserSessionBean().getIdentificationNo()).append(ContractConst.SEPRATE_LINE)
			.append(smSupplier.getRegNo()).append(ContractConst.SEPRATE_LINE).append(countryCode);
		orginalText  = sb.toString();
		return orginalText;
	}
	
	
	/**
	 * The method used for generating a list of available bank and account no
	 * for supplier as selection.
	 * 
	 * @return populateBankAccountList  the selection list
	 */
	public SelectItem[] getPopulateBankAccountList() {		
		if (getManagedBean().getPopulateBankAccountList() == null ) {
			SelectItem[] items = null;
			ISupplierSearchSO supplierSearchSO = null;
	        SupplierSearchReqDTO reqDTO = null;
	        SupplierSearchResDTO resDTO = null;
	        
	        try {
	        	if (getManagedBean().getCtContract() != null) {
	        		int i = 0;
	        		supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		            reqDTO = new SupplierSearchReqDTO();
		            resDTO = new SupplierSearchResDTO();
		            
		            reqDTO.setSupplierId(getManagedBean().getCtContract().getSmSupplier().getSupplierId());
		            if (getManagedBean().getContractVer().getCtContractValue().getSupplierBranchCode() != null) {
		            	reqDTO.setBranchCode(getManagedBean().getContractVer().getCtContractValue().getSupplierBranchCode());
		            	resDTO = supplierSearchSO.retrieveSupplierBranchBanksByBranchCode(reqDTO);
		            }		            
		            if (getManagedBean().getContractVer().getCtContractValue().getSupplierBranchCode() == null
		            		|| resDTO.getSmSupplierBankList().isEmpty()) {
		            	resDTO = supplierSearchSO.retrieveSmSupplierBankBySupplier(reqDTO);
		            }
		            
		            List<SmSupplierBank> list = resDTO.getSmSupplierBankList();
		            items = new SelectItem[list.size()];
		            
		            for (SmSupplierBank smSupplierBank : list) {
		            	String account = smSupplierBank.getAccountNo();
						if (smSupplierBank.getPmFinancialOrg() != null 
								&& smSupplierBank.getPmFinancialOrg().getFinOrgName() != null) {
							account = account + ContractConst.SPACE_SLASH + smSupplierBank.getPmFinancialOrg().getFinOrgName();
						}
						items[i++] = new SelectItem(smSupplierBank.getAccountNo(), account);
		            	if (!getManagedBean().getSupplierBankAccountMap().containsKey(smSupplierBank.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(smSupplierBank.getAccountNo(), smSupplierBank);
						}
		            	// Default the bank account no if only 1 bank account is available
		            	if (list.size() == 1) {
							getManagedBean().getContractVer().getCtContractValue().setBankAccountNo(smSupplierBank.getAccountNo());
							changeAccountActionListener();
						}
					}
		            getManagedBean().setPopulateBankAccountList(items);	        		
	        	}
	        } finally {
	        	supplierSearchSO = null;
	            reqDTO = null;
	            resDTO = null;
	        }
		}
		return getManagedBean().getPopulateBankAccountList();
	}
	
	/**
	 * The method used for generating a list of available factoring company
	 * for supplier as selection.
	 * 
	 * @return the selection list of factoring company
	 */
	public SelectItem[] retrieveFinancialOrgList() {
		if (getManagedBean().getListOfFinancialOrg() == null ) {
			SelectItem[] items = null;
	        IFinancialOrgSO financialOrgSO = null;
	        FinancialOrgReqDTO reqDTO = null;
	        FinancialOrgResDTO resDTO = null;
	        
	        try {
	        	int i = 1;
	            financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
	            reqDTO = new FinancialOrgReqDTO();
	            reqDTO.setServiceType(ParameterConst.BANK_SERVICES_FACTORING_COMPANY);
	            resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
	            
	            List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
	            items = new SelectItem[list.size() + 1];
	            items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
	            		"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
	            
	            for (PmFinancialOrg pmFinancialOrg : list) {
	            	items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(),	pmFinancialOrg.getFinOrgName());
	            	getManagedBean().getFactoringCompMap().put(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());
	            }
	            getManagedBean().setListOfFinancialOrg(items);
	        } finally {
	        	financialOrgSO = null;
	            reqDTO = null;
	            resDTO = null;
	        }
		}   
		return getManagedBean().getListOfFinancialOrg();
	}
	
	/**
	 * The method used to retrieve and display the bank and account information
	 * with the selected bank for change bank request.
	 */
	public void changeAccountActionListener() {
		getManagedBean().getContractVer().getCtContractValue().setBankName(null);
		getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
		getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
		
		SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
		if (supplierBank != null) {
			getManagedBean().getContractVer().getCtContractValue().setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
			getManagedBean().getContractVer().getCtContractValue().setBankBranch(supplierBank.getBankBranch());
			getManagedBean().getContractVer().getCtContractValue().setBankAddress(supplierBank.getSmAddress());
		}
	}	
	
	/**
	 * The method used to retrieve the factoring company name.
	 * 
	 * @return name of the factoring company
	 */
	public String getFactoringCompany() {
		if (getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null) {
			return " " + getManagedBean().getFactoringCompMap().get(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
		} else {
			return ContractConst.FACTORING_COMPANY_NAME;
		}
	}
	
    /**
	 * The method used to reset the factoring company name, bank account and bank address
	 * with the selected factoring company.
	 */
    public void resetFactoringCompany() {
    	IFinancialOrgSO financialOrgSO = null;
 	   	FinancialOrgReqDTO reqDTO = null;
 	   	FinancialOrgResDTO resDTO = null;
 	   	getManagedBean().setAccBankName(null);
 	   	getManagedBean().setFactoringBankAddress(null);
 	   
 		try {
 			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
 			reqDTO = new FinancialOrgReqDTO();
 			if (getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null) {
 				reqDTO.setFinancialOrgId(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
 				resDTO = financialOrgSO.retriveFinAccDtlByFinOrgId(reqDTO);
 				getManagedBean().setAccBankName(resDTO.getPmFinancialAcc().getAccountNo() + ContractConst.SPACE_SLASH + resDTO.getPmFinancialAcc().getPmPaymentBankOrg().getFinOrgName());
 				getManagedBean().setFactoringBankAddress(getFullAddress(resDTO.getPmAddressType().getPmAddress()));
 			}
 		} catch(Exception ex) {
 			ex.printStackTrace();
 		} finally {
 			financialOrgSO = null;
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
		 getManagedBean().setPopulateBankAccountList(null);
		 getManagedBean().setListOfFinancialOrg(null);
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
	 * The method used to set the upload index for
	 * file upload purpose.
	 * The index is used to differentiate what type of file has
	 * been uploaded.
	 * 
	 * @param index index of the uploaded file
	 */
    public void setUploadIndexValue(int index) {
    	getManagedBean().setIndex(index);
    }
    
	/**
	 * The method used to upload factoring file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
    public void fileUpload(FileUploadEvent event) throws Exception {
    	UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				ParameterConst.DOCTYPE_REQUEST_FACTORING, getManagedBean().getCtContract().getContractNo());
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + ContractConst.SPACE_SLASH + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();    	
	    
	    if (getManagedBean().getIndex() == 1) {
	    	getManagedBean().getFactoring().setFactoringPayFileName(item.getFileName());
	    	getManagedBean().getFactoring().setFactoringPayFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));	  
	    } else if(getManagedBean().getIndex() == 2) {
	    	getManagedBean().getFactoring().setFileName(item.getFileName());
	    	getManagedBean().getFactoring().setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));	  
	    } else if(getManagedBean().getIndex() == 3) {
	    	getManagedBean().getFactoring().setRemoveFileName(item.getFileName());
	  	    getManagedBean().getFactoring().setRemoveFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	    }
    }
    
    public void deleteFactoringAttachement(){
    	getManagedBean().getFactoring().setFileName(null);
  	    getManagedBean().getFactoring().setFilePath(null);
    }
    
    /**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
    public void downloadFile(String filePath, String fileName) throws IOException {
    	PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if (filePath != null) {
			filePath = DocumentRepositoryUtils.getBaseDir() + filePath;
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
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
    
    /**
	 * The method used to download the the Factoring Approval Form.
	 * 
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
    public void downloadFactoringApprovalForm() throws IOException {    	
    	String filePath =  EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.TEMPLATE_DIR) + 
    			ContractConst.DOCPATH_CT_FACTORING_APPROVAL_FORM;
    	String fileName =  ContractConst.TEMPLATE_FACTORING_APPROVAL_FORM + ".pdf";
    	downloadFile(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
    }    
    
    /**
	 * The method used to confirm the notification of factoring service is ready to view.
	 * 
	 * @return true, if factoring letter is ready to view
	 *         false, if factoring letter is not ready to view. User required to 
	 *         enter all necessary information
	 */
	public boolean isLetterGenerated() {
		if ((getManagedBean().getSelectedContract() != null 
				|| (getManagedBean().getCtContract() != null && getManagedBean().getCtContract().getLoa().getLoaNo() != null)) 
				&& getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null 
				&& getManagedBean().getFactoring().getAuthRefNo() != null 
				&& getManagedBean().getFactoring().getEffDate() != null 
				&& getManagedBean().getFactoring().getExpDate() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * The method used to generate the notification of factoring service and
	 * viewable from screen.
	 */
	public void previewAction() {		  
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		String templateName = null;
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		
		try {			
			context = new VelocityContext();	
			if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) 
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) 
					|| getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
				vmFile = "/ct/factoring/factoring-clause-supplier.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER;
			} else if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
					|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				vmFile = "/ct/factoring/factoring-clause-ca.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_CA;	
			}
	
			setEditorValue("");
			if (vmFile !=null && templateName != null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);	
				UserReqDTO userReqDTO = null;
				
				String imgUrl = getServerUrl()+ContractConst.IMAGE_PATH+ "eperolehan.png";
				context.put("imgUrl", imgUrl);
				if (getManagedBean().getFactoring().getCreatedBy() == null) {
					userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
					context.put("supplierOfficerName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("supplierDesignation", userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser().getDesignation());
				} else {
					userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getFactoring().getCreatedBy());
					context.put("supplierOfficerName", userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser().getUserName()); 
					context.put("supplierDesignation", userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser().getDesignation());
				}
				context.put("supplierAddress", AddressUtils.getFullAddress(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierAddress()).replaceAll("(\r\n|\n)", "<br/>"));
				context.put("telNo", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierPhoneNo());
				context.put("faxNo", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierFaxNo());				
				context.put("refNo", getManagedBean().getFactoring().getAuthRefNo());
				context.put("currentDate", df.format(DateUtils.getCurrentDateWithoutTime()));
				context.put("ptjName",  getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
				context.put("ministryName", retrieveMinistryByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId()));
				
			
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
				if (addressResDTO.getAddressTypeList().size() > 0) {
					context.put("ptjAddress", AddressUtils.getFullAddress(
							addressResDTO.getAddressTypeList().get(0).getPmAddress()).replaceAll("(\r\n|\n)", "<br/>"));
				}else{
					context.put("ptjAddress"," ");
				}
				
				context.put("contractTitle", getManagedBean().getCtContract().getContractName());
				if (getManagedBean().getCtContract().getCurrentContractVer().getContractPhysicalNo() == null) {
					context.put("contractPhysicalNo",getManagedBean().getCtContract().getContractNo());
				} else {
					context.put("contractPhysicalNo",getManagedBean().getCtContract().getCurrentContractVer().getContractPhysicalNo());
				}
				context.put("factoringStartDate", df.format(getManagedBean().getFactoring().getEffDate()));
				context.put("factoringCompName", getFactoringCompany());
				context.put("factoringBankAddress", getManagedBean().getFactoringBankAddress().replaceAll("(\r\n|\n)", "<br/>"));
				context.put("factoringAccountNo", getManagedBean().getAccBankName());
				context.put("supplierName", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierName());
				
				if (ContractConst.TEMPLATE_FACTORING_CLAUSE_CA.equalsIgnoreCase(templateName)) {
					PmUser user = null;
					for (PmUser approver : getManagedBean().getApproverModel().getTarget()) {
						if (getManagedBean().getUserMap()!=null && getManagedBean().getUserMap().get(approver.getUserId()) != null) {
							user = getManagedBean().getUserMap().get(approver.getUserId());
							break;
						}
					}
					if (user != null) {
						context.put("approverName", user.getUserName());
						context.put("approverDesignation", user.getDesignation());
					} else {
						context.put("approverName", "");
						context.put("approverDesignation", "");						
					}
				}
				
				if (vmFile != null && context != null) {
					content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
				}				
				setEditorValue(content);				
			}
		}  catch (Exception e) {
			throw new EpException(e.toString());
		} 
	}

	/**
	 * The method used to retrieve the base URL.
	 * 
	 * @return url  the base URL
	 */
    private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";

		return url;
	}
    
	/**
	 * The method used to retrieve proper portal name for factoring from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
    public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.request.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		if (getManagedBean().isRemoveFactoring()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.remove.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
    
    /**
	 * The method used to retrieve proper portal name for change bank request from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
    public String getChangeBankPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.request.change.bank.acc", 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		
		return portletTitle;
	}
    
    /**
	 * The method used to retrieve the contract administrator list.
	 * This method will get the all contract administrators with 
	 * contract owner organization profile when the loa's administrator 
	 * is invalid.
     */
    private void getContractAdminlist() {
		IUserSO userSO = null;
		UserResDTO userResDTO = null;
		UserReqDTO userReqDTO = null;
		
		try{
			if (getManagedBean().getContractAdminlist() == null) {
				getManagedBean().setContractAdminlist(new ArrayList<PmUser>());				
				if (getManagedBean().getCtContract().getLoa().getContractAdmin() == null
						|| !getManagedBean().getCtContract().getLoa().getContractAdmin().isRecordStatus()) {
					userReqDTO = new UserReqDTO();
					if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						userReqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
					} else {
						userReqDTO.setRoleCode(RoleCode.BPK_CCA.value());
					}
		    		userReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		    		userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
		    				ModuleCodeConst.PROFILE_MANAGEMENT,	IUserSO.class);
		    		userResDTO = userSO.retrieveUserListByRole(userReqDTO);
		    		getManagedBean().setContractAdminlist(userResDTO.getUserList());
				} else {
					getManagedBean().getContractAdminlist().add(getManagedBean().getCtContract().getLoa().getContractAdmin());
				}				
			}
		} finally {}	
	}
    
    /**
   	 * The method to used auto update of request.
   	 */
   	public void enableAutoUpdate() {
   		getManagedBean().setAutoUpdate(Boolean.TRUE);
   		RequestContext.getCurrentInstance().update("@form");
   	}
   	
   	/**
   	 * The method used to set update value false for disable purpose.
     */
   	public void disableUpdate() {
   		getManagedBean().setAutoUpdate(false);
   	}
    
    /**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {
		String userRole = null;		
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_CB_REQUEST_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_HT_NAME)) {
			if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				userRole = RoleCode.MOF_SUPPLIER_USER.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
				userRole = RoleCode.MOF_SUPPLIER_ADMIN.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
				userRole = RoleCode.BASIC_SUPPLIER_USER.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				userRole = RoleCode.BASIC_SUPPLIER_ADMIN.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
				userRole = RoleCode.GOVT_SELLER.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_CB_REQUEST_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FACTORING_REQUEST_HT_NAME))) {
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
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_FACTORING_REQUEST_HT_NAME)) {			
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
    
    /**
	 * The method used to verify the login user is supplier.
	 * 
	 * @return true, if login user is supplier
	 *         false, if login user is not supplier
     */
    public boolean isSupplierRole() {
		return (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				||getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())
				||getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value()));
	}
    
    /**
     * The method used to verify the login user is BPK approver 
     * or contract approver.
	 * 
	 * @return true, if login user is BPK approver or contract approver
	 *         false, if login user is not BPK approver or contract approver
     */
    public boolean isApproverRole() {
		return (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value()));
	}
    
    /**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
     */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
}
