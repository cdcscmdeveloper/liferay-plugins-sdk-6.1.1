package my.ep.web.util.sm.helper;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DateUtils;

import org.apache.log4j.Logger;

public class SmApplicationNotificationUtil {
	private static Logger log = Logger
			.getLogger(SmApplicationNotificationUtil.class);

	public static void setApprovalNotificationParameters(
			SmApplicationManagedBean managedBean, ReviewApplicationReqDTO reqDTO) {
		reqDTO.setBusinessRegNo(managedBean.getSmSupplier().getRegNo());
		reqDTO.setCompanyName(managedBean.getSmSupplier().getCompanyName());
		if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| managedBean
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			reqDTO.setPaymentMethods(" --- TO BE CONFIRMED ON THE PAYMENT LIST DETAILS ---");
			reqDTO.setCategoryDescription(retriveCategoryMessageForNotification(managedBean));
		} else if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			reqDTO.setCategoryDescription(retriveCancelRegistrationMessageForNotification(managedBean));
			// reqDTO.setMofNum(managedBean.getBasicInfo().getMofNo());
		}
		reqDTO.setNotifLink("<a href=\"https://www.google.com.my\"> Sila Klik </a>");

	}

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

	}

	public static String retriveCancelRegistrationMessageForNotification(
			SmApplicationManagedBean managedBean) {
		StringBuffer sb = new StringBuffer();
		log.info("*********** retriveCancelRegistrationMessageForNotification *********");
		sb.append("<table style=\"width:100%; border: 1px solid black;border-collapse: collapse;\">");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol1",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(DateUtils.dateToString("dd-mmm-yyyy", managedBean
						.getSmAppl().getApplSubmitDate())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol2",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(managedBean.getSmAppl().getApplNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol3",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(managedBean.getBasicInfo().getMofNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol4",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(managedBean.getSmSupplier().getCompanyName())
				.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol5",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(AddressUtils.getFullAddress(managedBean.getBasicInfo()
						.getAddress().getSmAddress())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol6",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(managedBean.getSupplierRemarks()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage("lbl.sm.cancelMof.notifCol7",
						null)).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\"> : </td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(managedBean.getApplnApprovalRemarks()).append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();

	}

	public static String retriveCategoryMessageForNotification(
			SmApplicationManagedBean managedBean) {
		StringBuffer approvedCatCodes = new StringBuffer();
		StringBuffer rejectedCatCodes = new StringBuffer();
		StringBuilder finalMsg = new StringBuilder();
		boolean existsRejectedCodes = false;
		rejectedCatCodes.append(SmCommonUtil.getMessage(
				"lbl.sm.approval.categoryNotif1", null));
		rejectedCatCodes
				.append("<table style=\"width:100%; border: 1px solid black;border-collapse: collapse;\">");
		rejectedCatCodes.append("<tr>");
		rejectedCatCodes
				.append("<th style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage(
						"lbl.sm.approval.categoryNotif2", null))
				.append("</th>");
		rejectedCatCodes
				.append("<th style=\"border: 1px solid black;\">")
				.append(SmCommonUtil.getMessage(
						"lbl.sm.approval.categoryNotif3", null))
				.append("</th>");
		rejectedCatCodes.append("</tr>");
		int i = 0;
		int j = 0;
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				approvedCatCodes.append("2.1.").append(++i + ".) ")
						.append(cat.getPmCategoryL1().getCategoryL1Code())
						.append(cat.getPmCategoryL2().getCategoryL2Code())
						.append(cat.getPmCategoryL3().getCategoryL3Code())
						.append("-")
						.append(cat.getPmCategoryL3().getCategoryName())
						.append("<br/>");
			} else if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.RECORD_STATUS_SUSPENDED) == 0) {
				existsRejectedCodes = true;
				rejectedCatCodes.append("<tr>");
				rejectedCatCodes
						.append("<td style=\"border: 1px solid black;\">")
						.append("2.2.").append(++j + ".) ")
						.append(cat.getPmCategoryL1().getCategoryL1Code())
						.append(cat.getPmCategoryL2().getCategoryL2Code())
						.append(cat.getPmCategoryL3().getCategoryL3Code())
						.append("-")
						.append(cat.getPmCategoryL3().getCategoryName())
						.append("</td>");
				rejectedCatCodes
						.append("<td style=\"border: 1px solid black;\">");
				if (managedBean.isPoUser()) {
					rejectedCatCodes.append(cat.getPoRemarks()).append("</td>");
				} else {
					rejectedCatCodes.append(cat.getApproverRemarks()).append(
							"</td>");
				}
				rejectedCatCodes.append("</tr>");
			}

		}

		rejectedCatCodes.append("</table>");
		finalMsg.append("<p style=\"margin-left: 20px;\">")
				.append(approvedCatCodes.toString()).append("</p>");
		if (existsRejectedCodes) {
			finalMsg.append("<div style=\"margin-left: 20px;\">")
					.append(rejectedCatCodes.toString()).append("</div>");
		}
		if (finalMsg.toString() == null) {
			finalMsg.append("");
		}
		return finalMsg.toString();
	}

}
