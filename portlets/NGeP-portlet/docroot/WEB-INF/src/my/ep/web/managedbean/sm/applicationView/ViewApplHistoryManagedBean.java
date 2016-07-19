package my.ep.web.managedbean.sm.applicationView;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
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
	private List<SelectItem> applTypeItems;
	private List<SelectItem> applStatusItems;

	public ViewApplHistoryManagedBean() {
		applTypeItems = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.basic", null));
		item.setValue(ParameterConst.BASIC_ACCOUNT);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.newmof", null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_MOF);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.bumiputera",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_BUMI);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.categorycode",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.updateprofile",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_UPDATE);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.renewmof", null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil
				.getMessage("lbl.sm.appltype.cancelmof", null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_CANCEL);
		applTypeItems.add(item);

		applStatusItems = new ArrayList<SelectItem>();
		List<PmStatusDesc> statusDescList = null;
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setLanguageCode(new FacesScopes().getUserLocale()
				.getLanguage());
		statusDescList = SmServiceLookup.getStatusSO()
				.retrieveAllSupplierApplicationStatus(statusReqDTO)
				.getStatusDescList();
		if (!EntityHelper.isCollectionEmpty(statusDescList)) {
			for (PmStatusDesc pd : statusDescList) {
				item = new SelectItem();
				item.setLabel(pd.getStatusName());
				item.setValue(pd.getPmStatus().getStatusId());
				applStatusItems.add(item);
			}
		}
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
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveApplicationHistoryUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();
		}
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

	public List<SelectItem> getApplTypeItems() {
		return applTypeItems;
	}

	public void setApplTypeItems(List<SelectItem> applTypeItems) {
		this.applTypeItems = applTypeItems;
	}

	public List<SelectItem> getApplStatusItems() {
		return applStatusItems;
	}

	public void setApplStatusItems(List<SelectItem> applStatusItems) {
		this.applStatusItems = applStatusItems;
	}

}
