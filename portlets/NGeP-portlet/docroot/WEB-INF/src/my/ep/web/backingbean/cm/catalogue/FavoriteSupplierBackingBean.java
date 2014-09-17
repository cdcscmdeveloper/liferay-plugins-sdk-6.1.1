package my.ep.web.backingbean.cm.catalogue;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.catalogue.IFavoriteSupplierSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.catalogue.FavoriteSupplierReqDTO;
import my.ep.dto.cm.catalogue.FavoriteSupplierResDTO;
import my.ep.persistence.entity.cm.catalog.CmFavoriteSupplier;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.FavoriteSupplierManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="favoriteSupplierBackingBean")
@RequestScoped
public class FavoriteSupplierBackingBean extends BaseBackingBean<FavoriteSupplierManagedBean>{

	private static Logger log = Logger.getLogger(FavoriteSupplierBackingBean.class);
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	public FavoriteSupplierBackingBean(){
		log.info("*******favoriteSupplierBackingBean************");
	}


	public void addToFavoriteSupplier(Long supplierId){
		System.out.println("supplierId:"+supplierId);
		IFavoriteSupplierSO favoriteSupplierSO = (IFavoriteSupplierSO) BusinessDelegate.getInstance().getServiceObject("cm",IFavoriteSupplierSO.class);

		CatalogueBackingBean catBB = new CatalogueBackingBean();
		FavoriteSupplierReqDTO reqDTO = new FavoriteSupplierReqDTO();

		//for(CmSupplierItem item : catBB.getManagedBean().getSelectedSupplierItemList()){
			CmFavoriteSupplier favor = new CmFavoriteSupplier();

			favor.setRecordStatus(true);
			favor.setPtjUserId(new Long(11003));
			favor.setSupplierId(supplierId);
			this.getManagedBean().getFavoriteSupplier().add(favor);	
		//}


		reqDTO.setFavoriteSupplier(this.getManagedBean().getFavoriteSupplier());

		FavoriteSupplierResDTO resDTO = favoriteSupplierSO.saveFavoriteSupplier(reqDTO);


		resDTO =  favoriteSupplierSO.retrieveFavoriteSupplierList(reqDTO);
		catBB.getManagedBean().setFavoriteSupplierList(resDTO.getFavoriteSupplier());

	}


	public void filterFavoriteSupplierItems(Long supplierId){

		log.info("*******filterFavoriteSupplierItems************");  
		CatalogueBackingBean catBB = new CatalogueBackingBean();
		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);

		ISearchBean<CmSupplierItem> searchBean = new SearchBean<CmSupplierItem>();
		statusFilterBean.setSearchField("supplierId");
		statusFilterBean.setSearchValue(supplierId);
		searchBean.getDataFilterBeans().add(statusFilterBean);

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();

		reqDTO.setSearchBean(searchBean);
		log.info("catalogueSO:"+catalogueSO);

		CatalogueResDTO resDTO = catalogueSO.searchSupplierItemByStatus(reqDTO);

		catBB.getManagedBean().setSupplierItemList(resDTO.getSupplierItemList());

		//return "supplierItemView?faces-redirect=true";
	}


	public void deleteFavoriteSupplier(int index){

		CatalogueBackingBean catBB = new CatalogueBackingBean();
		IFavoriteSupplierSO favoriteSupplierSO = (IFavoriteSupplierSO) BusinessDelegate.getInstance().getServiceObject("cm",IFavoriteSupplierSO.class);
		
		System.out.println("index****"+index);
		System.out.println("ID:"+catBB.getManagedBean().getFavoriteSupplierList().get(index).getSupplierId());
		
		FavoriteSupplierReqDTO reqDTO = new FavoriteSupplierReqDTO();
		
	
		reqDTO.setCmFavoriteSupplier(catBB.getManagedBean().getFavoriteSupplierList().remove(index));
		FavoriteSupplierResDTO resDTO = favoriteSupplierSO.deleteFavoriteSupplier(reqDTO);
		
		
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_710005,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

	}






}
