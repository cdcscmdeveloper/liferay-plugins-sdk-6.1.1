package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.ss.directpurchase.SelectOptionDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
/**
 * 
 * @author Tran Ngoc Hoa
 *
 */
@ManagedBean(name="directPurchaseMB")
@SessionScoped
public class DirectPurchaseManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(DirectPurchaseManagedBean.class);
	
	
	
	private	String	action	=	SourcingConst.CREATE_DIRECT_PURCHASE;
	private	ScRequestList	requestList = new	ScRequestList(); //contain request list information
	private	ScRequestListItem	selectedRequestListItem;//selected request list item (data table)
	
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // list of request supplier item to be recommend
	private	ScRequestSupplierItem	recommededRequestSupplierItem	=	new ScRequestSupplierItem();// item selected in recommend supplier table
	private	ScRequestSupplierItem	inquirySupplierItem;
	private	Long	recommendedSupplierId; //Apply recommend supplier for all the selected Item
	private	List<SmSupplier>	supplierList = new ArrayList<SmSupplier>();// List of suppliers to be recommend apply for all selected item
	
	private	List<ScRequestSupplierItem>	selectedSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // contain list of supplier selected item carry to simple quote/bid
	
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();//list request Note Dtl.
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	
	private	List<PmUser>	availableApprover;
	private	List<PmUser>	selectedApprover;
	private	DualListModel<PmUser>	approvalUsers;
	private	List<PmUser>	govUsers	=	new ArrayList<>();
	private	String	fulfillmentType;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int		periodValue;
	
	private	String	requestListRemark;
	private	String	recommendAction;
	private	Boolean	multipleRecommend = Boolean.FALSE;
	private	ScPrInquiry	scPrInquiry = new ScPrInquiry();
	
	private List<SelectOptionDTO> procumentList = null;
	private static Map<String, List<SelectOptionDTO>> justificationMap = null;
	private List<SelectOptionDTO> justificationTypes = new ArrayList<SelectOptionDTO>();
	
	private	int	activeTabIndex = 0;
	
	public DirectPurchaseManagedBean() {
		super();
		requestList = new	ScRequestList();
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(SourcingConst.REQUEST_LIST_NEW);
		requestList.setPmStatus(status);
		initialInformation();
		prepareDummyData();
	}
	public	void	initialInformation(){
		IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		availableApprover	=	userResDTO.getUserList();
		selectedApprover	=	new ArrayList<PmUser>();
		approvalUsers =	new DualListModel<PmUser>(availableApprover, selectedApprover);
		userResDTO = iUserSO.retrieveGovUserList();
		this.govUsers	=	userResDTO.getUserList();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<ScRequestListItem> getRequestListItems() {
		return this.requestList.getRequestItems();
	}

	public List<ScRequestSupplierItem>	getScRequestSupplierItems(){
		List<ScRequestSupplierItem> results	=	new ArrayList<ScRequestSupplierItem>();
		if(requestList.getRequestItems() != null && requestList.getRequestItems().size() > 0){
			for(ScRequestListItem requestItem: requestList.getRequestItems()){
				results.addAll(requestItem.getSupplierItems());
			}
		}
		return results;
	}

	private	void prepareDummyData(){
		
		//TODO hard code org profile first
		PmOrgProfile	orgProfile	=	new PmOrgProfile();
		orgProfile.setOrgProfileId(1L);
		requestList.setPmOrgProfile(orgProfile);
	}
	public ScRequestList getRequestList() {
		return requestList;
	}

	public void setRequestList(ScRequestList requestList) {
		this.requestList = requestList;
	}

	public ScPrInquiry getScPrInquiry() {
		return scPrInquiry;
	}

	public void setScPrInquiry(ScPrInquiry scPrInquiry) {
		this.scPrInquiry = scPrInquiry;
	}

	public String getRequestListRemark() {
		return requestListRemark;
	}

	public void setRequestListRemark(String requestListRemark) {
		this.requestListRemark = requestListRemark;
	}

	public List<ScRequestSupplierItem> getSelectedSupplierItems() {
		return selectedSupplierItems;
	}

	public void setSelectedSupplierItems(List<ScRequestSupplierItem> selectedSupplierItems) {
		this.selectedSupplierItems = selectedSupplierItems;
	}

	public List<ScRequestNoteDtl> getRequestNoteDtls() {
		return requestNoteDtls;
	}

	public void setRequestNoteDtls(List<ScRequestNoteDtl> requestNoteDtls) {
		this.requestNoteDtls = requestNoteDtls;
	}

	public List<ScRequestNoteDtl> getSelectedRequestNoteDtls() {
		return selectedRequestNoteDtls;
	}

	public void setSelectedRequestNoteDtls(List<ScRequestNoteDtl> selectedRequestNoteDtls) {
		this.selectedRequestNoteDtls = selectedRequestNoteDtls;
	}

	public ScRequestListItem getSelectedRequestListItem() {
		return selectedRequestListItem;
	}

	public void setSelectedRequestListItem(ScRequestListItem selectedRequestListItem) {
		this.selectedRequestListItem = selectedRequestListItem;
	}

	public List<ScRequestSupplierItem> getRequestSupplierItems() {
		return requestSupplierItems;
	}

	public void setRequestSupplierItems(List<ScRequestSupplierItem> requestSupplierItems) {
		this.requestSupplierItems = requestSupplierItems;
	}

	public Boolean getMultipleRecommend() {
		return multipleRecommend;
	}

	public void setMultipleRecommend(Boolean multipleRecommend) {
		this.multipleRecommend = multipleRecommend;
	}

	public String getRecommendAction() {
		return recommendAction;
	}

	public void setRecommendAction(String recommendAction) {
		this.recommendAction = recommendAction;
	}

	public ScRequestSupplierItem getRecommededRequestSupplierItem() {
		return recommededRequestSupplierItem;
	}

	public void setRecommededRequestSupplierItem(ScRequestSupplierItem recommededRequestSupplierItem) {
		this.recommededRequestSupplierItem = recommededRequestSupplierItem;
	}

	public Long getRecommendedSupplierId() {
		return recommendedSupplierId;
	}

	public void setRecommendedSupplierId(Long recommendedSupplierId) {
		this.recommendedSupplierId = recommendedSupplierId;
	}

	public List<SmSupplier> getSupplierList() {
		return supplierList;
	}
	public void setSupplierList(List<SmSupplier> supplierList) {
		this.supplierList = supplierList;
	}

	public DualListModel<PmUser> getApprovalUsers() {
		return approvalUsers;
	}

	public void setApprovalUsers(DualListModel<PmUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	public List<PmUser> getGovUsers() {
		return govUsers;
	}

	public void setGovUsers(List<PmUser> govUsers) {
		this.govUsers = govUsers;
	}

	public String getFulfillmentType() {
		return fulfillmentType;
	}

	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}

	public Long getAssignedToUserId() {
		return assignedToUserId;
	}

	public void setAssignedToUserId(Long assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public int getPeriodValue() {
		return periodValue;
	}

	public void setPeriodValue(int periodValue) {
		this.periodValue = periodValue;
	}
	public List<PmUser> getAvailableApprover() {
		return availableApprover;
	}
	public void setAvailableApprover(List<PmUser> availableApprover) {
		this.availableApprover = availableApprover;
	}
	public List<PmUser> getSelectedApprover() {
		return selectedApprover;
	}
	public void setSelectedApprover(List<PmUser> selectedApprover) {
		this.selectedApprover = selectedApprover;
	}
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}
	public void setProcumentList(List<SelectOptionDTO> procumentList) {
		this.procumentList = procumentList;
	}
	
	public List<SelectOptionDTO> getJustificationTypes() {
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
		justificationTypes = emergencyJustifycations;
		return justificationTypes;
	}

	public void setJustificationTypes(List<SelectOptionDTO> justificationTypes) {
		this.justificationTypes = justificationTypes;
	}

	public Map<String, List<SelectOptionDTO>> getJustificationMap() {
		return justificationMap;
	}

	public void setJustificationMap(Map<String, List<SelectOptionDTO>> justificationMap) {
		DirectPurchaseManagedBean.justificationMap = justificationMap;
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
	public ScRequestSupplierItem getInquirySupplierItem() {
		return inquirySupplierItem;
	}
	public void setInquirySupplierItem(ScRequestSupplierItem inquirySupplierItem) {
		this.inquirySupplierItem = inquirySupplierItem;
	}
}
