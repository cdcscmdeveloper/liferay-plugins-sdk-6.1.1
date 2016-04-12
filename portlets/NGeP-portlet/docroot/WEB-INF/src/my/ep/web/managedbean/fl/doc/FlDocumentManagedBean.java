/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.doc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
// TODO: Auto-generated Javadoc
//BackingBEan

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.fl.order.FlReliefOrder;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Managed Bean for Fulfillment document.
 *
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flDocumentManagedBean")
@SessionScoped
public class FlDocumentManagedBean extends BaseManagedBean {
	
	/** The log. */
	private static Logger log = Logger.getLogger(FlDocumentManagedBean.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The temp login id. */
	private String tempLoginId;
	
	/** The temp doc type. */
	private String tempDocType;
	
	/** The search type. */
	private String searchType = "D"; // Define or Advanced
	
	/** The search order type. */
	private String searchOrderType; 
	
	/** The search doc type. */
	private String searchDocType;
	
	/** The search doc no. */
	private String searchDocNo;
	
	/** The search contract no. */
	private String searchContractNo;
	
	/** The search circular no. */
	private String searchCircularNo;
	
	/** The search loa no. */
	private String searchLoaNo;
	
	/** The search title. */
	private String searchTitle;
	
	/** The search supp name. */
	private String searchSuppName;
	
	/** The search supp do ref. */
	private String searchSuppDoRef;
	
	/** The search supp inv ref. */
	private String searchSuppInvRef;
	
	/** The search status id. */
	private Long searchStatusId;
	
	/** The search contract from duration. */
	private Date searchContractFromDuration;
	
	/** The search contract to duration. */
	private Date searchContractToDuration;
	
	/** The search order no. */
	private String searchOrderNo;
	
	/** The search phys loa no. */
	private String searchPhysLoaNo;
	
	/** The search phys contract no. */
	private String searchPhysContractNo;
	
	private String searchCentralContractName;
	
	/** The doc status list. */
	private List<PmStatusDesc> docStatusList;
	
	/** The procurement type list. */
	private List<PmParameterDesc> procurementTypeList;
	
	/** The status desc id. */
	private Long statusDescId;
	
	/** The search procurement type id. */
	private Long searchProcurementTypeId;
	
	
	/** The fulfilment request. */
	private FlFulfilmentRequest fulfilmentRequest;
	
	/** The pm user. */
	private PmUser pmUser;
	
	/** The fl document list. */
	private List<VFlDocument> flDocumentList;
	
	/** The check doc type. */
	private Boolean checkDocType =false;
	
	/** The check po doc type. */
	private Boolean checkPoDocType =false;
	
	/** The check pa doc type. */
	private Boolean checkPaDocType =false;
	//private Boolean checkTitle =false;
	/** The check doc no. */
	private Boolean checkDocNo =false;
	
	/** The check do in doc type. */
	private Boolean  checkDoInDocType=false;
	
	/** The check do doc type. */
	private Boolean  checkDoDocType=false;
	
	/** The check in doc type. */
	private Boolean  checkInDocType=false;
	
	// getUserGroup
	/** The fl requisitioner. */
	private String flRequisitioner;
	
	/** The fl receving officer. */
	private String flRecevingOfficer;
	
	/** The fl supplier. */
	private String flSupplier;
	
	/** The fl pm officer. */
	private String flPmOfficer;
	
	/** The fl approver. */
	private String flApprover;
	
	// for selected Approver List
	/** The approver model. */
	private DualListModel<String> approverModel;
	
	/** The approver map. */
	private HashMap<String, PmUserGroupUser> approverMap;
	
	/** The fl document data model. */
	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	private int searchFinancialYear;
	
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile ptjGroup;
	
	private boolean trackingDiarySearch;
	
	private boolean isTaxable=false;
	
	/**
	 * Gets the temp login id.
	 *
	 * @return the temp login id
	 */
	public String getTempLoginId() {
		return tempLoginId;
	}
	
	/**
	 * The Class FlDocumentPagingDataModel.
	 * Transaction Type is mandatory field.
	 * <p>Document Type is mandatory field.
	 * <p>if the user specify the specific document no, the document type is not required to be filled in.
	 * <p>get fl document Search result based on selected and filled in parameters.
	 */
	private class FlDocumentPagingDataModel extends PagingDataModel<VFlDocument> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new fl document paging data model.
		 */
		public FlDocumentPagingDataModel() {
			super("flDocListDt",false);
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VFlDocument> pageChangeListener(ISearchBean<VFlDocument> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				if(null == getSearchOrderType()){ 
					throw new EpException(MessageConst.ERROR_740023);
				}else if((null ==getSearchDocType() &&  "".equalsIgnoreCase(getSearchOrderNo()) && "".equalsIgnoreCase(getSearchDocNo()))){
					throw new EpException(MessageConst.ERROR_740023);
				}
				
				FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
				searchBean.setUseEntityClass(true);
				reqDTO.setSearchBean(searchBean);
				
				reqDTO.setOrderType(getSearchOrderType());
				if(getSearchOrderNo()!=null && getSearchOrderNo()!="") {
					reqDTO.setOrderNo(getSearchOrderNo());
				}					
				if(getSearchDocType()!=null) {
					reqDTO.setDocType(getSearchDocType());
				} else {
					reqDTO.setDocTypeList(getDocTypeListByRole());
				}			
				if(getSearchDocNo()!=null && getSearchDocNo()!="") {
					reqDTO.setDocNo(getSearchDocNo());
				}
				if(getSearchContractNo()!=null && getSearchContractNo()!="") {
					reqDTO.setContractNo(getSearchContractNo());
				}
				if(getSearchPhysContractNo()!=null && getSearchPhysContractNo()!="") {
					reqDTO.setPhysicalContractNo(getSearchPhysContractNo());
				}
				if(getSearchCentralContractName()!=null && getSearchCentralContractName()!="") {
					reqDTO.setSearchCentralContractName(getSearchCentralContractName());
				}
				if(getSearchCircularNo()!=null && getSearchCircularNo()!="")  {
					reqDTO.setCircularNo(getSearchCircularNo());
				}
				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
					reqDTO.setLoaNo(getSearchLoaNo());
				}
				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
					reqDTO.setPhysicalLoaNo(getSearchPhysLoaNo());
				}					
				if(getSearchProcurementTypeId()!=null) {
					reqDTO.setProcurementTypeId(getSearchProcurementTypeId());
				}			
				if(getStatusDescId()!=null) {
					reqDTO.setStatusId(getStatusDescId());
				}
				if(getSearchTitle()!=null && getSearchTitle()!="") {
					reqDTO.setTitle(getSearchTitle());
				}
				if(getSearchSuppName()!=null && getSearchSuppName()!="") {
					reqDTO.setSupplierName(getSearchSuppName());
				}
				if(getSearchSuppDoRef()!=null && getSearchSuppDoRef()!="") {
					reqDTO.setSupplierDoRef(getSearchSuppDoRef());
				}
				if(getSearchSuppInvRef()!=null && getSearchSuppInvRef()!="") {
					reqDTO.setSupplierInvRef(getSearchSuppInvRef());
				}
				if(getSearchContractFromDuration()!=null) {
					reqDTO.setContractStartDate(searchContractFromDuration);
				}
				if(getSearchContractToDuration()!=null) {
					reqDTO.setContractEndDate(searchContractToDuration);
				}
				if(getSearchFinancialYear()!= 0){
					reqDTO.setFinancialYear(getSearchFinancialYear());
				}
				if( getPtj() != null){
					reqDTO.setPtj(getPtj());
				}
				if(getMinistry() != null){
					reqDTO.setMinistry(getMinistry());
				}
				if(getPtjGroup() != null){
					reqDTO.setPtjGroup(getPtjGroup());
				}
				
				if(isFlFulfilmentSupplier()){
					FacesScopes facesScopes = new FacesScopes();
					setPmUser(facesScopes.getUserSessionBean());
					if (getPmUser().getUserId() == null) {
						throw new IllegalArgumentException("userId cannot be null");
					}						
					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(getPmUser().getUserId());
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
					SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
					List<Long> supplierIdList = new ArrayList<Long>();
					for(SmSupplier smSupplier : smResDTO.getResultBean().getResultList()) {
						supplierIdList.add(smSupplier.getSupplierId());
					}
					if(supplierIdList.size()>0) {
						reqDTO.setSupplierIdList(supplierIdList);
					}
				} else {
					IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					FacesScopes facesScopes = new FacesScopes();
					setPmUser(facesScopes.getUserSessionBean());
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(getPmUser().getUserId());
					UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
					Long orgProfileId = null;
					if(userResDTO.getUserOrgList()!=null && userResDTO.getUserOrgList().size()>0) {
						orgProfileId = userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId();
					}
					// handle for Naik taraf PTJ
					IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
					OrgProfileReqDTO profileReqDTO = new OrgProfileReqDTO();
					profileReqDTO.setOrgProfileId(orgProfileId);
					OrgProfileResDTO profileResDTO = orgProfileSO.retrieveProfileGroupByMainOrgProfileId(profileReqDTO);			
					if(profileResDTO.getOrgProfileIdList()!=null && profileResDTO.getOrgProfileIdList().size()>0) {
						reqDTO.setOrgProfileList(profileResDTO.getOrgProfileIdList());
					} else {
						reqDTO.setOrgProfileId(orgProfileId);
					}
				}
				
				if(isAuditor()) {
					FacesScopes facesScopes = new FacesScopes();
					if(facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
						reqDTO.setUserRole(RoleCode.AUDITOR_GENERAL.value());
					} else {
						reqDTO.setUserRole(RoleCode.AUDITOR_INTERNAL.value());
					}
				}
				
				if(isFlFulfilmentSupplier()){
					reqDTO.setUserRole(RoleCode.BASIC_SUPPLIER_USER.value());
				}
				
				IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
				FlDocumentResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
				
				if(resDTO.isSuccessful()) {
					if(null != resDTO.getResultBean()){
						if(resDTO.getResultBean().getTotalRecord() >0){
							filterSearchResults(resDTO.getResultBean().getResultList());
						}
					}
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * Filter search results based on user id and doc type.
	 *
	 * @param searchResultList the search result list
	 */
	public void filterSearchResults(List<VFlDocument> searchResultList){
		IFulfilmentDocumentSO flDocumentSO  = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
		IFulfilmentOrderSO fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentOrderSO.class);
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
		
		for(VFlDocument docsearchResult:searchResultList){
			if(isTrackingDiarySearch()) {
				docsearchResult.setViewAction(true);
				continue;
			}
				docsearchResult.setUpdateAction(false);
				docsearchResult.setViewAction(false);
				docsearchResult.setCancelAction(false);
				if(null == docsearchResult.getStatusId() || ParameterConst.DOCTYPE_PURCHASE_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) ){
					docsearchResult.setViewAction(true);
				}else if(ParameterConst.DOCTYPE_CONTRACT_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) && FulfilmentStatusConst.CR_PENDING_SUBMISSION.equals(docsearchResult.getStatusId())){
					docsearchResult.setViewAction(true);
				}else if(ParameterConst.DOCTYPE_CONTRACT_REQUEST.equalsIgnoreCase(docsearchResult.getDocType())){
					docsearchResult.setViewAction(true);
				}else{
					docsearchResult.setViewAction(true);
					boolean isUpdate=false;
					boolean isCancel=false;
					boolean isStopFulfilment=false;
					boolean isRemoveFactoring=false;
					FLOrderInfo orderInfo=null;
					orderInfo=getStatusInfo(docsearchResult.getDocId(),docsearchResult.getDocType());
						if(null != orderInfo){						
							isUpdate = flDocumentSO.canDocumentUpdate(docsearchResult.getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole());
							isCancel = flDocumentSO.canDocumentCancel(docsearchResult.getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole(), orderInfo.getOrderId(),reqDTO.getUserId());
							isStopFulfilment = flDocumentSO.canDocumentStopFulfilment(docsearchResult.getStatusId(),docsearchResult.getDocId(),orderInfo.getRole(),orderInfo.getOrderId(),reqDTO.getUserId());
							if(docsearchResult.getDocType().equals(FulfilmentConst.DOC_TYPE_PO)){
								isRemoveFactoring = flDocumentSO.canDocumentRemoveFactoring(docsearchResult.getStatusId(),docsearchResult.getDocId(), orderInfo.getRole(),orderInfo.isFactored());
							}	
							if(isUpdate || isCancel) {
								if(docsearchResult.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_FULFILMENT_NOTE)) {
									reqDTO.setFulfilmentNoteId(docsearchResult.getDocId());
									resDTO = fulfilmentOrderSO.isSelectedAcknowledgeOfficerForOrder(reqDTO);
								}
							}
							if(isUpdate){
								docsearchResult.setUpdateAction(true);
								docsearchResult.setViewAction(true);
								if(docsearchResult.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_FULFILMENT_NOTE)) {
									if(!resDTO.isFlag()) {
										docsearchResult.setUpdateAction(false);
									}
								}
							}
							if(isCancel){
								docsearchResult.setCancelAction(true);
								docsearchResult.setViewAction(true);
								if(docsearchResult.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_FULFILMENT_NOTE)) {
									if(!resDTO.isFlag()) {
										docsearchResult.setCancelAction(false);
									}
								}								
							}
							if(isStopFulfilment){
								docsearchResult.setStopFulfilmentAction(true);
								docsearchResult.setViewAction(true);
							}
							if(isRemoveFactoring){
								docsearchResult.setRemoveFactoringAction(true);
								docsearchResult.setViewAction(true);
							}
							if(orderInfo.isCoGSTR()){
								docsearchResult.setCoGSTR(orderInfo.isCoGSTR);
								docsearchResult.setCoGSTRNo(orderInfo.getCoGSTRNo());
							}
							if(orderInfo.isTaxable()){
								docsearchResult.setTaxable(orderInfo.isTaxable);
							}
					  }
			     }
		 }		
	}

	/**
	 * Gets the status info based on doc type and doc id.
	 *
	 * @param docId the doc id
	 * @param docType the doc type
	 * @return the status info
	 */
	private FLOrderInfo getStatusInfo(Long docId,String docType){
		IReceivedNoteSO receivedNoteSO  = null;
		IFulfilmentOrderSO fulfilmentOrderSO = null;
		IDeliveryOrderSO deliveryOrderSO = null;
		IInvoiceSO invoiceSO = null;
		FLOrderInfo orderInfo = new FLOrderInfo();
		FulfilmentReqDTO reqDTO = new FulfilmentReqDTO();
		FulfilmentResDTO resDTO = new FulfilmentResDTO();
		
		if(null != docId && null != docType){
			if(ParameterConst.DOCTYPE_FULFILMENT_NOTE.equalsIgnoreCase(docType)){
				reqDTO.setFulfilmentNoteId(Long.valueOf(docId));
				receivedNoteSO  = (IReceivedNoteSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IReceivedNoteSO.class);
				resDTO = receivedNoteSO.getReceivedNoteById(reqDTO);
				if(null != resDTO.getFulfilmentNote()){
					if(null != resDTO.getFulfilmentNote().getFlRequest().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getFulfilmentNote().getFlRequest().getFlFulfilmentOrder().getFulfilmentOrderId());
						orderInfo.setOrderId(reqDTO.getFlOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentRecevingOfficer()) {
											orderInfo.setRole(RoleCode.RECEIVING_OFFICER.value());
										} else if(isAcknowledgeOfficer()) {
											orderInfo.setRole(RoleCode.ACKNOWLEDGE_OFFICER.value());
										}
									}
								}
							}
					}
				}
			}else if(ParameterConst.DOCTYPE_DELIVERY_ORDER.equalsIgnoreCase(docType)){
				deliveryOrderSO = (IDeliveryOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IDeliveryOrderSO.class);
				reqDTO.setDeliveryOrderId(Long.valueOf(docId));
				resDTO = deliveryOrderSO.getDeliveryOrderById(reqDTO);
				if(null != resDTO.getFlDeliveryOrder()){
					if(null != resDTO.getFlDeliveryOrder().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getFlDeliveryOrder().getFlFulfilmentOrder().getFulfilmentOrderId());
						orderInfo.setOrderId(reqDTO.getFlOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentSupplier()) {
											orderInfo.setRole( RoleCode.BASIC_SUPPLIER_USER.value());
										}
									}
								}
							}
					}
				}
				
			}else if(ParameterConst.DOCTYPE_INVOICE.equalsIgnoreCase(docType)){
				invoiceSO = (IInvoiceSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IInvoiceSO.class);
				reqDTO.setInvoiceId(Long.valueOf(docId));
				resDTO = invoiceSO.getInvoiceById(reqDTO);
				if(null != resDTO.getInvoice()){
					if(null != resDTO.getInvoice().getFlFulfilmentOrder()){
						reqDTO.setFlOrderId(resDTO.getInvoice().getFlFulfilmentOrder().getFulfilmentOrderId());
						orderInfo.setOrderId(reqDTO.getFlOrderId());
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentSupplier()) {
											orderInfo.setRole( RoleCode.BASIC_SUPPLIER_USER.value());
										}
									}
								}
							}
					}
				}
				resDTO = invoiceSO.isInvoiceTaxable(reqDTO);
				if(resDTO.isTaxable()){
					orderInfo.setTaxable(true);
				}
			}else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(docType) || ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(docType)){
				fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				reqDTO.setFlOrderId(Long.valueOf(docId));
				orderInfo.setOrderId(reqDTO.getFlOrderId());
		        resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);			
		        if(null != resDTO.getFlOrder()){
		        	if(null != resDTO.getFlOrder().getStatus()){
		        		if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
		        			orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
		        			if(isFlFulfilmentApprover()){
		        				orderInfo.setRole(RoleCode.FL_APPROVER.value());
		        			}else if(isFlFulfilmentSupplier()){
		        				orderInfo.setRole(RoleCode.BASIC_SUPPLIER_USER.value());
		        			}
		        			if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(docType)){
		        				orderInfo.setFactored(resDTO.getFlOrder().isFactored());
		        			}
		        		}
		        	}
		        	if(resDTO.getFlOrder().getFlReliefOrders() != null && !resDTO.getFlOrder().getFlReliefOrders().isEmpty()){
		        		if(isFlFulfilmentApprover() || isFlFulfilmentSupplier() || isAcknowledgeOfficer()) {
		        			orderInfo.setCoGSTR(true);
		        			for(FlReliefOrder reliefOrder : resDTO.getFlOrder().getFlReliefOrders()) {
		        				if(reliefOrder.getRecordStatusFlg().intValue()==DbValueConst.RECORD_STATUS_ACTIVE) {
		        					orderInfo.setCoGSTRNo(reliefOrder.getReliefOrderNo());
		        					break;
		        				}
		        			}
		        		}
		        	}
		        }
			} 
			if(orderInfo.getRole()==null) {
				if(isFlFulfilmentApprover()){
					orderInfo.setRole(RoleCode.FL_APPROVER.value());
				} else if(isFlFulfilmentSupplier()){
					orderInfo.setRole(RoleCode.BASIC_SUPPLIER_USER.value());
				}
			}
		}
		return orderInfo;
	}
	
	/**
	 * Checks if is Auditor General/Internal user.
	 *
	 * @return true, if is Auditor General/Internal user
	 */
	public boolean isAuditor() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value()) || 
				facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) || 
				 facesScopes.hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}
	
	/**
	 * Checks if is fl fulfilment requisitioner.
	 *
	 * @return true, if is fl fulfilment requisitioner
	 */
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	
	/**
	 * Checks if is fl fulfilment approver.
	 *
	 * @return true, if is fl fulfilment approver
	 */
	public boolean isBPKApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
	
	/**
	 * Checks if is acknowledge officer
	 *
	 * @return true, if is acknowledge officer
	 */
	public boolean isAcknowledgeOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.ACKNOWLEDGE_OFFICER.value());
	}
	
	/**
	 * Checks if is fl fulfilment supplier.
	 *
	 * @return true, if is fl fulfilment supplier
	 */
	public boolean isFlFulfilmentSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || 
				facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || 
				facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || 
				facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()) || 
				facesScopes.hasRoleUser(RoleCode.FL_USER.value()) || 
				facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value()) || 
				facesScopes.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value()) || 
				facesScopes.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value());
	}
	
	/**
	 * Checks if is fl fulfilment receving officer.
	 *
	 * @return true, if is fl fulfilment receving officer
	 */
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	
	/**
	 * Checks if is fl fulfilment payment officer.
	 *
	 * @return true, if is fl fulfilment payment officer
	 */
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
	}
	
	/**
	 * Gets the doc type list by role.
	 *
	 * @return the doc type list by role
	 */
	public List<String> getDocTypeListByRole() {
		List<String> flDocTypes = new ArrayList<String>();
		if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				//flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
			}
			if(isFlFulfilmentApprover() || isBPKApprover() || isAuditor()){
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				//flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer() || isAcknowledgeOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			}
			if(isFlFulfilmentPaymentOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
		} else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			}
			if(isFlFulfilmentApprover() || isBPKApprover() || isAuditor()){
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer() || isAcknowledgeOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			}
			if(isFlFulfilmentPaymentOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
		}
		if(isFlFulfilmentSupplier()) {
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
		}
		return flDocTypes;
	}
	
	/**
	 * The Class FLOrderInfo.
	 */
	private class FLOrderInfo {
		
		/** The order id. */
		private Long orderId;
		
		/** The order status id. */
		private Long orderStatusId;
		
		/** The role. */
		private String role;
		
		/** The factored. */
		private boolean factored = false;
		
		private boolean isCoGSTR=false;
		
		private boolean isTaxable=false;
		
		private String coGSTRNo;

		/**
		 * Gets the role.
		 *
		 * @return the role
		 */
		public String getRole() {
			return role;
		}

		/**
		 * Checks if is factored.
		 *
		 * @return the factored
		 */
		public boolean isFactored() {
			return factored;
		}

		/**
		 * Sets the role.
		 *
		 * @param role the role to set
		 */
		public void setRole(String role) {
			this.role = role;
		}

		/**
		 * Sets the factored.
		 *
		 * @param factored the factored to set
		 */
		public void setFactored(boolean factored) {
			this.factored = factored;
		}

		/**
		 * Gets the order status id.
		 *
		 * @return the orderStatusId
		 */
		public Long getOrderStatusId() {
			return orderStatusId;
		}

		/**
		 * Sets the order status id.
		 *
		 * @param orderStatusId the orderStatusId to set
		 */
		public void setOrderStatusId(Long orderStatusId) {
			this.orderStatusId = orderStatusId;
		}

		/**
		 * Gets the order id.
		 *
		 * @return the order id
		 */
		public Long getOrderId() {
			return orderId;
		}

		/**
		 * Sets the order id.
		 *
		 * @param orderId the new order id
		 */
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

		/**
		 * @return the isCoGSTR
		 */
		public boolean isCoGSTR() {
			return isCoGSTR;
		}

		/**
		 * @param isCoGSTR the isCoGSTR to set
		 */
		public void setCoGSTR(boolean isCoGSTR) {
			this.isCoGSTR = isCoGSTR;
		}

		/**
		 * @return the isTaxable
		 */
		public boolean isTaxable() {
			return isTaxable;
		}

		/**
		 * @param isTaxable the isTaxable to set
		 */
		public void setTaxable(boolean isTaxable) {
			this.isTaxable = isTaxable;
		}

		public String getCoGSTRNo() {
			return coGSTRNo;
		}

		public void setCoGSTRNo(String coGSTRNo) {
			this.coGSTRNo = coGSTRNo;
		}

		
		
	}

	/**
	 * Sets the temp login id.
	 *
	 * @param tempLoginId the new temp login id
	 */
	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	/**
	 * Gets the temp doc type.
	 *
	 * @return the temp doc type
	 */
	public String getTempDocType() {
		return tempDocType;
	}

	/**
	 * Sets the temp doc type.
	 *
	 * @param tempDocType the new temp doc type
	 */
	public void setTempDocType(String tempDocType) {
		this.tempDocType = tempDocType;
	}

	/**
	 * Gets the fl document list.
	 *
	 * @return the fl document list
	 */
	public List<VFlDocument> getFlDocumentList() {
		return flDocumentList;
	}

	/**
	 * Sets the fl document list.
	 *
	 * @param flDocumentList the new fl document list
	 */
	public void setFlDocumentList(List<VFlDocument> flDocumentList) {
		this.flDocumentList = flDocumentList;
	}

	/**
	 * Gets the search type.
	 *
	 * @return the search type
	 */
	public String getSearchType() {
		return searchType;
	}

	/**
	 * Sets the search type.
	 *
	 * @param searchType the new search type
	 */
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/**
	 * Gets the search order type.
	 *
	 * @return the search order type
	 */
	public String getSearchOrderType() {
		return searchOrderType;
	}

	/**
	 * Sets the search order type.
	 *
	 * @param searchOrderType the new search order type
	 */
	public void setSearchOrderType(String searchOrderType) {
		this.searchOrderType = searchOrderType;
	}

	/**
	 * Gets the search doc type.
	 *
	 * @return the search doc type
	 */
	public String getSearchDocType() {
		return searchDocType;
	}

	/**
	 * Sets the search doc type.
	 *
	 * @param searchDocType the new search doc type
	 */
	public void setSearchDocType(String searchDocType) {
		this.searchDocType = searchDocType;
	}

	/**
	 * Gets the search doc no.
	 *
	 * @return the search doc no
	 */
	public String getSearchDocNo() {
		return searchDocNo;
	}

	/**
	 * Sets the search doc no.
	 *
	 * @param searchDocNo the new search doc no
	 */
	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
	}

	/**
	 * Gets the search contract no.
	 *
	 * @return the search contract no
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}

	/**
	 * Sets the search contract no.
	 *
	 * @param searchContractNo the new search contract no
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}

	/**
	 * Gets the search circular no.
	 *
	 * @return the search circular no
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	/**
	 * Sets the search circular no.
	 *
	 * @param searchCircularNo the new search circular no
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	/**
	 * Gets the search loa no.
	 *
	 * @return the search loa no
	 */
	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	/**
	 * Sets the search loa no.
	 *
	 * @param searchLoaNo the new search loa no
	 */
	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	/**
	 * Gets the search status id.
	 *
	 * @return the search status id
	 */
	public Long getSearchStatusId() {
		return searchStatusId;
	}

	/**
	 * Sets the search status id.
	 *
	 * @param searchStatusId the new search status id
	 */
	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	/**
	 * Gets the fulfilment request.
	 *
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * Sets the fulfilment request.
	 *
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * Gets the check doc type.
	 *
	 * @return the checkDocType
	 */
	public Boolean getCheckDocType() {
		return checkDocType;
	}

	/**
	 * Sets the check doc type.
	 *
	 * @param checkDocType the checkDocType to set
	 */
	public void setCheckDocType(Boolean checkDocType) {
		this.checkDocType = checkDocType;
	}

	/**
	 * Gets the check po doc type.
	 *
	 * @return the checkPoDocType
	 */
	public Boolean getCheckPoDocType() {
		return checkPoDocType;
	}

	/**
	 * Sets the check po doc type.
	 *
	 * @param checkPoDocType the checkPoDocType to set
	 */
	public void setCheckPoDocType(Boolean checkPoDocType) {
		this.checkPoDocType = checkPoDocType;
	}

	/**
	 * Gets the check pa doc type.
	 *
	 * @return the checkPaDocType
	 */
	public Boolean getCheckPaDocType() {
		return checkPaDocType;
	}

	/**
	 * Sets the check pa doc type.
	 *
	 * @param checkPaDocType the checkPaDocType to set
	 */
	public void setCheckPaDocType(Boolean checkPaDocType) {
		this.checkPaDocType = checkPaDocType;
	}

	/**
	 * Gets the check doc no.
	 *
	 * @return the checkDocNo
	 */
	public Boolean getCheckDocNo() {
		return checkDocNo;
	}

	/**
	 * Sets the check doc no.
	 *
	 * @param checkDocNo the checkDocNo to set
	 */
	public void setCheckDocNo(Boolean checkDocNo) {
		this.checkDocNo = checkDocNo;
	}

	/**
	 * Gets the search contract to duration.
	 *
	 * @return the searchContractToDuration
	 */
	public Date getSearchContractToDuration() {
		return searchContractToDuration;
	}

	/**
	 * Sets the search contract to duration.
	 *
	 * @param searchContractToDuration the searchContractToDuration to set
	 */
	public void setSearchContractToDuration(Date searchContractToDuration) {
		this.searchContractToDuration = searchContractToDuration;
	}

	/**
	 * Gets the search contract from duration.
	 *
	 * @return the searchContractFromDuration
	 */
	public Date getSearchContractFromDuration() {
		return searchContractFromDuration;
	}

	/**
	 * Sets the search contract from duration.
	 *
	 * @param searchContractFromDuration the searchContractFromDuration to set
	 */
	public void setSearchContractFromDuration(Date searchContractFromDuration) {
		this.searchContractFromDuration = searchContractFromDuration;
	}

	/**
	 * Gets the search title.
	 *
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * Sets the search title.
	 *
	 * @param searchTitle the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * Gets the search supp name.
	 *
	 * @return the searchSuppName
	 */
	public String getSearchSuppName() {
		return searchSuppName;
	}

	/**
	 * Sets the search supp name.
	 *
	 * @param searchSuppName the searchSuppName to set
	 */
	public void setSearchSuppName(String searchSuppName) {
		this.searchSuppName = searchSuppName;
	}

	/**
	 * Gets the doc status list.
	 *
	 * @return the docStatusList
	 */
	public List<PmStatusDesc> getDocStatusList() {
		return docStatusList;
	}

	/**
	 * Sets the doc status list.
	 *
	 * @param docStatusList the docStatusList to set
	 */
	public void setDocStatusList(List<PmStatusDesc> docStatusList) {
		this.docStatusList = docStatusList;
	}

	/**
	 * Gets the status desc id.
	 *
	 * @return the statusDescId
	 */
	public Long getStatusDescId() {
		return statusDescId;
	}

	/**
	 * Sets the status desc id.
	 *
	 * @param statusDescId the statusDescId to set
	 */
	public void setStatusDescId(Long statusDescId) {
		this.statusDescId = statusDescId;
	}

	/**
	 * Gets the pm user.
	 *
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 *
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Gets the search procurement type id.
	 *
	 * @return the searchProcurementTypeId
	 */
	public Long getSearchProcurementTypeId() {
		return searchProcurementTypeId;
	}

	/**
	 * Sets the search procurement type id.
	 *
	 * @param searchProcurementTypeId the searchProcurementTypeId to set
	 */
	public void setSearchProcurementTypeId(Long searchProcurementTypeId) {
		this.searchProcurementTypeId = searchProcurementTypeId;
	}

	/**
	 * Gets the check do in doc type.
	 *
	 * @return the checkDoInDocType
	 */
	public Boolean getCheckDoInDocType() {
		return checkDoInDocType;
	}

	/**
	 * Sets the check do in doc type.
	 *
	 * @param checkDoInDocType the checkDoInDocType to set
	 */
	public void setCheckDoInDocType(Boolean checkDoInDocType) {
		this.checkDoInDocType = checkDoInDocType;
	}

	/**
	 * Gets the procurement type list.
	 *
	 * @return the procurementTypeList
	 */
	public List<PmParameterDesc> getProcurementTypeList() {
		return procurementTypeList;
	}

	/**
	 * Sets the procurement type list.
	 *
	 * @param procurementTypeList the procurementTypeList to set
	 */
	public void setProcurementTypeList(List<PmParameterDesc> procurementTypeList) {
		this.procurementTypeList = procurementTypeList;
	}

	/**
	 * Gets the search supp do ref.
	 *
	 * @return the searchSuppDoRef
	 */
	public String getSearchSuppDoRef() {
		return searchSuppDoRef;
	}

	/**
	 * Sets the search supp do ref.
	 *
	 * @param searchSuppDoRef the searchSuppDoRef to set
	 */
	public void setSearchSuppDoRef(String searchSuppDoRef) {
		this.searchSuppDoRef = searchSuppDoRef;
	}

	/**
	 * Gets the search supp inv ref.
	 *
	 * @return the searchSuppInvRef
	 */
	public String getSearchSuppInvRef() {
		return searchSuppInvRef;
	}

	/**
	 * Sets the search supp inv ref.
	 *
	 * @param searchSuppInvRef the searchSuppInvRef to set
	 */
	public void setSearchSuppInvRef(String searchSuppInvRef) {
		this.searchSuppInvRef = searchSuppInvRef;
	}

	/**
	 * Gets the fl requisitioner.
	 *
	 * @return the flRequisitioner
	 */
	public String getFlRequisitioner() {
		return flRequisitioner;
	}

	/**
	 * Sets the fl requisitioner.
	 *
	 * @param flRequisitioner the flRequisitioner to set
	 */
	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}

	/**
	 * Gets the fl receving officer.
	 *
	 * @return the flRecevingOfficer
	 */
	public String getFlRecevingOfficer() {
		return flRecevingOfficer;
	}

	/**
	 * Sets the fl receving officer.
	 *
	 * @param flRecevingOfficer the flRecevingOfficer to set
	 */
	public void setFlRecevingOfficer(String flRecevingOfficer) {
		this.flRecevingOfficer = flRecevingOfficer;
	}

	/**
	 * Gets the fl supplier.
	 *
	 * @return the flSupplier
	 */
	public String getFlSupplier() {
		return flSupplier;
	}

	/**
	 * Sets the fl supplier.
	 *
	 * @param flSupplier the flSupplier to set
	 */
	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}

	/**
	 * Gets the fl pm officer.
	 *
	 * @return the flPmOfficer
	 */
	public String getFlPmOfficer() {
		return flPmOfficer;
	}

	/**
	 * Sets the fl pm officer.
	 *
	 * @param flPmOfficer the flPmOfficer to set
	 */
	public void setFlPmOfficer(String flPmOfficer) {
		this.flPmOfficer = flPmOfficer;
	}

	/**
	 * Gets the fl approver.
	 *
	 * @return the flApprover
	 */
	public String getFlApprover() {
		return flApprover;
	}

	/**
	 * Sets the fl approver.
	 *
	 * @param flApprover the flApprover to set
	 */
	public void setFlApprover(String flApprover) {
		this.flApprover = flApprover;
	}

	/**
	 * Gets the approver model.
	 *
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	/**
	 * Sets the approver model.
	 *
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * Gets the approver map.
	 *
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		return approverMap;
	}

	/**
	 * Sets the approver map.
	 *
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}

	/**
	 * Gets the check do doc type.
	 *
	 * @return the checkDoDocType
	 */
	public Boolean getCheckDoDocType() {
		return checkDoDocType;
	}

	/**
	 * Sets the check do doc type.
	 *
	 * @param checkDoDocType the checkDoDocType to set
	 */
	public void setCheckDoDocType(Boolean checkDoDocType) {
		this.checkDoDocType = checkDoDocType;
	}

	/**
	 * Gets the check in doc type.
	 *
	 * @return the checkInDocType
	 */
	public Boolean getCheckInDocType() {
		return checkInDocType;
	}

	/**
	 * Sets the check in doc type.
	 *
	 * @param checkInDocType the checkInDocType to set
	 */
	public void setCheckInDocType(Boolean checkInDocType) {
		this.checkInDocType = checkInDocType;
	}

	/**
	 * Gets the search order no.
	 *
	 * @return the searchOrderNo
	 */
	public String getSearchOrderNo() {
		return searchOrderNo;
	}

	/**
	 * Sets the search order no.
	 *
	 * @param searchOrderNo the searchOrderNo to set
	 */
	public void setSearchOrderNo(String searchOrderNo) {
		this.searchOrderNo = searchOrderNo;
	}

	/**
	 * Gets the fl document data model.
	 *
	 * @return the flDocumentDataModel
	 */
	public IPagingDataModel<VFlDocument> getFlDocumentDataModel() {
		return flDocumentDataModel;
	}
	
	/**
	 * Gets the search phys loa no.
	 *
	 * @return the searchPhysLoaNo
	 */
	public String getSearchPhysLoaNo() {
		return searchPhysLoaNo;
	}
	
	/**
	 * Gets the search phys contract no.
	 *
	 * @return the searchPhysContractNo
	 */
	public String getSearchPhysContractNo() {
		return searchPhysContractNo;
	}
	
	/**
	 * Sets the search phys loa no.
	 *
	 * @param searchPhysLoaNo the searchPhysLoaNo to set
	 */
	public void setSearchPhysLoaNo(String searchPhysLoaNo) {
		this.searchPhysLoaNo = searchPhysLoaNo;
	}
	
	/**
	 * Sets the search phys contract no.
	 *
	 * @param searchPhysContractNo the searchPhysContractNo to set
	 */
	public void setSearchPhysContractNo(String searchPhysContractNo) {
		this.searchPhysContractNo = searchPhysContractNo;
	}

	/**
	 * @return the searchFinancialYear
	 */
	public int getSearchFinancialYear() {
		return searchFinancialYear;
	}

	/**
	 * @param searchFinancialYear the searchFinancialYear to set
	 */
	public void setSearchFinancialYear(int searchFinancialYear) {
		this.searchFinancialYear = searchFinancialYear;
	}

	/**
	 * @return the ptj
	 */
	public PmOrgProfile getPtj() {
		return ptj;
	}

	/**
	 * @param ptj the ptj to set
	 */
	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}

	/**
	 * @return the ministry
	 */
	public PmOrgProfile getMinistry() {
		return ministry;
	}

	/**
	 * @param ministry the ministry to set
	 */
	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;
	}

	public boolean isTrackingDiarySearch() {
		return trackingDiarySearch;
	}

	public void setTrackingDiarySearch(boolean trackingDiarySearch) {
		this.trackingDiarySearch = trackingDiarySearch;
	}

	/**
	 * @return the ptjGroup
	 */
	public PmOrgProfile getPtjGroup() {
		return ptjGroup;
	}

	/**
	 * @param ptjGroup the ptjGroup to set
	 */
	public void setPtjGroup(PmOrgProfile ptjGroup) {
		this.ptjGroup = ptjGroup;
	}

	/**
	 * @return the searchCentralContractName
	 */
	public String getSearchCentralContractName() {
		return searchCentralContractName;
	}

	/**
	 * @param searchCentralContractName the searchCentralContractName to set
	 */
	public void setSearchCentralContractName(String searchCentralContractName) {
		this.searchCentralContractName = searchCentralContractName;
	}

	/**
	 * @return the isTaxable
	 */
	public boolean isTaxable() {
		return isTaxable;
	}

	/**
	 * @param isTaxable the isTaxable to set
	 */
	public void setTaxable(boolean isTaxable) {
		this.isTaxable = isTaxable;
	}

}
