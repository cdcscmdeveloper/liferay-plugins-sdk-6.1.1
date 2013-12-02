/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.maksud.MaksudReqDTO;
import my.ep.dto.pm.common.maksud.MaksudResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 * Backing Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */

@ManagedBean(name = "contractRequestBackingBean")
@RequestScoped
public class ContractRequestBackingBean extends BaseBackingBean<ContractRequestManagedBean>{
	private static Logger log = Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private IPagingDataModel<VCtActiveContract> activeContractPagingDataModel = new ActiveContractPagingDataModel();

	public ContractRequestBackingBean(){

		if(getManagedBean().getFlRequest() == null){
			System.out.println("==contructor==");
			getManagedBean().setFlRequest(new FlFulfilmentRequest());
			getManagedBean().getFlRequest().setPmDocStatus(new PmStatus());
			getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setReceivedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCreatedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCtContract(new CtContract());
			getManagedBean().getFlRequest().getCtContract().setContractType(new PmCode());
			getManagedBean().getFlRequest().getCtContract().setFulfillmentType(new PmCode());
			getManagedBean().getFlRequest().getCtContract().setSmSupplier(new SmSupplier());
			getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			
		}

		if(getManagedBean().getApproverModel()==null) {
			System.out.println("--renew--");
			List<String> source = getAvailableApproverList();  
			List<String> target = new ArrayList<String>();  
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	
	}


	public String submitAction(String action) {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			System.out.println("title: "+flRequest.getTitle());

			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			reqDTO.setMto(getManagedBean().isMto());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getBpmTaskId());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			if(getManagedBean().getTempLogin() == null){
				reqDTO.setTempLogin("roslan");
			}else{
				reqDTO.setTempLogin(getManagedBean().getTempLogin());
			}
			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);

			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+resDTO.getFlFulfilmentRequest().getFulfilmentReqId();
				if(resDTO.getFlFulfilmentRequest().getDocNo()!=null) {
					message = message + ". Doc No. : "+resDTO.getFlFulfilmentRequest().getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { // save action will remain in the same page
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			} else {
				throw new EpException("Failed.");
			}

		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: ",e.getMessage()));
			log.error("Exception>>", e);
		}finally {
			iFulfilmentRequestSO = null;
		}
		return null;
	}

	public List<SelectItem> getPTJList(){
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		OrgProfileResDTO resDTO = new OrgProfileResDTO();
		IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

		if(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId() != null){
			orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
			resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		}

		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			retrievePTJList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrievePTJList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		

		return retrievePTJList;
	}

	public List<SelectItem> getDivisionProfileList(){
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		//TODO
		orgProfileReqDTO.setParentOrgProfileId(1L);
		orgProfileReqDTO.setOrgType(OrganizationType.DEPARTMENT.value());

		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		if(resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()){
			retrieveDivisionList.add(new SelectItem("", "- Select One -"));
			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for(PmOrgProfile obj : listDB){
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}		

		return retrieveDivisionList;
	}

	public void removeAddRow(){
		getManagedBean().getFlRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedAddrRow());
	}
	
	public void removeItemRow(){
		getManagedBean().getFlRequest().getFlFulfilmentItems().remove(getManagedBean().getSelectedItemRow());
	}

	public void updateDeliveryAddrDt(){
		for(PmAddressType addrType : getManagedBean().getSelectedPmAddrTypeList()){
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			deliveryAddr.setAddressType(addrType);
			getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
	}

	public void flDeliveryAddrList(){
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		//TODO
		ctReqDTO.setOrgProfileId(4L);
		ctReqDTO.setCtContract(getManagedBean().getCtContract());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);	
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setAddressTypesList(resDTO.getAddressTypeList());
	}

	public String procureContract(){
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
		ContractReqDTO ctDTO = new ContractReqDTO();
		ctDTO.setContractId(contractId);
		System.out.println("contractId: "+contractId);
		getManagedBean().getFlRequest().setCtContract(contractSO.retrieveContract(ctDTO).getContract());
		System.out.println("getManagedBean().getFlRequest().setCtContract: "+getManagedBean().getFlRequest().getCtContract());
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		if(resDTO.getPmOrgProfile() != null){
			getManagedBean().getFlRequest().setCreatedOrgProfile(resDTO.getPmOrgProfile());
		}
		this.getManagedBean().setActions(new String[]{"SAVE", "SUBMIT"}); 
		FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
		flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

		return NAV_TO_CREATE_CONTRACT_REQUEST;
	}

	public void selectedAddrRow(){
		//TODO
//		getManagedBean().setSelectedRowFlg(true);
	}

	public List<SelectItem> getMaksudCodeList(){
		List<SelectItem> maksudList = new ArrayList<SelectItem>();
		MaksudReqDTO maksudDTO = new MaksudReqDTO();
		maksudDTO.setOrgProfileId(1L);

		IMaksudSO maksudSO = (IMaksudSO)BusinessDelegate.getInstance().getServiceObject("pm",IMaksudSO.class);	
		MaksudResDTO resDTO = maksudSO.retrieveMaksudCodeList(maksudDTO);
		if(resDTO.getMaksudCodeList() != null && !resDTO.getMaksudCodeList().isEmpty()){
			maksudList.add(new SelectItem("", "- Select One -"));
			List<PmMaksud> maksudListDB = resDTO.getMaksudCodeList();
			for(PmMaksud obj : maksudListDB){
				maksudList.add(new SelectItem(obj.getMaksudId(),obj.getMaksudCode()));
			}
		}		
		return maksudList;

	}

	public void checkPtjFlg(){
		getManagedBean().setCheckPtjFlg(false);
	}

	public void addContractedItems(){
		IContractSO contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);	
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		ContractResDTO ctResDTO = contractSO.retrieveContract(ctReqDTO);
		getManagedBean().setContractedItemList(ctResDTO.getContract().getLatestContractVer().getCtContractItems());
	}

	public void updateContractedItemDt(){
		for(CtContractItem contractedItems : getManagedBean().getSelectedContractedItemList()){
			FlFulfilmentItem flItem = new FlFulfilmentItem();
			flItem.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			flItem.setContractedItem(contractedItems);
			getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			getManagedBean().getFlRequest().getFlFulfilmentItems().add(flItem);
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

	public IPagingDataModel<VCtActiveContract> getActiveContractPagingDataModel() {
		return activeContractPagingDataModel;
	}

	public void setActiveContractPagingDataModel(
			IPagingDataModel<VCtActiveContract> activeContractPagingDataModel) {
		this.activeContractPagingDataModel = activeContractPagingDataModel;
	}

	private class ActiveContractPagingDataModel extends PagingDataModel<VCtActiveContract> {
		private static final long serialVersionUID = 1L;

		public ActiveContractPagingDataModel() {
			super("contractListTable");
		}
		
		@Override
		public ISearchResultBean<VCtActiveContract> pageChangeListener(ISearchBean<VCtActiveContract> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				//TODO Change the orgProfileId value
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contract.ctAgencies.orgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(),1L));
				
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setActiveContractSearchBean(searchBean);
				
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
				ContractResDTO resDTO = contractSO.retrieveActiveContractList(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getActiveContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}

}
