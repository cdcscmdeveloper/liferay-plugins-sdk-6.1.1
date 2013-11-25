package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ss.directpurchase.RequestListReqDTO;
import my.ep.dto.ss.directpurchase.RequestListResDTO;
import my.ep.dto.ss.directpurchase.SelectOptionDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScDpRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBidSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
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
import my.ep.web.util.ss.helper.RequestListItemHelper;
import my.ep.web.util.ss.helper.RequestNoteDtlHelper;
import my.ep.web.util.ss.helper.RequestNoteHelper;
import my.ep.web.util.ss.helper.RequestSupplierItemHelper;

import org.apache.commons.collections.map.HashedMap;
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
	private List<SelectOptionDTO> procumentList = null;
	private static Map<String, List<SelectOptionDTO>> justificationMap = null;
	private List<SelectOptionDTO> justificationTypes = new ArrayList<SelectOptionDTO>();
	// TODO testing, remove it soon
	private SelectOptionDTO selectedProcumentType;
	private	int	activeTabIndex = 0;
	private static Logger log = Logger.getLogger(DirectPurchaseBackingBean.class);

	public DirectPurchaseBackingBean() {
		super();
		// TODO temporary hard code this one, remove it later
		if (MDC.get(LogKey.USER_ID) == null) {
			PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			User user = (User) request.getAttribute(WebKeys.USER);
			if (user != null) {
				// String userName = user.getScreenName();
				Long userName = user.getUserId();
				new LogKey(userName);
				if (log.isDebugEnabled()) {
					log.debug("userName: " + userName);
				}
			}
		}

	}

	public void showPRInquiry(ScRequestSupplierItem supplierItem) {

	}

	public void saveRequestList(String action) {
		log.info("start DirectPurchaseBackingBean.saveRequestList()");
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		if(this.getManagedBean().getRequestNoteDtls() != null && this.getManagedBean().getRequestNoteDtls().size() > 0){
			this.getManagedBean().getRequestList().setScRequestNotes(RequestNoteHelper.createRequestNoteFromRquestNoteDtls(this.getManagedBean().getRequestNoteDtls()));
		}
		if(this.getManagedBean().getApprovalUsers().getTarget() != null && this.getManagedBean().getApprovalUsers().getTarget().size() > 0){
			this.getManagedBean().getRequestList().setScApprovalUsers(ApprovalUserHelper.createApprovalUsers(this.getManagedBean().getApprovalUsers().getTarget()));
		}
		requestListReqDTO.setRequestListDetail(this.getManagedBean().getRequestList());
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO);
		getManagedBean().setTaskId(res.getTaskId());

		this.getManagedBean().setRequestList(requestListResDTO.getRequestListDetail());
		log.info("test test test");
	}
	
	public String	simpleQuoteBid(){
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
		if(quoteBidBackingBean == null){
			quoteBidBackingBean = new SimpleQuoteBidBackingBean();
		}
		//save request list and information
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		requestListReqDTO.setRequestListDetail(this.getManagedBean().getRequestList());
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		BpmResponseDTO res = createBPMTask(requestListResDTO);
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
		if(!uniqueRequestItemList.isEmpty()){
			quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(uniqueRequestItemList.values());
		}
		if(!uniqueQuoteBidSuppliers.isEmpty()){
			quoteBidBackingBean.getManagedBean().getQuoteBidSuppliers().addAll(uniqueQuoteBidSuppliers.values());
		}
		ScQuoteBid	quoteBid	=	new ScQuoteBid();
		quoteBid.setScRequestList(requestListResDTO.getRequestListDetail());
		quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBid);
		return "createSimpleQuoteBid";
	}
	public	void	preRenderView(){
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		log.info("taskId : "+ taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		//TODO
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		
		SCDirectPurchaseDataType directPurchaseDataType = (SCDirectPurchaseDataType)obj;
		directPurchaseDataType.getRecommend();
		log.info("requestList Id: ..........." + directPurchaseDataType.getRequestId());
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		requestListReqDTO.setRequestListId(Long.valueOf(directPurchaseDataType.getRequestId()));
		ScRequestList requestList = directPurchaseSO.getRequestListWithDtls(requestListReqDTO).getRequestListDetail();
		this.getManagedBean().setRequestList(requestList);
		this.getManagedBean().initialInformation();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "directPurchase");
	}
	public void prInquiryListener(ScRequestSupplierItem scRequestSupplierItem) {
		this.getManagedBean().setSelectedItem(scRequestSupplierItem);
		this.getManagedBean().setScPrInquiry(new ScPrInquiry());
	}

	public void savePrInquiry() {
		log.info("start method savePrInquiry().............");
	}
	public void addRemark() {
		ScDpRemark scDpRemark = new ScDpRemark();
		scDpRemark.setChangedDate(DateUtils.getCurrentDate());
		scDpRemark.setRemark(getManagedBean().getRequestListRemark());
		// TODO hard code pmstatus
		PmStatus status = new PmStatus();
		status.setStatusId(61000L);
		scDpRemark.setPmStatus(status);
		getManagedBean().setRequestListRemark("");
		this.getManagedBean().getRequestList().getScDpRemarks().add(scDpRemark);
	}

	public void deleteRemark(ScDpRemark scDpRemark) {
		if (getManagedBean().getRequestList().getScDpRemarks().contains(scDpRemark)) {
			getManagedBean().getRequestList().getScDpRemarks().remove(scDpRemark);
		}
	}
	public void setProcumentList(List<SelectOptionDTO> procumentList) {
		this.procumentList = procumentList;
	}

	public List<SelectOptionDTO> getJustificationTypes() {
		return this.justificationTypes;
	}

	public void setJustificationTypes(List<SelectOptionDTO> justificationTypes) {
		this.justificationTypes = justificationTypes;
	}

	public Map<String, List<SelectOptionDTO>> getJustificationMap() {
		return justificationMap;
	}

	public void setJustificationMap(Map<String, List<SelectOptionDTO>> justificationMap) {
		DirectPurchaseBackingBean.justificationMap = justificationMap;
	}

	public SelectOptionDTO getSelectedProcumentType() {
		return selectedProcumentType;
	}

	public void setSelectedProcumentType(SelectOptionDTO selectedProcumentType) {
		this.selectedProcumentType = selectedProcumentType;
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
			if(isMultipleRecommend(this.getManagedBean().getRequestList().getRequestItems())){
				this.getManagedBean().setMultipleRecommend(true);
				List<SmSupplier>	listSuppliers	=	new ArrayList<SmSupplier>(getRecommendSupplier(this.getManagedBean().getRequestList().getRequestItems()));
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
		List<ScRequestListItem> requestListItems = getUniqueItemList(catalogueBackingBean.getManagedBean().getCartList());
		//reset cartList
		catalogueBackingBean.getManagedBean().getCartList().clear();
		//TODO maybe have to merge RequestListItem with the existing requestListItem in RequestList
		getManagedBean().getRequestList().getRequestItems().addAll(requestListItems);
		return "directPurchase";
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
	public	void	showRequestListAction(){
		List<ScRequestSupplierItem>	recommendedSupplierItemList	=	getRecommendRequestSupplierItems(this.getManagedBean().getRequestList());
		this.getManagedBean().setRequestNoteDtls(RequestNoteDtlHelper.createRequestNoteDtls(recommendedSupplierItemList));
		//set focus on RequestList tab
		setActiveTabIndex(1);
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
	
	
	
	private	List<ScRequestListItem>		getUniqueItemList(List<CmSupplierItem> supplierItems){
		List<ScRequestListItem>	results	=	new ArrayList<ScRequestListItem>();
		Map<CmItem, List<CmSupplierItem>>	uniqueItemList	=	new HashMap<CmItem, List<CmSupplierItem>>();
		
		for(CmSupplierItem supplierItem: supplierItems){
			if(uniqueItemList.get(supplierItem.getCmItem()) != null){
				uniqueItemList.get(supplierItem.getCmItem()).add(supplierItem);
			}else{
				List<CmSupplierItem>	supplierItemList	=	new ArrayList<CmSupplierItem>();
				supplierItemList.add(supplierItem);
				uniqueItemList.put(supplierItem.getCmItem(), supplierItemList);
			}
		}
		Set<CmItem>	cmItems	=	uniqueItemList.keySet();
		for(CmItem cmItem: cmItems){
			List<CmSupplierItem>	supplierItemList	=	uniqueItemList.get(cmItem);
			ScRequestListItem	requestListItem	=	RequestListItemHelper.createRequestListItem(cmItem);
			requestListItem.setSupplierItems(RequestSupplierItemHelper.createRequestSupplierItems(requestListItem, supplierItemList));
			results.add(requestListItem);
		}
		return results;
	}
	
	
	private	Collection<SmSupplier>	getRecommendSupplier(List<ScRequestListItem> requestListItems){
		Map<Long,SmSupplier>	uniqueSmSuppliers	=	new HashMap<Long,SmSupplier>();
		for(ScRequestListItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				for(ScRequestSupplierItem requestSupplierItem: requestListItem.getSupplierItems()){
					uniqueSmSuppliers.put(requestSupplierItem.getSmSupplier().getSupplierId(), requestSupplierItem.getSmSupplier());
				}
			}
		}
		return uniqueSmSuppliers.values();
	}
	private	boolean	isMultipleRecommend(List<ScRequestListItem> requestListItems){
		int numberOfSelectedItem	= 0;
		for(ScRequestListItem requestListItem: requestListItems){
			if(requestListItem.isSelectedCheckBox()){
				numberOfSelectedItem = numberOfSelectedItem + 1;
			}
		}
		if(numberOfSelectedItem >=2){
			return true;
		}else{
			return false;
		}
	}
	
	private	List<ScRequestSupplierItem>	getRecommendRequestSupplierItems(ScRequestList requestList){
		List<ScRequestSupplierItem>	results	=	new ArrayList<ScRequestSupplierItem>();
		for(ScRequestListItem requestListItem: requestList.getRequestItems()){
			for(ScRequestSupplierItem supplierItem: requestListItem.getSupplierItems()){
				if(supplierItem.getIsRecommended()){
					results.add(supplierItem);
				}
			}
		}
		return results;
	}

	public int getActiveTabIndex() {
		return activeTabIndex;
	}

	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}
	public List<SelectOptionDTO> getProcumentList() {
		if (procumentList == null) {
			procumentList = new ArrayList<SelectOptionDTO>();
			SelectOptionDTO normalOption = new SelectOptionDTO();
			normalOption.setOptionValue("N");
			normalOption.setOptionName("Normal");
			normalOption.setDescription("Normal Procure");
			procumentList.add(normalOption);
			SelectOptionDTO adhocOption = new SelectOptionDTO();
			adhocOption.setOptionValue("A");
			adhocOption.setOptionName("Adhoc");
			adhocOption.setDescription("Adhoc Procure");
			procumentList.add(adhocOption);
			SelectOptionDTO emergencyOption = new SelectOptionDTO();
			emergencyOption.setOptionValue("E");
			emergencyOption.setOptionName("Emergency");
			emergencyOption.setDescription("Emergency Procure");
			procumentList.add(emergencyOption);
			SelectOptionDTO g2gOption = new SelectOptionDTO();
			g2gOption.setOptionValue("G");
			g2gOption.setOptionName("G2G");
			g2gOption.setDescription("Goverment to Goverment procurement");
			procumentList.add(g2gOption);

		}
		return procumentList;
	}
	public void procurementTypeChangeListener() {
		if (justificationMap == null) {
			justificationMap = new HashedMap();
			List<SelectOptionDTO> emergencyJustifycations = new ArrayList<SelectOptionDTO>();
			SelectOptionDTO epidemic = new SelectOptionDTO();
			epidemic.setOptionValue("E");
			epidemic.setOptionName("Epidemic");
			epidemic.setDescription("Emergency - Epidemic");
			emergencyJustifycations.add(epidemic);

			SelectOptionDTO fire = new SelectOptionDTO();
			fire.setOptionValue("F");
			fire.setOptionName("Fire");
			fire.setDescription("Emergency - Fire");
			emergencyJustifycations.add(fire);

			SelectOptionDTO flood = new SelectOptionDTO();
			flood.setOptionValue("B");
			flood.setOptionName("Flood");
			flood.setDescription("Emergency - Flood");
			emergencyJustifycations.add(flood);

			SelectOptionDTO others = new SelectOptionDTO();
			others.setOptionValue("O");
			others.setOptionName("Others");
			others.setDescription("Emergency - Others");
			emergencyJustifycations.add(others);

			justificationMap.put("E", emergencyJustifycations);

			List<SelectOptionDTO> adhocJustifications = new ArrayList<SelectOptionDTO>();
			SelectOptionDTO adhocMeeting = new SelectOptionDTO();
			adhocMeeting.setOptionValue("A");
			adhocMeeting.setOptionName("Ad hoc meeting");
			adhocMeeting.setDescription("Ad hoc meeting");
			adhocJustifications.add(adhocMeeting);

			SelectOptionDTO carBreak = new SelectOptionDTO();
			carBreak.setOptionValue("C");
			carBreak.setOptionName("Car Break down");
			carBreak.setDescription("Ad Hoc - Car Break down");
			adhocJustifications.add(carBreak);

			SelectOptionDTO adhocOthers = new SelectOptionDTO();
			adhocOthers.setOptionValue("O");
			adhocOthers.setOptionName("Others");
			adhocOthers.setDescription("Ad hoc - Others");
			adhocJustifications.add(adhocOthers);
			justificationMap.put("A", adhocJustifications);
		}
		this.justificationTypes = justificationMap.get(this.getManagedBean().getRequestList().getProcurementType());
		if (this.justificationTypes != null && this.justificationTypes.size() > 0) {
			this.getManagedBean().getRequestList().setJustificationType(justificationTypes.get(0).getOptionValue());
		}
	}
	private	BpmResponseDTO	createBPMTask(RequestListResDTO requestListResDTO){
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setProcessId("Sourcing.DirectPurchaseCreation");
		// reqDTO.setOutcome("SUBMIT");
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setRequestId(String.valueOf(requestListResDTO.getRequestListDetail().getRequestListId()));
		obj.setPtj("Asdqwe");
		// obj.getSuppliers().add(String.valueOf(
		// this.getManagedBean().getSelectedItem().getSmSupplier().getSupplierId()));
		if(getManagedBean().getTaskId() != null){
			reqDTO.setTaskId(getManagedBean().getTaskId());
		}
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
		return res;
	}
}
