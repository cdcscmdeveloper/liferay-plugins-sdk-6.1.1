package my.ep.web.managedbean.ct.circular.panel.group.item;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Panel Group Item Managed Bean
 * 
 * @author alam
 * @since 27/5/2015
 */
@ManagedBean
@SessionScoped
public class PanelGroupItemManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isLazy;
	private String groupName;
	private Long status;
	private CtPanelItemGroup ctPanelItemGroup ;

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
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	
	/**
	 * @return the ctPanelItemGroup
	 */
	public CtPanelItemGroup getCtPanelItemGroup() {
		return ctPanelItemGroup;
	}
	
	/**
	 * @param ctPanelItemGroup the ctPanelItemGroup to set
	 */
	public void setCtPanelItemGroup(CtPanelItemGroup ctPanelItemGroup) {
		this.ctPanelItemGroup = ctPanelItemGroup;
	}
}
