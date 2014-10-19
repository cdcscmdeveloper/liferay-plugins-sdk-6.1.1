package my.ep.web.backingbean.sc.bidding;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "onlineBiddingBB")
@ViewScoped
public class OnlineBiddingBackingBean extends BaseBackingBean {

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
	private String hashSupplierId;
	private Long selectedBidScheduleId;

	private String itemStartPriceTotal;
	private String itemLastBidPriceTotal;
	private String itemNewBidPriceTotal;
	private Long interval;
	private Long remainingBidTime;
	private int packageRank;

	private static Logger log = Logger
			.getLogger(OnlineBiddingBackingBean.class);

	public OnlineBiddingBackingBean() {

		System.out
				.println("*********************OnlineBiddingBackingBean constructor*******************");
	}

	@PostConstruct
	private void init() {

		System.out.println("********************* init() *******************");

		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String docId = originalServletRequest.getParameter("docId");
		String encodedSupplierId = originalServletRequest
				.getParameter("supplierId");
		String bidScheduleIdStr = originalServletRequest
				.getParameter("bidScheduleId");

		Long bidScheduleId = null;
		
		if (StringUtils.isNotBlank(bidScheduleIdStr)) {
			bidScheduleId = Long.parseLong(bidScheduleIdStr);
		}
		
		retrieveOnlineBiddingDtls(Long.parseLong(docId), encodedSupplierId,
				bidScheduleId,
				((PmUser) (new FacesScopes().getUserSessionBean())).getUserId());
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

	private void retrieveOnlineBiddingDtls(Long docId, String hashSupplierId,
			Long bidScheduleId, Long userId) {

		log.info("retrieveOnlineBiddingDtls");

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setUserID(userId);
			reqDTO.setHashSupplierId(hashSupplierId);
			reqDTO.setDocId(docId);
			reqDTO.setBidScheduleId(bidScheduleId);
			
			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				ScBidSchedule bidSchedule =resDTO.getScBidSchedule();
				
				setActiveBidSchedules(resDTO.getActiveBidSchedules());
				setScBidSchedule(bidSchedule);
				setHashSupplierId(hashSupplierId);
				setSelectedBidScheduleId(bidSchedule.getBidScheduleId());
				setBidContactPersons(resDTO.getBidContactPersons());
				setSmSupplier(resDTO.getSmSupplier());

				if (resDTO.getPackageRanking() != null) {
					setScBidAction(resDTO.getScBidAction());
				}

				if (resDTO.getScBidAction() != null) {
					setPackageRanking(resDTO.getPackageRanking());
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
	
	/*public String navigateToSelectedBid(){
		
		Long bidScheduleId = getSelectedBidScheduleId();
		System.out.println("********* bidScheduleId *******"+ bidScheduleId);
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
		BiddingReqDTO reqDTO = new BiddingReqDTO();
		reqDTO.setBidScheduleId(bidScheduleId);
		BiddingResDTO resDTO = biddingSO
				.retrieveBidScheduleById(reqDTO);
		this.setScBidSchedule(resDTO
				.getScBidSchedule());
		}finally {
			biddingSO = null;
		}

		//getOnlineBidScheduleDetails(bidScheduleId, getSmSupplier()
			//	.getSupplierId());

		//FacesContext fc = FacesContext.getCurrentInstance();
		
		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_PENDING_BIDDING)) {
			
			String reDirectionQueryStringForPB = "onlineBiddingCountdown.xhtml"
					+ "&faces-redirect=true&docId=" + getDocId() + "&supplierId=" + getHashSupplierId()
					+ "&bidScheduleId=" + getScBidSchedule().getBidScheduleId();
			System.out.println("********* reDirectionQueryStringForPB *******"+ reDirectionQueryStringForPB);
			//fc.getApplication().getNavigationHandler()
				//	.handleNavigation(fc, null, reDirectionQueryStringForPB);
			
			return reDirectionQueryStringForPB;
		} else if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_IN_PROGRESS)) {
			
			String reDirectionQueryStringForOB = "onlineBiddingMain.xhtml"
					+ "&faces-redirect=true&docId=" + getDocId() + "&supplierId=" + getHashSupplierId()
					+ "&bidScheduleId=" + getScBidSchedule().getBidScheduleId();
			System.out.println("********* reDirectionQueryStringForOB *******"+ reDirectionQueryStringForOB);
			//fc.getApplication().getNavigationHandler()
				//	.handleNavigation(fc, null, reDirectionQueryStringForOB);
			return reDirectionQueryStringForOB;
		} else {
			
			String reDirectionQueryStringForRest = "supplierOnlineBidding.xhtml"
					+ "&faces-redirect=true&docId=" + getDocId() + "&supplierId=" + getHashSupplierId()
					+ "&bidScheduleId=" + getScBidSchedule().getBidScheduleId();
			System.out.println("********* reDirectionQueryStringForRest *******"+ reDirectionQueryStringForRest);
			//fc.getApplication().getNavigationHandler()
				//	.handleNavigation(fc, null, reDirectionQueryStringForRest);
			return reDirectionQueryStringForRest;
		}

	}*/

	public void selectedBidChangeListener() throws PortalException, SystemException, IOException {
		log.info("selectedBidChangeListener");
		
		ThemeDisplay themeDisplay = (ThemeDisplay) new FacesScopes()
			.getRequestScope(WebKeys.THEME_DISPLAY);
		String commonUrl = themeDisplay.getURLCurrent().substring(
				0,
				themeDisplay.getURLCurrent().indexOf(
						themeDisplay.getLayout().getGroup()
								.getFriendlyURL())
						+ themeDisplay.getLayout().getGroup()
								.getFriendlyURL().length());

		System.out.println("commonUrl" + commonUrl);

		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		externalContext.redirect(commonUrl + "/" + 
				SourcingConstants.SC_BID_PAGE + scBidSchedule.getScBid().getDocId() +
				"&supplierId=" + hashSupplierId +
				"&bidScheduleId=" + selectedBidScheduleId);
	}

	public void pollListener() {
		log.info("pollListener - start");
		getOnlineBidScheduleDetails(getScBidSchedule().getBidScheduleId(),
				getSmSupplier().getSupplierId());

		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_CANCELLED)
				|| getScBidSchedule().getPmStatus().getStatusId()
						.equals(BidStatusConst.BID_PAUSED)) {
			RequestContext.getCurrentInstance().execute(
					"onlineBiddingNotification.show()");
		}
		
		if (!getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_COMPLETED)) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, "supplierOnlineBidding.xhtml");
		}
		
		log.info("pollListener - end");
	}

	private void getOnlineBidScheduleDetails(Long bidScheduleID, Long supplierID) {
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			reqDTO.setBidScheduleId(bidScheduleID);
			reqDTO.setSupplierId(supplierID);

			BiddingResDTO resDTO = biddingSO.retrieveCurrentBidDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				setScBidSchedule(resDTO.getScBidSchedule());
				setPackageRanking(resDTO.getPackageRanking());
				setScBidAction(resDTO.getScBidAction());
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
				setBidPrices(fetchedBidPrices);
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

		System.out.println("newPrice value is " + scBidPrice.getNewPrice());
		log.info("decrementAmountValueChangeListener");

		ScBidPrice newBidPrice = new ScBidPrice();
		BigDecimal newPrice = null;
		if (scBidPrice.getBidPrice() == null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = BigDecimal.valueOf(scBidPrice.getScBidSupplierItem()
					.getItemStartPrice().doubleValue()
					- scBidPrice.getNewPrice().doubleValue());
		}

		else if (scBidPrice.getBidPrice() != null
				&& scBidPrice.getNewPrice() != null) {
			newPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
					.doubleValue() - scBidPrice.getNewPrice().doubleValue());
		}
		newBidPrice.setBidPrice(newPrice);
		newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
		scBidPrice.setNewBidPrice(newBidPrice);
		int index = getBidPrices().indexOf(scBidPrice);
		getBidPrices().set(index, scBidPrice);

	}

	public void updateBidPriceAndCalculateRank(ScBidPrice bidPriceItem) {
		log.info("updateBidPriceNDcalculateRank");

		ScBidPrice priceItemToPersist = bidPriceItem.getNewBidPrice();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(priceItemToPersist);

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				// setScBidSchedule(resDTO.getScBidSchedule());
				int index = getBidPrices().indexOf(bidPriceItem);
				getBidPrices().set(index, resDTO.getScBidPrice());

				System.out.println("*****item rank is***** "
						+ resDTO.getScBidPrice().getRanking());

				if (ParameterConst.RECOMMEND_TYPE_PACKAGE.equals(this
						.getScBidSchedule().getScBid().getRecommendedTypeId()
						.getParameterCode())) {
					setPackageRank(resDTO.getPackageRanking().intValue());
				}
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

		Double newBidPriceTotal = 0D;
		if (this.getBidPrices() != null && this.getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : this.getBidPrices()) {

				if (bidPrice.getNewBidPrice() != null
						&& bidPrice.getNewBidPrice().getBidPrice() != null) {
					newBidPriceTotal += (bidPrice.getNewBidPrice()
							.getBidPrice().doubleValue() * bidPrice
							.getScBidSupplierItem().getScBidItem().getQty());
				} else if (bidPrice.getBidPrice() != null) {
					newBidPriceTotal += (bidPrice.getBidPrice().doubleValue() * bidPrice
							.getScBidSupplierItem().getScBidItem().getQty());
				} else {
					newBidPriceTotal += (bidPrice.getScBidSupplierItem()
							.getItemStartPrice().doubleValue() * bidPrice
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

				if (bidPrice.getBidPrice() == null) {
					lastBidPriceTotal += bidPrice.getScBidSupplierItem()
							.getItemStartPrice().doubleValue()
							* bidPrice.getScBidSupplierItem().getScBidItem()
									.getQty();
				} else {
					lastBidPriceTotal += (bidPrice.getBidPrice().doubleValue() * bidPrice
							.getScBidSupplierItem().getScBidItem().getQty());
				}
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

		if (getScBidSchedule().getPmStatus().getStatusId()
				.equals(BidStatusConst.BID_PENDING_BIDDING)) {

			if (!(durationCalculator(new Date(), scBidSchedule.getStartDate()) < 0)) {

				interval = durationCalculator(new Date(),
						scBidSchedule.getStartDate());
			} else {
				interval = 10L;
			}

		} else {
			interval = 10L;
		}

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
	 * @return the packageRank
	 */
	public int getPackageRank() {
		return packageRank;
	}

	/**
	 * @param packageRank
	 *            the packageRank to set
	 */
	public void setPackageRank(int packageRank) {
		this.packageRank = packageRank;
	}

	/**
	 * @return the hashSupplierId
	 */
	public String getHashSupplierId() {
		return hashSupplierId;
	}

	/**
	 * @param hashSupplierId
	 *            the hashSupplierId to set
	 */
	public void setHashSupplierId(String hashSupplierId) {
		this.hashSupplierId = hashSupplierId;
	}
	
	/**
	 * @return the selectedBidScheduleId
	 */
	public Long getSelectedBidScheduleId() {
		return selectedBidScheduleId;
	}

	/**
	 * @param selectedBidScheduleId the selectedBidScheduleId to set
	 */
	public void setSelectedBidScheduleId(Long selectedBidScheduleId) {
		this.selectedBidScheduleId = selectedBidScheduleId;
	}

	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		long time = TimeUnit.SECONDS.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		return time;
	}

}
