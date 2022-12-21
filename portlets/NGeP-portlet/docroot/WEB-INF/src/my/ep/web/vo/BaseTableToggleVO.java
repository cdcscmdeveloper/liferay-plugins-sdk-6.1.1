/**
 * Copyright © 2012 Archisoft. All rights reserved.
*/
package my.ep.web.vo;

import java.util.ArrayList;
import java.util.List;
/* PrimeFaces impact - kenny - 20130129
import my.ac.web.bean.common.collapsiblesubtable.IExpandable;*/
import my.ep.persistence.entity.BaseEntity;


public class BaseTableToggleVO /* PrimeFaces impact - kenny - 20130129 implements IExpandable*/ {

	private BaseEntity baseEntity;
	
	private BaseTableToggleVO parent;
	private List<BaseTableToggleVO> nodes;
	
	private boolean expandable;
	private int index;
			
	public String getKey() {
		StringBuilder key = new StringBuilder();
		
		if (parent != null) {
			key.append(parent.getKey());
			key.append(".");
		}
		key.append(index);
		
		return key.toString();
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	public BaseEntity getBaseEntity() {
		return baseEntity;
	}

	public void setBaseEntity(BaseEntity baseEntity) {
		this.baseEntity = baseEntity;
	}

	public BaseTableToggleVO getParent() {
		return parent;
	}

	public void setParent(BaseTableToggleVO parent) {
		this.parent = parent;
	}

	public List<BaseTableToggleVO> getNodes() {
		if (nodes == null) {
			nodes = new ArrayList<BaseTableToggleVO>();
		}
		return nodes;
	}

	public void setNodes(List<BaseTableToggleVO> nodes) {
		this.nodes = nodes;
	}	
}