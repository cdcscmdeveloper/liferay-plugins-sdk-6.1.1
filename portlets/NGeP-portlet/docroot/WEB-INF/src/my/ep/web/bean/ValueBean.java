package my.ep.web.bean;

import my.ep.persistence.entity.BaseEntity;

public class ValueBean<T extends BaseEntity> {
	private T entity;
	private boolean brandNew;
	private boolean selected;
	
	public ValueBean(T entity) {
		this.entity = entity;
	}

	/**
	 * @return the entity
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(T entity) {
		this.entity = entity;
	}

	/**
	 * @return the brandNew
	 */
	public boolean isBrandNew() {
		return brandNew;
	}

	/**
	 * @param brandNew the brandNew to set
	 */
	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
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
}
