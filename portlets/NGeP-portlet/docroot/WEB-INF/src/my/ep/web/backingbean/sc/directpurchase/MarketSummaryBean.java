package my.ep.web.backingbean.sc.directpurchase;

import java.io.Serializable;

public class MarketSummaryBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

	private String supplierName;
	private String deliveryTerm;
	private String minQty;
	private String addItemDesc;
	private String priceType;
	private String zone;
	private String frQty;
	private String toQty;
	private String unitPrice;
	private String stockAvailability;
	
	public MarketSummaryBean(){}
	
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getDeliveryTerm() {
		return deliveryTerm;
	}

	public void setDeliveryTerm(String deliveryTerm) {
		this.deliveryTerm = deliveryTerm;
	}

	public String getMinQty() {
		return minQty;
	}

	public void setMinQty(String minQty) {
		this.minQty = minQty;
	}

	public String getAddItemDesc() {
		return addItemDesc;
	}

	public void setAddItemDesc(String addItemDesc) {
		this.addItemDesc = addItemDesc;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getFrQty() {
		return frQty;
	}

	public void setFrQty(String frQty) {
		this.frQty = frQty;
	}

	public String getToQty() {
		return toQty;
	}

	public void setToQty(String toQty) {
		this.toQty = toQty;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getStockAvailability() {
		return stockAvailability;
	}

	public void setStockAvailability(String stockAvailability) {
		this.stockAvailability = stockAvailability;
	}
		
	
	
}
