package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Viswanath
 * 
 */
public class SupplierSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String supplierName;
	private String mofAccountNo;
	private BigDecimal minPaidUpCapital;;

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

}
