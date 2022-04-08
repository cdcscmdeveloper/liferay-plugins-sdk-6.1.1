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

import my.ep.biz.bean.search.BaseSearchFilterBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.fl.VFlDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.managedbean.trackingdiary.FlTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backingbean for Fulfillment document
 * 
 * @author Alvin
 * @since 2/11/2013
 */
@ManagedBean(name="flTrackingDiaryBackingBean")
@RequestScoped
public class FlTrackingDiaryBackingBean extends BaseBackingBean<FlTrackingDiaryManagedBean>{

	private static Logger log = Logger.getLogger(FlTrackingDiaryBackingBean.class);
	public static final String NAV_TO_FL_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_DETAIL_SEARCH = "flTrackingDiaryDetailSearch.xhtml";
	private IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel 	= new SearchTrackingDiaryPagingDataModel();

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	public void search(){
		if(null == getManagedBean().getSearchOrderType()){ 
			throw new EpException(MessageConst.ERROR_740023);
		}else if((null ==getManagedBean().getSearchDocType() 
				&& "".equalsIgnoreCase(getManagedBean().getSearchDocNo()))){
			throw new EpException(MessageConst.ERROR_740039, new String[]{
					ResourceBundleUtils.getMessage("lbl.fl.doc.maintain.doc.type",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()),
					ResourceBundleUtils.getMessage("lbl.fl.doc.maintain.circular.doc.no",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale())});
		}
		System.out.println("search");
		getManagedBean().getFlDocumentDataModel().reloadData();
	}
	
	public SelectItem[] getDocumentTypeDropDown() {
		List<SelectItem> selectedItemList = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.FULFILMENT_DOCUMENT_TYPE);
			selectedItemList.add(items[0]);
			List<SelectItem> itemList = new ArrayList<SelectItem>();
		    for(SelectItem selectItem : items) {
		    	itemList.add(selectItem);
		    }
			List<String> flDocTypes = getDocTypeListByRole();
			List<String> selectedItemCode = new ArrayList<String>();
		    for(SelectItem selectItem : itemList) {
		    	if(flDocTypes.contains(selectItem.getValue()) && !selectedItemCode.contains(selectItem.getValue().toString())) {
		    		selectedItemCode.add(selectItem.getValue().toString());
		    		selectedItemList.add(selectItem);
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(),e.getMessage()));
		}
	    return selectedItemList.toArray(new SelectItem[selectedItemList.size()]);
	}
	
//	public void selectedUser(AjaxBehaviorEvent e) {
//		// doing some stuff here
//		//System.out.println("====enter process checked===="+ getManagedBean().getTempLoginId());
//		String id = (String) e.getComponent().getAttributes().get("value");
//		System.out.println("=======get id value=====" + id);
//		getManagedBean().setTempLoginId(id);
//	}
	
	public void selectedDoctype(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		
		// reset everything to false
		//this.getManagedBean().setCheckDocNo(true);
//		if(id!=null && id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//			this.getManagedBean().setCheckDocType(true);
//		}else{
//			this.getManagedBean().setCheckDocType(false);
//		}
		
//		this.getManagedBean().setCheckPoDocType(false);
//		this.getManagedBean().setCheckDoDocType(false);
//		this.getManagedBean().setCheckInDocType(false);
//		this.getManagedBean().setCheckDoInDocType(false);
//		reset();
		
		if(id!=null) {
			/*if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
				this.getManagedBean().setCheckDocType(true);
			}else */
//			if(getManagedBean().getSearchOrderType().equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//				this.getManagedBean().setCheckDocType(true);
//			}
//			if(id.equals(ParameterConst.DOCTYPE_PURCHASE_ORDER)){
//				this.getManagedBean().setCheckPoDocType(true);
////				getProcurementTypeList();
//			}else if(id.equals(ParameterConst.DOCTYPE_DELIVERY_ORDER)){
//				this.getManagedBean().setCheckDoDocType(true);
//			}else if(id.equals(ParameterConst.DOCTYPE_INVOICE)){
//				this.getManagedBean().setCheckInDocType(true);
//			}
		}
		getManagedBean().setSearchDocType(id);
	}
	
	public void selectedOrderType(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		getManagedBean().setSearchDocType(null);

		// reset everything to false
		//this.getManagedBean().setCheckDocNo(false);
//		this.getManagedBean().setCheckDocType(false);
//		this.getManagedBean().setCheckPoDocType(false);
//		this.getManagedBean().setCheckDoDocType(false);
//		this.getManagedBean().setCheckInDocType(false);
//		this.getManagedBean().setCheckDoInDocType(false);
		resetSearchCriteria();
		if(id!=null) {
			if(id.equals(ParameterConst.DOCTYPE_CONTRACT_ORDER)){
//				this.getManagedBean().setCheckDocType(true);
			}	
		}
		
		
	}
	private void resetSearchCriteria() {
		getManagedBean().setSearchDocNo(null);
		getManagedBean().setStatusDescId(null);
		getManagedBean().setSearchSuppName(null);
		getManagedBean().setSearchSuppDoRef(null);
		getManagedBean().setSearchSuppInvRef(null);
		getManagedBean().setSearchLoaNo(null);
		getManagedBean().setSearchContractNo(null);
		getManagedBean().setSearchOrderNo(null);
		getManagedBean().setSearchPhysContractNo(null);
		getManagedBean().setSearchPhysLoaNo(null);
		defaultPtjAndMinistryValues();
		
	}
	
	public void reset(){
		this.getManagedBean().setSearchDocNo(null);
		this.getManagedBean().setSearchSuppName(null);
		this.getManagedBean().setSearchOrderToDuration(null);
		this.getManagedBean().setSearchOrderFromDuration(null);
		this.getManagedBean().setSearchLoaNo(null);
		this.getManagedBean().setSearchOrderType(null);
		this.getManagedBean().setSearchDocType(null);
		this.getManagedBean().setSearchPhysContractNo(null);
		this.getManagedBean().setSearchPhysLoaNo(null);
		this.getManagedBean().resetDocumentDataModel();
//		renewSesionObject();
	}
	
//	public void getProcurementTypeList(){
//		IParameterSO parameterSO = (IParameterSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
//		ParameterReqDTO reqDTO = new ParameterReqDTO();
//		reqDTO.setLanguageCode("en");
//		reqDTO.setParameterType(ParameterTypeConst.PROCUREMENT_TYPE);
//		ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
//		resDTO.getParameterDescList();
//		getManagedBean().setProcurementTypeList(resDTO.getParameterDescList());
//	}

	
	private List<String> getDocTypeListByRole() {
		List<String> flDocTypes = new ArrayList<String>();
		if(ParameterConst.DOCTYPE_CONTRACT_ORDER.equalsIgnoreCase(getManagedBean().getSearchOrderType())) {
			flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);	
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		} else if(ParameterConst.DOCTYPE_PURCHASE_ORDER.equalsIgnoreCase(getManagedBean().getSearchOrderType())) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);				
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
				flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
				flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
				flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
				flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		}
//			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
//			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
//			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
//			flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
//			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
//			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);	
//			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);
		return flDocTypes;
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
			
			String docType = getManagedBean().getvFlDocument().getDocType();
			Long docId = getManagedBean().getvFlDocument().getDocId();
			
			if (docType == null || docId == null) {
				log.info(String.format("FL tracking diary search filter empty[%s=%s][%s=%s]", "docType", docType, "docId", docId));
				return new SearchResultBean<PmTrackingDiary>();
			}

			TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
			SearchFilterBean searchFilterBean1 = new SearchFilterBean("docType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvFlDocument().getDocType());
			SearchFilterBean searchFilterBean2 = new SearchFilterBean("docId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvFlDocument().getDocId());
			SearchFilterBean searchFilterBean3 = new SearchFilterBean("groupDocType",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvFlDocument().getDocType());
			SearchFilterBean searchFilterBean4 = new SearchFilterBean("groupId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getvFlDocument().getDocId());
			
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

	public String backToPrev() {
		
		return NAV_TO_FL_SEARCH + "?faces-redirect=true";
	}

	public String goToTrackingDiarySearch(VFlDocument vFlDocument) {
//		log.debug("**********goToTrackingDiarySearch************"+ unspscObj.getUnspscCode());
		getManagedBean().setvFlDocument(vFlDocument);
		searchTrackingDiaryDataModel.reloadData();	
		return NAV_TO_DETAIL_SEARCH + "?faces-redirect=true";
	}

	public IPagingDataModel<PmTrackingDiary> getSearchTrackingDiaryDataModel() {
		return searchTrackingDiaryDataModel;
	}

	public void setSearchTrackingDiaryDataModel(
			IPagingDataModel<PmTrackingDiary> searchTrackingDiaryDataModel) {
		this.searchTrackingDiaryDataModel = searchTrackingDiaryDataModel;
	}
	
	   /**
		 * retrieve financial year options for current year and past 7 years
		 * @return financial year options
		 */
		public static List<SelectItem> getFinancialYearOpt() {
			int years = 0;
			List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
			for(Integer i = 0;i < 7;i++){
				years = CommonMethod.getYear(-i);
				 financialYearOpt.add(new SelectItem(years, Integer.toString(years)));
			}
			return financialYearOpt;
		} 
		
		/**
		 * The method used for generating a list of available display type of org
		 * type as selection.
		 * 
		 */
		public SelectItem[] getOrgTypeDropdownValues() {
			//defaultPtjAndMinistryValues();
			ISearchBean<PmParameterDesc> searchBean = null;
			if(getManagedBean().getSearchOrderType() != null){
				if (getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
					searchBean = getUserOrgTypeSearchBean("searchOrderTypePO");
				} else if (getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) ) {
					searchBean = getUserOrgTypeSearchBean("searchOrderTypeCO");
				}

			}
			
			SelectItem[] items = null;
			if (searchBean != null) {
				searchBean.getSortColumns();
				items = getDropdownCodeValuesBySearchBean(
						ParameterTypeConst.DOCUMENT_TYPES, searchBean);
			}

			return items;
		}
		
		private ISearchBean<PmParameterDesc> getUserOrgTypeSearchBean(
				String componentId) {
			ISearchBean<PmParameterDesc> userOrgTypeSearchBean = super
					.createSearchBean(PmParameterDesc.class, componentId);
			List<String> flDocTypes = new ArrayList<String>();
			if (getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_PURCHASE_ORDER)) {
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_PURCHASE_REQUEST);
			} else if (getManagedBean().getSearchOrderType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_ORDER) ) {
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_ORDER);
				flDocTypes.add(ParameterConst.DOCTYPE_CONTRACT_REQUEST);
				flDocTypes.add(ParameterConst.DOCTYPE_SHIPPING_INSTRUCTION);
			}
			flDocTypes.add(ParameterConst.DOCTYPE_DELIVERY_ORDER);
			flDocTypes.add(ParameterConst.DOCTYPE_FULFILMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_INVOICE);
			flDocTypes.add(ParameterConst.DOCTYPE_STOP_DELIVERY);
			flDocTypes.add(ParameterConst.DOCTYPE_DEBIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_CREDIT_ADJUSTMENT_NOTE);
			flDocTypes.add(ParameterConst.DOCTYPE_PAYMENT_ADVICE);	
			
			userOrgTypeSearchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_IN.value(),flDocTypes));
			return userOrgTypeSearchBean;
		}
		
		/**
		 * Gets the default ptj and ministry search values based on user role.
		 *
		 */
		private void defaultPtjAndMinistryValues() {
			if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				this.getManagedBean().setMinistry(null);
				this.getManagedBean().setPtj(null);

			} else {
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				PmOrgProfile orgProfile = null;
				if (userResDTO.getUserOrgList() != null	&& userResDTO.getUserOrgList().size() > 0) {
					orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
				}
				if (getFacesScopes().hasRoleUser(RoleCode.REQUISITIONER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.FL_APPROVER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.RECEIVING_OFFICER.value())
						|| getFacesScopes().hasRoleUser(RoleCode.PAY_OFFICER.value())) {
					this.getManagedBean().setPtj(orgProfile);
					this.getManagedBean().setMinistry(orgProfile);
				} else if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
					this.getManagedBean().setPtj(null);
					this.getManagedBean().setMinistry(orgProfile);
				}

			}
		}
		 /**
			 * Get PTJ condition for construct the LOV listing
			 * @return String
			 */
		public String getPTJCondition() {
			String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
					this.getManagedBean().getMinistry().getOrgProfileId();
				//this.getManagedBean().setPtjLovEnabled(true);
			return condition;
		}
			
		/**
			* The method used reset PTJ profile value once select the Ministry
		*/
		public void resetPtjProfile() {
			getManagedBean().setPtj(null);
			//this.getManagedBean().setPtjLovEnabled(true);
		}
			
		/**
		  * The method used to disable the ministry Lov
		  * if login user is FL User
		  * or internal auditor.
		  * 
		  * @return true, if login user is FL User/internal auditor
		  *         false, if login user is not User/internal auditor
		*/	
		public boolean disableMinistryLov() {
			return (this.getManagedBean().isFlFulfilmentRequisitioner() || this.getManagedBean().isFlFulfilmentApprover()
					|| this.getManagedBean().isFlFulfilmentRecevingOfficer()
					|| this.getManagedBean().isFlFulfilmentPaymentOfficer() || getFacesScopes()
					.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()));
		}
		
		/**
		  * The method used to disable the ptj Lov
		  * if login user is FL User
		  * or ministry is null.
		  * 
		  * @return true, if login user is FL User/ministry is null
		  *         false, if login user is not User/ministry is null
		*/	
		
		
		public boolean disablePtjLov() {
			return (this.getManagedBean().isFlFulfilmentRequisitioner() || this.getManagedBean().isFlFulfilmentApprover()
					|| this.getManagedBean().isFlFulfilmentRecevingOfficer()
					|| this.getManagedBean().isFlFulfilmentPaymentOfficer() || getManagedBean().getMinistry()==null);
		}
		
		/**
		 * set search bean description for tracking purpose
		 *
		 */
		private void setSearchBeanDescription(ISearchBean<PmTrackingDiary> searchBean, SearchFilterBean searchFilterBean1, SearchFilterBean searchFilterBean2) {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("[%s=%s]", searchFilterBean1.getSearchField(), searchFilterBean1.getSearchValue()));
			sb.append(String.format("[%s=%s]", searchFilterBean2.getSearchField(), searchFilterBean2.getSearchValue()));
			searchBean.setDescription("searchTrackingDiary" + sb.toString() + "[Caller:FlTrackingDiaryBackingBean.SearchTrackingDiaryPagingDataModel]");
		}
}
