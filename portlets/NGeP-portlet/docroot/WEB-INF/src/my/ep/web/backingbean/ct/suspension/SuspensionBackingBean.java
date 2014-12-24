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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

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
	    	
	    	if(getManagedBean().getUserMap().size()==0) {
				  initializeApproverList();
		     }
	    	
	    	HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
	        HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);	          
	    	if(originalServletRequest.getParameter(ContractConst.PARAMETER_ID) != null)
	    		getManagedBean().setViewOnly(true);
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
			          String contractVerId = originalServletRequest.getParameter(ContractConst.PARAMETER_ID);
			          IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			          
			          if(contractVerId!=null) {
			        	  ContractReqDTO reqDTO = new ContractReqDTO();
			        	  reqDTO.setContractVerId(Long.parseLong(contractVerId));
			        	  reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			        	  reqDTO.setUpdateAction(false);
			        	  ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			        	  //Restrict If login user not the contract admin
			        	  if (!getFacesScopes().getUserSessionBean().getUserId().equals(resDTO.getCtContractVer().getContractAdmin().getUserId())) {
							   FacesContext fc = FacesContext.getCurrentInstance();
								fc.addMessage(null, new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"", MessageUtils.getMessage(MessageConst.ERROR_700116,
												MessageUtils.BUNDLE_COMMON_MESSAGES,
												new FacesScopes().getUserLocale())));
								fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			
								return;
						   }
			        	  
			        	  getManagedBean().setViewOnly(true);
			        	  setSuspensionContractVer(resDTO);
			        	  return;
			          }
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
					  
					  ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			          setSuspensionContractVer(contractSO.retrieveContractVerDtls(ctReqDTO));
			          getManagedBean().setAutoUpdate(true);
			          
	     } catch(Exception e){
			  e.printStackTrace();
		 }
	 }
	 
	 public void setSuspensionContractVer(ContractResDTO resDTO){
		 
		    getManagedBean().setContractVer(resDTO.getCtContractVer());	
			getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
			if(getManagedBean().getContractVer().getCtContractItems() != null && getManagedBean().getContractVer().getCtContractItems().size() > 0 ) {
				Collections.sort(getManagedBean().getContractVer().getCtContractItems(), new CtContractItem());
				 
			 	 for (CtContractItem contractItem : getManagedBean().getContractVer().getCtContractItems()) {
			 		//Get latest contract item and map with the sc request item
			 		 for (CtContractItem latestContractItem : getManagedBean().getCtContract().getLatestContractVer().getCtContractItems()) {
			 			 if (contractItem.getScRequestItem().getRequestNoteItemId().equals(latestContractItem.getScRequestItem().getRequestNoteItemId())) {
			 				 getManagedBean().getItemMap().put(contractItem.getScRequestItem().getRequestNoteItemId(), latestContractItem);
			 				 break;
			 			 }
			 		 }
		 		 }
			}			
		    if(getManagedBean().getContractVer().getCtAgencies() != null && getManagedBean().getContractVer().getCtAgencies().size() > 0 )  {
			 	 Collections.sort(getManagedBean().getContractVer().getCtAgencies(), new CtAgency());			 	
			 	 for (CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
			 		 //Get latest agency and map with the org profile
			 		 for (CtAgency latestAgency : getManagedBean().getCtContract().getLatestContractVer().getCtAgencies()) {
			 			 if (ctAgency.getOrgProfile().getOrgProfileId().equals(latestAgency.getOrgProfile().getOrgProfileId())) {
			 				 getManagedBean().getAgencyMap().put(ctAgency.getOrgProfile().getOrgProfileId(), latestAgency);
			 				 break;
			 			 }
			 		 }			 		
			 	 }
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
		    ctBB.getManagedBean().setViewContract(getManagedBean().isViewOnly());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	  }
	
	 public String backAction() {
			return CONTRACT_MAINT_PAGE;
	 }
	 
	 public void setSuspensionStatusValue(CtContractItem item) {		 
		 for(CtContractItem ctItem :this.getManagedBean().getContractVer().getCtContractItems()) {
			 if(ctItem.equals(item)) {		
				 CtContractItem latestVerItem = getManagedBean().getItemMap().get(ctItem.getScRequestItem().getRequestNoteItemId());
				 Date effDate = null;
				 Date expDate = null;
				 
				 if(item.getRecordStatusFlg().intValue() == 1) {					 
					 effDate = (latestVerItem.getRecordStatusFlg() == 0) ? getCurrentDate() : latestVerItem.getEffDate();
					 expDate = (latestVerItem.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestVerItem.getExpDate();
					 item.setEffDate(effDate);
					 item.setExpDate(expDate);					
			     } else {
					 effDate = (latestVerItem.getRecordStatusFlg() == 0) ? latestVerItem.getEffDate() : getCurrentDate();
					 expDate = (latestVerItem.getRecordStatusFlg() == 0) ?  latestVerItem.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
					 item.setEffDate(effDate);
					 item.setExpDate(expDate);		
			     }
			 }
		 }
	 }
	 
	 public void setSuspensionAgencyStatusValue(CtAgency agency){	
		 for(CtAgency ctAgency  : getManagedBean().getContractVer().getCtAgencies()) {
			 if(ctAgency.equals(agency)) {
				 CtAgency latestAgency = getManagedBean().getAgencyMap().get(agency.getOrgProfile().getOrgProfileId());
				 Date effDate = null;
				 Date expDate = null;
				 
				 if(ctAgency.getRecordStatusFlg().intValue() == 1) {
					 effDate = (latestAgency.getRecordStatusFlg() == 0) ? getCurrentDate() : latestAgency.getEffDate();
					 expDate = (latestAgency.getRecordStatusFlg() == 0) ? getManagedBean().getContractVer().getCtContractValue().getExpDate() : latestAgency.getExpDate();
					 ctAgency.setEffDate(effDate);
					 ctAgency.setExpDate(expDate);					
			     } else {
			    	 effDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getEffDate() : getCurrentDate();
					 expDate = (latestAgency.getRecordStatusFlg() == 0) ? latestAgency.getExpDate() : getManagedBean().getContractVer().getCtContractValue().getExpDate();
					 ctAgency.setEffDate(effDate);
					 ctAgency.setExpDate(expDate);		
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
			     reqDTO.setUserMap(getManagedBean().getUserMap());
		        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		        reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			        
				 if(this.getManagedBean().getPmDigiSign() !=null){
					reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
				 }
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
		 getManagedBean().setAutoUpdate(true);
		 
		 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			 CtRemark remark = getManagedBean().getRemarkList().get(0);
			 if(remark.getRemark().equalsIgnoreCase(null) || remark.getRemark().equalsIgnoreCase("")){
				RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remarks"});
			 }
	      }
		 
		 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getApproverModel().getTarget().size()==0) {
			RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		 
		 if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getAttachmentList().isEmpty()){
			 RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_750018, new String[]{"contract suspension"});
		}
		 
		 
		 if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) 
					|| (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) 
					|| (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT))) {
					//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
					StringBuilder sb = new StringBuilder();
					sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
					sb.append(getManagedBean().getContractVer().getDocType());
					sb.append(getManagedBean().getContractVer().getCtContractValue().getEffDate());
					sb.append(getManagedBean().getContractVer().getCtContractValue().getExpDate());
					sb.append(getManagedBean().getContractVer().getCtContractValue().getContractAmount());
					sb.append(outcome);			
					getManagedBean().setTextBeforeSign(sb.toString());	
					getManagedBean().setButtonAction(outcome);
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
	 
	 public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.suspension", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
	 
	public String preSubmitContractSuspension(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		
		return saveContractSuspension(getManagedBean().getButtonAction());
	}
		
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}
	
	public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}
	
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
	
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FS_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FS_HT_NAME)))
			return true;
		else 
			return false;
	}

}
