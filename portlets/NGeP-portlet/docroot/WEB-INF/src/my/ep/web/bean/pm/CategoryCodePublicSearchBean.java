/* 
 ***************************************************
 *    
 * Copyright © 2015 Archisoft. All rights reserved.
 * 
 ***************************************************
*/
package my.ep.web.bean.pm;

/**
 * Category Code Search Bean for public search
 * 
 * @author Ken Wong
 * @since 7/12/2015
 *
 */
public class CategoryCodePublicSearchBean {
	
	private String catDesc;
	private Long selectedSupplierType;
	private Long selectedCompanyType;
	private Integer selectedBumiPercentage;
	private Integer paidUpCapital;
	private Integer homeOperated;
	
	public CategoryCodePublicSearchBean(){}

	/**
	 * @return the catDesc
	 */
	public String getCatDesc() {
		return catDesc;
	}

	/**
	 * @param catDesc the catDesc to set
	 */
	public void setCatDesc(String catDesc) {
		this.catDesc = catDesc;
	}

	/**
	 * @return the selectedSupplierType
	 */
	public Long getSelectedSupplierType() {
		return selectedSupplierType;
	}

	/**
	 * @param selectedSupplierType the selectedSupplierType to set
	 */
	public void setSelectedSupplierType(Long selectedSupplierType) {
		this.selectedSupplierType = selectedSupplierType;
	}

	/**
	 * @return the selectedCompanyType
	 */
	public Long getSelectedCompanyType() {
		return selectedCompanyType;
	}

	/**
	 * @param selectedCompanyType the selectedCompanyType to set
	 */
	public void setSelectedCompanyType(Long selectedCompanyType) {
		this.selectedCompanyType = selectedCompanyType;
	}

	/**
	 * @return the selectedBumiPercentage
	 */
	public Integer getSelectedBumiPercentage() {
		return selectedBumiPercentage;
	}

	/**
	 * @param selectedBumiPercentage the selectedBumiPercentage to set
	 */
	public void setSelectedBumiPercentage(Integer selectedBumiPercentage) {
		this.selectedBumiPercentage = selectedBumiPercentage;
	}

	/**
	 * @return the paidUpCapital
	 */
	public Integer getPaidUpCapital() {
		return paidUpCapital;
	}

	/**
	 * @param paidUpCapital the paidUpCapital to set
	 */
	public void setPaidUpCapital(Integer paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	/**
	 * @return the homeOperated
	 */
	public Integer getHomeOperated() {
		return homeOperated;
	}

	/**
	 * @param homeOperated the homeOperated to set
	 */
	public void setHomeOperated(Integer homeOperated) {
		this.homeOperated = homeOperated;
	}
	
	
}
