package my.ep.web.managedbean.sc.requestnote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.constants.SpecialCharacter;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sc.directpurchase.PurchaseRequestResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.cm.catalog.CmPricing;
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
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DualListModel;

/**
 * The Class ApprovalExceedThresholdManagedBean.
 *
 * @author Zairul
 * @since 28/04/2025
 */
@ManagedBean(name="approvalExceedThresholdMB")
@SessionScoped
public class ApprovalExceedThresholdManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	private LogKey logKey = new LogKey();
	
	/** The search document type. */
	private String searchDocumentType; 
	
	/** The search request note no. */
	private String requestNoteNo;
	
	/** The search request note title. */
	private String requestNoteTitle;
	
	/** The search status id. */
	private Long searchStatusId;
		
	/** The dp document data model. */
	private transient IPagingDataModel<Object[]> dpDocumentDataModel = new DocumentPagingDataModel();
	
	/** The document types. */
	private List<SelectItem> documentTypes;
	
	
	/** The search ministry profile. */
	private PmOrgProfile searchMinistryProfile;
	
	/** The search ptj group profile. */
	private PmOrgProfile searchPtjGroupProfile;
	
	/** The search ptj profile. */
	private PmOrgProfile searchPtjProfile;
	
	/** The document type desc. */
	private String documentTypeDesc;
	
	/** The is government. */
	private boolean government;
	
	private List<String> loaList;
	
	private boolean searchLoaByRN;
	
	private String searchFunctionType;
	
	private boolean isG2G;
	
	private boolean viewAsPTJ;
	
	private boolean viewAsSupplier;
	
	private boolean disableMinistryPtjLov = false;
	
	private boolean showMinistryPtjField = true;
	
	/** The source method. */
	private String sourceMethod = ParameterConst.METHOD_SC;
	
	/** The request note. */
	private	ScRequestNote	requestNote = new ScRequestNote();
	
	/** The request supplier items. */
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<>();
	
	/** The selected remarks. */
	private	List<ScRequestNoteRemark>	selectedRemarks	=	new	ArrayList<>();
	
	/** The request note dtls. */
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<>();
	
	/** The selected request note dtls. */
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<>();
	
	/** The request note items. */
	private List<ScRequestItem>	requestNoteItems = new ArrayList<>();
	
	/** The approval users. */
	private	DualListModel<PmUser>	approvalUsers = new DualListModel<>();
	
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
    
    /** The original request note dtl before selected */
    private ScRequestNoteDtl oriSequestNoteDtl = new ScRequestNoteDtl();
    
    /** The prepared bys. */
    private List<PmUser> preparedBys = new ArrayList<>();
    
    /** The price type code. */
    private String priceTypeCode;
    
    /** The just type option. */
    private boolean justTypeOption;
    
    /** The proc type option. */
    private boolean procTypeOption;
    
    /** The item check. */
    private boolean itemCheck;
    
    /** The just dropdown. */
    private SelectItem[] justDropdown;

    /** The price types. */
    private SelectItem[] priceTypes;
    
    /** The procurement types. */
    private SelectItem[] procurementTypes;
    
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
    private Long prInquiryId;
    
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
	private	List<ScRequestNoteDtl>	supplierRecList	=	new ArrayList<>();
	
	/** The pricing list. */
	private	List<ScSupplierItemPrice>	pricingList	=	new ArrayList<>();
    
    /** The compare supplier items. */
    private List<CmSupplierItem> compareSupplierItems = new ArrayList<>();
    
 
    /** The item search back btn. */
    private boolean itemSearchBackBtn = Boolean.TRUE;
    
    /** The delete request note dtls. */
    private	List<ScRequestNoteDtl>	deleteRequestNoteDtls	=	new ArrayList<>();
    
    /** The zone dtl map. */
    private	Map<String, TplSpecZoneDtl>	zoneDtlMap;
    
    /** The cm supplier item. */
    private CmSupplierItem cmSupplierItem;
    
    /** The recent price duration. */
    private String recentPriceDuration = ParameterConst.RECENT_PRICE_DURATION;
    
    /** The tier and promo tier flag. */
    private boolean tierAndPromoTierFlag;
    
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
	
	/** The recommended supplier id. */
	private Long recommendSupplierId;
	
	/** The is exceeded amount alerted. */
	private boolean isExceededAmountAlerted = Boolean.FALSE;
	
	/** The is exceeded amount message prompt. */
	private boolean isExceededMessagePrompt = Boolean.FALSE;
	
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
	
	
	/** The tracking dairy view. */
	private boolean trackingDairyView;
	
	/** The task expiry date */
	private Calendar taskExpiryDate;
	
	/** The pid verifiers and approvers */
	private String pidApprovers;
	
	/** The pid no for request note */
	private String pidNo;
	
	/** The ful fillment type. */
	private boolean fulFillmentType;
	
	/** The Clone and Re-Quote btn. */
    private boolean cloneReQuote = Boolean.FALSE;	
    	
	private boolean cancel = Boolean.FALSE; 
	
	/** The SQ end date */
	private Calendar sqEndDate;
	
	private String selectedCondition;
	
	private boolean pidValidityExpired = false;
	
	private String docStatus = "";
	
	private String normalAndTierPriceStr = StringUtils.EMPTY;
	
	private List<CmPricing> completeCmPricingList;
	
	private List<CmPricing> filteredCmPricingList;
	
	private boolean priceTypeStringFlag = Boolean.FALSE;
	
	private boolean priceTypeTableFlag = Boolean.FALSE;
	
	private boolean fromAndToQuantityFlag = Boolean.FALSE;
	
	private List<String> loginIdAssignees;
	
	private Long purchaseRequestId;
	
	private boolean qtyViewOnly = false;
	
    private boolean purchaseInquiryContinuity = Boolean.FALSE;
    
    private boolean disablePtjGroupLov = Boolean.TRUE;

    private boolean disablePtjLov = Boolean.TRUE;
    
    private PurchaseRequestResDTO purchaseRequestResDTO;
    
    /** The continuity page session */
    private String continuityPageSession = "";
    
    /** The pid flag */
    private boolean showPurchaseInDefault = Boolean.FALSE;
    
    /** The request note dtl. */
    private ScRequestNoteDtl scRequestNoteDtl = new ScRequestNoteDtl();
    
    /** The new sourcing dp bpm version flag */
    private boolean sourcingDpNewBpmVer = Boolean.FALSE;
    
    private Integer counter = 0;
    
    /**
     * Instantiates a new request note managed bean.
     */
    public ApprovalExceedThresholdManagedBean() {
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
	@Override
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}
	
	/**
	 * Sets the sm supplier.
	 *
	 * @param smSupplier the smSupplier to set
	 */
	@Override
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
	 * @return the oriSequestNoteDtl
	 */
	public ScRequestNoteDtl getOriSequestNoteDtl() {
		return oriSequestNoteDtl;
	}

	/**
	 * @param oriSequestNoteDtl the oriSequestNoteDtl to set
	 */
	public void setOriSequestNoteDtl(ScRequestNoteDtl oriSequestNoteDtl) {
		this.oriSequestNoteDtl = oriSequestNoteDtl;
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
	 * @return the preparedBys
	 */
	public List<PmUser> getPreparedBys() {
		return preparedBys;
	}

	/**
	 * @param preparedBys the preparedBys to set
	 */
	public void setPreparedBys(List<PmUser> preparedBys) {
		this.preparedBys = preparedBys;
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
	public boolean isPrInquiry() {
	    if (requestNote == null || isEmpty(requestNote.getRequestNoteItems())) {
	        return false;
	    }

	    for (ScRequestItem requestNoteItem : requestNote.getRequestNoteItems()) {
	        if (hasInquiry(requestNoteItem)) {
	            return true;
	        }
	    }

	    return false;
	}

	/**
	 * Checks if any supplier item under the request note item has PR inquiry.
	 */
	private boolean hasInquiry(ScRequestItem requestNoteItem) {
	    List<ScRequestSupplierItem> supplierItems = requestNoteItem.getSupplierItems();
	    if (isEmpty(supplierItems)) {
	        return false;
	    }

	    for (ScRequestSupplierItem supplierItem : supplierItems) {
	        if (supplierItem.getScPrInquiy() != null) {
	            return true;
	        }
	    }

	    return false;
	}

	/**
	 * Utility method to check for null or empty list.
	 */
	private boolean isEmpty(List<?> list) {
	    return list == null || list.isEmpty();
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
	@Override
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	/**
	 * Sets the auto update.
	 *
	 * @param autoUpdate the new auto update
	 */
	@Override
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
	 * @return the recommendSupplierId
	 */
	public Long getRecommendSupplierId() {
		return recommendSupplierId;
	}

	/**
	 * @param recommendSupplierId the recommendSupplierId to set
	 */
	public void setRecommendSupplierId(Long recommendSupplierId) {
		this.recommendSupplierId = recommendSupplierId;
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
	 * @return the isExceededMessagePrompt
	 */
	public boolean isExceededMessagePrompt() {
		return isExceededMessagePrompt;
	}

	/**
	 * @param isExceededMessagePrompt the isExceededMessagePrompt to set
	 */
	public void setExceededMessagePrompt(boolean isExceededMessagePrompt) {
		this.isExceededMessagePrompt = isExceededMessagePrompt;
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

	/**
	 * @return the fulFillmentType
	 */
	public boolean isFulFillmentType() {
		return fulFillmentType;
	}

	/**
	 * @param fulFillmentType the fulFillmentType to set
	 */
	public void setFulFillmentType(boolean fulFillmentType) {
		this.fulFillmentType = fulFillmentType;
	}

	/**
	 * @return the cloneReQuote
	 */
	public boolean isCloneReQuote() {
		return cloneReQuote;
	}

	/**
	 * @param cloneReQuote the cloneReQuote to set
	 */
	public void setCloneReQuote(boolean cloneReQuote) {
		this.cloneReQuote = cloneReQuote;
	}

	/**
	 * Checks if the operation is cancelled.
	 * 
	 * @return {@code true} if cancelled, {@code false} otherwise.
	 */
	public boolean isCancel() {
		return cancel;
	}

	/**
	 * Sets the cancellation status.
	 * 
	 * @param cancel
	 *            {@code true} to mark as cancelled, {@code false} otherwise.
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}
	
	/**
	 * @return the taskExpiryDate
	 */
	public Calendar getSqEndDate() {
		return sqEndDate;
	}

	/**
	 * @param taskExpiryDate the taskExpiryDate to set
	 */
	public void setSqEndDate(Calendar sqEndDate) {
		this.sqEndDate = sqEndDate;
	}

	/**
	 * Retrieves the currently selected condition.
	 * 
	 * @return The selected condition as a String.
	 */
	public String getSelectedCondition() {
		return selectedCondition;
	}

	/**
	 * Sets the selected condition.
	 * 
	 * @param selectedCondition
	 *            The selected condition to set.
	 */
	public void setSelectedCondition(String selectedCondition) {
		this.selectedCondition = selectedCondition;
	}

	/**
	 * Checks if the PID (Public Identification) validity has expired.
	 * 
	 * @return {@code true} if PID validity has expired, {@code false}
	 *         otherwise.
	 */
	public boolean isPidValidityExpired() {
		return pidValidityExpired;
	}

	/**
	 * Sets whether the PID (Public Identification) validity has expired.
	 * 
	 * @param pidValidityExpired
	 *            {@code true} if PID validity has expired, {@code false}
	 *            otherwise.
	 */
	public void setPidValidityExpired(boolean pidValidityExpired) {
		this.pidValidityExpired = pidValidityExpired;
	}
	
	/**
	 * @return the docStatus
	 */
	public String getDocStatus() {
		return docStatus;
	}

	/**
	 * @param docStatus the docStatus to set
	 */
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	/**
	 * Retrieves the string representation of normal and tier prices.
	 * 
	 * @return The normal and tier prices as a String.
	 */
	public String getNormalAndTierPriceStr() {
		return normalAndTierPriceStr;
	}

	/**
	 * Sets the string representation of normal and tier prices.
	 * 
	 * @param normalAndTierPriceStr
	 *            The string representation of normal and tier prices to set.
	 */
	public void setNormalAndTierPriceStr(String normalAndTierPriceStr) {
		this.normalAndTierPriceStr = normalAndTierPriceStr;
	}

	/**
	 * Retrieves the complete list of commercial pricing.
	 * 
	 * @return A list of {@link CmPricing} objects representing the complete
	 *         pricing list.
	 */
	public List<CmPricing> getCompleteCmPricingList() {
		return completeCmPricingList;
	}

	/**
	 * Sets the complete list of commercial pricing.
	 * 
	 * @param completeCmPricingList
	 *            The complete list of {@link CmPricing} objects to set.
	 */
	public void setCompleteCmPricingList(List<CmPricing> completeCmPricingList) {
		this.completeCmPricingList = completeCmPricingList;
	}

	/**
	 * Retrieves the filtered list of commercial pricing.
	 * 
	 * @return A list of {@link CmPricing} objects representing the filtered
	 *         pricing list.
	 */
	public List<CmPricing> getFilteredCmPricingList() {
		return filteredCmPricingList;
	}

	/**
	 * Sets the filtered list of commercial pricing.
	 * 
	 * @param filteredCmPricingList
	 *            The filtered list of {@link CmPricing} objects to set.
	 */
	public void setFilteredCmPricingList(List<CmPricing> filteredCmPricingList) {
		this.filteredCmPricingList = filteredCmPricingList;
	}

	/**
	 * Checks if the price type is represented as a string.
	 * 
	 * @return {@code true} if the price type is a string, {@code false}
	 *         otherwise.
	 */
	public boolean isPriceTypeStringFlag() {
		return priceTypeStringFlag;
	}

	/**
	 * Sets whether the price type is represented as a string.
	 * 
	 * @param priceTypeStringFlag
	 *            {@code true} if the price type is a string, {@code false}
	 *            otherwise.
	 */
	public void setPriceTypeStringFlag(boolean priceTypeStringFlag) {
		this.priceTypeStringFlag = priceTypeStringFlag;
	}

	/**
	 * Checks if the price type is represented as a table.
	 * 
	 * @return {@code true} if the price type is a table, {@code false}
	 *         otherwise.
	 */
	public boolean isPriceTypeTableFlag() {
		return priceTypeTableFlag;
	}

	/**
	 * Sets whether the price type is represented as a table.
	 * 
	 * @param priceTypeTableFlag
	 *            {@code true} if the price type is a table, {@code false}
	 *            otherwise.
	 */
	public void setPriceTypeTableFlag(boolean priceTypeTableFlag) {
		this.priceTypeTableFlag = priceTypeTableFlag;
	}

	/**
	 * Checks if the pricing uses "from and to" quantity ranges.
	 * 
	 * @return {@code true} if using "from and to" quantity, {@code false}
	 *         otherwise.
	 */
	public boolean isFromAndToQuantityFlag() {
		return fromAndToQuantityFlag;
	}

	/**
	 * Sets whether the pricing uses "from and to" quantity ranges.
	 * 
	 * @param fromAndToQuantityFlag
	 *            {@code true} if using "from and to" quantity, {@code false}
	 *            otherwise.
	 */
	public void setFromAndToQuantityFlag(boolean fromAndToQuantityFlag) {
		this.fromAndToQuantityFlag = fromAndToQuantityFlag;
	}

	/**
	 * Retrieves the list of Login IDs of assignees.
	 * 
	 * @return A list of Login IDs as Strings.
	 */
	public List<String> getLoginIdAssignees() {
		return loginIdAssignees;
	}

	/**
	 * Sets the list of Login IDs of assignees.
	 * 
	 * @param loginIdAssignees
	 *            The list of Login IDs to set.
	 */
	public void setLoginIdAssignees(List<String> loginIdAssignees) {
		this.loginIdAssignees = loginIdAssignees;
	}
	
	/**
	* Gets the purchase request id.
	*
	* @return the purchase request id
	*/
	public Long getPurchaseRequestId() {
		return purchaseRequestId;
	}

	/**
	* Sets the purchase request id.
	*
	* @param purchaseRequestId the new purchase request id
	*/
	public void setPurchaseRequestId(Long purchaseRequestId) {
		this.purchaseRequestId = purchaseRequestId;
	}
	
	/**
	 * Checks if the quantity field is in view-only mode.
	 * 
	 * @return {@code true} if the quantity is view-only, {@code false}
	 *         otherwise.
	 */
	public boolean isQtyViewOnly() {
		return qtyViewOnly;
	}

	/**
	 * Sets whether the quantity field should be in view-only mode.
	 * 
	 * @param qtyViewOnly
	 *            {@code true} to make the quantity view-only, {@code false}
	 *            otherwise.
	 */
	public void setQtyViewOnly(boolean qtyViewOnly) {
		this.qtyViewOnly = qtyViewOnly;
	}

	/**
	 * Checks if is purchase inquiry continuity.
	 *
	 * @return the purchaseInquiryContinuity
	 */
	public boolean isPurchaseInquiryContinuity() {
		return purchaseInquiryContinuity;
	}

	/**
	 * Sets the  purchase inquiry continuity.
	 * 
	 * @param purchaseInquiryContinuity
	 *            the purchaseInquiryContinuity to set
	 */
	public void setPurchaseInquiryContinuity(boolean purchaseInquiryContinuity) {
		this.purchaseInquiryContinuity = purchaseInquiryContinuity;
	}

	/**
	 * Retrieves the Purchase Request Response Data Transfer Object (DTO).
	 * 
	 * @return The {@link PurchaseRequestResDTO} object.
	 */
	public PurchaseRequestResDTO getPurchaseRequestResDTO() {
		return purchaseRequestResDTO;
	}

	/**
	 * Sets the Purchase Request Response Data Transfer Object (DTO).
	 * 
	 * @param purchaseRequestResDTO
	 *            The {@link PurchaseRequestResDTO} object to set.
	 */
	public void setPurchaseRequestResDTO(PurchaseRequestResDTO purchaseRequestResDTO) {
		this.purchaseRequestResDTO = purchaseRequestResDTO;
	}
	
	/**
	 * @return the continuityPageSession
	 */
	public String getContinuityPageSession() {
		return continuityPageSession;
	}

	/**
	* Sets the continuity Page Session.
	*
	* @param continuityPageSession the continuity Page Session
	*/
	public void setContinuityPageSession(String continuityPageSession) {
		this.continuityPageSession = continuityPageSession;
	}
	
	/**
	 * @return the showPurchaseInDefault
	 */
	public boolean isShowPurchaseInDefault() {
		return showPurchaseInDefault;
	}

	/**
	* Sets the purchase in default flag.
	*
	* @param showPurchaseInDefault the purchase in default flag
	*/
	public void setShowPurchaseInDefault(boolean showPurchaseInDefault) {
		this.showPurchaseInDefault = showPurchaseInDefault;
	}

	/**
	 * @return the scRequestNoteDtl
	 */
	public ScRequestNoteDtl getScRequestNoteDtl() {
		return scRequestNoteDtl;
	}

	/**
	* Sets the scRequestNoteDtl.
	*
	* @param scRequestNoteDtl the scRequestNoteDtl to set
	*/
	public void setScRequestNoteDtl(ScRequestNoteDtl scRequestNoteDtl) {
		this.scRequestNoteDtl = scRequestNoteDtl;
	}

	/**
	 * @return the sourcingDpNewVerBpm
	 */
	public boolean isSourcingDpNewBpmVer() {
		return sourcingDpNewBpmVer;
	}

	/**
	* Sets the new sourcingdp bpm version flag.
	*
	* @param sourcingDpNewVerBpm the purchase in default flag
	*/
	public void setSourcingDpNewBpmVer(boolean sourcingDpNewBpmVer) {
		this.sourcingDpNewBpmVer = sourcingDpNewBpmVer;
	}

	/**
	 * The Class DocumentPagingDataModel.
	 */
	private class DocumentPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public DocumentPagingDataModel() {
			super("dpDocumentTableID", false);
		}

		@Override
		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				ISourcingIntegrationSO sourcingIntegrationSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();

				String paramPtj = "pmOrgProfile.orgProfileId";
				String paramTitle ="title";
				String paramPtjGroupAndMinistry = "pmOrgProfile";
				
				searchBean.getDataFilterBeans()
						.add(new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(),
								getSearchDocumentType()));

				if (isGovernment()) {
					if (getSearchPtjProfile() != null && getSearchPtjProfile().getOrgProfileId() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(paramPtj, SearchOperator.OPERATOR_EQUALS.value(),
										getSearchPtjProfile().getOrgProfileId()));

					} else if (getSearchPtjGroupProfile() != null
							&& getSearchPtjGroupProfile().getOrgProfileId() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(
										paramPtjGroupAndMinistry
												+ "."
												+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT
														.value()), SearchOperator.OPERATOR_EQUALS.value(),
										getSearchPtjGroupProfile().getOrgProfileId()));

					} else if (getSearchMinistryProfile() != null
							&& getSearchMinistryProfile().getOrgProfileId() != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(paramPtjGroupAndMinistry + "."
										+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
										SearchOperator.OPERATOR_EQUALS.value(), getSearchMinistryProfile()
												.getOrgProfileId()));
					}
				}

				if (StringUtils.isNotBlank(getRequestNoteTitle())) {
					SearchFilterBean filterBean = new SearchFilterBean(paramTitle,
							SearchOperator.OPERATOR_LIKE.value(), String
									.valueOf(SpecialCharacter.MODULO.getCharacter()).concat(getRequestNoteTitle())
									.concat(String.valueOf(SpecialCharacter.MODULO.getCharacter())));
					searchBean.getDataFilterBeans().add(filterBean);
				}

				if (getRequestNoteNo() != null && getRequestNoteNo() != "") {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("docNo", SearchOperator.OPERATOR_LIKE.value(), String
									.valueOf(SpecialCharacter.MODULO.getCharacter()).concat(getRequestNoteNo())
									.concat(String.valueOf(SpecialCharacter.MODULO.getCharacter()))));
				}
				
				searchBean.getDataFilterBeans().add(
					    new SearchFilterBean("status", SearchOperator.OPERATOR_NOT_IN.value(),
					        Arrays.asList(DPStatusConstants.RN_PENDING_SUBMISSION, DPStatusConstants.RN_PENDING_REVISION)));

				searchBean.getDataFilterBeans().add(new SearchFilterBean("requestNoteDtls.isExceedThreshold", SearchOperator.OPERATOR_IS_NOT_NULL.value(), null));
	            searchBean.getDataFilterBeans().add(new SearchFilterBean("requestNoteDtls.bpkThresholdDecision", SearchOperator.OPERATOR_IS_NULL.value(), null));
	            // --- END NEW FILTER ---

				if (isReqOrApprover() && CollectionUtils.isNotEmpty(searchBean.getDataFilterMetadatas())) {
					searchBean.getDataFilterMetadatas().clear();

				}

				if (!isGovernment()) {
					loadSupplierId(reqDTO);
				}

				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);

				if (null != resDTO) {
					return resDTO.getSourcingDocSearchResultBean();
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError("DP", "ApprovalExceedThresholdManagedBean",
						getRequestNoteNo(), "DocumentPagingDataModel", logKey.getUserId(), e);
			}
			return null;
		}

		private void loadSupplierId(SourcingIntegrationReqDTO reqDTO) {
			// Filter By - supplierId
			ISupplierSearchSO supplierSearchSO = null;
			try {
				supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

				SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
				supplierSearchReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				List<SmSupplier> smSuppliers = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO)
						.getResultBean().getResultList();

				if (CollectionUtils.isNotEmpty(smSuppliers)) {
					List<Long> supplierIds = new ArrayList<>();
					for (SmSupplier supplier : smSuppliers) {
						supplierIds.add(supplier.getSupplierId());
					}
					reqDTO.setSupplierIds(supplierIds);
				}else {
					if (isViewAsSupplier()) {
						List<Long> supplierIds = new ArrayList<>();
						supplierIds.add(supplierSearchReqDTO.getUserId());
						reqDTO.setSupplierIds(supplierIds);
					}
				}

			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError("DP", "ApprovalExceedThresholdManagedBean",
						getRequestNoteNo(), "DocumentPagingDataModel", logKey.getUserId(), e);
			}
		}
	}

	/**
     * Checks if is requisitioner or approver.
     *
     * @return true, if is requisitioner or approver
     */
    public boolean isReqOrApprover() {
    	FacesScopes fc = new FacesScopes();
		return fc.hasRoleUser(RoleCode.REQUISITIONER.value())|| fc.hasRoleUser(RoleCode.RN_APPROVER.value());
	}

	/**
	 * @return the searchStatusId
	 */
	public Long getSearchStatusId() {
		return searchStatusId;
	}

	/**
	 * @param searchStatusId the searchStatusId to set
	 */
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	/**
	 * @return the dpDocumentDataModel
	 */
	public IPagingDataModel<Object[]> getDpDocumentDataModel() {
		return dpDocumentDataModel;
	}

	/**
	 * @param dpDocumentDataModel the dpDocumentDataModel to set
	 */
	public void setDpDocumentDataModel(
			IPagingDataModel<Object[]> dpDocumentDataModel) {
		this.dpDocumentDataModel = dpDocumentDataModel;
	}
	
	/**
	 * Reset document data model.
	 */
	public void resetDocumentDataModel() {
		this.dpDocumentDataModel = new DocumentPagingDataModel();
	}

	/**
	 * Gets the document types.
	 *
	 * @return the document types
	 */
	public List<SelectItem> getDocumentTypes() {
		return documentTypes;
	}

	/**
	 * Sets the document types.
	 *
	 * @param documentTypes the new document types
	 */
	public void setDocumentTypes(List<SelectItem> documentTypes) {
		this.documentTypes = documentTypes;
	}
	
	/**
	 * @return the documentTypeDesc
	 */
	public String getDocumentTypeDesc() {
		return documentTypeDesc;
	}

	/**
	 * @param documentTypeDesc the documentTypeDesc to set
	 */
	public void setDocumentTypeDesc(String documentTypeDesc) {
		this.documentTypeDesc = documentTypeDesc;
	}
	
	/**
	 * @return the government
	 */
	public boolean isGovernment() {
		return government;
	}

	/**
	 * @param government the government to set
	 */
	public void setGovernment(boolean government) {
		this.government = government;
	}

	/**
	 * @return the searchMinistryProfile
	 */
	public PmOrgProfile getSearchMinistryProfile() {
		return searchMinistryProfile;
	}

	/**
	 * @param searchMinistryProfile the searchMinistryProfile to set
	 */
	public void setSearchMinistryProfile(PmOrgProfile searchMinistryProfile) {
		this.searchMinistryProfile = searchMinistryProfile;
	}

	/**
	 * @return the searchPtjGroupProfile
	 */
	public PmOrgProfile getSearchPtjGroupProfile() {
		return searchPtjGroupProfile;
	}

	/**
	 * @param searchPtjGroupProfile the searchPtjGroupProfile to set
	 */
	public void setSearchPtjGroupProfile(PmOrgProfile searchPtjGroupProfile) {
		this.searchPtjGroupProfile = searchPtjGroupProfile;
	}

	/**
	 * @return the searchPtjProfile
	 */
	public PmOrgProfile getSearchPtjProfile() {
		return searchPtjProfile;
	}

	/**
	 * @param searchPtjProfile the searchPtjProfile to set
	 */
	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}
		
	/**
	 * Retrieves the function type used for searching.
	 * 
	 * @return The search function type as a String.
	 */
	public String getSearchFunctionType() {
		return searchFunctionType;
	}

	/**
	 * Sets the function type to be used for searching.
	 * 
	 * @param searchFunctionType
	 *            The search function type to set.
	 */
	public void setSearchFunctionType(String searchFunctionType) {
		this.searchFunctionType = searchFunctionType;
	}

	/**
	 * @return the isG2G
	 */
	public boolean isG2G() {
		return isG2G;
	}

	/**
	 * @param isG2G the isG2G to set
	 */
	public void setG2G(boolean isG2G) {
		this.isG2G = isG2G;
	}

	/**
	 * Checks if the search is being performed for Letter of Award (LOA) by
	 * Request Note (RN).
	 * 
	 * @return {@code true} if searching LOA by RN, {@code false} otherwise.
	 */
	public boolean isSearchLoaByRN() {
		return searchLoaByRN;
	}

	/**
	 * Sets whether the search should be performed for Letter of Award (LOA) by
	 * Request Note (RN).
	 * 
	 * @param searchLoaByRN
	 *            {@code true} to search LOA by RN, {@code false} otherwise.
	 */
	public void setSearchLoaByRN(boolean searchLoaByRN) {
		this.searchLoaByRN = searchLoaByRN;
	}

	/**
	 * Retrieves the list of Letters of Award (LOA).
	 * 
	 * @return A list of LOA identifiers as Strings.
	 */
	public List<String> getLoaList() {
		return loaList;
	}

	/**
	 * Sets the list of Letters of Award (LOA).
	 * 
	 * @param loaList
	 *            The list of LOA identifiers to set.
	 */
	public void setLoaList(List<String> loaList) {
		this.loaList = loaList;
	}

	/**
	 * Checks if the view is currently set for a PTJ (Perbadanan Tabung
	 * Pendidikan Tinggi Nasional) user.
	 * 
	 * @return {@code true} if viewing as PTJ, {@code false} otherwise.
	 */
	public boolean isViewAsPTJ() {
		return viewAsPTJ;
	}

	/**
	 * Sets whether the view should be for a PTJ (Perbadanan Tabung Pendidikan
	 * Tinggi Nasional) user.
	 * 
	 * @param viewAsPTJ
	 *            {@code true} to view as PTJ, {@code false} otherwise.
	 */
	public void setViewAsPTJ(boolean viewAsPTJ) {
		this.viewAsPTJ = viewAsPTJ;
	}

	/**
	 * Checks if the view is currently set for a Supplier user.
	 * 
	 * @return {@code true} if viewing as Supplier, {@code false} otherwise.
	 */
	public boolean isViewAsSupplier() {
		return viewAsSupplier;
	}

	/**
	 * Sets whether the view should be for a Supplier user.
	 * 
	 * @param viewAsSupplier
	 *            {@code true} to view as Supplier, {@code false} otherwise.
	 */
	public void setViewAsSupplier(boolean viewAsSupplier) {
		this.viewAsSupplier = viewAsSupplier;
	}

	/**
	 * Checks if the Ministry/PTJ (Perbadanan Tabung Pendidikan Tinggi Nasional)
	 * List of Values (LOV) is disabled.
	 * 
	 * @return {@code true} if the Ministry/PTJ LOV is disabled, {@code false}
	 *         otherwise.
	 */
	public boolean isDisableMinistryPtjLov() {
		return disableMinistryPtjLov;
	}

	/**
	 * Sets whether the Ministry/PTJ (Perbadanan Tabung Pendidikan Tinggi
	 * Nasional) List of Values (LOV) should be disabled.
	 * 
	 * @param disableMinistryPtjLov
	 *            {@code true} to disable the Ministry/PTJ LOV, {@code false} to
	 *            enable.
	 */
	public void setDisableMinistryPtjLov(boolean disableMinistryPtjLov) {
		this.disableMinistryPtjLov = disableMinistryPtjLov;
	}

	/**
	 * Checks if the Ministry/PTJ (Perbadanan Tabung Pendidikan Tinggi Nasional)
	 * field should be shown.
	 * 
	 * @return {@code true} if the Ministry/PTJ field should be shown,
	 *         {@code false} otherwise.
	 */
	public boolean isShowMinistryPtjField() {
		return showMinistryPtjField;
	}

	/**
	 * Sets whether the Ministry/PTJ (Perbadanan Tabung Pendidikan Tinggi
	 * Nasional) field should be shown.
	 * 
	 * @param showMinistryPtjField
	 *            {@code true} to show the Ministry/PTJ field, {@code false} to
	 *            hide it.
	 */
	public void setShowMinistryPtjField(boolean showMinistryPtjField) {
		this.showMinistryPtjField = showMinistryPtjField;
	}

	/**
	 * Returns the request note number.
	 *
	 * @return the request note number as a String.
	 */
	public String getRequestNoteNo() {
	    return requestNoteNo;
	}

	/**
	 * Sets the request note number.
	 *
	 * @param requestNoteNo the request note number to set.
	 */
	public void setRequestNoteNo(String requestNoteNo) {
	    this.requestNoteNo = requestNoteNo;
	}

	/**
	 * Returns the request note title.
	 *
	 * @return the request note title as a String.
	 */
	public String getRequestNoteTitle() {
	    return requestNoteTitle;
	}

	/**
	 * Sets the request note title.
	 *
	 * @param requestNoteTitle the request note title to set.
	 */
	public void setRequestNoteTitle(String requestNoteTitle) {
	    this.requestNoteTitle = requestNoteTitle;
	}

	/**
	 * Retrieves the document type used for searching.
	 *
	 * @return The search document type as a String.
	 */
	public String getSearchDocumentType() {
		return searchDocumentType;
	}

	/**
	 * Sets the document type to be used for searching.
	 * 
	 * @param searchDocumentType
	 *            The search document type to set.
	 */
	public void setSearchDocumentType(String searchDocumentType) {
		this.searchDocumentType = searchDocumentType;
	}

	/**
	 * Checks if the PTJ Group (Perbadanan Tabung Pendidikan Tinggi Nasional
	 * Group) List of Values (LOV) is disabled.
	 * 
	 * @return {@code true} if the PTJ Group LOV is disabled, {@code false}
	 *         otherwise.
	 */
	public boolean isDisablePtjGroupLov() {
		return disablePtjGroupLov;
	}

	/**
	 * Sets whether the PTJ Group List of Values (LOV) should be disabled.
	 * 
	 * @param disablePtjGroupLov
	 *            {@code true} to disable the PTJ Group LOV, {@code false} to
	 *            enable.
	 */
	public void setDisablePtjGroupLov(boolean disablePtjGroupLov) {
		this.disablePtjGroupLov = disablePtjGroupLov;
	}

	/**
	 * Checks if the PTJ (Perbadanan Tabung Pendidikan Tinggi Nasional) List of
	 * Values (LOV) is disabled.
	 * 
	 * @return {@code true} if the PTJ LOV is disabled, {@code false} otherwise.
	 */
	public boolean isDisablePtjLov() {
		return disablePtjLov;
	}

	/**
	 * Sets whether the PTJ (Perbadanan Tabung Pendidikan Tinggi Nasional) List
	 * of Values (LOV) should be disabled.
	 * 
	 * @param disablePtjLov
	 *            {@code true} to disable the PTJ LOV, {@code false} to enable.
	 */
	public void setDisablePtjLov(boolean disablePtjLov) {
		this.disablePtjLov = disablePtjLov;
	}

	/**
	 * Retrieves the current counter value.
	 * 
	 * @return The counter value as an {@link Integer}.
	 */
	public Integer getCounter() {
		return counter;
	}

	/**
	 * Sets the counter value.
	 * 
	 * @param counter
	 *            The counter value to set.
	 */
	public void setCounter(Integer counter) {
		this.counter = counter;
	}	
}
