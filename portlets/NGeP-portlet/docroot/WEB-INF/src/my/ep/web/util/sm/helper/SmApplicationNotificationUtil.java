package my.ep.web.util.sm.helper;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.vo.SmSupplierBasicInfoVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
public class SmApplicationNotificationUtil {
	private static Logger log = Logger
			.getLogger(SmApplicationNotificationUtil.class);

	/**
	 * Sets the approval notification parameters to be set in the
	 * ReviewApplicationDTO and pass it to the SO level.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * @param reqDTO
	 *            - ReviewApplicationReqDTO
	 */
	public static void setApprovalNotificationParameters(SmAppl smAppl,
			SmSupplierBasicInfoVO basicInfo, boolean isPoUser,
			SmSupplier supplier, String supplierRemarks,
			String approvalRemarks, ReviewApplicationReqDTO reqDTO) {
		reqDTO.setBusinessRegNo(supplier.getRegNo());
		reqDTO.setCompanyName(supplier.getCompanyName());
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
			reqDTO.setPaymentMethods(" --- TO BE CONFIRMED ON THE PAYMENT LIST DETAILS ---");
			reqDTO.setCategoryDescription(retriveCategoryMessageForNotification(
					smAppl, isPoUser));
		} else if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			reqDTO.setCategoryDescription(retriveCancelRegistrationMessageForNotification(
					smAppl, supplier, basicInfo, approvalRemarks,
					supplierRemarks));
			// reqDTO.setMofNum(managedBean.getBasicInfo().getMofNo());
		}
		if (!StringUtils.isEmpty(reqDTO.getLocation())) {
			if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SABAH)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sabah", null));
			} else if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SARAWAK)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sarawak", null));
			} else {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.semunanjung", null));
			}
		}
		reqDTO.setNotifLink("<a href=\"https://www.google.com.my\"> di sini </a>");

	}

	/**
	 * Sets the rejection notification parameters to be set in the
	 * ReviewApplicationDTO and pass it to the SO level.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * @param reqDTO
	 *            - ReviewApplicationReqDTO
	 */
	public static void setRejectionNotificationParameters(
			SmApplicationManagedBean managedBean, ReviewApplicationReqDTO reqDTO) {
		if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			reqDTO.setBusinessRegNo(managedBean.getSmSupplier().getRegNo());
			reqDTO.setCompanyName(managedBean.getSmSupplier().getCompanyName());
			reqDTO.setApproverOrPoRemarks(managedBean
					.getApplnRejectionRemarks());
			reqDTO.setMofNum(managedBean.getBasicInfo().getMofNo());
		}
		if (!StringUtils.isEmpty(reqDTO.getLocation())) {
			if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SABAH)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sabah", null));
			} else if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SARAWAK)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.sarawak", null));
			} else {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessage(
						"lbl.sm.notifAddr.semunanjung", null));
			}
		}

	}

	/**
	 * Set the tabular message for cancel registration message for
	 * notification.This will be passed as a parameter for approval notification
	 * of Cancel Registration with MOF
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * @return String - notification message or table content
	 */
	public static String retriveCancelRegistrationMessageForNotification(
			SmAppl smAppl, SmSupplier supplier,
			SmSupplierBasicInfoVO basicInfo, String approvalRemarks,
			String supplierRemarks) {
		StringBuilder sb = new StringBuilder();
		log.info("*********** retriveCancelRegistrationMessageForNotification *********");
		sb.append("<table style=\"width:100%; border: 1px solid black;border-collapse: collapse;\">");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol1",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(DateUtils.dateToString("dd-MMM-yyyy",
						smAppl.getApplSubmitDate())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol2",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(smAppl.getApplNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol3",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(basicInfo.getMofNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol4",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getCompanyName()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol5",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(AddressUtils.getFullAddress(basicInfo.getAddress()
						.getSmAddress())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol6",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplierRemarks).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol7",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(approvalRemarks).append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();

	}

	/**
	 * Set the tabular message for approval of registration or maintenance
	 * application.This will be passed as a parameter for approval notification
	 * of registration or maintenance application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * @return String - notification message or table content
	 */
	public static String retriveCategoryMessageForNotification(SmAppl smAppl,
			boolean isPoUser) {
		StringBuilder approvedCatCodes = new StringBuilder();
		StringBuilder rejectedCatCodes = new StringBuilder();
		StringBuilder finalMsg = new StringBuilder();
		boolean existsRejectedCodes = false;
		rejectedCatCodes.append(SmCommonUtil.getMessage(
				"lbl.sm.approval.categoryNotif1", null));
		rejectedCatCodes
				.append("<table style='width:100%; border: 1px solid black;border-collapse: collapse;'>");
		rejectedCatCodes.append("<tr>");
		rejectedCatCodes
				.append("<th style='border: 1px solid black;'>")
				.append(SmCommonUtil.getMessage(
						"lbl.sm.approval.categoryNotif2", null))
				.append("</th>");
		rejectedCatCodes
				.append("<th style='border: 1px solid black;'>")
				.append(SmCommonUtil.getMessage(
						"lbl.sm.approval.categoryNotif3", null))
				.append("</th>");
		rejectedCatCodes.append("</tr>");
		int i = 0;
		int j = 0;
		for (SmSupplierCategory cat : smAppl.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				approvedCatCodes.append("<br/>");
				approvedCatCodes.append("2.1.").append(++i + ") ")
						.append(cat.getPmCategoryL1().getCategoryL1Code())
						.append(cat.getPmCategoryL2().getCategoryL2Code())
						.append(cat.getPmCategoryL3().getCategoryL3Code())
						.append("-")
						.append(cat.getPmCategoryL3().getCategoryName());
			} else if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.RECORD_STATUS_SUSPENDED) == 0) {
				existsRejectedCodes = true;
				rejectedCatCodes.append("<tr>");
				rejectedCatCodes
						.append("<td style='border: 1px solid black;'>")
						.append("2.2.").append(++j + ") ")
						.append(cat.getPmCategoryL1().getCategoryL1Code())
						.append(cat.getPmCategoryL2().getCategoryL2Code())
						.append(cat.getPmCategoryL3().getCategoryL3Code())
						.append("-")
						.append(cat.getPmCategoryL3().getCategoryName())
						.append("</td>");
				rejectedCatCodes
						.append("<td style='border: 1px solid black;'>");
				if (isPoUser) {
					rejectedCatCodes.append(cat.getPoRemarks()).append("</td>");
				} else {
					rejectedCatCodes.append(cat.getApproverRemarks()).append(
							"</td>");
				}
				rejectedCatCodes.append("</tr>");
			}

		}

		rejectedCatCodes.append("</table>");
		finalMsg.append("<p style='padding-left: 2em;text-indent: -2em;'>")
				.append(approvedCatCodes.toString()).append("</p>");
		if (existsRejectedCodes) {
			finalMsg.append("<div style='margin-left: 20px;'>")
					.append(rejectedCatCodes.toString()).append("</div>");
		}
		if (finalMsg.toString() == null) {
			finalMsg.append("");
		}
		return finalMsg.toString();
	}

}
