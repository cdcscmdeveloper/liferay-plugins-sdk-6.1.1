package my.ep.web.backingbean.ss.directpurchase;

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

import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.biz.service.ss.directpurchase.IQuoteBidSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ss.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ss.directpurchase.QuoteBidReqDTO;
import my.ep.dto.ss.directpurchase.QuoteBidResDTO;
import my.ep.dto.ss.directpurchase.RequestListReqDTO;
import my.ep.dto.ss.directpurchase.RequestListResDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScApprovalUser;
import my.ep.persistence.entity.ss.directpurchase.ScDpRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBidSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.DirectPurchaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.ss.helper.ApprovalUserHelper;
import my.ep.web.util.ss.helper.RequestListHelper;
import my.ep.web.util.ss.helper.RequestNoteDtlHelper;
import my.ep.web.util.ss.helper.RequestNoteHelper;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.SelectEvent;

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
	}
	public	String	submitAction(String action){
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		ScRequestList requestList = getManagedBean().getRequestList();
		if(this.getManagedBean().getRequestNoteDtls() != null && this.getManagedBean().getRequestNoteDtls().size() > 0){
			requestList.setScRequestNotes(RequestNoteHelper.createRequestNoteFromRquestNoteDtls(this.getManagedBean().getRequestNoteDtls()));
		}
		if(this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0){
			requestList.setScApprovalUsers(ApprovalUserHelper.createApprovalUsers(this.getManagedBean().getApprovalUsers().getTarget()));
		}
		//change status to pending approval
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(SourcingConstants.REQUEST_LIST_PENDING_APPROVAL);
		requestList.setPmStatus(status);
		requestListReqDTO.setRequestListDetail(requestList);
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		//submit BPM task
		BpmResponseDTO res = createBPMTask(requestListResDTO, true);
		getManagedBean().setTaskId(res.getTaskId());
		this.getManagedBean().setRequestList(requestListResDTO.getRequestListDetail());
		log.info("finish save request List, return directPurchase");
		return "/xhtml/shared/acknowledge.xhtml";
	}

	public String saveRequestList(String action) {
		log.info("start DirectPurchaseBackingBean.saveRequestList()");
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		ScRequestList requestList = getManagedBean().getRequestList();
		if(this.getManagedBean().getRequestNoteDtls() != null && this.getManagedBean().getRequestNoteDtls().size() > 0){
			requestList.setScRequestNotes(RequestNoteHelper.createRequestNoteFromRquestNoteDtls(this.getManagedBean().getRequestNoteDtls()));
		}
		if(this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0){
			requestList.setScApprovalUsers(ApprovalUserHelper.createApprovalUsers(this.getManagedBean().getApprovalUsers().getTarget()));
		}
		requestListReqDTO.setRequestListDetail(requestList);
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO, false);
		getManagedBean().setTaskId(res.getTaskId());
		this.getManagedBean().setRequestList(requestListResDTO.getRequestListDetail());
		log.info("finish save request List, return directPurchase");
		return "/xhtml/shared/acknowledge.xhtml";
	}
	
	public String	simpleQuoteBid(){
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
		if(quoteBidBackingBean == null){
			quoteBidBackingBean = new SimpleQuoteBidBackingBean();
		}
		//save request list and information
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		ScRequestList requestList = getManagedBean().getRequestList();
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(61000L);
		requestList.setPmStatus(status);
		
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		requestListReqDTO.setRequestListDetail(requestList);
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO, false);
		quoteBidBackingBean.getManagedBean().setTaskId(res.getTaskId());
		
		//binding selected request supplier item to requestListItems and quoteBidSupplier in quoteBidManagedBean
		Map<Long, ScRequestListItem>	uniqueRequestItemList	=	new HashMap<Long, ScRequestListItem> ();
		Map<Long, ScQuoteBidSupplier>	uniqueQuoteBidSuppliers	=	new HashMap<Long, ScQuoteBidSupplier>();
		if(this.getManagedBean().getSelectedSupplierItems() != null && this.getManagedBean().getSelectedSupplierItems().size() > 0){
			for(ScRequestSupplierItem supplierItem: this.getManagedBean().getSelectedSupplierItems()){
				uniqueRequestItemList.put(supplierItem.getScRequestListItem().getCmItem().getItemId(), supplierItem.getScRequestListItem());
				uniqueQuoteBidSuppliers.put(supplierItem.getSmSupplier().getSupplierId(), new ScQuoteBidSupplier(supplierItem.getSmSupplier()));
			}
		}
		ScQuoteBid	quoteBid	=	new ScQuoteBid();
		quoteBid.setScRequestList(requestListResDTO.getRequestListDetail());
		if(!uniqueRequestItemList.isEmpty()){
			quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(uniqueRequestItemList.values());
		}
		if(!uniqueQuoteBidSuppliers.isEmpty()){
			quoteBidBackingBean.getManagedBean().getQuoteBidSuppliers().addAll(uniqueQuoteBidSuppliers.values());
			quoteBid.getScQuoteBidSuppliers().addAll(uniqueQuoteBidSuppliers.values());
		}
		
		quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBid);
		quoteBidBackingBean.getManagedBean().setAction(getManagedBean().getAction());
		return "createSimpleQuoteBid";
	}
	public	void	preRenderView(){
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		String	returnString	=	"directPurchase";
		this.getManagedBean().setTaskId(taskId);
		log.info("taskId : "+ taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		
		SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType)obj;
		getManagedBean().setOutComes(res.getAction());
		if("SQ".equalsIgnoreCase(directPurchaseDataType.getDpMode())){
			IQuoteBidSO quoteBidSO = (IQuoteBidSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteBidSO.class);
			QuoteBidReqDTO	quoteBidReqDTO	=	new QuoteBidReqDTO();
			quoteBidReqDTO.setRequestListId(Long.valueOf(directPurchaseDataType.getDpId()));
			QuoteBidResDTO	quoteBidResDTO	=	quoteBidSO.getQuoteBidDtl(quoteBidReqDTO);
			PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
			if(quoteBidBackingBean == null){
				quoteBidBackingBean = new SimpleQuoteBidBackingBean();
			}
			quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBidResDTO.getQuoteBidDtl());
			quoteBidBackingBean.getManagedBean().setTaskId(String.valueOf(directPurchaseDataType.getDpId()));
			quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(quoteBidResDTO.getQuoteBidDtl().getScRequestList().getRequestItems());
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "quoteBidSupplier");
		}else{
			directPurchaseDataType.getRecommend();
			log.info("requestList Id: ..........." + directPurchaseDataType.getDpId());
			IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
			RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
			requestListReqDTO.setRequestListId(Long.valueOf(directPurchaseDataType.getDpId()));
			ScRequestList requestList = directPurchaseSO.getRequestListWithDtls(requestListReqDTO).getRequestListDetail();
			this.getManagedBean().setRequestList(requestList);
			if(requestList.getScRequestNotes() != null && requestList.getScRequestNotes().size() > 0){
				List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
				for(ScRequestNote requestNote: requestList.getScRequestNotes()){
					requestNoteDtls.addAll(requestNote.getScRequestNoteDtls());
				}
				getManagedBean().setRequestNoteDtls(requestNoteDtls);
			}
			this.getManagedBean().initialInformation();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "directPurchase");
		}
	}
	public void prInquiryListener(ScRequestSupplierItem scRequestSupplierItem) {
		getManagedBean().setInquirySupplierItem(scRequestSupplierItem);
		getManagedBean().setScPrInquiry(new ScPrInquiry());
	}

	public void savePrInquiry() {
		log.info("start method savePrInquiry().............");
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		ScRequestList requestList = getManagedBean().getRequestList();
		ScRequestSupplierItem	inquirySupplierItem	=	getManagedBean().getInquirySupplierItem();
		List<ScPrInquiry> prInquiries = new ArrayList<ScPrInquiry>();
		prInquiries.add(getManagedBean().getScPrInquiry());
		inquirySupplierItem.setScPrInquiries(prInquiries);
		//change request list status to Pending Inquiry Response
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(SourcingConstants.REQUEST_LIST_PENDING_INQUIRY_RESPONSE);
		requestList.setPmStatus(status);
		requestListReqDTO.setRequestListDetail(requestList);
		//save	request	list
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO, false);
		getManagedBean().setTaskId(res.getTaskId());
		
	}
	public void addRemark() {
		ScDpRemark scDpRemark = new ScDpRemark();
		scDpRemark.setChangedDate(DateUtils.getCurrentDate());
		scDpRemark.setRemark(getManagedBean().getRequestListRemark());
		// TODO hard code pmstatus
		PmStatus status = new PmStatus();
		status.setStatusId(SourcingConstants.REQUEST_LIST_NEW);
		scDpRemark.setPmStatus(status);
		getManagedBean().setRequestListRemark("");
		this.getManagedBean().getRequestList().getScDpRemarks().add(scDpRemark);
	}

	public void deleteRemark(ScDpRemark scDpRemark) {
		if (getManagedBean().getRequestList().getScDpRemarks().contains(scDpRemark)) {
			getManagedBean().getRequestList().getScDpRemarks().remove(scDpRemark);
		}
	}
	 public void requestListItemSelection(SelectEvent event) {  
		 ScRequestListItem item	=	(ScRequestListItem) event.getObject();
		 this.getManagedBean().setRequestSupplierItems(item.getSupplierItems());
		 this.getManagedBean().setMultipleRecommend(Boolean.FALSE);
	}
	 
	public void	recommendActionListener(){
		if("U".equalsIgnoreCase(this.getManagedBean().getRecommendAction())){
			for(ScRequestListItem requestListItem: this.getManagedBean().getRequestList().getRequestItems()){
				requestListItem.setRecommended(false);
				requestListItem.setSelectedCheckBox(Boolean.FALSE);
				this.getManagedBean().setMultipleRecommend(false);
			}
		}
		if("R".equalsIgnoreCase(this.getManagedBean().getRecommendAction())){
			if(RequestListHelper.isMultipleRecommend(this.getManagedBean().getRequestList().getRequestItems())){
				this.getManagedBean().setMultipleRecommend(true);
				List<SmSupplier>	listSuppliers	=	new ArrayList<SmSupplier>(RequestListHelper.getRecommendSupplier(this.getManagedBean().getRequestList().getRequestItems()));
				this.getManagedBean().setSupplierList(listSuppliers);
			}else{
				this.getManagedBean().setMultipleRecommend(false);
			}
		}
		this.getManagedBean().setRecommendAction("-");
	}
	public	String	updateCatalogueSupplierItem(){
		FacesScopes	facesScopes	=	new FacesScopes();
		//PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		CatalogueBackingBean catalogueBackingBean = (CatalogueBackingBean) facesScopes.getRequestScope("catalogueBackingBean");
		
		if(catalogueBackingBean == null){
			catalogueBackingBean = new CatalogueBackingBean();
		}
		List<ScRequestListItem> requestListItems = RequestListHelper.getUniqueItemList(catalogueBackingBean.getManagedBean().getCartList());
		//reset cartList
		catalogueBackingBean.getManagedBean().getCartList().clear();
		//TODO maybe have to merge RequestListItem with the existing requestListItem in RequestList
		getManagedBean().getRequestList().getRequestItems().addAll(requestListItems);
		//for grouping
		getManagedBean().getItems().getSource().addAll(requestListItems);
		return "directPurchase";
	}
	public	void	simpleQuoteAction(){
		getManagedBean().setAction(SourcingConstants.CREATE_SIMPLE_QUOTE);
	}
	public	void	simpleBidAction(){
		getManagedBean().setAction(SourcingConstants.CREATE_SIMPLE_BID);
	}
	public void	applyRecommend(){
		if(this.getManagedBean().getMultipleRecommend()){
			this.getManagedBean().setMultipleRecommend(false);
			Long	recommendedSupplierId	=	this.getManagedBean().getRecommendedSupplierId();
			for(ScRequestListItem requestListItem: this.getManagedBean().getRequestList().getRequestItems()){
				if(requestListItem.isSelectedCheckBox()){
					for(ScRequestSupplierItem requestSupplierItem: requestListItem.getSupplierItems()){
						if(recommendedSupplierId.equals(requestSupplierItem.getSmSupplier().getSupplierId())){
							requestListItem.setRecommended(true);
							requestSupplierItem.setIsRecommended(Boolean.TRUE);
							break;
						}
					}
					requestListItem.setSelectedCheckBox(false);
				}
			}
		}else{
			ScRequestSupplierItem	recommendedSupplierItem = this.getManagedBean().getRecommededRequestSupplierItem();
			recommendedSupplierItem.setIsRecommended(Boolean.TRUE);
			this.getManagedBean().getSelectedRequestListItem().setRecommended(true);
			this.getManagedBean().setRecommededRequestSupplierItem(null);
		}
	}
	public	String	showRequestListAction(){
		List<ScRequestSupplierItem>	recommendedSupplierItemList	=	RequestListHelper.getRecommendRequestSupplierItems(this.getManagedBean().getRequestList());
		//TODO
		if(getManagedBean().getRequestList().getScRequestNotes() != null && getManagedBean().getRequestList().getScRequestNotes().size() > 0){
			List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
			for(ScRequestNote requestNote: getManagedBean().getRequestList().getScRequestNotes()){
				requestNoteDtls.addAll(requestNote.getScRequestNoteDtls());
			}
			getManagedBean().setRequestNoteDtls(requestNoteDtls);
		}else{
			getManagedBean().setRequestNoteDtls(RequestNoteDtlHelper.createRequestNoteDtls(recommendedSupplierItemList));
		}
		//set focus on RequestList tab
		getManagedBean().setActiveTabIndex(1);
		return "directPurchase";
	}
	
	public	void	applyForRequestNote(){
		for(ScRequestNoteDtl requestNoteDtl: getManagedBean().getSelectedRequestNoteDtls()){
			requestNoteDtl.setFulfillmentType(getManagedBean().getFulfillmentType());
			requestNoteDtl.setAssignedUserId(getManagedBean().getAssignedToUserId());
			requestNoteDtl.setDeliverPeriodType(getManagedBean().getPeriodType());
			requestNoteDtl.setDeliverPeriod(getManagedBean().getPeriodValue());
		}
		getManagedBean().getSelectedRequestNoteDtls().clear();
	}
	
	private	BpmResponseDTO	createBPMTask(RequestListResDTO requestListResDTO,Boolean submit){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String token = facesScopes.getBPMAuthenticationCtxToken();

		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);
		reqDTO.setProcessId(SCProcessNameConst.DIRECTPURCHASE_CREATION_PROCESS_ID);
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setDpId(requestListResDTO.getRequestListDetail().getRequestListId().intValue());
		obj.setPtj("Asdqwe");
		if(submit){
			String	approvaUserId	=	"";
			if(getManagedBean().getRequestList().getScApprovalUsers() != null){
				for(ScApprovalUser approvaUser: getManagedBean().getRequestList().getScApprovalUsers()){
					approvaUserId	=	approvaUserId+approvaUser.getPmUser().getLoginId() + ",";
				}
				//remove last ,
				if(approvaUserId.endsWith(",")){
					approvaUserId = approvaUserId.substring(0,approvaUserId.length()-1);
				}
				obj.setApprover(approvaUserId);
				//TODO pass in all the recommended supplier Id
				obj.setRecommend(""+ getManagedBean().getRequestList().getRequestItems().get(0).getSupplierItems().get(0).getSmSupplier().getSupplierId());
			}
			reqDTO.setOutcome("SUBMIT");
		}
		// obj.getSuppliers().add(String.valueOf(
		// this.getManagedBean().getSelectedItem().getSmSupplier().getSupplierId()));
		if(getManagedBean().getTaskId() != null){
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
		return res;
	}
	public	String	updateBpmTask(String action){
		//
		return null;
	}
}