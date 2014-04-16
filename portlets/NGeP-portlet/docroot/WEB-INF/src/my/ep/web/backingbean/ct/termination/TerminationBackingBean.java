/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.termination;

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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.termination.TerminationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Termination 
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "terminationBackingBean")
@RequestScoped
public class TerminationBackingBean extends BaseBackingBean<TerminationManagedBean> {

	private static Logger log = Logger.getLogger(TerminationBackingBean.class);
	private CtTerminationReason[] selectedTerminationReasonsList;
	
	public TerminationBackingBean(){
		if(this.getManagedBean().getAction() == null) 
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT});	
		if(getManagedBean().getUserMap().size()==0) {
			 initializeApproverList();
	    }
	}
	
	@PostConstruct
	public void initializeContractValue() throws ParseException {
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
	}
	
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
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
			System.out.println("taskId:"+taskId);
			if(taskId==null) {
				return;
			}
			
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			ctReqDTO.setUpdateAction(false);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setAction(res.getAction());
			this.getManagedBean().setTaskName(res.getTaskName());
			
			CTContractTerminationDataType dataType = (CTContractTerminationDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
			this.getManagedBean().setUserLoginId(dataType.getContractAdmin());
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			if(this.getManagedBean().getContractVer().getCtTermination() == null){
				this.getManagedBean().getContractVer().setCtTermination(new CtTermination());
				this.getManagedBean().setTerminateNotice(true);
			    if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType() == null){
			    	this.getManagedBean().getContractVer().getCtTermination().setDecisionType(new PmParameter());
			    	this.getManagedBean().getContractVer().getCtTermination().getDecisionType().setParameterId(new Long(retrieveParameterByTypeAndCode(ContractConst.TR_LETTER_MUTUAL)));
				//if(resDTO.getCtContractVer().getCtTermination().getCtTerminationReasons().size() == 0)
				//	resDTO.getCtContractVer().getCtTermination().setCtTerminationReasons(new ArrayList<CtTerminationReason>());
			   }
			}
			
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			
			this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
		    	this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
		    	this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			} 
			
			flush();
			String decoded ="";
			String parameterCode = retrieveParameterById(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId());
			
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
				this.getManagedBean().setTerminateWarningLetter(true);
				decoded = new String(this.getManagedBean().getContractVer().getCtTermination().getRemedyNotice(), "UTF-8");
			
			}if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE) || parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)){
				this.getManagedBean().setTerminateNotice(true);
				if(this.getManagedBean().getContractVer().getCtTermination().getTerminationNotice() == null)
					decoded =" ";
				else
				  decoded = new String(this.getManagedBean().getContractVer().getCtTermination().getTerminationNotice(), "UTF-8"); 
				}
			
			this.getManagedBean().setEditorValue(decoded);
			
			FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_TERMINATION);
		
	      } catch(Exception e){
				e.printStackTrace();
		  }

	 }
	
	 public String saveContractTermination(String action){
		
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
		    
		  
		    this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
		    
		    if(this.getManagedBean().isTerminateWarningLetter()){
		    	  this.getManagedBean().getContractVer().getCtTermination().setRemedyNotice(this.getManagedBean().getEditorValue().getBytes());	 
		    	  reqDTO.setTerminationAction(ContractConst.TR_LETTER_REMEDY);
		   
		    }if(this.getManagedBean().isTerminateNotice()){
		      this.getManagedBean().getContractVer().getCtTermination().setTerminationNotice(this.getManagedBean().getEditorValue().getBytes());
		      reqDTO.setTerminationAction(ContractConst.TR_LETTER_TERMINATE);
		    } 
		         
		    reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
		    reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		    
		    reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
		    reqDTO.setUserMap(getManagedBean().getUserMap());
		    reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		         
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
		 } catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
				contractSO = null;
		 }
		   return null;
	 }
	 
	 public int retrieveParameterByTypeAndCode(String parameterCode) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType("TDT");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter().getParameterId().intValue();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return 0;
	 } 
	 
	 public String retrieveParameterById(Long parameterId) throws NamingException {
		
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			this.getManagedBean().getContractVer().getCtTermination().setDecisionType(resDTO.getPmParameter());
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
	 
	 public void changeField() {

		try {
			flush();
			
		    String parameterCode = retrieveParameterById(new Long(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId()));
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
				this.getManagedBean().setTerminateWarningLetter(true);

			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE) ||
					parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
				this.getManagedBean().setTerminateNotice(true);

			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_NO_ACTION)) {
				this.getManagedBean().setTerminateRejectLetter(true);
			}
						
		 } catch (Exception e) {
			System.out.println(e);
		 }
	 }
	  
	 private void flush() {
	    
		 this.getManagedBean().setTerminateNotice(false);
		 this.getManagedBean().setTerminateRejectLetter(false);
		 this.getManagedBean().setTerminateWarningLetter(false);
	 }
	 
	 public boolean isContractAdmin() {
			return getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
		}
	 
	 public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
		if(terminateReason.getReasonType() == null)
			terminateReason.setReasonType(new PmParameter());
		this.getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().add(terminateReason);
	 }

	 public void deleteTerminateReason() {
		 for (int i = 0; i < selectedTerminationReasonsList.length; i++) {
				CtTerminationReason terminationReason = selectedTerminationReasonsList[i];
				this.getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().remove(terminationReason);
			}
		 selectedTerminationReasonsList = null;	
	 }

	public CtTerminationReason[] getSelectedTerminationReasonsList() {
		return selectedTerminationReasonsList;
	}

	public void setSelectedTerminationReasonsList(
			CtTerminationReason[] selectedTerminationReasonsList) {
		this.selectedTerminationReasonsList = selectedTerminationReasonsList;
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
	
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else 
			return null;
	}
	
    public SelectItem[] getTerminationReasonDesc() {
		
		SelectItem[] items = null;     
		try{
		    int i = 1;
		    IReasonSO reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
		    ReasonReqDTO reqDTO = new ReasonReqDTO();
		    reqDTO.setReasonCategory(ReasonCategory.CONTRACT_TERMINATION.value());
		    reqDTO.setLanguage("EN");
		    ReasonResDTO resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		    List<PmReasonDesc> list = resDTO.getReasonDescList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		      
			for(PmReasonDesc reasonDesc : list) 
			  items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());   
		 }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
	    return items;
	}

}
