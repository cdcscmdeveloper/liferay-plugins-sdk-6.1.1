package my.ep.web.backingbean.cm.catalogue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
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

		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("itemStatus");
		statusFilterBean.setSearchValue("P");
		searchBean.getDataFilterBeans().add(statusFilterBean);



		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);


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
		reqDTO.getSaveSupplierItems().add(bb.getManagedBean().getSelectedMaintainItem());

		catalogueSO.updateSupplierItem(reqDTO);
		
		return "catalogueTaskList?faces-redirect=true";

	}

	public String rejectCatalogueItem(){

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		CatalogueBackingBean bb = new CatalogueBackingBean();
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		bb.getManagedBean().getSelectedMaintainItem().setItemStatus("R");
		reqDTO.getSaveSupplierItems().add(bb.getManagedBean().getSelectedMaintainItem());

		catalogueSO.updateSupplierItem(reqDTO);
		
		return "catalogueTaskList?faces-redirect=true";

	}






}
