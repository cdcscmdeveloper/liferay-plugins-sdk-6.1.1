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
	private String subSequence;
	private String unitPrice;
	private String totalAmount;
	private String note;
	private String zone;
	private String freqUom;
	private String uomPerDay;
	private String uomPerMonth;
	private String uomPerYear;
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getSubSequence() {
		return subSequence;
	}
	public void setSubSequence(String subSequence) {
		this.subSequence = subSequence;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getFreqUom() {
		return freqUom;
	}
	public void setFreqUom(String freqUom) {
		this.freqUom = freqUom;
	}
	public String getUomPerDay() {
		return uomPerDay;
	}
	public void setUomPerDay(String uomPerDay) {
		this.uomPerDay = uomPerDay;
	}
	public String getUomPerMonth() {
		return uomPerMonth;
	}
	public void setUomPerMonth(String uomPerMonth) {
		this.uomPerMonth = uomPerMonth;
	}
	public String getUomPerYear() {
		return uomPerYear;
	}
	public void setUomPerYear(String uomPerYear) {
		this.uomPerYear = uomPerYear;
	}


	
}
