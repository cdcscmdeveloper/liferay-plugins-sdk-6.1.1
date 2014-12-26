package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cmTrackingDiaryBackingBean")
@ViewScoped
public class CmTrackingDiaryBackingBean extends
		BaseBackingBean<CdTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(CmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CODIFICATION_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CODIFICATION_DETAIL_SEARCH = "cmTrackingDiaryDetailSearch.xhtml";
	private SearchFilterBean itemNameFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean itemCodeFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean supplierNameFilterBean 	= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean docTypeFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean docIdFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean docNoFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	private IPagingDataModel<VCmSupplierItem> searchDataModel 	= new SearchPagingDataModel();
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	public CmTrackingDiaryBackingBean() {
//	    String unspscCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unspscCode");
//	    System.out.println("unspscCode====================================: "+unspscCode);
	}
	
	public void search() {
//		String unspscCode = codeFilterBean.getSearchValue().toString();
//		String unspscTitle = titleFilterBean.getSearchValue().toString();
//		String extensionCode = extensionCodeFilterBean.getSearchValue().toString();

//		log.info(".......................unspscCode="+ unspscCode);
//		log.info("................................unspscTitle="+ unspscTitle);
//		(extensionCode == null && unspscCode == null && (unspscTitle == null || unspscTitle.isEmpty()))
//		if ((extensionCode != null && extensionCode.isEmpty()) && ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle.isEmpty()))) {
//			throw new EpException(MessageConst.ERROR_716006);
//		}
//
//		if (unspscCode != null && !unspscCode.isEmpty() && unspscCode.length() != 8) {
//			throw new EpException(MessageConst.ERROR_715026);
//		}

		searchDataModel.reloadData();	
		
	}
	private class SearchPagingDataModel extends PagingDataModel<VCmSupplierItem> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("supplierItemID", false);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<VCmSupplierItem> pageChangeListener(
				ISearchBean<VCmSupplierItem> searchBean) {

			if(StringUtils.isBlank((String)itemNameFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)itemCodeFilterBean.getSearchValue())
					&&StringUtils.isBlank((String)supplierNameFilterBean.getSearchValue())) {
				log.debug("******Return New SearchResultBean******");
				return new SearchResultBean();
			}

			IViewSupplierItemSO viewSO = null;

			try {	

				viewSO = (IViewSupplierItemSO) BusinessDelegate.getInstance().getServiceObject("cm",IViewSupplierItemSO.class);


				if (StringUtils.isNotBlank((String)itemNameFilterBean.getSearchValue())) {
					itemNameFilterBean.setSearchField("itemName");
					itemNameFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(itemNameFilterBean);
				}

				if (StringUtils.isNotBlank((String)itemCodeFilterBean.getSearchValue())) {
					itemCodeFilterBean.setSearchField("itemCode");
					itemCodeFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(itemCodeFilterBean);
				}

				if (StringUtils.isNotBlank((String)supplierNameFilterBean.getSearchValue())) {
					supplierNameFilterBean.setSearchField("companyName");
					supplierNameFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(supplierNameFilterBean);
				}

				
				CatalogueReqDTO reqDTO = new CatalogueReqDTO();
				reqDTO.setViewSearchBean(searchBean);
				
				if(searchBean.getDataFilterBeans().size()<=0){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(MessageConst.ERROR_716006,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					//throw new EpException(MessageConst.ERROR_715014);
					return new SearchResultBean();
				}
				
				log.debug("*********b4***********");
				CatalogueResDTO resDTO = viewSO.searchViewSupplierItems(reqDTO);
				log.debug("*********after***********");
				
				return resDTO.getViewSuppItemResultBean();
			}catch(Exception e){
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				viewSO = null;
			}
		}

	}


	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			docTypeFilterBean.setSearchField("docType");
			docTypeFilterBean.setSearchValue("IN");
			searchBean.getDataFilterBeans().add(docTypeFilterBean);

//			docIdFilterBean.setSearchField("docId");
//			docIdFilterBean.setSearchValue("2233");
//			searchBean.getDataFilterBeans().add(docIdFilterBean);
//			
//			docNoFilterBean.setSearchField("docNo");
//			docNoFilterBean.setSearchValue("2233");
//			searchBean.getDataFilterBeans().add(docNoFilterBean);

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("SearchTrackingDiaryPagingDataModel TotalRecord====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

	public String goToTrackingDiarySearch(VCmSupplierItem unspscObj) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
//		getManagedBean().setVCmSupplierItem(unspscObj);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CODIFICATION_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String reset() {
		System.out.println("In reset ...................");
		itemNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		itemCodeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		supplierNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		this.setSearchDataModel(new SearchPagingDataModel());
		searchDataModel.reloadData();
		System.out.println("after reset.........................");
		return null;
	}

	public String backToPrev() {

		return NAV_TO_CODIFICATION_SEARCH;
	}

	public IPagingDataModel<VCmSupplierItem> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<VCmSupplierItem> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	public SearchFilterBean getDocTypeFilterBean() {
		return docTypeFilterBean;
	}

	public void setDocTypeFilterBean(SearchFilterBean docTypeFilterBean) {
		this.docTypeFilterBean = docTypeFilterBean;
	}

	public SearchFilterBean getDocIdFilterBean() {
		return docIdFilterBean;
	}

	public void setDocIdFilterBean(SearchFilterBean docIdFilterBean) {
		this.docIdFilterBean = docIdFilterBean;
	}


}

