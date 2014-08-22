package my.ep.web.backingbean.sc.bidding;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.OnlineBiddingManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "onlineBiddingBB")
@RequestScoped
public class OnlineBiddingBackingBean extends
		BaseBackingBean<OnlineBiddingManagedBean> {

	private static Logger log = Logger
			.getLogger(OnlineBiddingBackingBean.class);
	CommonUtils commonUtils = new CommonUtils();
	DecimalFormat nf = new DecimalFormat(
			commonUtils
					.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));

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

	public void slectedBidChangeListener(ScBidSchedule bid) {

		System.out
				.println("*********slectedBidChangeListener called***********");

		// TODO temporary hard coded
		Long supplierId = 1l;
		Long bidScheduleId = 1L;
		// Long bidScheduleId = bid.getBidScheduleId();
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

	public void decrementAmountValueChangeListener(ScBidPrice scBidPrice){
		
		System.out.println("***** inside decrementAmountValueChangeListener() *****");
		if (scBidPrice != null){
			System.out.println("***** scBidPrice passed to backing bean is not null *****");
		}
		
		ScBidPrice newBidPrice = new ScBidPrice();
		BigDecimal bidPrice;
		///TODO 
		if(scBidPrice.getNewPrice() != null){
			 bidPrice = BigDecimal.valueOf(scBidPrice.getBidPrice()
					.doubleValue()
					-scBidPrice.getNewPrice().doubleValue());
		}else {
			bidPrice = scBidPrice.getBidPrice();
		}
		newBidPrice.setBidPrice(bidPrice);
		newBidPrice.setScBidSupplierItem(scBidPrice.getScBidSupplierItem());
		scBidPrice.setNewBidPrice(newBidPrice);
		int index = getManagedBean().getBidPrices().indexOf(scBidPrice);
		getManagedBean().getBidPrices().set(index, scBidPrice);
		getManagedBean().setNewScBidPrice(newBidPrice);
		getManagedBean().setScBidPrice(scBidPrice);
		//itemNewBidPriceTotal();
		
	}
	
	public void submitBidPrice() {
		
		
		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDTO = new BiddingReqDTO();

			reqDTO.setNewBidPrice(getManagedBean().getNewScBidPrice());

			BiddingResDTO resDTO = biddingSO.submitBidPrice(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setScBidSchedule(resDTO.getScBidSchedule());
				int index = getManagedBean().getBidPrices().indexOf(getManagedBean().getScBidPrice());
				getManagedBean().getBidPrices().set(index,resDTO.getScBidPrice());
				
				List<ScBidPrice> bidPriceList =  getManagedBean().getBidPrices();
				
				for(ScBidPrice bidPrice : bidPriceList){
					
					bidPrice.setNewBidPrice(null);
				}
				getManagedBean().setBidPrices(bidPriceList);
				
				System.out.println("***** bidPrice submitted Sucessfuly *****");
				
			} else {

				System.out.println("*****bidPrice submission failed*****");
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

		getManagedBean().setItemStartPriceTotal(nf.format(startPriceTotal));
	}

	
	
}
