/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
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
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
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
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */

@ManagedBean(name = "contractRequestBackingBean")
@RequestScoped
public class ContractRequestBackingBean extends BaseBackingBean<ContractRequestManagedBean> {
	private static Logger log = Logger.getLogger(ContractRequestBackingBean.class);
	public static final String NAV_TO_CREATE_CONTRACT_REQUEST = "/xhtml/fl/contractrequest/crBasePage.xhtml";
	private boolean checkFlg = false;

	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public ContractRequestBackingBean() {

		if (getManagedBean().getFlRequest() == null) {
			System.out.println("==contructor==");
			getManagedBean().setFlRequest(new FlFulfilmentRequest());
			getManagedBean().getFlRequest().setIssuedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCreatedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setPreparedOrgProfile(new PmOrgProfile());
			getManagedBean().getFlRequest().setCtContract(new CtContract());
			getManagedBean().getFlRequest().getCtContract().setContractType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setFulfillmentType(new PmParameter());
			getManagedBean().getFlRequest().getCtContract().setSmSupplier(new SmSupplier());
			getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());
			getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
			
		}
		
		
		if(getManagedBean().getActiveContractList()==null) {
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			List<Long> orgProfileIdList = new ArrayList<Long>();
			//TODO please pass the correrct ptj list 
			orgProfileIdList.add(1147L);
			ctReqDTO.setOrgProfileIdList(orgProfileIdList);			
			ContractResDTO ctResDTO = contractSO.retrieveActiveContractList(ctReqDTO);
			getManagedBean().setActiveContractList(ctResDTO.getActiveContractList());
		}

//		if (getManagedBean().getApproverModel() == null && isRequisitioner()) {
//			System.out.println("-getApproverModel-");
//			List<String> source = getAvailableApproverList();
//			List<String> target = new ArrayList<String>();
//			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
//		}

		if (isRequisitioner()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setRequisitioner(facesScopes.getUserSessionBean().getLoginId());
		}
		
		if (isApprover()) {
			FacesScopes facesScopes = new FacesScopes();
			getManagedBean().setApprover(facesScopes.getUserSessionBean().getLoginId());
		}
		
		if(getManagedBean().getUserGroupMap()==null && isRequisitioner()) {
			initializeApproverGroup();
		}

	}

	public String submitAction(String action) {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			PmUser user = getFacesScopes().getUserSessionBean();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			//TODO waiting for sm api to check the current date against the suppleir exp date.
//			flRequest.getCtContract().getSmSupplier().getSmMofAccounts();
			checkCeilingAmount(flRequest);
			checkCeilingQty(flRequest);
			
			
			reqDTO.setFlFulfilmentRequest(flRequest);
			reqDTO.setContractRequest(Boolean.TRUE);
			reqDTO.setMto(getManagedBean().isMto());
			reqDTO.setImportedItems(getManagedBean().isImportedItems());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setFlAttachmentList(flBB.getManagedBean().getAttachmentList());
			reqDTO.setFlRemark(flBB.getManagedBean().getRemarkList().get(0));
			reqDTO.setToken(token);
			reqDTO.setApproverMap(getManagedBean().getApproverMap());
			reqDTO.setPmUserOrg(getManagedBean().getPmUserOrg());
			if(isRequisitioner()){
				System.out.println("getManagedBean().getApproverModel().getTarget(): "+getManagedBean().getApproverModel().getTarget());
				reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
				reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
				reqDTO.setLoginId(user.getLoginId());
			}

			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);

			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Fulfilment Request No. : "+ resDTO.getFlFulfilmentRequest().getFulfilmentReqId();
				if (resDTO.getFlFulfilmentRequest().getDocNo() != null) {
					message = message + ". Doc No. : "+ resDTO.getFlFulfilmentRequest().getDocNo();
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.", message));
				renewSesionObject();
				return "/xhtml/shared/acknowledge.xhtml";
			} else {
				throw new EpException("Failed.");
			}

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO, "Error: ", e.getMessage()));
			log.error("Exception>>", e);
		} finally {
			iFulfilmentRequestSO = null;
		}
		return null;
	}

	public List<SelectItem> getIssuedDivisionProfileList() {
		List<SelectItem> retrieveDivisionList = new ArrayList<SelectItem>();
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		retrieveDivisionList.add(new SelectItem("", "- Select One -"));
		System.out.println("getOrgProfileId: "+ getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
		getManagedBean().getFlRequest().setIssuedOrgProfile(getManagedBean().getPmUserOrg().getPmOrgProfile());

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		// reqDTO.setParentOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId());
		// reqDTO.setParentOrgProfileId(getManagedBean().getIssuedJabatanId());
		reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);

		if (resDTO.getOrgProfileList() != null && !resDTO.getOrgProfileList().isEmpty()) {

			List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
			for (PmOrgProfile obj : listDB) {
				retrieveDivisionList.add(new SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
			}
		}
		return retrieveDivisionList;
	}

	public List<SelectItem> getIssuedPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		retrievePTJList.add(new SelectItem("", "- Select One -"));
		System.out.println(":getTitle: "+getManagedBean().getFlRequest().getTitle());
		System.out.println("getContractId: "+getManagedBean().getFlRequest().getCtContract().getContractId());
		for (CtAgency ctAgent : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()) {

			retrievePTJList.add(new SelectItem(ctAgent.getOrgProfile().getOrgProfileId(), ctAgent.getOrgProfile().getOrgName()));
		}
		return retrievePTJList;
	}

	public List<SelectItem> getReceivedPTJList() {
		List<SelectItem> retrievePTJList = new ArrayList<SelectItem>();
		// OrgProfileResDTO resDTO = new OrgProfileResDTO();
		// IOrgProfileSO orgProfileSO =
		// (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		// OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		retrievePTJList.add(new SelectItem("", "- Select One -"));
		for (CtAgency ctAgent : getManagedBean().getFlRequest().getCtContract()
				.getLatestContractVer().getCtAgencies()) {
			retrievePTJList.add(new SelectItem(ctAgent.getOrgProfile()
					.getOrgProfileId(), ctAgent.getOrgProfile().getOrgName()));
		}
		// if(isCheckFlg()){
		// orgProfileReqDTO.setParentOrgProfileId(getManagedBean().getReceivedJabatanId());
		// orgProfileReqDTO.setOrgType(OrganizationType.DIVISION.value());
		// resDTO =
		// orgProfileSO.retrieveOrgProfileListByOrgType(orgProfileReqDTO);
		// }
		//
		// if(resDTO.getOrgProfileList() != null &&
		// !resDTO.getOrgProfileList().isEmpty()){
		//
		// List<PmOrgProfile> listDB = resDTO.getOrgProfileList();
		// for(PmOrgProfile obj : listDB){
		// retrievePTJList.add(new
		// SelectItem(obj.getOrgProfileId(),obj.getOrgName()));
		// }
		// }

		return retrievePTJList;
	}

	public void removeAddRow() {

		try {
			getManagedBean().getFlRequest().getFlDeliveryAddresses().remove(getManagedBean().getSelectedDelAddr());
			getManagedBean().setSelectedAddrRow(null);
			getManagedBean().setSelectedItemsRows(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	public String removeItemRow() {
		int delItemsListSize = getManagedBean().getSelectedItemsRows().size();
		if (delItemsListSize == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().size()) {
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().clear();
		} else {
			for (FlFulfilmentItemAddr fulfilmentItem : getManagedBean().getSelectedItemsRows()) {
				getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().remove(fulfilmentItem);
			}
		}
		return null;
	}

	public void updateDeliveryAddrDt() {
		for (CtAgencyAddress addrType : getManagedBean().getSelectedCtAddrTypeList()) {
			FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
			deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			deliveryAddr.setAddressType(addrType.getAddressType());
			getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		}
		getManagedBean().setAddressTypesList(new ArrayList<CtAgencyAddress>());
	}

	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		getManagedBean().setAddressTypesList(resDTO.getAgencyAddressList());
	}

	public void procureContract() throws PortalException, SystemException {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("contractId"));
		String scheduleNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("scheduleNo");
		System.out.println("scheduleNo: "+scheduleNo);
		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay)facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
		
		String commonUrl = themeDisplay.getURLCurrent().substring(0, themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup().getFriendlyURL())+themeDisplay.getLayout().getGroup().getFriendlyURL().length());
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		String url = "contract-request";


		try {
			externalContext.redirect(commonUrl+"/" + url+"?contractId="+contractId+"&scheduleNo="+scheduleNo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<SelectItem> getMaksudCodeList() {

		List<SelectItem> maksudList = new ArrayList<SelectItem>();
		MaksudReqDTO maksudDTO = new MaksudReqDTO();
		maksudDTO.setOrgProfileId(1L);

		IMaksudSO maksudSO = (IMaksudSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IMaksudSO.class);
		MaksudResDTO resDTO = maksudSO.retrieveMaksudCodeList(maksudDTO);
		maksudList.add(new SelectItem("", "- Select One -"));
		if (resDTO.getMaksudCodeList() != null
				&& !resDTO.getMaksudCodeList().isEmpty()) {
			List<PmMaksud> maksudListDB = resDTO.getMaksudCodeList();
			for (PmMaksud obj : maksudListDB) {
				maksudList.add(new SelectItem(obj.getMaksudId(), obj
						.getMaksudCode()));
			}
		}
		return maksudList;

	}

	public void checkPtjFlg() {
		getManagedBean().setCheckPtjFlg(false);

	}

	public void addContractedItems() {
//		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
//		ContractReqDTO ctReqDTO = new ContractReqDTO();
//		ctReqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
//		ContractResDTO ctResDTO = contractSO.retrieveContract(ctReqDTO);
//		getManagedBean().setContractedItemList(ctResDTO.getContract().getLatestContractVer().getCtContractItems());
		getManagedBean().setContractedItemList(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems());
	}

	public void updateContractedItemDt() {//TODO db changes qtitem change
		FlFulfilmentItem flItem = null;
		for (CtContractItem contractedItem : getManagedBean().getSelectedContractedItemList()) {
//			if (getManagedBean().getFlItemMap().get(contractedItem.getQtItem().getQtItemId()) == null) {
//				flItem = new FlFulfilmentItem();
//				flItem.setFlFulfilmentRequest(getManagedBean().getFlRequest());
//				flItem.setQtItem(contractedItem.getQtItem());
//				if(getManagedBean().getFlRequest().getFlFulfilmentItems() == null){
//					getManagedBean().getFlRequest().setFlFulfilmentItems(new ArrayList<FlFulfilmentItem>());
//				}
//				getManagedBean().getFlRequest().getFlFulfilmentItems().add(flItem);
//			} else {
//				flItem = getManagedBean().getFlItemMap().get(contractedItem.getQtItem().getQtItemId());
//			}
			IContractItemSO ctItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
//			reqDTO.setItemId(contractedItem.getQtItem().getQtItemId());
			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getAddressType().getPmAddress());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			reqDTO.setOrderedQty(new BigDecimal(1));
			ContractResDTO resDTO = ctItemSO.getItemPrice(reqDTO);

			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setFlFulfilmentItem(flItem);
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			flItemAddr.setUnitPrice(resDTO.getItemPrice());
			
			if(getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr() == null){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			
//			getManagedBean().getFlItemMap().put(contractedItem.getContractItemId(), flItem);
//			getManagedBean().getFlItemMap().put(contractedItem.getQtItem().getQtItemId(), flItem);
			if(flItem.getFlFulfilmentItemAddr() == null){
				flItem.setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}
			flItem.getFlFulfilmentItemAddr().add(flItemAddr);
		}
		getManagedBean().setContractedItemList(new ArrayList<CtContractItem>());
	}

	private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>();
		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IUserSO.class);

			PmUser user = getFacesScopes().getUserSessionBean();

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(user.getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
			reqDTO.setOrgProfileId(userOrg.getPmOrgProfile().getOrgProfileId());

			resDTO = userSO.retrieveUserGroupList(reqDTO);
			for (PmUserGroupUser userGroup : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				getManagedBean().getApproverMap().put(userGroup.getPmUser().getUserName(), userGroup);
				availableApprovers.add(userGroup.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							"Circular save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
	}

	public void preRenderView() {

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String contractId = originalServletRequest.getParameter("contractId");
		String scheduleNo = originalServletRequest.getParameter("scheduleNo");
		
		System.out.println("taskId:" + taskId);
		if (taskId != null) {

			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();

			IFulfilmentRequestSO requestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",
							IFulfilmentRequestSO.class);
			FulfilmentReqDTO flReqDTO = new FulfilmentReqDTO();
			FulfilmentResDTO resDTO = new FulfilmentResDTO();
			FLContractOrderDataType coObj = (FLContractOrderDataType) obj;
			if (coObj.getContractRequestId().intValue() == 0) {
				flReqDTO.setFlRequestId(coObj.getAssociatedContractRequestId());

			} else {
				flReqDTO.setFlRequestId(coObj.getContractRequestId());
			}

			flReqDTO.setSelectedApprovers(coObj.getApproverList());
			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

			if (coObj.getContractRequestId().intValue() == 0) {//TODO
				FlFulfilmentRequest flRequest = new FlFulfilmentRequest();
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setContractId(resDTO.getFlFulfilmentRequest().getCtContract().getContractId());
			
				resDTO.getFlFulfilmentRequest().setCtContract(contractSO.retrieveContract(contractReqDTO).getContract());//TODO
				//TODO db changes
//				contractReqDTO.setLoaId(resDTO.getFlFulfilmentRequest().getCtContract().getLatestContractVer().getCtMtoLoas().get(0).getLoa().getLoaId());
				ContractResDTO ctResDTO = contractSO.retrieveContractByLoaId(contractReqDTO);
				
				flRequest.setCtContract(ctResDTO.getContract());
//				flRequest.setCtContract(contractSO.retrieveNormalContractByMtoContract(contractReqDTO).getContract());
				getManagedBean().setFlRequest(flRequest);
				getManagedBean().getFlRequest().setPmUserGroup(new PmUserGroup());
				getManagedBean().getFlRequest().setIssuedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
				getManagedBean().getFlRequest().setChargeOrgProfile(resDTO.getFlFulfilmentRequest().getChargeOrgProfile());
				getManagedBean().getFlRequest().setPreparedOrgProfile(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile());
				getManagedBean().getFlRequest().setFlDeliveryAddresses(new ArrayList<FlDeliveryAddress>());

			} else {
				getManagedBean().setFlRequest(resDTO.getFlFulfilmentRequest());
			}
			getManagedBean().setTaskId(taskId);
			getManagedBean().setOutComes(res.getAction());
			if(isRequisitioner()){
				System.out.println("resDTO.getSelectedApproverList(): "+resDTO.getSelectedApproverList());
				getManagedBean().getApproverModel().setTarget(resDTO.getSelectedApproverList());
				getManagedBean().getApproverModel().getSource().removeAll(resDTO.getSelectedApproverList());
			}
			
			getManagedBean().setMinistryName(resDTO.getFlFulfilmentRequest().getIssuedOrgProfile().getParentOrgProfile().getParentOrgProfile()
							.getOrgName());
			if(getManagedBean().getFlManualAddress() == null){
				getManagedBean().setFlManualAddress(new FlDeliveryAddress());
				getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
				getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
				//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
				getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
				getManagedBean().getFlManualAddress().getPmAddress().getPmState().setStateId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
				getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
				getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
				getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			}
		}else if(contractId != null){
			getManagedBean().setContractId(Long.parseLong(contractId));
			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			IContractSO contractSO = BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO ctDTO = new ContractReqDTO();
			ctDTO.setContractId(getManagedBean().getContractId());
			getManagedBean().getFlRequest().setCtContract(contractSO.retrieveContract(ctDTO).getContract());
			getManagedBean().getFlRequest().setScheduleNo(scheduleNo);
			getLoginUserOrg();
			// getManagedBean().setMinistryName(getManagedBean().getPmUserOrg().getPmOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgName());

			getManagedBean().setOutComes(new String[] { "Submit" });
			FulfilmentBackingBean flBackingBean = new FulfilmentBackingBean();
			flBackingBean.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());
//			for (CtAgency ctAgent : getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies()) {
//
//				getManagedBean().getAgenciesList().add(ctAgent.getOrgProfile().getOrgProfileId());
//				
//			}
		}
		getManagedBean().setTestOrg(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtAgencies().get(0).getOrgProfile().getOrgProfileId());
		
		if(getManagedBean().getFlRequest().getCtContract().isZonalPricing()){
			getManagedBean().setFlManualAddress(new FlDeliveryAddress());
			getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
			getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManualAddress().getPmAddress().setPmState(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractZones().get(0).getCtContractZoneDtls().get(0).getState());
			getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
		}else if(getManagedBean().getFlManualAddress() == null){
			getManagedBean().setFlManualAddress(new FlDeliveryAddress());
			getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
			getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManualAddress().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManualAddress().getPmAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			
		}
	}

	public List<SelectItem> getGoList() {
		List<SelectItem> goList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IObjectSO.class);
		ObjectResDTO resDTO = objectSO.retrieveGOList();
		goList.add(new SelectItem("", "- Select One -"));
		if (resDTO.getPmGoList() != null && !resDTO.getPmGoList().isEmpty()) {
			List<PmGo> goDB = resDTO.getPmGoList();
			for (PmGo obj : goDB) {
				goList.add(new SelectItem(obj.getGoId(), obj.getGoCode()));
			}
		}
		return goList;

	}

	public List<SelectItem> getSoList() {
		List<SelectItem> soList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IObjectSO.class);
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		soList.add(new SelectItem("", "- Select One -"));
		if (getManagedBean().getGoId() != null) {

			reqDTO.setGoId(getManagedBean().getGoId());
			ObjectResDTO resDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			if (resDTO.getPmSoList() != null && !resDTO.getPmSoList().isEmpty()) {
				List<PmSo> soDB = resDTO.getPmSoList();
				for (PmSo obj : soDB) {
					soList.add(new SelectItem(obj.getSoId(), obj.getSoCode()));
				}
			}
		}
		return soList;
	}

	public List<SelectItem> getDoList() {
		List<SelectItem> doList = new ArrayList<SelectItem>();
		IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IObjectSO.class);
		ObjectReqDTO reqDTO = new ObjectReqDTO();
		doList.add(new SelectItem("", "- Select One -"));
		if (getManagedBean().getSoId() != null) {
			reqDTO.setSoId(getManagedBean().getSoId());
			ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			if (resDTO.getPmDoList() != null && !resDTO.getPmDoList().isEmpty()) {
				List<PmDo> doDB = resDTO.getPmDoList();
				for (PmDo obj : doDB) {
					doList.add(new SelectItem(obj.getDoId(), obj.getDoCode()));
				}
			}
		}
		return doList;
	}

	public void calculateSubTotal(BigDecimal quantity) {
		// getManagedBean().getSelectedItemAddr().setSubTotal(quantity);
		getManagedBean().setSubTotal(quantity);
	}

	/**
	 * @return the checkFlg
	 */
	public boolean isCheckFlg() {
		return checkFlg;
	}

	/**
	 * @param checkFlg
	 *            the checkFlg to set
	 */
	public void setCheckFlg(boolean checkFlg) {
		this.checkFlg = checkFlg;
	}

	public void getLoginUserOrg() {
		PmUser user = getFacesScopes().getUserSessionBean();

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(user.getUserId());
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

		PmUserOrg userOrg = resDTO.getUserOrgList().get(0);
		getManagedBean().setPmUserOrg(userOrg);

	}

	public void updateIssuedJabatan() {
		System.out.println("-updateIssuedJabatan- "+ getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if (getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId() != null) {

			reqDTO.setOrgProfileId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
			getManagedBean().getFlRequest().setIssuedOrgProfile(resDTO.getPmOrgProfile());
			getManagedBean().getFlRequest().setChargeOrgProfile(resDTO.getPmOrgProfile());
		} else {
			getManagedBean().getFlRequest().setIssuedOrgProfile(null);
			getManagedBean().getFlRequest().setChargeOrgProfile(null);
		}
	}

	public boolean isRequisitioner() {
		return getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value());

	}
	
	public boolean isApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.APPROVER.value());

	}
	
	public void checkCeilingAmount(FlFulfilmentRequest flRequest){
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());
		
		ContractResDTO ctResDTO = new ContractResDTO();
		ctResDTO = contractItemSO.getAgencyCeilingAmount(ctReqDTO);
		ctResDTO.getAgencyCeilingAmount();//TODO flRequest.deladdr loop then sum the flItemaddr orederAmt AND create SO get the ptj previous oredered amt , check contractId and issuePtj same then sum it and check with ceiling amt
		BigDecimal sumOrderedAmt = new BigDecimal("0");
		BigDecimal checkCeilingAmt = new BigDecimal("0");
		for(FlDeliveryAddress delAddr: flRequest.getFlDeliveryAddresses()){
			for(FlFulfilmentItemAddr itemAddr : delAddr.getFlFulfilmentItemAddr()){
				
				sumOrderedAmt = sumOrderedAmt.add(itemAddr.getOrderedQty());//TODO change back to getOrderedAmount
				System.out.println("sumOrderedAmt: "+sumOrderedAmt);
			}
		}
		IFulfilmentRequestSO iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		reqDTO.setIssuedOrgId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
		reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
		FulfilmentResDTO resDTO = iFulfilmentRequestSO.retrievePreviousSumOrderedAmtByPtj(reqDTO);
		checkCeilingAmt = resDTO.getPrevSumAmt().add(sumOrderedAmt);
		
		System.out.println("checkCeilingAmt total: "+checkCeilingAmt);
		System.out.println("getAgencyCeilingAmount: "+ctResDTO.getAgencyCeilingAmount().intValue());
		if(checkCeilingAmt.intValue() >  ctResDTO.getAgencyCeilingAmount().intValue()){
			throw new EpException("This PTJ has exceed the limit of the celing amount.");
		}
	}
	
	public void checkCeilingQty(FlFulfilmentRequest flRequest){//TODO db change qt item change
		IContractItemSO contractItemSO = BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());

		ContractResDTO ctResDTO = new ContractResDTO();
		
		for(FlFulfilmentItem item : getManagedBean().getFlRequest().getFlFulfilmentItems()){
			BigDecimal sumOrderedQty = new BigDecimal("0");
			BigDecimal checkCeilingQty = new BigDecimal("0");
//			ctReqDTO.setItemId(item.getQtItem().getQtItemId());
			ctResDTO = contractItemSO.getItemCeilingQuantity(ctReqDTO);
			ctResDTO.getItemCeilingQty();//TODO loop flrequest.flitems loop again itemAddr and sum ordered qty AND create SO get the ptj previous ordered qty,check contracID and issuePTJ same then sum it and check with ceiling Qty

			for(FlFulfilmentItemAddr itemAddr : item.getFlFulfilmentItemAddr()){
				sumOrderedQty = sumOrderedQty.add(itemAddr.getOrderedQty());
				System.out.println("sumOrderedQty: "+sumOrderedQty);
			}
			IFulfilmentRequestSO iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			reqDTO.setIssuedOrgId(getManagedBean().getFlRequest().getIssuedOrgProfile().getOrgProfileId());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.retrievePreviousSumOrderedQtyByPtj(reqDTO);
			checkCeilingQty = resDTO.getPrevSumQty().add(sumOrderedQty);

			System.out.println("checkCeilingQty total: "+checkCeilingQty);
			System.out.println("getAgencyCeilingAmount: "+ctResDTO.getItemCeilingQty());
			if(checkCeilingQty.intValue() >  ctResDTO.getItemCeilingQty().intValue()){
//				throw new EpException("Item "+ item.getQtItem().getItemName() +"  has been exceeded the quantity limit.");
			}
		}
		
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
			
			if(getManagedBean().getFlRequest().getPmUserGroup()==null) {
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getFlRequest().setPmUserGroup(pmUserGroup);
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
					getManagedBean().getFlRequest().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
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
	
	public void userGroupChangeListener() {
		PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getFlRequest().getPmUserGroup().getUserGroupId());
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
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
	
	public void selectAddrEvent() {
		getManagedBean().setSelectedItemsRows(null);
	}
	
	public void retrieveZonalPrice(FlFulfilmentItemAddr flItemAddr) {
		System.out.println("event "+flItemAddr.getFulfilmentItemAddrId());
		int unitPrice = 0;
		int orderedQty = 0;
		int orderedAmt = 0;
		if(null != flItemAddr.getUnitPrice()){
			unitPrice= flItemAddr.getUnitPrice().intValue();
			{
				if(null != flItemAddr.getOrderedQty()){
					orderedQty = flItemAddr.getOrderedQty().intValue();
				}
				orderedAmt =((unitPrice) * (orderedQty));
				flItemAddr.setOrderedAmt(new BigDecimal(orderedAmt) );
			}
		}
	}
	
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		getManagedBean().setFlManualAddress(flDelAddress);
		flDelAddress.setEditable(true);
		
	}
	
	public void updateManualDeliveryAddrDt() {
		if(null== getManagedBean().getFlManualAddress().getPmAddress() ||  getManagedBean().getFlManualAddress().isEditable()){
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddrType = new PmAddress();
		manualAddrType = getManagedBean().getFlManualAddress().getPmAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
		deliveryAddr.setPmAddress(manualAddrType);
		deliveryAddr.setCheckManualAddress(true);
		getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
	}
	
	public void flManualDeliveryAddr() {
		getManagedBean().setFlManualAddress(new FlDeliveryAddress());
		getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManualAddress().setAddressType(new PmAddressType());
		//getManagedBean().getFlManualAddress().getAddressType().setAddressTypeId(1L);
		getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
		//getManagedBean().getFlManualAddress().getAddressType().getPmAddress().setAddressId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManualAddress().getPmAddress().getPmState().setStateId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManualAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManualAddress().getPmAddress().getPmCity().setCityId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManualAddress().getPmAddress().getPmDivision().setDivisionId(1L);
		
		
		
	}
	
	public List<SelectItem> getState() {//TODO retrieve state contract zone
		List<SelectItem> stateList = new ArrayList<SelectItem>();
//		IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
//		ObjectReqDTO reqDTO = new ObjectReqDTO();
		stateList.add(new SelectItem("", "- Select One -"));
//		if (getManagedBean().getSoId() != null) {
//			reqDTO.setSoId(getManagedBean().getSoId());
//			ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
//			if (resDTO.getPmDoList() != null && !resDTO.getPmDoList().isEmpty()) {
//				List<PmDo> doDB = resDTO.getPmDoList();
//				for (PmDo obj : doDB) {
		getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractZones().get(0).getCtContractZoneDtls().get(0).getState().getStateName();
					stateList.add(new SelectItem(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractZones().get(0).getCtContractZoneDtls().get(0).getState().getStateId(), getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractZones().get(0).getCtContractZoneDtls().get(0).getState().getStateName()));
//				}
//			}
//		}
		return stateList;
	}
}
