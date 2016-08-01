package my.ep.web.managedbean.sm.changeapplicationflow;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmSupplierBasicInfoVO;

@ManagedBean(name = "changeApplicationFlowManagedBean")
@SessionScoped
public class ChangeApplicationFlowManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4094303372165130649L;
	private String currStatusName;
	private Long selectedStatusId;
	private List<SelectItem> statusItems;
	private SmSupplierBasicInfoVO basicInfo;
	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();

	private class SmSupplierPagingDataModel extends PagingDataModel<Object[]> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("changeApplFlowDtId");
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
			reqDTO.setActiveAppl(true);
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

	public Long getSelectedStatusId() {
		return selectedStatusId;
	}

	public void setSelectedStatusId(Long selectedStatusId) {
		this.selectedStatusId = selectedStatusId;
	}

	public List<SelectItem> getStatusItems() {
		return statusItems;
	}

	public void setStatusItems(List<SelectItem> statusItems) {
		this.statusItems = statusItems;
	}

	public String getCurrStatusName() {
		return currStatusName;
	}

	public void setCurrStatusName(String currStatusName) {
		this.currStatusName = currStatusName;
	}

	public SmSupplierBasicInfoVO getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(SmSupplierBasicInfoVO basicInfo) {
		this.basicInfo = basicInfo;
	}

}
