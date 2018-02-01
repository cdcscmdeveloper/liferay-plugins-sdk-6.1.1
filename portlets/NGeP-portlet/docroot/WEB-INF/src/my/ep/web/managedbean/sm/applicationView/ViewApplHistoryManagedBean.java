package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constant.sm.SupplierManagementConst;
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

@ManagedBean(name = "viewApplHistoryMB")
@SessionScoped
public class ViewApplHistoryManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1191700333390541593L;
	private SmAppl selectedApplHistory;
	private boolean claimTask;
	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();
	private PmTrackingDiaryBackingBean trackingDiary;

	public ViewApplHistoryManagedBean() {

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

//			int i = 0;
//			for (Object[] obj : retVal.getResultList()) {
//				obj[6] = getWorkflowStatus(Long.parseLong(obj[10] + ""), obj[6]
//						+ "");
//				retVal.getResultList().set(i, obj);
//				i++;
//			}
			return retVal;
		}
	}

	private String getWorkflowStatus(Long applId, String statusOri) {
		SmWorkflowStatus st = SmCommonUtil
				.getCurrentActiveWokFlowStatus(applId);
		String retVal = statusOri;
		if (st.getPmStatus() != null
				&& st.getPmStatus().getStatusId() != null) {
			PmStatusDesc sd = SmCommonUtil.getStatusDescByStatusId(st
					.getPmStatus().getStatusId());
			if (sd != null) {
				retVal = sd.getStatusName();
			}
		}
		return retVal;
	}

	/**
	 * Search supplier application history
	 */
	public void searchSupplier() {
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

	public PmTrackingDiaryBackingBean getTrackingDiary() {
		if (trackingDiary == null) {
			return new PmTrackingDiaryBackingBean();
		} else {
			return trackingDiary;
		}
	}

	public void setTrackingDiary(PmTrackingDiaryBackingBean trackingDiary) {
		this.trackingDiary = trackingDiary;
	}

}
