package my.ep.web.bean.paginator;

import java.util.List;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;




public interface IPagingDataModel<T> {
	/**
	 * Reset and reload the data from database.
	 */
	public void reloadData();
	
	/**
	 * Create search bean by data model.
	 * 
	 * @param dataModel
	 * 			the data model.
	 * @return	ISearchBean instance.
	 */
	public ISearchBean<T> createSearchBean();
	
	/**
	 * Update the search results into data model.
	 * 
	 * @param dataModel
	 * 			the data model.
	 * @param resultBean
	 * 			the search result bean.
	 * @return
	 */
	public List<T> updatePagingResults(ISearchResultBean<T> resultBean);
}
