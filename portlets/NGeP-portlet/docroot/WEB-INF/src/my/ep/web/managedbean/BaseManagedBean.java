/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang3.StringUtils;

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
import my.ep.persistence.entity.sm.SmSupplier_;
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

	//16052019 - SSM INTEGRATION CR - Fahmi
	private boolean ssmIntegrationActive;
	private boolean validateWithSSM;
	private boolean companyValidWithSSM;
	private boolean errorMessageSSM;
	private String statusFromSSM;
	private String statusDescFromSSM;
	private SmSupplier smSupplierSsm;
	private SmPersonnel smPersonnelSsm;
		
	private List<SmPersonnel> personnelsRole  = new ArrayList();
	
	// For sub title displayed in screen.
	private String title;
	
	//#24540 - start -
	private boolean disableFields;
	//#24540 - end -
	//27951
	private boolean preRegistration=false;
	private SsmResDTO ssmResDTO;
	private boolean checkRoleSSM=false;
	//27239
	private boolean dontGetSsmData = false;
	//28454
	private boolean resubmitAppl = false;
	private boolean showPopup = false;
	
	//17906
	private String displayMessageBank;

	//27346-start 
	private SmAppl smAppl;
	private SmSupplier SmSupplier;
	private List<SmPersonnel> personnelsDirector  = new ArrayList();
	private List<SmSupplierBranch>  smSupplierBranchSsm = new ArrayList();
	private List<SmEquityCompany> smEquityCompaniesSsm   = new ArrayList();   
	private boolean disAllowOwnerEdit; 
	private String businessTypeParamCode;
	private List<SmPersonnel> personnelsOwner = new ArrayList();
	private List<SmPersonnel> personnelsSsm  = new ArrayList();
	private List<SmSupplierBusiness> smSupplierBusinessSsm   = new ArrayList();
	private SmCompanyBasic smCompanyBasicSsm = null;
	private boolean showDetailSSM; 
	private List<SmPersonnel> smPersonnelDirectorSsm = new ArrayList();
	private List<SmPersonnel> personnelsOwnerSsm = new ArrayList();
	private SmAddress smAddress;
	//27346-end

	// #25638 - Start		
	private boolean displayReviseInstrn;
	// #25638 - End	
	
	//27346-start 
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

	public void setSmSupplierBusinessSsm(
			List<SmSupplierBusiness> smSupplierBusinessSsm) {
		this.smSupplierBusinessSsm = smSupplierBusinessSsm;
	}
	//27346-end
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
	
	//#24540 - start -
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
	//#24540 - end -	
	
	public List<SmPersonnel> getPersonnelsRole() {
		return personnelsRole;
	}

	public void setPersonnelsRole(List<SmPersonnel> personnelsRole) {
		this.personnelsRole = personnelsRole;
	}
	
	public String getDisplayMessageBank() {
		return displayMessageBank;
	}

	public void setDisplayMessageBank(String displayMessageBank) {
		this.displayMessageBank = displayMessageBank;
	}
	
	//27951
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
		
}
