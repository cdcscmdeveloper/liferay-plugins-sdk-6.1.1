package my.ep.web.managedbean.sc.bidding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPackage;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.chart.CartesianChartModel;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "biddingMB")
@SessionScoped
public class BiddingManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private List<ScBidSchedule> scheduledBidList;
	private ScBidAction bidAction;
	private String supplierNotes;

	private ScBidSchedule scheduledBid;

	private Long[] statusId;
	private List<Long> statusIdList;

	private List<PmStatusDesc> statusDescList;
	private List<PmStatusDesc> selectedStatusDescList;
	
	private String searchBiddingNo;
	private String searchDocumentNo;
	private String searchBidTitle;
	private Date searchBiddingDateFrom;
	private Date searchBiddingDateTo;

	private Date createdDateFrom;
	private Date createdDateTo;

	
	private List<ScBidPackage> bidPackages;

	private Long remainingTime;
	private List zoneNameList;
	private List viewByList;
	
	private String status;

	private List itemList;

	private String createdBy;

	public BiddingManagedBean() {

		retrieveSatusDescriptionList();

	}

	public void retrieveSatusDescriptionList() {
		IStatusSO statusSO = null;

		try {
			statusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IStatusSO.class);
			StatusReqDTO reqDTO = new StatusReqDTO();
			StatusResDTO resDTO = new StatusResDTO();
			reqDTO.setModuleCode("SC");
			reqDTO.setDocType("FA");
			reqDTO.setLanguageCode("en");
			resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
			if (resDTO.getStatusDescList() != null) {
				statusDescList = resDTO.getStatusDescList();
			} else {
				statusDescList = new ArrayList<PmStatusDesc>();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			statusSO = null;
		}

	}
	
	
	public List<ScBidPackage> getBidPackages() {
		return bidPackages;
	}

	public void setBidPackages(List<ScBidPackage> bidPackages) {
		this.bidPackages = bidPackages;
	}

	public List<Long> getStatusIdList() {
		return statusIdList;
	}

	public void setStatusIdList(List<Long> statusIdList) {
		this.statusIdList = statusIdList;
	}

	public List<PmStatusDesc> getSelectedStatusDescList() {
		return selectedStatusDescList;
	}

	public void setSelectedStatusDescList(
			List<PmStatusDesc> selectedStatusDescList) {
		this.selectedStatusDescList = selectedStatusDescList;
	}

	public String getSearchBiddingNo() {
		return searchBiddingNo;
	}

	public void setSearchBiddingNo(String searchBiddingNo) {
		this.searchBiddingNo = searchBiddingNo;
	}

	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}

	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}

	public String getSearchBidTitle() {
		return searchBidTitle;
	}

	public void setSearchBidTitle(String searchBidTitle) {
		this.searchBidTitle = searchBidTitle;
	}

	public Date getSearchBiddingDateFrom() {
		return searchBiddingDateFrom;
	}

	public void setSearchBiddingDateFrom(Date searchBiddingDateFrom) {
		this.searchBiddingDateFrom = searchBiddingDateFrom;
	}

	public Date getSearchBiddingDateTo() {
		return searchBiddingDateTo;
	}

	public void setSearchBiddingDateTo(Date searchBiddingDateTo) {
		this.searchBiddingDateTo = searchBiddingDateTo;
	}

	public String getSupplierNotes() {
		return supplierNotes;
	}

	public void setSupplierNotes(String supplierNotes) {
		this.supplierNotes = supplierNotes;
	}

	public ScBidAction getBidAction() {
		return bidAction;
	}

	public void setBidAction(ScBidAction bidAction) {
		this.bidAction = bidAction;
	}

	public IPagingDataModel<ScBidSchedule> getSearchDataModel() {
		return searchDataModel;
	}

	public void setSearchDataModel(
			IPagingDataModel<ScBidSchedule> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}

	public List<ScBidSchedule> getScheduledBidList() {
		return scheduledBidList;
	}

	public void setScheduledBidList(List<ScBidSchedule> scheduledBidList) {
		this.scheduledBidList = scheduledBidList;
	}

	public Long[] getStatusId() {
		return statusId;
	}

	public void setStatusId(Long[] statusId) {
		this.statusId = statusId;
	}

	public List<PmStatusDesc> getStatusDescList() {
		if (statusDescList == null)
			statusDescList = new ArrayList<PmStatusDesc>();
		return statusDescList;
	}

	public void setStatusDescList(List<PmStatusDesc> statusDescList) {
		this.statusDescList = statusDescList;
	}

	public ScBidSchedule getScheduledBid() {
		return scheduledBid;
	}

	public void setScheduledBid(ScBidSchedule scheduledBid) {
		this.scheduledBid = scheduledBid;
	}

	private CartesianChartModel linearModel;

	public Long getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(Long duration) {
		this.remainingTime = duration;
	}

	public CartesianChartModel getLinearModel() {
		return linearModel;
	}

	public void setLinearModel(CartesianChartModel linearModel) {
		this.linearModel = linearModel;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List getZoneNameList() {
		return zoneNameList;
	}

	public void setZoneNameList(List zoneNameList) {
		this.zoneNameList = zoneNameList;
	}

	public List getViewByList() {
		return viewByList;
	}

	public void setViewByList(List viewByList) {
		this.viewByList = viewByList;
	}

	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList = itemList;
	}

	public List getRankingList() {
		return rankingList;
	}

	public void setRankingList(List rankingList) {
		this.rankingList = rankingList;
	}

	private List rankingList;

	

	public Date getCreatedDateFrom() {
		return createdDateFrom;
	}

	public void setCreatedDateFrom(Date createdDateFrom) {
		this.createdDateFrom = createdDateFrom;
	}

	public Date getCreatedDateTo() {
		return createdDateTo;
	}

	public void setCreatedDateTo(Date createdDateTo) {
		this.createdDateTo = createdDateTo;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	private IPagingDataModel<ScBidSchedule> searchDataModel = new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<ScBidSchedule> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("");

		}

		@Override
		protected ISearchResultBean<ScBidSchedule> pageChangeListener(
				ISearchBean<ScBidSchedule> bidSchSearchBean) {

			if (getSearchBiddingNo() == null && getSearchDocumentNo() == null
					&& getSearchBidTitle() == null
					&& getSearchBiddingDateFrom() == null
					&& getSearchBiddingDateTo() == null
					&& getSelectedStatusDescList() == null) {
				System.out.println("******new BidSchSearchBean returned******");

				// return SearchResultbean();//bidSchResultBean();

			}

			IBiddingSO biddingSO = null;

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				if (getSearchBiddingNo() != null) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("bidNo",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchBiddingNo()));
				}

				if (getSearchDocumentNo() != null) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("docNo",
									SearchOperator.OPERATOR_EQUALS.value(),
									getSearchDocumentNo()));
				}

				if (getSearchBidTitle() != null) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("docTitle",
									SearchOperator.OPERATOR_LIKE.value(),
									getSearchBidTitle()));
				}

				// should be like bidstart from date to bidstart to date
				if (getSearchBiddingDateFrom() != null
						&& getSearchBiddingDateTo() != null) {

					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("startDate");
					bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
							.value());
					SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
							getSearchBiddingDateFrom(),
							getSearchBiddingDateTo());
					bean.setSearchValue(betweenVal);
					bidSchSearchBean.getDataFilterBeans().add(bean);
				}

				setStatusIdList(new ArrayList<Long>());
				System.out.println("statusID:" + statusId);
				for (Long status : statusId) {
					System.out.println("status:" + status);
					statusIdList.add(status);
				}

				System.out
						.println("statusIdList.size():" + statusIdList.size());
				if (statusIdList != null && statusIdList.size() > 0) {
					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("bidStatusId",
									SearchOperator.OPERATOR_IN.value(),
									getStatusIdList()));
				}

				// Order by created date
				bidSchSearchBean.getSortColumns().add(
						new SearchOrderBean("startDate", false));

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

	public void searchForScheduledBids() {
		searchDataModel.reloadData();
	}

}