package my.ep.web.backingbean.pm.organisation;

import java.util.ArrayList;
import java.util.Arrays;//27868
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
import my.ep.constant.sc.QTSearchBeanConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.CommonConstants;
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
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.pm.organisation.OrgProfileSearchManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.collections.CollectionUtils;//27868
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "organisationSearchBackingBean")
@ViewScoped
public class OrgProfileSearchBackingBean extends
		BaseBackingBean<OrgProfileSearchManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger
			.getLogger(OrgProfileSearchBackingBean.class);

	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}
	
	public SelectItem[] getJabatanDropdown() {
		// start - 27868
		if (getManagedBean().isSpecialItemProcType()) {
			List<Long> allowedPtjIds = getManagedBean().getPtjIds();
			SelectItem[] allItems = PmCommonUtil.getJabatanDropdown(getManagedBean().getMinistryProfileId());

			int count = 0;
			for (int i = 1; i < allItems.length; i++) {
				Object value = allItems[i].getValue();
				if (value != null && allowedPtjIds.contains(Long.valueOf(value.toString()))) {
					count++;
				}
			}

			SelectItem[] filteredItems = new SelectItem[count + 1];

			filteredItems[0] = allItems[0];

			int idx = 1;
			for (int i = 1; i < allItems.length; i++) {
				Object value = allItems[i].getValue();
				if (value != null && allowedPtjIds.contains(Long.valueOf(value.toString()))) {
					filteredItems[idx++] = allItems[i];
				}
			}
			return filteredItems;
		}
		// end - 27868
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
	
	public void initPtjPopupDefaultMinistry(Long orgProfileId) {
		initPtjPopup();
		getManagedBean().setSpecialItemProcType(false);//#27868
		
		// #26475 - add Approver value
		//#26474 - add Publication Approver & all FC Secretaries value	
		//#35322 - Add QQC_SECRETARY
		if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.APPROVER.value())	
				|| getFacesScopes().hasRoleUser(RoleCode.PUBLICATION_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.FPB_SECRETARY.value())
				|| getFacesScopes().hasRoleUser(RoleCode.QQC_SECRETARY.value())) {
			getManagedBean().setMinistryProfileId(orgProfileId);
			getManagedBean().setDisabledMinistry(true);
		}
		
		// #27502 - Start
		getManagedBean().setMinistrySearching(true);
		// #27502 - End
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
		
		// #27502 - Start
		if (getManagedBean().isMinistrySearching() && pmOrgProfile.getPmZone() != null) {
			getManagedBean().setZone(pmOrgProfile.getPmZone().getZoneId());
		}
		// #27502 - End
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
					// 27868 - start
					if (!CollectionUtils.isEmpty(reqDTO.getPtjIds())) {
						reqDTO.getPtjIds().clear();
					}

					if (getManagedBean().isSpecialItemSearching() && getManagedBean().getJabatanProfileId() == null) {
						reqDTO.getPtjIds().addAll(getManagedBean().getPtjIds());
					}
					resDTO = orgProfileSO.retrievePtjListByParents(reqDTO);
					// 27868 - end
				}else if(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL.equals(getManagedBean().getOrgType())){
					resDTO = orgProfileSO.retrievePegaiPengawaListByParents(reqDTO);
				}else if(ParameterConst.ORG_TYPE_PTJ_GROUP.equals(getManagedBean().getOrgType())){
					resDTO = orgProfileSO.retrieveKPtjListByParents(reqDTO);
				}
				
				if (resDTO != null && resDTO.isSuccessful()) {
					return resDTO.getOrgProfileResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void searchOrgProfiles(String orgType){
		getManagedBean().setSpecialItemSearching(false); //#27868
		getManagedBean().setOrgType(orgType);
		getOrgProfileDataModel().reloadData();
	}
	
	//#27868 - start
	/**
	 * Searches organization profiles for special items based on organization
	 * type.
	 * 
	 * @param orgType
	 *            the organization type to search
	 */
	public void searchOrgProfilesSpecialItem(String orgType) {
		getManagedBean().setSpecialItemSearching(true);
		getManagedBean().setOrgType(orgType);
		getOrgProfileDataModel().reloadData();
	}
	//#27868 - end
	
	public void resetOrgProfiles(){
		Long ministryId = this.getManagedBean().getMinistryProfileId(); //#26475
		boolean specialItemProcType = this.getManagedBean().isSpecialItemProcType(); //#27868
		List<Long> allowedPtjIds = this.getManagedBean().getPtjIds();//#27868
		this.renewSesionObject();
		
		getOrgProfileDataModel().resetData();
		
		this.getManagedBean().setSpecialItemProcType(specialItemProcType);//#27868
	    this.getManagedBean().setPtjIds(allowedPtjIds);//#27868
		
		//#26475 - start
		String portletName = (String) this.getFacesScopes().getRequest().getAttribute("PORTLET_ID");

		//#26474 -start-:::
		log.info("::: resetOrgProfiles() 001 portletName: "+ portletName);
		
		if (StringUtils.isNotBlank(portletName) && (portletName.toLowerCase().contains("loa") || 
				 portletName.toLowerCase().contains("createmanualsourcing") || 
				 portletName.toLowerCase().contains("reviewmanualsourcing"))) {
			 
			log.info("::: resetOrgProfiles() portletName: "+ portletName);
			//#26474 -end-:::
			
			this.getManagedBean().setMinistryProfileId(ministryId);
			//#29173 - CR 26475: Static Code Analysis
			//start
			//#29173 - replaced if-else statement to a single method invocation
			this.getManagedBean().setDisabledMinistry(ministryId != null);
			//end #29173 - CR 26475: Static Code Analysis
		}
		//#26475 - end
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
		// #27502 - Start
		if (getManagedBean().isMinistrySearching()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_BPK, SearchOperator.OPERATOR_EQUALS.value(), true));
			List<Long> zoneIds = new ArrayList<>();

			zoneIds.add(getManagedBean().getZone());

			String portletName = (String) getFacesScopes().getRequest().getAttribute(QTConstants.PORTLET_ID);
			if (!zoneIds.contains(new Long(SupplierManagementConst.PM_ZONE_SEMENANJUNG))
					&& StringUtils.isNotBlank(portletName) && portletName.toLowerCase().contains(CommonConstants.LOA)) {
				zoneIds.add(new Long(SupplierManagementConst.PM_ZONE_SEMENANJUNG));
			}
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(QTSearchBeanConst.SEARCH_PMZONE_ZONEID, SearchOperator.OPERATOR_IN.value(),
							zoneIds));
		}
		// #27502 - End
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
	
	//start - 27868
	/**
	 * Initializes the PTJ popup for special items and configures ministry
	 * access based on user roles.
	 * 
	 * <p>
	 * This method sets the procurement type flag for special items, clears and
	 * reloads PTJ IDs, and determines whether the ministry profile should be
	 * preselected and locked for users with specific roles.
	 * </p>
	 * 
	 * @param orgProfileId
	 *            the organization profile ID of the ministry
	 * @param ptjIds
	 *            the list of PTJ (department) IDs to initialize in the popup
	 */
	public void initPtjPopupDefaultSpecialItem(Long orgProfileId, List<Long> ptjIds) {
		initPtjPopup();
		getManagedBean().setSpecialItemProcType(true);
		getManagedBean().getPtjIds().clear();

		if (!ptjIds.isEmpty()) {
			getManagedBean().getPtjIds().addAll(ptjIds);
		}

		if (userHasMinistryAccessRole()) {
			getManagedBean().setMinistryProfileId(orgProfileId);
			getManagedBean().setDisabledMinistry(true);
		}
	}

	/**
	 * Checks whether the current user holds a role that grants access to the
	 * ministry profile in the special item PTJ popup.
	 * 
	 * @return {@code true} if the user has any of the ministry-related roles;
	 *         {@code false} otherwise
	 */
	private boolean userHasMinistryAccessRole() {
		List<String> rolesWithAccess = Arrays.asList(RoleCode.DESK_OFFICER.value(), RoleCode.APPROVER.value(),
				RoleCode.PUBLICATION_APPROVER.value(), RoleCode.PBA_SECRETARY.value(), RoleCode.PBB_SECRETARY.value(),
				RoleCode.PPB_SECRETARY.value(), RoleCode.QCA_SECRETARY.value(), RoleCode.QCB_SECRETARY.value(),
				RoleCode.FPB_SECRETARY.value());

		for (String role : rolesWithAccess) {
			if (getFacesScopes().hasRoleUser(role)) {
				return true;
			}
		}
		return false;
	}
	//end - 27868
	
}
