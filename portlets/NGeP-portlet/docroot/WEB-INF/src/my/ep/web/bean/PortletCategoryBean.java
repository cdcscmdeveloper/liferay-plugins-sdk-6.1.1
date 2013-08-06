package my.ep.web.bean;

import java.util.List;
import java.util.Set;

public class PortletCategoryBean {
	private String id;
	private String name;
	private Set<String> portletIds;
	private List<PortletCategoryBean> children;
	
	public PortletCategoryBean(String id, String name, Set<String> portletIds) {
		this.id = id;
		this.name = name;
		this.portletIds = portletIds;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the portletIds
	 */
	public Set<String> getPortletIds() {
		return portletIds;
	}

	/**
	 * @param portletIds the portletIds to set
	 */
	public void setPortletIds(Set<String> portletIds) {
		this.portletIds = portletIds;
	}

	/**
	 * @return the children
	 */
	public List<PortletCategoryBean> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<PortletCategoryBean> children) {
		this.children = children;
	}
}
