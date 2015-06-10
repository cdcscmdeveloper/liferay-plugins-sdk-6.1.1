package my.ep.web.bean;

import java.io.Serializable;
import java.util.List;

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
	private String selectionMode;
	

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
	public String getSelectionMode() {
		return selectionMode;
	}
	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}
	
	
}
