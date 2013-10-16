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

@ManagedBean(name = "wishListMB")
@SessionScoped
public class WishListManagedBean extends BaseManagedBean {

	private CmWishlist wishList;

	private CmWishlist[] selectedWishLists;
	
	private List<CmWishlistDtl> wishListDet;


	



	public List<CmWishlistDtl> getWishListDet() {
		if(wishListDet==null)
			wishListDet = new ArrayList<CmWishlistDtl>();
		return wishListDet;
	}

	public void setWishListDet(List<CmWishlistDtl> wishListDet) {
		this.wishListDet = wishListDet;
	}

	public CmWishlist[] getSelectedWishLists() {
		return selectedWishLists;
	}

	public void setSelectedWishLists(CmWishlist[] selectedWishLists) {
		this.selectedWishLists = selectedWishLists;
	}

	public CmWishlist getWishList() {
		if(wishList==null)
			wishList = new CmWishlist();
		return wishList;
	}

	public void setWishList(CmWishlist wishList) {
		this.wishList = wishList;
	}



}
