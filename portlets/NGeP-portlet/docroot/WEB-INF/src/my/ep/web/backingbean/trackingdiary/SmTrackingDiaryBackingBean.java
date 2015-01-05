package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.SmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "smTrackingDiaryBackingBean")
@SessionScoped
public class SmTrackingDiaryBackingBean extends
		BaseBackingBean<SmTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(SmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SMSUPPLIER_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_SMSUPPLIER_DETAIL_SEARCH = "smTrackingDiaryDetailSearch.xhtml";
	private SearchFilterBean MOFAccountFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean applicationTypeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean applicationNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean supplierNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private IPagingDataModel<SmSupplier> searchDataModel = new SearchPagingDataModel();
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	public SmTrackingDiaryBackingBean() {
//	    String smSupplierCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("smSupplierCode");
//		SmSupplier smSupplier = new SmSupplier();
	}
	
	public void search() {
//		log.info("................................smSupplierTitle="+ smSupplierTitle);
//		if ((extensionCode != null && extensionCode.isEmpty()) && ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle.isEmpty()))) {
//			throw new EpException(MessageConst.ERROR_716006);
//		}
		searchDataModel.reloadData();

		}

	
	private class SearchPagingDataModel extends PagingDataModel<SmSupplier> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("smSupplierTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			System.out.println("applicationNoFilterBean.getSearchValue()="+ applicationNoFilterBean.getSearchValue());
		

			if (StringUtils.isBlank((String) applicationNoFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) applicationNoFilterBean
							.getSearchValue())) {
				System.out.println("isBlank");
				return new SearchResultBean();
			}

			ISupplierSearchSO smSupplierSO = null;
			try {
				System.out.println("ISearchResultBean<SmSupplier> pageChangeListener");
				smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
						.getServiceObject("sm", ISupplierSearchSO.class);


				if (StringUtils.isNotBlank((String) applicationNoFilterBean
						.getSearchValue())) {
					System.out.println("*********applicationNoFilterBean***********");
					applicationNoFilterBean.setSearchField("appNo");
					searchBean.getDataFilterBeans().add(applicationNoFilterBean);
				}

				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				reqDTO.setCompanyName("METRO");
//				SupplierSearchReqDTO supplierSearchReqDTO = null;
//				SupplierSearchReqDTO supplierSearchReqDTO = null;
				SupplierSearchResDTO res = smSupplierSO.retriveSupplierProfiles(reqDTO);

				System.out.println("*********after***********"+res.getResultBean().getTotalRecord());
				return res.getResultBean();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				smSupplierSO = null;
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
//			docTypeFilterBean.setSearchField("docType");
//			docTypeFilterBean.setSearchValue("PO");
//			searchBean.getDataFilterBeans().add(docTypeFilterBean);
//
//			docIdFilterBean.setSearchField("docId");
//			docIdFilterBean.setSearchValue("2233");
//			searchBean.getDataFilterBeans().add(docTypeFilterBean);

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

//	public String goToTrackingDiarySearch(CmUnspsc unspscObj) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
//		getManagedBean().setCmUnspsc(unspscObj);
//		searchTrackingDiaryDataModel.reloadData();	
//		return NAV_TO_CODIFICATION_DETAIL_SEARCH + "?faces-redirect=true";
//	}

	public String reset() {
		return null;
	}

	public String backToPrev() {

		return NAV_TO_SMSUPPLIER_SEARCH;
	}

//	public IPagingDataModel<SmSupplier> getSearchDataModel() {
//		return searchDataModel;
//	}
//
//	public void setSearchDataModel(IPagingDataModel<SmSupplier> searchDataModel) {
//		this.searchDataModel = searchDataModel;
//	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	public SearchFilterBean getMOFAccountFilterBean() {
		return MOFAccountFilterBean;
	}

	public void setMOFAccountFilterBean(SearchFilterBean mOFAccountFilterBean) {
		MOFAccountFilterBean = mOFAccountFilterBean;
	}

	public SearchFilterBean getApplicationTypeFilterBean() {
		return applicationTypeFilterBean;
	}

	public void setApplicationTypeFilterBean(
			SearchFilterBean applicationTypeFilterBean) {
		this.applicationTypeFilterBean = applicationTypeFilterBean;
	}

	public SearchFilterBean getApplicationNoFilterBean() {
		return applicationNoFilterBean;
	}

	public void setApplicationNoFilterBean(SearchFilterBean applicationNoFilterBean) {
		this.applicationNoFilterBean = applicationNoFilterBean;
	}

	public SearchFilterBean getSupplierNameFilterBean() {
		return supplierNameFilterBean;
	}

	public void setSupplierNameFilterBean(SearchFilterBean supplierNameFilterBean) {
		this.supplierNameFilterBean = supplierNameFilterBean;
	}

	public IPagingDataModel<SmSupplier> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<SmSupplier> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

}

