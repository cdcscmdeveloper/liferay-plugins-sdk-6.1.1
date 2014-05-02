/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.suspension;

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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.suspension.SuspensionManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

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
	    	this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT});	
	    }
	    if(getManagedBean().getUserMap().size()==0) {
			 initializeApproverList();
	    }
	 }
	 
     @PostConstruct
	 public void initializeContractValue() throws ParseException {
		 ContractBackingBean ctSuspBB = new ContractBackingBean();
		 ctSuspBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
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
			PmUser pmUser = new PmUser();
			pmUser.setUserId(dataType.getContractAdmin().getUserId().longValue());
			pmUser.setUserName(dataType.getContractAdmin().getUserName());
			this.getManagedBean().setUserLogin(pmUser);
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ctReqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
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
			
			this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
		    	this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
		    	this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			}  
			
			/*String decoded = new String(this.getManagedBean().getContractVer().getCtContractValue().getSuspendActiveLetter(), "UTF-8");
			this.getManagedBean().setEditorValue(decoded);
			*/
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
		        
		      if(this.getManagedBean().getUserLogin() == null) {
		          reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		      } else {
		    	  reqDTO.setPmUser(this.getManagedBean().getUserLogin());
		      }
		       
		      this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
		      //this.getManagedBean().getContractVer().getCtContractValue().setContractPhysicalNo("PN"+new Long(System.currentTimeMillis()).toString().substring(10));  
		     // this.getManagedBean().getContractVer().getCtContractValue().setSuspendActiveLetter(this.getManagedBean().getEditorValue().getBytes());
		      
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
		        
		      reqDTO.setUserRole(getUserRole());
	          reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
			  reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			  reqDTO.setUserMap(getManagedBean().getUserMap());
			  reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			  reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		         
			  ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			  if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
							MessageUtils.getMessage(MessageConst.INFO_700005, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
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
	 
	 public String convertTime(Date time){ 
	        String str ="";
	    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
	        if(time == null)
	        	return str;
	        return format.format(time); 
	    } 
	 
	 public void chkJustificationAction(){
		  try {
			    if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && !this.getManagedBean().isChkConceValue()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
			    }else if(this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
			    	if(!this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && !this.getManagedBean().isChkConceValue()){
				    	  this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
				    	  this.getManagedBean().setChkConceValue(true);
				     }
			    }else if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && this.getManagedBean().isChkConceValue()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    	this.getManagedBean().setChkConceValue(false);
			    }else{
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    	this.getManagedBean().getContractVer().getCtContractValue().setConcession(false); 
			    	this.getManagedBean().setChkConceValue(false);
			    }
		   } catch (Exception e) {
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
