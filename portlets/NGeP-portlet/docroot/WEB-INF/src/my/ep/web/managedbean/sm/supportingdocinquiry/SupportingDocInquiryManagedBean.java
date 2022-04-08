package my.ep.web.managedbean.sm.supportingdocinquiry;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
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
	//#11641 - START
	private SmApplSupportDoc supportDoc;
	private SmApplDocFile smApplDocFile;
	private int attachedFileIndex;
	private List<SmApplDocFile> addedDocFile;
	private List<SmApplSupportDoc> updatedSupportDocs;
	private List<SmApplDocFile> emptyToRemove;
	//#11641 - End
	
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
	//#11641 - Start
	public SmApplSupportDoc getSupportDoc() {
		return supportDoc;
	}

	public void setSupportDoc(SmApplSupportDoc supportDoc) {
		this.supportDoc = supportDoc;
	}

	public SmApplDocFile getSmApplDocFile() {
		return smApplDocFile;
	}

	public void setSmApplDocFile(SmApplDocFile smApplDocFile) {
		this.smApplDocFile = smApplDocFile;
	}
	
	public int getAttachedFileIndex() {
		return attachedFileIndex;
	}
	
	public void setAttachedFileIndex(int attachedFileIndex) {
		this.attachedFileIndex = attachedFileIndex;
	}

	public List<SmApplDocFile> getAddedDocFile() {
		return addedDocFile;
	}

	public void setAddedDocFile(List<SmApplDocFile> addedDocFile) {
		this.addedDocFile = addedDocFile;
	}

	public List<SmApplSupportDoc> getUpdatedSupportDocs() {
		return updatedSupportDocs;
	}

	public void setUpdatedSupportDocs(List<SmApplSupportDoc> updatedSupportDocs) {
		this.updatedSupportDocs = updatedSupportDocs;
	}

	public List<SmApplDocFile> getEmptyToRemove() {
		return emptyToRemove;
	}

	public void setEmptyToRemove(List<SmApplDocFile> emptyToRemove) {
		this.emptyToRemove = emptyToRemove;
	}
	//#11641 - End
	
}
