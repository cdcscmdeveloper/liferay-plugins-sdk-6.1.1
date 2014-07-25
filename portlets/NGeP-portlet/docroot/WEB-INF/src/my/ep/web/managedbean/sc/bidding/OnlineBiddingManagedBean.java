package my.ep.web.managedbean.sc.bidding;

import java.math.BigDecimal;
import java.util.List;

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
	
	
	
	/**
	 * @return the rendered
	 */
	public boolean isRendered() {
		return rendered;
	}
	/**
	 * @param rendered the rendered to set
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
	 * @param itemLastBidPriceTotal the itemLastBidPriceTotal to set
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
	 * @param itemStartPriceTotal the itemStartPriceTotal to set
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
	 * @param newBidPrice the newBidPrice to set
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
	 * @param selectedDecrementAmt the selectedDecrementAmt to set
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
	 * @param scBidPrice the scBidPrice to set
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
	 * @param activeBidSchedules the activeBidSchedules to set
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
	 * @param scBidSchedule the scBidSchedule to set
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
	 * @param bidPrices the bidPrices to set
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
	 * @param bidContactPersons the bidContactPersons to set
	 */
	public void setBidContactPersons(List<ScBidContactPerson> bidContactPersons) {
		this.bidContactPersons = bidContactPersons;
	}
	
	

}