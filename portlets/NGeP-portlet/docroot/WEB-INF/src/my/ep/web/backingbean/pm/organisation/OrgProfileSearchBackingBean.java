package my.ep.web.backingbean.pm.organisation;

import java.util.List;
import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.pm.organisation.OrgProfileSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "organisationSearchBackingBean")
@ViewScoped
public class OrgProfileSearchBackingBean extends
		BaseBackingBean<OrgProfileSearchManagedBean> {
	private static Logger log = Logger
			.getLogger(OrgProfileSearchBackingBean.class);

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
		if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
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
		//only For Report to disable the IdentificationField
		this.getManagedBean().getReportManagedBean().setDisabledOrgFields(true);
	}

	private class OrgProfileDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public OrgProfileDataModel() {
			super("dtOrgProfile",false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			try {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				if(getManagedBean().getMinistryProfileId() != null){
					reqDTO.setMinistryId(getManagedBean().getMinistryProfileId());
				}
				if(getManagedBean().getPegaiPengawaProfileId() != null){
					reqDTO.setPegaiId(getManagedBean().getPegaiPengawaProfileId());
				}
				if(getManagedBean().getJabatanProfileId() != null){
					reqDTO.setJabatanId(getManagedBean().getJabatanProfileId());
				}
				ISearchBean<PmOrgProfile> searchBeanData = getSearchBeanData(searchBean);
			
				
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				reqDTO.setOrgProfileSearchBean(searchBeanData);
				OrgProfileResDTO resDTO = null;
				if(ParameterConst.ORG_TYPE_PTJ.equals(getManagedBean().getOrgType())){
					resDTO = orgProfileSO.retrievePtjListByParents(reqDTO);
				}else if(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(getManagedBean().getOrgType())){
					resDTO = orgProfileSO.retrievePegaiPengawaListByParents(reqDTO);
				}else if(ParameterConst.ORG_TYPE_PTJ_GROUP.equals(getManagedBean().getOrgType())){
					resDTO = orgProfileSO.retrieveKPtjListByParents(reqDTO);
				}
				
				if (resDTO.isSuccessful()) {
					return resDTO.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void searchOrgProfiles(String orgType){
		getManagedBean().setOrgType(orgType);
		getOrgProfileDataModel().reloadData();
	}
	
	public void resetOrgProfiles(){
		this.renewSesionObject();
		getOrgProfileDataModel().resetData();
	}

	public IPagingDataModel<PmOrgProfile> getOrgProfileDataModel() {
		IPagingDataModel<PmOrgProfile> orgProfileDataModel = getManagedBean().getOrgProfileDataModel();
		
		if (orgProfileDataModel == null) {
			orgProfileDataModel = new OrgProfileDataModel();
			getManagedBean().setOrgProfileDataModel(orgProfileDataModel);
		}
		
		return orgProfileDataModel;
	}
	
	public void okAction(String methodActionStr) {
		log.info("okAction");
		
		if (StringUtils.isNotBlank(methodActionStr)) {
			MethodExpression methodExp = FacesScopes
	                .createMethodExpression(FacesContext.getCurrentInstance(),
	                		methodActionStr, null,
	                        new Class[] { PmOrgProfile.class });
			if(getManagedBean().getSelectedPtj() != null){
				methodExp.invoke(
					FacesContext.getCurrentInstance().getELContext(), 
					new Object[]{getManagedBean().getSelectedPtj()});
			}
			
			
			RequestContext.getCurrentInstance().execute("ptjPopup.hide();");
		}
	}
	
	public static SelectItem[] getPtjOrKptjDropDown(String orgType) {
		SelectItem[] items = new SelectItem[2];
		if(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.PEGAWAI_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.pegaiCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PEGAWAI_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.pegaiName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		if(ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.KPTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.KPTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.kptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}else if(ParameterConst.ORG_TYPE_PTJ.equals(orgType)){
			items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjCode", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage(
					"lbl.pm.org.ptjName", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
		}
		
		return items;
	}
	
	private ISearchBean<PmOrgProfile> getSearchBeanData(ISearchBean<PmOrgProfile> searchBean){
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
						"pmOrgValidity.orgCode",
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
						"pmOrgValidity.orgName",
						SearchOperator.OPERATOR_LIKE.value(),
						getManagedBean().getPtjCodeNameSearchValues());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}
		}
		return searchBean;
	}
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
}
