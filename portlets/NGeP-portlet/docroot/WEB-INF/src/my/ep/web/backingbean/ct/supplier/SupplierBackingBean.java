/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.supplier;

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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
		 if(this.getManagedBean().getAction() == null) {
	 			this.getManagedBean().setAction(new String[]{"Save", "Submit"});
	 		}
	//	getFactoringDropdownValues();
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
			this.getManagedBean().getFactoring().setCtContract(new CtContract());
			this.getManagedBean().getFactoring().getCtContract().setLatestContractVer(new CtContractVer());
			this.getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
			this.getManagedBean().getFactoring().setContractAdmin(new PmUser());
			this.getManagedBean().getFactoring().setSmSupplier(new SmSupplier());
			this.getManagedBean().getFactoring().setStatus(new PmStatus());
			
			this.getManagedBean().getFactoring().getCtContract().setContractId(11L);
			this.getManagedBean().getFactoring().getFinancialOrg().setFinancialOrgId(1L);
			this.getManagedBean().getFactoring().getContractAdmin().setUserId(27L);
			this.getManagedBean().getFactoring().getStatus().setStatusId(1L);
			this.getManagedBean().getFactoring().getSmSupplier().setSupplierId(4L);
			
			this.getManagedBean().getFactoring().setAuthRefNo("123-09234-76453");
			this.getManagedBean().getFactoring().setEffDate(new Date());
			this.getManagedBean().getFactoring().setExpDate(new Date());
			
			this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().setContractVerId(11L);
			this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().setStatus(new PmStatus());
			this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().getStatus().setStatusId(1L);
			this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().setDocType("RF");
			this.getManagedBean().getFactoring().getCtContract().getLatestContractVer().setDocNo("8888878");
			if(this.getManagedBean().getFactoring().getLoa() == null)
				this.getManagedBean().getFactoring().setLoa(new ScLoa());
		}
		 
		if(this.getManagedBean().getCtTerminationReason() == null){
		    	this.getManagedBean().setCtTerminationReason(new CtTerminationReason());
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
    			ctReqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    		
    		}else if(res.getProcessName().equalsIgnoreCase("Contract Termination")){
    			CTContractTerminationDataType dataType = (CTContractTerminationDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    		
    		}else if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)){
    			CTFactoringDataType dataType = (CTFactoringDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
    		}

    		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
    		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
    		this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
    		this.getManagedBean().setContractVer(resDTO.getCtContractVer());

    		if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_BANK_ACCOUNT);
    	
    		}else if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_FACTORING);
    		
    		}else if(res.getProcessName().equalsIgnoreCase("Contract Termination")){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_TERMINATION);
    		
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
		try {
			
				 reqDTO = new ContractSupplierReqDTO();
				 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
				 FacesScopes facesScopes = new FacesScopes();
				 reqDTO.setUserLonginId(facesScopes.getUserSessionBean().getUserId());
				 ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
		         
			if (resDTO.getContractList()!= null && !resDTO.getContractList().isEmpty()) {
				populateContractList = new ArrayList<SelectItem>();
				dbContractList = resDTO.getContractList();
				for (CtContract obj : dbContractList) {
					populateContractList.add(new SelectItem(obj.getContractId(),obj.getContractNo()));
			    }      
		  }
		} finally {
			// resetObjectConditions();

		}
		return populateContractList;
	}
	
	/* Function to populate list of country */
	public List<SelectItem> getPopulateLoaList() {

		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try {
			
				 reqDTO = new ContractSupplierReqDTO();
				 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
				 FacesScopes facesScopes = new FacesScopes();
				 reqDTO.setUserLonginId(facesScopes.getUserSessionBean().getUserId());
				 ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
		         
			if (resDTO.getContractList()!= null && !resDTO.getContractList().isEmpty()) {
				populateContractList = new ArrayList<SelectItem>();
				dbContractList = resDTO.getContractList();
				for (CtContract obj : dbContractList) {
					populateContractList.add(new SelectItem(obj.getLatestContractVer().getContractVerId(),obj.getLatestContractVer().getDocNo()));
			    }      
		  }
		} finally {
			// resetObjectConditions();

		}
		return populateLoaList;
	}

	/* Function to populate state value */
	public void changeContractActionListener() {

		IContractSO contractSO = null;
        ContractReqDTO reqDTO = null;	
		try {
		
			 reqDTO = new ContractReqDTO();
			 contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			 reqDTO.setContractId(this.getManagedBean().getContractNo());
			 ContractResDTO resDTO = contractSO.retrieveContract(reqDTO);
			 if(resDTO.isSuccessful()){
				 this.getManagedBean().setCtContract(resDTO.getContract());
			     this.getManagedBean().setContractVer(resDTO.getContract().getLatestContractVer()); 
			    
			     changeAccountActionListener();
			 }
		} catch (Exception ex) {
			log.error("Exception", ex);
		} finally {
			// resetObjectConditions();
		}
	}/* Function End */
    
	public void changeAccountActionListener(){
		
		IMofRegistrationSO iMofRegistrationSO = null;
    	MofRegistrationReqDTO reqDTO = null;	
		try {
		
			 reqDTO = new MofRegistrationReqDTO();
	    	 reqDTO.setSupplierId(this.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
	    	 iMofRegistrationSO = (IMofRegistrationSO)BusinessDelegate.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
	    	 MofRegistrationResDTO resDTO = iMofRegistrationSO.retrieveSupplierProfile(reqDTO);
	    	 List<SmSupplierBank> smSupplierBanks = resDTO.getSmSupplier().getSmAppl().getSmSupplierBanks();
	        if (smSupplierBanks != null && !smSupplierBanks.isEmpty()) {
		    			populateBankAccountList = new ArrayList<SelectItem>();
		    			for (SmSupplierBank obj : smSupplierBanks) {
							populateBankAccountList.add(new SelectItem(obj.getSupplierBankId(), obj.getAccountNo()));
					    }      
		    		}
		} catch (Exception ex) {
			log.error("Exception", ex);
		} finally {
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
			
			reqDTO.setSupplierUserId(getFacesScopes().getUserSessionBean().getLoginId());
		    reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		    if (resDTO.isSuccessful()) {
				
				  log.info("Record saved successfully");
				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Change Bank Account. : "));
				  if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
				   }
			  } else {
					throw new EpException("Failed.");
				}
		} catch (Exception e) {
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
			 reqDTO.setSupplierUserId(getFacesScopes().getUserSessionBean().getLoginId());
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
		   	 reqDTO.setFactoring(this.getManagedBean().getFactoring());
			 
		   	 FacesScopes facesScopes = new FacesScopes();
			 reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			 reqDTO.setOutcome(action);
			 reqDTO.setTaskId(this.getManagedBean().getTaskId());
			 reqDTO.setSupplierUserId(getFacesScopes().getUserSessionBean().getLoginId());
			 ContractSupplierResDTO resDTO = supplierSO.saveFactoringRequest(reqDTO);
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Factoring Request. : "));
					// save action with task ID not null will remain in the same page
					if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
						(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
					}
				} else {
					throw new EpException("Failed.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Factoring Request save failed."));
			}finally{
			supplierSO = null;
			reqDTO = null;
		}
		
		return null;
	}
    
    public SelectItem[] getFactoringDropdownValues() {
		
    	SelectItem[] items = null;
	    IParameterSO parameterSO = null;
	    ParameterReqDTO reqDTO = null;
		try{
		   
			reqDTO = new ParameterReqDTO();
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			reqDTO.setParameterType("BS");
			reqDTO.setParameterCode("FC");
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			Long value = resDTO.getPmParameter().getParameterId();
		
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
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
