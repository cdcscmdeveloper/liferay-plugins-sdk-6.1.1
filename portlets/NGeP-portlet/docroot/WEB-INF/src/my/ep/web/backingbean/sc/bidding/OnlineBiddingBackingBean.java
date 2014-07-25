package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.OnlineBiddingManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "onlineBiddingBB")
@RequestScoped
public class OnlineBiddingBackingBean extends
		BaseBackingBean<OnlineBiddingManagedBean> {

	private static Logger log = Logger
			.getLogger(OnlineBiddingBackingBean.class);

	public OnlineBiddingBackingBean() {

		System.out.println("onlineBiddingBB constructor");

	}

	public void retrieveOnlineBiddingDtls() {
		// TODO temporary hard coded
		Long supplierId = 1l;
		Long bidScheduleId = 1l;

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setBidScheduleId(bidScheduleId);
			reqDTO.setSupplierId(supplierId);

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setActiveBidSchedules(
						resDTO.getActiveBidSchedules());
				if (resDTO.getActiveBidSchedules() != null) {
					System.out.println("*****ActiveBidSchedules list size is "
							+ resDTO.getActiveBidSchedules().size());
				} else {

				}
				getManagedBean().setBidContactPersons(
						resDTO.getBidContactPersons());
				if (resDTO.getBidPrices() != null) {
					System.out.println("***** bidPrices list size is "
							+ resDTO.getBidPrices().size());
					getManagedBean().setBidPrices(resDTO.getBidPrices());
				}
				getManagedBean().setScBidSchedule(resDTO.getScBidSchedule());

			} else {
				System.out.println("resDTO payload fetch failed");
			}

		}

		finally {
			biddingSO = null;
		}
		itemStartPriceTotal();

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "onlineBiddingMain.xhtml");
	}

	public void slectedBidChangeListener(AjaxBehaviorEvent e) {

		System.out
				.println("*********slectedBidChangeListener called***********");

		Long bidScheduleId = (Long) e.getComponent().getAttributes()
				.get("value");

		// TODO temporary hard coded
		Long supplierId = 1l;
		// Long bidScheduleId = 1l;

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();
			// reqDTO.setBidScheduleId(selectedScheduledBid.getBidScheduleId());
			// TODO temporary hard coded
			reqDTO.setBidScheduleId(bidScheduleId);
			reqDTO.setSupplierId(supplierId);

			BiddingResDTO resDTO = biddingSO
					.retrieveSupplierBidScheduleDtl(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setActiveBidSchedules(
						resDTO.getActiveBidSchedules());
				getManagedBean().setBidContactPersons(
						resDTO.getBidContactPersons());
				getManagedBean().setBidPrices(resDTO.getBidPrices());
				getManagedBean().setScBidSchedule(resDTO.getScBidSchedule());

			} else {
				System.out.println("resDTO payload fetch failed");
			}
		} finally {
			biddingSO = null;
		}
	}

	public void itemDetialsPopupAction(ScBidPrice scBidPrice) {

		getManagedBean().setScBidPrice(scBidPrice);

	}

	public void decrementAmountValueChangeListener(AjaxBehaviorEvent e) {
		ScBidPrice scBidPrice = (ScBidPrice) e.getComponent().getAttributes()
				.get("value");
		BigDecimal bidPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
				.doubleValue()
				- getManagedBean().getSelectedDecrementAmt().doubleValue());
		getManagedBean().setNewBidPrice(bidPrice);
		scBidPrice.setBidPrice(bidPrice);

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(scBidPrice);

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidSchedule(resDTO.getScBidSchedule());
				getManagedBean().setRendered(resDTO.isSuccessful());
				System.out.println("***** bidPrice submitted Sucessfuly *****");

			} else {
				System.out.println("resDTO payload fetch failed");
			}

		}

		finally {
			biddingSO = null;
		}

	}

	private void itemStartPriceTotal() {

		System.out.println("***** inside itemStartPriceTotal() *****");

		Double startPriceTotal = 0D;
		if (getManagedBean().getBidPrices() != null
				&& getManagedBean().getBidPrices().size() != 0) {
			System.out.println("***** BidPrices List is not null *****");
			for (ScBidPrice bidPrice : getManagedBean().getBidPrices()) {
				startPriceTotal += (bidPrice.getScBidSupplierItem()
						.getItemStartPrice().doubleValue() * bidPrice
						.getScBidSupplierItem().getScBidItem().getQty());
			}
			System.out.println("***** startPriceTotal value is *****"
					+ startPriceTotal);
		}
		getManagedBean().setItemStartPriceTotal(
				new DecimalFormat("###,###.##").format(startPriceTotal));

	}

	public void itemLastBidPriceTotal() {

		Double lastBidPriceTotal = 0D;
		if (getManagedBean().getBidPrices() != null
				&& getManagedBean().getBidPrices().size() != 0) {
			for (ScBidPrice bidPrice : getManagedBean().getBidPrices()) {
				lastBidPriceTotal += (bidPrice.getBidPrice().doubleValue() * bidPrice
						.getScBidSupplierItem().getScBidItem().getQty());
			}
		}

		getManagedBean().setItemStartPriceTotal(
				new DecimalFormat("###,###.##").format(lastBidPriceTotal));
	}

	/**
	 * Get a list of supplier bidding price.
	 * 
	 * @param reqDTO
	 *            ( Long bidScheduleId - bid schedule id (mandatory), Long
	 *            supplierId - supplier id (mandatory) )
	 * @return BiddingResDTO(List<ScBidPrice> bidPrices)
	 */
	// public BiddingResDTO retrieveSupplierBidPriceList(BiddingReqDTO reqDTO);

	/**
	 * Submit Bidding Price
	 * 
	 * @param reqDTO
	 *            ( ScBidPrice scBidPrice - bid price instance (mandatory) )
	 * @return BiddingResDTO(ScBidPrice scBidPrice)
	 */
	// public BiddingResDTO submitBidPrice(BiddingReqDTO reqDTO);

}
