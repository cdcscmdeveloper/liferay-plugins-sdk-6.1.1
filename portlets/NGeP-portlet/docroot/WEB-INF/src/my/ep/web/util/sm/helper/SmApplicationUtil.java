package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.race.IRaceSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.race.RaceResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

public class SmApplicationUtil {
	private static Logger log = Logger.getLogger(SmApplicationUtil.class);

	public static boolean validatePersonLimit(Long supplierId) {
		boolean validPerCount = true;
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelResDTO resDTO = smPersonnelSO
				.retrievePersonnelCountForSuppUserMaint(reqDTO);
		if (resDTO.getSmPersonnelList() != null
				&& resDTO.getSmPersonnelList().size() < 5) {
			validPerCount = true;
		} else {
			validPerCount = false;
		}
		return validPerCount;
	}

	public static boolean validateSupportingDocs(List<SmApplSupportDoc> suppDocs) {
		boolean validSupportDocs = true;
		int i = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("<br/>");
		for (SmApplSupportDoc doc : suppDocs) {
			if (doc.getSmApplDocFiles().get(0).getFileName()
					.equalsIgnoreCase(" ")
					|| doc.getSmApplDocFiles().get(0).getFilePath()
							.equalsIgnoreCase(" ")) {
				sb.append(++i + ".)");
				sb.append(doc.getPmDocument().getDocName());
				// sb.append(",  ");
				sb.append("<br/>");
				validSupportDocs = false;
			}
		}
		if (!validSupportDocs) {
			throw new EpException(MessageConst.ERROR_722537,
					new String[] { sb.toString() });
		}
		return validSupportDocs;
	}

	public static SmSupplier getCurrentSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			if (pmUser == null
					|| (pmUser != null && pmUser.getUserId() == null)) {
				System.out
						.println("**** Invalid user from the session scope ******");
				throw new EpException(MessageConst.ERROR_721017);
			}
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(pmUser.getLoginId());
			System.out.println("************Supplier PmUser Login Id:"
					+ pmUser.getLoginId());
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IUserSO.class);
			// UserResDTO userResDTO = userSO.isUserExist(userReqDTO);
			// if (userResDTO == null) {
			// throw new EpException(MessageConst.ERROR_721018);
			// } else {
			// if (userResDTO.getAllResult() != null
			// && !userResDTO.getAllResult().isEmpty()) {
			// if (userResDTO.getAllResult().get(0).length > 2) {
			// log.debug("******** Pm Profile  : "
			// + userResDTO.getAllResult().get(0)[0]);
			// log.debug("******** Liferay Profile  : "
			// + userResDTO.getAllResult().get(0)[1]);
			// log.debug("******** LDAP Profile  : "
			// + userResDTO.getAllResult().get(0)[2]);
			// }
			// }
			// if (!userResDTO.isExist()) {
			// log.debug("**** User doesn't exist any of the LDAP/PM/Liferay ******");
			// throw new EpException(MessageConst.ERROR_721018);
			// } else {
			// log.debug("**** To Check if user not exists in either of the LDAP/PM/Liferay******");
			// if (userResDTO.getAllResult().get(0)[0].startsWith("NO")
			// || userResDTO.getAllResult().get(0)[1]
			// .startsWith("NO")
			// || userResDTO.getAllResult().get(0)[2]
			// .startsWith("NO")) {
			// log.debug("**** This user is not configured in one or more of the LDAP/PM/Liferay places *****");
			// }
			// }
			// }

			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO = supplierSearchSO
					.retriveSupplierProfiles(supplierSearchReqDTO);
			if (smResDTO.getResultBean().getResultList() != null
					&& smResDTO.getResultBean().getResultList().size() > 0) {
				smSupp = (SmSupplier) smResDTO.getResultBean().getResultList()
						.get(0);
				log.debug("*************Current Supplier: "
						+ smSupp.getSupplierId());
			} else {
				log.debug("**** No valid supplier record found for this user ******");
				throw new EpException(MessageConst.ERROR_721018);
			}
			return smSupp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_721017);
		}

	}

	public static String getReviewApplicationBasePage(String applicationType) {
		String basePage = "";
		if (applicationType != null) {
			switch (applicationType) {
			case ParameterConst.NEW_SM_APPLN_FOR_MOF:
				basePage = "/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_BasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES:
				basePage = "/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_BasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_BUMI:
				basePage = "/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_BasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_UPDATE:
				basePage = "/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_BasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_RENEW:
				basePage = "/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_BasePage.xhtml";
				break;
			default:
				break;
			}
		}
		return basePage;
	}

	public static StatusResDTO setApplPmStatus(String statusId) {
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(Long.parseLong(statusId));
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusResDTO resDTO = reqSO.retrieveStatusByStatusId(reqDTO);
		return resDTO;
		// resDTO = reqSO.retrieveStatusDescById(reqDTO);
		// if (resDTO != null && resDTO.getPmStatusDesc() != null) {
		// managedBean.setCurrentStatus(
		// resDTO.getPmStatusDesc().getStatusName());
		// }
	}

	public static String assignSupplierBusinessType(SmSupplier sup) {
		if (sup.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT;
		} else if (sup
				.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT;

		} else if (sup
				.getBusinessType()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT;
		} else if (sup.getBusinessType().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_SDN_BHD)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_BHD)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT;
		} else if (sup.getBusinessType().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP)) {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT;
		} else {
			return SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_UNKNOWN;
		}
	}

	public static boolean manageEquityLimit(SmPersonnel personnel,
			String businessTypeVal, List<SmPersonnel> shrHldrs) {
		if (personnel.isEquityOwner()
				&& !businessTypeVal
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
			return true;
		} else if (businessTypeVal
				.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
				|| (businessTypeVal
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT) && shrHldrs
						.size() >= 1)
				|| (businessTypeVal
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT) && shrHldrs
						.size() >= 20)) {
			return false;

		} else {
			return true;
		}
	}

	public static void setNavigationForResubmitAppln(String applType) {
		FacesContext fc = FacesContext.getCurrentInstance();

		switch (applType) {
		case "N":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
					// "/xhtml/sm/NewMofApplication/smMofRegSideMenu_BasePage.xhtml");
							"/xhtml/sm/NewMofApplication/mofRegSmUser_BaseWizardPage.xhtml");
			break;
		case "A":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/addAdditionalCategoryCodes/smAddtnlCatApplnSideMenu_BasePage.xhtml");
			break;
		case "B":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/bumiPuteraApplication/smBumiSideMenu_BasePage.xhtml");
			break;
		case "U":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/updateMofDetails/smUpdateMofSideMenu_BasePage.xhtml");
			break;
		case "R":
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							"/xhtml/sm/renewMofApplication/smRenewRegSideMenu_BasePage.xhtml");
			break;
		default:
			log.debug("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
					+ applType);
			break;
		}
	}

	public static boolean validateIctCatCode(SmAppl appl,
			SmSupplierCategory category) {
		boolean validCat = true;
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			int prfCnt = 0;
			int semPrfCnt = 0;
			for (SmPersonnel per : appl.getSmPersonnels()) {
				if (per.isProf() && per.getWorkExperienceYear() >= 5) {
					prfCnt = prfCnt + 1;
				} else if (per.isSemiProf() && per.getWorkExperienceYear() >= 5) {
					semPrfCnt = semPrfCnt + 1;
				}
				if (prfCnt > 0 && semPrfCnt > 0) {
					break;
				}
			}
			if (semPrfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722511,
						new String[] { "semi professional staff" });
			}
			if (prfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722511,
						new String[] { "professional staff" });
			}

		}

		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONTRACTOR)) {
			log.debug("***** Validate ICT For contractor ********");

			int ownrCnt = 0;
			int stfCnt = 0;
			int dirCnt = 0;
			for (SmPersonnel per : appl.getSmPersonnels()) {

				if (per.isEquityOwner() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					ownrCnt = ownrCnt + 1;

				} else if (per.isDirector() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					dirCnt = dirCnt + 1;

				} else if (per.isClerical() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					stfCnt = stfCnt + 1;
				}
				if (stfCnt > 0 && dirCnt > 0 && ownrCnt > 0) {
					break;
				}
			}
			if (stfCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "staff" });
			}
			if (ownrCnt < 1) {
				log.debug("****** No OWNER ********** ");
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "equity owner" });
			}
			if (dirCnt < 1) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722512,
						new String[] { "director" });
			}

		}
		if (validCat) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (category.getPmCategoryL3().getCategoryL3Id() != 99
						&& category.getPmCategoryL3().getCategoryL3Id() == cat
								.getPmCategoryL3().getCategoryL3Id()
						&& category.getPmCategoryL2().getCategoryL2Id() == cat
								.getPmCategoryL2().getCategoryL2Id()
						&& category.getPmCategoryL1().getCategoryL1Id() == cat
								.getPmCategoryL1().getCategoryL1Id()) {
					validCat = false;
					throw new EpException(MessageConst.ERROR_722525);
					// break;

				}
			}
		}

		return validCat;

	}

	public static int assignMaxReviewCount(String appType, String applCatType,
			String supplierType) {
		int i = 0;
		switch (appType) {

		case "N":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.NEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.NEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "A":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.ADDLN_CATCODES_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "B":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.BUMI_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.BUMI_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		case "U":
			i = SupplierManagementConst.UPDATE_MOF_APPL_REVIEW_MAXCOUNT;
			break;
		case "R":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			}
			break;
		default:
			break;

		}
		if (supplierType
				.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
			i = i + 1;
		}
		return i;
	}

	public static boolean validateBasicInfoBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		if (appl.getSmFacilities().isEmpty()) {
			appl.getSmFacilities().add(managedBean.getBasicCompanyFacility());

		} else {
			// TODO
			for (int i = 0; i < appl.getSmFacilities().size(); i++) {
				if (appl.getSmFacilities().get(i).getFacilityType()
						.equalsIgnoreCase("B")
						|| appl.getSmFacilities().get(i).getFacilityType()
								.equalsIgnoreCase("H")) {
					appl.getSmFacilities().set(i,
							managedBean.getBasicCompanyFacility());
					break;
				}
			}
		}
		int validStfCont = 0;
		for (SmStaffTotal stfTl : appl.getSmStaffTotals()) {
			if (stfTl.getTotalBumi().compareTo(new BigDecimal(0)) > 0
					|| stfTl.getTotalForeigner().compareTo(new BigDecimal(0)) > 0
					|| stfTl.getTotalNonBumi().compareTo(new BigDecimal(0)) > 0) {
				validStfCont = validStfCont + 1;
				break;
			}
		}
		if (validStfCont == 0 && !appl.getApplType().equalsIgnoreCase("C")) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722526);
		}
		appl = null;
		return validSave;

	}

	public static boolean validateBankBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		int payAccCnt = 0;
		int regAccCnt = 0;
		int designatedAccCnt = 0;

		for (SmSupplierBank bank : appl.getSmSupplierBanks()) {
			if (bank.getAccountPurpose().equalsIgnoreCase("P")) {
				payAccCnt = payAccCnt + 1;
			} else if (bank.getAccountPurpose().equalsIgnoreCase("R")) {
				regAccCnt = regAccCnt + 1;
			} else if (bank.getAccountPurpose().equalsIgnoreCase("B")) {
				payAccCnt = payAccCnt + 1;
				regAccCnt = regAccCnt + 1;
			}
			if (payAccCnt > 0 && regAccCnt > 0) {
				break;
			}
		}
		if (payAccCnt == 0) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722515);
		}
		if (regAccCnt == 0) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722544);
		}
		managedBean.setSelectedSignatoryBankAcct(new SmSupplierBank());
		appl = null;
		return validSave;
	}

	public static boolean validateCategoriesBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		if (appl.getSmSupplierCategories() != null
				&& appl.getSmSupplierCategories().size() > 0) {
			for (SmSupplierCategory category : appl.getSmSupplierCategories()) {
				if (category.getPmCategoryL3().getCategoryL3Code()
						.equalsIgnoreCase("99")) {
					if (category.getOtherDesc() != null
							&& category.getOtherDesc().isEmpty()) {
						validSave = false;
						setNavigationBackToCurrentPage(currentTab, managedBean);
						throw new EpException(MessageConst.ERROR_722514);
					}
				}
			}
		}

		if (managedBean.isSupplierUser()) {
			if (appl.getSmSupplierCategories() == null
					|| appl.getSmSupplierCategories().isEmpty()) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722516);
			}

		}
		appl = null;
		return validSave;
	}

	public static boolean validatePersonnelBeforeSave(boolean validSave,
			int currentTab, SmApplicationManagedBean managedBean) {
		SmAppl appl = managedBean.getSmAppl();
		int mgmtCount = 0;
		int staffCount = 0;
		int directorCount = 0;
		int profCount = 0;
		int semiProfCount = 0;
		for (SmPersonnel per : appl.getSmPersonnels()) {

			if (per.isDirector()) {
				directorCount = directorCount + 1;
			}

			if (per.isManagement()) {
				mgmtCount = mgmtCount + 1;
			}
			if (appl.getSupplierType().equalsIgnoreCase(
					SupplierManagementConst.STYPE_CONTRACTOR)) {
				if (per.isClerical()) {
					staffCount = staffCount + 1;
				}
			}
			if (appl.getSupplierType().equalsIgnoreCase(
					SupplierManagementConst.STYPE_CONSULTANT)) {
				if (per.isClerical() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
						PmParameter param = retrievePmParameterById(aca
								.getQualificationLevelId().longValue());
						String qual = "";
						if (param != null && param.getParameterCode() != null) {
							qual = param.getParameterCode();
							if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_NOTDEFINED)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_CERTIFIED)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_DIPLOMA)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_BACHELORS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_MASTERS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_PHD)) {
								staffCount = staffCount + 1;
								break;
							}
						}
					}

				} else if (per.isProf() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
						PmParameter param = retrievePmParameterById(aca
								.getQualificationLevelId().longValue());
						String qual = "";
						if (param != null && param.getParameterCode() != null) {
							qual = param.getParameterCode();
							if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_BACHELORS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_MASTERS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_PHD)) {
								profCount = profCount + 1;
								break;
							}
						}
					}

				} else if (per.isSemiProf() && per.getEpfNo() != null
						&& per.getEpfNo() != "") {
					for (SmStaffAcademic aca : per.getSmStaffAcademics()) {
						PmParameter param = retrievePmParameterById(aca
								.getQualificationLevelId().longValue());
						String qual = "";
						if (param != null && param.getParameterCode() != null) {
							qual = param.getParameterCode();
							if (qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_DIPLOMA)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_BACHELORS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_MASTERS)
									|| qual.equalsIgnoreCase(SupplierManagementConst.SUPPLIER_EDU_PHD)) {
								semiProfCount = semiProfCount + 1;
								break;
							}
						}
					}
				}
			}

		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			if (semiProfCount < 1) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722510, new String[] {
						"Semi-Professional", "Diploma" });
			}
			if (profCount < 1) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722510, new String[] {
						"Professional", "Bachelor Degree" });
			}
			if (staffCount < 1) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722510, new String[] {
						"Clerical Staff",
						"SijilPelajaran Malaysia or equivalent certificate" });
			}
		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONTRACTOR)) {
			if (staffCount < 1) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722513, new String[] {
						"Staff", "one(1)", "Staff" });
			}
		}
		if (managedBean.getBusinessTypeVal() != null
				&& (managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT) || managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT))
				&& directorCount < 1) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722513, new String[] {
					"Director", "one(1)", "Director" });
		} else if (managedBean.getBusinessTypeVal() != null
				&& managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
				&& directorCount < 2) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722513, new String[] {
					"Director", "two(2)", "Director" });
		}
		if (mgmtCount < 1) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722513, new String[] {
					"Management Staff", "one(1)", "Management Staff" });
		}
		appl = null;
		return validSave;
	}

	private static void setNavigationBackToCurrentPage(int currentTab,
			SmApplicationManagedBean managedBean) {
		managedBean.setTabActiveIndex(currentTab);
		managedBean.getSmAppl().setCurrentActiveTab(
				managedBean.getSmAppl().getCurrentActiveTab());
	}

	public static boolean validateEquityOwnerCount(
			SmApplicationManagedBean managedBean, int currentTab) {
		boolean validCount = true;
		if (managedBean.getBusinessTypeVal() != null
				&& (managedBean.getBusinessTypeVal()
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT))) {
			if (managedBean.getSmapplShareHolders() == null
					|| (managedBean.getSmapplShareHolders() != null && managedBean
							.getSmapplShareHolders().size() < 1)) {

				validCount = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722507);
			}
		} else if (managedBean.getBusinessTypeVal() != null
				&& !managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
				&& (managedBean.getSmapplShareHolders() == null || (managedBean
						.getSmapplShareHolders() != null && managedBean
						.getSmapplShareHolders().size() < 2))) {

			validCount = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722508);
		}
		if (managedBean.getBusinessTypeVal() != null
				&& !managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
				&& managedBean.getSmapplShareHolders() != null) {
			StringBuffer sb = new StringBuffer();
			boolean validShrHldrsCap = true;
			int i = 0;
			for (SmPersonnel per : managedBean.getSmapplShareHolders()) {
				if (per.getShareAmt() == null
						|| per.getShareAmt().compareTo(new BigDecimal(0)) == 0) {
					sb.append("<br/>");
					sb.append(++i + ".)");
					sb.append(per.getName());
					validShrHldrsCap = false;
				}
			}
			if (!validShrHldrsCap) {
				validCount = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722552,
						new String[] { sb.toString() });
			}
		}
		return validCount;
	}

	public static void validatePaidCaptialAmt(boolean setNavigationCurrentPage,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		if (appl != null && appl.getSmCompanyBasic() != null
				&& appl.getSmCompanyBasic().getPaidUpCapital() != null
				&& appl.getSmCompanyBasic().getAuthorizedCapital() != null
				&& appl.getSmCompanyBasic().getTotalShare() != null) {
			if ((managedBean
					.getBusinessTypeVal()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT) || managedBean
					.getBusinessTypeVal()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT))
					&& appl.getSmCompanyBasic()
							.getPaidUpCapital()
							.compareTo(
									appl.getSmCompanyBasic()
											.getAuthorizedCapital()) > 0) {
				if (setNavigationCurrentPage) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722523,
						new String[] { "Authorized Capital" });
			}
			if (managedBean
					.getBusinessTypeVal()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
					&& appl.getSmCompanyBasic()
							.getPaidUpCapital()
							.compareTo(appl.getSmCompanyBasic().getTotalShare()) != 0) {
				if (setNavigationCurrentPage) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722509,
						new String[] { "Total Share" });
			}
		}
		appl = null;
	}

	public static void validatePersonnelICandEmailLiferayAndPm(
			SmPersonnel smPersonnel) {
		if (isExistEmailInPmUser(smPersonnel.getEmail())) {
			throw new EpException(MessageConst.ERROR_722540);
		}
		if (isExistUserByIdentificationNo(smPersonnel.getIdentificationNo())) {
			throw new EpException(MessageConst.ERROR_722541);
		}
		try {
			if (UserLocalServiceUtil.getUserByEmailAddress(
					Long.valueOf(getCompanyId()), smPersonnel.getEmail()) != null) {
				throw new EpException(MessageConst.ERROR_722540);
			}
		} catch (NumberFormatException | PortalException | SystemException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}

		try {
			if (UserLocalServiceUtil.getUserByScreenName(
					Long.valueOf(getCompanyId()), smPersonnel.getEmail()) != null) {
				throw new EpException(MessageConst.ERROR_722541);
			}
		} catch (NumberFormatException | PortalException | SystemException e) {
			// TODO Auto-generated catch block
			log.info(e.getMessage());
		}
	}

	public static boolean isExistEmailInPmUser(String email) {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);

		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);

		log.debug("*********resDTO.getUserList().size()*****"
				+ resDTO.getUserList().size());
		if (resDTO.getUserList().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isExistUserByIdentificationNo(String idNo) {
		log.debug("*********retrieveUserByIdentificationNo**********" + idNo);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);

		if (resDTO.getPmUser() != null) {
			return true;
		} else {
			return false;
		}
	}

	public static String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.PORTAL_COMPANY_ID);
	}

	public static boolean checkValidUser(SmPersonnel pers, SmAppl appl) {
		boolean validUser = true;
		if (pers.getIdentificationType().getParameterCode()
				.equalsIgnoreCase("I")) {
			if (pers.getIdentificationNo().length() != 12) {
				validUser = false;
				throw new EpException(MessageConst.ERROR_722532,
						new String[] { pers.getIdentificationNo() });
			}
			myIdentityVerification(pers);
		}
		if (!pers.isEquityOwner() && !pers.isDirector()
				&& !pers.isHigherManagement() && !pers.isManagement()
				&& !pers.isProf() && !pers.isSemiProf() && !pers.isClerical()) {
			validUser = false;
			throw new EpException(MessageConst.ERROR_722535);
		}
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (pers.getPersonnelId() == null && pers.equals(per)) {
				System.out.println("****** Same obj before save ");
			} else if (pers.getPersonnelId() != null
					&& pers.getPersonnelId().equals(per.getPersonnelId())) {
				System.out.println("**** Same obj without id ******");
			} else {
				if (per.getIdentificationNo() != null
						&& per.getIdentificationNo().equalsIgnoreCase(
								pers.getIdentificationNo())) {
					validUser = false;
					throw new EpException(MessageConst.ERROR_722524,
							new String[] { pers.getIdentificationNo() });
				}
				if (per.getEmail() != null
						&& per.getEmail().equalsIgnoreCase(pers.getEmail())) {
					validUser = false;
					throw new EpException(MessageConst.ERROR_722540);
				}
			}
		}
		if (pers.getEpRole() != null
				&& !pers.getEpRole().isEmpty()
				&& (pers.getEpRole().equalsIgnoreCase(
						RoleCode.MOF_SUPPLIER_USER.value()) || pers.getEpRole()
						.equalsIgnoreCase(RoleCode.CM_USER.value())))
			validatePersonnelICandEmailLiferayAndPm(pers);
		return validUser;
	}

	public static void myIdentityVerification(SmPersonnel per) {
		String postCode = "";
		for (SmPersonnelAddress addr : per.getSmPersonnelAddresses()) {
			if (addr.getAddressType() != null
					&& addr.getAddressType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_PERSONNEL_ADDR_TYPE_PERM)
					&& addr.getSmAddress() != null) {
				postCode = addr.getSmAddress().getPostcode();
				log.debug(" *** Perm Addr Post Code ****" + postCode);
			}
		}
		IMyIdentitySO myIdentitySO = (IMyIdentitySO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMyIdentitySO.class);
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		reqDTO.setName(per.getName());
		reqDTO.setNewIcNo(per.getIdentificationNo());
		reqDTO.setType("Online");
		reqDTO.setPostCode(postCode);
		log.debug(" *** Req DTO Post Code ****" + reqDTO.getPostCode());
		MyIdentityResDTO resDTO = myIdentitySO.myIdentityVerification(reqDTO);
		StringBuffer sb = new StringBuffer();
		sb.append("MessageConst.ERROR_MYIDENTITY_");
		if (!resDTO.getStatusCode().equalsIgnoreCase(
				SupplierManagementConst.SM_MYIDENTITY_STATUS_CODE_VALID)) {
			sb.append(resDTO.getStatusCode());
			throw new EpException(sb.toString());
		}
	}

	public static boolean checkValidBank(SmSupplierBank bank, SmAppl appl) {

		boolean validBank = true;
		for (SmSupplierBank supBank : appl.getSmSupplierBanks()) {
			if (bank.getSupplierBankId() == null && bank.equals(supBank)) {
				log.debug("****** Same Bank Obj before save ");
			} else if (bank.getSupplierBankId() != null
					&& bank.getSupplierBankId().equals(
							supBank.getSupplierBankId())) {
				log.debug("**** Same Bank object edit with exisiting id ******");
			} else {
				if (bank.getAccountNo() != null
						&& bank.getPmFinancialOrg() != null
						&& bank.getPmFinancialOrg().getFinancialOrgId() != null
						&& supBank.getAccountNo() != null
						&& supBank.getPmFinancialOrg() != null
						&& supBank.getPmFinancialOrg().getFinancialOrgId() != null
						&& bank.getAccountNo().equalsIgnoreCase(
								supBank.getAccountNo())
						&& bank.getPmFinancialOrg()
								.getFinancialOrgId()
								.equals(supBank.getPmFinancialOrg()
										.getFinancialOrgId())) {
					validBank = false;
					throw new EpException(MessageConst.ERROR_722543,
							new String[] { bank.getAccountNo(),
									bank.getPmFinancialOrg().getFinOrgName() });
				}

			}
		}

		return validBank;

	}

	public static SmApplSectionReview setVerificationValues(
			SmApplSectionReview sec, Boolean isVerifiedAll, int maxReviewCout) {

		if (isVerifiedAll) {
			sec.setCheckCount(maxReviewCout);

		} else {
			sec.setCheckCount(0);

		}
		sec.setBasicVerified(isVerifiedAll);
		sec.setOfficeFacilityVerfied(isVerifiedAll);
		sec.setStaffVerified(isVerifiedAll);

		sec.setTrnsNetworkVerified(isVerifiedAll);
		sec.setBranchVerified(isVerifiedAll);
		sec.setAssociateVerified(isVerifiedAll);

		sec.setCertVerified(isVerifiedAll);
		sec.setExperienceVerified(isVerifiedAll);
		sec.setCaptialVerified(isVerifiedAll);

		sec.setShareholderVerifed(isVerifiedAll);
		sec.setEquityCompVerifed(isVerifiedAll);
		sec.setPersonnelVerified(isVerifiedAll);

		sec.setBankVerified(isVerifiedAll);
		sec.setCategoryVerified(isVerifiedAll);
		sec.setSitevisitVerified(isVerifiedAll);
		sec.setFactoryVerify(isVerifiedAll);
		sec.setManufacturingVerified(isVerifiedAll);
		sec.setMachineryVerified(isVerifiedAll);
		sec.setLicenseVerified(isVerifiedAll);
		sec.setAssocBodyVerified(isVerifiedAll);

		return sec;
	}

	public static SmAddress setDefaultAddr(PmCountry defCntry) {
		SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		if (defCntry != null && defCntry.getCountryId() != null) {
			cntry = defCntry;
		} else {
			cntry = SmCommonUtil
					.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			// cntry.setCountryId(1l);
		}
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());
		return addr;
	}

	public static List<SmStaffTotal> getStaffList(SmAppl app) {
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		System.out.println("****  getStaffList *********"
				+ app.getSupplierType());
		if (app.getSupplierType().equals(
				SupplierManagementConst.STYPE_CONTRACTOR)) {
			SmStaffTotal stf = new SmStaffTotal();
			staffList.add(setDefaultStaffValues(stf, app,
					SupplierManagementConst.STAFF_TYPE_CONTRACTOR));
		} else if (app.getSupplierType().equals(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			SmStaffTotal stf1 = new SmStaffTotal();
			staffList.add(setDefaultStaffValues(stf1, app,
					SupplierManagementConst.STAFF_TYPE_PROFESSIONAL));
			SmStaffTotal stf2 = new SmStaffTotal();
			staffList.add(setDefaultStaffValues(stf2, app,
					SupplierManagementConst.STAFF_TYPE_SEMIPROF));
			SmStaffTotal stf3 = new SmStaffTotal();
			staffList.add(setDefaultStaffValues(stf3, app,
					SupplierManagementConst.STAFF_TYPE_CLERICAL));
		}
		return staffList;
	}

	public static boolean validL3CategoryCode(Long catL3Id, Long supplierId) {
		System.out
				.println("*** Validate the code with directors and equi owners ***** ");
		IMofRegistrationSO mofRegistrationSO = null;
		boolean validCategory = true;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(supplierId);
			reqDto.setCatL3Id(catL3Id);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.checkValidCategoryForSupplier(reqDto);
			if (resDTO.isInvalidCategory()) {
				validCategory = false;
				throw new EpException(MessageConst.ERROR_722548);
			} else {
				validCategory = true;
			}
			return validCategory;
		} finally {
			mofRegistrationSO = null;
		}
	}

	private static SmStaffTotal setDefaultStaffValues(SmStaffTotal stf,
			SmAppl app, String category) {
		stf.setRevNo(new BigDecimal(1));
		stf.setTotalBumi(new BigDecimal(0));
		stf.setTotalNonBumi(new BigDecimal(0));
		stf.setTotalForeigner(new BigDecimal(0));
		stf.setStaffCategory(category);
		stf.setSmAppl(app);
		return stf;
	}

	public static String getPOLoginId(Long statusId, Long docId) {
		System.out.println("*** This is resubmit to get po ****** ");
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setStatusId(statusId);
		reqDTO.setApplId(docId);
		ISmWorkFlowStatusSO reqSO = (ISmWorkFlowStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("sm", ISmWorkFlowStatusSO.class);
		SmWorkFlowStatusResDTO resDTO = reqSO
				.retrieveCurrentWorkDFlowByDocAndStatusId(reqDTO);
		String poLogin = null;
		if (resDTO != null && resDTO.getWorkflowStatus() != null
				&& resDTO.getWorkflowStatus().getChangedBy() != null) {
			System.out.println("******** The user id from the data ******* "
					+ resDTO.getWorkflowStatus().getChangedBy());
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(resDTO.getWorkflowStatus().getChangedBy());
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IUserSO.class);
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
			if (userResDTO != null && userResDTO.getPmUser() != null) {
				poLogin = userResDTO.getPmUser().getLoginId();
				System.out
						.println("******* After retrieve po details ******** ");
			}
		}
		return poLogin;
	}

	public static boolean validateBeforeProceed(Long supplierId,
			String applicationType) {
		boolean result = false;
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDto = new MofRegistrationResDTO();
		reqDto.setSupplierId(supplierId);
		resDto = mofRegistrationSO.retrieveSupplierProfile(reqDto);
		if (resDto.getSmSupplier() != null) {
			SmSupplier smSupplier = resDto.getSmSupplier();
			List<SmMofAccount> accounts = smSupplier.getSmMofAccounts();
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						smSupplier.getSmAppl().getSupplierType())) {
					Timestamp currentDate = new Timestamp(
							System.currentTimeMillis());
					long diff = account.getExpDate().getTime()
							- currentDate.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					if (applicationType.equalsIgnoreCase("R")) {
						if (diffDays > 90) {
							result = true;
							throw new EpException(MessageConst.ERROR_722501);
						} else if (diffDays < -365) {
							result = true;
							throw new EpException(MessageConst.ERROR_722502);
						}
					} else if (applicationType.equalsIgnoreCase("A")
							&& (diffDays >= 0 && diffDays < 90)) {
						result = true;
						log.debug("***************MOF Account Expiry near for Ac No.: "
								+ account.getMofNo()
								+ "\nExp Date:"
								+ account.getExpDate()
								+ "\nDiff Days:"
								+ diffDays);
						throw new EpException(MessageConst.ERROR_722503);
					} else if (applicationType.equalsIgnoreCase("B")
							&& smSupplier.isBumi()) {
						result = true;
						throw new EpException(MessageConst.ERROR_722504);
					}
				}
			}
		}
		return result;
	}

	public static boolean checkActiveSmApplForSupp(SmSupplier smSupplier) {
		boolean activeSmAppl = false;
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		resDTO = mofRegistrationSO.retrieveActiveSmApplBySupp(reqDTO);
		if (resDTO.getSmAppl() != null)
			activeSmAppl = true;
		return activeSmAppl;
	}

	public static List<PmCountry> retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		if (resDTO != null) {
			return resDTO.getCountryList();
		} else {
			return null;
		}
	}

	public static List<PmCertBody> retrievePmCertBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveCertifiedBodyList();
		if (resDTO != null) {
			return resDTO.getCertBodyList();
		} else {
			return null;
		}
	}

	public static List<PmRace> retrievePmRaceList() {
		IRaceSO reqSO = (IRaceSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IRaceSO.class);
		RaceResDTO resDTO = reqSO.retrieveRaceList();
		if (resDTO != null) {
			return resDTO.getRaceList();
		} else {
			return null;
		}
	}

	public static List<PmCertBody> retrievePmProfBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();
		if (resDTO != null) {
			return resDTO.getCertBodyList();
		} else {
			return null;
		}
	}

	public static List<PmCategoryL1> retrieveParentCategoryList() {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1List();
		if (resDTO != null) {
			return resDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	public static List<PmUom> retrieveUomList() {
		IUomSO reqSO = (IUomSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IUomSO.class);
		UomResDTO resDTO = reqSO.retrieveUomList();
		if (resDTO != null) {
			return resDTO.getPmUomList();
		} else {
			return null;
		}
	}

	public static List<PmReasonDesc> retrieveRejectReasonList() {
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(ReasonCategory.SUPPLIER_REJECTION_REASON
				.value());
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		ReasonResDTO resDTO = reqSO.retrieveReasonDescListByCategory(reqDTO);
		if (resDTO != null) {
			return resDTO.getReasonDescList();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	public static List<PmStatusDesc> retrieveRegistrationStatusList() {
		IStatusSO reqSO = (IStatusSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStatusSO.class);
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = reqSO.retrieveRegStatusList(reqDTO);
		if (resDTO != null) {
			return resDTO.getStatusDescList();
		} else {
			return null;
		}
	}

	public static List<PmEduField> retrieveEduFieldList() {
		EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
		IEducationFieldSO reqSO = (IEducationFieldSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IEducationFieldSO.class);
		EducationFieldResDTO resDTO = reqSO.retrieveEducationFieldList(reqDTO);
		if (resDTO != null) {
			return resDTO.getEduFieldList();
		} else {
			return null;
		}
	}

	public static PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

	public static MofRegistrationResDTO retriveApplicationById(int applId) {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.retrieveSmApplicationDetailsById(reqDto);
			return resDTO;
		} catch (Exception e) {
			System.out
					.println("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}

	public static String setNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.PROCEED_ADDTNLCAT_APPLN_SIDEMENU;
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.PROCEED_BUMI_APPLN_SIDEMENU;
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.PROCEED_UPDATE_APPLN_SIDEMENU;
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.PROCEED_RENEW_APPLN_SIDEMENU;
			// PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	public static String setExceptionNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = SupplierManagementConst.DEFAULT_MOF_REG;
			break;
		case "A":
			s = SupplierManagementConst.DEFAULT_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.DEFAULT_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.DEFAULT_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.DEFAULT_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.DEFAULT_CANCEL_MOF_APPLN;
			break;
		default:
			break;

		}
		return s;
	}

	public static String setParameterConst(String appType) {
		String s = null;
		switch (appType) {
		case "N":
			s = ParameterConst.NEW_SM_APPLN_FOR_MOF;
			break;
		case "A":
			s = ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES;
			break;
		case "B":
			s = ParameterConst.NEW_SM_APPLN_FOR_BUMI;
			break;
		case "U":
			s = ParameterConst.NEW_SM_APPLN_FOR_UPDATE;
			break;
		case "R":
			s = ParameterConst.NEW_SM_APPLN_FOR_RENEW;
			break;
		case "C":
			s = ParameterConst.NEW_SM_APPLN_FOR_CANCEL;
			break;
		default:
			break;

		}
		return s;
	}

	public static SmApplSupportDoc getDefaultSmApplSupportingDoc(
			PmDocument doc, SmAppl appl) {
		SmApplSupportDoc docu = new SmApplSupportDoc();
		docu.setPmDocument(doc);
		docu.setSmAppl(appl);
		docu.setVerified(true);
		docu.setVerifiedFlg(1);
		docu.setReceived(false);

		List<SmApplDocFile> files = new ArrayList<SmApplDocFile>();
		SmApplDocFile docFile = new SmApplDocFile();
		docFile.setSmApplSupportDoc(docu);
		docFile.setFileName(" ");
		docFile.setFilePath(" ");
		files.add(docFile);
		docu.setSmApplDocFiles(files);
		return docu;
	}

	// SM Address Clone code start
	public static void cloneShareHolderAddress(
			SmApplicationManagedBean managedBean) {
		managedBean.getShareHolderResdAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getShareHolderPermAddr()
						.getSmAddress(), managedBean.getShareHolderResdAddr()
						.getSmAddress(), managedBean.getShareHolderResdAddr(),
						managedBean.getDefPmCountry()));
		managedBean.getShareHolderOffAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getShareHolderPermAddr()
						.getSmAddress(), managedBean.getShareHolderOffAddr()
						.getSmAddress(), managedBean.getShareHolderOffAddr(),
						managedBean.getDefPmCountry()));
		managedBean.getShareHolderMailAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getShareHolderPermAddr()
						.getSmAddress(), managedBean.getShareHolderMailAddr()
						.getSmAddress(), managedBean.getShareHolderMailAddr(),
						managedBean.getDefPmCountry()));
	}

	private static SmAddress cloneAsPermenantAddr(SmAddress srcAddr,
			SmAddress desAddr, SmPersonnelAddress desPersAddr, PmCountry cntry) {
		SmAddress smAddress = new SmAddress();
		try {
			if (srcAddr.getAddressId() != null
					&& desAddr.getAddressId() != null
					&& srcAddr.getAddressId().equals(desAddr.getAddressId())) {
				SmAddress temp;

				// (SmAddress) BeanUtils.cloneBean(desAddr);
				if (desPersAddr.isSameAsPerm()) {
					temp = srcAddr;
				} else {
					temp = desAddr;

				}
				smAddress = SmApplicationUtil.setDefaultAddr(cntry);
				smAddress.setAddressId(null);
				resetToDefaultEntityValues(smAddress);
				resetSmAddressChildLists(smAddress);
				assignClonedAddressValues(smAddress, temp);
				return smAddress;
			} else {
				if (desPersAddr.isSameAsPerm()) {
					assignClonedAddressValues(desAddr, srcAddr);
				}
				return desAddr;
			}
		} finally {
			smAddress = null;
		}
	}

	private static void assignClonedAddressValues(SmAddress desAddr,
			SmAddress srcAddr) {
		desAddr.setAddress1(srcAddr.getAddress1());
		desAddr.setAddress2(srcAddr.getAddress2());
		desAddr.setAddress3(srcAddr.getAddress3());
		desAddr.setAddress4(srcAddr.getAddress4());
		desAddr.setPostcode(srcAddr.getPostcode());
		desAddr.getPmCountry().setCountryId(
				srcAddr.getPmCountry().getCountryId());
		desAddr.getPmState().setStateId(srcAddr.getPmState().getStateId());
		desAddr.getPmDistrict().setDistrictId(
				srcAddr.getPmDistrict().getDistrictId());
		desAddr.getPmCity().setCityId(srcAddr.getPmCity().getCityId());
	}

	private static void resetToDefaultEntityValues(BaseEntity baseEntity) {
		baseEntity.setBrandNew(true);
		baseEntity.setModified(false);
		baseEntity.setDeleted(false);
		baseEntity.setCreatedBy(null);
		baseEntity.setCreatedDate(null);
		baseEntity.setChangedBy(null);
		baseEntity.setChangedDate(null);
	}

	private static void resetSmAddressChildLists(SmAddress smAdd) {
		smAdd.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		smAdd.setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		smAdd.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		smAdd.setSmFacilities(new ArrayList<SmFacility>());
		smAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smAdd.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		smAdd.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
	}

	// SM Address Clone code end

	public static SmAppl defaultSmAppl(SmAppl appl) {
		// = new SmAppl();
		if (appl.getSmSupplierBranches() == null) {
			appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		}
		if (appl.getSmAssociateCompanies() == null) {
			appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		}
		if (appl.getSmAssociationBodies() == null) {
			appl.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		}
		if (appl.getSmSupplierCerts() == null) {
			appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		}
		if (appl.getSmEquityCompanies() == null) {
			appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		}
		if (appl.getSmSupplierCategories() == null) {
			appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		}
		if (appl.getSmFacilities() == null) {
			appl.setSmFacilities(new ArrayList<SmFacility>());
		}
		if (appl.getSmManufacturingDtls() == null) {
			appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		}
		if (appl.getSmMachineDtls() == null) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		}
		if (appl.getSmPersonnels() == null) {
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (appl.getSmSupplierBanks() == null) {
			appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		if (appl.getSmApplRejectReasons() == null) {
			appl.setSmApplRejectReasons(new ArrayList<SmApplRejectReason>());
		}

		return appl;
	}

	public static void calculateShareHolderPercent(
			SmApplicationManagedBean managedBean) {
		System.out.println("********* calculateShareHolderPercent ********  ");
		System.out.println(" *** Non Bumi Equity **** "
				+ managedBean.getSmAppl().getSmCompanyBasic()
						.getNonBumiEquity());
		System.out.println(" *** Bumi Equity **** "
				+ managedBean.getSmAppl().getSmCompanyBasic().getBumiEquity());
		System.out.println(" *** Non MY Equity **** "
				+ managedBean.getSmAppl().getSmCompanyBasic()
						.getForeignerEquity());

		IPersonnelSO perSO = null;
		String perType = null;
		List<SmPersonnel> selPerList = new ArrayList<SmPersonnel>();
		try {
			if (managedBean.getSelectedShareHolder().getShareAmt() != null) {
				managedBean.getSelectedShareHolder().setShareAmt(
						managedBean.getSelectedShareHolder().getShareAmt()
								.setScale(2, RoundingMode.HALF_UP));
			} else {
				managedBean.getSelectedShareHolder().setShareAmt(
						new BigDecimal(0));
			}
			managedBean.setEquiPerCapTotal(new BigDecimal(0));
			perSO = (IPersonnelSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", IPersonnelSO.class);
			PersonnelReqDTO reqDto = new PersonnelReqDTO();
			reqDto.setApplId(managedBean.getSmAppl().getApplId());
			PersonnelResDTO resDto = new PersonnelResDTO();
			String countryCode = "";
			if (managedBean.getSelectedShareHolder().getPmCountry() == null
					|| managedBean.getSelectedShareHolder().getPmCountry() != null
					&& managedBean.getSelectedShareHolder().getPmCountry()
							.getCountryId() == null) {
				managedBean.getSelectedShareHolder().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				managedBean.getSelectedShareHolder().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				throw new EpException(
						"Please select the nationality to add the shareholding amount");
			} else {
				PmCountry cntry = new PmCountry();
				cntry = retrievePmCountryById(managedBean
						.getSelectedShareHolder().getPmCountry().getCountryId());
				if (cntry != null && cntry.getCountryCode() != null) {
					countryCode = cntry.getCountryCode();
				}
			}
			if (!countryCode.equalsIgnoreCase("MY")) {
				resDto = perSO.retrieveNonMalaysianShareHoldersByApplId(reqDto);
				perType = SupplierManagementConst.PERSONNEL_TYPE_FRGNR;
			} else {
				if (managedBean.getSelectedShareHolder().getBumiFlg() == 1) {
					resDto = perSO.retrieveBumiShareHoldersByApplId(reqDto);
					perType = SupplierManagementConst.PERSONNEL_TYPE_BUMI;
				} else {
					perType = SupplierManagementConst.PERSONNEL_TYPE_NON_BUMI;
					resDto = perSO.retrieveNonBumiShareHoldersByApplId(reqDto);
				}
			}
			if (resDto.getSmPersonnelList() != null) {
				selPerList = resDto.getSmPersonnelList();
				System.out.println("*** List retrieved *** "
						+ selPerList.size());
			}
			for (SmPersonnel per : selPerList) {
				if ((per.getShareAmt() == null)) {
					per.setShareAmt(new BigDecimal(0));
				}
				if (per.isEquityOwner()
						&& !per.getPersonnelId().equals(
								managedBean.getSelectedShareHolder()
										.getPersonnelId())) {
					managedBean.setEquiPerCapTotal(managedBean
							.getEquiPerCapTotal().add(per.getShareAmt())
							.setScale(2, RoundingMode.HALF_UP));
				}
			}
			managedBean.setEquiPerCapTotal(managedBean.getEquiPerCapTotal()
					.add(managedBean.getSelectedShareHolder().getShareAmt())
					.setScale(2, RoundingMode.HALF_UP));
			Boolean validAmt = true;
			if (perType
					.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_BUMI)) {
				if (managedBean.getEquiPerCapTotal().compareTo(
						managedBean.getSmAppl().getSmCompanyBasic()
								.getBumiEquity()) <= 0) {
					validAmt = true;
				} else {
					validAmt = false;
				}
			} else if (perType
					.equalsIgnoreCase(SupplierManagementConst.PERSONNEL_TYPE_NON_BUMI)) {
				if (managedBean.getEquiPerCapTotal().compareTo(
						managedBean.getSmAppl().getSmCompanyBasic()
								.getNonBumiEquity()) <= 0) {
					validAmt = true;
				} else {
					validAmt = false;
				}

			} else {
				if (managedBean.getEquiPerCapTotal().compareTo(
						managedBean.getSmAppl().getSmCompanyBasic()
								.getForeignerEquity()) <= 0) {
					validAmt = true;
				} else {
					validAmt = false;
				}
			}
			if (validAmt) {
				BigDecimal mul = new BigDecimal(100);
				if (managedBean.getSmAppl().getSmCompanyBasic()
						.getPaidUpCapital().compareTo(new BigDecimal(0)) > 0) {
					managedBean.getSelectedShareHolder().setSharePercentage(
							managedBean
									.getSelectedShareHolder()
									.getShareAmt()
									.divide(managedBean.getSmAppl()
											.getSmCompanyBasic()
											.getPaidUpCapital(), 4,
											RoundingMode.HALF_UP).multiply(mul)
									.setScale(2, RoundingMode.HALF_UP));
				}

			} else {
				managedBean.getSelectedShareHolder().setSharePercentage(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				managedBean.getSelectedShareHolder().setShareAmt(
						new BigDecimal(0).setScale(2, RoundingMode.HALF_UP));
				throw new EpException(MessageConst.ERROR_722551, new String[] {
						"Personnel", "personnel", perType });
			}
		} catch (NumberFormatException ne) {
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		} finally {
			perSO = null;
			selPerList = null;
		}
	}

	public static PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	public static boolean siteVisitDataUpdate(
			SmApplicationManagedBean managedBean) {
		SmSvTask svTask = new SmSvTask();
		List<SmSvAddress> addrList = new ArrayList<SmSvAddress>();
		List<SmSvCategory> smSvCategories = new ArrayList<SmSvCategory>();
		svTask.setSmAppl(managedBean.getSmAppl());
		log.info("** Basic Office ***"
				+ managedBean.getBasicCompanyFacility().getSmAddress()
						.getAddress1() + " -- Id -- "
				+ managedBean.getBasicCompanyFacility().getFacilityId());
		SmSvOffice svOff = new SmSvOffice();
		svOff.setSmSvTask(svTask);
		svOff.setSmFacility(managedBean.getBasicCompanyFacility());
		svTask.setSmSvOffice(svOff);
		for (SmFacility fac : managedBean.getSelectedFactoryFacilities()) {
			SmSvAddress facAddr = new SmSvAddress();
			facAddr.setSmSvTask(svTask);
			facAddr.setSmAddress(fac.getSmAddress());
			addrList.add(facAddr);
		}
		for (SmSupplierBranch br : managedBean.getSelectedBranches()) {

			SmSvAddress brAddr = new SmSvAddress();
			brAddr.setSmSvTask(svTask);
			brAddr.setSmAddress(br.getSmAddress());
			addrList.add(brAddr);
		}
		for (SmAssociateCompany assoc : managedBean.getSelectedAssocCompanies()) {
			SmSvAddress asocAddr = new SmSvAddress();
			asocAddr.setSmSvTask(svTask);
			asocAddr.setSmAddress(assoc.getSmAddress());
			addrList.add(asocAddr);
		}
		svTask.setSmSvAddresses(addrList);
		List<PmParameter> catParam = new ArrayList<PmParameter>();
		for (SmSupplierCategory cat : managedBean.getSelectedCategories()) {
			if (!catParam.contains(cat.getPmCategoryL3().getCategoryTypeCode())) {
				catParam.add(cat.getPmCategoryL3().getCategoryTypeCode());

			}
			SmSvCategory svCat = new SmSvCategory();
			svCat.setSmSvTask(svTask);
			svCat.setPmCategoryL1(cat.getPmCategoryL1());
			svCat.setPmCategoryL2(cat.getPmCategoryL2());
			svCat.setPmCategoryL3(cat.getPmCategoryL3());
			smSvCategories.add(svCat);

		}
		svTask.setSmSvCategories(smSvCategories);
		List<SmSvTemplate> templates = new ArrayList<SmSvTemplate>();
		for (PmParameter param : catParam) {
			if (param.getParameterCode() == SupplierManagementConst.CAT_TYPE_TRANSPORTATION
					|| param.getParameterCode() == SupplierManagementConst.CAT_TYPE_WORKSHOP
					|| param.getParameterCode() == SupplierManagementConst.CAT_TYPE_INSURANCE
					|| param.getParameterCode() == SupplierManagementConst.CAT_TYPE_DOCKYARD
					|| param.getParameterCode() == SupplierManagementConst.CAT_TYPE_MANUFACTURING
					|| param.getParameterCode() == SupplierManagementConst.CAT_TYPE_READING_MATERIAL_FROM_OVERSEA) {
				SmSvTemplate temp = new SmSvTemplate();
				temp.setPmParameter(param);
				temp.setSmSvTask(svTask);
				templates.add(temp);
			}
		}

		if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			PmParameter applType = SmCommonUtil.retrieveParameterByTypeAndCode(
					ParameterTypeConst.CATEGORY_TYPE,
					SupplierManagementConst.CAT_TYPE_BUMIPUTERA,
					new FacesScopes().getUserLocale().getLanguage());
			SmSvTemplate temp = new SmSvTemplate();
			temp.setPmParameter(applType);
			temp.setSmSvTask(svTask);
			templates.add(temp);
		}
		svTask.setSmSvTemplates(templates);
		return updateSiteVisitTask(svTask);

	}

	private static boolean updateSiteVisitTask(SmSvTask svTask) {
		ISiteAssignSO siteSO = null;
		boolean updateSuccess = true;
		try {
			// Save or update to DB
			siteSO = (ISiteAssignSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISiteAssignSO.class);
			SiteAssignReqDTO reqDto = new SiteAssignReqDTO();
			reqDto.setSmSvTask(svTask);
			SiteAssignResDTO resDTO = siteSO.updateSiteVisitAssignment(reqDto);
			updateSuccess = resDTO.isSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			updateSuccess = false;
			throw new EpException(MessageConst.ERROR_722506);
		} finally {
			siteSO = null;
		}
		return updateSuccess;
	}

}
