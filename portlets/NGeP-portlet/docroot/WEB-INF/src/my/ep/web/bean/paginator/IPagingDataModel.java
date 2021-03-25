package my.ep.web.bean.paginator;

import java.util.List;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;




public interface IPagingDataModel<T> {
	/**
	 * Reload the data from database.
	 */
	public void reloadData();
	
	/**
	 * Reset and the data from database.
	 */
	public void resetData();
	
	/**
	 * Create search bean by data model.
	 * 
	 * @param dataModel
	 * 			the data model.
	 * @return	ISearchBean instance.
	 */
	public ISearchBean<T> createSearchBean();
	
	/**
	 * Get search bean
	 * 
	 * @return	ISearchBean instance.
	 */
	public ISearchBean<T> getSearchBean();
	
	/**
	 * Update the search results into data model.
	 * 
	 * @param dataModel
	 * 			the data model.
	 * @param resultBean
	 * 			the search result bean.
	 * @return the list of T instance
	 */
	public List<T> updatePagingResults(ISearchResultBean<T> resultBean);
	
	/**
	 * Get list of object from data model.
	 * 
	 * @return the list of T instance
	 */
	public List<T> getList();
	
	/**
	 * Get total record for search result.
	 * 
	 * @return the total record
	 */
	public int getRowCount();
	
	/**
	 * Reload the data from database.
	 */
	public void reloadDataWithoutReset();

	/**
	 * Reload the datatable paging.
	 */
	 //added for #28643 to only reset paging value
	public void reloadDatatable();
	
}
