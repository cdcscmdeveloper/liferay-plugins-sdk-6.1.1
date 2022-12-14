package my.ep.web.managedbean.sc.bidding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Bidding List Managed Bean
 * 
 * @author Venkat Chanumolu
 */

@ManagedBean(name = "biddingListMB")
@SessionScoped
public class BiddingListManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private SelectItem[] statusDescs;
	private Long selectedStatusDesc;
	private List<Long> statusIdList;
	private Date searchBiddingDateTo;
	private List<ScBidSchedule> scheduledBidList;
	private ScBidSchedule selectedBidSchedule;

	/**
	 * Default Constructor
	 */

	public BiddingListManagedBean() {
	}

	/**
	 * Method For searching bid schedules
	 */

	public void searchForScheduledBids() {
		searchDataModel.reloadData();
	}

	/**
	 * @return the scheduledBidList
	 */
	public List<ScBidSchedule> getScheduledBidList() {
		return scheduledBidList;
	}

	/**
	 * @param scheduledBidList
	 *            the scheduledBidList to set
	 */
	public void setScheduledBidList(List<ScBidSchedule> scheduledBidList) {
		this.scheduledBidList = scheduledBidList;
	}

	/**
	 * @return the statusDescs
	 */
	public SelectItem[] getStatusDescs() {
		return statusDescs;
	}

	/**
	 * @param statusDescs
	 *            the statusDescs to set
	 */
	public void setStatusDescs(SelectItem[] statusDescs) {
		this.statusDescs = statusDescs;
	}

	/**
	 * @return the selectedStatusDescList
	 */
	public Long getSelectedStatusDesc() {
		return selectedStatusDesc;
	}

	/**
	 * @param selectedStatusDescList
	 *            the selectedStatusDescList to set
	 */
	public void setSelectedStatusDesc(Long selectedStatusDesc) {
		this.selectedStatusDesc = selectedStatusDesc;
	}

	/**
	 * @return the searchBiddingDateTo
	 */
	public Date getSearchBiddingDateTo() {
		return searchBiddingDateTo;
	}

	/**
	 * @return the statusIdList
	 */
	public List<Long> getStatusIdList() {
		if (this.statusIdList == null || this.statusIdList.size() != 0) {
			this.statusIdList = new ArrayList<Long>();
			this.statusIdList.add(BidStatusConst.BID_PENDING_BIDDING);
			this.statusIdList.add(BidStatusConst.BID_IN_PROGRESS);
			this.statusIdList.add(BidStatusConst.BID_RESCHEDULE_BIDDING);
		}
		return statusIdList;
	}

	/**
	 * @param statusIdList
	 *            the statusIdList to set
	 */
	public void setStatusIdList(List<Long> statusIdList) {
		this.statusIdList = statusIdList;
	}

	/**
	 * @param searchBiddingDateTo
	 *            the searchBiddingDateTo to set
	 */
	public void setSearchBiddingDateTo(Date searchBiddingDateTo) {
		this.searchBiddingDateTo = searchBiddingDateTo;
	}
	

	/**
	 * @return the selectedBidSchedule
	 */
	public ScBidSchedule getSelectedBidSchedule() {
		return selectedBidSchedule;
	}

	/**
	 * @param selectedBidSchedule the selectedBidSchedule to set
	 */
	public void setSelectedBidSchedule(ScBidSchedule selectedBidSchedule) {
		this.selectedBidSchedule = selectedBidSchedule;
	}

	/**
	 * @return the searchDataModel
	 */
	public IPagingDataModel<ScBidSchedule> getSearchDataModel() {
		return searchDataModel;
	}

	/**
	 * @param searchDataModel
	 *            the searchDataModel to set
	 */
	public void setSearchDataModel(
			IPagingDataModel<ScBidSchedule> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	private IPagingDataModel<ScBidSchedule> searchDataModel = new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<ScBidSchedule> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("biddingList", true);
		}

		@Override
		public ISearchResultBean<ScBidSchedule> pageChangeListener(
				ISearchBean<ScBidSchedule> bidSchSearchBean) {

			IBiddingSO biddingSO = null;

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				// should be like bid start from today to bid end to date
				if (getSearchBiddingDateTo() != null) {

					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("startDate");
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							DateUtils.getCurrentDateWithoutTime(),
							getSearchBiddingDateTo());
					bean.setSearchValue(betweenVal);
					bidSchSearchBean.getDataFilterBeans().add(bean);
				} else {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("startDate",
									SearchOperator.OPERATOR_GREATER_EQUAL
											.value(), DateUtils
											.getCurrentDateWithoutTime()));
				}

				if (selectedStatusDesc != null && !selectedStatusDesc.equals(0)) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmStatus.statusId",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSelectedStatusDesc()));
				} else if (getStatusIdList() != null
						&& !getStatusIdList().isEmpty()) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmStatus.statusId",
									SearchOperator.OPERATOR_IN.value(),
									getStatusIdList()));
				}

				// Order by bid Start date
				bidSchSearchBean.getSortColumns().add(
						new SearchOrderBean("startDate", true));

				BiddingReqDTO reqDTO = new BiddingReqDTO();

				reqDTO.setBidSchSearchBean(bidSchSearchBean);
				BiddingResDTO resDTO = biddingSO
						.searchBiddingScheduleList(reqDTO);

				setScheduledBidList(resDTO.getBidSchResultBean()
						.getResultList());

				return resDTO.getBidSchResultBean();

			}

			finally {

				biddingSO = null;

			}

		}
	}
}