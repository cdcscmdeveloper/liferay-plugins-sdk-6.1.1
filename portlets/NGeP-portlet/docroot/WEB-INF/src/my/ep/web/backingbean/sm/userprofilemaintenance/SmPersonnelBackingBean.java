package my.ep.web.backingbean.sm.userprofilemaintenance;

import java.util.ArrayList;
import java.util.List;

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
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

@ManagedBean(name="smPersonnelBackingBean")
@ViewScoped
public class SmPersonnelBackingBean extends BaseBackingBean<SmPersonnelManagedBean>{

	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);
	
	public SmPersonnelBackingBean(){
		System.out.println("********************SmPersonnelBackingBean Constructor***************");
	}

	public void preRenderSmPersonnel(){
		System.out.println("********************SmPersonnelBackingBean PreRender***************");

		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());

		initUserPersonnelInfo();
		buildSearchRoleList();
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "userProfileSearch.xhtml");
	}

	public void preRenderUpdateOwnProfilePage(){
		System.out.println("********************SmPersonnelBackingBean preRenderUpdateOwnProfilePage***************");
		
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		getManagedBean().setDefaultCountryId(resDTO.getPmCountry().getCountryId());
		initUserPersonnelInfo();
		buildRoleList();
		
		SelectItem role = new SelectItem();
		role.setLabel("Supplier Admin");
		role.setValue(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		getManagedBean().getRoleList().add(role);
		
		fetchUpdateSmPersonnelAddresses(getManagedBean().getUpdateSmPersonnel());

//		getManagedBean().setDisableRole(false);
/*		SmPersonnel smPersonnel = new SmPersonnel();
		smPersonnel.setPmCountry(resDTO.getPmCountry());

		reqDTO.setCountryId(getManagedBean().getDefaultCountryId());

		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(resDTO.getPmCountry());
		mailAddress.setPmState(new PmState());
		mailAddress.setPmDistrict(new PmDistrict());
		mailAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
		mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
		mailPersonnelAddress.setSmAddress(mailAddress);
		mailPersonnelAddress.setSmPersonnel(smPersonnel);
		
		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress permAddress = new SmAddress();
		permAddress.setPmCountry(resDTO.getPmCountry());
		permAddress.setPmState(new PmState());
		permAddress.setPmDistrict(new PmDistrict());
		permAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
		permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
		permPersonnelAddress.setSmAddress(permAddress);
		permPersonnelAddress.setSmPersonnel(smPersonnel);

		getManagedBean().setUpdateSmPersonnelMailingAddress(mailPersonnelAddress);
		getManagedBean().setUpdateSmPersonnelPermanentAddress(permPersonnelAddress);
		getManagedBean().setUpdateSmPersonnel(smPersonnel);
		getCountryList();
*/
		if(getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress() != null)
			getManagedBean().setTempAddress(getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "updateUserProfile.xhtml");
	}

	public void resetSmPersonnelSearch() {
		System.out.println("********************SmPersonnelBackingBean resetSmPersonnelSearch***************");
		this.getManagedBean().setSearchCompanyRole(null);
		this.getManagedBean().setSearchEPRole(null);
		this.getManagedBean().setSearchUsername(null);
	}

	public void buildRoleList(){
		List<SelectItem> roles = new ArrayList<SelectItem>();
		
		SelectItem role = new SelectItem();
		role.setLabel("Supplier User");
		role.setValue(RoleCode.BASIC_SUPPLIER_USER.value());
		if(!getManagedBean().isDisableRole())
			roles.add(role);
		
		role = new SelectItem();
		role.setLabel("Catalogue User");
		role.setValue(RoleCode.CM_USER.value());
		roles.add(role);
		
		getManagedBean().setRoleList(roles);
	}
	
	public void buildSearchRoleList(){
		List<SelectItem> roles = new ArrayList<SelectItem>();
		
		SelectItem role = new SelectItem();
		role.setLabel("Supplier Admin");
		role.setValue(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		roles.add(role);
		
		role = new SelectItem();
		role.setLabel("Supplier User");
		role.setValue(RoleCode.BASIC_SUPPLIER_USER.value());
		roles.add(role);
		
		role = new SelectItem();
		role.setLabel("Catalogue User");
		role.setValue(RoleCode.CM_USER.value());
		roles.add(role);
		
		getManagedBean().setSearchRoleList(roles);
	}

	public String navigateToAddNewUserPage(){  
		System.out.println("********************SmPersonnelBackingBean navigateToAddNewUserPage***************");
		getManagedBean().setEditMode(false);
		removeEPRole();
		buildRoleList();
		
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(getManagedBean().getDefaultCountryId());
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);
		
//		getManagedBean().setDisableRole(false);
		SmPersonnel smPersonnel = new SmPersonnel();
		smPersonnel.setPmCountry(resDTO.getPmCountry());

		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress mailAddress = new SmAddress();
		mailAddress.setPmCountry(resDTO.getPmCountry());
		mailAddress.setPmState(new PmState());
		mailAddress.setPmDistrict(new PmDistrict());
		mailAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
		mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
		mailPersonnelAddress.setSmAddress(mailAddress);
		mailPersonnelAddress.setSmPersonnel(smPersonnel);
		
		resDTO = reqSO.retrieveCountryById(reqDTO);
		SmAddress permAddress = new SmAddress();
		permAddress.setPmCountry(resDTO.getPmCountry());
		permAddress.setPmState(new PmState());
		permAddress.setPmDistrict(new PmDistrict());
		permAddress.setPmCity(new PmCity());
		
		SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
		permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
		permPersonnelAddress.setSmAddress(permAddress);
		permPersonnelAddress.setSmPersonnel(smPersonnel);

		getManagedBean().setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
		getManagedBean().setBasicSmPersonnelPermanentAddress(permPersonnelAddress);
		getManagedBean().setBasicSmPersonnel(smPersonnel);
		getCountryList();
		updateMailAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
		updatePermAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
		return "basicUserProfile.xhtml";
	}

	public void initUserPersonnelInfo(){
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
	}	
	
	
	public String navigateToUpdateUserPage(String personnelId){
		System.out.println("********************SmPersonnelBackingBean navigateToUpdateUserPage***************");
		getManagedBean().setEditMode(true);
		buildRoleList();
		
		SelectItem role = new SelectItem();
		role.setLabel("Supplier Admin");
		role.setValue(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		getManagedBean().getRoleList().add(role);
		
		getPersonnelInfoById(personnelId);
		if(getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress() != null)
			getManagedBean().setTempAddress(getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress());
		return "basicUserProfile.xhtml";
	}
	
	public String cancel(){

		return "userProfileSearch.xhtml";

	}
	
	public void confirmCreateBasicApplication(){
		
	}
	
	public void initializeLists(){

		System.out.println("********************SmPersonnelBackingBean initializeLists***************");
		getCountryList();

		if(getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses() != null && getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses().size() > 0){
			SmAddress address = new SmAddress();
			if(getManagedBean().getBasicSmPersonnelMailingAddress() != null){
				address = getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress(); 
				if(address.getAddress1() != null){
					updateMailAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId().toString(), address.getPmDistrict().getDistrictId().toString());
				}
			} else {
				SmAddress s1 = new SmAddress();
				
			/*	Construct Mailing Address Object			*/
				ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());
				
				SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
				mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
				mailPersonnelAddress.setSmAddress(s1);
				mailPersonnelAddress.setSmPersonnel(getManagedBean().getBasicSmPersonnel());
				getManagedBean().setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
				getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses().add(getManagedBean().getBasicSmPersonnelMailingAddress());
				updateMailAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
			}
			if(getManagedBean().getBasicSmPersonnelPermanentAddress() != null){
				address = getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress(); 
				if(address.getAddress1() != null){
					updatePermAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId().toString(), address.getPmDistrict().getDistrictId().toString());
				}
			} else {
				SmAddress s1 = new SmAddress();
				
			/*	Construct Permanent Address Object			*/
				ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());
				
				SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
				permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
				permPersonnelAddress.setSmAddress(s1);
				permPersonnelAddress.setSmPersonnel(getManagedBean().getBasicSmPersonnel());
				getManagedBean().setBasicSmPersonnelPermanentAddress(permPersonnelAddress);
				getManagedBean().getBasicSmPersonnel().getSmPersonnelAddresses().add(getManagedBean().getBasicSmPersonnelPermanentAddress());
				updatePermAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
			}
		}
	}
	
	public void initializeUpdateLists(){

		System.out.println("********************SmPersonnelBackingBean initializeUpdateLists***************");
		getCountryList();

		if(getManagedBean().getUpdateSmPersonnel().getSmPersonnelAddresses() != null && getManagedBean().getUpdateSmPersonnel().getSmPersonnelAddresses().size() > 0){
			SmAddress address = new SmAddress();
			if(getManagedBean().getUpdateSmPersonnelMailingAddress() != null){
				address = getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress(); 
				if(address.getAddress1() != null){
					updateMailAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId().toString(), address.getPmDistrict().getDistrictId().toString());
				}
			} else {
					SmAddress s1 = new SmAddress();
				
				/*	Construct Mailing Address Object			*/
					ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
					CountryReqDTO reqCountryDTO = new CountryReqDTO();
					reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
					CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
					s1.setPmCountry(resCountryDTO.getPmCountry());
					s1.setPmState(new PmState());
					s1.setPmDistrict(new PmDistrict());
					s1.setPmCity(new PmCity());
					
					SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
					mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
					mailPersonnelAddress.setSmAddress(s1);
					mailPersonnelAddress.setSmPersonnel(getManagedBean().getUpdateSmPersonnel());
					getManagedBean().setUpdateSmPersonnelMailingAddress(mailPersonnelAddress);
					getManagedBean().getUpdateSmPersonnel().getSmPersonnelAddresses().add(getManagedBean().getUpdateSmPersonnelMailingAddress());
					updateMailAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
			}
			if(getManagedBean().getUpdateSmPersonnelPermanentAddress() != null){
				address = getManagedBean().getUpdateSmPersonnelPermanentAddress().getSmAddress();
				if(address.getAddress1() != null){
					updatePermAddLists(address.getPmCountry().getCountryId().toString(), address.getPmState().getStateId().toString(), address.getPmDistrict().getDistrictId().toString());
				}
			} else {
				SmAddress s1 = new SmAddress();
			
			/*	Construct Permanent Address Object			*/
				ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
				CountryReqDTO reqCountryDTO = new CountryReqDTO();
				reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
				CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
				s1.setPmCountry(resCountryDTO.getPmCountry());
				s1.setPmState(new PmState());
				s1.setPmDistrict(new PmDistrict());
				s1.setPmCity(new PmCity());
				
				SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
				permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
				permPersonnelAddress.setSmAddress(s1);
				permPersonnelAddress.setSmPersonnel(getManagedBean().getUpdateSmPersonnel());
				getManagedBean().setUpdateSmPersonnelPermanentAddress(permPersonnelAddress);
				getManagedBean().getUpdateSmPersonnel().getSmPersonnelAddresses().add(getManagedBean().getUpdateSmPersonnelPermanentAddress());
				updatePermAddLists(getManagedBean().getDefaultCountryId().toString(), "", "");
			}
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

	public void setMailingAddrSameAsPerm(AjaxBehaviorEvent e) {

		if (getManagedBean().getBasicSmPersonnelMailingAddress().isSameAsPerm()) {
			System.out.println("********* Duplicate Mail Addr ***********");
			SmAddress address = cloneAddress(getManagedBean().getBasicSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getBasicSmPersonnelMailingAddress().setSmAddress(address);
			updateMailAddLists(this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmCountry().getCountryId().toString(), this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmState().getStateId().toString(), this.getManagedBean().getBasicSmPersonnelMailingAddress().getSmAddress().getPmDistrict().getDistrictId().toString());
		} else {
			if(getManagedBean().getTempAddress() != null){
				System.out.println("*********** Prev Addr ************** ");
				getManagedBean().getBasicSmPersonnelMailingAddress().setSmAddress(
						getManagedBean().getTempAddress());
			} else {
				System.out.println("*********** Not same Addr ************** ");
				getManagedBean().getBasicSmPersonnelMailingAddress().setSmAddress(
						setDefaultAddr());
//				updateMailAddLists(getManagedBean().getDefaultCountryId().toString(),null,null);
			}
		}
	}

	public void setUpdateMailingAddrSameAsPerm(AjaxBehaviorEvent e) {
		if (getManagedBean().getUpdateSmPersonnelMailingAddress().isSameAsPerm()) {
			System.out.println("********* Duplicate Mail Addr ***********");
			SmAddress address = cloneAddress(getManagedBean().getUpdateSmPersonnelPermanentAddress().getSmAddress());
			this.getManagedBean().getUpdateSmPersonnelMailingAddress().setSmAddress(address);
			updateMailAddLists(this.getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmCountry().getCountryId().toString(), this.getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmState().getStateId().toString(), this.getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmDistrict().getDistrictId().toString());
		} else {
			if(getManagedBean().getTempAddress() != null){
				System.out.println("*********** Prev Addr ************** ");
				getManagedBean().getUpdateSmPersonnelMailingAddress().setSmAddress(
						getManagedBean().getTempAddress());
			} else {
				System.out.println("*********** Not same Addr ************** ");
				getManagedBean().getUpdateSmPersonnelMailingAddress().setSmAddress(
					setDefaultAddr());
			}
		}

	}

	private SmAddress cloneAddress(SmAddress refAddress){
		SmAddress address = new SmAddress();
		if(refAddress.getAddress1() != null)
			address.setAddress1(new String(refAddress.getAddress1().toString()));
		if(refAddress.getAddress2() != null)
			address.setAddress2(new String(refAddress.getAddress2().toString()));
		if(refAddress.getAddress3() != null)
			address.setAddress3(new String(refAddress.getAddress3().toString()));
		if(refAddress.getAddress4() != null)
			address.setAddress4(new String(refAddress.getAddress4().toString()));
		if(refAddress.getPostcode() != null)
			address.setPostcode(new String(refAddress.getPostcode().toString()));
		
		/****Cloning the address******/
		PmCountry country = new PmCountry();
		PmState state = new PmState();
		PmDistrict district = new PmDistrict();
		PmCity city = new PmCity();
		
		CountryReqDTO countryReqDTO = new CountryReqDTO();
		countryReqDTO.setCountryId(refAddress.getPmCountry().getCountryId());
		ICountrySO countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO countryResDTO = countrySO.retrieveCountryById(countryReqDTO);
		country = countryResDTO.getPmCountry();
		address.setPmCountry(country);
		
		StateReqDTO stateReqDTO = new StateReqDTO();
		IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance().getServiceObject("pm", IStateSO.class);
		stateReqDTO.setStateId(refAddress.getPmState().getStateId());
		StateResDTO stateResDTO = stateSO.retriveStateByID(stateReqDTO);
		state = stateResDTO.getPmState();
		address.setPmState(state);

		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
		districtReqDTO.setDistrictId(refAddress.getPmDistrict().getDistrictId());
		DistrictResDTO districtResDTO = districtSO.retriveDistrictByID(districtReqDTO);
		district = districtResDTO.getPmDistrict();
		address.setPmDistrict(district);
		
		CityReqDTO cityReqDTO = new CityReqDTO();
		ICitySO citySO = (ICitySO) BusinessDelegate.getInstance().getServiceObject("pm", ICitySO.class);
		cityReqDTO.setCityId(refAddress.getPmCity().getCityId());
		CityResDTO cityResDTO = citySO.retriveCityById(cityReqDTO);
		city = cityResDTO.getPmCity();
		address.setPmCity(city);

		return address;
	}
	
	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryCode("MY");
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryByCode(reqDTO);
		cntry = resDTO.getPmCountry();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
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
				if(smPA.getAddressType().equalsIgnoreCase(SupplierManagementConst.MAIL_ADD)){
					this.getManagedBean().setBasicSmPersonnelMailingAddress(smPA);
				} else if(smPA.getAddressType().equalsIgnoreCase(SupplierManagementConst.PERM_ADD)){
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
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());

/*			Construct Permanent Address Object			*/
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s2.setPmCountry(resCountryDTO.getPmCountry());
			s2.setPmState(new PmState());
			s2.setPmDistrict(new PmDistrict());
			s2.setPmCity(new PmCity());

			List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
			SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
			permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
			permPersonnelAddress.setSmAddress(s1);
			permPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setBasicSmPersonnelPermanentAddress(permPersonnelAddress);
			SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
			mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
			mailPersonnelAddress.setSmAddress(s1);
			mailPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setBasicSmPersonnelMailingAddress(mailPersonnelAddress);
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelPermanentAddress());
			smPersonnelAddresses.add(getManagedBean().getBasicSmPersonnelMailingAddress());
			smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);
			
			getCountryList();
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
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
	}

	public void fetchUpdateSmPersonnelAddresses(SmPersonnel smPersonnel){
		if(smPersonnel.getSmPersonnelAddresses() != null && smPersonnel.getSmPersonnelAddresses().size() > 0){
			List<SmPersonnelAddress> smPAList = new ArrayList<SmPersonnelAddress>();
			
			smPersonnel.getSmPersonnelAddresses().size();
			smPAList.addAll(smPersonnel.getSmPersonnelAddresses()); 
			for(SmPersonnelAddress smPA : smPAList){
				if(smPA.getAddressType().equalsIgnoreCase(SupplierManagementConst.MAIL_ADD)){
					this.getManagedBean().setUpdateSmPersonnelMailingAddress(smPA);
				} else if(smPA.getAddressType().equalsIgnoreCase(SupplierManagementConst.PERM_ADD)){
					this.getManagedBean().setUpdateSmPersonnelPermanentAddress(smPA);
				}
			}
			initializeUpdateLists();
		} else {
			
			SmAddress s1 = new SmAddress();
			SmAddress s2 = new SmAddress();
			
	/*			Construct Mailing Address Object			*/
			ICountrySO countrySO = (ICountrySO)BusinessDelegate.getInstance().getServiceObject("pm",ICountrySO.class);
			CountryReqDTO reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			CountryResDTO resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s1.setPmCountry(resCountryDTO.getPmCountry());
			s1.setPmState(new PmState());
			s1.setPmDistrict(new PmDistrict());
			s1.setPmCity(new PmCity());
	
	/*			Construct Permanent Address Object			*/
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			s2.setPmCountry(resCountryDTO.getPmCountry());
			s2.setPmState(new PmState());
			s2.setPmDistrict(new PmDistrict());
			s2.setPmCity(new PmCity());
	
			List<SmPersonnelAddress> smPersonnelAddresses = new ArrayList<SmPersonnelAddress>();
			SmPersonnelAddress permPersonnelAddress = new SmPersonnelAddress();
			permPersonnelAddress.setAddressType(SupplierManagementConst.PERM_ADD);
			permPersonnelAddress.setSmAddress(s1);
			permPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setUpdateSmPersonnelPermanentAddress(permPersonnelAddress);
			SmPersonnelAddress mailPersonnelAddress = new SmPersonnelAddress();
			mailPersonnelAddress.setAddressType(SupplierManagementConst.MAIL_ADD);
			mailPersonnelAddress.setSmAddress(s1);
			mailPersonnelAddress.setSmPersonnel(smPersonnel);
			getManagedBean().setUpdateSmPersonnelMailingAddress(mailPersonnelAddress);
			smPersonnelAddresses.add(getManagedBean().getUpdateSmPersonnelPermanentAddress());
			smPersonnelAddresses.add(getManagedBean().getUpdateSmPersonnelMailingAddress());
			smPersonnel.setSmPersonnelAddresses(smPersonnelAddresses);
			
			getCountryList();
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
			resCountryDTO = countrySO.retrieveCountryById(reqCountryDTO);
			smPersonnel.setPmCountry(resCountryDTO.getPmCountry());
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
		
		country = getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmCountry();
		state = getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmState();
		district = getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmDistrict();
		city = getManagedBean().getUpdateSmPersonnelMailingAddress().getSmAddress().getPmCity();
		
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
		if(getManagedBean().getBasicSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()))
			getManagedBean().getBasicSmPersonnel().setAuthorized(true);
		else if(getManagedBean().getBasicSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.CM_USER.value()))
			getManagedBean().getBasicSmPersonnel().setAuthorized(false);
		if(getManagedBean().getBasicSmPersonnel().getPersonnelId() == null){
			returnValue = saveNewUser(getManagedBean().getBasicSmPersonnel());
		} else {
			returnValue = saveUser(getManagedBean().getBasicSmPersonnel());
		}
			return returnValue;
	}

	public String saveUser(SmPersonnel smPersonnel){
//		validatePersonnelBeforeSave(smPersonnel);
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmPersonnel(smPersonnel);
		PersonnelResDTO resDTO = smPersonnelSO.savePersonnelRecord(reqDTO);
		System.out.println("Result:" + resDTO.isSuccessful());
		if(resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_100002, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return "/xhtml/sm/userProfileMaintenance/outputMsg";
		} else 
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
			
			if(smPersonnel.getIdentificationType().getParameterCode().equals("I")){
					myIdentityVerification(smPersonnel);
			}
			
			reqCountryDTO = new CountryReqDTO();
			reqCountryDTO.setCountryId(getManagedBean().getDefaultCountryId());
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
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_721014, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				return "/xhtml/sm/userProfileMaintenance/outputMsg";
			}
			else 
				return null;
	}
	
	private void validatePersonnelBeforeSave(SmPersonnel smPersonnel) {
		if(isExistEmailInPmUser(smPersonnel.getEmail())){
			throw new EpException(MessageConst.ERROR_722540);
		}
		if(isExistUserByIdentificationNo(smPersonnel.getIdentificationNo())){
			throw new EpException(MessageConst.ERROR_722541);
		}
		
		try {
			if(UserLocalServiceUtil.getUserByEmailAddress(Long.valueOf(getCompanyId()), smPersonnel.getEmail()) != null){
				throw new EpException(MessageConst.ERROR_722540);
			}
		} catch (NumberFormatException | PortalException | SystemException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
		
		try {
			if(UserLocalServiceUtil.getUserByScreenName(Long.valueOf(getCompanyId()), smPersonnel.getEmail()) != null) {
				throw new EpException(MessageConst.ERROR_722541);
			}
		} catch (NumberFormatException | PortalException | SystemException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
	}

	public void myIdentityVerification(SmPersonnel per) {
		String postCode = "";
		for (SmPersonnelAddress addr : per.getSmPersonnelAddresses()) {
			if (addr.getAddressType() != null
					&& addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
					&& addr.getSmAddress() != null) {
				postCode = addr.getSmAddress().getPostcode();
				System.out.println(" *** Perm Addr Post Code ****" + postCode);
			}
		}
		IMyIdentitySO myIdentitySO = (IMyIdentitySO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMyIdentitySO.class);
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		reqDTO.setName(per.getName());
		reqDTO.setNewIcNo(per.getIdentificationNo());
		reqDTO.setType("Online");
		reqDTO.setPostCode(postCode);
		System.out
				.println(" *** Req DTO Post Code ****" + reqDTO.getPostCode());
		MyIdentityResDTO resDTO = myIdentitySO.myIdentityVerification(reqDTO);
		StringBuffer sb = new StringBuffer();
		sb.append("MessageConst.ERROR_MYIDENTITY_");
		if (!resDTO.getStatusCode().equalsIgnoreCase(
				SupplierManagementConst.SM_MYIDENTITY_STATUS_CODE_VALID)) {
			sb.append(resDTO.getStatusCode());
			throw new EpException(sb.toString());
		}
	}

	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID);
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

	public boolean isExistUserByIdentificationNo(String idNo) {
		System.out.println("*********retrieveUserByIdentificationNo**********" + idNo);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
		
		if(resDTO.getPmUser() != null){
			return true;
		}else{
			return false;
		}
	}
	
	public String update(SmPersonnel smPersonnel){
		
//		boolean valid = true;
		try{
//			valid = validateUI(smPersonnel);
//			if(valid){
			
				IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
						.getInstance().getServiceObject("sm", IPersonnelSO.class);
				PersonnelReqDTO reqDTO = new PersonnelReqDTO();
				
				reqDTO.setSmPersonnel(smPersonnel);
		
				PersonnelResDTO resDTO = smPersonnelSO.updatePersonnelRecord(reqDTO);
		
				System.out.println("Result:" + resDTO.isSuccessful());
				
				if(resDTO.isSuccessful()){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_INFO,
							null,
							MessageUtils.getMessage(MessageConst.INFO_100002, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					return "/xhtml/sm/userProfileMaintenance/outputMsg";

				} else 
					return null;
//			} else {
//				return null;
//			}
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
		int smUserListSize = 0;
		if(getManagedBean().getSmPersonnelDataModel2().getList() != null)
			smUserListSize = getManagedBean().getSmPersonnelDataModel2().getList().size();
		if(smUserListSize < 4)
			getManagedBean().setDisableRole(false);
		else
			getManagedBean().setDisableRole(true);
	}
	
	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		System.out.println("***** The selected value of IC type is --- "
				+ getManagedBean().getBasicSmPersonnel()
						.getIdentificationTypeId());
		// getManagedBean().getSelectedShareHolder().setIdentificationNo("");
		if (getManagedBean().getBasicSmPersonnel().getIdentificationTypeId() != null) {
			getManagedBean().getBasicSmPersonnel()
					.setIdentificationType(
							retrievePmParameterById(getManagedBean()
									.getBasicSmPersonnel()
									.getIdentificationTypeId()));
			if (getManagedBean().getBasicSmPersonnel()
					.getIdentificationType() == null) {
				getManagedBean().getBasicSmPersonnel()
						.setIdentificationType(new PmParameter());
			}
		} else {
			getManagedBean().getBasicSmPersonnel().setIdentificationType(
					new PmParameter());
		}
	}
	
	private PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}


}
