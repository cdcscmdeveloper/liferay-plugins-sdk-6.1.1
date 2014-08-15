package my.ep.web.backingbean.pm.profilepersonalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
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
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.profilepersonalization.ProfilePersonalizationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "profilePersonalizationBackingBean")
@RequestScoped
public class ProfilePersonalizationBackingBean extends BaseBackingBean<ProfilePersonalizationManagedBean> {

	
	private static final Logger logger = EpLogger
			.getLogger(ProfilePersonalizationBackingBean.class);
	
	//Navigation Details
	public static final String NAV_TO_MYACCOUNT = "/xhtml/pm/profilePersonalization/userDetails/profileManagementMaintain.xhtml"; 
	//Mapper for DDL object
	private static Map<Long, Object> identityTypeMapper;
	
	//Tab values
	public static final int TAB_USER_PROFILE = 0;
	public static final int TAB_PERSONALIZATION = 1;
	public static final int TAB_CHANGE_PASSWORD = 2;
	
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
	
	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	/**
	 * load clean view for my account
	 */
	public void preLoadingMyAccount(){
		logger.info("preLoadingMyAccount");
		FacesScopes facesScopes = new FacesScopes();
		PmUser sUsers = facesScopes.getUserSessionBean();
		if(sUsers==null){
			throw new EpException("Unable to get the user details."); 
		}
		this.retrieveIdentityTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		loadUserProfile(sUsers.getUserId());
		
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_MYACCOUNT);
	}
	/**
	 * initial new pmUser object
	 */
	private void initialUserProfile(){
		logger.info("initialUserProfile");
		//load user attributes
		PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		PmUser pmUser = new PmUser();
		pmUser.setSalutationCode(new PmSalutation());
		pmUser.setPmCountry(defaultCountry);
		pmUser.setIdentificationCode(new PmParameter());
		pmUser.setSalutationCode(new PmSalutation());
		pmUser.setPMP(false);
		pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());
		pmUser.setOrgTypeCode(new PmParameter());
		
		//initial preference - notification
		PmCommonUtil.generateDefaultPmUserNotifications(pmUser, getFacesScopes().getUserLocale().getLanguage());

		//initial preference
		if(pmUser.getPmUserPreference()==null){
			PmUserPreference pmUserPreference = new PmUserPreference();
			pmUserPreference.setPmUser(pmUser);
			pmUser.setPmUserPreference(pmUserPreference);
		}
		
		//initial address
		this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());
		
		//load password
		pmUser.setPassword(null);
		pmUser.setOldPassword(null);
		pmUser.setConfirmPassword(null);
		
		this.getManagedBean().setSelectedUser(pmUser);
		System.out.println("login " + this.getManagedBean().getSelectedUser().getLoginId());
	}
	

	/**
	 * load pm user object by id
	 * @param id
	 */
	private void loadUserProfile(Long id){
		logger.info("loadUserProfile");
		try {
			initialUserProfile();
			if(id==null){
				throw new EpException("Please login."); //TODO
			}
			//load user
			PmUser pmUser = PmCommonUtil.retrieveUserById(id);
			pmUser.setPMP(pmUser.isPMP()?true:false);
			pmUser.setIdentificationCode(pmUser.getIdentificationCode()==null ? new PmParameter() : pmUser.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode()==null ? new PmSalutation() : pmUser.getSalutationCode());
			pmUser.setOrgTypeCode(pmUser.getOrgTypeCode()==null ? new PmParameter() : pmUser.getOrgTypeCode());
			
			//load address
			if (pmUser!=null && pmUser.getPmAddressTypes() != null) {
				populateUserAddress(pmUser);
			}
			//load preference - notification
			PmCommonUtil.generateDefaultPmUserNotifications(pmUser, getFacesScopes().getUserLocale().getLanguage());
			
			//load preference 
			if(pmUser.getPmUserPreference()==null){
				PmUserPreference pmUserPreference = new PmUserPreference();
				pmUserPreference.setPmUser(pmUser);
				pmUser.setPmUserPreference(pmUserPreference);
			}
			
			//load password
			pmUser.setPassword(null);
			pmUser.setOldPassword(null);
			pmUser.setConfirmPassword(null);
			
			this.getManagedBean().setSelectedUser(pmUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to view User Profile.", new String[]{e.getMessage()}); //TODO exception
		}	
	}
	

	/**
	 * populate user address to managed bean
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser){
		logger.info("populateUserAddress");
		boolean hasAddress = false;
		try {
			for (PmAddressType addressTypeObj : pmUser.getPmAddressTypes()) {
				if (addressTypeObj.getAddressType().equalsIgnoreCase(ProfileManagementConst.ADDRESS_USER)) {
					this.getManagedBean().setUserAddress(addressTypeObj.getPmAddress());
					hasAddress = true;
					break;
				}
			}
			if(!hasAddress){
				this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException("Unable to retrieve/populate address.", new String[]{e.getMessage()}); //TODO exception
		}
	}
	
	/**
	 * retrieve salutation list
	 * @return List<PmSalutation> 
	 */
	public SelectItem[] salutationOptions(){
		logger.info("salutationOptions");
		return getDropdownValues(ParameterTypeConst.SALUTATIONS);
	}
	

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}
	
	
	/**
	 * get all country list
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		logger.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}
	
	/**
	 * get state list by country id
	 * @param countryId
	 * @return List<PmState>
	 */
	public List<PmState> getStateList(Long countryId) {
		logger.info("getStateList");
		return PmCommonUtil.retrievePmStateList(countryId);
	}
	
	/**
	 * get division list by state id (only applicable for sarawak)
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public List<PmDivision> getDivisionList(Long stateId) {
		logger.info("getDivisionList");
		return PmCommonUtil.retrievePmDivisionList(stateId);
	}
	
	/**
	 * get district list by state id
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		logger.info("getDistrictList");
		return PmCommonUtil.retrievePmDistrictList(stateId);
	}
	
	/**
	 * get city list by district id
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		logger.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}

	/**
	 * update division flag to either disable or enable division selection on UI
	 * @param divisionType
	 * @param stateId
	 */
	public void updateDivisionFlag(Long stateId){
		logger.info("updateDivisionFlag");
		if(stateId!=null){
			PmState pmState = PmCommonUtil.retrievePmStateByStateId(stateId);
			this.getManagedBean().setDivisionFlagC(
					(pmState!=null && ParameterConst.SARAK_SK_CODE.equals(pmState.getStateCode())) ? true : false);
		}
	}
	

	/**
	 * retrieve identity type from PM
	 * @return SelectItem[]
	 */
	public SelectItem[] getIdentityTypeList() {
		logger.info("getIdentityTypeList");
		return getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}


	/**
	 * retrieve all identity type into mapper
	 */
	protected void retrieveIdentityTypeMap(){
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		identityTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}

	/**
	 * get all language list
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		logger.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}
	

	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() {
		debugLogger("saveUserProfile");
		PmUser user = this.getManagedBean().getSelectedUser();
		
		//update contact address
		if(user.getPmAddressTypes()!=null && !user.getPmAddressTypes().isEmpty()){
			for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
				if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressTypeObj.getAddressType())) {
					addressTypeObj.setPmAddress(this.getManagedBean().getUserAddress());
					break;
				}
			}
		}

		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(user);

			UserResDTO resDTO = userSO.saveUser(reqDTO); //saveUser2

			if (resDTO.isSuccessful()) {
				System.out.println("before reload");
				loadUserProfile(user.getUserId());
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								null, "Record save successfully for Login ID: "
										+ resDTO.getPmUser().getLoginId()));

			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
	}
	
	/**
	 * login id = identification no.
	 */
	public void updateLoginId(){
		this.getManagedBean().getSelectedUser().setLoginId(this.getManagedBean().getSelectedUser().getIdentificationNo());
	}

	
	
	//TODO: testing
	public void handleFileUpload(FileUploadEvent event) {
		System.out.println("handleFileUploadTest - start");
		UploadedFile item = event.getFile();

		System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
		System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());

		this.getManagedBean().getSelectedUser().setPortrait(item.getContents());
		
		System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
		System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());
		IUserSO userSO = null;
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());

			UserResDTO resDTO = userSO.updatePortrait(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								null, "Record save successfully for Login ID: "
										+ resDTO.getPmUser().getLoginId()));
				this.preLoadingMyAccount();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		} finally {
			userSO = null;
		}
		
		FacesMessage msg = new FacesMessage("Succesful", event.getFile()
				.getFileName() + " is uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		System.out.println("handleFileUploadTest - end");
	}

	
}
