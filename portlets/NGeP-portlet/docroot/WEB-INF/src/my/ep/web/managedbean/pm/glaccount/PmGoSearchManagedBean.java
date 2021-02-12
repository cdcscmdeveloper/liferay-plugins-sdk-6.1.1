package my.ep.web.managedbean.pm.glaccount;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.VPmGo;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "pmGoSearchManagedBean")
@SessionScoped
public class PmGoSearchManagedBean extends BaseManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String selectedAccType;
	private String selectedAccClass;
	private String selectedGenObjCode;
	private String desc;
	private VPmGo vPmGo;
	private Integer index;
	private String type;
	private String update;
	private boolean isLazy;
	private Long kptjId;
	private boolean accTypeDisabled;
	private boolean accClassDisabled;
	private String selectedGoSoObjCode;
	//210801@akmal - Enhancement27624 - Start
	private String selectionMode;
	private List<VPmGo> vPmGolist;
	private String saveObject;
	private boolean disableSelectbtn=false;
	//210801@akmal - Enhancement27624 - End
	//11022021@azza - Bug #30066 - Start
	private boolean disableSelectbtnPmGoDt = false;
	//11022021@azza - Bug #30066 - End
	
	public PmGoSearchManagedBean(){
		
	}

	/**
	 * @return the selectedAccType
	 */
	public String getSelectedAccType() {
		return selectedAccType;
	}

	/**
	 * @param selectedAccType the selectedAccType to set
	 */
	public void setSelectedAccType(String selectedAccType) {
		this.selectedAccType = selectedAccType;
	}

	/**
	 * @return the selectedAccClass
	 */
	public String getSelectedAccClass() {
		return selectedAccClass;
	}

	/**
	 * @param selectedAccClass the selectedAccClass to set
	 */
	public void setSelectedAccClass(String selectedAccClass) {
		this.selectedAccClass = selectedAccClass;
	}

	/**
	 * @return the selectedGenObjCode
	 */
	public String getSelectedGenObjCode() {
		return selectedGenObjCode;
	}

	/**
	 * @param selectedGenObjCode the selectedGenObjCode to set
	 */
	public void setSelectedGenObjCode(String selectedGenObjCode) {
		this.selectedGenObjCode = selectedGenObjCode;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the vPmGo
	 */
	public VPmGo getvPmGo() {
		return vPmGo;
	}

	/**
	 * @param vPmGo the vPmGo to set
	 */
	public void setvPmGo(VPmGo vPmGo) {
		this.vPmGo = vPmGo;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the update
	 */
	public String getUpdate() {
		return update;
	}

	/**
	 * @param update the update to set
	 */
	public void setUpdate(String update) {
		this.update = update;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the kptjId
	 */
	public Long getKptjId() {
		return kptjId;
	}

	/**
	 * @param kptjId the kptjId to set
	 */
	public void setKptjId(Long kptjId) {
		this.kptjId = kptjId;
	}

	/**
	 * @return the accTypeDisabled
	 */
	public boolean isAccTypeDisabled() {
		return accTypeDisabled;
	}

	/**
	 * @param accTypeDisabled the accTypeDisabled to set
	 */
	public void setAccTypeDisabled(boolean accTypeDisabled) {
		this.accTypeDisabled = accTypeDisabled;
	}

	/**
	 * @return the accClassDisabled
	 */
	public boolean isAccClassDisabled() {
		return accClassDisabled;
	}

	/**
	 * @param accClassDisabled the accClassDisabled to set
	 */
	public void setAccClassDisabled(boolean accClassDisabled) {
		this.accClassDisabled = accClassDisabled;
	}
	
	/**
	 * @return the selectedGoSoObjCode
	 */
	public String getSelectedGoSoObjCode() {
		return selectedGoSoObjCode;
	}

	/**
	 * @param selectedGoSoObjCode the selectedGoSoObjCode to set
	 */
	public void setSelectedGoSoObjCode(String selectedGoSoObjCode) {
		this.selectedGoSoObjCode = selectedGoSoObjCode;
	}
	//210801@akmal - Enhancement27624 - Start
	public String getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

	public List<VPmGo> getvPmGolist() {
		return vPmGolist;
	}

	public void setvPmGolist(List<VPmGo> vPmGolist) {
		this.vPmGolist = vPmGolist;
	}

	public String getSaveObject() {
		return saveObject;
	}

	public void setSaveObject(String saveObject) {
		this.saveObject = saveObject;
	}

	public boolean isDisableSelectbtn() {
		return disableSelectbtn;
	}

	public void setDisableSelectbtn(boolean disableSelectbtn) {
		this.disableSelectbtn = disableSelectbtn;
	}
	//210801@akmal - Enhancement27624 - End
	//11022021@azza - Bug #30066 - Start
	public boolean isDisableSelectbtnPmGoDt() {
		return disableSelectbtnPmGoDt;
	}

	public void setDisableSelectbtnPmGoDt(boolean disableSelectbtnPmGoDt) {
		this.disableSelectbtnPmGoDt= disableSelectbtnPmGoDt;
	}
	//11022021@azza - Bug #30066 - End
}
