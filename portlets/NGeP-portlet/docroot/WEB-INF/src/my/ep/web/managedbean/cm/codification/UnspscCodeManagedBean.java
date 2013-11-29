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
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.RemarksVO;

@ManagedBean(name = "unspscCodeMB")
@SessionScoped
public class UnspscCodeManagedBean extends BaseManagedBean {
	
	
	private CmRequestCode requestCode;
	private String segmentTitle;
	private String segmentDesc;
	private Long segmentKey;
	private String segmentCode;	
	
	private String familyTitle;
	private String familyDesc;
	private Long familyKey;
	private String familyCode;
	
	private String classTitle;
	private String classDesc;
	private Long classKey;
	private String classCode;
	
	private String commodityTitle;
	private String commodityDesc;
	private Long commodityKey;
	private String commodityCode;
	
	private CmBrand cmBrand;
	private CmColor cmColor;
	private CmMeasurement cmMeasurement;
	private CmType cmType;
	
	private String unspscCode;
	private String unspscTitle;
	
	private CmRequestCode requestExtCode;
	private List<CmRequestCode> requestExtCodeList;
	private List<CmBrand> brandList;
	private List<CmColor> colorList;
	private List<CmType> typeList;
	private List<CmMeasurement> measurementList;
	
	private String typeName;
	private String colorName;
	private String measurementName;
	private String brandName;
	
	private List<CmRequestCode> reviewExtCodeList;
	
	private String taskId;
	
	private String[] action;
	
	private String tempUser;
	
	private String taskName;
	private String processName;
	
	
	private Long typeId;
	private Long colorId;
	private Long measurementId;
	private Long brandId;
	
	private CmUnspsc selectedLovUnspsc;
	private CmUnspsc selectedLovUnspscSegment;
	private CmUnspsc selectedLovUnspscfamily;
	private CmUnspsc selectedLovUnspscClass;
	
	
	
	
	
	
	
	
	public CmUnspsc getSelectedLovUnspscfamily() {
		if(selectedLovUnspscfamily==null)
			selectedLovUnspscfamily = new CmUnspsc();
		return selectedLovUnspscfamily;
	}

	public void setSelectedLovUnspscfamily(CmUnspsc selectedLovUnspscfamily) {
		this.selectedLovUnspscfamily = selectedLovUnspscfamily;
	}

	public CmUnspsc getSelectedLovUnspscClass() {
		if(selectedLovUnspscClass==null)
			selectedLovUnspscClass = new CmUnspsc();
		return selectedLovUnspscClass;
	}

	public void setSelectedLovUnspscClass(CmUnspsc selectedLovUnspscClass) {
		this.selectedLovUnspscClass = selectedLovUnspscClass;
	}

	public CmUnspsc getSelectedLovUnspscSegment() {
		if(selectedLovUnspscSegment==null)
			selectedLovUnspscSegment = new CmUnspsc();
		return selectedLovUnspscSegment;
	}

	public void setSelectedLovUnspscSegment(CmUnspsc selectedLovUnspscSegment) {
		this.selectedLovUnspscSegment = selectedLovUnspscSegment;
	}

	public CmUnspsc getSelectedLovUnspsc() {
		if(selectedLovUnspsc == null)
			selectedLovUnspsc = new CmUnspsc();
		return selectedLovUnspsc;
	}

	public void setSelectedLovUnspsc(CmUnspsc selectedLovUnspsc) {
		this.selectedLovUnspsc = selectedLovUnspsc;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getColorId() {
		return colorId;
	}

	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}

	public Long getMeasurementId() {
		return measurementId;
	}

	public void setMeasurementId(Long measurementId) {
		this.measurementId = measurementId;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getTempUser() {
		return tempUser;
	}

	public void setTempUser(String tempUser) {
		this.tempUser = tempUser;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<CmRequestCode> getReviewExtCodeList() {
		if(reviewExtCodeList==null)
			reviewExtCodeList = new ArrayList<CmRequestCode>();
		return reviewExtCodeList;
	}

	public void setReviewExtCodeList(List<CmRequestCode> reviewExtCodeList) {
		this.reviewExtCodeList = reviewExtCodeList;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getMeasurementName() {
		return measurementName;
	}

	public void setMeasurementName(String measurementName) {
		this.measurementName = measurementName;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public List<CmBrand> getBrandList() {
		if(brandList==null)
			brandList = new ArrayList<CmBrand>();
		return brandList;
	}

	public void setBrandList(List<CmBrand> brandList) {
		this.brandList = brandList;
	}

	public List<CmColor> getColorList() {
		if(colorList==null)
			colorList = new ArrayList<CmColor>();
		return colorList;
	}

	public void setColorList(List<CmColor> colorList) {
		this.colorList = colorList;
	}

	public List<CmType> getTypeList() {
		if(typeList==null)
			typeList = new ArrayList<CmType>();
		return typeList;
	}

	public void setTypeList(List<CmType> typeList) {
		this.typeList = typeList;
	}

	public List<CmMeasurement> getMeasurementList() {
		if(measurementList==null)
			measurementList = new ArrayList<CmMeasurement>();
		return measurementList;
	}

	public void setMeasurementList(List<CmMeasurement> measurementList) {
		this.measurementList = measurementList;
	}

	public List<CmRequestCode> getRequestExtCodeList() {
		if(requestExtCodeList==null)
			requestExtCodeList = new ArrayList<CmRequestCode>();
		return requestExtCodeList;
	}

	public void setRequestExtCodeList(List<CmRequestCode> requestExtCodeList) {
		this.requestExtCodeList = requestExtCodeList;
	}

	public CmRequestCode getRequestExtCode() {
		if(requestExtCode==null)
			requestExtCode = new CmRequestCode();
		return requestExtCode;
	}

	public void setRequestExtCode(CmRequestCode requestExtCode) {
		this.requestExtCode = requestExtCode;
	}

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getUnspscTitle() {
		return unspscTitle;
	}

	public void setUnspscTitle(String unspscTitle) {
		this.unspscTitle = unspscTitle;
	}

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

	public Long getSegmentKey() {
		return segmentKey;
	}

	public void setSegmentKey(Long segmentKey) {
		this.segmentKey = segmentKey;
	}

	public Long getFamilyKey() {
		return familyKey;
	}

	public void setFamilyKey(Long familyKey) {
		this.familyKey = familyKey;
	}

	public Long getClassKey() {
		return classKey;
	}

	public void setClassKey(Long classKey) {
		this.classKey = classKey;
	}

	public Long getCommodityKey() {
		return commodityKey;
	}

	public void setCommodityKey(Long commodityKey) {
		this.commodityKey = commodityKey;
	}

	
	
	
	

}
