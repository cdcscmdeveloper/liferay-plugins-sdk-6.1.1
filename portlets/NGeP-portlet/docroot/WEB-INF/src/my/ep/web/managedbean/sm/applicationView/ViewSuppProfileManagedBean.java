package my.ep.web.managedbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "viewSuppProfileMB")
@SessionScoped
public class ViewSuppProfileManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029253736925390560L;
	private boolean supplierUser;
	private boolean poUser;
	private boolean approverUser;
	private boolean mofSupplier;
	private int searchBy;
	private SmSupplier searchSupplier;
	private SmAppl selectedApplHistory;
	private IPagingDataModel<SmAppl> smSupplierDataModel = new SmSupplierPagingDataModel();

	public ViewSuppProfileManagedBean() {

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
			SmApplRevisionResDTO resDTO = new SmApplRevisionResDTO();

			try {
				if (!isSupplierUser()) {
					if (StringUtils.isNotBlank(getSearchSupplier()
							.getCompanyName())
							|| StringUtils.isNotBlank(getSearchSupplier()
									.getEpNo())
							|| StringUtils.isNotBlank(getSearchSupplier()
									.getRegNo())) {
						reqDTO.setSmSupplier(getSearchSupplier());
						resDTO = SmServiceLookup
								.getSmApplRevisionSO()
								.retrieveApplicationHistoryBySupplierSearchCriteria(
										reqDTO);
						return resDTO.getResultBean();
					} else {
						return new SearchResultBean();
					}
				} else {
					reqDTO.setSmSupplier(getSearchSupplier());
					resDTO = SmServiceLookup
							.getSmApplRevisionSO()
							.retrieveApplicationHistoryBySupplierSearchCriteria(
									reqDTO);
					return resDTO.getResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
				resDTO = null;
			}
		}
	}

	/**
	 * Search supplier application history
	 */
	public void searchSupplier() {
		smSupplierDataModel.reloadData();
	}

	public boolean isSupplierUser() {
		return supplierUser;
	}

	public void setSupplierUser(boolean supplierUser) {
		this.supplierUser = supplierUser;
	}

	public boolean isPoUser() {
		return poUser;
	}

	public void setPoUser(boolean poUser) {
		this.poUser = poUser;
	}

	public boolean isApproverUser() {
		return approverUser;
	}

	public void setApproverUser(boolean approverUser) {
		this.approverUser = approverUser;
	}

	public boolean isMofSupplier() {
		return mofSupplier;
	}

	public void setMofSupplier(boolean mofSupplier) {
		this.mofSupplier = mofSupplier;
	}

	public SmSupplier getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(SmSupplier searchSupplier) {
		this.searchSupplier = searchSupplier;
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

	public int getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(int searchBy) {
		this.searchBy = searchBy;
	}

}
