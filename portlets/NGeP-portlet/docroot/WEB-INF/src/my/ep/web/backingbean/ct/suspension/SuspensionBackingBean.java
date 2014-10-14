/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspension;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Suspension Details
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "suspensionBackingBean")
@RequestScoped
public class SuspensionBackingBean extends BaseBackingBean<SuspensionManagedBean> {
	
	    private ContractBackingBean ctBB = null;
	    private static Logger log = Logger.getLogger(SuspensionBackingBean.class);
	    private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	
	    public SuspensionBackingBean(){
	           if(this.getManagedBean().getAction() == null) 
	        	   this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		
	           
	           if(ctBB == null){
	  			 ctBB = new ContractBackingBean(this.getManagedBean().getFileDirectory());
	  			 ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
	  		}
	    }
	 
	   private FacesScopes getFacesScopes(){
		      FacesScopes facesScopes = new FacesScopes();
		      return facesScopes;
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
					  
					  String[] outcome = new String[res.getAction().length + 1];
				      outcome[0] = BpmOutcomeConst.BPM_SAVE;
				      int i = 1;
				      for (String action : res.getAction()) {
					     outcome[i++] = action;
				      }
				      
					  getManagedBean().setTaskName(res.getTaskName());
					  getManagedBean().setAction(outcome);
					  getManagedBean().setTaskId(taskId);
						
					  ContractReqDTO ctReqDTO = new ContractReqDTO();
					  ctReqDTO.setUpdateAction(false);
					  CTContractSuspensionDataType dataType = (CTContractSuspensionDataType)obj;
					  ctReqDTO.setContractId(new Long(dataType.getContractId()));
					  ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
					  
			          IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			          ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			          setSuspensionContractVer(contractSO.retrieveContractVerDtls(ctReqDTO));
			          
	     } catch(Exception e){
			  e.printStackTrace();
		 }
	 }
	 
	 public void setSuspensionContractVer(ContractResDTO resDTO){
		 
		    getManagedBean().setContractVer(resDTO.getCtContractVer());	
			getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
			if(getManagedBean().getContractVer().getCtContractItems() != null && getManagedBean().getContractVer().getCtContractItems().size() > 0 )
				Collections.sort(getManagedBean().getContractVer().getCtContractItems(), new CtContractItem());	
		    if(getManagedBean().getContractVer().getCtAgencies() != null && getManagedBean().getContractVer().getCtAgencies().size() > 0 ) 
			 	 Collections.sort(getManagedBean().getContractVer().getCtAgencies(), new CtAgency());
			 
		    getManagedBean().setRemarkList(resDTO.getCtRemarks());
		    getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		    getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
			    getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
		    
		    ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	    
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	  }
	
	 public String backAction() {
			return CONTRACT_MAINT_PAGE;
	 }
	 
	 public void setSuspensionStatusValue(CtContractItem item) {		 
		 for(CtContractItem ctItem :this.getManagedBean().getContractVer().getCtContractItems()) {
			 if(ctItem.equals(item)) {		
				 if(ctItem.getRecordStatusFlg().intValue() == 1){
				      ctItem.setEffDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				      ctItem.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
				 } else {
					 ctItem.setEffDate(getCurrentDate());
				 }
			 }
		 }
	 }
	 
	 public void setSuspensionAgencyStatusValue(CtAgency agency){		 
		 for(CtAgency ctAgency :this.getManagedBean().getContractVer().getCtAgencies()) {
			 if(ctAgency.equals(agency)) {
				 if(ctAgency.getRecordStatusFlg().intValue() == 1){
				      ctAgency.setEffDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				      ctAgency.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			     } else {
				      ctAgency.setEffDate(getCurrentDate());
			     }
			 }
		 }  
	 }
	 
	 public String saveContractSuspension(String action){
		  
		 IContractSO contractSO = null;
		 ContractReqDTO reqDTO = null;
		 try{
			 
			     validateSuspensionDetails(action);		
				 contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		       
				 String token = getFacesScopes().getBPMAuthenticationCtxToken();
				 reqDTO = new ContractReqDTO();
		         reqDTO.setToken(token);
		         reqDTO.setOutcome(action);
		         reqDTO.setTaskId(this.getManagedBean().getTaskId());
		         this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
		             
		         reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
		         reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		         reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		         reqDTO.setUserRole(getUserRole());
	             reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			     reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			     ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			  
			     if(resDTO.isSuccessful()) {
					 log.info("Record saved successfully");
					 String messageId = MessageConst.INFO_700005;
					 if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					     messageId = MessageConst.INFO_700025;
				    } else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						 messageId = MessageConst.INFO_700009;
					 } else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					 } else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						messageId = MessageConst.INFO_700008;
					 }						
					 FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
									this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				      if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
						   renewSesionObject();
						   return CommonUIConst.ACKNOWLEDGE_PAGE;
					  }
				      else {	
				    	  ContractReqDTO ctReqDTO = new ContractReqDTO();
				    	  ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				    	  ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
					    	
				    	  ContractResDTO ctResDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
				    	  setSuspensionContractVer(ctResDTO);
				      }
		   	     } else {
						throw new EpException("Failed.");
				  }
			  }catch (Exception e) {
				log.error("Exception>>", e);
				e.printStackTrace();
				if(e instanceof EpException) {
					EpException ep = (EpException) e;
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
				}
			  }finally {
				contractSO = null;
			  }
		   return null;
	 } 
	 
	 public void validateSuspensionDetails(String outcome){
		
		 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			 CtRemark remark = getManagedBean().getRemarkList().get(0);
			 if(remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")){
				 throw new EpException(MessageConst.ERROR_100022, new String[]{"Remarks"});
			 }
	      }
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
		else
			return null;
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
	
	 public Date getCurrentDate() {
	        return new Date();
	    }
}
