/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Circular Panel Backingbean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@RequestScoped
public class PanelBackingBean extends BaseBackingBean<PanelManagedBean>{
	
	private static Logger log = Logger.getLogger(PanelBackingBean.class);
	public CtPanelItem[] selectedPanelItem;
	
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	public PanelBackingBean(){
		if(getManagedBean().getOutComes()==null) {
			getManagedBean().setOutComes(new String[]{"Save","Submit"});
			getManagedBean().setCircularVer(new CtCircularVer());
			getManagedBean().setCancelCircularReq(new CtCancelCircularReq());
			PmStatus status = new PmStatus();
			status.setStatusId(ContractStatusConst.PQ_PENDING_SUBMISSION);
			getManagedBean().getCircularVer().setStatus(status);
			getManagedBean().getCancelCircularReq().setStatus(status);
						
						
			initializeRemarks();
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		}
		if(getManagedBean().getUserGroupMap()==null) {
			initializeApproverGroup();
		}
		if(getManagedBean().getCircularDocType()==null) {
			getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
		}
		if(isPanelAdmin() || isCentralContractAdmin()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setCircularAdmin(facesScopes.getUserSessionBean().getLoginId());
		}
	}
	
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(getManagedBean().getApproverModel()==null) {
				List<String> source = new ArrayList<String>(); 
		        List<String> target = new ArrayList<String>();  
		        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}			
			
			getManagedBean().setUserGroupMap(new HashMap<Long, PmUserGroup>());
			int i = 0;
			for(PmUserGroup userGroup : resDTO.getUserGroupList()) {
				getManagedBean().getUserGroupMap().put(userGroup.getUserGroupId(), userGroup);
				if(i==0) {
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						source.add(user.getPmUser().getUserName());
					}
					getManagedBean().getApproverModel().setSource(source);
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
	}
	
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCircularVer().getCircularRemarks());
		initiateRemark(getManagedBean().getCircularVer().getZoneRemarks());
		initiateRemark(getManagedBean().getCircularVer().getItemRemarks());
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
	
	public boolean isPanelAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_HQ.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SA.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SK.value());
	}
	
	public boolean isCentralContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_HQ.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SA.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SK.value());
	}
	
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SA.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SK.value());
	}
	
	private String getUserRole() {
		if(getManagedBean().getCircularDocType()!=null) { // Cancellation Request
			if(getManagedBean().getCircularDocType()!=null && getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_PANEL)) {
				if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_HQ.value())) 
					return RoleCode.BPK_PCA_HQ.value();
				else if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SA.value()))
					return RoleCode.BPK_PCA_SA.value();
				else if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SK.value()))
					return RoleCode.BPK_PCA_SK.value();
			} else if(getManagedBean().getCircularDocType()!=null && getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)) {
				if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_HQ.value())) 
					return RoleCode.BPK_CCA_HQ.value();
				else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SA.value()))
					return RoleCode.BPK_CCA_SA.value();
				else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SK.value()))
					return RoleCode.BPK_CCA_SK.value();
			}
		} else { // Panel Circular Creation
			if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_HQ.value())) 
				return RoleCode.BPK_PCA_HQ.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SA.value()))
				return RoleCode.BPK_PCA_SA.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA_SK.value()))
				return RoleCode.BPK_PCA_SK.value();
		}
		
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) 
			return RoleCode.BPK_APPROVER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SA.value()))
			return RoleCode.BPK_APPROVER_SA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SK.value()))
			return RoleCode.BPK_APPROVER_SK.value();	
		else 
			return null;
	}
	
	public String saveCircularPanel(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			validateCircular(action);
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			if(MDC.get(LogKey.USER_ID)==null) {
				new LogKey(getFacesScopes().getUserSessionBean().getUserId());
			}
			
			CtCircular circular = getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			if(circularVer.getDocType()==null) {
				circularVer.setDocType(ContractConst.DOC_TYPE_PANEL_CIRCULAR);
			}
			
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getApproverModel().getTarget().size()==0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select at least one approver.",""));
				return null;
			}

			/*if(circularVer.getCtCircularZones().size()>0) {
				Map<String, CtCircularZone> circularZoneMap = new HashMap<String, CtCircularZone>();
				for(CtCircularZone ctCircularZone : circularVer.getCtCircularZones()) {
					circularZoneMap.put(ctCircularZone.toString(), ctCircularZone);
				}
				for(CtPanelItem ctPanelItem : circularVer.getCtPanelItems()) {
					for(CtPanelSupplier ctPanelSupplier : ctPanelItem.getCtPanelSuppliers()) {
						if(ctPanelSupplier.getCtCircularZone().getObjectId()==null) {
							ctPanelSupplier.getCtCircularZone().setObjectId(ctPanelSupplier.getCtCircularZone().toString());
						}
						if(ctPanelSupplier.getCtCircularZone()!=null && circularZoneMap.get(ctPanelSupplier.getCtCircularZone().getObjectId())!=null) {
							ctPanelSupplier.setCtCircularZone(circularZoneMap.get(ctPanelSupplier.getCtCircularZone().getObjectId()));
						}
					}
				}
			}*/
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			if(isBPKApprover()) {
				reqDTO.getCircularVer().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCircularVer().setApprovedDate(new Date());
			}
			
			/*if(circularVer.getPmUserGroup().getUserGroupId()!=null) {
				PmUserGroup pmUserGroup = getManagedBean().getUserGroupMap().get(circularVer.getPmUserGroup().getUserGroupId());
				Map<String, PmUser> userMap = new HashMap<String, PmUser>();
				if(pmUserGroup!=null) {
					for(PmUserGroupUser userGroupUser : pmUserGroup.getPmUserGroupUsers()) {
						userMap.put(userGroupUser.getPmUser().getUserName(), userGroupUser.getPmUser());
					}
				}
				reqDTO.setUserMap(userMap);
			}*/
			
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Circular No. : "+circularVer.getCircularNo();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				// save action with task ID not null will remain in the same page
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
				
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setRemarkList(resDTO.getRemarkList());
				getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
				
				for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
					for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()) {
						/*if(panelSupplier.getCtCircularZone()==null) {
							panelSupplier.setCtCircularZone(new CtCircularZone());
						}*/
					}
				}
				
				if(getManagedBean().getCircular().getCircularType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_PANEL)) {
					initializeRemarks();
				}
				ContractBackingBean ctBB = new ContractBackingBean();
				ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			} else {
				throw new EpException("000001");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof EpException) {
				EpException ep = (EpException) e;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		} finally {
			circularSO = null;
		}
		return "circularBase.xhtml";
	}
	
	public String saveCancelCircularReq(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			if(isBPKApprover()) {
				if(getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException("100022", new String[]{"Remark"});
				}
			}
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				if(getManagedBean().getApproverModel().getTarget().size()==0) {
					throw new EpException("100022", new String[]{"Selected approver(s)"});
				}
			}
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCancelCircularReq(getManagedBean().getCancelCircularReq());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());		
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
			reqDTO.setDocType(getManagedBean().getCircularDocType());
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			if(isBPKApprover()) {
				reqDTO.getCancelCircularReq().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCancelCircularReq().setApprovedDate(new Date());
			}
			
			/*if(getManagedBean().getCircularVer().getPmUserGroup().getUserGroupId()!=null) {
				getManagedBean().getCancelCircularReq().setPmUserGroup(getManagedBean().getCircularVer().getPmUserGroup());
				PmUserGroup pmUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getCancelCircularReq().getPmUserGroup().getUserGroupId());
				Map<String, PmUser> userMap = new HashMap<String, PmUser>();
				if(pmUserGroup!=null) {
					for(PmUserGroupUser userGroupUser : pmUserGroup.getPmUserGroupUsers()) {
						userMap.put(userGroupUser.getPmUser().getUserName(), userGroupUser.getPmUser());
					}
				}
				reqDTO.setUserMap(userMap);
			}*/
			
			CircularResDTO resDTO = circularSO.saveCancelCircularRequest(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Cancel Circular Request"));
				// save action with task ID not null will remain in the same page
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
				
				getManagedBean().setCancelCircularReq(resDTO.getCancelCircularReq());
				getManagedBean().setRemarkList(resDTO.getRemarkList());
				getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
				
				ContractBackingBean ctBB = new ContractBackingBean();
				ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());	
			} else {
				throw new EpException("000001");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof EpException) {
				EpException ep = (EpException) e;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	private void validateCircular(String outcome) {
		if(getManagedBean().getCircularVer().getCircularFileName()==null) {
			throw new EpException("100022", new String[]{"Circular Letter"});
		}

		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			if(getManagedBean().getCircularVer().getCircularRemarks().get(0).getRemark().length()==0 &&
				getManagedBean().getCircularVer().getZoneRemarks().get(0).getRemark().length()==0 &&
				getManagedBean().getCircularVer().getItemRemarks().get(0).getRemark().length()==0 ) {
				throw new EpException("100022", new String[]{"Circular Details/Zone/Item Remark"});
			}
		}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			if(getManagedBean().getApproverModel().getTarget().size()==0) {
				throw new EpException("100022", new String[]{"Selected approver(s)"});
			}
		}
	}
	
	public void searchCircularVer() {
		try { 
			circularVerPagingDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		}			
	}
	
	public void searchActiveCircularVer(ActionEvent event) {
		ICircularSO circularSO = null;
		try { 
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);

			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularType(getManagedBean().getCircularDocType());
			reqDTO.setFilterStatusId(ContractStatusConst.PQ_APPROVED);
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerList(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setCircularVerList(resDTO.getCircularVerList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		} finally {
			circularSO = null;
		}
	}
	
	public void addContactPerson(){
		log.info("PanelBackingBean.addContactPerson()");
		try {
			CtContactPerson contactPerson = new CtContactPerson();
	        getManagedBean().getCircularVer().getCtContactPersons().add(contactPerson);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteContactPerson(){
		log.info("PanelBackingBean.deleteContactPerson()");
		try {
			getManagedBean().getCircularVer().getCtContactPersons().removeAll(getManagedBean().getSelectedContactPersons());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelSupplier(){
			CtPanelSupplier panelSupplier = new CtPanelSupplier();
			panelSupplier.setSupplier(new SmSupplier());
			getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().add(panelSupplier);
	}
	
	public void deletePanelSupplier(){
		log.info("PanelBackingBean.deletePanelSupplier()");
		try {
			getManagedBean().getCtPanelItems().getCtPanelSuppliers().remove(getManagedBean().getSelectedPanelSupplier());
			getManagedBean().setSelectedPanelSupplier(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addCancelCircularListener() {
		log.info("PanelBackingBean.addCancelCircularListener()");
		try {
			if(getManagedBean().getCancelledCircularList()==null) {
				CircularReqDTO reqDTO = new CircularReqDTO();
				ISearchBean<CtCancelCircular> searchBean = new SearchBean<CtCancelCircular>();
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"ctCancelCircularVer.ctCircular.circularType", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						ContractConst.CIRCULAR_DOC_TYPE_PANEL));
				reqDTO.setCancelCircularSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCancelledCircular(reqDTO);
				if(resDTO.isSuccessful()) {
					getManagedBean().setCancelledCircularList(resDTO.getCancelCircularResultBean().getResultList());
				}
			}
			getManagedBean().setSelectedCancelledCircularList(new ArrayList<CtCancelCircular>());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addCancelledCircular(){
		log.info("PanelBackingBean.addCancelledCircular()");
        try {	
			getManagedBean().getCircularVer().getCtCancelCircularList().addAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelCircular(){
		log.info("PanelBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelCirculars());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addCircularZone(){
		/*log.info("PanelBackingBean.addCircularZone()");
        try {	
        	CtCircularZone circularZone = new CtCircularZone();
			getManagedBean().getCircularVer().getCtCircularZones().add(circularZone);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteCircularZone(){
		/*log.info("PanelBackingBean.deleteCircularZone() : "+getManagedBean().getSelectedCircularZone());
		try {
			getManagedBean().getCircularVer().getCtCircularZones().remove(getManagedBean().getSelectedCircularZone());
			getManagedBean().setSelectedCircularZone(null);
			getManagedBean().setSelectedCircularZoneDtl(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void selectZoneEvent() {
	//	getManagedBean().setSelectedCircularZoneDtl(null);
	}
	
	public void selectItemZoneEvent() {
		getManagedBean().setSelectedPanelSupplier(null);
	}
	
	public void addCircularZoneDtl(){
		/*log.info("PanelBackingBean.addCircularZoneDtl()");
        try {	
        	CtCircularZoneDtl circularZoneDtl = new CtCircularZoneDtl();
        	circularZoneDtl.setState(new PmState());
        	circularZoneDtl.setDistrict(new PmDistrict());
			getManagedBean().getSelectedCircularZone().getCtCircularZoneDtls().add(circularZoneDtl);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteCircularZoneDtl(){
		/*log.info("PanelBackingBean.deleteCircularZoneDtl()");
		try {
			getManagedBean().getSelectedCircularZone().getCtCircularZoneDtls().remove(getManagedBean().getSelectedCircularZoneDtl());
			getManagedBean().setSelectedCircularZoneDtl(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void addPanelItem(){
		log.info("PanelBackingBean.addPanelItem()");
        try {	
        		 CtPanelItem panelItem = new CtPanelItem();
				 panelItem.setItemCategoryId(new PmParameter());
				 panelItem.setPanelCoverageId(new PmParameter());
				 panelItem.setPmUom(new PmUom());
				 panelItem.setLocationLevelId(new PmParameter());
				 panelItem.setCtPanelZones(null);
				 this.getManagedBean().getCircularVer().getCtPanelItems().add(panelItem);
        	 
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelItem(){
		for(int i = 0; i<selectedPanelItem.length; i++){
			CtPanelItem panelItem = selectedPanelItem[i];
			getManagedBean().getCircularVer().getCtPanelItems().remove(panelItem);
		}selectedPanelItem = null;
	}
	
	public void addPanelZone(){
		CtPanelZone ctPanelZone = new CtPanelZone();
		this.getManagedBean().getCtPanelItems().getCtPanelZones().add(ctPanelZone);
	}
	
	public void deletePanelZone(){
		
		this.getManagedBean().getCtPanelItems().getCtPanelZones().remove(getManagedBean().getSelectedPanelZone());
	}
	
	public void selectPanelItemEvent() {
		//getManagedBean().setSelectedCircularZone(null);
	}
	
	public void selectActiveCirculars(){
		log.info("PanelBackingBean.selectActiveCirculars()");
        try {	
        	CtCancelCircular cancelCircular = null;
        	List<CtCancelCircular> cancelCircularList = new ArrayList<CtCancelCircular>();
        	/*for(CtCircularVer circularVer : getManagedBean().getSelectedCircularVerList()) {
        		cancelCircular = new CtCancelCircular();
        		cancelCircular.setCtCancelCircularVer(circularVer);
        		cancelCircularList.add(cancelCircular);
        	}*/
        	if(getManagedBean().getCancelCircularReq()==null) {
        		getManagedBean().setCancelCircularReq(new CtCancelCircularReq());
        	}
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().addAll(cancelCircularList);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelReqCircular() {
		log.info("PanelBackingBean.deleteCancelReqCircular()");
		try {
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public SelectItem[] getStateDropdownValues() {
		SelectItem[] items = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1l);
			StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
			
		    items = new SelectItem[resDTO.getStateList().size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmState state: resDTO.getStateList()) {
		    	items[i++] = new SelectItem(state.getStateId(), state.getStateName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public SelectItem[] getCircularDocTypeValues() {
		SelectItem[] items = null;
		try {
		    if(isPanelAdmin() && isCentralContractAdmin()) { 
		    	items = new SelectItem[2];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_PANEL, "Panel Circular");
		    	items[1] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT, "Central Contract Circular");
		    } else if(isPanelAdmin()) {
		    	items = new SelectItem[1];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_PANEL, "Panel Circular");
		    } else if(isCentralContractAdmin()) {
		    	items = new SelectItem[1];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT, "Central Contract Circular");
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public SelectItem[] getStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			
			List<Long> statusIdList = new ArrayList<Long>();
			statusIdList.add(ContractStatusConst.PQ_PENDING_SUBMISSION);
			statusIdList.add(ContractStatusConst.PQ_PENDING_REVISION);
			statusIdList.add(ContractStatusConst.PQ_PENDING_APPROVAL);
			statusIdList.add(ContractStatusConst.PQ_PENDING_REVISION_APPROVAL);
			statusIdList.add(ContractStatusConst.PQ_APPROVED);
			statusIdList.add(ContractStatusConst.PQ_REJECTED);
			
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
			items = new SelectItem[statusIdList.size()+1];
			int i = 0;
			items[i++] = new SelectItem(null, "- Select One -");
			for(PmStatusDesc statusDesc : resDTO.getStatusDescList()) {
				if(statusIdList.contains(statusDesc.getPmStatus().getStatusId())) {
					items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			statusSO = null;
		}
	    return items;
	}
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		try {
			items = new SelectItem[getManagedBean().getUserGroupMap().values().size()];
			int i = 0;
			for(PmUserGroup userGroup : getManagedBean().getUserGroupMap().values()) {
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public void userGroupChangeListener() {
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		/*for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}*/
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
	}
	
	public SelectItem[] getZonalLevelDropDown() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, "- Select One -");
		int i = 1;
		for(SelectItem item : getDropdownCodeValues(ParameterTypeConst.LOCATION_LEVEL)) {
			if(item.getValue()==null) {
				continue;
			}
			if(item.getValue().toString().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE) ||
				item.getValue().toString().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				items[i++] = item;
			}
		}
		return items;
	}
	
	public String gotoPage() {
		/*String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
		Long circularVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("circularVerId"));
		log.info("Action :"+action+" Id :"+circularVerId);
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			
			CircularResDTO resDTO = null;
			getManagedBean().setSelectedCircularZone(null);
			getManagedBean().setSelectedPanelItem(null);
			if(action.equalsIgnoreCase("view")) {
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				
				if(resDTO.getCircularVer().getZonalLevel()==null) {
					PmParameter parameter = new PmParameter();
					parameter.setParameterId(-1L);
					resDTO.getCircularVer().setZonalLevel(parameter);
				}						
				for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
					for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()) {
						if(panelSupplier.getCtCircularZone()==null) {
							panelSupplier.setCtCircularZone(new CtCircularZone());
						}
					}
				}
				getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(false);
				getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
								
				if(resDTO.getCircularVer().getZonalLevel()==null) {
					PmParameter parameter = new PmParameter();
					parameter.setParameterId(-1L);
					resDTO.getCircularVer().setZonalLevel(parameter);
				}							
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(true);
				getManagedBean().setOutComes(new String[]{"Save","Submit"});
			} else if(action.equalsIgnoreCase("cancel")) {
				
			}
			
			getManagedBean().getApproverModel().getSource().clear();
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			return "circularBase";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}*/
		return null;
	}
	
	public void preRenderView() {
		ICircularSO circularSO = null;
		try {
			System.out.println("**********preRenderView**************");
			// BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			System.out.println("taskId:" + taskId);

			if(taskId==null) {
				return;
			}
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin().getUserName());
			getManagedBean().setTaskId(taskId);
			String[] outcome = new String[res.getAction().length+1];
			outcome[0] = "Save";
			int i = 1;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}
			getManagedBean().setOutComes(outcome);			
			
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getDocId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerById(circularReqDTO);
								
			
			/*for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
				for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()) {
					if(panelSupplier.getCtCircularZone()==null) {
						panelSupplier.setCtCircularZone(new CtCircularZone());
					}
				}
			}*/
			
			getManagedBean().getApproverModel().getSource().clear();
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
			getManagedBean().setCircularVer(resDTO.getCircularVer());
			
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
	}
	
	public void preRenderViewCancelReq() {
		ICircularSO circularSO = null;
		try {
			System.out.println("**********preRenderView**************");
			// BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			System.out.println("taskId:" + taskId);

			if(taskId==null) {
				return;
			}
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularCancellationDataType dataType = (CTCircularCancellationDataType) obj;
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin().getUserName());
			getManagedBean().setTaskId(taskId);
			String[] outcome = new String[res.getAction().length+1];
			outcome[0] = "Save";
			int i = 1;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}
			getManagedBean().setOutComes(outcome);	
			
			CircularReqDTO cancelReqDTO = new CircularReqDTO();
			cancelReqDTO.setCancelCircularReqId(dataType.getDocId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCancelCircularReqById(cancelReqDTO);
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			getManagedBean().setCancelCircularReq(resDTO.getCancelCircularReq());
			
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}		
	}
	
	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
		getManagedBean().getCircularVer().setCircularFileName(item.getFileName());
		getManagedBean().getCircularVer().setCircularFilePath(filePath);
	}
	
	public void downloadFile() throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = getManagedBean().getCircularVer().getCircularFileName();
		String filePath = getManagedBean().getCircularVer().getCircularFilePath();
		
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
	
	private class CircularVerPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularVerPagingDataModel() {
			super("circularVerDt");
		}
		
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.circularType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_PANEL));
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchCircularNo()));	
				}
				if(getManagedBean().getSearchCircularName()!=null && getManagedBean().getSearchCircularName().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("name",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularName()));	
				}
				if(getManagedBean().getSearchSupplierName()!=null && getManagedBean().getSearchSupplierName().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctPanelSuppliers.supplier.supplierName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSupplierName()));	
				}			
				if(getManagedBean().getSearchFileRefNo()!=null && getManagedBean().getSearchFileRefNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularFileRefNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchFileRefNo()));	
				}	
				if(getManagedBean().getSearchItemName()!=null && getManagedBean().getSearchItemName().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctPanelItems.itemName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchItemName()));	
				}		
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
					SearchBetweenValue<Date> betweenValue = new SearchBetweenValue<Date>(getManagedBean().getSearchStartDate(), getManagedBean().getSearchEndDate());
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
				}	
				
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getManagedBean().getSearchStatusId());
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getCircularVerResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}

	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(
			IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
	
	
	public String getItemGeneralCode(Long parameterId){
		if(parameterId == null || parameterId.intValue() == 0)
			return ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC;
		
		  IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setLanguageCode("EN");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
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

	public List<CtPanelSupplier> getPanelSupplierList() {
		List<CtPanelSupplier> panelSuppliers = new ArrayList<CtPanelSupplier>();
		/*if(getManagedBean().getSelectedPanelItem()!=null) {
			List<CtPanelSupplier> panelSupplierList = getManagedBean().getSelectedPanelItem().getCtPanelSuppliers();
			if(getManagedBean().getCircularVer().getZonalLevel().getParameterCode()!=null) {
				for(CtPanelSupplier ctPanelSupplier : panelSupplierList) {
					if(getManagedBean().getSelectedCircularZone()!=null && getManagedBean().getSelectedCircularZone().getName().equalsIgnoreCase(ctPanelSupplier.getCtCircularZone().getName())) {
						panelSuppliers.add(ctPanelSupplier);
					}
				}
			} else {
				panelSuppliers = panelSupplierList;
			}
		}*/
		return panelSuppliers;
	}
	
	public void setCtPanelZoneListner(CtPanelItem ctPanelItem){
		this.getManagedBean().setCtPanelItems(ctPanelItem);
	}

	public CtPanelItem[] getSelectedPanelItem() {
		return selectedPanelItem;
	}

	public void setSelectedPanelItem(CtPanelItem[] selectedPanelItem) {
		this.selectedPanelItem = selectedPanelItem;
	}
	
	
	
}
