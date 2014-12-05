package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cdTrackingDiaryBackingBean")
@ViewScoped
public class CdTrackingDiaryBackingBean extends
		BaseBackingBean<CdTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CODIFICATION_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CODIFICATION_DETAIL_SEARCH = "cdTrackingDiaryDetailSearch.xhtml";
	private SearchFilterBean titleFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean codeFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean extensionCodeFilterBean 	= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean docTypeFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean docIdFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean docNoFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	private IPagingDataModel<CmUnspsc> searchDataModel 	= new SearchPagingDataModel();
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	public CdTrackingDiaryBackingBean() {
//	    String unspscCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unspscCode");
//	    System.out.println("unspscCode====================================: "+unspscCode);
	}
	
	public void unspscSearch() {
		String unspscCode = codeFilterBean.getSearchValue().toString();
		String unspscTitle = titleFilterBean.getSearchValue().toString();
		String extensionCode = extensionCodeFilterBean.getSearchValue().toString();

		log.info(".......................unspscCode="+ unspscCode);
		log.info("................................unspscTitle="+ unspscTitle);
//		(extensionCode == null && unspscCode == null && (unspscTitle == null || unspscTitle.isEmpty()))
		if ((extensionCode != null && extensionCode.isEmpty()) && ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		if (unspscCode != null && !unspscCode.isEmpty() && unspscCode.length() != 8) {
			throw new EpException(MessageConst.ERROR_715026);
		}

		searchDataModel.reloadData();	
		
	}

	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			System.out.println("titleFilterBean ="+ titleFilterBean.getSearchValue());
		

			if (StringUtils.isBlank((String) codeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) titleFilterBean
							.getSearchValue())) {
				System.out.println("isBlank");
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {
				System.out.println("pageChangeListener");
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);


				if (StringUtils.isNotBlank((String) titleFilterBean
						.getSearchValue())) {
					System.out.println("*********titleFilterBean***********");
					titleFilterBean.setSearchField("unspscTitle");
					titleFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(titleFilterBean);
				}

				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				CodificationResDTO resDTO = unspscSO.searchUNSPSC(reqDTO);

				System.out.println("*********after***********");
				return resDTO.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				unspscSO = null;
			}
			return new SearchResultBean();
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
			docTypeFilterBean.setSearchValue("PO");
			searchBean.getDataFilterBeans().add(docTypeFilterBean);

			docIdFilterBean.setSearchField("docId");
			docIdFilterBean.setSearchValue("2233");
			searchBean.getDataFilterBeans().add(docIdFilterBean);
			
			docNoFilterBean.setSearchField("docNo");
			docNoFilterBean.setSearchValue("2233");
			searchBean.getDataFilterBeans().add(docNoFilterBean);

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

	public String goToTrackingDiarySearch(CmUnspsc unspscObj) {
		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
		getManagedBean().setCmUnspsc(unspscObj);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CODIFICATION_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String reset() {
		System.out.println("In reset action...................");
		titleFilterBean = null;
		codeFilterBean = null;
		extensionCodeFilterBean = null;
//		statusFilterBean = null;
		titleFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		codeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		extensionCodeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
//		statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		this.setSearchDataModel(new SearchPagingDataModel());
		searchDataModel.reloadData();
		System.out.println("after reset.........................");
		return null;
	}

	public String backToPrev() {

		return NAV_TO_CODIFICATION_SEARCH;
	}

	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public SearchFilterBean getcodeFilterBean() {
		return codeFilterBean;
	}

	public void setcodeFilterBean(SearchFilterBean codeFilterBean) {
		this.codeFilterBean = codeFilterBean;
	}

	public SearchFilterBean getExtensionCodeFilterBean() {
		return extensionCodeFilterBean;
	}

	public void setExtensionCodeFilterBean(SearchFilterBean extensionCodeFilterBean) {
		this.extensionCodeFilterBean = extensionCodeFilterBean;
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	public SearchFilterBean getTitleFilterBean() {
		return titleFilterBean;
	}

	public void setTitleFilterBean(SearchFilterBean titleFilterBean) {
		this.titleFilterBean = titleFilterBean;
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

