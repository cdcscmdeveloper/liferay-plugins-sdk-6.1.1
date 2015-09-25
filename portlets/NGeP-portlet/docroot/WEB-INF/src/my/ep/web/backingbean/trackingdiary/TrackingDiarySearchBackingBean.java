/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.TrackingDiarySearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backingbean for Tracking Diary Search
 * 
 * @author GiriBabu Borugadda
 * @since 22/09/2015
 */
@ManagedBean(name="trackingDiarySearchBackingBean")
@RequestScoped
public class TrackingDiarySearchBackingBean extends BaseBackingBean<TrackingDiarySearchManagedBean>{

	private static Logger log = Logger.getLogger(TrackingDiarySearchBackingBean.class);
	public static final String NAV_TO_DETAIL_SEARCH = "/xhtml/trackingdiary/trackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	public String goToTrackingDiarySearch(String docType,String docNo,Long docId,Long statusId) {
		getManagedBean().setDocType(docType);
		getManagedBean().setDocNo(docNo);
		getManagedBean().setDocId(docId.toString());
		getManagedBean().setStatusId(statusId);
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}

	
	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getDocType());
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getDocId());
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getDocType());
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getDocId());
			
			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);
			log.info("Fl TrackingDiary docId " + getManagedBean().getDocId());
			log.info("Fl TrackingDiary DocType " + getManagedBean().getDocType());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resDTO.getResultBean();
			
		}
	}


	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}
	/**
	 * Back to previous page.
	 *
	 * @return the string
	 */
	public String backToPrev() {
		return getManagedBean().getFromPageUrl();
	}
	
	/**
	 * Gets the portlet title.
	 *
	 * @return the portlet title
	 */
	public String getPortletTitle() {
		String portletTitle = "Tracking Diary";
		return portletTitle;
	}
	  
}
