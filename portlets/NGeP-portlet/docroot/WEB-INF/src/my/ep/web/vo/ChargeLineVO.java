/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.math.BigDecimal;


/**
 * ChargeLineVO
 * 
 * @author KK LIM
 * @since 20/5/2014
 *
 */
public class ChargeLineVO  {
	
	
	private String itemCode;
    
    private String itemName;
     
    private String uom;
    
    private BigDecimal unitPrice = new BigDecimal(0);
    
    private BigDecimal orderedAmt = new BigDecimal(0);
    
    private BigDecimal invoiceAmt = new BigDecimal(0);
    
    private BigDecimal danAmt = new BigDecimal(0);
    
    public ChargeLineVO(String itemCode, String itemName, String uom,BigDecimal unitPrice,BigDecimal orderedAmt,BigDecimal invoiceAmt,BigDecimal danAmt) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.uom = uom;
        this.unitPrice = unitPrice;
        this.orderedAmt = orderedAmt;
        this.invoiceAmt = invoiceAmt;
        this.danAmt = danAmt;
    }
    

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
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
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the orderedAmt
	 */
	public BigDecimal getOrderedAmt() {
		return orderedAmt;
	}

	/**
	 * @param orderedAmt the orderedAmt to set
	 */
	public void setOrderedAmt(BigDecimal orderedAmt) {
		this.orderedAmt = orderedAmt;
	}

	/**
	 * @return the invoiceAmt
	 */
	public BigDecimal getInvoiceAmt() {
		return invoiceAmt;
	}

	/**
	 * @param invoiceAmt the invoiceAmt to set
	 */
	public void setInvoiceAmt(BigDecimal invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	/**
	 * @return the danAmt
	 */
	public BigDecimal getDanAmt() {
		return danAmt;
	}

	/**
	 * @param danAmt the danAmt to set
	 */
	public void setDanAmt(BigDecimal danAmt) {
		this.danAmt = danAmt;
	}


}
