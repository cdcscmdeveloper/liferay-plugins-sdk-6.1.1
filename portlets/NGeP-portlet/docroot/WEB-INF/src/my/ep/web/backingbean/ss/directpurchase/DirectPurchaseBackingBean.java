package my.ep.web.backingbean.ss.directpurchase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.ss.directpurchase.IQuoteSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ss.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.ss.directpurchase.QuoteReqDTO;
import my.ep.dto.ss.directpurchase.QuoteResDTO;
import my.ep.dto.ss.directpurchase.RequestNoteReqDTO;
import my.ep.dto.ss.directpurchase.RequestNoteResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScApprovalUser;
import my.ep.persistence.entity.ss.directpurchase.ScRNWorkFlowStatus;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScQuote;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.DirectPurchaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.ss.helper.ApprovalUserHelper;
import my.ep.web.util.ss.helper.RequestListHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author Tran Ngoc Hoa
 * 
 */
@ManagedBean(name = "directPurchaseBackingBean")
@RequestScoped
public class DirectPurchaseBackingBean extends BaseBackingBean<DirectPurchaseManagedBean> {
	private static Logger log = Logger.getLogger(DirectPurchaseBackingBean.class);

	public DirectPurchaseBackingBean() {
		super();
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
			if (user != null) {
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}
		ScRequestNote requestNote = new	ScRequestNote();
		requestNote.setRemarks(initializeRemarks(requestNote.getRemarks()));
		getManagedBean().setRequestNote(requestNote);
		populateUserDivisionList();
		
		//getManagedBean().getRequestList().setRemarks(initializeRemarks(getManagedBean().getRequestList().getRemarks()));
	}

	public String submitAction(String action) {
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		/*RequestNoteReqDTO requestListReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestList = getManagedBean().getRequestList();
		if (this.getManagedBean().getRequestNoteDtls() != null && this.getManagedBean().getRequestNoteDtls().size() > 0) {
			requestList.setScRequestNotes(RequestNoteHelper.createRequestNoteFromRquestNoteDtls(this.getManagedBean().getRequestNoteDtls()));
		}
		if (this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0) {
			requestList.setScApprovalUsers(ApprovalUserHelper.createApprovalUsers(this.getManagedBean().getApprovalUsers().getTarget()));
		}
		// change status to pending approval
		PmStatus status = new PmStatus();
		status.setStatusId(SourcingConstants.REQUEST_LIST_PENDING_APPROVAL);
		requestList.setPmStatus(status);
		requestList = removeEmptyRemarkBeforeSave(requestList);
		requestListReqDTO.setRequestListDetail(requestList);
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		// submit BPM task
		BpmResponseDTO res = createBPMTask(requestListResDTO, true);
		getManagedBean().setTaskId(res.getTaskId());
		this.getManagedBean().setRequestList(requestListResDTO.getRequestListDetail());
		log.info("finish save request List, return directPurchase");*/
		return "/xhtml/shared/acknowledge.xhtml";
	}
	/**
	 * save request note object
	 * browser to the request note page
	 */
	public void requestNote() {
		log.info("start DirectPurchaseBackingBean.saveRequestList()");
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		requestNoteReqDTO.setRequestNote(requestNote);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		BpmResponseDTO res = createBPMTask(requestNoteResDTO,SourcingConst.REQUEST_NOTE, false);
		getManagedBean().setTaskId(res.getTaskId());
		getManagedBean().setRequestNote(requestNoteResDTO.getRequestNoteDetail());
		log.info("finish save request List, return directPurchase");
		//return to direct purchase page
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "requestNote");
	}

	public String simpleQuoteBid() {
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
		if (quoteBidBackingBean == null) {
			quoteBidBackingBean = new SimpleQuoteBidBackingBean();
		}
		// save request list and information
		/*IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		ScRequestNote requestNote = getManagedBean().getRequestNote();
		PmStatus status = new PmStatus();
		status.setStatusId(61000L);
		requestNote.setPmStatus(status);
		requestNote = removeEmptyRemarkBeforeSave(requestNote);
		
		RequestNoteReqDTO requestNoteReqDTO = new RequestNoteReqDTO();
		requestNoteReqDTO.setRequestNote(requestNote);
		RequestNoteResDTO requestNoteResDTO = directPurchaseSO.saveRequestNote(requestNoteReqDTO);
		//initialize or update BPM task
		BpmResponseDTO res = createBPMTask(requestNoteResDTO,SourcingConst.REQUEST_NOTE, false);
		quoteBidBackingBean.getManagedBean().setTaskId(res.getTaskId());*/

		// binding selected request supplier item to requestListItems and
		// quoteBidSupplier in quoteBidManagedBean
		Map<Long, ScRequestItem> uniqueRequestItemList = new HashMap<Long, ScRequestItem>();
		Map<Long, ScQuoteSupplier> uniqueQuoteBidSuppliers = new HashMap<Long, ScQuoteSupplier>();
		if (this.getManagedBean().getSelectedSupplierItems() != null && this.getManagedBean().getSelectedSupplierItems().size() > 0) {
			for (ScRequestSupplierItem supplierItem : this.getManagedBean().getSelectedSupplierItems()) {
				uniqueRequestItemList.put(supplierItem.getRequestNoteItem().getCmItem().getItemId(), supplierItem.getRequestNoteItem());
				uniqueQuoteBidSuppliers.put(supplierItem.getSmSupplier().getSupplierId(), new ScQuoteSupplier(supplierItem.getSmSupplier()));
			}
		}
		ScQuote quoteBid = new ScQuote();
		//quoteBid.setRequestNote(requestNoteResDTO.getRequestNoteDetail());
		//clear quote/bid Item and add the new one
		quoteBidBackingBean.getManagedBean().getQuoteBidItems().clear();
		quoteBidBackingBean.getManagedBean().getQuoteBidSuppliers().clear();
		if (!uniqueRequestItemList.isEmpty()) {
			quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(uniqueRequestItemList.values());
		}
		if (!uniqueQuoteBidSuppliers.isEmpty()) {
			quoteBidBackingBean.getManagedBean().getQuoteBidSuppliers().addAll(uniqueQuoteBidSuppliers.values());
			quoteBid.getScQuoteSuppliers().addAll(uniqueQuoteBidSuppliers.values());
		}

		quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBid);
		quoteBidBackingBean.getManagedBean().setAction(getManagedBean().getAction());
		return "createSimpleQuoteBid";
	}
	/**
	 * Search catalogue tab change, update request note Item and clear the cart
	 */
	public	void	tabChangeListener(){
		/*FacesScopes facesScopes = new FacesScopes();
		CatalogueBackingBean catalogueBackingBean = (CatalogueBackingBean) facesScopes.getRequestScope("catalogueBackingBean");

		if (catalogueBackingBean == null) {
			catalogueBackingBean = new CatalogueBackingBean();
		}
		List<ScRequestNoteItem> requestNoteItems = RequestNoteHelper.getUniqueItemList(catalogueBackingBean.getManagedBean().getCartList());
		// reset cartList
		//catalogueBackingBean.getManagedBean().getCartList().clear();
		getManagedBean().getRequestNote().setRequestNoteItems(RequestNoteHelper.mergeItem(getManagedBean().getRequestNote().getRequestNoteItems(), requestNoteItems));;*/
	}
	/**
	 * render the view base on the DP mode and task id
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		if (StringUtils.isNotBlank(taskId)) {
			this.getManagedBean().setTaskId(taskId);
			log.info("taskId : " + taskId);
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			FacesScopes facesScopes = new FacesScopes();
			String token = facesScopes.getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType) obj;
			getManagedBean().setOutComes(res.getAction());
			if (SourcingConst.SIMPLE_QUOTE.equalsIgnoreCase(directPurchaseDataType.getDpMode())) {
				//simple quote
				IQuoteSO quoteBidSO = (IQuoteSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteSO.class);
				QuoteReqDTO quoteBidReqDTO = new QuoteReqDTO();
				quoteBidReqDTO.setRequestListId(Long.valueOf(directPurchaseDataType.getDpId()));
				QuoteResDTO quoteBidResDTO = quoteBidSO.getQuoteDtl(quoteBidReqDTO);
				PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
				if (quoteBidBackingBean == null) {
					quoteBidBackingBean = new SimpleQuoteBidBackingBean();
				}
				quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBidResDTO.getQuoteBidDtl());
				quoteBidBackingBean.getManagedBean().setTaskId(String.valueOf(directPurchaseDataType.getDpId()));
				//quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(quoteBidResDTO.getQuoteBidDtl().getRequestNotes().getRequestNoteItems());
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "quoteBidSupplier");
			} else if(SourcingConst.PROCUREMENT_PLAN.equalsIgnoreCase(directPurchaseDataType.getDpMode())){
				//Procurement plan
				ScRequestNote	requestNote	=	new	ScRequestNote();
				getManagedBean().setRequestNote(requestNote);
				IProcurementPlanSO procurementSO	=	EpEjbBusinessDelegate.getInstance().getServiceObject(SourcingConst.PROCUREMENT_MODULE, IProcurementPlanSO.class);
				//get procurement plan and set to the request Note
				ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
				planReqDTO.setPlanId(Long.valueOf(directPurchaseDataType.getDpId()));
				ProcurementPlanResDTO planResDTO	=	procurementSO.retrievePlanById(planReqDTO);
				if(planResDTO.getPlan() != null){
					requestNote.setPpPlan(planResDTO.getPlan());
				}
				//status is new
				//empty org profile
				requestNote.setPmOrgProfile(new PmOrgProfile());
				//maybe user belong to more than one PTJ, first time user have to choose PTJ
				populateUserDivisionList();
				//return to direct purchase page
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "directPurchase");
			}else {
				directPurchaseDataType.getRecommend();
				log.info("requestList Id: ..........." + directPurchaseDataType.getDpId());
				IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
				RequestNoteReqDTO requestListReqDTO = new RequestNoteReqDTO();
				requestListReqDTO.setRequestNoteId(Long.valueOf(directPurchaseDataType.getDpId()));
				ScRequestNote requestList = directPurchaseSO.getRequestNoteWithDtls(requestListReqDTO).getRequestNoteDetail();
				/*this.getManagedBean().setRequestList(requestList);
				if (requestList.getScRequestNotes() != null && requestList.getScRequestNotes().size() > 0) {
					List<ScRequestNoteDtl> requestNoteDtls = new ArrayList<ScRequestNoteDtl>();
					for (ScRequestNote requestNote : requestList.getScRequestNotes()) {
						requestNoteDtls.addAll(requestNote.getScRequestNoteDtls());
					}
					getManagedBean().setRequestNoteDtls(requestNoteDtls);
				}*/
				List<PmUser> approvalUsers = ApprovalUserHelper.getListApprovalUser(requestList.getApprovalUsers());
				DualListModel<PmUser>	dualListDataModel = new DualListModel<PmUser>();
				dualListDataModel.setTarget(approvalUsers);
				IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
				UserReqDTO userReqDto = new UserReqDTO();
				userReqDto.setRoleCode(RoleCode.APPROVER.value());
				UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
				List<PmUser> availableApprovers = userResDTO.getUserList();
				// remove existing approvalUser in available list
				List<PmUser>	existingUser	=	new ArrayList<>();
				for (PmUser user : approvalUsers) {
					for(PmUser user_: availableApprovers){
						if(user.getUserId().equals(user_.getUserId())){
							existingUser.add(user_);
						}
					}
				}
				availableApprovers.removeAll(existingUser);
				dualListDataModel.setSource(availableApprovers);
				getManagedBean().setApprovalUsers(dualListDataModel);
				//getManagedBean().getApprovalUsers().setSource(availableApprovers);
				getManagedBean().setTaskId(taskId);
				userResDTO = iUserSO.retrieveGovUserList();
				getManagedBean().setGovUsers(userResDTO.getUserList());
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "directPurchase");
			}
		}
	}

	public void prInquiryListener(ScRequestSupplierItem scRequestSupplierItem) {
		getManagedBean().setInquirySupplierItem(scRequestSupplierItem);
		getManagedBean().setScPrInquiry(new ScPrInquiry());
	}

	public void savePrInquiry() {
		log.info("start method savePrInquiry().............");
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestNoteReqDTO requestListReqDTO = new RequestNoteReqDTO();
		ScRequestNote requestList = getManagedBean().getRequestNote();
		ScRequestSupplierItem inquirySupplierItem = getManagedBean().getInquirySupplierItem();
		List<ScPrInquiry> prInquiries = new ArrayList<ScPrInquiry>();
		prInquiries.add(getManagedBean().getScPrInquiry());
		inquirySupplierItem.setScPrInquiries(prInquiries);
		// change request list status to Pending Inquiry Response
		requestListReqDTO.setRequestNote(requestList);
		// save request list
		RequestNoteResDTO requestListResDTO = directPurchaseSO.saveRequestNote(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO,SourcingConst.REQUEST_NOTE, false);
		getManagedBean().setTaskId(res.getTaskId());

	}



	public void requestListItemSelection(SelectEvent event) {
		ScRequestItem item = (ScRequestItem) event.getObject();
		this.getManagedBean().setRequestSupplierItems(item.getSupplierItems());
		this.getManagedBean().setMultipleRecommend(Boolean.FALSE);
	}


	public String updateCatalogueSupplierItem() {
		FacesScopes facesScopes = new FacesScopes();
		// PortletSession portletSession = (PortletSession)
		// FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		CatalogueBackingBean catalogueBackingBean = (CatalogueBackingBean) facesScopes.getRequestScope("catalogueBackingBean");

		if (catalogueBackingBean == null) {
			catalogueBackingBean = new CatalogueBackingBean();
		}
		List<ScRequestItem> requestListItems = RequestListHelper.getUniqueItemList(catalogueBackingBean.getManagedBean().getCartList());
		// reset cartList
		catalogueBackingBean.getManagedBean().getCartList().clear();
		// TODO maybe have to merge RequestListItem with the existing
		// requestListItem in RequestList
		getManagedBean().getRequestNote().getRequestNoteItems().addAll(requestListItems);
		// for grouping
		//getManagedBean().getItems().getSource().addAll(requestListItems);
		return "directPurchase";
	}

	public void simpleQuoteAction() {
		getManagedBean().setAction(SourcingConstants.CREATE_SIMPLE_QUOTE);
	}

	public void simpleBidAction() {
		getManagedBean().setAction(SourcingConstants.CREATE_SIMPLE_BID);
	}

	public void applyForRequestNote() {
		for (ScRequestNoteDtl requestNoteDtl : getManagedBean().getSelectedRequestNoteDtls()) {
			requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
			//requestNoteDtl.setAssignedUserId(getManagedBean().getAssignedToUserId());
			requestNoteDtl.setDeliverPeriodType(getManagedBean().getPeriodType());
			requestNoteDtl.setDeliverPeriod(getManagedBean().getPeriodValue());
		}
		getManagedBean().getSelectedRequestNoteDtls().clear();
	}

	private BpmResponseDTO createBPMTask(RequestNoteResDTO requestListResDTO, String dpMode, Boolean submit) {
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setDpId(requestListResDTO.getRequestNoteDetail().getRequestNoteId().intValue());
		obj.setPtj("Asdqwe");
		if (submit) {
			String approvaUserId = "";
			if (getManagedBean().getRequestNote().getApprovalUsers() != null) {
				for (ScApprovalUser approvaUser : getManagedBean().getRequestNote().getApprovalUsers()) {
					approvaUserId = approvaUserId + approvaUser.getPmUser().getLoginId() + ",";
				}
				// remove last ,
				if (approvaUserId.endsWith(",")) {
					approvaUserId = approvaUserId.substring(0, approvaUserId.length() - 1);
				}
				obj.setApprover(approvaUserId);
				// TODO pass in all the recommended supplier Id
				obj.setRecommend("" + getManagedBean().getRequestNote().getRequestNoteItems().get(0).getSupplierItems().get(0).getSmSupplier().getSupplierId());
			}
			reqDTO.setOutcome("SUBMIT");
		}
		// obj.getSuppliers().add(String.valueOf(
		// this.getManagedBean().getSelectedItem().getSmSupplier().getSupplierId()));
		if (getManagedBean().getTaskId() != null) {
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res;
		if(!submit && reqDTO.getTaskId() != null){
			res = bpmSO.updateTask(reqDTO);
		}else{
			reqDTO.setDocNumber("dp-234");
			res = bpmSO.initiateAndUpdateTask(reqDTO);
		}
		return res;
	}

	public String updateBpmTask(String action) {
		//
		return null;
	}


	private void populateUserDivisionList() {
		log.info("populateUserMinistryList");

		PmUser user = new FacesScopes().getUserSessionBean();

		if (user != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(user.getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> userDivisionList = resDTO.getOrgProfileList();
			getManagedBean().setUserDivisionList(userDivisionList);
		}
	}
	private	ScRequestNote	removeEmptyRemarkBeforeSave(ScRequestNote requestList){
		ScRequestNote result	=	requestList;
		List<ScRequestNoteRemark> removeList = new ArrayList<ScRequestNoteRemark>();
		if(requestList.getRemarks() != null){
			for(ScRequestNoteRemark remark: requestList.getRemarks()){
				if(StringUtils.isBlank(remark.getRemark())){
					removeList.add(remark);
				}
			}
			result.getRemarks().removeAll(removeList);
		}
		return result;
	}
	private List<ScRequestNoteRemark> initializeRemarks(List<ScRequestNoteRemark> remarkList) {
		List<ScRequestNoteRemark> results	=	remarkList;
		FacesScopes facesScopes = new FacesScopes();
		if(results.size()==0 || !compareUserName(results.get(0).getUser().getLoginId())) {
			ScRequestNoteRemark remark = new ScRequestNoteRemark();
			remark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setUser(facesScopes.getUserSessionBean());
			PmStatus status = new PmStatus();
			status.setStatusId(SourcingConstants.RN_PENDING_SUBMISSION);
			remark.setPmStatus(status);
			results.add(0, remark);
		} 
		return results;
	}
	
	private	void	initialInformation(){
		IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> availableApprover	=	userResDTO.getUserList();
		List<PmUser> selectedApprover	=	new ArrayList<PmUser>();
		DualListModel<PmUser> approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);
		getManagedBean().setApprovalUsers(approvalUsers);
		userResDTO = iUserSO.retrieveGovUserList();
		getManagedBean().setGovUsers(userResDTO.getUserList());
	}
}