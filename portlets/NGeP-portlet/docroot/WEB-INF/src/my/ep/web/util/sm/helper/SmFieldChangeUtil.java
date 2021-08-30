package my.ep.web.util.sm.helper;

import java.util.List;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.commons.lang.StringUtils;
// #30660
import org.apache.log4j.Logger;

public class SmFieldChangeUtil {
	// #30660
	private static Logger log = Logger.getLogger(SmFieldChangeUtil.class);

	/**
	 * Initialized all related application change field in table compare with
	 * previous approved application
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void initLatestAppl(SmApplicationManagedBean bean) {
		//16052019 - SSM INTEGRATION CR
		if (bean.isShowNewChanges()
				&& bean.getSmAppl() != null
				&& StringUtils.isNotBlank(bean.getSmAppl().getApplType())
				&& (!bean.getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF))) {//|| bean.getSmAppl().isApplValidWithSsm() -> remove this from if cz effect other
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			SmAppl srcAppl = null;
			mofReqDTO.setSupplierId(bean.getSmSupplier().getSupplierId());
			srcAppl = SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(mofReqDTO)
					.getSmAppl();
			bean.setLatestApproveAppl(srcAppl);
			if (bean.getSmAppl().isResubmit() && bean.isSupplierUser()
					&& bean.getSmAppl().getOriginalAppl() != null) {
				mofReqDTO.setSmAppl(bean.getSmAppl());
				SmAppl latestRejectedAppl = SmServiceLookup
						.getMofRegistrationSO()
						.retrieveLatestRejectedApplication(mofReqDTO)
						.getSmAppl();
				SmApplRevisionResDTO applRevDTO = SmCommonUtil
						.getSerializedApplicationByFilePathName(latestRejectedAppl
								.getSupplierSerFile());
				if (applRevDTO == null) {
					srcAppl = latestRejectedAppl;
				} else {
					srcAppl = applRevDTO.getSmAppl();
				}
			}
			bean.setLatestAppl(srcAppl);
			bean.getLatestAppl()
					.setHqFacility(
							SmApplicationUtil.getHqFacilityByAppl(bean
									.getLatestAppl()));
			setApplTableListChangeStatus(bean.getLatestAppl(),
					bean.getSmAppl(),
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
			bean.setShowFieldChanges(true);
		} else {
			bean.setShowFieldChanges(false);
			bean.setLatestAppl(null);
		}
	}

	/**
	 * Initialized all related personnel change field in table compare with
	 * latest approved personnel
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void initPersonnelChangeField(SmApplicationManagedBean bean) {
		bean.setLatestPersonnel(new SmPersonnel());
		if (bean.isShowFieldChanges()) {
			bean.setLatestPersonnel((SmPersonnel) EntityHelper
					.getEntityByMultiplePropertiesFromList(bean
							.getSelectedShareHolder(), bean.getLatestAppl()
							.getSmPersonnels(),
							new String[] { "identificationNo" }, null, null));
			if (bean.getLatestPersonnel() != null
					&& bean.getLatestPersonnel().getPersonnelId() != null) {
				bean.setLatestOffAddr(SmFieldChangeUtil
						.getPersonnelAddressByTypeFromList(
								SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE,
								bean.getLatestPersonnel()
										.getSmPersonnelAddresses()));
				bean.setLatestResdAddr(SmFieldChangeUtil
						.getPersonnelAddressByTypeFromList(
								SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES,
								bean.getLatestPersonnel()
										.getSmPersonnelAddresses()));
				bean.setLatestMailAddr(SmFieldChangeUtil
						.getPersonnelAddressByTypeFromList(
								SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL,
								bean.getLatestPersonnel()
										.getSmPersonnelAddresses()));
				bean.setLatestPermAddr(SmFieldChangeUtil
						.getPersonnelAddressByTypeFromList(
								SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM,
								bean.getLatestPersonnel()
										.getSmPersonnelAddresses()));

				setPersonnelTableListChangeStatus(bean);
			}
		}
	}

	/**
	 * Initialized company experience change field in table compare with latest
	 * approved company experience
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void initCompanyExperienceChangeField(
			SmApplicationManagedBean bean) {
		bean.setLatestCompanyExperience(new SmCompanyExperience());
		if (bean.isShowFieldChanges()) {
			bean.setLatestCompanyExperience((SmCompanyExperience) EntityHelper
					.getEntityByMultiplePropertiesFromList(bean
							.getSelectedCompanyExperience(), bean
							.getLatestAppl().getSmCompanyExperiences(),
							new String[] { "clientName", "title" }, null, null));

			if (bean.getLatestCompanyExperience() != null
					&& bean.getLatestCompanyExperience()
							.getCompanyExperienceId() != null) {
				// smCompanyExperienceCat
				setCurrSmCompanyExpCat(bean.getSelectedCompanyExperience(),
						bean.getLatestCompanyExperience());
			}
		}
	}

	/**
	 * Set current application entities object change field status for
	 * displaying in data table
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	public static void setApplTableListChangeStatus(SmAppl latestAppl,
			SmAppl currAppl, String currentPage) {
		if (currAppl != null && latestAppl != null) {

			if (currentPage != null) {

				if (currentPage
						.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
					// Cert of recognition
					setSuppCertFieldChangeStatus(latestAppl, currAppl);

					// Professional Body
					setProfessionalBodyFieldChangeStatus(latestAppl, currAppl);

				} else if (currentPage
						.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
					// Associate Company
					setAssocCompanyFieldChangeStatus(latestAppl, currAppl);

					// Company experience
					setCompanyExpFieldChangeStatus(latestAppl, currAppl);

				} else if (currentPage
						.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
					// Personnel
					setPersonnelFieldChangeStatus(latestAppl, currAppl);

					// smSupplierBranches
					setBranchFieldChangeStatus(latestAppl, currAppl);

				} else if (currentPage
						.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
					// Personnel
					setPersonnelFieldChangeStatus(latestAppl, currAppl);

					// SmEquityCompanies
					setEquityCompanyFieldChangeStatus(latestAppl, currAppl);

				} else if (currentPage
						.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)) {
					// smSupplierBanks
					setBankFieldChangeStatus(latestAppl, currAppl);
				}
			}

			// smMachineDetails

			// smManufacturingDtls
		}
	}

	/**
	 * Set current personnel entities object change field status for displaying
	 * in data table
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void setPersonnelTableListChangeStatus(
			SmApplicationManagedBean bean) {
		if (bean.isShowFieldChanges() && bean.getLatestPersonnel() != null
				&& bean.getLatestPersonnel().getPersonnelId() != null) {
			// staffExperiences
			setCurrPersonnelStaffExperiences(bean.getSelectedShareHolder(),
					bean.getLatestPersonnel());

			// staffAcademics
			setCurrPersonnelStaffEducations(bean.getSelectedShareHolder(),
					bean.getLatestPersonnel());

			// relatedCompanies
			setCurrPersonnelRelatedCompanies(bean.getSelectedShareHolder(),
					bean.getLatestPersonnel());

			// professionalBodies
			setCurrPersonnelAssociationBodies(bean.getLatestAppl(),
					bean.getSelectedShareHolder(), bean.getLatestPersonnel());
		}
	}

	/**
	 * Set current personnel entities child object change field status for
	 * displaying in data table
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmAppl
	 *            latestAppl
	 */
	private static void setCurrPersonnelFieldChange(SmPersonnel currPersonnel,
			SmAppl latestAppl) {
		SmPersonnel latestPersonnel = (SmPersonnel) EntityHelper
				.getEntityByMultiplePropertiesFromList(currPersonnel,
						latestAppl.getSmPersonnels(),
						new String[] { "identificationNo" }, null, null);
		// personnel
		setCurrPersonnel(currPersonnel, latestPersonnel);

		// smPersonnelAddresses
		setCurrPersonnelSmPersonnelAddresses(currPersonnel, latestPersonnel);

		// staffAcademics
		setCurrPersonnelStaffEducations(currPersonnel, latestPersonnel);

		// staffExperiences
		setCurrPersonnelStaffExperiences(currPersonnel, latestPersonnel);

		// professionalBodies
		setCurrPersonnelAssociationBodies(latestAppl, currPersonnel,
				latestPersonnel);

		// relatedCompanies
		setCurrPersonnelRelatedCompanies(currPersonnel, latestPersonnel);
	}

	/**
	 * Get staff total by category
	 * 
	 * @param SmAppl
	 *            appl
	 * @param String
	 *            staffCategory
	 * @return SmStaffTotal
	 */
	public static SmStaffTotal getStaffTotalByCategoryFromList(SmAppl appl,
			String staffCategory) {
		SmStaffTotal staffTotal = new SmStaffTotal();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmStaffTotals())) {
			for (SmStaffTotal staffTotalTmp : appl.getSmStaffTotals()) {
				if (staffTotalTmp.getStaffCategory().equals(staffCategory)) {
					staffTotal = staffTotalTmp;
					break;
				}
			}
		}
		return staffTotal;
	}

	/**
	 * Set personnel field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setPersonnelFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmPersonnels())
				&& !SmCommonUtil.isCollectionEmpty(currAppl.getSmPersonnels())) {
			for (SmPersonnel tmp : currAppl.getSmPersonnels()) {
				tmp.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl.getSmPersonnels())) {
				for (SmPersonnel tmp : currAppl.getSmPersonnels()) {
					if (!isCurrExistInLatest(tmp, latestAppl.getSmPersonnels(),
							new String[] { "identificationNo" }, null)) {
						tmp.setChangedFieldStatus("N");
					} else {
						setCurrPersonnelFieldChange(tmp, latestAppl);
					}
				}
			}
		}
	}

	/**
	 * Set current personnel change field status
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnel(SmPersonnel currPersonnel,
			SmPersonnel latestPersonnel) {
		if (currPersonnel.getPmCountry() != null
				&& currPersonnel.getPmCountry() != null
				&& latestPersonnel.getPmCountry() != null
				&& currPersonnel
						.getPmCountry()
						.getCountryId()
						.compareTo(
								latestPersonnel.getPmCountry().getCountryId()) != 0) {
			currPersonnel.setChangedFieldStatus("U");
		} else if ((currPersonnel.getTitle() != null && latestPersonnel
				.getTitle() == null)
				|| (currPersonnel.getTitle() == null && latestPersonnel
						.getTitle() != null)
				|| (currPersonnel.getTitle() != null
						&& latestPersonnel.getTitle() != null && currPersonnel
						.getTitle()
						.getSalutationId()
						.compareTo(latestPersonnel.getTitle().getSalutationId()) != 0)) {
			currPersonnel.setChangedFieldStatus("U");
		} else if ((currPersonnel.getPmRace() != null && latestPersonnel
				.getPmRace() == null)
				|| (currPersonnel.getPmRace() == null && latestPersonnel
						.getPmRace() != null)
				|| (currPersonnel.getPmRace() != null
						&& latestPersonnel.getPmRace() != null && currPersonnel
						.getPmRace().getRaceId()
						.compareTo(latestPersonnel.getPmRace().getRaceId()) != 0)) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isHasDifferences(latestPersonnel,
				currPersonnel, new String[] { "personnelId", "dirOrHghrMngmt",
						"roleName", "prevEpRole", "softcertValidityDate",
						"softcertNo", "disableStaff", "disableSemiProf",
						"prevApprovedDirector", "prevApprovedEquityOwner",
						"disableRowSelection", "freeSoftCert",
						"softcertRejectReason", "pmCountry", "pmUser",
						"pmRace", "title", "identificationType",
						"identityResponseCode", "tempPassword", "softcert",
						"softcertFlg" })) {
			currPersonnel.setChangedFieldStatus("U");
		}
	}

	/**
	 * Set current personnel association bodies change field status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnelAssociationBodies(SmAppl latestAppl,
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (latestPersonnel == null
				|| StringUtils.isBlank(latestPersonnel.getIdentificationNo())) {
			currPersonnel.setChangedFieldStatus("N");
			if (currPersonnel != null
					&& !SmCommonUtil.isCollectionEmpty(currPersonnel
							.getSmRelatedCompanies())) {
				for (SmAssociationBody currBody : currPersonnel
						.getSmAssociationBodies()) {
					currBody.setChangedFieldStatus("N");
				}
			}
		} else if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmAssociationBodies())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmAssociationBodies())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmAssociationBodies())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmAssociationBodies())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
			for (SmAssociationBody currBody : currPersonnel
					.getSmAssociationBodies()) {
				currBody.setChangedFieldStatus("N");
			}
		} else {
			for (SmAssociationBody currBody : currPersonnel
					.getSmAssociationBodies()) {
				if (isCurrExistInLatest(currBody,
						latestPersonnel.getSmAssociationBodies(),
						new String[] { "regNo" }, null)) {
					if (StringUtils.isBlank(currPersonnel
							.getChangedFieldStatus())) {
						currPersonnel.setChangedFieldStatus("U");
					}
					currBody.setChangedFieldStatus("N");
				} else {
					SmAssociationBody latestBody = (SmAssociationBody) EntityHelper
							.getEntityByMultiplePropertiesFromList(currBody,
									latestAppl.getSmAssociationBodies(),
									new String[] { "regNo" }, null, null);
					if (latestBody != null
							&& latestBody.getAssociationBodyId() != null) {
						if (SmCommonUtil.isHasDifferences(latestBody, currBody,
								new String[] { "associationBodyId",
										"smPersonnel", "pmCertBody" })) {
							if (StringUtils.isBlank(currPersonnel
									.getChangedFieldStatus())) {
								currPersonnel.setChangedFieldStatus("U");
							}
							currBody.setChangedFieldStatus("U");
						}
					} else {
						if (StringUtils.isBlank(currPersonnel
								.getChangedFieldStatus())) {
							currPersonnel.setChangedFieldStatus("U");
						}
						currBody.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	/**
	 * Set current personnel related companies change field status
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnelRelatedCompanies(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (latestPersonnel == null
				|| StringUtils.isBlank(latestPersonnel.getIdentificationNo())) {
			currPersonnel.setChangedFieldStatus("N");
			if (currPersonnel != null
					&& !SmCommonUtil.isCollectionEmpty(currPersonnel
							.getSmRelatedCompanies())) {
				for (SmRelatedCompany currRelComp : currPersonnel
						.getSmRelatedCompanies()) {
					currRelComp.setChangedFieldStatus("N");
				}
			}
		} else if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmRelatedCompanies())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmRelatedCompanies())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmRelatedCompanies())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmRelatedCompanies())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
			for (SmRelatedCompany currRelComp : currPersonnel
					.getSmRelatedCompanies()) {
				currRelComp.setChangedFieldStatus("N");
			}
		} else {
			for (SmRelatedCompany currRelComp : currPersonnel
					.getSmRelatedCompanies()) {
				if (!isCurrExistInLatest(currRelComp,
						latestPersonnel.getSmRelatedCompanies(),
						new String[] { "regNo" }, null)) {
					if (StringUtils.isBlank(currPersonnel
							.getChangedFieldStatus())) {
						currPersonnel.setChangedFieldStatus("U");
					}
					currRelComp.setChangedFieldStatus("N");
				} else {
					SmRelatedCompany latestRelComp = (SmRelatedCompany) EntityHelper
							.getEntityByMultiplePropertiesFromList(currRelComp,
									latestPersonnel.getSmRelatedCompanies(),
									new String[] { "regNo" }, null, null);
					if (latestRelComp != null
							&& latestRelComp.getRelatedCompanyId() != null) {
						if (SmCommonUtil.isHasDifferences(latestRelComp,
								currRelComp, new String[] { "relatedCompanyId",
										"smPersonnel" })) {
							if (StringUtils.isBlank(currPersonnel
									.getChangedFieldStatus())) {
								currPersonnel.setChangedFieldStatus("U");
							}
							currRelComp.setChangedFieldStatus("U");
						}
					} else {
						if (StringUtils.isBlank(currPersonnel
								.getChangedFieldStatus())) {
							currPersonnel.setChangedFieldStatus("U");
						}
						currRelComp.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	/**
	 * Set current personnel staff experiences change field status
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnelStaffExperiences(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (latestPersonnel == null
				|| StringUtils.isBlank(latestPersonnel.getIdentificationNo())) {
			currPersonnel.setChangedFieldStatus("N");
			if (currPersonnel != null
					&& !SmCommonUtil.isCollectionEmpty(currPersonnel
							.getSmStaffExperiences())) {
				for (SmStaffExperience currExp : currPersonnel
						.getSmStaffExperiences()) {
					currExp.setChangedFieldStatus("N");
				}
			}
		} else if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmStaffExperiences())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmStaffExperiences())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmStaffExperiences())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmStaffExperiences())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
			for (SmStaffExperience currExp : currPersonnel
					.getSmStaffExperiences()) {
				currExp.setChangedFieldStatus("N");
			}
		} else {
			for (SmStaffExperience currExp : currPersonnel
					.getSmStaffExperiences()) {
				if (!isCurrExistInLatest(currExp,
						latestPersonnel.getSmStaffExperiences(), new String[] {
								"companyName", "toYear" }, null)) {
					if (StringUtils.isBlank(currPersonnel
							.getChangedFieldStatus())) {
						currPersonnel.setChangedFieldStatus("U");
					}
					currExp.setChangedFieldStatus("N");
				} else {
					SmStaffExperience latestExp = (SmStaffExperience) EntityHelper
							.getEntityByMultiplePropertiesFromList(currExp,
									latestPersonnel.getSmStaffExperiences(),
									new String[] { "companyName", "toYear" },
									null, null);
					if (latestExp != null
							&& latestExp.getStaffExperienceId() != null) {
						if (SmCommonUtil.isHasDifferences(latestExp, currExp,
								new String[] { "staffExperienceId",
										"smPersonnel" })) {
							if (StringUtils.isBlank(currPersonnel
									.getChangedFieldStatus())) {
								currPersonnel.setChangedFieldStatus("U");
							}
							currExp.setChangedFieldStatus("U");
						}
					} else {
						if (StringUtils.isBlank(currPersonnel
								.getChangedFieldStatus())) {
							currPersonnel.setChangedFieldStatus("U");
						}
						currExp.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	/**
	 * Set current personnel staff educations change field status
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnelStaffEducations(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (latestPersonnel == null
				|| StringUtils.isBlank(latestPersonnel.getIdentificationNo())) {
			currPersonnel.setChangedFieldStatus("N");
			if (currPersonnel != null
					&& !SmCommonUtil.isCollectionEmpty(currPersonnel
							.getSmStaffAcademics())) {
				for (SmStaffAcademic currAcad : currPersonnel
						.getSmStaffAcademics()) {
					currAcad.setChangedFieldStatus("N");
				}
			}
		} else if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmStaffAcademics())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmStaffAcademics())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmStaffAcademics())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmStaffAcademics())) {
			if (StringUtils.isBlank(currPersonnel.getChangedFieldStatus())) {
				currPersonnel.setChangedFieldStatus("U");
			}
			for (SmStaffAcademic currAcad : currPersonnel.getSmStaffAcademics()) {
				currAcad.setChangedFieldStatus("N");
			}
		} else {
			for (SmStaffAcademic currAcad : currPersonnel.getSmStaffAcademics()) {
				if (!isCurrExistInLatest(currAcad,
						latestPersonnel.getSmStaffAcademics(), null,
						new String[] { "pmEduField", "qualificationId" })) {
					if (StringUtils.isBlank(currPersonnel
							.getChangedFieldStatus())) {
						currPersonnel.setChangedFieldStatus("U");
					}
					currAcad.setChangedFieldStatus("N");
				} else {
					SmStaffAcademic latestAcad = (SmStaffAcademic) EntityHelper
							.getEntityByMultiplePropertiesFromList(currAcad,
									latestPersonnel.getSmStaffAcademics(),
									null, new String[] { "qualificationId",
											"pmEduField" }, null);
					if (latestAcad != null
							&& latestAcad.getStaffAcademicId() != null) {
						if (SmCommonUtil.isHasDifferences(latestAcad, currAcad,
								new String[] { "staffAcademicId",
										"smPersonnel", "pmEduLevel",
										"qualificationLevelId" })) {
							if (StringUtils.isBlank(currPersonnel
									.getChangedFieldStatus())) {
								currPersonnel.setChangedFieldStatus("U");
							}
							currAcad.setChangedFieldStatus("U");
						}
					} else {
						if (StringUtils.isBlank(currPersonnel
								.getChangedFieldStatus())) {
							currPersonnel.setChangedFieldStatus("U");
						}
						currAcad.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	/**
	 * Set current personnel addresses change field status
	 * 
	 * @param SmPersonnel
	 *            currPersonnel
	 * @param SmPersonnel
	 *            latestPersonnel
	 */
	private static void setCurrPersonnelSmPersonnelAddresses(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmPersonnelAddresses())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmPersonnelAddresses())) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmPersonnelAddresses())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmPersonnelAddresses())) {
			currPersonnel.setChangedFieldStatus("U");
		} else {
			for (SmPersonnelAddress currAddr : currPersonnel
					.getSmPersonnelAddresses()) {
				if (!isCurrExistInLatest(currAddr,
						latestPersonnel.getSmPersonnelAddresses(),
						new String[] { "addressType" }, null)) {
					currPersonnel.setChangedFieldStatus("U");
				} else {
					SmPersonnelAddress latestAddr = (SmPersonnelAddress) EntityHelper
							.getEntityByMultiplePropertiesFromList(currAddr,
									latestPersonnel.getSmPersonnelAddresses(),
									new String[] { "addressType" }, null, null);
					if (latestAddr != null) {
						if (latestAddr.getSmAddress() != null
								&& currAddr.getSmAddress() != null
								&& SmCommonUtil.isAddressHasDifferences(
										latestAddr.getSmAddress(),
										currAddr.getSmAddress())) {
							currPersonnel.setChangedFieldStatus("U");
						}
					} else {
						currPersonnel.setChangedFieldStatus("U");
					}
				}
			}
		}
	}

	/**
	 * Set association company change field status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setAssocCompanyFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil
				.isCollectionEmpty(latestAppl.getSmAssociateCompanies())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmAssociateCompanies())) {
			for (SmAssociateCompany currAssocCompany : currAppl
					.getSmAssociateCompanies()) {
				currAssocCompany.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmAssociateCompanies())) {
				SmAssociateCompany latestAssocCompany = null;
				for (SmAssociateCompany currAssocCompany : currAppl
						.getSmAssociateCompanies()) {
					if (!isCurrExistInLatest(currAssocCompany,
							latestAppl.getSmAssociateCompanies(),
							new String[] { "regNo" }, null)) {
						currAssocCompany.setChangedFieldStatus("N");
					} else {
						latestAssocCompany = (SmAssociateCompany) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currAssocCompany,
										latestAppl.getSmAssociateCompanies(),
										new String[] { "regNo" }, null, null);
						if (latestAssocCompany == null
								|| latestAssocCompany.getAssociateCompanyId() == null) {
							currAssocCompany.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isAddressHasDifferences(
									latestAssocCompany.getSmAddress(),
									currAssocCompany.getSmAddress())
									|| SmCommonUtil
											.isHasDifferences(
													latestAssocCompany,
													currAssocCompany,
													new String[] { "associateCompanyId" })) {
								currAssocCompany.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set company experience field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setCompanyExpFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil
				.isCollectionEmpty(latestAppl.getSmCompanyExperiences())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmCompanyExperiences())) {
			for (SmCompanyExperience currCompExp : currAppl
					.getSmCompanyExperiences()) {
				currCompExp.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmCompanyExperiences())) {
				SmCompanyExperience latestCompExp = null;
				for (SmCompanyExperience currCompExp : currAppl
						.getSmCompanyExperiences()) {
					if (!isCurrExistInLatest(currCompExp,
							latestAppl.getSmCompanyExperiences(), new String[] {
									"clientName", "title" }, null)) {
						currCompExp.setChangedFieldStatus("N");
					} else {
						latestCompExp = (SmCompanyExperience) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currCompExp,
										latestAppl.getSmCompanyExperiences(),
										new String[] { "clientName", "title" },
										null, null);
						if (latestCompExp == null
								|| latestCompExp.getCompanyExperienceId() == null) {
							currCompExp.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isHasDifferences(latestCompExp,
									currCompExp,
									new String[] { "companyExperienceId" })) {
								currCompExp.setChangedFieldStatus("U");
							}
						}
						setCurrSmCompanyExpCat(currCompExp, latestCompExp);
					}
				}
			}
		}
	}

	/**
	 * Set bank field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setBankFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmSupplierBanks())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmSupplierBanks())) {
			for (SmSupplierBank currBank : currAppl.getSmSupplierBanks()) {
				currBank.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl.getSmSupplierBanks())) {
				SmSupplierBank latestBank = null;
				for (SmSupplierBank currBank : currAppl.getSmSupplierBanks()) {
					if (!isCurrExistInLatest(currBank,
							latestAppl.getSmSupplierBanks(), new String[] {
									"accountNo", "accountPurpose" }, null)) {
						currBank.setChangedFieldStatus("N");
					} else {
						latestBank = (SmSupplierBank) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currBank,
										latestAppl.getSmSupplierBanks(),
										new String[] { "accountNo",
												"accountPurpose" }, null, null);
						if (latestBank == null
								|| latestBank.getSupplierBankId() == null) {
							currBank.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isAddressHasDifferences(
									latestBank.getSmAddress(),
									currBank.getSmAddress())
									|| SmCommonUtil.isHasDifferences(
											latestBank, currBank, new String[] {
													"supplierBankId",
													"smSupplierBranch",
													"pmFinancialOrg" })) {
								currBank.setChangedFieldStatus("U");
							} else if (latestBank.getPmFinancialOrg() != null
									&& currBank.getPmFinancialOrg() != null
									&& currBank
											.getPmFinancialOrg()
											.getFinancialOrgId()
											.compareTo(
													latestBank
															.getPmFinancialOrg()
															.getFinancialOrgId()) != 0) {
								currBank.setChangedFieldStatus("U");
							} else {
								setCurrBankChequeSignatories(currBank,
										latestBank);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set branch field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setBranchFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmSupplierBranches())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmSupplierBranches())) {
			for (SmSupplierBranch currBranch : currAppl.getSmSupplierBranches()) {
				currBranch.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmSupplierBranches())) {
				SmSupplierBranch latestBranch = null;
				for (SmSupplierBranch currBranch : currAppl
						.getSmSupplierBranches()) {
					if (!isCurrExistInLatest(currBranch,
							latestAppl.getSmSupplierBranches(),
							new String[] { "branchCode" }, null)) {
						currBranch.setChangedFieldStatus("N");
					} else {
						latestBranch = (SmSupplierBranch) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currBranch,
										latestAppl.getSmSupplierBranches(),
										new String[] { "branchCode" }, null,
										null);
						if (latestBranch == null
								|| latestBranch.getSupplierBranchId() == null) {
							currBranch.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isAddressHasDifferences(
									latestBranch.getSmAddress(),
									currBranch.getSmAddress())
									|| SmCommonUtil.isHasDifferences(
											latestBranch, currBranch,
											new String[] { "supplierBranchId",
													"smPersonnel",
													"contactPersonId" })) {
								currBranch.setChangedFieldStatus("U");
							} else if ((currBranch.getSmPersonnel() != null && latestBranch
									.getSmPersonnel() == null)
									|| (currBranch.getSmPersonnel() == null && latestBranch
											.getSmPersonnel() != null)
									|| (currBranch.getSmPersonnel() != null
											&& latestBranch.getSmPersonnel() != null && !currBranch
											.getSmPersonnel()
											.getIdentificationNo()
											.equals(latestBranch
													.getSmPersonnel()
													.getIdentificationNo()))) {
								currBranch.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set equity company field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setEquityCompanyFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmEquityCompanies())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmEquityCompanies())) {
			for (SmEquityCompany currEquityCompany : currAppl
					.getSmEquityCompanies()) {
				currEquityCompany.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil
					.isCollectionEmpty(currAppl.getSmEquityCompanies())) {
				SmEquityCompany latestEquityCompany = null;
				for (SmEquityCompany currEquityCompany : currAppl
						.getSmEquityCompanies()) {
					if (!isCurrExistInLatest(currEquityCompany,
							latestAppl.getSmEquityCompanies(),
							new String[] { "regNo" }, null)) {
						currEquityCompany.setChangedFieldStatus("N");
					} else {
						latestEquityCompany = (SmEquityCompany) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currEquityCompany,
										latestAppl.getSmEquityCompanies(),
										new String[] { "regNo" }, null, null);
						if (latestEquityCompany == null
								|| latestEquityCompany.getEquityCompanyId() == null) {
							currEquityCompany.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isAddressHasDifferences(
									latestEquityCompany.getSmAddress(),
									currEquityCompany.getSmAddress())
									|| SmCommonUtil.isHasDifferences(
											latestEquityCompany,
											currEquityCompany,
											new String[] { "equityCompanyId" })) {
								currEquityCompany.setChangedFieldStatus("U");
							}
						}
					}
					// #30660 start
					log.info("<<< Current and Latest Equity Company Info Comparisaon : >>>");
					log.info("*** CompanyName : " + currEquityCompany.getCompanyName() + " > ");
//					log.info(latestEquityCompany.getCompanyName() + " ***");
//					log.info("*** RegNo : " + currEquityCompany.getRegNo() + " > " + latestEquityCompany.getRegNo() + " ***");
//					log.info("*** ShareAmt :" + currEquityCompany.getShareAmt() + " > :" + latestEquityCompany.getShareAmt() + " ***");
//					log.info("*** SharePercentage : " + currEquityCompany.getSharePercentage() + " > " + latestEquityCompany.getSharePercentage() + " ***");
//					log.info("*** SsmData : " + currEquityCompany.isSsmData() + " > " + latestEquityCompany.isSsmData() + " ***");
//					log.info("*** LocalCapital : " + currEquityCompany.isLocalCapital() + " > " + latestEquityCompany.isLocalCapital() + " ***");
//					log.info("*** ForeignCapital : " + currEquityCompany.isForeignCapital() + " > " + latestEquityCompany.isForeignCapital() + " ***");
//					log.info("*** GovAgencyCapital : " + currEquityCompany.isGovAgencyCapital() + " > " + latestEquityCompany.isGovAgencyCapital() + " ***");
//					log.info("*** GovEquityCapital : " + currEquityCompany.isGovEquityCapital() + " > " + latestEquityCompany.isGovEquityCapital() + " ***");
//					log.info("*** ShareAmtSsm : " + currEquityCompany.getShareAmtSsm() + " > " + latestEquityCompany.getShareAmtSsm() + " ***");
//					log.info("<<< Current and Latest Equity Company Address Comparison : >>>");
//					log.info("*** Address1 : " + currEquityCompany.getSmAddress().getAddress1() + " > " + latestEquityCompany.getSmAddress().getAddress1() + " ***");
//					log.info("*** Address2 : " + currEquityCompany.getSmAddress().getAddress2() + " > " + latestEquityCompany.getSmAddress().getAddress2() + " ***");
//					log.info("*** Address3 : " + currEquityCompany.getSmAddress().getAddress3() + " > " + latestEquityCompany.getSmAddress().getAddress3() + " ***");
//					log.info("*** MapLatitude : " + currEquityCompany.getSmAddress().getMapLatitude() + " > " + latestEquityCompany.getSmAddress().getMapLatitude() + " ***");
//					log.info("*** MapLongitude : " + currEquityCompany.getSmAddress().getMapLongitude() + " > " + latestEquityCompany.getSmAddress().getMapLongitude() + " ***");
//					log.info("*** Postcode : " + currEquityCompany.getSmAddress().getPostcode() + " > " + latestEquityCompany.getSmAddress().getPostcode() + " ***");
//					log.info("*** PmDivision : " + currEquityCompany.getSmAddress().getPmDivision() + " > " + latestEquityCompany.getSmAddress().getPmDivision() + " ***");
//					log.info("*** AddressSsmNull : " + currEquityCompany.getSmAddress().isAddressSsmNull() + " > " + latestEquityCompany.getSmAddress().isAddressSsmNull() + " ***");
//					log.info("*** PostcodeSsmNull : " + currEquityCompany.getSmAddress().isPostcodeSsmNull() + " > " + latestEquityCompany.getSmAddress().isPostcodeSsmNull() + " ***");
//					log.info("*** StateSsmNull : " + currEquityCompany.getSmAddress().isStateSsmNull() + " > " + latestEquityCompany.getSmAddress().isStateSsmNull() + " ***");
//					log.info("*** CountrySsmNull() : " + currEquityCompany.getSmAddress().isCountrySsmNull() + " > " + latestEquityCompany.getSmAddress().isCountrySsmNull() + " ***");
//					log.info("*** SsmData : " + currEquityCompany.getSmAddress().isSsmData() + " > " + latestEquityCompany.getSmAddress().isSsmData() + " ***");
//					log.info("*** DivisionId : " + currEquityCompany.getSmAddress().getDivisionId() + " > " + latestEquityCompany.getSmAddress().getDivisionId() + " ***");
					// #30660 end
				}
			}
		}
	}

	/**
	 * Set supplier certificate field change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setSuppCertFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmSupplierCerts())) {
			if (!SmCommonUtil.isCollectionEmpty(currAppl.getSmSupplierCerts())) {
				for (SmSupplierCert currCert : currAppl.getSmSupplierCerts()) {
					currCert.setChangedFieldStatus("N");
				}
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl.getSmSupplierCerts())) {
				SmSupplierCert latestCert = null;
				for (SmSupplierCert currCert : currAppl.getSmSupplierCerts()) {
					if (!isCurrExistInLatest(currCert,
							latestAppl.getSmSupplierCerts(), new String[] {
									"certTypeId", "certName" },
							new String[] { "pmCertBody" })) {
						currCert.setChangedFieldStatus("N");
					} else {
						latestCert = (SmSupplierCert) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currCert,
										latestAppl.getSmSupplierCerts(),
										new String[] { "certTypeId", "certName" },
										new String[] { "pmCertBody" }, null);
						if (latestCert == null
								|| latestCert.getSupplierCertId() == null) {
							currCert.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isHasDifferences(latestCert,
									currCert, new String[] { "supplierCertId",
											"pmCertBody" })) {
								currCert.setChangedFieldStatus("U");
							} else if (latestCert.getPmCertBody() != null
									&& currCert.getPmCertBody() != null
									&& latestCert.getPmCertBody()
											.getCertBodyId() != null
									&& currCert.getPmCertBody().getCertBodyId() != null
									&& currCert
											.getPmCertBody()
											.getCertBodyId()
											.compareTo(
													latestCert.getPmCertBody()
															.getCertBodyId()) != 0) {
								currCert.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set professional body change status
	 * 
	 * @param SmAppl
	 *            latestAppl
	 * @param SmAppl
	 *            currAppl
	 */
	private static void setProfessionalBodyFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil.isCollectionEmpty(latestAppl.getSmAssociationBodies())) {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmAssociationBodies())) {
				for (SmAssociationBody currBody : currAppl
						.getSmAssociationBodies()) {
					currBody.setChangedFieldStatus("N");
				}
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmAssociationBodies())) {
				SmAssociationBody latestBody = null;
				for (SmAssociationBody currBody : currAppl
						.getSmAssociationBodies()) {
					if (!isCurrExistInLatest(currBody,
							latestAppl.getSmAssociationBodies(),
							new String[] { "regNo" }, null)) {
						currBody.setChangedFieldStatus("N");
					} else {
						latestBody = (SmAssociationBody) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										currBody,
										latestAppl.getSmAssociationBodies(),
										new String[] { "regNo" }, null, null);
						if (latestBody == null
								|| latestBody.getAssociationBodyId() == null) {
							currBody.setChangedFieldStatus("N");
						} else {
							if (SmCommonUtil.isHasDifferences(latestBody,
									currBody,
									new String[] { "associationBodyId" })) {
								currBody.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set current bank cheque signatories change field status
	 * 
	 * @param SmSupplierBank
	 *            currBank
	 * @param SmSupplierBank
	 *            latestBank
	 */
	private static void setCurrBankChequeSignatories(SmSupplierBank currBank,
			SmSupplierBank latestBank) {
		if (SmCommonUtil.isCollectionEmpty(currBank.getSmChequeSignatories())
				&& !SmCommonUtil.isCollectionEmpty(latestBank
						.getSmChequeSignatories())) {
			currBank.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestBank
				.getSmChequeSignatories())
				&& !SmCommonUtil.isCollectionEmpty(currBank
						.getSmChequeSignatories())) {
			currBank.setChangedFieldStatus("U");
			for (SmChequeSignatory currSign : currBank.getSmChequeSignatories()) {
				currSign.setChangedFieldStatus("N");
			}
		} else {
			for (SmChequeSignatory currSign : currBank.getSmChequeSignatories()) {
				if (!isCurrBankChequeSignatoryExistInLatest(currSign,
						latestBank.getSmChequeSignatories())) {
					currBank.setChangedFieldStatus("U");
					currSign.setChangedFieldStatus("N");
				} else {
					SmChequeSignatory latestSign = getSignatoryByIdNoAndBankAccNoFromList(
							currSign.getSmPersonnel() != null ? currSign
									.getSmPersonnel().getIdentificationNo()
									: "", currBank.getAccountNo(),
							latestBank.getSmChequeSignatories());
					if (latestSign == null
							|| latestSign.getChequeSignatoryId() == null) {
						currBank.setChangedFieldStatus("U");
						currSign.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	/**
	 * Check if current bank signatories exist in latest bank signatories
	 * 
	 * @param SmChequeSignatory
	 *            currSign
	 * @param List
	 *            <SmChequeSignatory> latestBankSignList
	 * @return boolean
	 */
	private static boolean isCurrBankChequeSignatoryExistInLatest(
			SmChequeSignatory currSign,
			List<SmChequeSignatory> latestBankSignList) {
		boolean retVal = false;
		for (SmChequeSignatory latestBankSign : latestBankSignList) {
			if (latestBankSign.getSmPersonnel() != null
					&& currSign.getSmPersonnel() != null
					&& latestBankSign
							.getSmPersonnel()
							.getIdentificationNo()
							.equals(currSign.getSmPersonnel()
									.getIdentificationNo())) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Set current company experience category change field status
	 * 
	 * @param SmCompanyExperience
	 *            currCompExp
	 * @param SmCompanyExperience
	 *            latestcurrCompExp
	 */
	private static void setCurrSmCompanyExpCat(SmCompanyExperience currCompExp,
			SmCompanyExperience latestcurrCompExp) {
		if (SmCommonUtil.isCollectionEmpty(currCompExp
				.getSmCompanyExperienceCats())
				&& !SmCommonUtil.isCollectionEmpty(latestcurrCompExp
						.getSmCompanyExperienceCats())) {
			currCompExp.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestcurrCompExp
				.getSmCompanyExperienceCats())
				&& !SmCommonUtil.isCollectionEmpty(currCompExp
						.getSmCompanyExperienceCats())) {
			currCompExp.setChangedFieldStatus("U");
			for (SmCompanyExperienceCat currExpCat : currCompExp
					.getSmCompanyExperienceCats()) {
				currExpCat.setChangedFieldStatus("N");
			}
		} else {
			SmCompanyExperienceCat latestCompExpCat = null;
			for (SmCompanyExperienceCat currExpCat : currCompExp
					.getSmCompanyExperienceCats()) {
				latestCompExpCat = (SmCompanyExperienceCat) EntityHelper
						.getEntityByMultiplePropertiesFromList(currExpCat,
								latestcurrCompExp.getSmCompanyExperienceCats(),
								null, new String[] { "pmCategoryL3" }, null);
				if (latestCompExpCat == null) {
					currCompExp.setChangedFieldStatus("U");
					currExpCat.setChangedFieldStatus("N");
				}
			}
		}
	}

	/**
	 * Get personnel address from list by type
	 * 
	 * @param String
	 *            type
	 * @param List
	 *            <SmPersonnelAddress> personnelAddressList
	 * @return SmPersonnelAddress
	 */
	public static SmPersonnelAddress getPersonnelAddressByTypeFromList(
			String type, List<SmPersonnelAddress> personnelAddressList) {
		SmPersonnelAddress addr = null;
		if (personnelAddressList != null) {
			for (SmPersonnelAddress tmp : personnelAddressList) {
				if (tmp.getAddressType().equals(type)) {
					addr = tmp;
				}
			}
		}
		return addr;
	}

	/**
	 * Get signatory from list by identification no and bank account no
	 * 
	 * @param String
	 *            identificationNo
	 * @param String
	 *            accNo
	 * @param List
	 *            <SmChequeSignatory> signList
	 * @return SmChequeSignatory
	 */
	public static SmChequeSignatory getSignatoryByIdNoAndBankAccNoFromList(
			String identificationNo, String accNo,
			List<SmChequeSignatory> signList) {
		SmChequeSignatory retVal = null;
		if (!SmCommonUtil.isCollectionEmpty(signList)) {
			for (SmChequeSignatory latestSign : signList) {
				if (latestSign.getSmPersonnel() != null
						&& latestSign.getSmPersonnel().getIdentificationNo()
								.equals(identificationNo)
						&& latestSign.getSmSupplierBank() != null
						&& latestSign.getSmSupplierBank().getAccountNo()
								.equals(accNo)) {
					retVal = latestSign;
				}
			}
		}
		return retVal;
	}

	/**
	 * Is current object exist in latest object
	 * 
	 * @param Object
	 *            currObj
	 * @param List
	 *            <? extends Object> latestList
	 * @param String
	 *            [] primitiveProps
	 * @param String
	 *            [] nonePrimitiveProps
	 * @return boolean
	 */
	private static boolean isCurrExistInLatest(Object currObj,
			List<? extends Object> latestList, String[] primitiveProps,
			String[] nonePrimitiveProps) {
		boolean retVal = false;
		Object objRetVal = EntityHelper.getEntityByMultiplePropertiesFromList(
				currObj, latestList, primitiveProps, nonePrimitiveProps, null);
		retVal = objRetVal != null ? true : false;
		return retVal;
	}

}
