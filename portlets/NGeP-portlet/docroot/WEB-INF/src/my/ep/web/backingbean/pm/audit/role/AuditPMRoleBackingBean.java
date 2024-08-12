package my.ep.web.backingbean.pm.audit.role;

import my.ep.type.pm.RoleCode;
import my.ep.web.util.FacesScopes;

/**
 * AuditPMRoleBackingBean
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
}