package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "viewApplHistoryMB")
@SessionScoped
public class ViewApplHistoryManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1191700333390541593L;
	private SmAppl selectedApplHistory;
	private IPagingDataModel<SmAppl> smSupplierDataModel = new SmSupplierPagingDataModel();

	public ViewApplHistoryManagedBean() {

	}

	private class SmSupplierPagingDataModel extends PagingDataModel<SmAppl> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
		}

		@Override
		protected ISearchResultBean<SmAppl> pageChangeListener(
				ISearchBean<SmAppl> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSearchBean(searchBean);
			reqDTO.setViewApplicationHistory(isViewApplicationHistory());

			try {
				if (!isSupplierUser()) {
					if (!isSearchCriteriaEmpty()) {
						return getApplicationHistoryResultBean(reqDTO);
					} else {
						return new SearchResultBean();
					}
				} else {
					return getApplicationHistoryResultBean(reqDTO);
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmAppl> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveApplicationHistoryBySupplierSearchCriteria(reqDTO)
					.getResultBean();
		}
	}

	/**
	 * Search supplier application history
	 */
	public void searchSupplier() {
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<SmAppl> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<SmAppl> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmAppl getSelectedApplHistory() {
		return selectedApplHistory;
	}

	public void setSelectedApplHistory(SmAppl selectedApplHistory) {
		this.selectedApplHistory = selectedApplHistory;
	}

}
