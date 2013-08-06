package my.ep.web.bean;

import java.util.Set;


public class RoleBean {
	private String name;
	private String description;
	private Set<String> portletIds;
	
	public RoleBean(String name, String description) {
		this.name = name;
		this.description = description;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
}
