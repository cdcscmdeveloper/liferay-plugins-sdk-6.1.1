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
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.pm.RoleCode;
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
	private List<SelectItem> populateBankAccountList;
	private List<CtContract> dbContractList;
	ContractBackingBean ctBB = null;
	
	public SupplierBackingBean(){
	   if(this.getManagedBean().getAction() == null) 
	 	   this.getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SUBMIT});
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

    		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
    		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
    		this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
    		this.getManagedBean().setContractVer(resDTO.getCtContractVer());

    		if(resDTO.getCtContractVer().getCtContract().getCtFactoring()!= null 
    				&& resDTO.getCtContractVer().getCtContract().getCtFactoring().getFactoringId() != null){
    			this.getManagedBean().setFactoring(resDTO.getCtContractVer().getCtContract().getCtFactoring());
    			this.getManagedBean().setRemoveFactoring(true);
    		}
    		
    		this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			ContractBackingBean ctBB = new ContractBackingBean();
		    ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
    		
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
          }catch(Exception e){
    			e.printStackTrace();
    	  }
      }
    
      /* Function to populate list of country */
	  public List<SelectItem> getPopulateContractList() {

		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;	
		try{
			 reqDTO = new ContractSupplierReqDTO();
			 supplierSO = (IContractSupplierSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
			 reqDTO.setSupplierUser(getFacesScopes().getUserSessionBean());
			 ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
			 if(resDTO.getContractList()!= null && !resDTO.getContractList().isEmpty()) {
				populateContractList = new ArrayList<SelectItem>();
				dbContractList = resDTO.getContractList();
				for (CtContract obj : dbContractList) {
					populateContractList.add(new SelectItem(obj.getLatestContractVer().getContractVerId(),obj.getContractNo()));
			    }      
		    }
		}finally {
			supplierSO = null;
		}
		return populateContractList;
	 }

	 /* Function to populate state value */
	 public void changeContractActionListener() {

		IContractSO contractSO = null;
        ContractReqDTO reqDTO = null;	
		try{
		   reqDTO = new ContractReqDTO();
		   contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		   reqDTO.setContractVerId(this.getManagedBean().getContractVerId());
		   reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
		   reqDTO.setUpdateAction(true);
		   ContractResDTO resDTO = contractSO.retrieveContractVer(reqDTO);
		   if(resDTO.isSuccessful()){
			  this.getManagedBean().setContractVer(resDTO.getCtContractVer()); 
			  this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			  CombineSupAddress();
		   }
		 }catch (Exception ex) {
			log.error("Exception", ex);
		}finally {
			// resetObjectConditions();
		}
	 }/* Function End */
    
	 public List<SelectItem> getPopulateBankAccountList() {
	         populateBankAccountList = null;
	    	 if(this.getManagedBean().getCtContract() != null){
	    	   if(this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
	    	     && this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
	    		 populateBankAccountList = new ArrayList<SelectItem>();
			     List<SmSupplierBank> supplierBank = this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks();
				 for(SmSupplierBank obj : supplierBank) {
					populateBankAccountList.add(new SelectItem(obj.getAccountNo(),obj.getAccountNo()));
				 }      
	    	   }
	     }return populateBankAccountList;
	 }
	 
	 public void changeAccountActionListener(){
		 if(this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
		    	    && this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
			 for(SmSupplierBank supplierBank : this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()){
				 if(supplierBank.getAccountNo().equalsIgnoreCase(this.getManagedBean().getAccountNo())){
					 this.getManagedBean().setSupplierBank(supplierBank);
					 if(supplierBank.getSmAddress() != null)
					    CombineBankAddress(supplierBank.getSmAddress());
				 }
			 }
		 }
	  }
     
	  public String saveChangeBankRequest(String action){
		
	    IContractSO contractSO = null;
		try{
			
			ContractReqDTO reqDTO = new ContractReqDTO();
	   	    reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
	   	    reqDTO.setOutcome(action);
	   	    reqDTO.setTaskId(this.getManagedBean().getTaskId());
	   	    reqDTO.setSupplierPmUser(getFacesScopes().getUserSessionBean());
	   	    reqDTO.setUserRole(getUserRole());
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
            
			this.getManagedBean().getContractVer().getCtContractValue().setSupplierBankAccountNo(this.getManagedBean().getAccountNo());
		    reqDTO.setCtContract(this.getManagedBean().getCtContract());
            reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
			
		    reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			 if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					String messageId = MessageConst.INFO_700043;
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700025;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						messageId = MessageConst.INFO_700009;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
						messageId = MessageConst.INFO_700011;
					}
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
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
				contractSO = null;
			  }
		return null;
	 }
     
	private String getUserRole() {
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()))
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
			return RoleCode.BPK_CCA.value();		
		else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.SUPPLIER_USER.value()))
			return RoleCode.SUPPLIER_USER.value();
		else if (getFacesScopes().hasRoleUser(RoleCode.SUPPLIER_ADMIN.value()))
			return RoleCode.SUPPLIER_ADMIN.value();		
		else
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
									this.getManagedBean().getFactoring().getCtContract().getContractNo()}, 
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
	    	  reqDTO.setSupplierId(1L);
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
    
   
    
    public void CombineSupAddress(){
    	
    	StringBuilder address = new StringBuilder();
    	String strSpace =", ";
    	if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress1() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress1());
    		address.append(strSpace);
    	
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress2() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress2());
    		address.append(strSpace);
    	
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress3() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress3());
    		address.append(strSpace);
    	
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress4() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getAddress4());
    		address.append(strSpace);
    		
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmDistrict().getDistrictName() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmDistrict().getDistrictName());
    		address.append(strSpace);
    	
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmState().getStateName() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmState().getStateName());
    		address.append(strSpace);
    	
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmCountry().getCountryName() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPmCountry().getCountryName());
    		address.append("\n");
    	}if(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPostcode() != null){
    		address.append(this.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getSupplierAddress().getPostcode());
    	}
    	
    	this.getManagedBean().setSupplierAddress(address.toString());
    }
    
    public void CombineBankAddress(SmAddress smAddress){
    	
    	StringBuilder address = new StringBuilder();
    	String strSpace =", ";
    	if(smAddress.getAddress1() != null){
    		address.append(smAddress.getAddress1());
    		address.append(strSpace);
    	
    	}if(smAddress.getAddress2() != null){
    		address.append(smAddress.getAddress2());
    		address.append(strSpace);
    	
    	}if(smAddress.getAddress3() != null){
    		address.append(smAddress.getAddress3());
    		address.append(strSpace);
    	
    	}if(smAddress.getAddress4() != null){
    		address.append(smAddress.getAddress4());
    		address.append(strSpace);
    		
    	}if(smAddress.getPmDistrict().getDistrictName() != null){
    		address.append(smAddress.getPmDistrict().getDistrictName());
    		address.append(strSpace);
    	
    	}if(smAddress.getPmState().getStateName() != null){
    		address.append(smAddress.getPmState().getStateName());
    		address.append(strSpace);
    	
    	}if(smAddress.getPmCountry().getCountryName() != null){
    		address.append(smAddress.getPmCountry().getCountryName());
    		address.append("\n");
    	}if(smAddress.getPostcode() != null){
    		address.append(smAddress.getPostcode());
    	}
    	
    	this.getManagedBean().setSupplierAddress(address.toString());
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
