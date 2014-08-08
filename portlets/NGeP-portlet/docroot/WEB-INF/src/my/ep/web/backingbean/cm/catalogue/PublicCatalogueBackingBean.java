package my.ep.web.backingbean.cm.catalogue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.catalogue.PublicCatalogueManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="pCatalogueBackingBean")
@RequestScoped
public class PublicCatalogueBackingBean extends BaseBackingBean<PublicCatalogueManagedBean>{

	private static Logger log = Logger.getLogger(PublicCatalogueBackingBean.class);


	public List<String> autoCompleteItemName(String query) {

		System.out.println("query:::::::::::::::::::::::::::::: " + query);

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemName(query);

		CodificationResDTO resDTO = codificationSO.searchItemNameByAuto(reqDTO);

		List<String> itemNames = new ArrayList<String>();
		if(resDTO.getItemList()!=null && resDTO.getItemList().size() >0){
			for (CmItem item : resDTO.getItemList()) {
				itemNames.add(item.getItemName());
			}
		}

		return itemNames;
	}


	public void popularFamilyList(){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);		
		ICodificationSO codeSO = (ICodificationSO)BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);		
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		this.getManagedBean().setPopularCommodityList(null);
		CatalogueResDTO resDTO = catalogueSO.findPopularFamily(reqDTO);


		if(resDTO.getList()!=null && resDTO.getList().size()>0){
			for(int i=0; i<resDTO.getList().size(); i++){

				Object[] obj = (Object[]) resDTO.getList().get(i);

				String familyCode = (String)obj[0] + "0000";

				System.out.println("*********familyCode**********"+familyCode);
				codeReqDTO.setUnspscCode(familyCode);

				CodificationResDTO codeResDTO =  codeSO.searchUnspscObj(codeReqDTO);

				this.getManagedBean().getPopularCommodityList().add(codeResDTO.getUnspscObj());//.set Popular family code
			}
		}

	}



	public void searchCatalogue(){

		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);		
		ICodificationSO codificationSO = (ICodificationSO)BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);	
		String classCode = "";
		String familyCode = "";
		this.getManagedBean().setCatalogueList(null);
		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setFamilyCodeList(null);
		this.getManagedBean().setKeyWord(true);
		this.getManagedBean().setCloneItemNames(null);
		try {
			reqDTO.setItemName(this.getManagedBean().getItemName());
			CatalogueResDTO resDTO = catalogueSO.searchPublicCatalogueBy(reqDTO);
			System.out.println("resDTO.getPublicCatalogueResultBean().getResultList():"+resDTO.getPublicCatalogueResultBean().getResultList());
			List<String> itemNames = new ArrayList<String>();
			for(CmSupplierItem cat: resDTO.getPublicCatalogueResultBean().getResultList()){

				if(itemNames!=null && itemNames.size()>0){
					if(!itemNames.contains(cat.getCmItem().getCmUnspsc().getUnspscCode())){
						itemNames.add(cat.getCmItem().getCmUnspsc().getUnspscCode());
					}
				}else{
					itemNames.add(cat.getCmItem().getCmUnspsc().getUnspscCode());
				}
			}
			
			this.getManagedBean().setCloneItemNames(itemNames);
			System.out.println("size:"+itemNames.size());

			this.getManagedBean().setFamilyList(new ArrayList<CmUnspsc>());
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			for(String code: itemNames){
				System.out.println("*******code***********"+code);
				codeReqDTO.setUnspscCode(code);
				CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
				this.getManagedBean().getFamilyList().add(codeResDTO.getUnspscObj());

				if(code!=null && code.trim().length()>0){
					this.getManagedBean().getFamilyCodeList().add(code);
				}
			}
			
			this.getManagedBean().setCatalogueList(resDTO.getPublicCatalogueResultBean().getResultList());

		}catch(Exception ex){

		}finally{
			catalogueSO = null;
			codificationSO = null;
		}
		//return "publicCatalogueView";
	}


	public void preRenderPublicCatView(){

		this.getManagedBean().setFrontPage(true);
		popularFamilyList();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "publicCatalogueView");
	}



	public void searchCatalogueByPrice(){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);

		String subString = "";


		System.out.println("getFamilyCode:"+this.getManagedBean().getFamilyCode());

		reqDTO.getSubStringFamilyCode().add(this.getManagedBean().getFamilyCode());

		if(this.getManagedBean().getPriceForm()!=null && this.getManagedBean().getPriceForm().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
		}

		if(this.getManagedBean().getPriceTo()!=null && this.getManagedBean().getPriceTo().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
		}


		if(this.getManagedBean().getBrandId()!=null){
			if(!this.getManagedBean().getBrandId().equals(new Long (0))){
				System.out.println("brand selected");

				reqDTO.setBrandId(this.getManagedBean().getBrandId());
			}
		}


		CatalogueResDTO resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

		System.out.println("***********************"+resDTO.getViewPublicSuppItemResultBean().getResultList());

		for(VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()){

			this.getManagedBean().getCatalogueList().add(view.getCmSupplierItem());
		}

	}



	public void searchCatalogueByFamilyLevel(CmUnspsc commodity){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		System.out.println("getUnspscCode******"+commodity.getUnspscCode());
		String subString = "";

		this.getManagedBean().setKeyWord(false);
		this.getManagedBean().setUnspscTitle(commodity.getUnspscTitle());


		if(commodity.getUnspscCode()!=null && commodity.getUnspscCode().trim().length()>0){

			this.getManagedBean().setFamilyCode(commodity.getUnspscCode());
		}
		System.out.println("getFamilyCode:"+this.getManagedBean().getFamilyCode());

		reqDTO.getSubStringFamilyCode().add(this.getManagedBean().getFamilyCode());

		if(this.getManagedBean().getPriceForm()!=null && this.getManagedBean().getPriceForm().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
		}

		if(this.getManagedBean().getPriceTo()!=null && this.getManagedBean().getPriceTo().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
		}

		System.out.println("getCmBrand:"+commodity.getCmBrand().size());

		for (CmBrand brand : commodity.getCmBrand()) {
			System.out.println("getBrandName:"+brand.getBrandName());
			this.getManagedBean()
			.getBrandCriteria()
			.add(new SelectItem(brand.getBrandId(), brand
					.getBrandName()));
		}


		CatalogueResDTO resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

		System.out.println("***********************"+resDTO.getViewPublicSuppItemResultBean().getResultList());

		for(VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()){

			this.getManagedBean().getCatalogueList().add(view.getCmSupplierItem());
		}


		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().getFamilyList().add(commodity);

	}


	public String navigateToSupplierItemDet(CmSupplierItem supplierItem) {

		ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject("cm", ICatalogueSO.class);
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm",ISupplierSearchSO.class);
		
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CatalogueResDTO resDTO = null;
		
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		SupplierSearchResDTO smResDTO = null;
		
		CatalogueBackingBean bb = new CatalogueBackingBean();
		//PmUser pmUser = new FacesScopes().getUserSessionBean();
		//supplierReqDTO.setUserId(pmUser.getUserId());

		try{
			//smResDTO = supplierSearchSO.retriveSupplierProfiles(supplierReqDTO);
	
			//System.out.println("getSmSupplier" + smResDTO.getResultBean().getResultList().get(0));
	
			reqDTO.setSupplierId(supplierItem.getSmSupplier().getSupplierId());
			log.info("supplierItem.getSmSupplier().getSupplierId() = "+supplierItem.getSmSupplier().getSupplierId());
	
			resDTO = catalogueSO.retrieveSupplierZoneBySuppId(reqDTO);
	
			System.out.println("size:" + resDTO.getSupplierZoneLs().size());
			bb.getManagedBean().setSupplierZoneList(resDTO.getSupplierZoneLs());
			
			reqDTO.setSupplierItemId(supplierItem.getSupplierItemId());
			resDTO = catalogueSO.findActivePricing(reqDTO);
			supplierItem.setCmPricings(resDTO.getPriceLs());
			log.info("supplierItem.getCmPricings().size() = "+supplierItem.getCmPricings().size());
			bb.getManagedBean().setSelectedMaintainItem(supplierItem);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			catalogueSO = null;
			supplierSearchSO = null;
		}

		return "addCatalogView";
		//return "../catalogue/addCatalogView.xhtml";
	}


	public String cancelToPublicSearch(){


		return "publicCatalogueView";
	}

	public void resetCatalogue(){


		this.getManagedBean().setPriceForm(null);
		this.getManagedBean().setPriceTo(null);
		this.getManagedBean().setBrandId(0L);
	}
	
	
	public void backToList(){
		
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);
		
		this.getManagedBean().setFamilyList(null);
		
		for(String code: this.getManagedBean().getCloneItemNames()){

			System.out.println("*******code***********"+code);
			
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			codeReqDTO.setUnspscCode(code);
			CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);

			this.getManagedBean().getFamilyList().add(codeResDTO.getUnspscObj());

		}
		
	}





}
