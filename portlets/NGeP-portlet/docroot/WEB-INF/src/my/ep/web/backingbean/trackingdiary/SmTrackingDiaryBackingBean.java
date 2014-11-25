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
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
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
	private SearchFilterBean businessTypeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean regStatusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean MOFAccountFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean businessRegNoFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean companyNameFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private IPagingDataModel<SmSupplier> searchDataModel = new SearchPagingDataModel();
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();
	public SmTrackingDiaryBackingBean() {
	    String smSupplierCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("smSupplierCode");
	    System.out.println("smSupplierCode====================================: "+smSupplierCode);
		SmSupplier smSupplier = new SmSupplier();
	}
	
	public void smSupplierSearch() {
		String smSupplierTitle = businessRegNoFilterBean.getSearchValue().toString();

		log.info(".........................businessRegNoFilterBean="+ businessRegNoFilterBean.getSearchValue());
//		log.info(".......................smSupplierCode="+ smSupplierCode);
		log.info("................................smSupplierTitle="+ smSupplierTitle);

//		if ((smSupplierCode == null && (smSupplierTitle == null || smSupplierTitle
//				.isEmpty()))
//				|| ((smSupplierCode != null && smSupplierCode.isEmpty()) && (smSupplierTitle != null && smSupplierTitle
//						.isEmpty()))) {
//			throw new EpException(MessageConst.ERROR_716006);
//		}

//		if (smSupplierCode != null && !smSupplierCode.isEmpty()
//				&& smSupplierCode.length() != 8) {
//			throw new EpException(MessageConst.ERROR_715026);
//		}

		searchDataModel.reloadData();	}

	
	private class SearchPagingDataModel extends PagingDataModel<SmSupplier> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("smSupplierTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			System.out.println("businessRegNoFilterBean.getSearchValue()="+ businessRegNoFilterBean.getSearchValue());
		

			if (StringUtils.isBlank((String) businessRegNoFilterBean.getSearchValue())
					&& StringUtils.isBlank((String) businessRegNoFilterBean
							.getSearchValue())) {
				System.out.println("ssssssssssssss");
		return new SearchResultBean();
			}

			ISupplierSearchSO smSupplierSO = null;
			try {
				System.out.println("ssssddssssssssss");
				smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
						.getServiceObject("cm", ISupplierSearchSO.class);


				if (StringUtils.isNotBlank((String) businessRegNoFilterBean
						.getSearchValue())) {
					System.out.println("*********businessRegNoFilterBean***********");
					businessRegNoFilterBean.setSearchField("smSupplierTitle");
					businessRegNoFilterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(businessRegNoFilterBean);
				}
				FacesScopes fs = new FacesScopes();
				String itemType = fs.getParameter("itemType");

				log.info("............itemType=" + itemType);
				if (itemType != null) {
					if (itemType.equals("S") || itemType.equals("2")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("smSupplierCode",
										SearchOperator.OPERATOR_GREATER_EQUAL
												.value(), "70000000"));
					} else if (itemType.equals("P") || itemType.equals("1")) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("smSupplierCode",
										SearchOperator.OPERATOR_LESS.value(),
										"70000000"));
					}
				}

				/*
				 * levelFilterBean.setSearchField("smSupplierLevel");
				 * levelFilterBean.setSearchValue(3);
				 * searchBean.getDataFilterBeans().add(levelFilterBean);
				 */

				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSearchBean(searchBean);
				System.out.println("*********b4***********");
				ISearchResultBean<SmSupplier> res = smSupplierSO.retriveSupplierList(searchBean);

				System.out.println("*********after***********");
				return res;
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
			System.out.println("businessRegNoFilterBean.getSearchValue()="+ businessRegNoFilterBean.getSearchValue());
		


			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
//			statusFilterBean.setSearchField("trackingDiaryId");
//			statusFilterBean.setSearchValue(new Long(4));
//			searchBean.getDataFilterBeans().add(statusFilterBean);
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


	public String goToTrackingDiarySearch(SmSupplier smSupplierObj) {
		log.debug("*******www***goToExtensionSearch************");

		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_SMSUPPLIER_DETAIL_SEARCH + "?faces-redirect=true";
	}


	public String reset() {
		return null;
	}

	public String backToPrev() {

		return NAV_TO_SMSUPPLIER_SEARCH;
	}

	public IPagingDataModel<SmSupplier> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(IPagingDataModel<SmSupplier> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	public SearchFilterBean getBusinessRegNoFilterBean() {
		return businessRegNoFilterBean;
	}

	public void setBusinessRegNoFilterBean(SearchFilterBean businessRegNoFilterBean) {
		this.businessRegNoFilterBean = businessRegNoFilterBean;
	}

	public SearchFilterBean getRegStatusFilterBean() {
		return regStatusFilterBean;
	}

	public void setRegStatusFilterBean(SearchFilterBean regStatusFilterBean) {
		this.regStatusFilterBean = regStatusFilterBean;
	}

	public SearchFilterBean getBusinessTypeFilterBean() {
		return businessTypeFilterBean;
	}

	public void setBusinessTypeFilterBean(SearchFilterBean businessTypeFilterBean) {
		this.businessTypeFilterBean = businessTypeFilterBean;
	}

	public SearchFilterBean getMOFAccountFilterBean() {
		return MOFAccountFilterBean;
	}

	public void setMOFAccountFilterBean(SearchFilterBean mOFAccountFilterBean) {
		MOFAccountFilterBean = mOFAccountFilterBean;
	}

	public SearchFilterBean getCompanyNameFilterBean() {
		return companyNameFilterBean;
	}

	public void setCompanyNameFilterBean(SearchFilterBean companyNameFilterBean) {
		this.companyNameFilterBean = companyNameFilterBean;
	}


}

