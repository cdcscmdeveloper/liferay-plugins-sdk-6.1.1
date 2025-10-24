package my.ep.web.backingbean.sm.registration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.common.ssm.SsmReqDTO;
import my.ep.dto.sm.common.ssm.SsmResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonBlacklist;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.govtsellerregistration.GovtSellerRegistrationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
//#13320 - Rahim
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.util.CustomUtils;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBankInformationBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
//#28895_35501 End

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends BaseBackingBean<RegistrationManagedBean>{

	private static final long serialVersionUID = 1L;
 
	public static final String SUPPLIER = "SUP";

	private final static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public final static String newICMasking = "999999999999";
	private static String accountRegistration = "/xhtml/sm/onlinereg/registrationInfo.xhtml?faces-redirect=true";
	//16052019 - SSM INTEGRATION CR - Fahmi
	private static String ssmAccountCheck = "/xhtml/sm/shared/onlinereg/ssmAcountCheck.xhtml?faces-redirect=true";
	private static String signUp = "/xhtml/sm/onlinereg/signUp.xhtml?faces-redirect=true";
	private static String firstUserRegistration = "/xhtml/sm/onlinereg/firstUserRegistration.xhtml?faces-redirect=true";
	private static String termAndCondition = "/xhtml/sm/onlinereg/signUpTermCondition.xhtml?faces-redirect=true";
	private static String basicAccDeactivation = "/xhtml/sm/basicaccountdeactivation/basicAccountDeactivationDet.xhtml";
	private static String acknowledgement = "/xhtml/sm/onlinereg/outputMsg.xhtml";
	private static String g2gRegistration = "/xhtml/sm/govtSellerRegistration/preRenderGovtSeller.xhtml?faces-redirect=true";
	
	private static String defaultAccPurpose = "P";
	private static Map<Long, Object> identityType;
	private static Map<Long, PmCertBody> profCertBodyMap;
	private static List<String> sabahSarawakRemoveWhiteSpaceList = Arrays.asList("D", "E", "G", "H");
	private static List<String> rosRemoveWhiteSpaceList = Arrays.asList("O","M","P");
	private static List<String> regNoValidityList = Arrays.asList("O","M","R","A"); //#35581 START
	private static List<String> validators = Arrays.asList("^[a-zA-Z\\-\\/\\(\\)]+$","[a-zA-Z]+-[a-zA-Z]+","\\d+",
			"[a-zA-Z0-9]+","^(?=[a-zA-Z])(?=[0-9])[a-zA-Z0-9\\/\\-\\(\\)]*[\\/\\-\\(\\)].*[a-zA-Z0-9\\/\\-\\(\\)]*$",
			"^[a-zA-Z/\\-()]*[a-zA-Z]$","^[a-zA-Z0-9/\\-()]*[a-zA-Z0-9]$","^[a-zA-Z0-9/\\-()]*$"); //#35581 END
	//16052019 - SSM INTEGRATION CR - Fahmi
	boolean fromtncProceed = false;
	boolean fromBankAccount = false;
	
	public RegistrationBackingBean() {	
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		if (pmUser != null && pmUser.getUserId() != null) {
			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	/**
	 * Prepare the required information and redirect user to the Registration form
	 *
	 * @return String
	 */
	public String proceedToCompanyInfo() {
		this.getManagedBean().setSmSuppOwners(new ArrayList<SmSupplierOwner>()); //#37900
		if (this.getManagedBean().getSmSupp() == null) {
			//default to malaysia
			PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			PmCountry defaultPersonnelNationality = (PmCountry) defaultCountry.cloneEntity();
			PmCountry defaultResAddCountry = (PmCountry) defaultCountry.cloneEntity();
			PmCountry defaultPermAddCountry = (PmCountry) defaultCountry.cloneEntity();
			
			this.getManagedBean().setSmSupp(new SmSupplier());
			
			SmSupplier smSupp = this.getManagedBean().getSmSupp();
			smSupp.setSmAppls(new ArrayList<SmAppl>());
			smSupp.setSmSupplierOwners(new ArrayList<SmSupplierOwner>());

			this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			this.getManagedBean().setSmAppl(new SmAppl());
			getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			getManagedBean().getSmSuppOwner().setNationality(new PmCountry());
			ownerNationalityOnChange();
			
			SmAppl smAppl = this.getManagedBean().getSmAppl();
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
			smAppl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
			this.getManagedBean().setSmPersonnel(new SmPersonnel());
			this.getManagedBean().getSmPersonnel().setPmCountry(defaultPersonnelNationality); //personnelNationality
			
			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().getSmBusinessAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
			this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			//this.getManagedBean().getSmBusinessAdd().setPmDivision(new PmDivision());//13320

			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmResAdd().setPmCountry(defaultResAddCountry);
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
			//this.getManagedBean().getSmResAdd().setPmDivision(new PmDivision());//13320


			this.getManagedBean().setSmPermenantAdd(new SmAddress());
			this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
//			this.getManagedBean().getSmPermenantAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmPermenantAdd().setPmCountry(defaultPermAddCountry);
			this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
			this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
			//this.getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());//13320

			this.getManagedBean().setSameAddress(false);
			
			smSupp.setSmSupplierBusinesses(new ArrayList<SmSupplierBusiness>());
			
			this.retrievePmCountryList();
			this.populateStateByAddType("permenant", false);
			this.populateStateByAddType("residential", false);
			this.populateStateByAddType("business", false);
			this.getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
			this.retrieveIdentityTypeMap();
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());			
		}
		
		//GST 
		getManagedBean().setSmGstSupplier(new SmGstSupplier());
		prepareGstDoc();
		
		//harris
		getManagedBean().setSmSstSupplier(new SmSstSupplier());
			
		this.getManagedBean().getSmAppl().setApplType(this.getManagedBean().getApplType());
		getIndividualServiceType();
		//16052019 - SSM INTEGRATION CR - Fahmi
		if(this.getManagedBean().isSsmIntegrationActive() && this.getManagedBean().isValidateWithSSM() && !this.getManagedBean().isCompanyValidWithSSM()){
			return ssmAccountCheck;
		}else{
			if(!this.getManagedBean().isSsmIntegrationActive()){
				PmParameter identificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);

				SmSupplierOwner smSupplierOwner = new SmSupplierOwner();
				smSupplierOwner.setOwnerName("");
				PmCountry indNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				smSupplierOwner.setNationality(indNationality);					
				smSupplierOwner.setIdentificationNo(smSupplierOwner.getIdentificationNo());
				smSupplierOwner.setIdentificationType(identificationType);
				smSupplierOwner.setIdentificationTypeId(identificationType.getParameterId());
				//SSM CHANGES #27214
				smSupplierOwner.setTempCountryId(indNationality.getCountryId());
				smSupplierOwner.setNationalityNull(true);
				smSupplierOwner.setShowTextField(true);
				this.getManagedBean().getSmSuppOwners().add(smSupplierOwner);
			}
			return accountRegistration;
		}
		
	}
	
	//16052019 - SSM INTEGRATION CR 
	public void setHeaderName(){
		// 27691 - Start
		if(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN.equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI.equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN.equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2.equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())){
			this.getManagedBean().setHeaderName(SmCommonUtil.getMessage("lbl.sm.directorinfo", null));
		//Display both director and owner
		}else if(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP.equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())){
			this.getManagedBean().setHeaderName(SmCommonUtil.getMessage("lbl.sm.llp.ownerinfo", null));
			this.getManagedBean().setShowDirectorInOwner(true);
		}
		else{
			this.getManagedBean().setHeaderName(SmCommonUtil.getMessage("lbl.sm.ownerinfo", null));
		}
		//#27691 - End
	}
	public String validateWithSSM() {
		this.getManagedBean().setSmSuppOwners(new ArrayList<SmSupplierOwner>()); //#37876
		this.getManagedBean().setHideButton(false);
		String regNo = this.getManagedBean().getSmSupp().getRegNo();
		//30987 - start
		regNo = regNo.trim();
		this.getManagedBean().getSmSupp().setRegNo(regNo);
		//30987 - end
		String businessType = this.getManagedBean().getSmSupp().getBusinessType();
		String checkIcSSM = this.getManagedBean().getRegIcCheckSSM();
		//only business type ROB & ROC will check with SSM
		
		//29114
		//CR 27382: Static Code Analysis Start
		if (this.getManagedBean().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) && (this.getManagedBean().getSmAppl().getSupplierType() == null || "".equalsIgnoreCase(this .getManagedBean().getSmAppl().getSupplierType()))) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage( "lbl.sm.mofsuppliertype", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
		//CR 27382: Static Code Analysis End
		if (this.getManagedBean().getSmSupp().getBusinessType() == null || "".equalsIgnoreCase(this.getManagedBean().getSmSupp().getBusinessType())) {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] {ResourceBundleUtils.getMessage("lbl.sm.businesscomptype", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		if (this.getManagedBean().getSmSupp().getRegNo() == null || "".equalsIgnoreCase(this.getManagedBean().getSmSupp().getRegNo())) {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		//#35581
		if (StringUtils.isNotBlank(getManagedBean().getSmSupp().getRegNo())
				&& !validateString(getManagedBean().getSmSupp().getRegNo())) {
			if (sabahSarawakRemoveWhiteSpaceList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723100,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (regNoValidityList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723101,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}
		PmParameter identificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);

		if(businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupp().getRegNo(),this.getManagedBean().getSmSupp().getBusinessType())){
			

			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setSmSupplierOwner(getManagedBean().getSmSuppOwner());
			ssmReqDTO.setSmBusinessAdd(this.getManagedBean().getSmBusinessAdd());
			ssmReqDTO.setSmResAdd(this.getManagedBean().getSmResAdd());
			ssmReqDTO.setSmPermenantAdd(this.getManagedBean().getSmPermenantAdd());
			ssmReqDTO.setRegNo(regNo);
			//30987
			ssmReqDTO.getRegNo().trim();
			ssmReqDTO.setBusinessType(businessType);
			ssmReqDTO.setSmAppl(this.getManagedBean().getSmAppl());
			//CR 27382: Static Code Analysis Start
			ssmReqDTO.setBasicAcc(false);
			if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
				ssmReqDTO.setBasicAcc(true);
			}
			//CR 27382: Static Code Analysis End
			//27329
			ssmReqDTO.setFromRegistration(true);
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			this.getManagedBean().setCompanyValidWithSSM(false);
			this.getManagedBean().getDummy().clear();
			if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
				
				//CR 27382: Static Code Analysis End				
				this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
				String status = ssmResDTO.getStatusFromSSM().toUpperCase();
				switch(status){
					case "L"://27382
						throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});			
					case "T":
						throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});			
					case "B": //27382 - Dissolved Conversion to LLP
						if(businessTypeIsROC(businessType)){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
						}
						if(businessTypeIsROB(businessType)){
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
						}
					case "W":
					case "D":
					case "R":
					case "C":
					case "X":
					case "Y":
					//27382 - start
						throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					default:
						break;
				}
				//16052019 - SSM INTEGRATION CR
			}
			
			
				if(this.getManagedBean().getRegIcCheckSSM() == null || this.getManagedBean().getRegIcCheckSSM().isEmpty()){
					if(businessTypeIsROB(getManagedBean().getSmSupp().getBusinessType())){
						throw new EpException(MessageConst.ERROR_100051,new String[]{ResourceBundleUtils.getMessage("lbl.sm.register.owner.newic",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()),ResourceBundleUtils.getMessage("lbl.sm.register.owner",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}else if(businessTypeIsROC(getManagedBean().getSmSupp().getBusinessType())){
						throw new EpException(MessageConst.ERROR_100051,new String[]{ResourceBundleUtils.getMessage("lbl.sm.register.owner.newic",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()),ResourceBundleUtils.getMessage("lbl.sm.register.ownerdirector",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					
				} else if(ssmReqDTO.isBasicAcc()){ ///FOR BASIC ONLY
					SmPersonnel checkRegistrationIcOwnerSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(checkIcSSM, ssmResDTO.getSmPersonnelOwnersAll(), "identificationNo");
					if(checkRegistrationIcOwnerSSM == null){
						SmPersonnel checkRegistrationIcDirectorSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(checkIcSSM, ssmResDTO.getSmPersonnelDirector(), "identificationNo");
						if(checkRegistrationIcDirectorSSM == null){
							if(businessTypeIsROB(getManagedBean().getSmSupp().getBusinessType())){
								throw new EpException(MessageConst.ERROR_722678,new String[] {ResourceBundleUtils.getMessage("lbl.sm.register.owner",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							} else if(businessTypeIsROC(getManagedBean().getSmSupp().getBusinessType())){
								throw new EpException(MessageConst.ERROR_722678,new String[] {ResourceBundleUtils.getMessage("lbl.sm.register.ownerdirector",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}
					}else{
						if(checkRegistrationIcOwnerSSM.isForeigner()){
							//change to new message follow redmine #26977
							throw new EpException(MessageConst.ERROR_722679);
						}						
					}
				}else{//FOR MOF
					SmPersonnel checkRegistrationIcOwnerSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(checkIcSSM, ssmResDTO.getSmPersonnelOwnersAll(), "identificationNo");
					if(checkRegistrationIcOwnerSSM == null){
						SmPersonnel checkRegistrationIcDirectorSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(checkIcSSM, ssmResDTO.getSmPersonnelDirector(), "identificationNo");
						if(checkRegistrationIcDirectorSSM == null){
							if(businessTypeIsROB(getManagedBean().getSmSupp().getBusinessType())){
								throw new EpException(MessageConst.ERROR_722678,new String[] {ResourceBundleUtils.getMessage("lbl.sm.register.owner",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							} else if(businessTypeIsROC(getManagedBean().getSmSupp().getBusinessType())){
								throw new EpException(MessageConst.ERROR_722678,new String[] {ResourceBundleUtils.getMessage("lbl.sm.register.ownerdirector",ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}
					}
				}
				
				//end check registration ic with ssm #27148
				
			// #38143 - Start
			validateExistingBusinessRegNo();
			// #38143 - End
				SmCommonUtil.ssmIdentificationTypeError(ssmResDTO); //#29582
				this.getManagedBean().setCompanyValidWithSSM(true);
				this.getManagedBean().setValidateWithSSM(true);
				this.getManagedBean().setCompanyValidWithSSM(true);
				this.getManagedBean().setSmSuppOwners(ssmResDTO.getSmSupplierOwners());
				this.getManagedBean().setSmPersonnelAll(ssmResDTO.getSmPersonnelAll());
				this.getManagedBean().setSmEquityCompany(ssmResDTO.getSmEquityCompanys());
				this.getManagedBean().setSmSupplierBranches(ssmResDTO.getSmSupplierBranches());
				//set country desc
				/*for(SmSupplierOwner smSupplierOwner : ssmResDTO.getSmSupplierOwners()){
					if(smSupplierOwner.getNationality() != null && !"".equalsIgnoreCase(smSupplierOwner.getNationality().getCountryCodeSSM())){
						PmCountry pmCountry = SmCommonUtil.retrievePmCountryByCountryCodeSSM(smSupplierOwner.getNationality().getCountryCodeSSM());
						smSupplierOwner.setNationality(pmCountry);
					}
					smSupplierOwner.setIdentificationNo(smSupplierOwner.getIdentificationNo());
					smSupplierOwner.setIdentificationType(identificationType);
					smSupplierOwner.setIdentificationTypeId(identificationType.getParameterId());
					this.getManagedBean().setSmSuppOwner(smSupplierOwner);
				}*/
				//to set defaul value ic and country if supplier owner not exist in ssm;
				//- if no Equity Owner (individual), system to check at least one Director name is populated from SSM.
				//- if no Equity Owner (individual) AND no Director, system to prompt error below;
				if(ssmResDTO.getSmSupplierOwners().size()<=0 && businessTypeIsROB(this.getManagedBean().getSmSupp().getBusinessType())){
					SmSupplierOwner smSupplierOwner = new SmSupplierOwner();
					smSupplierOwner.setOwnerName("");
					PmCountry indNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
					smSupplierOwner.setNationality(indNationality);					
					smSupplierOwner.setIdentificationNo(smSupplierOwner.getIdentificationNo());
					smSupplierOwner.setIdentificationType(identificationType);
					smSupplierOwner.setIdentificationTypeId(identificationType.getParameterId());
					smSupplierOwner.setShowTextField(true);
					ssmResDTO.getSmSupplierOwners().add(smSupplierOwner);
				}else{
					//if ssm, disable role.
					this.getManagedBean().setDisableRole(true);
				}
				//24819
				this.getManagedBean().getSmSupp().setSsmNewRegNo(ssmResDTO.getSsmNewRegNo());
				this.getManagedBean().setSmSuppOwners(ssmResDTO.getSmSupplierOwners());
				this.getManagedBean().setSmPersonnelDirector(ssmResDTO.getSmPersonnelDirector());
				this.getManagedBean().setSmAppl(ssmResDTO.getSmAppl());
				this.getManagedBean().setSmBusinessAdd(ssmResDTO.getSmAddress());
				this.getManagedBean().setStatusFromSSM(ssmResDTO.getStatusFromSSM());
				this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
				this.getManagedBean().setSmPersonnelOwners(ssmResDTO.getSmPersonnelOwners());
				setPersonnelToItem();
				
				//get detail business nature from ep
				//List<SmSupplierBusiness> smSupplierBusinesses = ssmResDTO.getSmSupplierBusinessList();
				/*for(SmSupplierBusiness smSupplierBusiness : ssmResDTO.getSmSupplierBusinessList()){
					PmBusinessNature pmBusinessNature = SmCommonUtil.retrieveBusinessNatureByLevelCode(smSupplierBusiness.getPmBusinessNature().getLevelCode());
					smSupplierBusiness.setPmBusinessNature(pmBusinessNature);
				}*/				
				this.getManagedBean().getSmSupp().setSmSupplierBusinesses(ssmResDTO.getSmSupplierBusinessList());
				this.getManagedBean().setSmSuppOwners(ssmResDTO.getSmSupplierOwners());
				this.getManagedBean().getDummy().add("");//use to display data file ssmcheck
				this.populateStateByAddType("business", false);
				this.populateDistrictByAddType("business", false);
				//this.populateCityByAddType("business", false);
				
				//16052019 - SSM INTEGRATION CR 
				//CR 27382: Static Code Analysis Start
				/*Asal
				if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP)){
					if(this.getManagedBean().getSmSuppOwners().size() > 1){
						throw new EpException(MessageConst.ERROR_721077);
					}					
				} else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP)){
					if(this.getManagedBean().getSmSuppOwners().size() <= 1){
						throw new EpException(MessageConst.ERROR_721077);
					}
				}
				
				if(!businessTypeIsROB(this.getManagedBean().getSmSupp().getBusinessType())){
					if(ssmResDTO.getSmSupplierOwners().size() <= 0 && ssmResDTO.getSmPersonnelDirector().size() <= 0){
						this.getManagedBean().setHideButton(true);
						throw new EpException(MessageConst.ERROR_721080);
					}
				}
				*/
				if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP) && this.getManagedBean().getSmSuppOwners().size() > 1) {
					throw new EpException(MessageConst.ERROR_721077);				
				} else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP) && this.getManagedBean().getSmSuppOwners().size() <= 1 ){					
					throw new EpException(MessageConst.ERROR_721077);					
				}
				
				if(!businessTypeIsROB(this.getManagedBean().getSmSupp().getBusinessType()) && ssmResDTO.getSmSupplierOwners().size() <= 0 && ssmResDTO.getSmPersonnelDirector().size() <= 0 ){
					this.getManagedBean().setHideButton(true);
					throw new EpException(MessageConst.ERROR_721080);
				}
				
				if(!this.getManagedBean().isCompanyValidWithSSM()){
					throw new EpException(MessageConst.ERROR_721078);
				}
			
			return ssmAccountCheck;
			
		}else{
			// #16052019 - SSM INTEGRATION CR START
			//set default for owner
			SmSupplierOwner smSupplierOwner = new SmSupplierOwner();
			smSupplierOwner.setOwnerName("");
			PmCountry indNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			smSupplierOwner.setTempCountryId(indNationality.getCountryId());
			smSupplierOwner.setNationalityNull(true);
			smSupplierOwner.setNationality(indNationality);					
			smSupplierOwner.setIdentificationNo(smSupplierOwner.getIdentificationNo());
			smSupplierOwner.setIdentificationType(identificationType);
			smSupplierOwner.setIdentificationTypeId(identificationType.getParameterId());
			smSupplierOwner.setShowTextField(true);
			this.getManagedBean().getSmSuppOwners().add(smSupplierOwner);
			// #16052019 - SSM INTEGRATION CR END
	    	return accountRegistration;
			
		}
		
	}
	/**
	 * Prepare the required information and redirect user to the first user registration form
	 *
	 * @return String
	 */
	public String proceedToFirstUserRegistration() {
		
		//Validate GST No
		validateGstRegNo();
		
		validateBusinessType();
		
		//harris
		validateSstRegNo();
		validateSstServRegNo();
		
		//#27356
		if (StringUtils.isBlank(this.getManagedBean().getSmBusinessAdd().getMapLatitude())
				|| StringUtils.isBlank(this.getManagedBean().getSmBusinessAdd().getMapLongitude())) {
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.coordinate.required.msg",
					new String[] { SmCommonUtil.getMessage("lbl.sm.coordinate.company.info", null) }));
		}
		
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);

		}
		
		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);

		}
		//#16052019 - SSM INTEGRATION CR  - Check validation. Start		
		
		if(this.getManagedBean().isCompanyValidWithSSM()){
			if(this.getManagedBean().getSmSuppOwners() != null){
				for(SmSupplierOwner suppOwner:this.getManagedBean().getSmSuppOwners()){
					//SmSupplierOwner suppOwner = this.getManagedBean().getSmSuppOwner();
					if(SmCommonUtil.isSupplierOwnerIndividualAcc(suppOwner.getIdentificationType().getParameterCode(),suppOwner.getIdentificationNo(),
							my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)){
						//throw new EpException(MessageConst.ERROR_SSM_70062);
						//#27315
						throw new EpException(MessageConst.ERROR_721050);
					}
					validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
					/*if(suppOwner.getIdentificationType().getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
						if(getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
							//MOF
							SmCommonUtil.getInstance().verifyWithMyIdentity(suppOwner.getOwnerName(), suppOwner.getIdentificationNo(), 
									null, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN,this.getManagedBean().isCompanyValidWithSSM());
						}else{
							//Basic
							SmCommonUtil.getInstance().verifyOwnerWithMyIdentity(suppOwner.getOwnerName(), suppOwner.getIdentificationNo(), 
									null, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN, null,this.getManagedBean().isCompanyValidWithSSM());
						}
					}*/
					//set ssm updated name
					if(suppOwner.isShowNameTextField()){
						SmPersonnel personnel = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(suppOwner.getIdentificationNo(), this.getManagedBean().getSmPersonnelAll(), "identificationNo");
						if(personnel != null){
							personnel.setName(suppOwner.getOwnerName());
						}
					}
				}
				setPersonnelToItem();
				//Check Match identification with nationality
				//check owner
				//#29582 #30064 start
				for(SmSupplierOwner owner:this.getManagedBean().getSmSuppOwners()){
					if(owner.getNationality() == null){
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}else if(owner.getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_IC) 
							&& !owner.getNationality().getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)
							&& owner.getIdentityResidentStatus() !=null && (owner.getIdentityResidentStatus().equals("M") 
									&& owner.getIdentityResidentStatus().equals("P"))){
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}else if(owner.getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_PASSPORT) 
							&& ((owner.getNationality().getCountryCode()
									.equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) && !owner
									.getIdentificationNo().startsWith("K", 0)) || owner.getNationality()    //#30064
									.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE))) {
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}
					checkBlacklistedOwners(owner.getIdentificationNo(), true); //#26238 - 39069, #39355
				}
				//Check Director
				for(SmPersonnel director:this.getManagedBean().getSmPersonnelDirector()){
					if(director.getPmCountry() == null){
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}else if(director.getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_IC)
							&& !director.getPmCountry().getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)
							&& director.getIdentityResidentStatus() !=null && (director.getIdentityResidentStatus().equals("M") 
									&& director.getIdentityResidentStatus().equals("P"))){
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}else if (director.getIdentificationType().getParameterCode()
							.equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_PASSPORT)
							&& ((director.getPmCountry().getCountryCode()
									.equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) && !director
									.getIdentificationNo().startsWith("K", 0)) || director.getPmCountry() //#30064
									.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE))) {
						throw new EpException(MessageConst.ERROR_SSM_70063);
					}
					checkBlacklistedOwners(director.getIdentificationNo(), true); //#26238 - 39069, #39355
				}
			}//#29582 #30064 end
		}else{
			//STARt 27802
			/*if(getManagedBean().getSmPersonnel()!=null){
				getManagedBean().getSmPersonnel().setDirector(this.getManagedBean().isOwnerIsDirector());
				getManagedBean().getSmPersonnel().setEquityOwner(this.getManagedBean().isOwnerIsEquity());
			}*/
			//END 27802
			SmSupplierOwner suppOwner = this.getManagedBean().getSmSuppOwner();
			if(this.getManagedBean().getSmSuppOwners().size()>0){
				suppOwner = this.getManagedBean().getSmSuppOwners().get(0);
			}
			//START 16052019 - SSM INTEGRATION CR - Validation for owner name n ident no 
			if (suppOwner.getOwnerName() == null || "".equalsIgnoreCase(suppOwner.getOwnerName())) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.ownername", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			
			if (suppOwner.getOwnerName() == null || "".equalsIgnoreCase(suppOwner.getOwnerName())) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.individual.newicno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			if(this.getManagedBean().isShowDirector() || this.getManagedBean().isShowOwner()){
				if (!this.getManagedBean().isOwnerIsDirector() && !this.getManagedBean().isOwnerIsEquity()) {
					throw new EpException(MessageConst.ERROR_100022,
							new String[] {ResourceBundleUtils.getMessage("lbl.sm.person.composition.role", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			
			//END 16052019 - SSM INTEGRATION CR - Validation for owner name n ident no 
			
			//27537 : Disciplinary Action - start - fixed together for 27536 & 27520
			if(this.getManagedBean().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC) || 
				this.getManagedBean().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) ){
				checkBlacklistedOwners(suppOwner.getIdentificationNo(), false); //#26238 - 39069, #39355
			}
			//27537 : Disciplinary Action - end
			
			if(SmCommonUtil.isSupplierOwnerIndividualAcc(suppOwner.getIdentificationType().getParameterCode(),suppOwner.getIdentificationNo(),
					my.ep.constant.sm.SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)){
				throw new EpException(MessageConst.ERROR_721050);
			}
			validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
			if(suppOwner.getIdentificationType().getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
				if(getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
					//MOF
					SmCommonUtil.getInstance().verifyWithMyIdentity(suppOwner.getOwnerName(), suppOwner.getIdentificationNo(), 
							null, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN);
				}else{
					//Basic
					SmCommonUtil.getInstance().verifyOwnerWithMyIdentity(suppOwner.getOwnerName(), suppOwner.getIdentificationNo(), 
							null, my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN, null);
				}
			}
		}
		listenerChangeRole();
		//#16052019 - SSM INTEGRATION CR  - Check validation. End
		
		checkExistingBusinessRegNo();
		
		
		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());

		//check the professional body if supplier type is consultant
		if(this.getManagedBean().getSmAppl().getSupplierType() != null){
			if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
				if(this.getManagedBean().getSmAppl().getSmAssociationBodies() == null || this.getManagedBean().getSmAppl().getSmAssociationBodies().size() < 1){
					throw new EpException(MessageConst.ERROR_721033);
				}else{
					checkDuplicatedProfessionalBody();
				}
			}
		}
		
		personnelNationalityOnChange();
		
		//set all business network to false, if do not meet the criterias
		SmCompanyBasic compBasic = this.getManagedBean().getSmAppl().getSmCompanyBasic();
		if(!this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) 
				|| !this.getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
			compBasic.setWithFederal(false);
			compBasic.setWithGlc(false);
			compBasic.setWithOthers(false);
			compBasic.setWithState(false);
			compBasic.setWithStatutory(false);
		}else{
			if(!compBasic.isWithFederal() && !compBasic.isWithGlc() && !compBasic.isWithOthers() && !compBasic.isWithState() && !compBasic.isWithStatutory()){
				throw new EpException(MessageConst.ERROR_721036);
			}
		}
		
		if(this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)){
			compBasic.setWithFederal(true);
		} 
		
		if(this.getManagedBean().getSmPersonnel().getTitle() == null) {
			this.getManagedBean().getSmPersonnel().setTitle(new PmSalutation());
		}
		//12492 - START
		return firstUserRegistration;	
		//12492 - END
	}
	
	//#26238 - 39069, #39355
	/**
	 * Check Disciplinary Action for blacklisted owners/directors
	 * @param identificationNo
	 */
	private void checkBlacklistedOwners(String identificationNo, boolean checkDate){
		if (StringUtils.isEmpty(identificationNo)){
			return;
		}
		//32851 - start
		SmPersonBlacklist ban = SmServiceLookup.getBlacklistDirectorSO()
				.retrieveValidBlacklist(identificationNo, checkDate).getPersonBlacklist(); // #39355
		if(ban != null){
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.not.allowed.to.proceed.due.to.disciplinary.action",null));
		}
		//32851 - end
	}

	/**
	 * redirect user to the main page of supplier registration
	 *
	 * @return String
	 */
	public String returnToSignUp() {
		return signUp;
	}

	/**
	 * redirect user to the supplier registration - Company Info
	 *
	 * @return String
	 */
	public String returnToCompanyInfo() {
		return accountRegistration;
	}
	
	/**
	 * add new row to the Professional Body
	 */
	public void addProfessionalBody(){
		if(this.getManagedBean().getSmAppl().getSmAssociationBodies() == null){
			this.getManagedBean().getSmAppl().setSmAssociationBodies(new ArrayList<SmAssociationBody>());
			this.retrieveProfBody();
		}
		SmAssociationBody assocBody = new SmAssociationBody();
		assocBody.setPmCertBody(new PmCertBody());
		this.getManagedBean().getSmAppl().getSmAssociationBodies().add(assocBody);
	}
	
	/**
	 * remove selected Professional Body
	 */
	public void removeProfessionalBody(){
		if(this.getManagedBean().getSelectedAssociationBodies() != null && this.getManagedBean().getSelectedAssociationBodies().length > 0){
			for (SmAssociationBody suppAssocBody : this.getManagedBean().getSelectedAssociationBodies()) {
				this.getManagedBean().getSmAppl().getSmAssociationBodies().remove(suppAssocBody);
			}
		}
	}

	/**
	 * Submit Supplier Registration
	 * - Perform business checking and save.
	 * @return String
	 */
	public String submitRegistration() {
		
		//#33850 START
		if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)){
			this.getManagedBean().setSameAsOwner(true);
		}
		//#33850 END
		
		setAdminSameAsOwner();
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();
		
		//only use for individual flag
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		//#16052019 - SSM INTEGRATION CR
		PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		smAppl.getSmPersonnels().clear();
		if(this.getManagedBean().getSmSuppOwners().size()>0){
			smSuppOwner = this.getManagedBean().getSmSuppOwners().get(0);//save only one tu supp owner
		}
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
		SmAddress smResAdd = this.getManagedBean().getSmResAdd();
	
		if(!smPersonnel.getEmail().equals(this.getManagedBean().getConfirmEmail())){
			throw new EpException(MessageConst.ERROR_721020);
		}
		if(!smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
			//check business registration no with supplier Type;
			checkExistingBusinessRegNo();
		}else{
			//check whether identification code already registered any business type
			if(SmCommonUtil.isSupplierOwnerIndividualAcc(smSuppOwner.getIdentificationType().getParameterCode(),
					smSupp.getRegNo(),null)){
				throw new EpException(MessageConst.ERROR_721050);
			}
			
			IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
			PersonnelReqDTO personneleqDTO = new PersonnelReqDTO();
			
			personneleqDTO.setIdentificationNo(this.getManagedBean().getSmSupp().getRegNo());
			PersonnelResDTO personnelResDTO =  personnelSO.retrieveIsEquityOrIsDirectorByIdentNo(personneleqDTO);
			
			if(personnelResDTO.getSmPersonnelList().size() > 0){
				throw new EpException(MessageConst.ERROR_721026);
			}
		}
		
		if(smAppl.getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}
		
		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());
		
		if (smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)) {
			PmParameter identificationType = ParameterHelper.getInstance().
					getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
			smPersonnel.setIdentificationTypeId(identificationType.getParameterId());
			smPersonnel.setIdentificationType(identificationType);
			
			PmCountry indNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			smSupp.setServiceCategory(SupplierManagementConst.SPECIAL_SERVICE); //DEFAULT for Individual
			smSuppOwner.setIdentificationTypeId(identificationType.getParameterId());
			smSuppOwner.setOwnerName(smAppl.getSmCompanyBasic().getCompanyName().trim());
			smSuppOwner.setIdentificationNo(smSupp.getRegNo());
			smSuppOwner.setNationality(indNationality);
			smPersonnel.setName(smAppl.getSmCompanyBasic().getCompanyName().trim());
			smPersonnel.setIdentificationNo(smSupp.getRegNo());
			smPersonnel.setPmCountry(indNationality);
			validateNewIC(smPersonnel,true);
			this.getManagedBean().setSmBusinessAdd(this.getManagedBean().getSmPermenantAdd());
			
			//set mobileno to company phone no
			smAppl.getSmCompanyBasic().setPhoneNo(smPersonnel.getMobileNo());
			
			getManagedBean().setGstFlag(false);
			//harris
			getManagedBean().setSstFlag(false);
		}
		
		if (smAppl.getApplType().equals(SupplierManagementConst.ACC_BASIC)) {
			smAppl.setSupplierType(SupplierManagementConst.STYPE_BASIC);
		}

		smSupp.setCompanyName(smAppl.getSmCompanyBasic().getCompanyName().trim());
		smSupp.setEstablishDate(smAppl.getSmCompanyBasic().getEstablishDate());
		smAppl.setSmSupplier(smSupp);
		smAppl.getSmCompanyBasic().setSmAppl(smAppl);
		smAppl.getSmCompanyBasic().setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smAppl.getSmCompanyBasic().setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		
		/* add personnel And personnel address */
		this.getManagedBean().getSmPersonnel().setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smPersonnel.setAuthorizedFlg(SupplierManagementConst.FIRST_USER_AUTHO);
		smPersonnel.setContactPerson(SupplierManagementConst.FIRST_USER_CONT_PERSON);
		smPersonnel.setContractSignerFlg(SupplierManagementConst.FIRST_USER_CONT_SIGNER);
		smPersonnel.setEpRole(RoleCode.SUPPLIER_TEMP.value());
		smPersonnel.setActivationDate(new Timestamp(System.currentTimeMillis())); //32040
		
		if(!smPersonnel.getPmCountry().getCountryCode().
				equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
			smPersonnel.setBumiFlg(0);
			smPersonnel.setMuslimFlg(0);
		}else{
			//set personnel bumi flag or muslim flag 
			smPersonnel.setBumiFlg(getManagedBean().getUserBumiFlg());
			smPersonnel.setMuslimFlg(getManagedBean().getUserMuslimFlg());
		}
		// #30829 - start
		if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_FOR_ROLE_IN_COMPANY.contains(this.getManagedBean().getSmSupp().getBusinessType())) {
			// check if supplier admin is supplier owner set clerical false and equityOwner true
			if (smPersonnel.isEquityOwner() || smPersonnel.isDirector()) {
				smPersonnel.setClerical(false);
			}
		}
		// #30829 - end
				
		//#16052019 - SSM INTEGRATION CR
		
		for(SmSupplierOwner smSupplierOwner:this.getManagedBean().getSmSuppOwners()){
			if(smSupplierOwner!=null && smSupplierOwner.getIdentificationNo().equals(smPersonnel.getIdentificationNo())){
				// smPersonnel.setClerical(false); // #30829
				//#30829 START
				SmSupplier sup = this.getManagedBean().getSmSupp();
				log.info("----Supplier Buss Type: "+sup.getBusinessType());
				if(sup.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) ||
						sup.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) ||
						sup.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) ||
						sup.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2) ||
						sup.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2))
					smPersonnel.setEquityOwner(false);
				else
					smPersonnel.setEquityOwner(true);
				//#30829 END
				validateNewIC(smPersonnel,true);
				break;
			}
		}
		
		//to set the softcert Flag
		//All supplier type will mark to "SOFT_CERT_PENDING_REQ", except supplier type is "Consultant"
		if(!smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			smPersonnel.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
		}else{
			smPersonnel.setSoftcertFlg(0);
		}

		SmPersonnelAddress personnelAddRes = new SmPersonnelAddress();
		SmPersonnelAddress personnelAddPerm = new SmPersonnelAddress();
		
		personnelAddRes.setAddressType(SupplierManagementConst.MAIL_ADD);
		if(this.getManagedBean().isSameAddress()){
			personnelAddRes.setSmAddress(smPermAdd);
			personnelAddRes.setSmPersonnel(smPersonnel);
		}else{
			personnelAddRes.setSmAddress(smResAdd);
			personnelAddRes.setSmPersonnel(smPersonnel);
			smResAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
			smResAdd.getSmPersonnelAddresses().add(personnelAddRes);
		}
		smPersonnel.getSmPersonnelAddresses().add(personnelAddRes);
		//16052019 - SSM INTEGRATION CR
		personnelAddPerm.setAddressType(SupplierManagementConst.PERM_ADD);
		personnelAddPerm.setSmAddress(smPermAdd);
		personnelAddPerm.setSmPersonnel(smPersonnel);
		smPermAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPermAdd.getSmPersonnelAddresses().add(personnelAddPerm);
		smPersonnel.getSmPersonnelAddresses().add(personnelAddPerm);
		smPersonnel.setSmAppl(smAppl);
		//START SSM CHANGES #27201
		smPersonnel = setDesignationSSM(smPersonnel);
		//END SSM CHANGES #27201		
		smAppl.getSmPersonnels().add(smPersonnel);
		//#27691 START - Comment this line to proceed testing basic account maintenance-issue duplicate personnel for SSM company
		//32764 && 29582- start
		if(!getManagedBean().isSameAsOwner() && getManagedBean().getSmSuppOwners() != null
				&& getManagedBean().getSmSuppOwners().size() > 0 && !getManagedBean().isCompanyValidWithSSM()){
			SmPersonnel per = new SmPersonnel();
			SmSupplierOwner owner = getManagedBean().getSmSuppOwners().get(0);
			per.setName(owner.getOwnerName());
			per.setIdentificationNo(owner.getIdentificationNo());
			per.setIdentificationTypeId(owner.getIdentificationTypeId());
			per.setPmCountry(owner.getNationality());
			per.setEquityOwner(true);
			per.setSmAppl(smAppl);
			smAppl.getSmPersonnels().add(per);			
		}
		//32764 && 29582- end
		//#27691 END
		//#16052019 - SSM INTEGRATION CR
		//add list all personel & check if smPersonnel contain in this.getManagedBean().getSmPersonnelAll(), use smPersonnel to add into appl, otherwise use personnel
		if(this.getManagedBean().isCompanyValidWithSSM()){
			if(this.getManagedBean().getSmPersonnelAll()!= null){
				for(SmPersonnel personnel : this.getManagedBean().getSmPersonnelAll()){
					//START SSM CHANGES #27201
					personnel = setDesignationSSM(personnel);
					//END SSM CHANGES #27201
					SmPersonnel personnelSSM = (SmPersonnel)personnel.cloneEntity();
					//to check,personel is equity owner or not
					SmPersonnel personnelAdmin = (SmPersonnel) EntityHelper.compareSingleEntityByProp(smPersonnel.getIdentificationNo(), personnelSSM,"identificationNo");
					//not suppplier admin
					if(personnelAdmin == null && personnelSSM.getSmPersonnelAdd() != null ){//store personnel not supplier admin but contain address
						personnelSSM.setSmAppl(smAppl);
						personnelSSM.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
						personnelSSM.getSmPersonnelAdd().setSmPersonnel(personnelSSM);
						personnelSSM.getSmPersonnelAddresses().add(personnelSSM.getSmPersonnelAdd());
						personnelSSM.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
						smAppl.getSmPersonnels().add(personnelSSM);
					}else if(personnelAdmin != null && personnelAdmin.getSmPersonnelAddresses()!=null){//supplier admin, need to set residensial address from SSM
						if(personnelAdmin.getSmPersonnelAdd() != null){
							personnelAdmin.getSmPersonnelAdd().setSmPersonnel(smPersonnel);
							if(personnelAdmin.getSmPersonnelAdd().getSmAddress()!= null){
								if(personnelAdmin.getSmPersonnelAdd().getSmAddress().getPmState()!= null){
									personnelAdmin.getSmPersonnelAdd().getSmAddress().setPmState(personnelAdmin.getSmPersonnelAdd().getSmAddress().getPmState());
								}
							}
						}
						smPersonnel.getSmPersonnelAddresses().add(personnelSSM.getSmPersonnelAdd());
					}else if(personnelAdmin == null && personnelSSM.getSmPersonnelAddresses()==null){//store personnel not supplier admin and not contain address
						personnelSSM.setSmAppl(smAppl);
						personnelSSM.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
						if(personnelSSM.getIdentificationType().getParameterCode().equalsIgnoreCase(ParameterConst.IDENTIFICATION_TYPES_IC)){
							personnelSSM.setPmCountry((PmCountry)defaultCountry.cloneEntity());
						}else{
							personnelSSM.setPmCountry(null);
						}
						personnelSSM.setSmPersonnelAdd(null);
						personnelSSM.setSmPersonnelAddresses(null);
						smAppl.getSmPersonnels().add(personnelSSM);
					}
						
				}
			}
		}
		//#16052019 - SSM INTEGRATION CR
		//set company equity
		if(this.getManagedBean().isCompanyValidWithSSM() && this.getManagedBean().getSmEquityCompany()!=null && this.getManagedBean().getSmEquityCompany().size()>0){
			for(SmEquityCompany company : this.getManagedBean().getSmEquityCompany()){
				company.setSmAppl(smAppl);
				//set company to address
				if(company.getSmAddress()!= null){
					company.getSmAddress().setSmEquityCompanies(new ArrayList<SmEquityCompany>());
					company.getSmAddress().getSmEquityCompanies().add(company);
				}
			}
			smAppl.setSmEquityCompanies(this.getManagedBean().getSmEquityCompany());
		}
		//#16052019 - SSM INTEGRATION CR
		//set branches
		if(this.getManagedBean().isCompanyValidWithSSM() && this.getManagedBean().getSmSupplierBranches()!=null && this.getManagedBean().getSmSupplierBranches().size()>0){
			for(SmSupplierBranch branch : this.getManagedBean().getSmSupplierBranches()){
				branch.setSmAppl(smAppl);
				//set company to address
				if(branch.getSmAddress()!= null){
					branch.getSmAddress().setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
					branch.getSmAddress().getSmSupplierBranches().add(branch);
				}
			}
			smAppl.setSmSupplierBranches(this.getManagedBean().getSmSupplierBranches());
		}
		
		
		/* add company address */
		SmCompanyAddress smCompAdd = new SmCompanyAddress();
		smCompAdd.setAddressType(SupplierManagementConst.BUS_ADD);
		smCompAdd.setSmCompanyBasic(smAppl.getSmCompanyBasic());
		smCompAdd.setSmAddress(this.getManagedBean().getSmBusinessAdd());
		smAppl.getSmCompanyBasic().getSmCompanyAddresses().add(smCompAdd);

		for(SmSupplierBank suppBank : smAppl.getSmSupplierBanks()){
			suppBank.setSmAppl(smAppl);
			suppBank.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
			suppBank.setAccountPurpose(defaultAccPurpose);
		}
		
		for(SmSupplierBusiness smSuppBiz : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
			smSuppBiz.setSmSupplier(smSupp);
		}
		
		
		/*
		 * Professional Cert 
		 */
		if(this.getManagedBean().getSmAppl().getSupplierType() != null && this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
			if((smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT) && smAppl.getSupplierType() != null)){  
				for(SmAssociationBody suppAssocBody : smAppl.getSmAssociationBodies()){
					suppAssocBody.setSmAppl(smAppl);
					suppAssocBody.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
				}
			}else{
				smAppl.setSmAssociationBodies(null);
			}
		}
		
		//GST
		if(getManagedBean().isGstFlag()){
			getManagedBean().getSmSupp().setSmGstSuppliers(new ArrayList<SmGstSupplier>());
			getManagedBean().getSmSupp().setGstRegisteredFlag(DbValueConst.RECORD_STATUS_ACTIVE);
			setSupplierGst();
		}
		
		//harris
		//SST
				if(getManagedBean().isSstFlag()){
					getManagedBean().getSmSupp().setSmSstSuppliers(new ArrayList<SmSstSupplier>());
					getManagedBean().getSmSupp().setSstRegisteredFlag(DbValueConst.RECORD_STATUS_ACTIVE);
					setSupplierSst();
				}
		
		/*
		 * Add all related objects to Parent SmSupplier
		 */
		smSupp.getSmAppls().add(smAppl);
		smSupp.setPmStatus(new PmStatus());
		smSupp.getPmStatus().setStatusId(20101L);
		smSupp.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);
		//#16052019 - SSM INTEGRATION CR
		if(smSuppOwner.getOwnerName() != null && !"".equalsIgnoreCase(smSuppOwner.getOwnerName())){
			smSuppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
			smSupp.getSmSupplierOwners().add(smSuppOwner);
		}
		
		/* Create Basic Account */
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);
		
		if (resDTO.isSuccessful()) {
			String suppAdminEmail = smPersonnel != null
					&& StringUtils.isNotBlank(smPersonnel.getEmail()) ? smPersonnel
					.getEmail() : "";
					
			//14623 - start : tukar ayat
			if (this.getManagedBean().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_721012,
										new String[] {
												suppAdminEmail,
												SmRuleHelper.getInstance()
														.getEmailActivationDays()
														+ "" },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			} else if (this.getManagedBean().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(MessageConst.INFO_722686,
										new String[] {
												suppAdminEmail,
												SmRuleHelper.getInstance()
														.getEmailActivationDays()
														+ "" },
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
			}
			//14623 - end		
			renewSesionObject();
			this.getManagedBean().setSmAppl(null);
			this.getManagedBean().setSmSupp(null);
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			throw new EpException(MessageConst.ERROR_721006);
		}
//		return null;
	}
	
	public void onCallFirstUserTncDialog() {
		getManagedBean().setAggreeDeclareAdmin(false);
		// #34400 - Start
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();

		if (getManagedBean().getSmSupp().getBusinessType()
				.equalsIgnoreCase(SupplierManagementConst.INDIVIDUAL_FLAG)) {
			this.getManagedBean().setSameAsOwner(true);
			setAdminSameAsOwner();

			if (SmCommonUtil.isSupplierOwnerIndividualAcc(smSuppOwner
					.getIdentificationType().getParameterCode(), smSupp
					.getRegNo(), null)) {
				throw new EpException(MessageConst.ERROR_721050);
			}

			IPersonnelSO personnelSO = BusinessDelegate.getInstance()
					.getServiceObject("sm", IPersonnelSO.class);

			PersonnelReqDTO personneleqDTO = new PersonnelReqDTO();

			personneleqDTO.setIdentificationNo(this.getManagedBean()
					.getSmSupp().getRegNo());
			PersonnelResDTO personnelResDTO = personnelSO
					.retrieveIsEquityOrIsDirectorByIdentNo(personneleqDTO);

			if (!personnelResDTO.getSmPersonnelList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_721026);
			}

			PmParameter identificationType = ParameterHelper.getInstance()
					.getParameterByTypeAndCode(
							ParameterTypeConst.IDENTIFICATION_TYPES,
							ParameterConst.IDENTIFICATION_TYPES_IC);
			smPersonnel.setIdentificationTypeId(identificationType
					.getParameterId());
			smPersonnel.setIdentificationType(identificationType);

			PmCountry indNationality = SmCommonUtil
					.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			smSupp.setServiceCategory(SupplierManagementConst.SPECIAL_SERVICE); // DEFAULT
																				// for
																				// Individual
			smSuppOwner.setIdentificationTypeId(identificationType
					.getParameterId());
			smSuppOwner.setOwnerName(smAppl.getSmCompanyBasic()
					.getCompanyName().trim());
			smSuppOwner.setIdentificationNo(smSupp.getRegNo());
			smSuppOwner.setNationality(indNationality);
			smPersonnel.setName(smAppl.getSmCompanyBasic().getCompanyName()
					.trim());
			smPersonnel.setIdentificationNo(smSupp.getRegNo());
			smPersonnel.setPmCountry(indNationality);
			validateNewIC(smPersonnel, true);
			this.getManagedBean().setSmBusinessAdd(
					this.getManagedBean().getSmPermenantAdd());

			// set mobileno to company phone no
			smAppl.getSmCompanyBasic().setPhoneNo(smPersonnel.getMobileNo());

			getManagedBean().setGstFlag(false);
			getManagedBean().setSstFlag(false);
		} else {
			setAdminSameAsOwner();
			// #30829 - start
			checkRoleInCompany();
			// #30829 - end
		}
		// #34400 - End
		// SSM INTEGRATION
		// #27315
		// disable this, no need to check other supplier owner
		/*
		 * if(this.getManagedBean().isCompanyValidWithSSM()){
		 * for(SmSupplierOwner
		 * suppOwner:this.getManagedBean().getSmSuppOwners()){
		 * if(suppOwner.getIdentificationType
		 * ().getParameterCode().equals(SupplierManagementConst
		 * .BASIC_IND_IDENT_TYPE)){
		 * if(getManagedBean().getSmAppl().getApplType()
		 * .equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){ //MOF
		 * SmCommonUtil
		 * .getInstance().verifyWithMyIdentity(suppOwner.getOwnerName(),
		 * suppOwner.getIdentificationNo(), null,
		 * my.ep.constant.sm.SupplierManagementConst
		 * .MY_IDENTITY_TYPE_COMPLAIN,this
		 * .getManagedBean().isCompanyValidWithSSM()); }else{ //Basic
		 * SmCommonUtil
		 * .getInstance().verifyOwnerWithMyIdentity(suppOwner.getOwnerName(),
		 * suppOwner.getIdentificationNo(), null,
		 * my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_COMPLAIN,
		 * null,this.getManagedBean().isCompanyValidWithSSM()); } } } }
		 */
		validateNewIC(smPersonnel, false);
		checkBlacklistedOwners(smPersonnel.getIdentificationNo(), true); //#26238 - 39069, #39355
		RequestContext.getCurrentInstance()
				.execute("firstUserTncDialog.show()");
	}

	/**
	 * update Basic Supplier Account
	 * @return String
	 */
	public String updateOnlineAccount() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		
		// start - #30964
		if (smSupp.getSmAppl() != null) {
			smSupp.setCompanyName(smSupp.getSmAppl().getSmCompanyBasic().getCompanyName());
			smSupp.setEstablishDate(smSupp.getSmAppl().getSmCompanyBasic().getEstablishDate());
		}
		// end - #30964
		
		if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmPermenantAdd());
		}else{
			//start - #30964
			SmAddress smAddress = this.getManagedBean().getSmBusinessAdd();
			if(smAddress.getPmCountry()!=null){
				smAddress.getPmCountry().getCountryId();
			}
			if(smAddress.getPmCity()!=null){
				smAddress.getPmCity().getCityId();
			}
			if(smAddress.getPmDistrict()!=null){
				smAddress.getPmDistrict().getDistrictId();
			}
			if(smAddress.getPmState()!=null){
				smAddress.getPmState().getStateId();
			}
			if(smAddress.getPmDivision()!=null &&smAddress.getPmDivision().getDivisionId()!=null){
				smAddress.getPmDivision();
			}
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(smAddress);
			// end - #30964
		}
		smSupp.setEstablishDate(smSupp.getSmAppl().getSmCompanyBasic().getEstablishDate());
		getManagedBean().setSmSupp(smSupp);  //#30964
		
		// #27691 - Start- For supplier owner with record status 9 or 0
		/*if(!this.getManagedBean().getSupplierOwnerls().isEmpty()){
			getManagedBean().getSmSupp().getSmSupplierOwners().addAll(this.getManagedBean().getSupplierOwnerls());
		}*/
		// #27691 - End

		reqDTO.setSmSupp(getManagedBean().getSmSupp()); //#30964
		reqDTO.setDisabledCompanyNameInMaintenance(getManagedBean().isDisabledCompanyNameInMaintenance()); //#30964
		//27691 START
		// retrieve current smsupplierowner
		IRegistrationSO reqSO1 = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		List<SmSupplierOwner> suppOwnerList = reqSO1.retrieveCurrSmSupplierOwnerListBySupplierId(reqDTO);
		
		//To reactivate owner that have data in DB and
		//Remove unnecessary list to avoid duplicate without changing actual data
		List<SmSupplierOwner> listToRemove1 = new ArrayList<SmSupplierOwner>();
		if(!this.getManagedBean().getSupplierOwnerls().isEmpty()){
			for(SmSupplierOwner unactiveSuppOwner : this.getManagedBean().getSupplierOwnerls()){
				for(SmSupplierOwner currSuppOwner : getManagedBean().getSmSupp().getSmSupplierOwners()){
					if(unactiveSuppOwner.getIdentificationNo().equals(currSuppOwner.getIdentificationNo())
							&& currSuppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
						unactiveSuppOwner.setReactivateOwner(true);
						unactiveSuppOwner.setRecordStatusFlg(currSuppOwner.getRecordStatusFlg());
						listToRemove1.add(currSuppOwner);
					}
				}
			}
			if(!listToRemove1.isEmpty()){
				getManagedBean().getSmSupp().getSmSupplierOwners().removeAll(listToRemove1);
			}
			getManagedBean().getSmSupp().getSmSupplierOwners().addAll(this.getManagedBean().getSupplierOwnerls());
		}
		//For scenario user delete owner and adding back again the same owner before declare
		//Remove unnecessary list to avoid duplicate without changing actual data
		List<SmSupplierOwner> listToRemove2 = new ArrayList<SmSupplierOwner>();
		List<SmSupplierOwner> listToRemove3 = new ArrayList<SmSupplierOwner>();
		if(!this.getManagedBean().getDelSuppOwnerLs().isEmpty()){
			for(SmSupplierOwner delSuppOwner : this.getManagedBean().getDelSuppOwnerLs()){
				for(SmSupplierOwner currSuppOwner : getManagedBean().getSmSuppOwners()){
					if(delSuppOwner.getIdentificationNo().equals(currSuppOwner.getIdentificationNo())
							&& currSuppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
						delSuppOwner.setRecordStatusFlg(currSuppOwner.getRecordStatusFlg());
						listToRemove2.add(currSuppOwner);
						listToRemove3.add(delSuppOwner);
					}
				}
			}
			if(!listToRemove2.isEmpty()){
				getManagedBean().getSmSuppOwners().removeAll(listToRemove2);
			}
			getManagedBean().getSmSuppOwners().addAll(this.getManagedBean().getDelSuppOwnerLs());
			if(!listToRemove3.isEmpty()){
				getManagedBean().getDelSuppOwnerLs().removeAll(listToRemove3);
			}
		}
		//For scenario user add owner that currently in table supplier owner doesnt have the data of that owner
		//Remove unnecessary list to avoid duplicate without changing actual data
		if(!this.getManagedBean().getAddSuppOwnerLs().isEmpty()){
			Set<String> uniqueOwners = new HashSet<String>();
			Set<SmSupplierOwner> duplicateOwners = new HashSet<SmSupplierOwner>();
			for(SmSupplierOwner addSuppOwners : this.getManagedBean().getAddSuppOwnerLs()){
				for(SmSupplierOwner currSuppOwner : getManagedBean().getSmSuppOwners()){
					if(addSuppOwners.getIdentificationNo().equals(currSuppOwner.getIdentificationNo())
							&& currSuppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
						if (!uniqueOwners.add(currSuppOwner.getIdentificationNo())) {
							duplicateOwners.add(currSuppOwner);
						}
					}
				}
			}
			if(!duplicateOwners.isEmpty()){
				for(SmSupplierOwner addSuppOwners : this.getManagedBean().getAddSuppOwnerLs()){
					for(SmSupplierOwner currSuppOwner : getManagedBean().getSmSuppOwners()){
						if(addSuppOwners.getIdentificationNo().equals(currSuppOwner.getIdentificationNo())
								&& currSuppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
							throw new EpException(MessageConst.ERROR_722524,
									new String[] { currSuppOwner.getIdentificationNo() });
						}
					}
				}
			}
			
		}
		//Error duplicate for entering same IC
		if(!this.getManagedBean().getAddSuppOwnerLs().isEmpty()){
			for(SmSupplierOwner newSuppOwner : this.getManagedBean().getAddSuppOwnerLs()){
				for (SmSupplierOwner currSuppOwner : suppOwnerList) {
					if(newSuppOwner.getIdentificationNo().equals(currSuppOwner.getIdentificationNo())
							&& currSuppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
						throw new EpException(MessageConst.ERROR_722524,
								new String[] { currSuppOwner.getIdentificationNo() });
					}
				}
			}
		}
		//to handle data that have same ic with same appl id
		List<SmPersonnel> duplicatePersonnelList = new ArrayList<>();
        for(SmPersonnel pers : reqDTO.getSmSupp().getSmAppl().getSmPersonnels()){
        	for(SmPersonnel allPers : reqDTO.getSmSupp().getSmAppl().getSmPersonnels()){
        		if(pers.getIdentificationNo().equals(allPers.getIdentificationNo())
        				&& pers.getCreatedDate() != null && allPers.getCreatedDate() != null
        				&& pers.getCreatedDate().compareTo(allPers.getCreatedDate()) < 0){
        			duplicatePersonnelList.add(pers);
        		}
        	}
        }
		// added business type validation for SSM
		if(SmCommonUtil.getInstance().ssmActive() 
				&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupp().getRegNo(), this.getManagedBean().getSmSupp().getBusinessType())){
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(this.getManagedBean().getSmSupp().getRegNo());
			ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupp().getBusinessType());
			ssmReqDTO.setBasicAcc(false);
			if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
				ssmReqDTO.setBasicAcc(true);
			}
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			// retrieve latest smsupplierowner
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IRegistrationSO.class);
			List<SmSupplierOwner> currSuppOwnerList = reqSO.retrieveCurrentSmSupplierOwnerListBySmSupplier(reqDTO);
			//#28888 : checking empty list
			/*if(CollectionUtils.isNotEmpty(getManagedBean().getSmSupp().getSmSupplierOwners())){
				SmSupplierOwner smSupplierOwners = getManagedBean().getSmSupp().getSmSupplierOwners().get(0);
				getManagedBean().getSmSupp().getSmSupplierOwners().clear();
				getManagedBean().getSmSupp().getSmSupplierOwners().add(smSupplierOwners);
			}//end #28888
*/			getManagedBean().getSmSupp().setSmSupplierOwners(currSuppOwnerList);
			for(SmPersonnel personnel : reqDTO.getSmSupp().getSmAppl().getSmPersonnels()){
				if(!duplicatePersonnelList.contains(personnel)){
					if(personnel.isSsmData() && personnel.getIdentificationType() != null				//start - #30064
							&& personnel.getIdentificationType().getParameterCode()		
							.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
						//readding&check owner or director if not exist in sm supplier owner, if exist maintain the data
						boolean isExistedInOwner = false;
						if(getManagedBean().getSmSupp().getSmSupplierOwners().size() != 0){
							for(SmSupplierOwner suppOwner : getManagedBean().getSmSupp().getSmSupplierOwners()){
								if(personnel.getIdentificationNo().equals(suppOwner.getIdentificationNo())){
									suppOwner.setOwnerIsEquity(personnel.isEquityOwner());
									suppOwner.setOwnerIsDirector(personnel.isDirector());
									isExistedInOwner = true;
								}
							}
						}
						if(!isExistedInOwner){
							if((personnel.isEquityOwner() || personnel.isDirector()) && personnel.getRecordStatusFlg()!=9){
								SmSupplierOwner suppOwnerNewList = new SmSupplierOwner();
								suppOwnerNewList.setSmSupplier(this.getManagedBean().getSmSupp());
								suppOwnerNewList.setOwnerName(personnel.getName());
								suppOwnerNewList.setNationality(personnel.getPmCountry());
								suppOwnerNewList.setIdentificationType(personnel.getIdentificationType());
								suppOwnerNewList.setIdentificationTypeId(personnel.getIdentificationTypeId());
								suppOwnerNewList.setIdentificationNo(personnel.getIdentificationNo());
								suppOwnerNewList.setOwnerIsEquity(personnel.isEquityOwner());
								suppOwnerNewList.setOwnerIsDirector(personnel.isDirector());
								getManagedBean().getSmSupp().getSmSupplierOwners().add(suppOwnerNewList);
								
							}
						}
					}
				}else{
					personnel.setRevNo(0);
					personnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
				}
			}
			reqDTO.getSmSupp().setSmSupplierOwners(getManagedBean().getSmSupp().getSmSupplierOwners());
		}else{
			if (smSupp.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE
					&& getManagedBean().getSmSuppOwners() != null) {
				for (SmSupplierOwner smSupplierOwner : getManagedBean().getSmSuppOwners()) {
					Boolean personnelAvailable = false;
					if (!this.getManagedBean().getDelSuppOwnerLs().isEmpty()) {
						for (SmSupplierOwner delSuppOwner : this
								.getManagedBean().getDelSuppOwnerLs()) {
							if (delSuppOwner.getIdentificationNo()
									.equalsIgnoreCase(
											smSupplierOwner
													.getIdentificationNo())) {
								smSupplierOwner.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}
						}
					}
					for (SmPersonnel smPersonnel : reqDTO.getSmSupp().getSmAppl().getSmPersonnels()) {
						if (smSupplierOwner.getIdentificationNo().equals(smPersonnel.getIdentificationNo())) {
							personnelAvailable = true;
							if(!duplicatePersonnelList.contains(smPersonnel)){
								//For existing personnel with wrong data
								if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
									smPersonnel.setEquityOwner(false);
									smPersonnel.setDirector(true);
								}else if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
										|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)){
									smPersonnel.setEquityOwner(true);
									smPersonnel.setDirector(false);
								}else if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP)){
									if(smPersonnel.isEquityOwner() != smSupplierOwner.isOwnerIsEquity()){
										smPersonnel.setEquityOwner(smSupplierOwner.isOwnerIsEquity());
									}
									if(smPersonnel.isDirector() != smSupplierOwner.isOwnerIsDirector()){
										smPersonnel.setDirector(smSupplierOwner.isOwnerIsDirector());
									}
								}
								if(smPersonnel.getRevNo() != SupplierManagementConst.DEFAULT_REV_NO){
									smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
								}
								if(smPersonnel.getName() != smSupplierOwner.getOwnerName()){
									smPersonnel.setName(smSupplierOwner.getOwnerName());
								}
								if(smPersonnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)
										&& smSupplierOwner.getRecordStatusFlg().intValue()== DbValueConst.RECORD_STATUS_ACTIVE){
									smPersonnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
									smPersonnel.setCreatedDate(new Timestamp(System.currentTimeMillis()));
									smPersonnel.setCreatedBy(getUserIdSessionFromLoginId());
								}
								//reactivate personnel thru basic maintenance
								if (smSupplierOwner.isReactivateOwner() && smPersonnel.getRecordStatusFlg().equals(
										DbValueConst.RECORD_STATUS_DELETED)) {
									smPersonnel
											.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
								}
								reqDTO.setSmPersonnel(smPersonnel);
							}else{
								smPersonnel.setRevNo(0);
								smPersonnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
							}
						}
					}
					if (personnelAvailable == false) {
						SmPersonnel smPersonnel = new SmPersonnel();
						PmParameter identificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
						smPersonnel.setSmAppl(reqDTO.getSmSupp().getSmAppl());
						smPersonnel.setName(smSupplierOwner.getOwnerName());
						smPersonnel.setIdentificationType(smSupplierOwner.getIdentificationType());
						smPersonnel.setIdentificationTypeId(identificationType.getParameterId());
						smPersonnel.setIdentificationNo(smSupplierOwner.getIdentificationNo());
						smPersonnel.setPmCountry(smSupplierOwner.getNationality());
						smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO); 
						
						if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
							smPersonnel.setDirector(true);
						}else if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)){
							smPersonnel.setEquityOwner(true); 
						}else if(smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
								|| smSupp.getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
							smPersonnel.setEquityOwner(smSupplierOwner.isOwnerIsEquity());
							smPersonnel.setDirector(smSupplierOwner.isOwnerIsDirector());
						}
						//For newly added profile set SoftCert to 0
						if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
							smPersonnel.setSoftcertFlg(0);
						}
						//For newly added profile set Record Status to 1
						smPersonnel.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						
						reqDTO.getSmSupp().getSmAppl().getSmPersonnels().add(smPersonnel);
					}
					reqDTO.getSmSupp().setSmSupplierOwners(getManagedBean().getSmSuppOwners());
				}
			} 
		}
		//27691 END
		
		reqDTO.setGstList(getCleanGst());
		
		try{
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IRegistrationSO.class);			
			RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
			RegistrationResDTO ressDTO = reqSO.updateOnlineAccount(reqDTO); //#30964
			if (ressDTO.isSuccessful() && resDTO.isSuccessful()) { //#30964
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				//#15016 - Start
				//FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
				//		FacesMessage.SEVERITY_INFO,
				//		null,
				//		MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				// #28895_35501 Start - AUDIT TRAIL SM
				IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
				if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
					AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementBankInformationBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
				}
				// #28895_35501 End
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_721015, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				//#15016 - End
				return acknowledgement;
			} else {
				throw new EpException(MessageConst.ERROR_721006);
			}
		} catch(Exception ex){
			throw new EpException(ex.getMessage());
		}finally{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
		}
	}
	
	public List<SmGstSupplier> getCleanGst() {
		List<SmGstSupplier> supplierGstList = new ArrayList<SmGstSupplier>();
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getSmGstSupplier());
		supplierGstList.add(getManagedBean().getSmGstSupplier());
		return supplierGstList;
	}

	/**
	 * add new row to Supplier Bank
	 */
	public void addSupplierBank() {

		if (this.getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			this.getManagedBean().getSmAppl().setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		
		checkMaxBankAccount();
		
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		smSuppBank.setSmAppl(this.getManagedBean().getSmAppl());
		smSuppBank.setAccountPurpose(defaultAccPurpose);
		smSuppBank.setForHqFlg(1);
		// #34640 - start
		smSuppBank.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		// #34640 - end
		this.getManagedBean().getSmAppl().getSmSupplierBanks().add(smSuppBank);
	}

	/**
	 * remove selected Supplier Bank
	 */
	public void removeSupplierBank() {
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(suppBank);
			}
		}
		
		this.getManagedBean().setSelectedSuppBanks(null);
	}
	
	/**
	 * create new Business Nature Object and set to the selectedBusinessNature
	 * and reset all lvl and business nature selection list
	 */
	public void addBusinessNature() {
		this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());	
		this.getManagedBean().getSelectedBusinessNature().setPmBusinessNature(new PmBusinessNature());
		
		resetBusinessNatureSelection();
	}
	
	/**
	 * add the selectedBusinessNature to SmSupplierBusiness List
	 */
	public void addSupplierBusinessNature(){
			
		Long bizNatId  = this.getManagedBean().getSelectedBusinessNature().getLvl5();
		
		if(validateNatureBusiness(bizNatId)){
			PmBusinessNature bizNatObj = SmCommonUtil.retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
			if(bizNatObj != null){
				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bizNatObj);
				suppBusiness.setSmSupplier(this.getManagedBean().getSmSupp());
				this.getManagedBean().getSmSupp().getSmSupplierBusinesses().add(suppBusiness);
				RequestContext.getCurrentInstance().execute("bizDlg.hide()");
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
					MessageUtils.getMessage(MessageConst.ERROR_721007,MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
		}

	}

	/**
	 * Validate duplicate Business Nature
	 * @param bizNatureId
	 * @return boolean
	 */
	private boolean validateNatureBusiness(Long bizNatureId){
		List<String> bizNatureLs = new ArrayList<String>();
		boolean valid = true;
		
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() > 0){
			String bizNatId = bizNatureId.toString();
			for(SmSupplierBusiness suppBiz : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
				bizNatureLs.add(suppBiz.getPmBusinessNature().getBusinessNatureId().toString());
			}
			
			if(bizNatureLs.contains(bizNatId)){
				valid = false;
			}
		}
		
		return valid;
	}
	
	/**
	 * remove selected business nature from the SmSupplierBusinesses List
	 */
	public void removeBusinessNature() {
		if(!this.getManagedBean().getSmSupp().isBrandNew()){
			if(this.getManagedBean().getSelectedBusinessNatureLs().length ==  this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size()){
				throw new EpException(MessageConst.ERROR_721014);
			}
		}
		for (SmSupplierBusiness suppBusinessNature : this.getManagedBean().getSelectedBusinessNatureLs()) {
			this.getManagedBean().getSmSupp().getSmSupplierBusinesses().remove(suppBusinessNature);
		}
		
		this.getManagedBean().setSelectedBusinessNatureLs(null);
	}
	
	/**
	 * Edit Business Nature
	 */
	public void editBusinessNature(SmSupplierBusiness suppBusinessNat){
				
		SmSupplierBusiness selectedBiz = suppBusinessNat;
		PmBusinessNature pmBizNat = selectedBiz.getPmBusinessNature();
		
		//Get Parent Obj of L5(lv 4) value and List of Level 5
		PmBusinessNature pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2.getParentBusinessNature().getBusinessNatureId());
		
		this.getManagedBean().setSelectedBusinessNature(selectedBiz);
		this.getManagedBean().getSelectedBusinessNature().setLvl5(selectedBiz.getPmBusinessNature().getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl4(pmBizObjL4.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl3(pmBizObjL3.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl2(pmBizObjL2.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl1(pmBizObjL1.getBusinessNatureId());
		
		List<PmBusinessNature> bizNatureLs5 = retrieveBusinessNatureByLevelParent("50",pmBizNat.getParentBusinessNature().getBusinessNatureId());
		
		List<PmBusinessNature> bizNatureLs4 = retrieveBusinessNatureByLevelParent("40",pmBizObjL3.getBusinessNatureId());
		List<PmBusinessNature> bizNatureLs3 = retrieveBusinessNatureByLevelParent("30",pmBizObjL2.getBusinessNatureId());
		List<PmBusinessNature> bizNatureLs2 = retrieveBusinessNatureByLevelParent("20",pmBizObjL1.getBusinessNatureId());
		
		this.getManagedBean().setBusinessNatureL5(bizNatureLs5);
		this.getManagedBean().setBusinessNatureL4(bizNatureLs4);
		this.getManagedBean().setBusinessNatureL3(bizNatureLs3);
		this.getManagedBean().setBusinessNatureL2(bizNatureLs2);
	}
	
	/**
	 * update the edited business nature
	 */
	public void updateBusinessNature(){

		Long bizNatId = this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getBusinessNatureId();
		
		if(bizNatId !=null){
			if(bizNatId.longValue() == this.getManagedBean().getSelectedBusinessNature().getLvl5().longValue()){
				RequestContext.getCurrentInstance().execute("bizDlg.hide()");
			}else{
				if(validateNatureBusiness(this.getManagedBean().getSelectedBusinessNature().getLvl5().longValue())){
					PmBusinessNature bizNatObj = SmCommonUtil.retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
					if(bizNatObj != null){
						this.getManagedBean().getSelectedBusinessNature().setPmBusinessNature(bizNatObj);
						RequestContext.getCurrentInstance().execute("bizDlg.hide()");
					}
				}else{
					//show error msg on dialog
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
							MessageUtils.getMessage(MessageConst.ERROR_721007,MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
				}
			}
		}
	}
	
	/**
	 * set business Nature List based on business level
	 * @param bizLvl
	 */
	public void businessLvlOnChange(String bizLvl){
		SmSupplierBusiness selectedBiz = this.getManagedBean().getSelectedBusinessNature();

		switch (bizLvl) {
			case "20" : 
				this.getManagedBean().setBusinessNatureL2(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl2(null);
				selectedBiz.setLvl3(null);
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				if(selectedBiz.getLvl1() != null){
					this.getManagedBean().setBusinessNatureL2(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl1()));
				}
			break;
			case "30" :
				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl3(null);
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				if(selectedBiz.getLvl2() !=null){
					this.getManagedBean().setBusinessNatureL3(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl2()));
				}
			break;
			case "40" : 
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				if(selectedBiz.getLvl3()!=null){
					this.getManagedBean().setBusinessNatureL4(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl3()));
				}
			break;
			case "50" : 
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl5(null);
				if(selectedBiz.getLvl4()!=null){
					this.getManagedBean().setBusinessNatureL5(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl4()));
				}
			break;
			default:
		}
	}

	/**
	 * Retrieve Country List from PM
	 */
	private void retrievePmCountryList() {
		List<SelectItem> countryLs = SmCommonUtil.getCountryList();
		this.getManagedBean().setCountryLs(countryLs);
	}

	/**
	 * retrieve State List by country Id
	 * @param countryId
	 * @return List<PmState>
	 */
	private List<PmState> retrievePmStateList(Long countryId) {
		if(countryId != null){
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			return resDTO.getStateList();
		}

		return null;
	}

	/**
	 * retrieve PM FinancialOrg List 
	 */
	public List<PmFinancialOrg> retrieveFinancialOrgList() {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType("PB");
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO
				.retrieveFinancialOrgListByServiceType(reqDTO);
		return resDTO.getFinancialOrgList();
	}
	
	/**
	 * Retrieve Proffessional Body List from PM
	 */
	private void retrieveProfBody() {
		this.getManagedBean().setProfCertBodyLs(SmApplicationUtil.retrievePmProfBodyList());
		
		if(this.getManagedBean().getProfCertBodyLs()==null){
			this.getManagedBean().setProfCertBodyLs(new ArrayList<PmCertBody>());
		}
		
		if(this.getManagedBean().getProfCertBodyLs().size() > 0){
			profCertBodyMap = new HashMap<Long, PmCertBody>();
			for(PmCertBody cb : this.getManagedBean().getProfCertBodyLs()){
				profCertBodyMap.put(cb.getCertBodyId(), cb);
			}
		}
	}

	/**
	 * retrieve BusinessNature By Parent Level
	 * @param busLvl
	 * @param parentId
	 * @return List<PmBusinessNature>
	 */
	private List<PmBusinessNature> retrieveBusinessNatureByLevelParent(String busLvl, long parentId){
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		if(busLvl != "" || busLvl != null){
			reqDTO.setBusinessLevel(busLvl);
		}
		
		if(busLvl != "10"){
			reqDTO.setParentBusinessNatureId(parentId);
		}
		
		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureListByLevel(reqDTO);
		
		if(resDTO.getBusinessNatureList()!=null && CollectionUtils.isNotEmpty(resDTO.getBusinessNatureList())){
			// sort
			Collections.sort(resDTO.getBusinessNatureList(), new Comparator<PmBusinessNature>() {
				@Override
				public int compare(final PmBusinessNature p1, final PmBusinessNature p2) {
					String levelCode1 = p1.getLevelCode()==null?"":p1.getLevelCode();
					String levelCode2 = p2.getLevelCode()==null?"":p2.getLevelCode();
					return StringUtils.upperCase(levelCode1).compareTo(
							StringUtils.upperCase(levelCode2));
				}
			});
		}

		return resDTO.getBusinessNatureList();
	}
	
	/**
	 * Check the existance of business Reg No
	 * @return boolean
	 */
	private void checkExistingBusinessRegNo() {

		// to validate if there is same reg_no that has inProgress status
		if (StringUtils.isNotBlank(getManagedBean().getSmSupp().getRegNo())) {
			// #35581
			if (!validateString(getManagedBean().getSmSupp().getRegNo())) {
				if (sabahSarawakRemoveWhiteSpaceList.contains(getManagedBean()
						.getSmSupp().getBusinessType())) {
					throw new EpException(MessageConst.ERROR_723100,
							new String[] { ResourceBundleUtils.getMessage(
									"lbl.sm.businessregno",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
				} else if (regNoValidityList.contains(getManagedBean()
						.getSmSupp().getBusinessType())) {
					throw new EpException(MessageConst.ERROR_723101,
							new String[] { ResourceBundleUtils.getMessage(
									"lbl.sm.businessregno",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
				}
			}
		}

		// #38143 - Start
		validateExistingBusinessRegNo();
		// #38143 - End

	}
	//16052019 - SSM INTEGRATION CR - Fahmi
	public String returnToCompanyValidation(){
		/*getManagedBean().setCaptchaAnswer(null);
		getManagedBean().setAgreeTC(false);*/ //commented for 36710
		return ssmAccountCheck;

	}
	//16052019 - SSM INTEGRATION CR - Fahmi
	// #38143 - Start
	private void validateExistingBusinessRegNo() {
		if (isLocalAuthBusinessType()) {
			validateBusinessRegNoWithBussTypeAndLocalAuth();
			return;
		}

		trimRegNo();
		RegistrationReqDTO reqDTO = createRegistrationRequest();
		List<SmSupplier> suppliers = SmServiceLookup.getRegistrationSO()
				.retrieveSupplierByRegNoAndStatusLs(reqDTO).getSmSuppliers();

		if (suppliers.isEmpty()) {
			return;
		}

		if (isApplicationTypeMoF()) {
			validateForMoF(suppliers);
		} else {
			validateForBasic(suppliers);
		}
	}

	private boolean isLocalAuthBusinessType() {
		String businessType = getManagedBean().getSmSupp().getBusinessType();
		return !StringUtils.isBlank(businessType)
				&& SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE
						.contains(businessType);
	}

	private void trimRegNo() {
		SmSupplier supplier = getManagedBean().getSmSupp();
		supplier.setRegNo(supplier.getRegNo().trim());
	}

	private RegistrationReqDTO createRegistrationRequest() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setRegNo(getManagedBean().getSmSupp().getRegNo());
		reqDTO.setStatusLs(Arrays.asList(DbValueConst.RECORD_STATUS_ACTIVE,
				DbValueConst.RECORD_STATUS_SUSPENDED,
				DbValueConst.RECORD_STATUS_EXPIRED,
				DbValueConst.RECORD_STATUS_PENDING_ACTIVATION,
				DbValueConst.RECORD_STATUS_IN_PROGRESS));
		return reqDTO;
	}

	private boolean isApplicationTypeMoF() {
		return getManagedBean().getSmAppl().getApplType()
				.equalsIgnoreCase(SupplierManagementConst.ACC_MOF);
	}

	private void validateForMoF(List<SmSupplier> suppliers) {
		for (SmSupplier supplier : suppliers) {
			if (supplier.getSmAppl() == null) {
				errorMessageSSM(supplier);
				continue;
			}

			String currentType = getManagedBean().getSmAppl().getSupplierType();
			String existingType = supplier.getSmAppl().getSupplierType();

			if (currentType.equals(existingType)) {
				handleSameTypeMoF(supplier);
			} else {
				handleDifferentTypeMoF(suppliers, supplier, currentType,
						existingType);
			}
		}
	}

	private void handleSameTypeMoF(SmSupplier supplier) {
		String labelText = getSupplierTypeLabel(supplier.getSmAppl()
				.getSupplierType());
		int status = supplier.getRecordStatusFlg();

		switch (status) {
		case DbValueConst.RECORD_STATUS_ACTIVE:
			errorMessageSSM(supplier);
			throw new EpException(MessageConst.ERROR_721073, new String[] {
					SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
		case DbValueConst.RECORD_STATUS_SUSPENDED:
			throw new EpException(MessageConst.ERROR_721074, new String[] {
					SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
		case DbValueConst.RECORD_STATUS_PENDING_ACTIVATION:
			if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
				throw new EpException(MessageConst.ERROR_721061, new String[] {
						SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
			}else{
				throw new EpException(MessageConst.ERROR_721065, new String[] {
						SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
			}
			
		case DbValueConst.RECORD_STATUS_EXPIRED:
			throw new EpException(MessageConst.ERROR_721076, new String[] {
					SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
		case DbValueConst.RECORD_STATUS_IN_PROGRESS:
			handleInProgressStatus(supplier, labelText);
			break;
		default:
			throw new EpException(MessageConst.ERROR_721041, new String[] {
					labelText, supplier.getRegNo() });
		}
	}

	private void handleDifferentTypeMoF(List<SmSupplier> suppliers,
			SmSupplier supplier, String currentType, String existingType) {
		if (suppliers.size() > 1) {
			checkMultipleSuppliersForBasicContractor(suppliers, currentType);
		}

		if (suppliers.size() == 1) {
			onlyOneSupplierExist(existingType, currentType, supplier);
		} else {
			errorMessageSSM(supplier);
		}
	}
	
	private void checkMultipleSuppliersForBasicContractor(
			List<SmSupplier> suppliers, String currentType) {

		for (SmSupplier supp : suppliers) {
			String suppType = supp.getSmAppl().getSupplierType();

			if (SupplierManagementConst.STYPE_CONTRACTOR
					.equalsIgnoreCase(currentType)
					&& SupplierManagementConst.STYPE_BASIC
							.equalsIgnoreCase(suppType)) {

				int status = supp.getRecordStatusFlg();
				String regNo = supp.getRegNo();

				if (status == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
					throw new EpException(MessageConst.ERROR_721063,
							new String[] { regNo });
				} else if (status == DbValueConst.RECORD_STATUS_SUSPENDED) {
					throw new EpException(MessageConst.ERROR_721074,
							new String[] { regNo });
				} else {
					throw new EpException(MessageConst.ERROR_721042,
							new String[] { regNo });
				}
			}
		}

	}

	private void onlyOneSupplierExist(String existingType, String currentType,
			SmSupplier supplier) {
		String labelText = getSupplierTypeLabel(existingType);
		int status = supplier.getRecordStatusFlg();

		if (isConsultant(currentType, existingType)) {
			if (status == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
				handleInProgressDifferentType(supplier, labelText);
			}
			return; // silently ignore other statuses
		}

		switch (status) {
		case DbValueConst.RECORD_STATUS_SUSPENDED:
			throw new EpException(MessageConst.ERROR_721074,
					new String[] { supplier.getRegNo() });
		case DbValueConst.RECORD_STATUS_ACTIVE:
			throw new EpException(MessageConst.ERROR_721042,
					new String[] { getManagedBean().getSmSupp().getRegNo() });
		case DbValueConst.RECORD_STATUS_IN_PROGRESS:
			handleInProgressDifferentType(supplier, labelText);
			break;
		default:
			errorMessageSSM(supplier);
			break;
		}
	}

	private boolean isConsultant(String currentType, String existingType) {
		boolean isCurrentConsultant = currentType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT);
		boolean isExistingConsultant = existingType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT);
		return isCurrentConsultant != isExistingConsultant;
	}

	private void handleInProgressStatus(SmSupplier supplier, String labelText) {
		if (StringUtils.isBlank(supplier.getEpNo())) {
			if (supplier.getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				throw new EpException(MessageConst.ERROR_721075,
						new String[] {
								SmCommonUtil.getMessage(supplier.getRegNo(),
										null), "" });
			} else {
				throw new EpException(MessageConst.ERROR_721068,
						new String[] {
								SmCommonUtil.getMessage(supplier.getRegNo(),
										null), "" });
			}

		} else {
			throw new EpException(MessageConst.ERROR_721083, new String[] {
					supplier.getRegNo(), labelText });
		}
	}

	private void handleInProgressDifferentType(SmSupplier supplier,
			String labelText) {
		if (StringUtils.isBlank(supplier.getEpNo())) {
			if (supplier.getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)) {
				throw new EpException(MessageConst.ERROR_721068,
						new String[] {
								SmCommonUtil.getMessage(supplier.getRegNo(),
										null), "" });
			} else {
				throw new EpException(MessageConst.ERROR_721062, new String[] {
						supplier.getRegNo(), labelText });
			}

		} else {
			throw new EpException(MessageConst.ERROR_721064, new String[] {
					supplier.getRegNo(), labelText, labelText });
		}
	}

	private void validateForBasic(List<SmSupplier> suppliers) {
		for (SmSupplier supplier : suppliers) {
			String existingType = supplier.getSmAppl().getSupplierType();
			int status = supplier.getRecordStatusFlg();

			if (existingType
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				if (status == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
					validateInProgressBasic(supplier);
				}
				continue; // Skip other statuses for consultant
			}

			handleBasicSupplierByStatus(supplier, status);
		}
	}

	private void handleBasicSupplierByStatus(SmSupplier supplier, int status) {
		String regNoMsg = SmCommonUtil.getMessage(supplier.getRegNo(), null);

		switch (status) {
		case DbValueConst.RECORD_STATUS_ACTIVE:
			handleBasicActiveStatus(supplier, regNoMsg);
			break;
		case DbValueConst.RECORD_STATUS_SUSPENDED:
			throw new EpException(MessageConst.ERROR_721074,
					new String[] { regNoMsg });
		case DbValueConst.RECORD_STATUS_IN_PROGRESS:
			validateInProgressBasic(supplier);
			break;
		case DbValueConst.RECORD_STATUS_PENDING_ACTIVATION:
			throw new EpException(MessageConst.ERROR_721065,
					new String[] { regNoMsg });
		case DbValueConst.RECORD_STATUS_EXPIRED:
			validateExpiredBasic(supplier);
			break;
		default:
			// Optional logging or silent ignore
			break;
		}
	}
	
	private void handleBasicActiveStatus(SmSupplier supplier, String regNoMsg) {
		if (SupplierManagementConst.STYPE_CONTRACTOR.equalsIgnoreCase(supplier
				.getSmAppl().getSupplierType())) {
			if (checkIfMofExp(supplier)) {
				errorMessageSSM(supplier);
			} else {
				// Since if existing status 1 for K need different message if
				// going errorMessageSSM first can have different message
				throw new EpException(MessageConst.ERROR_721066,
						new String[] { regNoMsg });
			}
		} else {
			errorMessageSSM(supplier);
			throw new EpException(MessageConst.ERROR_721066,
					new String[] { regNoMsg });
		}
	}
	
	private boolean checkIfMofExp(SmSupplier supp) {
	    if (supp.getSmAppl() == null 
	        || supp.getSmAppl().getSmMofCerts() == null) {
	        return false;
	    }

	    String suppType = supp.getSmAppl().getSupplierType();
		if (!SupplierManagementConst.STYPE_CONSULTANT
				.equalsIgnoreCase(suppType)
				&& !SupplierManagementConst.STYPE_CONTRACTOR
						.equalsIgnoreCase(suppType)) {
			return false;
		}

	    for (SmMofCert cert : supp.getSmAppl().getSmMofCerts()) {
	        if (cert.getSmMofAccount().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_EXPIRED) {
	            return true;
	        }
	    }

	    return false;
	}

	private void validateInProgressBasic(SmSupplier supplier) {
		String labelText = getSupplierTypeLabel(supplier.getSmAppl()
				.getSupplierType());

		if (StringUtils.isBlank(supplier.getEpNo())) {
			throw new EpException(MessageConst.ERROR_721068, new String[] {
					SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
		} else {
			throw new EpException(MessageConst.ERROR_721083, new String[] {
					supplier.getRegNo(), labelText });
		}
	}

	private void validateExpiredBasic(SmSupplier supplier) {
		if (supplier.getSmMofAccount() != null
				&& supplier.getSmMofAccount().getExpDate() != null) {
			long days = DateUtils.getDaysDifference(supplier.getSmMofAccount()
					.getExpDate(), new Date());
			if (days <= 365) {
				throw new EpException(MessageConst.ERROR_721069,
						new String[] {
								SmCommonUtil.getMessage(supplier.getRegNo(),
										null), "" });
			} else {
				throw new EpException(MessageConst.ERROR_721072,
						new String[] {
								SmCommonUtil.getMessage(supplier.getRegNo(),
										null), "" });
			}
		} else {
			throw new EpException(MessageConst.ERROR_721069, new String[] {
					SmCommonUtil.getMessage(supplier.getRegNo(), null), "" });
		}
	}

	private String getSupplierTypeLabel(String supplierType) {
		if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
			return SmCommonUtil.getMessage("lbl.sm.contractor", null);
		} else if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			return SmCommonUtil.getMessage("lbl.sm.consultant", null);
		} else {
			return SmCommonUtil.getMessage("lbl.sm.basic", null);
		}
	}

	// #38143 - End
	
	/**
	 * populate indiviual Service Type
	 */
	public void errorMessageSSM(SmSupplier supp){
		//IF MOF CHECK MOF EXPIRED FIRST
		if(supp.getSmAppl() != null){
			//#38143 - Start
			if(supp.getSmAppl().getApplType()
					.equalsIgnoreCase(SupplierManagementConst.ACC_MOF)
					|| supp.getSmAppl()
							.getApplType()
							.equalsIgnoreCase(
									SupplierManagementConst.ACC_BASIC_REACTIVATE)
					|| supp.getSmAppl()
							.getSupplierType()
							.equalsIgnoreCase(
									SupplierManagementConst.STYPE_CONTRACTOR)
					|| supp.getSmAppl()
							.getSupplierType()
							.equalsIgnoreCase(
									SupplierManagementConst.STYPE_CONSULTANT)){
				//#38143 - End
				if(supp.getSmAppl().getSmMofCerts() != null){
					boolean expiredBelowOneYear = false;
					//boolean expiredMoreOneYear = false;
					for(SmMofCert cert:supp.getSmAppl().getSmMofCerts()){
						if(cert.getSmMofAccount() != null){
							if(cert.getSmMofAccount().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
								expiredBelowOneYear = false;
								//expiredMoreOneYear = false;
								break;
							}else if(cert.getSmMofAccount().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_EXPIRED){
								if(DateUtils.getDaysDifference(cert.getSmMofAccount().getExpDate(), new Date())<=365){
									expiredBelowOneYear = true;
								}else{
									//expiredMoreOneYear = true;
								}
							}
						}
					}
					if(expiredBelowOneYear){
						throw new EpException(MessageConst.ERROR_721069,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
					}
				}
			}
		}
		
		if(supp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE){
			throw new EpException(MessageConst.ERROR_721073,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
		}
		//b)	If registration number exist and account is inactive 
		else if(supp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED){
			throw new EpException(MessageConst.ERROR_721067,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
		}
		//c)	If account already created but has not been activated, 
		else if(supp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION){
			throw new EpException(MessageConst.ERROR_721075,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
		}
		//d)	If registration number exist and MoF Account has been expired
		else if(supp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_EXPIRED){
			throw new EpException(MessageConst.ERROR_721076,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
			
		}else if(supp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS){
			if(supp.getEpNo() ==null ||"".equalsIgnoreCase(supp.getEpNo())){
				throw new EpException(MessageConst.ERROR_721075,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
			}else{
				throw new EpException(MessageConst.ERROR_721073,new String[]{SmCommonUtil.getMessage(supp.getRegNo(),null), ""});
			}
		}
	}
	public void getIndividualServiceType(){	
		List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SPECIAL_SERVICE_CODE, new FacesScopes().getUserLocale().getLanguage());
		this.getManagedBean().setServiceLs(paramDesc);
	}
	
	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateStateByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			if(reset){
				getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
				 //#36718 - START
				if(!getManagedBean().getSmBusinessAdd().isSsmData()){
					getManagedBean().getSmBusinessAdd().setPostcode("");
				}
				getManagedBean().getSmBusinessAdd().setMapLatitude("");
				getManagedBean().getSmBusinessAdd().setMapLongitude("");
				 //#36718 - END
			}
			if(this.getManagedBean().getSmBusinessAdd().getPmCountry() != null){
				pmStateDropdownValues(false);//#36718 - add boolean reset to reset address or not
			}
			
			//registrationBackingBean.managedBean.smBusinessAdd.pmState.
			if(getManagedBean().getSmBusinessAdd().isBrandNew()){
				getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
				getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (typeAdd.equals("residential")) {
			 //#36718 - START
			if(reset){
				getManagedBean().getSmResAdd().setPostcode("");
			}
			 //#36718 - END
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		} else if (typeAdd.equals("permenant")) {
			 //#36718 - START
			if(reset){
				getManagedBean().getSmPermenantAdd().setPostcode("");
			}
			 //#36718 - END
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmPermenantAdd().getPmCountry().getCountryId());
			this.getManagedBean().setPermenantStateLs(stateLs);
			//registrationBackingBean.managedBean.smPermenantAdd.pmCountry.countryId
			if(getManagedBean().getSmPermenantAdd().isBrandNew()){
				if(getManagedBean().getSmPermenantAdd() == null){
					this.getManagedBean().setSmPermenantAdd(new SmAddress());
					this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
					PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
					this.getManagedBean().getSmPermenantAdd().setPmCountry(defaultCountry);
					this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
					
				}
				//#34159 - START
				else{
					if(this.getManagedBean().getSmPermenantAdd().getPmCountry().getCountryId() == null){
						this.getManagedBean().setPermenantStateLs(new ArrayList<SelectItem>());
					} 
				}
				
				this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
				//this.getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());
				this.getManagedBean().setPermenantDivisionLs(new ArrayList<SelectItem>());
				//#34159 - END
				////#16052019 - SSM INTEGRATION CR
				this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
				//this.getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());//13320
				getManagedBean().getSmPermenantAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmPermenantAdd().getPmCity().setCityId(null);
				//getManagedBean().getSmPermenantAdd().getPmDivision().setDivisionId(null);//13320
				getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
			}
		}		
	}

	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permanent
	 */
	public void populateDistrictByAddType(String typeAdd, boolean reset) {
				
		if (typeAdd.equals("business")) {
			//13320
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
				//#34159 - START
				getManagedBean().getSmBusinessAdd().setPmDivision(null);
				getManagedBean().setBusinessDivisionLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
				populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
				getManagedBean().getSmSupp().setLocalAuthorityId(null);
				//#34159 - END
				 //#36718 - START
				if(!getManagedBean().getSmBusinessAdd().isSsmData()){
					getManagedBean().getSmBusinessAdd().setPostcode("");
				}
				getManagedBean().getSmBusinessAdd().setMapLatitude("");
				getManagedBean().getSmBusinessAdd().setMapLongitude("");
				 //#36718 - END
			}
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId()));
			getManagedBean().getSmBusinessAdd().getPmState().setAdoptDivision(false);
			if(state != null){
				if(state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
										
					//#13320 - Rahim Added If Only and Else(content)
					if(this.getManagedBean().getSmBusinessAdd().getPmDivision()==null) {
						getManagedBean().getSmBusinessAdd().setPmDivision(new PmDivision());
					} else {
						//start - #30483
						if (this.getManagedBean().getSmBusinessAdd().getAddressId() != null) {//#30964-start
							ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
									.getServiceObject("sm", ISupplierSearchSO.class);
							SmAddress smAddress = supplierSearchSO.retrieveSmAddressByAddressId(this.getManagedBean()
									.getSmBusinessAdd().getAddressId());
							List<SelectItem> districtLs = SmCommonUtil.getDistrictListByID(smAddress.getPmDivision()
									.getDivisionId());
							// end - #30483
							this.getManagedBean().setBusinessDistrictLs(districtLs);
						}//#30964-end
					}
															
					List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
					this.getManagedBean().setBusinessDivisionLs(divisionLs);
					
					//#13320 - Rahim Commented Out
					//getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());					
					//this.getManagedBean().setBusinessCityLs(null);
					
					//#13320 - Rahim Added
					this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());

					getManagedBean().getSmBusinessAdd().getPmState().setAdoptDivision(true);
					
				} else {
					this.getManagedBean().getSmBusinessAdd().setPmState(SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId()));
					List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
					this.getManagedBean().setBusinessDistrictLs(districtLs);
					this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
				}
			}
				
		} else if (typeAdd.equals("residential")) {
			//13320
			if(reset){
				getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
				getManagedBean().getSmResAdd().getPmCity().setCityId(null);
				this.getManagedBean().getSmResAdd().setPmDivision(null); //#34159
				getManagedBean().getSmResAdd().setPostcode("");  //#36718
			}
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmResAdd().getPmState().getStateId()));
			getManagedBean().getSmResAdd().getPmState().setAdoptDivision(false);
			if(state != null){
			if(state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());//#34159
				//#13320 - Rahim Added If Only and Else(content)
				if(this.getManagedBean().getSmResAdd().getPmDivision()==null) {
					getManagedBean().getSmResAdd().setPmDivision(new PmDivision());
				} else {
					List<SelectItem> districtLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmResAdd().getPmDivision().getDivisionId());
					this.getManagedBean().setResidentialDistrictLs(districtLs);
				}
				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmResAdd().getPmState().getStateId());
				this.getManagedBean().setResidentialDivisionLs(divisionLs);
				
				//#13320 - Rahim Commented Out
				//getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
				//this.getManagedBean().setResidentialCityLs(null);
				
				//#13320 - Rahim Added
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
				
				getManagedBean().getSmResAdd().getPmState().setAdoptDivision(true);
			}else{
				    this.getManagedBean().getSmResAdd().setPmState(SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmResAdd().getPmState().getStateId()));
					List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmResAdd().getPmState().getStateId());
					this.getManagedBean().setResidentialDistrictLs(districtLs);
					//#34159 - START
					//this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
					//#34159 - END
				}
				
			}
			//#34159 - START
			 else {
				 this.getManagedBean().setResidentialDivisionLs(new ArrayList<SelectItem>());
				 this.getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
				 this.getManagedBean().getSmResAdd().setPmDivision(new PmDivision());
			}
			this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
			//#34159 - END
		} else if (typeAdd.equals("permenant")) {
			//13320
			if(reset){
				getManagedBean().getSmPermenantAdd().setPmDivision(null); //#34159 
				getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
				getManagedBean().getSmPermenantAdd().getPmCity().setCityId(null);
				getManagedBean().getSmPermenantAdd().setPostcode("");  //#36718
			}
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId()));
			getManagedBean().getSmPermenantAdd().getPmState().setAdoptDivision(false);
			if(state !=null){
				if(state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
					getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());//#34159
					//#13320 - Rahim Added If Only and Else(content)
					if(this.getManagedBean().getSmPermenantAdd().getPmDivision()==null) {
						getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());
					} else {
						List<SelectItem> districtLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmPermenantAdd().getPmDivision().getDivisionId());
						this.getManagedBean().setPermenantDistrictLs(districtLs);
					}
					
					List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
					this.getManagedBean().setPermenantDivisionLs(divisionLs);
					
					//#13320 - Rahim Commented Out
					//getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
					//this.getManagedBean().setPermenantCityLs(null);	
					
					//#13320 - Rahim Added
					this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
					
					getManagedBean().getSmPermenantAdd().getPmState().setAdoptDivision(true);
				}else{  
					   this.getManagedBean().getSmPermenantAdd().setPmState(SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId()));
						List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
						this.getManagedBean().setPermenantDistrictLs(districtLs);
						//#34159 - START
						//this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
						//#34159 - END
	
						log.info("------permenant LS-------------" + this.getManagedBean().getPermenantDistrictLs().size());
				}	
			}
			//#34159 - START
			else {
				this.getManagedBean().setPermenantDivisionLs(new ArrayList<SelectItem>());
				this.getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmPermenantAdd().setPmDivision(new PmDivision());
			}
			//#38079 START
			if(reset){
				this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
				this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
			}//#38079 END
			//#34159 - END
		}
			
	}
	

	/**
	 * set City By AddType
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateCityByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				//#34159 - Start
				populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
				getManagedBean().getSmSupp().setLocalAuthorityId(null);
				//#34159 - End
				 //#36718 - START
				if(!getManagedBean().getSmBusinessAdd().isSsmData()){
					getManagedBean().getSmBusinessAdd().setPostcode("");
				} 
				getManagedBean().getSmBusinessAdd().setMapLatitude("");
				getManagedBean().getSmBusinessAdd().setMapLongitude("");
				 //#36718 - END
			}else{ //start #30964
				// start - #30483
				PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd()
						.getPmState().getStateId()));
				if (state != null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
							.getServiceObject("sm", ISupplierSearchSO.class);
					if (this.getManagedBean().getSmBusinessAdd() != null && this.getManagedBean()
							.getSmBusinessAdd().getAddressId()!=null) { //#30726
						SmAddress smAddress = supplierSearchSO.retrieveSmAddressByAddressId(this.getManagedBean()
								.getSmBusinessAdd().getAddressId());
						this.getManagedBean().setSmBusinessAdd(smAddress);
					}
				} //end #30964
				// end - #30483
			}
			//#34159 - START
			if(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId()== null){
				this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
				this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			} else {
				this.getManagedBean().getSmBusinessAdd().setPmDistrict(SmCommonUtil.getInstance().getDistrictById(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId()));
				List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId());
				this.getManagedBean().setBusinessCityLs(cityLs);
			}
			populateLocalAuthority(getManagedBean().getSmSupp(), this.getManagedBean().getSmBusinessAdd());
		} else if (typeAdd.equals("residential")) {
			if(reset){
				getManagedBean().getSmResAdd().setPostcode("");
			}
			if(this.getManagedBean().getSmResAdd().getPmDistrict().getDistrictId() == null){
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
			} else {
				List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmResAdd().getPmDistrict().getDistrictId());
				this.getManagedBean().setResidentialCityLs(cityLs);
			}
		} else if (typeAdd.equals("permenant")) {
			if(reset){
				getManagedBean().getSmPermenantAdd().setPostcode("");
			}
			if(this.getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId() == null){
				this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
				this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
			} else {
				List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId());
				this.getManagedBean().setPermenantCityLs(cityLs);
			}
			//#34159 - END
		}
	}
	
	public void onSelectCity(String typeAdd){
		if (typeAdd.equals("business")) {
			//#34159 - START
			if(this.getManagedBean().getSmBusinessAdd().getPmCity().getCityId() != null){
				this.getManagedBean().getSmBusinessAdd().setPmCity(SmCommonUtil.getInstance().getCityById(this.getManagedBean().getSmBusinessAdd().getPmCity().getCityId()));
			} else {
				this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			}
			populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
			getManagedBean().getSmSupp().setLocalAuthorityId(null);
			//#34159 - END
			 //#36718 - START
			if(!getManagedBean().getSmBusinessAdd().isSsmData()){
				getManagedBean().getSmBusinessAdd().setPostcode("");
			}
			getManagedBean().getSmBusinessAdd().setMapLatitude("");
			getManagedBean().getSmBusinessAdd().setMapLongitude("");
		}
		if(typeAdd.equals("residential")){
			getManagedBean().getSmResAdd().setPostcode("");
		}
		if(typeAdd.equals("permenant")){
			getManagedBean().getSmPermenantAdd().setPostcode("");
		}
		 //#36718 - END
	}

	
	//16052019 - SSM INTEGRATION CR - Fahmi
	public void goToViewOnlineAccount(){
		getManagedBean().setRegAccountMaintainance(true);
		fromtncProceed = true;
		SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
		if(SmCommonUtil.getInstance().ssmActive() && businessTypeTovalidateWithSSM(smSupp.getRegNo(),smSupp.getBusinessType())){
			this.getManagedBean().setCompanyValidWithSSM(true);
			
			//16052019 - SSM INTEGRATION CR 
			//checking with ssm
			SsmReqDTO ssmReqDTO = new SsmReqDTO();
			ssmReqDTO.setRegNo(smSupp.getRegNo());
			ssmReqDTO.setBusinessType(smSupp.getBusinessType());
			ssmReqDTO.setCheckOnly(true);
			SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
			if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
				//check the business/company registration status in ssm
				this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
				if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
					//27382 - start
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
						throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}
					//27382 - Terminated 
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
						throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
					}
					//27382 - Dissolved Conversion to LLP
					if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
					if(businessTypeIsROC(smSupp.getBusinessType())){
						throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
					}
					if(businessTypeIsROB(smSupp.getBusinessType())){
						throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
					}
					}
					else{ 
					throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
					}
				//27382 - end
			} else{
				SmCommonUtil.ssmError(ssmResDTO);
			}
		}
		viewOnlineAccount();
	}
	/**
	 * view supplier basic account
	 */
	public void viewOnlineAccount() {
		//16052019 - SSM INTEGRATION CR - Fahmi
		if(this.getManagedBean().isViewMode()){
			fromtncProceed = true;//to set, if from view mode, no need show tnc
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		//27691 START
		this.getManagedBean().setSmPersonnelOwners(new ArrayList<SmPersonnel>());
		this.getManagedBean().setSmPersonnelDirector(new ArrayList<SmPersonnel>());
		this.getManagedBean().setDelSuppOwnerLs(new ArrayList<SmSupplierOwner>());
		this.getManagedBean().setSupplierOwnerls(new ArrayList<SmSupplierOwner>());
		this.getManagedBean().setAddSuppOwnerLs(new ArrayList<SmSupplierOwner>());
		//27691 END
		if(pmUser.getUserId().longValue() != 1 || pmUser == null){
			
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			//846846516516
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
			//16052019 - SSM INTEGRATION CR - Fahmi
			//businessTypeTovalidateWithSSM(smSupp.getRegNo(),smSupp.getBusinessType()) &&
			if(!fromtncProceed){
				if(smSupp.getBusinessType() != null && smSupp.getBusinessType().equals("Q")){
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceG2GTermAndCond.xhtml");
				}else {
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceTermAndCond.xhtml");
				}
				return;
			}
			//for govt seller basic account maintenance
			if(smSupp.getBusinessType().equals("Q")){
				GovtSellerRegistrationBackingBean govtSellerRegistrationBackingBean = new GovtSellerRegistrationBackingBean();
				govtSellerRegistrationBackingBean.preRenderGovtSellerMaintenance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/govtSellerRegistration/govtSellerMaintenanceBasePage.xhtml");
			}else{

				RegistrationReqDTO reqDTO = new RegistrationReqDTO();				
				reqDTO.setSupplierId(smSupp.getSupplierId());

				IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
				RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

				this.getManagedBean().setSmSupp(resDTO.getSmSupp());
				this.getManagedBean().setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
				this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
				getManagedBean().setApplType(getManagedBean().getSmAppl().getApplType());
				
				//27691 start - to show owner & director info for NON SSM Company
				if(!resDTO.getSmSupp().getSmAppl().isApplValidWithSsm()){
					for(SmPersonnel personnel : this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels()){
						Boolean ownerExist = false;
						for(SmSupplierOwner suppOwner : this.getManagedBean().getSmSupp().getSmSupplierOwners()){
							if(suppOwner!=null && personnel.getIdentificationNo().equals(suppOwner.getIdentificationNo())){
								ownerExist = true;
								if(suppOwner.isShowTextField()){ //#27691 - set show text field to false for existing data
									suppOwner.setShowTextField(false);
								}
								break;
							}
						}
						if(!ownerExist && (personnel.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE ||
								personnel.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) &&
								personnel.getPmCountry().getCountryCode().equalsIgnoreCase("MY") &&
								(personnel.isEquityOwner() || personnel.isDirector())){
							SmSupplierOwner suppOwnerDirector = new SmSupplierOwner();
							PmParameter identificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
							suppOwnerDirector.setSmSupplier(this.getManagedBean().getSmSupp());
							suppOwnerDirector.setOwnerName(personnel.getName());
							suppOwnerDirector.setNationality(personnel.getPmCountry());
							suppOwnerDirector.setIdentificationType(identificationType);
							suppOwnerDirector.setIdentificationTypeId(identificationType.getParameterId());
							suppOwnerDirector.setIdentificationNo(personnel.getIdentificationNo());
							this.getManagedBean().getSmSuppOwners().add(suppOwnerDirector);
							resDTO.getSmSupp().getSmSupplierOwners().add(suppOwnerDirector);
						}
						if(personnel.getRevNo() == 0 && !personnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){ //#27691 Set rev_no=1 
							personnel.setRevNo(1);
						}
					}
				}
				//27691 END
				//26238 - retrieve active DA. To display at basic account supplier screen
				retrieveActiveDisciplinaryAction(resDTO.getSmSupp());
				if(resDTO.getSmSupp().getSmSupplierOwners()!=null && CollectionUtils.isNotEmpty(resDTO.getSmSupp().getSmSupplierOwners())){
					// #27691 - Start
					if(!resDTO.getSmSupp().getSmAppl().isApplValidWithSsm()){
						//to handle data that have same ic with same appl id
						List<SmPersonnel> duplicatePersonnelList = new ArrayList<>();
				        for(SmPersonnel pers : resDTO.getSmSupp().getSmAppl().getSmPersonnels()){
				        	for(SmPersonnel allPers : resDTO.getSmSupp().getSmAppl().getSmPersonnels()){
				        		if(pers.getIdentificationNo().equals(allPers.getIdentificationNo())
				        				&& pers.getCreatedDate().compareTo(allPers.getCreatedDate()) < 0){
				        			duplicatePersonnelList.add(pers);
				        		}
				        	}
				        }
						// for view director.Display supplier owner with record status 1 or 8 only
						for (SmSupplierOwner smSuppOwner: resDTO.getSmSupp().getSmSupplierOwners()){
							if(smSuppOwner.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_DELETED
									||smSuppOwner.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED){
								this.getManagedBean().getSupplierOwnerls().add(smSuppOwner);
								}	
							for (SmPersonnel smPersonnel: resDTO.getSmSupp().getSmAppl().getSmPersonnels()){
								if(smSuppOwner.getIdentificationNo().equals(smPersonnel.getIdentificationNo())
										&& !duplicatePersonnelList.contains(smPersonnel)){ //#27691
									smSuppOwner.setHasPersonnel(true);
									smSuppOwner.setOwnerIsEquity(smPersonnel.isEquityOwner());
									smSuppOwner.setOwnerIsDirector(smPersonnel.isDirector());
									//#27691 START - fix with correct checkbox
									if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)){
										if(smPersonnel.isEquityOwner()){
											smSuppOwner.setOwnerIsEquity(false);
											smSuppOwner.setOwnerIsDirector(true);
										}
									}else if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) 
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH) 
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
										if(smPersonnel.isDirector()){
											smSuppOwner.setOwnerIsEquity(true);
											smSuppOwner.setOwnerIsDirector(false);
										}
									}
								}
								//to show correct tick checkbox for owner that not have personnel save
								if(!smSuppOwner.isHasPersonnel()){
									if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) ||
											this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)){
										smSuppOwner.setOwnerIsEquity(false);
										smSuppOwner.setOwnerIsDirector(true);
									}else if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) 
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH) 
											|| this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
										smSuppOwner.setOwnerIsEquity(true);
										smSuppOwner.setOwnerIsDirector(false);
									}
								}
							}
						}
						//filter personnel that not have role but exist in table SmSupplierOwner
						Iterator<SmSupplierOwner> ownerLs = resDTO.getSmSupp().getSmSupplierOwners().iterator();
						while (ownerLs.hasNext()) {
							SmSupplierOwner selectedOwner = ownerLs.next();
							if(selectedOwner.isHasPersonnel() &&
									!selectedOwner.isOwnerIsEquity() && !selectedOwner.isOwnerIsDirector()){
								ownerLs.remove();
							}
						}
						this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners() == null ? resDTO.getSmSupp().getSmSupplierOwners().get(0) : null);
						this.getManagedBean().setSmSuppOwners(resDTO.getSmSupp().getSmSupplierOwners());
						resDTO.getSmSupp().getSmSupplierOwners().removeAll(this.getManagedBean().getSupplierOwnerls());
					}
				// #27691 - End
					this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners() == null ? resDTO.getSmSupp().getSmSupplierOwners().get(0) : null);
					// start #29956
					//27691 START
					if (SmCommonUtil.getInstance().ssmActive()
							&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupp().getRegNo(), this
							.getManagedBean().getSmSupp().getBusinessType())) {
						this.getManagedBean().setSmSuppOwners(new ArrayList<SmSupplierOwner>());
					}
					//27691 END
					// end #29956
					// for view owner
					if(this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm()){
						this.getManagedBean().setShowOwnerPersonnel(true);
						for(SmPersonnel personnel: this.getManagedBean().getSmAppl().getSmPersonnels()){
							if (personnel.isEquityOwner()
									&& personnel.getIdentificationType() != null				//start - #30064
									&& personnel.getIdentificationType().getParameterCode()		
											.equals(ParameterConst.IDENTIFICATION_TYPES_IC)		//end - #30064
											&& !personnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)) {	//#27691
								if(personnel.getPmCountry() == null){
									PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
									personnel.setPmCountry(defaultCountry);
								}
								// start #29956
								if (!resDTO.getSmSupp().getSmSupplierOwners().isEmpty()
										&& businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupp().getRegNo(),
												this.getManagedBean().getSmSupp().getBusinessType())) {
									for (SmSupplierOwner smSuppOwner : resDTO.getSmSupp().getSmSupplierOwners()) {
										if (personnel.getIdentificationNo().equals(smSuppOwner.getIdentificationNo())) { //#27691
											this.getManagedBean().getSmSuppOwners().add(smSuppOwner);
											// 27691 - Start
											smSuppOwner.setOwnerIsEquity(personnel.isEquityOwner());
											// 27691 - End
										}
									}
								}
								// end #29956
								//27691 START - readd owner if not exist in supplier owner table
								if(personnel.isSsmData()){
									Boolean existSuppOwner = false;
									for(SmSupplierOwner smSuppOwner : resDTO.getSmSupp().getSmSupplierOwners()){
										if(personnel.getIdentificationNo().equalsIgnoreCase(smSuppOwner.getIdentificationNo())){
											existSuppOwner = true;
										}
									}
									if(!existSuppOwner){
										SmSupplierOwner suppOwner = new SmSupplierOwner();
										
										suppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
										suppOwner.setOwnerName(personnel.getName());
										suppOwner.setNationality(personnel.getPmCountry());
										suppOwner.setIdentificationType(personnel.getIdentificationType());
										suppOwner.setIdentificationTypeId(personnel.getIdentificationTypeId());
										suppOwner.setIdentificationNo(personnel.getIdentificationNo());
										this.getManagedBean().getSmSuppOwners().add(suppOwner);
										resDTO.getSmSupp().getSmSupplierOwners().add(suppOwner);
									}
								}
								//27691 END
								this.getManagedBean().getSmPersonnelOwners().add(personnel);
							}
							// 27691 - Start #29582 add isForeigner
							if (smSupp.getBusinessType().equals("J")
									|| smSupp.getBusinessType().equals("I")){
								if (personnel.isDirector() && personnel.getIdentificationType() != null			
										&& personnel.getIdentificationType().getParameterCode()		
										.equals(ParameterConst.IDENTIFICATION_TYPES_IC)
										&& !personnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)
										&& personnel.isForeigner()){
									this.getManagedBean().getSmPersonnelDirector().add(personnel);
								}
							}
							if(personnel.getRevNo() == 0 && !personnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){ //#27691 set rev_no=1
								personnel.setRevNo(1);
							}
							// 27691 - End
						}
					}
				}else{//27691 START - to view owner&director if smsupplierowner empty
					if(this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm()){
						this.getManagedBean().setSmSuppOwners(new ArrayList<SmSupplierOwner>());
						for(SmPersonnel personnel: this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels()){
							if(personnel.isSsmData() && (personnel.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE ||
									personnel.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
									&& personnel.getIdentificationType() != null && personnel.getIdentificationType().getParameterCode()		
											.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
								if(personnel.isDirector()){
									this.getManagedBean().getSmPersonnelDirector().add(personnel);
								}
								if(personnel.isEquityOwner()){
									SmSupplierOwner suppOwner = new SmSupplierOwner();
									suppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
									suppOwner.setOwnerName(personnel.getName());
									suppOwner.setNationality(personnel.getPmCountry());
									suppOwner.setIdentificationType(personnel.getIdentificationType());
									suppOwner.setIdentificationTypeId(personnel.getIdentificationTypeId());
									suppOwner.setIdentificationNo(personnel.getIdentificationNo());
									this.getManagedBean().getSmSuppOwners().add(suppOwner);
									resDTO.getSmSupp().getSmSupplierOwners().add(suppOwner);
								}
							}
						}
					}
				}
				//27691 END
				if(resDTO.getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
					SmPersonnel personnel = getManagedBean().getSmAppl().getSmPersonnels().get(0);
					getManagedBean().setSmPersonnel(personnel);
					getManagedBean().setUserBumiFlg(personnel.getBumiFlg());
					getManagedBean().setUserMuslimFlg(personnel.getMuslimFlg());
					getIndividualServiceType();
					this.getManagedBean().setSmPermenantAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					this.populateStateByAddType("permenant", false);
					this.populateDistrictByAddType("permenant", false);
					this.populateCityByAddType("permenant", false);

					this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					this.populateStateByAddType("business", false);
					this.populateDistrictByAddType("business", false);
					this.populateCityByAddType("business", false);
				}else{
					
					if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
						this.getManagedBean().setSabahSarawakType(true);
					}
					
					// start - #30483
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
							.getServiceObject("sm", ISupplierSearchSO.class);
					SmAddress smAddress = supplierSearchSO.retrieveSmAddressByAddressId(resDTO.getSmSupp().getSmAppl()
							.getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddressId());
					this.getManagedBean().setSmBusinessAdd(smAddress);
					// end - #30483
					
					this.populateStateByAddType("business", false);		
					
					//#13320 - Rahim Added If Statement
					if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)) {
						this.populateDivisionByAddType("business", false);
					}
										
					this.populateDistrictByAddType("business", false);				
					this.populateCityByAddType("business", false);
					
				}
				
				this.retrievePmCountryList();
				
				this.getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
				
				if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
					this.retrieveProfBody();
				}

				retrieveIdentityTypeMap();
				this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
				this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
				setDisableCompanyNameBusinessTypeInMaintenance();
				
				//retrieve Supplier GST Info
				initGstInfo(resDTO.getSmSupp(),getManagedBean(),false);
				getManagedBean().setGstFlag(false);
				//START CHANGES SSM
				
				if(!this.getManagedBean().isViewMode() && SmCommonUtil.getInstance().ssmActive() &&  businessTypeTovalidateWithSSM(this.getManagedBean().getSmSupp().getRegNo(),this.getManagedBean().getSmSupp().getBusinessType())){
										
					SsmReqDTO ssmReqDTO = new SsmReqDTO();
					ssmReqDTO.setRegNo(this.getManagedBean().getSmSupp().getRegNo());
					ssmReqDTO.setBusinessType(this.getManagedBean().getSmSupp().getBusinessType());
					//#29114, CR 27382: Static Code Analysis Start
					/*
					if(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType())){
						ssmReqDTO.setBasicAcc(true);
					}else{
						ssmReqDTO.setBasicAcc(false);
					}
					*/
					//#30964
					this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					ssmReqDTO.setBasicAcc(SupplierManagementConst.ACC_BASIC.equalsIgnoreCase(this.getManagedBean().getSmAppl().getApplType()));
					//#29114, CR 27382: Static Code Analysis End
					SsmResDTO ssmResDTO = SmCommonUtil.getInstance().validateWithSSM(ssmReqDTO);
					this.getManagedBean().setCompanyValidWithSSM(false);
					this.getManagedBean().setShowOwnerPersonnel(false);
					this.getManagedBean().setValidateWithSSM(true);
					if(ssmResDTO.getSeverity().equalsIgnoreCase("SUCC")){
						//POPUP USER NOT SSM OWNER #26734  START			
						//START #27239
						//START NEW CHANGES POPUP OWNER UPDATE ROLE		
						//ANY CHANGES HERE PLEASE CHANGES IN ViewApplHistoryBackingBean.redirectToReapplyNewAppl & viewOnlineAccount.RegistrationBackingBean & SmApplicationBackingBean.proceedToApplication
						List<SmPersonnel> personnelsRole = this.getManagedBean().getPersonnelsRole();
						//start #29956
						boolean showPopupDowngradeSSM = displayPopupDowngradeSSMOwner(getManagedBean().getSmSupp().getSmAppl(),ssmResDTO);

						if(showPopupDowngradeSSM) {
							getManagedBean().setSmSupplierSsm(this.getManagedBean().getSmSupp());	
							fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceTermAndCond.xhtml");
							return;								
						}
						//end #29956
						if(personnelsRole != null && personnelsRole.isEmpty()){
							for(SmPersonnel personnel :  this.getManagedBean().getSmAppl().getSmPersonnels()){
								//#29114, CR 27382: Static Code Analysis Start
								if (personnel.isEquityOwner() || personnel.isDirector()) { //29956
									//if(personnel.getEpRole() != null){
										SmPersonnel personnelSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelOwners(),"identificationNo");
										SmPersonnel personnelDirectorSSM = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelDirector(),"identificationNo");
										if(personnelDirectorSSM == null && personnelSSM == null){
											if(isMofAccEffDatePassedCurrDate(this.getManagedBean().getSmSupp())){
												if(personnel.getEpRole() !=  null && my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES.contains(personnel.getEpRole())){
													personnelsRole.add(personnel);
												}
											} else {
												personnelsRole.add(personnel);
											}
											
										}
									//}
								}
								//#29114, CR 27382: Static Code Analysis End
							}
						}else{
							//set latest role to personnel
							for(SmPersonnel personnel :  personnelsRole){
								SmPersonnel personnelActive = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), this.getManagedBean().getSmAppl().getSmPersonnels(), "identificationNo");
								if(personnelActive != null){
									//copy latest data to supplier admin
									String[] copyEquityOwner = {"equityOwner","director","clerical","higherManagement","management","downgradeRoleSsm"};
									EntityHelper.copyEntityPropertiesOnly(personnel,personnelActive,copyEquityOwner);
								}
							}
						} 
						// #26734 - END
						// #27239 - END
						
						this.getManagedBean().setStatusDescFromSSM(setStatusMessageFromSSM(ssmResDTO.getStatusFromSSM()));
						if(!ssmResDTO.getStatusFromSSM().equalsIgnoreCase("A") && !ssmResDTO.getStatusFromSSM().equalsIgnoreCase("E")){
							//27382 - start
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("L")){
								throw new EpException(MessageConst.ERROR_721097,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
							}
							//27382 - Terminated 
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("T")){
								throw new EpException(MessageConst.ERROR_721098,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});
							}
							//27382 - Dissolved Conversion to LLP
							if(ssmResDTO.getStatusFromSSM().equalsIgnoreCase("B")){
							if(businessTypeIsROC(smSupp.getBusinessType())){
								throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.b", null) });
							}
							if(businessTypeIsROB(smSupp.getBusinessType())){
								throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage("lbl.sm.status.dissolved", null) });
							}
							}
							else{ 
							throw new EpException(MessageConst.ERROR_721070,new String[]{SmCommonUtil.getMessage(this.getManagedBean().getStatusDescFromSSM(),null), ""});}
							}
						//27382 - end
						this.getManagedBean().setCompanyValidWithSSM(true);
						this.getManagedBean().getSmSupp().getSmAppl().setApplValidWithSsm(true);
						//SET COMPANY NAME
						String[] copyProp = {"companyName","robExpiryDate","authorizedCapital","bumiEquity","foreignCompanyEquity","foreignerEquity","govtEquity","govtUnitrustEquity","businessDesc","revenueSsmNull","robExpirySsmNull","authorizedCapitalSsmNull","companySsmStatus"};
						EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmCompanyBasic(),this.getManagedBean().getSmAppl().getSmCompanyBasic(),copyProp);
						//SET ADDRESS
						String[] copyPropAddress = {"address1","address2","address3","addressSsmNull","postcodeSsmNull","stateSsmNull","countrySsmNull","ssmData","postcode"};

						EntityHelper.copyEntityPropertiesOnly(ssmResDTO.getSmAddress(),this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress(),copyPropAddress);
						
						this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPostcode(ssmResDTO.getSmAddress().getPostcode());
						this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmState(ssmResDTO.getSmAddress().getPmState());
						//START 27488 
						if(ssmResDTO.getSmAddress().getPmState()!= null){
							this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setStateId(ssmResDTO.getSmAddress().getPmState().getStateId());
							this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setDistrictId(new Long (0));
							this.getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().setPmDistrict(new PmDistrict());
							this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
							//START #30964
							if (getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic() != null) {
								if (getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
										&& !getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()
												.isEmpty()) {
									SmCompanyAddress comAdd = getManagedBean().getSmSupp().getSmAppl().getSmCompanyBasic()
											.getSmCompanyAddresses().get(0);
									SmAddress addr = comAdd.getSmAddress();
									
									
									if(addr.getPmState().getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
									
										List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(addr.getPmState().getStateId());
										this.getManagedBean().setBusinessDivisionLs(divisionLs);
										this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId()));
										this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
									}else{
										this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(addr.getPmState().getStateId()));
									}									
								}
							}//END #30964
						}
						//END 27488
						this.getManagedBean().getSmSupp().getSmAppl().setApplValidWithSsm(true);
						//start - #29956
						if (ssmResDTO.getSmCompanyBasic() != null) {
							// set establish date
							this.getManagedBean().getSmAppl().getSmCompanyBasic()
									.setEstablishDate(ssmResDTO.getSmCompanyBasic().getEstablishDate());
						}
						//end - #29956
						
						//CHECK EQUITY OWNER EXIT IN SSM TO DISABLE CHECKBOX
						int i = 0;
						for(SmPersonnel personnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
							if(EntityHelper
									.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(),ssmResDTO.getSmPersonnelOwners(),"identificationNo") != null){
								personnel.setSsmData(true);
								this.getManagedBean().getSmAppl().getSmPersonnels().set(i, personnel);
							}
							i++;
						}
						//SET SMPERSONNEL
						if(ssmResDTO.getSmPersonnelAll()!=null){
							if(this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels()==null){
								this.getManagedBean().getSmSupp().getSmAppl().setSmPersonnels(new ArrayList<SmPersonnel>());
							}
							for(SmPersonnel personnel : ssmResDTO.getSmPersonnelAll()){
								//#27691 START
								//Show all personnel based on data current SSM personnel
								if(this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels() != null){
									Boolean existPerson = false;
									for(SmPersonnel currPersonnel : this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels()){
										if(personnel.getIdentificationNo().equals(currPersonnel.getIdentificationNo())){
											existPerson = true;
											if(personnel.isEquityOwner()){
												currPersonnel.setEquityOwner(personnel.isEquityOwner());
												//27691 start
												Boolean existOwner = false;
												if(this.getManagedBean().getSmSuppOwners() != null){
													for(SmSupplierOwner suppOwner : this.getManagedBean().getSmSuppOwners()){
														if(suppOwner.getIdentificationNo().equalsIgnoreCase(currPersonnel.getIdentificationNo())){
															existOwner = true;
														}
													}
													if(!existOwner && personnel.isSsmData() && personnel.getIdentificationType() != null			
															&& personnel.getIdentificationType().getParameterCode()		
															.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
														SmSupplierOwner suppOwner = new SmSupplierOwner();
														suppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
														suppOwner.setOwnerName(personnel.getName());
														suppOwner.setNationality(personnel.getPmCountry());
														suppOwner.setIdentificationType(personnel.getIdentificationType());
														suppOwner.setIdentificationTypeId(personnel.getIdentificationTypeId());
														suppOwner.setIdentificationNo(personnel.getIdentificationNo());
														this.getManagedBean().getSmSuppOwners().add(suppOwner);
														resDTO.getSmSupp().getSmSupplierOwners().add(suppOwner);
													}
												}
											}
											if(personnel.isDirector()){
												currPersonnel.setDirector(personnel.isDirector());
												Boolean existDirector = false;
												if(this.getManagedBean().getSmPersonnelDirector() != null){
													for(SmPersonnel personnelDir : this.getManagedBean().getSmPersonnelDirector()){
														if(personnelDir.getIdentificationNo().equalsIgnoreCase(currPersonnel.getIdentificationNo())){
															existDirector = true;
														}
													}
													if(!existDirector && personnel.isSsmData() && personnel.getIdentificationType() != null			
															&& personnel.getIdentificationType().getParameterCode()		
															.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
														this.getManagedBean().getSmPersonnelDirector().add(personnel);
													}
												}
											}
											//27691 END
											if(currPersonnel.getRevNo() == 0 && !currPersonnel.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){
												currPersonnel.setRevNo(1);
											}
										}
									}
									if(!existPerson && personnel.isSsmData() && personnel.getIdentificationType() != null			
											&& personnel.getIdentificationType().getParameterCode()		
											.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
										personnel.setRevNo(1);
										if(personnel.isDirector()){
											this.getManagedBean().getSmPersonnelDirector().add(personnel);
										}
										if(personnel.isEquityOwner()){
											SmSupplierOwner suppOwner = new SmSupplierOwner();
											suppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
											suppOwner.setOwnerName(personnel.getName());
											suppOwner.setNationality(personnel.getPmCountry());
											suppOwner.setIdentificationType(personnel.getIdentificationType());
											suppOwner.setIdentificationTypeId(personnel.getIdentificationTypeId());
											suppOwner.setIdentificationNo(personnel.getIdentificationNo());
											this.getManagedBean().getSmSuppOwners().add(suppOwner);
											resDTO.getSmSupp().getSmSupplierOwners().add(suppOwner);
										}
									}
								}
								//Add personnel using ID Type IC in SM_PERSONNEL only
								if(personnel.getIdentificationType() != null && personnel.getIdentificationType().getParameterCode()		
										.equals(ParameterConst.IDENTIFICATION_TYPES_IC)){
									if(EntityHelper.getEntityBySinglePropertyFromList(personnel.getIdentificationNo(), this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels(), "identificationNo") == null){
										personnel.setSmAppl(this.getManagedBean().getSmAppl());
										//27477
										personnel.setSmPersonnelAddresses(null);
										personnel.setSmPersonnelAdd(null);
										this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels().add(personnel);
									}
								}
							}
							//#27691 END
						}
						//24819
						getManagedBean().getSmSupp().setSsmNewRegNo(ssmResDTO.getSsmNewRegNo());
						//SET SMSUPPLIEROWNER
						//27691 START - TO HANDLE ISSUE OWNER ID NULL
						if(ssmResDTO.getSmSupplierOwners() != null && this.getManagedBean().getSmSupp().getSmSupplierOwners() != null) {
							if(this.getManagedBean().getSmSuppOwners() == null){
								this.getManagedBean().setSmSuppOwners(new ArrayList());
							}
							for(SmSupplierOwner suppOwner : ssmResDTO.getSmSupplierOwners()){
								SmSupplierOwner suppOwnerOld = (SmSupplierOwner)EntityHelper.getEntityBySinglePropertyFromList(suppOwner.getIdentificationNo(), this.getManagedBean().getSmSuppOwners(), "identificationNo");
								for(SmSupplierOwner smSuppOwner : this.getManagedBean().getSmSupp().getSmSupplierOwners()){
									if(suppOwner.getIdentificationNo().equals(smSuppOwner.getIdentificationNo())){
										if(suppOwnerOld == null) {
											suppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
											smSuppOwner.setTempCountryId(suppOwner.getTempCountryId());
											this.getManagedBean().getSmSuppOwners().add(smSuppOwner);
										}else{
											String[] copyOwner = {"tempCountryId","nationalityNull","nationality"};
											EntityHelper.copyEntityPropertiesOnly(suppOwner,suppOwnerOld,copyOwner);
										}
									}
								}
							}
						}
						//27691 END
						//START 27477
						//SET SM SUPPOWNER
						if(this.getManagedBean().getSmSuppOwners() != null){
							for(SmSupplierOwner owner:this.getManagedBean().getSmSuppOwners()){
								if(owner.getSmSupplier() == null){
									owner.setSmSupplier(this.getManagedBean().getSmSupp());
								}
							}
						}
						//27691 START - checkbox
						for(SmPersonnel smPersonnel : this.getManagedBean().getSmSupp().getSmAppl().getSmPersonnels()){
							for(SmSupplierOwner smSuppOwner : this.getManagedBean().getSmSuppOwners()){
								if(smPersonnel.getIdentificationNo().equals(smSuppOwner.getIdentificationNo())){
									smSuppOwner.setOwnerIsEquity(smPersonnel.isEquityOwner());
									smSuppOwner.setOwnerIsDirector(smPersonnel.isDirector());
								}
							}
						}
						//27691 END
						if(ssmResDTO.getSmSupplierBusinessList()!= null){
							boolean existInOld=false;
							//compare old data with ssm, if ssm has new data, add data into list
							if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() > 0){
								for(SmSupplierBusiness smSupplierBusiness:ssmResDTO.getSmSupplierBusinessList()){
									//27828
									existInOld=false;
									if(smSupplierBusiness.getPmBusinessNature()!=null){
										for(SmSupplierBusiness smSuppBussOld:this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
											if(smSupplierBusiness.getPmBusinessNature().getLevelCode().equalsIgnoreCase(smSuppBussOld.getPmBusinessNature().getLevelCode())){
												existInOld=true;
											}
										}
									}
									if(!existInOld){
										smSupplierBusiness.setSmSupplier(this.getManagedBean().getSmSupp());
										this.getManagedBean().getSmSupp().getSmSupplierBusinesses().add(smSupplierBusiness);
									}
								}
							//if old data not exits,set all data from ssm into list
							}else if(ssmResDTO.getSmSupplierBusinessList() != null && ssmResDTO.getSmSupplierBusinessList().size() > 0){
								this.getManagedBean().getSmSupp().setSmSupplierBusinesses(ssmResDTO.getSmSupplierBusinessList());
								for(SmSupplierBusiness business :this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
									business.setSmSupplier(this.getManagedBean().getSmSupp());
								}
							}
						}
						//END 27477
					}else{
						this.getManagedBean().setCompanyValidWithSSM(false);
					}
					
					// maybe here to put method to check with ssm
					SmCommonUtil.ssmIdentificationTypeError(ssmResDTO); //#29582
					if(!this.getManagedBean().isCompanyValidWithSSM()){
						throw new EpException(MessageConst.ERROR_721078);
					}
				}else{
					//set pmcountry to temp					
					for(SmSupplierOwner suppOwner : this.getManagedBean().getSmSuppOwners()){
						if(suppOwner.getNationality()!= null){
							suppOwner.setTempCountryId(suppOwner.getNationality().getCountryId());
						}else{
							suppOwner.setNationalityNull(true);
						}
						
					}
				}
				//END CHANGES SSM
				//START 17906
				if(this.getManagedBean().getSmSupp().getSmAppl() != null && this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks() != null){
					// #28895_35501 Start - AUDIT TRAIL SM
					this.getManagedBean().setOldBanks(
							CustomUtils.getInstance().deepCopyArrayList(this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()));
					// #28895_35501 End
					StringBuilder sb = new StringBuilder();
					for(SmSupplierBank bank:this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()){
						if(bank.getAccountNo() != null){
							sb = new StringBuilder();
							sb.append("<font color='red'>");

							bank.setHasActiveTransactionContractManagement(SmApplicationUtil.hasActiveTransactionForContractManagement(this.getManagedBean().getSmSupp(), 
									bank.getAccountNo(), this.getManagedBean().getSmSupp().getSupplierId())); //#30693
							bank.setHasActiveTransactionFulfilment(SmApplicationUtil.hasActiveTransactionForFulfilment(this.getManagedBean().getSmSupp(), 
									bank.getAccountNo(), this.getManagedBean().getSmSupp().getSupplierId())); //#30693
							if(bank.isHasActiveTransactionContractManagement()){
								sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied",null));
								sb.append("<br/>");
							}
							if(bank.isHasActiveTransactionFulfilment()){
								sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied",null));
								sb.append("<br/>");
							}
							sb.append("</font>");
							bank.setDisplayMessageBank(sb.toString());
						}
					}					
				}
				//END 17906
				
				// start - #30483
				PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd()
						.getPmState().getStateId()));
				if (state != null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
							.getServiceObject("sm", ISupplierSearchSO.class);
					SmAddress smAddress = supplierSearchSO.retrieveSmAddressByAddressId(resDTO.getSmSupp().getSmAppl()
							.getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getAddressId());
					
					this.getManagedBean().setSmBusinessAdd(smAddress);
				}
				// end - #30483
				//START #30964
				if (this.getManagedBean().isCompanyValidWithSSM()){
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
				}//END #30964
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceDet.xhtml");
			}
			
		}else{
			fc.addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					null,
					MessageUtils.getMessage(MessageConst.ERROR_721017, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,acknowledgement);

		}
	}
	
	public void initGstInfo(SmSupplier sup,
			RegistrationManagedBean managedBean, boolean loadBranchGst) {

		managedBean.setSmGstSupplier(SmCommonUtil
				.getSmGstSupplierBySupplierIdAndBranchCode(sup.getSupplierId(),
						null, null, true));
		if (managedBean.getSmGstSupplier() == null) {
			managedBean.setSmGstSupplier(new SmGstSupplier());
			managedBean.getSmGstSupplier().setIsCurrent(true);
		}
		SmApplicationUtil.initGstSuppDocs(managedBean.getSmGstSupplier());
		if (loadBranchGst) {
			SmApplicationUtil.initBranchGstInformation(
					managedBean.getSmGstSupplier(), managedBean.getSmAppl());
		}
	}
	
	//harris
	public void initSstInfo(SmSupplier sup,
			RegistrationManagedBean managedBean, boolean loadBranchSst) {

		managedBean.setSmSstSupplier(SmCommonUtil
				.getSmSstSupplierBySupplierIdAndBranchCode(sup.getSupplierId(),
						null, null, true));
		if (managedBean.getSmSstSupplier() == null) {
			managedBean.setSmSstSupplier(new SmSstSupplier());
			managedBean.getSmSstSupplier().setIsCurrent(true);
		}
		if (loadBranchSst) {
			SmApplicationUtil.initBranchSstInformation(
					managedBean.getSmSstSupplier(), managedBean.getSmAppl());
		}
	}
	
	/**
	 * populate the business type drop down based
	 * @return SelectItem[]
	 */
	public SelectItem[] pmBusinessTypeDropDown(){
		String applType = this.getManagedBean().getApplType();
		SelectItem[] items = null;
		String businessType = getManagedBean().getSmSupp()!= null?getManagedBean().getSmSupp().getBusinessType():"";
		FacesScopes fs = new FacesScopes();
		
		if(applType.equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)){
			items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);
			List<SelectItem> basicBusinessLs = new ArrayList<SelectItem>();
			basicBusinessLs.add(new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						fs.getUserLocale())));
			
			//to filter out professional body
			for(SelectItem item : items){
				//#29114, CR 27382: Static Code Analysis Start 
				//#27601 - start
				//if(item.getValue() != null){
					if(item.getValue() != null && !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY) 
							&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2) ){
						basicBusinessLs.add(item);
					}
				//}
				//#29114, CR 27382: Static Code Analysis End
				//#27601 - end
			}
			
			items =  basicBusinessLs.toArray(new SelectItem[basicBusinessLs.size()]);
		}else{
			String supplierType = this.getManagedBean().getSmAppl().getSupplierType();
			if(supplierType != null){
				if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
					//#27601 - START
					items = getDropdownCodeValuesContractorCooperative(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR);
					//#27601 - END
				}else if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
					items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT);
				}
			}else{
				items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						fs.getUserLocale()));
			}
		}

		//SSM INTEGRATION CR
		//to filter out by company type
		if(this.getManagedBean().isSsmIntegrationActive() && this.getManagedBean().isCompanyValidWithSSM() || this.getManagedBean().isRegAccountMaintainance()){
			List<SelectItem> itemTemp = new ArrayList<SelectItem>();
			itemTemp.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					fs.getUserLocale())));
			for(SelectItem item : items){
				if(this.getManagedBean().isSsmIntegrationActive() && this.getManagedBean().isCompanyValidWithSSM() || this.getManagedBean().isRegAccountMaintainance()){
					if(item.getValue() == null){
						continue;
					}//START #27255
					if(businessTypeIsROB(businessType)){
						if(item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG) || item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)){
							itemTemp.add(item);
						}
					}else if(businessTypeIsROC(businessType)){
						if(item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD) || item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)){
							itemTemp.add(item);
						} 
					} else {
						itemTemp.add(item);
					} //END #27255
				}
			}
			items = null;
			items =  itemTemp.toArray(new SelectItem[itemTemp.size()]);
		}
		
		return items;
	}
	
	/**
	 * retrieve Business Nature L5
	 * and set to a Map
	 */
	public void retrieveBusinessNatureL5Map(){
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessLevel("50");
		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureListByLevel(reqDTO);
		
		Map<Long, Object> bizNatureL5Map = new HashMap<Long,Object>();
		for(PmBusinessNature bizNat : resDTO.getBusinessNatureList()){
			bizNatureL5Map.put(bizNat.getBusinessNatureId(), bizNat);
		}
		
	}

	/**
	 * reset Business Nature Selection - clean the selectedBusinessNature
	 */
	public void resetBusinessNatureSelection(){
		this.getManagedBean().getSelectedBusinessNature().setLvl1(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl2(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl3(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl4(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl5(null);
		
		this.getManagedBean().setBusinessNatureL2(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
	}
	
	/**
	 * check the email existence in PmUser
	 * @param email
	 * @return boolean
	 */
	public boolean isExistEmailInPmUser(String email){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);
		
		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);
		//#29114, CR 27382: Static Code Analysis Start
		/*
		if(resDTO.getUserList().size() > 0){
			return true;
		}else{
			return false;
		}
		*/
		return !resDTO.getUserList().isEmpty() ? false : true;
		//#29114, CR 27382: Static Code Analysis End
	}
	
	/**
	 * check the email existence in SmPersonnel
	 * @param email
	 */
	public void checkExistEmailInPersonnel(String email){
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setEmail(email);
		PersonnelResDTO resDTO = personnelSO.retrievePersonnelByEmail(reqDTO);
		if(resDTO.getSmPersonnelList().size() > 0){
			SmPersonnel personnel = resDTO.getSmPersonnelList().get(0);
			if(personnel.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS){
				long applDateInMilis = personnel.getCreatedDate().getTime();
				long dayInMilis = 1000 * 60 * 60 * 24;
				Date expiryDate = new Date(applDateInMilis + (7 * dayInMilis));

				if(new Date().before(expiryDate)){
					throw new EpException(MessageConst.ERROR_721019);
				}
			}else{
				throw new EpException(MessageConst.ERROR_721004);
			}
		}
	}

	/**
	 * check user existence by identification No
	 * @param idNo
	 * @return boolean
	 */
	public boolean isExistUserByIdentificationNo(String idNo) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
		//#29114, CR 27382: Static Code Analysis Start
		/*
		if(resDTO.getPmUser() != null){
			return true;
		}else{
			return false;
		}
		*/
		return resDTO.getPmUser() != null ? true : false;
		//#29114, CR 27382: Static Code Analysis End
	}
	
	/**
	 * Supplier Bank Validation
	 */
	public void validateBankInfo(List<SmSupplierBank> banks){
		List<String> bankList = new ArrayList<String>();
		for(SmSupplierBank suppBank : banks){
			if(suppBank.getPmFinancialOrg().getFinancialOrgId() == null){
				throw new EpException(MessageConst.ERROR_721008);
			}

			if(StringUtils.isBlank(suppBank.getAccountNo())){
				throw new EpException(MessageConst.ERROR_721009);
			}
				
			if (StringUtils.isNotBlank(suppBank.getAccountNo()) && !StringUtils.isNumeric(suppBank.getAccountNo())) {
				throw new EpException(MessageConst.ERROR_722616);
			}

			String uniqBank = suppBank.getPmFinancialOrg().getFinancialOrgId().toString() + suppBank.getAccountNo();
			if(bankList.contains(uniqBank)){
				//12492 - START
				RequestContext.getCurrentInstance().execute("bankDialogDuplicate.hide()");
				throw new EpException(MessageConst.ERROR_721012);
				//12492 - END
			}
			bankList.add(uniqBank);
		}
	}
	
	/**
	 * Validate entered business registration no whether already exist
	 */
	private void validateBusinessRegNoWithBussTypeAndLocalAuth() {
		if (StringUtils.isNotBlank(getManagedBean().getSmSupp()
				.getBusinessType())
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getRegNo())) {
			if (SmCommonUtil.isRegNoAlreadyExist(getManagedBean().getSmSupp()
					.getRegNo(),
					getManagedBean().getSmSupp().getBusinessType(),
					getManagedBean().getSmSupp().getLocalAuthorityId(),
					getManagedBean().getSmAppl().getSupplierType())) {
				throw new EpException(MessageConst.ERROR_722634);
			}
			//SIR:#26002 - check if already got reg_no with supplier type 'B'
			if (SmCommonUtil.isRegNoAlreadyExist(getManagedBean().getSmSupp()
					.getRegNo(),
					getManagedBean().getSmSupp().getBusinessType(),
					getManagedBean().getSmSupp().getLocalAuthorityId(),
					SupplierManagementConst.STYPE_BASIC)) {
				throw new EpException(MessageConst.ERROR_722634);
			}
		}
	}
	/**
	 * reset Session Date
	 */
	public void resetSessionData() {
		renewSesionObject();
		validateLoginSession();
	}
	
	/**
	 * get portal company Id
	 * @return String
	 */
	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID);
	}
	
	/**
	 * retrieve identity type from PM
	 * @return SelectItem[]
	 */
	public SelectItem[] pmIdTypeDropdownValues() {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		for(SelectItem selectItem : items) {
			
			if(selectItem.getValue() == null){
				itemList.add(selectItem);
			}else{
				PmParameter pmParameter = (PmParameter) identityType.get(selectItem.getValue());
				if(!pmParameter.getParameterCode().equals("A")){
					itemList.add(selectItem);
				}
			}
			
		}
		
		return itemList.toArray(new SelectItem[itemList.size()]);
		
	}
	
	public String getParamDescByParameterId(Long parameterId){
		return SmCommonUtil.getParamDescByParameterId(parameterId);
	}
	//16052019 - SSM INTEGRATION CR
	public String getParamDescriptionByLanguageAndPmParameter(PmParameter parameter) {
		return SmCommonUtil.getParamDescriptionByLanguageAndPmParameter(parameter);
	}
	
	// 27691 - start -  Add/Delete Supplier Owner
	public boolean isShowSupplierOwnersActionPanel() {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser == null || (pmUser != null && pmUser.getUserId() == null)) {
			// User not logged in yet
			return false;
		} else {
			return !this.getManagedBean().isShowOwnerPersonnel() && 
					(!this.getManagedBean().isViewMode()
							&& !this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm()); 
		}		
	}
	//27691 START
	public void addSupplierOwner() {
		String businessType = this.getManagedBean().getSmSupp().getBusinessType();
		if (this.getManagedBean().getSmSuppOwners() == null) {
			this.getManagedBean().setSmSuppOwners(new ArrayList<SmSupplierOwner>());
		}
		
		SmSupplierOwner smSupplierOwner = new SmSupplierOwner();
		PmCountry defaultNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		PmParameter identificationType = ParameterHelper.getInstance().getParameterByTypeAndCode(ParameterTypeConst.IDENTIFICATION_TYPES, ParameterConst.IDENTIFICATION_TYPES_IC);
		smSupplierOwner.setSmSupplier(this.getManagedBean().getSmSupp());
		smSupplierOwner.setNationality(defaultNationality);
		smSupplierOwner.setTempCountryId(defaultNationality.getCountryId());
		smSupplierOwner.setIdentificationType(identificationType);
		smSupplierOwner.setIdentificationTypeId(identificationType.getParameterId());
		smSupplierOwner.setShowTextField(true);
		//to auto tick checkbox if other than LLP&ROC business type
		if(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP.equalsIgnoreCase(businessType)
				|| (!this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm() 
					&& (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD.equalsIgnoreCase(businessType)
				    || SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD.equalsIgnoreCase(businessType)))){
			smSupplierOwner.setOwnerIsEquity(false);
			smSupplierOwner.setOwnerIsDirector(false);
		}else if (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG
				.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK
						.equalsIgnoreCase(businessType)){
			smSupplierOwner.setOwnerIsEquity(true);
		}else {
			smSupplierOwner.setOwnerIsDirector(true);
		}
		this.getManagedBean().getSmSuppOwners().add(smSupplierOwner);
		this.getManagedBean().getAddSuppOwnerLs().add(smSupplierOwner);
		this.getManagedBean().getSmSupp().setSmSupplierOwners(this.getManagedBean().getSmSuppOwners());
	}
	//27691 END
	
	//#27691 - START 
	public void removeSupplierOwner() {
		if (this.getManagedBean().getSelectedSuppOwners() != null && this.getManagedBean().getSelectedSuppOwners().size() > 0) {
			for (SmSupplierOwner smSupplierOwner : this.getManagedBean().getSelectedSuppOwners()) {
				// #27691 - Start - Set record status to 9 for deleted personnel
				if(!this.getManagedBean().getSmAppl().getSmPersonnels().isEmpty()){
					for (SmPersonnel smPersonnel : this.getManagedBean().getSmAppl().getSmPersonnels()){
						if(smSupplierOwner.getIdentificationNo().equalsIgnoreCase(smPersonnel.getIdentificationNo())
								&& smSupplierOwner.getOwnerName().equalsIgnoreCase(smPersonnel.getName())){
							smPersonnel.setRecordStatusFlg(9);
							this.getManagedBean().getDelSuppOwnerLs().add(smSupplierOwner);
						}
					}
				}
				// #27691 - End
				this.getManagedBean().getSmSuppOwners().remove(smSupplierOwner);
			}
		}
		this.getManagedBean().setSelectedSuppOwners(null);
	}
	//remove this comment code- use new changes
	/*public void supplierOwnerOnRowSelected(SelectEvent event) {
		unselectSupplierOwnerAdminRow();
	}
	
	public void supplierOwnerOnRowUnselected(UnselectEvent event) {
	}
	
	public void supplierOwnerOnCheckboxToggled(ToggleSelectEvent event) {
		unselectSupplierOwnerAdminRow();
	}
	
	public void unselectSupplierOwnerAdminRow() {
		int index = 0;
	    for (SmSupplierOwner smSuppOwner : this.getManagedBean().getSmSuppOwners()) {
	    	if (isSmSupplierOwnerIsAdmin(smSuppOwner)) {
	    		RequestContext.getCurrentInstance().execute("unselectSupplierOwner(" + index + ")");
	    	}
	    	index++;
	    }
	}
	
	public String getSupplierOwnerRowStyle(SmSupplierOwner smSuppOwner) {
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser == null || (pmUser != null && pmUser.getUserId() == null)) return "";
		return isSmSupplierOwnerIsAdmin(smSuppOwner) ? "ui-state-disabled" : "";
	}*/
	
	public boolean isSmSupplierOwnerIsAdmin(SmSupplierOwner smSupplierOwner) {
		//32899 - Start
		//Added checking for SmSupplier
		/*if (smSupplierOwner.getSmSupplier() != null){
			for (SmPersonnel smPersonnel : smSupplierOwner.getSmSupplier().getSmAppl().getSmPersonnels()) {
				if (smPersonnel.getIdentificationNo().equals(smSupplierOwner.getIdentificationNo()) && smPersonnel.getEpRole() != null) { //27691
					//boolean isSupplierAdmin = smPersonnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) || smPersonnel.getEpRole().equals(RoleCode.BASIC_SUPPLIER_ADMIN.value());
					return smPersonnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) || smPersonnel.getEpRole().equals(RoleCode.BASIC_SUPPLIER_ADMIN.value());
					}
				}
			} //32899 - End
		return false;*/
		boolean isAdmin = false;
		if (smSupplierOwner.getSmSupplier() != null){
			for (SmPersonnel smPersonnel : smSupplierOwner.getSmSupplier().getSmAppl().getSmPersonnels()) {
				if (smPersonnel.getIdentificationNo().equals(smSupplierOwner.getIdentificationNo()) && smPersonnel.getEpRole() != null
						&& (smPersonnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value()) || smPersonnel.getEpRole().equals(RoleCode.BASIC_SUPPLIER_ADMIN.value()))) {
					isAdmin = true;
				}
			}
		}
		return isAdmin;
	}
	// 27691 - end -  Add/Delete Supplier Owner
	
	/**
	 * set supplier owner mask based on selected id type
	 */
	public void suppOwnerIdTypeChange(){
		PmParameter pmParameter = (PmParameter) identityType.get(this.getManagedBean().getSmSuppOwner().getIdentificationTypeId());
		this.getManagedBean().getSmSuppOwner().setIdentificationType(pmParameter);
	}

	/**
	 * set personnel id mask based on selected id type
	 */
	public void personnelIdTypeChange(){
		PmParameter pmParameter = (PmParameter) identityType.get(this.getManagedBean().getSmPersonnel().getIdentificationTypeId());
		this.getManagedBean().getSmPersonnel().setIdentificationType(pmParameter);
	}
	
	/**
	 * trim owner Passport Value
	 */
	public void trimOwnerPassportValue(){
		String newPassportVal = "";
		if(StringUtils.isNotBlank(this.getManagedBean().getSmSuppOwner().getIdentificationNo())){
			newPassportVal = this.getManagedBean().getSmSuppOwner().getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSmSuppOwner().setIdentificationNo(newPassportVal);
		}
	}
	
	/**
	 * trim Personnel Passport Value
	 */
	public void trimPersonnelPassportValue(){
		String newPassportVal = "";
		if(StringUtils.isNotBlank(this.getManagedBean().getSmPersonnel().getIdentificationNo())){
			newPassportVal = this.getManagedBean().getSmPersonnel().getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSmPersonnel().setIdentificationNo(newPassportVal);
		}
	}
	
	/**
	 * retrieve all identity type into mapper
	 */
	public void retrieveIdentityTypeMap(){
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		identityType = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			identityType.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * update correspondence address based on "same address" value
	 */
	public void updateCorrespondenceAddress(){
		if(this.getManagedBean().isSameAddress()){
			SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
			this.getManagedBean().setSmResAdd(smPermAdd);
			this.getManagedBean().getSmResAdd().getPmState().setStateId(smPermAdd.getPmState().getStateId());
			populateDistrictByAddType("residential", false);
			this.getManagedBean().getSmResAdd().getPmDistrict().setDistrictId(smPermAdd.getPmDistrict().getDistrictId());
			populateCityByAddType("residential", false);
			this.getManagedBean().getSmResAdd().getPmCity().setCityId(smPermAdd.getPmCity().getCityId());
		}else{
			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
		}
	}
	
	/**
	 * Validate ID format
	 * @param identNo
	 * @param identType
	 */
	public void validateIDFormat(String identNo, String identType){
		if(identType.equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
			if(identNo.length() != 12){
				throw new EpException(MessageConst.ERROR_721013);
			}

			if(!identNo.matches("[0-9]+")){
				throw new EpException(MessageConst.ERROR_721013);
			}
			
		}
	}
	
	/**
	 * check Duplicated Professional Body
	 * @return boolean
	 */
	public boolean checkDuplicatedProfessionalBody(){
		List<String> assocBodyList = new ArrayList<String>();
		for(SmAssociationBody assocBody : this.getManagedBean().getSmAppl().getSmAssociationBodies()){

			String uniqAssocBody = assocBody.getPmCertBody().getCertBodyId().toString() + assocBody.getRegNo();
			if(assocBodyList.contains(uniqAssocBody)){
				throw new EpException(MessageConst.ERROR_721034);
			}
			assocBodyList.add(uniqAssocBody);
		}
		return true;
	}
	
	/**
	 * set state drop down value
	 */
	public void pmStateDropdownValues(boolean reset) { //#36718 - add boolean reset to reset address or not
		Long countryId = this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId();
		//SSM INTEGRATION
		if(this.getManagedBean().getSmAppl()!= null && this.getManagedBean().getSmAppl().isApplValidWithSsm() && countryId == null){
			PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			countryId = defaultCountry.getCountryId();
		}
		List<PmState> stateLs = retrievePmStateList(countryId);
		List<SelectItem> stateSelectLs = new ArrayList<SelectItem>();
		PmState state = new PmState();
		
		if(this.getManagedBean().getBusinessStateLs() == null){
			this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
		}
		
		//#36718 - START
		if(this.getManagedBean().getSmSupp().getBusinessType() == null){
			getManagedBean().setSabahSarawakType(false);
			if(getManagedBean().getSmBusinessAdd().getPmDivision()!=null)
				getManagedBean().getSmBusinessAdd().getPmDivision().setDivisionId(null);
			getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
			getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
			getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
			getManagedBean().setBusinessDistrictLs(null);
			getManagedBean().setBusinessCityLs(null);
			getManagedBean().getSmBusinessAdd().setMapLatitude("");
			getManagedBean().getSmBusinessAdd().setMapLongitude("");
			getManagedBean().getSmBusinessAdd().setPostcode("");
			getManagedBean().getSmSupp().setLocalAuthorityId(null);
		}
		//#36718 - END
				
		if(this.getManagedBean().getSmSupp().getBusinessType() != null && !this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase("")){
			if(SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE.contains(this.getManagedBean().getSmSupp().getBusinessType())){
				
				PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
				this.getManagedBean().setSabahSarawakType(true);
				this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
				
				
				if(this.getManagedBean().getSmBusinessAdd().isBrandNew()){
					//this.getManagedBean().getSmBusinessAdd().setPmDivision(new PmDivision());
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
					this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				}

				if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
					
					List<PmState> sarawakLs = SmCommonUtil.getInstance().retrieveStateByZone(SupplierManagementConst.SM_STATE_NAME_SARAWAK,  SupplierManagementConst.COUNTRY_MALAYSIA_CODE);				
					if(!sarawakLs.isEmpty()){
						stateSelectLs = EntityHelper.convertListToSelectItem(sarawakLs,"stateName", "stateId");
						state = sarawakLs.get(0);
						this.getManagedBean().getSmBusinessAdd().setPmState(state); //13320-only sarawak
							
						//#13320 - Rahim Start
						//if(this.getManagedBean().getSmBusinessAdd().getPmDivision() == null ||  this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId() < 1){
							if(state !=null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
								
								//#13320 - Rahim - If Statement Only
								//if(this.getManagedBean().getSmBusinessAdd().getPmDivision() == null ||  this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId() < 1){
									
									this.getManagedBean().getSmBusinessAdd().setPmDivision(new PmDivision());
									this.getManagedBean().getSmBusinessAdd().getPmDivision().setDivisionId(null);
									getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
									getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
									//#36718 - START
									getManagedBean().getSmBusinessAdd().setMapLatitude("");
									getManagedBean().getSmBusinessAdd().setMapLongitude("");
									getManagedBean().getSmBusinessAdd().setPostcode("");
									//#36718 - END
								//} 
																
								List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
								this.getManagedBean().setBusinessDivisionLs(divisionLs);								
								this.getManagedBean().getSmBusinessAdd().getPmState().getStateId();
								getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
								this.getManagedBean().setBusinessCityLs(null);
								
							}
						//}
						//#13320 - Rahim End
							if(state !=null && state.getAdoptDivision()) {
								getManagedBean().setDivisionBox(false);	
							} else{
								this.getManagedBean().setDivisionBox(true);
								this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId())); //13320
							}
					}
				}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
					
					List<PmState> sabahLs = SmCommonUtil.getInstance().retrieveStateByZone(SupplierManagementConst.SM_STATE_NAME_SABAH,  SupplierManagementConst.COUNTRY_MALAYSIA_CODE);	
					if(!sabahLs.isEmpty()){
						stateSelectLs = EntityHelper.convertListToSelectItem(sabahLs,"stateName", "stateId");
						state = sabahLs.get(0);
						this.getManagedBean().getSmBusinessAdd().setPmState(state);
						this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
					}
				}
				
				if(getManagedBean().getSmBusinessAdd().isBrandNew()){
					
					getManagedBean().setBusinessCityLs(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().getSmSupp().setLocalAuthorityId(null);
					//#36718 - START
					getManagedBean().getSmBusinessAdd().setMapLatitude("");
					getManagedBean().getSmBusinessAdd().setMapLongitude("");
					getManagedBean().getSmBusinessAdd().setPostcode("");
					//#36718 - END
				}
			}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) ||
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)){

				List<PmState> semenanjungLs = new ArrayList<PmState>();
				
				for(PmState smjState : stateLs){
					if(!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SABAH_CODE) && 
							!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
						semenanjungLs.add(smjState);
					}
				}
				
				stateSelectLs  = EntityHelper.convertListToSelectItem(semenanjungLs,"stateName", "stateId");
				//#16052019 - SSM INTEGRATION CR 
				if(getManagedBean().getSmBusinessAdd().isBrandNew() && !getManagedBean().isValidateWithSSM()){
					getManagedBean().setSabahSarawakType(false);
					//#36718 - START
					if(getManagedBean().getSmBusinessAdd().getPmDivision()!=null)
						getManagedBean().getSmBusinessAdd().getPmDivision().setDivisionId(null);
					getManagedBean().getSmBusinessAdd().getPmState().setAdoptDivision(false);
					//#36718 - END
					getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().setBusinessDistrictLs(null);
					getManagedBean().setBusinessCityLs(null);
					//#36718 - START
					getManagedBean().getSmBusinessAdd().setMapLatitude("");
					getManagedBean().getSmBusinessAdd().setMapLongitude("");
					//#36718 - END
				}
			}else{
				
				this.getManagedBean().setSabahSarawakType(false);
				//#36718 - START - if ROC maintained as is
				if(!this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD) &&
				   !this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD) && reset){
					if(getManagedBean().getSmBusinessAdd().getPmDivision()!=null)
						getManagedBean().getSmBusinessAdd().getPmDivision().setDivisionId(null);
					getManagedBean().getSmBusinessAdd().getPmState().setAdoptDivision(false);
					getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().setBusinessDistrictLs(null);
					getManagedBean().setBusinessCityLs(null);
					getManagedBean().getSmBusinessAdd().setMapLatitude("");
					getManagedBean().getSmBusinessAdd().setMapLongitude("");
					getManagedBean().getSmBusinessAdd().setPostcode("");
				}
				//#36718 - START
			}
			showOwnerCB();		
		}

		//16052019 - SSM INTEGRATION CR - Fahmi
		setHeaderName();
		if(stateSelectLs.size() < 1){
			stateSelectLs = EntityHelper.convertListToSelectItem(stateLs,"stateName", "stateId");
		}
		this.getManagedBean().setBusinessStateLs(stateSelectLs);
		populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		
		// #36718 - Start - reset regNo when business type changed
		if (this.getManagedBean().getSmAppl().isBrandNew()
				&& !this.getManagedBean().getSmAppl().isApplValidWithSsm()) {
			this.getManagedBean().getSmSupp().setRegNo(null);
		}
		// #36718 - End
	}
			
	/**
	 * Professional Body OnChange -Listener
	 * @param certBody
	 * @return boolean
	 */
	public boolean profBodyOnChange(PmCertBody certBody){
		boolean stat = false;
		//CR 27382: Static Code Analysis Start
		/*Asal
		if(certBody!= null){
			if(certBody.getCertBodyId() != null){
				Long profId = certBody.getCertBodyId();
				PmCertBody selectedCertBody = profCertBodyMap.get(profId);
				if(selectedCertBody != null){
					if(selectedCertBody.getBodyCode().equalsIgnoreCase(SupplierManagementConst.PM_CERT_BODY_OTHERS)){
						//log.info("--------IS LAIN LAIN PROF--------" +selectedCertBody.getBodyCode());
						stat = true;
					}
				}
			}
		}
		*/
		if (certBody != null && certBody.getCertBodyId() != null) {
			Long profId = certBody.getCertBodyId();
			PmCertBody selectedCertBody = profCertBodyMap.get(profId);
			if (selectedCertBody != null && selectedCertBody.getBodyCode().equalsIgnoreCase(SupplierManagementConst.PM_CERT_BODY_OTHERS)) {
				stat = true;
			}
		}
		//CR 27382: Static Code Analysis End
		return stat;
	}
	
	/**
	 * display section levelcode in table
	 * @param sectionId
	 * @return String
	 */
	public String getSectionBusinessNatureLevelCode(Long sectionId) {
		String retVal = "";
		//CR 27382: Static Code Analysis Start
		/*Asal
		if(sectionId!=null && sectionId.compareTo(new Long(0))==0){
			if (CollectionUtils.isNotEmpty(getManagedBean().getBusinessNatureL1())) {
				for (PmBusinessNature section : getManagedBean().getBusinessNatureL1()) {
					if(sectionId.compareTo(section.getBusinessNatureId())==0){
						retVal = section.getLevelCode()+" - ";
						break;
					}
				}
			}
		}
		*/
		if(sectionId!=null && sectionId.compareTo((long) 0)==0 && CollectionUtils.isNotEmpty(getManagedBean().getBusinessNatureL1()) ){
			for (PmBusinessNature section : getManagedBean().getBusinessNatureL1()) {
				if (sectionId.compareTo(section.getBusinessNatureId()) == 0) {
					retVal = section.getLevelCode() + " - ";
					break;
				}
			}

		}
		//CR 27382: Static Code Analysis End

		return retVal;
	}
	
	/**
	 * update Basic Supplier
	 * @return String
	 */
	public String updateBasicSupplier(){
		// 27691 - start -  Add/Delete Supplier Owner - log only for dev server debugging
		/*log.info("---After declare button clicked Supplier Owner size: " + this.getManagedBean().getSmSupp().getSmSupplierOwners().size() + "---");
		for (int i = 0; i < this.getManagedBean().getSmSupp().getSmSupplierOwners().size(); i++) {
			log.info("---" + this.getManagedBean().getSmSupp().getSmSupplierOwners().get(i).getOwnerName()  + ": " + this.getManagedBean().getSmSupp().getSmSupplierOwners().get(i).getIdentificationNo() + "---");
		}*/
		// 27691 - end -  Add/Delete Supplier Owner - log only for dev server debugging
		//CR 27382: Static Code Analysis Start
		/*Asal
		if(!this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
		}
		*/
		if (!this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG) && this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1) {
			throw new EpException(MessageConst.ERROR_721011);
		}
		//CR 27382: Static Code Analysis End

		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}

		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());
		//27691 START
		if(this.getManagedBean().getSmSupp().getSmSupplierOwners() != null){
			for(SmSupplierOwner suppOwner : this.getManagedBean().getSmSupp().getSmSupplierOwners()){
				validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
				if(!this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm() &&
						( !suppOwner.isOwnerIsEquity() && !suppOwner.isOwnerIsDirector() )
						&& this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP)){
					throw new EpException(MessageConst.ERROR_722535);
				}
			}
		}
		if(!this.getManagedBean().getSmSupp().getSmAppl().isApplValidWithSsm()){
			if(this.getManagedBean().getSmSupp().getSmSupplierOwners().size() < 1){
				throw new EpException(MessageConst.ERROR_775195);
			}else if(this.getManagedBean().getSmSupp().getSmSupplierOwners() != null){
				Boolean exist = false;
				for(SmSupplierOwner suppOwner : this.getManagedBean().getSmSupp().getSmSupplierOwners()){
					if(suppOwner.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
						exist = true;
						break;
					}
				}
				if(!exist){
					throw new EpException(MessageConst.ERROR_775195);
				}
			}
		}
		//27691 END

		if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
			if(this.getManagedBean().getSmAppl().getSmAssociationBodies().size() < 1){
				throw new EpException(MessageConst.ERROR_721033);
			}else{
				checkDuplicatedProfessionalBody();
				for(SmAssociationBody assocBody : this.getManagedBean().getSmAppl().getSmAssociationBodies()){
					if(assocBody.getSmAppl() == null){
						assocBody.setSmAppl(this.getManagedBean().getSmAppl());
					}
				}		
			}
		}
		
		return updateOnlineAccount();
		
	}
	
	/**
	 * get Business Nature Section
	 * @param suppBusinessNat
	 * @return String
	 */
	public String getBusinessNatureSection(SmSupplierBusiness suppBusinessNat) {
		String sectionLvl1Code = "";
		PmBusinessNature pmBizNat = suppBusinessNat.getPmBusinessNature();
		PmBusinessNature pmBizObjL4 = null;
		if (pmBizNat != null
				&& pmBizNat.getParentBusinessNature() != null
				&& pmBizNat.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL3 = null;
		if (pmBizObjL4 != null
				&& pmBizObjL4.getParentBusinessNature() != null
				&& pmBizObjL4.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL2 = null;
		if (pmBizObjL3 != null
				&& pmBizObjL3.getParentBusinessNature() != null
				&& pmBizObjL3.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL1 = null;
		if (pmBizObjL2 != null
				&& pmBizObjL2.getParentBusinessNature() != null
				&& pmBizObjL2.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2
					.getParentBusinessNature().getBusinessNatureId());
		}
		sectionLvl1Code = pmBizObjL1 == null ? "" : pmBizObjL1.getLevelCode();
		return sectionLvl1Code;
	}

	/**
	 * remove RegNo WhiteSpace
	 */
	public void removeRegNoWhiteSpace() {
		if (getManagedBean().getSmSupp() != null
				&& getManagedBean().getSmSupp().getBusinessType() != null
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getBusinessType())
				&& getManagedBean().getSmSupp().getRegNo() != null
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getRegNo())) {
			String bussType = getManagedBean().getSmSupp().getBusinessType();
			//#35581
			if (!validateString(getManagedBean().getSmSupp().getRegNo())) {
				if (sabahSarawakRemoveWhiteSpaceList.contains(getManagedBean().getSmSupp().getBusinessType())) {
					throw new EpException(MessageConst.ERROR_723100,
							new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				} else if (regNoValidityList.contains(getManagedBean().getSmSupp().getBusinessType())) {
					throw new EpException(MessageConst.ERROR_723101,
							new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			//#29114, CR 27382: Static Code Analysis Start
			/*
			if (sabahSarawakRemoveWhiteSpaceList.contains(bussType)) {
				getManagedBean().getSmSupp().setRegNo(StringUtils.deleteWhitespace(getManagedBean().getSmSupp().getRegNo()));
			} else if (rosRemoveWhiteSpaceList.contains(bussType)) {
				getManagedBean().getSmSupp().setRegNo(StringUtils.deleteWhitespace(getManagedBean().getSmSupp().getRegNo()));
			}
			*/
			if (sabahSarawakRemoveWhiteSpaceList.contains(bussType) || rosRemoveWhiteSpaceList.contains(bussType) ) {
				getManagedBean().getSmSupp().setRegNo(StringUtils.deleteWhitespace(getManagedBean().getSmSupp().getRegNo()));
			} 
			//#29114, CR 27382: Static Code Analysis End
		}else{//35581
			if (sabahSarawakRemoveWhiteSpaceList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723100,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (regNoValidityList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723101,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			
		}
	}
	
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);

		super.commonDownload(filePath, filePath);
	}
	
	/**
	 * View Business Nature
	 */
	public void viewBusinessNature(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if(pmUser != null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			SmSupplier smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0); 

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSupplierId(smSupp.getSupplierId());

			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.retrieveBusinessNatureBySupplierId(reqDTO);
			
			this.getManagedBean().setSmSupp(resDTO.getSmSupp());
			
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplierbusinessnature/businessNatureMaint.xhtml");
	}
	
	/**
	 * update Business Nature Maintenance
	 * @return
	 */
	public String updateBusinessNatureMaintenance(){
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);
		}else{
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSmSupp(this.getManagedBean().getSmSupp());

			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.saveSupplierBusinessNature(reqDTO);

			if(resDTO.isSuccessful()){
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_100002,
						MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));	
				return "/xhtml/common/acknowledge.xhtml";
			}
		}
		
		return null;
	}

	/**
	 * post Update BizNature
	 * @param bisNatures
	 */
	public void postUpdateBizNature(List<BaseEntity> bisNatures) {

		IBusinessNatureSO bizNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		Map<Long, PmBusinessNature> bizNatureUniqueMap = new HashMap<Long, PmBusinessNature>();
		
		for(SmSupplierBusiness ssb : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()) {
			bizNatureUniqueMap.put(ssb.getPmBusinessNature().getBusinessNatureId(), ssb.getPmBusinessNature());
		}
		
		String duplicateCode = null;
//		StringBuilder duplicateCode = new StringBuilder();
		for(int i = 0; i < bisNatures.size(); i++) {
			PmBusinessNature bn = (PmBusinessNature)bisNatures.get(i);
			
			if(!bizNatureUniqueMap.containsKey(bn.getBusinessNatureId())) {
				
				BusinessNatureReqDTO bnReqDTO = new BusinessNatureReqDTO();
				bnReqDTO.setBusinessNatureId(bn.getBusinessNatureId());
				BusinessNatureResDTO resDTO = bizNatureSO.fetchParentBusinessNature(bnReqDTO);
				bn = resDTO.getBusinessNature();
				bizNatureUniqueMap.put(bn.getBusinessNatureId(), bn);

				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bn);
				suppBusiness.setSmSupplier(this.getManagedBean().getSmSupp());
				this.getManagedBean().getSmSupp().getSmSupplierBusinesses().add(suppBusiness);
			} else {
				if(duplicateCode == null ) {
					duplicateCode = bn.getLevelCode() + " - " + bn.getLevelName() + "<br />";
					//duplicateCode.append(bn.getLevelCode()).append(" - ").append(bn.getLevelName()).append("<br />");
					
				} else {
					duplicateCode = duplicateCode + "  " + bn.getLevelCode() + " - " + bn.getLevelName() + "<br />";  
					//duplicateCode.append(duplicateCode).append(" ").append(bn.getLevelCode()).append(" - ").append(bn.getLevelName()).append("<br />");
				}
			}
		
		}
		if(duplicateCode != null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721223,
									new String[] { duplicateCode }, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
		}
	}
	
	/**
	 * Set admin related information same as owner after ticked check box
	 */
	private void setAdminSameAsOwner() {
		// #30829 - start
		if (!getManagedBean().isSameAsOwner()) {
			for (SmSupplierOwner supplierOwner : getManagedBean().getSmSuppOwners()) {
				if (supplierOwner.getIdentificationNo().equals(getManagedBean().getSmPersonnel().getIdentificationNo())) {
					getManagedBean().setSameAsOwner(true);
				}
			}
			for (SmPersonnel director : this.getManagedBean().getSmPersonnelDirector()) {
				if ((getManagedBean().getSmPersonnel().getIdentificationNo().equals(director.getIdentificationNo()))
						&& director.isDirector()) {
					getManagedBean().getSmPersonnel().setDirector(true);
				}
			}
		}
		// #30829 - end		
		//#16052019 - SSM INTEGRATION CR - if ssm no need to do this
		if (getManagedBean().isSameAsOwner() && !getManagedBean().isCompanyValidWithSSM()) {
			if(getManagedBean().getSmSuppOwners().size()>0){
				getManagedBean().setSmSuppOwner(getManagedBean().getSmSuppOwners().get(0));
			}
			getManagedBean().getSmPersonnel().setName(
					getManagedBean().getSmSuppOwner().getOwnerName());
			getManagedBean().getSmPersonnel().setPmCountry(
					getManagedBean().getSmBusinessAdd().getPmCountry());
			getManagedBean().getSmPersonnel()
					.setIdentificationTypeId(
							getManagedBean().getSmSuppOwner()
									.getIdentificationTypeId());
			getManagedBean().getSmPersonnel().setIdentificationNo(
					getManagedBean().getSmSuppOwner().getIdentificationNo());
			getManagedBean().getSmPersonnel().setPmCountry(getManagedBean().getSmSuppOwner().getNationality());
		}
	}

	/**
	 * Set whether to disable company name and business type field from editing
	 * in maintenance
	 */
	private void setDisableCompanyNameBusinessTypeInMaintenance() {
		getManagedBean().setDisabledCompanyNameInMaintenance(false);
		//32870 START - remove checking for active transaction with other modules
//		if (SmApplicationUtil.hasActiveTransactions(this.getManagedBean()
//				.getSmSupp()) || SmApplicationUtil.hasOtherActiveTransactions(this.getManagedBean()
//				.getSmSupp())) {
//			getManagedBean().setDisabledCompanyNameInMaintenance(true);
//			//disabled for #10998
//		}
		//32870 END
		if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
		}
		if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_ALLOWED_TO_BE_EDITED
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		
		SmAppl isActieveAppl = SmCommonUtil.getActiveApplBySupplier(getManagedBean().getSmSupp());
		if(isActieveAppl != null){
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		
		if (SmApplicationUtil
				.isApplHasSoftcertProcessingNotComplete(getManagedBean()
						.getSmSupp().getSmAppl())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
	}
	
	/**
	 * Validate gst no and format
	 */
	public void validateGstRegNo() {
		String gstNo = this.getManagedBean().getSmGstSupplier().getGstRegNo();

		// Validate GST reg no. uniqueness
		SmCommonUtil.validateGstRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), gstNo);

		if (!StringUtils.isBlank(gstNo)) {
			if (!SmCommonUtil.validateGstRegNo(gstNo)) {
				this.getManagedBean().setGstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setGstFlag(true);
			}
		} else {
			if (StringUtils.isBlank(gstNo)) {
				// if both gst no and gst eff date null means deleted gst
				if (this.getManagedBean().getSmGstSupplier().getGstEffDate() == null) {
					deleteGst(this.getManagedBean().getSmGstSupplier());
					this.getManagedBean().setGstFlag(false);
				} else {
					this.getManagedBean().setGstFlag(false);
					throw new EpException(MessageConst.ERROR_721027);
				}
			}
		}
	}
	
	//harris
	public void validateSstRegNo() {
		String sstNo = this.getManagedBean().getSmSstSupplier().getSalestaxregno();

		// Validate SST reg no. uniqueness
		SmCommonUtil.validateSstRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), sstNo);

		if (!StringUtils.isBlank(sstNo)) {
			if (!SmCommonUtil.validateSstRegNo(sstNo)) {
				this.getManagedBean().setSstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setSstFlag(true);
			}
		} else {
			if (StringUtils.isBlank(sstNo)) {
				// if both gst no and gst eff date null means deleted gst
				if (this.getManagedBean().getSmSstSupplier().getSalestaxregDate() == null) {
					deleteSst(this.getManagedBean().getSmSstSupplier());
					this.getManagedBean().setSstFlag(false);
				} else {
					this.getManagedBean().setSstFlag(false);
					throw new EpException(MessageConst.ERROR_721027);
				}
			}
		}
	}
	
	//harris
	public void validateSstServRegNo() {
		String sstServNo = this.getManagedBean().getSmSstSupplier().getServtaxregno();

		// Validate SST reg no. uniqueness
		SmCommonUtil.validateSstRegNoUniqueness(getManagedBean().getSmSupp()
				.getRegNo(), sstServNo);

		if (!StringUtils.isBlank(sstServNo)) {
			if (!SmCommonUtil.validateSstRegNo(sstServNo)) {
				this.getManagedBean().setSstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			} else {
				this.getManagedBean().setSstFlag(true);
			}
		} else {
			if (StringUtils.isBlank(sstServNo)) {
				// if both gst no and gst eff date null means deleted gst
				if (this.getManagedBean().getSmSstSupplier().getServtaxregDate() == null) {
					deleteSst(this.getManagedBean().getSmSstSupplier());
					this.getManagedBean().setSstFlag(false);
				} else {
					this.getManagedBean().setSstFlag(false);
					throw new EpException(MessageConst.ERROR_721027);
				}
			}
		}
	}
	
	
	/**
	 * FIle upload Handler
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		handleFileUpload(getManagedBean().getSmGstSupplier()
				.getSmGstSupplierDocs(), getManagedBean()
				.getSelectedGstSupplierDoc(), event);
	}

	public void handleFileUpload(List<SmGstSupplierDoc> mainDocList,
			SmGstSupplierDoc doc, FileUploadEvent event) {
		if (event != null && doc!=null) {
			String tempPath = String.valueOf((new Date()).getTime());
			String filePath = SmCommonUtil
					.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
					+ tempPath + "/";

			String fileName = String.valueOf((new Date()).getTime()) + "_"
					+ event.getFile().getFileName();
			doc.setFileName(StringUtils.replace(fileName, " ", "-"));
			doc.setFilePath(filePath);

			setGstDocToMainList(mainDocList, doc);
			
			SmCommonUtil.processUpload(filePath, event, fileName);
		}
	}
	
	/**
	 * Back to application history search page
	 */
	public String backToHistory() {
		return "/xhtml/sm/revisionSmApplView/viewApplicationHistory/searchSupplierApplicationHistoryPage.xhtml?faces-redirect=true";
	}
	
	private void setGstDocToMainList(List<SmGstSupplierDoc> mainDocList,
			SmGstSupplierDoc selectedDoc) {
		Iterator<SmGstSupplierDoc> docVOIter = mainDocList.iterator();
		while (docVOIter.hasNext()) {
			SmGstSupplierDoc docVO = docVOIter.next();
			if (docVO.getPmDocument().getDocumentId().compareTo(selectedDoc.getPmDocument().getDocumentId()) == 0) {

				docVO = selectedDoc;

			}
		}
	}
	//CR 27382: Static Code Analysis - Remove this unused private "convertToStateSelectItem" method
	/*
	private SelectItem convertToStateSelectItem(PmState state){
		SelectItem si = new SelectItem();
		si.setLabel(state.getStateName());
		si.setValue(state.getStateId());
		
		return si;
	}
	*/
	
	/**
	 * Redirect to liferay logout url
	 */
	public void proceedToLogout() {
		RequestContext.getCurrentInstance().execute("successDialog.hide()");
	}
	
	/**
	 * proceed to Term & Condition Page
	 * @return String
	 */
	public String proceedToTermAndCondition(){
		if(getManagedBean().getApplType() != null){
			if(getManagedBean().getApplType().equals(SupplierManagementConst.ACC_GOVT_SELLER)){
				return g2gRegistration;
			}else{
				//16052019 - SSM INTEGRATION CR - Fahmi
				getManagedBean().setSsmIntegrationActive(SmCommonUtil.getInstance().ssmActive());
				if(getManagedBean().isSsmIntegrationActive())
					getManagedBean().setValidateWithSSM(true);
				return termAndCondition;
			}
		}else{
			return signUp;
		}
	}
	
	/**
	 * return to Term & Condition Page
	 * @return String
	 */
	public String returnToTermAndCondition(){
		getManagedBean().setCaptchaAnswer(null);
		getManagedBean().setAgreeTC(false);
		getManagedBean().setValidateWithSSM(true);
		getManagedBean().setCompanyValidWithSSM(false);
		// #37900 -start
		getManagedBean().getSmSupp().setRegNo(null);
		getManagedBean().getSmSupp().setBusinessType(null);
		getManagedBean().getSmAppl().setSupplierType(null);
		getManagedBean().getSmAppl().setApplValidWithSsm(false);
		getManagedBean().setRegIcCheckSSM(null);
		getManagedBean().getSmAppl().setSmCompanyBasic(new SmCompanyBasic());
		getManagedBean().getSmAppl().getSmSupplierBanks().clear();
		getManagedBean().getSmSupp().getSmSupplierBusinesses().clear();
		getManagedBean().getSmPersonnelOwners().clear();
		getManagedBean().getSmPersonnelDirector().clear();
		getManagedBean().getSmBusinessAdd().setAddress1("");
		getManagedBean().getSmBusinessAdd().setAddress2("");
		getManagedBean().getSmBusinessAdd().setAddress3("");
		getManagedBean().getSmSupp().setSsmNewRegNo(null);
		// #37900 -end
		return termAndCondition;

	}

	/**
	 * Retrieve Gst Supporting Doc From PmDocument
	 */
	public List<PmDocument> retrieveGstDocFromPmDocument(){
		IDocumentSO reqSO = SmServiceLookup.getDocumentSO();
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		DocumentResDTO resDTO = reqSO.retrieveGstSuppDocuments(reqDTO);
		
		return resDTO.getDocumentList();
	}
	
	/**
	 * Set GstSupplier Object to supplier
	 */
	private void setSupplierGst(){
		SmGstSupplier gstSupplier = getManagedBean().getSmGstSupplier();
		gstSupplier.setIsCurrent(true);
		//CR 27382: Static Code Analysis
		//if(gstSupplier.getSmGstSupplierDocs().size() > 0){
		if(!gstSupplier.getSmGstSupplierDocs().isEmpty()){
			List<SmGstSupplierDoc> tempLs = new ArrayList<SmGstSupplierDoc>();
			tempLs.addAll(gstSupplier.getSmGstSupplierDocs());
			
			for(SmGstSupplierDoc supplierDoc : gstSupplier.getSmGstSupplierDocs()){
				if(supplierDoc.getFileName() == null || supplierDoc.getFilePath() == null){
					tempLs.remove(supplierDoc);
				}
			}
			gstSupplier.setSmGstSupplierDocs(tempLs);
		}
		
		SmSupplier supplier = getManagedBean().getSmSupp();
		gstSupplier.setSmSupplier(getManagedBean().getSmSupp());
		supplier.getSmGstSuppliers().add(gstSupplier);
	}
	
	//harris sst
	private void setSupplierSst(){
		SmSstSupplier sstSupplier = getManagedBean().getSmSstSupplier();
		sstSupplier.setIsCurrent(true);
		
		SmSupplier supplier = getManagedBean().getSmSupp();
		sstSupplier.setSmSupplier(getManagedBean().getSmSupp());
		supplier.getSmSstSuppliers().add(sstSupplier);
	}
	
	/**
	 * Create Gst Document List
	 */
	private void prepareGstDoc(){
		SmGstSupplier gstSupplier = getManagedBean().getSmGstSupplier();
		List<PmDocument> docLs = retrieveGstDocFromPmDocument();
		gstSupplier.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		//CR 27382: Static Code Analysis
		//if(docLs.size() > 0){
		if(!docLs.isEmpty()){
			for(PmDocument document : docLs){
				SmGstSupplierDoc gstSupplierDoc = new SmGstSupplierDoc();
				gstSupplierDoc.setSmGstSupplier(gstSupplier);
				gstSupplierDoc.setPmDocument(document);
				gstSupplier.getSmGstSupplierDocs().add(gstSupplierDoc);
			}
		}
	}
	
	/**
	 * set Selected Gst Doc
	 * @param gstSupplierDoc
	 */
	public void setSelectedGstDoc(SmGstSupplierDoc gstSupplierDoc){
		this.getManagedBean().setSelectedGstSupplierDoc(gstSupplierDoc);
	}

	/**
	 * Validate user login
	 */
	private void validateLoginSession(){
		String screenName = new FacesScopes().getUserScreenName();
		FacesContext fc = FacesContext.getCurrentInstance();
		if (screenName != null && StringUtils.isNotEmpty(screenName)) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					MessageUtils.getMessage(MessageConst.ERROR_721045,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			

		}else{
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, signUp);
		}
	}

	public boolean isGmapDisabled() {
		boolean gmapDisabled = false;
		
		//if DA are Cancel Registration  or Black List
		if(this.getManagedBean().isActiveDisicplinaryAction()){
			gmapDisabled = true;
		}else if(getManagedBean().getSmBusinessAdd() != null){
			SmAddress bizAddress = getManagedBean().getSmBusinessAdd();

			if(bizAddress.getPmCountry() == null || bizAddress.getPmDistrict() == null || 
					bizAddress.getPmState() == null || bizAddress.getPmCity() == null || 
					bizAddress.getAddress1() == null ){
					gmapDisabled = true;
			}else{
				if(StringUtils.isBlank(bizAddress.getAddress1()) || bizAddress.getPmCountry().getCountryId() == null ||
						bizAddress.getPmDistrict().getDistrictId() == null || bizAddress.getPmState().getStateId() == null ||
						bizAddress.getPmCity().getCityId() == null){
					gmapDisabled = true;
				}

			}
		}else{
			gmapDisabled = true;
		}

		return gmapDisabled;
	}
	
	//captcha test method
	public void check(ActionEvent e){
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Your Code Is Correct !",null));
	}

	public boolean isShowNewChanges() {
		String newChanges = EpPropertiesUtils.getInstance().getProperty("sm.show.new.changes");
		//CR 27382: Static Code Analysis
		//boolean showNewChanges = Boolean.valueOf(newChanges);
		//return showNewChanges;
		return Boolean.parseBoolean(newChanges);
	}

	/**
	 * retrieve Basic Account Status
	 * @param
	 */
	public void retrieveBasicAccountStatus(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		if(pmUser.getUserId().longValue() != 1 || pmUser == null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();

			getManagedBean().setSmSupp(smSupp);
			initHasActiveTransactionStatus();
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, basicAccDeactivation);
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					null,
					MessageUtils.getMessage(MessageConst.ERROR_721017, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,acknowledgement);
		}
	}
	
	/**
	 * Update basic account status
	 */
	public String updateBasicAccountStatus(){
		if(this.getManagedBean().getSmSupp().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED){
			if(SmApplicationUtil.hasActiveTransactions(this.getManagedBean().getSmSupp())){
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721035);
			}else{
				RegistrationReqDTO reqDTO = new RegistrationReqDTO();
				reqDTO.setSmSupp(getManagedBean().getSmSupp());
				//#31674 - Start
				reqDTO.setFromDeactivateBasicSupp(true);
				//#31674 - End
				IRegistrationSO reqSO = SmServiceLookup.getRegistrationSO();
				try {
					reqSO.updateOnlineAcc(reqDTO);			
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					SmCommonUtil.callDeactivationSuccessDialog(MessageConst.INFO_721034);
					return null;
				} catch(Exception ex){
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					throw new EpException(ex.getMessage());
				}finally{
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				}
			}
		}else{
			return acknowledgement;
		}
	}
	
	/**
	 * Save Basic Account Status
	 */
	public String saveBasicAccountStatus(){
		//boolean deactivateAccount = this.getManagedBean().getSmSupp().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED;
		if(this.getManagedBean().getSmSupp().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
			return null;
		}else{
			//#15016 - Start
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			//FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
			//		FacesMessage.SEVERITY_INFO,
			//		null,
			//		MessageUtils.getMessage(MessageConst.INFO_721016, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			//#15016 - End
			
			return acknowledgement;
		}
		
	}
	
	/**
	 * Delete GST
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public void deleteGst(SmGstSupplier gst) {
		gst.setGstRegNo(null);
		gst.setGstEffDate(null);
		gst.setGstEndDate(null);
		gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		SmApplicationUtil.initGstSuppDocs(gst);
	}
	
	//harris
	public void deleteSst(SmSstSupplier sst) {
		sst.setSalestaxregno(null);
		sst.setSalestaxregDate(null);
		sst.setSalestaxregEndDate(null);
		sst.setServtaxregno(null);
		sst.setServtaxregDate(null);
		sst.setServtaxregEndDate(null);
	}
	
	public void removeGstUploadedFile(){
		SmGstSupplierDoc gstDoc = getManagedBean().getSelectedGstSupplierDoc();
		gstDoc.setFileName(null);
		gstDoc.setFilePath(null);

	}
	
	public Date getMinGstEndDate() {
		if (getManagedBean().getSmGstSupplier() != null
				&& getManagedBean().getSmGstSupplier().getGstEffDate() != null) {
			return getManagedBean().getSmGstSupplier().getGstEffDate();
		} else {
			CommonConstants commonConst = new CommonConstants();
			return commonConst.getGST_LAUNCH_DATE();
		}
	}
	
	public String getOwnerLabel(){
		String bizType = getManagedBean().getSmSupp().getBusinessType();
		if(bizType != null){
			if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
				return "lbl.sm.director.name";
			}else if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)){
				return "lbl.sm.chairman.name";
			}else{
				return "lbl.sm.ownername";
			}
		}else{
			return "lbl.sm.ownername";
		}
	}
	
	public void ownerNationalityOnChange(){
		//16052019 - SSM INTEGRATION CR => START
		List<SmSupplierOwner> listSuppOwner = getManagedBean().getSmSuppOwners();
		//CR 27382: Static Code Analysis
		//if(listSuppOwner.size() > 0){
		if(!listSuppOwner.isEmpty()){
			for(SmSupplierOwner owner : listSuppOwner){
				setOwnerNationality(owner,false);
			}
		} else {
		//16052019 - SSM INTEGRATION CR => END
		
			getUnknownCountry();
			SmSupplierOwner owner = getManagedBean().getSmSuppOwner();
			Long countryId = owner.getNationality().getCountryId();
			ParameterReqDTO reqDTO = new ParameterReqDTO();
		
			if(countryId != null){
				PmCountry country = SmCommonUtil.retrievePmCountryById(countryId);
				owner.setNationality(country);
				if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) || 
						country.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE)){
					reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
				}else{
					reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_PASSPORT);
				}
				reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			}else{
				PmCountry defaultNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				owner.setNationality(defaultNationality);
				reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
				reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			}
			
			PmParameter identObj = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqDTO).getPmParameter();
			owner.setIdentificationTypeId(identObj.getParameterId());
			owner.setIdentificationType(identObj);
			//#26987
			if(getManagedBean().isSameAsOwner() && getManagedBean().getSmPersonnel()!= null){
				SmPersonnel personnel = getManagedBean().getSmPersonnel();
				personnel.setPmCountry(owner.getNationality());
				personnel.setName(owner.getOwnerName());
				personnel.setIdentificationNo(owner.getIdentificationNo());
			}
		}
	}

	public void personnelNationalityOnChange(){
		SmPersonnel personnel = getManagedBean().getSmPersonnel();
		Long countryId = personnel.getPmCountry().getCountryId();
		ParameterReqDTO reqDTO = new ParameterReqDTO();
	
		if(countryId != null){
			PmCountry country = SmCommonUtil.retrievePmCountryById(countryId);
			if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
				reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			}else{
				if(country.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE)){
					reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
				}else{
					reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_PASSPORT);
				}
			}
			reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			personnel.setPmCountry(country);
			
			PmParameter identObj = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqDTO).getPmParameter();
			personnel.setIdentificationTypeId(identObj.getParameterId());
			personnel.setIdentificationType(identObj);
		}

	}

	private void checkMaxBankAccount(){
		int maxPaymentBank = SmServiceLookup.getSmRuleSO().retrieveMaxPaymentBank().getMaxPaymentBank();
		
		if(getManagedBean().getSmAppl().getSmSupplierBanks().size() >= maxPaymentBank){
			throw new EpException(MessageConst.ERROR_721049,new String[]{String.valueOf(maxPaymentBank)});
		}
	}
	
	
	private void initHasActiveTransactionStatus() {
		getManagedBean().setHasActiveTransaction(false);
		if (getManagedBean().getSmSupp() != null) {
			getManagedBean().setHasActiveTransaction(
					SmApplicationUtil.hasActiveTransactions(getManagedBean()
							.getSmSupp()));
			if (!getManagedBean().isHasActiveTransaction()) {
				getManagedBean().setHasActiveTransaction(
						SmApplicationUtil
								.checkActiveSmApplForSupp(getManagedBean()
										.getSmSupp()));
			}

		}

	}
	
	public void updateMOFExpiredPersonnel(){
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMof();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		
		//CR 27382: Static Code Analysis
		//if(mofAccountLs.size() > 0){
		if(!mofAccountLs.isEmpty()){
			for(SmMofAccount mofAccount : mofAccountLs){
				boolean isChangeStatus = false;
				//CR 27382: Static Code Analysis
				//boolean activeAppl = SmApplicationUtil.checkActiveSmApplForSupp(mofAccount.getSmSupplier());				
				//if(!activeAppl){
				if(!SmApplicationUtil.checkActiveSmApplForSupp(mofAccount.getSmSupplier())){
					isChangeStatus = true;
				}

				if(!isChangeStatus){
					//CR 27382: Static Code Analysis
					//boolean isHasActiveTransaction = SmApplicationUtil.hasActiveTransactions(mofAccount.getSmSupplier());
					//if(!isHasActiveTransaction){
					if(!SmApplicationUtil.hasActiveTransactions(mofAccount.getSmSupplier())){
						isChangeStatus = true;
					}
				}

				//change role to basic
				if(isChangeStatus){
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					schedulerReqDTO.setMofAccount(mofAccount);
					
					try{
						SmServiceLookup.getSmSchedulerSO().updateMOFSupplierPersonnelToBasic(schedulerReqDTO);
					}catch(Exception ex){
						log.error("ChangeExpiredMofPersonnel updateMoFAcc -  Exception>>>>" +  ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier());
					}
				}
			}

		}
		
	}
	
	public void sameAsOwnerOnChange(){
		SmPersonnel personnel = getManagedBean().getSmPersonnel();
		// #16052019 - SSM INTEGRATION CR
		if(getManagedBean().isSameAsOwner()){
			if(!getManagedBean().isCompanyValidWithSSM()){
				SmSupplierOwner suppOwner = getManagedBean().getSmSuppOwner();
				if(this.getManagedBean().getSmSuppOwners().size()>0){
					suppOwner = this.getManagedBean().getSmSuppOwners().get(0);
				}
				personnel.setPmCountry(suppOwner.getNationality());
				personnel.setName(suppOwner.getOwnerName());
				personnel.setIdentificationNo(suppOwner.getIdentificationNo());
				personnel.setIdentificationTypeId(suppOwner.getIdentificationTypeId());
				// 27691 - Check box follow first screen info
				personnel.setDirector(getManagedBean().isOwnerIsDirector());
				//27802
				personnel.setEquityOwner(getManagedBean().isOwnerIsEquity());
			}else{
				this.getManagedBean().setSmPersonnelSelected(new SmPersonnel());				
			}
			

		}else{
			PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			personnel.setPmCountry(defaultCountry);
			personnel.setName(null);
			personnel.setIdentificationNo(null);
			personnel.setIdentificationTypeId(null);
			personnel.setBirthDateSsmNull(true);
			personnel.setBirthDate(null);
			//START 27609
			personnel.setEquityOwner(false);
			personnel.setDirector(false);
			personnel.setClerical(false);
			// #30829 - start
			// listenerChangeRole();
			personnel.setHigherManagement(false);
			personnel.setManagement(false);
			personnel.setProf(false);
			personnel.setSemiProf(false);
			// #30829 - end
			//END 27609
			this.getManagedBean().setSmPersonnelSelected(new SmPersonnel());
			//#16052019 - SSM INTEGRATION CR
			//SmAddress address = new SmAddress();
			//address.setPmCity(new PmCity());
			//address.setPmDistrict(new PmDistrict());
			//address.setPmCountry(defaultCountry);
			//address.setPmState(new PmState());
			//this.getManagedBean().setSmPermenantAdd(address);
		}
		// #30829 - start
		listenerChangeRole();
		// #30829 - end
		getManagedBean().setSmPersonnel(personnel);
		personnelNationalityOnChange();
	}
	
	
	private void validateNewIC(SmPersonnel smPersonnel, boolean isIndividual){
		//Validate IC Length
		PmParameter personnelIDType = ParameterHelper.getInstance().getPmParameterById(smPersonnel.getIdentificationTypeId());
		if(personnelIDType.getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
			this.validateIDFormat(smPersonnel.getIdentificationNo(), personnelIDType.getParameterCode());
		
			if(!isIndividual){
				if(!smPersonnel.getPmCountry().getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE)){
					SmCommonUtil.getInstance().verifyWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(),
							getManagedBean().getSmPermenantAdd().getPostcode(), null,smPersonnel.isSsmData());
				}else{
					SmCommonUtil.getInstance().verifyWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(),
							getManagedBean().getSmPermenantAdd().getPostcode(), my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE,smPersonnel.isSsmData());
				}
			}else{
				if(smPersonnel.isSsmData()){
					SmCommonUtil.getInstance().verifyOwnerWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(),
							getManagedBean().getSmPermenantAdd().getPostcode(), my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_SSM,
							null,smPersonnel.isSsmData());
				}else{
					SmCommonUtil.getInstance().verifyOwnerWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(),
							getManagedBean().getSmPermenantAdd().getPostcode(), my.ep.constant.sm.SupplierManagementConst.MY_IDENTITY_TYPE_ONLINE,
							null,smPersonnel.isSsmData());
				}
				
			}
		}
		
	}
	//#16052019 - SSM INTEGRATION CR
	public void nameOnchange(){
		if(this.getManagedBean().getSmPersonnelSelected() != null){	
			if(this.getManagedBean().getSmPersonnelSelected().getIdentificationNo() != null){
				SmPersonnel personnel = (SmPersonnel) EntityHelper
						.getEntityBySinglePropertyFromList(this.getManagedBean().getSmPersonnelSelected().getIdentificationNo(),this.getManagedBean().getSmPersonnelAll(),"identificationNo");
				
				if(personnel.getPmCountry() == null || personnel.getPmCountry().getCountryId() == null){
					PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
					personnel.setPmCountry(defaultCountry);
				}				
				getManagedBean().setSmPersonnel((SmPersonnel)personnel.cloneEntity());
			} else {
				this.getManagedBean().getSmPersonnel().setBirthDate(null);
				this.getManagedBean().getSmPersonnel().setIdentificationNo(null);
			}
			
			if(this.getManagedBean().getSmPersonnel().getTitle() == null) {
				this.getManagedBean().getSmPersonnel().setTitle(new PmSalutation());
			}
			listenerChangeRole();
		}
	}
	//#16052019 - SSM INTEGRATION CR
	public void showOwnerCB() {
		String businessType = this.getManagedBean().getSmSupp().getBusinessType();
		this.getManagedBean().setShowDirectorInOwner(false);
		this.getManagedBean().setOwnerIsEquity(false);
		this.getManagedBean().setDisableRole(false);
		this.getManagedBean().setOwnerIsDirector(false);
		if (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD.equalsIgnoreCase(businessType)) {
			// show all & editable & minimum 1
			//For ROC and LLP
			//- Role in company to display "Equity Owner" and "Director" and editable and to select at least one.
			this.getManagedBean().setShowDirector(true);
			this.getManagedBean().setShowOwner(true);
			this.getManagedBean().setDisableRole(false);
			this.getManagedBean().setOwnerIsEquity(true);
			this.getManagedBean().setOwnerIsDirector(true);
		} else if (SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG
				.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH
						.equalsIgnoreCase(businessType)
				|| SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK
						.equalsIgnoreCase(businessType)) {
			// show “Equity Owner” and the checkbox auto-ticked and
			// non-editable.
			//For ROB, Professional Body, Business Registration Number in Sabah & Sarawak
			//- Role in company to display “Equity Owner” and the checkbox auto-ticked and non-editable.
			this.getManagedBean().setShowDirector(false);
			this.getManagedBean().setShowOwner(true);
			this.getManagedBean().setDisableRole(true);
			this.getManagedBean().setOwnerIsEquity(true);
		} else {
			// show “Director” and the checkbox auto-ticked and non-editable.
			//For Cooperative (Registered with SKM and Others), Society, ROS (Organization registered with JPPM) and Organization (Others)
			//- Role in company to display “Director” and the checkbox auto-ticked and non-editable.
			this.getManagedBean().setShowDirector(true);
			this.getManagedBean().setShowDirectorInOwner(true);
			this.getManagedBean().setShowOwner(false);
			this.getManagedBean().setDisableRole(true);
			this.getManagedBean().setOwnerIsDirector(true);
		}
		//if company valid with ssm need to disable checkbox role
		if(this.getManagedBean().isCompanyValidWithSSM()){
			this.getManagedBean().setDisableRole(true);
		}
		//if DA is active
		if(this.getManagedBean().isActiveDisicplinaryAction()){
			this.getManagedBean().setDisableRole(true);
		}
	}
	
	//16052019 - SSM INTEGRATION CR START
	public void validateBusinessType() {
			if(SmCommonUtil.getInstance().ssmActive()){
				String businessType = getManagedBean().getSmSupp().getBusinessType().toUpperCase();
				switch(businessType){
					//ROB PARTNERSHIP must > 1 owners
					case SupplierManagementConst.BUSINESS_TYPE_ROB_PARTNERSHIP:
						if(getManagedBean().getSmSuppOwners().size() <= 1) {
							throw new EpException(MessageConst.ERROR_721077);
						}
						break;
					//ROB SOLE PROPRIETOSHIP must <= 1 owner
					case SupplierManagementConst.BUSINESS_TYPE_ROB_SOLE_PROPRIETORSHIP:
						if(getManagedBean().getSmSuppOwners().size() > 1) {
							throw new EpException(MessageConst.ERROR_721077);
						}
						break;
					default:
						break;
				}	
			}				
	}
	//16052019 - SSM INTEGRATION CR END
	
	public void setPersonnelToItem(){
		this.getManagedBean().getSelectItemsSmPersonnelOwners().clear();
		for(SmPersonnel personnel : this.getManagedBean().getSmPersonnelAll()){
			if(personnel.isEquityOwner() || personnel.isDirector()){
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(personnel.getName());
				selectItem.setValue(personnel.getIdentificationNo());
				//Redmine #26594
				//if(this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
					this.getManagedBean().getSelectItemsSmPersonnelOwners().add(selectItem);//FOR MOF
				//}else if(personnel.isEquityOwner()){
					//this.getManagedBean().getSelectItemsSmPersonnelOwners().add(selectItem);//FOR BASIC
				//}
			}
		}
	}
	//16052019 - SSM INTEGRATION CR => START
	public void directorNationalityOnChange(SmPersonnel director){
		//16052019 - SSM INTEGRATION CR => START
		if(director != null){
			PmCountry country = SmCommonUtil.retrievePmCountryById(director.getPmCountry().getCountryId());
			director.setPmCountry(country);
			SmPersonnel personnel = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(director.getIdentificationNo(), this.getManagedBean().getSmPersonnelAll(), "identificationNo");
			personnel.setPmCountry(director.getPmCountry());
			personnel.setName(director.getName());
		}
		
		
	}
	
	//26238 - start 
	public void retrieveActiveDisciplinaryAction(SmSupplier supplier){
		if (supplier == null){
			return;
		}
		//39068 - START
		supplier.setEndDate(true);
		getManagedBean().setDisciplinaryAction(
				!EntityHelper.isCollectionEmpty(SmApplicationUtil
						.retrieveSmDisciplinaryActionBySupplier(supplier)));
		//39068 - END
		List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		if(EntityHelper.isCollectionEmpty(discList)){
			getManagedBean().setNoDisciplinary(DbValueConst.FLAG_YES);
			return;
		}
		getManagedBean().setNoDisciplinary(DbValueConst.FLAG_NO);
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(supplier.getSupplierId());
		
		//27915: Disciplinary Action - start - 26238
		for(SmDisciplinaryAction disc : discList){
			getManagedBean().setWarning(disc.isWarning());
			getManagedBean().setCancelCategory(disc.isCancelCategory());
			getManagedBean().setSuspendRegistration(disc.isSuspendRegistration());
			getManagedBean().setCancelRegistration(disc.isCancelRegistration());
			getManagedBean().setBlacklistowner(disc.isBlacklistOwner());
			//26238 - 38419
			getManagedBean().setBlacklistownerDirectorList(getPersonnelBlacklist(
					disc.getDisciplinaryActionId()));
			String pattern = "dd/MM/yyyy";
			getManagedBean().setStartDate(DateUtils.dateToString(pattern, disc.getStartDate()));
			getManagedBean().setEndDate(DateUtils.dateToString(pattern, disc.getEndDate()));
			getManagedBean().setRemarks(disc.getRemark());
		}
		getManagedBean().setActiveDisicplinaryAction(
				getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration() 
				|| getManagedBean().isBlacklistowner());
	}
	
	//26238 - end
	
	//16052019 - SSM INTEGRATION CR => START
	public void listenerChangeRole(){
		if(this.getManagedBean().getSmPersonnel()!=null){
			if (this.getManagedBean().getSmPersonnel().isEquityOwner()) {
				// #30829 - start
				this.getManagedBean().setDisableSemiProf(true);
				if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_FOR_ROLE_IN_COMPANY.contains(this.getManagedBean()
						.getSmSupp().getBusinessType())) {
					this.getManagedBean().setDisableStaff(false);
					this.getManagedBean().setDisableProf(false);
				} else {
					this.getManagedBean().setDisableManagement(false);
					this.getManagedBean().setDisableStaff(true);
					this.getManagedBean().setDisableProf(false);
				}
				// #30829 - end
			} else if (!this.getManagedBean().getSmPersonnel().isEquityOwner()) {
				// #30829 - start
				this.getManagedBean().setDisableHigherManagement(false);
				this.getManagedBean().setDisableManagement(false);
				this.getManagedBean().setDisableProf(false);
				this.getManagedBean().setDisableSemiProf(false);
				this.getManagedBean().setDisableStaff(false);

				if (this.getManagedBean().getSmPersonnel().isDirector()
						|| this.getManagedBean().getSmPersonnel().isHigherManagement()
						|| this.getManagedBean().getSmPersonnel().isManagement()) {
					this.getManagedBean().setDisableSemiProf(true);
					this.getManagedBean().setDisableStaff(true);
				}
				if (this.getManagedBean().getSmPersonnel().isProf()) {
					this.getManagedBean().setDisableSemiProf(true);
					if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_FOR_ROLE_IN_COMPANY.contains(this
							.getManagedBean().getSmSupp().getBusinessType())) {
						this.getManagedBean().setDisableStaff(true);
					}
				}
				if (this.getManagedBean().getSmPersonnel().isSemiProf()) {
					this.getManagedBean().setDisableHigherManagement(true);
					this.getManagedBean().setDisableManagement(true);
					this.getManagedBean().setDisableProf(true);
					this.getManagedBean().setDisableStaff(true);
				}

				if (this.getManagedBean().getSmPersonnel().isClerical()) {
					this.getManagedBean().setDisableHigherManagement(true);
					this.getManagedBean().setDisableManagement(true);
					this.getManagedBean().setDisableSemiProf(true);
					if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_FOR_ROLE_IN_COMPANY.contains(this
							.getManagedBean().getSmSupp().getBusinessType())) {
						this.getManagedBean().setDisableProf(true);
					}
				}
			}
			// #30829 - end
		}
	}
	
	//SSM INTEGRATION CR
	public void setOwnerNationality(SmSupplierOwner owner,boolean updatePersonnel){
		getUnknownCountry();
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		PmCountry country = null;
		if(owner.getTempCountryId() > 0){
			country  = SmCommonUtil.retrievePmCountryById(owner.getTempCountryId());
		}else{
			country  = SmCommonUtil.retrievePmCountryById(owner.getNationality().getCountryId());
		}
		if(owner.getTempCountryId() > 0 || owner.getNationality().getCountryId() > 0) {
			owner.setNationality(country);
			//set if malaysia or not
			//#30064, #29582 & #35300 start
			if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) || 
					country.getCountryCode().equalsIgnoreCase(ParameterConst.COUNTRY_UNKNOWN_CODE)||owner.isNationalityPMR()){
				reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
			}
			if (!country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) ) {
				if (owner.getIdentityResidentStatus() != null || owner.isNationalityPMR()) {
					if (owner.isNationalityPMR()|| owner.getIdentityResidentStatus().equals("M") || owner.getIdentityResidentStatus().equals("P") ) {
						reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
					}
				} else {
					reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_PASSPORT);
				}
			}//#30064, #29582 & #35300 end
			reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
		} else {
			PmCountry defaultNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			owner.setNationality(defaultNationality);
			reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
			reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
		}
		
		PmParameter identObj = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqDTO).getPmParameter();
		owner.setIdentificationTypeId(identObj.getParameterId());
		owner.setIdentificationType(identObj);
		//#26987
		if(getManagedBean().isSameAsOwner() && getManagedBean().getSmPersonnel()!= null){
			SmPersonnel personnel = getManagedBean().getSmPersonnel();
			personnel.setPmCountry(owner.getNationality());
			personnel.setName(owner.getOwnerName());
			personnel.setIdentificationNo(owner.getIdentificationNo());
		}
		if(updatePersonnel){
			SmPersonnel personnel = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(owner.getIdentificationNo(), this.getManagedBean().getSmPersonnelAll(), "identificationNo");
			//27477
			if(personnel != null){
				personnel.setPmCountry(owner.getNationality());
				personnel.setName(owner.getOwnerName());
			}			
		}
	}
	//Start 17906

	/**
	 * Removed selected approved categories from the list.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void unSelectRow(AjaxBehaviorEvent e) {
		
		if (getManagedBean().getSelectedSuppBanks() != null) {
			SmSupplierBank[] selectedSuppBanks = getManagedBean().getSelectedSuppBanks();
			for(int i = 0;i<getManagedBean().getSelectedSuppBanks().length;i++){
				SmSupplierBank bank = getManagedBean().getSelectedSuppBanks()[i];
				if(bank.isHasActiveTransactionContractManagement() || bank.isHasActiveTransactionFulfilment()){
					selectedSuppBanks = ArrayUtils.remove(selectedSuppBanks, i);
				}
			}			
			getManagedBean().setSelectedSuppBanks(selectedSuppBanks);
		}		
	}
	
	public void checkingBeforeRemoveSupplierBank() {
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='red'>");
		boolean showErrorPopup = false;
		boolean showErrorContract = false;//to avoid duplicate message
		boolean showErrorFl = false;//to avoid duplicate message
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			this.getManagedBean().setDisplayMessageBank("");
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				if(suppBank.isHasActiveTransactionContractManagement() && !showErrorContract){
					showErrorPopup = true;
					showErrorContract = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied",null));
					sb.append("<br/>");
				}
				if(suppBank.isHasActiveTransactionFulfilment() && !showErrorFl){
					showErrorPopup = true;
					showErrorFl = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied",null));
					sb.append("<br/>");
				}				
			}
		}
		sb.append("</font>");
		this.getManagedBean().setDisplayMessageBank(sb.toString());
		if(showErrorPopup){
			RequestContext.getCurrentInstance().execute("bankAlertPopUp.show()");
			return;
		}
		RequestContext.getCurrentInstance().execute("bankDeletePopUp.show()");
	}
	//End 17906
		
	//13320-start
	public void populateDivisionByAddType(String typeAdd, boolean reset){				
		if((typeAdd.equals("business"))){
			//registrationBackingBean.managedBean.smBusinessAdd.pmDivision.divisionId

			//#34159 - START
			if(this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId() == null){
				this.getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
			} else {
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId());			
				this.getManagedBean().setBusinessDistrictLs(divisionLs);
			}
			
			this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
			getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
			//#36718 - START - execute only if reset
			if(reset){
				getManagedBean().getSmSupp().setLocalAuthorityId(null);
				if(!getManagedBean().getSmBusinessAdd().isSsmData()){
					getManagedBean().getSmBusinessAdd().setPostcode("");
				} 
				getManagedBean().getSmBusinessAdd().setMapLatitude("");
				getManagedBean().getSmBusinessAdd().setMapLongitude("");
			}
			//#36718 - END
			//#34159 - END
		}
		if((typeAdd.equals("residential"))){
			//registrationBackingBean.managedBean.smResAdd.pmDivision.divisionId
			//#34159 - START
			if(reset){
				getManagedBean().getSmResAdd().setPostcode("");
			}
			if(this.getManagedBean().getSmResAdd().getPmDivision().getDivisionId() == null){
				this.getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
			} else {
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmResAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setResidentialDistrictLs(divisionLs);
			}
			
			this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
			//#34159 - END
		}
		if((typeAdd.equals("permenant"))){
			//registrationBackingBean.managedBean.smPermenantAdd.pmDivision.divisionId
			//#34159 - START
			if(reset){
				getManagedBean().getSmPermenantAdd().setPostcode("");
			}
			if(this.getManagedBean().getSmPermenantAdd().getPmDivision().getDivisionId() == null){
				this.getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
			} else {
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmPermenantAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setPermenantDistrictLs(divisionLs);
			}
			this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());	
			this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());
			//#34159 - END
		}
	}
	public boolean showDivision(PmState state){
		if(state!=null){
			PmState st = (SmCommonUtil.getInstance().getStateById(state.getStateId()));
			if(st!=null){
				return st.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE);
			}
		}		
		return false;
	}
	//13320-end
	
	//14685 - START
	public void showMessageToggle(){
		if(this.getManagedBean().isShowMessage()){
			this.getManagedBean().setShowMessage(false);
		}else{
			this.getManagedBean().setShowMessage(true);
		}
	}
	//14685 - END
	
	//  #30829 - Start 1/11/2021
    //  #30829 - Start 1/11/2021
	private SmPersonnel onSaveSmPersonnelEquityOwner(SmAppl smAppl, SmSupplierOwner smSupplierOwner, SmPersonnel smPersonnel){
		
		smPersonnel.setName(smSupplierOwner.getOwnerName());
		smPersonnel.setIdentificationNo(smSupplierOwner.getIdentificationNo());
		smPersonnel.setIdentificationTypeId(smSupplierOwner.getIdentificationTypeId());
		smPersonnel.setIdentificationType(smSupplierOwner.getIdentificationType());

		this.getManagedBean().getSmPersonnel().setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smPersonnel.setAuthorizedFlg(SupplierManagementConst.FIRST_USER_AUTHO);
		smPersonnel.setContactPerson(false);
		smPersonnel.setContractSignerFlg(0);
		smPersonnel.setBumiFlg(0);
		smPersonnel.setMuslimFlg(0);
//		smPersonnel.setClerical(false);
		smPersonnel.setEquityOwner(true);
		
		//to set the softcert Flag
		//All supplier type will mark to "SOFT_CERT_PENDING_REQ", except supplier type is "Consultant"
		if(!smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			smPersonnel.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
		}else{
			smPersonnel.setSoftcertFlg(0);
		}
		
		smPersonnel.setSmAppl(smAppl);
		
		return smPersonnel;
	}
	//  #30829 - End 1/11/2021
		
	// #30829 - start
	private void checkRoleInCompany() {
		if (!getManagedBean().isSameAsOwner()) {
			if (!getManagedBean().getSmPersonnel().isEquityOwner() && !getManagedBean().getSmPersonnel().isDirector()
					&& !getManagedBean().getSmPersonnel().isHigherManagement()
					&& !getManagedBean().getSmPersonnel().isManagement() && !getManagedBean().getSmPersonnel().isProf()
					&& !getManagedBean().getSmPersonnel().isSemiProf()
					&& !getManagedBean().getSmPersonnel().isClerical()) {
				throw new EpException(MessageConst.ERROR_722535);
			}
		}
	}
	// #30829 - end
	//#27691 - START 
	public void toggleAllOwnerTick(){
		if(this.getManagedBean().isAllOwnerSelected()){
			for(SmSupplierOwner owner : this.getManagedBean().getSmSuppOwners()){
				if(!isSmSupplierOwnerIsAdmin(owner)){
					owner.setOwnerSelected(true);
					if(this.getManagedBean().getSelectedSuppOwners()==null || this.getManagedBean().getSelectedSuppOwners().isEmpty()){
						this.getManagedBean().setSelectedSuppOwners(new ArrayList<SmSupplierOwner>());
						this.getManagedBean().getSelectedSuppOwners().add(owner);
					}else{
						this.getManagedBean().getSelectedSuppOwners().add(owner);
					}
				}
			}
		}else{
			for(SmSupplierOwner owner : this.getManagedBean().getSmSuppOwners()){
				owner.setOwnerSelected(false);
				Iterator<SmSupplierOwner> iterator = this.getManagedBean().getSelectedSuppOwners().iterator();
				while (iterator.hasNext()) {
					SmSupplierOwner selectedOwner = iterator.next();
					if(owner.equals(selectedOwner)){
						iterator.remove();
					}
				}
			}
		}
	}
	
	public void toggleOwnerCheckBox(SmSupplierOwner owner){
		if(owner.isOwnerSelected()){
			if(this.getManagedBean().getSelectedSuppOwners()==null || this.getManagedBean().getSelectedSuppOwners().isEmpty()){
				this.getManagedBean().setSelectedSuppOwners(new ArrayList<SmSupplierOwner>());
				this.getManagedBean().getSelectedSuppOwners().add(owner);
			}else{
				this.getManagedBean().getSelectedSuppOwners().add(owner);
			}
		}else{
			this.getManagedBean().setAllOwnerSelected(false);
			Iterator<SmSupplierOwner> iterator = this.getManagedBean().getSelectedSuppOwners().iterator();
			while (iterator.hasNext()) {
				SmSupplierOwner selectedOwner = iterator.next();
				if(owner.equals(selectedOwner)){
					iterator.remove();
				}
			}
		}
		
		if(!this.getManagedBean().getSmSuppOwners().isEmpty()){
			boolean isSelectAll = true;
			for(SmSupplierOwner ownerSelect : this.getManagedBean().getSmSuppOwners()){
				if(!ownerSelect.isOwnerSelected() && !isSmSupplierOwnerIsAdmin(ownerSelect)){
					isSelectAll = false;
					break;
				}
			}
			if(!isSelectAll){
				this.getManagedBean().setAllOwnerSelected(false);
			}else{
				this.getManagedBean().setAllOwnerSelected(true);
			}
		}
	}
	//#27691 - END 
	//#35581 START
	/**
	 * check validity RegNo
	 */
	public void regNoValidity() {
		if (StringUtils.isNotBlank(getManagedBean().getSmSupp().getRegNo())
				&& !validateString(getManagedBean().getSmSupp().getRegNo())) {
			if (sabahSarawakRemoveWhiteSpaceList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723100,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (regNoValidityList.contains(getManagedBean().getSmSupp().getBusinessType())) {
				throw new EpException(MessageConst.ERROR_723101,
						new String[] {ResourceBundleUtils.getMessage("lbl.sm.businessregno", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}
	}
	
	/**
	 * check validity string
	 */
	public static boolean validateString(String regNo) {
        for (String validator : validators) {
            Pattern pattern = Pattern.compile(validator);
            Matcher matcher = pattern.matcher(regNo);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
	//#35581 END
	
	//#36594 - Start
	/**
	 * Set terms and condition message with parameter.
	 * 
	 * @return String
	 */
	public String setMofRegTnc() {
		int renewalPeriod = SmServiceLookup.getSmRuleSO()
				.retrieveRenewalPeriodForAppl(
						ParameterConst.NEW_SM_APPLN_FOR_RENEW);

		String word = NumberWordUtil.changeNumberToWord(renewalPeriod, new FacesScopes().getUserLocale());
		
		return ResourceBundleUtils.getMessage("lbl.sm.basic.account.mof.tnc",
				new String[] { word, String.valueOf(renewalPeriod) },
				ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
	}
	//#36594 - End
	
	//#36710 --START
	public SelectItem[] pmBusinessTypeDropDownAccountInfo(){
		FacesScopes facesScopes = new FacesScopes();		
		List<SelectItem> businessTypeLs = new ArrayList<SelectItem>();
		businessTypeLs.add(new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					facesScopes.getUserLocale())));
		
		SelectItem[] items = null;
		String applType = this.getManagedBean().getApplType();
		String businessType = getManagedBean().getSmSupp()!= null?getManagedBean().getSmSupp().getBusinessType():"";
		
		if(applType != null && applType.equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)){
			items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);
			
			for(SelectItem item : items){
				if(item.getValue() != null){
					if(!businessTypeIsROB(businessType) || !businessTypeIsROC(businessType)){
						if(!item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY) 
								&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
							businessTypeLs.add(item);
						}
					}
				}
			}	
			items =  businessTypeLs.toArray(new SelectItem[businessTypeLs.size()]);
		}else{
			String supplierType = this.getManagedBean().getSmAppl().getSupplierType();
			if(supplierType != null){
				if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
					items = getDropdownCodeValuesContractorCooperative(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR);
					
					for(SelectItem item : items){
						if(item.getValue() != null){
							if(!businessTypeIsROB(businessType) || !businessTypeIsROC(businessType)){
								if(!item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
									businessTypeLs.add(item);
								}
							}
						}
					}
					items =  businessTypeLs.toArray(new SelectItem[businessTypeLs.size()]);
				}else if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
					items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT);
					
					for(SelectItem item : items){
						if(item.getValue() != null){
							if(!businessTypeIsROB(businessType) || !businessTypeIsROC(businessType)){
								if(!item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD) 
										&& !item.getValue().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)){
									businessTypeLs.add(item);
								}
							}
						}
					}
					items =  businessTypeLs.toArray(new SelectItem[businessTypeLs.size()]);
				}
			}else{
				items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						facesScopes.getUserLocale()));
			}
		}
		return items;
	} //#36710 --END
	
	//#36718 START
	/**
	 * To reset correspondence address
	 * 
	 */
	public void onTickSameAddress(){
		if(!getManagedBean().isSameAddress()){
			getManagedBean().setSmResAdd(SmApplicationUtil.setDefaultAddr(null));
		}
	}
	//#36718 END
}