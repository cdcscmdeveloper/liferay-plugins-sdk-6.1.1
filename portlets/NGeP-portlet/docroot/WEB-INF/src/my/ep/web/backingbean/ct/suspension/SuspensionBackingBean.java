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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
import my.ep.constants.CommonConstants;
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
import my.ep.web.constant.ct.ContractManagementConst;
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
	
	 private static Logger log = Logger.getLogger(SuspensionBackingBean.class);
	 private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	
	 private ContractBackingBean ctBB = null;
	 private List<CtContractItem> ctContractItems;
	
	 public SuspensionBackingBean(){
	  
		 if(this.getManagedBean().getAction() == null) {
	    	this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SUBMIT});	
	    }
	    
	    if(ctBB == null){
			  ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
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
			if(taskId==null) {
				return;
			}
				
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			ctReqDTO.setUpdateAction(false);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setAction(res.getAction());
			this.getManagedBean().setTaskId(taskId);
			CTContractSuspensionDataType dataType = (CTContractSuspensionDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0){
				 for(CtContractItem ctContractItem : this.getManagedBean().getContractVer().getCtContractItems()){
					 if(ctContractItem.isRecordStatus()){
						 ctContractItem.setRecordStatusFlg(1);
					 } else{
						 ctContractItem.setRecordStatusFlg(0);
					 }
				 }
			}
			
			if(this.getManagedBean().getContractVer().getCtAgencies().size() > 0){
				for(CtAgency ctAgency : this.getManagedBean().getContractVer().getCtAgencies()){
					 if(ctAgency.isRecordStatus())
						 ctAgency.setRecordStatusFlg(1);
					 else
						 ctAgency.setRecordStatusFlg(0);
				   }
			 }
			
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	    
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_SUSPENSION);
		      
	    } catch(Exception e){
			e.printStackTrace();
		}
	 }
	 
	 public String backAction() {
			return CONTRACT_MAINT_PAGE;
	 }
	 
	 public void setSuspensionStatusValue(CtContractItem item){
		 
		 for(CtContractItem ctitems :this.getManagedBean().getContractVer().getCtContractItems())
		 if(ctitems.equals(item)){
			 if(item.getRecordStatusFlg() == 0){
			   ctitems.setRecordStatus(false);
		    }else{
		    	ctitems.setRecordStatus(true);
		    	item.setRecordStatusFlg(1);
		    	ctitems.setEffDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
		    	ctitems.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
		    }
		 }
	 }
	 
    public void setSuspensionAgencyStatusValue(CtContractItem agency){
		 
		 for(CtAgency ctagency :this.getManagedBean().getContractVer().getCtAgencies()){
		 if(ctagency.equals(agency)){
			 if(ctagency.getRecordStatusFlg() == 1){
				 ctagency.setEffDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
				 ctagency.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getEffDate());
		    }
		 }
	   }  
    }
	 public String saveContractSuspension(String action){
		  
		 IContractSO contractSO = null;
		 ContractReqDTO reqDTO = null;
		 try{
			  validateSuspensionDetails(action);		
			  reqDTO = new ContractReqDTO();
			  String token = getFacesScopes().getBPMAuthenticationCtxToken();
					
			  contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		   
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
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
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
		   	     }else {
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
				 throw new EpException(MessageConst.INFO_100022, new String[]{"Remarks"});
			 }
	      }
	 }
	 
	 public void validateAgencyDate(CtAgency agency){
		 
		 if (agency.getExpDate().before(agency.getEffDate()) 
				 || agency.getExpDate().before(new Date()) || agency.getEffDate().before(new Date())) {
			 for (CtAgency dataList : this.getManagedBean().getContractVer().getCtAgencies()) {
					
				 if(agency.getEffDate().before(new Date())){
					 dataList.setDateLessThanCurDate(true);
					 dataList.setValidateScheduleDate(false); 
				
				 } else if(dataList.toString().equalsIgnoreCase(agency.toString())){
						dataList.setValidateScheduleDate(true); 
						dataList.setDateLessThanCurDate(false); 
					}
		     }
		 }else{
			 for(CtAgency dataList : this.getManagedBean().getContractVer().getCtAgencies()) {
				    if(dataList.toString().equalsIgnoreCase(agency.toString())){
				        dataList.setValidateScheduleDate(false);
				        dataList.setDateLessThanCurDate(false); 
				    }
				 }
		 }
	}
	 
   public void validateItemDate(CtContractItem ctItems){
		   
	    
	      if(ctItems.getExpDate().before(ctItems.getEffDate()) || 
	    	 ctItems.getEffDate().before(new Date()) || ctItems.getExpDate().before(new Date())) {
			 for(CtContractItem dataList : this.getManagedBean().getContractVer().getCtContractItems()) {
			  
				  if(ctItems.getEffDate().before(new Date())){
					  dataList.setDateLessThanCurDate(true);
					  dataList.setValidateScheduleDate(false); 
				 
				  } else if (dataList.toString().equalsIgnoreCase(ctItems.toString())){
						dataList.setValidateScheduleDate(true); 
					    dataList.setDateLessThanCurDate(false); 
				 }
		     }
		 }else{
			 for(CtContractItem dataList : this.getManagedBean().getContractVer().getCtContractItems()) {
				    if(dataList.toString().equalsIgnoreCase(ctItems.toString())){
				        dataList.setValidateScheduleDate(false);
				        dataList.setDateLessThanCurDate(false); 
				    }
				 }
		 }
	}

	 
	 public String convertTime(Date time){ 
	        String str ="";
	    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
	        if(time == null)
	        	return str;
	        return format.format(time); 
	    } 
	 
	 public SelectItem[] getItemStatusDropdown() {
			
			SelectItem[] items = null;     
			try{
			    items = new SelectItem[2];
			    items[0] = new SelectItem(0, "Inactive");
			    items[1] = new SelectItem(1, "Active");  
			 }catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			 }
		    return items;
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

	public List<CtContractItem> getCtContractItems() {
		return ctContractItems;
	}

	public void setCtContractItems(List<CtContractItem> ctContractItems) {
		this.ctContractItems = ctContractItems;
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
				out.write( buffer, 0, bytesRead );
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
}
