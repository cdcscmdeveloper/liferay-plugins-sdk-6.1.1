package my.ep.web.backingbean.sm.userprofilemaintenance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="smPersonnelBackingBean")
@ViewScoped
public class SmPersonnelBackingBean extends BaseBackingBean<SmPersonnelManagedBean>{

	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);
	
	public SmPersonnelBackingBean(){
		System.out.println("********************SmPersonnelBackingBean Constructor***************");
	}

	public void preRenderSmPersonnel(){
		System.out.println("********************SmPersonnelBackingBean PreRender***************");
		initUserPersonnelInfo();
//		initializeUpdateLists();
		
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "smUserManagementHome");
	}
	
	public void resetSmPersonnelSearch() {
		System.out.println("********************SmPersonnelBackingBean resetSmPersonnelSearch***************");
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
	}
	
	public String navigateToAddNewUserPage(){  
		System.out.println("********************SmPersonnelBackingBean navigateToAddNewUserPage***************");
		removeEPRole();
		SmPersonnel smPersonnel = new SmPersonnel();
		smPersonnel.setPmCountry(new PmCountry());

		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(new PmCountry());
		mailAddress.setPmState(new PmState());
		mailAddress.setPmDistrict(new PmDistrict());
		mailAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
		mailPersonnelAddress.setAddressType("M");
		mailPersonnelAddress.setSmAddress(mailAddress);
		mailPersonnelAddress.setSmPersonnel(smPersonnel);
		
		SmAddress permAddress = new SmAddress();
		permAddress.setPmCountry(new PmCountry());
		permAddress.setPmState(new PmState());
		permAddress.setPmDistrict(new PmDistrict());
		permAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
		permPersonnelAddress.setAddressType("P");
		permPersonnelAddress.setSmAddress(permAddress);
		permPersonnelAddress.setSmPersonnel(smPersonnel);

		getManagedBean().setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
		getManagedBean().setBasicSmPersonnelPermanentAddress(permPersonnelAddress);
		getManagedBean().setBasicSmPersonnel(smPersonnel);
		getCountryList();
		return "basicSmUser";
	}

	public void initUserPersonnelInfo(){
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
	}	
	
	
	public String navigateToUpdateUserPage(String personnelId){
		System.out.println("********************SmPersonnelBackingBean navigateToUpdateUserPage***************");
		getPersonnelInfoById(personnelId);
		return "basicSmUser";
	}
	
	public String cancel(){

		return "smUserManagementHome";

	}
	
	public void confirmCreateBasicApplication(){
		
	}
	
	public void initializeLists(){

		System.out.println("********************SmPersonnelBackingBean initializeLists***************");
		getCountryList();

		if(getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses() != null && getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses().size() > 0){
			updateMailAddLists(getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmCountry().getCountryId().toString(), getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmState().getStateId().toString(), getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmDistrict().getDistrictId().toString());
			updatePermAddLists(getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().getPmCountry().getCountryId().toString(), getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().getPmState().getStateId().toString(), getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress().getPmDistrict().getDistrictId().toString());
		}
	}
	
	public void initializeUpdateLists(){

		System.out.println("********************SmPersonnelBackingBean initializeUpdateLists***************");
		getCountryList();

		if(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmCountry() != null){
			getManagedBean().setStateList(getStateList(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmCountry().getCountryId()));
		}
		
		if(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmState() != null){
			getManagedBean().setDistrictList(getDistrictList(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmState().getStateId()));
		}
		
		if(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmDistrict() != null){
			getManagedBean().setCityList(getCityList(getManagedBean().getUpdateSmPersonnelMailingAddress().getPmDistrict().getDistrictId()));
		}
	}
	
	public void getCountryList(){
		
		ICountrySO countrySO = (ICountrySO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		
		CountryResDTO resDTO = countrySO.retrieveCountryList();
		
		List<SelectItem> countryList = new ArrayList<SelectItem>();
		
		for(PmCountry country : resDTO.getCountryList()){
			SelectItem si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList.add(si);
		}
		
		getManagedBean().setCountryList(countryList);
		
		getManagedBean().setMailAddCountryList(countryList);
		
		getManagedBean().setPermAddCountryList(countryList);
		
	}
	
	public List<SelectItem> getStateList(Long countryId){

		IStateSO stateSO = (IStateSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		
		reqDTO.setCountryId(countryId);
		
		StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
		
		List<SelectItem> stateList = new ArrayList<SelectItem>();
		
		for(PmState state : resDTO.getStateList()){
			SelectItem si = new SelectItem();
			si.setLabel(state.getStateName());
			si.setValue(state.getStateId());
			stateList.add(si);
		}
		
		return stateList;
		
	}
	
	public List<SelectItem> getDistrictList(Long stateId){

		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		
		reqDTO.setStateId(stateId);
		
		DistrictResDTO resDTO = districtSO.retriveDistrictListByStateID(reqDTO);
		
		List<SelectItem> districtList = new ArrayList<SelectItem>();
		
		for(PmDistrict district : resDTO.getDistrictList()){
			SelectItem si = new SelectItem();
			si.setLabel(district.getDistrictName());
			si.setValue(district.getDistrictId());
			districtList.add(si);
		}
		
		return districtList;
		
	}
	
	public List<SelectItem> getCityList(Long districtId){

		ICitySO citySO = (ICitySO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		
		reqDTO.setDistrictId(districtId);
		
		CityResDTO resDTO = citySO.retriveCityListByDistrictId(reqDTO);
		
		List<SelectItem> cityList = new ArrayList<SelectItem>();
		
		for(PmCity city : resDTO.getCityList()){
			SelectItem si = new SelectItem();
			si.setLabel(city.getCityName());
			si.setValue(city.getCityId());
			cityList.add(si);
		}
		
		return cityList;
		
	}
	
	public void updateELists(String countryId, String stateId, String districtId){
		if(!countryId.isEmpty() || countryId.equalsIgnoreCase("null")){
			getManagedBean().setStateList(getStateList(Long.valueOf(countryId)));
			getManagedBean().setDistrictList(null);
			getManagedBean().setCityList(null);
			
			getManagedBean().getEditSmPersonnelMailingAddress().setPmDistrict(null);
			getManagedBean().getEditSmPersonnelMailingAddress().setPmCity(null);
		}
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")){
			getManagedBean().setDistrictList(getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setCityList(null);
			
			getManagedBean().getEditSmPersonnelMailingAddress().setPmCity(null);
		}
		if(!districtId.isEmpty() || districtId.equalsIgnoreCase("null")){
			getManagedBean().setCityList(getCityList(Long.valueOf(districtId)));
		}
	}
	
	public void updateULists(String countryId, String stateId, String districtId){
		if(!countryId.isEmpty() || countryId.equalsIgnoreCase("null")){
			getManagedBean().setStateList(getStateList(Long.valueOf(countryId)));
			getManagedBean().setDistrictList(null);
			getManagedBean().setCityList(null);
			
			getManagedBean().getUpdateSmPersonnelMailingAddress().setPmDistrict(null);
			getManagedBean().getUpdateSmPersonnelMailingAddress().setPmCity(null);
		}
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")){
			getManagedBean().setDistrictList(getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setCityList(null);
			
			getManagedBean().getUpdateSmPersonnelMailingAddress().setPmCity(null);
		}
		if(!districtId.isEmpty() || districtId.equalsIgnoreCase("null")){
			getManagedBean().setCityList(getCityList(Long.valueOf(districtId)));
		}
	}
	
	public void updateMailAddLists(String countryId, String stateId, String districtId){
		if(!countryId.isEmpty() || countryId.equalsIgnoreCase("null")){
			getManagedBean().setMailAddStateList(getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailAddDistrictList(null);
			getManagedBean().setMailAddCityList(null);
		}
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")){
			getManagedBean().setMailAddDistrictList(getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setMailAddCityList(null);
		}
		if(!districtId.isEmpty() || districtId.equalsIgnoreCase("null")){
			getManagedBean().setMailAddCityList(getCityList(Long.valueOf(districtId)));
		}
	}
	
	public void updatePermAddLists(String countryId, String stateId, String districtId){
		if(!countryId.isEmpty() || countryId.equalsIgnoreCase("null")){
			getManagedBean().setPermAddStateList(getStateList(Long.valueOf(countryId)));
			getManagedBean().setPermAddDistrictList(null);
			getManagedBean().setPermAddCityList(null);
		}
		if(!stateId.isEmpty() || stateId.equalsIgnoreCase("null")){
			getManagedBean().setPermAddDistrictList(getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setPermAddCityList(null);
		}
		if(!districtId.isEmpty() || districtId.equalsIgnoreCase("null")){
			getManagedBean().setPermAddCityList(getCityList(Long.valueOf(districtId)));
		}
	}
	
	public void getPersonnelInfoById(String personnelId){
		
		System.out.println("********************SmPersonnelBackingBean getPersonnelInfoById***************");
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
		reqDTO.setPersonnelId(Long.valueOf(personnelId));

		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelById(reqDTO);
		
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		
		this.getManagedBean().setBasicSmPersonnel(smPersonnel);
		
		List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();
		
		if(smPersonnel.getSmPersonnelAddresses() != null && smPersonnel.getSmPersonnelAddresses().size() > 0){
			
			smPersonnel.getSmPersonnelAddresses().size();
			smPAList.addAll(smPersonnel.getSmPersonnelAddresses()); 
			for(SmPersonnelAddress smPA : smPAList){
				if(smPA.getAddressType().equalsIgnoreCase("M")){
					this.getManagedBean().setBasicSmPersonnelMailingAddress(smPA);
				} else if(smPA.getAddressType().equalsIgnoreCase("P")){
					this.getManagedBean().setBasicSmPersonnelPermanentAddress(smPA);
				}
			}
			initializeLists();
		} else {
			
			SmAddress s1 = new SmAddress();
			SmAddress s2 = new SmAddress();
			
/*			Construct Mailing Address Object			*/
			ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(1L);
			CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

/*			Construct Permanent Address Object			*/
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(1L);
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s2.setPmCountry(resCountryDTO.getPmCountry());
			s2.setPmState(new PmState());
			s2.setPmDistrict(new PmDistrict());
			s2.setPmCity(new PmCity());

			List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
			SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
			permPersonnelAddress.setAddressType("P");
			permPersonnelAddress.setSmAddress(s1);
			permPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setBasicSmPersonnelPermanentAddress(permPersonnelAddress);
			SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
			mailPersonnelAddress.setAddressType("M");
			mailPersonnelAddress.setSmAddress(s1);
			mailPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelPermanentAddress());
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelMailingAddress());
			smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);
			
			getCountryList();
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(1L);
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			smPersonnel.setPmCountry(resCountryDTO.getPmCountry());
		}
		
	}
	
	
	public void getPersonnelByPmUser(PmUser usr){
		
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));

		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelByUserId(reqDTO);
		
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		
		this.getManagedBean().setUpdateSmPersonnel(smPersonnel);
		
		if(smPersonnel.getSmPersonnelAddresses() != null){
			List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();
			
			smPersonnel.getSmPersonnelAddresses().size();
			smPAList.addAll(smPersonnel.getSmPersonnelAddresses()); 
			for(SmPersonnelAddress smPA : smPAList){
				if(smPA.getAddressType().equalsIgnoreCase("M")){
					this.getManagedBean().setUpdateSmPersonnelMailingAddress(smPA.getSmAddress());
				} else if(smPA.getAddressType().equalsIgnoreCase("P")){
					this.getManagedBean().setUpdateSmPersonnelPermanentAddress(smPA.getSmAddress());
				}
			}
		}
		
	}

	
	public boolean validateUI(SmPersonnel personnel){
		
		boolean returnMsg = true;
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		
		PmCountry country;
		PmState state;
		PmDistrict district;
		PmCity city;
		
		if(personnel.getPmUser().getUserId() == usr.getUserId()){
			country = getManagedBean().getUpdateSmPersonnelMailingAddress().getPmCountry();
			state = getManagedBean().getUpdateSmPersonnelMailingAddress().getPmState();
			district = getManagedBean().getUpdateSmPersonnelMailingAddress().getPmDistrict();
			city = getManagedBean().getUpdateSmPersonnelMailingAddress().getPmCity();
		} else {
			country = getManagedBean().getEditSmPersonnelMailingAddress().getPmCountry();
			state = getManagedBean().getEditSmPersonnelMailingAddress().getPmState();
			district = getManagedBean().getEditSmPersonnelMailingAddress().getPmDistrict();
			city = getManagedBean().getEditSmPersonnelMailingAddress().getPmCity();
		}
		
		if(country == null || state == null || district == null || city == null || country.getCountryId() == null || state.getStateId() == null || district.getDistrictId() == null || city.getCityId() == null){
			returnMsg = false;
			throw new EpException(MessageConst.ERROR_722527);
		} else {
			returnMsg = true;
		}
		return returnMsg;
	}
	
	public String save(){
		
		String returnValue = null;
		if(getManagedBean().getBasicSmPersonnel().getPersonnelId() == null){
			returnValue = saveNewUser(getManagedBean().getBasicSmPersonnel());
		} else {
			returnValue = saveUser(getManagedBean().getBasicSmPersonnel());
		}
			return returnValue;
	}

	public String saveUser(SmPersonnel smPersonnel){
		validatePersonnelBeforeSave(smPersonnel);
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmPersonnel(smPersonnel);
		PersonnelResDTO resDTO = smPersonnelSO.savePersonnelRecord(reqDTO);
		System.out.println("Result:" + resDTO.isSuccessful());
		if(resDTO.isSuccessful())
			return "acknowledge.xhtml";
		else 
			return null;
	}
	
	public String saveNewUser(SmPersonnel smPersonnel){
		
			validatePersonnelBeforeSave(smPersonnel);
		
			SmAddress s1 = getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress();
			
			SmAddress s2 = getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress();
			
/*			Construct Mailing Address Object			*/

			PmCity city = s1.getPmCity();
			PmDistrict district = s1.getPmDistrict();
			PmState state = s1.getPmState();
			PmCountry country = s1.getPmCountry();

			Long cityId = city.getCityId();
			Long districtId = district.getDistrictId();
			Long stateId = state.getStateId();
			Long countryId = country.getCountryId();
			
			ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(countryId);
			CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			PmCountry newCountry = resCountryDTO.getPmCountry();
			
			s1.setPmCountry(newCountry);
			
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
			StateReqDTO reqStateDTO = new StateReqDTO();
			reqStateDTO.setStateId(stateId);
			StateResDTO resStateDTO = stateSO.retriveStateByID(reqStateDTO);
			PmState newState = resStateDTO.getPmState();
			
			s1.setPmState(newState);

			IDistrictSO districtSO = (IDistrictSO)BusinessDelegate.getInstance().getServiceObject("pm",IDistrictSO.class);
			DistrictReqDTO reqDistrictDTO = new DistrictReqDTO();
			reqDistrictDTO.setDistrictId(districtId);
			DistrictResDTO resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
			PmDistrict newDistrict = resDistrictDTO.getPmDistrict();
			
			s1.setPmDistrict(newDistrict);

			ICitySO citySO = (ICitySO)BusinessDelegate.getInstance().getServiceObject("pm",ICitySO.class);
			CityReqDTO reqCityDTO = new CityReqDTO();
			reqCityDTO.setCityId(cityId);
			CityResDTO resCityDTO = citySO.retriveCityById(reqCityDTO);
			PmCity newCity = resCityDTO.getPmCity();
			
			s1.setPmCity(newCity);

/*			Construct Permanent Address Object			*/
			
			city = s2.getPmCity();
			district = s2.getPmDistrict();
			state = s2.getPmState();
			country = s2.getPmCountry();

			cityId = city.getCityId();
			districtId = district.getDistrictId();
			stateId = state.getStateId();
			countryId = country.getCountryId();
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(countryId);
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			newCountry = resCountryDTO.getPmCountry();
			
			s2.setPmCountry(newCountry);
			
			reqStateDTO = new StateReqDTO();
			reqStateDTO.setStateId(stateId);
			resStateDTO = stateSO.retriveStateByID(reqStateDTO);
			newState = resStateDTO.getPmState();
			
			s2.setPmState(newState);

			reqDistrictDTO = new DistrictReqDTO();
			reqDistrictDTO.setDistrictId(districtId);
			resDistrictDTO = districtSO.retriveDistrictByID(reqDistrictDTO);
			newDistrict = resDistrictDTO.getPmDistrict();
			
			s2.setPmDistrict(newDistrict);

			reqCityDTO = new CityReqDTO();
			reqCityDTO.setCityId(cityId);
			resCityDTO = citySO.retriveCityById(reqCityDTO);
			newCity = resCityDTO.getPmCity();
			
			s2.setPmCity(newCity);

			List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
			
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelPermanentAddress());
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelMailingAddress());
			
			smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);
			
//			smPersonnel.setAuthorizedFlg(1);
//			smPersonnel.setContractSignerFlg(1);
//			smPersonnel.setSoftcertFlg(1);
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(1L);
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			newCountry = resCountryDTO.getPmCountry();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
			smPersonnel.setPmCountry(newCountry);
			
			IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			
			FacesScopes fs = new FacesScopes();
			reqDTO.setServerName(fs.getRequest().getServerName());
			reqDTO.setServerPort(fs.getRequest().getServerPort());
			reqDTO.setSmPersonnel(smPersonnel);
			
			smPersonnel.setSmAppl(getManagedBean().getUpdateSmPersonnel().getSmAppl());
	
			PersonnelResDTO resDTO = smPersonnelSO.savePersonnelRecord(reqDTO);
	
			System.out.println("Result:" + resDTO.isSuccessful());
			
			if(resDTO.isSuccessful()){
				return "acknowledge.xhtml";
			}
			else 
				return null;
	}
	
	private void validatePersonnelBeforeSave(SmPersonnel smPersonnel) {
		if(isExistEmailInPmUser(smPersonnel.getEmail())){
			throw new EpException(MessageConst.ERROR_722540);
		}
	}

	public boolean isExistEmailInPmUser(String email){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);
		
		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);
		
		System.out.println("*********resDTO.getUserList().size()*****" + resDTO.getUserList().size());
		if(resDTO.getUserList().size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public String update(SmPersonnel smPersonnel){
		
		boolean valid = true;
		try{
			valid = validateUI(smPersonnel);
			if(valid){
			
				IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
						.getInstance().getServiceObject("sm", IPersonnelSO.class);
				PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				
				reqDTO.setSmPersonnel(smPersonnel);
		
				PersonnelResDTO resDTO = smPersonnelSO.updatePersonnelRecord(reqDTO);
		
				System.out.println("Result:" + resDTO.isSuccessful());
				
				if(resDTO.isSuccessful())
					return "acknowledge.xhtml";
				else 
					return null;
			} else {
				return null;
			}
		} finally {
			// do nothing
		}
	}
	
	public void assignAccountPrivilege(AjaxBehaviorEvent e) {
		System.out.println("**** Enter Account privilege ******");
		if (getManagedBean().getBasicSmPersonnel().getAuthorizedFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setSoftcertFlg(0);
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}

		if (getManagedBean().getBasicSmPersonnel().getSoftcertFlg() == 0) {
			getManagedBean().getBasicSmPersonnel().setContractSignerFlg(0);
		}
	}
	
	public void removeEPRole(){
		int smUserListSize = getManagedBean().getSmPersonnelDataModel2().getList().size();
		if(smUserListSize < 4)
			getManagedBean().setDisableRole(false);
		else
			getManagedBean().setDisableRole(true);
	}
}
