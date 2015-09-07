package my.ep.web.util.sm.helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmStaffTotal;

import org.apache.commons.beanutils.BeanUtils;

public class SmFieldChangeUtil {

	public static void setTableListChangeStatus(SmAppl latestAppl,
			SmAppl currAppl) {
		if (currAppl != null && latestAppl != null) {
			// Professional Body
			setProfessionalBodyFieldChangeStatus(latestAppl, currAppl);

			// Associate Company
			setAssocCompanyFieldChangeStatus(latestAppl, currAppl);
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
					currAssocCompany.getRegNo())
					&& changeApplied(latestAssocCompany, currAssocCompany, null)) {
				currAssocCompany.setChangedFieldStatus("U");
			}
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
					&& changeApplied(latestBody, currBody, null)) {
				currBody.setChangedFieldStatus("U");
			}
		}
	}

	public static boolean changeApplied(Object latest, Object current,
			String[] excludeProps) {
		boolean retVal = false;
		List<String> excludeList = new ArrayList<String>();
		excludeList.add("changedFieldStatus");
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
