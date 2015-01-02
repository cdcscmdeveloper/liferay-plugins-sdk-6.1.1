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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;

import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
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
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Supplier
 *  * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean(name = "supplierBackingBean")
@ViewScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	private String editorValue;
	private ContractBackingBean ctBB = null;
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);
	
	public SupplierBackingBean(){
		if(getManagedBean().getAction() == null) 
			getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SUBMIT });

		if(getManagedBean().getUserMap().size()==0) {
			initializeApproverList();
		}
		if(getManagedBean().getFactoring() == null) 
			initializeFactoring();
		
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
        HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
    	if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
    		getManagedBean().setViewOnly(true);
	}
	
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
		  	 List<String> source = new ArrayList<String>();
			 List<String> target = new ArrayList<String>();
			 getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	private void initializeFactoring() {
		getManagedBean().setFactoring(new CtFactoring());
		PmStatus pmStatus = new PmStatus();
		pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
		getManagedBean().getFactoring().setStatus(pmStatus);
		getManagedBean().getFactoring().setCtContract(new CtContract());
		getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
		getManagedBean().getFactoring().setPmFinancialBranch(new PmFinancialBranch());
		getManagedBean().setAccountNo(null);
		getManagedBean().setFactoringBankName(null);
		getManagedBean().setFinancialBankBranch(null);
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(null, getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
	}
	
	 private FacesScopes getFacesScopes(){
		  return new FacesScopes();
	 }
	
	 public void preRenderChangeBankAccount() {
	    	renewSesionObject();
	    	FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "changeBankAccount.xhtml?faces-redirect=true");
	  }
	
	  public void preRenderRequestFactoring() {
		    renewSesionObject();
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "requestOfFactoring.xhtml?faces-redirect=true");
	  }
    
	  public void preRenderView() {
	   
		    IUserSO userSO = null;
		    IContractSO contractSO = null;
		    ContractResDTO resDTO = null;
		    ContractReqDTO ctReqDTO = null;
		    UserReqDTO userReqDTO = null;
		    UserResDTO userResDTO = null;
		    ContractSupplierResDTO csResDTO = null;
		    IContractSupplierSO contractSupplierSO = null;
		    
		    try {
			
		    	System.out.println("**********preRenderView**************");
			    HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			    HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			    String taskId = originalServletRequest.getParameter("taskId");
			    String factoringId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);
			    
			    if(factoringId!=null) {
			    	ContractSupplierReqDTO csReqDTO = new ContractSupplierReqDTO();
		     	 	csReqDTO.setFactoringId(Long.parseLong(factoringId));
		     	 	contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
		     	 	csResDTO = contractSupplierSO.retrieveFactoringRequest(csReqDTO);
			           
			         //Restrict If login user not the contract admin
		     	 	if (!getFacesScopes().getUserSessionBean().getUserId().equals(csResDTO.getFactoring().getLoa().getContractAdmin().getUserId())) {
						   FacesContext fc = FacesContext.getCurrentInstance();
							fc.addMessage(null, new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"", MessageUtils.getMessage(MessageConst.ERROR_700116,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
							return;
					   }
		     	 	
			           getManagedBean().setViewOnly(true);
			           setFactoringField(csResDTO);
			           return;
	        	}
	        	  
			    if(taskId == null) 
				return;
			    
			    BpmRequestDTO reqDTO = new BpmRequestDTO();
			    reqDTO.setCtxToken(getFacesScopes().getBPMAuthenticationCtxToken());
			    reqDTO.setTaskId(taskId);
			    getManagedBean().setTaskId(taskId);

			    String docType = null;
			    IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			    BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			    Object obj = res.getPojo();
		        getManagedBean().setAction(res.getAction());
			    getManagedBean().setTaskName(res.getTaskName());
			    getManagedBean().setAutoUpdate(true);
			    
			    userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,	IUserSO.class);
			     
			    if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)) {
				      
			    	  ctReqDTO = new ContractReqDTO();
			    	  CTBankAccountDataType dataType = (CTBankAccountDataType) obj;
				      ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				      docType = ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT;
				      ctReqDTO.setDocType(docType);
				      getManagedBean().setContractVerId(ctReqDTO.getContractVerId());
				       
				       userReqDTO = new UserReqDTO();
				       userReqDTO.setUserId(new Long(dataType.getSupplier().getUserId()));
				       userResDTO = userSO.retrieveUserByUserId(userReqDTO);
				       getManagedBean().setSupplierPmUser(userResDTO.getPmUser());
				      
				       contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
					   resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
				       getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
				       getManagedBean().setContractVer(resDTO.getCtContractVer());
				       
				       getManagedBean().setRemarkList(resDTO.getCtRemarks());
					   getManagedBean().setAttachmentList(resDTO.getCtAttachments());
					
				       if(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo()!=null) {
				    	     String account = getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
					         if (getManagedBean().getContractVer().getCtContractValue().getBankName() != null)
					               account = account + " / " + getManagedBean().getContractVer().getCtContractValue().getBankName();
					         getManagedBean().setAccountNoAndName(account);
				       }
				       
				       getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath
					    	(ModuleCodeConst.CONTRACT_MANAGEMENT, docType, getManagedBean().getCtContract().getContractNo()));
					     
					   ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
					   ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
				
			    } else if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)) {			            
			    	       ContractSupplierReqDTO csReqDTO = new ContractSupplierReqDTO();
			    	       CTFactoringDataType dataType = (CTFactoringDataType) obj;
			     	       csReqDTO.setFactoringId(new Long(dataType.getDocId()));
			     	       contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
				           csResDTO = contractSupplierSO.retrieveFactoringRequest(csReqDTO);
				           setFactoringField(csResDTO);	
			    }
		    } finally {
		    	contractSupplierSO = null;
			    contractSO = null;
			    resDTO = null;
			    csResDTO = null;
			    userReqDTO = null;
			    userResDTO = null;
		   }
	 }
		    
    private void setFactoringField(ContractSupplierResDTO csResDTO) {
    	getManagedBean().setFactoring(csResDTO.getFactoring());
        getManagedBean().setCtContract(csResDTO.getFactoring().getCtContract());
        getManagedBean().setRemarkList(csResDTO.getCtRemarks());
        getManagedBean().setAttachmentList(csResDTO.getCtAttachments());

        getManagedBean().setLoaDate(getManagedBean().getCtContract().getLoa().getAckDate());
        getManagedBean().setContractNo(getManagedBean().getCtContract().getContractNo());
        getManagedBean().setContractName(getManagedBean().getCtContract().getContractName());
        getManagedBean().setEffDate(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getEffDate());
        getManagedBean().setExpDate(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getExpDate());
        reset();
        if (getManagedBean().getFactoring().getPmFinancialBranch() == null) 
	             getManagedBean().getFactoring().setPmFinancialBranch(new PmFinancialBranch());
        else 
	           resetBranch();
      
        if(getManagedBean().getFactoring().getRecordStatusFlg()==DbValueConst.RECORD_STATUS_SUSPENDED) 
	           getManagedBean().setRemoveFactoring(true);
      
        getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		   if(csResDTO.getApproverList() != null && csResDTO.getApproverList().size() != 0) {
			      getManagedBean().getApproverModel().setTarget(csResDTO.getApproverList());
			      getManagedBean().getApproverModel().getSource().removeAll(csResDTO.getApproverList());
		   } 
		   
		   getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath
		    		  (ModuleCodeConst.CONTRACT_MANAGEMENT, ParameterConst.DOCTYPE_REQUEST_FACTORING, getManagedBean().getCtContract().getContractNo()));
		   ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		   ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
		   ctBB.initializeRemarkAndAttachement(getManagedBean().getFactoring().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
    }
    
	public SelectItem[] getPopulateContractList() {
		
		      if(getManagedBean().getPopulateContractList() == null) {
		    	   
		    	     IContractSupplierSO supplierSO = null;
	     	         ContractSupplierReqDTO reqDTO = null;
	     	         ContractSupplierResDTO resDTO = null;
	     	         SelectItem[] items = null;
	     	         
	     	        ISupplierSearchSO supplierSearchSO = null;
		            SupplierSearchReqDTO supplierReqDTO = null;
		            SupplierSearchResDTO supplierResDTO = null;
		     
		            try {
		       
		            	        supplierReqDTO = new SupplierSearchReqDTO();
		         		        supplierReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		         		        supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
                                supplierResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
                                if(supplierResDTO.getResultBean() != null && supplierResDTO.getResultBean().getResultList() != null 
                                		&& !supplierResDTO.getResultBean().getResultList().isEmpty()) { 
		         		        List<Long> supplierIdList = new ArrayList<Long>();
		         		        for(SmSupplier supplier : supplierResDTO.getResultBean().getResultList()) {
		         		              	supplierIdList.add(supplier.getSupplierId());
		         		        }
		         		        
		                    int i =1;
		        	        reqDTO = new ContractSupplierReqDTO();
		        	        reqDTO.setSupplierIds(supplierIdList);
		        	        supplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
				            resDTO = supplierSO.retrieveSupplierContract(reqDTO);
				            List<CtContract> list = resDTO.getContractList();
				            items = new SelectItem[list.size() + 1];
				            items[0] = new SelectItem(null, "- Select One -");
				            for (CtContract obj : list) {
				          	     items[i++] = new SelectItem(obj.getCurrentContractVer().getContractVerId(), obj.getContractNo());
				        	} 
                           }getManagedBean().setPopulateContractList(items);
		             } finally {
		         	supplierSO = null;
		         	reqDTO = null;
		 		    resDTO = null;
		 		    supplierSearchSO = null;
		            supplierReqDTO = null;
		            supplierResDTO = null;
		         }
		     } 
		     return getManagedBean().getPopulateContractList();
	    }

	public void changeContractActionListener() {
		
		    IContractSO contractSO = null;
		    ContractReqDTO reqDTO  = null;
		    ContractResDTO resDTO   = null;
		    try {
			 
		    	    if (getManagedBean().getContractVerId() != null) {
				            contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				     
				            reqDTO = new ContractReqDTO();
				            reqDTO.setContractVerId(getManagedBean().getContractVerId());
			 	            reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
				            reqDTO.setUpdateAction(true);
				            
				            resDTO = contractSO.retrieveContractVerDtls(reqDTO);
				            if (resDTO.isSuccessful()) {
					               getManagedBean().setContractVer(resDTO.getCtContractVer());
					               getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
					               if(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo()!=null) {
					            	    getManagedBean().setOriginalAccountNo(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
						                getPopulateBankAccountList();
					               }
				           }
			        } else {
				
			        	       CtContractVer ctContractVer = new CtContractVer();
				               CtContractValue ctContractValue = new CtContractValue();
				               ctContractVer.setCtContractValue(ctContractValue);
				
				               getManagedBean().setContractVer(ctContractVer);
				               getManagedBean().setCtContract(null);
				               getManagedBean().setOriginalAccountNo(null);
			     }
		     } finally {
		       	contractSO = null;
		        reqDTO  = null;
			    resDTO   = null;
		}
	}
    
	  private void setBankInfo() {
	
		      getManagedBean().getContractVer().getCtContractValue().setBankName(null);
		     getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
		     getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
		     
		     SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
		     if(supplierBank!=null) {
			     getManagedBean().getContractVer().getCtContractValue().setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
			      getManagedBean().getContractVer().getCtContractValue().setBankBranch(supplierBank.getBankBranch());
			      getManagedBean().getContractVer().getCtContractValue().setBankAddress(supplierBank.getSmAddress());
		     }
	   }
	 
	   public List<SelectItem> getPopulateBankAccountList() {
		    
		       List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		       if (getManagedBean().getCtContract() != null) {
			        
		    	   if (getManagedBean().getCtContract().getSmSupplier() != null && getManagedBean().getCtContract().getSmSupplier().getSmAppl() != null
		    			 &&  getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks() != null
				          && getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				             for(SmSupplierBank obj : getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()) {
				            	
				            	 if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
				            		 
				            		 String account = obj.getAccountNo();
							           
						              if(obj.getPmFinancialOrg() != null && obj.getPmFinancialOrg().getFinOrgName() != null)
							               account = account + " / " + obj.getPmFinancialOrg().getFinOrgName();
						                
						              populateBankAccountList.add(new SelectItem(obj.getAccountNo(), account));
								       
						              if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getAccountNo())) 
							                 getManagedBean().getSupplierBankAccountMap().put(obj.getAccountNo(), obj);
						          }
				           }
			        }
		     } return populateBankAccountList;
	  }
	 
	public void changeAccountActionListener() {
		setBankInfo();
	}
     
	public String saveChangeBankRequest(String action) throws Exception {
	
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		
		try {
			
			    validateChangeBankRequest(action);
                reqDTO = new ContractReqDTO();
			    reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			    reqDTO.setOutcome(action);
			    reqDTO.setTaskId(this.getManagedBean().getTaskId());
			    reqDTO.setUserRole(getUserRole());
			    if(getSupplierUserRole()) {
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
			  if(getManagedBean().getPmDigiSign() !=null) {
			      reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			  }    
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractSO.class);
			resDTO = contractSO.saveContractVer(reqDTO);
		
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700043;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} 
			} else {
				throw new EpException("Failed.");
			}
		} finally {	
			contractSO = null;
		}
		return null;
	}
     
	
	public void validateChangeBankRequest(String action){
		getManagedBean().setAutoUpdate(true);
		if (getManagedBean().getOriginalAccountNo()!=null && getManagedBean().getOriginalAccountNo().equalsIgnoreCase(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo())) {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_750000);
		}
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
			 CtRemark remark = getManagedBean().getRemarkList().get(0);
			 if(remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")){
				 RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				 throw new EpException(MessageConst.ERROR_100022, new String[]{"Remarks"});
			 }
	    }
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(getManagedBean().getContractVer().getDocType());
			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
			sb.append(getManagedBean().getContractVer().getCtContractValue().getContractAmount());
			sb.append(action);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(action);
		}	
		
	}
	
	public boolean getSupplierUserRole(){
	    if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return true;		
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return true;		
		else
			return false;
	}
	
	private String getUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()))
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
			return RoleCode.BPK_CCA.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return RoleCode.MOF_SUPPLIER_ADMIN.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();		
		else
			return null;
	}
	
	public boolean isApproverRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return true;
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return true;
		else 
			return false;
	}
      
	private void getContractAdminlist(){
		 IUserSO userSO = null;
		 UserResDTO  userResDTO = null;
		 UserReqDTO userReqDTO = null;
		 try{
			    getManagedBean().setContractAdminlist(new ArrayList<PmUser>());
		        if(!getManagedBean().getCtContract().getLoa().getContractAdmin().isRecordStatus()) {
	    	         userReqDTO = new UserReqDTO();
	    	         userReqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
	    	         userReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
	    	         userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,	IUserSO.class);
	    	         userResDTO = userSO.retrieveUserListByRole(userReqDTO);
	 		         getManagedBean().setContractAdminlist(userResDTO.getUserList());
	 	       } else {
	 	    	    getManagedBean().getContractAdminlist().add(getManagedBean().getCtContract().getLoa().getContractAdmin());
               }
		} finally {}	
	}
	
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
				if(getSupplierUserRole()) {
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
			 
				if(getManagedBean().getPmDigiSign() !=null) {
					   reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
				}   
				
				supplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
			    ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
			  
			    if (resDTO.isSuccessful()) {
			    	log.info("Record saved successfully");
				    String messageId = MessageConst.INFO_700043;
				    if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					     messageId = MessageConst.INFO_700011;
				    } else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
					    messageId = MessageConst.INFO_700045;
				    }
				    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO,	null,
					MessageUtils.getMessage(messageId,new String[] {getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_REQUEST_FACTORING),
											this.getManagedBean().getFactoring().getLoa().getLoaNo() },
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    
				    if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			supplierSO = null;		
		}
		return null;
	}
	
	public void validateFactoringRequest(String action){
		getManagedBean().setAutoUpdate(true);
		if(getManagedBean().getFactoring().getFileName() == null || getManagedBean().getFactoring().getFileName().isEmpty()) {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_100022,	new String[] { "Copy Of Factoring Agreement" });
		}
	
		if(getManagedBean().isRemoveFactoring()){
		    if(getManagedBean().getFactoring().getRemoveFileName() == null || getManagedBean().getFactoring().getRemoveFileName().isEmpty()) {
		    	RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			    throw new EpException(MessageConst.ERROR_100022,	new String[] { "Copy Of Remove Factoring" });
		  }
		}
		
		if(!getManagedBean().isAgreeFactoringClause()) {
			if(!isApproverRole() && !getManagedBean().isRemoveFactoring()) {
				RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				throw new EpException(MessageConst.ERROR_750001);
			}
		}
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) && getManagedBean().getApproverModel().getTarget().size()==0) {
			RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)){
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
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
	
	public void flush(){
		this.getManagedBean().setFinancialBankBranch(null);
		this.getManagedBean().setAccountNo(null);
		this.getManagedBean().setFactoringBankName(null);
	}
    
	public SelectItem[] retrieveFinancialOrgList() throws NamingException {
		
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
			            items[0] = new SelectItem(null, "- Select One -");
			            for (PmFinancialOrg pmFinancialOrg : list) {
				             items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(),	pmFinancialOrg.getFinOrgName());
				             getManagedBean().getFactoringCompMap().put(pmFinancialOrg.getFinancialOrgId(),	pmFinancialOrg.getFinOrgName());
			           } getManagedBean().setListOfFinancialOrg(items);
		        } finally {
			            financialOrgSO = null;
			            reqDTO = null;
			            resDTO = null;
		        }
		   }   
		return getManagedBean().getListOfFinancialOrg();
	}

   public void reset(){ 
	 
	   SelectItem[] items = null;
	   IFinancialOrgSO financialOrgSO = null;
	   FinancialOrgReqDTO reqDTO = null;
	   FinancialOrgResDTO resDTO = null;
	   flush();
		try {
			int i = 1;
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
			reqDTO = new FinancialOrgReqDTO();
			if (getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null) {
				 
				    reqDTO.setFinancialOrgId(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
				    resDTO = financialOrgSO.retriveFinancialBranchByFinancialOrgId(reqDTO);
			    
				    if(resDTO.getFinancialBranchList() != null && !resDTO.getFinancialBranchList().isEmpty()){
					         List<PmFinancialBranch> list = resDTO.getFinancialBranchList();
					         items = new SelectItem[list.size() + 1];
					         items[0] = new SelectItem(null, "- Select One -");
					         for (PmFinancialBranch pmFinancialBranch : list) {
						          items[i++] = new SelectItem(pmFinancialBranch.getFinancialBranchId(), pmFinancialBranch.getBranchName());
				             } getManagedBean().setFinancialBankBranch(items);
				   }else{
					         resDTO = financialOrgSO.retriveFinAccDtlByFinOrgId(reqDTO);
					         getManagedBean().setAccountNo(resDTO.getPmFinancialAcc().getAccountNo());
					         getManagedBean().setFactoringBankName(resDTO.getPmFinancialAcc().getPmPaymentBankOrg().getFinOrgName());
					         getManagedBean().setFactoringBankAddress(getFullAddress(resDTO.getPmAddressType().getPmAddress()));
			       }
		    }
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		 finally{
			financialOrgSO = null;
		 }
   }
   
   public void resetBranch(){
	   IFinancialOrgSO financialOrgSO;
	   try {
		   if(getManagedBean().getFactoring().getPmFinancialBranch().getFinancialBranchId() != null) {
			   financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
			   FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			   reqDTO.setFinancialBranchId(getManagedBean().getFactoring().getPmFinancialBranch().getFinancialBranchId());
			   FinancialOrgResDTO resDTO = financialOrgSO.retriveFinAccDtlByBranchId(reqDTO);
			   getManagedBean().setAccountNo(resDTO.getPmFinancialAcc().getAccountNo());
			   getManagedBean().setFactoringBankName(resDTO.getPmFinancialAcc().getPmPaymentBankOrg().getFinOrgName());
			   getManagedBean().setFactoringBankAddress(getFullAddress(resDTO.getPmAddressType().getPmAddress()));
		   }
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }finally{
		   financialOrgSO = null;
	   }
    }
   
	public void changeLoaActionListener() {
	
		    IContractSO contractSO = null;
		    ContractReqDTO reqDTO = null;
		    ContractResDTO resDTO = null;
	    	try {
			
	    		    if (getManagedBean().getLoaId() != null) {
				           reqDTO = new ContractReqDTO();
				           contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				           reqDTO.setLoaId(getManagedBean().getLoaId());
				         
				           resDTO = contractSO.retrieveFactoringDetailsByLoaId(reqDTO);
			          	   if(resDTO.isSuccessful()) {
			          		      getManagedBean().setCtContract(resDTO.getContract());
					               if(getManagedBean().getCtContract().getCtFactoring() != null &&  getManagedBean().getCtContract().isFactored() &&
					            		   getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue() ) {
						                   getManagedBean().setFactoring(resDTO.getContract().getCtFactoring());
						                   getManagedBean().getFactoring().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						                   getManagedBean().setRemoveFactoring(true);
						                   PmStatus pmStatus = new PmStatus();
						                   pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
						                   getManagedBean().getFactoring().setStatus(pmStatus);
					          	            reset();
					              	        if(getManagedBean().getFactoring().getPmFinancialBranch() == null) 
							                     getManagedBean().getFactoring().setPmFinancialBranch(new PmFinancialBranch());
						                   else 
							                    resetBranch();
					                  }  else {
					                     getManagedBean().setRemoveFactoring(false);
					                     initializeFactoring();
				              }
		                  }
	    		    }
	    	 }	catch(Exception ex){
	    			 ex.printStackTrace();
	    		}
		    finally {
			     contractSO = null;
		   }
	 }
    
	public void removeFileUpload(FileUploadEvent event) throws Exception{
	
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				ParameterConst.DOCTYPE_REQUEST_FACTORING, getManagedBean().getCtContract().getContractNo());
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();    	
	    getManagedBean().getFactoring().setRemoveFileName(item.getFileName());
	    getManagedBean().getFactoring().setRemoveFilePath(filePath);
	}
	
    public void fileUpload(FileUploadEvent event) throws Exception {
    	UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				ParameterConst.DOCTYPE_REQUEST_FACTORING, getManagedBean().getCtContract().getContractNo());
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();    	
	    getManagedBean().getFactoring().setFileName(item.getFileName());
	    getManagedBean().getFactoring().setFilePath(filePath);
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
				out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public SelectItem[]  getContractSupplierLoa() {
		
		       if(getManagedBean().getContractSupplierLoa() == null) {
		               SelectItem[] items = null;
		               IContractSupplierSO contractSupplierSO = null;
		               ContractSupplierResDTO contractSupplierResDTO = null;
		               ContractSupplierReqDTO contractSupplierReqDTO = null;
		               ISupplierSearchSO supplierSearchSO = null;
		               SupplierSearchReqDTO supplierReqDTO = null;
		               SupplierSearchResDTO supplierResDTO = null;
		     
		               try {
		       
		            	        supplierReqDTO = new SupplierSearchReqDTO();
		         		        supplierReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		         		        supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
                                supplierResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
                                if(supplierResDTO.getResultBean() != null && supplierResDTO.getResultBean().getResultList() != null 
                                		&& !supplierResDTO.getResultBean().getResultList().isEmpty()) { 
		         		
		         		        List<Long> supplierIdList = new ArrayList<Long>();
		         		        for(SmSupplier supplier : supplierResDTO.getResultBean().getResultList()) {
		         		              	supplierIdList.add(supplier.getSupplierId());
		         		        }
		         		        
		                          contractSupplierReqDTO = new ContractSupplierReqDTO();
		                          contractSupplierReqDTO.setSupplierIds(supplierIdList);
			                      contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
		                          contractSupplierResDTO = contractSupplierSO.retrieveSupplierLoaWithContract(contractSupplierReqDTO);
		                          List<CtContract> list = contractSupplierResDTO.getContractList();
		                          List<CtContract> contractList = new ArrayList<CtContract>();
			                       for (CtContract contractLoa : list) {  
			                    	   ContractSupplierResDTO ctSupplierResDTO = contractSupplierSO.retrieveFactoringByLoaId(contractLoa.getLoa().getLoaId());
			                    	   if(ctSupplierResDTO.getFactoring() != null && ctSupplierResDTO.getFactoring().getStatus() != null &&
			                    			   ctSupplierResDTO.getFactoring().getStatus().getStatusId().longValue() !=  ContractStatusConst.RF_COMPLETE.longValue()) {
			                   		         continue;
			                    	    } contractList.add(contractLoa);
			                    	} 
			                       items = new SelectItem[contractList.size()+1];
				                    items[0] = new SelectItem(null, "- Select One -");
				                    int i = 1;
				                    for (CtContract ctLoalist : contractList) {  
	                         	       items[i++] = new SelectItem(ctLoalist.getLoa().getLoaId(), ctLoalist.getLoa().getLoaNo());
				                    }  
                                }
		                      getManagedBean().setContractSupplierLoa(items);
		               } finally {
		                    contractSupplierResDTO = null;
		                    contractSupplierSO = null;
		                    contractSupplierReqDTO = null;
		                    supplierSearchSO = null;
				            supplierReqDTO = null;
				            supplierResDTO = null;
				       }
		         }  
		         return getManagedBean().getContractSupplierLoa();
	   }
	
	public String getFactoringCompany() {
		if(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null) {
			return " " + getManagedBean().getFactoringCompMap().get(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
		} else {
			return " Factoring Company Name";
		}
	}
	
	public boolean isLetterGenerated() {
		if ((getManagedBean().getLoaId() != null || (getManagedBean().getCtContract() != null && getManagedBean().getCtContract().getLoa().getLoaNo() != null)) && getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId()  != null && 
				getManagedBean().getAccountNo() != null && getManagedBean().getFactoring().getAuthRefNo() != null && 
				getManagedBean().getFactoring().getEffDate() != null && getManagedBean().getFactoring().getExpDate() != null)
			return true;
		else
			return false;
	}
	
	public void previewAction() {		  
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		String templateName = null;
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
		
		try {			
			context = new VelocityContext();	
			if(getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				vmFile = "/ct/factoring/factoring-clause-supplier.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER;
			} else if(getUserRole().equalsIgnoreCase(RoleCode.MIN_CONTRACT_ADMIN.value()) || getUserRole().equalsIgnoreCase(RoleCode.BPK_CCA.value())
					|| getUserRole().equalsIgnoreCase(RoleCode.CT_APPROVER.value()) || getUserRole().equalsIgnoreCase(RoleCode.BPK_APPROVER.value())) {
				vmFile = "/ct/factoring/factoring-clause-ca.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_CA;	
			}			
	
			setEditorValue("");
			if(vmFile !=null && templateName != null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);	
				
				String imgUrl = getServerUrl()+ "css/images/eperolehan.png";
				context.put("imgUrl", imgUrl);
				if (getManagedBean().getFactoring().getCreatedBy() == null) {
					context.put("supplierOfficerName", getFacesScopes().getUserSessionBean().getUserName()); 
				}					
				else {
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getManagedBean().getFactoring().getCreatedBy());
					context.put("supplierOfficerName", userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser().getUserName()); 
				}
				context.put("supplierAddress", AddressUtils.getFullAddress(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierAddress()));
				context.put("telNo", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierPhoneNo());
				context.put("faxNo", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierFaxNo());				
				context.put("refNo", getManagedBean().getFactoring().getAuthRefNo());
				context.put("currentDate", df.format(new Date()));
				context.put("ptjName",  getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
				if(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile()==null) {
					context.put("jabatanMinistry",  getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
					}
					else {
						context.put("jabatanMinistry", getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName());
					}
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
				if(addressResDTO.getAddressTypeList().size()>0) {
					context.put("ptjAddress", AddressUtils.getFullAddress(addressResDTO.getAddressTypeList().get(0).getPmAddress()));
				}
				
				context.put("contractTitle", getManagedBean().getCtContract().getContractName());
				if(getManagedBean().getCtContract().getCurrentContractVer().getContractPhysicalNo()==null) {
					context.put("contractPhysicalNo",getManagedBean().getCtContract().getContractNo());
				} else {
					context.put("contractPhysicalNo",getManagedBean().getCtContract().getCurrentContractVer().getContractPhysicalNo());
				}
				context.put("factoringStartDate", df.format(getManagedBean().getFactoring().getEffDate()));
				context.put("factoringCompName", getFactoringCompany());
				context.put("factoringBankAddress", getManagedBean().getFactoringBankAddress());
				context.put("factoringAccountNo", getManagedBean().getAccountNo());
				context.put("supplierName", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierName());
				
				if(ContractConst.TEMPLATE_FACTORING_CLAUSE_CA.equalsIgnoreCase(templateName)) {
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
					
					context.put("contractAdminName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("contractAdminDesignation", userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser().getDesignation());
				}
				
				if (vmFile != null && context != null) {
					content = DocumentGenerator.getInstance().getHtmlFromVelocity(
							vmFile, context);
				}				
				setEditorValue(content);
				
			}
		}  catch (Exception e) {
			throw new EpException(e.toString());
		} 
	}

	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.request.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		if(getManagedBean().isRemoveFactoring()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.remove.factoring", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	  public boolean isContractAdmin() {
			return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())||getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
		}

	  private List<String> getAvailableApproverList() {
			 IUserSO userSO = null;
			 List<String> sourceList = new ArrayList<String>();
		
			 try {
				    userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				    UserReqDTO reqDTO = new UserReqDTO();
				    reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				
				    UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
				    if (resDTO.getUserOrgList().size() > 0)
				    	reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
				
				    if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) 
					     reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
				    else // Central Contract	
				    	 reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
				    
	                resDTO = userSO.retrieveUserListByRole(reqDTO);
				    List<PmUser> list = resDTO.getUserList();
				
				    for (PmUser user : list) {
					    sourceList.add(user.getUserName());
					    getManagedBean().getUserMap().put(user.getUserName(), user);
				    }
		   	  } catch (Exception e) {
			     	e.printStackTrace();
			    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			  } finally {
				  userSO = null;
			  }
			 return sourceList;
		}
	  
	  public String getMinistryAndPtjName(int temp){
		  if(getManagedBean().getCtContract() != null && getManagedBean().getCtContract().getOwnerOrgProfile() != null){
			  if( temp == 1 && getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile() != null){
				  return getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName();
			  } else if( temp ==2){
				    return getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName();
			 }
		 }
        return " ";
	  }
	  
	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
	public Date getCurrentDate() {
        return new Date();
    }
    
    public String preSubmitChangeBankRequest(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveChangeBankRequest(getManagedBean().getButtonAction());
	}
	
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}
	
    public String preSubmitFactoringRequest(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveFactoringRequest(getManagedBean().getButtonAction());
	}
    
    private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance().getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";

		return url;
	}
}
