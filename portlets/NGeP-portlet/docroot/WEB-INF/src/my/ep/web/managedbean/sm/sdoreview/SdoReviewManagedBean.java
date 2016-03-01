package my.ep.web.managedbean.sm.sdoreview;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "sdoReviewMB")
@SessionScoped
public class SdoReviewManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private SmAppl smAppl;
	private SmAddress companyAdd;
	private long appId;
	private String[] bpmAction;
	private String taskId;
	private String taskName;
	private Object bpmPojo;
	private SmRemark smRemark;
	private SmPersonnel smPersonnel;
	private SmApplInquiry smApplInquiry;
	private SmApplInquiry selectedInquiryResponse;
	private List<PmParameterDesc> businessTypeLs;
	private List<SmApplSupportDoc> unselectedSmApplSuppDocs;
	private String zoneCode;
	private Map<Long,SmApplSupportDoc> supportingDocMap;
	private SmGstSupplier smGstSupplier;
	private boolean claimTask;

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public String[] getBpmAction() {
		return bpmAction;
	}

	public void setBpmAction(String[] bpmAction) {
		this.bpmAction = bpmAction;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Object getBpmPojo() {
		return bpmPojo;
	}

	public void setBpmPojo(Object bpmPojo) {
		this.bpmPojo = bpmPojo;
	}

	public SmAddress getCompanyAdd() {
		return companyAdd;
	}

	public void setCompanyAdd(SmAddress companyAdd) {
		this.companyAdd = companyAdd;
	}

	public SmRemark getSmRemark() {
		return smRemark;
	}

	public void setSmRemark(SmRemark smRemark) {
		this.smRemark = smRemark;
	}

	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	public SmApplInquiry getSmApplInquiry() {
		return smApplInquiry;
	}

	public void setSmApplInquiry(SmApplInquiry smApplInquiry) {
		this.smApplInquiry = smApplInquiry;
	}

	public SmApplInquiry getSelectedInquiryResponse() {
		return selectedInquiryResponse;
	}

	public void setSelectedInquiryResponse(SmApplInquiry selectedInquiryResponse) {
		this.selectedInquiryResponse = selectedInquiryResponse;
	}

	public List<PmParameterDesc> getBusinessTypeLs() {
		return businessTypeLs;
	}

	public void setBusinessTypeLs(List<PmParameterDesc> businessTypeLs) {
		this.businessTypeLs = businessTypeLs;
	}

	public List<SmApplSupportDoc> getUnselectedSmApplSuppDocs() {
		return unselectedSmApplSuppDocs;
	}

	public void setUnselectedSmApplSuppDocs(
			List<SmApplSupportDoc> unselectedSmApplSuppDocs) {
		this.unselectedSmApplSuppDocs = unselectedSmApplSuppDocs;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public Map<Long, SmApplSupportDoc> getSupportingDocMap() {
		return supportingDocMap;
	}

	public void setSupportingDocMap(Map<Long, SmApplSupportDoc> supportingDocMap) {
		this.supportingDocMap = supportingDocMap;
	}

	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	public boolean isClaimTask() {
		return claimTask;
	}

	public void setClaimTask(boolean claimTask) {
		this.claimTask = claimTask;
	}

	
}
