/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
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
import my.ep.jaxb.sc.directpurchasedata.SCDirectPurchaseDataType;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.directpurchase.ScRNZone;
import my.ep.persistence.entity.sc.directpurchase.ScRequestItem;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNote;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteRemark;
import my.ep.persistence.entity.sc.directpurchase.ScRequestSupplierItem;
import my.ep.persistence.entity.sc.directpurchase.ScSupplierItemPrice;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
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
	private	ScRequestNote	requestNote = new ScRequestNote(); //contain request list information
	private	List<ScRequestSupplierItem>	requestSupplierItems	=	new ArrayList<ScRequestSupplierItem>(); // list of request supplier item to be recommend
	private	List<ScRequestNoteRemark>	selectedRemarks	=	new	ArrayList<ScRequestNoteRemark>();
	private	List<ScRequestNoteDtl>	requestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();//list request Note Dtl.
	private	List<ScRequestNoteDtl>	selectedRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
	private List<ScRequestItem>	requestNoteItems = new ArrayList<ScRequestItem>();
	private	DualListModel<PmUser>	approvalUsers = new DualListModel<PmUser>();
	private	String	fulfillmentType;
	private Long supplierId;
	private	Long	assignedToUserId;
	private	String	periodType;
	private	int	periodValue;
	
	private List<PmOrgProfile> userDivisionList;
    private ScRequestSupplierItem requestSupplierItem = new ScRequestSupplierItem();
    private ScRequestNoteDtl requestNoteDtl = new ScRequestNoteDtl();
    private List<PmUser> userList = new ArrayList<PmUser>();
    
    private String priceTypeCode;
    private boolean justTypeOption;
    private boolean procTypeOption;
    private boolean itemCheck; // For checking item whether service or product
    private SelectItem[] justDropdown; // justification drop down
    private SelectItem[] priceTypes; // price type drop down for add to cart
    private SelectItem[] procurementTypes; // for procurement type dropdown
    private String processName;
    private PmStatus requestNoteStatus;
    private boolean required = Boolean.FALSE;
    private boolean justRequired = Boolean.FALSE;
    private boolean addToCartFlag;
    
    private String justification;
    private String justificationType;
    private String procurementType;
    private String priceType;
    
    private boolean titleChecker = Boolean.TRUE;
    private Long prInquiryId; // To get PI id to cancel in request note 
    private ScRNZone scRNZone = new ScRNZone(); 
    
    // BPM Task
    private SCDirectPurchaseDataType directPurchaseDataType;
    
    // For Search Request Note
    private String searchReqNoteNo;
    private String searchSrcMethod;
    private Date searchCreatedDateFrom;
    private Date searchCreatedDateTo;
    private List<Long> searchStatuses;
    private List<PmStatusDesc> statusDescs;
    private String createdBy;
	private	List<ScRequestNoteDtl>	supplierRecList	=	new ArrayList<ScRequestNoteDtl>();
	private	List<ScSupplierItemPrice>	pricingList	=	new ArrayList<ScSupplierItemPrice>(); // For pricing pop to show all pricing
    
    private IPagingDataModel<ScRequestNote> reqNoteDataModel = new RequestNotePagingDataModel();
    private List<CmSupplierItem> compareSupplierItems = new ArrayList<CmSupplierItem>(); // copy supplier items from the compare screen
    private boolean piRNChecker = Boolean.TRUE;
    
    private	List<ScRequestNoteDtl>	deleteRequestNoteDtls	=	new ArrayList<ScRequestNoteDtl>();
    private	Map<String, TplSpecZoneDtl>	zoneDtlMap;
    private CmSupplierItem cmSupplierItem;
    private String recentPriceDuration;
    
    private String reportURL;
    
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
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplierId;
	}
	
	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
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
	 * @return the requestNoteStatus
	 */
	public PmStatus getRequestNoteStatus() {
		return requestNoteStatus;
	}
	/**
	 * @param requestNoteStatus the requestNoteStatus to set
	 */
	public void setRequestNoteStatus(PmStatus requestNoteStatus) {
		this.requestNoteStatus = requestNoteStatus;
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
	/**
	 * @return the requestNoteItems
	 */
	public List<ScRequestItem> getRequestNoteItems() {
		return requestNoteItems;
	}
	/**
	 * @param requestNoteItems the requestNoteItems to set
	 */
	public void setRequestNoteItems(List<ScRequestItem> requestNoteItems) {
		this.requestNoteItems = requestNoteItems;
	}
	
	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	/**
	 * @return the justification
	 */
	public String getJustification() {
		return justification;
	}
	/**
	 * @param justification the justification to set
	 */
	public void setJustification(String justification) {
		this.justification = justification;
	}
	/**
	 * @return the justificationType
	 */
	public String getJustificationType() {
		return justificationType;
	}
	/**
	 * @param justificationType the justificationType to set
	 */
	public void setJustificationType(String justificationType) {
		this.justificationType = justificationType;
	}
	/**
	 * @return the procurementType
	 */
	public String getProcurementType() {
		return procurementType;
	}
	/**
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}
	/**
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}
	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * @return the searchReqNoteNo
	 */
	public String getSearchReqNoteNo() {
		return searchReqNoteNo;
	}
	
	/**
	 * @param searchReqNoteNo the searchReqNoteNo to set
	 */
	public void setSearchReqNoteNo(String searchReqNoteNo) {
		this.searchReqNoteNo = searchReqNoteNo;
	}
	
	/**
	 * @return the searchSrcMethod
	 */
	public String getSearchSrcMethod() {
		return searchSrcMethod;
	}
	
	/**
	 * @param searchSrcMethod the searchSrcMethod to set
	 */
	public void setSearchSrcMethod(String searchSrcMethod) {
		this.searchSrcMethod = searchSrcMethod;
	}
	
	/**
	 * @return the searchCreatedDateFrom
	 */
	public Date getSearchCreatedDateFrom() {
		return searchCreatedDateFrom;
	}
	/**
	 * @param searchCreatedDateFrom the searchCreatedDateFrom to set
	 */
	public void setSearchCreatedDateFrom(Date searchCreatedDateFrom) {
		this.searchCreatedDateFrom = searchCreatedDateFrom;
	}
	/**
	 * @return the searchCreatedDateTo
	 */
	public Date getSearchCreatedDateTo() {
		return searchCreatedDateTo;
	}
	/**
	 * @param searchCreatedDateTo the searchCreatedDateTo to set
	 */
	public void setSearchCreatedDateTo(Date searchCreatedDateTo) {
		this.searchCreatedDateTo = searchCreatedDateTo;
	}
	/**
	 * @return the searchStatuses
	 */
	public List<Long> getSearchStatuses() {
		return searchStatuses;
	}
	/**
	 * @param searchStatuses the searchStatuses to set
	 */
	public void setSearchStatuses(List<Long> searchStatuses) {
		this.searchStatuses = searchStatuses;
	}
	/**
	 * @return the statusDescs
	 */
	public List<PmStatusDesc> getStatusDescs() {
		return statusDescs;
	}
	/**
	 * @param statusDescs the statusDescs to set
	 */
	public void setStatusDescs(List<PmStatusDesc> statusDescs) {
		this.statusDescs = statusDescs;
	}
	/**
	 * @return the reqNoteDataModel
	 */
	public IPagingDataModel<ScRequestNote> getReqNoteDataModel() {
		return reqNoteDataModel;
	}
	
	/**
	 * @param reqNoteDataModel the reqNoteDataModel to set
	 */
	public void setReqNoteDataModel(IPagingDataModel<ScRequestNote> reqNoteDataModel) {
		this.reqNoteDataModel = reqNoteDataModel;
	}

	private class RequestNotePagingDataModel extends PagingDataModel<ScRequestNote> {
		private static final long serialVersionUID = 1L;

		public RequestNotePagingDataModel() {
			super("reqNoteTbl");
		}

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
				
				List<Long> searchStatuses = getSearchStatuses();
				if(searchStatuses != null && searchStatuses.size() > 0) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("workFlowStatuses.pmStatus.statusId",
									SearchOperator.OPERATOR_IN.value(),
									searchStatuses));
					
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
	 * @return the justRequired
	 */
	public boolean isJustRequired() {
		return justRequired;
	}
	/**
	 * @param justRequired the justRequired to set
	 */
	public void setJustRequired(boolean justRequired) {
		this.justRequired = justRequired;
	}
	/**
	 * @return the procurementTypes
	 */
	public SelectItem[] getProcurementTypes() {
		return procurementTypes;
	}
	/**
	 * @param procurementTypes the procurementTypes to set
	 */
	public void setProcurementTypes(SelectItem[] procurementTypes) {
		this.procurementTypes = procurementTypes;
	}
	
	/**
	 * @return the addToCartFlag
	 */
	public boolean isAddToCartFlag() {
		return addToCartFlag;
	}
	/**
	 * @param addToCartFlag the addToCartFlag to set
	 */
	public void setAddToCartFlag(boolean addToCartFlag) {
		this.addToCartFlag = addToCartFlag;
	}
	/**
	 * @return the titleChecker
	 */
	public boolean isTitleChecker() {
		return titleChecker;
	}
	/**
	 * @param titleChecker the titleChecker to set
	 */
	public void setTitleChecker(boolean titleChecker) {
		this.titleChecker = titleChecker;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the supplierRecList
	 */
	public List<ScRequestNoteDtl> getSupplierRecList() {
		return supplierRecList;
	}
	/**
	 * @param supplierRecList the supplierRecList to set
	 */
	public void setSupplierRecList(List<ScRequestNoteDtl> supplierRecList) {
		this.supplierRecList = supplierRecList;
	}
	/**
	 * @return the prInquiryId
	 */
	public Long getPrInquiryId() {
		return prInquiryId;
	}
	/**
	 * @param prInquiryId the prInquiryId to set
	 */
	public void setPrInquiryId(Long prInquiryId) {
		this.prInquiryId = prInquiryId;
	}

	/**
	 * @return the scRNZone
	 */
	public ScRNZone getScRNZone() {
		return scRNZone;
	}
	/**
	 * @param scRNZone the scRNZone to set
	 */
	public void setScRNZone(ScRNZone scRNZone) {
		this.scRNZone = scRNZone;
	}
	/**
	 * @return the pricingList
	 */
	public List<ScSupplierItemPrice> getPricingList() {
		return pricingList;
	}
	/**
	 * @param pricingList the pricingList to set
	 */
	public void setPricingList(List<ScSupplierItemPrice> pricingList) {
		this.pricingList = pricingList;
	}
	/**
	 * @return the compareSupplierItems
	 */
	public List<CmSupplierItem> getCompareSupplierItems() {
		return compareSupplierItems;
	}
	/**
	 * @param compareSupplierItems the compareSupplierItems to set
	 */
	public void setCompareSupplierItems(List<CmSupplierItem> compareSupplierItems) {
		this.compareSupplierItems = compareSupplierItems;
	}
	/**
	 * @return the piRNChecker
	 */
	public boolean isPiRNChecker() {
		return piRNChecker;
	}
	/**
	 * @param piRNChecker the piRNChecker to set
	 */
	public void setPiRNChecker(boolean piRNChecker) {
		this.piRNChecker = piRNChecker;
	}
	/**
	 * @return the deleteRequestNoteDtls
	 */
	public List<ScRequestNoteDtl> getDeleteRequestNoteDtls() {
		return deleteRequestNoteDtls;
	}
	/**
	 * @param deleteRequestNoteDtls the deleteRequestNoteDtls to set
	 */
	public void setDeleteRequestNoteDtls(
			List<ScRequestNoteDtl> deleteRequestNoteDtls) {
		this.deleteRequestNoteDtls = deleteRequestNoteDtls;
	}
	
	/**
	 * @return the zoneDtlMap
	 */
	public Map<String, TplSpecZoneDtl> getZoneDtlMap() {
		return zoneDtlMap;
	}
	
	/**
	 * @param zoneDtlMap the zoneDtlMap to set
	 */
	public void setZoneDtlMap(Map<String, TplSpecZoneDtl> zoneDtlMap) {
		this.zoneDtlMap = zoneDtlMap;
	}
	public CmSupplierItem getCmSupplierItem() {
		return cmSupplierItem;
	}
	public void setCmSupplierItem(CmSupplierItem cmSupplierItem) {
		this.cmSupplierItem = cmSupplierItem;
	}
	public String getRecentPriceDuration() {
		return recentPriceDuration;
	}
	public void setRecentPriceDuration(String recentPriceDuration) {
		this.recentPriceDuration = recentPriceDuration;
	}
	public String getReportURL() {
		return reportURL;
	}
	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}
	
}
