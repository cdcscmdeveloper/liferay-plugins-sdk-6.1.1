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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.fl.frn.IReceivedNoteSO;
import my.ep.biz.service.fl.invoice.IInvoiceSO;
import my.ep.biz.service.fl.order.IDeliveryOrderSO;
import my.ep.biz.service.fl.order.IFulfilmentOrderSO;
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
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
//BackingBEan

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flDocumentManagedBean")
@SessionScoped
public class FlDocumentManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(FlDocumentManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String tempLoginId;
	private String tempDocType;
	private String searchType = "D"; // Define or Advanced
	private String searchOrderType; 
	private String searchDocType;
	private String searchDocNo;
	private String searchContractNo;
	private String searchCircularNo;
	private String searchLoaNo;
	private String searchTitle;
	private String searchSuppName;
	private String searchSuppDoRef;
	private String searchSuppInvRef;
	private Long searchStatusId;
	private Date searchContractFromDuration;
	private Date searchContractToDuration;
	private String searchOrderNo;
	
	private List<PmStatusDesc> docStatusList;
	private List<PmParameterDesc> procurementTypeList;
	private Long statusDescId;
	private Long searchProcurementTypeId;
	
	
	private FlFulfilmentRequest fulfilmentRequest;
	private PmUser pmUser;
	
	private List<VFlDocument> flDocumentList;
	
	private Boolean checkDocType =false;
	private Boolean checkPoDocType =false;
	private Boolean checkPaDocType =false;
	//private Boolean checkTitle =false;
	private Boolean checkDocNo =false;
	private Boolean  checkDoInDocType=false;
	private Boolean  checkDoDocType=false;
	private Boolean  checkInDocType=false;
	
	// getUserGroup
	private String flRequisitioner;
	private String flRecevingOfficer;
	private String flSupplier;
	private String flPmOfficer;
	private String flApprover;
	
	// for selected Approver List
	private DualListModel<String> approverModel;
	private HashMap<String, PmUserGroupUser> approverMap;
	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();

	public String getTempLoginId() {
		return tempLoginId;
	}
	
	private class FlDocumentPagingDataModel extends PagingDataModel<VFlDocument> {
		private static final long serialVersionUID = 1L;

		public FlDocumentPagingDataModel() {
			super("flDocListDt",false);
		}
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
				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orderType",SearchOperator.OPERATOR_EQUALS.value(),getSearchOrderType()));
				if(getSearchDocType()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getSearchDocType()));
				} else {
					List<String> flDocTypeList = getDocTypeListByRole();
					if(flDocTypeList.size()>0) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_IN.value(),flDocTypeList));
					}
				}
				if(getSearchDocNo()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocNo()));
				}
				if(getSearchContractNo()!=null && getSearchContractNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),getSearchContractNo()));
				}
				if(getSearchCircularNo()!=null && getSearchCircularNo()!="")  {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_LIKE.value(),getSearchCircularNo()));
				}
				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo()));
				}		
				if(getSearchProcurementTypeId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("procurementType",SearchOperator.OPERATOR_EQUALS.value(),getSearchProcurementTypeId()));
				}			
				if(getStatusDescId()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getStatusDescId()));
				}
				if(getSearchTitle()!=null && getSearchTitle()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("title",SearchOperator.OPERATOR_LIKE.value(),getSearchTitle(), true));
				}
				if(getSearchSuppName()!=null && getSearchSuppName()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.companyName",SearchOperator.OPERATOR_LIKE.value(),getSearchSuppName(), true));
				}
				if(getSearchSuppDoRef()!=null && getSearchSuppDoRef()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("supplierDoRef",SearchOperator.OPERATOR_LIKE.value(),getSearchSuppDoRef(), true));
				}
				if(getSearchSuppInvRef()!=null && getSearchSuppInvRef()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("supplierInvRef",SearchOperator.OPERATOR_LIKE.value(),getSearchSuppInvRef(), true));
				}
				if(getSearchOrderNo()!=null && getSearchOrderNo()!="") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("orderNo",SearchOperator.OPERATOR_LIKE.value(),getSearchOrderNo()));
				}
			
				if(isFlFulfilmentSupplier()){
					FacesScopes facesScopes = new FacesScopes();
					setPmUser(facesScopes.getUserSessionBean());
					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(getPmUser().getUserId());
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
					SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
					List<Long> supplierIdList = new ArrayList<Long>();
					for(SmSupplier smSupplier : smResDTO.getResultBean().getResultList()) {
						supplierIdList.add(smSupplier.getSupplierId());
					}
					if(supplierIdList.size()>0) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.supplierId", SearchOperator.OPERATOR_IN.value(), supplierIdList));
					}
				} else {
					IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					FacesScopes facesScopes = new FacesScopes();
					setPmUser(facesScopes.getUserSessionBean());
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setUserId(getPmUser().getUserId());
					UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
					Long orgProfileId = null;
					if(resDTO.getUserOrgList()!=null && resDTO.getUserOrgList().size()>0) {
						orgProfileId = resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId();
					}
					
					searchBean.getDataFilterBeans().add(
							new SearchMultiFilterBean(
									new SearchFilterBean(
											"pmCreatedOrgProfile.orgProfileId", 
											SearchOperator.OPERATOR_EQUALS.value(), 
											orgProfileId), 
									new SearchMultiFilterBean(
											new SearchFilterBean(
													"pmIssuedOrgProfile.orgProfileId", 
													SearchOperator.OPERATOR_EQUALS.value(), 
													orgProfileId), 
											SearchCondition.CONDITION_OR.value(),		
											new SearchFilterBean(
													"pmPreparedOrgProfile.orgProfileId", 
													SearchOperator.OPERATOR_EQUALS.value(), 
													orgProfileId,
													SearchCondition.CONDITION_OR.value()))
											));
					
				}
				FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
				reqDTO.setSearchBean(searchBean);
				
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
	public void filterSearchResults(List<VFlDocument> searchResultList){
		IFulfilmentDocumentSO flDocumentSO  = null;
			for(VFlDocument docsearchResult:searchResultList){
				docsearchResult.setUpdateAction(false);
				docsearchResult.setViewAction(false);
				docsearchResult.setCancelAction(false);
				if(null == docsearchResult.getStatus() || ParameterConst.DOCTYPE_PURCHASE_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) ){
					docsearchResult.setViewAction(true);
				}else if(ParameterConst.DOCTYPE_CONTRACT_REQUEST.equalsIgnoreCase(docsearchResult.getDocType()) && FulfilmentStatusConst.CR_PENDING_SUBMISSION.equals(docsearchResult.getStatus().getStatusId())){
					docsearchResult.setUpdateAction(true);
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
							flDocumentSO  = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
							isUpdate = flDocumentSO.canDocumentUpdate(docsearchResult.getStatus().getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole());
							isCancel = flDocumentSO.canDocumentCancel(docsearchResult.getStatus().getStatusId(), orderInfo.getOrderStatusId(), orderInfo.getRole());
							isStopFulfilment = flDocumentSO.canDocumentStopFulfilment(docsearchResult.getStatus().getStatusId(),docsearchResult.getDocId(),orderInfo.getRole());
							if(docsearchResult.getDocType().equals(FulfilmentConst.DOC_TYPE_PO)){
								isRemoveFactoring = flDocumentSO.canDocumentRemoveFactoring(docsearchResult.getStatus().getStatusId(),docsearchResult.getDocId(), orderInfo.getRole(),orderInfo.isFactored());
							}	
							if(isUpdate){
								docsearchResult.setUpdateAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action update ### :: "+docsearchResult.isUpdateAction());
							}
							if(isCancel){
								docsearchResult.setCancelAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  cancel @@@@@@@:: "+docsearchResult.isCancelAction());
							}
							if(isStopFulfilment){
								docsearchResult.setStopFulfilmentAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  StopFulfilmen %%%%%%:: "+docsearchResult.isStopFulfilmentAction());
							}
							if(isRemoveFactoring){
								docsearchResult.setRemoveFactoringAction(true);
								docsearchResult.setViewAction(true);
								//System.out.println("final action  RemoveFactoring @@@@@@@:: "+docsearchResult.isRemoveFactoringAction());
							}
					  }
			     }
		 }		
	}
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
						fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
						resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);
							if(null != resDTO.getFlOrder()){
								if(null != resDTO.getFlOrder().getStatus()){
									if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
										orderInfo.setOrderStatusId(resDTO.getFlOrder().getStatus().getStatusId());
										if(isFlFulfilmentRecevingOfficer()) {
											orderInfo.setRole( RoleCode.RECEIVING_OFFICER.value());
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
			}else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(docType) || ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(docType)){
				fulfilmentOrderSO = (IFulfilmentOrderSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentOrderSO.class);
				reqDTO.setFlOrderId(Long.valueOf(docId));
		        resDTO = fulfilmentOrderSO.getFulfilmentOrderById(reqDTO);			
				if(null != resDTO.getFlOrder()){
					if(null != resDTO.getFlOrder()){
						if(null != resDTO.getFlOrder().getStatus()){
							if(null !=resDTO.getFlOrder().getStatus().getStatusId()){
								//System.out.println("2222222222222222   po/co staus id is :::: "+resDTO.getFlOrder().getStatus().getStatusId());
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
					}
				}
			}
		}
		return orderInfo;
	}
	
	public boolean isFlFulfilmentRequisitioner() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value());
	}
	public boolean isFlFulfilmentApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value());
	}
	public boolean isFlFulfilmentSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}
	public boolean isFlFulfilmentRecevingOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value());
	}
	public boolean isFlFulfilmentPaymentOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value());
	}
	private List<String> getDocTypeListByRole() {
		List<String> flDocTypes = new ArrayList<String>();
		if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
			}
			if(isFlFulfilmentApprover()){
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			}
			if(isFlFulfilmentPaymentOfficer()){
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
		} else if(getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
			if(isFlFulfilmentRequisitioner()) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			}
			if(isFlFulfilmentApprover()){
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			}
			if(isFlFulfilmentRecevingOfficer()){
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
		}
		return flDocTypes;
	}
	private class FLOrderInfo {
		private Long orderStatusId;
		private String role;
		private boolean factored = false;

		/**
		 * @return the role
		 */
		public String getRole() {
			return role;
		}

		/**
		 * @return the factored
		 */
		public boolean isFactored() {
			return factored;
		}

		/**
		 * @param role the role to set
		 */
		public void setRole(String role) {
			this.role = role;
		}

		/**
		 * @param factored the factored to set
		 */
		public void setFactored(boolean factored) {
			this.factored = factored;
		}

		/**
		 * @return the orderStatusId
		 */
		public Long getOrderStatusId() {
			return orderStatusId;
		}

		/**
		 * @param orderStatusId the orderStatusId to set
		 */
		public void setOrderStatusId(Long orderStatusId) {
			this.orderStatusId = orderStatusId;
		}
		
	}

	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	public String getTempDocType() {
		return tempDocType;
	}

	public void setTempDocType(String tempDocType) {
		this.tempDocType = tempDocType;
	}

	public List<VFlDocument> getFlDocumentList() {
		return flDocumentList;
	}

	public void setFlDocumentList(List<VFlDocument> flDocumentList) {
		this.flDocumentList = flDocumentList;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchOrderType() {
		return searchOrderType;
	}

	public void setSearchOrderType(String searchOrderType) {
		this.searchOrderType = searchOrderType;
	}

	public String getSearchDocType() {
		return searchDocType;
	}

	public void setSearchDocType(String searchDocType) {
		this.searchDocType = searchDocType;
	}

	public String getSearchDocNo() {
		return searchDocNo;
	}

	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
	}

	public String getSearchContractNo() {
		return searchContractNo;
	}

	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}

	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}

	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	public Long getSearchStatusId() {
		return searchStatusId;
	}

	public void setSearchStatusId(Long searchStatusId) {
		this.searchStatusId = searchStatusId;
	}

	/**
	 * @return the fulfilmentRequest
	 */
	public FlFulfilmentRequest getFulfilmentRequest() {
		return fulfilmentRequest;
	}

	/**
	 * @param fulfilmentRequest the fulfilmentRequest to set
	 */
	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
		this.fulfilmentRequest = fulfilmentRequest;
	}

	/**
	 * @return the checkDocType
	 */
	public Boolean getCheckDocType() {
		return checkDocType;
	}

	/**
	 * @param checkDocType the checkDocType to set
	 */
	public void setCheckDocType(Boolean checkDocType) {
		this.checkDocType = checkDocType;
	}

	/**
	 * @return the checkPoDocType
	 */
	public Boolean getCheckPoDocType() {
		return checkPoDocType;
	}

	/**
	 * @param checkPoDocType the checkPoDocType to set
	 */
	public void setCheckPoDocType(Boolean checkPoDocType) {
		this.checkPoDocType = checkPoDocType;
	}

	/**
	 * @return the checkPaDocType
	 */
	public Boolean getCheckPaDocType() {
		return checkPaDocType;
	}

	/**
	 * @param checkPaDocType the checkPaDocType to set
	 */
	public void setCheckPaDocType(Boolean checkPaDocType) {
		this.checkPaDocType = checkPaDocType;
	}

	/**
	 * @return the checkDocNo
	 */
	public Boolean getCheckDocNo() {
		return checkDocNo;
	}

	/**
	 * @param checkDocNo the checkDocNo to set
	 */
	public void setCheckDocNo(Boolean checkDocNo) {
		this.checkDocNo = checkDocNo;
	}

	/**
	 * @return the searchContractToDuration
	 */
	public Date getSearchContractToDuration() {
		return searchContractToDuration;
	}

	/**
	 * @param searchContractToDuration the searchContractToDuration to set
	 */
	public void setSearchContractToDuration(Date searchContractToDuration) {
		this.searchContractToDuration = searchContractToDuration;
	}

	/**
	 * @return the searchContractFromDuration
	 */
	public Date getSearchContractFromDuration() {
		return searchContractFromDuration;
	}

	/**
	 * @param searchContractFromDuration the searchContractFromDuration to set
	 */
	public void setSearchContractFromDuration(Date searchContractFromDuration) {
		this.searchContractFromDuration = searchContractFromDuration;
	}

	/**
	 * @return the searchTitle
	 */
	public String getSearchTitle() {
		return searchTitle;
	}

	/**
	 * @param searchTitle the searchTitle to set
	 */
	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	/**
	 * @return the searchSuppName
	 */
	public String getSearchSuppName() {
		return searchSuppName;
	}

	/**
	 * @param searchSuppName the searchSuppName to set
	 */
	public void setSearchSuppName(String searchSuppName) {
		this.searchSuppName = searchSuppName;
	}

	/**
	 * @return the docStatusList
	 */
	public List<PmStatusDesc> getDocStatusList() {
		return docStatusList;
	}

	/**
	 * @param docStatusList the docStatusList to set
	 */
	public void setDocStatusList(List<PmStatusDesc> docStatusList) {
		this.docStatusList = docStatusList;
	}

	/**
	 * @return the statusDescId
	 */
	public Long getStatusDescId() {
		return statusDescId;
	}

	/**
	 * @param statusDescId the statusDescId to set
	 */
	public void setStatusDescId(Long statusDescId) {
		this.statusDescId = statusDescId;
	}

	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * @return the searchProcurementTypeId
	 */
	public Long getSearchProcurementTypeId() {
		return searchProcurementTypeId;
	}

	/**
	 * @param searchProcurementTypeId the searchProcurementTypeId to set
	 */
	public void setSearchProcurementTypeId(Long searchProcurementTypeId) {
		this.searchProcurementTypeId = searchProcurementTypeId;
	}

	/**
	 * @return the checkDoInDocType
	 */
	public Boolean getCheckDoInDocType() {
		return checkDoInDocType;
	}

	/**
	 * @param checkDoInDocType the checkDoInDocType to set
	 */
	public void setCheckDoInDocType(Boolean checkDoInDocType) {
		this.checkDoInDocType = checkDoInDocType;
	}

	/**
	 * @return the procurementTypeList
	 */
	public List<PmParameterDesc> getProcurementTypeList() {
		return procurementTypeList;
	}

	/**
	 * @param procurementTypeList the procurementTypeList to set
	 */
	public void setProcurementTypeList(List<PmParameterDesc> procurementTypeList) {
		this.procurementTypeList = procurementTypeList;
	}

	/**
	 * @return the searchSuppDoRef
	 */
	public String getSearchSuppDoRef() {
		return searchSuppDoRef;
	}

	/**
	 * @param searchSuppDoRef the searchSuppDoRef to set
	 */
	public void setSearchSuppDoRef(String searchSuppDoRef) {
		this.searchSuppDoRef = searchSuppDoRef;
	}

	/**
	 * @return the searchSuppInvRef
	 */
	public String getSearchSuppInvRef() {
		return searchSuppInvRef;
	}

	/**
	 * @param searchSuppInvRef the searchSuppInvRef to set
	 */
	public void setSearchSuppInvRef(String searchSuppInvRef) {
		this.searchSuppInvRef = searchSuppInvRef;
	}

	/**
	 * @return the flRequisitioner
	 */
	public String getFlRequisitioner() {
		return flRequisitioner;
	}

	/**
	 * @param flRequisitioner the flRequisitioner to set
	 */
	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}

	/**
	 * @return the flRecevingOfficer
	 */
	public String getFlRecevingOfficer() {
		return flRecevingOfficer;
	}

	/**
	 * @param flRecevingOfficer the flRecevingOfficer to set
	 */
	public void setFlRecevingOfficer(String flRecevingOfficer) {
		this.flRecevingOfficer = flRecevingOfficer;
	}

	/**
	 * @return the flSupplier
	 */
	public String getFlSupplier() {
		return flSupplier;
	}

	/**
	 * @param flSupplier the flSupplier to set
	 */
	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}

	/**
	 * @return the flPmOfficer
	 */
	public String getFlPmOfficer() {
		return flPmOfficer;
	}

	/**
	 * @param flPmOfficer the flPmOfficer to set
	 */
	public void setFlPmOfficer(String flPmOfficer) {
		this.flPmOfficer = flPmOfficer;
	}

	/**
	 * @return the flApprover
	 */
	public String getFlApprover() {
		return flApprover;
	}

	/**
	 * @param flApprover the flApprover to set
	 */
	public void setFlApprover(String flApprover) {
		this.flApprover = flApprover;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		return approverMap;
	}

	/**
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}

	/**
	 * @return the checkDoDocType
	 */
	public Boolean getCheckDoDocType() {
		return checkDoDocType;
	}

	/**
	 * @param checkDoDocType the checkDoDocType to set
	 */
	public void setCheckDoDocType(Boolean checkDoDocType) {
		this.checkDoDocType = checkDoDocType;
	}

	/**
	 * @return the checkInDocType
	 */
	public Boolean getCheckInDocType() {
		return checkInDocType;
	}

	/**
	 * @param checkInDocType the checkInDocType to set
	 */
	public void setCheckInDocType(Boolean checkInDocType) {
		this.checkInDocType = checkInDocType;
	}

	/**
	 * @return the searchOrderNo
	 */
	public String getSearchOrderNo() {
		return searchOrderNo;
	}

	/**
	 * @param searchOrderNo the searchOrderNo to set
	 */
	public void setSearchOrderNo(String searchOrderNo) {
		this.searchOrderNo = searchOrderNo;
	}

	/**
	 * @return the flDocumentDataModel
	 */
	public IPagingDataModel<VFlDocument> getFlDocumentDataModel() {
		return flDocumentDataModel;
	}

}
