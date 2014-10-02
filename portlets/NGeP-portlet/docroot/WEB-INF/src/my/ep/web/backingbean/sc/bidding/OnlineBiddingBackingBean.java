package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidPackage;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "onlineBiddingBB")
@ViewScoped
public class OnlineBiddingBackingBean extends BaseBackingBean {
	private static final long serialVersionUID = 1L;

	private List<ScBidSchedule> activeBidSchedules;
	private ScBidSchedule scBidSchedule;
	private List<ScBidPrice> bidPrices;
	private List<ScBidContactPerson> bidContactPersons;
	private SmSupplier smSupplier;
	private ScBidAction scBidAction;
	private ScBidPackage scBidPackage;
	private ScBidPrice scBidPrice;
	private ScBidPrice newScBidPrice;
	private BigDecimal selectedDecrementAmt;
	private BigDecimal newBidPrice;

	private String itemStartPriceTotal;
	private String itemLastBidPriceTotal;
	private String itemNewBidPriceTotal;
	private Long interval = 30L;
	private Long remainingBidTime;
	private Long remainingTimeToBid;

	private static Logger log = Logger
			.getLogger(OnlineBiddingBackingBean.class);

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
	 * To redirect to Supplier online Bidding Screen..
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String bidScheduleId = originalServletRequest
				.getParameter("bidScheduleId");
		String encodedSupplierId = originalServletRequest
				.getParameter("supplierId");
		retrieveOnlineBiddingDtls(Long.parseLong(bidScheduleId),
				encodedSupplierId,
				((PmUser) (new FacesScopes().getUserSessionBean())).getUserId());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "onlineBiddingMain.xhtml");
	}

	private void retrieveOnlineBiddingDtls(Long bidScheduleId,
			String supplierId, Long userId) {

		log.info("retrieveOnlineBiddingDtls");

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setUserID(userId);
			reqDTO.setHashSupplierId(supplierId);
			reqDTO.setBidScheduleId(bidScheduleId);

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				setActiveBidSchedules(resDTO.getActiveBidSchedules());
				setScBidSchedule(resDTO.getScBidSchedule());
				setBidContactPersons(resDTO.getBidContactPersons());
				setSmSupplier(resDTO.getSmSupplier());

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

	public void selectedBidChangeListener(ScBidSchedule bid) {
		log.info("selectedBidChangeListener");
		getOnlineBidScheduleDetails(bid.getBidScheduleId(), getSmSupplier()
				.getSupplierId());

		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_CANCELLED)
				|| getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PAUSED)) {
			RequestContext.getCurrentInstance().execute("onlineBiddingNotification.show()");
		}
	}

	public void pollListener() {
		log.info("pollListener");
		getOnlineBidScheduleDetails(getScBidSchedule().getBidScheduleId(),
				getSmSupplier().getSupplierId());
		
		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_CANCELLED)
				|| getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PAUSED)) {
			RequestContext.getCurrentInstance().execute("onlineBiddingNotification.show()");
		}
	}

	private void getOnlineBidScheduleDetails(Long bidScheduleID, Long supplierID) {
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidScheduleId(bidScheduleID);
			reqDTO.setSupplierId(supplierID);

			BiddingResDTO resDTO = biddingSO
					.retrieveOnlineBidScheduleDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				setBidContactPersons(resDTO.getBidContactPersons());
				setBidPrices(resDTO.getBidPrices());
				setScBidSchedule(resDTO.getScBidSchedule());
				setScBidPackage(resDTO.getScBidPackage());
				setScBidAction(resDTO.getScBidAction());
			}
		} finally {
			biddingSO = null;
		}
	}

	public void itemDetialsPopupAction(ScBidPrice scBidPrice) {
		log.info("itemDetialsPopupAction");

		setScBidPrice(scBidPrice);

	}

	public void decrementAmountValueChangeListener(ScBidPrice scBidPrice) {
		log.info("decrementAmountValueChangeListener");

		ScBidPrice newBidPrice = new ScBidPrice();
		BigDecimal bidPrice;
		// /TODO
		if (scBidPrice.getNewPrice() != null) {
			bidPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
					.doubleValue() - scBidPrice.getNewPrice().doubleValue());
		} else {
			bidPrice = scBidPrice.getBidPrice();
		}
		newBidPrice.setBidPrice(bidPrice);
		newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
		scBidPrice.setNewBidPrice(newBidPrice);
		int index = getBidPrices().indexOf(scBidPrice);
		getBidPrices().set(index, scBidPrice);
		setNewScBidPrice(newBidPrice);
		setScBidPrice(scBidPrice);
		// itemNewBidPriceTotal();

	}

	public void updateBidPriceAndCalculateRank() {
		log.info("updateBidPriceNDcalculateRank");
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(getNewScBidPrice());

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				setScBidSchedule(resDTO.getScBidSchedule());
				int index = getBidPrices().indexOf(getScBidPrice());
				getBidPrices().set(index, resDTO.getScBidPrice());

				List<ScBidPrice> bidPriceList = getBidPrices();

				for (ScBidPrice bidPrice : bidPriceList) {

					bidPrice.setNewBidPrice(null);
				}
				setBidPrices(bidPriceList);
			}

		} finally {
			biddingSO = null;
		}
	}

	private void itemStartPriceTotal() {
		Double startPriceTotal = 0D;
		if (getBidPrices() != null && getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : getBidPrices()) {
				startPriceTotal += (bidPrice.getScBidSupplierItem()
						.getItemStartPrice().doubleValue() * bidPrice
						.getScBidSupplierItem().getScBidItem().getQty());
			}
		}

		setItemStartPriceTotal(nf.format(startPriceTotal));
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
	 * @return the itemNewBidPriceTotal
	 */
	public String getItemNewBidPriceTotal() {

		System.out
				.println("******!!!$$$$ inside getItemNewBidPriceTotal method ***** ");

		Double newBidPriceTotal = 0D;
		if (this.getBidPrices() != null && this.getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : this.getBidPrices()) {

				if (bidPrice.getNewBidPrice() != null
						&& bidPrice.getNewBidPrice().getBidPrice() != null) {

					newBidPriceTotal += (bidPrice.getNewBidPrice()
							.getBidPrice().doubleValue() * bidPrice
							.getScBidSupplierItem().getScBidItem().getQty());
				} else {

					newBidPriceTotal += (bidPrice.getBidPrice().doubleValue() * bidPrice
							.getScBidSupplierItem().getScBidItem().getQty());
				}

			}
		}
		this.setItemNewBidPriceTotal(nf.format(newBidPriceTotal));

		return itemNewBidPriceTotal;
	}

	/**
	 * @param itemNewBidPriceTotal
	 *            the itemNewBidPriceTotal to set
	 */
	public void setItemNewBidPriceTotal(String itemNewBidPriceTotal) {
		this.itemNewBidPriceTotal = itemNewBidPriceTotal;
	}

	/**
	 * @return the remainingTimeToBid
	 */
	public Long getRemainingTimeToBid() {

		if (scBidSchedule != null) {

			remainingTimeToBid = durationCalculator(new Date(),
					scBidSchedule.getStartDate());
		}

		return remainingTimeToBid;
	}

	/**
	 * @param remainingTimeToBid
	 *            the remainingTimeToBid to set
	 */
	public void setRemainingTimeToBid(Long remainingTimeToBid) {
		this.remainingTimeToBid = remainingTimeToBid;
	}

	/**
	 * @return the remainingBidTime
	 */
	public Long getRemainingBidTime() {

		if (scBidSchedule != null) {

			remainingBidTime = durationCalculator(new Date(),
					scBidSchedule.getEndDate());
		}
		return remainingBidTime;
	}

	/**
	 * @param remainingBidTime
	 *            the remainingBidTime to set
	 */
	public void setRemainingBidTime(Long remainingBidTime) {
		this.remainingBidTime = remainingBidTime;
	}

	/**
	 * @return the itemLastBidPriceTotal
	 */
	public String getItemLastBidPriceTotal() {

		Double lastBidPriceTotal = 0D;
		if (this.getBidPrices() != null && this.getBidPrices().size() != 0) {

			for (ScBidPrice bidPrice : this.getBidPrices()) {
				lastBidPriceTotal += (bidPrice.getBidPrice().doubleValue() * bidPrice
						.getScBidSupplierItem().getScBidItem().getQty());
			}
		}
		this.setItemLastBidPriceTotal(nf.format(lastBidPriceTotal));
		return itemLastBidPriceTotal;
	}

	/**
	 * @param itemLastBidPriceTotal
	 *            the itemLastBidPriceTotal to set
	 */
	public void setItemLastBidPriceTotal(String itemLastBidPriceTotal) {
		this.itemLastBidPriceTotal = itemLastBidPriceTotal;
	}

	/**
	 * @return the itemStartPriceTotal
	 */
	public String getItemStartPriceTotal() {
		return itemStartPriceTotal;
	}

	/**
	 * @param itemStartPriceTotal
	 *            the itemStartPriceTotal to set
	 */
	public void setItemStartPriceTotal(String itemStartPriceTotal) {
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
	 * @return the interval
	 */
	public Long getInterval() {

		/*
		 * if
		 * (getScBidSchedule().getPmStatus().getStatusId().equals(BidStatusConst
		 * .BID_PENDING_BIDDING)){ setInterval(getRemainingTimeToBid()); }else
		 * if
		 * (getScBidSchedule().getPmStatus().getStatusId().equals(BidStatusConst
		 * .BID_IN_PROGRESS)){ setInterval(5L); }
		 */
		return interval;
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
	 * @return the scBidPackage
	 */
	public ScBidPackage getScBidPackage() {
		return scBidPackage;
	}

	/**
	 * @param scBidPackage
	 *            the scBidPackage to set
	 */
	public void setScBidPackage(ScBidPackage scBidPackage) {
		this.scBidPackage = scBidPackage;
	}

	/**
	 * @return the scBidAction
	 */
	public ScBidAction getScBidAction() {
		return scBidAction;
	}

	/**
	 * @param scBidAction the scBidAction to set
	 */
	public void setScBidAction(ScBidAction scBidAction) {
		this.scBidAction = scBidAction;
	}

	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		long time = TimeUnit.SECONDS.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		return time;
	}

}
