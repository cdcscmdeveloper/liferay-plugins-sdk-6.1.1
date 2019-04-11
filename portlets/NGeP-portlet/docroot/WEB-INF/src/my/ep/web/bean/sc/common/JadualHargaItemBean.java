/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.bean.sc.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JadualHargaItemBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private boolean isZonal;
	private String item;
	private String zoneName;
	private List<ItemBean> productItemBeans;
	private List<ItemBean> serviceItemBeans;
	private List<Long> zoneIds = new ArrayList<>();
	/**
	 * @return the isZonal
	 */
	public boolean isZonal() {
		return isZonal;
	}
	/**
	 * @param isZonal the isZonal to set
	 */
	public void setZonal(boolean isZonal) {
		this.isZonal = isZonal;
	}
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
	 * @return the productItemBeans
	 */
	public List<ItemBean> getProductItemBeans() {
		return productItemBeans;
	}
	/**
	 * @param productItemBeans the productItemBeans to set
	 */
	public void setProductItemBeans(List<ItemBean> productItemBeans) {
		this.productItemBeans = productItemBeans;
	}
	/**
	 * @return the serviceItemBeans
	 */
	public List<ItemBean> getServiceItemBeans() {
		return serviceItemBeans;
	}
	/**
	 * @param serviceItemBeans the serviceItemBeans to set
	 */
	public void setServiceItemBeans(List<ItemBean> serviceItemBeans) {
		this.serviceItemBeans = serviceItemBeans;
	}
	/**
	 * @return the zoneIds
	 */
	public List<Long> getZoneIds() {
		return zoneIds;
	}
	/**
	 * @param zoneIds the zoneIds to set
	 */
	public void setZoneIds(List<Long> zoneIds) {
		this.zoneIds = zoneIds;
	}
	
}
