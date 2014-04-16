/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspension;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.search.ParseException;
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
	 private List<CtContractItem> ctFilterItems;
	 private List<CtContractItem> ctContractItems;
	 public SuspensionBackingBean(){
	    if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {"SAVE","SUBMIT"});
	    }
	 }
	 
     @PostConstruct
	 public void initializeContractValue() throws ParseException {
		 ContractBackingBean ctSuspBB = new ContractBackingBean();
		 ctSuspBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
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
			this.getManagedBean().setTaskId(taskId);
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
			
			CTContractSuspensionDataType dataType = (CTContractSuspensionDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			this.getManagedBean().setUserLoginId(dataType.getContractAdmin());
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0){
				 for(CtContractItem ctContractItem : this.getManagedBean().getContractVer().getCtContractItems()){
					 if(ctContractItem.isRecordStatus())
					    ctContractItem.setChkStatusId(1);
				    else
					 ctContractItem.setChkStatusId(0);
				 }
			}
			
			if(this.getManagedBean().getContractVer().getCtAgencies().size() > 0){
				for(CtAgency ctAgency : this.getManagedBean().getContractVer().getCtAgencies()){
					 if(ctAgency.isRecordStatus())
						 ctAgency.setChkStatusId(1);
				    else
				    	ctAgency.setChkStatusId(0);
				 }
			}
			
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());  
			
			String decoded = new String(this.getManagedBean().getContractVer().getCtContractValue().getSuspendActiveLetter(), "UTF-8");
			this.getManagedBean().setEditorValue(decoded);
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_SUSPENSION);
		      
	    } catch(Exception e){
			e.printStackTrace();
		}
	 }
	 
	 public String saveContractSuspension(String action){
		  
		 IContractSO contractSO = null;
		 ContractReqDTO reqDTO = null;
		 try{
					
			  reqDTO = new ContractReqDTO();
			  String token = getFacesScopes().getBPMAuthenticationCtxToken();
					
			  contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
		   
		      reqDTO.setToken(token);
		      reqDTO.setOutcome(action);
		      reqDTO.setTaskId(this.getManagedBean().getTaskId());
		        
		      if(this.getManagedBean().getUserLoginId() == null)
		          reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
		      else
		          reqDTO.setUserLoginId(this.getManagedBean().getUserLoginId());
		       
		      this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
		      //this.getManagedBean().getContractVer().getCtContractValue().setContractPhysicalNo("PN"+new Long(System.currentTimeMillis()).toString().substring(10));  
		      this.getManagedBean().getContractVer().getCtContractValue().setSuspendActiveLetter(this.getManagedBean().getEditorValue().getBytes());
		      
		      if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0){
			    for(CtContractItem ctContractItem : this.getManagedBean().getContractVer().getCtContractItems()){
				    if(ctContractItem.getChkStatusId() == 1)
					   ctContractItem.setRecordStatus(true);
					else
					   ctContractItem.setRecordStatus(false);
				 }
			  }
				
			  if(this.getManagedBean().getContractVer().getCtAgencies().size() > 0){
				 for(CtAgency ctAgency : this.getManagedBean().getContractVer().getCtAgencies()){
					if(ctAgency.getChkStatusId() == 1)
					   ctAgency.setRecordStatus(true);
					else
					   ctAgency.setRecordStatus(false);
				 }
			  }
		             
		      reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
		      reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		        
		      reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		      reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
		         
			  ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			  if(resDTO.isSuccessful()) {
						
				   log.info("Record saved successfully");
				   FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ 
				   this.getManagedBean().getContractVer().getContractVerId()));
				   if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					  (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
						renewSesionObject();
					    return "/xhtml/shared/acknowledge.xhtml";
					}
				 } else{
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
	 
	 public String convertTime(Date time){ 
	        String str ="";
	    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
	        if(time == null)
	        	return str;
	        return format.format(time); 
	    } 
	 
	 public void chkJustificationAction(){
		  try{
			  if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()){
			     this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
			  }else if(this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			     this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
			  }else{
			    this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    this.getManagedBean().getContractVer().getCtContractValue().setConcession(false); 
			  }
		  }catch (Exception e) {
				System.out.println(e);
		  }
	 }
	 
	 public List<CtContractItem> getCtFilterItems() {
			return ctFilterItems;
	 }

	 public void setCtFilterItems(List<CtContractItem> ctFilterItems) {
			this.ctFilterItems = ctFilterItems;
	 }

	public List<CtContractItem> getCtContractItems() {
		return ctContractItems;
	}

	public void setCtContractItems(List<CtContractItem> ctContractItems) {
		this.ctContractItems = ctContractItems;
	}
}
