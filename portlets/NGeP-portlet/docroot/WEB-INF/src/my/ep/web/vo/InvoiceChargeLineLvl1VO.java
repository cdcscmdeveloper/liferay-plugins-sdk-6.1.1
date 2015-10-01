/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.math.BigDecimal;


/**
 * ChargeLineVO
 * 
 * @author KK LIM
 * @since 11/8/2015
 *
 */
public class InvoiceChargeLineLvl1VO  {
	
	
	private String itemCode;
    private String itemName;
    private String itemDesc;
    private String uom;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal gstAmount = BigDecimal.ZERO;
    private BigDecimal gstUnitPrice = BigDecimal.ZERO;
    private BigDecimal orderedQty = BigDecimal.ZERO;
    private BigDecimal orderedAmt = BigDecimal.ZERO;
    private BigDecimal receivedQty = BigDecimal.ZERO;
    private BigDecimal receivedAmt = BigDecimal.ZERO;
    private BigDecimal recalledAmt = BigDecimal.ZERO;
    private BigDecimal recalledQty = BigDecimal.ZERO;
    private BigDecimal invoiceAmt = BigDecimal.ZERO;
    private boolean lvl1;
    private boolean lvl2;
    private boolean lvl3;
    private String uomCode;
    
    public InvoiceChargeLineLvl1VO(){
    	
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
	 * @return the gstAmount
	 */
	public BigDecimal getGstAmount() {
		return gstAmount;
	}


	/**
	 * @param gstAmount the gstAmount to set
	 */
	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}


	/**
	 * @return the gstUnitPrice
	 */
	public BigDecimal getGstUnitPrice() {
		return gstUnitPrice;
	}


	/**
	 * @param gstUnitPrice the gstUnitPrice to set
	 */
	public void setGstUnitPrice(BigDecimal gstUnitPrice) {
		this.gstUnitPrice = gstUnitPrice;
	}


	/**
	 * @return the orderedQty
	 */
	public BigDecimal getOrderedQty() {
		return orderedQty;
	}


	/**
	 * @param orderedQty the orderedQty to set
	 */
	public void setOrderedQty(BigDecimal orderedQty) {
		this.orderedQty = orderedQty;
	}


	/**
	 * @return the receivedQty
	 */
	public BigDecimal getReceivedQty() {
		return receivedQty;
	}


	/**
	 * @param receivedQty the receivedQty to set
	 */
	public void setReceivedQty(BigDecimal receivedQty) {
		this.receivedQty = receivedQty;
	}


	/**
	 * @return the receivedAmt
	 */
	public BigDecimal getReceivedAmt() {
		return receivedAmt;
	}


	/**
	 * @param receivedAmt the receivedAmt to set
	 */
	public void setReceivedAmt(BigDecimal receivedAmt) {
		this.receivedAmt = receivedAmt;
	}


	/**
	 * @return the recalledAmt
	 */
	public BigDecimal getRecalledAmt() {
		return recalledAmt;
	}


	/**
	 * @param recalledAmt the recalledAmt to set
	 */
	public void setRecalledAmt(BigDecimal recalledAmt) {
		this.recalledAmt = recalledAmt;
	}


	/**
	 * @return the recalledQty
	 */
	public BigDecimal getRecalledQty() {
		return recalledQty;
	}


	/**
	 * @param recalledQty the recalledQty to set
	 */
	public void setRecalledQty(BigDecimal recalledQty) {
		this.recalledQty = recalledQty;
	}


	/**
	 * @return the lvl1
	 */
	public boolean isLvl1() {
		return lvl1;
	}


	/**
	 * @param lvl1 the lvl1 to set
	 */
	public void setLvl1(boolean lvl1) {
		this.lvl1 = lvl1;
	}


	/**
	 * @return the lvl2
	 */
	public boolean isLvl2() {
		return lvl2;
	}


	/**
	 * @param lvl2 the lvl2 to set
	 */
	public void setLvl2(boolean lvl2) {
		this.lvl2 = lvl2;
	}


	/**
	 * @return the lvl3
	 */
	public boolean isLvl3() {
		return lvl3;
	}


	/**
	 * @param lvl3 the lvl3 to set
	 */
	public void setLvl3(boolean lvl3) {
		this.lvl3 = lvl3;
	}
	/**
	 * @return the uomCode
	 */
	public String getUomCode() {
		return uomCode;
	}
	/**
	 * @param uomCode the uomCode to set
	 */
	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
}
