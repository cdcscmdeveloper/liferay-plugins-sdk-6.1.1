package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.RemarksVO;

@ManagedBean(name = "requestCodeMB")
@SessionScoped
public class UnspscCodeManagedBean extends BaseManagedBean {
	
	
	private CmRequestCode requestCode;
	private String segmentTitle;
	private String segmentDesc;
	private long segmentKey;
	private String segmentCode;	
	
	private String familyTitle;
	private String familyDesc;
	private long familyKey;
	private String familyCode;
	
	private String classTitle;
	private String classDesc;
	private long classKey;
	private String classCode;
	
	private String commodityTitle;
	private String commodityDesc;
	private long commodityKey;
	private String commodityCode;
	
	private CmBrand cmBrand;
	private CmColor cmColor;
	private CmMeasurement cmMeasurement;
	private CmType cmType;
	
	

	public CmBrand getCmBrand() {
		if(cmBrand==null)
			cmBrand = new CmBrand();
		return cmBrand;
	}

	public void setCmBrand(CmBrand cmBrand) {
		this.cmBrand = cmBrand;
	}

	public CmColor getCmColor() {
		if(cmColor==null)
			cmColor = new CmColor();
		return cmColor;
	}

	public void setCmColor(CmColor cmColor) {
		this.cmColor = cmColor;
	}

	public CmMeasurement getCmMeasurement() {
		if(cmMeasurement==null)
			cmMeasurement = new CmMeasurement();
		return cmMeasurement;
	}

	public void setCmMeasurement(CmMeasurement cmMeasurement) {
		this.cmMeasurement = cmMeasurement;
	}

	public CmType getCmType() {
		if(cmType==null)
			cmType = new CmType();
		return cmType;
	}

	public void setCmType(CmType cmType) {
		this.cmType = cmType;
	}

	public CmRequestCode getRequestCode() {
		if(requestCode==null)
			requestCode = new CmRequestCode();
		return requestCode;
	}

	public void setRequestCode(CmRequestCode requestCode) {
		this.requestCode = requestCode;
	}

	public String getSegmentTitle() {
		return segmentTitle;
	}

	public void setSegmentTitle(String segmentTitle) {
		this.segmentTitle = segmentTitle;
	}

	public String getSegmentDesc() {
		return segmentDesc;
	}

	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}

	public long getSegmentKey() {
		return segmentKey;
	}

	public void setSegmentKey(long segmentKey) {
		this.segmentKey = segmentKey;
	}

	

	public String getFamilyTitle() {
		return familyTitle;
	}

	public void setFamilyTitle(String familyTitle) {
		this.familyTitle = familyTitle;
	}

	public String getFamilyDesc() {
		return familyDesc;
	}

	public void setFamilyDesc(String familyDesc) {
		this.familyDesc = familyDesc;
	}

	public long getFamilyKey() {
		return familyKey;
	}

	public void setFamilyKey(long familyKey) {
		this.familyKey = familyKey;
	}

	
	public String getClassTitle() {
		return classTitle;
	}

	public void setClassTitle(String classTitle) {
		this.classTitle = classTitle;
	}

	public String getClassDesc() {
		return classDesc;
	}

	public void setClassDesc(String classDesc) {
		this.classDesc = classDesc;
	}

	public long getClassKey() {
		return classKey;
	}

	public void setClassKey(long classKey) {
		this.classKey = classKey;
	}



	public String getCommodityTitle() {
		return commodityTitle;
	}

	public void setCommodityTitle(String commodityTitle) {
		this.commodityTitle = commodityTitle;
	}

	public String getCommodityDesc() {
		return commodityDesc;
	}

	public void setCommodityDesc(String commodityDesc) {
		this.commodityDesc = commodityDesc;
	}

	public long getCommodityKey() {
		return commodityKey;
	}

	public void setCommodityKey(long commodityKey) {
		this.commodityKey = commodityKey;
	}

	public String getSegmentCode() {
		return segmentCode;
	}

	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getFamilyCode() {
		return familyCode;
	}

	public void setFamilyCode(String familyCode) {
		this.familyCode = familyCode;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	
	
	
	

}
