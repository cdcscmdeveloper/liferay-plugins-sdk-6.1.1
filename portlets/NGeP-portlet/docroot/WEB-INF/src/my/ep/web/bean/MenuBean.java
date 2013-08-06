package my.ep.web.bean;

import java.util.List;


public class MenuBean {
	private String name;
	private String title;
	private Long imageId;
	private String url;
	private List<MenuBean> children;
	
	public MenuBean(String name, String title, Long imageId, String url) {
		this.name = name;
		this.title = title;
		this.imageId = imageId;
		this.url = url;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the imageId
	 */
	public Long getImageId() {
		return imageId;
	}

	/**
	 * @param imageId the imageId to set
	 */
	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the children
	 */
	public List<MenuBean> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<MenuBean> children) {
		this.children = children;
	}
}
