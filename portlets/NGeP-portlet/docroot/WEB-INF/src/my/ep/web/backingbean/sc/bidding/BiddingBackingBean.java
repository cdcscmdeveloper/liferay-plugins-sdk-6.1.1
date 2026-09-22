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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidItem;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.component.datetime.DateTimeInput;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.sc.bidding.BiddingManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;


//25791 -start-
import my.ep.persistence.entity.sc.bidding.ScBidScheduleForQt;

//25791 -end-

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
	private DateTimeInput reScheduleDateTimeInputUIComponent;
	private static final DecimalFormat nf = new DecimalFormat((new CommonUtils())			
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

	/**
	 * Default Constructor
	 */
	public BiddingBackingBean() {
		getDefaultPtjAndMinistryValues();
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
			//8755 -start-
			SearchBean<PmStatusDesc> statusSearchBean = new SearchBean<>();
			// Filter By - statusId
			List<Long> statusIds = Arrays.asList(BidStatusConst.BID_HAS_BEEN_CANCELED);
		
			statusSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					SourcingConstants.QT_PMSTATUS_STATUS_ID,
					SearchOperator.OPERATOR_NOT_IN.value(),
					statusIds));
			statusDescs = getStatusDropdownValuesBySearchBean(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(),
					ParameterConst.DOCTYPE_BIDDING,statusSearchBean);
			//8755 -end-
			getManagedBean().setStatusDescs(statusDescs);
		}

	}

	/**
	 * Method to fetch duration selection drop down menu values
	 */

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

		List<SelectItem> items = null;
		FacesScopes face = new FacesScopes();
		try {

			items = new ArrayList<SelectItem>();
			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale())));
			long startHour = 1;
			long endHour = 2;
			if (getManagedBean().getBidMinDuration() != null) {
				startHour = getManagedBean().getBidMinDuration();
			}
			if (getManagedBean().getBidMaxDuration() != null) {
				endHour = getManagedBean().getBidMaxDuration();
			}
			for (long duration = startHour; duration <= endHour; duration++) {				
				items.add(new SelectItem(duration, duration+""));
			}
			/*
			 * getManagedBean().getBidMinDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hour",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 * getManagedBean().getBidMaxDuration()+" "+
			 * ResourceBundleUtils.getMessage( "lbl.txt.hours",
			 * ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale())
			 */
			getManagedBean().setSelectItems(items.toArray(new SelectItem[items.size()]));
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
		this.getManagedBean().setAutoUpdate(false);
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
		this.getManagedBean().setAutoUpdate(false);
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
				this.getManagedBean().setAutoUpdate(true);

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
	 * @param ScBidAction
	 *            bidAction Instance
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
				// #28777 & #29729 - Start
				this.getManagedBean().setBidPackagesDisqualified(resDto.getBidPackagesDisqualified());
				this.getManagedBean().setDisqualified(resDto.isDisqualified());
				// #28777 & #29729 - End
			}
			QuationTenderReqDTO qtreqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO qtresDTO = new QuationTenderResDTO();
			if (null != bid.getScBid()) {
				qtreqDTO.setQtTndrNo(bid.getScBid().getDocNo());
				qtresDTO = qTDetailSO.retriveQtByQtNumber(qtreqDTO);
			}
			if (null != qtresDTO.getQtDetail()) {
				this.getManagedBean().setScQt(qtresDTO.getQtDetail());
			}
		}

		finally {

			biddingSO = null;
			qTDetailSO = null;
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

		//LineChartSeries chartSeries = new LineChartSeries();

		//chartSeries.setLabel("Supplier Code");
    // Map<String,LineChartSeries> suppliersets=new LinkedHashMap<>();
  // 1. Create a List to hold all unique time ticks
    List<String> allTicks = new ArrayList<String>();
  // --- Supplier 3 ---
     LineChartSeries s3 = new LineChartSeries();
     s3.setLabel("3"); // This is the Supplier Code
     s3.set("10:01:00", 750000);
     s3.set("10:06:00", 650000); // Becomes Point (2, 3, 631876)
     s3.set("10:10:00", 700000);
   
    // allTicks.add("10:01:00");
    // allTicks.add("10:06:00");
     // --- Supplier 1 ---
     // Different times and different amounts
     LineChartSeries s1 = new LineChartSeries();
     s1.setLabel("1"); // Supplier Code
     s1.set(getCurrentTimeWithoutDate(new Date()), 0);
    // s1.set("09:45:00", 300000);
    // s1.set("09:55:00", 29000); 
    // s1.set("10:12:00", 400000); // Becomes Point (3, 1, 25478)
    // allTicks.add("09:55:00");
     //allTicks.add("09:45:00");
     //allTicks.add("10:12:00");
  // 2. Sort the times so they appear chronologically on the axis
     LineChartSeries s2 = new LineChartSeries();
     s2.setLabel("2"); // This is the Supplier Code
     s2.set("11:01:00", 850000);
     s2.set("11:50:00", 1000000);
     
     LineChartSeries s4 = new LineChartSeries();
     s4.setLabel("4"); // This is the Supplier Code
     s4.set("11:10:00", 500000);
     s4.set("11:15:00", 700000);
     s4.set("11:20:00", 600000);
     s4.set("11:22:00", 800000);
     
     LineChartSeries s5 = new LineChartSeries();
     s5.setLabel("5"); // This is the Supplier Code
     s5.set("11:13:00", 600000);
     s5.set("11:18:00", 750000);
     s5.set("11:25:00", 650000);
     s5.set("11:35:00", 850000);
     s5.set("11:45:00", 950000);
   
     Collections.sort(allTicks);
     getManagedBean().getBidPriceLinearModel().addSeries(s1);
     getManagedBean().getBidPriceLinearModel().addSeries(s2);
     getManagedBean().getBidPriceLinearModel().addSeries(s3);
     getManagedBean().getBidPriceLinearModel().addSeries(s4);
    getManagedBean().getBidPriceLinearModel().addSeries(s5);
     
 /*	if (chartSeries.getData() == null || chartSeries.getData().isEmpty()) {
		chartSeries.set(getCurrentTimeWithoutDate(new Date()), 0);
	}
	

	if (getManagedBean().getBidPriceLinearModel() != null) {
		getManagedBean().getBidPriceLinearModel().addSeries(chartSeries);
	}*/

    // model.addSeries(s3);
    // model.addSeries(s1);
	/*	for (BidPackageBean bidPackage : bidPackageList) {
			LineChartSeries suuplierSeries=	suppliersets.get(bidPackage.getScBidSupplier().getSupplierNo());
			 log.info("suuplierSeriesKey = " + suuplierSeries +
                     ", suuplierSeriesValue = " + suuplierSeries);
			if (!bidPackage.getScBidSupplier().getPackageStartPrice()
					.equals(bidPackage.getBidPriceTotal())) {
				if(null==suuplierSeries){
					suuplierSeries=new LineChartSeries();
					suuplierSeries.setLabel(bidPackage.getScBidSupplier().getSupplierNo().toString());
				}
				suuplierSeries.set(
						getCurrentTimeWithoutDate(bidPackage.getLastBidDate()),
						bidPackage.getBidPriceTotal());
				log.info("suuplierSeriesNumber= " + bidPackage.getScBidSupplier().getSupplierNo() +
	                     ", BidpriceTotal = " + bidPackage.getBidPriceTotal());
				//chartSeries.set(
						//getCurrentTimeWithoutDate(bidPackage.getLastBidDate()),
						//bidPackage.getBidPriceTotal());
				suppliersets.put(bidPackage.getScBidSupplier().getSupplierNo().toString(), suuplierSeries);
				
			}
		}

		if (chartSeries.getData() == null || chartSeries.getData().isEmpty()) {
			chartSeries.set(getCurrentTimeWithoutDate(new Date()), 0);
		}
		

		if (getManagedBean().getBidPriceLinearModel() != null) {
			getManagedBean().getBidPriceLinearModel().addSeries(chartSeries);
		}
		getManagedBean().getBidPriceLinearModel().addSeries(s3);
		getManagedBean().getBidPriceLinearModel().addSeries(s1);
		if(!suppliersets.isEmpty()){
			for (Map.Entry<String,LineChartSeries> i : suppliersets.entrySet()) {
	           log.info("Key = " + i.getKey() +
	                             ", Value = " + i.getValue());
	           getManagedBean().getBidPriceLinearModel().addSeries(i.getValue());
			}
		}
		// Explicitly define the ticks in order
	    // In a real app, collect these from your data list and sort them
	    xTicks = "['09:45:00', '09:55:00', '10:01:00', '10:06:00', '10:12:00']";*/
		// Grouping by Supplier ensures each supplier gets their own line
	    // and their own Supplier Number in the label
	   /* Map<String, LineChartSeries> seriesMap = new HashMap<String, LineChartSeries>();

	    for (BidPackageBean bidPackage : bidPackageList) {
	        String supplierNo = bidPackage.getScBidSupplier().getSupplierNo().toString();
	        
	        // 1. Create series for this supplier if it doesn't exist
	        if (!seriesMap.containsKey(supplierNo)) {
	            LineChartSeries s = new LineChartSeries();
	            // RULE: Store the Supplier Number directly in the Label
	            s.setLabel(supplierNo); 
	            seriesMap.put(supplierNo, s);
	        }

	        LineChartSeries currentSeries = seriesMap.get(supplierNo);
	        String datePrefix = "2026-01-13";
	        // 2. Existing price logic
	        if (!bidPackage.getScBidSupplier().getPackageStartPrice()
	                .equals(bidPackage.getBidPriceTotal())) {
	            String fullDate= getCurrentTimeWithoutDate(bidPackage.getLastBidDate());
	            log.info("===displayfulldate===="+fullDate);
	            if(supplierNo.equals("3")){
	            	  fullDate="10:00:00"	;
	            }
	            else if(supplierNo.equals("4")){
	            	  fullDate="12:00:00"	;
	            }
	            log.info("===displayfulldate===="+fullDate);
	            currentSeries.set(
	            		fullDate,
	                    bidPackage.getBidPriceTotal());
	        }
	    }

	    // 3. Add all created series to the model
	    for (LineChartSeries s : seriesMap.values()) {
	        if (s.getData() != null && !s.getData().isEmpty()) {
	            getManagedBean().getBidPriceLinearModel().addSeries(s);
	        }
	    }*/
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
				packageItem.getScBidSupplier().getSupplierNo());

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
				// #28777 & 29729 - Start
				this.getManagedBean().setBidPriceItemsDisqualified(resDTO.getBidPriceItemsDisqualified());
				this.getManagedBean().setDisqualified(resDTO.isDisqualified());
				// #28777 & 29729 - End

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
		if(isQTMaintenanceScreen()){ //#17376-144
		return this.getManagedBean().getPreviousPageUrl();	
		} //#17376-144
		return "/xhtml/sc/bidding/bidManagement/biddingmain.xhtml ? faces-redirect=true";
	}

	/**
	 * Method to get formatted total amount.
	 * 
	 * @param specQuestion
	 * @param bidPrice
	 * @param qty
	 * @return String (Formatted Total Amount)
	 */
	public String formattedTotalAmount(TplSpecQuestion specQuestion,
			BigDecimal bidPrice, BigDecimal qty) {
		return nf.format(TemplateSpecHelper.getInstance().calcTotalAmount(
				specQuestion, bidPrice, qty));
	}

	/**
	 * Method to get formatted item start price total amount.
	 * 
	 * @param itemBean
	 * @return String (Formatted Item Start Price Total Amount)
	 */
	public String itemizedStartPriceTotal(BidItemBean itemBean) {

		return formattedTotalAmount(this.getManagedBean().getSelectedBidItem()
				.getScRequestItem().getTplSpecQuestion(),
				itemBean.getStartingPrice(), this.getManagedBean()
						.getSelectedBidItem().getQty());
	}

	/**
	 * Method to get bidAction type radio button code values
	 * 
	 * @param parameterType
	 * @return SelectItem[] (Select Items)
	 */
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
			SearchFilterBean pmParameterFilterBean = new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(),
					BiddingConst.BID_ACTION_TYPE_RESUME);
			pmParameterFilterBean.setCaseInsensitive(true);
			searchBean.getDataFilterBeans().add(pmParameterFilterBean);
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

	/**
	 * Listener Method for poll component
	 */
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
	 *            the current status id
	 * @return the indicator qt status published.
	 */
	public boolean isQTStatusPublished(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId,
				QTStatusConst.QT_BRIEFING_SITE_VISIT);
	}

	/**
	 * Check if QT Status greater than or equals from status.
	 * 
	 * @param currentStatusId
	 *            the current status id
	 * @param fromStatusId
	 *            the from status id
	 * @return the indicator qt status greater than or equals from status.
	 */
	public boolean isQTStatusGreaterThanOrEquals(Long currentStatusId,
			Long fromStatusId) {
		List<Long> orderedStatuses = Arrays
				.asList(QTStatusConst.QT_ORDERED_STATUSES);

		return orderedStatuses.indexOf(currentStatusId) >= orderedStatuses
				.indexOf(fromStatusId);
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

	/**
	 * Listener method to reset custom date component memory value.
	 */
	public void resetDateTimeComponent() {
		this.getManagedBean().setAutoUpdate(true);
		if (this.getDateTimeInputUIComponent() != null) {
			this.getDateTimeInputUIComponent().resetDateTime();
		}
	}

	/**
	 * Action method to close popup.
	 */
	public void closePopUpAction() {
		this.getManagedBean().setAutoUpdate(true);
	}
	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		
		PmOrgProfile orgProfile = null;
		try {
			orgProfile = getOrgProfileByLoginUser();
		} catch (EpException e) {
			String msgCode = MessageConst.ERROR_770224;
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(msgCode, null,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
									+ " (" + msgCode + ")"));
		}
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		this.getManagedBean().setPtjOrgProfile(ptjProfile);
		this.getManagedBean().setPtjGroupProfile(ptjGroupProfile);
		this.getManagedBean().setPtjMinistryProfile(ministryProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& resDTO.getUserOrgList().size() > 0) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}

	// #-17376-144 start
	/**
	 * 25791-Based on QTDetails Rendering Bidding Details Screen.
	 * 
	 * @param bid
	 * @param previousPageUrl
	 * @return Bidding screen.
	 */
	public String bidManagementQuotationTenderAction(ScBidScheduleForQt bid, String previousPageUrl) {//25791 
		//25791 - remove bidding codes start -
		//getManagedBean().setScheduledBid(bid);
		//25791 - remove bidding codes end -

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
			QuationTenderReqDTO qtreqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO qtresDTO = new QuationTenderResDTO();
			if (null != bid.getScBid()) {
				qtreqDTO.setQtTndrNo(bid.getScBid().getDocNo());
				qtresDTO = qTDetailSO.retriveQtByQtNumber(qtreqDTO);
			}
			if (null != qtresDTO.getQtDetail()) {
				this.getManagedBean().setScQt(qtresDTO.getQtDetail());
			}
		}

		finally {

			biddingSO = null;
			qTDetailSO = null;
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
        this.getManagedBean().setPreviousPageUrl(previousPageUrl);
		getManagedBean().setRendered(false);	

		return QTConstants.QT_MAINTENACE_BIDDING_MANAGEMENT_VIEW; //25791 

	}
	
	// 25791 - start added comments-
	/**
	 * check screen is qt maintenance or audit or technical supports.
	 * 
	 * @return boolean based on screen type.
	 */
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())// ***17376#55

				// 25791 -start-
				|| QTConstants.PORTLET_NAME_QT_TECHSUPP
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
		// 25791 -end-

		;
	}

	// 25791 -end-
	// #17376-144 End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check if this supplier disqualified
	 * 
	 * @param qtId
	 * @param supplierId
	 * @return true or false
	 **/
	public Boolean checkDisqualifiedSupplierBranchDeleted(Long qtId, Long supplierId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.flagBiddingDisqualifiedSupplierBranchDeleted(qtId, supplierId);
	}
	// #28777 & #29729 - End
	
	// #28777 & #29729 - Start
	/**
	 * Method to check if any zones are disqualified
	 * 
	 * @param qtId
	 * @param supplierId
	 * @param specZoneId
	 * @return true or false
	 **/
	public Boolean checkZonesDisqualifiedSupplierBranchDeleted(Long qtId, Long supplierId, Long specZoneId) {
		ISourcingIntegrationSO sourcingIntegrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
		return sourcingIntegrationSO.flagZonesBiddingDisqualifiedSupplierBranchDeleted(qtId, supplierId, specZoneId);
	}
	// #28777 & #29729 - End
	
	
	/**
	 * Action method for bid reschedule or re-bid event
	 * 
	 * @param bid
	 */
	public void pendingReScheduleBidPopUpAction(ScBidSchedule bidSchedule) {
		//resetReScheduleDateTimeComponent();
		this.getManagedBean().setAutoUpdate(false);
		this.getManagedBean().setBidAction(null);
		IBiddingSO biddingSO = null;
		Long bidScheduleId = bidSchedule.getBidScheduleId();
		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();
			this.getManagedBean().setScheduledBid(bidSchedule);
			reqDto.setBidScheduleId(bidScheduleId);

			BiddingResDTO resDto = biddingSO
					.retrieveLastBidActionByScheduleId(reqDto);
			if (resDto.isSuccessful()) {
				this.getManagedBean().setAutoUpdate(false);
				ScBidAction lastBidAction = resDto.getScBidAction();
				this.getManagedBean().setLastBidAction(lastBidAction);
				this.getManagedBean().setBidAction(new ScBidAction());
				this.getManagedBean().getBidAction()
						.setScBidSchedule(bidSchedule);
				this.getManagedBean().getBidAction()
						.setActionType(BiddingConst.BID_ACTION_TYPE_RESCHEDULE);
				Calendar minDate = Calendar.getInstance();
				minDate.setTime(bidSchedule.getEndDate());
				getManagedBean().setMindate(minDate.getTime());
				if(bidSchedule.getEndDate().before(DateUtils.getCurrentDate())){
					//// minDate = Calendar.getInstance();
					//minDate.setTime(DateUtils.getCurrentDate());
					//minDate.add(Calendar.MINUTE, 15);
					getManagedBean().setMindate(getNextDate());	
				}
				if(null==lastBidAction){
					
					
					getManagedBean().setNewBidDuration(
							durationCalculator(bidSchedule.getStartDate(),
									bidSchedule.getEndDate()));
				}else{
				//Calendar minDate = Calendar.getInstance();
				//minDate.setTime(bidSchedule.getEndDate());
			//	minDate.add(Calendar.MINUTE, 15);
				//Calendar endDate = Calendar.getInstance();
				//endDate.setTime(bidAction.getNewStartDate());
				//getManagedBean().setMindate(minDate.getTime());
				getManagedBean().setNewBidDuration(
						durationCalculator(lastBidAction.getCreatedDate(),
								bidSchedule.getEndDate()));
				}

			}

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					"BiddingBackingBean", String.valueOf(bidScheduleId),
					"pendingReScheduleBidPopUpAction",
					getUserIdSessionFromLoginId(), e);

		}
	}
	
	/**
	 * Listener Method for date selection events for resume bid
	 */
	public void rescheduleNewStartDateListener() {
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
					endDate.setTime(bidAction
							.getNewStartDate());
					endDate.add(Calendar.HOUR, getManagedBean().getBidAction()
							.getRebidDuration().intValue());
					bidAction.setNewEndDate(endDate.getTime());
					getManagedBean().setBidAction(bidAction);
					getManagedBean().getBidAction().getScBidSchedule().setStartDate(getManagedBean().getBidAction()
							.getNewStartDate());
					getManagedBean().getBidAction().getScBidSchedule().setEndDate(getManagedBean().getBidAction()
							.getNewEndDate());
				} else {
					bidAction.setNewEndDate(null);
					getManagedBean().setBidAction(bidAction);
				}
			}
		}
	}
	
	/**
	 * Action method for re schedule or re-bid
	 */
	public void pendingReScheduleBidAction()  {

		//IBiddingSO biddingSO = null;
		//ScBidAction bidAction=this.getManagedBean().getBidAction();

		//try {
		IBiddingSO	biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
		   // Date newStartDate=	bidAction.getNewStartDate();
		    //Date newEndDate=	bidAction.getNewEndDate();
		   // String justification=	bidAction.getRemark();
		/*	if (null!=getManagedBean().getBidAction().getNewStartDate()) {
				getManagedBean().getBidAction().
						getScBidSchedule()
						.setStartDate(
								getManagedBean().getBidAction().getNewStartDate());
			}
			if (null!=getManagedBean().getBidAction().getNewEndDate()) {
				getManagedBean().getBidAction()
						.getScBidSchedule()
						.setEndDate(
								getManagedBean().getBidAction().getNewEndDate());
			}*/
			/*if (StringUtils.isBlank(getManagedBean().getBidAction().getRemark())
					|| StringUtils.isEmpty(getManagedBean().getBidAction().getRemark())) {
				String justificationLbl = ResourceBundleUtils.getMessage(
						SourcingConstants.justification,
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());

				throw new EpException(MessageConst.ERROR_775185,
						new String[] { justificationLbl });
			}*/
			
			validateBidScheduleDetails(getManagedBean().getBidAction());

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setScBidAction(this.getManagedBean().getBidAction());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			BiddingResDTO resDTO = biddingSO.updateBidAction(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setAutoUpdate(true);
				RequestContext.getCurrentInstance().execute(
					QTConstants.PENDING_ONLINE_BIDDING_POPUP	);
				
					resetReScheduleDateTimeComponent();
					getManagedBean().setBidAction(null);
					getManagedBean().getSearchDataModel().reloadData();
					this.getManagedBean().setAutoUpdate(true);
					//RequestContext.getCurrentInstance().update("@form");
					successMessage(MessageConst.INFO_700004, getMessageCommon(SourcingConstants.ONLINE_BID_SUCCESS_MESSAGE), "", Boolean.FALSE);
				
			}
		//}finally{
			//biddingSO=null;
		//}

		/*catch (Exception e) {
			Long bidScheduleId= bidAction
			.getScBidSchedule().getBidScheduleId();
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					"BiddingBackingBean", String.valueOf(bidScheduleId),
					"pendingReScheduleBidAction",
					getUserIdSessionFromLoginId(), e);

		}*/

	}
	
	//8755 -start-
	/**
	 * validate bidSchedule details during pending online bidding time.
	 * @param bidAction
	 */
	private void validateBidScheduleDetails(ScBidAction bidAction) {
		String remark = bidAction.getRemark();
		String justification = bidAction.getRemark();
		Long bidDuration = bidAction.getRebidDuration();
		Date biddingStartDate = bidAction.getNewStartDate();
		if (null == biddingStartDate) {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] {getMessageCommon(SourcingConstants.BIDDING_START_DATE)});
		}
		if (null == bidDuration) {

			throw new EpException(MessageConst.ERROR_760805);
		}
		if (StringUtils.isBlank(remark) || StringUtils.isEmpty(remark)) {

			throw new EpException(MessageConst.ERROR_760806);
		}
		if (StringUtils.isBlank(justification)
				|| StringUtils.isEmpty(justification)) {

			throw new EpException(MessageConst.ERROR_760807);
		}

	}
	//8755 -end-

	/**
	 * @return the reScheduleDateTimeInputUIComponent
	 */
	public DateTimeInput getReScheduleDateTimeInputUIComponent() {
		return reScheduleDateTimeInputUIComponent;
	}

	/**
	 * @param reScheduleDateTimeInputUIComponent the reScheduleDateTimeInputUIComponent to set
	 */
	public void setReScheduleDateTimeInputUIComponent(
			DateTimeInput reScheduleDateTimeInputUIComponent) {
		this.reScheduleDateTimeInputUIComponent = reScheduleDateTimeInputUIComponent;
	}
	
	/**
	 * Listener method to reset custom date component memory value.
	 */
	public void resetReScheduleDateTimeComponent() {
		this.getManagedBean().setAutoUpdate(true);
		if (this.getReScheduleDateTimeInputUIComponent() != null) {
			this.getReScheduleDateTimeInputUIComponent().resetDateTime();
		}
	}

	/**
	 * To get message common
	 * 
	 * @param name
	 * @return String instance
	 */
	public static String getMessageCommon(String name) {
		return ResourceBundleUtils.getMessage(name,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}

}
