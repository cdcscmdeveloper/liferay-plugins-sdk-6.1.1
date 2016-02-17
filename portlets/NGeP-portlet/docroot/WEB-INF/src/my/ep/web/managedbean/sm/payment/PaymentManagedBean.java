package my.ep.web.managedbean.sm.payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.py.PyBill;
import my.ep.persistence.entity.py.PyBillDtl;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "paymentMB")
@SessionScoped
public class PaymentManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private Long orderId;
	private String billName;
	private String billEmail;
	private String billMobile;
	private String billDesc;
	private String country;
	private SmAppl smAppl;
	private PyBill pyBill;
	private BigDecimal totalAmount;
	private BigDecimal totalAmountBeforeGst;
	private BigDecimal gstAmount;
	private SmPersonnel[] selectedSoftCertLs;
	private List<SmPersonnel> softCertReqPersonnels;
	private PyBillDtl applicationPayment;
	private List<PyBillDtl> newPyBillDtlLs;
	private Map<Long,PyBillDtl> defaultCertPaymentMap;
	private Object bpmPojo;
	private boolean softcertPayment;
	private Long appId;
	private String docNo;
	
	/**
	 * @return the orderId
	 */
	public Long getOrderId() {
		return orderId;
	}
	/**
	 * @return the billName
	 */
	public String getBillName() {
		return billName;
	}
	/**
	 * @return the billEmail
	 */
	public String getBillEmail() {
		return billEmail;
	}
	/**
	 * @return the billMobile
	 */
	public String getBillMobile() {
		return billMobile;
	}
	/**
	 * @return the billDesc
	 */
	public String getBillDesc() {
		return billDesc;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
//	/**
//	 * @param amount the amount to set
//	 */
//	public void setAmount(double amount) {
//		this.amount = amount;
//	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	/**
	 * @param billName the billName to set
	 */
	public void setBillName(String billName) {
		this.billName = billName;
	}
	/**
	 * @param billEmail the billEmail to set
	 */
	public void setBillEmail(String billEmail) {
		this.billEmail = billEmail;
	}
	/**
	 * @param billMobile the billMobile to set
	 */
	public void setBillMobile(String billMobile) {
		this.billMobile = billMobile;
	}
	/**
	 * @param billDesc the billDesc to set
	 */
	public void setBillDesc(String billDesc) {
		this.billDesc = billDesc;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the smAppl
	 */
	public SmAppl getSmAppl() {
		return smAppl;
	}
	/**
	 * @param smAppl the smAppl to set
	 */
	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}
	/**
	 * @return the smBill
	 */
	public PyBill getPyBill() {
		return pyBill;
	}
	/**
	 * @param smBill the smBill to set
	 */
	public void setPyBill(PyBill pyBill) {
		this.pyBill = pyBill;
	}

	/**
	 * @return the selectedSoftCertLs
	 */
	public SmPersonnel[] getSelectedSoftCertLs() {
		return selectedSoftCertLs;
	}
	/**
	 * @param selectedSoftCertLs the selectedSoftCertLs to set
	 */
	public void setSelectedSoftCertLs(SmPersonnel[] selectedSoftCertLs) {
		this.selectedSoftCertLs = selectedSoftCertLs;
	}
	/**
	 * @return the softCertReqPersonnels
	 */
	public List<SmPersonnel> getSoftCertReqPersonnels() {
		return softCertReqPersonnels;
	}
	/**
	 * @param softCertReqPersonnels the softCertReqPersonnels to set
	 */
	public void setSoftCertReqPersonnels(List<SmPersonnel> softCertReqPersonnels) {
		this.softCertReqPersonnels = softCertReqPersonnels;
	}
	/**
	 * @return the applicationPayment
	 */
	public PyBillDtl getApplicationPayment() {
		return applicationPayment;
	}
	/**
	 * @param applicationPayment the applicationPayment to set
	 */
	public void setApplicationPayment(PyBillDtl applicationPayment) {
		this.applicationPayment = applicationPayment;
	}
	/**
	 * @return the newSmBillDtlLs
	 */
	public List<PyBillDtl> getNewPyBillDtlLs() {
		return newPyBillDtlLs;
	}
	/**
	 * @param newSmBillDtlLs the newSmBillDtlLs to set
	 */
	public void setNewPyBillDtlLs(List<PyBillDtl> newPyBillDtlLs) {
		this.newPyBillDtlLs = newPyBillDtlLs;
	}
	/**
	 * @return the defaultCertPaymentMap
	 */
	public Map<Long, PyBillDtl> getDefaultCertPaymentMap() {
		return defaultCertPaymentMap;
	}
	/**
	 * @param defaultCertPaymentMap the defaultCertPaymentMap to set
	 */
	public void setDefaultCertPaymentMap(Map<Long, PyBillDtl> defaultCertPaymentMap) {
		this.defaultCertPaymentMap = defaultCertPaymentMap;
	}
	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
//	/**
//	 * @return the offlinePayment
//	 */
//	public boolean isOfflinePayment() {
//		return offlinePayment;
//	}
//	/**
//	 * @param offlinePayment the offlinePayment to set
//	 */
//	public void setOfflinePayment(boolean offlinePayment) {
//		this.offlinePayment = offlinePayment;
//	}
	/**
	 * @return the applId
	 */
	public Long getAppId() {
		return appId;
	}
	/**
	 * @param applId the applId to set
	 */
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public Object getBpmPojo() {
		return bpmPojo;
	}
	public void setBpmPojo(Object bpmPojo) {
		this.bpmPojo = bpmPojo;
	}
	public boolean isSoftcertPayment() {
		return softcertPayment;
	}
	public void setSoftcertPayment(boolean softcertPayment) {
		this.softcertPayment = softcertPayment;
	}
	public BigDecimal getTotalAmountBeforeGst() {
		return totalAmountBeforeGst;
	}
	public void setTotalAmountBeforeGst(BigDecimal totalAmountBeforeGst) {
		this.totalAmountBeforeGst = totalAmountBeforeGst;
	}
	public BigDecimal getGstAmount() {
		return gstAmount;
	}
	public void setGstAmount(BigDecimal gstAmount) {
		this.gstAmount = gstAmount;
	}
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

}
