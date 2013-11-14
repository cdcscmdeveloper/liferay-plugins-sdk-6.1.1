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

import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.ss.directpurchase.RequestListReqDTO;
import my.ep.dto.ss.directpurchase.RequestListResDTO;
import my.ep.dto.ss.directpurchase.SelectOptionDTO;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.directpurchase.ScDpRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScQuoteBid;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.DirectPurchaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

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
	// TODO temporary hard code, change it later in the development phase

	private static Logger log = Logger.getLogger(DirectPurchaseBackingBean.class);
	private DualListModel<String> approveUseritems;

	public DirectPurchaseBackingBean() {
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

		List<String> citiesSource = new ArrayList<String>();
		List<String> citiesTarget = new ArrayList<String>();

		citiesSource.add("user1");
		citiesSource.add("user2");
		citiesSource.add("user3");
		citiesSource.add("user4");
		citiesSource.add("user5");

		approveUseritems = new DualListModel<String>(citiesSource, citiesTarget);
	}

	public DualListModel<String> getApproveUseritems() {
		return approveUseritems;
	}

	public void setApproveUseritems(DualListModel<String> approveUseritems) {
		this.approveUseritems = approveUseritems;
	}

	public void showPRInquiry(ScRequestSupplierItem supplierItem) {

	}

	public void saveRequestList(String action) {
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		requestListReqDTO.setRequestListDetail(this.getManagedBean().getRequestList());
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);

		// Create BPM task
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setProcessId("Quotation_Tender.DirectPurchaseCreation");
		// reqDTO.setOutcome("SUBMIT");
		SCDirectPurchaseDataType obj = new SCDirectPurchaseDataType();
		obj.setRequestId(String.valueOf(requestListResDTO.getRequestListDetail().getRequestListId()));
		obj.setPtj("Asdqwe");
		// obj.getSuppliers().add(String.valueOf(
		// this.getManagedBean().getSelectedItem().getSmSupplier().getSupplierId()));
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);

		//

		this.getManagedBean().setRequestList(requestListResDTO.getRequestListDetail());
		log.info("test test test");
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

	public void prInquiryListener(ScRequestSupplierItem scRequestSupplierItem) {
		this.getManagedBean().setSelectedItem(scRequestSupplierItem);
		this.getManagedBean().setScPrInquiry(new ScPrInquiry());
	}

	public void savePrInquiry() {
		log.info("start method savePrInquiry().............");
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

	public SelectOptionDTO getSelectedProcumentType() {
		return selectedProcumentType;
	}

	public void setSelectedProcumentType(SelectOptionDTO selectedProcumentType) {
		this.selectedProcumentType = selectedProcumentType;
	}

	public String	simpleQuoteBid(){
		PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		SimpleQuoteBidBackingBean quoteBidBackingBean = (SimpleQuoteBidBackingBean) portletSession.getAttribute("simpleQuoteBidBackingBean");
		if(quoteBidBackingBean == null){
			quoteBidBackingBean = new SimpleQuoteBidBackingBean();
		}
		//TODO
		//save request list and information
		IDirectPurchaseSO directPurchaseSO = (IDirectPurchaseSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IDirectPurchaseSO.class);
		RequestListReqDTO requestListReqDTO = new RequestListReqDTO();
		requestListReqDTO.setRequestListDetail(this.getManagedBean().getRequestList());
		RequestListResDTO requestListResDTO = directPurchaseSO.saveRequestList(requestListReqDTO);
		//binding selected request supplier item to requestListItems and quoteBidSupplier in quoteBidManagedBean
		Map<Long, ScRequestListItem>	uniqueRequestItemList	=	new HashMap<Long, ScRequestListItem> ();
		if(this.getManagedBean().getSelectedSupplierItems() != null && this.getManagedBean().getSelectedSupplierItems().size() > 0){
			for(ScRequestSupplierItem supplierItem: this.getManagedBean().getSelectedSupplierItems()){
				uniqueRequestItemList.put(supplierItem.getScRequestListItem().getRequestListItemId(), supplierItem.getScRequestListItem());
			}
		}
		quoteBidBackingBean.getManagedBean().getQuoteBidItems().addAll(uniqueRequestItemList.values());
		ScQuoteBid	quoteBid	=	new ScQuoteBid();
		quoteBid.setScRequestList(requestListResDTO.getRequestListDetail());
		quoteBidBackingBean.getManagedBean().setScQuoteBid(quoteBid);
		return "createSimpleQuoteBid";
	}
}
