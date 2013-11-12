/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.state.IStateSO;
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
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelState;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplierItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

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
		}
		if(getManagedBean().getCircularVer().getCtPanelSuppliers()!=null) {
			for(CtPanelSupplier panelSupplier : getManagedBean().getCircularVer().getCtPanelSuppliers()) {
				if(panelSupplier.getFactoringOrgProfile()==null) {
					panelSupplier.setFactoringOrgProfile(new PmOrgProfile());
				}
			}
		}
		if(getManagedBean().getApproverModel()==null) {
			List<String> source = getAvailableApproverList();  
	        List<String> target = new ArrayList<String>();  
	        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>(); 
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			//TODO
			reqDTO.setOrgProfileId(1250l);
			UserResDTO resDTO = userSO.retrieveUserGroupList(reqDTO);
			for(PmUserGroupUser user : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				availableApprovers.add(user.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
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
			if(circularLetter!=null) {
				circularVer.setCircularFileName(circularLetter.getFileName());
				circularVer.setCircularFilePath(circularLetter.getFileName());
			} else {
				// TODO
				circularVer.setCircularFileName("temp");
				circularVer.setCircularFilePath("tempPath");
			}
			
			String token = getManagedBean().getToken();
			if(token==null) {
				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
				token = bpmSO.authenticate("roslan", "welcome1");
				if(token==null) {
					throw new EpException("BPM token not found");
				}
			}

			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.","Circular No. : "+circularVer.getCircularNo()));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
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
	
	public void searchCircularVer() {
		try { 
			circularVerPagingDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
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
			// TODO supplir should selected from LOV
			SmSupplier supplier = new SmSupplier();
			supplier.setSupplierId(27l);
			PmOrgProfile factoringOrgProfile = new PmOrgProfile();
			factoringOrgProfile.setOrgProfileId(1245L);
			panelSupplier.setSupplier(supplier);
			panelSupplier.setFactoringOrgProfile(factoringOrgProfile);
	        getManagedBean().getCircularVer().getCtPanelSuppliers().add(panelSupplier);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelSupplier(){
		log.info("PanelBackingBean.deletePanelSupplier()");
		try {
			getManagedBean().getCircularVer().getCtPanelSuppliers().remove(getManagedBean().getSelectedPanelSupplier());
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
		log.info("PanelBackingBean.deletePanelSupplier()");
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
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
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
			getManagedBean().getCircularVer().getCtPanelItems().remove(getManagedBean().getSelectedPanelItem());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelSupplierItem(){
		log.info("PanelBackingBean.addPanelSupplierItem()");
        try {	
        	CtPanelSupplierItem panelSupplierItem = new CtPanelSupplierItem();
        	panelSupplierItem.setCtPanelSupplier(new CtPanelSupplier());
        	panelSupplierItem.setCtPanelZone(new CtPanelZone());
			getManagedBean().getSelectedPanelItem().getCtPanelSupplierItems().add(panelSupplierItem);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelSupplierItem(){
		log.info("PanelBackingBean.deletePanelSupplierItem()");
		try {
			getManagedBean().getSelectedPanelItem().getCtPanelSupplierItems().remove(getManagedBean().getSelectedPanelSupplierItem());
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
			if(action.equalsIgnoreCase("view")) {
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				if(resDTO.getCircularVer().getCircularTypeCode()==null) {
					PmCode code = new PmCode();
					code.setCodeId(-1L);
					resDTO.getCircularVer().setCircularTypeCode(code);
				}
				for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
					for(CtPanelSupplierItem panelSupplierItem : panelItem.getCtPanelSupplierItems()) {
						if(panelSupplierItem.getCtPanelZone()==null) {
							panelSupplierItem.setCtPanelZone(new CtPanelZone());
						}
					}
				}
				getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(true);
				getManagedBean().setOutComes(new String[]{"SAVE","SUBMIT"});
			} else if(action.equalsIgnoreCase("cancel")) {
				
			}
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			return "circularBase";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	public void taskListAction(Long circularVerId) {
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerById(reqDTO);
			if(resDTO.getCircularVer().getCircularTypeCode()==null) {
				PmCode code = new PmCode();
				code.setCodeId(-1L);
				resDTO.getCircularVer().setCircularTypeCode(code);
			}
			for(CtPanelItem panelItem : resDTO.getCircularVer().getCtPanelItems()) {
				for(CtPanelSupplierItem panelSupplierItem : panelItem.getCtPanelSupplierItems()) {
					if(panelSupplierItem.getCtPanelZone()==null) {
						panelSupplierItem.setCtPanelZone(new CtPanelZone());
					}
				}
			}
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
			getManagedBean().setCircularVer(resDTO.getCircularVer());
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
