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

@ManagedBean(name = "reviewCatMB")
@SessionScoped
public class ReviewCatalogueManagedBean extends BaseManagedBean {
	
	private List<CmSupplierItem> reviewSupplierItems;
	
	private CmSupplierItem selectedReviewCat;
	
	private boolean rejectReason = false;
	
	private Integer catDetTabActiveIndex = 0;
	
	
	
	
	

	public CmSupplierItem getSelectedReviewCat() {
		return selectedReviewCat;
	}

	public void setSelectedReviewCat(CmSupplierItem selectedReviewCat) {
		this.selectedReviewCat = selectedReviewCat;
	}

	public List<CmSupplierItem> getReviewSupplierItems() {
		if(reviewSupplierItems==null)
			reviewSupplierItems = new ArrayList<CmSupplierItem>();
		return reviewSupplierItems;
	}

	public void setReviewSupplierItems(List<CmSupplierItem> reviewSupplierItems) {
		this.reviewSupplierItems = reviewSupplierItems;
	}

	public boolean isRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(boolean rejectReason) {
		this.rejectReason = rejectReason;
	}

	public Integer getCatDetTabActiveIndex() {
		return catDetTabActiveIndex;
	}

	public void setCatDetTabActiveIndex(Integer catDetTabActiveIndex) {
		this.catDetTabActiveIndex = catDetTabActiveIndex;
	}
	
	
	

	
}
