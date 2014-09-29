package my.ep.web.util.pm.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.language.ILanguageSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.salutation.ISalutationSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.userattachment.IUserAttachmentSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralReqDTO;
import my.ep.dto.pm.common.confgeneral.ConfigGeneralResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.language.LanguageResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.salutation.SalutationReqDTO;
import my.ep.dto.pm.common.salutation.SalutationResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentReqDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.aut.SearchOperator;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

public class PmCommonUtil {
	private static Logger log = Logger.getLogger(PmCommonUtil.class);
	private static final String FILE_DATETIME_FORMAT = "ddMMyyyyHHmmss";
	
	/**
	 * initialize a complete Pm Address
	 */
	public static PmAddress getNewPmAddress(){
		PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		PmAddress pmAddress = new PmAddress();
		pmAddress.setPmCountry(defaultCountry!=null ? defaultCountry : new PmCountry());
		pmAddress.setPmState(new PmState());
		pmAddress.setPmDistrict(new PmDistrict());
		pmAddress.setPmCity(new PmCity());
		pmAddress.setPmDivision(new PmDivision());
		return pmAddress;
	}
	
	/**
	 * initialize a default notifications
	 * @param pmUser
	 * @return List <PmUserNotification>
	 */
	
	public static List<PmUserNotification> generateDefaultPmUserNotifications(PmUser pmUser, String languageCode){
		IParameterSO parameterSO = null;
		List <PmUserNotification> displayNotifications = new ArrayList<PmUserNotification>();
		try{
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			SearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmParameter.parameterCode",
					SearchOperator.OPERATOR_NOT_EQUALS.value(),
					ParameterConst.NOTIFICATION_SMS));
			
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(ParameterTypeConst.NOTIFICATION_TYPE);
			parameterReqDTO.setLanguageCode(languageCode);
			
			parameterReqDTO.setParameterDescSearchBean(searchBean);
			
			ParameterResDTO parameterResDTO = parameterSO.retrieveParameterDescListByType(parameterReqDTO);
			List <PmParameterDesc> pmParameterDescs = parameterResDTO.getParameterDescList();
			
			
			//add into pmUser.getPmUserNotifications()
			//this is to sync back the notification list
			if(pmUser.getPmUserNotifications()!=null && !pmUser.getPmUserNotifications().isEmpty()){
				if(pmParameterDescs!=null && (pmParameterDescs.size() != pmUser.getPmUserNotifications().size())){
					for(PmParameterDesc pmParameterDesc: pmParameterDescs){
						boolean isRequired = true;
						for(PmUserNotification pmUserNotification:pmUser.getPmUserNotifications()){
							if(pmUserNotification.getNotifyMode().equals(pmParameterDesc.getPmParameter().getParameterCode())){
								isRequired = false;
								break;
							}
						}
						if(isRequired){
							PmUserNotification notification = new PmUserNotification();
							notification.setPreferred(true);
							notification.setNotifyMode(pmParameterDesc.getPmParameter().getParameterCode());	
							pmUser.addPmUserNotifications(notification);
						}
					}
				}
			}else{
				pmUser.setPmUserNotifications(new ArrayList<PmUserNotification>());
				for(PmParameterDesc pmParameterDesc: pmParameterDescs){
					PmUserNotification notification = new PmUserNotification();
					notification.setPreferred(true);
					notification.setNotifyMode(pmParameterDesc.getPmParameter().getParameterCode());	
					pmUser.addPmUserNotifications(notification);
				}
			}
			
			//generate displayNotification
			
			for(PmUserNotification pmUserNotification:pmUser.getPmUserNotifications()){
				if(!ParameterConst.NOTIFICATION_DASHBOARD.equals(pmUserNotification.getNotifyMode())
						&& !ParameterConst.NOTIFICATION_SMS.equals(pmUserNotification.getNotifyMode())){
					displayNotifications.add(pmUserNotification);
				}
			}
		}finally{
			parameterSO = null;
		}
		return displayNotifications;
	}
	
	/**
	 * set default notification mode and preferences
	 * @param pmUser
	 */
	public static void loadPmUserPreference(PmUser pmUser, PmUserPreference pmUserPreference) {
		IConfigGeneralSO configGeneralSO =  null;
		try{
			configGeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IConfigGeneralSO.class);
			ConfigGeneralReqDTO configGeneralReqDTO = new ConfigGeneralReqDTO();
			ConfigGeneralResDTO configGeneralResDTO = configGeneralSO.getConfigGeneral(configGeneralReqDTO);
			PmConfigGeneral configGeneral = null;
			if(configGeneralResDTO!=null && configGeneralResDTO.isSuccessful()){
				configGeneral = configGeneralResDTO.getPmConfigGeneral();
			}
			if(configGeneral==null){
				throw new EpException(MessageConst.ERROR_770052);
			}
			if(pmUserPreference == null){
				pmUserPreference= new PmUserPreference();
				pmUserPreference.setPreferredLanguage(configGeneral.getPreferredLanguage());
				pmUserPreference.setNotifyRemovalDay(configGeneral.getNotifyFreq());
				pmUserPreference.setFormatDate(configGeneral.getFormatdate());
				pmUserPreference.setFormatNumber(configGeneral.getFormatNumber());
				pmUserPreference.setFormatTime(configGeneral.getFormatTime());
				pmUserPreference.setPmUser(pmUser);
				pmUser.setPmUserPreference(pmUserPreference);
			}else{
				pmUserPreference.setFormatDate(configGeneral.getFormatdate());
				pmUserPreference.setFormatNumber(configGeneral.getFormatNumber());
				pmUserPreference.setFormatTime(configGeneral.getFormatTime());
				pmUserPreference.setPmUser(pmUser);
			}
		}finally{
			configGeneralSO = null;
		}
	}
	
	/**
	 * get YES & NO options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getYesNoOptions() {
		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(1, "Yes");
		items[1] = new SelectItem(0, "No");
	    return items;
	}
	
	/**
	 * retrieve all languages from pm
	 * @param paramType
	 * @return
	 */
	public static List <PmLanguage> retrieveLanguageObjs() {
		log.info("retrieveLanguageObjs");
		List<PmLanguage> languages = new ArrayList<PmLanguage>();
		ILanguageSO languageSO = null;
		try {
			languageSO = (ILanguageSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ILanguageSO.class);
			LanguageResDTO languagecResDTO = languageSO.retrieveLanguages();
			languages = languagecResDTO.getLanguageList();
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770057);
		}finally{
			languageSO = null;
		}
		return languages;
	}

	/**
	 * get YES & NO options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getYesNoBooleanOptions() {
		SelectItem[] items =  new SelectItem[2];
		items[0] = new SelectItem(true, "Yes");
		items[1] = new SelectItem(false, "No");
	    return items;
	}
	
	
	
	/**
	 * get record status options //TODO: to load dynamically
	 * @return SelectItem[] 
	 */
	public static SelectItem[] getStatusOptions() {
		SelectItem[] items =  new SelectItem[3];
		items[0] = new SelectItem(null, "- Select One -");
		items[1] = new SelectItem(1, "Active");
		items[2] = new SelectItem(0, "Inactive");
	    return items;
	}
	
	/**
	 * retrieve the default PmCountry as per configured
	 * @param countryCode
	 * @return PmCountry
	 */
	public static PmCountry retrieveCountryByCountryCode(String countryCode){
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode(countryCode);
		
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		
		return resDTO.getPmCountry();
	}
	
	
	/**
	 * retrieve all countries from Pm Country
	 * @return List<PmCountry>
	 */
	public static List<PmCountry> retrievePmCountryList() {
		ICountrySO reqSO = null;
		List<PmCountry> resultList = null;
		try{
			reqSO = (ICountrySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryResDTO resDTO = reqSO.retrieveCountryList();
			resultList = resDTO.getCountryList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve state list from Pm State by country id 
	 * @param countryId
	 * @return List<PmState>
	 */
	public static List<PmState> retrievePmStateList(Long countryId) {
		IStateSO reqSO = null;
		List<PmState> resultList = null;
		try{
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			resultList = resDTO.getStateList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}
	
	/**
	 * retrieve division list from Pm Division by state id 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public static  List<PmDivision> retrievePmDivisionList(Long stateId) {
		IDivisionSO reqSO = null;
		List<PmDivision> resultList = null;
		try{
			DivisionReqDTO reqDTO = new DivisionReqDTO();
			reqDTO.setStateId(stateId);
			reqSO = (IDivisionSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
			DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
			resultList = resDTO.getDivisionList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}
	
	/**
	 * retrieve district list from Pm District by state id 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public static List<PmDistrict> retrievePmDistrictList(Long stateId) {
		IDistrictSO reqSO = null;
		List<PmDistrict> resultList = null;
		try{
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			 reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			resultList = resDTO.getDistrictList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}

	
	/**
	 * retrieve city list from Pm City by district id
	 * @param districtId
	 * @return List<PmCity>
	 */
	public static List<PmCity> retrievePmCityList(Long districtId) {
		ICitySO reqSO = null;
		List<PmCity> resultList = null;
		try{
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			reqSO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			resultList = resDTO.getCityList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}
	
	/**
	 * retrieve state by state id
	 * @param stateId
	 * @return PmState
	 */
	public static PmState retrievePmStateByStateId(Long stateId){
		IStateSO reqSO = null;
		PmState pmState = null;
		try{
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setStateId(stateId);
			 reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
			pmState = resDTO.getPmState();
		}finally{
			reqSO = null;
		}
		return pmState;
	}
	
	/**
	 * retrieve all salutation from Pm Salutation
	 * @return List<PmSalutation>
	 */
	public static List<PmSalutation> retrievePmSalutationList() {
		ISalutationSO reqSO = null;
		List<PmSalutation> resultList = null;
		try{
			SalutationReqDTO reqDTO = new SalutationReqDTO();
			reqSO = (ISalutationSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ISalutationSO.class);
			ISearchBean<PmSalutation> searchBean = new SearchBean<PmSalutation>();
			
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			reqDTO.setSalutationSearchBean(searchBean);
			SalutationResDTO resDTO = reqSO.retrieveSalutationBySearch(reqDTO);
			resultList = resDTO.getSalutationList();
		}finally{
			reqSO = null;
		}
		return resultList;
	}

	/**
	 * retrieve filtered org type list
	 * @param paramType
	 * @param notInOrg
	 * @param inOrg
	 * @returnList<PmParameterDesc>
	 */
	public static List<PmParameterDesc> retrieveFilteredOrgTypes(String paramType, List<String>notInOrg, List<String>inOrg) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		if(notInOrg!=null && !notInOrg.isEmpty()){
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(notInOrg);
			searchBean.getDataFilterBeans().add(bean);
		}
		if(inOrg!=null && !inOrg.isEmpty()){
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmParameter.parameterCode");
			bean.setSearchValue(inOrg);
			searchBean.getDataFilterBeans().add(bean);
		}
		
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(paramType, searchBean);
	}
	
	/**
	 * retrieve pmUser by id
	 * @param id
	 * @return PmUser
	 */
	public static PmUser retrieveUserById(Long id, boolean ignoreStatus){
		PmUser pmUser = new PmUser();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			if(ignoreStatus){
				resDTO = userSO.retrieveUserByLoginIdOrUserIdIgnoreStatus(reqDTO);
			}else{
				resDTO = userSO.retrieveUserProfileByUserId(reqDTO);
			}
			
			if(resDTO.isSuccessful()){
				pmUser = resDTO.getPmUser();
			}
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770053, new String[]{id.toString(), e.getMessage()});
		}finally{
			userSO = null;
		}		
		return pmUser;
	}

	/**
	 * retrieve active PmUserOrgs
	 * @param id
	 * @return List<PmUserOrg> 
	 */
	public static List<PmUserOrg> retrieveActiveOrg(Long id){
		List <PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		IUserSO userSO = null;
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if(resDTO.isSuccessful()){
				pmUserOrgs = resDTO.getUserOrgList();
			}
				
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770058 , new String[]{id.toString(), e.getMessage()}); 
		}finally{
			userSO = null;
		}		
		return pmUserOrgs;
	}
	

	/**
	 * retrieve address type list by user id
	 * @param userId
	 * @return List<PmAddressType>
	 */
	public static List<PmAddressType> retrieveAddressTypesByUserIdOrAddressType(Long userId, String addressType){
		List <PmAddressType> pmAddressTypes = new ArrayList<PmAddressType>();
		IAddressSO addressSO = null;
		try {
			AddressReqDTO reqDTO = new AddressReqDTO();
			AddressResDTO resDTO = null;
			if(userId!=null){
				reqDTO.setUserId(userId);
			}
			if(addressType!=null){
				reqDTO.setAddressType(addressType);
			}
			addressSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
			resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			if(resDTO.isSuccessful()){
				pmAddressTypes = resDTO.getAddressTypeList();
			}
				
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770060 , new String[]{userId.toString(), e.getMessage()}); 
			
		}finally{
			addressSO = null;
		}	
		return pmAddressTypes;
	}
	
	/**
	 * Get full attachment upload path
	 * @return String
	 */
	public static String getUploadPath(String fileName){
		String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		StringBuilder filePath = new StringBuilder(docRepoBaseDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(DateUtils.getLast2DigitOfYear());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.PROFILE_MANAGEMENT.toUpperCase());
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(DateUtils.dateToString(FILE_DATETIME_FORMAT, DateUtils.getCurrentDate()));
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(fileName);
		return filePath.toString();
	}

	/**
	 * Get attachment upload path
	 * @return String
	 */
	public static String getAttachmentPath(String fullPath){
		String path = "";
		if(!StringUtils.isBlank(fullPath)){
			int index = fullPath.lastIndexOf("/");
			path = fullPath.substring(0, index);
		}
		return path;
	}

	
	public static void commonUploadFile(FileUploadEvent event, String path){
		File dir = new File(path);
		log.info(" dir.exists()"+ dir.exists());
		  
		if(dir.exists()){}
		else{dir.mkdirs();}
		
		String uploadDir = "";
		
		UploadedFile file = event.getFile();
		
		uploadDir = path + CommonConstants.DOC_REPO_PATH_SEPARATOR + file.getFileName();
		
		genFile(uploadDir, file.getContents());

	}
	
	
	public static void genFile(String uploadDir, byte[] content){


		try {	
			
			BufferedOutputStream bufferedOutputStream =new BufferedOutputStream(new FileOutputStream(uploadDir));

			bufferedOutputStream.write(content);

			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}



		}catch(Exception e) {
			log.error(e);
		}


	}
	
	/**
	 * Retrieve user name
	 * @param userId
	 * @return
	 */
	public static String retriveUserName(Long userId){
		IUserSO userSo=null;
		String userName="";
		try{
			if(userId!=null){
				userSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTo=new UserReqDTO();
				reqDTo.setUserId(userId);
				UserResDTO resDTO=userSo.retrieveUserByUserId(reqDTo);
				if(resDTO.isSuccessful()==true){
					userName=resDTO.getPmUser()!=null ? resDTO.getPmUser().getUserName(): "";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			userSo = null;
		}
		
		return userName;
	}
	
	public static void deleteUploadFile(String path){
		File dir = new File(path);
		System.out.println(path);
		log.info(" dir.exists()"+ dir.exists());
		if(dir.exists()){
			dir.delete();
		}else{
			throw new EpException(MessageConst.ERROR_770056);
		}
	}
	

/**
	 * Delete attachment status
	 * @return boolean
	 */
	public static boolean deleteUserAttachment(List<PmUserAttachment> pmUserAttachments) {
		boolean isSuccess = false;
		UserAttachmentReqDTO reqDTO = null;
		IUserAttachmentSO reqSO = null;
		UserAttachmentResDTO resDTO = null;
		try{
			if(pmUserAttachments!=null && !pmUserAttachments.isEmpty()){
				reqDTO = new UserAttachmentReqDTO();
				reqDTO.setPmUserAttachments(pmUserAttachments);
				reqSO = (IUserAttachmentSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserAttachmentSO.class);
				resDTO = reqSO.deleteUserAttachment(reqDTO);
			}
			isSuccess = resDTO!=null? resDTO.isSuccessful() : false;
		}finally{
			reqSO = null;
		}
		return isSuccess;
	}
	

	/**
	 * retrieve parameter obj
	 * @param pmParameter
	 */
	
	public static PmParameter retrievePmParameter(String paramType, String paramCode){
		IParameterSO parameterSO = null;
		PmParameter pmParameter = null;
		try{
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterType(paramType);
			parameterReqDTO.setParameterCode(paramCode);
			ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
			pmParameter = parameterResDTO.getPmParameter();
		
		}finally{
			parameterSO = null;
		}
		return pmParameter;
	}
	
	/**
	 * retrieve org profile
	 * @param id
	 * @return PmOrgProfile
	 */
	public static PmOrgProfile retrievePmOrgProfile(Long id){
		
		PmOrgProfile pmOrgProfile = new PmOrgProfile();
		IOrgProfileSO orgProfileSO   = null;
		try{
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			OrgProfileResDTO resDTO = null;
			reqDTO.setUserId(id);
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			resDTO = orgProfileSO.retrieveOrgProfileDtlByProfileId(reqDTO);
			if(resDTO.isSuccessful()){
				pmOrgProfile = resDTO.getPmOrgProfile();
			}
		}finally{
			orgProfileSO = null;
		}	
			
		return pmOrgProfile;
	}
	
	/**
	 * Generate current date for LOV date format
	 * @param date
	 * @return String
	 */
	public static String getCurrentDateStr(Date date){
		if(date!=null){
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		}else{
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		}
		
	}
	

	/**
	 * retrieve status by status id
	 * @param statusId
	 * @return PmStatus
	 */
	public static PmStatus getPmStatusById(Long statusId){
		PmStatus pmStatus = new PmStatus();
		StatusReqDTO reqDTO = new StatusReqDTO();
		IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
		reqDTO.setStatusId(statusId);
		StatusResDTO resDTO = statusSO.retrieveStatusByStatusId(reqDTO);
		if(resDTO.isSuccessful()){
			pmStatus = resDTO.getPmStatus();
		}
		return pmStatus;
	}

	/**
	 * Retrieve user name
	 * @param userIds
	 * @return List<PmUser>
	 */
	public static List<PmUser> retriveUsersByIds(List<Long> userIds){
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		
		try{
			if(userIds!=null && !userIds.isEmpty()){
				ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();

				// active user
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchField("recordStatusFlg");
				bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
				searchBean.getDataFilterBeans().add(bean);
							
				bean = new SearchFilterBean();
				bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
				bean.setSearchField("userId");
				bean.setSearchValue(userIds);
				searchBean.getDataFilterBeans().add(bean);
				
				pmUsers = searchUsersBySearchBean(searchBean);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return pmUsers;
	}
	/**
	 * search user by search bean
	 * @param userIds
	 * @return List<PmUser>
	 */
	public static List<PmUser> searchUsersBySearchBean(ISearchBean<PmUser> searchBean){
		List<PmUser> pmUsers = new ArrayList<PmUser>();
		IUserSO userSO  = null;
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			reqDTO.setUserSearchBean(searchBean);
			userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.searchUserProfile(reqDTO);
			
			if(resDTO.isSuccessful() && resDTO.getUserResultBean()!=null && resDTO.getUserResultBean().getResultList()!=null){
				pmUsers = resDTO.getUserResultBean().getResultList();
			}
		}finally{
			userSO = null;
		}
		return pmUsers;
	}
	
	
}
