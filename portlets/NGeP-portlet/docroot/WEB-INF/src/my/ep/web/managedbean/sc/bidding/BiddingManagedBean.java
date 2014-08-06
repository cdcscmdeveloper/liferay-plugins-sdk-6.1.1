package my.ep.web.managedbean.sc.bidding;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPackage;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.chart.CartesianChartModel;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "biddingMB")
@SessionScoped
public class BiddingManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String searchBiddingNo;
	private List<PmStatusDesc> statusDescs;
	private List<Long> selectedStatusDescList;
	private String searchDocumentNo;
	private String searchBidTitle;
	private Date searchBiddingDateFrom;
	private Date searchBiddingDateTo;
	
	
	private Long remainingTime;
	private String duration;
	private String newBidDuration;
	private ScBidAction bidAction;
	private Date mindate;
	private Date maxdate;
	private String actionType;
	private Date newStartDate;
	private Date newEndDate;
	
		
	
	private List<ScBidSchedule> scheduledBidList;
	
	private String supplierNotes;
	private Long remainingBidTime;
	private ScBidSchedule scheduledBid;
	private List<ScBidPrice> scBidPriceList;
	private List<ScBidPrice> bidHistoryPriceList;
	
	private ScBidPrice historyPrice;
	
	private List<ScBidPrice> lastBidPricesList;
	private List<ScBidItem> scBidItemsList;

	private ScBidItem selectedtem;

	
	
	private CartesianChartModel bidPriceLinearModel;
	
	private CartesianChartModel itemPriceLinearModel;
	
	private CartesianChartModel historyPriceLinearModel;
	
	private Long supplierCode;
	
	private List<ScBidPackage> bidPackages;
	private BigDecimal lowestNonBumiOfferedPrice;
	
	private List<TplSpecZoneDtl> zoneDetailsList;
	private String selectedZone;

	private Date createdDateFrom;
	private Date createdDateTo;

	
	
	
	private String createdBy;

	private String bidManagementViewBy;
	private boolean rendered;
	
	public BiddingManagedBean() {

		
		bidAction = new ScBidAction();
		
	}
	
	public void searchForScheduledBids() {
		searchDataModel.reloadData();
	}
	
	
	/**
	 * @return the remainingBidTime
	 */
	public Long getRemainingBidTime() {

		if (scheduledBid != null) {

			remainingBidTime = durationCalculator(new Date(),
					scheduledBid.getEndDate());
		}
		return remainingBidTime;
	}
	
	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		long time = TimeUnit.SECONDS.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		return time;
	}

	/**
	 * @param remainingBidTime
	 *            the remainingBidTime to set
	 */
	public void setRemainingBidTime(Long remainingBidTime) {
		this.remainingBidTime = remainingBidTime;
	}

	/**
	 * @return the lowestNonBumiOfferedPrice
	 */
	public BigDecimal getLowestNonBumiOfferedPrice() {
		return lowestNonBumiOfferedPrice;
	}

	/**
	 * @param lowestNonBumiOfferedPrice the lowestNonBumiOfferedPrice to set
	 */
	public void setLowestNonBumiOfferedPrice(BigDecimal lowestNonBumiOfferedPrice) {
		this.lowestNonBumiOfferedPrice = lowestNonBumiOfferedPrice;
	}

	/**
	 * @return the newBidDuration
	 */
	public String getNewBidDuration() {
		return newBidDuration;
	}

	/**
	 * @param newBidDuration the newBidDuration to set
	 */
	public void setNewBidDuration(String newBidDuration) {
		this.newBidDuration = newBidDuration;
	}

	/**
	 * @return the maxdate
	 */
	public Date getMaxdate() {
		return maxdate;
	}

	/**
	 * @param maxdate the maxdate to set
	 */
	public void setMaxdate(Date maxdate) {
		this.maxdate = maxdate;
	}

	/**
	 * @return the remainingTime
	 */
	public Long getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime the remainingTime to set
	 */
	public void setRemainingTime(Long remainingTime) {
		this.remainingTime = remainingTime;
	}

	/**
	 * @return the mindate
	 */
	public Date getMindate() {
		return mindate;
	}

	/**
	 * @param mindate the mindate to set
	 */
	public void setMindate(Date mindate) {
		this.mindate = mindate;
	}

	/**
	 * @return the scheduledBidList
	 */
	public List<ScBidSchedule> getScheduledBidList() {
		return scheduledBidList;
	}



	/**
	 * @param scheduledBidList the scheduledBidList to set
	 */
	public void setScheduledBidList(List<ScBidSchedule> scheduledBidList) {
		this.scheduledBidList = scheduledBidList;
	}



	/**
	 * @return the bidAction
	 */
	public ScBidAction getBidAction() {
		return bidAction;
	}



	/**
	 * @param bidAction the bidAction to set
	 */
	public void setBidAction(ScBidAction bidAction) {
		this.bidAction = bidAction;
	}



	/**
	 * @return the supplierNotes
	 */
	public String getSupplierNotes() {
		return supplierNotes;
	}



	/**
	 * @param supplierNotes the supplierNotes to set
	 */
	public void setSupplierNotes(String supplierNotes) {
		this.supplierNotes = supplierNotes;
	}



	/**
	 * @return the scheduledBid
	 */
	public ScBidSchedule getScheduledBid() {
		return scheduledBid;
	}



	/**
	 * @param scheduledBid the scheduledBid to set
	 */
	public void setScheduledBid(ScBidSchedule scheduledBid) {
		this.scheduledBid = scheduledBid;
	}



	/**
	 * @return the scBidPriceList
	 */
	public List<ScBidPrice> getScBidPriceList() {
		return scBidPriceList;
	}



	/**
	 * @param scBidPriceList the scBidPriceList to set
	 */
	public void setScBidPriceList(List<ScBidPrice> scBidPriceList) {
		this.scBidPriceList = scBidPriceList;
	}



	/**
	 * @return the lastBidPricesList
	 */
	public List<ScBidPrice> getLastBidPricesList() {
		return lastBidPricesList;
	}



	/**
	 * @param lastBidPricesList the lastBidPricesList to set
	 */
	public void setLastBidPricesList(List<ScBidPrice> lastBidPricesList) {
		this.lastBidPricesList = lastBidPricesList;
	}



	/**
	 * @return the scBidItemsList
	 */
	public List<ScBidItem> getScBidItemsList() {
		return scBidItemsList;
	}



	/**
	 * @param scBidItemsList the scBidItemsList to set
	 */
	public void setScBidItemsList(List<ScBidItem> scBidItemsList) {
		this.scBidItemsList = scBidItemsList;
	}



	/**
	 * @return the statusDescs
	 */
	public List<PmStatusDesc> getStatusDescs() {
		return statusDescs;
	}



	/**
	 * @param statusDescs the statusDescs to set
	 */
	public void setStatusDescs(List<PmStatusDesc> statusDescs) {
		this.statusDescs = statusDescs;
	}



	/**
	 * @return the selectedStatusDescList
	 */
	public List<Long> getSelectedStatusDescList() {
		return selectedStatusDescList;
	}



	/**
	 * @param selectedStatusDescList the selectedStatusDescList to set
	 */
	public void setSelectedStatusDescList(List<Long> selectedStatusDescList) {
		this.selectedStatusDescList = selectedStatusDescList;
	}



	/**
	 * @return the searchBiddingNo
	 */
	public String getSearchBiddingNo() {
		return searchBiddingNo;
	}



	/**
	 * @param searchBiddingNo the searchBiddingNo to set
	 */
	public void setSearchBiddingNo(String searchBiddingNo) {
		this.searchBiddingNo = searchBiddingNo;
	}



	/**
	 * @return the searchDocumentNo
	 */
	public String getSearchDocumentNo() {
		return searchDocumentNo;
	}



	/**
	 * @param searchDocumentNo the searchDocumentNo to set
	 */
	public void setSearchDocumentNo(String searchDocumentNo) {
		this.searchDocumentNo = searchDocumentNo;
	}



	/**
	 * @return the searchBidTitle
	 */
	public String getSearchBidTitle() {
		return searchBidTitle;
	}



	/**
	 * @param searchBidTitle the searchBidTitle to set
	 */
	public void setSearchBidTitle(String searchBidTitle) {
		this.searchBidTitle = searchBidTitle;
	}



	/**
	 * @return the searchBiddingDateFrom
	 */
	public Date getSearchBiddingDateFrom() {
		return searchBiddingDateFrom;
	}



	/**
	 * @param searchBiddingDateFrom the searchBiddingDateFrom to set
	 */
	public void setSearchBiddingDateFrom(Date searchBiddingDateFrom) {
		this.searchBiddingDateFrom = searchBiddingDateFrom;
	}



	/**
	 * @return the searchBiddingDateTo
	 */
	public Date getSearchBiddingDateTo() {
		return searchBiddingDateTo;
	}



	/**
	 * @param searchBiddingDateTo the searchBiddingDateTo to set
	 */
	public void setSearchBiddingDateTo(Date searchBiddingDateTo) {
		this.searchBiddingDateTo = searchBiddingDateTo;
	}



	/**
	 * @return the bidPackages
	 */
	public List<ScBidPackage> getBidPackages() {
		return bidPackages;
	}



	/**
	 * @param bidPackages the bidPackages to set
	 */
	public void setBidPackages(List<ScBidPackage> bidPackages) {
		this.bidPackages = bidPackages;
	}



	/**
	 * @return the selectedZone
	 */
	public String getSelectedZone() {
		return selectedZone;
	}



	/**
	 * @param selectedZone the selectedZone to set
	 */
	public void setSelectedZone(String selectedZone) {
		this.selectedZone = selectedZone;
	}



	/**
	 * @return the createdDateFrom
	 */
	public Date getCreatedDateFrom() {
		return createdDateFrom;
	}



	/**
	 * @param createdDateFrom the createdDateFrom to set
	 */
	public void setCreatedDateFrom(Date createdDateFrom) {
		this.createdDateFrom = createdDateFrom;
	}



	/**
	 * @return the createdDateTo
	 */
	public Date getCreatedDateTo() {
		return createdDateTo;
	}



	/**
	 * @param createdDateTo the createdDateTo to set
	 */
	public void setCreatedDateTo(Date createdDateTo) {
		this.createdDateTo = createdDateTo;
	}


	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}



	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * @return the newStartDate
	 */
	public Date getNewStartDate() {
		return newStartDate;
	}



	/**
	 * @param newStartDate the newStartDate to set
	 */
	public void setNewStartDate(Date newStartDate) {
		this.newStartDate = newStartDate;
	}



	/**
	 * @return the newEndDate
	 */
	public Date getNewEndDate() {
		return newEndDate;
	}



	/**
	 * @param newEndDate the newEndDate to set
	 */
	public void setNewEndDate(Date newEndDate) {
		this.newEndDate = newEndDate;
	}



	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}



	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}



	/**
	 * @return the bidManagementViewBy
	 */
	public String getBidManagementViewBy() {
		return bidManagementViewBy;
	}



	/**
	 * @param bidManagementViewBy the bidManagementViewBy to set
	 */
	public void setBidManagementViewBy(String bidManagementViewBy) {
		this.bidManagementViewBy = bidManagementViewBy;
	}



	/**
	 * @return the searchDataModel
	 */
	public IPagingDataModel<ScBidSchedule> getSearchDataModel() {
		return searchDataModel;
	}



	/**
	 * @param searchDataModel the searchDataModel to set
	 */
	public void setSearchDataModel(IPagingDataModel<ScBidSchedule> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}



	/**
	 * @return the rendered
	 */
	public boolean isRendered() {
		return rendered;
	}



	/**
	 * @param rendered the rendered to set
	 */
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}



	/**
	 * @return the selectedtem
	 */
	public ScBidItem getSelectedtem() {
		return selectedtem;
	}

	/**
	 * @return the historyPrice
	 */
	public ScBidPrice getHistoryPrice() {
		return historyPrice;
	}


	/**
	 * @param historyPrice the historyPrice to set
	 */
	public void setHistoryPrice(ScBidPrice historyPrice) {
		this.historyPrice = historyPrice;
	}

	/**
	 * @param selectedtem the selectedtem to set
	 */
	public void setSelectedtem(ScBidItem selectedtem) {
		this.selectedtem = selectedtem;
	}

	
	/**
	 * @return the bidHistoryPriceList
	 */
	public List<ScBidPrice> getBidHistoryPriceList() {
		return bidHistoryPriceList;
	}

	/**
	 * @param bidHistoryPriceList the bidHistoryPriceList to set
	 */
	public void setBidHistoryPriceList(List<ScBidPrice> bidHistoryPriceList) {
		this.bidHistoryPriceList = bidHistoryPriceList;
	}

	/**
	 * @return the historyPriceLinearModel
	 */
	public CartesianChartModel getHistoryPriceLinearModel() {
		return historyPriceLinearModel;
	}




	/**
	 * @param historyPriceLinearModel the historyPriceLinearModel to set
	 */
	public void setHistoryPriceLinearModel(
			CartesianChartModel historyPriceLinearModel) {
		this.historyPriceLinearModel = historyPriceLinearModel;
	}




	/**
	 * @return the itemPriceLinearModel
	 */
	public CartesianChartModel getItemPriceLinearModel() {
		return itemPriceLinearModel;
	}




	/**
	 * @param itemPriceLinearModel the itemPriceLinearModel to set
	 */
	public void setItemPriceLinearModel(CartesianChartModel itemPriceLinearModel) {
		this.itemPriceLinearModel = itemPriceLinearModel;
	}


	/**
	 * @return the supplierCode
	 */
	public Long getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(Long supplierCode) {
		this.supplierCode = supplierCode;
	}

	
	
	/**
	 * @param bidPriceLinearModel the bidPriceLinearModel to set
	 */
	public void setBidPriceLinearModel(CartesianChartModel bidPriceLinearModel) {
		this.bidPriceLinearModel = bidPriceLinearModel;
	}


	/**
	 * @return the bidPriceLinearModel
	 */
	public CartesianChartModel getBidPriceLinearModel() {
		return bidPriceLinearModel;
	}

	/**
	 * @return the zoneDetailsList
	 */
	public List<TplSpecZoneDtl> getZoneDetailsList() {
		return zoneDetailsList;
	}

	/**
	 * @param zoneDetailsList the zoneDetailsList to set
	 */
	public void setZoneDetailsList(List<TplSpecZoneDtl> zoneDetailsList) {
		this.zoneDetailsList = zoneDetailsList;
	}

	
	private IPagingDataModel<ScBidSchedule> searchDataModel = new SearchPagingDataModel();

	private class SearchPagingDataModel extends PagingDataModel<ScBidSchedule> {

		private static final long serialVersionUID = 1L;

		public SearchPagingDataModel() {
			super("biddingList");

		}

		@Override
		protected ISearchResultBean<ScBidSchedule> pageChangeListener(
				ISearchBean<ScBidSchedule> bidSchSearchBean) {

			IBiddingSO biddingSO = null;

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				if (getSearchBiddingNo() != null
						&& StringUtils
								.isNotBlank((String) getSearchBiddingNo())) {
					
					SearchFilterBean bidNoFilterBean = new SearchFilterBean("bidNo",
							SearchOperator.OPERATOR_LIKE.value(),
							getSearchBiddingNo());
					bidNoFilterBean.setCaseInsensitive(true);
					
					bidSchSearchBean.getDataFilterBeans().add(bidNoFilterBean);
				}

				if (getSearchDocumentNo() != null
						&& StringUtils
								.isNotBlank((String) getSearchDocumentNo())) {
					
					SearchFilterBean docNOFilterBean = new SearchFilterBean("scBid.docNo",
							SearchOperator.OPERATOR_LIKE.value(),
							getSearchDocumentNo());
					docNOFilterBean.setCaseInsensitive(true);
					
					bidSchSearchBean.getDataFilterBeans().add(docNOFilterBean);
				}

				if (getSearchBidTitle() != null
						&& StringUtils.isNotBlank((String) getSearchBidTitle())) {
					
					SearchFilterBean docTitleFilterBean = new SearchFilterBean("scBid.docTitle",
							SearchOperator.OPERATOR_LIKE.value(),
							getSearchBidTitle());
					docTitleFilterBean.setCaseInsensitive(true);
					bidSchSearchBean.getDataFilterBeans().add(docTitleFilterBean);
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

				if (selectedStatusDescList != null
						&& selectedStatusDescList.size() > 0) {
					

					bidSchSearchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmStatus.statusId",
									SearchOperator.OPERATOR_IN.value(),
									getSelectedStatusDescList()));
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

	
}