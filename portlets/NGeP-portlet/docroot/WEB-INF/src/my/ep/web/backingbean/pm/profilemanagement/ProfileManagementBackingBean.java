package my.ep.web.backingbean.pm.profilemanagement;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRole;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
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
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.RoleHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Backing Bean for User Profiles (Management)
 * 
 * @author DK, RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "profileManagementBackingBean")
@ViewScoped
public class ProfileManagementBackingBean extends BaseBackingBean<ProfileManagementManagedBean> {

	private static final Logger logger = EpLogger
			.getLogger(ProfileManagementBackingBean.class);

	
	//Navigation Details
	public static final String NAV_TO_SEARCH = "/xhtml/pm/profilemanagement/search/profileManagementMain.xhtml"; // main page for portlet profile maintenance
	public static final String NAV_TO_MAINTAIN = "/xhtml/pm/profilemanagement/userDetails/profileManagementMaintain.xhtml"; 

	//Default value
	public static final boolean SEARCH_DEFAULT = false;
	
	//Tab values
	public static final int TAB_USER_PROFILE = 0;
	public static final int TAB_AUTHORIZATION = 1;
	public static final int TAB_PERSONALIZATION = 2;
	public static final int TAB_CHANGE_PASSWORD = 3;
	
	//Paging for listing
	private IPagingDataModel<PmUser> userProfileDataModel = new UserProfilePagingDataModel();
	private IPagingDataModel<PmRoleDesc> roleDataModel = new RolePagingDataModel();	

	//Mapper for DDL object
	private static Map<Long, Object> identityTypeMapper;

	/**
	 * @return the tabUserProfile
	 */
	public int getTabUserProfile() {
		return TAB_USER_PROFILE;
	}

	/**
	 * @return the tabAuthorization
	 */
	public int getTabAuthorization() {
		return TAB_AUTHORIZATION;
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
	
	
	
	public ProfileManagementBackingBean(){
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	
	}
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.info(message);
		}
		logger.info(message);
	}
	
	/**
	 * load clean view for Profile Maintenance
	 */
	public void preLoadingUserProfile(){
		logger.info("preLoadingUserProfile");
		this.resetUserProfileSearchBean();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		FacesContext fc = FacesContext.getCurrentInstance(); 
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	
	/**
	 * reset basic search bean
	 */
	public void resetUserProfileSearchBean() {
		this.getManagedBean().setUserProfileSearchBean(new UserProfilesSearchBean());
	}
	
	/**
	 * USER PROFILE - START
	 */
	
	/**
	 * redirect to user details
	 * @param pmUser
	 * @return
	 */
	public String redirectToMaintainUser(Long id) throws Exception{
		logger.info("redirectToMaintainUser");
		this.retrieveIdentityTypeMap();
		this.getManagedBean().setSelectedTabIndex(TAB_USER_PROFILE);
		if(id!=null && id>0){
			loadUserProfile(id);
		}else{
			initialUserProfile();
		}
		return NAV_TO_MAINTAIN;
	}
	
	/**
	 * redirect to main page
	 * @return String
	 */
	public String redirectToMainPage() {
		logger.info("redirectToMainPage");

		return NAV_TO_SEARCH;
	}
	
	/**
	 * load pm user object by id
	 * @param id
	 */
	private void loadUserProfile(Long id){
		logger.info("loadUserProfile - Start");
		try {
			initialUserProfile();
			if(id==null){
				throw new EpException(MessageConst.ERROR_770015); 
			}
			//load user
			PmUser pmUser = PmCommonUtil.retrieveUserById(id, true);
			pmUser.setPMP(pmUser.isPMP()?true:false);
			pmUser.setIdentificationCode(pmUser.getIdentificationCode()==null ? new PmParameter() : pmUser.getIdentificationCode());
			pmUser.setSalutationCode(pmUser.getSalutationCode()==null ? new PmSalutation() : pmUser.getSalutationCode());
			pmUser.setOrgTypeCode(pmUser.getOrgTypeCode()==null ? new PmParameter() : pmUser.getOrgTypeCode());
			
			populateUserAddress(pmUser);
			
			//load preference
			PmCommonUtil.loadPmUserPreference(pmUser);
			
			//load attachment
			if(pmUser.getPmUserAttachments()==null){
				pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());
			}
			
			//load password
			pmUser.setPassword(null);
//			pmUser.setOldPassword(null);
			pmUser.setConfirmPassword(null);
			
			this.getManagedBean().setSelectedUser(pmUser);
			this.getManagedBean().setDeleteUserRoles(null);
			this.getManagedBean().setDeleteReceivingAddrTypes(null);
			this.getManagedBean().setSelectedDeliveringAddrTypes(null);
			this.getManagedBean().setSelectedUserOrg(null);
			this.getManagedBean().setSelectedRoles(null);
			this.getManagedBean().setSelectedReceivingAddrTypes(null);
			this.getManagedBean().setReceivingAddressFlag(false);
			this.getManagedBean().setDeletePmUserAttachments(null);
			this.getManagedBean().setDeletePortrait(false);
			this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
			//for the initial load
			if(pmUser.getPmUserOrgs()!=null && !pmUser.getPmUserOrgs().isEmpty()){
				this.getManagedBean().setSelectedUserOrg(pmUser.getPmUserOrgs().get(0));
				showReceivingAddress();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770016, new String[]{e.getMessage()}); 
		}	
		logger.info("loadUserProfile - End");
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
		pmUser.setPmUserAttachments(new ArrayList<PmUserAttachment>());
		
		this.getManagedBean().setSelectedUser(pmUser);
		this.getManagedBean().setSelectedUserOrg(null);
		this.getManagedBean().setSelectedRoles(null);
		this.getManagedBean().setDeleteUserRoles(null);
		this.getManagedBean().setDeleteReceivingAddrTypes(null);
		this.getManagedBean().setSelectedDeliveringAddrTypes(null);
		this.getManagedBean().setSelectedReceivingAddrTypes(null);
		this.getManagedBean().setDeletePmUserAttachments(null);
		this.getManagedBean().setDeletePortrait(false);
		this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
		System.out.println("login " + this.getManagedBean().getSelectedUser().getLoginId());
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

	/**
	 * retrieve salutation list
	 * @return List<PmSalutation> 
	 */
	public List<PmSalutation> salutationOptions(){
		logger.info("salutationOptions");
		return PmCommonUtil.retrievePmSalutationList();
//		return getDropdownValues(ParameterTypeConst.SALUTATIONS);
	}
	

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public SelectItem[] getYesNoOptions() {
		return PmCommonUtil.getYesNoBooleanOptions();
	}
	

	/**
	 * get record status options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getStatusOptions() {
		return PmCommonUtil.getStatusOptions();
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
	
	public Date getCurrentDate(){
		Date currentDate = new Date();
		return currentDate;
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
		return getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}


	/**
	 * retrieve all identity type into mapper
	 */
	private void retrieveIdentityTypeMap(){
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescListBySearchBean(ParameterTypeConst.IDENTIFICATION_TYPES, null);
		
		identityTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			identityTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * set user identity type
	 */
	public void identityTypeChange(){
		logger.info("identityTypeChange");
		
		if(this.getManagedBean().getSelectedUser().getIdentificationCode().getParameterId()!=null){
			PmParameter pmParameter = (PmParameter) identityTypeMapper.get(this.getManagedBean().getSelectedUser().getIdentificationCode().getParameterId());
			this.getManagedBean().getSelectedUser().getIdentificationCode().setParameterCode(pmParameter.getParameterCode());
		}
	}
	
	/**
	 * login id = identification no.
	 */
	public void updateLoginId(){
		this.getManagedBean().getSelectedUser().setLoginId(this.getManagedBean().getSelectedUser().getIdentificationNo());
	}
	
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
	
	/**
	 * USER PROFILE - END
	 */
	
	/**
	 * get all language list
	 * @return List<PmLanguage>
	 */
	public List<PmLanguage> getLanguageList() {
		logger.info("getLanguageList");
		return PmCommonUtil.retrieveLanguageObjs();
	}
	
	/**
	 * TAB Authorization - START
	 */
	
	/**
	 * Retrieve Org. Type from PM
	 * @return List<PmParameterDesc> 
	 */
	public List<PmParameterDesc> getOrgTypeList() {
		logger.info("getOrgTypeList");
		return PmCommonUtil.retrieveFilteredOrgTypes(ParameterTypeConst.ORGANISATION_TYPES
				, new ArrayList(Arrays.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE))
				, null);
	}
	
	/**
	 * To add new item to Responsible Org. Unit list
	 */
	public void addOrgUnit() {
		logger.info("addOrgUnit");
		if(this.getManagedBean().getSelectedUser().getPmUserOrgs()==null){
			this.getManagedBean().getSelectedUser().setPmUserOrgs(new ArrayList<PmUserOrg>());
		}
		if(!this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()){
			int size = this.getManagedBean().getSelectedUser().getPmUserOrgs().size();
			PmUserOrg pmUserOrg = this.getManagedBean().getSelectedUser().getPmUserOrgs().get(size-1);
			if(pmUserOrg.getExpDate()==null){
				throw new EpException(MessageConst.ERROR_770018); 
			}
		}
		
		PmUserOrg pmUserOrg = new PmUserOrg();
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		pmOrgProfile.setOrgTypeCode(new PmParameter());
		pmOrgProfile.setPmOrgValidity(new PmOrgValidity());
	
		pmUserOrg.setPmOrgProfile(pmOrgProfile);
		pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
		this.getManagedBean().getSelectedUser().addPmUserOrg(pmUserOrg);
		this.getManagedBean().setSelectedUserOrg(pmUserOrg);
		showReceivingAddress();
	}


	/**
	 * Remove selected Responsible Org. Unit
	 */
	public void deleteOrgUnit(PmUserOrg pmUserOrg) {
		logger.info("deleteOrgUnit");
		if(pmUserOrg!=null && pmUserOrg.isBrandNew()){
			if(this.getManagedBean().getSelectedUser().getPmUserOrgs()==null){
				this.getManagedBean().getSelectedUser().setPmUserOrgs(new ArrayList<PmUserOrg>());
			}
			this.getManagedBean().getSelectedUser().getPmUserOrgs().remove(pmUserOrg);
		}
	}
	
	/**
	 * validate unique responsible org unit
	 * @param pmOrgProfile
	 */
	public void updateOrgCode(PmOrgProfile pmOrgProfile, int index){
		logger.info("updateOrgCode");
		boolean isValid = true;
		if(pmOrgProfile!=null && pmOrgProfile.getOrgProfileId()!=null){
			if(this.getManagedBean().getSelectedUser().getPmUserOrgs()!=null &&
					!this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()){
				int i=0;
				for(PmUserOrg pmUserOrg: this.getManagedBean().getSelectedUser().getPmUserOrgs()){
					if(pmUserOrg.getPmOrgProfile().getOrgProfileId().equals(pmOrgProfile.getOrgProfileId())
							&& i!=index){
						isValid = false;
						break;
					}
					i++;
				}
				if(!isValid){
					this.getManagedBean().getSelectedUser().getPmUserOrgs().get(index).getPmOrgProfile().setPmOrgValidity(new PmOrgValidity());
					throw new EpException(MessageConst.ERROR_770019); 
				}
				
			}
		}
	}
	
	/*
	public void orgUnitStatusChange(int index){
		logger.info("orgUnitStatusChange");
		if(this.getManagedBean().getSelectedUser().getPmUserOrgs()!=null && !this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()){
			this.getManagedBean().setSelectedUserOrg(this.getManagedBean().getSelectedUser().getPmUserOrgs().get(index));
			int status = this.getManagedBean().getSelectedUserOrg().getRecordStatusFlg();
			if(this.getManagedBean().getSelectedUserOrg()!=null && this.getManagedBean().getSelectedUserOrg().getPmUserRoles()!=null){
				for(PmUserRole pmUserRoles:this.getManagedBean().getSelectedUserOrg().getPmUserRoles()){
					pmUserRoles.setRecordStatusFlg(status);
					userRoleStatusChange(pmUserRoles);
				}
			}
		}
	}
	
	public void changeResponsibleOrgDates(ValueChangeEvent changeEvent){
		logger.info("changeResponsibleOrgDates");
		System.out.println("NEW " + changeEvent.getNewValue());
		System.out.println("OLD " + changeEvent.getOldValue());
	}
	*/
	
	/**
	 * Reset PmUserOrg info
	 * @param index
	 */
	public void changeResOrgType(int index){
		logger.info("changeResOrgType");
		if(this.getManagedBean().getSelectedUser().getPmUserOrgs()!=null && !this.getManagedBean().getSelectedUser().getPmUserOrgs().isEmpty()){
			this.getManagedBean().setSelectedUserOrg(this.getManagedBean().getSelectedUser().getPmUserOrgs().get(index));
			if(this.getManagedBean().getSelectedUserOrg()!=null){
				this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().setPmOrgValidity(new PmOrgValidity());
				this.getManagedBean().getSelectedUserOrg().setPmUserRoles(new ArrayList<PmUserRole>());
			}
		}	
	}
	
	/**
	 * Get Role description by role code.
	 * 
	 * @param roleCode
	 * 			the role code.
	 * @return role description
	 */
	public String getRoleDescByRoleCode(String roleCode) {	
		logger.info("getRoleDescByRoleCode");
		String roleDesc = null;
		
		if (roleCode != null) {
			// Get the role description.
			PmRoleDesc pmRoleDesc = RoleHelper.getInstance()
					.getRoleDescByCode(roleCode);
			
			if (pmRoleDesc != null) {
				roleDesc = pmRoleDesc.getRoleName();
			}
		}
		
		return roleDesc;
	}
	

	/**
	 * Add new selected role to user role list
	 */
	public void confirmRoles(){
		logger.info("confirmRoles");

		if(this.getManagedBean().getSelectedRoles()!=null && !this.getManagedBean().getSelectedRoles().isEmpty()){
			if(this.getManagedBean().getSelectedUserOrg()!=null){
				//Validation for unique roles added to list
				if(isUniqueRoles()){
					for(PmRoleDesc pmRoleDesc : this.getManagedBean().getSelectedRoles()){
						PmUserRole pmUserRole = new PmUserRole();
						pmUserRole.setPmRole(pmRoleDesc.getPmRole());
						this.getManagedBean().getSelectedUserOrg().addPmUserRole(pmUserRole);
					}
					this.getManagedBean().getSelectedRoles().clear();
					updateReceivingAddressFlag();
				}else{
					throw new EpException(MessageConst.ERROR_770020);
				}
			}else{
				throw new EpException(MessageConst.ERROR_770021);
			}
		}else{
			throw new EpException(MessageConst.ERROR_770022); 
		}
		RequestContext.getCurrentInstance().execute("dlRoleOptions.hide()");
		
	}
	
	/**
	 * update ui receiving address tab if selected roles contain "Receiving Role"
	 */
	private void updateReceivingAddressFlag(){
		logger.info("updateReceivingAddressFlag");
		if(this.getManagedBean().getSelectedUserOrg().getPmUserRoles()!=null && !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()){
			for(PmUserRole userRole: this.getManagedBean().getSelectedUserOrg().getPmUserRoles()){
				if(ProfileManagementConst.RECEIVING_OFFICER.equals(userRole.getPmRole().getRoleCode())){
					this.getManagedBean().setReceivingAddressFlag(true);
					break;
				}
			}
		}else{
			this.getManagedBean().setReceivingAddressFlag(false);
		}
	}
	
	/**
	 * Delete selected Role
	 */
	public void deleteRole(){
		logger.info("deleteRole");
		checkDeleteRoles();
		if(this.getManagedBean().getDeleteUserRoles()!=null && !this.getManagedBean().getDeleteUserRoles().isEmpty()){
			if(this.getManagedBean().getSelectedUserOrg().getPmUserRoles()==null){
				this.getManagedBean().getSelectedUserOrg().setPmUserRoles(new ArrayList<PmUserRole>());
			}
			for(PmUserRole userRole: this.getManagedBean().getDeleteUserRoles()){
				if(userRole.isBrandNew()){
					this.getManagedBean().getSelectedUserOrg().getPmUserRoles().remove(userRole);
				}
			}
			this.getManagedBean().getDeleteUserRoles().clear();
			updateReceivingAddressFlag();
		}else{
			throw new EpException(MessageConst.ERROR_770022);
		}
	}
	
	public void checkDeleteRoles(){
		if(this.getManagedBean().getDeleteUserRoles()!=null && !this.getManagedBean().getDeleteUserRoles().isEmpty()){
			List <PmUserRole> deleteRoles = new ArrayList<PmUserRole>();
			for(PmUserRole userRole: this.getManagedBean().getDeleteUserRoles()){
				if(!userRole.isBrandNew()){
					deleteRoles.add(userRole);
				}
			}
			for(PmUserRole userRole: deleteRoles){
				this.getManagedBean().getDeleteUserRoles().remove(userRole);
			}
			
		}
	}
	
	/**
	 * Validate if selected roles are not duplicated
	 * @return boolean
	 */
	private boolean isUniqueRoles(){
		logger.info("isUniqueRoles");
		boolean isUnique = true;
		if(this.getManagedBean().getSelectedUserOrg().getPmUserRoles()!=null 
				&& !this.getManagedBean().getSelectedUserOrg().getPmUserRoles().isEmpty()){
			List <PmRole> oriPmRoles = new ArrayList<PmRole>();
//			List <PmRole> newPmRoles = new ArrayList<PmRole>();
			
			for(PmUserRole obj:this.getManagedBean().getSelectedUserOrg().getPmUserRoles()){
				oriPmRoles.add(obj.getPmRole());
			}
			for(PmRoleDesc obj:this.getManagedBean().getSelectedRoles()){
				if(oriPmRoles.contains(obj.getPmRole())){
					isUnique = false;
					break;
				}
//				newPmRoles.add(obj.getPmRole());
			}
			
//			if(oriPmRoles.containsAll(newPmRoles)){
//				isUnique = false;
//			}
		}
		return isUnique;
	}
	
	/**
	 * Track user role status
	 * @param pmUserRole
	 */
	public void userRoleStatusChange(PmUserRole pmUserRole){
		logger.info("userRoleStatusChange");
		if(pmUserRole!=null){
			 if(pmUserRole.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_SUSPENDED){
				 if(!pmUserRole.isModified()){
					 pmUserRole.setModified(true);
				 }else{
					 pmUserRole.setModified(false);
				 }
			 }else if(pmUserRole.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE){
		 		if(!pmUserRole.isModified()){
		 			pmUserRole.setModified(true);
				}else{
					pmUserRole.setModified(false);
				}
			 }
		}
	}

	/**
	 * Show receiving addresses and user roles for selected responsible org.
	 * @param index
	 */
	public void showReceivingAddress(){
		logger.info("showReceivingAddress");
		try {

			/* show selected org's receiving addr */ 
			if(this.getManagedBean().getSelectedUserOrg()!=null && this.getManagedBean().getSelectedUser().getPmAddressTypes()!=null){
				if(this.getManagedBean().getSelectedReceivingAddrTypes()==null){
					this.getManagedBean().setSelectedReceivingAddrTypes(new ArrayList<PmAddressType>());
				}
				this.getManagedBean().getSelectedReceivingAddrTypes().clear();
				for (PmAddressType addressTypeObj : this.getManagedBean().getSelectedUser().getPmAddressTypes()){
					if (ProfileManagementConst.ADDRESS_RECEIVING.equalsIgnoreCase(addressTypeObj.getAddressType())
							&& (addressTypeObj.getPmOrgProfile()!=null && this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgProfileId() 
							== addressTypeObj.getPmOrgProfile().getOrgProfileId())) {
						this.getManagedBean().getSelectedReceivingAddrTypes().add(addressTypeObj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770023, new String[]{e.getMessage()});
		}
	}
	
	/**
	 * Add selected delivering address to receiving address
	 * @param deliveringAddrs
	 */
	public void updateReceivingAddr(List<PmAddressType>deliveringAddrs){
		logger.info("updateReceivingAddr");
		if(deliveringAddrs!=null){
			if(this.getManagedBean().getSelectedUser().getPmAddressTypes()==null){
				this.getManagedBean().getSelectedUser().setPmAddressTypes(new ArrayList<PmAddressType>());
			}
			if(isUniqueAddress()){
				System.out.println("selected size " + deliveringAddrs.size());
				for(PmAddressType pmAddressType: deliveringAddrs){
					PmAddressType tempAddTypeObj = new PmAddressType();
					tempAddTypeObj.setAddressType(ProfileManagementConst.ADDRESS_RECEIVING);
					tempAddTypeObj.setPmAddress(pmAddressType.getPmAddress());
					tempAddTypeObj.setPmUser(this.getManagedBean().getSelectedUser());
					tempAddTypeObj.setPmOrgProfile(this.getManagedBean().getSelectedUserOrg().getPmOrgProfile());
					this.getManagedBean().getSelectedUser().addPmAddressType(tempAddTypeObj);
				}
				this.getManagedBean().getSelectedDeliveringAddrTypes().clear();
				showReceivingAddress();
			}else{
				throw new EpException(MessageConst.ERROR_770024);
			}
		}
	}

	/**
	 * Delete selected receiving address
	 */
	public void deleteReceivingAddr(){
		logger.info("deleteReceivingAddr");
		if(this.getManagedBean().getDeleteReceivingAddrTypes()!=null && !this.getManagedBean().getDeleteReceivingAddrTypes().isEmpty()){
			if(this.getManagedBean().getSelectedReceivingAddrTypes()==null){
				this.getManagedBean().setSelectedReceivingAddrTypes(new ArrayList<PmAddressType>());
			}
			for(PmAddressType pmAddressType: this.getManagedBean().getDeleteReceivingAddrTypes()){
				this.getManagedBean().getSelectedUser().getPmAddressTypes().remove(pmAddressType);
//				this.getManagedBean().getSelectedReceivingAddrTypes().remove(pmAddressType);
			}
			this.getManagedBean().getDeleteReceivingAddrTypes().clear();
			showReceivingAddress();
		}else{
			throw new EpException(MessageConst.ERROR_770025); 
		}
	
	}
	
	/**
	 * Validate if selected delivering address are not duplicated
	 * @return boolean
	 */
	private boolean isUniqueAddress(){
		logger.info("isUniqueAddress");
		boolean isUnique = true;
		if(this.getManagedBean().getSelectedDeliveringAddrTypes()!=null 
				&& !this.getManagedBean().getSelectedDeliveringAddrTypes().isEmpty()){
			if(this.getManagedBean().getSelectedReceivingAddrTypes()!=null && !this.getManagedBean().getSelectedReceivingAddrTypes().isEmpty()){
				List <Long> deliveringAddr = new ArrayList<Long>();
				
				for(PmAddressType pmAddressType: this.getManagedBean().getSelectedDeliveringAddrTypes()){
					deliveringAddr.add(pmAddressType.getPmAddress().getAddressId());
				}	
			
				for(PmAddressType pmAddressType: this.getManagedBean().getSelectedReceivingAddrTypes()){
					if(deliveringAddr.contains(pmAddressType.getPmAddress().getAddressId())){
						isUnique = false;
						break;
					}
				}
			}
		}
		return isUnique;
	}
	
	/**
	 * Add attachment to list
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event)  throws FileNotFoundException, IOException {
		logger.info("handleFileUpload - START");
		try{
			if(event!=null){
				if(this.getManagedBean().getSelectedUser().getPmUserAttachments()==null){
					this.getManagedBean().getSelectedUser().setPmUserAttachments(new ArrayList<PmUserAttachment>());
				}
				if(this.getManagedBean().getFileEventMapper()==null){
					this.getManagedBean().setFileEventMapper(new HashMap<PmUserAttachment, FileUploadEvent>());
				}
				UploadedFile item = event.getFile();
			    PmUserAttachment attachment = new PmUserAttachment();
				attachment.setFileName(item.getFileName());
				attachment.setFilePath(PmCommonUtil.getUploadPath(item.getFileName()));
				attachment.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
				attachment.setCreatedUser(getFacesScopes().getUserSessionBean());
				attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				
				this.getManagedBean().getSelectedUser().addPmUserAttachments(attachment);
				this.getManagedBean().getFileEventMapper().put(attachment, event);
				
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException("Failed to upload attachement: {0}", new String[]{e.getMessage()});
			
		}
		logger.info("handleFileUpload - END");
	}
	

	/**
	 * Delete attachment
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachment(PmUserAttachment attachment) throws FileNotFoundException, IOException {
		logger.info("deleteAttachment - START");
		try{
			if(attachment!=null){
				if(this.getManagedBean().getDeletePmUserAttachments()==null){
					this.getManagedBean().setDeletePmUserAttachments(new ArrayList<PmUserAttachment>());
				}
				if(!attachment.isBrandNew()){
					this.getManagedBean().getDeletePmUserAttachments().add(attachment);
				}
				this.getManagedBean().getSelectedUser().getPmUserAttachments().remove(attachment);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054, new String[]{e.getMessage()});
			
		}
		logger.info("deleteAttachment - END");
	}


	/**
	 * Download attachment
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void downloadAttachment(PmUserAttachment attachment) throws FileNotFoundException, IOException {
		logger.info("downloadAttachment - START");
		try{
			if(attachment!=null){
				downloadFile(attachment.getFilePath(), attachment.getFileName());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770055, new String[]{e.getMessage()});
			
		}
		logger.info("downloadAttachment - END");
	}
	
	/**
	 * Download attachment from server
	 * @param filePath
	 * @param fileName
	 * @throws IOException
	 */
	private void downloadFile(String filePath, String fileName) throws IOException {
		logger.info("Inside downloadFile start");
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if(filePath!=null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1){ 
				out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
			}
			input.close();			
		} 
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
		logger.info("Inside downloadFile end");
	}
	
	public String getUsername(Long userId){
		return PmCommonUtil.retriveUserName(userId);
	}
	
	/**
	 * TAB Authorization - END
	 */
	
	
	/** Roles & Paging - Start  */
	/**
	 * show user role options
	 */
	
	private ISearchBean<PmRoleDesc> getRoleBeanData(ISearchBean<PmRoleDesc> searchBean, String parentCode){
		logger.info("getRoleBeanData");
		Long currentOrgTypeId = null;
		
		if(this.getManagedBean().getSelectedUserOrg()!=null && 
				this.getManagedBean().getSelectedUserOrg().getPmOrgProfile()!=null && 
				this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode()!=null){
			currentOrgTypeId = this.getManagedBean().getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId();
		}
		logger.info("currentOrgTypeId " + currentOrgTypeId);
		
		if(currentOrgTypeId!=null){
			SearchFilterBean languageBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			languageBean.setSearchField("languageCode");
			languageBean.setSearchValue(getFacesScopes().getUserLocale().getLanguage());
			searchBean.getDataFilterBeans().add(languageBean);
			
			if(parentCode!=null){
				logger.info("with parentCode");
				searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
				new SearchFilterBean("pmRole.pmOrgRoles.orgType.parameterId", SearchOperator.OPERATOR_EQUALS.value(), currentOrgTypeId), 
				SearchCondition.CONDITION_OR.value(),
				new SearchFilterBean("pmRole.parentRole.roleCode", SearchOperator.OPERATOR_EQUALS.value(), ProfileManagementConst.ROLE_APPROVER)));
				
//				SearchFilterBean orgBean = new SearchFilterBean(
//						SearchOperator.OPERATOR_EQUALS.value());
//				orgBean.setSearchField("pmRole.parentRole.roleCode");
//				orgBean.setSearchValue(currentOrgTypeId);
//				searchBean.getDataFilterBeans().add(orgBean);
			}else{
				logger.info("without parentCode");
				SearchFilterBean orgBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				orgBean.setSearchField("pmRole.pmOrgRoles.orgType.parameterId");
				orgBean.setSearchValue(currentOrgTypeId);
				searchBean.getDataFilterBeans().add(orgBean);
				
				SearchFilterBean statusBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				statusBean.setSearchField("pmRole.recordStatusFlg");
				statusBean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(statusBean);
				
				//remove parent role as children were selected
				SearchFilterBean approverBean = new SearchFilterBean(
						SearchOperator.OPERATOR_NOT_EQUALS.value());
				approverBean.setSearchField("pmRole.roleCode");
				approverBean.setSearchValue(ProfileManagementConst.ROLE_APPROVER);
				searchBean.getDataFilterBeans().add(approverBean);
			}
		}else{
			SearchFilterBean statusBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			statusBean.setSearchField("pmRole.recordStatusFlg");
			statusBean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(statusBean);
		}
		return searchBean;
	}
	
	
	/**
	 * search user roles
	 */
	public void getAvailbleUserRole() {
		logger.info("getAvailbleUserRole");
		roleDataModel.reloadData();
	}
	
	
	/**
	 * @return the roleDataModel
	 */
	public IPagingDataModel<PmRoleDesc> getRoleDataModel() {
		return roleDataModel;
	}

	/**
	 * @param roleDataModel the roleDataModel to set
	 */
	public void setRoleDataModel(IPagingDataModel<PmRoleDesc> roleDataModel) {
		this.roleDataModel = roleDataModel;
	}

	
	// pagination for role search list
	private class RolePagingDataModel extends PagingDataModel<PmRoleDesc> {
		private static final long serialVersionUID = 1L;

		public RolePagingDataModel() {
			super("dtRoleOptions");
		}

		@Override
		public ISearchResultBean<PmRoleDesc> pageChangeListener(ISearchBean<PmRoleDesc> searchBean) {
			if(logger.isDebugEnabled()) {
				logger.info("pageChangeListener");
			}
			try {
//				boolean isApprover = false;
				ISearchBean<PmRoleDesc> searchBeanData = getRoleBeanData(searchBean, null);
				RoleReqDTO roleReqDTO = new RoleReqDTO();
				roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
				roleReqDTO.setSearchRoleBean(searchBeanData);
				IRoleSO roleSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
				RoleResDTO resDto = roleSO.retrieveRoleDecsBySearchBean(roleReqDTO);
				
				if (resDto != null && resDto.isSuccessful()) {
					/*
					List <PmRoleDesc> roleResults = resDto.getSearchRoleResultBean().getResultList();
					for(PmRoleDesc roleDesc: roleResults){
						if(ProfileManagementConst.ROLE_APPROVER.equals(roleDesc.getPmRole().getRoleCode())){
							isApprover = true;
							break;
						}
					}
					if(isApprover){
						searchBeanData = getRoleBeanData(searchBean, ProfileManagementConst.ROLE_APPROVER);
						resDto = roleSO.retrieveRoleDecsBySearchBean(roleReqDTO);
						System.out.println("with parent "+resDto.getSearchRoleResultBean().getTotalRecord());
					}
					 */
//					getManagedBean().setAvailableRoles(resDto.getSearchRoleResultBean().getResultList());
					
					// disabled selected roles
					markupRoleResult(resDto.getSearchRoleResultBean());
					
					return resDto.getSearchRoleResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private void markupRoleResult(ISearchResultBean<PmRoleDesc> searchRoleResultBean){
		logger.info("markupRoleResult");
		// disabled selected roles
		if(searchRoleResultBean!=null 
				&& searchRoleResultBean.getResultList()!=null 
				&& !searchRoleResultBean.getResultList().isEmpty()){
			if(getManagedBean().getSelectedUserOrg()!=null 
					&& getManagedBean().getSelectedUserOrg().getPmUserRoles()!=null){
				List <PmRole> selectedRoles = new ArrayList<PmRole>();
				for(PmUserRole obj:getManagedBean().getSelectedUserOrg().getPmUserRoles()){
					selectedRoles.add(obj.getPmRole());
					
				}
				for(PmRoleDesc obj:searchRoleResultBean.getResultList()){
					if(selectedRoles.contains(obj.getPmRole())){
						obj.setSelected(true);
					}
				}
			}
		}
	}
	/** Roles & Paging - End  */

	
	
	/** User Search & Paging - Start  */
	
	/**
	 * search user profiles
	 */
	public void searchUserProfile(){
		logger.info("searchUserProfile");
		userProfileDataModel.reloadData();
	}
	
	/**
	 * get search bean 
	 * @return ISearchBean<PmUser>
	 */
	private ISearchBean<PmUser> getSearchBeanData(ISearchBean<PmUser> searchBean){
		logger.info("getSearchBeanData - Start");
		boolean isSearchable = SEARCH_DEFAULT;
		boolean isEmpty = true;
		UserProfilesSearchBean upSearchBean = this.getManagedBean().getUserProfileSearchBean();
		//Login Id
		if (upSearchBean.getLoginId() != null && !"".equalsIgnoreCase(upSearchBean.getLoginId())) {
			logger.info("+ loginId "  +upSearchBean.getLoginId());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("loginId");
			userSearchBean.setSearchValue(upSearchBean.getLoginId());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		//User Name
		if (upSearchBean.getUserName() != null && !"".equalsIgnoreCase(upSearchBean.getUserName())) {
			logger.info("+ userName "  +upSearchBean.getUserName());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("userName");
			userSearchBean.setSearchValue(upSearchBean.getUserName());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		//Org Name
		if (upSearchBean.getUserOrganizationName() != null && !"".equalsIgnoreCase(upSearchBean.getUserOrganizationName())) {
			logger.info("+ userOrganizationName "  +upSearchBean.getUserOrganizationName());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("pmUserOrgs.pmOrgProfile.pmOrgValidity.orgName");
			userSearchBean.setSearchValue(upSearchBean.getUserOrganizationName());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}

		//Role
		if (upSearchBean.getUserRole() != null && !"".equalsIgnoreCase(upSearchBean.getUserRole())) {
			logger.info("+ userRole "  +upSearchBean.getUserRole());
			SearchFilterBean userSearchBean = new SearchFilterBean(
					SearchOperator.OPERATOR_LIKE.value());
			userSearchBean.setCaseInsensitive(true);
			userSearchBean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.pmRoleDescs.roleName");
			userSearchBean.setSearchValue(upSearchBean.getUserRole());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		
		//Status if non provided then select default (active)
		if(upSearchBean.getUserStatus()!=null){
			logger.info("+ status "  +upSearchBean.getUserStatus());
			SearchFilterBean userSearchBean = new SearchFilterBean();
			userSearchBean.setSearchField("recordStatusFlg");
			userSearchBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			userSearchBean.setSearchValue(upSearchBean.getUserStatus());
			searchBean.getDataFilterBeans().add(userSearchBean);
			isEmpty = false;
		}
		
		logger.info("+ status try only "  + null);
		//base on the flag, default list is either select all or empty
		if(isEmpty && !isSearchable && upSearchBean.getUserStatus()==null){
			logger.info("+ status "  + null);
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("recordStatusFlg");
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchValue(null);
			searchBean.getDataFilterBeans().add(bean);
		}
		logger.info("getSearchBeanData - End");
		return searchBean;
	}
	
	public IPagingDataModel<PmUser> getUserProfileDataModel() {
		return userProfileDataModel;
	}

	public void setUserProfileDataModel(
			IPagingDataModel<PmUser> userProfileDataModel) {
		this.userProfileDataModel = userProfileDataModel;

	}
	
	// pagination for user search list
	private class UserProfilePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public UserProfilePagingDataModel() {
			super("dtUserResult");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
			try {	
				ISearchBean<PmUser> searchBeanData = getSearchBeanData(searchBean);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserSearchBean(searchBeanData);
				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);	
				UserResDTO resDto = userSO.searchUserProfile(userReqDTO);
				if (resDto != null && resDto.isSuccessful()) {
					markupUserResult(resDto.getUserResultBean());
					return resDto.getUserResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}
	
	private void markupUserResult(ISearchResultBean<PmUser> userResultBean){
		logger.info("markupUserResult");
		if(userResultBean!=null 
				&& userResultBean.getResultList()!=null 
				&& !userResultBean.getResultList().isEmpty()){
			
			for(PmUser pmUser:userResultBean.getResultList()){
				List<PmAddressType> addressTypes = PmCommonUtil.retrieveAddressTypesByUserIdOrAddressType(pmUser.getUserId(), ProfileManagementConst.ADDRESS_USER);
				pmUser.setAddressTypeC((addressTypes!=null && !addressTypes.isEmpty()) ? addressTypes.get(0) : new PmAddressType());
			}
		}
	}
	
	/** User Search & Paging - End  */



	
	/**
	 * load user address - C
	 * @param user
	 */
	private void loadUserAddress(PmUser user){
		if(!user.isBrandNew()){
			//update contact address
			if(user.getPmAddressTypes()!=null && !user.getPmAddressTypes().isEmpty()){
				for (PmAddressType addressTypeObj : user.getPmAddressTypes()) {
					if (ProfileManagementConst.ADDRESS_USER.equalsIgnoreCase(addressTypeObj.getAddressType())) {
						addressTypeObj.setPmAddress(this.getManagedBean().getUserAddress());
						if(addressTypeObj.getPmAddress().getPmDivision()!=null && addressTypeObj.getPmAddress().getPmDivision().getDivisionId()==null){
							addressTypeObj.getPmAddress().setPmDivision(null);
						}
						break;
					}
				}
			}
		}else{
			if(user.getPmAddressTypes()==null){
				user.setPmAddressTypes(new ArrayList<PmAddressType>());
			}
			PmAddressType pmAddressType = new PmAddressType();
			pmAddressType.setAddressType(ProfileManagementConst.ADDRESS_USER);
			pmAddressType.setPmAddress(this.getManagedBean().getUserAddress());
			if(pmAddressType.getPmAddress().getPmDivision()!=null && pmAddressType.getPmAddress().getPmDivision().getDivisionId()==null){
				pmAddressType.getPmAddress().setPmDivision(null);
			}
			pmAddressType.setPmOrgProfile(null);
			user.addPmAddressType(pmAddressType);
		}

	}
	
	/**
	 * load user org type with the last PmUserOrgs
	 * @param user
	 */
	private void loadUserOrgType(PmUser user){
		logger.info("loadUserOrgType");
		if(user.getPmUserOrgs()!=null && !user.getPmUserOrgs().isEmpty()){
			for(PmUserOrg userOrg: user.getPmUserOrgs()){
				if(userOrg.getRecordStatusFlg()== DbValueConst.RECORD_STATUS_ACTIVE){
					if(checkEffDate(userOrg)){
						user.setOrgTypeCode(userOrg.getPmOrgProfile().getOrgTypeCode());
						break;
					}
				}
			}
		}else{
			throw new EpException(MessageConst.ERROR_770026);
		}
	}
	
	/**
	 * sync notification data
	 * @param user
	 */
	private void loadUserNotification(PmUser user){
		logger.info("loadUserNotification");
		List <PmUserNotification> displayNotifications = this.getManagedBean().getNotifications();
		if(displayNotifications!=null && !displayNotifications.isEmpty()){
			for(PmUserNotification displayNotification: displayNotifications){
				for(PmUserNotification pmUserNotification: user.getPmUserNotifications()){
					if(!ParameterConst.NOTIFICATION_DASHBOARD.equals(displayNotification.getNotifyMode())
							&& !ParameterConst.NOTIFICATION_SMS.equals(displayNotification.getNotifyMode())){
						if(displayNotification.getNotifyMode().equals(pmUserNotification.getNotifyMode())){
							pmUserNotification.setPreferred(displayNotification.isPreferred());
						}
					}
				}
			}
		}
	}
	
	/**
	 * Generate new role and deleted role to update Liferay and OIM
	 * @param user
	 * @param reqDTO
	 */
	private void loadUserRoles(PmUser user, UserReqDTO reqDTO){
		logger.info("loadUserRoles");
		if(user.getPmUserOrgs()!=null && !user.getPmUserOrgs().isEmpty()){
			List<PmUserRole> newRoleList = reqDTO.getNewRoleList();
			List<PmUserRole> deleteRoleList = reqDTO.getRemoveRoleList();
			long activeUserOrgId = 0;
			boolean isChanged = false;
			for(PmUserOrg pmUserOrg:user.getPmUserOrgs()){
				if(pmUserOrg.isBrandNew()){
					isChanged = true;
				}
				if(pmUserOrg.isBrandNew() || pmUserOrg.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE){
					if(checkEffDate(pmUserOrg)){
						activeUserOrgId = pmUserOrg.getUserOrgId()!=null ? pmUserOrg.getUserOrgId() : 0;
						for(PmUserRole pmUserRole: pmUserOrg.getPmUserRoles()){
							if(pmUserRole.isBrandNew() || (pmUserRole.isModified() && pmUserRole.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE)){
								newRoleList.add(pmUserRole);
								System.out.println("ADD NEW: " + pmUserRole.getPmRole().getRoleCode());
							}else if(pmUserRole.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_SUSPENDED && pmUserRole.isModified()){
								deleteRoleList.add(pmUserRole);
								System.out.println("DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
							}
						}
					}
				}
			}
			if(activeUserOrgId>0 || isChanged){
				// retrieve previous active org list
				List <PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(user.getUserId());
				System.out.println("size " + pmUserOrgs.size());
				for(PmUserOrg pmUserOrg:pmUserOrgs){
					if(pmUserOrg.getUserOrgId()!=activeUserOrgId){
						for(PmUserRole pmUserRole: pmUserOrg.getPmUserRoles()){
							if(pmUserRole.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE){
								deleteRoleList.add(pmUserRole);
								System.out.println("2 DELETE OLD: " + pmUserRole.getPmRole().getRoleCode());
							}
						}
					}
				}
				reqDTO.setNewRoleList(newRoleList);
				reqDTO.setRemoveRoleList(deleteRoleList);
			}
		}
	}
	
	/**
	 * update only active 
	 * @param pmUserOrg
	 * @return
	 */
	private boolean checkEffDate(PmUserOrg pmUserOrg){
		logger.info("checkEffDate");
		Date currentDate = new Date();
		boolean isActive = true;
		// effDate after currentDate
		if(pmUserOrg.getEffDateDisplay().compareTo(currentDate)>0){
			isActive = false;
		// expDate not null and expDate before currentDate
		}else if(pmUserOrg.getExpDateDisplay()!=null && pmUserOrg.getExpDateDisplay().compareTo(currentDate)<0){
			isActive = false;
		}
		logger.info("checkEffDate active:" + isActive);
		
		
		return isActive;
	}


	/**
	 * Validation for Responsible Org. Unit date range
	 * @param user
	 * @throws Exception
	 */
	public void validateResponsibleOrg(PmUser user) throws Exception {
		logger.info("overlappingValidation");

		if (user.getPmUserOrgs() != null && !user.getPmUserOrgs().isEmpty()) {
			for (int index1 = 0; index1 < user.getPmUserOrgs().size(); index1++) {
				if (user.getPmUserOrgs().get(index1).getRecordStatusFlg() 
						== DbValueConst.RECORD_STATUS_ACTIVE) {
					if (user.getPmUserOrgs().size() > 1) {
						for (int index2 = index1 + 1; index2 < user.getPmUserOrgs().size(); index2++) {
							if (user.getPmUserOrgs().get(index2).getRecordStatusFlg() 
									== DbValueConst.RECORD_STATUS_ACTIVE) {
								// Overlap exists if (StartA <= EndB) and (EndA
								// >= StartB)
								Date startA = user.getPmUserOrgs().get(index1).getEffDate();
								Date endA = user.getPmUserOrgs().get(index1).getExpDate();
								Date startB = user.getPmUserOrgs().get(index2).getEffDate();
								Date endB = user.getPmUserOrgs().get(index2).getExpDate();

								// not last item
								if ((index2 + 1) == user.getPmUserOrgs().size()) {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
									Date endLess = sdf.parse("2099-12-31");
									endB = endLess;
								}
								boolean isStartABeforeEndB = ((startA
										.compareTo(endB)) < 0 || startA
										.equals(endB));
								boolean isEndAAfterStartB = ((endA
										.compareTo(startB)) > 0 || endA
										.equals(startB));

								boolean isCurrentPairOverlap = false;

								isCurrentPairOverlap = isStartABeforeEndB
										&& isEndAAfterStartB;

								if (isCurrentPairOverlap) {
									throw new EpException(
											MessageConst.ERROR_770027);
								}
							}
						}
					} 
//					else {
//						// make sure it is currently active if only one item
//						if (!checkEffDate(user.getPmUserOrgs().get(index1))) {
//								throw new EpException("Active Responsible Org. Unit is required.");
//						}
//					}
				}
			}
		}
	}
	
	/**
	 * Check if only one active org unit.
	 * @param user
	 */
	private void oneActiveResponsibleOrg(PmUser user){
		int activeNumber = 0;
		if(user.getPmUserOrgs()!=null){
			for(PmUserOrg userOrg: user.getPmUserOrgs()){
				if(userOrg.getRecordStatusFlg()== DbValueConst.RECORD_STATUS_ACTIVE){
					if(checkEffDate(userOrg)){
						activeNumber++;
					}
				}
			}
		}
		if(activeNumber>1){
			throw new EpException(MessageConst.ERROR_770028);
		}else if(activeNumber==0){
			throw new EpException(MessageConst.ERROR_770029);
		}
	}
	
	/**
	 * Validation for PmUser's fields
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
		if(StringUtils.isBlank(user.getPmUserPreference().getPreferredLanguage())){
			throw new EpException(MessageConst.ERROR_770037);
		}
		if(user.getPmUserPreference().getNotifyRemovalDay()==null 
				|| (user.getPmUserPreference().getNotifyRemovalDay()!=null 
				&& user.getPmUserPreference().getNotifyRemovalDay()==0)){
			throw new EpException(MessageConst.ERROR_770038);
		}
//		if(!StringUtils.isBlank(user.getConfirmPassword()) && !StringUtils.isBlank(user.getPassword()) && StringUtils.isBlank(user.getOldPassword())){
//			throw new EpException("Old Password is mandatory.");
//		}
	}
	
	/**
	 * Validation for PmAddress's fields
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
	
//	/**
//	 * Validation for PmUserNotification
//	 * @param PmUserNotification
//	 */
//	private void validateNotification(List <PmUserNotification> pmUserNotifications){
//		logger.info("validateNotification");
//		int selected = 0;
//		if(pmUserNotifications!=null && !pmUserNotifications.isEmpty()){
//			for(PmUserNotification pmUserNotification:pmUserNotifications){
//				if(DbValueConst.RECORD_STATUS_ACTIVE==pmUserNotification.getRecordStatusFlg()){
//					if(pmUserNotification.isPreferred()){
//						selected++;
//						break;
//					}
//				}
//			}
//		}
//		if(selected==0){
//			throw new EpException(MessageConst.ERROR_770048);
//		}
//	}

	/**
	 * Method to update the user details
	 */
	public void saveUserProfile() throws Exception{
		logger.info("saveUserProfile");
		
		UserReqDTO reqDTO = null;
		IUserSO userSO = null;
		PmUser user =  null; 
//		boolean isChangePwd = false;

		try {
			
			user = this.getManagedBean().getSelectedUser();
			
			// Validation
			validateUser(user);
			validateUserAddress(this.getManagedBean().getUserAddress());
			validateResponsibleOrg(user);
			oneActiveResponsibleOrg(user);
			validatePreference(user);
			
			reqDTO = new UserReqDTO();
			reqDTO.setServerName(getFacesScopes().getRequest().getServerName());
			reqDTO.setServerPort(getFacesScopes().getRequest().getServerPort());
			
			/* no need check as assumed user have logged in
			if(!StringUtils.isBlank(user.getConfirmPassword()) && !StringUtils.isBlank(user.getPassword())){ //!StringUtils.isBlank(user.getOldPassword()) &&
				isChangePwd = true;
			}
			
	
			if(isChangePwd){
				if(!checkOldPassword(user)){
					throw new EpException("Old Password is invalid."); 
				}
			}*/
			
			loadUserAddress(user);
			
			loadUserOrgType(user);
			
			loadUserNotification(user);
			
			
			
			if(!user.isBrandNew()){
				reqDTO.setNewRoleList(new ArrayList<PmUserRole>());
				reqDTO.setRemoveRoleList(new ArrayList<PmUserRole>());
				loadUserRoles(user, reqDTO);
			}

			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			reqDTO.setPmUser(user);
			UserResDTO resDTO = userSO.saveUser(reqDTO); 
			

			if (resDTO.isSuccessful()) {
				
				uploadAttachments();
				deleteAttachments();
				/*
				if(user.getPortrait()!=null){
					uploadPortrait(resDTO.getPmUser());
				}*/
				
				loadUserProfile(resDTO.getPmUser().getUserId());
				
				if(!StringUtils.isBlank(resDTO.getRemarks())) {
					//OIM user creation successfully, problem with roles.
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, resDTO.getRemarks(), resDTO.getRemarks()));
				}else{
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(
							    FacesMessage.SEVERITY_INFO,
							    null,
					    		MessageUtils.getMessage(MessageConst.INFO_770049, new String[]{resDTO.getPmUser().getLoginId()}, 
						    		MessageUtils.BUNDLE_COMMON_MESSAGES, 
						    		new FacesScopes().getUserLocale())
					    		));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
//			throw new EpException(MessageConst.ERROR_770059, new String[]{e.getMessage(),e.getMessage()});
		} finally {
			userSO = null;
		}
	}
	
	/**
	 * Upload files
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void uploadAttachments() throws FileNotFoundException, IOException {
		logger.info("uploadAttachments - START");
		try{
			if(this.getManagedBean().getFileEventMapper()!=null && !this.getManagedBean().getFileEventMapper().isEmpty()){
				PmUserAttachment tempPmUserAttachment = null;
				FileUploadEvent tempFileUploadEvent = null;
				for (Map.Entry<PmUserAttachment, FileUploadEvent> entry : this.getManagedBean().getFileEventMapper().entrySet()) {
					tempPmUserAttachment = entry.getKey(); //PmUserAttachment
					tempFileUploadEvent = entry.getValue(); //FileUploadEvent
					PmCommonUtil.commonUploadFile(tempFileUploadEvent, PmCommonUtil.getAttachmentPath(tempPmUserAttachment.getFilePath()));
				}
				this.getManagedBean().getFileEventMapper().clear();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770060, new String[]{e.getMessage()});
			
		}
		logger.info("uploadAttachments - END");
	}

	/**
	 * Delete attachment
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachments() throws FileNotFoundException, IOException {
		logger.info("deleteAttachment - START");
		try{
			if(this.getManagedBean().getDeletePmUserAttachments()!=null && !this.getManagedBean().getDeletePmUserAttachments().isEmpty()){
				for(PmUserAttachment pmUserAttachment:this.getManagedBean().getDeletePmUserAttachments()){
					if(!pmUserAttachment.isBrandNew()){
						PmCommonUtil.deleteUploadFile(pmUserAttachment.getFilePath());
					}
				}
				PmCommonUtil.deleteUserAttachment(this.getManagedBean().getDeletePmUserAttachments());
				this.getManagedBean().getDeletePmUserAttachments().clear();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054, new String[]{e.getMessage()});
			
		}
		logger.info("deleteAttachment - END");
	}
	
	/*
	private boolean checkOldPassword(PmUser pmUser) throws PortalException, SystemException{
		logger.info("checkOldPassword");
		boolean result = false;
		long status = 0;
		long companyId = Long.parseLong(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID));
		String loginId = pmUser.getLoginId();
		String pwd = pmUser.getOldPassword();
		System.out.println("loginId: " + loginId);
		System.out.println("pwd: " + pwd);
		System.out.println("companyId: " + companyId);
		status = UserLocalServiceUtil.authenticateForBasic(companyId, CompanyConstants.AUTH_TYPE_SN, loginId, pwd);
		if(status>0){
			result = true;
		}
		return result;
	}
*/
	

	/**
	 * delete portrait
	 * @throws Exception
	 */
	public void deletePortrait() throws Exception{
		logger.info("deletePortrait - START");
		this.getManagedBean().getSelectedUser().setPortrait(null);
		this.getManagedBean().getSelectedUser().setReplacePortrait(true);
		this.getManagedBean().setDeletePortrait(true);
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
//		logger.info("imageMaxSize: " + imageMaxSize);
//		logger.info("companyId: " + companyId);
//		logger.info("bytes: " + bytes);
//		logger.info("pmUser.getLoginId(): " + pmUser.getLoginId());
//		if(bytes!=null){
//			User user = UserServiceUtil.getUserByScreenName(companyId, pmUser.getLoginId());
//			if(user!=null){
//				System.out.println("user id" + user.getUserId());
//			}else{
//				System.out.println("user null");
//			}
//			userId = user.getUserId();
//			logger.info("userId: " + userId);
//			UserServiceUtil.updatePortrait(user.getUserId(), bytes);
//		}
//		logger.info("uploadPortrait - END");
//	}
//	
//	/**
//	 * Get user portrait 
//	 * @param event
//	 * @throws Exception
//	 */
//	public void handleFileUpload1() throws Exception{
//		try{
//			logger.info("handleFileUpload - START - UserServiceUtil");
//			byte[] bytes = this.getManagedBean().getSelectedUser().getPortrait();
//			long imageMaxSize = PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
//			System.out.println("size = " + imageMaxSize);
//			System.out.println("bytes.length = " + bytes.length);   
//			System.out.println("screen name = " + this.getManagedBean().getSelectedUser().getLoginId());
//			System.out.println("identity no = " + this.getManagedBean().getSelectedUser().getIdentificationNo());
//			String screenName =  this.getManagedBean().getSelectedUser().getLoginId()==null
//						? this.getManagedBean().getSelectedUser().getIdentificationNo() 
//						: this.getManagedBean().getSelectedUser().getLoginId();
//			long companyId = Long.parseLong(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID));
//			User user = UserServiceUtil.getUserByScreenName(companyId , screenName);
//			if(user!=null){
//				System.out.println("user id" + user.getUserId());
//			}else{
//				System.out.println("user null");
//			}
//			long userId = user.getUserId();
//			UserServiceUtil.updatePortrait(userId, bytes); 
//		}catch(Exception e){
//			e.printStackTrace();
//			throw e;
//		}
//		logger.info("handleFileUpload - END");
//	}
//
//	/**
//	 * Get user portrait 
//	 * @param event
//	 * @throws Exception
//	 */
//	public void handleFileUpload2() throws Exception{
//		logger.info("handleFileUpload2 - START - Remote");
//		byte[] bytes = this.getManagedBean().getSelectedUser().getPortrait();
//		long imageMaxSize = PrefsPropsUtil.getLong(PropsKeys.USERS_IMAGE_MAX_SIZE);
//		System.out.println("size = " + imageMaxSize);
//		System.out.println("bytes.length = " + bytes.length);   
//		System.out.println("screen name = " + this.getManagedBean().getSelectedUser().getLoginId());
//		System.out.println("identity no = " + this.getManagedBean().getSelectedUser().getIdentificationNo());
//	
//		IUserSO userSO = null;
//		UserReqDTO reqDTO = new UserReqDTO();
//		try {
//			PmUser pmUser = this.getManagedBean().getSelectedUser();
//			reqDTO.setPmUser(pmUser);
//			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//			UserResDTO resDTO = userSO.updatePortrait(reqDTO); //saveuser2
//			if(resDTO!=null && resDTO.isSuccessful()){
//				FacesContext.getCurrentInstance().addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO,
//								null, "Delete Portrait Successfull for Login ID: "
//										+ resDTO.getPmUser().getLoginId()));
//				
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			FacesContext.getCurrentInstance().addMessage(
//					null,
//					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
//							e.getMessage()));
//		} finally {
//			userSO = null;
//		}
//		logger.info("handleFileUpload - END");
//	}
//	
	
	
	
	public void dummyAddressSample(){
		PmAddress pmAdress =this.getManagedBean().getUserAddress();
		
		System.out.println("dummyAddress");
		pmAdress.setAddress1("add1");
		pmAdress.setAddress2("add2");
		pmAdress.setAddress3("add3");
		this.getManagedBean().getSelectedUser().setEmail(this.getManagedBean().getSelectedUser().getUserName() + "@test.com");
		pmAdress.setFaxNo("1234567890");
		pmAdress.setMobileNo("1234567890");
		pmAdress.setPhoneNo("1234567890");
		pmAdress.getPmState().setStateId(54L);
		pmAdress.getPmDistrict().setDistrictId(402L);
		pmAdress.getPmCity().setCityId(1869L);
		pmAdress.setPmDivision(null);
		pmAdress.setPostcode("12345");
		this.getManagedBean().setUserAddress(pmAdress);
		
	}

//
//
//	// to create new profiles into database
//	public void createNewProfiles(PmUser user) {
//
//		IUserSO userSO;
//		user.getOrgTypeCode().setParameterId(166L); //TODO remove
//		try {
//			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//			UserReqDTO reqDTO = new UserReqDTO();
//			reqDTO.setPmUser(this.getManagedBean().getUser());
//
//			UserResDTO resDTO = userSO.saveMyProfile(reqDTO);
//
//			if (resDTO.isSuccessful()) {
//				FacesContext.getCurrentInstance().addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO,
//								"Record save successfully", "User ID: "
//										+ resDTO.getPmUser().getUserId()));
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
//	}
/*
	// to add roles names datatables
	public void addRolesNames() {

		// getManagedBean().setSelectedRolesList(new PmRoleDesc());

		// getManagedBean().getSelectedRolesList().setPmRole(
		// getManagedBean().getRoles());

	}


	
	public void addSelectedRole() {
		debugLogger("addSelectedRole");
		List<PmRoleDesc> roledescList = this.getManagedBean()
				.getSelectedUserRole();
		if (roledescList == null) {
			roledescList = new ArrayList<PmRoleDesc>();
		}
		PmRoleDesc roledesc = new PmRoleDesc();
		this.getManagedBean().setSelectUserRole(roledesc);
		getManagedBean().setSelectedUserRole(roledescList);
	}	
	
	public void acceptRoleListAction() {
		debugLogger("acceptRoleListAction");
		
		List<PmRoleDesc> roleList = getManagedBean()
				.getSelectedUserRole();
	
		if (null == roleList) {
			roleList = new ArrayList<PmRoleDesc>();
		}
		

			PmRoleDesc name = new PmRoleDesc();
			name.setRoleName(getManagedBean().getSelectUserRole()
					.getRoleName());

			roleList.add(name);
		
	}
	
	public void addRecevingAddress() {
		debugLogger("addRecevingAddress");
		List<PmAddressType> addressTypeList = this.getManagedBean()
				.getPmRecevingAddressList();
		if (addressTypeList == null) {
			addressTypeList = new ArrayList<PmAddressType>();
		}
		PmAddressType addressname = new PmAddressType();
		this.getManagedBean().setSelectRecevingAddress(addressname);
		getManagedBean().setPmRecevingAddressList(addressTypeList);
	}	
	
*/
	
	
	
	
	
	
	
	

	

	
			
		
//			System.out.println("handleFileUploadTest - start");
//			UploadedFile item = event.getFile();
//
//			System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
//			System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());
//
//			this.getManagedBean().getSelectedUser().setPortrait(item.getContents());
//			
//			System.out.println("login ID: " + this.getManagedBean().getSelectedUser().getLoginId());
//			System.out.println("username: " + this.getManagedBean().getSelectedUser().getUserName());
//			IUserSO userSO = null;
//			try {
//				userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
//						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
//				UserReqDTO reqDTO = new UserReqDTO();
//				reqDTO.setPmUser(this.getManagedBean().getSelectedUser());
//
//				UserResDTO resDTO = userSO.updatePortrait(reqDTO);
//
//				if (resDTO.isSuccessful()) {
//					FacesContext.getCurrentInstance().addMessage(
//							null,
//							new FacesMessage(FacesMessage.SEVERITY_INFO,
//									null, "Record save successfully for Login ID: "
//											+ resDTO.getPmUser().getLoginId()));
//					this.preLoadingUserProfile();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				FacesContext.getCurrentInstance().addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
//								e.getMessage()));
//			} finally {
//				userSO = null;
//			}
//			
//			FacesMessage msg = new FacesMessage("Succesful", event.getFile()
//					.getFileName() + " is uploaded.");
//			FacesContext.getCurrentInstance().addMessage(null, msg);
//			
//			System.out.println("handleFileUploadTest - end");
//		}
	
	
	
//	private List<PmRoleDesc> getChildRoles(String parentCode){
//		List<PmRoleDesc> childList = new ArrayList<PmRoleDesc>();
//		try{
//			if(parentCode!=null){
//				RoleReqDTO roleReqDTO = new RoleReqDTO();
//				roleReqDTO.setParentRole(parentCode);
//				roleReqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
//				IRoleSO roleSO = BusinessDelegate.getInstance().getServiceObject(
//						ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
//				RoleResDTO resDto = roleSO.searchRoleNameList(roleReqDTO);
//				if(resDto!=null && resDto.isSuccessful()){
//					childList = resDto.getRoleList();
//				}
//				
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return childList;
//	}
//	

	/*
	private void dummyUser(PmUser pmUser){
		System.out.println("dummyUser");
		
		//dummy a role here
		if(!pmUser.isBrandNew()){
			System.out.println("dummy user not brand new ");
			if(pmUser.getPmUserOrgs()==null){
				System.out.println("dummy user empty pmUserOrgs");
				pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());
				//pmUserOrg
				PmUserOrg pmUserOrg = new PmUserOrg();
				
				
				//pmUserOrg.pmOrgProfile
				PmOrgProfile pmOrgProfile = new PmOrgProfile();
				pmOrgProfile.setOrgProfileId(1L);
				
				//pmOrgProfile.orgTypeCode
				PmParameter pmParameter = new PmParameter();
				pmParameter.setParameterId(166L);
				pmOrgProfile.setOrgTypeCode(pmParameter);
				pmOrgProfile.setBrandNew(false);
				pmUserOrg.setPmOrgProfile(pmOrgProfile);
				
				//pmUserOrg.pmUserRole.pmRole
				PmRole pmRole = new PmRole();
				pmRole.setRoleCode("BUDGET_APPROVER");
				
				PmRole pmRole2 = new PmRole();
				pmRole2.setRoleCode("CD_OFFICER");
				
				//pmUserOrg.pmUserRole
				PmUserRole pmUserRole = new PmUserRole();
				pmUserRole.setPmRole(pmRole);
				
				PmUserRole pmUserRole2 = new PmUserRole();
				pmUserRole2.setPmRole(pmRole2);
				
				pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
				
				pmUserOrg.addPmUserRole(pmUserRole);
				pmUserOrg.addPmUserRole(pmUserRole2);
				
				pmUser.addPmUserOrg(pmUserOrg);
				
				
			}else{
				//ADD
				PmUserOrg tempUserOrg = pmUser.getPmUserOrgs().get(0);
				PmUserRole pmUserRole = new PmUserRole();
				//pmUserOrg.pmUserRole.pmRole
				PmRole pmRole = new PmRole();
				pmRole.setRoleCode("CD_COMMITTEE");
				pmUserRole.setPmRole(pmRole);
				tempUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
				tempUserOrg.addPmUserRole(pmUserRole);
				
			}
			
		}else{
			pmUser.getSalutationCode().setSalutationId(454L);
			pmUser.getIdentificationCode().setParameterId(70L);
			pmUser.setLoginId(pmUser.getIdentificationNo());
			pmUser.setDesignation("test");
			pmUser.setGrade("test");
			pmUser.setFaxNo("1234567890");
			pmUser.setMobileNo("1234567890");
			pmUser.setPhoneNo("1234567890");
			pmUser.setEmail(pmUser.getUserName() + "@test.com");
			pmUser.getOrgTypeCode().setParameterId(166L);
			
			pmUser.setPmUserOrgs(new ArrayList<PmUserOrg>());
			//pmUserOrg
			PmUserOrg pmUserOrg = new PmUserOrg();
			
			
			//pmUserOrg.pmOrgProfile
			PmOrgProfile pmOrgProfile = new PmOrgProfile();
			pmOrgProfile.setOrgProfileId(1L);
			
			//pmOrgProfile.orgTypeCode
			PmParameter pmParameter = new PmParameter();
			pmParameter.setParameterId(166L);
			pmOrgProfile.setOrgTypeCode(pmParameter);
			pmOrgProfile.setBrandNew(false);
			pmUserOrg.setPmOrgProfile(pmOrgProfile);
			pmUserOrg.setEffDate(new Timestamp(System.currentTimeMillis()));
			//pmUserOrg.pmUserRole.pmRole
			PmRole pmRole1 = new PmRole();
			pmRole1.setRoleCode("BUDGET_APPROVER");
			
			PmRole pmRole2 = new PmRole();
			pmRole2.setRoleCode("CD_OFFICER");
			
			//pmUserOrg.pmUserRole
			PmUserRole pmUserRole1 = new PmUserRole();
			pmUserRole1.setPmRole(pmRole1);
			
			PmUserRole pmUserRole2 = new PmUserRole();
			pmUserRole2.setPmRole(pmRole2);
			
			pmUserOrg.setPmUserRoles(new ArrayList<PmUserRole>());
			
			pmUserOrg.addPmUserRole(pmUserRole1);
			pmUserOrg.addPmUserRole(pmUserRole2);
			
			pmUser.addPmUserOrg(pmUserOrg);
			
		}
		
	
	}

	
	private void dummyTestUpdateRole(UserReqDTO reqDTO){
		//ADD
		List <PmUserRole> newUserRoles = new ArrayList<PmUserRole>();
		PmUserRole pmUserRole1 = new PmUserRole();
		PmRole pmRole1 = new PmRole();
		pmRole1.setRoleCode("CD_COMMITTEE");
		pmUserRole1.setPmRole(pmRole1);
		newUserRoles.add(pmUserRole1);
		
		List <PmUserRole> delUserRoles = new ArrayList<PmUserRole>();
		PmUserRole pmUserRole2 = new PmUserRole();
		PmRole pmRole2 = new PmRole();
		pmRole2.setRoleCode("BUDGET_APPROVER");
		pmUserRole2.setPmRole(pmRole2);
		delUserRoles.add(pmUserRole2);

		reqDTO.setNewRoleList(newUserRoles);
		reqDTO.setRemoveRoleList(delUserRoles);
	}
	*/
	
	
}
