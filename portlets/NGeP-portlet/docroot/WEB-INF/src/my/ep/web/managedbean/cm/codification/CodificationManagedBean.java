package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "unspscCodeMB")
@SessionScoped
public class CodificationManagedBean extends BaseManagedBean {


	private String cmNavigationURL;

	private List<CmUnspsc> unspscList;
	private List<CmItem> itemList;
	private CmUnspsc selectedUNSPSCObj;

	private String unspscCode;
	private String unspscTitle;

	private List<SelectItem> typeCriteria;
	private List<SelectItem> brandCriteria;
	private List<SelectItem> measurementCriteria;
	private List<SelectItem> colorCriteria;
	
	private List<CmUnspsc> contractedItem;

	

	private CmItem selectedItem;
	private CmItem[] selectedItems;
	
	private List<CmUnspsc> segmentList;
	private List<CmUnspsc> familyList;
	private List<CmUnspsc> classList;
	private List<CmUnspsc> commodityList;


	private List<SelectItem> segmentCriteria;
	private List<SelectItem> familyCriteria;
	private List<SelectItem> classCriteria;
	private List<SelectItem> commodityCriteria;
	
	
	private String segmentCode;
	private String familyCode;
	private String classCode;
	private String commodityCode;
	private String brandCode;
	private String typeCode;
	private String colorCode;
	private String measurementCode;
	
	
	

	
	


	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getMeasurementCode() {
		return measurementCode;
	}

	public void setMeasurementCode(String measurementCode) {
		this.measurementCode = measurementCode;
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

	

	public List<SelectItem> getSegmentCriteria() {
		if(segmentCriteria==null)
			segmentCriteria = new ArrayList<SelectItem>();
		return segmentCriteria;
	}

	public void setSegmentCriteria(List<SelectItem> segmentCriteria) {
		this.segmentCriteria = segmentCriteria;
	}

	public List<SelectItem> getFamilyCriteria() {
		if(familyCriteria==null)
			familyCriteria = new ArrayList<SelectItem>();
		return familyCriteria;
	}

	public void setFamilyCriteria(List<SelectItem> familyCriteria) {
		this.familyCriteria = familyCriteria;
	}

	public List<SelectItem> getClassCriteria() {
		if(classCriteria==null)
			classCriteria = new ArrayList<SelectItem>();
		return classCriteria;
	}

	public void setClassCriteria(List<SelectItem> classCriteria) {
		this.classCriteria = classCriteria;
	}

	public List<SelectItem> getCommodityCriteria() {
		if(commodityCriteria==null)
			commodityCriteria = new ArrayList<SelectItem>();
		return commodityCriteria;
	}

	public void setCommodityCriteria(List<SelectItem> commodityCriteria) {
		this.commodityCriteria = commodityCriteria;
	}

	public List<CmUnspsc> getSegmentList() {
		if(segmentList==null)
			segmentList= new ArrayList<CmUnspsc>();
		return segmentList;
	}

	public void setSegmentList(List<CmUnspsc> segmentList) {
		this.segmentList = segmentList;
	}

	public List<CmUnspsc> getFamilyList() {
		if(familyList==null)
			familyList= new ArrayList<CmUnspsc>();
		return familyList;
	}

	public void setFamilyList(List<CmUnspsc> familyList) {
		this.familyList = familyList;
	}

	public List<CmUnspsc> getClassList() {
		if(classList==null)
			classList = new ArrayList<CmUnspsc>();
		return classList;
	}

	public void setClassList(List<CmUnspsc> classList) {
		this.classList = classList;
	}

	public List<CmUnspsc> getCommodityList() {
		if(commodityList==null)
			commodityList= new ArrayList<CmUnspsc>();
		return commodityList;
	}

	public void setCommodityList(List<CmUnspsc> commodityList) {
		this.commodityList = commodityList;
	}

	public List<CmUnspsc> getContractedItem() {
		if(contractedItem==null)
			contractedItem = new ArrayList<CmUnspsc>();
		return contractedItem;
	}

	public void setContractedItem(List<CmUnspsc> contractedItem) {
		this.contractedItem = contractedItem;
	}

	public CmItem[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(CmItem[] selectedItems) {
		this.selectedItems = selectedItems;
	}


	public CmItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CmItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public List<SelectItem> getColorCriteria() {
		if(colorCriteria==null)
			colorCriteria = new ArrayList<SelectItem>();
		return colorCriteria;
	}

	public void setColorCriteria(List<SelectItem> colorCriteria) {
		this.colorCriteria = colorCriteria;
	}

	public List<SelectItem> getBrandCriteria() {
		if(brandCriteria==null)
			brandCriteria = new ArrayList<SelectItem>();
		return brandCriteria;
	}

	public void setBrandCriteria(List<SelectItem> brandCriteria) {

		this.brandCriteria = brandCriteria;
	}

	public List<SelectItem> getMeasurementCriteria() {
		if(measurementCriteria==null)
			measurementCriteria = new ArrayList<SelectItem>();
		return measurementCriteria;
	}

	public void setMeasurementCriteria(List<SelectItem> measurementCriteria) {
		this.measurementCriteria = measurementCriteria;
	}

	public List<SelectItem> getTypeCriteria() {
		if(typeCriteria==null)
			typeCriteria = new ArrayList<SelectItem>();
		return typeCriteria;
	}

	public void setTypeCriteria(List<SelectItem> typeCriteria) {
		this.typeCriteria = typeCriteria;
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

	public String getCmNavigationURL() {
		return cmNavigationURL;
	}

	public void setCmNavigationURL(String cmNavigationURL) {
		this.cmNavigationURL = cmNavigationURL;
	}

	public List<CmUnspsc> getUnspscList() {
		return unspscList;
	}

	public void setUnspscList(List<CmUnspsc> unspscList) {
		this.unspscList = unspscList;
	}

	public CmUnspsc getSelectedUNSPSCObj() {
		if(selectedUNSPSCObj==null)
			selectedUNSPSCObj = new CmUnspsc();
		return selectedUNSPSCObj;
	}

	public void setSelectedUNSPSCObj(CmUnspsc selectedUNSPSCObj) {
		this.selectedUNSPSCObj = selectedUNSPSCObj;
	}


	public List<CmItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<CmItem> itemList) {
		this.itemList = itemList;
	}







}
