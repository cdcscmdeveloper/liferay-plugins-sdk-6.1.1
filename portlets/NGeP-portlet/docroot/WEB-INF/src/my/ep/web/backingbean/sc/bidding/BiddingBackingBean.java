package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPackage;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.BiddingManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean(name = "biddingBB")
@ViewScoped
public class BiddingBackingBean extends BaseBackingBean<BiddingManagedBean> {

	private static Logger log = Logger.getLogger(BiddingBackingBean.class);

	public BiddingBackingBean() {

		System.out.println("biddingBB constructor");

		getStatusDescList();

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public void resetSearchFormAction() {

		getManagedBean().setSearchBiddingNo(null);
		getManagedBean().setSearchDocumentNo(null);
		getManagedBean().setSearchBidTitle(null);
		getManagedBean().setSearchBiddingDateFrom(null);
		getManagedBean().setSearchBiddingDateTo(null);
		getManagedBean().setSelectedStatusDescList(null);
		getManagedBean().setMindate(null);

	}

	public void startDateSelectListener(SelectEvent event) {

		getManagedBean()
				.setSearchBiddingDateToMinDate((Date) event.getObject());

	}

	public List<PmStatusDesc> getStatusDescList() {
		List<PmStatusDesc> statusDescs = getManagedBean().getStatusDescs();

		if (statusDescs == null || statusDescs.size() == 0) {
			IStatusSO statusSO = null;

			try {
				statusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);

				String languageCode = new FacesScopes().getUserLocale()
						.getLanguage();

				StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode(ModuleCodeConst.SUPPLIER_SELECTION
						.toUpperCase());
				reqDTO.setDocType(ParameterConst.DOCTYPE_BIDDING);
				reqDTO.setLanguageCode(languageCode);

				StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
				if (resDTO.isSuccessful()) {
					statusDescs = resDTO.getStatusDescList();

					getManagedBean().setStatusDescs(statusDescs);
				}
			} finally {
				statusSO = null;
			}
		}

		return statusDescs;
	}

	public void bidPausePopUpAction(ScBidSchedule bid) {

		getManagedBean().setBidAction(new ScBidAction());
		getManagedBean().setDuration("0:0");
		getManagedBean().getBidAction().setActionType(
				BiddingConst.BID_ACTION_TYPE_PAUSED);
		getManagedBean().getBidAction().setScBidSchedule(bid);
		getManagedBean().setDuration(
				durationCalculator(new Date(), bid.getEndDate()));

		// RequestContext.getCurrentInstance().execute("bidPauseOrCancel.show()");

	}

	public void bidCancelPopUpAction(ScBidSchedule bid) {

		getManagedBean().setBidAction(new ScBidAction());
		getManagedBean().setDuration("0:0");
		getManagedBean().getBidAction().setActionType(
				BiddingConst.BID_ACTION_TYPE_CANCELLED);
		getManagedBean().getBidAction().setScBidSchedule(bid);
		getManagedBean().setDuration(
				durationCalculator(new Date(), bid.getEndDate()));

	}

	public String bidPauseOrCancelAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setScBidAction(getManagedBean().getBidAction());

			BiddingResDTO resDto = biddingSO.updateBidAction(reqDto);
			if (resDto.isSuccessful()) {
				log.info("bidPauseOrCancelAction successful");
			}

		}

		finally {
			biddingSO = null;
			getManagedBean().setBidAction(null);
			getManagedBean().setDuration("0:0");

		}

		return null;

	}

	public void reScheduleOrReBidPopUpAction(ScBidSchedule bid) {

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setBidScheduleId(bid.getBidScheduleId());

			BiddingResDTO resDto = biddingSO
					.retrieveLastBidActionByScheduleId(reqDto);
			if (resDto.isSuccessful()) {
				getManagedBean().setBidAction(resDto.getScBidAction());
				getManagedBean().getBidAction().setRemark(null);
			}

		} finally {
			biddingSO = null;

		}
		if (getManagedBean().getBidAction() != null) {

			getManagedBean().getBidAction().setScBidSchedule(bid);

			getManagedBean().getBidAction().setActionType(
					BiddingConst.BID_ACTION_TYPE_RESUME);

			getManagedBean().setMindate(new Date());

			getManagedBean().setRemainingTime(
					remainingTimeCalculator(getManagedBean().getBidAction()
							.getCreatedDate(), bid.getEndDate()));

			getManagedBean().setDuration(
					durationCalculator(getManagedBean().getBidAction()
							.getCreatedDate(), bid.getEndDate()));
			getManagedBean().setNewBidDuration("");
		}

	}

	public void bidActionTypeChangeListener(AjaxBehaviorEvent e) {

		String type = (String) e.getComponent().getAttributes().get("value");

		getManagedBean().getBidAction().setActionType(type);

		getManagedBean().getBidAction().setNewStartDate(null);
		getManagedBean().getBidAction().setNewEndDate(null);
		getManagedBean().setNewBidDuration("");

		if (type.equalsIgnoreCase("B")) {
			Calendar c = Calendar.getInstance();
			c.setTime(DateUtils.getCurrentDateWithoutTime());
			c.add(Calendar.DATE, 1);
			getManagedBean().setMindate(c.getTime());
		}

	}

	public void resumeBidNewStartDateSelectListener(AjaxBehaviorEvent e) {

		Date startDate = (Date) e.getComponent().getAttributes().get("value");
		getManagedBean().getBidAction().setNewStartDate(startDate);

		if (getManagedBean().getBidAction().getActionType()
				.equalsIgnoreCase("R")) {

			final long ONE_MINUTE_IN_MILLIS = 60000;

			long curTimeInMs = getManagedBean().getBidAction()
					.getNewStartDate().getTime();

			Date newEndDate = new Date(curTimeInMs
					+ (getManagedBean().getRemainingTime())
					* ONE_MINUTE_IN_MILLIS);

			getManagedBean().getBidAction().setNewEndDate(newEndDate);
			// getManagedBean().setNewBidDuration(durationCalculator(startDate,
			// newEndDate));
			getManagedBean().setNewBidDuration(getManagedBean().getDuration());

		} else if (getManagedBean().getBidAction().getActionType()
				.equalsIgnoreCase("B")) {

			getManagedBean().getBidAction().setNewEndDate(null);
			getManagedBean().setMindate(startDate);
			Calendar c = Calendar.getInstance();
			c.setTime(startDate);
			c.add(Calendar.HOUR, 2);
			getManagedBean().setMaxdate(c.getTime());
		}
	}

	public void reBidNewEndDateSelectListener(AjaxBehaviorEvent e) {

		Date endDate = (Date) e.getComponent().getAttributes().get("value");
		getManagedBean().getBidAction().setNewEndDate(endDate);

		if (getManagedBean().getBidAction().getActionType()
				.equalsIgnoreCase("B")) {

			getManagedBean().setNewBidDuration(
					durationCalculator(getManagedBean().getBidAction()
							.getNewStartDate(), getManagedBean().getBidAction()
							.getNewEndDate()));
		}
	}

	public void reScheduleOrReBidAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setScBidAction(this.getManagedBean().getBidAction());
			BiddingResDTO resDTO = biddingSO.updateBidAction(reqDTO);
			if (resDTO.isSuccessful()) {

			}

		}

		finally {
			biddingSO = null;
			getManagedBean().setBidAction(null);
			getManagedBean().setRemainingTime(null);
			getManagedBean().setDuration(null);

		}

	}

	private String durationCalculator(Date pausedTime, Date endDate) {

		long durationInMills = endDate.getTime() - pausedTime.getTime();

		long diffSeconds = durationInMills / 1000 % 60;
		long diffMinutes = durationInMills / (60 * 1000) % 60;
		long diffHours = durationInMills / (60 * 60 * 1000) % 24;
		DecimalFormat durationFormat = new DecimalFormat("#00");
		String duration = durationFormat.format(diffHours) + ":"
				+ durationFormat.format(diffMinutes) + ":"
				+ durationFormat.format(diffSeconds);
		return duration;
	}

	private Long remainingTimeCalculator(Date pausedTime, Date endDate) {

		long durationInMills = endDate.getTime() - pausedTime.getTime();
		long time = TimeUnit.MINUTES.convert(durationInMills,
				TimeUnit.MILLISECONDS);
		return time;

	}

	public void bidManagementAction(ScBidSchedule bid) {

		getManagedBean().setScheduledBid(bid);

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setBidScheduleId(bid.getBidScheduleId());
			BiddingResDTO resDto = biddingSO.retrieveBidScheduleDtl(reqDto);

			if (resDto.isSuccessful()) {
				getManagedBean().setScheduledBid(resDto.getScBidSchedule());
				getManagedBean().setBidPackages(resDto.getBidPackages());
			}
		}

		finally {

			biddingSO = null;
		}

		getManagedBean().setRemainingTime(
				remainingTimeCalculator(new Date(), getManagedBean()
						.getScheduledBid().getEndDate()));

		getManagedBean().setDuration(
				durationCalculator(getManagedBean().getScheduledBid()
						.getStartDate(), getManagedBean().getScheduledBid()
						.getEndDate()));
		if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(getManagedBean()
				.getScheduledBid().getScBid().getRecommendedTypeId()
				.getParameterCode())) {
			getManagedBean().setBidManagementViewBy(
					ParameterConst.RECOMMEND_TYPE_PACKAGE);
			getManagedBean().setBidManagementInitialViewBy(
					ParameterConst.RECOMMEND_TYPE_PACKAGE);
			createViewByPackageLineChart();

		} else if (ParameterConst.RECOMMEND_TYPE_ITEM.equals(getManagedBean()
				.getScheduledBid().getScBid().getRecommendedTypeId()
				.getParameterCode())) {
			getManagedBean().setBidManagementViewBy(
					ParameterConst.RECOMMEND_TYPE_ITEM);
			getManagedBean().setBidManagementInitialViewBy(
					ParameterConst.RECOMMEND_TYPE_ITEM);

			ScBidItem bidItem = getManagedBean().getScheduledBid()
					.getScBidItems().get(0);

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				BiddingReqDTO reqDTO = new BiddingReqDTO();
				reqDTO.setBidItemId(bidItem.getBidItemId());
				BiddingResDTO resDTO = biddingSO
						.retrieveLastBidPricesByItem(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setScBidPriceList(resDTO.getBidPrices());
				}
			}

			finally {
				biddingSO = null;
			}

			createViewByItemLineChart();
		}

		

		getManagedBean().setRendered(false);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "bidManagementMain.xhtml");

		// return "bidManagementMain";

	}

	private void createViewByPackageLineChart() {
		
		List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();
		for (ScBidPackage scBidPackage : getManagedBean().getBidPackages()) {

			if (!scBidPackage.getScBidSupplier().getSmSupplier().isBumi()) {

				nonBumiPriceList.add(scBidPackage.getBidPrice());
			}
		}
		if (nonBumiPriceList != null && nonBumiPriceList.size() != 0) {
			Collections.sort(nonBumiPriceList);
			if (nonBumiPriceList.get(0) != null
					&& nonBumiPriceList.get(0).doubleValue() != 0.00d) {
				getManagedBean().setLowestNonBumiOfferedPrice(
						nonBumiPriceList.get(0));
			} else {
				getManagedBean().setLowestNonBumiOfferedPrice(
						BigDecimal.valueOf(1l));
			}
		} else {
			getManagedBean().setLowestNonBumiOfferedPrice(
					BigDecimal.valueOf(1l));
		}
		
		
		List<ScBidPackage> listForCustomSort = new ArrayList<ScBidPackage>();
		for (ScBidPackage scBidPackage : getManagedBean().getBidPackages()) {
			listForCustomSort.add(scBidPackage);
		}
		// to sort list for line Graph
		Collections.sort(listForCustomSort, new Comparator<ScBidPackage>() {
			public int compare(ScBidPackage bidPackage1,
					ScBidPackage bidPackage2) {

				if (bidPackage1.getCreatedDate().after(
						bidPackage2.getCreatedDate())) {
					return 1;
				} else if (bidPackage1.getCreatedDate().before(
						bidPackage2.getCreatedDate())) {
					return -1;
				} else if (bidPackage1.getCreatedDate().equals(
						bidPackage2.getCreatedDate())) {
					return 0;
				}
				return 0;

			}
		});
		createBidPackagePriceLinearModel(listForCustomSort);

		listForCustomSort = null;
	}

	private void createBidPackagePriceLinearModel(
			List<ScBidPackage> bidPackageList) {

		getManagedBean().setBidPriceLinearModel(new CartesianChartModel());

		LineChartSeries chartSeries = new LineChartSeries();

		chartSeries.setLabel("Bid Price");

		for (ScBidPackage bidPackage : bidPackageList) {

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPackage.getCreatedDate()),
					bidPackage.getBidPrice());
		}

		if (getManagedBean().getBidPriceLinearModel() != null) {
			getManagedBean().getBidPriceLinearModel().addSeries(chartSeries);
		}

	}

	private void createViewByItemLineChart() {
		if (getManagedBean().getScBidPriceList() != null) {

			List<ScBidPrice> listForCustomSort = new ArrayList<ScBidPrice>();
			for (ScBidPrice scBidPrice : getManagedBean().getScBidPriceList()) {
				listForCustomSort.add(scBidPrice);
			}
			Collections.sort(listForCustomSort, new Comparator<ScBidPrice>() {
				public int compare(ScBidPrice bidPrice1, ScBidPrice bidPrice2) {

					if (bidPrice1.getCreatedDate().after(
							bidPrice2.getCreatedDate())) {
						return 1;
					} else if (bidPrice1.getCreatedDate().before(
							bidPrice2.getCreatedDate())) {
						return -1;
					} else if (bidPrice1.getCreatedDate().equals(
							bidPrice2.getCreatedDate())) {
						return 0;
					}
					return 0;
				}
			});

			createBidItemPriceLinearModel(listForCustomSort);

			listForCustomSort = null;
		}
	}

	private void createBidItemPriceLinearModel(List<ScBidPrice> bidPriceList) {

		getManagedBean().setItemPriceLinearModel(new CartesianChartModel());

		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");

		for (ScBidPrice bidPrice : bidPriceList) {

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPrice.getCreatedDate()),
					bidPrice.getBidPrice());
		}

		if (getManagedBean().getItemPriceLinearModel() != null) {

			getManagedBean().getItemPriceLinearModel().addSeries(chartSeries);
		}
	}

	public void viewByChangeListener(AjaxBehaviorEvent e) {

		log.info("viewByChangeListener");

		String type = (String) e.getComponent().getAttributes().get("value");

		getManagedBean().setBidManagementViewBy(type);
		IBiddingSO biddingSO = null;

		if (type.equalsIgnoreCase(ParameterConst.RECOMMEND_TYPE_ITEM)) {

			ScBidItem bidItem = getManagedBean().getScheduledBid()
					.getScBidItems().get(0);

			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

				BiddingReqDTO reqDTO = new BiddingReqDTO();

				reqDTO.setBidItemId(bidItem.getBidItemId());

				BiddingResDTO resDTO = biddingSO
						.retrieveLastBidPricesByItem(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setScBidPriceList(resDTO.getBidPrices());
				}
			}

			finally {
				biddingSO = null;
			}
			createViewByItemLineChart();
		} else if (type.equalsIgnoreCase(ParameterConst.RECOMMEND_TYPE_PACKAGE)) {
			try {
				biddingSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
				BiddingReqDTO reqDTO = new BiddingReqDTO();
				reqDTO.setBidScheduleId(getManagedBean().getScheduledBid().getBidScheduleId());
				BiddingResDTO resDTO = biddingSO.retrieveBidScheduleDtl(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setScheduledBid(resDTO.getScBidSchedule());
					getManagedBean().setBidPackages(resDTO.getBidPackages());
				}
			}finally{
				biddingSO = null;
			}
			createViewByPackageLineChart();
		}

	}

	public void supplierItemPricePopupAction(ScBidPackage packageItem) {

		getManagedBean().setSupplierCode(
				packageItem.getScBidSupplier().getSmSupplier().getSupplierId());

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setSupplierId(packageItem.getScBidSupplier().getSmSupplier()
					.getSupplierId());
			reqDTO.setBidScheduleId(getManagedBean().getScheduledBid()
					.getBidScheduleId());

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidPriceList(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidPriceList(resDTO.getBidPrices());
			}
		} finally {
			biddingSO = null;
		}
	}

	public void rowSelectListener(SelectEvent event) {
		log.info("rowSelectListener");

		ScBidItem bidItem = (ScBidItem) event.getObject();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setBidItemId(bidItem.getBidItemId());

			BiddingResDTO resDTO = biddingSO
					.retrieveLastBidPricesByItem(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidPriceList(resDTO.getBidPrices());
			}
		}

		finally {
			biddingSO = null;
		}

		createViewByItemLineChart();

		getManagedBean().setRendered(false);

	}

	public void historyPricePopupAction(ScBidPrice bidPrice) {

		getManagedBean().setHistoryPrice(bidPrice);

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setSupplierId(bidPrice.getScBidSupplierItem()
					.getScBidSupplier().getSmSupplier().getSupplierId());
			reqDto.setBidItemId(bidPrice.getScBidSupplierItem().getScBidItem()
					.getBidItemId());

			BiddingResDTO resDto = biddingSO
					.retrieveHistoryPricesByItemNSupplier(reqDto);

			if (resDto.isSuccessful()) {
				getManagedBean().setBidHistoryPriceList(resDto.getBidPrices());
			}
		}

		finally {

			biddingSO = null;
		}
		List<ScBidPrice> customSortBidPrices = new ArrayList<>();
		for (ScBidPrice scBidPrice : getManagedBean().getBidHistoryPriceList()) {
			customSortBidPrices.add(scBidPrice);
		}
		Collections.reverse(customSortBidPrices);
		createHistoryPriceLinearModel(customSortBidPrices);
		getManagedBean().setRendered(true);

	}

	private void createHistoryPriceLinearModel(List<ScBidPrice> bidPriceList) {

		getManagedBean().setHistoryPriceLinearModel(new CartesianChartModel());
		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");

		for (ScBidPrice bidPrice : bidPriceList) {

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPrice.getCreatedDate()),
					bidPrice.getBidPrice());
		}

		if (getManagedBean().getItemPriceLinearModel() != null) {

			getManagedBean().getHistoryPriceLinearModel()
					.addSeries(chartSeries);
		}
	}

	private String getCurrentTimeWithoutDate(Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
		String time = localDateFormat.format(date);
		return time;
	}

	public String biddingMonitorBackAction() {
		debugLogger("supplierProposalBackAction");
		return "/xhtml/sc/bidding/biddingmain.xhtml ? faces-redirect=true";
	}

}
