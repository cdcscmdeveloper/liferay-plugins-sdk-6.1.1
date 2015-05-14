/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.type.aut.SearchOperator;
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
				if (getSearchTransactionType() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("transactionType",SearchOperator.OPERATOR_EQUALS.value(),getSearchTransactionType()));
				}				
				if (getSearchDocNo() != null && getSearchDocNo().trim() != "") {
					SearchFilterBean filterBean = new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getSearchDocNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getSearchDocType() != null) {
					SearchFilterBean filterBean = new SearchFilterBean("docType",SearchOperator.OPERATOR_LIKE.value(),getSearchDocType());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getSearchPhysicalDocNo() != null && getSearchPhysicalDocNo().trim() != "") {
					SearchFilterBean filterBean = new SearchFilterBean("physicalDocNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalDocNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getSearchLoaNo() != null && getSearchLoaNo().trim() != "") {
					SearchFilterBean filterBean = new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),getSearchLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getSearchPhysicalLoaNo() != null && getSearchPhysicalLoaNo().trim() != "") {
					SearchFilterBean filterBean = new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),getSearchPhysicalLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				// Order by document number
				searchBean.getSortColumns().add(new SearchOrderBean("docNo", true));
				
				IContractIntegrationSO contractIntegrationSO = (IContractIntegrationSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);
				ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
				
				reqDTO.setCtDocumentSearchBean(searchBean);
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
