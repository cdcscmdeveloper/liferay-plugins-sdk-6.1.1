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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.supplier.IContractSupplierSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
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
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
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
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialBranch;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.supplier.SupplierManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
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
	
	private String editorValue;
	private ContractBackingBean ctBB = null;
	private static Logger log = Logger.getLogger(SupplierBackingBean.class);
	
	public SupplierBackingBean(){
	
	   	 if (this.getManagedBean().getAction() == null) 
			  this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SUBMIT });
		
	  	 if (getManagedBean().getFactoring() == null) 
			   initializeFactoring();
		 
		if(ctBB == null){
			ctBB = new ContractBackingBean(this.getManagedBean().getFileDirectory());
			ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
		}
	}
	
	private void initializeFactoring() {
	    
		    getManagedBean().setFactoring(new CtFactoring());
		    PmStatus pmStatus = new PmStatus();
		    pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
		    getManagedBean().getFactoring().setStatus(pmStatus);
		 
		    if(this.getManagedBean().getFactoring().getFinancialOrg() == null) 
			     this.getManagedBean().getFactoring().setFinancialOrg(new PmFinancialOrg());
		
		    if(this.getManagedBean().getFactoring().getPmFinancialBranch() == null) 
			     this.getManagedBean().getFactoring().setPmFinancialBranch(new PmFinancialBranch());
	}
	
	 private FacesScopes getFacesScopes(){
		  return new FacesScopes();
	 }
	
	 public void preRenderChangeBankAccount() {
	    	renewSesionObject();
	    	FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "changeBankAccount.xhtml?faces-redirect=true");
	  }
	
	  public void preRenderRequestFactoring() {
		    renewSesionObject();
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "requestOfFactoring.xhtml?faces-redirect=true");
	  }
    
	  public void preRenderView() {
	   
		    IContractSupplierSO contractSupplierSO = null;
		    IContractSO contractSO = null;
		    Calendar cal = Calendar.getInstance();
	        log.info("SupplierBackingBean: Performance Testing preRenderView Function: Time taken to start >>>>"+ cal.getTime());
		
		    try {
			
		    	System.out.println("**********preRenderView**************");
			    HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			    HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			    String taskId = originalServletRequest.getParameter("taskId");
			    if(taskId == null) 
				return;
			

		        String token = getFacesScopes().getBPMAuthenticationCtxToken();
			    BpmRequestDTO reqDTO = new BpmRequestDTO();
			    reqDTO.setCtxToken(token);
			    reqDTO.setTaskId(taskId);

			    IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			    BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			    Object obj = res.getPojo();
			    getManagedBean().setTaskId(taskId);
		        getManagedBean().setAction(res.getAction());
			    getManagedBean().setTaskName(res.getTaskName());
			    String docType = null;

			    if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)) {
				      CTBankAccountDataType dataType = (CTBankAccountDataType) obj;
				      docType = ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT;
				      ContractReqDTO ctReqDTO = new ContractReqDTO();
				      ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				    
				      contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractSO.class);
				      ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
				
				      getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
				      getManagedBean().setContractVer(resDTO.getCtContractVer());
				      getManagedBean().setContractVerId(ctReqDTO.getContractVerId());
				      getManagedBean().setRemarkList(resDTO.getCtRemarks());
				      getManagedBean().setAttachmentList(resDTO.getCtAttachments());
				
				      if(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo()!=null) {
					      //getPopulateBankAccountList();
					         String account = getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
					         if (getManagedBean().getContractVer().getCtContractValue().getBankName() != null)
					                   	account = account + " / " + getManagedBean().getContractVer().getCtContractValue().getBankName();
					         getManagedBean().setAccountNoAndName(account);
				      }
		
			    } else if (res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)) {
			           	CTFactoringDataType dataType = (CTFactoringDataType) obj;
			     	    docType = ParameterConst.DOCTYPE_REQUEST_FACTORING;
				
			     	   ContractSupplierReqDTO csReqDTO = new ContractSupplierReqDTO();
					   csReqDTO.setFactoringId(new Long(dataType.getDocId()));
			
					   contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
				       ContractSupplierResDTO csResDTO = contractSupplierSO.retrieveFactoringRequest(csReqDTO);
				
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
				
			      }
			
			      getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				     docType, getManagedBean().getCtContract().getContractNo()));
			      ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			      ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			 
			      if (res.getProcessName().equalsIgnoreCase(CTProcessNameConst.BANK_ACCOUNT_CHANGE_PROCESS_NAME)) {
				         FacesContext fc = FacesContext.getCurrentInstance();
				         fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_BANK_ACCOUNT);
			    
			      } else if (res.getProcessName().equalsIgnoreCase(CTProcessNameConst.FACTORING_REQUEST_PROCESS_NAME)) {
			            	FacesContext fc = FacesContext.getCurrentInstance();
				            fc.getApplication().getNavigationHandler().handleNavigation(fc,	null, ContractManagementConst.NAV_TO_CONTRACT_FACTORING);
			     
			      } else {
				         FacesContext fc = FacesContext.getCurrentInstance();
				         fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "navigation");
			      }
		
		    } finally {
			
		    	contractSupplierSO = null;
			    contractSO = null;
			    log.info("SupplierBackingBean: Performance Testing preRenderView Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
			    log.info("SupplierBackingBean: Performance Testing preRenderView Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);	
	   	   }
	 }
    
	public List<SelectItem> getPopulateContractList() {
		
		IContractSupplierSO supplierSO = null;
		ContractSupplierReqDTO reqDTO = null;
		List<SelectItem> populateContractList = new ArrayList<SelectItem>();
		
		try {
			
			  if (getManagedBean().getContractList().size() == 0) {
			     	reqDTO = new ContractSupplierReqDTO();
				    supplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
				    reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				    ContractSupplierResDTO resDTO = supplierSO.retrieveSupplierContract(reqDTO);
				    if (resDTO.getContractList() != null && !resDTO.getContractList().isEmpty()) 
					       getManagedBean().getContractList().addAll(resDTO.getContractList());    
		   	}
			for (CtContract obj : getManagedBean().getContractList()) {
				populateContractList.add(new SelectItem(obj.getLatestContractVer().getContractVerId(), obj.getContractNo()));
			}
		} finally {
			supplierSO = null;
		}
		return populateContractList;
	}

	public void changeContractActionListener() {
		IContractSO contractSO = null;
		try {
			if(getManagedBean().getContractVerId()!=null) {
				ContractReqDTO reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				reqDTO.setContractVerId(getManagedBean().getContractVerId());
				reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
				reqDTO.setUpdateAction(true);
				ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
				if (resDTO.isSuccessful()) {
					getManagedBean().setContractVer(resDTO.getCtContractVer());
					getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
					getManagedBean().setOriginalAccountNo(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
					if(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo()!=null) {
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
		}
	}
    
	private void setBankInfo() {
		SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
		
		getManagedBean().getContractVer().getCtContractValue().setBankName(null);
		getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
		getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
		
		if(supplierBank!=null) {
			getManagedBean().getContractVer().getCtContractValue().setBankAccountNo(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
			getManagedBean().getContractVer().getCtContractValue().setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
			getManagedBean().getContractVer().getCtContractValue().setBankBranch(supplierBank.getBankBranch());
			getManagedBean().getContractVer().getCtContractValue().setBankAddress(supplierBank.getSmAddress());
		}
	}
	 
	public List<SelectItem> getPopulateBankAccountList() {
		List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		if (this.getManagedBean().getCtContract() != null) {
			if (this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks() != null
					&& this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				List<SmSupplierBank> supplierBank = this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks();
				for (SmSupplierBank obj : supplierBank) {
					if(obj.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
						String account = obj.getAccountNo();
						if (obj.getPmFinancialOrg() != null)
							account = account + " / " + obj.getPmFinancialOrg().getFinOrgName();
						populateBankAccountList.add(new SelectItem(obj.getAccountNo(), account));
						if(!getManagedBean().getSupplierBankAccountMap().containsKey(obj.getAccountNo())) {
							getManagedBean().getSupplierBankAccountMap().put(obj.getAccountNo(), obj);
						}
					}
				}
			}
		}
		return populateBankAccountList;
	}
	 
	public void changeAccountActionListener() {
		setBankInfo();
	}
     
	public String saveChangeBankRequest(String action) throws Exception {
		IContractSO contractSO = null;
		 Calendar cal = Calendar.getInstance();
	     log.info("SupplierBackingBean: Performance Testing saveChangeBankRequest Function: Time taken to start >>>>"+ cal.getTime());
		try {
			if (getManagedBean().getOriginalAccountNo()!=null && getManagedBean().getOriginalAccountNo().equalsIgnoreCase(getManagedBean().getContractVer().getCtContractValue().getBankAccountNo())) {
				throw new EpException(MessageConst.ERROR_750000);
			}
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
				 CtRemark remark = getManagedBean().getRemarkList().get(0);
				 if(remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")){
					 throw new EpException(MessageConst.INFO_100022, new String[]{"Remarks"});
				 }
		      }

			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setUserRole(getUserRole());
			if(reqDTO.getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) || 
					reqDTO.getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value()) ||
					reqDTO.getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()) || 
					reqDTO.getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				reqDTO.setSupplierPmUser(getFacesScopes().getUserSessionBean());
			}
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,	IContractSO.class);

			reqDTO.setCtContract(getManagedBean().getCtContract());
			reqDTO.setCtContractVer(getManagedBean().getContractVer());

			reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700043;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
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
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			 log.info("SupplierBackingBean: Performance Testing saveChangeBankRequest Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
			log.info("SupplierBackingBean: Performance Testing saveChangeBankRequest Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
			
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
      
	public String saveFactoringRequest(String action) throws Exception {
		IContractSupplierSO supplierSO = null;
		 Calendar cal = Calendar.getInstance();
	     log.info("SupplierBackingBean: Performance Testing saveFactoringRequest Function: Time taken to start >>>>"+ cal.getTime());
	
		try {
			ContractSupplierReqDTO reqDTO = new ContractSupplierReqDTO();
			supplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSupplierSO.class);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());

			if(getManagedBean().getFactoring().getFileName() == null || getManagedBean().getFactoring().getFileName().isEmpty()) {
				throw new EpException(MessageConst.INFO_100022,	new String[] { "Factoring Agreement" });
			}
			if(!getManagedBean().isAgreeFactoringClause()) {
				if(!isApproverRole() && !getManagedBean().isRemoveFactoring()) {
					throw new EpException(MessageConst.ERROR_750001);
				}
			}

			reqDTO.setOutcome(action);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			reqDTO.setUserRole(getUserRole());
			
			getManagedBean().getFactoring().setCtContract(this.getManagedBean().getCtContract());
			getManagedBean().getFactoring().setSmSupplier(this.getManagedBean().getCtContract().getSmSupplier());
			getManagedBean().getFactoring().setLoa(this.getManagedBean().getCtContract().getLoa());
			reqDTO.setFactoring(getManagedBean().getFactoring());

			reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
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
			log.info("SupplierBackingBean: Performance Testing saveFactoringRequest Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
			log.info("SupplierBackingBean: Performance Testing saveFactoringRequest Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
			
		}
		return null;
	}
	
	public void flush(){
		this.getManagedBean().setFinancialBankBranch(null);
		this.getManagedBean().setAccountNo(null);
		this.getManagedBean().setFactoringBankName(null);
	}
    
	public SelectItem[] retrieveFinancialOrgList() throws NamingException {
		SelectItem[] items = null;
		IFinancialOrgSO financialOrgSO;
		
		try {
			int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_FACTORING_COMPANY);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			for (PmFinancialOrg pmFinancialOrg : list) {
				items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(),	pmFinancialOrg.getFinOrgName());
				getManagedBean().getFactoringCompMap().put(pmFinancialOrg.getFinancialOrgId(),	pmFinancialOrg.getFinOrgName());
			}
		} finally {
			financialOrgSO = null;
		}
		return items;
	}

   public void reset(){ 
	   
	   SelectItem[] items = null;
	   IFinancialOrgSO financialOrgSO;
	   flush();
		try {
			int i = 1;
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			if (this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId() != null) {
				reqDTO.setFinancialOrgId(this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
				FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgById(reqDTO);
				
				if(resDTO.getPmFinancialOrg().getPmFinancialBranchs() != null && resDTO.getPmFinancialOrg().getPmFinancialBranchs().size() > 0){
					List<PmFinancialBranch> list = resDTO.getPmFinancialOrg().getPmFinancialBranchs();
					items = new SelectItem[list.size() + 1];
					items[0] = new SelectItem(null, "- Select One -");
					for (PmFinancialBranch pmFinancialBranch : list) {
						items[i++] = new SelectItem(pmFinancialBranch.getFinancialBranchId(), pmFinancialBranch.getBranchName());
					}this.getManagedBean().setFinancialBankBranch(items);
				
				}else{
					   List<PmFinancialAcc> accList = resDTO.getPmFinancialOrg().getPmFinancialAccs();
					   for(PmFinancialAcc financialAcc : accList) {
						 if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))){
							getManagedBean().setAccountNo(financialAcc.getAccountNo());
							getManagedBean().setFactoringBankName(financialAcc.getPmPaymentBankOrg().getFinOrgName());
							for(PmAddressType pmAddressType : financialAcc.getPmPaymentBankOrg().getPmAddressTypes()) {
							   if(pmAddressType.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())) {
								  getManagedBean().setFactoringBankAddress(getFullAddress(pmAddressType.getPmAddress()));
								  break;
								}
						    }
					    }
				     }
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
			if (getManagedBean().getFactoring().getPmFinancialBranch().getFinancialBranchId() != null) {
				financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IFinancialOrgSO.class);
				FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
				reqDTO.setFinancialOrgId(this.getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
				FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgById(reqDTO);
				List<PmFinancialBranch> list = resDTO.getPmFinancialOrg().getPmFinancialBranchs();
		        for(PmFinancialBranch pmFinancialBranch : list) {
			      if(pmFinancialBranch.getFinancialBranchId().equals(getManagedBean().getFactoring().getPmFinancialBranch().getFinancialBranchId())) {
				  for (PmFinancialAcc financialAcc : pmFinancialBranch.getPmFinancialAccs()) {
					if(financialAcc.getEffDate().before(new Date()) && (financialAcc.getExpDate()==null || financialAcc.getExpDate().after(new Date()))) {
						getManagedBean().setAccountNo(financialAcc.getAccountNo());
						getManagedBean().setFactoringBankName(financialAcc.getPmPaymentBankOrg().getFinOrgName());
						for(PmAddressType pmAddressType : financialAcc.getPmPaymentBankOrg().getPmAddressTypes()) {
							if(pmAddressType.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())) {
								getManagedBean().setFactoringBankAddress(getFullAddress(pmAddressType.getPmAddress()));
								break;
							}
					     }
				      }
			        }
			      }
		        }
			 }
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			financialOrgSO = null;
		}
    }
	public void changeLoaActionListener() {
		IContractSO contractSO = null;
		try {
			if (this.getManagedBean().getLoaId() != null) {
				ContractReqDTO reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				reqDTO.setLoaId(this.getManagedBean().getLoaId());
				ContractResDTO resDTO = contractSO.retrieveContractByLoaId(reqDTO);
				if (resDTO.getContract() != null && resDTO.isSuccessful()) {
					this.getManagedBean().setLoaDate(resDTO.getContract().getLoa().getAckDate());
					this.getManagedBean().setContractNo(resDTO.getContract().getContractNo());
					this.getManagedBean().setContractName(resDTO.getContract().getContractName());
					this.getManagedBean().setEffDate(resDTO.getContract().getCurrentContractVer().getCtContractValue().getEffDate());
					this.getManagedBean().setExpDate(resDTO.getContract().getCurrentContractVer().getCtContractValue().getExpDate());
					this.getManagedBean().setCtContract(resDTO.getContract());
					if(resDTO.getContract().getCtFactoring()!=null) {
						getManagedBean().setFactoring(resDTO.getContract().getCtFactoring());
						getManagedBean().getFactoring().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						getManagedBean().setRemoveFactoring(true);
						PmStatus pmStatus = new PmStatus();
						pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
						getManagedBean().getFactoring().setStatus(pmStatus);
						reset();
						if (this.getManagedBean().getFactoring().getPmFinancialBranch() == null) {
							this.getManagedBean().getFactoring().setPmFinancialBranch(new PmFinancialBranch());
						}
						else {
							resetBranch();
						}
					}
				} else {
					this.getManagedBean().setLoaDate(null);
					this.getManagedBean().setContractNo(null);
					this.getManagedBean().setContractName(null);
					this.getManagedBean().setEffDate(null);
					this.getManagedBean().setExpDate(null);
					this.getManagedBean().setCtContract(null);
					this.getManagedBean().setAccountNo(null);
					getManagedBean().setRemoveFactoring(false);
					initializeFactoring();
				}
			} else {
				this.getManagedBean().setLoaDate(null);
				this.getManagedBean().setContractNo(null);
				this.getManagedBean().setContractName(null);
				this.getManagedBean().setEffDate(null);
				this.getManagedBean().setExpDate(null);
				this.getManagedBean().setCtContract(null);
				this.getManagedBean().setAccountNo(null);
				getManagedBean().setRemoveFactoring(false);
				initializeFactoring();
			}
		} finally {
			contractSO = null;
		}
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
	    this.getManagedBean().getFactoring().setFileName(item.getFileName());
	    this.getManagedBean().getFactoring().setFilePath(filePath);
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
	
	public SelectItem[] getSupplierLoaDropdown() {
		SelectItem[] items = null;
		IQTAwardSO qtAwardSO = null;
		ISupplierSearchSO supplierSearchSO = null;
		IContractAmendmentSO contractAmendmentSO = null;
		IContractSupplierSO contractSupplierSO = null;
		try {
			supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			SupplierSearchReqDTO reqDTO1 = new SupplierSearchReqDTO();
			reqDTO1.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			SupplierSearchResDTO resDTO1 = supplierSearchSO.retriveSupplierProfiles(reqDTO1);
			List<Long> supplierIdList = new ArrayList<Long>();
			for(SmSupplier smSupplier : resDTO1.getResultBean().getResultList()) {
				supplierIdList.add(smSupplier.getSupplierId());
			}
			// Retrieve amendment loa list
			contractAmendmentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();
			amendmentReqDTO.setSupplierIds(supplierIdList);
			ContractAmendmentResDTO amendmentResDTO = contractAmendmentSO.getAmendmentLoaIdsBySupplierIds(amendmentReqDTO);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			qtAwardSO = (IQTAwardSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
			reqDTO.setSupplierIdList(supplierIdList);
			if(amendmentResDTO.getAmendmentLoaIds()!=null && amendmentResDTO.getAmendmentLoaIds().size()>0) {
				if(reqDTO.getExcludeLoaIdList() == null){
					reqDTO.setExcludeLoaIdList(new ArrayList<Long>());
				}
				reqDTO.getExcludeLoaIdList().addAll(amendmentResDTO.getAmendmentLoaIds());
			}
			
			// Retrieve in progress factoring loa list			
			contractSupplierSO = (IContractSupplierSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSupplierSO.class);
			ContractSupplierReqDTO contractSupplierReqDTO = new ContractSupplierReqDTO();
			contractSupplierReqDTO.setSupplierIds(supplierIdList);
			ContractSupplierResDTO contractSupplierResDTO = contractSupplierSO.retrieveProgressingLoaList(contractSupplierReqDTO);
			if(contractSupplierResDTO.getProgressigLoaIds()!=null && contractSupplierResDTO.getProgressigLoaIds().size()>0) {
				if(reqDTO.getExcludeLoaIdList() == null){
					reqDTO.setExcludeLoaIdList(new ArrayList<Long>());
				}
				reqDTO.getExcludeLoaIdList().addAll(contractSupplierResDTO.getProgressigLoaIds());
			}
			
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAListBySupplier(reqDTO);
			List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, "- Select One -");
			int i = 1;
			for (ScLoa loa : list) {
				items[i++] = new SelectItem(loa.getLoaId(), loa.getLoaNo());
			}
		} finally {
			supplierSearchSO = null;
			qtAwardSO = null;
			contractAmendmentSO = null;
		}
		return items;
	}

	public String getFactoringCompany() {
		if(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId()!=null) {
			return getManagedBean().getFactoringCompMap().get(getManagedBean().getFactoring().getFinancialOrg().getFinancialOrgId());
		} else {
			return "Factoring Company Name";
		}
	}
	
//	public void previewAction() throws UnsupportedEncodingException {
//		IContractTemplateSO contractTemplateSO = null;
//		Calendar cal = Calendar.getInstance();
//	     log.info("SupplierBackingBean: Performance Testing previewAction Function: Time taken to start >>>>"+ cal.getTime());
//	
//		try {
//			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
//			ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
//			if(getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())
//				|| getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
//				reqDTO.setTemplateName(ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER);	
//			} else if(getUserRole().equalsIgnoreCase(RoleCode.MIN_CONTRACT_ADMIN.value()) || getUserRole().equalsIgnoreCase(RoleCode.BPK_CCA.value())) {
//				reqDTO.setTemplateName(ContractConst.TEMPLATE_FACTORING_CLAUSE_CA);	
//			}
//			
//			ContractTemplateResDTO resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
//			setEditorValue("");
//			if(resDTO.getCtTemplate()!=null) {
//				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
//				String content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
//				
//				Map<String, String> valuesMap = new HashMap<String, String>();
//				valuesMap.put("FACTORING_COMP_NAME", getFactoringCompany());
//				valuesMap.put("FACTORING_ACCOUNT_NO", getManagedBean().getAccountNo());
//				
//				if(reqDTO.getTemplateName().equalsIgnoreCase(ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER)) {
//					valuesMap.put("CURRENT_DATE", df.format(new Date()));
//					valuesMap.put("FACTORING_BANK_NAME", getManagedBean().getFactoringBankName());
//					valuesMap.put("FACTORING_BANK_ADDRESS", getManagedBean().getFactoringBankAddress());
//					valuesMap.put("SUPPLIER_OFFICER_NAME", getFacesScopes().getUserSessionBean().getUserName());
//					valuesMap.put("SUPPLIER_OFFICER_DESIGNATION", getFacesScopes().getUserSessionBean().getDesignation());
//				}
//				
//				if(reqDTO.getTemplateName().equalsIgnoreCase(ContractConst.TEMPLATE_FACTORING_CLAUSE_CA)) {
//					valuesMap.put("CONTRAT_ADMIN_NAME", getFacesScopes().getUserSessionBean().getUserName());
//					valuesMap.put("CONTRACT_ADMIN_DESIGNATION", getFacesScopes().getUserSessionBean().getDesignation());
//					String jabatanMinistry = null;
//						if(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile()==null) {
//							jabatanMinistry = getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName();
//						}
//						else {
//							jabatanMinistry = getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName() + " / " + getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName();
//						}
//								
//					valuesMap.put("JABATAN_MINISTRY", jabatanMinistry);
//					if(getManagedBean().getCtContract().getContractPhysicalNo()==null) {
//						valuesMap.put("CONTRACT_PHYSICAL_NO",getManagedBean().getCtContract().getContractNo());
//					} else {
//						valuesMap.put("CONTRACT_PHYSICAL_NO",getManagedBean().getCtContract().getContractPhysicalNo());
//					}
//					valuesMap.put("CONTRACT_NO",getManagedBean().getCtContract().getContractNo());
//				}
//				
//				StrSubstitutor sub = new StrSubstitutor(valuesMap);
//				setEditorValue(sub.replace(content));
//			}
//		} finally {
//			contractTemplateSO = null;
//			log.info("SupplierBackingBean: Performance Testing previewAction Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
//			log.info("SupplierBackingBean: Performance Testing previewAction Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
//		}
//	}
	
	public void previewAction() {		  
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		String templateName = null;
		try {			
			context = new VelocityContext();	
			if(getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()) || getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				vmFile = "/ct/factoring/factoring-clause-supplier.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER;
			} else if(getUserRole().equalsIgnoreCase(RoleCode.MIN_CONTRACT_ADMIN.value()) || getUserRole().equalsIgnoreCase(RoleCode.BPK_CCA.value())) {
				vmFile = "/ct/factoring/factoring-clause-ca.vm";
				templateName = ContractConst.TEMPLATE_FACTORING_CLAUSE_CA;	
			}			
	
			setEditorValue("");
			if(vmFile !=null && templateName != null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);		
				context.put("factoringCompName", getFactoringCompany());
				context.put("factoringAccountNo", getManagedBean().getAccountNo());
				
				if(ContractConst.TEMPLATE_FACTORING_CLAUSE_SUPPLIER.equalsIgnoreCase(templateName)) {
					context.put("currentDate", df.format(new Date()));
					context.put("factoringBankName", getManagedBean().getFactoringBankName());
					context.put("factoringBankAddress", getManagedBean().getFactoringBankAddress());
					context.put("supplierOfficerName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("supplierOfficerDesignation", getFacesScopes().getUserSessionBean().getDesignation());
				}
				
				if(ContractConst.TEMPLATE_FACTORING_CLAUSE_CA.equalsIgnoreCase(templateName)) {
					context.put("contractAdminName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("contractAdminDesignation", getFacesScopes().getUserSessionBean().getDesignation());
					String jabatanMinistry = null;
					if(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile()==null) {
					   jabatanMinistry = getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName();
					}
					else {
					   jabatanMinistry = getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName() + " / " + getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName();
					}
								
					context.put("jabatanMinistry", jabatanMinistry);
					if(getManagedBean().getCtContract().getContractPhysicalNo()==null) {
						context.put("contractPhysicalNo",getManagedBean().getCtContract().getContractNo());
					} else {
						context.put("contractPhycicalNo",getManagedBean().getCtContract().getContractPhysicalNo());
					}
					context.put("contractNo",getManagedBean().getCtContract().getContractNo());
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
		String portletTitle = "Request Factoring";
		if(getManagedBean().isRemoveFactoring()) {
			portletTitle = portletTitle + " Removal";
		}
		return portletTitle;
	}
	
	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
}
