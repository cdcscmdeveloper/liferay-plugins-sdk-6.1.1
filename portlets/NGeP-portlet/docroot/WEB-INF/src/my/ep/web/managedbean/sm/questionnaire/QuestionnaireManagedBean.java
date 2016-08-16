package my.ep.web.managedbean.sm.questionnaire;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmQuestion;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierAnswer;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="questionnaireMB")
@SessionScoped
public class QuestionnaireManagedBean extends BaseManagedBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<SmQuestion> smQuestionLs;
	private SmSupplier smSupp;
	private List<SmSupplierAnswer> smSuppAnswerLs;
	private SmMofAccount smMofAccount;

	private String mofCertSerialNo;
	private String mofAccountNo;
	private String effDate;
	private String expDate;
	private String regDate;
	private String targetUrl;
	private SmAppl smAppl;
	private SmAppl rejectedAppl;
	private boolean resetLatestResubmitAppl;
	
	public List<SmQuestion> getSmQuestionLs() {
		return smQuestionLs;
	}

	public void setSmQuestionLs(List<SmQuestion> smQuestionLs) {
		this.smQuestionLs = smQuestionLs;
	}

	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public List<SmSupplierAnswer> getSmSuppAnswerLs() {
		return smSuppAnswerLs;
	}

	public void setSmSuppAnswerLs(List<SmSupplierAnswer> smSuppAnswerLs) {
		this.smSuppAnswerLs = smSuppAnswerLs;
	}

	public String getMofCertSerialNo() {
		return mofCertSerialNo;
	}

	public void setMofCertSerialNo(String mofCertSerialNo) {
		this.mofCertSerialNo = mofCertSerialNo;
	}

	public String getMofAccountNo() {
		return mofAccountNo;
	}

	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	public String getEffDate() {
		return effDate;
	}

	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	/**
	 * @return the smMofAccount
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * @param smMofAccount the smMofAccount to set
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * @return the targetUrl
	 */
	public String getTargetUrl() {
		return targetUrl;
	}

	/**
	 * @param targetUrl the targetUrl to set
	 */
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
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

	public SmAppl getRejectedAppl() {
		return rejectedAppl;
	}

	public void setRejectedAppl(SmAppl rejectedAppl) {
		this.rejectedAppl = rejectedAppl;
	}

	public boolean isResetLatestResubmitAppl() {
		return resetLatestResubmitAppl;
	}

	public void setResetLatestResubmitAppl(boolean resetLatestResubmitAppl) {
		this.resetLatestResubmitAppl = resetLatestResubmitAppl;
	}
}
