package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
import my.ep.web.managedbean.cm.codification.CodificationManagedBean;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cdTrackingDiaryBackingBean")
@SessionScoped
public class CdTrackingDiaryBackingBean extends
		TrackingDiaryBaseBackingBean<CodificationManagedBean> {
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CODIFICATION_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CODIFICATION_DETAIL_SEARCH = "cdTrackingDiaryDetailSearch.xhtml";
	private SearchFilterBean UNSPSCTitleFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean UNSPSCCodeFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean extensionCodeFilterBean 	= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private IPagingDataModel<CmUnspsc> searchDataModel 	= new SearchPagingDataModel();
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	public CdTrackingDiaryBackingBean() {
	    String unspscCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unspscCode");
	    System.out.println("unspscCode====================================: "+unspscCode);
		this.getManagedBean().setContractedItem(null);
		CmUnspsc unspsc = new CmUnspsc();
		unspsc.setUnspscTitle("item");
		this.getManagedBean().getContractedItem().add(unspsc);
	}
	
	public void unspscSearch() {
		String unspscCode = getManagedBean().getAutoCompleteUnspscCode().getUnspscCode();
		String unspscTitle = UNSPSCTitleFilterBean.getSearchValue().toString();

		log.info(".........................titleFilterBean="+ UNSPSCTitleFilterBean.getSearchValue());
		log.info(".......................unspscCode="+ unspscCode);
		log.info("................................unspscTitle="+ unspscTitle);

		if ((unspscCode == null && (unspscTitle == null || unspscTitle
				.isEmpty()))
				|| ((unspscCode != null && unspscCode.isEmpty()) && (unspscTitle != null && unspscTitle
						.isEmpty()))) {
			throw new EpException(MessageConst.ERROR_716006);
		}

//		if (unspscCode != null && !unspscCode.isEmpty()
//				&& unspscCode.length() != 8) {
//			throw new EpException(MessageConst.ERROR_715026);
//		}

		searchDataModel.reloadData();	}

	
	private class SearchPagingDataModel extends PagingDataModel<CmUnspsc> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("unspscTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<CmUnspsc> pageChangeListener(
				ISearchBean<CmUnspsc> searchBean) {
			System.out.println("UNSPSCTitleFilterBean.getSearchValue()="+ UNSPSCTitleFilterBean.getSearchValue());
		

			if (StringUtils.isBlank((String) UNSPSCCodeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) UNSPSCTitleFilterBean
							.getSearchValue())) {
				System.out.println("ssssssssssssss");
		return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {
				System.out.println("ssssddssssssssss");
				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);


				if (StringUtils.isNotBlank((String) UNSPSCTitleFilterBean
						.getSearchValue())) {
					System.out.println("*********UNSPSCTitleFilterBean***********");
					UNSPSCTitleFilterBean.setSearchField("UNSPSCCode");
					UNSPSCTitleFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(UNSPSCTitleFilterBean);
				}
				FacesScopes fs = new FacesScopes();
				String itemType = fs.getParameter("itemType");

				log.info("............itemType=" + itemType);
				if (itemType != null) {
					if (itemType.equals("S") || itemType.equals("2")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_GREATER_EQUAL
												.value(), "70000000"));
					} else if (itemType.equals("P") || itemType.equals("1")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("unspscCode",
										SearchOperator.OPERATOR_LESS.value(),
										"70000000"));
					}
				}

				/*
				 * levelFilterBean.setSearchField("unspscLevel");
				 * levelFilterBean.setSearchValue(3);
				 * searchBean.getDataFilterBeans().add(levelFilterBean);
				 */

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
			System.out.println("UNSPSCTitleFilterBean.getSearchValue()="+ UNSPSCTitleFilterBean.getSearchValue());
		


			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			statusFilterBean.setSearchField("trackingDiaryId");
			statusFilterBean.setSearchValue(new Long(4));
			searchBean.getDataFilterBeans().add(statusFilterBean);
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


	public String goToTrackingDiarySearch(CmUnspsc unspscObj) {
		log.debug("*******www***goToExtensionSearch************"+ unspscObj.getUnspscCode());

		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CODIFICATION_DETAIL_SEARCH + "?faces-redirect=true";
	}


	public String reset() {
		System.out.println("In reset action...................");
		UNSPSCTitleFilterBean = null;
		UNSPSCCodeFilterBean = (null);

		UNSPSCTitleFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		setUNSPSCCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
//		extensionCodeFilterBean = null;
		statusFilterBean = (null);

//		extensionCodeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		setStatusFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));		
		this.setSearchDataModel(new SearchPagingDataModel());
//		this.getManagedBean().setAutoCompleteUnspscCode(null);
		System.out.println(" reset.........................");
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

	public SearchFilterBean getUNSPSCTitleFilterBean() {
		return UNSPSCTitleFilterBean;
	}

	public void setUNSPSCTitleFilterBean(SearchFilterBean UNSPSCTitleFilterBean) {
		this.UNSPSCTitleFilterBean = UNSPSCTitleFilterBean;
	}

	public SearchFilterBean getUNSPSCCodeFilterBean() {
		return UNSPSCCodeFilterBean;
	}

	public void setUNSPSCCodeFilterBean(SearchFilterBean uNSPSCCodeFilterBean) {
		UNSPSCCodeFilterBean = uNSPSCCodeFilterBean;
	}

	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
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


}

