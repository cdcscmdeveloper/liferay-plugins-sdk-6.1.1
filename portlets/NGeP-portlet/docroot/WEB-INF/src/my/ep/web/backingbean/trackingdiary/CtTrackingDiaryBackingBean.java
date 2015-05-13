/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CtTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backingbean for Contract Tracking Diary
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name="ctTrackingDiaryBackingBean")
@RequestScoped
public class CtTrackingDiaryBackingBean extends BaseBackingBean<CtTrackingDiaryManagedBean>{
	
	public static final String NAV_TO_CONTRACT_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CONTRACT_DETAIL_SEARCH = "ctTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * The method used for generating a list of available transaction type base on user role
	 * for contract module as selection.
	 * 
	 * @return selectItems the selection list of available transaction type
	 */
	public List<SelectItem> getContractType() {
		List<SelectItem> items = null;

		try {
			items = new ArrayList<SelectItem>();			

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));
			
			// Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				items.add(new SelectItem(new String(ParameterConst.CONTRACT_TYPE_MINISTRY), ResourceBundleUtils.getMessage(
						"lbl.ct.ministry.contract", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));				
			}
			// Central Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				items.add(new SelectItem(new String(ParameterConst.CONTRACT_TYPE_CENTRAL), ResourceBundleUtils.getMessage(
						"lbl.ct.central.contract", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
				items.add(new SelectItem(new String(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR), ResourceBundleUtils.getMessage(
						"lbl.ct.central.contract.circular", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale())));
			}
			// Panel Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) {			
				items.add(new SelectItem(new String(ParameterConst.DOCTYPE_PANEL_CIRCULAR), ResourceBundleUtils.getMessage(
					"lbl.ct.panel.circular", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
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
	 * The method used for generating a list of available document type base on
	 * selected transaction type as selection.
	 * 
	 * @return selectItems the selection list of available document type
	 */
	public List<SelectItem> getContractDocType() {
		List<SelectItem> items = null;
		List<String> docTypeList = null;

		try {
			items = new ArrayList<SelectItem>();
			docTypeList = new ArrayList<String>();
			
			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));
			
			// Document type list for MC & CC
			//if (getManagedBean().getSearchTransactionType() != null && getManagedBean().getSearchTransactionType().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)
			//		|| getManagedBean().getSearchTransactionType().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
				docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
				docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
				docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
				docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
				docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
				docTypeList.add(ParameterConst.DOCTYPE_REQUEST_FACTORING);
				docTypeList.add(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
				docTypeList.add(ParameterConst.DOCTYPE_AGREEMENT);
				docTypeList.add(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
			//}
			// Document type list for CQ & PQ
//			else if (getManagedBean().getSearchTransactionType() != null && getManagedBean().getSearchTransactionType().equalsIgnoreCase(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR)
//					|| getManagedBean().getSearchTransactionType().equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR)) {
//				docTypeList.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
//				docTypeList.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
//				docTypeList.add(ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
//			}
			
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
	 * The method used for searching the contract tracking diary.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 */
	public void search(){
		getManagedBean().getCtDocumentDataModel().reloadData();
	}
	
	/**
	 * The method used to reset the search criteria for contract tracking diary
	 * except transaction type.
	 */
	public void resetSearchCriteria(){
		this.getManagedBean().setSearchDocNo(null);
		this.getManagedBean().setSearchDocType(null);
		this.getManagedBean().setSearchLoaNo(null);
		this.getManagedBean().setSearchPhysicalDocNo(null);
		this.getManagedBean().setSearchPhysicalLoaNo(null);
	}
	
	/**
	 * The method used to reset all search criteria and search result.
	 */
	public void reset(){
		this.getManagedBean().setSearchTransactionType(null);
		resetSearchCriteria();
		this.getManagedBean().resetCtDocumentDataModel();
	}
	
	/**
	 * @return the searchTrackingDiaryDataModel
	 */
	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	/**
	 * @param searchTrackingDiaryDataModel the searchTrackingDiaryDataModel to set
	 */
	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}
	
	/**
	 * Pagination for available tracking diary details list
	 */
	private class SearchTrackingDiaryPagingDataModel extends PagingDataModel<PmTrackingDiary> {
		private static final long serialVersionUID = 1L;

		public SearchTrackingDiaryPagingDataModel() {
			super("trackingDiaryTableID");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		protected ISearchResultBean<PmTrackingDiary> pageChangeListener(
				ISearchBean<PmTrackingDiary> searchBean) {

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = null;
			SearchFilterBean searchFilterBean2 = null;
			SearchFilterBean searchFilterBean3 = null;			
			BaseSearchFilterBean multiFilterBean1 = null;
			
			searchFilterBean1 = new SearchFilterBean("groupDocType", SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getvCtDocument().getDocType());
			searchFilterBean2 = new SearchFilterBean("groupId", SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getvCtDocument().getDocId());
			
			multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);			
			
			if (getManagedBean().getSearchDocType() != null) {
				searchFilterBean3 = new SearchFilterBean("docType", SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getSearchDocType());
				multiFilterBean1 = new SearchMultiFilterBean(multiFilterBean1, searchFilterBean3);				
							
			}
			searchBean.getDataFilterBeans().add(multiFilterBean1);
			
			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				e.printStackTrace();
			}return resDTO.getResultBean();
			
		}
	}

	/**
	 * The method used to navigate to tracking diary details screen.
	 * 
	 * @return the navigate URL 
	 */
	public String goToTrackingDiarySearch(VCtDocument vCtDocument) {
		getManagedBean().setvCtDocument(vCtDocument);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CONTRACT_DETAIL_SEARCH + "?faces-redirect=true";
	}

	/**
	 * The method used to navigate to previous screen.
	 * 
	 * @return the navigate URL 
	 */
	public String backToPrev() {
		return NAV_TO_CONTRACT_SEARCH + "?faces-redirect=true";
	}
}
