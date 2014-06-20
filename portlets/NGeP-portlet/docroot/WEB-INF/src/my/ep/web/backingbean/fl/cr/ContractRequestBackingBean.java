/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.fl.cr;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.fl.request.IFulfilmentRequestSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.fl.codata.FLContractOrderDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
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
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.FulfilmentBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.fl.cr.ContractRequestManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

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
	private String className="@(.myCrClassName)";
	
	private String queryItemCode;
	private String queryItemName;
	private String queryDesc;
	
	private static Map<Long, String> votFundCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> progmActivityCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> projectCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> setiaCodeMap = new HashMap<Long, String>();
	private static Map<Long, String> subSetiaCodeMap = new HashMap<Long, String>();
	
	// bulk charge popup
	private Long pmPrjSetia;
	private Long pmPrgActivity;
		
		// display charge for selected item in main form
		
	private Long disPmPrjSetia;
	private Long disPmPrgActivity;

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
			orgProfileIdList.add(4L);
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
		if(null == getManagedBean().getFlManulaAddress()){
			getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
			getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
			//getManagedBean().getFlManulaAddress().setAddressType(new PmAddressType());
			//getManagedBean().getFlManulaAddress().getAddressType().setAddressTypeId(1L);
			getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
			//getManagedBean().getFlManulaAddress().getAddressType().getPmAddress().setAddressId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(0L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmState().setStateId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmDistrict().setDistrictId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCity().setCityId(1L);
			getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmDivision().setDivisionId(1L);
			getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
			
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress()){
			 getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		}
		if(null == getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry()){
			getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(new PmCountry());
			getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().setCountryId(0L);
		}
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
		
		//addContractedItems();
		getVotFundList(4L);
		getManagedBean().setTermDays("Days");
		//get this lists for manual Address dropdowns
		retrieveCountryList();
	}

	public String submitAction(String action) {
		IFulfilmentRequestSO iFulfilmentRequestSO = null;
		FulfilmentBackingBean flBB = new FulfilmentBackingBean();
		try {
			PmUser user = getFacesScopes().getUserSessionBean();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
			FlFulfilmentRequest flRequest = getManagedBean().getFlRequest();
			for(FlDeliveryAddress address : flRequest.getFlDeliveryAddresses()){
				for(FlFulfilmentItemAddr flItemAddress: address.getFlFulfilmentItemAddr()){
					flItemAddress.getFlFulfilmentItem().getPmVotFund().getVotFundId();
				}
			}
			if(isRequisitioner()) {
				reqDTO.setRequisitioner(Boolean.TRUE);
			}
			flRequest.setFinancialYear(2014);
			PmOrgProfile pmorgProfile = new PmOrgProfile();
			pmorgProfile.setOrgProfileId(4L);
			flRequest.setChargeOrgProfile(pmorgProfile);
			flRequest.setIssuedOrgProfile(pmorgProfile);
			flRequest.setPreparedOrgProfile(pmorgProfile);
			flRequest.setCreatedOrgProfile(pmorgProfile);
			PmAgOffice agOffice = new  PmAgOffice();
			agOffice.setAgOfficeId(1L);
			flRequest.setAgOffice(agOffice);
			//TODO waiting for sm api to check the current date against the suppleir exp date.
//			flRequest.getCtContract().getSmSupplier().getSmMofAccounts();
			//checkCeilingAmount(flRequest);
			//checkCeilingQty(flRequest);
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
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			if(isRequisitioner()){
				System.out.println("getManagedBean().getApproverModel().getTarget(): "+getManagedBean().getApproverModel().getTarget());
				reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
				reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
				///reqDTO.setLoginId(user.getLoginId());
			}

			iFulfilmentRequestSO = (IFulfilmentRequestSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentRequestSO.class);
			FulfilmentResDTO resDTO = iFulfilmentRequestSO.saveFulfillmentRequest(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(messageId, 
								new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CONTRACT_REQUEST), flRequest.getDocNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
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
		if (null == getManagedBean().getSelectedCtAddrTypeList()
				|| getManagedBean().getSelectedCtAddrTypeList().isEmpty()) {
			return;
		}
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		for (CtAgencyAddress addrType : getManagedBean().getSelectedCtAddrTypeList()) {
			//Validate to eliminate duplicate address. 
			boolean isAddressContains = isDeliveryAddressContains(addrType,
					getManagedBean().getFlRequest()
							.getFlDeliveryAddresses());
			if (!isAddressContains) {
				FlDeliveryAddress deliveryAddr = new FlDeliveryAddress();
				deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
				deliveryAddr.setAddressType(addrType.getAddressType());
				//deliveryAddr.setPmAddress(addrType.getAddressType().getPmAddress());
				paramReqDTO.setParameterId(574L);
				deliveryAddr.setPmUom(parameterSO.retrieveParameterById(paramReqDTO).getPmParameter());
				deliveryAddr.setDeliveryTerm(10);
				deliveryAddr.setReceivingGrp(addrType.getAddressType());
				getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
			}	
		}
		getManagedBean().setAddressTypesList(new ArrayList<CtAgencyAddress>());
	}
	public void updateManualDeliveryAddrDt() {
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		
		if(null== getManagedBean().getFlManulaAddress().getPmAddress() ||  getManagedBean().getFlManulaAddress().isEditable()){
			return;
		}
		FlDeliveryAddress deliveryAddr = null;
		PmAddress manualAddr=null;
		manualAddr=getManagedBean().getFlManulaAddress().getPmAddress();
		deliveryAddr = new FlDeliveryAddress();
		deliveryAddr.setFlFulfilmentRequest(getManagedBean().getFlRequest());
		deliveryAddr.setPmAddress(manualAddr);
		//deliveryAddr.setAddressType(manualAddr);
		deliveryAddr.setCheckManualAddress(true);
		deliveryAddr.setStartDate(new Timestamp(System.currentTimeMillis()));
		deliveryAddr.setEndDate(new Timestamp(System.currentTimeMillis()));
		paramReqDTO.setParameterId(574L);
		deliveryAddr.setPmUom(parameterSO.retrieveParameterById(paramReqDTO).getPmParameter());
		deliveryAddr.setDeliveryTerm(10);
		getManagedBean().setSelectedDelAddr(deliveryAddr);
		getManagedBean().getFlRequest().getFlDeliveryAddresses().add(deliveryAddr);
		
		// add to cart validation
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}
	public void closeListener() {
		log.info("closeListener");
		RequestContext.getCurrentInstance().execute("addManualDeliveryAddr.hide()");
		getManagedBean().setRequired(Boolean.FALSE);
	}
	
	/**
	 * @param addrType selected delivery address.
	 * @param flDeliveryAddresses list of delivery addresses.
	 * @return returns true if selected delivery address is already added,otherwise false.
	 */
	private boolean isDeliveryAddressContains(CtAgencyAddress addrType,
			List<FlDeliveryAddress> flDeliveryAddresses) {
		boolean isContains=false;
		if(null==flDeliveryAddresses|| flDeliveryAddresses.isEmpty()){
			return false;
		}
		for(FlDeliveryAddress flAddress:flDeliveryAddresses){
			if(addrType.getAddressType().getAddressTypeId()==flAddress.getAddressType().getAddressTypeId()){
				isContains=true;
			}
		}
		return isContains;
	}

	public void flDeliveryAddrList() {
		ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
		ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
		IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);
		ContractAmendmentResDTO resDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
		System.out.println("address List::: "+resDTO.getAgencyAddressList().size());
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
	

	public void checkPtjFlg() {
		getManagedBean().setCheckPtjFlg(false);

	}

	public void addContractedItems() {
		getManagedBean().setContractedItemList(getManagedBean().getFlRequest().getCtContract().getLatestContractVer().getCtContractItems());
	}
	
	public void updateContractedItemDt() {//TODO db changes qtitem change
		FlFulfilmentItem flItem = null;
		for (CtContractItem contractedItem : getManagedBean().getSelectedContractedItemList()) {
			
			flItem = new FlFulfilmentItem();
			CmItem cmItem = new CmItem();
			cmItem.setItemId(contractedItem.getScRequestItem().getCmItem().getItemId());
			//cmItem.setExtensionCode(contractedItem.getScRequestItem().getCmItem().getExtensionCode());
			//PmVotFund pmVotFund=new PmVotFund();
		    //pmVotFund.setVotFundId(1L);
			cmItem.setExtensionCode(contractedItem.getScRequestItem().getCmItem().getExtensionCode());
			PmParameter pmParameter = new PmParameter();
		    pmParameter.setParameterId(155L);
			flItem.setItemType(pmParameter);
			flItem.setCmItem(cmItem);
			flItem.setFlFulfilmentRequest(getManagedBean().getFlRequest());
			getManagedBean().getFlRequest().getFlFulfilmentItems().add(flItem);
			IContractItemSO ctItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractItemSO.class);
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
//			reqDTO.setItemId(contractedItem.getQtItem().getQtItemId());
			reqDTO.setPmAddress(getManagedBean().getSelectedDelAddr().getPmAddress());
			reqDTO.setContractId(getManagedBean().getFlRequest().getCtContract().getContractId());
			reqDTO.setOrderedQty(new BigDecimal(1));
			///ContractItemResDTO resDTO = ctItemSO.getItemPrice(reqDTO);

			FlFulfilmentItemAddr flItemAddr = new FlFulfilmentItemAddr();
			flItemAddr.setFlFulfilmentItem(flItem);
			flItemAddr.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			///flItemAddr.setUnitPrice(resDTO.getItemPrice());
			flItemAddr.setUnitPrice(new BigDecimal(20));
			flItemAddr.setCmItem(contractedItem.getScRequestItem().getCmItem());
			flItemAddr.setScRequestItem(contractedItem.getScRequestItem());
			
			
			if(getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr() == null){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}
			//TODO
			//Code for already selected items.
			
			getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItemAddr);
			
//			getManagedBean().getFlItemMap().put(contractedItem.getContractItemId(), flItem);
//			getManagedBean().getFlItemMap().put(contractedItem.getQtItem().getQtItemId(), flItem);
			//if(flItem.getFlFulfilmentItemAddr() == null){
			//	flItem.setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			//}
			//flItem.getFlFulfilmentItemAddr().add(flItemAddr);
		}
		//getManagedBean().setContractedItemList(new ArrayList<CtContractItem>());
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
			if (coObj.getContractId().intValue() == 0) {
				flReqDTO.setFlRequestId(coObj.getAssociatedCrId());

			} else {
				flReqDTO.setFlRequestId(coObj.getContractId());
			}

			///flReqDTO.setSelectedApprovers(coObj.getApproverList());
			resDTO = requestSO.retrieveFulfilmentRequest(flReqDTO);

			FulfilmentBackingBean flBB = new FulfilmentBackingBean();
			flBB.getManagedBean().setRemarkList(resDTO.getRemarkList());
			flBB.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			flBB.initializeRemarkAndAttachement(flBB.getManagedBean().getRemarkList(), flBB.getManagedBean().getAttachmentList());

			if (coObj.getContractId().intValue() == 0) {//TODO
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
			combineSupAddress(resDTO.getFlFulfilmentRequest());
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
				getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(0L);
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
			ContractResDTO contractResDTO = contractSO.retrieveContract(ctDTO);
			getManagedBean().getFlRequest().setCtContract(contractResDTO.getContract());
			getManagedBean().getFlRequest().setScheduleNo(scheduleNo);
			getManagedBean().getFlRequest().setSmSupplier(contractResDTO.getContract().getSmSupplier());
			//TODO
			/*if(contractResDTO.getContract().getSmSupplierBranch() != null){
				getManagedBean().getFlRequest().setSupplierBranch(contractResDTO.getContract().getSmSupplierBranch());
			}*/
			getLoginUserOrg();
			// getManagedBean().setMinistryName(getManagedBean().getPmUserOrg().getPmOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgName());

			getManagedBean().setOutComes(new String[] { "SUBMIT" });
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
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(0L);
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
			getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(0L);
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
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());
		
		ContractItemResDTO ctResDTO = new ContractItemResDTO();
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
		ContractItemReqDTO ctReqDTO = new ContractItemReqDTO();
		ctReqDTO.setContractId(flRequest.getCtContract().getContractId());
		ctReqDTO.setOrgProfileId(flRequest.getIssuedOrgProfile().getOrgProfileId());

		ContractItemResDTO ctResDTO = new ContractItemResDTO();
		
		for(FlFulfilmentItem item : getManagedBean().getFlRequest().getFlFulfilmentItems()){
			BigDecimal sumOrderedQty = new BigDecimal("0");
			BigDecimal checkCeilingQty = new BigDecimal("0");
//			ctReqDTO.setItemId(item.getQtItem().getQtItemId());
			ctResDTO = contractItemSO.getItemCeilingQuantity(ctReqDTO);
			ctResDTO.getItemCeilingQuantity();//TODO loop flrequest.flitems loop again itemAddr and sum ordered qty AND create SO get the ptj previous ordered qty,check contracID and issuePTJ same then sum it and check with ceiling Qty

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
			System.out.println("getAgencyCeilingAmount: "+ctResDTO.getItemCeilingQuantity());
			if(checkCeilingQty.intValue() >  ctResDTO.getItemCeilingQuantity().intValue()){
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
	
	public void retrieveOrderAmount(FlFulfilmentItemAddr flItemAddress){
		getManagedBean().getSelectedDelAddr();
		int unitPrice = 0;
		int ordQnty = 0;
		int ordAmoun = 0;
		BigDecimal ordAmount = new BigDecimal("0");
		if(null != flItemAddress.getUnitPrice())
			unitPrice= flItemAddress.getUnitPrice().intValue();
		if(null == flItemAddress.getOrderedQty()){
			flItemAddress.setOrderedAmt(ordAmount);
		}else{
			ordQnty = flItemAddress.getOrderedQty().intValue();
			ordAmoun =((unitPrice) * (ordQnty));
			ordAmount=new BigDecimal(ordAmoun);
			flItemAddress.setOrderedAmt(ordAmount);
			PmParameter pmParameter = new PmParameter();
		    pmParameter.setParameterId(155L);
			CmItem cmItem = new CmItem();
			cmItem.setItemId(flItemAddress.getCmItem().getItemId());
			cmItem.setExtensionCode(flItemAddress.getCmItem().getExtensionCode());
			
			//Fetch flitem from list by looping item list.
			//FlFulfilmentItem fulfilmentItem =new FlFulfilmentItem();
			
		    flItemAddress.setItemType(pmParameter);
		    flItemAddress.setCmItem(cmItem);
		    flItemAddress.getScRequestItem().setCmItem(cmItem);
		    //flItem.setPmVotFund(pmVotFund);
		    
		   // preparing FlFulfilmentItem for SAVE
		    //flItem.getFlFulfilmentItem().setFlFulfilmentRequest(flFulfilmentRequest)
			flItemAddress.getFlFulfilmentItem().setFlFulfilmentRequest(getManagedBean().getFlRequest());
		    flItemAddress.getFlFulfilmentItem().setUnitPrice(flItemAddress.getUnitPrice());
		    flItemAddress.getFlFulfilmentItem().setChargeLineSeq(1);
		    flItemAddress.getFlFulfilmentItem().setItemType(pmParameter);
		    flItemAddress.getFlFulfilmentItem().setCmItem(cmItem);
			//fulfilmentItem.setPmVotFund(pmVotFund);
			//flItem.setFlFulfilmentItem(fulfilmentItem);
			
		    /*if(null == getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr()){
				getManagedBean().getSelectedDelAddr().setFlFulfilmentItemAddr(new ArrayList<FlFulfilmentItemAddr>());
			}*/
		  //  flItemAddress.setFlFulfilmentItem(flItemAddress.getFlFulfilmentItem());
		    flItemAddress.setFlDeliveryAddress(getManagedBean().getSelectedDelAddr());
			//getManagedBean().getSelectedDelAddr().getFlFulfilmentItemAddr().add(flItem);
			
		}
		// add  or remove items to Selected delivery address
		//itemsToSelectedAddress(flItem);
	}
	
	public void editManualAddress(FlDeliveryAddress flDelAddress){
		getManagedBean().setFlManualAddress(flDelAddress);
		flDelAddress.setEditable(true);
		
	}
	
	
	
	public void flManualDeliveryAddr() {
		/*getManagedBean().setFlManualAddress(new FlDeliveryAddress());
		getManagedBean().getFlManualAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
		//getManagedBean().getFlManualAddress().getAddressType().setAddressTypeId(1L);
		getManagedBean().getFlManualAddress().setPmAddress(new PmAddress());
		//getManagedBean().getFlManualAddress().getAddressType().getPmAddress().setAddressId(1L);
		getManagedBean().getFlManualAddress().getPmAddress().setPmCountry(new PmCountry());
		getManagedBean().getFlManualAddress().getPmAddress().getPmCountry().setCountryId(0L);
		getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());*/
		
		getManagedBean().setFlManulaAddress(new FlDeliveryAddress());
		getManagedBean().getFlManulaAddress().setDeliveryAddressId(1L);
		getManagedBean().getFlManulaAddress().setPmAddress(new PmAddress());
		// default to Malaysia
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryCode("MY");
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCountry(countrySO.retrieveCountryByCode(countryReqDTO).getPmCountry());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmState(new PmState());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getFlManulaAddress().getPmAddress().setPmDivision(new PmDivision());
		getManagedBean().getFlManulaAddress().setReceivingGrp(new PmAddressType());
		
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(getManagedBean().getFlManulaAddress().getPmAddress().getPmCountry().getCountryId());
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
		
		if(getManagedBean().getAddressTypesList()==null) {
			ContractAmendmentReqDTO ctReqDTO = new ContractAmendmentReqDTO();
			ctReqDTO.setCtContract(getManagedBean().getFlRequest().getCtContract());
			IContractAmendmentSO ctAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAmendmentSO.class);
			ContractAmendmentResDTO respDTO = ctAmendmentSO.getActiveLocation(ctReqDTO);
			System.out.println("address List::: "+respDTO.getAgencyAddressList().size());
			getManagedBean().setAddressTypesList(respDTO.getAgencyAddressList());
			
			
		}
		if(getManagedBean().getManualAddressMap() == null || getManagedBean().getManualAddressMap().size() == 0){
			setManualAddressList();
		}		
	}
	
	private void setManualAddressList(){
		Map<Long, List<CtAgencyAddress>> addressTypeMap = new HashMap<Long, List<CtAgencyAddress>>();
		for(CtAgencyAddress addressType : getManagedBean().getAddressTypesList()){
			Long key = addressType.getAddressType().getPmAddress().getPmDistrict().getDistrictId();
			if(!addressTypeMap.containsKey(key)){
				addressTypeMap.put(key, new ArrayList<CtAgencyAddress>());
			}
			addressTypeMap.get(key).add(addressType);
		}
		getManagedBean().setManualAddressMap(addressTypeMap);
	}
	 public void combineSupAddress(FlFulfilmentRequest fulfilmentRequest){
	    	
	    	StringBuilder address = new StringBuilder();
	    	String strSpace = " ";
	    	//TODO
	    	/*if(null != fulfilmentRequest.getSupplierBranch()){
	    		if(null != fulfilmentRequest.getSupplierBranch().getSmAddress()){
	    			if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1()){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress1());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress2());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress3());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getAddress4());
	    	    		address.append(strSpace);
	    	    		
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmDistrict().getDistrictName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmState().getStateName());
	    	    		address.append(strSpace);
	    	    	
	    	    	}if(null != fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPmCountry().getCountryName());
	    	    		address.append("\n");
	    	    	}if(null !=fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode() ){
	    	    		address.append(fulfilmentRequest.getSupplierBranch().getSmAddress().getPostcode());
	    	    	}
	    	    	if(null != address.toString()){
	    	    		//System.out.println("addresss     "+address.toString());
	    	    	}
	    			
	    		}
	    		
	    	}*/
	    	
	    	this.getManagedBean().setSupplierAddress(address.toString());
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
	public void receivingOfficerList(FlDeliveryAddress flDelAddress){
		/*String addressType="D";
		
		if(null== flDelAddress.getAddressType().getPmAddress().getAddressId()){
			flDelAddress.getAddressType().getPmAddress().setAddressId(1L);
		}
		AddressResDTO resDTO=getReceivingOfficerList(flDelAddress.getAddressType().getPmAddress().getAddressId(),addressType);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());*/
		AddressResDTO resDTO=getReceivingOfficerList(flDelAddress.getPmAddress().getAddressId(),AddressType.RECEIVING_OFFICER_ADDRESS.value());
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
	}
	public AddressResDTO getReceivingOfficerList(Long addressId, String addressType){
		IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject("pm",IAddressSO.class);
		AddressReqDTO reqDTO = new AddressReqDTO();
		reqDTO.setAddressId(addressId);
		reqDTO.setAddressType(addressType);
		AddressResDTO resDTO = addressSO.retrieveReceivingOfficerList(reqDTO);
		getManagedBean().setReceivingOffList(resDTO.getAddressTypeList());
		System.out.println("size ::: "+resDTO.getAddressTypeList().size());
		//resDTO.get
		return resDTO;
	}
	public void retrieveCountryList() {
		
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		getManagedBean().setCountryList(resDTO.getCountryList());
	}
	
	public void retrieveStateList(AjaxBehaviorEvent e) {
		Long countryIdId = (Long) e.getComponent().getAttributes().get("value");
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryIdId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		getManagedBean().setStateList(resDTO.getStateList());
	}
	
	public void retrieveDivisionList(AjaxBehaviorEvent e){
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DivisionReqDTO reqDTO= new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		getManagedBean().setDivisionList(resDTO.getDivisionList());
	}
	
	public void retrieveDistrictListByDivisonId(AjaxBehaviorEvent e) {
		Long divisionId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDivisionId(divisionId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByDivision(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());
		
	}
	
	public void retrieveDistrictList(AjaxBehaviorEvent e) {
		Long stateId = (Long) e.getComponent().getAttributes().get("value");
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		//reqDTO.setDivisionId(divisionId);
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		getManagedBean().setDistrictList(resDTO.getDistrictList());
		
	}
	
	public void retrieveCityList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		getManagedBean().setCityList(resDTO.getCityList());
	}
	public void retrieveDeliveryAddressList(AjaxBehaviorEvent e) {
		Long districtId = (Long) e.getComponent().getAttributes().get("value");
		//getManagedBean().setManualAddressList(getManagedBean().getManualAddressMap().get(districtId));
		
		List<SelectItem> manualAddrList = new ArrayList<SelectItem>();
		if(getManagedBean().getManualAddressList()!=null) {
			for (PmAddressType obj : getManagedBean().getManualAddressList()) {
				manualAddrList.add(new SelectItem(obj.getAddressTypeId(), obj.getPmAddress().getAddressName()));
			}
		}
		getManagedBean().setManualAddrList(manualAddrList);
	}
	public void onTabChange(int selectedTabIndex){
		if(selectedTabIndex==3){
			getManagedBean().setSelectedNode(null);
			getManagedBean().setVotFundCode(null);
			
			getManagedBean().populateChargeLineData();	
			setClassName("@(.crChargeLinePnlClass)");
			getVotFundList(4L);
		}
	}
	public void onNodeSelect() {
		TreeNode selectedNode=getManagedBean().getSelectedNode();
				if(null!=selectedNode){
					for(TreeNode children:selectedNode.getChildren()){
						FlFulfilmentItemAddr childData=((FlFulfilmentItemAddr)children.getData());
						if(null !=childData.getPmVotFund()){
							//do it for others
							String votFundCode=getVotFundCodeById(childData.getPmVotFund().getVotFundId());
							getManagedBean().setVotFundCode(votFundCode);
							String progActvCode=getprogActivityCodeById(childData.getPmPrgActivity().getPrgActivityId());
							getManagedBean().setPrgActivityCode(progActvCode);
							String projectCode=getProjectCodeById(childData.getPmSubSetia().getPmSetia().getPmProject().getProjectId());
							getManagedBean().setProjectCode(projectCode);
							String setiaCode=getSetiaCodeById((childData.getPmSubSetia().getPmSetia().getSetiaId()));
							getManagedBean().setSetiaCode(setiaCode);
							String subSetiaCode=getSubSetiaCodeById((childData.getPmSubSetia().getSubSetiaId()));
							getManagedBean().setSubSetiaCode((subSetiaCode));
						}else{
							getManagedBean().setVotFundCode("");
							getManagedBean().setPrgActivityCode("");
							getManagedBean().setProjectCode("");
							getManagedBean().setSetiaCode("");
							getManagedBean().setSubSetiaCode((""));
						}
					}
				}
	}
	public void clearPopupLists(){
		getManagedBean().setVotFundId(0L);
		getManagedBean().setPrgActivityId(0L);
		getManagedBean().setProjectId(0L);
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
	}
	public void checkNodeSelected(){
		getManagedBean().setVotFundId(0L);
		getManagedBean().setPrgActivityId(0L);
		getManagedBean().setProjectId(0L);
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);
		getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		getVotFundList(4L);
		getManagedBean().setProjectList(new ArrayList<PmProject>());
		getManagedBean().setPrgActivityList(new ArrayList<PmPrgActivity>());
		getManagedBean().setSetiaList(new ArrayList<PmSetia>());
		getManagedBean().setSubSetiaList(new ArrayList<PmSubSetia>());
		/*if(null == getManagedBean().getSelectedNodes()){
			throw new EpException("Please Select Item Node.");
			
		}*/
		
	}
	
	public void onNodeUnSelect() {
		getManagedBean().getSelectedNodes();
		if("true".equalsIgnoreCase(getManagedBean().getChargeCheck())){
			if(null != getManagedBean().getSelectedNodes() ){
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmSubSetia().getSubSetiaId());
						getManagedBean().setPrgActivityId(((FlFulfilmentItemAddr)children.getData()).getPmPrgActivity().getPrgActivityId());
					}
				}
			}else{
				//setPmPrjSetia(0L);
				//setPmPrgActivity(0L);
				
			}
		}else{
			if(null != getManagedBean().getSelectedNodes() ){
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						//setPmPrjSetia(0L);
						//setPmPrgActivity(0L);
					}
				}
			}
		}
	}
	public void selectAllCheckboxSelect(AjaxBehaviorEvent e){
		//boolean checkValue=(boolean)e.getComponent().getAttributes().get("value")
		if((boolean) e.getComponent().getAttributes().get("value")){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress){
					deliveryAddress.setSelectedAddressRowDelete(true);
				}
			}
			getManagedBean().setDeleteButtonHide(false);
			
		}else{
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress){
					deliveryAddress.setSelectedAddressRowDelete(false);
				}
			}
			getManagedBean().setDeleteButtonHide(true);
			
		}
	}
	
	public void chargeLineApply() {
		getPmPrjSetia();
		getPmPrgActivity();
		getManagedBean().getVotFundId();
		updateSelectedTreeNodes();
		
			if(null == getManagedBean().getSelectedNodes()){
				throw new EpException("Please Select Item Node.");
				//(getManagedBean().getSelectedNodes().length >0 && null !=getManagedBean().getSelectedNodes())
			}else{
				
				if(null == getManagedBean().getVotFundId() || null== getManagedBean().getProjectId() || 
						null == getManagedBean().getPrgActivityId() || null == getManagedBean().getSetiaId() ||
						null== getManagedBean().getSubSetiaId()){
					throw new EpException("Please Select All Dropdowns");
				}
				updateAllItemsWithChargeDrops(getManagedBean().getSelectedNodes()); 
				//
				PmVotFund pmVotFund=new PmVotFund();
				PmSetia pmSetia=new PmSetia();
				PmSubSetia pmSubSetia =new PmSubSetia();
				PmPrgActivity pmPrgActivity=new PmPrgActivity();
				PmProject pmProject= new PmProject();
				pmVotFund.setVotFundId(getManagedBean().getVotFundId());
				pmVotFund.setVotFundCode(getManagedBean().getVotFundCode());
				pmSetia.setSetiaId(getManagedBean().getSetiaId());
				if(null != getManagedBean().getProjectId()){
					pmProject.setProjectId(getManagedBean().getProjectId());
					pmProject.setProjectCode(getManagedBean().getProjectCode());
					pmSetia.setPmProject(pmProject);
				}
				pmSubSetia.setSubSetiaId(getManagedBean().getSetiaId());
				pmSubSetia.setSubSetiaCode(getManagedBean().getSubSetiaCode());
				pmSubSetia.setPmSetia(pmSetia);
				pmPrgActivity.setPrgActivityId(getManagedBean().getPrgActivityId());
				pmPrgActivity.setPrgActivityCode(getManagedBean().getPrgActivityCode());
				
				for(TreeNode selectedNode:getManagedBean().getSelectedNodes()){
					for(TreeNode children:selectedNode.getChildren()){
						//do it for others
						
						FlFulfilmentItemAddr childItemAddress=(FlFulfilmentItemAddr)children.getData();
						childItemAddress.setPmSubSetia(pmSubSetia);
						childItemAddress.setPmPrgActivity(pmPrgActivity);
						childItemAddress.setPmVotFund(pmVotFund);
						childItemAddress.getFlFulfilmentItem().setPmSubSetia(pmSubSetia);
						childItemAddress.getFlFulfilmentItem().setPmPrgActivity(pmPrgActivity);
						childItemAddress.getFlFulfilmentItem().setPmVotFund(pmVotFund);
					}
					FlFulfilmentItemAddr childItemAddress=(FlFulfilmentItemAddr)selectedNode.getData();
					//Code for multiple delivery addresses based on item code
				for (FlDeliveryAddress delAddress : getManagedBean().getFlRequest().getFlDeliveryAddresses()) {
					for (FlFulfilmentItemAddr itemDelAddress : delAddress.getFlFulfilmentItemAddr()) {
						if (itemDelAddress.getCmItem().getItemId()
								.equals(childItemAddress.getCmItem().getItemId())) {
							
							itemDelAddress.setPmSubSetia(pmSubSetia);
							itemDelAddress.setPmPrgActivity(pmPrgActivity);
							itemDelAddress.setPmVotFund(pmVotFund);
							itemDelAddress.getFlFulfilmentItem().setPmSubSetia(pmSubSetia);
							itemDelAddress.getFlFulfilmentItem().setPmPrgActivity(pmPrgActivity);
							itemDelAddress.getFlFulfilmentItem().setPmVotFund(pmVotFund);
						}
					}
				}
				}
			}
			getManagedBean().populateChargeLineData();
	}
	//itemAddress.getScRequestItem().getRequestNoteItemId().equals(arg0)

	private void updateSelectedTreeNodes() {
		List<TreeNode> selectedNodes=new ArrayList<TreeNode>();
		TreeNode root=getManagedBean().getChargeLineItemRoot();
		for(TreeNode node:root.getChildren()){
			FlFulfilmentItemAddr addr=(FlFulfilmentItemAddr) node.getData();
			if(addr.isSelectedChargeItem()){
				//System.out.println("in update methos check ");
				selectedNodes.add(node);
			}
		}
		
      getManagedBean().setSelectedNodes(selectedNodes.toArray(new TreeNode[selectedNodes.size()]));
		
	}

	private void updateAllItemsWithChargeDrops(TreeNode[] selectedNodes) {
		if(null !=getManagedBean().getFlRequest().getFlDeliveryAddresses()){
			for(FlDeliveryAddress  deliveryAddress :getManagedBean().getFlRequest().getFlDeliveryAddresses()){
				if(null!=deliveryAddress.getFlFulfilmentItemAddr()){
					for(FlFulfilmentItemAddr itemAddress:deliveryAddress.getFlFulfilmentItemAddr()){
						if(null !=selectedNodes){
							for(TreeNode node:selectedNodes){
								//getRequestNoteItemId()
								FlFulfilmentItemAddr selectedItem=(FlFulfilmentItemAddr)node.getData();
								//itemAddress.getCmItem().getItemId()
								if(itemAddress.getCmItem().getItemId().equals(selectedItem.getCmItem().getItemId())){
									PmPrgActivity pmPrgActivity=new PmPrgActivity();
									itemAddress.setCoaValue("Y");
									//itemAddress.setPmSubSetia(getManagedBean().getSetiaId());
									//itemAddress.setPmPrgActivity(getManagedBean().getPrgActivityId());
								}
							}
						}
					}
				}	
			}
		}	
		
	}
	public OrgProfileResDTO getVotFundList(Long profileId){
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm",IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(profileId);
		OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
		if(null != resDTO.getPmOrgProfile()){
			for(PmVotFund pmVotFund : resDTO.getPmOrgProfile().getPmPtjGroup().getPmVotFunds()) {
				votFundCodeMap.put(pmVotFund.getVotFundId(), pmVotFund.getVotFundCode());
			}
		}
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);	
		if(null != resDTO.getPmOrgProfile()){
			getManagedBean().setVotFundList(resDTO.getPmOrgProfile().getPmPtjGroup().getPmVotFunds());
		}else{
			getManagedBean().setVotFundList(new ArrayList<PmVotFund>());
		}
		return resDTO;
	}
	
	public ProjectSetiaResDTO getProjectList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveProjectList(reqDTO);
			for(PmProject pmProject : resDTO.getProjectList()) {
				projectCodeMap.put(pmProject.getProjectId(), pmProject.getProjectCode());
			}
		getManagedBean().setProjectId(0L);	
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);	
		getManagedBean().setProjectList(resDTO.getProjectList());
		return resDTO;
	}
	
	public ProgramActivityResDTO getProgramList(AjaxBehaviorEvent e) {
		Long votFundId = (Long) e.getComponent().getAttributes().get("value");
		IProgramActivitySO programActivitySO = (IProgramActivitySO) BusinessDelegate.getInstance().getServiceObject("pm",IProgramActivitySO.class);
		ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
		reqDTO.setVotFundId(votFundId);
		ProgramActivityResDTO resDTO = programActivitySO.retrieveProgramActivityList(reqDTO);
			for(PmPrgActivity pmPrgActivity : resDTO.getPrgActivityList()) {
				progmActivityCodeMap.put(pmPrgActivity.getPrgActivityId(), pmPrgActivity.getPrgActivityCode());
			}
		getManagedBean().setPrgActivityId(0L);	
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSubSetiaId(0L);	
		getManagedBean().setPrgActivityList(resDTO.getPrgActivityList());
		return resDTO;
	}
	
	public ProjectSetiaResDTO getSetiaList(AjaxBehaviorEvent e) {
		Long projectId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setProjectId(projectId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSetiaList(reqDTO);
			for(PmSetia pmSetia : resDTO.getSetiaList()) {
				setiaCodeMap.put(pmSetia.getSetiaId(), pmSetia.getSetiaCode());
			}
		getManagedBean().setSetiaId(0L);
		getManagedBean().setSetiaList(resDTO.getSetiaList());
		return resDTO;
	}
	
	public ProjectSetiaResDTO getSubSetiaList(AjaxBehaviorEvent e) {
		Long setiaId = (Long) e.getComponent().getAttributes().get("value");
		IProjectSetiaSO projectSetiaSO = (IProjectSetiaSO) BusinessDelegate.getInstance().getServiceObject("pm",IProjectSetiaSO.class);
		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		reqDTO.setSetiaId(setiaId);
		ProjectSetiaResDTO resDTO = projectSetiaSO.retrieveSubSetiaList(reqDTO);
		
			for(PmSubSetia pmSubSetia : resDTO.getSubSetiaList()) {
				subSetiaCodeMap.put(pmSubSetia.getSubSetiaId(), pmSubSetia.getSubSetiaCode());
			}
		getManagedBean().setSubSetiaId(0L);
		getManagedBean().setSubSetiaList(resDTO.getSubSetiaList());
		return resDTO;
	}

	// display itemCode,itemName,QueryDesc in popup
	public void showItemQuery(FlFulfilmentItemAddr flItemAddress){
		
		flItemAddress.getScRequestItem().getItemName();
		setQueryItemCode(flItemAddress.getCmItem().getItemId().toString());
		setQueryItemName(flItemAddress.getScRequestItem().getItemName());
	}
	//get chargeline code by ID
	
		public String getVotFundCodeById(Long votFundId) {
			return votFundCodeMap.get(votFundId);
		}
		public String getprogActivityCodeById(Long progActvId) {
			return progmActivityCodeMap.get(progActvId);
		}
		public String getProjectCodeById(Long projectId) {
			return projectCodeMap.get(projectId);
		}
		public String getSetiaCodeById(Long setiaId) {
			return setiaCodeMap.get(setiaId);
		}
		public String getSubSetiaCodeById(Long subSetiaId) {
			return subSetiaCodeMap.get(subSetiaId);
		}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the queryItemCode
	 */
	public String getQueryItemCode() {
		return queryItemCode;
	}

	/**
	 * @param queryItemCode the queryItemCode to set
	 */
	public void setQueryItemCode(String queryItemCode) {
		this.queryItemCode = queryItemCode;
	}

	/**
	 * @return the queryItemName
	 */
	public String getQueryItemName() {
		return queryItemName;
	}

	/**
	 * @param queryItemName the queryItemName to set
	 */
	public void setQueryItemName(String queryItemName) {
		this.queryItemName = queryItemName;
	}

	/**
	 * @return the queryDesc
	 */
	public String getQueryDesc() {
		return queryDesc;
	}

	/**
	 * @param queryDesc the queryDesc to set
	 */
	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	/**
	 * @return the votFundCodeMap
	 */
	public static Map<Long, String> getVotFundCodeMap() {
		return votFundCodeMap;
	}

	/**
	 * @param votFundCodeMap the votFundCodeMap to set
	 */
	public static void setVotFundCodeMap(Map<Long, String> votFundCodeMap) {
		ContractRequestBackingBean.votFundCodeMap = votFundCodeMap;
	}

	/**
	 * @return the progmActivityCodeMap
	 */
	public static Map<Long, String> getProgmActivityCodeMap() {
		return progmActivityCodeMap;
	}

	/**
	 * @param progmActivityCodeMap the progmActivityCodeMap to set
	 */
	public static void setProgmActivityCodeMap(
			Map<Long, String> progmActivityCodeMap) {
		ContractRequestBackingBean.progmActivityCodeMap = progmActivityCodeMap;
	}

	/**
	 * @return the projectCodeMap
	 */
	public static Map<Long, String> getProjectCodeMap() {
		return projectCodeMap;
	}

	/**
	 * @param projectCodeMap the projectCodeMap to set
	 */
	public static void setProjectCodeMap(Map<Long, String> projectCodeMap) {
		ContractRequestBackingBean.projectCodeMap = projectCodeMap;
	}

	/**
	 * @return the setiaCodeMap
	 */
	public static Map<Long, String> getSetiaCodeMap() {
		return setiaCodeMap;
	}

	/**
	 * @param setiaCodeMap the setiaCodeMap to set
	 */
	public static void setSetiaCodeMap(Map<Long, String> setiaCodeMap) {
		ContractRequestBackingBean.setiaCodeMap = setiaCodeMap;
	}

	/**
	 * @return the subSetiaCodeMap
	 */
	public static Map<Long, String> getSubSetiaCodeMap() {
		return subSetiaCodeMap;
	}

	/**
	 * @param subSetiaCodeMap the subSetiaCodeMap to set
	 */
	public static void setSubSetiaCodeMap(Map<Long, String> subSetiaCodeMap) {
		ContractRequestBackingBean.subSetiaCodeMap = subSetiaCodeMap;
	}

	/**
	 * @return the pmPrjSetia
	 */
	public Long getPmPrjSetia() {
		return pmPrjSetia;
	}

	/**
	 * @param pmPrjSetia the pmPrjSetia to set
	 */
	public void setPmPrjSetia(Long pmPrjSetia) {
		this.pmPrjSetia = pmPrjSetia;
	}

	/**
	 * @return the pmPrgActivity
	 */
	public Long getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(Long pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * @return the disPmPrjSetia
	 */
	public Long getDisPmPrjSetia() {
		return disPmPrjSetia;
	}

	/**
	 * @param disPmPrjSetia the disPmPrjSetia to set
	 */
	public void setDisPmPrjSetia(Long disPmPrjSetia) {
		this.disPmPrjSetia = disPmPrjSetia;
	}

	/**
	 * @return the disPmPrgActivity
	 */
	public Long getDisPmPrgActivity() {
		return disPmPrgActivity;
	}

	/**
	 * @param disPmPrgActivity the disPmPrgActivity to set
	 */
	public void setDisPmPrgActivity(Long disPmPrgActivity) {
		this.disPmPrgActivity = disPmPrgActivity;
	}
}
