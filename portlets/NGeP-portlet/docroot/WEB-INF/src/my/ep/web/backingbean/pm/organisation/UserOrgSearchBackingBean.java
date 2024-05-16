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
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.authorization.userorg.UserOrgReqDTO;
import my.ep.dto.pm.authorization.userorg.UserOrgResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.organisation.UserOrgSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * 20230111@Haziq - #12487
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
		this.getManagedBean().setPegaiPengawaProfileId(null);
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
				.getPegaiPengawaProfileId());
	}
	
	/**
	 * To initiate data
	 * @param orgProfileId
	 */
	public void initPtjPopupDefaultMinistry(Long orgProfileId) {
		initPtjPopup();
		
		if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.APPROVER.value())	
				|| getFacesScopes().hasRoleUser(RoleCode.PUBLICATION_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.FPB_SECRETARY.value())) {
			getManagedBean().setMinistryProfileId(orgProfileId);
			getManagedBean().setDisabledMinistry(true);
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
			getManagedBean().setPegaiPengawaProfileId(retrievePegawaiPegawalOrgProfile(pmOrgProfile.getOrgProfileId()).getOrgProfileId());
			getManagedBean().setJabatanProfileId(pmOrgProfile.getOrgProfileId());
			getManagedBean().setDisabledMinistry(true);
			getManagedBean().setDisabledPegaiPengawa(true);
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
				if(getManagedBean().getPegaiPengawaProfileId() != null){
					reqDTO.setUserPegawaiId(getManagedBean().getPegaiPengawaProfileId());
				}
				if(getManagedBean().getJabatanProfileId() != null){
					reqDTO.setUserJabatanId(getManagedBean().getJabatanProfileId());
				}
				ISearchBean<PmUserOrg> searchBeanData = getSearchBeanData(searchBean);
			
				
				IUserSO userSO = (IUserSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IUserSO.class);
				reqDTO.setUserOrgSearchBean(searchBeanData);
				UserOrgResDTO resDTO = null;
				if(ParameterConst.ORG_TYPE_PTJ.equals(getManagedBean().getOrgType())){
					resDTO = userSO.retrieveUserPtjListByParents(reqDTO);
				//}else if(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(getManagedBean().getOrgType())){
				//	resDTO = orgProfileSO.retrievePegaiPengawaListByParents(reqDTO);
				//}else if(ParameterConst.ORG_TYPE_PTJ_GROUP.equals(getManagedBean().getOrgType())){
				//	resDTO = orgProfileSO.retrieveKPtjListByParents(reqDTO);
				}
				
				if (resDTO != null && resDTO.isSuccessful()) {
					return resDTO.getUserOrgResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * Search user by organization type
	 * @param orgType
	 */
	public void searchUserOrgs(String orgType){
		getManagedBean().setOrgType(orgType);
	

		if((getManagedBean().getPtjCodeNameSearchValues() != null && StringUtils.isNotBlank(getManagedBean().getPtjCodeNameSearchValues()) && getManagedBean().getPtjCodeNameSearchValues().trim().length()<3) || 
		   (getManagedBean().getUserCodeNameSearchValues() != null && StringUtils.isNotBlank(getManagedBean().getUserCodeNameSearchValues()) && getManagedBean().getUserCodeNameSearchValues().trim().length()<3)){
			 throw new EpException(MessageConst.ERROR_716019);
		}
		
		
		getUserOrgDataModel().reloadData();
	}
	
	/**
	 * reset 
	 */
	public void resetUserOrg(){
		Long ministryId = this.getManagedBean().getMinistryProfileId(); //#26475
		
		this.renewSesionObject();
		
		getUserOrgDataModel().resetData();
		
		String portletName = (String) this.getFacesScopes().getRequest().getAttribute("PORTLET_ID");

		log.info("::: resetOrgProfiles() 001 portletName: "+ portletName);
		
		if (StringUtils.isNotBlank(portletName) && (portletName.toLowerCase().contains("loa") || 
				 portletName.toLowerCase().contains("createmanualsourcing") || 
				 portletName.toLowerCase().contains("reviewmanualsourcing"))) {
			 
			log.info("::: resetOrgProfiles() portletName: "+ portletName);
						
			this.getManagedBean().setMinistryProfileId(ministryId);
		
			this.getManagedBean().setDisabledMinistry(ministryId != null);
			
		}
	}

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
			
			RequestContext.getCurrentInstance().execute("userPtjPopup.hide();");
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
	 * Search data base on searchBean
	 * @param searchBean
	 * @return
	 */
	private ISearchBean<PmUserOrg> getSearchBeanData(ISearchBean<PmUserOrg> searchBean){
		log.info("getSearchBeanData");	
		if (StringUtils.isNotBlank(getManagedBean().getPtjCodeName())
				&& (StringUtils.isNotBlank(getManagedBean().getPtjCodeNameSearchValues()))) {
			if (getManagedBean().getPtjCodeName().equalsIgnoreCase(
					ProfileManagementConst.PTJ_CODE) || 
					getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_CODE )
							|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
									ProfileManagementConst.PEGAWAI_CODE)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgProfile.pmOrgValidity.orgCode",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);

			} else if (getManagedBean().getPtjCodeName()
					.equalsIgnoreCase(ProfileManagementConst.PTJ_NAME) ||
					 getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.KPTJ_NAME)
					|| getManagedBean().getPtjCodeName().equalsIgnoreCase(
							ProfileManagementConst.PEGAWAI_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmOrgProfile.pmOrgValidity.orgName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
				
		//20230111@Haziq - #12487
		
		if (StringUtils.isNotBlank(getManagedBean().getUserCodeName())
				&& (StringUtils.isNotBlank(getManagedBean().getUserCodeNameSearchValues()))) {
			if (getManagedBean().getUserCodeName().equalsIgnoreCase(
					ProfileManagementConst.USER_IC)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmUser.identificationNo",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getUserCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);

			} else if (getManagedBean().getUserCodeName()
					.equalsIgnoreCase(ProfileManagementConst.USER_NAME)) {
				SearchFilterBean filterBean = new SearchFilterBean(
						"pmUser.userName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getUserCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
	
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmOrgProfile.bpk",
						SearchOperator.OPERATOR_EQUALS.value(), true));
		List<Long> zoneIds = new ArrayList<>();
		zoneIds.add(getManagedBean().getZone());
		zoneIds.add(new Long(SupplierManagementConst.PM_ZONE_SEMENANJUNG));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmOrgProfile.pmZone.zoneId",
				SearchOperator.OPERATOR_IN.value(),
				zoneIds));
		
		return searchBean;
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
