/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.StreamedContent;

/**
 * @author siew mei
 * 
 */
public abstract class BaseManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object token;

	// For BPM task details
	private String taskId;
	private String[] outComes;
	private Long currentStatusId;
	private String taskName;
	private boolean isAssignedState;
	private Object payLoad;
	private String processName;
	private Boolean groupTask;
	private boolean isReadOnly;
	private String currentTaskDocType;// #25037
	private Calendar currentTaskCreatedDate;// #25037
	/*
	 * commonly used as a flag to user's permission to download documents/attachment
	 */
	private Boolean enableDownloadAllDocuments = Boolean.FALSE;
	private List<SelectItem> localAuthorityItems;
	private String captchaText;
	private String captchaAnswer;
	private Set<UserTasks> userTasks;
	private Map<String, List<String>> additionalPages;

	// 16052019 - SSM INTEGRATION CR - Fahmi
	private boolean ssmIntegrationActive;
	private boolean validateWithSSM;
	private boolean companyValidWithSSM;
	private boolean errorMessageSSM;
	private String statusFromSSM;
	private String statusDescFromSSM;
	private SmSupplier smSupplierSsm;
	private SmPersonnel smPersonnelSsm;
	
	//27382
	private boolean errorInvalidSelfRequest;

	private List<SmPersonnel> personnelsRole = new ArrayList<SmPersonnel>();

	// For sub title displayed in screen.
	private String title;

	private boolean disableFields;

	private boolean disciplinaryAction;

	// 18685 - Start.
	private String reportDownloadFileName;
	private ScAttachment scAttachment;
	private List<ScQtEvaluationReport> scQtEvaluationReportList;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private List<ScQtEvaluationReport> selectedScQtEvaluationReports;
	private ScQtEvaluationReport selectedReport;
	private List<ScQtFinalizationReport> evaluationReportList;
	private List<ScQtEvaluationReport> reportsToBeRemovedList;
	private boolean autoUpdate = Boolean.TRUE;
	private List<ScQtFinalizationReport> newBriefingPaperReportList;
	private ScQtFinalizationReport selectedBriefingReport;
	private List<ScQtFinalizationReport> removeFinalizationReports;
	private List<ScQtFinalizationReport> consolidationBriefingPaperReportList;
	private List<ScQtFinalizationReport> sfBriefingPaperReportList;
	private String docNo;
	// 18685 - End.

	// 17906
	private String displayMessageBank;

	// 27951
	private boolean preRegistration = false;
	private SsmResDTO ssmResDTO;
	private boolean checkRoleSSM = false;
	// 27239
	private boolean dontGetSsmData = false;
	// 28454
	private boolean resubmitAppl = false;
	private boolean showPopup = false;

	// #25638 - Start
	private boolean displayReviseInstrn;
	// #25638 - End
	// #25037-start
	private boolean validFor25037;// FLAG TO IDENTIFY SUPPORTED ACTION FOR WIP
									// DATA. PLEASE REFER 25037
									// PMCOMPOSITEVALIDITY
	private boolean committeModified;
	// #25037-end

	// 27346-start
	private SmAppl smAppl;
	private SmSupplier SmSupplier;
	private List<SmPersonnel> personnelsDirector = new ArrayList<SmPersonnel>();
	private List<SmSupplierBranch> smSupplierBranchSsm = new ArrayList<SmSupplierBranch>();
	private List<SmEquityCompany> smEquityCompaniesSsm = new ArrayList<SmEquityCompany>();   
	private boolean disAllowOwnerEdit;
	private String businessTypeParamCode;
	private List<SmPersonnel> personnelsOwner = new ArrayList<SmPersonnel>();
	private List<SmPersonnel> personnelsSsm = new ArrayList<SmPersonnel>();
	private List<SmSupplierBusiness> smSupplierBusinessSsm = new ArrayList<SmSupplierBusiness>();
	private SmCompanyBasic smCompanyBasicSsm = null;
	private boolean showDetailSSM;
	private List<SmPersonnel> smPersonnelDirectorSsm = new ArrayList<SmPersonnel>();
	private List<SmPersonnel> personnelsOwnerSsm = new ArrayList<SmPersonnel>();
	private SmAddress smAddress;

	// 28767
	private List<SmPersonnel> personnelDirectorManager = new ArrayList<SmPersonnel>();
	private List<SmPersonnel> personnelMembers = new ArrayList<SmPersonnel>();
	private BigDecimal totalShare;

	// start #29956
	private String termAndConditionCheckOwner;
	private String termAndConditionCheckOwnerA;
	private String termAndConditionCheckOwnerC;
	private String termAndConditionCheckOwnerChangeRole;
	private Boolean isDowngradeRole;
	// end #29956
	
	//14685 - START
	private boolean showMessage;
	//14685 - END
	
	// 27346
	private StreamedContent downloadfile;
	private String inactiveSCMemberStr; //25037 - note 510

	//#25726 start
	private BigDecimal lowerLimit = new BigDecimal (200000);	
	//#25726 end
	
	//#32236 start
	private String outputPage;
	//#32236 end
	
	private Integer wipStatusMax;//34865,34866,34867 - 29021 WIP
	
	//#28784 - Demo WIP - To display QT BPM version
	private String bpmVersionNo; // #29007 wip
	private boolean newVersion;
	// #28895_35501 - AUDIT TRAIL SM
	private boolean forceReplace;


	public boolean isShowDetailSSM() {
		return showDetailSSM;
	}

	public void setShowDetailSSM(boolean showDetailSSM) {
		this.showDetailSSM = showDetailSSM;
	}

	public List<SmPersonnel> getSmPersonnelDirectorSsm() {
		return smPersonnelDirectorSsm;
	}

	public void setSmPersonnelDirectorSsm(List<SmPersonnel> smPersonnelDirectorSsm) {
		this.smPersonnelDirectorSsm = smPersonnelDirectorSsm;
	}

	public List<SmPersonnel> getPersonnelsOwnerSsm() {
		return personnelsOwnerSsm;
	}

	public void setPersonnelsOwnerSsm(List<SmPersonnel> personnelsOwnerSsm) {
		this.personnelsOwnerSsm = personnelsOwnerSsm;
	}

	public SmAddress getSmAddress() {
		return smAddress;
	}

	public void setSmAddress(SmAddress smAddress) {
		this.smAddress = smAddress;
	}

	public SmCompanyBasic getSmCompanyBasicSsm() {
		return smCompanyBasicSsm;
	}

	public void setSmCompanyBasicSsm(SmCompanyBasic smCompanyBasicSsm) {
		this.smCompanyBasicSsm = smCompanyBasicSsm;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public SmSupplier getSmSupplier() {
		return SmSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		SmSupplier = smSupplier;
	}

	public List<SmPersonnel> getPersonnelsDirector() {
		return personnelsDirector;
	}

	public void setPersonnelsDirector(List<SmPersonnel> personnelsDirector) {
		this.personnelsDirector = personnelsDirector;
	}

	public List<SmSupplierBranch> getSmSupplierBranchSsm() {
		return smSupplierBranchSsm;
	}

	public void setSmSupplierBranchSsm(List<SmSupplierBranch> smSupplierBranchSsm) {
		this.smSupplierBranchSsm = smSupplierBranchSsm;
	}

	public List<SmEquityCompany> getSmEquityCompaniesSsm() {
		return smEquityCompaniesSsm;
	}

	public void setSmEquityCompaniesSsm(List<SmEquityCompany> smEquityCompaniesSsm) {
		this.smEquityCompaniesSsm = smEquityCompaniesSsm;
	}

	public boolean isDisAllowOwnerEdit() {
		return disAllowOwnerEdit;
	}

	public void setDisAllowOwnerEdit(boolean disAllowOwnerEdit) {
		this.disAllowOwnerEdit = disAllowOwnerEdit;
	}

	public String getBusinessTypeParamCode() {
		return businessTypeParamCode;
	}

	public void setBusinessTypeParamCode(String businessTypeParamCode) {
		this.businessTypeParamCode = businessTypeParamCode;
	}

	public List<SmPersonnel> getPersonnelsOwner() {
		return personnelsOwner;
	}

	public void setPersonnelsOwner(List<SmPersonnel> personnelsOwner) {
		this.personnelsOwner = personnelsOwner;
	}

	public List<SmPersonnel> getPersonnelsSsm() {
		return personnelsSsm;
	}

	public void setPersonnelsSsm(List<SmPersonnel> personnelsSsm) {
		this.personnelsSsm = personnelsSsm;
	}

	public List<SmSupplierBusiness> getSmSupplierBusinessSsm() {
		return smSupplierBusinessSsm;
	}

	public void setSmSupplierBusinessSsm(List<SmSupplierBusiness> smSupplierBusinessSsm) {
		this.smSupplierBusinessSsm = smSupplierBusinessSsm;
	}

	// 27346-end

	/**
	 * @return the token
	 */
	public Object getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(Object token) {
		this.token = token;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the outComes
	 */
	public String[] getOutComes() {
		return outComes;
	}

	/**
	 * @param outComes
	 *            the outComes to set
	 */
	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	/**
	 * @return the currentStatusId
	 */
	public Long getCurrentStatusId() {
		return currentStatusId;
	}

	/**
	 * @param currentStatusId
	 *            the currentStatusId to set
	 */
	public void setCurrentStatusId(Long currentStatusId) {
		this.currentStatusId = currentStatusId;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the isAssignedState
	 */
	public boolean isAssignedState() {
		return isAssignedState;
	}

	/**
	 * @param isAssignedState
	 *            the isAssignedState to set
	 */
	public void setAssignedState(boolean isAssignedState) {
		this.isAssignedState = isAssignedState;
	}

	/**
	 * @return the payLoad
	 */
	public Object getPayLoad() {
		return payLoad;
	}

	/**
	 * @param payLoad
	 *            the payLoad to set
	 */
	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName
	 *            the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the groupTask
	 */
	public Boolean getGroupTask() {
		return groupTask;
	}

	/**
	 * @param groupTask
	 *            the groupTask to set
	 */
	public void setGroupTask(Boolean groupTask) {
		this.groupTask = groupTask;
	}

	/**
	 * @return the isReadOnly
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * @param isReadOnly
	 *            the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @return local authority items
	 */
	public List<SelectItem> getLocalAuthorityItems() {
		return localAuthorityItems;
	}

	/**
	 * @param List
	 *            <SelectItem> localAuthorityItems
	 */
	public void setLocalAuthorityItems(List<SelectItem> localAuthorityItems) {
		this.localAuthorityItems = localAuthorityItems;
	}

	/**
	 * Gets the captcha text.
	 * 
	 * @return the captcha text
	 */
	public String getCaptchaText() {
		return captchaText;
	}

	/**
	 * Sets the captcha text.
	 *
	 * @param captchaText the new captcha text
	 */
	public void setCaptchaText(String captchaText) {
		this.captchaText = captchaText;
	}

	/**
	 * Gets the captcha answer.
	 * 
	 * @return the captcha answer
	 */
	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	/**
	 * Sets the captcha answer.
	 *
	 * @param captchaAnswer the new captcha answer
	 */
	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

	public Boolean getEnableDownloadAllDocuments() {
		return enableDownloadAllDocuments;
	}

	public void setEnableDownloadAllDocuments(Boolean enableDownloadAllDocuments) {
		this.enableDownloadAllDocuments = enableDownloadAllDocuments;
	}

	public boolean isSsmIntegrationActive() {
		return ssmIntegrationActive;
	}

	public void setSsmIntegrationActive(boolean ssmIntegrationActive) {
		this.ssmIntegrationActive = ssmIntegrationActive;
	}

	public boolean isValidateWithSSM() {
		return validateWithSSM;
	}

	public void setValidateWithSSM(boolean validateWithSSM) {
		this.validateWithSSM = validateWithSSM;
	}

	public boolean isCompanyValidWithSSM() {
		return companyValidWithSSM;
	}

	public void setCompanyValidWithSSM(boolean companyValidWithSSM) {
		this.companyValidWithSSM = companyValidWithSSM;
	}

	public boolean isErrorMessageSSM() {
		return errorMessageSSM;
	}

	public void setErrorMessageSSM(boolean errorMessageSSM) {
		this.errorMessageSSM = errorMessageSSM;
	}

	public String getStatusFromSSM() {
		return statusFromSSM;
	}

	public void setStatusFromSSM(String statusFromSSM) {
		this.statusFromSSM = statusFromSSM;
	}

	public String getStatusDescFromSSM() {
		return statusDescFromSSM;
	}

	public void setStatusDescFromSSM(String statusDescFromSSM) {
		this.statusDescFromSSM = statusDescFromSSM;
	}

	/**
	 * @return the disableFields
	 */
	public boolean isDisableFields() {
		return disableFields;
	}

	/**
	 * @param disableFields the disableFields to set
	 */
	public void setDisableFields(boolean disableFields) {
		this.disableFields = disableFields;
	}

	public Set<UserTasks> getUserTasks() {
		return userTasks;
	}

	public void setUserTasks(Set<UserTasks> userTasks) {
		this.userTasks = userTasks;
	}

	public void addUserTasks(UserTasks userTask) {
		if (this.userTasks == null) {
			userTasks = new HashSet<UserTasks>();
		}
		userTasks.add(userTask);
	}

	public void removeUserTasks(UserTasks userTask) {
		if (this.userTasks != null && !CollectionUtils.isEmpty(userTasks)) {
			userTasks.remove(userTask);
		}
	}

	public Map<String, List<String>> getAdditionalPages() {
		return additionalPages;
	}

	public void setAdditionalPages(Map<String, List<String>> additionalPages) {
		this.additionalPages = additionalPages;
	}

	public void addAdditionalPages(String k, String page) {
		if (this.additionalPages == null) {
			this.additionalPages = new HashMap<String, List<String>>();
			List<String> t = new ArrayList<String>();
			this.additionalPages.put(k, t);
		}
		if (!this.additionalPages.containsKey(k) || this.additionalPages.get(k) == null) {
			List<String> t = new ArrayList<String>();
			this.additionalPages.put(k, t);
		}
		this.additionalPages.get(k).add(page);
	}

	/**
	 * Copy BPM TaskDetails to another instance of BaseManagedBean
	 * @param bm - another BaseManagedBean instance
	 */
	public void copyBPMTaskDetails(BaseManagedBean bm) {
		bm.setToken(this.token);
		bm.setTaskId(this.taskId);
		bm.setOutComes(this.outComes);
		bm.setCurrentStatusId(this.currentStatusId);
		bm.setTaskName(taskName);
		bm.setAssignedState(this.isAssignedState);
		bm.setPayLoad(this.payLoad);
		bm.setProcessName(this.processName);
		bm.setGroupTask(this.groupTask);
		bm.setReadOnly(this.isReadOnly);
	}

	public SmSupplier getSmSupplierSsm() {
		return smSupplierSsm;
	}

	public void setSmSupplierSsm(SmSupplier smSupplierSsm) {
		this.smSupplierSsm = smSupplierSsm;
	}

	public SmPersonnel getSmPersonnelSsm() {
		return smPersonnelSsm;
	}

	public void setSmPersonnelSsm(SmPersonnel smPersonnelSsm) {
		this.smPersonnelSsm = smPersonnelSsm;
	}

	/**
	 * @return the disciplinaryAction
	 */
	public boolean isDisciplinaryAction() {
		if (!disciplinaryAction) {
			disciplinaryAction = new FacesScopes().getUserSessionBean().isDisciplinaryAction();
		}
		return disciplinaryAction;
	}

	/**
	 * @param disciplinaryAction the disciplinaryAction to set
	 */
	public void setDisciplinaryAction(boolean disciplinaryAction) {
		this.disciplinaryAction = disciplinaryAction;
	}

	public String getReportDownloadFileName() {
		return reportDownloadFileName;
	}

	public void setReportDownloadFileName(String reportDownloadFileName) {
		this.reportDownloadFileName = reportDownloadFileName;
	}

	public ScAttachment getScAttachment() {
		return scAttachment;
	}

	public void setScAttachment(ScAttachment scAttachment) {
		this.scAttachment = scAttachment;
	}

	public List<ScQtEvaluationReport> getScQtEvaluationReportList() {
		return scQtEvaluationReportList;
	}

	public void setScQtEvaluationReportList(List<ScQtEvaluationReport> scQtEvaluationReportList) {
		this.scQtEvaluationReportList = scQtEvaluationReportList;
	}

	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	public List<ScQtEvaluationReport> getSelectedScQtEvaluationReports() {
		return selectedScQtEvaluationReports;
	}

	public void setSelectedScQtEvaluationReports(List<ScQtEvaluationReport> selectedScQtEvaluationReports) {
		this.selectedScQtEvaluationReports = selectedScQtEvaluationReports;
	}

	public ScQtEvaluationReport getSelectedReport() {
		return selectedReport;
	}

	public void setSelectedReport(ScQtEvaluationReport selectedReport) {
		this.selectedReport = selectedReport;
	}

	public List<ScQtFinalizationReport> getEvaluationReportList() {
		return evaluationReportList;
	}

	public void setEvaluationReportList(List<ScQtFinalizationReport> evaluationReportList) {
		this.evaluationReportList = evaluationReportList;
	}

	public List<ScQtEvaluationReport> getReportsToBeRemovedList() {
		return reportsToBeRemovedList;
	}

	public void setReportsToBeRemovedList(List<ScQtEvaluationReport> reportsToBeRemovedList) {
		this.reportsToBeRemovedList = reportsToBeRemovedList;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the newBriefingPaperReportList
	 */
	public List<ScQtFinalizationReport> getNewBriefingPaperReportList() {
		return newBriefingPaperReportList;
	}

	/**
	 * @param newBriefingPaperReportList the newBriefingPaperReportList to set
	 */
	public void setNewBriefingPaperReportList(List<ScQtFinalizationReport> newBriefingPaperReportList) {
		this.newBriefingPaperReportList = newBriefingPaperReportList;
	}

	/**
	 * @return the selectedBriefingReport
	 */
	public ScQtFinalizationReport getSelectedBriefingReport() {
		return selectedBriefingReport;
	}

	/**
	 * @param selectedBriefingReport the selectedBriefingReport to set
	 */
	public void setSelectedBriefingReport(ScQtFinalizationReport selectedBriefingReport) {
		this.selectedBriefingReport = selectedBriefingReport;
	}

	/**
	 * @return the removeFinalizationReports
	 */
	public List<ScQtFinalizationReport> getRemoveFinalizationReports() {
		return removeFinalizationReports;
	}

	/**
	 * @param removeFinalizationReports the removeFinalizationReports to set
	 */
	public void setRemoveFinalizationReports(List<ScQtFinalizationReport> removeFinalizationReports) {
		this.removeFinalizationReports = removeFinalizationReports;
	}

	public List<SmPersonnel> getPersonnelsRole() {
		return personnelsRole;
	}

	public void setPersonnelsRole(List<SmPersonnel> personnelsRole) {
		this.personnelsRole = personnelsRole;
	}

	// #18685 - Start
	/**
	 * @return the consolidationBriefingPaperReportList
	 */
	public List<ScQtFinalizationReport> getConsolidationBriefingPaperReportList() {
		return consolidationBriefingPaperReportList;
	}

	/**
	 * @param consolidationBriefingPaperReportList the consolidationBriefingPaperReportList to set
	 */
	public void setConsolidationBriefingPaperReportList(
			List<ScQtFinalizationReport> consolidationBriefingPaperReportList) {
		this.consolidationBriefingPaperReportList = consolidationBriefingPaperReportList;
	}

	/**
	 * @return the sfBriefingPaperReportList
	 */
	public List<ScQtFinalizationReport> getSfBriefingPaperReportList() {
		return sfBriefingPaperReportList;
	}
	
	/**
	 * @param sfBriefingPaperReportList the sfBriefingPaperReportList to set
	 */
	public void setSfBriefingPaperReportList(
			List<ScQtFinalizationReport> sfBriefingPaperReportList) {
		this.sfBriefingPaperReportList = sfBriefingPaperReportList;
	}
	
	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	// #18685 - End.

	public String getDisplayMessageBank() {
		return displayMessageBank;
	}

	public void setDisplayMessageBank(String displayMessageBank) {
		this.displayMessageBank = displayMessageBank;
	}

	// 27951
	public boolean isPreRegistration() {
		return preRegistration;
	}

	public void setPreRegistration(boolean preRegistration) {
		this.preRegistration = preRegistration;
	}

	public SsmResDTO getSsmResDTO() {
		return ssmResDTO;
	}

	public void setSsmResDTO(SsmResDTO ssmResDTO) {
		this.ssmResDTO = ssmResDTO;
	}

	public boolean isCheckRoleSSM() {
		return checkRoleSSM;
	}

	public void setCheckRoleSSM(boolean checkRoleSSM) {
		this.checkRoleSSM = checkRoleSSM;
	}

	public boolean isDontGetSsmData() {
		return dontGetSsmData;
	}

	public void setDontGetSsmData(boolean dontGetSsmData) {
		this.dontGetSsmData = dontGetSsmData;
	}

	public String getCurrentTaskDocType() {
		return currentTaskDocType;
	}

	public void setCurrentTaskDocType(String currentTaskDocType) {
		this.currentTaskDocType = currentTaskDocType;
	}

	public boolean isShowPopup() {
		return showPopup;
	}

	public void setShowPopup(boolean showPopup) {
		this.showPopup = showPopup;
	}

	public boolean isResubmitAppl() {
		return resubmitAppl;
	}

	public void setResubmitAppl(boolean resubmitAppl) {
		this.resubmitAppl = resubmitAppl;
	}

	// #25638 - Start
	/**
	 * @return the displayReviseInstrn
	 */
	public boolean isDisplayReviseInstrn() {
		return displayReviseInstrn;
	}

	/**
	 * @param displayReviseInstrn the displayReviseInstrn to set
	 */
	public void setDisplayReviseInstrn(boolean displayReviseInstrn) {
		this.displayReviseInstrn = displayReviseInstrn;
	}
	// #25638 - End

	public boolean isValidFor25037() {
		return validFor25037;
	}

	public void setValidFor25037(boolean validFor25037) {
		this.validFor25037 = validFor25037;
	}

	public boolean isCommitteModified() {
		return committeModified;
	}

	public void setCommitteModified(boolean committeModified) {
		this.committeModified = committeModified;
	}

	// 28767
	public List<SmPersonnel> getPersonnelDirectorManager() {
		return personnelDirectorManager;
	}

	public void setPersonnelDirectorManager(List<SmPersonnel> personnelDirectorManager) {
		this.personnelDirectorManager = personnelDirectorManager;
	}

	public List<SmPersonnel> getPersonnelMembers() {
		return personnelMembers;
	}

	public void setPersonnelMembers(List<SmPersonnel> personnelMembers) {
		this.personnelMembers = personnelMembers;
	}

	public BigDecimal getTotalShare() {
		return totalShare;
	}

	public void setTotalShare(BigDecimal totalShare) {
		this.totalShare = totalShare;
	}

	// start #29956
	public String getTermAndConditionCheckOwner() {
		return termAndConditionCheckOwner;
	}

	public void setTermAndConditionCheckOwner(String termAndConditionCheckOwner) {
		this.termAndConditionCheckOwner = termAndConditionCheckOwner;
	}

	public String getTermAndConditionCheckOwnerA() {
		return termAndConditionCheckOwnerA;
	}

	public void setTermAndConditionCheckOwnerA(String termAndConditionCheckOwnerA) {
		this.termAndConditionCheckOwnerA = termAndConditionCheckOwnerA;
	}

	public String getTermAndConditionCheckOwnerC() {
		return termAndConditionCheckOwnerC;
	}

	public void setTermAndConditionCheckOwnerC(String termAndConditionCheckOwnerC) {
		this.termAndConditionCheckOwnerC = termAndConditionCheckOwnerC;
	}

	public String getTermAndConditionCheckOwnerChangeRole() {
		return termAndConditionCheckOwnerChangeRole;
	}

	public void setTermAndConditionCheckOwnerChangeRole(String termAndConditionCheckOwnerChangeRole) {
		this.termAndConditionCheckOwnerChangeRole = termAndConditionCheckOwnerChangeRole;
	}

	// end #29956

	// start 27346
	public StreamedContent getDownloadfile() {
		return downloadfile;
	}

	public void setDownloadfile(StreamedContent downloadfile) {
		this.downloadfile = downloadfile;
	}
	// end 27346

	//27382
	public boolean isErrorInvalidSelfRequest() {
		return errorInvalidSelfRequest;
	}

	public void setErrorInvalidSelfRequest(boolean errorInvalidSelfRequest) {
		this.errorInvalidSelfRequest = errorInvalidSelfRequest;
	}
	
	//14685 - START
	public boolean isShowMessage() {
		return showMessage;
	}

	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}
	//14685 - END

	//start #29956
	public Boolean isDowngradeRole() {
		return isDowngradeRole;
	}

	public void setDowngradeRole(Boolean isDowngradeRole) {
		this.isDowngradeRole = isDowngradeRole;
	}
	//end #29956
	//25037 - start - note 510
	public String getInactiveSCMemberStr() {
		return inactiveSCMemberStr;
	}

	public void setInactiveSCMemberStr(String inactiveSCMemberStr) {
		this.inactiveSCMemberStr = inactiveSCMemberStr;
	}
	//25037 - end - note 510

	//#25726 start
	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}	
	//#25726 end

	//#32236 start
	public String getOutputPage() {
		return outputPage;
	}

	public void setOutputPage(String outputPage) {
		this.outputPage = outputPage;
	}
	//#32236 end

	public Integer getWipStatusMax() {
		return wipStatusMax;
	}

	public void setWipStatusMax(Integer wipStatusMax) {
		this.wipStatusMax = wipStatusMax;
	}

	// #29007 wip -start-
	public String getBpmVersionNo() {
		return bpmVersionNo;
	}

	public void setBpmVersionNo(String bpmVersionNo) {
		this.bpmVersionNo = bpmVersionNo;
	}
	// #29007 wip -end-

	public boolean isNewVersion() {
		return newVersion;
	}

	public void setNewVersion(boolean newVersion) {
		this.newVersion = newVersion;
	}
	//25037 - start
	public Calendar getCurrentTaskCreatedDate() {
		return currentTaskCreatedDate;
	}

	public void setCurrentTaskCreatedDate(Calendar currentTaskCreatedDate) {
		this.currentTaskCreatedDate = currentTaskCreatedDate;
	}
	//25037 - end	
	// #28895_35501 Start - AUDIT TRAIL SM
	public boolean isForceReplace() {
		return forceReplace;
	}

	public void setForceReplace(boolean forceReplace) {
		this.forceReplace = forceReplace;
	}
	// #28895_35501 End
}