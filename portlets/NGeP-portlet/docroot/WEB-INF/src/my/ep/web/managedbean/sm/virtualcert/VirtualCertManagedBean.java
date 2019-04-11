package my.ep.web.managedbean.sm.virtualcert;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean(name="virtualCertMB")
@SessionScoped
public class VirtualCertManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private SmSupplier smSupplier;
	private String mofCertSerialNo;
	private String mofAccountNo;
	private String effDate;
	private String expDate;
	private List<SmMofAccount> mofAccountLs;
	private List<SmMofCert> mofCertLs;

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the mofCertSerialNo
	 */
	public String getMofCertSerialNo() {
		return mofCertSerialNo;
	}

	/**
	 * @param mofCertSerialNo the mofCertSerialNo to set
	 */
	public void setMofCertSerialNo(String mofCertSerialNo) {
		this.mofCertSerialNo = mofCertSerialNo;
	}

	/**
	 * @return the mofAccountNo
	 */
	public String getMofAccountNo() {
		return mofAccountNo;
	}

	/**
	 * @param mofAccountNo the mofAccountNo to set
	 */
	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	/**
	 * @return the effDate
	 */
	public String getEffDate() {
		return effDate;
	}

	/**
	 * @param effDate the effDate to set
	 */
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	/**
	 * @return the expDate
	 */
	public String getExpDate() {
		return expDate;
	}

	/**
	 * @param expDate the expDate to set
	 */
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	/**
	 * @return the mofAccountLs
	 */
	public List<SmMofAccount> getMofAccountLs() {
		return mofAccountLs;
	}

	/**
	 * @param mofAccountLs the mofAccountLs to set
	 */
	public void setMofAccountLs(List<SmMofAccount> mofAccountLs) {
		this.mofAccountLs = mofAccountLs;
	}

	public List<SmMofCert> getMofCertLs() {
		return mofCertLs;
	}

	public void setMofCertLs(List<SmMofCert> mofCertLs) {
		this.mofCertLs = mofCertLs;
	}	
	
}