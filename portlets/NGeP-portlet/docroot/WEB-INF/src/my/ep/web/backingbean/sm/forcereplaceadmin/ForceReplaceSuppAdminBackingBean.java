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
import my.ep.dto.sm.search.SupplierSearchReqDTO;
//#28765 - End
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
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

	/**
	 * Pre - render and redirect to Force Replace Supplier Admin page
	 */
	public void preRenderForceReplaceAdmin() {
		renewSesionObject();
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		getManagedBean().setPermStates(new ArrayList<SelectItem>());
		getManagedBean().setPermDistricts(new ArrayList<SelectItem>());
		getManagedBean().setPermCities(new ArrayList<SelectItem>());
		getManagedBean().setMailStates(new ArrayList<SelectItem>());
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
				// Agency name does not exist for G2G
				throw new EpException(MessageConst.ERROR_722659);
			} else {
				// Buss Reg No. and Established Date does not exist for Supplier
				throw new EpException(MessageConst.ERROR_722593);
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
				if(getManagedBean().getOldAdmin() != null){
					sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare4", new String[] {getManagedBean().getOldAdmin().getName()})); //#28765
				}else if(getManagedBean().getSmSupplier().getSmPersonnel() != null){
					sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare4", new String[] {getManagedBean().getSmSupplier().getSmPersonnel().getName()})); //#28765
				}
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
		RequestContext.getCurrentInstance().execute("declareDeletePopUp.show()");
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
			reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
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

		getManagedBean().getNewAdmin().setSoftcertFlg(
				SupplierManagementConst.SOFT_CERT_PENDING_REQ);
		SmCommonUtil.setNewPersonnelPermAndMailAddress(getManagedBean()
				.getNewAdmin(), getManagedBean().getMailAddr(),
				getManagedBean().getPermAddr());

		validateMyIdentity();
		PersonnelResDTO resDto = SmServiceLookup.getPersonnelSO()
				.replaceAdminWithNewPersonnel(getReqDTO());
		// Start 28765 comment changes 12981
		//START 12981
		//#28765 - Start - to pass through inactiveSupplierRole for G2G normal replace supplier admin	
		if((getManagedBean().isBypassFlag()==false && !getManagedBean().getAccType().equals(SupplierManagementConst.ACC_GOVT_SELLER)) 
//				&& resDto.isTerminateSupplier() == false
				) {
			// End 28765 comment changes 12981
			PersonnelResDTO resDto2 = SmServiceLookup.getPersonnelSO()
					.inactiveSupplierRole(getOldAdminReqDTO());
		} else if (getManagedBean().getAccType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) && 
				(getManagedBean().getSmAppl().getPmStatus().getStatusId().toString().equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER) ||
				getManagedBean().getSmAppl().getPmStatus().getStatusId().toString().equalsIgnoreCase(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)) ){
			getManagedBean().setBypassFlag1(1);
		}
		//#28765 - End
		redirectToSuccessPage(resDto);
		//END 12981
	}

	/**
	 * Replace admin with existing personnel and redirect to success page
	 */
	public void replaceWithExistingUser() {
		
		//bug #26798, #25969
		SmCommonUtil.setNewPersonnelPermAndMailAddress(getManagedBean()
				.getNewAdmin(), getManagedBean().getMailAddr(),
				getManagedBean().getPermAddr());
		validateMyIdentity();
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
				}else {
					throw new EpException(MessageConst.ERROR_722676);
				}
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
		reqDTO.setSmPersonnel(getManagedBean().getNewAdmin());
		reqDTO.setEmail(getManagedBean().getNewAdmin().getEmail());
		//#28765 - Start chained method added .getApplId()
		reqDTO.setApplId(getManagedBean().getSmSupplier().getSmAppl()
				.getApplId());	
		//#28765 - End
		FacesScopes fs = new FacesScopes();
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setOldAdminRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
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
		
		listenerChangeRole();
	}

	/**
	 * Execute based on decision for existing personnel call terms and condition
	 * dialog pop up and for new personnel redirect to new personnel info page
	 */
	private void executeDecision() {
		// replace with new personnel
		if (!getManagedBean().isExistingUser()) {
			RequestContext.getCurrentInstance().execute(
					"replaceNewAdminConfirm.show()");
		}
		// replace with existing personnel
		else {
			setShowOrHideEmail();
			initDefaultNewAdmin();
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/forcereplaceadmin/addNewAdminInfo.xhtml", "",
					null, null);
		}
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
	public void updateMailAddLists(String countryId, String stateId,
			String districtId) {
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setMailStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setMailDistricts(null);
			getManagedBean().setMailCities(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setMailDistricts(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setMailCities(null);
		}
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
	public void updatePermAddLists(String countryId, String stateId,
			String districtId) {
		if (!countryId.isEmpty() || countryId.equalsIgnoreCase("null")) {
			getManagedBean().setPermStates(
					SmCommonUtil.getStateList(Long.valueOf(countryId)));
			getManagedBean().setPermDistricts(null);
			getManagedBean().setPermCities(null);
		}
		if (!stateId.isEmpty() || stateId.equalsIgnoreCase("null")) {
			getManagedBean().setPermDistricts(
					SmCommonUtil.getDistrictList(Long.valueOf(stateId)));
			getManagedBean().setPermCities(null);
		}
		if (!districtId.isEmpty() || districtId.equalsIgnoreCase("null")) {
			getManagedBean().setPermCities(
					SmCommonUtil.getCityList(Long.valueOf(districtId)));
		}
	}

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
			perm.setSmPersonnel(getManagedBean().getNewAdmin());
			perm.setSameAsPerm(false);
		} else {
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
			mail.setSmPersonnel(getManagedBean().getNewAdmin());
			mail.setSameAsPerm(false);
		} else {
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
		} else {
			throw new EpException(MessageConst.ERROR_721113);
		}
	}

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
	
	public void copyPermanentAddress(SmAddress refAddress){
		if(this.getManagedBean().getMailAddr().isSameAsPerm()){
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
				PmDistrict district = new PmDistrict();
				PmCity city = new PmCity();

				country.setCountryId(refAddress.getPmCountry().getCountryId());
				address.setPmCountry(country);
				
				state.setStateId(refAddress.getPmState().getStateId());
				address.setPmState(state);
				
				updateMailAddLists("", refAddress.getPmState().getStateId().toString(),"");
				district.setDistrictId(refAddress.getPmDistrict().getDistrictId());
				address.setPmDistrict(district);
				updateMailAddLists("", "",refAddress.getPmDistrict().getDistrictId().toString());
				city.setCityId(refAddress.getPmCity().getCityId());
				address.setPmCity(city);
			}
		}
	}
	
	//16052019 - SSM INTEGRATION CR
	public void listenerChangeRole(){
		if(this.getManagedBean().getNewAdmin() !=null){
			if(this.getManagedBean().getNewAdmin().isEquityOwner()){
				this.getManagedBean().setDisableStaff(true);
				this.getManagedBean().getNewAdmin().setClerical(false);
			}else if(!this.getManagedBean().getNewAdmin().isEquityOwner()){
				this.getManagedBean().setDisableStaff(false);
				this.getManagedBean().setDisableEquity(false);				
			}else if(this.getManagedBean().getNewAdmin().isClerical()){
				this.getManagedBean().setDisableStaff(false);
				this.getManagedBean().setDisableEquity(false);
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
		listenerChangeRole();
		
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
				sb.append(SmCommonUtil.getMessage("lbl.msg.confirm.declare4", new String[] {getManagedBean().getOldAdmin().getName()}));  //#28765
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
}