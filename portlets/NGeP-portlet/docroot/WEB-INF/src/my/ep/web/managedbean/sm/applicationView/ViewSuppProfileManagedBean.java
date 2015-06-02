package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "viewSuppProfileMB")
@SessionScoped
public class ViewSuppProfileManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029253736925390560L;
	private SmSupplier selectedSupplier;
	private IPagingDataModel<SmSupplier> smSupplierDataModel = new SmSupplierPagingDataModel();

	public ViewSuppProfileManagedBean() {

	}

	private class SmSupplierPagingDataModel extends PagingDataModel<SmSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);
			reqDTO.setViewApplicationHistory(isViewApplicationHistory());

			try {
				if (!isSupplierUser()) {
					if (!isSearchCriteriaEmpty()) {
						return getApplicationHistoryResultBean(reqDTO);
					} else {
						return new SearchResultBean();
					}
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmSupplier> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}

	/**
	 * Search supplier latest application
	 */
	public void searchSupplier() {
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<SmSupplier> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<SmSupplier> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

}
