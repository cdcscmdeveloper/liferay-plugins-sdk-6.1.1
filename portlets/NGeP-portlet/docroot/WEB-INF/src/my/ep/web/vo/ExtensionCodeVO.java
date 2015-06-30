package my.ep.web.vo;

import java.io.Serializable;



public class ExtensionCodeVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	private Long typeId;
	private Long brandId;
	private Long measurementId;
	private Long colorId;
	private String typeName;
	private String brandName;
	private String measurementName;
	private String colorName;
	private boolean selected = false;
	private String key;
	
	
	
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public Long getBrandId() {
		return brandId;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public Long getMeasurementId() {
		return measurementId;
	}
	public void setMeasurementId(Long measurementId) {
		this.measurementId = measurementId;
	}
	public Long getColorId() {
		return colorId;
	}
	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getMeasurementName() {
		return measurementName;
	}
	public void setMeasurementName(String measurementName) {
		this.measurementName = measurementName;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
	
	
	
	
	
	
	
	
}