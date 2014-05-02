/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.termination;

import java.text.Format;
import java.text.SimpleDateFormat;
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
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
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
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.termination.TerminationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

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
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SUBMIT});	
	}
	
	@PostConstruct
	public void initializeContractValue() throws ParseException {
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
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
			this.getManagedBean().setUserLoginId(dataType.getContractAdmin().getUserName());
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			if(this.getManagedBean().getContractVer().getCtTermination() == null){
				this.getManagedBean().getContractVer().setCtTermination(new CtTermination());
			    if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType() == null){
			    	this.getManagedBean().getContractVer().getCtTermination().setDecisionType(new PmParameter());
			    	this.getManagedBean().getContractVer().getCtTermination().getDecisionType().setParameterId(new Long(retrieveParameterByTypeAndCode(ContractConst.TR_LETTER_MUTUAL)));
			   }
			}
			
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			
			flush();
			String parameterCode = retrieveParameterById(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId());
			
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
				this.getManagedBean().setRemedy(true);
			} else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
				this.getManagedBean().setTermination(true);
			} else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
				this.getManagedBean().setMutualTermination(true);
			}
			
			if(getManagedBean().getContractVer().getStatus().getStatusId().longValue()==ContractStatusConst.FT_PENDING_LETTER_PRINT_AND_UPDATE.longValue() 
				&& getManagedBean().getContractVer().getCtTermination().getNoticeDate()==null) {
				getManagedBean().getContractVer().getCtTermination().setNoticeDate(new Date());
			}
			
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
					
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		   
		    reqDTO.setToken(token);
		    reqDTO.setOutcome(action);
		    reqDTO.setTaskId(this.getManagedBean().getTaskId());
		    reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		  
		    this.getManagedBean().getContractVer().setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
		    
		    if(this.getManagedBean().isRemedy()){
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_REMEDY);
		    } else if(this.getManagedBean().isTermination()){
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_TERMINATE);
		    } else if(this.getManagedBean().isMutualTermination()) {
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_MUTUAL);
		    }
		         
		    reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
		    reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		    
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
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getTaskName()!=null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_UPDATE_HT_NAME)) {
					messageId = MessageConst.INFO_700026;
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					getManagedBean().setContractVer(resDTO.getCtContractVer());
					getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
					ContractBackingBean ctBB = new ContractBackingBean();
					ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
					
					flush();
					String parameterCode = retrieveParameterById(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId());
					
					if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
						this.getManagedBean().setRemedy(true);
					} else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
						this.getManagedBean().setTermination(true);
					} else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						this.getManagedBean().setMutualTermination(true);
					}
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
	 
	 public int retrieveParameterByTypeAndCode(String parameterCode) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType("TDT");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
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
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
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
			IContractTemplateSO contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			flush();
			
		    String parameterCode = retrieveParameterById(new Long(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId()));
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
				this.getManagedBean().setRemedy(true);
				ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
				reqDTO.setTemplateId(1L);
			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
				this.getManagedBean().setTermination(true);
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgency()==null || getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().length()==0) {
					getManagedBean().getContractVer().getCtTermination().setRecommendedAgency(getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgName());
				}
			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
				this.getManagedBean().setMutualTermination(true);
			}
						
		 } catch (Exception e) {
			System.out.println(e);
		 }
	 }
	  
	 private void flush() {
	     this.getManagedBean().setTermination(false);
		 this.getManagedBean().setRemedy(false);
		 this.getManagedBean().setMutualTermination(false);
	 }
	 
	 public boolean isContractAdmin() {
			return getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
		}
	 
	 public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
		if(terminateReason.getReasonType() == null)
			terminateReason.setReasonType(new PmReason());
		    terminateReason.setCtTermination(this.getManagedBean().getContractVer().getCtTermination());
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
	
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) 
			return RoleCode.BPK_CCA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();		
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
		    reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
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
