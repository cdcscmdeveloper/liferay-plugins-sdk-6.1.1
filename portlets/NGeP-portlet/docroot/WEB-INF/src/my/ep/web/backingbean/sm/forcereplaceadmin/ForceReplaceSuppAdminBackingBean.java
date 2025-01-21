package my.ep.web.backingbean.sm.forcereplaceadmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmReplaceSupplierAdminDoc;
import my.ep.persistence.entity.sm.SmReplaceSupplierAdminDocFile;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.forcereplaceadmin.ForceReplaceSuppAdminManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
//#28765 - Start
//#28765 - End
import org.primefaces.event.SelectEvent;
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementAdminInformationBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
// #28895_35501 End

@ManagedBean(name = "forceReplaceSuppAdminBackingBean")
@RequestScoped
public class ForceReplaceSuppAdminBackingBean extends
		BaseBackingBean<ForceReplaceSuppAdminManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 650929814581089830L;
	
	//#28765 - Start
	private static final String CATALOGUE_PUBLISHED = "U";
	//#28765 - End
	private static final Logger log = EpLogger.getLogger(ForceReplaceSuppAdminBackingBean.class);
	// Start 28765 comment changes 12981
	//12981
	//private PersonnelResDTO replaceExistingPersonnel;
	// End 28765 comment changes 12981

	/**
	 * Pre - render and redirect to Force Replace Supplier Admin page
	 */
	public void preRenderForceReplaceAdmin() {
		renewSesionObject();
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		getManagedBean().setPermStates(new ArrayList<SelectItem>());
		getManagedBean().setPermDivisions(new ArrayList<SelectItem>()); //#34159
		getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
		getManagedBean().setPermCities(new ArrayList<SelectItem>());
		getManagedBean().setMailStates(new ArrayList<SelectItem>());
		getManagedBean().setMailDivisions(new ArrayList<SelectItem>()); //#34159
		getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
		getManagedBean().setMailCities(new ArrayList<SelectItem>());
		getManagedBean().setHasSupplierUser(false);
		getManagedBean().setReactivateBasic(new ReactivateSupplierVO());
		getManagedBean().setSmSupplier(new SmSupplier());
		getManagedBean().setSalutationItems(SmCommonUtil.getSalutationItems());
		initIdentificationTypes();
		initCountries();
		initStates();
		SmCommonUtil
				.redirectToPage("forceReplaceAdmin.xhtml", null, null, null);
	}

	/**
	 * Submit force replace and display pop up (for existing personnel) or
	 * redirect to new personnel record page
	 */
	public void submit() {
		getManagedBean().setExistingUser(false);
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		/*log.info("Acc.Type:" + getManagedBean().getAccType());
		log.info("Reg.No:" + getManagedBean().getReactivateBasic().getRegNo());
		log.info("Est.Date:" + getManagedBean().getReactivateBasic().getEstablishDate());
		log.info("Agency Name:" + getManagedBean().getReactivateBasic().getAgencyName());
		log.info("Supplier Type:" + getManagedBean().getReactivateBasic().getSuppType());*/
		reqDTO.setRegNo(getManagedBean().getReactivateBasic().getRegNo());
		reqDTO.setEstDate(getManagedBean().getReactivateBasic()
				.getEstablishDate());
		reqDTO.setCompanyName(getManagedBean().getReactivateBasic()
				.getAgencyName());
		reqDTO.setSuppType(getManagedBean().getReactivateBasic().getSuppType());
		reqDTO.setAccType(getManagedBean().getAccType()); //#33129

		//#28765 chained method retrieveSupplierAndPersonnelsByBusRegNoAndNameBypassActiveAcc
		if (getManagedBean().getAccType().equals(
				SupplierManagementConst.ACC_GOVT_SELLER)) {
			getManagedBean().setSmSupplier(
					SmServiceLookup
							.getSupplierSearchSO()
							.retrieveSupplierAndPersonnelsByBusRegNoAndNameBypassActiveAcc(
									reqDTO).getSmSupplier());

			if (getManagedBean().getSmSupplier() != null
					&& getManagedBean().getSmSupplier().getSmAppl() != null
					&& !getManagedBean()
							.getSmSupplier()
							.getSmAppl()
							.getSupplierType()
							.equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
				// Agency name does not exist for G2G
				throw new EpException(MessageConst.ERROR_722659);
			}
		} else {
			//START 35023
			if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)) {
				SmSupplier smSupplier = SmServiceLookup.getSupplierSearchSO().retrieveSupplierByRegNo(
						getManagedBean().getReactivateBasic().getRegNo());
				if (smSupplier != null && smSupplier.getBusinessType().equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_INDIVIDUAL)) {
					throw new EpException(MessageConst.ERROR_722706);
				}
			}
			//END 35023
			getManagedBean()
					.setSmSupplier(
							SmServiceLookup
									.getSupplierSearchSO()
									.retrieveSuppProfileAndPersonnelsByBussRegNoAndEstDate(
											reqDTO).getSmSupplier());
		}

		// Company not exist
		if (getManagedBean().getSmSupplier() == null
				|| (getManagedBean().getSmSupplier().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE
						&& getManagedBean().getSmSupplier()
								.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_IN_PROGRESS && getManagedBean()
						.getSmSupplier().getRecordStatusFlg() != 7)) {
			if (getManagedBean().getAccType().equals(
					SupplierManagementConst.ACC_GOVT_SELLER)) {
				//#33129 - START 
				if(getManagedBean().getSmSupplier() != null){
					switch (getManagedBean().getSmSupplier().getRecordStatusFlg()){
						case DbValueConst.RECORD_STATUS_SUSPENDED : throw new EpException(MessageConst.ERROR_722703);
						case DbValueConst.RECORD_STATUS_CANCELLED : throw new EpException(MessageConst.ERROR_722701);
						case DbValueConst.RECORD_STATUS_DELETED   : throw new EpException(MessageConst.ERROR_722702);
					}
				} else {
					// Agency name does not exist for G2G
					throw new EpException(MessageConst.ERROR_722659);
				}
				//#33129 - END
			} else {
				//33129 - START
				if(getManagedBean().getSmSupplier() != null){
					switch (getManagedBean().getSmSupplier().getRecordStatusFlg()){
						case DbValueConst.RECORD_STATUS_SUSPENDED : throw new EpException(MessageConst.ERROR_722700);
						case DbValueConst.RECORD_STATUS_CANCELLED : throw new EpException(MessageConst.ERROR_722698);
						case DbValueConst.RECORD_STATUS_DELETED   : throw new EpException(MessageConst.ERROR_722699);
					}
				}
				else{
					// Buss Reg No. and Established Date does not exist for Supplier
					throw new EpException(MessageConst.ERROR_722593);
				}
				//#33129 - END
			}
		} else {
			//#28765 - Start
			if (getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER) && 
					getManagedBean().getSmSupplier()!=null && 
					checkPendingSoftCertRequest(getManagedBean().getSmSupplier().getSupplierId()) && 
					getManagedBean().getSmSupplier().getRecordStatusFlg()!=DbValueConst.RECORD_STATUS_ACTIVE) {
					personnelBypassValidation();
				}
			if (getManagedBean().getAccType().equals(SupplierManagementConst.ACC_BASIC) && 
					getManagedBean().getSmSupplier() != null && 
					(checkCatalogueUpload(getManagedBean().getSmSupplier().getSupplierId())==null || 
					checkPendingSoftCertRequest(getManagedBean().getSmSupplier().getSupplierId())) && 
					getManagedBean().getSmSupplier().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
				log.info("BASIC ACCOUNT <<<<<<<<<<<<");
				personnelBypassValidation();
			}
			if(getManagedBean().getSmSupplier()!=null && getManagedBean().getAccType().equals(SupplierManagementConst.ACC_MOF)) {
				personnelBypassValidation();
			}
			if(getManagedBean().isBypassFlag()==false) {
				StringBuilder sb = new StringBuilder();
				//12981 
				//28765 - START
				sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare4", null)); //#33129
				getManagedBean().setDisplayMessageReplaceTrans(sb.toString());
				//#28765 End
				validate();
				//START 27239
				if(SmApplicationUtil.isMofAccEffDatePassedCurrDate(getManagedBean().getSmSupplier())){
					if (!getManagedBean().isExistingUser()) {
						throw new EpException(MessageConst.ERROR_721087);
					}
				}
				
				//END 27239
				executeDecision();
				
				// redirectToNonExistNewAdminPage();
			}
			//#28765 - End
		}
	}
	//#28765 - Start
	public void checkingBeforeDeclare() {
		//# 33563
		validateRole();
		//#33589 - Start
		if(getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)){
			RequestContext.getCurrentInstance().execute("declareDeletePopUpBeforeId.show()");
		}else{
			RequestContext.getCurrentInstance().execute("declareDeletePopUp.show()");
		}
		//#33589 - End
	}
	//#28765 - End
	public void declare() {
		if (getManagedBean().isExistingUser()) {
			replaceWithExistingUser();
		} else {
			replaceWithNewUser();
		}
	}

	//#28765 - Start
		private PersonnelReqDTO getOldAdminReqDTO() {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
			reqDTO.setAdminPersonnel(getManagedBean().getOldAdmin());
			reqDTO.setSmPersonnel(getManagedBean().getOldAdmin());
			reqDTO.setEmail(getManagedBean().getOldAdmin().getEmail());
			//#28765 - Start chained method added .getApplId()
			reqDTO.setApplId(getManagedBean().getSmSupplier().getSmAppl()
					.getApplId());	
			//#28765 - End
			FacesScopes fs = new FacesScopes();
			reqDTO.setLanguage(fs.getUserLocale().getLanguage());
			reqDTO.setServerName(fs.getRequest().getServerName());
			reqDTO.setServerPort(fs.getRequest().getServerPort());
			reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
			reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE); //#33129
			reqDTO.setActionType(my.ep.constant.sm.SupplierManagementConst.FORCE_REPLACE_ADMIN);
			reqDTO.setTaskList(getTaskList());
			//#28765 - Start
			reqDTO.setUserLocale(fs.getUserLocale());
			reqDTO.setSmReplaceSupplierAdminDocs(getManagedBean().getSmReplaceSupplierAdminDocs());
			if (getManagedBean().getBypassFlag1()==1 || getManagedBean().getBypassFlag2()==1) {
					reqDTO.setRemoveSupplier(true);
			}	
			reqDTO.setFromReplaceAdmin(true);
			//#28765 - End
			// Start 28765 comment changes 12981
			//12981 - START
//			if(replaceExistingPersonnel != null && replaceExistingPersonnel.isTerminateSupplier()){
//				reqDTO.setRemoveSupplier(true);
//			}
			//#12981 - END
			// End 28765 comment changes 12981
			SmAppl activeAppl = getCurrActiveSmAppl(getManagedBean()
					.getSmSupplier());
			if (activeAppl != null) {
				reqDTO.setHasActiveAppl(true);
				reqDTO.setDraftApplId(activeAppl.getApplId());
			}
			return reqDTO;
		}
		//#28765 - End
	/**
	 * Replace admin with new personnel and redirect to success page
	 */
	public void replaceWithNewUser() {

		//#33589 - Start
		PersonnelResDTO resDto = null;
		if (!getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)) {
			getManagedBean().getNewAdmin().setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
			SmCommonUtil.setNewPersonnelPermAndMailAddress(getManagedBean().getNewAdmin(), getManagedBean()
					.getMailAddr(), getManagedBean().getPermAddr());

			validateMyIdentity();
			resDto = SmServiceLookup.getPersonnelSO().replaceAdminWithNewPersonnel(getReqDTO());
		} else {
			resDto = SmServiceLookup.getPersonnelSO().replaceAdminWithoutNewPersonnel(getReqDTO());
		}

		// Start 28765 comment changes 12981
		// START 12981
		// #28765 - Start - to pass through inactiveSupplierRole for G2G normal
		// replace supplier admin

		if (getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)) {
			redirectToSuccessPageForInProgress(resDto);
		} else {
			PersonnelResDTO resDto2 = SmServiceLookup.getPersonnelSO()
						.inactiveSupplierRole(getOldAdminReqDTO());
			//#33129
			if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) && getManagedBean().getSmSupplier().getSmAppl() != null &&
					getManagedBean().getSmSupplier().getSmAppl().getPmStatus() != null &&
					(getManagedBean().getSmSupplier().getSmAppl().getPmStatus().getStatusId().toString().equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER) ||
					getManagedBean().getSmSupplier().getSmAppl().getPmStatus().getStatusId().toString().equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)) ){ //#34483
				getManagedBean().setBypassFlag1(1);
			}
			//#28765 - End
			
			redirectToSuccessPage(resDto);
		}
		//#33589 - End
		// END 12981
	}

	/**
	 * Replace admin with existing personnel and redirect to success page
	 */
	public void replaceWithExistingUser() {
		
		//bug #26798, #25969
		//#33589 - Start
		if(!getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)){
			SmCommonUtil.setNewPersonnelPermAndMailAddress(getManagedBean()
					.getNewAdmin(), getManagedBean().getMailAddr(),
					getManagedBean().getPermAddr());	
			
			validateMyIdentity();
		}
		//#33589 - End
		// Start 28765 comment changes 12981
		//12981 START
		PersonnelResDTO resDto = SmServiceLookup.getPersonnelSO()
				.reassignSupplierAdmin(getReqDTO());
		//START 12981
		//#28765 - Start - inactiveSupplierRole to apply for both bypass/normal replace supplier admin
//		if(getManagedBean().getBypassFlag1()==1 && getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER)) {
			PersonnelResDTO resDto2 = SmServiceLookup.getPersonnelSO()
					.inactiveSupplierRole(getOldAdminReqDTO());
//		}
		//#28765 - End
		redirectToSuccessPage(resDto);
		//12981 END
		// End 28765 comment changes 12981
	}
	
	//#25969
	private void checkingAppl() {
		MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		mofReqDTO.setSupplierId(getManagedBean().getSmSupplier()
				.getSupplierId());
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofReqDTO).getSmAppl();
		//#27300
		SmAppl latestRejectedAppl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppType(this
				.getManagedBean().getSmSupplier().getSupplierId(), ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		
		/* #28765 - Comment G2G Negation
		if (!getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER)) {
		*/
			
			if (getManagedBean().getSmSupplier().getRecordStatusFlg()
						.equals(DbValueConst.RECORD_STATUS_IN_PROGRESS) && !getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER)){
				//#27300
				//28765
				if (activeAppl != null) {
					throw new EpException(MessageConst.ERROR_722669, new String[] { activeAppl.getApplNo() });
				}else if (latestRejectedAppl != null) {
					throw new EpException(MessageConst.ERROR_722669, new String[] { latestRejectedAppl.getApplNo() });
				}else if(SmApplicationUtil.isMofAccEffDatePassedCurrDate(getManagedBean().getSmSupplier())){
					if (!getManagedBean().isExistingUser()) {
						throw new EpException(MessageConst.ERROR_721087);
					}
				}
				//28765
//				else {
//					throw new EpException(MessageConst.ERROR_722676);
//				}
			} 
			
			SmPersonnel admin = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(
					RoleCode.SUPPLIER_TEMP.value(), getManagedBean().getSmSupplier().getSmAppl().getSmPersonnels(),
					"epRole");

			if (admin != null) {
				if (admin.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ
						|| admin.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
					//#27300
					if (activeAppl != null) {
						throw new EpException(MessageConst.ERROR_722669, new String[] { activeAppl.getApplNo() });
					}else if (latestRejectedAppl != null) {
						throw new EpException(MessageConst.ERROR_722669, new String[] { latestRejectedAppl.getApplNo() });
					}else {
						throw new EpException(MessageConst.ERROR_722676);
					}
				}
			}
		/* #28765 Closing for G2G Negation
		}
		*/
	}
	
	/**
	 * Checking if have multiple supplier have same registration number
	 * 
	 */
	//32208 - start
	private void checkingSupplier(){
		
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		
		List<Integer> recStatusLs = new ArrayList<Integer>();
		recStatusLs.add(DbValueConst.RECORD_STATUS_ACTIVE);
		recStatusLs.add(DbValueConst.RECORD_STATUS_IN_PROGRESS);
		
		reqDTO.setRegNo(this.getManagedBean().getSmSupplier().getRegNo().trim());
		reqDTO.setStatusLs(recStatusLs);
		
		RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO().retrieveSupplierByRegNoAndStatusLs(reqDTO);
		
		List<SmSupplier> supplierLs = resDTO.getSmSuppliers();
		
		//#33153 - Start
		// Sorting First By SupplierType
		//// #33129 - START : checking only for MOF account 
		if(getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){ 
			List<SmSupplier> removeSupp = new ArrayList<SmSupplier>();
			boolean isContractor = false;
			if (getManagedBean().getReactivateBasic().getSuppType()
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
				isContractor = true;
			} else {
				isContractor = false;
			}
	
			if (supplierLs.size() > 1) {
				for (SmSupplier supplier : supplierLs) {
					if (supplier.getSmAppl().getSupplierType() != null) {
						if (isContractor) {
							if (!supplier.getSmAppl().getSupplierType()
									.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
								removeSupp.add(supplier);
							}
						} else if (!isContractor) {
							if (!supplier.getSmAppl().getSupplierType()
									.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
								removeSupp.add(supplier);
							}
						}
					}
				}
				supplierLs.removeAll(removeSupp);
			}
		}  
		//#33129 - END
		//then start for throwing exception based from list that has been remove by supplier type
		//#33153 - End
		
		if(supplierLs.size() > 1){
			
			StringBuilder suppleirList = new StringBuilder();
			int num = 0;
			
			for (int i = 0; i < supplierLs.size(); i++) {
				
				num = i+1;
				
				SmSupplier supplier = supplierLs.get(i);
				String recordStatus = "";
				
				if(supplier.getRecordStatusFlg().equals(1)){
					recordStatus = MessageUtils.getMessage(MessageConst.INFO_700108, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()); //Active
				}else if(supplier.getRecordStatusFlg().equals(5)){
					recordStatus = MessageUtils.getMessage(MessageConst.INFO_700109, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()); //Registration process in progress
				}
				
				suppleirList.append(" <br> "+num+". " + supplier.getEpNo() + ": " + supplier.getCompanyName() + " (" + recordStatus + ") ");
			}
			
			suppleirList.append(" <br> ");
			
			throw new EpException(MessageConst.ERROR_722692,
					new String[] { suppleirList.toString() });
		}
	}
	//32208 - end
	

	/**
	 * Redirect back to Force Replace Admin first page from add new personnel
	 * information
	 * 
	 * @return String
	 */
	public String back() {
		getManagedBean().setAgreeTnc(false);
		getManagedBean().setExistingUser(false);
		getManagedBean().getReactivateBasic().setShowEmailInput(false);
		getManagedBean().setNewAdmin(null);
		getManagedBean().setOldAdmin(null);
		//28765 - START
		if(getManagedBean().getSmReplaceSupplierAdminDocs() != null){
			getManagedBean().getSmReplaceSupplierAdminDocs().clear();
		}
		//28765 - END
		return "forceReplaceAdmin.xhtml";
	}

	/**
	 * Close terms and condition pop up
	 */
	public void cancelTncPopup() {
		getManagedBean().setExistingUser(false);
		getManagedBean().setAgreeTnc(false);
		RequestContext.getCurrentInstance().execute("tncDialog.hide()");
	}

	/**
	 * Get current date
	 * 
	 * @return Date
	 */
	public Date getCurrDate() {
		return new Date();
	}

	/**
	 * Show or hide email input after click submit
	 */
	private void setShowOrHideEmail() {
		getManagedBean().getReactivateBasic().setShowEmailInput(false);
		if (getManagedBean().getNewAdmin() != null
				&& StringUtils.isBlank(getManagedBean().getNewAdmin()
						.getEmail())
				|| getManagedBean().getNewAdmin().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
			getManagedBean().getReactivateBasic().setShowEmailInput(true);
		}
	}

	/**
	 * Get constructed DTO
	 * 
	 * @return PersonnelReqDTO
	 */
	private PersonnelReqDTO getReqDTO() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		reqDTO.setAdminPersonnel(getManagedBean().getOldAdmin());
		
		//#33589 - Start
		reqDTO.setSmPersonnel(getManagedBean().getNewAdmin());
		if(!getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)){
			reqDTO.setEmail(getManagedBean().getNewAdmin().getEmail());	
		}
		//#33589 - End
		//#28765 - Start chained method added .getApplId()
		reqDTO.setApplId(getManagedBean().getSmSupplier().getSmAppl()
				.getApplId());	
		//#28765 - End
		FacesScopes fs = new FacesScopes();
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE); //#33129
		reqDTO.setActionType(my.ep.constant.sm.SupplierManagementConst.FORCE_REPLACE_ADMIN);
		reqDTO.setTaskList(getTaskList());
		//#28765 - Start
		reqDTO.setSmReplaceSupplierAdminDocs(getManagedBean().getSmReplaceSupplierAdminDocs());
		if (getManagedBean().getBypassFlag1()==1 || getManagedBean().getBypassFlag2()==1) {
				reqDTO.setRemoveSupplier(true);
		}	
		//#28765 - End
		SmAppl activeAppl = getCurrActiveSmAppl(getManagedBean()
				.getSmSupplier());
		if (activeAppl != null) {
			reqDTO.setHasActiveAppl(true);
			reqDTO.setDraftApplId(activeAppl.getApplId());
		}
		return reqDTO;
	}

	/**
	 * Validate before proceed to force replace admin
	 */
	private void validate() {
		//32208 - start
		checkingSupplier();
		//32208 - end
		
		//#25969
		checkingAppl();

		// validate ic length
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		paramReqDTO.setParameterId(getManagedBean().getReactivateBasic()
				.getNewIdentificationType());
		PmParameter idno = SmServiceLookup.getParameterSO()
				.retrieveParameterById(paramReqDTO).getPmParameter();
		if (idno.getParameterCode().equals(SupplierManagementConst.NEW_IC)) {
			if (getManagedBean().getReactivateBasic().getNewIdentificationNo()
					.length() < 12) {
				throw new EpException(MessageConst.ERROR_722532,
						new String[] { getManagedBean().getReactivateBasic()
								.getNewIdentificationNo() });
			}
		}

		// check if new admin identificationNo same as existing admin
		// identificationNo
		if (getManagedBean()
				.getReactivateBasic()
				.getIdentificationNo()
				.equals(getManagedBean().getReactivateBasic()
						.getNewIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_722600);
		}

		// validate if new admin identification no already exist in on going
		// application

		MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		mofReqDTO.setSupplierId(getManagedBean().getSmSupplier()
				.getSupplierId());
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofReqDTO).getSmAppl();

		if (activeAppl != null) {
			throw new EpException(MessageConst.ERROR_722681,
					new String[] { activeAppl.getApplNo() });
		}

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		List<SmPersonnel> latestActivePersonnels = SmServiceLookup
				.getPersonnelSO().retrieveLatestPersonnelBySupplierId(reqDTO)
				.getSmPersonnelList();

		if (!SmCommonUtil.isCollectionEmpty(latestActivePersonnels)) {
			for (SmPersonnel smPersonnel : latestActivePersonnels) {
				if (smPersonnel.getIdentificationNo() != null
						&& StringUtils.isNotBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {

					if (smPersonnel.getSmAppl() != null) {
						// already mof
						if (StringUtils.isNotBlank(smPersonnel.getSmAppl()
								.getApplType())
								&& !smPersonnel
										.getSmAppl()
										.getApplType()
										.equals(SupplierManagementConst.ACC_BASIC)) {
							// check if already had active mof supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& (smPersonnel.getEpRole().equals(
											RoleCode.MOF_SUPPLIER_USER.value()) || smPersonnel
											.getEpRole().equals(
													RoleCode.FL_USER.value()))
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
						// basic
						else {
							// check if already had active basic supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& (smPersonnel.getEpRole().equals(
											RoleCode.BASIC_SUPPLIER_USER
													.value()) || smPersonnel
											.getEpRole().equals(
													RoleCode.G2G_SUPPLIER_USER
															.value()))
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
					}
					// set old admin
					if (smPersonnel.getIdentificationNo().trim().equalsIgnoreCase(
							getManagedBean().getReactivateBasic()
									.getIdentificationNo().trim())) {
						getManagedBean().setOldAdmin(smPersonnel);
					}
					// set new admin
					else if (smPersonnel.getIdentificationNo().trim()
							.equalsIgnoreCase(
									getManagedBean().getReactivateBasic()
											.getNewIdentificationNo().trim())) {
						getManagedBean().setNewAdmin(smPersonnel);
						getManagedBean().setExistingUser(true);
					}
				}

				// existing deleted personnel
				else if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED
						&& !StringUtils.isBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getIdentificationNo().equalsIgnoreCase(
								getManagedBean().getReactivateBasic()
										.getNewIdentificationNo())) {
					getManagedBean().setNewAdmin(smPersonnel);
					getManagedBean().setExistingUser(false);
				}
			}
		}

		// selected old admin not exist
		if (getManagedBean().getOldAdmin() == null) {
			throw new EpException(MessageConst.ERROR_722594);
		} else {
			// [#26050] check if selected old admin does have any pending task - start
			//28765 -  Checking null pm user
			if (getManagedBean().getOldAdmin() != null && getManagedBean().getOldAdmin().getPmUser() !=null) {
				//log.info("[#26050] Old Admin login Id: " + getManagedBean().getOldAdmin().getPmUser().getLoginId());
				if (!StringUtils.isBlank(getManagedBean().getOldAdmin().getPmUser().getLoginId())				
						&& PmCommonUtil.isAnyPendingTask(getManagedBean().getOldAdmin().getPmUser().getLoginId())) {
					throw new EpException(MessageConst.ERROR_770167);
				}				
			}
			// [#26050] check if selected old admin does have any pending task - end
		}

		// selected old admin does not hold role as supplier admin
		if (getManagedBean().getOldAdmin() != null) {
			if (StringUtils.isBlank(getManagedBean().getOldAdmin().getEpRole())
					|| (getManagedBean().getOldAdmin().getEpRole()
							.equals(RoleCode.BASIC_SUPPLIER_USER.value())
							|| getManagedBean().getOldAdmin().getEpRole()
									.equals(RoleCode.MOF_SUPPLIER_USER.value()) || getManagedBean()
							.getOldAdmin().getEpRole()
							.equals(RoleCode.G2G_SUPPLIER_USER.value()))) {
				throw new EpException(MessageConst.ERROR_722651,
						new String[] { getManagedBean().getOldAdmin()
								.getIdentificationNo() });
			}
		}
		
		listenerClickStaffRole(); // #33563
	}

	/**
	 * Execute based on decision for existing personnel call terms and condition
	 * dialog pop up and for new personnel redirect to new personnel info page
	 */
	private void executeDecision() {
		hideShowRoleByBusinessTypeAndSupplierType(this.getManagedBean().getSmSupplier().getSmAppl().getSupplierType(),this.getManagedBean().getSmSupplier().getBusinessType());
		// replace with new personnel
		//#33589 - Start
		if (!getManagedBean().isExistingUser()) {
			if(getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)){
				checkingBeforeDeclare();
			}else{
				RequestContext.getCurrentInstance().execute(
						"replaceNewAdminConfirm.show()");	
			}
		}
		// replace with existing personnel
		else {
			if(getManagedBean().getSmSupplier().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_IN_PROGRESS)){
				checkingBeforeDeclare();
			}else{
				setShowOrHideEmail();
				initDefaultNewAdmin();
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/forcereplaceadmin/addNewAdminInfo.xhtml", "",
						null, null);
			}
		}
		//#33589 - End
	}

	/**
	 * Redirect to non exist new admin page
	 */
	public void redirectToNonExistNewAdminPage() {

		initDefaultNewAdmin();
		//28765
		if(getManagedBean().getAccType().equals(SupplierManagementConst.ACC_BASIC)){
			getManagedBean().setDisableNationality(disableNationalityDropDown(getManagedBean().getSmSupplier().getSmAppl().getSmPersonnels()));
		}
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/forcereplaceadmin/addNewAdminInfo.xhtml", "", null,
				null);
	}

	/**
	 * Clear entered information on change account type
	 * 
	 * @param e
	 */
	public void onChangeAccType(AjaxBehaviorEvent e) {
		getManagedBean().setReactivateBasic(new ReactivateSupplierVO());
		setDefaultIdentificationType(getManagedBean().getReactivateBasic());
	}

	private void setDefaultIdentificationType(ReactivateSupplierVO vo) {
		PmParameter ic = SmCommonUtil.retrieveParameterByTypeAndCode(
				ParameterTypeConst.IDENTIFICATION_TYPES,
				SupplierManagementConst.NEW_IC, StringUtils.EMPTY);
		vo.setNewIdentificationType(ic.getParameterId());
		
		//#32445 - start
		if (getManagedBean().getReactivateBasic().getNewIdentificationType() != null) {
			getManagedBean().setIdentificationType(
					SmApplicationUtil.retrievePmParameterById(getManagedBean()
							.getReactivateBasic().getNewIdentificationType()));
			getManagedBean().getReactivateBasic().setNewIdentificationNo("");
		}
		//#32445 - end
	}

	/**
	 * Validate my identity
	 * 
	 * @param SmPersonnel
	 *            personnel
	 */
	private void validateMyIdentity() {
		if (getManagedBean().getNewAdmin() != null
				&& getManagedBean().getReactivateBasic() != null
				&& getManagedBean().getReactivateBasic()
						.getNewIdentificationType() != null) {
			PmParameter idType = SmApplicationUtil
					.retrievePmParameterById(getManagedBean()
							.getReactivateBasic().getNewIdentificationType());

			if (idType.getParameterCode()
					.equals(SupplierManagementConst.NEW_IC)) {
				SmCommonUtil.getInstance().verifyWithMyIdentity(
						getManagedBean().getNewAdmin().getName(),
						getManagedBean().getNewAdmin().getIdentificationNo(),
						getManagedBean().getPermAddr().getSmAddress()
								.getPostcode(), null);
			}
		}
	}

	/**
	 * Validate ID format
	 * 
	 * @param identNo
	 * @param identType
	 */
	public void validateIDFormat(String identNo, String identType) {
		if (identType.equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)) {
			if (identNo.length() != 12) {
				throw new EpException(MessageConst.ERROR_721013);
			}

			if (!identNo.matches("[0-9]+")) {
				throw new EpException(MessageConst.ERROR_721013);
			}

		}
	}

	/**
	 * Initialize new admin values
	 */
	private void initDefaultNewAdmin() {
		//#28765 - Start
		initReplaceAdminSupportDoc();
		//#28765 - End
		if (!getManagedBean().isExistingUser()) {
			if (getManagedBean().getNewAdmin() == null) {
				getManagedBean().setNewAdmin(new SmPersonnel());
			}
			setDefaultNewAdminPersonnelValues();
		} else {
			if (getManagedBean().getNewAdmin().getTitle() == null) {
				getManagedBean().getNewAdmin().setTitle(new PmSalutation());
			}
		}
		setDefaultNewAdminAddress();
	}

	/**
	 * Initialize list of identification types
	 */
	private void initIdentificationTypes() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}
		getManagedBean().setIdentificationTypes(new ArrayList<SelectItem>());
		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.IDENTIFICATION_TYPES, null);

		for (SelectItem selectItem : items) {
			if (selectItem.getValue() == null) {
				getManagedBean().getIdentificationTypes().add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					getManagedBean().getIdentificationTypes().add(selectItem);
				}
			}
		}
	}

	/**
	 * Initialize list of country
	 */
	private void initCountries() {
		CountryResDTO resDTO = SmServiceLookup.getCountrySO()
				.retrieveCountryList();
		List<SelectItem> countryList = new ArrayList<SelectItem>();
		SelectItem si = null;
		for (PmCountry country : resDTO.getCountryList()) {
			si = new SelectItem();
			si.setLabel(country.getCountryName());
			si.setValue(country.getCountryId());
			countryList.add(si);
		}
		getManagedBean().setNationalities(countryList);
		getManagedBean().setPermCountries(countryList);
		getManagedBean().setMailCountries(countryList);
	}

	/**
	 * Initialize list of state
	 */
	private void initStates() {
		getManagedBean().setPermStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
		getManagedBean().setMailStates(
				SmCommonUtil.getStateList(getManagedBean().getMyCountryId()));
	}

	/**
	 * Update mail address country, state and district dropdown list
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
	public void updateMailAddLists(String countryId, String stateId, String divisionId, String districtId) { //#34159
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setMailStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailDivisions(null); //#34159
			getManagedBean().setMailDistricts(null);
			getManagedBean().setMailCities(null);
		}
		//#34159 - START
		if (!stateId.isEmpty() || ("null").equalsIgnoreCase(stateId)) {
			if(("14").equals(stateId)){
				getManagedBean().setMailDivisions(SmCommonUtil.getDivisionList(Long.valueOf(stateId)));
				getManagedBean().setMailDistricts(null);
				getManagedBean().setMailCities(null);
			} else {
				getManagedBean().setMailDivisions(null);
				if(getManagedBean().getMailAddr().getSmAddress().getPmDivision() != null){
					getManagedBean().getMailAddr().getSmAddress().getPmDivision().setDivisionId(null);
				}
				getManagedBean().setMailDistricts(SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
				getManagedBean().getMailAddr().getSmAddress().getPmDistrict().setDistrictId(null);
				getManagedBean().setMailCities(null);
			}
		}
		if(!divisionId.isEmpty() || ("null").equalsIgnoreCase(divisionId)){
			getManagedBean().setMailDistricts(SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().setMailCities(null);
		}
		//#34159 - END
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setMailCities(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

	/**
	 * Update permanent address country, state and district dropdown list
	 * 
	 * @param String
	 *            countryId
	 * @param String
	 *            stateId
	 * @param String
	 *            districtId
	 */
	public void updatePermAddLists(String countryId, String stateId, String divisionId, String districtId) { //#34159
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setPermStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setPermDivisions(null); //#34159
			getManagedBean().setPermDistricts(null);
			getManagedBean().setPermCities(null);
		}
		//#34159 - START
		if (!stateId.isEmpty() || ("null").equalsIgnoreCase(stateId)) {
			if(("14").equals(stateId)){
				getManagedBean().setPermDivisions(SmCommonUtil.getDivisionList(Long.valueOf(stateId)));
				getManagedBean().setPermDistricts(null);
				getManagedBean().setPermCities(null);
			} else {
				getManagedBean().setPermDivisions(null);
				if(getManagedBean().getPermAddr().getSmAddress().getPmDivision() != null){
					getManagedBean().getPermAddr().getSmAddress().getPmDivision().setDivisionId(null);
				}
				getManagedBean().setPermDistricts(SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
				getManagedBean().getPermAddr().getSmAddress().getPmDistrict().setDistrictId(null);
				getManagedBean().setPermCities(null);
			}
		}
		if(!divisionId.isEmpty() || ("null").equalsIgnoreCase(divisionId)){
			getManagedBean().setPermDistricts(SmCommonUtil.getDistrictListByID(Long.valueOf(divisionId)));
			getManagedBean().setPermCities(null);
		}
		//#34159 - END
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setPermCities(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}
	
	//#34159 - START
	public void onChangeCountry(SmAddress addr, String addrType){
		Long countryId = addr.getPmCountry().getCountryId();
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)){
			if(countryId != null){
				addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(countryId));
				this.getManagedBean().setPermStates(SmCommonUtil.getStateList(countryId));
			} else {
				this.getManagedBean().setPermStates(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setPermDivisions(new ArrayList<SelectItem>());
			this.getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
			this.getManagedBean().setPermCities(new ArrayList<SelectItem>());
		}
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)){
			if(countryId != null){
				addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(countryId));
				this.getManagedBean().setMailStates(SmCommonUtil.getStateList(countryId));
			} else {
				this.getManagedBean().setMailStates(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setMailDivisions(new ArrayList<SelectItem>());
			this.getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
			this.getManagedBean().setMailCities(new ArrayList<SelectItem>());
		}
		
		addr.setPmState(new PmState());
		addr.setPmDivision(new PmDivision());
		addr.setPmDistrict(new PmDistrict());
		addr.setPmCity(new PmCity());
	}
	
	public void onChangeState(SmAddress addr, String addrType){
		Long stateId = addr.getPmState().getStateId();
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)){
			if(stateId != null){
				addr.setPmState(SmCommonUtil.getInstance().getStateById(stateId));
				if(addr.getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){
					this.getManagedBean().setPermDivisions(SmCommonUtil.getDivisionList(stateId));
					this.getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
				} else {
					this.getManagedBean().setPermDistricts(SmCommonUtil.getDistrictList(stateId));
				}
			} else {
				addr.setPmState(new PmState());
				this.getManagedBean().setPermDivisions(new ArrayList<SelectItem>());
				this.getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setPermCities(new ArrayList<SelectItem>());
		}
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)){
			if(stateId != null){
				addr.setPmState(SmCommonUtil.getInstance().getStateById(stateId));
				if(addr.getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){
					this.getManagedBean().setMailDivisions(SmCommonUtil.getDivisionList(stateId));
					this.getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
				} else {
					this.getManagedBean().setMailDistricts(SmCommonUtil.getDistrictList(stateId));
				}
			} else {
				addr.setPmState(new PmState());
				this.getManagedBean().setMailDivisions(new ArrayList<SelectItem>());
				this.getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setMailCities(new ArrayList<SelectItem>());
		}
		
		addr.setPmDivision(new PmDivision());
		addr.setPmDistrict(new PmDistrict());
		addr.setPmCity(new PmCity());
	}
	
	public void onChangeDivision(SmAddress addr, String addrType){
		Long divisionId = addr.getPmDivision().getDivisionId();
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)){
			if(divisionId != null){
				this.getManagedBean().setPermDistricts(SmCommonUtil.getDistrictListByID(divisionId));
			} else {
				this.getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setPermCities(new ArrayList<SelectItem>());
		}
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)){
			if(divisionId != null){
				this.getManagedBean().setMailDistricts(SmCommonUtil.getDistrictListByID(divisionId));
			} else {
				this.getManagedBean().setMailDistricts(new ArrayList<SelectItem>());
			}
			this.getManagedBean().setMailCities(new ArrayList<SelectItem>());
			
		}
		
		addr.setPmDistrict(new PmDistrict());
		addr.setPmCity(new PmCity());
	}
	
	public void onChangeDistrict(SmAddress addr, String addrType){
		Long districtId = addr.getPmDistrict().getDistrictId();
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)){
			if(districtId != null){
				this.getManagedBean().setPermCities(SmCommonUtil.getCityList(districtId));
			} else {
				this.getManagedBean().setPermCities(new ArrayList<SelectItem>());
			}
		}
		
		if(addrType.equals(SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL)){
			if(districtId != null){
				this.getManagedBean().setMailCities(SmCommonUtil.getCityList(districtId));
			} else {
				this.getManagedBean().setMailCities(new ArrayList<SelectItem>());
			}
		}
		
		addr.setPmCity(new PmCity());
	}
	//#34159 - END

	/**
	 * Set default permanent and mail address to Malaysia
	 */
	private void setDefaultNewAdminAddress() {

		PmCountry countryMy = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		
		SmPersonnelAddress perm = null;
		if (getManagedBean().getNewAdmin() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getNewAdmin().getSmPersonnelAddresses())) {
			perm = (SmPersonnelAddress) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.PERM_ADD, getManagedBean()
									.getNewAdmin().getSmPersonnelAddresses(),
							"addressType");

		}
		if (perm == null) {
			perm = new SmPersonnelAddress();
			perm.setAddressType(SupplierManagementConst.PERM_ADD);
			perm.setSmAddress(SmApplicationUtil.setDefaultAddr(countryMy));
			perm.getSmAddress().setPmDivision(new PmDivision());//#34159
			perm.setSmPersonnel(getManagedBean().getNewAdmin());
			perm.setSameAsPerm(false);
		} else {
			//#34159 - START
			if(perm.getSmAddress().getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){
				getManagedBean().setPermDivisions(SmCommonUtil.getDivisionList(perm.getSmAddress().getPmState().getStateId()));
			} else {
				getManagedBean().setPermDivisions(new ArrayList<SelectItem>());
			}
			if(perm.getSmAddress().getPmDivision() == null){
				perm.getSmAddress().setPmDivision(new PmDivision());
			}
			//#34159 - END
			getManagedBean().setPermDistricts(
					SmCommonUtil.getDistrictList(perm.getSmAddress()
							.getPmState().getStateId()));
			getManagedBean().setPermCities(
					SmCommonUtil.getCityList(perm.getSmAddress()
							.getPmDistrict().getDistrictId()));
		}

		SmPersonnelAddress mail = null;
		if (getManagedBean().getNewAdmin() != null
				&& !EntityHelper.isCollectionEmpty(getManagedBean()
						.getNewAdmin().getSmPersonnelAddresses())) {
			mail = (SmPersonnelAddress) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.MAIL_ADD, getManagedBean()
									.getNewAdmin().getSmPersonnelAddresses(),
							"addressType");
		}
		if (mail == null) {
			mail = new SmPersonnelAddress();
			mail.setAddressType(SupplierManagementConst.MAIL_ADD);
			mail.setSmAddress(SmApplicationUtil.setDefaultAddr(countryMy));
			mail.getSmAddress().setPmDivision(new PmDivision());//#34159
			mail.setSmPersonnel(getManagedBean().getNewAdmin());
			mail.setSameAsPerm(false);
		} else {
			//#34159 - START
			if(mail.getSmAddress().getPmState().getStateCode().equals(SupplierManagementConst.SARAWAK_CODE)){
				getManagedBean().setMailDivisions(SmCommonUtil.getDivisionList(mail.getSmAddress().getPmState().getStateId()));
			} else {
				getManagedBean().setMailDivisions(new ArrayList<SelectItem>());
			}
			if(mail.getSmAddress().getPmDivision() == null){
				mail.getSmAddress().setPmDivision(new PmDivision());
			}
			//#34159 - END
			getManagedBean().setMailDistricts(
					SmCommonUtil.getDistrictList(mail.getSmAddress()
							.getPmState().getStateId()));
			getManagedBean().setMailCities(
					SmCommonUtil.getCityList(mail.getSmAddress()
							.getPmDistrict().getDistrictId()));
		}

		getManagedBean().setPermAddr(perm);
		getManagedBean().setMailAddr(mail);

	}

	/**
	 * Set default new personnel values
	 */
	private void setDefaultNewAdminPersonnelValues() {
		getManagedBean().getNewAdmin().setIdentificationTypeId(
				getManagedBean().getReactivateBasic()
						.getNewIdentificationType());
		getManagedBean().getNewAdmin().setIdentificationType(
				SmCommonUtil.retrieveParameterById(getManagedBean()
						.getNewAdmin().getIdentificationTypeId()));
		getManagedBean().getNewAdmin().setIdentificationNo(
				getManagedBean().getReactivateBasic().getNewIdentificationNo());
		getManagedBean().getNewAdmin().setTitle(new PmSalutation());
		getManagedBean().getNewAdmin().setEpRole(
				getManagedBean().getOldAdmin().getEpRole());
		getManagedBean().getNewAdmin().setSmAppl(
				getManagedBean().getSmSupplier().getSmAppl());
		getManagedBean().getNewAdmin().setRevNo(1);
		getManagedBean().getNewAdmin().setBumiFlg(1);
		getManagedBean().getNewAdmin().setMuslimFlg(1);
		getManagedBean().getNewAdmin().setRecordStatusFlg(
				DbValueConst.RECORD_STATUS_PENDING_ACTIVATION);
		getManagedBean().getNewAdmin().setAuthorizedFlg(
				DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().getNewAdmin().setContractSignerFlg(
				DbValueConst.RECORD_STATUS_ACTIVE);
		getManagedBean().getNewAdmin().setContactPerson(true);
		getManagedBean().getNewAdmin().setDesignation(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setEmail(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setBirthDate(null);
		getManagedBean().getNewAdmin().setPhoneArea(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setPhoneNo(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setMobileArea(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setMobileNo(StringUtils.EMPTY);
		getManagedBean().getNewAdmin().setMobileTelcoId(StringUtils.EMPTY);

		PmCountry country = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		getManagedBean().getNewAdmin().setPmCountry(country);
	}

	/**
	 * Redirect to success page
	 * 
	 * @param PersonnelResDTO
	 *            resDto
	 */
	private void redirectToSuccessPage(PersonnelResDTO resDTO) {
		if (resDTO.isSuccessful()) {
			//#28765 - Start
			String accTypeStr = "";
			if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.basic", null);
			} else if (getManagedBean().getReactivateBasic().getSuppType()!=null && getManagedBean().getReactivateBasic().getSuppType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant",null);
			} else if (getManagedBean().getReactivateBasic().getSuppType()!=null && getManagedBean().getReactivateBasic().getSuppType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor",null);
			} else {getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_GOVT_SELLER);
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
			}
			//#28765 - End
			if (getManagedBean().getNewAdmin().getPmUser() == null) {
				//Before #28765
				//SmCommonUtil.redirectToPage(
				//		SmApplicationUtil.getApplicationErrorPage(),
				//		MessageConst.INFO_721037, new String[] {getManagedBean().getNewAdmin().getEmail(),
				//				SmRuleHelper.getInstance()
				//						.getEmailActivationDays() + "" }, null);
				//#28765 - Start
				if(getManagedBean().getBypassFlag1()==1) {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721041, new String[] {accTypeStr}, null);
				} else {
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721037, new String[] {getManagedBean().getOldAdmin().getName(),
							getManagedBean().getNewAdmin().getName(),getManagedBean().getNewAdmin().getEmail(),
								SmRuleHelper.getInstance()
										.getEmailActivationDays() + "" }, null);
				}
				//#28765 - End
			} else {
				if (getManagedBean().getReactivateBasic().isShowEmailInput()) {
					//Before #28765
					//SmCommonUtil.redirectToPage(
					//		SmApplicationUtil.getApplicationErrorPage(),
					//		MessageConst.INFO_721037, new String[] {
					//				getManagedBean().getNewAdmin().getEmail(),
					//				SmRuleHelper.getInstance()
					//						.getEmailActivationDays() + "" },
					//		null);
					//#28765 - Start
					if(getManagedBean().getBypassFlag1()==1) {
						SmCommonUtil.redirectToPage(
								SmApplicationUtil.getApplicationErrorPage(),
								MessageConst.INFO_721041, new String[] {accTypeStr}, null);
					} else {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721037, new String[] {getManagedBean().getOldAdmin().getName(),
								getManagedBean().getNewAdmin().getName(),
									getManagedBean().getNewAdmin().getEmail(),
									SmRuleHelper.getInstance()
											.getEmailActivationDays() + "" },
							null);
					}
					//#28765 - End
				} else {
					SmCommonUtil.redirectToPage(
							SmApplicationUtil.getApplicationErrorPage(),
							MessageConst.INFO_721036, null, null);
				}
			}
			// #28895_35501 Start - AUDIT TRAIL SM
			IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
			if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
				getManagedBean().setForceReplace(true);
				getManagedBean().setOldIdentification(getManagedBean().getReactivateBasic().getIdentificationNo());
				getManagedBean().setNewIdentifiction(getManagedBean().getReactivateBasic().getNewIdentificationNo());
				AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementAdminInformationBackingBean();
				auditSmBackingBean.saveAuditData(getManagedBean(), null);
			}
			// #28895_35501 End
		} else {
			throw new EpException(MessageConst.ERROR_721113);
		}
	}
	
	//#33589 - Start
	private void redirectToSuccessPageForInProgress(PersonnelResDTO resDTO) {
		if (resDTO.isSuccessful()) {
			String accTypeStr = "";
			if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.basic", null);
			} else if (getManagedBean().getReactivateBasic().getSuppType() != null
					&& getManagedBean().getReactivateBasic().getSuppType()
							.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.consultant", null);
			} else if (getManagedBean().getReactivateBasic().getSuppType() != null
					&& getManagedBean().getReactivateBasic().getSuppType()
							.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)) {
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.mof.contractor", null);
			} else {
				getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_GOVT_SELLER);
				accTypeStr = SmCommonUtil.getMessage("lbl.sm.basic.account.govt", null);
			}

			if (getManagedBean().getNewAdmin() == null) {
				if (getManagedBean().getBypassFlag1() == 1) {
					SmCommonUtil.redirectToPage(SmApplicationUtil.getApplicationErrorPage(), MessageConst.INFO_721041,
							new String[] { accTypeStr }, null);
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_721113);
		}
	}
	//#33589 - End

	/**
	 * Set identification type
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void assignIdentificationType(AjaxBehaviorEvent e) {
		if (getManagedBean().getReactivateBasic().getNewIdentificationType() != null) {
			getManagedBean().setIdentificationType(
					SmApplicationUtil.retrievePmParameterById(getManagedBean()
							.getReactivateBasic().getNewIdentificationType()));
			//#32445 - start
			getManagedBean().getReactivateBasic().setNewIdentificationNo("");
			//#32445 - end
		}
	}

	/**
	 * Redirect to Terms And Condition Details Page
	 */
	public void viewTnc() {
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/forcereplaceadmin/tncDetails.xhtml", "", null, null);
	}

	private List<String> getTaskList() {
		List<String> taskList = new ArrayList<String>();
		if (getManagedBean().getOldAdmin() != null
				&& getManagedBean().getOldAdmin().getPmUser() != null
				&& getManagedBean().getOldAdmin().getPmUser().getUserId() != null) {
			List<WorklistItemVO> items = SmCommonUtil
					.getBpmTaskListByLoginId(getManagedBean().getOldAdmin()
							.getPmUser().getLoginId());
			if (!EntityHelper.isCollectionEmpty(items)) {
				for (WorklistItemVO task : items) {
					taskList.add(task.getTaskId());
				}
			}
		}
		return taskList;
	}

	private SmAppl getCurrActiveSmAppl(SmSupplier smSupplier) {
		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		mofRegistrationReqDTO.setSupplierId(smSupplier.getSupplierId());
		return SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofRegistrationReqDTO).getSmAppl();
	}
	
	// #36716 - Start
	/**
	 * Rechecks the validity of the specified source address.
	 * @param srcAddr
	 * @throws NullPointerException
	 */
	public void recheckVal(SmAddress srcAddr) {
		if (isInvalidAddress(srcAddr)) {
			handleInvalidAddress();
		}
	}
	
	private boolean isInvalidAddress(SmAddress srcAddr) {
	    if (srcAddr == null) {
	        return true;
	    }
	    return isBlank(srcAddr.getAddress1()) || hasInvalidLocation(srcAddr) || isBlank(srcAddr.getPostcode());
	}
	
	private boolean hasInvalidLocation(SmAddress srcAddr) {
	    return isInvalidState(srcAddr) || isInvalidDivision(srcAddr) 
	            || isInvalidDistrict(srcAddr) || isInvalidCity(srcAddr); 
	}

	private boolean isInvalidState(SmAddress srcAddr) {
		return srcAddr.getPmState() == null || srcAddr.getPmState().getStateId() == null;
	}

	private boolean isInvalidDivision(SmAddress srcAddr) {
	    return "14".equalsIgnoreCase(srcAddr.getPmState().getStateId().toString()) 
	            && (srcAddr.getPmDivision() == null 
	                || srcAddr.getPmDivision().getDivisionId() == null);
	}

	private boolean isInvalidDistrict(SmAddress srcAddr) {
		return srcAddr.getPmDistrict() == null || srcAddr.getPmDistrict().getDistrictId() == null;
	}

	private boolean isInvalidCity(SmAddress srcAddr) {
		return srcAddr.getPmCity() == null || srcAddr.getPmCity().getCityId() == null;
	}

	private boolean isBlank(String value) {
		return StringUtils.isBlank(value);
	}

	private void handleInvalidAddress() {
		String addrType = SmCommonUtil.getMessage("lbl.sm.permenantaddress", null);
		getManagedBean().getMailAddr().setSameAsPerm(false);
		throw new EpException(MessageConst.ERROR_722656, new String[] { addrType });
	}
	// #36716 - End
	
	public void copyPermanentAddress(SmAddress refAddress){
		if(this.getManagedBean().getMailAddr().isSameAsPerm()){
			//#36716 - Start
			recheckVal(refAddress);
			//#36716 - End
			if(this.getManagedBean().getMailAddr().getSmAddress() != null){
				if(this.getManagedBean().getMailAddr().getSmAddress() == null){
					this.getManagedBean().getMailAddr().setSmAddress(new SmAddress());
				}
				SmAddress address = this.getManagedBean().getMailAddr().getSmAddress();
				if (refAddress.getAddress1() != null)
					address.setAddress1(new String(refAddress.getAddress1().toString()));
				if (refAddress.getAddress2() != null)
					address.setAddress2(new String(refAddress.getAddress2().toString()));
				if (refAddress.getAddress3() != null)
					address.setAddress3(new String(refAddress.getAddress3().toString()));
				if (refAddress.getPostcode() != null)
					address.setPostcode(new String(refAddress.getPostcode().toString()));

				PmCountry country = new PmCountry();
				PmState state = new PmState();
				//#34159 - START
				PmDivision division = new PmDivision();
				//#34159 - END
				PmDistrict district = new PmDistrict();
				PmCity city = new PmCity();
				
				//#36716 - START
				if (refAddress.getPmCountry().getCountryId() != null) {
					country = SmCommonUtil.getInstance().getCountryById(refAddress.getPmCountry().getCountryId());
				}

				address.setPmCountry(country);
				onChangeCountry(address, "M");
				
				if(refAddress.getPmState().getStateId() != null){
					state = SmCommonUtil.getInstance().getStateById(refAddress.getPmState().getStateId());
				}
				//#36716 - END
				address.setPmState(state);
				
				//#34159 - START
				//#36716 - START
				onChangeState(address, "M");
				
				if(refAddress.getPmState().getStateId()!= null &&("14").equalsIgnoreCase(refAddress.getPmState().getStateId().toString())){
					//#36716 - END
					division.setDivisionId(refAddress.getPmDivision().getDivisionId());
					updateMailAddLists("", "",refAddress.getPmDivision().getDivisionId().toString(), "");
				} else {
					division.setDivisionId(null);
				}
				address.setPmDivision(division);
				
				//#36716 - START
				if(refAddress.getPmDistrict().getDistrictId() != null){
					district = SmCommonUtil.getInstance().getDistrictById(refAddress.getPmDistrict().getDistrictId());
				}
				address.setPmDistrict(district);
				onChangeDistrict(address, "M");
				//#34159 - END
				
				if(refAddress.getPmCity().getCityId() != null){
					city = SmCommonUtil.getInstance().getCityById(refAddress.getPmCity().getCityId());
				}
				//#36716 - END
				address.setPmCity(city);
			}
		}
	}
	
	//#28765 - Start
	public void confirmDeclare(){
		RequestContext.getCurrentInstance().execute("groupDeleteConfirm.show()"); 
	}
	private boolean isExistInSelectedPersonnel(SmPersonnel personnel) {
		boolean exist = false;
		for (SmPersonnel selectedPersonnel : getManagedBean()
				.getReplaceSupplierAdminSelectList()) {
			if (personnel.getIdentificationNo().equals(
					selectedPersonnel.getIdentificationNo())) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
		
			// construct file path
			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/"
					+ "REPLACESUPPADMIN/";

			// init suppDocFile (currently only default one file)
			SmReplaceSupplierAdminDocFile docFile = getManagedBean().getSmReplaceSupplierAdminDocs().get(0).getSmReplacesupplieradminDocFiles().get(0);
			docFile.setSmReplaceSupplierAdminDoc(getManagedBean().getSmReplaceSupplierAdminDocs().get(0));
			//docFile.setFileName(System.currentTimeMillis() + "_");
			docFile.setFileName(StringUtils.replace(event.getFile().getFileName()," ", "_"));
			docFile.setFilePath(filePath);
			getManagedBean().getSmReplaceSupplierAdminDocs().get(0).setPmUser(getManagedBean().getOldAdmin().getPmUser());
			//getManagedBean().setSmReplaceSupplierAdminDocs();
			SmCommonUtil.processUpload(filePath, event,docFile.getFileName());
			RequestContext.getCurrentInstance().execute("uploadDialog.hide()");
		}
	}
	public void deleteSupDocFileAttachment() {
		if (getManagedBean().getSmReplaceSupplierAdminDocs() != null && getManagedBean().getSmReplaceSupplierAdminDocs().size()>0) {

			SmReplaceSupplierAdminDocFile docFile = getManagedBean().getSmReplaceSupplierAdminDocs().get(0).getSmReplacesupplieradminDocFiles().get(0);
			// set empty value to deleted file
			SmCommonUtil.deleteUpload(docFile.getFilePath(), docFile.getFileName());
			docFile.setSmReplaceSupplierAdminDoc(getManagedBean().getSmReplaceSupplierAdminDocs().get(0));		
			docFile.setFileName(StringUtils.EMPTY);
			docFile.setFilePath("");		
		}
	}
	//#28765 - End
	//START 28765 - Replace Supplier Admin
	private void initReplaceAdminSupportDoc() {
		//#28765 - Start
		List<PmDocument>  replaceAdminSuppDocList = new ArrayList<PmDocument>();
		//#28765 - End
		DocumentReqDTO reqDTO = null;					
		Locale userLocale = new FacesScopes().getUserLocale();				
		reqDTO = new DocumentReqDTO();
		reqDTO.setUserLocale(userLocale);
		//#28765 - Start
		replaceAdminSuppDocList = SmServiceLookup.getDocumentSO().retrieveReplaceAdminSuppDocuments(reqDTO).getDocumentList();
		PmDocument document = new PmDocument();
		if(replaceAdminSuppDocList != null && replaceAdminSuppDocList.size()>0){
			document = replaceAdminSuppDocList.get(0);
		}
		SmReplaceSupplierAdminDoc replaceSupplierAdminDoc = new SmReplaceSupplierAdminDoc();
		replaceSupplierAdminDoc.setPmDocument(document);
		SmReplaceSupplierAdminDocFile adminDocFile = new SmReplaceSupplierAdminDocFile();
		replaceSupplierAdminDoc.setSmReplacesupplieradminDocFiles(new ArrayList());
		replaceSupplierAdminDoc.getSmReplacesupplieradminDocFiles().add(adminDocFile);
		log.info("--------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+replaceSupplierAdminDoc.getPmDocument().getDocName());
		getManagedBean().getSmReplaceSupplierAdminDocs().add(replaceSupplierAdminDoc);
	}
	private void personnelBypassValidation() {	
		//from checking appl
		MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
		mofReqDTO.setSupplierId(getManagedBean().getSmSupplier()
				.getSupplierId());		
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofReqDTO).getSmAppl();

		//from validat
		ParameterReqDTO paramReqDTO = new ParameterReqDTO();
		paramReqDTO.setParameterId(getManagedBean().getReactivateBasic()
				.getNewIdentificationType());
		PmParameter idno = SmServiceLookup.getParameterSO()
				.retrieveParameterById(paramReqDTO).getPmParameter();
		if (idno.getParameterCode().equals(SupplierManagementConst.NEW_IC)) {
			if (getManagedBean().getReactivateBasic().getNewIdentificationNo()
					.length() < 12) {
				throw new EpException(MessageConst.ERROR_722532,
						new String[] { getManagedBean().getReactivateBasic()
								.getNewIdentificationNo() });
			}
		}
		if (getManagedBean()
				.getReactivateBasic()
				.getIdentificationNo()
				.equals(getManagedBean().getReactivateBasic()
						.getNewIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_722600);
		}
		if(SmApplicationUtil.isMofAccEffDatePassedCurrDate(getManagedBean().getSmSupplier())){
			if (!getManagedBean().isExistingUser()) {
//#28765 Bypass				throw new EpException(MessageConst.ERROR_721087);
			}
		}
		//SupplierSearchReqDTO ssreqDTO = new SupplierSearchReqDTO();
		//ssreqDTO.setSmSupplier(getManagedBean().getSmSupplier());
		//ssreqDTO.setSmAppl(getManagedBean().getSmSupplier().getSmAppl());
		//SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO()
		//		.retrieveNewSupplierAndPersonnelsByBusRegNoAndName(ssreqDTO);
		//SmAppl appl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(resDTO.getSmSupplier().getSupplierId(),ParameterConst.NEW_SM_APPLN_FOR_MOF);
		SmAppl appl = SmApplicationUtil.getLatesRejectedApplicationBySuppIdAndAppTypeNoExpired(getManagedBean().getSmSupplier().getSupplierId(),ParameterConst.NEW_SM_APPLN_FOR_MOF);
				
		PersonnelReqDTO reqDTO2 = new PersonnelReqDTO();
		reqDTO2.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		List<SmPersonnel> latestActivePersonnels = SmServiceLookup
				.getPersonnelSO().retrieveLatestPersonnelBySupplierId(reqDTO2)
				.getSmPersonnelList();

		if (!SmCommonUtil.isCollectionEmpty(latestActivePersonnels)) {
			for (SmPersonnel smPersonnel : latestActivePersonnels) {
				if (smPersonnel.getIdentificationNo() != null
						&& StringUtils.isNotBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					log.info(smPersonnel.getRecordStatusFlg() + " >>> " + smPersonnel.getIdentificationNo() + " ==========================================");
					if (smPersonnel.getSmAppl() != null) {
						// already mof
						if (StringUtils.isNotBlank(smPersonnel.getSmAppl()
								.getApplType())
								&& !smPersonnel
										.getSmAppl()
										.getApplType()
										.equals(SupplierManagementConst.ACC_BASIC)) {
							// check if already had active mof supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& (smPersonnel.getEpRole().equals(
											RoleCode.MOF_SUPPLIER_USER.value()) || smPersonnel
											.getEpRole().equals(
													RoleCode.FL_USER.value()))
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
						// basic
						else {
							// check if already had active basic supplier user
							// account
							if (StringUtils.isNotBlank(smPersonnel.getEpRole())
									&& (smPersonnel.getEpRole().equals(
											RoleCode.BASIC_SUPPLIER_USER
													.value()) || smPersonnel
											.getEpRole().equals(
													RoleCode.G2G_SUPPLIER_USER
															.value()))
									&& smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
								getManagedBean().setHasSupplierUser(true);
							}
						}
					}
					// set old admin
					if (smPersonnel.getIdentificationNo().trim().equalsIgnoreCase(
							getManagedBean().getReactivateBasic()
									.getIdentificationNo().trim())) {
						getManagedBean().setOldAdmin(smPersonnel);
						log.info("SET OLD ADMIN ==========================================");
					}
					// set new admin
					else if (smPersonnel.getIdentificationNo().trim()
							.equalsIgnoreCase(
									getManagedBean().getReactivateBasic()
											.getNewIdentificationNo().trim())) {
						getManagedBean().setNewAdmin(smPersonnel);
						getManagedBean().setExistingUser(true);
						log.info("SET NEW ADMIN ==========================================");
					}
				}

				// existing deleted personnel
				else if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED
						&& !StringUtils.isBlank(smPersonnel
								.getIdentificationNo())
						&& smPersonnel.getIdentificationNo().equalsIgnoreCase(
								getManagedBean().getReactivateBasic()
										.getNewIdentificationNo())) {
					getManagedBean().setNewAdmin(smPersonnel);
					getManagedBean().setExistingUser(false);
					log.info("EXISTING DELETED PERSONNEL 2 ==========================================");
				}
			}
		}
		// selected old admin not exist
				if (getManagedBean().getOldAdmin() == null) {
					throw new EpException(MessageConst.ERROR_722594);
				} /*else { // disable 28765 -> because
					// [#26050] check if selected old admin does have any pending task - start
					if (getManagedBean().getOldAdmin() != null) {
						//log.info("[#26050] Old Admin login Id: " + getManagedBean().getOldAdmin().getPmUser().getLoginId());
						if (!StringUtils.isBlank(getManagedBean().getOldAdmin().getPmUser().getLoginId())				
								&& PmCommonUtil.isAnyPendingTask(getManagedBean().getOldAdmin().getPmUser().getLoginId())) {
							//#28765 - Start comment this code
							//throw new EpException(MessageConst.ERROR_770167);
							//#28765 - End
						}				
					}
					// [#26050] check if selected old admin does have any pending task - end
				}*/

				// selected old admin does not hold role as supplier admin
				if (getManagedBean().getOldAdmin() != null) {
					if (StringUtils.isBlank(getManagedBean().getOldAdmin().getEpRole())
							|| (getManagedBean().getOldAdmin().getEpRole()
									.equals(RoleCode.BASIC_SUPPLIER_USER.value())
									|| getManagedBean().getOldAdmin().getEpRole()
											.equals(RoleCode.MOF_SUPPLIER_USER.value()) || getManagedBean()
									.getOldAdmin().getEpRole()
									.equals(RoleCode.G2G_SUPPLIER_USER.value()))) {
						throw new EpException(MessageConst.ERROR_722651,
								new String[] { getManagedBean().getOldAdmin()
										.getIdentificationNo() });
					}
				}
		
		SmPersonnel admin = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(
				RoleCode.SUPPLIER_TEMP.value(), getManagedBean().getSmSupplier().getSmAppl().getSmPersonnels(),
				"epRole");
		//28765
		if (	admin!=null && 
				getManagedBean().getAccType().equals(SupplierManagementConst.ACC_MOF) && activeAppl!=null && 
				activeAppl.getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_REGISTERED) && 
				getManagedBean().getSmSupplier().getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE && 
				(
					checkCatalogueUpload(getManagedBean().getSmSupplier().getSupplierId())==null 
					|| 
					checkPendingSoftCertRequest(getManagedBean().getSmSupplier().getSupplierId())
				)) {
			getManagedBean().setBypassFlag1(1);
		} else if (	admin!=null && getManagedBean().getSmSupplier().getSmAppl().getSmCompanyBasic().isWithFederal() &&
				getManagedBean().getAccType().equals(SupplierManagementConst.ACC_MOF) && getManagedBean().getOldAdmin().getPmUser().getUserId()!=null && 
				activeAppl==null && appl==null && (
						checkCatalogueUpload(getManagedBean().getSmSupplier().getSupplierId())==null 
						|| 
						checkPendingSoftCertRequest(getManagedBean().getSmSupplier().getSupplierId())	
				)
			) {
			if(getManagedBean().getNewAdmin() != null && getManagedBean().isExistingUser()
					&& getManagedBean().getNewAdmin().getIdentificationNo().equalsIgnoreCase(getManagedBean().getReactivateBasic().getNewIdentificationNo())){
				log.info("MOF ACCOUNT DOES NOT HAVE ACTIVEAPPL VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
				getManagedBean().setBypassFlag1(1);
			}else{
				getManagedBean().setBypassFlag1(2);
			}
		} else if (	admin!=null && !getManagedBean().getSmSupplier().getSmAppl().getSmCompanyBasic().isWithFederal() &&
				getManagedBean().getAccType().equals(SupplierManagementConst.ACC_MOF) && getManagedBean().getOldAdmin().getPmUser().getUserId()!=null && 
				activeAppl==null && appl==null) {
			if(getManagedBean().getNewAdmin() != null && getManagedBean().isExistingUser()
					&& getManagedBean().getNewAdmin().getIdentificationNo().equalsIgnoreCase(getManagedBean().getReactivateBasic().getNewIdentificationNo())){
				getManagedBean().setBypassFlag1(1);
			}else{
				getManagedBean().setBypassFlag1(2);
			}
		} else if (activeAppl==null && admin!=null && getManagedBean().getAccType().equals(SupplierManagementConst.ACC_MOF) && appl!=null) {
			getManagedBean().setBypassFlag2(1);
		} else if ((   admin!=null &&
				getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER)  
				&& 
				checkPendingSoftCertRequest(getManagedBean().getSmSupplier().getSupplierId())
			) || 
			(	admin!=null && 
				getManagedBean().getAccType().equals(SupplierManagementConst.ACC_BASIC) 
				&& 
				(
					checkCatalogueUpload(getManagedBean().getSmSupplier().getSupplierId())==null
					|| 
					getManagedBean().getOldAdmin().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ
				)
			)) {
				getManagedBean().setBypassFlag1(1);
			}
			
			StringBuilder sb = new StringBuilder();
			if(getManagedBean().getBypassFlag1()==1) {
				sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare1", new String[] {getManagedBean().getSmSupplier().getCompanyName()}));
			} else if(getManagedBean().getBypassFlag2()==1) {
				sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare2", new String[] {
						appl.getApplNo(),
						getManagedBean().getSmSupplier().getCompanyName()}));
			} else {
				sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare4", null)); //#33129 
			}
			getManagedBean().setDisplayMessageReplaceTrans(sb.toString());
			getManagedBean().setSmAppl(appl);
			if(getManagedBean().getBypassFlag1()==1 || getManagedBean().getBypassFlag2()==1) {
				getManagedBean().setBypassFlag(true);
				executeDecision();
			} 		
	}
	private String checkCatalogueUpload(Long supplierId){
		List<CmSupplierItem> suppItems = getCmSupplierItems(supplierId);
		log.info("checkCatalogueUpload Supplier id>>>>>>" + supplierId);
		log.info("checkCatalogueUpload SupplierItems size:>>>>> " + suppItems.size());
		String catalogueStatus = null;
				
		
		if(suppItems.size() > 0){
			for(CmSupplierItem cmSuppItem : suppItems){
				if(cmSuppItem.getItemStatus().equalsIgnoreCase(CATALOGUE_PUBLISHED)){
					catalogueStatus = CATALOGUE_PUBLISHED;
					break;
				}else{
					catalogueStatus = cmSuppItem.getItemStatus();
				}
			}
		}
		
		return catalogueStatus;
	}
	private List<CmSupplierItem> getCmSupplierItems(Long supplierId){
		CatalogueReqDTO reqDTO = new CatalogueReqDTO();
		reqDTO.setSupplierId(supplierId);
		
		ICatalogueSO reqSO = (ICatalogueSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT, ICatalogueSO.class);
		CatalogueResDTO resDTO = reqSO.retrieveSupplierItemBySupplierId(reqDTO);
		
		return resDTO.getSupplierItemList();
	}
	private boolean checkPendingSoftCertRequest(Long supplierId){
		IPersonnelSO personnelSO =  SmServiceLookup.getPersonnelSO();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		
		PersonnelResDTO resDTO = personnelSO.retrievePersonnelBySupplierId(reqDTO); 
		boolean softCertStatus = false;
		
		log.info("resDTO.getSmPersonnelList().size(): "+ resDTO.getSmPersonnelList().size());
		if(resDTO.getSmPersonnelList().size() > 0){
			for(SmPersonnel personnel : resDTO.getSmPersonnelList()){
				if(personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ){
					softCertStatus = true;
					break;
				}
			}
		}
		
		return softCertStatus;
	}
	private boolean selectedOption;
    public void showDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (selectedOption) {
            context.execute("testW.show()");
        } else {
            context.execute("testW.hide()");
        }

    }
    public boolean isSelectedOption() {
        return selectedOption;
    }
    public void setSelectedOption(boolean selectedOption) {
        this.selectedOption = selectedOption;
    }
	//END 28765 - Replace Supplier Admin
    
    public void onChangeEmail()
	{
		String email = this.getManagedBean().getNewAdmin().getEmail().toLowerCase();
		if(email.contains("@yahoo.com") || email.contains("@ymail.com") || email.contains("@rocketmail.com"))
		{
			this.getManagedBean().getNewAdmin().setEmail("");
			RequestContext.getCurrentInstance().execute(
					"emailPopUpWidgetVar.show()");
		}
	} // #28720 - End
    //28765
    public static boolean disableNationalityDropDown(List<SmPersonnel> smPersonnelList) {
		boolean retVal = false;

		// disabled nationality for Basic
		for(SmPersonnel smPersonnel : smPersonnelList){
			if (!StringUtils.isBlank(smPersonnel.getPmCountry().getCountryCode())
					&& (smPersonnel.getPmCountry().getCountryCode().equalsIgnoreCase("MY"))) {
				retVal = true;
			}
		}
		return retVal;
	}

    // -> START #33563
    public void hideShowRoleByBusinessTypeAndSupplierType(String supplierType, String businessType){
    	switch(supplierType){
    	case SupplierManagementConst.SUPP_TYPE_BASIC:
    	case SupplierManagementConst.SUPP_TYPE_CONTRACTOR:
    		switch(businessType){
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK:
    			//director hide
    			//high management hide
    			//semi pro hide
    			//pro hide
    			this.getManagedBean().setDisplayRoleEquity(true);
    			this.getManagedBean().setDisplayRoleDirector(false);
    			this.getManagedBean().setDisplayRoleHighManagement(false);
    			this.getManagedBean().setDisplayRoleManagement(true);
    			this.getManagedBean().setDisplayRoleProfessional(false);
    			this.getManagedBean().setDisplayRoleSemiPro(false);
    			this.getManagedBean().setDisplayRoleClericalStaff(true);
    			break;
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP:
    			//semi pro hide
    			//pro hide
    			this.getManagedBean().setDisplayRoleEquity(true);
    			this.getManagedBean().setDisplayRoleDirector(true);
    			this.getManagedBean().setDisplayRoleHighManagement(true);
    			this.getManagedBean().setDisplayRoleManagement(true);
    			this.getManagedBean().setDisplayRoleProfessional(false);
    			this.getManagedBean().setDisplayRoleSemiPro(false);
    			this.getManagedBean().setDisplayRoleClericalStaff(true);
    			break;
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2:
    			//hide owner
    			//semi pro hide
    			//pro hide
    			this.getManagedBean().setDisplayRoleEquity(false);
    			this.getManagedBean().setDisplayRoleDirector(true);
    			this.getManagedBean().setDisplayRoleHighManagement(true);
    			this.getManagedBean().setDisplayRoleManagement(true);
    			this.getManagedBean().setDisplayRoleProfessional(false);
    			this.getManagedBean().setDisplayRoleSemiPro(false);
    			this.getManagedBean().setDisplayRoleClericalStaff(true);
    			break;
    			default:
    				//all hide
    				this.getManagedBean().setDisplayRoleEquity(false);
        			this.getManagedBean().setDisplayRoleDirector(false);
        			this.getManagedBean().setDisplayRoleHighManagement(false);
        			this.getManagedBean().setDisplayRoleManagement(false);
        			this.getManagedBean().setDisplayRoleProfessional(false);
        			this.getManagedBean().setDisplayRoleSemiPro(false);
        			this.getManagedBean().setDisplayRoleClericalStaff(false);
    				break;
    		}
    		break;
    	case SupplierManagementConst.SUPP_TYPE_CONSULTANT:
    		switch(businessType){
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY:
    			//director hide
    			//high management hide
    			this.getManagedBean().setDisplayRoleEquity(true);
    			this.getManagedBean().setDisplayRoleDirector(false);
    			this.getManagedBean().setDisplayRoleHighManagement(false);
    			this.getManagedBean().setDisplayRoleManagement(true);
    			this.getManagedBean().setDisplayRoleProfessional(true);
    			this.getManagedBean().setDisplayRoleSemiPro(true);
    			this.getManagedBean().setDisplayRoleClericalStaff(true);
    			break;
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD:
    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP:
    			//all show
    			this.getManagedBean().setDisplayRoleEquity(true);
    			this.getManagedBean().setDisplayRoleDirector(true);
    			this.getManagedBean().setDisplayRoleHighManagement(true);
    			this.getManagedBean().setDisplayRoleManagement(true);
    			this.getManagedBean().setDisplayRoleProfessional(true);
    			this.getManagedBean().setDisplayRoleSemiPro(true);
    			this.getManagedBean().setDisplayRoleClericalStaff(true);
    			break;
    			default:
    				this.getManagedBean().setDisplayRoleEquity(false);
        			this.getManagedBean().setDisplayRoleDirector(false);
        			this.getManagedBean().setDisplayRoleHighManagement(false);
        			this.getManagedBean().setDisplayRoleManagement(false);
        			this.getManagedBean().setDisplayRoleProfessional(false);
        			this.getManagedBean().setDisplayRoleSemiPro(false);
        			this.getManagedBean().setDisplayRoleClericalStaff(false);
    				//all hide
    				break;
    		}
    		break;
    		default:
    			//all hide
    			this.getManagedBean().setDisplayRoleEquity(false);
    			this.getManagedBean().setDisplayRoleDirector(false);
    			this.getManagedBean().setDisplayRoleHighManagement(false);
    			this.getManagedBean().setDisplayRoleManagement(false);
    			this.getManagedBean().setDisplayRoleProfessional(false);
    			this.getManagedBean().setDisplayRoleSemiPro(false);
    			this.getManagedBean().setDisplayRoleClericalStaff(false);
    			break;
    	}
    }
    
    public void listenerClickStaffRole(){
    	resetRoleEnableDisable();
    	
    	SmPersonnel currentPersonnel = this.getManagedBean().getNewAdmin();
    	SmPersonnel passPersonnel = this.getManagedBean().getOldAdmin();
    	String supplierType = this.getManagedBean().getSmSupplier().getSmAppl().getSupplierType();
    	String businessType = this.getManagedBean().getSmSupplier().getBusinessType();
    	
//    	if(currentPersonnel != null){
    		switch(supplierType){
        	case SupplierManagementConst.SUPP_TYPE_BASIC:
        	case SupplierManagementConst.SUPP_TYPE_CONTRACTOR:
	    		this.getManagedBean().setDisplayRolePanel(true);
	    		switch(businessType){
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK:
	    			setEnableDisableRoleOneEquityOneStaff(currentPersonnel,passPersonnel);
	    			break;
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK:
	    			setEnableDisableRoleStafOnly(currentPersonnel);
	    			break;
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2:
	    			setEnableDisableRoleStafOnly(currentPersonnel);
	    			break;
	    			default:
	    				resetRoleEnableDisable();
	    				break;
	    		}
	    		break;
	    		
        	case SupplierManagementConst.SUPP_TYPE_CONSULTANT:
	    		this.getManagedBean().setDisplayRolePanel(true);
        		switch(businessType){
        		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK:
	    			//only 1 equity owner can apply role staff
	    			setEnableDisableRoleOneEquityOneStaff(currentPersonnel,passPersonnel);
	    			
	    			break;
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY:
	    			//management and staff can't apply in one - no director & h.management
	    			setEnableDisableRoleStafOnly(currentPersonnel);
	    			break;
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD:
	    		case SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP:
	    		//management and staff can't apply in one - with director & h.management
	    			setEnableDisableRoleStafOnly(currentPersonnel);
	    			break;
	    			default:
	    				//all enable
	    				resetRoleEnableDisable();
	    				break;
        		}
        		break;
        		
	    		default:
	    			//all enable
		    		this.getManagedBean().setDisplayRolePanel(false);
	    			resetRoleEnableDisable();
	    			break;
    		}
//    	} else {
//    		//all enable
//    		resetRoleEnableDisable();
//    	}
    }
    
    public void resetRoleEnableDisable(){
    	this.getManagedBean().setDisabledRoleDirector(false);
    	this.getManagedBean().setDisabledRoleEquity(false);
    	this.getManagedBean().setDisabledRoleHighManagement(false);
    	this.getManagedBean().setDisabledRoleManagement(false);
    	this.getManagedBean().setDisabledRoleProfessional(false);
    	this.getManagedBean().setDisabledRoleSemiPro(false);
    	this.getManagedBean().setDisabledRoleClericalStaff(false);
    }
    
    public void setEnableDisableRoleStafOnly(SmPersonnel currentPersonnel){
    	resetRoleEnableDisable();
    	/*
    	 * RULES
    	 * 1. Role Management & Staff cannot be apply in one profile 
    	 */
		if (currentPersonnel != null) {
			if (currentPersonnel.isDirector() || currentPersonnel.isEquityOwner()
					|| currentPersonnel.isHigherManagement() || currentPersonnel.isManagement()
					|| currentPersonnel.isProf()) {
				this.getManagedBean().setDisabledRoleDirector(false);
				this.getManagedBean().setDisabledRoleEquity(false);
				this.getManagedBean().setDisabledRoleHighManagement(false);
				this.getManagedBean().setDisabledRoleManagement(false);
				this.getManagedBean().setDisabledRoleProfessional(false);
				this.getManagedBean().setDisabledRoleSemiPro(true);
				this.getManagedBean().setDisabledRoleClericalStaff(true);

				this.getManagedBean().getNewAdmin().setSemiProf(false);
				this.getManagedBean().getNewAdmin().setClerical(false);
			} else if (currentPersonnel.isSemiProf()) {
				this.getManagedBean().setDisabledRoleDirector(true);
				this.getManagedBean().setDisabledRoleEquity(true);
				this.getManagedBean().setDisabledRoleHighManagement(true);
				this.getManagedBean().setDisabledRoleManagement(true);
				this.getManagedBean().setDisabledRoleProfessional(true);
				this.getManagedBean().setDisabledRoleSemiPro(false);
				this.getManagedBean().setDisabledRoleClericalStaff(true);

				this.getManagedBean().getNewAdmin().setDirector(false);
				this.getManagedBean().getNewAdmin().setEquityOwner(false);
				this.getManagedBean().getNewAdmin().setHigherManagement(false);
				this.getManagedBean().getNewAdmin().setManagement(false);
				this.getManagedBean().getNewAdmin().setProf(false);
				this.getManagedBean().getNewAdmin().setClerical(false);
			} else if (currentPersonnel.isClerical()) {
				this.getManagedBean().setDisabledRoleDirector(true);
				this.getManagedBean().setDisabledRoleEquity(true);
				this.getManagedBean().setDisabledRoleHighManagement(true);
				this.getManagedBean().setDisabledRoleManagement(true);
				this.getManagedBean().setDisabledRoleProfessional(false);
				this.getManagedBean().setDisabledRoleSemiPro(true);
				this.getManagedBean().setDisabledRoleClericalStaff(false);

				this.getManagedBean().getNewAdmin().setDirector(false);
				this.getManagedBean().getNewAdmin().setEquityOwner(false);
				this.getManagedBean().getNewAdmin().setHigherManagement(false);
				this.getManagedBean().getNewAdmin().setManagement(false);
				this.getManagedBean().getNewAdmin().setSemiProf(false);
			}
		}
    }
    
    public void setEnableDisableRoleOneEquityOneStaff(SmPersonnel currentPersonnel, SmPersonnel passPersonnel){
    	/*
    	 * RULES
    	 * 1. Only 1 of Equity Owner can apply role Staff
    	 * 2. All Equity can apply role Management
    	 * 3. Role Management & Staff cannot be apply in one profile without Equity Owner 
    	 */
    	resetRoleEnableDisable();
		if (currentPersonnel != null) {
			int count = 0;
			for (SmPersonnel smPersonnel : this.getManagedBean().getSmSupplier().getSmAppl().getSmPersonnels()) {
				if (currentPersonnel.getIdentificationNo() == smPersonnel.getIdentificationNo()
						|| passPersonnel.getIdentificationNo() == smPersonnel.getIdentificationNo()) {
					continue;
				} else {
					if (smPersonnel.isEquityOwner() && smPersonnel.isClerical()) {
						count++;
					}
				}
			}

			if (currentPersonnel.isEquityOwner()) {
				if (count > 0) {
					this.getManagedBean().setDisabledRoleDirector(false);
					this.getManagedBean().setDisabledRoleEquity(false);
					this.getManagedBean().setDisabledRoleHighManagement(false);
					this.getManagedBean().setDisabledRoleManagement(false);
					this.getManagedBean().setDisabledRoleProfessional(false);
					this.getManagedBean().setDisabledRoleSemiPro(true);
					this.getManagedBean().setDisabledRoleClericalStaff(true);

					this.getManagedBean().getNewAdmin().setSemiProf(false);
					this.getManagedBean().getNewAdmin().setClerical(false);
				} else {
					this.getManagedBean().setDisabledRoleDirector(false);
					this.getManagedBean().setDisabledRoleEquity(false);
					this.getManagedBean().setDisabledRoleHighManagement(false);
					this.getManagedBean().setDisabledRoleManagement(false);
					this.getManagedBean().setDisabledRoleProfessional(false);
					this.getManagedBean().setDisabledRoleSemiPro(true);
					this.getManagedBean().setDisabledRoleClericalStaff(false);

					this.getManagedBean().getNewAdmin().setSemiProf(false);
				}
			} else if (currentPersonnel.isHigherManagement() || currentPersonnel.isManagement()) {
				this.getManagedBean().setDisabledRoleDirector(false);
				this.getManagedBean().setDisabledRoleEquity(false);
				this.getManagedBean().setDisabledRoleHighManagement(false);
				this.getManagedBean().setDisabledRoleManagement(false);
				this.getManagedBean().setDisabledRoleProfessional(false);
				this.getManagedBean().setDisabledRoleSemiPro(true);
				this.getManagedBean().setDisabledRoleClericalStaff(true);

				this.getManagedBean().getNewAdmin().setSemiProf(false);
				this.getManagedBean().getNewAdmin().setClerical(false);
			} else if (currentPersonnel.isClerical()) {
				if (count > 0) {
					this.getManagedBean().setDisabledRoleDirector(false);
					this.getManagedBean().setDisabledRoleEquity(true);
					this.getManagedBean().setDisabledRoleHighManagement(true);
					this.getManagedBean().setDisabledRoleManagement(true);
					this.getManagedBean().setDisabledRoleProfessional(false);
					this.getManagedBean().setDisabledRoleSemiPro(true);
					this.getManagedBean().setDisabledRoleClericalStaff(false);

					this.getManagedBean().getNewAdmin().setEquityOwner(false);
					this.getManagedBean().getNewAdmin().setSemiProf(false);
					this.getManagedBean().getNewAdmin().setHigherManagement(false);
					this.getManagedBean().getNewAdmin().setManagement(false);
				} else {
					this.getManagedBean().setDisabledRoleDirector(false);
					this.getManagedBean().setDisabledRoleEquity(false);
					this.getManagedBean().setDisabledRoleHighManagement(true);
					this.getManagedBean().setDisabledRoleManagement(true);
					this.getManagedBean().setDisabledRoleProfessional(false);
					this.getManagedBean().setDisabledRoleSemiPro(true);
					this.getManagedBean().setDisabledRoleClericalStaff(false);

					this.getManagedBean().getNewAdmin().setSemiProf(false);
					this.getManagedBean().getNewAdmin().setHigherManagement(false);
					this.getManagedBean().getNewAdmin().setManagement(false);
				}

			} else if (currentPersonnel.isProf()) {
				this.getManagedBean().setDisabledRoleDirector(false);
				this.getManagedBean().setDisabledRoleEquity(false);
				this.getManagedBean().setDisabledRoleHighManagement(false);
				this.getManagedBean().setDisabledRoleManagement(false);
				this.getManagedBean().setDisabledRoleProfessional(false);
				this.getManagedBean().setDisabledRoleSemiPro(true);
				this.getManagedBean().setDisabledRoleClericalStaff(false);

				this.getManagedBean().getNewAdmin().setSemiProf(false);

			} else if (currentPersonnel.isSemiProf()) {
				this.getManagedBean().setDisabledRoleDirector(true);
				this.getManagedBean().setDisabledRoleEquity(true);
				this.getManagedBean().setDisabledRoleHighManagement(true);
				this.getManagedBean().setDisabledRoleManagement(true);
				this.getManagedBean().setDisabledRoleProfessional(true);
				this.getManagedBean().setDisabledRoleSemiPro(false);
				this.getManagedBean().setDisabledRoleClericalStaff(true);

				this.getManagedBean().getNewAdmin().setDirector(false);
				this.getManagedBean().getNewAdmin().setEquityOwner(false);
				this.getManagedBean().getNewAdmin().setHigherManagement(false);
				this.getManagedBean().getNewAdmin().setManagement(false);
				this.getManagedBean().getNewAdmin().setProf(false);
				this.getManagedBean().getNewAdmin().setClerical(false);
			}
		}
    }
    
    public void validateRole(){
    	if(this.getManagedBean().getNewAdmin() != null){
    		switch(this.getManagedBean().getSmSupplier().getSmAppl().getSupplierType()){
    		case SupplierManagementConst.SUPP_TYPE_BASIC:
    		case SupplierManagementConst.SUPP_TYPE_CONSULTANT:
    		case SupplierManagementConst.SUPP_TYPE_CONTRACTOR:
    			if(!this.getManagedBean().getNewAdmin().isEquityOwner()
        				&& !this.getManagedBean().getNewAdmin().isDirector()
        				&& !this.getManagedBean().getNewAdmin().isHigherManagement()
        				&& !this.getManagedBean().getNewAdmin().isManagement()
        				&& !this.getManagedBean().getNewAdmin().isProf()
        				&& !this.getManagedBean().getNewAdmin().isSemiProf()
        				&& !this.getManagedBean().getNewAdmin().isClerical()){
        			throw new EpException(MessageConst.ERROR_722535);
        		}
    		break;
    		default:
    			break;
    		}
    		
    	}
    }
    // END -> #33563
}