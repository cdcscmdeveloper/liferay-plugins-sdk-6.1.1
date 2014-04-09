/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.agreement;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import my.ep.biz.service.ct.agreement.IAgreementSO;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.agreement.CTAgreementDataType;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.template.CtTemplate;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.ct.AgreementDetails;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Agreement
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean(name="agreementBackingBean")
@RequestScoped
public class AgreementBackingBean extends BaseBackingBean<AgreementManagedBean> {

	private static final String PROCESS_DOWNLOAD_PAGE ="/xhtml/ct/agreement/downloadDocument.xhtml";
	private static Logger log = Logger.getLogger(AgreementBackingBean.class);

	private boolean supplementValue = true; 
	private AgreementDetails agreementDetails;
	private CtAgreementParty[] selectedCtAgreementParty;
	private CtAgreementParty[] selectedSignSuppliers;
	private CtAgreementParty[] selectedSignWitness;
	//private CtAgreementAppendix[] selectedAppendixs;
	
	public AgreementBackingBean(){
	   if(this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
	   }
	   if(this.getManagedBean().getCtAgreement() == null){
		   
			this.getManagedBean().setCtAgreement(new CtAgreement());
	     	this.getManagedBean().getCtAgreement().setCtSignParties(new ArrayList<CtAgreementParty>());
		}
	   
	   if(this.getManagedBean().getCtAgreement().getCtAmendment() == null){
		   this.getManagedBean().getCtAgreement().setCtAmendment(new CtAmendment());
	   }
	   
	   if(this.getManagedBean().getCtTemplates() == null){
		   this.getManagedBean().setCtTemplates(new CtTemplate());
	   }
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void init(){
		agreementDetails = new AgreementDetails();	
	/*	if(this.getManagedBean().getCtAgreementAppendix() == null){
			this.getManagedBean().setCtAgreementAppendix(new CtAgreementAppendix());
			if(this.getManagedBean().getCtAgreementAppendix().getAppendixType() == null){
			   this.getManagedBean().getCtAgreementAppendix().setAppendixType(new PmParameter());
			   this.getManagedBean().getCtAgreementAppendix().getAppendixType().setParameterId(89L); 
			}
		}*/
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());	
	
		
		if(this.getManagedBean().getCtSignParties() == null){
		   this.getManagedBean().setCtSignParties(new ArrayList<CtAgreementParty>());
		}
	    if(this.getManagedBean().getCtSignSuppliers() == null){
		   this.getManagedBean().setCtSignSuppliers(new ArrayList<CtAgreementParty>());
		}
		if(this.getManagedBean().getCtSignWitness() == null){
		   this.getManagedBean().setCtSignWitness(new ArrayList<CtAgreementParty>());
		}
	 }
	
 	 private void initializeApproverGroup() {
		IUserSO userSO = null;
		
		try{
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			//reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			reqDTO.setOrgProfileId(1250L);
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(this.getManagedBean().getApproverModel()==null) {
				List<String> source = new ArrayList<String>(); 
		        List<String> target = new ArrayList<String>();  
		        this.getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}			
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}finally {
			userSO = null;
		}
	}
	
	 public void preRenderView(){
			
		try{	
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId==null) 
			   return;
		
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
            reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setAction(res.getAction());
			
			ContractAgreementReqDTO ctReqDTO = new ContractAgreementReqDTO();
			CTAgreementDataType dataType = (CTAgreementDataType)obj;
			
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setAgreementId(new Long(dataType.getAgreementId()));
			this.getManagedBean().setUserLoginId(dataType.getContractAdmin());
			IAgreementSO agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
			
			ContractAgreementResDTO resDTO = new ContractAgreementResDTO();
			if(dataType.getAgreementType().equalsIgnoreCase("supplemental")){   
			    this.getManagedBean().setAgreementType(dataType.getAgreementType());
			    
			    if(dataType.getAgreementId().intValue() == 0){
			    	 
			    	ContractReqDTO conReqDTO = new ContractReqDTO();
					 conReqDTO.setContractId(ctReqDTO.getContractId());
					 IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
					 ContractResDTO conResDTO = contractSO.retrieveContract(conReqDTO);
					 this.getManagedBean().setCtContract(conResDTO.getContract());
					 this.getManagedBean().getCtAgreement().setCtAmendment(conResDTO.getContract().getCurrentContractVer().getCtAmendment());
			    	
			    }else{
			    	resDTO = agreementSO.retrieveAgreement(ctReqDTO);
			  }
			}else{
				 resDTO = agreementSO.retrieveAgreement(ctReqDTO);
			} 
			
			if(resDTO.getAgreement() != null){
			
			   if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			     this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			     this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
		  	   }
			  
			   this.getManagedBean().setCtAgreement(resDTO.getAgreement());  
			   this.getManagedBean().setCtContract(resDTO.getAgreement().getCtContract());
			   if(this.getManagedBean().getCtContract().getFulfillmentType() == null){
			     this.getManagedBean().getCtContract().setFulfillmentType(new PmParameter());
			   }
			
			   this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			   this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			   ContractBackingBean ctBB = new ContractBackingBean();
			   ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			   if(this.getManagedBean().getCtSignParties().size()>0)
				  this.getManagedBean().getCtSignParties().clear();
			
			   if(this.getManagedBean().getCtSignSuppliers().size()>0)
				  this.getManagedBean().getCtSignSuppliers().clear();
			
			   if(this.getManagedBean().getCtSignWitness().size()>0)
				  this.getManagedBean().getCtSignWitness().clear();
			
			   for(CtAgreementParty ctAgreementParty : this.getManagedBean().getCtAgreement().getCtSignParties()){
				  if(ctAgreementParty.getAgreementPartyType().getParameterId() .equals(92L)){
					if(ctAgreementParty.getOrgProfile() == null){
						ctAgreementParty.setOrgProfile(new PmOrgProfile());
					}
					ctAgreementParty.getOrgProfile().setOrgProfileId(1038L);
					this.getManagedBean().setMinistryProfileId(211L);
					this.getManagedBean().setDepartmentProfileId(1003L);
					this.getManagedBean().getCtSignParties().add(ctAgreementParty);
				 }else if(ctAgreementParty.getAgreementPartyType().getParameterId() .equals(93L)){
					   this.getManagedBean().getCtSignSuppliers().add(ctAgreementParty);
				 }else{
					 this.getManagedBean().getCtSignWitness().add(ctAgreementParty);
				 }
			  }
		   }
		  
		   if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.AGREEMENT_CREATION_PROCESS_NAME)){
			  if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_FINALIST_AGREEMENT_HT_NAME) ||
				  res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)){
				  this.getManagedBean().setUploadFinalAgreement("Upload");
			   }
			  FacesContext fc = FacesContext.getCurrentInstance();
			  fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_AGREEMENT);   
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	public String createAgreement(String action) {
		
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO reqDTO = null;
		
		try {
		
			reqDTO = new ContractAgreementReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
			
			if(this.getManagedBean().getAgreementType().equalsIgnoreCase("supplemental")){
				reqDTO.setAgreementType("supplemental");
			}else{
			reqDTO.setAgreementType("agreement");
			}
			
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			
			if(this.getManagedBean().getCtAgreement().getCtSignParties().size() > 0)
				this.getManagedBean().getCtAgreement().getCtSignParties().clear();
			this.getManagedBean().getCtAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSignParties());
			this.getManagedBean().getCtAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSignSuppliers());
			this.getManagedBean().getCtAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSignWitness());
		
			    
            this.getManagedBean().getCtAgreement().setApprovedDate(new Date());
       //     this.getManagedBean().getCtAgreementAppendix().setTitle("Agreement Creation");
            this.getManagedBean().getCtAgreement().setCtContract(this.getManagedBean().getCtContract());
            
           // reqDTO.setSelectedApprovers(this.getManagedBean().getApproverModel().getTarget());
          //  reqDTO.setAvailableApprovers(this.getManagedBean().getApproverModel().getSource());
            
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			
			 reqDTO.setUserRole(getUserRole());
			 
			 if(this.getManagedBean().getUserLoginId() == null)
			    reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
			 else
			    reqDTO.setUserLoginId(this.getManagedBean().getUserLoginId());
			
			reqDTO.setAgreement(this.getManagedBean().getCtAgreement());
			
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully1");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Agreement Name. : "+ this.getManagedBean().getCtContract().getContractId()));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			}else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Agreement save failed."));
		} finally {
			agreementSO = null;
			reqDTO = null;
		}
		return null;
	}

	public String createSupAgreement(String action) {
		IAgreementSO agreementSO = null;
		ContractAgreementReqDTO reqDTO = null;
		try {
			reqDTO = new ContractAgreementReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			agreementSO = (IAgreementSO) BusinessDelegate.getInstance().getServiceObject("ct", IAgreementSO.class);
		//	this.getManagedBean().getAgreement().setCtAmendment(this.getManagedBean().getCtContract().getLatestContractVer().getCtAmendment());
		//	this.getManagedBean().getAgreement().setCtAgreementAppendixs(this.getManagedBean().getAppendixList());
		//	this.getManagedBean().getAgreement().setCtContract(this.getManagedBean().getCtContract());
		//	reqDTO.setAgreement(this.getManagedBean().getAgreement());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setAgreementType(this.getManagedBean().getAgreementType());
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully1");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Agreement Name. : "+ this.getManagedBean().getCtContract().getContractId()));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Agreement save failed."));
		} finally {
			agreementSO = null;
			reqDTO = null;
		}
		return null;
	}
	
	
	
	public void addSignParty(){
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		ctAgreementParty.setAgreementPartyType(new PmParameter());
		ctAgreementParty.getAgreementPartyType().setParameterId(92L);
		if(ctAgreementParty.getOrgProfile() == null){
		   ctAgreementParty.setOrgProfile(new PmOrgProfile());
			
			if(ctAgreementParty.getOrgProfile().getParentOrgProfile() == null){
			   ctAgreementParty.getOrgProfile().setParentOrgProfile(new PmOrgProfile());
				
				if(ctAgreementParty.getOrgProfile().getParentOrgProfile().getParentOrgProfile() == null)
				   ctAgreementParty.getOrgProfile().getParentOrgProfile().setParentOrgProfile(new PmOrgProfile());
			}
		}
	    this.getManagedBean().getCtSignParties().add(ctAgreementParty);
	}
	
	public void deleteSignParty(){
		for(int i = 0; i < selectedCtAgreementParty.length; i++) {
			CtAgreementParty ctAgreementParty = selectedCtAgreementParty[i];
			this.getManagedBean().getCtSignParties().remove(ctAgreementParty);
		}selectedCtAgreementParty = null;	
	}
	
	public void addSignSupplier(){
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		ctAgreementParty.setAgreementPartyType(new PmParameter());
		ctAgreementParty.getAgreementPartyType().setParameterId(93L);
		if(ctAgreementParty.getOrgProfile() == null)
			ctAgreementParty.setOrgProfile(new PmOrgProfile());
	    this.getManagedBean().getCtSignSuppliers().add(ctAgreementParty);
	}
	
	public void deleteSignSupplier(){
		for(int i = 0; i < selectedSignSuppliers.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSignSuppliers[i];
			this.getManagedBean().getCtSignSuppliers().remove(ctAgreementParty);
		}selectedSignSuppliers = null;	
	}
	
	public void addSignWitness(){
		CtAgreementParty ctAgreementParty = new CtAgreementParty();
		if(ctAgreementParty.getOrgProfile() == null){
			ctAgreementParty.setOrgProfile(new PmOrgProfile());
			
			if(ctAgreementParty.getAgreementPartyType() == null)
				ctAgreementParty.setAgreementPartyType(new PmParameter());
		}this.getManagedBean().getCtSignWitness().add(ctAgreementParty);
	}
	
	public void deleteSignWitness(){
		for(int i = 0; i < selectedSignWitness.length; i++) {
			CtAgreementParty ctAgreementParty = selectedSignWitness[i];
			this.getManagedBean().getCtSignWitness().remove(ctAgreementParty);
		}selectedSignWitness = null;
	}
	
	public void addAppendix(){
	/*	CtAgreementAppendix ctAgreementAppendix = new CtAgreementAppendix();
	    ctAgreementAppendix.setAppendixType(new PmParameter());*/
		
	}
	
	public void deleteUserDefinedAppendix(){
	 /*	for(int i = 0; i < selectedAppendixs.length; i++) {
		    CtAgreementAppendix ctAgreementAppendix = selectedAppendixs[i];
			this.getManagedBean().getCtUserAppendixList().remove(ctAgreementAppendix);
		}selectedAppendixs = null;	*/
	}
	
    public void deleteAppendix(){
	/*	for(int i = 0; i < selectedAppendixs.length; i++) {
		    CtAgreementAppendix ctAgreementAppendix = selectedAppendixs[i];
		}selectedAppendixs = null;	*/
		
	}
    
    public void addSystemAppendix(){
    /*	CtAgreementAppendix ctAgreementAppendix = new CtAgreementAppendix();
		if(ctAgreementAppendix.getAppendixType() == null){
		   ctAgreementAppendix.setAppendixType(new PmParameter());
		   ctAgreementAppendix.setAppendixType(this.getManagedBean().getPmParameter());
      }this.getManagedBean().getCtSystemAppendixList().add(ctAgreementAppendix);*/
    }
    
    public void deleteSystemDefinedAppendix(){
	/*	for(int i = 0; i < selectedAppendixs.length; i++) {
		    CtAgreementAppendix ctAgreementAppendix = selectedAppendixs[i];
			this.getManagedBean().getCtSystemAppendixList().remove(ctAgreementAppendix);
		}selectedAppendixs = null;	*/
	}
    
	public void fileUpload(FileUploadEvent event) throws Exception {
		
		UploadedFile item = event.getFile();
		String fileName = item.getFileName();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    int i=0;
	 /*   for(CtAgreementAppendix ctAgreementAppendix : this.getManagedBean().getCtAgreement().getCtAgreementAppendixs()){
	    	if(ctAgreementAppendix.equals(this.getManagedBean().getCtAgreementAppendix())){
	    		this.getManagedBean().getCtAgreement().getCtAgreementAppendixs().get(i).setFilePath(filePath);
	    		this.getManagedBean().getCtAgreement().getCtAgreementAppendixs().get(i).setFileName(fileName);
	    	 }i++;
	     }*/
	    
	  /*  int i=0;
	    for(CtAgreementAppendix ctAgreementAppendix : this.getManagedBean().getCtUserAppendixList()){
	    	if(ctAgreementAppendix.equals(this.getManagedBean().getCtAgreementAppendix())){
	    		this.getManagedBean().getCtUserAppendixList().get(i).setFilePath(filePath);
	    		this.getManagedBean().getCtUserAppendixList().get(i).setFileName(fileName);
	    	 }i++;
	     }*/
	}
	
	public void uploadDocument(FileUploadEvent event) throws Exception{
		UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    this.getManagedBean().getCtAgreement().setFileName(item.getFileName());
	    this.getManagedBean().getCtAgreement().setFilePath(filePath);
	}
	
	public void downloadFile(String filePath) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		
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
	
	public void downloadDocFile(CtTemplate ctTemplate) throws IOException{
		
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		OutputStream out = null;
		String decoded ="";
		
		try{
		     out = res.getOutputStream();
		     fileName = ctTemplate.getName();
		     res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		     res.setHeader( "Content-Transfer-Encoding", "binary" );
		     res.setContentType("application/octet-stream");
		   // res.setContentType("Content-Type: application/docx");
		     res.flushBuffer();
		     decoded = html2text(new String(ctTemplate.getContent(), "UTF-8"));
			 byte[] content = decoded.getBytes();
			out.write(content);
		
		}catch(UnsupportedEncodingException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		}
	     
		out.close();
		out.flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	public void openNewWindow(AgreementDetails agreementDetails) throws IOException{
		 
	    IContractTemplateSO contractTemplateSO = null;
	    ContractTemplateReqDTO reqDTO = null;
	    ContractTemplateResDTO ResDTO = null;
		try {
			 this.getManagedBean().setFilePath(agreementDetails.getFilePath());
			 contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
			 reqDTO = new ContractTemplateReqDTO();
			 reqDTO.setTemplateId(30L);
		     ResDTO = contractTemplateSO.retrieveTemplateById(reqDTO);
		     this.getManagedBean().setCtTemplates(ResDTO.getCtTemplate());
	   }catch(Exception ex) {
			log.error("Exception>>", ex);
	   }finally{
		    contractTemplateSO = null;
	   }
	}
	
	public void closedWindow(){
		
	}
	
	public void downloadUploadFile() throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		String filePath = null;
		fileName = this.getManagedBean().getCtAgreement().getFileName() ;
		filePath = this.getManagedBean().getCtAgreement().getFilePath();
		
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
	
	
	public String fileNames(String filePath){
		 
	    if(filePath.equalsIgnoreCase("") ||filePath.equalsIgnoreCase(null) || filePath.equalsIgnoreCase(" "))
		  return null;
		
	    String fileName = null;
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	    return fileName;
	}/*End Bond Details*/
	
    public SelectItem[] getMinistryDropdown() {
		
		SelectItem[] items = null;     
		try{
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		    OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		      
			for(PmOrgProfile orgProfile : list) 
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());   
		 }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
	    return items;
	}
    
    public SelectItem[] getDepartmentDropdown() {
    	
		SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		   reqDTO.setParentOrgProfileId(this.getManagedBean().getMinistryProfileId());
		   OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		   List<PmOrgProfile> list = resDTO.getOrgProfileList();
		   items = new SelectItem[list.size()+1];
	       items[0] = new SelectItem(null, "- Select One -");
	       for(PmOrgProfile orgProfile : list) {
		       items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
	       }
		 }catch (Exception e) {
	    	   e.printStackTrace();
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	     }
	   return items;
	}
    
    public SelectItem[] getPtjCodeName(){
		
    	SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DIVISION.value());
			 
		   if(this.getManagedBean().getDepartmentProfileId() == null)
			  this.getManagedBean().setDepartmentProfileId(1L);
			  
		   reqDTO.setParentOrgProfileId(this.getManagedBean().getDepartmentProfileId());
		   OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		   List<PmOrgProfile> list = resDTO.getOrgProfileList();
		   items = new SelectItem[list.size()+1];
		   items[0] = new SelectItem(null, "- Select One -");
		   for(PmOrgProfile orgProfile : list) {
			   items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
		    }
	     }catch(Exception e) {
		    	e.printStackTrace();
		    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
		return items;
	}
	
    public void resetDepartment(){
    	
     }
    
    public void resetPtj(){
    	
    }
    
    public void reset(){
    	
    }
	
	public boolean isContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
	}
	
	public String convertTime(Date time){ 
        String str ="";
    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
        if(time == null)
        	return str;
        return format.format(time); 
    } 
	
	 private String getUserRole() {
			
		 if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		 else if(getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()))
			return RoleCode.APPROVER.value();
		 else 
			return null;
	}
	 
	 public void userGroupChangeListener() {
			
			List<String> source = new ArrayList<String>(); 
	    List<String> target = new ArrayList<String>();  
		
		this.getManagedBean().getApproverModel().setSource(source);
		this.getManagedBean().getApproverModel().setTarget(target);
	 }
		
	 public SelectItem[] getUserGroupDropdown() {
			
		SelectItem[] items = null;
		try{
			items = new SelectItem[getManagedBean().getUserGroupMap().values().size()];
			int i = 0;
			for(PmUserGroup userGroup : this.getManagedBean().getUserGroupMap().values()) 
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
		} catch(Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	 }/*End approval details*/
	
	 public void changeApendexType(){
/*//		flush();
//		try {
//			
//			String parameterCode = retrieveParameterById(this.getManagedBean().getParameterId());
//			if(parameterCode.equalsIgnoreCase("S")){
//				this.getManagedBean().setSysDefined(true);
//			}if(parameterCode.equalsIgnoreCase("U")){
//				this.getManagedBean().setUserDefined(true);
//			}
//		} catch (NamingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
*/	 }
	  
	 public String retrieveParameterObj(Long parameterId) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			return getParameterDesc(resDTO.getPmParameter());
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 } 
	 
	 public void flush(){
		this.getManagedBean().setUserDefined(false);
		this.getManagedBean().setSysDefined(false);
	 }
	 
	 public void addAgreementAppendex(){
		 
		/* for(int i=0; i<selectedAppendixs.length; i++){
			 CtAgreementAppendix stAgreementAppendix = selectedAppendixs[i];
			// this.getManagedBean().getCtAgreement().getCtAgreementAppendixs().add(stAgreementAppendix);
		 }selectedAppendixs = null;
		 if(this.getManagedBean().getCtUserAppendixList().size()>0){
			 this.getManagedBean().setCtUserAppendixList(null);
		 }
		 if(this.getManagedBean().getCtSystemAppendixList().size()>0){
			 this.getManagedBean().setCtSystemAppendixList(null);
		 }*/
	 }
	 
	 public String retrieveParameterById(Long parameterId) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			this.getManagedBean().setPmParameter(resDTO.getPmParameter());
			return resDTO.getPmParameter().getParameterCode();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 } 
	 
	 public String blobToStr(byte[] content) throws IOException {
		
		   String decoded ="";
		   if(content == null)
			   content = new byte[0];
		   try{
			  decoded = new String(content, "UTF-8");
		   }catch(UnsupportedEncodingException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	   return decoded;
	 }
	 
	 public List<AgreementDetails> getDetailsList() {
			
		      if(this.getManagedBean().getAgreementList() == null){
		    	  this.getManagedBean().setAgreementList(new ArrayList<AgreementDetails>());
		    	  List<AgreementDetails> agreementList = new ArrayList<AgreementDetails>();
		  		
		    	  AgreementDetails cover = new AgreementDetails();
		    	  cover.setFileName("AGREEMENT COVER PAGE");
		    	  cover.setFilePath("C:/projects/uploads/cover.docx");
		    	  cover.setFileType("System Define");
			  	  agreementList.add(cover);
			  		
			  	  AgreementDetails CLAUSE = new AgreementDetails();
			  	  CLAUSE.setFileName("AGREEMENT CLAUSE");
			  	  CLAUSE.setFilePath("C:/projects/uploads/clause.docx");
			  	  CLAUSE.setFileType("System Define");
			  	  agreementList.add(CLAUSE);
			  		
			  	  AgreementDetails signing = new AgreementDetails();
			  	  signing.setFileName("AGREEMENT SIGNING PAGE");
			  	  signing.setFilePath("C:/projects/uploads/signing.docx");
			  	  signing.setFileType("System Define");
			  	  agreementList.add(signing);
			  		
		    	  
		    	AgreementDetails Agr = new AgreementDetails();
		  		Agr.setFileName("SURAT SETUJU TERIMA");
		  		Agr.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr.setFileType("System Define");
		  		agreementList.add(Agr);
		  		
		  		AgreementDetails Agr1 = new AgreementDetails();
		  		Agr1.setFileName("PENGESAHAN SURAT SETUJU TERIMA");
		  		Agr1.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr1.setFileType("System Define");
		  		agreementList.add(Agr1);
		  		
		  		AgreementDetails Agr2 = new AgreementDetails();
		  		Agr2.setFileName("SURAT AKUAN PEMBIDA BERJAYA");
		  		Agr2.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr2.setFileType("System Define");
		  		agreementList.add(Agr2);
		  		
		  		AgreementDetails Agr3 = new AgreementDetails();
		  		Agr3.setFileName("TERMA-TERMA KONTRAK");
		  		Agr3.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr3.setFileType("System Define");
		  		agreementList.add(Agr3);
		  		
		  		AgreementDetails Agr4 = new AgreementDetails();
		  		Agr4.setFileName("JADUAL PEMBAYARAN");
		  		Agr4.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr4.setFileType("System Define");
		  		agreementList.add(Agr4);
		  		
		  		AgreementDetails Agr5 = new AgreementDetails();
		  		Agr5.setFileName("JADUAL SPESIFIKASI");
		  		Agr5.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr5.setFileType("System Define");
		  		agreementList.add(Agr5);
		  		
		  		AgreementDetails Agr6 = new AgreementDetails();
		  		Agr6.setFileName("JADUAL HARGA");
		  		Agr6.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr6.setFileType("System Define");
		  		agreementList.add(Agr6);
		  		
		  		AgreementDetails Agr7 = new AgreementDetails();
		  		Agr7.setFileName("JADUAL PENGHANTARAN");
		  		Agr7.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr7.setFileType("System Define");
		  		agreementList.add(Agr7);
		  		
		  		AgreementDetails Agr8 = new AgreementDetails();
		  		Agr8.setFileName("SKOP KERJA/PERKHIDMATAN");
		  		Agr8.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr8.setFileType("System Define");
		  		agreementList.add(Agr8);
		  		
		  		AgreementDetails Agr9 = new AgreementDetails();
		  		Agr9.setFileName("BON PELAKSANAAN");
		  		Agr9.setFilePath("C:/projects/uploads/agreement.docx");
		  		Agr9.setFileType("System Define");
		  		agreementList.add(Agr9);
		  		this.getManagedBean().getAgreementList().addAll(agreementList);
				}
				
				return this.getManagedBean().getAgreementList();
	}
	
	public CtAgreementParty[] getSelectedCtAgreementParty() {
	  return selectedCtAgreementParty;
	}

	public void setSelectedCtAgreementParty(
			CtAgreementParty[] selectedCtAgreementParty) {
		this.selectedCtAgreementParty = selectedCtAgreementParty;
	}

	public boolean isSupplementValue() {
		return supplementValue;
	}

	public void setSupplementValue(boolean supplementValue) {
		this.supplementValue = supplementValue;
	}

	public AgreementDetails getAgreementDetails() {
		return agreementDetails;
	}
	
	public void setAgreementDetails(AgreementDetails agreementDetails) {
		this.agreementDetails = agreementDetails;
	}

	public CtAgreementParty[] getSelectedSignSuppliers() {
		return selectedSignSuppliers;
	}

	public void setSelectedSignSuppliers(CtAgreementParty[] selectedSignSuppliers) {
		this.selectedSignSuppliers = selectedSignSuppliers;
	}

	public CtAgreementParty[] getSelectedSignWitness() {
		return selectedSignWitness;
	}

	public void setSelectedSignWitness(CtAgreementParty[] selectedSignWitness) {
		this.selectedSignWitness = selectedSignWitness;
	}

	/*public CtAgreementAppendix[] getSelectedAppendixs() {
		return selectedAppendixs;
	}

	public void setSelectedAppendixs(CtAgreementAppendix[] selectedAppendixs) {
		this.selectedAppendixs = selectedAppendixs;
	}*/
}
