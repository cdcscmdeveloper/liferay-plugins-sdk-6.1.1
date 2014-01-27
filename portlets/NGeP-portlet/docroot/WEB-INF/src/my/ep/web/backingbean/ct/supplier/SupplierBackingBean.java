/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.supplier;

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
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.supplier.ContractSupplierReqDTO;
import my.ep.dto.ct.supplier.ContractSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.bankaccount.CTBankAccountDataType;
import my.ep.jaxb.ct.factoring.CTFactoringDataType;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
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
	private List<CtContract> dbContractList;
	
	public SupplierBackingBean(){
			
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void init(){
	    
		if(this.getManagedBean().getCtContract() == null){
			this.getManagedBean().setCtContract(new CtContract());
			this.getManagedBean().getCtContract().setLatestContractVer(new CtContractVer());
			
		}
		
		if(this.getManagedBean().getFactoring() == null){
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
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
		}
		
		if(this.getManagedBean().getCtTerminationReason() == null){
			
			this.getManagedBean().setCtTerminationReason(new CtTerminationReason());
			this.getManagedBean().getCtTerminationReason().setReasonType(new PmParameter());
			this.getManagedBean().getCtTerminationReason().getReasonType().setParameterId(1L);
		}
		
         if(this.getManagedBean().getTermination() == null){
			
			this.getManagedBean().setTermination(new CtTermination());
			this.getManagedBean().getTermination().setCtContractVer(new CtContractVer());
            this.getManagedBean().getTermination().setDecisionType(new PmParameter());
			this.getManagedBean().getTermination().getDecisionType().setParameterId(1L);
			
		}
		
         if(this.getManagedBean().getAction() == null) {
 			this.getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SAVE, "Submit"});
 		}
         
	//	ContractBackingBean ctBB = new ContractBackingBean();
	//	ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());

	}
    
    public void preRenderView(){
    	
    	try{	
    		System.out.println("**********preRenderView**************");
    	//	BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
    		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
    		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
    		String taskId = originalServletRequest.getParameter("taskId");
    		if(taskId == null)
    		return;
    		System.out.println("taskId:"+taskId);

    		FacesScopes face = new FacesScopes();
    		String token =face.getBPMAuthenticationCtxToken();
    		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

    		BpmRequestDTO reqDTO = new BpmRequestDTO();


    		reqDTO.setCtxToken(token);
    		reqDTO.setTaskId(taskId);
    		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
    		Object obj = res.getPojo();
    		ContractReqDTO ctReqDTO = new ContractReqDTO();
    		
    		if(res.getProcessName().equalsIgnoreCase("Bank Account Change Request")){
    			CTBankAccountDataType dataType = (CTBankAccountDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    		
    		}else if(res.getProcessName().equalsIgnoreCase("Contract Termination")){
    			CTContractTerminationDataType dataType = (CTContractTerminationDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    		
    		}else if(res.getProcessName().equalsIgnoreCase("Factoring Company Request")){
    			CTFactoringDataType dataType = (CTFactoringDataType)obj;
    			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
    			ctReqDTO.setContractId(new Long(dataType.getContractId()));
    			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
    		}

    		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
    		ContractResDTO resDTO = contractSO.retrieveContract(ctReqDTO);
    		this.getManagedBean().setCtContract(resDTO.getContract());

    		this.getManagedBean().setTaskName(res.getTaskName());
    		this.getManagedBean().setTaskId(taskId);
    		this.getManagedBean().setAction(res.getAction());
    		this.getManagedBean().setChkMenuOrTxtBox("BPM");
    		if(res.getProcessName().equalsIgnoreCase("Bank Account Change Request")){
    			
    			FacesContext fc = FacesContext.getCurrentInstance();
    			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_BANK_ACCOUNT);
    	
    		}else if(res.getProcessName().equalsIgnoreCase("Factoring Company Request")){
    			
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
					populateContractList.add(new SelectItem(obj.getContractId(),obj.getLatestContractVer().getDocNo()));
				    log.info(obj.getContractId() + "==========="+obj.getLatestContractVer().getDocNo());
			    }      
		  }
		} finally {
			// resetObjectConditions();

		}
		return populateContractList;
	}

	/* Function to populate state value */
	public void changeContractActionListener() {

		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try {
		
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
			 reqDTO.setContractId(this.getManagedBean().getContractNo());
			 ContractSupplierResDTO resDTO = supplierSO.retrieveContractById(reqDTO);
			 if(resDTO.isSuccessful()){
			     this.getManagedBean().setCtContract(resDTO.getCtContract());
			     this.getManagedBean().setContractNo(new Long(this.getManagedBean().getCtContract().getLatestContractVer().getDocNo()));
			 }
		} catch (Exception ex) {
			log.error("Exception", ex);
		} finally {
			// resetObjectConditions();
		}
	}/* Function End */
    
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
     	IContractSupplierSO supplierSO = null;
 		ContractSupplierReqDTO reqDTO = null;	
 		try{
 			
 			 reqDTO = new ContractSupplierReqDTO();
 			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSupplierSO.class);
 			 this.getManagedBean().getCtTerminationReason().setCtTermination(this.getManagedBean().getTermination());
 			 this.getManagedBean().getTermination().getCtTerminationReasons().add(this.getManagedBean().getCtTerminationReason());
 			 this.getManagedBean().getTermination().getDecisionType().setParameterId(155L);
 			 this.getManagedBean().getTermination().getCtContractVer().setContractVerId(this.getManagedBean().getContractVer().getContractVerId());
 			 reqDTO.setTerminationRequest(this.getManagedBean().getTermination());
 			
 			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(outcome);
			
			ContractSupplierResDTO resDTO = supplierSO.saveTerminationRequest(reqDTO);
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
 	 
 		   supplierSO = null;
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

}
