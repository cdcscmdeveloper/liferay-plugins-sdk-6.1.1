/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.io.Serializable;

public class JadualHargaBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String item;
	private String uom;
	private String quantity;
	private String tier;
	private String subSequence;
	private String unitPrice;
	private String totalAmount;
	private String note;
	private String zone;
	private String freqUom;
	private String uomPerDay;
	private String uomPerMonth;
	private String uomPerYear;
	private String priceType;
	
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}
	
	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}
	
	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	
	/**
	 * @param zone the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	/**
	 * @return the subSequence
	 */
	public String getSubSequence() {
		return subSequence;
	}
	
	/**
	 * @param subSequence the subSequence to set
	 */
	public void setSubSequence(String subSequence) {
		this.subSequence = subSequence;
	}
	
	/**
	 * @return the unitPrice
	 */
	public String getUnitPrice() {
		return unitPrice;
	}
	
	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	/**
	 * @return the totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}
	
	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * @return the freqUom
	 */
	public String getFreqUom() {
		return freqUom;
	}
	
	/**
	 * @param freqUom the freqUom to set
	 */
	public void setFreqUom(String freqUom) {
		this.freqUom = freqUom;
	}
	
	/**
	 * @return the uomPerDay
	 */
	public String getUomPerDay() {
		return uomPerDay;
	}
	
	/**
	 * @param uomPerDay the uomPerDay to set
	 */
	public void setUomPerDay(String uomPerDay) {
		this.uomPerDay = uomPerDay;
	}
	
	/**
	 * @return the uomPerMonth
	 */
	public String getUomPerMonth() {
		return uomPerMonth;
	}
	
	/**
	 * @param uomPerMonth the uomPerMonth to set
	 */
	public void setUomPerMonth(String uomPerMonth) {
		this.uomPerMonth = uomPerMonth;
	}
	
	/**
	 * @return the uomPerYear
	 */
	public String getUomPerYear() {
		return uomPerYear;
	}
	
	/**
	 * @param uomPerYear the uomPerYear to set
	 */
	public void setUomPerYear(String uomPerYear) {
		this.uomPerYear = uomPerYear;
	}
	
	/**
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}
	
	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	
	/**
	 * @return the tier
	 */
	public String getTier() {
		return tier;
	}
	
	/**
	 * @param tier the tier to set
	 */
	public void setTier(String tier) {
		this.tier = tier;
	}
}
