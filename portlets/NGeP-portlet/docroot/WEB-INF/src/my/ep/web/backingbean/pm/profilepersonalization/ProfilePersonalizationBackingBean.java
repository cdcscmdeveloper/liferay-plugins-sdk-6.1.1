package my.ep.web.backingbean.pm.profilepersonalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.pm.ChallengeQAList;
import my.ep.web.managedbean.pm.profilepersonalization.ProfilePersonalizationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * @author DK, RAIN POH
 * 
 */
@ManagedBean(name = "profilePersonalizationBackingBean")
@RequestScoped
public class ProfilePersonalizationBackingBean extends
		BaseBackingBean<ProfilePersonalizationManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(ProfilePersonalizationBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_MYACCOUNT = "/xhtml/pm/profilePersonalization/userDetails/profileManagementMaintain.xhtml";
	// Mapper for DDL object
	private static Map<Long, Object> identityTypeMapper;

	// Tab values
	public static final int TAB_USER_PROFILE = 0;
	public static final int TAB_PERSONALIZATION = 1;
	public static final int TAB_CHANGE_PASSWORD = 2;
	public static final int TAB_CHANGE_CHALLENGE_QUESTION = 3;

	/**
	 * @return the tabUserProfile
	 */
	public int getTabUserProfile() {
		return TAB_USER_PROFILE;
	}

	/**
	 * @return the tabPersonalization
	 */
	public int getTabPersonalization() {
		return TAB_PERSONALIZATION;
	}

	/**
	 * @return the tabChangePassword
	 */
	public int getTabChangePassword() {
		return TAB_CHANGE_PASSWORD;
	}

	/**
	 * @return the tabChangeChallengeQuestion
	 */
	public int getTabChangeChallengeQuestion() {
		return TAB_CHANGE_CHALLENGE_QUESTION;
	}

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
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

	/**
	 * load clean view for my account
	 */
	public void preLoadingMyAccount() {
		logger.info("preLoadingMyAccount");
		FacesScopes facesScopes = new FacesScopes();
		PmUser sUsers = facesScopes.getUserSessionBean();
		if (sUsers == null) {
			throw new EpException(MessageConst.ERROR_770045);
		}
		this.retrieveIdentityTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		loadUserProfile(sUsers.getUserId());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_MYACCOUNT);
	}
	public void saluationChanged(ValueChangeEvent event){
		 System.out.println("...........saluationChanged............");
		this.getManagedBean().getSelectedUser().setModified(true);	
	}
	
	public void notifyChangeListener(ValueChangeEvent event){
		 System.out.println("...........notifyChangeListener............");
		this.getManagedBean().getSelectedUser().setModified(true);	
	}
	public void languageChangeListener(ValueChangeEvent event){
		 System.out.println("...........languageChangeListener............");
		this.getManagedBean().getSelectedUser().setModified(true);	
	}
	
	public void notifySmsChangeListener(){
		 System.out.println("...........notifySmsChangeListener............");
		 if(getManagedBean().getSelectedUser().isNotifyBySms() 
				 && StringUtils.isBlank(getManagedBean().getSelectedUser().getMobileNo())){
			 getManagedBean().getSelectedUser().setNotifyBySms(false); 
			 throw new EpException(MessageConst.ERROR_770090);
		 }
		 if(getManagedBean().getSelectedUser().isNotifyBySms() 
				 && getManagedBean().getSelectedUser().getMobileTelcoId() == null){
			 getManagedBean().getSelectedUser().setNotifyBySms(false); 
			 throw new EpException(MessageConst.ERROR_770250);
		 }
		this.getManagedBean().getSelectedUser().setModified(true);	
	}
	
	// /**
	// * initial new pmUser object
	// */
	// private void initialUserProfile(){
	// logger.info("initialUserProfile");
	// //load user attributes
	// PmCountry defaultCountry =
	// PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
	// PmUser pmUser = new PmUser();
	// pmUser.setSalutationCode(new PmSalutation());
	// pmUser.setPmCountry(defaultCountry);
	// pmUser.setIdentificationCode(new PmParameter());
	// pmUser.setSalutationCode(new PmSalutation());
	// pmUser.setPMP(false);
	// pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());
	// pmUser.setOrgTypeCode(new PmParameter());
	//
	// pmUser.setNotifyByEmail(true);
	//
	// //initial preference
	// PmCommonUtil.loadPmUserPreference(pmUser);
	//
	// //initial address
	// this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());
	//
	// //load password
	// pmUser.setPassword(null);
	// // pmUser.setOldPassword(null);
	// pmUser.setConfirmPassword(null);
	// this.getManagedBean().setDeletePortrait(false);
	// this.getManagedBean().setSelectedUser(pmUser);
	// System.out.println("login " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// }
	//

	/**
	 * load pm user object by id
	 * 
	 * @param id
	 */
	private void loadUserProfile(Long id) {
		logger.info("loadUserProfile");
		try {
			// initialUserProfile();
			if (id == null) {
				throw new EpException(MessageConst.ERROR_770015);
			}

			// load user
			PmUser pmUser = PmCommonUtil.retrieveUserWithoutOrgInfoById(id);
			pmUser.setPMP(pmUser.isPMP() ? true : false);
			pmUser.setIdentificationCode(pmUser.getIdentificationCode() == null ? new PmParameter()
					: pmUser.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode() == null ? new PmSalutation()
					: pmUser.getSalutationCode());
			pmUser.setOrgTypeCode(pmUser.getOrgTypeCode() == null ? new PmParameter()
					: pmUser.getOrgTypeCode());

			// load address
			// populateUserAddress(pmUser); 19Nov2014

			// initial preference
			PmCommonUtil.loadPmUserPreference(pmUser);

			// retrieve current active org
			List<PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(id);
			this.getManagedBean().setCurrentProfile(
					pmUserOrgs != null ? pmUserOrgs.get(0).getPmOrgProfile()
							: null);
			this.getManagedBean().setCurrentUserOrg(
					pmUserOrgs != null ? pmUserOrgs.get(0) : null);
			this.getManagedBean().setUserActiveRoles(new ArrayList<PmUserRole>());
			if (this.getManagedBean().getCurrentUserOrg() != null) {
				for (PmUserRole userRole : this.getManagedBean()
						.getCurrentUserOrg().getPmUserRoles()) {
					if(userRole.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
						this.getManagedBean().getUserActiveRoles().add(userRole);
					}

				}
			}
			// load password
			pmUser.setPassword(null);
			pmUser.setConfirmPassword(null);

			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setSelectedUser(pmUser);

			// update address entered flag
			// addressEntered(); 19Nov2014
			
			//load challengeQA
			clearChallengeQuestions();
			
			//load challengeQADropDownList
			getManagedBean().setChallengeQADDList(PmCommonUtil.retrieveChallengeQAList(pmUser));
			
			//clear password policy
			getManagedBean().setPasswordPolicy(null);
			generatePasswordPolicy();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016,
					new String[] { e.getMessage() });
		}
	}

	/**
	 * Get Role description by role code.
	 * 
	 * @param roleCode
	 *            the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode) {
		logger.info("getRoleDescByRoleCode");
		return PmCommonUtil.getRoleDescByRoleCode(roleCode);
	}

	/**
	 * retrieve user portrait, return default portrait if liferay return null
	 * 
	 * @param loginId
	 * @return String
	 */
	public String userImagePathWithDefaultImage(String loginId) {
		logger.info("userImagePathWithDefaultImage");
		String imagePath = "";

		imagePath = userImagePath(loginId);
		if (StringUtils.isBlank(imagePath)) {
			imagePath = ProfileManagementConst.DEFAULT_PORTRAIT;
		}
		return imagePath;
	}

	/**
	 * populate user address to managed bean
	 * 
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser) {
		logger.info("populateUserAddress");
		try {
			List<PmAddressType> addressTypes = pmUser.getPmAddressTypes();
			PmAddressType userAddress = null;
			for (PmAddressType addressType : addressTypes) {
				if (ProfileManagementConst.ADDRESS_USER
						.equalsIgnoreCase(addressType.getAddressType())) {
					userAddress = addressType;
					break;
				}

			}

			if (userAddress != null) {
				this.getManagedBean()
						.setUserAddress(userAddress.getPmAddress());
			} else {
				this.getManagedBean().setUserAddress(
						PmCommonUtil.getNewPmAddress());
				this.getManagedBean().getUserAddress()
						.setPmCountry(new PmCountry());
			}

			// List<PmAddressType> addressTypes =
			// PmCommonUtil.retrieveAddressTypesByUserIdOrAddressType(pmUser.getUserId(),
			// ProfileManagementConst.ADDRESS_USER);
			// this.getManagedBean().setUserAddress((addressTypes!=null &&
			// !addressTypes.isEmpty()) ? addressTypes.get(0).getPmAddress() :
			// PmCommonUtil.getNewPmAddress());

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770017,
					new String[] { e.getMessage() });
		}
	}

	public String retrieveParamDesc(String type, String code) {
		if (code != null) {
			return getParameterDesc(type, code);
		}
		return null;
	}

	/**
	 * retrieve salutation list
	 * 
	 * @return List<PmSalutation>
	 */
	public List<PmSalutation> salutationOptions() {
		logger.info("salutationOptions");
		// return getDropdownValues(ParameterTypeConst.SALUTATIONS);
		return PmCommonUtil.retrievePmSalutationList();
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}

	/**
	 * get all country list
	 * 
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		logger.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}

	/**
	 * get state list by country id
	 * 
	 * @param countryId
	 * @return List<PmState>
	 */
	public List<PmState> getStateList(Long countryId) {
		logger.info("getStateList");
		return PmCommonUtil.retrievePmStateList(countryId);
	}

	/**
	 * get division list by state id (only applicable for sarawak)
	 * 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public List<PmDivision> getDivisionList(Long stateId) {
		logger.info("getDivisionList");
		return PmCommonUtil.retrievePmDivisionList(stateId);
	}

	/**
	 * get district list by state id
	 * 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		logger.info("getDistrictList");
		return PmCommonUtil.retrievePmDistrictList(stateId);
	}

	/**
	 * get city list by district id
	 * 
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		logger.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}

	/**
	 * update division flag to either disable or enable division selection on UI
	 * 
	 * @param divisionType
	 * @param stateId
	 */
	public void updateDivisionFlag(Long stateId) {
		logger.info("updateDivisionFlag");
		if (stateId != null) {
			PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			this.getManagedBean().setDivisionFlagC(
					(pmState != null && ParameterConst.SARAK_SK_CODE
							.equals(pmState.getStateCode())) ? true : false);
		}
		updateAddress(1);
	}

	/**
	 * reset PmAddress accordingly
	 * 
	 * @param address
	 */
	public void updateAddress(int address) {
		logger.info("updateAddress");
		logger.info("address: " + address);
		if (0 == address) { // country
			this.getManagedBean().getUserAddress().setPmState(new PmState());
			this.getManagedBean().getUserAddress()
					.setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress()
					.setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		} else if (1 == address) { // state
			this.getManagedBean().getUserAddress()
					.setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress()
					.setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		} else if (2 == address) { // district);
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		}
		addressEntered();
	}

	/**
	 * retrieve identity type from PM
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getIdentityTypeList() {
		logger.info("getIdentityTypeList");
		return getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}

	/**
	 * retrieve all identity type into mapper
	 */
	protected void retrieveIdentityTypeMap() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);

		identityTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
	}

	/**
	 * get all language list
	 * 
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		logger.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}

	/**
	 * Validation for PmUser's fields (User Profile Sub Menu)
	 * 
	 * @param user
	 */
	private void validateUser(PmUser user) {
		logger.info("validateUser");
		if (StringUtils.isBlank(user.getLoginId())) {
			throw new EpException(MessageConst.ERROR_770030);
		}
		if (user.getSalutationCode().getSalutationId() == null) {
			throw new EpException(MessageConst.ERROR_770031);
		}
		if (StringUtils.isBlank(user.getUserName())) {
			throw new EpException(MessageConst.ERROR_770032);
		}
		if (user.getIdentificationCode().getParameterId() == null) {
			throw new EpException(MessageConst.ERROR_770033);
		}
		if (StringUtils.isBlank(user.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_770034);
		}
		if (user.getPmCountry().getCountryId() == null) {
			throw new EpException(MessageConst.ERROR_770035);
		}
		if (StringUtils.isBlank(user.getEmail())) {
			throw new EpException(MessageConst.ERROR_770036);
		}
		// if(StringUtils.isBlank(user.getPreferredLanguage())){
		// throw new EpException(MessageConst.ERROR_770037);
		// }
		// if(user.getNotifyRemovalDay()==null
		// || (user.getNotifyRemovalDay()!=null
		// && user.getNotifyRemovalDay()==0)){
		// throw new EpException(MessageConst.ERROR_770038);
		// }

	}

	public void addressEntered() {
		PmAddress pmAddress = this.getManagedBean().getUserAddress();
		// address is optional
		if (pmAddress.getPmCountry().getCountryId() != null
				|| !StringUtils.isBlank(pmAddress.getAddress1())
				|| !StringUtils.isBlank(pmAddress.getPostcode())
				|| pmAddress.getPmState().getStateId() != null
				|| pmAddress.getPmDistrict().getDistrictId() != null
				|| pmAddress.getPmDivision().getDivisionId() != null) {
			this.getManagedBean().setAddressEntered(true);
		} else {
			this.getManagedBean().setAddressEntered(false);
		}
	}

	/**
	 * Validation for PmAddress's fields (User Profile Sub Menu)
	 * 
	 * @param pmAddress
	 */
	private boolean validateUserAddress(PmAddress pmAddress) {
		logger.info("validateUserAddress");
		boolean addressEntered = false;

		// address is optional
		if (pmAddress.getPmCountry().getCountryId() != null
				|| !StringUtils.isBlank(pmAddress.getAddress1())
				|| !StringUtils.isBlank(pmAddress.getPostcode())
				|| pmAddress.getPmState().getStateId() != null
				|| pmAddress.getPmDistrict().getDistrictId() != null
				|| pmAddress.getPmDivision().getDivisionId() != null) {
			addressEntered = true;
		}

		if (addressEntered) {
			if (pmAddress.getPmCountry().getCountryId() == null) {
				throw new EpException(MessageConst.ERROR_770039);
			}

			if (StringUtils.isBlank(pmAddress.getAddress1())) {
				throw new EpException(MessageConst.ERROR_770040);
			}

			if (StringUtils.isBlank(pmAddress.getPostcode())) {
				throw new EpException(MessageConst.ERROR_770041);
			}

			if (pmAddress.getPmState().getStateId() == null) {
				throw new EpException(MessageConst.ERROR_770042);
			}

			if (pmAddress.getPmDistrict().getDistrictId() == null) {
				throw new EpException(MessageConst.ERROR_770043);
			}

			if (this.getManagedBean().isDivisionFlagC()) {
				if (pmAddress.getPmDivision().getDivisionId() == null) {
					throw new EpException(MessageConst.ERROR_770044);
				}
			}
		}
		return addressEntered;
	}

	/**
	 * Validation for PmUser
	 * 
	 * @param PmUser
	 */
	private void validatePreference(PmUser pmUser) {
		logger.info("validatePreferences");
		if (pmUser != null) {
			if (StringUtils.isBlank(pmUser.getPreferredLanguage())) {
				throw new EpException(MessageConst.ERROR_770046);
			}
			
			 if(pmUser.isNotifyBySms() 
					 && StringUtils.isBlank(pmUser.getMobileNo())){
				 throw new EpException(MessageConst.ERROR_770090);
			 }
			 
			 if(pmUser.isNotifyBySms() 
					 && (pmUser.getMobileTelcoId()  == null)){
				 throw new EpException(MessageConst.ERROR_770250);
			 }
			
//			if (pmUser.getNotifyRemovalDay() == null
//					|| pmUser.getNotifyRemovalDay() <= 0) {
//				throw new EpException(MessageConst.ERROR_770047);
//			}
		}
	}

	/**
	 * load user address - C
	 * 
	 * @param user
	 * @param isAddressExist
	 */
	private void loadUserAddress(PmUser user, boolean isAddressExist) {
		logger.info("loadUserAddress");

		// update contact address
		if (user.getPmAddressTypes() != null
				&& !user.getPmAddressTypes().isEmpty()) {
			boolean isUserAddressExist = false;
			for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
				if (DbValueConst.RECORD_STATUS_ACTIVE == addressTypeObj
						.getRecordStatusFlg()) {
					if (ProfileManagementConst.ADDRESS_USER
							.equalsIgnoreCase(addressTypeObj.getAddressType())) {
						// update address
						if (isAddressExist) {
							addressTypeObj.setPmAddress(this.getManagedBean()
									.getUserAddress());
							if (addressTypeObj.getPmAddress().getPmDivision() != null
									&& addressTypeObj.getPmAddress()
											.getPmDivision().getDivisionId() == null) {
								addressTypeObj.getPmAddress().setPmDivision(
										null);
							}
						} else {
							// remove address
							addressTypeObj
									.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						}
						isUserAddressExist = true;
						break;
					}
				}
			}

			if (!isUserAddressExist) {
				// add address
				PmAddressType pmAddressType = new PmAddressType();
				pmAddressType
						.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(this.getManagedBean()
						.getUserAddress());
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision()
								.getDivisionId() == null) {
					pmAddressType.getPmAddress().setPmDivision(null);
				}
				pmAddressType.setPmOrgProfile(null);
				user.addPmAddressType(pmAddressType);
			}
		} else {
			// add address
			if (isAddressExist) {
				if (user.getPmAddressTypes() == null) {
					user.setPmAddressTypes(new ArrayList<PmAddressType>());
				}
				PmAddressType pmAddressType = new PmAddressType();
				pmAddressType
						.setAddressType(ProfileManagementConst.ADDRESS_USER);
				pmAddressType.setPmAddress(this.getManagedBean()
						.getUserAddress());
				if (pmAddressType.getPmAddress().getPmDivision() != null
						&& pmAddressType.getPmAddress().getPmDivision()
								.getDivisionId() == null) {
					pmAddressType.getPmAddress().setPmDivision(null);
				}
				pmAddressType.setPmOrgProfile(null);
				user.addPmAddressType(pmAddressType);
			}
		}

	}

	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() throws Exception {
		debugLogger("saveUserProfile");
		PmUser user = this.getManagedBean().getSelectedUser();
		boolean addressEntered = false;
		// validation
		validateUser(user);
		// addressEntered
		// =validateUserAddress(this.getManagedBean().getUserAddress());
		// 19Nov2014
		validatePreference(user);
		fillUserOrgInfoFromUser(user);

		// loadUserAddress(user, addressEntered); 19Nov2014

		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			if(!user.isBrandNew()){
				//track changes for tracking Diary
				if(user.isModified()){
					IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
							.getServiceObject("pm", IStatusSO.class);
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(user.getRecordStatusFlg()
							.longValue());
					StatusResDTO statusResDTO = statusSO
							.retrieveStatusByStatusId(statusReqDTO);
					
				logger.info("Tracking diary for my profile :saveUserProfile");
				String messageId = MessageConst.TD_80023;
				String[] msgParam = new String[] {user.getUserName()};
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam),statusResDTO.getPmStatus() != null ? statusResDTO
										.getPmStatus() : new PmStatus()));
				
				}
			}
			reqDTO.setPmUser(user);

			UserResDTO resDTO = userSO.saveUserWithoutOrg(reqDTO); // saveUser2

			if (resDTO.isSuccessful()) {
				/*
				 * if(user.getPortrait()!=null){
				 * uploadPortrait(resDTO.getPmUser()); }
				 */
				loadUserProfile(user.getUserId());
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_770051,
										new String[] { resDTO.getPmUser()
												.getUserName() },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
			// throw new EpException(MessageConst.ERROR_770050, new
			// String[]{e.getMessage()});
		} finally {
			userSO = null;
		}
	}
	private String getActionDesc(String messageId, String[] params) {
		Locale myLocale = new Locale("ms", "MY");
		return MessageUtils.getMessage(messageId, params,
				MessageUtils.BUNDLE_COMMON_MESSAGES, myLocale);
	}
	/**
	 * get record status options //TODO: to load dynamically
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}

	/**
	 * login id = identification no.
	 */
	public void updateLoginId() {
		this.getManagedBean()
				.getSelectedUser()
				.setLoginId(
						this.getManagedBean().getSelectedUser()
								.getIdentificationNo());
	}

	/**
	 * Update portrait
	 * 
	 * @throws Exception
	 */
	public void updatePortrait() throws Exception {
		logger.info("updatePortrait - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();

		try {
			if (this.getManagedBean().getSelectedUser().getPortrait() == null) {
				throw new EpException(MessageConst.ERROR_770119);
			}

			reqDTO.setPortrait(this.getManagedBean().getSelectedUser()
					.getPortrait());
			reqDTO.setLoginId(this.getManagedBean().getSelectedUser()
					.getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.updatePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_770122,
														new String[] { this
																.getManagedBean()
																.getSelectedUser()
																.getLoginId() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770120,
					new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("updatePortrait - END");
	}

	/**
	 * Update portrait
	 * 
	 * @throws Exception
	 */
	public void deletePortrait() throws Exception {
		logger.info("deletePortrait - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();
		try {

			reqDTO.setLoginId(this.getManagedBean().getSelectedUser()
					.getLoginId());

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.deletePortrait(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_770123,
														new String[] { this
																.getManagedBean()
																.getSelectedUser()
																.getLoginId() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770121,
					new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("deletePortrait - END");
	}

	/**
	 * Get user portrait
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void handlePortraitUpload(FileUploadEvent event) throws Exception {
		logger.info("handlePortraitUpload - START");
		UploadedFile item = event.getFile();
		byte[] bytes = item.getContents();
		this.getManagedBean().getSelectedUser().setPortrait(bytes);
		this.getManagedBean().getSelectedUser().setReplacePortrait(true);
		updatePortrait();
		logger.info("handlePortraitUpload - END");
	}

	/**
	 * clear user portrait
	 * 
	 * @throws Exception
	 */
	public void clearPortraitUpload() throws Exception {
		logger.info("clearPortraitUpload - START");
		this.getManagedBean().getSelectedUser().setPortrait(null);
		this.getManagedBean().getSelectedUser().setReplacePortrait(false);
		logger.info("clearPortraitUpload - END");
	}

	/**
	 * reset password ui
	 */
	public void clearPassword() {
		logger.info("clearPassword");
		this.getManagedBean().getSelectedUser().setPassword(null);
		this.getManagedBean().getSelectedUser().setConfirmPassword(null);
	}

	/**
	 * Change password
	 * 
	 * @throws Exception
	 */
	public void changePassword() throws Exception {
		logger.info("changePassword - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();
		try {

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getLoginId())) {
				throw new EpException(MessageConst.ERROR_770125);
			}
			
			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getOldPassword())) {
				throw new EpException(MessageConst.ERROR_770124);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getPassword())) {
				throw new EpException(MessageConst.ERROR_770126);
			}

			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getConfirmPassword())) {
				throw new EpException(MessageConst.ERROR_770127);
			}

			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
			PmUser user = this.getManagedBean().getSelectedUser();
            reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
        	IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStatusSO.class);
			StatusReqDTO statusReqDTO = new StatusReqDTO();
        	statusReqDTO.setStatusId(user.getRecordStatusFlg()
					.longValue());
			StatusResDTO statusResDTO = statusSO
					.retrieveStatusByStatusId(statusReqDTO);
			logger.info("Tracking diary for my profile: change password :changePassword");
			String messageId = MessageConst.TD_80024;
			String[] msgParam = new String[] {user.getUserName()};
			reqDTO.setTrackingStatus(true);
			reqDTO.getTrackDairyVOs().add(
						PmCommonUtil.assignTrackingDairyValues(user,
								getActionDesc(messageId, msgParam),statusResDTO.getPmStatus() != null ? statusResDTO
										.getPmStatus() : new PmStatus()));
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			resDTO = userSO.changePassword(reqDTO);

			if (resDTO != null && resDTO.isSuccessful()) {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														MessageConst.INFO_770125,
														new String[] { this
																.getManagedBean()
																.getSelectedUser()
																.getLoginId() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				clearPassword();

			}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770128,
					new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("changePassword - END");
	}
	
	public SelectItem[] getChallengeQAList(){
		PmUser pmUser = getFacesScopes().getUserSessionBean();			
		return PmCommonUtil.retrieveChallengeQAList(pmUser);
	}
	
	public String getChallengeQuestionsLabel(Long index){
		index = index + 1;
		return ResourceBundleUtils.getMessage(
				"lbl.pm.profiles.ccq.q"+index,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	public String getChallengeAnswersLabel(Long index){
		index = index + 1;
		return ResourceBundleUtils.getMessage(
				"lbl.pm.profiles.ccq.a"+index,
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	
	
	/**
	 * Change password
	 * 
	 * @throws Exception
	 */
	public void changeChallengeQuestions() throws Exception {
		logger.info("changeChallengeQuestions - START");
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		UserResDTO resDTO = new UserResDTO();
		try {
			if (StringUtils.isBlank(this.getManagedBean().getSelectedUser()
					.getLoginId())) {
				throw new EpException(MessageConst.ERROR_770125);
			}
			if (StringUtils.isBlank(this.getManagedBean().getCurrentPassword())) {
				throw new EpException(MessageConst.ERROR_770124);
			}
			Set<String> questionsSet = new HashSet<String>();
			Set<String> answersSet = new HashSet<String>();
			List<String> questionList = new ArrayList<String>();
			List<String> answerList = new ArrayList<String>();
			int counter = 1;
			for(ChallengeQAList qa : getManagedBean().getChallengeQAList()){
				if(!StringUtils.isBlank(qa.getQuestion())){
					questionsSet.add(qa.getQuestion());
					questionList.add(qa.getQuestion());
				}else{
					throw new EpException("Please Select Question " + counter);
				}
				
				if(!StringUtils.isBlank(qa.getAnswer())){
					answersSet.add(qa.getAnswer());
					answerList.add(qa.getAnswer());
				}else{
					throw new EpException("Please Insert Answer " + counter);
				}
				counter ++;
			}
			
			
			if(questionsSet.size() != getManagedBean().getChallengeQAList().size()){
				throw new EpException("Duplicate Questions are selected");
			}
			
			if(answersSet.size() != getManagedBean().getChallengeQAList().size()){
				throw new EpException("Duplicate Answers are inserted");
			}
			
			 userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
			 ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			 reqDTO = new UserReqDTO();
			 reqDTO.setTrackDairyVOs(new ArrayList<PmTrackingDairyVO>());
			 PmUser user = getManagedBean().getSelectedUser();
				IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance()
						.getServiceObject("pm", IStatusSO.class);
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				statusReqDTO.setStatusId(user.getRecordStatusFlg()
						.longValue());
				StatusResDTO statusResDTO = statusSO
						.retrieveStatusByStatusId(statusReqDTO);
				logger.info("Tracking diary for my profile: change challenge Questions :changeChallengeQuestions");
				String messageId = MessageConst.TD_80029;
				String[] msgParam = new String[] {user.getUserName()};
				reqDTO.setTrackingStatus(true);
				reqDTO.getTrackDairyVOs().add(
							PmCommonUtil.assignTrackingDairyValues(user,
									getActionDesc(messageId, msgParam), statusResDTO.getPmStatus() != null ? statusResDTO
											.getPmStatus() : new PmStatus()));
			 reqDTO.setLoginId(getManagedBean().getSelectedUser()
					.getLoginId());
			 reqDTO.setUserOimPassword(getManagedBean().getCurrentPassword());
			 reqDTO.setChallangeQuestions(questionList);
			 reqDTO.setChallangeAnswers(answerList);
			 resDTO = userSO.setOimChallengeQA(reqDTO);
			 if (resDTO != null && resDTO.isSuccessful()) {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_INFO,
											null,
											MessageUtils
													.getMessage(
															MessageConst.INFO_770171,
															new String[] { this
																	.getManagedBean()
																	.getSelectedUser()
																	.getLoginId() },
															MessageUtils.BUNDLE_COMMON_MESSAGES,
															new FacesScopes()
																	.getUserLocale())));
					clearChallengeQuestions();

				}

		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770171,
					new String[] { e.getMessage() });
		} finally {
			userSO = null;
		}
		logger.info("changeChallengeQuestions - END");
	}
	
	
	/**
	 * reset challenge questions ui
	 */
	public void clearChallengeQuestions() {
		logger.info("clearChallengeQuestions");
		this.getManagedBean().setCurrentPassword(null);
		//load challengeQA
		List<ChallengeQAList> challengeQAList = new ArrayList<ChallengeQAList>();
		challengeQAList.add(new ChallengeQAList(null,null));
		challengeQAList.add(new ChallengeQAList(null,null));
		challengeQAList.add(new ChallengeQAList(null,null));			
		getManagedBean().setChallengeQAList(challengeQAList);
	}
	
	
	private void fillUserOrgInfoFromUser(PmUser user) {
		for(PmUserOrg activeOrg: user.getPmUserOrgs()){
			if(DbValueConst.RECORD_STATUS_ACTIVE == activeOrg.getRecordStatusFlg()){
				activeOrg.setEmail(user.getEmail());
				activeOrg.setPhoneNo(user.getPhoneNo());
				activeOrg.setFaxNo(user.getFaxNo());
				activeOrg.setMobileNo(user.getMobileNo());
				break;
			}
		}
	}
	
	public void generatePasswordPolicy(){	
		IUserSO userSO = null;
		if(StringUtils.isBlank(getManagedBean().getPasswordPolicy())){
			try {
				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setLoginId(getManagedBean().getSelectedUser().getLoginId());		
				reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
				UserResDTO resDTO = userSO.getPasswordPolicyInfo(reqDTO);
				if(resDTO.isSuccessful() && resDTO.getPolicyInfos() != null){
					StringBuilder sb = new StringBuilder();					
					sb.append("<b>");
					sb.append(ResourceBundleUtils.getMessage(
							"lbl.pm.profiles.cp.tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
					sb.append("</b><br/>");
					for (String policy : resDTO.getPolicyInfos()) {
						sb.append("&#160;&#8226;&#160; ");
						sb.append(policy);
						sb.append("<br/>");
					}
					getManagedBean().setPasswordPolicy(sb.toString());
				}				
			}finally{
					userSO = null;
			}
		}
		
	}

	// //TODO: testing
	// public void handleFileUpload(FileUploadEvent event) {
	// System.out.println("handleFileUploadTest - start");
	// UploadedFile item = event.getFile();s
	//
	// System.out.println("login ID: " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("username: " +
	// this.getManagedBean().getSelectedUser().getUserName());
	//
	// this.getManagedBean().getSelectedUser().setPortrait(item.getContents());
	//
	// System.out.println("login ID: " +
	// this.getManagedBean().getSelectedUser().getLoginId());
	// System.out.println("username: " +
	// this.getManagedBean().getSelectedUser().getUserName());
	// IUserSO userSO = null;
	// try {
	// userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
	// ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	// UserReqDTO reqDTO = new UserReqDTO();
	// reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
	//
	// UserResDTO resDTO = userSO.updatePortrait(reqDTO);
	//
	// if (resDTO.isSuccessful()) {
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_INFO,
	// null, "Record save successfully for Login ID: "
	// + resDTO.getPmUser().getLoginId()));
	// this.preLoadingMyAccount();
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// FacesContext.getCurrentInstance().addMessage(
	// null,
	// new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
	// e.getMessage()));
	// } finally {
	// userSO = null;
	// }
	//
	// FacesMessage msg = new FacesMessage("Succesful", event.getFile()
	// .getFileName() + " is uploaded.");
	// FacesContext.getCurrentInstance().addMessage(null, msg);
	//
	// System.out.println("handleFileUploadTest - end");
	// }

}
