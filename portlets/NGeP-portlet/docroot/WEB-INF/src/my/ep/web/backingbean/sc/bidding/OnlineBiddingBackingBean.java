package my.ep.web.backingbean.sc.bidding;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sc.bidding.BiddingManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "onlineBiddingBB")
@RequestScoped
public class OnlineBiddingBackingBean extends BaseBackingBean<BiddingManagedBean> {

	private static Logger log = Logger.getLogger(OnlineBiddingBackingBean.class);

	public OnlineBiddingBackingBean() {

		System.out.println("onlineBiddingBB constructor");

	}
	
	
	public void biddingNumberChangeListener(){}
	
	/**
	 * Get supplier bidding details.
	 * 
	 * @param reqDTO(
	 * 		Long bidScheduleId - bid schedule id (mandatory),
	 * 		Long supplierId - supplier id (mandatory)
	 * )
	 * @return BiddingResDTO(
	 * 		List<ScBidSchedule> activeBidSchedules,
	 * 		ScBidSchedule scBidSchedule,
	 * 		List<ScBidPrice> bidPrices,
	 * 		List<ScBidContactPerson> bidContactPersons
	 * )
	 */
	//public BiddingResDTO retrieveSupplierBidScheduleDtl(BiddingReqDTO reqDTO);
	
	/**
	 * Get a list of supplier bidding price.
	 * 
	 * @param reqDTO(
	 * 		Long bidScheduleId - bid schedule id (mandatory),
	 * 		Long supplierId - supplier id (mandatory)
	 * )
	 * @return BiddingResDTO(List<ScBidPrice> bidPrices)
	 */
	//public BiddingResDTO retrieveSupplierBidPriceList(BiddingReqDTO reqDTO);
	
	/**
	 * Submit Bidding Price
	 * 
	 * @param reqDTO(
	 * 		ScBidPrice scBidPrice - bid price instance (mandatory)
	 * )
	 * @return BiddingResDTO(ScBidPrice scBidPrice)
	 */
	//public BiddingResDTO submitBidPrice(BiddingReqDTO reqDTO);

	

}
