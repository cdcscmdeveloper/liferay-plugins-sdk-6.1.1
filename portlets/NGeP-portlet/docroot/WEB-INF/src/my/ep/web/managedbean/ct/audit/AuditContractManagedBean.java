package my.ep.web.managedbean.ct.audit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.web.managedbean.BaseManagedBean;
import java.io.Serializable;

/**
 * Managed Bean for handling Audit Contract operations in a JSF web application.
 * This bean is session-scoped, meaning it maintains state across multiple requests 
 * within a single user session.
 * 
 * @author putra
 */
@ManagedBean(name = "auditContractMB")
@SessionScoped
public class AuditContractManagedBean extends BaseManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isLazy = false;
    private boolean auditTrailView;
    private String backURL;
    private String language;
    private Long docId;
    private Long contractVerId;
    private String docType;
    private String ctType;
    private String ctCircularType;
    private boolean isSearching = true;
    private Long auTabView;
    private String captchaText;
    private int activeTab;

    /**
     * Default constructor.
     */
    public AuditContractManagedBean() {
        // Default initialization logic, if needed.
    }

    /**
     * Checks if lazy loading is enabled.
     *
     * @return true if lazy loading is enabled, false otherwise.
     */
    public boolean isLazy() {
        return isLazy;
    }

    /**
     * Sets the lazy loading flag.
     *
     * @param isLazy true to enable lazy loading, false otherwise.
     */
    public void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    /**
     * Checks if the audit trail view is enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public boolean isAuditTrailView() {
        return auditTrailView;
    }

    /**
     * Sets the audit trail view flag.
     *
     * @param auditTrailView true to enable the audit trail view, false otherwise.
     */
    public void setAuditTrailView(boolean auditTrailView) {
        this.auditTrailView = auditTrailView;
    }

    /**
     * Gets the back navigation URL.
     *
     * @return the back URL as a string.
     */
    public String getBackURL() {
        return backURL;
    }

    /**
     * Sets the back navigation URL.
     *
     * @param backURL the back URL to set.
     */
    public void setBackURL(String backURL) {
        this.backURL = backURL;
    }

    /**
     * Gets the selected language.
     *
     * @return the language code.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language.
     *
     * @param language the language code to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets the document ID.
     *
     * @return the document ID.
     */
    public Long getDocId() {
        return docId;
    }

    /**
     * Sets the document ID.
     *
     * @param docId the document ID to set.
     */
    public void setDocId(Long docId) {
        this.docId = docId;
    }

    /**
     * Checks if the search is in progress.
     *
     * @return true if searching, false otherwise.
     */
    public boolean isSearching() {
        return isSearching;
    }

    /**
     * Sets the searching status.
     *
     * @param isSearching true if searching is in progress, false otherwise.
     */
    public void setSearching(boolean isSearching) {
        this.isSearching = isSearching;
    }

    /**
     * Gets the current audit tab view ID.
     *
     * @return the audit tab view ID.
     */
    public Long getAuTabView() {
        return auTabView;
    }

    /**
     * Sets the audit tab view ID.
     *
     * @param auTabView the audit tab view ID to set.
     */
    public void setAuTabView(Long auTabView) {
        this.auTabView = auTabView;
    }

    /**
     * Gets the document type.
     *
     * @return the document type.
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets the document type.
     *
     * @param docType the document type to set.
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets the contract version ID.
     *
     * @return the contract version ID.
     */
    public Long getContractVerId() {
        return contractVerId;
    }

    /**
     * Sets the contract version ID.
     *
     * @param contractVerId the contract version ID to set.
     */
    public void setContractVerId(Long contractVerId) {
        this.contractVerId = contractVerId;
    }

    /**
     * Gets the captcha text.
     *
     * @return the captcha text.
     */
    public String getCaptchaText() {
        return captchaText;
    }

    /**
     * Sets the captcha text.
     *
     * @param captchaText the captcha text to set.
     */
    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    /**
     * Gets the contract type.
     *
     * @return the contract type.
     */
    public String getCtType() {
        return ctType;
    }

    /**
     * Sets the contract type.
     *
     * @param ctType the contract type to set.
     */
    public void setCtType(String ctType) {
        this.ctType = ctType;
    }

    /**
     * Gets the currently active tab.
     *
     * @return the index of the active tab.
     */
    public int getActiveTab() {
        return activeTab;
    }

    /**
     * Sets the currently active tab.
     *
     * @param activeTab the index of the active tab.
     */
    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

	public String getCtCircularType() {
		return ctCircularType;
	}

	public void setCtCircularType(String ctCircularType) {
		this.ctCircularType = ctCircularType;
	}
}
