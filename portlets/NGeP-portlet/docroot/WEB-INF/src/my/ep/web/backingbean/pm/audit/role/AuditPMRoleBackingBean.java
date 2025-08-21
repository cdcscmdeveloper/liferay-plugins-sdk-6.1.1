package my.ep.web.backingbean.pm.audit.role;

import java.util.Arrays;
import java.util.List;

import my.ep.type.pm.RoleCode;
import my.ep.web.util.FacesScopes;

/**
 * #35509
 * Setting checking user role that allow to access menu 
 * based on PM config/process.
 * 
 * @author normuliana
 * 
 */
public class AuditPMRoleBackingBean {
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * roleLocation
	 * 
	 * @return
	 */
	public String roleLocation() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_1.value()) ? RoleCode.EP_ADMIN_1.value() : getFacesScopes().hasRoleUser(
				RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : null;
	}

	/**
	 * roleSupplierZone
	 * 
	 * @return
	 */
	public String roleSupplierZone() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleUOM() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleCalendar() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleSalutation() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleRace() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleQTChecklist() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleEducation() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;

	}

	/**
	 * @return
	 */
	public String roleProfCertBody() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;

	}

	/**
	 * @return
	 */
	public String roleBusinessNature() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String supplierPerformanceCriteriaUOM() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleSupportingDoc() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleSysParam() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_1.value()) ? RoleCode.EP_ADMIN_1.value() : null;
	}

	/**
	 * @return
	 */
	public String rolePoJobWeight() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleWorkAllocation() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleProcessOfficerRoleTask() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleEquipmentMachine() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}

	/**
	 * @return
	 */
	public String roleSoftCert() {
		List<RoleCode> rolePriority = Arrays.asList(RoleCode.EP_ADMIN_1, RoleCode.EP_ADMIN_2, RoleCode.SUPPORT_APPL);

		for (RoleCode role : rolePriority) {
			if (getFacesScopes().hasRoleUser(role.value())) {
				return role.value();
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String roleReasonMaintenance() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value()
				: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() 
						: getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() 
								: getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN
						.value() : null;
	}

	/**
	 * @return
	 */
	public String roleUsrGroup() {
		List<RoleCode> rolePriority = Arrays.asList(RoleCode.EP_ADMIN, RoleCode.EP_ADMIN_2, RoleCode.PTJ_ADMIN,
				RoleCode.MINISTRY_ADMIN, RoleCode.JABATAN_ADMIN);

		for (RoleCode role : rolePriority) {
			if (getFacesScopes().hasRoleUser(role.value())) {
				return role.value();
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String roleAuthorizedSign() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}
	
	/**
	 * @return
	 */
	public String roleCategoryCode() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.UPS_ADMIN.value()) ? RoleCode.UPS_ADMIN.value() : null;
	}
	
	/**
	 * @return
	 */
	public String roleOrganizationProfile() {
		List<RoleCode> rolePriority = Arrays.asList(RoleCode.EP_ADMIN, RoleCode.EP_ADMIN_2, RoleCode.PTJ_ADMIN,
				RoleCode.MINISTRY_ADMIN, RoleCode.JABATAN_ADMIN, RoleCode.BPK_ADMIN, RoleCode.SUPPORT_ADMIN);

		for (RoleCode role : rolePriority) {
			if (getFacesScopes().hasRoleUser(role.value())) {
				return role.value();
			}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public String roleFinancialOrganization() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
	}
	
	/**
	 * @return
	 */
	public String roleConfUser() {
		List<RoleCode> rolePriority = Arrays.asList(RoleCode.EP_ADMIN, RoleCode.EP_ADMIN_2, RoleCode.BPK_ADMIN,
				RoleCode.SUPPORT_APPL, RoleCode.UPS_ADMIN, RoleCode.PTJ_ADMIN, RoleCode.MINISTRY_ADMIN, RoleCode.JABATAN_ADMIN,
				RoleCode.SUPPORT_ADMIN);
		for (RoleCode role : rolePriority) {
			if (getFacesScopes().hasRoleUser(role.value())) {
				return role.value();
			}
		}
		return null;
	}
}