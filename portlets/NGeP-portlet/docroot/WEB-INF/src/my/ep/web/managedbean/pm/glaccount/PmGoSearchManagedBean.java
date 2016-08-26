package my.ep.web.managedbean.pm.glaccount;

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
	
	
}
