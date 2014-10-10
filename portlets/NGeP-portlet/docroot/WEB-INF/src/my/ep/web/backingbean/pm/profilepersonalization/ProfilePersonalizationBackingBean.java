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
import my.ep.constant.MessageConst;
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
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
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
			throw new EpException(MessageConst.ERROR_770045); 
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
	
		pmUser.setNotifyByEmail(true);
		
		//initial preference
		PmCommonUtil.loadPmUserPreference(pmUser);
		
		//initial address
		this.getManagedBean().setUserAddress(PmCommonUtil.getNewPmAddress());
		
		//load password
		pmUser.setPassword(null);
//		pmUser.setOldPassword(null);
		pmUser.setConfirmPassword(null);
		this.getManagedBean().setDeletePortrait(false);
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
				throw new EpException(MessageConst.ERROR_770015);
			}
			//load user
			PmUser pmUser = PmCommonUtil.retrieveUserById(id, false);
			pmUser.setPMP(pmUser.isPMP()?true:false);
			pmUser.setIdentificationCode(pmUser.getIdentificationCode()==null ? new PmParameter() : pmUser.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode()==null ? new PmSalutation() : pmUser.getSalutationCode());
			pmUser.setOrgTypeCode(pmUser.getOrgTypeCode()==null ? new PmParameter() : pmUser.getOrgTypeCode());
			
			//load address
			populateUserAddress(pmUser);
			
			//initial preference
			PmCommonUtil.loadPmUserPreference(pmUser);
			
			//load password
			pmUser.setPassword(null);
//			pmUser.setOldPassword(null);
			pmUser.setConfirmPassword(null);
			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setSelectedUser(pmUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[]{e.getMessage()});
		}	
	}
	

	/**
	 * populate user address to managed bean
	 * @param pmUser
	 */
	private void populateUserAddress(PmUser pmUser){
		logger.info("populateUserAddress");
		try {
			List<PmAddressType> addressTypes = PmCommonUtil.retrieveAddressTypesByUserIdOrAddressType(pmUser.getUserId(), ProfileManagementConst.ADDRESS_USER);
			this.getManagedBean().setUserAddress((addressTypes!=null && !addressTypes.isEmpty()) ? addressTypes.get(0).getPmAddress() : PmCommonUtil.getNewPmAddress());
			
	
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770017, new String[]{e.getMessage()}); 
		}
	}
	
	public String retrieveParamDesc(String type, String code){
		if(code!=null){
			return getParameterDesc(type, code);
		}
		return null;
	}


	/**
	 * retrieve salutation list
	 * @return List<PmSalutation> 
	 */
	public List<PmSalutation> salutationOptions(){
		logger.info("salutationOptions");
//		return getDropdownValues(ParameterTypeConst.SALUTATIONS);
		return PmCommonUtil.retrievePmSalutationList();
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
		updateAddress(1);
	}
	
	/**
	 * reset PmAddress accordingly
	 * @param address
	 */
	public void updateAddress(int address){
		if(0==address){ //country
			this.getManagedBean().getUserAddress().setPmState(new PmState());
			this.getManagedBean().getUserAddress().setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress().setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		}else if(1==address){ //state
			this.getManagedBean().getUserAddress().setPmDivision(new PmDivision());
			this.getManagedBean().getUserAddress().setPmDistrict(new PmDistrict());
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
		}else if(2==address){ //district);
			this.getManagedBean().getUserAddress().setPmCity(new PmCity());
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
	 * Validation for PmUser's fields (User Profile Sub Menu)
	 * @param user
	 */
	private void validateUser(PmUser user){
		logger.info("validateUser");
		if(StringUtils.isBlank(user.getLoginId())){
			throw new EpException(MessageConst.ERROR_770030);
		}
		if(user.getSalutationCode().getSalutationId()==null){
			throw new EpException(MessageConst.ERROR_770031);
		}
		if(StringUtils.isBlank(user.getUserName())){
			throw new EpException(MessageConst.ERROR_770032);
		}
		if(user.getIdentificationCode().getParameterId()==null){
			throw new EpException(MessageConst.ERROR_770033);
		}
		if(StringUtils.isBlank(user.getIdentificationNo())){
			throw new EpException(MessageConst.ERROR_770034);
		}
		if(user.getPmCountry().getCountryId()==null){
			throw new EpException(MessageConst.ERROR_770035);
		}
		if(StringUtils.isBlank(user.getEmail())){
			throw new EpException(MessageConst.ERROR_770036);
		}
		if(StringUtils.isBlank(user.getPreferredLanguage())){
			throw new EpException(MessageConst.ERROR_770037);
		}
		if(user.getNotifyRemovalDay()==null 
				|| (user.getNotifyRemovalDay()!=null 
				&& user.getNotifyRemovalDay()==0)){
			throw new EpException(MessageConst.ERROR_770038);
		}

	}
	
	/**
	 * Validation for PmAddress's fields (User Profile Sub Menu)
	 * @param pmAddress
	 */
	private void validateUserAddress(PmAddress pmAddress){
		logger.info("validateUserAddress");
		if(pmAddress.getPmCountry().getCountryId()==null){
			throw new EpException(MessageConst.ERROR_770039);
		}

		if(StringUtils.isBlank(pmAddress.getAddress1())){
			throw new EpException(MessageConst.ERROR_770040);
		}
		
		if(StringUtils.isBlank(pmAddress.getPostcode())){
			throw new EpException(MessageConst.ERROR_770041);
		}
		
		if(pmAddress.getPmState().getStateId()==null){
			throw new EpException(MessageConst.ERROR_770042);
		}

		if(pmAddress.getPmDistrict().getDistrictId()==null){
			throw new EpException(MessageConst.ERROR_770043);
		}
		
		if(this.getManagedBean().isDivisionFlagC()){
			if(pmAddress.getPmDivision().getDivisionId()==null){
				throw new EpException(MessageConst.ERROR_770044);
			}
		}
	
	}	

	/**
	 * Validation for PmUser
	 * @param PmUser
	 */
	private void validatePreference(PmUser pmUser){
		logger.info("validatePreferences");
		if(pmUser!=null){
			if(StringUtils.isBlank(pmUser.getPreferredLanguage())){
				throw new EpException(MessageConst.ERROR_770046);
			}
			
			if(pmUser.getNotifyRemovalDay()==null || pmUser.getNotifyRemovalDay()<=0){
				throw new EpException(MessageConst.ERROR_770047);
			}	
		}
	}

	
	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() throws Exception{
		debugLogger("saveUserProfile");
		PmUser user = this.getManagedBean().getSelectedUser();
		
		//validation
		validateUser(user);	
		validateUserAddress(this.getManagedBean().getUserAddress());		
		validatePreference(user);

		
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
				/*
				if(user.getPortrait()!=null){
					uploadPortrait(resDTO.getPmUser());
				}
				*/
				loadUserProfile(user.getUserId());
				FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(
					    FacesMessage.SEVERITY_INFO,
					    null,
			    		MessageUtils.getMessage(MessageConst.INFO_770049, new String[]{resDTO.getPmUser().getLoginId()}, 
				    		MessageUtils.BUNDLE_COMMON_MESSAGES, 
				    		new FacesScopes().getUserLocale())
			    		));

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
//			throw new EpException(MessageConst.ERROR_770050, new String[]{e.getMessage()});
		} finally {
			userSO = null;
		}
	}
	
	/**
	 * get record status options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
	}
	/**
	 * login id = identification no.
	 */
	public void updateLoginId(){
		this.getManagedBean().getSelectedUser().setLoginId(this.getManagedBean().getSelectedUser().getIdentificationNo());
	}

	/**
	 * delete portrait
	 * @throws Exception
	 */
	public void deletePortrait() throws Exception{
		logger.info("deletePortrait - START");
		this.getManagedBean().getSelectedUser().setPortrait(null);
		this.getManagedBean().getSelectedUser().setReplacePortrait(true);
		this.getManagedBean().setDeletePortrait(true);
		/*
		IUserSO userSO = null;
		UserReqDTO reqDTO = new UserReqDTO();
		try {
			PmUser pmUser = this.getManagedBean().getSelectedUser();
			reqDTO.setPmUser(pmUser);
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserResDTO resDTO = userSO.deletePortrait(reqDTO); //saveuser2
			if(resDTO!=null && resDTO.isSuccessful()){
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								null, "Delete Portrait Successfull for Login ID: "
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
		}*/
		logger.info("deletePortrait - END");
	}
	
//	/**
//	 * TODO: fix the remote updatePortrait
//	 * @param pmUser
//	 * @throws Exception
//	 */
//	private void uploadPortrait(PmUser pmUser) throws Exception{
//		logger.info("uploadPortrait - START");
//		long imageMaxSize = PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
//		long companyId = Long.parseLong(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID));
//		byte[] bytes = pmUser.getPortrait();
//		long userId;
//		logger.debug("imageMaxSize: " + imageMaxSize);
//		logger.debug("companyId: " + companyId);
//		logger.debug("bytes: " + bytes);
//		logger.debug("pmUser.getLoginId(): " + pmUser.getLoginId());
//		if(bytes!=null){
//			User user = UserServiceUtil.getUserByScreenName(companyId, pmUser.getLoginId());
//			if(user!=null){
//				System.out.println("user id" + user.getUserId());
//			}else{
//				System.out.println("user null");
//			}
//			userId = user.getUserId();
//			logger.debug("userId: " + userId);
//			UserServiceUtil.updatePortrait(user.getUserId(), bytes);
//		}
//		logger.info("uploadPortrait - END");
//	}
	/**
	 * Get user portrait 
	 * @param event
	 * @throws Exception
	 */
	public void handlePortraitUpload(FileUploadEvent event) throws Exception{
		logger.info("handlePortraitUpload - START");
		UploadedFile item = event.getFile();
		byte[] bytes = item.getContents();
		this.getManagedBean().getSelectedUser().setPortrait(bytes);  
		this.getManagedBean().getSelectedUser().setReplacePortrait(true);
		logger.info("handlePortraitUpload - END");
	}

	/**
	 * clear user portrait 
	 * @throws Exception
	 */
	public void clearPortraitUpload() throws Exception{
		logger.info("clearPortraitUpload - START");
		this.getManagedBean().getSelectedUser().setPortrait(null);  
		this.getManagedBean().getSelectedUser().setReplacePortrait(false);
		logger.info("clearPortraitUpload - END");
	}

		
		
	
//	//TODO: testing
//	public void handleFileUpload(FileUploadEvent event) {
//		System.out.println("handleFileUploadTest - start");
//		UploadedFile item = event.getFile();s
//
//		System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
//		System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());
//
//		this.getManagedBean().getSelectedUser().setPortrait(item.getContents());
//		
//		System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
//		System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());
//		IUserSO userSO = null;
//		try {
//			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//			UserReqDTO reqDTO = new UserReqDTO();
//			reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
//
//			UserResDTO resDTO = userSO.updatePortrait(reqDTO);
//
//			if (resDTO.isSuccessful()) {
//				FacesContext.getCurrentInstance().addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO,
//								null, "Record save successfully for Login ID: "
//										+ resDTO.getPmUser().getLoginId()));
//				this.preLoadingMyAccount();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			FacesContext.getCurrentInstance().addMessage(
//					null,
//					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
//							e.getMessage()));
//		} finally {
//			userSO = null;
//		}
//		
//		FacesMessage msg = new FacesMessage("Succesful", event.getFile()
//				.getFileName() + " is uploaded.");
//		FacesContext.getCurrentInstance().addMessage(null, msg);
//		
//		System.out.println("handleFileUploadTest - end");
//	}

	
}
