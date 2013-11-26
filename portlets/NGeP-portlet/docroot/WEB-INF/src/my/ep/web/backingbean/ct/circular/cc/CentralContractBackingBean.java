/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.cc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.PmCodeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.cc.CentralContractManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

/**
 * Central Contract Circular Backingbean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@RequestScoped
public class CentralContractBackingBean extends BaseBackingBean<CentralContractManagedBean>{
	
	private static Logger log = Logger.getLogger(CentralContractBackingBean.class);
	
	private IPagingDataModel<CtCancelCircular> cancelledCircularDataModel = new CancelledCircularPagingDataModel();
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	public CentralContractBackingBean(){
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		
		if(getManagedBean().getOutComes()==null) {
			getManagedBean().setOutComes(new String[]{"SAVE","SUBMIT"});
			getManagedBean().setCircularVer(new CtCircularVer());
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
	
	public void searchContract(){
		IContractSO contractSO = null;
		ICircularSO circularSO = null;
		try { 
			contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();

			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractTypeCode.code",SearchOperator.OPERATOR_EQUALS.value(),PmCodeConst.CONTRACT_TYPE_CENTRAL));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),ContractStatusConst.APPROVED));
			//TODO shouldn't use this method as will cause performance issue. Please find a better way.
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractId",SearchOperator.OPERATOR_NOT_IN.value(), circularSO.retrieveApprovedCircularContractId(null).getContractIdList()));
			
			if(getManagedBean().getSearchContractNo()!=null && getManagedBean().getSearchContractNo().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchContractNo()));	
			}
			if(getManagedBean().getSearchContractName()!=null && getManagedBean().getSearchContractName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractName()));	
			}
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(searchBean);
			
			ContractResDTO resDTO = contractSO.searchContract(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setContractList(resDTO.getContractResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		} finally {
			contractSO = null;
			circularSO = null;
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
	
	public void addContactPerson(){
		log.info("CentralContractBackingBean.addContactPerson()");
		try {
			CtContactPerson contactPerson = new CtContactPerson();
	        getManagedBean().getCircularVer().getCtContactPersons().add(contactPerson);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteContactPerson(){
		log.info("CentralContractBackingBean.deleteContactPerson()");
		try {
			getManagedBean().getCircularVer().getCtContactPersons().remove(getManagedBean().getSelectedContactPerson());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	public void deleteCircularContract(){
		log.info("CentralContractBackingBean.deleteCircularContract()");
		try {
			getManagedBean().getCircularVer().getCtCircularContracts().remove(getManagedBean().getSelectedCircularContract());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	public void addCancelledCircular(){
		log.info("CentralContractBackingBean.addCancelledCircular()");
        try {	
        	for(CtCancelCircular cancelCircular : getManagedBean().getSelectedCancelledCircularList()) {
        		getManagedBean().getCircularVer().getCtCancelCircularList().add(cancelCircular);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelCircular(){
		log.info("CentralContractBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().remove(getManagedBean().getSelectedCancelCircular());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addSelectedContract(){
		log.info("CentralContractBackingBean.addSelectedContract()");
        try {	
        	CtCircularContract circularContract = null;
        	for(CtContract contract : getManagedBean().getSelectedContractList()) {
        		circularContract = new CtCircularContract();
        		circularContract.setCtContract(contract);
        		getManagedBean().getCircularVer().getCtCircularContracts().add(circularContract);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.docType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchCircularNo()));	
				}
				if(getManagedBean().getSearchCircularName()!=null && getManagedBean().getSearchCircularName().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("name",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularName()));	
				}
				if(getManagedBean().getSearchFileRefNo()!=null && getManagedBean().getSearchFileRefNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularFileRefNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchFileRefNo()));	
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
	
	public String saveCircular(String outcome){
		log.debug("CentralContractBackingBean.saveCircular("+outcome+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CtCircular circular = getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setDocType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			// TODO
			circularVer.setCircularFileName("temp");
			circularVer.setCircularFilePath("tempPath");
			
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
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(token);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Circular No. : "+circularVer.getCircularNo();
				if(circularVer.getUpdateNo()!=null) {
					message = message + ". Update No. : "+circularVer.getUpdateNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!token.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
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

	public void fileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		String fileName = file.getFileName();
		byte[] fileBytes = file.getContents();
		log.info("Processed uploaded file " + fileName + " of size:" + fileBytes.length);
		this.getManagedBean().getCircularVer().setCircularFileName(fileName);
		this.getManagedBean().getCircularVer().setCircularFilePath("C:\\upload");
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,	"The file was uploaded successfully.", null);
		FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("C:\\upload\\" + fileName);
			fos.write(fileBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
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
				getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				if(resDTO.getCircularVer().getCircularTypeCode()==null) {
					PmCode code = new PmCode();
					code.setCodeId(-1L);
					resDTO.getCircularVer().setCircularTypeCode(code);
				}				
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

	public IPagingDataModel<CtCancelCircular> getCancelledCircularDataModel() {
		return cancelledCircularDataModel;
	}

	public void setCancelledCircularDataModel(IPagingDataModel<CtCancelCircular> cancelledCircularDataModel) {
		this.cancelledCircularDataModel = cancelledCircularDataModel;
	}

	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}

}