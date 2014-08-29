package my.ep.web.managedbean.sc.supplierfinalization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author Faradiana
 * Supplier Finalization Managed Bean
 */
@ManagedBean(name = "suppFinalBiddingManagedBean")
@SessionScoped
public class SupplierFinalizationBiddingManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private int onlineBiddingActiveIndex;
	
	//Header
	private ScQt scQt;
	private ScQtFinalization scQtFinalization;
		
	//Online Bidding Endorsement
	private List<ScQtCommitteeActivity> committeeEndorsementList;
	private List<ScQtCommitteeActivity> reminderList;
	
	private String subject;
	private String message;
	private String pinNo;
	
	/**
	 * @return the onlineBiddingActiveIndex
	 */
	public int getOnlineBiddingActiveIndex() {
		return onlineBiddingActiveIndex;
	}
	/**
	 * @param onlineBiddingActiveIndex the onlineBiddingActiveIndex to set
	 */
	public void setOnlineBiddingActiveIndex(int onlineBiddingActiveIndex) {
		this.onlineBiddingActiveIndex = onlineBiddingActiveIndex;
	}
	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}
	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}
	/**
	 * @return the scQtFinalization
	 */
	public ScQtFinalization getScQtFinalization() {
		return scQtFinalization;
	}
	/**
	 * @param scQtFinalization the scQtFinalization to set
	 */
	public void setScQtFinalization(ScQtFinalization scQtFinalization) {
		this.scQtFinalization = scQtFinalization;
	}
	/**
	 * @return the committeeEndorsementList
	 */
	public List<ScQtCommitteeActivity> getCommitteeEndorsementList() {
		return committeeEndorsementList;
	}
	/**
	 * @param committeeEndorsementList the committeeEndorsementList to set
	 */
	public void setCommitteeEndorsementList(
			List<ScQtCommitteeActivity> committeeEndorsementList) {
		this.committeeEndorsementList = committeeEndorsementList;
	}
	/**
	 * @return the reminderList
	 */
	public List<ScQtCommitteeActivity> getReminderList() {
		return reminderList;
	}
	/**
	 * @param reminderList the reminderList to set
	 */
	public void setReminderList(List<ScQtCommitteeActivity> reminderList) {
		this.reminderList = reminderList;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the pinNo
	 */
	public String getPinNo() {
		return pinNo;
	}
	/**
	 * @param pinNo the pinNo to set
	 */
	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}
	
	

	

}
