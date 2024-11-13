package my.ep.web.backingbean.pm.organisation;

import java.util.ArrayList;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.DropDownLabelConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.pm.organisation.UserOrgSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * 20230111@Haziq - #12487&29693
 * @author muhammadasyrafhaziq
 * This class to retrieve user organization details information
 */
@ManagedBean(name = "userOrganisationSearchBackingBean")
@ViewScoped
public class UserOrgSearchBackingBean extends
		BaseBackingBean<UserOrgSearchManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(UserOrgSearchBackingBean.class);

	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}
	
	public SelectItem[] getJabatanDropdown() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean()
				.getMinistryProfileId());
	}
	
	public SelectItem[] getMinistryDropdownRpt() {
		SelectItem[] ministryItems = PmCommonUtil.getMinistryDropdown();
		this.getManagedBean().setPegawaiPengawalProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		return ministryItems;
	}	
	public SelectItem[] getPegwaiDropdownRpt() {
		SelectItem[] pegaiItems = PmCommonUtil
				.getPegwaiPengawalDropdown(getManagedBean()
						.getMinistryProfileId());
		this.getManagedBean().setJabatanProfileId(null);
		return pegaiItems;
	}	
	public SelectItem[] getJabatanDropdownRpt() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean()
				.getPegawaiPengawalProfileId());
	}
	
	/**
	 * To initiate data
	 * @param orgProfileId
	 */
	public void initPtjPopupDefaultMinistry(Long orgProfileId) {
		initPtjPopup();
		
	    RoleCode[] allowedRoles = {
	        RoleCode.DESK_OFFICER, 
	        RoleCode.APPROVER,
	        RoleCode.PUBLICATION_APPROVER,
	        RoleCode.PBA_SECRETARY,
	        RoleCode.PBB_SECRETARY,
	        RoleCode.PPB_SECRETARY,
	        RoleCode.QCA_SECRETARY,
	        RoleCode.QCB_SECRETARY,
	        RoleCode.FPB_SECRETARY
	    };
  
	    for (RoleCode role : allowedRoles) {
	        if (getFacesScopes().hasRoleUser(role.value())) {
	            getManagedBean().setMinistryProfileId(orgProfileId);
	            getManagedBean().setDisabledMinistry(true);
	            return;
	        }
	    }
	}

	/**
	 * To set PTJ details info
	 */
	public void initPtjPopup(){
		this.renewSesionObject();
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		//Internal Auditor (report) same authorization as Ministry Admin 
		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {
			getManagedBean().setMinistryProfileId(pmOrgProfile.getOrgProfileId());
			getManagedBean().setDisabledMinistry(true);
		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
					pmOrgProfile.getOrgProfileId()).getOrgProfileId());
			getManagedBean().setPegawaiPengawalProfileId(retrievePegawaiPegawalOrgProfile(pmOrgProfile.getOrgProfileId()).getOrgProfileId());
			getManagedBean().setJabatanProfileId(pmOrgProfile.getOrgProfileId());
			getManagedBean().setDisabledMinistry(true);
			getManagedBean().setDisabledPegawaiPengawal(true);
			getManagedBean().setDisabledJabatan(true);
		} 
		
		this.getManagedBean().setPtjCodeName(ProfileManagementConst.PTJ_CODE);
	}

	private class UserOrgDataModel extends PagingDataModel<PmUserOrg> {
		private static final long serialVersionUID = 1L;

		public UserOrgDataModel() {
			super("dtUserOrg",false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmUserOrg> pageChangeListener(
				ISearchBean<PmUserOrg> searchBean) {
			try {
				UserOrgReqDTO reqDTO = new UserOrgReqDTO();
				if(getManagedBean().getMinistryProfileId() != null){
					reqDTO.setUserMinistryId(getManagedBean().getMinistryProfileId());
				}
				if(getManagedBean().getPegawaiPengawalProfileId() != null){
					reqDTO.setUserPegawaiId(getManagedBean().getPegawaiPengawalProfileId());
				}
				if(getManagedBean().getJabatanProfileId() != null){
					reqDTO.setUserJabatanId(getManagedBean().getJabatanProfileId());
				}
				ISearchBean<PmUserOrg> searchBeanData = getSearchBeanData(searchBean);
			
				
				IUserSO userSO =  BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				reqDTO.setUserOrgSearchBean(searchBeanData);
				UserOrgResDTO resDTO = null;
				if(ParameterConst.ORG_TYPE_PTJ.equals(getManagedBean().getOrgType())){
					resDTO = userSO.retrieveUserPtjListByParents(reqDTO);
				}
				
				if (resDTO != null && resDTO.isSuccessful()) {
					return resDTO.getUserOrgResultBean();
				}
			} catch (Exception e) {
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, ParameterConst.DOCTYPE_QUOTATION,
						null, "UserOrgSearchBackingBean.pageChangeListener",null, e);
				 
			}
			return null;
		}
		
		/**
		 * Search data base on searchBean
		 * @param searchBean
		 * @return
		 */
		private ISearchBean<PmUserOrg> getSearchBeanData(ISearchBean<PmUserOrg> searchBean){
			log.info("getSearchBeanData");
			
			  String ptjCodeName = getManagedBean().getPtjCodeName();
			  String ptjCodeNameSearchValues = getManagedBean().getPtjCodeNameSearchValues();
			  String userCodeName = getManagedBean().getUserCodeName();
			  String userCodeNameSearchValues = getManagedBean().getUserCodeNameSearchValues();
			
			if (StringUtils.isNotBlank(ptjCodeName)	&& StringUtils.isNotBlank(ptjCodeNameSearchValues)) {
				if (isOrgCode(ptjCodeName)) {
					SearchFilterBean filterBean = new SearchFilterBean(
							QTSearchBeanConst.SEARCH_PMORGPROFILE_PMORGVALIDITY_ORGCODE,
							SearchOperator.OPERATOR_LIKE.value(),
							ptjCodeNameSearchValues);
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);

				} else if (isOrgName(ptjCodeName)) {
					SearchFilterBean filterBean = new SearchFilterBean(
							QTSearchBeanConst.SEARCH_PMORGPROFILE_PMORGVALIDITY_ORGNAME,
							SearchOperator.OPERATOR_LIKE.value(),
							ptjCodeNameSearchValues);
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			}
					
			//20230111@Haziq - #12487
			
			if (StringUtils.isNotBlank(userCodeName)&& StringUtils.isNotBlank(userCodeNameSearchValues)) {
				if (userCodeName.equalsIgnoreCase(ProfileManagementConst.USER_IC)) {
					SearchFilterBean filterBean = new SearchFilterBean(
							QTSearchBeanConst.SEARCH_PMUSER_IDENTIFICATIONNO,
							SearchOperator.OPERATOR_LIKE.value(),
							userCodeNameSearchValues);
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);

				} else if (userCodeName.equalsIgnoreCase(ProfileManagementConst.USER_NAME)) {
					SearchFilterBean filterBean = new SearchFilterBean(
							QTSearchBeanConst.SEARCH_PMUSER_USERNAME,
							SearchOperator.OPERATOR_LIKE.value(),
							userCodeNameSearchValues);
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
			}
		
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMORGPROFILE_BPK,
							SearchOperator.OPERATOR_EQUALS.value(), true));
			List<Long> zoneIds = new ArrayList<>();
			zoneIds.add(getManagedBean().getZone());
			zoneIds.add(new Long(SupplierManagementConst.PM_ZONE_SEMENANJUNG));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMORGPROFILE_PMZONE_ZONEID,
					SearchOperator.OPERATOR_IN.value(),
					zoneIds));
			
			return searchBean;
		}
		
		private boolean isOrgCode(String ptjCodeName) {
		    return ptjCodeName.equalsIgnoreCase(ProfileManagementConst.PTJ_CODE) || 
		           ptjCodeName.equalsIgnoreCase(ProfileManagementConst.KPTJ_CODE) ||
		           ptjCodeName.equalsIgnoreCase(ProfileManagementConst.PEGAWAI_CODE);
		}

		private boolean isOrgName(String ptjCodeName) {
		    return ptjCodeName.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME) ||
		           ptjCodeName.equalsIgnoreCase(ProfileManagementConst.KPTJ_NAME) ||
		           ptjCodeName.equalsIgnoreCase(ProfileManagementConst.PEGAWAI_NAME);
		}
		
	}
	
	/**
	 * Search user by organization type
	 * @param orgType
	 */
	public void searchUserOrgs(String orgType){
		getManagedBean().setOrgType(orgType);
	

	    String ptjCodeNameSearchValues = getManagedBean().getPtjCodeNameSearchValues();
	    String userCodeNameSearchValues = getManagedBean().getUserCodeNameSearchValues();

	    if ((StringUtils.isNotBlank(ptjCodeNameSearchValues) && ptjCodeNameSearchValues.trim().length() < 3) ||
	        (StringUtils.isNotBlank(userCodeNameSearchValues) && userCodeNameSearchValues.trim().length() < 3)) {
	        throw new EpException(MessageConst.ERROR_716019);
	    }
		
		getUserOrgDataModel().reloadData();
	}
	
	/**
	 * reset 
	 */
	public void resetUserOrg(){
		Long ministryId = this.getManagedBean().getMinistryProfileId();
		
		this.renewSesionObject();
		
		getUserOrgDataModel().resetData();
		
		String portletName = (String) this.getFacesScopes().getRequest().getAttribute(QTConstants.PORTLET_ID);

		
		if (StringUtils.isNotBlank(portletName) && (portletName.toLowerCase().contains(CommonConstants.LOA) || 
				 portletName.toLowerCase().contains(QTConstants.PORTLET_NAME_CREATE) || 
				 portletName.toLowerCase().contains(QTConstants.PORTLET_NAME_REVIEW))) {
			 
						
			this.getManagedBean().setMinistryProfileId(ministryId);
		
			this.getManagedBean().setDisabledMinistry(ministryId != null);
			
		}
	}

	/**
	 * Get user organization data model
	 * @return
	 */
	public IPagingDataModel<PmUserOrg> getUserOrgDataModel() {
		IPagingDataModel<PmUserOrg> userOrgDataModel = getManagedBean().getUserOrgDataModel();
		
		if (userOrgDataModel == null) {
			userOrgDataModel = new UserOrgDataModel();
			getManagedBean().setUserOrgDataModel(userOrgDataModel);
		}
		
		return userOrgDataModel;
	}
	
	/**
	 * OK action execute
	 * @param methodActionStr
	 */
	public void okAction(String methodActionStr) {
		log.info("okAction");
		
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		methodActionStr, null,
	                        new Class[] { PmUserOrg.class });
			if(getManagedBean().getSelectedUser() != null){
				methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{getManagedBean().getSelectedUser()});
			}
			
			RequestContext.getCurrentInstance().execute(QTConstants.USER_PTJ_POPUP_HIDE);
		}
	}
	
	/**
	 * List dropdown for PTJ or KPTJ
	 * @param orgType
	 * @return
	 */
	public static SelectItem[] getPtjOrKptjDropDown(String orgType) {
		SelectItem[] items = new SelectItem[2];
			if(ParameterConst.ORG_TYPE_PTJ.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage(
					DropDownLabelConst.PM_ORG_PTJ_CODE, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage(
					DropDownLabelConst.PM_ORG_PTJ_NAME, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		
		return items;
	}
	//20230111@Haziq - #12487
	/**
	 *  List dropdown for user name and ic
	 * @return
	 */
	public static SelectItem[] getUserDropDown() {
		SelectItem[] items = new SelectItem[3];
		
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
		    		DropDownLabelConst.TEXT_SELECT, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.USER_IC, ResourceBundleUtils.getMessage(
					DropDownLabelConst.PM_PROFILE_IC, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[2] = new SelectItem(ProfileManagementConst.USER_NAME, ResourceBundleUtils.getMessage(
					DropDownLabelConst.PM_PROFILE_NAME, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
	
		return items;
	}
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
}
