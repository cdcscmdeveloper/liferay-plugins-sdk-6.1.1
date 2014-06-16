package my.ep.web.backingbean.cm.catalogue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.cm.catalog.VCmPubSuppItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.managedbean.cm.catalogue.PublicCatalogueManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="pCatalogueBackingBean")
@RequestScoped
public class PublicCatalogueBackingBean extends BaseBackingBean<PublicCatalogueManagedBean>{




	public List<String> autoCompleteItemName(String query) {

		System.out.println("query:::::::::::::::::::::::::::::: " + query);

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		reqDTO.setItemName(query);

		CodificationResDTO resDTO = codificationSO.searchItemNameByAuto(reqDTO);

		List<String> itemNames = new ArrayList<String>();

		for (CmItem item : resDTO.getItemList()) {
			itemNames.add(item.getItemName());
		}

		return itemNames;
	}


	public void popularFamilyList(){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);		
		ICodificationSO codeSO = (ICodificationSO)BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);		
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		CodificationReqDTO codeReqDTO = new CodificationReqDTO();

		this.getManagedBean().setPopularFamilyList(null);
		CatalogueResDTO resDTO = catalogueSO.findPopularFamily(reqDTO);


		if(resDTO.getList()!=null && resDTO.getList().size()>0){
			for(int i=0; i<resDTO.getList().size(); i++){

				Object[] obj = (Object[]) resDTO.getList().get(i);

				String familyCode = (String)obj[0] + "0000";

				System.out.println("*********familyCode**********"+familyCode);
				codeReqDTO.setUnspscCode(familyCode);

				CodificationResDTO codeResDTO =  codeSO.searchUnspscObj(codeReqDTO);

				this.getManagedBean().getPopularFamilyList().add(codeResDTO.getUnspscObj());
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
		try {



			reqDTO.setItemName(this.getManagedBean().getItemName());


			CatalogueResDTO resDTO = catalogueSO.searchPublicCatalogueBy(reqDTO);
			System.out.println("resDTO.getPublicCatalogueResultBean().getResultList():"+resDTO.getPublicCatalogueResultBean().getResultList());
			List<String> itemNames = new ArrayList<String>();
			for(CmSupplierItem cat: resDTO.getPublicCatalogueResultBean().getResultList()){


				classCode = UnspscCodeBackingBean.getParentCode(new Long(cat.getCmItem().getCmUnspsc().getUnspscCode()));



				familyCode = UnspscCodeBackingBean.getParentCode(new Long(classCode));

				if(itemNames!=null && itemNames.size()>0){
					if(!itemNames.contains(familyCode)){
						itemNames.add(familyCode);
					}
				}else{
					itemNames.add(familyCode);
				}

			}

			System.out.println("size:"+itemNames.size());

			this.getManagedBean().setFamilyList(new ArrayList<CmUnspsc>());
			CodificationReqDTO codeReqDTO = new CodificationReqDTO();
			for(String code: itemNames){

				System.out.println("*******code***********"+code);
				codeReqDTO.setUnspscCode(code);
				CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);

				this.getManagedBean().getFamilyList().add(codeResDTO.getUnspscObj());
				
				if(code!=null && code.trim().length()>0){
					String subString = code.substring(0, 4);
					 this.getManagedBean().getFamilyCodeList().add(subString);
				}
				
				

			}


			this.getManagedBean().setCatalogueList(resDTO.getPublicCatalogueResultBean().getResultList());

		}catch(Exception ex){

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

		CatalogueResDTO resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

		System.out.println("***********************"+resDTO.getViewPublicSuppItemResultBean().getResultList());

		for(VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()){

			this.getManagedBean().getCatalogueList().add(view.getCmSupplierItem());
		}

	}
	


	public void searchCatalogueByFamilyLevel(CmUnspsc family){

		ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		this.getManagedBean().setFrontPage(false);
		this.getManagedBean().setCatalogueList(null);
		System.out.println("getUnspscCode******"+family.getUnspscCode());
		String subString = "";

		if(family.getUnspscCode()!=null && family.getUnspscCode().trim().length()>0){
			 subString = family.getUnspscCode().substring(0, 4);
			 this.getManagedBean().setFamilyCode(subString);
		}
		System.out.println("getFamilyCode:"+this.getManagedBean().getFamilyCode());

		reqDTO.getSubStringFamilyCode().add(this.getManagedBean().getFamilyCode());

		if(this.getManagedBean().getPriceForm()!=null && this.getManagedBean().getPriceForm().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceFrom(this.getManagedBean().getPriceForm());
		}

		if(this.getManagedBean().getPriceTo()!=null && this.getManagedBean().getPriceTo().compareTo(new BigDecimal("0.00"))>0){
			reqDTO.setPriceTo(this.getManagedBean().getPriceTo());
		}

		CatalogueResDTO resDTO = catalogueSO.findPubCatalogueByFamilyLevel(reqDTO);

		System.out.println("***********************"+resDTO.getViewPublicSuppItemResultBean().getResultList());

		for(VCmPubSuppItem view : resDTO.getViewPublicSuppItemResultBean().getResultList()){

			this.getManagedBean().getCatalogueList().add(view.getCmSupplierItem());
		}


		this.getManagedBean().setFamilyList(null);
		this.getManagedBean().getFamilyList().add(family);

	}





}
