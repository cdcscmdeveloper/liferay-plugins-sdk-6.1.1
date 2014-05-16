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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Supplier
 *  * 
 * @author Alam
 * @since 7/11/2013	 
 */
@ManagedBean(name = "supplierBackingBean")
@RequestScoped
public class SupplierBackingBean extends BaseBackingBean<SupplierManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);
	private List<SelectItem> populateContractList;
	private List<SelectItem> populateLoaList;
	private List<SelectItem> populateBankAccountList;
	private List<SmSupplierBank> smSupplierBank;
	private List<CtContract> dbContractList;
	ContractBackingBean ctBB = null;
	
	public SupplierBackingBean(){
	   if(this.getManagedBean().getAction() == null) 
	 	   this.getManagedBean().setAction(new String[]{"Save", "Submit"});
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void init(){
	    
		ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
		
		if(this.getManagedBean().getFactoring() == null){
		   this.getManagedBean().setFactoring(new CtFactoring());
		   if(this.getManagedBean().getFactoring().getFinancialOrg() == null){
		      this.getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
		      this.getManagedBean().getFactoring().getFinancialOrg().setFinancialOrgId(1L);
		   
		   }if(this.getManagedBean().getFactoring().getFinancialBranchId() == null)
			   this.getManagedBean().getFactoring().setFinancialBranchId(new PmFinancialBranch());
		}
	}
    
    public void preRenderView(){
    	
    	try{	
    		System.out.println("**********preRenderView**************");
     		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
    		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
    		String taskId = originalServletRequest.getParameter("taskId");
    		if(taskId == null)
    		return;
    
    		FacesScopes face = new FacesScopes();
    		String token =face.getBPMAuthenticationCtxToken();
    		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

    		ContractReqDTO ctReqDTO = new ContractReqDTO();
    		ctReqDTO.setUpdateAction(false);
    		
    		BpmRequestDTO reqDTO = new BpmRequestDTO();
    		reqDTO.setCtxToken(token);
    		reqDTO.setTaskId(taskId);
    		
    		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
    		Object obj = res.getPojo();
    		this.getManagedBean().setTaskId(taskId);
    		this.getManagedBean().setChkMenuOrTxtBox("BPM");
    		this.getManagedBean().setAction(res.getAction());
    		this.getManagedBean().setTaskName(res.getTaskName());
    		
    		if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)){
    			CTBankAccountDataType dataType = (CTBankAccountDataType)obj;
    			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    		
    		}else if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)){
    			CTFactoringDataType dataType = (CTFactoringDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
    		}

    		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
    		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
    		this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
    		this.getManagedBean().setContractVer(resDTO.getCtContractVer());

    		if(resDTO.getCtContractVer().getCtContract().getCtFactoring()!= null 
    				&& resDTO.getCtContractVer().getCtContract().getCtFactoring().getFactoringId() != null){
    			this.getManagedBean().setFactoring(resDTO.getCtContractVer().getCtContract().getCtFactoring());
    			this.getManagedBean().setRemoveFactoring(true);
    		}
    		if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_BANK_ACCOUNT);
    	
    		}else if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_FACTORING);
    		
    		}else{
    		
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "navigation");
    		}
          } catch(Exception e){
    			e.printStackTrace();
    		}

    	}
    
    /* Function to populate list of country */
	public List<SelectItem> getPopulateContractList() {

		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		
		try{
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
			 reqDTO.setSupplierUser(getFacesScopes().getUserSessionBean());
			 ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
		     
			 if(resDTO.getContractList()!= null && !resDTO.getContractList().isEmpty()) {
				populateContractList = new ArrayList<SelectItem>();
				dbContractList = resDTO.getContractList();
				for (CtContract obj : dbContractList) {
					populateContractList.add(new SelectItem(obj.getContractId(),obj.getContractNo()));
			    }      
		    }
		}finally {
			supplierSO = null;
		}
		return populateContractList;
	}
	
	/* Function to populate list of country */
	public List<SelectItem> getPopulateLoaList() {

		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			
			reqDTO = new ContractSupplierReqDTO();
			supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setUserLonginId(facesScopes.getUserSessionBean().getUserId());
			ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
		         
			if (resDTO.getContractList()!= null && !resDTO.getContractList().isEmpty()) {
				populateContractList = new ArrayList<SelectItem>();
				dbContractList = resDTO.getContractList();
				for (CtContract obj : dbContractList) {
					populateContractList.add(new SelectItem(obj.getLatestContractVer().getContractVerId(),obj.getContractNo()));
			    }      
		    }
		 }finally {}
		return populateLoaList;
	 }

	 /* Function to populate state value */
	 public void changeContractActionListener() {

		IContractSO contractSO = null;
        ContractReqDTO reqDTO = null;	
		try{
		
		   reqDTO = new ContractReqDTO();
		   contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
		  // reqDTO.setContractId(this.getManagedBean().getContractNo());
		   ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);
		   if(resDTO.isSuccessful()){
			  this.getManagedBean().setCtContract(resDTO.getContract());
			  this.getManagedBean().setContractVer(resDTO.getContract().getLatestContractVer()); 
			  changeAccountActionListener();
		   }
		 }catch (Exception ex) {
			log.error("Exception", ex);
		}finally {
			// resetObjectConditions();
		}
	 }/* Function End */
    
	 public void changeAccountActionListener(){
		
		IMofRegistrationSO iMofRegistrationSO = null;
    	MofRegistrationReqDTO reqDTO = null;	
		try{
		
			 reqDTO = new MofRegistrationReqDTO();
	    	 reqDTO.setSupplierId(this.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
	    	 iMofRegistrationSO = (IMofRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	    	 MofRegistrationResDTO resDTO = iMofRegistrationSO.retrieveSupplierProfile(reqDTO);
	    	 List<SmSupplierBank> smSupplierBanks = resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks();
	         if(smSupplierBanks != null && !smSupplierBanks.isEmpty()) {
		    	populateBankAccountList = new ArrayList<SelectItem>();
		    	for(SmSupplierBank obj : smSupplierBanks) {
					populateBankAccountList.add(new SelectItem(obj.getSupplierBankId(), obj.getAccountNo()));
				}      
		     }
		 }catch (Exception ex) {
			log.error("Exception", ex);
		 }finally {
			// resetObjectConditions();
		 }
	  }
	
	  public SelectItem[] getBankAccountList(){
		
		  SelectItem[] items = null;   
		  IMofRegistrationSO iMofRegistrationSO = null;
    	  MofRegistrationReqDTO reqDTO = null;
	      try{
	    	    int i = 1;
	    		reqDTO = new MofRegistrationReqDTO();
	    		reqDTO.setSupplierId(this.getManagedBean().getCtContract().getSmSupplier().getSupplierId());
	    		iMofRegistrationSO = (IMofRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	    		MofRegistrationResDTO resDTO = iMofRegistrationSO.retrieveSupplierProfile(reqDTO);
	    		List<SmSupplierBank> smSupplierBanks = resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks();
	    		items = new SelectItem[smSupplierBanks.size()+1];
			    items[0] = new SelectItem(null, "- Select One -");
			    for(SmSupplierBank bankList : smSupplierBanks) {
				     items[i++] = new SelectItem(bankList.getSupplierBankId(), bankList.getAccountNo());
			       }
			 }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     }
		return items;
	  }
     
	  public String saveChangeBankRequest(String action){
		
	    IContractSO contractSO = null;
		try{
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
            this.getManagedBean().getCtContract().getLatestContractVer().setDocType(ContractConst.DOC_TYPE_CHANGE_BANK_ACCOUNT);
            reqDTO.setCtContract(this.getManagedBean().getCtContract());
            reqDTO.setCtContractVer(this.getManagedBean().getCtContract().getLatestContractVer());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			
			 reqDTO.setSupplierPmUser(getFacesScopes().getUserSessionBean());
		    reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		    if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Change Bank Account. : "));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
				    renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			 }else{
			   throw new EpException("Failed.");
			 }
		  }catch (Exception e) {
			  log.error("Exception>>", e);
			  e.printStackTrace();
			  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		  } finally {
			  contractSO = null;
		  }
		return null;
	  }
     
      public String saveTerminationRequest(String outcome){
     	
     	 System.out.println("inside supplier termination Log >>>>>>>>>>>>>>>>>>");
     	 IContractSO contractSO = null;
      	 ContractReqDTO reqDTO = null;	
     	 ContractResDTO resDTO = null;
 		
     	 try{
 			
 			 reqDTO = new ContractReqDTO();
 			 contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
 			 String token = getFacesScopes().getBPMAuthenticationCtxToken();
 			 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
  			
 			 reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
			 reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
			 resDTO = contractSO.objCloneSupplierTerminationReq(reqDTO);
			 reqDTO.setCtContractVer(resDTO.getCtContractVer());
 		
 			 reqDTO.setToken(token);
			 reqDTO.setOutcome(outcome);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 reqDTO.setFromSupplier(true);
			 reqDTO.setSupplierPmUser(getFacesScopes().getUserSessionBean());
			 reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			 reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			 
			 resDTO = contractSO.saveContractVer(reqDTO);
 			 
			 if(resDTO.isSuccessful()) {
 					log.info("Record saved successfully");
 					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Contract Termination. : "));
					// save action with task ID not null will remain in the same page
					if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
						(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 	 						
 						renewSesionObject();
 						return "/xhtml/shared/acknowledge.xhtml";
 					}
 				} else {
 					throw new EpException("Failed.");
 				}
 				
 			} catch (Exception e) {
 				e.printStackTrace();
 				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Termination save failed."));
 			}finally{
 	 
 		   contractSO = null;
 		   reqDTO = null;
 		}
 		
 		return null;		
     }
	
    public String saveFactoringRequest(String action){
    	IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class); 
		   	 reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			
		   	 reqDTO.setOutcome(action);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 reqDTO.setSupplierUser(getFacesScopes().getUserSessionBean());
			 
			 this.getManagedBean().getFactoring().setCtContract(this.getManagedBean().getCtContract());
			 this.getManagedBean().getFactoring().setSmSupplier(this.getManagedBean().getCtContract().getSmSupplier());
			 this.getManagedBean().getFactoring().setLoa(this.getManagedBean().getCtContract().getLoa());
			 reqDTO.setFactoring(this.getManagedBean().getFactoring());
			 this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().setDocType(ParameterConst.DOCTYPE_REQUEST_FACTORING);
			 
			 reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			 reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			 ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
			  if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					String messageId = MessageConst.INFO_700005;
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700025;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						messageId = MessageConst.INFO_700009;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					}
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
						renewSesionObject();
						return CommonUIConst.ACKNOWLEDGE_PAGE;
					}
				  }else {
						throw new EpException("Failed.");
				  }
			  }catch (Exception e) {
				log.error("Exception>>", e);
				e.printStackTrace();
				if(e instanceof EpException) {
					EpException ep = (EpException) e;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ep.getMessage(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				}
			  }finally {
				  supplierSO = null;
			  }
		
		return null;
	}
        
    public void retrieveSupplierProfile(Long supplierId){
    	
    	IMofRegistrationSO iMofRegistrationSO = null;
    	MofRegistrationReqDTO reqDTO = null;
    	try{
    		reqDTO = new MofRegistrationReqDTO();
    		reqDTO.setSupplierId(supplierId);
    		iMofRegistrationSO = (IMofRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
    		MofRegistrationResDTO resDTO = iMofRegistrationSO.retrieveSupplierProfile(reqDTO);
    		this.getManagedBean().getCtContract().setSmSupplier(resDTO.getSmSupplier());
    	
    		if (resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks() != null && !resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks().isEmpty()) {
    			populateBankAccountList = new ArrayList<SelectItem>();
    			smSupplierBank = resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks();
				for (SmSupplierBank obj : smSupplierBank) {
					populateBankAccountList.add(new SelectItem(obj.getSupplierBankId(), obj.getAccountNo()));
			    }      
    		}
    	}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
    }
    
    public SelectItem[] retrieveFinancialOrgList() throws NamingException {
		
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;
		try{
		    int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_FACTORING_COMPANY);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    for(PmFinancialOrg pmFinancialOrg : list) 
				  items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());   
	    }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}   	
	   return items;
	}

    public SelectItem[] retrieveFinancialBranchOrgById() throws NamingException {
	  SelectItem[] items = null; 
	  IFinancialOrgSO financialOrgSO;
	  try{
	       int i = 1;
	       financialOrgSO = (IFinancialOrgSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
	       FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
	       reqDTO.setFinancialOrgId(this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
	       FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgById(reqDTO);
	       List<PmFinancialBranch> list = resDTO.getPmFinancialOrg().getPmFinancialBranchs();
	       items = new SelectItem[list.size()+1];
	       items[0] = new SelectItem(null, "- Select One -");
	       for(PmFinancialBranch pmFinancialBranch : list) 
			  items[i++] = new SelectItem(pmFinancialBranch.getFinancialBranchId(), pmFinancialBranch.getBranchName());
    
	    }catch (Exception e) {
		e.printStackTrace();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	 }   	
     return items;
   }

   public String getPaymentBankName(){
	   IFinancialOrgSO financialOrgSO;
		  try{
		       financialOrgSO = (IFinancialOrgSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
		       FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		       if(this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() == null)
		    	   this.getManagedBean().getFactoring().getFinancialOrg().setFinancialOrgId(1L);
		       reqDTO.setFinancialOrgId(this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
		       FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgById(reqDTO);
		       if((resDTO.getPmFinancialOrg().getPmFinancialBranchs().size() == 0 || resDTO.getPmFinancialOrg().getPmFinancialBranchs() == null)
		    		&& (resDTO.getPmFinancialOrg().getPmFinancialAccs().size() > 0 && resDTO.getPmFinancialOrg().getPmFinancialAccs()!= null)){
		    	   List<PmFinancialAcc> list = resDTO.getPmFinancialOrg().getPmFinancialAccs();
			       PmFinancialAcc pmObj = null;
			       for(PmFinancialAcc o : list) {
			    	   if (pmObj == null || o.getExpDate().compareTo(new Date()) > 0){
			    		   pmObj = o;
					   } 
			       } this.getManagedBean().setAccountNo(pmObj.getAccountNo());
			       return pmObj.getPmPaymentBankOrg().getFinOrgName();
		       
		       }else if(resDTO.getPmFinancialOrg().getPmFinancialBranchs().size() > 0 && resDTO.getPmFinancialOrg().getPmFinancialBranchs() != null){
		    	   
		    	   List<PmFinancialBranch> list = resDTO.getPmFinancialOrg().getPmFinancialBranchs();
			       PmFinancialAcc pmObj = null;
			       for(PmFinancialBranch pmFinancialBranch : list){ 
			    	   if(pmFinancialBranch.getFinancialBranchId().equals(this.getManagedBean().getFactoring().getFinancialBranchId().getFinancialBranchId())){
			               for(PmFinancialAcc o : pmFinancialBranch.getPmFinancialAccs()) {
			    	          if (pmObj == null || o.getExpDate().compareTo(new Date()) > 0)
			    		          pmObj = o;
			              } this.getManagedBean().setAccountNo(pmObj.getAccountNo());
			                return pmObj.getPmPaymentBankOrg().getFinOrgName();
		               }
			       }
		       } 
		    }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}   	
	  return "";
   }

   public void reset(){ 
	   
	   this.getManagedBean().setAccountNo(null);
   }
   
   public void setActionListener(){
	   IContractSO contractSO = null;
       ContractReqDTO reqDTO = null;	
		try{
		
		   reqDTO = new ContractReqDTO();
		   contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
		   reqDTO.setLoaId(this.getManagedBean().getLoaId());
		   ContractResDTO resDTO = contractSO.retrieveContractByLoaId(reqDTO);
		   if(resDTO.getContract() != null && resDTO.isSuccessful()){
			  this.getManagedBean().setLoaDate(resDTO.getContract().getLoa().getAckDate());
			  this.getManagedBean().setContractNo(resDTO.getContract().getContractNo());
			  this.getManagedBean().setContractName(resDTO.getContract().getContractName());
			  this.getManagedBean().setEffDate(resDTO.getContract().getLatestContractVer().getCtContractValue().getEffDate());
			  this.getManagedBean().setExpDate(resDTO.getContract().getLatestContractVer().getCtContractValue().getExpDate());
			  this.getManagedBean().setCtContract(resDTO.getContract());
		   }else{
			   
		   }
		 }catch (Exception ex) {
			log.error("Exception", ex);
		}finally {
			// resetObjectConditions();
		}
	 }/* Function End */
    
    public void fileUpload(FileUploadEvent event) throws Exception {
	    UploadedFile item = event.getFile();
	    String filePath = "C:/projects/uploads/"+item.getFileName();
	    FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    this.getManagedBean().getFactoring().setFileName(item.getFileName());
	    this.getManagedBean().getFactoring().setFilePath(filePath);
        fileOuputStream.write(item.getContents());
        fileOuputStream.close();
    }
    
    public String fileNames(String filePath){
		String fileName = "";
		if(filePath == null || filePath=="")
		  return fileName;
		
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	    return fileName;
	}
    
    public void downloadFile(String filePath) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		
		File file = new File(filePath);
		fileName = file.getName();
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
	
    public SelectItem[]  getSupplierLoaDropdown(){
    	 SelectItem[] items = null;  
    	 IQTAwardSO qtAwardSO = null;
    	 AwardSupplierReqDTO reqDTO = null;		
	     try{
	    	 
	    	 ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
	    	 SupplierSearchReqDTO reqDTO1 = new SupplierSearchReqDTO();
	    	 reqDTO1.setUserId(45L);
	 		 SupplierSearchResDTO resDTO1 = supplierSearchSO.retriveSupplierProfiles(reqDTO1);
	 		 List<SmSupplier> supplier = resDTO1.getResultBean().getResultList();
	 		
	    	  int i = 1;
	    	  reqDTO = new AwardSupplierReqDTO();
	    	  qtAwardSO = (IQTAwardSO)BusinessDelegate.getInstance().getServiceObject("sc", IQTAwardSO.class);
	    	  reqDTO.setSupplierId(296l);
	    	  AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);
	    	  List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
	    	  items = new SelectItem[list.size()+1];
			  items[0] = new SelectItem(null, "- Select One -");
			  for(ScLoa loa : list) {
				     items[i++] = new SelectItem(loa.getLoaId(), loa.getLoaNo());
			   }
			 }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     }
		return items;
    }
    
    public List<SelectItem> getPopulateBankAccountList() {
		return populateBankAccountList;
	}

	public void setPopulateBankAccountList(List<SelectItem> populateBankAccountList) {
		this.populateBankAccountList = populateBankAccountList;
	}

	public String convertTime(Date time){ 
        String str ="";
    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
        if(time == null)
        	return str;
        return format.format(time); 
    } 
}
