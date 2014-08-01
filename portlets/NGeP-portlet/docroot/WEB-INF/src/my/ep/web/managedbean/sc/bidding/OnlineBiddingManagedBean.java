package my.ep.web.managedbean.sc.bidding;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.bidding.ScBidContactPerson;
import my.ep.persistence.entity.sc.bidding.ScBidPrice;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Venkat Chanumolu
 * 
 */

@ManagedBean(name = "onlineBiddingMB")
@SessionScoped
public class OnlineBiddingManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private List<ScBidSchedule> activeBidSchedules;
	private ScBidSchedule scBidSchedule;
	private List<ScBidPrice> bidPrices;
	private List<ScBidContactPerson> bidContactPersons;
	private ScBidPrice scBidPrice;
	private BigDecimal selectedDecrementAmt;
	private BigDecimal newBidPrice;
	private String itemStartPriceTotal;
	private String itemLastBidPriceTotal;
	private boolean rendered;
	private Long remainingBidTime;
	private Long remainingTimeToBid;
	
	
	private BigDecimal testDecrementAmt1 =BigDecimal.valueOf(1000.00)  ;
	private BigDecimal testminAmt1 = BigDecimal.valueOf(1000.00);
	private BigDecimal testmaxAmt1= BigDecimal.valueOf(10000.00);
	
	private Double testDecrementAmt3 = 1000.00d;
	private Double testminAmt3 = 1000.00d;
	private Double testmaxAmt3 = 10000.00d;
	
	
	private Long testDecrementAmt2 = 1000l;
	private Long testminAmt2 = 1000l;
	private Long testmaxAmt2 = 10000l;
	

	
	public BigDecimal getTestDecrementAmt1() {
		return testDecrementAmt1;
	}

	public BigDecimal getTestminAmt1() {
		return testminAmt1;
	}

	public BigDecimal getTestmaxAmt1() {
		return testmaxAmt1;
	}

	public Double getTestDecrementAmt3() {
		return testDecrementAmt3;
	}

	public Double getTestminAmt3() {
		return testminAmt3;
	}

	public Double getTestmaxAmt3() {
		return testmaxAmt3;
	}

	public Long getTestDecrementAmt2() {
		return testDecrementAmt2;
	}

	public Long getTestminAmt2() {
		return testminAmt2;
	}

	public Long getTestmaxAmt2() {
		return testmaxAmt2;
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
	 * @return the rendered
	 */
	public boolean isRendered() {
		return rendered;
	}

	/**
	 * @param rendered
	 *            the rendered to set
	 */
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	/**
	 * @return the itemLastBidPriceTotal
	 */
	public String getItemLastBidPriceTotal() {
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

	private Long durationCalculator(Date startDate, Date endDate) {

		long durationInMills = endDate.getTime() - startDate.getTime();

		long time = TimeUnit.SECONDS.convert(durationInMills,
				TimeUnit.MILLISECONDS);

		return time;
	}

}