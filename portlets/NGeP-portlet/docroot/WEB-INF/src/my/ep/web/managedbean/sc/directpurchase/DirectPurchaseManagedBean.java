/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.DPStatusConstants;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
// TODO: Auto-generated Javadoc

/**
 * The Class DirectPurchaseManagedBean.
 *
 * @author Sri
 * @since 29/12/2015
 */
@ManagedBean(name="directPurchaseManagedBean")
@SessionScoped
public class DirectPurchaseManagedBean extends BaseManagedBean{
	private static Logger log = Logger.getLogger(DirectPurchaseManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	/** The search document type. */
	private String searchDocumentType; 
	
	/** The search doc no. */
	private String searchDocNo;
	
	/** The search document no. */
	private String searchDocumentNo;
	
	/** The search document title. */
	private String searchDocumentTitle;
	
	/** The search status id. */
	private Long searchStatusId;
	
	/** The search document from duration. */
	private Date searchDocumentFromDuration;
	
	/** The search document to duration. */
	private Date searchDocumentToDuration;
	
	/** The dp obj. */
	private Object[] dpObj = new Object[6];
	
	/** The dp document data model. */
	private IPagingDataModel<Object[]> dpDocumentDataModel = new DocumentPagingDataModel();
	
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
	
	private boolean searchDPM;
	
	//#22211 - start
	private List<String> loaList;
	
	private boolean searchLoaByRN;
	//#22211 - end
	// Redmine 25396:
	private String searchFunctionType;
	
	private boolean isG2G;
	
	private boolean viewAsPTJ;
	
	private boolean viewAsSupplier;
	
	//Start - #25396
	private boolean disableMinistryPtjLov = false;
	private boolean showMinistryPtjField = true;
	//End - #25396

	/**
	 * The Class DocumentPagingDataModel.
	 */
	private class DocumentPagingDataModel extends PagingDataModel<Object[]> {
		private static final long serialVersionUID = 1L;

		public DocumentPagingDataModel() {
			super("dpDocumentTableID",false);
		}
		@Override
		public ISearchResultBean<Object[]> pageChangeListener(ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				if(null == getSearchDocumentType()){ 
					throw new EpException(MessageConst.ERROR_740023);
				}
				String paramPtj = "";
				String paramPtjGroupAndMinistry = "";
				String paramTitle = "";

				if(getSearchDocumentType() != null && getSearchDocumentType() != "") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(), getSearchDocumentType()));
					if(getSearchDocumentType().equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("MOD", SearchOperator.OPERATOR_EQUALS.value(), ModuleCodeConst.DIRECT_PURCHASE));
					}
				}
				
				if(ParameterConst.DOCTYPE_REQUEST_NOTE.equals(getSearchDocumentType())){
					paramPtj = "pmOrgProfile.orgProfileId";
					paramTitle ="title";
					if (isSearchDPM()) {
						if (getSearchStatusId().equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)) {
							paramPtj = "requestNote.pmOrgProfile.orgProfileId";
							paramTitle ="requestNote.title";
						} 
					} 
					paramPtjGroupAndMinistry = "pmOrgProfile";
				} else if(ParameterConst.DOCTYPE_SIMPLE_QUOTE.equals(getSearchDocumentType())){
					paramPtj = "requestNotes.pmOrgProfile.orgProfileId";
					paramPtjGroupAndMinistry = "requestNotes.pmOrgProfile";
					paramTitle ="title";
				} else if(ParameterConst.DOCTYPE_PURCHASE_INQUIRY.equals(getSearchDocumentType())){
					paramPtj = "scRequestSupplierItems.requestNoteItem.requestNote.pmOrgProfile.orgProfileId";
					paramPtjGroupAndMinistry = "scRequestSupplierItems.requestNoteItem.requestNote.pmOrgProfile";
					paramTitle ="title";
				} else if(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT.equals(getSearchDocumentType())){
					paramPtj = "pmOrgProfile.orgProfileId";
					paramPtjGroupAndMinistry = "pmOrgProfile";
					paramTitle ="title";
				} else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(getSearchDocumentType())){
					paramPtj = "scLoiLoa.pmOrgProfile.orgProfileId";
					paramPtjGroupAndMinistry = "scLoiLoa.pmOrgProfile";
					paramTitle ="scLoiLoa.title";
				}
				
				//#25396 - start
				if(isGovernment()) {
					if (getSearchPtjProfile() != null && getSearchPtjProfile().getOrgProfileId() != null) {
						System.out.println("25936 test1 ");
						searchBean.getDataFilterBeans().add(new SearchFilterBean(paramPtj, SearchOperator.OPERATOR_EQUALS.value(),
								getSearchPtjProfile().getOrgProfileId()));
						
					} else if (getSearchPtjGroupProfile() != null && getSearchPtjGroupProfile().getOrgProfileId() != null) {
						System.out.println("25936 test2 ");
						searchBean.getDataFilterBeans().add(new SearchFilterBean(
								paramPtjGroupAndMinistry+"."
										+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT
												.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								getSearchPtjGroupProfile().getOrgProfileId()));
						
					} else if (getSearchMinistryProfile() != null && getSearchMinistryProfile().getOrgProfileId() != null) {
						System.out.println("25936 test3 ");
						searchBean.getDataFilterBeans().add(new SearchFilterBean(
								paramPtjGroupAndMinistry+"."
										+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY
												.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								getSearchMinistryProfile().getOrgProfileId()));
					}
				}
				//#25396 - end
				
				
				if (StringUtils.isNotBlank(getSearchDocumentTitle())) {
					SearchFilterBean filterBean = new SearchFilterBean(paramTitle, SearchOperator.OPERATOR_LIKE.value(),
							getSearchDocumentTitle());
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				
				if(getSearchDocumentNo() != null && getSearchDocumentNo() != "") {
					if (isSearchLoaByRN()) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo", SearchOperator.OPERATOR_IN.value(), getLoaList()));
					}else{
						searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo", SearchOperator.OPERATOR_EQUALS.value(), getSearchDocumentNo()));
					}
				}
				if(getSearchStatusId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status", SearchOperator.OPERATOR_EQUALS.value(), getSearchStatusId()));
				}
				
				if(getSearchDocumentFromDuration() != null && getSearchDocumentToDuration() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("createdDate", SearchOperator.OPERATOR_BETWEEN.value(), 
							new SearchBetweenValue<Date>(getSearchDocumentFromDuration(),
									 getSearchDocumentToDuration())));
				}
				
				if(isReqOrApprover()){
					// Removed the restriction condition 
					if (CollectionUtils.isNotEmpty(searchBean
							.getDataFilterMetadatas())) {
						searchBean.getDataFilterMetadatas().clear();
					}
				}
				
				if(!isGovernment()) {
					// Filter By - supplierId
					ISupplierSearchSO supplierSearchSO = null;
					try {
						supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

						SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
						supplierSearchReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
						List<SmSupplier> smSuppliers = supplierSearchSO
								.retriveSupplierProfiles(supplierSearchReqDTO)
								.getResultBean().getResultList();
						
						if (CollectionUtils.isNotEmpty(smSuppliers)) {
							List<Long> supplierIds = new ArrayList<Long>();
							for (SmSupplier smSupplier : smSuppliers) {
								supplierIds.add(smSupplier.getSupplierId());
							}
							reqDTO.setSupplierIds(supplierIds);
						}
						//#25396 - start
						else{
							if(isViewAsSupplier()){
								List<Long> supplierIds = new ArrayList<Long>();
								supplierIds.add(supplierSearchReqDTO.getUserId());
								reqDTO.setSupplierIds(supplierIds);
							}
						}							
						//#25396 - end

					} finally {
						supplierSearchSO = null;
					}
				}
				
				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = null;
				if (isSearchDPM()) {
					if (getSearchStatusId().equals(DPStatusConstants.RN_PENDING_ITEM_CODIFICATION)) {
						resDTO = sourcingIntegrationSO.searchSourcingDocumentForDPM(reqDTO);
					} else {
						resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
					}
				} else {
					resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
				}
				
				if (null != resDTO) {
					return resDTO.getSourcingDocSearchResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
    /**
     * Checks if is requisitioner or approver.
     *
     * @return true, if is requisitioner or approver
     */
    public boolean isReqOrApprover() {
    	FacesScopes fc = new FacesScopes();
		return (fc.hasRoleUser(RoleCode.REQUISITIONER.value()) 
				|| fc.hasRoleUser(RoleCode.RN_APPROVER.value()));
	}

	/**
	 * @return the searchDocumentType
	 */
	public String getSearchDocumentType() {
		return searchDocumentType;
	}

	/**
	 * @param searchDocumentType the searchDocumentType to set
	 */
	public void setSearchDocumentType(String searchDocumentType) {
		this.searchDocumentType = searchDocumentType;
	}

	/**
	 * @return the searchDocNo
	 */
	public String getSearchDocNo() {
		return searchDocNo;
	}

	/**
	 * @param searchDocNo the searchDocNo to set
	 */
	public void setSearchDocNo(String searchDocNo) {
		this.searchDocNo = searchDocNo;
	}

	/**
	 * @return the searchDocumentNo
	 */
	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}

	/**
	 * @param searchDocumentNo the searchDocumentNo to set
	 */
	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}

	/**
	 * @return the searchDocumentTitle
	 */
	public String getSearchDocumentTitle() {
		return searchDocumentTitle;
	}

	/**
	 * @param searchDocumentTitle the searchDocumentTitle to set
	 */
	public void setSearchDocumentTitle(String searchDocumentTitle) {
		this.searchDocumentTitle = searchDocumentTitle;
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
	 * @return the searchDocumentFromDuration
	 */
	public Date getSearchDocumentFromDuration() {
		return searchDocumentFromDuration;
	}

	/**
	 * @param searchDocumentFromDuration the searchDocumentFromDuration to set
	 */
	public void setSearchDocumentFromDuration(Date searchDocumentFromDuration) {
		this.searchDocumentFromDuration = searchDocumentFromDuration;
	}

	/**
	 * @return the searchDocumentToDuration
	 */
	public Date getSearchDocumentToDuration() {
		return searchDocumentToDuration;
	}

	/**
	 * @param searchDocumentToDuration the searchDocumentToDuration to set
	 */
	public void setSearchDocumentToDuration(Date searchDocumentToDuration) {
		this.searchDocumentToDuration = searchDocumentToDuration;
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
	 * @return the searchDPM
	 */
	public boolean isSearchDPM() {
		return searchDPM;
	}

	/**
	 * @param searchDPM the searchDPM to set
	 */
	public void setSearchDPM(boolean searchDPM) {
		this.searchDPM = searchDPM;
	}
		
	// Redmine 25396: remove duplication option view by adding new drop down field
	public String getSearchFunctionType() {
		return searchFunctionType;
	}

	public void setSearchFunctionType(String searchFunctionType) {
		this.searchFunctionType = searchFunctionType;
	}
	// end 25396:

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

	public boolean isSearchLoaByRN() {
		return searchLoaByRN;
	}

	public void setSearchLoaByRN(boolean searchLoaByRN) {
		this.searchLoaByRN = searchLoaByRN;
	}

	public List<String> getLoaList() {
		return loaList;
	}

	public void setLoaList(List<String> loaList) {
		this.loaList = loaList;
	}
	
	//Start - #25396
	public boolean isViewAsPTJ() {
		return viewAsPTJ;
	}

	public void setViewAsPTJ(boolean viewAsPTJ) {
		this.viewAsPTJ = viewAsPTJ;
	}

	public boolean isViewAsSupplier() {
		return viewAsSupplier;
	}
	
	public void setViewAsSupplier(boolean viewAsSupplier) {
		this.viewAsSupplier = viewAsSupplier;
	}
	//End - #25396

	/**
	 * @return the dpmDocumentDataModel
	 *//*
	public IPagingDataModel<Object[]> getDpmDocumentDataModel() {
		return dpmDocumentDataModel;
	}

	*//**
	 * @param dpmDocumentDataModel the dpmDocumentDataModel to set
	 *//*
	public void setDpmDocumentDataModel(IPagingDataModel<Object[]> dpmDocumentDataModel) {
		this.dpmDocumentDataModel = dpmDocumentDataModel;
	}*/
	
	//Start - #25396
	public boolean isDisableMinistryPtjLov() {
		return disableMinistryPtjLov; 
	}
	
	public void setDisableMinistryPtjLov(boolean disableMinistryPtjLov) {
		this.disableMinistryPtjLov = disableMinistryPtjLov;
	}
	
	public boolean isShowMinistryPtjField() {
		return showMinistryPtjField; 
	}
	
	public void setShowMinistryPtjField(boolean showMinistryPtjField) {
		this.showMinistryPtjField = showMinistryPtjField;
	}
	//End - #25396
}
