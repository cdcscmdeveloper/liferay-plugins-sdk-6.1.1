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
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.ct.template.CtTemplate;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.agreement.AgreementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
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
	private CtAgreementParty[] selectedAgencyWitnes;
	private CtAgreementParty[] selectedAgencySigner;
	private CtAgreementParty[] selectedSupplierWitnes;
	private CtAgreementParty[] selectedSupplierSigner;

	public AgreementBackingBean(){
	   if(this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
	   }
	   if(getManagedBean().getUserMap().size()==0) {
			 initializeApproverList();
	    }
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void init(){
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());	
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
			this.getManagedBean().setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setAction(res.getAction());
			
			ContractAgreementReqDTO ctReqDTO = new ContractAgreementReqDTO();
			CTAgreementDataType dataType = (CTAgreementDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setAgreementId(new Long(dataType.getAgreementId()));
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
					 this.getManagedBean().setContractVer(conResDTO.getContract().getCurrentContractVer());
					// this.getManagedBean().getCtAgreement().setCtAmendment(conResDTO.getContract().getCurrentContractVer().getCtAmendment());
			    	
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
			  
			   this.getManagedBean().setCtContract(resDTO.getAgreement().getCtContract());
			   this.getManagedBean().setContractVer(resDTO.getAgreement().getCtContract().getCurrentContractVer());
			   if(this.getManagedBean().getCtContract().getFulfillmentType() == null){
			     this.getManagedBean().getCtContract().setFulfillmentType(new PmParameter());
			   }
			
			   this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			   this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			   ContractBackingBean ctBB = new ContractBackingBean();
			   ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			   if(this.getManagedBean().getCtAgencySigner().size()>0)
				  this.getManagedBean().getCtAgencySigner().clear();
			
			   if(this.getManagedBean().getCtSupplierSigner().size()>0)
				  this.getManagedBean().getCtSupplierSigner().clear();
			
			   if(this.getManagedBean().getCtSignAgencyWitness().size()>0)
				  this.getManagedBean().getCtSignAgencyWitness().clear();
			   
			   if(this.getManagedBean().getCtSupplierWitness().size()>0)
					  this.getManagedBean().getCtSupplierWitness().clear();
			
			   for(CtAgreementParty ctAgreementParty : this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties()){
				  if(ctAgreementParty.getAgreementPartyType().getParameterId().equals(retrieveParameterByCodeType(
					 ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_SIGNER).getParameterId())){  
					this.getManagedBean().getCtAgencySigner().add(ctAgreementParty);
				  }else if(ctAgreementParty.getAgreementPartyType().getParameterId() .equals(retrieveParameterByCodeType(
					ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_WITNESS).getParameterId())){
					   this.getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);
				 }else if(ctAgreementParty.getAgreementPartyType().getParameterId() .equals(retrieveParameterByCodeType(
					ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER).getParameterId())){
					   this.getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
				 }else{
					 this.getManagedBean().getCtSupplierWitness().add(ctAgreementParty);
				 }
			  }
		   }
			
		   this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			   this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			   this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			} 
		    
		    initializeRemarks();
		    ContractBackingBean ctBB = new ContractBackingBean();
		    ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
		  
		 	  if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FINALIST_AGREEMENT_HT_NAME) ||
				  res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPLOAD_SIGNED_STAMPED_FINAL_AGREEMENT)){
				  this.getManagedBean().setUploadFinalAgreement(true);
			   }
			  FacesContext fc = FacesContext.getCurrentInstance();
			  fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_AGREEMENT);   
		 
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
			
			if(this.getManagedBean().getAgreementType().equalsIgnoreCase("supplemental"))
				reqDTO.setAgreementType("supplemental");
			else
			    reqDTO.setAgreementType("agreement");
			
			reqDTO.setToken(token);
			reqDTO.setOutcome(action);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setLoginPmUSer(getFacesScopes().getUserSessionBean());
			reqDTO.setDocType(ContractConst.DOC_TYPE_AGREEMENT);
			
			if(this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().size() > 0)
				this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().clear();
			this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().addAll(this.getManagedBean().getCtAgencySigner());
			this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSupplierSigner());
			this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSignAgencyWitness());
			this.getManagedBean().getCtContract().getCurrentAgreement().getCtSignParties().addAll(this.getManagedBean().getCtSupplierWitness());
		 
			this.getManagedBean().getCtContract().getCurrentContractVer().setDocType(ContractConst.DOC_TYPE_AGREEMENT);
      		this.getManagedBean().getCtContract().getCurrentAgreement().setCtContract(this.getManagedBean().getCtContract());
            
      		if(isApprover()) {
            	this.getManagedBean().getCtContract().getCurrentContractVer().setApprover(getFacesScopes().getUserSessionBean());
            	this.getManagedBean().getCtContract().getCurrentContractVer().setApprovedDate(new Date());
			}
      		
      		reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
 		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
 		    reqDTO.setUserMap(getManagedBean().getUserMap());
 		    reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
 			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
	        
			reqDTO.setAgreement(this.getManagedBean().getCtContract().getCurrentAgreement());
			ContractAgreementResDTO resDTO = agreementSO.saveAgreement(reqDTO);
			
			 if(resDTO.isSuccessful()) {
				 log.info("Record saved successfully");
				 String messageId = MessageConst.INFO_700005;
				 if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				 }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						messageId = MessageConst.INFO_700009;
				 }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
				 }
				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
					MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getCtContract().getCurrentContractVer().getDocType()), 
					this.getManagedBean().getCtContract().getContractNo()}, 
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { } 
				  }else {
						throw new EpException("Failed.");
				  }
			  }catch (Exception e) {
				log.error("Exception>>", e);
				e.printStackTrace();
				if(e instanceof EpException) {
					EpException ep = (EpException) e;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ep.getMessage(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				}else{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				}
			  }finally {
				  agreementSO = null;
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
	
    public SelectItem[] getDepartmentDropdown(Long ministryId) {
    	
		SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		   reqDTO.setParentOrgProfileId(ministryId);
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
    
    public SelectItem[] getPtjCodeName(Long departmentId){
		
    	SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DIVISION.value());
			 
		   if(departmentId == null)
			   departmentId = 1L;
			  
		   reqDTO.setParentOrgProfileId(departmentId);
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
    
    public SelectItem[] getContractSignerDropdown(CtAgreementParty  agreementParty){
    	SelectItem[] items = null;     
		try{
		   int i = 1;
		   IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		   UserReqDTO reqDTO = new UserReqDTO();
		   reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());	
		 //  reqDTO.setOrgProfileId(this.getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		   UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
		   List<PmUser> list = resDTO.getUserList();
		   items = new SelectItem[list.size()+1];
	       items[0] = new SelectItem(null, "- Select One -");
	       for(PmUser pmUser : list) {
		       items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
	       }
		 }catch (Exception e) {
	    	   e.printStackTrace();
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	     }return items;
    }
	
    public void resetDepartment(Long id){
    	this.getManagedBean().setDepartmentProfileId(id);
     }
    
    public void resetPtj(){
    	
    }
    
public void fileUpload(FileUploadEvent event) throws Exception {
		
		UploadedFile item = event.getFile();
		String fileName = item.getFileName();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    int i=0;
	    for(CtAgreementContent agreementContent : this.getManagedBean().getCtContract().getCurrentAgreement().getAgreementContents()){
	    	if(agreementContent.equals(this.getManagedBean().getSelectedAgreementContent())){
	    		this.getManagedBean().getCtContract().getCurrentAgreement().getAgreementContents().get(i).setFilePath(filePath);
	    		this.getManagedBean().getCtContract().getCurrentAgreement().getAgreementContents().get(i).setFileName(fileName);
	    	 }i++;
	     }
	}

    public void uploadStampedPage(FileUploadEvent event) throws Exception{
    	UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		this.getManagedBean().getCtContract().getCurrentAgreement().setStampedPageName(item.getFileName());
	    this.getManagedBean().getCtContract().getCurrentAgreement().setStampedPagePath(filePath);
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
    }
	
	public void uploadSignedPage(FileUploadEvent event) throws Exception{
		UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		this.getManagedBean().getCtContract().getCurrentAgreement().setSignedPageName(item.getFileName());
	    this.getManagedBean().getCtContract().getCurrentAgreement().setSignedPagePath(filePath);
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
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
		IContractTemplateSO contractTemplateSO = null;
		String fileName = "";
		try{
			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			reqDTO.setTemplateName(ContractConst.TEMPLATE_AGREEMENT_COVER_PAGE);
			ContractTemplateResDTO resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
			String content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
			
			if(resDTO.getCtTemplate()!=null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				Map<String, String> valuesMap = new HashMap<String, String>();
				
				valuesMap.put("CONTRACT_TITLE", getManagedBean().getCtContract().getQuotationTender().getQtTitle());
				valuesMap.put("SUPPLIER_NAME", getManagedBean().getCtContract().getSmSupplier().getCompanyName());
				valuesMap.put("SUPPLIER_REGISTRATION_NO", getManagedBean().getCtContract().getSmSupplier().getRegNo());
				valuesMap.put("PHYSICAL_CONTRACT_NO", getManagedBean().getCtContract().getContractPhysicalNo());
				valuesMap.put("CONTRACT_START_DATE", df.format(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getEffDate()));
				valuesMap.put("CONTRACT_END_DATE", df.format(getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getExpDate()));
				valuesMap.put("CONTRACT_PERIOD", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getContractPeriod() + "");
				valuesMap.put("CONTRACT_VALUE", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getContractAmount()+ "");
				//valuesMap.put("CONTRACT_VALUE_TEXT", getManagedBean().getCtContract().getCurrentContractVer());
				//valuesMap.put("PERFORMANCE_BOND_VALUE", getManagedBean().getCtContract());
				valuesMap.put("PERFORMANCE_BOND_VALUE_TEXT", getManagedBean().getCtContract().getContractPhysicalNo());
				
				StrSubstitutor sub = new StrSubstitutor(valuesMap);
				content = sub.replace(content);
			}
			fileName = "AgreementCoverPage.docx";
		     			
		 	WordprocessingMLPackage wordMLPackage;
		 	try {
		 		wordMLPackage = WordprocessingMLPackage.createPackage();
		 		AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
		 		afiPart.setBinaryData(content.getBytes());
		 		afiPart.setContentType(new ContentType("text/html"));
		 		Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
		 		 
		 		CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
		 		ac.setId(altChunkRel.getId() );
		 		wordMLPackage.getMainDocumentPart().addObject(ac);
		 		// .. content type
		 		wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");
		 		wordMLPackage.save(new java.io.File("C:/downloads/" + fileName));
		 	} catch (InvalidFormatException e) {
		 		e.printStackTrace();
		 	}catch (Docx4JException e) {
	 			e.printStackTrace();
	 		}
		
		}catch(UnsupportedEncodingException e) {
			  e.printStackTrace();
		}
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		
		String filePath = "C:/downloads/" + fileName;
		if(filePath != null) {
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
	
	private String formAddress(String address1, String address2, String address3, String address4) {
		StringBuffer address = new StringBuffer();
		if(address1!=null && address1.length()>0) {
			address.append(address1).append(" ");
		}
		if(address2!=null && address2.length()>0) {
			address.append(address2).append(" ");
		}
		if(address3!=null && address3.length()>0) {
			address.append("<BR/>").append(address3).append(" ");
		}
		if(address4!=null && address4.length()>0) {
			address.append(address4).append(" ");
		}
		return address.toString();
	}
	
	public void openNewWindow() throws IOException{
		 
	    /*IContractTemplateSO contractTemplateSO = null;
	    ContractTemplateReqDTO reqDTO = null;
	    ContractTemplateResDTO ResDTO = null;
		try {
			 //this.getManagedBean().setFilePath(agreementDetails.getFilePath());
			 contractTemplateSO = (IContractTemplateSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractTemplateSO.class);
			 reqDTO = new ContractTemplateReqDTO();
			 reqDTO.setTemplateId(agreementContent);
		     ResDTO = contractTemplateSO.retrieveTemplateById(reqDTO);
		     this.getManagedBean().setCtTemplates(ResDTO.getCtTemplate());
	   }catch(Exception ex) {
			log.error("Exception>>", ex);
	   }finally{
		    contractTemplateSO = null;
	   }
		agreementDetails.getFileType().;ParameterConst.agreementTemplateDoc;*/
		//agreementContent.getTitle();
		downloadDocFile(new CtTemplate());
	}
	
	public void closedWindow(){
		
	}
	
	public void downloadUploadFile() throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		String filePath = null;
		/*fileName = this.getManagedBean().getCtAgreement().getFileName() ;
		filePath = this.getManagedBean().getCtAgreement().getFilePath();*/
		
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
	
	 public PmParameter retrieveParameterByCodeType(String parameterType, String parameterCode) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(parameterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 } 
	 
	 public void addAgreementContents(){
		 try {
		       CtAgreementContent contents = new CtAgreementContent();
		       if(contents.getContentType() == null){
			       contents.setContentType(new PmParameter());
			       contents.setContentType(retrieveParameterByCodeType(ParameterConst.DOCUMENT_DEFINITION_TYPE, ParameterConst.AGREEMENT_USER_UPLOAD_DOC));
		       }this.getManagedBean().getCtContract().getCurrentAgreement().getAgreementContents().add(contents);
		 } catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }
	 
	 public void delAgreementContents(){
		 this.getManagedBean().getCtContract().getCurrentAgreement().getAgreementContents().remove(this.getManagedBean().getSelectedAgreementContent());
	 }
	 
	 public void addAgencySignerParty(){
		try{
			 CtAgreementParty ctAgreementParty = new CtAgreementParty();
			 ctAgreementParty.setAgreementPartyType(new PmParameter());
		     ctAgreementParty.getAgreementPartyType().setParameterId(retrieveParameterByCodeType(ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_SIGNER).getParameterId());
			 if(ctAgreementParty.getOfficer() == null)
				ctAgreementParty.setOfficer(new PmUser());
			 if(ctAgreementParty.getOrgProfile() == null){
			   ctAgreementParty.setOrgProfile(new PmOrgProfile());
				if(ctAgreementParty.getOrgProfile().getParentOrgProfile() == null){
				   ctAgreementParty.getOrgProfile().setParentOrgProfile(new PmOrgProfile());
				}
			 }this.getManagedBean().getCtAgencySigner().add(ctAgreementParty);
		  }catch (NamingException e) {
 			  e.printStackTrace();
		  }
	  }
		
		public void deleteAgencySignerParty(){
			for(int i = 0; i < selectedAgencySigner.length; i++) {
				CtAgreementParty ctAgreementParty = selectedAgencySigner[i];
				this.getManagedBean().getCtAgencySigner().remove(ctAgreementParty);
			}selectedAgencySigner = null;	
		}
		
		public void addSupplierSigner(){
			try{
			    CtAgreementParty ctAgreementParty = new CtAgreementParty();
			    ctAgreementParty.setAgreementPartyType(new PmParameter());
		        ctAgreementParty.getAgreementPartyType().setParameterId(retrieveParameterByCodeType(ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_SUPPLIER_SIGNER).getParameterId());
			    if(ctAgreementParty.getOfficer() == null)
			       ctAgreementParty.setOfficer(new PmUser());
		        this.getManagedBean().getCtSupplierSigner().add(ctAgreementParty);
			} catch (NamingException e){
				e.printStackTrace();
			}
		}
		
		public void deleteSupplierSigner(){
			for(int i = 0; i < selectedSupplierSigner.length; i++) {
				CtAgreementParty ctAgreementParty = selectedSupplierSigner[i];
				this.getManagedBean().getCtSupplierSigner().remove(ctAgreementParty);
			}selectedSupplierSigner = null;	
		}
		
		public void addSupplierWitness(){
			try{
			    CtAgreementParty ctAgreementParty = new CtAgreementParty();
		        ctAgreementParty.setAgreementPartyType(new PmParameter());
		        ctAgreementParty.getAgreementPartyType().setParameterId(retrieveParameterByCodeType(ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_SUPPLIER_WITNESS).getParameterId());
			    this.getManagedBean().getCtSupplierWitness().add(ctAgreementParty);
			} catch (NamingException e){
				e.printStackTrace();
			}
		}
		
		public void deleteSupplierWitness(){
			for(int i = 0; i < selectedSupplierWitnes.length; i++) {
				CtAgreementParty ctAgreementParty = selectedSupplierWitnes[i];
				this.getManagedBean().getCtSupplierWitness().remove(ctAgreementParty);
			}selectedSupplierWitnes = null;	
		}
		
		public void addAgencyWitnes(){
			try{
			    CtAgreementParty ctAgreementParty = new CtAgreementParty();
			    ctAgreementParty.setAgreementPartyType(new PmParameter());
		        ctAgreementParty.getAgreementPartyType().setParameterId(retrieveParameterByCodeType(ParameterTypeConst.AGREEMENT_PARTY_TYPE, ParameterTypeConst.AGREEMENT_AGENCY_WITNESS).getParameterId());
				this.getManagedBean().getCtSignAgencyWitness().add(ctAgreementParty);
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void deleteAgencyWitnes(){
			for(int i = 0; i < selectedAgencyWitnes.length; i++) {
				CtAgreementParty ctAgreementParty = selectedAgencyWitnes[i];
				this.getManagedBean().getCtSignAgencyWitness().remove(ctAgreementParty);
			}selectedAgencyWitnes = null;
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
	
	 public String blobToStr(byte[] content) throws IOException {	
		 String decoded ="";
		 try{
		     if(content == null)
			   content = new byte[0];
		     decoded = new String(content, "UTF-8");
		   }catch(UnsupportedEncodingException e) {
			  e.printStackTrace();
		  }
	   return decoded;
	 }
	 
	public String fileNames(String filePath){
		if(filePath.equalsIgnoreCase("") ||filePath.equalsIgnoreCase(null) || filePath.equalsIgnoreCase(" "))
		return null;
			
		String fileName = null;
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		return fileName;
	}/*End Bond Details*/
		
	public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}
	 
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	private List<String> getAvailableApproverList() {
		IUserSO userSO = null;
		List<String> sourceList = new ArrayList<String>();
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();

			if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
				reqDTO.setOrgProfileId(getFacesScopes().getUserSessionBean().getPmUserOrgs().get(0).getPmOrgProfile().getOrgProfileId());
				// TODO hardcode to get the value, should remove after have real data
				reqDTO.setOrgProfileId(1003L);
			} else { // Central Contract	
				reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
				reqDTO.setOrgType(getFacesScopes().getUserSessionBean().getPmUserOrgs().get(0).getPmOrgProfile().getOrgTypeCode().getParameterCode());
				// TODO hardcode to get the value, should remove after have real data
				reqDTO.setOrgType("RSH");
			}

			UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
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
	
    public void setUserValueAgency(CtAgreementParty ctParty,Long userId){
		
		for(CtAgreementParty party : this.getManagedBean().getCtAgencySigner()){
			if(party.equals(ctParty)){
			   try{
				    IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				    UserReqDTO reqDTO = new UserReqDTO();
				    reqDTO.setUserId(userId);
				    UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
				    party.setOfficerIc(resDTO.getPmUser().getIdentificationNo());
				    party.setOfficerPosition(resDTO.getPmUser().getDesignation());
			   }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		       }
			}
		}
	}

	public void setUserValue(CtAgreementParty ctParty,Long userId){
		
		for(CtAgreementParty party : this.getManagedBean().getCtSupplierSigner()){
			if(party.equals(ctParty)){
			   try{
				    IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				    UserReqDTO reqDTO = new UserReqDTO();
				    reqDTO.setUserId(userId);
				    UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
				    party.setOfficerIc(resDTO.getPmUser().getIdentificationNo());
				    party.setOfficerPosition(resDTO.getPmUser().getDesignation());
			   }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		       }
			}
		}
	}
	
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCtContract().getCurrentContractVer().getSigningAgRemark());
		
	}
		
	private void initiateRemark(List<CtRemark> remarks) {
		if(remarks.size()==0 || !compareUserName(remarks.get(0).getCreatedUser().getLoginId())) {
		  CtRemark remark = new CtRemark();
		  remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
		  remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		  remark.setChangedBy(remark.getCreatedBy());
		  remark.setChangedDate(remark.getCreatedDate());
		  remark.setCreatedUser(getFacesScopes().getUserSessionBean());
		  remarks.add(0, remark);
		}
	}
	
    public boolean isSupplementValue() {
		return supplementValue;
	}

	public void setSupplementValue(boolean supplementValue) {
		this.supplementValue = supplementValue;
	}

	public CtAgreementParty[] getSelectedAgencyWitnes() {
		return selectedAgencyWitnes;
	}

	public void setSelectedAgencyWitnes(CtAgreementParty[] selectedAgencyWitnes) {
		this.selectedAgencyWitnes = selectedAgencyWitnes;
	}

	public CtAgreementParty[] getSelectedAgencySigner() {
		return selectedAgencySigner;
	}

	public void setSelectedAgencySigner(CtAgreementParty[] selectedAgencySigner) {
		this.selectedAgencySigner = selectedAgencySigner;
	}

	public CtAgreementParty[] getSelectedSupplierWitnes() {
		return selectedSupplierWitnes;
	}

	public void setSelectedSupplierWitnes(CtAgreementParty[] selectedSupplierWitnes) {
		this.selectedSupplierWitnes = selectedSupplierWitnes;
	}

	public CtAgreementParty[] getSelectedSupplierSigner() {
		return selectedSupplierSigner;
	}

	public void setSelectedSupplierSigner(CtAgreementParty[] selectedSupplierSigner) {
		this.selectedSupplierSigner = selectedSupplierSigner;
	}
	
}
