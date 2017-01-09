package my.ep.web.managedbean.sm.customerservice;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smCustServiceMB")
@SessionScoped
public class SmCustomerServiceManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -659689758644790222L;
	private int targetPage;
	private int noOfCatPublished;
	private int noOfCatExpired;
	private int noOfCatApproved;
	private int noOfCatRejected;
	private String mofRegStatus;
	private SmPersonnel suppAdmin;

	private IPagingDataModel<Object[]> smSupplierDataModel = new SmSupplierPagingDataModel();

	public SmCustomerServiceManagedBean() {

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
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setEpNo(getSearchEpNo());
			reqDTO.setApplicationNo(getSearchApplNo());
			reqDTO.setPmStatusId(getSearchApplStatusId());
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveApplicationHistoryUsingNativeQuery(reqDTO)
					.getObjectArrResultBean();
		}
	}

	public int getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(int targetPage) {
		this.targetPage = targetPage;
	}

	public IPagingDataModel<Object[]> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<Object[]> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public String getMofRegStatus() {
		return mofRegStatus;
	}

	public void setMofRegStatus(String mofRegStatus) {
		this.mofRegStatus = mofRegStatus;
	}

	public SmPersonnel getSuppAdmin() {
		return suppAdmin;
	}

	public void setSuppAdmin(SmPersonnel suppAdmin) {
		this.suppAdmin = suppAdmin;
	}

	public int getNoOfCatPublished() {
		return noOfCatPublished;
	}

	public void setNoOfCatPublished(int noOfCatPublished) {
		this.noOfCatPublished = noOfCatPublished;
	}

	public int getNoOfCatExpired() {
		return noOfCatExpired;
	}

	public void setNoOfCatExpired(int noOfCatExpired) {
		this.noOfCatExpired = noOfCatExpired;
	}

	public int getNoOfCatApproved() {
		return noOfCatApproved;
	}

	public void setNoOfCatApproved(int noOfCatApproved) {
		this.noOfCatApproved = noOfCatApproved;
	}

	public int getNoOfCatRejected() {
		return noOfCatRejected;
	}

	public void setNoOfCatRejected(int noOfCatRejected) {
		this.noOfCatRejected = noOfCatRejected;
	}

}
