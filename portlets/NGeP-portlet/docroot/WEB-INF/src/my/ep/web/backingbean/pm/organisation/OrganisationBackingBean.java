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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.pm.fee.settings.IFeeExemptionSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
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
import my.ep.dto.pm.fee.settings.FeeExemptionReqDTO;
import my.ep.dto.pm.fee.settings.FeeExemptionResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
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
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.OrganisationSearchBean;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
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
		// getManagedBean().setOfficeContactPerson(new PmContact());

		getManagedBean().setOrgType(null);
		getManagedBean().setPmFinancialAcc(new ArrayList<PmFinancialAcc>());
		getManagedBean().setProfileGroup(new ArrayList<PmProfileGroup>());
		getManagedBean().setOrgProfile(orgProifle);
		// set org category to others
		getManagedBean().setOrgCategory(OTHERS);
		// getManagedBean().setAddressTypesList(null);
	}

	private void resetInitialOrg() {

		this.getManagedBean().setFinancialOrg(null);
		this.getManagedBean().setProfileGroup(null);
		this.getManagedBean().setSupplierUserList(null);
		this.getManagedBean().setSupplierUsers(null);
		this.getManagedBean().setSelectedSupplierUsers(null);
		this.getManagedBean().setOfficeAddress(null);
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
			
			PmUser user = PmCommonUtil.retrieveUserById(getFacesScopes().getUserSessionBean().getUserId(), false);
			PmOrgProfile orgProfile = null;
			if(user != null){
				orgProfile = user.getPmUserOrgs().get(0).getPmOrgProfile();
			}
			
			PmParameter orgTypeParameter = 
					ParameterHelper.getInstance().getPmParameterById(Long.valueOf(oSearchBean.getOrgTypeCode()));
			Set<Long> orgProfileSet = null;
			if(orgTypeParameter != null && orgProfile != null
					&& ParameterTypeConst.ORGANISATION_TYPES.equals(orgTypeParameter.getParameterType())){
			   if(OrganizationType.MINISTRY.value().equals(orgTypeParameter.getParameterCode())){
				   if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
					   searchBean.getDataFilterBeans().add(
								new SearchFilterBean("orgProfileId",
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
						// Naik Taraf / Group Org.
						if(orgProfile.isGroupOrg()){
							orgProfileSet = new HashSet<Long>(); 							
							//add current org profile
							orgProfileSet.add(orgProfile.getOrgProfileId());
							//loop and add authorized profile (child list)
							for(PmProfileGroup profileGroup : orgProfile.getMainProfileGroups()){
								orgProfileSet.add(profileGroup.getAuthorizedOrgProfile().getOrgProfileId());
							}	
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("orgProfileId",
												SearchOperator.OPERATOR_IN.value(),new ArrayList<Long>(orgProfileSet)));
						}else{
							searchBean.getDataFilterBeans().add(
											new SearchFilterBean("orgProfileId",
													SearchOperator.OPERATOR_EQUALS.value(),orgProfile.getOrgProfileId()));
						}
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
			if (getManagedBean().getOrgProfile().getPmAddressTypes() != null
					&& !getManagedBean().getOrgProfile().getPmAddressTypes()
							.isEmpty()) {
				for (PmAddressType pmAddressType : getManagedBean()
						.getOrgProfile().getPmAddressTypes()) {

					if (pmAddressType.getAddressType().equalsIgnoreCase(
							AddressType.DELIVERY_ADDRESS.value()) && 
							(DbValueConst.RECORD_STATUS_ACTIVE == pmAddressType.getRecordStatusFlg() ||
								DbValueConst.RECORD_STATUS_SUSPENDED == pmAddressType.getRecordStatusFlg())) {

						this.getManagedBean().getAddressTypesList()
								.add(pmAddressType);
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

			if (orgProfile.getSmSupplier() != null) {
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
					for (SmPersonnel supplierBank : orgProfile.getSmSupplier()
							.getSmAppl().getSmPersonnels()) {
						if (supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								|| supplierBank.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
							this.getManagedBean().getSmPersonnels()
									.add(supplierBank);

						}
					}
				}
				// this.getManagedBean().setSmPersonnels(orgProfile.getSmSupplier().getSmAppl().getSmPersonnels());
				log.info("Supplier Users ---->"
						+ orgProfile.getSmSupplier().getSmAppl()
								.getSmPersonnels().size());
			}

		}
		// Org Address
		List<PmAddressType> orgAddressTypes = getManagedBean().getOrgProfile()
				.getPmAddressTypes();

		boolean addrExist = false;
		boolean contactExist = false;
		if (orgAddressTypes != null && !orgAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : orgAddressTypes) {

				if (pmAddressType.getAddressType().equalsIgnoreCase(
						AddressType.BILLING_ADDRESS.value())
						&& (pmAddressType.getRecordStatusFlg()
								.equals(DbValueConst.RECORD_STATUS_ACTIVE))) {
					//cater for sarawak if no division
					if(pmAddressType.getPmAddress().getPmDivision() == null){
						pmAddressType.getPmAddress().setPmDivision(new PmDivision()); 
					}
					this.getManagedBean().setOfficeAddress(
							pmAddressType.getPmAddress());
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

		IFinancialOrgSO financialOrgSO = null;
		IOrgProfileSO orgProfileSO = null;
		IParameterSO parameterSO = null;
		IRegistrationSO registrationSO = null;
		String redirectUrl = null;

		try {
			// check govSeller
			if (this.getManagedBean().getOrgProfile().isGovtSeller()) {
				validateSupplierBankListAndSupplierPersonnel();
			}
			// check group org
			if (this.getManagedBean().getOrgProfile().isGroupOrg()) {
				if (this.getManagedBean().getProfileGroup() == null
						|| this.getManagedBean().getProfileGroup().isEmpty()) {
					throw new EpException(MessageConst.ERROR_775103);
				}
			}

			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IFinancialOrgSO.class);

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

			PmOrgProfile orgProfile = this.getManagedBean().getOrgProfile();
			PmOrgValidity orgValidity = getManagedBean().getOrgProfile()
					.getPmOrgValidity();
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

			validateOrgProfiel(orgProfile);
			validateOrgAddress(this.getManagedBean().getOfficeAddress());
			validateContactPerson(this.getManagedBean()
					.getOfficeContactPerson());

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
			// PTJ, GLC, LAT, STG and STB
			if (getManagedBean().getOrgType().equalsIgnoreCase(
					OrganizationType.DIVISION.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATE_GOVERNMENT.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.STATUTORY_BODIES.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.LOCAL_AUTHORITY.value())
					|| getManagedBean().getOrgType().equalsIgnoreCase(
							OrganizationType.GOVERNMENT_LINK_COMPANIES.value())) {
				registrationSO = (IRegistrationSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT,
								IRegistrationSO.class);

				if (getManagedBean().getOrgProfile().isGovtSeller()) {

					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_MANAGEMENT,
									ISupplierSearchSO.class);
					SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
					suppSearchReqDTO
							.setSupplierId(orgProfile.getOrgProfileId());
					SupplierSearchResDTO suppSearchResDTO = supplierSearchSO
							.retriveSupplierById(suppSearchReqDTO);

					// new Supplier
					if (orgProfile.getSmSupplier() == null
							&& suppSearchResDTO.getSmSupplier() == null) {
						log.info("Create new Supplier");
						RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
						regReqDTO.setPmUsers(getManagedBean()
								.getSupplierUsers());
						regReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
						if (!getManagedBean().getSmSupplierBanks().isEmpty()) {
							for (SmSupplierBank supplierBank : getManagedBean()
									.getSmSupplierBanks()) {
								FinancialOrgReqDTO finReqDTO = new FinancialOrgReqDTO();
								finReqDTO.setFinancialOrgId(supplierBank
										.getPmFinancialOrg()
										.getFinancialOrgId());
								FinancialOrgResDTO finResDTO = financialOrgSO
										.retrieveFinancialOrgById(finReqDTO);
								PmFinancialOrg pmFinancialOrg = finResDTO
										.getPmFinancialOrg();
								regReqDTO.setBankAccountNo(supplierBank
										.getAccountNo());
								regReqDTO.setBank(pmFinancialOrg);
							}
						}
						RegistrationResDTO regResDTO = registrationSO
								.createPTJSupplier(regReqDTO);
						regResDTO.setSuccessful(true);

						// Fee Exemption
						IFeeExemptionSO exemptionSO = (IFeeExemptionSO) BusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IFeeExemptionSO.class);
						FeeExemptionReqDTO feeReqDTO = new FeeExemptionReqDTO();
						PmFeeExemption feeEx = new PmFeeExemption();
						feeEx.setOrgProfileId(orgProfile);
						feeReqDTO.setFeeExemption(feeEx);
						FeeExemptionResDTO feeResDTO = exemptionSO
								.insertFeeExemption(feeReqDTO);
						feeResDTO.setSuccessful(true);
					}

					if (orgProfile.getSmSupplier() != null) {
						log.info("Update Supplier");
						reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
						OrgProfileResDTO resDTO = orgProfileSO
								.retrieveSupplierByProfileId(reqDTO);
						PmOrgProfile orgProfileSm = resDTO.getPmOrgProfile();
						SmSupplier supplier = orgProfileSm.getSmSupplier();
						if (supplier != null) {
							RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
							List<SmSupplierBank> smSupplierBanks = getManagedBean()
									.getSmSupplierBanks();
							if (!smSupplierBanks.isEmpty()) {
								for (int i = 0; i < smSupplierBanks.size(); i++) {
									SmSupplierBank suppBank = smSupplierBanks
											.get(i);
									if (supplier.getSmAppl() != null
											&& supplier.getSmAppl()
													.getSmSupplierBanks() != null
											&& !supplier.getSmAppl()
													.getSmSupplierBanks()
													.isEmpty()) {
										for (SmSupplierBank tempSuppBank : supplier
												.getSmAppl()
												.getSmSupplierBanks()) {
											if (tempSuppBank.getAccountNo()
													.equals(suppBank
															.getAccountNo())
													&& (tempSuppBank
															.getPmFinancialOrg()
															.getFinancialOrgId()
															.equals(suppBank
																	.getPmFinancialOrg()
																	.getFinancialOrgId()))) {
												// supplier bank is existed.
												Integer suppBankRecordStatus = suppBank
														.getRecordStatusFlg();
												suppBank = tempSuppBank;
												suppBank.setRecordStatusFlg(suppBankRecordStatus);
												getManagedBean()
														.getSmSupplierBanks()
														.set(i, suppBank);
											}
										}
									}
								}

								for (SmSupplierBank supplierBank : getManagedBean()
										.getSmSupplierBanks()) {
									FinancialOrgReqDTO finReqDTO = new FinancialOrgReqDTO();
									finReqDTO.setFinancialOrgId(supplierBank
											.getPmFinancialOrg()
											.getFinancialOrgId());
									FinancialOrgResDTO finResDTO = financialOrgSO
											.retrieveFinancialOrgById(finReqDTO);
									PmFinancialOrg pmFinancialOrg = finResDTO
											.getPmFinancialOrg();
									if (supplierBank.isBrandNew()) {
										supplierBank.setAccountNo(supplierBank
												.getAccountNo());
										supplierBank
												.setPmFinancialOrg(pmFinancialOrg);
										supplierBank.setAccountPurpose("P");
										supplierBank.setSmAppl(supplier
												.getSmAppl());
										supplier.getSmAppl()
												.getSmSupplierBanks()
												.add(supplierBank);
									}
									if (supplierBank
											.getRecordStatusFlg()
											.equals(DbValueConst.RECORD_STATUS_ACTIVE)) {
										supplierBank
												.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
										log.info("Active Supplier Bank");
									} else {
										log.info("Inactive Supplier Bank");
										supplierBank
												.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
										supplier.getSmAppl()
												.setSmSupplierBanks(
														getManagedBean()
																.getSmSupplierBanks());
									}
									log.info("Supplier Bank Record Status------>"
											+ supplierBank.getRecordStatusFlg()
											+ "<-----");
								}

							}
							log.info("Sm Personel list Size"
									+ getManagedBean().getSmPersonnels().size());

							if (getManagedBean().getSmPersonnels() != null
									&& !getManagedBean().getSmPersonnels()
											.isEmpty()) {
								log.info("SM Persnels test");
								for (SmPersonnel personnel : getManagedBean()
										.getSmPersonnels()) {
									if (personnel.getRecordStatusFlg().equals(
											DbValueConst.RECORD_STATUS_ACTIVE)) {
										personnel
												.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
										log.info("Personal Record Status Active");
									} else {
										log.info("Personal Record Status inactie");
										personnel
												.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
									}

									supplier.getSmAppl().setSmPersonnels(
											getManagedBean().getSmPersonnels());
								}
							}
							regReqDTO.setSmSupp(supplier);
							RegistrationResDTO regResDTO = registrationSO
									.updatePTJSupplier(regReqDTO);
							regResDTO.setSuccessful(true);
						}
					}
				} else {
					log.info("Deactvate Supplier=======");
					if (orgProfile.getSmSupplier() != null) {

						reqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
						OrgProfileResDTO resDTO = orgProfileSO
								.retrieveSupplierByProfileId(reqDTO);

						PmOrgProfile orgProfileSm = resDTO.getPmOrgProfile();

						SmSupplier smSupplier = orgProfileSm.getSmSupplier();
						if (smSupplier != null) {
							RegistrationReqDTO regReqDTO = new RegistrationReqDTO();

							if (!getManagedBean().getSmSupplierBanks()
									.isEmpty()) {
								for (SmSupplierBank supplierBank : getManagedBean()
										.getSmSupplierBanks()) {
									supplierBank
											.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
									smSupplier.getSmAppl().setSmSupplierBanks(
											getManagedBean()
													.getSmSupplierBanks());
								}
							}
							if (getManagedBean().getSmPersonnels() != null
									&& !getManagedBean().getSmPersonnels()
											.isEmpty()) {
								for (SmPersonnel personnel : getManagedBean()
										.getSmPersonnels()) {
									personnel
											.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
									smSupplier.getSmAppl().setSmPersonnels(
											getManagedBean().getSmPersonnels());
								}
							}
							regReqDTO.setSmSupp(smSupplier);
							RegistrationResDTO regResDTO = registrationSO
									.updatePTJSupplier(regReqDTO);
							regResDTO.setSuccessful(true);
						}
						IFeeExemptionSO exemptionSO = (IFeeExemptionSO) BusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IFeeExemptionSO.class);
						FeeExemptionReqDTO feeReqDTO = new FeeExemptionReqDTO();

						feeReqDTO.setOrgProfileId(orgProfile.getOrgProfileId());
						log.info("Org ProifleId--"
								+ orgProfile.getOrgProfileId());
						FeeExemptionResDTO feeResDTO = exemptionSO
								.deleteFeeExemptionByOrgProfile(feeReqDTO);
						feeResDTO.setSuccessful(true);
					}

				}
			}
			// PTJ
			if (parameterResDTO.getPmParameter().getParameterCode()
					.equalsIgnoreCase(OrganizationType.DIVISION.value())) {
				// Billing Address
				for (PmAddressType addressType : getManagedBean()
						.getOrgProfile().getPmAddressTypes()) {
					if (addressType.getAddressType().equals(
							AddressType.BILLING_ADDRESS.value())
							&& (addressType.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_ACTIVE))
							&& !getManagedBean().getAddressTypesList()
									.contains(addressType)) {
						getManagedBean().getAddressTypesList().add(addressType);
					}
				}
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

				// Pm Address Types
				orgProfile.setPmAddressTypes(getManagedBean()
						.getAddressTypesList());
				// New OR Update Profile Groups
				if (getManagedBean().getOrgProfile().isGroupOrg()) {
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
			}

			reqDTO.setPmOrgProfile(orgProfile);
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
				redirectUrl = NAV_TO_SEARCH;
			} else if (BpmOutcomeConst.BPM_UPDATE.equalsIgnoreCase(outcome)) {
				message = ResourceBundleUtils
						.getMessage("lbl.pm.org.updated.successfully",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				redirectUrl = null;
				redirectToOrganisation(getManagedBean().getOrgProfile()
						.getOrgProfileId());
			}

			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Org Name : ", message));
			}
		} finally {
			financialOrgSO = null;
			orgProfileSO = null;
			parameterSO = null;
			registrationSO = null;
		}

		return redirectUrl;

	}

	private void validateOrgProfiel(PmOrgProfile orgProfile) {

		if (StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgCode())) {
			throw new EpException(MessageConst.ERROR_775105);
		}
		if (StringUtils.isBlank(orgProfile.getPmOrgValidity().getOrgName())) {
			throw new EpException(MessageConst.ERROR_775106);
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
			if (ParameterConst.SARAK_SK_CODE.equals(pmAddress.getPmState()
					.getStateCode())) {
				if (pmAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_770044);
				}
			} else {
				log.info("set division null");
				pmAddress.setPmDivision(null);
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
				throw new EpException(MessageConst.ERROR_775107);
			}

			if (StringUtils.isBlank(deliverAddress.getAddress1())) {
				throw new EpException(MessageConst.ERROR_775108);
			}

			if (StringUtils.isBlank(deliverAddress.getPostcode())) {
				throw new EpException(MessageConst.ERROR_775109);
			}

			if (deliverAddress.getPmState().getStateId() == null) {
				throw new EpException(MessageConst.ERROR_775110);
			}

			if (deliverAddress.getPmDistrict().getDistrictId() == null) {
				throw new EpException(MessageConst.ERROR_775111);
			}
			if (deliverAddress.getPmCity().getCityId() == null) {
				throw new EpException(MessageConst.ERROR_775112);
			}
			if (ParameterConst.SARAK_SK_CODE.equals(deliverAddress.getPmState()
					.getStateCode())) {
				if (deliverAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_770044);
				}
			} else {

				deliverAddress.setPmDivision(null);
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
		getManagedBean().setOfficeAddress(PmCommonUtil.getNewPmAddress());
		getManagedBean().setOfficeContactPerson(new PmContact());
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
		ZoneResDTO resDTO = zoneSO.retrieveZoneList();
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
		if (address.getPmAddress().isBrandNew()) {
			address.getPmAddress().setModified(true);
		}
		address.setEditable(true);
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
			int index = this.getManagedBean().getSmSupplierBanks().size();
			this.getManagedBean().getSmSupplierBanks()
					.add(index, newSupplierBank);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * The method used for delete supplier bank in government seller tab.
	 * 
	 */
	public void deleteSupplierBank(int index) {
		SmSupplierBank smSupplierBank = this.getManagedBean()
				.getSmSupplierBanks().get(index);
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
	public void updateOrgCode(PmOrgProfile pmOrgProfile, int index) {

		boolean isValid = true;
		if (pmOrgProfile != null && pmOrgProfile.getOrgProfileId() != null) {
			if (this.getManagedBean().getSelectedProfileGroups() != null
					&& !this.getManagedBean().getSelectedProfileGroups()
							.isEmpty()) {
				int i = 0;
				for (PmProfileGroup pmUserOrg : this.getManagedBean()
						.getSelectedProfileGroups()) {
					if (pmUserOrg.getMainOrgProfile().getOrgProfileId()
							.equals(pmOrgProfile.getOrgProfileId())
							&& i != index) {
						isValid = false;
						break;
					}
					i++;
				}
				if (!isValid) {
					this.getManagedBean().getSelectedProfileGroups().get(index)
							.getAuthorizedOrgProfile()
							.setPmOrgValidity(new PmOrgValidity());
					throw new EpException(MessageConst.ERROR_770019);
				}

			}
		}
	}

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
	public void deleteOrgUnit(PmProfileGroup pmProfileGroup) {

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
	public void deletePersonnel(int index) {

		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnels()
				.get(index);
		smPersonnel.setDeleted(true);
		// this.getManagedBean().getDeletedSmPersonnels()
		// .add(smPersonnel);// should added to original list before
		// // save
		this.getManagedBean().getSmPersonnels().remove(smPersonnel);
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
					addedPersonnelList.add(newSmPersonnel);
				}
			}
		}
		this.getManagedBean().getSmPersonnels().addAll(addedPersonnelList);
		this.getManagedBean().setSupplierUsers(selectedUserList);

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
	 * get district list by state id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		log.info("getDistrictList");
		return PmCommonUtil.retrievePmDistrictList(stateId);
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
	public void retrievePmState(PmState pmState) {
		log.info("retrievePmState");
		if (pmState != null && pmState.getStateId() != null) {
			log.info("pmState.getStateId() " + pmState.getStateId());
			PmState tempState = PmCommonUtil.retrievePmStateByStateId(pmState
					.getStateId());
			pmState.setStateCode(tempState.getStateCode());
		} else {
			pmState.setStateCode(null);
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
		sb.append("|groupOrg^0");
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
				if (supplierBank.getRecordStatusFlg() == 1) {
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
				if (personnel.getRecordStatusFlg() == 1) {
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

}
