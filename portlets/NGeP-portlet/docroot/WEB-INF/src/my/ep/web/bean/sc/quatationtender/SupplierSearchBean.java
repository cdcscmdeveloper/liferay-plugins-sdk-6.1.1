/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.type.aut.SearchOperator;

/**
 * The Class SupplierSearchBean.
 *
 * @author Viswanath
 */
public class SupplierSearchBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The sc qt category list. */
	private List<ScQtCategory> scQtCategoryList;
	
	/** The supplier name. */
	private String supplierName;
	
	/** The mof account no. */
	private String mofAccountNo;
	
	/** The min paid up capital. */
	private BigDecimal minPaidUpCapital;
	
	/** The search operator. */
	private int searchOperator = SearchOperator.OPERATOR_EQUALS.value();

	/**
	 * Instantiates a new supplier search bean.
	 */
	public SupplierSearchBean() {

	}
	
	/**
	 * Instantiates a new supplier search bean.
	 *
	 * @param searchOperator the search operator
	 * @param searchField the search field
	 */
	public SupplierSearchBean(int searchOperator, String searchField) {
		this.searchOperator = searchOperator;
		this.supplierName = searchField;
		this.mofAccountNo = searchField;
	}
	
	/**
	 * Instantiates a new supplier search bean.
	 *
	 * @param searchOperator the search operator
	 * @param searchField the search field
	 */
	public SupplierSearchBean(int searchOperator, BigDecimal searchField) {
		this.searchOperator = searchOperator;
		this.minPaidUpCapital = searchField;
	}

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
	 * Gets the mof account no.
	 *
	 * @return the mof account no
	 */
	public String getMofAccountNo() {
		return mofAccountNo;
	}

	/**
	 * Sets the mof account no.
	 *
	 * @param mofAccountNo the new mof account no
	 */
	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	/**
	 * Gets the min paid up capital.
	 *
	 * @return the min paid up capital
	 */
	public BigDecimal getMinPaidUpCapital() {
		return minPaidUpCapital;
	}

	/**
	 * Sets the min paid up capital.
	 *
	 * @param minPaidUpCapital the new min paid up capital
	 */
	public void setMinPaidUpCapital(BigDecimal minPaidUpCapital) {
		this.minPaidUpCapital = minPaidUpCapital;
	}

	/**
	 * Gets the sc qt category list.
	 *
	 * @return the sc qt category list
	 */
	public List<ScQtCategory> getScQtCategoryList() {
		return scQtCategoryList;
	}

	/**
	 * Sets the sc qt category list.
	 *
	 * @param scQtCategoryList the new sc qt category list
	 */
	public void setScQtCategoryList(List<ScQtCategory> scQtCategoryList) {
		this.scQtCategoryList = scQtCategoryList;
	}

	/**
	 * Gets the search operator.
	 *
	 * @return the search operator
	 */
	public int getSearchOperator() {
		return searchOperator;
	}

	/**
	 * Sets the search operator.
	 *
	 * @param searchOperator the new search operator
	 */
	public void setSearchOperator(int searchOperator) {
		this.searchOperator = searchOperator;
	}

}
