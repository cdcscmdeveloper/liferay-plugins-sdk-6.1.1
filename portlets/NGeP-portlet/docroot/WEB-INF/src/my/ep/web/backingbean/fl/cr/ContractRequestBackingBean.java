/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.IPmSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
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
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.portal.util.PortalUtil;

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
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public ContractRequestBackingBean(){

		if(getManagedBean().getFlRequest() == null){
			System.out.println("==contructor==");
			getManagedBean().setFlRequest(new FlFulfilmentRequest());
			getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setReceivedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCtContract(new CtContract());
			getManagedBean().getFlRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setFulfillmentType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setSmSupplier(new SmSupplier());
			getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());

		}

		if(getManagedBean().getApproverModel()==null) {
			List<String> source = getAvailableApproverList();  
			List<String> target = new ArrayList<String>();  
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}


	public String submitAction(String action) {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			reqDTO.setMto(getManagedBean().isMto());
			reqDTO.setImportedItems(getManagedBean().isImportedItems());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setToken(token);
			reqDTO.setApproverMap(getManagedBean().getApproverMap());

			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl", IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);

			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+resDTO.getFlFulfilmentRequest().getFulfilmentReqId();
				if(resDTO.getFlFulfilmentRequest().getDocNo()!=null) {
					message = message + ". Doc No. : "+resDTO.getFlFulfilmentRequest().getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
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

	/*public void removeItemRow(){
		System.out.println("we are in Remove function");
		System.out.println("selected Item Row   "+getManagedBean().getSelectedItemAddr());
		System.out.println("Original Item Row    "+getManagedBean().getFlRequest().getFlFulfilmentItems().size());
		getManagedBean().getFlRequest().getFlFulfilmentItems().remove(getManagedBean().getSelectedItemAddr());
	}*/
	public String removeItemRow() {
		int delItemsListSize=getManagedBean().getSelectedItemsRows().size();
		if(delItemsListSize==getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().size()){
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().clear();
		}else{
			for(FlFulfilmentItemAddr fulfilmentItem: getManagedBean().getSelectedItemsRows()){
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().remove(fulfilmentItem);
				
			}
		}
		return null;
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
		getManagedBean().getFlRequest().setCtContract(contractSO.retrieveContract(ctDTO).getContract());
//		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
//		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
//		orgProfileReqDTO.setOrgProfileId(4L);// set current login user org profile ID
//		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);
		
		PmUser user = getFacesScopes().getUserSessionBean();

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO ) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO .class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		System.out.println("getOrgName: "+userOrg.getPmOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgName());
		getManagedBean().getFlRequest().setIssuedOrgProfile(userOrg.getPmOrgProfile());

		this.getManagedBean().setOutComes(new String[]{"Submit"}); 
		FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
		flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
//		getManagedBean().getFlRequest().setStatus();//TODO
		return NAV_TO_CREATE_CONTRACT_REQUEST;
	}

	public List<SelectItem> getMaksudCodeList(){

		List<SelectItem> maksudList = new ArrayList<SelectItem>();
		MaksudReqDTO maksudDTO = new MaksudReqDTO();
		maksudDTO.setOrgProfileId(1L);

		IMaksudSO maksudSO = (IMaksudSO)BusinessDelegate.getInstance().getServiceObject("pm",IMaksudSO.class);	
		MaksudResDTO resDTO = maksudSO.retrieveMaksudCodeList(maksudDTO);
		maksudList.add(new SelectItem("", "- Select One -"));
		if(resDTO.getMaksudCodeList() != null && !resDTO.getMaksudCodeList().isEmpty()){
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
		FlFulfilmentItem flItem = null;
		for(CtContractItem contractedItems : getManagedBean().getSelectedContractedItemList()){
			if(getManagedBean().getFlItemMap().get(contractedItems.getContractItemId()) == null){
				flItem = new FlFulfilmentItem();
				flItem.setFlFulfilmentRequest(getManagedBean().getFlRequest());
				flItem.setQtItem(contractedItems.getQtItem());
				getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
				getManagedBean().getFlRequest().getFlFulfilmentItems().add(flItem);
			}else{
				flItem = getManagedBean().getFlItemMap().get(contractedItems.getContractItemId());

			}
			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setFlFulfilmentItem(flItem);
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);

			getManagedBean().getFlItemMap().put(contractedItems.getContractItemId(), flItem);
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
				getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
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
				String language = new FacesScopes().getUserLocale().getLanguage();
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contract.latestContractVer.ctAgencies.orgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(),1147L));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contract.fulfillmentType.pmParameterDescs.languageCode",SearchOperator.OPERATOR_EQUALS.value(),language));
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contract.contractType.pmParameterDescs.languageCode",SearchOperator.OPERATOR_EQUALS.value(),language));

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

	public void preRenderView() {

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:" + taskId);
		if(taskId != null){

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
//			if(!res.getTaskName().equalsIgnoreCase(FlProcessNameConst.CREATE_NEW_CO_HT)){

				IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
				FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
				FulfilmentResDTO resDTO = new FulfilmentResDTO();
				FLContractOrderDataType coObj = (FLContractOrderDataType)obj;
				if(coObj.getContractRequestId().intValue() == 0){
//					flReqDTO.setFlRequestId(coObj.getParentContractRequestId());
					flReqDTO.setFlRequestId(coObj.getAssociatedContractRequestId());
					
				}else{
					flReqDTO.setFlRequestId(coObj.getContractRequestId());
				}


				flReqDTO.setSelectedApprovers(coObj.getApproverList());
				resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

				FulfilmentBackingBean flBB = new FulfilmentBackingBean();
				flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
				flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
				flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

				IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
				//TODO
				orgProfileReqDTO.setOrgProfileId(4L);
				OrgProfileResDTO pmResDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgProfileReqDTO);

				if(coObj.getContractRequestId().intValue() == 0){
					FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
					IContractSO contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
					ContractReqDTO contractReqDTO = new ContractReqDTO();
					contractReqDTO.setContractId(resDTO.getFlFulfilmentRequest().getCtContract().getContractId());

					flRequest.setCtContract(contractSO.retrieveNormalContractByMtoContract(contractReqDTO).getContract());
					getManagedBean().setFlRequest(flRequest);
					getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
					getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
					getManagedBean().getFlRequest().setReceivedOrgProfile(new PmOrgProfile());
					getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());

				} else{
					getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
				}
				getManagedBean().setTaskId(taskId);
				getManagedBean().setOutComes(res.getAction());
				getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
				getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			}

//		}
	}
	
	public List<SelectItem> getGoList(){
		List<SelectItem> goList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectResDTO resDTO = objectSO.retrieveGOList();
		goList.add(new SelectItem("", "- Select One -"));
		if(resDTO.getPmGoList() != null && !resDTO.getPmGoList().isEmpty()){
			List<PmGo> goDB = resDTO.getPmGoList();
			for(PmGo obj : goDB){
				goList.add(new SelectItem(obj.getGoId(),obj.getGoCode()));
			}
		}
		return goList;
		
	}
	
	public List<SelectItem> getSoList(){
		List<SelectItem> soList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		soList.add(new SelectItem("", "- Select One -"));
		if(getManagedBean().getGoId() != null){

			reqDTO.setGoId(getManagedBean().getGoId());
			ObjectResDTO resDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			if(resDTO.getPmSoList() != null && !resDTO.getPmSoList().isEmpty()){
				List<PmSo> soDB = resDTO.getPmSoList();
				for(PmSo obj : soDB){
					soList.add(new SelectItem(obj.getSoId(),obj.getSoCode()));
				}
			}
		}
		return soList;
	}
	
	public List<SelectItem> getDoList(){
		List<SelectItem> doList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO)BusinessDelegate.getInstance().getServiceObject("pm",IObjectSO.class);	
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		doList.add(new SelectItem("", "- Select One -"));
		if(getManagedBean().getSoId() != null){
			reqDTO.setSoId(getManagedBean().getSoId());
			ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			if(resDTO.getPmDoList() != null && !resDTO.getPmDoList().isEmpty()){
				List<PmDo> doDB = resDTO.getPmDoList();
				for(PmDo obj : doDB){
					doList.add(new SelectItem(obj.getDoId(),obj.getDoCode()));
				}
			}	
		}
		return doList;
	}
	
	public void calculateSubTotal(BigDecimal quantity){
//		getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
		getManagedBean().setSubTotal(quantity);
	}

}
