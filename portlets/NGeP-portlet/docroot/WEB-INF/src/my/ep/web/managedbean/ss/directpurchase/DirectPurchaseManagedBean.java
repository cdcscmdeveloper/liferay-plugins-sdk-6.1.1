package my.ep.web.managedbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.ss.directpurchase.IDirectPurchaseSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.directpurchase.ScDpRemark;
import my.ep.persistence.entity.ss.directpurchase.ScPrInquiry;
import my.ep.persistence.entity.ss.directpurchase.ScRequestList;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNote;
import my.ep.persistence.entity.ss.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.ss.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.ss.helper.RequestNoteDtlHelper;

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
	private	ScRequestList	requestList; //contain request list information
	private	ScRequestListItem	selectedRequestListItem;
	
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // list of request supplier item to be recommend
	private	ScRequestSupplierItem	recommededRequestSupplierItem	=	new ScRequestSupplierItem();
	private	Long	recommendedSupplierId;
	private	List<SmSupplier>	supplierList = new ArrayList<SmSupplier>();// List of suppliers to be recommend
	
	private	ScRequestSupplierItem	selectedItem	=	new ScRequestSupplierItem(); // pr inquiry item
	
	private	List<ScRequestSupplierItem>	selectedSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // contain list of supplier selected item carry to simple quote/bid
	
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();//list request Note Dtl.
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	private	DualListModel<PmUser>	approvalUsers	=	new DualListModel<PmUser>();
	private	List<PmUser>	govUsers	=	new ArrayList<>();
	private	String	fulfillmentType;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int		periodValue;
	
	private	String	requestListRemark;
	private	String	recommendAction;
	private	Boolean	multipleRecommend = Boolean.FALSE;
	private	ScPrInquiry	scPrInquiry = new ScPrInquiry();
	
	public DirectPurchaseManagedBean() {
		super();
		initialInformation();
		requestList	=	new	ScRequestList();
		PmStatus	status	=	new	PmStatus();
		status.setStatusId(61000L);
		requestList.setPmStatus(status);
		requestList.setProcurementType("N");
		prepareDummyData();
	}
	public	void	initialInformation(){
		IUserSO iUserSO	=	(IUserSO)BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		List<PmUser>	availableApprovalUsers	=	new ArrayList<PmUser>();
		//TODO
		reqDTO.setOrgProfileId(1250l);
		UserResDTO resDTO = iUserSO.retrieveUserGroupList(reqDTO);
		for(PmUserGroupUser user : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
			availableApprovalUsers.add(user.getPmUser());
		}
		approvalUsers.setSource(availableApprovalUsers);
		resDTO = iUserSO.retrieveGovUserList();
		this.govUsers	=	resDTO.getUserList();
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
	public void prepareRequestNoteDtl(){
		List<ScRequestNote>		requestNotes	=	new ArrayList<ScRequestNote>();
	}
	
	public ScRequestList getRequestList() {
		return requestList;
	}

	public void setRequestList(ScRequestList requestList) {
		this.requestList = requestList;
	}

	public ScRequestSupplierItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ScRequestSupplierItem selectedItem) {
		this.selectedItem = selectedItem;
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
	
}
