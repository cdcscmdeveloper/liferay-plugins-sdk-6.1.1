/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.sc.directpurchase;

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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;

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
					paramPtjGroupAndMinistry = "pmOrgProfile";
					paramTitle ="title";
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
				
				if (getSearchPtjProfile() != null && getSearchPtjProfile().getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(paramPtj, SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjProfile().getOrgProfileId()));
					
				} else if (getSearchPtjGroupProfile() != null && getSearchPtjGroupProfile().getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(
							paramPtjGroupAndMinistry+"."
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT
											.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjGroupProfile().getOrgProfileId()));
					
				} else if (getSearchMinistryProfile() != null && getSearchMinistryProfile().getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(
							paramPtjGroupAndMinistry+"."
									+ OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY
											.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							getSearchMinistryProfile().getOrgProfileId()));
				}
				
				if (StringUtils.isNotBlank(getSearchDocumentTitle())) {
					SearchFilterBean filterBean = new SearchFilterBean(paramTitle, SearchOperator.OPERATOR_LIKE.value(),
							getSearchDocumentTitle());
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				
				if(getSearchDocumentNo() != null && getSearchDocumentNo() != "") {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo", SearchOperator.OPERATOR_EQUALS.value(), getSearchDocumentNo()));
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
				
				ISourcingIntegrationSO sourcingIntegrationSO = (ISourcingIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
				SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
				reqDTO.setSourcingDocSearchBean(searchBean);
				SourcingIntegrationResDTO resDTO = sourcingIntegrationSO.searchSourcingDocument(reqDTO);
				return resDTO.getSourcingDocSearchResultBean();
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
}
