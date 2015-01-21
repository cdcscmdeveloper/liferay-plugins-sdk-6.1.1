/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.termination;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
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
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
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
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

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
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	
	private ContractBackingBean ctBB = null;
	private CtTerminationReason[] selectedTerminationReasonsList;
	private Map<Long, String> reasonDescMap = new HashMap<Long, String>();
	
	public TerminationBackingBean() {
		if(this.getManagedBean().getAction() == null)
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		
		
		 if(getManagedBean().getUserMap().size()==0) {
			  initializeApproverList();
	     }
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
    }
	
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
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
			    reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			
			    UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			    if (resDTO.getUserOrgList().size() > 0)
			    	reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			
			    if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) 
				     reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			    else // Central Contract	
				    reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
			    
                resDTO = userSO.retrieveUserListByRole(reqDTO);
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
			
			  String[] outcome = new String[res.getAction().length + 1];
		      outcome[0] = BpmOutcomeConst.BPM_SAVE;
		      int i = 1;
		      for (String action : res.getAction()) {
			     outcome[i++] = action;
		      }
			
			getManagedBean().setTaskId(taskId);
			getManagedBean().setAction(outcome);
			getManagedBean().setTaskName(res.getTaskName());
			
			CTContractTerminationDataType dataType = (CTContractTerminationDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
			ctReqDTO.setDocType(dataType.getDocType());
			getManagedBean().setUserLoginId(dataType.getContractAdmin().getUserName());
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
			setTerminationContractVer(resDTO);
			
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
	
	public void setTerminationContractVer(ContractResDTO resDTO) {
		getManagedBean().setContractVer(resDTO.getCtContractVer());
		getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
		if(getManagedBean().getContractVer().getCtTermination() == null){
			getManagedBean().getContractVer().setCtTermination(new CtTermination());
		    getManagedBean().getContractVer().getCtTermination().setDecisionType(new PmParameter());
		}
		
		getManagedBean().getApproverModel().setSource(getAvailableApproverList());
	    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
		     getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
		     getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
		} 
		
		getManagedBean().setRemarkList(resDTO.getCtRemarks());
		getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
				getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
		
	    // Get performance bond rule amount
	    IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IRuleSO.class);
	    RuleReqDTO ruleReqDTO = new RuleReqDTO();
	    ruleReqDTO.setRuleType(RuleTypeConst.BOND);
	    RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
	    if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
	    	getManagedBean().setPerformanceBondRuleAmt(Double.parseDouble(roleResDTO.getRuleValueList().get(0).getRuleValue()));
	    }
		
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.getManagedBean().setViewContract(getManagedBean().isViewContract());
		ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
		
		flush();
		if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
			getManagedBean().setRemedy(true);
		} else if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
			getManagedBean().setTermination(true);
		} else if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
			getManagedBean().setMutualTermination(true);
		}
		
		if(getManagedBean().getContractVer().getStatus().getStatusId().longValue()==ContractStatusConst.FT_PENDING_LETTER_PRINT_AND_UPDATE.longValue() 
			&& getManagedBean().getContractVer().getCtTermination().getNoticeDate()==null) {
			getManagedBean().getContractVer().getCtTermination().setNoticeDate(new Date());
		}
	}
	
	public String saveContractTermination(String action) throws Exception{
		
		 IContractSO contractSO = null;
		 ContractReqDTO reqDTO = null;
		 try{
			 
			validateContractTermination(action);
			 
			reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
					
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		   
		    reqDTO.setToken(token);
		    reqDTO.setOutcome(action);
		    reqDTO.setTaskId(this.getManagedBean().getTaskId());
		    reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		  
		    getManagedBean().getContractVer().setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
		    
		    if(getManagedBean().isRemedy()){
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_REMEDY);
		    } else if(getManagedBean().isTermination()){
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_TERMINATE);
		    } else if(getManagedBean().isMutualTermination()) {
		    	reqDTO.setTerminationAction(ContractConst.TR_LETTER_MUTUAL);
		    }
		         
		    reqDTO.setCtContract(getManagedBean().getCtContract());
		    reqDTO.setCtContractVer(getManagedBean().getContractVer());
		    
		    reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(getManagedBean().getAttachmentList());
		    reqDTO.setUserMap(getManagedBean().getUserMap());
	        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
	        reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
	           
			if(this.getManagedBean().getPmDigiSign() !=null){
			   reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
			}    
		         
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
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}	
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, getManagedBean().getContractVer().getDocType()), 
								getManagedBean().getContractVer().getCtContract().getContractNo()}, 
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
			    	setTerminationContractVer(ctResDTO);
				}
			  }
		  }finally {
			contractSO = null;
		  }
		  return null;
	 }
	
	public void validateContractTermination(String outcome){
		this.getManagedBean().setAutoUpdate(true);
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
 			String remark = getManagedBean().getRemarkList().get(0).getRemark();
 			if((remark == null || remark.length() == 0)) {
 				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
 			}
 		}	
				
		if(getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100023, new String[]{"Termination Reason"});
		}		
		
		// Mandatory to upload meeting minutes if the decision is Mutual Termination		
		if(this.getManagedBean().isMutualTermination() && this.getManagedBean().getAttachmentList().isEmpty()) {
			throw new EpException(MessageConst.ERROR_750014);
		}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getApproverModel().getTarget().size()==0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getAttachmentList().isEmpty()){
			throw new EpException(MessageConst.ERROR_750018, new String[]{"contract termination"});
		}
	}
	 
	 public String retrieveParameterById(Long parameterId) throws NamingException {
		
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			getManagedBean().getContractVer().getCtTermination().setDecisionType(resDTO.getPmParameter());
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
			getManagedBean().getContractVer().getCtTermination().setRemedyStartDate(null);
		    String parameterCode = retrieveParameterById(new Long(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId()));
			if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
				  getManagedBean().setRemedy(true);
			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
				  getManagedBean().setTermination(true);
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgency()==null || getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().length()==0) {
				  	getManagedBean().getContractVer().getCtTermination().setRecommendedAgency(getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgName());
				}
			}else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
				  getManagedBean().setMutualTermination(true);
			}
						
		 } catch (Exception e) {
			System.out.println(e);
		 }
	 }
	  
	 public void flush() {
	     getManagedBean().setTermination(false);
		 getManagedBean().setRemedy(false);
		 getManagedBean().setMutualTermination(false);
	 }
	
	public void previewNoticeAction() {
		IAddressSO addressSO =  null;
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		
		try {
			addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			context = new VelocityContext();			
			getManagedBean().setEditorValue("");
			String imgUrl = getServerUrl()
					+ "css/images/gov.png";
			if(getManagedBean().isTermination()) {				
				vmFile = "/ct/termination/termination-notice.vm";
			}else if(getManagedBean().isMutualTermination()) {
				vmFile = "/ct/termination/mutual-termination-notice.vm";
			}if(getManagedBean().isRemedy()) {			
				vmFile = "/ct/termination/remedy-notice.vm";
			}
			
			getManagedBean().setEditorValue("");
			if(vmFile !=null) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
								
				AddressReqDTO addressReqDTO = new AddressReqDTO();
				addressReqDTO.setAddressType(AddressType.BILLING_ADDRESS.value());
				addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
				PmAddressType pmAddressType = null;
				if(addressResDTO.getAddressTypeList().size()>0) {
					pmAddressType = addressResDTO.getAddressTypeList().get(0);
				}
				if(imgUrl != null){
				   context.put("imgUrl",imgUrl);
				}
				context.put("ptjName", getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
//				context.put("JABATAN_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName());
				if(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile()!=null) {
					context.put("ministryName", getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName());
				}
				if(pmAddressType!=null) {
					context.put("ptjAddress", formAddress(pmAddressType.getPmAddress().getAddress1(), pmAddressType.getPmAddress().getAddress2(), pmAddressType.getPmAddress().getAddress3(), pmAddressType.getPmAddress().getAddress4()));
					context.put("telNo", pmAddressType.getPmAddress().getPhoneNo());
					context.put("faxNo", pmAddressType.getPmAddress().getFaxNo());
					context.put("email", pmAddressType.getPmAddress().getEmail());
				}
				if(getManagedBean().getContractVer().getCtTermination().getFileRefNo()!=null && getManagedBean().getContractVer().getCtTermination().getFileRefNo().length()>0) {
					context.put("fileRefNo", getManagedBean().getContractVer().getCtTermination().getFileRefNo());
				}
				if(getManagedBean().getContractVer().getCtTermination().getNoticeDate()!=null) {
					context.put("noticeDate", df.format(getManagedBean().getContractVer().getCtTermination().getNoticeDate()));
				}			
				context.put("supplierName",getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				SmAddress smAddress = getManagedBean().getContractVer().getCtContractValue().getSupplierAddress();
				context.put("supplierAddress",formAddress(smAddress.getAddress1(), smAddress.getAddress2(), smAddress.getAddress3(), smAddress.getAddress4()));
				context.put("contractTitle",getManagedBean().getCtContract().getContractName());
				if(getManagedBean().getContractVer().getContractPhysicalNo()==null) {
					context.put("contractPhysicalNo",getManagedBean().getCtContract().getContractNo());
				} else {
					context.put("contractPhysicalNo",getManagedBean().getContractVer().getContractPhysicalNo());
				}
				context.put("contractNo",getManagedBean().getCtContract().getContractNo());
				context.put("reasonDescMap",reasonDescMap );
				context.put("reasonList", getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons());
				if(getManagedBean().isRemedy()) {
					if(getManagedBean().getContractVer().getCtTermination().getTerminationEffDate()!=null) {
						context.put("terminateEffDate",df.format(getManagedBean().getContractVer().getCtTermination().getTerminationEffDate()));
					}
				}
				if(getManagedBean().getContractVer().getCtTermination().getRemedyStartDate()!=null) {
					context.put("remedyStartDate",df.format(getManagedBean().getContractVer().getCtTermination().getRemedyStartDate()));
				}
				if(getManagedBean().getContractVer().getCtTermination().getRemedyEndDate()!=null) {
					context.put("remedyEndDate",df.format(getManagedBean().getContractVer().getCtTermination().getRemedyEndDate()));
				}				
				if(getManagedBean().getContractVer().getApprover()!=null) {
					context.put("approverName", getManagedBean().getContractVer().getApprover().getUserName());
					context.put("approverDesignation", getManagedBean().getContractVer().getApprover().getDesignation());
				}
				// Extra For Termination Notice
				context.put("contractAmount", getManagedBean().getContractVer().getCtContractValue().getContractAmount().toString());
				context.put("effDate", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
				context.put("expDate", df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
				if(getManagedBean().getContractVer().getCtTermination().getLastRemedyDate()!=null) {
					context.put("lastRemedyDate", df.format(getManagedBean().getContractVer().getCtTermination().getLastRemedyDate()));
				}
				if(getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo()!=null && getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo().length()>0) {
					context.put("lastRemedyFileNo", getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo());
				}
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgency()!=null && getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().length()>0) {
					context.put("recommendAgency", getManagedBean().getContractVer().getCtTermination().getRecommendedAgency());
				}		
				if(getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate()!=null) {
					context.put("recommendAgencyDate", df.format(getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate()));
				}			
				if (vmFile != null && context != null) {
					content = DocumentGenerator.getInstance().getHtmlFromVelocity(
							vmFile, context);
				}				
				getManagedBean().setEditorValue(content);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			addressSO = null;
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
		 return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
		}
	 
	 public boolean isPrintAndUpdateTask() {
		 if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_UPDATE_HT_NAME)) {
			 return true;
		 }
		 else
			 return false;
	 }
	 
	 public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
			terminateReason.setReasonType(new PmReason());
		    terminateReason.setCtTermination(getManagedBean().getContractVer().getCtTermination());
		    getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().add(terminateReason);
	 }

	 public void deleteTerminateReason() {
		 for (int i = 0; i < selectedTerminationReasonsList.length; i++) {
				CtTerminationReason terminationReason = selectedTerminationReasonsList[i];
				getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().remove(terminationReason);
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
		
    	if(getManagedBean().getTerminationReasonDesc() == null) {
    	     SelectItem[] items = null;   
    	     ReasonReqDTO reqDTO = null;
    	     ReasonResDTO resDTO = null;
    	     IReasonSO reasonSO = null;
		     try {
		             int i = 1;
		             reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IReasonSO.class);
		             reqDTO = new ReasonReqDTO();
		             reqDTO.setReasonCategory(ReasonCategory.CONTRACT_TERMINATION.value());
		             reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		             resDTO = reasonSO.retrieveReasonDescListByCategory(reqDTO);
		             List<PmReasonDesc> list = resDTO.getReasonDescList();
		             items = new SelectItem[list.size()+1];
		             items[0] = new SelectItem(null, "- Select One -");
		             for(PmReasonDesc reasonDesc : list) {
			             items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			             reasonDescMap.put(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			         } getManagedBean().setTerminationReasonDesc(items);
		     } catch (Exception e) {
			      e.printStackTrace();
			      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     } finally {
		    	 reqDTO = null;
	    	     resDTO = null;
	    	     reasonSO = null;
		     }
    	}
	    return getManagedBean().getTerminationReasonDesc();
	}
    
    public void lodgeComplaint() {
    	getManagedBean().getContractVer().getCtTermination().setComplaintLodged(true);
    	//TODO Call SM API to trigger a task for complaint.
    }
    
    public String gotoTerminatePage() throws NamingException {
    	Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
    	ContractReqDTO ctReqDTO = new ContractReqDTO();
    	ctReqDTO.setContractVerId(contractVerId);
    	ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
	    IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
	    getManagedBean().setViewContract(true);
	    getManagedBean().setExternalPageUrl(TERMINATE_HISTORY_PAGE);
	    setTerminationContractVer(resDTO);
		
		return PROCESS_TERMINATE_PAGE;
	}
    
    private String getServerUrl() {
		ExternalContext extcontext = FacesContext.getCurrentInstance()
				.getExternalContext();
		PortletRequest portletRequest = (PortletRequest) extcontext
				.getRequest();
		HttpServletRequest httpServletRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil
						.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		String url = requestURL.substring(0, requestURL.length()
				- httpServletRequest.getRequestURI().length())
				+ httpServletRequest.getContextPath() + "/";

		return url;
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
    
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}
    
    public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.termination", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
    
    public String preSubmitContractTermination(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContractTermination(getManagedBean().getButtonAction());
	}
	
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FT_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FT_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FT_HT_NAME)))
			return true;
		else 
			return false;
	}
	
	public String gpkiSignAction(String outcome) throws Exception{
		 if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) 
					|| (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_ENDORSE))){
			validateContractTermination(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(getManagedBean().getContractVer().getDocType());
			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			sb.append(df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
			sb.append(getManagedBean().getContractVer().getCtContractValue().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else if(BpmOutcomeConst.BPM_CANCEL.equals(outcome)){
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		}
		else{
			return saveContractTermination(outcome);
		}
	}
	
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
}
