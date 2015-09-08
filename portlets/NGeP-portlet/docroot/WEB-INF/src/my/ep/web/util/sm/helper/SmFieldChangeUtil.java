package my.ep.web.util.sm.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.commons.beanutils.BeanUtils;

public class SmFieldChangeUtil {

	private static String[] smAddressExcludeProps = { "addressId",
			"smAssociateCompanies", "smEquityCompanies", "smFacilities",
			"smPersonnelAddresses", "smSupplierBanks", "smSupplierBranches",
			"smCompanyAddresses" };

	public static void initLatestAppl(SmApplicationManagedBean bean) {
		String showFieldChange = EpPropertiesUtils.getInstance().getProperty(
				"sm.show.field.changed");
		if (showFieldChange.equalsIgnoreCase("Y")) {
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSupplierId(bean.getSmSupplier().getSupplierId());
			bean.setLatestAppl(SmServiceLookup.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(mofReqDTO)
					.getSmAppl());
			bean.getLatestAppl()
					.setHqFacility(
							SmApplicationUtil.getHqFacilityByAppl(bean
									.getLatestAppl()));
			SmFieldChangeUtil.setTableListChangeStatus(bean.getLatestAppl(),
					bean.getSmAppl());
			bean.setShowFieldChanges(true);
		} else {
			bean.setShowFieldChanges(false);
		}
	}

	public static void setTableListChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (currAppl != null && latestAppl != null) {

			// Cert of recognition (pending cant be done due to dont have id to
			// compare)

			// Professional Body
			setProfessionalBodyFieldChangeStatus(latestAppl, currAppl);

			// Associate Company
			setAssocCompanyFieldChangeStatus(latestAppl, currAppl);

			// Company experience (pending cant be done due to dont have id to
			// compare)

			// Personnel
			setPersonnelFieldChangeStatus(latestAppl, currAppl);
		}
	}

	public static SmAssociateCompany getAssocCompanyByRegNoFromList(
			SmAppl appl, String regNo) {
		SmAssociateCompany retVal = new SmAssociateCompany();
		if (appl != null && appl.getSmAssociateCompanies() != null
				&& !appl.getSmAssociateCompanies().isEmpty()) {
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
		if (appl != null && appl.getSmAssociationBodies() != null
				&& !appl.getSmAssociationBodies().isEmpty()) {
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

	public static SmPersonnel getPersonnelByIdentificationNoFromList(
			SmAppl appl, String identificationNo) {
		SmPersonnel retVal = new SmPersonnel();
		if (appl != null && appl.getSmPersonnels() != null
				&& !appl.getSmPersonnels().isEmpty()) {
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
		if (appl != null && appl.getSmStaffTotals() != null
				&& !appl.getSmStaffTotals().isEmpty()) {
			for (SmStaffTotal staffTotalTmp : appl.getSmStaffTotals()) {
				if (staffTotalTmp.getStaffCategory().equals(staffCategory)) {
					staffTotal = staffTotalTmp;
					break;
				}
			}
		}
		return staffTotal;
	}

	private static void setPersonnelFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (latestAppl.getSmPersonnels() == null
				|| latestAppl.getSmPersonnels().isEmpty()) {
			if (currAppl.getSmPersonnels() != null
					&& !currAppl.getSmPersonnels().isEmpty()) {
				for (SmPersonnel tmp : currAppl.getSmPersonnels()) {
					tmp.setChangedFieldStatus("N");
				}
			}
		} else {
			if (currAppl.getSmPersonnels() != null
					&& !currAppl.getSmPersonnels().isEmpty()) {
				for (SmPersonnel tmp : currAppl.getSmPersonnels()) {
					if (!isCurrPersonnelExistInLatestPersonnel(tmp,
							latestAppl.getSmPersonnels())) {
						tmp.setChangedFieldStatus("N");
					} else {
						setCurrPersonnelFieldChange(tmp,
								latestAppl.getSmPersonnels());
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

	private static void setCurrPersonnelFieldChange(SmPersonnel currPersonnel,
			List<SmPersonnel> latestPersonnelList) {
		for (SmPersonnel latestPersonnel : latestPersonnelList) {
			if (latestPersonnel.getIdentificationNo().equalsIgnoreCase(
					currPersonnel.getIdentificationNo())) {

				// personnel
				setCurrPersonnel(currPersonnel, latestPersonnel);

				// staffAcademics

				// staffExperiences

				// professionalBodies
				setCurrAssociationBodies(currPersonnel, latestPersonnel);
			}
		}
	}

	private static void setCurrPersonnel(SmPersonnel currPersonnel,
			SmPersonnel latestPersonnel) {
		if (changeApplied(latestPersonnel, currPersonnel, new String[] {
				"personnelId", "revNo", "smAssociationBodies",
				"smChequeSignatories", "smPersonnelAddresses",
				"smStaffAcademics", "smStaffExperiences", "smRelatedCompanies",
				"smSupplierBranches", "dirOrHghrMngmt", "roleName",
				"prevEpRole", "softcertValidityDate", "softcertNo",
				"disableStaff", "disableSemiProf", "prevApprovedDirector",
				"prevApprovedEquityOwner", "disableRowSelection",
				"freeSoftCert", "softcertRejectReason" })) {
			currPersonnel.setChangedFieldStatus("U");
		}
	}

	private static void setCurrAssociationBodies(SmPersonnel currPersonnel,
			SmPersonnel latestPersonnel) {
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
		} else {
			for (SmAssociationBody currBody : currPersonnel
					.getSmAssociationBodies()) {
				for (SmAssociationBody latestBody : latestPersonnel
						.getSmAssociationBodies()) {
					if (latestBody.getRegNo().equals(currBody.getRegNo())
							&& changeApplied(latestBody, currBody,
									new String[] { "associationBodyId" })) {
						currBody.setChangedFieldStatus("U");
					}
				}
			}
		}
	}

	private static void setAssocCompanyFieldChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (latestAppl.getSmAssociateCompanies() == null
				|| latestAppl.getSmAssociateCompanies().isEmpty()) {
			if (currAppl.getSmAssociateCompanies() != null
					&& !currAppl.getSmAssociateCompanies().isEmpty()) {
				for (SmAssociateCompany currAssocCompany : currAppl
						.getSmAssociateCompanies()) {
					currAssocCompany.setChangedFieldStatus("N");
				}
			}
		} else {
			if (currAppl.getSmAssociateCompanies() != null
					&& !currAppl.getSmAssociateCompanies().isEmpty()) {
				for (SmAssociateCompany currAssocCompany : currAppl
						.getSmAssociateCompanies()) {
					if (!isCurrAssocCompExistInLatestAssocComp(
							currAssocCompany,
							latestAppl.getSmAssociateCompanies())) {
						currAssocCompany.setChangedFieldStatus("N");
					} else {
						setCurrAssocCompFieldChange(currAssocCompany,
								latestAppl.getSmAssociateCompanies());
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

	private static void setCurrAssocCompFieldChange(
			SmAssociateCompany currAssocCompany,
			List<SmAssociateCompany> latestAssocCompanyList) {
		for (SmAssociateCompany latestAssocCompany : latestAssocCompanyList) {
			if (latestAssocCompany.getRegNo().equalsIgnoreCase(
					currAssocCompany.getRegNo())) {
				setCurrAssocComp(latestAssocCompany, currAssocCompany);
				setCurrAssocCompAddress(latestAssocCompany, currAssocCompany);
			}
		}
	}

	private static void setCurrAssocComp(SmAssociateCompany latestAssocCompany,
			SmAssociateCompany currAssocCompany) {
		if (changeApplied(latestAssocCompany, currAssocCompany,
				new String[] { "associateCompanyId" })) {
			currAssocCompany.setChangedFieldStatus("U");
		}
	}

	private static void setCurrAssocCompAddress(
			SmAssociateCompany latestAssocCompany,
			SmAssociateCompany currAssocCompany) {
		if ((latestAssocCompany.getSmAddress() == null && currAssocCompany
				.getSmAddress() != null)
				|| (latestAssocCompany.getSmAddress() != null && currAssocCompany
						.getSmAddress() == null)
				|| (changeApplied(latestAssocCompany.getSmAddress(),
						currAssocCompany.getSmAddress(), smAddressExcludeProps))) {
			currAssocCompany.setChangedFieldStatus("U");
		}
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
				for (SmAssociationBody currBody : currAppl
						.getSmAssociationBodies()) {
					if (!isCurrProfBodyExistInLatestProfBody(currBody,
							latestAppl.getSmAssociationBodies())) {
						currBody.setChangedFieldStatus("N");
					} else {
						setCurrProfBodyFieldChange(currBody,
								latestAppl.getSmAssociationBodies());
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

	private static void setCurrProfBodyFieldChange(SmAssociationBody currBody,
			List<SmAssociationBody> latestProfBodyList) {
		for (SmAssociationBody latestBody : latestProfBodyList) {
			if (latestBody.getRegNo().equalsIgnoreCase(currBody.getRegNo())
					&& changeApplied(latestBody, currBody,
							new String[] { "associationBodyId" })) {
				currBody.setChangedFieldStatus("U");
			}
		}
	}

	public static boolean changeApplied(Object latest, Object current,
			String[] excludeProps) {
		boolean retVal = false;
		List<String> excludeList = new ArrayList<String>();
		excludeList.add("changedFieldStatus");
		excludeList.add("createdDate");
		excludeList.add("createdBy");
		excludeList.add("changedDate");
		excludeList.add("changedBy");
		excludeList.add("revNo");
		excludeList.add("smAppl");
		excludeList.add("smAddress");
		if (excludeProps != null && excludeProps.length > 0) {
			excludeList.addAll(Arrays.asList(excludeProps));
		}
		try {
			Map<String, String> latestMap = BeanUtils.describe(latest);
			Map<String, String> currMap = BeanUtils.describe(current);

			if ((latestMap.keySet().containsAll(currMap.keySet()))) {
				for (final String key : latestMap.keySet()) {
					if (!excludeList.contains(key)) {
						if (latestMap.get(key) == null
								|| latestMap.get(key).isEmpty()) {
							if (currMap.get(key) != null
									&& !currMap.get(key).isEmpty()) {
								retVal = true;
								break;
							}
						} else {
							if (!latestMap.get(key).equals(currMap.get(key))) {
								retVal = true;
								break;
							}
						}
					}
				}
			}
		} catch (IllegalAccessException e) {

		} catch (InvocationTargetException e) {

		} catch (NoSuchMethodException e) {

		}
		return retVal;
	}
}
