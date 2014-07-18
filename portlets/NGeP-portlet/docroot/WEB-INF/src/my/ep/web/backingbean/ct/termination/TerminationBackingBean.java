/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.termination;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
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
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.termination.TerminationManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;

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
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	
	private ContractBackingBean ctBB = null;
	private Map<Long, String> reasonDescMap = new HashMap<Long, String>();
	private CtTerminationReason[] selectedTerminationReasonsList;
	
	public TerminationBackingBean(){
		if(this.getManagedBean().getAction() == null) 
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SUBMIT});	
		
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
			    this.getManagedBean().getContractVer().getCtTermination().setDecisionType(new PmParameter());
			}
			
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			
			flush();
			if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
				this.getManagedBean().setRemedy(true);
			} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
				this.getManagedBean().setTermination(true);
			} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
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
	
	public String backAction() {
		String navigateUrl = (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
		getManagedBean().setExternalPageUrl(null);
		return navigateUrl;
	}
	
	public String saveContractTermination(String action) throws Exception{
		
		 IContractSO contractSO = null;
		 ContractReqDTO reqDTO = null;
		 try{
			// Mandatory to upload meeting minutes if the decision is Mutual Termination		
			if(this.getManagedBean().isMutualTermination() && this.getManagedBean().getAttachmentList().size()==0) {
				throw new EpException(MessageConst.ERROR_750044);
			}
			
			if(getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().size()==0) {
				throw new EpException(MessageConst.ERROR_100023, new String[]{"Termination Reason"});
			}
			 
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
		         
		    reqDTO.setCtContract(this.getManagedBean().getCtContract());
		    reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		    
		    reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
		         
		    ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		    if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700046;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE)) {
					messageId = MessageConst.INFO_700045;
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
					ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
					
					flush();
					if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
						this.getManagedBean().setRemedy(true);
					} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
						this.getManagedBean().setTermination(true);
					} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						this.getManagedBean().setMutualTermination(true);
					}
				}
			  }
		  }finally {
			contractSO = null;
		  }
		  return null;
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
			flush();
			
		    String parameterCode = retrieveParameterById(new Long(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId()));
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
				this.getManagedBean().setRemedy(true);
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
	 
	public void previewNoticeAction() {
		IContractTemplateSO contractTemplateSO = null;
		IAddressSO addressSO =  null;
		try {
			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			ContractTemplateReqDTO reqDTO = new ContractTemplateReqDTO();
			if(getManagedBean().isTermination()) {
				reqDTO.setTemplateName(ContractConst.TEMPLATE_TERMINATION_NOTICE);	
			} else if(getManagedBean().isRemedy()) {
				reqDTO.setTemplateName(ContractConst.TEMPLATE_REMEDY_NOTICE);
			} else if (getManagedBean().isMutualTermination()) {
				reqDTO.setTemplateName(ContractConst.TEMPLATE_MUTUAL_TERMINATION_NOTICE);
			}
			ContractTemplateResDTO resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
			getManagedBean().setEditorValue("");
			if(resDTO.getCtTemplate()!=null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
				String content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
				
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
				addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
				PmAddressType pmAddressType = null;
				if(addressResDTO.getAddressTypeList().size()>0) {
					pmAddressType = addressResDTO.getAddressTypeList().get(0);
				}
				
				Map<String, String> valuesMap = new HashMap<String, String>();
				valuesMap.put("PTJ_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
				valuesMap.put("JABATAN_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName());
				valuesMap.put("MINISTRY_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgName());
				if(pmAddressType!=null) {
					valuesMap.put("PTJ_ADDRESS", formAddress(pmAddressType.getPmAddress().getAddress1(), pmAddressType.getPmAddress().getAddress2(), pmAddressType.getPmAddress().getAddress3(), pmAddressType.getPmAddress().getAddress4()));
					valuesMap.put("TEL_NO", pmAddressType.getPmAddress().getPhoneNo());
					valuesMap.put("FAX_NO", pmAddressType.getPmAddress().getFaxNo());
					valuesMap.put("EMAIL", pmAddressType.getPmAddress().getEmail());
				}
				if(getManagedBean().getContractVer().getCtTermination().getFileRefNo()!=null && getManagedBean().getContractVer().getCtTermination().getFileRefNo().length()>0) {
					valuesMap.put("FILE_REF_NO", getManagedBean().getContractVer().getCtTermination().getFileRefNo());
				}
				if(getManagedBean().getContractVer().getCtTermination().getNoticeDate()!=null) {
					valuesMap.put("NOTICE_DATE", df.format(getManagedBean().getContractVer().getCtTermination().getNoticeDate()));
				}			
				valuesMap.put("SUPPLIER_NAME",getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				SmAddress smAddress = getManagedBean().getContractVer().getCtContractValue().getSupplierAddress();
				valuesMap.put("SUPPLIER_ADDRESS",formAddress(smAddress.getAddress1(), smAddress.getAddress2(), smAddress.getAddress3(), smAddress.getAddress4()));
				valuesMap.put("CONTRACT_TITLE",getManagedBean().getCtContract().getContractName());
				if(getManagedBean().getCtContract().getContractPhysicalNo()==null) {
					valuesMap.put("CONTRACT_PHYSICAL_NO",getManagedBean().getCtContract().getContractNo());
				} else {
					valuesMap.put("CONTRACT_PHYSICAL_NO",getManagedBean().getCtContract().getContractPhysicalNo());
				}
				valuesMap.put("CONTRACT_NO",getManagedBean().getCtContract().getContractNo());
				StringBuffer terminationReason = new StringBuffer();
				for(CtTerminationReason ctTerminationReason : getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons()) {
					terminationReason.append(reasonDescMap.get(ctTerminationReason.getReasonType().getReasonId()));
					terminationReason.append(" - ");
					terminationReason.append(ctTerminationReason.getDescription());
					terminationReason.append("<BR/>");
				}
				valuesMap.put("REASON_LIST", terminationReason.toString());
				if(getManagedBean().isRemedy()) {
					if(getManagedBean().getContractVer().getCtTermination().getTerminationEffDate()!=null) {
						valuesMap.put("TERMINATE_EFF_DATE",df.format(getManagedBean().getContractVer().getCtTermination().getTerminationEffDate()));
					}
				}
				if(getManagedBean().getContractVer().getCtTermination().getRemedyStartDate()!=null) {
					valuesMap.put("REMEDY_START_DATE",df.format(getManagedBean().getContractVer().getCtTermination().getRemedyStartDate()));
				}
				if(getManagedBean().getContractVer().getCtTermination().getRemedyEndDate()!=null) {
					valuesMap.put("REMEDY_END_DATE",df.format(getManagedBean().getContractVer().getCtTermination().getRemedyEndDate()));
				}				
				if(getManagedBean().getContractVer().getApprover()!=null) {
					valuesMap.put("APPROVER_NAME", getManagedBean().getContractVer().getApprover().getUserName());
					valuesMap.put("APPROVER_DESIGNATION", getManagedBean().getContractVer().getApprover().getDesignation());
				}
				// Extra For Termination Notice
				valuesMap.put("CONTRACT_AMOUNT", getManagedBean().getContractVer().getCtContractValue().getContractAmount().toString());
				valuesMap.put("EFF_DATE", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
				valuesMap.put("EXP_DATE", df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
				if(getManagedBean().getContractVer().getCtTermination().getLastRemedyDate()!=null) {
					valuesMap.put("LAST_REMEDY_DATE", df.format(getManagedBean().getContractVer().getCtTermination().getLastRemedyDate()));
				}
				if(getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo()!=null && getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo().length()>0) {
					valuesMap.put("LAST_REMEDY_FILE_NO", getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo());
				}
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgency()!=null && getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().length()>0) {
					valuesMap.put("RECOMMEDED_AGENCY", getManagedBean().getContractVer().getCtTermination().getRecommendedAgency());
				}		
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate()!=null) {
					valuesMap.put("RECOMMENDED_AGENCY_DATE", df.format(getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate()));
				}			
				// End of Extra For Termination Notice
				StrSubstitutor sub = new StrSubstitutor(valuesMap);
				getManagedBean().setEditorValue(sub.replace(content));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractTemplateSO = null;
		}
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
	 
	 public boolean isContractAdmin() {
			return getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
		}
	 
	 public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
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
		      
			for(PmReasonDesc reasonDesc : list) {
			  items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			  reasonDescMap.put(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			}
		 }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
	    return items;
	}
    
    public void lodgeComplaint() {
    	getManagedBean().getContractVer().getCtTermination().setComplaintLodged(true);
    	//TODO Call SM API to trigger a task for complaint.
    }
    
    public String gotoTerminatePage() throws NamingException {
    	Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
    	ContractReqDTO ctReqDTO = new ContractReqDTO();
    	ctReqDTO.setContractVerId(contractVerId);
	    IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
	    ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);

    	getManagedBean().setContractVer(resDTO.getCtContractVer());
	    getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	    getManagedBean().setViewContract(true);
	    getManagedBean().setExternalPageUrl(TERMINATE_HISTORY_PAGE);
	    
		getManagedBean().setRemarkList(resDTO.getCtRemarks());
		getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
		
		flush();
		
		if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
			this.getManagedBean().setRemedy(true);
		} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
			this.getManagedBean().setTermination(true);
		} else if(this.getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
			this.getManagedBean().setMutualTermination(true);
		}
		
		return PROCESS_TERMINATE_PAGE;
	}
}
