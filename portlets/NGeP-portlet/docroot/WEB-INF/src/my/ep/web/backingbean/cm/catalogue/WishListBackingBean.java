/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1 . 00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
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


/**
 * The Class WishListBackingBean.
 * @author hketan
 */
@ManagedBean(name="wishListBackingBean")
@RequestScoped
public class WishListBackingBean extends BaseBackingBean<WishListManagedBean>{

	/** The log. */
	private static Logger log = Logger.getLogger(WishListBackingBean.class);

	/**
	 * Instantiates a new wish list backing bean.
	 */
	public WishListBackingBean(){
		log.info("*******WishListBackingBean************");
	}

	/**
	 * Save wish list.
	 */
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


	/**
	 * Adds item into wish list.
	 */
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


	/**
	 * View wish list item.
	 * 
	 * @return the string
	 */
	public String viewWishListItem(){

		CatalogueBackingBean catBB = new CatalogueBackingBean();

		this.getManagedBean().setWishListDet(catBB.getManagedBean().getWishLists().get(0).getCmWishlistDtls());

		return "viewWishListItem";
	}


	/**
	 * Filter wish list type.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void filterWishListType(int index){

		CatalogueBackingBean catBB = new CatalogueBackingBean();

		catBB.getManagedBean().setSupplierItemList(null);
		System.out.println("wishlist==============" + catBB.getManagedBean().getWishLists().get(index).getCmWishlistDtls() + "  " + catBB.getManagedBean().getWishLists().get(index).getCmWishlistDtls().size());
		
		for(CmWishlistDtl det : catBB.getManagedBean().getWishLists().get(index).getCmWishlistDtls()){
			
			catBB.getManagedBean().getSupplierItemList().add(det.getCmSupplierItem());
		}
		
	}
	
	
	/**
	 * Filter wish list type pop up.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void filterWishListTypePopUp(int index){

		CatalogueBackingBean catBB = new CatalogueBackingBean();

		this.getManagedBean().setSelectedWishList(catBB.getManagedBean().getWishLists().get(index));
		
	}
	
	/**
	 * Delete favorite wish list.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteFavoriteWishList(int index){
		
		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);
		WishListReqDTO reqDTO = new WishListReqDTO();
	
		CatalogueBackingBean catBB = new CatalogueBackingBean();
		
		

		reqDTO.setCmWishlist(catBB.getManagedBean().getWishLists().get(index));
		
		WishListResDTO resDTO = wishListSO.deleteWishListItem(reqDTO);
		catBB.getManagedBean().getWishLists().remove(index);
	}
	
	
	/**
	 * Delete wish list item.
	 * 
	 * @param index
	 *            (int index(mandatory))
	 */
	public void deleteWishListItem(int index){
		IWishListSO wishListSO = (IWishListSO)BusinessDelegate.getInstance().getServiceObject("cm",IWishListSO.class);
		WishListReqDTO reqDTO = new WishListReqDTO();

		
		reqDTO.setCmWishListDet(this.getManagedBean().getSelectedWishList().getCmWishlistDtls().get(index));
		
		//WishListResDTO resDTO = wishListSO.deleteWishListDetail(reqDTO);
		
	}
	
	
	
	





}
