package my.ep.web.backingbean.sc.directpurchase;

import java.io.Serializable;

public class MarketSummaryAndPIBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	

	private Long supplierItemId;
	private String supplierName;
	private String deliveryTerm;
	private String minQty;
	private String addItemDesc;
	private String priceType;
	private String zone;
	private int frQty;
	private int toQty;
	private String unitPrice;
	private String stockAvailability;
	
	public MarketSummaryAndPIBean(){}
	
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

	public Long getSupplierItemId() {
		return supplierItemId;
	}

	public void setSupplierItemId(Long supplierItemId) {
		this.supplierItemId = supplierItemId;
	}

	public int getFrQty() {
		return frQty;
	}

	public void setFrQty(int frQty) {
		this.frQty = frQty;
	}

	public int getToQty() {
		return toQty;
	}

	public void setToQty(int toQty) {
		this.toQty = toQty;
	}


		
	
	
}
