package my.ep.web.backingbean.pm.financialorg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
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
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.FinancialOrgSearchBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.pm.financialorg.FinancialOrgManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * Backing Bean for Financial Org
 * 
 * @author RAIN POH
 * @since 8/1/2014
 *
 */
@ManagedBean(name = "financialOrgBackingBean")
@ViewScoped
public class FinancialOrgBackingBean extends
		BaseBackingBean<FinancialOrgManagedBean> {
	
	private static Logger log = Logger.getLogger(FinancialOrgBackingBean.class);
	boolean result = false;
	// Tab values
	public static final int GENERRAL = 0;
	public static final int FACTRING_BRANCH = 1;
	

	// Default value
	public static final boolean SEARCH_DEFAULT = false;
	// Navigation Details
	public static final String NAV_TO_SEARCH = "financialOrgSearchMain";
	public static final String NAV_TO_MAINTAIN = "maintainFinancialOrg";
	private IPagingDataModel<PmFinancialOrg> searchFinancialOrg = new FinancialOrgPagingDataModel();


	//Mapper for DDL object
	private static Map<Long, Object> serviceTypeMapper;
	

	public FinancialOrgBackingBean() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			//System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}
	
	/* ------------------ PORLET - START ------------------  */
	/**
	 * load clean search view
	 */
	public void preLoadingFinOrg() {
		log.info("preLoading");
		this.resetFinancialOrgSearchBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
	}

	public int getGeneralInfoTab() {
		return GENERRAL;
	}

	public int getFactringBranchInfoTab() {
		return FACTRING_BRANCH;
	}

	/**
	 * porlet side menu 
	 * @param index
	 */
	public void changeMenuIndex(int index){
		log.info("changeMenuIndex");
		this.getManagedBean().setSelectedTabIndex(index);
	}
	/**
	 * navigate to search page
	 * @return search page
	 */
	public String backToMain() {
		log.info("backToMain");		
		getManagedBean().setServiceTypes(null);
		initialFinOrg();
		return NAV_TO_SEARCH;

	}
	
	/**
	 * navigate to financial org details
	 * @param id
	 * @return maintain page
	 * @throws Exception
	 */
	public String redirectToFinOrg(Long id) throws Exception {
		log.info("redirectToFinOrg");
		resetBeans();
		retrieveServiceTypeMap();

		if (id != null && id > 0) {
			maintainFinancialOrg(id);

		} else {
			initialFinOrg();

		}
		getManagedBean().setShowParentMsg(true);
		return NAV_TO_MAINTAIN;
	}
	
	
	/**
	 * reset managed bean values
	 */
	private void resetBeans(){
		log.info("resetBeans");
		//reset managedBean
		this.getManagedBean().setFinancialOrg(null);
//		this.getManagedBean().setBranchAddress(null);
//		this.getManagedBean().setBranchContactPerson(null);
		this.getManagedBean().setContactPerson(null);
		this.getManagedBean().setPmAddress(null);
		this.getManagedBean().setSelectedTabIndex(GENERRAL);
		this.getManagedBean().setEnableUpdateStatus(true);
		this.getManagedBean().setShowFactoringBank(true);
		this.getManagedBean().setServiceTypes(null);
	}
	
	/**
	 * initial new pmFinancialOrg
	 */
	public void initialFinOrg() {
		log.info("initialFinOrg");
		this.getManagedBean().setSelectedTabIndex(GENERRAL);
		//Country Code
		PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		
		// General
		/* enable it  */
		PmFinancialOrg financialOrg = new PmFinancialOrg();
		financialOrg.setPmCountry(defaultCountry);
		financialOrg.setPmFinancialAccs(new ArrayList<PmFinancialAcc>());
		financialOrg.setPmFinancialSvcs(new ArrayList<PmFinancialSvc>());
		getManagedBean().setServiceTypes(new ArrayList<String>());
		getManagedBean().setFinancialOrg(financialOrg);
		
		//Financial Org Address
		getManagedBean().setPmAddress(PmCommonUtil.getNewPmAddress());
		getManagedBean().setContactPerson(new PmContact());
	
//		//Branch Details
//		getManagedBean().setBranchAddress(PmCommonUtil.getNewPmAddress());
//		getManagedBean().setBranchContactPerson(new PmContact());


	}

	/**
	 * retrieve financial org by id
	 * @param id
	 * @return PmFinancialOrg
	 */
	private PmFinancialOrg retrievePmFinancialOrg(Long id){
		log.info("retrievePmFinancialOrg");
		PmFinancialOrg pmFinancialOrg = new PmFinancialOrg();
		IFinancialOrgSO finSO  = null;
		try{
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setFinancialOrgId(id);
			finSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IFinancialOrgSO.class);
			FinancialOrgResDTO resDTO = finSO.retrieveFinancialOrgDetById(reqDTO);
			pmFinancialOrg = resDTO.getPmFinancialOrg();
		}finally{
			finSO = null;
		}
		return pmFinancialOrg;
	}
	
	/**
	 * load financial org by id
	 * @param id
	 */
	public void maintainFinancialOrg(Long id) {
		log.info("maintainFinancialOrg");
		
		this.getManagedBean().setFinancialOrg(retrievePmFinancialOrg(id));
		getManagedBean().setServiceTypes(new ArrayList<String>());

		for (PmFinancialSvc financialSvc : this.getManagedBean().getFinancialOrg().getPmFinancialSvcs()) {
			PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(financialSvc.getServiceTypeCode().getParameterId());
			if(this.getManagedBean().getServiceTypes()== null){
				this.getManagedBean().setServiceTypes(new ArrayList<String>());			   
			}
			this.getManagedBean().getServiceTypes().add(pmParameter.getParameterCode());
			 
			 // disable/enable status update 
			 if(ParameterConst.BANK_SERVICES_PAYMENT_BANK.equalsIgnoreCase(pmParameter.getParameterCode())){
				 if(DbValueConst.RECORD_STATUS_ACTIVE == financialSvc.getRecordStatusFlg()){					
					 this.getManagedBean().setEnableUpdateStatus(false);
				 }
				
			 }			
			 
			 if(ParameterConst.BANK_SERVICES_FACTORING_COMPANY.equalsIgnoreCase(pmParameter.getParameterCode())){
				
				
				 if(DbValueConst.RECORD_STATUS_DELETED == financialSvc.getRecordStatusFlg()){
					 
					 this.getManagedBean().setShowFactoringBank(false);
				 }	
				
				 for(PmFinancialAcc financialAcc:getManagedBean().getFinancialOrg().getPmFinancialAccs()){
					 if(!getManagedBean().getFinancialOrg().getPmFinancialSvcs().isEmpty()&& getManagedBean().getFinancialOrg().getPmFinancialSvcs()!=null){						 
					
						 IFinancialOrgSO financialOrgSO = null;
							FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
							log.info("FinancialOrg ID"+financialAcc.getPmPaymentBankOrg().getFinancialOrgId());
							reqDTO.setFinancialOrgId(financialAcc.getPmPaymentBankOrg().getFinancialOrgId());
							financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
									IFinancialOrgSO.class);
							FinancialOrgResDTO resDTO  = financialOrgSO
									.retrieveFinancialSvcByFinancialOrgId(reqDTO);

							PmFinancialSvc pmFinancialSvc = resDTO.getPmFinancialSvc();
							if(null!=pmFinancialSvc){
							//log.info("BankCode"+pmFinancialSvc.getBankCode());
							financialAcc.setBankCode(pmFinancialSvc.getBankCode());		
							}
					 }
				 }
		}
		}
		

		
		// financial org Address
		List<PmAddressType> finAddressTypes = this.getManagedBean().getFinancialOrg().getPmAddressTypes();

		boolean addrExist = false;
		boolean contactExist = false;
	
		if (finAddressTypes != null && !finAddressTypes.isEmpty()) {
			for (PmAddressType pmAddressType : finAddressTypes) {
				// financial address
				if (pmAddressType.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())&&(pmAddressType.getRecordStatusFlg() ==
						DbValueConst.RECORD_STATUS_ACTIVE)) {
					this.getManagedBean().setPmAddress(pmAddressType.getPmAddress());
					addrExist = true;
					// financial contact
					if (pmAddressType.getPmContacts() != null && !pmAddressType.getPmContacts().isEmpty()) {
						this.getManagedBean().setContactPerson(pmAddressType.getPmContacts().get(0));
						contactExist= true;
					}else if(!contactExist){
						log.info("Contact not Exist");
						List<PmContact> contactLst = new ArrayList<PmContact>();
						getManagedBean().setContactPerson(new PmContact());
						PmContact contact = getManagedBean().getContactPerson();					
						contact.setPmAddressType(pmAddressType);
						contactLst.add(contact);
						pmAddressType.setPmContacts(contactLst);
					}
				}
				
				
			}
		}
		if (!addrExist) {
			// new financial contact
			this.getManagedBean().setPmAddress(PmCommonUtil.getNewPmAddress());
			// new financial address
			this.getManagedBean().setContactPerson(new PmContact());
			
			List<PmContact> contactLst = new ArrayList<PmContact>();
			PmAddressType addressType = new PmAddressType();
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			addressType.setPmAddress(this.getManagedBean().getPmAddress());
			addressType.setPmFinancialOrg(this.getManagedBean().getFinancialOrg());

			// bind contact & address type
			this.getManagedBean().getContactPerson().setPmAddressType(addressType);
			contactLst.add(this.getManagedBean().getContactPerson());
			addressType.setPmContacts(contactLst);
			
			// by address type & financial org
			finAddressTypes.add(addressType);
			this.getManagedBean().getFinancialOrg().setPmAddressTypes(finAddressTypes);
	
		}

	}

	/* ----*/
	
	/**
	 * contact person add button action- show the contact person dialog
	 */
	public void addContacePerson() {
		RequestContext.getCurrentInstance().execute("contactPersonPopup.show()");
		getManagedBean().setShowParentMsg(false);
	}

	/**
	 * contact person ok button action- hide the contact person dialog
	 */
	public void okContacePerson() {
		RequestContext.getCurrentInstance().execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}

	/**
	 * contact person close button action- hide the contact person dialog
	 */
	public void closeContacePerson() {
		RequestContext.getCurrentInstance().execute("contactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}

	/**
	 * branch contact person add button action- show the branch contact person dialog
	 */
	public void addBranchContacePerson() {

		RequestContext.getCurrentInstance().execute("branchContactPersonPopup.show()");
		getManagedBean().setShowParentMsg(false);
	}

	/**
	 * branch contact person ok button action- hide the branch contact person dialog
	 */
	public void okBranchContacePersonAction() {
		RequestContext.getCurrentInstance().execute("branchContactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(false);
	}

	/**
	 * branch contact person close button action- hide the branch contact person dialog
	 */
	public void closeBranchContacePerson() {

		RequestContext.getCurrentInstance().execute(
				"branchContactPersonPopup.hide()");
		getManagedBean().setShowParentMsg(true);
	}
	/**
	 * get all country list
	 * @return List<PmCountry>
	 */
	public List<PmCountry> getCountryList() {
		log.info("getCountryList");
		return PmCommonUtil.retrievePmCountryList();
	}
	
	/**
	 * get state list by country id
	 * @param countryId
	 * @return List<PmState>
	 */
	public List<PmState> getStateList(Long countryId) {
		log.info("getStateList");
		return  PmCommonUtil.retrievePmStateList(countryId);
	}
	
	/**
	 * get division list by state id (only applicable for sarawak)
	 * @param stateId
	 * @return List<PmDivision>
	 */
	public List<PmDivision> getDivisionList(Long stateId) {
		log.info("getDivisionList");
		return PmCommonUtil.retrievePmDivisionList(stateId);
	}
	
	/**
	 * get district list by state id
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	public List<PmDistrict> getDistrictList(Long stateId) {
		log.info("getDistrictList");
		log.info("getDistrictList");
		if(getManagedBean().getPmAddress().getPmDivision() != null 
				&& getManagedBean().getPmAddress().getPmDivision().getDivisionId() !=null){
			return PmCommonUtil.retrievePmDistrictListByDivisionId(getManagedBean().getPmAddress().getPmDivision().getDivisionId());					
		}else {
				return PmCommonUtil.retrievePmDistrictList(stateId);
		}
	
	}
	
	/**
	 * get city list by district id
	 * @param districtId
	 * @return List<PmCity>
	 */
	public List<PmCity> getCityList(Long districtId) {
		log.info("getCityList");
		return PmCommonUtil.retrievePmCityList(districtId);
	}
	
	/**
	 * retrieve servery type into mapper
	 */
	private void retrieveServiceTypeMap(){
		log.info("retrieveServiceTypeMap");
		List<PmParameterDesc> parameterDescs = retrieveServiceTypeList();
		serviceTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			serviceTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * retrieve service type
	 * @return List<PmParameterDesc>
	 */
	private List<PmParameterDesc> retrieveServiceTypeList() {
		log.info("retrieveServiceTypeList");
		return ParameterHelper.getInstance().getParameterDescListBySearchBean(ParameterTypeConst.BANK_SERVICES, null);
	}
	
	/**
	 * generate service type options
	 * @return  List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getServiceTypeList() {
		log.info("getServiceTypeList");
		List<PmParameterDesc> serviceTypeList = new ArrayList<PmParameterDesc>();
		/*if(this.getManagedBean().getServiceTypes()!=null && this.getManagedBean().getServiceTypes().contains(ParameterConst.BANK_SERVICES_PAYMENT_BANK)){
				serviceTypeList = retrieveServiceTypeList();
		}else{*/
			List<String> recommendList = new ArrayList<String>();
			recommendList.add(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
			serviceTypeList = PmCommonUtil.retrieveFilteredParamDesc(ParameterTypeConst.BANK_SERVICES
					, recommendList
					, null);
		//}
		return serviceTypeList;

	}
	
	/**
	 * payment bank options
	 * @return List<PmFinancialOrg>
	 */
	public List<PmFinancialOrg> getPaymentBankList() {
		log.info("getPaymentBankList");
		return retrievePaymentBankList();

	}
	
	/**
	 * retrieve payment list from db
	 * @return List<PmFinancialOrg>
	 */
	private List<PmFinancialOrg> retrievePaymentBankList() {
		log.info("retrievePaymentBankList");
		IFinancialOrgSO financialOrgSO = null;
		List<PmFinancialOrg> paymentBankList = new ArrayList<PmFinancialOrg>();
		try{
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
			
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			paymentBankList = resDTO.getFinancialOrgList();
		}finally{
			financialOrgSO = null;
		}
		return paymentBankList;
	}


	/**
	 * status options
	 * @return SelectItem[]
	 */
	public static SelectItem[] getStatusOptions() {
		log.info("getStatusOptions");
		return PmCommonUtil.getRecordStatusOptions();
	}
	
//	/**
//	 * retrieve state object
//	 * @param pmState
//	 */
//	public void retrievePmState(PmState pmState){
//		log.info("retrievePmState");
//		if(pmState!=null){
//			if(pmState.getStateId()!=null){
//				log.info("pmState.getStateId() " + pmState.getStateId());
//				PmState tempState =  PmCommonUtil.retrievePmStateByStateId(pmState.getStateId());
//				pmState.setStateCode(tempState.getStateCode());
//			}else{
//				pmState.setStateCode(null);
//			}
//		}
//	}
	
	/**
	 * retrieve state object
	 * 
	 * @param pmState
	 */
	public void retrievePmState(PmState pmState) {
		log.info("retrievePmState");
		getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getPmAddress().setPmCity(new PmCity());
		getManagedBean().getPmAddress().setPmDivision(null); 
		if(pmState != null ){
			if (pmState.getStateId() != null) {
				log.info("pmState.getStateId() " + pmState.getStateId());
				pmState = PmCommonUtil.retrievePmStateByStateId(pmState
						.getStateId());				
				getManagedBean().getPmAddress().setPmState(pmState);				
				if(pmState.getAdoptDivision()){
					getManagedBean().getPmAddress().setPmDivision(new PmDivision()); 
				}	
			} else {
				pmState.setStateCode(null);
			}
		}
	}

	/**
	 * validate financial org fields
	 */
	private void validationFinancialOrg() {
		log.info("validationFinancialOrg");
		if(this.getManagedBean().getFinancialOrg().getPmFinancialSvcs()!=null && this.getManagedBean().getFinancialOrg().getPmFinancialSvcs().isEmpty()){
			throw new EpException(MessageConst.ERROR_775203);
		}
		
		if(StringUtils.isBlank(this.getManagedBean().getFinancialOrg().getBizRegNumber())){
			if(!this.getManagedBean().getServiceTypes().contains(ParameterConst.BANK_SERVICES_PAYMENT_BANK)){
				throw new EpException(MessageConst.ERROR_775204);
			}
		}
		
		if(this.getManagedBean().getServiceTypes().contains(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)){
			if(this.getManagedBean().getFinancialOrg().getPmFinancialAccs() == null || this.getManagedBean().getFinancialOrg().getPmFinancialAccs().isEmpty()){
				throw new EpException(MessageConst.ERROR_775205);
			}
		}
		
		 if(this.getManagedBean().getPmAddress()!=null){
//			 for(PmAddressType pmAddressType:this.getManagedBean().getFinancialOrg().getPmAddressTypes()){
//					if (AddressType.BILLING_ADDRESS.value().equalsIgnoreCase(pmAddressType.getAddressType()) 
//							&& DbValueConst.RECORD_STATUS_ACTIVE == pmAddressType.getRecordStatusFlg()){
						PmAddress pmAddress = this.getManagedBean().getPmAddress();
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
						
//						PmState pmState = PmCommonUtil.retrievePmStateByStateId(pmAddress.getPmState().getStateId());
						if(pmAddress.getPmState().getAdoptDivision()){
							if(pmAddress.getPmDivision().getDivisionId()==null){
								throw new EpException(MessageConst.ERROR_770044);
							}
						}else{
							//set set division null
							pmAddress.setPmDivision(null);
						}
						
				
//						for(PmContact pmContact:pmAddressType.getPmContacts()){
							if(StringUtils.isBlank(this.getManagedBean().getContactPerson().getContactName())){
								throw new EpException(MessageConst.ERROR_775206);
							}
							if(StringUtils.isBlank(this.getManagedBean().getContactPerson().getEmail())){
								throw new EpException(MessageConst.ERROR_775207);
							}
//						}
					}
		 
		 if(StringUtils.isNotBlank(getManagedBean().getFinancialOrg().getFinOrgName())){
			 String finOrgName = getManagedBean().getFinancialOrg().getFinOrgName();
			 getManagedBean().getFinancialOrg().setFinOrgName(finOrgName.trim());
			 IFinancialOrgSO financialOrgSO = null;
				try{
					ISearchBean<PmFinancialOrg> searchBean = getFinOrgSearchBean("newOrgTypeFederalList");
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("finOrgName",
									SearchOperator.OPERATOR_EQUALS.value(),
									getManagedBean().getFinancialOrg().getFinOrgName(), true));
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("recordStatusFlg",
									SearchOperator.OPERATOR_EQUALS.value(), 
									DbValueConst.RECORD_STATUS_ACTIVE
									));
					
					if(!getManagedBean().getFinancialOrg().isBrandNew() 
							&& getManagedBean().getFinancialOrg().getFinancialOrgId() != null){
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("financialOrgId",
										SearchOperator.OPERATOR_NOT_EQUALS.value(),
										getManagedBean().getFinancialOrg().getFinancialOrgId()));
					}
					
					FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
					reqDTO.setFinancialSearchBean(searchBean);
					financialOrgSO = (IFinancialOrgSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IFinancialOrgSO.class);
					FinancialOrgResDTO resDto = financialOrgSO
							.searchFinancialOrgData(reqDTO);
					
					if(resDto.getFinaceialOrgResultBean() != null 
							&& resDto.getFinaceialOrgResultBean().getResultList() != null 
							&& !resDto.getFinaceialOrgResultBean().getResultList().isEmpty()){
						throw new EpException(
								MessageConst.ERROR_775208,  
								new String[]{getManagedBean().getFinancialOrg().getFinOrgName()});
					}		
					
				}
				catch (Exception e) {
					e.printStackTrace();
					throw e;
				}finally{
					financialOrgSO = null;
				}
		 }

		
		
	}
	
	/**
	 * update main address 
	 */
	private void updateMainAddress(){
		log.info("updateMainAddress");
		boolean hasAddressType = false;
		if(this.getManagedBean().getFinancialOrg().getPmAddressTypes()==null){
			this.getManagedBean().getFinancialOrg().setPmAddressTypes(new ArrayList<PmAddressType>());
		}
		
		for(PmAddressType pmAddressType:this.getManagedBean().getFinancialOrg().getPmAddressTypes()){
			if (AddressType.BILLING_ADDRESS.value().equalsIgnoreCase(pmAddressType.getAddressType()) 
					&& DbValueConst.RECORD_STATUS_ACTIVE == pmAddressType.getRecordStatusFlg()){
				log.info("updateMainAddress - update");
				hasAddressType = true;
				pmAddressType.setPmAddress(this.getManagedBean().getPmAddress());
				
				if(pmAddressType.getPmContacts()==null){
					pmAddressType.setPmContacts(new ArrayList<PmContact>());
				}
				if(this.getManagedBean().getContactPerson().isBrandNew()){
					this.getManagedBean().getContactPerson().setPmAddressType(pmAddressType);
					pmAddressType.getPmContacts().add(this.getManagedBean().getContactPerson());
				}else{
					pmAddressType.getPmContacts().clear();
					pmAddressType.getPmContacts().add(this.getManagedBean().getContactPerson());
				}
			}
		}
		
		if(!hasAddressType){
			log.info("updateMainAddress - add");
			
			
			List<PmContact> contactLst = new ArrayList<PmContact>();
			PmAddressType addressType = new PmAddressType();
			addressType.setAddressType(AddressType.BILLING_ADDRESS.value());
			addressType.setPmAddress(this.getManagedBean().getPmAddress());
			addressType.setPmFinancialOrg(this.getManagedBean().getFinancialOrg());

			// bind contact & address type
			if(this.getManagedBean().getContactPerson().isBrandNew()){
				this.getManagedBean().getContactPerson().setPmAddressType(addressType);
			}
			contactLst.add(this.getManagedBean().getContactPerson());
			addressType.setPmContacts(contactLst);
			
			// by address type & financial org
			this.getManagedBean().getFinancialOrg().getPmAddressTypes().add(addressType);
		}
	
	}
	
	/**
	 * validate financial account fields
	 * @throws Exception
	 */
	private void validateFinancialAccount()throws Exception {
		log.info("validateFinancialAccount");
		List<PmFinancialAcc> pmFinancialAccList = this.getManagedBean().getFinancialOrg().getPmFinancialAccs();
		//System.out.println("pmFinancialAccList size " + pmFinancialAccList.size());		
		if (pmFinancialAccList != null && !pmFinancialAccList.isEmpty()) {
			for(PmFinancialAcc pmFinancialAcc: pmFinancialAccList){
				if(pmFinancialAcc.getPmPaymentBankOrg()==null 
						|| pmFinancialAcc.getPmPaymentBankOrg().getFinancialOrgId()==null){
					throw new EpException(MessageConst.ERROR_775209);
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date endLess = sdf.parse("2099-12-31");
			for (int index1 = 0; index1 < pmFinancialAccList.size(); index1++) {
				if (pmFinancialAccList.get(index1).getRecordStatusFlg() 
						== DbValueConst.RECORD_STATUS_ACTIVE) {
					if (pmFinancialAccList.size() > 1) {
						for (int index2 = index1 + 1; index2 < pmFinancialAccList.size(); index2++) {
							if (pmFinancialAccList.get(index2).getRecordStatusFlg() 
									== DbValueConst.RECORD_STATUS_ACTIVE) {
								// Overlap exists if (StartA <= EndB) and (EndA
								// >= StartB)
								Date startA = pmFinancialAccList.get(index1).getEffDate();
								Date endA = pmFinancialAccList.get(index1).getExpDate();
								Date startB = pmFinancialAccList.get(index2).getEffDate();
								Date endB = pmFinancialAccList.get(index2).getExpDate();

								// not null
								if(endA==null){
									endA = endLess;
								}if(endB==null){
									endB = endLess;
								}
								
								boolean isCurrentPairOverlap = false;
								isCurrentPairOverlap = PmCommonUtil.dateOverlapping(startA, endA, startB, endB, false);
								
								if (isCurrentPairOverlap) {
									throw new EpException(MessageConst.ERROR_775210);
								}
							}
						}
					} 
				}
			}
		}
	}

	/**
	 * save/update financial org
	 * @return
	 * @throws Exception
	 */
	public String saveFinancialOrg(String outcome) throws Exception{
		log.info("saveFinancialOrg");
		String redirectUrl = null;
		IFinancialOrgSO financialOrgSO = null;
		try{
			
			validationFinancialOrg(); // Financial Org
			validateFinancialService(); // Financial Service
			validateFinancialAccount(); // Factoring Bank Det
//			validateBranch(); // Branch
			
			updateMainAddress();
			
			
			PmFinancialOrg financialOrg = this.getManagedBean().getFinancialOrg();
			saveFactoringDetails(financialOrg);
	
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setPmFinancialOrg(financialOrg);
			reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			
			financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
			FinancialOrgResDTO resDTO = financialOrgSO.saveOrUpdateFinancialOrgDetails(reqDTO);
	
			if (resDTO.isSuccessful()) {
				result = resDTO.isSuccessful();
//				FacesContext.getCurrentInstance().addMessage(
//						null,
//						new FacesMessage(FacesMessage.SEVERITY_INFO,
//								"Financial Org successfully.", "Org Name : "
//										+ this.getManagedBean().getFinancialOrg()
//												.getFinOrgName()));
				
				String message = "";
				if(BpmOutcomeConst.BPM_SAVE.equalsIgnoreCase(outcome)){
					message = MessageUtils.getMessage(MessageConst.INFO_780093, new String[]{resDTO.getPmFinancialOrg().getFinOrgName()}, 
				    		MessageUtils.BUNDLE_COMMON_MESSAGES, 
				    		new FacesScopes().getUserLocale());
					redirectUrl = NAV_TO_SEARCH;
				}else if(BpmOutcomeConst.BPM_UPDATE.equalsIgnoreCase(outcome)){
					message = MessageUtils.getMessage(MessageConst.INFO_780096, 
				    		MessageUtils.BUNDLE_COMMON_MESSAGES, 
				    		new FacesScopes().getUserLocale());
					redirectUrl = null;
					redirectToFinOrg(resDTO.getPmFinancialOrg().getFinancialOrgId());
				}
				
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(
						    FacesMessage.SEVERITY_INFO,
						    null,
						    message
				    		));
	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			financialOrgSO = null;
		}
		return redirectUrl;
	}
	
	/**
	 * save pmOrgProfile if financial org contains factoring org
	 * @param financialOrg
	 */
	public void saveFactoringDetails(PmFinancialOrg financialOrg){
		log.info("saveFactoringDetails");
		boolean orgRequired = false;
		boolean hasFactoringOrg = false;
		
		// check if Factoring Org. is required
		for (PmFinancialSvc financialSvc : financialOrg.getPmFinancialSvcs()) {
			//System.out.println("Record Status Fin SVC---====>>>"+financialSvc.getRecordStatusFlg());
			PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(financialSvc.getServiceTypeCode().getParameterId());
			if(ParameterConst.BANK_SERVICES_FACTORING_COMPANY.equalsIgnoreCase(pmParameter.getParameterCode())){
				if(financialSvc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED){
					for(PmFinancialAcc finAcc:financialOrg.getPmFinancialAccs()){
						//System.out.println("Record Status Fin ACC---====>>>"+finAcc.getRecordStatusFlg());
						finAcc.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						finAcc.setExpDate(new Date());
						
					}
				}
				orgRequired = true;
			}
		}
		
		if(orgRequired){
			if(financialOrg.getPmOrgProfiles() == null){
				financialOrg.setPmOrgProfiles(new ArrayList<PmOrgProfile>());
			}
			// check if it is already added
			for(PmOrgProfile pmOrgProfile : financialOrg.getPmOrgProfiles()){
				if(pmOrgProfile.isBrandNew()){
					if(OrganizationType.FACTORING_COMPANY.value().equalsIgnoreCase(pmOrgProfile.getOrgCode())){
						hasFactoringOrg = true;
						break;
					}
				}
			}
			
			if(!financialOrg.isBrandNew()){
				if(!checkFactoringOrg(financialOrg.getFinancialOrgId())){
					hasFactoringOrg = true;
				}
			}
			
			// add  Factoring Org. if required
			if(!hasFactoringOrg){
				log.info("add new factoring org");
				PmOrgProfile orgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				
				orgTypeCode.setParameterCode(OrganizationType.FACTORING_COMPANY.value());
				orgProfile.setOrgTypeCode(orgTypeCode);
				orgProfile.setPmFinancialOrg(financialOrg);	

				if(financialOrg.getPmOrgProfiles()==null){
					financialOrg.setPmOrgProfiles(new ArrayList<PmOrgProfile>());
				}
				financialOrg.getPmOrgProfiles().add(orgProfile);
			}
		}
	}

	/**
	 * check if pmOrgProfile for factoring have been added before
	 * @param financialOrgId
	 * @return boolean
	 */
	private boolean checkFactoringOrg(Long financialOrgId){
		log.info("checkFactoringOrg");
		boolean isExisted = false;
		IOrgProfileSO orgSO  = null;
		try{
			// check DB
			ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
			// Filter By - Org Type
			searchBean.getDataFilterBeans().add(
							new SearchFilterBean("orgTypeCode.parameterCode",
									SearchOperator.OPERATOR_EQUALS.value(), OrganizationType.FACTORING_COMPANY.value()));
			searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmFinancialOrg.financialOrgId",
									SearchOperator.OPERATOR_EQUALS.value(), financialOrgId));
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
			

			
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileSearchBean(searchBean);
			orgSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgSO.searchOrgProfile(reqDTO);
			if(resDTO.getOrgProfileResultBean()!=null && resDTO.getOrgProfileResultBean().getResultList()!=null 
					&& !resDTO.getOrgProfileResultBean().getResultList().isEmpty()){
				isExisted = true;
			}
		}finally{
			orgSO = null;
		}
		log.info("isExisted: " + isExisted);
		
		return isExisted;
	}
	
	/* ------------------ PORLET -END ------------------  */
	
	/* ------------------ GENERAL TAB - START  ------------------  */
	
	/* Financial Service - START */
	
	/**
	 * add new financial service to list
	 */
	public void addFinancialService() {
		log.info("addFinancialService");
		if(this.getManagedBean().getFinancialOrg().getPmFinancialSvcs()!=null){
			if(this.getManagedBean().getFinancialOrg().getPmFinancialSvcs().size() > ProfileManagementConst.MAX_FINANCIAL_SERVICE){
				throw new EpException(MessageConst.ERROR_775200);
			}
		}	
		validateFinancialService();
		
		PmFinancialSvc pmFinancialSvc = new PmFinancialSvc();
		try {
			List<PmFinancialSvc> pmFinancialList = this.getManagedBean().getFinancialOrg().getPmFinancialSvcs();
			if (null == pmFinancialList) {
				pmFinancialList = new ArrayList<PmFinancialSvc>();
			}
			
			pmFinancialSvc.setServiceTypeCode(new PmParameter());
			pmFinancialList.add(pmFinancialSvc);
			this.getManagedBean().getFinancialOrg().setPmFinancialSvcs(pmFinancialList);
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * delete financial service from list
	 */
	public void deleteFinancialService(int index) {
		PmFinancialSvc pmFinancialSvc = getManagedBean().getFinancialOrg()
				.getPmFinancialSvcs().get(index);
		pmFinancialSvc.setDeleted(true);
		getManagedBean().getFinancialOrg()
		.getPmFinancialSvcs().remove(pmFinancialSvc);
	}

	
	/**
	 * update service type list based on selected value
	 * @param e
	 */
	public void updateService(AjaxBehaviorEvent e) {
		log.info("updateService");
		validateFinancialService();
		
//		Long finServiceId = (Long) e.getComponent().getAttributes().get("value");
//		System.out.println("finServiceId :" + finServiceId);
//
//		PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(finServiceId);
		PmParameter pmParameter = null;
		getManagedBean().setServiceTypes(new ArrayList<String>());
		Set<String> serviceTypeSet= new HashSet<String>();
		for(PmFinancialSvc finSev :getManagedBean().getFinancialOrg().getPmFinancialSvcs()){
			pmParameter = (PmParameter) serviceTypeMapper.get(finSev.getServiceTypeCode().getParameterId());
			serviceTypeSet.add(pmParameter.getParameterCode());
		}
		
		getManagedBean().getServiceTypes().addAll(serviceTypeSet);
	}
	
	/**
	 * validate financial service
	 */
	private void validateFinancialService(){
		log.info("validateFinancialService");
		//check if duplicated service added
		List <Long> tempServiceList = new ArrayList<Long>();
		for (PmFinancialSvc financialService : this.getManagedBean().getFinancialOrg().getPmFinancialSvcs()) {
			if(financialService.getServiceTypeCode()==null 
					|| financialService.getServiceTypeCode().getParameterId()==null){
				throw new EpException(MessageConst.ERROR_775201);
			}
			tempServiceList.add(financialService.getServiceTypeCode().getParameterId());
		}
		Set<Long> tempServiceSet = new HashSet<Long>(tempServiceList);
		if(tempServiceSet.size()!=tempServiceList.size()){
			throw new EpException(MessageConst.ERROR_775202);
		}
		
	}
	
	/**
	 * check if one of the active Financial Services is “Payment Bank”
	 * 
	 * **/
	public boolean checkFinancialServices(){
		boolean disabled = false;
		if(getManagedBean().getFinancialOrg().getPmFinancialSvcs() !=null &&
				!getManagedBean().getFinancialOrg().getPmFinancialSvcs().isEmpty()){
			for(PmFinancialSvc financialSvc:getManagedBean().getFinancialOrg().getPmFinancialSvcs()){
				if(ParameterConst.BANK_SERVICES_PAYMENT_BANK.equals(financialSvc.getServiceTypeCode().getParameterCode())){
					disabled = true;
					break;		
				}
			}
		}
		return disabled;
		
	}
	/* Financial Service - END */
	
	/* Factoring Bank - START */
	
	/**
	 * add new financial account to list
	 */
	public void addFinancialAcc() {
		log.info("addFinancialAcc");
		try {
			List<PmFinancialAcc> pmFinancialAccList = this.getManagedBean().getFinancialOrg().getPmFinancialAccs();
			if (pmFinancialAccList == null) {
				pmFinancialAccList = new ArrayList<PmFinancialAcc>();
			}
			PmFinancialAcc finAcc = new PmFinancialAcc();
			finAcc.setEffDate(new Date());
			finAcc.setPmPaymentBankOrg(new PmFinancialOrg());
			pmFinancialAccList.add(finAcc);
			this.getManagedBean().getFinancialOrg().setPmFinancialAccs(pmFinancialAccList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * delete financial acc from list
	 */
	public void deleteFinancialAcc(int index) {
		log.info("deleteFinancialAcc");	
		
		PmFinancialAcc pmFinancialAcc = getManagedBean().getFinancialOrg()
				.getPmFinancialAccs().get(index);
		pmFinancialAcc.setDeleted(true);
		getManagedBean().getFinancialOrg()
		.getPmFinancialAccs().remove(pmFinancialAcc);
	}
	
	/**
	 * define bank details editable mode
	 * @param effDate
	 * @return boolean
	 */
	public boolean editableBankDet(Date effDate){
		log.info("editableBankDet");
		Date today = new Date();
		if(effDate!=null){
	    	if(today.compareTo(effDate)>0){
	    		//today > orgDate
	    		return false;
	    	}else if(today.compareTo(effDate)==0){
	    		return false;
			}else{
	    		return true;
	    	}
		}else{
			return true;
		}
	}
	public void getBankCode(Long financialOrgId){
		
		log.info("Org ProfileID-------->"+financialOrgId);
		IFinancialOrgSO financialOrgSO = null;
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setFinancialOrgId(financialOrgId);
		financialOrgSO = (IFinancialOrgSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO  = financialOrgSO
				.retrieveFinancialSvcByFinancialOrgId(reqDTO);

		PmFinancialSvc pmFinancialSvc = resDTO.getPmFinancialSvc();
		if(resDTO.isSuccessful()){
			if(null!=pmFinancialSvc){
			log.info("BANK CODE"+pmFinancialSvc.getBankCode()
					 + "|" + pmFinancialSvc.getFinancialSvcId());
			for(PmFinancialAcc financialAcc:getManagedBean().getFinancialOrg().getPmFinancialAccs()){
			    if(financialAcc.isBrandNew()){
			     financialAcc.setPmFinancialOrg(this.getManagedBean().getFinancialOrg());			   
			     financialAcc.setBankCode(pmFinancialSvc.getBankCode());
			    }
			   }
		}
		
		}
		
	}
	/* Factoring Bank - END */
	
	
	
	
	/* ------------------ GENERAL TAB - END  ------------------  */	

	
	
	/* ------------------ SEARCH - START  ------------------  */
	
	public IPagingDataModel<PmFinancialOrg> getSearchFinancialOrg() {
		return searchFinancialOrg;
	}

	public void setSearchFinancialOrg(
			IPagingDataModel<PmFinancialOrg> searchFinancialOrg) {
		this.searchFinancialOrg = searchFinancialOrg;
	}

	
	/**
	 * reset search bean fields
	 */
	public void resetFinancialOrgSearchBean() {
		log.info("resetFinancialOrgSearchBean");
		getManagedBean().setLazy(false);
		this.getManagedBean().setFinancialOrgSearchBean(
				new FinancialOrgSearchBean());
		searchFinancialOrg = new FinancialOrgPagingDataModel();
	}
	
	private ISearchBean<PmFinancialOrg> getSearchBeanData(
			ISearchBean<PmFinancialOrg> searchBean) {
		log.info("getSearchBeanData - Start");
		FinancialOrgSearchBean fSearchBean = getManagedBean()
				.getFinancialOrgSearchBean();
		if (fSearchBean.getOrgName() != null
				&& fSearchBean.getOrgName().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("finOrgName",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getOrgName(), true));
		}

		if (fSearchBean.getServiceType() != null
				&& fSearchBean.getServiceType().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"pmFinancialSvcs.serviceTypeCode.parameterCode",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getServiceType()));
		}	
		
		if(fSearchBean.getOrgStatus() != null && fSearchBean.getOrgStatus().trim().length() > 0){
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getOrgStatus()));
		}else{
			List<String> activeAndInActiveStatus = new ArrayList<String>();
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_INACTIVE);
			activeAndInActiveStatus.add(ParameterConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("recordStatusFlg",
							SearchOperator.OPERATOR_IN.value(), 
							activeAndInActiveStatus
			));
		}
		
		if (fSearchBean.getRegNumber() != null
				&& fSearchBean.getRegNumber().trim().length() > 0) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("bizRegNumber",
							SearchOperator.OPERATOR_LIKE.value(),
							getManagedBean().getFinancialOrgSearchBean()
									.getRegNumber(), true));
		}

		log.info("getSearchBeanData - End");
		return searchBean;
	}

	private class FinancialOrgPagingDataModel extends
			PagingDataModel<PmFinancialOrg> {

		private static final long serialVersionUID = 1L;

		public FinancialOrgPagingDataModel() {

			super("searchFinancialOrg", getManagedBean().isLazy());
		}

		public ISearchResultBean<PmFinancialOrg> pageChangeListener(
				ISearchBean<PmFinancialOrg> searchBean) {

			IFinancialOrgSO financialOrgSO;
			try {

				FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
				ISearchBean<PmFinancialOrg> searchBeanData = getSearchBeanData(searchBean);
				reqDTO.setFinancialSearchBean(searchBeanData);
				financialOrgSO = (IFinancialOrgSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IFinancialOrgSO.class);
				FinancialOrgResDTO resDto = financialOrgSO
						.searchFinancialOrgData(reqDTO);

				if (resDto.isSuccessful()) {
					return resDto.getFinaceialOrgResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	public void searchFinancialOrg() {
		getManagedBean().setLazy(true);
		searchFinancialOrg.reloadData();
	}	
	
	/* ------------------ SEARCH - END  ------------------  */
	
	
	private ISearchBean<PmFinancialOrg> getFinOrgSearchBean(String componentId) {		
		ISearchBean<PmFinancialOrg> finOrgSearchBean = super.createSearchBean(
				PmFinancialOrg.class, componentId);
		return finOrgSearchBean;
	}
	
	public void retrievePmDistrict(PmDistrict pmDistrict) {
		if(pmDistrict != null && pmDistrict.getDistrictId() != null){
			PmDistrict tempDistrict = PmCommonUtil.retrievePmDistById(pmDistrict.getDistrictId());				
			getManagedBean().getPmAddress().setPmDistrict(tempDistrict);
		}		
		getManagedBean().getPmAddress().setPmCity(new PmCity());
	}
	
	public void retrievePmDivision(PmDivision pmDivision) {
		if(pmDivision != null && pmDivision.getDivisionId() != null){
			PmDivision tempDivision = PmCommonUtil.retrievePmDivisionById(pmDivision.getDivisionId());			
			getManagedBean().getPmAddress().setPmDivision(tempDivision);
		}
		getManagedBean().getPmAddress().setPmDistrict(new PmDistrict());
		getManagedBean().getPmAddress().setPmCity(new PmCity());
	}
	
	public void retrievePmCity(PmCity pmCity) {
		if(pmCity != null && pmCity.getCityId() != null){
			PmCity tempCity = PmCommonUtil.retrievePmCityById(pmCity.getCityId());			
			getManagedBean().getPmAddress().setPmCity(tempCity);
		}
	}
}
