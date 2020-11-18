package my.ep.web.managedbean.sm.cancelregistration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "cancelMB")
@SessionScoped
public class CancelRegistartionManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String accType;
	private String businessRegNo;
	private String companyName;
	private String suppAdminIdentificationNo;
	
	private String cancellationReason;
	private SmSupplier smSupplier;
	private SmAppl smAppl;
	private String confirmationMsg;

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getBusinessRegNo() {
		return businessRegNo;
	}

	public void setBusinessRegNo(String businessRegNo) {
		this.businessRegNo = businessRegNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSuppAdminIdentificationNo() {
		return suppAdminIdentificationNo;
	}

	public void setSuppAdminIdentificationNo(String suppAdminIdentificationNo) {
		this.suppAdminIdentificationNo = suppAdminIdentificationNo;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public String getConfirmationMsg() {
		return confirmationMsg;
	}

	public void setConfirmationMsg(String confirmationMsg) {
		this.confirmationMsg = confirmationMsg;
	}

}
