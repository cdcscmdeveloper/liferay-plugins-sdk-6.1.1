package my.ep.web.managedbean.cm.catalogue;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "pCatalogueMB")
@SessionScoped
public class PublicCatalogueManagedBean extends BaseManagedBean {

	private String itemName;
	private List <CmUnspsc> familyList;
	private List <CmSupplierItem> catalogueList;
	private List <CmUnspsc> popularFamilyList;
	private boolean frontPage = true;
	

	private SearchFilterBean itemNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());

	private IPagingDataModel<CmSupplierItem> searchDataModel = new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<CmSupplierItem> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmSupplierItem> pageChangeListener(
				ISearchBean<CmSupplierItem> searchBean) {

			if(StringUtils.isBlank((String)itemNameFilterBean.getSearchValue())) {
				System.out.println("******SearchResultBean******");
				return new SearchResultBean();
			}
			CatalogueReqDTO reqDTO = new CatalogueReqDTO();
			ICatalogueSO catalogueSO = (ICatalogueSO)BusinessDelegate.getInstance().getServiceObject("cm",ICatalogueSO.class);		
			ICodificationSO codificationSO = (ICodificationSO)BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);	
			String classCode = "";
			String familyCode = "";
			try {



				if (StringUtils.isNotBlank((String) itemNameFilterBean.getSearchValue())) {

					reqDTO.setItemName((String)itemNameFilterBean.getSearchValue());

				}


				CatalogueResDTO resDTO = catalogueSO.searchPublicCatalogueBy(reqDTO);

				List <CmSupplierItem> publicCat = resDTO.getPublicCatalogueResultBean().getResultList();

				List<String> itemNames = new ArrayList<String>();
				for(CmSupplierItem cat: publicCat){


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
				
				setFamilyList(new ArrayList<CmUnspsc>());
				CodificationReqDTO codeReqDTO = new CodificationReqDTO();
				for(String code: itemNames){
					
					System.out.println("*******code***********"+code);
					codeReqDTO.setUnspscCode(code);
					CodificationResDTO codeResDTO = codificationSO.searchUnspscObj(codeReqDTO);
					
					familyList.add(codeResDTO.getUnspscObj());
					
				}
				//RequestContext.getCurrentInstance().update(":form:catalogueTabId:resultpl");
				System.out.println("*********familyList*********"+familyList.size());
				

				return resDTO.getPublicCatalogueResultBean();

			} finally {
				catalogueSO = null;
				
				
			}
		}

	}


	public void searchCatalogue(){
		searchDataModel.reloadData();
	}


	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public IPagingDataModel<CmSupplierItem> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmSupplierItem> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public SearchFilterBean getItemNameFilterBean() {
		return itemNameFilterBean;
	}

	public void setItemNameFilterBean(SearchFilterBean itemNameFilterBean) {
		this.itemNameFilterBean = itemNameFilterBean;
	}


	public List<CmUnspsc> getFamilyList() {
		if(familyList==null)
			familyList = new ArrayList<CmUnspsc>();
		return familyList;
	}


	public void setFamilyList(List<CmUnspsc> familyList) {
		this.familyList = familyList;
	}


	public List<CmSupplierItem> getCatalogueList() {
		if(catalogueList==null)
			catalogueList = new ArrayList<CmSupplierItem>();
		return catalogueList;
	}


	public void setCatalogueList(List<CmSupplierItem> catalogueList) {
		this.catalogueList = catalogueList;
	}


	public List<CmUnspsc> getPopularFamilyList() {
		if(popularFamilyList==null)
			popularFamilyList = new ArrayList<CmUnspsc>();
		return popularFamilyList;
	}


	public void setPopularFamilyList(List<CmUnspsc> popularFamilyList) {
		this.popularFamilyList = popularFamilyList;
	}


	public boolean isFrontPage() {
		return frontPage;
	}


	public void setFrontPage(boolean frontPage) {
		this.frontPage = frontPage;
	}

	
	







}
