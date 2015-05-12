/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.cm.catalogue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;


/**
 * The Class ReviewCatalogueManagedBean.
 * @author hketan
 */
@ManagedBean(name = "reviewCatMB")
@SessionScoped
public class ReviewCatalogueManagedBean extends BaseManagedBean {
	
	
	
	/** The selected review cat. */
	private CmSupplierItem selectedReviewCat;
	
	/** The reject reason. */
	private boolean rejectReason = false;
	
	/** The cat det tab active index. */
	private Integer catDetTabActiveIndex = 0;
	
	
	private IPagingDataModel<CmSupplierItem> approvalSearchDataModel = new ApprovalSearchPagingDataModel();
	
	
	private class ApprovalSearchPagingDataModel extends PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public ApprovalSearchPagingDataModel() {
			super("catalogueTableId");
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * @param  searchBean 
		 *                  (ISearchBean<CmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<CmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
					.getInstance().getServiceObject("cm", ICatalogueSO.class);

			CatalogueReqDTO reqDTO = new CatalogueReqDTO();

			
			try {
				
				
				reqDTO.setItemStatus("P");
				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);
				System.out.println("resDTO.getSupplierItemList():"
						+ resDTO.getSupplierItemList().size());

				return resDTO.getSuppItemResultBean();

			}catch(Exception e){
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				catalogueSO = null;
				
			}
		}

	}
	
	

	/**
	 * Gets the selected review cat.
	 * 
	 * @return the selected review cat
	 */
	public CmSupplierItem getSelectedReviewCat() {
		return selectedReviewCat;
	}

	/**
	 * Sets the selected review cat.
	 * 
	 * @param selectedReviewCat
	 *            the new selected review cat
	 */
	public void setSelectedReviewCat(CmSupplierItem selectedReviewCat) {
		this.selectedReviewCat = selectedReviewCat;
	}


	/**
	 * Checks if is reject reason.
	 * 
	 * @return true, if is reject reason
	 */
	public boolean isRejectReason() {
		return rejectReason;
	}

	/**
	 * Sets the reject reason.
	 * 
	 * @param rejectReason
	 *            the new reject reason
	 */
	public void setRejectReason(boolean rejectReason) {
		this.rejectReason = rejectReason;
	}

	/**
	 * Gets the cat det tab active index.
	 * 
	 * @return the cat det tab active index
	 */
	public Integer getCatDetTabActiveIndex() {
		return catDetTabActiveIndex;
	}

	/**
	 * Sets the cat det tab active index.
	 * 
	 * @param catDetTabActiveIndex
	 *            the new cat det tab active index
	 */
	public void setCatDetTabActiveIndex(Integer catDetTabActiveIndex) {
		this.catDetTabActiveIndex = catDetTabActiveIndex;
	}

	public IPagingDataModel<CmSupplierItem> getApprovalSearchDataModel() {
		return approvalSearchDataModel;
	}

	public void setApprovalSearchDataModel(
			IPagingDataModel<CmSupplierItem> approvalSearchDataModel) {
		this.approvalSearchDataModel = approvalSearchDataModel;
	}
	
	
	
	
	

	
}
