/**
 * 
 */
package my.ep.web.managedbean.ss.templatemaintenance;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author ACER
 *
 */
@ManagedBean(name="TemplateMMB")
@SessionScoped
public class TemplateMaintenanceManagedBean extends BaseManagedBean  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String supportingDoc;
	private String templateType;
	private String specificationType;
	public ScQtDoc scqtDoc;
	private String ministryName;
	public List<ScQtCheckList> supportDocList;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;
	private Long searchProcurementId;
	private List<ScQtDoc> docSearchlist;
	private List<SelectItem> checkListItem;
	private List<SelectItem>  zoneList;
	public TemplateMaintenanceManagedBean(){
		//scqtDoc=new ScQtDoc();
		/*getScqtDoc().setTplSpecMaster(new TplSpecMaster());
		getScqtDoc().getTplSpecMaster().setItemTypeCode(new PmCode());
		getScqtDoc().getTplSpecMaster().setProcurementCategory(new PmCode());
		getScqtDoc().getTplSpecMaster().setSpecTypeCode(new PmCode());
		getScqtDoc().getTplSpecMaster().setTemplateTypeCode(new PmCode());
		getScqtDoc().getTplSpecMaster().setTemplateTypeCode(new PmCode());*/
	}
	/**
	 * @return the supportingDoc
	 */
	public String getSupportingDoc() {
		return supportingDoc;
	}
	/**
	 * @param supportingDoc the supportingDoc to set
	 */
	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
	}
	
	/**
	 * @return the specificationType
	 */
	public String getSpecificationType() {
		return specificationType;
	}
	/**
	 * @param specificationType the specificationType to set
	 */
	public void setSpecificationType(String specificationType) {
		this.specificationType = specificationType;
	}
	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}
	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	/**
	 * @return the scqtDoc
	 */
	public ScQtDoc getScqtDoc() {
		return scqtDoc;
	}
	/**
	 * @param scqtDoc the scqtDoc to set
	 */
	public void setScqtDoc(ScQtDoc scqtDoc) {
		this.scqtDoc = scqtDoc;
	}
	/**
	 * @return the ministryName
	 */
	public String getMinistryName() {
		return ministryName;
	}
	/**
	 * @param ministryName the ministryName to set
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}
	/**
	 * @return the supportDocList
	 */
	public List<ScQtCheckList> getSupportDocList() {
		return supportDocList;
	}
	/**
	 * @param supportDocList the supportDocList to set
	 */
	public void setSupportDocList(List<ScQtCheckList> supportDocList) {
		this.supportDocList = supportDocList;
	}
	/**
	 * @return the searchItemTypeId
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}
	/**
	 * @param searchItemTypeId the searchItemTypeId to set
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}
	/**
	 * @return the searchSpecTypeId
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}
	/**
	 * @param searchSpecTypeId the searchSpecTypeId to set
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}
	/**
	 * @return the searchProcurementId
	 */
	public Long getSearchProcurementId() {
		return searchProcurementId;
	}
	/**
	 * @param searchProcurementId the searchProcurementId to set
	 */
	public void setSearchProcurementId(Long searchProcurementId) {
		this.searchProcurementId = searchProcurementId;
	}
	/**
	 * @return the docSearchlist
	 */
	public List<ScQtDoc> getDocSearchlist() {
		return docSearchlist;
	}
	/**
	 * @param docSearchlist the docSearchlist to set
	 */
	public void setDocSearchlist(List<ScQtDoc> docSearchlist) {
		this.docSearchlist = docSearchlist;
	}
	/**
	 * @return the checkListItem
	 */
	public List<SelectItem> getCheckListItem() {
		return checkListItem;
	}
	/**
	 * @param checkListItem the checkListItem to set
	 */
	public void setCheckListItem(List<SelectItem> checkListItem) {
		this.checkListItem = checkListItem;
	}
	/**
	 * @return the zoneList
	 */
	public List<SelectItem> getZoneList() {
		return zoneList;
	}
	/**
	 * @param zoneList the zoneList to set
	 */
	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

}
