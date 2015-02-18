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
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.cm.catalog.CmWishlistDtl;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;


/**
 * The Class ReviewCatalogueManagedBean.
 * @author hketan
 */
@ManagedBean(name = "reviewCatMB")
@SessionScoped
public class ReviewCatalogueManagedBean extends BaseManagedBean {
	
	/** The review supplier items. */
	private List<CmSupplierItem> reviewSupplierItems;
	
	/** The selected review cat. */
	private CmSupplierItem selectedReviewCat;
	
	/** The reject reason. */
	private boolean rejectReason = false;
	
	/** The cat det tab active index. */
	private Integer catDetTabActiveIndex = 0;
	
	
	
	
	

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
	 * Gets the review supplier items.
	 * 
	 * @return the review supplier items
	 */
	public List<CmSupplierItem> getReviewSupplierItems() {
		if(reviewSupplierItems==null)
			reviewSupplierItems = new ArrayList<CmSupplierItem>();
		return reviewSupplierItems;
	}

	/**
	 * Sets the review supplier items.
	 * 
	 * @param reviewSupplierItems
	 *            the new review supplier items
	 */
	public void setReviewSupplierItems(List<CmSupplierItem> reviewSupplierItems) {
		this.reviewSupplierItems = reviewSupplierItems;
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
	
	
	

	
}
