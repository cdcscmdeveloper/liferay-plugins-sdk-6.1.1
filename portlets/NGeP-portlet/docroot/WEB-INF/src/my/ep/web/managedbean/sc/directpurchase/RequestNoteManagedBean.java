/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
/**
 * 
 * @author sri
 *
 */
@ManagedBean(name="requestNoteMB")
@SessionScoped
public class RequestNoteManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private String sourceMethod = ParameterConst.METHOD_SC;
	private	ScRequestNote	requestNote = new	ScRequestNote(); //contain request list information
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // list of request supplier item to be recommend
	private	List<ScRequestNoteRemark>	selectedRemarks	=	new	ArrayList<ScRequestNoteRemark>();
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();//list request Note Dtl.
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	private	DualListModel<PmUser>	approvalUsers;
	private	String	fulfillmentType;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int	periodValue;
	private Long orgProfileId;
	private PmOrgProfile orgProfile = new PmOrgProfile();
	
	private List<PmOrgProfile> userDivisionList;
    private ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
    private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
    private List<PmUser> userList = new ArrayList<PmUser>();
    
    private String priceTypeCode;
    private boolean justTypeOption;
    private boolean procTypeOption;
    private boolean itemCheck; // For checking item whether service or product
    private SelectItem[] justDropdown; // justification drop down
    private SelectItem[] priceTypes; // price type drop down 
    private String processName;
    // BPM Task
    private SCDirectPurchaseDataType directPurchaseDataType;
    
    public RequestNoteManagedBean() {
	}
	/**
	 * @return the requestNote
	 */
	public ScRequestNote getRequestNote() {
		return requestNote;
	}
	/**
	 * @param requestNote the requestNote to set
	 */
	public void setRequestNote(ScRequestNote requestNote) {
		this.requestNote = requestNote;
	}
	/**
	 * @return the requestSupplierItems
	 */
	public List<ScRequestSupplierItem> getRequestSupplierItems() {
		return requestSupplierItems;
	}
	/**
	 * @param requestSupplierItems the requestSupplierItems to set
	 */
	public void setRequestSupplierItems(
			List<ScRequestSupplierItem> requestSupplierItems) {
		this.requestSupplierItems = requestSupplierItems;
	}
	/**
	 * @return the requestNoteDtls
	 */
	public List<ScRequestNoteDtl> getRequestNoteDtls() {
		return requestNoteDtls;
	}
	/**
	 * @param requestNoteDtls the requestNoteDtls to set
	 */
	public void setRequestNoteDtls(List<ScRequestNoteDtl> requestNoteDtls) {
		this.requestNoteDtls = requestNoteDtls;
	}
	/**
	 * @return the selectedRemarks
	 */
	public List<ScRequestNoteRemark> getSelectedRemarks() {
		return selectedRemarks;
	}
	/**
	 * @param selectedRemarks the selectedRemarks to set
	 */
	public void setSelectedRemarks(List<ScRequestNoteRemark> selectedRemarks) {
		this.selectedRemarks = selectedRemarks;
	}
	/**
	 * @return the approvalUsers
	 */
	public DualListModel<PmUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * @return the fulfillmentType
	 */
	public String getFulfillmentType() {
		return fulfillmentType;
	}
	/**
	 * @param fulfillmentType the fulfillmentType to set
	 */
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	/**
	 * @return the assignedToUserId
	 */
	public Long getAssignedToUserId() {
		return assignedToUserId;
	}
	/**
	 * @param assignedToUserId the assignedToUserId to set
	 */
	public void setAssignedToUserId(Long assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}
	/**
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}
	/**
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	/**
	 * @return the periodValue
	 */
	public int getPeriodValue() {
		return periodValue;
	}
	/**
	 * @param periodValue the periodValue to set
	 */
	public void setPeriodValue(int periodValue) {
		this.periodValue = periodValue;
	}
	/**
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}
	/**
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}
	/**
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}
	/**
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}
	
	/**
	 * @return the requestSupplierItem
	 */
	public ScRequestSupplierItem getRequestSupplierItem() {
		return requestSupplierItem;
	}
	/**
	 * @param requestSupplierItem the requestSupplierItem to set
	 */
	public void setRequestSupplierItem(ScRequestSupplierItem requestSupplierItem) {
		this.requestSupplierItem = requestSupplierItem;
	}
	/**
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}
	/**
	 * @param requestNoteDtl the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}
	
	/**
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}
	/**
	 * @param justTypeOption the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}
	/**
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}
	/**
	 * @param procTypeOption the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}
	/**
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}
	/**
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}
	/**
	 * @return the selectedRequestNoteDtls
	 */
	public List<ScRequestNoteDtl> getSelectedRequestNoteDtls() {
		return selectedRequestNoteDtls;
	}
	/**
	 * @param selectedRequestNoteDtls the selectedRequestNoteDtls to set
	 */
	public void setSelectedRequestNoteDtls(
			List<ScRequestNoteDtl> selectedRequestNoteDtls) {
		this.selectedRequestNoteDtls = selectedRequestNoteDtls;
	}
	/**
	 * @return the userList
	 */
	public List<PmUser> getUserList() {
		return userList;
	}
	/**
	 * @param userList the userList to set
	 */
	public void setUserList(List<PmUser> userList) {
		this.userList = userList;
	}
	/**
	 * @return the priceTypes
	 */
	public SelectItem[] getPriceTypes() {
		return priceTypes;
	}
	/**
	 * @param priceTypes the priceTypes to set
	 */
	public void setPriceTypes(SelectItem[] priceTypes) {
		this.priceTypes = priceTypes;
	}

	/**
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(DualListModel<PmUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/**
	 * @return the sourceMethod
	 */
	public String getSourceMethod() {
		return sourceMethod;
	}

	/**
	 * @param sourceMethod the sourceMethod to set
	 */
	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/**
	 * @return the priceTypeCode
	 */
	public String getPriceTypeCode() {
		return priceTypeCode;
	}

	/**
	 * @param priceTypeCode the priceTypeCode to set
	 */
	public void setPriceTypeCode(String priceTypeCode) {
		this.priceTypeCode = priceTypeCode;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the directPurchaseDataType
	 */
	public SCDirectPurchaseDataType getDirectPurchaseDataType() {
		return directPurchaseDataType;
	}

	/**
	 * @param directPurchaseDataType the directPurchaseDataType to set
	 */
	public void setDirectPurchaseDataType(
			SCDirectPurchaseDataType directPurchaseDataType) {
		this.directPurchaseDataType = directPurchaseDataType;
	}
	/**
	 * @return the itemCheck
	 */
	public boolean isItemCheck() {
		return itemCheck;
	}
	/**
	 * @param itemCheck the itemCheck to set
	 */
	public void setItemCheck(boolean itemCheck) {
		this.itemCheck = itemCheck;
	}
	
	/**
	 * @return the orgProfile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}
	/**
	 * @param orgProfile the orgProfile to set
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}
	public	boolean	isPrInquiry(){
		boolean result	= false;
		if(requestNote != null){
			for(ScRequestItem requestNoteItem: requestNote.getRequestNoteItems()){
				for(ScRequestSupplierItem supplierItem: requestNoteItem.getSupplierItems()){
					if(supplierItem.getScPrInquiy() != null){
						result	=	true;
						return result;
					}
				}
			}
		}
		return result;
	}
}
