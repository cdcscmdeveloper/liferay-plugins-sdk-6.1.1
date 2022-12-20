package my.ep.web.bean.sc.directpurchase;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketSummaryAndPIBean.
 */
public class MarketSummaryAndPIBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;	
	
	/** The supplier id. */
	private Long supplierId;
	
	/** The supplier name. */
	private String supplierName;
	
	/** The delivery term. */
	private String deliveryTerm;
	
	/** The min qty. */
	private String minQty;
	
	/** The add item desc. */
	private String addItemDesc;
	
	/** The price type. */
	private String priceType;
	
	/** The zone. */
	private String zone;
	
	/** The fr qty. */
	private String frQty;
	
	/** The to qty. */
	private String toQty;
	
	/** The unit price. */
	private String unitPrice;
	
	/** The stock availability. */
	private String stockAvailability;
	
	/** The catalogue price. */
	private String cataloguePrice;
	
	/**
	 * Instantiates a new market summary and pi bean.
	 */
	public MarketSummaryAndPIBean(){}
	
	/**
	 * Gets the supplier name.
	 *
	 * @return the supplier name
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * Sets the supplier name.
	 *
	 * @param supplierName the new supplier name
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * Gets the delivery term.
	 *
	 * @return the delivery term
	 */
	public String getDeliveryTerm() {
		return deliveryTerm;
	}

	/**
	 * Sets the delivery term.
	 *
	 * @param deliveryTerm the new delivery term
	 */
	public void setDeliveryTerm(String deliveryTerm) {
		this.deliveryTerm = deliveryTerm;
	}

	/**
	 * Gets the min qty.
	 *
	 * @return the min qty
	 */
	public String getMinQty() {
		return minQty;
	}

	/**
	 * Sets the min qty.
	 *
	 * @param minQty the new min qty
	 */
	public void setMinQty(String minQty) {
		this.minQty = minQty;
	}

	/**
	 * Gets the adds the item desc.
	 *
	 * @return the adds the item desc
	 */
	public String getAddItemDesc() {
		return addItemDesc;
	}

	/**
	 * Sets the adds the item desc.
	 *
	 * @param addItemDesc the new adds the item desc
	 */
	public void setAddItemDesc(String addItemDesc) {
		this.addItemDesc = addItemDesc;
	}

	/**
	 * Gets the price type.
	 *
	 * @return the price type
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * Sets the price type.
	 *
	 * @param priceType the new price type
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
	 * Gets the unit price.
	 *
	 * @return the unit price
	 */
	public String getUnitPrice() {
		return unitPrice;
	}

	/**
	 * Sets the unit price.
	 *
	 * @param unitPrice the new unit price
	 */
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * Gets the stock availability.
	 *
	 * @return the stock availability
	 */
	public String getStockAvailability() {
		return stockAvailability;
	}

	/**
	 * Sets the stock availability.
	 *
	 * @param stockAvailability the new stock availability
	 */
	public void setStockAvailability(String stockAvailability) {
		this.stockAvailability = stockAvailability;
	}

	/**
	 * Gets the fr qty.
	 *
	 * @return the fr qty
	 */
	public String getFrQty() {
		return frQty;
	}

	/**
	 * Sets the fr qty.
	 *
	 * @param frQty the new fr qty
	 */
	public void setFrQty(String frQty) {
		this.frQty = frQty;
	}

	/**
	 * Gets the to qty.
	 *
	 * @return the to qty
	 */
	public String getToQty() {
		return toQty;
	}

	/**
	 * Sets the to qty.
	 *
	 * @param toQty the new to qty
	 */
	public void setToQty(String toQty) {
		this.toQty = toQty;
	}

	/**
	 * Gets the supplier id.
	 *
	 * @return the supplier id
	 */
	public Long getSupplierId() {
		return supplierId;
	}

	/**
	 * Sets the supplier id.
	 *
	 * @param supplierId the new supplier id
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the cataloguePrice
	 */
	public String getCataloguePrice() {
		return cataloguePrice;
	}

	/**
	 * @param cataloguePrice the cataloguePrice to set
	 */
	public void setCataloguePrice(String cataloguePrice) {
		this.cataloguePrice = cataloguePrice;
	}
}
