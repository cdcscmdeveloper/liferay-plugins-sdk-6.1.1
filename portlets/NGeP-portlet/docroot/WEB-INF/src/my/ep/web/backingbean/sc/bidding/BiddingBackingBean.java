package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import my.ep.persistence.entity.sc.bidding.ScBid;
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

		getManagedBean().setMindate((Date) event.getObject());

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

				System.out
						.println("*********bidAction update sucessfull********");
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

		if (getManagedBean().getBidAction() == null) {

			// getManagedBean().setBidAction(new ScBidAction());

			System.out
					.println("*********bidAction object in reScheduleOrReBidPopUpAction() is null ********");
		}

		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setBidScheduleId(bid.getBidScheduleId());

			BiddingResDTO resDto = biddingSO
					.retrieveLastBidActionByScheduleId(reqDto);
			if (resDto.isSuccessful()) {
				System.out.println("last bid action fetch was successful");
				getManagedBean().setBidAction(resDto.getScBidAction());
				getManagedBean().getBidAction().setRemark(null);
				System.out.println("last bidAction was set");
			}

		} finally {
			biddingSO = null;

		}
		if (getManagedBean().getBidAction() != null) {

			getManagedBean().getBidAction().setScBidSchedule(bid);
			System.out.println("ScBidSchedule instance set to  bidAction");

			getManagedBean().getBidAction().setActionType(
					BiddingConst.BID_ACTION_TYPE_RESUME);
			System.out.println("default actiontype set to  bidAction");

			getManagedBean().setMindate(new Date());

			getManagedBean().setRemainingTime(
					remainingTimeCalculator(getManagedBean().getBidAction()
							.getCreatedDate(), bid.getEndDate()));

			getManagedBean().setDuration(
					durationCalculator(getManagedBean().getBidAction()
							.getCreatedDate(), bid.getEndDate()));
			System.out.println("********* value set to remainingTime"
					+ getManagedBean().getRemainingTime());
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

			final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs

			long curTimeInMs = getManagedBean().getBidAction()
					.getNewStartDate().getTime();

			Date newEndDate = new Date(curTimeInMs
					+ (getManagedBean().getRemainingTime())
					* ONE_MINUTE_IN_MILLIS);

			getManagedBean().getBidAction().setNewEndDate(newEndDate);
			Long remainingTime = getManagedBean().getRemainingTime();
			Long hours = remainingTime / 60;
			Long minutes = remainingTime % 60;
			String duration = hours + ":" + minutes;
			getManagedBean().setNewBidDuration(duration);

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

			BiddingReqDTO reqDto = new BiddingReqDTO();
			reqDto.setScBidAction(this.getManagedBean().getBidAction());
			BiddingResDTO resDto = biddingSO.updateBidAction(reqDto);

			if (resDto.isSuccessful()) {

				System.out
						.println("*********bidAction update sucessfull********");
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

		/*
		 * long time = TimeUnit.MINUTES.convert(durationInMills,
		 * TimeUnit.MILLISECONDS); Long hours = time / 60; Long minutes = time %
		 * 60;
		 */
		long diffSeconds = durationInMills / 1000 % 60;
		long diffMinutes = durationInMills / (60 * 1000) % 60;
		long diffHours = durationInMills / (60 * 60 * 1000) % 24;
		String duration = diffHours + ":" + diffMinutes + ":" + diffSeconds;
		System.out.println("**** Value from duration calc *******" + duration);
		return duration;
	}

	

	private Long remainingTimeCalculator(Date pausedTime, Date endDate) {

		long durationInMills = endDate.getTime() - pausedTime.getTime();
		long time = TimeUnit.MINUTES.convert(durationInMills,
				TimeUnit.MILLISECONDS);
		System.out.println("**** Value from remainingTimeCalculator *******"
				+ time);
		return time;

	}

	/**
	 * Get Bidding Schedule Details by id.
	 * 
	 * @param reqDTO
	 *            ( Long bidScheduleId - bidding schedule id (mandatory) )
	 * @return BiddingResDTO( ScBidSchedule scBidSchedule, List<ScBidItem>
	 *         scBidSchedule.scBidItems, List<ScBidPackage> bidPackages )
	 */

	public String bidManagementAction(ScBidSchedule bid) {

		System.out.println("****inside bidManagementAction()*****");

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setBidScheduleId(bid.getBidScheduleId());
			System.out.println("*******bidScheduleId set in reqDTO is***** "
					+ reqDto.getBidScheduleId());

			BiddingResDTO resDto = biddingSO.retrieveBidScheduleDtl(reqDto);

			if (resDto.isSuccessful()) {
				System.out.println("******resDTO payload fetch seccess******");

				getManagedBean().setScheduledBid(resDto.getScBidSchedule());
				System.out
						.println("**********retrieved bidScheduled id is************"
								+ resDto.getScBidSchedule().getBidScheduleId());

				getManagedBean().setBidPackages(resDto.getBidPackages());
				// getManagedBean().getScheduledBid().getScBidSuppliers()

				System.out
						.println("*********retrieved BidPackages List size is********"
								+ resDto.getBidPackages().size());
				System.out
						.println("*********retrieved ScBidSupplier List size is********"
								+ resDto.getScBidSchedule().getScBidSuppliers()
										.size());

				System.out
						.println("*********retrieved BidItems List size is**********"
								+ resDto.getScBidSchedule().getScBidItems()
										.size());
			} else {
				System.out
						.println("*********resDTO ScBidSchedule ScBidPackages ScBidItemsfetch failed********");
			}
		}

		finally {

			biddingSO = null;
		}

		getManagedBean().setRemainingTime(
				remainingTimeCalculator(new Date(), getManagedBean()
						.getScheduledBid().getEndDate()));

		System.out.println("****remaining time Set*****");

		getManagedBean().setDuration(
				durationCalculator(getManagedBean().getScheduledBid()
						.getStartDate(), getManagedBean().getScheduledBid()
						.getEndDate()));
		getManagedBean().setBidManagementViewBy("P");
		createBidPackagePriceLinearModel(getManagedBean().getBidPackages());

		List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();
		for (ScBidPackage scBidPackage : getManagedBean().getBidPackages()) {

			if (!scBidPackage.getScBidSupplier().getSmSupplier().isBumi()) {

				nonBumiPriceList.add(scBidPackage.getBidPrice());
			}
		}
		if (nonBumiPriceList != null && nonBumiPriceList.size() != 0) {
			Collections.sort(nonBumiPriceList);
			if (nonBumiPriceList.get(0).intValue() != 0) {
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

		getManagedBean().setRendered(false);

		return "bidManagementMain";

	}

	private void createBidPackagePriceLinearModel(
			List<ScBidPackage> bidPackageList) {

		System.out.println("****inside createBidPackagePriceLinearModel*****");

		getManagedBean().setBidPriceLinearModel(new CartesianChartModel());

		System.out
				.println("****BidPriceLinearModel instantiated with CartesianChartModel instance *****");

		LineChartSeries chartSeries = new LineChartSeries();

		chartSeries.setLabel("Bid Price");

		for (ScBidPackage bidPackage : bidPackageList) {

			System.out.println("line chart date" + bidPackage.getCreatedDate()
					+ "line chart price" + bidPackage.getBidPrice());

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPackage.getCreatedDate()),
					bidPackage.getBidPrice());
		}

		if (getManagedBean().getBidPriceLinearModel() != null) {

			getManagedBean().getBidPriceLinearModel().addSeries(chartSeries);
			System.out
					.println("****chartSeries set to BidPriceLinearModel*****");
		}

	}

	public void viewByChangeListener(AjaxBehaviorEvent e) {

		System.out.println("*********viewByChangeListener called***********");

		String type = (String) e.getComponent().getAttributes().get("value");

		getManagedBean().setBidManagementViewBy(type);

		if (type.equalsIgnoreCase("I")) {

			ScBidItem bidItem = getManagedBean().getScheduledBid()
					.getScBidItems().get(0);

			if (bidItem == null) {

				System.out.println("bidItem object is null");
			}

			System.out.println("getting initial values for viewByItem Page");

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
				} else {
					System.out.println("resDTO ScBidPriceList fetch failed");
				}

			}

			finally {
				biddingSO = null;
			}

			createBidItemPriceLinearModel(getManagedBean().getScBidPriceList());

			/*
			 * if (getManagedBean().getHistoryPriceLinearModel() == null) {
			 * 
			 * getManagedBean().setHistoryPriceLinearModel( new
			 * CartesianChartModel()); LineChartSeries chartSeries = new
			 * LineChartSeries(); chartSeries.setLabel("Bid Price");
			 * getManagedBean().getHistoryPriceLinearModel().addSeries(
			 * chartSeries); System.out .println(
			 * "********historyPriceLinearModel is null and initialized*********"
			 * ); }
			 */

		}

	}

	public void supplierItemPricePopupAction(ScBidPackage packageItem) {

		System.out
				.println("***********inisde supplierItemPricePopupAction()**********");

		getManagedBean().setSupplierCode(
				packageItem.getScBidSupplier().getSmSupplier().getSupplierId());

		/*
		 * System.out .println(
		 * "********the itemId used to retrieve supplier Items is**********" +
		 * packageItem.getScBidSupplier() .getScBidSupplierItems().get(0)
		 * .getScBidItem().getBidItemId());
		 */

		System.out
				.println("********the itemId used to retrieve supplier Items is**********"
						+ packageItem.getScBidSupplier().getScBidSchedule()
								.getScBidItems().get(0).getBidItemId());

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setBidItemId(packageItem.getScBidSupplier()
					.getScBidSupplierItems().get(0).getScBidItem()
					.getBidItemId());

			System.out.println("********the itemId is set to reqDTO**********");

			BiddingResDTO resDTO = biddingSO
					.retrieveLastBidPricesByItem(reqDTO);

			System.out
					.println("********retrieveLastBidPricesByItem(reqDTO)called**********");

			if (resDTO.isSuccessful()) {

				getManagedBean().setScBidPriceList(resDTO.getBidPrices());

				System.out.println(" ScBidPriceList fetch success and size is"
						+ getManagedBean().getScBidPriceList().size());

			} else {
				System.out.println("resDTO ScBidPriceList fetch failed");
			}

		}

		finally {
			biddingSO = null;
		}

	}

	public void rowSelectListener(SelectEvent event) {

		ScBidItem bidItem = (ScBidItem) event.getObject();

		System.out.println("inside rowSelectListener method");

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
			} else {
				System.out.println("resDTO ScBidPriceList fetch failed");
			}

		}

		finally {
			biddingSO = null;
		}

		createBidItemPriceLinearModel(getManagedBean().getScBidPriceList());

		getManagedBean().setRendered(false);

	}

	private void createBidItemPriceLinearModel(List<ScBidPrice> bidPriceList) {

		System.out.println("****inside createBidItemPriceLinearModel*****");

		getManagedBean().setItemPriceLinearModel(new CartesianChartModel());

		System.out
				.println("****itemPriceLinearModel instantiated with CartesianChartModel instance *****");

		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");

		for (ScBidPrice bidPrice : bidPriceList) {

			System.out.println("line chart date" + bidPrice.getCreatedDate()
					+ "line chart price" + bidPrice.getBidPrice());

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPrice.getCreatedDate()),
					bidPrice.getBidPrice());
		}

		if (getManagedBean().getItemPriceLinearModel() != null) {

			System.out
					.println("****chartSeries set to itemPriceLinearModel*****");

			getManagedBean().getItemPriceLinearModel().addSeries(chartSeries);

		}
	}

	public void historyPricePopupAction(ScBidPrice bidPrice) {

		System.out.println("inside historyPricePopupAction() method ");

		getManagedBean().setHistoryPrice(bidPrice);

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			System.out.println("*****supplier ID is*******"
					+ bidPrice.getScBidSupplierItem().getScBidSupplier()
							.getSmSupplier().getSupplierId());

			System.out.println("*****BidItem ID is*******"
					+ bidPrice.getScBidSupplierItem().getScBidItem()
							.getBidItemId());

			reqDto.setSupplierId(bidPrice.getScBidSupplierItem()
					.getScBidSupplier().getSmSupplier().getSupplierId());
			reqDto.setBidItemId(bidPrice.getScBidSupplierItem().getScBidItem()
					.getBidItemId());

			BiddingResDTO resDto = biddingSO
					.retrieveHistoryPricesByItemNSupplier(reqDto);

			if (resDto.isSuccessful()) {
				getManagedBean().setBidHistoryPriceList(resDto.getBidPrices());
				System.out
						.println("resDTO ScBidPriceList fetch success and set to bidHistoryPriceList ");

			} else {
				System.out.println("resDTO ScBidPriceList fetch failed");
			}
		}

		finally {

			biddingSO = null;
		}

		createHistoryPriceLinearModel(getManagedBean().getBidHistoryPriceList());

		getManagedBean().setRendered(true);

	}

	private void createHistoryPriceLinearModel(List<ScBidPrice> bidPriceList) {

		System.out.println("****inside createBidItemPriceLinearModel*****");

		getManagedBean().setHistoryPriceLinearModel(new CartesianChartModel());

		System.out
				.println("****historyPriceLinearModel instantiated with CartesianChartModel instance *****");

		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");

		for (ScBidPrice bidPrice : bidPriceList) {

			System.out.println("line chart date" + bidPrice.getCreatedDate()
					+ "line chart price" + bidPrice.getBidPrice());

			chartSeries.set(
					getCurrentTimeWithoutDate(bidPrice.getCreatedDate()),
					bidPrice.getBidPrice());
		}

		if (getManagedBean().getItemPriceLinearModel() != null) {

			System.out
					.println("****chartSeries set to historyPriceLinearModel*****");

			getManagedBean().getHistoryPriceLinearModel()
					.addSeries(chartSeries);

		}
	}

	private String getCurrentTimeWithoutDate(Date date) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
		String time = localDateFormat.format(date);
		return time;
	}

}
