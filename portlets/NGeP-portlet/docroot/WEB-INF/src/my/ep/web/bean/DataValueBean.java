package my.ep.web.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.BaseEntity;

public class DataValueBean<T> {
	private static final int LIST_DISPLAYED = 0;
	private static final int LIST_REMOVED = 1;
	
	private Object parent;
	private Map<Object, List<T>[]> dataMap;
	
	public DataValueBean() {
		this.dataMap = new HashMap<Object, List<T>[]>();
	}
	
	public boolean containsKey(BaseEntity parent) {
		return dataMap.containsKey(parent);
	}
	
	public void putToList(Object parent, List<T> displayedList) {
		@SuppressWarnings("unchecked")
		List<T>[] listArr = new List[]{displayedList, new ArrayList<T>()};
		
		this.parent = parent;
		dataMap.put(parent, listArr);
	}
	
	public List<T> getAllListByParent(Object parent) {
		List<T> list = new ArrayList<T>();
		List<T>[] listArr = dataMap.get(parent);
		
		
		if (listArr != null) {
			// Displayed List
			list.addAll(listArr[LIST_DISPLAYED]);
			
			// Removed List
			list.addAll(listArr[LIST_REMOVED]);
		}
		
		return list;
	}

	/**
	 * @return the parent
	 */
	public Object getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Object parent) {
		this.parent = parent;
	}

	/**
	 * @return the displayedList
	 */
	public List<T> getDisplayedList() {
		List<T>[] listArr = dataMap.get(parent);
		
		if (listArr == null) {
			return Collections.emptyList();
		} else {
			return listArr[LIST_DISPLAYED];
		}
	}

	/**
	 * @return the removedList
	 */
	public List<T> getRemovedList() {
		List<T>[] listArr = dataMap.get(parent);
		
		if (listArr == null) {
			return Collections.emptyList();
		} else {
			return listArr[LIST_REMOVED];
		}
	}
}
