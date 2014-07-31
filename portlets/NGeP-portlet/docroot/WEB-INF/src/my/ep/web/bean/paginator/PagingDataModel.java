/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.bean.paginator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SecurityFilterUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * The data model for Lazy loaded DataTable from database.
 * 
 * @author Kenny
 * @since 24.06.2014
 *
 * @param <T>
 * 		the object type of the data.
 */
public abstract class PagingDataModel<T> extends LazyDataModel<T> implements IPagingDataModel<T> {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PagingDataModel.class);
	
	private boolean firstLoad;
	private boolean defaultFirstLoad;
	private int first;
	private int pageNo;
	private String componentId;
	private String sortField;
	private SortOrder sortOrder;
	private ISearchBean<T> searchBean;

	/**
	 * PagingDataModel Contructor
	 * 
	 * @param componentId
	 * 		the component id
	 */
	public PagingDataModel(String componentId) {
		// TODO - temporary set first load default as true.
		this(componentId, true);
	}
	
	/**
	 * PagingDataModel Contructor
	 * 
	 * @param componentId
	 * 		the component id
	 * @param defaultFirstLoad
	 * 		the first load flag - default is false.
	 */
	public PagingDataModel(String componentId, boolean defaultFirstLoad) {
		if (StringUtils.isBlank(componentId)) {
			throw new IllegalArgumentException("Invalid componentId.");
		}
		this.componentId = componentId;
		this.defaultFirstLoad = defaultFirstLoad;
		this.firstLoad = defaultFirstLoad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#getRowData(java.lang.String)
	 */
	@Override
	public final T getRowData(String rowKey) {
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>)getWrappedData();
		
		for (T obj : list) {
			if (obj.toString().equals(rowKey))
				return obj;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.primefaces.model.LazyDataModel#getRowKey(java.lang.Object)
	 */
	@Override
	public final Object getRowKey(T key) {
		return key.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getList() {
		return (List<T>)super.getWrappedData();
	}

	/*
	 * Load the data from database.
	 * 
	 * @see org.primefaces.model.LazyDataModel#load(int, int, java.lang.String, org.primefaces.model.SortOrder, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		debugLogger("load");
		
		int pageNo = (int)(first / pageSize) + 1;
		ISearchResultBean<T> resultBean = null;
		
		if (pageNo != this.pageNo) {
			// retrieve new list from DB.
			this.pageNo = pageNo;
			this.sortField = sortField;
			this.sortOrder = sortOrder;
			super.setPageSize(pageSize);
			
			if (firstLoad) {
				this.searchBean = createSearchBean();
				
				resultBean = pageChangeListener(searchBean);
			}  else {
				firstLoad = true;
				
				resultBean = new SearchResultBean<T>();
			}
			
			return updatePagingResults(resultBean);	
		} else {
			// return cache list
			return (List<T>)getWrappedData();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.bean.paginator.IPagingDataModel#reloadData()
	 */
	@Override
	public final void reloadData() {
		debugLogger("reloadData");
		
		UIComponent component = new FacesScopes().findComponentById(
				FacesContext.getCurrentInstance().getViewRoot(), 
				componentId);
		
		if (component instanceof DataTable) {
			DataTable dataTable = (DataTable)component;
			
			dataTable.reset();
			//dataTable.loadLazyData();
		}
		
		resetValue();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.bean.paginator.IPagingDataModel#resetData()
	 */
	@Override
	public final void resetData() {
		debugLogger("resetData");
		
		firstLoad = defaultFirstLoad;
		
		reloadData();
	}
	
	/**
	 * Temporary fix 
	 * Primefaces Issue 6081: java.lang.ArithmeticException: / by zero.
	 * 
	 * @see org.primefaces.model.LazyDataModel#setRowIndex(int)
	 */
	@Override
	public void setRowIndex(int rowIndex) {
		int newRowIndex = -1;
		int pageSize = super.getPageSize();
		
        if(rowIndex == -1 || pageSize == 0)
        	newRowIndex = -1;
        else
        	newRowIndex = (rowIndex % pageSize);
        
        super.setRowIndex(newRowIndex);
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.bean.paginator.IPagingDataModel#createSearchBean()
	 */
	@Override
	public final ISearchBean<T> createSearchBean() {
		String componentId = this.getComponentId();
		SearchBean<T> searchBean = new SearchBean<T>(EpPortletUtil.getLoginUserId(FacesContext.getCurrentInstance()),
				SecurityFilterUtil.getDataFilterScreenMetadatas(componentId));
		
		int pageNo = this.getPageNo();
		int pageSize = this.getPageSize();
		String sortField = this.getSortField();
		SortOrder sortOrder = this.getSortOrder();
		
		debugLogger("pageNo:" + pageNo);
		debugLogger("pageSize:" + pageSize);
		debugLogger("sortField:" + sortField);
		debugLogger("sortOrder:" + sortOrder);
		
		searchBean.setNavigatable(true);
		searchBean.setPageNo(pageNo);
		searchBean.setNoOfRowsPerPage(pageSize);
		
		if (StringUtils.isNotBlank(sortField)) {
			List<SearchOrderBean> sortColumns = new ArrayList<SearchOrderBean>();
			sortColumns.add(new SearchOrderBean(sortField, !SortOrder.DESCENDING.equals(sortOrder)));
			searchBean.setSortColumns(sortColumns);
		}
		
		return searchBean;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.bean.paginator.IPagingDataModel#updatePagingResults(my.ep.biz.bean.search.ISearchResultBean)
	 */
	@Override
	public final List<T> updatePagingResults(ISearchResultBean<T> resultBean) {
		int totalRecord = 0;
		List<T> list = null;
		
		if (resultBean != null) {
			totalRecord = resultBean.getTotalRecord();
			list = resultBean.getResultList();
		}
		
		setRowCount(totalRecord);
		setWrappedData(list);
		
		return list;
	}
	
	/**
	 * This method will invoke when there is page change event.
	 * 
	 * @param searchBean
	 * 		the search bean.
	 * @return	ISearchResultBean instance.
	 */
	protected abstract ISearchResultBean<T> pageChangeListener(ISearchBean<T> searchBean);
	
	private void resetValue() {
		first = 0;
		pageNo = 0;
		sortField = null;
		sortOrder = null;
		searchBean = null;
	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
        log.info(message);
    }

	/**
	 * @return the first
	 */
	public int getFirst() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(int first) {
		this.first = first;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId the componentId to set
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	/**
	 * @return the sortField
	 */
	public String getSortField() {
		return sortField;
	}

	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * @param sortOrder the sortOrder to set
	 */
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * @return the searchBean
	 */
	public ISearchBean<T> getSearchBean() {
		return searchBean;
	}

	/**
	 * @param searchBean the searchBean to set
	 */
	public void setSearchBean(ISearchBean<T> searchBean) {
		this.searchBean = searchBean;
	}
}
