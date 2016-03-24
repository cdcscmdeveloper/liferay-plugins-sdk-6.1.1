package my.ep.web.backingbean.pm.profilemanagementsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.ProfileManagementSearchBean;
import my.ep.web.managedbean.pm.profilemanagementsearch.ProfileManagementSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.StatusHelper;

import org.apache.log4j.Logger;


/**
 * The backing bean for Profile Management Search
 * 
 * @author Zeinab
 * @since 24.02.2016
 */
@ManagedBean(name = "profileManagSearchBackingBean")
@ViewScoped
public class ProfileManagementSearchBackingBean extends
		BaseBackingBean<ProfileManagementSearchManagedBean> {

	// Navigation search
		public static final String NAV_TO_SEARCH = "/xhtml/pm/profilemanagementsearch/profileManagementMain.xhtml?faces-redirect=true";
	/** The log. */
	private static Logger log = Logger
			.getLogger(ProfileManagementSearchBackingBean.class);
	private ISearchBean<PmParameterDesc> appTypeSearchBean;
	private SelectItem[] applicationStatus;
	
	// Paging for listing
		private IPagingDataModel<Object[]> accessAPPLDataModel = new AccessAPPLPagingDataModel();

		public IPagingDataModel<Object[]> getAccessAPPLDataModel() {
			return accessAPPLDataModel;
		}

		public void setAccessAPPLDataModel(
				IPagingDataModel<Object[]> accessAPPLDataModel) {
			this.accessAPPLDataModel = accessAPPLDataModel;
		}
		// pagination for profile management search list
		private class AccessAPPLPagingDataModel extends
				PagingDataModel<Object[]> {
			private static final long serialVersionUID = 1L;

			public AccessAPPLPagingDataModel() {
				super("dtPmResult");
				log.info("....AccessAPPLPagingDataModel");
			}
			
			@Override
			protected ISearchResultBean<Object[]> pageChangeListener(
					ISearchBean<Object[]> searchBean) {
				log.info("....pageChangeListener");
				IAccessApplSO accessApplSO = null;
				if (getManagedBean().getSelectedAppTypeParamCode() != null) {
					try {
//						ISearchBean<Object[]> searchBeanData = getSearchBeanData(searchBean);
						log.info("....after getsearchbean");
						log.info("....getProfileManagSearchBean().getAppStatusId()="+getManagedBean().getProfileManagSearchBean().getAppStatusId());
						AccessApplReqDTO applReqDTO = new AccessApplReqDTO();
						getManagedBean().fillRequestDTO(applReqDTO);
						applReqDTO.setAccessApplSearch(searchBean);
						applReqDTO.setAppStatusId(getManagedBean().getProfileManagSearchBean()
								.getAppStatusId());
						applReqDTO.setDocType(getManagedBean().getSelectedAppTypeParamCode());
						accessApplSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IAccessApplSO.class);
						log.info("....efor call  getAccessApplListBySearchBean");
						AccessApplResDTO resDto = accessApplSO
								.getAccessApplListBySearchBean(applReqDTO);
						log.info("....after call  getAccessApplListBySearchBean");
						if (resDto != null && resDto.isSuccessful()) {
							log.info("....pageChangeListener..resultt.end");
							return resDto.getAppLResultBean();
						}
					} finally {
						accessApplSO = null;
					}
				}
				log.info("....pageChangeListener...end");
				return null;
			}

		}


	public void preLoadingAccessAPL() {
		log.info("preLoadingAccessAPL");
		renewSesionObject();
		resetProfileManagSearchBean();
		log.info("preLoadingAccessAPL");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	public ISearchBean<PmParameterDesc> getAppTypeSearchBean() {
		if (this.appTypeSearchBean == null) {
			this.appTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "accessAppType");

			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(new ArrayList<String>(Arrays
					.asList(new String[] {
							ParameterConst.DOCTYPE_MARKET_RESEARCH,
							ParameterConst.DOCTYPE_COMPANY_PROFILE })));
			appTypeSearchBean.getDataFilterBeans().add(bean);

		}

		return this.appTypeSearchBean;
	}
	
	
//	public SelectItem[] getApplicationStatus() {
//		String paramCode = this.getManagedBean().getSelectedAppTypeParamCode();
//		Long[] statusIds = null;
//		Long[] cpStatusIds = new Long[]{75005L,75010L,75015L,75020L,75025L,75030L};
//		Long[] mrStatusIds = new Long[]{75000L,75010L,75015L,75020L,75025L,75030L};
//		if(paramCode != null){
//		    if(paramCode.equals(ParameterConst.DOCTYPE_MARKET_RESEARCH)){
//			  statusIds = mrStatusIds;
//		    }else if(paramCode.equals(ParameterConst.DOCTYPE_COMPANY_PROFILE)){
//		    	 statusIds = cpStatusIds;
//		    }
//		   }
//		    //fill application status dropdown
//			  SelectItem[] items = null;
//			  FacesScopes face = new FacesScopes();
//				try {
//					List<PmStatusDesc> statusDescList = new ArrayList<PmStatusDesc>();
//					for(Long statusId:cpStatusIds){
//						PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(statusId);
//						if(pmStatusDesc != null){
//							log.info("......>>>......");
//							statusDescList.add(pmStatusDesc);
//						}
//					}
//					
//					items = new SelectItem[statusDescList.size() + 1];
//					items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
//							"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
//							face.getUserLocale()));
//					int i = 1;
//					for (PmStatusDesc pmStatusDesc : statusDescList) {
//						items[i++] = new SelectItem(pmStatusDesc.getPmStatus().getStatusId(), pmStatusDesc.getStatusName());
//						log.info("......add to>>......");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					FacesContext.getCurrentInstance().addMessage(
//							null,
//							new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
//									e.getMessage()));
//				}
//			  
//		return items;
//	}

	public void setApplicationStatus(SelectItem[] applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public SelectItem[] getApplicationStatus() {
		if(applicationStatus == null){
			appTypeChange();
		}
		return applicationStatus;
	}

	public void searchAccessAppL(){
		getAccessAPPLDataModel().reloadData();
		log.info("searchAccessAppL-end");
	}
	
	public String retrieveOrgName(PmOrgProfile orgProfile){
		String orgName = null;
		if(orgProfile != null){
			if(ParameterConst.ORG_TYPE_SUPPLIER.equals(orgProfile.getOrgTypeCode().getParameterCode())){
				orgName = orgProfile.getSmSupplier().getCompanyName();
			}else if(ParameterConst.ORG_TYPE_FACTORING.equals(orgProfile.getOrgTypeCode().getParameterCode())){
				orgName = orgProfile.getPmFinancialOrg().getFinOrgName();
			}else{
				orgName = orgProfile.getPmOrgValidity().getOrgName();
			}
		}
		
		return orgName;
	}
	
	/**
	 * reset basic search bean
	 */
	public void resetProfileManagSearchBean() {
		log.info("resetProfileManagSearchBean");
		this.getManagedBean().setProfileManagSearchBean(
				new ProfileManagementSearchBean());
		this.getManagedBean().setSelectedAppTypeParamCode(null);
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		
		setAccessAPPLDataModel(new AccessAPPLPagingDataModel());
	}
	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		SelectItem[] items = null;
//		if (getManagedBean().getOrgTypeMap().get("somAccessApplOrgType") == null) {
			ISearchBean<PmParameterDesc> searchBean = super.createSearchBean(
					PmParameterDesc.class, "somAccessApplOrgType");

			List<String> recommendList = new ArrayList<String>();
			recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
			recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES
					.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES.value());
			recommendList.add(OrganizationType.STATUTORY_BODIES_STATE.value());
			recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());

			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(), recommendList));
			if (searchBean != null) {
				searchBean.getSortColumns();
				items = getDropdownValuesBySearchBean(
						ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			}
//			getManagedBean().getOrgTypeMap().put("somAccessApplOrgType", items);
//		} else {
//			items = getManagedBean().getOrgTypeMap()
//					.get("somAccessApplOrgType");
//		}

		return items;
	}

	/**
	 * retrieve application type
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] applicationTypeOptions() {
		log.info("applicationTypeOptions");
		return getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, getAppTypeSearchBean());
	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getSelectedOrgType());
		sb.append("|recordStatus^1");
		return sb.toString();
	}

	public void appTypeChange(){
		log.info("......appTypeChange......");
	String paramCode = this.getManagedBean().getSelectedAppTypeParamCode();
	Long[] statusIds = null;
	Long[] cpStatusIds = new Long[]{75005L,75010L,75015L,75020L,75025L,75030L};
	Long[] mrStatusIds = new Long[]{75000L,75010L,75015L,75020L,75025L,75030L};
	if(paramCode != null){
	    if(paramCode.equals(ParameterConst.DOCTYPE_MARKET_RESEARCH)){
		  this.getManagedBean().getProfileManagSearchBean().setMarketResearch(true);
		  this.getManagedBean().getProfileManagSearchBean().setCompanyProfile(false);
		  this.getManagedBean().getProfileManagSearchBean().setCompanyProfile(null);
		  statusIds = mrStatusIds;
	    }else if(paramCode.equals(ParameterConst.DOCTYPE_COMPANY_PROFILE)){
	    	this.getManagedBean().getProfileManagSearchBean().setCompanyProfile(true);
	    	this.getManagedBean().getProfileManagSearchBean().setMarketResearch(false);
	    	this.getManagedBean().getProfileManagSearchBean().setMarketResearch(null);
	    	 statusIds = cpStatusIds;
	    }
	    //fill application status dropdown
		  SelectItem[] items = null;
		  FacesScopes face = new FacesScopes();
			try {
				List<PmStatusDesc> statusDescList = new ArrayList<PmStatusDesc>();
				for(Long statusId:cpStatusIds){
					PmStatusDesc pmStatusDesc = StatusHelper.getInstance().getStatusDescById(statusId);
					if(pmStatusDesc != null){
					
						statusDescList.add(pmStatusDesc);
					}
				}
				
				items = new SelectItem[statusDescList.size() + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				int i = 1;
				for (PmStatusDesc pmStatusDesc : statusDescList) {
					items[i++] = new SelectItem(pmStatusDesc.getPmStatus().getStatusId(), pmStatusDesc.getStatusName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
								e.getMessage()));
			}
		  
	
		  setApplicationStatus(items);

		  //////////////
	}
}
 
	public void changeOrgUnit(BaseEntity entity) {
		log.info("changeOrgUnit");
	}

	public void changeOrgType() {
		log.info("changeResOrgType");
		log.info("this.getManagedBean().getSelectedOrgType()="+this.getManagedBean().getSelectedOrgType());
		if (this.getManagedBean().getSelectedOrgProfile() != null) {
			getManagedBean().setSelectedOrgProfile(null);
		}

	}

}
