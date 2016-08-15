package my.ep.web.util.sm.helper;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.educationfield.IEducationFieldSO;
//import my.ep.biz.service.pm.common.educationlevel.IEducationLevelSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.race.IRaceSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.uom.IUomSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.certbody.CertBodyReqDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldReqDTO;
import my.ep.dto.pm.common.educationfield.EducationFieldResDTO;
//import my.ep.dto.pm.common.educationlevel.EducationLevelReqDTO;
//import my.ep.dto.pm.common.educationlevel.EducationLevelResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.race.RaceResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.pm.common.zone.ZoneResDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusReqDTO;
import my.ep.dto.sm.workFlowStatus.SmWorkFlowStatusResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmEduField;
//import my.ep.persistence.entity.pm.common.PmEduLevel;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCategoryFacility;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.persistence.entity.sm.SmSvAddress;
import my.ep.persistence.entity.sm.SmSvCategory;
import my.ep.persistence.entity.sm.SmSvOffice;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.persistence.entity.sm.SmSvTemplate;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.pm.cc.CategoryCodeSearchBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.vo.SmApplStatusInfoVO;
import my.ep.web.vo.SmAuthorizedPersonnelCountVO;
import my.ep.web.vo.SmSiteVisitFacilityVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import com.liferay.portal.kernel.util.StringUtil;

// TODO: Auto-generated Javadoc
public class SmApplicationUtil {
	private static Logger log = Logger.getLogger(SmApplicationUtil.class);

	private static SmApplicationUtil instance;
	private static CategoryCodeSearchBackingBean categoryCodeBbInstance;

	/**
	 * Validate person limit.Set the max limit of authorizes personnel that can
	 * added within this particular application
	 * 
	 * @param supplierId
	 *            - Long supplier id
	 * @param applType
	 *            - the appl type
	 * @param applId
	 *            - Long appl id
	 * @return the max - count of authorized persons that can be added
	 */
	public static int validatePersonLimit(Long supplierId, String applType,
			Long applId) {
		log.info("******* Validate Personnel Count Start ******");
		int validPerCount = 0;
		int count = 5;
		int resultCount = 0;
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		reqDTO.setApplId(applId);
		reqDTO.setMof(true);
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelResDTO resDTO = smPersonnelSO.retrievePersonnelCount(reqDTO);
		if (applType.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
			count = 10;
		}
		if (resDTO != null && resDTO.getCount() != null) {
			resultCount = resDTO.getCount().intValue();
			if (resultCount < count) {
				if (count == 5 || ((count == 10) && (resultCount < 5)))
					validPerCount = 1;
				else
					validPerCount = 2;
			} else {
				validPerCount = 0;
			}
		} else {
			validPerCount = 0;
		}

		log.info("******* Validate Personnel Count Start ******");
		return validPerCount;
	}

	/**
	 * Gets the sm authorized personnel count vo.
	 * 
	 * @param supplierId
	 *            - Long supplier id
	 * @param applType
	 *            - the appl type
	 * @param applId
	 *            - Long appl id
	 * @param isAddNewPersonnel
	 *            - boolean
	 * @return the sm authorized personnel count vo object
	 */
	public static SmAuthorizedPersonnelCountVO getSmAuthorizedPersonnelCountVO(
			Long supplierId, String applType, Long applId,
			boolean isAddNewPersonnel) {
		int resultCount = 0;

		SmAuthorizedPersonnelCountVO countVO = new SmAuthorizedPersonnelCountVO();
		countVO.setReachAuthorizedPersonnelLimit(false);
		countVO.setJustificationRequired(false);

		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		reqDTO.setApplId(applId);
		reqDTO.setMof(true);

		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelCount(reqDTO);

		int authPersLimit = SmRuleHelper.getInstance()
				.getMaxAdditionalMofAuthorizedUser();

		if (resDTO != null && resDTO.getCount() != null) {
			resultCount = resDTO.getCount().intValue();
			if (resultCount >= authPersLimit) {
				countVO.setReachAuthorizedPersonnelLimit(true);
			}

			if (isAddNewPersonnel) {
				resultCount = resultCount + 1;
			}
			// for additional authorized personnel(6 until 10) need to add
			// justification
			if (resultCount > SmRuleHelper.getInstance()
					.getMaxMofAuthorizedUser()) {
				countVO.setJustificationRequired(true);
			}
		}
		return countVO;
	}

	/**
	 * Validate new category added.Check if at least one new category is added
	 * within that particular application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return true, if successful
	 */
	public static boolean valiateNewCategoryAdded(
			SmApplicationManagedBean managedBean) {
		boolean addedNewCode = false;
		if (managedBean.isSupplierUser()) {
			if (managedBean.getSmAppl().getSmSupplierCategories() == null
					|| managedBean.getSmAppl().getSmSupplierCategories()
							.isEmpty()) {
				addedNewCode = false;
				throw new EpException(MessageConst.ERROR_722516);
			}

		}
		if (!managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					addedNewCode = true;
					break;
				}
			}
		} else {
			addedNewCode = true;
		}

		return addedNewCode;

	}

	/**
	 * Validate supporting documents.For online application check whether
	 * supplier uploaded all the files or not
	 * 
	 * @param suppDocs
	 *            - List of SmApplSupportDoc
	 * @return true, if successful
	 */
	public static boolean validateSupportingDocs(List<SmApplSupportDoc> suppDocs) {
		boolean validSupportDocs = true;
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("<br/>");
		for (SmApplSupportDoc doc : suppDocs) {
			if (doc.getPmDocument() != null
					&& doc.getPmDocument().isMandatory()
					&& !doc.getPmDocument().isHardCopyReq()
					&& (doc.getSmApplDocFiles().get(0).getFileName()
							.equalsIgnoreCase(" ") || doc.getSmApplDocFiles()
							.get(0).getFilePath().equalsIgnoreCase(" "))) {
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
		} else {
			for (SmApplSupportDoc doc : suppDocs) {
				if (doc.getPmDocument() != null
						&& doc.getPmDocument().isHardCopyReq()) {
					doc.getSmApplDocFiles().clear();
				}
			}
		}
		return validSupportDocs;
	}

	/**
	 * Gets the current supplier based on the login id.
	 * 
	 * @return the SmSupplier object
	 */
	public static SmSupplier getCurrentSupplier() {
		SmSupplier smSupp = new SmSupplier();
		try {
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			if (pmUser == null
					|| (pmUser != null && pmUser.getUserId() == null)) {
				log.info("**** Invalid user from the session scope ******");
				throw new EpException(MessageConst.ERROR_721017);
			}
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(pmUser.getLoginId());
			log.info("************Supplier PmUser Login Id:"
					+ pmUser.getLoginId());

			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			supplierSearchReqDTO.setAllStatuses(true);
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
			if (smSupp.getSupplierId() == null) {
				throw new EpException(MessageConst.ERROR_721018);
			}
			return smSupp;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_721017);
		}

	}

	/**
	 * Gets the review application base page.
	 * 
	 * @param applicationType
	 *            the application type
	 * @return the review application base page
	 */
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

	/**
	 * Gets the review or view only application supplier base page.
	 * 
	 * @param applicationType
	 *            the application type
	 * @return the review application supplier base page
	 */
	public static String getReviewApplicationSupplierBasePage(
			String applicationType) {
		String basePage = "";
		if (applicationType != null) {
			switch (applicationType) {
			case ParameterConst.NEW_SM_APPLN_FOR_MOF:
				basePage = "/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_SuppBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES:
				basePage = "/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_SuppBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_BUMI:
				basePage = "/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_SuppBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_UPDATE:
				basePage = "/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_SuppBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_RENEW:
				basePage = "/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_SuppBasePage.xhtml";
				break;
			default:
				break;
			}
		}
		return basePage;
	}

	/**
	 * Gets the review application supplier profile base page.
	 * 
	 * @param applicationType
	 *            the application type
	 * @return the review application supplier profile base page
	 */
	public static String getReviewApplicationSupplierProfileBasePage(
			String applicationType) {
		String basePage = "";
		if (applicationType != null) {
			switch (applicationType) {
			case ParameterConst.NEW_SM_APPLN_FOR_MOF:
				basePage = "/xhtml/sm/revisionSmApplView/newMofApplicationView/smViewMofRegSideMenu_SuppProfileBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES:
				basePage = "/xhtml/sm/revisionSmApplView/addAdditionalCategoryCodesView/smViewAddtnlCatApplnSideMenu_SuppProfileBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_BUMI:
				basePage = "/xhtml/sm/revisionSmApplView/bumiputeraApplicationView/smViewBumiSideMenu_SuppProfileBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_UPDATE:
				basePage = "/xhtml/sm/revisionSmApplView/updateMofDetailsView/smViewUpdateMofSideMenu_SuppProfileBasePage.xhtml";
				break;
			case ParameterConst.NEW_SM_APPLN_FOR_RENEW:
				basePage = "/xhtml/sm/revisionSmApplView/renewMofApplicationView/smViewRenewRegSideMenu_SuppProfileBasePage.xhtml";
				break;
			default:
				break;
			}
		}
		return basePage;
	}

	/**
	 * Gets the application error page.
	 * 
	 * @return the application error page
	 */
	public static String getApplicationErrorPage() {
		return "/xhtml/sm/userProfileMaintenance/outputMsg";
	}

	/**
	 * Sets the application pm status.
	 * 
	 * @param statusId
	 *            the status id
	 * @return the status res dto object
	 */
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

	/**
	 * Assign supplier business type.Group of certain category of business into
	 * one default type for the simplifying the screen validation checks
	 * 
	 * @param sup
	 *            - SmSupplier object
	 * @return the business type value
	 */
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
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)
				|| sup.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_NGO)) {
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

	/**
	 * Manage max and min equity limit for the supplier.
	 * 
	 * @param personnel
	 *            - SmPersonnel object
	 * @param businessTypeVal
	 *            - the business type val
	 * @param shrHldrs
	 *            the List of share holders
	 * @return true, if successful
	 */
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

	/**
	 * Sets the navigation for resubmit application based on application type.
	 * 
	 * @param applType
	 *            - the application type
	 */
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

	/**
	 * Validate pre requisites before adding category of type ICT, based on the
	 * supplier type.
	 * 
	 * @param appl
	 *            - SmAppl object
	 * @param category
	 *            - SmSupplierCategory object
	 * @return true, if successful
	 */
	public static boolean validateIctCatCode(String businessTypeVal,
			SmAppl appl, SmSupplierCategory category) {
		boolean validCat = true;
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			int prfCnt = 0;
			int semPrfCnt = 0;
			for (SmPersonnel per : appl.getSmPersonnels()) {
				if (per.isProf()
						&& per.getWorkExperienceYear().compareTo(
								new BigDecimal(5)) >= 0) {
					prfCnt = prfCnt + 1;
				} else if (per.isSemiProf()
						&& per.getWorkExperienceYear().compareTo(
								new BigDecimal(5)) >= 0) {
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
			if (businessTypeVal != SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT
					&& businessTypeVal != SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT
					&& dirCnt < 1) {
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

	/**
	 * Assign max review count of the sections based on application ,
	 * supplier,category types.
	 * 
	 * @param appType
	 *            - the app type
	 * @param applCatType
	 *            -the appl cat type
	 * @param supplierType
	 *            - the supplier type
	 * @param businessType
	 *            -the business type
	 * @return int - max review count number
	 */
	public static int assignMaxReviewCount(String appType, String applCatType,
			String supplierType, String businessType, boolean isReviewSiteVisit) {
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
			if (supplierType
					.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)) {
				i = i + 1;
			}
			break;
		case "U":
			i = SupplierManagementConst.UPDATE_MOF_APPL_REVIEW_MAXCOUNT;
			break;
		case "R":
			if (applCatType != null && applCatType.equalsIgnoreCase("S")) {
				i = SupplierManagementConst.RENEW_APPL_SPLCAT_REVIEW_MAXCOUNT;
			} else {
				i = SupplierManagementConst.RENEW_APPL_NORMCAT_REVIEW_MAXCOUNT;
			}
			break;
		default:
			break;

		}
		if (businessType
				.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
			i = i - 2;
		}
		if (businessType
				.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)
				|| businessType
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)) {
			i = i - 1;
		}
		if (isReviewSiteVisit) {
			i = i + 1;
		}
		return i;
	}

	public static void validateGSt(SmApplicationManagedBean managedBean,
			int currentTab, SmRuleHelper ruleHelper) {
		SmGstSupplier gst = managedBean.getCompanyBasicGst();
		if (!isGstEmpty(gst)) {
			if (StringUtils.isNotBlank(gst.getGstRegNo())
					&& gst.getGstRegNo().length() != ruleHelper
							.getGstRegNumLength()) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_721027);
			} else if (StringUtils.isNotBlank(gst.getGstRegNo())
					&& gst.getGstEffDate() == null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Effective date is required
				throw new EpException(MessageConst.ERROR_721030);
			} else if (StringUtils.isBlank(gst.getGstRegNo())
					&& gst.getGstEffDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no is required
				throw new EpException(MessageConst.ERROR_721029);
			} else if (StringUtils.isBlank(gst.getGstRegNo())
					&& gst.getGstEndDate() != null) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				// GST Registration no and GST Effective Date is required
				throw new EpException(MessageConst.ERROR_721043);
			} else if (gst.getGstEndDate() != null
					&& gst.getGstEffDate().compareTo(gst.getGstEndDate()) > 0) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_721044);
			}
		}
	}

	/**
	 * Validate sm company basic info before saving the application.
	 * 
	 * @param validSave
	 *            - boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateBasicInfoBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		if (appl.getSmFacilities().isEmpty()) {
			appl.getSmFacilities().add(managedBean.getBasicCompanyFacility());

		} else {
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
		if (appl.getSmCompanyBasic().getSmCompanyAddresses() != null
				&& !appl.getSmCompanyBasic().getSmCompanyAddresses().isEmpty()
				&& appl.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress() != null
				&& appl.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress().getPmState() != null
				&& appl.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress().getPmDistrict() != null) {
			PmState state = retrievePmStateById(appl.getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress().getPmState()
					.getStateId());
			PmDistrict dist = retrievePmDistById(appl.getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress()
					.getPmDistrict().getDistrictId());
			if (state.getStateCode().equalsIgnoreCase(
					SupplierManagementConst.SARAWAK_CODE)
					&& dist.getDistrictCode().equalsIgnoreCase(
							SupplierManagementConst.KUCHING_DISTRICT_CODE)
					&& (appl.getSmCompanyBasic().getIncomeTaxNo() == null || appl
							.getSmCompanyBasic().getIncomeTaxNo().trim() == "")) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722561);
			}

		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			String staffInfoValMsg = getStaffInfoValidateMsg(appl
					.getSmStaffTotals());
			if (!StringUtils.isBlank(staffInfoValMsg)
					&& !appl.getApplType().equalsIgnoreCase("C")) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722557,
						new String[] { staffInfoValMsg });
			}
			if (managedBean.getSmAppl().getSmAssociationBodies() == null
					|| managedBean.getSmAppl().getSmAssociationBodies()
							.isEmpty()) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722598);
			}
		} else {
			int validStfCont = 0;
			for (SmStaffTotal stfTl : appl.getSmStaffTotals()) {
				if (stfTl.getTotalBumi().compareTo(new BigDecimal(0)) > 0
						|| stfTl.getTotalForeigner().compareTo(
								new BigDecimal(0)) > 0
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
		}

		if (!appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			if (appl.getSmCompanyBasic() != null
					&& !appl.getSmCompanyBasic().isWithFederal()
					&& !appl.getSmCompanyBasic().isWithGlc()
					&& !appl.getSmCompanyBasic().isWithOthers()
					&& !appl.getSmCompanyBasic().isWithState()
					&& !appl.getSmCompanyBasic().isWithStatutory()) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722601);
			}
		}
		appl = null;
		return validSave;

	}

	/**
	 * Validate sm supplier banks before saving application.
	 * 
	 * @param validSave
	 *            - boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateBankBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		int payAccCnt = 0;
		int regAccCnt = 0;
		StringBuffer sb = new StringBuffer();
		int noSignatoryCount = 0;
		List<SmSupplierBranch> paymentAccBranch = new ArrayList<SmSupplierBranch>();

		for (SmSupplierBank bank : appl.getSmSupplierBanks()) {
			if (bank.getAccountPurpose().equalsIgnoreCase("P")) {
				payAccCnt = payAccCnt + 1;
			} else if (bank.getAccountPurpose().equalsIgnoreCase("R")) {
				regAccCnt = regAccCnt + 1;
			} else if (bank.getAccountPurpose().equalsIgnoreCase("B")) {
				payAccCnt = payAccCnt + 1;
				regAccCnt = regAccCnt + 1;
			}
			// if (payAccCnt > 0 && regAccCnt > 0) {
			// break;
			// }
			if (bank.getSmChequeSignatories() == null
					|| bank.getSmChequeSignatories().isEmpty()) {
				sb.append("<br/>");
				sb.append(++noSignatoryCount + ".) ");
				sb.append(bank.getAccountNo());
			}

			if (!SmCommonUtil
					.isCollectionEmpty(bank.getSmSupplierBranchBanks())) {
				if (bank.getAccountPurpose().equals("P")
						|| bank.getAccountPurpose().equals("B")) {
					for (SmSupplierBranchBank brBank : bank
							.getSmSupplierBranchBanks()) {
						if (brBank.getSmSupplierBranch() != null
								&& brBank.getSmSupplierBranch()
										.getSupplierBranchId() != null) {
							paymentAccBranch.add(brBank.getSmSupplierBranch());
						}
					}
				}
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
		if (noSignatoryCount > 0) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722556,
					new String[] { sb.toString() });
		}

		managedBean.setSelectedSignatoryBankAcct(new SmSupplierBank());
		appl = null;
		return validSave;
	}

	/**
	 * Check if each registered branch already assigned to bank account type
	 * payment
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param int currentTab
	 * @param boolean validSave
	 */
	public static void validateBranchWithoutBankAcc(
			SmApplicationManagedBean bean, int currentTab, boolean validSave) {
		if (!SmCommonUtil.isCollectionEmpty(bean.getSmAppl()
				.getSmSupplierBranches())) {
			for (SmSupplierBranch brAppl : bean.getSmAppl()
					.getSmSupplierBranches()) {
				if (!isBranchAssignToBank(bean.getSmAppl(), brAppl)) {
					validSave = false;
					throw new EpException(MessageConst.ERROR_722622,
							new String[] { brAppl.getBranchName() });
				}
			}
		}
	}

	public static boolean isBranchAssignToBank(SmAppl appl,
			SmSupplierBranch paramBranch) {
		boolean retVal = false;
		if (paramBranch != null && paramBranch.getSupplierBranchId() != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmSupplierBanks())) {
			for (SmSupplierBank bnk : appl.getSmSupplierBanks()) {
				if (!SmCommonUtil.isCollectionEmpty(bnk
						.getSmSupplierBranchBanks())) {
					for (SmSupplierBranchBank brBank : bnk
							.getSmSupplierBranchBanks()) {
						if (brBank.getSmSupplierBranch() != null
								&& brBank.getSmSupplierBranch()
										.getSupplierBranchId() != null
								&& brBank
										.getSmSupplierBranch()
										.getSupplierBranchId()
										.compareTo(
												paramBranch
														.getSupplierBranchId()) == 0) {
							retVal = true;
							break;
						}
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Validate category other description.Mandatory data entry validation
	 * 
	 * @param validSave
	 *            - boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateCategoryOtherDesc(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		validateRenewalApplTypeForSpecialCategory(appl);
		if (appl.getSmSupplierCategories() != null
				&& appl.getSmSupplierCategories().size() > 0) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (SmSupplierCategory category : appl.getSmSupplierCategories()) {
				if (category.getPmCategoryL3().getOtherDescReq() != null
						&& category.getPmCategoryL3().getOtherDescReq()
								.equals(new Integer(1))
						&& category.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE) {
					if (StringUtils.isEmpty(category.getOtherDesc())) {
						sb.append("<br/>")
								.append(++i)
								.append(".) ")
								.append(category.getPmCategoryL1()
										.getCategoryL1Code())
								.append(category.getPmCategoryL2()
										.getCategoryL2Code())
								.append(category.getPmCategoryL3()
										.getCategoryL3Code());
						validSave = false;
					}
				}
			}
			if (!validSave) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722514,
						new String[] { sb.toString() });
			}
		}
		return validSave;
	}

	/**
	 * Validate categories before saving application based on supplier type ,
	 * personnel info and logged in user role.
	 * 
	 * @param validSave
	 *            -boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateCategoriesBeforeSave(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		SmAppl appl = managedBean.getSmAppl();
		validateCategoryOtherDesc(validSave, managedBean, currentTab);

		if (managedBean.isSupplierUser()) {
			if (appl.getSmSupplierCategories() == null
					|| appl.getSmSupplierCategories().isEmpty()) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722516);
			}

		}
		if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
				&& !managedBean.isSiteVisitAfterApproval()) {
			validSave = valiateNewCategoryAdded(managedBean);
			if (!validSave) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722570);
			}
		}

		if (!managedBean.isSupplierUser()) {
			if (!SmCommonUtil.isCollectionEmpty(managedBean
					.getActiveCategoryList())) {
				for (SmSupplierCategory cat : managedBean
						.getActiveCategoryList()) {
					if (managedBean.getSmAppl().getApplType()
							.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
						if (managedBean.isPoUser()
								&& (cat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS
										|| cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS || cat
										.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS)) {
							if (cat.getPoDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil
												.getMessage(
														"lbl.sm.required.msg",
														new String[] { SmCommonUtil
																.getMessage(
																		"lbl.sm.catCodes.poAct",
																		null)
																+ " "
																+ SmCommonUtil
																		.getMessage(
																				"lbl.sm.catCodes.forCatCode",
																				null)
																+ " "
																+ cat.getPmCategoryL1()
																		.getCategoryL1Code()
																+ cat.getPmCategoryL2()
																		.getCategoryL2Code()
																+ cat.getPmCategoryL3()
																		.getCategoryL3Code() }));
							}
							if (StringUtils.isBlank(cat.getPoRemarks())
									&& cat.getPoDecFlg() == 0) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.rejRemarks",
												null)
												+ " "
												+ cat.getPmCategoryL1()
														.getCategoryL1Code()
												+ cat.getPmCategoryL2()
														.getCategoryL2Code()
												+ cat.getPmCategoryL3()
														.getCategoryL3Code() }));
							}
						} else if (managedBean.isApproverUser()
								&& (cat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS
										|| cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS || cat
										.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS)) {
							if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil
												.getMessage(
														"lbl.sm.required.msg",
														new String[] { SmCommonUtil
																.getMessage(
																		"lbl.sm.catCodes.appAct",
																		null)
																+ " "
																+ SmCommonUtil
																		.getMessage(
																				"lbl.sm.catCodes.forCatCode",
																				null)
																+ " "
																+ cat.getPmCategoryL1()
																		.getCategoryL1Code()
																+ cat.getPmCategoryL2()
																		.getCategoryL2Code()
																+ cat.getPmCategoryL3()
																		.getCategoryL3Code() }));
							}
							if (StringUtils.isBlank(cat.getApproverRemarks())
									&& cat.getApprvrDecFlg() == 0) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.rejRemarks",
												null)
												+ " "
												+ cat.getPmCategoryL1()
														.getCategoryL1Code()
												+ cat.getPmCategoryL2()
														.getCategoryL2Code()
												+ cat.getPmCategoryL3()
														.getCategoryL3Code() }));
							}
						}
					} else {
						if (managedBean.isPoUser()
								&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
							if (cat.getPoDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil
												.getMessage(
														"lbl.sm.required.msg",
														new String[] { SmCommonUtil
																.getMessage(
																		"lbl.sm.catCodes.poAct",
																		null)
																+ " "
																+ SmCommonUtil
																		.getMessage(
																				"lbl.sm.catCodes.forCatCode",
																				null)
																+ " "
																+ cat.getPmCategoryL1()
																		.getCategoryL1Code()
																+ cat.getPmCategoryL2()
																		.getCategoryL2Code()
																+ cat.getPmCategoryL3()
																		.getCategoryL3Code() }));
							}
							if (StringUtils.isBlank(cat.getPoRemarks())
									&& cat.getPoDecFlg() == 0) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.rejRemarks",
												null)
												+ " "
												+ cat.getPmCategoryL1()
														.getCategoryL1Code()
												+ cat.getPmCategoryL2()
														.getCategoryL2Code()
												+ cat.getPmCategoryL3()
														.getCategoryL3Code() }));
							}
						} else if (managedBean.isApproverUser()
								&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
							if (cat.getApprvrDecFlg() == null) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(
										SmCommonUtil
												.getMessage(
														"lbl.sm.required.msg",
														new String[] { SmCommonUtil
																.getMessage(
																		"lbl.sm.catCodes.appAct",
																		null)
																+ " "
																+ SmCommonUtil
																		.getMessage(
																				"lbl.sm.catCodes.forCatCode",
																				null)
																+ " "
																+ cat.getPmCategoryL1()
																		.getCategoryL1Code()
																+ cat.getPmCategoryL2()
																		.getCategoryL2Code()
																+ cat.getPmCategoryL3()
																		.getCategoryL3Code() }));
							}
							if (StringUtils.isBlank(cat.getApproverRemarks())
									&& cat.getApprvrDecFlg() == 0) {
								validSave = false;
								setNavigationBackToCurrentPage(currentTab,
										managedBean);
								throw new EpException(SmCommonUtil.getMessage(
										"lbl.sm.required.msg",
										new String[] { SmCommonUtil.getMessage(
												"lbl.sm.catCodes.rejRemarks",
												null)
												+ " "
												+ cat.getPmCategoryL1()
														.getCategoryL1Code()
												+ cat.getPmCategoryL2()
														.getCategoryL2Code()
												+ cat.getPmCategoryL3()
														.getCategoryL3Code() }));
							}
						}
					}
				}
			}
		}
		appl = null;
		return validSave;
	}

	public static boolean validatePersonnelAcademics(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		log.info("******* Validate Academics For personnel******** ");
		boolean validPersonnels = true;
		StringBuilder sb = new StringBuilder();
		int i = 0;
		SmAppl appl = managedBean.getSmAppl();
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (per.getSmStaffAcademics() == null
					|| per.getSmStaffAcademics().isEmpty()) {
				sb.append("<br/>");
				sb.append(++i + ".)");
				sb.append(per.getName());
				validPersonnels = false;
			}
		}
		if (!validPersonnels) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722599,
					new String[] { sb.toString() });
		}

		return validSave;
	}

	/**
	 * Validate sm personnels before saving applicaiton the information.
	 * 
	 * @param validSave
	 *            - boolean
	 * @param currentTab
	 *            - int current tab
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return true, if successful
	 */
	public static boolean validatePersonnelBeforeSave(boolean validSave,
			int currentTab, SmApplicationManagedBean managedBean) {
		Locale locale = new FacesScopes().getUserLocale();
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
				if (per.isClerical() && !StringUtils.isBlank(per.getEpfNo())) {
					staffCount = staffCount + 1;

				} else if (per.isProf() && !StringUtils.isBlank(per.getEpfNo())) {
					profCount = profCount + 1;

				} else if (per.isSemiProf()
						&& !StringUtils.isBlank(per.getEpfNo())) {
					semiProfCount = semiProfCount + 1;
				}
			}

		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONSULTANT)) {
			StringBuilder errMsg = new StringBuilder();
			if (semiProfCount < 1) {
				errMsg.append(SmCommonUtil.getMessage(
						"lbl.sm.person.semi.prof.cont.epf.req", null));
				errMsg.append("<br />");
			}
			if (profCount < 1) {
				errMsg.append(SmCommonUtil.getMessage(
						"lbl.sm.person.prof.cont.epf.req", null));
				errMsg.append("<br />");
			}
			if (staffCount < 1) {
				errMsg.append(SmCommonUtil.getMessage(
						"lbl.sm.person.clerk.cont.epf.req", null));
				errMsg.append("<br />");
			}
			if (errMsg != null && errMsg.length() > 0) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, null, errMsg.toString()));
			}
		}
		if (appl.getSupplierType().equalsIgnoreCase(
				SupplierManagementConst.STYPE_CONTRACTOR)) {
			if (staffCount < 1) {
				validSave = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722513, new String[] {
						ResourceBundleUtils.getMessage(
								"lbl.sm.personnel.comprole.staff",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								locale),
						ResourceBundleUtils.getMessage(
								"lbl.sm.personnel.one.lowercaps",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								locale),
						ResourceBundleUtils.getMessage(
								"lbl.sm.personnel.comprole.staff",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								locale) });
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
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.comprole.director",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.one.lowercaps",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.comprole.director",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		} else if (managedBean.getBusinessTypeVal() != null
				&& managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
				&& directorCount < 2) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722513, new String[] {
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.comprole.director",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.two.lowercaps",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.comprole.director",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}
		if (mgmtCount < 1) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722513, new String[] {
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.mngmnt.staff",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.one.lowercaps",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale),
					ResourceBundleUtils.getMessage(
							"lbl.sm.personnel.mngmnt.staff",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
		}
		appl = null;
		return validSave;
	}

	/**
	 * Sets the navigation back to current page.
	 * 
	 * @param currentTab
	 *            - int current tab
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void setNavigationBackToCurrentPage(int currentTab,
			SmApplicationManagedBean managedBean) {
		managedBean.setTabActiveIndex(currentTab);
		managedBean.getSmAppl().setCurrentActiveTab(
				managedBean.getSmAppl().getCurrentActiveTab());
	}

	/**
	 * Validate equity owner count based on the supplier type.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateEquityOwnerCount(
			SmApplicationManagedBean managedBean, int currentTab,
			String currentPageType) {
		boolean validCount = true;
		if (managedBean.getBusinessTypeVal() != null
				&& (managedBean.getBusinessTypeVal()
						.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT))) {
			if (EntityHelper.isCollectionEmpty(managedBean
					.getSmapplShareHolders())
					|| (managedBean.getSmapplShareHolders().size() != 1)) {

				validCount = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, managedBean);
				}
				throw new EpException(MessageConst.ERROR_722507);
			}
		} else if (managedBean.getBusinessTypeVal() != null
				&& (managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT) || managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT))) {
			if (currentPageType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				int perShrCnt = 0;
				int compShrCnt = 0;
				if (managedBean.getSmapplShareHolders() != null) {
					perShrCnt = managedBean.getSmapplShareHolders().size();
				}
				if (managedBean.getSmAppl().getSmEquityCompanies() != null) {
					compShrCnt = managedBean.getSmAppl().getSmEquityCompanies()
							.size();
				}
				if (perShrCnt + compShrCnt < 2) {
					validCount = false;
					if (currentTab != -1) {
						setNavigationBackToCurrentPage(currentTab, managedBean);
					}
					throw new EpException(MessageConst.ERROR_722592);
				}
			}
		} else if (managedBean.getBusinessTypeVal() != null
				&& managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)
				&& (EntityHelper.isCollectionEmpty(managedBean
						.getSmapplShareHolders()) || (managedBean
						.getSmapplShareHolders().size() > 20 || managedBean
						.getSmapplShareHolders().size() < 2))) {
			validCount = false;
			if (currentTab != -1) {
				setNavigationBackToCurrentPage(currentTab, managedBean);
			}
			throw new EpException(MessageConst.ERROR_722508);

		}
		return validCount;
	}

	/**
	 * Validate equity company amount total calculation and percentage summary.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - inr current tab
	 * @return true, if successful
	 */
	public static boolean validateEquityCompanyAmount(
			SmApplicationManagedBean managedBean, int currentTab) {
		boolean validAmt = true;
		if ((managedBean.getBusinessTypeVal().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT) || managedBean
				.getBusinessTypeVal()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT))) {
			managedBean.setEquiCompCapTotal(new BigDecimal(0));
			for (SmEquityCompany comp : managedBean.getSmAppl()
					.getSmEquityCompanies()) {
				if (comp.getShareAmt() == null) {
					comp.setShareAmt(new BigDecimal(0));
				}
				managedBean.setEquiCompCapTotal(managedBean
						.getEquiCompCapTotal().add(comp.getShareAmt())
						.setScale(2, RoundingMode.HALF_UP));
			}
			if (managedBean.getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getCompaniesEquityTotal()
							.compareTo(new BigDecimal(0)) > 0
					&& managedBean.getEquiCompCapTotal().compareTo(
							new BigDecimal(0)) == 0) {
				validAmt = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722583);
			} else if (managedBean.getSmAppl().getSmCompanyBasic()
					.getCompaniesEquityTotal() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getCompaniesEquityTotal()
							.compareTo(managedBean.getEquiCompCapTotal()) != 0) {
				validAmt = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722584);
			}

		}

		return validAmt;
	}

	/**
	 * Validate at least one facility is required if the facility panel is
	 * available
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static boolean validateFacilityPanel(SmApplicationManagedBean bean,
			int currentTab, boolean validSave) {
		if (bean.isShowFactoryPanel()) {
			validSave = validFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_FACTORY,
					"lbl.sm.facility.type.factory");

		}
		if (bean.isShowDockyardPanel()) {
			validSave = validFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD,
					"lbl.sm.facility.type.dockyard");
		}
		if (bean.isShowWorkShopPanel()) {
			validSave = validFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP,
					"lbl.sm.facility.type.workshop");
		}
		if (bean.isShowStorePanel()) {
			validSave = validFacility(validSave, currentTab, bean,
					SupplierManagementConst.SM_FACILITY_TYPE_STORE,
					"lbl.sm.facility.type.store");
		}
		return validSave;
	}

	private static boolean validFacility(boolean valid, int currentTab,
			SmApplicationManagedBean bean, String facType, String facTypeLabel) {
		List<SmFacility> facList = bean.getSmAppl().getSmFacilities();
		boolean facilityExist = false;
		if (!SmCommonUtil.isCollectionEmpty(facList)) {
			for (SmFacility fac : facList) {
				if (fac.getFacilityType() != null
						&& fac.getFacilityType().equals(facType)) {
					facilityExist = true;
					break;
				}
			}
			if (!facilityExist) {
				valid = false;
				if (currentTab != -1) {
					setNavigationBackToCurrentPage(currentTab, bean);
				}
				throw new EpException(MessageConst.ERROR_722621,
						new String[] { SmCommonUtil.getMessage(facTypeLabel,
								null) });
			}
		}
		return valid;
	}

	/**
	 * Validate equity owners or share holding personnel's amount total
	 * calculation and percentage summary.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 * @return true, if successful
	 */
	public static boolean validateEquityOwnerAmount(
			SmApplicationManagedBean managedBean, int currentTab,
			SmRuleHelper ruleHelper) {
		Locale locale = new FacesScopes().getUserLocale();
		boolean validAmt = true;
		String s = "";
		if (managedBean
				.getBusinessTypeVal()
				.equalsIgnoreCase(
						SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)
				|| managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)) {
			s = ResourceBundleUtils.getMessage("lbl.sm.accumulated.capital",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if (managedBean.getBusinessTypeVal().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
				|| managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_LLP_DEFAULT)) {
			s = ResourceBundleUtils.getMessage("lbl.sm.paidup.capital",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		} else if (managedBean.getBusinessTypeVal().equalsIgnoreCase(
				SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
			s = ResourceBundleUtils.getMessage("lbl.sm.authorized.share",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
		}

		if (managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital() != null) {
			boolean validPaidUp = true;
			// String amt = "";
			String supType = "";
			StringBuilder sb = new StringBuilder();
			sb.append("RM ");
			if (managedBean.getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.STYPE_CONSULTANT)
					&& managedBean
							.getSmAppl()
							.getSmCompanyBasic()
							.getPaidUpCapital()
							.compareTo(
									new BigDecimal(ruleHelper
											.getMinPaidUpCapitalForConsultant())) < 0) {
				validPaidUp = false;
				sb.append(ruleHelper.getMinPaidUpCapitalForConsultant());
				supType = ResourceBundleUtils.getMessage(
						"lbl.sm.consultant.lowercaps",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
			} else if (managedBean.getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.STYPE_CONTRACTOR)
					&& managedBean
							.getSmAppl()
							.getSmCompanyBasic()
							.getPaidUpCapital()
							.compareTo(
									new BigDecimal(ruleHelper
											.getMinPaidUpCapitalForContractor())) < 0) {
				validPaidUp = false;
				sb.append(ruleHelper.getMinPaidUpCapitalForContractor());
				supType = ResourceBundleUtils.getMessage(
						"lbl.sm.contractor.lowercaps",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
			}
			if (!sb.toString().contains(".")) {
				sb.append(".00");
			}
			if (!validPaidUp) {
				validAmt = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722555, new String[] {
						s, sb.toString(), supType });
			}
		}
		if (managedBean.getBusinessTypeVal() != null
				&& !managedBean
						.getBusinessTypeVal()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)
				&& managedBean.getSmapplShareHolders() != null) {
			StringBuilder sb = new StringBuilder();
			boolean validShrHldrsCap = true;
			int i = 0;
			BigDecimal perShrSum = new BigDecimal(0);
			for (SmPersonnel per : managedBean.getSmapplShareHolders()) {
				if (per.getShareAmt() == null
						|| per.getShareAmt().compareTo(new BigDecimal(0)) == 0) {
					sb.append("<br/>");
					sb.append(++i + ".)");
					sb.append(per.getName());
					validShrHldrsCap = false;
				} else {
					perShrSum = perShrSum.add(per.getShareAmt());
				}
			}
			if (!validShrHldrsCap) {
				validAmt = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				throw new EpException(MessageConst.ERROR_722552,
						new String[] { sb.toString() });
			}
			BigDecimal allPersCapSum = new BigDecimal(0);
			if (managedBean.getSmAppl().getSmCompanyBasic().getBumiEquity() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getNonBumiEquity() != null
					&& managedBean.getSmAppl().getSmCompanyBasic()
							.getForeignerEquity() != null) {
				allPersCapSum = allPersCapSum
						.add(managedBean.getSmAppl().getSmCompanyBasic()
								.getBumiEquity())
						.add(managedBean.getSmAppl().getSmCompanyBasic()
								.getNonBumiEquity())
						.add(managedBean.getSmAppl().getSmCompanyBasic()
								.getForeignerEquity());

			}
			// checking the personnel sum is exactly same as the sum of
			// bumi +non bumi + non malaysian
			if (perShrSum.compareTo(allPersCapSum) != 0) {
				validAmt = false;
				setNavigationBackToCurrentPage(currentTab, managedBean);
				log.info("*** Value **** " + s);
				throw new EpException(MessageConst.ERROR_722554);
			}
		}
		return validAmt;
	}

	/**
	 * Validate paid capital amount calculation for the supplier based on the
	 * supplier type.
	 * 
	 * @param setNavigationCurrentPage
	 *            -boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int current tab
	 */
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
						new String[] { SmCommonUtil.getMessage(
								"lbl.sm.authorized.capital", null) });
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
						new String[] { SmCommonUtil.getMessage(
								"label.sm.total.share", null) });
			}
		}
		appl = null;
	}

	/**
	 * Validate the selected personnel's IC and email liferay and pm.
	 * 
	 * @param smPersonnel
	 *            the sm personnel
	 */
	public static void validatePersonnelICandEmailLiferayAndPm(
			SmPersonnel smPersonnel) {

	}

	/**
	 * Checks if is exist email in pm user table.
	 * 
	 * @param email
	 *            - the email
	 * @return true, if is exist email in pm user
	 */
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

	/**
	 * Checks if is exist user by identification no.
	 * 
	 * @param idNo
	 *            - the id no
	 * @return true, if is exist user by identification no
	 */
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

	/**
	 * Gets the company id.
	 * 
	 * @return the company id
	 */
	public static String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.PORTAL_COMPANY_ID);
	}

	/**
	 * Check if selected user is valid for the application.Check for Duplicate
	 * identification number, email..etc within the same application
	 * 
	 * @param pers
	 *            - SmPersonnel object
	 * @param appl
	 *            - SmAppl object
	 * @return true, if successful
	 */
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
			if (pers.getIdentityResponseCode().equals("70020")) {
				validUser = false;
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70020);
			} else if (pers.getIdentityResponseCode().equals("70031")) {
				validUser = false;
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70031);
			}
		}
		if (!pers.isEquityOwner() && !pers.isDirector()
				&& !pers.isHigherManagement() && !pers.isManagement()
				&& !pers.isProf() && !pers.isSemiProf() && !pers.isClerical()) {
			validUser = false;
			throw new EpException(MessageConst.ERROR_722535);
		}
		for (SmPersonnel per : appl.getSmPersonnels()) {
			if (pers.getPersonnelId() == null && pers.equals(per)) {
				log.info("****** Same obj before save ");
			} else if (pers.getPersonnelId() != null
					&& pers.getPersonnelId().equals(per.getPersonnelId())) {
				log.info("**** Same obj without id ******");
			} else {
				if (per.getIdentificationNo() != null
						&& per.getIdentificationNo().equalsIgnoreCase(
								pers.getIdentificationNo())) {
					validUser = false;
					throw new EpException(MessageConst.ERROR_722524,
							new String[] { pers.getIdentificationNo() });
				}
				// if (per.getEmail() != null
				// && per.getEmail().equalsIgnoreCase(pers.getEmail())) {
				// validUser = false;
				// throw new EpException(MessageConst.ERROR_722540);
				// }
			}
		}
		if (pers.getPmUser() == null
				&& pers.getEpRole() != null
				&& !pers.getEpRole().isEmpty()
				&& (pers.getEpRole().equalsIgnoreCase(
						RoleCode.MOF_SUPPLIER_USER.value()) || pers.getEpRole()
						.equalsIgnoreCase(RoleCode.CM_USER.value())))
			validatePersonnelICandEmailLiferayAndPm(pers);
		return validUser;
	}

	/**
	 * Integration with MY Identity to validate the selected personnel
	 * 
	 * @param per
	 *            - SmPersonnel object
	 */
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
		MyIdentityResDTO resDTO;
		try {
			resDTO = myIdentitySO.myIdentityVerification(reqDTO);
			// StringBuffer sb = new StringBuffer();
			if (!StringUtils.isBlank(resDTO.getStatusCode())) {
				per.setIdentityResponseCode(resDTO.getStatusCode());
			}
			log.info("**** My identiy output is *****" + resDTO.getStatusCode());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("**********error calling myidentity api[" + e.getMessage()
					+ "]");
		}

		// sb.append("MessageConst.ERROR_MYIDENTITY_");
		// if (!resDTO.getStatusCode().equalsIgnoreCase(
		// SupplierManagementConst.SM_MYIDENTITY_STATUS_CODE_VALID)) {
		// sb.append(resDTO.getStatusCode());
		// throw new EpException(sb.toString());
		// }
	}

	/**
	 * Check if the added bank is valid.Check of Duplicate account numbers in
	 * same supplier ..etc
	 * 
	 * @param bank
	 *            - SmSupplierBank object
	 * @param appl
	 *            - SmAppl object
	 * @return true, if successful
	 */
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

	/**
	 * Sets the verification values for the sections.Update during PO or
	 * Approver review
	 * 
	 * @param sec
	 *            - SmApplSectionReview object
	 * @param isVerifiedAll
	 *            - boolean
	 * @param maxReviewCout
	 *            - int max count value
	 * @return the SmApplSectionReview object
	 */
	public static SmApplSectionReview setVerificationValues(
			SmApplSectionReview sec, Boolean isVerifiedAll, int maxReviewCout,
			SmApplicationManagedBean managedBean) {

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
		if (managedBean.isReviewSiteVisit()) {
			sec.setSitevisitVerified(isVerifiedAll);
		}
		sec.setFactoryVerify(isVerifiedAll);
		sec.setManufacturingVerified(isVerifiedAll);
		sec.setMachineryVerified(isVerifiedAll);
		sec.setLicenseVerified(isVerifiedAll);
		sec.setAssocBodyVerified(isVerifiedAll);
		sec.setSupplierSystemVerified(isVerifiedAll);

		return sec;
	}

	/**
	 * Sets the default address value.
	 * 
	 * @param defCntry
	 *            - PmCountry object
	 * 
	 * @return the SmAddress object
	 */
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
		addr.setCountryId(cntry.getCountryId());
		addr.setPmCity(new PmCity());
		addr.setPmCountry(cntry);
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());
		return addr;
	}

	/**
	 * Sets the default addr with country Malaysia and address lines empty.
	 * 
	 * @param defCntry
	 *            - PmCountry object
	 * @param addr
	 *            - SmAddress object
	 * @return the SmAddress object
	 */
	public static SmAddress setDefaultAddrWithSameId(PmCountry defCntry,
			SmAddress addr) {
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
		addr.setAddress1("");
		addr.setAddress2("");
		addr.setAddress3("");
		addr.setAddress4("");
		addr.setPostcode("");
		return addr;
	}

	/**
	 * Sets the default base office address.
	 * 
	 * @param baseOffAddr
	 *            - SmAddress object
	 * @param facAddr
	 *            - SmAddress object
	 * @return the SmAddress object
	 */
	public static SmAddress setDefaultBaseOfficeAddr(SmAddress baseOffAddr,
			SmAddress facAddr) {
		// SmAddress addr = new SmAddress();
		PmCountry cntry = new PmCountry();
		cntry = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		if (baseOffAddr.getPmCity() != null) {
			facAddr.setPmCity(baseOffAddr.getPmCity());
		} else {
			facAddr.setPmCity(new PmCity());
		}
		if (baseOffAddr.getPmCountry() != null) {
			facAddr.setPmCountry(baseOffAddr.getPmCountry());
		} else {
			facAddr.setPmCountry(cntry);
		}
		if (baseOffAddr.getPmState() != null) {
			facAddr.setPmState(baseOffAddr.getPmState());
		} else {
			facAddr.setPmState(new PmState());
		}
		if (baseOffAddr.getPmDistrict() != null) {
			facAddr.setPmDistrict(baseOffAddr.getPmDistrict());
		} else {
			facAddr.setPmDistrict(new PmDistrict());
		}
		facAddr.setAddress1(baseOffAddr.getAddress1());
		facAddr.setAddress2(baseOffAddr.getAddress2());
		facAddr.setAddress3(baseOffAddr.getAddress3());
		facAddr.setAddress4(baseOffAddr.getAddress4());
		facAddr.setPostcode(baseOffAddr.getPostcode());

		return facAddr;
	}

	/**
	 * Gets the staff list supplier type and staff type in the application.
	 * 
	 * @param app
	 *            - SmAppl object
	 * @return the SmStaffTotal list
	 */
	public static List<SmStaffTotal> getStaffList(SmAppl app) {
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		log.info("****  getStaffList *********" + app.getSupplierType());
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

	/**
	 * Gets the remarks by doctype and Id .
	 * 
	 * @param docType
	 *            - the doc type
	 * @param docId
	 *            - Long the doc id
	 * @return the remarks by doc
	 */
	public static SmRemaksResDTO getRemarksByDoc(String docType, Long docId) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocId(docId);
		reqDTO.setDocType(docType);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISmRemarksSO.class);
		SmRemaksResDTO resDTO = reqSO.retrieveSmRemarksByDocument(reqDTO);
		return resDTO;
	}

	/**
	 * Gets the sm remarks by doc type and id.
	 * 
	 * @param docType
	 *            - the doc type
	 * @param docId
	 *            - Long the doc id
	 * @return the sm remarks
	 */
	public static String getSmRemarksByDocTypeAndId(String docType, Long docId) {
		SmRemaksResDTO resDTO = getRemarksByDoc(docType, docId);
		StringBuilder sb = new StringBuilder();
		if (resDTO != null && resDTO.getRemarksList() != null
				&& !resDTO.getRemarksList().isEmpty()) {
			sb.append(resDTO.getRemarksList().get(0).getRemark());
			return sb.toString();
		} else {
			return "";
		}

	}

	/**
	 * Check over lapping categories.Checks if Same owner exist in different
	 * supplier and has same one or more similar categories
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkOverLappingCategories(
			SmApplicationManagedBean managedBean) {
		managedBean.setOverLappedCategoryExists(false);
		managedBean.setOverLappedCategoryMsg("");
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.isOverLapCategory()
					&& (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.APPROVED_RECORD_STATUS) == 0 || cat
							.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0)) {
				managedBean.setOverLappedCategoryExists(true);
			}
		}
		if (managedBean.isOverLappedCategoryExists()) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(managedBean.getSmAppl().getSmSupplier()
					.getSupplierId());
			reqDTO.setApplId(managedBean.getSmAppl().getApplId());
			List<Object[]> resultList = SmServiceLookup.getMofRegistrationSO()
					.retrieveOverLappedCategoryDetails(reqDTO).getResultList();
			managedBean.setOverLappedCategoryMsg(SmCommonUtil.getMessage(
					"lbl.sm.category.overlappingMsg", null)
					+ "<br />"
					+ getOverlappedCategoryMsg(resultList));
			Locale loc = new Locale("ms", "MY");
			ResourceBundleUtils.getMessage("lbl.sm.category.overlappingMsg",
					null, ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc);
			managedBean.setOverLappedCategoryRemarks(ResourceBundleUtils
					.getMessage("lbl.sm.category.overlappingMsg", null,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, loc)
					+ System.lineSeparator()
					+ System.lineSeparator()
					+ getOverlappedCategoryRemarks(resultList));
		}
	}

	/**
	 * Valid l3 category code for over lapping.Checks if Same owner exists in
	 * differrnt supplier and has same category
	 * 
	 * @param catL3Id
	 *            - Long the cat l3 id
	 * @param supplierId
	 *            - Long supplier id
	 * @param applId
	 *            - Long current application id
	 * @return true, if successful
	 */
	public static boolean validL3CategoryCodeForOverLapping(Long catL3Id,
			Long supplierId, Long applId) {
		log.info("*** Validate the code with directors and equi owners ***** ");
		IMofRegistrationSO mofRegistrationSO = null;
		boolean overLapCategory = false;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(supplierId);
			reqDto.setCatL3Id(catL3Id);
			reqDto.setApplId(applId);
			MofRegistrationResDTO resDTO = mofRegistrationSO
					.checkValidCategoryForSupplier(reqDto);
			if (resDTO.isInvalidCategory()) {
				overLapCategory = true;
				// throw new EpException(MessageConst.ERROR_722548);
			} else {
				overLapCategory = false;
			}
			log.info("*** Over lap record *** " + overLapCategory);
			return overLapCategory;
		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * Get overlapped category message
	 * 
	 * @param Long
	 *            catL3Id
	 * @param Long
	 *            supplierId
	 * @param Long
	 *            applId
	 * @return String
	 */
	public static String getOverlappedCategoryMsg(List<Object[]> resultList) {
		StringBuilder msg = new StringBuilder();
		if (!SmCommonUtil.isCollectionEmpty(resultList)) {
			int count = 1;
			msg.append("<table style='width: 55%;table-layout:fixed'>");
			for (Object[] arr : resultList) {
				if (arr.length == 5) {
					msg.append("<tr>");
					msg.append("<td style='width:5%'>").append(count)
							.append(")").append("</td>");
					msg.append("<td style='width:10%'>").append(arr[0])
							.append(arr[1]).append(arr[2]).append("</td>");
					msg.append("<td style='white-space: normal;'>").append("(")
							.append(arr[3]).append(" / ").append(arr[4])
							.append(")").append("</td>");
					count++;
				}
			}
			msg.append("</table><br />");
		}
		return msg.toString();
	}

	public static String getOverlappedCategoryRemarks(List<Object[]> resultList) {
		StringBuilder msg = new StringBuilder();
		String SPACE = " ";
		if (!SmCommonUtil.isCollectionEmpty(resultList)) {
			int count = 1;
			for (Object[] arr : resultList) {
				if (arr.length == 5) {
					msg.append(SPACE).append(SPACE).append(SPACE);
					msg.append(count);
					msg.append(")").append(SPACE);
					msg.append(arr[0]).append(arr[1]).append(arr[2])
							.append(SPACE).append(SPACE);
					msg.append("(").append(arr[3]).append(" / ").append(arr[4])
							.append(")").append(System.lineSeparator());
					count++;
				}
			}
		}
		return msg.toString();
	}

	/**
	 * Validate supporting docs decision by PO.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void validateSupportingDocsDecision(
			SmApplicationManagedBean managedBean) {
		for (SmApplSupportDoc doc : managedBean.getSmAppl()
				.getSmApplSupportDocs()) {
			if (doc.getVerifiedFlg() == null
					&& !EntityHelper.isCollectionEmpty(doc.getSmApplDocFiles())
					&& !StringUtils.isBlank(doc.getSmApplDocFiles().get(0)
							.getFileName())) {
				throw new EpException(MessageConst.ERROR_722559);
			}
		}
	}

	/**
	 * Validate category and docs decision.Whether verified PO and Approver
	 * before making the final decision
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void validateCategoryAndDocsDecision(
			SmApplicationManagedBean managedBean) {
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				if (managedBean.isPoUser() && cat.getPoDecFlg() == null) {
					throw new EpException(MessageConst.ERROR_722558,
							new String[] { SmCommonUtil.getMessage(
									"lbl.sm.catCodes.poAct", null) });
				} else if (managedBean.isApproverUser()
						&& cat.getApprvrDecFlg() == null) {
					throw new EpException(MessageConst.ERROR_722558,
							new String[] { SmCommonUtil.getMessage(
									"lbl.sm.catCodes.appAct", null) });
				}

			}
		}
		validateSupportingDocsDecision(managedBean);

	}

	/**
	 * Check support docs validity.Whether verified by PO.
	 * 
	 * @param validSave
	 *            - boolean
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param currentTab
	 *            - int the current tab
	 * @return true, if successful
	 */
	public static boolean checkSupportDocsValidity(boolean validSave,
			SmApplicationManagedBean managedBean, int currentTab) {
		validSave = true;
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (SmApplSupportDoc doc : managedBean.getMandatoryDocs()) {
			if (doc.getVerifiedFlg() == null) {
				validSave = false;
				sb.append("<br/>").append(++i).append(".)")
						.append(doc.getPmDocument().getDocName());
			}
		}
		if (!validSave) {
			validSave = false;
			setNavigationBackToCurrentPage(currentTab, managedBean);
			throw new EpException(MessageConst.ERROR_722562,
					new String[] { sb.toString() });
		}
		return validSave;
	}

	/**
	 * Sets the cancellation sm attachment file.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void setCancellationSmAttachment(
			SmApplicationManagedBean managedBean) {
		SmAttachment attach = new SmAttachment();
		attach.setDocId(managedBean.getSmAppl().getApplId());
		attach.setDocType(managedBean.getSmAppl().getApplNo().substring(0, 2));
		managedBean.setCancellationAttachment(attach);
	}

	/**
	 * Assign active category list with status Active or Pending approval
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void assignActiveAndApprovedCategoryList(
			SmApplicationManagedBean managedBean) {

		if (managedBean.getSmAppl().getApplType()
				.equals(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			assignExistingAndAdditionalCategoryList(managedBean);
		} else {
			List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
			List<SmSupplierCategory> apprvList = new ArrayList<SmSupplierCategory>();
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					catList.add(cat);
				} else if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
					apprvList.add(cat);

				}
			}
			if (managedBean.getActiveCategoryList() != null
					&& !managedBean.getActiveCategoryList().isEmpty()) {
				managedBean.getActiveCategoryList().clear();
			}
			if (managedBean.getApprovedCategoryList() != null
					&& !managedBean.getApprovedCategoryList().isEmpty()) {
				managedBean.getApprovedCategoryList().clear();
			}
			sortSupplierCategory(apprvList);
			managedBean.setActiveCategoryList(catList);
			managedBean.setApprovedCategoryList(apprvList);
		}
	}

	private static void assignExistingAndAdditionalCategoryList(
			SmApplicationManagedBean managedBean) {
		List<SmSupplierCategory> addtnlList = new ArrayList<SmSupplierCategory>();
		List<SmSupplierCategory> existingList = new ArrayList<SmSupplierCategory>();
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0
					&& cat.getPmCategoryL3() != null) {

				SmSupplierCategory tmp = (SmSupplierCategory) EntityHelper
						.getEntityBySinglePropertyFromList(cat
								.getPmCategoryL3().getCategoryL3Id(),
								managedBean.getSmAppl().getSmSupplier()
										.getSmAppl().getSmSupplierCategories(),
								"pmCategoryL3");
				if (tmp != null
						&& (tmp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
								|| tmp.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL || tmp
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
					existingList.add(cat);
				} else if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					addtnlList.add(cat);
				}
			}
		}
		if (managedBean.getActiveCategoryList() != null
				&& !managedBean.getActiveCategoryList().isEmpty()) {
			managedBean.getActiveCategoryList().clear();
		}
		if (managedBean.getApprovedCategoryList() != null
				&& !managedBean.getApprovedCategoryList().isEmpty()) {
			managedBean.getApprovedCategoryList().clear();
		}
		sortSupplierCategory(existingList);
		managedBean.setActiveCategoryList(addtnlList);
		managedBean.setApprovedCategoryList(existingList);
	}

	/**
	 * Sort SmSupplierCategory by category type and category level code
	 * 
	 * @param apprvList
	 */
	public static void sortSupplierCategory(List<SmSupplierCategory> apprvList) {
		// sort by code
		Collections.sort(apprvList, new Comparator<SmSupplierCategory>() {
			@Override
			public int compare(final SmSupplierCategory s1,
					final SmSupplierCategory s2) {
				return (s1.getPmCategoryL1().getCategoryL1Code()
						+ s1.getPmCategoryL2().getCategoryL2Code() + s1
						.getPmCategoryL3().getCategoryL3Code()).compareTo(s2
						.getPmCategoryL1().getCategoryL1Code()
						+ s2.getPmCategoryL2().getCategoryL2Code()
						+ s2.getPmCategoryL3().getCategoryL3Code());
			}
		});

		// sort by category type
		Collections.sort(apprvList, new Comparator<SmSupplierCategory>() {
			@Override
			public int compare(final SmSupplierCategory s1,
					final SmSupplierCategory s2) {
				return Boolean.compare(s1.isSpecialCategory(),
						s2.isSpecialCategory());
			}
		});
	}

	/**
	 * Sort personnels by role in company
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public static void sortPersonnelByRoleInCompany(SmAppl appl) {
		List<SmPersonnel> equityOwners = new ArrayList<SmPersonnel>();
		List<SmPersonnel> directors = new ArrayList<SmPersonnel>();
		List<SmPersonnel> higherManagements = new ArrayList<SmPersonnel>();
		List<SmPersonnel> managements = new ArrayList<SmPersonnel>();
		List<SmPersonnel> profs = new ArrayList<SmPersonnel>();
		List<SmPersonnel> semiProfs = new ArrayList<SmPersonnel>();
		List<SmPersonnel> staffs = new ArrayList<SmPersonnel>();
		List<SmPersonnel> noRoles = new ArrayList<SmPersonnel>();
		if (appl != null
				&& !EntityHelper.isCollectionEmpty(appl.getSmPersonnels())) {

			for (SmPersonnel per : appl.getSmPersonnels()) {
				if (per.isEquityOwner()) {
					equityOwners.add(per);
				} else if (!per.isEquityOwner() && per.isDirector()) {
					directors.add(per);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& per.isHigherManagement()) {
					higherManagements.add(per);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && per.isManagement()) {
					managements.add(per);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& per.isProf()) {
					profs.add(per);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& !per.isProf() && per.isSemiProf()) {
					semiProfs.add(per);
				} else if (!per.isEquityOwner() && !per.isDirector()
						&& !per.isHigherManagement() && !per.isManagement()
						&& !per.isProf() && !per.isSemiProf()
						&& per.isClerical()) {
					staffs.add(per);
				} else {
					noRoles.add(per);
				}
			}

			appl.getSmPersonnels().clear();
			if (!EntityHelper.isCollectionEmpty(equityOwners)) {
				appl.getSmPersonnels().addAll(equityOwners);
			}
			if (!EntityHelper.isCollectionEmpty(directors)) {
				appl.getSmPersonnels().addAll(directors);
			}
			if (!EntityHelper.isCollectionEmpty(higherManagements)) {
				appl.getSmPersonnels().addAll(higherManagements);
			}
			if (!EntityHelper.isCollectionEmpty(managements)) {
				appl.getSmPersonnels().addAll(managements);
			}
			if (!EntityHelper.isCollectionEmpty(profs)) {
				appl.getSmPersonnels().addAll(profs);
			}
			if (!EntityHelper.isCollectionEmpty(semiProfs)) {
				appl.getSmPersonnels().addAll(semiProfs);
			}
			if (!EntityHelper.isCollectionEmpty(staffs)) {
				appl.getSmPersonnels().addAll(staffs);
			}
			if (!EntityHelper.isCollectionEmpty(noRoles)) {
				appl.getSmPersonnels().addAll(noRoles);
			}
		}
	}

	/**
	 * Assign professional body list for the normal screen, not in the personnel
	 * section.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void assignNonPersonnelProfBodyList(
			SmApplicationManagedBean managedBean) {
		List<SmAssociationBody> assocBodyList = new ArrayList<SmAssociationBody>();
		for (SmAssociationBody profBdy : managedBean.getSmAppl()
				.getSmAssociationBodies()) {
			if (profBdy.getSmPersonnel() == null
					|| profBdy.getSmPersonnel() != null
					&& profBdy.getSmPersonnel().getPersonnelId() == null) {
				assocBodyList.add(profBdy);
			}
		}
		if (managedBean.getNonPersonnelProfBodyList() != null
				&& !managedBean.getNonPersonnelProfBodyList().isEmpty()) {
			managedBean.getNonPersonnelProfBodyList().clear();
		}
		managedBean.setNonPersonnelProfBodyList(assocBodyList);
	}

	/**
	 * Sets the default staff values.
	 * 
	 * @param stf
	 *            - SmStaffTotal object
	 * @param app
	 *            - SmAppl object
	 * @param category
	 *            - the category i.e. prof ,semi prof,clerical
	 * @return the SmStaffTotal object
	 */
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

	/**
	 * Gets the PO login id, to resubmit or route the application.
	 * 
	 * @param statusId
	 *            - Long status id
	 * @param secondaryStatusId
	 *            - Long secondary status id
	 * @param docId
	 *            -Long doc id
	 * @return the PO login id
	 */
	public static String getPOLoginId(Long statusId, Long secondaryStatusId,
			Long docId) {
		log.info("*** This is resubmit to get po ****** ");
		SmWorkFlowStatusReqDTO reqDTO = new SmWorkFlowStatusReqDTO();
		reqDTO.setStatusId(statusId);
		reqDTO.setSecondaryStatusId(secondaryStatusId);
		reqDTO.setApplId(docId);
		ISmWorkFlowStatusSO reqSO = (ISmWorkFlowStatusSO) BusinessDelegate
				.getInstance()
				.getServiceObject("sm", ISmWorkFlowStatusSO.class);
		SmWorkFlowStatusResDTO resDTO = reqSO
				.retrieveCurrentWorkDFlowByDocAndWithinStatusIds(reqDTO);
		String poLogin = null;
		if (resDTO != null && resDTO.getWorkflowStatus() != null
				&& resDTO.getWorkflowStatus().getCreatedBy() != null) {
			log.info("******** The user id for PO from the data ******* "
					+ resDTO.getWorkflowStatus().getCreatedBy());
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(resDTO.getWorkflowStatus().getCreatedBy());
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IUserSO.class);
			UserResDTO userResDTO = userSO.retrieveUserByUserId(userReqDTO);
			if (userResDTO != null && userResDTO.getPmUser() != null) {
				poLogin = userResDTO.getPmUser().getLoginId();
				log.info("******* After retrieve po details ******** ");
			}
		}
		return poLogin;
	}

	/**
	 * Gets the PO login id, to resubmit or route the application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean
	 * 
	 * @return the PO login id
	 */
	public static String getPOLoginId(SmSupplier supplier, SmAppl smAppl) {
		log.info("*** This is resubmit to get po from MOF API ****** ");
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		reqDTO.setAppType(smAppl.getApplType());
		StringBuilder sb = new StringBuilder();
		sb.append("(")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER)
				.append(",")
				.append(SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM)
				.append(")");
		reqDTO.setWorkflowStatus(sb.toString());
		IMofRegistrationSO reqSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationResDTO resDTO = reqSO
				.retriveApplIdForLastRejectedApplicationByApplType(reqDTO);
		String poLogin = null;
		if (resDTO != null && resDTO.getApplId() != null) {

			return getPOLoginId(
					SupplierManagementConst.PM_STATUS_PO_ESCALTED_TO_APPROVER,
					SupplierManagementConst.PM_STATUS_PO_REJECTED_APPLICATION,
					resDTO.getApplId());

		}
		return poLogin;
	}

	/**
	 * Check if there is pending activation of personnels in the supplier
	 * 
	 * @param supplierId
	 *            -Long supplier id
	 * @return true, if successful
	 */
	public static boolean checkPendingActivationPersonnelList(Long supplierId) {
		boolean result = false;
		IPersonnelSO smPersonnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		PersonnelResDTO resDTO = new PersonnelResDTO();
		resDTO = smPersonnelSO
				.retrievePersonnelPendingActivationBySupplierId(reqDTO);
		List<SmPersonnel> smPersonnelList = resDTO.getSmPersonnelList();
		if (smPersonnelList != null && !smPersonnelList.isEmpty()) {
			result = true;
			throw new EpException(MessageConst.ERROR_722567);
		}
		return result;
	}

	/**
	 * Check if the equity owner can be updated or just view only for this
	 * application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkEquityOwnerUpdateAllowance(
			SmApplicationManagedBean managedBean) {
		managedBean.setDisAllowOwnerEdit(true);
		if (isMofAccEffDatePassedCurrDate(managedBean.getSmSupplier())) {
			managedBean.setDisAllowOwnerEdit(false);
		}
	}

	/**
	 * Validate the status before proceeding with application.
	 * 
	 * @param supplierId
	 *            - Long supplier id
	 * @param applicationType
	 *            - the application type
	 * @return true, if successful
	 */
	public static boolean validateBeforeProceed(Long supplierId,
			String applicationType) {
		boolean result = false;
		MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
		MofRegistrationResDTO resDto = new MofRegistrationResDTO();
		reqDto.setSupplierId(supplierId);
		resDto = SmServiceLookup.getMofRegistrationSO()
				.retrieveSupplierProfile(reqDto);
		if (resDto.getSmSupplier() != null) {
			SmSupplier smSupplier = resDto.getSmSupplier();

			if (smSupplier.getSmAppl() != null
					&& !smSupplier.getSmAppl().isQuestionnaire()) {
				result = true;
				throw new EpException(MessageConst.ERROR_722576);
			}
			if (smSupplier.getSmAppl() != null
					&& smSupplier
							.getSmAppl()
							.getSupplierType()
							.equalsIgnoreCase(
									SupplierManagementConst.STYPE_CONSULTANT)
					&& applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
				result = true;
				throw new EpException(MessageConst.ERROR_722566);
			}
			List<SmMofAccount> accounts = smSupplier.getSmMofAccounts();
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						smSupplier.getSmAppl().getSupplierType())
						&& account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
					Timestamp currentDate = new Timestamp(
							System.currentTimeMillis());
					long diff = account.getExpDate().getTime()
							- currentDate.getTime();
					long diffDays = diff / (24 * 60 * 60 * 1000);
					if (!applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
						Date today = new Date();
						if (!(today.compareTo(account.getExpDate()) < 0)) {
							result = true;
							throw new EpException(MessageConst.ERROR_722564);
						}
					}
					if (applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
						if (diffDays > 90) {
							result = true;
							throw new EpException(MessageConst.ERROR_722501);
						} else if (diffDays < -365) {
							result = true;
							throw new EpException(MessageConst.ERROR_722502);
						}
					} else if (applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)
							&& (diffDays >= 0 && diffDays < 90)) {
						result = true;
						log.debug("***************MOF Account Expiry near for Ac No.: "
								+ account.getMofNo()
								+ "\nExp Date:"
								+ account.getExpDate()
								+ "\nDiff Days:"
								+ diffDays);
						throw new EpException(MessageConst.ERROR_722503);
					} else if (applicationType
							.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
							&& smSupplier.isBumi()) {
						result = true;
						throw new EpException(MessageConst.ERROR_722504);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Checks if the supplier has any active transactions.
	 * 
	 * @param smSupplier
	 *            - SmSupplier object
	 * @return true, if successful
	 */
	public static boolean hasActiveTransactions(SmSupplier smSupplier) {

		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());

		MofRegistrationResDTO resDTO = mofRegistrationSO
				.hasActiveTransactionsForSupplier(reqDTO);

		return resDTO.isHasActiveTransaction();
	}

	/**
	 * Check if there is any active sm appl for supplier.
	 * 
	 * @param smSupplier
	 *            - SmSupplier object
	 * @return true, if successful
	 */
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

	/**
	 * Retrieve pm country list.
	 * 
	 * @return the list of PmCountry
	 */
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

	/**
	 * Generate new application number based on application , supplier type.
	 * 
	 * @param applType
	 *            - the appl type
	 * @param suppType
	 *            - the supp type
	 * @return the application number
	 */
	public static String generateApplicationNum(String applType, String suppType) {
		IMofRegistrationSO reqSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setAppType(applType);
		reqDTO.setSupplierType(suppType);
		MofRegistrationResDTO resDTO = reqSO.generateApplicaitonNumber(reqDTO);
		if (resDTO != null && resDTO.getApplNum() != null) {
			return resDTO.getApplNum();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve pm cert body list based on certType.
	 * 
	 * @param certType
	 *            - the cert type
	 * @return the list of PmCertBody
	 */
	public static List<PmCertBody> retrievePmCertBodyList(String certType) {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = null;
		if (certType.equalsIgnoreCase("GLP")) {
			resDTO = reqSO.retrieveGlpBodyList(new CertBodyReqDTO());
		} else {
			resDTO = reqSO.retrieveNonGlpBodyList(new CertBodyReqDTO());
		}
		if (resDTO != null) {
			return resDTO.getCertBodyList();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve pm race list.
	 * 
	 * @return the list PmRace
	 */
	public static List<PmRace> retrievePmRaceList() {
		List<PmRace> retVal = null;
		IRaceSO reqSO = (IRaceSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IRaceSO.class);
		RaceResDTO resDTO = reqSO.retrieveRaceList();
		if (resDTO != null
				&& !EntityHelper.isCollectionEmpty(resDTO.getRaceList())) {
			retVal = new ArrayList<PmRace>();
			for (PmRace race : resDTO.getRaceList()) {
				if (race.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					retVal.add(race);
				}
			}
		}
		return retVal;
	}

	/**
	 * Retrieve pm professional body list.
	 * 
	 * @return the list of PmCertBody
	 */
	public static List<PmCertBody> retrievePmProfBodyList() {
		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();
		if (resDTO != null) {
			List<PmCertBody> pmCertBodyList = new ArrayList<PmCertBody>();
			PmCertBody otherTypeCertBody = null;
			for (PmCertBody certBody : resDTO.getCertBodyList()) {
				if (certBody.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					if (certBody.getBodyName() != null
							&& (certBody.getBodyName().contains("LAIN-LAIN") || certBody
									.getBodyName().contains("OTHERS"))) {
						otherTypeCertBody = certBody;
					} else {
						pmCertBodyList.add(certBody);
					}
				}
			}
			if (otherTypeCertBody != null) {
				pmCertBodyList.add(otherTypeCertBody);
			}
			return pmCertBodyList;
		} else {
			return null;
		}
	}

	/**
	 * Get PmProfBody as select items
	 * 
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getProfBodyItems() {
		List<SelectItem> retVal = new ArrayList<SelectItem>();
		List<PmCertBody> certBodyList = retrievePmProfBodyList();
		if (!EntityHelper.isCollectionEmpty(certBodyList)) {
			SelectItem item = null;
			for (PmCertBody certBody : certBodyList) {
				item = new SelectItem();
				item.setValue(certBody.getCertBodyId());
				item.setLabel(certBody.getBodyName());
				retVal.add(item);
			}
		}
		return retVal;
	}

	/**
	 * Retrieve parent category list for experience page.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL1
	 */
	public static List<PmCategoryL1> retrieveParentCategoryListForExperiencePage(
			SmApplicationManagedBean managedBean) {
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(managedBean.getSmSupplier()
				.getBusinessType());
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);
		if (resDTO != null) {
			return resDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve parent category list for category codes page.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL1
	 */
	public static List<PmCategoryL1> retrieveParentCategoryListForCategoryCodesPage(
			SmApplicationManagedBean managedBean) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		log.info("** getSupplierType type ***"
				+ managedBean.getSmAppl().getSupplierType());
		log.info("** getApplCategoryType type ***"
				+ managedBean.getSmAppl().getApplCategoryType());
		reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		if (managedBean.getBasicCompanyFacility() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType()
						.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital() != null) {
			reqDTO.setPaidUpCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital().intValue());
		}
		Integer bumiPerc = new Integer(0);
		if (managedBean.getSmAppl().getSmCompanyBasic().getBumiPercentage() != null) {
			bumiPerc = managedBean.getSmAppl().getSmCompanyBasic()
					.getBumiPercentage().intValue();
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		if (managedBean.getSmAppl().getApplCategoryType().equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		log.info("*** Bumi Percent Sent for Category L1 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL1s(reqDTO);

		return resDTO.getCategoryL1List();
	}

	/**
	 * Retrieve child category list for company experience screen ,based on
	 * parent category id.
	 * 
	 * @param categoryL1Id
	 *            - Long category l1 id
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL2
	 */
	public static List<PmCategoryL2> retrieveExperienceChildCategoryList(
			long categoryL1Id, SmApplicationManagedBean managedBean) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		// reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);

		return resDTO.getCategoryL2List();
	}

	/**
	 * Retrieve child category list based on the parent catgory Id.
	 * 
	 * @param categoryL1Id
	 *            - Long category l1 id
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL2
	 */
	public static List<PmCategoryL2> retrieveChildCategoryList(
			long categoryL1Id, SmApplicationManagedBean managedBean) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		if (managedBean.getBasicCompanyFacility() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType()
						.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital() != null) {
			reqDTO.setPaidUpCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital().intValue());
		}
		if (managedBean.getSmAppl().getApplCategoryType().equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		Integer bumiPerc = new Integer(0);
		if (managedBean.getSmAppl().getSmCompanyBasic().getBumiPercentage() != null) {
			bumiPerc = managedBean.getSmAppl().getSmCompanyBasic()
					.getBumiPercentage().intValue();
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		reqDTO.setBusinessTypeCode(managedBean.getSmSupplier()
				.getBusinessType());
		log.info("*** Bumi Percent Sent for Category L2 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2s(reqDTO);

		return resDTO.getCategoryL2List();
	}

	/**
	 * Retrieve sub child category list for company experience screen ,based on
	 * child category id.
	 * 
	 * @param categoryL2Id
	 *            - Long category l2 id
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL3
	 */
	public static List<PmCategoryL3> retrieveExperienceSubChildCategoryList(
			long categoryL2Id, SmApplicationManagedBean managedBean) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		// reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		return resDTO.getCategoryL3List();
	}

	/**
	 * Retrieve sub child category list based on child category Id.
	 * 
	 * @param categoryL2Id
	 *            - Long category l2 id
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the list of PmCategoryL3
	 */
	public static List<PmCategoryL3> retrieveSubChildCategoryList(
			long categoryL2Id, SmApplicationManagedBean managedBean) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		log.info("** getSupplierType type ***"
				+ managedBean.getSmAppl().getSupplierType());
		log.info("** getApplCategoryType type ***"
				+ managedBean.getSmAppl().getApplCategoryType());
		reqDTO.setSupplierTypeCode(managedBean.getSmAppl().getSupplierType());
		reqDTO.setBusinessTypeCode(managedBean.getSmSupplier()
				.getBusinessType());
		if (managedBean.getSmAppl().getApplCategoryType().equals("N")) {
			reqDTO.setIsSpecialCategory(false);
		} else {
			reqDTO.setIsSpecialCategory(true);
		}
		if (managedBean.getBasicCompanyFacility() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType() != null
				&& managedBean.getBasicCompanyFacility().getFacilityType()
						.equalsIgnoreCase("H")) {
			reqDTO.setHomeOperated(1);
		} else {
			reqDTO.setHomeOperated(0);
		}
		if (managedBean.getSmAppl().getSmCompanyBasic().getPaidUpCapital() != null) {
			reqDTO.setPaidUpCapital(managedBean.getSmAppl().getSmCompanyBasic()
					.getPaidUpCapital().intValue());
		}
		Integer bumiPerc = new Integer(0);
		if (managedBean.getSmAppl().getSmCompanyBasic().getBumiPercentage() != null) {
			bumiPerc = managedBean.getSmAppl().getSmCompanyBasic()
					.getBumiPercentage().intValue();
			if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
				bumiPerc = 99;
			}
		}
		log.info("*** Bumi Percent Sent for Category L3 ***** " + bumiPerc);
		reqDTO.setBumiPercentage(bumiPerc);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);

		return resDTO.getCategoryL3List();
	}

	/**
	 * Check if the selected categories have any pending transaction.
	 * 
	 * @param categories
	 *            - List of categories
	 * @param supplierId
	 *            - Long supplier id
	 * @return true, if successful
	 */
	public static boolean checkCategoryPendingTransaction(
			List<SmSupplierCategory> categories, Long supplierId) {
		log.info("*** Validate the code having active transaction ***** ");
		IMofRegistrationSO mofRegistrationSO = null;
		boolean hasActiveTransactionCategory = false;
		try {
			mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm",
							IMofRegistrationSO.class);
			MofRegistrationReqDTO reqDto = new MofRegistrationReqDTO();
			reqDto.setSupplierId(supplierId);
			StringBuilder sb = new StringBuilder();
			int i = 0;
			sb.append(SmCommonUtil.getMessage(
					"lbl.sm.category.pendingTransaction", null));
			for (SmSupplierCategory category : categories) {
				if (category.getRecordStatusFlg().compareTo(
						SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
					reqDto.setCatL3Id(category.getPmCategoryL3()
							.getCategoryL3Id());
					MofRegistrationResDTO resDTO = mofRegistrationSO
							.hasActiveTransactionsForSupplierAndCategoryCode(reqDto);
					log.info("**** Active Transactions ********"
							+ resDTO.isHasActiveTransaction());
					if (resDTO.isHasActiveTransaction()) {
						hasActiveTransactionCategory = true;
						sb.append("<br/>")
								.append(++i)
								.append(".) ")
								.append(category.getPmCategoryL1()
										.getCategoryL1Code())
								.append(category.getPmCategoryL2()
										.getCategoryL2Code())
								.append(category.getPmCategoryL3()
										.getCategoryL3Code());
					}
				}
			}
			log.info("*** Has active transaction  *** "
					+ hasActiveTransactionCategory);
			if (hasActiveTransactionCategory) {
				throw new EpException(sb.toString());
			}
			return hasActiveTransactionCategory;
		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * Retrieve uom list.
	 * 
	 * @return the list of PmUom
	 */
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

	public static Long retrieveOtherRejectReasonIdByParamCodeAndType(
			String paramCode, String others) {
		Long id = null;
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		IReasonSO resReqSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IReasonSO.class);

		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterCode(paramCode);
		reqDTO.setParameterType("RC");
		ParameterResDTO resDTO = reqSO.retrieveParameterByTypeAndCode(reqDTO);
		if (resDTO != null && resDTO.getPmParameter() != null) {
			ReasonReqDTO resReqDTO = new ReasonReqDTO();
			resReqDTO.setReasonCategoryId(resDTO.getPmParameter()
					.getParameterId());
			resReqDTO.setLanguage(new FacesScopes().getUserLocale()
					.getLanguage());
			resReqDTO.setReasonCode(others);
			ReasonResDTO resResDTO = resReqSO
					.retrieveReasonByReasonCode(resReqDTO);
			if (resResDTO != null && resResDTO.getPmReason() != null) {
				id = resResDTO.getPmReason().getReasonId();
			} else {
				id = null;
			}
		} else {
			id = null;
		}

		return id;
	}

	/**
	 * Gets the uom list for select items in drop down.
	 * 
	 * @return the uom select items
	 */
	public static List<SelectItem> getUomSelectItems() {
		List<SelectItem> uomSelectItems = new ArrayList<SelectItem>();
		List<PmUom> uomList = retrieveUomList();

		SelectItem si = null;
		if (uomList != null) {
			for (PmUom pmUom : uomList) {
				if (pmUom.getUomId() != null && pmUom.getUomName() != null
						&& !StringUtils.isEmpty(pmUom.getUomName())) {
					si = new SelectItem();
					si.setLabel(pmUom.getUomName());
					si.setValue(pmUom.getUomId());
					uomSelectItems.add(si);
				}
			}
		}

		// sort
		Collections.sort(uomSelectItems, new Comparator<SelectItem>() {
			@Override
			public int compare(final SelectItem s1, final SelectItem s2) {
				return StringUtil.upperCase(s1.getLabel()).compareTo(
						StringUtil.upperCase(s2.getLabel()));
			}
		});
		return uomSelectItems;
	}

	/**
	 * Gets the race list for select items in drop down.
	 * 
	 * @return the race select items
	 */
	public static List<SelectItem> getRaceSelectItems() {
		List<SelectItem> raceSelectItems = new ArrayList<SelectItem>();
		List<PmRace> racesList = retrievePmRaceList();

		SelectItem si = null;
		if (racesList != null) {
			for (PmRace pmRace : racesList) {
				if (pmRace.getRaceId() != null && pmRace.getRaceName() != null
						&& !StringUtils.isEmpty(pmRace.getRaceName())) {
					si = new SelectItem();
					si.setLabel(pmRace.getRaceName());
					si.setValue(pmRace.getRaceId());
					raceSelectItems.add(si);
				}
			}
		}

		// sort
		Collections.sort(raceSelectItems, new Comparator<SelectItem>() {
			@Override
			public int compare(final SelectItem s1, final SelectItem s2) {
				return s1.getLabel().compareTo(s2.getLabel());
			}
		});

		return raceSelectItems;
	}

	/**
	 * Retrieve reject reason list.
	 * 
	 * @return the list PmReasonDesc of type rejection reason
	 */
	public static List<PmReasonDesc> retrieveRejectReasonListByParameterCode(
			String paramterCode) {
		List<PmReasonDesc> reasonDescList = null;
		IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IReasonSO.class);
		ReasonReqDTO reqDTO = new ReasonReqDTO();
		reqDTO.setReasonCategory(paramterCode);
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		ReasonResDTO resDTO = reqSO.retrieveReasonDescListByCategory(reqDTO);

		if (resDTO != null) {
			reasonDescList = resDTO.getReasonDescList();
			if (!EntityHelper.isCollectionEmpty(reasonDescList)) {
				// sort
				Collections.sort(reasonDescList,
						new Comparator<PmReasonDesc>() {
							@Override
							public int compare(final PmReasonDesc s1,
									final PmReasonDesc s2) {
								return StringUtil.upperCase(s1.getReasonDesc())
										.compareTo(
												StringUtil.upperCase(s2
														.getReasonDesc()));
							}
						});
			}
		}
		return reasonDescList;
	}

	// @SuppressWarnings("unused")
	/**
	 * Retrieve registration status list.
	 * 
	 * @return the list if PmStatusDesc of type education field
	 */
	public List<PmStatusDesc> retrieveRegistrationStatusList(String suppType,
			String applType) {
		List<PmStatusDesc> descList = new ArrayList<PmStatusDesc>();
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		if (applType.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)) {
			descList = SmServiceLookup.getStatusSO()
					.retrieveRegStatusListForBumi(reqDTO).getStatusDescList();
		} else {
			descList = SmServiceLookup.getStatusSO()
					.retrieveRegStatusList(reqDTO).getStatusDescList();
			if (!EntityHelper.isCollectionEmpty(descList)
					&& !StringUtils.isBlank(suppType) && suppType.equals("J")) {
				List<PmStatusDesc> toRemoveList = new ArrayList<PmStatusDesc>();
				for (PmStatusDesc statDesc : descList) {
					if (statDesc.getPmStatus() != null
							&& statDesc.getPmStatus().getPmRegStatus() != null
							&& statDesc.getPmStatus().getPmRegStatus()
									.isBumiAppl()) {
						toRemoveList.add(statDesc);
					}
				}
				EntityHelper.compareAndRemoveEntityFromList(toRemoveList,
						descList, "statusDescId");
			}
		}
		return descList;
	}

	/**
	 * Retrieve education field list.
	 * 
	 * @return the list of PmEduField
	 */
	public static List<PmEduField> retrieveEduFieldList() {
		List<PmEduField> eduFieldList = null;
		EducationFieldReqDTO reqDTO = new EducationFieldReqDTO();
		IEducationFieldSO reqSO = (IEducationFieldSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IEducationFieldSO.class);
		EducationFieldResDTO resDTO = reqSO.retrieveEducationFieldList(reqDTO);
		if (resDTO != null) {
			eduFieldList = new ArrayList<PmEduField>();
			for (PmEduField eduField : resDTO.getEduFieldList()) {
				if (eduField.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					eduFieldList.add(eduField);
				}
			}

			// sort
			Collections.sort(eduFieldList, new Comparator<PmEduField>() {
				@Override
				public int compare(final PmEduField s1, final PmEduField s2) {
					String field1 = StringUtils.isBlank(s1.getFieldName()) ? StringUtils.EMPTY
							: s1.getFieldName();
					String field2 = StringUtils.isBlank(s2.getFieldName()) ? StringUtils.EMPTY
							: s2.getFieldName();
					return StringUtil.upperCase(field1).compareTo(
							StringUtil.upperCase(field2));
				}
			});
		}
		return eduFieldList;
	}

	/**
	 * Retrieve pm country by id.
	 * 
	 * @param countryId
	 *            - Long country id
	 * @return PmCountry object
	 */
	public static PmCountry retrievePmCountryById(Long countryId) {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryReqDTO reqDTO = new CountryReqDTO();
		reqDTO.setCountryId(countryId);
		CountryResDTO resDTO = reqSO.retrieveCountryById(reqDTO);

		return resDTO.getPmCountry();
	}

	/**
	 * Retrieve pm state by id.
	 * 
	 * @param stateId
	 *            - Long state id
	 * @return PmState object
	 */
	public static PmState retrievePmStateById(Long stateId) {
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);
		StateResDTO resDTO = reqSO.retriveStateDetailsByID(reqDTO);

		return resDTO.getPmState();
	}

	/**
	 * Retrieve zone by state id.
	 * 
	 * @param stateId
	 *            - Long state id
	 * @return PmZone object
	 */
	public static PmZone retrieveZoneByStateId(Long stateId) {
		IZoneSO zoneSO = (IZoneSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IZoneSO.class);

		ZoneReqDTO reqDTO = new ZoneReqDTO();
		reqDTO.setStateId(stateId);

		ZoneResDTO resDTO = new ZoneResDTO();
		resDTO = zoneSO.retrieveZoneByStateId(reqDTO);
		if (resDTO != null && resDTO.getPmZone() != null) {
			return resDTO.getPmZone();
		} else {
			throw new EpException(
					"Cannot Submit Application : The State or Zone for supplier base office is empty or invalid.Please select valid address");
		}
	}

	/**
	 * Retrieve pm city by id.
	 * 
	 * @param cityId
	 *            - Long the city id
	 * @return PmCity object
	 */
	public static PmCity retrievePmCityById(Long cityId) {
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setCityId(cityId);
		CityResDTO resDTO = reqSO.retriveCityById(reqDTO);

		return resDTO.getPmCity();
	}

	/**
	 * Retrieve pm dist by id.
	 * 
	 * @param distId
	 *            - Long the dist id
	 * @return PmDistrict object
	 */
	public static PmDistrict retrievePmDistById(Long distId) {
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(distId);
		DistrictResDTO resDTO = reqSO.retriveDistrictByID(reqDTO);

		return resDTO.getPmDistrict();
	}

	/**
	 * Retrive application by id.
	 * 
	 * @param applId
	 *            - Long the appl id
	 * @return the mof registration res dto object
	 */
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
			log.info("**** THIS IS THE EXCEPTION IN RETRIVE LIST ****");
			MofRegistrationResDTO resDTO = new MofRegistrationResDTO();
			resDTO.setErrorCaued(Boolean.TRUE);
			return resDTO;
		} finally {
			mofRegistrationSO = null;
		}

	}

	/**
	 * Gets the last approved appliction for supplier.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return SmAppl object
	 */
	public static SmAppl getLastApprovedApplictionForSupplier(
			SmApplicationManagedBean managedBean) {
		ISmApplRevisionSO revisionSO = null;
		SmAppl appl = null;
		try {
			revisionSO = (ISmApplRevisionSO) BusinessDelegate.getInstance()
					.getServiceObject("sm", ISmApplRevisionSO.class);
			SmApplRevisionReqDTO reqDto = new SmApplRevisionReqDTO();
			if (managedBean.getSmAppl() != null
					&& managedBean.getSmAppl().getApprovedSerFile() != null) {
				reqDto.setDeSerFileName(managedBean.getSmAppl()
						.getApprovedSerFile());
				SmApplRevisionResDTO resDTO = revisionSO
						.readApplicationHist(reqDto);
				if (resDTO != null) {
					appl = resDTO.getSmAppl();
				}
			}
		} finally {
			revisionSO = null;
		}
		return appl;

	}

	/**
	 * Sets the navigation for application page based on application type.
	 * 
	 * @param appType
	 *            - the app type
	 * @return the redirect page
	 */
	public static String setNavigationForAppl(String appType) {
		String s = null;
		switch (appType) {

		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG;
			break;
		case "A":
			// TODO changed to satisfy enhancement of adding the bank section
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

	/**
	 * Sets the navigation from application terms and conditions page based on
	 * application type.
	 * 
	 * @param applType
	 *            - the application type
	 */
	public static void setNavigationForApplTnCPage(String applType) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String s = "";

		switch (applType) {
		case "N":
			s = SupplierManagementConst.PROCEED_MOF_REG_TNC_PAGE;
			break;
		case "A":
			s = SupplierManagementConst.PROCEED_ADDTNLCAT_APPLN_TNC_PAGE;
			// PROCEED_ADDTNLCAT_MOF_APPLN;
			break;
		case "B":
			s = SupplierManagementConst.PROCEED_BUMI_APPLN_TNC_PAGE;
			// PROCEED_BUMI_APPLN;
			break;
		case "U":
			s = SupplierManagementConst.PROCEED_UPDATE_APPLN_TNC_PAGE;
			// PROCEED_UPDATE_MOF_APPLN;
			break;
		case "R":
			s = SupplierManagementConst.PROCEED_RENEW_APPLN_TNC_PAGE;
			// PROCEED_RENEW_MOF_APPLN;
			break;
		case "C":
			s = SupplierManagementConst.PROCEED_CANCEL_MOF_APPLN_TNC_PAGE;
			break;
		case "DPA":
			s = SupplierManagementConst.PROCEED_DELETE_PENDING_APPLN_TNC_PAGE;
			break;
		default:
			log.debug("***** THIS IS DEFAULT PAGE --- i.e. no matching type ---"
					+ applType);
			break;
		}
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, s);
	}

	/**
	 * Sets the exception navigation page for appl.
	 * 
	 * @param appType
	 *            - the application type
	 * @return the redirect page
	 */
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

	/**
	 * Sets the parameter const for application typpe.
	 * 
	 * @param appType
	 *            - the app type
	 * @return the string
	 */
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

	/**
	 * Gets the default sm appl supporting doc and assign a document file
	 * 
	 * @param doc
	 *            -PmDocument object
	 * @param appl
	 *            - SmAppl object
	 * @return SmApplSupportDoc object
	 */
	public static SmApplSupportDoc getDefaultSmApplSupportingDoc(
			PmDocument doc, SmAppl appl) {
		SmApplSupportDoc docu = new SmApplSupportDoc();
		docu.setPmDocument(doc);
		docu.setSmAppl(appl);
		docu.setVerified(true);
		// docu.setVerifiedFlg(1);
		docu.setReceived(false);

		setDefaultSmApplDocFiles(docu);
		return docu;
	}

	/**
	 * Set default supporting doc files
	 * 
	 * @param SmApplSupportDoc
	 *            docu
	 */
	public static void setDefaultSmApplDocFiles(SmApplSupportDoc docu) {
		List<SmApplDocFile> files = new ArrayList<SmApplDocFile>();
		SmApplDocFile docFile = new SmApplDocFile();
		docFile.setSmApplSupportDoc(docu);
		docFile.setFileName(" ");
		docFile.setFilePath(" ");
		files.add(docFile);
		docu.setSmApplDocFiles(files);
	}

	// SM Address Clone code start
	/**
	 * Clone as residential or mailing permenant addr, when selected the check
	 * box.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void cloneShareHolderAddress(
			SmApplicationManagedBean managedBean) {
		managedBean.getShareHolderResdAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getShareHolderPermAddr()
						.getSmAddress(), managedBean.getShareHolderResdAddr()
						.getSmAddress(), managedBean.getShareHolderResdAddr(),
						managedBean.getDefPmCountry()));
		managedBean.getShareHolderMailAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getShareHolderPermAddr()
						.getSmAddress(), managedBean.getShareHolderMailAddr()
						.getSmAddress(), managedBean.getShareHolderMailAddr(),
						managedBean.getDefPmCountry()));
		managedBean.getShareHolderOffAddr().setSmAddress(
				cloneAsPermenantAddr(managedBean.getSmAppl()
						.getSmCompanyBasic().getSmCompanyAddresses().get(0)
						.getSmAddress(), managedBean.getShareHolderOffAddr()
						.getSmAddress(), managedBean.getShareHolderOffAddr(),
						managedBean.getDefPmCountry()));
	}

	/**
	 * Clone as residential or mailing permenant addr, when selected the check
	 * box.
	 * 
	 * @param srcAddr
	 *            - SmAddress object
	 * @param desAddr
	 *            - SmAddress object
	 * @param desPersAddr
	 *            -SmPersonnelAddress object
	 * @param cntry
	 *            - PmCountry object
	 * @return the sm address
	 */
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

	/**
	 * Assign cloned address values.
	 * 
	 * @param desAddr
	 *            -SmAddress object
	 * @param srcAddr
	 *            -SmAddress object
	 * 
	 */
	public static void assignClonedAddressValues(SmAddress desAddr,
			SmAddress srcAddr) {
		if (desAddr == null) {
			desAddr = new SmAddress();
		}
		desAddr.setAddress1(srcAddr.getAddress1());
		desAddr.setAddress2(srcAddr.getAddress2());
		desAddr.setAddress3(srcAddr.getAddress3());
		desAddr.setAddress4(srcAddr.getAddress4());
		desAddr.setPostcode(srcAddr.getPostcode());
		if (srcAddr.getPmCountry() != null
				&& srcAddr.getPmCountry().getCountryId() != null) {
			desAddr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
					srcAddr.getPmCountry().getCountryId()));
			desAddr.setCountryId(srcAddr.getCountryId());
		}
		if (srcAddr.getPmState() != null
				&& srcAddr.getPmState().getStateId() != null) {
			desAddr.setPmState(SmCommonUtil.getInstance().getStateById(
					srcAddr.getPmState().getStateId()));
			desAddr.setStateId(srcAddr.getStateId());
		}
		if (srcAddr.getPmDistrict() != null
				&& srcAddr.getPmDistrict().getDistrictId() != null) {
			desAddr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
					srcAddr.getPmDistrict().getDistrictId()));
			desAddr.setDistrictId(srcAddr.getDistrictId());
		}
		if (srcAddr.getPmCity() != null
				&& srcAddr.getPmCity().getCityId() != null) {
			desAddr.setPmCity(SmCommonUtil.getInstance().getCityById(
					srcAddr.getPmCity().getCityId()));
			desAddr.setCityId(srcAddr.getCityId());
		}
	}

	public void cloneAddress(SmAddress desAddr, SmAddress srcAddr) {
		desAddr.setAddress1(srcAddr.getAddress1());
		desAddr.setAddress2(srcAddr.getAddress2());
		desAddr.setAddress3(srcAddr.getAddress3());
		desAddr.setAddress4(srcAddr.getAddress4());
		desAddr.setPostcode(srcAddr.getPostcode());
		if (srcAddr.getPmCountry() != null
				&& srcAddr.getPmCountry().getCountryId() != null) {
			desAddr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
					srcAddr.getPmCountry().getCountryId()));
		}
		desAddr.setCountryId(srcAddr.getCountryId());
		if (srcAddr.getPmState() != null
				&& srcAddr.getPmState().getStateId() != null) {
			desAddr.setPmState(SmCommonUtil.getInstance().getStateById(
					srcAddr.getPmState().getStateId()));
		}
		desAddr.setStateId(srcAddr.getStateId());
		if (srcAddr.getPmDistrict() != null
				&& srcAddr.getPmDistrict().getDistrictId() != null) {
			desAddr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
					srcAddr.getDistrictId()));
		}
		desAddr.setDistrictId(srcAddr.getDistrictId());
		if (srcAddr.getPmCity() != null
				&& srcAddr.getPmCity().getCityId() != null) {
			desAddr.setPmCity(SmCommonUtil.getInstance().getCityById(
					srcAddr.getCityId()));
		}
		desAddr.setCityId(srcAddr.getCityId());
	}

	/**
	 * Reset to default entity values.
	 * 
	 * @param baseEntity
	 *            the base entity
	 */
	public static void resetToDefaultEntityValues(BaseEntity baseEntity) {
		baseEntity.setBrandNew(true);
		baseEntity.setModified(false);
		baseEntity.setDeleted(false);
		baseEntity.setCreatedBy(null);
		baseEntity.setCreatedDate(null);
		baseEntity.setChangedBy(null);
		baseEntity.setChangedDate(null);
	}

	/**
	 * Reset sm address child lists.During cloning of the addresses
	 * 
	 * @param smAdd
	 *            -SmAddress object
	 */
	public static void resetSmAddressChildLists(SmAddress smAdd) {
		smAdd.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		smAdd.setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		smAdd.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		smAdd.setSmFacilities(new ArrayList<SmFacility>());
		smAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smAdd.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		smAdd.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
	}

	// SM Address Clone code end

	/**
	 * Set default values for new SmAppl object.
	 * 
	 * @param appl
	 *            - SmAppl object
	 * @return SmAppl object
	 */
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

	/**
	 * Calculate share holding percent for the personnel with role equity. Based
	 * on Bumi, Non-Bumi and Non-Malaysian
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void calculateShareHolderPercent(
			SmApplicationManagedBean managedBean) {
		log.info("********* calculateShareHolderPercent ********  ");
		log.info(" *** Non Bumi Equity **** "
				+ managedBean.getSmAppl().getSmCompanyBasic()
						.getNonBumiEquity());
		log.info(" *** Bumi Equity **** "
				+ managedBean.getSmAppl().getSmCompanyBasic().getBumiEquity());
		log.info(" *** Non MY Equity **** "
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
				throw new EpException(MessageConst.ERROR_722560);
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
				log.info("*** List retrieved *** " + selPerList.size());
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
						SmCommonUtil.getMessage("lbl.sm.personnel", null),
						SmCommonUtil.getMessage("lbl.sm.personnel", null),
						perType });
			}
		} catch (NumberFormatException ne) {
			// ne.printStackTrace();
			throw new EpException(MessageConst.ERROR_722522);
		} finally {
			perSO = null;
			selPerList = null;
		}
	}

	/**
	 * Retrieve pm parameter by id.
	 * 
	 * @param paramId
	 *            -Long param id
	 * @return PmParameter object
	 */
	public static PmParameter retrievePmParameterById(Long paramId) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterId(paramId);
		ParameterResDTO resDTO = reqSO.retrieveParameterById(reqDTO);

		return resDTO.getPmParameter();
	}

	/**
	 * Retrieve pm parameter by type and code.
	 * 
	 * @param paramId
	 *            -String param type,String param code
	 * @return PmParameter object
	 */
	public static PmParameter retrievePmParameterByTypeAndCode(String type,
			String code) {
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(type);
		reqDTO.setParameterCode(code);
		ParameterResDTO resDTO = reqSO.retrieveParameterByTypeAndCode(reqDTO);

		return resDTO.getPmParameter();
	}

	/**
	 * Retrieve pm education level by code.
	 * 
	 * @param code
	 *            -String levelCode
	 * @return PmEduLevel object
	 */
	/*
	 * public static PmEduLevel retrievePmEduLevelByCode(String code) {
	 * IEducationLevelSO reqSO = (IEducationLevelSO) BusinessDelegate
	 * .getInstance().getServiceObject( ModuleCodeConst.PROFILE_MANAGEMENT,
	 * IEducationLevelSO.class); EducationLevelReqDTO reqDTO = new
	 * EducationLevelReqDTO(); reqDTO.setLevelCode(code); EducationLevelResDTO
	 * resDTO = reqSO .retrieveEducationLevelByCode(reqDTO);
	 * 
	 * return resDTO.getEduLevel(); }
	 */

	/**
	 * Gets the sm supplier basic info vo for mapping data.
	 * 
	 * @param bean
	 *            - SmApplicationManagedBean object
	 * @return the sm supplier basic info vo object
	 */
	public static SmSupplierBasicInfoVO getSmSupplierBasicInfoVO(
			SmSupplierBasicInfoVO basicVO, SmSupplier supplier, SmAppl smAppl) {
		SmSupplierBasicInfoVO smSupplierBasicInfoVO = basicVO;
		SmGstSupplier hqGst = SmCommonUtil
				.getSmGstSupplierBySupplierIdAndBranchCode(
						supplier.getSupplierId(), null, null, true);
		if (smSupplierBasicInfoVO == null) {
			smSupplierBasicInfoVO = new SmSupplierBasicInfoVO();
		}
		// gst details
		if (smAppl.getSmCompanyBasic() != null) {
			smSupplierBasicInfoVO
					.setGstNum((hqGst == null || StringUtils.isBlank(hqGst
							.getGstRegNo())) ? "--" : hqGst.getGstRegNo());
			smSupplierBasicInfoVO.setGstRegDate((hqGst == null || hqGst
					.getGstEffDate() == null) ? "--" : DateUtils.dateToString(
					"dd/MM/yyyy", hqGst.getGstEffDate()));
			smSupplierBasicInfoVO.setGstEndDate((hqGst == null || hqGst
					.getGstEndDate() == null) ? "--" : DateUtils.dateToString(
					"dd/MM/yyyy", hqGst.getGstEndDate()));
		}
		// mof account
		if (supplier != null && supplier.getSmMofAccounts() != null) {
			SmMofAccount smMofAccount = getActiveMofAccount(
					supplier.getSmMofAccounts(), smAppl.getSupplierType());
			smSupplierBasicInfoVO.setMofNo(smMofAccount.getMofNo());
			smSupplierBasicInfoVO
					.setStartDate(smMofAccount.getEffDate() == null ? ""
							: DateUtils.dateToString("dd/MM/yyyy",
									smMofAccount.getEffDate()));
			smSupplierBasicInfoVO
					.setEndDate(smMofAccount.getExpDate() == null ? ""
							: DateUtils.dateToString("dd/MM/yyyy",
									smMofAccount.getExpDate()));
		}
		smSupplierBasicInfoVO.setEpNo(supplier == null ? "" : supplier
				.getEpNo());
		smSupplierBasicInfoVO.setRegNo(supplier == null ? "" : supplier
				.getRegNo());

		// address & company
		if (smAppl.getSmCompanyBasic() != null
				&& smAppl.getSmCompanyBasic().getSmCompanyAddresses() != null
				&& !smAppl.getSmCompanyBasic().getSmCompanyAddresses()
						.isEmpty()) {
			smSupplierBasicInfoVO
					.setCompanyName(smAppl.getSmCompanyBasic() != null ? smAppl
							.getSmCompanyBasic().getCompanyName() : "");
			smSupplierBasicInfoVO.setAddress(smAppl.getSmCompanyBasic()
					.getSmCompanyAddresses().get(0));
			if (smSupplierBasicInfoVO.getAddress().getSmAddress() != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getAddress1());
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getAddress2())) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getAddress2());
				}
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getAddress3())) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getAddress3());
				}
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getAddress4())) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getAddress4());
				}
				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmDistrict() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmDistrict()
									.getDistrictName());
				}
				if (StringUtils.isNotBlank(smSupplierBasicInfoVO.getAddress()
						.getSmAddress().getPostcode())
						&& smSupplierBasicInfoVO.getAddress().getSmAddress()
								.getPmCity() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPostcode())
							.append("  ")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmCity().getCityName());

				}

				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmState() != null) {
					sb.append(",")
							.append("<br/>")
							.append(smSupplierBasicInfoVO.getAddress()
									.getSmAddress().getPmState().getStateName());
				}
				if (smSupplierBasicInfoVO.getAddress().getSmAddress()
						.getPmCountry() != null) {
					sb.append(",").append(
							smSupplierBasicInfoVO.getAddress().getSmAddress()
									.getPmCountry().getCountryName());
				}
				smSupplierBasicInfoVO.setAddressLine(sb.toString());
			}

		}
		SmPersonnel smPersonnel = getAdminPersonnel(smAppl.getSmPersonnels());
		if (smPersonnel != null) {
			smSupplierBasicInfoVO.setAdminEmail(smPersonnel.getEmail());
			smSupplierBasicInfoVO.setAdminPhoneNo(smPersonnel.getMobileFull());
			smSupplierBasicInfoVO.setAdminName(smPersonnel.getName());
		}

		return smSupplierBasicInfoVO;
	}

	/**
	 * Get branch bank from list by branch ID and bank ID
	 * 
	 * @param Long
	 *            branchId
	 * @param Long
	 *            bankId
	 * @param List
	 *            <SmSupplierBranchBank> list
	 * @return SmSupplierBranchBank
	 */
	public static SmSupplierBranchBank getBranchBankFromListByBranchIdAndBankId(
			Long branchId, Long bankId, List<SmSupplierBranchBank> list) {
		SmSupplierBranchBank retVal = null;
		if (!SmCommonUtil.isCollectionEmpty(list)) {
			for (SmSupplierBranchBank tmp : list) {
				if (tmp.getSmSupplierBank() != null
						&& tmp.getSmSupplierBank().getSupplierBankId()
								.equals(bankId)
						&& tmp.getSmSupplierBranch() != null
						&& tmp.getSmSupplierBranch().getSupplierBranchId()
								.equals(branchId)) {
					retVal = tmp;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Check if branch id exist branch id list
	 * 
	 * @param Long
	 *            branchId
	 * @param Long
	 *            [] branchIds
	 * @return boolean
	 */
	public static boolean isBranchIdExistInList(Long branchId, Long[] branchIds) {
		boolean retVal = false;
		if (branchIds != null && branchIds.length > 0 && branchId != null) {
			for (Long brId : branchIds) {
				if (branchId.equals(brId)) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Gets the active mof account for supplier based on the supplier type.
	 * 
	 * @param smMofAccounts
	 *            - List of sm mof accounts
	 * @param suppType
	 *            - the supp type
	 * @return SmMofAccount object
	 */
	public static SmMofAccount getActiveMofAccount(
			List<SmMofAccount> smMofAccounts, String suppType) {
		SmMofAccount smMofAccount = new SmMofAccount();

		for (SmMofAccount account : smMofAccounts) {
			if (account.getSupplierType() != null
					&& account.getSupplierType().equalsIgnoreCase(suppType)
					&& account.getRecordStatusFlg().compareTo(
							DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
				smMofAccount = account;
				break;
			}
		}
		return smMofAccount;
	}

	/**
	 * Gets the personnel with Admin role.
	 * 
	 * @param smPersonnels
	 *            - List of sm personnels
	 * @return SmPersonnel object
	 */
	public static SmPersonnel getAdminPersonnel(List<SmPersonnel> smPersonnels) {
		SmPersonnel smPersonnelRetVal = null;
		if (smPersonnels != null) {
			for (SmPersonnel smPersonnel : smPersonnels) {
				if (smPersonnel.getEpRole() != null
						&& (smPersonnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value())
								|| smPersonnel
										.getEpRole()
										.equalsIgnoreCase(
												SupplierManagementConst.BASIC_SUPPLIER_ADMIN) || smPersonnel
								.getEpRole()
								.equalsIgnoreCase(
										SupplierManagementConst.MOF_SUPPLIER_ADMIN))) {
					smPersonnelRetVal = smPersonnel;
					break;
				}
			}
		}
		return smPersonnelRetVal;
	}

	/**
	 * Check if the Approver or PO is allowed allowed to approve application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void checkAllowedToApprove(
			SmApplicationManagedBean managedBean) {
		boolean validForApproval = false;
		if (managedBean.getSmAppl().getApplType()
				.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)
				|| managedBean
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| managedBean
						.getSmAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES)) {
			boolean pendingExits = false;
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					pendingExits = true;
					if ((managedBean.isPoUser() && cat.getPoDecFlg() != null && cat
							.getPoDecFlg()
							.compareTo(
									SupplierManagementConst.APPROVED_RECORD_STATUS) == 0)
							|| (managedBean.isApproverUser()
									&& cat.getApprvrDecFlg() != null && cat
									.getApprvrDecFlg()
									.compareTo(
											SupplierManagementConst.APPROVED_RECORD_STATUS) == 0)) {
						validForApproval = true;
						break;
					}
				}
			}
			if (!pendingExits) {
				validForApproval = true;
			}
		} else {
			validForApproval = true;
		}
		log.info("*** Valid For Approval After category checking Decision **** "
				+ validForApproval);
		if (validForApproval) {
			if (!EntityHelper.isCollectionEmpty(managedBean.getMandatoryDocs())) {
				for (SmApplSupportDoc doc : managedBean.getMandatoryDocs()) {
					if (doc.getVerifiedFlg() == null
							|| (doc.getVerifiedFlg() != null && doc
									.getVerifiedFlg().compareTo(0) == 0)) {
						validForApproval = false;
						break;
					} else {
						validForApproval = true;
					}
				}
			}
		}
		log.info("*** Valid For Approval Final Decision **** "
				+ validForApproval);
		managedBean.setAllowedToApprove(validForApproval);
	}

	/**
	 * Validate site visit selections i.e. for facilities and categories
	 * selected.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return true, if successful
	 */
	public static boolean validateSiteVisitSelections(
			SmApplicationManagedBean managedBean) {
		boolean valid = true;

		for (SmSiteVisitFacilityVO facVO : managedBean.getSiteVisitFacVOList()) {
			if (!EntityHelper.isListGotSelectedValue(facVO.getAssocCompanies())
					&& !EntityHelper.isListGotSelectedValue(facVO
							.getSuppBranches())
					&& !EntityHelper.isListGotSelectedValue(facVO
							.getFacilities())) {
				valid = false;
				throw new EpException(MessageConst.ERROR_722627,
						new String[] { facVO.getCatTypeLabel() });
			}
		}

		return valid;

	}

	/**
	 * Construct Site visit data based on facilities and categories selected for
	 * site visit.
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void setSiteVisitData(SmApplicationManagedBean bean) {
		if (!SmCommonUtil.isCollectionEmpty(bean.getSiteVisitFacVOList())) {
			bean.setSvTaskList(new ArrayList<SmSvTask>());
			bean.getSmAppl().setSmSvTasks(new ArrayList<SmSvTask>());
			SmSvTask svTask = null;
			PmParameter bumiType = SmCommonUtil.retrieveParameterByTypeAndCode(
					ParameterTypeConst.CATEGORY_TYPE,
					SupplierManagementConst.CAT_TYPE_BUMIPUTERA,
					new FacesScopes().getUserLocale().getLanguage());
			for (SmSiteVisitFacilityVO facVO : bean.getSiteVisitFacVOList()) {

				// add assocCompany address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getAssocCompanies())) {
					for (SmAssociateCompany assoc : facVO.getAssocCompanies()) {
						if (assoc.isSelected()) {

							// set smSvTask related table
							setSmSvTask(bean, bumiType, facVO, svTask,
									assoc.getSmAddress());
						}
					}
				}

				// add supplierBranch address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getSuppBranches())) {
					for (SmSupplierBranch br : facVO.getSuppBranches()) {
						if (br.isSelected()) {

							// set smSvTask related table
							setSmSvTask(bean, bumiType, facVO, svTask,
									br.getSmAddress());
						}
					}
				}

				// add smFacility address to site visit
				if (!SmCommonUtil.isCollectionEmpty(facVO.getFacilities())) {
					for (SmFacility fac : facVO.getFacilities()) {
						if (fac.isSelected()) {

							// set smSvTask related table
							setSmSvTask(bean, bumiType, facVO, svTask,
									fac.getSmAddress());
						}
					}
				}
			}
		}
	}

	private static void setSmSvTask(SmApplicationManagedBean bean,
			PmParameter bumiType, SmSiteVisitFacilityVO facVO, SmSvTask svTask,
			SmAddress addr) {
		// SmSvTask
		svTask = new SmSvTask();
		svTask.setSmAppl(bean.getSmAppl());

		// SmSvOffice
		setSmSvOffice(bean, svTask);

		// SmSvTemplate
		setSmSvTemplate(bumiType, facVO.getL3(), svTask);

		// SmSvAddress
		setSmSvAddress(addr, svTask);

		// SmSvCategories
		setSmSvCategories(svTask, facVO.getSuppCategories());

		bean.getSmAppl().getSmSvTasks().add(svTask);
		bean.getSvTaskList().add(svTask);
	}

	private static void setSmSvTemplate(PmParameter bumiType, PmCategoryL3 l3,
			SmSvTask svTask) {
		SmSvTemplate template = new SmSvTemplate();
		template.setSmSvTask(svTask);
		if (l3 != null && l3.getCategoryTypeCode() != null) {
			template.setPmParameter(l3.getCategoryTypeCode());
		} else {
			template.setPmParameter(bumiType);
		}
		svTask.setSmSvTemplate(template);
	}

	private static void setSmSvAddress(SmAddress addr, SmSvTask svTask) {
		SmSvAddress smSvAddress = new SmSvAddress();
		smSvAddress.setSmSvTask(svTask);
		smSvAddress.setSmAddress(addr);
		svTask.setSmSvAddress(smSvAddress);
	}

	private static void setSmSvOffice(SmApplicationManagedBean bean,
			SmSvTask svTask) {
		SmSvOffice svOff = new SmSvOffice();
		svOff.setSmSvTask(svTask);
		svOff.setSmFacility(bean.getBasicCompanyFacility());
		svTask.setSmSvOffice(svOff);
	}

	private static void setSmSvCategories(SmSvTask svTask,
			List<SmSupplierCategory> catList) {
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			svTask.setSmSvCategories(new ArrayList<SmSvCategory>());
			for (SmSupplierCategory cat : catList) {
				SmSvCategory svCat = new SmSvCategory();
				svCat.setSmSvTask(svTask);
				svCat.setPmCategoryL1(cat.getPmCategoryL1());
				svCat.setPmCategoryL2(cat.getPmCategoryL2());
				svCat.setPmCategoryL3(cat.getPmCategoryL3());
				svTask.getSmSvCategories().add(svCat);
			}
		}
	}

	/**
	 * Update site visit task.
	 * 
	 * @param svTask
	 *            - SmSvTask object
	 * @return true, if successful
	 */
	@SuppressWarnings("unused")
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

	/**
	 * Show supplier profile not ready validation message.
	 * 
	 * @param fc
	 *            -FacesContext object
	 */
	public static void showSuppProfileNotReadyMsg(FacesContext fc) {
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721212,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
	}

	/**
	 * Gets the active SmMofAccount record for supplier based on the supplier
	 * type.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the active mof num for supplier
	 */
	public static String getActiveMofNumForSupplier(
			SmApplicationManagedBean managedBean) {
		String mofNum = "";
		List<SmMofAccount> accounts = managedBean.getSmSupplier()
				.getSmMofAccounts();
		if (accounts != null && !accounts.isEmpty()) {
			for (SmMofAccount account : accounts) {
				if (account.getSupplierType().equalsIgnoreCase(
						managedBean.getSmAppl().getSupplierType())
						&& account.getRecordStatusFlg().compareTo(
								DbValueConst.RECORD_STATUS_ACTIVE) == 0) {
					mofNum = account.getMofNo();
				}
			}
		}
		return mofNum;
	}

	/**
	 * Handle show or hide facility panels based on categories in the
	 * application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void handleShowOrHideFacilityPanels(
			SmApplicationManagedBean managedBean) {
		managedBean.setAddrSameAsItems(new ArrayList<SelectItem>());
		SelectItem item = new SelectItem();
		item.setValue(StringUtils.EMPTY);
		item.setLabel(SmCommonUtil.getMessage("lbl.txt.select", null));
		managedBean.getAddrSameAsItems().add(item);
		item = new SelectItem();
		item.setValue("HQ");
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.facility.type._B", null));
		managedBean.getAddrSameAsItems().add(item);
		if (!EntityHelper.isCollectionEmpty(managedBean.getSmAppl()
				.getSmSupplierBranches())) {
			for (SmSupplierBranch br : managedBean.getSmAppl()
					.getSmSupplierBranches()) {
				item = new SelectItem();
				item.setValue(br.getSupplierBranchId());
				item.setLabel(br.getBranchName());
				managedBean.getAddrSameAsItems().add(item);
			}
		}

		managedBean.setShowDockyardPanel(false);
		managedBean.setShowStorePanel(false);
		managedBean.setShowFactoryPanel(false);
		managedBean.setShowWorkShopPanel(false);

		// if site after approval, show panel if facility type has records
		if (managedBean.isSiteVisitAfterApproval()) {
			for (SmFacility facility : managedBean.getSmAppl()
					.getSmFacilities()) {
				if (!StringUtils.isBlank(facility.getFacilityType())) {
					if (facility.getFacilityType().equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
						managedBean.setShowFactoryPanel(true);
					} else if (facility.getFacilityType().equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_STORE)) {
						managedBean.setShowStorePanel(true);
					} else if (facility.getFacilityType().equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP)) {
						managedBean.setShowWorkShopPanel(true);
					} else if (facility.getFacilityType().equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD)) {
						managedBean.setShowDockyardPanel(true);
					}
				}
			}
		}
		// if not site visit after approval, check based on pending activation
		// cat code
		else {
			for (SmSupplierCategory cat : managedBean.getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory()
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode()
								.getParameterId() != null
						&& cat.getRecordStatusFlg() != null
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0
						&& !isCategoryRejected(cat)) {
					String facType = "";
					if (cat.getPmCategoryL3().getFacilityType() != null) {
						facType = cat.getPmCategoryL3().getFacilityType();
					}
					if (facType
							.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_FACTORY)) {
						managedBean.setShowFactoryPanel(true);
					} else if (facType
							.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_STORE)) {
						managedBean.setShowStorePanel(true);
					} else if (facType
							.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP)) {
						managedBean.setShowWorkShopPanel(true);
					} else if (facType
							.equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD)) {
						managedBean.setShowDockyardPanel(true);
					}
				}
			}
		}
	}

	/**
	 * Show or hide travelling software section panel
	 * 
	 * @param SmApplicationManagedBean
	 *            managedBean
	 */
	public static void showOrHideTravelSoftwarePanel(
			SmApplicationManagedBean managedBean) {
		managedBean.setShowTravelSoftwarePanel(false);
		for (SmSupplierCategory cat : managedBean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.isSpecialCategory()
					&& cat.getPmCategoryL3() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterId() != null
					&& cat.getRecordStatusFlg() != null
					&& cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0
					&& !isCategoryRejected(cat)) {
				String catType = "";
				if (cat.getPmCategoryL3().getCategoryTypeCode() != null) {
					catType = cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterCode();
				}
				if (StringUtils.isNotBlank(catType)
						&& catType
								.equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)) {
					managedBean.setShowTravelSoftwarePanel(true);
				}
			}
		}
	}

	/**
	 * Remove application information that already not valid after changes been
	 * made before save
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public void removeNotApplicableInformationBeforeSave(
			SmApplicationManagedBean bean) {
		// only check for application not in site visit after approved
		if (!bean.isSiteVisitAfterApproval()) {
			removePendingApprovedCategoryIfNotValid(bean.getSmAppl(), bean
					.getBasicCompanyFacility().getFacilityType());
			removeFacilitiesNotInTheSameCategoryFacType(bean.getSmAppl());
			removeTravellingSoftwareIfNotApplicable(bean);
			removeEquipmentIfNotApplicable(bean);
		}
	}

	/**
	 * Remove travelling software if related category code is not picked
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public void removeTravellingSoftwareIfNotApplicable(
			SmApplicationManagedBean bean) {
		boolean isTravelSoftwareApplicable = false;
		for (SmSupplierCategory cat : bean.getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.isSpecialCategory()
					&& cat.getPmCategoryL3() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode() != null
					&& cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterId() != null
					&& (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
							.getRecordStatusFlg()
							.compareTo(
									SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
					&& !isCategoryRejected(cat)) {
				String catType = "";
				if (cat.getPmCategoryL3().getCategoryTypeCode() != null) {
					catType = cat.getPmCategoryL3().getCategoryTypeCode()
							.getParameterCode();
				}
				if (StringUtils.isNotBlank(catType)
						&& catType
								.equalsIgnoreCase(ParameterConst.CATEGORY_TYPE_TRAVEL)) {
					isTravelSoftwareApplicable = true;
				}
			}
		}

		if (!isTravelSoftwareApplicable
				&& !SmCommonUtil.isCollectionEmpty(bean.getSmAppl()
						.getSmSupplierSystems())) {
			bean.getSmAppl().setSmSupplierSystems(
					new ArrayList<SmSupplierSystem>());
		}
		bean.setShowTravelSoftwarePanel(isTravelSoftwareApplicable);
	}

	/**
	 * Remove equipment information from application if category type is normal
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public void removeEquipmentIfNotApplicable(SmApplicationManagedBean bean) {
		SmAppl appl = bean.getSmAppl();
		bean.setShowMachineryEquipmentPanel(true);

		// if category select items in machinery is empty, dont show machinery
		// panel
		if (EntityHelper.isCollectionEmpty(getSelectedCategoryTypeItems(appl))) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
			bean.setShowMachineryEquipmentPanel(false);
		} else {
			List<String> pendingApproveCatTypeList = new ArrayList<String>();
			if (appl.getApplCategoryType() != null
					&& appl.getApplCategoryType().equals("N")) {
				appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
				appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
			}
			if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
				for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
					if ((cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
							.getRecordStatusFlg()
							.compareTo(
									SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
							&& !isCategoryRejected(cat)) {
						pendingApproveCatTypeList.add(cat.getPmCategoryL3()
								.getCategoryTypeCode().getParameterCode());
					}
				}
				if (!EntityHelper.isCollectionEmpty(appl.getSmMachineDtls())) {
					for (Iterator<SmMachineDtl> macIt = appl.getSmMachineDtls()
							.iterator(); macIt.hasNext();) {
						SmMachineDtl mac = macIt.next();
						if (mac.getCategoryType() != null
								&& mac.getCategoryType().getParameterId() != null
								&& (EntityHelper
										.isCollectionEmpty(pendingApproveCatTypeList) || !pendingApproveCatTypeList
										.contains(mac.getCategoryType()
												.getParameterCode()))) {
							macIt.remove();
						}
					}
				}
			}
		}
	}

	/**
	 * Remove facility that dont have selected supplier category facility type
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public void removeFacilitiesNotInTheSameCategoryFacType(SmAppl appl) {
		List<String> catTypeList = new ArrayList<String>();
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (StringUtils.isNotBlank(cat.getPmCategoryL3()
						.getFacilityType())) {
					if ((cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0 || cat
							.getRecordStatusFlg()
							.compareTo(
									SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0)
							&& !isCategoryRejected(cat)) {
						catTypeList
								.add(cat.getPmCategoryL3().getFacilityType());
					}
				}
			}
		}

		if (!SmCommonUtil.isCollectionEmpty(appl.getSmFacilities())) {
			for (Iterator<SmFacility> facIt = appl.getSmFacilities().iterator(); facIt
					.hasNext();) {
				SmFacility fac = facIt.next();
				if (StringUtils.isNotBlank(fac.getFacilityType())
						&& !fac.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
						&& !fac.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
					if (SmCommonUtil.isCollectionEmpty(catTypeList)
							|| !catTypeList.contains(fac.getFacilityType())) {
						facIt.remove();
					}
				}
			}
		}

	}

	/**
	 * Remove pending approved category if not valid based on selected bumi
	 * percentage,paid up capital,business type
	 * 
	 * @param SmAppl
	 *            appl
	 * @param String
	 *            facilityType
	 */
	public void removePendingApprovedCategoryIfNotValid(SmAppl appl,
			String facilityType) {
		List<SmSupplierCategory> catList = appl.getSmSupplierCategories();
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			for (Iterator<SmSupplierCategory> catIt = catList.iterator(); catIt
					.hasNext();) {
				SmSupplierCategory cat = catIt.next();
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.PENDING_ACT_STATUS) == 0
						|| cat.getRecordStatusFlg()
								.compareTo(
										SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
					if (!isCatL3Valid(appl, facilityType, cat)) {
						catIt.remove();
					}
				}
			}
		}
	}

	private static boolean isCategoryRejected(SmSupplierCategory cat) {
		if ((cat.getPoDecFlg() != null && cat.getPoDecFlg() == 0)
				|| (cat.getApprvrDecFlg() != null && cat.getApprvrDecFlg() == 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set SME status for supplier application
	 * 
	 * @param SmAppl
	 *            appl
	 */
	public static void setSmeStatus(SmAppl appl) {
		BigDecimal total = BigDecimal.ZERO;
		String smeStatus = SupplierManagementConst.SME_STATUS_NONE_SME;
		List<SmSupplierCategory> catList = appl.getSmSupplierCategories();

		// categoryCodeL3 = 99
		boolean isCatCode99 = false;
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (StringUtils.isNotBlank(cat.getPmCategoryL3()
						.getCategoryL3Code())
						&& cat.getPmCategoryL3().getCategoryL3Code()
								.equals("99")) {
					if (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_ACT_STATUS) == 0
							|| cat.getRecordStatusFlg()
									.compareTo(
											SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
						isCatCode99 = true;
						break;
					}
				}
			}
		}

		if (isCatCode99) {

			PmParameter annualRev = retrievePmParameterById(appl
					.getSmCompanyBasic().getAnnualRevenue());

			// SME Manufacturing
			// revenue <=50M
			if (annualRev != null && annualRev.getParameterCode() != null
					&& !annualRev.getParameterCode().equals("100000000")
					&& !annualRev.getParameterCode().equals("100000001")) {
				smeStatus = SupplierManagementConst.SME_STATUS_MANUFACTURING;
			}
			// SME Manufacturing
			// staff total <=200
			if (!SmCommonUtil.isCollectionEmpty(appl.getSmStaffTotals())) {
				for (SmStaffTotal staffTotal : appl.getSmStaffTotals()) {
					total = total.add(staffTotal.getTotalBumi());
					total = total.add(staffTotal.getTotalForeigner());
					total = total.add(staffTotal.getTotalNonBumi());
				}
				if (total.compareTo(new BigDecimal(200)) != 1) {
					smeStatus = SupplierManagementConst.SME_STATUS_MANUFACTURING;
				}
			}

			// SME Service (on hold because not in the parameter list)
			// revenue<=20M
		}

		else {
			// SME Service
			// staff total <=75
			if (!SmCommonUtil.isCollectionEmpty(appl.getSmStaffTotals())) {
				for (SmStaffTotal staffTotal : appl.getSmStaffTotals()) {
					total = total.add(staffTotal.getTotalBumi());
					total = total.add(staffTotal.getTotalForeigner());
					total = total.add(staffTotal.getTotalNonBumi());
				}
				if (total.compareTo(new BigDecimal(75)) != 1) {
					smeStatus = smeStatus
							.equals(SupplierManagementConst.SME_STATUS_MANUFACTURING) ? SupplierManagementConst.SME_STATUS_BOTH
							: SupplierManagementConst.SME_STATUS_SERVICES;
				}
			}
		}

		appl.setSmeStatus(smeStatus);
	}

	/**
	 * Set facilities to be display for site visit assignment
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void setAssignSiteVisitVO(SmApplicationManagedBean bean) {
		FacesScopes fs = new FacesScopes();
		SmSiteVisitFacilityVO facVO = null;
		bean.setSiteVisitFacVOList(new ArrayList<SmSiteVisitFacilityVO>());
		List<SmSupplierCategory> catList = null;
		List<SmFacility> facList = null;
		PmCategoryL3 l3 = null;
		SmFacility hq = getHqFacilityByAppl(bean.getSmAppl());
		for (String catType : SupplierManagementConst.CAT_TYPE_CODES) {
			catList = getCategoryByCatType(catType, bean.getSplCategoryList());
			if (!SmCommonUtil.isCollectionEmpty(catList)) {
				l3 = getCatL3ByCatTypeFromList(catType, catList);
				if (l3 != null) {
					facVO = new SmSiteVisitFacilityVO();
					facVO.setL3(l3);
					for (PmParameterDesc codeDesc : l3.getCategoryTypeCode()
							.getPmParameterDescs()) {
						if (StringUtils.isNotBlank(codeDesc.getLanguageCode())
								&& codeDesc.getLanguageCode().equals(
										fs.getUserLocale().getLanguage())) {
							facVO.setCatTypeLabel(codeDesc.getCodeName());
							break;
						}
					}

					// add facility include HQ
					facList = getFacilityByFacTypeFromList(
							l3.getFacilityType(), bean.getSmAppl()
									.getSmFacilities());
					if (EntityHelper.getEntityByMultiplePropertiesFromList(hq,
							facList, new String[] { "facilityId" }, null, null) == null) {
						facList.add((SmFacility) SerializationUtils.clone(hq));
					}
					facVO.setFacilities(facList);

					// add associate company
					facVO.setAssocCompanies(new ArrayList<SmAssociateCompany>());
					if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
							.getSmAssociateCompanies())) {
						for (SmAssociateCompany assoc : bean.getSmAppl()
								.getSmAssociateCompanies()) {
							facVO.getAssocCompanies().add(
									(SmAssociateCompany) SerializationUtils
											.clone(assoc));
						}
					}

					// add branch
					facVO.setSuppBranches(new ArrayList<SmSupplierBranch>());
					if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
							.getSmSupplierBranches())) {
						for (SmSupplierBranch br : bean.getSmAppl()
								.getSmSupplierBranches()) {
							facVO.getSuppBranches().add(
									(SmSupplierBranch) SerializationUtils
											.clone(br));
						}
					}

					facVO.setSuppCategories(catList);
					bean.getSiteVisitFacVOList().add(facVO);
				}
			}
		}

		if (SmCommonUtil.isCollectionEmpty(bean.getSiteVisitFacVOList())) {
			facVO = new SmSiteVisitFacilityVO();
			facVO.setFacilities(new ArrayList<SmFacility>());
			facVO.setCatTypeLabel(SupplierManagementConst.FACILITY_TYPE_MAP
					.get(hq.getFacilityType()));
			facVO.getFacilities().add(hq);
			facVO.setAssocCompanies(bean.getSmAppl().getSmAssociateCompanies());
			facVO.setSuppBranches(bean.getSmAppl().getSmSupplierBranches());
			bean.getSiteVisitFacVOList().add(facVO);
		}
	}

	/**
	 * Get supplier category by facility type
	 * 
	 * @param String
	 *            facType
	 * @param List
	 *            <SmSupplierCategory> catList
	 * @return List<SmSupplierCategory>
	 */
	public static List<SmSupplierCategory> getCategoryByFacType(String facType,
			List<SmSupplierCategory> catList) {
		List<SmSupplierCategory> retVal = new ArrayList<SmSupplierCategory>();
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			for (SmSupplierCategory cat : catList) {
				if (cat.getPmCategoryL3() != null
						&& StringUtils.isNotBlank(cat.getPmCategoryL3()
								.getFacilityType())
						&& facType.equals(cat.getPmCategoryL3()
								.getFacilityType())) {
					retVal.add(cat);
				}
			}
		}
		return retVal;
	}

	/**
	 * Get supplier category by level3 category type
	 * 
	 * @param String
	 *            facType
	 * @param List
	 *            <SmSupplierCategory> catList
	 * @return List<SmSupplierCategory>
	 */
	public static List<SmSupplierCategory> getCategoryByCatType(String catType,
			List<SmSupplierCategory> catList) {
		List<SmSupplierCategory> retVal = new ArrayList<SmSupplierCategory>();
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			for (SmSupplierCategory cat : catList) {
				if (cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& StringUtils.isNotBlank(cat.getPmCategoryL3()
								.getCategoryTypeCode().getParameterCode())
						&& catType.equals(cat.getPmCategoryL3()
								.getCategoryTypeCode().getParameterCode())) {
					retVal.add(cat);
				}
			}
		}
		return retVal;
	}

	/**
	 * Get facility by facility type
	 * 
	 * @param String
	 *            facType
	 * @param List
	 *            <SmFacility> facList
	 * @return List<SmFacility>
	 */
	public static List<SmFacility> getFacilityByFacTypeFromList(String facType,
			List<SmFacility> facList) {
		List<SmFacility> retVal = new ArrayList<SmFacility>();
		if (!SmCommonUtil.isCollectionEmpty(facList)) {
			for (SmFacility fac : facList) {
				if (fac.getFacilityType() != null
						&& fac.getFacilityType().equals(facType)) {
					retVal.add(fac);
				}
			}
		}
		return retVal;
	}

	private static PmCategoryL3 getCatL3ByCatTypeFromList(String catType,
			List<SmSupplierCategory> catList) {
		PmCategoryL3 l3 = null;
		if (!SmCommonUtil.isCollectionEmpty(catList)) {
			for (SmSupplierCategory cat : catList) {
				if (cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode()
								.getParameterCode().equals(catType)) {
					l3 = cat.getPmCategoryL3();
					break;
				}
			}
		}
		return l3;
	}

	/**
	 * Checks if personnel is created in basic.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object
	 * @param supplierId
	 *            - Long supplier id
	 * @return true, if personnel is created in basic
	 */
	public static boolean isPersonnelCreatedInBasic(SmPersonnel smPersonnel,
			Long supplierId) {
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmPersonnel(smPersonnel);
		reqDTO.setSmSupplierId(supplierId);
		return personnelSO.isPersonnelCreatedDuringBasicAccount(reqDTO);
	}

	/**
	 * Assign the values and save new sm remarks.
	 * 
	 * @param docId
	 *            - Long doc id
	 * @param docType
	 *            - the doc type
	 * @param remarks
	 *            - the remarks
	 */
	public static void manageAndSaveNewSmRemarks(Long docId, String docType,
			String remarks) {
		SmRemark remark = new SmRemark();
		remark.setDocId(docId);
		remark.setDocType(docType);
		remark.setRemark(remarks);
		saveSmRemarks(remark);

	}

	/**
	 * Save sm remarks.
	 * 
	 * @param remarks
	 *            - SmRemark object
	 */
	public static void saveSmRemarks(SmRemark remarks) {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setSmRemark(remarks);
		ISmRemarksSO reqSO = (ISmRemarksSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISmRemarksSO.class);
		reqSO.saveSmRemarks(reqDTO);

	}

	/**
	 * Checks if selected is personnel signatory for any of the listed banks.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object
	 * @param smSupplierBanks
	 *            - List of sm supplier banks
	 * @return true, if personnel is a signatory
	 */
	public static boolean isPersonnelBankSignatory(SmPersonnel smPersonnel,
			List<SmSupplierBank> smSupplierBanks) {
		boolean retVal = false;
		if (smSupplierBanks != null && !smSupplierBanks.isEmpty()
				&& smPersonnel != null && smPersonnel.getPersonnelId() != null) {
			for (SmSupplierBank smSupplierBank : smSupplierBanks) {
				if (smSupplierBank.getSmChequeSignatories() != null
						&& !smSupplierBank.getSmChequeSignatories().isEmpty()) {
					for (SmChequeSignatory smChequeSignatory : smSupplierBank
							.getSmChequeSignatories()) {
						if (smChequeSignatory.getSmPersonnel() != null
								&& smChequeSignatory.getSmPersonnel()
										.getPersonnelId() != null
								&& smChequeSignatory
										.getSmPersonnel()
										.getPersonnelId()
										.compareTo(smPersonnel.getPersonnelId()) == 0) {
							retVal = true;
						}
					}
				}
			}
		}
		return retVal;

	}

	/**
	 * Check if supplier branches exist as bank associated branch
	 * 
	 * @param SmSupplierBranch
	 *            branch
	 * @param List
	 *            <SmSupplierBank> smSupplierBanks
	 * @return boolean
	 */
	public static boolean isBranchBankAssoc(SmSupplierBranch branch,
			List<SmSupplierBank> smSupplierBanks) {
		boolean retVal = false;
		if (!SmCommonUtil.isCollectionEmpty(smSupplierBanks) && branch != null
				&& branch.getSupplierBranchId() != null) {
			for (SmSupplierBank smSupplierBank : smSupplierBanks) {
				if (!EntityHelper.isCollectionEmpty(smSupplierBank
						.getSmSupplierBranchBanks())) {
					SmSupplierBranchBank brTmp = (SmSupplierBranchBank) EntityHelper
							.getEntityBySinglePropertyFromList(
									branch.getSupplierBranchId(),
									smSupplierBank.getSmSupplierBranchBanks(),
									"smSupplierBranch");
					if (brTmp != null && brTmp.getSmSupplierBranch() != null) {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Get related bank name by branch
	 * 
	 * @param List
	 *            <SmSupplierBank> banks
	 * @param SmSupplierBranch
	 *            branch
	 * @return String
	 */
	public static String getRelatedBankNameByBranch(List<SmSupplierBank> banks,
			SmSupplierBranch branch) {
		String bank = StringUtils.EMPTY;
		List<String> bankNames = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		if (!SmCommonUtil.isCollectionEmpty(banks) && branch != null
				&& branch.getSupplierBranchId() != null) {
			for (SmSupplierBank smSupplierBank : banks) {
				if (!EntityHelper.isCollectionEmpty(smSupplierBank
						.getSmSupplierBranchBanks())) {
					SmSupplierBranchBank brTmp = (SmSupplierBranchBank) EntityHelper
							.getEntityBySinglePropertyFromList(
									branch.getSupplierBranchId(),
									smSupplierBank.getSmSupplierBranchBanks(),
									"smSupplierBranch");
					if (brTmp != null && brTmp.getSmSupplierBranch() != null
							&& smSupplierBank.getPmFinancialOrg() != null) {
						if (!bankNames.contains(smSupplierBank
								.getPmFinancialOrg().getFinOrgName())) {
							bankNames.add(smSupplierBank.getPmFinancialOrg()
									.getFinOrgName());
							sb.append(
									smSupplierBank.getPmFinancialOrg()
											.getFinOrgName()).append(", ");
						}
					}
				}
			}
			bank = "(" + StringUtils.removeEnd(sb.toString(), ",") + ")";
		}
		return bank;
	}

	/**
	 * Checks if selected personnel is point of contact for any of the listed
	 * branches.
	 * 
	 * @param smPersonnel
	 *            - SmPersonnel object
	 * @param smSupplierBranches
	 *            - List of sm supplier branches
	 * @return true, if is personnel is point of contact
	 */
	public static boolean isPersonnelBranchPointOfContact(
			SmPersonnel smPersonnel, List<SmSupplierBranch> smSupplierBranches) {
		boolean retVal = false;
		if (smSupplierBranches != null && !smSupplierBranches.isEmpty()
				&& smPersonnel != null && smPersonnel.getPersonnelId() != null) {
			for (SmSupplierBranch smSupplierBranch : smSupplierBranches) {
				if (smSupplierBranch.getSmPersonnel() != null
						&& smSupplierBranch.getSmPersonnel().getPersonnelId() != null
						&& smSupplierBranch.getSmPersonnel().getPersonnelId()
								.compareTo(smPersonnel.getPersonnelId()) == 0) {
					retVal = true;
				}
			}
		}
		return retVal;
	}

	/**
	 * Gets the list of previously approved personnel director owner.
	 * 
	 * @param smAppl
	 *            - SmAppl object
	 * @return the List<SmPersonnel>
	 */
	public static List<SmPersonnel> getPrevApprovedPersonnelDirectorOwner(
			SmAppl smAppl) {
		List<SmPersonnel> prevApprovedPersonnelsDirectorOwner = new ArrayList<SmPersonnel>();
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			prevApprovedPersonnelsDirectorOwner = SmCommonUtil
					.getBasicPersonnelsBySupplierId(smAppl.getSmSupplier()
							.getSupplierId());
		} else if (!smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {

			String prevApplNo = smAppl.getApprovedSerFile() == null ? ""
					: smAppl.getApprovedSerFile().substring(
							smAppl.getApprovedSerFile().lastIndexOf('/') + 1,
							smAppl.getApprovedSerFile().indexOf("_APP"));
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setApplNo(prevApplNo);
			List<SmPersonnel> prevApprovedPersonnels = SmServiceLookup
					.getPersonnelSO().retrievePersonnelsByApplNo(reqDTO)
					.getSmPersonnelList();
			if (prevApprovedPersonnels != null
					&& !prevApprovedPersonnels.isEmpty()) {
				for (SmPersonnel prevPersonnel : prevApprovedPersonnels) {
					prevApprovedPersonnelsDirectorOwner.add(prevPersonnel);
				}
			}

		}
		return prevApprovedPersonnelsDirectorOwner;
	}

	/**
	 * Sets the personnels previously approved edit restricted.
	 * 
	 * @param smAppl
	 *            - SmAppl object
	 * @param prevApprovedPersonnels
	 *            - List of SmPersonnel
	 * @param disallowedOwnerEdit
	 *            - boolean
	 */
	public static void setPrevApprovedPersonnelNoneEditable(SmAppl smAppl,
			List<SmPersonnel> prevApprovedPersonnels,
			boolean disallowedOwnerEdit) {
		if (!smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_CANCEL)
				&& !smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_MOF)
				&& !smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				&& disallowedOwnerEdit) {
			for (SmPersonnel currPersonnel : smAppl.getSmPersonnels()) {
				setPrevApprovedPersonnelNoneEditable(prevApprovedPersonnels,
						currPersonnel);
			}
		}
	}

	/**
	 * Set personnel whether already need to renew softcert
	 * 
	 * @param SmSupplier
	 *            supplier
	 * @param List
	 *            <SmPersonnel> personnelList
	 */
	public static void setPersonnelSoftcertRenewalStatus(SmSupplier supplier,
			List<SmPersonnel> personnelList) {
		if (personnelList != null && !personnelList.isEmpty()) {
			for (SmPersonnel personnel : personnelList) {
				if (supplier != null) {
					if (SmCommonUtil.isSoftcertReadyForRenewal(
							supplier.getSupplierId(), personnel)) {
						personnel
								.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
					}
				}
			}
		}
	}

	/**
	 * Assign new tracking dairy values.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param actionDesc
	 *            the action desc
	 * @param status
	 *            - PmStatus object
	 * @return the SmTrackingDairyVO object
	 */
	public static SmTrackingDairyVO assignTrackingDairyValues(
			SmApplicationManagedBean managedBean, String actionDesc,
			PmStatus status) {
		SmTrackingDairyVO trckVO = new SmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : 1L);
		trckVO.setActionedByRole(getUserRoleValue(managedBean));
		trckVO.setDocId(managedBean.getSmAppl().getApplId());
		trckVO.setDocNo(managedBean.getSmAppl().getApplNo());
		trckVO.setDocType("SR");
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(null);
		return trckVO;

	}

	/**
	 * Sets the prev director equity owner.
	 * 
	 * @param personnels
	 *            - List of personnels
	 * @param personnelParam
	 *            - SmPersonnel object
	 */
	private static void setPrevApprovedPersonnelNoneEditable(
			List<SmPersonnel> personnels, SmPersonnel personnelParam) {
		personnelParam.setPrevApprovedDirector(false);
		personnelParam.setPrevApprovedEquityOwner(false);
		for (SmPersonnel personnelTmp : personnels) {
			if (personnelParam.getIdentificationNo().equals(
					personnelTmp.getIdentificationNo())) {
				personnelParam.setPrevApprovedDirector(true);
				personnelParam.setPrevApprovedEquityOwner(true);
			}
		}
	}

	/**
	 * Gets the user role value.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the user role value as String
	 */
	public static String getUserRoleValue(SmApplicationManagedBean managedBean) {
		if (managedBean.isPoUser()) {
			return RoleCode.UPS_PO.value();
		} else if (managedBean.isApproverUser()) {
			return RoleCode.UPS_APPROVER.value();
		} else {
			FacesScopes sc = new FacesScopes();
			if (sc.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				return RoleCode.BASIC_SUPPLIER_ADMIN.value();
			} else if (sc.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
				return RoleCode.MOF_SUPPLIER_ADMIN.value();
			} else if (sc.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
				return RoleCode.BASIC_SUPPLIER_USER.value();
			} else if (sc.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				return RoleCode.MOF_SUPPLIER_USER.value();
			} else if (sc.hasRoleUser(RoleCode.FL_USER.value())) {
				return RoleCode.FL_USER.value();
			} else {
				if (managedBean.getSmAppl().getApplType()
						.equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
					return RoleCode.BASIC_SUPPLIER_ADMIN.value();
				} else {
					return RoleCode.MOF_SUPPLIER_ADMIN.value();
				}

			}

		}
	}

	/**
	 * Reset deactivated personnels status in the application.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return true, if successful
	 */
	public static boolean resetDeactivatedPersonnel(
			SmApplicationManagedBean managedBean) {
		boolean updateNecessary = false;
		if (managedBean.getSmAppl().getSmPersonnels() != null
				&& managedBean.getSmAppl().getSmPersonnels().size() > 0) {
			for (SmPersonnel personnel : managedBean.getSmAppl()
					.getSmPersonnels()) {
				if (personnel.getRecordStatusFlg().equals(
						SupplierManagementConst.DELETED_RECORD_STATUS)) {
					personnel
							.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
					personnel.setEpRole(null);
					personnel.setAuthorizedFlg(0);
					updateNecessary = true;
				}
			}
		}
		return updateNecessary;
	}

	/**
	 * Sets the default base office address.
	 * 
	 * @param addr
	 *            - SmAddress object
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @return the SmAddress object
	 */
	public static SmAddress setDefaultBaseOfficeAddress(SmAddress addr,
			SmApplicationManagedBean managedBean) {
		if (managedBean.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
				&& !managedBean.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().isEmpty()) {
			SmAddress baseAddr = managedBean.getSmAppl().getSmCompanyBasic()
					.getSmCompanyAddresses().get(0).getSmAddress();
			if (baseAddr != null) {
				addr = setDefaultBaseOfficeAddr(baseAddr, addr);
			} else {
				addr = setDefaultAddr(managedBean.getDefPmCountry());
			}
		} else {
			addr = SmApplicationUtil.setDefaultAddr(managedBean
					.getDefPmCountry());
		}
		return addr;
	}

	/**
	 * Sets the application category selected i.e. special or normal.Used to set
	 * the max sections count for review.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void setApplicationCategorySelected(
			SmApplicationManagedBean managedBean) {
		managedBean.getSmAppl().setCurrentActiveTab(
				managedBean.getTabActiveIndex());
		if (managedBean.isPoUser() || managedBean.isApproverUser()) {
			assignMaxReviewCount(managedBean.getSmAppl().getApplType(),
					managedBean.getSmAppl().getApplCategoryType(), managedBean
							.getSmAppl().getSupplierType(),
					managedBean.getBusinessTypeVal(),
					managedBean.isReviewSiteVisit());
		}

		if (managedBean.getSmAppl().getSupplierType().equals("J")) {
			// added to custom confirm dialog title and msg
			// based on category type Physical or Non Physical
			setRegCatCodeConsConfirmDialog(managedBean);
		} else {
			// added to custom confirm dialog title and msg
			// based on category type Special or Normal
			setRegCatCodeConfirmDialog(managedBean);
		}
	}

	/**
	 * Sets the register categor code confirm dialog text.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void setRegCatCodeConfirmDialog(
			SmApplicationManagedBean managedBean) {
		managedBean.setRegCatCodeConfirmHeader(SmCommonUtil.getMessage(
				"lbl.sm.regCatCode.confirm.header.normal", null));
		managedBean.setRegCatCodeConfirmMsg(SmCommonUtil.getMessage(
				"lbl.sm.regCatCode.confirm.msg.normal", null));
		if (managedBean.getSmAppl() != null) {
			if (StringUtils.isNotBlank(managedBean.getSmAppl()
					.getApplCategoryType())
					&& managedBean.getSmAppl().getApplCategoryType()
							.equals("S")) {
				managedBean.setRegCatCodeConfirmHeader(SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.header.special", null));
				managedBean.setRegCatCodeConfirmMsg(SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.msg.special", null));
			}
		}
	}

	/**
	 * Sets the register categor code confirm dialog text for consultant.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	public static void setRegCatCodeConsConfirmDialog(
			SmApplicationManagedBean managedBean) {
		managedBean.setRegCatCodeConfirmHeader(SmCommonUtil.getMessage(
				"lbl.sm.regCatCode.confirm.header.physical", null));
		managedBean.setRegCatCodeConfirmMsg(SmCommonUtil.getMessage(
				"lbl.sm.regCatCode.confirm.msg.physical", null));
		if (managedBean.getSmAppl() != null) {
			if (StringUtils.isNotBlank(managedBean.getPhysicalConsulting())
					&& managedBean
							.getPhysicalConsulting()
							.equals(my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE)) {
				managedBean.setRegCatCodeConfirmHeader(SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.header.nonphysical", null));
				managedBean.setRegCatCodeConfirmMsg(SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.msg.nonphysical", null));
			}
		}
	}

	/**
	 * Get whether latest approved personnel is activated
	 * 
	 * @param identificationNo
	 *            , supplierId - String identificationNo - Long supplierId
	 */
	public static boolean isLatestApprovedPersonnelActivated(
			String identificationNo, Long supplierId) {
		boolean isAlreadyActivate = false;
		try {
			IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setIdentificationNo(identificationNo);
			reqDTO.setSmSupplierId(supplierId);
			PersonnelResDTO resDTO = personnelSO
					.retrieveLatestApprovedPersonnelByIdentificationNoAndSupplierId(reqDTO);
			if (resDTO.getSmPersonnel() != null
					&& StringUtils.isNotBlank(resDTO.getSmPersonnel()
							.getEpRole())
					&& resDTO.getSmPersonnel().getPmUser() != null
					&& resDTO.getSmPersonnel().getPmUser().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				isAlreadyActivate = true;
			}
		} catch (Exception ex) {
			log.info("Error in isLatestApprovedPersonnelAlreadyAuthorized:"
					+ ex.getMessage());
		}
		return isAlreadyActivate;
	}

	/**
	 * Retrieve pm state list.
	 * 
	 * @param countryId
	 *            - long country id
	 * @return the List<PmState>
	 */
	public static List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}

	/**
	 * Sets the pm state dropdown values for basic information panel.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 * @param countryId
	 *            - Long countryId
	 */
	public static void setPmStateDropdownValuesForBasicInfo(
			SmApplicationManagedBean managedBean, Long countryId) {

		List<SelectItem> stateLs = SmCommonUtil.getStateList(countryId);

		if (managedBean.getBasicCompanyStates() == null) {
			managedBean.setBasicCompanyStates(new ArrayList<SelectItem>());
		}
		if (managedBean.getSmSupplier().getBusinessType() != null
				&& !managedBean.getSmSupplier().getBusinessType()
						.equalsIgnoreCase("")) {

			if (managedBean
					.getBusinessTypeVal()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_INDIVIDUAL_DEFAULT)
					|| managedBean
							.getBusinessTypeVal()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PARTNER_DEFAULT)) {

				managedBean.setBasicCompanyStates(new ArrayList<SelectItem>());
				if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)) {
					for (SelectItem state : stateLs) {
						if (state.getLabel().equalsIgnoreCase("SARAWAK")) {
							managedBean.getBasicCompanyStates().add(state);
						}
					}
				} else if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)) {
					for (SelectItem state : stateLs) {
						if (state.getLabel().equalsIgnoreCase("SABAH")) {
							managedBean.getBasicCompanyStates().add(state);

						}
					}
				} else if (managedBean
						.getSmSupplier()
						.getBusinessType()
						.equalsIgnoreCase(
								SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG)
						|| managedBean
								.getSmSupplier()
								.getBusinessType()
								.equalsIgnoreCase(
										SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)) {

					for (SelectItem state : stateLs) {
						if (!state.getLabel().equalsIgnoreCase("SABAH")
								&& !state.getLabel()
										.equalsIgnoreCase("SARAWAK")) {
							managedBean.getBasicCompanyStates().add(state);
						}
					}
				}

			} else {
				managedBean.setBasicCompanyStates(stateLs);
			}
		} else {
			managedBean.setBasicCompanyStates(stateLs);
		}

	}

	/**
	 * Get academic qualifications from PmParameterDesc
	 * 
	 * @return the List<SelectItem>
	 */
	public static List<SelectItem> getAcademicQualifications(
			SmApplicationManagedBean managedBean) {
		List<SelectItem> academics = new ArrayList<SelectItem>();
		/*
		 * List<PmEduLevel> eduLvls = new ArrayList<PmEduLevel>();
		 * EducationLevelReqDTO reqDTO = new EducationLevelReqDTO();
		 * IEducationLevelSO reqSO = (IEducationLevelSO) BusinessDelegate
		 * .getInstance().getServiceObject("pm", IEducationLevelSO.class);
		 * EducationLevelResDTO resDTO =
		 * reqSO.retrieveEducationLevelList(reqDTO);
		 */

		List<PmParameterDesc> eduLvls = new ArrayList<PmParameterDesc>();
		// if (resDTO != null && resDTO.getEduLevelList() != null) {
		eduLvls = ParameterHelper.getInstance().getParameterDescList(
				ParameterTypeConst.QUALIFICATION_LEVEL);
		// sort
		/*
		 * Collections.sort(eduLvls, new Comparator<PmParameter>() {
		 * 
		 * @Override public int compare(final PmParameter s1, final PmParameter
		 * s2) { return StringUtil.upperCase(s2.getParameterCode()).compareTo(
		 * StringUtil.upperCase(s1.getParameterCode())); } });
		 */
		managedBean.setEduLevelList(eduLvls);
		// }

		academics.add(new SelectItem(null, SmCommonUtil.getMessage(
				"lbl.txt.select", null)));
		for (PmParameterDesc edl : eduLvls) {
			academics.add(new SelectItem(edl.getParameterDescId(), edl
					.getCodeName()));
		}
		return academics;
	}

	/**
	 * get application status for header
	 * 
	 * @param smAppl
	 * @return SmApplStatusInfoVO object
	 */
	public static SmApplStatusInfoVO getSmApplStatusInfoVO(SmAppl smAppl) {
		SmApplStatusInfoVO applStatusInfo = new SmApplStatusInfoVO();
		if (smAppl != null) {
			PmUser userCreated = null;
			PmUser userModified = null;
			IUserSO reqSO;
			try {
				reqSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

				UserReqDTO reqDTO = new UserReqDTO();
				UserResDTO resDTO = null;
				reqDTO.setUserId(smAppl.getCreatedBy());
				resDTO = reqSO.retrieveUserByUserId(reqDTO);
				userCreated = resDTO.getPmUser();
				reqDTO.setUserId(smAppl.getChangedBy());
				resDTO = reqSO.retrieveUserByUserId(reqDTO);
				userModified = resDTO.getPmUser();

			} catch (Exception e) {

			} finally {
				reqSO = null;
			}
			long dayBal = DateUtils.getDaysDifference(new Date(),
					smAppl.getDraftExpiryDate()) + 1;
			String[] dayLeftParam = { dayBal + "" };
			Date saveDate = smAppl.getApplSaveDate() == null ? smAppl
					.getChangedDate() : smAppl.getApplSaveDate();

			// ensure that modified date display is not more than created date
			saveDate = saveDate.compareTo(smAppl.getCreatedDate()) >= 0 ? saveDate
					: smAppl.getCreatedDate();

			applStatusInfo.setSubmissionDate(DateUtils.dateToString(
					"dd-MMM-yyyy", smAppl.getDraftExpiryDate()));
			applStatusInfo.setDaysLeft(SmCommonUtil.getMessage(
					"lbl.sm.appl.status.daysleft", dayLeftParam));
			applStatusInfo.setDaysLeftOriginal(applStatusInfo.getDaysLeft());
			applStatusInfo.setCreatedByAndDate((userCreated == null ? "SYSTEM"
					: userCreated.getUserName())
					+ " ("
					+ DateUtils.dateToString("dd-MMM-yyyy hh:mm a",
							smAppl.getCreatedDate()) + ")");
			applStatusInfo
					.setModifiedByAndDate((userModified == null ? "SYSTEM"
							: userModified.getUserName())
							+ " ("
							+ DateUtils.dateToString("dd-MMM-yyyy hh:mm a",
									saveDate) + ")");
		}
		return applStatusInfo;
	}

	/**
	 * Check whether need to disable company name field from editing. Only can
	 * be edited for draft New MOF Application
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isDisableCompanyNameInAppl(SmSupplier smSupplier,
			SmAppl smAppl) {
		boolean retVal = false;

		if (!smAppl.getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			retVal = true;
		} else {
			if (smAppl.getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)
					&& smAppl.isActiveAppl()) {
				if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME
						.contains(smSupplier.getBusinessType())) {
					retVal = true;
				} else if (isApplHasSoftcertProcessingNotComplete(smAppl)) {
					retVal = true;
				}
			} else {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Check whether need to disable business type field from editing. Can be
	 * edited for all application type.
	 * 
	 * @param SmSupplier
	 *            smSupplier
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isDisableBusinessTypeInAppl(SmSupplier smSupplier,
			SmAppl smAppl) {
		boolean retVal = false;
		if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_ALLOWED_TO_BE_EDITED
				.contains(smSupplier.getBusinessType())) {
			retVal = true;
		} else if (isApplHasSoftcertProcessingNotComplete(smAppl)) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Check if personnel in application got soft certificate that still
	 * processing
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isApplHasSoftcertProcessingNotComplete(SmAppl smAppl) {
		boolean retVal = false;
		for (SmPersonnel smPersonnel : smAppl.getSmPersonnels()) {
			if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Check if, application has already transacted before with basic
	 * account.Verify if application has a personnel with role
	 * Basic_Supplier_Admin and got soft certificate issued
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return boolean
	 */
	public static boolean isTransactWithBasicAccountApprovedSoftCert(
			SmAppl smAppl) {
		boolean retVal = false;
		for (SmPersonnel smPersonnel : smAppl.getSmPersonnels()) {
			if (smPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED
					&& smPersonnel.getEpRole() != null
					&& smPersonnel.getEpRole().equalsIgnoreCase(
							RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	/**
	 * Check whether personnel already request for soft certificate with
	 * Digicert
	 * 
	 * @param supplierId
	 * @param userId
	 * @return boolean
	 */
	public static boolean isPersonnelAlreadyRequestSoftcert(Long supplierId,
			Long userId) {
		boolean retVal = false;
		SmSoftcertRequest softcertRequest = getPersonnelSoftcertRequest(
				supplierId, userId);
		if (softcertRequest != null) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Get personnel softcert request
	 * 
	 * @param Long
	 *            supplierId
	 * @param Long
	 *            userId
	 * @return SmSoftcertRequest
	 */
	public static SmSoftcertRequest getPersonnelSoftcertRequest(
			Long supplierId, Long userId) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplierId);
		reqDTO.setUserId(userId);
		SmSoftcertRequest softcertRequest = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO)
				.getSoftcertRequest();
		return softcertRequest;
	}

	/**
	 * Retrieve latest approved application
	 * 
	 * @param Long
	 *            supplierId
	 * @return SmAppl
	 */
	public static SmAppl getLatestApplBySupplierId(Long supplierId) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplierId);
		MofRegistrationResDTO resDTO = SmServiceLookup.getMofRegistrationSO()
				.retrieveLatestSmApplWithChildBySupplierId(reqDTO);
		return resDTO.getSmAppl();
	}

	/**
	 * Set personnel authorized information when edit in personnel section
	 * 
	 * @param currAppl
	 * @param prevPersonnel
	 * @param currPersonnel
	 */
	public static void setPersonnelRoleChange(SmAppl currAppl,
			SmPersonnel prevPersonnel, SmPersonnel currPersonnel) {
		// already activated
		if (currPersonnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
			// if catalogue_user directly set softcert 0 and contractsigner 0
			if (StringUtils.isNotBlank(currPersonnel.getEpRole())
					&& currPersonnel.getEpRole().equals(
							RoleCode.CM_USER.value())) {
				if (currPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
					currPersonnel.setSoftcertFlg(0);
				}
				currPersonnel.setContractSignerFlg(0);
			} else {
				// if no epRole directly set contractsigner 0 and softcert 0
				if (StringUtils.isBlank(currPersonnel.getEpRole())) {
					if (currPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
						currPersonnel.setSoftcertFlg(0);
					}
					currPersonnel.setContractSignerFlg(0);
				}
				// if has epRole
				else {
					// if dont have prev approved directly set softcert to
					// pending request and contract to 0
					if (prevPersonnel == null) {
						currPersonnel
								.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
						currPersonnel.setContractSignerFlg(0);
					}
					// if have prev approved
					else {
						// if prev approved softcert is 0, directly set
						// to pending request
						if (prevPersonnel.getSoftcertFlg() == 0) {
							currPersonnel
									.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
						}
						// if prev approved softcert is not 0, directly
						// get prev approved softcert
						else {
							currPersonnel.setSoftcertFlg(prevPersonnel
									.getSoftcertFlg());
						}
						currPersonnel.setContractSignerFlg(prevPersonnel
								.getContractSignerFlg());
					}
				}
			}
		}
		// pending activation
		else {
			if (currPersonnel.getEpRole().equals(RoleCode.CM_USER.value())) {
				currPersonnel.setSoftcertFlg(0);
				currPersonnel.setContractSignerFlg(0);
			} else {
				currPersonnel
						.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
				currPersonnel.setContractSignerFlg(prevPersonnel == null ? 0
						: prevPersonnel.getContractSignerFlg());
			}
		}
	}

	/**
	 * Get latest resubmit application by rejected application
	 * 
	 * @param SmAppl
	 *            rejectedAppl
	 * @return SmAppl
	 */
	public static SmAppl getLatesResubmitApplicationByRejectedAppl(
			SmAppl rejectedAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSmAppl(rejectedAppl);
		return SmServiceLookup.getMofRegistrationSO()
				.retrieveLatestResubmitApplicationByRejectedAppl(reqDTO)
				.getSmAppl();
	}

	/**
	 * Initialized gst information for HQ
	 * 
	 * @param SmAppl
	 *            appl
	 * @param SmGstSupplier
	 *            gst
	 * @return SmGstSupplier
	 */
	public static SmGstSupplier initHqGstInformation(SmAppl appl,
			SmGstSupplier gst) {
		SmGstSupplier gstCurrent = SmCommonUtil
				.getSmGstSupplierBySupplierIdAndBranchCode(appl.getSmSupplier()
						.getSupplierId(), null, null, true);
		SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
		gstReqDTO.setBranchCode(null);
		gstReqDTO.setApplNo(appl.getApplNo());
		SmGstSupplier gstByApplNo = SmServiceLookup.getSupplierGstSO()
				.retrieveGstInfoByApplNoAndBranchCode(gstReqDTO)
				.getSmGstSupplier();

		if (gstByApplNo != null) {
			gst = gstByApplNo;
		} else {
			if (gstCurrent != null) {
				if (gstCurrent.getApplNo().equals(appl.getApplNo())) {
					gst = gstCurrent;
				} else {
					gst = new SmGstSupplier();
					gst.setGstRegNo(gstCurrent.getGstRegNo());
					gst.setGstEffDate(gstCurrent.getGstEffDate());
					gst.setGstEndDate(gstCurrent.getGstEndDate());
					if (gstCurrent.getSmGstSupplierDocs() != null
							&& !gstCurrent.getSmGstSupplierDocs().isEmpty()) {
						gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
						SmGstSupplierDoc doc = null;
						for (SmGstSupplierDoc docCurr : gstCurrent
								.getSmGstSupplierDocs()) {
							doc = new SmGstSupplierDoc();
							doc.setPmDocument(docCurr.getPmDocument());
							doc.setFileName(docCurr.getFileName());
							doc.setFilePath(docCurr.getFilePath());
							gst.getSmGstSupplierDocs().add(doc);
						}
					}
				}
			} else {
				gst = new SmGstSupplier();
			}
		}
		initGstSuppDocs(gst);
		return gst;
	}

	/**
	 * Initialized gst information for Branch
	 * 
	 * @param SmGstSupplier
	 *            hqGst
	 * @param SmAppl
	 *            appl
	 */
	public static void initBranchGstInformation(SmGstSupplier hqGst, SmAppl appl) {
		SupplierGstReqDTO reqDTO = null;
		SmGstSupplier brCurr = null;
		SmGstSupplier brByApplNo = null;
		List<SmSupplierBranch> branchList = appl.getSmSupplierBranches();
		if (!isGstEmpty(hqGst) && branchList != null) {
			for (SmSupplierBranch br : branchList) {
				reqDTO = new SupplierGstReqDTO();
				reqDTO.setApplNo(appl.getApplNo());
				reqDTO.setBranchCode(br.getBranchCode() == null ? br
						.getSupplierBranchId() : br.getBranchCode());
				brByApplNo = SmServiceLookup.getSupplierGstSO()
						.retrieveGstInfoByApplNoAndBranchCode(reqDTO)
						.getSmGstSupplier();
				brCurr = SmCommonUtil
						.getSmGstSupplierBySupplierIdAndBranchCode(appl
								.getSmSupplier().getSupplierId(), br
								.getBranchCode() != null ? br.getBranchCode()
								: br.getSupplierBranchId(), null, true);

				if (brByApplNo != null) {
					br.setGst(brByApplNo);
					br.setSameAsHq(SmApplicationUtil.isBranchGstSameAsHq(hqGst,
							br.getGst()));
				} else {
					if (brCurr != null) {
						if (brCurr.getApplNo().equals(appl.getApplNo())) {
							br.setGst(brCurr);
							br.setSameAsHq(SmApplicationUtil
									.isBranchGstSameAsHq(hqGst, br.getGst()));
						} else {
							br.setGst(new SmGstSupplier());
							br.getGst().setGstRegNo(brCurr.getGstRegNo());
							br.getGst().setGstEffDate(brCurr.getGstEffDate());
							br.getGst().setGstEndDate(brCurr.getGstEndDate());
							br.setSameAsHq(SmApplicationUtil
									.isBranchGstSameAsHq(hqGst, br.getGst()));
							if (brCurr.getSmGstSupplierDocs() != null
									&& !brCurr.getSmGstSupplierDocs().isEmpty()) {
								br.getGst().setSmGstSupplierDocs(
										new ArrayList<SmGstSupplierDoc>());
								SmGstSupplierDoc doc = null;
								for (SmGstSupplierDoc docCurr : brCurr
										.getSmGstSupplierDocs()) {
									doc = new SmGstSupplierDoc();
									doc.setPmDocument(docCurr.getPmDocument());
									doc.setFileName(docCurr.getFileName());
									doc.setFilePath(docCurr.getFilePath());
									br.getGst().getSmGstSupplierDocs().add(doc);
								}
							}
						}
					} else {
						br.setGst(new SmGstSupplier());
						br.setSameAsHq(true);
					}
				}
			}
		}
	}

	/**
	 * Add new gst to branch and check whether need to be copied gst information
	 * from HQ
	 * 
	 * @param SmGstSupplier
	 *            hqGst
	 * @param SmSupplierBranch
	 *            br
	 */
	public static void addNewBranchGst(SmGstSupplier hqGst, SmSupplierBranch br) {
		if (!isGstEmpty(hqGst)) {
			br.setGst(new SmGstSupplier());
			br.setSameAsHq(true);
			SmCommonUtil.copyHqGstToBranch(hqGst, br.getGst(), true,
					br.getBranchCode());
		} else {
			br.setGst(new SmGstSupplier());
			br.getGst().setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		}
		SmApplicationUtil.initGstSuppDocs(br.getGst());
	}

	/**
	 * Validate if added associate branch already reach maximum bank account for
	 * payment purpose
	 * 
	 * @param SmSupplierBranch
	 *            br
	 */
	public static void validateBankBranchLimit(SmSupplierBranch br) {
		int paymntBankCount = 0;
		if (!SmCommonUtil.isCollectionEmpty(br.getSmSupplierBranchBanks())) {
			for (SmSupplierBranchBank bankBranch : br
					.getSmSupplierBranchBanks()) {
				if (bankBranch.getSmSupplierBank().getAccountPurpose() != null
						&& (bankBranch.getSmSupplierBank().getAccountPurpose()
								.equals("B") || bankBranch.getSmSupplierBank()
								.getAccountPurpose().equals("P"))) {
					paymntBankCount++;
				}
			}
			// Branch already reached maximum bank accounts
			// allowed
			int maxPaymentBankForBranch = SmRuleHelper.getInstance()
					.getMaxPaymentBankForEachBranch();
			if (paymntBankCount > maxPaymentBankForBranch) {
				throw new EpException(MessageConst.ERROR_722620, new String[] {
						br.getBranchName(), maxPaymentBankForBranch + "" });
			}
		}
	}

	/**
	 * Check whether gst is empty or not
	 * 
	 * @param SmGstSupplier
	 *            gst
	 * @return boolean
	 */
	public static boolean isGstEmpty(SmGstSupplier gst) {
		boolean retVal = false;
		if (gst == null) {
			retVal = true;
		} else if (StringUtils.isBlank(gst.getGstRegNo())
				&& gst.getGstEffDate() == null && gst.getGstEndDate() == null) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Initialized gst supporting documents
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public static void initGstSuppDocs(SmGstSupplier gst) {
		List<PmDocument> suppDocList = SmServiceLookup.getDocumentSO()
				.retrieveGstSuppDocuments(new DocumentReqDTO())
				.getDocumentList();
		SmGstSupplierDoc suppDoc = null;
		if (gst == null) {
			gst = new SmGstSupplier();
		}
		if (suppDocList != null && !suppDocList.isEmpty()) {
			if (gst.getSmGstSupplierDocs() == null
					|| gst.getSmGstSupplierDocs().isEmpty()) {
				gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
				for (PmDocument doc : suppDocList) {
					suppDoc = new SmGstSupplierDoc();
					suppDoc.setPmDocument(doc);
					suppDoc.setSmGstSupplier(gst);
					gst.getSmGstSupplierDocs().add(suppDoc);
				}
			} else {
				for (PmDocument doc : suppDocList) {
					if (!isGstPmDocumentAlreadyAdded(doc,
							gst.getSmGstSupplierDocs())) {
						suppDoc = new SmGstSupplierDoc();
						suppDoc.setPmDocument(doc);
						suppDoc.setSmGstSupplier(gst);
						gst.getSmGstSupplierDocs().add(suppDoc);
					}
				}
			}
		}
	}

	private static boolean isGstPmDocumentAlreadyAdded(PmDocument gstPmDoc,
			List<SmGstSupplierDoc> gstDocList) {
		boolean retVal = false;
		for (SmGstSupplierDoc gstDoc : gstDocList) {
			if (gstDoc.getPmDocument() != null
					&& gstDoc.getPmDocument().getDocumentId()
							.compareTo(gstPmDoc.getDocumentId()) == 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	private static void validateRenewalApplTypeForSpecialCategory(SmAppl smAppl) {
		if (!smAppl.getApplCategoryType().equals(
				ParameterConst.CATEGORY_TYPE_NORMAL)
				&& smAppl.getApplType().equals(
						ParameterConst.NEW_SM_APPLN_FOR_RENEW)) {
			int count = 0;
			for (SmSupplierCategory category : smAppl.getSmSupplierCategories()) {
				if (category.getPmCategoryL3() != null
						&& category.getPmCategoryL3().getCategoryTypeCode() != null
						&& category.getPmCategoryL3().getCategoryTypeCode()
								.getParameterCode() != null) {
					if (!category.getPmCategoryL3().getCategoryTypeCode()
							.getParameterCode()
							.equals(ParameterConst.CATEGORY_TYPE_NORMAL)) {
						count++;
						break;
					}
				}
			}

			if (count == 0) {
				throw new EpException(MessageConst.ERROR_722606);
			}
		}
	}

	public static void setUpdatedBranchGstToMainList(
			List<SmSupplierBranch> branchList, SmGstSupplier gst) {
		if (branchList != null && !branchList.isEmpty()) {
			for (SmSupplierBranch br : branchList) {
				if (br.getBranchCode() != null
						&& gst.getBranchCode() != null
						&& br.getBranchCode().compareTo(gst.getBranchCode()) == 0) {
					br.setGst(gst);
				}
			}
		}
	}

	public static boolean isBranchGstSameAsHq(SmGstSupplier hqGst,
			SmGstSupplier brGst) {
		boolean retVal = false;
		if (hqGst.getGstEffDate() != null && brGst.getGstEffDate() != null
				&& StringUtils.isNotBlank(brGst.getGstRegNo())
				&& StringUtils.isNotBlank(hqGst.getGstRegNo())
				&& brGst.getGstRegNo().equals(hqGst.getGstRegNo())
				&& brGst.getGstEffDate().compareTo(hqGst.getGstEffDate()) == 0) {
			retVal = true;
		}
		return retVal;
	}

	public static SmFacility getHqFacilityByAppl(SmAppl appl) {
		for (SmFacility smFac : appl.getSmFacilities()) {
			if ((smFac.getFacilityType().equalsIgnoreCase(
					SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || smFac
					.getFacilityType()
					.equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
					&& smFac.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
				return smFac;
			}
		}
		return null;
	}

	public static boolean isMofAccEffDatePassedCurrDate(SmSupplier supplier) {
		boolean retVal = false;
		if (supplier != null && supplier.getSupplierId() != null) {
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			reqDTO.setParamMonths(6);
			int count = SmServiceLookup
					.getMofRegistrationSO()
					.countMofAccEffDatePassedCurrDateByMonthsAndSupplierId(
							reqDTO).getCount();
			if (count > 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static SmApplicationUtil getInstance() {
		if (instance == null) {
			try {
				instance = new SmApplicationUtil();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return instance;

	}

	public void populateStateByCountry(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long coutryId = addr.getCountryId();
			addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
					coutryId));
			List<PmState> stateLs = new ArrayList<PmState>();
			List<SelectItem> stateItems = SmCommonUtil.getStateList(coutryId);
			if (!panelName
					.equalsIgnoreCase(SupplierManagementConst.SM_BASIC_INFO_PANEL)) {
				stateLs = retrievePmStateList(coutryId);
			}
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				setPmStateDropdownValuesForBasicInfo(bean, coutryId);
				bean.setBasicCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBasicCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacStates(stateItems);
				bean.setBasicOffFacDistricts(new ArrayList<SelectItem>());
				bean.setBasicOffFacCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyStates(stateItems);
				bean.setBranchCompanyDistricts(new ArrayList<SelectItem>());
				bean.setBranchCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyStates(stateItems);
				bean.setAssocCompanyDistricts(new ArrayList<SelectItem>());
				bean.setAssocCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyStates(stateItems);
				bean.setEquiCompanyDistricts(new ArrayList<SelectItem>());
				bean.setEquiCompanyCities(new ArrayList<SelectItem>());
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityStates(stateItems);
				bean.setFactoryFacilityDistricts(new ArrayList<SelectItem>());
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityStates(stateItems);
				bean.setDockFacilityDistricts(new ArrayList<SelectItem>());
				bean.setDockFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityStates(stateItems);
				bean.setWorkShopFacilityDistricts(new ArrayList<SelectItem>());
				bean.setWorkShopFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityStates(stateItems);
				bean.setStoreFacilityDistricts(new ArrayList<SelectItem>());
				bean.setStoreFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermStates(stateItems);
				bean.setShrHldrPermDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdStates(stateItems);
				bean.setShrHldrResdDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffStates(stateItems);
				bean.setShrHldrOffDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailStates(stateItems);
				bean.setShrHldrMailDistricts(new ArrayList<SelectItem>());
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankStates(stateItems);
				bean.setBankDistricts(new ArrayList<SelectItem>());
				bean.setBankCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermStates(stateLs);
				bean.setPersnlPermDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlPermCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdStates(stateLs);
				bean.setPersnlResdDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlResdCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffStates(stateLs);
				bean.setPersnlOffDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlOffCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailStates(stateLs);
				bean.setPersnlMailDistricts(new ArrayList<PmDistrict>());
				bean.setPersnlMailCities(new ArrayList<PmCity>());
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			addr.setStateId(null);
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateDistrictsByState(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long stateId = addr.getStateId();
			if (stateId != null) {
				addr.setPmState(SmCommonUtil.getInstance()
						.getStateById(stateId));
			}
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			List<PmDistrict> distLs = resDTO.getDistrictList();
			List<SelectItem> distItems = SmCommonUtil.getDistrictList(stateId);
			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyDistricts(distItems);
				bean.setBasicCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacDistricts(distItems);
				bean.setBasicOffFacCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyDistricts(distItems);
				bean.setBranchCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyDistricts(distItems);
				bean.setAssocCompanyCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyDistricts(distItems);
				bean.setEquiCompanyCities(new ArrayList<SelectItem>());
				break;

			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityDistricts(distItems);
				bean.setFactoryFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityDistricts(distItems);
				bean.setDockFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityDistricts(distItems);
				bean.setWorkShopFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityDistricts(distItems);
				bean.setStoreFacilityCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermDistricts(distItems);
				bean.setShrHldrPermCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdDistricts(distItems);
				bean.setShrHldrResdCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffDistricts(distItems);
				bean.setShrHldrOffCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailDistricts(distItems);
				bean.setShrHldrMailCities(new ArrayList<SelectItem>());
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermDistricts(distLs);
				bean.setPersnlPermCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdDistricts(distLs);
				bean.setPersnlResdCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffDistricts(distLs);
				bean.setPersnlOffCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailDistricts(distLs);
				bean.setPersnlMailCities(new ArrayList<PmCity>());
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankDistricts(distItems);
				bean.setBankCities(new ArrayList<SelectItem>());
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			addr.setDistrictId(null);
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateCitiesByDistrict(SmAddress addr, String panelName,
			SmApplicationManagedBean bean) {
		try {
			Long districtId = addr.getDistrictId();
			addr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
					districtId));
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			List<PmCity> cityLs = resDTO.getCityList();
			List<SelectItem> cityItems = SmCommonUtil.getCityList(districtId);

			switch (panelName) {
			case SupplierManagementConst.SM_BASIC_INFO_PANEL:
				bean.setBasicCompanyCities(SmCommonUtil.getCityList(districtId));
				break;
			case SupplierManagementConst.SM_OFFICE_FACILITY_INFO_PANEL:
				bean.setBasicOffFacCities(cityItems);
				break;
			case SupplierManagementConst.SM_BRANCH_PANEL:
				bean.setBranchCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_ASSOC_COMPANY_PANEL:
				bean.setAssocCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_EQUI_COMPANY_PANEL:
				bean.setEquiCompanyCities(cityItems);
				break;
			case SupplierManagementConst.SM_FACTORY_FACILITY_PANEL:
				bean.setFactoryFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_DOCKYARD_FACILITY_PANEL:
				bean.setDockFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_WORKSHOP_FACILITY_PANEL:
				bean.setWorkShopFacilityCities(cityItems);
				break;
			case SupplierManagementConst.SM_STORE_FACILITY_PANEL:
				bean.setStoreFacilityCities(cityItems);
			case SupplierManagementConst.SM_SHRHLDR_PERM_ADDR_PANEL:
				bean.setShrHldrPermCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_RESD_ADDR_PANEL:
				bean.setShrHldrResdCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_OFF_ADDR_PANEL:
				bean.setShrHldrOffCities(cityItems);
				break;
			case SupplierManagementConst.SM_SHRHLDR_MAIL_ADDR_PANEL:
				bean.setShrHldrMailCities(cityItems);
				break;
			case SupplierManagementConst.SM_PERSNL_PERM_ADDR_PANEL:
				bean.setPersnlPermCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_RESD_ADDR_PANEL:
				bean.setPersnlResdCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_OFF_ADDR_PANEL:
				bean.setPersnlOffCities(cityLs);
				break;
			case SupplierManagementConst.SM_PERSNL_MAIL_ADDR_PANEL:
				bean.setPersnlMailCities(cityLs);
				break;
			case SupplierManagementConst.SM_BANK_INFO_PANEL:
				bean.setBankCities(cityItems);
				break;
			default:
				log.info("**** This is an unknown panel ***** ");
				break;
			}
			addr.setCityId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assign or populate states, districits and cities in the selected panel
	 * for sel values.
	 * 
	 * @param countryId
	 *            - Long country id
	 * @param stateID
	 *            - Long state id
	 * @param districtId
	 *            - Long district id
	 * @param panelName
	 *            - String panel name
	 */
	public void assignStateDistAndCityForSelVal(SmApplicationManagedBean bean,
			SmAddress addr, String panelName) {
		if (addr.getPmCountry() != null) {
			addr.setCountryId(addr.getPmCountry().getCountryId());
		}
		getInstance().populateStateByCountry(addr, panelName, bean);
		if (addr.getPmState() != null) {
			addr.setStateId(addr.getPmState().getStateId());
		}
		getInstance().populateDistrictsByState(addr, panelName, bean);
		if (addr.getPmDistrict() != null) {
			addr.setDistrictId(addr.getPmDistrict().getDistrictId());
		}
		getInstance().populateCitiesByDistrict(addr, panelName, bean);
		if (addr.getPmCity() != null) {
			addr.setCityId(addr.getPmCity().getCityId());
		}
	}

	public void setApplAddressTransientValue(SmAppl appl, boolean isSave) {
		// company basic
		if (appl.getSmCompanyBasic() != null
				&& !SmCommonUtil.isCollectionEmpty(appl.getSmCompanyBasic()
						.getSmCompanyAddresses())) {
			if (isSave) {
				setAddressTransientValueBeforeSave(appl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress());
			} else {
				setAddressTransientValueAfterLoad(appl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress());
			}
		}
		// facility
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmFacilities())) {
			for (SmFacility smFac : appl.getSmFacilities()) {
				if (smFac.getFacilityType() != null) {
					if (isSave) {
						setAddressTransientValueBeforeSave(smFac.getSmAddress());
					} else {
						setAddressTransientValueAfterLoad(smFac.getSmAddress());
					}
				}
			}
		}
		// assoc company
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmAssociateCompanies())) {
			for (SmAssociateCompany assoc : appl.getSmAssociateCompanies()) {
				if (isSave) {
					setAddressTransientValueBeforeSave(assoc.getSmAddress());
				} else {
					setAddressTransientValueAfterLoad(assoc.getSmAddress());
				}
			}
		}
		// branch
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmSupplierBranches())) {
			for (SmSupplierBranch branch : appl.getSmSupplierBranches()) {
				if (isSave) {
					setAddressTransientValueBeforeSave(branch.getSmAddress());
				} else {
					setAddressTransientValueAfterLoad(branch.getSmAddress());
				}
			}
		}
		// bank
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmSupplierBanks())) {
			for (SmSupplierBank bank : appl.getSmSupplierBanks()) {
				if (isSave) {
					setAddressTransientValueBeforeSave(bank.getSmAddress());
				} else {
					setAddressTransientValueAfterLoad(bank.getSmAddress());
				}
			}
		}
		// smPersonnel
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmPersonnels())) {
			for (SmPersonnel pers : appl.getSmPersonnels()) {
				if (!SmCommonUtil.isCollectionEmpty(pers
						.getSmPersonnelAddresses())) {
					for (SmPersonnelAddress perAddr : pers
							.getSmPersonnelAddresses()) {
						if (isSave) {
							setAddressTransientValueBeforeSave(perAddr
									.getSmAddress());
						} else {
							setAddressTransientValueAfterLoad(perAddr
									.getSmAddress());
						}
					}
				}
			}
		}
		// smCompanyEquities
		if (!SmCommonUtil.isCollectionEmpty(appl.getSmEquityCompanies())) {
			for (SmEquityCompany eq : appl.getSmEquityCompanies()) {
				if (isSave) {
					setAddressTransientValueBeforeSave(eq.getSmAddress());
				} else {
					setAddressTransientValueAfterLoad(eq.getSmAddress());
				}
			}
		}
	}

	public void setAddressTransientValueAfterLoad(SmAddress addr) {
		if (addr != null) {
			if (addr.getPmCountry() != null
					&& addr.getPmCountry().getCountryId() != null) {
				addr.setCountryId(addr.getPmCountry().getCountryId());
			}
			if (addr.getPmState() != null
					&& addr.getPmState().getStateId() != null) {
				addr.setStateId(addr.getPmState().getStateId());
			}
			if (addr.getPmDistrict() != null
					&& addr.getPmDistrict().getDistrictId() != null) {
				addr.setDistrictId(addr.getPmDistrict().getDistrictId());
			}
			if (addr.getPmCity() != null
					&& addr.getPmCity().getCityId() != null) {
				addr.setCityId(addr.getPmCity().getCityId());
			}
		}
	}

	public void setAddressTransientValueBeforeSave(SmAddress addr) {
		if (addr != null) {
			if (addr.getCountryId() != null) {
				addr.setPmCountry(SmCommonUtil.getInstance().getCountryById(
						addr.getCountryId()));
			}
			if (addr.getStateId() != null) {
				addr.setPmState(SmCommonUtil.getInstance().getStateById(
						addr.getStateId()));
			}
			if (addr.getDistrictId() != null) {
				addr.setPmDistrict(SmCommonUtil.getInstance().getDistrictById(
						addr.getDistrictId()));
			}
			if (addr.getCityId() != null) {
				addr.setPmCity(SmCommonUtil.getInstance().getCityById(
						addr.getCityId()));
			}
		}
	}

	public static void checkPersonnelsEmailDomainWithExceptionMsg(
			List<SmPersonnel> personnels) {
		// StringBuffer bfFinal = new StringBuffer();
		// if (!SmCommonUtil.isCollectionEmpty(personnels)) {
		// StringBuffer bfTmp = new StringBuffer();
		// int i = 0;
		// String email = "";
		// for (SmPersonnel per : personnels) {
		// email = per.getPmUser() != null
		// && StringUtils.isNotBlank(per.getPmUser().getEmail()) ? per
		// .getPmUser().getEmail() : per.getEmail();
		// if (!SmCommonUtil.isValidEmailDomain(email)) {
		// i++;
		// bfTmp.append(i + ") ").append(per.getName()).append(" (")
		// .append(per.getPmUser().getEmail()).append(")")
		// .append("<br />");
		// }
		// }
		// if (bfTmp.length() > 0) {
		// bfFinal.append(bfTmp.toString());
		// }
		// }
		// if (bfFinal.length() > 0) {
		// throw new EpException(MessageConst.ERROR_721116,
		// new String[] { bfFinal.toString() });
		// }
	}

	private boolean isCatL3Valid(SmAppl appl, String facilityType,
			SmSupplierCategory cat) {
		boolean retVal = true;
		String suppType = StringUtils.isBlank(appl.getSupplierType()) ? ""
				: appl.getSupplierType();
		Integer paidUpCapital = (appl.getSmCompanyBasic() != null && appl
				.getSmCompanyBasic().getPaidUpCapital() != null) ? appl
				.getSmCompanyBasic().getPaidUpCapital().intValue() : 0;
		Integer bumiPercentage = (appl.getSmCompanyBasic() != null && appl
				.getSmCompanyBasic().getBumiPercentage() != null) ? appl
				.getSmCompanyBasic().getBumiPercentage().intValue() : 0;
		String applCatType = StringUtils.isNotBlank(appl.getApplCategoryType()) ? appl
				.getApplCategoryType() : "";
		String businessType = (appl.getSmSupplier() != null && StringUtils
				.isNotBlank(appl.getSmSupplier().getBusinessType())) ? appl
				.getSmSupplier().getBusinessType() : "";
		Long catL2Id = (cat.getPmCategoryL2() != null && cat.getPmCategoryL2()
				.getCategoryL2Id() != null) ? cat.getPmCategoryL2()
				.getCategoryL2Id() : null;
		Long catL3Id = (cat.getPmCategoryL3() != null && cat.getPmCategoryL3()
				.getCategoryL3Id() != null) ? cat.getPmCategoryL3()
				.getCategoryL3Id() : null;
		if (catL2Id != null && catL3Id != null) {
			CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
			reqDTO.setCategoryL2Id(catL2Id);
			reqDTO.setSupplierTypeCode(suppType);
			reqDTO.setBusinessTypeCode(businessType);
			if (applCatType.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
				reqDTO.setIsSpecialCategory(false);
			} else {
				reqDTO.setIsSpecialCategory(true);
			}
			if (facilityType.equalsIgnoreCase("H")) {
				reqDTO.setHomeOperated(1);
			} else {
				reqDTO.setHomeOperated(0);
			}
			if (paidUpCapital != null) {
				reqDTO.setPaidUpCapital(paidUpCapital.intValue());
			}
			Integer bumiPerc = new Integer(0);
			if (bumiPercentage != null) {
				bumiPerc = bumiPercentage.intValue();
				if (bumiPerc.compareTo(51) >= 0 && bumiPerc.compareTo(100) < 0) {
					bumiPerc = 99;
				}
			}
			reqDTO.setBumiPercentage(bumiPerc);
			CategoryCodeResDTO resDTO = SmServiceLookup.getCategoryCodeSO()
					.retrieveCategoryL3ListByL2Id(reqDTO);
			if (!SmCommonUtil.isCollectionEmpty(resDTO.getCategoryL3List())) {
				for (PmCategoryL3 l3 : resDTO.getCategoryL3List()) {
					if (l3.getCategoryL3Id().compareTo(catL3Id) == 0) {
						retVal = true;
						break;
					}
				}
			} else {
				retVal = false;
			}
		} else {
			retVal = false;
		}
		return retVal;
	}

	/**
	 * Get category types from pending activation category
	 * 
	 * @param SmAppl
	 *            appl
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getSelectedCategoryTypeItems(SmAppl appl) {
		String language = new FacesScopes().getUserLocale().getLanguage();
		List<SelectItem> categoryTypeItems = new ArrayList<SelectItem>();
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& Arrays
								.asList(my.ep.constant.sm.SupplierManagementConst.SM_CAT_TYPE_MACHINERY)
								.contains(
										cat.getPmCategoryL3()
												.getCategoryTypeCode()
												.getParameterCode())
						&& !isCategoryRejected(cat)) {
					SelectItem catType = (SelectItem) EntityHelper
							.getEntityBySinglePropertyFromList(cat
									.getPmCategoryL3().getCategoryTypeCode()
									.getParameterId(), categoryTypeItems,
									"value");
					if (catType == null) {
						if (!EntityHelper.isCollectionEmpty(cat
								.getPmCategoryL3().getCategoryTypeCode()
								.getPmParameterDescs())) {
							SelectItem catTypeItem = new SelectItem();
							PmParameterDesc desc = (PmParameterDesc) EntityHelper
									.getEntityBySinglePropertyFromList(
											language, cat.getPmCategoryL3()
													.getCategoryTypeCode()
													.getPmParameterDescs(),
											"languageCode");
							if (desc != null) {
								catTypeItem.setLabel(desc.getCodeName());
								catTypeItem
										.setValue(cat.getPmCategoryL3()
												.getCategoryTypeCode()
												.getParameterId());
								categoryTypeItems.add(catTypeItem);
							}
						}
					}
				}
			}
		}
		return categoryTypeItems;
	}

	/**
	 * Get pending activation category codes by category type Id
	 * 
	 * @param Long
	 *            categoryTypeId
	 * @param SmAppl
	 *            appl
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getCategoryCodesItemsByCatTypeId(
			Long categoryTypeId, SmAppl appl) {
		List<SelectItem> categoryCodeItems = new ArrayList<SelectItem>();
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode()
								.getParameterId().compareTo(categoryTypeId) == 0) {
					SelectItem catCodeItem = new SelectItem();
					catCodeItem.setLabel(cat.getPmCategoryL3()
							.getCategoryL3Code()
							+ " - "
							+ cat.getPmCategoryL3().getCategoryName());
					catCodeItem.setValue(cat.getPmCategoryL3()
							.getCategoryL3Id());
					categoryCodeItems.add(catCodeItem);
				}
			}
		}
		return categoryCodeItems;
	}

	/**
	 * Validate machine or equipment section
	 * 
	 * @param boolean valid
	 * @param int currentTab
	 * @param SmApplicationManagedBean
	 *            bean
	 * @return boolean
	 */
	public static boolean validateMachineDetails(boolean valid, int currentTab,
			SmApplicationManagedBean bean) {
		SmAppl appl = bean.getSmAppl();
		PmParameterDesc desc = null;
		String language = new FacesScopes().getUserLocale().getLanguage();
		String categoryTypeDesc = StringUtils.EMPTY;
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& cat.getPmCategoryL3() != null
						&& cat.getPmCategoryL3().getCategoryTypeCode() != null
						&& Arrays
								.asList(my.ep.constant.sm.SupplierManagementConst.SM_CAT_TYPE_MACHINERY)
								.contains(
										cat.getPmCategoryL3()
												.getCategoryTypeCode()
												.getParameterCode())) {
					desc = (PmParameterDesc) EntityHelper
							.getEntityBySinglePropertyFromList(language, cat
									.getPmCategoryL3().getCategoryTypeCode()
									.getPmParameterDescs(), "languageCode");
					categoryTypeDesc = desc != null ? desc.getCodeName()
							: categoryTypeDesc;

					if (bean.isShowMachineryEquipmentPanel()) {
						if (EntityHelper.isCollectionEmpty(appl
								.getSmMachineDtls())) {
							valid = false;
							if (currentTab != -1) {
								setNavigationBackToCurrentPage(currentTab, bean);
							}
							throw new EpException(MessageConst.ERROR_722631,
									new String[] { categoryTypeDesc });
						} else {
							SmMachineDtl mac = (SmMachineDtl) EntityHelper
									.getEntityBySinglePropertyFromList(cat
											.getPmCategoryL3()
											.getCategoryTypeCode()
											.getParameterId(),
											appl.getSmMachineDtls(),
											"categoryType");
							if (mac == null) {
								valid = false;
								if (currentTab != -1) {
									setNavigationBackToCurrentPage(currentTab,
											bean);
								}
								throw new EpException(
										MessageConst.ERROR_722631,
										new String[] { categoryTypeDesc });
							}
						}
					}

				}
			}
		}
		return valid;
	}

	/**
	 * Validate added branch GST information
	 * 
	 * @param boolean valid
	 * @param int currentTab
	 * @param SmApplicationManagedBean
	 *            bean
	 */
	public static void validateBranchGstInformation(boolean valid,
			int currentTab, SmApplicationManagedBean bean) {
		SmSupplierBranch br = bean.getSelectedBranch();
		if (bean.getCompanyBasicGst() != null
				&& !StringUtils
						.isBlank(bean.getCompanyBasicGst().getGstRegNo())) {
			if (br.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
					&& (br.getGst() == null || StringUtils.isBlank(br.getGst()
							.getGstRegNo()))) {
				throw new EpException(SmCommonUtil.getMessage(
						"lbl.sm.gst.branch.req.msg", null));
			}
		}
	}

	/**
	 * Get machine select items by machine detail
	 * 
	 * @param SmMachineDtl
	 *            machine
	 * @return List<SelectItem>
	 */
	public static List<SelectItem> getMachineItemsByMachineDetails(
			SmMachineDtl machine) {
		List<SelectItem> machineItems = new ArrayList<SelectItem>();
		if (machine != null) {
			SelectItem others = new SelectItem();
			others.setValue(-1);
			others.setLabel(SmCommonUtil.getMessage("lbl.sm.caps.others", null));
			if (machine.getCategoryType() != null
					&& machine.getCategoryType().getParameterId() != null
					&& (machine.getCategoryCode() == null || machine
							.getCategoryCode().getCategoryL3Id() == null)) {
				machineItems = SmCommonUtil.getInstance()
						.getMachineItemsByCatTypeCode(
								machine.getCategoryType().getParameterCode());
			} else {
				if (machine.getCategoryCode() != null
						&& machine.getCategoryCode().getCategoryL3Id() != null) {
					machineItems = SmCommonUtil
							.getPrintingMachineItemsByCategoryCode(machine
									.getCategoryCode());
				}
			}
			machineItems.add(others);
		}
		return machineItems;
	}

	/**
	 * Get supplier admin and supplier user that already had login by appl
	 * 
	 * @param SmAppl
	 *            smAppl
	 * @return List<PmUser>
	 */
	public static List<PmUser> getActivatedSupplierUserList(SmAppl smAppl) {
		List<PmUser> retVal = new ArrayList<PmUser>();
		if (smAppl != null
				&& !EntityHelper.isCollectionEmpty(smAppl.getSmPersonnels())) {
			for (SmPersonnel personnel : smAppl.getSmPersonnels()) {
				if (personnel.getPmUser() != null
						&& !StringUtils.isBlank(personnel.getEpRole())
						&& (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
								.contains(personnel.getEpRole()) || my.ep.constant.sm.SupplierManagementConst.SUPPLIER_USER_ROLES
								.contains(personnel.getEpRole()))) {
					retVal.add(personnel.getPmUser());
				}
			}
		}
		return retVal;
	}

	public static void generateOfflineSuppDocChecklistPdf(SmAppl smAppl) {
		try {
			String tmpPath = null;
			String tmpFolder = SmCommonUtil
					.getUploadFilePath(SupplierManagementConst.OFFLINE_DOC_CHECKLIST_PATH);
			tmpPath = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			tmpPath = tmpPath + File.separator + tmpFolder;
			File dir = new File(tmpPath);
			if (!dir.exists())
				dir.mkdirs();
			String fileName = smAppl.getApplNo()
					+ SupplierManagementConst.OFFLINE_SUPP_DOC_CHECKLIST_PREFIX;
			String fileNameAndPath = tmpPath + fileName;
			String vm = "/sm/application/supp-doc-checklist.vm";
			VelocityContext context = new VelocityContext();
			context.put("header", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.checklist.application.no", null));
			context.put("applNo", smAppl.getApplNo());
			context.put("note1", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.note1", null));
			context.put("note2", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.note2", null));
			context.put("note3", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.note3", null));
			context.put("peninAddr1", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.peninAddr1", null));
			context.put("peninAddr2", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.peninAddr2", null));
			context.put("sabahAddr1", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.sabahAddr1", null));
			context.put("sabahAddr2", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.sabahAddr2", null));
			context.put("sarawakAddr1", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.sarawakAddr1", null));
			context.put("sarawakAddr2", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.offline.sarawakAddr2", null));
			context.put("supportDocs", smAppl.getSmApplSupportDocs());
			context.put("docDesc", SmCommonUtil.getMessage(
					"lbl.sm.suppDoc.document.description", null));
			SmCommonUtil.generateToPdf(vm, context, fileNameAndPath);
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * generate the MOF declaration form and populate required informatiion into
	 * the PDF.
	 * 
	 * @param smAppl
	 */
	public static void generateDeclarationFormPdf(SmAppl smAppl) {
		try {
			String tmpPath = null;
			String tmpFolder = SmCommonUtil
					.getUploadFilePath(SupplierManagementConst.SUPPORTING_DOC_TEMP_PATH);
			tmpPath = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			tmpPath = tmpPath + File.separator + tmpFolder;
			File dir = new File(tmpPath);
			if (!dir.exists())
				dir.mkdirs();
			String fileName = smAppl.getApplNo()
					+ SupplierManagementConst.DECLARE_SUPP_DOC_PREFIX;
			String fileNameAndPath = tmpPath + fileName;
			String vm = "/sm/application/MOF-declaration-form.vm";
			VelocityContext context = new VelocityContext();
			FacesScopes fs = new FacesScopes();
			context.put("hostUrl", fs.getRequest().getServerName() + ":"
					+ fs.getRequest().getServerPort());
			context.put("applNo", smAppl.getApplNo());
			context.put(
					"applType",
					SmCommonUtil.retrieveParameterDescByTypeAndCodeAndLang(
							ParameterTypeConst.SUPPLIER_APPLICATION_TYPES,
							smAppl.getApplType(), "en").getCodeName());
			context.put("companyName", smAppl.getSmSupplier().getCompanyName());
			SmCommonUtil.generateToPdf(vm, context, fileNameAndPath);
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Generate the bank verification form in PDF format for the respective
	 * application
	 * 
	 * @param smAppl
	 */
	public static void generateBankVerificationFormPdf(SmAppl smAppl) {
		try {
			String tmpPath = null;
			String tmpFolder = SmCommonUtil
					.getUploadFilePath(SupplierManagementConst.SUPPORTING_DOC_TEMP_PATH);
			tmpPath = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
			tmpPath = tmpPath + File.separator + tmpFolder;
			File dir = new File(tmpPath);
			if (!dir.exists())
				dir.mkdirs();

			List<String> filePaths = new ArrayList<String>();
			String fileName = smAppl.getApplNo()
					+ SupplierManagementConst.BANK_VERIFICATION_FORM_PREFIX;

			for (int i = 0; i < smAppl.getSmSupplierBanks().size(); i++) {
				SmSupplierBank supplierBank = smAppl.getSmSupplierBanks()
						.get(i);

				String fileNameAndPath = tmpPath + i + fileName;
				String vm = "/sm/application/Bank-verification-form.vm";
				VelocityContext context = new VelocityContext();
				FacesScopes fs = new FacesScopes();
				context.put("hostUrl", fs.getRequest().getServerName() + ":"
						+ fs.getRequest().getServerPort());
				context.put("companyName", smAppl.getSmSupplier()
						.getCompanyName());
				context.put("companyRegNo", smAppl.getSmSupplier().getRegNo());
				context.put("bankName", supplierBank.getPmFinancialOrg()
						.getFinOrgName());
				context.put("bankBranch", supplierBank.getBankBranch());
				context.put("bankStreetAddress",
						getBankAddress(supplierBank.getSmAddress()));
				context.put("bankCityDistrict",
						getBankCityDistrict(supplierBank.getSmAddress()));
				context.put("bankPostCode", supplierBank.getSmAddress()
						.getPostcode());
				context.put("bankState", supplierBank.getSmAddress()
						.getPmState().getStateName());
				context.put("bankAccountNo", supplierBank.getAccountNo());
				context.put("bankTnC", supplierBank.getSignatureInstruction());
				context = setSignatories(context,
						supplierBank.getSmChequeSignatories());

				SmCommonUtil.generateToPdf(vm, context, fileNameAndPath);
				filePaths.add(fileNameAndPath);
			}

			List<File> tempFiles = new ArrayList<File>();
			for (String filePath : filePaths) {
				tempFiles.add(new File(filePath));
			}
			SmCommonUtil.combinePdf(tempFiles, tmpPath + fileName);
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Assign supplier category facility default value during edit facility
	 * (FACTORY/WORKSHOP/STORE/DOCKYARD)
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param SmFacility
	 *            facility
	 */
	public static void assignSupplierCategoryFacility(
			SmApplicationManagedBean bean, SmFacility facility) {
		bean.setSelectedSuppCategoryFacilityIds(null);

		// assign category to facility
		Long[] suppCatFacIds = null;
		if (EntityHelper.isCollectionEmpty(facility
				.getSmSupplierCategoryFacilities())) {
			suppCatFacIds = new Long[0];
		} else {
			suppCatFacIds = new Long[facility.getSmSupplierCategoryFacilities()
					.size()];
			for (int i = 0; i < suppCatFacIds.length; i++) {
				suppCatFacIds[i] = facility.getSmSupplierCategoryFacilities()
						.get(i).getPmCategoryL3().getCategoryL3Id();

			}
		}
		bean.setSelectedSuppCategoryFacilityIds(suppCatFacIds);
	}

	/**
	 * Add or update facility category during add new facility or update
	 * existing facility
	 * 
	 * @param SmApplicationManagedBean
	 *            bean
	 * @param SmFacility
	 *            smFac
	 */
	public static void addOrUpdateFacilityCategory(
			SmApplicationManagedBean bean, SmFacility smFac) {
		if (bean.getSelectedSuppCategoryFacilityIds() != null) {
			SmSupplierCategoryFacility catFac = null;
			if (EntityHelper.isCollectionEmpty(smFac
					.getSmSupplierCategoryFacilities())) {
				smFac.setSmSupplierCategoryFacilities(new ArrayList<SmSupplierCategoryFacility>());
			}
			for (Long catId : bean.getSelectedSuppCategoryFacilityIds()) {
				for (SmSupplierCategory cat : bean.getSmAppl()
						.getSmSupplierCategories()) {

					// insert new selected category
					if (cat.getPmCategoryL3().getCategoryL3Id().equals(catId)) {
						catFac = getSupplierCategoryFacilityByCategoryL3(
								smFac.getSmSupplierCategoryFacilities(), catId);
						if (catFac == null) {
							catFac = new SmSupplierCategoryFacility();
							catFac.setSmFacility(smFac);
							catFac.setPmCategoryL3(cat.getPmCategoryL3());
							smFac.getSmSupplierCategoryFacilities().add(catFac);
						}
					}
				}
			}

			// remove not selected category
			if (!EntityHelper.isCollectionEmpty(smFac
					.getSmSupplierCategoryFacilities())) {
				Iterator<SmSupplierCategoryFacility> removeCatFacIt = smFac
						.getSmSupplierCategoryFacilities().iterator();
				while (removeCatFacIt.hasNext()) {
					SmSupplierCategoryFacility removeCatFac = removeCatFacIt
							.next();

					if (!EntityHelper.isIdExistInIds(removeCatFac
							.getPmCategoryL3().getCategoryL3Id(), bean
							.getSelectedSuppCategoryFacilityIds())) {
						removeCatFacIt.remove();
					}
				}
			}

		} else {
			// none selected so remove all
			smFac.setSmSupplierCategoryFacilities(null);
		}
	}

	private static SmSupplierCategoryFacility getSupplierCategoryFacilityByCategoryL3(
			List<SmSupplierCategoryFacility> list, Long id) {
		SmSupplierCategoryFacility retVal = null;
		for (SmSupplierCategoryFacility obj : list) {
			if (obj.getPmCategoryL3().getCategoryL3Id().equals(id)) {
				retVal = obj;
				break;
			}
		}
		return retVal;
	}

	private static VelocityContext setSignatories(VelocityContext context,
			List<SmChequeSignatory> smChequeSignatories) {
		for (int i = 0; i < smChequeSignatories.size(); i++) {
			SmChequeSignatory smChequeSignatory = smChequeSignatories.get(i);
			context.put("csNo" + i, i + 1);
			context.put("csName" + i, smChequeSignatory.getSmPersonnel()
					.getName());
			context.put("csIc" + i, smChequeSignatory.getSmPersonnel()
					.getIdentificationNo());
			System.out.println("Signatory " + i + ": "
					+ smChequeSignatory.getSmPersonnel().getName() + " : "
					+ smChequeSignatory.getSmPersonnel().getIdentificationNo());
		}
		return context;
	}

	private static String getBankCityDistrict(SmAddress address) {
		String cityName = address.getPmCity().getCityName();
		String districtName = address.getPmDistrict().getDistrictName();

		return cityName.concat(", ").concat(districtName);
	}

	private static String getBankAddress(SmAddress address) {

		String streetAddress = address.getAddress1();
		streetAddress = checkAddressSuffix(address.getAddress1())
				+ checkAddressSuffix(address.getAddress2())
				+ checkAddressSuffix(address.getAddress3())
				+ checkAddressSuffix(address.getAddress4());

		return streetAddress;
	}

	private static String checkAddressSuffix(String addressLine) {
		if (StringUtils.isBlank(addressLine)) {
			return "";
		}
		if (addressLine.substring(addressLine.length() - 1).equals(",")) {
			addressLine.concat(", ");
		}
		return addressLine;
	}

	public static SmAppl deleteApplChildEntities(SmAppl appl) {
		appl.setSmApplSectionReviews(new ArrayList<SmApplSectionReview>());
		appl.setSmApplSupportDocs(new ArrayList<SmApplSupportDoc>());
		appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		appl.setSmAssociationBodies(new ArrayList<SmAssociationBody>());
		appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		appl.setSmSupplierSystems(new ArrayList<SmSupplierSystem>());
		appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		appl.setSmStaffTotals(new ArrayList<SmStaffTotal>());
		appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		appl.setSmApplRejectReasons(new ArrayList<SmApplRejectReason>());
		appl.setSmCompanyBasic(null);
		appl.setSmCompanyExperiences(new ArrayList<SmCompanyExperience>());
		appl.setSmFacilities(new ArrayList<SmFacility>());
		appl.setSmApplInquiries(new ArrayList<SmApplInquiry>());
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSmAppl(appl);
		appl = SmServiceLookup.getMofRegistrationSO().removeSmApplChilds(reqDTO)
				.getSmAppl();
		return appl;
	}

	public static PmDocument getCertDocByCertBodyId(Long certBodyId) {
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		reqDTO.setCertBodyId(certBodyId);
		List<PmDocument> docList = SmServiceLookup.getDocumentSO()
				.retrieveGlpCertificatesByCertBodyId(reqDTO).getDocumentList();
		if (!EntityHelper.isCollectionEmpty(docList)) {
			return docList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Get current registration status description by smAppl
	 * 
	 * @param SmAppl
	 *            appl
	 * @return String
	 */
	public static String getCurrRegStatusDescBySmAppl(SmAppl appl) {
		String retVal = StringUtils.EMPTY;
		String language = new FacesScopes().getUserLocale().getLanguage();
		try {
			if (appl.getSmSupplier().getSmAppl().getPmRegStatus() != null
					&& appl.getSmSupplier().getSmAppl().getPmRegStatus()
							.getPmStatus() != null
					&& !EntityHelper.isCollectionEmpty(appl.getSmSupplier()
							.getSmAppl().getPmRegStatus().getPmStatus()
							.getPmStatusDescs())) {
				for (PmStatusDesc sd : appl.getSmSupplier().getSmAppl()
						.getPmRegStatus().getPmStatus().getPmStatusDescs()) {
					if (language.equals(sd.getLanguageCode())) {
						retVal = sd.getStatusName();
						break;
					}
				}
			}
		} catch (Exception e) {

		}
		return retVal;
	}

	/**
	 * Check if all supporting doc already verified
	 * 
	 * @param SmAppl
	 *            appl
	 * @return boolean
	 */
	public static boolean isAllSuppDocAlreadyVerified(
			List<SmApplSupportDoc> docs, String suppDocMode) {
		boolean retVal = true;
		for (SmApplSupportDoc doc : docs) {
			if (doc.getVerifiedFlg() == null) {
				if (suppDocMode != null
						&& suppDocMode
								.equals(SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
					if (!EntityHelper
							.isCollectionEmpty(doc.getSmApplDocFiles())
							&& !StringUtils.isBlank(doc.getSmApplDocFiles()
									.get(0).getFileName())) {
						retVal = false;
					}
				} else {
					if (doc.getPmDocument().isMandatory()) {
						retVal = false;
					}
				}

			}
		}
		return retVal;
	}

	/**
	 * Sort staff total
	 * 
	 * @param List
	 *            <SmStaffTotal> staffTotals
	 */
	public static void sortStaffInfo(List<SmStaffTotal> staffTotals) {
		if (!EntityHelper.isCollectionEmpty(staffTotals)) {
			for (SmStaffTotal tot : staffTotals) {
				if (!StringUtils.isBlank(tot.getStaffCategory())) {
					if (tot.getStaffCategory().equals(
							SupplierManagementConst.STAFF_TYPE_CONTRACTOR)) {
						tot.setSortOrder(1);
					} else if (tot.getStaffCategory().equals(
							SupplierManagementConst.STAFF_TYPE_PROFESSIONAL)) {
						tot.setSortOrder(2);
					} else if (tot.getStaffCategory().equals(
							SupplierManagementConst.STAFF_TYPE_SEMIPROF)) {
						tot.setSortOrder(3);
					} else if (tot.getStaffCategory().equals(
							SupplierManagementConst.STAFF_TYPE_CLERICAL)) {
						tot.setSortOrder(4);
					}
				}
			}
			Collections.sort(staffTotals, new Comparator<SmStaffTotal>() {
				@Override
				public int compare(final SmStaffTotal s1, final SmStaffTotal s2) {
					return s1.getSortOrder() - s2.getSortOrder();
				}
			});
		}
	}

	/**
	 * Construct staff info validation message
	 * 
	 * @param List
	 *            <SmStaffTotal> staffTotals
	 * @return String
	 */
	public static String getStaffInfoValidateMsg(List<SmStaffTotal> staffTotals) {
		StringBuilder sb = new StringBuilder();
		if (!EntityHelper.isCollectionEmpty(staffTotals)) {
			int count = 1;
			for (SmStaffTotal tot : staffTotals) {
				if ((tot.getTotalBumi() == null || tot.getTotalBumi()
						.compareTo(BigDecimal.ZERO) == 0)
						&& (tot.getTotalNonBumi() == null || tot
								.getTotalNonBumi().compareTo(BigDecimal.ZERO) == 0)
						&& (tot.getTotalForeigner() == null || tot
								.getTotalForeigner().compareTo(BigDecimal.ZERO) == 0)) {
					if (!StringUtils.isBlank(tot.getStaffCategory())) {
						if (tot.getStaffCategory()
								.equals(SupplierManagementConst.STAFF_TYPE_PROFESSIONAL)) {
							sb.append(SmCommonUtil.getMessage(
									"lbl.sm.staffinfo.prof",
									new String[] { count + ". " }));

						} else if (tot.getStaffCategory().equals(
								SupplierManagementConst.STAFF_TYPE_SEMIPROF)) {
							sb.append(SmCommonUtil.getMessage(
									"lbl.sm.staffinfo.semipro",
									new String[] { count + ". " }));
						} else {
							sb.append(SmCommonUtil.getMessage(
									"lbl.sm.staffinfo.clerk",
									new String[] { count + ". " }));
						}
						count++;
					}
				}
			}
		}
		return sb.length() > 0 ? sb.toString() : StringUtils.EMPTY;
	}

	public static CategoryCodeSearchBackingBean getCatCodeBbInstance() {
		if (categoryCodeBbInstance == null) {
			try {
				categoryCodeBbInstance = new CategoryCodeSearchBackingBean();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return categoryCodeBbInstance;
	}

}
