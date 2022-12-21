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

// TODO: Auto-generated Javadoc
/**
 * The Class WishListManagedBean.
 * @author hketan
 */
@ManagedBean(name = "wishListMB")
@SessionScoped
public class WishListManagedBean extends BaseManagedBean {

	/** The wish list. */
	private CmWishlist wishList;

	/** The selected wish lists. */
	private CmWishlist[] selectedWishLists;
	
	/** The wish list det. */
	private List<CmWishlistDtl> wishListDet;
	
	/** The selected wish list. */
	private CmWishlist selectedWishList;


	



	/**
	 * Gets the wish list det.
	 * 
	 * @return the wish list det
	 */
	public List<CmWishlistDtl> getWishListDet() {
		if(wishListDet==null)
			wishListDet = new ArrayList<CmWishlistDtl>();
		return wishListDet;
	}

	/**
	 * Sets the wish list det.
	 * 
	 * @param wishListDet
	 *            the new wish list det
	 */
	public void setWishListDet(List<CmWishlistDtl> wishListDet) {
		this.wishListDet = wishListDet;
	}

	/**
	 * Gets the selected wish lists.
	 * 
	 * @return the selected wish lists
	 */
	public CmWishlist[] getSelectedWishLists() {
		return selectedWishLists;
	}

	/**
	 * Sets the selected wish lists.
	 * 
	 * @param selectedWishLists
	 *            the new selected wish lists
	 */
	public void setSelectedWishLists(CmWishlist[] selectedWishLists) {
		this.selectedWishLists = selectedWishLists;
	}

	/**
	 * Gets the wish list.
	 * 
	 * @return the wish list
	 */
	public CmWishlist getWishList() {
		if(wishList==null)
			wishList = new CmWishlist();
		return wishList;
	}

	/**
	 * Sets the wish list.
	 * 
	 * @param wishList
	 *            the new wish list
	 */
	public void setWishList(CmWishlist wishList) {
		this.wishList = wishList;
	}

	/**
	 * Gets the selected wish list.
	 * 
	 * @return the selected wish list
	 */
	public CmWishlist getSelectedWishList() {
		return selectedWishList;
	}

	/**
	 * Sets the selected wish list.
	 * 
	 * @param selectedWishList
	 *            the new selected wish list
	 */
	public void setSelectedWishList(CmWishlist selectedWishList) {
		this.selectedWishList = selectedWishList;
	}



}
