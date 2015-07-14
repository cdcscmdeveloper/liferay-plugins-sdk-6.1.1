package my.ep.web.backingbean.trackingdiary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.trackingdiary.ITrackingDiarySO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.managedbean.trackingdiary.PmTrackingDiaryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

@ManagedBean(name = "pmTrackingDiaryBackingBean")
@ViewScoped
public class PmTrackingDiaryBackingBean extends
		BaseBackingBean<PmTrackingDiaryManagedBean> {
	private static Logger log = Logger
			.getLogger(PmTrackingDiaryBackingBean.class);
	public static final String NAV_TO_PM_SEARCH = "trackingDiary.xhtml";
	public static final String NAV_TO_PM_DETAIL_SEARCH = "pmTrackingDiaryDetailSearch.xhtml";
	// Authorization
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;
	// Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;
	

	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		return orgTypeSearchBean;
	}

	public void setOrgTypeSearchBean(
			ISearchBean<PmParameterDesc> orgTypeSearchBean) {
		this.orgTypeSearchBean = orgTypeSearchBean;
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrganisationSearchBean");
		OrganisationSearchBean orgSearch= new OrganisationSearchBean();
		this.getManagedBean().setOrganisationSearchBean(
				orgSearch);
		this.getManagedBean().resetOrgResultBean();
	}

	/**
	 * reset search bean fields
	 */
	public void resetUserProfilesSearchBean() {
		log.info("resetUserProfilesSearchBean");
		this.getManagedBean().setUserProfileSearchBean(
				new UserProfilesSearchBean());
		this.getManagedBean().resetUserResultBean();
	}
	
	public void onFunctionSelect() {
		System.out.println("............onFunctionSelect..............");
		resetOrganisationSearchBean();
		if (this.getManagedBean().getFunctionType() != null) {
			if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase(ParameterConst.ORG_PROFILE_PARAM_CODE)) {
			
				retrieveOrgTypeMap();
				this.getManagedBean().setOrgProfileRequired(true);
			} else if (this.getManagedBean().getFunctionType()
					.equalsIgnoreCase(ParameterConst.USER_PROFILE_PARAM_CODE)) {
				resetUserProfilesSearchBean();
			}
		}

	}


	public String goToOrgTrackingDiarySearch(PmOrgProfile orgProfile) {
		log.debug("**********goToOrgTrackingDiarySearch************"+ orgProfile.getOrgProfileId());
		getManagedBean().setSelectedDocType(ParameterConst.ORG_PROFILE_PARAM_CODE);
		getManagedBean().setSelectedDocId(orgProfile.getOrgProfileId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();	
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	
	public String goToUserTrackingDiarySearch(PmUser user) {
		log.debug("**********goToOrgTrackingDiarySearch************"+ user.getUserId());
		getManagedBean().setSelectedDocType(ParameterConst.USER_PROFILE_PARAM_CODE);
		getManagedBean().setSelectedDocId(user.getUserId());
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();	
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	public String goToRuleConfigTrackingDiarySearch(){
		log.info("**********goToRuleConfigTrackingDiarySearch************");
		getManagedBean().setSelectedDocType2(null);
		getManagedBean().setMultiRuleType(false);
		getManagedBean().setSelectedDocType(ParameterConst.RULE_CONFIG_PARAM_CODE);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();	
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	public String goToFeeSettTrackingDiarySearch(){
		log.info("**********goToFeeSettTrackingDiarySearch************");
		getManagedBean().setSelectedDocType2(null);
		getManagedBean().setMultiRuleType(false);
		getManagedBean().setSelectedDocType(ParameterConst.FEE_EXEMPTION_PARAM_CODE);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();	
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	public String goToThresholdTrackingDiarySearch(){
		log.info("**********goToThresholdTrackingDiarySearch************");
		getManagedBean().setSelectedDocType(ParameterConst.UNPLANDED_THRESHOLD_PARAM_CODE);
		getManagedBean().setSelectedDocType2(ParameterConst.BUDGET_VS_SPENT_THRESHOLD_PARAM_CODE);
		getManagedBean().setMultiRuleType(true);
		getManagedBean().setSelectedDocId(null);
		getManagedBean().getSearchTrackingDiaryDataModel().reloadData();	
		/////
		
		return NAV_TO_PM_DETAIL_SEARCH + "?faces-redirect=true";
	}
	
	public String backToPrev() {

		return NAV_TO_PM_SEARCH + "?faces-redirect=true";
	}


	/**
	 * retrieve and set org type mapper
	 */
	private void retrieveOrgTypeMap() {
		log.info("retrieveOrgTypeMap");
		List<PmParameterDesc> parameterDescs = getOrgTypeList();
		orgTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * Retrieve Org. Type from PM
	 * 
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getOrgTypeList() {
		log.info("getOrgTypeList");
		return PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.ORGANISATION_TYPES,
						new ArrayList(
								Arrays.asList(ProfileManagementConst.PM_TRACKING_DIARY_ORG_TYPE_EXCLUDE)),null);
	}



	public void searchUserOrgProfile() {
		log.info("searchUserOrgProfile");
		this.getManagedBean().getUserOrgDataModel().reloadData();
	}
	
	public void searchUserProfile(){
		log.info(",,,searchUserProfile");
		this.getManagedBean().getUserDataModel().reloadData();
	}

	/**
	 * change organization type
	 */
	public void changeOrgType() {

		log.info("changeOrgType");
		if (this.getManagedBean().getOrganisationSearchBean() != null) {

			if (this.getManagedBean().getOrganisationSearchBean()
					.getOrgProfile() == null) {
				this.getManagedBean().getOrganisationSearchBean()
						.setOrgProfile(new PmOrgProfile());
			}
//
//			this.getManagedBean().getOrganisationSearchBean().getOrgProfile()
//					.setOrgName("");
			
			if (this.getManagedBean().getOrganisationSearchBean().getOrgType()
					.getParameterId() != null) {
				System.out.println("...........this.getManagedBean().getOrganisationSearchBean().getOrgType().getParameterId()="+this.getManagedBean().getOrganisationSearchBean().getOrgType()
						.getParameterId());
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
						.getManagedBean().getOrganisationSearchBean()
						.getOrgType().getParameterId());
				System.out.println("...........pmParameter"+pmParameter);
				System.out.println("...........pmParameter.getParameterCode()"+pmParameter.getParameterCode());
				System.out.println("...........pmParameter.getParameterCode()"+pmParameter.getParameterId());
				this.getManagedBean().getOrganisationSearchBean().getOrgType()
						.setParameterCode(pmParameter.getParameterCode());

				if (ParameterConst.ORG_TYPE_SM_BPK_HQ.equals(pmParameter
						.getParameterCode())
						|| ParameterConst.ORG_TYPE_SM_PMS_SABAH
								.equals(pmParameter.getParameterCode())
						|| ParameterConst.ORG_TYPE_SM_PMS_SARAWAK
								.equals(pmParameter.getParameterCode())
						|| ParameterConst.ORG_TYPE_BPK_HQ.equals(pmParameter
								.getParameterCode())
						|| ParameterConst.ORG_TYPE_PMS_SABAH.equals(pmParameter
								.getParameterCode())
						|| ParameterConst.ORG_TYPE_PMS_SARAWAK
								.equals(pmParameter.getParameterCode())) {
					this.getManagedBean().setOrgProfileRequired(false);
					// default org profile
					PmOrgValidity pmOrgValidity = getOrgProfile(pmParameter
							.getParameterCode());
					this.getManagedBean()
							.getOrganisationSearchBean()
							.getOrgProfile()
							.setOrgProfileId(
									pmOrgValidity.getPmOrgProfile()
											.getOrgProfileId());
					this.getManagedBean().getOrganisationSearchBean()
							.getOrgProfile()
							.setOrgName(pmOrgValidity.getOrgName());
				} else {
					this.getManagedBean().setOrgProfileRequired(true);
				}
			}
		}
	}

	/**
	 * retrieve valid org profile based on selected org type
	 * 
	 * @param paramCode
	 * @return PmOrgValidity
	 */
	private PmOrgValidity getOrgProfile(String paramCode) {
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();

		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("orgTypeCode.parameterCode");
		bean.setSearchValue(paramCode);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);

		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmOrgValidity.recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);

		PmOrgValidity pmOrgValidity = new PmOrgValidity();

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = null;
		reqDTO.setOrgType(paramCode);
		// reqDTO.setOrgProfileSearchBean(searchBean);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		resDTO = orgProfileSO.retrieveValidOrgProfile(reqDTO);
		if (resDTO.isSuccessful()) {
			pmOrgValidity = resDTO.getPmOrgValidity();
			// System.out.println("size>>>>" +
			// resDTO.getOrgProfileResultBean().getResultList().size());
			// searchOrgProfile
			// for(PmOrgProfile
			// temp:resDTO.getOrgProfileResultBean().getResultList()){
			// System.out.println(temp.getPmOrgValidity().getOrgName());
			// }
		}
		return pmOrgValidity;

	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getOrganisationSearchBean().getOrgType()
				.getParameterId());
		sb.append("|recordStatus^1");
		// if(OrganizationType.MINISTRY.value()
		// .equals(getManagedBean().getOrganisationSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }else if(OrganizationType.DEPARTMENT.value()
		// .equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else
		// if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }else if(OrganizationType.DIVISION.value()
		// .equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
		// if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else
		// if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
		// sb.append("|");
		// sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		// sb.append("^");
		// sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }else if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
		// sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		// }
		// }

		return sb.toString();
	}

}
