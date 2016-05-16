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

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.catalogue.ReviewCatalogueBackingBean;
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

	private String itemName;
	private String supplierName;





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


				ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();




				if(bb.getManagedBean().getSupplierName()!=null && bb.getManagedBean().getSupplierName().trim().length()>0)
					reqDTO.setSupplierName(bb.getManagedBean().getSupplierName());

				if(bb.getManagedBean().getItemName()!=null && bb.getManagedBean().getItemName().trim().length()>0)
					reqDTO.setItemName(bb.getManagedBean().getItemName());

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

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}



	private IPagingDataModel<CmSupplierItem> suppItemDashboardSearchDataModel = new SuppItemDashboardSearchDataModel();


	private class SuppItemDashboardSearchDataModel extends PagingDataModel<CmSupplierItem> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public SuppItemDashboardSearchDataModel() {
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


				ReviewCatalogueBackingBean bb = new ReviewCatalogueBackingBean();


				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

				SmSupplier smSupplier = null;


				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());
				supplierReqDTO.setAllStatuses(true);

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);

				if(smResDTO.getResultBean().getResultList().isEmpty()) {
					throw new EpException(MessageConst.ERROR_721017);
				}


				smSupplier = smResDTO.getResultBean().getResultList().get(0);

				List<String> itemStatusLs = new ArrayList<String>();

				itemStatusLs.add("A"); ///approved
				itemStatusLs.add("R"); ///rejected

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("itemStatus",
								SearchOperator.OPERATOR_IN.value(),itemStatusLs ));


				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("smSupplier.supplierId",
								SearchOperator.OPERATOR_EQUALS.value(), smSupplier.getSupplierId()));
								
								searchBean.getDataFilterBeans().add(
										new SearchFilterBean("recordStatus",
												SearchOperator.OPERATOR_EQUALS.value(), true));

				reqDTO.setSearchBean(searchBean);
				CatalogueResDTO resDTO = catalogueSO.retrieveSupplierItemDashBoard(reqDTO);
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
	 * @return the suppItemDashboardSearchDataModel
	 */
	public IPagingDataModel<CmSupplierItem> getSuppItemDashboardSearchDataModel() {
		return suppItemDashboardSearchDataModel;
	}

	/**
	 * @param suppItemDashboardSearchDataModel the suppItemDashboardSearchDataModel to set
	 */
	public void setSuppItemDashboardSearchDataModel(
			IPagingDataModel<CmSupplierItem> suppItemDashboardSearchDataModel) {
		this.suppItemDashboardSearchDataModel = suppItemDashboardSearchDataModel;
	}






}
