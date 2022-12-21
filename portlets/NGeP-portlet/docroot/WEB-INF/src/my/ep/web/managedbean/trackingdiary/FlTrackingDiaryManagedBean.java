/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Giribabu
 * @since 2/11/2013
 */
@ManagedBean(name = "flTrackingDiaryManagedBean")
@SessionScoped
public class FlTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(FlTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
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
	private Date searchOrderFromDuration;
	private Date searchOrderToDuration;
	private String searchOrderNo;
	private String searchPhysLoaNo;
	private String searchPhysContractNo;
	private Long statusDescId;
	private Long searchProcurementTypeId;
	private int searchFinancialYear;
	
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmUser pmUser;
	
	private VFlDocument vFlDocument;

	private List<VFlDocument> flDocumentList;
	
	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
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
				if(getSearchCircularNo()!=null && getSearchCircularNo()!="")  {
					reqDTO.setCircularNo(getSearchCircularNo());
				}
				if(getSearchLoaNo()!=null && getSearchLoaNo()!="") {
					reqDTO.setLoaNo(getSearchLoaNo());
				}
				if(getSearchPhysLoaNo()!=null && getSearchPhysLoaNo()!="") {
					reqDTO.setPhysicalLoaNo(getSearchPhysLoaNo());
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
				if(getSearchFinancialYear()!= 0){
					reqDTO.setFinancialYear(getSearchFinancialYear());
				}
				if( getPtj() != null){
					reqDTO.setPtj(getPtj());
				}
				if(getMinistry() != null){
					reqDTO.setMinistry(getMinistry());
				}
				if(isFlFulfilmentSupplier()){
					reqDTO.setUserRole(RoleCode.BASIC_SUPPLIER_USER.value());
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
					reqDTO.setOrgProfileId(orgProfileId);
				}
				
				reqDTO.setUserRole(getUserRole());
				
				//reqDTO.setOrgProfileId(getOrgProfile());
				reqDTO.setOrgProfileList(getNaikTarafChild(reqDTO.getOrgProfileId()));
				IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
				FlDocumentResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
				System.out.println("pageChangeListener");

				if(resDTO.isSuccessful()) {
					System.out.println("pageChangeListener"+ resDTO.getResultBean().getTotalRecord());
					return resDTO.getResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/*private Long getOrgProfile() {
			FacesScopes facescope = new FacesScopes();
			PmUser user = facescope.getUserSessionBean();
			UserReqDTO uReqDTO = new UserReqDTO();
			uReqDTO.setUserId(user.getUserId());
			IUserSO userSO = (IUserSO ) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO .class);
			UserResDTO uResDTO = userSO.retrieveUserOrgListByUserId(uReqDTO);
			if(!uResDTO.getUserOrgList().isEmpty()){
				return uResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId();
			}
			return null;
		}*/
		
		private List<Long> getNaikTarafChild(Long orgProfileId) {
			IOrgProfileSO orgProfileSO = (IOrgProfileSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(orgProfileId);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveProfileGroupByMainOrgProfileId(reqDTO);			
			if(resDTO.isSuccessful()){
				return resDTO.getOrgProfileIdList();
			}else{
				return new ArrayList<Long>();
			}
		}
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
		return facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()) || facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()) || facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value());
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
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);	
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
		} else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(getSearchOrderType())) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);				
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		}
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		return flDocTypes;
	}
	private class FLOrderInfo {
		private Long orderId;
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

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		
	}

	public List<VFlDocument> getFlDocumentList() {
		return flDocumentList;
	}

	public void setFlDocumentList(List<VFlDocument> flDocumentList) {
		this.flDocumentList = flDocumentList;
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

//	/**
//	 * @return the fulfilmentRequest
//	 */
//	public FlFulfilmentRequest getFulfilmentRequest() {
//		return fulfilmentRequest;
//	}
//
//	/**
//	 * @param fulfilmentRequest the fulfilmentRequest to set
//	 */
//	public void setFulfilmentRequest(FlFulfilmentRequest fulfilmentRequest) {
//		this.fulfilmentRequest = fulfilmentRequest;
//	}

//	/**
//	 * @return the checkDocType
//	 */
//	public Boolean getCheckDocType() {
//		return checkDocType;
//	}
//
//	/**
//	 * @param checkDocType the checkDocType to set
//	 */
//	public void setCheckDocType(Boolean checkDocType) {
//		this.checkDocType = checkDocType;
//	}
//
//	/**
//	 * @return the checkPoDocType
//	 */
//	public Boolean getCheckPoDocType() {
//		return checkPoDocType;
//	}
//
//	/**
//	 * @param checkPoDocType the checkPoDocType to set
//	 */
//	public void setCheckPoDocType(Boolean checkPoDocType) {
//		this.checkPoDocType = checkPoDocType;
//	}
//
//	/**
//	 * @return the checkPaDocType
//	 */
//	public Boolean getCheckPaDocType() {
//		return checkPaDocType;
//	}
//
//	/**
//	 * @param checkPaDocType the checkPaDocType to set
//	 */
//	public void setCheckPaDocType(Boolean checkPaDocType) {
//		this.checkPaDocType = checkPaDocType;
//	}
//
//	/**
//	 * @return the checkDocNo
//	 */
//	public Boolean getCheckDocNo() {
//		return checkDocNo;
//	}
//
//	/**
//	 * @param checkDocNo the checkDocNo to set
//	 */
//	public void setCheckDocNo(Boolean checkDocNo) {
//		this.checkDocNo = checkDocNo;
//	}

	/**
	 * @return the searchOrderToDuration
	 */
	public Date getSearchOrderToDuration() {
		return searchOrderToDuration;
	}

	/**
	 * @param searchOrderToDuration the searchOrderToDuration to set
	 */
	public void setSearchOrderToDuration(Date searchOrderToDuration) {
		this.searchOrderToDuration = searchOrderToDuration;
	}

	/**
	 * @return the searchOrderFromDuration
	 */
	public Date getSearchOrderFromDuration() {
		return searchOrderFromDuration;
	}

	/**
	 * @param searchOrderFromDuration the searchOrderFromDuration to set
	 */
	public void setSearchOrderFromDuration(Date searchOrderFromDuration) {
		this.searchOrderFromDuration = searchOrderFromDuration;
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

//	/**
//	 * @return the pmUser
//	 */
//	public PmUser getPmUser() {
//		return pmUser;
//	}
//
//	/**
//	 * @param pmUser the pmUser to set
//	 */
//	public void setPmUser(PmUser pmUser) {
//		this.pmUser = pmUser;
//	}

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
	 * @return the procurementTypeList
	 */

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

//	/**
//	 * @return the flRequisitioner
//	 */
//	public String getFlRequisitioner() {
//		return flRequisitioner;
//	}
//
//	/**
//	 * @param flRequisitioner the flRequisitioner to set
//	 */
//	public void setFlRequisitioner(String flRequisitioner) {
//		this.flRequisitioner = flRequisitioner;
//	}
//
//	/**
//	 * @return the flRecevingOfficer
//	 */
//	public String getFlRecevingOfficer() {
//		return flRecevingOfficer;
//	}
//
//	/**
//	 * @param flRecevingOfficer the flRecevingOfficer to set
//	 */
//	public void setFlRecevingOfficer(String flRecevingOfficer) {
//		this.flRecevingOfficer = flRecevingOfficer;
//	}
//
//	/**
//	 * @return the flSupplier
//	 */
//	public String getFlSupplier() {
//		return flSupplier;
//	}
//
//	/**
//	 * @param flSupplier the flSupplier to set
//	 */
//	public void setFlSupplier(String flSupplier) {
//		this.flSupplier = flSupplier;
//	}
//
//	/**
//	 * @return the flPmOfficer
//	 */
//	public String getFlPmOfficer() {
//		return flPmOfficer;
//	}
//
//	/**
//	 * @param flPmOfficer the flPmOfficer to set
//	 */
//	public void setFlPmOfficer(String flPmOfficer) {
//		this.flPmOfficer = flPmOfficer;
//	}
//
//	/**
//	 * @return the flApprover
//	 */
//	public String getFlApprover() {
//		return flApprover;
//	}
//
//	/**
//	 * @param flApprover the flApprover to set
//	 */
//	public void setFlApprover(String flApprover) {
//		this.flApprover = flApprover;
//	}
//
//	/**
//	 * @return the approverModel
//	 */
//	public DualListModel<String> getApproverModel() {
//		return approverModel;
//	}
//
//	/**
//	 * @param approverModel the approverModel to set
//	 */
//	public void setApproverModel(DualListModel<String> approverModel) {
//		this.approverModel = approverModel;
//	}
//
//	/**
//	 * @return the approverMap
//	 */
//	public HashMap<String, PmUserGroupUser> getApproverMap() {
//		return approverMap;
//	}
//
//	/**
//	 * @param approverMap the approverMap to set
//	 */
//	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
//		this.approverMap = approverMap;
//	}
//
//	/**
//	 * @return the checkDoDocType
//	 */
//	public Boolean getCheckDoDocType() {
//		return checkDoDocType;
//	}
//
//	/**
//	 * @param checkDoDocType the checkDoDocType to set
//	 */
//	public void setCheckDoDocType(Boolean checkDoDocType) {
//		this.checkDoDocType = checkDoDocType;
//	}
//
//	/**
//	 * @return the checkInDocType
//	 */
//	public Boolean getCheckInDocType() {
//		return checkInDocType;
//	}
//
//	/**
//	 * @param checkInDocType the checkInDocType to set
//	 */
//	public void setCheckInDocType(Boolean checkInDocType) {
//		this.checkInDocType = checkInDocType;
//	}

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
	/**
	 * @return the searchPhysLoaNo
	 */
	public String getSearchPhysLoaNo() {
		return searchPhysLoaNo;
	}
	/**
	 * @return the searchPhysContractNo
	 */
	public String getSearchPhysContractNo() {
		return searchPhysContractNo;
	}
	/**
	 * @param searchPhysLoaNo the searchPhysLoaNo to set
	 */
	public void setSearchPhysLoaNo(String searchPhysLoaNo) {
		this.searchPhysLoaNo = searchPhysLoaNo;
	}
	/**
	 * @param searchPhysContractNo the searchPhysContractNo to set
	 */
	public void setSearchPhysContractNo(String searchPhysContractNo) {
		this.searchPhysContractNo = searchPhysContractNo;
	}
	public VFlDocument getvFlDocument() {
		return vFlDocument;
	}
	public void setvFlDocument(VFlDocument vFlDocument) {
		this.vFlDocument = vFlDocument;
	}

	public void resetDocumentDataModel() {
		this.flDocumentDataModel = new FlDocumentPagingDataModel();
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
	 * Gets the user role.
	 *
	 * @return the user role
	 */
	private String getUserRole() {
		FacesScopes facesScopes = new FacesScopes();
		if (facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value()))
			return RoleCode.AUDITOR_GENERAL.value();
		else if (facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()))
			return RoleCode.AUDITOR_INTERNAL.value();
		else if (facesScopes.hasRoleUser(RoleCode.FL_APPROVER.value()))
			return RoleCode.FL_APPROVER.value();	
		else if (facesScopes.hasRoleUser(RoleCode.PAY_OFFICER.value()))
			return RoleCode.PAY_OFFICER.value();
		else if (facesScopes.hasRoleUser(RoleCode.REQUISITIONER.value()))
			return RoleCode.REQUISITIONER.value();	
		else if (facesScopes.hasRoleUser(RoleCode.RECEIVING_OFFICER.value()))
			return RoleCode.RECEIVING_OFFICER.value();	
		else if (facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();
		else if (facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return RoleCode.MOF_SUPPLIER_ADMIN.value();		
		else if (facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();
		else if (facesScopes.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();			
		else if (facesScopes.hasRoleUser(RoleCode.GOVT_SELLER.value()))
			return RoleCode.GOVT_SELLER.value();
		else if (facesScopes.hasRoleUser(RoleCode.FACTORING_ADMIN.value()))
			return RoleCode.FACTORING_ADMIN.value();	
		else if (facesScopes.hasRoleUser(RoleCode.FACTORING_USER.value()))
			return RoleCode.FACTORING_USER.value();	
		else
			return null;
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

}
