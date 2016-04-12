/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.bean.sc.quatationtender;

import java.util.Date;

/**
 * The Market Research Search Bean
 * 
 * @author Ken
 * @since 11.04.2016
 */
public class MarketResearchSearchBean {

	private String procurementTitle;
	private String itemName;
	private String spec;
	private String loaNo;
	private String selectedProcurementMethod;
	private Date loaAckDateFrom;
	private Date loaAckDateTo;
	
	public MarketResearchSearchBean(){
		
	}

	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle() {
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle) {
		this.procurementTitle = procurementTitle;
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
	 * @return the spec
	 */
	public String getSpec() {
		return spec;
	}

	/**
	 * @param spec the spec to set
	 */
	public void setSpec(String spec) {
		this.spec = spec;
	}

	/**
	 * @return the loaNo
	 */
	public String getLoaNo() {
		return loaNo;
	}

	/**
	 * @param loaNo the loaNo to set
	 */
	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}

	/**
	 * @return the selectedProcurementMethod
	 */
	public String getSelectedProcurementMethod() {
		return selectedProcurementMethod;
	}

	/**
	 * @param selectedProcurementMethod the selectedProcurementMethod to set
	 */
	public void setSelectedProcurementMethod(String selectedProcurementMethod) {
		this.selectedProcurementMethod = selectedProcurementMethod;
	}

	/**
	 * @return the loaAckDateFrom
	 */
	public Date getLoaAckDateFrom() {
		return loaAckDateFrom;
	}

	/**
	 * @param loaAckDateFrom the loaAckDateFrom to set
	 */
	public void setLoaAckDateFrom(Date loaAckDateFrom) {
		this.loaAckDateFrom = loaAckDateFrom;
	}

	/**
	 * @return the loaAckDateTo
	 */
	public Date getLoaAckDateTo() {
		return loaAckDateTo;
	}

	/**
	 * @param loaAckDateTo the loaAckDateTo to set
	 */
	public void setLoaAckDateTo(Date loaAckDateTo) {
		this.loaAckDateTo = loaAckDateTo;
	}
	
	
}
