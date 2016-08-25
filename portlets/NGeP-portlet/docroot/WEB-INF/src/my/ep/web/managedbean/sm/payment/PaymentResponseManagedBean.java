package my.ep.web.managedbean.sm.payment;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean(name = "paymentResponseMB")
@SessionScoped
public class PaymentResponseManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private Long paymentId;
	private boolean paymentStatus;
	private String billTypeLbl;
	private String messageId;
	private Severity messageSeverity;
	private String errDesc;
	
	public Long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
	public boolean isPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getBillTypeLbl() {
		return billTypeLbl;
	}
	public void setBillTypeLbl(String billTypeLbl) {
		this.billTypeLbl = billTypeLbl;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Severity getMessageSeverity() {
		return messageSeverity;
	}
	public void setMessageSeverity(Severity messageSeverity) {
		this.messageSeverity = messageSeverity;
	}
	public String getErrDesc() {
		return errDesc;
	}
	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

}
