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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.ct.VCtDocument;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.trackingdiary.CtTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
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
	 * The method used for generating a list of available transaction type
	 * for contract module as selection.
	 * 
	 * @return selectItems the selection list of available transaction type
	 */
	public SelectItem[] getContractType() {
		ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
				PmParameterDesc.class, "transactionType");

		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
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
			
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			docTypeList.add(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			docTypeList.add(ParameterConst.DOCTYPE_REQUEST_FACTORING);
			docTypeList.add(ParameterConst.DOCTYPE_REMOVE_FACTORING);
			docTypeList.add(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
			docTypeList.add(ParameterConst.DOCTYPE_AGREEMENT);
			docTypeList.add(ParameterConst.DOCTYPE_SUPPLEMENTAL_AGREEMENT);
				
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
	 * The method used for generating a list of available ministry
	 * for searching agency as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByMinistry(reqDTO);
		    
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			for (PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
    
    /**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getSearchMinistryProfileId();
		return condition;
	}
	
	/**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
     */
    public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
    
    /**
     * The method used to verify the login user is BPK CC approver 
     * or contract approver.
	 * 
	 * @return true, if login user is BPK CC approver or contract approver
	 *         false, if login user is not BPK CC approver or contract approver
     */
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value()));
	} 
    
    /**
     * The method used to verify the login user is general auditor.
	 * 
	 * @return true, if login user is general auditor
	 *         false, if login user is not general auditor
     */
    public boolean isGeneralAuditor() {
		return (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value()));
	} 
    
    /**
   	 * The method used to disable the ministry selection
   	 * if login user is central contract administrator
   	 * or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is central contract administrator/ministry administrator/internal auditor
   	 *         false, if login user is not central contract administrator/ministry administrator/internal auditor
	 */
    public boolean disableMinistrySelection() {
    	return (isContractAdmin() || isApprover() || (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) 
				&& !isGeneralAuditor()));
    }
    	
	private void defaultMinistryPtjSearchValue() {
		if (getManagedBean().getSearchTransactionType() != null) {				
	    	if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())) {
	    		this.getManagedBean().setSearchMinistryProfileId(null);
	    		this.getManagedBean().setSearchPtjProfile(null);
				
			} else {
				IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				PmOrgProfile orgProfile = null;
				if (userResDTO.getUserOrgList()!=null && userResDTO.getUserOrgList().size()>0) {
					orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
				}
				
				if (getManagedBean().getSearchTransactionType().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
					this.getManagedBean().setSearchMinistryProfileId(null);
					this.getManagedBean().setSearchPtjProfile(orgProfile);
				} else {
					if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {			
						this.getManagedBean().setSearchMinistryProfileId(orgProfile.getOrgProfileId());
						this.getManagedBean().setSearchPtjProfile(null);
					} else if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
							|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
						this.getManagedBean().setSearchMinistryProfileId(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId());
						this.getManagedBean().setSearchPtjProfile(orgProfile);
					}
				}
			}
		}
    }
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setSearchPtjProfile(null);
	}
	
	/**
	 * The method used for searching the contract tracking diary.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 */
	public void search() {
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
		this.getManagedBean().setSearchMinistryProfileId(null);
		this.getManagedBean().setSearchPtjProfile(null);
		defaultMinistryPtjSearchValue();
	}
	
	/**
	 * The method used to reset all search criteria and search result.
	 */
	public void reset() {
		this.getManagedBean().setSearchTransactionType(null);
		this.getManagedBean().resetCtDocumentDataModel();
		resetSearchCriteria();
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
			
			searchFilterBean1 = new SearchFilterBean("groupDocType", SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getvCtDocument().getTransactionType());
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
				ITrackingDiarySO trackingDiarySO = (ITrackingDiarySO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.COMMON_COMPONENT, ITrackingDiarySO.class);
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
