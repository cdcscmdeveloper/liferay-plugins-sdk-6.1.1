package my.ep.web.backingbean.sm.updatepersonnel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.userprofilemaintenance.SmPersonnelBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.updatepersonnel.UpdatePersonnelManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "updatePersonnelBackingBean")
@RequestScoped
public class UpdatePersonnelBackingBean extends
		BaseBackingBean<UpdatePersonnelManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267823583927057585L;
	private static Logger log = Logger.getLogger(SmPersonnelBackingBean.class);

	public void preRenderUpdatePersonnel() {
		renewSesionObject();
		SmCommonUtil.setMDCUserId();
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveLatestSmApplWithChildBySupplierId(reqDTO)
						.getSmAppl());
		init();
		getManagedBean().setUpdatePersonnelPage(true);
		SmCommonUtil.redirectToPage("updatePersonnelBasePage.xhtml", "", null,
				null);
	}

	/**
	 * To redirect from personnel information page the main screen when click
	 * Back.
	 */
	public void backFromPersonnelPage() {
		getManagedBean().setRedirectToPersonPage(false);
		getManagedBean().setValidSmUserLimit(false);
		init();
		if (SmApplicationUtil.getCurrAuthPersonnelCount(getManagedBean()) >= SmRuleHelper
				.getInstance().getMaxAdditionalMofAuthorizedUser()) {
			getManagedBean().setValidSmUserLimit(true);
		}
		RequestContext.getCurrentInstance().reset(":form:shareHolderDialog");
	}

	/**
	 * Add or create a new SmPersonnel bank object
	 * 
	 * @param personelType
	 *            - the personel type to define equity or not
	 */
	public void assignNewShareHolder() {
		SmApplicationUtil.initNewSmPersonnel(getManagedBean(), 0);

	}

	/**
	 * Builds the role list.
	 */
	public void buildRoleList(SmPersonnel personnel) {
		getManagedBean().setePRoleList(
				SmApplicationUtil.getPersonnelRoleItems(personnel,
						getManagedBean().getSmAppl(), getManagedBean()
								.getAuthVO()));
	}

	/**
	 * Remove un selected admin from the selected personnel list.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void unSelectAdmin(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.removePersonnelAdminOrSoftcertProcessFromList(getManagedBean()
						.getSelectedShareHolders());
	}

	/**
	 * To check whether need to disable ep role drop down
	 * 
	 * @param smPersonnel
	 * @return boolean
	 */
	public boolean disableEpRoleDropDown(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.disableEpRoleDropDown(smPersonnel,
				getManagedBean().getPrevApprovedPersonnels());
		return retVal;
	}

	/**
	 * Check whether need to disable authorized radio button
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return boolean
	 */
	public boolean disableAuthorizedRadio(SmPersonnel personnel) {
		boolean retVal = false;
		retVal = SmApplicationUtil.isDisableAuthorizedRadio(personnel,
				getManagedBean().isValidSmUserLimit());
		getManagedBean().setDisableAuthorizedRadio(retVal);
		return retVal;
	}

	/**
	 * Checks if is personnel view only.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object to check
	 * @return true, if is personnel view only
	 */
	public boolean isPersonnelViewOnly(SmPersonnel smPersonnel) {
		boolean retVal = SmApplicationUtil.isPersonnelViewOnly(smPersonnel);
		return retVal;
	}

	/**
	 * Gets the my identity status description based on the response code.
	 * 
	 * @param identityResponseCode
	 *            - the identity response code
	 * @return String - MY Identity status description value
	 */
	public String getMyIdentityStatusDescription(String identityResponseCode) {
		return SmCommonUtil.getInstance().getMyIdentityStatusDescription(
				identityResponseCode);
	}

	/**
	 * Delete or remove selected personnel from the SmPeronnel list of the
	 * application.
	 * 
	 * @param personType
	 *            - the person type
	 */
	public void deleteSelectedShareHolders(String personType,
			boolean confirmDelete) {
		int countDeleteAuthPersonnel = SmApplicationUtil
				.validateDeletePersonnel(personType, getManagedBean());

		if (!confirmDelete && countDeleteAuthPersonnel > 0) {
			RequestContext.getCurrentInstance().execute(
					"deleteAuthPersConfirmDialog.show()");
		} else {
			if (getManagedBean().getSelectedShareHolders().size() > 0) {
				SmApplicationUtil.getInstance().setApplAddressTransientValue(
						getManagedBean().getSmAppl(), false);
				SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(
						getManagedBean().getSmAppl(), getManagedBean()
								.getPrevApprovedPersonnels(), getManagedBean()
								.isDisAllowOwnerEdit());
				SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
						.getSmAppl());
				getManagedBean().getSelectedShareHolders().clear();
			} else {
				throw new EpException(MessageConst.ERROR_722534,
						new String[] { SmCommonUtil.getMessage(
								"lbl.sm.personnel", null) });
			}
		}

	}

	/**
	 * Manage personnel roles for the selected SmPersonnel in the application.
	 */
	public void managePersonnelRoles(String updateAddressDetails) {
		SmApplicationUtil.managePersonnelRoles(getManagedBean(),
				updateAddressDetails);
	}

	/**
	 * Assign selected SmPersonnel object to managed bean for editing the
	 * information.
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		getManagedBean()
				.setSelectedShareholderIdentificationType(
						SmCommonUtil
								.getParamDescriptionByLanguageAndPmParameter(shareHolderBean
										.getIdentificationType()));
		SmApplicationUtil.assignSelShareHolder(shareHolderBean,
				getManagedBean(), 0);

	}

	/**
	 * Submit personnel maintenance and regenerate virtual certificate
	 */
	public void save() {

		SmPersonnel prevPersonnel = (SmPersonnel) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSelectedShareHolder().getIdentificationNo(),
						getManagedBean().getPrevApprovedPersonnels(),
						"identificationNo");

		if (getManagedBean().isValidSmUserLimit()
				&& getManagedBean().getSelectedShareHolder().getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE
				&& prevPersonnel != null
				&& prevPersonnel.getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
			throw new EpException(MessageConst.ERROR_722646);
		}

		if (getManagedBean().isDisableRoleSelection()
				&& prevPersonnel != null
				&& !prevPersonnel.getEpRole().equals(
						getManagedBean().getSelectedShareHolder().getEpRole())) {
			throw new EpException(MessageConst.ERROR_722647);
		}

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		reqDTO.setAppl(getManagedBean().getSmAppl());

		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.submitPersonnelMaintenance(reqDTO);
		if (resDTO != null && resDTO.getSmPersonnel() != null) {
			getManagedBean().setSelectedShareHolder(resDTO.getSmPersonnel());
			getManagedBean().setSmAppl(resDTO.getSmAppl());
		}
		try {
			SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(
					getManagedBean().getSmAppl().getApplId(), false);
			SmCommonUtil.showFacesMsg(null, FacesMessage.SEVERITY_INFO,
					MessageUtils.getMessage(MessageConst.INFO_721226,
							new String[] { getManagedBean()
									.getSelectedShareHolder().getName() },
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);
		} catch (Exception e) {
			log.debug(">>>>>>>>>FAILED TO GENERATE VIRTUAL CERT REPORT:"
					+ e.getMessage() + "<<<<<<<<<<");
		}
	}

	/**
	 * Confirmation on deleting authorized personnel
	 */
	public void confirmDeleteAuthorizedPersonnel() {
		deleteSelectedShareHolders(getManagedBean().getPersonType(), true);
	}

	/**
	 * Assign selected job function - SmStaffExperience object to managed bean
	 * for editing the information.
	 * 
	 * @param exp
	 *            - SmStaffExperience object
	 */
	public void assignSelShrHldrJobFunc(SmStaffExperience exp) {
		getManagedBean().setShowPersonnelViewOnly(true);
		SmApplicationUtil.assignSelShrHldrJobFunc(exp, getManagedBean());
	}

	/**
	 * Add or create a new sm staff experience object
	 */
	public void addShrHldrJobFunction() {
		SmApplicationUtil.addNewPersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				new SmStaffExperience(), "selectedJobFunction",
				"equiJobFunctionInfo");
	}

	/**
	 * Delete or remove selected job functions from the SmStaffExperience list
	 * of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrJobFunction() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrJobFunctions", "smStaffExperiences",
				MessageConst.ERROR_722534,
				new String[] { SmCommonUtil.getMessage(
						"lbl.sm.personnel.dash.jobfunction", null) });

	}

	/**
	 * Cancel add or update functionality and close the job function information
	 * dialog
	 */
	public void cancelJobFunctions() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:equiJobFunctionInfo");
		RequestContext.getCurrentInstance().execute(
				"equiJobFunctionInfo.hide()");
	}

	/**
	 * Add or update selected job function info to SmStaffExperience list of the
	 * selected SmPersonnel.
	 */
	public void addOrUpdateJobFunctions() {
		SmApplicationUtil.addOrUpdateJobFunctions(getManagedBean());
	}

	/**
	 * Add or create a new related company object
	 */
	public void addShrHldrRelatedCompany() {
		SmApplicationUtil.addNewPersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				new SmRelatedCompany(), "selectedRelatedCompany",
				"equiRelCompInfo");
	}

	/**
	 * Add or create a new shr hldr prof body object
	 */
	public void addShrHldrProfBody() {
		SmAssociationBody assBody = new SmAssociationBody();
		assBody.setPmCertBody(new PmCertBody());
		assBody.setRevNo(1);
		assBody.setSmAppl(getManagedBean().getSmAppl());
		assBody.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStfPrfBdy(assBody);
		SmApplicationUtil.assignValReqdToTrue(getManagedBean());
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.show()");
	}

	/**
	 * Assign selected related company - SmRelatedCompany object to managed bean
	 * for editing the information.
	 * 
	 * @param com
	 *            -SmRelatedCompany object
	 */
	public void assignSelShrHldrRelatedCompany(SmRelatedCompany com) {
		getManagedBean().setShowPersonnelViewOnly(true);
		SmApplicationUtil.assignSelShrHldrRelatedCompany(com, getManagedBean());
	}

	/**
	 * Add or update selected related company info to SmRelatedCompany list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateRelatedCompanies() {
		SmApplicationUtil.addOrUpdateRelatedCompanies(getManagedBean());
	}

	/**
	 * Delete or remove selected related companies from the SmRelatedCompany
	 * list of the selected SmPersonnel
	 */
	public void deleteSelectedShrHldrRelatedCompanies() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrRelatedCompanies", "smRelatedCompanies",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.relatedcompany",
								null) });
	}

	/**
	 * Cancel add or update functionality and close the related company
	 * information dialog
	 */
	public void cancelShrHldrRelatedCompanies() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:tt:equiRelCompInfo");
		RequestContext.getCurrentInstance().execute("equiRelCompInfo.hide()");
	}

	/**
	 * Assign selected professional body - SmAssocication object to managed bean
	 * for editing the information.
	 * 
	 * @param asb
	 *            -SmAssociationBody object
	 */
	public void assignSelShrHldrProfBody(SmAssociationBody asb) {
		getManagedBean().setShowPersonnelViewOnly(true);
		SmApplicationUtil.assignSelShrHldrProfBody(asb, getManagedBean());
	}

	/**
	 * Delete or remove selected prof bodies from the SmAssocationBody list of
	 * the selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrProfBodies() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrProfBodies", "smAssociationBodies",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.profbody", null) });
	}

	/**
	 * Cancel add or update functionality and close the professional body
	 * information dialog
	 */
	public void cancelShrHldrProfBody() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:equiShrProfBeanInfo");
		RequestContext.getCurrentInstance().execute(
				"equiShrProfBeanInfo.hide()");
	}

	/**
	 * Load cert body from DB based on selected id and set to PmCertBody object
	 */
	public void onChangeProfBody() {
		CertBodyReqDTO reqDTO = new CertBodyReqDTO();
		reqDTO.setCertBodyId(getManagedBean().getSelectedStfPrfBdy()
				.getPmCertBody().getCertBodyId());
		PmCertBody selCertBody = SmServiceLookup.getCertBodySO()
				.retrievePmCertBodyDtlById(reqDTO).getCertBody();
		getManagedBean().getSelectedStfPrfBdy().setPmCertBody(selCertBody);
	}

	/**
	 * Assign selected staff academics - SmStaffAcademic object to managed bean
	 * for editing the information.
	 * 
	 * @param aca
	 *            -SmStaffAcademic
	 */
	public void assignSelShrHldrAcademics(SmStaffAcademic aca) {
		getManagedBean().setShowPersonnelViewOnly(true);
		SmApplicationUtil.assignSelShrHldrAcademics(aca, getManagedBean());
	}

	/**
	 * Add or create a new staff academics object for selected SmPersonnel
	 */
	public void addShrHldrAcademicQualification() {
		SmStaffAcademic aca = new SmStaffAcademic();
		aca.setPmEduField(new PmEduField());
		// aca.setPmEduLevel(new PmEduLevel());
		aca.setQualificationId(new PmParameter());
		aca.setSmPersonnel(getManagedBean().getSelectedShareHolder());
		getManagedBean().setSelectedStaffAcademic(aca);
		SmApplicationUtil.assignValReqdToTrue(getManagedBean());
		RequestContext.getCurrentInstance().execute("equiAcadInfo.show()");
	}

	/**
	 * Add or update selected staff academics info to SmStaffAcademic list of
	 * the selected SmPersonnel.
	 */
	public void addOrUpdateShrHldrAcademic() {
		SmApplicationUtil.addOrUpdateShrHldrAcademic(getManagedBean());
	}

	/**
	 * Delete or remove selected records from the SmStaffAcademics list of the
	 * selected SmPersonnel.
	 */
	public void deleteSelectedShrHldrAcademics() {
		SmApplicationUtil.deletePersonnelChildObject(getManagedBean(),
				getManagedBean().getSelectedShareHolder(),
				"selectedShrHldrAcademics", "smStaffAcademics",
				MessageConst.ERROR_722534, new String[] { SmCommonUtil
						.getMessage("lbl.sm.personnel.dash.academics", null) });
	}

	/**
	 * Cancel add or update functionality and close the staff academics
	 * information dialog
	 */
	public void cancelShrHldrAcademic() {
		assignValReqdToFalse();
		RequestContext.getCurrentInstance().reset(":form:equiAcadInfo");
		RequestContext.getCurrentInstance().execute("equiAcadInfo.hide()");
	}

	/**
	 * Get list of academic qualification
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getAcademicQualifications() {
		return SmApplicationUtil.getQualificationLevels(getManagedBean());
	}

	/**
	 * Get education level from session list by eduLvlId
	 * 
	 * @param Long
	 *            eduLvlId
	 * @return String
	 */
	public String getEduLvlByIdFromList(Long eduLvlId) {
		String retVal = "";
		if (!SmCommonUtil.isCollectionEmpty(getManagedBean().getEduLevelList())
				&& eduLvlId != null) {
			for (PmParameterDesc eduLvl : getManagedBean().getEduLevelList()) {
				if (eduLvl.getParameterDescId().compareTo(eduLvlId) == 0) {
					retVal = eduLvl.getCodeName();
				}
			}
		}
		return retVal;
	}

	/**
	 * Set address fields whether need to be mandatory
	 */
	public void setRoleStaffAddrReqd() {
		SmApplicationUtil.setRoleStaffAddrReqd(getManagedBean());
	}

	/**
	 * Retrieve identity type from PM.
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPmIdTypeDropdownValues() {
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		Map<Long, Object> identityType = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			identityType.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);

		for (SelectItem selectItem : items) {

			if (selectItem.getValue() == null) {
				itemList.add(selectItem);
			} else {
				PmParameter pmParameter = (PmParameter) identityType
						.get(selectItem.getValue());
				if (!pmParameter.getParameterCode().equals("A")) {
					itemList.add(selectItem);
				}
			}

		}

		return itemList.toArray(new SelectItem[itemList.size()]);

	}

	/**
	 * Assign selected personnel nationality.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonNationality(AjaxBehaviorEvent e) {
		SmApplicationUtil.setSelectedPersonnelNationality(getManagedBean());
	}

	/**
	 * Ajax call to assign selected personnel privilege
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilege(AjaxBehaviorEvent e) {
		SmApplicationUtil.assignShareHolderPrivilege(getManagedBean());
	}

	/**
	 * Ajax call to assign selected personnel privilege by ep role.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void assignShareHolderPrivilegeByEpRole(AjaxBehaviorEvent e) {
		SmApplicationUtil.setPersonnelRoleChange(getManagedBean().getSmAppl(),
				SmApplicationUtil.getPrevApprovedPersonnel(getManagedBean()
						.getSelectedShareHolder(), getManagedBean()
						.getPrevApprovedPersonnels()), getManagedBean()
						.getSelectedShareHolder());
		getManagedBean()
				.setDisableRoleSelection(
						disableEpRoleDropDown(getManagedBean()
								.getSelectedShareHolder()));

		getManagedBean().setDisableAuthorizedRadio(
				disableAuthorizedRadio(getManagedBean()
						.getSelectedShareHolder()));
	}
	
	/**
	 * Assign selected personnel identity type.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 */
	public void assignPersonIdentType(AjaxBehaviorEvent e) {
		SmApplicationUtil
				.setSelectedPersonnelIdentificationType(getManagedBean()
						.getSelectedShareHolder());
	}

	/**
	 * Load selected staff academic qualification
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void assignQualification(AjaxBehaviorEvent e) {
		SmApplicationUtil.assignQualification(getManagedBean()
				.getSelectedStaffAcademic());
	}

	/**
	 * Get epRole from select items list
	 * 
	 * @param String
	 *            roleCode
	 * @return String
	 */
	public String getEpRoleLabelByRole(String roleCode) {
		String retVal = StringUtils.EMPTY;
		SelectItem item = (SelectItem) EntityHelper
				.getEntityBySinglePropertyFromList(roleCode, getManagedBean()
						.getePRoleList(), "value");
		if (item != null) {
			retVal = item.getLabel();
		}
		return retVal;
	}

	/**
	 * Get year list for work experience
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> getYearsWorkExp() {
		List<SelectItem> initYears = Arrays.asList(getYearDropDown());
		return SmApplicationUtil.getYearsWorkExp(getManagedBean()
				.getSelectedJobFunction(), initYears);
	}

	/**
	 * Getys Authorized Personnel Account Status
	 * 
	 * @param shareHolderBean
	 *            - SmPersonnel object
	 */
	public String getPersonnelActivationStatus(SmPersonnel smPersonnel,
			SmSupplier smSupplier) {
		return SmApplicationUtil.getPersonnelActivationStatus(smPersonnel,
				smSupplier);
	}

	/**
	 * Ajax call when select country to populate states
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeCountry(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateStateByCountry(addr, panelName,
				getManagedBean());
		setRoleStaffAddrReqd();
	}

	/**
	 * Ajax call when select state to populate district
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeState(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateDistrictsByState(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
	}

	/**
	 * Ajax call when select district to populate cities
	 * 
	 * @param SmAddress
	 *            addr
	 * @param String
	 *            panelName
	 */
	public void onChangeDistrict(SmAddress addr, String panelName) {
		SmApplicationUtil.getInstance().populateCitiesByDistrict(addr,
				panelName, getManagedBean());
		setRoleStaffAddrReqd();
		populateLocalAuthority(getManagedBean().getSmSupplier(),
				getManagedBean().getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress());
	}

	/**
	 * Ajax call when select city to reload and set city by city id transient
	 * value
	 * 
	 * @param SmAddress
	 *            addr
	 */
	public void onChangeCity(SmAddress addr, String panelName) {
		addr.setPmCity(SmCommonUtil.getInstance().getCityById(addr.getCityId()));
		setRoleStaffAddrReqd();
	}

	/**
	 * Get personnel maximum experience code
	 * 
	 * @return Long
	 */
	public Long getMaxExpCode() {
		return SmApplicationUtil.getPersonnelMaxExpCode(getManagedBean()
				.getSelectedShareHolder());
	}

	/**
	 * Get country label by country Id
	 * 
	 * @param Long
	 *            countryId
	 * @return String
	 */
	public String getCountryLabelById(Long countryId) {
		return SmCommonUtil.getCountryLabelById(countryId);
	}

	/**
	 * Get state label by state Id
	 * 
	 * @param Long
	 *            stateId
	 * @return String
	 */
	public String getStateLabelById(Long stateId) {
		return SmCommonUtil.getStateLabelById(stateId);
	}

	/**
	 * Get district label by district Id
	 * 
	 * @param Long
	 *            districtId
	 * @return String
	 */
	public String getDistrictLabelById(Long districtId) {
		return SmCommonUtil.getDistrictLabelById(districtId);
	}

	/**
	 * Get city label by city Id
	 * 
	 * @param Long
	 *            cityId
	 * @return String
	 */
	public String getCityLabelById(Long cityId) {
		return SmCommonUtil.getCityLabelById(cityId);
	}

	private void calcPersonnelListCount() {
		SmPersonnleCalcVO calcVO = SmCommonUtil.calcPersonnelListCount(
				getManagedBean().getSmAppl().getSmPersonnels(),
				getManagedBean().getSmAppl().getSmStaffTotals(),
				getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setPersonCalc(calcVO);
		calcAuthorizedPersonnel();
	}

	private void calcAuthorizedPersonnel() {
		// set already reach valid max authorized personnel and justification
		SmAuthorizedPersonnelCountVO countVO = SmApplicationUtil
				.getSmAuthorizedPersonnelCountVO(getManagedBean().getSmAppl());
		getManagedBean().setValidSmUserLimit(
				countVO.isReachAuthorizedPersonnelLimit());
		getManagedBean().setAuthVO(countVO);
	}

	/**
	 * Assign value required to false when any of the pop up is closed in the
	 * screen.Used for enable and disable screen validation dynamically.
	 */
	private void assignValReqdToFalse() {
		getManagedBean().setShowParentMsg(true);
		getManagedBean().setValueReqd(false);
	}

	private void initPersonnelList() {
		getManagedBean().setAuthorizedPersonnels(new ArrayList<SmPersonnel>());
		getManagedBean().setNormalPersonnels(new ArrayList<SmPersonnel>());
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.isAuthorized()) {
				getManagedBean().getAuthorizedPersonnels().add(per);
			} else {
				getManagedBean().getNormalPersonnels().add(per);
			}

		}
	}

	/**
	 * Initialize related information from DB
	 */
	private void init() {
		getManagedBean().setSmSupplier(
				getManagedBean().getSmAppl().getSmSupplier());
		initPersonnelList();
		getManagedBean().setSupplierUser(true);
		getManagedBean().setPrevApprovedPersonnels(
				SmApplicationUtil
						.getPrevApprovedPersonnelDirectorOwner(getManagedBean()
								.getSmAppl()));
		calcPersonnelListCount();
		SmApplicationUtil.setPrevApprovedPersonnelNoneEditable(getManagedBean()
				.getSmAppl(), getManagedBean().getPrevApprovedPersonnels(),
				getManagedBean().isDisAllowOwnerEdit());
		SmApplicationUtil.setPersonnelSoftcertRenewalStatus(this
				.getManagedBean().getSmSupplier(), this.getManagedBean()
				.getSmAppl().getSmPersonnels());
		SmApplicationUtil.sortPersonnelByRoleInCompany(getManagedBean()
				.getSmAppl());
		getManagedBean().setBusinessTypeVal(
				SmApplicationUtil.assignSupplierBusinessType(getManagedBean()
						.getSmSupplier()));
		getManagedBean().setCurrentPageType(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL);
		getManagedBean().setAcadQualifications(
				SmApplicationUtil.getAcademicQualifications(getManagedBean()));
		getManagedBean().setEduFieldList(
				SmApplicationUtil.retrieveEduFieldList());
		getManagedBean().setRacesList(SmApplicationUtil.retrievePmRaceList());
		getManagedBean().setCountryItems(SmCommonUtil.getCountryList());
		getManagedBean().setProfBodyItems(SmApplicationUtil.getProfBodyItems());
	}

}
