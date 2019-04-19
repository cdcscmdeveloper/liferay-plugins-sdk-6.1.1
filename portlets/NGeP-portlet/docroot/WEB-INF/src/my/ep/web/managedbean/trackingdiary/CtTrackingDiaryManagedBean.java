/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * Managed Bean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name = "ctTrackingDiaryManagedBean")
@SessionScoped
public class CtTrackingDiaryManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(CtTrackingDiaryManagedBean.class);

	private static final long serialVersionUID = 1L;
	
	private String searchTransactionType;
	private String searchDocNo;
	private String searchDocType;
	private String searchPhysicalDocNo;	
	private String searchLoaNo;
	private String searchPhysicalLoaNo;
	private Long searchMinistryProfileId;
	private PmOrgProfile searchPtjProfile;
	private VCtDocument vCtDocument;
	private IPagingDataModel<VCtDocument> ctDocumentDataModel = new CtDocumentPagingDataModel();
	
	/**
	 * Pagination for available contract tracking diary search list
	 */
	private class CtDocumentPagingDataModel extends PagingDataModel<VCtDocument> {
		private static final long serialVersionUID = 1L;

		public CtDocumentPagingDataModel() {
			super("ctDocListDt",false);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VCtDocument> pageChangeListener(ISearchBean<VCtDocument> searchBean) {
			
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();				
				searchBean.setUseEntityClass(true);
				reqDTO.setCtDocumentSearchBean(searchBean);
				
				if (getSearchTransactionType() != null) {
					reqDTO.setTransactionType(getSearchTransactionType());
				}		
				
				if (getSearchDocNo() != null && getSearchDocNo().trim() != "") {
					reqDTO.setDocNo(getSearchDocNo());
				}
				if (getSearchDocType() != null) {
					reqDTO.setDocType(getSearchDocType());
				}
				if (getSearchPhysicalDocNo() != null && getSearchPhysicalDocNo().trim() != "") {
					reqDTO.setPhysicalDocNo(getSearchPhysicalDocNo());
				}
				if (getSearchLoaNo() != null && getSearchLoaNo().trim() != "") {
					reqDTO.setLoaNo(getSearchLoaNo());
				}
				if (getSearchPhysicalLoaNo() != null && getSearchPhysicalLoaNo().trim() != "") {
					reqDTO.setPhysicalLoaNo(getSearchPhysicalLoaNo());
				}
				if (getSearchPtjProfile() != null) {
					reqDTO.setPtjOrgId(getSearchPtjProfile().getOrgProfileId());
					
				} else if (getSearchMinistryProfileId() != null) {
					reqDTO.setMinistryOrgId(getSearchMinistryProfileId());
				}
				
				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);								
				ContractIntegrationResDTO resDTO = contractIntegrationSO.searchContractDocument(reqDTO);
				
				return resDTO.getCtDocumentSearchResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * @return the searchTransactionType
	 */
	public String getSearchTransactionType() {
		return searchTransactionType;
	}

	/**
	 * @param searchTransactionType the searchTransactionType to set
	 */
	public void setSearchTransactionType(String searchTransactionType) {
		this.searchTransactionType = searchTransactionType;
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
	 * @return the searchDocType
	 */
	public String getSearchDocType() {
		return searchDocType;
	}

	/**
	 * @param searchDocType the searchDocType to set
	 */
	public void setSearchDocType(String searchDocType) {
		this.searchDocType = searchDocType;
	}

	/**
	 * @return the searchPhysicalDocNo
	 */
	public String getSearchPhysicalDocNo() {
		return searchPhysicalDocNo;
	}

	/**
	 * @param searchPhysicalDocNo the searchPhysicalDocNo to set
	 */
	public void setSearchPhysicalDocNo(String searchPhysicalDocNo) {
		this.searchPhysicalDocNo = searchPhysicalDocNo;
	}

	/**
	 * @return the searchLoaNo
	 */
	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	/**
	 * @param searchLoaNo the searchLoaNo to set
	 */
	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
	}

	/**
	 * @return the searchPhysicalLoaNo
	 */
	public String getSearchPhysicalLoaNo() {
		return searchPhysicalLoaNo;
	}

	/**
	 * @param searchPhysicalLoaNo the searchPhysicalLoaNo to set
	 */
	public void setSearchPhysicalLoaNo(String searchPhysicalLoaNo) {
		this.searchPhysicalLoaNo = searchPhysicalLoaNo;
	}	

	/**
	 * @return the searchMinistryProfileId
	 */
	public Long getSearchMinistryProfileId() {
		return searchMinistryProfileId;
	}

	/**
	 * @param searchMinistryProfileId the searchMinistryProfileId to set
	 */
	public void setSearchMinistryProfileId(Long searchMinistryProfileId) {
		this.searchMinistryProfileId = searchMinistryProfileId;
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
	 * @return the vCtDocument
	 */
	public VCtDocument getvCtDocument() {
		return vCtDocument;
	}

	/**
	 * @param vCtDocument the vCtDocument to set
	 */
	public void setvCtDocument(VCtDocument vCtDocument) {
		this.vCtDocument = vCtDocument;
	}

	/**
	 * @return the ctDocumentDataModel
	 */
	public IPagingDataModel<VCtDocument> getCtDocumentDataModel() {
		return ctDocumentDataModel;
	}

	/**
	 * @param ctDocumentDataModel the ctDocumentDataModel to set
	 */
	public void setCtDocumentDataModel(IPagingDataModel<VCtDocument> ctDocumentDataModel) {
		this.ctDocumentDataModel = ctDocumentDataModel;
	}
	
	public void resetCtDocumentDataModel() {
		this.ctDocumentDataModel = new CtDocumentPagingDataModel();
	}
}
