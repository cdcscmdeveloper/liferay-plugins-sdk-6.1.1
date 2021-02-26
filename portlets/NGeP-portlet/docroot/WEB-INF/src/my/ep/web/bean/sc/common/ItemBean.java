/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.bean.sc.common;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class ItemBean.
 * 
 * @author Sri
 */
public class ItemBean {
	private String item;
	private String quantity;
	private String fromQty;
	private String toQty;
	private String zoneName;
	private boolean tier;
	private boolean zonal;
	private List<String> specs;
	//26226 Start
	private String clDurationMonth;
	private String clDeliveryTerm;
   //End
	
	//#28784
	private boolean itemReqDemo;
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
	 * @return the zoneName
	 */	
	public String getZoneName() {
		return zoneName;
	}
	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	/**
	 * @return the fromQty
	 */
	public String getFromQty() {
		return fromQty;
	}
	/**
	 * @param fromQty the fromQty to set
	 */
	public void setFromQty(String fromQty) {
		this.fromQty = fromQty;
	}
	/**
	 * @return the toQty
	 */
	public String getToQty() {
		return toQty;
	}
	/**
	 * @param toQty the toQty to set
	 */
	public void setToQty(String toQty) {
		this.toQty = toQty;
	}
	/**
	 * @return the tier
	 */
	public boolean isTier() {
		return tier;
	}
	/**
	 * @param tier the tier to set
	 */
	public void setTier(boolean tier) {
		this.tier = tier;
	}
	/**
	 * @return the zonal
	 */
	public boolean isZonal() {
		return zonal;
	}
	/**
	 * @param zonal the zonal to set
	 */
	public void setZonal(boolean zonal) {
		this.zonal = zonal;
	}
	/**
	 * @return the specs
	 */
	public List<String> getSpecs() {
		return specs;
	}
	/**
	 * @param specs the specs to set
	 */
	public void setSpecs(List<String> specs) {
		this.specs = specs;
	}
	//26226 Start
	public String getClDurationMonth() {
		return clDurationMonth;
	}
	public void setClDurationMonth(String clDurationMonth) {
		this.clDurationMonth = clDurationMonth;
	}
	public String getClDeliveryTerm() {
		return clDeliveryTerm;
	}
	public void setClDeliveryTerm(String clDeliveryTerm) {
		this.clDeliveryTerm = clDeliveryTerm;
	}
	//End
	
	//#28784
	public boolean isItemReqDemo() {
		return itemReqDemo;
	}
	public void setItemReqDemo(boolean itemReqDemo) {
		this.itemReqDemo = itemReqDemo;
	}
	
}
