package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CdTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "cdTrackingDiaryBackingBean")
@SessionScoped
public class CdTrackingDiaryBackingBean extends
		BaseBackingBean<CdTrackingDiaryManagedBean> {

	private static final String NAV_TO_EXTENSION_SEARCH = null;
	private static Logger log = Logger.getLogger(CdTrackingDiaryBackingBean.class);
	private SearchFilterBean codeTitleFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean UNSPSCCodeFilterBean 		= new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
//	private SearchFilterBean extensionCodeTypeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean statusFilterBean 			= new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private IPagingDataModel<CmUnspsc> searchDataModel 	= new SearchPagingDataModel();

	public CdTrackingDiaryBackingBean() {
	    String unspscCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unspscCode");
	    System.out.println("unspscCode====================================: "+unspscCode);
		CmUnspsc cmUnspsc = new CmUnspsc();
	}

	public void searchTrackingDiary() {
	    String unspscCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("unspscCode");
	    System.out.println("unspscCode====================================: "+unspscCode);

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
			System.out.println("codeTitleFilterBean.getSearchValue()="+ codeTitleFilterBean.getSearchValue());
		

			if (StringUtils.isBlank((String) UNSPSCCodeFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) codeTitleFilterBean
							.getSearchValue())) {
				return new SearchResultBean();
			}

			ICodificationSO unspscSO = null;
			try {

				unspscSO = (ICodificationSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ICodificationSO.class);


				if (StringUtils.isNotBlank((String) codeTitleFilterBean
						.getSearchValue())) {
					System.out.println("*********codeTitleFilterBean***********");
					codeTitleFilterBean.setSearchField("unspscTitle");
					codeTitleFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(codeTitleFilterBean);
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

	public String goToExtensionSearch(CmUnspsc unspscObj) {
		log.debug("**********goToExtensionSearch************");

		return NAV_TO_EXTENSION_SEARCH + "?faces-redirect=true";
	}


	public String resetAction() {
		System.out.println("In reset action...................");
		codeTitleFilterBean = null;
		UNSPSCCodeFilterBean = (null);

		codeTitleFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		setUNSPSCCodeFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));
//		extensionCodeTypeFilterBean = null;
		statusFilterBean = (null);

//		extensionCodeTypeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
		setStatusFilterBean(new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value()));		this.setSearchDataModel(new SearchPagingDataModel());
//		this.getManagedBean().setAutoCompleteUnspscCode(null);
		System.out.println(" reset.........................");
		return null;
	}

	public IPagingDataModel<CmUnspsc> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<CmUnspsc> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public SearchFilterBean getCodeTitleFilterBean() {
		return codeTitleFilterBean;
	}

	public void setCodeTitleFilterBean(SearchFilterBean codeTitleFilterBean) {
		this.codeTitleFilterBean = codeTitleFilterBean;
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

}

