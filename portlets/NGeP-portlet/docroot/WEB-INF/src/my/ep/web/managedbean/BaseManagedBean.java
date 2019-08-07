/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;

import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtEvaluationReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalizationReport;
import my.ep.web.bean.tpl.TemplateSpecBean;

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
	/*
	 * commonly used as a flag to user's permission to download documents/attachment
	 */
	private Boolean enableDownloadAllDocuments = Boolean.FALSE;
	private List<SelectItem> localAuthorityItems;
	private String captchaText;
	private String captchaAnswer;
	private String reportDownloadFileName;
	private ScAttachment scAttachment;
	private List<ScQtEvaluationReport> scQtEvaluationReportList;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private List<ScQtEvaluationReport> selectedScQtEvaluationReports;
	private ScQtEvaluationReport selectedReport;
	private List<ScQtFinalizationReport> evaluationReportList;
	private List<ScQtEvaluationReport> reportsToBeRemovedList;
	private boolean autoUpdate = Boolean.TRUE;
	private Set<UserTasks> userTasks;
	private Map<String, List<String>> additionalPages;
	
	//16052019 - SSM INTEGRATION CR - Fahmi
	private boolean ssmIntegrationActive;
	private boolean validateWithSSM;
	private boolean companyValidWithSSM;
	private boolean errorMessageSSM;
	private String statusFromSSM;
	private String statusDescFromSSM;
		
	
	// For sub title displayed in screen.
	private String title;
	
	private boolean disableFields;
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

	/**
	 * @return the reportDownloadFileName
	 */
	public String getReportDownloadFileName() {
		return reportDownloadFileName;
	}

	/**
	 * @param reportDownloadFileName the reportDownloadFileName to set
	 */
	public void setReportDownloadFileName(String reportDownloadFileName) {
		this.reportDownloadFileName = reportDownloadFileName;
	}

	/**
	 * @return the scAttachment
	 */
	public ScAttachment getScAttachment() {
		return scAttachment;
	}

	/**
	 * @param scAttachment the scAttachment to set
	 */
	public void setScAttachment(ScAttachment scAttachment) {
		this.scAttachment = scAttachment;
	}

	/**
	 * @return the scQtEvaluationReportList
	 */
	public List<ScQtEvaluationReport> getScQtEvaluationReportList() {
		return scQtEvaluationReportList;
	}

	/**
	 * @param scQtEvaluationReportList the scQtEvaluationReportList to set
	 */
	public void setScQtEvaluationReportList(List<ScQtEvaluationReport> scQtEvaluationReportList) {
		this.scQtEvaluationReportList = scQtEvaluationReportList;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the selectedScQtEvaluationReports
	 */
	public List<ScQtEvaluationReport> getSelectedScQtEvaluationReports() {
		return selectedScQtEvaluationReports;
	}

	/**
	 * @param selectedScQtEvaluationReports the selectedScQtEvaluationReports to set
	 */
	public void setSelectedScQtEvaluationReports(List<ScQtEvaluationReport> selectedScQtEvaluationReports) {
		this.selectedScQtEvaluationReports = selectedScQtEvaluationReports;
	}

	/**
	 * @return the selectedReport
	 */
	public ScQtEvaluationReport getSelectedReport() {
		/*if (null != selectedReport && StringUtils.isNotEmpty(selectedReport.getReportContent())) {
			List<ScQtEvaluationReport> evaluationReportList = this.getScQtEvaluationReportList();
			for (ScQtEvaluationReport evaluationReport : evaluationReportList) {
				if (evaluationReport.getQtReportId() == selectedReport.getQtReportId())
				evaluationReport.setReportContent(selectedReport.getReportContent());
				break;
			}
		}*/
		
		//#25142 to display special character after save the content --- tested for Supplier finalization (briefing paper tab)
		String reportContent = null;
		try {
			if(selectedReport != null && StringUtils.isNotEmpty(selectedReport.getReportContent()))
			{
				reportContent = selectedReport.getReportContent();
				
				reportContent = URLDecoder.decode(reportContent, "UTF-8");
				
				selectedReport.setReportContent(reportContent);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			reportContent = null;
		}
		
		return selectedReport;
	}

	/**
	 * @param selectedReport the selectedReport to set
	 */
	public void setSelectedReport(ScQtEvaluationReport selectedReport) {
		this.selectedReport = selectedReport;
	}

	/**
	 * @return the evaluationReportList
	 */
	public List<ScQtFinalizationReport> getEvaluationReportList() {
		return evaluationReportList;
	}

	/**
	 * @param evaluationReportList the evaluationReportList to set
	 */
	public void setEvaluationReportList(List<ScQtFinalizationReport> evaluationReportList) {
		this.evaluationReportList = evaluationReportList;
	}

	/**
	 * @return the reportsToBeRemovedList
	 */
	public List<ScQtEvaluationReport> getReportsToBeRemovedList() {
		return reportsToBeRemovedList;
	}

	/**
	 * @param reportsToBeRemovedList the reportsToBeRemovedList to set
	 */
	public void setReportsToBeRemovedList(List<ScQtEvaluationReport> reportsToBeRemovedList) {
		this.reportsToBeRemovedList = reportsToBeRemovedList;
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
	
	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public Set<UserTasks> getUserTasks() {
		return userTasks;
	}

	public void setUserTasks(Set<UserTasks> userTasks) {
		this.userTasks = userTasks;
	}
	
	public void addUserTasks(UserTasks userTask){
		if(this.userTasks == null){
			userTasks = new HashSet<UserTasks>();
		}
		userTasks.add(userTask);
	}
	
	public void removeUserTasks(UserTasks userTask){
		if(this.userTasks != null && !CollectionUtils.isEmpty(userTasks)){
			userTasks.remove(userTask);
		}
	}

	public Map<String, List<String>> getAdditionalPages() {
		return additionalPages;
	}

	public void setAdditionalPages(Map<String, List<String>> additionalPages) {
		this.additionalPages = additionalPages;
	}
	
	public void addAdditionalPages(String k, String page){
		if(this.additionalPages == null){
			this.additionalPages = new HashMap<String, List<String>>();
			List<String> t= new ArrayList<String>();
			this.additionalPages.put(k, t);
		}
		if(!this.additionalPages.containsKey(k) || this.additionalPages.get(k)==null){
			List<String> t= new ArrayList<String>();
			this.additionalPages.put(k, t);
		}
		this.additionalPages.get(k).add(page);
	}
	
	/**
	 * Copy BPM TaskDetails to another instance of BaseManagedBean
	 * @param bm - another BaseManagedBean instance
	 */
	public void copyBPMTaskDetails(BaseManagedBean bm){
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
	
	
	
}


// ***25201
// ***25692
