package my.ep.web.util.sm.helper;

import java.util.List;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduLevel;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.commons.lang3.StringUtils;

public class SmFieldChangeUtil {

	public static void initLatestAppl(SmApplicationManagedBean bean) {
		String showFieldChange = EpPropertiesUtils.getInstance().getProperty(
				"sm.show.field.changed");
		if (showFieldChange.equalsIgnoreCase("Y")
				&& bean.getSmAppl() != null
				&& StringUtils.isNotBlank(bean.getSmAppl().getApplType())
				&& !bean.getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSupplierId(bean.getSmSupplier().getSupplierId());
			bean.setLatestAppl(SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(mofReqDTO)
					.getSmAppl());
			bean.getLatestAppl()
					.setHqFacility(
							SmApplicationUtil.getHqFacilityByAppl(bean
									.getLatestAppl()));
			setApplTableListChangeStatus(bean.getLatestAppl(), bean.getSmAppl());
			bean.setShowFieldChanges(true);
		} else {
			bean.setShowFieldChanges(false);
		}
	}

	public static void initPersonnelChangeField(SmApplicationManagedBean bean) {
		bean.setLatestPersonnel(new SmPersonnel());
		if (bean.isShowFieldChanges()) {
			bean.setLatestPersonnel(SmFieldChangeUtil
					.getPersonnelByIdentificationNoFromList(bean
							.getLatestAppl(), bean.getSelectedShareHolder()
							.getIdentificationNo()));
			bean.setLatestOffAddr(SmFieldChangeUtil
					.getPersonnelAddressByTypeFromList(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_OFFICE,
							bean.getLatestPersonnel().getSmPersonnelAddresses()));
			bean.setLatestResdAddr(SmFieldChangeUtil
					.getPersonnelAddressByTypeFromList(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_RES,
							bean.getLatestPersonnel().getSmPersonnelAddresses()));
			bean.setLatestMailAddr(SmFieldChangeUtil
					.getPersonnelAddressByTypeFromList(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_MAIL,
							bean.getLatestPersonnel().getSmPersonnelAddresses()));
			bean.setLatestPermAddr(SmFieldChangeUtil
					.getPersonnelAddressByTypeFromList(
							SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM,
							bean.getLatestPersonnel().getSmPersonnelAddresses()));

			setPersonnelTableListChangeStatus(bean);
		}
	}

	public static void setApplTableListChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (currAppl != null && latestAppl != null) {

			// Cert of recognition (pending cant be done due to dont have id to
			// compare)

			// Professional Body
			setProfessionalBodyFieldChangeStatus(latestAppl, currAppl);

			// Associate Company
			setAssocCompanyFieldChangeStatus(latestAppl, currAppl);

			// Company experience
			setCompanyExpFieldChangeStatus(latestAppl, currAppl);

			// Personnel
			setPersonnelFieldChangeStatus(latestAppl, currAppl);

			// SmEquityCompanies
			setEquityCompanyFieldChangeStatus(latestAppl, currAppl);

			// smSupplierBranches
			setBranchFieldChangeStatus(latestAppl, currAppl);

			// smSupplierBanks
			setBankFieldChangeStatus(latestAppl, currAppl);

			// smSupplierCategories
			setCategoryFieldChangeStatus(latestAppl, currAppl);
		}
	}

	public static void setPersonnelTableListChangeStatus(
			SmApplicationManagedBean bean) {
		if (bean.isShowFieldChanges()) {
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

	private static void setCurrPersonnelFieldChange(SmPersonnel currPersonnel,
			SmAppl latestAppl) {
		SmPersonnel latestPersonnel = getPersonnelByIdentificationNoFromList(
				latestAppl, currPersonnel.getIdentificationNo());
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

	public static SmEquityCompany getEquityCompanyByRegNoFromList(SmAppl appl,
			String regNo) {
		SmEquityCompany retVal = new SmEquityCompany();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmEquityCompanies())) {
			for (SmEquityCompany tmp : appl.getSmEquityCompanies()) {
				if (tmp.getRegNo() != null && regNo != null
						&& tmp.getRegNo().equals(regNo)) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmAssociateCompany getAssocCompanyByRegNoFromList(
			SmAppl appl, String regNo) {
		SmAssociateCompany retVal = new SmAssociateCompany();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl
						.getSmAssociateCompanies())) {
			for (SmAssociateCompany tmp : appl.getSmAssociateCompanies()) {
				if (tmp.getRegNo() != null && regNo != null
						&& tmp.getRegNo().equals(regNo)) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmAssociationBody getProfBodyByRegNoFromList(SmAppl appl,
			String regNo) {
		SmAssociationBody retVal = new SmAssociationBody();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl
						.getSmAssociationBodies())) {
			for (SmAssociationBody tmp : appl.getSmAssociationBodies()) {
				if (tmp.getRegNo() != null && regNo != null
						&& tmp.getRegNo().equals(regNo)) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmCompanyExperience getCompanyExpByClientNameAndTitleFromList(
			SmAppl appl, String clientName, String title) {
		SmCompanyExperience retVal = new SmCompanyExperience();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl
						.getSmCompanyExperiences())) {
			for (SmCompanyExperience tmp : appl.getSmCompanyExperiences()) {
				if (StringUtils.isNotBlank(tmp.getClientName())
						&& StringUtils.isNotBlank(clientName)
						&& StringUtils.isNotBlank(tmp.getTitle())
						&& StringUtils.isNotBlank(title)
						&& clientName.equalsIgnoreCase(tmp.getClientName())
						&& title.equalsIgnoreCase(tmp.getTitle())) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmPersonnel getPersonnelByIdentificationNoFromList(
			SmAppl appl, String identificationNo) {
		SmPersonnel retVal = new SmPersonnel();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmPersonnels())) {
			for (SmPersonnel tmp : appl.getSmPersonnels()) {
				if (tmp.getIdentificationNo() != null
						&& identificationNo != null
						&& tmp.getIdentificationNo().equals(identificationNo)) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

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

	public static SmSupplierBranch getBranchByBranchCodeFromList(SmAppl appl,
			Long branchCode) {
		SmSupplierBranch retVal = new SmSupplierBranch();
		if (appl != null
				&& !SmCommonUtil
						.isCollectionEmpty(appl.getSmSupplierBranches())) {
			for (SmSupplierBranch tmp : appl.getSmSupplierBranches()) {
				if (tmp.getBranchCode() != null && branchCode != null
						&& tmp.getBranchCode().compareTo(branchCode) == 0) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmSupplierBank getBankByAccNoAndAccPurposeFromList(
			SmAppl appl, String accNo, String accPurpose) {
		SmSupplierBank retVal = new SmSupplierBank();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmSupplierBanks())) {
			for (SmSupplierBank tmp : appl.getSmSupplierBanks()) {
				if (StringUtils.isNotBlank(accNo)
						&& StringUtils.isNotBlank(tmp.getAccountNo())
						&& accNo.equals(tmp.getAccountNo())) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	public static SmSupplierCategory getCategoryByCatLvl3IdFromList(
			SmAppl appl, PmCategoryL3 catL3) {
		SmSupplierCategory retVal = new SmSupplierCategory();
		if (appl != null
				&& !SmCommonUtil.isCollectionEmpty(appl
						.getSmSupplierCategories())) {
			for (SmSupplierCategory tmp : appl.getSmSupplierCategories()) {
				if (catL3 != null
						&& tmp.getPmCategoryL3() != null
						&& catL3.getCategoryL3Id() != null
						&& tmp.getPmCategoryL3().getCategoryL3Id() != null
						&& catL3.getCategoryL3Id().compareTo(
								tmp.getPmCategoryL3().getCategoryL3Id()) == 0) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

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
					if (!isCurrPersonnelExistInLatestPersonnel(tmp,
							latestAppl.getSmPersonnels())) {
						tmp.setChangedFieldStatus("N");
					} else {
						setCurrPersonnelFieldChange(tmp, latestAppl);
					}
				}
			}
		}
	}

	private static boolean isCurrPersonnelExistInLatestPersonnel(
			SmPersonnel currPersonnel, List<SmPersonnel> latestPersonnelList) {
		boolean retVal = false;
		for (SmPersonnel latestPersonnel : latestPersonnelList) {
			if (latestPersonnel.getIdentificationNo().equalsIgnoreCase(
					currPersonnel.getIdentificationNo())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	private static void setCurrPersonnel(SmPersonnel currPersonnel,
			SmPersonnel latestPersonnel) {
		if (currPersonnel.getPmCountry() != null
				&& currPersonnel.getPmCountry() != null
				&& currPersonnel
						.getPmCountry()
						.getCountryId()
						.compareTo(
								latestPersonnel.getPmCountry().getCountryId()) != 0) {
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
		} else if (isHasDifferences(latestPersonnel, currPersonnel,
				new String[] { "personnelId", "dirOrHghrMngmt", "roleName",
						"prevEpRole", "softcertValidityDate", "softcertNo",
						"disableStaff", "disableSemiProf",
						"prevApprovedDirector", "prevApprovedEquityOwner",
						"disableRowSelection", "freeSoftCert",
						"softcertRejectReason", "pmCountry", "pmUser",
						"pmRace", "identificationType", "identityResponseCode",
						"tempPassword" })) {
			currPersonnel.setChangedFieldStatus("U");
		}
	}

	private static void setCurrPersonnelAssociationBodies(SmAppl latestAppl,
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmAssociationBodies())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmAssociationBodies())) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmAssociationBodies())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmAssociationBodies())) {
			currPersonnel.setChangedFieldStatus("U");
			for (SmAssociationBody currBody : currPersonnel
					.getSmAssociationBodies()) {
				currBody.setChangedFieldStatus("N");
			}
		} else {
			for (SmAssociationBody currBody : currPersonnel
					.getSmAssociationBodies()) {
				if (!isCurrProfBodyExistInLatestProfBody(currBody,
						latestPersonnel.getSmAssociationBodies())) {
					currPersonnel.setChangedFieldStatus("U");
					currBody.setChangedFieldStatus("N");
				} else {
					SmAssociationBody latestBody = getProfBodyByRegNoFromList(
							latestAppl, currBody.getRegNo());
					if (latestBody != null
							&& latestBody.getAssociationBodyId() != null) {
						if (isHasDifferences(latestBody, currBody,
								new String[] { "associationBodyId",
										"smPersonnel" })) {
							currPersonnel.setChangedFieldStatus("U");
							currBody.setChangedFieldStatus("U");
						}
					} else {
						currPersonnel.setChangedFieldStatus("U");
						currBody.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	private static void setCurrPersonnelRelatedCompanies(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmRelatedCompanies())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmRelatedCompanies())) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmRelatedCompanies())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmRelatedCompanies())) {
			currPersonnel.setChangedFieldStatus("U");
			for (SmRelatedCompany currRelComp : currPersonnel
					.getSmRelatedCompanies()) {
				currRelComp.setChangedFieldStatus("N");
			}
		} else {
			for (SmRelatedCompany currRelComp : currPersonnel
					.getSmRelatedCompanies()) {
				if (!isCurrPersonnelRelatedCompaniesExistInLatest(currRelComp,
						latestPersonnel.getSmRelatedCompanies())) {
					currPersonnel.setChangedFieldStatus("U");
					currRelComp.setChangedFieldStatus("N");
				} else {
					SmRelatedCompany latestRelComp = getRelatedCompanyByRegNoFromList(
							currRelComp.getRegNo(),
							latestPersonnel.getSmRelatedCompanies());
					if (latestRelComp != null
							&& latestRelComp.getRelatedCompanyId() != null) {
						if (isHasDifferences(latestRelComp, currRelComp,
								new String[] { "relatedCompanyId",
										"smPersonnel" })) {
							currPersonnel.setChangedFieldStatus("U");
							currRelComp.setChangedFieldStatus("U");
						}
					} else {
						currPersonnel.setChangedFieldStatus("U");
						currRelComp.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	private static boolean isCurrPersonnelRelatedCompaniesExistInLatest(
			SmRelatedCompany currRelComp,
			List<SmRelatedCompany> latestPersonnelRelCompList) {
		boolean retVal = false;
		for (SmRelatedCompany latestRelComp : latestPersonnelRelCompList) {
			if (latestRelComp.getRegNo().equals(currRelComp.getRegNo())) {
				retVal = true;
			}
		}
		return retVal;
	}

	private static void setCurrPersonnelStaffExperiences(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (SmCommonUtil.isCollectionEmpty(currPersonnel
				.getSmStaffExperiences())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmStaffExperiences())) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmStaffExperiences())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmStaffExperiences())) {
			currPersonnel.setChangedFieldStatus("U");
			for (SmStaffExperience currExp : currPersonnel
					.getSmStaffExperiences()) {
				currExp.setChangedFieldStatus("N");
			}
		} else {
			for (SmStaffExperience currExp : currPersonnel
					.getSmStaffExperiences()) {
				if (!isCurrPersonnelStaffExperienceExistInLatest(currExp,
						latestPersonnel.getSmStaffExperiences())) {
					currPersonnel.setChangedFieldStatus("U");
					currExp.setChangedFieldStatus("N");
				} else {
					SmStaffExperience latestExp = getStaffExperienceByCompNamFromList(
							currExp.getCompanyName(),
							latestPersonnel.getSmStaffExperiences());
					if (latestExp != null
							&& latestExp.getStaffExperienceId() != null) {
						if (isHasDifferences(latestExp, currExp, new String[] {
								"staffExperienceId", "smPersonnel" })) {
							currPersonnel.setChangedFieldStatus("U");
							currExp.setChangedFieldStatus("U");
						}
					} else {
						currPersonnel.setChangedFieldStatus("U");
						currExp.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	private static boolean isCurrPersonnelStaffExperienceExistInLatest(
			SmStaffExperience currExp,
			List<SmStaffExperience> latestPersonnelExpList) {
		boolean retVal = false;
		for (SmStaffExperience latestExp : latestPersonnelExpList) {
			if (latestExp.getCompanyName().equals(currExp.getCompanyName())) {
				retVal = true;
			}
		}
		return retVal;
	}

	private static void setCurrPersonnelStaffEducations(
			SmPersonnel currPersonnel, SmPersonnel latestPersonnel) {
		if (SmCommonUtil.isCollectionEmpty(currPersonnel.getSmStaffAcademics())
				&& !SmCommonUtil.isCollectionEmpty(latestPersonnel
						.getSmStaffAcademics())) {
			currPersonnel.setChangedFieldStatus("U");
		} else if (SmCommonUtil.isCollectionEmpty(latestPersonnel
				.getSmStaffAcademics())
				&& !SmCommonUtil.isCollectionEmpty(currPersonnel
						.getSmStaffAcademics())) {
			currPersonnel.setChangedFieldStatus("U");
			for (SmStaffAcademic currAcad : currPersonnel.getSmStaffAcademics()) {
				currAcad.setChangedFieldStatus("N");
			}
		} else {
			for (SmStaffAcademic currAcad : currPersonnel.getSmStaffAcademics()) {
				if (!isCurrPersonnelStaffEducationExistInLatest(currAcad,
						latestPersonnel.getSmStaffAcademics())) {
					currPersonnel.setChangedFieldStatus("U");
					currAcad.setChangedFieldStatus("N");
				} else {
					SmStaffAcademic latestAcad = getStaffAcadByPmEdu(
							currAcad.getPmEduField(), currAcad.getPmEduLevel(),
							latestPersonnel.getSmStaffAcademics());
					if (latestAcad != null
							&& latestAcad.getStaffAcademicId() != null) {
						if (isHasDifferences(latestAcad, currAcad,
								new String[] { "staffAcademicId",
										"qualificationLevelId", "smPersonnel" })) {
							currPersonnel.setChangedFieldStatus("U");
							currAcad.setChangedFieldStatus("U");
						}
					} else {
						currPersonnel.setChangedFieldStatus("U");
						currAcad.setChangedFieldStatus("N");
					}
				}
			}
		}
	}

	private static boolean isCurrPersonnelStaffEducationExistInLatest(
			SmStaffAcademic currAcad,
			List<SmStaffAcademic> latestPersonnelAcadList) {
		boolean retVal = false;
		for (SmStaffAcademic latestAcad : latestPersonnelAcadList) {
			if (latestAcad.getPmEduField() != null
					&& currAcad.getPmEduField() != null
					&& latestAcad.getPmEduLevel() != null
					&& currAcad.getPmEduLevel() != null
					&& latestAcad
							.getPmEduField()
							.getEduFieldId()
							.compareTo(currAcad.getPmEduField().getEduFieldId()) == 0
					&& latestAcad
							.getPmEduLevel()
							.getEduLevelId()
							.compareTo(currAcad.getPmEduLevel().getEduLevelId()) == 0) {
				retVal = true;
			}
		}
		return retVal;
	}

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
				if (!isCurrPersonnelAddrExistInLatest(currAddr,
						latestPersonnel.getSmPersonnelAddresses())) {
					currPersonnel.setChangedFieldStatus("U");
				} else {
					SmPersonnelAddress latestAddr = getPersonnelAddressByTypeFromList(
							currAddr.getAddressType(),
							latestPersonnel.getSmPersonnelAddresses());
					if (latestAddr != null) {
						if (latestAddr.getSmAddress() != null
								&& currAddr.getSmAddress() != null
								&& isAddressHasDifferences(
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

	private static boolean isCurrPersonnelAddrExistInLatest(
			SmPersonnelAddress currAddr,
			List<SmPersonnelAddress> latestPersonnelAddrList) {
		boolean retVal = false;
		for (SmPersonnelAddress latestAddr : latestPersonnelAddrList) {
			if (latestAddr.getAddressType().equals(currAddr.getAddressType())) {
				retVal = true;
			}
		}
		return retVal;
	}

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
					if (!isCurrAssocCompExistInLatestAssocComp(
							currAssocCompany,
							latestAppl.getSmAssociateCompanies())) {
						currAssocCompany.setChangedFieldStatus("N");
					} else {
						latestAssocCompany = getAssocCompanyByRegNoFromList(
								latestAppl, currAssocCompany.getRegNo());
						if (latestAssocCompany == null
								|| latestAssocCompany.getAssociateCompanyId() == null) {
							currAssocCompany.setChangedFieldStatus("N");
						} else {
							if (isAddressHasDifferences(
									latestAssocCompany.getSmAddress(),
									currAssocCompany.getSmAddress())
									|| isHasDifferences(
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

	private static boolean isCurrAssocCompExistInLatestAssocComp(
			SmAssociateCompany currAssocCompany,
			List<SmAssociateCompany> latestAssocCompanyList) {
		boolean retVal = false;
		for (SmAssociateCompany latestAssocCompany : latestAssocCompanyList) {
			if (latestAssocCompany.getRegNo().equalsIgnoreCase(
					currAssocCompany.getRegNo())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

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
					if (!isCurrCompExpExistInLatestCompExp(currCompExp,
							latestAppl.getSmCompanyExperiences())) {
						currCompExp.setChangedFieldStatus("N");
					} else {
						latestCompExp = getCompanyExpByClientNameAndTitleFromList(
								latestAppl, currCompExp.getClientName(),
								currCompExp.getTitle());
						if (latestCompExp == null
								|| latestCompExp.getCompanyExperienceId() == null) {
							currCompExp.setChangedFieldStatus("N");
						} else {
							if (isHasDifferences(latestCompExp, currCompExp,
									new String[] { "companyExperienceId" })) {
								currCompExp.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	private static boolean isCurrCompExpExistInLatestCompExp(
			SmCompanyExperience currCompanyExp,
			List<SmCompanyExperience> latestCompExpList) {
		boolean retVal = false;
		for (SmCompanyExperience latestCompExp : latestCompExpList) {
			if (StringUtils.isNotBlank(latestCompExp.getClientName())
					&& StringUtils.isNotBlank(latestCompExp.getTitle())
					&& StringUtils.isNotBlank(currCompanyExp.getTitle())
					&& StringUtils.isNotBlank(currCompanyExp.getClientName())
					&& currCompanyExp.getClientName().equalsIgnoreCase(
							latestCompExp.getClientName())
					&& currCompanyExp.getTitle().equalsIgnoreCase(
							latestCompExp.getTitle())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	private static void setCategoryFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (SmCommonUtil
				.isCollectionEmpty(latestAppl.getSmSupplierCategories())
				&& !SmCommonUtil.isCollectionEmpty(currAppl
						.getSmSupplierCategories())) {
			for (SmSupplierCategory currCategory : currAppl
					.getSmSupplierCategories()) {
				currCategory.setChangedFieldStatus("N");
			}
		} else {
			if (!SmCommonUtil.isCollectionEmpty(currAppl
					.getSmSupplierCategories())) {
				SmSupplierCategory latestCategory = null;
				for (SmSupplierCategory currCategory : currAppl
						.getSmSupplierCategories()) {
					if (!isCurrCategoryExistInLatestCategory(currCategory,
							latestAppl.getSmSupplierCategories())) {
						currCategory.setChangedFieldStatus("N");
					} else {
						latestCategory = getCategoryByCatLvl3IdFromList(
								latestAppl, currCategory.getPmCategoryL3());
						if (latestCategory == null
								|| latestCategory.getSupplierCategoryId() == null) {
							currCategory.setChangedFieldStatus("N");
						} else {
							if (currCategory.getPmCategoryL3() != null
									&& latestCategory.getPmCategoryL3() != null
									&& currCategory.getPmCategoryL3()
											.getCategoryL3Id() != null
									&& latestCategory.getPmCategoryL3()
											.getCategoryL3Id() != null
									&& currCategory
											.getPmCategoryL3()
											.getCategoryL3Id()
											.compareTo(
													latestCategory
															.getPmCategoryL3()
															.getCategoryL3Id()) != 0) {
								currCategory.setChangedFieldStatus("N");
							}
						}
					}
				}
			}
		}
	}

	private static boolean isCurrCategoryExistInLatestCategory(
			SmSupplierCategory currCategory,
			List<SmSupplierCategory> latestCategoryList) {
		boolean retVal = false;
		for (SmSupplierCategory latestCategory : latestCategoryList) {
			if (latestCategory.getPmCategoryL3() != null
					&& currCategory.getPmCategoryL3() != null
					&& latestCategory.getPmCategoryL3().getCategoryL3Id() != null
					&& currCategory.getPmCategoryL3().getCategoryL3Id() != null
					&& latestCategory
							.getPmCategoryL3()
							.getCategoryL3Id()
							.compareTo(
									currCategory.getPmCategoryL3()
											.getCategoryL3Id()) == 0) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

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
					if (!isCurrBankExistInLatestBank(currBank,
							latestAppl.getSmSupplierBanks())) {
						currBank.setChangedFieldStatus("N");
					} else {
						latestBank = getBankByAccNoAndAccPurposeFromList(
								latestAppl, currBank.getAccountNo(),
								currBank.getAccountPurpose());
						if (latestBank == null
								|| latestBank.getSupplierBankId() == null) {
							currBank.setChangedFieldStatus("N");
						} else {
							if (isAddressHasDifferences(
									latestBank.getSmAddress(),
									currBank.getSmAddress())
									|| isHasDifferences(latestBank, currBank,
											new String[] { "supplierBankId",
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

	private static boolean isCurrBankExistInLatestBank(SmSupplierBank currBank,
			List<SmSupplierBank> latestBankList) {
		boolean retVal = false;
		for (SmSupplierBank latestBank : latestBankList) {
			if (StringUtils.isNotBlank(latestBank.getAccountNo())
					&& StringUtils.isNotBlank(currBank.getAccountNo())
					&& latestBank.getAccountNo()
							.equals(currBank.getAccountNo())
					&& latestBank.getAccountPurpose().equals(
							currBank.getAccountPurpose())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

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
					if (!isCurrBranchExistInLatestBranch(currBranch,
							latestAppl.getSmSupplierBranches())) {
						currBranch.setChangedFieldStatus("N");
					} else {
						latestBranch = getBranchByBranchCodeFromList(
								latestAppl, currBranch.getBranchCode());
						if (latestBranch == null
								|| latestBranch.getSupplierBranchId() == null) {
							currBranch.setChangedFieldStatus("N");
						} else {
							if (isAddressHasDifferences(
									latestBranch.getSmAddress(),
									currBranch.getSmAddress())
									|| isHasDifferences(latestBranch,
											currBranch, new String[] {
													"supplierBranchId",
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

	private static boolean isCurrBranchExistInLatestBranch(
			SmSupplierBranch currBranch, List<SmSupplierBranch> latestBranchList) {
		boolean retVal = false;
		for (SmSupplierBranch latestBranch : latestBranchList) {
			if (currBranch.getBranchCode() != null
					&& latestBranch.getBranchCode() != null
					&& currBranch.getBranchCode().compareTo(
							latestBranch.getBranchCode()) == 0) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

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
					if (!isCurrEquityCompExistInLatestEquityComp(
							currEquityCompany,
							latestAppl.getSmEquityCompanies())) {
						currEquityCompany.setChangedFieldStatus("N");
					} else {
						latestEquityCompany = getEquityCompanyByRegNoFromList(
								latestAppl, currEquityCompany.getRegNo());
						if (latestEquityCompany == null
								|| latestEquityCompany.getEquityCompanyId() == null) {
							currEquityCompany.setChangedFieldStatus("N");
						} else {
							if (isAddressHasDifferences(
									latestEquityCompany.getSmAddress(),
									currEquityCompany.getSmAddress())
									|| isHasDifferences(
											latestEquityCompany,
											currEquityCompany,
											new String[] { "associateCompanyId" })) {
								currEquityCompany.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	private static boolean isCurrEquityCompExistInLatestEquityComp(
			SmEquityCompany currEquityCompany,
			List<SmEquityCompany> latestEquityCompanyList) {
		boolean retVal = false;
		for (SmEquityCompany latestEquityCompany : latestEquityCompanyList) {
			if (latestEquityCompany.getRegNo().equalsIgnoreCase(
					currEquityCompany.getRegNo())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	private static void setProfessionalBodyFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (latestAppl.getSmAssociationBodies() == null
				|| latestAppl.getSmAssociationBodies().isEmpty()) {
			if (currAppl.getSmAssociationBodies() != null
					&& !currAppl.getSmAssociationBodies().isEmpty()) {
				for (SmAssociationBody currBody : currAppl
						.getSmAssociationBodies()) {
					currBody.setChangedFieldStatus("N");
				}
			}
		} else {
			if (currAppl.getSmAssociationBodies() != null
					&& !currAppl.getSmAssociationBodies().isEmpty()) {
				SmAssociationBody latestBody = null;
				for (SmAssociationBody currBody : currAppl
						.getSmAssociationBodies()) {
					if (!isCurrProfBodyExistInLatestProfBody(currBody,
							latestAppl.getSmAssociationBodies())) {
						currBody.setChangedFieldStatus("N");
					} else {
						latestBody = getProfBodyByRegNoFromList(latestAppl,
								currBody.getRegNo());
						if (latestBody == null
								|| latestBody.getAssociationBodyId() == null) {
							currBody.setChangedFieldStatus("N");
						} else {
							if (isHasDifferences(latestBody, currBody,
									new String[] { "associationBodyId" })) {
								currBody.setChangedFieldStatus("U");
							}
						}
					}
				}
			}
		}
	}

	private static boolean isCurrProfBodyExistInLatestProfBody(
			SmAssociationBody currBody,
			List<SmAssociationBody> latestProfBodyList) {
		boolean retVal = false;
		for (SmAssociationBody latestBody : latestProfBodyList) {
			if (latestBody.getRegNo().equalsIgnoreCase(currBody.getRegNo())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

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

	public static SmStaffAcademic getStaffAcadByPmEdu(PmEduField eduField,
			PmEduLevel eduLevel, List<SmStaffAcademic> staffAcadList) {
		SmStaffAcademic acad = null;
		if (!SmCommonUtil.isCollectionEmpty(staffAcadList)) {
			for (SmStaffAcademic latestAcad : staffAcadList) {
				if (latestAcad.getPmEduField() != null
						&& latestAcad.getPmEduField().getEduFieldId() != null
						&& eduField != null
						&& eduField.getEduFieldId() != null
						&& latestAcad.getPmEduLevel() != null
						&& latestAcad.getPmEduLevel().getEduLevelId() != null
						&& eduLevel != null
						&& eduLevel.getEduLevelId() != null
						&& latestAcad.getPmEduField().getEduFieldId()
								.compareTo(eduField.getEduFieldId()) == 0
						&& latestAcad.getPmEduLevel().getEduLevelId()
								.compareTo(eduLevel.getEduLevelId()) == 0) {
					acad = latestAcad;
				}
			}
		}
		return acad;
	}

	public static SmStaffExperience getStaffExperienceByCompNamFromList(
			String companyName, List<SmStaffExperience> expList) {
		SmStaffExperience retVal = null;
		if (!SmCommonUtil.isCollectionEmpty(expList)) {
			for (SmStaffExperience latestExp : expList) {
				if (latestExp.getCompanyName().equals(companyName)) {
					retVal = latestExp;
				}
			}
		}
		return retVal;
	}

	public static SmRelatedCompany getRelatedCompanyByRegNoFromList(
			String regNo, List<SmRelatedCompany> relCompList) {
		SmRelatedCompany retVal = null;
		if (SmCommonUtil.isCollectionEmpty(relCompList)) {
			for (SmRelatedCompany latestRelComp : relCompList) {
				if (latestRelComp.getRegNo().equals(regNo)) {
					retVal = latestRelComp;
				}
			}
		}
		return retVal;
	}

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

	private static boolean isAddressHasDifferences(SmAddress latest,
			SmAddress current) {
		boolean retVal = false;
		if ((latest == null && current != null)
				|| (latest != null && current == null)) {
			retVal = true;
		} else if (isHasDifferences(latest, current, new String[] {
				"addressId", "pmCountry", "pmState", "pmCity", "pmDistrict" })) {
			retVal = true;
		} else {
			if ((latest.getPmCountry() != null && current.getPmCountry() == null)
					|| (latest.getPmCountry() == null && current.getPmCountry() != null)
					|| (latest.getPmCountry() != null
							&& current.getPmCountry() != null && latest
							.getPmCountry().getCountryId()
							.compareTo(current.getPmCountry().getCountryId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmState() != null && current.getPmState() == null)
					|| (latest.getPmState() == null && current.getPmState() != null)
					|| (latest.getPmState() != null
							&& current.getPmState() != null && latest
							.getPmState().getStateId()
							.compareTo(current.getPmState().getStateId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmCity() != null && current.getPmCity() == null)
					|| (latest.getPmCity() == null && current.getPmCity() != null)
					|| (latest.getPmCity() != null
							&& current.getPmCity() != null && latest
							.getPmCity().getCityId()
							.compareTo(current.getPmCity().getCityId()) != 0)) {
				retVal = true;
			} else if ((latest.getPmDistrict() != null && current
					.getPmDistrict() == null)
					|| (latest.getPmDistrict() == null && current
							.getPmDistrict() != null)
					|| (latest.getPmDistrict() != null
							&& current.getPmDistrict() != null && latest
							.getPmDistrict().getDistrictId()
							.compareTo(current.getPmDistrict().getDistrictId()) != 0)) {
				retVal = true;
			}
		}
		return retVal;
	}

	private static boolean isHasDifferences(Object latest, Object current,
			String[] excludeProps) {
		return SmCommonUtil.isSmObjectPropsDifference(latest, current,
				excludeProps);
	}

}
