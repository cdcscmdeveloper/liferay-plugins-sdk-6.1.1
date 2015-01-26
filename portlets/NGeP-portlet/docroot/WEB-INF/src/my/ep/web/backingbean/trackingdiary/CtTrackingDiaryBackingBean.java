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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
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
	private VCtDocument vCtDocument;
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void search(){
//		if(null == getManagedBean().getSearchTrasactionType()){ 
//			throw new EpException(MessageConst.ERROR_740023);
//		}else if((null ==getManagedBean().getSearchViewType() 
//				&& "".equalsIgnoreCase(getManagedBean().getSearchContractNo()) 
//				&& "".equalsIgnoreCase(getManagedBean().getSearchLoaNo()))){
//			throw new EpException(MessageConst.ERROR_740023);
//		MessageConst.ERROR_715026
//		}
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

	public SelectItem[] getContractType() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {

			items = new SelectItem[5];

			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			items[1] = new SelectItem(new String("CC"), new String("Central Contract"));
			items[2] = new SelectItem(new String("MC"), new String("Ministry Contract"));
			items[3] = new SelectItem(new String("CQ"), new String("Central Circular"));
			items[4] = new SelectItem(new String("PQ"), new String("Panel Circular"));
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
		FacesScopes face = new FacesScopes();

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

//	public SelectItem[] getDocumentTypeDropDown() {
//		List<SelectItem> selectedItemList = new ArrayList<SelectItem>();
//		try {
//			SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FULFILMENT_DOCUMENT_TYPE);
//			selectedItemList.add(items[0]);
//			List<SelectItem> itemList = new ArrayList<SelectItem>();
//		    for(SelectItem selectItem : items) {
//		    	itemList.add(selectItem);
//		    }
//			List<String> flDocTypes = getDocTypeListByRole();
//			List<String> selectedItemCode = new ArrayList<String>();
//		    for(SelectItem selectItem : itemList) {
//		    	if(flDocTypes.contains(selectItem.getValue()) && !selectedItemCode.contains(selectItem.getValue().toString())) {
//		    		selectedItemCode.add(selectItem.getValue().toString());
//		    		selectedItemList.add(selectItem);
//		    	}
//		    }
//		} catch (Exception e) {
//			e.printStackTrace();
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(),e.getMessage()));
//		}
//	    return selectedItemList.toArray(new SelectItem[selectedItemList.size()]);
//	}
	
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
			/*if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				this.getManagedBean().setCheckDocType(true);
			}else */
//			if(getManagedBean().getSearchOrderType().equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//				this.getManagedBean().setCheckDocType(true);
//			}
//			if(id.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
//				this.getManagedBean().setCheckPoDocType(true);
//				getProcurementTypeList();
//			}else if(id.equals(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
//				this.getManagedBean().setCheckDoDocType(true);
//			}else if(id.equals(ParameterConst.DOCTYPE_INVOICE)){
//				this.getManagedBean().setCheckInDocType(true);
//			}
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
		docTypeList.add("FD");
		docTypeList.add("FA");
		docTypeList.add("FT");
		docTypeList.add("FS");
		docTypeList.add("FU");
		docTypeList.add("RF");
		docTypeList.add("CB");
		docTypeList.add("AC");
		docTypeList.add("SA");
		docTypeList.add("PQ");
		docTypeList.add("CQ");
		docTypeList.add("RC");
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"pmStatus.docType",
				SearchOperator.OPERATOR_IN.value(),
				docTypeList));
		searchBean.getDataFilterBeans().add(new SearchFilterBean(
				"languageCode",
				SearchOperator.OPERATOR_EQUALS.value(),
				"en"));
		
		reqDTO.setStatusDescSearchBean(searchBean);
		StatusResDTO resDTO = statusSO.searchStatusDesc(reqDTO);
		
		List<PmStatusDesc> list = resDTO.getStatusDescList();
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();

		try {
			List<PmStatusDesc> statusDescs = resDTO.getStatusDescList();

			items = new SelectItem[statusDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
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
//		this.getManagedBean().setSearchTitle(null);
//		this.getManagedBean().setSearchSuppName(null);
//		this.getManagedBean().setSearchSuppDoRef(null);
//		this.getManagedBean().setSearchSuppInvRef(null);
//		this.getManagedBean().setSearchContractNo(null);
//		this.getManagedBean().setSearchContractToDuration(null);
//		this.getManagedBean().setSearchContractFromDuration(null);
//		this.getManagedBean().setSearchLoaNo(null);
//		this.getManagedBean().setSearchCircularNo(null);
//		this.getManagedBean().setSearchTrasactionType(null);
//		this.getManagedBean().setSearchViewType(null);
//		this.getManagedBean().setSearchPhysContractNo(null);
//		this.getManagedBean().setSearchPhysLoaNo(null);
		
		renewSesionObject();
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
			searchBean.getDataFilterBeans().add(new SearchFilterBean("docNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractNo()));
			System.out.println("TrackingDiaryReqDTO" + vCtDocument.getDocNo());

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
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
//		getManagedBean().setSmSupplier(smSupplier);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_CONTRACT_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}

}
