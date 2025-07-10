package my.ep.web.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedProperty;

import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;

public class LovBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String lovId;
	private String componentId;
	private List<LovColumnBean> columns;
	private String title;
	private String pk;
	private String saveObject;
	private String daoClass;
	private String searchMethod;
	private String conditionField;
	private String clientId;
	private String update;
	private String postUpdateListener;
	private String postUpdateListenerWithIndex;
	private String selectionMode;
	private int saveObjectIndex;
	private String autoUpdateValue;
	private boolean isDisplayResultWhenFirstLoad;
	private boolean isDisplayResultWhenReset;
	//Start - #29416
	private boolean itemUsageMonitoringReport = false;
	private String docNo;
	private boolean unSelectAndCancel = false;
	private boolean resetFlag = false;
	//End - #29416
	// #29832 start 
	//added managedproperty to pass the value from simplequoteBB into LovBB to trigger delete selected list
	@ManagedProperty(value="#{simpleQuoteBackingBean}")
	private SimpleQuoteBackingBean simpleQuoteBackingBean;
	// #29832 end

	/* #28895_35509 PM AUDIT */
	private String roleCode;
	private boolean storeProc;
	private String orgType;

	/* #28895_35509 PM AUDIT */
	
	public String getLovId() {
		return lovId;
	}
	public void setLovId(String lovId) {
		this.lovId = lovId;
	}
	public List<LovColumnBean> getColumns() {
		return columns;
	}
	public void setColumns(List<LovColumnBean> columns) {
		this.columns = columns;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getSaveObject() {
		return saveObject;
	}
	public void setSaveObject(String saveObject) {
		this.saveObject = saveObject;
	}
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	public String getDaoClass() {
		return daoClass;
	}
	public void setDaoClass(String daoClass) {
		this.daoClass = daoClass;
	}
	public String getSearchMethod() {
		return searchMethod;
	}
	public void setSearchMethod(String searchMethod) {
		this.searchMethod = searchMethod;
	}
	public String getConditionField() {
		return conditionField;
	}
	public void setConditionField(String conditionField) {
		this.conditionField = conditionField;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public String getPostUpdateListener() {
		return postUpdateListener;
	}
	public void setPostUpdateListener(String postUpdateListener) {
		this.postUpdateListener = postUpdateListener;
	}
	public String getPostUpdateListenerWithIndex() {
		return postUpdateListenerWithIndex;
	}
	public void setPostUpdateListenerWithIndex(String postUpdateListenerWithIndex) {
		this.postUpdateListenerWithIndex = postUpdateListenerWithIndex;
	}
	public String getSelectionMode() {
		return selectionMode;
	}
	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}
	public int getSaveObjectIndex() {
		return saveObjectIndex;
	}
	public void setSaveObjectIndex(int saveObjectIndex) {
		this.saveObjectIndex = saveObjectIndex;
	}
	
	/**
	 * @return the autoUpdateValue
	 */
	public String getAutoUpdateValue() {
		return autoUpdateValue;
	}
	
	/**
	 * @param autoUpdateValue the autoUpdateValue to set
	 */
	public void setAutoUpdateValue(String autoUpdateValue) {
		this.autoUpdateValue = autoUpdateValue;
	}
	
	public boolean isDisplayResultWhenFirstLoad() {
		return isDisplayResultWhenFirstLoad;
	}
	public void setDisplayResultWhenFirstLoad(boolean isDisplayResultWhenFirstLoad) {
		this.isDisplayResultWhenFirstLoad = isDisplayResultWhenFirstLoad;
	}
	public boolean isDisplayResultWhenReset() {
		return isDisplayResultWhenReset;
	}
	public void setDisplayResultWhenReset(boolean isDisplayResultWhenReset) {
		this.isDisplayResultWhenReset = isDisplayResultWhenReset;
	}
	
	// #29832 start @zaim
	/**
	 * @return the simpleQuoteBackingBean
	 */
	public SimpleQuoteBackingBean getSimpleQuoteBackingBean() {
		return simpleQuoteBackingBean;
	}
	/**
	 * @param simpleQuoteBackingBean the simpleQuoteBackingBean to set
	 */
	public void setSimpleQuoteBackingBean(SimpleQuoteBackingBean simpleQuoteBackingBean) {
		this.simpleQuoteBackingBean = simpleQuoteBackingBean;
	}
	// #29832 end
	//Start - #29416
	public boolean getItemUsageMonitoringReport() {
		return itemUsageMonitoringReport;
	}

	public void setItemUsageMonitoringReport(boolean itemUsageMonitoringReport) {
		this.itemUsageMonitoringReport = itemUsageMonitoringReport;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public boolean isUnSelectAndCancel() {
		return unSelectAndCancel;
	}

	public void setUnSelectAndCancel(boolean unSelectAndCancel) {
		this.unSelectAndCancel = unSelectAndCancel;
	}
	
	public boolean isResetFlag() {
		return resetFlag;
	}
	public void setResetFlag(boolean resetFlag) {
		this.resetFlag = resetFlag;
	}
	//End - #29416

	/* #28895_35509 PM AUDIT */
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public boolean isStoreProc() {
		return storeProc;
	}

	public void setStoreProc(boolean storeProc) {
		this.storeProc = storeProc;
	}
	/* #28895_35509 PM AUDIT */
	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
}
