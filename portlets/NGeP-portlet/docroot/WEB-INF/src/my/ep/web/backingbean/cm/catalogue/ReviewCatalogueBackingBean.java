package my.ep.web.backingbean.cm.catalogue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.ReviewCatalogueManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="reviewCatBackingBean")
@RequestScoped
public class ReviewCatalogueBackingBean extends BaseBackingBean<ReviewCatalogueManagedBean>{

	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());


	public ReviewCatalogueBackingBean(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();


		reqDTO.setItemStatus("P");
		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		this.getManagedBean().setReviewSupplierItems(resDTO.getSupplierItemList());

	}



	public String navigateToCatalogDet(){

		System.out.println("************navigateToCatalogDet*************");
		CatalogueBackingBean bb = new CatalogueBackingBean();

		bb.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getSelectedReviewCat());

		return "addCatalogItem";

	}


	public String approveCatalogueItem(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		bb.getManagedBean().getSelectedMaintainItem().setItemStatus("A");
		
		
		
		for(CmZonalStandardPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}

		for(CmZonalPromotionPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}


		for(CmZonalTierPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}

		for(CmZonalPromotionTierPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}
		
		
		reqDTO.getSaveSupplierItems().add(bb.getManagedBean().getSelectedMaintainItem());

		try {
			catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "catalogueTaskList?faces-redirect=true";

	}

	public String rejectCatalogueItem(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		bb.getManagedBean().getSelectedMaintainItem().setItemStatus("R");
		
		
		for(CmZonalStandardPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalStandardPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}

		for(CmZonalPromotionPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}


		for(CmZonalTierPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalTierPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}

		for(CmZonalPromotionTierPricing promotion: bb.getManagedBean().getSelectedMaintainItem().getCmZonalPromotionTierPricings()){

			promotion.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			if(promotion.getPmDistrict().getDistrictId() == null) {
				promotion.setPmDistrict(null);
			}
		}
		
		
		
		
		reqDTO.getSaveSupplierItems().add(bb.getManagedBean().getSelectedMaintainItem());

		try {
			catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "catalogueTaskList?faces-redirect=true";

	}






}
