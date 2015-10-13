package my.ep.web.managedbean.sm.supportingdocinquiry;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;

@ManagedBean(name = "supportingDocInquiryManagedBean")
@SessionScoped
public class SupportingDocInquiryManagedBean extends ViewApplHistoryManagedBean {

	private static final long serialVersionUID = 1L;
	
	private SmApplInquiry smApplInquiry;
	private Long appId;
	private Object bpmPojo;
	private String[] bpmAction;
	private List<SmAppl> applList;
	private List<Object[]> expiredSdoVerifySuppDocApplList;
	private boolean allowedToSubmit;
	
	private Date inquiryStartDate;
	private Date inquiryEndDate;

	public Object getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(Object bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	public SmApplInquiry getSmApplInquiry() {
		return smApplInquiry;
	}

	public void setSmApplInquiry(SmApplInquiry smApplInquiry) {
		this.smApplInquiry = smApplInquiry;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public List<SmAppl> getApplList() {
		return applList;
	}

	public void setApplList(List<SmAppl> applList) {
		this.applList = applList;
	}

	public boolean isAllowedToSubmit() {
		return allowedToSubmit;
	}

	public void setAllowedToSubmit(boolean allowedToSubmit) {
		this.allowedToSubmit = allowedToSubmit;
	}

	public List<Object[]> getExpiredSdoVerifySuppDocApplList() {
		return expiredSdoVerifySuppDocApplList;
	}

	public void setExpiredSdoVerifySuppDocApplList(
			List<Object[]> expiredSdoVerifySuppDocApplList) {
		this.expiredSdoVerifySuppDocApplList = expiredSdoVerifySuppDocApplList;
	}

	public Date getInquiryEndDate() {
		return inquiryEndDate;
	}

	public void setInquiryEndDate(Date inquiryEndDate) {
		this.inquiryEndDate = inquiryEndDate;
	}

	public Date getInquiryStartDate() {
		return inquiryStartDate;
	}

	public void setInquiryStartDate(Date inquiryStartDate) {
		this.inquiryStartDate = inquiryStartDate;
	}

}
