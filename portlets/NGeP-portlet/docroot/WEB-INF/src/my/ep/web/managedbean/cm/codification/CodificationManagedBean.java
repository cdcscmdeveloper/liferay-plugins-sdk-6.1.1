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

	

	private CmItem selectedItem;
	private CmItem[] selectedItems;




	



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
