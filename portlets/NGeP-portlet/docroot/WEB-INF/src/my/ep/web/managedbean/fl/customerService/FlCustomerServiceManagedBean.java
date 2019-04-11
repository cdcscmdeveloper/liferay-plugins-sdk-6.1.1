/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.customerService;

import java.util.ArrayList;
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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.bean.ct.CustomerServiceEntity;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Managed Bean for Customer Service.
 *
 * @author Giribabu
 * @since 23/11/2016
 */
@ManagedBean(name = "flCustomerServiceManagedBean")
@SessionScoped
public class FlCustomerServiceManagedBean extends BaseManagedBean {
	
	/** The log. */
	private static Logger log = Logger.getLogger(FlCustomerServiceManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String searchType = "D"; // Define or Advanced
	
	private String searchOrderType; 
	private String searchDocType;
	private String searchDocNo;
	private String searchSuppName;
	private String searchOrderNo;
	
	private List<PmStatusDesc> docStatusList;
	private Long statusDescId;
	private PmUser pmUser;
	
	private List<VFlDocument> flDocumentList;
	
	private DualListModel<String> approverModel;
	
	private IPagingDataModel<VFlDocument> flDocumentDataModel = new FlDocumentPagingDataModel();
	
	private PmOrgProfile ptj;
	
	private Long ministryProfileId;
	private Long jabatanProfileId;
	private String ptjCodeName;
	private String ptjCodeNameSearchValues;
	
	private List<CustomerServiceEntity> customerServiceEntity;
	private String viewDocNo;
	private String viewOrdrNo;
	private String viewMinistry;
	private String viewKumpulanPTJ;
	private String viewPtjName;
	private String viewPtjCode;
	private String docType;
	private Long statusId;
	
	
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
				if(null == getPtj()){ 
					throw new EpException(MessageConst.ERROR_100022, 
							new String[] {ResourceBundleUtils.getMessage("lbl.fl.ptj", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}else {
				
					FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
					searchBean.setUseEntityClass(true);
					reqDTO.setSearchBean(searchBean);
					reqDTO.setPtj(getPtj());
					
					if(getSearchOrderType()!=null && getSearchOrderType()!="") {
						reqDTO.setOrderType(getSearchOrderType());
					}					
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
					if(getStatusId()!=null) {
						reqDTO.setStatusId(getStatusId());
					}
				
					reqDTO.setUserRoleList(getUserRoles());
					reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				
					IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,IFulfilmentDocumentSO.class);
					FlDocumentResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
				
					if(resDTO.isSuccessful()) {
						return resDTO.getResultBean();
					}	
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
	public void filterSearchResults(List<VFlDocument> searchResultList){}

	/**
	 * Gets the status info based on doc type and doc id.
	 *
	 * @param docId the doc id
	 * @param docType the doc type
	 * @return the status info
	 */
	//private FLOrderInfo getStatusInfo(Long docId,String docType){}
	
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
	/**
	 * Gets the doc type list by role.
	 *
	 * @return the doc type list by role
	 */
	public List<String> getDocTypeListByRole() {
		List<String> flDocTypes = new ArrayList<String>();
		if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
		}else if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getSearchOrderType())){
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
		}else{
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
		}
			
		return flDocTypes;
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
	 * @return the ministryProfileId
	 */
	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	/**
	 * @return the jabatanProfileId
	 */
	public Long getJabatanProfileId() {
		return jabatanProfileId;
	}

	/**
	 * @return the ptjCodeName
	 */
	public String getPtjCodeName() {
		return ptjCodeName;
	}

	/**
	 * @return the ptjCodeNameSearchValues
	 */
	public String getPtjCodeNameSearchValues() {
		return ptjCodeNameSearchValues;
	}

	/**
	 * @param ministryProfileId the ministryProfileId to set
	 */
	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	/**
	 * @param jabatanProfileId the jabatanProfileId to set
	 */
	public void setJabatanProfileId(Long jabatanProfileId) {
		this.jabatanProfileId = jabatanProfileId;
	}

	/**
	 * @param ptjCodeName the ptjCodeName to set
	 */
	public void setPtjCodeName(String ptjCodeName) {
		this.ptjCodeName = ptjCodeName;
	}

	/**
	 * @param ptjCodeNameSearchValues the ptjCodeNameSearchValues to set
	 */
	public void setPtjCodeNameSearchValues(String ptjCodeNameSearchValues) {
		this.ptjCodeNameSearchValues = ptjCodeNameSearchValues;
	}

	private List<String> getUserRoles() {
		List<String> roleList = new ArrayList<String>();
		
		// Supplier User
		roleList.add(RoleCode.SUPPORT_USER.value());
	return roleList;
	}
	
	public List<CustomerServiceEntity> getCustomerServiceEntity() {
		if(customerServiceEntity == null){
				customerServiceEntity = new ArrayList<CustomerServiceEntity>();
		}
		return customerServiceEntity;
	}

	public void setCustomerServiceEntity(
			List<CustomerServiceEntity> customerServiceEntity) {
		this.customerServiceEntity = customerServiceEntity;
	}

	/**
	 * @return the viewDocNo
	 */
	public String getViewDocNo() {
		return viewDocNo;
	}

	/**
	 * @return the viewOrdrNo
	 */
	public String getViewOrdrNo() {
		return viewOrdrNo;
	}

	/**
	 * @param viewDocNo the viewDocNo to set
	 */
	public void setViewDocNo(String viewDocNo) {
		this.viewDocNo = viewDocNo;
	}

	/**
	 * @param viewOrdrNo the viewOrdrNo to set
	 */
	public void setViewOrdrNo(String viewOrdrNo) {
		this.viewOrdrNo = viewOrdrNo;
	}

	/**
	 * @return the viewMinistry
	 */
	public String getViewMinistry() {
		return viewMinistry;
	}

	/**
	 * @return the viewKumpulanPTJ
	 */
	public String getViewKumpulanPTJ() {
		return viewKumpulanPTJ;
	}

	/**
	 * @return the viewPtjName
	 */
	public String getViewPtjName() {
		return viewPtjName;
	}

	/**
	 * @param viewMinistry the viewMinistry to set
	 */
	public void setViewMinistry(String viewMinistry) {
		this.viewMinistry = viewMinistry;
	}

	/**
	 * @param viewKumpulanPTJ the viewKumpulanPTJ to set
	 */
	public void setViewKumpulanPTJ(String viewKumpulanPTJ) {
		this.viewKumpulanPTJ = viewKumpulanPTJ;
	}

	/**
	 * @param viewPtjName the viewPtjName to set
	 */
	public void setViewPtjName(String viewPtjName) {
		this.viewPtjName = viewPtjName;
	}

	public String getViewPtjCode() {
		return viewPtjCode;
	}

	public void setViewPtjCode(String viewPtjCode) {
		this.viewPtjCode = viewPtjCode;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

}
