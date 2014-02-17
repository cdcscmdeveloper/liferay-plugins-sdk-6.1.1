package my.ep.web.backingbean.cm.catalogue;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.persistence.entity.cm.catalog.CmItemRejectReason;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalPromotionTierPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalStandardPricing;
import my.ep.persistence.entity.cm.catalog.CmZonalTierPricing;
import my.ep.persistence.entity.pm.common.PmStatus;
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
		
		
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);

		StatusReqDTO statusReqDTO = new StatusReqDTO();

		statusReqDTO.setDocType("CMR");
		statusReqDTO.setModuleCode("CM");
		statusReqDTO.setLanguageCode("en");

		StatusResDTO resDTO = statusSO.retrieveStatusDescList(statusReqDTO);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		bb.getManagedBean().setStatusDescList(resDTO.getStatusDescList());
		
		bb.getManagedBean().setSelectedMaintainItem(this.getManagedBean().getSelectedReviewCat());
		bb.getManagedBean().setRejectReasonIds(new ArrayList<Long>());
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
		this.getManagedBean().setRejectReason(false);
		
		bb.getManagedBean().getSelectedMaintainItem().setReviewRequired(false);
		System.out.println("bb.getManagedBean().getRejectReasonIds(): " + bb.getManagedBean().getRejectReasonIds());
		
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
		

		
	
		for(int i = 0; i < bb.getManagedBean().getRejectReasonIds().size(); i++) {
			Long id = Long.valueOf(String.valueOf(bb.getManagedBean().getRejectReasonIds().get(i)));
			
//			Long id = new Long(bb.getManagedBean().getRejectReasonIds().get(i));
			CmItemRejectReason reason = new CmItemRejectReason();
			PmStatus reasonStatus = new PmStatus();
			reasonStatus.setStatusId(id);
			reasonStatus.setBrandNew(false);
			
			reason.setRemark(bb.getManagedBean().getRejectReason());
			reason.setCmSupplierItem(bb.getManagedBean().getSelectedMaintainItem());
			reason.setPmStatus(reasonStatus);
			bb.getManagedBean().getSelectedMaintainItem().getCmItemRejectReasons().add(reason);			
		}
		

		
		reqDTO.getSaveSupplierItems().add(bb.getManagedBean().getSelectedMaintainItem());


		if(bb.getManagedBean().getRejectReasonIds().isEmpty()){
			
			this.getManagedBean().setCatDetTabActiveIndex(2);
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR," Item Reject Reason Required!", ""));
			
			return "addCatalogView";
		}
		
		
		try {
			catalogueSO.updateSupplierItem(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "catalogueTaskList?faces-redirect=true";

	}
	
	
	public String cancelToPendingApprovalItem(){
		
		
		return "catalogueTaskList";
	}






}
