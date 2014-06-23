package my.ep.web.backingbean.sc.bidding;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.BiddingConst;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.persistence.entity.sc.bidding.ScBidAction;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.BiddingManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "biddingBB")
@ViewScoped
public class BiddingBackingBean extends BaseBackingBean<BiddingManagedBean> {

	private static Logger log = Logger.getLogger(BiddingBackingBean.class);

	public BiddingBackingBean() {

	}

	public void resetSearchFormAction() {

		this.getManagedBean().setSearchBiddingNo(null);
		this.getManagedBean().setSearchDocumentNo(null);
		this.getManagedBean().setSearchBidTitle(null);
		this.getManagedBean().setSearchBiddingDateFrom(null);
		this.getManagedBean().setSearchBiddingDateTo(null);
		this.getManagedBean().setSelectedStatusDescList(null);
	}

	public void bidPausePopUpAction(ScBidSchedule bid) {

		ScBidAction bidAction = new ScBidAction();
		bidAction.setActionType(BiddingConst.BID_ACTION_TYPE_PAUSED);
		bidAction.setScBidSchedule(bid);

		BiddingReqDTO reqDto = new BiddingReqDTO();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			reqDto.setScBidAction(bidAction);
			BiddingResDTO resDto = biddingSO.updateBidAction(reqDto);
			if (resDto.isSuccessful()) {
				this.getManagedBean().setBidAction(resDto.getScBidAction());
			}
		} finally {
			biddingSO = null;
		}

		// this.getManagedBean().getBidAction().setRemainingTime(
		// durationCalculator(this.getManagedBean().getBidAction().getCreatedDate().get,
		// bid.getEndDate()));

	}

	public void bidCancelPopUpAction(ScBidSchedule bid) {

		ScBidAction bidAction = new ScBidAction();
		bidAction.setActionType(BiddingConst.BID_ACTION_TYPE_CANCELLED);
		bidAction.setScBidSchedule(bid);

		BiddingReqDTO reqDto = new BiddingReqDTO();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			reqDto.setScBidAction(bidAction);
			BiddingResDTO resDto = biddingSO.updateBidAction(reqDto);
			if (resDto.isSuccessful()) {
				this.getManagedBean().setBidAction(resDto.getScBidAction());
			}
		} finally {
			biddingSO = null;
		}

		// this.getManagedBean().getBidAction().setRemainingTime(
		// durationCalculator(this.getManagedBean().getBidAction().getCreatedDate().get,
		// bid.getEndDate()));

	}

	public void pauseBidAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			this.getManagedBean().getBidAction()
					.setActionType(BiddingConst.BID_ACTION_TYPE_PAUSED);
			this.getManagedBean().getBidAction()
					.setRemark(this.getManagedBean().getSupplierNotes());
			// this.getManagedBean().getBidAction()
			// .setScBidSchedule(this.getManagedBean().getScheduledBid());
			reqDto.setScBidAction(this.getManagedBean().getBidAction());

			biddingSO.updateBidAction(reqDto);
		}

		finally {
			biddingSO = null;

		}

	}

	public void cancelBidAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			BiddingReqDTO reqDto = new BiddingReqDTO();

			this.getManagedBean().getBidAction()
					.setActionType(BiddingConst.BID_ACTION_TYPE_CANCELLED);
			this.getManagedBean().getBidAction()
					.setRemark(this.getManagedBean().getSupplierNotes());
			this.getManagedBean().getBidAction()
					.setScBidSchedule(this.getManagedBean().getScheduledBid());
			reqDto.setScBidAction(this.getManagedBean().getBidAction());

			biddingSO.updateBidAction(reqDto);

		}

		finally {
			biddingSO = null;

		}

	}

	public void reScheduleOrReBidPopUpAction(ScBidSchedule bid) {

		BiddingReqDTO reqDto = new BiddingReqDTO();

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);

			reqDto.setBidScheduleId(bid.getBidScheduleId());
			BiddingResDTO resDto = biddingSO
					.retrieveLastBidActionByScheduleId(reqDto);
			if (resDto.isSuccessful()) {

				this.getManagedBean().setBidAction(resDto.getScBidAction());
				// this.getManagedBean().getBidAction().setScBidSchedule(bid);
				// this.getManagedBean().getBidAction().setRemainingTime(
				// durationCalculator(this.getManagedBean().getBidAction().getCreatedDate().get,
				// bid.getEndDate()));
			}
		} finally {
			biddingSO = null;
		}

	}

	public void reScheduleOrReBidAction() {

		IBiddingSO biddingSO = null;

		try {
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			BiddingReqDTO reqDto = new BiddingReqDTO();
			
			reqDto.setScBidAction(this.getManagedBean().getBidAction());
			biddingSO.updateBidAction(reqDto);

		}

		finally {
			biddingSO = null;

		}

	}

	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = startDate.getTime() - endDate.getTime();

		return TimeUnit.MILLISECONDS.toMinutes(durationInMills);

	}

}
