package my.ep.web.backingbean.pm.organisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "organisationBackingBean")
@ViewScoped
public class OrganisationBackingBean extends
		BaseBackingBean<OrganisationManagedBean> {
	private static Logger log = Logger.getLogger(OrganisationBackingBean.class);

	private IPagingDataModel<PmOrgProfile> searchOrganisation = new SearchOrgPagingDataModel();

	boolean result = false;
	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	// Tab values
	public static final int PROFILE_DETAIL = 0;
	public static final int GOV_SELLER = 1;
	public static final int RESPONSIBLE_PROFILE = 2;
	private static final String NOT_RECOMMENDED_STATE = "LUAR NEGARA";
	
	// Navigation Details
	public static final String NAV_TO_SEARCH = "organisationMain.xhtml?faces-redirect=true";
	public static final String NAV_TO_MAINTAIN = "maintainOrganisation";

	// Org Category values
	public static final int FEDERAL_GOVT = 0;
	public static final int OTHERS = 1;
	
	private boolean gmapDisabled;
	/**
	 * The constructor used for save user logging.
	 * 
	 **/
	public OrganisationBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			log.info("userID--" + pmUser.getUserId());

			new LogKey(pmUser.getUserId());
		} else {
			new LogKey(1L);
		}

	}

	/**
	 * This method used for reset the search bean field and navigate to the
	 * search main page.
	 * 
	 **/
	public void preLoadingOrg() {
		log.info("preLodingOrg");
		this.resetOrganisationSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
		
	}

	/**
	 * 
	 * This method used to get the tab index for Profile Detail.
	 * 
	 **/
	public int getProfileDetails() {
		return PROFILE_DETAIL;
	}

	/**
	 * 
	 * This method used to get the tab index for Government Seller.
	 * 
	 **/
	public int getGovernmentSeller() {
		return GOV_SELLER;
	}

	/**
	 * This method used to get the tab index for Jabatan.
	 * 
	 **/
	public int getResponsibleProfile() {
		return RESPONSIBLE_PROFILE;
	}

	/**
	 * This method used to set the data table lazy loading to true, initial the
	 * organisation variables and redirect to the search main page.
	 * 
	 **/
	public String backToSearch() {
		initialOrganisation();
		return NAV_TO_SEARCH;
	}

	/**
	 * This method used for redirect the organisation page Reset the
	 * Organisation variables and set the variables. Used for new record and
	 * existing record.
	 * 
	 * @param Long
	 *            id - organisation Id (optional)
	 * @return the navigate URL
	 * @throws Exception
	 *             if an exception occurred
	 * @see Exception
	 * 
	 **/
	public String redirectToOrganisation(Long id) throws Exception {
		log.info("..............redirectToOrganisation.......id=" + id);
		resetInitialOrg();
		retrieveFinancialOrgList();

		this.getManagedBean().getDeliveryAddress()
				.setPmAddress(PmCommonUtil.getNewPmAddress());

		if (id != null && id > 0) {
			maintainOrganisation(id);

		} else {
			initialOrganisation();

		}
		getManagedBean().setShowParentMsg(true);
		RequestContext.getCurrentInstance().execute("window.scrollTo(0,0);");
		return NAV_TO_MAINTAIN;
	}

	/**
	 * This method used for initialize organisation variables.
	 * 
	 **/
	public void initialOrganisation() {

		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		PmOrgProfile orgProifle = new PmOrgProfile();
		orgProifle.setPmOrgValidity(new PmOrgValidity());
		getManagedBean().setOfficeAddress(PmCommonUtil.getNewPmAddress());
		getManagedBean().setSelectedStateId(null);
		// getManagedBean().setOfficeContactPerson(new PmContact());

		getManagedBean().setOrgType(null);
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setProfileGroup(new ArrayList<PmProfileGroup>());
		getManagedBean().setOrgProfile(orgProifle);
		// set org category to others
		getManagedBean().setOrgCategory(OTHERS);
		getManagedBean().setGstSupplier(new SmGstSupplier());
		// getManagedBean().setAddressTypesList(null);
	}

	private void resetInitialOrg() {

		this.getManagedBean().setFinancialOrg(null);
		this.getManagedBean().setProfileGroup(null);
		this.getManagedBean().setSupplierUserList(null);
		this.getManagedBean().setSupplierUsers(null);
		this.getManagedBean().setSelectedSupplierUsers(null);
		this.getManagedBean().setSelectedZoneId(null);
		this.getManagedBean().setOfficeAddress(null);
		this.getManagedBean().setOfficeContactPerson(new PmContact());
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		this.getManagedBean().setOrgTypeId(null);
		getManagedBean().setAddressTypesList(new ArrayList<PmAddressType>());
	}

	/* -- ORG PROFILE SERACH-- */

	/**
	 * This method used for cater search action.
	 * 
	 **/
	public void searchOrganisation() {
		if (getManagedBean().getOrganisationSearchBean().getOrgTypeCode() != null) {
			getManagedBean().setIslazy(true);
			getManagedBean().setNotFirstRun(true);
			searchOrganisation.reloadData();
		}

	}

	private ISearchBean<PmOrgProfile> getSearchBeanData(
			ISearchBean<PmOrgProfile> searchBean) {
		log.info("getSearchBeanData - Start");
		OrganisationSearchBean oSearchBean = this.getManagedBean()
				.getOrganisationSearchBean();

		if (oSearchBean.getOrgTypeCode() != null
				&& oSearchBean.getOrgTypeCode().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgTypeCode.parameterId",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgTypeCode()));
			
			List<PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
			PmOrgProfile orgProfile = null;
			if(userOrgList != null && !userOrgList.isEmpty()){
				orgProfile = userOrgList.get(0).getPmOrgProfile();
			}
			
			PmParameter orgTypeParameter = 
					ParameterHelper.getInstance().getPmParameterById(Long.valueOf(oSearchBean.getOrgTypeCode()));
			if(orgTypeParameter != null && orgProfile != null
					&& ParameterTypeConst.ORGANISATION_TYPES.equals(orgTypeParameter.getParameterType())){
			   if(OrganizationType.MINISTRY.value().equals(orgTypeParameter.getParameterCode())){
				   if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					   searchBean.getDataFilterBeans().add(
								new SearchFilterBean("orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
				   }
				   
			   }else if(OrganizationType.PENGAWAI_PENGAWAL.value().equals(orgTypeParameter.getParameterCode())){
				   if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					   searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.PENGAWAI_PENGAWAL.value(),OrganizationType.MINISTRY.value()),
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
						
				   }				   
			   }else if(OrganizationType.DEPARTMENT.value().equals(orgTypeParameter.getParameterCode())){
				   if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					   searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()),
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
						
				   }else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
					   searchBean.getDataFilterBeans().add(
								new SearchFilterBean("orgProfileId",
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
				   }
				   
			   }else if(OrganizationType.DIVISION.value().equals(orgTypeParameter.getParameterCode())){				   
					if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {				
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
							
					}else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
										SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
							
					}else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {						
//						// Naik Taraf / Group Org.
//						if(ParameterConst.ORG_AUTH_TYPE_ORG_GROUP.equals(orgProfile.getGroupOrgType())){
//							orgProfileSet = new HashSet<Long>(); 							
//							//add current org profile
//							orgProfileSet.add(orgProfile.getOrgProfileId());
//							//loop and add authorized profile (child list)
//							for(PmProfileGroup profileGroup : orgProfile.getMainProfileGroups()){
//								orgProfileSet.add(profileGroup.getAuthorizedOrgProfile().getOrgProfileId());
//							}	
//							searchBean.getDataFilterBeans().add(
//									new SearchFilterBean("orgProfileId",
//												SearchOperator.OPERATOR_IN.value(),new ArrayList<Long>(orgProfileSet)));
//						}else{
							searchBean.getDataFilterBeans().add(
											new SearchFilterBean("orgProfileId",
													SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
//						}
					}
			   }
			}	
		}
		

		if (oSearchBean.getOrgCode() != null
				&& oSearchBean.getOrgCode().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgCode(), true));
		}
		if (oSearchBean.getOrgName() != null
				&& oSearchBean.getOrgName().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getOrganisationSearchBean()
									.getOrgName(), true));

		}
		if (oSearchBean.getOrgStatus() != null
				&& oSearchBean.getOrgStatus().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(), oSearchBean
									.getOrgStatus()));
		} else {
			List<String> activeAndInActiveStatus = new ArrayList<String>();
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(),
							activeAndInActiveStatus));
		}
		
		Date currentDate = new Date();
		// Filter By -  effDate
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmOrgValidity.effDate",
						SearchOperator.OPERATOR_LESS_EQUAL.value(),
						currentDate));

		// Filter By -  expDate
		searchBean.getDataFilterBeans().add(
				new SearchMultiFilterBean(new SearchFilterBean(
						"pmOrgValidity.expDate",
						SearchOperator.OPERATOR_GREATER_EQUAL.value(),
						currentDate), new SearchFilterBean(
						"pmOrgValidity.expDate",
						SearchOperator.OPERATOR_IS_NULL.value(), null,
						SearchCondition.CONDITION_OR.value())));
		
		searchBean.getSortColumns().add(new SearchOrderBean("pmOrgValidity.orgName",true));
		
		
		log.info("getSearchBeanData - End");
		return searchBean;
	}

	private class SearchOrgPagingDataModel extends
			PagingDataModel<PmOrgProfile> {

		private static final long serialVersionUID = 1L;

		public SearchOrgPagingDataModel() {
			super("searchOrganisation", getManagedBean().isIslazy());
		}

		public ISearchResultBean<PmOrgProfile> pageChangeListener(
				ISearchBean<PmOrgProfile> searchBean) {
			IOrgProfileSO orgProfileSO;
			try {
				ISearchBean<PmOrgProfile> searchBeanData = getSearchBeanData(searchBean);
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

				orgProfileReqDTO.setOrgProfileSearchBean(searchBeanData);
				orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDto = orgProfileSO
						.searchOrgProfile(orgProfileReqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getOrgProfileResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	/**
	 * @return the searchOrganisation
	 */
	public IPagingDataModel<PmOrgProfile> getSearchOrganisation() {
		return searchOrganisation;
	}

	/**
	 * @param searchOrganisation
	 *            the searchOrganisation to set
	 */
	public void setSearchOrganisation(
			IPagingDataModel<PmOrgProfile> searchOrganisation) {
		this.searchOrganisation = searchOrganisation;
	}

	/**
	 * reset search bean fields
	 */
	public void resetOrganisationSearchBean() {
		log.info("resetOrgSearchBean");
		this.getManagedBean().setOrganisationSearchBean(null);
		getManagedBean().setIslazy(false);
		getManagedBean().setNotFirstRun(false);
		searchOrganisation = new SearchOrgPagingDataModel();
	}

	/* -- END ORG PROFILE SERACH-- */

	/**
	 * This method used to initialize variables for existing organisation
	 * 
	 * @param Long
	 *            id - organisation Id (mandatory)
	 * @return the navigate URL
	 **/
	public String maintainOrganisation(Long id) {
		log.info(".......maintainOrganisation......");
		// clear list's previous value
		this.getManagedBean().setSmSupplierBanks(
				new ArrayList<SmSupplierBank>());
		this.getManagedBean().setProfileGroup(new ArrayList<PmProfileGroup>());
		this.getManagedBean().setSmPersonnels(new ArrayList<SmPersonnel>());
		this.getManagedBean().setSelectedTabIndex(PROFILE_DETAIL);
		retrieveSupplierUserList(id);
		getManagedBean().setGstSupplier(new SmGstSupplier());

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(id);
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgDetailsByOrgId(reqDTO);
		PmOrgProfile orgProfile = orgResDTO.getPmOrgProfile();
		getManagedBean().setOrgType(
				orgProfile.getOrgTypeCode().getParameterCode());

		getManagedBean().setOrgTypeId(
				orgProfile.getOrgTypeCode().getParameterId());
		log.info("orgProfile.getOrgTypeCode().getParameterCode()="
				+ orgProfile.getOrgTypeCode().getParameterCode());
	
		// Kumpulan PTJ
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.PENGAWAI_PENGAWAL.value())) {
			retrieveMinistryList();
			if(orgProfile.getParentOrgProfile() == null){
				orgProfile.setParentOrgProfile(new PmOrgProfile());
			}
			
		}
		
		this.getManagedBean().setOrgProfile(orgProfile);

		// set org category
		if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_FEDERAL_GOVT)
				.contains(getManagedBean().getOrgType())) {
			getManagedBean().setOrgCategory(FEDERAL_GOVT);
		} else if (Arrays.asList(ProfileManagementConst.ORG_CATEGORY_OTHERS)
				.contains(getManagedBean().getOrgType())) {
			getManagedBean().setOrgCategory(OTHERS);
		}

		// BPK Sabah sarawak
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PENDAFTARAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().getOrgProfile().setPmZone(
					orgProfile.getPmZone() == null ? new PmZone() : orgProfile
							.getPmZone());

		}
		
	
		// PTJ
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())) {
			//retrieveMinistryList();

//			if (orgProfile.getParentOrgProfile() == null) {
//				orgProfile.setParentOrgProfile(new PmOrgProfile());
//				getManagedBean().setEmptyMinistry(true);
//			} else {
//				getManagedBean().setEmptyMinistry(false);
//			}
			
			List<PmAddressType> deliveryAddressList = new ArrayList<PmAddressType>();
			if (getManagedBean().getOrgProfile().getPmAddressTypes() != null
					&& !getManagedBean().getOrgProfile().getPmAddressTypes()
							.isEmpty()) {
				for (PmAddressType pmAddressType : getManagedBean()
						.getOrgProfile().getPmAddressTypes()) {

					if (pmAddressType.getAddressType().equalsIgnoreCase(
							AddressType.DELIVERY_ADDRESS.value()) && 
							(DbValueConst.RECORD_STATUS_ACTIVE == pmAddressType.getRecordStatusFlg() ||
								DbValueConst.RECORD_STATUS_SUSPENDED == pmAddressType.getRecordStatusFlg())) {
						if(StringUtils.isBlank(pmAddressType.getPmAddress().getPhoneCountry())){
							//default to 60
							pmAddressType.getPmAddress().setPhoneCountry("60");
						}
						deliveryAddressList.add(pmAddressType);
						// this.getManagedBean().setDeliveryContactPerson(pmAddressType.getPmContacts().get(0));
					}
				}
				
//				if(getManagedBean().getAddressTypesList() != null && !getManagedBean().getAddressTypesList().isEmpty()){
//					RequestContext requestContext = RequestContext.getCurrentInstance();
//					requestContext.update("deliveryAddressId");
//					requestContext.execute(
//							"deliveryAddres.clearFilters();");
//				}
		
			}
			
			if(!deliveryAddressList.isEmpty()){
				getManagedBean().setAddressTypesList(deliveryAddressList);
			}
			
			// Profile Groups
			if (orgProfile.getMainProfileGroups() != null
					&& !orgProfile.getMainProfileGroups().isEmpty()) {
				for (PmProfileGroup profileGroup : orgProfile
						.getMainProfileGroups()) {
					if (profileGroup.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							|| profileGroup.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						this.getManagedBean().getProfileGroup()
								.add(profileGroup);
					}
				}
			}
			
			retrievePmZoneList();
			getManagedBean().getOrgProfile().setPmZone(
					orgProfile.getPmZone() == null ? new PmZone() : orgProfile
							.getPmZone());
			if(getManagedBean().getOrgProfile().getPmZone() != null && getManagedBean().getOrgProfile().getPmZone().getZoneId() != null){
				getManagedBean().setSelectedZoneId(getManagedBean().getOrgProfile().getPmZone().getZoneId() );
			}
		}
		// PTJ, GLC, LAT, STG and STB

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.DIVISION.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.GOVERNMENT_LINK_COMPANIES.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.STATUTORY_BODIES.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.STATE_GOVERNMENT.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.LOCAL_AUTHORITY.value())) {

			if (orgProfile.getSmSupplier() != null && orgProfile.isGovtSeller()) {
				log.info("Supplier Test");
				/*
				 * this.getManagedBean().setSmSupplierBanks(
				 * orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks());
				 */
				if (orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks() != null
						&& !orgProfile.getSmSupplier().getSmAppl()
								.getSmSupplierBanks().isEmpty()) {
					for (SmSupplierBank supplierBank : orgProfile
							.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
						if (supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								|| supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
							this.getManagedBean().getSmSupplierBanks()
									.add(supplierBank);

						}
					}
				}

				log.info("Supplier Bank-----> "
						+ orgProfile.getSmSupplier().getSmAppl()
								.getSmSupplierBanks().size());
				if (orgProfile.getSmSupplier().getSmAppl().getSmPersonnels() != null
						&& !orgProfile.getSmSupplier().getSmAppl()
								.getSmPersonnels().isEmpty()) {
					for (SmPersonnel personnel : orgProfile.getSmSupplier()
							.getSmAppl().getSmPersonnels()) {
						if (personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								|| personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
							this.getManagedBean().getSmPersonnels()
									.add(personnel);

						}
					}
				}
				// this.getManagedBean().setSmPersonnels(orgProfile.getSmSupplier().getSmAppl().getSmPersonnels());
				log.info("Supplier Users ---->"
						+ orgProfile.getSmSupplier().getSmAppl()
								.getSmPersonnels().size());
				
//				if(orgProfile.getSmSupplier().getSmGstSuppliers() != null 
//						&& !orgProfile.getSmSupplier().getSmGstSuppliers().isEmpty()){
//					for(SmGstSupplier gstSupplier : orgProfile.getSmSupplier().getSmGstSuppliers()){
//						if(gstSupplier.getIsCurrent()){
//							getManagedBean().setGstSupplier(gstSupplier);
//							break;
//						}
//					}
//				}
				ISupplierGstSO supplierGstSO = null;
				try{
					supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance()
								.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
									ISupplierGstSO.class);
					SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
					gstReqDTO.setSupplierId(orgProfile.getSmSupplier().getSupplierId());
					SupplierGstResDTO gstResDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
					if(gstResDTO.isSuccessful() && gstResDTO.getSmGstSupplier() != null){
						getManagedBean().setGstSupplier(gstResDTO.getSmGstSupplier());
					}
					
				}finally{ 
					supplierGstSO = null;
				}
			}
		}
		// Org Address
		List<PmAddressType> orgAddressTypes = getManagedBean().getOrgProfile()
				.getPmAddressTypes();

		boolean addrExist = false;
		boolean contactExist = false;
		AddressReqDTO addressReqDTO = null;
		AddressResDTO addressResDTO = null;
		if (orgAddressTypes != null && !orgAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : orgAddressTypes) {
				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.BILLING_ADDRESS.value())
						&& (pmAddressType.getRecordStatusFlg().intValue() == 
								DbValueConst.RECORD_STATUS_ACTIVE)) {
					//cater for sarawak if no division
					IAddressSO addressSO = null;
					try {
						addressSO = BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
						addressReqDTO = new AddressReqDTO();
						addressReqDTO.setAddressId(pmAddressType.getPmAddress().getAddressId());
						addressResDTO = addressSO.retrieveAddressDtlById(addressReqDTO);
						if(addressResDTO != null && addressResDTO.getPmAddress() != null){
							if(addressResDTO.getPmAddress().getPmState() != null 
									&& addressResDTO.getPmAddress().getPmDivision() == null
									&& addressResDTO.getPmAddress().getPmState().getAdoptDivision()){
								pmAddressType.getPmAddress().setPmDivision(new PmDivision()); 
							}							
						}
					}finally{
						addressSO = null;
					}
					
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());
					getManagedBean().setSelectedStateId(pmAddressType.getPmAddress().getPmState().getStateId());
					addrExist = true;
					log.info("Address Exist");

					if (pmAddressType.getPmContacts() != null
							&& !pmAddressType.getPmContacts().isEmpty()) {
						this.getManagedBean().setOfficeContactPerson(
								pmAddressType.getPmContacts().get(0));
						log.info("Contact Exist");
						contactExist = true;
					}
					if (!contactExist) {
						log.info("Contact not Exist");
						List<PmContact> contactLst = new ArrayList<PmContact>();
						getManagedBean()
								.setOfficeContactPerson(new PmContact());
						PmContact contact = getManagedBean()
								.getOfficeContactPerson();
						contact.setPmAddressType(pmAddressType);
						contactLst.add(contact);
						pmAddressType.setPmContacts(contactLst);
					}					
						break;
					}
			}
		}
		if (!addrExist) {
			log.info("address not Exist");
			PmAddressType addressTypet = getManagedBean().getPmAddressType();
			orgAddressTypes.add(addressTypet);
			addressTypet.setAddressType(AddressType.BILLING_ADDRESS.value());
			getManagedBean().setOfficeAddress(PmCommonUtil.getNewPmAddress());
			PmAddress address = getManagedBean().getOfficeAddress();
			addressTypet.setPmAddress(address);
			List<PmContact> contactLst = new ArrayList<PmContact>();
			getManagedBean().setOfficeContactPerson(new PmContact());
			PmContact contact = getManagedBean().getOfficeContactPerson();
			contact.setPmAddressType(addressTypet);
			contactLst.add(contact);
			addressTypet.setPmContacts(contactLst);
			addressTypet.setPmOrgProfile(orgProfile);
			orgProfile.setPmAddressTypes(orgAddressTypes);
		}
		return NAV_TO_SEARCH;
	}

	/**
	 * This method used to save organisation data.
	 * 
	 * @param String
	 *            outcome - button action trigged by user (mandatory)
	 * @return the navigate URL
	 **/
	public String saveOrganization(String outcome) throws Exception {

		IOrgProfileSO orgProfileSO = null;
		IParameterSO parameterSO = null;
		String redirectUrl = null;

		try {
			PmOrgProfile orgProfile = this.getManagedBean().getOrgProfile();			
			// check govSeller
			if (orgProfile.isGovtSeller()) {
				validateSupplierBankListAndSupplierPersonnel();
				validateGstDetails();
			}
			
			// check group org
			if (ParameterConst.ORG_AUTH_TYPE_ORG_GROUP.equals(orgProfile.getGroupOrgType())) {
				validateGroupOrg();				
			}
			
			validateOrgProfile(orgProfile);
			validateOrgAddress(this.getManagedBean().getOfficeAddress());
			validateContactPerson(this.getManagedBean()
					.getOfficeContactPerson());

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			parameterSO = (IParameterSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO
					.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
			parameterReqDTO.setParameterCode(getManagedBean().getOrgType());
			ParameterResDTO parameterResDTO = parameterSO
					.retrieveParameterByTypeAndCode(parameterReqDTO);			
			PmOrgValidity orgValidity = orgProfile.getPmOrgValidity();
			
			List<PmProfileGroup> profileGroup = new ArrayList<PmProfileGroup>();
			if (orgProfile.isBrandNew()) {
				// check BPK
				checkBpkOrgStatus(parameterResDTO.getPmParameter()
						.getParameterCode());
				orgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
				orgValidity.setEffDate(new Date());
				PmAddressType addressType = this.getManagedBean()
						.getPmAddressType();
				addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
				List<PmAddressType> pmAddressTypes = this.getManagedBean()
						.getOfficeAddressTypeList();
				pmAddressTypes.add(addressType);
				PmAddress address = this.getManagedBean().getOfficeAddress();
				PmContact contact = this.getManagedBean()
						.getOfficeContactPerson();
				contact.setPmAddressType(addressType);
				List<PmContact> contactLst = new ArrayList<PmContact>();
				contactLst.add(contact);
				addressType.setPmContacts(contactLst);
				addressType.setPmAddress(address);
				addressType.setPmOrgProfile(orgProfile);
				orgProfile.setPmAddressTypes(pmAddressTypes);
				orgValidity.setPmOrgProfile(orgProfile);
				orgProfile.setPmOrgValidity(orgValidity);
			}
			
			if (parameterResDTO
					.getPmParameter()
					.getParameterCode()
					.equalsIgnoreCase(
							OrganizationType.BPK_PENDAFTARAN_HQ.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.BPK_PENDAFTARAN_SABAH.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.BPK_PENDAFTARAN_SARAWAK.value())) {
				orgProfile.setPmZone(getManagedBean().getOrgProfile()
						.getPmZone());
			}
			
			// PTJ, GLC, LAT, STG , STS and STB
			if (getManagedBean().getOrgType().equalsIgnoreCase(
					OrganizationType.DIVISION.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATE_GOVERNMENT.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATUTORY_BODIES.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATUTORY_BODIES_STATE.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.LOCAL_AUTHORITY.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.GOVERNMENT_LINK_COMPANIES.value())) {

				if (orgProfile.isGovtSeller()) {
					reqDTO.setSupplierUsers(getManagedBean()
							.getSupplierUsers());
					reqDTO.setSupplierBanks(getManagedBean().getSmSupplierBanks());
					reqDTO.setSmPersonnels(getManagedBean().getSmPersonnels());
					SmGstSupplier gstSupplier = getManagedBean().getGstSupplier();
					if(StringUtils.isNotBlank(gstSupplier.getGstRegNo())){
						reqDTO.setGstSupplier(gstSupplier);
					}else{
						if(!gstSupplier.isBrandNew()){
							reqDTO.setGstSupplier(gstSupplier);
						}
					}
				} else {
					log.info("Deactvate Supplier=======");
					reqDTO.setSupplierBanks(getManagedBean().getSmSupplierBanks());
					reqDTO.setSmPersonnels(getManagedBean().getSmPersonnels());				
				}
			}			
			
			// PTJ
			if (parameterResDTO.getPmParameter().getParameterCode()
					.equalsIgnoreCase(OrganizationType.DIVISION.value())) {			
				// Validate the Delivery Address
				for (PmAddressType deliveryAddress : getManagedBean()
						.getAddressTypesList()) {
					if (deliveryAddress.getAddressType().equals(
							AddressType.DELIVERY_ADDRESS.value())) {
						log.info("Validate Delivery Address"
								+ deliveryAddress.getPmAddress().getAddress1());
						validateDeliveryAddress(deliveryAddress.getPmAddress());
					}
				}
				
				// Billing Address
				for (PmAddressType addressType : getManagedBean()
						.getOrgProfile().getPmAddressTypes()) {
					if (addressType.getAddressType().equals(
							AddressType.BILLING_ADDRESS.value())
							&& (addressType.getRecordStatusFlg().intValue() == 
									DbValueConst.RECORD_STATUS_ACTIVE)
							&& !getManagedBean().getAddressTypesList()
									.contains(addressType)) {
						getManagedBean().getAddressTypesList().add(addressType);
					}
				}

				// Pm Address Types
				orgProfile.setPmAddressTypes(getManagedBean()
						.getAddressTypesList());
				// New OR Update Profile Groups
				if (ParameterConst.ORG_AUTH_TYPE_ORG_GROUP.equals(getManagedBean().getOrgProfile().getGroupOrgType())) {
					List<PmProfileGroup> profileGrp = getManagedBean()
							.getProfileGroup();

					for (PmProfileGroup proGroup : profileGrp) {
						if (proGroup.isBrandNew()) {
							proGroup.setMainOrgProfile(orgProfile);
						}
					}
					profileGroup.addAll(profileGrp);
					orgProfile.setMainProfileGroups(profileGroup);
				}
				// Suspend profileGroups
				else {
					if (getManagedBean().getProfileGroup() != null
							&& !getManagedBean().getProfileGroup().isEmpty()) {

						for (PmProfileGroup proGroup : getManagedBean()
								.getProfileGroup()) {
							proGroup.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							log.info("Profile Suspended");
							profileGroup.add(proGroup);
							orgProfile.setMainProfileGroups(profileGroup);
						}
					}
				}
				
				// remove pm zone if bpk = false;
				if(getManagedBean().getOrgProfile().getBpk() !=null && 
						getManagedBean().getOrgProfile().getBpk() == false){
					getManagedBean().getOrgProfile().setPmZone(null);
				}else{
					if(getManagedBean().getSelectedZoneId() != null){
						if(getManagedBean().getOrgProfile().getPmZone() == null){
							getManagedBean().getOrgProfile().setPmZone(new PmZone());
						}
						getManagedBean().getOrgProfile().getPmZone().setZoneId(
								getManagedBean().getSelectedZoneId() );
					}
				}
				
			}
			
			reqDTO.setPmOrgProfile(orgProfile);
			reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
			reqDTO.setUser(getFacesScopes().getUserSessionBean());
			OrgProfileResDTO resDTO = orgProfileSO.saveOrgProfile(reqDTO);
			/*
			 * reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
			 * OrgProfileResDTO resDTO1 = orgProfileSO
			 * .retrieveOrgDetailsByOrgId(reqDTO); if (resDTO1 != null &&
			 * resDTO1.getPmOrgProfile() != null) {
			 * getManagedBean().setOrgProfile(resDTO1.getPmOrgProfile()); }
			 */

			String message = "";
			if (BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)) {
				message = ResourceBundleUtils
						.getMessage("lbl.pm.org.created.successfully",
								new String[] {
										getManagedBean().getOrgProfile()
												.getOrgName() },
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				redirectUrl = "organisationMain.xhtml";
			} else if (BpmOutcomeConst.BPM_UPDATE.equalsIgnoreCase(outcome)) {
				message = ResourceBundleUtils
						.getMessage("lbl.pm.org.updated.successfully",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				redirectToOrganisation(getManagedBean().getOrgProfile()
						.getOrgProfileId());
			}

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								null, message));
			}
		} finally {
			orgProfileSO = null;
			parameterSO = null;
		}

		return redirectUrl;

	}

	private void validateOrgProfile(PmOrgProfile orgProfile) {

		if (StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgCode())) {
			throw new EpException(MessageConst.ERROR_775105);
		}
		if (StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgName())) {
			throw new EpException(MessageConst.ERROR_775106);
		}
		
		if(DbValueConst.RECORD_STATUS_ACTIVE == orgProfile.getRecordStatusFlg()){
			//For Ministry, Statutory Body, GLC, State Govt and Local Authority 
			if (getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATE_GOVERNMENT.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATUTORY_BODIES.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATUTORY_BODIES_STATE.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.LOCAL_AUTHORITY.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.GOVERNMENT_LINK_COMPANIES.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.MINISTRY.value())) {
				validateUniqueOrgCode(orgProfile,getManagedBean().getOrgType());
				validateUniqueOrgName(orgProfile,getManagedBean().getOrgType());
			}
		}
		

	}

	private void validateContactPerson(PmContact contactPerson) {

		if (StringUtils.isBlank(contactPerson.getContactName())) {
			throw new EpException(MessageConst.ERROR_775113);
		}
		if (StringUtils.isBlank(contactPerson.getEmail())) {
			throw new EpException(MessageConst.ERROR_775114);
		}

	}

	private boolean validateOrgAddress(PmAddress pmAddress) {

		boolean addressEntered = false;

		// address is optional
		if (pmAddress.getPmCountry().getCountryId() != null
				|| !StringUtils.isBlank(pmAddress.getAddress1())
				|| !StringUtils.isBlank(pmAddress.getPostcode())
				|| pmAddress.getPmState().getStateId() != null
				|| pmAddress.getPmDistrict().getDistrictId() != null) {
			addressEntered = true;
		}
		if (addressEntered) {
			if (pmAddress.getPmCountry().getCountryId() == null) {
				throw new EpException(MessageConst.ERROR_775107);
			}

			if (StringUtils.isBlank(pmAddress.getAddress1())) {
				throw new EpException(MessageConst.ERROR_775108);
			}

			if (StringUtils.isBlank(pmAddress.getPostcode())) {
				throw new EpException(MessageConst.ERROR_775109);
			}

			if (pmAddress.getPmState().getStateId() == null) {
				throw new EpException(MessageConst.ERROR_775110);
			}

			if (pmAddress.getPmDistrict().getDistrictId() == null) {
				throw new EpException(MessageConst.ERROR_775111);
			}
			if (pmAddress.getPmCity().getCityId() == null) {
				throw new EpException(MessageConst.ERROR_775112);
			}
			if (pmAddress.getPmState().getAdoptDivision()) {
				if (pmAddress.getPmDivision() == null || pmAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_770044);
				}
			} else {
				log.info("set division null");
				pmAddress.setPmDivision(null);
			}
			
			if(StringUtils.isBlank(pmAddress.getPhoneCountry())){
				throw new EpException(MessageConst.ERROR_775136);
			}
			
			if(StringUtils.isBlank(pmAddress.getPhoneArea())){
				throw new EpException(MessageConst.ERROR_775135);
			}
			
			if(StringUtils.isBlank(pmAddress.getPhoneNo())){
				throw new EpException(MessageConst.ERROR_775128);
			}	
			
			PmAddressType addressType = null;
			for(PmUser user : getManagedBean().getSupplierUsers()){
				addressType = new PmAddressType();
				addressType.setAddressType(AddressType.CONTACT_ADDRESS.value());
				addressType.setPmAddress(pmAddress);
				user.setAddressTypeC(addressType);
			}
			
			for(SmPersonnel personnel : getManagedBean().getSmPersonnels()){
				addressType = new PmAddressType();
				addressType.setAddressType(AddressType.CONTACT_ADDRESS.value());
				addressType.setPmAddress(pmAddress);
				personnel.getPmUser().setAddressTypeC(addressType);
			}
		}

		return addressEntered;
	}

	private boolean validateDeliveryAddress(PmAddress deliverAddress) {

		boolean addressEntered = false;

		// address is optional
		if (deliverAddress.getPmCountry().getCountryId() != null
				|| !StringUtils.isBlank(deliverAddress.getAddress1())
				|| !StringUtils.isBlank(deliverAddress.getPostcode())
				|| deliverAddress.getPmState().getStateId() != null
				|| deliverAddress.getPmDistrict().getDistrictId() != null) {
			addressEntered = true;
		}
		if (addressEntered) {
			if (deliverAddress.getPmCountry().getCountryId() == null) {
				throw new EpException(MessageConst.ERROR_775121);
			}

			if (StringUtils.isBlank(deliverAddress.getAddress1())) {
				throw new EpException(MessageConst.ERROR_775122);
			}

			if (StringUtils.isBlank(deliverAddress.getPostcode())) {
				throw new EpException(MessageConst.ERROR_775123);
			}

			if (deliverAddress.getPmState().getStateId() == null) {
				throw new EpException(MessageConst.ERROR_775124);
			}

			if (deliverAddress.getPmDistrict().getDistrictId() == null) {
				throw new EpException(MessageConst.ERROR_775125);
			}
			if (deliverAddress.getPmCity().getCityId() == null) {
				throw new EpException(MessageConst.ERROR_775126);
			}
			if (deliverAddress.getPmState().getAdoptDivision()) {
				if (deliverAddress.getPmDivision() == null || deliverAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_775127);
				}
			} else {

				deliverAddress.setPmDivision(null);
			}
			
			if(StringUtils.isBlank(deliverAddress.getPhoneCountry())){
				throw new EpException(MessageConst.ERROR_775134);
			}
			
			if(StringUtils.isBlank(deliverAddress.getPhoneArea())){
				throw new EpException(MessageConst.ERROR_775133);
			}
			
			if(StringUtils.isBlank(deliverAddress.getPhoneNo())){
				throw new EpException(MessageConst.ERROR_775129);
			}
			
		
		}

		return addressEntered;
	}

	/**
	 * This medhod used for reset the org profile page based on the type if
	 * trigger change in organisation type selection list.
	 * 
	 **/
	public void resetOrgProfile() {
		getManagedBean().setOrgProfile(new PmOrgProfile());
		getManagedBean().getOrgProfile().setPmOrgValidity(new PmOrgValidity());
//		getManagedBean().setOfficeAddress(PmCommonUtil.getNewPmAddress());
//		getManagedBean().setSelectedStateId(null);
//		getManagedBean().setOfficeContactPerson(new PmContact());
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(getManagedBean().getOrgTypeId());
		ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
		this.getManagedBean().setOrgType(
				resDTO.getPmParameter().getParameterCode());
		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PENDAFTARAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SABAH.value())
						|| getManagedBean().getOrgType().equalsIgnoreCase(
								OrganizationType.BPK_PENDAFTARAN_SARAWAK.value())) {
			retrievePmZoneList();
			getManagedBean().getOrgProfile().setPmZone(new PmZone());
		}
		checkBpkOrgStatus(resDTO.getPmParameter().getParameterCode());
	}

	private void checkBpkOrgStatus(String orgCode) {

		OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
		orgReqDTO.setOrgType(orgCode);

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgProfileByOrgType(orgReqDTO);
		orgResDTO.setSuccessful(true);

		if (getManagedBean().getOrgType().equalsIgnoreCase(
				OrganizationType.BPK_PENDAFTARAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PENDAFTARAN_SARAWAK.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_HQ.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SABAH.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value())
				|| getManagedBean().getOrgType().equalsIgnoreCase(
						OrganizationType.JABATAN_AUDIT_NEGARA.value())) {
			throw new EpException(MessageConst.ERROR_775104);

		}
	}

	/**
	 * 
	 * This method used to pop up/show the contact person dialog and set show
	 * parent error message to false.
	 * 
	 **/
	public void addContacePerson() {
		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.show()");
		getManagedBean().setShowParentMsg(false);
	}

	/**
	 * 
	 * This method used to hide the contact person dialog and set show parent
	 * error message to true.
	 * 
	 **/
	public void okContacePerson() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}

	/**
	 * 
	 * This method used to hide the contact person dialog and set show parent
	 * error message to true.
	 * 
	 **/
	public void closeContacePerson() {

		RequestContext.getCurrentInstance()
				.execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}

	private void retrievePmZoneList() {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);
		ZoneResDTO resDTO = zoneSO.retrieveActiveZoneList();
		this.getManagedBean().setZoneList(resDTO.getZoneList());
	}

	/**
	 * 
	 * This method used to add new delivery address
	 * 
	 **/
	public void addDeliveryAddr() {
		log.info("Add Delivery Address");

		PmAddressType addressType = new PmAddressType();
		addressType.setAddressType(AddressType.DELIVERY_ADDRESS.value());
		addressType.setPmAddress(PmCommonUtil.getNewPmAddress());
		/*
		 * List<PmContact> contactList= new ArrayList<PmContact>();
		 * 
		 * getManagedBean().setDeliveryContactPerson(new PmContact());
		 * contactList.add(this.getManagedBean().getDeliveryContactPerson());
		 * addressType.setPmContacts(contactList);
		 */
		getManagedBean().setDeliveryAddress(addressType);
	}

	/**
	 * 
	 * This method used to update the delivery address in pop up dialog and hide
	 * the dialog after that.
	 * 
	 **/
	public void updateDeliveryAddrDt() {

		PmAddressType deliveryAddr = getManagedBean().getDeliveryAddress();
		PmAddress manualAddr = deliveryAddr.getPmAddress();
		if (manualAddr.isBrandNew()) {
			deliveryAddr.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			deliveryAddr.setPmOrgProfile(getManagedBean().getOrgProfile());
			deliveryAddr.setPmAddress(manualAddr);
		}

		if (manualAddr.getPmState() != null
				&& manualAddr.getPmState().getStateId() != null) {
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setStateId(manualAddr.getPmState().getStateId());
			StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
			manualAddr.setPmState(resDTO.getPmState());
		}

		if (manualAddr.getPmDistrict() != null
				&& manualAddr.getPmDistrict().getDistrictId() != null) {
			IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IDistrictSO.class);
			DistrictReqDTO districtReqDTO = new DistrictReqDTO();
			districtReqDTO.setDistrictId(manualAddr.getPmDistrict()
					.getDistrictId());
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictByID(districtReqDTO);
			manualAddr.setPmDistrict(districtResDTO.getPmDistrict());
		}

		if (manualAddr.getPmCity() != null
				&& manualAddr.getPmCity().getCityId() != null) {
			ICitySO citySO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICitySO.class);
			CityReqDTO cityReqDTO = new CityReqDTO();
			cityReqDTO.setCityId(manualAddr.getPmCity().getCityId());
			CityResDTO cityResDTO = citySO.retriveCityById(cityReqDTO);
			manualAddr.setPmCity(cityResDTO.getPmCity());
		}
		validateDeliveryAddress(manualAddr);
		if (manualAddr.isBrandNew() && !manualAddr.isModified()) {
			getManagedBean().getAddressTypesList().add(deliveryAddr);
			deliveryAddr.setPmAddress(manualAddr);
		} else {
			for (int i = 0; i < getManagedBean().getAddressTypesList().size(); i++) {
				PmAddressType adressType = getManagedBean()
						.getAddressTypesList().get(i);
				PmAddress address = adressType.getPmAddress();

				if ((!manualAddr.isBrandNew())
						&& address.getAddressId() != null
						&& address.getAddressId().equals(
								manualAddr.getAddressId())) {
					getManagedBean().getAddressTypesList().set(i, adressType);
					deliveryAddr.setPmAddress(manualAddr);
				} else if (address.getAddressName().equals(
						manualAddr.getAddressName())) {
					getManagedBean().getAddressTypesList().set(i, adressType);
					deliveryAddr.setPmAddress(manualAddr);
				}
			}
		}

		RequestContext.getCurrentInstance().execute("addressPopup.hide();");	

	}

	/**
	 * This method used to edit delivery address.
	 * 
	 **/
	public void editDeliveryAddress(PmAddressType address) {

		boolean addrExist = false;
		if (address != null) {
			
			if (address.getAddressType().equalsIgnoreCase(
					AddressType.DELIVERY_ADDRESS.value())) {
				if (address.getPmAddress().getPmState() != null &&address.getPmAddress().getPmState().getAdoptDivision()
					 && address.getPmAddress().getPmDivision() == null ) {
					address.getPmAddress().setPmDivision(new PmDivision());
				}
				this.getManagedBean().setDeliveryAddress(address);
				addrExist = true;
			}

			/*
			 * if (address.getPmContacts() != null &&
			 * !address.getPmContacts().isEmpty()) {
			 * this.getManagedBean().setOfficeContactPerson
			 * (address.getPmContacts().get(0)); } else{ List<PmContact>
			 * contactLst = new ArrayList<PmContact>();
			 * this.getManagedBean().setDeliveryContactPerson(new PmContact());
			 * PmContact contact =
			 * this.getManagedBean().getOfficeContactPerson();
			 * contact.setPmAddressType(address); contactLst.add(contact);
			 * address.setPmContacts(contactLst); }
			 */

		}
		if (!addrExist) {

			List<PmAddressType> orgAddressTypes = this.getManagedBean()
					.getAddressTypesList();
			// List<PmAddressType> orgAddressTypes =
			// this.getManagedBean().getOrgProfile().getPmAddressTypes();
			PmAddressType addressTypet = getManagedBean().getPmAddressType();
			orgAddressTypes.add(addressTypet);
			addressTypet.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			getManagedBean().setOfficeAddress(PmCommonUtil.getNewPmAddress());
			PmAddress officeAddress = getManagedBean().getOfficeAddress();
			addressTypet.setPmAddress(officeAddress);
			/*
			 * List<PmContact> contactLst = new ArrayList<PmContact>();
			 * getManagedBean().setDeliveryContactPerson(new PmContact());
			 * PmContact contact = getManagedBean().getDeliveryContactPerson();
			 * contact.setPmAddressType(addressTypet); contactLst.add(contact);
			 * addressTypet.setPmContacts(contactLst);
			 */
			addressTypet.setPmOrgProfile(this.getManagedBean().getOrgProfile());
			// orgProfile.setPmAddressTypes(orgAddressTypes);
			// orgAddressTypes.add(addressTypet);
			this.getManagedBean().getOrgProfile()
					.setPmAddressTypes(orgAddressTypes);
		}
		if (address != null) {
			if (address.getPmAddress().isBrandNew()) {
				address.getPmAddress().setModified(true);
			}
			address.setEditable(true);
		}
	}

	/**
	 * 
	 * This method used to delete the selected delivery address in managed bean
	 * if the address is new record.
	 * 
	 **/
	public void deleteSelectedAdddress() {
		if (getManagedBean().getSelectedAddressTypes().size() > 0) {
			for (PmAddressType address : getManagedBean()
					.getSelectedAddressTypes()) {
				if (address.isBrandNew() && address.getAddressTypeId() == null) {
					getManagedBean().getAddressTypesList().remove(address);
				}
			}
			getManagedBean().getSelectedAddressTypes().clear();
		} else {
			log.info("Error");
		}
	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = null;
		if (getManagedBean().getSearchOrgCategory() == FEDERAL_GOVT){
			searchBean = getOrgTypeSearchBean("orgTypeFederalList");
		}else if (getManagedBean().getSearchOrgCategory() == OTHERS) {
			searchBean = getOrgTypeSearchBean("orgTypeOthersList");
		}
		
		//List<String> recommendList = new ArrayList<String>();
//		recommendList.add(OrganizationType.EP_PROFILE.value());
//	//	recommendList.add(OrganizationType.DEPARTMENT.value());
//		recommendList.add(OrganizationType.FACTORING_COMPANY.value());
//		recommendList.add(OrganizationType.SUPPLIER.value());
//		recommendList.add(OrganizationType.OTHERS.value());
//
//		if (getManagedBean().getSearchOrgCategory() == FEDERAL_GOVT) {
//			recommendList.add(OrganizationType.JABATAN_AUDIT_NEGARA.value());
//			recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES
//					.value());
//			recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());
//			recommendList.add(OrganizationType.STATUTORY_BODIES.value());
//			recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
//			recommendList.add(OrganizationType.SUPPORT.value());
//			
//			// check if user is ministry admin,jabatan admin or ptj admin
//			if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {			
//				recommendList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
//						.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
//				recommendList
//						.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
//			}else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
//				recommendList.add(OrganizationType.MINISTRY.value());
//				recommendList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
//						.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
//				recommendList
//						.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
//			}else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
//				recommendList.add(OrganizationType.MINISTRY.value());
//				recommendList.add(OrganizationType.DEPARTMENT.value());
//				recommendList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
//						.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
//				recommendList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
//				recommendList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
//				recommendList
//						.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
//			}
//			
//			// recommendList.add(OrganizationType.OTHERS.value());
//		} else if (getManagedBean().getSearchOrgCategory() == OTHERS) {
//			recommendList.add(OrganizationType.MINISTRY.value());
//			recommendList.add(OrganizationType.DEPARTMENT.value());
//			recommendList.add(OrganizationType.DIVISION.value());
//			recommendList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
//					.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
//			recommendList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
//			recommendList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
//			recommendList
//					.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
//		}

//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("pmParameter.parameterCode",
//						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = null;
		if(searchBean != null){
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}		

		return items;
	}

	private ISearchBean<PmParameterDesc> getOrgTypeSearchBean(String componentId) {		
		ISearchBean<PmParameterDesc> orgTypeSearchBean = super.createSearchBean(
						PmParameterDesc.class, componentId);
		return orgTypeSearchBean;
	}
	
	/**
	 * The method used for generating a list of available display type of new
	 * org type as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getNewOrgTypeDropdownValues() {

		ISearchBean<PmParameterDesc> searchBean = null;
		if (getManagedBean().getOrgCategory() == FEDERAL_GOVT){
			searchBean = getOrgTypeSearchBean("newOrgTypeFederalList");
			if(getManagedBean().getOrgProfile().isBrandNew()){
				List<String> excludeList = new ArrayList<String>();
				excludeList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
						.value());
				excludeList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
				excludeList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
				excludeList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
				excludeList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
				excludeList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
				excludeList
						.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
		
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_NOT_IN.value(), excludeList));
			}
		}else if (getManagedBean().getOrgCategory() == OTHERS) {
			searchBean = getOrgTypeSearchBean("newOrgTypeOtherList");
		}
		
		
//		List<String> recommendList = new ArrayList<String>();
//		recommendList.add(OrganizationType.EP_PROFILE.value());
//		recommendList.add(OrganizationType.DEPARTMENT.value());
//		recommendList.add(OrganizationType.FACTORING_COMPANY.value());
//		recommendList.add(OrganizationType.SUPPLIER.value());
//		recommendList.add(OrganizationType.OTHERS.value());
//		recommendList.add(OrganizationType.DIVISION.value());
//		//recommendList.add(OrganizationType.SUPPORT.value());
//
//		if (getManagedBean().getOrgCategory() == FEDERAL_GOVT) {
//			recommendList.add(OrganizationType.JABATAN_AUDIT_NEGARA.value());
//			recommendList.add(OrganizationType.GOVERNMENT_LINK_COMPANIES
//					.value());
//			recommendList.add(OrganizationType.LOCAL_AUTHORITY.value());
//			recommendList.add(OrganizationType.STATUTORY_BODIES.value());
//			recommendList.add(OrganizationType.STATE_GOVERNMENT.value());
//		} else if (getManagedBean().getOrgCategory() == OTHERS) {
//			recommendList.add(OrganizationType.MINISTRY.value());
//			recommendList.add(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN
//					.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_HQ.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_SABAH.value());
//			recommendList.add(OrganizationType.BPK_PENDAFTARAN_SARAWAK.value());
//			recommendList.add(OrganizationType.BPK_PEROLEHAN_HQ.value());
//			recommendList.add(OrganizationType.BPK_PEROLEHAN_PM_SABAH.value());
//			recommendList
//					.add(OrganizationType.BPK_PEROLEHAN_PM_SARAWAK.value());
//		}
//
//		searchBean.getDataFilterBeans().add(
//				new SearchFilterBean("pmParameter.parameterCode",
//						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = null;
		if(searchBean != null){
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		}		
		
		return items;
	}

	/**
	 * The method used for generating a list of available display type of all
	 * org type as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getAllOrgTypeDropdownValues(String itemType) {

		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getSortColumns();

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.ORGANISATION_TYPES, searchBean);
		return items;
	}

	private void retrieveFinancialOrgList() {
		IFinancialOrgSO financialOrgSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
		FinancialOrgResDTO resDTO = financialOrgSO
				.retrieveFinancialOrgListByServiceType(reqDTO);

		this.getManagedBean().setFinancialBanks(resDTO.getFinancialOrgList());
	}

	/**
	 * The method used for add supplier bank in government seller tab.
	 * 
	 */
	public void addSupplierBank() {
		try {
			if (this.getManagedBean().getSmSupplierBanks() == null) {
				this.getManagedBean().setSmSupplierBanks(
						new ArrayList<SmSupplierBank>());
			}
			SmSupplierBank newSupplierBank = new SmSupplierBank();
			newSupplierBank.setPmFinancialOrg(new PmFinancialOrg());
			newSupplierBank.setAccountPurpose("P");
			newSupplierBank.setForHqFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			
			if(getManagedBean().getOrgProfile().getSmSupplier() !=null
					&& getManagedBean().getOrgProfile().getSmSupplier().getSmAppl() !=null){
				newSupplierBank.setSmAppl(getManagedBean().getOrgProfile().getSmSupplier().getSmAppl());
			}
			int index = this.getManagedBean().getSmSupplierBanks().size();
			this.getManagedBean().getSmSupplierBanks()
					.add(index, newSupplierBank);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

//	/**
//	 * The method used for delete supplier bank in government seller tab.
//	 * 
//	 */
//	public void deleteSupplierBank(int index) {
//		SmSupplierBank smSupplierBank = this.getManagedBean()
//				.getSmSupplierBanks().get(index);
//		smSupplierBank.setDeleted(true);
//		this.getManagedBean().getDeletedSupplierBankList().add(smSupplierBank);
//		// should added to original list before save
//		this.getManagedBean().getSmSupplierBanks().remove(smSupplierBank);
//
//	}
	
	/**
	 * The method used for delete supplier bank in government seller tab.
	 * 
	 */
	public void deleteSupplierBank() {
		SmSupplierBank smSupplierBank = this.getManagedBean().getSelectedDeletePaymentBank();
		smSupplierBank.setDeleted(true);
		this.getManagedBean().getDeletedSupplierBankList().add(smSupplierBank);
		// should added to original list before save
		this.getManagedBean().getSmSupplierBanks().remove(smSupplierBank);

	}

	private void retrieveMinistryList() {
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByMinistry(reqDTO);
		this.getManagedBean().setOrgProfileList(resDTO.getOrgProfileList());
	}

	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}

	/**
	 * This method used for update organisation code in Jabatan tab.
	 * 
	 * @param pmOrgProfile
	 *            (mandatory)
	 * @param index
	 *            (mandatory)
	 * 
	 **/
//	public void updateOrgCode(BaseEntity entity) {
//		PmOrgProfile pmOrgProfile = (PmOrgProfile) entity;
//		boolean isValid = true;
//		if (pmOrgProfile != null && pmOrgProfile.getOrgProfileId() != null) {
//			if (this.getManagedBean().getSelectedProfileGroups() != null
//					&& !this.getManagedBean().getSelectedProfileGroups()
//							.isEmpty()) {
//				int i = 0;
//				for (PmProfileGroup profileGroup : this.getManagedBean()
//						.getSelectedProfileGroups()) {
//					if (profileGroup.getMainOrgProfile().getOrgProfileId()
//							.equals(pmOrgProfile.getOrgProfileId())) {
//						isValid = false;
//						break;
//					}
//					i++;
//				}
//				if (!isValid) {
//					this.getManagedBean().getSelectedProfileGroups().get(i)
//							.getAuthorizedOrgProfile()
//							.setPmOrgValidity(new PmOrgValidity());
//					throw new EpException(MessageConst.ERROR_770019);
//				}
//
//			}
//		}
//	}

	/**
	 * This method used for add organisation unit in Jabatan tab.
	 * 
	 * 
	 **/
	public void addOrgUnit() {
		try {
			List<PmProfileGroup> ptjList = this.getManagedBean()
					.getProfileGroup();
			if (null == ptjList) {
				ptjList = new ArrayList<PmProfileGroup>();
			}
			this.getManagedBean().getProfileGroup().add(new PmProfileGroup());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}

	}

	/**
	 * This method used for delete organisation unit in Jabatan tab.
	 * 
	 * 
	 **/
	public void deleteOrgUnit() {
		PmProfileGroup pmProfileGroup = getManagedBean().getSelectedDeleteOrgUnit();
		if (pmProfileGroup != null && pmProfileGroup.isBrandNew()) {
			if (this.getManagedBean().getProfileGroup() == null) {
				this.getManagedBean().setProfileGroup(
						new ArrayList<PmProfileGroup>());
			}
			this.getManagedBean().getProfileGroup().remove(pmProfileGroup);
		}
	}

	private void retrieveSupplierUserList(Long id) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setOrgProfileId(id);

		UserResDTO resDTO = userSO.retrieveUserListByOrgProfileId(reqDTO);

		this.getManagedBean().setSupplierUserList(resDTO.getUserList());
	}

	/**
	 * This method used to add new personnel in government seller tab.
	 * 
	 **/
	public void addPersonnel() {

		if (this.getManagedBean().getSmPersonnels() == null) {
			this.getManagedBean().setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (this.getManagedBean().getSelectedSupplierUsers() != null) {
			this.getManagedBean().getSelectedSupplierUsers().clear();
		}
		// int index = this.getManagedBean().getSmPersonnels().size();
		// this.getManagedBean().getSmPersonnels().add(index , new
		// SmPersonnel());

	}

	/**
	 * This method used to delete personnel in government seller tab.
	 * 
	 **/
	public void deletePersonnel() {

		SmPersonnel smPersonnel = this.getManagedBean().getSelectedDeleteSupplierUser();
		smPersonnel.setDeleted(true);
		Long selectedUserId = smPersonnel.getPmUser().getUserId();
		
		// this.getManagedBean().getDeletedSmPersonnels()
		// .add(smPersonnel);// should added to original list before
		// // save
		this.getManagedBean().getSmPersonnels().remove(smPersonnel);
		
		if(selectedUserId != null && selectedUserId.longValue() > 0) {
			if(this.getManagedBean().getSupplierUsers() != null && this.getManagedBean().getSupplierUsers().size() > 0) {
				for(int i = 0; i < this.getManagedBean().getSupplierUsers().size(); i++) {
					PmUser selectedUser = this.getManagedBean().getSupplierUsers().get(i);
					if(selectedUser.getUserId().longValue() == selectedUserId.longValue()) {
						getManagedBean().getSupplierUsers().remove(selectedUser);
					}
				}
			}
		}
	}

	/**
	 * This method used to check whether the user is redundant and existed. If
	 * the user is existed, grab the data from db instead of create new user.
	 * 
	 **/
	public void conformUsers() {

		List<PmUser> selectedUserList = getManagedBean()
				.getSelectedSupplierUsers();
		List<SmPersonnel> addedPersonnelList = new ArrayList<SmPersonnel>();
		boolean redaundant;
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		for (PmUser selectedUser : selectedUserList) {
			redaundant = false;
			for (SmPersonnel smPersonnel : this.getManagedBean()
					.getSmPersonnels()) {
				if (smPersonnel.getPmUser().getUserId()
						.equals(selectedUser.getUserId())) {
					redaundant = true;
					break;
				}
			}
			if (!redaundant) {
				boolean found = false;
				reqDTO.setOrgProfileId(getManagedBean().getOrgProfile()
						.getOrgProfileId());
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveSupplierByProfileId(reqDTO);

				PmOrgProfile orgProfileSm = resDTO.getPmOrgProfile();

				SmSupplier supplier = orgProfileSm.getSmSupplier();
				if (supplier != null && supplier.getSmAppl() != null) {
					for (SmPersonnel personnel : supplier.getSmAppl()
							.getSmPersonnels()) {
						if (personnel.getPmUser().getUserId()
								.equals(selectedUser.getUserId())) {
							personnel.setBrandNew(true);
							personnel
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							addedPersonnelList.add(personnel);
							found = true;
						}
					}
				}
				if (!found) {
					SmPersonnel newSmPersonnel = new SmPersonnel();
					newSmPersonnel.setPmUser(selectedUser);
					
					if(supplier != null && supplier.getSmAppl() != null){
						newSmPersonnel.setSmAppl(getManagedBean().getOrgProfile().getSmSupplier().getSmAppl());
					}
					
					addedPersonnelList.add(newSmPersonnel);
				}
			}
		}
		this.getManagedBean().getSmPersonnels().addAll(addedPersonnelList);
		this.getManagedBean().getSupplierUsers().addAll(selectedUserList);

	}

	/**
	 * get all country list
	 * 
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		log.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}

	/**
	 * get state list by country id
	 * 
	 * @param countryId
	 * @return List<PmState>
	 */
	public List<PmState> getStateList(Long countryId) {
		log.info("getStateList");
		List<PmState> resultList=new ArrayList<PmState>();
		
		for(PmState pmState:PmCommonUtil.retrievePmStateList(countryId))
		{ 
			if(!pmState.getStateName().equalsIgnoreCase(NOT_RECOMMENDED_STATE))
			{
				resultList.add(pmState);
			}
		}

		return resultList;
	}

	/**
	 * get division list by state id (only applicable for sarawak)
	 * 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public List<PmDivision> getDivisionList(Long stateId) {
		log.info("getDivisionList");
		return PmCommonUtil.retrievePmDivisionList(stateId);
	}

	/**
	 * get district list by state id / division id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId,PmDivision pmDivision) {
		log.info("getDistrictList");
		if(pmDivision != null 
				&& pmDivision.getDivisionId() !=null){
			return PmCommonUtil.retrievePmDistrictListByDivisionId(pmDivision.getDivisionId());					
		}else {
			return PmCommonUtil.retrievePmDistrictList(stateId);
		}
		
	}

	/**
	 * get city list by district id
	 * 
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		log.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}

	/**
	 * retrieve state object
	 * 
	 * @param pmState
	 */
	public void retrievePmState() {
		log.info("retrievePmState");
		getManagedBean().getOfficeAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());
		getManagedBean().getOfficeAddress().setPmDivision(null); 
		if(getManagedBean().getSelectedStateId() != null ){
				log.info("pmState.getStateId() " + getManagedBean().getSelectedStateId());
				PmState pmState = PmCommonUtil.retrievePmStateByStateId(getManagedBean()
						.getSelectedStateId());				
				getManagedBean().getOfficeAddress().setPmState(pmState);		
				if(pmState.getAdoptDivision()){
					getManagedBean().getOfficeAddress().setPmDivision(new PmDivision()); 
				}	
			} 
		
	}
		
	public void retrieveDeliveryPmState(PmState pmState) {
		log.info("retrievePmState");
		if(pmState != null ){
			if (pmState.getStateId() != null) {
				log.info("pmState.getStateId() " + pmState.getStateId());
				pmState = PmCommonUtil.retrievePmStateByStateId(pmState
						.getStateId());				
				getManagedBean().getDeliveryAddress().getPmAddress().setPmState(pmState);
				getManagedBean().getDeliveryAddress().getPmAddress().setPmDistrict(new PmDistrict());
				getManagedBean().getDeliveryAddress().getPmAddress().setPmCity(new PmCity());
				if(pmState.getAdoptDivision()){
					getManagedBean().getDeliveryAddress().getPmAddress().setPmDivision(new PmDivision()); 
				}	
			} else {
				pmState.setStateCode(null);
			}
		}
	}

	/**
	 * This method used for construct condition fields for LOV (Lov Id :
	 * PM_ORG_PROFILE)
	 **/
	public String constuctConditionFields() {
		StringBuilder sb = new StringBuilder();
		PmOrgProfile ministryProfile = PmCommonUtil.retrieveMinistryByOrgProfileId(getManagedBean().getOrgProfile().getOrgProfileId());
		sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
		sb.append("^");
		sb.append(ministryProfile
				.getOrgProfileId());
		sb.append("|recordStatus^1");
		sb.append("|groupOrgType^");
		sb.append(ParameterConst.ORG_AUTH_TYPE_ORG_GROUP);
		sb.append("^2*groupOrgType^null^11");
		sb.append("|orgProfileId^");
		sb.append(getManagedBean().getOrgProfile().getOrgProfileId());
		sb.append("^2");

		StringBuilder childIds = new StringBuilder();
		for (int i = 0; i < getManagedBean().getProfileGroup().size(); i++) {
			PmProfileGroup profileGroup = getManagedBean().getProfileGroup()
					.get(i);
			if (profileGroup.getAuthorizedOrgProfile() != null) {
				if (i != 0) {
					childIds.append(",");
				}
				childIds.append(profileGroup.getAuthorizedOrgProfile()
						.getOrgProfileId());
			}
		}

		if (childIds.length() > 0) {
			sb.append("|orgProfileId^");
			sb.append(childIds.toString());
			sb.append("^10");
		}

		return sb.toString();
	}

	/**
	 * This method used for check whether the org profile is added in group org
	 * table.
	 * 
	 * @return isGroupOrg
	 **/
	public boolean isGroupOrg() {
		IOrgProfileSO orgProfileSO = null;
		boolean isGroupOrg = false;
		try {
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(getManagedBean().getOrgProfile()
					.getOrgProfileId());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveProfileGroupByAuthorizedProfileId(orgProfileReqDTO);
			if (CollectionUtils.isNotEmpty(orgProfileResDTO.getProfileGroups())) {
				isGroupOrg = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			orgProfileSO = null;
		}
		return isGroupOrg;
	}

	/**
	 * This method used for reset the profile group in Jabatan Tab if any
	 * changes in parent org profile
	 **/
	public void resetProfileGroup() {
		getManagedBean().setProfileGroup(new ArrayList<PmProfileGroup>());
	}

	private void validateSupplierBankListAndSupplierPersonnel() {
		List<SmSupplierBank> tempSupplierBankList = new ArrayList<SmSupplierBank>();
		if (this.getManagedBean().getSmSupplierBanks() == null) {
			throw new EpException(MessageConst.ERROR_775101);
		} else {
			// filter list for active records and check again
			for (SmSupplierBank supplierBank : this.getManagedBean()
					.getSmSupplierBanks()) {
				log.info("..............supplierBank.getRecordStatusFlg="
						+ supplierBank.getRecordStatusFlg());
				log.info("..............supplierBank.isRecordStatus="
						+ supplierBank.isRecordStatus());
				if (supplierBank.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
					tempSupplierBankList.add(supplierBank);
				}
			}
			log.info(".................tempSupplierBankList.size="
					+ tempSupplierBankList.size());
			if (tempSupplierBankList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_775101);
			}

		}
		if (this.getManagedBean().getSmPersonnels() == null) {
			throw new EpException(MessageConst.ERROR_775102);
		} else {
			// filter list for active records and check again
			List<SmPersonnel> tempPersonnelList = new ArrayList<SmPersonnel>();
			for (SmPersonnel personnel : this.getManagedBean()
					.getSmPersonnels()) {
				if (personnel.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
					tempPersonnelList.add(personnel);
				}
			}
			if (tempPersonnelList.isEmpty()) {
				throw new EpException(MessageConst.ERROR_775102);
			}
		}

		boolean isUnique = true;
		if (getManagedBean().getSmSupplierBanks() != null
				&& !getManagedBean().getSmSupplierBanks().isEmpty()) {
			tempSupplierBankList = new ArrayList<SmSupplierBank>(
					getManagedBean().getSmSupplierBanks());
			Set<String> tempSupplierBankSet = new HashSet<String>();
			StringBuilder sb = null;
			for (SmSupplierBank suppBank : getManagedBean()
					.getSmSupplierBanks()) {
				sb = new StringBuilder();
				sb.append(suppBank.getPmFinancialOrg().getFinancialOrgId());
				sb.append(suppBank.getAccountNo());
				tempSupplierBankSet.add(sb.toString());
			}
			if (tempSupplierBankList.size() != tempSupplierBankSet.size()) {
				isUnique = false;
			}
		}

		if (!isUnique) {
			throw new EpException(MessageConst.ERROR_775115);
		}
	}
	
	private void validateGstDetails(){
		SmGstSupplier gstSupplier = getManagedBean().getGstSupplier();
		if(StringUtils.isNotBlank(gstSupplier.getGstRegNo())){
			String pattern = "\\d{12}";
		    if(!gstSupplier.getGstRegNo().matches(pattern)){
		    	throw new EpException(MessageConst.ERROR_775120);		    
		    }
			
			//validate gst eff date
			if(gstSupplier.getGstEffDate() == null){
				throw new EpException(MessageConst.ERROR_775118);
			}
				
			//validate end date
			if(gstSupplier.getGstEndDate() != null && gstSupplier.getGstEndDate().before(gstSupplier.getGstEffDate())){
				throw new EpException(MessageConst.ERROR_775119);
			}		
			
			//validate unique gst registration number
			if(!validateGstRegNo(gstSupplier)){
				throw new EpException(MessageConst.ERROR_775117);
			}
		}else{
			gstSupplier.setGstEffDate(null);
			gstSupplier.setGstEndDate(null);
		}
	}
	
	private void validateGroupOrg(){
		boolean isValid = false;
		if (this.getManagedBean().getProfileGroup() == null
				|| this.getManagedBean().getProfileGroup().isEmpty()) {
			isValid = false;
		}
		
		if (this.getManagedBean().getProfileGroup() != null
				&& !this.getManagedBean().getProfileGroup().isEmpty()) {
			// validate empty profile group
			for(PmProfileGroup profileGroup : getManagedBean().getProfileGroup()){
				if(profileGroup.getAuthorizedOrgProfile() == null){
					throw new EpException(MessageConst.ERROR_775130);
				}
			}
			
			// at least one active profile group
			for(PmProfileGroup profileGroup : getManagedBean().getProfileGroup()){
				if(profileGroup.getAuthorizedOrgProfile() != null && 
						DbValueConst.RECORD_STATUS_ACTIVE == profileGroup.getRecordStatusFlg()){
					isValid = true;
					break;
				}
			}
		}
		
		if(!isValid){
			throw new EpException(MessageConst.ERROR_775103);
		}
	}

	/**
     * This method used to retrieve login user information.
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public String retrieveMinistryName(){
		return retrieveMinistryByOrgProfileId(getManagedBean().getOrgProfile().getOrgProfileId());
	}
	
	public String retrievePegawaiPengawalName(){
		return retrievePegawaiPengawalByOrgProfileId(getManagedBean().getOrgProfile().getOrgProfileId());
	}
	
	public String retrieveJabatanName(){
		return retrieveJabatanByOrgProfileId(getManagedBean().getOrgProfile().getOrgProfileId());
	}
	
	
	/**
	 * @return the federalGovt
	 */
	public int getFederalGovt() {
		return FEDERAL_GOVT;
	}

	/**
	 * @return the others
	 */
	public int getOthers() {
		return OTHERS;
	}
	
	public boolean isEPAdmin(){
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value());
	}

	private boolean validateGstRegNo(SmGstSupplier gstSupplier) {
		ISupplierGstSO supplierGstSO = null;
		boolean valid = true;
		try{
			Long supplierId = getManagedBean().getOrgProfile().getOrgProfileId();
			supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);			
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setGstRegNo(gstSupplier.getGstRegNo());
			reqDTO.setSupplierId(supplierId);
			SupplierGstResDTO suppGstResDTO = supplierGstSO.retrieveGstInfoByGstRegNo(reqDTO);
			if(suppGstResDTO.getGstList() != null && !suppGstResDTO.getGstList().isEmpty()){
				for(SmGstSupplier gst : suppGstResDTO.getGstList()){
					// validate against PTJ supplier
					if(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2.equals(gst.getSmSupplier().getBusinessType())){
						PmOrgProfile ministry1 = retrieveMinistryOrgProfile(getManagedBean().getOrgProfile().getOrgProfileId());
						PmOrgProfile ministry2 = retrieveMinistryOrgProfile(gst.getSmSupplier().getSupplierId());
						if(ministry1 != null && ministry2 !=null ){
							if(!ministry1.getOrgProfileId().equals(ministry2.getOrgProfileId())){
								valid = false;
								break;
							}							
						}						
					}else{
						// validate unique gst number against supplier other than PTJ (Govt seller)
						valid = false;
						break;
					}
				}
			}
		}finally{
			supplierGstSO = null;
		}
		return valid;
	}
	
	public void retrievePmDistrict(PmDistrict pmDistrict) {	
		if(pmDistrict != null && pmDistrict.getDistrictId() != null){
			PmDistrict tempDistrict = PmCommonUtil.retrievePmDistById(pmDistrict.getDistrictId());				
			getManagedBean().getOfficeAddress().setPmDistrict(tempDistrict);			
		}
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());		
	}
	
	public void retrievePmDivision(PmDivision pmDivision) {
		if(pmDivision != null && pmDivision.getDivisionId() != null){
			PmDivision tempDivision = PmCommonUtil.retrievePmDivisionById(pmDivision.getDivisionId());			
			getManagedBean().getOfficeAddress().setPmDivision(tempDivision);
		}
		getManagedBean().getOfficeAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getOfficeAddress().setPmCity(new PmCity());			
	}
	
	public void retrievePmCity(PmCity pmCity) {
		if(pmCity != null && pmCity.getCityId() !=null){
			PmCity tempCity = PmCommonUtil.retrievePmCityById(pmCity.getCityId());			
			getManagedBean().getOfficeAddress().setPmCity(tempCity);
		}
	}	
	
	public void retrieveDeliveryPmDistrict(PmDistrict pmDistrict) {
		if(pmDistrict != null && pmDistrict.getDistrictId() != null){
			PmDistrict tempDistrict = PmCommonUtil.retrievePmDistById(pmDistrict.getDistrictId());				
			getManagedBean().getDeliveryAddress().getPmAddress().setPmDistrict(tempDistrict);
		}		
		getManagedBean().getDeliveryAddress().getPmAddress().setPmCity(new PmCity());
		
	}
	
	public void retrieveDeliveryPmDivision(PmDivision pmDivision) {
		if(pmDivision != null && pmDivision.getDivisionId() != null){
			PmDivision tempDivision = PmCommonUtil.retrievePmDivisionById(pmDivision.getDivisionId());			
			getManagedBean().getDeliveryAddress().getPmAddress().setPmDivision(tempDivision);
		}
		getManagedBean().getDeliveryAddress().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getDeliveryAddress().getPmAddress().setPmCity(new PmCity());			
	}
	
	public void retrieveDeliveryPmCity(PmCity pmCity) {
		if(pmCity != null && pmCity.getCityId() !=null){
			PmCity tempCity = PmCommonUtil.retrievePmCityById(pmCity.getCityId());			
			getManagedBean().getDeliveryAddress().getPmAddress().setPmCity(tempCity);
		}
	}
	
	private void validateUniqueOrgCode(PmOrgProfile orgProfile, String orgType){
		log.info("validateUniqueOrgCode");
		IOrgProfileSO orgSO  = null;
		try{			
			ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgType));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfile.getOrgCode()));		
			
			if(!orgProfile.isBrandNew()){
				searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgProfileId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							orgProfile.getOrgProfileId()));	
			}
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			
						
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			orgSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgSO.searchOrgProfile(reqDTO);
			if(resDTO.getOrgProfileResultBean()!=null && resDTO.getOrgProfileResultBean().getResultList()!=null 
					&& !resDTO.getOrgProfileResultBean().getResultList().isEmpty()){
				if(orgProfile.isBrandNew()){
					throw new EpException(MessageConst.ERROR_775131, new
							 String[]{orgProfile.getOrgCode()});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.org.code")
					});	
				}
			}
		}finally{
			orgSO = null;
		}
	}
	
	private void validateUniqueOrgName(PmOrgProfile orgProfile, String orgType){
		log.info("validateUniqueOrgName");
		IOrgProfileSO orgSO  = null;
		try{			
			ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();			
			List<String> searchOrgTypes = new ArrayList<String>();
			if(OrganizationType.MINISTRY.value().equals(orgType)){
				searchOrgTypes.add(OrganizationType.MINISTRY.value());//added ministry
			}else{
				searchOrgTypes.add(OrganizationType.STATE_GOVERNMENT.value());
				searchOrgTypes.add(OrganizationType.LOCAL_AUTHORITY.value());
				searchOrgTypes.add(OrganizationType.GOVERNMENT_LINK_COMPANIES.value());
				searchOrgTypes.add(OrganizationType.STATUTORY_BODIES.value());
				searchOrgTypes.add(OrganizationType.STATUTORY_BODIES_STATE.value());
			}	
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							searchOrgTypes));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmOrgValidity.orgName",
							SearchOperator.OPERATOR_EQUALS.value(),
							orgProfile.getOrgName(), true));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							DbValueConst.RECORD_STATUS_ACTIVE));
			
			if(!orgProfile.isBrandNew()){
				searchBean.getDataFilterBeans().add(
					new SearchFilterBean("orgProfileId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							orgProfile.getOrgProfileId()));	
			}
			
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			orgSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgSO.searchOrgProfile(reqDTO);
			if(resDTO.getOrgProfileResultBean()!=null && resDTO.getOrgProfileResultBean().getResultList()!=null 
					&& !resDTO.getOrgProfileResultBean().getResultList().isEmpty()){
				String orgTypeDesc = getParameterDesc(
						ParameterTypeConst.ORGANISATION_TYPES,
						resDTO.getOrgProfileResultBean().getResultList().get(0).getOrgTypeCode().getParameterCode());
				if(orgProfile.isBrandNew()){
					throw new EpException(MessageConst.ERROR_775132, new
							 String[]{orgProfile.getOrgName(),orgTypeDesc,orgTypeDesc});
				}else{
					throw new EpException(MessageConst.ERROR_770220, new String[] {
							PmCommonUtil.getResourceBundleLabels("lbl.pm.org.name")
					});	
				}
			}
		}finally{
			orgSO = null;
		}
	}
	
	public boolean isGmapDisabled() {
		gmapDisabled = false;
		
		if(getManagedBean().getOfficeAddress() != null){
			PmAddress officeAddress = getManagedBean().getOfficeAddress();

			if(officeAddress.getPmCountry() == null || officeAddress.getPmDistrict() == null || 
					officeAddress.getPmState() == null || officeAddress.getPmCity() == null || 
							officeAddress.getAddress1() == null ){
					gmapDisabled = true;
			}else{
				if(StringUtils.isBlank(officeAddress.getAddress1()) || officeAddress.getPmCountry().getCountryId() == null ||
						officeAddress.getPmDistrict().getDistrictId() == null || officeAddress.getPmState().getStateId() == null ||
								officeAddress.getPmCity().getCityId() == null){
					gmapDisabled = true;
				}

			}
		}else{
			gmapDisabled = true;
		}

		return gmapDisabled;
	}
	
	public SelectItem[] getAuthTypeList() {
		SelectItem[] items = null;
		FacesScopes face = new FacesScopes();
		try {
			boolean isAuthorizedProfile = false;
			ISearchBean<PmParameterDesc> searchBean = null;		
			//check if it's authorized org profile
			if(getManagedBean().getOrgProfile().getAuthorizedProfileGroups() != null
					&& !getManagedBean().getOrgProfile().getAuthorizedProfileGroups().isEmpty()){
				for(PmProfileGroup group : getManagedBean().getOrgProfile().getAuthorizedProfileGroups()){
					if(DbValueConst.RECORD_STATUS_ACTIVE == group.getRecordStatusFlg()){
						isAuthorizedProfile = true;
						break;
					}
				}
			}
			
			if(isAuthorizedProfile){
				// filter out the org group type in drop down
				searchBean = new SearchBean<PmParameterDesc>();
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmParameter.parameterCode",
								SearchOperator.OPERATOR_NOT_EQUALS.value(),
								ParameterConst.ORG_AUTH_TYPE_ORG_GROUP));
				
			}
		
			List<PmParameterDesc> parameterDescs = ParameterHelper
					.getInstance().getParameterDescListBySearchBean(
							ParameterTypeConst.ORG_AUTH_TYPE, searchBean);

			items = new SelectItem[parameterDescs.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					face.getUserLocale()));
			int i = 1;
			for (PmParameterDesc parameterDesc : parameterDescs) {
				items[i++] = new SelectItem(parameterDesc.getPmParameter()
						.getParameterCode(), parameterDesc.getCodeName());
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
	
	public void bpkChangeListener(){
		if(getManagedBean().getOrgProfile().getBpk() !=null && 
				getManagedBean().getOrgProfile().getBpk() == false){
			getManagedBean().setSelectedZoneId(null);
		}
		
	}
	
	public void govtSellerChange(){
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgProfileId(this.getManagedBean().getOrgProfile().getOrgProfileId());
		reqDTO.setFetchGovtSeller(true);
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);

		OrgProfileResDTO orgResDTO = orgProfileSO
				.retrieveOrgDetailsByOrgId(reqDTO);
		PmOrgProfile orgProfile = orgResDTO.getPmOrgProfile();
		if (orgProfile.getSmSupplier() != null && getManagedBean().getOrgProfile().isGovtSeller()) {
			log.info("Supplier Test");
			/*
			 * this.getManagedBean().setSmSupplierBanks(
			 * orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks());
			 */
			if (orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks() != null
					&& !orgProfile.getSmSupplier().getSmAppl()
							.getSmSupplierBanks().isEmpty()) {
				this.getManagedBean().setSmSupplierBanks(
						new ArrayList<SmSupplierBank>());				
				for (SmSupplierBank supplierBank : orgProfile
						.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
					if (supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							|| supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						this.getManagedBean().getSmSupplierBanks()
								.add(supplierBank);

					}
				}
			}

			log.info("Supplier Bank-----> "
					+ orgProfile.getSmSupplier().getSmAppl()
							.getSmSupplierBanks().size());
			if (orgProfile.getSmSupplier().getSmAppl().getSmPersonnels() != null
					&& !orgProfile.getSmSupplier().getSmAppl()
							.getSmPersonnels().isEmpty()) {
				this.getManagedBean().setSmPersonnels(new ArrayList<SmPersonnel>());
				for (SmPersonnel personnel : orgProfile.getSmSupplier()
						.getSmAppl().getSmPersonnels()) {
					if (personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
							|| personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
						this.getManagedBean().getSmPersonnels()
								.add(personnel);

					}
				}
			}
			// this.getManagedBean().setSmPersonnels(orgProfile.getSmSupplier().getSmAppl().getSmPersonnels());
			log.info("Supplier Users ---->"
					+ orgProfile.getSmSupplier().getSmAppl()
							.getSmPersonnels().size());
			
//			if(orgProfile.getSmSupplier().getSmGstSuppliers() != null 
//					&& !orgProfile.getSmSupplier().getSmGstSuppliers().isEmpty()){
//				for(SmGstSupplier gstSupplier : orgProfile.getSmSupplier().getSmGstSuppliers()){
//					if(gstSupplier.getIsCurrent()){
//						getManagedBean().setGstSupplier(gstSupplier);
//						break;
//					}
//				}
//			}
			ISupplierGstSO supplierGstSO = null;
			try{
				supplierGstSO = (ISupplierGstSO) BusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierGstSO.class);
				SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
				gstReqDTO.setSupplierId(orgProfile.getSmSupplier().getSupplierId());
				SupplierGstResDTO gstResDTO = supplierGstSO.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
				if(gstResDTO.isSuccessful() && gstResDTO.getSmGstSupplier() != null){
					getManagedBean().setGstSupplier(gstResDTO.getSmGstSupplier());
				}
				
			}finally{ 
				supplierGstSO = null;
			}
		}
	}
	
	public void autoSelectOrgType(){
		//RequestContext.getCurrentInstance().execute("if($(\"input[id$=':form:sorSearchOrgTypeId:0']\").is(':disabled')){console.log(\"0\"); $(\"input[id$=':form:sorSearchOrgTypeId:1']\").prop('checked', true);$(\"input[id$=':form:sorSearchOrgTypeId:1']\").change();}else if($(\"input[id$=':form:sorSearchOrgTypeId:1']\").is(':disabled')){ console.log(\"1\"); $(\"input[id$=':form:sorSearchOrgTypeId:0']\").prop('checked', true);$(\"input[id$=':form:sorSearchOrgTypeId:0']\").change();}else{ console.log(\"2\");$(\"input[id$=':form:sorSearchOrgTypeId:0']\").prop('checked', true);	$(\"input[id$=':form:sorSearchOrgTypeId:0']\").change();");
		if(!getManagedBean().isNotFirstRun()){
			log.info("updateAuto");
			RequestContext.getCurrentInstance().execute("autoSelectOrgType();");
			getManagedBean().setNotFirstRun(true);
		}
		
	}
}
