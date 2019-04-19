package my.ep.web.bean.tree;

import java.io.Serializable;

public class TreeNodeBean<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean selected;
	private String type;
	private T object;
	
	public TreeNodeBean() {
	}
	
	public TreeNodeBean(String type, T object) {
		this.type = type;
		this.object = object;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
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
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}
}
