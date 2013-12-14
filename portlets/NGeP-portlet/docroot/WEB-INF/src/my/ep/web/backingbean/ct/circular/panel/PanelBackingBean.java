/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.circular.panel.CtPanelState;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
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
	
	private IPagingDataModel<CtCancelCircular> cancelledCircularDataModel = new CancelledCircularPagingDataModel();
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	private UploadedFile circularLetter;
	
	public PanelBackingBean(){
		if(getManagedBean().getOutComes()==null) {
			getManagedBean().setOutComes(new String[]{"SAVE","SUBMIT"});
			getManagedBean().setCircularVer(new CtCircularVer());
			
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
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			FacesScopes facesScopes = new FacesScopes();
			reqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(getManagedBean().getCircularVer().getPmUserGroup()==null) {
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getCircularVer().setPmUserGroup(pmUserGroup);
			}
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
					getManagedBean().getCircularVer().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
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
	
	public boolean isPanelAdmin() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser("BPK_PCA_HQ") || facesScopes.hasRoleUser("BPK_PCA_SA") || facesScopes.hasRoleUser("BPK_PCA_SK");
	}
	
	public boolean isCentralContractAdmin() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser("BPK_CCA_HQ") || facesScopes.hasRoleUser("BPK_CCA_SA") || facesScopes.hasRoleUser("BPK_CCA_SK");
	}
	
	public boolean isBPKApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser("BPK_APPROVER") || facesScopes.hasRoleUser("BPK_APPROVER_SA") || facesScopes.hasRoleUser("BPK_APPROVER_SK");
	}
	
	public String saveCircularPanel(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CtCircular circular = getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getApproverModel().getTarget().size()==0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select at least one approver.",""));
				return null;
			}
			
			if(circularLetter!=null) {
				circularVer.setCircularFileName(circularLetter.getFileName());
				circularVer.setCircularFilePath(circularLetter.getFileName());
			} else {
				// TODO
				circularVer.setCircularFileName("temp");
				circularVer.setCircularFilePath("tempPath");
			}
			
			if(circularVer.getCtPanelZones().size()>0) {
				Map<String, CtPanelZone> panelZoneMap = new HashMap<String, CtPanelZone>();
				for(CtPanelZone ctPanelZone : circularVer.getCtPanelZones()) {
					panelZoneMap.put(ctPanelZone.toString(), ctPanelZone);
				}
				for(CtPanelItem ctPanelItem : circularVer.getCtPanelItems()) {
					for(CtPanelSupplier ctPanelSupplier : ctPanelItem.getCtPanelSuppliers()) {
						if(panelZoneMap.get(ctPanelSupplier.getCtPanelZone().getObjectId())!=null) {
							ctPanelSupplier.setCtPanelZone(panelZoneMap.get(ctPanelSupplier.getCtPanelZone().getObjectId()));
						}
					}
				}
			}
			
			FacesScopes facesScope = new FacesScopes();
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			reqDTO.setOutcome(action);
			reqDTO.setToken(facesScope.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setCircularAdmin(getManagedBean().getCircularAdmin());
			
			if(circularVer.getPmUserGroup().getUserGroupId()!=null) {
				PmUserGroup pmUserGroup = getManagedBean().getUserGroupMap().get(circularVer.getPmUserGroup().getUserGroupId());
				Map<String, PmUser> userMap = new HashMap<String, PmUser>();
				if(pmUserGroup!=null) {
					for(PmUserGroupUser userGroupUser : pmUserGroup.getPmUserGroupUsers()) {
						userMap.put(userGroupUser.getPmUser().getUserName(), userGroupUser.getPmUser());
					}
				}
				reqDTO.setUserMap(userMap);
			}
			
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Circular No. : "+circularVer.getCircularNo();
				if(circularVer.getUpdateNo()!=null) {
					message = message + ". Update No. : "+circularVer.getUpdateNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				// save action with task ID not null will remain in the same page
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	public String saveCancelCircularReq(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			FacesScopes facesScope = new FacesScopes();
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCancelCircularReq(getManagedBean().getCancelCircularReq());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());		
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
			reqDTO.setDocType(getManagedBean().getCircularDocType());
			reqDTO.setOutcome(action);
			reqDTO.setToken(facesScope.getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setCircularAdmin(getManagedBean().getCircularAdmin());
			
			if(getManagedBean().getCircularVer().getPmUserGroup().getUserGroupId()!=null) {
				getManagedBean().getCancelCircularReq().setPmUserGroup(getManagedBean().getCircularVer().getPmUserGroup());
				PmUserGroup pmUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getCancelCircularReq().getPmUserGroup().getUserGroupId());
				Map<String, PmUser> userMap = new HashMap<String, PmUser>();
				if(pmUserGroup!=null) {
					for(PmUserGroupUser userGroupUser : pmUserGroup.getPmUserGroupUsers()) {
						userMap.put(userGroupUser.getPmUser().getUserName(), userGroupUser.getPmUser());
					}
				}
				reqDTO.setUserMap(userMap);
			}
			
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
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Cancel circular request save failed."));
		} finally {
			circularSO = null;
		}
		return null;
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

			ISearchBean<CtCircularVer> searchBean = new SearchBean<CtCircularVer>();
			if(getManagedBean().getCircularDocType()!=null) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCircularDocType()));
			}
			if(getManagedBean().getSearchStatusId()!=null) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));	
			}					
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerSearchBean(searchBean);
			
			CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setCircularVerList(resDTO.getCircularVerResultBean().getResultList());
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
			getManagedBean().getCircularVer().getCtContactPersons().remove(getManagedBean().getSelectedContactPerson());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelSupplier(){
		log.info("PanelBackingBean.addPanelSupplier()");
		try {
			CtPanelSupplier panelSupplier = new CtPanelSupplier();
			panelSupplier.setSupplier(new SmSupplier());
			panelSupplier.setCtPanelZone(new CtPanelZone());
			getManagedBean().getSelectedPanelItem().getCtPanelSuppliers().add(panelSupplier);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelSupplier(){
		log.info("PanelBackingBean.deletePanelSupplier()");
		try {
			getManagedBean().getSelectedPanelItem().getCtPanelSuppliers().remove(getManagedBean().getSelectedPanelSupplier());
			getManagedBean().setSelectedPanelSupplier(null);
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
			getManagedBean().getCircularVer().getCtCancelCircularList().remove(getManagedBean().getSelectedCancelCircular());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelZone(){
		log.info("PanelBackingBean.addPanelZone()");
        try {	
        	CtPanelZone panelZone = new CtPanelZone();
			getManagedBean().getCircularVer().getCtPanelZones().add(panelZone);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelZone(){
		log.info("PanelBackingBean.deletePanelZone() : "+getManagedBean().getSelectedPanelZone());
		try {
			getManagedBean().getCircularVer().getCtPanelZones().remove(getManagedBean().getSelectedPanelZone());
			getManagedBean().setSelectedPanelZone(null);
			getManagedBean().setSelectedPanelState(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void selectZoneEvent() {
		getManagedBean().setSelectedPanelState(null);
	}
	
	public void addPanelState(){
		log.info("PanelBackingBean.addPanelState()");
        try {	
        	CtPanelState panelState = new CtPanelState();
        	panelState.setState(new PmState());
			getManagedBean().getSelectedPanelZone().getCtPanelStates().add(panelState);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelState(){
		log.info("PanelBackingBean.deletePanelState()");
		try {
			getManagedBean().getSelectedPanelZone().getCtPanelStates().remove(getManagedBean().getSelectedPanelState());
			getManagedBean().setSelectedPanelState(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelItem(){
		log.info("PanelBackingBean.addPanelItem()");
        try {	
        	CtPanelItem panelItem = new CtPanelItem();
			getManagedBean().getCircularVer().getCtPanelItems().add(panelItem);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelItem(){
		log.info("PanelBackingBean.deletePanelItem() : "+getManagedBean().getSelectedPanelItem());
		try {
			getManagedBean().getCircularVer().getCtPanelItems().remove(this.getManagedBean().getSelectedPanelItem());
			getManagedBean().setSelectedPanelItem(null);
			getManagedBean().setSelectedPanelSupplier(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void selectPanelItemEvent() {
		getManagedBean().setSelectedPanelSupplier(null);
	}
	
	public void addPanelItemSpec(){
		log.info("PanelBackingBean.addPanelItemSpec()");
        try {	
        	CtPanelItemSpec panelItemSpec = new CtPanelItemSpec();
        	System.out.println("1###"+getManagedBean().getSelectedPanelItem());
        	System.out.println("2###"+getManagedBean().getSelectedPanelItem().getCtPanelItemSpecs());
			getManagedBean().getSelectedPanelItem().getCtPanelItemSpecs().add(panelItemSpec);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void selectActiveCirculars(){
		log.info("PanelBackingBean.selectActiveCirculars()");
        try {	
        	CtCancelCircular cancelCircular = null;
        	List<CtCancelCircular> cancelCircularList = new ArrayList<CtCancelCircular>();
        	for(CtCircularVer circularVer : getManagedBean().getSelectedCircularVerList()) {
        		cancelCircular = new CtCancelCircular();
        		cancelCircular.setCtCancelCircularVer(circularVer);
        		cancelCircularList.add(cancelCircular);
        	}
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
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().remove(getManagedBean().getSelectedCancelCircular());
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
		PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getCircularVer().getPmUserGroup().getUserGroupId());
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
	}
	
	public SelectItem[] getPanelZoneDropdown() {
		SelectItem[] items = null;
		try {
		    items = new SelectItem[getManagedBean().getCircularVer().getCtPanelZones().size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(CtPanelZone panelZone : getManagedBean().getCircularVer().getCtPanelZones()) {
		    	if(panelZone.getObjectId()==null) {
		    		panelZone.setObjectId(panelZone.toString());
		    	}
		    	items[i++] = new SelectItem(panelZone.getObjectId(), panelZone.getName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	// Temporary table. this should replace with supplier lov later
	public SelectItem[] getPanelSupplierDropdown() {
		SelectItem[] items = null;
		try {
		    items = new SelectItem[4];
		    items[0] = new SelectItem(0L, "- Select One -");
		    items[1] = new SelectItem(1L, "ABC News");
		    items[2] = new SelectItem(2L, "Maxis Malaysia Berhad");
		    items[3] = new SelectItem(3L, "MDEC malaysia");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public String gotoPage() {
		String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
		Long circularVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("circularVerId"));
		log.info("Action :"+action+" Id :"+circularVerId);
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			
			CircularResDTO resDTO = null;
			getManagedBean().setSelectedPanelZone(null);
			getManagedBean().setSelectedPanelItem(null);
			if(action.equalsIgnoreCase("view")) {
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				if(resDTO.getCircularVer().getUpdateType()==null) {
					PmCode code = new PmCode();
					code.setCodeId(-1L);
					resDTO.getCircularVer().setUpdateType(code);
				}
				for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
					for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()) {
						if(panelSupplier.getCtPanelZone()==null) {
							panelSupplier.setCtPanelZone(new CtPanelZone());
						}
					}
				}
				getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				if(resDTO.getCircularVer().getUpdateType()==null) {
					PmCode code = new PmCode();
					code.setCodeId(-1L);
					resDTO.getCircularVer().setUpdateType(code);
				}				
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(true);
				getManagedBean().setOutComes(new String[]{"SAVE","SUBMIT"});
			} else if(action.equalsIgnoreCase("cancel")) {
				
			}
			
			getManagedBean().getApproverModel().getSource().clear();
			if(resDTO.getCircularVer().getPmUserGroup()!=null) {
				for(PmUserGroupUser user : resDTO.getCircularVer().getPmUserGroup().getPmUserGroupUsers()) {
					getManagedBean().getApproverModel().getSource().add(user.getPmUser().getUserName());
				}
			} else {
				resDTO.getCircularVer().setPmUserGroup(new PmUserGroup());
			}
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			return "circularBase";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
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
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getCircularId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerById(circularReqDTO);
			if(resDTO.getCircularVer().getUpdateType()==null) {
				PmCode code = new PmCode();
				code.setCodeId(-1L);
				resDTO.getCircularVer().setUpdateType(code);
			}
			for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
				for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()) {
					if(panelSupplier.getCtPanelZone()==null) {
						panelSupplier.setCtPanelZone(new CtPanelZone());
					}
				}
			}
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
			getManagedBean().setCircularVer(resDTO.getCircularVer());
			
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
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin());
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			
			CircularReqDTO cancelReqDTO = new CircularReqDTO();
			cancelReqDTO.setCancelCircularReqId(dataType.getCircularIds().get(0).longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCancelCircularReqById(cancelReqDTO);
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			getManagedBean().setCancelCircularReq(resDTO.getCancelCircularReq());
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}		
	}
	
	private class CancelledCircularPagingDataModel extends PagingDataModel<CtCancelCircular> {
		private static final long serialVersionUID = 1L;

		public CancelledCircularPagingDataModel() {
			super("cancelledCclDt");
		}
		
		@Override
		public ISearchResultBean<CtCancelCircular> pageChangeListener(ISearchBean<CtCancelCircular> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCancelCircularReq.status.statusId",SearchOperator.OPERATOR_EQUALS.value(),ContractStatusConst.APPROVED));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircularVer.circularVerId",SearchOperator.OPERATOR_IS_NULL.value(),null));
				
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCancelCircularSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCancelledCircular(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getCancelCircularResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.docType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_PANEL));
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
				if(getManagedBean().getSearchCircularTypeCodeId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularTypeCode.codeId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchCircularTypeCodeId()));	
				}				
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
					SearchBetweenValue<Date> betweenValue = new SearchBetweenValue<Date>(getManagedBean().getSearchStartDate(), getManagedBean().getSearchEndDate());
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
				}	
				if(getManagedBean().getSearchStatusId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));	
				}					
				
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				
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
	
	public UploadedFile getCircularLetter() {
		return circularLetter;
	}

	public void setCircularLetter(UploadedFile circularLetter) {
		this.circularLetter = circularLetter;
	}

	public IPagingDataModel<CtCancelCircular> getCancelledCircularDataModel() {
		return cancelledCircularDataModel;
	}

	public void setCancelledCircularDataModel(
			IPagingDataModel<CtCancelCircular> cancelledCircularDataModel) {
		this.cancelledCircularDataModel = cancelledCircularDataModel;
	}

	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(
			IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}

}
