package my.ep.web.backingbean.cm.catalogue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.cm.catalogue.IWishListSO;
import my.ep.dto.cm.catalogue.WishListReqDTO;
import my.ep.dto.cm.catalogue.WishListResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmWishlist;
import my.ep.persistence.entity.cm.catalog.CmWishlistDtl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.WishListManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="wishListBackingBean")
@RequestScoped
public class WishListBackingBean extends BaseBackingBean<WishListManagedBean>{

	private static Logger log = Logger.getLogger(WishListBackingBean.class);

	public WishListBackingBean(){
		log.info("*******WishListBackingBean************");
	}

	public void saveWishList(){

		CatalogueBackingBean catBB = new CatalogueBackingBean();
		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);

		log.info("wishList Desc:"+this.getManagedBean().getWishList().getWishlistDesc());
		WishListReqDTO reqDTO = new WishListReqDTO();
		reqDTO.setCmWishlist(this.getManagedBean().getWishList());

		WishListResDTO resDTO = wishListSO.saveWishList(reqDTO);

		//IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);
		//WishListReqDTO reqDTO = new WishListReqDTO();

		resDTO = wishListSO.retrieveWishList(reqDTO);
		log.info("size:"+resDTO.getWishLists().size());

		catBB.getManagedBean().setWishLists(resDTO.getWishLists());



	}


	public void addItemIntoWishList(){

		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);
		WishListReqDTO reqDTO = new WishListReqDTO();
		CatalogueBackingBean catBB = new CatalogueBackingBean();

		log.info("getSelectedSupplierItemList:"+catBB.getManagedBean().getSelectedSupplierItemList().size());
		log.info("wishList length:"+this.getManagedBean().getSelectedWishLists().length);

		for(int i=0; i<this.getManagedBean().getSelectedWishLists().length; i++){
			CmWishlist wishList = this.getManagedBean().getSelectedWishLists()[i];

			for(CmSupplierItem item : catBB.getManagedBean().getSelectedSupplierItemList()){
				log.info("name:"+item.getCmItem().getItemName());
				CmWishlistDtl wishListDet = new CmWishlistDtl();
				wishListDet.setCmWishlist(wishList);
				wishListDet.setCmSupplierItem(item);

				wishList.getCmWishlistDtls().add(wishListDet);
			}

			reqDTO.setCmWishlist(wishList);

			WishListResDTO resDTO = wishListSO.addWishListItem(reqDTO);

		}


	}


	public String viewWishListItem(){

		CatalogueBackingBean catBB = new CatalogueBackingBean();

		this.getManagedBean().setWishListDet(catBB.getManagedBean().getWishLists().get(0).getCmWishlistDtls());

		return "viewWishListItem";
	}


	public void filterWishListType(int index){

		CatalogueBackingBean catBB = new CatalogueBackingBean();

//		this.getManagedBean().setWishListDet(catBB.getManagedBean().getWishLists().get(index).getCmWishlistDtls());
		this.getManagedBean().setSelectedWishList(catBB.getManagedBean().getWishLists().get(index));
	}
	
	public void deleteWishListItem(int index){
		
		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);
		WishListReqDTO reqDTO = new WishListReqDTO();
	
//		this.getManagedBean().getWishList().getCmWishlistDtls().remove(index);
		this.getManagedBean().getSelectedWishList().getCmWishlistDtls().remove(index);
//		reqDTO.setCmWishListDet(this.getManagedBean().getWishListDet().get(index));
		reqDTO.setCmWishlist(this.getManagedBean().getSelectedWishList());
		
		WishListResDTO resDTO = wishListSO.deleteWishListItem(reqDTO);
		
	}





}
