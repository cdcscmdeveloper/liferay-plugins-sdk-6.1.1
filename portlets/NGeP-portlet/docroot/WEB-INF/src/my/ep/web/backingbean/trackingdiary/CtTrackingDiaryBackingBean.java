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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CtTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

/**
 * Backingbean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name="ctTrackingDiaryBackingBean")
@RequestScoped
public class CtTrackingDiaryBackingBean extends BaseBackingBean<CtTrackingDiaryManagedBean>{

	private static Logger log = Logger.getLogger(CtTrackingDiaryBackingBean.class);
	public static final String NAV_TO_CONTRACT_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_CONTRACT_DETAIL_SEARCH = "ctTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void search(){
		if ((null == getManagedBean().getSearchTrasactionType()) 
				&& (getManagedBean().getSearchContractNo() != null && getManagedBean().getSearchContractNo().trim().isEmpty()) 
				&& (getManagedBean().getSearchPhysContractNo() != null && getManagedBean().getSearchPhysContractNo().trim().isEmpty())
				&& (getManagedBean().getSearchLoaNo() != null && getManagedBean().getSearchLoaNo().trim().isEmpty()) 
				&& (getManagedBean().getSearchPhysLoaNo() != null && getManagedBean().getSearchPhysLoaNo().trim().isEmpty())
				&& (getManagedBean().getSearchStatusId() == null)) {
			throw new EpException(MessageConst.ERROR_716006);
		}

		System.out.println("pageChangeListener");
		getManagedBean().getCtDocumentDataModel().reloadData();
	}
	
//	private List<String> getDocTypeListByRole() {
//		List<String> flDocTypes = new ArrayList<String>();
//		if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getManagedBean().getSearchTrasactionType())) {
//			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
//
//				return flDocTypes;
//		}
//		return flDocTypes;
//	}

	public List<SelectItem> getContractType() {
		List<SelectItem> items = null;
		FacesScopes face = new FacesScopes();

		try {

			items = new ArrayList<SelectItem>();
			

			items.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale())));
			
			// Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				items.add(new SelectItem(new String(ParameterConst.CONTRACT_TYPE_MINISTRY), ResourceBundleUtils.getMessage(
						"lbl.ct.ministry.contract", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale())));				
			}
			// Central Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				items.add(new SelectItem(new String(ParameterConst.CONTRACT_TYPE_CENTRAL), ResourceBundleUtils.getMessage(
						"lbl.ct.central.contract", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale())));
				items.add(new SelectItem(new String(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR), ResourceBundleUtils.getMessage(
						"lbl.ct.central.contract.circular", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale())));
			}
			// Panel Contract Administrator
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) {			
				items.add(new SelectItem(new String(ParameterConst.DOCTYPE_PANEL_CIRCULAR), ResourceBundleUtils.getMessage(
					"lbl.ct.panel.circular", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale())));
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

	public SelectItem[] getContractViewType() {
		SelectItem[] items = null;
		//FacesScopes face = new FacesScopes();

		try {

			items = new SelectItem[2];

			items[0] = new SelectItem(new String("TRUE"), new String("GROUP DOC TYPE"));
			items[1] = new SelectItem(new String("FALSE"), new String("VERSION DOC TYPE"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
	}

	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchViewType(null);
		reset();
		if(id!=null) {
			if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//				this.getManagedBean().setCheckDocType(true);
			}	
		}
	}

	public void selectedDoctype(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		
		// reset everything to false
		//this.getManagedBean().setCheckDocNo(true);
		if(id!=null && id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//			this.getManagedBean().setCheckDocType(true);
		}else{
//			this.getManagedBean().setCheckDocType(false);
		}
		
		reset();
		
		if(id!=null) {
		}
		getManagedBean().setSearchViewType(id);
	}
	public SelectItem[] searchStatusDesc() throws NamingException {
		StatusReqDTO reqDTO = new StatusReqDTO();
		IStatusSO statusSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.moduleCode",
				SearchOperator.OPERATOR_EQUALS.value(),
				"CT"));
		List<String> docTypeList = new ArrayList<String>();
//		if(id.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
//		this.getManagedBean().setCheckPoDocType(true);
//		getProcurementTypeList();
//	}else if(id.equals(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
//		this.getManagedBean().setCheckDoDocType(true);
//	}else if(id.equals(ParameterConst.DOCTYPE_INVOICE)){
//		this.getManagedBean().setCheckInDocType(true);
		docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
		docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
		docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
		docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
		docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		docTypeList.add(ParameterConst.DOCTYPE_REQUEST_FACTORING);
		docTypeList.add(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
		docTypeList.add(ParameterConst.DOCTYPE_AGREEMENT);
		docTypeList.add(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
		docTypeList.add(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		docTypeList.add(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
		docTypeList.add(ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.docType",
				SearchOperator.OPERATOR_IN.value(),
				docTypeList));
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"languageCode",
				SearchOperator.OPERATOR_EQUALS.value(),
				getFacesScopes().getUserLocale().getLanguage()));
		
		reqDTO.setStatusDescSearchBean(searchBean);
		StatusResDTO resDTO = statusSO.searchStatusDesc(reqDTO);
		
		//List<PmStatusDesc> list = resDTO.getStatusDescList();
		SelectItem[] items = null;

		try {
			List<PmStatusDesc> statusDescs = resDTO.getStatusDescList();

			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					getFacesScopes().getUserLocale()));
			int i = 1;
			for (PmStatusDesc statusDesc : statusDescs) {
				items[i++] = new SelectItem(statusDesc.getPmStatus()
						.getStatusId(), statusDesc.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}

		return items;
//		=MC o CC, get all CT status except DOC_TYPE=CQ,PQ,RC;
//		If transaction type=CQ, get CT status where it's doctype=CQ,RC; 
//		If transaction type=PQ, get CT status where it's doctype=PQ,RC
//		System.out.println("list: " + list.size());

//		for (PmStatusDesc statusDesc : list) {
//			System.out.println(statusDesc.getStatusDescId()
//					 + "|" + statusDesc.getStatusName()
//					 + "|" + statusDesc.getPmStatus().getStatusId());
//		}
//		
//		System.out.println("done");
	}
	public void reset(){
		this.getManagedBean().setSearchTrasactionType(null);
		this.getManagedBean().setSearchViewType(null);
		this.getManagedBean().setSearchContractNo(null);
		this.getManagedBean().setSearchLoaNo(null);
		this.getManagedBean().setSearchPhysContractNo(null);
		this.getManagedBean().setSearchPhysLoaNo(null);
		this.getManagedBean().setSearchContractToDuration(null);
		this.getManagedBean().setSearchContractFromDuration(null);
		this.getManagedBean().resetDocumentDataModel();
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
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocType()));
//			searchBean.getDataFilterBeans().add(new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocId()));
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocType());
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocId());
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocType());
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvCtDocument().getDocId());
			
			BaseSearchFilterBean multiFilterBean1 = new SearchMultiFilterBean(searchFilterBean1, searchFilterBean2);
			BaseSearchFilterBean multiFilterBean2 = new SearchMultiFilterBean(searchFilterBean3, SearchCondition.CONDITION_OR.value(), searchFilterBean4);
			BaseSearchFilterBean multiFilterBean3 = new SearchMultiFilterBean(multiFilterBean1, SearchCondition.CONDITION_OR.value(), multiFilterBean2);
			searchBean.getDataFilterBeans().add(multiFilterBean3);
			System.out.println("getCtDocumentDataModel docId "+ getManagedBean().getvCtDocument().getDocId());

			reqDTO.setSearchBean(searchBean);
			TrackingDiaryResDTO resDTO = new TrackingDiaryResDTO();
			try {
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject("cc", ITrackingDiarySO.class);
				resDTO = trackingDiarySO.retrieveTrackingDiaryBySearchBean(reqDTO);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("resDTO.getResultBean()====================================: "+ resDTO.getResultBean().getTotalRecord());
			return resDTO.getResultBean();
			
		}
	}

	public String goToTrackingDiarySearch(VCtDocument vCtDocument) {
		System.out.println("**********vCtDocument************"+ vCtDocument.getDocId());
		getManagedBean().setvCtDocument(vCtDocument);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CONTRACT_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public String backToPrev() {

		return NAV_TO_CONTRACT_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}
