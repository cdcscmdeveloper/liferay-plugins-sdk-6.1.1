package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.BidItemBean;
import my.ep.biz.bean.sc.BidPackageBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.component.datetime.DateTimeInput;
import my.ep.web.managedbean.sc.bidding.BiddingManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.SchedulerHelper;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 * Bidding Backing Bean
 * 
 * @author Venkat Chanumolu
 */

@ManagedBean(name = "biddingBB")
@RequestScoped
public class BiddingBackingBean extends BaseBackingBean<BiddingManagedBean> {

	private static Logger log = Logger.getLogger(BiddingBackingBean.class);
	private DateTimeInput dateTimeInputUIComponent;

	/**
	 * Default Constructor
	 */
	public BiddingBackingBean() {
		getStatusDescList();
		if (this.getManagedBean() != null) {
			if (this.getManagedBean().getSelectItems() == null) {
				populateDurationDropDownValues();
			}

		} else {
			populateDurationDropDownValues();
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	CommonUtils commonUtils = new CommonUtils();
	DecimalFormat nf = new DecimalFormat(
			commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

	/**
	 * method to reset search fields
	 */
	public void resetSearchFormAction() {

		getManagedBean().setSearchBiddingNo(null);
		getManagedBean().setSearchDocumentNo(null);
		getManagedBean().setSearchBidTitle(null);
		getManagedBean().setSearchBiddingDateFrom(null);
		getManagedBean().setSearchBiddingDateTo(null);
		getManagedBean().setSelectedStatusDesc(null);
		getManagedBean().setMindate(null);
		getManagedBean().getSearchDataModel().resetData();
	}

	/**
	 * Listener method for search for start date selection events
	 * 
	 * @param event
	 */
	public void startDateSelectListener(SelectEvent event) {

		getManagedBean()
				.setSearchBiddingDateToMinDate((Date) event.getObject());

	}

	/**
	 * method to fetch status drop down list values
	 * 
	 * @return
	 */
	public void getStatusDescList() {
		SelectItem[] statusDescs = getManagedBean().getStatusDescs();
		if (statusDescs == null || statusDescs.length == 0) {
			statusDescs = getStatusDropdownValues(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(),
					ParameterConst.DOCTYPE_BIDDING);
			getManagedBean().setStatusDescs(statusDescs);
		}

	}

	public void populateDurationDropDownValues() {
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingResDTO resDto = biddingSO.fetchBidDurationValuesFromRules();
			getManagedBean().setBidMinDuration(resDto.getMinDuration());
			getManagedBean().setBidMaxDuration(resDto.getMaxDuration());
		} finally {
			biddingSO = null;
		}

		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {

			items = new SelectItem[3];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			items[1] = new SelectItem(getManagedBean().getBidMinDuration(),
					"01:00");

			items[2] = new SelectItem(getManagedBean().getBidMaxDuration(),
					"02:00");
			/*
			 * getManagedBean().getBidMinDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hour",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 * getManagedBean().getBidMaxDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hours",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 */
			getManagedBean().setSelectItems(items);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}

	/**
	 * Action method for bid pause event
	 * 
	 * @param bid
	 */
	public void bidPausePopUpAction(ScBidSchedule bid) {
		this.getManagedBean().setAutoUpdate(Boolean.FALSE);
		getManagedBean().setDuration("0:0");
		getManagedBean().getBidAction().setActionType(
				BiddingConst.BID_ACTION_TYPE_PAUSED);
		getManagedBean().getBidAction().setScBidSchedule(bid);
		getManagedBean().setDuration(
				durationCalculator(new Date(), bid.getEndDate()));

		// RequestContext.getCurrentInstance().execute("bidPauseOrCancel.show()");

	}

	/**
	 * Action method for bid cancel event
	 * 
	 * @param bid
	 */
	public void bidCancelPopUpAction(ScBidSchedule bid) {
		this.getManagedBean().setAutoUpdate(Boolean.FALSE);
		getManagedBean().setDuration("0:0");
		getManagedBean().getBidAction().setActionType(
				BiddingConst.BID_ACTION_TYPE_CANCELLED);
		getManagedBean().getBidAction().setScBidSchedule(bid);
		getManagedBean().setDuration(
				durationCalculator(new Date(), bid.getEndDate()));

	}

	/**
	 * Action method for bid pause or cancel event
	 * 
	 * @return
	 */
	public String bidPauseOrCancelAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setScBidAction(getManagedBean().getBidAction());

			reqDto.setPmUser(new FacesScopes().getUserSessionBean());

			BiddingResDTO resDto = biddingSO.updateBidAction(reqDto);
			if (resDto.isSuccessful()) {
				RequestContext.getCurrentInstance().execute(
						"bidPauseOrCancel.hide();");
				log.info("bidPauseOrCancelAction successful");
				this.getManagedBean().getSearchDataModel().reloadData();

			}

		}

		finally {
			biddingSO = null;
			getManagedBean().setBidAction(null);
			getManagedBean().setDuration("0:0");

		}

		return null;

	}

	/**
	 * Action method for bid reschedule or re-bid event
	 * 
	 * @param bid
	 */
	public void reScheduleOrReBidPopUpAction(ScBidSchedule bid) {
		this.getManagedBean().setAutoUpdate(false);
		this.getManagedBean().setBidAction(null);
		IBiddingSO biddingSO = null;
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();
			this.getManagedBean().setScheduledBid(bid);
			reqDto.setBidScheduleId(bid.getBidScheduleId());

			BiddingResDTO resDto = biddingSO
					.retrieveLastBidActionByScheduleId(reqDto);
			if (resDto.isSuccessful()) {
				this.getManagedBean().setAutoUpdate(false);
				System.out.println("auto update set to :"
						+ getManagedBean().isAutoUpdate());
				ScBidAction lastBidAction = resDto.getScBidAction();
				this.getManagedBean().setLastBidAction(lastBidAction);
				this.getManagedBean().setBidAction(new ScBidAction());
				this.getManagedBean().getBidAction().setScBidSchedule(bid);
				getManagedBean().setRemainingTime(
						remainingTimeCalculator(lastBidAction.getCreatedDate(),
								bid.getEndDate()));
				getManagedBean().setDuration(
						durationCalculator(lastBidAction.getCreatedDate(),
								bid.getEndDate()));
				if (lastBidAction != null
						&& lastBidAction.getActionType().equalsIgnoreCase(
								BiddingConst.BID_ACTION_TYPE_FAILED)) {
					this.getManagedBean()
							.getBidAction()
							.setActionType(
									BiddingConst.BID_ACTION_TYPE_RESCHEDULE);
					getManagedBean().setMindate(getNextDate());
					getManagedBean().setNewBidDuration(
							durationCalculator(bid.getStartDate(),
									bid.getEndDate()));
				} else {
					this.getManagedBean().getBidAction()
							.setActionType(BiddingConst.BID_ACTION_TYPE_RESUME);
					Calendar minDate = Calendar.getInstance();
					minDate.setTime(DateUtils.getCurrentDate());
					minDate.add(Calendar.MINUTE, 15);
					getManagedBean().setMindate(minDate.getTime());
					getManagedBean().setNewBidDuration(
							durationCalculator(lastBidAction.getCreatedDate(),
									bid.getEndDate()));
				}
			}

		} finally {
			biddingSO = null;

		}
	}

	/**
	 * Listener method for bid action type change
	 * 
	 * @param e
	 */
	public void bidActionTypeChangeListener(AjaxBehaviorEvent e) {

		String type = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().getBidAction().setActionType(type);
		getManagedBean().getBidAction().setNewStartDate(null);
		getManagedBean().getBidAction().setNewEndDate(null);
		ScBidAction lastBidAction = this.getManagedBean().getLastBidAction();
		ScBidSchedule bidSchedule = this.getManagedBean().getScheduledBid();
		if (lastBidAction != null
				&& lastBidAction.getActionType().equalsIgnoreCase(
						BiddingConst.BID_ACTION_TYPE_FAILED)) {
			if (bidSchedule != null
					&& (type.equalsIgnoreCase(BiddingConst.BID_ACTION_TYPE_RESUME) || type
							.equalsIgnoreCase(BiddingConst.BID_ACTION_TYPE_REBIDDING))) {
				String remainingTime = durationCalculator(
						bidSchedule.getStartDate(), bidSchedule.getEndDate());
				this.getManagedBean().setNewBidDuration(remainingTime);
				this.getManagedBean().setDuration(remainingTime);
				getManagedBean().setMindate(getMinDate());
			} else {
				getManagedBean().setMindate(getNextDate());
			}
		} else {
			if ((type.equalsIgnoreCase(BiddingConst.BID_ACTION_TYPE_RESUME) || type
					.equalsIgnoreCase(BiddingConst.BID_ACTION_TYPE_REBIDDING))
					&& lastBidAction != null) {
				getManagedBean().setMindate(getMinDate());
				String remainingTime = durationCalculator(
						lastBidAction.getCreatedDate(),
						bidSchedule.getEndDate());
				this.getManagedBean().setNewBidDuration(remainingTime);
				this.getManagedBean().setDuration(remainingTime);
			} else {
				getManagedBean().setMindate(getNextDate());
			}
		}
		this.getDateTimeInputUIComponent().resetDateTime();
	}

	private Date getMinDate() {
		Calendar minDate = Calendar.getInstance();
		minDate.setTime(DateUtils.getCurrentDate());
		minDate.add(Calendar.MINUTE, 15);
		return minDate.getTime();
	}

	private Date getNextDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtils.getCurrentDateWithoutTime());
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	/**
	 * Listener Method for date selection events for resume bid
	 * 
	 * @param e
	 */
	public void resumeBidNewStartDateSelectListener() {
		ScBidAction bidAction = this.getManagedBean().getBidAction();
		if (bidAction.getNewStartDate() != null) {
			if (bidAction.getActionType().equalsIgnoreCase(
					ParameterConst.BID_ACTION_TYPES_RESUME_BIDDING)) {

				final long ONE_MINUTE_IN_MILLIS = 60000;

				long curTimeInMs = getManagedBean().getBidAction()
						.getNewStartDate().getTime();

				Date newEndDate = new Date(curTimeInMs
						+ (getManagedBean().getRemainingTime())
						* ONE_MINUTE_IN_MILLIS);
				bidAction.setNewEndDate(newEndDate);
				getManagedBean().setBidAction(bidAction);
				getManagedBean().setNewBidDuration(
						getManagedBean().getDuration());

			} else {
				if (getManagedBean().getBidAction().getRebidDuration() != null) {
					Calendar endDate = Calendar.getInstance();
					endDate.setTime(getManagedBean().getBidAction()
							.getNewStartDate());
					endDate.add(Calendar.HOUR, getManagedBean().getBidAction()
							.getRebidDuration().intValue());
					bidAction.setNewEndDate(endDate.getTime());
					getManagedBean().setBidAction(bidAction);
				} else {
					bidAction.setNewEndDate(null);
					getManagedBean().setBidAction(bidAction);
				}
			}
		}
	}

	/**
	 * Listener Method for date selection events for re-bid
	 * 
	 * @param e
	 *            (event)
	 */
	public void reBidDurationSelectListener(ScBidAction bidAction) {
		if (getManagedBean().getBidAction().getActionType()
				.equalsIgnoreCase(ParameterConst.BID_ACTION_TYPES_REBIDDING)
				|| getManagedBean()
						.getBidAction()
						.getActionType()
						.equalsIgnoreCase(
								ParameterConst.BID_ACTION_TYPES_RESCHEDULE)) {
			if (bidAction.getNewStartDate() != null
					&& bidAction.getRebidDuration() != null) {
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(bidAction.getNewStartDate());
				endDate.add(Calendar.HOUR, getManagedBean().getBidAction()
						.getRebidDuration().intValue());
				bidAction.setNewEndDate(endDate.getTime());
			} else {
				bidAction.setNewEndDate(null);
			}
			getManagedBean().setBidAction(bidAction);
		}
	}

	/**
	 * Action method for re schedule or re-bid
	 */
	public void reScheduleOrReBidAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			if (getManagedBean().getBidAction().getNewStartDate() != null) {
				getManagedBean()
						.getBidAction()
						.getScBidSchedule()
						.setStartDate(
								getManagedBean().getBidAction()
										.getNewStartDate());
			}
			if (getManagedBean().getBidAction().getNewEndDate() != null) {
				getManagedBean()
						.getBidAction()
						.getScBidSchedule()
						.setEndDate(
								getManagedBean().getBidAction().getNewEndDate());
			}

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setScBidAction(this.getManagedBean().getBidAction());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			BiddingResDTO resDTO = biddingSO.updateBidAction(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setAutoUpdate(true);
				RequestContext.getCurrentInstance().execute(
						"bidRescheduleOrResume.hide();");
				log.info("resume Or Rebid Or Reschedule Action successful");
				if ((resDTO.getScBidAction().getActionType()
						.equals(BiddingConst.BID_ACTION_TYPE_RESUME) || resDTO
						.getScBidAction().getActionType()
						.equals(BiddingConst.BID_ACTION_TYPE_REBIDDING))
						&& resDTO.getScBidAction().getNewStartDate()
								.before(getNextDate())) {
					SchedulerHelper.getJobBidScheduleIdList().add(
							resDTO.getScBidAction().getScBidSchedule()
									.getBidScheduleId());
					getManagedBean().setRemainingTime(null);
					getManagedBean().setDuration(null);
					resetDateTimeComponent();
					getManagedBean().setBidAction(null);
					getManagedBean().getSearchDataModel().reloadData();
				}
			}
		}

		finally {
			biddingSO = null;
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

	/**
	 * Action method to navigate to bid management screen
	 * 
	 * @param bid
	 */
	public String bidManagementAction(ScBidSchedule bid) {
		renewSesionObject();

		getManagedBean().setScheduledBid(bid);

		IBiddingSO biddingSO = null;
		IQTDetailSO qTDetailSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			qTDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setBidScheduleId(bid.getBidScheduleId());
			BiddingResDTO resDto = biddingSO.retrieveBidScheduleDtl(reqDto);

			if (resDto.isSuccessful()) {
				getManagedBean().setScheduledBid(resDto.getScBidSchedule());
				getManagedBean().setBidPackages(resDto.getBidPackages());
			}
			QuationTenderReqDTO qtreqDTO=new QuationTenderReqDTO();
			QuationTenderResDTO qtresDTO=new QuationTenderResDTO();
			if(null!=bid.getScBid()){
			qtreqDTO.setQtTndrNo(bid.getScBid().getDocNo());
			qtresDTO=qTDetailSO.retriveQtByQtNumber(qtreqDTO);
		}
			if(null!=qtresDTO.getQtDetail()){
				this.getManagedBean().setScQt(qtresDTO.getQtDetail());
			}
		}
		

		finally {

			biddingSO = null;
			qTDetailSO=null;
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
			getManagedBean().setSelectedBidItem(
					getManagedBean().getScheduledBid().getScBidItems().get(0));
			rowSelectListener();
		}

		getManagedBean().setRendered(false);
		return "bidManagementMain.xhtml?faces-redirect=true";

	}

	private void createViewByPackageLineChart() {

		List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();
		for (BidPackageBean scBidPackage : getManagedBean().getBidPackages()) {

			if (!scBidPackage.getScBidSupplier().getSmSupplier().isBumi()) {

				nonBumiPriceList.add(scBidPackage.getBidPriceTotal());
			}
		}
		if (nonBumiPriceList != null && nonBumiPriceList.size() != 0) {
			getManagedBean().setNonBumiAvail(true);
			if (nonBumiPriceList.size() > 1) {
				Collections.sort(nonBumiPriceList);
			}
			if (nonBumiPriceList.get(0) != null) {
				getManagedBean().setLowestNonBumiOfferedPrice(
						nonBumiPriceList.get(0));
			}
		} else {
			getManagedBean().setNonBumiAvail(false);
			getManagedBean().setLowestNonBumiOfferedPrice(new BigDecimal(0d));
		}

		List<BidPackageBean> listForCustomSort = new ArrayList<BidPackageBean>();
		for (BidPackageBean scBidPackage : getManagedBean().getBidPackages()) {
			listForCustomSort.add(scBidPackage);
		}

		if (listForCustomSort.size() > 1) {
			// to sort list for line Graph
			Collections.sort(listForCustomSort,
					new Comparator<BidPackageBean>() {
						public int compare(BidPackageBean bidPackage1,
								BidPackageBean bidPackage2) {

							if (bidPackage1.getLastBidDate().after(
									bidPackage2.getLastBidDate())) {
								return 1;
							} else if (bidPackage1.getLastBidDate().before(
									bidPackage2.getLastBidDate())) {
								return -1;
							} else if (bidPackage1.getLastBidDate().equals(
									bidPackage2.getLastBidDate())) {
								return 0;
							}
							return 0;

						}
					});
		}

		createBidPackagePriceLinearModel(listForCustomSort);

		listForCustomSort = null;
	}

	private void createBidPackagePriceLinearModel(
			List<BidPackageBean> bidPackageList) {

		getManagedBean().setBidPriceLinearModel(new CartesianChartModel());

		LineChartSeries chartSeries = new LineChartSeries();

		chartSeries.setLabel(ResourceBundleUtils.getMessage("lbl.sc.bid.price",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));

		for (BidPackageBean bidPackage : bidPackageList) {

			if (!bidPackage.getScBidSupplier().getPackageStartPrice()
					.equals(bidPackage.getBidPriceTotal())) {
				chartSeries.set(
						getCurrentTimeWithoutDate(bidPackage.getLastBidDate()),
						bidPackage.getBidPriceTotal());
			}
		}

		if (chartSeries.getData() == null || chartSeries.getData().isEmpty()) {
			chartSeries.set(getCurrentTimeWithoutDate(new Date()), 0);
		}

		if (getManagedBean().getBidPriceLinearModel() != null) {
			getManagedBean().getBidPriceLinearModel().addSeries(chartSeries);
		}

	}

	private void createViewByItemLineChart() {
		if (getManagedBean().getBidPriceItems() != null) {

			List<BidItemBean> listForCustomSort = new ArrayList<BidItemBean>();
			for (BidItemBean bidItemBean : getManagedBean().getBidPriceItems()) {
				listForCustomSort.add(bidItemBean);
			}
			if (listForCustomSort.size() > 1) {
				Collections.sort(listForCustomSort,
						new Comparator<BidItemBean>() {
							public int compare(BidItemBean bidPrice1,
									BidItemBean bidPrice2) {

								if (bidPrice1.getLastBidDate() != null
										&& bidPrice2.getLastBidDate() != null
										&& bidPrice1.getLastBidDate().after(
												bidPrice2.getLastBidDate())) {
									return 1;
								} else if (bidPrice1.getLastBidDate() != null
										&& bidPrice2.getLastBidDate() != null
										&& bidPrice1.getLastBidDate().before(
												bidPrice2.getLastBidDate())) {
									return -1;
								} else if (bidPrice1.getLastBidDate() != null
										&& bidPrice2.getLastBidDate() != null
										&& bidPrice1.getLastBidDate().equals(
												bidPrice2.getLastBidDate())) {
									return 0;
								}
								return 0;
							}
						});
			}
			createBidItemPriceLinearModel(listForCustomSort);
			listForCustomSort = null;
		}
	}

	private void createBidItemPriceLinearModel(List<BidItemBean> bidPriceList) {

		getManagedBean().setItemPriceLinearModel(new CartesianChartModel());

		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");
		for (BidItemBean bidPrice : bidPriceList) {
			if (bidPrice.getBidPrice() != null
					&& bidPrice.getLastBidDate() != null
					&& !(bidPrice.getBidPrice().equals(bidPrice
							.getStartingPrice()))) {
				chartSeries.set(
						getCurrentTimeWithoutDate(bidPrice.getLastBidDate()),
						bidPrice.getBidPrice());
			}
		}

		if (chartSeries.getData() == null || chartSeries.getData().isEmpty()) {
			chartSeries.set(getCurrentTimeWithoutDate(new Date()), 0);
		}
		if (getManagedBean().getItemPriceLinearModel() != null) {

			getManagedBean().getItemPriceLinearModel().addSeries(chartSeries);
		}
	}

	/**
	 * Listener method for view type change
	 * 
	 * @param e
	 *            (event)
	 */
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
				reqDTO.setBidScheduleId(getManagedBean().getScheduledBid()
						.getBidScheduleId());
				BiddingResDTO resDTO = biddingSO.retrieveBidScheduleDtl(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setScheduledBid(resDTO.getScBidSchedule());
					getManagedBean().setBidPackages(resDTO.getBidPackages());
				}
			} finally {
				biddingSO = null;
			}
			createViewByPackageLineChart();
		}

	}

	/**
	 * action method to populate supplier item details
	 * 
	 * @param packageItem
	 */
	public void supplierItemPricePopupAction(BidPackageBean packageItem) {

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

	/**
	 * Listener method for row selection events
	 * 
	 * @param bidItem
	 */
	public void rowSelectListener() {
		log.info("rowSelectListener");

		// ScBidItem bidItem = (ScBidItem) event.getObject();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			ScBidItem selectedItem = getManagedBean().getSelectedBidItem();
			reqDTO.setBidItemId(selectedItem.getBidItemId());
			reqDTO.setScBidSchedule(getManagedBean().getScheduledBid());

			BiddingResDTO resDTO = biddingSO
					.retrieveLastBidPricesByItem(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setBidPriceItems(resDTO.getBidPriceItems());

				if (resDTO.getBidPriceItems() != null
						&& resDTO.getBidPriceItems().size() != 0) {
					List<BigDecimal> nonBumiPriceList = new ArrayList<BigDecimal>();
					for (BidItemBean bidItem : resDTO.getBidPriceItems()) {

						if (!bidItem.isBumi()) {

							nonBumiPriceList.add(bidItem.getBidPriceTotal());
						}
					}
					if (nonBumiPriceList != null
							&& nonBumiPriceList.size() != 0) {
						getManagedBean().setNonBumiAvail(true);
						if (nonBumiPriceList.size() > 1) {
							Collections.sort(nonBumiPriceList);
						}
						if (nonBumiPriceList.get(0) != null) {
							getManagedBean().setLowestNonBumiOfferedPrice(
									nonBumiPriceList.get(0));
						}
					} else {
						getManagedBean().setNonBumiAvail(false);
						getManagedBean().setLowestNonBumiOfferedPrice(
								new BigDecimal(0d));
					}
				}
			}
		}

		finally {
			biddingSO = null;
		}

		createViewByItemLineChart();

		getManagedBean().setRendered(false);

	}

	/**
	 * action method to populate history price details
	 * 
	 * @param bidPrice
	 */
	public void historyPricePopupAction(BidItemBean bidPrice) {

		// getManagedBean().setHistoryPrice(bidPrice);

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();

			reqDto.setSupplierId(bidPrice.getSupplierId());

			reqDto.setBidItemId(getManagedBean().getSelectedBidItem()
					.getBidItemId());

			BiddingResDTO resDto = biddingSO
					.retrieveHistoryPricesByItemNSupplier(reqDto);

			if (resDto.isSuccessful()) {
				getManagedBean().setBidHistoryPriceList(resDto.getBidPrices());
				getManagedBean().setHistoryPrice(resDto.getBidPrices().get(0));
			}
		}

		finally {

			biddingSO = null;
		}
		List<ScBidPrice> customSortBidPrices = new ArrayList<>();
		for (ScBidPrice scBidPrice : getManagedBean().getBidHistoryPriceList()) {
			customSortBidPrices.add(scBidPrice);
		}
		if (customSortBidPrices.size() > 1) {
			Collections.reverse(customSortBidPrices);
		}
		createHistoryPriceLinearModel(customSortBidPrices);
		getManagedBean().setRendered(true);

	}

	private void createHistoryPriceLinearModel(List<ScBidPrice> bidPriceList) {

		getManagedBean().setHistoryPriceLinearModel(new CartesianChartModel());
		LineChartSeries chartSeries = new LineChartSeries();
		chartSeries.setLabel("Bid Price");

		for (ScBidPrice bidPrice : bidPriceList) {
			if (bidPrice.getCreatedDate() != null) {
				chartSeries.set(
						getCurrentTimeWithoutDate(bidPrice.getCreatedDate()),
						bidPrice.getBidPrice());
			}
		}
		if (chartSeries.getData() == null || chartSeries.getData().isEmpty()) {
			chartSeries.set(getCurrentTimeWithoutDate(new Date()), 0);
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

	/**
	 * action method to navigate toprevious page
	 * 
	 * @return
	 */
	public String biddingMonitorBackAction() {
		debugLogger("supplierProposalBackAction");
		return "/xhtml/sc/bidding/bidManagement/biddingmain.xhtml ? faces-redirect=true";
	}

	public String formattedTotalAmount(TplSpecQuestion specQuestion,
			BigDecimal bidPrice, BigDecimal qty) {
		return nf.format(getManagedBean().getTplSpecHelper().calcTotalAmount(
				specQuestion, bidPrice, qty));
	}

	public String itemizedStartPriceTotal(BidItemBean itemBean) {

		return formattedTotalAmount(this.getManagedBean().getSelectedBidItem()
				.getScRequestItem().getTplSpecQuestion(),
				itemBean.getStartingPrice(), this.getManagedBean()
						.getSelectedBidItem().getQty());
	}

	public SelectItem[] getBidActionTypeRadioButtonCodeValues(
			String parameterType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		if (this.getManagedBean().getLastBidAction() != null
				&& this.getManagedBean().getLastBidAction().getActionType()
						.equalsIgnoreCase(BiddingConst.BID_ACTION_TYPE_FAILED)) {
			SearchFilterBean parameterFilterBean = new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(),
					BiddingConst.BID_ACTION_TYPE_FAILED);
			parameterFilterBean.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(parameterFilterBean);
		} else {
			SearchFilterBean pmParameterFilterBean = new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(),
					BiddingConst.BID_ACTION_TYPE_RESCHEDULE);
			pmParameterFilterBean.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(pmParameterFilterBean);
			SearchFilterBean parameterFilterBean = new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(),
					BiddingConst.BID_ACTION_TYPE_FAILED);
			parameterFilterBean.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(parameterFilterBean);
		}

		SelectItem[] items = super.getRadioButtonCodeValuesBySearchBean(
				parameterType, searchBean);
		return items;
	}

	public void pollListener() {
		bidManagementAction(this.getManagedBean().getScheduledBid());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"bidManagementMain.xhtml?faces-redirect=true");
	}

	
	/**
	 * Check if QT Status published.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @return	the indicator qt status published.
	 */
	public boolean isQTStatusPublished(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_BRIEFING_SITE_VISIT);
	}
	
	/**
	 * Check if QT Status greater than or equals from status.
	 * 
	 * @param currentStatusId
	 * 		the current status id
	 * @param fromStatusId
	 * 		the from status id
	 * @return	the indicator qt status greater than or equals from status.
	 */
	public boolean isQTStatusGreaterThanOrEquals(Long currentStatusId, Long fromStatusId) {
		List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
		
		return orderedStatuses.indexOf(currentStatusId) >= orderedStatuses.indexOf(fromStatusId);
	}
	
	/**
	 * @return the dateTimeInputUIComponent
	 */
	public DateTimeInput getDateTimeInputUIComponent() {
		return dateTimeInputUIComponent;
	}

	/**
	 * @param dateTimeInputUIComponent
	 *            the dateTimeInputUIComponent to set
	 */
	public void setDateTimeInputUIComponent(
			DateTimeInput dateTimeInputUIComponent) {
		this.dateTimeInputUIComponent = dateTimeInputUIComponent;
	}

	public void resetDateTimeComponent() {
		this.getManagedBean().setAutoUpdate(true);
		if (this.getDateTimeInputUIComponent() != null) {
			this.getDateTimeInputUIComponent().resetDateTime();
		}
	}

	public void closePopUpAction() {
		this.getManagedBean().setAutoUpdate(true);
	}
}
