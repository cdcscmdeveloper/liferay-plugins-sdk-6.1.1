package my.ep.web.backingbean.trackingdiary;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.cm.catalog.VCmSupplierItem;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.managedbean.trackingdiary.CmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cmTrackingDiaryBackingBean")
@ViewScoped
public class CmTrackingDiaryBackingBean extends
		BaseBackingBean<CmTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(CmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CATALOG_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CATALOG_DETAIL_SEARCH = "cmTrackingDiaryDetailSearch.xhtml";
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
//		if(null == getManagedBean().getSearchTrasactionType()){ 
//		throw new EpException(MessageConst.ERROR_740023);
//	}else if((null ==getManagedBean().getSearchViewType() 
//			&& "".equalsIgnoreCase(getManagedBean().getSearchContractNo()) 
//			&& "".equalsIgnoreCase(getManagedBean().getSearchLoaNo()))){
//		throw new EpException(MessageConst.ERROR_740023);
//	MessageConst.ERROR_715026
//	}
	System.out.println("pageChangeListener");
	getManagedBean().getCmDocumentDataModel().reloadData();

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

//		searchDataModel.reloadData();	
		
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

			IViewSupplierItemSO viewSO = null;

			try {	

				viewSO = (IViewSupplierItemSO) BusinessDelegate.getInstance().getServiceObject("cm",IViewSupplierItemSO.class);


				
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
				
				log.info("*********b4***********");
				CatalogueResDTO resDTO = viewSO.searchViewSupplierItems(reqDTO);
				log.info("*********after***********");
				log.info("*********resDTO.getViewSuppItemResultBean()***********"+resDTO.getViewSuppItemResultBean().getTotalRecord());

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
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),"RN150000000005580"));
//			System.out.println("TrackingDiaryReqDTO" + vCtDocument.getDocNo());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

	public String goToTrackingDiarySearch(VCtDocument vCtDocument) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
//		getManagedBean().setSmSupplier(smSupplier);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CATALOG_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String reset() {
		System.out.println("In reset ...................");
//		itemNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
//		itemCodeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
//		supplierNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		this.setSearchDataModel(new SearchPagingDataModel());
		searchDataModel.reloadData();
		System.out.println("after reset.........................");
		return null;
	}
	public SelectItem[] getStatus() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));

			items[1] = new SelectItem(new String("ACT"), new String("ACTIVE"));
			items[2] = new SelectItem(new String("NACT"), new String("NON ACTIVE"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	public String backToPrev() {

		return NAV_TO_CATALOG_SEARCH;
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

	public SearchFilterBean getDocNoFilterBean() {
		return docNoFilterBean;
	}

	public void setDocNoFilterBean(SearchFilterBean docNoFilterBean) {
		this.docNoFilterBean = docNoFilterBean;
	}


}

