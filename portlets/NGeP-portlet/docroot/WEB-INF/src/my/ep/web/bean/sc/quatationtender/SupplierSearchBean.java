package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScQtCategory;

/**
 * @author Viswanath
 * 
 */
public class SupplierSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ScQtCategory> scQtCategoryList;
	private String supplierName;
	private String mofAccountNo;
	private BigDecimal minPaidUpCapital;

	public SupplierSearchBean() {

	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getMofAccountNo() {
		return mofAccountNo;
	}

	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	public BigDecimal getMinPaidUpCapital() {
		return minPaidUpCapital;
	}

	public void setMinPaidUpCapital(BigDecimal minPaidUpCapital) {
		this.minPaidUpCapital = minPaidUpCapital;
	}

	/**
	 * @return the scQtCategoryList
	 */
	public List<ScQtCategory> getScQtCategoryList() {
		return scQtCategoryList;
	}

	/**
	 * @param scQtCategoryList the scQtCategoryList to set
	 */
	public void setScQtCategoryList(List<ScQtCategory> scQtCategoryList) {
		this.scQtCategoryList = scQtCategoryList;
	}

}
