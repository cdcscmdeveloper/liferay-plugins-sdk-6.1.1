/**
 * 
 */
package my.ep.web.bean;

import javax.el.ValueExpression;

/**
 * @author Kian Seng
 *
 */
public class LovColumnBean {

	private String bundleFile;
	private String bundleKey;
	private String columnTitle;
	private String columnName;
	private String methodName;
	
	private Object columnValue;
	private String labelMap;

	private String propertyType;
	private boolean searchable;
	private boolean viewable;
	private Integer columnWidth;
	
	private ValueExpression columnNameve;
	
	
	public LovColumnBean() {
		viewable = true;
		searchable = true;
		propertyType = "String";
	}
	
	public String getBundleFile() {
		return bundleFile;
	}
	public void setBundleFile(String bundleFile) {
		this.bundleFile = bundleFile;
	}
	public String getBundleKey() {
		return bundleKey;
	}
	public void setBundleKey(String bundleKey) {
		this.bundleKey = bundleKey;
	}
	public String getColumnTitle() {
		System.out.println("columnTitle: " + columnTitle);
		return columnTitle;
	}
	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Object getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(Object columnValue) {
		this.columnValue = columnValue;
	}
	public String getLabelMap() {
		return labelMap;
	}
	public void setLabelMap(String labelMap) {
		this.labelMap = labelMap;
	}
	public String getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public Integer getColumnWidth() {
		return columnWidth;
	}
	public void setColumnWidth(Integer columnWidth) {
		this.columnWidth = columnWidth;
	}
	public boolean isViewable() {
		return viewable;
	}
	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	public ValueExpression getColumnNameve() {
		return columnNameve;
	}

	public void setColumnNameve(ValueExpression columnNameve) {
		this.columnNameve = columnNameve;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
}
