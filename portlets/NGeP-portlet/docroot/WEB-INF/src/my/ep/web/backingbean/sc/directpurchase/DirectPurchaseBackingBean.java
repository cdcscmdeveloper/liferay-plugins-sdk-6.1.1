/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.sc.directpurchase;

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
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.purchaseindefault.PurchaseInDefaultBackingBean;
import my.ep.web.backingbean.sc.purchaseinquiry.PurchaseInquiryBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.backingbean.sc.simplequote.SimpleQuoteBackingBean;
import my.ep.web.backingbean.trackingdiary.TrackingDiarySearchBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.sc.directpurchase.DirectPurchaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.springframework.util.CollectionUtils;
// TODO: Auto-generated Javadoc

/**
 * The Class DirectPurchaseBackingBean.
 *
 * @author Sri
 * @since 29/12/2015
 */
@ManagedBean(name = "directPurchaseBackingBean")
@RequestScoped
public class DirectPurchaseBackingBean extends SourcingBaseBackingBean<DirectPurchaseManagedBean> {
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

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

	public List<SelectItem> searchStatusDesc() throws NamingException {
		List<SelectItem> items = null;

		try {
			items = new ArrayList<SelectItem>();

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale())));

			List<PmStatusDesc> statusDescs = StatusHelper.getInstance().getStatusDescList(
					ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase(), getManagedBean().getSearchDocumentType());
			for (PmStatusDesc statusDesc : statusDescs) {
				items.add(new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName()));
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
	 * Search.
	 */
	public void search() {
		debugLogger("DpTrackingDiaryBackingBean search");
		if ((null == getManagedBean().getSearchDocumentType())) {
			throw new EpException(MessageConst.ERROR_776101);
		}
		getManagedBean().getDpDocumentDataModel().reloadData();
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
			Object[] dpObj = getManagedBean().getDpObj();
			String docType = (String) dpObj[3];
			Long docId = (Long) dpObj[0];

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
	 * View doc.
	 *
	 * @param dpObj the dp obj
	 */
	public void viewDoc(Object[] dpObj){
		debugLogger("viewDoc");
		long docId = (long) dpObj[0];
		String docNo = (String) dpObj[1];
		String docType = (String) dpObj[3];
		long statusId = (long) dpObj[4];
		
		TrackingDiarySearchBackingBean diarySearchBackingBean = new TrackingDiarySearchBackingBean();
		diarySearchBackingBean.getManagedBean().setDocNo(docNo);
		diarySearchBackingBean.getManagedBean().setDocType(docType);
		diarySearchBackingBean.getManagedBean().setStatusId(statusId);
		
		if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_REQUEST_NOTE)){
			RequestNoteBackingBean rnBackingBean = new RequestNoteBackingBean();
			rnBackingBean.viewRequestNote(docId);
			rnBackingBean.getManagedBean().setDpObj(dpObj);
			rnBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_INQUIRY)){
			PurchaseInquiryBackingBean prInqBackingBean = new PurchaseInquiryBackingBean();
			prInqBackingBean.viewPurchaseInquiry(docId);
			prInqBackingBean.getManagedBean().setDpObj(dpObj);
			prInqBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_IN_DEFAULT)){
			PurchaseInDefaultBackingBean pidBackingBean = new PurchaseInDefaultBackingBean();
			pidBackingBean.viewPurchaseInDefault(docId);
			pidBackingBean.getManagedBean().setDpObj(dpObj);
			pidBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_SIMPLE_QUOTE)){
			SimpleQuoteBackingBean sqBackingBean = new SimpleQuoteBackingBean();
			sqBackingBean.viewQuote(docId);
			sqBackingBean.getManagedBean().setDpObj(dpObj);
			sqBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);

		} else if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_LETTER_OF_AWARD)){
			LOADetailBackingBean loaDetailBackingBean = new LOADetailBackingBean();
			loaDetailBackingBean.viewLOA(docId);
			loaDetailBackingBean.getManagedBean().setDpObj(dpObj);
			loaDetailBackingBean.getManagedBean().setTrackingDairyView(Boolean.TRUE);
		}
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

	/**
	 * Gets the user name.
	 *
	 * @param userId the user id
	 * @return the user name
	 */
	public String getUserName(long userId){
		String userName = "";
		UserReqDTO userReqDTO  = new UserReqDTO();
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		userReqDTO.setUserId(userId);
		PmUser pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
		if(pmUser != null){
			userName = pmUser.getUserName();
		}
		return userName;
	}

	private ISourcingIntegrationSO getSourcingIntegrationSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * Debug logger.
	 *
	 * @param message the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
}