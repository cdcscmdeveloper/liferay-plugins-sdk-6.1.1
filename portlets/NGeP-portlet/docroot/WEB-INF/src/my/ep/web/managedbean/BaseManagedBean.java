/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.faces.model.SelectItem;

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
			if(selectedReport != null)
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
}


// ***25201
