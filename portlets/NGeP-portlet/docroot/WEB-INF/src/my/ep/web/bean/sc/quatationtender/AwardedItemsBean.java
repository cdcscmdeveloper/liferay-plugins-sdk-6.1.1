/**
 *  Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Bean holds required properties of awarded items 
 * to display in Awarded list screen.
 * 
 * @author Hemanth
 *
 */
public class AwardedItemsBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String itemName;
	private String supplierName;
	private BigDecimal priceByLoa;
	private BigDecimal priceByProposal;
	private BigDecimal priceByBidding;
	private BigDecimal loaAmount; // #29624

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName
	 *            the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName
	 *            the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the priceByLoa
	 */
	public BigDecimal getPriceByLoa() {
		return priceByLoa;
	}

	/**
	 * @param priceByLoa
	 *            the priceByLoa to set
	 */
	public void setPriceByLoa(BigDecimal priceByLoa) {
		this.priceByLoa = priceByLoa;
	}

	/**
	 * @return the priceByProposal
	 */
	public BigDecimal getPriceByProposal() {
		return priceByProposal;
	}

	/**
	 * @param priceByProposal
	 *            the priceByProposal to set
	 */
	public void setPriceByProposal(BigDecimal priceByProposal) {
		this.priceByProposal = priceByProposal;
	}

	/**
	 * @return the priceByBidding
	 */
	public BigDecimal getPriceByBidding() {
		return priceByBidding;
	}

	/**
	 * @param priceByBidding the priceByBidding to set
	 */
	public void setPriceByBidding(BigDecimal priceByBidding) {
		this.priceByBidding = priceByBidding;
	}

	// #29624 - start
	public BigDecimal getLoaAmount() {
		return loaAmount;
	}

	public void setLoaAmount(BigDecimal loaAmount) {
		this.loaAmount = loaAmount;
	}
	// #29624 - end
}
