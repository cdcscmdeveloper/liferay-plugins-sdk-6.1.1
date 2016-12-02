package my.ep.web.backingbean.sm.outputmsg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.outputmsg.SmOutputMsgManagedBean;
import my.ep.web.util.FacesScopes;

@ManagedBean(name = "smOutputMsgBackingBean")
@RequestScoped
public class SmOutputMsgBackingBean extends
		BaseBackingBean<SmOutputMsgManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5000622483921275922L;

	/**
	 * Print Payment Acknowledgment
	 * 
	 * @throws IOException
	 */
	public void printPaymentAck() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLID", getManagedBean().getSmAppl().getApplId()
				.toString());

		// always locale as MY
		Locale loc = new Locale("ms", "MY");
		String suppDocMode = ResourceBundleUtils.getMessage(
				"lbl.sm.suppDoc.offline",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
		if (getManagedBean().getSmAppl().getSupportingDocMode()
				.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			suppDocMode = ResourceBundleUtils.getMessage(
					"lbl.sm.suppDoc.online",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
		}
		paramMap.put("PDOC", suppDocMode);
		generateReport(ReportConst.SM_SUPPLIER_PAYMENT_ACK_REPORT, paramMap);
	}

	/**
	 * Print Payment Details
	 * 
	 * @throws IOException
	 */
	public void printPaymentDetails() throws IOException {
		SmPersonnel loginPersonnel = null;
		PmUser loginUser = new FacesScopes().getUserSessionBean();
		if (loginUser != null && loginUser.getUserId() != null) {
			for (SmPersonnel per : getManagedBean().getSmAppl()
					.getSmPersonnels()) {
				if (per.getPmUser() != null
						&& per.getPmUser().getUserId() != null
						&& per.getPmUser().getUserId()
								.compareTo(loginUser.getUserId()) == 0) {
					loginPersonnel = per;
					break;
				}
			}
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLID", getManagedBean().getSmAppl().getApplId()
				.toString());
		paramMap.put("PNAME", loginPersonnel == null ? StringUtils.EMPTY
				: loginPersonnel.getName());
		paramMap.put("PPOSITION", loginPersonnel == null ? StringUtils.EMPTY
				: loginPersonnel.getDesignation());
		generateReport(ReportConst.SM_SUPPLIER_PAYMENT_DETAILS_REPORT, paramMap);
	}

	/**
	 * Print Supplier Profile
	 * 
	 * @throws IOException
	 */
	public void printSupplierProfile() throws IOException {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PAPPLNO", getManagedBean().getSmAppl().getApplNo());
		generateReport(ReportConst.SM_SUPPLIER_UNREGIS_REPORT, paramMap);
	}

}
