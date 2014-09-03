package my.ep.web.util.pm.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.language.ILanguageSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.salutation.ISalutationSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.userattachment.IUserAttachmentSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.dto.pm.common.salutation.SalutationResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentReqDTO;
import my.ep.dto.pm.common.userattachment.UserAttachmentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserAttachment;
import my.ep.persistence.entity.pm.authorization.PmUserNotification;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserPreference;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmLanguage;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
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
	 */
	
	public static void generateDefaultPmUserNotifications(PmUser pmUser, String languageCode){
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterType(ParameterTypeConst.NOTIFICATION_TYPE);
		parameterReqDTO.setLanguageCode(languageCode);
		ParameterResDTO parameterResDTO = parameterSO.retrieveParameterDescListByType(parameterReqDTO);
		List <PmParameterDesc> pmParameterDescs = parameterResDTO.getParameterDescList();
		
		//this is to sync back the notification list
		if(pmUser.getPmUserNotifications()!=null){
			//Temp remove SMS from UI - Start
			PmUserNotification deleteUserNotification = null;
			for(PmUserNotification pmUserNotification:pmUser.getPmUserNotifications()){
				if(ParameterConst.NOTIFICATION_SMS.equals(pmUserNotification.getNotifyMode())){
					deleteUserNotification = pmUserNotification;
					break;
				}
			}
			if(deleteUserNotification!=null){
				pmUser.getPmUserNotifications().remove(deleteUserNotification);
			}
			//Temp remove SMS from UI - End
			if(pmParameterDescs!=null && (pmParameterDescs.size() != pmUser.getPmUserNotifications().size())){
				for(PmParameterDesc pmParameterDesc: pmParameterDescs){
					if(!ParameterConst.NOTIFICATION_SMS.equals(pmParameterDesc.getPmParameter().getParameterCode())){ 
						boolean isRequired = true;
						for(PmUserNotification pmUserNotification:pmUser.getPmUserNotifications()){
							if(pmUserNotification.getNotifyMode().equals(pmParameterDesc.getPmParameter().getParameterCode())){
								isRequired = false;
								break;
							}
						}
						if(isRequired){
							PmUserNotification notification = new PmUserNotification();
							notification.setPreferred(ParameterConst.NOTIFICATION_SMS.equals(pmParameterDesc.getPmParameter().getParameterCode())? false : true);
							notification.setNotifyMode(pmParameterDesc.getPmParameter().getParameterCode());	
							pmUser.addPmUserNotifications(notification);
						}
					}
				}
			}
		}else{
			pmUser.setPmUserNotifications(new ArrayList<PmUserNotification>());
			for(PmParameterDesc pmParameterDesc: pmParameterDescs){
				if(!ParameterConst.NOTIFICATION_SMS.equals(pmParameterDesc.getPmParameter().getParameterCode())){ 
					PmUserNotification notification = new PmUserNotification();
					notification.setPreferred(ParameterConst.NOTIFICATION_SMS.equals(pmParameterDesc.getPmParameter().getParameterCode())? false : true);
					notification.setNotifyMode(pmParameterDesc.getPmParameter().getParameterCode());	
					pmUser.addPmUserNotifications(notification);
				}
			}
		}
	}
	
	/**
	 * set default notification mode and preferences
	 * @param pmUser
	 */
	public static void loadPmUserPreference(PmUser pmUser, PmUserPreference pmUserPreference) {
		IConfigGeneralSO configGeneralSO = (IConfigGeneralSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IConfigGeneralSO.class);
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
		try {
			ILanguageSO languageSO = (ILanguageSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ILanguageSO.class);
			LanguageResDTO languagecResDTO = languageSO.retrieveLanguages();
			languages = languagecResDTO.getLanguageList();
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770057);
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
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		return resDTO.getCountryList();
	}

	/**
	 * retrieve state list from Pm State by country id 
	 * @param countryId
	 * @return List<PmState>
	 */
	public static  List<PmState> retrievePmStateList(Long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}
	
	/**
	 * retrieve division list from Pm Division by state id 
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public static  List<PmDivision> retrievePmDivisionList(Long stateId) {
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setStateId(stateId);
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDivisionSO.class);
		DivisionResDTO resDTO = reqSO.retriveDivisionListByState(reqDTO);
		return resDTO.getDivisionList();
	}
	
	/**
	 * retrieve district list from Pm District by state id 
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public static  List<PmDistrict> retrievePmDistrictList(Long stateId) {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
//		reqDTO.setDivisionId(divisionId);//TODO
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		return resDTO.getDistrictList();
	}

	
	/**
	 * retrieve city list from Pm City by district id
	 * @param districtId
	 * @return List<PmCity>
	 */
	public static  List<PmCity> retrievePmCityList(Long districtId) {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		return resDTO.getCityList();
	}
	
	/**
	 * retrieve state by state id
	 * @param stateId
	 * @return PmState
	 */
	public static PmState retrievePmStateByStateId(Long stateId){
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStateByID(reqDTO);
		return resDTO.getPmState();
	}
	
	/**
	 * retrieve all salutation from Pm Salutation
	 * @return List<PmSalutation>
	 */
	public static List<PmSalutation> retrievePmSalutationList() {
		ISalutationSO reqSO = (ISalutationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ISalutationSO.class);
		SalutationResDTO resDTO = reqSO.retrieveSalutationList();
		return resDTO.getSalutationList();
	}

	/**
	 * retrieve filtered org type list
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public static List<PmParameterDesc> retrieveFilteredOrgTypes(String paramType) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_NOT_IN.value());
		bean.setSearchField("pmParameter.parameterCode");
		bean.setSearchValue(new ArrayList(Arrays.asList(ProfileManagementConst.ORG_TYPE_EXCLUDE)));
		searchBean.getDataFilterBeans().add(bean);
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(paramType, searchBean);
	}
	
	/**
	 * retrieve pmUser by id
	 * @param id
	 * @return PmUser
	 */
	public static PmUser retrieveUserById(Long id, boolean ignoreStatus){
		PmUser pmUser = new PmUser();
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
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
		}		
		return pmUser;
	}

	/**
	 * retrieve active PmUserOrgs
	 * @param id
	 * @return PmUser
	 */
	public static List<PmUserOrg> retrieveActiveOrg(Long id){
		List <PmUserOrg> pmUserOrgs = new ArrayList<PmUserOrg>();
		try {
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = null;
			reqDTO.setUserId(id);
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if(resDTO.isSuccessful()){
				pmUserOrgs = resDTO.getUserOrgList();
			}
				
		} catch (Exception e) {
			throw new EpException(MessageConst.ERROR_770058 , new String[]{id.toString(), e.getMessage()}); 
		}		
		return pmUserOrgs;
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
		UserAttachmentReqDTO reqDTO = null;
		IUserAttachmentSO reqSO = null;
		UserAttachmentResDTO resDTO = null;
		if(pmUserAttachments!=null && !pmUserAttachments.isEmpty()){
			reqDTO = new UserAttachmentReqDTO();
			reqDTO.setPmUserAttachments(pmUserAttachments);
			reqSO = (IUserAttachmentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserAttachmentSO.class);
			resDTO = reqSO.deleteUserAttachment(reqDTO);
		}
		return resDTO!=null? resDTO.isSuccessful() : false;
	}

}
