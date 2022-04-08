package my.ep.web.util.sm.helper;

//10908
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
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
		//#29782 - START
		if (smAppl.getApplType().equalsIgnoreCase( 
				ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				|| smAppl.getApplType().equalsIgnoreCase( 
						ParameterConst.NEW_SM_APPLN_FOR_BUMI)
						|| smAppl.getApplType().equalsIgnoreCase( 
								ParameterConst.NEW_SM_APPLN_FOR_UPDATE)
								|| smAppl.getApplType().equalsIgnoreCase( 
										ParameterConst.NEW_SM_APPLN_FOR_RENEW)
										|| smAppl.getApplType().equalsIgnoreCase( 
												ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			reqDTO.setCategoryDescription(retriveAddNewCategoryMessageForNotification( 
					smAppl, supplier, basicInfo, supplierRemarks, approvalRemarks, reqDTO)); 
		//#29782 - END
		}else if ((smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL))) {
			reqDTO.setCategoryDescription(retriveCancelRegistrationMessageForNotification(
					smAppl, supplier, basicInfo, supplierRemarks, approvalRemarks, reqDTO));
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
		reqDTO.setNotifLink("");

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
	
	//10908 - change to be same as approve 
	public static void setRejectionNotificationParameters(SmAppl smAppl,
			SmSupplier supplier, SmSupplierBasicInfoVO basicInfo, 
			String supplierRemarks, String approvalRemarks,
			ReviewApplicationReqDTO reqDTO) {
		reqDTO.setBusinessRegNo(supplier.getRegNo());
		reqDTO.setCompanyName(supplier.getCompanyName());
		
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
			reqDTO.setCategoryDescription(retriveRejectCancelRegistrationMessageForNotification(
					smAppl, supplier, basicInfo, supplierRemarks, approvalRemarks, reqDTO));
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
	//10908 - add approved date
	public static String retriveCancelRegistrationMessageForNotification(
			SmAppl smAppl, SmSupplier supplier,
			SmSupplierBasicInfoVO basicInfo, 
			String supplierRemarks, String approvalRemarks, ReviewApplicationReqDTO reqDTO) {
		String preferredLang =CommonConstants.LANGUAGE_CODE_MALAY;
		log.info(preferredLang+"---reqDTO.getLangCode()>>>>>>>>>>" + reqDTO.getLangCode());
		
		if(reqDTO.getLangCode()!=null && !"".equalsIgnoreCase(reqDTO.getLangCode())){
			preferredLang = reqDTO.getLangCode();
		}
		Locale usrLocale = getLocaleByLang(preferredLang);
		
		log.info("preferredLang>>>>>>>>>>" + preferredLang);
		log.info("User Locale>>>>>>>>>>" + usrLocale.getLanguage());
		
		String submitDate = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol1"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("submitDate>>>>>>>>>>>>>>>>>>"+submitDate);
		String applNo = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol2"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String approveDate = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol9"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String mofNo = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol3"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String companyName = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol4"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String companyAdd = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol5"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		//10908
		String suppRemarks = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol12"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String appRemarks = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol7"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		StringBuilder sb = new StringBuilder();
		log.info("*********** retriveCancelRegistrationMessageForNotification *********");
		sb.append("<table style=\"width:50%; border: 1px solid black;border-collapse: collapse;\">");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(submitDate).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(DateUtils.dateToString("dd-MMM-yyyy",
						smAppl.getApplSubmitDate())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(applNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(smAppl.getApplNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(approveDate).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(sdf.format(new Date())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(mofNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(basicInfo.getMofNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(companyName).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getCompanyName()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(companyAdd).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(AddressUtils.getFullAddress(basicInfo.getAddress()
						.getSmAddress())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(suppRemarks).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplierRemarks).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(appRemarks).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(approvalRemarks).append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	//10908 
	public static String retriveRejectCancelRegistrationMessageForNotification(
			SmAppl smAppl, SmSupplier supplier,
			SmSupplierBasicInfoVO basicInfo, 
			String supplierRemarks, String approvalRemarks, ReviewApplicationReqDTO reqDTO) {
		String preferredLang =CommonConstants.LANGUAGE_CODE_MALAY;
		log.info(preferredLang+"---reqDTO.getLangCode()>>>>>>>>>>" + reqDTO.getLangCode());
		
		if(reqDTO.getLangCode()!=null && !"".equalsIgnoreCase(reqDTO.getLangCode())){
			preferredLang = reqDTO.getLangCode();
		}
		Locale usrLocale = getLocaleByLang(preferredLang);
		
		log.info("preferredLang>>>>>>>>>>" + preferredLang);
		log.info("User Locale>>>>>>>>>>" + usrLocale.getLanguage());
		
		String submitDate = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol1"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("submitDate>>>>>>>>>>>>>>>>>>"+submitDate);
		String applNo = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol2"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String rejectDate = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol8"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String mofNo = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol3"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String companyName = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol4"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String regNo = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol10"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String companyAdd = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol5"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String suppRemarks = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol6"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		String appRemarks = ResourceBundleUtils.getMessage("lbl.sm.cancelMof.notifCol11"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		StringBuilder sb = new StringBuilder();
		log.info("*********** retriveRejectCancelRegistrationMessageForNotification *********");
		sb.append("<table style=\"width:50%; border: 1px solid black;border-collapse: collapse;\">");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(submitDate).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(DateUtils.dateToString("dd-MMM-yyyy",
						smAppl.getApplSubmitDate())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(applNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(smAppl.getApplNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(rejectDate).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(sdf.format(new Date())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(mofNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(basicInfo.getMofNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(companyName).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getCompanyName()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(regNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getRegNo()).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(companyAdd).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(AddressUtils.getFullAddress(basicInfo.getAddress()
						.getSmAddress())).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(suppRemarks).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplierRemarks).append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(appRemarks).append("</td>");
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

	private static String getUserPreferredLanguage(String loginId){
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(loginId);
		PmUser user = SmServiceLookup.getUserSO().retrieveUserByLoginId(reqDTO).getPmUser();
		String lang = "en";
		if(user != null){
			lang = user.getPreferredLanguage();
		}
		
		return null;
	}
	
	private static Locale getLocaleByLang(String lang){
		if(StringUtils.isNotBlank(lang)){
			if(lang.equalsIgnoreCase(CommonConstants.LANGUAGE_CODE_MALAY)){
				return new Locale(CommonConstants.LANGUAGE_CODE_MALAY,CommonConstants.DEFAULT_COUNTRY_CODE);
			}else{
				return new Locale(CommonConstants.DEFAULT_LANGUAGE_CODE);
			}
		}else{
			//return new Locale(CommonConstants.LANGUAGE_CODE_MALAY,CommonConstants.DEFAULT_COUNTRY_CODE);
			return new Locale(CommonConstants.DEFAULT_LANGUAGE_CODE);
		}
	}
	
	//	#29782 - START
	/**
	 * Set the tabular message for Add New Category message for
	 * notification.This will be passed as a parameter for approval notification
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * @return String - notification message or table content
	 */
	//29782 - Add New Category
	public static String retriveAddNewCategoryMessageForNotification(
			SmAppl smAppl, SmSupplier supplier,
			SmSupplierBasicInfoVO basicInfo, 
			String supplierRemarks, String approvalRemarks, ReviewApplicationReqDTO reqDTO) {
		String preferredLang =CommonConstants.LANGUAGE_CODE_MALAY;
		
		if(reqDTO.getLangCode()!=null && !"".equalsIgnoreCase(reqDTO.getLangCode())){
			preferredLang = reqDTO.getLangCode();
		}
		Locale usrLocale = getLocaleByLang(preferredLang);
		
		log.info("preferredLang>>>>>>>>>>" + preferredLang);
		log.info("User Locale>>>>>>>>>>" + usrLocale.getLanguage());
		

		String applType = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol1"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("applType>>>>>>>>>>>>>>>>>>"+applType);
		String applNo = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol2"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("applNo>>>>>>>>>>>>>>>>>>"+applNo);
		String companyName = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol3"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("companyName>>>>>>>>>>>>>>>>>>"+companyName);
		String regNo = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol4"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("regNo>>>>>>>>>>>>>>>>>>"+regNo);
		String epNo = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol5"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("epNo>>>>>>>>>>>>>>>>>>"+epNo);
		String approveDate = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol6"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("approveDate>>>>>>>>>>>>>>>>>>"+approveDate);
		String addNewCatCodes = ResourceBundleUtils.getMessage("lbl.sm.addNewCategory.notifCol7"
				,ResourceBundleUtils.BUNDLE_COMMON_LABELS, usrLocale);
		log.info("addNewCatCodes>>>>>>>>>>>>>>>>>>"+addNewCatCodes);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		StringBuilder sb = new StringBuilder();
		log.info("*********** retriveAddNewCategoryMessageForNotification *********");
		sb.append("<table style=\"width:50%; border: 1px solid black;border-collapse: collapse;\">");
		// <VAR1>
		//Company Name 
		sb.append("<tr>"); 
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(companyName).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getCompanyName()).append("</td>");
		sb.append("</tr>");
		log.info("*********** Company Name ********* :"+supplier.getCompanyName());
		//Business Registration No. 
		sb.append("<tr>"); 
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(regNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getRegNo()).append("</td>");
		sb.append("</tr>");
		log.info("*********** Business Registration No. ********* :"+supplier.getRegNo());
		//eP No.
		sb.append("<tr>"); 
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(epNo).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(supplier.getEpNo()).append("</td>");
		sb.append("</tr>");
		log.info("*********** eP No. ********* :"+supplier.getEpNo());
		//Approved Date
		sb.append("<tr>"); 
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(approveDate).append("</td>");
		sb.append("<td style=\"border: 1px solid black;\">")
				.append(sdf.format(new Date())).append("</td>");
		sb.append("</tr>");
		log.info("*********** Approved Date ********* :"+sdf.format(new Date()));
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	//29782 - END
	
}
