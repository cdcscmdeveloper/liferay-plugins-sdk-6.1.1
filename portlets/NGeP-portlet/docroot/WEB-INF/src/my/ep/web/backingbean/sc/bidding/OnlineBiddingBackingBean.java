package my.ep.web.backingbean.sc.bidding;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * Online Bidding Backing Bean
 * 
 * @author Venkat Chanumolu
 * 
 */
@ManagedBean(name = "onlineBiddingBB")
@ViewScoped
public class OnlineBiddingBackingBean extends BaseBackingBean implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private List<ScBidSchedule> activeBidSchedules;
	private ScBidSchedule scBidSchedule;
	private List<ScBidPrice> bidPrices;
	private List<ScBidContactPerson> bidContactPersons;
	private SmSupplier smSupplier;
	private ScBidAction scBidAction;
	private BigDecimal packageRanking;
	private ScBidPrice scBidPrice;
	private ScBidPrice newScBidPrice;
	private BigDecimal selectedDecrementAmt;
	private BigDecimal newBidPrice;
	private Long selectedBidScheduleId;
	private String recommendTypeItem = ParameterConst.RECOMMEND_TYPE_ITEM;
	private String recommendTypePackage = ParameterConst.RECOMMEND_TYPE_PACKAGE;
	private Long previousStatusId;

	private boolean isBidPaused = false;

	private BigDecimal itemStartPriceTotal;
	private BigDecimal itemLastBidPriceTotal;
	private BigDecimal itemNewBidPriceTotal;
	private Long interval;
	private Long remainingBidTime;

	private String remainingTime;
	private String remainingHours;
	private String remainingMinutes;
	private String remainingSeconds;
	private int index;

	private BigDecimal tolerancePriceLowerBound;
	private BigDecimal tolerancePriceUpperBound;
	private boolean toleranceCheck = Boolean.FALSE;
	private String previousPageUrl = null;

	private Long servicetype = Long.parseLong(ParameterConst.ITEM_TYPE_SERVICE);

	private TemplateSpecHelper tplSpecHelper = TemplateSpecHelper.getInstance();
	private ScQt scQt;

	private static Logger log = Logger
			.getLogger(OnlineBiddingBackingBean.class);

	DecimalFormat durationFormat = new DecimalFormat("#00");

	CommonUtils commonUtils = new CommonUtils();
	DecimalFormat nf = new DecimalFormat(
			commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

	/**
	 * Default Constructor
	 */
	public OnlineBiddingBackingBean() {

	}

	/**
	 * Fetch tolerance price.
	 */
	private void fetchTolerancePrice() {
		IRuleSO ruleSO = null;

		try {
			ruleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.BIDDING_TOLERANCE_PERCENTAGE);
			RuleResDTO ruleResDTO = ruleSO
					.getRuleValuesByTypeInCurrentDate(ruleReqDTO);

			if (ruleResDTO.getRuleValueList() != null
					&& ruleResDTO.getRuleValueList().size() != 0) {
				PmConfigRuleValue configRulevalue = ruleResDTO
						.getRuleValueList().get(0);
				BigDecimal tolerancePercentage = new BigDecimal(
						configRulevalue.getRuleValue());
				Date ruleExpiryDate = configRulevalue.getExpDate();
				if (!tolerancePercentage.equals(0)
						&& (ruleExpiryDate == null || !ruleExpiryDate
								.before(new Date()))) {
					this.toleranceCheck = true;
					BigDecimal tolerancePriceLowerBound = (new BigDecimal(100))
							.subtract(tolerancePercentage);
					this.setTolerancePriceLowerBound(tolerancePriceLowerBound);

					BigDecimal tolerancePriceUpperBound = (new BigDecimal(100))
							.add(tolerancePercentage);
					this.setTolerancePriceUpperBound(tolerancePriceUpperBound);
				}

			}

		} finally {
			ruleSO = null;
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	@PostConstruct
	private void init() {

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String bidScheduleIdStr = originalServletRequest
				.getParameter("bidScheduleId");
		String previousPageUrl = originalServletRequest
				.getParameter("previousPageUrl");

		Long bidScheduleId = null;
		if (StringUtils.isNotBlank(bidScheduleIdStr)) {
			bidScheduleId = Long.parseLong(bidScheduleIdStr);
		}
		this.previousPageUrl = previousPageUrl;

		PmUser supplierUser = (new FacesScopes().getUserSessionBean());
		retrieveOnlineBiddingDtls(bidScheduleId, supplierUser.getUserId());
		fetchTolerancePrice();
	}

	/**
	 * Back to previous page
	 * 
	 * @return the previous page
	 */
	public String backAction() {
		return this.previousPageUrl;
	}

	/**
	 * Get Online Bidding Dtls
	 * 
	 * @param bidScheduleId
	 *            the bid schedule id
	 * @param previousPageUrl
	 *            the previous page url.
	 * @param isReadonly
	 *            the readonly flag
	 * @return the supplier online bidding page
	 */
	private void retrieveOnlineBiddingDtls(Long bidScheduleId, Long userId) {

		log.info("retrieveOnlineBiddingDtls");

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setUserID(userId);
			reqDTO.setBidScheduleId(bidScheduleId);

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				ScBidSchedule bidSchedule = resDTO.getScBidSchedule();

				setActiveBidSchedules(resDTO.getActiveBidSchedules());
				setScBidSchedule(bidSchedule);
				if (null != bidSchedule) {
					retriveQtDetails(bidSchedule);
					this.previousStatusId = bidSchedule.getPmStatus()
							.getStatusId();
					setSelectedBidScheduleId(bidSchedule.getBidScheduleId());
					if (bidSchedule.getPmStatus().getStatusId()
							.equals(BidStatusConst.BID_PAUSED)) {
						this.setBidPaused(true);
					} else {
						this.setBidPaused(false);
					}
				}
				setBidContactPersons(resDTO.getBidContactPersons());
				setSmSupplier(resDTO.getSmSupplier());

				if (resDTO.getPackageRanking() != null) {
					setPackageRanking(resDTO.getPackageRanking());
				}

				if (resDTO.getScBidAction() != null) {
					setScBidAction(resDTO.getScBidAction());
					if (resDTO
							.getScBidAction()
							.getActionType()
							.equalsIgnoreCase(
									BiddingConst.BID_ACTION_TYPE_PAUSED)) {
						setRemainingTime(remainingTimeAfterPauseCalculator());
					}

				}

				if (resDTO.getBidPrices() != null) {
					setBidPrices(resDTO.getBidPrices());
				}
			}
		}

		finally {
			biddingSO = null;
		}
		itemStartPriceTotal();

	}

	/**
	 * Listener method for Bid selection events
	 * 
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void selectedBidChangeListener() throws PortalException,
			SystemException, IOException {
		log.info("selectedBidChangeListener");

		navigateUsingExternalContext(selectedBidScheduleId);
	}

	/**
	 * Navigate using external context.
	 * 
	 * @param bidScheduleId
	 *            the bid schedule id
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void navigateUsingExternalContext(Long bidScheduleId)
			throws PortalException, SystemException, IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay) new FacesScopes()
				.getRequestScope(WebKeys.THEME_DISPLAY);
		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup().getFriendlyURL())
						+ themeDisplay.getLayout().getGroup().getFriendlyURL()
								.length());

		System.out.println("commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		externalContext.redirect(commonUrl + "/"
				+ SourcingConstants.NAV_PAGE_ONLINE_BIDDING
				+ SourcingConstants.NAV_PARAM_BID_SCHEDULE_ID
				+ SourcingConstants.NAV_PARAM_SEPARATOR + bidScheduleId);
	}

	/**
	 * Listener method for poll events
	 * 
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void pollListener() throws PortalException, SystemException,
			IOException {
		log.info("pollListener - start");
		getOnlineBidScheduleDetails(getScBidSchedule().getBidScheduleId(),
				getSmSupplier().getSupplierId());
		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_PAUSED)) {
			RequestContext.getCurrentInstance().execute(
					"onlineBiddingNotification.show()");
		} else if (this.previousStatusId
				.equals(BidStatusConst.BID_PENDING_BIDDING)
				&& getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_IN_PROGRESS)) {
			this.previousStatusId = BidStatusConst.BID_IN_PROGRESS;
			navigateUsingExternalContext(getScBidSchedule().getBidScheduleId());
		} else if (this.previousStatusId.equals(BidStatusConst.BID_IN_PROGRESS)
				&& getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_COMPLETED)) {
			this.previousStatusId = BidStatusConst.BID_COMPLETED;
			navigateUsingExternalContext(getScBidSchedule().getBidScheduleId());
		}
		if (this.isBidPaused()
				&& getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_IN_PROGRESS)) {
			navigateUsingExternalContext(getScBidSchedule().getBidScheduleId());
		}

	}

	/**
	 * Gets the online bid schedule details.
	 * 
	 * @param bidScheduleID
	 *            the bid schedule ID
	 * @param supplierID
	 *            the supplier ID
	 * @return the online bid schedule details
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void getOnlineBidScheduleDetails(Long bidScheduleID, Long supplierID)
			throws PortalException, SystemException, IOException {
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidScheduleId(bidScheduleID);
			reqDTO.setSupplierId(supplierID);

			BiddingResDTO resDTO = biddingSO.retrieveCurrentBidDtl(reqDTO);

			if (resDTO.isSuccessful()) {

				setPackageRanking(resDTO.getPackageRanking());
				setScBidAction(resDTO.getScBidAction());
				//always set scBidAction object before this
				if (resDTO.getScBidAction() != null
						&& resDTO
								.getScBidAction()
								.getActionType()
								.equalsIgnoreCase(
										BiddingConst.BID_ACTION_TYPE_PAUSED)) {
					setRemainingTime(remainingTimeAfterPauseCalculator());
				}

				List<ScBidPrice> fetchedBidPrices = resDTO.getBidPrices();
				for (int i = 0; i < fetchedBidPrices.size(); i++) {
					for (ScBidPrice bidPrice : this.getBidPrices()) {
						if (bidPrice.getScBidSupplierItem()
								.getBidSupplierItemId() == fetchedBidPrices
								.get(i).getScBidSupplierItem()
								.getBidSupplierItemId()) {
							fetchedBidPrices.get(i).setNewBidPrice(
									bidPrice.getNewBidPrice());
							fetchedBidPrices.get(i).setNewPrice(
									bidPrice.getNewPrice());
							break;
						}

					}
				}
				this.setBidPrices(fetchedBidPrices);
				this.setScBidSchedule(resDTO.getScBidSchedule());

			}
		} finally {
			biddingSO = null;
		}
	}

	/**
	 * Action method to populate item details
	 * 
	 * @param scBidPrice
	 */
	public void itemDetialsPopupAction(ScBidPrice scBidPrice) {
		log.info("itemDetialsPopupAction");

		setScBidPrice(scBidPrice);

	}

	/**
	 * Listener method for spinner component change events
	 * 
	 * @param scBidPrice
	 */
	public void decrementAmountValueChangeListener(ScBidPrice scBidPrice) {

		log.info("decrementAmountValueChangeListener");

		if (scBidPrice.getNewPrice() != null) {
			if (-1 == scBidPrice.getNewPrice().compareTo(
					scBidPrice.getScBidSupplierItem().getScBidItem()
							.getDecrementAmt())) {
				int index = getBidPrices().indexOf(scBidPrice);
				scBidPrice.setNewPrice(new BigDecimal(0.0d));
				getBidPrices().set(index, scBidPrice);
				throw new EpException(MessageConst.ERROR_760301);

			} else if (1 == scBidPrice.getNewPrice().compareTo(
					scBidPrice.getScBidSupplierItem().getScBidItem()
							.getDecrementAmt())) {

				BigDecimal lastBidPrice = scBidPrice.getBidPrice() == null ? scBidPrice
						.getScBidSupplierItem().getItemStartPrice()
						: scBidPrice.getBidPrice();
				if (1 == scBidPrice.getNewPrice().compareTo(lastBidPrice)) {
					int index = getBidPrices().indexOf(scBidPrice);
					scBidPrice.setNewPrice(new BigDecimal(0.0d));
					getBidPrices().set(index, scBidPrice);
					throw new EpException(MessageConst.ERROR_760302);
				} else if (scBidPrice
						.getNewPrice()
						.remainder(
								scBidPrice.getScBidSupplierItem()
										.getScBidItem().getDecrementAmt())
						.compareTo(new BigDecimal(0.0d)) != 0) {
					int index = getBidPrices().indexOf(scBidPrice);
					scBidPrice.setNewPrice(new BigDecimal(0.0d));
					getBidPrices().set(index, scBidPrice);
					throw new EpException(MessageConst.ERROR_760301);
				}

			}
		}
		ScBidPrice newBidPrice = new ScBidPrice();
		BigDecimal newPrice = null;

		if (scBidPrice.getBidPrice() == null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = scBidPrice.getScBidSupplierItem().getItemStartPrice()
					.subtract(scBidPrice.getNewPrice());
		}

		else if (scBidPrice.getBidPrice() != null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = scBidPrice.getBidPrice().subtract(
					scBidPrice.getNewPrice());
		}
		newBidPrice.setBidPrice(newPrice);
		newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
		scBidPrice.setNewBidPrice(newBidPrice);
		int index = getBidPrices().indexOf(scBidPrice);
		getBidPrices().set(index, scBidPrice);
	}

	/**
	 * Listener method for spinner component change events
	 * 
	 * @param component
	 * @param newValue
	 */
	public void decrementAmountValueChangeListener(UIComponent component,
			Object newValue) {

		log.info("decrementAmountValueChangeListener");

		Object scBidPriceObj = component.getNamingContainer().getAttributes()
				.get("scBidPrice");

		if (scBidPriceObj != null) {
			ScBidPrice newBidPrice = new ScBidPrice();
			BigDecimal newPrice = null;
			ScBidPrice scBidPrice = (ScBidPrice) scBidPriceObj;

			if (newValue != null && newValue instanceof BigDecimal) {
				BigDecimal value = (BigDecimal) newValue;

				if (scBidPrice.getBidPrice() == null && value != null) {
					newPrice = scBidPrice.getScBidSupplierItem()
							.getItemStartPrice().subtract(value);
				}

				else if (scBidPrice.getBidPrice() != null && value != null) {
					newPrice = scBidPrice.getBidPrice().subtract(value);
				}
			}

			newBidPrice.setBidPrice(newPrice);
			newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
			scBidPrice.setNewBidPrice(newBidPrice);
		}
	}

	/**
	 * Gets the bid price totals.
	 * 
	 * @param scBidPrice
	 *            the sc bid price
	 * @param itemPrice
	 *            the item price
	 * @return the bid price totals
	 */
	private Double getBidPriceTotals(ScBidPrice scBidPrice, BigDecimal itemPrice) {

		return this.tplSpecHelper.calcTotalAmount(
				scBidPrice.getScBidSupplierItem().getScBidItem()
						.getScRequestItem().getTplSpecQuestion(), itemPrice,
				scBidPrice.getScBidSupplierItem().getScBidItem().getQty())
				.doubleValue();
	}

	/**
	 * Action method to confirm and submit bid price
	 * 
	 * @param scBidPrice
	 */
	public void confirmAndSubmitBidPrice(ScBidPrice scBidPrice) {

		System.out.println("########inside confirmAndSubmitBIdPrice######"
				+ scBidPrice);

		log.info("confirmAndSubmitBIdPrice");
		BigDecimal newPrice = null;
		Double lastBidpriceTotal = null;
		Double newBidpriceTotal = null;
		if (scBidPrice
				.getNewPrice()
				.remainder(
						scBidPrice.getScBidSupplierItem().getScBidItem()
								.getDecrementAmt())
				.compareTo(BigDecimal.valueOf(0)) != 0) {
			throw new EpException(MessageConst.ERROR_760301);
		}
		if (scBidPrice.getBidPrice() == null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = BigDecimal.valueOf(scBidPrice.getScBidSupplierItem()
					.getItemStartPrice().doubleValue()
					- scBidPrice.getNewPrice().doubleValue());
			lastBidpriceTotal = getBidPriceTotals(scBidPrice, scBidPrice
					.getScBidSupplierItem().getItemStartPrice());
			newBidpriceTotal = getBidPriceTotals(scBidPrice, newPrice);

		}

		else if (scBidPrice.getBidPrice() != null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
					.doubleValue() - scBidPrice.getNewPrice().doubleValue());
			lastBidpriceTotal = getBidPriceTotals(scBidPrice,
					scBidPrice.getBidPrice());
			newBidpriceTotal = getBidPriceTotals(scBidPrice, newPrice);

		}

		if (this.toleranceCheck
				&& this.getScBidSchedule().getScBid().getRecommendedTypeId()
						.getParameterCode().equals("P")) {
			BigDecimal dividendLB = this.getScBidSchedule()
					.getPackageEstimateCost()
					.multiply(this.tolerancePriceLowerBound);
			BigDecimal tolerancePriceLB = dividendLB
					.divide(new BigDecimal(100));
			if (getItemNewBidPriceTotal().doubleValue() < tolerancePriceLB
					.doubleValue()) {
				throw new EpException(MessageConst.ERROR_760369);
			}
			BigDecimal dividendUB = this.getScBidSchedule()
					.getPackageEstimateCost()
					.multiply(this.tolerancePriceUpperBound);
			BigDecimal tolerancePriceUB = dividendUB
					.divide(new BigDecimal(100));
			if (getItemNewBidPriceTotal().doubleValue() > tolerancePriceUB
					.doubleValue()) {
				throw new EpException(MessageConst.ERROR_760433);
			}
		}

		if (lastBidpriceTotal != null && newBidpriceTotal != null) {
			this.index = this.getBidPrices().indexOf(scBidPrice);
			Double diffPercentage = ((lastBidpriceTotal - newBidpriceTotal) / lastBidpriceTotal) * 100;
			if (diffPercentage > 10) {

				this.setScBidPrice(scBidPrice);
				RequestContext.getCurrentInstance().execute(
						"newBidPriceConfirm.show()");
			} else {
				this.setScBidPrice(scBidPrice);
				updateBidPriceAndCalculateRank();
			}
		}

	}

	/**
	 * Action method to update bid price and calculate rank.
	 */
	public void updateBidPriceAndCalculateRank() {
		log.info("updateBidPriceNDcalculateRank");
		ScBidPrice priceItemToPersist = null;
		priceItemToPersist = this.getScBidPrice().getNewBidPrice();
		priceItemToPersist.setBidTotalPrice(this.tplSpecHelper.calcTotalAmount(
				this.getScBidPrice().getScBidSupplierItem().getScBidItem()
						.getScRequestItem().getTplSpecQuestion(), this
						.getScBidPrice().getNewBidPrice().getBidPrice(), this
						.getScBidPrice().getScBidSupplierItem().getScBidItem()
						.getQty()));

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(priceItemToPersist);

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				ScBidPrice persistedItem = resDTO.getScBidPrice();
				persistedItem.setNewPrice(new BigDecimal(0d));
				getBidPrices().set(this.index, persistedItem);

				if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(this
						.getScBidSchedule().getScBid().getRecommendedTypeId()
						.getParameterCode())) {
					setPackageRanking(resDTO.getPackageRanking());
				}
				RequestContext.getCurrentInstance().execute("rc()");
			}

		} finally {
			biddingSO = null;
		}

	}

	/**
	 * Listener method to redirect if bid status changed to pause
	 * 
	 * @throws PortalException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void biddStatusNotificationAction() throws PortalException,
			SystemException, IOException {
		log.info("biddStatusNotificationAction");
		navigateUsingExternalContext(getScBidSchedule().getBidScheduleId());

	}

	/**
	 * Duration calculator.
	 * 
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the long
	 */
	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		long time = TimeUnit.SECONDS.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		return time;
	}

	/**
	 * Method to calculate Item start price total.
	 */
	private void itemStartPriceTotal() {

		BigDecimal startPriceTotal = new BigDecimal(0.0d);
		List<ScBidPrice> bidPriceList = getBidPrices();

		if (getBidPrices() != null && getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : bidPriceList) {
				BigDecimal temptotal = tplSpecHelper.calcTotalAmount(bidPrice
						.getScBidSupplierItem().getScBidItem()
						.getScRequestItem().getTplSpecQuestion(), bidPrice
						.getScBidSupplierItem().getItemStartPrice(), bidPrice
						.getScBidSupplierItem().getScBidItem().getQty());
				startPriceTotal = startPriceTotal.add(temptotal);
			}
		}
		setItemStartPriceTotal(startPriceTotal);
	}

	/**
	 * method to calculate item new price total
	 * 
	 * @return the itemNewBidPriceTotal
	 */
	public BigDecimal getItemNewBidPriceTotal() {
		BigDecimal newBidPriceTotal = new BigDecimal(0.0d);
		if (this.getBidPrices() != null && this.getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : this.getBidPrices()) {

				if (bidPrice.getNewBidPrice() != null
						&& bidPrice.getNewBidPrice().getBidPrice() != null) {
					BigDecimal temptotal = tplSpecHelper.calcTotalAmount(
							bidPrice.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPrice.getNewBidPrice().getBidPrice(), bidPrice
									.getScBidSupplierItem().getScBidItem()
									.getQty());

					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				} else if (bidPrice.getBidPrice() != null) {

					BigDecimal temptotal = tplSpecHelper.calcTotalAmount(
							bidPrice.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPrice.getBidPrice(), bidPrice
									.getScBidSupplierItem().getScBidItem()
									.getQty());
					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				} else {

					BigDecimal temptotal = tplSpecHelper
							.calcTotalAmount(bidPrice.getScBidSupplierItem()
									.getScBidItem().getScRequestItem()
									.getTplSpecQuestion(),
									bidPrice.getScBidSupplierItem()
											.getItemStartPrice(), bidPrice
											.getScBidSupplierItem()
											.getScBidItem().getQty());
					newBidPriceTotal = newBidPriceTotal.add(temptotal);

				}
			}
		}

		System.out.println("getItemNewBidPriceTotal" + newBidPriceTotal);
		this.setItemNewBidPriceTotal(newBidPriceTotal);

		return itemNewBidPriceTotal;
	}

	/**
	 * method to calculate remaining time for bid
	 * 
	 * @return the remainingBidTime
	 */
	public Long getRemainingBidTime() {

		if (scBidSchedule != null
				&& getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_IN_PROGRESS)) {

			remainingBidTime = durationCalculator(new Date(),
					scBidSchedule.getEndDate());
		}

		else if (scBidSchedule != null
				&& getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PENDING_BIDDING)) {

			remainingBidTime = durationCalculator(new Date(),
					scBidSchedule.getStartDate());

		} else {
			remainingBidTime = 0L;
		}

		return remainingBidTime;
	}

	/**
	 * method to calculate item last bid price total
	 * 
	 * @return the itemLastBidPriceTotal
	 */
	public BigDecimal getItemLastBidPriceTotal() {

		BigDecimal lastBidPriceTotal = new BigDecimal(0.0d);
		if (this.getBidPrices() != null && this.getBidPrices().size() != 0) {

			for (ScBidPrice bidPrice : this.getBidPrices()) {

				if (bidPrice.getBidPrice() == null) {
					BigDecimal tempTotal = tplSpecHelper
							.calcTotalAmount(bidPrice.getScBidSupplierItem()
									.getScBidItem().getScRequestItem()
									.getTplSpecQuestion(),
									bidPrice.getScBidSupplierItem()
											.getItemStartPrice(), bidPrice
											.getScBidSupplierItem()
											.getScBidItem().getQty());
					lastBidPriceTotal = lastBidPriceTotal.add(tempTotal);

				} else {
					BigDecimal tempTotal = tplSpecHelper.calcTotalAmount(
							bidPrice.getScBidSupplierItem().getScBidItem()
									.getScRequestItem().getTplSpecQuestion(),
							bidPrice.getBidPrice(), bidPrice
									.getScBidSupplierItem().getScBidItem()
									.getQty());
					lastBidPriceTotal = lastBidPriceTotal.add(tempTotal);

				}
			}
		}
		this.setItemLastBidPriceTotal(lastBidPriceTotal);
		return itemLastBidPriceTotal;
	}

	/**
	 * method to calculate refresh interval for poll component
	 * 
	 * @return the interval
	 */
	public Long getInterval() {

		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_PENDING_BIDDING)) {

			if (!(durationCalculator(new Date(), scBidSchedule.getStartDate()) < 0)) {

				interval = durationCalculator(new Date(),
						scBidSchedule.getStartDate());
			} else {
				interval = 10L;
			}

		} else if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_IN_PROGRESS)
				&& durationCalculator(new Date(), scBidSchedule.getStartDate()) < 20) {
			interval = 5L;
		} else {
			interval = 10L;
		}

		return interval;
	}

	/**
	 * method to return remaining seconds for static clock
	 * 
	 * @return the remainingSeconds
	 */
	public String getRemainingSeconds() {

		if (scBidSchedule != null && scBidAction != null) {

			long durationInMills = scBidSchedule.getEndDate().getTime()
					- scBidAction.getCreatedDate().getTime();
			long diffSeconds = durationInMills / 1000 % 60;
			remainingHours = durationFormat.format(diffSeconds);
		}

		return remainingSeconds;
	}

	/**
	 * method to return remaining minutes for static clock
	 * 
	 * @return the remainingMinutes
	 */
	public String getRemainingMinutes() {

		if (scBidSchedule != null && scBidAction != null) {

			long durationInMills = scBidSchedule.getEndDate().getTime()
					- scBidAction.getCreatedDate().getTime();
			long diffMinutes = durationInMills / (60 * 1000) % 60;
			remainingHours = durationFormat.format(diffMinutes);
		}

		return remainingMinutes;
	}

	/**
	 * method to return remaining hours for static clock
	 * 
	 * @return the remainingHours
	 */
	public String getRemainingHours() {

		if (scBidSchedule != null && scBidAction != null) {

			long durationInMills = scBidSchedule.getEndDate().getTime()
					- scBidAction.getCreatedDate().getTime();
			long diffHours = durationInMills / (60 * 60 * 1000) % 24;
			remainingHours = durationFormat.format(diffHours);
		}

		return remainingHours;
	}

	/**
	 * method to return item new price total
	 * 
	 * @return the itemNewPriceTotal
	 */
	public String itemNewPriceTotal(ScBidPrice bidPriceItem) {
		BigDecimal newPriceTotal = null;
		if (bidPriceItem.getNewBidPrice() != null
				&& bidPriceItem.getNewBidPrice().getBidPrice() != null) {
			newPriceTotal = this.tplSpecHelper.calcTotalAmount(bidPriceItem
					.getScBidSupplierItem().getScBidItem().getScRequestItem()
					.getTplSpecQuestion(), bidPriceItem.getNewBidPrice()
					.getBidPrice(), bidPriceItem.getScBidSupplierItem()
					.getScBidItem().getQty());
		}
		if (newPriceTotal != null
				&& (newPriceTotal.compareTo(new BigDecimal(0.0d)) == 1)) {
			return nf.format(newPriceTotal);
		} else {
			return null;

		}
	}

	/**
	 * method to return last bid price total
	 * 
	 * @return the lastBidPriceTotal
	 */
	public String lastBidPriceTotal(ScBidPrice bidPriceItem) {

		BigDecimal lastBidPriceTotal = null;
		if (bidPriceItem.getBidPrice() != null) {
			lastBidPriceTotal = this.tplSpecHelper
					.calcTotalAmount(bidPriceItem.getScBidSupplierItem()
							.getScBidItem().getScRequestItem()
							.getTplSpecQuestion(), bidPriceItem.getBidPrice(),
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getQty());
		}
		if (lastBidPriceTotal != null
				&& (lastBidPriceTotal.compareTo(new BigDecimal(0.0d)) == 1)) {
			return nf.format(lastBidPriceTotal);
		} else {
			return null;
		}
	}

	/**
	 * method to return item start price total
	 * 
	 * @return the startPriceTotal
	 */
	public String startPriceTotal(ScBidPrice bidPriceItem) {

		BigDecimal lastStartPriceTotal = null;
		if (bidPriceItem.getScBidSupplierItem().getItemStartPrice() != null) {
			lastStartPriceTotal = this.tplSpecHelper
					.calcTotalAmount(bidPriceItem.getScBidSupplierItem()
							.getScBidItem().getScRequestItem()
							.getTplSpecQuestion(), bidPriceItem
							.getScBidSupplierItem().getItemStartPrice(),
							bidPriceItem.getScBidSupplierItem().getScBidItem()
									.getQty());
		}
		if (lastStartPriceTotal != null
				&& (lastStartPriceTotal.compareTo(new BigDecimal(0.0d)) == 1)) {
			return nf.format(lastStartPriceTotal);
		} else {
			return null;
		}
	}

	/**
	 * @return the tplSpecHelper
	 */
	public TemplateSpecHelper getTplSpecHelper() {

		if (null == tplSpecHelper) {
			tplSpecHelper = new TemplateSpecHelper();
		}
		return tplSpecHelper;
	}

	/**
	 * Retrive qt details.
	 * 
	 * @param bid
	 *            the bid
	 */
	private void retriveQtDetails(ScBidSchedule bid) {
		IQTDetailSO qTDetailSO = null;
		try {

			qTDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO qtreqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO qtresDTO = new QuationTenderResDTO();
			if (null != bid && null != bid.getScBid()) {
				qtreqDTO.setQtTndrNo(bid.getScBid().getDocNo());
				qtresDTO = qTDetailSO.retriveQtByQtNumber(qtreqDTO);
			}
			if (null != qtresDTO.getQtDetail()) {
				setScQt(qtresDTO.getQtDetail());
			}
		} finally {
			qTDetailSO = null;
		}
	}

	/**
	 * Check whether Date is null or not
	 * 
	 * @param publishDate
	 * @param createdDate
	 * @return Date
	 */
	public Date retriveDatesforOrgProfile(Date publishDate, Date createdDate) {
		if (publishDate == null) {
			log.info("===========createdDate=====" + createdDate);
			return createdDate;
		}
		log.info("===========publishDate=====" + publishDate);
		return publishDate;

	}

	/**
	 * @return the newScBidPrice
	 */
	public ScBidPrice getNewScBidPrice() {
		return newScBidPrice;
	}

	/**
	 * @param newScBidPrice
	 *            the newScBidPrice to set
	 */
	public void setNewScBidPrice(ScBidPrice newScBidPrice) {
		this.newScBidPrice = newScBidPrice;
	}

	/**
	 * @param itemNewBidPriceTotal
	 *            the itemNewBidPriceTotal to set
	 */
	public void setItemNewBidPriceTotal(BigDecimal itemNewBidPriceTotal) {
		this.itemNewBidPriceTotal = itemNewBidPriceTotal;
	}

	/**
	 * @param remainingBidTime
	 *            the remainingBidTime to set
	 */
	public void setRemainingBidTime(Long remainingBidTime) {
		this.remainingBidTime = remainingBidTime;
	}

	/**
	 * @param itemLastBidPriceTotal
	 *            the itemLastBidPriceTotal to set
	 */
	public void setItemLastBidPriceTotal(BigDecimal itemLastBidPriceTotal) {
		this.itemLastBidPriceTotal = itemLastBidPriceTotal;
	}

	/**
	 * @return the itemStartPriceTotal
	 */
	public BigDecimal getItemStartPriceTotal() {
		return itemStartPriceTotal;
	}

	/**
	 * @param itemStartPriceTotal
	 *            the itemStartPriceTotal to set
	 */
	public void setItemStartPriceTotal(BigDecimal itemStartPriceTotal) {
		this.itemStartPriceTotal = itemStartPriceTotal;
	}

	/**
	 * @return the newBidPrice
	 */
	public BigDecimal getNewBidPrice() {
		return newBidPrice;
	}

	/**
	 * @param newBidPrice
	 *            the newBidPrice to set
	 */
	public void setNewBidPrice(BigDecimal newBidPrice) {
		this.newBidPrice = newBidPrice;
	}

	/**
	 * @return the selectedDecrementAmt
	 */
	public BigDecimal getSelectedDecrementAmt() {
		return selectedDecrementAmt;
	}

	/**
	 * @param selectedDecrementAmt
	 *            the selectedDecrementAmt to set
	 */
	public void setSelectedDecrementAmt(BigDecimal selectedDecrementAmt) {
		this.selectedDecrementAmt = selectedDecrementAmt;
	}

	/**
	 * @return the scBidPrice
	 */
	public ScBidPrice getScBidPrice() {
		return scBidPrice;
	}

	/**
	 * @param scBidPrice
	 *            the scBidPrice to set
	 */
	public void setScBidPrice(ScBidPrice scBidPrice) {
		this.scBidPrice = scBidPrice;
	}

	/**
	 * @return the activeBidSchedules
	 */
	public List<ScBidSchedule> getActiveBidSchedules() {
		return activeBidSchedules;
	}

	/**
	 * @param activeBidSchedules
	 *            the activeBidSchedules to set
	 */
	public void setActiveBidSchedules(List<ScBidSchedule> activeBidSchedules) {
		this.activeBidSchedules = activeBidSchedules;
	}

	/**
	 * @return the scBidSchedule
	 */
	public ScBidSchedule getScBidSchedule() {
		return scBidSchedule;
	}

	/**
	 * @param scBidSchedule
	 *            the scBidSchedule to set
	 */
	public void setScBidSchedule(ScBidSchedule scBidSchedule) {
		this.scBidSchedule = scBidSchedule;
	}

	/**
	 * @return the bidPrices
	 */
	public List<ScBidPrice> getBidPrices() {
		return bidPrices;
	}

	/**
	 * @param bidPrices
	 *            the bidPrices to set
	 */
	public void setBidPrices(List<ScBidPrice> bidPrices) {
		this.bidPrices = bidPrices;
	}

	/**
	 * @return the bidContactPersons
	 */
	public List<ScBidContactPerson> getBidContactPersons() {
		return bidContactPersons;
	}

	/**
	 * @param bidContactPersons
	 *            the bidContactPersons to set
	 */
	public void setBidContactPersons(List<ScBidContactPerson> bidContactPersons) {
		this.bidContactPersons = bidContactPersons;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(Long interval) {
		this.interval = interval;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the packageRanking
	 */
	public BigDecimal getPackageRanking() {
		return packageRanking;
	}

	/**
	 * @param packageRanking
	 *            the packageRanking to set
	 */
	public void setPackageRanking(BigDecimal packageRanking) {
		this.packageRanking = packageRanking;
	}

	/**
	 * @return the scBidAction
	 */
	public ScBidAction getScBidAction() {
		return scBidAction;
	}

	/**
	 * @param scBidAction
	 *            the scBidAction to set
	 */
	public void setScBidAction(ScBidAction scBidAction) {
		this.scBidAction = scBidAction;
	}

	/**
	 * @return the selectedBidScheduleId
	 */
	public Long getSelectedBidScheduleId() {
		return selectedBidScheduleId;
	}

	/**
	 * @param selectedBidScheduleId
	 *            the selectedBidScheduleId to set
	 */
	public void setSelectedBidScheduleId(Long selectedBidScheduleId) {
		this.selectedBidScheduleId = selectedBidScheduleId;
	}

	/**
	 * @return the recommendTypeItem
	 */
	public String getRecommendTypeItem() {
		return recommendTypeItem;
	}

	/**
	 * @param recommendTypeItem
	 *            the recommendTypeItem to set
	 */
	public void setRecommendTypeItem(String recommendTypeItem) {
		this.recommendTypeItem = recommendTypeItem;
	}

	/**
	 * @return the recommendTypePackage
	 */
	public String getRecommendTypePackage() {
		return recommendTypePackage;
	}

	/**
	 * @param recommendTypePackage
	 *            the recommendTypePackage to set
	 */
	public void setRecommendTypePackage(String recommendTypePackage) {
		this.recommendTypePackage = recommendTypePackage;
	}

	/**
	 * @param remainingHours
	 *            the remainingHours to set
	 */
	public void setRemainingHours(String remainingHours) {
		this.remainingHours = remainingHours;
	}

	/**
	 * @param remainingMinutes
	 *            the remainingMinutes to set
	 */
	public void setRemainingMinutes(String remainingMinutes) {
		this.remainingMinutes = remainingMinutes;
	}

	/**
	 * @param remainingSeconds
	 *            the remainingSeconds to set
	 */
	public void setRemainingSeconds(String remainingSeconds) {
		this.remainingSeconds = remainingSeconds;
	}

	/**
	 * @param tplSpecHelper
	 *            the tplSpecHelper to set
	 */
	public void setTplSpecHelper(TemplateSpecHelper tplSpecHelper) {
		this.tplSpecHelper = tplSpecHelper;
	}

	/**
	 * @return the servicetype
	 */
	public Long getServicetype() {
		return servicetype;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the isBidPaused
	 */
	public boolean isBidPaused() {
		return isBidPaused;
	}

	/**
	 * @param isBidPaused
	 *            the isBidPaused to set
	 */
	public void setBidPaused(boolean isBidPaused) {
		this.isBidPaused = isBidPaused;
	}

	/**
	 * @return the previousStatusId
	 */
	public Long getPreviousStatusId() {
		return previousStatusId;
	}

	/**
	 * @param previousStatusId
	 *            the previousStatusId to set
	 */
	public void setPreviousStatusId(Long previousStatusId) {
		this.previousStatusId = previousStatusId;
	}

	/**
	 * @return the tolerancePriceUpperBound
	 */
	public BigDecimal getTolerancePriceUpperBound() {
		return tolerancePriceUpperBound;
	}

	/**
	 * @param tolerancePriceUpperBound
	 *            the tolerancePriceUpperBound to set
	 */
	public void setTolerancePriceUpperBound(BigDecimal tolerancePriceUpperBound) {
		this.tolerancePriceUpperBound = tolerancePriceUpperBound;
	}

	/**
	 * @return the tolerancePriceLowerBound
	 */
	public BigDecimal getTolerancePriceLowerBound() {
		return tolerancePriceLowerBound;
	}

	/**
	 * @param tolerancePriceLowerBound
	 *            the tolerancePriceLowerBound to set
	 */
	public void setTolerancePriceLowerBound(BigDecimal tolerancePriceLowerBound) {
		this.tolerancePriceLowerBound = tolerancePriceLowerBound;
	}

	/**
	 * @return the toleranceCheck
	 */
	public boolean isToleranceCheck() {
		return toleranceCheck;
	}

	/**
	 * @param toleranceCheck
	 *            the toleranceCheck to set
	 */
	public void setToleranceCheck(boolean toleranceCheck) {
		this.toleranceCheck = toleranceCheck;
	}

	/**
	 * @return the previousPageUrl
	 */
	public String getPreviousPageUrl() {
		return previousPageUrl;
	}

	/**
	 * @param previousPageUrl
	 *            the previousPageUrl to set
	 */
	public void setPreviousPageUrl(String previousPageUrl) {
		this.previousPageUrl = previousPageUrl;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt
	 *            the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the remainingTime
	 */
	public String getRemainingTime() {
		return remainingTime;
	}

	/**
	 * @param remainingTime
	 *            the remainingTime to set
	 */
	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}

	private String remainingTimeAfterPauseCalculator() {
		if (this.scBidAction != null
				&& this.scBidAction.getActionType().equalsIgnoreCase(
						BiddingConst.BID_ACTION_TYPE_PAUSED)) {
			long durationInMills = this.getScBidSchedule().getEndDate()
					.getTime()
					- this.scBidAction.getCreatedDate().getTime();

			long diffSeconds = durationInMills / 1000 % 60;
			long diffMinutes = durationInMills / (60 * 1000) % 60;
			long diffHours = durationInMills / (60 * 60 * 1000) % 24;
			String duration = durationFormat.format(diffHours) + ":"
					+ durationFormat.format(diffMinutes) + ":"
					+ durationFormat.format(diffSeconds);
			
			System.out.println("Remaing bid time is : $$$$$"+duration);
			return duration;
		} else {
			return "";
		}
	}
}
