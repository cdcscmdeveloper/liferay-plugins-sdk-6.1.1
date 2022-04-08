package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.DpTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

/**
 * The Class DpTrackingDiaryBackingBean.
 */
@ManagedBean(name = "dpTrackingDiaryBackingBean")
@RequestScoped
public class DpTrackingDiaryBackingBean extends
BaseBackingBean<DpTrackingDiaryManagedBean> {
	private static Logger log = Logger.getLogger(DpTrackingDiaryBackingBean.class);
	public static final String NAV_TO_SC_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "dpTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * Instantiates a new dp tracking diary backing bean.
	 */
	public DpTrackingDiaryBackingBean() {
	}

	/**
	 * Gets the document type.
	 *
	 * @return the document type
	 */
	public List<SelectItem> getDocumentType() {
		List<SelectItem> items = null;
		List<String> docTypeList = null;

		try {

			items = new ArrayList<SelectItem>();
			docTypeList = new ArrayList<String>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));

			docTypeList.add(ParameterConst.DOCTYPE_SIMPLE_QUOTE);
			docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_INQUIRY);
			docTypeList.add(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT);
			docTypeList.add(ParameterConst.DOCTYPE_REQUEST_NOTE);
			docTypeList.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);

			for (String docType : docTypeList) {
				items.add(new SelectItem(docType, ResourceBundleUtils.getMessage(
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, docType), ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}

	/**
	 * Search status desc.
	 *
	 * @return the list
	 * @throws NamingException the naming exception
	 */
	public SelectItem[] searchStatusDesc() throws NamingException {
		return super.getStatusDropdownValues(
				ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), 
				getManagedBean().getSearchDocumentType());
	}

	/**
	 * Search.
	 */
	public void search() {
		log.info("DpTrackingDiaryBackingBean search");
		if (null == getManagedBean().getSearchDocumentType()) {
			throw new EpException(MessageConst.ERROR_776101);
		}
		getManagedBean().getDpDocumentDataModel().reloadData();
	}

	/**
	 * The Class SearchTrackingDiaryPagingDataModel.
	 */
	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {

		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
			// TODO Auto-generated constructor stub
		}

		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {
			Object[] dpObj = getManagedBean().getDpObj();
			String docType = (String) dpObj[3];
			Long docId = (Long) dpObj[0];
			
			if (docType == null || docId == null) {
				log.info(String.format("DP tracking diary search filter empty[%s=%s][%s=%s]", "docType", docType, "docId", docId));
				return new SearchResultBean<PmTrackingDiary>();
			}

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(), docType);
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(), docId);
			List<Object[]> parents = getParentDocument(docType, docId);
			if(!CollectionUtils.isEmpty(parents)) {
				for(Object[] parent : parents) {
					BaseSearchFilterBean multiFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(), parent[0]), SearchCondition.CONDITION_OR.value(), 
							new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(), parent[1]));
					searchBean.getDataFilterBeans().add(multiFilterBean);
				}
			}

			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);
			setSearchBeanDescription(searchBean, searchFilterBean1, searchFilterBean2);
			
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

	/**
	 * Go to tracking diary search.
	 *
	 * @param dpObj the dp obj
	 * @return the string
	 */
	public void goToTrackingDiarySearch(Object[] dpObj) {
		log.info("goToTrackingDiarySearch");
		getManagedBean().setDpObj(dpObj);
		searchTrackingDiaryDataModel.reloadData();
		FacesContext fc = FacesContext.getCurrentInstance();
		getManagedBean().setFromPageUrl(fc.getViewRoot().getViewId());
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "dpTrackDairyDetailSearch");
	}

	/**
	 * Back to prev.
	 */
	public void backToPrev() {
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, getManagedBean().getFromPageUrl());
	}

	/**
	 * Reset.
	 */
	public void reset(){
		this.getManagedBean().setSearchDocNo(null);
		this.getManagedBean().setSearchDocumentType(null);
		this.getManagedBean().setSearchStatusId(null);
		this.getManagedBean().setSearchDocumentTitle(null);
		this.getManagedBean().setSearchDocumentToDuration(null);
		this.getManagedBean().setSearchDocumentFromDuration(null);
		this.getManagedBean().resetDocumentDataModel();
	}

	/**
	 * Gets the search tracking diary data model.
	 *
	 * @return the search tracking diary data model
	 */
	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	/**
	 * Sets the search tracking diary data model.
	 *
	 * @param searchTrackingDiaryDataModel the new search tracking diary data model
	 */
	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

	private List<Object[]> getParentDocument(String docType, Long docId) {
		List<Object[]> docParents = new ArrayList<Object[]>();

		SourcingIntegrationReqDTO searchReqDTO = new SourcingIntegrationReqDTO();
		searchReqDTO.setDocType(docType);
		searchReqDTO.setDocId(Long.valueOf(docId));

		Long groupDocId = docId;
		String groupDocType = docType;

		while(groupDocId != null && groupDocType != null) {
			SourcingIntegrationResDTO resDTO = getSourcingIntegrationSO().retrieveGroupDocByDocIdAndDocType(searchReqDTO);
			groupDocId = resDTO.getGroupDocId();
			groupDocType = resDTO.getGroupDocType();
			if(resDTO != null) {
				if(groupDocId != null && groupDocType != null) {
					docParents.add(new Object[]{groupDocType, groupDocId});
				}
			}
			searchReqDTO.setDocType(groupDocType);
			searchReqDTO.setDocId(groupDocId);
		}

		return docParents;
	}

	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
	}
	
	/**
	 * set search bean description for tracking purpose
	 *
	 */
	private void setSearchBeanDescription(ISearchBean<PmTrackingDiary> searchBean, SearchFilterBean searchFilterBean1, SearchFilterBean searchFilterBean2) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[%s=%s]", searchFilterBean1.getSearchField(), searchFilterBean1.getSearchValue()));
		sb.append(String.format("[%s=%s]", searchFilterBean2.getSearchField(), searchFilterBean2.getSearchValue()));
		searchBean.setDescription("searchTrackingDiary" + sb.toString() + "[Caller:DpTrackingDiaryBackingBean.SearchTrackingDiaryPagingDataModel]");
	}
}

