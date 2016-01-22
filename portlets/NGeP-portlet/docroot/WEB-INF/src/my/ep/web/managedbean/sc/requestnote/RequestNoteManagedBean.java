/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.requestnote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.directpurchase.IDirectPurchaseSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.directpurchase.RequestNoteReqDTO;
import my.ep.dto.sc.directpurchase.RequestNoteResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DualListModel;
// TODO: Auto-generated Javadoc

/**
 * The Class RequestNoteManagedBean.
 *
 * @author sri
 * @since 18/02/2014
 */
@ManagedBean(name="requestNoteMB")
@SessionScoped
public class RequestNoteManagedBean extends BaseManagedBean{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The source method. */
	private String sourceMethod = ParameterConst.METHOD_SC;
	
	/** The request note. */
	private	ScRequestNote	requestNote = new ScRequestNote(); //contain request list information
	
	/** The request supplier items. */
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // list of request supplier item to be recommend
	
	/** The selected remarks. */
	private	List<ScRequestNoteRemark>	selectedRemarks	=	new	ArrayList<ScRequestNoteRemark>();
	
	/** The request note dtls. */
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();//list request Note Dtl.
	
	/** The selected request note dtls. */
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	
	/** The request note items. */
	private List<ScRequestItem>	requestNoteItems = new ArrayList<ScRequestItem>();
	
	/** The approval users. */
	private	DualListModel<PmUser>	approvalUsers = new DualListModel<PmUser>();
	
	/** The fulfillment type. */
	private	String	fulfillmentType;
	
	/** The supplier id. */
	private Long supplierId;
	
	/** The sm supplier. */
	private SmSupplier smSupplier;
	
	/** The assigned to user id. */
	private	Long	assignedToUserId;
	
	/** The period type. */
	private	String	periodType;
	
	/** The period value. */
	private	Integer	periodValue;
	
	/** The user division list. */
	private List<PmOrgProfile> userDivisionList;
    
    /** The request supplier item. */
    private ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
    
    /** The request note dtl. */
    private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
    
    /** The user list. */
    private List<PmUser> userList = new ArrayList<PmUser>();
    
    /** The price type code. */
    private String priceTypeCode;
    
    /** The just type option. */
    private boolean justTypeOption;
    
    /** The proc type option. */
    private boolean procTypeOption;
    
    /** The item check. */
    private boolean itemCheck; // For checking item whether service or product
    
    /** The just dropdown. */
    private SelectItem[] justDropdown; // justification drop down
    
    /** The price types. */
    private SelectItem[] priceTypes; // price type drop down for add to cart
    
    /** The procurement types. */
    private SelectItem[] procurementTypes; // for procurement type dropdown
    
    /** The request note status. */
    private PmStatus requestNoteStatus;
    
    /** The required. */
    private boolean required = Boolean.FALSE;
    
    /** The just required. */
    private boolean justRequired = Boolean.FALSE;
    
    /** The add to cart flag. */
    private boolean addToCartFlag;
    
    /** The justification. */
    private String justification;
    
    /** The justification type. */
    private String justificationType;
    
    /** The procurement type. */
    private String procurementType;
    
    /** The price type. */
    private String priceType;
    
    /** The title checker. */
    private boolean titleChecker = Boolean.TRUE;
    
    /** The pr inquiry id. */
    private Long prInquiryId; // To get PI id to cancel in request note
    
    /** The sc rn zone. */
    private ScRNZone scRNZone = new ScRNZone(); 
    
    // For Search Request Note
    /** The search req note no. */
    private String searchReqNoteNo;
    
    /** The search src method. */
    private String searchSrcMethod;
    
    /** The search created date from. */
    private Date searchCreatedDateFrom;
    
    /** The search created date to. */
    private Date searchCreatedDateTo;
    
    /** The search status. */
    private Long searchStatus;
    
    /** The status descs. */
    private List<PmStatusDesc> statusDescs;
    
    /** The created by. */
    private String createdBy;
	
	/** The supplier rec list. */
	private	List<ScRequestNoteDtl>	supplierRecList	=	new ArrayList<ScRequestNoteDtl>();
	
	/** The pricing list. */
	private	List<ScSupplierItemPrice>	pricingList	=	new ArrayList<ScSupplierItemPrice>(); // For pricing pop to show all pricing
    
    /** The req note data model. */
    private IPagingDataModel<ScRequestNote> reqNoteDataModel = new RequestNotePagingDataModel();
    
    /** The compare supplier items. */
    private List<CmSupplierItem> compareSupplierItems = new ArrayList<CmSupplierItem>(); // copy supplier items from the compare screen
    
 
    /** The item search back btn. */
    private boolean itemSearchBackBtn = Boolean.TRUE;
    
    /** The delete request note dtls. */
    private	List<ScRequestNoteDtl>	deleteRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
    
    /** The zone dtl map. */
    private	Map<String, TplSpecZoneDtl>	zoneDtlMap;
    
    /** The cm supplier item. */
    private CmSupplierItem cmSupplierItem;
    
    /** The recent price duration. */
    private String recentPriceDuration = ParameterConst.RECENT_PRICE_DURATION;
    
    /** The tier and promo tier flag. */
    private boolean tierAndPromoTierFlag;;
    
    /** The zonal standard and promo flag. */
    private boolean zonalStandardAndPromoFlag;
    
    /** The report url. */
    private String reportURL;
    
    /** The v cm supplier item. */
    private VCmSupplierItem vCmSupplierItem;
    
    /** The auto update. */
    private boolean autoUpdate = Boolean.TRUE;
    
    /** The action. */
    private String action;
    
    /** The purchase in default id. */
    private Long purchaseInDefaultId;
    
    /** The view only. */
    private boolean viewOnly = false;
    
    /** The back to previous page. */
    private String backToPreviousPage;
    
    /** The end loa token. */
    private Object endLoaToken;
    
	/** The end loa task id. */
	private String endLoaTaskId;
	
	/** The end loa outcome. */
	private String endLoaOutcome;
	
	/** The end loa rn. */
	private ScRequestNote endLoaRN;
	
	/** The proc title. */
	private String procTitle;
	
	/** The Proc type category id. */
	private Long procTypeCategoryId;
	
	/** The is exceeded amount alerted. */
	private boolean isExceededAmountAlerted = Boolean.FALSE;
	
	/** The prompt message. */
	private String promptMessage;
	
	/** The approver List by group */
	private List<PmUser> approverList;
	
	/** The user group list. */
	private List<PmUserGroup> userGroupList;
	
	/** The selected approver id. */
	private Long selectedApproverId;
	
	/** The user group id. */
	private Long userGroupId;
	
	/** The back up approvers */
	private List<PmUser> backupApprovers;
	
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	/** The task expiry date */
	private Calendar taskExpiryDate;
	
	/** The pid verifiers and approvers */
	private String pidApprovers;
	
	/** The pid no for request note */
	private String pidNo;
	
    /**
     * Instantiates a new request note managed bean.
     */
    public RequestNoteManagedBean() {
    	PmParameter pmParameter = new PmParameter();
		requestNoteDtl.setProcurementType(pmParameter);
		requestNoteDtl.setFulfilmentType(pmParameter);
		requestNoteDtl.setPriceType(pmParameter);
    	ScRequestItem requestItem = new ScRequestItem();
    	ScSupplierItemPrice itemPrice = new ScSupplierItemPrice();
    	itemPrice.setPriceType(pmParameter);
    	requestSupplierItem.setRequestNoteItem(requestItem);
    	requestSupplierItem.setSupplierItemPrice(itemPrice);
	}
	
	/**
	 * Gets the request note.
	 *
	 * @return the requestNote
	 */
	public ScRequestNote getRequestNote() {
		return requestNote;
	}
	
	/**
	 * Sets the request note.
	 *
	 * @param requestNote the requestNote to set
	 */
	public void setRequestNote(ScRequestNote requestNote) {
		this.requestNote = requestNote;
	}
	
	/**
	 * Gets the request supplier items.
	 *
	 * @return the requestSupplierItems
	 */
	public List<ScRequestSupplierItem> getRequestSupplierItems() {
		return requestSupplierItems;
	}
	
	/**
	 * Sets the request supplier items.
	 *
	 * @param requestSupplierItems the requestSupplierItems to set
	 */
	public void setRequestSupplierItems(
			List<ScRequestSupplierItem> requestSupplierItems) {
		this.requestSupplierItems = requestSupplierItems;
	}
	
	/**
	 * Gets the request note dtls.
	 *
	 * @return the requestNoteDtls
	 */
	public List<ScRequestNoteDtl> getRequestNoteDtls() {
		return requestNoteDtls;
	}
	
	/**
	 * Sets the request note dtls.
	 *
	 * @param requestNoteDtls the requestNoteDtls to set
	 */
	public void setRequestNoteDtls(List<ScRequestNoteDtl> requestNoteDtls) {
		this.requestNoteDtls = requestNoteDtls;
	}
	
	/**
	 * Gets the selected remarks.
	 *
	 * @return the selectedRemarks
	 */
	public List<ScRequestNoteRemark> getSelectedRemarks() {
		return selectedRemarks;
	}
	
	/**
	 * Sets the selected remarks.
	 *
	 * @param selectedRemarks the selectedRemarks to set
	 */
	public void setSelectedRemarks(List<ScRequestNoteRemark> selectedRemarks) {
		this.selectedRemarks = selectedRemarks;
	}
	
	/**
	 * Gets the approval users.
	 *
	 * @return the approvalUsers
	 */
	public DualListModel<PmUser> getApprovalUsers() {
		return approvalUsers;
	}

	/**
	 * Gets the fulfillment type.
	 *
	 * @return the fulfillmentType
	 */
	public String getFulfillmentType() {
		return fulfillmentType;
	}
	
	/**
	 * Sets the fulfillment type.
	 *
	 * @param fulfillmentType the fulfillmentType to set
	 */
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}
	
	/**
	 * Gets the supplier id.
	 *
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplierId;
	}
	
	/**
	 * Sets the supplier id.
	 *
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	
	/**
	 * Gets the sm supplier.
	 *
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}
	
	/**
	 * Sets the sm supplier.
	 *
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}
	
	/**
	 * Gets the assigned to user id.
	 *
	 * @return the assignedToUserId
	 */
	public Long getAssignedToUserId() {
		return assignedToUserId;
	}
	
	/**
	 * Sets the assigned to user id.
	 *
	 * @param assignedToUserId the assignedToUserId to set
	 */
	public void setAssignedToUserId(Long assignedToUserId) {
		this.assignedToUserId = assignedToUserId;
	}
	
	/**
	 * Gets the period type.
	 *
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}
	
	/**
	 * Sets the period type.
	 *
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	/**
	 * Gets the period value.
	 *
	 * @return the periodValue
	 */
	public Integer getPeriodValue() {
		return periodValue;
	}
	
	/**
	 * Sets the period value.
	 *
	 * @param periodValue the periodValue to set
	 */
	public void setPeriodValue(Integer periodValue) {
		this.periodValue = periodValue;
	}

	/**
	 * Gets the user division list.
	 *
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}
	
	/**
	 * Sets the user division list.
	 *
	 * @param userDivisionList the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}
	
	/**
	 * Gets the request supplier item.
	 *
	 * @return the requestSupplierItem
	 */
	public ScRequestSupplierItem getRequestSupplierItem() {
		return requestSupplierItem;
	}
	
	/**
	 * Sets the request supplier item.
	 *
	 * @param requestSupplierItem the requestSupplierItem to set
	 */
	public void setRequestSupplierItem(ScRequestSupplierItem requestSupplierItem) {
		this.requestSupplierItem = requestSupplierItem;
	}
	
	/**
	 * Gets the request note dtl.
	 *
	 * @return the requestNoteDtl
	 */
	public ScRequestNoteDtl getRequestNoteDtl() {
		return requestNoteDtl;
	}
	
	/**
	 * Sets the request note dtl.
	 *
	 * @param requestNoteDtl the requestNoteDtl to set
	 */
	public void setRequestNoteDtl(ScRequestNoteDtl requestNoteDtl) {
		this.requestNoteDtl = requestNoteDtl;
	}
	
	/**
	 * Checks if is just type option.
	 *
	 * @return the justTypeOption
	 */
	public boolean isJustTypeOption() {
		return justTypeOption;
	}
	
	/**
	 * Sets the just type option.
	 *
	 * @param justTypeOption the justTypeOption to set
	 */
	public void setJustTypeOption(boolean justTypeOption) {
		this.justTypeOption = justTypeOption;
	}
	
	/**
	 * Checks if is proc type option.
	 *
	 * @return the procTypeOption
	 */
	public boolean isProcTypeOption() {
		return procTypeOption;
	}
	
	/**
	 * Sets the proc type option.
	 *
	 * @param procTypeOption the procTypeOption to set
	 */
	public void setProcTypeOption(boolean procTypeOption) {
		this.procTypeOption = procTypeOption;
	}
	
	/**
	 * Gets the just dropdown.
	 *
	 * @return the justDropdown
	 */
	public SelectItem[] getJustDropdown() {
		return justDropdown;
	}
	
	/**
	 * Sets the just dropdown.
	 *
	 * @param justDropdown the justDropdown to set
	 */
	public void setJustDropdown(SelectItem[] justDropdown) {
		this.justDropdown = justDropdown;
	}
	
	/**
	 * Gets the selected request note dtls.
	 *
	 * @return the selectedRequestNoteDtls
	 */
	public List<ScRequestNoteDtl> getSelectedRequestNoteDtls() {
		return selectedRequestNoteDtls;
	}
	
	/**
	 * Sets the selected request note dtls.
	 *
	 * @param selectedRequestNoteDtls the selectedRequestNoteDtls to set
	 */
	public void setSelectedRequestNoteDtls(
			List<ScRequestNoteDtl> selectedRequestNoteDtls) {
		this.selectedRequestNoteDtls = selectedRequestNoteDtls;
	}
	
	/**
	 * Gets the user list.
	 *
	 * @return the userList
	 */
	public List<PmUser> getUserList() {
		return userList;
	}
	
	/**
	 * Sets the user list.
	 *
	 * @param userList the userList to set
	 */
	public void setUserList(List<PmUser> userList) {
		this.userList = userList;
	}
	
	/**
	 * Gets the price types.
	 *
	 * @return the priceTypes
	 */
	public SelectItem[] getPriceTypes() {
		return priceTypes;
	}
	
	/**
	 * Sets the price types.
	 *
	 * @param priceTypes the priceTypes to set
	 */
	public void setPriceTypes(SelectItem[] priceTypes) {
		this.priceTypes = priceTypes;
	}

	/**
	 * Sets the approval users.
	 *
	 * @param approvalUsers the approvalUsers to set
	 */
	public void setApprovalUsers(DualListModel<PmUser> approvalUsers) {
		this.approvalUsers = approvalUsers;
	}

	/**
	 * Gets the source method.
	 *
	 * @return the sourceMethod
	 */
	public String getSourceMethod() {
		return sourceMethod;
	}

	/**
	 * Sets the source method.
	 *
	 * @param sourceMethod the sourceMethod to set
	 */
	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	/**
	 * Gets the price type code.
	 *
	 * @return the priceTypeCode
	 */
	public String getPriceTypeCode() {
		return priceTypeCode;
	}

	/**
	 * Sets the price type code.
	 *
	 * @param priceTypeCode the priceTypeCode to set
	 */
	public void setPriceTypeCode(String priceTypeCode) {
		this.priceTypeCode = priceTypeCode;
	}
	
	/**
	 * Checks if is item check.
	 *
	 * @return the itemCheck
	 */
	public boolean isItemCheck() {
		return itemCheck;
	}
	
	/**
	 * Sets the item check.
	 *
	 * @param itemCheck the itemCheck to set
	 */
	public void setItemCheck(boolean itemCheck) {
		this.itemCheck = itemCheck;
	}
	
	/**
	 * Gets the request note status.
	 *
	 * @return the requestNoteStatus
	 */
	public PmStatus getRequestNoteStatus() {
		return requestNoteStatus;
	}
	
	/**
	 * Sets the request note status.
	 *
	 * @param requestNoteStatus the requestNoteStatus to set
	 */
	public void setRequestNoteStatus(PmStatus requestNoteStatus) {
		this.requestNoteStatus = requestNoteStatus;
	}
	
	/**
	 * Checks if is pr inquiry.
	 *
	 * @return true, if is pr inquiry
	 */
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
	
	/**
	 * Gets the request note items.
	 *
	 * @return the requestNoteItems
	 */
	public List<ScRequestItem> getRequestNoteItems() {
		return requestNoteItems;
	}
	
	/**
	 * Sets the request note items.
	 *
	 * @param requestNoteItems the requestNoteItems to set
	 */
	public void setRequestNoteItems(List<ScRequestItem> requestNoteItems) {
		this.requestNoteItems = requestNoteItems;
	}
	
	/**
	 * Checks if is required.
	 *
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
	
	/**
	 * Sets the required.
	 *
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	/**
	 * Gets the justification.
	 *
	 * @return the justification
	 */
	public String getJustification() {
		return justification;
	}
	
	/**
	 * Sets the justification.
	 *
	 * @param justification the justification to set
	 */
	public void setJustification(String justification) {
		this.justification = justification;
	}
	
	/**
	 * Gets the justification type.
	 *
	 * @return the justificationType
	 */
	public String getJustificationType() {
		return justificationType;
	}
	
	/**
	 * Sets the justification type.
	 *
	 * @param justificationType the justificationType to set
	 */
	public void setJustificationType(String justificationType) {
		this.justificationType = justificationType;
	}
	
	/**
	 * Gets the procurement type.
	 *
	 * @return the procurementType
	 */
	public String getProcurementType() {
		return procurementType;
	}
	
	/**
	 * Sets the procurement type.
	 *
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}
	
	/**
	 * Gets the price type.
	 *
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}
	
	/**
	 * Sets the price type.
	 *
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * Gets the search req note no.
	 *
	 * @return the searchReqNoteNo
	 */
	public String getSearchReqNoteNo() {
		return searchReqNoteNo;
	}
	
	/**
	 * Sets the search req note no.
	 *
	 * @param searchReqNoteNo the searchReqNoteNo to set
	 */
	public void setSearchReqNoteNo(String searchReqNoteNo) {
		this.searchReqNoteNo = searchReqNoteNo;
	}
	
	/**
	 * Gets the search src method.
	 *
	 * @return the searchSrcMethod
	 */
	public String getSearchSrcMethod() {
		return searchSrcMethod;
	}
	
	/**
	 * Sets the search src method.
	 *
	 * @param searchSrcMethod the searchSrcMethod to set
	 */
	public void setSearchSrcMethod(String searchSrcMethod) {
		this.searchSrcMethod = searchSrcMethod;
	}
	
	/**
	 * Gets the search created date from.
	 *
	 * @return the searchCreatedDateFrom
	 */
	public Date getSearchCreatedDateFrom() {
		return searchCreatedDateFrom;
	}
	
	/**
	 * Sets the search created date from.
	 *
	 * @param searchCreatedDateFrom the searchCreatedDateFrom to set
	 */
	public void setSearchCreatedDateFrom(Date searchCreatedDateFrom) {
		this.searchCreatedDateFrom = searchCreatedDateFrom;
	}
	
	/**
	 * Gets the search created date to.
	 *
	 * @return the searchCreatedDateTo
	 */
	public Date getSearchCreatedDateTo() {
		return searchCreatedDateTo;
	}
	
	/**
	 * Sets the search created date to.
	 *
	 * @param searchCreatedDateTo the searchCreatedDateTo to set
	 */
	public void setSearchCreatedDateTo(Date searchCreatedDateTo) {
		this.searchCreatedDateTo = searchCreatedDateTo;
	}
	
	/**
	 * @return the searchStatus
	 */
	public Long getSearchStatus() {
		return searchStatus;
	}

	/**
	 * @param searchStatus the searchStatus to set
	 */
	public void setSearchStatus(Long searchStatus) {
		this.searchStatus = searchStatus;
	}

	/**
	 * Gets the status descs.
	 *
	 * @return the statusDescs
	 */
	public List<PmStatusDesc> getStatusDescs() {
		return statusDescs;
	}
	
	/**
	 * Sets the status descs.
	 *
	 * @param statusDescs the statusDescs to set
	 */
	public void setStatusDescs(List<PmStatusDesc> statusDescs) {
		this.statusDescs = statusDescs;
	}
	
	/**
	 * Gets the req note data model.
	 *
	 * @return the reqNoteDataModel
	 */
	public IPagingDataModel<ScRequestNote> getReqNoteDataModel() {
		return reqNoteDataModel;
	}
	
	/**
	 * Sets the req note data model.
	 *
	 * @param reqNoteDataModel the reqNoteDataModel to set
	 */
	public void setReqNoteDataModel(IPagingDataModel<ScRequestNote> reqNoteDataModel) {
		this.reqNoteDataModel = reqNoteDataModel;
	}

	/**
	 * The Class RequestNotePagingDataModel.
	 */
	private class RequestNotePagingDataModel extends PagingDataModel<ScRequestNote> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new request note paging data model.
		 */
		public RequestNotePagingDataModel() {
			super("reqNoteTbl");
		}

		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScRequestNote> pageChangeListener(ISearchBean<ScRequestNote> searchBean) {
			IDirectPurchaseSO directPurchaseSO;
			try {
				directPurchaseSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IDirectPurchaseSO.class);
				
				if(StringUtils.isNotBlank(getSearchReqNoteNo())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("requestNoteNo",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchReqNoteNo()));
				}
				
				if(StringUtils.isNotBlank(getSearchSrcMethod())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("sourceMethod",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchSrcMethod()));
				}
				
				if(getSearchStatus() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("workFlowStatuses.pmStatus.statusId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchStatus()));
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("workFlowStatuses.current",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));
				}
				
				if(getSearchCreatedDateFrom() != null && getSearchCreatedDateTo() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("createdDate",
									SearchOperator.OPERATOR_BETWEEN.value(),
									new SearchBetweenValue<Date>(
											getSearchCreatedDateFrom(), 
											getSearchCreatedDateTo())));
				}
				
				RequestNoteReqDTO reqDTO = new RequestNoteReqDTO();
				reqDTO.setRequestNoteSearchBean(searchBean);
				
				RequestNoteResDTO resDTO = directPurchaseSO.searchRequestNoteList(reqDTO);
				
				if(resDTO.isSuccessful()) {
					return resDTO.getRequestNoteResultBean();
				}
			} finally {
				directPurchaseSO = null;
			}
			
			return null;
		}
	}

	/**
	 * Checks if is just required.
	 *
	 * @return the justRequired
	 */
	public boolean isJustRequired() {
		return justRequired;
	}
	
	/**
	 * Sets the just required.
	 *
	 * @param justRequired the justRequired to set
	 */
	public void setJustRequired(boolean justRequired) {
		this.justRequired = justRequired;
	}
	
	/**
	 * Gets the procurement types.
	 *
	 * @return the procurementTypes
	 */
	public SelectItem[] getProcurementTypes() {
		return procurementTypes;
	}
	
	/**
	 * Sets the procurement types.
	 *
	 * @param procurementTypes the procurementTypes to set
	 */
	public void setProcurementTypes(SelectItem[] procurementTypes) {
		this.procurementTypes = procurementTypes;
	}
	
	/**
	 * Checks if is adds the to cart flag.
	 *
	 * @return the addToCartFlag
	 */
	public boolean isAddToCartFlag() {
		return addToCartFlag;
	}
	
	/**
	 * Sets the adds the to cart flag.
	 *
	 * @param addToCartFlag the addToCartFlag to set
	 */
	public void setAddToCartFlag(boolean addToCartFlag) {
		this.addToCartFlag = addToCartFlag;
	}
	
	/**
	 * Checks if is title checker.
	 *
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}
	
	/**
	 * Sets the title checker.
	 *
	 * @param titleChecker the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}
	
	/**
	 * Gets the created by.
	 *
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	
	/**
	 * Sets the created by.
	 *
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	/**
	 * Gets the supplier rec list.
	 *
	 * @return the supplierRecList
	 */
	public List<ScRequestNoteDtl> getSupplierRecList() {
		return supplierRecList;
	}
	
	/**
	 * Sets the supplier rec list.
	 *
	 * @param supplierRecList the supplierRecList to set
	 */
	public void setSupplierRecList(List<ScRequestNoteDtl> supplierRecList) {
		this.supplierRecList = supplierRecList;
	}
	
	/**
	 * Gets the pr inquiry id.
	 *
	 * @return the prInquiryId
	 */
	public Long getPrInquiryId() {
		return prInquiryId;
	}
	
	/**
	 * Sets the pr inquiry id.
	 *
	 * @param prInquiryId the prInquiryId to set
	 */
	public void setPrInquiryId(Long prInquiryId) {
		this.prInquiryId = prInquiryId;
	}
	
	/**
	 * Gets the sc rn zone.
	 *
	 * @return the scRNZone
	 */
	public ScRNZone getScRNZone() {
		return scRNZone;
	}
	
	/**
	 * Sets the sc rn zone.
	 *
	 * @param scRNZone the scRNZone to set
	 */
	public void setScRNZone(ScRNZone scRNZone) {
		this.scRNZone = scRNZone;
	}
	
	/**
	 * Gets the pricing list.
	 *
	 * @return the pricingList
	 */
	public List<ScSupplierItemPrice> getPricingList() {
		return pricingList;
	}
	
	/**
	 * Sets the pricing list.
	 *
	 * @param pricingList the pricingList to set
	 */
	public void setPricingList(List<ScSupplierItemPrice> pricingList) {
		this.pricingList = pricingList;
	}
	
	/**
	 * Gets the compare supplier items.
	 *
	 * @return the compareSupplierItems
	 */
	public List<CmSupplierItem> getCompareSupplierItems() {
		return compareSupplierItems;
	}
	
	/**
	 * Sets the compare supplier items.
	 *
	 * @param compareSupplierItems the compareSupplierItems to set
	 */
	public void setCompareSupplierItems(List<CmSupplierItem> compareSupplierItems) {
		this.compareSupplierItems = compareSupplierItems;
	}
	
	/**
	 * Gets the delete request note dtls.
	 *
	 * @return the deleteRequestNoteDtls
	 */
	public List<ScRequestNoteDtl> getDeleteRequestNoteDtls() {
		return deleteRequestNoteDtls;
	}
	
	/**
	 * Sets the delete request note dtls.
	 *
	 * @param deleteRequestNoteDtls the deleteRequestNoteDtls to set
	 */
	public void setDeleteRequestNoteDtls(
			List<ScRequestNoteDtl> deleteRequestNoteDtls) {
		this.deleteRequestNoteDtls = deleteRequestNoteDtls;
	}
	
	/**
	 * Gets the zone dtl map.
	 *
	 * @return the zoneDtlMap
	 */
	public Map<String, TplSpecZoneDtl> getZoneDtlMap() {
		return zoneDtlMap;
	}
	
	/**
	 * Sets the zone dtl map.
	 *
	 * @param zoneDtlMap the zoneDtlMap to set
	 */
	public void setZoneDtlMap(Map<String, TplSpecZoneDtl> zoneDtlMap) {
		this.zoneDtlMap = zoneDtlMap;
	}
	
	/**
	 * Gets the cm supplier item.
	 *
	 * @return the cm supplier item
	 */
	public CmSupplierItem getCmSupplierItem() {
		return cmSupplierItem;
	}
	
	/**
	 * Sets the cm supplier item.
	 *
	 * @param cmSupplierItem the new cm supplier item
	 */
	public void setCmSupplierItem(CmSupplierItem cmSupplierItem) {
		this.cmSupplierItem = cmSupplierItem;
	}
	
	/**
	 * Gets the recent price duration.
	 *
	 * @return the recent price duration
	 */
	public String getRecentPriceDuration() {
		return recentPriceDuration;
	}
	
	/**
	 * Sets the recent price duration.
	 *
	 * @param recentPriceDuration the new recent price duration
	 */
	public void setRecentPriceDuration(String recentPriceDuration) {
		this.recentPriceDuration = recentPriceDuration;
	}
	
	/**
	 * Gets the report url.
	 *
	 * @return the report url
	 */
	public String getReportURL() {
		return reportURL;
	}
	
	/**
	 * Sets the report url.
	 *
	 * @param reportURL the new report url
	 */
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
	/**
	 * Checks if is tier and promo tier flag.
	 *
	 * @return true, if is tier and promo tier flag
	 */
	public boolean isTierAndPromoTierFlag() {
		return tierAndPromoTierFlag;
	}
	
	/**
	 * Sets the tier and promo tier flag.
	 *
	 * @param tierAndPromoTierFlag the new tier and promo tier flag
	 */
	public void setTierAndPromoTierFlag(boolean tierAndPromoTierFlag) {
		this.tierAndPromoTierFlag = tierAndPromoTierFlag;
	}
	
	/**
	 * Checks if is zonal standard and promo flag.
	 *
	 * @return true, if is zonal standard and promo flag
	 */
	public boolean isZonalStandardAndPromoFlag() {
		return zonalStandardAndPromoFlag;
	}
	
	/**
	 * Sets the zonal standard and promo flag.
	 *
	 * @param zonalStandardAndPromoFlag the new zonal standard and promo flag
	 */
	public void setZonalStandardAndPromoFlag(boolean zonalStandardAndPromoFlag) {
		this.zonalStandardAndPromoFlag = zonalStandardAndPromoFlag;
	}
	
	/**
	 * Gets the v cm supplier item.
	 *
	 * @return the v cm supplier item
	 */
	public VCmSupplierItem getvCmSupplierItem() {
		return vCmSupplierItem;
	}
	
	/**
	 * Sets the v cm supplier item.
	 *
	 * @param vCmSupplierItem the new v cm supplier item
	 */
	public void setvCmSupplierItem(VCmSupplierItem vCmSupplierItem) {
		this.vCmSupplierItem = vCmSupplierItem;
	}
	
	/**
	 * Checks if is auto update.
	 *
	 * @return true, if is auto update
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	/**
	 * Sets the auto update.
	 *
	 * @param autoUpdate the new auto update
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Gets the purchase in default id.
	 *
	 * @return the purchase in default id
	 */
	public Long getPurchaseInDefaultId() {
		return purchaseInDefaultId;
	}
	
	/**
	 * Sets the purchase in default id.
	 *
	 * @param purchaseInDefaultId the new purchase in default id
	 */
	public void setPurchaseInDefaultId(Long purchaseInDefaultId) {
		this.purchaseInDefaultId = purchaseInDefaultId;
	}
	
	/**
	 * Checks if is view only.
	 *
	 * @return true, if is view only
	 */
	public boolean isViewOnly() {
		return viewOnly;
	}
	
	/**
	 * Sets the view only.
	 *
	 * @param viewOnly the new view only
	 */
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	
	/**
	 * Gets the back to previous page.
	 *
	 * @return the back to previous page
	 */
	public String getBackToPreviousPage() {
		return backToPreviousPage;
	}
	
	/**
	 * Sets the back to previous page.
	 *
	 * @param backToPreviousPage the new back to previous page
	 */
	public void setBackToPreviousPage(String backToPreviousPage) {
		this.backToPreviousPage = backToPreviousPage;
	}
	/**
	 * Gets the end loa token.
	 *
	 * @return the end loa token
	 */
	public Object getEndLoaToken() {
		return endLoaToken;
	}

	/**
	 * Sets the end loa token.
	 *
	 * @param endLoaToken the new end loa token
	 */
	public void setEndLoaToken(Object endLoaToken) {
		this.endLoaToken = endLoaToken;
	}

	/**
	 * Gets the end loa task id.
	 *
	 * @return the end loa task id
	 */
	public String getEndLoaTaskId() {
		return endLoaTaskId;
	}

	/**
	 * Sets the end loa task id.
	 *
	 * @param endLoaTaskId the new end loa task id
	 */
	public void setEndLoaTaskId(String endLoaTaskId) {
		this.endLoaTaskId = endLoaTaskId;
	}

	/**
	 * Gets the end loa outcome.
	 *
	 * @return the end loa outcome
	 */
	public String getEndLoaOutcome() {
		return endLoaOutcome;
	}

	/**
	 * Sets the end loa outcome.
	 *
	 * @param endLoaOutcome the new end loa outcome
	 */
	public void setEndLoaOutcome(String endLoaOutcome) {
		this.endLoaOutcome = endLoaOutcome;
	}
	
	/**
	 * Gets the end loa rn.
	 *
	 * @return the endLoaRN
	 */
	public ScRequestNote getEndLoaRN() {
		return endLoaRN;
	}

	/**
	 * Sets the end loa rn.
	 *
	 * @param endLoaRN the endLoaRN to set
	 */
	public void setEndLoaRN(ScRequestNote endLoaRN) {
		this.endLoaRN = endLoaRN;
	}

	/**
	 * @return the procTitle
	 */
	public String getProcTitle() {
		return procTitle;
	}

	/**
	 * @param procTitle the procTitle to set
	 */
	public void setProcTitle(String procTitle) {
		this.procTitle = procTitle;
	}

	/**
	 * @return the procTypeCategoryId
	 */
	public Long getProcTypeCategoryId() {
		return procTypeCategoryId;
	}

	/**
	 * @param procTypeCategoryId the procTypeCategoryId to set
	 */
	public void setProcTypeCategoryId(Long procTypeCategoryId) {
		this.procTypeCategoryId = procTypeCategoryId;
	}

	/**
	 * @return the itemSearchBackBtn
	 */
	public boolean isItemSearchBackBtn() {
		return itemSearchBackBtn;
	}

	/**
	 * @param itemSearchBackBtn the itemSearchBackBtn to set
	 */
	public void setItemSearchBackBtn(boolean itemSearchBackBtn) {
		this.itemSearchBackBtn = itemSearchBackBtn;
	}

	/**
	 * @return the isExceededAmountAlerted
	 */
	public boolean isExceededAmountAlerted() {
		return isExceededAmountAlerted;
	}

	/**
	 * @param isExceededAmountAlerted the isExceededAmountAlerted to set
	 */
	public void setExceededAmountAlerted(boolean isExceededAmountAlerted) {
		this.isExceededAmountAlerted = isExceededAmountAlerted;
	}

	/**
	 * @return the promptMessage
	 */
	public String getPromptMessage() {
		return promptMessage;
	}

	/**
	 * @param promptMessage the promptMessage to set
	 */
	public void setPromptMessage(String promptMessage) {
		this.promptMessage = promptMessage;
	}

	/**
	 * @return the approverList
	 */
	public List<PmUser> getApproverList() {
		if(approverList == null){
			setApproverList(new ArrayList<PmUser>());
		}
		return approverList;
	}

	/**
	 * @param arrayList the approverList to set
	 */
	public void setApproverList(List<PmUser> arrayList) {
		this.approverList = arrayList;
	}

	/**
	 * @return the userGroupList
	 */
	public List<PmUserGroup> getUserGroupList() {
		return userGroupList;
	}

	/**
	 * @param userGroupList the userGroupList to set
	 */
	public void setUserGroupList(List<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}
	
	/**
	 * @return the backupApprovers
	 */
	public List<PmUser> getBackupApprovers() {
		if(backupApprovers == null) {
			setBackupApprovers(new ArrayList<PmUser>());
		}
		return backupApprovers;
	}

	/**
	 * @param backupApprovers the backupApprovers to set
	 */
	public void setBackupApprovers(List<PmUser> backupApprovers) {
		this.backupApprovers = backupApprovers;
	}

	/**
	 * @return the userGroupId
	 */
	public Long getUserGroupId() {
		return userGroupId;
	}

	/**
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * @return the selectedApproverId
	 */
	public Long getSelectedApproverId() {
		return selectedApproverId;
	}

	/**
	 * @param selectedApproverId the selectedApproverId to set
	 */
	public void setSelectedApproverId(Long selectedApproverId) {
		this.selectedApproverId = selectedApproverId;
	}

	/**
	 * @return the dpObj
	 */
	public Object[] getDpObj() {
		return dpObj;
	}

	/**
	 * @param dpObj the dpObj to set
	 */
	public void setDpObj(Object[] dpObj) {
		this.dpObj = dpObj;
	}

	/**
	 * @return the trackingDairyView
	 */
	public boolean isTrackingDairyView() {
		return trackingDairyView;
	}

	/**
	 * @param trackingDairyView the trackingDairyView to set
	 */
	public void setTrackingDairyView(boolean trackingDairyView) {
		this.trackingDairyView = trackingDairyView;
	}

	/**
	 * @return the taskExpiryDate
	 */
	public Calendar getTaskExpiryDate() {
		return taskExpiryDate;
	}

	/**
	 * @param taskExpiryDate the taskExpiryDate to set
	 */
	public void setTaskExpiryDate(Calendar taskExpiryDate) {
		this.taskExpiryDate = taskExpiryDate;
	}

	/**
	 * @return the pidApprovers
	 */
	public String getPidApprovers() {
		return pidApprovers;
	}

	/**
	 * @param pidApprovers the pidApprovers to set
	 */
	public void setPidApprovers(String pidApprovers) {
		this.pidApprovers = pidApprovers;
	}

	/**
	 * @return the pidNo
	 */
	public String getPidNo() {
		return pidNo;
	}

	/**
	 * @param pidNo the pidNo to set
	 */
	public void setPidNo(String pidNo) {
		this.pidNo = pidNo;
	}
	
}
