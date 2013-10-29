package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmFavoriteSupplier;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.cm.catalog.CmWishlistDtl;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

@ManagedBean(name = "favoriteSupplierMB")
@SessionScoped
public class FavoriteSupplierManagedBean extends BaseManagedBean {
	
	private List<CmFavoriteSupplier>  favoriteSupplier;

	public List<CmFavoriteSupplier> getFavoriteSupplier() {
		
		if(favoriteSupplier==null)
			favoriteSupplier = new ArrayList<CmFavoriteSupplier>();
		return favoriteSupplier;
	}

	public void setFavoriteSupplier(List<CmFavoriteSupplier> favoriteSupplier) {
		this.favoriteSupplier = favoriteSupplier;
	}
	
	
	
	
	
}
