package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "viewHistoryMB")
@SessionScoped
public class ViewHistoryManagedBean extends BaseViewManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8766816149866269965L;
	private SmAppl selectedApplHistory;
	private boolean claimTask;
	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();
	private boolean alreadyLoadCompanyInfo;

	public ViewHistoryManagedBean() {

	}

	private class SmSupplierPagingDataModel extends PagingDataModel<Object[]> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setObjArrSearchBean(searchBean);
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

		private ISearchResultBean<Object[]> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			reqDTO.setEpNo(getSearchEpNo());
			reqDTO.setApplicationType(getSearchApplType());
			reqDTO.setApplicationNo(getSearchApplNo());
			reqDTO.setPmStatusId(getSearchApplStatusId());

			ISearchResultBean<Object[]> retVal = SmServiceLookup
					.getSmApplRevisionSO()
					.retrieveApplicationHistoryUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();
			return retVal;
		}
	}

	/**
	 * Search supplier application history
	 */
	public void searchSupplier() {
		validateAtLeastOneSearchCriteria();
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<Object[]> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<Object[]> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmAppl getSelectedApplHistory() {
		return selectedApplHistory;
	}

	public void setSelectedApplHistory(SmAppl selectedApplHistory) {
		this.selectedApplHistory = selectedApplHistory;
	}

	public boolean isClaimTask() {
		return claimTask;
	}

	public void setClaimTask(boolean claimTask) {
		this.claimTask = claimTask;
	}

	public boolean isAlreadyLoadCompanyInfo() {
		return alreadyLoadCompanyInfo;
	}

	public void setAlreadyLoadCompanyInfo(boolean alreadyLoadCompanyInfo) {
		this.alreadyLoadCompanyInfo = alreadyLoadCompanyInfo;
	}
}
