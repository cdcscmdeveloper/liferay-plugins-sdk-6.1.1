package my.ep.web.managedbean.report.sm;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "smeSupplierRptMB")
@SessionScoped
public class SmeSupplierRptManagedBean extends SmReportManagedBean {
	private static final long serialVersionUID = 1L;

	private String accountType;
	private String smeStatusType;
	private List<SelectItem> statusTypeList;
	private String smeCorpRegistered;
	private Date dateStartFrom;
	private Date dateStartTo;
	private Date dateEndFrom;
	private Date dateEndTo;

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getSmeStatusType() {
		return smeStatusType;
	}

	public void setSmeStatusType(String smeStatusType) {
		this.smeStatusType = smeStatusType;
	}

	public List<SelectItem> getStatusTypeList() {
		return statusTypeList;
	}

	public void setStatusTypeList(List<SelectItem> statusTypeList) {
		this.statusTypeList = statusTypeList;
	}

	public String getSmeCorpRegistered() {
		return smeCorpRegistered;
	}

	public void setSmeCorpRegistered(String smeCorpRegistered) {
		this.smeCorpRegistered = smeCorpRegistered;
	}

	public Date getDateStartFrom() {
		return dateStartFrom;
	}

	public void setDateStartFrom(Date dateStartFrom) {
		this.dateStartFrom = dateStartFrom;
	}

	public Date getDateEndFrom() {
		return dateEndFrom;
	}

	public void setDateEndFrom(Date dateEndFrom) {
		this.dateEndFrom = dateEndFrom;
	}

	public Date getDateStartTo() {
		return dateStartTo;
	}

	public void setDateStartTo(Date dateStartTo) {
		this.dateStartTo = dateStartTo;
	}

	public Date getDateEndTo() {
		return dateEndTo;
	}

	public void setDateEndTo(Date dateEndTo) {
		this.dateEndTo = dateEndTo;
	}

}